/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationVersion;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.manager.JdbcManagerImpl;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.admin.MapBackup;
import jp.ecom_plat.map.admin.MapRestore;
import jp.ecom_plat.map.db.EditLog;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.file.SLDFile;
import jp.ecom_plat.map.servlet.SLDServlet;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.RasterDownloadStatusDto;
import jp.ecom_plat.saigaitask.dto.admin.setupper.DeleteLocalgovInfoStatusDto;
import jp.ecom_plat.saigaitask.dto.admin.setupper.InitSetupStatusDto;
import jp.ecom_plat.saigaitask.service.db.AdminmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.JSTLConstantRegister;

/**
 * システムの初期化サービス
 * 設定ファイルの読み込みやDBの初期化を行う。
 */
@org.springframework.stereotype.Service
public class InitService extends BaseService {

	Logger logger = Logger.getLogger(InitService.class);

	// db
	@Resource protected JdbcManager jdbcManager;
	@Resource protected AdminmenuInfoService adminmenuInfoService;
	@Resource protected SummarylistcolumnInfoService summarylistcolumnInfoService;

	@Resource protected RasterDownloadStatusDto rasterDownloadStatusDto;
	@Resource protected DeleteLocalgovInfoStatusDto deleteLocalgovInfoStatusDto;
	@Resource protected InitSetupStatusDto initSetupStatusDto;

	/** eコミJarライブラリの対応DBバージョン */
	public static String ecommap_jar_db_version;

	/**
	 * 初期実行
	 * @param dbMigration DBマイグレーション実行フラグ
	 */
	public void init(boolean dbMigration) {
		String msg = /*lang._*/("An error occurred during the initialization process.");

		// logging system versions
		try{
			logger.info("startup");
			readPomProperties();
			readManifest();
		} catch(Exception e) {
			logger.error(msg, e);
		}

		// initialize/upgrade database
		try{
			initDatabase(dbMigration);
		} catch(Exception e) {
			logger.error(msg, e);
		}

		// initialize ecommap directory path, and check version
		try{
			initEcompath();
		} catch(Exception e) {
			logger.error(msg, e);
		}

		// initialize JSTL Constant
		try{
	    	JSTLConstantRegister register = new JSTLConstantRegister();
	    	register.registerAll();
		} catch(Exception e) {
			logger.error(msg, e);
		}

		// logging java system property
		try{
			logSystemProperty();
		} catch(Exception e) {
			logger.error(msg, e);
		}
	}

	/**
	 * システムバージョン情報等を取得してログ出力する.
	 */
	public void readPomProperties() {
		String version = Config.getVersion();
		if(version==null) {
			logger.debug(/*lang._*/("Failed to get version."));
		}
		else {
			logger.info("Version: "+version);
		}
		//logger.info("Major Version: "+Arrays.toString(Config.getMajorVersion()));
	}

	/**
	 * マニフェストファイルからシステムのバージョン情報などを取得する.
	 * マニフェストファイルは、Maven でパッケージを作成しないと生成されない.
	 */
	public void readManifest() {
		// マニフェストファイルをログ出力
		Manifest manifest = Config.getManifest();
		if(manifest!=null) {
			Attributes attributes = manifest.getMainAttributes();
			for(String key : new String[]{"Built-Time", "SVN-Revision", "SVN-Status", "SVN-Path"}) {
				logger.info(key+": "+attributes.getValue(key));
			}
		}
	}

	/**
	 * データベースの初期化
	 * WEB-INF/classes/data/sql にあるマイグレーションSQLファイルを使ってDBの更新を行う。
	 * マイグレーション実行前のSQLファイル検証チェックは行わない。(validateOnMigrate=false)
	 * もし、あとから追加したSQLファイルがある場合は実行する。(outOfOrder=true)
	 * @param dbMigration DBマイグレーション実行フラグ
	 */
	public void initDatabase(boolean dbMigration) {
		logger.info(/*lang._*/("Initialize database"));
		try {
			// DB の初期化
			if(dbMigration) {
				Flyway flyway = getFlyway();

				// configure version
				MigrationVersion beforeMigrateVersion = null;
				if(flyway.info().current()!=null) {
					beforeMigrateVersion = flyway.info().current().getVersion();
				}
				else {
					// set baseline version
					String baselineVersion = assumeBaselineVersion();
					logger.info(/*lang._*/("DB version base line:")+baselineVersion);
					flyway.setBaselineOnMigrate(true);
					flyway.setBaselineVersionAsString(baselineVersion);
					beforeMigrateVersion = flyway.getBaselineVersion();
				}

				try {
					// execute migration
					int migrationSuccessCount = flyway.migrate();

					// check if db updated
					MigrationVersion afterMigrateVersion = flyway.info().current().getVersion();
					logger.info(/*lang._*/("DB version:")+beforeMigrateVersion+(0<migrationSuccessCount?"->"+afterMigrateVersion:""));
				} catch(FlywayException e)  {
					throw new ServiceException(/*lang._*/("Unable to update DB."), e);
				}
			}

			// ビューの再作成
			{
				// 外部参照ビューの追加
				StringBuffer sql = new StringBuffer();
				sql.append("CREATE OR REPLACE VIEW reference_view AS (");
				sql.append("  SELECT tc_from.table_name AS reference_from_table_name, tc_to.table_name AS reference_to_table_name");
				sql.append("  FROM information_schema.table_constraints tc_from");
				sql.append("  LEFT JOIN information_schema.referential_constraints rc ON rc.constraint_name=tc_from.constraint_name");
				sql.append("  LEFT JOIN information_schema.table_constraints tc_to ON tc_to.constraint_name=rc.unique_constraint_name");
				sql.append("  WHERE tc_from.table_schema='public' AND tc_from.constraint_type='FOREIGN KEY'");
				sql.append(");");
				sql.append(/*lang._*/("COMMENT ON VIEW reference_view IS 'external reference view';"));
				jdbcManager.updateBySql(sql.toString()).execute();
				logger.debug("reference_view created: "+sql.toString());
			}

			// マスタデータのリストア
			{
				// 管理画面メニューの初期化
				adminmenuInfoService.restoreAdminmenuInfo();
			}

			// 集計リスト項目を V1.4 から V2.0 に更新
			{
				summarylistcolumnInfoService.upgradeToVer2();
			}
		} catch(Exception e) {
			throw new ServiceException(/*lang._*/("Failed to initialize DB."), e);
		}
	}

	/**
	 * @return Flyway(DB migration tool)
	 */
	public Flyway getFlyway() {
		try {
			// DBの接続チェック
			DataSource dataSource = ((JdbcManagerImpl) jdbcManager).getDataSource();
			logger.debug("DataSource.LoginTimeout: "+dataSource.getLoginTimeout());

			// configure migration
			Flyway flyway = new Flyway();
			flyway.setDataSource(dataSource);
			flyway.setLocations("data/sql");
			// マイグレーション実行前の検証チェックを行わない
			flyway.setValidateOnMigrate(false);
			// あとから追加された(Out of Order)マイグレーションファイルを実行する
			flyway.setOutOfOrder(true);
			return flyway;
		} catch(SQLException e) {
			throw new ServiceException(/*lang._*/("Initialization failure of DB migration tool(Flyway)"), e);
		}
	}

	/**
	 * 現在のDBのバージョンを推測する。
	 * @return 推測した現在のDBのバージョン
	 */
	public String assumeBaselineVersion() {
		// 開発者の導入向け(本来は不要)
		if(isExists("publiccommons_report_data", "contentdescription")) return "1.1.12";
		if(isExists("publiccommons_send_to_info", "endpoint_url_backup")) return "1.1.11";
		if(isExists("menu_info", "deleted")) return "1.1.10";
		//if(isExists("")) return "1.1.9";
		if(isExists("trackgroup_data")) return "1.1.8";
		if(isExists("publiccommons_report_data_last_general")) return "1.1.7";
		if(isExists("reportcontent2_data", "houseall1")) return "1.1.6";
		if(isExists("externallistsummarycolumn_info")) return "1.1.5";
		if(isExists("observlist_info", "observid")) return "1.1.4";
		if(isExists("ecomgwpost_info")) return "1.1.3";
		if(isExists("publiccommons_report_data_last_refuge")) return "1.1.2";
		if(isExists("disastersummaryhistory_data")) return "1.1.1";
		// check if v1.0
		// ※バージョン1.0はDB管理がないままリリースしたため判定は必須
		// track_data テーブルがあれば 1.0 と判定する
		if(isExists("track_data")) return "1.0";

		// 初期インストールの場合はバージョン 0.0
		return "0.0";
	}
	/**
	 * テーブルの存在チェック
	 * @param tableNamePattern
	 * @return 存在フラグ
	 */
	public boolean isExists(String tableNamePattern) {
		return isExists(tableNamePattern, null);
	}
	/**
	 * カラムの存在チェック
	 * @param tableNamePattern
	 * @param columnNamePattern
	 * @return 存在フラグ
	 */
	public boolean isExists(String tableNamePattern, String columnNamePattern) {
		DataSource dataSource = ((JdbcManagerImpl) jdbcManager).getDataSource();

		Connection con = null;
		ResultSet result = null;
		try {
			con = dataSource.getConnection();
			DatabaseMetaData meta = con.getMetaData();
			//String catalog = null;
			String schemaPattern = "public";

			if(StringUtil.isEmpty(columnNamePattern)) {
				result = meta.getTables( /*catalog*/ null, schemaPattern, tableNamePattern, new String[]{"TABLE"});
			}
			else {
				result = meta.getColumns(/*catalog*/ null, schemaPattern, tableNamePattern, /*columnNamePattern*/ null);
			}
			return result.next();
		} catch(SQLException e) {
			//throw new ServiceException(/*lang._*/("An error occurred during table ({0}) existing check process.", tableNamePattern+(StringUtil.isNotEmpty(columnNamePattern)?"."+columnNamePattern:"")), e);
			String arg0 = tableNamePattern+(StringUtil.isNotEmpty(columnNamePattern)?"."+columnNamePattern:"");
			throw new ServiceException("An error occurred during table ("+arg0+") existing check process.", e);
		} finally {
			if(result!=null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}


	/**
	 * eコミマップのパスを初期化
	 * eコミJavaライブラリで、ファイルの取得できるように調整
	 */
	public void initEcompath() {
		// eコミのJARファイルの情報を取得
		try {
			String ecomJarFilePath = MapDB.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			// Linux の場合: /usr/local/apache-tomcat-8.0.43/webapps/SaigaiTask/WEB-INF/lib/ecommap-2.4.x-SNAPSHOT.jar
			// Windows だと file:/C:/Users/oku/application/pleiades-4.4/workspace/SaigaiTask2-2.0/src/main/webapp/WEB-INF/lib/ecommap-2.4.x-2.2.0-SNAPSHOT.jar!/jp/ecom_plat/map/db/MapDB.class
			// となるため、JARのパスを抽出する
			if(ecomJarFilePath.contains("!")) {
				int beginIdx = 0;
				if(ecomJarFilePath.startsWith("file:/C:")) beginIdx = "file:/C:".length();
				ecomJarFilePath = ecomJarFilePath.substring(beginIdx, ecomJarFilePath.indexOf('!'));
			}
			logger.debug("ecommap.jar path: "+ecomJarFilePath);
			File ecomJarFile = new File(ecomJarFilePath);
			if(!ecomJarFile.exists()) {
				logger.warn(/*lang._*/("Unable to get e-Com JAR library."));
			}
			else {
				// マニフェストファイルの読み込み
				JarFile jarFile = null;
				try {
					jarFile = new JarFile(ecomJarFile);
					Manifest manifest = jarFile.getManifest();
					ecommap_jar_db_version = manifest.getMainAttributes().getValue("db_version");
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				} finally {
					if(jarFile!=null) {
						try {
							jarFile.close();
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		} catch(Exception e) {
			logger.warn(lang.__("Unable to get e-Com JAR library."), e);
		}
		logger.info(/*lang._*/("e-Com Jar library DB version:")+ecommap_jar_db_version);

		// eコミDBバージョンを取得
		MapDB mapDB = MapDB.getMapDB();
		String ecommapDBVersion = null;
		try {
			ecommapDBVersion = mapDB.getVersion();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info(/*lang._*/("e-Com DB version")+ecommapDBVersion);

		// DBバージョンチェック
		if(StringUtil.isNotEmpty(ecommap_jar_db_version)&&StringUtil.isNotEmpty(ecommapDBVersion)) {
			try {
				// 互換性バージョンのチェック
				boolean compatible = false;
				Map<String, String> compatibles = new HashMap<String, String>();
				// key: ecommap_jar_db_version, value: ecommapDBVersion
				compatibles.put("2.1.6", "2.1.5");
				// GeoServer2.11 は eコミマップDBVer2.2.1でないと動作しないが、
				// eコミマップのJARはDBVer2.2.0でも問題なし
				compatibles.put("2.2.0", "2.2.1");
				for(Map.Entry<String, String> entry : compatibles.entrySet()) {
					if(entry.getKey().equals(ecommap_jar_db_version)&&entry.getValue().equals(ecommapDBVersion)) {
						compatible = true;
						break;
					}
				}
				// 互換性有りの場合
				if(compatible) {
					logger.info(/*lang._*/("It is compatible with e-com map."));
				}
				else {
					// DBバージョンの比較
					// return 0: 同一バージョン、 positive num: v1の方が新しい、 negative num: v1 の方が古い
					int result = 0;
					String[] versions1 = ecommap_jar_db_version.split("\\.");
					String[] versions2 = ecommapDBVersion.split("\\.");
					int maxLength = Math.max(versions1.length, versions2.length);
					for(int index=0; index<maxLength; index++) {
						int v1 = versions1.length<=index ? 0 : Integer.parseInt(versions1[index]);
						int v2 =  versions2.length<=index ? 0 : Integer.parseInt( versions2[index]);
						result = v1 - v2;
						if(result!=0) break;
					}

					String arg0 = ecommap_jar_db_version;
					String arg1 = ecommapDBVersion;
					if(result==0) logger.info(/*lang._*/("Corresponding e-Com map "));
					else if(result<0) logger.warn(/*lang._*/("DB version of corresponding e-Com map is "+arg0+" ,but using DB version is "+arg1+",so some features might not work."));
					else logger.fatal(/*lang._*/("For DB version of e-Com map is old, update DB version "+arg0+"."));

					// JAR が 2.1.8 の場合は、それ以前のバージョンでは不具合がおきる
					if(ecommap_jar_db_version.equals("2.1.8") && 0<result) {
						logger.fatal(/*lang._*/("Can't work correctly due to old DB version. Update DB to "+arg0+"."));
					}
				}
			} catch(Exception e) {
				logger.warn(/*lang._*/("Unable to compare e-Com map DB version."));
			}
		}
		else logger.warn(/*lang._*/("Unable to get e-Com DB version of JAR or DB."));

		// eコミディレクトリを設定
		SLDFile.setMapDir(Config.getMapDir().getPath());
		MapBackup.setMapDir(Config.getMapDir().getPath());
		MapRestore.setMapDir(Config.getMapDir().getPath());
		logger.info(/*lang._*/("e-Com directory")+SLDFile.getMapDir());
		String logPath = Config.getPathInfoProperty("LOG_PATH");
		if(StringUtil.isEmpty(logPath)) logPath = "/logs";
		// MAPDIR=/home/map/webapps/map
		// LOGPATH=/home/map/webapps/map/../../logs
		//      -> /home/map/logs
		EditLog.setLogPath(Config.getMapDir().getPath()+"/../.."+logPath);
		logger.info(/*lang._*/("e-Com directory")+SLDFile.getMapDir());

		// NAMESPACE を設定
		try {
			// privateなstatic変数を読み取り
			Field NAME_SPACE = SLDServlet.class.getDeclaredField("NAME_SPACE");
			NAME_SPACE.setAccessible(true);
			NAME_SPACE.set(null, Config.getNameSpace());
			logger.info("Config SLDServlet.NAME_SPACE="+Config.getNameSpace());
		} catch(Exception e) {
			logger.error("failed to config SLDServlet.NAME_SPACE", e);
		}
	}

	protected void logSystemProperty() {
		Properties props = System.getProperties();
		@SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>) props.propertyNames();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			logger.debug(" *** System Properties *** "+key + " -- " + props.getProperty(key));
		}
	}

	/**
	 * 終了処理
	 */
	public void destroy() {
		try {
			rasterDownloadStatusDto.destroy();
			deleteLocalgovInfoStatusDto.destroy();
			initSetupStatusDto.destroy();
		} finally {
			logger.info("shutdown");
		}
	}
}
