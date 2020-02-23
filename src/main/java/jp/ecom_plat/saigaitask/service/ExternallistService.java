/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.Base64Util;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.dto.ExternalListDto;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.dto.SummaryListDto;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.ClearinghousemetadataInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternaltabledataInfoService;
import jp.ecom_plat.saigaitask.service.db.StationlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.util.CSWUtil;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 外部リストのサービスクラスです.
 */
@org.springframework.stereotype.Service
public class ExternallistService extends BaseService {

	/** 総括表と識別するメタデータID */
	public static final String METADATA_GENERALIZATION = "generalization";
	/** 集計リストと識別するメタデータID */
	public static final String METADATA_SUMMARY = "summary";
	/** 内部リストのみの集計リストと識別するメタデータID */
	public static final String METADATA_SUMMARY_INTERNAL = "summary_internal";
	/** 外部リストのみの集計リストと識別するメタデータID */
	public static final String METADATA_SUMMARY_EXTERNAL = "summary_external";

	Logger logger = Logger.getLogger(ExternallistService.class);

	// Service
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected TrackgroupDataService trackgroupDataService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected StationlayerInfoService stationlayerInfoService;
	@Resource protected WFSService wfsService;
	@Resource protected TableService tableService;
	@Resource protected ExternaltabledataInfoService externaltabledataInfoService;
	@Resource protected SummarylistcolumnInfoService summarylistcolumnInfoService;
	@Resource protected ClearinghousemetadataInfoService clearinghousemetadataInfoService;
	@Resource protected CkanService ckanService;
    
	// Dto
    /** ログイン情報 */
    @Resource protected LoginDataDto loginDataDto;
	/** ページDto */
	@Resource public PageDto pageDto;

	/** 外部リストのフィルタ用trackdataidマップ */
	public Map<String, Long> filterTrackdataidMap;

	/**
	 * メニューIDを指定して外部地図のメタデータID/タイトルのMapを取得する
	 * @param menuid メニューID
	 * @param isTraining 訓練モードフラグ
	 * @return 外部地図のメタデータID/タイトルのMap
	 */
	public Map<String, String> getMetadataMap(long menuid, boolean isTraining) {
		long starttime = System.currentTimeMillis();
		String cswurl = isTraining ? Config.getString("CSWURL_TRAINING") : Config.getString("CSWURL");
		String cswuser = isTraining ? Config.getString("CSWUSER_TRAINING") : Config.getString("CSWUSER");
		String cswpasswd = isTraining ? Config.getString("CSWPASSWD_TRAINING") : Config.getString("CSWPASSWD");
		Map<String, String> metadataMap = new LinkedHashMap<String, String>();
		List<ExternaltabledataInfo> externaltabledataInfos = externaltabledataInfoService.findByMenuInfoId(menuid);
		for(ExternaltabledataInfo externaltabledataInfo : externaltabledataInfos) {
			// 認証情報のセッションを登録
			if(externaltabledataInfo.authorizationInfo != null){
				String authData = externaltabledataInfo.authorizationInfo.username + ":" + externaltabledataInfo.authorizationInfo.userpass;
				session.setAttribute("Externaltabledatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + externaltabledataInfo.metadataid, "Basic " + Base64Util.encode(authData.getBytes()));
			}else{
				session.setAttribute("Externaltabledatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + externaltabledataInfo.metadataid, "");
			}
			String metadataId = externaltabledataInfo.metadataid;
			String metadataName = metadataId;
			if(loginDataDto.isUseCkan()) {
				try {
					JSONObject jsonMetadata = ckanService.getRecordById(metadataId, isTraining);
					metadataName = jsonMetadata.getJSONObject("organizationData").getJSONObject("result").getString("title");
				} catch(Exception e) {
					logger.error("failed to get jsonMetadata organizationData.result.title(CKAN)", e);
				}
			}
			else {
				// メタデータのタイトルから自治体名を取得する
				String xml = CSWUtil.getRecordById(cswurl,cswuser,cswpasswd, metadataId);
				try {
					JSONObject json = new JSONObject(CSWUtil.MDMetadataToJSON(xml));
					String title = "No Name";
					if (json.has("title"))
						title = json.getString("title");
					metadataName = title;
					if(StringUtil.isNotEmpty(title)) {
						int index = title.lastIndexOf("/");
						metadataName = title.substring(index+1);
					}
				} catch (JSONException e) {
					logger.error("menuid: "+menuid+", metadataid: "+metadataId, e);
				}
			}

			metadataMap.put(metadataId, metadataName);
		}
		// 集計項目の設定があれば、集計リストのメニューを追加
		//if(0<summarylistcolumnInfoService.getCountByMenuid(menuid)) {
			//metadataMap.put(METADATA_SUMMARY, lang.__("Aggregation list"));
		//}
		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] ExternallistService.getMetadataMap elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");
		return metadataMap;
	}

	/**
	 * 外部リストのメタデータ
	 * @param menuid
	 * @return 外部リスト情報
	 */
	public Map<Long, ExternaltabledataInfo> getExternaltabledataInfoMap(long menuid) {
		Map<Long, ExternaltabledataInfo> map = new HashMap<Long, ExternaltabledataInfo>();

		// メニューに設定された外部リストのうち
		List<ExternaltabledataInfo> externaltabledataInfos = externaltabledataInfoService.findByMenuInfoId(menuid);
		for(ExternaltabledataInfo externaltabledataInfo : externaltabledataInfos) {
			String metadataId = externaltabledataInfo.metadataid;
			// メタデータを登録した自治体が、同じDBにあれば保存する
			List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByMetadataid(metadataId);
			for(ClearinghousemetadataInfo clearinghousemetadataInfo : clearinghousemetadataInfos) {
				if(clearinghousemetadataInfo!=null && clearinghousemetadataInfo.localgovInfo!=null) {
					map.put(clearinghousemetadataInfo.localgovinfoid, externaltabledataInfo);
				}
			}
		}

		return map;
	}

	/**
	 * 属性IDから属性名称を得られるMapを生成
	 * @param mtbl メニューテーブル情報
	 * @return 属性IDから属性名称を得られるMap
	 */
	public Map<String, String> getAttrNameMap(MenutableInfo mtbl) {
		Map<String, String> attrNameMap = null;
		if(mtbl!=null) {
			attrNameMap = new HashMap<String, String>();
			List<TablelistcolumnInfo> colinfoItems = mtbl.tablelistcolumnInfoList;
			if(colinfoItems!=null && 0<colinfoItems.size()) {
				for(TablelistcolumnInfo tablelistcolumnInfo : colinfoItems) {
					attrNameMap.put(tablelistcolumnInfo.attrid, tablelistcolumnInfo.name);
				}
			}
		}
		return attrNameMap;
	}

	/**
	 * ログイン中の記録グループの自治体情報リストを取得
	 * @return 自治体情報リスト
	 */
	public List<LocalgovInfo> getTrackgroupLocalgovInfo() {
		List<LocalgovInfo> localgovInfos = new ArrayList<LocalgovInfo>();
		LinkedHashMap<LocalgovInfo, Long> map = getTrackgroupLocalgovInfoMap();
		for(Map.Entry<LocalgovInfo, Long> entry : map.entrySet()) {
			localgovInfos.add(entry.getKey());
		}
		return localgovInfos;
	}

	/**
	 * ログイン中の記録グループの自治体情報リストを取得
	 * @return Map<自治体情報, 記録データID>
	 */
	public LinkedHashMap<LocalgovInfo, Long> getTrackgroupLocalgovInfoMap() {
		LinkedHashMap<LocalgovInfo, Long> map = new LinkedHashMap<LocalgovInfo, Long>();
		{
			// 都道府県でログイン中
			LocalgovInfo prefLocagovinfo = null;
			Long preftrackdataid = null;
			if(Localgovtype.PREF.equals(loginDataDto.getLocalgovInfo().localgovtypeid)) {
				prefLocagovinfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
				preftrackdataid = loginDataDto.getTrackdataid();
			}
			// 市町村でログイン中
			else {
				if(loginDataDto.getLocalgovInfo().preflocalgovinfoid!=null) {
					prefLocagovinfo = localgovInfoService.findById(loginDataDto.getLocalgovInfo().preflocalgovinfoid);
					List<TrackgroupData> trackgroupDatas = trackgroupDataService.findByPreflocalgovinfoAndCitytrackdataid(prefLocagovinfo.id, loginDataDto.getTrackdataid());
					// 災害グループに参加していない場合
					if(trackgroupDatas.size()==0) {
						map.put(loginDataDto.getLocalgovInfo(), loginDataDto.getTrackdataid());
						return map;
					}
					// 災害グループに参加している場合
					else {
						for(TrackgroupData trackgroupData : trackgroupDatas) {
							preftrackdataid = trackgroupData.preftrackdataid;
						}
					}
				}
			}
			if(prefLocagovinfo!=null) {
				map.put(prefLocagovinfo, preftrackdataid);
			}

			// 災害グループを取得
			List<TrackgroupData> trackgroupDatas = null;
			if(preftrackdataid!=null) {
				trackgroupDatas = trackgroupDataService.findByPreftrackdataid(preftrackdataid);
				// 災害グループに属する自治体を取得
				for(TrackgroupData trackgroupData : trackgroupDatas) {
					LocalgovInfo cityLocalgovInfo = trackgroupData.cityTrackData.localgovInfo;
					map.put(cityLocalgovInfo, trackgroupData.citytrackdataid);
				}
			}
		}
		return map;
	}

	/**
	 * 外部リストの総括リストを生成
	 * 各リストを１つの表にマージする.
	 * @param menuid 集計項目が定義されているメニューID
	 * @param mtbl 内部リスト
	 * @param filterTrackgroup 記録グループでフィルタする
	 * @param timeParam 時間パラメータ
	 * @param isTraining 訓練フラグ
	 * @return リストDto
	 */
	public ListDto createGeneralizationExternalList(long menuid, MenutableInfo mtbl, boolean filterTrackgroup, Date[] timeParam, boolean isTraining) {

		// リストの作成
		ListDto listDto = new ListDto();

		//
		// 列情報の設定
		//
		// 1列目は自治体名で固定
		listDto.columnNames.add(lang.__("Local gov. name"));

		// メニューテーブル情報から列情報を設定
		if(mtbl!=null) {
			for(TablelistcolumnInfo tablelistcolumnInfo : mtbl.tablelistcolumnInfoList) {
				listDto.columnNames.add(tablelistcolumnInfo.name);
			}
		}


		//
		// 行の追加
		//

		// リストに表示する自治体情報を取得
		List<LocalgovInfo> localgovInfos = getTrackgroupLocalgovInfo();

		// 自治体情報からリストを取得し、総括表リストへ追加
		{
			// システムにあると判断されたメタデータのMap
			Map<Long, ExternaltabledataInfo> externaltabledataMap = getExternaltabledataInfoMap(menuid);
			for(LocalgovInfo localgovInfo : localgovInfos) {
				if(localgovInfo==null) continue;

				// 自治体名を取得
				String localgovName = localgovInfo.pref;
				if(Localgovtype.CITY.equals(localgovInfo.localgovtypeid)) localgovName = localgovInfo.city;

				// テーブルリスト項目情報の列を持つリスト
				ListDto tablelistcolumnListDto = null;

				// ログイン中の自治体なら内部リスト
				if(loginDataDto.getLocalgovinfoid()==localgovInfo.id.longValue()) {
					tablelistcolumnListDto = createMenutableList(mtbl, filterTrackgroup, timeParam);
				}
				// 外部リストを取得
				else {
					ExternaltabledataInfo externaltabledataInfo = externaltabledataMap.get(localgovInfo.id);
					if(externaltabledataInfo!=null) {
						String metadataid = externaltabledataInfo.metadataid;

						// 属性マッチング済みの外部リストを取得
						tablelistcolumnListDto = createExternalList(metadataid, mtbl, null, filterTrackgroup, timeParam, isTraining, -2);
					}
				}

				// リストが得られない場合
				if(tablelistcolumnListDto==null || tablelistcolumnListDto.columnValues.size()==0) {
					List<String> generalizationValues = new ArrayList<String>();
					// 先頭は自治体名
					generalizationValues.add(localgovName);
					for(int index=0; index<mtbl.tablelistcolumnInfoList.size(); index++) {
						String value = SummaryListDto.VALUE_UNDEFINED;
						generalizationValues.add(value);
					}
					// リストへ一行追加
					listDto.columnValues.add(generalizationValues);
				}
				// リストが得られた場合
				else {
					// 得られたリストの値を総括表へ記録
					for(List<String> values : tablelistcolumnListDto.columnValues) {
						List<String> generalizationValues = new ArrayList<String>();
						// 先頭は自治体名
						generalizationValues.add(localgovName);

						// 外部リストの値を記録
						for(int index=0; index<mtbl.tablelistcolumnInfoList.size(); index++) {
							String value = SummaryListDto.VALUE_UNDEFINED;
							value = values.get(index);
							generalizationValues.add(value);
						}

						// リストへ一行追加
						listDto.columnValues.add(generalizationValues);
					}
				}
			}
		}

		return listDto;
	}

	/**
	 * 外部リストの集計リストを生成
	 * @param menuid 集計項目が定義されているメニューID
	 * @param metadataMap 外部リストのメタデータ
	 * @param mtbl 内部リスト
	 * @param filterTrackgroup 記録グループでフィルタする
	 * @param timeParam 時間パラメータ
	 * @param isTraining 訓練フラグ
	 * @return 集計リストDto
	 */
	public SummaryListDto createSummaryExternalList(long menuid, Map<String, String> metadataMap, MenutableInfo mtbl, boolean filterTrackgroup, Date[] timeParam, boolean isTraining) {
		SummaryListDto summaryListDto = new SummaryListDto();
		summaryListDto.columnNames = new ArrayList<String>();
		summaryListDto.columnValues = new ArrayList<List<String>>();

		// 属性IDから属性名称を得られるMapを生成
		Map<String, String> attrNameMap = getAttrNameMap(mtbl);

		// 集計項目の設定
		summaryListDto.addLocalgovnameRow(); //最初の列は自治体名で固定
		// 2番目の列に発生災害数を追加
		summaryListDto.addDisasternumRow();
		// DB から集計項目を取得
		List<SummarylistcolumnInfo> summarylistcolumnInfos = summarylistcolumnInfoService.findByMenuid(menuid);
		if(summarylistcolumnInfos.size()==0) throw new ServiceException(lang.__("External list summary info has not been set."));
		for(SummarylistcolumnInfo summarylistcolumnInfo : summarylistcolumnInfos) {
			//summaryListDto.columnNames.add(summarylistcolumnInfo.name);
			SummaryListDto.Calc calc = SummaryListDto.Calc.parse(summarylistcolumnInfo.function, attrNameMap);
			if(calc!=null) {
				// 条件があれば設定する
				String condition = summarylistcolumnInfo.condition;
				if(StringUtil.isNotEmpty(condition)) calc.condition(condition);
				summaryListDto.calcs.add(calc);
			}
		}

		// 内部リストがあれば取得
		if(mtbl!=null) {
			// リストを取得する
			ListDto list = createMenutableList(mtbl, filterTrackgroup, timeParam);

			// 集計リストに追加
			// 最初の列は自治体名で固定
			String localgovName = loginDataDto.getLocalgovInfo().prefcode + loginDataDto.getLocalgovInfo().citycode;
			summaryListDto.addList("", localgovName, list);
		}


		// 外部リストを取得
		if(metadataMap!=null) {
			for(Map.Entry<String, String> entry : metadataMap.entrySet()) {
				String metadataid = entry.getKey();
				if(METADATA_SUMMARY.equals(metadataid)) continue;

				// 属性マッチング済みの外部リストを取得
				//ListDto list = createExternalList(metadataid, mtbl, null, filterTrackgroup, isTraining);
				ExternalListDto list = createExternalList(metadataid, mtbl, null, filterTrackgroup, timeParam, isTraining, -2);
				String localgovName = list.localgovcode;
				summaryListDto.addList(metadataid, localgovName, list);
			}
		}

		// 集計を実行
		summaryListDto.summary();


		return summaryListDto;
	}

	/**
	 * 内部リストを生成.
	 * @param mtbl メニューテーブル情報
	 * @param filterTrackgroup 記録グループでフィルタする
	 * @param timeParam 時間パラメータ
	 *
	 * @return リストDto
	 */
	public ListDto createMenutableList(MenutableInfo mtbl, boolean filterTrackgroup, Date[] timeParam) {

//		// このメニューの列情報をMapで取得
//		Map<String, String> attrNameMap = getAttrNameMap(mtbl);
//		if(attrNameMap==null) {
//			logger.warn("テーブルリスト項目情報がありません.");
//			return null;
//		}

		//
		// リストの初期化
		//
		ListDto list = new ListDto();
		list.columnNames = new ArrayList<String>();
		list.columnValues = new ArrayList<List<String>>();
		list.distinctTrackdataids = new HashSet<Long>();

		// 列名の取得
		// テーブルリスト項目情報を取得
		for(TablelistcolumnInfo column : mtbl.tablelistcolumnInfoList) {
			list.columnNames.add(column.name);
		}

		//
		// 行の追加
		//

		List<BeanMap> result = null;
		{
			String table = null;
			String layerId = null;
			// マスタマップ
			if(loginDataDto.getTrackdataid()==0) {
				TablemasterInfo tablemasterInfo = tablemasterInfoService.findByNotDeletedId(mtbl.tablemasterinfoid);
				if(tablemasterInfo==null) {
					logger.warn(lang.__("Table master info:{0} not found.", mtbl.tablemasterinfoid));
				}
				else {
					table = tablemasterInfo.tablename;
					layerId = tablemasterInfo.layerid;
				}
			}
			// 災害中
			else {
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());
				if(ttbl==null) {
					logger.warn(lang.__("Table master info :{1} of record data :{0} not found.", loginDataDto.getTrackdataid(), mtbl.tablemasterinfoid));
				}
				else {
					table = ttbl.tablename;
					layerId = ttbl.layerid;
				}
			}

			// eコミマップのレイヤ
			if(StringUtil.isNotEmpty(layerId)) {
				result = tableService.selectAll(layerId, "desc", "gid", timeParam);
			}
			// リストのみのレイヤ
			else {
				result = tableService.selectByTrackdataid(table, loginDataDto.getTrackdataid(), "desc", "id");
			}
		}

		// 結果からDto を作成
		if(result!=null) {
			for(BeanMap bean : result) {
				// １行生成
				List<String> values = new ArrayList<String>();

				// trackdataid を取得
				Long trackdataid = null;
				try {
					trackdataid = Long.valueOf((String) bean.get(TrackdataidAttrService.TRACKDATA_ATTR_ID));
				} catch(Exception e) {
					logger.warn(e);
				}

				// 行のフィルタ
				// trackdataid でフィルタする
				if(filterTrackgroup) {
					if(trackdataid!=null && 0<trackdataid && 0<loginDataDto.getTrackdataid()) {
						if(trackdataid!=loginDataDto.getTrackdataid()) {
							continue;
						}
					}
				}

				// テーブルリスト項目情報を取得
				for(TablelistcolumnInfo column : mtbl.tablelistcolumnInfoList) {
					String attrId = column.attrid;
					String value = (String) bean.get(attrId);

					// データを追加
					values.add(value);
				}

				// リストへ追加
				list.columnValues.add(values);
				// 追加した行の trackdataid は保存する
				if(trackdataid!=null) {
					// 記録
					list.distinctTrackdataids.add(trackdataid);
				}
			}
		}

		return list;
	}

	/**
	 * 外部リストを生成
	 * 属性の日本語名称があれば、属性名でマッチングし、
	 * ない場合は、属性IDでマッチングされる
	 * @param metadataId メタデータID
	 * @param mtbl メニューテーブル情報
	 * @param matching 外部リストの属性IDから内部リストの属性IDが得られるMap
	 * @param filterTrackgroup 記録グループでリストをフィルタする
	 * @param timeParam 時間パラメータ
	 * @param isTraining 訓練フラグ
	 * @return 外部リストDto
	 */
	public ExternalListDto createExternalList(String metadataId, MenutableInfo mtbl, Map<String, String> matching, boolean filterTrackgroup, Date[] timeParam, boolean isTraining, int npage) {
		return createExternalListImpl("list", metadataId, mtbl, matching, filterTrackgroup, timeParam, isTraining, npage, null, null);
	}
	public ExternalListDto createExternalHistoryList(String metadataId, MenutableInfo mtbl, Map<String, String> matching, boolean filterTrackgroup, Date[] timeParam, boolean isTraining, int npage,
		Long gid, Long _orgid) {
		return createExternalListImpl("history", metadataId, mtbl, matching, filterTrackgroup, timeParam, isTraining, npage, gid, _orgid);
	}
	public ExternalListDto createExternalListImpl(String mode, String metadataId, MenutableInfo mtbl, Map<String, String> matching, boolean filterTrackgroup, Date[] timeParam, boolean isTraining, int npage,
			Long gid, Long _orgid) {

		// メタデータを登録した自治体が、同じDBにあれば保存する
		ClearinghousemetadataInfo clearinghousemetadataInfo = null;
		List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByMetadataid(metadataId);
		if(0<clearinghousemetadataInfos.size()) clearinghousemetadataInfo = clearinghousemetadataInfos.get(0);

		// 記録グループでフィルタするなら、クリアリングハウスメタデータ情報は必須
		if(filterTrackgroup && clearinghousemetadataInfo==null) {
			return null;
		}

		// ログ表示
		logger.info("Create External List: metadataId="+metadataId
				+(mtbl!=null?", mtbl="+mtbl.id+", menuinfoid="+mtbl.menuinfoid:"")
		);

		// リスト表示のメニューテーブル情報があれば
		int timeFromColumnIdx = -1;
		if(mtbl!=null)  {
			// 外部地図データにメニューテーブル情報の設定を反映する.
			List<TablelistcolumnInfo> colinfoItems = mtbl.tablelistcolumnInfoList;
			if(colinfoItems!=null && 0<colinfoItems.size()) {

				// 履歴表示の場合
				if("history".equals(mode)) {
					List<TablelistcolumnInfo> newlist = new ArrayList<>(2+colinfoItems.size());
					// 最終更新日時
					{
						TablelistcolumnInfo colInfo = new TablelistcolumnInfo();
						colInfo.name = lang.__("Update date and time");
						colInfo.attrid = "time_from";
						newlist.add(colInfo);
					}
					// 編集者
					{
						TablelistcolumnInfo colInfo = new TablelistcolumnInfo();
						//colInfo.name = lang.__("Editor");
						colInfo.name = Config.getString("EDITUSERATTR_NAME", lang.__("[NIED DISS]Editor"));
						colInfo.attrid = "st_edituser";
						newlist.add(colInfo);
					}
					newlist.addAll(colinfoItems);
					mtbl.tablelistcolumnInfoList = newlist;
				}
			}
		}

		// 外部リストDto を作成
		if(metadataId.indexOf("list") == 0){return null;}
		ExternalListDto listDto = createMachingWfsList(metadataId, mtbl, matching, timeParam, isTraining, npage,
				gid, _orgid);

		// 履歴表示の場合
		if("history".equals(mode)) {
			// st_edituser列の列名を調整
			for(TablelistcolumnInfo colInfo : mtbl.tablelistcolumnInfoList) {
				if("st_edituser".equals(colInfo.attrid)) {
					colInfo.name = lang.__("Editor");
					break;
				}
			}
		}
		
		// メタデータID を保存
		listDto.metadataId = metadataId;
		listDto.clearinghousemetadataInfo = clearinghousemetadataInfo;
		
		// 履歴ボタンを表示
		listDto.historybtn = "list".equals(mode);

		// リスト表示のメニューテーブル情報があれば
		if(mtbl!=null)  {
			// 外部地図データにメニューテーブル情報の設定を反映する.
			List<TablelistcolumnInfo> colinfoItems = mtbl.tablelistcolumnInfoList;
			if(colinfoItems!=null && 0<colinfoItems.size()) {
				// データがない場合は、列名だけ設定する
				if(listDto.columnValues.size()==0) {
					listDto.columnNames = new ArrayList<String>();
					for(TablelistcolumnInfo tablelistcolumnInfo : colinfoItems) {
						// 属性名を内部地図から取得
						String name = tablelistcolumnInfo.name;
						listDto.columnNames.add(name);
					}
				}
				// 外部地図データに対応する属性があればインデックスを指定し、
				// 対応する属性がなければ null を指定する.
				else {
					// メニューの項目設定を取得
					List<String> columnNames = new ArrayList<String>();
					List<Integer> columnIdxs = new ArrayList<Integer>();
					for(TablelistcolumnInfo tablelistcolumnInfo : colinfoItems) {
						String name = tablelistcolumnInfo.name;
						String attrId = tablelistcolumnInfo.attrid;
						int columnIdx = StringUtil.isNotEmpty(attrId) ? listDto.columnNames.indexOf(attrId) : -1;
						columnIdxs.add(columnIdx);
						columnNames.add(name);
						// time_from の columnIdxを保存
						if(attrId.equals("time_from")) timeFromColumnIdx = columnIdx;
					}

					// trackdataid属性のインデックスを取得
					int trackdataidAttrIdx = listDto.getTrackdataidRowIdx();
					if(trackdataidAttrIdx==-1) {
						logger.debug(lang.__("Metadata : {0} does not have  {1} attribute.", metadataId, TrackdataidAttrService.TRACKDATA_ATTR_ID));
					}
					else {
						// メタデータが clearinghousemetadata_info にないなら他システムのデータとみなし、trackdataid列は追加しない
						if(listDto.clearinghousemetadataInfo==null) {
							logger.debug(lang.__("Metadata : {0} has trackdataid but it does not correspond to clearinghousemetadata_info. Then it is judged to other system.", metadataId));
						}
						else {
							// trackdataid属性があるなら、trackdataid列を追加する
							columnIdxs.add(trackdataidAttrIdx);
							columnNames.add(TrackdataidAttrService.TRACKDATA_ATTR_ID);
						}
					}

					// 外部地図データにメニューの設定を反映する
					List<List<String>> columnValues = new ArrayList<List<String>>();
					listDto.distinctTrackdataids = new HashSet<Long>();
					for(List<String> columnValue : listDto.columnValues) {
						// 行の記録IDを取得
						Long columnTrackdataid = null;
						if(trackdataidAttrIdx!=-1) {
							String str = columnValue.get(trackdataidAttrIdx);
							try {
								if(StringUtil.isNotEmpty(str)) {
									columnTrackdataid = Long.valueOf(str);
								}
							} catch(Exception e) {
								logger.warn(lang.__("Unable to get trackdataid(\"{1}\") of external list(\"{0}\").", metadataId, str), e);
							}
						}
						// trackdataid 属性の値があれば
						if(columnTrackdataid!=null) {
							// フィルタ用trackdataid が指定されていれば、それ以外は飛ばす
							if(filterTrackgroup) {
								Long filterTrackdataid = filterTrackdataidMap.get(metadataId);
								if(filterTrackdataid!=null && 0<columnTrackdataid && !filterTrackdataid.equals(columnTrackdataid)) {
									continue;
								}
							}

							// 追加した trackdataid は記録する
							listDto.distinctTrackdataids.add(columnTrackdataid);
						}
						// メニューの項目設定のみのデータだけ取得する
						List<String> value = new ArrayList<String>();
						for(Integer columnIdx : columnIdxs) {
							if(-1<columnIdx && columnIdx<columnValue.size()) {
								// time_fromならUTCからローカル時間に変更
								if(timeFromColumnIdx==columnIdx) {
									String timeFrom = columnValue.get(columnIdx);
									String localTimeFrom = timeFrom;
									if(StringUtil.isNotEmpty(timeFrom)) {
										Date timeFromDate = TimeUtil.parseISO8601(timeFrom, true);
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
										localTimeFrom = sdf.format(timeFromDate);
									}
									value.add(localTimeFrom);
								}
								else {
									value.add(columnValue.get(columnIdx));
								}
							}
							else value.add(null);
						}
						columnValues.add(value);
					}
					
					for(Map<String, String> columnValueMap : listDto.columnValueMaps) {
						// 履歴ボタン用のURLを生成
						String historybtnUrlParam = "";
						String _orgidVal = columnValueMap.get("_orgid");
						if(StringUtil.isNotEmpty(_orgidVal)) {
							historybtnUrlParam += "_orgid="+_orgidVal;
						}
						String idVal = columnValueMap.get("id");
						if(StringUtil.isNotEmpty(idVal)) {
							if(StringUtil.isNotEmpty(historybtnUrlParam)) historybtnUrlParam += "&";
							historybtnUrlParam += "gid="+(idVal.indexOf('.')!=-1?idVal.split("\\.")[1]:idVal);
						}
						listDto.historybtnUrlParams.add(historybtnUrlParam);
					}

					// 内部リストの列にリストを更新
					listDto.columnNames = columnNames;
					listDto.columnValues = columnValues;
				}
			}
		}
		return listDto;
	}

	/**
	 * 外部リストを取得し、属性IDをマッチングして、内部リストの属性IDに振り直す
	 * @param metadataId メタデータID
	 * @param mtbl メニューテーブル情報
	 * @param matching 属性IDマッチングMap<外部リスト属性ID, 内部リスト属性ID>
	 * @param timeParam 時間パラメータ
	 * @param isTraining 訓練フラグ
	 * @return 外部リストDto
	 */
	public ExternalListDto createMachingWfsList(String metadataId, MenutableInfo mtbl, Map<String, String> matching, Date[] timeParam, boolean isTraining, int npage,
			Long gid, Long _orgid) {

		logger.debug("create wfs list metadataid=\""+metadataId+"\"");
		// メタデータに紐付く認証情報を取得する
		String authorizationHeader = "";
		String sessionAuthorizationHeader = (String) session.getAttribute("Externaltabledatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + metadataId);
		if(sessionAuthorizationHeader != null){
			authorizationHeader = sessionAuthorizationHeader;
		}
		// メタデータから WFS URL などをクリアリングハウスに問い合わせる
		String[] ret = wfsService.initWfs(metadataId, isTraining, authorizationHeader);
		if (ret == null) return null;

		String wfsUrl = ret[0];
		String typeName = ret[1];
		String exTemp = ret[4];
		ExternalListDto listDto = wfsService.getListImpl(wfsUrl, typeName, timeParam, authorizationHeader, exTemp, npage, gid, _orgid);
		listDto.localgovcode = ret[3];

		// マッチングがなければ作成する
		if(matching==null) {
			// 外部リストの属性名称と内部リストの属性名称でマッチング
			Map<String, String> labelMap = wfsService.getLabelMap(wfsUrl, typeName);
			if(labelMap!=null && mtbl!=null) {
				logger.debug(lang.__("-- Matching by attribute in Japanese name BEGIN --"));
				logger.debug(lang.__("Label \"internal list attribute name\"(internal list attribute ID) match external list attribute ID"));
				matching = new HashMap<String, String>();
				matching.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, TrackdataidAttrService.TRACKDATA_ATTR_ID); // 複数同時災害対応
				// 外部地図データにメニューテーブル情報の設定を反映する.
				List<TablelistcolumnInfo> colinfoItems = mtbl.tablelistcolumnInfoList;
				if(colinfoItems!=null && 0<colinfoItems.size()) {
					for(TablelistcolumnInfo tablelistcolumnInfo : colinfoItems) {
						// 属性名を内部地図から取得
						String name = tablelistcolumnInfo.name;
						if(labelMap.containsKey(name)) {
							String attrId = labelMap.get(name);
							matching.put(attrId, tablelistcolumnInfo.attrid);
							logger.debug("label \"" + name + "\"("+tablelistcolumnInfo.attrid+") match "+attrId);
						}
						else {
							logger.debug("label \"" + name + "\"("+tablelistcolumnInfo.attrid+") no match");
						}
					}
				}
				logger.debug(lang.__("-- Matching by attribute in Japanese name END --"));
			}
		}

		// マッチングを反映する
		if(matching!=null) {
			logger.debug(lang.__("Matching   External list attribute ID -> Internal list attribute ID"));
			for(int idx=0; idx<listDto.columnNames.size(); idx++) {
				String attrId = listDto.columnNames.get(idx);
				// マッチングがあるなら変換
				if(matching.containsKey(attrId)) {
					listDto.columnNames.set(idx, matching.get(attrId));
					logger.debug("match " + attrId + " -> " + matching.get(attrId));
				}
				// マッチングしないものは null に変換
				else {
					listDto.columnNames.set(idx, null);
					logger.debug("no match " + attrId);
				}
			}
		}
		else logger.debug(lang.__("Matching by attribute ID"));

		return listDto;
	}
}
