/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.ResourceUtil;
import org.springframework.core.io.Resource;

import jp.ecom_plat.saigaitask.service.InitService;

/**
 * 設定ファイルを読み込むクラス
 */
public class Config {

	static Logger logger = Logger.getLogger(Config.class);

	private static Properties properties = ResourceUtil.getProperties("SaigaiTask.properties");
	private static Properties resourceInfoProperties = ResourceUtil.getProperties("ResourceInfo.properties");
	private static Properties pathInfoProperties = ResourceUtil.getProperties("PathInfo.properties");
	private static Properties pomProperties;
	private static Manifest manifest;
	
	public static Properties getPomProperties() {
		if(pomProperties==null) {
			ServletContext application = SpringContext.getApplicationContext().getBean(ServletContext.class);
			// try to load from maven properties first
			InputStream is = null;
			try {
				is = application.getResourceAsStream("/META-INF/maven/jp.ecom_plat.saigaitask/SaigaiTask2/pom.properties");
				if(is!=null) {
					pomProperties = new Properties();
					pomProperties.load(is);
				}
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				try { is.close(); } catch(Exception e) { logger.error(e.getMessage(), e);}
			}
		}
		return pomProperties;
	}

	/**
	 * @return Manifest
	 */
	public static Manifest getManifest() {
		if(manifest==null) {
			ServletContext application = SpringContext.getApplicationContext().getBean(ServletContext.class);
			InputStream is = null;
			try {
				is = application.getResourceAsStream("/META-INF/MANIFEST.MF");
				if(is!=null) {
					manifest = new Manifest(is);
				}
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				if(is!=null) try { is.close(); } catch(Exception e) { logger.error(e.getMessage(), e);}
			}
		}
		return manifest;
	}

	/**
	 * SaigaiTask.properyからプロパティを取得します.
	 * @param key キー
	 * @return 設定値
	 */
	public static String getString(String key) {
		return properties.getProperty(key);
	}

	/**
	 * SaigaiTask.properyからプロパティを取得します.
	 * @param key キー
	 * @param defaultValue デフォルト値
	 * @return 設定値
	 */
	public static String getString(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * カンマ区切り文字列を配列で取得
	 * @param key キー
	 * @return 設定値がなければ空の文字列配列
	 */
	public static String[] getStrings(String key) {
		String value= Config.getString(key);
		if(value!=null) {
			return value.split(",");
		}
		return new String[0];
	}

	/**
	 * eコミマップのホームディレクトリをFileで取得
	 * デフォルトは "/home/map/webapps/map"
	 * @return File
	 */
	public static File getMapDir() {
		final String key = "MAPDIR";
		File mapDir = new File(getString(key, "/home/map/webapps/map"));
		if(!mapDir.exists()) {
			logger.fatal(key+" not exists: "+mapDir.getPath());
		}
		return mapDir;
	}

	public static String getRequestURL(HttpServletRequest request) {
		if(request!=null) {
			String port = ":"+request.getServerPort();
			if("http".equalsIgnoreCase(request.getScheme()) && request.getServerPort()==80) port = "";
			if("https".equalsIgnoreCase(request.getScheme()) && request.getServerPort()==443) port = "";
			return request.getScheme()+"://"+request.getServerName()+port+"/";
		}
		return null;
	}
	
	public static String getRequestContextURL(HttpServletRequest request) {
		if(request!=null) {
			String requestURL = Config.getRequestURL(request);
			if(requestURL!=null) {
				return requestURL + request.getContextPath().substring(1);
			}			
		}
		return null;
	}
	
	/**
	 * @return eコミマップURL
	 */
	public static String getEcommapURL() {
		HttpServletRequest request = SpringContext.getRequest();
		return getEcommapURL(request);
	}

	/**
	 * @param request Request object for acquiring domain, protocol, etc. used in URL
	 * @return eコミマップURL http://example.com/
	 */
	public static String getEcommapURL(HttpServletRequest request) {
		String defaultURL = null;
		if(request!=null) {
			getRequestURL(request);
		}
		return getString("ECOMIMAPURL", defaultURL);
	}

	/**
	 * @return eコミマップのローカルURL http://localhost:18080
	 */
	public static String getLocalRootURL() {
		return pathInfoProperties.getProperty("LOCAL_ROOT_URL");
	}

	/**
	 * @return GeoServer WMS のローカルURL http://localhost:18080/geoserver/wms?
	 */
	public static String getLocalGeoserverWMS() {
		String localGeoserverWMS = pathInfoProperties.getProperty("GEOSERVER_WMS");
		if(!localGeoserverWMS.startsWith("http")) {
			localGeoserverWMS = Config.getLocalRootURL() + localGeoserverWMS;
			//localGeoserverWMS = BaseService.getSaigaiTaskProperty("ECOMIMAPURL").replaceAll("/$", "")+localGeoserverWMS;
		}
		//<div lang="ja">パラメータは不要なのでついていたら除外</div>
		//<div lang="en">Because parameter is not required, if parameters are attached, then remove them</div>
		localGeoserverWMS = localGeoserverWMS.substring(0,localGeoserverWMS.indexOf('?')+1);
		return localGeoserverWMS;
	}

	public static String getEarthquakeGeoserverWMS() {
		String earthquakeGeoserverWMS = pathInfoProperties.getProperty("EARTHQUAKE_GEOSERVER_WMS");
		return earthquakeGeoserverWMS;
	}
	public static String getEarthquakeGeoserverWorkspace() {
		String earthquakeGeoserverWMS = pathInfoProperties.getProperty("EARTHQUAKE_GEOSERVER_WORKSPACE");
		return earthquakeGeoserverWMS;
	}
	/**
	 * @return eコミマップの ローカルLegendServletURL http://localhost:18080/map/legend?
	 */
	public static String getEcommapLegendServletURL() {
		String legendServletURL = pathInfoProperties.getProperty("LEGEND_URL");
		if(!legendServletURL.startsWith("http")) {
			legendServletURL = Config.getLocalRootURL() + legendServletURL;
			//localGeoserverWMS = BaseService.getSaigaiTaskProperty("ECOMIMAPURL").replaceAll("/$", "")+localGeoserverWMS;
		}
		return legendServletURL;
	}

	/**
	 * @return GeoServerに登録するeコミマップの名前空間
	 */
	public static String getNameSpace() {
		String namespace = resourceInfoProperties.getProperty("NAMESPACE");
		return namespace;

	}

	/**
	 * フォントファイルのリアルパスを取得
	 * ResourceInfo.properties:FONT
	 * @return String
	 */
	public static String getFontFilePath() {
		Resource resource = SpringContext.getApplicationContext().getResource(resourceInfoProperties.getProperty("FONT"));
		String fontFilePath = null;
		try {
			fontFilePath = resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			logger.error("failed to get FONT path", e);
		}
		return fontFilePath;
	}

	/**
	 * @return 利用するジオコーダ
	 */
	public static String getGeocoder() {
		return getString("GEOCODER");
	}

	/**
	 * PathInfo.properties から設定を取得する
	 * @param key キー
	 * @return 設定値
	 */
	public static String getPathInfoProperty(String key) {
		return pathInfoProperties.getProperty(key);
	}

	/**
	 * バージョン情報を "/META-INF/maven/jp.ecom_plat.saigaitask/SaigaiTask2/pom.properties" から取得する。
	 * この pom.properties は Maven によるビルドで生成される。
	 * リリース版では 1.1 1.1.1 1.1.2 ... 1.2 1.2.1 ... のようにメジャーバージョン（＋マイナーバージョン）という書式になる。
	 * メンテナンスブランチでは 1.4-SNAPSHOT のように "メジャーバージョン-SNAPSHOT" という書式になる。(マイナーバージョンは付かず -SNAPSHOT となる)
	 * 開発版でもメンテナンスブランチ同様に -SNAPSHOT が付く。メジャーバージョンが不明な場合は記号となる場合がある dev-global-SNAPSHOT
	 * @return バージョン
	 */
	public static String getVersion() {
		String version = null;
		version = getPomProperties().getProperty("version");
		return version;
	}


	/**
	 * バージョン付与方法として、
	 * X.Y.Z　の最大３つの番号をドットで区切る。
	 * X:　メジャー、大きな変更時に付与する。
	 * Y:　マイナー、機能追加など。
	 * Z:　デバッグ、不具合修正、機能変更はなし。
	 * ※Zが0 の場合は、省略とする。（あえて明示しても問題はない）
	 * ※番号は２桁で付与する場合もある。
	 * ※一般公開せずに一部に公開しているバージョン（農研機構など）はベータ版を意味する「b」をZの後に付ける。
	 * したがって、過去にリリースしたバージョンには追加しない。
	 *
	 * @return 画面上に表示する用のバージョン
	 */
	public static String getVersionForView() {
		String version = getVersion();
		if(StringUtils.isNotEmpty(version)) {
			// SNAPSHOT は外す
			if(isSnapshotVersion()) {
				version = version.replace("-SNAPSHOT", "");
			}
		}
		return version;
	}

	/**
	 *
	 * @return バージョンに -SNAPSHOT が付いているかどうか
	 */
	public static boolean isSnapshotVersion() {
		String version = getVersion();
		if(version!=null) {
			return version.endsWith("-SNAPSHOT");
		}
		return true;
	}

	/**
	 * @return メジャーバージョン配列(要素数: 2) バージョンとれなければ null
	 */
	public static int[] getMajorVersion() {
		String version = getVersion();
		if(version!=null) {
			// -SNAPSHOT がついていれば外す
			if(isSnapshotVersion()) {
				version = version.substring(0, version.length()-"-SNAPSHOT".length());
			}
			String[] elems = version.split("\\.");
			// メジャーバージョンがあるなら要素数は２以上
			if(2<=elems.length) {
				Integer m1 = null, m2 = null;
				try {
					m1 = Integer.parseInt(elems[0]);
					m2 = Integer.parseInt(elems[1]);
				} catch(Exception e) {
					// ignore
				}

				// 2要素目の数値が取れていればメジャーバージョンがパースできている。
				if(m2!=null) {
					return new int[]{m1, m2};
				}
			}

		}
		return null;
	}

	/**
	 * @return SVN Revision
	 */
	public static String getRevision() {
		Manifest manifest = getManifest();
		return manifest!=null ? manifest.getMainAttributes().getValue("SVN-Revision") : null;
	}

	/**
	 * @return データベースバージョン
	 */
	public static String getDBVersion() {
		InitService initService = SpringContext.getApplicationContext().getBean(InitService.class);
		Flyway flyway = initService.getFlyway();
		return flyway.info().current().getVersion().toString();
	}

	/**
	 * @return ログ出力する閾値をミリ秒で設定
	 */
	public static long getLogMinDurationRequest() {
		long ms = 1000L;
		try {
			ms = Long.parseLong(getString("LOG_MIN_DURATION_REQUEST", "1000"));
		} catch(Exception e) {
			logger.error("Cannot get LOG_MIN_DURATION_REQUEST(ms)", e);
		}
		return ms;
	}

	/**
	 * @return eコミマップの時間パラメータをUTCとして扱うかどうか
	 */
	public static boolean isAvailableUTCTimeZone() {
		return true;
	}
}
