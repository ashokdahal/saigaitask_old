/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.ServletException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.dto.AlertrequestInfoDto;
import jp.ecom_plat.saigaitask.dto.AlerttriggerInfoDto;
import jp.ecom_plat.saigaitask.entity.db.JalertreceivefileData;
import jp.ecom_plat.saigaitask.entity.db.JalertrequestInfo;
import jp.ecom_plat.saigaitask.entity.db.JalertserverInfo;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoxsltInfo;
import jp.ecom_plat.saigaitask.service.db.JalertreceivefileDataService;
import jp.ecom_plat.saigaitask.service.db.JalertrequestInfoService;
import jp.ecom_plat.saigaitask.service.db.JalertserverInfoService;
import jp.ecom_plat.saigaitask.service.db.JalerttriggerInfoService;
import jp.ecom_plat.saigaitask.util.BasicAuthenticator;
import jp.ecom_plat.saigaitask.util.Constants;


@org.springframework.stereotype.Service
public class JAlertAlarmService extends AbstractAlarmService {

	
	@Resource
	protected JalertserverInfoService jalertserverInfoService;
	@Resource
	protected JalertrequestInfoService jalertrequestInfoService;
	@Resource
	protected JalertreceivefileDataService jalertreceivefileDataService;
	@Resource
	protected JalerttriggerInfoService jalerttriggerInfoService;

	private String indexcsv = "index.csv";
	private String areaxsltfile = "type_date_area.xsl";
	
	/**
	 * 初期実行
	 * @param start スレッド実行フラグ
	 */
	public void init(boolean start)
	{
		super.init(start);
	}	

	/**
	 * 終了処理を行う。
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * アラームを行うスレッドを開始する
	 *
	 * @throws ServletException
	 */
	/*protected void startThread() {
		super.startThread();
	}*/


	/**
	 * アラームチェック実行
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void checkAlarm() {
		long starttime = System.currentTimeMillis();
		
		List<JalertserverInfo> serverList = jalertserverInfoService.findAll();
		
		File xslt = new File(fileService.getXmlRoot() + mxsltPath + areaxsltfile);

		try {
			for (JalertserverInfo server : serverList) {
				if (StringUtil.isNotEmpty(server.userid)) {
					BasicAuthenticator http_authenticator = new BasicAuthenticator(server.userid, server.password);
					Authenticator.setDefault(http_authenticator);
				}
				else
					Authenticator.setDefault(null);
				
				/** 取得XML文字列をファイル化して格納 */
				//自治体IDごとにディレクトリを作成
				String meteoXmlPath =  fileService.getXmlRoot() + mxmlPath+server.localgovinfoid;
				//ディレクトリが無ければ作成する
				File dateDir = new File(meteoXmlPath);
				if(!dateDir.exists()) {
					boolean created = dateDir.mkdirs();
					if(!created) {
						logger.error("failed mkdirs "+dateDir.getAbsolutePath());
					}
				}
				
				HttpURLConnection urlconn = (HttpURLConnection)new URL(server.serverurl+indexcsv).openConnection();
				urlconn.setReadTimeout(30000);
				urlconn.setRequestMethod("GET");
				urlconn.setInstanceFollowRedirects(false);
				urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
				
				InputStream is = urlconn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "MS932");
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				for (int i = 0; br.ready(); i++) {
					line = br.readLine();
					String data[] = line.split(",");
					
					String textfname = data[2];
					JalertreceivefileData rfiledata = jalertreceivefileDataService.findByLocalgovInfoIdAndOrgtextfilename(server.localgovinfoid, textfname);
					//すでに取得済み
					if (rfiledata != null) continue;
					
					Date time = sdf.parse(data[0]);
					int type = getType(data[1]);
					String datafname = data[3];
					if (datafname.indexOf("xml.gz") > 0) {
						datafname = datafname.substring(0, datafname.lastIndexOf('.'));
					}
					
					//ファイル出力
					File textfile = new File(meteoXmlPath+"/"+textfname);
					writeTextfile(server.serverurl+textfname, textfile);
					
					File jmaxml = new File(meteoXmlPath+"/"+datafname);
					if (data[3].indexOf("xml.gz") > 0) {
						writeGzipfile(server.serverurl+data[3], jmaxml);
					}
					
					JalertreceivefileData receivefileData = new JalertreceivefileData();
					receivefileData.jalerttypeid = type;
					receivefileData.localgovinfoid = server.localgovinfoid;
					receivefileData.receivetime = new Timestamp(time.getTime());
					receivefileData.orgtextfilename = data[2];
					receivefileData.orgdatafilename = data[3];
					receivefileData.textfilepath = textfile.getParentFile().getPath();
					receivefileData.filepath = jmaxml.getParentFile().getPath();
					jalertreceivefileDataService.insert(receivefileData);
					
					//そもそもこのエリアの情報？
					List<String> areas = new ArrayList<String>();
					if (type != Constants.JALERT_KINKYURENRAKU && type != Constants.JALERT_KOKUMINHOGOJYOUHOU) {
						StringWriter swriter = new StringWriter();
						parseXML(xslt, jmaxml, swriter);
						areas = getAreaData(swriter);
					}
					
					List<JalertrequestInfo> requestList = jalertrequestInfoService.findByLocalgovinfoIdAndJalerttypeId(server.localgovinfoid, type);
					for (JalertrequestInfo req : requestList) {
						if (type == req.jalerttypeid) {
							boolean ok = false;
							if (StringUtil.isEmpty(req.meteoareaid) && StringUtil.isEmpty(req.meteoareaid2))
								ok = true;
							if (StringUtil.isNotEmpty(req.meteoareaid) && areas.contains(req.meteoareaid))
								ok = true;
							if (StringUtil.isNotEmpty(req.meteoareaid2) && areas.contains(req.meteoareaid2))
								ok = true;
							
							//取得したXMLファイルをXSLTでparse
							if (ok) {
								receivefileData.jalertrequestinfoid = req.id;
								jalertreceivefileDataService.update(receivefileData);
								parseXML(req, jmaxml, receivefileData, 0l);
								break;
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("",e);
			e.printStackTrace();
		}
	}

	/**
	 * 取得したXMLファイルをXSLTでparse
	 * @param xml
	 */
	protected void parseXML(JalertrequestInfo req, File jmaxml, JalertreceivefileData receivefileData, long demoid) throws Exception {
		MeteoxsltInfo xsltInfo = meteoxsltInfoService.findByLocalgovinfoidOrZeroAndMeteotypeid(req.localgovinfoid, req.jalerttypeid);
		Timestamp reportdatetime = null;
		String[] datas = null;
		if (xsltInfo != null) {
			File xsltFile = new File(fileService.getXmlRoot() + mxsltPath + xsltInfo.filepath);
			
			//parse準備
			TransformerFactory trfactory = TransformerFactory.newInstance();
			StringWriter swriter = new StringWriter();
			Transformer t = trfactory.newTransformer(new StreamSource(xsltFile));
			//parse
			t.setParameter("code", req.meteoareaid);
			t.transform(new StreamSource(jmaxml), new StreamResult(swriter));
			//結果をStringに出力
			String parseStr = swriter.toString();
			System.out.println(parseStr);
			datas = parseStr.split("\\\\n");
			
			//発表日時は全種類共通なのでここでセット
			//xsltの解析で、機械的に時刻を+9しているだけなので、24時間を超える時間になっている場合があるので、ここでTimestampに直して発表時刻を修正
			reportdatetime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(datas[1]).getTime());
		}
		else {//国民保護情報など
			reportdatetime = receivefileData.receivetime;
			FileReader fr = new FileReader(receivefileData.textfilepath+"\\"+receivefileData.orgtextfilename);
			BufferedReader br = new BufferedReader(fr);
			StringBuffer buff = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buff.append(line);
				buff.append("\\n");
			}
			br.close();
			datas = buff.toString().split("\\\\n");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String reportdatetimeStr = sdf.format(reportdatetime);
		
		//メッセージ用StringBuffer定義
		StringBuffer msg = new StringBuffer();
		msg.append(lang.__("[{0} announcement]", reportdatetimeStr));
		String title = lang.__("[{0} announcement]", reportdatetimeStr);

		if (demoid > 0 || reportdatetime.getTime() > System.currentTimeMillis()-expire) {//2日前までしか取得しない
			AlertrequestInfoDto requestInfo = Beans.createAndCopy(AlertrequestInfoDto.class, req).execute();
			requestInfo.meteotypeid = req.jalerttypeid;
			JalerttriggerInfo trigger = jalerttriggerInfoService.findByJalertrequestInfoId(req.id);
			AlerttriggerInfoDto triggerInfo = null;
			if (trigger != null) {
				triggerInfo = Beans.createAndCopy(AlerttriggerInfoDto.class, trigger).execute();
				triggerInfo.meteorequestinfoid = trigger.jalertrequestinfoid;
			}
			
			execAlarmData(requestInfo, triggerInfo, jmaxml, datas, title, msg, receivefileData.id, demoid);
		}
	}
	
	@Override
	protected void execAlarmData(AlertrequestInfoDto req, AlerttriggerInfoDto trigger, File jmaxml, String[] datas, String title, StringBuffer msg, long meteodataid, long demoid) throws Exception
	{
		super.execAlarmData(req, trigger, jmaxml, datas, title, msg, meteodataid, demoid);
		if (req.meteotypeid.equals(Constants.JALERT_KOKUMINHOGOJYOUHOU) || req.meteotypeid.equals(Constants.JALERT_KINKYURENRAKU)) {
			for(int j=0; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				String atype = "error";
				if (req.meteotypeid.equals(Constants.JALERT_KINKYURENRAKU))
						atype = "information";
				addAlarm(req.localgovinfoid, msg.toString(), atype, demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), req.meteotypeid, demoid);
			}
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		
	}
	
	public static void parseXML(File xslt, File xml, StringWriter swriter){
		try {
			TransformerFactory trfactory = TransformerFactory.newInstance();
			Transformer t = trfactory.newTransformer(new StreamSource(xslt));
			t.transform(new StreamSource(xml), new StreamResult(swriter));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getAreaData(StringWriter swriter){

		List<String> areas = new ArrayList<String>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try{

			//登録データ用変数定義
			//String info = null;
			//Timestamp reportdatetime = null;
			//String[] cols = new String[] {null, null, null};

			//データ読み込み
			String buff = swriter.toString();
			StringReader sr = new StringReader(buff);
			//fis = new InputStream(swriter);
			//ファイルはUTF8で書かれていることを想定
			//isr = new InputStreamReader(fis, "utf-8");
			br = new BufferedReader(sr);
			String line;
			int cnt = 0;
			while ( ( line = br.readLine()) != null ) {
				line = new String(line.getBytes("UTF8"),"UTF8");

				//1行目：種類
				if(cnt==0){
					//info=line;
				//2行目：発表日時
				}else if(cnt==1){
					//try{
					//	reportdatetime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(line).getTime());
					//} catch (ParseException e) {
					//	e.printStackTrace();
					//}
				//3行目以降：地域名、複数あるのである分だけ処理
				}else{
					String[] area = line.split(":");
					if(area.length ==3)
						areas.add(area[1]);
					//System.out.println("csv file : "+Arrays.toString(area));
				}
				cnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (fis != null) fis.close();
			if (isr != null) isr.close();
			if (br != null) br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return areas;
	}
	
	protected void writeTextfile(String fileurl, File file) throws IOException {
		URL url = new URL(fileurl);
		URLConnection urlcon = url.openConnection();
		urlcon.setReadTimeout(30000);
		InputStream in = urlcon.getInputStream();
		
		FileOutputStream fos = new FileOutputStream(file);
		
		//ファイルの読み込み
		byte[] buff = new byte[256];
		int n = 0;
		while((n = in.read(buff)) != -1) {
			fos.write(buff, 0, n);
		}
		in.close();
		fos.close();
	}

	protected void writeGzipfile(String fileurl, File file) throws IOException {
		URL url = new URL(fileurl);
		URLConnection urlcon = url.openConnection();
		urlcon.setReadTimeout(30000);
		InputStream in = urlcon.getInputStream();
		GZIPInputStream gin = new GZIPInputStream(in);
		
		FileOutputStream fos = new FileOutputStream(file);
		
		//ファイルの読み込み
		byte[] buff = new byte[256];
		int n = 0;
		while((n = gin.read(buff)) != -1) {
			fos.write(buff, 0, n);
		}
		gin.close();
		fos.close();
	}
	
	private int getType(String stype) {
		if (stype.equals("EPRQ"))
			return Constants.METEO_KINKYUJISHINSOKUHOU;
		else if (stype.equals("ISSW"))
			return Constants.METEO_TSUNAMIKEIHOUCHUIHOU;
		else if (stype.equals("VOLC"))
			return Constants.METEO_FUNKAKEIHOUYOHOU;
		else if (stype.equals("IOEQ"))
			return Constants.METEO_SHINDOSOKUHOU;
		else if (stype.equals("WRMA"))
			return Constants.METEO_KISHOUKEIHOUCHUIHOU;
		else if (stype.equals("JALT"))
			return Constants.JALERT_KOKUMINHOGOJYOUHOU;
		else if (stype.equals("IFDA"))
			return Constants.JALERT_KINKYURENRAKU;
		return 0;
	}
}
