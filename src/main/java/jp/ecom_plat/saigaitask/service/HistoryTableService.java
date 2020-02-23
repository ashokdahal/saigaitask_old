/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.HistorycolumnlistInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.HistorycolumnlistInfoService;
import jp.ecom_plat.saigaitask.service.db.HistorytableInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 履歴テーブルを利用するためのサービスクラス
 */
@org.springframework.stereotype.Service
public class HistoryTableService {

	Logger logger = Logger.getLogger(AbstractService.class);
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;
	protected static final String HISTORYTABLE_SCHEMA = "public";

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
	@Resource protected HistorytableInfoService historytableInfoService;
	@Resource protected HistorycolumnlistInfoService historycolumnlistInfoService;
	@Resource protected TableService tableService;

	/**
	 * すべてのデータを履歴テーブルに記録する。
	 * @param ttbl 対象となる記録テーブル
	 * @param htbl ログ記録先履歴テーブル
	 * @param layerInfo レイヤ情報
	 */
	public void logAll(TracktableInfo ttbl, HistorytableInfo htbl, LayerInfo layerInfo){
		// 履歴レイヤの場合、記録しない
		if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			return;
		}

		//履歴を保存するカラム
		List<HistorycolumnlistInfo> hcols = historycolumnlistInfoService.findByHistorytableinfoid(htbl.id);
		String hcolnames = "";
		for(HistorycolumnlistInfo hcol:hcols){
			hcolnames += ","+hcol.attrId;
		}
		hcolnames = hcolnames.substring(1);

		String sql = "";
		// 班ID追加対応 ////////////////////
		//sql = "insert into "+htbl.historytablename+" ("+hcolnames+") ";
		//sql += " select "+hcolnames+" from "+ttbl.tablename;
		// 2015.12.21 履歴テーブルのスキーマはpublic固定
		sql = "insert into "+HISTORYTABLE_SCHEMA+"."+htbl.historytablename+" ("+hcolnames+",groupid) ";
		sql += " select "+hcolnames+ ","+loginDataDto.getGroupid()+" from "+ttbl.tablename;
		// 班ID追加対応 ////////////////////

		// 班ID追加対応2 ////////////////////
		// groupidカラムの存在確認を行い、存在しない場合は追加する
		checkGroupidColumnAndAdd(htbl.historytablename, true);
		// 班ID追加対応2 ////////////////////

		jdbcManager.updateBySql(sql).execute();
	}

	/**
	 * idを指定して履歴テーブルに記録する。
	 * @param ttbl 対象となる記録テーブル
	 * @param htbl ログ記録先履歴テーブル
	 * @param idInTracktable 記録するデータの記録テーブルでのid
	 * @param layerInfo レイヤ情報
	 */
	public void log(TracktableInfo ttbl, HistorytableInfo htbl, Long idInTracktable, LayerInfo layerInfo){
		// 履歴レイヤの場合、記録しない
		if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			return;
		}

		//履歴を保存するカラム
		List<HistorycolumnlistInfo> hcols = historycolumnlistInfoService.findByHistorytableinfoid(htbl.id);
		String hcolnames = "";
		for(HistorycolumnlistInfo hcol:hcols){
			hcolnames += ","+hcol.attrId;
		}
		hcolnames = hcolnames.substring(1);

		String sql = "";
		// 班ID追加対応 ////////////////////
		//sql = "insert into "+htbl.historytablename+" ("+hcolnames+") ";
		//sql += " select "+hcolnames+" from "+ttbl.tablename;
		// 2015.12.21 履歴テーブルのスキーマはpublic固定
		sql = "insert into "+HISTORYTABLE_SCHEMA+"."+htbl.historytablename+" ("+hcolnames+",groupid) ";
		sql += " select "+hcolnames+ ","+loginDataDto.getGroupid()+" from "+ttbl.tablename;
		// 班ID追加対応 ////////////////////
		sql += " where "+htbl.idColumn+" = ?";

		// 班ID追加対応2 ////////////////////
		// groupidカラムの存在確認を行い、存在しない場合は追加する
		checkGroupidColumnAndAdd(htbl.historytablename, true);
		// 班ID追加対応2 ////////////////////

		jdbcManager.updateBySql(sql,Long.class).params(idInTracktable).execute();
	}

	/**
	 * 記録テーブルでのidを指定してその履歴データを時間順にソートして一括取得
	 * @param htbl 履歴テーブル
	 * @param idInTracktable 記録テーブルでのid値
	 * @return 履歴データのリスト
	 */
	public List<BeanMap> getLogByIDSorted(HistorytableInfo htbl, Long idInTracktable){
		// 2015.12.21 履歴テーブルのスキーマはpublic固定
		String sql = "select * from "+HISTORYTABLE_SCHEMA+"."+htbl.historytablename+" where "+htbl.idColumn+" = ? order by -log_id";
		return jdbcManager.selectBySql(BeanMap.class, sql, idInTracktable).getResultList();
	}

	/** 記録テーブルでのidごとにまとめて時間順にソートして返す
	 * @param htbl 記録テーブル
	 * @return 同一id値の履歴データのリストをさらにリストにしたもの
	 */

	public List<List<BeanMap>> getLogGroupById(HistorytableInfo htbl){
		// 2015.12.21 履歴テーブルのスキーマはpublic固定
		String sql = "select * from "+HISTORYTABLE_SCHEMA+"."+htbl.historytablename
				+ " order by "+ htbl.idColumn+ ", -log_id";
		List<BeanMap> datas = jdbcManager.selectBySql(BeanMap.class, sql).getResultList();

		return groupById(htbl, datas);
	}

	/**
	 * trackdataidで検索し、記録テーブルでのidごとにまとめて時間順にソートして返す
	 * @param htbl 履歴テーブル
	 * @param trackdataid 記録id
	 * @return 同一id値の履歴データのリストをさらにリストにしたもの
	 */

	public List<List<BeanMap>> getLogByTrackdataidGroupById(HistorytableInfo htbl, Long trackdataid){
		String trackdataidStr = Long.toString(trackdataid);

		// 2015.12.21 履歴テーブルのスキーマはpublic固定
		String sql = "select * from "+HISTORYTABLE_SCHEMA+"."+htbl.historytablename
				+" WHERE trackdataid = '"+ trackdataidStr + "'"
				+ " order by "+ htbl.idColumn+ ", -log_id";
		List<BeanMap> datas = jdbcManager.selectBySql(BeanMap.class, sql).getResultList();

		return groupById(htbl, datas);
	}

	/**
	 * 履歴データを記録テーブルでのidごとにまとめる
	 * @param htbl 履歴データを取りだした履歴テーブル
	 * @param datas 履歴データのリスト
	 * @return 同一idの履歴データのリストをさらにリストにしたもの
	 */
	private List<List<BeanMap>> groupById(HistorytableInfo htbl, List<BeanMap> datas){
		Long lastId = null;
		List<List<BeanMap>> a = new ArrayList<List<BeanMap>>();
		List<BeanMap> datasForId = new ArrayList<BeanMap>();
		for(BeanMap data: datas){
			Long nowId = (Long)data.get(htbl.idColumn);
			if(lastId == null){
				lastId = (Long)data.get(htbl.idColumn);
			}else if(!lastId.equals(nowId)){
				lastId = (Long)data.get(htbl.idColumn);
				a.add(datasForId);
				datasForId = new ArrayList<BeanMap>();
			}
			datasForId.add(data);
		}
		if(lastId != null) a.add(datasForId);

		return a;
	}

	/**
	 * 記録テーブルを作成する
	 * @param ttable 作成する記録テーブルに対応する記録テーブル
	 * @param layerInfo レイヤ情報
	 * @return 作成した履歴テーブル
	 */
	public HistorytableInfo createTable(TracktableInfo ttable, LayerInfo layerInfo) {
		// 履歴レイヤの場合、記録しない
		if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			return null;
		}

		if(ttable.layerid == null  || ttable.layerid.isEmpty()) {



			//履歴テーブルの名称
			String htname;
			String ttname = ttable.tablename;
			if(ttname.endsWith("_data")){
				htname = ttname.substring(0, ttname.length()-4)+"history";
			}else{
				htname = ttname+"_history";
			}
			//存在するか確認
			if(!tableService.isExists(htname))
				return null;

			//履歴テーブルリストへの登録
			HistorytableInfo htable = new HistorytableInfo();
			htable.tablemasterinfoid = ttable.tablemasterinfoid;
			htable.trackmapinfoid = ttable.trackmapinfoid;
			htable.historytablename = htname;
			htable.idColumn = "id";
			historytableInfoService.insert(htable);

			//カラムリストの登録
			List<BeanMap> attrs = tableService.getCols(ttname);
			for (BeanMap map : attrs) {
				Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator();
				while (it.hasNext()) {
					HistorycolumnlistInfo hcli = new HistorycolumnlistInfo();
					hcli.historytableinfoid = htable.id;
					hcli.attrId = String.valueOf(it.next().getValue());
					hcli.attrName = "";
					hcli.doLog = true;
					historycolumnlistInfoService.insert(hcli);
				}
			}

			return htable;
		} else{
			//記録テーブルの名称
			String htname = ttable.tablename+"_history";
			//記録テーブルを作成する元テーブルのカラムの取得
			String sql = "select * from _attr where attr_layer_id = '"+ttable.tablename+"'";
			List<BeanMap> attrs = jdbcManager.selectBySql(BeanMap.class,sql).getResultList();

			// テーブルが存在していなければ作成する
			// コピーフラグ:false の場合、すでに作成済みであるため
			if(tableService.isExists(htname)==false) {
				//テーブル作成SQL文の組み立て
				// 2015.12.21 履歴テーブルのスキーマはpublic固定
				String createSql = "create table "+HISTORYTABLE_SCHEMA+"."+htname+" (";
				createSql += "log_id bigserial PRIMARY KEY";
				createSql += ",log_time timestamp without time zone DEFAULT now() NOT NULL";
				createSql += ",gid bigint";
				createSql += ",the_geom geometry";
				for(BeanMap attr: attrs){
					createSql += ","+attr.get("attrId")+" text";
				}
				// 班ID追加対応 ////////////////////
				createSql += ",groupid text";
				// 班ID追加対応 ////////////////////
				createSql += ")";

				//テーブル作成SQL実行
				jdbcManager.updateBySql(createSql).execute();
			}

			//履歴テーブルリストへの登録
			HistorytableInfo htable = new HistorytableInfo();
			htable.tablemasterinfoid = ttable.tablemasterinfoid;
			htable.trackmapinfoid = ttable.trackmapinfoid;
			htable.historytablename = htname;
			htable.idColumn = "gid";
			historytableInfoService.insert(htable);
			//カラムリストの登録
			for(BeanMap attr: attrs){
				HistorycolumnlistInfo hcli = new HistorycolumnlistInfo();
				hcli.historytableinfoid = htable.id;
				hcli.attrId = (String) attr.get("attrId");
				hcli.attrName = (String) attr.get("attrName");
				hcli.doLog = true;
				historycolumnlistInfoService.insert(hcli);
			}
			HistorycolumnlistInfo hcli = new HistorycolumnlistInfo();
			hcli.historytableinfoid = htable.id;
			hcli.attrId = "gid";
			hcli.attrName = lang.__("Feature ID<!--2-->");
			hcli.doLog = true;
			historycolumnlistInfoService.insert(hcli);
			hcli = new HistorycolumnlistInfo();
			hcli.historytableinfoid = htable.id;
			hcli.attrId = "the_geom";
			hcli.attrName = lang.__("Geographical info");
			hcli.doLog = true;
			historycolumnlistInfoService.insert(hcli);

			return htable;
		}
	}

	/**
	 * 履歴テーブルを削除する.
	 * 履歴テーブルが存在するのに削除失敗した場合は{@link ServiceException}が発生する.
	 * @param entity 履歴テーブル情報
	 * @return 削除フラグ
	 */
	public boolean dropTable(HistorytableInfo entity) {
		boolean deleted = false;
		if(entity!=null && StringUtil.isNotEmpty(entity.historytablename)) {
			// 履歴テーブルが作成されるスキーマは災害起動時の言語のスキーマとなるので、
			// 全言語のスキーまでテーブルが存在するか確認する。
			// ↑2015.12.21 履歴テーブルスキーマ対応 作成スキーマはpublicで固定とした
			List<String> schemas = new ArrayList<String>();
			schemas.add(HISTORYTABLE_SCHEMA);
			List<MultilangInfo> multilangInfos = SpringContext.getApplicationContext().getBean(MultilangInfoService.class).findAll();
			for(MultilangInfo multilangInfo : multilangInfos) {
				schemas.add(multilangInfo.code);
			}

			// 全スキーマを検索して、最初に見つかった履歴テーブルをDROPする
			for(String schema : schemas) {
				// find history table
				TableService tableService = SpringContext.getApplicationContext().getBean(TableService.class);
				if(tableService.isExists(schema, entity.historytablename)) {
					// if found, drop history table
					tableService.dropTable(schema, entity.historytablename);
					// check if history tabe dropped
					if(tableService.isExists(schema, entity.historytablename)) {
						// if history table exists, occur error
						throw new ServiceException("can not delete HistoryTable. historytablename="+schema+"."+entity.historytablename+" drop failed");
					}
					// drop history table success
					deleted = true;
					continue;
				}
			}

			// 履歴テーブル情報があるのに、実テーブルがない場合は警告ログを出力して処理を継続
			if(!deleted) {
				logger.warn("can not delete HistoryTable. historytablename="+entity.historytablename+" not found: "+schemas.toString());
				//throw new ServiceException("can not delete HistoryTable. historytablename="+entity.historytablename+" not found: "+schemas.toString());
			}
		}

		return deleted;
	}

	/**
	 * 履歴テーブルを取得する。なければ作成する。
	 * @param ttable 取得する履歴テーブルに対応する記録テーブル
	 * @param layerInfo レイヤ情報
	 * @return 履歴テーブル
	 */
	public HistorytableInfo findOrCreateByTracktableInfo(TracktableInfo ttable, LayerInfo layerInfo) {
		// 履歴レイヤの場合、記録しない
		if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			return null;
		}
		HistorytableInfo htable = historytableInfoService.findByTracktableInfo(ttable);
		if(htable != null) return htable;
		else return createTable(ttable, layerInfo);
	}

	/**
	 * 履歴テーブルにgroupidカラムの存在確認を行い、存在しない場合は追加する。
	 * @param historyTableName
	 * @param create
	 * @return 履歴テーブルにgroupidカラムの存在フラグ
	 */
	public boolean checkGroupidColumnAndAdd(String historyTableName, boolean create){
		String targetColumnName = "groupid";
		String targetColumnTypeName = "text";

		List<BeanMap> historyTableColumns = tableService.getCols(historyTableName);
		boolean hasGroupid = false;
		for(BeanMap historyTableColumn : historyTableColumns){
			String colName = (String)historyTableColumn.get("attname");

			if(targetColumnName.equals(colName.toLowerCase())){
				hasGroupid = true;
				break;
			}
		}

		if( (! hasGroupid) && create ){
			// 2015.12.21 履歴テーブルのスキーマはpublic固定
			String addSql = "ALTER TABLE " + HISTORYTABLE_SCHEMA+"."+ historyTableName + " ADD " + targetColumnName + " " + targetColumnTypeName;
			jdbcManager.updateBySql(addSql).execute();
		}

		if(hasGroupid){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @param trackmapinfoid 記録地図情報ID
	 * @param mapmasterinfoid マスターマップ情報ID
	 * @return 履歴テーブルが存在するなら true
	 */
	public boolean checkIfHistorytableExists(long trackmapinfoid, long mapmasterinfoid) {
		String sql = "/*checkIfHistorytableExists*/ select 0<count(*) AS exists from historytable_info where trackmapinfoid=? and tablemasterinfoid in(select id from tablemaster_info where mapmasterinfoid=?)";
		BeanMap beanMap = jdbcManager.selectBySql(BeanMap.class, sql, trackmapinfoid, mapmasterinfoid).getSingleResult();
		return (Boolean) beanMap.get("exists");
	}
}
