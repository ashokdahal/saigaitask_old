/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.ListForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.ExternallistService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.util.SpringContext;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 体制の集計・総括
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class StationGeneralizationAction extends AbstractPageAction {

	protected ListForm listForm;
	// Service
	@Resource protected TrackgroupDataService trackgroupDataService;
	@Resource protected SummarylistcolumnInfoService summarylistcolumnInfoService;
	@Resource protected MenutableInfoService menutableInfoService;
	@Resource protected TablelistcolumnInfoService tablelistcolumnInfoService;
	@Resource protected ExternallistService externallistService;
	@Resource protected SummarylistInfoService summarylistInfoService;

	/** ページ種別 */
	public static final String PAGE_TYPE = "stationGeneralization";

	/** 表示するリスト */
	public ListDto listDto;

	/** 表示するリスト */
	public ListDto sublistDto;

	/** 表示するリスト */
	public List<ListDto> listDtos;

	/** 体制列 */
	public String STATIONCLASS_ROW_NAME() { return lang.__("System"); }
	/** 発令日時列 */
	public String SHIFTTIME_ROW_NAME() { return lang.__("Announcement date and time"); }

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("listDto", listDto);
		model.put("sublistDto", sublistDto);
		model.put("listDtos", listDtos);
		model.put("listForm", listForm);
	}
	/**
	 * 外部リストページを表示する.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/stationGeneralization","/page/stationGeneralization/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult) {
		this.listForm = listForm;
		// ページの初期化
		initPage(PAGE_TYPE, listForm);
		pageDto.setEnableFullscreen(true);

		setupModel(model);
		return getContent();
	}


	/**
	 * 集計表と総括表を表示する
	 * @return フォワード先
	 */
	public String getContent() {
		final long menuid = pageDto.getMenuInfo().id;

		// リスト初期化
		listDtos = new ArrayList<ListDto>();

		// メニューテーブル情報
		listForm.menuid = menuid;
		MenutableInfo mtbl = null;
		MenutableInfo mtblsummary = null;
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		if(0<mtbllist.size()) {
			// 1番目のテーブルを集計対象とする
			mtbl = mtbllist.get(0);
			mtbl.tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
			// 2番目のテーブルを集計表とする
			if(2<=mtbllist.size())
				mtblsummary = mtbllist.get(1);
		}
		boolean isTraining = false;
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			isTraining = trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
		}

		//
		// 集計表を表示
		//
		try {
			if(mtblsummary!=null) {
				ListDto sublistDto = new ListDto();
				// 集計表の先頭行は自治体名で固定
				sublistDto.columnNames.add(lang.__("Local gov. name"));
				sublistDto.styleId = "summarylist";
				sublistDto.title = lang.__("Aggregation table");

				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtblsummary.tablemasterinfoid, loginDataDto.getTrackdataid());
				String layerId = ttbl.layerid;
				List<BeanMap> layers = tableService.selectAll(layerId);
				// 集計リスト項目情報の取得
				List<SummarylistcolumnInfo> slclist = summarylistcolumnInfoService.findByMenuid(menuid);
				// 集計リスト情報の取得
				SummarylistInfo summarylistInfo = summarylistInfoService.findByTablemasterinfoId(mtblsummary.tablemasterinfoid);
				List<String> attrids = new ArrayList<String>();
				// 自治体コード項目名
				attrids.add(summarylistInfo.localgovcode);
				for(SummarylistcolumnInfo slc : slclist){
					attrids.add(slc.attrid);
				}

				// 集計リストの項目名を取得
				for(BeanMap layer: layers){
					List<String> columnNames = new ArrayList<String>();
					for(String attrid: attrids){
						if(layer.get(attrid) != null){
							columnNames.add(layer.get(attrid).toString());
						}else{
							columnNames.add("-");
						}
					}
					sublistDto.columnValues.add(columnNames);
				}
				//絞り込み条件
				for(SummarylistcolumnInfo summarylistcolumnInfo : slclist){
					String condition = summarylistcolumnInfo.condition;
					sublistDto.columnNames.add(condition);
				}

				listDtos.add(sublistDto);
			}

			//
			// 総括表を生成
			//
			if(mtbl!=null) {
				listDto = externallistService.createGeneralizationExternalList(menuid, mtbl, false, null, isTraining);
				listDto.styleId = "generalizationlist";
				listDto.title = lang.__("Summary table");

				// デフォルトソートをパラメータから引き継ぐ
				if (StringUtil.isNotEmpty(listForm.sort) && !listForm.sort.equals("undefined") && !listForm.sort.equals("-1"))
					listDto.defsort = listForm.sort;

				listDtos.add(listDto);
			}

		}catch(Exception e) {
			logger.warn(lang.__("Aggregation table of the system summary menu not created."), e);
		}

		return "/page/"+ExternallistAction.PAGE_TYPE+"/index";
	}

	@org.springframework.web.bind.annotation.RequestMapping(value="/page/stationGeneral/updateSummary")
	public String updateSummary(Map<String,Object>model,
			@Valid @ModelAttribute ListForm listForm, BindingResult bindingResult){
		this.listForm = listForm;
		initPage(PAGE_TYPE, listForm);
		pageDto.setEnableFullscreen(true);
		final long menuid = pageDto.getMenuInfo().id;
		Timestamp time = new Timestamp(TimeUtil.newDate().getTime());
		Date[] timeParam = new Date[]{new Date(time.getTime())};

		// リスト初期化
		listDtos = new ArrayList<ListDto>();

		// メニューテーブル情報
		listForm.menuid = menuid;
		MenutableInfo mtbl = null;
		MenutableInfo mtblsummary = null;
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		if(0<mtbllist.size()) {
			mtbl = mtbllist.get(0);
			mtbl.tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);

			if(2<=mtbllist.size())
				mtblsummary = mtbllist.get(1);
		}
		TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtblsummary.tablemasterinfoid, loginDataDto.getTrackdataid());
		TablemasterInfo master = tablemasterInfoService.findById(mtblsummary.tablemasterinfoid);
		boolean isTraining = false;
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			isTraining = trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
		}

		//
		// 集計表を生成
		//
		try {
			if(mtbl!=null) {
				// 集計対象の外部リストを取得
				Map<String, String> metadataMap = externallistService.getMetadataMap(menuid, isTraining);

				// 集計表を生成
				ListDto sublistDto = externallistService.createSummaryExternalList(menuid, metadataMap, mtbl, false, timeParam, isTraining)
						.deleteDisasternumRow();
				sublistDto.styleId = "summarylist";
				sublistDto.title = lang.__("Aggregation table");

				// 集計リスト項目情報の取得
				List<SummarylistcolumnInfo> slclist = summarylistcolumnInfoService.findByMenuid(menuid);
				for(int k = 0;k < slclist.size();k++){
					String condtion = slclist.get(k).condition;
					sublistDto.columnNames.add(condtion);
				}

				for(int i = 0;i < sublistDto.columnValues.size();i++){
					// 自治体コードを取得
					String localgovcodetag = sublistDto.columnValues.get(i).get(0);
					// htmlタグを外す
					String localgovcode = localgovcodetag.substring(localgovcodetag.indexOf(">")+1, localgovcodetag.indexOf("</a>"));
					String layerId = ttbl.layerid;
					LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
					Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
					vecLayerInfo.add(layerInfo);
					TrackmapInfo tmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());

					// 集計リスト情報の取得
					SummarylistInfo summarylistInfo = summarylistInfoService.findByTablemasterinfoId(mtblsummary.tablemasterinfoid);
					// 集計リスト情報から自治体コードが格納されている項目名の取得
					String sllocalgovcode = summarylistInfo.localgovcode;

					// featureIdを取得
					FeatureResultList fList = FeatureDB.searchFeatureBbox(null, tmapInfo.mapid, vecLayerInfo, null, sllocalgovcode+"="+localgovcode, 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, timeParam);
					FeatureResult fResult = fList.getResult(0);
					Long featureId = fResult.featureId;

					UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());
					HashMap<String, String> attribute = new HashMap<String, String>();

					for(int j = 0;j < slclist.size();j++){
						String attrid = slclist.get(j).attrid;
						// 集計結果を取得
						// 2番目以降が集計結果なので+1
						String value = sublistDto.columnValues.get(i).get(j+1);
						// feature属性の更新
						attribute.put(attrid, value);
						//属性更新日時をセット
						if (!StringUtil.isEmpty(master.updatecolumn))
							attribute.put(master.updatecolumn, time.toString());
						FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attribute, null);
					}
				}
			}

			listDtos.add(sublistDto);

			//
			// 総括表を生成
			//
			if(mtbl!=null) {
				listDto = externallistService.createGeneralizationExternalList(menuid, mtbl, false, timeParam, isTraining);
				listDto.styleId = "generalizationlist";
				listDto.title = lang.__("Summary table");

				// デフォルトソートをパラメータから引き継ぐ
				if (StringUtil.isNotEmpty(listForm.sort) && !listForm.sort.equals("undefined") && !listForm.sort.equals("-1"))
					listDto.defsort = listForm.sort;

				listDtos.add(listDto);
			}
			// 最終更新日時
			if (ttbl != null && !StringUtil.isEmpty(master.updatecolumn)) {
				Timestamp lasttime =null;
				//eコミマップのレイヤ
				lasttime = tableService.getEcomDataLastUpdateTime(ttbl.tablename, master.updatecolumn, timeParam);
				setUpdateTime(lasttime);
			}

		}catch(Exception e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display list."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}
		setupModel(model);
		return "/page/"+ExternallistAction.PAGE_TYPE+"/reload.jsp";
	}

	/**
	 * 体制総括表カラムを初期化
	 * @param mtbl
	 */
	public void initializeStationGeneralizationTablelistcolumnInfos(MenutableInfo mtbl) {
		// 体制レイヤ情報があれば、カラムを固定に変更する
		StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if(stationlayerInfo!=null) {

			// 体制レイヤ情報を取得
			LayerInfo layerInfo = null;
			String stationlayerId = stationlayerInfo.tablemasterInfo.layerid;
			if(StringUtil.isNotEmpty(stationlayerId)) {
				layerInfo = MapDB.getMapDB().getLayerInfo(stationlayerId);
			}

			// 列を定義しなおす
			if(mtbl.tablelistcolumnInfoList!=null) mtbl.tablelistcolumnInfoList.clear();
			else mtbl.tablelistcolumnInfoList = new ArrayList<TablelistcolumnInfo>();

			// 体制の列
			{
				// 体制の項目を設定
				TablelistcolumnInfo column = new TablelistcolumnInfo();
				column.attrid = stationlayerInfo.stationclassattrid;
				// マッチング用名称
				column.name = STATIONCLASS_ROW_NAME();
				// 属性名称が得られれば、属性名称でマッチングさせる
				if(StringUtil.isNotEmpty(column.attrid)) {
					AttrInfo attrInfo = layerInfo.getAttrInfo(column.attrid);
					if(attrInfo==null) {
						logger.warn(lang.__("{0} attribute : {1} of system layer not found.", column.name, column.attrid));
					}
					else {
						column.name = attrInfo.name;
					}
				}
				mtbl.tablelistcolumnInfoList.add(column);
			}

			// 発令日時の列
			{
				// 発令日時の項目を設定
				TablelistcolumnInfo column = new TablelistcolumnInfo();
				column.attrid = stationlayerInfo.shifttimeattrid;
				// マッチング用名称
				column.name = SHIFTTIME_ROW_NAME();
				// 属性名称が得られれば、属性名称でマッチングさせる
				if(StringUtil.isNotEmpty(column.attrid)) {
					AttrInfo attrInfo = layerInfo.getAttrInfo(column.attrid);
					if(attrInfo==null) {
						logger.warn(lang.__("{0} attribute : {1} of system layer not found.", column.name, column.attrid));
					}
					else {
						column.name = attrInfo.name;
					}
				}
				mtbl.tablelistcolumnInfoList.add(column);
			}
		}
	}

	/**
	 * タブレイアウトで表示
	 * @return フォワード先
	 */
	public String getTabContent() {
		final long menuid = pageDto.getMenuInfo().id;

		// 災害グループを取得する
		List<TrackgroupData> trackgroupDatas = null;
		if(Localgovtype.PREF.equals(loginDataDto.getLocalgovInfo().localgovtypeid)) {
			trackgroupDatas = trackgroupDataService.findByPreftrackdataid(loginDataDto.getTrackdataid());
		}

		// 同じDB上にある外部リストのメタデータを取得
		Map<Long, ExternaltabledataInfo> map = externallistService.getExternaltabledataInfoMap(menuid);

		// 総括表タブを追加
		{
			// リクエストパラメータ
			List<String> params = new ArrayList<String>();
			params.add("metaid="+ExternallistService.METADATA_GENERALIZATION);
			params.add("menuid="+menuid);
			params.add("menutaskid="+listForm.menutaskid);
			params.add("filterTrackgroup="+Boolean.FALSE); // 災害グループでフィルタしない
			// タブ追加
			addPageTab(lang.__("Summary table"), SpringContext.getRequest().getContextPath()+("/page/"+ExternallistAction.PAGE_TYPE+"/content?") + StringUtils.join(params, "&"));
		}

		// 集計項目の設定があれば、集計表タブを追加
		if(0<summarylistcolumnInfoService.getCountBySummarylistinfoid(menuid)) { // FIXME summarylistinfoid に直す
			// リクエストパラメータ
			List<String> params = new ArrayList<String>();
			params.add("metaid="+ExternallistService.METADATA_SUMMARY);
			params.add("menuid="+menuid);
			params.add("menutaskid="+listForm.menutaskid);
			params.add("filterTrackgroup="+Boolean.FALSE); // 災害グループでフィルタしない
			params.add("onlyTotale="+Boolean.TRUE); // 合計行のみ表示
			// タブ追加
			addPageTab(lang.__("Aggregation table"), SpringContext.getRequest().getContextPath()+("/page/"+ExternallistAction.PAGE_TYPE+"/content?") + StringUtils.join(params, "&"));
		}

		// 内部リストタブを追加
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		// とりあえずタブは１つだけ
		if(0<mtbllist.size()) {
			// リクエストパラメータ
			List<String> params = new ArrayList<String>();
			params.add("metaid="+mtbllist.get(0).id);
			params.add("menuid="+menuid);
			params.add("menutaskid="+listForm.menutaskid);
			params.add("filterTrackgroup="+Boolean.FALSE); // 災害グループでフィルタしない
			// 内部タブ追加
			String localgovName = loginDataDto.getLocalgovInfo().pref;
			if(Localgovtype.CITY.equals(loginDataDto.getLocalgovInfo().localgovtypeid)) localgovName = loginDataDto.getLocalgovInfo().city;
			addPageTab(localgovName, SpringContext.getRequest().getContextPath()+("/page/"+ExternallistAction.PAGE_TYPE+"/content?") + StringUtils.join(params, "&"));
		}

		// 災害グループの各自治体の外部リストタブを追加
		if(trackgroupDatas!=null) {
			for(TrackgroupData trackgroupData : trackgroupDatas) {
				// リクエストパラメータ
				List<String> params = new ArrayList<String>();
				LocalgovInfo cityLocalgovInfo = trackgroupData.cityTrackData.localgovInfo;
				ExternaltabledataInfo externaltabledataInfo = map.get(cityLocalgovInfo.id);
				if(externaltabledataInfo!=null) {
					params.add("metaid="+externaltabledataInfo.metadataid);
					params.add("menuid="+menuid);
					params.add("menutaskid="+listForm.menutaskid);
					params.add("trackdataid="+trackgroupData.citytrackdataid);
					params.add("filterTrackgroup="+Boolean.FALSE); // 災害グループでフィルタしない
				}

				// 外部リストタブ追加
				String localgovName = cityLocalgovInfo.pref;
				if(Localgovtype.CITY.equals(cityLocalgovInfo.localgovtypeid)) localgovName = cityLocalgovInfo.city;
				addPageTab(localgovName+lang.__("(External list)"), SpringContext.getRequest().getContextPath()+("/page/"+ExternallistAction.PAGE_TYPE+"/content?") + StringUtils.join(params, "&"));
			}
		}

		return "index.jsp";
	}

	/**
	 * リストに表示されているレコードをCSV出力
	 * テーブル全体ではなく表示されている項目のみ
	 * フィルタリングがかかっている場合は、表示対象のレコードのみ
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/stationGeneralization/outputcsv")
	public String outputcsv(HttpServletResponse res, Map<String,Object>model,
			@Valid @ModelAttribute ListForm listForm, BindingResult bindingResult){
		this.listForm = listForm;
		outputCSV(res, listForm.value, listForm.time);
		setupModel(model);
		return null;
	}

	/**
	 * リストに表示されているレコードをPDF出力
	 * テーブル全体ではなく表示されている項目のみ
	 * フィルタリングがかかっている場合は、表示対象のレコードのみ
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/stationGeneralization/outputpdf")
	public String outputpdf(HttpServletResponse res, Map<String,Object>model,
			@Valid @ModelAttribute ListForm listForm, BindingResult bindingResult){
		this.listForm = listForm;
		long menuid = listForm.menuid;
		long taskid = listForm.menutaskid;

		MenuInfo menuInfo = menuInfoService.findById(menuid);
		MenutaskInfo menutaskInfo = menutaskInfoService.findById(taskid);
		MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(menutaskInfo.menuprocessinfoid);
		String menuprocessName = menuprocessInfo.name;
		String menutaskName = menutaskInfo.name;
		String menuName = menuInfo.name;

		outputListPdf(res, listForm.time, menuprocessName, menutaskName, menuName, listForm.dataList, listForm.totalable);

		setupModel(model);
		return null;
	}
}
