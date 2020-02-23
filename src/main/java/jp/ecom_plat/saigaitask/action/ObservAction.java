/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.ecom_plat.saigaitask.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import jp.ecom_plat.saigaitask.constant.Observ;
import jp.ecom_plat.saigaitask.constant.TMObsrvtn;
import jp.ecom_plat.saigaitask.dto.ObservDto;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.form.page.MapForm;
import jp.ecom_plat.saigaitask.service.db.ObservatorydamInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryrainInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryriverInfoService;
import jp.ecom_plat.saigaitask.service.db.TelemeterDataService;

import org.springframework.web.bind.annotation.PathVariable;


/**
 * 監視観測所の詳細画面を表示するアクションクラスです.
 * @author GOTO
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ObservAction extends AbstractAction {

	/** 雨量観測地点情報 サービスクラス */
	@Resource
	ObservatoryrainInfoService observatoryrainInfoService;

	/** 水位観測地点情報 サービスクラス */
	@Resource
	ObservatoryriverInfoService observatoryriverInfoService;

	/** ダム観測所情報 サービスクラス */
	@Resource
	ObservatorydamInfoService observatorydamInfoService;

	/** テレメータデータ サービスクラス */
	@Resource
	TelemeterDataService telemeterDataService;

	/** 観測種別の観測地点情報ID */
	public String id;
	/**
	 * 時間パラメータ
	 * タイムスライダーで時間指定した状態で、
	 * 地図画面上の観測所アイコンをクリックして詳細画面を表示する場合に、
	 * グラフ表示の基点となる日時がセットされる。
	 */
	public String time;
	/** 観測間隔 */
	public int timeaxis = Observ.HOUR;
	/** 観測時刻 */
	public String observdate;
	/** 緯度 */
	public int[] lat = new int[3];
	/** 経度 */
	public int[] lon = new int[3];

	/** 雨量観測地点情報 */
	public ObservatoryrainInfo observatoryrainInfo;
	/** 河川水位観測地点情報 */
	public ObservatoryriverInfo observatoryriverInfo;
	/** ダム観測所情報 */
	public ObservatorydamInfo observatorydamInfo;

	/** 表用に加工したテレメータデータ */
	public List<ObservDto> tableItems = new ArrayList<ObservDto>();
	/** グラフ用に加工したテレメータデータ */
	public List<ObservDto> graphItems = new ArrayList<ObservDto>();

	/** 矢印アイテム */
	//public Map<Integer, Map<Timestamp, String>> arrowmapItems = new HashMap<Integer, Map<Timestamp, String>>();

	/** （水位詳細画面用）水位データの矢印 */
	public List<Integer> arrowItems = new ArrayList<Integer>();
	/** （水位詳細画面用）最新水位 */
	public String lastdata = null;
	/** （水位詳細画面用）最新水位データの矢印 */
	public String lastarrow = null;
	/** （水位詳細画面用）水位レベル */
	public Integer waterlevelIdx;
	
	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("id", id);
		model.put("time", time);
		model.put("timeaxis", timeaxis);
		model.put("observdate", observdate);
		model.put("lat", lat);
		model.put("lon", lon);
		model.put("observatoryrainInfo", observatoryrainInfo);
		model.put("observatoryriverInfo", observatoryriverInfo);
		model.put("observatorydamInfo", observatorydamInfo);
		model.put("tableItems", tableItems);
		model.put("graphItems", graphItems);
		model.put("arrowItems", arrowItems);
		model.put("lastdata", lastdata);
		model.put("lastarrow", lastarrow);
		model.put("waterlevelIdx", waterlevelIdx);
	}

	/**
	 * @return 表示データ時間配列
	 */
	public Date[] timeParams() {
		MapForm form = new MapForm();
		form.time = this.time;
		return form.timeParams(true);
	}

	/**
	 * デフォルト画面
	 */
	/*
	public String index() {
		return null;
	}*/

	/**
	 * 雨量詳細画面を表示する
	 * @param id 雨量観測地点情報ID
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/observ/rain/{id}")
	public String rainDefault(@PathVariable String id) {
		return "forward:/observ/rain/" + id + "/" + timeaxis;
	}

	/**
	 * 雨量詳細画面を表示する
	 * @param id 雨量観測地点情報ID
	 * @param timeaxis データ取得間隔
	 * @return フォワード先、雨量詳細画面
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/observ/rain/{id}/{timeaxis}")
	public String rain(Map<String,Object>model, @PathVariable String id, @PathVariable String timeaxis) {
		this.timeaxis = Integer.parseInt(timeaxis);
		this.id = id;
		//雨量観測地点情報を取得
		observatoryrainInfo = observatoryrainInfoService.findById(Long.parseLong(id));

		if (observatoryrainInfo != null) {

			//緯度経度を取得
			if (observatoryrainInfo.latitude != null && observatoryrainInfo.longitude != null) {
				double dlat = observatoryrainInfo.latitude;
				double dlon = observatoryrainInfo.longitude;
				lat[0] = (int)dlat;
				lat[1] = (int)((dlat - lat[0]) * 60);
				lat[2] = (int)(((dlat - lat[0]) * 60 - lat[1]) * 60);
				lon[0] = (int)dlon;
				lon[1] = (int)((dlon - lon[0]) * 60);
				lon[2] = (int)(((dlon - lon[0]) * 60 - lon[1]) * 60);
			}

			tableItems = new ArrayList<ObservDto>();
			graphItems = new ArrayList<ObservDto>();
			arrowItems = new ArrayList<Integer>();

			//観測局特定フルコード（管理事務所番号5d＋データ種別3d＋観測所番号5d）
			Long pointcode = observatoryrainInfo.getPointFullCode();
			//データ項目コード
			int itemcode;

			//10分雨量（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist1 = null;
			itemcode = 10;
			datalist1 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );
			//時間雨量（60分毎）テレメータデータを取得
			List<TelemeterData> datalist2 = null;
			itemcode = 30;
			datalist2 = telemeterDataService.getData(pointcode, itemcode, (24+1), timeParams());
			//累加雨量（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist3 = null;
			itemcode = 70;
			datalist3 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );

			//時刻一覧（10分毎）を作成
			List<Timestamp> timelist1 = new Vector<Timestamp>();
			setTimeList(timelist1, datalist1, timeParams());
			Collections.sort(timelist1, compa2);
			//時刻一覧（毎正時）を作成
			List<Timestamp> timelist2 = new Vector<Timestamp>();
			setTimeList60(timelist2, datalist2, timeParams());
			Collections.sort(timelist2, compa2);


			//日付と時刻のフォーマットを作成
			SimpleDateFormat sdf_ymd6 = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf_ymd4 = new SimpleDateFormat("yyyy/M/d");
			SimpleDateFormat sdf_HHmm = new SimpleDateFormat("HH:mm");	// eg, 23:59 + 1 hour =>> 00:59
			SimpleDateFormat sdf_kkmm = new SimpleDateFormat("kk:mm");	// eg, 23:59 + 1 hour =>> 24:59

			//雨量と累加雨量データのフォーマットを作成
			DecimalFormat df_dec1 = new DecimalFormat("0.0");		//有効桁数小数第一位


			String dateValue = null;		//データの観測時刻の日付の値
			String dateLabel = null;		//データの観測時刻の日付のラベル
			String timeValue = null;		//データの観測時刻の時刻の値
			String timeLabel = null;		//データの観測時刻の時刻のラベル

			//テーブル用
			List<Timestamp> tabletimelist;
			List<TelemeterData> tabledatalist1 = null;
			List<TelemeterData> tabledatalist2 = null;
			if (this.timeaxis == Observ.MIN) {
				tabletimelist = timelist1;
				tabledatalist1 = datalist1;
				tabledatalist2 = datalist3;
			} else {
				this.timeaxis = Observ.HOUR;
				tabletimelist = timelist2;
				tabledatalist1 = datalist2;
				tabledatalist2 = datalist3;
			}

			//グラフ用
			List<Timestamp> graphtimelist;
			List<TelemeterData> graphdatalist1 = null;
			List<TelemeterData> graphdatalist2 = null;
			graphtimelist = timelist2;
			graphdatalist1 = datalist2;
			graphdatalist2 = datalist3;

			//テーブル用のデータをDtoに格納
			Timestamp datetime = null;
			TelemeterData pre1 = null;
			for (int i = 0; i < tabletimelist.size(); i++) {
				ObservDto observDto = new ObservDto();

				//データの観測時刻をDtoに格納
				datetime = tabletimelist.get(i);

				//データの観測時刻の値
				dateValue = sdf_ymd4.format(datetime);
				timeValue = sdf_HHmm.format(datetime);
				//データの観測時刻のラベル
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd4.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd4.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				//データの観測時刻をDtoに格納
				observDto.setDatevalue(dateValue);
				observDto.setDatelabel(dateLabel);
				observDto.setTimevalue(timeValue);
				observDto.setTimelabel(timeLabel);

				//雨量データをDtoに格納
				if (tabledatalist1 != null) {
					boolean found = false;
					Iterator<TelemeterData> it = tabledatalist1.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata1((t.val != null) ? df_dec1.format(t.val) : null);
							observDto.setContentscode1(t.contentscode);
							observDto.setContentsname1(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							//矢印
							if (t.val == null) {
								pre1 = null;
								continue;
							}
							if (pre1 != null) {
								if (new BigDecimal(t.val).equals(new BigDecimal(pre1.val)))
									observDto.setItemdataarrow1("→");
								else if (t.val > pre1.val)
									observDto.setItemdataarrow1("<span style=\"color:red;\">↑</span>");
								else if (t.val < pre1.val)
									observDto.setItemdataarrow1("↓");
							}
							pre1 = t;
							found = true;
							break;
						}
					}
					if (found == false) {
						pre1 = null;
					}
				}

				//累加雨量データをDtoに格納
				if (tabledatalist2 != null ) {
					Iterator<TelemeterData> it = tabledatalist2.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata2((t.val != null) ? df_dec1.format(t.val) : null);
							observDto.setContentscode2(t.contentscode);
							observDto.setContentsname2(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				if (i == 0) continue;

				//詳細画面のテーブル用のデータに追加
				tableItems.add(observDto);
			}

			//グラフ用のデータをDtoに格納
			for (int i = 0; i < graphtimelist.size(); i++) {
				ObservDto observDto = new ObservDto();

				//データの観測時刻をDtoに格納
				datetime = graphtimelist.get(i);
				//データの観測時刻の値
				dateValue = sdf_ymd4.format(datetime);
				timeValue = sdf_HHmm.format(datetime);
				//データの観測時刻のラベル
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd4.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd4.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				//データの観測時刻をDtoに格納
				observDto.setDatevalue(dateValue);
				observDto.setDatelabel(dateLabel);
				observDto.setTimevalue(timeValue);
				observDto.setTimelabel(timeLabel);

				//グラフ用の雨量データをDtoに格納
				if (graphdatalist1 != null ) {
					Iterator<TelemeterData> it = graphdatalist1.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata1((t.val != null) ? df_dec1.format(t.val) : null);
							observDto.setContentscode1(t.contentscode);
							observDto.setContentsname1(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				//グラフ用の累加雨量データをDtoに格納
				if (graphdatalist2 != null ) {
					Iterator<TelemeterData> it = graphdatalist2.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata2((t.val != null) ? df_dec1.format(t.val) : null);
							observDto.setContentscode2(t.contentscode);
							observDto.setContentsname2(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				if (i == 0) continue;

				//詳細画面のグラフ用のデータに追加
				graphItems.add(observDto);
			}

			//詳細画面の観測時刻のラベル
			if (tabletimelist.size() > 0) {
				datetime = tabletimelist.get(tabletimelist.size() - 1);
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd6.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd6.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				StringBuffer sb = new StringBuffer("");
				sb.append(dateLabel);
				sb.append("　");
				sb.append(timeLabel);
				observdate = sb.toString();
			}
		}

		setupModel(model);

		return "/observ/rain";
	}

	/**
	 * 水位詳細画面を表示する
	 * @param id 河川水位観測地点情報ID
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/observ/river/{id}")
	public String riverDefault(@PathVariable String id) {
		return "forward:/observ/river/" + id + "/" + timeaxis;
	}

	/**
	 * 水位詳細画面を表示する
	 * @param id 河川水位観測地点情報ID
	 * @param timeaxis データ取得間隔
	 * @return フォワード先、水位詳細画面
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/observ/river/{id}/{timeaxis}")
	public String river(Map<String,Object>model, @PathVariable String id, @PathVariable String timeaxis) {
		this.id = id;
		this.timeaxis = Integer.parseInt(timeaxis);
		//水位観測地点情報を取得
		observatoryriverInfo = observatoryriverInfoService.findById(Long.parseLong(id));

		if (observatoryriverInfo != null) {

			//観測局特定フルコード（管理事務所番号5d＋データ種別3d＋観測所番号5d）
			Long pointcode = observatoryriverInfo.getPointFullCode();
			//データ項目コード
			int itemcode;

			tableItems = new ArrayList<ObservDto>();
			graphItems = new ArrayList<ObservDto>();
			arrowItems = new ArrayList<Integer>();

			//河川水位（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist1 = null;
			itemcode = 10;
			datalist1 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );
			//水位変化量（10分毎）テレメータデータを取得
			List<TelemeterData> datalist2 = null;
			itemcode = 50;
			datalist2 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );
			//水位変化量（毎正時）のテレメータデータを取得
			List<TelemeterData> datalist3 = null;
			itemcode = 70;
			datalist3 = telemeterDataService.getData(pointcode, itemcode, (24+1), timeParams());

			//時刻一覧（10分毎）を作成
			List<Timestamp> timelist1 = new Vector<Timestamp>();
			setTimeList(timelist1, datalist1, timeParams());
			Collections.sort(timelist1, compa2);
			//時刻一覧（毎正時）を作成
			List<Timestamp> timelist2 = new Vector<Timestamp>();
			setTimeList60(timelist2, datalist1, timeParams());
			Collections.sort(timelist2, compa2);


			//日付と時刻のフォーマットを作成
			SimpleDateFormat sdf_ymd6 = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf_ymd4 = new SimpleDateFormat("yyyy/M/d");
			SimpleDateFormat sdf_HHmm = new SimpleDateFormat("HH:mm");	// eg, 23:59 + 1 hour =>> 00:59
			SimpleDateFormat sdf_kkmm = new SimpleDateFormat("kk:mm");	// eg, 23:59 + 1 hour =>> 24:59

			//水位データのフォーマットを作成
			DecimalFormat df_dec2 = new DecimalFormat("0.00");


			String dateValue;		//データの観測時刻の日付の値
			String dateLabel;		//データの観測時刻の日付のラベル
			String timeValue;		//データの観測時刻の時刻の値
			String timeLabel;		//データの観測時刻の時刻のラベル

			//テーブル用
			List<Timestamp> tabletimelist;
			List<TelemeterData> tabledatalist1 = null;	//水位
			List<TelemeterData> tabledatalist2 = null;	//水位変化量
			if (this.timeaxis == Observ.MIN) {
				tabletimelist = timelist1;
				tabledatalist1 = datalist1;
				tabledatalist2 = datalist2;
			} else {
				this.timeaxis = Observ.HOUR;
				tabletimelist = timelist2;
				tabledatalist1 = datalist1;
				tabledatalist2 = datalist3;
			}

			//テーブル用のデータをDtoに格納
			Timestamp datetime = null;
			TelemeterData pre1 = null;
			//TelemeterData pre2 = null;
			for (int i = 0; i < tabletimelist.size(); i++) {
				ObservDto observDto = new ObservDto();

				//データの観測時刻をDtoに格納
				datetime = tabletimelist.get(i);
				//データの観測時刻の値
				dateValue = sdf_ymd4.format(datetime);
				timeValue = sdf_HHmm.format(datetime);
				//データの観測時刻のラベル
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					// 24:00の場合、1日前の日付を求める（2/3 00:00 =>> 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd4.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd4.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				//データの観測時刻をDtoに格納
				observDto.setDatevalue(dateValue);
				observDto.setDatelabel(dateLabel);
				observDto.setTimevalue(timeValue);
				observDto.setTimelabel(timeLabel);

				//水位データをDtoに格納
				if (tabledatalist1 != null) {
					boolean found = false;
					Iterator<TelemeterData> it = tabledatalist1.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata1((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode1(t.contentscode);
							observDto.setContentsname1(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							//矢印
							if (t.val == null) {
								pre1 = null;
								break;
							}
							if (pre1 != null) {
								if (new BigDecimal(t.val).equals(new BigDecimal(pre1.val)))
									observDto.setItemdataarrow1("→");
								else if (t.val > pre1.val)
									observDto.setItemdataarrow1("<span style=\"color:red;\">↑</span>");
								else if (t.val < pre1.val)
									observDto.setItemdataarrow1("↓");
							}
							pre1 = t;
							found = true;
							break;
						}
					}
					if (found == false) {
						pre1 = null;
					}
				}

				//水位変化量データをDtoに格納
				if (tabledatalist2 != null) {
					Iterator<TelemeterData> it = tabledatalist2.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata2((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode2(t.contentscode);
							observDto.setContentsname2(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							//矢印
							if (t.val == null) {
								break;
							}
							if (t.val == 0)
								observDto.setItemdataarrow2("→");
							else if (t.val > 0)
								observDto.setItemdataarrow2("<span style=\"color:red;\">↑</span>");
							else if (t.val < 0)
								observDto.setItemdataarrow2("↓");
							break;
						}
					}
				}

				if (i == 0) continue;

				//詳細画面のテーブル用の監視データに追加
				tableItems.add(observDto);
			}


			//グラフ用
			List<Timestamp> graphtimelist;
			List<TelemeterData> graphdatalist1 = null;	//水位
			graphtimelist = timelist2;
			graphdatalist1 = datalist1;

			//グラフ用のデータをDtoに格納
			for (int i = 0; i < graphtimelist.size(); i++) {
				ObservDto observDto = new ObservDto();

				//データの観測時刻をDtoに格納
				datetime = graphtimelist.get(i);
				//データの観測時刻の値
				dateValue = sdf_ymd4.format(datetime);
				timeValue = sdf_HHmm.format(datetime);
				//データの観測時刻のラベル
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd4.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd4.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				//データの観測時刻をDtoに格納
				observDto.setDatevalue(dateValue);
				observDto.setDatelabel(dateLabel);
				observDto.setTimevalue(timeValue);
				observDto.setTimelabel(timeLabel);

				//グラフ用の水位データをDtoに格納
				if (graphdatalist1 != null ) {
					Iterator<TelemeterData> it = graphdatalist1.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata1((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode1(t.contentscode);
							observDto.setContentsname1(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				if (i == 0) continue;

				//詳細画面のグラフ用のデータに追加
				graphItems.add(observDto);
			}


			//水位の表示カラム位置
			Float waterlevel1 = observatoryriverInfo.waterlevel1;
			Float waterlevel2 = observatoryriverInfo.waterlevel2;
			Float waterlevel3 = observatoryriverInfo.waterlevel3;
			Float waterlevel4 = observatoryriverInfo.waterlevel4;
			if(0<tableItems.size()) {
				ObservDto dto = tableItems.get(tableItems.size() - 1);
				lastdata = (dto.getItemdata1() != null) ? dto.getItemdata1() : null;
				lastarrow = (dto.getItemdataarrow1() != null) ? dto.getItemdataarrow1() : null;
				waterlevelIdx =
						(dto.getItemdata1() == null) ? -1 :
						(waterlevel1 == null || Double.valueOf(dto.getItemdata1()) < waterlevel1) ? 0 :
						(waterlevel2 == null || Double.valueOf(dto.getItemdata1()) < waterlevel2) ? 1 :
						(waterlevel3 == null || Double.valueOf(dto.getItemdata1()) < waterlevel3) ? 2 :
						(waterlevel4 == null || Double.valueOf(dto.getItemdata1()) < waterlevel4) ? 3 : 4;
			}

			//詳細画面の観測時刻のラベル
			if (tabletimelist.size() > 0) {
				datetime = tabletimelist.get(tabletimelist.size() - 1);

				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd6.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd6.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}

				StringBuffer sb = new StringBuffer("");
				sb.append(dateLabel);
				sb.append("　");
				sb.append(timeLabel);
				observdate = sb.toString();
			}
		}

		setupModel(model);

		return "/observ/river";
	}

	/**
	 * ダム諸元詳細画面を表示する
	 * @param id ダム観測所情報ID
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/observ/dam/{id}")
	public String damDefault(@PathVariable String id) {
		return "forward:/observ/dam/" + id + "/" + timeaxis;
	}

	/**
	 * ダム諸元詳細画面を表示する
	 * @param id ダム観測所情報ID
	 * @param timeaxis データ取得間隔
	 * @return フォワード先、ダム諸元詳細画面
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/observ/dam/{id}/{timeaxis}")
	public String dam(Map<String,Object>model, @PathVariable String id, @PathVariable String timeaxis) {
		this.id = id;
		this.timeaxis = Integer.parseInt(timeaxis);
		//ダム観測所情報を取得
		observatorydamInfo = observatorydamInfoService.findById(Long.parseLong(id));

		if (observatorydamInfo != null) {

			//緯度経度を取得
			if (observatorydamInfo.latitude != null && observatorydamInfo.longitude != null) {
				double dlat = observatorydamInfo.latitude;
				double dlon = observatorydamInfo.longitude;
				lat[0] = (int)dlat;
				lat[1] = (int)((dlat - lat[0]) * 60);
				lat[2] = (int)(((dlat - lat[0]) * 60 - lat[1]) * 60);
				lon[0] = (int)dlon;
				lon[1] = (int)((dlon - lon[0]) * 60);
				lon[2] = (int)(((dlon - lon[0]) * 60 - lon[1]) * 60);
			}

			tableItems = new ArrayList<ObservDto>();
			graphItems = new ArrayList<ObservDto>();
			arrowItems = new ArrayList<Integer>();

			//観測局特定フルコード（管理事務所番号5d＋データ種別3d＋観測所番号5d）
			Long pointcode = observatorydamInfo.getPointFullCode();
			//データ項目コード
			int itemcode;

			//貯水位（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist1 = null;
			itemcode = 10;
			datalist1 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );
			//貯水率（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist2 = null;
			itemcode = 40;
			datalist2 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );
			//全流入量（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist3 = null;
			itemcode = 50;
			datalist3 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );
			//全放流量（10分毎）のテレメータデータを取得
			List<TelemeterData> datalist4 = null;
			itemcode = 70;
			datalist4 = telemeterDataService.getData(pointcode, itemcode, ((24+1)*(60/10)), timeParams() );

			//時刻一覧（10分毎）を作成
			List<Timestamp> timelist1 = new Vector<Timestamp>();
			setTimeList(timelist1, datalist1, timeParams());
			Collections.sort(timelist1, compa2);
			//時刻一覧（毎正時）を作成
			List<Timestamp> timelist2 = new Vector<Timestamp>();
			setTimeList60(timelist2, datalist1, timeParams());
			Collections.sort(timelist2, compa2);


			//日付と時刻のフォーマットを作成
			SimpleDateFormat sdf_ymd6 = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf_ymd4 = new SimpleDateFormat("yyyy/M/d");
			SimpleDateFormat sdf_HHmm = new SimpleDateFormat("HH:mm");	// eg, 23:59 + 1 hour =>> 00:59
			SimpleDateFormat sdf_kkmm = new SimpleDateFormat("kk:mm");	// eg, 23:59 + 1 hour =>> 24:59

			//貯水率データのフォーマットを作成
			DecimalFormat df_dec1 = new DecimalFormat("0.0");
			//貯水位と流量データのフォーマットを作成
			DecimalFormat df_dec2 = new DecimalFormat("0.00");


			String dateValue;		//データの観測時刻の日付の値
			String dateLabel;		//データの観測時刻の日付のラベル
			String timeValue;		//データの観測時刻の時刻の値
			String timeLabel;		//データの観測時刻の時刻のラベル

			//テーブル用
			List<Timestamp> tabletimelist;
			List<TelemeterData> tabledatalist1 = null;	//貯水位
			List<TelemeterData> tabledatalist2 = null;	//貯水率
			List<TelemeterData> tabledatalist3 = null;	//全流入量
			List<TelemeterData> tabledatalist4 = null;	//全放流量
			if (this.timeaxis == Observ.MIN) {
				tabletimelist = timelist1;
			} else {
				this.timeaxis = Observ.HOUR;
				tabletimelist = timelist2;
			}
			tabledatalist1 = datalist1;
			tabledatalist2 = datalist2;
			tabledatalist3 = datalist3;
			tabledatalist4 = datalist4;

			//グラフ用
			List<Timestamp> graphtimelist;
			List<TelemeterData> graphdatalist1 = null;	//貯水位
			List<TelemeterData> graphdatalist2 = null;	//貯水率
			List<TelemeterData> graphdatalist3 = null;	//全流入量
			List<TelemeterData> graphdatalist4 = null;	//全放流量
			graphtimelist = timelist2;
			graphdatalist1 = datalist1;
			graphdatalist2 = datalist2;
			graphdatalist3 = datalist3;
			graphdatalist4 = datalist4;

			//テーブル用のデータをDtoに格納
			Timestamp datetime = null;
			TelemeterData pre1 = null;
			//TelemeterData pre2 = null;
			TelemeterData pre3 = null;
			TelemeterData pre4 = null;
			for (int i = 0; i < tabletimelist.size(); i++) {
				ObservDto observDto = new ObservDto();

				//データの観測時刻をDtoに格納
				datetime = tabletimelist.get(i);
				//データの観測時刻の値
				dateValue = sdf_ymd4.format(datetime);
				timeValue = sdf_HHmm.format(datetime);
				//データの観測時刻のラベル
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd4.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd4.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				//データの観測時刻をDtoに格納
				observDto.setDatevalue(dateValue);
				observDto.setDatelabel(dateLabel);
				observDto.setTimevalue(timeValue);
				observDto.setTimelabel(timeLabel);

				//貯水位データをDtoに格納
				if (tabledatalist1 != null) {
					boolean found = false;
					Iterator<TelemeterData> it = tabledatalist1.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata1((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode1(t.contentscode);
							observDto.setContentsname1(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							//矢印
							if (t.val == null) {
								pre1 = null;
								break;
							}
							if (pre1 != null) {
								if (new BigDecimal(t.val).equals(new BigDecimal(pre1.val)))
									observDto.setItemdataarrow1("→");
								else if (t.val > pre1.val)
									observDto.setItemdataarrow1("<span style=\"color:red;\">↑</span>");
								else if (t.val < pre1.val)
									observDto.setItemdataarrow1("↓");
							}
							pre1 = t;
							found = true;
							break;
						}
					}
					if (found == false) {
						pre1 = null;
					}
				}

				//貯水率データをDtoに格納
				if (tabledatalist2 != null ) {
					Iterator<TelemeterData> it = tabledatalist2.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata2((t.val != null) ? df_dec1.format(t.val) : null);
							observDto.setContentscode2(t.contentscode);
							observDto.setContentsname2(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				//全流入量データをDtoに格納
				if (tabledatalist3 != null) {
					boolean found = false;
					Iterator<TelemeterData> it = tabledatalist3.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata3((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode3(t.contentscode);
							observDto.setContentsname3(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							//矢印
							if (t.val == null) {
								pre3 = null;
								break;
							}
							if (pre3 != null) {
								if (new BigDecimal(t.val).equals(new BigDecimal(pre3.val)))
									observDto.setItemdataarrow3("→");
								else if (t.val > pre3.val)
									observDto.setItemdataarrow3("<span style=\"color:red;\">↑</span>");
								else if (t.val < pre3.val)
									observDto.setItemdataarrow3("↓");
							}
							pre3 = t;
							found = true;
							break;
						}
					}
					if (found == false) {
						pre3 = null;
					}
				}

				//全放流量データをDtoに格納
				if (tabledatalist4 != null) {
					boolean found = false;
					Iterator<TelemeterData> it = tabledatalist4.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata4((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode4(t.contentscode);
							observDto.setContentsname4(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							//矢印
							if (t.val == null) {
								pre4 = null;
								break;
							}
							if (pre4 != null) {
								if (new BigDecimal(t.val).equals(new BigDecimal(pre4.val)))
									observDto.setItemdataarrow4("→");
								else if (t.val > pre4.val)
									observDto.setItemdataarrow4("<span style=\"color:red;\">↑</span>");
								else if (t.val < pre4.val)
									observDto.setItemdataarrow4("↓");
							}
							pre4 = t;
							found = true;
							break;
						}
					}
					if (found == false) {
						pre4 = null;
					}
				}

				if (i == 0) continue;

				//詳細画面のテーブル用のデータに追加
				tableItems.add(observDto);
			}

			//グラフ用のデータをDtoに格納
			for (int i = 0; i < graphtimelist.size(); i++) {
				ObservDto observDto = new ObservDto();

				//データの観測時刻をDtoに格納
				datetime = graphtimelist.get(i);
				//データの観測時刻の値
				dateValue = sdf_ymd4.format(datetime);
				timeValue = sdf_HHmm.format(datetime);
				//データの観測時刻のラベル
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd4.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd4.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				//データの観測時刻をDtoに格納
				observDto.setDatevalue(dateValue);
				observDto.setDatelabel(dateLabel);
				observDto.setTimevalue(timeValue);
				observDto.setTimelabel(timeLabel);

				//グラフ用の貯水位データをDtoに格納
				if (graphdatalist1 != null ) {
					Iterator<TelemeterData> it = graphdatalist1.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata1((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode1(t.contentscode);
							observDto.setContentsname1(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				//グラフ用の貯水率データをDtoに格納
				if (graphdatalist2 != null ) {
					Iterator<TelemeterData> it = graphdatalist2.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata2((t.val != null) ? df_dec1.format(t.val) : null);
							observDto.setContentscode2(t.contentscode);
							observDto.setContentsname2(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				//グラフ用の全流入量データをDtoに格納
				if (graphdatalist3 != null ) {
					Iterator<TelemeterData> it = graphdatalist3.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata3((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode3(t.contentscode);
							observDto.setContentsname3(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				//グラフ用の全放流量データをDtoに格納
				if (graphdatalist4 != null ) {
					Iterator<TelemeterData> it = graphdatalist4.iterator();
					while (it.hasNext()) {
						TelemeterData t = it.next();
						if (t.observtime.equals(datetime)) {
							observDto.setItemdata4((t.val != null) ? df_dec2.format(t.val) : null);
							observDto.setContentscode4(t.contentscode);
							observDto.setContentsname4(TMObsrvtn.CONTENTS_CODE_MAP.get(t.contentscode));
							break;
						}
					}
				}

				if (i == 0) continue;

				//詳細画面のグラフ用のデータに追加
				graphItems.add(observDto);
			}

			//詳細画面の観測時刻のラベル
			if (tabletimelist.size() > 0) {
				datetime = tabletimelist.get(tabletimelist.size() - 1);
				if (sdf_kkmm.format(datetime).equals("24:00")) {
					//24:00の場合は、1日前の日付を求める（2/3 00:00 => 2/2 24:00）
					Calendar cal = Calendar.getInstance();
					cal.setTime(datetime);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					dateLabel = sdf_ymd6.format(cal.getTime());
					timeLabel = sdf_kkmm.format(datetime);
				} else {
					dateLabel = sdf_ymd6.format(datetime);
					timeLabel = sdf_HHmm.format(datetime);
				}
				StringBuffer sb = new StringBuffer("");
				sb.append(dateLabel);
				sb.append("　");
				sb.append(timeLabel);
				observdate = sb.toString();
			}
		}

		setupModel(model);

		return "/observ/dam";
	}


	/**
	 * テレメータデータの取得結果から時刻リストを作る（10分毎）
	 * @param timelist
	 * @param telemeterdatalist
	 */
	private void setTimeList(List<Timestamp> timelist, List<TelemeterData> telemeterdatalist, Date[] timeParam) {
		int timelistNum = 24+1;
		if (telemeterdatalist != null && 0<telemeterdatalist.size()) {
			Timestamp t1 = telemeterdatalist.get(0).observtime;
			for (int i = 0; i < timelistNum; i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(t1);
				c.add(Calendar.MINUTE, -10 * i);
				Timestamp t2 = new Timestamp(c.getTimeInMillis());
				timelist.add(t2);
			}
		}
		// 時刻リストが生成できなかった場合は、基点時刻から生成する
		if(timelist.size()==0) {
			// 基点時刻の決定
			Date base = new Date();
			if(timeParam!=null && timeParam.length==1) base = timeParam[0];
			// 時刻リストを動的生成
			{
				Calendar c = Calendar.getInstance();
				c.setTime(base);
				StringBuffer minutesStr = new StringBuffer(String.valueOf(c.get(Calendar.MINUTE)));
				int firstDigitsIdx = minutesStr.length()-1;
				// 一の位が０でなければ０に変える
				if('0'!=minutesStr.charAt(firstDigitsIdx)) {
					minutesStr.setCharAt(firstDigitsIdx, '0');
					c.set(Calendar.MINUTE, Integer.parseInt(minutesStr.toString()));
				}
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				base = c.getTime();
			}
			// 時間リストは 0,10,20... といった 10分単位になるように調整
			{
				Calendar c = Calendar.getInstance();
				c.setTime(base);
				for(int i=0; i<timelistNum; i++) {
					if(i!=0) c.add(Calendar.MINUTE, -10);
					timelist.add(new Timestamp(c.getTimeInMillis()));
				}
			}
		}
	}

	/**
	 * テレメータデータの取得結果から時刻リストを作る（毎正時）
	 * @param timelist
	 * @param telemeterdatalist
	 */
	private void setTimeList60(List<Timestamp> timelist, List<TelemeterData> telemeterdatalist, Date[] timeParam) {
		int timelistNum = 24+1;
		if (telemeterdatalist != null) {
			for (int i = 0; i < telemeterdatalist.size(); i++) {
				Timestamp t1 = telemeterdatalist.get(i).observtime;
				Calendar c = Calendar.getInstance();
				c.setTime(t1);
				if (c.get(Calendar.MINUTE) == 0) {
					for (int j = 0; j < timelistNum; j++) {
						Calendar c2 = Calendar.getInstance();
						c2.setTime(t1);
						c2.add(Calendar.MINUTE, -60 * j);
						Timestamp t2 = new Timestamp(c2.getTimeInMillis());
						timelist.add(t2);
					}
					break;
				}
			}
		}
		// 時刻リストが生成できなかった場合は、基点時刻から生成する
		if(timelist.size()==0) {
			// 基点時刻の決定
			Date base = new Date();
			if(timeParam!=null && timeParam.length==1) base = timeParam[0];
			// 時刻リストを動的生成
			{
				Calendar c = Calendar.getInstance();
				c.setTime(base);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				base = c.getTime();
			}
			// 時間リストは 05:00,04:00,03:00... といった 1時間単位になるように調整
			{
				Calendar c = Calendar.getInstance();
				c.setTime(base);
				for(int i=0; i<timelistNum; i++) {
					if(i!=0) c.add(Calendar.HOUR_OF_DAY, -1);
					timelist.add(new Timestamp(c.getTimeInMillis()));
				}
			}
		}
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

	/**
	 * 時刻を降順にする
	 */
	Comparator<Timestamp> compa2 = new Comparator<Timestamp>(){
		public int compare(Timestamp o1, Timestamp o2){
			if (o1.getTime() > o2.getTime())
			return 1;
			else if (o1.getTime() < o2.getTime())
				return -1;
			else
				return 0;
		}
	};

}
