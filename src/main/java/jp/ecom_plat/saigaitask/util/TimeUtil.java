/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.geoserver.util.ISO8601Formatter;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.service.training.XmlFileEditHelper;

/**
 * 時間関連のユーティリティクラス
 */
public class TimeUtil {

	static Logger logger = Logger.getLogger(TimeUtil.class);

	static int iDateTimeBase = 0;

	/**
	 * ISO8601の日付フォーマッター
	 * ex: 2016-06-21T14:29.123Z
	 */
	public static ISO8601Formatter iso8601Formatter = new ISO8601Formatter();

	/**
	 * デフォルトタイムゾーンのオフセットを足して ISO8601 形式に変換する.
	 * eコミマップの time_from/time_to が TimeZone をもっていないため、
	 * リクエストするときはタイムゾーン分の時間（日本なら９時間）をプラスする必要がある.
	 * @param msec ミリ秒
	 * @return ex: 2016-06-21T14:29.123Z
	 */
	public static String formatISO8601WithOffset(long msec) {
		return TimeUtil.iso8601Formatter.format(Config.isAvailableUTCTimeZone() ? new Date(msec) : newDateWithOffset(msec));
	}

	/**
	 * @return 現在時刻でDateオブジェクトを取得
	 */
	public static Date newDate() {
		//if(Config.isAvailableUTCTimeZone()) return new Date();
		if(Config.isAvailableUTCTimeZone()) return newUTCDate(System.currentTimeMillis());
		else return newDateWithOffset(System.currentTimeMillis());
	}

	/**
	 * タイムゾーンのオフセットを足した Date を取得
	 * UTC時刻をローカルタイムに戻す場合に利用する
	 * @param msec UTC時刻 ミリ秒
	 * @return タイムゾーンのオフセットを足した Date
	 */
	public static Date newDateWithOffset(long msec) {
		TimeZone tz = TimeZone.getDefault();
		int offset = tz.getOffset(msec);
		return new Date(msec+offset);
	}

	/**
	 * タイムゾーンのオフセットを足した Timestamp を取得
	 * UTC時刻をローカルタイムに戻す場合に利用する
	 * @param msec UTC時刻 ミリ秒
	 * @return タイムゾーンのオフセットを足した Timestamp
	 */
	public static Date newTimestampWithOffset(long msec) {
		TimeZone tz = TimeZone.getDefault();
		int offset = tz.getOffset(msec);
		return new Timestamp(msec+offset);
	}

	/**
	 * タイムゾーンのオフセットを引いた Date を取得
	 * ローカルタイムを UTC時刻に変換する場合に利用する
	 * @param msec ローカルタイム ミリ秒
	 * @return タイムゾーンのオフセットを引いた Date
	 */
	public static Date newUTCDate(long msec) {
		TimeZone tz = TimeZone.getDefault();
		int offset = tz.getOffset(msec);
		return new Date(msec-offset);
	}

	/**
	 * @param time 時間文字列
	 * @return 日付オブジェクト
	 */
	public static Date parseISO8601(String time) {
		return parseISO8601(time, false);
	}
	public static Date parseISO8601(String time, boolean autoTimeZone) {
		// @see com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl
		String[] formats = new String[]{
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
				"yyyy-MM-dd'T'HH:mm:ss.SSS",
				"yyyy-MM-dd'T'HH:mm:ssZ",
				"yyyy-MM-dd'T'HH:mm:ss",
				"yyyy-MM-dd'T'HH:mmZ",
				"yyyy-MM-dd'T'HH:mm",
				"yyyy-MM-ddZ",
				"yyyy-MM-dd"
		};

		if(StringUtil.isNotEmpty(time)) {

			for(String format : formats) {
				try {
					if(autoTimeZone) format = format.replace("Z", "'Z'"); // シングルクォートで囲まないとparse失敗する
					SimpleDateFormat df = new SimpleDateFormat(format);
					if(autoTimeZone && format.endsWith("'Z'")) df.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date date = df.parse(time);
					df.setTimeZone(TimeZone.getDefault());
					return date;
				} catch(ParseException e) {
					// do nothing
				}
			}
		}

		logger.error("Date parse error: \""+time+"\" available formats="+Arrays.toString(formats));

		return null;
	}

	public static Timestamp getTimestamp(String month, String day, String ampm, String hour, String min)
	{
		try {
			Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			int m = Integer.parseInt(month)-1;
			int d = Integer.parseInt(day);
			int h = Integer.parseInt(hour);
			int mi = Integer.parseInt(min);
			int ap = Integer.parseInt(ampm);
			if (ap == Calendar.PM)
				h += 12;

			//最終日チェック
			cal.set(y, m, 1);
			int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (d > max)
				d = max;

			cal.set(y, m, d, h, mi, 0);
			return new Timestamp(cal.getTimeInMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Timestamp getTimestamp(String year, String month, String day, String hour)
	{
		if (year == null || year.length() == 0) return null;
		try {
			int y = Integer.parseInt(year)+2000;
			int m = Integer.parseInt(month)-1;
			int d = Integer.parseInt(day);
			int h = Integer.parseInt(hour);

			Calendar cal = Calendar.getInstance();
			//最終日チェック
			cal.set(y, m, 1);
			int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (d > max)
				d = max;

			cal.set(y, m, d, h, 0, 0);
			return new Timestamp(cal.getTimeInMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param year 西暦年
	 * @param month 月
	 * @param day 日
	 * @param hour 時間（0-23）
	 * @param minute 分
	 * @param sec 秒
	 * @return 引数を使って整形されたTimestamp
	 */
	public static Timestamp getTimestamp2(String year, String month, String day, String hour, String minute, String sec)
	{
		if (year == null || year.length() == 0) return null;
		try {
			int y = Integer.parseInt(year);
			int m = Integer.parseInt(month)-1;
			int d = Integer.parseInt(day);
			int h = Integer.parseInt(hour);
			int min = Integer.parseInt(minute);
			int s = 0;
			if(sec!=null)s = Integer.parseInt(minute);

			Calendar cal = Calendar.getInstance();
			//最終日チェック
			cal.set(y, m, 1);
			int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (d > max)
				d = max;

			cal.set(y, m, d, h, min, s);
			return new Timestamp(cal.getTimeInMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *「yyyy-MM-dd HH:mm:ss」の文字列をTimestampに変換する
	 * @param timeStr
	 * @return Timestamp
	 */
	public static Timestamp getTimestamp3(String timeStr){
		String[] timeArr = convertTimestamp3(timeStr);
		return getTimestamp2(timeArr[0], timeArr[1], timeArr[2], timeArr[3], timeArr[4],timeArr[5]);
	}

	/**
	 * 「（和暦）年MM月 HH時MM分」の文字列をTimestampに変換する
	 * @param timeStr
	 * @return 「yyyy-MM-dd HH:mm:ss」に整形された文字列
	 */
	public static Timestamp getTimestamp4(String timeStr){
		System.out.println();
		timeStr = timeStr.trim();//スペース削除
		String yearStr = timeStr.substring(0,timeStr.indexOf("年"));
		yearStr = convertJ2Single(yearStr);
		String monthStr = timeStr.substring(timeStr.indexOf("年")+1,timeStr.indexOf("月"));
		monthStr = convertJ2Single(monthStr);
		String dayStr = timeStr.substring(timeStr.indexOf("月")+1,timeStr.indexOf("日"));
		dayStr = convertJ2Single(dayStr);
		String hourStr = timeStr.substring(timeStr.indexOf("日")+2,timeStr.indexOf("時"));//日と時の間のスペースを考慮して+2
		hourStr = convertJ2Single(hourStr);
		String minuteStr = timeStr.substring(timeStr.indexOf("時")+1,timeStr.indexOf("分"));
		minuteStr = convertJ2Single(minuteStr);

		Date date = formatWareki(yearStr, monthStr, dayStr, hourStr, minuteStr, "0");
		if(date!=null){
			return new Timestamp(date.getTime());
		}else{
			return null;
		}
	}


	/**
	 * 「yyyy年MM月 HH時MM分」の文字列を「yyyy-MM-dd HH:mm:ss」に変換する
	 * @param timeStr
	 * @return 「yyyy-MM-dd HH:mm:ss」に整形された文字列
	 */
	public static String convertTimestamp(String timeStr){
		timeStr = timeStr.trim();//スペース削除
		String year = timeStr.substring(0,timeStr.indexOf("年"));
		year = convertJ2Single(year);
		String month = timeStr.substring(timeStr.indexOf("年")+1,timeStr.indexOf("月"));
		month = convertJ2Single(month);
		if(month.length()==1)month="0"+month;
		String day = timeStr.substring(timeStr.indexOf("月")+1,timeStr.indexOf("日"));
		day = convertJ2Single(day);
		if(day.length()==1)day="0"+day;
		String hour = timeStr.substring(timeStr.indexOf("日")+1,timeStr.indexOf("時"));
		hour = convertJ2Single(hour);
		if(hour.length()==1)hour="0"+hour;
		String minute = timeStr.substring(timeStr.indexOf("時")+1,timeStr.indexOf("分"));
		minute = convertJ2Single(minute);
		if(minute.length()==1)minute="0"+minute;

		String stampStr = year+"-"+month+"-"+day+hour+":"+minute+":00.0";
		return stampStr;

	}

	/**
	 * 「yyyy-MM-dd HH:mm:ss」の文字列を「yyyy年MM月 HH時MM分」に変換する
	 * @param timeStr
	 * @return「yyyy年MM月 HH時MM分」に整形された文字列
	 */
	public static String convertTimestamp2(String timeStr){
		String[] tmpStr = timeStr.split(" ");
		//yyyy-MM-dd
		String[] ymd = tmpStr[0].split("-");
		//HH:mm:ss
		String[] hms = tmpStr[1].split(":");

		String stampStr = MessageFormat.format("{0}年{1}月{2}日 {3}時{4}分", ymd[0], ymd[1], ymd[2], hms[0], hms[1]);
		return stampStr;

	}

	/**
	 * 「yyyy-MM-dd HH:mm:ss」の文字列を、yyyy,MM,dd,HH,mm,ss.ssの配列に分解して返す
	 * @param timeStr
	 * @return yyyy,MM,dd,HH,mm,ss.ssの文字列配列
	 */
	public static String[] convertTimestamp3(String timeStr){
		String[] retStr = new String[6];

		String[] tmpStr = timeStr.split(" ");
		//yyyy-MM-dd
		String[] ymd = tmpStr[0].split("-");
		//HH:mm:ss
		String[] hms = tmpStr[1].split(":");

		//先頭が「0」の場合は「0」を削除した文字列を返す
		retStr[0] = ymd[0]; //とりあえず年はそのまま
		retStr[1] = ymd[1].substring(0,1).equals("0") ? ymd[1].substring(1) : ymd[1];
		retStr[2] = ymd[2].substring(0,1).equals("0") ? ymd[2].substring(1) : ymd[2];
		retStr[3] = hms[0].substring(0,1).equals("0") ? hms[0].substring(1) : hms[0];
		retStr[4] = hms[1].substring(0,1).equals("0") ? hms[1].substring(1) : hms[1];
		retStr[5] = hms[2].substring(0,1).equals("0") ? hms[2].substring(1) : hms[2];

		return retStr;

	}

	public static String[] convertTimestamp4(Date date) {
		Locale.setDefault(new Locale("ja","JP","JP"));
		SimpleDateFormat sdf = new SimpleDateFormat("GGGGyyyy/M/d/H/m/");
		String stime = sdf.format(date);
		String[] sdate = stime.split("/");
		Locale.setDefault(Locale.JAPAN);

		return sdate;
	}

	//全角数字と漢数字変換
	public static String convertJ2Single(String str){
		//全角数字を半角へ
		str = str.replaceAll("０", "0");
		str = str.replaceAll("１", "1");
		str = str.replaceAll("２", "2");
		str = str.replaceAll("３", "3");
		str = str.replaceAll("４", "4");
		str = str.replaceAll("５", "5");
		str = str.replaceAll("６", "6");
		str = str.replaceAll("７", "7");
		str = str.replaceAll("８", "8");
		str = str.replaceAll("９", "9");
		//漢数字を半角へ、とりあえず31まで
		//正規表現なので長い文字列から順次置換
		str = str.replaceAll("二十一", "21");
		str = str.replaceAll("二十二", "22");
		str = str.replaceAll("二十三", "23");
		str = str.replaceAll("二十四", "24");
		str = str.replaceAll("二十五", "25");
		str = str.replaceAll("二十六", "26");
		str = str.replaceAll("二十七", "27");
		str = str.replaceAll("二十八", "28");
		str = str.replaceAll("二十九", "29");
		str = str.replaceAll("三十一", "31");
		str = str.replaceAll("十一", "11");
		str = str.replaceAll("十二", "12");
		str = str.replaceAll("十三", "13");
		str = str.replaceAll("十四", "14");
		str = str.replaceAll("十五", "15");
		str = str.replaceAll("十六", "16");
		str = str.replaceAll("十七", "17");
		str = str.replaceAll("十八", "18");
		str = str.replaceAll("十九", "19");
		str = str.replaceAll("二十", "20");
		str = str.replaceAll("三十", "30");
		str = str.replaceAll("〇", "0");
		str = str.replaceAll("一", "1");
		str = str.replaceAll("二", "2");
		str = str.replaceAll("三", "3");
		str = str.replaceAll("四", "4");
		str = str.replaceAll("五", "5");
		str = str.replaceAll("六", "6");
		str = str.replaceAll("七", "7");
		str = str.replaceAll("八", "8");
		str = str.replaceAll("九", "9");
		str = str.replaceAll("十", "10");
		return str;
	}

	/**
	 * Dateオブジェクトから和暦の「年号」「年」を取得する.
	 * @param date
	 * @return dateがnullの場合は null を返す.
	 */
	public static String[] getWareki(Date date) {
		if(date == null)	return null;
		Locale locale = new Locale("ja", "JP", "JP");
		String[] nen2 = new String[2];
		nen2[0] = new SimpleDateFormat("GGGG", locale).format(date);	// 年号
		nen2[1] = new SimpleDateFormat("yyyy", locale).format(date);	// 年(yが４個で”元年”が使えるらしいです)
		Locale.setDefault(Locale.JAPAN);
		return nen2;
	}

	// 和暦からDateオブジェクトの作成
	// ※nengou_yearは、年号＋年の文字列。(例："平成24")
	// ※month(月)は、1～12
	// ※hourは、24時間表記
	public static Date formatWareki(String nengou_year, String month, String day, String hour, String minute, String second) {
		month = StringUtil.isNotEmpty(month) ? month : "1";
		day = StringUtil.isNotEmpty(day) ? day : "1";
		hour = StringUtil.isNotEmpty(hour) ? hour : "0";
		minute = StringUtil.isNotEmpty(minute) ? minute : "0";
		second = StringUtil.isNotEmpty(second) ? second : "0";
		int mon = Integer.parseInt(month);
		int d = Integer.parseInt(day);
		int h = Integer.parseInt(hour);
		int m = Integer.parseInt(minute);
		int s = Integer.parseInt(second);
		return formatWareki(nengou_year, mon, d, h, m, s);
	}
	public static Date formatWareki(String nengou_year, int month, int day, int hour, int minute, int second) {
		Locale locale = new Locale("ja", "JP", "JP");
		//TODO:最終日チェックが必要 2月31日 とか
		String source = String.format("%1$s-%2$02d-%3$02d-%4$02d-%5$02d-%6$02d", nengou_year, month, day, hour, minute, second);
		SimpleDateFormat format = new SimpleDateFormat("GGGGyyyy-MM-dd-HH-mm-ss", locale);
		Date date = null;
		try {
			date = format.parse(source);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			date = null;
		}
		return date;
	}
	public static Date formatWareki(String nengou, String year, int month, int day, int hour, int minute, int second) {
		return formatWareki(nengou+year, month, day, hour, minute, second);
	}

	/**
	 * XMLGregorianCalendarをDateで取得します.
	 * @param obj
	 * @return Date
	 */
	public static Date toDate(Object obj) {
		Date date = null;
		/*
		if(obj instanceof BeanWrapper) {
			BeanWrapper beanWrapper = (BeanWrapper) obj;
			Object bean = SAStrutsUtil.getBean(beanWrapper);
			return toDate(bean);
		}
		*/
		if(obj instanceof XMLGregorianCalendar) {
			XMLGregorianCalendar xmlGregorianCalendar = (XMLGregorianCalendar) obj;
			GregorianCalendar gregorianCalendar = xmlGregorianCalendar.toGregorianCalendar();
			date = gregorianCalendar.getTime();
		}
		if (obj instanceof String) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			try {
				date = sdf.parse((String)obj);
			} catch (ParseException e) {}
			if (date == null) {
				sdf = new SimpleDateFormat("yyyy/MM/dd");
				try {
					date = sdf.parse((String)obj);
				} catch (ParseException e) {}
			}
			if (date == null) {
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					date = sdf.parse((String)obj);
				} catch (ParseException e) {}
			}
		}
		return date;
	}
	/**
	 * XMLGregorianCalendarをDateで取得します.
	 * @param obj
	 * @return Date
	 */
	public static Date toDateTime(Object obj) {
		Date date = null;
		/*
		if(obj instanceof BeanWrapper) {
			BeanWrapper beanWrapper = (BeanWrapper) obj;
			Object bean = SAStrutsUtil.getBean(beanWrapper);
			return toDate(bean);
		}
		*/
		if(obj instanceof XMLGregorianCalendar) {
			XMLGregorianCalendar xmlGregorianCalendar = (XMLGregorianCalendar) obj;
			GregorianCalendar gregorianCalendar = xmlGregorianCalendar.toGregorianCalendar();
			date = gregorianCalendar.getTime();
		}
		if (obj instanceof String) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			try {
				date = sdf.parse((String)obj);
			} catch (ParseException e) {}

			XmlFileEditHelper.outputLog("toDateTime date1=" + date);

			if (date == null) {
				sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				try {
					date = sdf.parse((String)obj);
				} catch (ParseException e) {}
			}
			XmlFileEditHelper.outputLog("toDateTime date2=" + date);
			if (date == null) {
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					date = sdf.parse((String)obj);
				} catch (ParseException e) {}
			}
			XmlFileEditHelper.outputLog("toDateTime date3=" + date);
		}
		return date;
	}
	/**
	 * YYYYMMDD Dateで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateYYYYMMDD() {
		String str = "";
		Date date = null;
		date = new Date();
		date = new Date(date.getTime());
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sdf = new SimpleDateFormat("yyyyMMdd");
			try {
				str = sdf.format(date);
			} catch (Exception e) {}
		}
		return str;
	}
	/**
	 * YYYYMMDDHHMMSS Dateで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateYYYYMMDDHHMMSS() {
		String str = "";
		Date date = null;
		date = new Date();
		date = new Date(date.getTime());
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			try {
				str = sdf.format(date);
			} catch (Exception e) {}
		}
		return str;
	}
	/**
	 * 現在時刻をYYYY-MM-DD HH:MM:SS 文字列で取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateYYYYMMDDHHMMSS2() {
		String str = "";
		Date date = null;
		date = new Date();
		date = new Date(date.getTime());
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				str = sdf.format(date);
			} catch (Exception e) {}
		}
		return str;
	}
	/**
	 * 2010-01-25T16:15:00+09:00 Dateで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateXmlEditorYMDHMS(String strOrgDateTime) {
		String str = "";
		Date date = null;
		XmlFileEditHelper.outputLog("getCurDateXmlEditorYMDHMS  strOrgDateTime=" + strOrgDateTime);
		date = toDateTime(strOrgDateTime);
		{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss+09:00");
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.HOUR, -9);
				str = sdf.format(cal.getTime());

				XmlFileEditHelper.outputLog("getCurDateXmlEditorYMDHMS  str=" + str);
				str = str.replace(" ", "T");
			} catch (Exception e) {

			}
		}
		return str;
	}
	/**
	 * 2010-01-25T16:15:00+09:00 Dateで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getSaveDateXmlEditorYMDHMS(String strOrgDateTime) {
		String str = "";
		Date date = null;
		date = toDateTime(strOrgDateTime);
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.HOUR, 9);
				str = sdf.format(cal.getTime());
			} catch (Exception e) {

			}
		}
		return str;
	}
	/**
	 * TimeBaseで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateTimeDDDD() {
		iDateTimeBase++;
		if(iDateTimeBase > 10000)iDateTimeBase = 0;
		String str = "";
		str = String.format("%04d", iDateTimeBase);
		return str;
	}
	/**
	 * TimeBaseで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateTimeDDDDDDDD() {
		iDateTimeBase++;
		if(iDateTimeBase > 100000000)iDateTimeBase = 0;
		String str = "";
		str = String.format("%08d", iDateTimeBase);
		return str;
	}
	/**
	 * TimeBaseで取得します.
	 * @param obj
	 * @return Date
	 */
	public static String getCurDateTimeDDDD(int iOldBase) {
		iOldBase++;
		if(iOldBase > 10000)iOldBase = 0;
		String str = "";
		str = String.format("%04d", iOldBase);
		return str;
	}

	public static Integer age(String bdate) {
		Date date = toDate(bdate);
		if (date != null)
			return age(new Timestamp(date.getTime()));
		return null;
	}

	public static Integer age(Timestamp date) {
		// 誕生日の年月日を得るためCalendar型のインスタンス取得
		Calendar birth = Calendar.getInstance();
		birth.setTime(date);
		// 年齢計算日を得るためCalendar型のインスタンス取得
		Calendar today = Calendar.getInstance();
		today.setTime(new Timestamp(System.currentTimeMillis()));
		// 計算日の年と誕生日の年の差を算出
		int year = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		// ただし誕生月・日より年齢計算月日が前であれば年齢は1歳少ない
		if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
			year--;
		} else if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
			year--;
		}
		return year;
	}
}
