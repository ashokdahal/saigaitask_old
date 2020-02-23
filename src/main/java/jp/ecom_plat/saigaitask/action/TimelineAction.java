/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TimelinetableInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.HistorytableInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.StationlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecolumnMasterService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablerowstyleInfoService;
import jp.ecom_plat.saigaitask.service.db.TimelinetableInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * タイムラインを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class TimelineAction extends AbstractAction {

	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected MenuInfoService menuInfoService;
	@Resource
	protected MenutaskInfoService menutaskInfoService;
	@Resource
	protected MenutaskmenuInfoService menutaskmenuInfoService;
	@Resource
	protected MenuprocessInfoService menuprocessInfoservice;
	@Resource
	protected MenuloginInfoService menuloginInfoService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
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
	@Resource
	protected HistorytableInfoService historytableInfoService;
	@Resource
	protected HistoryTableService historyTableService;
	@Resource
	protected TableService tableService;
	@Resource
	protected TimelinetableInfoService timelinetableInfoService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected StationlayerInfoService stationlayerInfoService;
	@Resource
	protected TrackDataService trackDataService;

	private boolean htmlMode = true;

	static class TimelineMenue {
		static final int TYPE_LIST = 0;
		static final int TYPE_STAT = 1;
		static final int TYPE_TIME = 2;
		int id;
		String name;
		int type;
		List<TimelinetableInfo> tables;//TYPE_STATおよびTYPE_TIMEのとき
		String tablename;//TYPE＿LISTのとき
		String filterColumn;
		String filterData;
		List<TimelineColumn> columns;
		String statColumn;
		/** 状態メニューの状態カラム */
		//String statTimecolumn = "logTime";
		String statTimecolumn = "timeFrom";
		String statuses;
		String columnTitle;
		boolean numericStatus = false;
		List<TimelineColumnColoring> colorings;
		int timeInterval;//ミリ秒
		Long menuinfoid;
		String filter;
		boolean hasHistoryLink = false;
	}

	static class TimelineColumn {
		int id;
		String attrid;
		String name;
	}

	static class TimelineColumnColoring {
		int id;
		String valueNow;
		String valueBefore;
		Boolean isDefault;
		String value;
		String color;
		public TimelineColumnColoring(int id, String valueNow, String valueBefore, Boolean isDefault, String value, String color) {
			this.id = id;
			this.valueNow = valueNow;
			this.valueBefore = valueBefore;
			this.isDefault = isDefault;
			this.value = value;
			this.color = color;
		}
		public TimelineColumnColoring() {}
	}

	/**
	 * タイムラインのデフォルトメニューを生成します.
	 * TYPE_LIST は、もともとシステム用のメニューだったが
	 * 体制のレイヤ化により、タイムラインテーブル情報で設定できるようにした.
	 * @return デフォルトタイムラインメニューリスト
	 */
	public List<TimelineMenue> createDefaultTimelineMenues() {
		// 各自治体ごとにタイムラインテーブル情報の設定で menues が書き換わるため、static にはしない.
		List<TimelineMenue> menues = new ArrayList<TimelineMenue>();
		TimelineMenue menue;
		TimelineColumn col;
		int menueid = 0;
		int colid = 0;
		int coloringid = 0;
		//避難勧告・避難指示
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("Evacuation advisory/order");
		menue.type = TimelineMenue.TYPE_TIME;
		menue.tables = new ArrayList<TimelinetableInfo>();
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr0";
		col.name = lang.__("District");
		menue.columns.add(col);
		menue.statColumn = "attr4";
		menue.columnTitle = lang.__("Announcement time");
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("Evacuation order"),null,true,lang.__("Instruction"),"#ff3a3a"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("Evacuation advisory"),null,true,lang.__("Recommendation"),"#fca13a"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("Evacuation preparation"),null,true,lang.__("Preparation"),"#f0e68c"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("Evacuation order"),lang.__("Evacuation order"),false,"→","#ff3a3a"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("Evacuation advisory"),lang.__("Evacuation advisory"),false,"→","#fca13a"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("Evacuation preparation"),lang.__("Evacuation preparation"),false,"→","#f0e68c"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("None"),lang.__("Evacuation advisory"),false,lang.__("Cancel<!--4-->"),"#34d8d3"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("None"),lang.__("Evacuation order"),false,lang.__("Cancel<!--4-->"),"#34d8d3"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("None"),lang.__("Evacuation preparation"),false,lang.__("Cancel<!--4-->"),"#34d8d3"));
		menue.colorings.add(new TimelineColumnColoring(coloringid++,lang.__("None"),lang.__("None"),false,"","#ffffff"));
		menue.hasHistoryLink = true;
		menues.add(menue);
		//人命救助
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("Life-saving");
		menue.type = TimelineMenue.TYPE_STAT;
		menue.tables = new ArrayList<TimelinetableInfo>();
		//menue.statuses = lang.__(":Before rescue, Nothing: Before rescue, Requested: Requested, In rescue, Rescue completed: Rescue completed, Missing: Missing");
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr0";
		col.name = lang.__("Name");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr1";
		col.name = lang.__("District");
		menue.columns.add(col);
		menue.statColumn = "attr4";
		menue.columnTitle = lang.__("Status");
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menue.hasHistoryLink = true;
		menues.add(menue);
		//体制
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++; // ID=3
		menue.name = lang.__("System");
		//menue.type = TimelineMenue.TYPE_LIST;
		menue.type = TimelineMenue.TYPE_STAT;
		menue.tables = new ArrayList<TimelinetableInfo>();
		menue.tablename = ("stationorder_data");
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		menue.statColumn = "";
		menue.columnTitle = lang.__("System");
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menues.add(menue);
		//避難所の開設
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("Open shelter");
		menue.type = TimelineMenue.TYPE_STAT;
		menue.tables = new ArrayList<TimelinetableInfo>();
		//menue.statuses = lang.__("Pre-opening: Pre-opening, Opening: Opening, Non-Opening: Non-Opening");
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr0";
		col.name = lang.__("Name");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr1";
		col.name = lang.__("District");
		menue.columns.add(col);
		menue.statColumn = "attr4";
		menue.columnTitle = lang.__("Status");
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menue.hasHistoryLink = true;
		menues.add(menue);
		//避難所の入所者数
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("Number of Residents in shelter");
		menue.type = TimelineMenue.TYPE_TIME;
		menue.tables = new ArrayList<TimelinetableInfo>();
		menue.statuses = "";
		menue.numericStatus = true;
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr0";
		col.name = lang.__("Name");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr1";
		col.name = lang.__("Capacity (people)");
		menue.columns.add(col);
		menue.statColumn = "attr4";
		menue.columnTitle = lang.__("Status");
		menue.timeInterval = 60*60*1000;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menues.add(menue);
		//道路規制・啓開
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("Road restrictions/clearing");
		menue.type = TimelineMenue.TYPE_STAT;
		menue.tables = new ArrayList<TimelinetableInfo>();
		//menue.statuses = lang.__("Traffic restrictions (Permission for emergency vehicle) : Under restrictions, Blocked for passing through: Blocked for passing through,  Blocked for passing through (Clearing): Under clearing, Re-opened: Clearing completed");
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr0";
		col.name = lang.__("Route name");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr1";
		col.name = lang.__("Starting point");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "attr2";
		col.name = lang.__("End point");
		menue.columns.add(col);
		menue.statColumn = "attr4";
		menue.columnTitle = lang.__("Status");
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menue.hasHistoryLink = true;
		menues.add(menue);
		//公共情報コモンズ発信(メディア)
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("L-Alert notice (media)");
		menue.type = TimelineMenue.TYPE_LIST;
		menue.tables = new ArrayList<TimelinetableInfo>();
		menue.tablename = ("noticemail_data");
		menue.filterColumn = "noticetypeid";
		menue.filterData = "5";
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "title";
		col.name = lang.__("Info type");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "sendtime";
		col.name = lang.__("Date and time");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "content";
		col.name = lang.__("Content");
		menue.columns.add(col);
		menue.statColumn = "";
		menue.columnTitle = "";
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menues.add(menue);
		//公共情報コモンズ発信(緊急速報メール)
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("L-Alert send (emergency e-mail)");
		menue.type = TimelineMenue.TYPE_LIST;
		menue.tables = new ArrayList<TimelinetableInfo>();
		menue.tablename = ("noticemail_data");
		menue.filterColumn = "noticetypeid";
		menue.filterData = "4";
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "title";
		col.name = lang.__("Info type");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "sendtime";
		col.name = lang.__("Date and time");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "content";
		col.name = lang.__("Content");
		menue.columns.add(col);
		menue.statColumn = "";
		menue.columnTitle = "";
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menues.add(menue);
		//SNS発信
		colid = 0;
		coloringid = 0;
		menue = new TimelineMenue();
		menue.id = menueid++;
		menue.name = lang.__("Send SNS");
		menue.type = TimelineMenue.TYPE_LIST;
		menue.tables = new ArrayList<TimelinetableInfo>();
		menue.tablename = ("noticemail_data");
		menue.filterColumn = "noticetypeid";
		menue.filterData = "6,7";
		menue.statuses = "";
		menue.columns = new ArrayList<TimelineColumn>();
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "title";
		col.name = lang.__("Info type");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "sendtime";
		col.name = lang.__("Date and time");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "noticetypeid";
		col.name = lang.__("SNS type");
		menue.columns.add(col);
		col = new TimelineColumn();
		col.id = colid++;
		col.attrid = "content";
		col.name = lang.__("Content");
		menue.columns.add(col);
		menue.statColumn = "";
		menue.columnTitle = "";
		menue.timeInterval = 0;
		menue.colorings = new ArrayList<TimelineColumnColoring>();
		menues.add(menue);

		return menues;
	}

	/** 設定が必要なメニュー */
	private static final int rewriteMenues[] = {0,1,2,3,4,5};

	/** タイムラインメニューリスト */
	public List<HashMap<String,String>> menuelist = new ArrayList<HashMap<String,String>>();
	/** テーブルヘッダー */
	public String[][] ths;
	/** テーブルデータ */
	public String[][][] datas;
	/** セルの色 */
	public String[][][] colors;
	/** 対応履歴部分のテーブルヘッダー */
	public String[][] subths;
	/** 対応する対応履歴ページのタスクの名称 */
	//public String pagetasknames[][];
	/** 対応する対応履歴ページの名称 */
	//public String pagenames[][];
	/** 対応する対応履歴ページへのリンク */
	//public Long pageids[][];
	/** エラーメッセージ */
	public String[] errorMessages;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("menuelist", menuelist);
		model.put("ths", ths);
		model.put("datas", datas);
		model.put("colors", colors);
		model.put("subths", subths);
		model.put("errorMessages", errorMessages);
	}

	void setDatas() throws ParseException {
		// タイムラインテーブル情報からメニューを作成
		List<TimelineMenue> menues = createDefaultTimelineMenues();
		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		for(int menueId: rewriteMenues){
			List<TimelinetableInfo> tables = menues.get(menueId).tables;
			tables.clear();
			List<TimelinetableInfo> tltis = timelinetableInfoService.findByTimelinemenueMasterId(Long.valueOf(menueId+1));
			for(TimelinetableInfo tlti: tltis){
				TablemasterInfo tmi = tablemasterInfoService.findByNotDeletedId(tlti.tablemasterinfoid);
				if(tmi!=null && tmi.mapmasterinfoid.equals(mapmasterInfo.id))tables.add(tlti);
			}
		}

		//メニュー一覧をmenulistに格納
		String[] types = {"list","stat","time"};
		for(TimelineMenue menue: menues){
			HashMap<String, String> h = new HashMap<String, String>();
			h.put("name", menue.name);
			h.put("id", String.valueOf(menue.id));
			h.put("type", types[menue.type]);
			menuelist.add(h);
		}

		ths = new String[menues.size()][];
		datas = new String[menues.size()][][];
		colors = new String[menues.size()][][];
		subths = new String[menues.size()][];
		//pagetasknames = new String[menues.size()][];
		//pagenames = new String[menues.size()][];
		//pageids = new Long[menues.size()][];
		errorMessages = new String[menues.size()];

		for(int menuindex=0; menuindex<menues.size(); menuindex++) {
			TimelineMenue menue = menues.get(menuindex);
			createMenu(menue, menuindex);
		}
	}

	void createMenu(TimelineMenue menue, int menuindex) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat mdf = new SimpleDateFormat("MM/dd");
		SimpleDateFormat hmf = new SimpleDateFormat("HH:mm");

		if(menue!=null) {
			//エラーメッセージ用
			errorMessages[menuindex] = "";
			//テーブル取得
			List<TablemasterInfo> masterlist = new ArrayList<TablemasterInfo>();
			List<TracktableInfo> ttbllist = new ArrayList<TracktableInfo>();
			//List<HistorytableInfo> htbllist = new ArrayList<HistorytableInfo>();
			//List<String> pageTaskNames = new ArrayList<String>();
			//List<String> pageNames = new ArrayList<String>();
			//List<Long> pageIds = new ArrayList<Long>();
			for (TimelinetableInfo table: menue.tables) {
				Long tablemasterinfoid = table.tablemasterinfoid;
				//対応するテーブルを参照するメニューを取得
				/*if(menue.hasHistoryLink){
					BeanMap conditions = new BeanMap();
					conditions.put(Names.menutableInfo().tablemasterinfoid().toString(), tablemasterinfoid);
					List<MenutableInfo> mtis = menutableInfoService.findByCondition(conditions);
					for(MenutableInfo mti: mtis){
						// 削除済の場合は処理しない
						if(mti.deleted) continue;
						MenuInfo mi = menuInfoService.findByNotDeletedId(mti.menuinfoid);
						if(mi != null){
							if (mi.menutypeid == null) continue;
							if(mi.menutypeid.equals(Menutype.HISTORY)){//履歴メニュー
								List<MenutaskmenuInfo> tmenuList = menutaskmenuInfoService.findByMenuInfoId(mi.id);
								for (MenutaskmenuInfo tmenuInfo : tmenuList) {
									MenutaskInfo task = menutaskInfoService.findByNotDeletedId(tmenuInfo.menutaskinfoid);
									if(task!=null){
										MenuprocessInfo process = menuprocessInfoservice.findByNotDeletedId(task.menuprocessinfoid);
										if(process!=null){
											MenuloginInfo menulogin = menuloginInfoService.findByNotDeletedId(process.menulogininfoid);
											//if(menulogin!=null && menulogin.groupid == loginDataDto.getGroupid() && menulogin.disasterid == loginDataDto.getDisasterid()){
											if(menulogin!=null && menulogin.groupid == loginDataDto.getGroupid()){
												pageTaskNames.add(task.name);
												pageNames.add(mi.name);
												pageIds.add(mi.id);
											}
										}
									}
								}
							}
						}
					}
				}*/
				//記録テーブルを取得
				TablemasterInfo master = tablemasterInfoService.findById(tablemasterinfoid);
				masterlist.add(master);
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterinfoid, loginDataDto.getTrackdataid());
				ttbllist.add(ttbl);
				/*if(ttbl==null) {
					errorMessages[menuindex] += lang.__("Recording table is not found.")+(htmlMode?"<br>":"")+"\n";
					htbllist.add(null);
				}
				else {
					HistorytableInfo htbl = historytableInfoService.findByTracktableInfo(ttbl);
					htbllist.add(htbl);
				}*/

				// eコミのレイヤかどうか
				boolean isLayerTable = master!=null && StringUtil.isNotEmpty(master.layerid);

				// eコミのレイヤの場合は、列名は属性名で再設定する
				if(isLayerTable) {
					// レイヤ情報を取得
					String layerId = master.layerid;
					if(ttbl!=null && StringUtil.isNotEmpty(ttbl.layerid)) {
						layerId = ttbl.layerid;
					}
					LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);

					// 表示する列は設定で決める
					menue.columns.clear();
					List<String> attrIds = new ArrayList<String>();
					attrIds.add(table.column1);
					attrIds.add(table.column2);
					attrIds.add(table.column3);
					for(int attrIdsIdx=0; attrIdsIdx<attrIds.size(); attrIdsIdx++) {
						String attrId = attrIds.get(attrIdsIdx);
						if(StringUtil.isNotEmpty(attrId)) {
							TimelineColumn timelineColumn = new TimelineColumn();
							menue.columns.add(timelineColumn);
							timelineColumn.id = attrIdsIdx;
							timelineColumn.attrid = attrId;
							timelineColumn.name = attrId;

							// 列名を属性名から取得
							AttrInfo attrInfo = layerInfo.getAttrInfo(attrId);
							if(attrInfo!=null && StringUtil.isNotEmpty(attrInfo.name)) {
								timelineColumn.name = attrInfo.name;
							}
						}
					}

					// 体制のタイムラインメニューであれば、stationTimecolumn を更新
					final int ID_STATION_MENU=2;
					if(menue.id==ID_STATION_MENU) {
						StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
						if(stationlayerInfo!=null && StringUtil.isNotEmpty(stationlayerInfo.stationclassattrid)) {
							AttrInfo attrInfo = layerInfo.getAttrInfo(stationlayerInfo.shifttimeattrid);
							if(attrInfo==null) {
								logger.debug(lang.__("Log time is shown in system timeline, not transition time due to shifttimeattrid of system layer info unconfigured."));
							}
							else {
								// 体制レイヤ情報に移行時間の属性IDが指定されていれば、移行時間でタイムラインを表示する
								menue.statTimecolumn = attrInfo.attrId;
							}
						}
					}
				}
			}

			//対応履歴へのリンク
			/*pagetasknames[menuindex] = new String[pageTaskNames.size()];
			pagenames[menuindex] = new String[pageNames.size()];
			pageids[menuindex] = new Long[pageNames.size()];
			for(int i=0; i<pageNames.size(); i++){
				pagetasknames[menuindex][i] = pageTaskNames.get(i);
				pagenames[menuindex][i] = pageNames.get(i);
				pageids[menuindex][i] = pageIds.get(i);
			}*/

			//テーブルデータ取得
			List<List<BeanMap>> hdatas = new ArrayList<List<BeanMap>>();
			List<BeanMap> hldatas = new ArrayList<BeanMap>();
			if(menue.type == TimelineMenue.TYPE_LIST){
				List<BeanMap> datas = null;
				for(int index=0; index<masterlist.size(); index++) {
					TablemasterInfo tablemasterInfo = masterlist.get(index);
					boolean isLayerTable = tablemasterInfo!=null && StringUtil.isNotEmpty(tablemasterInfo.layerid);
					// eコミマップレイヤテーブル
					if(isLayerTable) {
						// データ取得
						String layerId = tablemasterInfo.layerid;
						TracktableInfo tracktableInfo = ttbllist.get(index);
						if(tracktableInfo!=null && StringUtil.isNotEmpty(tracktableInfo.layerid)) {
							layerId = tracktableInfo.layerid;
						}
						datas = tableService.selectAll(layerId);
					}
				}

				// レイヤからデータがとれなかった場合は、システムテーブル
				if(datas==null) {
					datas = tableService.selectByTrackdataid(menue.tablename, loginDataDto.getTrackdataid());
				}
				// フィルタリング
				if(menue.filterColumn != null && menue.filterColumn.length()>0){
					String[] filterDatas = menue.filterData.split(",");
					for (BeanMap beanMap : datas) {
						Object val = beanMap.get(menue.filterColumn);
						if(val != null){
							for(String f: filterDatas){
								if(val.toString().equals(f)){
									hldatas.add(beanMap);
									break;
								}
							}
						}
					}
				}else{
					hldatas = datas;
				}
			}else{
				if(masterlist.size() == 0){
					errorMessages[menuindex] += lang.__("This table is no setting.")+(htmlMode?"<br>":"")+"\n";
				}
				for(int i=0; i<masterlist.size(); i++){
					boolean badTableInfo = false;
					TracktableInfo tracktableInfo = ttbllist.get(i);
					// 平常時モードだとタイムラインが開けないので、
					// とりあえず処理をとばす
					if(tracktableInfo==null) continue;
					//HistorytableInfo htbl = htbllist.get(i);
					TimelinetableInfo tltbl = menue.tables.get(i);
					List<List<BeanMap>> adatas = new ArrayList<List<BeanMap>>();
					/*if(htbl==null) {
						errorMessages[menuindex] = lang.__("History table is not found.");
						continue;
					}*/
					String message = lang.__("Settings of the timeline table is invalid. %s of tablemasterinfoid:%d has not been set.")+(htmlMode?"<br>":"")+"%n";
					if(tltbl.column1 == null || tltbl.column1.isEmpty()){
						//TODO:errorMessages[menuindex] += String.format(message,htbl.tablemasterinfoid,"column1");
						badTableInfo = true;
					}
					if(menue.columns.size()>=2){
						if(tltbl.column2 == null || tltbl.column2.isEmpty()){
							//TODO:errorMessages[menuindex] += String.format(message,htbl.tablemasterinfoid,"column2");
							badTableInfo = true;
						}
					}
					if(menue.columns.size()>=3){
						if(tltbl.column3 == null || tltbl.column3.isEmpty()){
							//TODO:errorMessages[menuindex] += String.format(message,htbl.tablemasterinfoid,"column3");
							badTableInfo = true;
						}
					}
					if(tltbl.statecolumn == null || tltbl.statecolumn.isEmpty()){
						//TODO:errorMessages[menuindex] += String.format(message,htbl.tablemasterinfoid,"statecolumn");
						badTableInfo = true;
					}

					if(!badTableInfo){
						//adatas = historyTableService.getLogGroupById(htbl);
						TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
						java.util.Date[] dateparam = new java.util.Date[2];
						dateparam[0] = new java.util.Date(trackData.starttime.getTime());
						if (trackData.endtime != null)
							dateparam[1] = new java.util.Date(trackData.endtime.getTime());
						else dateparam[1] = new java.util.Date();
						List<BeanMap> datalist = tableService.selectAll(tracktableInfo.layerid, "", "_orgid,-gid", dateparam);
						adatas = groupById(datalist);
						List<List<BeanMap>> bdatas = new ArrayList<List<BeanMap>>();
						for(List<BeanMap> datas: adatas){
							List<BeanMap> tdatas = new ArrayList<BeanMap>();
							for(BeanMap m: datas){
								BeanMap tm = new BeanMap();
								//tm.put("logId", m.get("logId"));/**/
								tm.put("logId", m.get("gid"));
								tm.put(menue.statTimecolumn, m.get(menue.statTimecolumn));
								if(menue.columns.size()>=1) {
									if(tltbl.column1 != null && !tltbl.column1.isEmpty())
										tm.put(menue.columns.get(0).attrid, m.get(tltbl.column1));
									else
										tm.put(menue.columns.get(0).attrid, null);
								}
								if(menue.columns.size()>=2)
									if(tltbl.column2 != null && !tltbl.column2.isEmpty())
										tm.put(menue.columns.get(1).attrid, m.get(tltbl.column2));
									else
										tm.put(menue.columns.get(1).attrid, null);
								if(menue.columns.size()>=3)
									if(tltbl.column3 != null && !tltbl.column3.isEmpty())
										tm.put(menue.columns.get(2).attrid, m.get(tltbl.column3));
									else
										tm.put(menue.columns.get(2).attrid, null);
								if(tltbl.statecolumn != null && !tltbl.statecolumn.isEmpty())
									tm.put(menue.statColumn, m.get(tltbl.statecolumn));
								tdatas.add(tm);
							}
							bdatas.add(tdatas);
						}
						hdatas.addAll(bdatas);
					}
				}
			}

			//テーブルヘッダー
			if(menue.type == TimelineMenue.TYPE_LIST)
				ths[menuindex] = new String[menue.columns.size()];
			else
				ths[menuindex] = new String[menue.columns.size()+1];
			int coli = 0;
			for(TimelineColumn col: menue.columns){
				ths[menuindex][coli] = col.name;
				coli++;
			}
			if(menue.type != TimelineMenue.TYPE_LIST)
				ths[menuindex][coli] = menue.columnTitle;

			//テーブル作成の準備

			//状態リスト
			HashMap<String,String> showStats = new HashMap<String,String>();
			HashMap<String,Integer> stats = new HashMap<String,Integer>();
			if(menue.type == TimelineMenue.TYPE_STAT) {
				int statidx=0;
				HashMap<String,String> tmpShowStats = new HashMap<String,String>();
				String[] tmpStats = menue.statuses.split(",");
				for(String tmpStat: tmpStats) {
					String[] tmpStatKeyVal = tmpStat.split(":");
					if(tmpStatKeyVal.length != 2)continue;
					showStats.put(tmpStatKeyVal[0], tmpStatKeyVal[1]);
					if(tmpShowStats.get(tmpStatKeyVal[1])==null){
						tmpShowStats.put(tmpStatKeyVal[1],"");
						stats.put(tmpStatKeyVal[1],statidx++);
					}
				}
				for(List<BeanMap> data: hdatas){
					for(int i=data.size()-1; i>=0; i--){
						BeanMap mm = data.get(i);
						String stat = (String)mm.get(menue.statColumn);
						if(stat != null && !stat.isEmpty() && !stat.equals(" ") && !stat.equals(lang.__("None")) && stats.get(stat)==null){
							stats.put(stat,statidx++);
							showStats.put(stat,stat);
						}
					}
				}
			}

			//時間チェック
			List<Long> times = new ArrayList<Long>();
			if(menue.type == TimelineMenue.TYPE_TIME){
				if(menue.timeInterval == 0){//不等間隔
					for(int idindex=0; idindex<hdatas.size(); idindex++){//地物
						List<BeanMap> data = hdatas.get(idindex);
						String lastState = null;
						for(int d = data.size() -1 ; d >= 0; d--){
							BeanMap m = data.get(d);
							String state = (String)m.get(menue.statColumn);
							if(lastState == null || !lastState.equals(state)){
								Timestamp ts = (Timestamp)m.get(menue.statTimecolumn);
								long time = ts.getTime();
								time = time/(1000*60)*1000*60;//分毎に丸める
								m.put(menue.statTimecolumn,new Timestamp(time));
								int i;
								for(i=0; i<times.size(); i++){
									if(times.get(i)==time){
										break;
									}else if(times.get(i)>time){
										times.add(i,time);
										break;
									}
								}
								if(i==times.size()) times.add(time);
								lastState = state;
							}
						}
					}
				}else{//一定間隔
					long minTime=Long.MAX_VALUE,maxTime=Long.MIN_VALUE;
					for(List<BeanMap> data: hdatas){//地物
						String lastState = null;
						for(int i=data.size()-1; i>=0; i--){
							BeanMap m = data.get(i);
							String state = (String)m.get(menue.statColumn);
							Timestamp ts = (Timestamp)m.get(menue.statTimecolumn);
							long time = ts.getTime();
							if(lastState!=null && !lastState.equals(state) || lastState==null&&state!=null&&!state.isEmpty()&&!state.equals(" ")&&!state.equals(lang.__("None"))){
								if(minTime>time)minTime=time;
								if(maxTime<time)maxTime=time;
							}
							lastState = state;
						}
					}
					minTime = minTime/menue.timeInterval*menue.timeInterval;
					if(maxTime%menue.timeInterval != 0)
						maxTime = maxTime/menue.timeInterval*menue.timeInterval;
					else
						maxTime = (maxTime/menue.timeInterval)*menue.timeInterval;
					for(long l=minTime; l<=maxTime; l+=menue.timeInterval)
						times.add(l);
				}

				subths[menuindex] = new String[times.size()];
				for(int i=0; i<times.size(); i++){
					subths[menuindex][i] = mdf.format(new Date(times.get(i)))+(htmlMode?"<br>":" ")
											+ hmf.format(new Date(times.get(i)));
				}
			}
			//状態
			else if(menue.type == TimelineMenue.TYPE_STAT){
				subths[menuindex] = new String[stats.size()];
				for(Map.Entry<String, Integer> entry : stats.entrySet()) {
					subths[menuindex][entry.getValue()] = entry.getKey();
				}
			}
			//リスト
			else{
				subths[menuindex] = null;
			}

			//テーブルデータ用構造作成
			if(menue.type == TimelineMenue.TYPE_LIST){
				datas[menuindex] = new String[hldatas.size()][menue.columns.size()];
				colors[menuindex] = new String[hldatas.size()][menue.columns.size()];
			}else{
				datas[menuindex] = new String[hdatas.size()+(menue.numericStatus?1:0)][menue.columns.size()+subths[menuindex].length];
				colors[menuindex] = new String[hdatas.size()+(menue.numericStatus?1:0)][menue.columns.size()+subths[menuindex].length];
				for(int i=0; i<datas[menuindex].length; i++){
					for(int j=0; j<subths[menuindex].length; j++){
						datas[menuindex][i][j] = null;
						colors[menuindex][i][j] = null;
					}
				}
			}

			//テーブルデータ格納
			if(menue.type==TimelineMenue.TYPE_TIME){
				//セルの色付けをHashMapに格納
				HashMap<String,TimelineColumnColoring> color1 = new HashMap<String,TimelineColumnColoring>();
				HashMap<String,HashMap<String,TimelineColumnColoring>> color2 = new HashMap<String,HashMap<String,TimelineColumnColoring>>();
				for(TimelineColumnColoring cc: menue.colorings){
					if(cc.isDefault){
						color1.put(cc.valueNow,cc);
					}else{
						if(!color2.containsKey(cc.valueNow))
							color2.put(cc.valueNow, new HashMap<String,TimelineColumnColoring>());
						color2.get(cc.valueNow).put(cc.valueBefore, cc);
					}
				}
				//時間処理の準備

				//データ処理
				for(int idindex=0; idindex < hdatas.size(); idindex++){
					List<BeanMap> data = hdatas.get(idindex);
					long minTime = 0;
					HashMap<Long,Integer> ts = new HashMap<Long,Integer>();
					if(menue.timeInterval == 0){//不等間隔
						for(int i=0;i<times.size();i++)
							ts.put(times.get(i), i);
					}else{//一定間隔
						if(times.size() != 0)
							minTime = times.get(0);
					}
					String lastStat = null;
					//for(BeanMap m:data){
					for(int t=data.size()-1; t>=0; t--){
						BeanMap m = data.get(t);
						String stat = (String)m.get(menue.statColumn);

						//状態以外のカラム
						for(int colindex=0; colindex < menue.columns.size(); colindex++ ){
							TimelineColumn col = menue.columns.get(colindex);
							datas[menuindex][idindex][colindex] = (String)m.get(col.attrid);
						}

						if(lastStat == null || !lastStat.equals(stat)){
							//時間-状態
							int idx;
							if(menue.timeInterval == 0){//不等間隔
								long tt = ((Timestamp)m.get(menue.statTimecolumn)).getTime();

								Integer timeidx = ts.get(tt);
								if(timeidx != null)
									idx = menue.columns.size()+timeidx;
								else
									idx = menue.columns.size();
							}else{//一定間隔
								long tt = ((Timestamp)m.get(menue.statTimecolumn)).getTime();
								idx = (int) (menue.columns.size()+(tt-minTime)/menue.timeInterval);
							}
							if(idx<menue.columns.size() || idx>=menue.columns.size()+times.size())continue;
							//色付け
							TimelineColumnColoring cc = null;
							if(color2.containsKey(stat) && lastStat!=null && color2.get(stat).containsKey(lastStat))
								cc = color2.get(stat).get(lastStat);
							else if(color1.containsKey(stat))
								cc = color1.get(stat);
							if(cc != null){
								datas[menuindex][idindex][idx] = cc.value;
								colors[menuindex][idindex][idx] = cc.color;
							}else{
								datas[menuindex][idindex][idx] = stat;
								colors[menuindex][idindex][idx] = null;
							}
							if(color2.containsKey(stat) && color2.get(stat).containsKey(stat))
								cc = color2.get(stat).get(stat);
							else if(color1.containsKey(stat))
								cc = color1.get(stat);
							for(int i=idx+1; i<datas[menuindex][idindex].length; i++){
								if(cc != null){
									datas[menuindex][idindex][i] = cc.value;
									colors[menuindex][idindex][i] = cc.color;
								}else{
									datas[menuindex][idindex][i] = stat;
									colors[menuindex][idindex][i] = null;
								}
							}
							lastStat = stat;
						}
					}
					//合計値
					if(menue.numericStatus){
						datas[menuindex][hdatas.size()][0] = lang.__("Total<!--2-->");
						for(int i=menue.columns.size(); i<menue.columns.size()+times.size(); i++){
							int sum = 0;
							for(int j=0; j<hdatas.size(); j++){
								if(datas[menuindex][j][i] != null && !"".equals(datas[menuindex][j][i]))
									sum += Integer.parseInt(datas[menuindex][j][i]);
							}
							datas[menuindex][hdatas.size()][i] = String.valueOf(sum);
						}
					}
				}
			}else{
				if(menue.type==TimelineMenue.TYPE_STAT){
					for(int idindex=0; idindex < hdatas.size(); idindex++){
						List<BeanMap> data = hdatas.get(idindex);
						//状態カラム以外
						BeanMap m = data.get(0);
						for(int colindex=0; colindex < menue.columns.size(); colindex++ ){
							TimelineColumn col = menue.columns.get(colindex);
							datas[menuindex][idindex][colindex] = (String)m.get(col.attrid);
						}
						//状態カラム
						//String lastStat = null;
						Integer lastStateIndex = null;
						// 古い履歴から走査する
						for(int i=data.size()-1; i>=0; i--){
							BeanMap mm = data.get(i);
							String stat = (String)mm.get(menue.statColumn);
							//if(lastStat==null || !lastStat.equals(stat)){
								if(showStats.get(stat)!=null){
									String displayData = null;
									try {
										Object val = mm.get(menue.statTimecolumn);
										if(val instanceof Timestamp) {
											Timestamp ts = (Timestamp) val;
											displayData = mdf.format(ts)+" "+hmf.format(ts);
										}
										else {
											// 日付文字列なら書式をフォーマットする
											String str = (String) val;
											try {
												Timestamp ts = Timestamp.valueOf(str.replaceAll("/", "-"));
												displayData = mdf.format(ts)+" "+hmf.format(ts);
											} catch(IllegalArgumentException e) {
												logger.warn(e);
											}
											// そのまま表示
											if(displayData==null) {
												displayData = str;
											}
										}
									} catch(Exception e) {
										logger.error(lang.__("Failed to get the value of column status."), e);
									}
									lastStateIndex = stats.get(showStats.get(stat));
									if(displayData!=null) {
										datas[menuindex][idindex][lastStateIndex+menue.columns.size()] = displayData;
									}
								}
							//}
							//lastStat = stat;
						}
						if(lastStateIndex!=null)colors[menuindex][idindex][lastStateIndex+menue.columns.size()] = "#fca13a";
					}
				}else if(menue.type==TimelineMenue.TYPE_LIST){

					//idで入っているカラムの値集合を取得
					HashMap<String,HashMap<Long,String>> attrvals = new HashMap<String,HashMap<Long,String>>();
					for(TimelineColumn col: menue.columns){
						String tbn = null;
						if(col.attrid.endsWith("infoid")){
							tbn = col.attrid.replaceFirst("infoid", "_info");
						}else if(col.attrid.endsWith("id")){
							tbn = col.attrid.replaceFirst("masterid$", "_master");
							tbn = col.attrid.replaceFirst("id$", "_master");
						}
						if(tbn != null){
							List<BeanMap> valbs = tableService.selectAll(tbn);
							HashMap<Long,String> vals = new HashMap<Long,String>();
							for(BeanMap valb: valbs){
								Long id;
								Object o = valb.get("id");
								if(o.getClass() == Integer.class) id = Long.valueOf(String.valueOf((Integer)o));
								else id = (Long)o;
								vals.put(id,(String)valb.get("name"));
							}
							attrvals.put(col.attrid, vals);
						}
					}
					//テーブルに値を格納
					for(int idindex=0; idindex < hldatas.size(); idindex++){
						BeanMap m = hldatas.get(idindex);
							for(int colindex=0; colindex < menue.columns.size(); colindex++ ){
								TimelineColumn col = menue.columns.get(colindex);
								Object o = m.get(col.attrid);
								String str = null;
								if(o != null){
									if(attrvals.containsKey(col.attrid)){
										Long id;
										if(o.getClass() == Integer.class) id = Long.valueOf(String.valueOf((Integer)o));
										else id = (Long)o;
										str = attrvals.get(col.attrid).get(id);
									}else if(o.getClass() == String.class)
										str = (String)m.get(col.attrid);
									else if(o.getClass() == Timestamp.class)
										str = sdf.format(new Date(((Timestamp)o).getTime()));
									else if(o.getClass() == Long.class || o.getClass() == Integer.class){
										str = String.valueOf(o);
									}
								}
								str = StringUtils.abbreviate(str, 80);
								datas[menuindex][idindex][colindex] = str;
							}

					}
				}
			}
		}
	}

	/**
	 * タイムライン画面を表示する.
	 * @return フォワード先
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/timeline", "/timeline/index"})
	public String index(Map<String,Object>model) throws ParseException {
		menuelist = new ArrayList<HashMap<String,String>>();
		setDatas();
		setupModel(model);
		return "/timeline/index";
	}

	/**
	 * pdfを出力する.
	 * @return null(フォワードしない)
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/timeline/outputpdf"})
	public String outputpdf(Map<String,Object>model, HttpServletResponse res) throws ParseException {
		menuelist = new ArrayList<HashMap<String,String>>();
    	// CSRF対策
    	/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

		htmlMode = false;
		setDatas();

		//出力用一時ファイル名を時間で作成
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(time);

		//出力用のストリームをインスタンス化
		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();

		//step 1:新規ドキュメントを作成
		Document document = new Document();

		try {
			//日本語フォント設定
			BaseFont bfont = BaseFont.createFont(Config.getFontFilePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			//BaseFont bfont_heiseiKakuGo = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
			//BaseFont bfont_heiseiMin    = BaseFont.createFont("HeiseiMin-W3", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
			//BaseFont bfont_ipaMincho   = BaseFont.createFont("C:/IDE/eclipse-4.3-64bit/workspace/JavaStudy/fonts/ipam00303/ipam.ttf", BaseFont.IDENTITY_H, true);

			Font font10 = new Font(bfont, 10);
			Font font10b_white = new Font(bfont, 10, Font.BOLD);
			font10b_white.setColor(WebColors.getRGBColor("#FFFFFF"));


			//ドキュメントの設定
			// Pager Size    : A4
			// Document Body :
			//
			// cf. 1pt = 1/72.27 inch = 0.3514 mm
			//      A4 = (210mm x 297mm) = (595.0pt x842.0pt)
			//
			float margin = 36f;
			//document.setPageSize(PageSize.A4);	//用紙サイズ A4縦
			document.setPageSize(PageSize.A4.rotate());	//用紙サイズ A4横
			document.setMargins(margin, margin, margin, margin);

			//step 2:PdfWriterインスタンスの取得
			PdfWriter.getInstance(document, bos1);
			//PdfWriter writer = PdfWriter.getInstance(document, bos1);


			/**
			//ヘッダーフッター
			HeaderFooter event = new HeaderFooter();
			Rectangle pageSize = document.getPageSize();	//ページサイズ
			Rectangle rectHF = new Rectangle(margin, margin, (pageSize.getWidth()-margin), (pageSize.getHeight()-margin));
			writer.setBoxSize("art", rectHF);
			writer.setPageEvent(event);
			**/

			//step 3:ドキュメントをオープン
			document.open();

			//メタ情報を追加
			//document.addAuthor("");
			document.addTitle(lang.__("Timeline"));
			//document.addSubject("sub title");
			document.addCreationDate();
			document.addCreator(loginDataDto.getLocalgovInfo().systemname);

			for(int menueindex = 0; menueindex < menuelist.size(); menueindex++){
				HashMap<String,String> menue = menuelist.get(menueindex);
				//step 4:ドキュメントにコンテンツを追加する
				Paragraph p3 = new Paragraph(menue.get("name"), font10);

				p3.setLeading(0, 2f);

				document.add(p3);

				document.add(Chunk.NEWLINE);	//改行

				// データの件数を取得
				int fields = ths[menueindex].length;
				if(subths[menueindex] != null && subths[menueindex].length>0)fields += subths[menueindex].length-1;

				//テーブルの作成と設定
				PdfPTable table = new PdfPTable(fields);
				table.getDefaultCell().setBorder(1);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);

				//セルのカラーを定義
				Color color_header = WebColors.getRGBColor("#4F81BD");
				Color color_odd = WebColors.getRGBColor("#F0F0F6");
				Color color_border = WebColors.getRGBColor("#CDCDCD");

				//セルの幅
				float[] widths = new float[fields];

				PdfPCell cell = null;

				//テーブルヘッダ
				for (int j = 0; j< ths[menueindex].length; j++){
					//文字列のサイズを取得
					float textWidth = bfont.getWidthPoint(ths[menueindex][j], 10);
					if (widths[j] < textWidth)
						widths[j] = textWidth;

					//セルにコンテンツを追加
					cell = new PdfPCell(new Phrase(ths[menueindex][j], font10b_white));
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(color_header);
					cell.setBorderColor(color_border);
					cell.setPadding(4f);
					if(subths[menueindex] != null && subths[menueindex].length != 0){
						if(j == ths[menueindex].length-1) cell.setColspan(subths[menueindex].length);
						/*
						else cell.setRowspan(2);
						*/
						else cell.setColspan(2);
					}

					//テーブルにセルを追加
					table.addCell(cell);
				}
				if(subths[menueindex] != null){
					for (int j = 0; j< subths[menueindex].length; j++){
						//文字列のサイズを取得
						float textWidth = bfont.getWidthPoint(subths[menueindex][j], 10);
						if (widths[j] < textWidth)
							widths[j] = textWidth;

						//セルにコンテンツを追加
						cell = new PdfPCell(new Phrase(subths[menueindex][j], font10b_white));
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(color_header);
						cell.setBorderColor(color_border);
						cell.setPadding(4f);

						//テーブルにセルを追加
						table.addCell(cell);
					}
				}

				//データをセルに追加
				int nr = 0;
				for (int i = 0; i < datas[menueindex].length; i++) {
					for (int j = 0; j < datas[menueindex][i].length; j++) {
						//文字列のサイズを取得
						String data = datas[menueindex][i][j];
						if(data == null) data = "";
						float textWidth = bfont.getWidthPoint(data, 10);
						if (widths[j] < textWidth)
							widths[j] = textWidth;

						//セルにコンテンツを追加
						cell = new PdfPCell(new Phrase(data, font10));
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						if (nr%2!=0)
							cell.setBackgroundColor(color_odd);
						if(colors[menueindex][i][j] != null){
							Color color_thiscell = WebColors.getRGBColor(colors[menueindex][i][j]);
							cell.setBackgroundColor(color_thiscell);
						}
						cell.setBorderColor(color_border);
						cell.setPadding(4f);

						//テーブルにセルを追加
						table.addCell(cell);
					}
					table.completeRow();
					nr++;

				}

				//table.setHorizontalAlignment(Element.ALIGN_CENTER);	//テーブルの表示位置
				table.setHorizontalAlignment(Element.ALIGN_LEFT);	//テーブルの表示位置
				table.setWidths(widths);	//セル幅のフィッティング

				//ドキュメントにテーブルを追加
				document.add(table);

			}

			//step 5:ドキュメントをクローズする
			document.close();


			//ページ番号の埋め込み
			PdfReader reader = new PdfReader(bos1.toByteArray());
			PdfStamper stamper = new PdfStamper(reader, bos2);

			int num = reader.getNumberOfPages();
			for (int i = 1; i <= num; i++) {
				String page = String.format("%d/%d",  i, num);
				float strWidth = bfont.getWidthPoint(page, 10);
				ColumnText.showTextAligned(
						stamper.getOverContent(i),
						Element.ALIGN_LEFT,
						new Phrase(page),
						(document.right() / 2) + (strWidth / 2),
						document.bottom() - (document.bottomMargin() / 3 * 2),
						0
				);
			}

			stamper.close();
			reader.close();


			//ブラウザへ出力

			//HTTPレスポンスヘッダの定義
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "attachment; filename=\""+now+".pdf\"");
			//キャッシュを無効化する
			res.addHeader("Cache-Control", "no-cache");
			res.addHeader("Pragma", "no-cache");
			res.addHeader("Expires", "0");

			//出力ストリームの生成
			OutputStream out = res.getOutputStream();
			out.write(bos2.toByteArray());
			out.close();

		} catch (DocumentException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} finally {
			document.close();
		}
		return null;
	}

	long parseDate(String str) throws ParseException{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf1.parse(str).getTime();
			} catch (ParseException e) {
				return sdf2.parse(str).getTime();
			}
	}

	private List<List<BeanMap>> groupById(List<BeanMap> datas){
		Long lastId = null;
		List<List<BeanMap>> a = new ArrayList<List<BeanMap>>();
		List<BeanMap> datasForId = new ArrayList<BeanMap>();
		for(BeanMap data: datas){
			Long nowId = (Long)data.get("_orgid");
			if(lastId == null){
				lastId = (Long)data.get("_orgid");
			}else if(!lastId.equals(nowId)){
				lastId = (Long)data.get("_orgid");
				a.add(datasForId);
				datasForId = new ArrayList<BeanMap>();
			}
			datasForId.add(data);
		}
		if(lastId != null) a.add(datasForId);

		return a;
	}

}
