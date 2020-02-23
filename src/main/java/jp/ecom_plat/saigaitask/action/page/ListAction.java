/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.postgis.PGgeometry;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.FilteredFeatureId;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.map.util.GeometryUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.FilterState;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.dto.FilterDto;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.ListEditDto;
import jp.ecom_plat.saigaitask.dto.SlimerDto;
import jp.ecom_plat.saigaitask.entity.db.DatatransferInfo;
import jp.ecom_plat.saigaitask.entity.db.FilterInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistkarteInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablerowstyleInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.FileForm;
import jp.ecom_plat.saigaitask.form.page.ListForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.DataTransferService;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.EvalService;
import jp.ecom_plat.saigaitask.service.ExternallistService;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.TableFeatureService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.AutocompleteDataService;
import jp.ecom_plat.saigaitask.service.db.AutocompleteInfoService;
import jp.ecom_plat.saigaitask.service.db.DatatransferInfoService;
import jp.ecom_plat.saigaitask.service.db.DecisionsupportInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculateInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculatecolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecolumnMasterService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistkarteInfoService;
import jp.ecom_plat.saigaitask.service.db.TablerowstyleInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.HttpUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;


/**
 * リストページを表示するアクションクラスです.
 * spring checked take 5/10
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/page/list")
public class ListAction extends AbstractPageAction {

	protected ListForm listForm;

	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;
	@Resource
	protected TablerowstyleInfoService tablerowstyleInfoService;
	@Resource
	protected LayerService layerService;
	@Resource
	protected MapService mapService;
	@Resource
	protected TablecolumnMasterService tablecolumnMasterService;
	/** アップロードサービス */
	@Resource
	protected FileUploadService fileUploadService;
	@Resource
	protected HistoryTableService historyTableService;
	@Resource
	protected TableFeatureService tableFeatureService;
	@Resource
	protected ClearinghouseService clearinghouseService;
	@Resource
	protected AutocompleteInfoService autocompleteInfoService;
	@Resource
	protected AutocompleteDataService autocompleteDataService;
	@Resource
	protected TablecalculateInfoService tablecalculateInfoService;
	@Resource
	protected TablecalculatecolumnInfoService tablecalculatecolumnInfoService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected EvalService evalService;
	@Resource
	protected ExternallistService externallistService;
	@Resource
	protected TrackdataidAttrService trackdataidAttrService;
	@Resource
	protected EdituserAttrService edituserAttrService;
	@Resource
	protected DecisionsupportInfoService decisionsupportInfoService;
	@Resource
	protected SummarylistInfoService summarylistInfoService;
	@Resource
	protected SummarylistcolumnInfoService summarylistcolumnInfoService;
	@Resource
    protected UserTransaction userTransaction;
	@Resource
	protected TablelistkarteInfoService tablelistkarteInfoService;
	@Resource
	protected DataTransferService dataTransferService;
	@Resource
	protected DatatransferInfoService datatransferInfoService;
	/** 一括変更用Dto */
	@Resource
	public SlimerDto slimerDto;

	/** 項目名リスト */
	public List<TablelistcolumnInfo> colinfoItems;
	/** 検索結果（表示対象） */
	public List<BeanMap> result;
	/** 最初のテーブルフラグ（集計リストは２件目になる） */
	public Boolean firsttable;
	/** テーブル名 */
	public String table;
	/** レコードのキーの項目名 */
	public String key;
	/** 編集時のアイテムのクラス */
	public String[] editClass;
	/** 選択形式Map */
	public Map<String, String[]> selectStr = new TreeMap<String, String[]>();
	/** 選択形式値Map */
	public Map<String, String[]> selectVal = new TreeMap<String, String[]>();
	/** 選択形式、編集できない画面での表示用値Map */
	public Map<String, Map<String,String>> selectValView = new TreeMap<String, Map<String,String>>();
	/** チェックボックスMap */
	public Map<String, String> checkStr = new HashMap<String, String>();
	/** スタイルMap */
	public Map<Long, List<TablerowstyleInfo>> styleMap = new HashMap<Long, List<TablerowstyleInfo>>();
	/** 必須項目チェック用Map */
	public Map<String, Boolean> colMap = new HashMap<String, Boolean>();
	/** レコード追加フラグ */
	public Boolean addable;
	/** レコード削除フラグ */
	public Boolean deletable;
	/** 編集可フラグ */
	public Boolean editable = false;
	/** 合計フラグ */
	public Boolean totalable = false;
	/** 合計値用 */
	public Map<String,String> sumItems = new HashMap<String,String>();
	/** デフォルトソート */
	public String defsort = null;
	/** 項目名リスト */
	public List<TablelistcolumnInfo> attridItems;
	/** データ件数 */
	public int count;

	/** フィルタの条件や結果などのDto */
	public FilterDto filterDto = null;
	/** カルテ項目名リスト */
	public List<TablelistkarteInfo> kartecolinfoItems = new ArrayList<TablelistkarteInfo>();
	/** テーブルマスタ情報 */
	public TablemasterInfo master;
	/** メニューテーブル情報 */
	public MenutableInfo mtbl;

	/** フィルタリングの結果をIDと共に格納
	 * key：id
	 * value：表示対象の場合はtrue、非表示対象の場合はfalse
	 * */
	public Map<String, Boolean> filterIds = new HashMap<String, Boolean>();
	/** フィルタ時のクラス名を格納 */
	public Map<String, String> filterClasses = new HashMap<String, String>();

	/**
	 * 意思決定支援の避難者推計結果をIDと共に格納
	 * key : id
	 * value : 表示対象の場合はtrue、非表示対象の場合はfalse
	 */
	/** 意思決定支援用フィルタ時のクラス名を格納 */
	public Map<String, Boolean> decisionIds = new HashMap<String, Boolean>();
	public String decisionUpdateTime = null;

	/** フィルタリングフラグ */
	public Boolean filtering = false;
	public String filtername = "";

	/**
	 * 外部地図のメタデータMap.
	 */
	public Map<String, String> metadataMap;

	/** ページング情報 */
	public Boolean paging = false;
	public Boolean nextpage = false;

	/** ページ種別 */
	public static final String PAGE_TYPE = "list";


	/** ファイルの有無チェック用Map */
	public Map<String, Boolean>fileexist = new HashMap<String, Boolean>();

	/** eコミマップURL */
	public String ecommapURL;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("slimerDto", slimerDto);
		model.put("colinfoItems", colinfoItems);
		model.put("result", result);
		model.put("firsttable", firsttable);
		model.put("table", table);
		model.put("key", key);
		model.put("editClass", editClass);
		model.put("selectStr", selectStr);
		model.put("selectVal", selectVal);
		model.put("selectValView", selectValView);
		model.put("checkStr", checkStr);
		model.put("styleMap", styleMap);
		model.put("colMap", colMap);
		model.put("addable", addable);
		model.put("deletable", deletable);
		model.put("editable", editable);
		model.put("totalable", totalable);
		model.put("sumItems", sumItems);
		model.put("defsort", defsort);
		model.put("attridItems", attridItems);
		model.put("count", count);
		model.put("filterDto", filterDto);
		model.put("kartecolinfoItems", kartecolinfoItems);
		model.put("master", master);
		model.put("mtbl", mtbl);
		model.put("filterIds", filterIds);
		model.put("filterClasses", filterClasses);
		model.put("decisionIds", decisionIds);
		model.put("decisionUpdateTime", decisionUpdateTime);
		model.put("filtering", filtering);
		model.put("filtername", filtername);
		model.put("metadataMap", metadataMap);
		model.put("paging", paging);
		model.put("nextpage", nextpage);
		model.put("fileexist", fileexist);
		model.put("ecommapURL", ecommapURL);
		model.put("listForm", listForm);
	}

	/**
	* リストページを表示する.
	 * @return フォワード先 リストページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult) {
		this.listForm = listForm;
		initPage(PAGE_TYPE, listForm);
		pageDto.setEnableFullscreen(true);

		long menuid = pageDto.getMenuInfo().id;
		long taskid = pageDto.getMenutaskInfo().id;
		try {
			content(taskid, menuid);
		} catch(ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display list."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);

		return "/page/list/index";
	}

	/**
	 * リストの内容を取得する。
	 * @param taskid タスクID
	 * @param menuid メニューID
	 */
	protected void content(long taskid, long menuid) {
		content(taskid, menuid, 0);
	}

	/**
	 * データメンバの初期化
	 */
	protected void initParam() {
		colinfoItems = new ArrayList<TablelistcolumnInfo>();
		sumItems = new HashMap<String,String>();
		//subsumItems = new HashMap<String,Map<String, String>>();
		//subsumAttrId = null;
		defsort = "";
		paging = false;
		nextpage = false;
		editable = false;
		result = new ArrayList<BeanMap>();
		selectStr = new TreeMap<String, String[]>();
		selectVal = new TreeMap<String, String[]>();
		selectValView = new TreeMap<String, Map<String,String>>();
		checkStr = new HashMap<String, String>();
		styleMap = new HashMap<Long, List<TablerowstyleInfo>>();
		colMap = new HashMap<String, Boolean>();
		colinfoItems = null;
		count = 0;
		filterDto = null;
		kartecolinfoItems = new ArrayList<TablelistkarteInfo>();
		filterIds = new HashMap<String, Boolean>();
		filterClasses = new HashMap<String, String>();
		filtering = false;
		filtername = "";
	}

	/**
	 * リストの内容を取得する。
	 * @param taskid タスクID
	 * @param menuid メニューID
	 */
	protected void content(long taskid, long menuid,long fid) {

		initParam();
		ecommapURL = Config.getEcommapURL()+"map";

		// ログイン中の記録データ
		final TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());

		boolean isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;

		// 複合災害対応により、記録データ内にある地図情報IDを使う
		TrackmapInfo trackmapInfo = null;
		boolean isMultiDisaster = false;
		if(trackData!=null) {
			if(trackData.trackmapinfoid==null) throw new ServiceException(lang.__("Disaster map info is not found."));
			trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackData.trackmapinfoid);
			isMultiDisaster = 1 < trackmapInfo.trackDatas.size();
		}

		// フィルタ検索だとFormに追加されないので、パラメータに含めて受け渡す
		String decisionsupportStr = request.getParameter("decisionsupport");
		if(decisionsupportStr != null && !decisionsupportStr.equals("")){
			pageDto.setDecisionSupport(decisionsupportStr.equals("true") ? true : false);
		}

		// メニューテーブル情報をDBから取得
		listForm.menuid = menuid;
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		//テーブル取得
		List<TracktableInfo> ttbllist = new ArrayList<TracktableInfo>();
		for (MenutableInfo mtbl : mtbllist) {
			// 削除フラグが設定されていれば何もしない
			if(mtbl.deleted) continue;
			//TablemasterInfo tmi = tablemasterInfoService.findById(mtbl.tablemasterinfoid);
			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());
			if(ttbl != null)ttbllist.add(ttbl);
			// 記録テーブル情報がみつからない
			else {
				// 災害時に記録テーブル情報がみつからない
				if(0<loginDataDto.getTrackdataid()) {
					// TODO: 対応中の災害であればコピーする
					// 過去の災害であれば、エラーメッセージを表示
					TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(mtbl.tablemasterinfoid);
					ActionMessages errors = new ActionMessages();
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("\""+tablemasterInfo.name+"\"" + lang.__("Table not found in this disaster."), false));
					ActionMessagesUtil.addErrors(session, errors);
				}
			}
		}
		//TODO:とりあえずテーブル一つから。
		//属性設定取得
		//for (TracktableInfo ttbl : ttbllist) {
		//	TablelistcolumnInfo cinfo = tablelistcolumnInfoService.findByMenuInfoIdAndTablemasterInfoId(menuid, ttbl.tablemasterinfoid);
		//}
		if (ttbllist.size() == 0 && loginDataDto.getTrackdataid() > 0 || mtbllist.size() == 0) {
			// 前に表示したメニューテーブルのデータが残ったままの場合があるので初期化する
			result = new ArrayList<BeanMap>();
			colinfoItems = new ArrayList<TablelistcolumnInfo>();
			totalable = false;
			return ;
		}

		firsttable = true;
		TracktableInfo ttbl = null;
		mtbl = mtbllist.get(0);
		if (ttbllist.size() > 0){
			ttbl = ttbllist.get(0);
			String metaid = listForm.metaid;
			if(metaid != null){
				for(int i = 0; i < ttbllist.size();i++){
					if(0<i) firsttable = false;
					try{
						String listidstr = metaid.substring(metaid.indexOf("-")+1, metaid.length());
						int listid = Integer.parseInt(listidstr);
						ttbl = ttbllist.get(listid);
						mtbl = mtbllist.get(listid);
					}catch(NumberFormatException e){
						//特に何もしない
					}
				}
			}else{
				ttbl = ttbllist.get(0);
				mtbl = mtbllist.get(0);
			}
		}
		master = tablemasterInfoService.findById(mtbl.tablemasterinfoid);
		if (ttbl == null /*&& loginDataDto.isMaster() && !pageDto.isViewMode()*/) {//マスタ確認モード
			ttbl = new TracktableInfo();
			ttbl.tablemasterinfoid = master.id;
			ttbl.layerid = master.layerid;
			ttbl.tablename = master.tablename;
			ttbl.trackmapinfoid = 0L;
		}

		// 最終更新日時
		if (ttbl != null && !StringUtil.isEmpty(master.updatecolumn)) {
			Timestamp lasttime =null;
			//eコミマップのレイヤ
			if (ttbl.layerid != null && !ttbl.layerid.isEmpty()) {
				lasttime = tableService.getEcomDataLastUpdateTime(ttbl.tablename, master.updatecolumn, listForm.timeParams());
			//eコミマップでないレイヤ
			}else{
				lasttime = tableService.getSystemDataLastUpdateTime(ttbl.tablename, master.updatecolumn, loginDataDto.getTrackdataid());
			}
			setUpdateTime(lasttime);
		}

		// 列情報をDBから取得
		colinfoItems = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
		// 地図に記録データが複数あるなら、trackdataid属性を表示させる
		if(isMultiDisaster) {
			boolean find = false;
			for(TablelistcolumnInfo tablelistcolumnInfo : colinfoItems) {
				if(TrackdataidAttrService.TRACKDATA_ATTR_ID.equals(tablelistcolumnInfo.attrid)) {
					find = true;
					break;
				}
			}
			if(!find) {
				TablelistcolumnInfo tablelistcolumnInfo = trackdataidAttrService.createTablelistcolumnInfo(colinfoItems);
				colinfoItems.add(tablelistcolumnInfo);
			}
		}

		//一覧表示するテーブルと結果の取得
		if (ttbl != null)
			table = ttbl.tablename;
		else
			table = master.tablename;

		//データ追加フラグ
		addable = (mtbl.addable && loginDataDto.isEdiable());
		if(addable==null)addable=false;//設定されていない場合はfalse
		deletable = (mtbl.deletable && loginDataDto.isEdiable());
		if (deletable == null) deletable=false;//設定されていない場合はfalse
		if (ttbl == null) addable=false;
		totalable = (mtbl.totalable==null)?Boolean.valueOf(false):mtbl.totalable;
		int pagerow = pageDto.getPagerow();
		if (!master.paging) pagerow = 0;

		// 列情報を設定
		LayerInfo linfo = null;
		// 平時のテーブル情報を取得
		String layerid = master.layerid;
		String tablename = master.tablename;
		// 災害のテーブル情報があれば取得
		if (ttbl!=null) {
			layerid = ttbl.layerid;
			tablename = ttbl.tablename;
		}
		//eコミマップのレイヤ
		if (StringUtil.isNotEmpty(layerid)) {
			linfo = layerService.getLayerInfo(layerid);
			if(isMultiDisaster) {
				trackdataidAttrService.overrideTrackdataidAttr(linfo, trackData.id, trackmapInfo.trackDatas);
			}
			if(linfo==null) {
				ActionMessages errors = new ActionMessages();
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Layer ({0}) not found.", layerid), false));
				ActionMessagesUtil.addErrors(session, errors);
			}
			else {
				key = LayerInfo.TimeSeriesType.HISTORY.equals(linfo.timeSeriesType) ? "_orgid" : "gid";

				// 一括変更
				//項目チェック
				for (int i = colinfoItems.size()-1; i >= 0; i--) {
					TablelistcolumnInfo cinfo = colinfoItems.get(i);
					AttrInfo attr = linfo.getAttrInfo(cinfo.attrid);
					if (attr == null) colinfoItems.remove(i);
				}
				editClass = new String[colinfoItems.size()];
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo cinfo = colinfoItems.get(i);
					AttrInfo attr = linfo.getAttrInfo(cinfo.attrid);
					// 編集可能な属性が１つでもあれば、編集フラグを立てる
					if (cinfo.editable) editable = true;
					// 一括変更情報に追加
					if(attr!=null) {
						mapService.addEcomColumn(slimerDto, linfo, attr, cinfo.editable, cinfo.grouping, cinfo.defaultcheck, cinfo.groupdefaultcheck, cinfo.addable);
						try {
							JSONObject column = slimerDto.getColumn(linfo.layerId, cinfo.attrid);
							if(column!=null) {
								// データ型
								String dataType = null;
								if(column.has("dataType")) dataType = editClass[i] = column.getString("dataType");

								// 型に応じた値
								if(StringUtil.isNotEmpty(dataType)) {
									if("Select".equals(dataType)) {
										Map<String, String> selMap = new HashMap<String, String>();
										selectValView.put(attr.attrId, selMap);
										if(column.has("selectOptions")) {
											JSONArray selectOptions = column.getJSONArray("selectOptions");
											String[] selectStrs = new String[selectOptions.length()];
											String[] selectVals = new String[selectOptions.length()];
											for(int selectOptionIdx=0; selectOptionIdx<selectOptions.length(); selectOptionIdx++) {
												JSONObject selectOption = selectOptions.getJSONObject(selectOptionIdx);
												selectStrs[selectOptionIdx] = selectOption.getString("text");
												selectVals[selectOptionIdx] = selectOption.getString("value");
												selMap.put(selectOption.getString("value"), selectOption.getString("text"));
											}
											//eコミマップではSelectの選択肢と値は同じ
											selectStr.put(cinfo.attrid, selectStrs);
											selectVal.put(cinfo.attrid, selectVals);
										}
									}
									else if("Checkbox".equals(dataType)) {
										if(column.has("checkDisplay")) {
											String checkDisplay = column.getString("checkDisplay");
											checkStr.put(cinfo.attrid, checkDisplay);
										}
									}
								}

								//必須項目対応
								if(column.has("nullable")) {
									boolean nullable = column.getBoolean("nullable");
									colMap.put(cinfo.attrid, !nullable);
								}
							}
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}

			pageDto.addTimesliderConf(master.name, layerid);
			//eコミマップでないレイヤ
		}else{
			key = "id";
			editClass = new String[colinfoItems.size()];
			for (int i = 0; i < colinfoItems.size(); i++) {
				TablelistcolumnInfo cinfo = colinfoItems.get(i);
				String colName = cinfo.attrid;//カラム名取得
				if (cinfo.editable) editable = true;
				String type = tableService.getAttrType(tablename, colName);
				if (type == null) throw new ServiceException(lang.__("Check attribute item [{0}] settings.", colName));
				// trackdataid
				if(TrackdataidAttrService.TRACKDATA_ATTR_ID.equals(colName)) {
					editClass[i] = "Select";
					JSONArray dataExp = new JSONArray();
					if(trackData!=null && trackmapInfo!=null) {
						dataExp = trackdataidAttrService.getTrackdataidAttrDataExp(trackData.id, trackmapInfo.trackDatas);
					}
					List<String> selectStrs = new ArrayList<String>();
					List<String> selectVals = new ArrayList<String>();
					Map<String, String> selMap = new HashMap<String, String>();
					selectValView.put(colName, selMap);
					for(int idx=0; idx<dataExp.length(); idx++) {
						try {
							JSONObject map = dataExp.getJSONObject(idx);
							if(map.keys().hasNext()) {
								String trackdataid = map.keys().next().toString();
								String trackdataName = map.getString(trackdataid);
								selectStrs.add(trackdataName);
								selectVals.add(trackdataid);
								selMap.put(trackdataid, trackdataName);
							}
						} catch(Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
					selectStr.put(colName, selectStrs.toArray(new String[selectStrs.size()]));
					selectVal.put(colName, selectVals.toArray(new String[selectVals.size()]));
				}
				//カラム名の末尾が「○○○id」の場合は「○○○_master」テーブルの「name」カラムのデータを取得して、Selectで選択させる
				else if(TableService.isMasterColumn(colName)){
					editClass[i] = "Select";
					//指定したmasterテーブルがあれば、そのnameカラムをSelectの選択肢として追加
					Map<String, String> selMap = tableService.setRelTable("master", colName, selectStr, selectVal);
					//表示用のMap格納
					selectValView.put(colName, selMap);

					//カラム名の末尾が「infoid」の場合は「○○○_info」テーブルの「name」カラムのデータを取得して、Selectで選択させる
				}else if(TableService.isInfoColumn(colName)){
					editClass[i] = "Select";
					//指定したinfoテーブルがあれば、そのnameカラムをSelectの選択肢として追加
					Map<String, String> selMap = tableService.setRelTable("info", colName, selectStr, selectVal);
					//表示用のMap格納
					selectValView.put(colName, selMap);

					//アップロードカラム
				}else if (cinfo.uploadable && type.equals(Constants.DATABASE_COLTYPE_TEXT)){
					editClass[i] = "Upload";
					//その他のカラム型に応じたeditClassの指定
				}else{
					//数値
					if(type.equals(Constants.DATABASE_COLTYPE_INT) || type.equals(Constants.DATABASE_COLTYPE_BIGINT)){
						editClass[i] = "Number";
						//小数
					}else if(type.equals(Constants.DATABASE_COLTYPE_FLOAT) || type.equals(Constants.DATABASE_COLTYPE_DOUBLE)){
						editClass[i] = "Float";
						//文字列
					}else if(type.equals(Constants.DATABASE_COLTYPE_TEXT)){
						editClass[i] = "String";
						//timestamp
					}else if(type.equals(Constants.DATABASE_COLTYPE_TIMESTAMP)){
						editClass[i] = "DateTime";
						//それ以外
					}else{
						//とりあえず文字列
						editClass[i] = "String";
					}
				}
				//必須項目対応
				if(tablecolumnMasterService.checkNullable(tablename, cinfo.attrid)){//nullでもOK
					colMap.put(cinfo.attrid, false);
				}else{//必須
					colMap.put(cinfo.attrid, true);
				}
				colMap.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, true);

				// 一括変更カラムへの追加
				if(slimerDto!=null) {
					Map<String, String> selectOptions = null;
					String dataType = editClass[i];
					if("Select".equals(dataType)) {
						selectOptions = new HashMap<String, String>();
						String[] selectStrs = selectStr.get(cinfo.attrid);
						String[] selectVals = selectVal.get(cinfo.attrid);
						if(selectStrs!=null) {
							for(int idx=0; idx<selectStrs.length; idx++) {
								selectOptions.put(selectStrs[idx], selectVals[idx]);
							}
						}
					}
					slimerDto.addColumn(table, cinfo.name, cinfo.attrid, dataType, selectOptions, null, cinfo.editable, colMap.get(cinfo.attrid), cinfo.defaultcheck, cinfo.groupdefaultcheck, cinfo.addable);
					slimerDto.setKey(table, "id");
					//グルーピング対応
					if(cinfo.grouping){
						List<String> groupList = tableService.selectDistinctByTrackdataid(tablename, cinfo.attrid, loginDataDto.getTrackdataid());
						//キーは「カラム名:カラム日本語名称」とする
						if (groupList.contains(null) && groupList.contains(""))
							groupList.remove(null);

						Map<String, String> gSelectOptions = new LinkedHashMap<String, String>();
						for(String groupValue : groupList) {
							if(StringUtil.isNotEmpty(groupValue)) {
								String groupText = groupValue;
								if("Select".equals(dataType)) {
									groupText = selectValView.get(colName).get(groupValue);
								}
								gSelectOptions.put(groupText, groupValue);
							}
						}

						slimerDto.addGrouping(table, cinfo.name, cinfo.attrid, dataType, gSelectOptions, cinfo.groupdefaultcheck);
					}
				}
			}
		}

		//セルのスタイル
		for (int i = 0; i < colinfoItems.size(); i++) {
			TablelistcolumnInfo cinfo = colinfoItems.get(i);
			List<TablerowstyleInfo> stylelist = tablerowstyleInfoService.findByTablelistcolumnInfoId(cinfo.id);
			if (stylelist != null && stylelist.size() > 0)
				styleMap.put(cinfo.id, stylelist);
			if (cinfo.defaultsort >= 0) {
				int add = 1;
				if (mtbl.deletable) add++;
				defsort = ""+(i+add)+","+cinfo.defaultsort+","+cinfo.attrid;
				//listForm.sort = ""+(i+add)+","+cinfo.defaultsort;
			}
			//座標の場合は編集不可
			if (StringUtil.isNotEmpty(master.coordinatecolumn) && cinfo.attrid.equals(master.coordinatecolumn)) {
				cinfo.editable = false;
				//cinfo.highlight = false;
			}
		}

		FilterInfo filterInfo = null;
		if(pageDto.getMenuInfo().filterInfoList==null) {
			pageDto.getMenuInfo().filterInfoList = filterInfoService.findByMenuid(pageDto.getMenuInfo().id);
		}
		//if(0<pageDto.getMenuInfo().filterInfoList.size()) filterInfo = pageDto.getMenuInfo().filterInfoList.get(0);
		// デフォルトはフィルター解除
		if(0<pageDto.getMenuInfo().filterInfoList.size()) filterInfo = mapService.createNothingFilterInfo(pageDto.getMenuInfo().id);

		//フィルタリング対応
		filtering = false;
		List<Long> filterfeatureIds = null;
		List<String> ids = new ArrayList<String>();
		int filterstateid = -1;

		// 属性検索のロード？
		try {
			if (fid == 0l && loginDataDto.getMenuConditionMap().get(pageDto.getMenuInfo().id) != null) {
				listForm.conditionValue = new JSONObject(loginDataDto.getMenuConditionMap().get(pageDto.getMenuInfo().id));
				Long filid = loginDataDto.getMenuFilterIdMap().get(pageDto.getMenuInfo().id);
				if (filid != null && filid > 0) {
					filterInfo = filterInfoService.findById(filid);
				}
				else
					filterstateid = FilterState.FILTEROFF;
			}
		} catch (JSONException e) {e.printStackTrace();}

		// idsを取得して、StringのListに加工する
		//eコミマップのレイヤ
		if (fid == 0l && ttbl != null && master.layerid != null && !master.layerid.isEmpty()) {
			String filterid = request.getParameter("filterid");
			if (filterid != null && !filterid.equals("0")) {
				try {
					if (listForm.conditionValue != null && listForm.conditionValue.has("filterinfoid") && listForm.conditionValue.getLong("filterinfoid") == Long.parseLong(filterid))
						filterDto = mapService.filter(listForm.conditionValue, listForm.timeParams());
					else {
						filterInfo = filterInfoService.findById(Long.parseLong(filterid));
						if (filterInfo != null)
							filterDto = mapService.filter(filterInfo, listForm.timeParams());
					}
				} catch (JSONException e) {e.printStackTrace();}
			}
			else
				filterDto = mapService.filter(listForm.conditionValue, listForm.timeParams());
			if(filterDto!=null) {
				filtering = true;
				listForm.conditionValue = filterDto.getConditionValue();
				if(mapService.isNoFilter(listForm.conditionValue)) {
					// フィルター解除
					filtering = false;
					filterDto.setFilterInfoId(0L);
				}
				else {
					filtername = filterDto.getFiltername();
					filterfeatureIds = filterDto.getFilteredFeatureIds();
					if (filterstateid > 0) filterDto.setFilterstateid(filterstateid);
					if (filterDto.getFilterInfoId() != null)
						loginDataDto.getMenuFilterIdMap().put(pageDto.getMenuInfo().id, filterDto.getFilterInfoId());
				}
			}

			if(filterfeatureIds!=null) {
				for(Long filterfeatureId : filterfeatureIds) {
					ids.add(""+filterfeatureId);
				}
			}
		}

		//データ数を取得
		if (!StringUtil.isEmpty(master.layerid))//eコミ
			count = tableService.getCount(table, listForm.timeParams()).intValue();
		else if (tableService.isExistsColumn(table, "trackdataid"))
			count = tableService.getCount(table, loginDataDto.getTrackdataid()).intValue();
		else if (tableService.isExistsColumn(table, "localgovinfoid"))
			count = tableService.getCount(table, loginDataDto.getLocalgovinfoid()).intValue();
		if (filterfeatureIds != null && filterfeatureIds.size() > 0 && filterDto == null) {//空間検索などの場合の処理 フィルタリングされているけど、Dtoはなし
			filterDto = new FilterDto();
			filterDto.setFilteredFeatureIds(filterfeatureIds);
			filterDto.setTotal((long)count);
			filterDto.setFilterstateid(FilterState.FILTEROFF);
		}
		if (filterfeatureIds != null && filterfeatureIds.size() > 0 && filterDto.getFilterstateid().equals(FilterState.FILTEROFF))
			count = filterfeatureIds.size();
		if (listForm.npage == -1 && pagerow > 0) {//最終ページ
			listForm.npage = (int)Math.ceil( (double)count / pagerow)-1;
		}
		listForm.pagesize = (int)Math.ceil( (double)count / pagerow);
		if (listForm.npage > 0) {
			int npage = (int)Math.ceil( (double)count / pagerow)-1;
			if (listForm.npage > npage) listForm.npage = npage;
		}
		String desc = "desc";
		for (TablelistcolumnInfo colinfo : colinfoItems) {
			if (colinfo.defaultsort != null) {
				if (colinfo.defaultsort.equals(0))
					desc = "asc";
				else if (colinfo.defaultsort.equals(1))
					desc = "desc";
			}
		}
		String sortval = key;
		if (linfo != null)
			sortval = LayerInfo.TimeSeriesType.HISTORY.equals(linfo.timeSeriesType) ? "_orgid" : "gid";
		int sortidx = 0;
		if (!sortempty(listForm.sort)) {
			String[] str = listForm.sort.split(",");
			if (str.length == 3) {
				if (str[1].equals("0"))//昇順降順
					desc = "asc";
				else desc = "desc";
				sortval = str[2];
				for (int i = 0; i < colinfoItems.size(); i++) {
					if (colinfoItems.get(i).attrid.equals(sortval)) {
						sortidx = i;
						break;
					}
				}
			}
			else
				System.out.println("sort text="+listForm.sort);
		}
		else if (StringUtil.isNotEmpty(defsort)) {
			String[] str = defsort.split(",");
			if (str[1].equals("0"))//昇順降順
				desc = "asc";
			sortval = str[2];
			for (int i = 0; i < colinfoItems.size(); i++) {
				if (colinfoItems.get(i).attrid.equals(sortval)) {
					sortidx = i;
					break;
				}
			}
		}

		//eコミマップのレイヤ：trackdataidは無い
		DecimalFormat df = new DecimalFormat("0.00000");
		if (!StringUtil.isEmpty(master.layerid)) {
			if (fid == 0l && (filtering && filterDto != null && filterDto.getFilterstateid().equals(FilterState.FILTEROFF))) {
				List<Long> featureIds = new ArrayList<Long>();
				if (filterfeatureIds != null) featureIds = filterfeatureIds;
				if (featureIds.size() == 0) featureIds.add(0l);
				if (editClass[sortidx].equals("Number") && !sortval.equals(key))
					result = tableService.selectByIdsSortCast(table, key, featureIds, desc, sortval, "int", listForm.npage*pagerow, pagerow+1, listForm.timeParams());
				else {
					String sortcol = sortval;
					if (!sortval.equals(key)) sortcol += ","+key;
					String sorttext = null;
					if (pagerow == 0)
						result = tableService.selectByIds(table, key, featureIds, desc, sortcol, null, 0, 0, listForm.timeParams());
					else
						result = tableService.selectByIds(table, key, featureIds, desc, sortcol, null, listForm.npage*pagerow, pagerow+1, listForm.timeParams());
				}
				featureIds.remove(0l);
			}
			else if(fid == 0){
				String sortSpec = key;
				// アコーディオンの指定がある場合は、最初にアコーディオン対象属性でソートする（PDF出力用）
				if (!StringUtil.isEmpty(mtbl.accordionattrid)) {
					sortSpec = "lower(nullif(trim(" + mtbl.accordionattrid + "), ''))," + key;
					defsort = "";//アコーディオンの場合はデフォルトソートなし
				}
				//result = tableService.selectAll(table, "desc", sortSpec, listForm.timeParams());
				if (sortempty(listForm.sort) && StringUtil.isEmpty(defsort)) {
					if (pagerow == 0)
						result = tableService.selectAll(table, desc, sortSpec, listForm.timeParams());
					else
						result = tableService.selectAll(table, desc, sortSpec, listForm.npage*pagerow, pagerow+1, listForm.timeParams());
				}
				else {
					if (pagerow == 0) {
						if (editClass[sortidx].equals("Number") && !sortval.equals(key))
							result = tableService.selectAllSortCast(table, desc, sortval, "int", listForm.timeParams());
						else
							result = tableService.selectAll(table, desc, sortval, listForm.timeParams());
					} else {
						if (editClass[sortidx].equals("Number") && !sortval.equals(key))
							result = tableService.selectAllSortCast(table, desc, sortval, "int", listForm.npage*pagerow, pagerow+1, listForm.timeParams());
						else {
							result = tableService.selectAll(table, desc, sortval, listForm.npage*pagerow, pagerow+1, listForm.timeParams());
						}
					}
				}
			}else{
				if(key=="_orgid") {
					long orgid = fid;
					//時系列管理時はfid入れ替え
					LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerid);
					if (layerInfo!=null && layerInfo.timeSeriesType == TimeSeriesType.HISTORY) {
						/*
						long historyId = ExMapDB.getHistoryFeatureId(layerInfo.layerId, orgid, listForm.timeParams());
						if (historyId > 0) fid = historyId;
						result = tableService.selectById(table, "gid", historyId);
						*/
						result = tableService.selectById(table, key, orgid, null, null, listForm.timeParams());
					}
				}
				else {
					result = tableService.selectById(table, key, fid);
				}
			}

			if (!StringUtil.isEmpty(master.coordinatecolumn)) {
				for (BeanMap map : result) {
					if (map.containsKey(master.coordinatecolumn)) {
						String coord = (String)map.get(master.coordinatecolumn);
						if (StringUtil.isNotEmpty(coord)) {
							int idx = coord.indexOf(',');
							if (idx > 0) {
								if (loginDataDto.getLocalgovInfo().coordinatedecimal) {
									double dlon = Double.parseDouble(coord.substring(1, idx));
									double dlat = Double.parseDouble(coord.substring(idx+1, coord.length()-1));
									map.put(master.coordinatecolumn, "("+df.format(dlon)+","+df.format(dlat)+")");
								} else {
									map.put(master.coordinatecolumn, to60Digree(coord));
								}
							}
						}
					}
				}
			}

			//合計値算出
			if (totalable && linfo!=null) {
				for (TablelistcolumnInfo cinfo : colinfoItems) {
					AttrInfo attr = linfo.getAttrInfo(cinfo.attrid);
					//Integer
					/*if(attr.dataType==AttrInfo.DATATYPE_INTEGER){
						long sum = 0;
						for(BeanMap map : result){
							String valStr = (String)map.get(cinfo.attrid);
							try {
								if(valStr!=null && valStr.length()>0)sum += Integer.parseInt(valStr);
							} catch ( NumberFormatException e) {}
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//Float
					}else*/ if(attr.dataType==AttrInfo.DATATYPE_FLOAT || attr.dataType==AttrInfo.DATATYPE_INTEGER){
						BigDecimal sum = new BigDecimal(0);
						if (!((result.size() > pagerow && pagerow > 0) || listForm.npage > 0)) {//
							for(BeanMap map : result){
								String valStr = (String)map.get(cinfo.attrid);
								BigDecimal num = new BigDecimal(0);
								try {
									if(valStr!=null && valStr.length()>0)num = new BigDecimal(valStr);
								} catch (NumberFormatException e) {}
								sum = sum.add(num);
							}
						}
						else {//ページングしているので、合計はDBから
							BigDecimal one = BigDecimal.ONE;
							if (attr.dataType==AttrInfo.DATATYPE_INTEGER)
								sum = tableService.getSum(table, cinfo.attrid, "int", listForm.timeParams());
							else if (attr.dataType==AttrInfo.DATATYPE_FLOAT) {
								sum = tableService.getSum(table, cinfo.attrid, "float", listForm.timeParams());
								//SQLで丸め誤差がでるばあいがある。
								sum = sum.divide(one, 2, RoundingMode.HALF_UP);
							}
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//Checkbox
					}else if(attr.dataType==AttrInfo.DATATYPE_CHECKBOX){
						BigDecimal sum = new BigDecimal(0);
						for(BeanMap map : result){
							String valStr = (String)map.get(cinfo.attrid);
							if(StringUtil.isNotEmpty(valStr)) {
								sum = sum.add(new BigDecimal(1));
							}
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//数値以外の場合はハイフン
					}else{
						sumItems.put(cinfo.name, "-");
					}
				}
			}
		//リストのみのレイヤ
		}else{
			String sortSpec = key;
			// アコーディオンの指定がある場合は、最初にアコーディオン対象属性でソートする（PDF出力用）
			if (!StringUtil.isEmpty(mtbl.accordionattrid))
				sortSpec = "lower(nullif(trim(" + mtbl.accordionattrid + "), ''))," + key;
			result = tableService.selectByTrackdataid(table, loginDataDto.getTrackdataid(), "desc", sortSpec);
			if (totalable) {
				//合計値算出
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo cinfo = colinfoItems.get(i);
					String colName = cinfo.attrid;//カラム名取得
					String type = tableService.getAttrType(table, colName);
					//Integerでかつ、○○id、○○infoidでない
					if(type.equals(Constants.DATABASE_COLTYPE_INT) && !TableService.isMasterColumn(colName) && !TableService.isInfoColumn(colName)){
						long sum = 0;
						for(BeanMap map : result){
							Object obj = map.get(colName);
							if(obj!=null && 0<obj.toString().length())sum += (Integer)obj;
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//Longでかつ、○○id、○○infoidでない
					}else if(type.equals(Constants.DATABASE_COLTYPE_BIGINT) && !TableService.isMasterColumn(colName) && !TableService.isInfoColumn(colName)){
						long sum = 0;
						for(BeanMap map : result){
							Object obj = map.get(colName);
							if(obj!=null && 0<obj.toString().length())sum += (Long)obj;
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//Float
					}else if(type.equals(Constants.DATABASE_COLTYPE_FLOAT)){
						BigDecimal sum = new BigDecimal(0);
						for(BeanMap map : result){
							Object obj = map.get(colName);
							if(obj!=null && 0<obj.toString().length()) sum = sum.add(new BigDecimal(((Float)obj).toString()));
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//Double
					}else if(type.equals(Constants.DATABASE_COLTYPE_DOUBLE)){
						BigDecimal sum = new BigDecimal(0);
						for(BeanMap map : result){
							Object obj = map.get(colName);
							if(obj!=null && 0<obj.toString().length()) sum = sum.add(new BigDecimal(((Double)obj).toString()));
						}
						sumItems.put(cinfo.name, String.valueOf(sum));
					//数値以外の場合はハイフン
					}else{
						sumItems.put(cinfo.name, "-");
					}
				}
			}
		}

		int nrows = result.size();

		if (nrows > pagerow && pagerow > 0) {
			paging = true;
			nextpage = true;
		}
		else if (listForm.npage > 0)
			paging = true;
		else nextpage = false;

		// 結果とフィルタ数が同じ場合は、すべて更新となる
		// 検索などの場合は 結果とフィルタ数は同数となる。
		if(filterfeatureIds != null)
			slimerDto.setTargetIds(table, filterfeatureIds);
		//フィルタリング対応
		/*filtering = false;
		List<String> ids = new ArrayList<String>();
		// idsを取得して、StringのListに加工する
		//eコミマップのレイヤ
		if (master.layerid != null && !master.layerid.isEmpty()) {
			filterDto = mapService.filter(listForm.conditionValue, listForm.timeParams());
			if(filterDto!=null) {
				// レイヤIDが一致しない場合
				String layerId = ttbl!=null ? ttbl.layerid : master.layerid;
				if(layerId.equals(filterDto.getLayerId())==false) {
					ActionMessages errors = new ActionMessages();
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Setting of filter searching scope is invalid."), false));
					ActionMessagesUtil.addErrors(session, errors);
				}
				else {
					filtering = true;
					listForm.conditionValue = filterDto.getConditionValue();
					List<Long> filterfeatureIds = filterDto.getFilteredFeatureIds();
					if(filterfeatureIds!=null) {
						for(Long filterfeatureId : filterfeatureIds) {
							ids.add(String.valueOf(filterfeatureId));
						}
						// 結果とフィルタ数が同じ場合は、すべて更新となる
						if(result.size()!=filterDto.getFilteredFeatureIds().size()) {
							slimerDto.setTargetIds(table, filterDto.getFilteredFeatureIds());
						}
					}
				}
			}
		}*/

		// フィルタリングidsから各レコードのグレーアウトを設定する
		if(result!=null) {
			for(BeanMap map : result){
				String recordId = String.valueOf(map.get(key));
				if(filtering){
					//key（id）が合致したらtrue
					if(ids.indexOf(recordId)!=-1) {
						filterIds.put(recordId, true);
						filterClasses.put(recordId, "");
					}
					//合致するkey（id）が無かったらfalse
					else {
						filterIds.put(recordId, false);
						filterClasses.put(recordId, "gray");
					}
					// JSP側で使用するClass名を定義する
					//filterClasses.put(String.valueOf((Long)map.get(key)), "gray");
				}
				//フィルタリング無し
				else{
					filterIds.put(recordId, true);
				}
			}
		}

		// 意志決定支援（避難者推計）のフィルタリングで上書き
		if(pageDto.isDecisionSupport()){
			HashMap<String, String> shelterFilter = new HashMap<String, String>();
			shelterFilter = decisionsupportInfoService.filterShelter(true);
			for(Map.Entry<String, String> shelter_map : shelterFilter.entrySet()){
				boolean hit = false;
				for(BeanMap map : result){
					if((Long)map.get(key) == Long.parseLong(shelter_map.getKey())){
						// filterはfalseを利用しているので合わせた
						decisionIds.put(shelter_map.getKey(), false);
						if(!filtering){
							filterClasses.put(shelter_map.getKey(), shelter_map.getValue());
							hit = true;
						}else{
							if(filterClasses.containsKey(shelter_map.getKey())){
								if(filterIds.get(shelter_map.getKey())){
									// フィルタの設定にプラスする
									filterClasses.put(shelter_map.getKey(), shelter_map.getValue());
								}else{
									filterClasses.put(shelter_map.getKey(), filterClasses.get(shelter_map.getKey()) + " " + shelter_map.getValue());
								}
								hit = true;
							}
						}
						break;
					}
				}
				if(!hit){
					decisionIds.put(shelter_map.getKey(), true);
				}
			}
			// 総避難者数レイヤの更新時間を取得
			Timestamp decisionLastTime = decisionsupportInfoService.decisionsupportUpdateTime();
			String decisionUpdateTimeWareki = getUpdateTimeBy(decisionLastTime);
			decisionUpdateTime = decisionUpdateTimeWareki == null ? lang.__("not yet executed") : decisionUpdateTimeWareki;
		}
		//フィルタリングdebug用
		//filterIds.put("4", false);
		//filterIds.put("5", false);
		//filterIds.put("26", false);
		//filterIds.put("27", false);
		//filterIds.put("28", false);

		//座標付与デバッグ
		//logger.info("setCoordinate");
		//tableService.setCoordinate("c43",true ,false);

		//resultのジオメトリをPOINTに変換
		for(BeanMap map : result){
			if (map.containsKey("theGeom")) {
				PGgeometry pggeom = (PGgeometry)map.get("theGeom");
				if (pggeom != null) {
					String geomstr = pggeom.toString();
					if (StringUtil.isNotEmpty(geomstr)) {
						Geometry geom;
						try {
							geomstr = geomstr.substring(geomstr.indexOf(';')+1);
							geom = GeometryUtils.getGeometryFromWKT(geomstr);
							map.put("wkt", geom.toText());
						} catch (ParseException e) {
							e.printStackTrace();
							logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
							logger.error("", e);
						}
						if(geomstr.indexOf("POINT") == -1){
							try {
								geomstr = geomstr.substring(geomstr.indexOf(';')+1);
								geom = GeometryUtils.getGeometryFromWKT(geomstr);
								Point pt = geom.getCentroid();
								map.put("theGeom", pt.toText());
							} catch (ParseException e) {
								e.printStackTrace();
								logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
								logger.error("", e);
							}
						}
					}
				}
			}
		}

		List<BeanMap> photogid = tableService.fileexist(layerid);
		for(int i = 0;i < photogid.size();i++){
				String gidstr = photogid.get(i).toString();
				String gid = gidstr.substring(5, gidstr.length()-1);
			if(!photogid.get(i).isEmpty()){
				fileexist.put(gid, true);
			}else{
				fileexist.put(gid, false);
			}
		}

		// viewモードなら編集不可
		if(pageDto.isViewMode()) editable = false;

		// 外部地図リスト切り替えのセレクトボックスを取得する
		metadataMap = externallistService.getMetadataMap(menuid, isTraining);

		// 集計リスト切り替えのセレクトボックスを取得する
		if(mtbllist.size() > 1){
			for(int i=1; i < mtbllist.size();i++){
				MenutableInfo mtbl2 = mtbllist.get(i);
				metadataMap.put("list-"+i, mtbl2.tablemasterInfo.name);
			}
		}

		//検索文字列表示
		if (filterDto != null && (filterDto.getFilterInfoId() == null || filterDto.getFilterInfoId() == 0l) && StringUtil.isEmpty(filterDto.getSearchText()))
			filterDto.setSearchText(getSearchText(taskid, menuid, colinfoItems));
	}

	/**
	 * ソートのありなしを判定
	 */
	protected boolean sortempty(String sort) {
		return StringUtil.isEmpty(listForm.sort) || listForm.sort.equals("undefined") || listForm.sort.equals("-1");
	}

	/**
	 * 更新後のリロードで呼び出されるアクション
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/listpage/{menutaskid}/{menuid}/{npage}/{sort}")
	public String listpage(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		return list(model, listForm);
	}

	/**
	 * 更新後のリロードで呼び出されるアクション
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/list/{menutaskid}/{menuid}/{sort}")
	public String list(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;
		// 地図ボタンの表示フラグとして、PageDto.pagetoggleButton を見ているため初期化する
		initPage(PAGE_TYPE, listForm);

		Long menuid = listForm.menuid;
		Long taskid = listForm.menutaskid;
		//ResponseUtil.write("aaaaaa");
		pageDto.setMenuInfo(menuInfoService.findById(menuid));
		content(taskid, menuid);
		//if (StringUtil.isNotEmpty(listForm.sort) && listForm.sort.equals("undefined"))
		//	listForm.sort = "";
		if (!sortempty(listForm.sort))
			defsort = listForm.sort;

		setupModel(model);

		return "page/list/reload";
	}

	/**
	 * コンテンツのみロードを共通化するための実行メソッド
	 * @return フォワード先 リストコンテンツページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/content")
	public String content(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;
		String metaid = listForm.metaid;
		listForm.summarylist = false;
		if(metaid!=null){
			if(metaid.indexOf("list") == 0){
				listForm.summarylist = true;
			}
		}
		return list(model, listForm);
	}

	@org.springframework.web.bind.annotation.RequestMapping("/updateSummary")
	public String updateSummary(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult) {
		try {
			String metaid = listForm.metaid;
			listForm.summarylist = false;
			if(metaid!=null){
				if(metaid.indexOf("list") == 0){
					listForm.summarylist = true;
					long menuid = listForm.menuid;
					List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
					pageDto.setMenuInfo(menuInfoService.findById(menuid));

					boolean isTraining = false;
					// 訓練状態の取得
					if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
						TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
						isTraining = trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
					}

					metadataMap = externallistService.getMetadataMap(menuid, isTraining);
					// metadataidから必要なメニューテーブル情報を取得
					String listidstr = metaid.substring(metaid.indexOf("-")+1, metaid.length());
					int listid = Integer.parseInt(listidstr);
					// 集計対象となるメニューテーブル情報を取得
					MenutableInfo mtbl = mtbllist.get(0);
					mtbl.tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
					// 集計リストのメニューテーブル情報を取得
					MenutableInfo mtblsummary = mtbllist.get(listid);
					// 集計表作成
					ListDto listdto = externallistService.createSummaryExternalList(menuid, metadataMap, mtbl, false, listForm.timeParams(),false);
					for(int i = 0;i < listdto.columnValues.size();i++){
						mtbl = mtbllist.get(listid);
						// 自治体コードを取得
						String localgovcodetag = listdto.columnValues.get(i).get(0);
						// htmlタグを外す
						String localgovcode = localgovcodetag.substring(localgovcodetag.indexOf(">")+1, localgovcodetag.indexOf("</a>"));

						TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtblsummary.tablemasterinfoid, loginDataDto.getTrackdataid());
						String layerId = ttbl.layerid;
						try {
							LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
							Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
							vecLayerInfo.add(layerInfo);
							TrackmapInfo tmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());

							// 集計リスト情報の取得
							SummarylistInfo summarylistInfo = summarylistInfoService.findByTablemasterinfoId(mtblsummary.tablemasterinfoid);
							// 集計リスト情報から自治体コードが格納されている項目名の取得
							String sllocalgovcode = summarylistInfo.localgovcode;

							// featureIdを取得
							FeatureResultList fList = FeatureDB.searchFeatureBbox(null, tmapInfo.mapid, vecLayerInfo, null, sllocalgovcode+"="+localgovcode, 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false);
							if(fList.countResult()==0) throw new ServiceException(lang.__("Municipal code \"{0}\" aggregation result can not be saved.", localgovcode));
							FeatureResult fResult = fList.getResult(0);
							Long featureId = fResult.featureId;

							UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());
							HashMap<String, String> attribute = new HashMap<String, String>();

							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							String now = sdf.format(new Date());
							master = tablemasterInfoService.findById(ttbl.tablemasterinfoid);
							//属性更新日時をセット
							if (!StringUtil.isEmpty(master.updatecolumn))
								attribute.put(master.updatecolumn, now);

							// 集計リスト項目情報の取得
							List<SummarylistcolumnInfo> slclist = summarylistcolumnInfoService.findByMenuid(menuid);

							for(int j = 0;j < slclist.size();j++){
								String attrid = slclist.get(j).attrid;
								// 集計結果を取得
								// 3番目以降が集計結果なので+2
								String value = listdto.columnValues.get(i).get(2+j);
								// feature属性の更新
								attribute.put(attrid, value);
							}
							FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attribute, null);

						} catch (Exception e) {
							if(e instanceof ServiceException) throw (ServiceException) e;
							logger.error("updateSummary error: "+e.getMessage(), e);
						}
					}
				}
			}
		} catch(ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display list."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}
		return content(model, listForm);
	}


	/**
	 * 更新処理
	 * @return null
	 */
	@Transactional(propagation=Propagation.NEVER)
	@org.springframework.web.bind.annotation.RequestMapping(value="/update", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String update(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {

		long trackmapinfoid = 0L;
		if(loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			trackmapinfoid = trackData.trackmapinfoid;
		}

		long fid = 0;
		Long orgid = null;
		try {
			//コミットされる前にページの読み込みが走る場合があるので。トランザクションを別管理にする。
			userTransaction.begin();

			MapmasterInfo mastermap = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

			// 送られた来た属性値単位のデータを、地物単位の attributesマップに変換する
			Map<String, Map<Long, HashMap<String, String>>> featuresMap = new HashMap<String, Map<Long, HashMap<String, String>>>();
			// 削除対象地物
			List<Long> delfids = new ArrayList<Long>();
			for (ListEditDto data : listForm.saveDatas) {
				String value = data.value;
				String ids[] = data.id.split(":");
				String table = ids[0];
				String attrid = ids[1];
				//String key = ids[2];
				//String id = ids[3];
				logger.debug("datas : "+ids[0]+","+ids[1]+","+ids[2]+","+ids[3]+","+data.value);

				// テーブル名から フィーチャマップを取得（なければ作成）
				Map<Long, HashMap<String, String>> features = featuresMap.get(table);
				if(features==null) {
					features = new HashMap<Long, HashMap<String, String>>();
					featuresMap.put(table, features);
				}

				// 地物IDから 属性マップを取得（なければ作成）
				// ※ fid が 0 の場合は、登録処理
				fid = Long.parseLong(ids[3]);
				HashMap<String, String> attribute = features.get(fid);
				if(attribute==null) {
					attribute = new HashMap<String, String>();
					features.put(fid, attribute);
				}

				// 属性値を保存
				attribute.put(attrid, value);

				// 削除の場合は、delfids に追加する
				if (fid != 0l){
					if (ids[1].equals("delete")) {
						if (ids[3].equals(value))
							delfids.add(fid);
					}
				}
			}

			// テーブルのループ
			for(Map.Entry<String, Map<Long, HashMap<String, String>>> featuresMapEntry : featuresMap.entrySet()) {
				String table = featuresMapEntry.getKey();
				Map<Long, HashMap<String, String>> features = featuresMapEntry.getValue();

				// 更新対象テーブルのテーブルマスタ情報などを取得する
				TracktableInfo ttbl = tracktableInfoService.findByTrackDataIdAndTablename(loginDataDto.getTrackdataid(), table);
				TablemasterInfo master = null;
				// マスタテーブルを使用する
				if (ttbl == null) {
					master = tablemasterInfoService.findByMapmasterInfoIdAndTablename(mastermap.id, table);
					if (master == null) return null;
				}
				// 記録テーブルを使用する
				else {
					master = tablemasterInfoService.findById(ttbl.tablemasterinfoid);
				}

				orgid = tableFeatureService.execute(master, ttbl, features, /*isAddColumn*/null, delfids);

				//
				// システムテーブル、eコミレイヤの共通後処理
				//
				if(StringUtil.isNotEmpty(master.layerid)) {
					String layerid = ttbl!=null ? ttbl.layerid : master.layerid;
					// クリアリングハウスのメタデータを更新
					clearinghouseService.updatemetadataByLayerId(layerid, trackmapinfoid);

					//データ転送
					DatatransferInfo transinfo = datatransferInfoService.findByTablemasterInfoIdAndValid(master.id);
					if (transinfo != null)
						dataTransferService.transferData(transinfo, loginDataDto.getTrackdataid());
				}
			}

			// レスポンス
			try {
				JSONObject feature = new JSONObject();
				feature.put("id", orgid!=null ? orgid : fid);
				JSONArray features = new JSONArray();
				features.put(feature);

				// 結果JSONを生成
				JSONObject result = new JSONObject();
				result.put("success", true);
				result.put("features", features);

				// 出力の準備
				//response.setContentType("application/json");
				//response.setCharacterEncoding("UTF-8");
				//PrintWriter out = response.getWriter();
				//out.print(result.toString());
				return result.toString();
			} catch (Exception e2) {
				logger.error(loginDataDto.logInfo(), e2);
				throw new ServiceException(e2);
			}

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {
				logger.error(loginDataDto.logInfo(), e1);
			}

			// エラーレスポンス
			try {
				// 結果JSONを生成
				JSONObject result = new JSONObject();
				result.put("success", false);
				result.put("msg", e.getMessage());

				// 出力の準備
				//response.setContentType("application/json");
				//response.setCharacterEncoding("UTF-8");
				//PrintWriter out = response.getWriter();
				//out.print(result.toString());
				return result.toString();
			} catch (Exception e2) {
				logger.error(loginDataDto.logInfo(), e2);
				throw new ServiceException(e2);
			}

		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		}

		//ResponseUtil.write("success");

		//return null;
	}

	/**
	 * ファイルアップロード処理
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/upload", method = RequestMethod.POST)
	@ResponseBody
	public String upload(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;
    	// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

    	long fid = 0;
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String now = sdf.format(time);

		// 必要に応じてエラーをここで渡す
		listForm.responseMessage = "";

		if(listForm.formFile != null) {
			try {
				// 拡張子をチェック
				FileUtil.assertAllowedExtent(listForm.formFile.getOriginalFilename());

				String path = FileUtil.upload(application, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), listForm.formFile);
				String ids[] = listForm.id.split(":");
				fid = Long.parseLong(listForm.value);

				TracktableInfo ttbl = tracktableInfoService.findByTrackDataIdAndTablename(loginDataDto.getTrackdataid(), ids[0]);
				TablemasterInfo master = tablemasterInfoService.findById(ttbl.tablemasterinfoid);
				//eコミマップのテーブル
				if(ttbl.layerid != null && !ttbl.layerid.isEmpty()){
					//TODO:
				}else{
					//テーブル名からEntityのクラスを取得
					Class<?> entityClass = tableService.getEntity(ids[0]);
					Object entityObj = null;//ClassUtil.newInstance(entityClass);
					entityObj = tableService.selectById(entityClass, ids[0], fid);
					tableService.setFieldValue(entityClass, entityObj, ids[1], path);
					//名称保存可能？
					//Field f = ClassUtil.getField(entityClass, ids[1]+"name");
					//if (f != null) {
					//	tableService.setFieldValue(entityClass, entityObj, ids[1]+"name", listForm.formFile.getName());
					//}
					//属性更新日時
					if (!StringUtil.isEmpty(master.updatecolumn))
						tableService.setFieldValue(entityClass, entityObj, master.updatecolumn, now);
					//更新
					tableService.updateByEntity(entityObj);

					logger.info("datas : UPDATE NOT ecommap");

				}
			} catch(Exception e) {
				e.printStackTrace();
				listForm.responseMessage = e.getMessage();
			}
		}else{
			listForm.responseMessage = lang.__("No uploaded files");
		}


		//JSON形式で返す（返り値は特に使っていない）
		/* IE8でFormDataが使用出来ない為、JSON返却を中止 */
		/*
		try {


			HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
			httpServletResponse.setContentType("application/json");
			PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());

			//Entity→JSON形式に変換して出力します。
			sendPoint.println("[{\"id\": \""+fid+"\"}]");
			sendPoint.flush();
			sendPoint.close();

			listForm.responseMessage = "[{\"id\": \""+fid+"\"}]";

		} catch (IOException e) {
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			throw new ServiceException(e);
		}
		 */
		JSONObject result = new JSONObject();
		try {
			result.put("id", fid);
			return result.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * eコミの写真をアップロード
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/upphoto/{menuid}/{id}", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String upphoto(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {

		long fid = Long.parseLong(listForm.id);
		long menuid = listForm.menuid;

		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		TracktableInfo ttbl = null;
		if (mtbllist.size() > 0) {
			MenutableInfo mtbl = mtbllist.get(0);
			ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());
		}
		if (ttbl == null) {
			//ResponseUtil.write("fail");
			return "fail";
		}

		TrackmapInfo tmap = trackmapInfoService.findById(ttbl.trackmapinfoid);

		String layerid = ttbl.layerid;
		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layerInfo = mapDB.getLayerInfo(layerid);
		// 履歴レイヤの場合は、添付ファイル更新時に地物も更新して gid を新しくする
		// 同じgidを利用すると、前の添付ファイルに追加する形で更新されるため。
		if (layerInfo.timeSeriesType == TimeSeriesType.HISTORY) {
			UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());

			try {
				// 更新前のフィーチャファイルリストを取得
				JSONArray fileList = new JSONArray();
				JSONObject json = mapService.getContents(userInfo.authId, menuid, layerid, fid, listForm.timeParams());
				if (json.length() > 0) {
					JSONArray files = json.getJSONArray("files");
					for(int i = 0;i < files.length(); i+=2){
						JSONArray photo = new JSONArray();
						photo.put(files.get(i));
						photo.put(files.get(i+1));
						fileList.put(photo);
					}
				}

				// gid 更新のために地物更新（属性値・ジオメトリは更新しない）
				{

					HashMap<String, String> attribute = new HashMap<String, String>();

					// 履歴の場合は時間を指定する
					Timestamp time = new Timestamp(TimeUtil.newDate().getTime());
					attribute.put("time_from", time.toString());

					// 編集者も更新項目として追加
					if(edituserAttrService.hasEdituserAttr(layerInfo)) {
						attribute.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
					}

					FeatureDB.updateFeatureAttribute(userInfo, layerid, fid, attribute, null);
				}

				// フィーチャファイルのコピー
				Date[] timeParam = new Date[]{TimeUtil.newDate()}; // 現在時刻
				fileUploadService.updateFeatureFileList(tmap.mapid, layerInfo, fid, fileList, timeParam);

				// 履歴レイヤの場合は、更新すると gid が変化するので、更新時に指定した time_from から gid を取得する。
				if(LayerInfo.TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
					fid = ExMapDB.getHistoryFeatureId(layerid, fid, timeParam);
				}

			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		try {
			boolean success = false;
			String msg = null;
			JSONArray fileList =  null;

			// ファイルアップロード
			try {
				if (listForm.formFile != null) {
					FileForm fileform = new FileForm();
					fileform.formFile = listForm.formFile;
					fileform.title = listForm.formFile.getOriginalFilename();
					List<FileForm> fileForms = new ArrayList<FileForm>();
					fileForms.add(fileform);

					long mid = tmap.mapid.longValue();
					String layerId = ttbl.layerid;
					fileList = fileUploadService.uploadContents(layerId, fileForms);
					Date[] timeParam = new Date[]{TimeUtil.newDate()}; // 現在時刻
					fileUploadService.insertFeatureFileList(mid, layerInfo, fid, fileList, timeParam);
					success = true;
					msg = lang.__("File registered.");
				}
			} catch(Exception e) {
				success = false;
				if(e instanceof ServiceException) msg = e.getMessage();
				logger.error("/page/map/ecommap/contents/uploadFile error: "+e.getMessage(), e);
			}

			// 結果JSONを生成
			JSONObject result = new JSONObject();
			result.put("success", success);
			result.put("msg", msg);
			result.put("fileList", fileList);

			// 出力の準備
			// @see https://www.sencha.com/forum/showthread.php?132949-Fileupload-Invalid-JSON-string
			// The server response is parsed by the browser to create the document for the IFRAME.
			// If the server is using JSON to send the return object,
			// then the Content-Type header must be set to "text/html" in order to tell the browser to insert the text unchanged into the document body.
			// content-type should be set to text/html
			//response.setContentType("text/html");
			//response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(result.toString());
			return result.toString();
		//} catch (IOException e) {
		//	logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
		//	logger.error("", e);
		} catch (JSONException e) {
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
		}

		return null;
	}

	/**
	 * リストに表示されているレコードをCSV出力
	 * テーブル全体ではなく表示されている項目のみ
	 * フィルタリングがかかっている場合は、表示対象のレコードのみ
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/outputcsv", method = RequestMethod.POST)
	public String outputcsv(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, HttpServletResponse res){
		this.listForm = listForm;
		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}*/
		long menuid = listForm.menuid;
		MenuInfo menuInfo = menuInfoService.findById(menuid);

		boolean bsum = true;
		if (menuInfo.menutypeid.equals(Menutype.HISTORY) || menuInfo.menutypeid.equals(Menutype.OBSERVE))
			bsum = false;

		Boolean paging = Boolean.parseBoolean(request.getParameter("paging"));
		if (!paging || !listForm.pageall) {
			outputCSV(res, listForm.value, listForm.time);
		}
		else {//全出力
			pageDto.setMenuInfo(menuInfo);
			int row = pageDto.getPagerow();
			pageDto.setPagerow(0);
			content(listForm.menutaskid, listForm.menuid);
			pageDto.setPagerow(row);

			HashMap<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
			Iterator<Entry<String, String[]>> it = selectVal.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String[]> entry = it.next();
				String key = entry.getKey();
				HashMap<String, String> map = new HashMap<String, String>();
				maps.put(key, map);
				String[] vals = entry.getValue();
				String[] strs = selectStr.get(key);
				for (int i = 0; i < vals.length; i++)
					map.put(vals[i], strs[i]);
			}

			StringBuffer buff = new StringBuffer();
			if (bsum) bsum = mtbl.totalable;
			for (int i = 0; i < colinfoItems.size(); i++) {
				TablelistcolumnInfo col = colinfoItems.get(i);
				if (i > 0 || bsum)
					buff.append(',');
				buff.append("\"").append(col.name).append("\"");
			}
			if (bsum) {//合計表示
				buff.append('\n');
				buff.append(lang.__("Total<!--2-->"));
				for (TablelistcolumnInfo colinfo : colinfoItems)
					buff.append(",\""+sumItems.get(colinfo.name)+"\"");
			}
			buff.append('\n');

			Map<String, String> acrdmap = new HashMap<String, String>();
			String acrdval = null;
			int num = 0;
			if (StringUtil.isNotEmpty(mtbl.accordionattrid)) {
				//先にアコーディオンの数をカウントしておく
				for (BeanMap map : result) {
					String val = "";
					if (map.get(mtbl.accordionattrid) != null) val = map.get(mtbl.accordionattrid).toString().trim();
					if (acrdval == null || !acrdval.equals(val)) {
						if (num > 0) acrdmap.put(acrdval, acrdval+" ("+num+")");
						acrdval = val;
						num = 0;
					}
					num++;
				}
			}
			if (num > 0) acrdmap.put(acrdval, acrdval+" ("+num+")");

			for (BeanMap map : result) {
				Long fid = (Long)map.get(key);
				if (filterIds.containsKey(""+fid) && !filterIds.get(""+fid)) continue;

				StringBuffer line = new StringBuffer();
				if (StringUtil.isNotEmpty(mtbl.accordionattrid)) {
					String val = "";
					if (map.get(mtbl.accordionattrid) != null) val = map.get(mtbl.accordionattrid).toString().trim();
					if (acrdval == null || !acrdval.equals(val)) {
						line.append(acrdmap.get(val)+"\n");
						acrdval = val;
					}
				}

				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo col = colinfoItems.get(i);
					String key = col.attrid;
					Object val = map.get(key);
					if (val != null && maps.containsKey(key)) {
						Map<String, String> vals = maps.get(key);
						val = vals.get(val.toString());
					}
					if (i > 0 || bsum)
						line.append(',');
					if (val != null)
						line.append("\"").append(val.toString()).append("\"");
				}
				line.append('\n');
				buff.append(line);

			}
			outputCSV(res, buff.toString(), listForm.time);
		}

		return null;
	}

	/**
	 * リストに表示されているレコードをPDF出力
	 * テーブル全体ではなく表示されている項目のみ
	 * フィルタリングがかかっている場合は、表示対象のレコードのみ
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/outputpdf", method = RequestMethod.POST)
	public String outputpdf(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, HttpServletResponse res){
		this.listForm = listForm;
		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}*/

		long menuid = listForm.menuid;
		long taskid = listForm.menutaskid;

		MenuInfo menuInfo = menuInfoService.findById(menuid);
		MenutaskInfo menutaskInfo = menutaskInfoService.findById(taskid);
		MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(menutaskInfo.menuprocessinfoid);
		String menuprocessName = menuprocessInfo.name;
		String menutaskName = menutaskInfo.name;
		String menuName = menuInfo.name;

		boolean bsum = true;
		if (menuInfo.menutypeid.equals(Menutype.HISTORY))
			bsum = false;
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		if (mtbllist.size() > 0) {
			bsum = mtbllist.get(0).totalable;
		}
		Boolean paging = Boolean.parseBoolean(request.getParameter("paging"));
		if (!paging || !listForm.pageall) {
			outputListPdf(res, listForm.time, menuprocessName, menutaskName, menuName, listForm.dataList, bsum);
		}
		else {//全出力
			pageDto.setMenuInfo(menuInfo);
			int row = pageDto.getPagerow();
			pageDto.setPagerow(0);
			content(listForm.menutaskid, listForm.menuid);
			pageDto.setPagerow(row);

			JSONArray jary = new JSONArray();
			JSONArray jcol = new JSONArray();
			JSONArray jsum = new JSONArray();
			JSONArray filrow = new JSONArray();
			filrow.put(0);//ヘッダ分
			if (bsum) filrow.put(0);
			for (TablelistcolumnInfo colinfo : colinfoItems) {
				jcol.put(colinfo.name);
			}
			jary.put(jcol);
			if (bsum) {
				for (TablelistcolumnInfo colinfo : colinfoItems)
					jsum.put(sumItems.get(colinfo.name));
				jary.put(jsum);
			}

			HashMap<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
			Iterator<Map.Entry<String, String[]>> it = selectVal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = it.next();
				String key = entry.getKey();
				HashMap<String, String> map = new HashMap<String, String>();
				maps.put(key, map);
				String[] vals = entry.getValue();
				String[] strs = selectStr.get(key);
				for (int i = 0; i < vals.length; i++)
					map.put(vals[i], strs[i]);
			}

			String acrdval = null;
			int num = 0;
			JSONObject jacrd = new JSONObject();
			for (BeanMap map : result) {
				Long fid = (Long)map.get(key);
				if (filterIds.containsKey(""+fid) && !filterIds.get(""+fid)) continue;

				if (StringUtil.isNotEmpty(mtbl.accordionattrid)) {
					String val = "";
					if (map.get(mtbl.accordionattrid) != null) val = map.get(mtbl.accordionattrid).toString().trim();
					try {
						JSONArray jval = new JSONArray();
						if (acrdval == null || !acrdval.equals(val)) {
							num = 0;
							jacrd = new JSONObject();
							jacrd.put("open", true);
							jacrd.put("value", val);
							jacrd.put("count", "("+num+")");
							jval.put(jacrd);
							jary.put(jval);
							acrdval = val;
						}
						num++;
						jacrd.put("count", "("+num+")");
					} catch (JSONException e) {e.printStackTrace();}
				}

				JSONArray jval = new JSONArray();
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo col = colinfoItems.get(i);
					String key = col.attrid;
					Object val = map.get(key);
					if (val != null && maps.containsKey(key)) {
						Map<String, String> vals = maps.get(key);
						val = vals.get(val.toString());
					}
					if (val != null)
						jval.put(val);
					else jval.put("");
				}
				jary.put(jval);
			}
			listForm.dataList = jary;

			outputListPdf(res, listForm.time, menuprocessName, menutaskName, menuName, listForm.dataList, bsum);
		}

		return null;
	}

	/**
	 * @return 履歴表示
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/history/{menutaskid}/{menuid}/{id}")
	public String history(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult){
		this.listForm = listForm;
		try {
			long fid = Long.parseLong(listForm.id);
			Long taskid = listForm.menutaskid;
			Long menuid = listForm.menuid;

			pageDto.setDefault();
			MenuInfo menuInfo = menuInfoService.findById(menuid);
			List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
			MenutableInfo tableInfo = null;
			if (mtbllist.size() > 0) {
				tableInfo = mtbllist.get(0);
			}

			List<String> attrlist = new ArrayList<String>();
			if (tableInfo != null) {
				attridItems = tablelistcolumnInfoService.findByMenutableInfoId(tableInfo.id);
				for (TablelistcolumnInfo col : attridItems) {
					attrlist.add(col.attrid);
				}
			}

			pageDto.setMenuInfo(menuInfo);
			content(taskid, menuid, fid);

			// check if table is history layer
			MapDB mapDB = MapDB.getMapDB();
			LayerInfo layerInfo = mapDB.getLayerInfo(table);
			if(layerInfo==null || layerInfo.timeSeriesType!=LayerInfo.TimeSeriesType.HISTORY) {
				throw new ServiceException(lang.__("No history displayed because it's not history layer."));
			}
			Long orgid = null;
			for (BeanMap map : result) {
				orgid = (Long)map.get("_orgid");
				break;
			}

			//履歴データ取得
			Date[] dateparam = null;
			if (loginDataDto.getTrackdataid() > 0l) {
				dateparam = new Date[2];
				TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
				dateparam[0] = Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(trackData.starttime.getTime()) : new Date(trackData.starttime.getTime());
				if (trackData.endtime != null)
					dateparam[1] = Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(trackData.endtime.getTime()) : new Date(trackData.endtime.getTime());
				else dateparam[1] = Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(System.currentTimeMillis()) : new Date();
			}
			else {
				dateparam = new Date[2];
				List<TrackData> tracklist = trackDataService.findByOldTrackData(loginDataDto.getLocalgovinfoid());
				if (tracklist.size() > 0) {
					TrackData trackData = tracklist.get(tracklist.size()-1);
					dateparam[0] = Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(trackData.endtime.getTime()) : new Date(trackData.endtime.getTime());
				}
				else dateparam[0] = Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(System.currentTimeMillis()) : new Date();
				dateparam[1] = Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(System.currentTimeMillis()) : new Date();
			}
			if (orgid != null) {
				result = tableService.selectById(table, "_orgid", orgid, null, null, dateparam);
				Collections.sort(result, new HistoryComparator());
				//履歴のマージ処理
				result = mergeHistory(result, attrlist.toArray(new String[1]));

				//履歴にない項目は削除
				if (result.size() > 0) {
					List<TablelistcolumnInfo> attridItems2 = new ArrayList<TablelistcolumnInfo>();
					for (TablelistcolumnInfo col : attridItems) {
						if (result.get(0).containsKey(col.attrid))
							attridItems2.add(col);
					}
					attridItems = attridItems2;
				}
			}

			setupModel(model);
		} catch(Exception e) {
			addRequestErrorMessage(bindingResult, lang.__("Error:{0}", e.getMessage()));
			logger.error("history error", e);
		}
		return "/page/list/history";
	}

	/**
	 * 変更のない履歴は省く処理
	 * @param history
	 * @param attr
	 * @return 変更のない履歴を省いた結果リスト
	 */
	public List<BeanMap> mergeHistory(List<BeanMap> history, String[] attr)
	{
		List<BeanMap> newlist = new ArrayList<BeanMap>();
		if (history != null) {
			for (int i = 0; i < history.size()-1; i++) {
				BeanMap map = history.get(i);
				BeanMap mapp = history.get(i+1);
				boolean diff = false;
				for (int j = 0; j < attr.length; j++) {
					if (!map.containsKey(attr[j])) continue;
					if (map.get(attr[j])!=null || mapp.get(attr[j])!=null) {
						if ((map.get(attr[j])==null && mapp.get(attr[j])!=null) ||
								(map.get(attr[j])!=null && mapp.get(attr[j])==null) ||
								!map.get(attr[j]).equals(mapp.get(attr[j]))) {
							diff = true;
							break;
						}
					}
				}
				if (diff) {
					//newlist.add(mapp);
					newlist.add(map);
				}
			}
			//最後のものは追加
			BeanMap map = history.get(history.size()-1);
			Timestamp time = (Timestamp)map.get("timeFrom");
			if (time.getTime()<=0)
				map.put("timeFrom", null);
			newlist.add(map);
		}
		return newlist;
	}

	/**
	 * 住所からWKT文字列を取得
	 * @param address
	 * @return WKT
	 * @throws IOException
	 */
	public String getWktFromAddress(String address) throws IOException
	{
		Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("ja").getGeocoderRequest();
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
		if (geocoderResponse.getResults().size() == 0) return null;

		GeocoderResult gresult = geocoderResponse.getResults().get(0);
		GeocoderGeometry geom = gresult.getGeometry();
		String wkt = "POINT("+geom.getLocation().getLng()+" "+geom.getLocation().getLat()+")";

		return wkt;
	}

	@org.springframework.web.bind.annotation.RequestMapping("/detail/{menutaskid}/{menuid}/{id}")
	public String detail(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult){
		this.listForm = listForm;
		HttpUtil.throwBadRequestExceptionIfIsNull(listForm.id, listForm.menutaskid, listForm.menuid);
    	long orgid = Long.parseLong(listForm.id);
    	long taskid = listForm.menutaskid;
    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster() && !loginDataDto.isUsual()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

		pageDto.setDefault();
		kartecolinfoItems = new ArrayList<TablelistkarteInfo>();
    	content(taskid, menuid, orgid);
    	List<TablelistkarteInfo> kartelist = tablelistkarteInfoService.findByMenutableInfoId(mtbl.id);
    	if(0<kartelist.size()) {
    		for(TablelistkarteInfo karte : kartelist){
    			kartecolinfoItems.add(karte);
    		}

    		// 列情報を設定
    		LayerInfo linfo = null;
    		// 平時のテーブル情報を取得
    		String layerid = master.layerid;
    		String tablename = master.tablename;
    		//eコミマップのレイヤ
    		if (StringUtil.isNotEmpty(layerid)) {
    			linfo = layerService.getLayerInfo(layerid);
    			if(linfo==null) {
    				ActionMessages errors = new ActionMessages();
    				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Layer ({0}) not found.", layerid), false));
    				ActionMessagesUtil.addErrors(bindingResult, errors);
    			}
    			else {
    				editClass = new String[kartecolinfoItems.size()];
					Map<String, String> selectOptions = null;
    				for (int i = 0; i < kartecolinfoItems.size(); i++) {
    					TablelistkarteInfo kinfo = kartecolinfoItems.get(i);
    					AttrInfo attr = linfo.getAttrInfo(kinfo.attrid);
        				String dataType = "String";
    					if(attr!=null) {
	    					switch(attr.dataType) {
	    					case AttrInfo.DATATYPE_SELECT: dataType = "Select";
		    					selectOptions = new LinkedHashMap<String, String>();
		    					// JSON配列文字列なら、キーと値をJSONから取得
		    					try {
		    						JSONArray dataExps = new JSONArray(attr.dataExp);
		    						for(int idx=0; idx<dataExps.length(); idx++) {
		    							JSONObject json = dataExps.getJSONObject(idx);
		    							@SuppressWarnings("unchecked")
		    							Iterator<String> it = json.keys();
		    							while(it.hasNext()) {
		    								String key = it.next();
		    								selectOptions.put(json.getString(key), key);
		    							}
		    						}
		    						break;
		    					} catch(JSONException e) { }
		    					//eコミマップではSelectの選択肢と値は同じ
		    					String[] selectItems = attr.dataExp.split(",");
		    					//for(String selectItem : selectItems) selectOptions.put(selectItem, selectItem);
								//String[] selectStrs = new String[selectItems.length];
								//String[] selectVals = new String[selectItems.length];
								//for(int selectOptionIdx=0; selectOptionIdx<selectItems.length; selectOptionIdx++) {
								//	selectStrs[selectOptionIdx] = selectOption.getString("text");
								//	selectVals[selectOptionIdx] = selectOption.getString("value");
								//	selMap.put(selectOption.getString("value"), selectOption.getString("text"));
								//}
								//eコミマップではSelectの選択肢と値は同じ
								selectStr.put(kinfo.attrid, selectItems);
								selectVal.put(kinfo.attrid, selectItems);
								break;
	    					case AttrInfo.DATATYPE_CHECKBOX: dataType = "Checkbox"; break;
	    					case AttrInfo.DATATYPE_INTEGER:  dataType="Number"; break;
	    					case AttrInfo.DATATYPE_FLOAT:    dataType="Float"; break;
	    					case AttrInfo.DATATYPE_DATE:     dataType="Date"; break;
	    					case AttrInfo.DATATYPE_DATETIME: dataType="DateTime"; break;
	    					case AttrInfo.DATATYPE_TEXT:	 dataType = "String";
	    					if (attr.length > 20)
	    						dataType = "TextArea";
	    					break;
	    					default: dataType = "String"; break;
	    					}
							//必須項目対応
							if(attr.nullable)
								colMap.put(kinfo.attrid, !attr.nullable);
    					}
    					editClass[i] = dataType;
    				}
    			}
    			//eコミマップでないレイヤ
    		}else{
    			key = "id";
    			editClass = new String[kartecolinfoItems.size()];
    			for (int i = 0; i < kartecolinfoItems.size(); i++) {
    				TablelistkarteInfo kinfo = kartecolinfoItems.get(i);
    				String colName = kinfo.attrid;//カラム名取得
    				String type = tableService.getAttrType(tablename, colName);
    				if (type == null) throw new ServiceException(lang.__("Check attribute item [{0}] settings.", colName));
    				// trackdataid
    				if(TrackdataidAttrService.TRACKDATA_ATTR_ID.equals(colName)) {
    					editClass[i] = "Select";
    				}
    				//カラム名の末尾が「○○○id」の場合は「○○○_master」テーブルの「name」カラムのデータを取得して、Selectで選択させる
    				else if(TableService.isMasterColumn(colName)){
    					editClass[i] = "Select";
    					//カラム名の末尾が「infoid」の場合は「○○○_info」テーブルの「name」カラムのデータを取得して、Selectで選択させる
    				}else if(TableService.isInfoColumn(colName)){
    					editClass[i] = "Select";
    					//アップロードカラム
    				//}else if (kinfo.uploadable && type.equals(Constants.DATABASE_COLTYPE_TEXT)){
    				//	editClass[i] = "Upload";
    					//その他のカラム型に応じたeditClassの指定
    				}else{
    					//数値
    					if(type.equals(Constants.DATABASE_COLTYPE_INT) || type.equals(Constants.DATABASE_COLTYPE_BIGINT)){
    						editClass[i] = "Number";
    						//小数
    					}else if(type.equals(Constants.DATABASE_COLTYPE_FLOAT) || type.equals(Constants.DATABASE_COLTYPE_DOUBLE)){
    						editClass[i] = "Float";
    						//文字列
    					}else if(type.equals(Constants.DATABASE_COLTYPE_TEXT)){
    						editClass[i] = "String";
    						//timestamp
    					}else if(type.equals(Constants.DATABASE_COLTYPE_TIMESTAMP)){
    						editClass[i] = "DateTime";
    						//それ以外
    					}else{
    						//とりあえず文字列
    						editClass[i] = "String";
    					}
    				}
    				//必須項目対応
    				if(tablecolumnMasterService.checkNullable(tablename, kinfo.attrid)){//nullでもOK
    					colMap.put(kinfo.attrid, false);
    				}else{//必須
    					colMap.put(kinfo.attrid, true);
    				}
    				colMap.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, true);
    			}
    		}
    	}
    	// カルテ情報がなければ、テーブルリストカラム情報の設定を流用
    	else {
    		for(TablelistcolumnInfo colinfo : colinfoItems) {
    			TablelistkarteInfo karteInfo = Beans.createAndCopy(TablelistkarteInfo.class, colinfo).execute();
    			kartecolinfoItems.add(karteInfo);
    		}
    	}

    	// 時間パラメータ指定時は編集・追加不可
    	if(listForm.timeParams()!=null) {
    		addable = false;
    		editable = false;
    	}

    	setupModel(model);

    	return "/page/list/detail";
	}

	@org.springframework.web.bind.annotation.RequestMapping("/opensearch/{menutaskid}/{menuid}/{pagetype}")
	public String opensearch(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){
		this.listForm = listForm;

    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);

		///////////////////////////////////
    	//disasterId = loginDataDto.getDisasterid();
    	List<ListEditDto> searchData = loginDataDto.getMenuSearchDatas().get(menuid);
    	if (searchData != null)
    		listForm.saveDatas = searchData;

    	/////////////////////////
		listForm.menuid = menuid;
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		//テーブル取得
		List<TracktableInfo> ttbllist = new ArrayList<TracktableInfo>();
		for (MenutableInfo mtbl : mtbllist) {
			//TablemasterInfo tmi = tablemasterInfoService.findById(mtbl.tablemasterinfoid);
			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());
			if(ttbl != null)ttbllist.add(ttbl);
		}

		TracktableInfo ttbl = null;
		if (ttbllist.size() > 0) ttbl = ttbllist.get(0);
		MenutableInfo mtbl = mtbllist.get(0);
		TablemasterInfo master = tablemasterInfoService.findById(mtbl.tablemasterinfoid);
		if (ttbl == null /*&& loginDataDto.isMaster() && !pageDto.isViewMode()*/) {//マスタ確認モード
			ttbl = new TracktableInfo();
			ttbl.tablemasterinfoid = master.id;
			ttbl.layerid = master.layerid;
			ttbl.tablename = master.tablename;
			ttbl.trackmapinfoid = 0L;
		}

		colinfoItems = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
		// 属性IDからカラム情報を取得するMapを初期化
		Map<String, TablelistcolumnInfo> colinfoItemMap = new HashMap<>();
		for (TablelistcolumnInfo cinfo : colinfoItems) {
			colinfoItemMap.put(cinfo.attrid, cinfo);
		}

		// 最初に属性検索ダイアログを開くと、opensearchall は null となる。
		// ここで、リスト表示属性のみにするか、全属性にするかを決める。

		JSONObject conditionValue = createFileterString(listForm.saveDatas, false);

		if (!StringUtil.isEmpty(master.layerid))
			key = "gid";
		else
			key = "id";

		//一覧表示するテーブルと結果の取得
		table = ttbl.tablename;

		//eコミマップのレイヤ
		//if (ttbl != null) {
			if (ttbl.layerid != null && !ttbl.layerid.isEmpty()) {
				LayerInfo linfo = layerService.getLayerInfo(ttbl.layerid);
				// 一括変更
				if(linfo!=null) {
					editClass = new String[colinfoItems.size()];
					for (int i = 0; i < colinfoItems.size(); i++) {
						TablelistcolumnInfo cinfo = colinfoItems.get(i);
						AttrInfo attr = linfo.getAttrInfo(cinfo.attrid);
						// 一括変更情報に追加
						if(attr!=null) {
							mapService.addEcomColumn(slimerDto, linfo, attr, cinfo.editable, cinfo.grouping, cinfo.defaultcheck, cinfo.groupdefaultcheck, cinfo.addable);
							try {
								JSONObject column = slimerDto.getColumn(linfo.layerId, cinfo.attrid);
								if(column!=null) {
									// データ型
									String dataType = null;
									if(column.has("dataType")) dataType = editClass[i] = column.getString("dataType");

									// 型に応じた値
									if(StringUtil.isNotEmpty(dataType)) {
										if("Select".equals(dataType)) {
											if(column.has("selectOptions")) {
												JSONArray selectOptions = column.getJSONArray("selectOptions");
												String[] selectStrs = new String[selectOptions.length()];
												String[] selectVals = new String[selectOptions.length()];
												for(int selectOptionIdx=0; selectOptionIdx<selectOptions.length(); selectOptionIdx++) {
													JSONObject selectOption = selectOptions.getJSONObject(selectOptionIdx);
													selectStrs[selectOptionIdx] = selectOption.getString("text");
													selectVals[selectOptionIdx] = selectOption.getString("value");
												}
												//eコミマップではSelectの選択肢と値は同じ
												selectStr.put(cinfo.attrid, selectStrs);
												selectVal.put(cinfo.attrid, selectVals);
											}
										}
										else if("Checkbox".equals(dataType)) {
											if(column.has("checkDisplay")) {
												String checkDisplay = column.getString("checkDisplay");
												checkStr.put(cinfo.attrid, checkDisplay);
											}
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
			//eコミマップでないレイヤ
			}else{
				editClass = new String[colinfoItems.size()];
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo cinfo = colinfoItems.get(i);
					String colName = cinfo.attrid;//カラム名取得
					if (cinfo.editable) editable = true;
					String type = tableService.getAttrType(ttbl.tablename, colName);
					if (type == null) throw new ServiceException(lang.__("attribute item")+"["+colName+"]"+lang.__("check the settings"));
					//カラム名の末尾が「○○○id」の場合は「○○○_master」テーブルの「name」カラムのデータを取得して、Selectで選択させる
					if(TableService.isMasterColumn(colName)){
						editClass[i] = "Select";
						//指定したmasterテーブルがあれば、そのnameカラムをSelectの選択肢として追加
						Map<String, String> selMap = tableService.setRelTable("master", colName, selectStr, selectVal);
						//表示用のMap格納
						selectValView.put(colName, selMap);

					//カラム名の末尾が「infoid」の場合は「○○○_info」テーブルの「name」カラムのデータを取得して、Selectで選択させる
					}else if(TableService.isInfoColumn(colName)){
						editClass[i] = "Select";
						//指定したinfoテーブルがあれば、そのnameカラムをSelectの選択肢として追加
						Map<String, String> selMap = tableService.setRelTable("info", colName, selectStr, selectVal);
						//表示用のMap格納
						selectValView.put(colName, selMap);

					//カラム名の末尾が「dataid」の場合は「○○○_data」テーブルの「name」カラムのデータを取得して、Selectで選択させる
					}else if(TableService.isDataColumn(colName)){
						editClass[i] = "Select";
						//指定したinfoテーブルがあれば、そのnameカラムをSelectの選択肢として追加
						Map<String, String> selMap = tableService.setRelTable("data", colName, selectStr, selectVal);
						//表示用のMap格納
						selectValView.put(colName, selMap);

					//アップロードカラム
					}else if (cinfo.uploadable && type.equals(Constants.DATABASE_COLTYPE_TEXT)){
						editClass[i] = "Upload";
					//その他のカラム型に応じたeditClassの指定
					}else{
						//数値
						if(type.equals(Constants.DATABASE_COLTYPE_INT) || type.equals(Constants.DATABASE_COLTYPE_BIGINT)){
							editClass[i] = "Number";
						//小数
						}else if(type.equals(Constants.DATABASE_COLTYPE_FLOAT) || type.equals(Constants.DATABASE_COLTYPE_DOUBLE)){
							editClass[i] = "Float";
						//文字列
						}else if(type.equals(Constants.DATABASE_COLTYPE_TEXT)){
							editClass[i] = "String";
						//timestamp
						}else if(type.equals(Constants.DATABASE_COLTYPE_TIMESTAMP)){
							editClass[i] = "DateTime";
						//それ以外
						}else{
							//とりあえず文字列
							editClass[i] = "String";
						}
					}
				}
			}
		//}

		///////////////////////////////////
    	//disasterId = loginDataDto.getDisasterid();
    	//List<ListEditDto> searchData = loginDataDto.menuSearchDatas.get(menuid);
    	//if (searchData != null)
    	//	listForm.saveDatas = searchData;

		setupModel(model);

    	return "/page/list/search";
	}

	/**
	 * 検索実行
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/search/{menutaskid}/{menuid}/{pagetype}")
	@ResponseBody
	public String search(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {

    	long menuid = listForm.menuid;

    	JSONObject conditionValue = createFileterString(listForm.saveDatas, false);
    	if (conditionValue == null) {
    		loginDataDto.getMenuConditionMap().remove(menuid);
    		FilteredFeatureId.removeFilteredFeatureId(session, String.valueOf(menuid));
    	}
    	else {
    		loginDataDto.getMenuConditionMap().put(Long.valueOf(menuid), conditionValue.toString());
    		loginDataDto.getMenuFilterIdMap().remove(menuid);
    	}

    	loginDataDto.getMenuSearchDatas().put(menuid, listForm.saveDatas);

		JSONArray jary = new JSONArray();
    	if (listForm.pagetype.equals("map")) {
			Vector<Long> filteredFeatureIds = new Vector<Long>();
			MenuInfo menuInfo = menuInfoService.findById(menuid);
			pageDto.setMenuInfo(menuInfo);
			filterDto = mapService.filter(conditionValue, menuInfo, listForm.timeParams());
			// フィルタの設定があれば
			if(filterDto!=null) {
				for (Long fid : filterDto.getFilteredFeatureIds())
					filteredFeatureIds.add(fid);
				FilteredFeatureId.setFilteredFeatureId(session, filterDto.getFilterkey(), filteredFeatureIds);
				for (Long fid : filteredFeatureIds)
					jary.put(fid);
			}
			// フィルタの設定がない場合
			else {
				FilteredFeatureId.setFilteredFeatureId(session, String.valueOf(menuid), null);
			}
    	}
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		if (filterDto != null && mtbllist != null && mtbllist.size() > 0) {
    		MenutableInfo mtbl = mtbllist.get(0);
    		colinfoItems = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
		}

		try {
			//HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
			//httpServletResponse.setContentType("application/json");
			//PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());

			//Entity→JSON形式に変換して出力します。
			//sendPoint.println("[{\"id\":"+fid+", \"nullAlert\":\""+nullAlert+"\"}]");
			JSONObject json = new JSONObject();
			if (conditionValue != null)
				json.put("search", conditionValue.toString());
			else
				json.put("search", "true");
			if (listForm.pagetype.equals("map") && filterDto != null) {
				json.put("total", filterDto.getTotal());
				json.put("count", filterDto.getFilteredFeatureIds().size());
				if (jary.length() > 0) json.put("filteredFeatureIds", jary);
			}
			//sendPoint.println(json.toString());
			//sendPoint.flush();
			//sendPoint.close();

			return json.toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			throw new ServiceException(e);
		}

		//return null;
	}

	/**
	 * ページ表示
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablebody/{menutaskid}/{menuid}")
	@ResponseBody
	public String tablebody(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){

		long menuid = listForm.menuid;
		long menutaskid = listForm.menutaskid;
		pageDto.setMenuInfo(menuInfoService.findById(menuid));
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster() && !loginDataDto.isUsual()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

		content(model, listForm);

		if (key == null) return null;

		boolean hasmap = !pageDto.getMenuInfo().menutypeid.equals(Menutype.LIST);
		//boolean btns = ((key.equals("gid") || key.equals("_orgid")) && !pageDto.getMenuInfo().menutypeid.equals(Menutype.LIST)) || detailable || historiable;
		StringBuffer buff = new StringBuffer();
		if (result == null) return null;
		/*if (totalable) {//これをやるとソート時に合計行が下にくることがある。
			buff.append("<tr class=\"sum\" id=\"total-header\">\n");
			if (!downloadable && !deletable && !btns) {
				buff.append("<td class=\"sum noout\">合計</td>\n");
			}
			if (deletable || downloadable) {
				buff.append("<td class=\"sum noout\">合計</td>\n");
			}
			if (btns) {
				buff.append("<td class=\"sum noout\">");
				if (!deletable && !downloadable) buff.append("合計");
				buff.append("</td>");
			}
			for (TablelistcolumnInfo e : colinfoItems) {
				if (e.listable) {
					buff.append("<td class=\"sum\">");
					buff.append(sumItems.get(e.name));
					buff.append("</td>");
				}
			}
			buff.append("</tr>");
		}*/

		String icon = "ui-icon-pencil";
		if (!editable) icon = "ui-icon-document";
		String textclass="";
		String dbtntext = "";
		String dtext = lang.__("edit details");
		String preSubVal = "";
		//<c:forEach var="e" varStatus="s" items="${result}">
		for (BeanMap map : result) {

			if (filterIds.get(map.get(key).toString()) != null && !filterIds.get(map.get(key).toString()))
				buff.append("<tr class=\"gray\" style=\"display:table-row;\" >");
			else if (result.size() == 1)
				buff.append("<tr class=\"odd ui-state-default\">");
			else
				buff.append("<tr>");

			//if (!deletable && !key.equals("gid"))
			//	buff.append("<td class=\"noout\">&nbsp;</td>");

			if (deletable) {
				buff.append("<td id=\""+table+":delete:"+key+":"+map.get(key)+"\" class=\"Checkbox noout\" style=\"min-width:30px;\">");
				buff.append("<input type=\"checkbox\" name=\"delete:"+map.get(key)+"\" value=\""+map.get(key)+"\" />");
				buff.append("</td>");
			}

			//<%--mapの場合 --%>
			if (key.equals("gid") || key.equals("_orgid")) {
				if (!pageDto.getMenuInfo().menutypeid.equals(Menutype.POSTING_PHOTO)) {
					if (listForm.summarylist) {
						buff.append("<td>");
					}
					else {
						buff.append("<td id=\"mapbtn\" width=\""+(pageDto.getPagetoggleButton()!=null && pageDto.getPagetoggleButton().getFirst()=="map"?"120px":"80px")+"\">");
						buff.append("<input type=\"hidden\" name=\"layerid\" value=\""+table+"\"/>");
						buff.append("<input type=\"hidden\" name=\"gid\" value=\""+map.get(key)+"\"/>");
						if (firsttable) {
							if (pageDto.getPagetoggleButton()!=null && pageDto.getPagetoggleButton().getFirst()=="map")
								buff.append("<a id=\"listmapbtn\" href=\"#\" class=\"btn_icon "+(map.get("theGeom")!=null ? "map-btn blue" : "icon-gray")+" style=\"font-weight:normal;\">🌏</a>");
						}
						buff.append("<a id=\"listphotobtn\" href=\""+request.getContextPath()+"/page/list/detail/"+listForm.menutaskid+"/"+listForm.menuid+"/"+map.get(key)+"\" class=\"btn_icon listphotobtn blue\" style=\"font-weight:normal;\">&#x2710</a>");
						buff.append("<a id=\"historybtn\" href=\""+request.getContextPath()+"/page/list/history/"+listForm.menutaskid+"/"+listForm.menuid+"/"+map.get(key)+"\" class=\"btn_icon blue historybtn\" style=\"font-weight:normal;\">🕒</a>");
					}
					buff.append("</td>");
				}
			}

			//<c:forEach var="f" varStatus="t" items="${colinfoItems}">
			int t = 0;
			for (TablelistcolumnInfo f : colinfoItems) {
				String hi = "";
				String st = "";
				if (f.highlight!=null && f.highlight) hi = " highlight";
				if (styleMap.get(f.id) != null) {
					//<c:forEach var="g" varStatus="u" items="${styleMap[f.id]}">
					for (TablerowstyleInfo g : styleMap.get(f.id)) {
						if (g.val.equals(map.get(f.attrid)))
							st = g.style;
					}
				}

				//<%-- データ変換 --%>
				Object cval = map.get(f.attrid);
				String colVal = null;
				String imgVal[] = null;
				if (cval != null) colVal = cval.toString();
				if (colVal == null) colVal = "";
				//<%-- 日付形式は yy/mm/dd HH:MM:SS になるように変換する --%>
				if (editClass[t].equals("Date") || editClass[t].equals("DateTime")) {
					colVal = colVal.replace('-', '/');
					colVal = colVal.replace(".0", "");
				}
				if (editClass[t].equals("Date") && colVal.trim().indexOf(" ") >= 0) {
					colVal = colVal.substring(0, colVal.indexOf(" "));
				}
				if (editClass[t].equals("Image")) {
					imgVal = colVal.split("|");
				}
				String st2 = "";
				String tip = "";
				if (editClass[t].equals("String") || editClass[t].equals("TextArea") || editClass[t].equals("Date") || editClass[t].equals("DateTime") || editClass[t].equals("Upload")) {
					st2 = "text-align:"+pageDto.getText_align();
					tip = " showtip";
				}
				else if (editClass[t].equals("Number") || editClass[t].equals("Float"))
					st2 = "text-align:"+pageDto.getNumber_align();

				if (f.editable && !pageDto.isViewMode()) {
					buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+editClass[t]+" "+hi+" "+tip+"\" style=\""+st+" "+st2+"\">");
					if (editClass[t].equals("String") || editClass[t].equals("TextArea") || editClass[t].equals("Number") || editClass[t].equals("Float") || editClass[t].equals("Date") || editClass[t].equals("DateTime"))
						buff.append(colVal);
					else if (editClass[t].equals("Select")) {
						buff.append("<span id=\""+f.attrid+":"+map.get(key)+"\" style=\"display:none;\">"+colVal+"</span>");
						buff.append("<select name=\""+f.attrid+":"+map.get(key)+"\" onChange=\"onSelStyleChange("+f.id+", this, '#"+f.attrid+":"+map.get(key)+"')\">");
						if (!colMap.get(f.attrid) && StringUtil.isNotEmpty(selectVal.get(f.attrid)[0]))
							buff.append("<option value=\"\">");
						int u = 0;
						for (String g : selectStr.get(f.attrid)) {
							String[] g2 = selectVal.get(f.attrid);
							if (!g2[u].equals(colVal)) {
								buff.append("<option value=\""+g2[u]+"\">"+g);
							}
							else {
								buff.append("<option value=\""+g2[u]+"\" selected>"+g);
							}
							u++;
						}
						buff.append("</select>");
					}
					else if (editClass[t].equals("Checkbox")) {
						buff.append("<span id=\""+f.attrid+":"+map.get(key)+"\" style=\"display:none;\">"+colVal+"</span>");
						buff.append("<input type=\"checkbox\" name=\""+f.attrid+":"+map.get(key)+"\" value=\""+checkStr.get(f.attrid)+"\" "+((checkStr.get(f.attrid).equals(colVal))?"checked":"")+" onClick=\"onCheckStyleChange("+f.id+", this, '#"+f.attrid+":"+map.get(key)+"')\">");
					}
					buff.append("</td>");
				}
				else {
					Map<String,String> selMap = selectValView.get(f.attrid);
					if (selMap != null && selMap.size() > 0 && editClass[t].equals("Select")) {
						String val = selMap.get(colVal);
						if (val == null) val = "";
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+" showtip\" style=\""+st+"\">"+val+"</td>");
					}
					else if (editClass[t].equals("Upload")) {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+"\" style=\""+st+"\">");
						if (colVal.length() > 0)
							buff.append("<a href=\""+request.getContextPath()+colVal+"\" target=\"_blank\">"+colVal+"</a>");
						buff.append("</td>");
					}
					else if (editClass[t].equals("Date") || editClass[t].equals("DateTime")) {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+" "+tip+"\" style=\""+st+" "+st2+"\">"+colVal+"</td>");
					}
					else {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+" "+tip+"\" style=\""+st+" "+st2+"\">"+colVal+"</td>");
					}
				}
				t++;
			}
			buff.append("</tr>");
		}
		buff.append("<script type=\"text/javascript\">");
		buff.append(" nextpage = "+nextpage+";");
		if (!nextpage) {
			buff.append("$('.pagenext').removeClass('ui-state-default');");
			buff.append("$('.pagenext').addClass('ui-state-disabled');");
			buff.append("$('.pageend').removeClass('ui-state-default');");
			buff.append("$('.pageend').addClass('ui-state-disabled');");
		}
		if (nextpage) {
			buff.append("$('.pagenext').removeClass('ui-state-disabled');");
			buff.append("$('.pagenext').addClass('ui-state-default');");
			buff.append("$('.pageend').removeClass('ui-state-disabled');");
			buff.append("$('.pageend').addClass('ui-state-default');");
		}
		if (listForm.npage > 0) {
			buff.append("$('.pageprev').removeClass('ui-state-disabled');");
			buff.append("$('.pageprev').addClass('ui-state-default');");
			buff.append("$('.pagestart').removeClass('ui-state-disabled');");
			buff.append("$('.pagestart').addClass('ui-state-default');");
		}
		if (listForm.npage == 0) {
			buff.append("$('.pageprev').removeClass('ui-state-default');");
			buff.append("$('.pageprev').addClass('ui-state-disabled');");
			buff.append("$('.pagestart').removeClass('ui-state-default');");
			buff.append("$('.pagestart').addClass('ui-state-disabled');");
		}
		buff.append("page = "+listForm.npage+";");
		if (filterDto != null && filterDto.getFilteredFeatureIds() != null)
			buff.append("SaigaiTask.Page.filter.getFilterNumEl().text(\" ( "+filterDto.getFilteredFeatureIds().size()+lang.__("Items")+" / "+filterDto.getTotal()+lang.__("Items")+" )\");");
		int from = pageDto.getPagerow()*listForm.npage+1;
		int to = pageDto.getPagerow()*(listForm.npage+1)+1;
		if (to > count) to = count;
		buff.append("$('.paging-num').text(' ['+addFigure("+from+")+'-'+addFigure("+to+")+'/'+addFigure("+count+")+'"+lang.__("Items")+"　"+(listForm.npage+1)+"/"+listForm.pagesize+lang.__("display page")+"]');");
		buff.append("</script>");

		//ResponseUtil.write(buff.toString());
		return buff.toString();
	}

	/**
	 * ページ表示
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablepagebody/{menutaskid}/{menuid}/{npage}")
	@ResponseBody
	public String tablepagebody(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){

		return tablebody(model, listForm);
	}

	/**
	 * ページ表示
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablepagebodysort/{menutaskid}/{menuid}/{npage}/{sort}")
	@ResponseBody
	public String tablepagebodysort(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){

		return tablebody(model, listForm);
	}

	/**
	 * eコミの写真を削除
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/deletephoto/{menuid}/{id}/{layerid}/{deleteid}", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String deletephoto(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;

		long fid = Long.parseLong(listForm.id);
		long menuid = listForm.menuid;
		String layerid = listForm.layerid;
		int deleteid = Integer.parseInt(listForm.deleteid)*2;

		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		TracktableInfo ttbl = null;
		if (mtbllist.size() > 0) {
			MenutableInfo mtbl = mtbllist.get(0);
			ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());
		}
		if (ttbl == null) {
			//ResponseUtil.write("fail");
			return "[{\"result\":fail}]";
		}

		TrackmapInfo tmap = trackmapInfoService.findById(ttbl.trackmapinfoid);
		String authId = loginDataDto.getEcomUser();
		JSONObject json = mapService.getContents(authId, menuid, layerid, fid, listForm.timeParams());
		JSONArray result = new JSONArray();
		try{
			JSONArray fileList = json.getJSONArray("files");
			for(int i = 0;i < fileList.length(); i+=2){
				JSONArray photo = new JSONArray();
				if(i == deleteid){
				}else{
					photo.put(fileList.get(i));
					photo.put(fileList.get(i+1));
					result.put(photo);
				}
			}
		}catch(Exception e){
			logger.error(loginDataDto.logInfo()+" transform coordinatedecimal error: "+e.getMessage(), e);
		}

		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layerInfo = mapDB.getLayerInfo(layerid);
		// 履歴レイヤの場合は、添付ファイル更新時に地物も更新して gid を新しくする
		// 同じgidを利用すると、前の添付ファイルに追加する形で更新されるため。
		if (layerInfo.timeSeriesType == TimeSeriesType.HISTORY) {
			UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(authId);
			try {
				// 属性値もジオメトリも更新しない
				FeatureDB.updateFeatureAttribute(userInfo, layerid, fid, /*attributes*/new HashMap<String, String>(), null);
			} catch (Exception e) {
				logger.error("FeatureDB.updateFeatureAttribute error: "+e.getMessage(), e);
			}
		}
		fileUploadService.updateFeatureFileList(tmap.mapid, layerInfo, fid, result, listForm.timeParams());

		return "[{\"result\":\"success\"}]";
	}
}
