/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.SummaryListDto;
import jp.ecom_plat.saigaitask.entity.db.DisastersituationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.form.page.ExternalListForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.ExternallistService;
import jp.ecom_plat.saigaitask.service.db.DisastersituationhistoryDataService;
import jp.ecom_plat.saigaitask.service.db.DisastersummaryhistoryDataService;

/**
 * 被災集計機能の総括表のアクション.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class DisastersummaryGeneralizationAction extends AbstractPageAction {

	protected ExternalListForm externalListForm;
	// Service
	@Resource protected DisastersituationhistoryDataService disastersituationhistoryDataService;
	@Resource protected DisastersummaryhistoryDataService disastersummaryhistoryDataService;
	@Resource protected ExternallistService externallistService;
	// Dto
	@Resource protected SummaryListDto summaryListDto;

	/** ページ種別 */
	public static final String PAGE_TYPE = "disastersummaryGeneralization";

	/** リスト画面用Dto */
	public ListDto listDto;
	/** サブリストのDto */
	public List<ListDto> listDtos;
	/** システムが自動追加するユーザ日時列 */
	public String USERDATE_ROW_NAME() { return lang.__("User date and time<!--2-->"); }

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("externalListForm", externalListForm);
		model.put("listDto", listDto);
		model.put("listDtos", listDtos);
	}

	/**
	 * 総括表ページ
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/disastersummaryGeneralization","/page/disastersummaryGeneralization/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ExternalListForm externalListForm) {
		this.externalListForm = externalListForm;
		initPage(PAGE_TYPE, externalListForm);

		listDtos = new ArrayList<ListDto>();

		// 総括表
		SummaryListDto generalizationListDto = new SummaryListDto();
		generalizationListDto.title = lang.__("Aggregation/summary table");

		//
		// 列の設定
		//
		generalizationListDto.addLocalgovnameRow();
		generalizationListDto.columnNames.add(USERDATE_ROW_NAME());
		// 被害項目の列は事前に定義できないため、
		// 行を生成しながら列を拡張する

		//
		// 行の設定
		//
		// 自治体毎の集計
		generalizationListDto.columnValues = new ArrayList<List<String>>();

		// 災害グループの自治体情報と記録データIDを取得
		LinkedHashMap<LocalgovInfo, Long> trackgroupLocalgovInfoMap = externallistService.getTrackgroupLocalgovInfoMap();
		for(Map.Entry<LocalgovInfo, Long> entry : trackgroupLocalgovInfoMap.entrySet()) {

			// 総括・集計行
			List<String> values = new ArrayList<String>();

			// 一列目に自治体名
			LocalgovInfo localgovInfo =  entry.getKey();
			String localgovName = localgovInfo.pref;
			if(StringUtil.isNotEmpty(localgovInfo.city)) localgovName = localgovInfo.city;
			values.add(localgovName);

			// 自治体の被災集計を取得
			Long trackdataid = entry.getValue();
			ListDto disastersummaryListDto = createDisastersummaryListDto(trackdataid, true);
			disastersummaryListDto.title = localgovName;

			// デバッグ用、画面に表を表示する
			//listDtos.add(disastersummaryListDto);

			// 被災集計表の行を走査する
			List<String> columnValues = null;
			if(0<disastersummaryListDto.columnValues.size()) columnValues = disastersummaryListDto.columnValues.get(0);

			// 既知の被害項目列を追加
			for(String columnName : generalizationListDto.columnNames) {
				// 自治体名カラムは除く
				if(summaryListDto.LOCALGOVNAME_ROW_NAME().equals(columnName)) continue;

				// 被災集計表から被害項目値を取得
				String value = SummaryListDto.VALUE_UNDEFINED;
				int index = disastersummaryListDto.columnNames.indexOf(columnName);
				if(0<=index) value = columnValues.get(index);
				// 行へ値を追加
				values.add(value);
			}

			// 新規の被害項目列があるかチェック
			for(int columnNamesIndex=0; columnNamesIndex<disastersummaryListDto.columnNames.size(); columnNamesIndex++) {
				String columnName = disastersummaryListDto.columnNames.get(columnNamesIndex);
				int index = generalizationListDto.columnNames.indexOf(columnName);
				// 未追加の被害項目列であれば
				if(index==-1) {
					// 列を追加
					generalizationListDto.columnNames.add(columnName);
					// すでに追加した行を拡張
					for(int i=0; i<generalizationListDto.columnValues.size(); i++) {
						generalizationListDto.columnValues.get(i).add(SummaryListDto.VALUE_UNDEFINED);
					}
					// 作成中の行に追加
					String value = columnValues.get(columnNamesIndex);
					values.add(value);
				}
			}

			// 総括・集計行を追加
			generalizationListDto.columnValues.add(values);
		}

		// 合計行
		generalizationListDto.total();

		// jsp へ ListDto を渡す
		listDto = generalizationListDto;
		listDtos.add(listDto);

		setupModel(model);
		return "/page/"+ExternallistAction.PAGE_TYPE+"/index";
	}

	/**
	 * 最新の被災集計を ListDto で取得する.
	 * @param trackdataid 記録データID
	 * @param onlyTotal 合計の表のみを取得(手入力がある場合は優先)
	 *                  false の場合は、エリア名を1列目にもつエリア別表を生成する.
	 * @return リスト画面用Dto
	 */
	public ListDto createDisastersummaryListDto(Long trackdataid, boolean onlyTotal) {

		boolean addUserdate = true;

		// リストを初期化
		ListDto listDto = new ListDto();
		listDto.columnNames = new ArrayList<String>();
		listDto.columnValues = new ArrayList<List<String>>();

		if(trackdataid==null) {
			return listDto;
		}

		// 被災集計履歴の最新を取得する
		DisastersummaryhistoryData disastersummaryhistoryData = disastersummaryhistoryDataService.findCurrentByTrackdataid(trackdataid);
		if(disastersummaryhistoryData!=null) {

			// エリア別表の場合
			if(!onlyTotal) {
				// 1列目は地域名で固定
				listDto.columnNames.add(lang.__("District name"));
			}
			if(addUserdate) {
				listDto.columnNames.add(USERDATE_ROW_NAME());
			}

			// データの取得
			List<DisastersituationhistoryData> disastersituationhistoryDatas = disastersituationhistoryDataService.findByDisastersummaryhistoryid(disastersummaryhistoryData.id);

			// エリア数分の行の生成
			for(int num=1; num<=disastersummaryhistoryData.areanum; num++) {
				List<String> values = new ArrayList<String>();

				// エリア別表の場合
				if(!onlyTotal) {
					// エリア名の取得
					Field areaField = ReflectionUtil.getField(DisastersummaryhistoryData.class, "area"+num);
					String areaName = FieldUtil.getString(areaField, disastersummaryhistoryData);
					values.add(areaName);
				}
				if(addUserdate) {
					values.add(disastersummaryhistoryData.usertime);
				}

				// エリアの被害項目値のフィールドを取得
				Field areapeopleField = ReflectionUtil.getField(DisastersituationhistoryData.class, "area"+num+"people");

				// 各被災項目の値を取得
				for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDatas) {
					// 被害項目名がなければスキップ
					if(StringUtil.isEmpty(disastersituationhistoryData.damageitem)) continue;

					// １つ目のエリアの場合は、列を初期化する
					if(num==1) {
						// カラム名を、"被害項目名(単位)" のフォーマットで組み立て
						String columnName = "";
						if(StringUtil.isNotEmpty(disastersituationhistoryData.damageitem)) columnName += disastersituationhistoryData.damageitem;
						if(StringUtil.isNotEmpty(disastersituationhistoryData.unit)) columnName += "(" + disastersituationhistoryData.unit + ")";
						listDto.columnNames.add(columnName);
					}

					// エリア別表の場合
					if(!onlyTotal) {
						// 行に被災項目の値を追加
						Integer areaPeople = (Integer) FieldUtil.get(areapeopleField, disastersituationhistoryData);
						values.add(String.valueOf(areaPeople));
					}
					// 合計のみの表の場合
					else {
						// 行に被災項目の合計値を追加
						Integer total = disastersituationhistoryData.autototal;
						Integer manualtotal = disastersituationhistoryData.manualtotal;
						if(manualtotal!=null && 0<manualtotal) total = manualtotal;
						values.add(String.valueOf(total));
					}
				}

				// 1行追加
				listDto.columnValues.add(values);

				// 合計のみの表の場合は1行のみ
				if(onlyTotal) break;
			}
		}

		return listDto;
	}
}
