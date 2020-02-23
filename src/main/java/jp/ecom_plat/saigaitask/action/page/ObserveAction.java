/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Observ;
import jp.ecom_plat.saigaitask.constant.TMObsrvtn;
import jp.ecom_plat.saigaitask.dto.ObservListDto;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleData;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservlistInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.form.page.ObserveForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.db.ObservatorydamInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryrainInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryriverInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservlistInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.TelemeterDataService;

import org.seasar.framework.beans.util.Beans;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 監視・観測メニューのアクションクラス
 * spring checked take 5/5
 * 
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ObserveAction extends AbstractPageAction {

	/** アクションフォーム */
	protected ObserveForm observeForm;
	
	@Resource
	protected ObservlistInfoService observlistInfoService;
	@Resource
	protected ObservmenuInfoService observmenuInfoService;
	@Resource
	protected ObservatoryrainInfoService observatoryrainInfoService;
	@Resource
	protected ObservatoryriverInfoService observatoryriverInfoService;
	@Resource
	protected ObservatorydamInfoService observatorydamInfoService;
	@Resource
	protected TelemeterDataService telemeterDataService;

	/** 時刻アイテム */
	public List<Timestamp> timeItems = new ArrayList<Timestamp>();
	/** データアイテム */
	public List<Map<Timestamp, TelemeterData>> datamapItems = new ArrayList<Map<Timestamp, TelemeterData>>();
	/** 矢印アイテム */
	public Map<Integer, Map<Timestamp, String>> arrowmapItems = new HashMap<Integer, Map<Timestamp, String>>();
	/** 名称アイテム */
	public List<ObservListDto> observItems = new ArrayList<ObservListDto>();
	/** 監視観測メニュー */
	public ObservmenuInfo omenuInfo;
	
	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("timeItems", timeItems);
		model.put("datamapItems", datamapItems);
		model.put("arrowmapItems", arrowmapItems);
		model.put("observItems", observItems);
		model.put("omenuInfo", omenuInfo);
		model.put("observeForm", observeForm);
	}
	
	/**
	 * 監視観測ページを表示する
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/observe","/page/observe/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ObserveForm observeForm) {
		this.observeForm = observeForm;
		initPage("observe", observeForm);
	
		long menuid = pageDto.getMenuInfo().id;
		
		getData(menuid, Observ.MIN);
		
		setupModel(model);
		
		// 入力画面
		return "page/observe/index";
	}

	/**
	 * 監視観測データを取得
	 * @param menuid メニューID
	 * @param interval 表示単位
	 */
	public void getData(long menuid, int interval) {
		
		omenuInfo = observmenuInfoService.findByMenuInfoId(menuid);
		if(omenuInfo==null) {
			throw new ServiceException(lang.__("Set monitoring/observation menu info."));
		}
		
		observItems = new ArrayList<ObservListDto>();
		datamapItems = new ArrayList<Map<Timestamp, TelemeterData>>();
		List<ObservlistInfo> observlist = observlistInfoService.findByObservmenuInfoId(omenuInfo.id);
		int num = omenuInfo.count+1;
		if (interval == Observ.HOUR && num <= 24)
			num = 24+1;
		for (int i = 0; i < observlist.size(); i++) {
			ObservlistInfo observ = observlist.get(i);
			ObservListDto observDto = new ObservListDto();
			observDto.no = i;
			observDto.id = observ.observatoryinfoid;
			observDto.observid = observ.observid;
			List<TelemeterData> datalist = null;
			if (observ.observid.equals(Observ.RAIN)) {
				ObservatoryrainInfo rain = observatoryrainInfoService.findById(observ.observatoryinfoid);
				if (rain != null) {
					Long pointcode = rain.getPointFullCode();
					if (interval == Observ.MIN)
						datalist = telemeterDataService.getData(pointcode, observ.itemcode, num, /*タイムスライダー未対応*/null);
					else
						datalist = telemeterDataService.getHourData(pointcode, observ.itemcode, num, /*タイムスライダー未対応*/null);
					observDto.name = rain.name;
					observDto.itemname = TMObsrvtn.ITEM_CODE_MAP.get(TMObsrvtn.ITEMKIND_RAIN).get(observ.itemcode);
					observDto.typename = TMObsrvtn.ITEMKIND_CODE_MAP.get(TMObsrvtn.ITEMKIND_RAIN);
				}
			}
			else if (observ.observid.equals(Observ.RIVER)) {
				ObservatoryriverInfo river = observatoryriverInfoService.findById(observ.observatoryinfoid);
				if (river != null) {
					Long pointcode = river.getPointFullCode();
					if (interval == Observ.MIN)
						datalist = telemeterDataService.getData(pointcode, observ.itemcode, num, /*タイムスライダー未対応*/null);
					else
						datalist = telemeterDataService.getHourData(pointcode, observ.itemcode, num, /*タイムスライダー未対応*/null);
					observDto.name = river.name;
					observDto.itemname = TMObsrvtn.ITEM_CODE_MAP.get(TMObsrvtn.ITEMKIND_RIVER).get(observ.itemcode);
					observDto.typename = TMObsrvtn.ITEMKIND_CODE_MAP.get(TMObsrvtn.ITEMKIND_RIVER);
				}
			}
			else if (observ.observid.equals(Observ.DAM)) {
				ObservatorydamInfo dam = observatorydamInfoService.findById(observ.observatoryinfoid);
				if (dam != null) {
					Long pointcode = dam.getPointFullCode();
					if (interval == Observ.MIN)
						datalist = telemeterDataService.getData(pointcode, observ.itemcode, num, /*タイムスライダー未対応*/null);
					else
						datalist = telemeterDataService.getHourData(pointcode, observ.itemcode, num, /*タイムスライダー未対応*/null);
					observDto.name = dam.name;
					observDto.itemname = TMObsrvtn.ITEM_CODE_MAP.get(TMObsrvtn.ITEMKIND_DAM).get(observ.itemcode);
					observDto.typename = TMObsrvtn.ITEMKIND_CODE_MAP.get(TMObsrvtn.ITEMKIND_DAM);
				}
			}

			if (datalist != null) {
				Map<Timestamp, TelemeterData> map = new HashMap<Timestamp, TelemeterData>();
				for (TelemeterData data : datalist) {
					map.put(data.observtime, data);
				}				
				datamapItems.add(map);
				
				//時刻を取得
				for (TelemeterData data : datalist) {
					if (!timeItems.contains(data.observtime))
						timeItems.add(data.observtime);
				}

				//矢印
				if (observ.itemcode == 10) {
					Map<Timestamp, String> arrowmap = new HashMap<Timestamp, String>();
					TelemeterData pre = null;
					for (TelemeterData data : datalist) {
						if (data.val == null) continue;
						if (pre != null) {
							if (new BigDecimal(pre.val).equals(new BigDecimal(data.val)))
								arrowmap.put(pre.observtime, "→");
							else if (pre.val > data.val)
								arrowmap.put(pre.observtime, "<span style=\"color:#880000;\">↑</span>");
							else if (pre.val < data.val)
								arrowmap.put(pre.observtime, "↓");
						}
						pre = data;
					}
					arrowmapItems.put(Integer.valueOf(i), arrowmap);
				}
			}
			observItems.add(observDto);
		}
		Collections.sort(timeItems, compa);

		//項目のcolspanを計算
		ObservListDto preDto = new ObservListDto();
		for (ObservListDto observDto : observItems) {
			if (preDto.typename.equals(observDto.typename)) {
				preDto.typecol++;
				observDto.typecol = 0;
			}
			else
				preDto = observDto;
		}
		preDto = new ObservListDto();
		for (ObservListDto observDto : observItems) {
			if (preDto.name.equals(observDto.name)) {
				preDto.namecol++;
				observDto.namecol = 0;
			}
			else
				preDto = observDto;
		}		
	}

	/**
	 * 監視観測画面にて定期的にデータ更新するためのデータを提供するアクションメソッド
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/observe/telemeterdata/{menuid}/{interval}")
	@ResponseBody
	public String telemeterdata(Map<String,Object>model, @Valid @ModelAttribute ObserveForm observeForm, BindingResult result) {
		this.observeForm = observeForm;
		long menuid = observeForm.menuid;
		getData(menuid, observeForm.interval);

		SimpleDateFormat sdf = new SimpleDateFormat(lang.__("dd 'day'  HH:mm"));
		
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < timeItems.size(); i++) {
			Timestamp time = timeItems.get(i);
			if (i < omenuInfo.count) {
				buff.append("<tr>");
				buff.append("<td>");
				buff.append(sdf.format(time));
				buff.append("</td>");
				
				for (int j = 0; j < datamapItems.size(); j++) {
					Map<Timestamp, TelemeterData> datamap = datamapItems.get(j);
					TelemeterData data = datamap.get(time);
					String tdstyle = "";
					if (data != null && data.judgeresultstyleDataList.size() > 0) {
						tdstyle += "style=\"";
						List<String> styles = new ArrayList<String>();
						for (JudgeresultstyleData sdata : data.judgeresultstyleDataList) {
							if (sdata.judgeresultstyleInfo.judgemanInfo.localgovinfoid != loginDataDto.getLocalgovinfoid())
								continue;
							if (sdata.judgeresultstyleInfo != null && !styles.contains(sdata.judgeresultstyleInfo.style))
								styles.add(sdata.judgeresultstyleInfo.style);
						}
						for (String style : styles)
							tdstyle += style;
						tdstyle+="\"";
					}
					
					buff.append("<td "+tdstyle+">");
					if (data != null) {
						String val = "";
						if (data.val != null)
							val = data.val.toString();
						buff.append(val);
						ObservListDto ob = observItems.get(j);
						if (arrowmapItems.get(ob.no) != null) {
							buff.append("&nbsp;");
							val = arrowmapItems.get(ob.no).get(time);
							if (val == null) val = "";
							buff.append(val);
						}
					}
					buff.append("</td>");
				}
				buff.append("</tr>");
			}
		}
		//ResponseUtil.write(buff.toString());
		return buff.toString();
		
		//return null;
	}	
	
	/**
	 * 時刻を昇順にする
	 */
	Comparator<Timestamp> compa = new Comparator<Timestamp>(){
		public int compare(Timestamp o1, Timestamp o2){
			if (o1.getTime() < o2.getTime())
				return 1;
			else if (o1.getTime() > o2.getTime())
				return -1;
			else
				return 0;
		}
	};
}
