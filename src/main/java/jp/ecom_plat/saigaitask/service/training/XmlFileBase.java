/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.training;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Namespace;
import org.jdom2.xpath.XPathFactory;
import org.seasar.framework.util.FileUtil;

import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.util.StringUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class XmlFileBase {
	protected Logger logger = Logger.getLogger(XmlFileBase.class);

	/** サービスクラス */
	@Resource
	protected ResponseService responseService = null;
	/**
	 * ファイル管理クラス
	 */
	public XmlFileMgrService clsXmlFileMgrServiceObj = null;
	/**
	 * ファイルタイプ
	 */
	protected int iFileType = 0;
	/**
	 * 保存データファイル名
	 */
	protected String strSaveFileName = "";
	/**
	 * xml editor
	 */
	public static final Namespace NAMESPACE_JMX = Namespace.getNamespace("jmx", "http://xml.kishou.go.jp/jmaxml1/");
	public static final Namespace NAMESPACE_JMX_IB = Namespace.getNamespace("jmx_ib", "http://xml.kishou.go.jp/jmaxml1/informationBasis1/");
	public static final Namespace NAMESPACE_JMX_SEIS = Namespace.getNamespace("jmx_seis", "http://xml.kishou.go.jp/jmaxml1/body/seismology1/");
	public static final Namespace NAMESPACE_XSLS = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");

	/**
	 * 画面用中間データ
	 */
	public List<Map<String, String>> tableData;

	/**
	 * XSLT管理クラス
	 */
	protected static XPathFactory xFactory = XPathFactory.instance();
	/**
	 * XML管理クラス
	 */
	protected Document docXml;
	/**
	 * 画面からのデータ
	 */
	protected HttpServletRequest request;

	/**
	 * ファイルタイプを設定
	 * @param iType
	 */
	public void setFileType(int iType) {
		iFileType = iType;
	}
	/**
	 * Browserへ情報を戻る
	 * @param responseService
	 */
	public void setRP(ResponseService responseService) {
		this.responseService = responseService;
	}
	/**
	 * 保存ファイル名を取得
	 * @return
	 */
	public String getSaveFileName() {
		return strSaveFileName;
	}
	/**
	 * 一時ファイル名を取得する
	 * @return
	 */
	public String getFileNameBase() {
		// 20150827test.xml
		String str = TimeUtil.getCurDateYYYYMMDDHHMMSS() + "_" + TimeUtil.getCurDateTimeDDDD();
		return str;
	}
	/**
	 * GovermentIDを取得する
	 * @return
	 */
	public String getLocalGovID() {
		// 20150827test.xml
		String str = (String) request.getSession().getAttribute("JsonGetCurLocalGovID");
		return str;
	}

	/**
	 * 新規追加共通関数
	 * @param iPFileType
	 * @throws Exception
	 */
	void newFileCom(int iPFileType) throws Exception {
		// 最新ファイル名を構築する
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXml = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_PATH;
		String strFolder = this.clsXmlFileMgrServiceObj.getFolder(iPFileType);
		String strF = getFileNameBase();
		String strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/" + strF + ".xml.new";
		strTemp = StringUtil.replaceAll("//", "/", strTemp);

		String strTempF = contextPath + "/WEB-INF/" + strXml + "/" + strFolder;
		strTempF = StringUtil.replaceAll("//", "/", strTempF);
		File f = new File(strTempF);
		f.mkdirs();

		for( ; ; ) {

			File file = new File(strTemp);
			if(file.exists()) {
				strF = getFileNameBase();
				strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/" + strF + ".xml.new";
			} else {
				break;
			}
		}

		XmlFileEditHelper.outputLog("newFile strTemp=" + strTemp);
		/*
		<option value="1">地震速報</option>
		<option value="2">地震・震度に関する情報</option>
		<option value="3">津波警報・注意報</option>
		<option value="4">噴火警報・予報</option>
		<option value="5">気象警報・注意報</option>
		<option value="6">指定河川洪水予報</option>
		<option value="7">土砂災害警戒情報</option>
		<option value="8">記録的短時間大雨情報</option>
		<option value="9">竜巻注意報</option>
		 */
		String strTemplFile = "";

		XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
		@SuppressWarnings("unchecked")
		List<Map<String, String>> oType = (List<Map<String, String>>)this.request.getAttribute("MeteotypeMasterServiceExt");
		String strTypeFolder = ext.getTypeByID(""+iPFileType, oType);
		strTemplFile = contextPath + "/WEB-INF/" + strXml + "/" + "template" + "/" + strTypeFolder + "_template.xml";


		strTemplFile = StringUtil.replaceAll("//", "/", strTemplFile);
		// new file
		FileUtil.copy(new File(strTemplFile), new File(strTemp));

		strSaveFileName = strTemp;

		request.getSession().setAttribute("JsonGetCurFileNameOnlyNew", strF + ".xml.new");
	}

	/**
	 * ファイルのフォルダを取得
	 * @param iPFileType
	 * @return
	 */
	public String getFileNamePre(int iPFileType) {
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXml = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_PATH;
		String strFolder = this.clsXmlFileMgrServiceObj.getFolder(iPFileType);
		String strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/";
		strTemp = StringUtil.replaceAll("//", "/", strTemp);
		strSaveFileName = strTemp;
		return strSaveFileName;
	}
	/**
	 * ファイルのフォルダを取得
	 * @param iPFileType
	 * @return
	 */
	public String getXmlFilePath(int iPFileType) {
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXml = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_PATH;
		String strFolder = this.clsXmlFileMgrServiceObj.getFolder(iPFileType);
		String strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/";
		strTemp = StringUtil.replaceAll("//", "/", strTemp);
		return strTemp;
	}
	/**
	 * ファイルのTemplateを取得
	 * @param iPFileType
	 * @return
	 */
	public String getXmlTemplateFilePath(int iPFileType) {
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXml = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_PATH;

		String strTemplFile = "";
		XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
		@SuppressWarnings("unchecked")
		List<Map<String, String>> oType = (List<Map<String, String>>)this.request.getAttribute("MeteotypeMasterServiceExt");
		String strTypeFolder = ext.getTypeByID(""+iPFileType, oType);
		strTemplFile = contextPath + "/WEB-INF/" + strXml + "/" + "template" + "/" + strTypeFolder + "_template.xml";
		strTemplFile = StringUtil.replaceAll("//", "/", strTemplFile);
		return strTemplFile;
	}
	/**
	 * ファイルのTemplateを取得
	 * @param iPFileType
	 * @return
	 */
	public String getXmlXsltFilePath(int iPFileType) {
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXsltEdit = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_XSLTEDITPATH;


		String strTemplFile = "";
		XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
		@SuppressWarnings("unchecked")
		List<Map<String, String>> oType = (List<Map<String, String>>)this.request.getAttribute("MeteotypeMasterServiceExt");
		String strTypeFolder = ext.getTypeByID(""+iPFileType, oType);
		strTemplFile = contextPath + "/WEB-INF/" + strXsltEdit + "/" + strTypeFolder + "_edit.xsl";

		strTemplFile = StringUtil.replaceAll("//", "/", strTemplFile);
		return strTemplFile;
	}
	/**
	 * ファイルのSaveTemplateを取得
	 * @param iPFileType
	 * @return
	 */
	public String getXmlXsltSaveFilePath(int iPFileType) {
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXsltEdit = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_XSLTEDITPATH;


		String strTemplFile = "";
		XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
		@SuppressWarnings("unchecked")
		List<Map<String, String>> oType = (List<Map<String, String>>)this.request.getAttribute("MeteotypeMasterServiceExt");
		String strTypeFolder = ext.getTypeByID(""+iPFileType, oType);
		strTemplFile = contextPath + "/WEB-INF/" + strXsltEdit + "/" + strTypeFolder+ "_edit_save.xsl";

		strTemplFile = StringUtil.replaceAll("//", "/", strTemplFile);
		return strTemplFile;
	}
	/**
	 * JSONの配列を取得する
	 * @param jsonIn
	 * @param strKey
	 * @return
	 */
	public JSONArray getJsonArray(JSONObject jsonIn, String strKey) {

		try {
			JSONArray jsonArray = jsonIn.getJSONArray(strKey);
			return jsonArray;
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * 新規追加
	 * @return
	 * @throws Exception
	 */
	abstract boolean newFile() throws Exception;
	/**
	 * 追加Buttonの処理
	 * @param strLineUID
	 * @param strFileName
	 * @return
	 * @throws Exception
	 */
	abstract boolean addButton(String strLineUID, String strFileName) throws Exception;
	/**
	 * 削除Buttonの処理
	 * @param strLineUID
	 * @param strFileName
	 * @return
	 * @throws Exception
	 */
	abstract boolean delButton(String strLineUID, String strFileName) throws Exception;
	/**
	 * ComboboxChangeの処理
	 * @param strLineUID
	 * @param strFileName
	 * @return
	 * @throws Exception
	 */
	abstract boolean comboboxChange(String strLineUID, String strFileName) throws Exception;
	/**
	 * XMLファイル保存処理
	 * @param jsonIn
	 * @return
	 * @throws Exception
	 */
	abstract boolean jsonDataToFile(JSONObject jsonIn, String strFileName, String strOrgFileName) throws Exception;
	/**
	 * XMLファイルの編集の読み込み
	 * @param jsonIn
	 * @return
	 * @throws Exception
	 */
	abstract boolean fileToJsonData(JSONObject jsonIn) throws Exception;
}
