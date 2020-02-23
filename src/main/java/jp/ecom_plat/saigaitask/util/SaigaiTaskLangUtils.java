/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
//SVLANGTEST
/* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

package jp.ecom_plat.saigaitask.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.ecom_plat.map.db.Language;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangmesInfo;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangmesInfoService;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.container.SingletonS2Container;

/**
 * <div lang="ja">
 * 多言語利用のため、Resource Bundle を処理・格納するクラス。
 * </div>
 * <div lang="en">
 * Class to save and process Resource Bundle for multiple languages.
 * </div>
 */
public class SaigaiTaskLangUtils {

	public static String LANG_CODES = "en,ja";
	public static String LANG_NAMES = "English,\u65e5\u672c\u8a9e";

	private static HashMap<String, String> langJsonMap = new HashMap<String, String>();
	private static MapDB mapDB = null;

	/**<div lang="ja">最終的なデフォルト言語コード。</div>
	 * <div lang="en">The final default language code.</div>*/
	public static String DEFAULT_LANGUAGE = "ja";

	public static String SYSTEM_DEFAULT_LANGUAGE = "SYSTEM_DEFAULT_LANGUAGE";
	private static Map<String,Map<String,String>> messageCache = new HashMap<String,Map<String,String>>();
	//private static HashSet<String> JSMessages = new HashSet<String>();
	private static Map<Object, HashSet<String>> JSMessages = new HashMap<Object, HashSet<String>>();
	private static Logger logger = Logger.getLogger(SaigaiTaskLangUtils.class.getName());

    /**<div lang="ja"> *.js ファイル探索対象ディレクトリ。</div>
	 * <div lang="en">Target directory to search *.js.</div>*/
	static protected final String[] jsDirs = { "js", "admin-js" };
	static protected final Pattern jsDirExclude = Pattern.compile("ckeditor.*|jquery.*|OpenLayers|i18n");
	static protected final Pattern jsFileExclude = Pattern.compile("jquery.*|.*\\.min\\.js|.*-all\\.js");
	static protected final String  jqGridLocaleDir = "admin-js/js/i18n";
	static protected final Pattern jqGridLocaleFilePattern = Pattern.compile("grid\\.locale-[a-z][a-z]\\.js");

	/**
	 * <div lang="ja">
	 * 言語レコード内のパラメータをフォーマット実施。
	 * @param msg パラメータ含む言語レコード
	 * @param paramValue パラメータ値（複数化）、複数の場合は "param1","param2"...で代入
	 * @return  パラメータ値を代入された言語レコード
	 * </div>
	 * <div lang="en">
	 * Format language messsage with replacement of embedded parameters.
	 * @param msg Language message with parameters inside
	 * @param paramValue Parameter values (multiple). In case of multiple value, assign "param1","param2"...
	 * @return  Language message with all parameters are replaced
	 * </div>
	 * */
	static public String getMessages(String msg, Object... paramValue) {
		MessageFormat messageFormat = new MessageFormat(msg);
		return messageFormat.format(paramValue);
	}

	static public String getLangJson(HttpServletRequest request)
	{
		String langJson = null;

		try {
			final SaigaiTaskDBLang langResource = SaigaiTaskLangUtils.getSiteDBLang(request);

			// マップからの取得を試す
			synchronized (langJsonMap) {
				langJson = langJsonMap.get(langResource.langCode);
				if (langJson != null)
					return langJson;
			}

			/*javascrypt で使用さている全文字列を取得*/
			String contextPath = request.getServletContext().getRealPath("");
			final Set<String> jsResource = SaigaiTaskLangUtils.getJSMessages(contextPath);

			Map<String,String> langHashMap = langResource.mapping;
			JSONObject langJsonObj = new JSONObject();
			for (String key : jsResource) {
				String val = langHashMap.get(key);
				if (val != null) langJsonObj.put(key, val);
			}
			//マップに保存
			langJson = langJsonObj.toString();
			synchronized (langJsonMap) {
				langJsonMap.put(langResource.langCode, langJson);
			}
			return langJson;

		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	static public SaigaiTaskDBLang getSiteDBLang(String langCode) {
		return getSiteDBLang(null, langCode);
	}

	static public SaigaiTaskDBLang getSiteDBLang(HttpServletRequest request, String langCode) {
		SaigaiTaskDBLang dbLang = null;
		MultilangInfoService multilangInfoService = SpringContext.getApplicationContext().getBean(MultilangInfoService.class);
		try {
			// スキーマの切り替え
			multilangInfoService.setSchema(langCode);

			Map<String, String> mapping = SaigaiTaskLangUtils.getLangMessages(langCode);

			dbLang = new SaigaiTaskDBLang(langCode);				//<div lang="ja">言語リソースはDBから</div>

			dbLang.putMessageMapping(mapping);

			// session に  SaigaiTaskDBLang のインスタンスを保存する
			if(request!=null) {
				request.getSession().setAttribute(Constants.SESSIONPARAM_LANG, dbLang);
			}
		}
		catch (Exception e) {											//<div lang="ja">例外の場合は：言語コードは DEFAULT_LANGUAGE、言語リソースは .properties から</div>
		}

		return dbLang;
	}

	static private SaigaiTaskDBLang getSiteDBLang(HttpServletRequest request, boolean setSchrma) {
		SaigaiTaskDBLang dbLang = null;
		MultilangInfoService multilangInfoService = SpringContext.getApplicationContext().getBean(MultilangInfoService.class);
		LocalgovInfoService localgovInfoService = SpringContext.getApplicationContext().getBean(LocalgovInfoService.class);

		// セッションから SaigaiTaskDBLang インスタンスの取得を試す
		HttpSession session = null;
		if (request != null) {
			session =  request.getSession();
			if (session != null) {
				dbLang = (SaigaiTaskDBLang)session.getAttribute(Constants.SESSIONPARAM_LANG);
				// 翻訳テーブルが変更されている場合は破棄して取得しなおす
				if (dbLang != null && dbLang.mapping != messageCache.get(dbLang.langCode))
					dbLang = null;
			}
		}

		// セッションから lSaigaiTaskDBLang インスタンスを取得できなかった場合は、
		// ログイン画面からのリクエストと判断する
		if (dbLang == null) {
			String langCode = null;
	    	String url2 = request.getServerName();
	    	if (url2 != null && url2.length() > 0) {
				langCode = SaigaiTaskLangUtils.DEFAULT_LANGUAGE;
				//if (localgovInfoService == null){
				//	localgovInfoService = new LocalgovInfoService();
				//}
	        	LocalgovInfo gov = localgovInfoService.findByURLAndValid(url2);
	        	if (gov != null){
	    			if (gov.multilanginfoid != null){
		    			MultilangInfo multilangInfo = multilangInfoService.findById(gov.multilanginfoid);
		    			if(multilangInfo != null ){
		    				langCode = multilangInfo.code;
		    			}
	    			}
	        	}
	    	}

			// 言語コードを取得できなかった場合はデフォルト言語コードをセット
			if(langCode == null || langCode.length() <= 0){
				langCode = SaigaiTaskLangUtils.DEFAULT_LANGUAGE;
			}

			dbLang = SaigaiTaskLangUtils.getSiteDBLang(request, langCode);
		}

		else if (setSchrma) {

			// スキーマの切り替え
			multilangInfoService.setSchema(dbLang.langCode);
		}

		return dbLang;
	}

	static public SaigaiTaskDBLang getSiteDBLang(HttpServletRequest request) {
		return SaigaiTaskLangUtils.getSiteDBLang(request, false);
	}

	/**
	 * Action クラスの @Execute メソッドの最初に実行する初期化処理
	 * @param request TODO
	 * @return
	 */
	public static SaigaiTaskDBLang initLang(HttpServletRequest request) {
		return SaigaiTaskLangUtils.getSiteDBLang(request, true);
	}

	/**
	 * session より SaigaiTaskDBLang のインスタンスを取り出す。
	 *
	 * @return
	 */
	public static SaigaiTaskDBLang getSessionLang() {
		HttpServletRequest request =  SpringContext.getRequest();
		if(request!=null) {
			HttpSession session = request.getSession();
			return (SaigaiTaskDBLang) session.getAttribute(Constants.SESSIONPARAM_LANG);
		}
		/*
		// Spring では sessionScope は利用不可
		Map<String, Object> sessionScope = SpringContext.getApplicationContext().getBean("sessionScope");
		if (sessionScope != null) {
			return (SaigaiTaskDBLang)sessionScope.get(Constants.SESSIONPARAM_LANG);
		}
		*/
		return null;
	}


	/**
	 * <div lang="ja">
	 * アクセスしているシステム管理者に対しシステム管理画面の言語コードを設定する。
	 * @param	String 	システム管理画面の新しい言語コード
	 * </div>
	 *
	 * <div lang="en">
	 * Set system administrator's language code.
	 * @param	String	System administrator's new language code
	 * </div>
	 */
	static public void setCurrAdminLangCode(String langCode) {
		if (mapDB == null) mapDB = MapDB.getMapDB();
		try {
			mapDB.setOption(SYSTEM_DEFAULT_LANGUAGE, langCode);
		}
		catch (Exception e) {
		}
	}


	/**
	 * <div lang="ja">
	 * eコミマップが対応している言語リストを取得する。この関数は設定プロパティからLANG_CODESとLANG_NAMESキー OR LANG_CODES_INSTALLとLANG_NAMES_INSTALLキー の値を取得しLanguage列形で返す。
	 * この関数はインストール画面から呼ばれるべきです。
	 * @param	String				ターゲットを指定する。
	 * 								"install"の場合は言語がインストール画面で利用され、LANG_CODES_INSTALLとLANG_NAMES_INSTALLキーから読む。
	 * 								"system"の場合は言語がシステムで利用され、LANG_CODESとLANG_NAMESキーから読む。
	 * @return	Vector<Language>	対応している言語をVector型で返す。
	 * </div>
	 *
	 * <div lang="en">
	 * Get e-Com Map's list of supporting languages. This function will get LANG_CODES and LANG_NAMES key-value pair OR LANG_CODES_INSTALL and LANG_NAMES_INSTALL key-value pair from setting properties file and return data in form of Language object.
	 * This function should be called from install form.
	 * @paremt	String				Indicate the target to which the language list is used
	 * 								"install": indicate that the language list is used in install form, then the list is loaded from LANG_CODES_INSTALL and LANG_NAMES_INSTALL key-value pair
	 * 								"system": indicate that the language list is used in system, then the list is loaded from LANG_CODES and LANG_NAMES key-value pair
	 * @return	Vector<Language>	Return list of Language objects in form of Vector
	 * </div>
	 */
	static public Vector<jp.ecom_plat.map.db.Language> getInstallSupportLanguages(String target) {
		ResourceBundle bundle = ResourceBundle.getBundle("LangInfo", Locale.getDefault());
		if (target.equals("install")) {
			LANG_CODES = bundle.getString("LANG_CODES_INSTALL");
			LANG_NAMES = bundle.getString("LANG_NAMES_INSTALL");
		}
		else {
			LANG_CODES = bundle.getString("LANG_CODES");
			LANG_NAMES = bundle.getString("LANG_NAMES");
		}

		String[] codes = LANG_CODES.split(",");
		String[] names = LANG_NAMES.split(",");

		Vector<jp.ecom_plat.map.db.Language> languages = new Vector<Language>();
		Language language = null;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		for (int i = 0; i < codes.length; i++) {
			language = new Language (codes[i], names[i], timestamp);
			languages.add(language);
		}

		return languages;
	}


	/**
	 * <div lang="ja">
	 * eコミマップが対応している言語リストを取得する。この関数はDBの _language テーブルから対応言語リストを取得しLanguage列形で返す。
	 *
	 * @return	Vector<Language>	対応している言語をVector型で返す。
	 * </div>
	 *
	 * <div lang="en">
	 * Get e-Com Map's list of supporting languages. This function obtains supporting languages from table _language in DB, then return the result in form of array of Language objects.
	 *
	 * @return	Vector<Language>	Return supporting languages in Vector type
	 * </div>
	 */
	static public Vector<jp.ecom_plat.map.db.Language> getSupportLanguages() {
		if (mapDB == null) mapDB = MapDB.getMapDB();
		try {
			Vector<jp.ecom_plat.map.db.Language> languages = mapDB.getSupportLanguages();
			return languages;
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * <div lang="ja">
	 * 言語のメッセージデータを取得する。キャッシュに存在する場合はキャッシュから、存在しない場合はDBから取得する。
	 *
	 * @param	String	言語コード
	 * </div>
	 *
	 * <div lang="en">
	 * Get language message data. If the data is saved in cache, obtain data from cache. Otherwise, read data from DB.
	 *
	 * @param	String	Language code
	 * </div>
	 */
	static public Map<String, String> getLangMessages(String langCode) throws Exception {
		MultilangInfoService multilangInfoService = SpringContext.getApplicationContext().getBean(MultilangInfoService.class);
		MultilangmesInfoService multilangmesInfoService = SpringContext.getApplicationContext().getBean(MultilangmesInfoService.class);
		synchronized(messageCache) {

			// キャッシュからの取得を試みる
			Map<String, String> mapping = messageCache.get(langCode);
			if (mapping == null) {

				// 言語情報ID を求める
				Long multilanglnfoid = null;
				MultilangInfo multilangInfo = multilangInfoService.findByCode(langCode);
				if(multilangInfo != null){
					multilanglnfoid = multilangInfo.id;
				}else{
					langCode = SaigaiTaskLangUtils.DEFAULT_LANGUAGE;
					multilangInfo = multilangInfoService.findByCode(langCode);
					if(multilangInfo != null){
						multilanglnfoid = multilangInfo.id;
					}else{
						multilanglnfoid = 0L;
					}
				}

				// DB から取得する
				mapping = new HashMap<String,String>();
				List<MultilangmesInfo> multilangmesInfoList = multilangmesInfoService.findByMultilanginfoid(multilanglnfoid);
				for(MultilangmesInfo multilangmesInfo : multilangmesInfoList){
					mapping.put(multilangmesInfo.messageid, multilangmesInfo.message);
				}

				// キャッシュに保存する
				messageCache.put(langCode, mapping);
			}

			return mapping;
		}
	}

	/**
	 * <div lang="ja">
	 * 指定された言語のキャッシュされたメッセージを破棄する。
	 *
	 * @param	String	言語コード
	 * </div>
	 *
	 * <div lang="en">
	 * Discard message data of a language from cache.
	 *
	 * @param	String	Language code
	 * </div>
	 */
	static public void discardMessageCache(String langCode) throws Exception {
		synchronized(messageCache) {
			messageCache.remove(langCode);
		}
		synchronized(langJsonMap) {
			langJsonMap.clear();
		}
	}

	/**
	 * <div lang="ja">
	 * ディレクトリを再帰的に探索し、*.js という名前のファイルを見つけ出す。
	 *
	 * @param File ディレクトリ
	 * @return List<File> ファイルの一覧
	 * </div>
	 *
	 * <div lang="en">
	 * Find all JS file recursively from a directory.
	 *
	 * @param File Directory
	 * @return List<File> List of file
	 * </div>
	 */
	static protected List<File> listJSFiles(File dir, Pattern jsDirExclude) {
		LinkedList<File> list = new LinkedList<File>();
		File[] listFiles = dir.listFiles();
		if(listFiles==null) return new ArrayList<File>();
		for (File file : listFiles) {
			if (file.isDirectory()) {
				if (jsDirExclude == null || !jsDirExclude.matcher(file.getName()).find())
					list.addAll(listJSFiles(file, jsDirExclude));
			}
			else if (file.getName().endsWith(".js"))
				list.add(file);
		}
		return list;
	}

	/**
	 * <div lang="ja">
	 * *.js ファイルで使用されている国際化対象文字列を拾い出す。
	 *
	 * @param String topDir ディレクトリ
	 * @return Set<String> 文字列の一覧
	 * </div>
	 *
	 * <div lang="en">
	 * Pick out Japanese message from .js files for multiple language processing.
	 *
	 * @param String topDir Top directory
	 * @return Set<String> List of strings
	 * </div>
	 */
	static public Set<String> getJSMessages(String topDir) {
		return getJSMessages(topDir, jsDirs);
	}

	/**
	 * <div lang="ja">
	 * *.js ファイルで使用されている国際化対象文字列を拾い出す。
	 *
	 * @param String topDir ディレクトリ
	 * @param String[] dirs 探索対象ディレクトリの配列
	 * @return Set<String> 文字列の一覧
	 * </div>
	 *
	 * <div lang="en">
	 * Pick out Japanese message from .js files for multiple language processing.
	 *
	 * @param String topDir Top directory
	 * @return Set<String> List of strings
	 * @param String[] dirs an array of the directories searched
	 * </div>
	 */
	private static Set<String> getJSMessages(String topDir, String[] dirs) {
		synchronized(JSMessages) {
			HashSet<String> hs = JSMessages.get(dirs);
			if (hs != null)
				return hs;

			hs = new HashSet<String>();
			for (String subDir : dirs) {
				File dir  = new File(topDir + "/" + subDir);
				List<File> files = listJSFiles(dir, jsDirExclude);
				for (File file : files) {
					if (jsFileExclude.matcher(file.getName()).find()) {
						continue;
					}
					try {
						BufferedReader br = new BufferedReader(new FileReader(file));
						StreamTokenizer st = new StreamTokenizer(br);
						st.slashStarComments(true);
						st.slashSlashComments(true);
					 	st.ordinaryChar('.');
						st.ordinaryChar('-');
						st.ordinaryChar('/');
						st.ordinaryChar('<');
						st.ordinaryChar('>');
					 	st.wordChars('_', '_');
						do {
							if (st.nextToken() == StreamTokenizer.TT_WORD &&
								st.sval.equals("lang") &&
								st.nextToken() == '.' &&
								st.nextToken() == StreamTokenizer.TT_WORD &&
								st.sval.equals("__") &&
								st.nextToken() == '(' &&
								(st.nextToken() == '"' || st.ttype == '\'')) {
								hs.add(st.sval);
								st.nextToken();
							}
						} while (st.ttype != StreamTokenizer.TT_EOF);
						br.close();
					}
					catch (java.io.IOException e) {
					}
				}
			}
			JSMessages.put(dirs, hs);
			return hs;
		}
	}

	/**
	 * <div lang="ja">
	 * JqGridのロケールファイル群から言語コードリストを作成する
	 *
	 * @param _request HttpServletRequest
	 * @return List<String> 言語コードリスト
	 * </div>
	 *
	 * <div lang="en">
	 * Create language code list from JqGrid locale files.
	 *
	 * @param _request HttpServletRequest
	 * @return List<String> Language code list
	 * </div>
	 */
	public static List<String> getJqgridLangCodes(HttpServletRequest _request) {
		String contextPath = _request.getServletContext().getRealPath("");
		String topDir = contextPath;
		List<String> retValue = new ArrayList<String>();

		File dir  = new File(topDir + "/" + jqGridLocaleDir);
		List<File> files = listJSFiles(dir, null);
		for (File file : files) {
			if (jqGridLocaleFilePattern.matcher(file.getName()).find()) {
				String langCode = file.getName().substring(file.getName().indexOf("-") + 1 , file.getName().indexOf("-") + 3);
				retValue.add(langCode);
			}
		}
		return retValue;
	}
}
