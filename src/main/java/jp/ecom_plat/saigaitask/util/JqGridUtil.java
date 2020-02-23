/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import jp.ecom_plat.map.util.StringUtils;

import org.seasar.framework.beans.util.BeanUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * jqGridのユーティリティクラス
 */
public class JqGridUtil {

    //jqgridとS2JDBCの比較演算子のマップ
    static Map<String, String> operStrMap = new HashMap<String, String>();
    static {
        operStrMap.put("EQ", "EQ"); //等しい
        operStrMap.put("NE", "NE"); //等しくない
        operStrMap.put("LT", "LT"); //より小さい
        operStrMap.put("LE", "LE"); //以下
        operStrMap.put("GT", "GT"); //より大きい
        operStrMap.put("GE", "GE"); //以上
        operStrMap.put("BW", "STARTS"); //で始まる
        operStrMap.put("EW", "ENDS");   //で終わる
        operStrMap.put("CN", "CONTAINS");   //を含む
        //以下はS2JDBCのリファレンスでは使える記述があるが、org.seasar.extension.jdbc.AutoSelect のAPIでは使えないようだ。
        //operStrMap.put("BN", "NOT_STARTS");   //で始まらない
        //operStrMap.put("EN", "NOT_ENDS"); //で終わらない
        //operStrMap.put("NC", "NOT_CONTAINS"); //を含まない
    }

	/**
	 * jqgridの比較演算子文字列とカラム名からS2JDBCの検索条件文字列に変換して返却する。
	 * @param propName
	 * @param operStr
	 * @return String
	 */
	public static String getCoditionStr(String propName, String jqgridOperStr) {
        return propName + "_" + operStrMap.get(jqgridOperStr.toUpperCase());
	}

    /**
     * 他者により対象データが変更されているかをチェックする。（楽観排他）
     * @param serializedPreEditData 画面が持っていた編集前テーブルデータのシリアライズ文字列
     * @param targetEntityCls 対象のエンティティクラス
     * @param nowEntity 現時点でのテーブルデータ
     * @return true:他者によりテーブルが変更されていない。false:他者によりテーブルが変更されている。
     * @throws Exception
     */
    public static boolean existsNoChangeByOther(String serializedPreEditData, Class targetEntityCls, Object nowEntity) throws Exception{
//        //編集前データのシリアライズ文字列をデシリアライズ
//        String[] serializedOldDataAry = serializedPreEditData.split("&");
//        Map<String,String> prePostColMap = new HashMap<String,String>();
//        for(int i = 0; i < serializedOldDataAry.length; i++){
//            String[] oldColDataAry = serializedOldDataAry[i].split("=");
//            prePostColMap.put(oldColDataAry[0], (oldColDataAry.length==2)?URLDecoder.decode(oldColDataAry[1], "UTF-8"):"");
//        }
//
//        //ターゲットエンティティからテーブルカラム名一覧取得
//        String[] tableColumnNames = getFieldNames(targetEntityCls).toArray(new String[0]);
//
//        //編集前データmapからテーブルカラムプロパティ値のみコピー
//        Object prePostEntity = targetEntityCls.newInstance();
//        Beans.copy(prePostColMap, prePostEntity).includes(tableColumnNames).execute();
//        //編集前データ中の空文字をnullへ変換
//        prePostEntity = convertEmptyStringToNullForObjectProperty(prePostEntity);
//        //編集前データ中の改行コードを削除
//        prePostEntity = removeLineseparatorForObjectProperty(prePostEntity);
//
//        //エンティティからテーブルカラムプロパティ値のみコピー
//        Object nowEntityOnlyColumn = targetEntityCls.newInstance();
//        Beans.copy(nowEntity, nowEntityOnlyColumn).includes(tableColumnNames).execute();
//        //テーブルデータ中の空文字をnullへ変換
//        nowEntityOnlyColumn = convertEmptyStringToNullForObjectProperty(nowEntityOnlyColumn);
//        //テーブルデータ中の改行コードを削除
//        nowEntityOnlyColumn = removeLineseparatorForObjectProperty(nowEntityOnlyColumn);
//
//        //画面が持っていた編集前テーブルデータと現時点でのテーブルデータとを比較
//        return EqualsBuilder.reflectionEquals(prePostEntity, nowEntityOnlyColumn);
    	return true;
    }

    public static Object escapeHtml(Object obj){
        Map<String, Object> _tmpMap = new HashMap<String, Object>();
        BeanUtil.copyProperties(obj, _tmpMap);
        for(Map.Entry<String, Object> e : _tmpMap.entrySet()) {
        	if(e.getValue() instanceof String){
       			_tmpMap.put(e.getKey(), StringUtils.escapeHTML(e.getValue().toString()));
       		}else{
       			_tmpMap.put(e.getKey(), e.getValue());
       		}
        }
        BeanUtil.copyProperties(_tmpMap, obj);
        return obj;
    }


    /**
     * selectボックスに表示する文字列のリストを生成する。<br>
     * 表示する文字列は基本的に、「コード値：デコード値」。
     * @param entityList selectボックスを生成する対象のテーブルデータ
     * @param code コードプロパティ名
     * @param decode デコードプロパティ名
     * @return selectTagList selectボックスに表示する文字列のリスト
     * @throws Exception
     */
    public static List<Map<String, Object>> createSelectTagList(List<?> entityList, String code, String... decode) throws Exception{
        List<Map<String, Object>> selectTagList = new ArrayList<Map<String, Object>>();
        for(Object entity : entityList){
            Map<String, Object> map = new HashMap<String, Object>();
            Class<?> clazz = entity.getClass();
            Object codeValue = clazz.getField(code).get(entity);
            StringBuffer decodeValue = new StringBuffer();
            for(int i=0; i<decode.length; i++){
                decodeValue.append(clazz.getField(decode[i]).get(entity));
                decodeValue.append("  ");
            }
            map.put("key", codeValue);
            if(decode.length == 1 && "id".equals(decode[0])){
                //デコードカラムが"id"のみならばコード値のみとする。
                map.put("value", codeValue);
            }else{
                map.put("value", codeValue + "：" + decodeValue);
            }
            selectTagList.add(map);
        }

        return selectTagList;
    }

    /**
     * オブジェクトのプロパティ値が空文字（""）であった場合、nullへ変換する。
     * @param obj
     * @return 変換後の引数のオブジェクト
     */
    public static Object convertEmptyStringToNullForObjectProperty(Object obj){
        Map<String, Object> _tmpMap = new HashMap<String, Object>();
        BeanUtil.copyProperties(obj, _tmpMap);
        for(Map.Entry<String, Object> e : _tmpMap.entrySet()) {
        	if("".equals(e.getValue())){
        		_tmpMap.put(e.getKey(), null);
        	}
        }
        BeanUtil.copyProperties(_tmpMap, obj);
        return obj;
    }

    /**
     * オブジェクトのプロパティ値が文字列型ならば、改行コードを削除する。
     * @param obj
     * @return 変換後の引数のオブジェクト
     */
    public static Object removeLineseparatorForObjectProperty(Object obj){
        Map<String, Object> _tmpMap = new HashMap<String, Object>();
        BeanUtil.copyProperties(obj, _tmpMap);
        String LINE_SEPARATOR_PATTERN =  "\r\n|[\n\r\u2028\u2029\u0085]";
        Pattern sp = Pattern.compile(LINE_SEPARATOR_PATTERN);
        for(Map.Entry<String, Object> e : _tmpMap.entrySet()) {
            if(e.getValue() instanceof String){
                //文字列データならば改行コードをシステム改行コードに一律削除。
                Matcher m = sp.matcher(e.getValue().toString());
                _tmpMap.put(e.getKey(), m.replaceAll(""));
            }
        }
        BeanUtil.copyProperties(_tmpMap, obj);
        return obj;
    }

    /**
     * エンティティクラスに特定のプロパティが存在するか判定する。
     * @param entityClassname エンティティクラス名（完全）
     * @param propertyName プロパティ名
     * @return true:存在する false:存在しない
     * @throws Exception
     */
    public static boolean hasEntityProperty(String entityClassname, String propertyName) throws Exception{
    	boolean has = false;
        Class<?> clazz = ReflectionUtil.forName(entityClassname);
    	Field[] fields = clazz.getFields();
    	for(int i=0; i<fields.length; i++){
	    	if(fields[i].getName().equals(propertyName)){
	    		has = true;
	    		break;
	    	}
    	}

    	return has;
    }

	/**
	 * クラスの各フィールド名
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getFieldNames(Class cls) {
		Field[] fields = cls.getDeclaredFields();
		List<String> flist = new ArrayList<String>();
		for (Field field : fields)
			// finai修飾子のフィールドと@JoinColumnのフィールドは除く
			if ( ! Modifier.isFinal(field.getModifiers()) && field.getAnnotation(JoinColumn.class)==null && field.getAnnotation(OneToMany.class)==null) {
				flist.add(field.getName());
			}
		return flist;
	}
}
