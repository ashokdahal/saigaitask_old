/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import static jp.ecom_plat.saigaitask.service.ExternallistService.METADATA_GENERALIZATION;
import static jp.ecom_plat.saigaitask.service.ExternallistService.METADATA_SUMMARY;
import static jp.ecom_plat.saigaitask.service.ExternallistService.METADATA_SUMMARY_EXTERNAL;
import static jp.ecom_plat.saigaitask.service.ExternallistService.METADATA_SUMMARY_INTERNAL;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.page.AbstractPageAction.HistoryComparator;
import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.SummaryListDto;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.form.page.ExternalListForm;
import jp.ecom_plat.saigaitask.service.ExternallistService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 外部地図リストページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ExternallistAction extends AbstractPageAction {

	protected ExternalListForm externalListForm;
	// Service
	@Resource protected MenutableInfoService menutableInfoService;
	@Resource protected TablelistcolumnInfoService tablelistcolumnInfoService;
	@Resource protected TrackgroupDataService trackgroupDataService;
	@Resource protected ExternallistService externallistService;

	/** ページ種別 */
	public static final String PAGE_TYPE = "externallist";

	/** 表示するメインのリスト */
	public ListDto listDto;

	/** 表示するテーブル */
	public List<ListDto> listDtos;

	/** メタデータマップ */
	public Map<String, String> metadataMap;

	/** データ件数 */
	public int count;

	/** ページング情報 */
	public Boolean paging = false;
	public Boolean nextpage = false;

	/** テーブルマスタ情報 */
	public TablemasterInfo master;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("externalListForm", externalListForm);
		model.put("listDto", listDto);
		model.put("listDtos", listDtos);
		model.put("metadataMap", metadataMap);
		model.put("count", count);
		model.put("paging", paging);
		model.put("nextpage", nextpage);
		model.put("master", master);
	}
	
	/**
	 * 外部リストページを表示する.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/externallist","/page/externallist/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externalListForm) {
		this.externalListForm = externalListForm;
		getContentData();
		//1ページあたりの表示件数。SaigaiTask.propertiesで指定されている。
		int pagerow = pageDto.getPagerow();
		if (externalListForm.npage == -1 && pagerow > 0) {//最終ページ
			externalListForm.npage = (int)Math.ceil( (double)count / pagerow)-1;
		}
		externalListForm.pagesize = (int)Math.ceil( (double)count / pagerow);
		if (externalListForm.npage > 0) {
			int npage = (int)Math.ceil( (double)count / pagerow)-1;
			if (externalListForm.npage > npage) externalListForm.npage = npage;
		}

		if (paging){
			if (pagerow >= count) {
				paging = false;
			} else {
				//そのページに表示される件数。paging、nextpageフラグを決める
				int nrows =listDto.index;
				if (nrows > pagerow && pagerow > 0) {
					paging = true;
					nextpage = true;
				}
				else if (externalListForm.npage > 0)
					paging = true;
				else nextpage = false;

				}
			}



		setupModel(model);

		return "/page/externallist/index";
	}

	/**
	 * 外部リストページを表示する.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/externallist/content")
	public String content(Map<String,Object>model,
			@Valid @ModelAttribute ExternalListForm externalListForm, BindingResult bindingResult) {
		this.externalListForm = externalListForm;
		try {
			externalListForm.setNpage(0);
			getContentData();
			int pagerow = pageDto.getPagerow();
			// ページングONで、１Pの表示件数が全件数以上の場合は通常のページングOFFの画面を表示する。
			if (paging){
				if (pagerow >= count) {
					paging = false;
				} else {
					externalListForm.pagesize = (int)Math.ceil( (double)count / pagerow);

					//listDto.indexはそのページに表示される件数。paging、nextpageフラグを決める
					if (listDto.index > pagerow && pagerow > 0) {
						paging = true;
						nextpage = true;
					}
					else if (externalListForm.npage > 0)
						paging = true;
					else nextpage = false;

				}
			}


		} catch(ServiceException e2) {
			logger.error("Unable to display list.", e2);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e2.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}
		setupModel(model);

		return "/page/externallist/content";
	}

	/**
	 * リストのデータを取得
	 */
	public void getContentData() {

		// リスト初期化
		listDtos = new ArrayList<ListDto>();

		// メタデータＩＤ
		String metadataId = externalListForm.metaid;
		if(StringUtil.isEmpty(metadataId)) throw new ServiceException(lang.__("Parameters of meta data ID (metaid) is required."));

		// ページの初期化
		initPage(PAGE_TYPE, externalListForm);
		pageDto.setEnableFullscreen(true);
		final long menuid = pageDto.getMenuInfo().id;

		boolean isTraining = false;
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
		}
		

		// 外部地図リスト切り替えのセレクトボックスを取得する
		metadataMap = externallistService.getMetadataMap(menuid, isTraining);

		// 外部リストがない自治体情報
		List<LocalgovInfo> notexistLocalgovInfos = new ArrayList<LocalgovInfo>();

		// 災害グループでフィルタする
		if(externalListForm.filterTrackgroup) {
			externallistService.filterTrackdataidMap = new HashMap<String, Long>();

			// 災害グループを取得する
			List<TrackgroupData> trackgroupDatas = null;
			if(Localgovtype.PREF.equals(loginDataDto.getLocalgovInfo().localgovtypeid)) {
				trackgroupDatas = trackgroupDataService.findByPreftrackdataid(loginDataDto.getTrackdataid());

				// 災害グループのメタデータ、記録データID だけ残す
				Map<String, String> trackgroupMetadataMap = new HashMap<String, String>();

				// 災害グループがあるなら
				if(0<trackgroupDatas.size()) {

					// 同じDB上にある外部リストのメタデータを取得
					Map<Long, ExternaltabledataInfo> map = externallistService.getExternaltabledataInfoMap(menuid);

					// 災害グループのメタデータ、記録データID だけ残す
					for(TrackgroupData trackgroupData : trackgroupDatas) {
						LocalgovInfo cityLocalgovInfo = trackgroupData.cityTrackData.localgovInfo;
						// 同じDB上にある 外部リスト情報
						ExternaltabledataInfo externaltabledataInfo = map.get(cityLocalgovInfo.id);
						// 外部リスト情報が同じDB上に見つからない
						if(externaltabledataInfo==null) {
							notexistLocalgovInfos.add(cityLocalgovInfo);
						}
						// 同じDB上にある 外部リスト情報
						else {
							// メタデータMap に登録
							String metadataName = metadataMap.get(externaltabledataInfo.metadataid);
							trackgroupMetadataMap.put(externaltabledataInfo.metadataid, metadataName);
							// フィルタtrackdataid に登録
							externallistService.filterTrackdataidMap.put(externaltabledataInfo.metadataid, trackgroupData.citytrackdataid);
						}
					}

				}

				// 災害グループのメタデータMapへ変更
				metadataMap = trackgroupMetadataMap;
			}
		}

		// メニューテーブル情報
		externalListForm.menuid = menuid;
		MenutableInfo mtbl = null;
		List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
		if(0<mtbllist.size()) {
			mtbl = mtbllist.get(0);
			mtbl.tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
		}

		// リストに表示するデータを取得する
		int localgovNameRowIndex = -1;
		// 総括リストを表示
		if(metadataId.startsWith(METADATA_GENERALIZATION)) {
			localgovNameRowIndex = 0;
			listDto = externallistService.createGeneralizationExternalList(menuid, mtbl, externalListForm.filterTrackgroup, externalListForm.timeParams(), isTraining);
		}
		// 集計リストを表示
		else if(metadataId.startsWith(METADATA_SUMMARY)) {
			localgovNameRowIndex = 0;
			SummaryListDto summaryListDto = null;
			// 内部リストのみの集計リスト
			if(METADATA_SUMMARY_INTERNAL.equals(metadataId)) {
				summaryListDto = externallistService.createSummaryExternalList(menuid, null, mtbl, externalListForm.filterTrackgroup, externalListForm.timeParams(), isTraining);
				metadataMap.put(METADATA_SUMMARY_INTERNAL, lang.__("Aggregation list(internal)"));
			}
			// 外部リストのみの集計リスト
			else if(METADATA_SUMMARY_EXTERNAL.equals(metadataId)){
				summaryListDto = externallistService.createSummaryExternalList(menuid, metadataMap, null, externalListForm.filterTrackgroup, externalListForm.timeParams(), isTraining);
				metadataMap.put(METADATA_SUMMARY_EXTERNAL, lang.__("Aggregation list(external)"));
			}
			// 集計リスト
			else {
				summaryListDto = externallistService.createSummaryExternalList(menuid, metadataMap, mtbl, externalListForm.filterTrackgroup, externalListForm.timeParams(), isTraining);
			}

			// 複数同時災害のリストが１つもないなら
			if(!summaryListDto.hasMultiDisasterRow()) {
				// 発生災害数の行を削除する
				summaryListDto.deleteDisasternumRow();
			}

			listDto = summaryListDto;

			// 外部リストのない災害グループのレコードを追加
			for(LocalgovInfo localgovInfo : notexistLocalgovInfos) {
				List<String> values = new ArrayList<String>();
				values.add(localgovInfo.pref+localgovInfo.city);
				for(int i=1; i<listDto.columnNames.size(); i++) {
					values.add(SummaryListDto.VALUE_UNDEFINED);
				}
				listDto.columnValues.add(values);
			}
		}
		// リストを１つ表示
		else {
			// 数字なら内部リストを１つ表示
			if(NumberUtils.isNumber(metadataId)) {
				Long menutableid = Long.valueOf(metadataId);
				mtbl = menutableInfoService.findById(menutableid);
				mtbl.tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
				if(mtbl.tablelistcolumnInfoList==null || mtbl.tablelistcolumnInfoList.size()==0 || mtbl.deleted) {
					logger.warn(lang.__("Menu table item info has not been set to menu table info : {0}", mtbl.id));
					throw new ServiceException(lang.__("Set menu table item info."));
				}
				listDto = externallistService.createMenutableList(mtbl, externalListForm.filterTrackgroup, externalListForm.timeParams());
			}
			// 数字でないなら外部リストを１つ表示
			else {
				//テーブルマスター情報の該当するマスタのページングフラグを確認する。
				master = tablemasterInfoService.findById(mtbl.tablemasterinfoid);
				paging = master.paging;

				// pagerowが0と設定されている場合はページングFalseにする。
				int pagerow = pageDto.getPagerow();
				if (pagerow == 0) paging = false;

				if (!paging) externalListForm.setNpage(-2);
				listDto = externallistService.createExternalList(metadataId, mtbl, null, externalListForm.filterTrackgroup, externalListForm.timeParams(), isTraining, externalListForm.npage);
			}

			// 複数同時災害のリストでないなら
			if(!listDto.isMultiDisaster()) {
				// trackdataid 行があれば、削除する
				listDto.deleteTrackdataidRowIfExists();
			}

			// trackdataid属性があれば ID から 災害名称 へ変換しておく
			listDto.replaceTrackdataid2NameIfExists(trackDataService);
		}

		// 合計行を計算
		listDto.total();

		// 集計リストの場合は合計行を必ず出す
		if(METADATA_SUMMARY.equals(metadataId)) listDto.totalable = true;
		// メニューに合計行の設定があれば、合計行表示フラグを上書き
		else if(mtbl!=null) listDto.totalable = mtbl.totalable;

		// 合計行のみ表示
		if(externalListForm.onlyTotale) {
			listDto.onlyTotalList();

			// 自治体名 の列を削除
			listDto.deleteRow(localgovNameRowIndex);
		}

		// デフォルトソートをパラメータから引き継ぐ
		if (StringUtil.isNotEmpty(externalListForm.sort) && !externalListForm.sort.equals("undefined") && !externalListForm.sort.equals("-1"))
			listDto.defsort = externalListForm.sort;

		// 全件数を代入
		count = listDto.getCount();

		listDtos.add(listDto);
	}

	/**
	 * リストに表示されているレコードをCSV出力
	 * テーブル全体ではなく表示されている項目のみ
	 * フィルタリングがかかっている場合は、表示対象のレコードのみ
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/externallist/outputcsv")
	public String outputcsv(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externalListForm,HttpServletResponse res){
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	this.externalListForm = externalListForm;

		Boolean paging = Boolean.parseBoolean(request.getParameter("paging"));
		if (!paging || !externalListForm.pageall) {
			outputCSV(res, externalListForm.value, externalListForm.time);
		}
		else {//全出力
			externalListForm.setNpage(-2);
			getContentData();

			StringBuffer buff = new StringBuffer();
			// 項目名行
			int n = 0;
			for (n = 0; n < listDto.columnNames.size(); n++) {
				if (n > 0 || externalListForm.totalable) buff.append(',');
				buff.append("\"").append(listDto.columnNames.get(n)).append("\"");
			}
			buff.append('\n');

			// 合計行
			if (externalListForm.totalable) {
				buff.append(lang.__("Total<!--2-->"));
				for (int i=0; i < listDto.sumItems.size(); i++) {
					String name =listDto.columnNames.get(i);
					buff.append(",\"").append(listDto.sumItems.get(name)).append("\"");
				}
				buff.append('\n');
			}

			// データ行
			int s = 0;
			for (List<String> f : listDto.columnValues) {
				for (int i = 0; i < f.size(); i++) {
					if (i > 0 || externalListForm.totalable) buff.append(',');
					String val = f.get(i);
					if (val == null) val = "";

					buff.append("\"").append(val).append("\"");

				}
				buff.append('\n');

			}
			outputCSV(res, buff.toString(), externalListForm.time);
		}
		return null;
	}

	/**
	 * リストに表示されているレコードをPDF出力
	 * テーブル全体ではなく表示されている項目のみ
	 * フィルタリングがかかっている場合は、表示対象のレコードのみ
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/externallist/outputpdf")
	public String outputpdf(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externalListForm,HttpServletResponse res){
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	this.externalListForm = externalListForm;

    	long menuid = externalListForm.menuid;
		long taskid = externalListForm.menutaskid;

		MenuInfo menuInfo = menuInfoService.findById(menuid);
		MenutaskInfo menutaskInfo = menutaskInfoService.findById(taskid);
		MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(menutaskInfo.menuprocessinfoid);
		String menuprocessName = menuprocessInfo.name;
		String menutaskName = menutaskInfo.name;
		String menuName = menuInfo.name;

		Boolean paging = Boolean.parseBoolean(request.getParameter("paging"));
		if (!paging || !externalListForm.pageall) {
			outputListPdf(res, externalListForm.time, menuprocessName, menutaskName, menuName, externalListForm.dataList, externalListForm.totalable);
		}
		else {//全出力
			//全件を取得したいのでnpageは-2を指定する。
			externalListForm.setNpage(-2);
			//getContentData()の処理中にexternalListForm.timeの値が入っているとエラーになるので、
			//別の変数に値を保持してForm.timeはnullに変えておく
			String time = externalListForm.time;
			externalListForm.setTime(null);
			getContentData();

			/*
			 * jary PDF出力するデータ丸ごと
			 * jcol 項目名レコード
			 * jsum 合計値レコード
			 * jval データレコード
			 *  */
			JSONArray jary = new JSONArray();
			JSONArray jcol = new JSONArray();
			JSONArray jsum = new JSONArray();
			JSONArray jval = new JSONArray();

			// 項目名行
			for (int n = 0; n < listDto.columnNames.size(); n++) {
				jcol.put(listDto.columnNames.get(n));
				}
			jary.put(jcol);

			// 合計行
			if (externalListForm.totalable) {
				for (int i=0; i < listDto.sumItems.size(); i++) {
					String name =listDto.columnNames.get(i);
					jsum.put(listDto.sumItems.get(name));
				}
				jary.put(jsum);
			}

			// データ行
			for (List<String> f : listDto.columnValues) {
				for (int i = 0; i < f.size(); i++) {
					String val = f.get(i);
					if (val == null) val = "";
					jval.put(val);
				}
				jary.put(jval);
				jval = new JSONArray();
			}

			externalListForm.dataList = jary;

			outputListPdf(res, time, menuprocessName, menutaskName, menuName, externalListForm.dataList, externalListForm.totalable);
		}
		return null;
	}

	/**
	 * ページ表示
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablebody/{menutaskid}/{menuid}")
	@ResponseBody
	public String tablebody(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externallistForm){

		long menuid = externallistForm.menuid;
		long menutaskid = externallistForm.menutaskid;
		pageDto.setMenuInfo(menuInfoService.findById(menuid));
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster() && !loginDataDto.isUsual()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

		//content(model, externallistForm); 引数足りなくてエラーになるからとりあえずコメントしとく
		index(model, externallistForm);

		StringBuffer buff = new StringBuffer();

		int s = 0;
		for (List<String> f : listDto.columnValues) {
			buff.append("<tr>");
			if (listDto.totalable) buff.append("<td class=\"\"></td>");
			String st2= "";
			int s2 = 0;
			for (String f2: f ) {
				String name =listDto.columnNames.get(s2);
				if(listDto.typeItems.get(name).equals("number")) {
					st2 = "text-align:"+pageDto.getNumber_align();
				}
				else if (listDto.typeItems.get(name).equals("text")) {
					st2 = "text-align:"+pageDto.getText_align();
				}
				if (f2 == null) f2 = "";
				buff.append("<td class=\"showtip\" headers=\"column-" + s2 + "\" style=\"" + st2 + "\">" + f2 + "</td>");
				st2 = "";
				s2 = s2 + 1;
			}
			buff.append("</tr>");
		}


		buff.append("<script type=\"text/javascript\">");
		buff.append(" nextpage = "+nextpage+";");
		//次のページがないとき（最終ページ）、次・最後ボタン非活性
		if (!nextpage) {
			buff.append("$('.pagenext').removeClass('ui-state-default');");
			buff.append("$('.pagenext').addClass('ui-state-disabled');");
			buff.append("$('.pageend').removeClass('ui-state-default');");
			buff.append("$('.pageend').addClass('ui-state-disabled');");
		}
		//次のページがあるとき（最終ページ以外）、次・最後ボタン活性
		if (nextpage) {
			buff.append("$('.pagenext').removeClass('ui-state-disabled');");
			buff.append("$('.pagenext').addClass('ui-state-default');");
			buff.append("$('.pageend').removeClass('ui-state-disabled');");
			buff.append("$('.pageend').addClass('ui-state-default');");
		}
		//最初のページではないとき、前・先頭ボタン活性
		if (externalListForm.npage > 0) {
			buff.append("$('.pageprev').removeClass('ui-state-disabled');");
			buff.append("$('.pageprev').addClass('ui-state-default');");
			buff.append("$('.pagestart').removeClass('ui-state-disabled');");
			buff.append("$('.pagestart').addClass('ui-state-default');");
		}
		//最初のページのとき、前・先頭ボタン非活性
		if (externalListForm.npage == 0) {
			buff.append("$('.pageprev').removeClass('ui-state-default');");
			buff.append("$('.pageprev').addClass('ui-state-disabled');");
			buff.append("$('.pagestart').removeClass('ui-state-default');");
			buff.append("$('.pagestart').addClass('ui-state-disabled');");
		}
		buff.append("page = "+externalListForm.npage+";");
		int from = pageDto.getPagerow()*externalListForm.npage+1;
		int to = pageDto.getPagerow()*(externalListForm.npage+1)+1;
		if (to > count) to = count;
		buff.append("$('.paging-num').text(' ['+addFigure("+from+")+'-'+addFigure("+to+")+'/'+addFigure("+count+")+'"+lang.__("Items")+"　"+(externalListForm.npage+1)+"/"+externalListForm.pagesize+lang.__("display page")+"]');");

		// 合計行を更新
		if (listDto.totalable) {
			for (int i=0; i < listDto.sumItems.size(); i++) {
				String name =listDto.columnNames.get(i);
				String sum = listDto.sumItems.get(name);
				buff.append("document.getElementById('column-"+i+"-sum').innerHTML = \""+sum+"\";");
			}
		}

		buff.append("</script>");

		//ResponseUtil.write(buff.toString());
		return buff.toString();
	}

	/**
	 * ページ表示
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/externallist/tablepagebody/{menutaskid}/{menuid}/{metaid}/{npage}")
	@ResponseBody
	public String tablepagebody(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externallistForm){

		return tablebody(model, externallistForm);
	}

	/**
	 * ページ表示
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/externallist/tablepagebodysort/{menutaskid}/{menuid}/{metaid}/{npage}/{sort}")
	@ResponseBody
	public String tablepagebodysort(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externallistForm){

		return tablebody(model, externallistForm);
	}

	/**
	 * @return 履歴表示
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/externallist/history")
	public String history(Map<String,Object>model,
			@Valid @ModelAttribute ExternalListForm externalListForm, BindingResult bindingResult,
			@RequestParam(value="_orgid") Long _orgid) {
		externalListForm._orgid = _orgid;
		this.externalListForm = externalListForm;
		try {
			// メタデータＩＤ
			String metadataId = externalListForm.metaid;
			if(StringUtil.isEmpty(metadataId)) throw new ServiceException(lang.__("Parameters of meta data ID (metaid) is required."));

			// ページの初期化
			initPage(PAGE_TYPE, externalListForm);
			pageDto.setEnableFullscreen(true);
			final long menuid = pageDto.getMenuInfo().id;

			boolean isTraining = false;
			if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
				TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
				isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
			}
			
			// メニューテーブル情報
			externalListForm.menuid = menuid;
			MenutableInfo mtbl = null;
			List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(menuid);
			if(0<mtbllist.size()) {
				mtbl = mtbllist.get(0);
				mtbl.tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
			}

			//履歴データ取得
			Date[] dateparam = null;
			if (loginDataDto.getTrackdataid() > 0l) {
				dateparam = new Date[2];
				TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
				dateparam[0] = new Date(trackData.starttime.getTime());
				if (trackData.endtime != null)
					dateparam[1] = new Date(trackData.endtime.getTime());
				else dateparam[1] = new Date();
			}
			else {
				dateparam = new Date[2];
				List<TrackData> tracklist = trackDataService.findByOldTrackData(loginDataDto.getLocalgovinfoid());
				if (tracklist.size() > 0) {
					TrackData trackData = tracklist.get(tracklist.size()-1);
					dateparam[0] = new Date(trackData.endtime.getTime());
				}
				else dateparam[0] = new Date();
				dateparam[1] = new Date();
			}
			listDto = externallistService.createExternalHistoryList(metadataId, mtbl, null, externalListForm.filterTrackgroup, dateparam, isTraining, externalListForm.npage,
					externalListForm.gid, externalListForm._orgid);

			//履歴のマージ処理
			listDto.mergeHistory();

			// リスト初期化
			listDtos = new ArrayList<ListDto>();
			listDtos.add(listDto);

		} catch(ServiceException e2) {
			logger.error("externallist history error", e2);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e2.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);

		if(listDto!=null&&listDto.columnValues!=null&&listDto.columnValues.size()!=0) {
			return "/page/externallist/content";
		}
		else {
			// 履歴がありません。と表示する
			return "/page/list/history";
		}
	}
}
