/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom2.Element;
import org.jdom2.Namespace;

import jp.ecom_plat.saigaitask.util.StringUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * XMLファイル編集
 */
public class XmlFileTallBaseHelper {
	/**
	 *Commentを追加の情報
	 */
	public class TCommentInfo {
		public String strNameComment = "";
		public String strDataType = "";
		public String strDBUid = "";
		public String strGroupID = "";
		public String strLevelFlag = "";
		public String strAtrribFlag = "";
	}
	/**
	 *Groupデータ情報
	 */
	public class TGroupInfo {
		public String strComboboxCode = "";
		public String strComboboxName = "";
		public ArrayList<String> listAllGroup = null;
	}
	/**
	 * 入力したLevelの全子Levelを探す
	 * @param mapVar
	 * @param strParent
	 * @return
	 */
	public ArrayList<String> findChildren(Map<String, Integer> mapVar, String strParent) {

		ArrayList<String> listKey = new ArrayList<String>();

		// 1_label
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(strParent, listRet, "_");
		int iLevelNum = listRet.size();  //2,3,4...N
		for(int i = 0; i < mapVar.size(); i++) {
			String strCurKey = mapVar.keySet().toArray()[i].toString();

			listRet = new ArrayList<String>();
			StringUtil.getSplitArrayD(strCurKey, listRet, "_");
			int iLevelCurNum = listRet.size();
			if(iLevelCurNum > iLevelNum) {
				// child
				listKey.add(strCurKey);
			}
		}
		return listKey;
	}
	/**
	 * 入力したLevelの全同じLevelを探す
	 * @param mapVar
	 * @param strCur
	 * @return
	 */
	public ArrayList<String> findSameLevel(Map<String, Integer> mapVar, String strCur) {

		ArrayList<String> listKey = new ArrayList<String>();

		// 1_label
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(strCur, listRet, "_");
		int iLevelNum = listRet.size();  //2,3,4...N
		for(int i = 0; i < mapVar.size(); i++) {
			String strCurKey = mapVar.keySet().toArray()[i].toString();

			listRet = new ArrayList<String>();
			StringUtil.getSplitArrayD(strCurKey, listRet, "_");
			int iLevelCurNum = listRet.size();
			if(iLevelCurNum == iLevelNum) {
				// child
				listKey.add(strCurKey);
			}
		}
		return listKey;
	}
	/**
	 * 入力したLevelの全上のLevelFieldを探す
	 * @param mapVar
	 * @param strCur
	 * @return
	 */
	public ArrayList<String> findUpLevelFieldAll(Map<String, Integer> mapVarField, String strCur) {

		ArrayList<String> listKey = new ArrayList<String>();

		// 1_label
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(strCur, listRet, "_");
		int iLevelNum = listRet.size();  //2,3,4...N
		for(int i = 0; i < mapVarField.size(); i++) {
			String strCurKey = mapVarField.keySet().toArray()[i].toString();

			listRet = new ArrayList<String>();
			StringUtil.getSplitArrayD(strCurKey, listRet, "_");
			int iLevelCurNum = listRet.size();
			// uplevel
			if(iLevelCurNum < iLevelNum) {
				// child
				listKey.add(strCurKey);
			}
		}
		return listKey;
	}
	/**
	 * 入力したLevelの全上のLevelを探す
	 * @param mapVar
	 * @param strCur
	 * @return
	 */
	public ArrayList<String> findUpLevelAll(Map<String, Integer> mapVar, String strCur) {

		ArrayList<String> listKey = new ArrayList<String>();

		// 1_label
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(strCur, listRet, "_");
		int iLevelNum = listRet.size();  //2,3,4...N
		for(int i = 0; i < mapVar.size(); i++) {
			String strCurKey = mapVar.keySet().toArray()[i].toString();

			listRet = new ArrayList<String>();
			StringUtil.getSplitArrayD(strCurKey, listRet, "_");
			int iLevelCurNum = listRet.size();
			// uplevel
			if(iLevelCurNum < iLevelNum) {
				// child
				listKey.add(strCurKey);
			}
		}
		return listKey;
	}
	/**
	 * 入力したLevelの全子Levelを探す
	 * @param mapVar
	 * @param strParent
	 * @return
	 */
	public ArrayList<Map<String, String>> findChildrenAllInfo(
			String strParent,
			ArrayList<Map<String, String>> tableConfDataObj) {

		XmlFileEditHelper.outputLog("findChildrenAllInfo A strParent=" + strParent);

		ArrayList<Map<String, String>> listKey = new ArrayList<Map<String, String>>();

		// 1_label
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(strParent, listRet, "_");
		String strPreP = "";
		for(int k = 0; k < listRet.size()-1; k++) {
			strPreP += listRet.get(k) + "_";
		}

		XmlFileEditHelper.outputLog("findChildrenAllInfo strPreP=" + strPreP);

		int iLevelNum = listRet.size();  //2,3,4...N
		for(int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			String strCurKey = mapT.get(XmlFileEditHelper.strConstLineType);

			listRet = new ArrayList<String>();
			StringUtil.getSplitArrayD(strCurKey, listRet, "_");

			String strPre = "";
			for(int k = 0; k < listRet.size()-1; k++) {
				strPre += listRet.get(k) + "_";
			}

			XmlFileEditHelper.outputLog("findChildrenAllInfo strPre=" + strPre);

			int iLevelCurNum = listRet.size();

			XmlFileEditHelper.outputLog("findChildrenAllInfo iLevelCurNum=" + iLevelCurNum);
			XmlFileEditHelper.outputLog("findChildrenAllInfo iLevelNum=" + iLevelNum);

			XmlFileEditHelper.outputLog("findChildrenAllInfo B strCurKey=" + strCurKey);

			if(iLevelCurNum >= iLevelNum) {
				XmlFileEditHelper.outputLog("findChildrenAllInfo iLevelCurNum=OK");
				if(strPre.startsWith(strPreP)) {
					XmlFileEditHelper.outputLog("findChildrenAllInfo strPre=OK");
					// child
					listKey.add(mapT);
				}
			}
		}
		return listKey;
	}
	/**
	 * 入力したLevelの全子Levelの数を取得
	 * @param mapVar
	 * @param strParent
	 * @return
	 */
	public int findChildrenAllInfoCnt(
			String strParent,
			ArrayList<Map<String, String>> tableConfDataObj) {

		ArrayList<Map<String, String>> listKey = new ArrayList<Map<String, String>>();

		// 1_label
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(strParent, listRet, "_");
		String strPreP = "";
		for(int k = 0; k < listRet.size() -1; k++) {
			strPreP += listRet.get(k) + "_";
		}

		int iLevelNum = listRet.size();  //2,3,4...N
		for(int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			String strCurKey = mapT.get(XmlFileEditHelper.strConstLineType);

			listRet = new ArrayList<String>();
			StringUtil.getSplitArrayD(strCurKey, listRet, "_");

			String strPre = "";
			for(int k = 0; k < listRet.size()-1; k++) {
				strPre += listRet.get(k) + "_";
			}

			int iLevelCurNum = listRet.size();
			if(iLevelCurNum >= iLevelNum) {
				if(strPre.startsWith(strPreP)) {
					// child
					listKey.add(mapT);
				}
			}
		}
		int iSize = listKey.size();
		listKey.clear();
		return iSize;
	}

	/**
	 * コメント情報を解析
	 * @param lastComment
	 * @param strSplit
	 * @return
	 */
	public TCommentInfo getCommentInfo(String lastComment, String strSplit) {

		lastComment = lastComment.trim();

		TCommentInfo info = new TCommentInfo();
		ArrayList<String> listRet = new ArrayList<String>();
		StringUtil.getSplitArrayD(lastComment, listRet, strSplit);
		if(listRet.size() >= 1) {
			info.strNameComment = listRet.get(0).toString();
		}
		for(int i = 1; i < listRet.size(); i++)
		{
			String strTemp = listRet.get(i).toString();

			int iIndex = strTemp.indexOf("=");
			String strType = strTemp.substring(0, iIndex);
			XmlFileEditHelper.outputLog("getCommentInfo strType=" + strType);
			if(strType.toLowerCase().equals("type")) {
				strTemp = strTemp.substring(iIndex+1, strTemp.length());
				XmlFileEditHelper.outputLog("getCommentInfo strTemp=" + strTemp);
				if(strTemp.equals("inputstring")) {
					info.strDataType = "text";
				}
				if(strTemp.equals("datetime")) {
					info.strDataType = "datetime";
				}
				if(strTemp.equals("label")) {
					info.strDataType = "label";
				}
				if(strTemp.equals("combobox")) {
					info.strDataType = "combobox";
				}
				if(strTemp.equals("comboboxsave")) {
					info.strDataType = "comboboxsave";
				}
			}
			else if(strType.toLowerCase().equals("dbuid")) {
				strTemp = strTemp.substring(iIndex+1, strTemp.length());
				info.strDBUid = strTemp;
			}
			else if(strType.toLowerCase().equals("groupid")) {
				strTemp = strTemp.substring(iIndex+1, strTemp.length());
				info.strGroupID = strTemp;
			}
			else if(strType.toLowerCase().equals("levelflag")) {
				strTemp = strTemp.substring(iIndex+1, strTemp.length());
				info.strLevelFlag = strTemp;
			}
			else if(strType.toLowerCase().equals("attribflag")) {
				strTemp = strTemp.substring(iIndex+1, strTemp.length());
				info.strAtrribFlag = strTemp;
			}


		}


		return info;
	}

	/**
	 * Namespace情報を取得する
	 * @param elXslt
	 * @return
	 */
	List<Namespace> getXsltNameSpace(Element elXslt) {
		List<Namespace> listT = new ArrayList<Namespace>();

		XmlFileEditHelper.outputLog("parseNewXsl elXslt.getNamespacesInherited().size()=" + elXslt.getNamespacesInherited().size());
		XmlFileEditHelper.outputLog("parseNewXsl elXslt.getNamespacesInScope().size()=" + elXslt.getNamespacesInScope().size());
		XmlFileEditHelper.outputLog("parseNewXsl elXslt.getNamespacesIntroduced().size()=" + elXslt.getNamespacesIntroduced().size());
		XmlFileEditHelper.outputLog("parseNewXsl elXslt.getAdditionalNamespaces().size()=" + elXslt.getAdditionalNamespaces().size());

		HashMap<String,String> map = new HashMap<String,String>();

		for(int i = 0; i < elXslt.getNamespacesInherited().size(); i++) {
			Namespace n = elXslt.getNamespacesInherited().get(i);
			if(!map.containsKey(n.getURI())) {
				map.put(n.getURI(), "");
				XmlFileEditHelper.outputLog("getURI=" + n.getURI());
				listT.add(n);
			}
		}
		for(int i = 0; i < elXslt.getNamespacesInScope().size(); i++) {
			Namespace n = elXslt.getNamespacesInScope().get(i);
			if(!map.containsKey(n.getURI())) {
				map.put(n.getURI(), "");
				XmlFileEditHelper.outputLog("getURI=" + n.getURI());
				listT.add(n);
			}
		}
		for(int i = 0; i < elXslt.getNamespacesIntroduced().size(); i++) {
			Namespace n = elXslt.getNamespacesIntroduced().get(i);
			if(!map.containsKey(n.getURI())) {
				map.put(n.getURI(), "");
				XmlFileEditHelper.outputLog("getURI=" + n.getURI());
				listT.add(n);
			}
		}
		for(int i = 0; i < elXslt.getAdditionalNamespaces().size(); i++) {
			Namespace n = elXslt.getAdditionalNamespaces().get(i);
			if(!map.containsKey(n.getURI())) {
				map.put(n.getURI(), "");
				XmlFileEditHelper.outputLog("getURI=" + n.getURI());
				listT.add(n);
			}
		}

		return listT;
	}
	/**
	 * Groupデータの処理
	 * @param req
	 * @param tableData
	 * @param strGroupID
	 * @param strPLineUID
	 */
	String findGroupIDDataGetValueFromComboBox(HttpServletRequest req,String strDBUid, String strValue) {
		String strName = "";
		if(strDBUid != null && !strDBUid.isEmpty()) {
			@SuppressWarnings("unchecked")
			ArrayList<Map<String, String>> mapDBList = (ArrayList<Map<String, String>>)req.getAttribute(strDBUid);
			if(mapDBList != null) {
				for(int k = 0; k < mapDBList.size(); k++) {
					Map<String, String> mapTT = mapDBList.get(k);

					//XmlFileEditHelper.outputLog("findGroupIDData  i1=mapTT.get(key)=" +mapTT.get("key"));

					if(mapTT.get("key").equals(strValue)) {
						strName = mapTT.get("name");

						XmlFileEditHelper.outputLog("findGroupIDData  i1=strName=" +strName);

					}
				}
			}
		}
		return strName;
	}
	/**
	 * Groupデータの処理
	 * @param req
	 * @param tableData
	 * @param strGroupID
	 * @param strPLineUID
	 */
	void findGroupIDData(HttpServletRequest req, List<Map<String,String>> tableData,
			String strGroupID,
			String strPLineUID,
			List<Map<String,String>> tableDataConfForSave) {
		TGroupInfo info = new TGroupInfo();
		info.listAllGroup = new ArrayList<String>();

		XmlFileEditHelper.outputLog("findGroupIDData  i1=strGroupID=" +strGroupID);



		XmlFileEditHelper.outputLog("findGroupIDData  i2=" +0);

		XmlFileEditHelper  helper = new XmlFileEditHelper();

		for(int i = 0; i < tableData.size(); i++) {
			Map<String, String> mapT = tableData.get(i);
			String strGroupIDT = mapT.get(XmlFileEditHelper.strConstExt_Groupid);
			String strLineUID = mapT.get(XmlFileEditHelper.strConstLineUID);

			if(strGroupIDT == null)continue;
			if(strGroupIDT.isEmpty())continue;

			XmlFileEditHelper.outputLog("put key strGroupID=" +strGroupID);

			if(strGroupID.equals(strGroupIDT)) {
				//info.listAllGroup.add(strLineUID);
				String strLineCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);

				XmlFileEditHelper.outputLog("put key strLineCtrlType=" +strLineCtrlType);

				if(strLineCtrlType.equals("combobox")) {
					// code

					for(int j = 0; j < tableDataConfForSave.size(); j++) {
						Map<String, String> mapSaveT = tableDataConfForSave.get(j);
						String strGroupSaveIDT = mapSaveT.get(XmlFileEditHelper.strConstExt_Groupid);
						String strLineCtrlTypeT = mapSaveT.get(XmlFileEditHelper.strConstLineCtrlType);
						if(strGroupSaveIDT != null && strLineCtrlTypeT != null) {

							if(!strGroupID.isEmpty() && strGroupSaveIDT.equals(strGroupID) && strLineCtrlTypeT.equals("comboboxsave")) {

								XmlFileEditHelper.outputLog("put key strLineCtrlTypeT=" +strLineCtrlTypeT + " strGroupID=" + strGroupID + " strGroupSaveIDT=" + strGroupSaveIDT + " strGroupIDT=" + strGroupIDT + " ");

								String strDBUid = mapT.get(XmlFileEditHelper.strConstExt_DBUID);
								String strValue = mapT.get(XmlFileEditHelper.strConstValue);
								info.strComboboxName = findGroupIDDataGetValueFromComboBox(req, strDBUid, strValue);

								tableData.add(i,
										helper.xmltableDataDetailAddExt(
												mapSaveT.get(XmlFileEditHelper.strConstLevel),
												mapSaveT.get(XmlFileEditHelper.strConstName),
												info.strComboboxName,
												mapSaveT.get(XmlFileEditHelper.strConstButton),
												mapSaveT.get(XmlFileEditHelper.strConstLineType),
												mapSaveT.get(XmlFileEditHelper.strConstLineCtrlType),
												mapSaveT.get(XmlFileEditHelper.strConstExt_DBUID),
												mapSaveT.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag),
												mapSaveT.get(XmlFileEditHelper.strConstExt_Select),
												mapSaveT.get(XmlFileEditHelper.strConstExt_SelectForsave),
												mapSaveT.get(XmlFileEditHelper.strConstExt_Groupid),
												mapSaveT.get(XmlFileEditHelper.strConstExt_AttribFlag)
										)
										);
								i++;

							}

						}
					}


				}
				//if(strLineCtrlType.equals("comboboxsave")) {
				//	// name
				//	mapT.put(XmlFileEditHelper.strConstValue, info.strComboboxName);
				//	XmlFileEditHelper.outputLog("put key strName=" +info.strComboboxName);
				//}

			}
		}
		XmlFileEditHelper.outputLog("findGroupIDData  i3=" +0);
	}

	/**
	 * Groupデータの処理
	 * @param req
	 * @param tableData
	 */
	void createXmlDataPre(HttpServletRequest req, List<Map<String,String>> tableData,
			List<Map<String,String>> tableDataConfForSave) {

		HashMap<String, String> mapGroupID = new HashMap<String, String>();
		String strGroupID = "";
		String strLineUID = "";

		XmlFileEditHelper.outputLog("createXmlDataPre  1");

		ArrayList listAddComboboxSave = new ArrayList();
		ArrayList<String> listGroupID = new ArrayList<String>();
		ArrayList<String> listLineUID = new ArrayList<String>();

		// get all group id
		for(int i = 0; i < tableData.size(); i++) {

			Map<String, String> mapT = tableData.get(i);
			strLineUID = mapT.get(XmlFileEditHelper.strConstLineUID);
			strGroupID = mapT.get(XmlFileEditHelper.strConstExt_Groupid);

			// datetime の値の標準化
			String strValue = mapT.get(XmlFileEditHelper.strConstValue);
			String strCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);

			if(strCtrlType.toLowerCase().equals("datetime")) {
				//2010-01-25T16:15:00+09:00（日本標準時刻）
				//2010-01-25T16:15:00+Z（世界標準時）についてですが、日本標準時刻に統一します。
				strValue = TimeUtil.getCurDateXmlEditorYMDHMS(strValue);

				XmlFileEditHelper.outputLog("createXmlDataPre strValue=" + strValue);

				mapT.put(XmlFileEditHelper.strConstValue, strValue);
			}

			if(strGroupID == null)continue;
			if(strGroupID.isEmpty())continue;

			XmlFileEditHelper.outputLog("createXmlDataPre  i=" + i);

			if(mapGroupID.containsKey(strGroupID) == false) {
				//findGroupIDData(req, tableData, strGroupID, strLineUID, tableDataConfForSave);
				listGroupID.add(strGroupID);
				listLineUID.add(strLineUID);
				mapGroupID.put(strGroupID, strGroupID);
			}


			XmlFileEditHelper.outputLog("createXmlDataPre strCtrlType=" + strCtrlType);



		}
		for(int i = 0; i < listGroupID.size(); i++) {
			strGroupID = (String)listGroupID.get(i);
			strLineUID = (String)listLineUID.get(i);
			findGroupIDData(req, tableData, strGroupID, strLineUID, tableDataConfForSave);
		}

	}
}
