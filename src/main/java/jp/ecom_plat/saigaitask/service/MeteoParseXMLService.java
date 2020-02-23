/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;
import jp.ecom_plat.saigaitask.entity.db.MeteoxsltInfo;
import jp.ecom_plat.saigaitask.entity.db.TelopData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AlertcontentDataService;
import jp.ecom_plat.saigaitask.service.db.MeteoxsltInfoService;
import jp.ecom_plat.saigaitask.service.db.TelopDataService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

@org.springframework.stereotype.Service
public class MeteoParseXMLService {
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;
	
	@Resource
	protected FileService fileService;
	@Resource
	protected MeteoxsltInfoService meteoxsltInfoService;
	@Resource
	protected AlertcontentDataService alertcontentDataService;
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;
	@Resource
	protected TelopDataService telopDataService;
	
	public String[] parseMeteoXML(File jmaxml, long localgovinfoid, int meteotypeid, String meteoareaid) throws TransformerException{
		String mxsltPath = Config.getString("METEOXSLTPATH");
		
		/** 取得したXMLファイルをXSLTでparse */
		//xsltファイルの取得
		MeteoxsltInfo xsltInfo = meteoxsltInfoService.findByLocalgovinfoidOrZeroAndMeteotypeid(localgovinfoid, meteotypeid);
		File xsltFile = new File(fileService.getXmlRoot() + mxsltPath + xsltInfo.filepath);
		//parse準備
		TransformerFactory trfactory = TransformerFactory.newInstance();
		StringWriter swriter = new StringWriter();
		Transformer t = trfactory.newTransformer(new StreamSource(xsltFile));
		//parse
		t.setParameter("code", meteoareaid);
		t.transform(new StreamSource(jmaxml), new StreamResult(swriter));
		//結果をStringに出力
		String parseStr = swriter.toString();
		String[] datas = parseStr.split("\\\\n");
		
		return datas;
	}
	
	/**
	 * 発表日時は全種類共通
	 * xsltの解析で、機械的に時刻を+9しているだけなので、24時間を超える時間になっている場合があるので、ここでTimestampに直して発表時刻を修正
	 * @param times
	 * @return
	 * @throws ParseException
	 */
	public String getXMLReporttime(String times) throws ParseException{
		Timestamp reporttime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(times).getTime());
		String reportdatetimeStr = reporttime.toString();
		//末尾の「.0」を削除する
		return reportdatetimeStr.substring(0, reportdatetimeStr.length()-2);
	}
	
	
	/**
	 * 入力ストリームを文字列に変換
	 * @param is 入力ストリーム
	 * @return 文字列
	 * @throws IOException
	 */
	public String convertString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while (null != (line = reader.readLine())) {
			sb.append(line);
		}
		return sb.toString();
	}
	
	/**
	 * テロップ用の文字列を作成するメソッド
	 * @param datas : parseした文字列配列
	 * @param reportdatetimeStr : 配信時間の文字列
	 * @param meteotypeid : 気象上種別番号
	 * @return
	 */
	public String createTelopString(String[] datas, String reportdatetimeStr, Integer meteotypeid){
		// メッセージ用StringBuffer定義
		StringBuffer msg = new StringBuffer();
		msg.append(lang.__("【{0} 気象庁発表】", reportdatetimeStr));
		
		if (meteotypeid.equals(Constants.METEO_KISHOUKEIHOUCHUIHOU)){//気象警報・注意報
			for(int j=2; j<datas.length; j++){
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_SHINDOSOKUHOU)) {//震度速報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_TSUNAMIKEIHOUCHUIHOU)) {//津波警報・注意報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_SHINGENSHINDOJOUHOU)) {//震源・震度に関する情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_SHITEIKASENKOUZUIYOHOU)) {//指定河川洪水予報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_DOSYASAIGAIKEIKAIJOUHOU)) {//土砂災害警戒情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_KIROKUTEKITANJIKANOOAMEJOUHOU)) {//記録的短時間大雨情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_TASTUMAKICHUUIJOUHOU)) {//竜巻注意情報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_FUNKAKEIHOUYOHOU)) {//噴火警報・予報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				msg.append(datas[j]);
				msg.append("　");
			}
		}
		else if (meteotypeid.equals(Constants.METEO_KINKYUJISHINSOKUHOU)) {//緊急地震速報
			for(int j=2; j<datas.length; j++){
				//msgに文字列を組み立てる
				//エリアの最後は「, 」で終わっているので、削除する
				if(datas[j].substring(datas[j].length()-2, datas[j].length()).equals(", ")){
					datas[j] = datas[j].substring(0,datas[j].length()-2);
				}
				msg.append(datas[j]);
				msg.append("　");
			}
		}else{
			return "";
		}
		return msg.toString();
	}
	
	/**
	 * 受信履歴に追加
	 * @param localgovinfoid
	 * @param type
	 * @param title
	 * @param content
	 * @param filepath
	 */
	public void addHistory(long localgovinfoid, int type, String title, String content, String filepath)
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
	
	/**
	 * 対応中の災害すべてにアラームの追加
	 * @param govid 自治体ID
	 * @param msg メッセージ
	 * @param type 種別名
	 */
	public void addAlarm(List<TrackData> tracks, Long govid, String msg, String type, long demoid)
	{
		if (demoid > 0)
			msg = Config.getString("DEMO_CONTENT_HEADER") + "\n"+msg+"\n" + Config.getString("DEMO_CONTENT_FOOTER");

		List<Long> trackids = new ArrayList<Long>();
		//List<TrackData> tracks = trackDataService.findByCurrentTrackDatas(govid);
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
	public void addTelop(Long govid, String msg, Integer type, long demoid)
	{
		if (demoid > 0)
			msg = Config.getString("DEMO_TITLE_HEADER") + msg;
		
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
