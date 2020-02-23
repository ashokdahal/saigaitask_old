package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.entity.db.DatatransferInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.util.Config;

@org.springframework.stereotype.Service
public class DataTransferService {

	Logger logger = Logger.getLogger(DataTransferService.class);

	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	@Resource
	protected LocalgovInfoService localgovInfoService;

	public void transferData(DatatransferInfo transinfo, long trackdataid) {

		TablemasterInfo master = tablemasterInfoService.findById(transinfo.tablemasterinfoid);
		if (master == null) {System.out.println("master is null"); return;}
		TracktableInfo ttable = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(master.id, trackdataid);
		if (ttable == null) {System.out.println("tracktable is null"); return;};
		MapmasterInfo map = mapmasterInfoService.findById(master.mapmasterinfoid);
		String layerid = ttable.layerid;
		String ecommapURL = Config.getEcommapURL()+"map/export?FORMAT="+transinfo.format+"&layer="+layerid+"&cid="+map.communityid;

		//出力ファイル
		String tempDir = System.getProperty("java.io.tmpdir");
		File outfile = new File(tempDir + "/" + layerid +"."+transinfo.format);

		//送信ファイル
		LocalgovInfo localgov = localgovInfoService.findById(map.localgovinfoid);
		String govname = localgov.pref+(StringUtil.isNotEmpty(localgov.city)?localgov.city:"");
		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layer = mapDB.getLayerInfo(layerid);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fname = govname+"_"+layer.name+"_"+sdf.format(new Timestamp(System.currentTimeMillis()));

		try {
			URL url = new URL(ecommapURL);
			HttpURLConnection connection = null;
			FileOutputStream fos = null;
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					fos = new FileOutputStream(outfile);

					//ファイルを取得してテンポラリへ書き込み
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = connection.getInputStream().read(bytes)) != -1) {
						fos.write(bytes, 0, read);
					}
					/*try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
							BufferedReader reader = new BufferedReader(isr)) {
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
					}*/

					//ファイル送信
					if (transinfo.protocol.equalsIgnoreCase("ftps")) {
						sendFTPS(transinfo, outfile, fname);
					}
				}
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				if (fos != null)
					fos.close();
			}
		} catch (IOException e) {
			logger.error(e, e);
			e.printStackTrace();
		}
	}

	/**
	 * FTPSでファイルの送信
	 * @param transinfo
	 * @param outfile
	 */
	protected void sendFTPS(DatatransferInfo transinfo, File outfile, String fname) {
		String protocol = "SSL";
		//String protocol = "TLS";
		FTPSClient ftps = new FTPSClient(protocol);
		ftps.setDefaultPort(Integer.parseInt(transinfo.port));
		ftps.setDefaultTimeout(30000);
		ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		ftps.setControlEncoding("SJIS");

		//FTPサーバへの接続
		try {
			ftps.connect(transinfo.host);
			int reply = ftps.getReplyCode();
			 if (!FTPReply.isPositiveCompletion(reply))
			 {
				 ftps.disconnect();
				 System.err.println("FTP server refused connection.");
			 }
		}
		catch (IOException e)
		{
			if (ftps.isConnected()) {
				try {ftps.disconnect();}
				catch (IOException f){}
			}
			logger.error("Could not connect to server.", e);
			e.printStackTrace();
		}
		if (ftps.isConnected()) {
			try {
				ftps.setBufferSize(1000);

				//ログイン
				if (!ftps.login(transinfo.userid, transinfo.password)) {
					ftps.logout();
				}

				ftps.setFileType(FTP.BINARY_FILE_TYPE);

				//パッシブモード
				//ftps.pasv();
				ftps.execPBSZ(0);  // Set protection buffer size
				ftps.execPROT("P"); // Set data channel protection to private
				ftps.enterLocalPassiveMode();

				//FTPFile[] flist = ftps.listFiles();
				//for (FTPFile ffile : flist)
				//	System.out.println(ffile.getName());
				//flist = ftps.listDirectories();
				//for (FTPFile ffile : flist)
				//	System.out.println(ffile.getName());

				if (StringUtil.isNotEmpty(transinfo.directory)) {
					boolean ch = ftps.changeWorkingDirectory(transinfo.directory);
					if (!ch) throw new IOException("can't change directory : "+transinfo.directory);
				}

				//ファイル送信
				InputStream input = new FileInputStream(outfile);
				String ext = outfile.getName().substring(outfile.getName().indexOf('.'));
				boolean store = ftps.storeFile(fname+ext, input);
				if (!store)
					logger.error("can't store file : "+outfile.getName());
				input.close();
				ftps.logout();
			}
			catch (FTPConnectionClosedException e) {
				logger.error("Server closed connection.", e);
				e.printStackTrace();
			}
			catch (IOException e) {
				logger.error(e, e);
				e.printStackTrace();
			}
			finally {
				if (ftps.isConnected()) {
					try {
						ftps.disconnect();
					}
					catch (IOException f){}
				}
			}

		}
	}

}
