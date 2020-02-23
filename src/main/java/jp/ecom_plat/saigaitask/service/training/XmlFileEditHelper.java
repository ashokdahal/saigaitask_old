/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.training;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;



import jp.ecom_plat.saigaitask.util.StringUtil;

/**
 *ファイル編集補助クラス
 */
public class XmlFileEditHelper {
	protected Logger logger = Logger.getLogger(XmlFileEditHelper.class);
	protected static Logger loggerS = Logger.getLogger(XmlFileEditHelper.class);
	public final static String strConstLevel = "level";
	public final static String strConstName = "name";
	public final static String strConstValue = "value";
	public final static String strConstButton = "button";
	public final static String strConstLineUID = "lineuid";
	public final static String strConstLineType = "linetype";
	public final static String strConstLineCtrlType = "linectrltype";


	public final static String strConstExt_DBUID = "comboboxuid";
	public final static String strConstExt_ErrorFlag = "errflg";
	// 0 no,1 last
	public final static String strConstExt_CssLv1EndFlag = "csslv1endflag";
	// for save
	public final static String strConstExt_Select = "xsltselect";
	public final static String strConstExt_SelectForsave = "xsltselectforsave";
	public final static String strConstExt_Groupid = "groupid";
	public final static String strConstExt_AttribFlag = "attribflag";

	/**
	 * 画面データに情報を追加する
	 * @param strLevel
	 * @param name
	 * @param strValue
	 * @param button
	 * @param strType
	 * @param strCtrlType
	 * @return
	 */
	public Map<String, String> xmltableDataDetailAdd(
			Object strLevel, Object name, Object strValue, Object button, Object strType, Object strCtrlType) {
		Map<String, String> record = new HashMap<String, String>();

		record.put(strConstLevel, (String) strLevel);
		record.put(strConstName,  (String) name);
		record.put(strConstValue, (String) strValue);
		record.put(strConstButton, (String) button);

		String strUID = StringUtil.getUUID();

		record.put(strConstLineUID, (String) strUID);
		record.put(strConstLineType, (String) strType);
		record.put(strConstLineCtrlType, (String) strCtrlType);

		record.put(strConstExt_CssLv1EndFlag, (String) "0");
		return record;
	}
	/**
	 * 画面データに情報を追加する
	 * @param strLevel
	 * @param name
	 * @param strValue
	 * @param button
	 * @param strType
	 * @param strCtrlType
	 * @param strComboboxUID
	 * @param strCssendflag
	 * @param strSelect
	 * @param strSelectForsave
	 * @param strGroupid
	 * @return
	 */
	public Map<String, String> xmltableDataDetailAddExt(
			Object strLevel, Object name, Object strValue, Object button, Object strType, Object strCtrlType,
			Object strComboboxUID, Object strCssendflag,
			Object strSelect,
			Object strSelectForsave,
			Object strGroupid,
			Object strAttribFlag
			) {
		Map<String, String> record = new HashMap<String, String>();

		record.put(strConstLevel, (String) strLevel);
		record.put(strConstName,  (String) name);
		record.put(strConstValue, (String) strValue);
		record.put(strConstButton, (String) button);

		String strUID = StringUtil.getUUID();

		record.put(strConstLineUID, (String) strUID);
		record.put(strConstLineType, (String) strType);
		record.put(strConstLineCtrlType, (String) strCtrlType);
		record.put(strConstExt_DBUID, (String) strComboboxUID);

		record.put(strConstExt_CssLv1EndFlag, (String)strCssendflag);

		record.put(strConstExt_Select, (String)strSelect);
		record.put(strConstExt_SelectForsave, (String)strSelectForsave);
		record.put(strConstExt_Groupid, (String)strGroupid);
		record.put(strConstExt_AttribFlag, (String)strAttribFlag);

		XmlFileEditHelper.outputLog("xmltableDataDetailAddExt tableData strLevel　=" + strLevel);
		XmlFileEditHelper.outputLog("xmltableDataDetailAddExt tableData name　=" + name);
		XmlFileEditHelper.outputLog("xmltableDataDetailAddExt tableData strValue　=" + strValue);


		return record;
	}
	/**
	 * 展開データを設定する
	 * @param strComboboxUID
	 * @param record
	 */
	public void xmltableExtDataDetailSet(
			Object strComboboxUID, HashMap<String, String> record) {
		record.put(strConstExt_DBUID, (String) strComboboxUID);
		return;
	}

	/**
	 * 展開データを設定する
	 * @param strCssendflag
	 * @param record
	 */
	public void xmltableExtDataDetailSetCss(
			Object strCssendflag, HashMap<String, String> record) {
		record.put(strConstExt_CssLv1EndFlag, (String) strCssendflag);
		return;
	}

	/**
	 * 展開データを設定する
	 * @param strValue
	 * @param record
	 */
	public void xmltableExtDataDetailSetValue(
			Object strValue, HashMap<String, String> record) {
		record.put(strConstValue, (String) strValue);
		return;
	}
	/**
	 * 一時ファイルへ保存
	 * @param strFileName
	 * @param tableData
	 * @throws Exception
	 */
	public void writeXmlTempFile(String strFileName, List<Map<String,String>> tableData) throws Exception {
		if(strFileName.endsWith(".new")) {
			strFileName = strFileName.substring(0, strFileName.length() - 4);
		} else {

		}
		strFileName = strFileName + ".tmp";

		FileOutputStream fos = new FileOutputStream(strFileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(tableData);
		oos.flush();
		oos.close();
		fos.close();

		//DEBUG
		if(XmlFileEditHelper.isXmlEditorDebug() == true) {
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(strFileName+".txt"), "UTF-8"));


			for(int i = 0; i < tableData.size(); i++) {
				Map<String,String> mapT = tableData.get(i);
				StringBuilder bd = new StringBuilder();


				bd.append(mapT.get(XmlFileEditHelper.strConstLevel));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstName));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstValue));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstButton));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstLineType));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstLineCtrlType));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstExt_DBUID));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstExt_Select));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstExt_SelectForsave));bd.append(",");
				bd.append(mapT.get(XmlFileEditHelper.strConstExt_Groupid));bd.append(",");

				bufferedWriter.write(bd.toString());
				bufferedWriter.write("\n");
			}

			bufferedWriter.close();
		}
	}
	/**
	 * 一時ファイルから取る
	 * @param strFileName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> readXmlTempFile(String strFileName) throws Exception {

		if(strFileName.endsWith(".new")) {
			strFileName = strFileName.substring(0, strFileName.length() - 4);
		} else {

		}
		strFileName = strFileName + ".tmp";

		FileInputStream fis = new FileInputStream(strFileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		@SuppressWarnings("unchecked")
		List<Map<String,String>> tableData = (List<Map<String,String>>) ois.readObject();

		XmlFileEditHelper.outputLog("readXmlTempFile tableData size　=" + tableData.size());

		ois.close();
		fis.close();

		return tableData;
	}
	/**
	 * DBのデータを取得する
	 * @return
	 */
	public ArrayList<Map<String,String>> getComboboxTAll(String strDBUid, int iFileType) {
		ArrayList<Map<String, String>>  list = new ArrayList<Map<String, String>>();
		if(strDBUid.equals("dbareacode")) {
			//領域コード:領域名
			//エリア:エリアコード
			XmlFileDbHelper.Meteoseismicarea1MasterServiceExt ext = new XmlFileDbHelper().new Meteoseismicarea1MasterServiceExt();
			list = ext.getComboboxDataCodeName2();
		}
		else if(strDBUid.equals("dbmaxquake")) {
			//県最大震度
			/*
			Map<String, String> map = null;
			map = new HashMap<String, String>();map.put("key", "1");map.put("value", "1");list.add(map);
			map = new HashMap<String, String>();map.put("key", "2");map.put("value", "2");list.add(map);
			map = new HashMap<String, String>();map.put("key", "3");map.put("value", "3");list.add(map);
			map = new HashMap<String, String>();map.put("key", "4");map.put("value", "4");list.add(map);
			map = new HashMap<String, String>();map.put("key", "5-");map.put("value", "5-");list.add(map);
			map = new HashMap<String, String>();map.put("key", "5+");map.put("value", "5+");list.add(map);
			map = new HashMap<String, String>();map.put("key", "6-");map.put("value", "6-");list.add(map);
			map = new HashMap<String, String>();map.put("key", "6+");map.put("value", "6+");list.add(map);
			map = new HashMap<String, String>();map.put("key", "7");map.put("value", "7");list.add(map);
			*/
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);

		}
		else if(strDBUid.equals("dbprefcode")) {
			//県:県コード
			XmlFileDbHelper.LocalgovInfoServiceExt ext = new XmlFileDbHelper().new LocalgovInfoServiceExt();
			list = ext.getComboboxDataPrefCodeName();
		}
		else if(strDBUid.equals("dbcitycode")) {
			//市:市コード
			XmlFileDbHelper.LocalgovInfoServiceExt ext = new XmlFileDbHelper().new LocalgovInfoServiceExt();
			list = ext.getComboboxDataCityCodeName();
		}
		else if(strDBUid.equals("dbseismiccode")) {
			//市町村等コード 地震
			XmlFileDbHelper.MeteoareainformationcityMasterServiceExt ext = new XmlFileDbHelper().new MeteoareainformationcityMasterServiceExt();
			list = ext.getComboboxDataNameseismicCodeName();
		}
		else if(strDBUid.equals("dbkishoukeihoucode")) {
			//市町村等コード 気象
			XmlFileDbHelper.MeteoareainformationcityMasterServiceExt ext = new XmlFileDbHelper().new MeteoareainformationcityMasterServiceExt();
			list = ext.getComboboxDataKishoukeihouCodeName();
		}
		else if(strDBUid.equals("dbwarncode")) {
			//気象警報注意報
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
			/*
			Map<String, String> map = null;
			map = new HashMap<String, String>();map.put("key", "00");map.put("value", "00:解除");list.add(map);
			map = new HashMap<String, String>();map.put("key", "02");map.put("value", "02:暴風雪警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "03");map.put("value", "03:大雨警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "04");map.put("value", "04:洪水警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "05");map.put("value", "05:暴風警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "06");map.put("value", "06:大雪警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "07");map.put("value", "07:波浪警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "08");map.put("value", "08:高潮警報");list.add(map);

			map = new HashMap<String, String>();map.put("key", "10");map.put("value", "10:大雨注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "12");map.put("value", "12:大雪注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "13");map.put("value", "13:風雪注意報");list.add(map);

			map = new HashMap<String, String>();map.put("key", "14");map.put("value", "14:雷注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "15");map.put("value", "15:強風注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "16");map.put("value", "16:波浪注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "17");map.put("value", "17:融雪注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "18");map.put("value", "18:洪水注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "19");map.put("value", "19:高潮注意報");list.add(map);


			map = new HashMap<String, String>();map.put("key", "20");map.put("value", "20:濃霧注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "21");map.put("value", "21:乾燥注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "22");map.put("value", "22:なだれ注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "23");map.put("value", "23:低温注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "24");map.put("value", "24:霜注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "25");map.put("value", "25:着氷注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "26");map.put("value", "26:着雪注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "27");map.put("value", "27:その他の注意報");list.add(map);

			map = new HashMap<String, String>();map.put("key", "32");map.put("value", "32:暴風雪特別警報（Control/Title=気象警報・注意報の場合には出現しない");list.add(map);
			map = new HashMap<String, String>();map.put("key", "33");map.put("value", "33:大雨特別警報（Control/Title=気象警報・注意報の場合には出現しない");list.add(map);
			map = new HashMap<String, String>();map.put("key", "35");map.put("value", "35:暴風特別警報（Control/Title=気象警報・注意報の場合には出現しない");list.add(map);
			map = new HashMap<String, String>();map.put("key", "36");map.put("value", "36:大雪特別警報（Control/Title=気象警報・注意報の場合には出現しない");list.add(map);
			map = new HashMap<String, String>();map.put("key", "37");map.put("value", "37:波浪特別警報（Control/Title=気象警報・注意報の場合には出現しない");list.add(map);
			map = new HashMap<String, String>();map.put("key", "38");map.put("value", "38:高潮特別警報（Control/Title=気象警報・注意報の場合には出現しない");list.add(map);
			*/

		}else if(strDBUid.equals("dbtatsunamiareacode")) {
			//津波エリア情報コード
			XmlFileDbHelper.MeteotsunamiareaMasterServiceExt ext = new XmlFileDbHelper().new MeteotsunamiareaMasterServiceExt();
			list = ext.getComboboxDaTatsunamiAreaCodeName();
		}else if(strDBUid.equals("dbcategorykeihoucode")) {
			//津波警報注意報コード
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
			/*
			Map<String, String> map = null;
			map = new HashMap<String, String>();map.put("key", "00");map.put("value", "00:津波なし");list.add(map);
			map = new HashMap<String, String>();map.put("key", "50");map.put("value", "50:警報解除");list.add(map);
			map = new HashMap<String, String>();map.put("key", "51");map.put("value", "51:津波警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "52");map.put("value", "52:大津波警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "53");map.put("value", "53:大津波警報：発表");list.add(map);
			map = new HashMap<String, String>();map.put("key", "60");map.put("value", "60:津波注意報解除");list.add(map);
			map = new HashMap<String, String>();map.put("key", "62");map.put("value", "62:津波注意報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "71");map.put("value", "71:津波予報（若干の海面変動）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "72");map.put("value", "72:津波予報（若干の海面変動）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "73");map.put("value", "73:津波予報（若干の海面変動）");list.add(map);
			*/
		} else if(strDBUid.equals("dbdosyasaigaiwarncode")) {
			//土砂災害警報情報 警報コード
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
			/*
			Map<String, String> map = null;
			map = new HashMap<String, String>();map.put("key", "0");map.put("value", "0:なし");list.add(map);
			map = new HashMap<String, String>();map.put("key", "1");map.put("value", "1:解除");list.add(map);
			map = new HashMap<String, String>();map.put("key", "3");map.put("value", "3:警戒 ");list.add(map);
			*/
		}
		else if(strDBUid.equals("dbdosyasaigaiareacode")) {
			//市町村等コード 土砂災害警報
			XmlFileDbHelper.MeteoareainformationcityMasterServiceExt ext = new XmlFileDbHelper().new MeteoareainformationcityMasterServiceExt();
			list = ext.getComboboxDataDosyasaigaiCodeName();
		}
		else if(strDBUid.equals("dbvolcanoareacode")) {
			//市町村等コード 火山情報
			XmlFileDbHelper.MeteoareainformationcityMasterServiceExt ext = new XmlFileDbHelper().new MeteoareainformationcityMasterServiceExt();
			list = ext.getComboboxDataVolcanoAreaCodeName();
		}
		else if(strDBUid.equals("dbvolcanotypecode")) {
			//火山情報 VolcanicWarning コード表
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
			/*
			Map<String, String> map = null;
			map = new HashMap<String, String>();map.put("key", "01");map.put("value", map.get("key")+":"+"噴火警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "02");map.put("value", map.get("key")+":"+"火口周辺警報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "03");map.put("value", map.get("key")+":"+"噴火警報（周辺海域）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "04");map.put("value", map.get("key")+":"+"噴火予報：警報解除");list.add(map);

			map = new HashMap<String, String>();map.put("key", "05");map.put("value", map.get("key")+":"+"噴火予報");list.add(map);
			map = new HashMap<String, String>();map.put("key", "06");map.put("value", map.get("key")+":"+"降灰予報（定時）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "07");map.put("value", map.get("key")+":"+"降灰予報（速報）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "08");map.put("value", map.get("key")+":"+"降灰予報（詳細）");list.add(map);

			map = new HashMap<String, String>();map.put("key", "11");map.put("value", map.get("key")+":"+"レベル１（平常）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "12");map.put("value", map.get("key")+":"+"レベル２（火口周辺規制）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "13");map.put("value", map.get("key")+":"+"レベル３（入山規制）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "14");map.put("value", map.get("key")+":"+"レベル４（避難準備）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "15");map.put("value", map.get("key")+":"+"レベル５（避難）");list.add(map);

			map = new HashMap<String, String>();map.put("key", "21");map.put("value", map.get("key")+":"+"平常");list.add(map);
			map = new HashMap<String, String>();map.put("key", "22");map.put("value", map.get("key")+":"+"火口周辺危険");list.add(map);
			map = new HashMap<String, String>();map.put("key", "23");map.put("value", map.get("key")+":"+"入山危険");list.add(map);
			map = new HashMap<String, String>();map.put("key", "24");map.put("value", map.get("key")+":"+"山麓厳重警戒");list.add(map);
			map = new HashMap<String, String>();map.put("key", "25");map.put("value", map.get("key")+":"+"居住地域厳重警戒");list.add(map);

			map = new HashMap<String, String>();map.put("key", "31");map.put("value", map.get("key")+":"+"海上警報（噴火警報）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "32");map.put("value", map.get("key")+":"+"海上警報（噴火警報解除）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "33");map.put("value", map.get("key")+":"+"海上予報（噴火予報）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "35");map.put("value", map.get("key")+":"+"平常（海底火山）");list.add(map);
			map = new HashMap<String, String>();map.put("key", "36");map.put("value", map.get("key")+":"+"周辺海域警戒");list.add(map);

			map = new HashMap<String, String>();map.put("key", "41");map.put("value", map.get("key")+":"+"噴火警報：避難等");list.add(map);
			map = new HashMap<String, String>();map.put("key", "42");map.put("value", map.get("key")+":"+"噴火警報：入山規制等");list.add(map);
			map = new HashMap<String, String>();map.put("key", "43");map.put("value", map.get("key")+":"+"火口周辺警報：入山規制等");list.add(map);
			map = new HashMap<String, String>();map.put("key", "44");map.put("value", map.get("key")+":"+"噴火警報（周辺海域）：周辺海域警戒");list.add(map);
			map = new HashMap<String, String>();map.put("key", "45");map.put("value", map.get("key")+":"+"平常");list.add(map);
			map = new HashMap<String, String>();map.put("key", "46");map.put("value", map.get("key")+":"+"噴火警報：当該居住地域厳重警戒");list.add(map);
			map = new HashMap<String, String>();map.put("key", "47");map.put("value", map.get("key")+":"+"噴火警報：当該山麓厳重警戒");list.add(map);
			map = new HashMap<String, String>();map.put("key", "48");map.put("value", map.get("key")+":"+"噴火警報：火口周辺警戒");list.add(map);
			map = new HashMap<String, String>();map.put("key", "49");map.put("value", map.get("key")+":"+"火口周辺警報：火口周辺警戒");list.add(map);

			map = new HashMap<String, String>();map.put("key", "51");map.put("value", map.get("key")+":"+"爆発");list.add(map);
			map = new HashMap<String, String>();map.put("key", "52");map.put("value", map.get("key")+":"+"噴火");list.add(map);
			map = new HashMap<String, String>();map.put("key", "53");map.put("value", map.get("key")+":"+"噴火開始");list.add(map);
			map = new HashMap<String, String>();map.put("key", "54");map.put("value", map.get("key")+":"+"連続噴火継続");list.add(map);
			map = new HashMap<String, String>();map.put("key", "55");map.put("value", map.get("key")+":"+"連続噴火停止");list.add(map);
			map = new HashMap<String, String>();map.put("key", "56");map.put("value", map.get("key")+":"+"噴火多発");list.add(map);


			map = new HashMap<String, String>();map.put("key", "61");map.put("value", map.get("key")+":"+"爆発したもよう");list.add(map);
			map = new HashMap<String, String>();map.put("key", "62");map.put("value", map.get("key")+":"+"噴火したもよう");list.add(map);
			map = new HashMap<String, String>();map.put("key", "63");map.put("value", map.get("key")+":"+"噴火開始したもよう");list.add(map);
			map = new HashMap<String, String>();map.put("key", "64");map.put("value", map.get("key")+":"+"連続噴火が継続しているもよう");list.add(map);
			map = new HashMap<String, String>();map.put("key", "65");map.put("value", map.get("key")+":"+"連続噴火は停止したもよう");list.add(map);

			map = new HashMap<String, String>();map.put("key", "70");map.put("value", map.get("key")+":"+"降灰");list.add(map);
			map = new HashMap<String, String>();map.put("key", "71");map.put("value", map.get("key")+":"+"少量の降灰");list.add(map);
			map = new HashMap<String, String>();map.put("key", "72");map.put("value", map.get("key")+":"+"やや多量の降灰");list.add(map);
			map = new HashMap<String, String>();map.put("key", "73");map.put("value", map.get("key")+":"+"多量の降灰");list.add(map);
			map = new HashMap<String, String>();map.put("key", "75");map.put("value", map.get("key")+":"+"小さな噴石の落下");list.add(map);

			map = new HashMap<String, String>();map.put("key", "91");map.put("value", map.get("key")+":"+"不明");list.add(map);
			map = new HashMap<String, String>();map.put("key", "99");map.put("value", map.get("key")+":"+"その他の現象");list.add(map);
			*/

		}
		else if(strDBUid.equals("dbkirokutekirainareacode")) {
			//記録的短時間大雨情報 雨量エリアマスタ
			XmlFileDbHelper.MeteorainareaMasterServiceExt ext = new XmlFileDbHelper().new MeteorainareaMasterServiceExt();
			list = ext.getComboboxDataRainAreaCodeName();
		}
		else if(strDBUid.equals("dbkirokutekiwarncode")) {
			//記録的短時間大雨情報 情報warnCode
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
		}
		else if(strDBUid.equals("dbtatsumakiwarncode")) {
			//竜巻注意情報 情報warnCode
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
		}
		else if(strDBUid.equals("dbtatsumakiareacode")) {
			//竜巻注意情報 エリアCode
			XmlFileDbHelper.MeteoareainformationcityMasterServiceExt ext = new XmlFileDbHelper().new MeteoareainformationcityMasterServiceExt();
			list = ext.getComboboxDataTatsumakiAreaCodeName();
		}
		else if(strDBUid.equals("dbsiteigawawarncode")) {
			//指定河川洪水予報 情報warnCode
			XmlFileDbHelper.MeteowarningcodeMasterServiceExt ext = new XmlFileDbHelper().new MeteowarningcodeMasterServiceExt();
			list = ext.getComboboxWarningCodeName(iFileType);
		}
		else if(strDBUid.equals("dbsiteigawaareacode")) {
			//指定河川洪水予報 エリアCode
			XmlFileDbHelper.MeteoriverMasterServiceExt ext = new XmlFileDbHelper().new MeteoriverMasterServiceExt();
			list = ext.getComboboxDataRiverAreaCodeName();
		}

		return list;
	}

	/**
	 * LOGを出力
	 */
	static public boolean isXmlEditorDebug() {
		return false;
	}
	/**
	 * LOGを出力
	 * @param str
	 */
	static public void outputLog(String str) {
		if(isXmlEditorDebug()) {
			loggerS.debug(str);
		}
	}
}
