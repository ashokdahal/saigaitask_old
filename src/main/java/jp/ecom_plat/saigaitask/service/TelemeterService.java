/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.geoserver.util.ISO8601Formatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.telemeter.ItemData;
import jp.ecom_plat.saigaitask.dto.telemeter.PointContainer;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.entity.db.TelemeterfileData;
import jp.ecom_plat.saigaitask.entity.db.TelemeterofficeInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterpointInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterserverInfo;
import jp.ecom_plat.saigaitask.service.db.ObservatorydamInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatorydamlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryrainInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryrainlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryriverInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryriverlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TelemeterDataService;
import jp.ecom_plat.saigaitask.service.db.TelemeterfileDataService;
import jp.ecom_plat.saigaitask.service.db.TelemeterofficeInfoService;
import jp.ecom_plat.saigaitask.service.db.TelemeterpointInfoService;
import jp.ecom_plat.saigaitask.service.db.TelemeterserverInfoService;
import jp.ecom_plat.saigaitask.util.BasicAuthenticator;

/**
 * テレメータの値を取得するサービスクラス
 */
@org.springframework.stereotype.Service
public class TelemeterService {

	Logger logger = Logger.getLogger(TelemeterService.class);
	
	@Resource
	protected TelemeterserverInfoService telemeterserverInfoService;
	@Resource
	protected TelemeterofficeInfoService telemeterofficeInfoService;
	@Resource
	protected TelemeterfileDataService telemeterfileDataService;
	@Resource
	protected TelemeterDataService telemeterDataService;
	@Resource
	protected TelemeterpointInfoService telemeterpointInfoService;
	
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected ObservatoryrainInfoService observatoryrainInfoService;
	@Resource
	protected ObservatoryrainlayerInfoService observatoryrainlayerInfoService;
	@Resource
	protected ObservatoryriverInfoService observatoryriverInfoService;
	@Resource
	protected ObservatoryriverlayerInfoService observatoryriverlayerInfoService;
	@Resource
	protected ObservatorydamInfoService observatorydamInfoService;
	@Resource
	protected ObservatorydamlayerInfoService observatorydamlayerInfoService;
	
	/**
	 * データを取得するスレッド
	 */
	//protected DataThread thread = null;
	/**
	 * データを取得するタスク
	 */
	//protected DataTask task;
	/**
	 * タスクのインターバルミリ秒
	 */
	//protected int interval = 60000;
	
	private static final long MIN10 = 10*60*1000;
	private static final long DELAY = 8*60*1000;

	private static final String[] DATATYPE = new String[]{"001", "004", "007"};
	/** 雨量データタイプ */
	public static final String RAIN_DATATYPE = "001";
	/** 水位データタイプ */
	public static final String RIVER_DATATYPE = "004";
	/** ダムデータタイプ */
	public static final String DAM_DATATYPE = "007";
	
	private static final int[] rainvlevel = {-1, 0, 1, 5, 10, 20, 50, 100, 10000};

	
	/**
	 * 初期実行
	 * @param start スレッド実行フラグ
	 */
	public void init(boolean start)
	{
		logger.info("telemeter service start");
		//if (start)
		//	startThread();
	}

	/**
	 * 終了処理を行う。
	 */
	public void destroy() {
		/*if (task != null)
			task.cancel();
		if (thread != null) {
			thread.interrupt();
		}
		thread = null;*/
	}

	/**
	 * アラームを行うスレッドを開始する
	 *
	 * @throws ServletException
	 */
	/*protected void startThread() {

		try {
			task = new DataTask();
			//アップデートスレッドを作成して実行
			thread = new DataThread("DataThread", task);
			thread.start();
			logger.info("telemeter thread start");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}*/

	/**
	 * アラームを行う処理をスレッドで行う。
	 *
	 */
	//protected class DataThread extends Thread {
		/**
		 * リスク判定タスク
		 */
		//DataTask task;

		/**
		 * コンストラクタ。
		 *
		 * @param name スレッド名
		 * @param task タスク
		 */
		/*public DataThread(String name, DataTask task) {
			super(name);

			this.task = task;
		}*/

		/**
		 * スレッドの実行処理を行う。
		 */
		/*public void run() {

			Timer timer = new Timer();

			timer.schedule(task, 0, interval);
		}
	}*/
	
	/**
	 * アラームタスク
	 */
	//protected class DataTask extends TimerTask {
		/**
		 * コンストラクタ
		 */
		/*public DataTask() {
		}*/

		/**
		 * アラームチェック実行
		 */
		/*public void run()
		{
			try {
				logger.info("telemeter check start");
				checkData();
				logger.info("telemeter check end");
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}*/
	
	public void checkData() throws IOException
	{
		List<TelemeterserverInfo> serverlist = telemeterserverInfoService.findByValid();
		for (TelemeterserverInfo server : serverlist) {
			if (server.url != null && server.userid != null && server.password != null) {
				BasicAuthenticator http_authenticator = new BasicAuthenticator(server.userid, server.password);
				Authenticator.setDefault(http_authenticator);

				List<TelemeterofficeInfo> officelist = telemeterofficeInfoService.findByTelemeterserverInfoId(server.id);
				// 現在時刻から DELAY(8分前) に遡って１０分単位に変換する（切り捨て）
				// 例:   現在時刻: 201606161707
				// 例:  DELAY時刻: 201606161659 
				// 例: 切捨て時刻: 201606161650 time/MIN10*MIN10
				long time = System.currentTimeMillis()-DELAY;
				time = time/MIN10*MIN10;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				String stime = sdf.format(new Timestamp(time));
				//午前０時だけ別扱い
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(time);
				if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
					long time2 = time-60000;//前日
					sdf = new SimpleDateFormat("yyyyMMdd");
					stime = sdf.format(new Timestamp(time2))+"2400";
				}
				
				for (TelemeterofficeInfo office : officelist) {
					
					for (String type : DATATYPE) {
						String fname = "tm-"+office.officecode+"-0000-"+stime+"-"+type+"-00000000000000.xml";
						
						TelemeterfileData fdata = telemeterfileDataService.findByFilename(fname);
						if (fdata != null) continue;
						
						HttpURLConnection urlconn = (HttpURLConnection)new URL(server.url+fname).openConnection();
						urlconn.setReadTimeout(30000);
						urlconn.setRequestMethod("GET");
						urlconn.setInstanceFollowRedirects(false);
						urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
						logger.info("telemeter url="+server.url+fname);
						receiveData(server, office, type, fname, urlconn);;
					}
				}
			}
		}
	}

	protected void receiveData(TelemeterserverInfo server, TelemeterofficeInfo office, String type, String fname, URLConnection urlconn) {
		MapDB mapDB = MapDB.getMapDB();
		try {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fact.newDocumentBuilder();
			Document doc = builder.parse(urlconn.getInputStream());
			ObservPointParser parser = new ObservPointParser(doc);
			parser.parse();
			
			for (PointContainer point : parser.pointList) {
				TelemeterpointInfo tpoint = telemeterpointInfoService.findByPointcode(point.getPointFullCode());
				if (tpoint == null || StringUtil.isEmpty(tpoint.itemcode)) continue;
				String itemcodes[] = tpoint.itemcode.split(",");
				
				Vector<String> codes = new Vector<String>();
				for (String code : itemcodes)
					codes.add(code);
				for (ItemData item : point.itemList) {
					if (!codes.contains(item.itemCode)) continue;
					if (item.itemType.equals("NumData")) {
						logger.info("telemeter data insert");
						TelemeterData data = new TelemeterData();
						data.code = point.getPointFullCode();
						data.itemcode = item.getItemCode();
						data.contentscode = item.getContentsCode();
						data.observtime = parser.observTime;
						if (StringUtil.isNotEmpty(item.value) && item.itemType.equals("NumData"))
							data.val = Double.parseDouble(item.value);
						int num = telemeterDataService.insert(data);
						logger.info("telemeter insert num="+num);
					}
					else if (item.itemType.equals("TimeData")) {
						/*TelemetertimeData data = new TelemetertimeData();
						data.code = rain.getPointFullCode();
						data.itemcode = item.getItemCode();
						data.contentscode = item.getContentsCode();
						data.observtime = parser.observTime;
						if (StringUtil.isNotEmpty(item.value) && item.itemType.equals("NumData"))
							data.val = 
						telemetertimeDataService.insert(data);
						*/
					}
				}
				//eコミデータのアップデート
				updateEcomAttribute(mapDB, server.localgovinfoid, type, point);
			}
			
			//ファイルの取得記録を保存
			TelemeterfileData fileData = new TelemeterfileData();
			fileData.telemeterofficeinfoid = office.id;
			fileData.filename = fname;
			fileData.observtime = new Timestamp(System.currentTimeMillis());
			telemeterfileDataService.insert(fileData);
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	protected void updateEcomAttribute(MapDB mapDB, Long govid, String type, PointContainer point) throws Exception
	{
		String scode = Long.toString(point.getPointFullCode());
		String sadmin = scode.substring(0, 5);
		String sno = scode.substring(8);
		
		if (type.equals(RAIN_DATATYPE)) {
			ObservatoryrainInfo rain = observatoryrainInfoService.findByLocalgovInfoIdAndOfficecodeAndObsrvtncode(govid, Integer.parseInt(sadmin), Integer.parseInt(sno));
			if (rain != null) {
				ObservatoryrainlayerInfo rlayer = observatoryrainlayerInfoService.findByTablemasterInfoId(rain.tablemasterinfoid);
				if (rlayer != null)
					updateRainAttribute(mapDB, rlayer, rain, point);
			}
		}
		else if (type.equals(RIVER_DATATYPE)) {
			ObservatoryriverInfo river = observatoryriverInfoService.findByLocalgovInfoIdAndOfficecodeAndObsrvtncode(govid, Integer.parseInt(sadmin), Integer.parseInt(sno));
			if (river != null) {
				ObservatoryriverlayerInfo rlayer = observatoryriverlayerInfoService.findByTablemasterInfoId(river.tablemasterinfoid);
				if (rlayer != null)
					updateRiverAttribute(mapDB, rlayer, river, point);
			}
		}
		else if (type.equals(DAM_DATATYPE)) {
			ObservatorydamInfo dam = observatorydamInfoService.findByLocalgovInfoIdAndOfficecodeAndObsrvtncode(govid, Integer.parseInt(sadmin), Integer.parseInt(sno));
			if (dam != null) {
				ObservatorydamlayerInfo rlayer = observatorydamlayerInfoService.findByTablemasterInfoId(dam.tablemasterinfoid);
				if (rlayer != null)
					updateDamAttribute(mapDB, rlayer, dam, point);
			}
		}
	}
	
	/**
	 * ダム水位の属性を更新する
	 * @param mapDB
	 * @param omapInfo
	 * @param dlayer
	 * @param dam
	 * @throws Exception
	 */
	protected void updateDamAttribute(MapDB mapDB, ObservatorydamlayerInfo dlayer, ObservatorydamInfo dam, PointContainer point) throws Exception
	{
		Long tableid = dam.tablemasterinfoid;
		TablemasterInfo table = tablemasterInfoService.findById(tableid);
		long fid = dam.featureid;
		
		//値を取得
		ItemData data = null;
		for (ItemData item : point.itemList) {
			if (item.getItemCode() == 10)
				data = item;
		}

		Double val = null;
		if (data.value != null)
			val = Double.parseDouble(data.value);
		Timestamp time = point.observTime;
		
		//送信データを準備
		JSONObject json = new JSONObject();
		json.put("fid", fid);
		if (StringUtil.isNotEmpty(dlayer.attrvalue)) {
			json.put(dlayer.attrvalue, val);
		}
		//if (dlayer.getAttrlevel() != null && !dlayer.getAttrlevel().isEmpty()) {
		//}
		if (StringUtil.isNotEmpty(dlayer.attrtime)) {
			json.put(dlayer.attrtime, String.valueOf(time));
		}

		execEcomAttrUpdate(table, fid, json, time);
	}

	/**
	 * 雨量データをフィーチャに登録する
	 * @param mapDB 地図DBオブジェクト
	 * @param rlayer レイヤ
	 * @param rain 雨量観測所情報
	 * @throws Exception
	 */
	protected void updateRainAttribute(MapDB mapDB, ObservatoryrainlayerInfo rlayer, ObservatoryrainInfo rain, PointContainer point) throws Exception
	{
		Long tableid = rain.tablemasterinfoid;
		TablemasterInfo table = tablemasterInfoService.findById(tableid);
		long fid = rain.featureid;
		
		ItemData data = null;
		for (ItemData item : point.itemList) {
			if (item.getItemCode() == 10)
				data = item;
		}
		
		Double val = null;
		if (data.value != null)
			val = Double.parseDouble(data.value);
		Timestamp time = point.observTime;

		//送信データを準備
		double dval = -1;
		JSONObject json = new JSONObject();
		json.put("fid", fid);
		if (StringUtil.isNotEmpty(rlayer.attrvalue)) {
			json.put(rlayer.attrvalue, val);
			if (val != null)
				dval = val;
		}
		if (StringUtil.isNotEmpty(rlayer.attrlevel)) {
			//値インデックスを計算
			int lvl = 0;
			for (int i = 0; i < rainvlevel.length; i++) {
				if (dval <= rainvlevel[i]) {
					lvl = i;
					break;
				}
			}
			json.put(rlayer.attrlevel, String.valueOf(lvl));
		}
		if (StringUtil.isNotEmpty(rlayer.attrtime)) {
			json.put(rlayer.attrtime, String.valueOf(time));
		}
		

		execEcomAttrUpdate(table, fid, json, time);
	}
	
	/**
	 * 河川水位の属性を更新する
	 * @param mapDB
	 * @param omapInfo
	 * @param rlayer
	 * @param river
	 * @throws Exception
	 */
	protected void updateRiverAttribute(MapDB mapDB, ObservatoryriverlayerInfo rlayer, ObservatoryriverInfo river, PointContainer point) throws Exception
	{
		Long tableid = river.tablemasterinfoid;
		TablemasterInfo table = tablemasterInfoService.findById(tableid);
		long fid = river.featureid;
		
		ItemData data = null;
		ItemData data2 = null;//水位変化量
		for (ItemData item : point.itemList) {
			if (item.getItemCode() == 10)
				data = item;
			if (item.getItemCode() == 50)
				data2 = item;
		}
		
		Double val = null;
		if (data.value != null)
			val = Double.parseDouble(data.value);
		Double val2 = null;
		if (data2.value != null)
			val2 = Double.parseDouble(data2.value);
		Timestamp time = point.observTime;
		
		//送信データを準備
		JSONObject json = new JSONObject();
		json.put("fid", fid);
		if (StringUtil.isNotEmpty(rlayer.attrvalue)) {
			json.put(rlayer.attrvalue, val);
		}
		if (StringUtil.isNotEmpty(rlayer.attrlevel) && val2 != null) {
			//値インデックスを計算
			int lvl = 0;
			if (river.waterlevel1 == null && river.waterlevel2 == null && river.waterlevel3 == null && river.waterlevel4 == null) {
				if (val2 < 0d) lvl = 1;
				if (new BigDecimal(val2).equals(new BigDecimal(0d))) lvl = 2;
				if (val2 > 0d) lvl = 3;
			}
			else {
				Float[] wlevel = new Float[]{Float.valueOf(0), river.waterlevel1, river.waterlevel2, river.waterlevel3, river.waterlevel4};
				for (int i = 0; i < wlevel.length; i++) {
					if (wlevel[i] != null && wlevel[i] < val) {
						if (val2 < 0d) lvl = 4+(i*3);
						if (new BigDecimal(val2).equals(new BigDecimal(0d))) lvl = 5+(i*3);
						if (val2 > 0d) lvl = 6+(i*3);
					}
				}
			}
			json.put(rlayer.attrlevel, String.valueOf(lvl));
		}
		if (StringUtil.isNotEmpty(rlayer.attrtime)) {
			json.put(rlayer.attrtime, String.valueOf(time));
		}
		
		execEcomAttrUpdate(table, fid, json, time);
	}

	protected long execEcomAttrUpdate(TablemasterInfo table, long fid, JSONObject json, Timestamp time) {
		try {
			MapDB mapDB = MapDB.getMapDB();
			UserInfo userInfo = mapDB.getAuthIdUserInfo("admin");
			LayerInfo layerInfo = mapDB.getLayerInfo(table.layerid);
			// 履歴レイヤの登録処理
			if(TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
				// オリジナル地物を取得
				FeatureResult orgfeature = FeatureDB.getFeatureContent(layerInfo, fid, /*attrGrouped*/false, /*resultGeomType*/ FeatureDB.GEOM_TYPE_GEOM, /*bbox*/null, /*timeParam*/null);
				String wkt = orgfeature.getWKT();
				HashMap<String, String> attributes = ExMapDB.getAttributes(orgfeature);

				// 属性更新
				JSONArray names = json.names();
				for(int idx=0; idx<names.length(); idx++) {
					String name = names.getString(idx);
					if("fid".equals(name)) continue; // fid はコピー除外
					attributes.put(name, json.get(name).toString());
				}

				ISO8601Formatter format = new ISO8601Formatter();
				attributes.put("time_from", format.format(time));

				long newFid = FeatureDB.insertFeatureHistory(layerInfo, /*orgId*/fid, wkt, attributes, userInfo);
				return newFid;
			}
			// 通常の登録情報レイヤの登録処理
			else {
				mapDB.updateContentsAttribute(table.layerid, json);
				return fid;
			}
		} catch(Exception e) {
			throw new ServiceException("Error execEcomAttrUpdate: "+table.layerid+"."+fid+" timestamp at "+time, e);
		}
	}

	public class ObservPointParser {
		/**
		 * DOMツリーのルート.
		 */
		Node rootnode;

		public String office;
		
		public Timestamp observTime;
		
		public List<PointContainer> pointList;
		
		public ObservPointParser(Document doc) {
			rootnode = doc.getDocumentElement();
			
			pointList = new ArrayList<PointContainer>();
		}
		
		/**
		 * パース
		 * 
		 * @throws ParseException
		 * @throws IOException
		 */
		public void parse() throws ParseException, IOException
		{
			NodeList childList = rootnode.getChildNodes();
			int childCount = childList.getLength();
			for (int i = 0; i < childCount; i++){
				Node child = childList.item(i);
				int type = child.getNodeType();
				if( type == Node.ELEMENT_NODE )
				{
					Element elem = (Element)child;
					String tagName = elem.getTagName (); 
					if(tagName.equals("Header")) {
						readHead(elem);
					}
					else if (tagName.equals("Body")) {
						readBody(elem);
					}
				}
			}
		}	
		
		protected void readHead(Element node) throws ParseException
		{
			NodeList childList = node.getChildNodes();
			int childCount = childList.getLength();
			for (int i = 0; i < childCount; i++){
				Node child = childList.item(i);
				int type = child.getNodeType();
				if( type == Node.ELEMENT_NODE )
				{
					Element elem = (Element)child;
					String tagName = elem.getTagName (); 
					if(tagName.equals("Office")) {
						
					}
					else if(tagName.equals("ObsrvtnTime")) {
						if (elem.getFirstChild() != null) {
							String time = ((Text)(elem.getFirstChild())).getData().trim();
							time = time.replace('T', ' ');
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							observTime = new Timestamp(df.parse(time).getTime());
						}
					}
				}
			}
		}	
		protected void readBody(Element node) throws ParseException
		{
			NodeList childList = node.getChildNodes();
			int childCount = childList.getLength();
			for (int i = 0; i < childCount; i++){
				Node child = childList.item(i);
				int type = child.getNodeType();
				if( type == Node.ELEMENT_NODE )
				{
					Element elem = (Element)child;
					String tagName = elem.getTagName (); 
					if(tagName.equals("ItemKind")) {
						readItemKind(elem);
					}
				}
			}
		}
		
		protected void readItemKind(Element node) throws ParseException
		{
			NodeList childList = node.getChildNodes();
			int childCount = childList.getLength();
			for (int i = 0; i < childCount; i++){
				Node child = childList.item(i);
				int type = child.getNodeType();
				if( type == Node.ELEMENT_NODE )
				{
					Element elem = (Element)child;
					String tagName = elem.getTagName (); 
					if(tagName.equals("ObsrvtnPointContainer")) {
						PointContainer rain = new PointContainer();
						pointList.add(rain);
						rain.pointFullCode = elem.getAttribute("obsrvtnPointFullCode");
						rain.pointName = elem.getAttribute("obsrvtnPointName");
						rain.observTime = this.observTime;
						readPointContainer(elem, rain);
					}
				}
			}
		}

		protected void readPointContainer(Element node, PointContainer rain) throws ParseException
		{
			NodeList childList = node.getChildNodes();
			int childCount = childList.getLength();
			for (int i = 0; i < childCount; i++){
				Node child = childList.item(i);
				int type = child.getNodeType();
				if( type == Node.ELEMENT_NODE )
				{
					Element elem = (Element)child;
					String tagName = elem.getTagName (); 
					if(tagName.equals("ItemData")) {
						ItemData item = new ItemData();
						item.itemCode = elem.getAttribute("itemCode");
						item.itemName = elem.getAttribute("itemName");
						item.itemType = elem.getAttribute("itemType");
						item.unitName = elem.getAttribute("unitName");
						item.contentsCode = elem.getAttribute("contentsCode");
						item.contentsName = elem.getAttribute("contentsName");
						if (elem.getFirstChild() != null)
							item.value = ((Text)(elem.getFirstChild())).getData().trim();
						rain.itemList.add(item);
					}
				}
			}
		}
	}
}
