/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.adminbackupData;
import static org.seasar.extension.jdbc.operation.Operations.asc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.seasar.extension.jdbc.manager.JdbcManagerImpl;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.AdminbackupData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import net.sf.json.JSONObject;

@org.springframework.stereotype.Repository
public class AdminbackupDataService extends AbstractService<AdminbackupData> {

	//@Resource
	//S2Container container;

	@Resource
	protected HttpServletResponse response;

	@Resource
	protected ServletContext context;

	public AdminbackupData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * JSON形式の文字列をレスポンスへ出力する。
	 * 
	 * @param json
	 *            JSONオブジェクト
	 * @throws Exception
	 */
	public void responseJson(JSONObject json) throws Exception {
		response.setContentType("application/json");
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		pw.println(json.toString());
		pw.close();
	}

	/**
	 * TEXT形式の文字列をレスポンスへ出力する。
	 * 
	 * @param text
	 *            文字列
	 * @throws Exception
	 */
	public void responseText(String text) throws Exception {
		response.setContentType("text/plain");
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		if (text != null)
			pw.println(text);
		pw.close();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * 
	 * @param conditions
	 *            検索条件
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, ? extends Object> conditions) {
		return (int) select().where(conditions).getCount();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。
	 * 
	 * @param conditions
	 *            検索条件
	 * @return 全データ
	 */
	public List<AdminbackupData> listBackup(
			Map<String, ? extends Object> conditions) {
		String sortName = "id";

		return select().leftOuterJoin("localgovInfo").innerJoin("groupInfo")
				.where(conditions).orderBy(asc(sortName)).getResultList();
	}

	/**
	 * バックアップを実行する
	 * 
	 * @param
	 */
	public String backup(String name, LoginDataDto login) throws Exception {
		String error = null;
		StringBuffer stdoutSB = new StringBuffer();
		InputStream is = null;
		BufferedReader br = null;
		ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask",
				Locale.getDefault());

		try {
			// ダンプ情報を取得
			String command = context.getRealPath("/")
					+ bundle.getString("ADMINBACKUPDATA_BACKUP"); // ダンプコマンド
			String data_dir = bundle.getString("ADMINBACKUPDATA_DIR"); // バックアップファイルを保持するディレクトリぃ
			String dmp_cmd = bundle.getString("DMP_CMD"); // POSTGRESQL のダンプコマンド
			String webapps = bundle.getString("ADMINBACKUPDATA_WEBAPPS"); // WEBAPPS
																			// のディレクトリ
			String geoserver = bundle.getString("ADMINBACKUPDATA_GEOSERVER"); // GEOSERVER
																				// のディレクトリ

			// ログイン情報より値を取得
			AdminbackupData rec = new AdminbackupData();
			rec.id = null;
			rec.localgovinfoid = Long.valueOf(login.getLocalgovinfoid());
			rec.groupid = login.getGroupInfo().id;
			rec.name = name;
			rec.admin = false;
			// システム管理 の判断
			if (loginDataDto.getGroupid() == 0) {
				rec.admin = true;
			}
			rec.registtime = new Timestamp(System.currentTimeMillis());
			// バックアップしたファイルの保持場所とファイルプレフィックスを設定
			String pathAndPrefix = data_dir + rec.registtime.getTime();
			rec.path = pathAndPrefix;

			// DBのコンテナ情報を取得する
			/*
			 * SelectableDataSourceProxy proxy =
			 * (SelectableDataSourceProxy)container
			 * .getComponent(SelectableDataSourceProxy.class); DataSourceImpl ds
			 * = (DataSourceImpl) proxy.getDataSource(); ConnectionPoolImpl cp =
			 * (ConnectionPoolImpl) ds.getConnectionPool(); XADataSourceImpl
			 * xads = (XADataSourceImpl) cp.getXADataSource(); String url =
			 * xads.getURL(); String[] url_items = url.split("/"); String user =
			 * xads.getUser(); String password = xads.getPassword();
			 */
			DataSource ds = ((JdbcManagerImpl) jdbcManager).getDataSource();
			String url = ds.getConnection().getMetaData().getURL();
			String[] url_items = url.split("/");
			String user = ds.getConnection().getMetaData().getUserName();
			String password = "";

			// バックアップコマンド実行
			ProcessBuilder pb = new ProcessBuilder("/bin/sh", command, dmp_cmd,
					url_items[2], user, password, pathAndPrefix, url_items[3],
					webapps, geoserver);
			Process p = pb.start();
			br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			for (;;) {
				String line = br.readLine();
				if (line == null)
					break;
				stdoutSB.append(line);
			}
			int ret = p.waitFor();
			if (ret == 0) {
				// adminbackup_data にレコード追加
				insert(rec);
				error = null;
			} else {
				error = stdoutSB.toString();
			}
		} finally {
			if (br != null)
				br.close();
		}

		return error;
	}

	/**
	 * リストアを実行する
	 * 
	 * @param
	 */
	public String restore(Long id) throws Exception {
		String error = null;
		StringBuffer stdoutSB = new StringBuffer();
		InputStream is = null;
		BufferedReader br = null;
		ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask",
				Locale.getDefault());

		AdminbackupData ad = findById(id);

		try {
			// ダンプ情報を取得
			String command = context.getRealPath("/")
					+ bundle.getString("ADMINBACKUPDATA_RESTORE"); // リストアコマンド
			String data_dir = bundle.getString("ADMINBACKUPDATA_DIR"); // バックアップファイルを保持するディレクトリぃ
			String dmp_cmd = bundle.getString("RESTORE_CMD"); // POSTGRESQL
																// のリストアコマンド
			String webapps = bundle.getString("ADMINBACKUPDATA_WEBAPPS"); // WEBAPPS
																			// のディレクトリ
			String geoserver = bundle.getString("ADMINBACKUPDATA_GEOSERVER"); // GEOSERVER
																				// のディレクトリ
			// バックアップしたファイルの保持場所とファイルプレフィックスを設定
			String pathAndPrefix = ad.path;

			// DBのコンテナ情報を取得する
			/*
			 * SelectableDataSourceProxy proxy =
			 * (SelectableDataSourceProxy)container
			 * .getComponent(SelectableDataSourceProxy.class); DataSourceImpl ds
			 * = (DataSourceImpl) proxy.getDataSource(); ConnectionPoolImpl cp =
			 * (ConnectionPoolImpl) ds.getConnectionPool(); XADataSourceImpl
			 * xads = (XADataSourceImpl) cp.getXADataSource(); String url =
			 * xads.getURL(); String[] url_items = url.split("/"); String user =
			 * xads.getUser(); String password = xads.getPassword();
			 */

			DataSource ds = ((JdbcManagerImpl) jdbcManager).getDataSource();
			String url = ds.getConnection().getMetaData().getURL();
			String[] url_items = url.split("/");

			String user = ds.getConnection().getMetaData().getUserName();
			String password = "''";

			// リストアコマンド実行
			String execCommand = "/bin/sh" + " " + command + " " + dmp_cmd
					+ " " + url_items[2] + " " + user + " " + password + " "
					+ pathAndPrefix + " " + url_items[3] + " " + webapps + " "
					+ geoserver;
			ExecCommand ec = new ExecCommand(execCommand); // 実行
			Integer exitCode = ec.getExitCode(); // 終了コードを取得
			byte[] strout = ec.getStdout(); // 標準出力を取得
			byte[] errout = ec.getStderr(); // エラー出力を取得
			if (exitCode == 0) {
				error = null;
			} else {
				error = new String(strout);
				logger.error("localgovinfoid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
				logger.error(error);
			}

			/*******************************************************************************
			 * ProcessBuilder pb = new ProcessBuilder("/bin/sh", command,
			 * dmp_cmd, url_items[2], user, password, pathAndPrefix,
			 * url_items[3], webapps, geoserver); Process p = pb.start(); // is
			 * = p.getInputStream(); is = p.getErrorStream(); is.close(); br =
			 * new BufferedReader(new InputStreamReader(p.getInputStream())); //
			 * br = new BufferedReader(new //
			 * InputStreamReader(p.getErrorStream())); for (;;) { String line =
			 * br.readLine(); if (line == null) break;
			 * System.out.println("★stdoutSB＝" + line); stdoutSB.append(line); }
			 * int ret = p.waitFor(); if (ret == 0) { error = null; } else {
			 * error = stdoutSB.toString(); logger.error("localgovinfoid : " +
			 * loginDataDto.getLocalgovinfoid() + ", groupid : " +
			 * loginDataDto.getGroupid()); logger.error(error); }
			 *******************************************************************************/
		} finally {
			/*
			 * if (br != null) br.close();
			 */}

		return error;
	}

	/**
	 *
	 */
	class StreamGobbler extends Thread {
		InputStream is;
		OutputStream os;

		StreamGobbler(InputStream is, OutputStream redirect) {
			this.is = is;
			this.os = redirect;
		}

		public void run() {
			PrintWriter pw = new PrintWriter(os);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					pw.println(line);
				}
			} catch (IOException exception) {
				logger.error("localgovinfoid : " + loginDataDto.getLocalgovinfoid()
						+ ", groupid : " + loginDataDto.getGroupid());
				logger.error("", exception);
				throw new ServiceException(exception);
			}
			pw.flush();
		}
	}

	/**
	 * 外部コマンドの実行を行うクラス定義
	 */
	class ExecCommand {
		private byte[] stdout;
		private byte[] stderr;
		private Integer exitCode;

		/**
		 * @param command
		 * @throws IOException
		 */
		public ExecCommand(String command) throws IOException {
			ByteArrayOutputStream out = null;
			ByteArrayOutputStream err = null;

			try {
				StreamGobbler outGobbler;
				StreamGobbler errGobbler;
				// コマンド実行プロセス開始
				Process proc = Runtime.getRuntime().exec(command);
				// 標準出力Streamの読み込みスレッド開始
				out = new ByteArrayOutputStream();
				outGobbler = new StreamGobbler(proc.getInputStream(), out);
				outGobbler.start();
				// エラー出力Streamの読み込みスレッド開始
				err = new ByteArrayOutputStream();
				errGobbler = new StreamGobbler(proc.getErrorStream(), err);
				errGobbler.start();
				// すべての処理が終わるまで待機
				this.exitCode = proc.waitFor();
				outGobbler.join();
				errGobbler.join();
				this.stdout = out.toByteArray();
				this.stderr = err.toByteArray();
			} catch (Throwable t) {
				logger.error("localgovinfoid : " + loginDataDto.getLocalgovinfoid()
						+ ", groupid : " + loginDataDto.getGroupid());
				logger.error("", t);
				throw new ServiceException(t);
			} finally {
				if (out != null)
					out.close();
				if (err != null)
					err.close();
			}
		}

		public byte[] getStdout() {
			return stdout;
		}

		public byte[] getStderr() {
			return stderr;
		}

		public Integer getExitCode() {
			return exitCode;
		}
	}

	public List<AdminbackupData> check() {
		List<AdminbackupData> reslist = select().leftOuterJoin(adminbackupData().localgovInfo())
				.leftOuterJoin(adminbackupData().groupInfo()).getResultList();
		List<AdminbackupData> nolist = new ArrayList<AdminbackupData>();
		for (AdminbackupData data : reslist) {
			if (data.localgovInfo == null || data.groupInfo == null)
				nolist.add(data);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(AdminbackupData entity, DeleteCascadeResult result){

		// FIXME: ファイル削除
		if(StringUtil.isNotEmpty(entity.path)) {
			throw new ServiceException("Delete AdminbackupData File not support yet.");
		}

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
