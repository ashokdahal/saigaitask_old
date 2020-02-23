/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONString;
import org.seasar.framework.beans.util.BeanUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.tags.S2Functions;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;

public class StringUtil {
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	private static String BR = "<br>";

	/**
	 * カスタムタグとして作成
	 * f:h の<br>だけOK版
	 *
	 * @param input
	 * @return
	 */
	public static String h(String input) {

		String out = S2Functions.h(input);
		StringBuffer buff = new StringBuffer(out);
		int idx = -1;
		while ((idx = buff.indexOf("&lt;br&gt;")) >= 0) {
			buff = buff.replace(idx, idx+10, "<br>");
		}

		return buff.toString();
	}

	public static String ton(String input) {
		if (input == null || input.length() == 0)
			return "";

		String out = input.replaceAll("<br>", "\n").replaceAll("<BR>", "\n")
				.replaceAll("<br />", "\n").replaceAll("<BR />", "\n");
		return out;
	}

	public static String br(String input) {
		if (input == null || input.length() == 0)
			return "";

		return input.replaceAll("\r\n", BR).replaceAll("\r", BR).replaceAll(
				"\n", BR);
	}

	/**
	 * カスタムタグとして作成
	 * 文字列内に改行コードがあると、Javascriptで変数として取得した時にエラーとなるので、その対策用
	 *
	 * @param input
	 * @return
	 */
	public static String nolf(String input) {
		if (input == null || input.length() == 0)
			return "";
		return input.replaceAll("\r\n", "" ).replaceAll("\r", "").replaceAll("\n", "");
	}

	/**
	 * リストをセミコロンで連結した文字列にする.
	 * 引数はオブジェクトにしたいところだけど、EL式でエラーになるのでListにする
	 * @param list リスト
	 * @return セミコロンで連結した文字列
	 */
	public static String join(List<?> list){
		String separator = ",";
		return join(list, separator);
	}

	/**
	 * オブジェクトを指定の連結子で連結した文字列にする.
	 * @param obj オブジェクト
	 * @param splitter 連結子
	 * @return 文字列
	 */
	public static String join(Object obj, String splitter){
		if(obj==null) return null;
		if(obj instanceof List){
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for(Object elem : (List<?>)obj){
				if(first) first=false;
				else sb.append(splitter);
				sb.append(elem);
			}
			return sb.toString();
		}
		else if (obj instanceof String[]) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String elem : (String[])obj) {
				if(first) first=false;
				else sb.append(splitter);
				sb.append(elem);
			}
			return sb.toString();
		}
		else{
			return (String)obj;
		}
	}

	/**
	 * オブジェクトをJSONに変換する
	 * @param obj
	 * @return
	 */
	public static String json(Object obj) {
		/*
		// JSP の ${fal:json()} で呼ばれた場合、BeanWrapper として引数に渡される.
		// bean を取り出して、それを JSON文字列にする.
		if(obj instanceof BeanWrapper) {
			try {
				BeanWrapper beanWrapper = (BeanWrapper) obj;
				Field field = BeanWrapper.class.getDeclaredField("bean");
				field.setAccessible(true);
				Object bean = field.get(beanWrapper);
				return json(bean);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}*/
		// JSONString インタフェースが実装されていたら、それを使って JSON文字列 を取得する.
		if(obj instanceof JSONString) {
			return ((JSONString) obj).toJSONString();
		}
		if(obj instanceof Object[] || obj instanceof List<?> || obj instanceof Enum) {
			return JSONArray.fromObject(obj).toString();
		}
		else if(obj instanceof org.json.JSONObject) {
			return ((org.json.JSONObject) obj).toString();
		}
		else if(obj instanceof org.json.JSONArray) {
			return ((org.json.JSONArray) obj).toString();
		}
		return JSONObject.fromObject(obj).toString();
	}

	/**
	 * オブジェクトをJSONに変換する
	 * javascriptの変数としても使えるように、「"」を「\"」でエスケープした結果を返す
	 * @param obj
	 * @return
	 */
	public static String jsonEsc(Object obj){
		if(obj instanceof Object[] || obj instanceof List<?>) {
			String retStr = JSONArray.fromObject(obj).toString().replaceAll("\"", "\\\\\"");
			return retStr;
		}
		String retStr = JSONObject.fromObject(obj).toString().replaceAll("\"", "\\\\\"");
		return retStr;
	}

	/**
	 * 文字列をIntegerに変換する（EL式用）
	 * @param str
	 * @return
	 */
	public static Integer toInteger(String str) {
		Integer i = null;
		if(org.seasar.framework.util.StringUtil.isNotEmpty(str)) {
			i = Integer.parseInt(str);
		}
		return i;
	}

	/**
	 * 和暦に変換
	 * @param timestamp
	 * @return
	 */
	public static String toWareki(Timestamp timestamp) {
		return toWareki(timestamp, lang.__("MMM.d,yy 'at' H:m"));
	}

	/**
	 * 和暦に変換
	 * @param timestamp
	 * @param pattern
	 * @return
	 */
	public static String toWareki(Timestamp timestamp, String pattern) {
		//Locale.setDefault(new Locale("ja","JP","JP"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, lang.getLocale());
		String result = sdf.format(cal.getTime());
		//Locale.setDefault(Locale.JAPAN);
		return result;
	}

	/**
	 * パスワードの暗号化
	 *
	 * @param password パスワード
	 * @return 暗号化されたパスワード
	 */
	public static String encrypt(String password) {

		try {
			byte[] btePassword = password.getBytes();

			// Salt
			byte[] salt = {
				 (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
				 (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
			};
		 	// Iteration count
			int count = 20;
			PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);

			char[] pass = "bosaicloud".toCharArray();
			PBEKeySpec pbeKeySpec = new PBEKeySpec(pass);
			SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

			Cipher descipher = Cipher.getInstance("PBEWithMD5AndDES");
			descipher.init(Cipher.ENCRYPT_MODE,pbeKey,pbeParamSpec);
			//暗号化
			byte[] cipherPassword = descipher.doFinal(btePassword);
			//byte[]-->String
			String strPassword = toHexString(cipherPassword);
			return strPassword;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "12345678";
	}

	/**
	 * バイト配列の１６進数化
	 *
	 * @param bs バイト配列
	 * @return １６進数化された文字列
	 */
	public static String toHexString(byte[] bs) {

	 	StringBuffer buffer = new StringBuffer(bs.length * 2);
	 	for (int i = 0; i < bs.length; i++) {
			if(bs[i] >= 0 && bs[i]<0x10){
		  		buffer.append('0');
			}
			buffer.append(Integer.toHexString(0xff&bs[i]));
	 	}
		return buffer.toString();
  	}

	/**
	 * 暗号化されたパスワードの復号
	 *
	 * @param strPassword 暗号化されたパスワード
	 * @return 復号化されたパスワード
	 */
	public static String decrypt(String strPassword) {
		try{
			//String-->byte[]
			byte[] cipherPassword = toBytes(strPassword);

			// Salt
			byte[] salt = {
				 (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
				 (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
			};
			// Iteration count
			int count = 20;
			PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);

			char[] pass = "bosaicloud".toCharArray();
			PBEKeySpec pbeKeySpec = new PBEKeySpec(pass);
			SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

			Cipher descipher = Cipher.getInstance("PBEWithMD5AndDES");
			descipher.init(Cipher.DECRYPT_MODE,pbeKey,pbeParamSpec);
			//複号化
			byte[] btePassword = descipher.doFinal(cipherPassword);

			//byte[]-->String
			String password = new String(btePassword);

			return password;
		}catch(Exception exc){
			//エラー処理
		}
		return "12345678";
	}

	/**
	 * １６進数文字列からバイト配列に変換する。
	 *
	 * @param hexString １６進数文字列
	 * @return バイト配列
	 * @throws NumberFormatException
	 */
	public static byte[] toBytes(String hexString) throws NumberFormatException
	{
		if(hexString.length()%2!=0){
			hexString = '0' + hexString;
		}
		byte[] bytes = new byte[hexString.length()/2];
		for (int i = bytes.length-1; i >= 0; i--) {
			String b = hexString.substring(i*2,i*2+2);
			bytes[i] = (byte)Integer.parseInt(b, 16);
		}
		return bytes;
	}

	/**
	 * 英数字を全角から半角へ変換
	 * @param value
	 * @return 半角
	 */
	public static String zenkakuToHankaku(String value) {
	    StringBuilder sb = new StringBuilder(value);
	    for (int i = 0; i < sb.length(); i++) {
	        int c = (int) sb.charAt(i);
	        if ((c >= 0xFF10 && c <= 0xFF19) || (c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) {
	            sb.setCharAt(i, (char) (c - 0xFEE0));
	        }
	    }
	    value = sb.toString();
	    return value;
	}

    /**
     * スネーク型文字列からキャメル型文字列へ変換
     * @param targetStr
     * @return
     */
    public static String snakeToCamel(String targetStr) {
        if(StringUtils.isEmpty(targetStr)){
            return null;
        }
        Pattern p = Pattern.compile("_([a-z])");
        Matcher m = p.matcher(targetStr.toLowerCase());

        StringBuffer sb = new StringBuffer(targetStr.length());
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * スネーク型文字列からキャメル型文字列へ変換し、先頭文字を大文字にする
     * @param targetStr
     * @return
     */
    public static String snakeToCamelCapitalize(String targetStr) {
        if(StringUtils.isEmpty(targetStr)){
            return null;
        }
        return StringUtils.capitalize(snakeToCamel(targetStr));
    }

    /**
     * キャメル型文字列からスネーク型文字列へ変換
     * @param targetStr
     * @return
     */
    public static String camelToSnakeTo(String targetStr) {
        if(StringUtils.isEmpty(targetStr)){
            return null;
        }
        Pattern p = Pattern.compile("([A-Z])");
        Matcher m = p.matcher(StringUtils.uncapitalize(targetStr));

        StringBuffer sb = new StringBuffer(targetStr.length());
        while (m.find()) {
            m.appendReplacement(sb, "_" + m.group(1).toLowerCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 相互参照オブジェクトを階層にもつオブジェクトをJSON形式に変換する。<br>
     * またjson-libがbeanのpublicフィールドに未対応のため、beanをMapに変換して対応する。
     * @param list
     * @return
    */
    public static String jsonForCyclicalReferences(Object obj){
        List<?> list = new ArrayList<Object>();
        if(obj instanceof Object[] || obj instanceof List<?>) {
            if(obj instanceof Object[]){
                list = Arrays.asList(obj);
            }else{
                list = (List<?>)obj;
            }

            List<Map<String, Object>> entityMapList = new ArrayList<Map<String, Object>>();
            for(Object entity : list){
                //Entityリストの1行毎にbeanをMapに変換
                Map<String, Object> entityMap = new HashMap<String, Object>();
                BeanUtil.copyProperties(entity, entityMap);
                for(Map.Entry<String, Object> e : entityMap.entrySet()){
                    //Entityのプロパティ毎に
                    if(e.getValue() != null){
                        if(e.getValue() instanceof List){
                            //Entityのプロパティがリストの場合
                            for(Object refEntity : (List<Object>)e.getValue()){
                                //リストから１件ずつ取り出し
                                Class<?> clazz = e.getValue().getClass();
                                List<Map<String, Object>> refEntityMapList = new ArrayList<Map<String, Object>>();
                                if(clazz.isAnnotationPresent(javax.persistence.Entity.class)){
                                    //取り出したオブジェクトがEntityの場合、benaをMapに変換
                                    Map<String, Object> refEntityMap = new HashMap<String, Object>();
                                    BeanUtil.copyProperties(refEntity, refEntityMap);
                                    refEntityMapList.add(refEntityMap);
                                }
                                e.setValue(refEntityMapList);
                            }
                        }else{
                            Class<?> clazz = e.getValue().getClass();
                            if(clazz.isAnnotationPresent(javax.persistence.Entity.class)){
                                //EntityのプロパティがEntityの場合
                                Map<String, Object> refEntityMap = new HashMap<String, Object>();
                                BeanUtil.copyProperties(e.getValue(), refEntityMap);
                                e.setValue(refEntityMap);
                            }
                        }
                    }
                }
                entityMapList.add(entityMap);
            }

            JsonConfig conf = new JsonConfig();
            conf.registerDefaultValueProcessor(
                    String.class,
                    new DefaultValueProcessor(){
                        public Object getDefaultValue(Class type){
                            return "";
                        }
                    });
            conf.setIgnoreDefaultExcludes(false);
            conf.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            return JSONArray.fromObject(entityMapList, conf).toString();
        }else{
            JsonConfig conf = new JsonConfig();
            conf.setIgnoreDefaultExcludes(false);
            conf.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            // 2017.09.26 SV MOD ---
            // Entityクラスに@lombok.Getter @lombok.Setterアノテーションが付いたので、
            // publicフィールドはJSON化の対象外にする
            //conf.setIgnorePublicFields(false);
            conf.setIgnorePublicFields(true);
            // 2017.09.26 SV MOD ---
            conf.registerDefaultValueProcessor(
                    String.class,
                    new DefaultValueProcessor(){
                        public Object getDefaultValue(Class type){
                            return "I am empty!";
                        }
                    });
            return JSONObject.fromObject(obj, conf).toString();
        }
    }

    /** URLを抽出するための正規表現パターン */
    public static final Pattern convURLLinkPtn =
        Pattern.compile
        ("(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+",
        Pattern.CASE_INSENSITIVE);

    /**
     * 指定された文字列内のURLを、正規表現を使用し、
     * リンク（a href=...）に変換する。
     * @param str 指定の文字列。
     * @param attr aタグの追加属性
     * @return リンクに変換された文字列。
     */
    public static String convURLLink(String str, String attr) {
    	if(StringUtils.isEmpty(attr)) attr = "";
    	else attr = " " + attr;
        Matcher matcher = convURLLinkPtn.matcher(str);
        return matcher.replaceAll("<a href=\"$0\""+attr+">$0</a>");
    }
	/**
	 * 文字列をIntegerに変換する
	 * @param str
	 * @return
	 */
	public static int getInt(String str) {
		int i = 0;
		
		try {
			i = Integer.parseInt(str);
			//Integer.decode(str);
		} catch(Exception e) {
			i = 0;
		}
		
		return i;
	}  
	
	/**
	 * Relace Allの実装
	 * @param strSrc 変換元
	 * @param strDes 変換先
	 * @param strOrg 元の文字列
	 * @return 変換した文字列
	 */
	public static String replaceAll(String strSrc, String strDes, String strOrg) {
		
		int iLen = strSrc.length();
		ArrayList listTemp = new ArrayList();
		for (long i = 0;; i++) {
			int iRet = strOrg.indexOf(strSrc);
			if (iRet == -1) {
				listTemp.add(strOrg);
				break;
			}
			//XmlFileEditHelper.outputLog("iRet=" + iRet);
			//XmlFileEditHelper.outputLog("strOrg=" + strOrg);
			String strT = strOrg.substring(0, iRet);
			//XmlFileEditHelper.outputLog("strT=" + strT);
			listTemp.add(strT);
			
			strOrg = strOrg.substring(iRet + iLen, strOrg.length());
			//XmlFileEditHelper.outputLog("strOrg=" + strOrg);
		}
		StringBuilder b = new StringBuilder();
		for (int i = 0;i < listTemp.size(); i++) {
			b.append(listTemp.get(i));
			if(i < listTemp.size() - 1) {
				b.append(strDes);
			}
		}
		
		return b.toString();
	}	
	 public static String escapeUJava (String json){ 
		json = StringEscapeUtils.escapeJava(json);
		json = replaceAll("\\\"", "\"", json);
		json = replaceAll("\\\\u0027", "'", json);
		return json;
	 }	
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	public static String escapeJSONString(String json) {
		String escapedJSON = json;
		escapedJSON = escapedJSON.replaceAll("\\n", "\\\\n");
		escapedJSON = escapedJSON.replaceAll("\\'", "\\\\'");
		escapedJSON = escapedJSON.replaceAll("\\\"", "\\\\\"");
		escapedJSON = escapedJSON.replaceAll("\\&", "\\\\&");
		escapedJSON = escapedJSON.replaceAll("\\r", "\\\\r");
		escapedJSON = escapedJSON.replaceAll("\\t", "\\\\t");
		escapedJSON = escapedJSON.replaceAll("\\b", "\\\\b");
		escapedJSON = escapedJSON.replaceAll("\\f", "\\\\f");
		return escapedJSON;
	}

	/**
	 * 文字列を切る
	 * @param strText 文字列
	 * @param listRet 戻るArray
	 * @param strSplit 分割文字
	 */
	public static void getSplitArrayD(String strText, ArrayList listRet,
			String strSplit) {
		int i = 0;
		String strTempLine = "";
		strText.trim();
		int iLen = strText.length();
		for (i = 0; i < iLen; i++) {
			char c = strText.charAt(i);
			if (strSplit.indexOf(c) != -1) {
				listRet.add(strTempLine);
				strTempLine = "";
			} else {
				strTempLine = strTempLine + c;
			}
		}
		if (iLen == i && strTempLine.length() > 0) {
			listRet.add(strTempLine);
		}
		if (iLen > 0 && listRet.size() < 1) {
			listRet.add(strText);
		}
	}
    /**
     * UUIDを取得する
     * @return UUID
     */
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    } 
    /**
     * UUIDを取得する
     * @return UUID
     */
    public static String getUUID(String strPre){ 
        String s = UUID.randomUUID().toString(); 
        return strPre + s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    } 	

	/**
	 * 配列結合
	 * jp.ecom_plat.map.util.StringUtils.concat の移植
	 */
	static public String[] concat(String[] arr1, String[] arr2)
	{
		String[] join = new String[arr1.length+arr2.length];
		int idx = 0;
		for (String str : arr1) join[idx++] = str;
		for (String str : arr2) join[idx++] = str;
		return join;
	}

	/**
	 * @param query
	 * @return クエリ分割Map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, List<String>> splitQuery(String query) throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = query.split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			//final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
			final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}

	/**
	 * @param query_pairs
	 * @return クエリ文字列
	 * @throws UnsupportedEncodingException
	 */
	public static String toQuery(Map<String, List<String>> query_pairs) throws UnsupportedEncodingException {
		StringBuffer query = new StringBuffer();
		for(Map.Entry<String, List<String>> entry : query_pairs.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			for(String value : values) {
				if(value==null) continue;
				if(query.length()!=0) query.append("&");
				//query.append(URLEncoder.encode(key, "UTF-8"));
				//query.append("=");
				//query.append(URLEncoder.encode(value, "UTF-8"));
				query.append(key+"="+value);
			}
		}
		return query.toString();
	}

	/**
	 * @param params パラメータMap
	 * @return クエリ文字列
	 * @throws UnsupportedEncodingException
	 */
	public static String mapToQueryString(Map<String, String> params) {
		try {
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, String> e : params.entrySet()){
				if(sb.length() > 0){
					sb.append('&');
				}
				sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=');
				if(StringUtils.isNotEmpty(e.getValue())) sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
			}
			return sb.toString();
		} catch(Exception e) {
			throw new ServiceException("StringUtil.mapToQueryString exception: "+e.getMessage(), e);
		}
	}
	
    /** Write the object to a Base64 string. */
	public static String serialize(Serializable serializable) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(serializable);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
	}

	/**
	 * Read the object from Base64 string. 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static Object deserialize(String s) throws IOException, ClassNotFoundException {
		 byte [] data = Base64.getDecoder().decode( s );
		 ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		 Object o  = ois.readObject();
		 ois.close();
		 return o;
	}
}
