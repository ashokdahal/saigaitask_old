/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import facebook4j.FacebookException;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Disaster;
import jp.ecom_plat.saigaitask.constant.PubliccommonsSendStatusValue;
import jp.ecom_plat.saigaitask.dto.AlertrequestInfoDto;
import jp.ecom_plat.saigaitask.dto.AlerttriggerDataDto;
import jp.ecom_plat.saigaitask.dto.AlerttriggerInfoDto;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.RefugeInformationDto;
import jp.ecom_plat.saigaitask.dto.ShelterInformationDto;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.MeteotypeMaster;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportRefugeInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TelopData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AlertcontentDataService;
import jp.ecom_plat.saigaitask.service.db.AssembleInfoService;
import jp.ecom_plat.saigaitask.service.db.FacebookInfoService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteotriggerDataService;
import jp.ecom_plat.saigaitask.service.db.MeteotypeMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteoxsltInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticemailDataService;
import jp.ecom_plat.saigaitask.service.db.NoticemailsendDataService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportRefugeInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportShelterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TelopDataService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.db.TwitterInfoService;
import jp.ecom_plat.saigaitask.service.publiccommons.PublicCommonsService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import twitter4j.TwitterException;


/**
 * 気象情報を取得してアラームを出す共通処理をもった抽象サービス
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractAlarmService {

	Logger logger = Logger.getLogger(AbstractAlarmService.class);
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/**
	 * データを取得するスレッド
	 */
	//protected AlarmThread thread = null;
	/**
	 * データを取得するタスク
	 */
	//protected AlarmTask task;
	/**
	 * タスクのインターバルミリ秒
	 */
	//protected int interval = 60000;

	/** 気象情報種別マスタサービス */
	@Resource
	protected MeteotypeMasterService meteotypeMasterService;
	protected Map<Integer, String> meteoTypeMap = new HashMap<Integer, String>();
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;
	@Resource
	protected MeteoxsltInfoService meteoxsltInfoService;

	@Resource
	protected FileService fileService;
	@Resource
	protected LocalgovInfoService localgovInfoService;

	@Resource
	protected NoticegroupInfoService noticegroupInfoService;
	@Resource
	protected StationService stationService;


	@Resource
	protected AssembleInfoService assembleInfoService;
	@Resource
	protected NoticemailDataService noticemailDataService;
	@Resource
	protected NoticemailsendDataService noticemailsendDataService;
	@Resource
	protected FacebookInfoService facebookInfoService;
	@Resource
	protected TwitterInfoService twitterInfoService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	@Resource
	protected TableService tableService;
	@Resource
	protected TableFeatureService tableFeatureService;
	@Resource
	protected MeteotriggerDataService meteotriggerDataService;
	@Resource
	protected HistoryTableService historyTableService;
	@Resource
    protected UserTransaction userTransaction;
	@Resource
	protected MeteoricEarthQuakeService meteoricEarthQuakeService;
	@Resource
	protected AlertcontentDataService alertcontentDataService;

	/** 班サービス */
	@Resource
	protected GroupInfoService groupInfoService;
	/** テロップデータサービス */
	@Resource
	protected TelopDataService telopDataService;
	@Resource
	protected PublicCommonsService publicCommonsService;
	@Resource
	protected PubliccommonsReportRefugeInfoService publiccommonsReportRefugeInfoService;
	@Resource
	protected PubliccommonsReportShelterInfoService publiccommonsReportShelterInfoService;

	/** ログイン情報 */
	@Resource public LoginDataDto loginDataDto;

	protected String meteoUrl;
	protected String mxmlPath;
	protected String mxsltPath;
	protected String mxsltvalPath;
	protected String demoTitleHeader;
	protected String demoContentHeader;
	protected String demoContentFooter;

	protected static final long expire = 2*24*60*60*1000;//2日

	/**
	 * 初期実行
	 * @param start スレッド実行フラグ
	 */
	public void init(boolean start)
	{
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		meteoUrl = rb.getString("METEOURL");

		mxmlPath = rb.getString("METEOXMLPATH");
		mxsltPath = rb.getString("METEOXSLTPATH");
		mxsltvalPath = rb.getString("METEOXSLTVALUEPATH");
		demoTitleHeader = rb.getString("DEMO_TITLE_HEADER");
		demoContentHeader = rb.getString("DEMO_CONTENT_HEADER");
		demoContentFooter = rb.getString("DEMO_CONTENT_FOOTER");

		List<MeteotypeMaster> typelist = meteotypeMasterService.findAll();
		for (MeteotypeMaster type : typelist)
			meteoTypeMap.put(type.id, type.type);

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
			task = new AlarmTask();
			//アップデートスレッドを作成して実行
			thread = new AlarmThread("AlarmThread", task);
			thread.start();
			//System.out.println("Alarm Thread Start!");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}*/

	/**
	 * アラームを行う処理をスレッドで行う。
	 *
	 */
	//protected class AlarmThread extends Thread {
		/**
		 * リスク判定タスク
		 */
		//AlarmTask task;

		/**
		 * コンストラクタ。
		 *
		 * @param name スレッド名
		 * @param task タスク
		 */
		/*public AlarmThread(String name, AlarmTask task) {
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
	//protected class AlarmTask extends TimerTask {
		/**
		 * コンストラクタ
		 */
		/*public AlarmTask() {
		}*/

		/**
		 * アラームチェック実行
		 */
		/*public void run()
		{
			try {
				checkAlarm();
				//logger.debug("check alarm!");
				// 震度レイヤを一定期間毎に地物を削除する関数を呼び出す
				meteoricEarthQuakeService.earthquakeLayerDeleteCheck();
			}
			catch (Exception e) {
				logger.error("",e);
			}
		}
	}*/

	/**
	 * アラームチェック実行
	 */
	protected abstract void checkAlarm();

	protected void execAlarmData(AlertrequestInfoDto req, AlerttriggerInfoDto trigger, File jmaxml, String[] datas, String title, StringBuffer msg, long meteodataid, long demoid) throws Exception
	{
		/** 以下、災害種別ごとにテロップ表示用の文字列を加工する */
		if (req.meteotypeid.equals(Constants.METEO_KISHOUKEIHOUCHUIHOU)){//気象警報・注意報
			for(int j=2; j<datas.length; j++){
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_KISHOUKEIHOUCHUIHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "kishoukeihouChuihou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_SHINDOSOKUHOU)) {//震度速報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_SHINDOSOKUHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "shindoSokuhou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_TSUNAMIKEIHOUCHUIHOU)) {//津波警報・注意報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_TSUNAMIKEIHOUCHUIHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "tsunamikeihouChuihou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_SHINGENSHINDOJOUHOU)) {//震源・震度に関する情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_SHINGENSHINDOJOUHOU, demoid);
			}
			// 地震用レイヤの作成
			// レイヤ追加フラグを取得
			// TODO: 気象庁XML受信(MeteoricAlarmService)と、JAlert受信(JAlertAlarmService)の両方で受信すると震度レイヤが重複して作成される
			if(trigger!=null && trigger.addlayer){
				if (req.meteotypeid.equals(Constants.METEO_SHINGENSHINDOJOUHOU)) {//震源・震度に関する情報
					meteoricEarthQuakeService.inputEarthQuake(jmaxml, meteodataid, req);
				}
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "shingenShindoJouhou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_SHITEIKASENKOUZUIYOHOU)) {//指定河川洪水予報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_SHITEIKASENKOUZUIYOHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "shiteiKasenKouzuiYohou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_DOSYASAIGAIKEIKAIJOUHOU)) {//土砂災害警戒情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_DOSYASAIGAIKEIKAIJOUHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "dosyasaigaiKeikaiJouhou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_KIROKUTEKITANJIKANOOAMEJOUHOU)) {//記録的短時間大雨情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_KIROKUTEKITANJIKANOOAMEJOUHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "kirokutekiTanjikanOoameJouhou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_TASTUMAKICHUUIJOUHOU)) {//竜巻注意情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_TASTUMAKICHUUIJOUHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "tastumakiChuuiJouhou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_FUNKAKEIHOUYOHOU)) {//噴火警報・予報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_FUNKAKEIHOUYOHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			startDisasterMode(req, trigger, mxsltvalPath + "funkakeihouYohou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
		else if (req.meteotypeid.equals(Constants.METEO_KINKYUJISHINSOKUHOU)) {//緊急地震速報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
			if (req.alarm) {//アラーム
				addAlarm(req.localgovinfoid, msg.toString(), "information", demoid);
			}
			if (req.view) {
				addTelop(req.localgovinfoid, msg.toString(), Constants.TELOPTYPE_KINKYUJISHINSOKUHOU, demoid);
			}
			//災害モード起動、体制発令、コモンズ通知など
			//startDisasterMode(req, trigger, mxsltvalPath + "shingenShindoJouhou_value.xsl", jmaxml, title, msg.toString(), demoid);
			//受信履歴
			addHistory(req.localgovinfoid, req.meteotypeid, title, msg.toString(), jmaxml.getPath());
		}
	}

	/**
	 * 受信履歴に追加
	 * @param localgovinfoid
	 * @param type
	 * @param title
	 * @param content
	 * @param filepath
	 */
	protected void addHistory(long localgovinfoid, int type, String title, String content, String filepath)
	{
		AlertcontentData contentData = new AlertcontentData();
		contentData.localgovinfoid = localgovinfoid;
		contentData.receivetime = new Timestamp(System.currentTimeMillis());
		contentData.teloptypeid = type;
		contentData.filepath = filepath;
		contentData.title = title;
		contentData.content = content;
		alertcontentDataService.insert(contentData);
	}

	protected String[] getDatas(String xsltFileName, File jmaxml) throws TransformerException
	{
		File xsltFile = new File(fileService.getXmlRoot() + xsltFileName);
		//parse準備
		TransformerFactory trfactory = TransformerFactory.newInstance();
		StringWriter swriter = new StringWriter();
		Transformer t = trfactory.newTransformer(new StreamSource(xsltFile));
		//parse
		t.transform(new StreamSource(jmaxml), new StreamResult(swriter));
		String[] lines = swriter.toString().split("\\\\n");

		return lines;
	}

	/**
	 * 災害種別が「震度速報」の場合、震度5以上であれば災害モードに入る
	 * @param req
	 * @param shindo
	 * @throws Exception
	 */
	protected void startDisasterMode(AlertrequestInfoDto req, AlerttriggerInfoDto trigger, String xsltFileName, File jmaxml, String title, String content, long demoid) throws Exception 	{
		PCommonsSendDto pCommonsSendDto = new PCommonsSendDto();
		if (trigger == null) return;

		// 訓練モード時は終了する
		List<TrackData> currentTrainings = trackDataService.findByCurrentTrackDatas(req.localgovinfoid, true);
		if(currentTrainings.size() > 0) return;

		//デモ？
		if (demoid > 0) {
			title = demoTitleHeader+title;
			content = demoContentHeader+"\n"+content+"\n"+demoContentFooter;
		}

		//自動発砲OFF
		LocalgovInfo gov = localgovInfoService.findById(req.localgovinfoid);
		if (!gov.autostart) return;

		AlerttriggerDataDto triggerData = new AlerttriggerDataDto();
		triggerData.localgovinfoid = req.localgovinfoid;
		triggerData.meteotriggerinfoid = trigger.id;

		String[] datas = getDatas(xsltFileName, jmaxml);
		String[] trgs = trigger.trigger.split(",");

		boolean bmatch = false;
		int disasterid = 0;
		String areacode = req.meteoareaid;//エリアID
		//areacode = "220";//震度速報：宮崎県北部
		//areacode = "290010";//警報注意報：奈良県北部
		//areacode = "300";//津波：茨城県
		//areacode = "0421300";//震源震度：栗原市

		for (String data : datas) {//エリアコード:観測値
			String dat[] = data.split(":");
			String acodes[] = dat[0].split(",");
			for (String acode : acodes) {//エリアコード
				if (acode.equals(areacode)) {
					String vals[] = dat[1].split(",");
					for (String val : vals) {//観測値
						for (String trg : trgs) {//トリガー値
							if (val.equals(trg)) {//ヒット
								bmatch = true;
								break;
							}
						}
						if (bmatch) break;
					}
				}
				if (bmatch) break;
			}
			if (bmatch) break;
		}

		if (!bmatch) return;

		userTransaction.commit();

		//気象警報注意報
		if (req.meteotypeid.equals(Constants.METEO_KISHOUKEIHOUCHUIHOU) || req.meteotypeid.equals(Constants.METEO_DOSYASAIGAIKEIKAIJOUHOU) ||
				req.meteotypeid.equals(Constants.METEO_KIROKUTEKITANJIKANOOAMEJOUHOU) || req.meteotypeid.equals(Constants.METEO_TASTUMAKICHUUIJOUHOU) ||
				req.meteotypeid.equals(Constants.METEO_SHITEIKASENKOUZUIYOHOU))
			disasterid = Disaster.FLOOD;
		//震度速報 津波
		else if (req.meteotypeid.equals(Constants.METEO_SHINDOSOKUHOU) || req.meteotypeid.equals(Constants.METEO_TSUNAMIKEIHOUCHUIHOU) ||
				req.meteotypeid.equals(Constants.METEO_SHINGENSHINDOJOUHOU))
			disasterid = Disaster.EARTHQUAKE;
		//火山
		else if (req.meteotypeid.equals(Constants.METEO_FUNKAKEIHOUYOHOU))
			disasterid = Disaster.VOLCANO;

		//災害モード起動
		TrackData track = null;
		triggerData.startup = false;
		List<TrackData> currentTracks = trackDataService.findByCurrentTrackDatas(req.localgovinfoid);
		if(0<currentTracks.size()) {
			logger.info(lang.__("For other disaster is already running, new disaster can not be run automatically by the trigger: Japan Meteorological Agency XML acquisition info = {0}", req.id));
		}
		else {
			try {
				userTransaction.begin();
				// 災害自動起動フラグがON
				if (trigger.startup) {
					if (disasterid != 0) {
						//災害記録の作成
						TrackData trackData = new TrackData();
						Timestamp now = new Timestamp(System.currentTimeMillis());
						SimpleDateFormat sdf = new SimpleDateFormat(lang.__("M 'month' d 'day'"));
						trackData.localgovinfoid = req.localgovinfoid;
						trackData.demoinfoid = demoid;
						//trackData.disasterid = disasterid;
						// すでに記録データがあれば、同じ地図を使う
						trackData.trackmapinfoid = 0<currentTracks.size() ? currentTracks.get(0).trackmapinfoid : null;
						trackData.name = sdf.format(now)+" " + lang.__("Disaster");
						trackData.starttime = now;
						trackData.deleted = false;
						trackDataService.insert(trackData);

						GroupInfo groupInfo = groupInfoService.findByLocalgovInfoIdAndAdmin(req.localgovinfoid);

						if(currentTracks.size()==0) {
							try {
								//地図の作成
								trackDataService.createDisasterMap(req.localgovinfoid, groupInfo, trackData, null);
							} catch (Exception e) {
								logger.error("",e);
							}
						}

						track = trackData;
					}
					triggerData.startup = true;

				}
				// 災害を自動起動しない場合は、すでにあれば最新の記録データIDを使う
				else if(0<currentTracks.size()) {
					track = currentTracks.get(0);
				}

				triggerData.trackdataid = track.id;
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {e1.printStackTrace();}
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e);}
			}

			//メール通知
			try {
				userTransaction.begin();
				if (trigger.noticegroupinfoid != null && trigger.noticegroupinfoid > 0) {
					List<Long> glist = new ArrayList<Long>();
					glist.add(trigger.noticegroupinfoid);
					Object[] ret = noticegroupInfoService.sendMailToNoticeGroups(req.localgovinfoid, glist, null, title, content);
					//送信結果
					Long tid = 0l;
					if (track != null) tid = track.id;
					setNoticeData(ret, tid, glist, title, content);
					triggerData.noticegroupinfoid = trigger.noticegroupinfoid;
				}
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {e1.printStackTrace();}
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e); }
			}

			//体制変更
			try {
				userTransaction.begin();
				triggerData.assemblemail = false;

				// トリガーに体制移行区分が設定されていれば
				if (track != null && trigger.stationclassinfoid != null && 0<trigger.stationclassinfoid) {
					// 体制移行トリガーを起動する
					boolean triggered = stationService.triggerShiftstation(track, trigger);
					if(triggered) {
						// トリガーデータへ記録
						triggerData.stationclassinfoid = trigger.stationclassinfoid;

						//職員参集メール
						if (trigger.assemblemail) {
							AssembleInfo assemble = assembleInfoService.findByLocalgovInfoIdAndStationclassInfoIdAndValid(req.localgovinfoid, trigger.stationclassinfoid);
							if (assemble != null) {
								//送信
								Object ret[] = assembleInfoService.sendAssembleMail(assemble, title, content, track);
								List<Long> grpids = new ArrayList<Long>();
								grpids.add(assemble.noticegroupinfoid);
								//送信結果
								setNoticeData(ret, track.id, grpids, title, content);
								triggerData.assemblemail = true;
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {e1.printStackTrace();}
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e); }
			}

			//避難勧告
			triggerData.issue = false;
			try {
				userTransaction.begin();
				if (track != null && trigger.issuetablemasterinfoid != null && trigger.issuetablemasterinfoid > 0 &&
						StringUtil.isNotEmpty(trigger.issueattrid) && StringUtil.isNotEmpty(trigger.issuetext)) {
					//避難勧告をセット
					TablemasterInfo master = tablemasterInfoService.findByNotDeletedId(trigger.issuetablemasterinfoid);
					if (master != null) {
						TracktableInfo table = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(trigger.issuetablemasterinfoid, track.id);
						if (table != null) {

							LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(table.layerid);
							String key = LayerInfo.TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType) ? "_orgid" : "gid";

							// 時系列対応版
							if(layerInfo.timeSeriesType==LayerInfo.TimeSeriesType.HISTORY) {

								Timestamp time = new Timestamp(TimeUtil.newDate().getTime());
								String timeQuery = "";
								if(LayerInfo.TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
									Date[] timeParam = new Date[]{new Date(time.getTime())};
									timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
								}

								//eコミマップのテーブル
								Map<Long, HashMap<String, String>> features = new HashMap<>();
								List<BeanMap> updateRecords = null;
								if(StringUtil.isNotEmpty(table.layerid)){
									updateRecords = tableService.selectByGrouping(table.layerid, key, /*no group*/new ArrayList<Long>(), timeQuery);

									// 更新属性
									HashMap<String, String> attribute = new HashMap<String, String>();
									attribute.put(trigger.issueattrid, trigger.issuetext);

									// 対象IDのリストを初期化
									for(BeanMap updateRecord : updateRecords) {
										Long fid = (Long) updateRecord.get(key);
										features.put(fid, attribute);
									}
								}

								// 実行
								tableFeatureService.execute(master, table, features, /*isAddColumn*/null, /*delfids*/null);
							}
							else {
								tableService.updateAll(table.tablename, trigger.issueattrid, trigger.issuetext);
								if (StringUtil.isNotBlank(master.updatecolumn)) {
									//更新日時を更新
									Timestamp time = new Timestamp(System.currentTimeMillis());
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									String now = sdf.format(time);
									tableService.updateAll(table.tablename, master.updatecolumn, now);
									triggerData.issue = true;
									triggerData.issuetext = trigger.issuetext;
								}
								// 編集者 属性の値を設定
								tableService.updateAll(table.tablename, EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
								//履歴テーブルに記録
								HistorytableInfo htbl = historyTableService.findOrCreateByTracktableInfo(table, layerInfo);
								if(htbl != null)
									historyTableService.logAll(table, htbl, layerInfo);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {e1.printStackTrace();}
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e); }
			}

			//公共コモンズ
			triggerData.publiccommons = false;
			try {
				userTransaction.begin();
				// デモの時は訓練固定(管理画面の運用種別の設定より優先する)
				if (demoid != 0) pCommonsSendDto.statusValue = PubliccommonsSendStatusValue.EXERCISE;//訓練
				pCommonsSendDto.trackdataid = track.id;
				if (trigger.publiccommons) {
					//避難情報
					PubliccommonsReportRefugeInfo rinfo = publiccommonsReportRefugeInfoService.findByLocalgovInfoId(req.localgovinfoid);
					List<RefugeInformationDto> refugelist = publicCommonsService.getRefugeInformationList(req.localgovinfoid, track.id, rinfo.tablemasterinfoid);
					publicCommonsService.sendMediaEvacuationOrderAuto(refugelist, pCommonsSendDto);
					//避難所送信
					List<ShelterInformationDto> shelterlist = publicCommonsService.getShelterInformationList(req.localgovinfoid, track.id);
					publicCommonsService.sendMediaShelterAuto(shelterlist, pCommonsSendDto);
					triggerData.publiccommons = true;
				}
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) { logger.error(e1); }
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e); }
			}

			//公共コモンズエリアメール
			triggerData.publiccommonsmail = false;
			try {
				userTransaction.begin();
				// デモの時は訓練固定(管理画面の運用種別の設定より優先する)
				if (demoid != 0) pCommonsSendDto.statusValue = PubliccommonsSendStatusValue.EXERCISE;//訓練
				pCommonsSendDto.trackdataid = track.id;
				pCommonsSendDto.title = title;
				pCommonsSendDto.description = content;
				if (trigger.publiccommonsmail) {
					publicCommonsService.sendUrgentMaiAutol(pCommonsSendDto);
					triggerData.publiccommonsmail = true;
				}
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) { logger.error(e1); }
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e); }
			}

			//SNS
			triggerData.sns = false;
			try {
				userTransaction.begin();
				if (trigger.sns) {
					sendSNSMessage(req.localgovinfoid, content);
					triggerData.sns = true;
				}
			} catch (Exception e) {
				logger.error("startDisasterMode",e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {e1.printStackTrace();}
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
						userTransaction.commit();
					else
						userTransaction.rollback();
				} catch (Exception e) { logger.error(e.getMessage(), e); }
			}
		}

		//トリガーの保存
		saveTriggerData(triggerData);

		userTransaction.begin();
	}

	/**
	 * トリガー結果の保存
	 * @param triggerData
	 */
	public void saveTriggerData(AlerttriggerDataDto triggerData) {
		try {
			userTransaction.begin();
			triggerData.triggertime = new Timestamp(System.currentTimeMillis());
			MeteotriggerData tdata = Beans.createAndCopy(MeteotriggerData.class, triggerData).execute();
			meteotriggerDataService.insert(tdata);
		} catch (Exception e) {
			logger.error("startDisasterMode",e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {e1.printStackTrace();}
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
					userTransaction.commit();
				else
					userTransaction.rollback();
			} catch (Exception e) { logger.error(e.getMessage(), e); }
		}
	}

	/**
	 * SNSへメッセージの送信
	 * @param govid
	 * @param title
	 * @param content
	 */
	protected void sendSNSMessage(Long govid, String content)
	{
		//Facebookへ送信
		try {
			facebookInfoService.postMessage(govid, content);
		} catch (FacebookException e) {
			logger.error("sendSNSMessage",e);
		}
		//Twitterへ送信
		try {
			twitterInfoService.postMessage(govid, content);
		} catch (TwitterException e) {
			logger.error("sendSNSMessage",e);
		}
	}

	/**
	 * 通知送信データの保存
	 * @param ret 送信結果
	 * @param trackdataid 記録ID（平時は０）
	 * @param grpids 送信グループID
	 * @param title
	 * @param content
	 */
	protected void setNoticeData(Object[] ret, Long trackdataid, List<Long> grpids, String title, String content)
	{
		//実行時刻
		Timestamp now = new Timestamp(System.currentTimeMillis());

		boolean bsend = (Boolean)ret[0];
		String mailto = (String)ret[1];

		//通知データ（メール配信）を履歴に登録
		NoticemailData entity = new NoticemailData();
		entity.trackdataid = trackdataid;
		entity.mailto = mailto;
		entity.title = title;
		entity.content = content;
		entity.sendtime = now;
		entity.send = bsend;
		noticemailDataService.insert(entity);

		for (Long grpid : grpids) {
			//通知先データ（メールグループ）を履歴に登録
			NoticemailsendData entity2 = new NoticemailsendData();
			entity2.noticemaildataid = entity.id;
			entity2.noticegroupinfoid = grpid;
			noticemailsendDataService.insert(entity2);
		}

	}

	/*
	protected boolean enableDisasterMode(MeteorequestInfo req, JSONArray jary) throws JSONException
	{
		TrackData track = trackDataService.findByCurrentTrackData(req.getLocalgovinfoid());
		if (track != null) return false;//すでに災害モード

		if (req.getMeteotypeid().equals(Constants.METEO_SHINDOSOKUHOU)) {
			//TODO:本当はトリガーは設定で持ってくる
			float trigger = 5.0f;
			JSONObject obj = jary.getJSONObject(0);
			String shindo = obj.getString("bousaiinfoitem");
			float fshindo = Float.parseFloat(StringUtil.zenkakuToHankaku(shindo.substring(2, 3)));
			if (shindo.length() == 4 && shindo.substring(3).equals("強"))
				fshindo += 0.5f;
			if (fshindo >= trigger)
				return true;
		}
		else if (req.getMeteotypeid().equals(Constants.METEO_TUNAMIKEHOUCHUIHOUYOHOU)) {
			//TODO:本当はトリガーは設定で持ってくる
			String[] trigger = {"62", "51", "52"};
			JSONObject obj = jary.getJSONObject(0);
			String warn = obj.getString("itemcode");
			for (String tr : trigger) {
				if (tr.equals(warn))
					return true;
			}
		}

		return false;
	}
	*/

	/**
	 * 対応中の災害すべてにアラームの追加
	 * @param govid 自治体ID
	 * @param msg メッセージ
	 * @param type 種別名
	 */
	protected void addAlarm(Long govid, String msg, String type, long demoid)
	{
		if (demoid > 0)
			msg = demoContentHeader+"\n"+msg+"\n"+demoContentFooter;

		List<Long> trackids = new ArrayList<Long>();
		List<TrackData> tracks = trackDataService.findByCurrentTrackDatas(govid);
		if(tracks.size()==0) trackids.add(-1L);
		else {
			for(TrackData track : tracks) {
				if (track != null)
					trackids.add(track.id);
			}
		}

		for(Long trackid : trackids) {
			AlarmmessageData alarm = new AlarmmessageData();
			alarm.localgovinfoid = govid;
			alarm.duration = 0;
			alarm.message = msg;
			alarm.messagetype = type;
			alarm.showmessage = true;
			alarm.groupid = 0L;
			alarm.registtime = new Timestamp(System.currentTimeMillis());
			alarm.trackdataid = trackid;
			alarm.deleted = false;

			alarmmessageDataService.insert(alarm);
		}
	}

	/**
	 * テロップの保存
	 * @param govid 自治体ID
	 * @param msg メッセージ
	 * @param type 種別
	 */
	protected void addTelop(Long govid, String msg, Integer type, long demoid)
	{
		if (demoid > 0)
			msg = demoTitleHeader+msg;

		long now = System.currentTimeMillis();
		long lim = now + 86400000L;

		TelopData telop = new TelopData();
		telop.localgovinfoid = govid;
		telop.teloptypeid = type;
		telop.message = msg;
		telop.registtime = new Timestamp(now);
		telop.viewlimit = new Timestamp(lim);
		telopDataService.insert(telop);
	}
}
