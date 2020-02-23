/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.ClassLoaderUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.AnnotationUtil;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResult.AttrResult;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.util.GeometryUtils;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.LoginData;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * データベースのテーブル操作のサービスクラス
 */
@org.springframework.stereotype.Service
public class TableService {

	Logger logger = Logger.getLogger(TableService.class);
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/** ログイン情報 */
	@Resource protected LoginDataDto loginDataDto;

	/**
	 * JDBCマネージャです。
	 */
	@Resource
	protected JdbcManager jdbcManager;

	/** HTTP Session */
	@Resource protected HttpSession session;

	/** Service */
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected LayerService layerService;
	@Resource protected TrackmapInfoService trackmapInfoService;

	/**
	 * マスタ参照の項目名かどうか判定する
	 * @param colName
	 * @return マスタ参照項目フラグ
	 */
	public static boolean isMasterColumn(String colName) {
		//末尾がinfoid 除外
		Pattern p = Pattern.compile(".infoid$");
		Matcher m = p.matcher(colName);
		if (m.find()) return false;

		//末尾がidはOK
		p = Pattern.compile(".id$");
		m = p.matcher(colName);
		if (m.find()) return true;

		//idの後ろに数値の場合OK
		p = Pattern.compile(".id\\d+$");
		m = p.matcher(colName);
		if (m.find()) return true;

		//if(
		//	(colName.length()>6 && colName.substring(colName.length()-2).equals("id") && !(colName.substring(colName.length()-6).equals("infoid")))
		//	|| (colName.length()<=6 && colName.length()>2 && colName.substring(colName.length()-2).equals("id"))
		//)
		//	return true;
		return false;
	}

	/**
	 * 情報参照の項目名かどうか判定する
	 * @param colName
	 * @return 情報参照項目フラグ
	 */
	public static boolean isInfoColumn(String colName) {
		if (colName.length()>6 && colName.substring(colName.length()-6).equals("infoid"))
			return true;
		return false;
	}

	/**
	 * 情報参照の項目名かどうか判定する
	 * @param colName
	 * @return 情報参照項目フラグ
	 */
	public static boolean isDataColumn(String colName) {
		if (colName.length()>6 && colName.substring(colName.length()-6).equals("dataid"))
			return true;
		return false;
	}

	/**
	 * レイヤ情報を取得する.
	 * @param tablename テーブル名
	 * @return レイヤ取得失敗時は null となる
	 */
	public static LayerInfo getLayerInfo(String tablename) {
		LayerInfo layerInfo = null;
		try {
			layerInfo = MapDB.getMapDB().getLayerInfo(tablename);
		} catch(Exception e) {
			Logger.getLogger(TableService.class).warn(tablename+" LayerInfo not found: "+e.getMessage(), e);
		}
		return layerInfo;
	}

	/**
	 * データ数をカウントする
	 * @param tablename	テーブル名
	 * @return データ数
	 */
	public Long getCount(String tablename)
	{
		return getCount(tablename, null);
	}
	/**
	 * データ数をカウントする
	 * @param tablename	テーブル名
	 * @param timeParam 時間パラメータ
	 * @return データ数
	 */
	public Long getCount(String tablename, Date[] timeParam)
	{
		LayerInfo layerInfo = getLayerInfo(tablename);

		// 履歴レイヤの場合
		if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			if(timeParam==null) timeParam = new Date[]{new Date()};
			String timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			BeanMap beanMap = jdbcManager.selectBySql(BeanMap.class, "select count(*) AS count from \""+tablename+"\" where true "+timeQuery).getSingleResult();
			return (Long) beanMap.get("count");
		}

		// 通常のテーブルの場合
		BeanMap beanMap = jdbcManager.selectBySql(BeanMap.class, "select count(*) AS count from \""+tablename+"\"").getSingleResult();
		return (Long) beanMap.get("count");
	}

	/**
	 * データ数をカウントする
	 * @param tablename	テーブル名
	 * @return
	 */
	public Long getCount(String tablename, long dataid)
	{
		if (isExistsColumn(tablename, "trackdataid")) {
			BeanMap beanMap = jdbcManager.selectBySql(BeanMap.class, "select count(*) AS count from \""+tablename+"\" where trackdataid = "+dataid).getSingleResult();
			return (Long) beanMap.get("count");
		}
		else if (isExistsColumn(tablename, "localgovinfoid")) {
			BeanMap beanMap = jdbcManager.selectBySql(BeanMap.class, "select count(*) AS count from \""+tablename+"\" where localgovinfoid = "+dataid).getSingleResult();
			return (Long) beanMap.get("count");
		}
		else
			return 0l;
	}

	/**
	 * データを合計する
	 * @param tablename	テーブル名
	 * @return
	 */
	public BigDecimal getSum(String tablename, String attrid, String cast, Date[] timeParam)
	{
		LayerInfo layerInfo = getLayerInfo(tablename);

		BeanMap beanMap = null;
		// 通常のテーブルの場合
		if(layerInfo==null || TimeSeriesType.NONE.equals(layerInfo.timeSeriesType)) {
			beanMap = jdbcManager.selectBySql(BeanMap.class, "select sum(cast(case when length("+attrid+") =0 then null else "+attrid+" end as "+cast+")) AS total from \""+tablename+"\" where true ").getSingleResult();
		}
		// 履歴レイヤの場合
		else if(TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			if(timeParam==null) timeParam = new Date[]{new Date()};
			String timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			beanMap = jdbcManager.selectBySql(BeanMap.class, "select sum(cast(case when length("+attrid+") =0 then null else "+attrid+" end as "+cast+")) AS total from \""+tablename+"\" where true "+timeQuery).getSingleResult();
		}

		try {
			if (beanMap != null) {
				Object total = beanMap.get("total");
				if (total != null) {
					BigDecimal big = new BigDecimal(total.toString());
					return big;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BigDecimal(0);
	}

	/**
	 * データ検索
	 * @param tablename テーブル名
	 * @return 結果リスト
	 */
	public List<BeanMap> selectAll(String tablename)
	{
		return selectAll(tablename, null, null, null);
	}
	/**
	 * データ検索
	 * @param tablename	テーブル名
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @return 結果リスト
	 */
	public List<BeanMap> selectAll(String tablename, String asc, String col) {
		return selectAll(tablename, asc, col, null);
	}
	/**
	 * データ検索
	 * @param tablename	テーブル名
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @param timeParam 時間パラメータ
	 * @return 結果リスト
	 */
	public List<BeanMap> selectAll(String tablename, String asc, String col, Date[] timeParam)
	{
		return this.selectAll(tablename, asc, col, null, 0, 0, timeParam);
	}
	public List<BeanMap> selectAll(String tablename, String asc, String col, String cast, int offset, int limit, Date[] timeParam)
	{
		LayerInfo layerInfo = getLayerInfo(tablename);

		String orderby = StringUtil.isNotEmpty(col) ? " order by "+col+" "+asc : "";
		if (StringUtil.isNotEmpty(cast))
			orderby = "order by cast(case when length("+col+") =0 then null else "+col+" end as "+cast+") "+asc;

		String offlim = (offset > 0) ? " offset "+offset : "" ;
		offlim += (limit > 0) ? " limit "+limit : "";

		// 通常のテーブルの場合
		if(layerInfo==null || TimeSeriesType.NONE.equals(layerInfo.timeSeriesType)) {
			return jdbcManager.selectBySql(BeanMap.class, "select * from \""+tablename+"\""+orderby+offlim).getResultList();
		}
		// 履歴レイヤの場合
		else if(TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			if(timeParam==null) timeParam = new Date[]{new Date()};
			String timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			return jdbcManager.selectBySql(BeanMap.class, "select * from \""+tablename+"\" where true "+timeQuery+orderby+offlim).getResultList();
		}
		return null;
	}

	/**
	 * データ検索
	 * @param tablename	テーブル名
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @return 結果リスト
	 */
	public List<BeanMap> selectAll(String tablename, String asc, String col, int offset, int limit, Date[] timeParam)
	{
		return selectAll(tablename, asc, col, null, offset, limit, timeParam);
	}

	/**
	 * データ検索
	 * @param tablename	テーブル名
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @return
	 */
	public List<BeanMap> selectAllSortCast(String tablename, String asc, String col, String cast, Date[] timeParam)
	{
		return selectAll(tablename, asc, col, cast, 0, 0, timeParam);
	}

	/**
	 * データ検索
	 * @param tablename	テーブル名
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @return
	 */
	public List<BeanMap> selectAllSortCast(String tablename, String asc, String col, String cast, int offset, int limit, Date[] timeParam)
	{
		return selectAll(tablename, asc, col, cast, offset, limit, timeParam);
	}

	/**
	 * trackdataidでデータ検索
	 * @param tablename	テーブル名
	 * @param trackdataid	trackdataid
	 * @return 結果リスト
	 */
	public List<BeanMap> selectByTrackdataid(String tablename, long trackdataid)
	{
		return jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename +" WHERE trackdataid = "+ trackdataid).getResultList();
	}

	/**
	 * 指定のidでデータ検索
	 * @param tablename	テーブル名
	 * @param idname ID列名
	 * @param id	id
	 * @return 結果リスト
	 */
	public List<BeanMap> selectById(String tablename, String idname, long id)
	{
		return selectById(tablename, idname, id, null, null, null);
	}

	/**
	 * 指定のidでデータ検索
	 * @param tablename	テーブル名
	 * @param id	id
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @return
	 */
	public List<BeanMap> selectById(String tablename, String idname, long id, String asc, String col)
	{
		return selectById(tablename, idname, id, asc, col, null);
	}

	/**
	 * 指定のidでデータ検索
	 * @param tablename	テーブル名
	 * @param idname ID列名
	 * @param id	id
	 * @return 結果リスト
	 */
	public List<BeanMap> selectById(String tablename, String idname, long id, String asc, String col, Date[] timeParam)
	{
		LayerInfo layerInfo = getLayerInfo(tablename);

		String orderby = StringUtil.isNotEmpty(col) ? " order by "+col+" "+asc : "";

		// 通常のテーブルの場合
		if(layerInfo==null || TimeSeriesType.NONE.equals(layerInfo.timeSeriesType)) {
			return jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename +" WHERE "+idname+" = "+ id).getResultList();
		}
		// 履歴レイヤの場合
		else if(TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			if(timeParam==null) timeParam = new Date[]{new Date()};
			String timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			List<BeanMap> result = jdbcManager.selectBySql(BeanMap.class, "select * from \""+tablename+"\" where "+idname+" = "+ id +timeQuery+orderby).getResultList();
			for(BeanMap bean : result) {
				// time_from/to は UTC時刻で格納されているので調整
				Timestamp timeFrom = (Timestamp)bean.get("timeFrom");
				if(timeFrom!=null) bean.put("timeFrom", TimeUtil.newTimestampWithOffset(timeFrom.getTime()));
				Timestamp timeTo = (Timestamp)bean.get("timeTo");
				if(timeTo!=null) bean.put("timeTo", TimeUtil.newTimestampWithOffset(timeTo.getTime()));
			}
			return result;
		}
		return null;
	}

	/**
	 * 指定のidでデータ検索
	 * @param tablename	テーブル名
	 * @param id	id
	 * @return
	 */
	public List<BeanMap> selectByIds(String tablename, String idname, List<Long> idary, String asc, String col, String cast, int offset, int limit, Date[] timeParam)
	{
		if (idary.size() == 0)
			return new ArrayList<BeanMap>();
		String ids = getIds(idary);
		LayerInfo layerInfo = getLayerInfo(tablename);

		String orderby = StringUtil.isNotEmpty(col) ? " order by "+col+" "+asc : "";
		if (StringUtil.isNotEmpty(cast))
			orderby = " order by cast(case when length("+col+") =0 then null else "+col+" end as "+cast+")";

		String cols = null;
		if (cols == null) cols = "*";
		String offlim = (limit > 0) ? " offset "+offset+" limit "+limit : "";

		// 通常のテーブルの場合
		if(layerInfo==null || TimeSeriesType.NONE.equals(layerInfo.timeSeriesType)) {
			return jdbcManager.selectBySql(BeanMap.class, "select "+cols+" from "+tablename +" WHERE "+idname+" in ("+ ids +")"+orderby+offlim).getResultList();
		}
		// 履歴レイヤの場合
		else if(TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			if(timeParam==null) timeParam = new Date[]{new Date()};
			if (cols.indexOf("time_from")==-1)
				cols += ",time_from,time_to";

			String timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			List<BeanMap> result = jdbcManager.selectBySql(BeanMap.class, "select "+cols+" from \""+tablename+"\" where "+idname+" in ("+ ids +")" +timeQuery+orderby+offlim).getResultList();
			for(BeanMap bean : result) {
				// time_from/to は UTC時刻で格納されているので調整
				Timestamp timeFrom = (Timestamp)bean.get("timeFrom");
				if(timeFrom!=null) bean.put("timeFrom", TimeUtil.newTimestampWithOffset(timeFrom.getTime()));
				Timestamp timeTo = (Timestamp)bean.get("timeTo");
				if(timeTo!=null) bean.put("timeTo", TimeUtil.newTimestampWithOffset(timeTo.getTime()));
			}
			return result;
		}
		return null;
		//return jdbcManager.selectBySql(BeanMap.class, "select "+cols+" from "+tablename +" WHERE "+idname+" in ("+ ids +")  order by "+col+" "+asc).offset(offset).limit(limit).getResultList();
	}

	/**
	 * 指定のidでデータ検索
	 * @param tablename	テーブル名
	 * @param id	id
	 * @return
	 */
	public List<BeanMap> selectByIdsSortCast(String tablename, String idname, List<Long> idary, String asc, String col, String cast, int offset, int limit, Date[] timeParam)
	{
		return selectByIds(tablename, idname, idary, asc, col, cast, offset, limit, timeParam);
	}

	/**
	 * idのCSVを返す
	 * @param idary
	 * @return
	 */
	public String getIds(List<Long> idary) {
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < idary.size(); i++){
			if (i == 0) ids.append("'");
			if (i > 0) ids.append("\',\'");
			ids.append(idary.get(i));
			if (i == idary.size()-1) ids.append("'");
		}
		if (idary.size() == 0)
			ids.append(-1);
		return ids.toString();
	}

	/**
	 * trackdataidでデータ検索
	 * @param tablename	テーブル名
	 * @param trackdataid	trackdataid
	 * @return 結果リスト
	 */
	public List<BeanMap> selectByTrackdataid(String tablename, long trackdataid, String asc, String col)
	{
		return jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename +" WHERE trackdataid = "+ trackdataid+" order by "+col+" "+asc).getResultList();
	}

	/**
	 * 非eコミマップテーブルから、trackdataidでデータ検索（グルーピング指定あり）
	 * @param tablename	テーブル名
	 * @param trackdataid	trackdataid
	 * @param idCol		idのカラム名
	 * @param groupingIds	グルーピング条件に適合したレコードのidの配列
	 * @return 結果リスト
	 */
	public List<BeanMap> selectByTrackdataidGrouping(String tablename, long trackdataid, String idCol, List<Long> groupingIds)
	{
		//グルーピングの条件指定のWHERE文を組み立てる
		String groupingSQL = "";
		if(groupingIds.size()>0){
			groupingSQL += " AND (";
			int cnt = 0;
			for(long fid : groupingIds){
				if(cnt>0)groupingSQL += " OR ";
				groupingSQL += idCol + " = "+ fid;
				cnt++;
			}
			groupingSQL += ")";
		}

		//検索
		if(groupingSQL.length()>0){
			return jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename +" WHERE trackdataid = "+ trackdataid + groupingSQL).getResultList();
		}else{
			return jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename +" WHERE trackdataid = "+ trackdataid).getResultList();
		}

	}

	/**
	 * グルーピング指定でデータ検索
	 * @param tablename	テーブル名
	 * @param idCol		idのカラム名
	 * @param groupingIds	グルーピング条件に適合したレコードのidの配列
	 * @param timeQuery 時間クエリ
	 * @return
	 */
	public List<BeanMap> selectByGrouping(String tablename, String idCol, List<Long> groupingIds, String timeQuery)
	{
		//返り値用List
		ArrayList<BeanMap> retList = new ArrayList<BeanMap>();
		if(groupingIds.size()>0){
			for(long fid : groupingIds){
				BeanMap retMap = jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename +" WHERE " + idCol + " = "+ fid + timeQuery).getSingleResult();
				if(retMap!=null)retList.add(retMap);
			}
			if(retList.size()>0){
				return retList;
			}else{
				return null;
			}
		}else{
			return jdbcManager.selectBySql(BeanMap.class, "select * from "+tablename+" WHERE true "+timeQuery).getResultList();
		}
	}

	/**
	 * 指定したEntityのテーブルから、指定したIDのレコードを検索
	 * @param entityClass	検索するEntityのクラス
	 * @param tablename	検索するテーブル
	 * @param id			検索するID
	 * @return				検索結果のEntityのObject
	 */
	public Object selectById(Class<?> entityClass, String tablename, long id)
	{
		return jdbcManager.selectBySql(entityClass, "select * from "+tablename+ " WHERE id = "+id).getSingleResult();
	}

	/**
	 * 指定テーブルの指定属性のDistinctを取って返す。
	 * @param tablename
	 * @param attrname
	 * @return 文字列のリスト
	 */
	public List<String> selectDistinct(String tablename, String attrname)
	{
		try {
			return jdbcManager.selectBySql(String.class, "select distinct(\""+attrname+"\") from \""+tablename+"\"").getResultList();
		} catch (Exception e) {
			logger.error("selectDistinct", e);
			throw new ServiceException(lang.__("Check settings. Table:")+tablename+" " + lang.__("Item:")+attrname, e);
		}
	}

	/**
	 * 指定テーブルの指定属性のDistinctを取って返す。
	 * @param tablename
	 * @param attrname
	 * @param timeParam 時間指定
	 * @return 文字列のリスト
	 */
	public List<String> selectDistinct(String tablename, String attrname, Date[] timeParam)
	{
		try {
			String timeQuery = "";

			// 履歴レイヤの場合
			LayerInfo layerInfo = getLayerInfo(tablename);
			if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
				if(timeParam==null) timeParam = new Date[]{new Date()};
				timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			}

			return jdbcManager.selectBySql(String.class, "select distinct(\""+attrname+"\") from \""+tablename+"\" where true "+timeQuery).getResultList();
		} catch (Exception e) {
			logger.error("selectDistinct", e);
			throw new ServiceException(lang.__("Check settings. Table:")+tablename+" " + lang.__("Item:")+attrname, e);
		}
	}

	/**
	 * 指定テーブルの指定属性のDistinctを取って返す。(trackdataidによる絞り込みつき)
	 * @param tablename
	 * @param attrname
	 * @return 文字列のリスト
	 */
	public List<String> selectDistinctByTrackdataid(String tablename, String attrname, long trackdataid)
	{
		try {
			return jdbcManager.selectBySql(String.class, "select distinct(\""+attrname+"\") from \""+tablename+"\" WHERE trackdataid = "+trackdataid).getResultList();
		} catch (Exception e) {
			logger.error("selectDistinct", e);
			throw new ServiceException(lang.__("Check settings. Table:")+tablename+" " + lang.__("Item:")+attrname, e);
		}
	}

	/**
	 * 指定テーブルの time_from の一覧を取得
	 * @param tablename
	 * @return 指定テーブルの time_from の一覧
	 */
	public List<Timestamp> selectDistinctByTimeFrom(String tablename) {
		try {
			return jdbcManager.selectBySql(Timestamp.class, "select distinct(time_from) from \""+tablename+"\" order by time_from").getResultList();
		} catch (Exception e) {
			logger.error("selectDistinctByTimeFrom", e);
			return new ArrayList<>();
		}
	}

	/**
	 * eコミマップのテーブルの、指定されたgid、カラムの値を取得して返す
	 * 通常のテーブルだと、返り値の型がわからないので、eコミマップのテーブル以外には使わないこと！
	 * @param tblName	eコミマップのテーブル名
	 * @param colName	eコミマップのカラム名
	 * @param id		eコミマップのgid
	 * @return			値
	 */
	public String getEcomColValue(String tblName, String colName, String id){
		return jdbcManager.selectBySql(String.class, "select " + colName +" from "+ tblName + " WHERE gid = " + id).getSingleResult();
	}

	/**
	 * 既存レコードのアップデート
	 * @param tablename	アップデートするテーブル
	 * @param attrid		アップデートするカラム
	 * @param key			絞込みに使うカラム
	 * @param id			絞込みに使うカラムの値
	 * @param value		アップデートする値
	 */
	public void update(String tablename, String attrid, String key, Long id, String value)
	{
		jdbcManager.updateBySql("update "+tablename+" set "+attrid+"=? where "+key+"=?", String.class, Long.class).params(value, id).execute();
	}

	/**
	 * 既存レコードのアップデート (一括編集用)
	 * @param tablename	アップデートするテーブル
	 * @param attrid		アップデートするカラム
	 * @param key			絞込みに使うカラム
	 * @param id			絞込みに使うカラムの値
	 * @param value		アップデートする値
	 * @param addable	一括追記フラグ
	 */
	public void update(String tablename, String attrid, String key, Long id, String value, Boolean addable)
	{
		if(addable){
			jdbcManager.updateBySql("update "+tablename+"set "+attrid+"="+attrid+"||? where "+key+"=?", String.class, Long.class).params(value, id).execute();
		}else{
			jdbcManager.updateBySql("update "+tablename+" set "+attrid+"=? where "+key+"=?", String.class, Long.class).params(value, id).execute();
		}
	}

	/**
	 * 既存レコードジオメトリのアップデート
	 * @param tablename
	 * @param attrid
	 * @param key
	 * @param id
	 * @param value
	 */
	public void updateGeometry(String tablename, String attrid, String key, Long id, String value)
	{
		String sql = "update "+tablename+" set "+attrid+" = '"+value+"' where "+key+"=?";
		jdbcManager.updateBySql(sql, Long.class).params(id).execute();
	}

	/**
	 * 既存レコードのアップデート（一括編集用）
	 * @param tablename	アップデートするテーブル
	 * @param attrid		アップデートするカラム
	 * @param value		アップデートする値
	 */
	public void updateAll(String tablename, String attrid, String value)
	{
		Map<String, String> updateValues = new HashMap<String, String>();
		updateValues.put(attrid, value);
		updateAll(tablename, updateValues);
	}

	/**
	 * 既存レコードの複数アップデート（一括編集用）
	 * @param tablename アップデートするテーブル
	 * @param updateValues アップデートするカラムと値の Map
	 */
	public void updateAll(String tablename, Map<String, String> updateValues)
	{
		LayerInfo layerInfo = getLayerInfo(tablename);

		// 通常のテーブルの場合
		if(layerInfo==null || TimeSeriesType.NONE.equals(layerInfo.timeSeriesType)) {
			// SQL パラメータを設定
			StringBuffer set = new StringBuffer();
			LinkedList<Object> paramList = new LinkedList<Object>();
			LinkedList<Class<?>> paramClassList = new LinkedList<Class<?>>();
			for(Map.Entry<String, String> entry : updateValues.entrySet()) {
				String column = entry.getKey();
				String value = entry.getValue();
				if(0<set.length()) set.append(",");
				set.append(column+"=?");
				paramClassList.add(String.class);
				paramList.add(value);
			}

			// build sql
			StringBuffer sql = new StringBuffer("/* jp.ecom_plat.saigaitask.service.TableService.updateAll case TimeSeriesType.NONE */ ");
			sql.append("UPDATE "+tablename+" SET "+set);

			// execute
			Class<?>[] paramClasses = paramClassList.toArray(new Class[paramClassList.size()]);
			Object[] params = paramList.toArray(new Object[paramList.size()]);
			jdbcManager.updateBySql(sql.toString(), paramClasses).params(params).execute();
		}
		// 履歴レイヤの場合は time_to=infinity を一括変更 ※時間は戻らない前提
		else if(TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			String layerId = layerInfo.layerId;
			Timestamp updatetime = new Timestamp(System.currentTimeMillis());
			Timestamp updatetimeBefore1ms = new Timestamp(updatetime.getTime()-1L);

			/*
			// 通常の属性しか更新させない
			if(layerInfo.getAttrInfo(attrid)==null) {
				logger.error("TableService.updateAll: unknown attrId=\""+attrid+"\""+layerInfo.toString());
				return;
			}
			*/

			// 全カラムをコピー対象とする
			List<String> copyColumns = new ArrayList<String>();
			{
				List<BeanMap> cols = getCols(layerId);
				for(BeanMap col : cols) {
					String attname = (String) col.get("attname");
					copyColumns.add(attname);
				}
			}

			// SQL パラメータを設定
			List<String> colList = new ArrayList<String>(copyColumns);
			List<String> valList = new ArrayList<String>(copyColumns);
			LinkedList<Object> paramList = new LinkedList<Object>();
			LinkedList<Class<?>> paramClassList = new LinkedList<Class<?>>();
			// 値をセットしなおす場合は一旦削除して、最後に追加することで位置がずれないようにする
			{
				// gid は連番ふりなおし
				colList.remove("gid"); valList.remove("gid");
				colList.add("gid");
				valList.add("nextval('"+layerId+"_gid_seq')");

				// time_from は現在時刻
				colList.remove("time_from"); valList.remove("time_from");
				colList.add("time_from");
				valList.add("?");
				paramClassList.add(Timestamp.class);
				paramList.add(updatetime);

				// 指定属性の更新
				for(Map.Entry<String, String> entry : updateValues.entrySet()) {
					String column = entry.getKey();
					String value = entry.getValue();
					colList.remove(column); valList.remove(column);
					colList.add(column);
					valList.add("?");
					paramClassList.add(String.class);
					paramList.add(value);
				}
			}

			// 先に time_to=infinity を -1ms に更新しておく
			{
				StringBuffer sql = new StringBuffer();
				sql.append("UPDATE "+layerId+" SET time_to=? WHERE time_to='infinity';");
				jdbcManager.updateBySql(sql.toString(), Timestamp.class).params(updatetimeBefore1ms).execute();
			}

			// build sql
			String cols = StringUtils.join(colList, ",");
			String values = StringUtils.join(valList, ",");
			StringBuffer sql = new StringBuffer("/* jp.ecom_plat.saigaitask.service.TableService.updateAll case TimeSeriesType.HISTORY */ ");
			sql.append("WITH latest AS (SELECT * FROM "+layerId+" WHERE time_to=?) ");
			sql.append("INSERT INTO "+layerId+"("+cols+") ");
			sql.append("SELECT "+values+" FROM latest;");

			// time_to の条件追加
			paramClassList.addFirst(Timestamp.class);
			paramList.addFirst(updatetimeBefore1ms);

			// execute
			Class<?>[] paramClasses = paramClassList.toArray(new Class[paramClassList.size()]);
			Object[] params = paramList.toArray(new Object[paramList.size()]);
			jdbcManager.updateBySql(sql.toString(), paramClasses).params(params).execute();
		}
	}

	/**
	 * eコミマップテーブルの既存レコードのアップデート（一括編集用、グルーピング指定あり）
	 * @param tablename	アップデートするテーブル
	 * @param attrid		アップデートするカラム
	 * @param value		アップデートする値
	 * @param idCol		idのカラム名
	 * @param groupingIds	グルーピング条件に適合したレコードのidの配列
	 * @param addable	一括追記の可フラグ
	 */
	public void updateAllGrouping(String tablename, String attrid, String value, String idCol, List<Long> groupingIds, Boolean addable)
	{
		//グルーピングの条件指定のWHERE文を組み立てる
		String groupingSQL = "";
		if(groupingIds.size()>0){
			groupingSQL += " WHERE ";
			int cnt = 0;
			for(long fid : groupingIds){
				if(cnt>0)groupingSQL += " OR ";
				//取り敢えずシングルクォートで囲んでおけば、どんなカラム型でもOKのはず
				groupingSQL += idCol + " = "+ fid;
				cnt++;
			}
		}
		//更新
		if(groupingSQL.length()>0){
			if(addable){
				jdbcManager.updateBySql("update "+tablename+" set "+attrid+"="+attrid+"||?"+ groupingSQL, String.class).params(value).execute();
			}else{
				jdbcManager.updateBySql("update "+tablename+" set "+attrid+"=? "+ groupingSQL, String.class).params(value).execute();
			}
		}else{
			if(addable){
				jdbcManager.updateBySql("update "+tablename+" set "+attrid+"="+attrid+"||?", String.class).params(value).execute();
			}else{
				jdbcManager.updateBySql("update "+tablename+" set "+attrid+"=?", String.class).params(value).execute();
			}
		}
	}

	/**
	 * 指定したテーブルの、idのレコードが、グルーピング条件に適合するかを判別
	 * @param groupingCols	グルーピング条件となる「カラム名:値」のStringのList
	 * @param tblName		テーブル名
	 * @param idCol		idのカラム名
	 * @param id			id
	 * @return
	 */
	/*
	public boolean checkGroupingData(List<String> groupingCols, String tblName, String idCol, Long id){
		String groupingSQL = "";
		for(long fid : groupingIds){
			groupingSQL += " AND ";
			//カラム名と値を区切る、値が時間の時は文字列に「:」が含まれるので、indexOfで分割
			String col = data.substring(0, data.indexOf(":"));
			String val = data.substring(data.indexOf(":")+1);
			//取り敢えずシングルクォートで囲んでおけば、どんなカラム型でもOKのはず
			groupingSQL += col + " = '"+ val +"'";
		}

		BeanMap res = jdbcManager.selectBySql(BeanMap.class,"SELECT * FROM "+tblName+" WHERE "+idCol+" = "+id + groupingSQL).getSingleResult();
		if(res==null){
			return false;
		}else{
			return true;
		}
	}
	*/

	/**
	 * 指定したテーブルの、指定したカラムのidのレコードが、グルーピング条件に適合するもののidを取得
	 * @param tblName		グルーピング対象のテーブル名
	 * @param groupingCols	グルーピング条件となる「カラム名:値」のStringのList
	 * @param idCol		idのカラム名
	 * @param timeQuery 時間クエリ
	 * @return
	 */
	public List<Long> getGroupingDataIds(String tblName, List<String> groupingCols, String idCol, String timeQuery){

		if(groupingCols.size()>0){
			String groupingSQL = "";
			for(String data : groupingCols){
				if(groupingSQL.length()>0)groupingSQL += " AND ";
				//カラム名と値を区切る、値が時間の時は文字列に「:」が含まれるので、indexOfで分割
				String col = data.substring(0, data.indexOf(":"));
				String val = data.substring(data.indexOf(":")+1);
				//取り敢えずシングルクォートで囲んでおけば、どんなカラム型でもOKのはず
				if (StringUtil.isNotEmpty(val))
					groupingSQL += col + " = '"+ val +"'";
				else
					groupingSQL += " (" + col + " = '"+ val +"' OR " + col + " is null) ";
			}
			return jdbcManager.selectBySql(Long.class,"SELECT " + idCol +" FROM "+tblName+" WHERE "+ groupingSQL+ timeQuery).getResultList();
		}else{
			//グルーピング条件が無い場合は、グルーピング条件に適合するレコード無し空の配列を返す
			return new ArrayList<Long>();
		}
	}

	/**
	 * 既存レコードのアップデート（Entity指定）
	 * @param entity
	 */
	public void updateByEntity(Object entity)
	{
		jdbcManager.update(entity).execute();
	}

	/**
	 * Entityを指定してINSERT
	 * @param entity	値をセットしたEntity
	 */
	public void insertByEntity(Object entity)
	{
		jdbcManager.insert(entity).execute();
	}

	/**
	 * Entityを指定してDELETE
	 * @param entity	値をセットしたEntity
	 */
	public void deleteByEntity(Object entity)
	{
		jdbcManager.delete(entity).execute();
	}

	/**
	 * テーブルの存在確認
	 * @param tblName	テーブル名
	 * @return　引数のテーブルが存在していればtrue、なければfalse
	 */
	public boolean isExists(String tblName){
		return isExists("public", tblName);
	}

	/**
	 * テーブルの存在確認
	 * @param schema スキーマ
	 * @param tblName	テーブル名
	 * @return　引数のテーブルが存在していればtrue、なければfalse
	 */
	public boolean isExists(String schema, String tblName){
		String sql = "SELECT relname FROM pg_class cls"
				+" INNER JOIN pg_namespace nsp ON (cls.relnamespace=nsp.oid)"
				+" WHERE relkind = 'r' AND relname = '"+tblName+"'"+" AND nspname='"+schema+"';";
		BeanMap res = jdbcManager.selectBySql(BeanMap.class,sql).getSingleResult();
		if(res==null){
			return false;
		}else{
			return true;
		}

	}

	/**
	 * テーブルとその項目の存在確認
	 * @param tblName	テーブル名
	 * @param colname 項目名
	 * @return　引数のテーブルが存在していればtrue、なければfalse
	 */
	public boolean isExistsColumn(String tblName, String colname){
		BeanMap res = jdbcManager.selectBySql(BeanMap.class,"select column_name as attname from information_schema.columns where table_name =? and column_name=?", tblName, colname).getSingleResult();
		if(res==null){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 指定したテーブルの、指定したカラムの型を取得
	 * @param tblName	テーブル名
	 * @param colName	カラム名
	 * @return DBで定義されている型名を文字列で
	 */
	public String getAttrType(String tblName, String colName){
		BeanMap res = jdbcManager.selectBySql(BeanMap.class,
			"select typname from pg_class, pg_attribute, pg_type WHERE relkind ='r' AND relname = '"+tblName+"'"
			+" AND attrelid = (select relid from pg_stat_all_tables where relname = '"+tblName+"')"
			+" AND attnum > 0 AND pg_type.oid = atttypid AND attname='"+colName+"';").getSingleResult();
		if(res != null){
			return (String)res.get("typname");
		}else{
			return null;
		}
	}

	/**
	 * 指定したテーブルのカラム名一覧を取得
	 * @param tblName	テーブル名
	 * @return	カラム名を格納したBeanMapのList
	 */
	public List<BeanMap> getCols(String tblName){
		return jdbcManager.selectBySql(BeanMap.class,
			"select attname from pg_class, pg_attribute, pg_type WHERE relkind ='r' AND relname = '"+tblName+"'"
			+" AND attrelid = (select relid from pg_stat_all_tables where relname = '"+tblName+"')"
			+" AND attnum > 0 AND pg_type.oid = atttypid;").getResultList();
	}

	/**
	 * master「○○○_master」もしくは「○○○_info」テーブルのnameの値をSelectの選択肢にセットする
	 * @param type			masterかinfoかを文字列で指定
	 * @param colName		「○○○id」もしくは「○○○infoid」を持っているカラムの名前
	 * @param selectStr	Selectの表示文字列をセットするMap
	 * @param selectVal	Selectの値をセットするMap
	 * @return				値をkey、表示文字列をvalueにしたMap
	 */
	public Map<String, String> setRelTable(String type, String colName, Map<String, String[]> selectStr, Map<String, String[]> selectVal){

		//返り値用map定義
		Map<String, String> valMap = new HashMap<String, String>();

		String tbl = "";
		//master
		if(type.equals("master")){
			int idx = colName.lastIndexOf("id");
			tbl = colName.substring(0,idx)+"_master";
		//info
		}else if(type.equals("info")){
			tbl = colName.substring(0,colName.length()-6)+"_info";
		}
		//テーブルが存在してれば処理続行
		if(isExists(tbl)){
			List<BeanMap> resList = selectAll(tbl);
			if (resList.size() > 0) {
				ArrayList<String> selStrArr  = new ArrayList<String>();
				ArrayList<String> selValArr  = new ArrayList<String>();
				int mapIdx = 0;
				//ソートする。
				if (resList.size() > 0) {
					BeanMap map = resList.get(0);
					if (map.containsKey("disporder")) {
						Collections.sort(resList, new Comparator<BeanMap>() {
							@Override
							public int compare(BeanMap m1, BeanMap m2) {
								Integer d1 = (Integer)m1.get("disporder");
								Integer d2 = (Integer)m2.get("disporder");
								int i1 = 0, i2 = 0;
								if (d1 != null) i1 = d1.intValue();
								if (d2 != null) i2 = d2.intValue();
								return (i1 > i2 ? 1 : (i1 < i2 ? -1 : 0));
							}
						});
					}
				}
				for(BeanMap map : resList){
					if (map.containsKey("localgovinfoid")) {
						Long val = (Long)map.get("localgovinfoid");
						if (val != null && !val.equals(loginDataDto.getLocalgovinfoid()))
							continue;
					}
					if(map.containsKey("name")){
						selStrArr.add((String)map.get("name"));
					}else{
						logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
						logger.error("[name] column does NOT exist in ["+tbl+"] table...");
						throw new ServiceException("[name] column does NOT exist in ["+tbl+"] table...");
					}
					if(map.containsKey("id")){
						selValArr.add(String.valueOf(map.get("id")));
					}else{
						logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
						logger.error("[id] column does NOT exist in ["+tbl+"] table...");
						throw new ServiceException("[id] column does NOT exist in ["+tbl+"] table...");
					}

					valMap.put(selValArr.get(mapIdx), selStrArr.get(mapIdx));

					mapIdx++;
				}
				selectStr.put(colName, selStrArr.toArray(new String[selStrArr.size()]));
				selectVal.put(colName, selValArr.toArray(new String[selValArr.size()]));

				return valMap;
			}
			return null;
		}else{
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("master table [ "+tbl+"] is NOT exist...");
			throw new ServiceException("master table ["+tbl+"] is NOT exist...");
		}
	}

	/**
	 * eコミマップデータの最終更新日を取得する
	 * @param tablename
	 * @param attrname
	 * @param timeParam 時間パラメータ
	 * @return 最終更新日
	 */
	public Timestamp getEcomDataLastUpdateTime(String tablename, String attrname, Date[] timeParam)
	{
		try {
			LayerInfo layerInfo = getLayerInfo(tablename);

			// 履歴レイヤの場合
			String timeQuery = "";
			if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
				if(timeParam!=null) timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			}
			String sql = "select \""+attrname+"\" from \""+tablename+"\" where \""+attrname+"\" is not null and \""+attrname+"\" != '' "+timeQuery+" order by \""+attrname+"\" desc";
			String timestm = jdbcManager.selectBySql(String.class, sql).limit(1).getSingleResult();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			//年の区切りが「-」の場合は「/」に置換する
			if (timestm != null) {
				timestm = timestm.replaceAll("-", "/");
				try {
					Date tm = sdf.parse(timestm);
					return new Timestamp(tm.getTime());
				} catch (ParseException e) {
					logger.error("getLastUpdateTime", e);
				}
			}
			return null;
		} catch (Exception e) {
			logger.error("getLastUpdateTime", e);
			throw new ServiceException(lang.__("Check settings of last update"), e);
		}
	}

	/**
	 * eコミマップデータの最終更新日を取得する
	 * @param tablename
	 * @param attrname
	 * @param trackdataid
	 * @return 最終更新日
	 */
	public Timestamp getSystemDataLastUpdateTime(String tablename, String attrname, Long trackdataid)
	{
		try {
			return jdbcManager.selectBySql(Timestamp.class, "select \""+attrname+"\" from \""+tablename+"\" where trackdataid = "+trackdataid+" and \""+attrname+"\" is not null order by \""+attrname+"\" desc").limit(1).getSingleResult();
		} catch (Exception e) {
			logger.error("getLastUpdateTime", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * テーブルの内容のコピー
	 * @param srctable
	 * @param totable
	 */
	public void copyData(String srctable, String totable)
	{
		logger.info("copy from " + srctable + " to " + totable);
		List<BeanMap> srcCols = getCols(srctable);
		if (srcCols == null || srcCols.size() == 0) return;
		List<BeanMap> dstCols = getCols(totable);
		if (dstCols == null || dstCols.size() == 0) return;

		String sql = "insert into \""+totable+"\" (";
		StringBuffer col = new StringBuffer();
		for (BeanMap map : srcCols) {
			// totable にあるものだけコピーする
			if(dstCols.contains(map)) {
				Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator();
				while (it.hasNext()) {
					if (col.length() > 0) col.append(",");
					col.append("\"");
					col.append(it.next().getValue());
					col.append("\"");
				}
			}
		}
		sql += col;
		sql += ") (select ";
		sql += col;
		sql += " from \""+srctable+"\");";

		logger.debug("copyData sql: " + sql);

		jdbcManager.callBySql(sql).execute();
	}

	/**
	 * テーブルの内容のコピー
	 * @param srctable
	 * @param totable
	 * @param resetTimefrom time_fromリセット値
	 */
	public void copyLatestData(String srctable, String totable, Timestamp resetTimefrom)
	{
		logger.info("copy from " + srctable + " to " + totable);
		List<BeanMap> srcCols = getCols(srctable);
		if (srcCols == null || srcCols.size() == 0) return;
		List<BeanMap> dstCols = getCols(totable);
		if (dstCols == null || dstCols.size() == 0) return;

		String sql = "insert into \""+totable+"\" (";
		StringBuffer col = new StringBuffer();
		for (BeanMap map : srcCols) {
			// totable にあるものだけコピーする
			if(dstCols.contains(map)) {
				Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator();
				while (it.hasNext()) {
					if (col.length() > 0) col.append(",");
					col.append("\"");
					col.append(it.next().getValue());
					col.append("\"");
				}
			}
		}
		sql += col;
		sql += ") (select ";
		sql += col;
		sql += " from \""+srctable+"\" where time_to='infinity'::timestamp);";

		logger.debug("copyData sql: " + sql);

		jdbcManager.callBySql(sql).execute();

		// reset sequence
		setSequence(totable, "gid", totable+"_gid_seq");

		// 最新の地物しかないので _orgid を gid に一括更新
		sql = "UPDATE \""+totable+"\" SET _orgid=gid";
		jdbcManager.callBySql(sql).execute();

		// reset time_from
		sql = "UPDATE \""+totable+"\" SET time_from=?";
		jdbcManager.callBySql(sql, resetTimefrom).execute();
	}

	/**
	 * シーケンスの設定
	 *
	 * @param table テーブル名
	 * @param id ID名
	 * @param seqtable シーケンス名
	 */
	public void setSequence(String table, String id, String seqtable)
	{
		String sql = "select "+id+" from "+table+" order by "+id+" desc limit 1";
		Integer gid = jdbcManager.selectBySql(Integer.class, sql).getSingleResult();
		if (gid != null) {
			sql = "select setval('"+seqtable+"', "+gid+")";
			jdbcManager.callBySql(sql).execute();
		}
	}

	/**
	 * シーケンスリセットしたトランザクションでエラーによりロールバックされたときのために、
	 * シーケンスをリセットしたテーブル名を保存しておく.
	 */
	private List<String> resetSequenceTableNames = new ArrayList<String>();

	/**
	 * シーケンスを現在の最大値にリセットする.
	 * レコードがなくて最大値がとれない場合は、1にリセットする.
	 * @param tableName テーブル名
	 * @return リセットされたシーケンス値
	 */
	public Long resetMaxSequence(String tableName)
	{
		// リセットするシーケンス値を決定
		Long resetSeq = 1L;
		Long max = jdbcManager.selectBySql(Long.class, "SELECT max(id) FROM "+tableName).getSingleResult();
		if(max!=null) {
			resetSeq = max+1;
		}

		// シーケンス値をリセット
		String sequenceName = tableName+"_id_seq";
		boolean isCalled = false; // false: セットした値から使う, true: 次の値から使う
		Long result = jdbcManager
				//.updateBySql("SELECT setval(?, ?, ?)", String.class, Long.class, Boolean.class)
				.selectBySql(Long.class, "SELECT setval(?, ?, ?)", sequenceName, resetSeq, isCalled)
				.getSingleResult();

		// 結果のチェック
		if(!resetSeq.equals(result)) {
			throw new ServiceException(lang.__("Unable to set ID sequence \"{1}\" of {0}.", tableName, sequenceName));
		}
		// 未追加なら追加する
		if(!resetSequenceTableNames.contains(tableName)) {
			resetSequenceTableNames.add(tableName);
		}

		return resetSeq;
	}

	/**
	 * リセットされたシーケンスを再度リセットする
	 * @return リセットされたシーケンス値
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public LinkedHashMap<String, Long> rollbackResetMaxSequenceAll()
	{
		LinkedHashMap<String, Long> result = new LinkedHashMap<String, Long>();
		for(String tableName : resetSequenceTableNames) {
			result.put(tableName, resetMaxSequence(tableName));
		}
		return result;
	}

	/** 依存関係クラス */
	public static class Dependency {
		/** 依存元テーブル名 */
		public String referenceFromTableName;
		/** 依存先テーブル名 */
		public String referenceToTableName;
	}

	public static class Node<T> {

		Logger logger = Logger.getLogger(Node.class);

		private T data;
		private Node<T> parent;
		private List<Node<T>> parents = new ArrayList<Node<T>>();
		private List<Node<T>> children = new ArrayList<Node<T>>();

		public Node(T data) {
			this.data = data;
		}

		public void add(Node<T> child) {
			child.parent = this;
			child.parents.add(this);
			this.children.add(child);
		}

		public void remove() {
			// すべての親から削除
			for(Node<T> p : parents) {
				p.children.remove(this);
				logger.debug("Delete "+this.data+" from parent "+p.data);
			}
		}

		public String getPath() {
			StringBuffer sb = new StringBuffer();
			boolean first = true;
			Node<T> node = this;
			while(node!=null) {
				if(first) first=false;
				else sb.insert(0, " ->");
				sb.insert(0, node.data);
				node = node.parent;
			}
			return sb.toString();
		}
	}

	/** 指定テーブルに依存しているテーブルを検索するSQL(自己参照を除く) */
	public static final String SHOW_DEPENDON_SQL =
			 "WITH RECURSIVE r AS ("
			+"  SELECT * FROM reference_view WHERE reference_to_table_name=?"
			+"  UNION"
			+"  SELECT reference_view.* FROM reference_view, r WHERE reference_view.reference_to_table_name=r.reference_from_table_name"
			+")"
			+"SELECT * FROM r WHERE reference_from_table_name!=reference_to_table_name;";

	/**
	 * 指定テーブルに依存しているテーブルを検索する
	 * @param tableName
	 * @return
	 */
	public List<Dependency> selectDependOn(String tableName) {
		return jdbcManager
				.selectBySql(Dependency.class, SHOW_DEPENDON_SQL, tableName)
				.getResultList();
	}

	/** 指定テーブルに依存しているテーブルを検索するSQL(自己参照を除く) */
	public static final String SHOW_DEPENDEDON_SQL =
			 "WITH RECURSIVE r AS ("
			+"  SELECT * FROM reference_view WHERE reference_from_table_name=?"
			+"  UNION"
			+"  SELECT reference_view.* FROM reference_view, r WHERE reference_view.reference_from_table_name=r.reference_to_table_name"
			+")"
			+"SELECT * FROM r WHERE reference_from_table_name!=reference_to_table_name;";

	/**
	 * 指定テーブルが依存しているテーブルを検索する
	 * @param tablenName
	 * @return
	 */
	public List<Dependency> selectDependedOn(String tableName) {
		return jdbcManager
				.selectBySql(Dependency.class, SHOW_DEPENDEDON_SQL, tableName)
				.getResultList();
	}

	/**
	 * 外部キーで依存しているテーブルごとテーブルを削除する。
	 * @param tableName 削除テーブル名
	 * @param excludes キーにテーブル名がはいっていれば、削除対象から除く
	 * @return 削除したテーブルと削除数のマップ
	 */
	public Map<String, Integer> deleteAllWithDependency(String tableName, Map<String, ?> excludes) {
		TableService tableService = this;

		// 削除したもの
		Map<String, Integer> deleted = new LinkedHashMap<String, Integer>();

		// 依存テーブルを取得し、依存性ツリーを構築する
		List<Dependency> results = selectDependOn(tableName);
		Map<String, Node<String>> nodes = new HashMap<String, Node<String>>();
		Node<String> rootNode = new Node<String>(tableName);
		nodes.put(rootNode.data, rootNode);
		for(Dependency dependency : results) {
			// ノード取得
			Node<String> tonode = nodes.get(dependency.referenceToTableName);
			if(tonode==null) {
				tonode = new Node<String>(dependency.referenceToTableName);
				nodes.put(tonode.data, tonode);
			}
			Node<String> fromnode = nodes.get(dependency.referenceFromTableName);
			if(fromnode==null) {
				fromnode = new Node<String>(dependency.referenceFromTableName);
				nodes.put(fromnode.data, fromnode);
			}

			// 依存関係追加
			tonode.add(fromnode);
		}

		// 依存テーブルがあれば先に削除
		Node<String> node = rootNode;
		List<Node<String>> depth = new ArrayList<Node<String>>();
		depth.add(rootNode);
		while(node!=null) {
			StringBuffer path = new StringBuffer();
			boolean first = true;
			for(Node<String> n : depth) {
				if(first) first=false;
				else path.append(" -> ");
				path.append(n.data);
			}
			logger.debug("Delete "+tableName+" Process: "+path.toString()+"("+node.children.size()+" childs)");

			// 削除除外ノードに来たら、ツリーから除外後、ルートに戻る
			if(excludes.containsKey(node.data)) {
				logger.debug("Delete "+tableName+" Process: "+node.data+" is excluded.");
				// 親ノードに戻る
				node.remove();
				depth.remove(node);
				node = depth.size()==0?null:depth.get(depth.size()-1);
				continue;
			}

			// 葉ノードに来たら削除
			if(node.children.size()==0) {
				logger.debug("Delete "+tableName+" Process: "+node.data+" is deleted.");

				// データの削除実行
				int deletedNum = tableService.deleteAll(node.data);
				deleted.put(node.data, deletedNum);

				// 親ノードに戻る
				node.remove();
				depth.remove(node);
				node = depth.size()==0?null:depth.get(depth.size()-1);
			}
			else {
				// 先頭の子ノードを取得
				node = node.children.get(0);
				depth.add(node);
			}
		}

		return deleted;
	}

	/**
	 * テーブルのデータをすべて削除し、シーケンスをリセットする.
	 * @param tableName 削除テーブル名
	 * @return
	 */
	public int deleteAll(String tableName) {
		try {
			int deleted = jdbcManager.updateBySql("DELETE FROM "+tableName).execute();
			resetMaxSequence(tableName);
			return deleted;
		} catch(Exception e) {
			String dbError = DatabaseUtil.getErrorMessage(e);
			//throw new ServiceException(tableName+(dbError!=null?"が"+dbError+"のため":"を")+"データ削除できませんでした。", e);
			throw new ServiceException(dbError!=null?lang.__("Unable to delete {0} for {1}.", tableName, dbError):lang.__("Unable to delete {0}.", tableName), e);
		}
	}

	/**
	 * テーブルのデータをすべて削除するが、シーケンスはリセットしない.
	 * @param tableName 削除テーブル名
	 * @return
	 */
	public int deleteAllNoResetSequence(String tableName) {
		try {
			int deleted = jdbcManager.updateBySql("DELETE FROM "+tableName).execute();
			return deleted;
		} catch(Exception e) {
			String dbError = DatabaseUtil.getErrorMessage(e);
			//throw new ServiceException(tableName+(dbError!=null?"が"+dbError+"のため":"を")+"データ削除できませんでした。", e);
			throw new ServiceException(dbError!=null?lang.__("Unable to delete {0} for {1}.", tableName, dbError):lang.__("Unable to delete {0}.", tableName), e);
		}
	}

	/**
	 * テーブル名から対応するEntityのクラスを返す
	 * @param tblName	テーブル名
	 * @return			対応するクラスのオブジェクト
	 */
	public Class<?> getEntity(String tblName) {
		URI uri;
		try {
			//ここで指定するクラス名は、検索したいパッケージ内にあるものであれば何でもOK
			uri = LoginData.class.getResource("LoginData.class").toURI();
		} catch (URISyntaxException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
		//ファイル名からクラスファイルを取得して、Listに格納
		File test = new File(uri);
		File dir = test.getParentFile();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for(String path : dir.list()){
			File entry = new File(dir, path);
			if(entry.isFile() && path.endsWith("class")){
				//クラスのバイナリ名が必要なので「.class」を削除
				String className = entry.getName().substring(0,entry.getName().length()-6);
				classes.add(ClassLoaderUtil.loadClass(ResourceUtil.getClassLoader(),Constants.ENTITY_PKG + "." + className));
			}
		}
		//クラスに指定された＠Tableアノテーションに記載されているテーブル名とマッチング
		for(Class<?> clazz : classes){
			Annotation[] annos = clazz.getAnnotations();
			for(Annotation anno : annos){
				if(anno.annotationType().getName().equals("javax.persistence.Table")){
					Map<String, Object> props = AnnotationUtil.getProperties(anno);
					if(props.containsKey("name")){
						if(props.get("name").equals(tblName)){
							return clazz;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 指定したEntityに、指定したカラムの型を自動判別して値をセットする
	 * @param entityObj	値をセットするEntityのObject
	 * @param colName		値をセットするカラム名
	 * @param value		セットする値
	 * @return				値をセットした後のEntityのObject
	 */
	public Object setFieldValue(Class<?> entityClass, Object entityObj, String colName, String value){
		try {
			Field f = ClassUtil.getField(entityClass, colName);
			Class<?> colType = f.getType();
			logger.info("colType : "+colType);

			/** フィールド型に応じてキャスト */
			//Timestamp
			if(colType == (Class<?>)java.sql.Timestamp.class){
				//年月日の区切りが「-」の場合があるので、登録時は「/」に統一
				value = value.replaceAll("-", "/");
				//秒まである場合と無い場合がある
				String[] timeArr = value.split(":");
				Timestamp valTime = null;
				if(timeArr.length>2){
					//さらに秒の小数点まである場合もある
					String[] secArr = timeArr[2].split("\\.");
					if(secArr.length>1){
						if(secArr[1].length()==1){
							valTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S").parse(value).getTime());
						}else if(secArr[1].length()==2){
							valTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SS").parse(value).getTime());
						}else if(secArr[1].length()==3){
							valTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").parse(value).getTime());
						}else{
							logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
							throw new ServiceException("Illegal Timestamp format : "+ value);
						}
					}else{
						valTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(value).getTime());
					}
				}else{
					if (StringUtil.isNotEmpty(value))
						valTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(value).getTime());
				}
				FieldUtil.set(f, entityObj, valTime);
			//Integer
			}else if(colType == (Class<?>)Integer.class){
				if (StringUtil.isNotEmpty(value)) {
					int valInt = Integer.parseInt(value);
					FieldUtil.set(f, entityObj, valInt);
				}
				else
					FieldUtil.set(f, entityObj, null);
			//Long
			}else if(colType == (Class<?>)Long.class){
				if (StringUtil.isNotEmpty(value)) {
					long valLong = Long.parseLong(value);
					FieldUtil.set(f, entityObj, valLong);
				}
				else
					FieldUtil.set(f, entityObj, null);
			//Float
			}else if(colType == (Class<?>)Float.class){
				if (StringUtil.isNotEmpty(value)) {
					float valFloat = Float.parseFloat(value);
					FieldUtil.set(f, entityObj, valFloat);
				}
				else
					FieldUtil.set(f, entityObj, null);
			//Double
			}else if(colType == (Class<?>)Double.class){
				if (StringUtil.isNotEmpty(value)) {
					double valDouble = Double.parseDouble(value);
					FieldUtil.set(f, entityObj, valDouble);
				}
				else
					FieldUtil.set(f, entityObj, null);
			//Boolean
			}else if(colType == (Class<?>)Boolean.class){
				if (StringUtil.isNotEmpty(value)) {
					boolean valBool = value.toLowerCase().equals("true") ? true : false;
					FieldUtil.set(f, entityObj, valBool);
				}
				else
					FieldUtil.set(f, entityObj, null);
			//String、デフォルト
			}else{
				FieldUtil.set(f, entityObj, value);
			}

			return entityObj;

		} catch (IllegalArgumentException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} catch (SecurityException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} catch (ParseException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}

	}


	/**
	 * TablemasterInfoのcoordinatecolumnが指定されているテーブルに、座標情報をセットする。
	 * @param layerId	座標情報を追加するレイヤのID、nullの場合はtrackdataid配下のすべてのレイヤにセット
	 * @param force	true：既に座標情報が登録されていても上書き、false：値がセットされていない場合にのみ座標情報追加
	 * @param mgrs		true:座標値をMGRS形式にする、false：座標値を十進緯度経度にする
	 * @param mgrsdigit MGRS座標の桁数
	 */
	public void setCoordinate(String layerId, boolean force, boolean mgrs, int mgrsdigit){
		//全レコード取得
		List<TablemasterInfo> tmlist = tablemasterInfoService.findAll();

		for(TablemasterInfo tinfo : tmlist){
			// coordinatecolumnに値がセットされているもののみ対象
			if(tinfo.coordinatecolumn!=null && tinfo.coordinatecolumn.length()>0){
				//trackdataidで絞込み
				TracktableInfo ttinfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tinfo.id, loginDataDto.getTrackdataid());
				if(ttinfo!=null){

					try {
						//レイヤ情報を取得
						LayerInfo lInfo = layerService.getLayerInfo(ttinfo.layerid);
						//引数でレイヤIDの指定があれば、そのレイヤのみ対象にする
						if(layerId!=null && !layerId.equals(lInfo.layerId))continue;

						Vector<LayerInfo> lVec = new Vector<LayerInfo>();
						lVec.add(lInfo);
						TrackmapInfo tmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());
						//対象となるレイヤの全フィーチャを取得
						FeatureResultList fList = FeatureDB.searchFeatureBbox(session, tmapInfo.mapid, lVec, null, null, 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false);

						//取得したフィーチャの数だけ繰り返し
						for(int i=0; i<fList.countResult(); i++){
							FeatureResult feature = fList.getResult(i);
							//座標カラムの値を取得、無ければ座標値をセットする
							AttrResult attr = feature.getAttrResult(tinfo.coordinatecolumn);
							if(feature.getWKT()!=null){//位置情報のあるレコードのみ
								//forceがtrueの場合は、すでに値がセットされていても上書き
								if(force){
									String coord = getCoordinate(feature, mgrs, mgrsdigit);
									logger.info("setCoordinate : "+ttinfo.layerid+":"+feature.featureId+":"+coord);
									//this.update(ttinfo.tablename, tinfo.coordinatecolumn, "gid", feature.featureId, coord);
								//forceがfalseの場合は、値がセットされていない場合にのみ追加（上書きしない）
								}else{
									if(attr.getAttrValue()==null || attr.getAttrValue().length()==0){
										String coord = getCoordinate(feature, mgrs, mgrsdigit);
										logger.info("setCoordinate : "+ttinfo.layerid+":"+feature.featureId+":"+coord);
										//this.update(ttinfo.tablename, tinfo.coordinatecolumn, "gid", feature.featureId, coord);
									}
								}
							}
						}
					} catch (Exception e) {
						logger.error(loginDataDto.logInfo(), e);
						throw new ServiceException(e);
					}
				}
			}
		}
	}

	/**
	 * 座標情報を取得する
	 * @param feature	座標情報取得対象となるeコミマップのFeatureResultオブジェクト
	 * @param mgrs		true:座標値をMGRS形式にする、false：座標値を十進緯度経度にする
	 * @param mgrsdigit MGRS桁数
	 * @return
	 * @throws com.vividsolutions.jts.io.ParseException
	 */
	public String getCoordinate(FeatureResult feature, boolean mgrs, int mgrsdigit) throws com.vividsolutions.jts.io.ParseException{
		return getCoordinate(feature.getWKT(), mgrs, mgrsdigit);
	}

	/**
	 * 座標情報を取得する
	 * @param wkt
	 * @param mgrs true:座標値をMGRS形式にする、false：座標値を十進緯度経度にする
	 * @param mgrsdigit MGRS桁数
	 * @return
	 * @throws com.vividsolutions.jts.io.ParseException
	 */
	public String getCoordinate(String wkt, boolean mgrs, int mgrsdigit) throws com.vividsolutions.jts.io.ParseException{
		String coord = null;
		Geometry geom = GeometryUtils.getGeometryFromWKT(wkt);
		Point pt = geom.getCentroid();
		/** MGRS対応 */
		if(mgrs){
			Angle lonAngle = Angle.fromDegreesLongitude(pt.getX());
			Angle latAngle = Angle.fromDegreesLatitude(pt.getY());
			MGRSCoord mgrsCoord = MGRSCoord.fromLatLon(latAngle, lonAngle, mgrsdigit);
			coord = mgrsCoord.toString();
		}else{
			coord = "("+String.valueOf(pt.getX())+","+ String.valueOf(pt.getY())+")";
		}
		return coord;
	}

	/**
	 * DBの整合性チェック時に、指定のレコードの有無をチェックする。
	 * @param item チェックアイテム
	 * @return 検索結果
	 */
	public List<BeanMap> checkDistinct(String[] item)
	{
		String sql = "select * from "+item[0]+" where "+item[1]+" not in (select distinct(\""+item[3]+"\") from "+item[2]+" where "+item[3]+" > 0) and "+item[1]+" > 0";
		List<BeanMap> map = jdbcManager.selectBySql(BeanMap.class, sql).getResultList();
		return map;
	}

	/**
	 * DBの整合性チェック時にレコードを削除する。
	 * @param table
	 * @param idname
	 * @param key
	 */
	public void checkDelete(String table, String idname, String key)
	{
		String sql = "delete from "+table+" where "+idname+"="+key;
		jdbcManager.callBySql(sql).execute();
	}

	/**
	 * DBの整合性チェック時に外部キーの有無をチェックする。
	 * @return
	 */
	public List<BeanMap> checkForeignKey()
	{
		//String sql = "select ref.CONSTRAINT_NAME,col.TABLE_SCHEMA,col.TABLE_CATALOG,TABLE_NAME,COLUMN_NAME,ORDINAL_POSITION "+
		//		"from INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS as ref, INFORMATION_SCHEMA.KEY_COLUMN_USAGE as col "+
		//		"where ref.CONSTRAINT_NAME=col.CONSTRAINT_NAME and TABLE_CATALOG='ecommap_saigaitask' order by ref.CONSTRAINT_NAME,ORDINAL_POSITION;";
		String sql = "select CONSTRAINT_NAME, CONSTRAINT_CATALOG "+
				"from INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS ;";
		List<BeanMap> map = jdbcManager.selectBySql(BeanMap.class, sql).getResultList();
		return map;
	}

	/**
	 * 外部キー違反かを判定する。
	 * @param se SQL例外
	 * @return 判定結果
	 */
	public static boolean isForeignKeyViolation(SQLRuntimeException se){
		//PostgreSQLのエラーコード
		final String FOREIGN_KEY_VIOLATION_CODE = "23503";
		Object[] args = se.getArgs();
		if(args.length > 3){
			String error_code = (String)args[3];
			if(FOREIGN_KEY_VIOLATION_CODE.equals(error_code)){
				return true;
			}
		}

		return false;
	}

	/**
	 * IDそのままでInsertする
	 * @param entity エンティティ
	 */
	public int insertKeepId(final Object entity) {
		int num = jdbcManager.update(entity).execute();
		if(num==0) num = DatabaseUtil.insertKeepId(jdbcManager, entity).execute();
		return num;
	}
	/**
	 * IDそのままでInsertする
	 * @param entities エンティティリスト
	 */
	public int insertKeepId(List<?> entities) {
		int count = 0;
		if(entities!=null) {
			// 各レコードを挿入
			for(Object entity : entities) {
				// ID 付きで insert
				count += this.insertKeepId(entity);
			}
		}
		return count;
	}

	/**
	 * テーブルを削除(DROP)する
	 * @param table テーブル名
	 * @return 結果
	 */
	public int dropTable(String table) {
		return dropTable(null, table);
	}
	/**
	 * テーブルを削除(DROP)する
	 * @param schema スキーマ
	 * @param table テーブル名
	 * @return 結果
	 */
	public int dropTable(String schema, String table) {
		String sql = "DROP TABLE ";
		if(StringUtil.isNotEmpty(schema)) sql+=schema+".";
		sql+=table+";";

		return jdbcManager.updateBySql(sql).execute();
	}
	/**
	 *
	 */
	public List<BeanMap> fileexist(String layerid){
		String sql = "SELECT DISTINCT files.gid FROM _feature_files AS files WHERE files.layerid='"+ layerid + "'";
		return jdbcManager.selectBySql(BeanMap.class, sql).getResultList();
	}
}
