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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;

import org.apache.log4j.Logger;

/**
 * <div lang="ja">DBベースの言語リソースを維持するクラス。 </div>
 * <div lang="en">Class to store language messages</div>
 */
public class SaigaiTaskDBLang {

	/**<div lang="ja">現在のメッセージの言語コードを指定する。</div>
	 * <div lang="en">Current language code of storing messages</div>*/
	protected String langCode;

	/**
	 * スレッド単位で lang を管理
	 * Runnable や Callable など別スレッドを起動した場合など、
	 * HttpRequest でないスレッドで SaigaiTaskLangUtils.getSessionLang() が利用できない場合は、
	 * ThreadLocal を使ってスレッド単位で lang を管理する。
	 */
	public static ThreadLocal<SaigaiTaskDBLang> langThreadLocal = new ThreadLocal<>();

	/**
	 * <div lang="ja">
	 * メッセージデータを維持するハッシューテーブル。<br/>
	 * 同一英文メッセージを区別するための接尾語
	 * </div>
	 *
	 * <div lang="en">
	 * Hash table to store message data. <br/>
	 * Add postfix the messages which are the same in English
	 * </div>
	 **/
	public Map<String, String> mapping = new HashMap<String, String>();

	protected static final String msgSuffix = "<!--[0-9]*-->$";

	public SaigaiTaskDBLang() {
	}

	/**　
	 * <div lang="ja">
	 * 指定する言語に対してのDBLangオブジェクトを作成する。
	 * @param 	String 	lang_code: 言語コードを指定する。
	 * </div>
	 *
	 * <div lang="en">
	 * Create DBLang object for a specific language code.
	 * @param 	String 	lang_code: Indicate language code
	 * </div>
	 */
	public SaigaiTaskDBLang(String langCode) {
		this.langCode = langCode;
	}


	/**
	 * <div lang="ja">
	 * １つのメッセージをハッシューテーブルに「キー／値」形で追加する。
	 *
	 * @param	String	メッセージID
	 * @param	String 	メッセージ内容
	 * </div>
	 *
	 * <div lang="en">
	 * Store one message as key/value pair in hash table.
	 *
	 * @param	String	Message ID
	 * @param	String 	Message content with respect to the current language code
	 * </div>
	 */
	public void putString(String key, String value) {
		mapping.put(key, value);
	}

	/**
	 * <div lang="ja">
	 * メッセージマッピングでメッセージ一括追加する。
	 *
	 * @param	Map<String, String>	メッセージマッピング
	 * </div>
	 *
	 * <div lang="en">
	 * Set batch of message data by mapping object.
	 *
	 * @param	Map<String, String>	Message mapping
	 * </div>
	 */
	public void putMessageMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}

	/**
	 * <div lang="ja">
	 * 現在の言語コードを返す。
	 *
	 * @return	String	内部の言語コード（lang_code）を返す。
	 * </div>
	 *
	 * <div lang="en">
	 * Return current language code.
	 *
	 * @return	String	Return internal language code （lang_code）
	 * </div>
	 */
	public String getLangCode() {
		if (langCode == null) {
			SaigaiTaskDBLang lang = SaigaiTaskLangUtils.getSessionLang();
			// スレッドで管理している langCode があれば返す
			if(lang==null) lang = langThreadLocal.get();
			if (lang != null)
				return lang.langCode;
			// 取得できない場合は、OSの言語
			return System.getProperty("user.language");
		}
		return langCode;
	}


	/**
	 * <div lang="ja">
	 * メッセージ値をキーで取得する。もし、キーが存在しなければこのキーを値として返します。
	 *
	 * @param	String 	メッセージID
	 * @return	String 	メッセージ内容
	 * </div>
	 *
	 * <div lang="en">
	 * Get message content through message key. If the key does not exists in the mapping, return the key itself.
	 *
	 * @param	String 	Message ID
	 * @return	String 	Message content
	 * </div>
	 */
	public String __(String key) {
		if (langCode == null) {
			SaigaiTaskDBLang lang = SaigaiTaskLangUtils.getSessionLang();
			// スレッドで管理している langCode があれば返す
			if(lang==null) lang = langThreadLocal.get();
			if (lang != null)
				return lang.__(key);
			else
				return key;
		}
		else if (langCode.isEmpty()) {
			return key;
		}

		String value = mapping.get(key);
		if (value == null)
			if(key==null) return null;
			else return key.replaceFirst(msgSuffix, "");
		else
			return value;
	}

	public String _E(String key) {
		String value = __(key);
		return value.replaceAll("\n", "\\\\n");
	}

	/**
	 * <div lang="ja">
	 * メッセージ値をキーで取得する。もし、キーが存在しなければこのキーを値として返します。
	 *
	 * @param	String 	メッセージID
	 * @param paramValue パラメータ値（複数化）、複数の場合は "param1","param2"...で代入
	 * @return	String 	パラメータ値を代入されたメッセージ
	 * </div>
	 *
	 * <div lang="en">
	 * Get message content through message key. If the key does not exists in the mapping, return the key itself.
	 * This function allow some parameters inside the message content to be replaced by specific value.
	 *
	 * @param	String 	Message ID
	 * @param paramValue Parameter values (multiple). If there are multiple values, pass the values in form of "param1","param2"...
	 * @return	String 	Message content with parameters are already replaced.
	 * </div>
	 */
	public String __(String key, Object... paramValue) {
		String value = __(key);
		//value = value.replaceAll("'", "''");
		MessageFormat messageFormat = new MessageFormat(value);
		return messageFormat.format(paramValue);
	}

	public String getJqgridLocaleFile(){
		// 現在の言語ロケールを取得
		String currentLangCode = langCode;
		String jqGridLocalFileName = "grid.locale-" + currentLangCode.toLowerCase() + ".js";

		return jqGridLocalFileName;
	}

	/**
	 * エンティティクラスから{@link EntityNameInterface}を使って名称を取得する。
	 * @param entityClass エンティティクラス
	 * @return	String 	メッセージ内容
	 */
	public String getEntityName(Class<?> entityClass) {

		String entityName = entityClass.getSimpleName();
		try {
			@SuppressWarnings("unchecked")
			Class<EntityNameInterface> namesClass = (Class<EntityNameInterface>) SAStrutsUtil.getEntityNamesClass(entityClass);
			EntityNameInterface entityNameInterface = namesClass.newInstance();
			entityName = entityNameInterface.entityName();
		} catch(Exception e) {
			Logger.getLogger(getClass())
			.error(e.getMessage(), e);
		}

		return this.__(entityName);
	}

	/**
	 * Locale オブジェクトを返す
	 * @return Locale オブジェクト
	 */
	public Locale getLocale() {
		String langage = getLangCode();
		if ("ja".equals(langage))
			return new Locale("ja", "JP", "JP");
		else
			return new Locale(langage);
	}
}
