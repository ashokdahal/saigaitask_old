/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.seasar.framework.log.Logger;

import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgeInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgealarmInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgeformulaMaster;
import jp.ecom_plat.saigaitask.entity.db.JudgemanInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgenoticeInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultData;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleData;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgeInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgealarmInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgeformulaMasterService;
import jp.ecom_plat.saigaitask.service.db.JudgemanInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgenoticeInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgeresultDataService;
import jp.ecom_plat.saigaitask.service.db.JudgeresultstyleDataService;
import jp.ecom_plat.saigaitask.service.db.JudgeresultstyleInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticemailDataService;
import jp.ecom_plat.saigaitask.service.db.NoticemailsendDataService;
import jp.ecom_plat.saigaitask.service.db.TelemeterDataService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.hishidama.eval.ExpRuleFactory;
import jp.hishidama.eval.Expression;
import jp.hishidama.eval.var.MapVariable;

/**
 * テレメータの値が閾値を超えた場合にトリガーを起動するサービスクラス
 */
@org.springframework.stereotype.Service
public class RiskJudgeService {

	Logger logger = Logger.getLogger(RiskJudgeService.class);

	/**
	 * データを取得するスレッド
	 */
	//protected JudgeThread thread = null;
	/**
	 * データを取得するタスク
	 */
	//protected JudgeTask task;
	/**
	 * タスクのインターバルミリ秒
	 */
	//protected int interval = 60000;

	/** 判定管理情報サービス */
	@Resource
	protected JudgemanInfoService judgemanInfoService;
	/** 判定情報サービス */
	@Resource
	protected JudgeInfoService judgeInfoService;
	/** 判定結果サービス */
	@Resource
	protected JudgeresultDataService judgeresultDataService;
	/** 式サービス */
	@Resource
	protected JudgeformulaMasterService judgeformulaMasterService;
	/** 判定体制情報サービス */
	//@Resource
	//protected JudgestationInfoService judgestationInfoService;
	/** データ情報サービス */
	@Resource
	protected TelemeterDataService telemeterDataService;
	/** 記録データサービス */
	@Resource
	protected TrackDataService trackDataService;

	/** 判定アラーム情報サービス */
	@Resource
	protected JudgealarmInfoService judgealarmInfoService;
	/** アラームサービス */
	@Resource
	protected AlarmmessageInfoService alarmmessageInfoService;
	/** アラームデータサービス */
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;
	@Resource
	protected JudgenoticeInfoService judgenoticeInfoService;
	@Resource
	protected NoticegroupInfoService noticegroupInfoService;
	@Resource
	protected NoticemailDataService noticemailDataService;
	@Resource
	protected NoticemailsendDataService noticemailsendDataService;
	@Resource
	protected JudgeresultstyleInfoService judgeresultstyleInfoService;
	@Resource
	protected JudgeresultstyleDataService judgeresultstyleDataService;

	//@Resource
	//protected StationreachDataService stationreachDataService;

	/**
	 * 初期実行
	 * @param start スレッド実行フラグ
	 */
	public void init(boolean start)
	{
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
	 * リスク判定を行うスレッドを開始する
	 *
	 * @throws ServletException
	 */
	/*protected void startThread() {
		try {
			task = new JudgeTask();
			//アップデートスレッドを作成して実行
			thread = new JudgeThread("JudgeThread", task);
			thread.start();
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}*/

	/**
	 * リスク判定を行う処理をスレッドで行う。
	 *
	 */
	//public class JudgeThread extends Thread {
		/**
		 * リスク判定タスク
		 */
		//JudgeTask task;

		/**
		 * コンストラクタ。
		 *
		 * @param name スレッド名
		 * @param task タスク
		 */
		/*public JudgeThread(String name, JudgeTask task) {
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
	 * リスク判定タスク
	 */
	//protected class JudgeTask extends TimerTask {
		/**
		 * コンストラクタ
		 */
		/*public JudgeTask() {
		}*/

		/**
		 * リスク判定
		 */
		/*public void run()
		{
			try {
				checkRisk();
				//System.out.println("checkRisk!");
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}*/

	/**
	 * 判定実行
	 */
	public void checkRisk() {

		List<JudgemanInfo> jmanlist = judgemanInfoService.findByValidOrderByJudgeOrder();

		//判定するリスク管理を検索
		for (JudgemanInfo jman : jmanlist) {

			if (!canJudgeTime(jman)) continue;

			List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(jman.localgovinfoid);
			//if (track == null) continue;

			//すでに判定済みか確認
			JudgeresultData res = judgeresultDataService.findByJudgemanInfoId(jman.id);
			//if (res != null && res.bsent) continue;

			boolean bjudge = true;
			List<JudgeInfo> jlist = judgeInfoService.findByJudgemanInfoIdAndValid(jman.id);
			if (jlist.size() == 0) bjudge = false;
			String rvalue = "";
			List<TelemeterData> datalist = null;
			for (JudgeInfo judge : jlist) {

				datalist = telemeterDataService.getData(judge.telemeterdatacode, judge.itemcode, 1, /*タイムスライダー未対応*/null);
				if (datalist.size() != 1) {
					bjudge = false;
					continue;
				}
				TelemeterData data = datalist.get(0);
				if (data.val == null) {
					bjudge = false;
					continue;
				}

				Object oval = Double.parseDouble(judge.val);
				Object val = data.val;
				try {
					JudgeformulaMaster formula = judgeformulaMasterService.findById(judge.judgeformulaid);
					boolean ret = judgeRisk(Types.DOUBLE, formula.formula, oval, val);
					if (!ret)//基本的にAND判定
						bjudge = false;
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				if (rvalue.isEmpty())
					rvalue = val.toString();
				else
					rvalue += ","+val.toString();
			}

			if (bjudge && (res == null || res.bcancel)) {//判定結果TRUE
				//結果の登録
				JudgeresultData result = new JudgeresultData();
				result.judgemaninfoid = jman.id;
				result.judgetime = new Timestamp(System.currentTimeMillis());
				result.val = rvalue;
				result.bcancel = false;
				judgeresultDataService.insert(result);

				//アラームの送信
				List<JudgealarmInfo> alarmList = judgealarmInfoService.findByJudgemanIdAndValid(jman.id);
				for (JudgealarmInfo alarm : alarmList) {
					AlarmmessageInfo msgInfo = alarmmessageInfoService.findById(alarm.alarmmessageinfoid);
					// 対応中の災害すべてにアラームを出す
					List<AlarmmessageData> msgs = new ArrayList<AlarmmessageData>();
					for(TrackData track : trackDatas) {
						AlarmmessageData msg = alarmmessageDataService.insert(track.id, 0L, msgInfo, null, rvalue);
						msgs.add(msg);
					}

					// メッセージはどれも同じ
					String message = msgs.get(0).message;

					List<JudgenoticeInfo> noticelist = judgenoticeInfoService.findByJudgealarmInfoIdAndValid(alarm.id);
					for (JudgenoticeInfo notice : noticelist) {
						if (notice.noticegroupinfoid != null) {
							List<Long> grpids = new ArrayList<Long>();
							grpids.add(notice.noticegroupinfoid);
							Object[] ret = noticegroupInfoService.sendMailToNoticeGroups(jman.localgovinfoid, grpids, null, msgInfo.name, message);
							boolean bsend = (Boolean)ret[0];
							String mailto = (String)ret[1];

							for(TrackData track : trackDatas) {
								NoticemailData entity = new NoticemailData();
								entity.trackdataid = track.id;
								entity.mailto = mailto;
								entity.title = msgInfo.name;
								entity.content = message;
								entity.sendtime = new Timestamp(System.currentTimeMillis());
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
						}
					}
				}

				//体制移行の送信
				/*List<JudgestationInfo> stationList = judgestationInfoService.findByJudgemanIdAndValid(jman.getId());
				for (JudgestationInfo station : stationList) {
					StationreachData reach = new StationreachData();
					reach.setReached(true);
					reach.setReachtime(new Timestamp(System.currentTimeMillis()));
					reach.setStationtriggerinfoid(station.getStationtriggerinfoid());
					reach.setTrackdataid(track.getId());
					stationreachDataService.insert(reach);
				}*/
			}
			else if (!bjudge && res != null && !res.bcancel) {//判定結果FALSE

				//判定解除
				res.bcancel = true;
				res.canceltime = new Timestamp(System.currentTimeMillis());
				judgeresultDataService.update(res);
			}
			//リストの色設定
			if (bjudge) {
				JudgeresultstyleInfo style = judgeresultstyleInfoService.findByJudgemanInfoId(jman.id);
				if (style != null && datalist != null && datalist.size() > 0) {
					for (TelemeterData data : datalist) {
						if (judgeresultstyleDataService.findByTelemeterDataId(data.id) == null) {
							JudgeresultstyleData sdata = new JudgeresultstyleData();
							sdata.judageresultstyleinfoid = style.id;
							sdata.telemeterdataid = data.id;
							judgeresultstyleDataService.insert(sdata);
						}
					}
				}
			}
		}
	}

	/**
	 * 現在時刻で判定を行うかどうか返す。
	 * @param jman データ判定管理情報
	 *
	 * @return TRUE 判定を行う
	 * @throws SQLException
	 */
	public boolean canJudgeTime(JudgemanInfo jman)
	{
		Calendar now = Calendar.getInstance();
		int min = now.get(Calendar.MINUTE);

		int intv = jman.interval;
		if (intv == 0) return false;//固定の判定

		int div = (min-jman.delay) % intv;
		if (div != 0)
			return false;

		return true;
	}

	/**
	 * 値の変換
	 * @param type
	 * @param val
	 * @return 値
	 */
	protected Object changeValue(int type, String val)
	{
		switch (type) {
		case Types.BIGINT:
			return Long.parseLong(val);
		case Types.DOUBLE:
			return Double.parseDouble(val);
		case Types.FLOAT:
		case Types.REAL:
			return Float.parseFloat(val);
		case Types.INTEGER:
			return Integer.parseInt(val);
		case Types.SMALLINT:
		case Types.TINYINT:
			return Short.parseShort(val);
		}
		return val;
	}

	/**
	 * 値の判定
	 * @param valueType SQLの型
	 * @param method 式
	 * @param oval 閾値
	 * @param obj 値
	 * @return 結果
	 * @throws IOException
	 */
	public static boolean judgeRisk(int valueType, String method, Object oval, Object obj) throws IOException
	{
		//値の判定
		Map<String, Object> varMap = new HashMap<String, Object>();
		if (method.indexOf("r1") >= 0)//判定値
			varMap.put("r1", oval);
		if (method.indexOf("v1") >= 0)//情報値
			varMap.put("v1", obj);
		//System.out.println("vi="+((Double)obj).doubleValue());
		return judgeRisk(valueType, method, varMap);
	}

	protected static boolean judgeRisk(int valueType, String method, Map<String, Object> varMap) throws IOException
	{
		boolean judge = false;

		Expression exp = ExpRuleFactory.getDefaultRule().parse(method);
		exp.setVariable(new MapVariable(varMap));
		switch (valueType) {
		case Types.VARCHAR:
			Object oret = exp.eval();
			if (oret instanceof Boolean) {
				if (((Boolean)oret).booleanValue())
					judge = true;
			}
			else
				throw new IOException("unsupported value or method type ");
			break;
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.TINYINT:
			long ret = exp.evalLong();
			if (ret == 1) //配列の一つでも有効であれば送信可
				judge = true;
			break;
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
			double dret = exp.evalDouble();
			if (dret == 1d) //配列の一つでも有効であれば送信可
				judge = true;
			break;
		default:
			throw new IOException("unsupported value or method type ");
		}

		return judge;
	}
}
