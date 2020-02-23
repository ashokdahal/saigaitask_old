/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;

import jp.ecom_plat.saigaitask.util.StringUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * XMLファイル編集
 */
/**
 *
 */
public class XmlFileTAllBase extends XmlFileBase{

	/**
	 *編集情報
	 */
	public class T01EditInfo {
		public String strFileName = "";
	}

	int iCommentCnt = 0;
	String strXsltSplit = "";
	/**
	 *削除情報
	 */
	public class T01DelInfo {
		public String strFileName = "";
	}
	/**
	 *Buttonを追加の情報
	 */
	public class T01AddButtonInfo {
		public String strOldLineType = "";
	}


	/**
	 *Comboboxを関連の情報
	 */
	public class T01ComboboxMgrFieldInfo {
		public String strFirstComboboxUID = "";
		public String strFirstLineUID = "";
		public String strFirstComboboxValue = "";
		public String strMgrComboboxUID = "";
		public String strMgrLineUID = "";
		public String strMgrComboboxValue = "";
	}
	public T01EditInfo clsT01EditInfoObj = new T01EditInfo();
	public T01DelInfo clsT01DelInfoObj = new T01DelInfo();

	/**
	 * 表示のために
	 */
	public ArrayList<Map<String, String>> tableConfDataObj = null;
	/**
	 * 保存のために
	 */
	public ArrayList<Map<String, String>> tableConfDataForSaveObj = null;

	protected Document docXml;

	/* (非 Javadoc)
	 * @see jp.ecom_plat.saigaitask.service.training.XmlFileBase#newFile()
	 */
	@Override
	boolean newFile() throws Exception {

		newFileCom(this.iFileType);

		XmlFileEditHelper.outputLog("XmlFileTAllBase 1=");

		tableData = new ArrayList<Map<String, String>>();

		XmlFileEditHelper helper = new XmlFileEditHelper();

		readXslEditConfFile(iFileType);

		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();
		//tableConfDataObj.get(index)
		XmlFileEditHelper.outputLog("XmlFileTAllBase 2=" + tableConfDataObj.size());
		Map<String, Integer> mapVar = new HashMap<String, Integer>();
		for (int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			//Object strLevel, Object name, Object strValue, Object button,
			//Object strType, Object strCtrlType,
			//Object strComboboxUID, Object strCssendflag
			tableData.add(helper.xmltableDataDetailAddExt(
					mapT.get(XmlFileEditHelper.strConstLevel),
					mapT.get(XmlFileEditHelper.strConstName),
					mapT.get(XmlFileEditHelper.strConstValue),
					mapT.get(XmlFileEditHelper.strConstButton),
					mapT.get(XmlFileEditHelper.strConstLineType),
					mapT.get(XmlFileEditHelper.strConstLineCtrlType),
					mapT.get(XmlFileEditHelper.strConstExt_DBUID),
					mapT.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag),
					mapT.get(XmlFileEditHelper.strConstExt_Select),
					mapT.get(XmlFileEditHelper.strConstExt_SelectForsave),
					mapT.get(XmlFileEditHelper.strConstExt_Groupid),
					mapT.get(XmlFileEditHelper.strConstExt_AttribFlag)
					)

					);

		}
		XmlFileEditHelper.outputLog("XmlFileTAllBase 3=" + tableData);

		// 画面へ表示する
		setAttributeCnv(tableData);

		// 易い、迅速処理するために、データを保存する
		helper.writeXmlTempFile(strSaveFileName, tableData);
		XmlFileEditHelper.outputLog("XmlFileTAllBase 4=");
		return true;
	}



	/* (非 Javadoc)
	 * @see jp.ecom_plat.saigaitask.service.training.XmlFileBase#jsonDataToFile(net.sf.json.JSONObject)
	 */
	@Override
	boolean jsonDataToFile(JSONObject jsonIn, String strFileName, String strOrgFileName) throws Exception {
		readXslEditConfFile(iFileType);


		XmlFileEditHelper.outputLog("jsonDataToFile 1=strFileName=" +strFileName);
		XmlFileEditHelper.outputLog("jsonDataToFile 1=strOrgFileName=" +strOrgFileName);

		XmlFileEditHelper helper = new XmlFileEditHelper();

		String strXmlPath = getXmlFilePath(this.iFileType);

		String strOldTemp = strXmlPath + strOrgFileName;
		if(strOrgFileName.endsWith(".new")) {
			// new->save
			// 新規の場合、一時ファイル（.new、.tmpだけ存在する）
			XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strOrgFileName " + strOrgFileName);
			String strT = strOrgFileName.substring(0, strOrgFileName.length() - 4);
			XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strT " + strT);

			strOrgFileName = strT;
			XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strOrgFileName " + strOrgFileName);

			String strNewTemp = strXmlPath + strFileName;
			XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strOldTemp " + strOldTemp);
			XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strNewTemp " + strNewTemp);

			//FileUtil.copy(new File(strOldTemp), new File(strNewTemp));
		}


		String strTemp = strXmlPath + strOrgFileName;
		String strNewTemp = strXmlPath + strFileName;

		XmlFileEditHelper.outputLog("jsonDataToFile 2=read ok1 " + strTemp);

		tableData = helper.readXmlTempFile(strTemp);

		XmlFileEditHelper.outputLog("jsonDataToFile 2=read ok2 " + strTemp);
		XmlFileEditHelper.outputLog("jsonDataToFile 2=read ok3 " + strNewTemp);

		setTableValue(tableData, jsonIn);
		setAttributeCnv(tableData);

		XmlFileEditHelper.outputLog("jsonDataToFile 3=setAttribute ok");

		//画面データからT01EditInfoへ変換する
		//保存のためにデータを修正する
		createXmlData(tableData, strNewTemp);

		//データを回復する
		tableData = helper.readXmlTempFile(strTemp);
		setTableValue(tableData, jsonIn);
		setAttributeCnv(tableData);

		XmlFileEditHelper.outputLog("jsonDataToFile 3=createXmlData ok");


		XmlFileEditHelper.outputLog("jsonDataToFile 4 " + strNewTemp);

		// Start save
		// ファイル名はstrNewTempである
		createXml(strNewTemp, strNewTemp + ".in");
		// Save End

		helper.writeXmlTempFile(strNewTemp, tableData);

		return true;
	}

	/**
	 * JSONの情報を取得
	 * @param jsonIn
	 */
	void jsonDataToVar_ForEdit(JSONObject jsonIn) {
		clsT01EditInfoObj.strFileName = jsonIn.getString("xmlext_filename");
	}

	/* (非 Javadoc)
	 * @see jp.ecom_plat.saigaitask.service.training.XmlFileBase#fileToJsonData(net.sf.json.JSONObject)
	 */
	@Override
	boolean fileToJsonData(JSONObject jsonIn) throws Exception {
		readXslEditConfFile(iFileType);


		XmlFileEditHelper.outputLog("fileToJsonData-------------------1");

		jsonDataToVar_ForEdit(jsonIn);


		String strXml = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_PATH;
		String strXsltEdit = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLEDIT_XSLTEDITPATH;

		String contextPath = this.getFileNamePre(this.iFileType);
		String xmlFile = contextPath + clsT01EditInfoObj.strFileName;
		XmlFileEditHelper.outputLog("fileToJsonData-------------------1xml=" + clsT01EditInfoObj.strFileName);

		// Save start
		String strXsltTemplFile = this.getXmlXsltFilePath(this.iFileType);
		XmlFileEditHelper.outputLog("fileToJsonData-------------------2");

		SAXBuilder builder = new SAXBuilder();
		docXml = builder.build(new File(xmlFile));
		Document docXslt = builder.build(new File(strXsltTemplFile));

		Element elXsltRoot = docXslt.getRootElement();
		Element elTemplate = elXsltRoot.getChild("template", NAMESPACE_XSLS);


		tableData = new ArrayList<Map<String, String>>();

		parse(0, elTemplate, "");
		XmlFileEditHelper helper = new XmlFileEditHelper();
		tableData.add(helper.xmltableDataDetailAdd("-1", "", "", "", "fileeof", ""));

		String strFolder = this.clsXmlFileMgrServiceObj.getFolder(this.iFileType);
		contextPath = request.getSession().getServletContext().getRealPath("");
		String strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/" + clsT01EditInfoObj.strFileName;
		strTemp = StringUtil.replaceAll("//", "/", strTemp);
		checkButton(strTemp, tableData);

		XmlFileEditHelper.outputLog("fileToJsonData-------------------3 size=" + tableData.size());

		return false;
	}


	//select
	private String convertSelectStr(String select) {
		String strR = "";
		int iSplit = 0;
		for(int i  = 0; i < select.length(); i++)
		{
			char c = select.charAt(i);
			if(select.charAt(i) == '[') {
				iSplit = 1;
				continue;
			}
			if(select.charAt(i) == ']') {
				iSplit = 0;
				continue;
			}
			if(iSplit == 0) {
				strR += c;
			}

		}

		return strR;
	}

	/**
	 * XMLファイル読み込み
	 * @param level
	 * @param elXslt
	 * @param xpath
	 */
	private void parse(int level, Element elXslt, String xpath) {
		String lastComment = null;

		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();
		List<Namespace> namelist = baseHeler.getXsltNameSpace(elXslt);

		String ctrlTypeTEXT = "text";
		String ctrlTypeLABEL = "label";
		String ctrlTypeFIELD = "field";
		String ctrlTypeCBX = "combobox";
		String underbar = "_";

		String strLevel = null;
		String name = null;
		String strValue = null;
		String button = "1";
		String strType = null;
		String strCtrlType = null;

		XmlFileEditHelper helper = new XmlFileEditHelper();


		for (Content content : elXslt.getContent()) {

			if (content.getCType() == Content.CType.Comment) {
				Comment comment = (Comment) content;
				lastComment = comment.getText().trim();
			}
			else if (content.getCType() == Content.CType.Element) {
				Element element = (Element) content;
				String select = xpath + element.getAttributeValue("select");


				/*
				// UT
				if(element.getAttributeValue("select").startsWith("@")) {
					// attrib
					if(xpath.endsWith("/")) {
						xpath = xpath.substring(0, xpath.length() - 1);
					}
					select = xpath + element.getAttributeValue("select");
				}*/

				XmlFileEditHelper.outputLog("fileToJsonData-----Element start------org select=" + select);
				XPathExpression<Element> expr = xFactory.compile(select, Filters.element(), null, namelist);
				XPathExpression<Attribute> exprAttrib = xFactory.compile(select, Filters.attribute(), null, namelist);

				List<Element> elements = expr.evaluate(docXml);

				List<Attribute> elementAttribs = exprAttrib.evaluate(docXml);

				XmlFileEditHelper.outputLog("fileToJsonData-------------------org elements.size()=" + elements.size());
				XmlFileEditHelper.outputLog("fileToJsonData-------------------org elementAttribs.size()=" + elementAttribs.size());
				XmlFileEditHelper.outputLog("fileToJsonData-------------------element.getName()=" + element.getName());
				XmlFileEditHelper.outputLog("fileToJsonData-------------------element.getText()=" + element.getText());
				XmlFileEditHelper.outputLog("fileToJsonData-------------------element.getValue()=" + element.getValue());
				XmlFileEditHelper.outputLog("fileToJsonData-------------------element.getCType()=" + element.getCType());
				XmlFileEditHelper.outputLog("fileToJsonData-------------------element.getAttribute()=" + element.getAttribute("type"));
				XmlFileEditHelper.outputLog("fileToJsonData-------------------element.size()=" + element.getAttributes().size());



				if ("value-of".equals(element.getName())) {

					XmlFileEditHelper.outputLog("fileToJsonData-----value start------=======");
					if(elementAttribs.size() > 0) {
						for(int a = 0; a < elementAttribs.size(); a++) {
							String nameA = elementAttribs.get(a).getName();
							String valueA = elementAttribs.get(a).getValue();
							AttributeType valueTA = elementAttribs.get(a).getAttributeType();
							XmlFileEditHelper.outputLog("fileToJsonData-------------------nameA=" + nameA);
							XmlFileEditHelper.outputLog("fileToJsonData-------------------valueA=" + valueA);
							XmlFileEditHelper.outputLog("fileToJsonData-------------------valueTA=" + valueTA);

							strValue = valueA;

							for (int k = 0; k <= tableConfDataObj.size(); k++) {
								Map<String, String> mapT = tableConfDataObj.get(k);

								XmlFileEditHelper.outputLog("fileToJsonData----ATTRIB_strConstLineCtrlType=" + mapT.get(XmlFileEditHelper.strConstLineCtrlType));
								XmlFileEditHelper.outputLog("fileToJsonData----ATTRIB_strConstExt_Select=" + mapT.get(XmlFileEditHelper.strConstExt_Select));
								if(mapT.get(XmlFileEditHelper.strConstLineType).toLowerCase().equals("fileeof")) {
									break;
								}

								if(!mapT.get(XmlFileEditHelper.strConstLineCtrlType).toLowerCase().equals("label"))
								{
									if(mapT.get(XmlFileEditHelper.strConstExt_Select).equals(convertSelectStr(select))) {

										XmlFileEditHelper.outputLog("fileToJsonData--ATTRIB_strConstLevel=" + mapT.get(XmlFileEditHelper.strConstLevel));
										XmlFileEditHelper.outputLog("fileToJsonData--ATTRIB_strConstName=" + mapT.get(XmlFileEditHelper.strConstName));
										XmlFileEditHelper.outputLog("fileToJsonData--ATTRIB_strValue=" + strValue);


										tableData.add(
												helper.xmltableDataDetailAddExt(
														mapT.get(XmlFileEditHelper.strConstLevel),
														mapT.get(XmlFileEditHelper.strConstName),
														strValue,
														button,
														mapT.get(XmlFileEditHelper.strConstLineType),
														mapT.get(XmlFileEditHelper.strConstLineCtrlType),
														mapT.get(XmlFileEditHelper.strConstExt_DBUID),
														mapT.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag),
														mapT.get(XmlFileEditHelper.strConstExt_Select),
														mapT.get(XmlFileEditHelper.strConstExt_SelectForsave),
														mapT.get(XmlFileEditHelper.strConstExt_Groupid),
														mapT.get(XmlFileEditHelper.strConstExt_AttribFlag)
														));
										XmlFileEditHelper.outputLog("fileToJsonData--ATTRIB_strValue2=" + strValue);

									}
								}
							}

						}
					}
					if(elements.size() > 0) {

						String text = elements.get(0).getTextTrim();
						//String text = element.getTextTrim();

						//select = xpath + elements.get(w).getAttributeValue("select");

						strLevel = String.valueOf(level);
						name = lastComment;
						strValue = text;

						XmlFileEditHelper.outputLog("fileToJsonData-------------------strValue=" + text);
						XmlFileEditHelper.outputLog("fileToJsonData-------------------select A=" + element.getAttributeValue("select"));
						XmlFileEditHelper.outputLog("fileToJsonData-------------------select A=" + elements.get(0));
						XmlFileEditHelper.outputLog("fileToJsonData-------------------select A=" + elements.get(0).getCType());
						XmlFileEditHelper.outputLog("fileToJsonData-------------------select A=" + elements.get(0).getName());
						XmlFileEditHelper.outputLog("fileToJsonData-------------------select A=" + elements.get(0).getText());
						XmlFileEditHelper.outputLog("fileToJsonData-------------------select=" + select);
						XmlFileEditHelper.outputLog("fileToJsonData-------------------xpath=" + xpath);

						for (int k = 0; k <= tableConfDataObj.size(); k++) {
							Map<String, String> mapT = tableConfDataObj.get(k);

							XmlFileEditHelper.outputLog("fileToJsonData----strConstLineCtrlType=" + mapT.get(XmlFileEditHelper.strConstLineCtrlType));
							XmlFileEditHelper.outputLog("fileToJsonData----strConstExt_Select=" + mapT.get(XmlFileEditHelper.strConstExt_Select));
							if(mapT.get(XmlFileEditHelper.strConstLineType).toLowerCase().equals("fileeof")) {
								break;
							}

							if(!mapT.get(XmlFileEditHelper.strConstLineCtrlType).toLowerCase().equals("label"))
							{
								if(mapT.get(XmlFileEditHelper.strConstExt_Select).equals(convertSelectStr(select))) {

									XmlFileEditHelper.outputLog("fileToJsonData--strConstLevel=" + mapT.get(XmlFileEditHelper.strConstLevel));
									XmlFileEditHelper.outputLog("fileToJsonData--strConstName=" + mapT.get(XmlFileEditHelper.strConstName));
									XmlFileEditHelper.outputLog("fileToJsonData--strValue=" + strValue);

									{
										// datetime の値の標準化
										String strCtrlTypeTT = mapT.get(XmlFileEditHelper.strConstLineCtrlType);
										if(strCtrlTypeTT.toLowerCase().equals("datetime")) {
											//2010-01-25T16:15:00+09:00（日本標準時刻）
											//2010-01-25T16:15:00+Z（世界標準時）についてですが、日本標準時刻に統一します。
											strValue = StringUtil.replaceAll("T", " ", strValue);
											strValue = TimeUtil.getSaveDateXmlEditorYMDHMS(strValue);
										}

									}

									tableData.add(
											helper.xmltableDataDetailAddExt(
													mapT.get(XmlFileEditHelper.strConstLevel),
													mapT.get(XmlFileEditHelper.strConstName),
													strValue,
													button,
													mapT.get(XmlFileEditHelper.strConstLineType),
													mapT.get(XmlFileEditHelper.strConstLineCtrlType),
													mapT.get(XmlFileEditHelper.strConstExt_DBUID),
													mapT.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag),
													mapT.get(XmlFileEditHelper.strConstExt_Select),
													mapT.get(XmlFileEditHelper.strConstExt_SelectForsave),
													mapT.get(XmlFileEditHelper.strConstExt_Groupid),
													mapT.get(XmlFileEditHelper.strConstExt_AttribFlag)
													));
									XmlFileEditHelper.outputLog("fileToJsonData--strValue2=" + strValue);

								}
							}
						}
					}
					XmlFileEditHelper.outputLog("fileToJsonData-----value end------=======");

				} else if ("for-each".equals(element.getName())) {
					XmlFileEditHelper.outputLog("fileToJsonData-----for-each start------=======");
					select = xpath + element.getAttributeValue("select");

					strLevel = String.valueOf(level + 1);
					name = lastComment;
					strValue = null;
					strCtrlType = ctrlTypeLABEL;

					XmlFileEditHelper.outputLog("fileToJsonData-------------------LOOP select=" + select);

					for (int i = 1; i <= elements.size(); i++) {

						for (int k = 0; k <= tableConfDataObj.size(); k++) {
							Map<String, String> mapT = tableConfDataObj.get(k);

							XmlFileEditHelper.outputLog("fileToJsonData　LOOP----strConstLineCtrlType=" + mapT.get(XmlFileEditHelper.strConstLineCtrlType));
							XmlFileEditHelper.outputLog("fileToJsonData　LOOP----strConstExt_Select=" + mapT.get(XmlFileEditHelper.strConstExt_Select));
							if(mapT.get(XmlFileEditHelper.strConstLineType).toLowerCase().equals("fileeof")) {
								break;
							}


							if(mapT.get(XmlFileEditHelper.strConstLineCtrlType).toLowerCase().equals("label"))
							{
								if(mapT.get(XmlFileEditHelper.strConstExt_Select).equals(convertSelectStr(select))) {

									XmlFileEditHelper.outputLog("fileToJsonData　LOOP--strConstLevel=" + mapT.get(XmlFileEditHelper.strConstLevel));
									XmlFileEditHelper.outputLog("fileToJsonData　LOOP--strConstName=" + mapT.get(XmlFileEditHelper.strConstName));
									XmlFileEditHelper.outputLog("fileToJsonData　LOOP--strValue=" + strValue);


									tableData.add(
											helper.xmltableDataDetailAddExt(
													mapT.get(XmlFileEditHelper.strConstLevel),
													mapT.get(XmlFileEditHelper.strConstName),
													strValue,
													button,
													mapT.get(XmlFileEditHelper.strConstLineType),
													mapT.get(XmlFileEditHelper.strConstLineCtrlType),
													mapT.get(XmlFileEditHelper.strConstExt_DBUID),
													mapT.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag),
													mapT.get(XmlFileEditHelper.strConstExt_Select),
													mapT.get(XmlFileEditHelper.strConstExt_SelectForsave),
													mapT.get(XmlFileEditHelper.strConstExt_Groupid),
													mapT.get(XmlFileEditHelper.strConstExt_AttribFlag)
													));
									XmlFileEditHelper.outputLog("fileToJsonData LOOP--strValue2=" + strValue);

								}
							}
						}

						//parse(level + 1, element, select + "[" + i + "]/");
						parse(level + 1, element, select + "[" + i + "]/");
					}
					XmlFileEditHelper.outputLog("fileToJsonData-----for-each end------=======");
				}
			}
		}

	}


	/**
	 * XMLファイル作成
	 * @param tableData
	 * @param strNewTemp
	 * @throws Exception
	 */
	private void createXmlData(List<Map<String,String>> tableData, String strNewTemp) throws Exception{

		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();

		strNewTemp = strNewTemp + ".in";

		int iCurFieldCnt = 0;
		XmlFileEditHelper.outputLog("createXmlData  tableData.size()=" + tableData.size());

		baseHeler.createXmlDataPre(request, tableData, this.tableConfDataForSaveObj);

        Element tempElement = new Element("ReportRoot");
        Document doc = new Document(tempElement);


        Element lastLevelElement = null;
        int iLastLevel = -1;
        ArrayList lastLevelList = new ArrayList();

		for(int i = 0; i < tableData.size(); i++) {
			//editInfo.
			Map<String, String> mapT = tableData.get(i);
			String strLevel = mapT.get(XmlFileEditHelper.strConstLevel);
			String strLineType = mapT.get(XmlFileEditHelper.strConstLineType);
			String strLineCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);
			String strSelectForsave = mapT.get(XmlFileEditHelper.strConstExt_SelectForsave);
			String strValue = mapT.get(XmlFileEditHelper.strConstValue);

			XmlFileEditHelper.outputLog("createXmlData  ===================i =" + i);
			XmlFileEditHelper.outputLog("createXmlData  strSelectForsave =" + strSelectForsave);
			XmlFileEditHelper.outputLog("createXmlData  strValue =" + strValue);

			int iLevel = StringUtil.getInt(strLevel);

			if(strSelectForsave != null)
			{

				tempElement = new Element(strSelectForsave);

				XmlFileEditHelper.outputLog("createXmlData  strLineType = " + strLineType);
				XmlFileEditHelper.outputLog("createXmlData  strLineCtrlType = " + strLineCtrlType);
				XmlFileEditHelper.outputLog("createXmlData  iLastLevel = " + iLastLevel);
				XmlFileEditHelper.outputLog("createXmlData  iLevel = " + iLevel);
				XmlFileEditHelper.outputLog("createXmlData  tempElement = " + tempElement);
				XmlFileEditHelper.outputLog("createXmlData  lastLevelElement = " + lastLevelElement);



				if(iLevel == 0) {
					//strValue -> strValue + name
					if(strValue == null)strValue = "";
					tempElement.setText(strValue);
					doc.getRootElement().addContent(tempElement);
				}
				if(iLevel == 1 && strLineCtrlType.toLowerCase().equals("label")) {
					XmlFileEditHelper.outputLog("createXmlData  A1 ");
					// label for
					doc.getRootElement().addContent(tempElement);
					lastLevelList = new ArrayList();
					lastLevelList.add(tempElement);

				}
				else if(iLevel > 1 && strLineCtrlType.toLowerCase().equals("label")) {
					XmlFileEditHelper.outputLog("createXmlData  A2-4 ");
					// label for
					XmlFileEditHelper.outputLog("createXmlData  A2-4 size=" + lastLevelList.size());
					XmlFileEditHelper.outputLog("createXmlData  A2-4 iLevel - 2=" + (iLevel - 2));

					XmlFileEditHelper.outputLog("createXmlData  A2-4 iLevel - iLastLevel=" + (iLevel - iLastLevel));

					if(iLevel > iLastLevel) {
						lastLevelElement = (Element)lastLevelList.get(lastLevelList.size() - 1);
						lastLevelElement.addContent(tempElement);
						lastLevelList.add(tempElement);
						XmlFileEditHelper.outputLog("createXmlData  lastLevelList.size()A=" + (lastLevelList.size()));
					}
					else if(iLevel == iLastLevel)
					{
						if(lastLevelList.size() - 2 >= 0) {
							lastLevelElement = (Element)lastLevelList.get(lastLevelList.size() - 2);
							lastLevelElement.addContent(tempElement);
							lastLevelList.remove(lastLevelList.size() - 1);
							lastLevelList.add(tempElement);
						} else {
							doc.getRootElement().addContent(tempElement);
							lastLevelList = new ArrayList();
							lastLevelList.add(tempElement);
						}
						XmlFileEditHelper.outputLog("createXmlData  lastLevelList.size()B=" + (lastLevelList.size()));

					} else {
						// none noting to do
						XmlFileEditHelper.outputLog("createXmlData  lastLevelList.size()C=" + (lastLevelList.size()));
					}
				}

				if(!strLineCtrlType.toLowerCase().equals("label")) {
					XmlFileEditHelper.outputLog("createXmlData  other ");

					if(iLevel < iLastLevel) {
						if(lastLevelList.size() - 1 > 0) {
							lastLevelList.remove(lastLevelList.size() - 1);
						}
					}

					if(iLevel >= 1) {
						lastLevelElement = (Element)lastLevelList.get(iLevel - 1);

						if(lastLevelElement != null) {
							if(strValue == null)strValue = "";
							tempElement.setText(strValue);
							lastLevelElement.addContent(tempElement);
						}
					}
				}

				iLastLevel = iLevel;

				XmlFileEditHelper.outputLog("createXmlData  2_1_field end=" + i);
			}

		}

		//DEBUG
		if(XmlFileEditHelper.isXmlEditorDebug() == true)
		{
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(strNewTemp+".csv"), "UTF-8"));


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
		try {
	        XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());

	        XmlFileEditHelper.outputLog("createXml  start=5=3");
	        OutputStream st = Files.newOutputStream(Paths.get(strNewTemp),StandardOpenOption.CREATE_NEW);
	        xmlOut.output(doc, st);
	        st.close();

        } catch(Exception e) {
        	XmlFileEditHelper.outputLog("Exception e="+ e.getMessage());
        	XmlFileEditHelper.outputLog("Exception e="+ e.getStackTrace()[0].toString());
        	XmlFileEditHelper.outputLog("Exception e="+ e.getStackTrace()[1].toString());
        	XmlFileEditHelper.outputLog("Exception e="+ e.getStackTrace()[2].toString());
        	XmlFileEditHelper.outputLog("Exception e="+ e.getStackTrace()[3].toString());

        	try {
        		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
        		OutputStream st = Files.newOutputStream(Paths.get(strNewTemp),StandardOpenOption.TRUNCATE_EXISTING);
    	        xmlOut.output(doc, st);
    	        st.close();
        		XmlFileEditHelper.outputLog("createXml  start=6A end");
        		return;
        	} catch(Exception e2) {
        		throw e2;
        	}

        }
	}



	/**
	 * XMLファイル作成
	 * @param xmlFileName
	 * @param editInfo
	 * @throws IOException
	 */
	private void createXml(String xmlFileName,String xmlFileNameIn) throws Exception{

		// java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile dosyasaigaiKeikaiJouhou_edit_save.xsl
		// It will attempt to compile your XSL and display any syntax errors at that time.
		// It's great for troubleshooting things like this when you don't have a proper tool.

		XmlFileEditHelper.outputLog("createXml-------------------1");
		String xsltFile = getXmlXsltSaveFilePath(this.iFileType);
		XmlFileEditHelper.outputLog("createXml-------------------2");

		String src = xmlFileNameIn;
		String dest = xmlFileName;
		String xslt = xsltFile;

		/*
		try{

		     SAXBuilder statesaxBuilder = new SAXBuilder();

		      Document stateDocument = statesaxBuilder.build(xmlFileNameIn);

		      TransformerFactory factory = TransformerFactory.newInstance();

		      Transformer transformer = factory.newTransformer(new
    		  StreamSource(xsltFile));

		      JDOMSource source = new JDOMSource(stateDocument);

		      JDOMResult result = new JDOMResult();

		      transformer.transform(source, result);

		      //Document tranformedDocument = result.getDocument();



		}catch(Exception e){
			logger.error("createXml exception:");
			logger.error(e.getMessage(), e);

			throw e;
		}
		*/

		File src2 = new File(src);
		File dest2 = new File(dest);
		File xslt2 = new File (xslt);

		Source srcSource = new StreamSource(src2);
		Result destResult = new StreamResult(dest2);
		Source xsltSource = new StreamSource(xslt2);


		try{
			TransformerFactory factory = TransformerFactory.newInstance();

		    Transformer transformer = factory.newTransformer(new
		    		StreamSource(xsltFile));

		    transformer.transform(srcSource,destResult);


		}catch(Exception e){
			logger.error("createXml exception:");
			logger.error(e.getMessage(), e);
			logger.error(destResult.toString());
			throw e;
		}



        XmlFileEditHelper.outputLog("createXml  start=6B end");

//        xmlCopy2webdir(dest2);

        File file = new File(xmlFileNameIn);
        if(XmlFileEditHelper.isXmlEditorDebug() == true) {
        	// noting to do
        } else {
        	file.delete();
        	file.deleteOnExit();
        }

	}
	/**
	 * 追加、削除Buttonと追加だけの計算と設定
	 * @param strTemp
	 * @param tableData
	 * @throws Exception
	 */
	void checkButton(String strTemp, List<Map<String, String>> tableData) throws Exception {
		XmlFileEditHelper helper = new XmlFileEditHelper();
		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();
		//tableConfDataObj.get(index)

		Map<String, Integer> mapVar = new HashMap<String, Integer>();
		for (int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			String strType = mapT.get(XmlFileEditHelper.strConstLineType);
			String strCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);
			if(strCtrlType.equals("label")) {
				mapVar.put(strType, 0);
			}
		}
		for (int i = 0; i < tableData.size(); i++) {
			Map<String, String> mapT = tableData.get(i);
			String strType = mapT.get(XmlFileEditHelper.strConstLineType);
			String strLevel = mapT.get(XmlFileEditHelper.strConstLevel);
			{
				String strTypeT = mapT.get(XmlFileEditHelper.strConstExt_DBUID);
				if(strTypeT == null)strTypeT = "";
				XmlFileEditHelper.outputLog("checkButton strConstExt_ComboxUID size=" + strTypeT);
				XmlFileEditHelper.outputLog("checkButton strConstExt_ComboxUID i=" + i);
				XmlFileEditHelper.outputLog("checkButton strConstExt_ComboxUID type=" + mapT.get(XmlFileEditHelper.strConstLineCtrlType));
			}

			if(mapVar.containsKey(strType) == true) {
				//1_label
				if(mapVar.get(strType) >= 1) {
					mapT.put(XmlFileEditHelper.strConstButton, "2");
				}
				int iT = mapVar.get(strType)+1;
				mapVar.put(strType, iT);
				//全子Level
				ArrayList listKey = baseHeler.findChildren(mapVar, strType);
				for(int k = 0; k < listKey.size(); k++) {
					String strChild = (String)listKey.get(k);
					mapVar.put(strChild, 0);
				}
			}

		}


		setAttributeCnv(tableData);
		helper.writeXmlTempFile(strTemp, tableData);
	}

	/**
	 * 現在選択したButtonのしたのAreaの前に追加する（最後ない場合、最後に追加する）
	 * @param tableData
	 * @param strLineUID
	 * @param info
	 * @return
	 * @throws Exception
	 */
	String findAddPos(List<Map<String, String>> tableData, String strLineUID, T01AddButtonInfo info) throws Exception {
		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();
		//tableConfDataObj.get(index)

		Map<String, Integer> mapVar = new HashMap<String, Integer>();
		Map<String, Integer> mapVarField = new HashMap<String, Integer>();
		for (int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			String strType = mapT.get(XmlFileEditHelper.strConstLineType);
			String strCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);
			if(strCtrlType.equals("label")) {
				mapVar.put(strType, 0);
			}
			if(strType.endsWith("_field")) {
				mapVarField.put(strType, 0);
			}

		}

		String strNewLineUID = "";

		int iFindPos = -1;
		int iNewFindPos = -1;
		for (int i = 0; i < tableData.size(); i++) {
			Map<String, String> mapT = tableData.get(i);
			if (mapT.containsValue(strLineUID) == true) {
				iFindPos = i;
				String strOldLineType = mapT.get(XmlFileEditHelper.strConstLineType);

				info.strOldLineType = strOldLineType;

				for (int j = iFindPos+1; j < tableData.size(); j++) {
					mapT = tableData.get(j);
					String strLineType = mapT.get(XmlFileEditHelper.strConstLineType);
					strNewLineUID = mapT.get(XmlFileEditHelper.strConstLineUID);
					XmlFileEditHelper.outputLog("addButton 5 strLineType=" + strLineType);

					//現在選択したButtonのしたのAreaの前に追加する（最後ない場合、最後に追加する）
					if(mapVar.containsKey(strOldLineType) == true) {
						boolean bFind = false;
						//SameLevel
						ArrayList listKey = baseHeler.findSameLevel(mapVar, strOldLineType);
						for(int k = 0; k < listKey.size(); k++) {
							String strChild = (String)listKey.get(k);
							if (strLineType.equals(strChild)){
								bFind = true;
								break;
							}
						}
						if(bFind == false) {
							//UpAllLevel
							listKey = baseHeler.findUpLevelAll(mapVar, strOldLineType);
							for(int k = 0; k < listKey.size(); k++) {
								String strChild = (String)listKey.get(k);
								if (strLineType.equals(strChild)){
									bFind = true;
									break;
								}
							}
						}
						if(bFind == false) {
							//UpAllLevelField
							listKey = baseHeler.findUpLevelFieldAll(mapVarField, strOldLineType);
							for(int k = 0; k < listKey.size(); k++) {
								String strChild = (String)listKey.get(k);
								if (strLineType.equals(strChild)){
									bFind = true;
									break;
								}
							}
						}
						if(bFind == true) {
							break;
						}
					}

					//fileeof
					if (strLineType.equals("fileeof"))break;
				}

				break;

			}
		}

		return strNewLineUID;
	}
	/* (非 Javadoc)
	 * @see jp.ecom_plat.saigaitask.service.training.XmlFileBase#addButton(java.lang.String, java.lang.String)
	 */
	@Override
	boolean addButton(String strLineUID, String strFileName) throws Exception {
		XmlFileEditHelper.outputLog("addButton 0=");
		readXslEditConfFile(iFileType);

		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();
		//tableConfDataObj.get(index)

		Map<String, Integer> mapVar = new HashMap<String, Integer>();
		for (int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			String strType = mapT.get(XmlFileEditHelper.strConstLineType);
			String strCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);
			if(strCtrlType.equals("label")) {
				mapVar.put(strType, 0);
			}
		}
		XmlFileEditHelper.outputLog("addButton-------------------4 tableConfDataObj.size()　=" + tableConfDataObj.size());

		JSONObject jsonIn = this.clsXmlFileMgrServiceObj.getJsonInData();
		XmlFileEditHelper.outputLog("addButton 1="+ jsonIn.toString());


		String strXmlPath = getXmlFilePath(this.iFileType);
		String strTemp = strXmlPath + strFileName;

		XmlFileEditHelper.outputLog("addButton 3=");
		XmlFileEditHelper helper = new XmlFileEditHelper();

		// 新しい請求が来る時、tableDataがNULLです。
		// XmlFileEditHelper.outputLog("addButton-------------------4 tableData size=" +
		// tableData.size());

		tableData = new ArrayList<Map<String, String>>();

		XmlFileEditHelper.outputLog("addButton-------------------4 strTemp　=" + strTemp + ".tmp");

		tableData = helper.readXmlTempFile(strTemp);


		setTableValue(tableData, jsonIn);

		XmlFileEditHelper.outputLog("addButton-------------------4 tableData size=" + tableData.size());

		XmlFileEditHelper.outputLog("addButton 4 strLineUID=" + strLineUID);

		T01AddButtonInfo info = new T01AddButtonInfo();
		String strNewLineUID = findAddPos(tableData, strLineUID, info);

		int iFindPos = -1;
		for (int i = 0; i < tableData.size(); i++) {
			Map<String, String> mapT = tableData.get(i);

			XmlFileEditHelper.outputLog("addButton 4 strLineUIDCur " + i + " =" + mapT.get(XmlFileEditHelper.strConstLineUID));

			if (mapT.containsValue(strNewLineUID) == true) {
				// find or last
				iFindPos = i;

				XmlFileEditHelper.outputLog("addButton 5 iFindPos=" + iFindPos);

				String strLineType = info.strOldLineType;//mapT.get(XmlFileEditHelper.strConstLineType);

				XmlFileEditHelper.outputLog("addButton 5 strLineType=" + strLineType);

				int iTempPos = iFindPos;

				if(mapVar.containsKey(strLineType) == true) {
					ArrayList<Map<String, String>> mapTInfo = baseHeler.findChildrenAllInfo(strLineType, tableConfDataObj);

					XmlFileEditHelper.outputLog("addButton 5.1 mapTInfo.size()=" + mapTInfo.size());

					for(int k = 0; k < mapTInfo.size(); k++) {
						Map<String, String> mapConfTInfo = mapTInfo.get(k);
						//Object strLevel, Object name, Object strValue, Object button,
						//Object strType, Object strCtrlType,
						//Object strComboboxUID, Object strCssendflag

						XmlFileEditHelper.outputLog("addButton 5.1 L k=" + mapConfTInfo.get(XmlFileEditHelper.strConstLevel));
						XmlFileEditHelper.outputLog("addButton 5.1 N k=" + mapConfTInfo.get(XmlFileEditHelper.strConstName));
						XmlFileEditHelper.outputLog("addButton 5.1 V k=" + mapConfTInfo.get(XmlFileEditHelper.strConstValue));

						tableData.add(iTempPos++, helper.xmltableDataDetailAddExt(
								mapConfTInfo.get(XmlFileEditHelper.strConstLevel),
								mapConfTInfo.get(XmlFileEditHelper.strConstName),
								mapConfTInfo.get(XmlFileEditHelper.strConstValue),
								mapConfTInfo.get(XmlFileEditHelper.strConstButton),
								mapConfTInfo.get(XmlFileEditHelper.strConstLineType),
								mapConfTInfo.get(XmlFileEditHelper.strConstLineCtrlType),
								mapConfTInfo.get(XmlFileEditHelper.strConstExt_DBUID),
								mapConfTInfo.get(XmlFileEditHelper.strConstExt_CssLv1EndFlag),
								mapConfTInfo.get(XmlFileEditHelper.strConstExt_Select),
								mapConfTInfo.get(XmlFileEditHelper.strConstExt_SelectForsave),
								mapConfTInfo.get(XmlFileEditHelper.strConstExt_Groupid),
								mapConfTInfo.get(XmlFileEditHelper.strConstExt_AttribFlag)
								));

					}
				}


				setAttributeCnv(tableData);
				helper.writeXmlTempFile(strTemp, tableData);
				break;
			}
		}

		checkButton(strTemp, tableData);

		return false;
	}

	/* (非 Javadoc)
	 * @see jp.ecom_plat.saigaitask.service.training.XmlFileBase#delButton(java.lang.String, java.lang.String)
	 */
	@Override
	boolean delButton(String strLineUID, String strFileName) throws Exception {
		readXslEditConfFile(iFileType);

		XmlFileTallBaseHelper  baseHeler = new XmlFileTallBaseHelper();
		//tableConfDataObj.get(index)

		Map<String, Integer> mapVar = new HashMap<String, Integer>();
		for (int i = 0; i < tableConfDataObj.size(); i++) {
			Map<String, String> mapT = tableConfDataObj.get(i);
			String strType = mapT.get(XmlFileEditHelper.strConstLineType);
			String strCtrlType = mapT.get(XmlFileEditHelper.strConstLineCtrlType);
			if(strCtrlType.equals("label")) {
				mapVar.put(strType, 0);
			}
		}


		XmlFileEditHelper.outputLog("delButton 1=");

		JSONObject jsonIn = this.clsXmlFileMgrServiceObj.getJsonInData();

		String strXmlPath = getXmlFilePath(this.iFileType);
		String strTemp = strXmlPath + strFileName;

		XmlFileEditHelper.outputLog("delButton 3=");
		XmlFileEditHelper helper = new XmlFileEditHelper();

		// 新しい請求が来る時、tableDataがNULLです。
		// XmlFileEditHelper.outputLog("delButton-------------------4 tableData size=" +
		// tableData.size());

		tableData = new ArrayList<Map<String, String>>();

		XmlFileEditHelper.outputLog("delButton-------------------4 strTemp　=" + strTemp + ".tmp");

		tableData = helper.readXmlTempFile(strTemp);

		setTableValue(tableData, jsonIn);
		XmlFileEditHelper.outputLog("delButton-------------------4 tableData size=" + tableData.size());

		XmlFileEditHelper.outputLog("delButton 4 strLineUID=" + strLineUID);

		int iFindPos = -1;
		for (int i = 0; i < tableData.size(); i++) {
			Map<String, String> mapT = tableData.get(i);

			XmlFileEditHelper.outputLog("delButton 4 strLineUIDCur " + i + " =" + mapT.get(XmlFileEditHelper.strConstLineUID));
			if (mapT.containsValue(strLineUID) == true) {
				// find
				iFindPos = i;

				XmlFileEditHelper.outputLog("delButton 5 iFindPos=" + iFindPos);

				String strLineType = mapT.get(XmlFileEditHelper.strConstLineType);

				XmlFileEditHelper.outputLog("delButton 5 strLineType=" + strLineType);

				int iTempPos = iFindPos;

				// findChildrenAllInfoCnt
				if(mapVar.containsKey(strLineType) == true) {
					int iCnt = baseHeler.findChildrenAllInfoCnt(strLineType, tableConfDataObj);
					iTempPos = iTempPos + iCnt - 1;
					for (int d = 0; d < iCnt; d++) {
						tableData.remove(iTempPos--);
					}

				}

				setAttributeCnv(tableData);
				helper.writeXmlTempFile(strTemp, tableData);
				break;
			}
		}

		checkButton(strTemp, tableData);

		return false;
	}
	/**
	 * 画面から後ろのJAVAに保存する
	 * @param tableData
	 * @param jsonIn
	 * @throws Exception
	 */
	void setTableValue(List<Map<String, String>> tableData, JSONObject jsonIn) throws Exception {
		XmlFileEditHelper helper = new XmlFileEditHelper();

		JSONArray array = jsonIn.getJSONArray("dataarray");

		Map<String, Map<String, String>> mapTemp = new HashMap<String, Map<String, String>>();
		XmlFileEditHelper.outputLog("setTableValue 5 1=" + 1);
		for(int i = 0; i < tableData.size(); i++) {
			XmlFileEditHelper.outputLog("setTableValue 5 1 i=" + i);
			Map<String, String> mapT = tableData.get(i);
			mapTemp.put(mapT.get(XmlFileEditHelper.strConstLineUID), mapT);
		}
		for(int i = 0; i < array.size(); i++) {
			XmlFileEditHelper.outputLog("setTableValue 5 2 i=" + i);
			JSONObject obj = (JSONObject)array.get(i);
			String strUID = obj.getString("datauid");
			String strV = obj.getString("datavalue");
			XmlFileEditHelper.outputLog("setTableValue 5 3 i=" + i);
			Map<String, String> mapT = mapTemp.get(strUID);
			XmlFileEditHelper.outputLog("setTableValue 5 4 i=" + i);
			mapT.put(XmlFileEditHelper.strConstValue, strV);

			XmlFileEditHelper.outputLog("setTableValue 5 strUID=" + strV);
		}

	}



	/* (非 Javadoc)
	 * @see jp.ecom_plat.saigaitask.service.training.XmlFileBase#comboboxChange(java.lang.String, java.lang.String)
	 */
	@Override
	boolean comboboxChange(String strLineUID, String strFileName) throws Exception {
		//将来のためにのこす
		return false;
	}

	/**
	 * 画面データの最後を設定する
	 * @param strLevel
	 * @param name
	 * @param strValue
	 * @param button
	 * @param strType
	 * @param strCtrlType
	 * @param strDBType
	 * @return
	 */
	public Map<String, String> uTmltableDataDetailAdd(
			Object strLevel, Object name, Object strValue, Object button, Object strType, Object strCtrlType, Object strDBType) {
		Map<String, String> record = new HashMap<String, String>();

		record.put(XmlFileEditHelper.strConstLevel, (String) strLevel);
		record.put(XmlFileEditHelper.strConstName,  (String) name);
		record.put(XmlFileEditHelper.strConstValue, (String) strValue);
		record.put(XmlFileEditHelper.strConstButton, (String) button);

		String strUID = StringUtil.getUUID();

		record.put(XmlFileEditHelper.strConstLineUID, (String) strUID);
		record.put(XmlFileEditHelper.strConstLineType, (String) strType);
		record.put(XmlFileEditHelper.strConstLineCtrlType, (String) strCtrlType);
		record.put(XmlFileEditHelper.strConstExt_DBUID, (String) strDBType);


		return record;
	}



	/**
	 * XSLTファイルから配置内容を取得する
	 * @param iType
	 * @return
	 * @throws Exception
	 */
	ArrayList<Map<String, String>> readXslEditConfFile(int iType) throws Exception {
		//tableConfDataObj = uTReadXslEditConfFile();
		//ArrayList<Map<String, String>> tableConfData = new ArrayList<Map<String, String>>();
		//tableData.add(iTempPos++,
		//		helper.xmltableDataDetailAdd("2", "エリア最大震度", "", "1", "2_1_field", "combobox"));
		//helper.xmltableExtDataDetailSet("getComboboxT01_02_02B", (HashMap<String, String>) tableData.get(iTempPos - 1));
		//
		/*
		public final static String strConstLevel = "level";
		public final static String strConstName = "name";
		public final static String strConstValue = "value";
		public final static String strConstButton = "button";
		public final static String strConstLineUID = "lineuid";
		public final static String strConstLineType = "linetype";
		public final static String strConstLineCtrlType = "linectrltype";
		public final static String strConstExt_ComboxUID = "comboboxuid";
		// level must　必要
		// name must　必要
		// value empty　要らない
		// button 1 　要らない
		// lineuid 　必要
		// linetype　必要 1_label, 1_1_label, 1_1_1_label Loop数よって自動連番　labelはLoopの最終のTitleです
		//　　　　　　　　その中に
		　　　　　　　　　1_field,1_1_field, Loop数よって自動連番、Field数関係ありません　　
		// linectrltype　必要　type=“inputstring” type=“datetime”type=”combobox”、type=”labelLoop”
		// comboboxuid   必要  datatype="xxxxuid" 全XMLEidt唯一
		*/

		iCommentCnt = 0;
		strXsltSplit = "";
		String strXmlTemplFile = this.getXmlTemplateFilePath(this.iFileType);
		String strXsltTemplFile = this.getXmlXsltFilePath(this.iFileType);;

		XmlFileEditHelper.outputLog("parseNewXsl strXmlTemplFile=" + strXmlTemplFile);
		XmlFileEditHelper.outputLog("parseNewXsl strXsltTemplFile=" + strXsltTemplFile);


		tableConfDataObj = new ArrayList<Map<String, String>>();
		tableConfDataForSaveObj = new ArrayList<Map<String, String>>();
		readXsl(new File(strXmlTemplFile), strXsltTemplFile);

		for(int i = 0; i < tableConfDataForSaveObj.size(); i++) {
			Map<String, String> mapT = tableConfDataForSaveObj.get(i);

			if(mapT.get(XmlFileEditHelper.strConstExt_Select).toLowerCase().length() > 0) {

				String strAttribFlag = mapT.get(XmlFileEditHelper.strConstExt_AttribFlag);

				// xsltselectforsave
				String strSelect = mapT.get(XmlFileEditHelper.strConstExt_Select);
				ArrayList listRetT = new ArrayList();
				StringUtil.getSplitArrayD(strSelect, listRetT, "/");
				String strSelectForSave = "";
				for(int w = 0; w < listRetT.size(); w++) {
					String strCur = (String) listRetT.get(w);
					if(strCur.indexOf(":") == -1) {

					} else {
						ArrayList listRetTSec = new ArrayList();
						StringUtil.getSplitArrayD(strCur, listRetTSec, ":");
						if(listRetTSec.size() >= 2)
							strCur = (String) listRetTSec.get(1);
					}
					strCur = strCur.replace("@", "");
					if(w < listRetT.size() - 1) {
						strSelectForSave += strCur + "_";
					} else {
						strSelectForSave += strCur;
					}
				}

				if(strAttribFlag != null && !strAttribFlag.isEmpty()) {
					// attrib
					// 属性と普通のNodeのテキストの名前が同じ可能性があるので、後ろにTagを追加する
					strSelectForSave += "_SaigaiTaskUIDAttribFlag";
				} else {
					// value text
					// noting to do
				}

				XmlFileEditHelper.outputLog("parseNewXsl null=strSelectForSave=" + strSelectForSave);
				mapT.put(XmlFileEditHelper.strConstExt_SelectForsave, strSelectForSave);
			}

			if(mapT.get(XmlFileEditHelper.strConstLineCtrlType).toLowerCase().equals("comboboxsave")) {
				continue;
			}
			XmlFileEditHelper.outputLog("parseNewXsl null=A i" + mapT.get(XmlFileEditHelper.strConstLineType).toLowerCase());
			if(mapT.get(XmlFileEditHelper.strConstLineCtrlType).toLowerCase().equals("label")) {
				XmlFileEditHelper.outputLog("parseNewXsl null=i" + i);
				mapT.put(XmlFileEditHelper.strConstValue, null);
			}

			if(mapT.get(XmlFileEditHelper.strConstExt_DBUID).toLowerCase().length() > 0) {

				XmlFileEditHelper.outputLog("parseNewXsl XmlFileEditHelper.strConstExt_ComboxUID)=" + mapT.get(XmlFileEditHelper.strConstExt_DBUID));

				XmlFileEditHelper helper = new XmlFileEditHelper();
				ArrayList<Map<String,String>> listCombo = helper.getComboboxTAll(
						mapT.get(XmlFileEditHelper.strConstExt_DBUID).toLowerCase(),
						this.iFileType);
				request.setAttribute(mapT.get(XmlFileEditHelper.strConstExt_DBUID).toLowerCase(), listCombo);

			}


			tableConfDataObj.add(mapT);
		}

		tableConfDataObj.add(uTmltableDataDetailAdd("-1", "", "", "", "fileeof", "",""));
		tableConfDataForSaveObj.add(uTmltableDataDetailAdd("-1", "", "", "", "fileeof", "",""));

		return tableConfDataObj;
	}


	/**
	 * 画面へ保存する前に、変換する（現在保留）
	 * @param tableData
	 * @throws Exception
	 */
	private void setAttributeCnv(List<Map<String,String>> tableData) throws Exception{
		List<Map<String,String>> tableDataTemp = new ArrayList<Map<String,String>>();
		/*
		//将来処理がある場合、ここから追加する
		for(int i = 0; i < tableData.size(); i++) {
			Map<String, String> mapT = tableData.get(i);
			Map<String, String> mapNew = new HashMap<String, String>();

			for(int j = 0; j < mapT.size(); j++) {
				String strK = mapT.keySet().toArray()[j].toString();
				String strV = mapT.get(strK);
				if(true) {
					if(strK.equals(XmlFileEditHelper.strConstName)||
							strK.equals(XmlFileEditHelper.strConstValue)) {
						if(strV != null) {
							XmlFileEditHelper.outputLog("setAttributeCnv strV B=" + strV);

							//strV = StringUtil.replaceAll("\"", "&#34;", strV);
							//strV = StringUtil.replaceAll("&", "&amp;", strV);
							//strV = StringUtil.replaceAll("<", "&lt;", strV);
							//strV = StringUtil.replaceAll(">", "&gt;", strV);
							//strV = StringUtil.replaceAll(" ", "&nbsp;", strV);

							XmlFileEditHelper.outputLog("setAttributeCnv strV A=" + strV);
						}
					}
				}
				mapNew.put(strK, strV);
			}
			tableDataTemp.add(mapNew);
		}*/
		this.request.setAttribute("tableData", tableData);
	}

	/**
	 * XSLTファイルから配置内容を取得する
	 * @param xmlFile
	 * @param xslFile
	 * @throws Exception
	 */
	private void readXsl(File xmlFile,String xslFile) throws Exception{
		try {

			SAXBuilder sax = new SAXBuilder();
			docXml = sax.build(xmlFile);

			Document docXslt = sax.build(new File(xslFile));
			Element elXsltRoot = docXslt.getRootElement();
			Element elTemplate = elXsltRoot.getChild("template", NAMESPACE_XSLS);


			parseNewXsl(0, elTemplate, "");


		} catch (Exception e) {
			logger.error("readXsl exception:");
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * XSLTファイルから配置内容を取得する
	 * @param level
	 * @param elXslt
	 * @param xpath
	 */
	private void parseNewXsl(int level, Element elXslt, String xpath) {
		XmlFileTallBaseHelper.TCommentInfo lastComment = null;
		XmlFileTallBaseHelper basehelper = new XmlFileTallBaseHelper();

		List<Namespace> namelist = basehelper.getXsltNameSpace(elXslt);

		for(Content content : elXslt.getContent()) {
			XmlFileEditHelper.outputLog("parseNewXsl content.getCType()=" + content.getCType());
			XmlFileEditHelper.outputLog("parseNewXsl content.getValue()=" + content.getValue());
			if (content.getCType() == Content.CType.Comment) {
				Comment comment = (Comment)content;
				XmlFileEditHelper.outputLog("parseNewXsl comment=" + comment.getText());

				String strCurComment = comment.getText().trim();
				if(iCommentCnt == 0) {
					strXsltSplit = strCurComment.substring(0, 1);
					strCurComment = strCurComment.substring(1);
					XmlFileEditHelper.outputLog("parseNewXsl strSplit=" + strXsltSplit);
				}
				lastComment = basehelper.getCommentInfo(strCurComment, strXsltSplit);
				iCommentCnt++;
				XmlFileEditHelper.outputLog("parseNewXsl comment end=" + comment.getValue());
			}
			else if (content.getCType() == Content.CType.Element) {
				Element element = (Element)content;
				String select = xpath + element.getAttributeValue("select");

				XmlFileEditHelper.outputLog("parseNewXsl getName=" + element.getName());
				XmlFileEditHelper.outputLog("parseNewXsl getName=" + element.getNamespace());
				XmlFileEditHelper.outputLog("parseNewXsl select=" + select);

				//XPathExpression<Element> expr = xFactory.compile(select, Filters.element(), null, NAMESPACE_JMX, NAMESPACE_JMX_IB, NAMESPACE_JMX_SEIS);
				XPathExpression<Element> expr = xFactory.compile(select, Filters.element(), null, namelist);
				List<Element> elements = expr.evaluate(docXml);
				if ("value-of".equals(element.getName())) {
					XmlFileEditHelper.outputLog("parseNewXsl level=" + element);

					//String text = elements.get(0).getTextTrim();
					Map<String, String> record = new HashMap<String, String>();
					if(lastComment.strAtrribFlag != null && !lastComment.strAtrribFlag.isEmpty()) {
						record.put(XmlFileEditHelper.strConstLevel, "" + (level));
					} else {
						record.put(XmlFileEditHelper.strConstLevel, "" + level);
					}
					record.put(XmlFileEditHelper.strConstName,  lastComment.strNameComment);
					record.put(XmlFileEditHelper.strConstValue, "");
					record.put(XmlFileEditHelper.strConstButton, "1");
					record.put(XmlFileEditHelper.strConstLineCtrlType, lastComment.strDataType);
					record.put(XmlFileEditHelper.strConstExt_DBUID, lastComment.strDBUid);
					record.put(XmlFileEditHelper.strConstExt_Groupid, lastComment.strGroupID);
					record.put(XmlFileEditHelper.strConstLineType, lastComment.strLevelFlag);
					record.put(XmlFileEditHelper.strConstExt_Select, select);
					record.put(XmlFileEditHelper.strConstExt_AttribFlag, lastComment.strAtrribFlag);


					XmlFileEditHelper.outputLog("parseNewXsl level=" + level);
					XmlFileEditHelper.outputLog("parseNewXsl name=" + lastComment.strNameComment);
					XmlFileEditHelper.outputLog("parseNewXsl strConstLineCtrlType=" + lastComment.strDataType);
					XmlFileEditHelper.outputLog("parseNewXsl strConstExt_ComboxUID=" + lastComment.strDBUid);
					XmlFileEditHelper.outputLog("parseNewXsl strConstExt_Groupid=" + lastComment.strGroupID);
					XmlFileEditHelper.outputLog("parseNewXsl strConstLineType=" + lastComment.strLevelFlag);
					XmlFileEditHelper.outputLog("parseNewXsl select=" + select);
					XmlFileEditHelper.outputLog("parseNewXsl strAtrribFlag=" + lastComment.strAtrribFlag);


					tableConfDataForSaveObj.add(record);
				}
				else if ("for-each".equals(element.getName())) {

					XmlFileEditHelper.outputLog("parseNewXsl elements.size()=" + elements.size());

					for (int i = 1; i <= elements.size(); i++) {
						Map<String, String> record = new HashMap<String, String>();
						//String text = elements.get(0).getTextTrim();
						String button = "1";


						/*
						for(Map<String, String> map : tableData){
							if(map.get("name").equals(lastComment) && button.equals(map.get("button"))){
								button = "2";
								break;
							}
						}*/
						record.put(XmlFileEditHelper.strConstLevel, "" + (level+1));
						record.put(XmlFileEditHelper.strConstName,  lastComment.strNameComment);
						record.put(XmlFileEditHelper.strConstValue, "");
						record.put(XmlFileEditHelper.strConstButton, "1");
						record.put(XmlFileEditHelper.strConstLineCtrlType, lastComment.strDataType);
						record.put(XmlFileEditHelper.strConstExt_DBUID, lastComment.strDBUid);
						record.put(XmlFileEditHelper.strConstExt_Groupid, lastComment.strGroupID);
						record.put(XmlFileEditHelper.strConstLineType, lastComment.strLevelFlag);
						record.put(XmlFileEditHelper.strConstExt_Select, select);
						record.put(XmlFileEditHelper.strConstExt_AttribFlag, lastComment.strAtrribFlag);

						tableConfDataForSaveObj.add(record);

						XmlFileEditHelper.outputLog("parseNewXsl level=" + (level+1));
						XmlFileEditHelper.outputLog("parseNewXsl name=" + lastComment.strNameComment);
						XmlFileEditHelper.outputLog("parseNewXsl strConstLineCtrlType=" + lastComment.strDataType);
						XmlFileEditHelper.outputLog("parseNewXsl strConstExt_ComboxUID=" + lastComment.strDBUid);
						XmlFileEditHelper.outputLog("parseNewXsl strConstExt_Groupid=" + lastComment.strGroupID);
						XmlFileEditHelper.outputLog("parseNewXsl strConstLineType=" + lastComment.strLevelFlag);
						XmlFileEditHelper.outputLog("parseNewXsl select=" + select);
						XmlFileEditHelper.outputLog("parseNewXsl strAtrribFlag=" + lastComment.strAtrribFlag);

						//parseNewXsl(level + 1, element, select + "[" + i + "]/");
						parseNewXsl(level + 1, element, select + "/");
					}
				}
			}
		}

	}

//	/**
//	 * 新規作成・修正したXMLファイルを、訓練情報のトリガーとなるファイルURLに該当するディレクトリへコピーする
//	 * @param srcFile
//	 * @return
//	 * @throws IOException
//	 */
//	private boolean xmlCopy2webdir(File srcFile) throws IOException{
//		String destPathBaseStr = this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLURL_BASE_PATH + "/" + this.clsXmlFileMgrServiceObj.getPropData().strMETEOXMLURL_PATH_PREFIX;
//		String destFileName = srcFile.getName();
//
//		String basePathStr = srcFile.getAbsolutePath().replace(File.separator, "/");
//		String regexp =  "/[0-9]+/";
//		Pattern pt = Pattern.compile(regexp);
//		Matcher m = pt.matcher(basePathStr);
//		if(m.find()){
//			String basePathStr2 = basePathStr.substring(m.start());
//			File tempFile = new File(basePathStr2);
//			String tempFileParentPathStr = tempFile.getParent();
//			File destDir = new File(destPathBaseStr + File.separator + tempFileParentPathStr);
//			if(! destDir.exists()){
//				destDir.mkdirs();
//			}
//
//			File destFile = new File(destDir + File.separator + destFileName);
//
//			FileChannel inChannel = null;
//			FileChannel outChannel = null;
//			if(destFile.exists() && ( ! destFile.canWrite()) ){
//				return false;
//			}
//			else{
//				try {
//					inChannel = new FileInputStream(srcFile).getChannel();
//					outChannel = new FileOutputStream(destFile).getChannel();
//					inChannel.transferTo(0, inChannel.size(),outChannel);
//
//					// 訓練情報選択時時に使用するので、ファイル名をセッションに保存
//					request.getSession().setAttribute(Constants.SESSION_SAVEXMLHTTPNAME, basePathStr2);
//				}catch (IOException e) {
//					return false;
//				}
//				finally {
//					if (inChannel != null) inChannel.close();
//					if (outChannel != null) outChannel.close();
//				}
//			}
//		}else{
//			return false;
//		}
//
//
//		return true;
//	}
}
