/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.RefugeInformationDto;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ne.publiccommons.xml.pcxml1._1.TypeControl;
import jp.ne.publiccommons.xml.pcxml1._1.TypeReport;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeEvacuationOrder;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.EnumTypeSort;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.EnumTypeType;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.TypeShelter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.urgentmail.TypeInformation;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.EnumTypeOfficeInfoContactInfoAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeContactInfo;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDisaster;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeOfficeInfo;
import jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis.TypeHead;
import jp.ne.publiccommons.xml.xml.edxl.CategoryType;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import jp.ne.publiccommons.xml.xml.edxl.CommonsTargetAreaType;
import oasis.names.tc.emergency.edxl.de._1.AnyXMLType;
import oasis.names.tc.emergency.edxl.de._1.ContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;
import oasis.names.tc.emergency.edxl.de._1.StatusValues;
import oasis.names.tc.emergency.edxl.de._1.TypeValues;
import oasis.names.tc.emergency.edxl.de._1.XmlContentType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;

/**
 * 公共情報コモンズのユーティリティクラスです.
 */
public class PublicCommonsUtils {
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	private static Properties properties = ResourceUtil.getProperties("SaigaiTask.properties");

	//========================================================================================
	// 定数
	//========================================================================================
	/** メッセージ機密性(固定値) */
	public static final String COMBINED_CONFIDENTIALITY = "UNCLASSIFIED AND NOT SENSITIVE";

	//========================================================================================
	// EDXL
	//========================================================================================
	/** EDXLファクトリ */
	public static oasis.names.tc.emergency.edxl.de._1.ObjectFactory edxlFactory =
			new oasis.names.tc.emergency.edxl.de._1.ObjectFactory();

	//========================================================================================
	// PCXML
	//========================================================================================
	/** コモンズEDXLファクトリ */
	public static jp.ne.publiccommons.xml.xml.edxl.ObjectFactory commonsEdxlFactory =
			new jp.ne.publiccommons.xml.xml.edxl.ObjectFactory();

	/** PCXファクトリ. ルート要素、Control部 */
	public static jp.ne.publiccommons.xml.pcxml1._1.ObjectFactory pcxFactory =
			new jp.ne.publiccommons.xml.pcxml1._1.ObjectFactory();

	/** PCX_EBファクトリ. 共通要素 */
	public static jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.ObjectFactory pcxEbFactory =
			new jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.ObjectFactory();

	/** PCX_ADDファクトリ. 追加の要素 */
	public static jp.ne.publiccommons.xml.pcxml1._1_3.addition.ObjectFactory pcxAddFactory =
			new jp.ne.publiccommons.xml.pcxml1._1_3.addition.ObjectFactory();

	//========================================================================================
	// JAXBContext
	//========================================================================================
	/** 共通パッケージのリスト */
	private static List<String> pkgList;
	static {
		pkgList = new ArrayList<String>();
		pkgList.add("oasis.names.tc.emergency.edxl.de._1");
		pkgList.add("jp.ne.publiccommons.xml.xml.edxl");
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1");
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis");
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.addition");
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation"); // for EvacuationOrder
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter"); // for Shelter
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.urgentmail"); // for UrgentMail
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.event"); // for Event
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation"); // for generalinformation
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation"); // for damageinformation3
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter"); // for antidisasterheadquarter
		pkgList.add("jp.ne.publiccommons.xml.pcxml1._1_3.html");
	}

	/** 公共情報コモンズJAXBコンテキスト */
	public static JAXBContext publiccommonsJAXBContext = getJAXBContext();

	/**
	 * JAXBコンテキストパスを取得します.
	 * @return
	 */
	private static String getContextPath(List<String> pkgList) {
		return pkgList==null ? null : StringUtils.join(pkgList.toArray(new String[pkgList.size()]), ":");
	}

	/**
	 * JAXBContextを取得します.
	 * @return
	 */
	public static JAXBContext getJAXBContext() {
		JAXBContext jaxbContext =null;
		try {
			String contextPath = getContextPath(pkgList);
			jaxbContext = JAXBContext.newInstance(contextPath);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return jaxbContext;
	}

	public static String toText(JAXBElement<?> jaxbElement) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			marshal(jaxbElement, os);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return os.toString();
	}

	public static void marshal(JAXBElement<?> jaxbElement, OutputStream os) throws JAXBException {
		JAXBContext jaxbContext = getJAXBContext();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(jaxbElement, os);
	}

	/**
	 * ファイルに出力します.
	 * @param edxlDistributionType EDXL-DE
	 * @param file 出力ファイル
	 */
	public static void marshal(JAXBElement<?> jaxbElement, File file) throws FileNotFoundException, JAXBException {
		FileOutputStream os = new FileOutputStream(file);
		marshal(jaxbElement, os);
	}

	public static JAXBElement<?> unmarshal(File file) {
		JAXBElement<?> jaxbElement = null;
		try {
			JAXBContext jaxbContext = getJAXBContext();
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			jaxbElement = (JAXBElement<?>) unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return jaxbElement;
	}

	/**
	 * XMLファイルからEDXLDistributionTypeインスタンスを生成します.
	 * @param file
	 * @return
	 */
	public static EDXLDistributionType unmarshalEDXLDistribution(File file) {
		EDXLDistributionType edxlDistributionType = null;
		JAXBElement<?> jaxbElement = unmarshal(file);
		Object unmarshalObject = jaxbElement.getValue();
		if(unmarshalObject instanceof EDXLDistributionType) {
			edxlDistributionType = (EDXLDistributionType) unmarshalObject;
		}
		return edxlDistributionType;
	}

	/**
	 * XMLファイルからEDXLDistributionTypeインスタンスを生成します.
	 * @param file
	 * @return
	 */
	public static TypeReport unmarshalReport(File file) {
		TypeReport typeReport = null;
		JAXBElement<?> jaxbElement = unmarshal(file);
		if(jaxbElement!=null) {
			Object unmarshalObject = jaxbElement.getValue();
			if(unmarshalObject instanceof TypeReport) {
				typeReport = (TypeReport) unmarshalObject;
			}
		}
		return typeReport;
	}

	/**
	 * EDXL-DEのルート要素を生成します.
	 * @param senderID 発信システムID
	 * @param distributionID メッセージID
	 * @param distributionType 更新種別
	 * @param distributionStatus 運用種別
	 * @param targetArea 対象地域
	 */
	public static EDXLDistributionType createEDXLDistribution(
			String senderID,
			String distributionID,
			TypeValues distributionType,
			StatusValues distributionStatus,
			CommonsTargetAreaType targetArea,
			CommonsContentObjectType description)
	{

		// EDXLDistribution
		EDXLDistributionType edxlDistributionType = edxlFactory.createEDXLDistributionType();
		edxlDistributionType.setDistributionID(distributionID);
		edxlDistributionType.setSenderID(senderID);
		XMLGregorianCalendar dateTimeSent = PublicCommonsUtils.getXMLGregorianCalendar(new Date());
		edxlDistributionType.setDateTimeSent(dateTimeSent);
		edxlDistributionType.setDistributionStatus(distributionStatus);
		edxlDistributionType.setDistributionType(distributionType);
		edxlDistributionType.setCombinedConfidentiality(COMBINED_CONFIDENTIALITY);
		//edxlDistributionType.getTargetArea().add(edxlFactory.createTargetArea(targetArea));
		edxlDistributionType.getTargetArea().add(commonsEdxlFactory.createTargetArea(targetArea));
		edxlDistributionType.getContentObject().add(commonsEdxlFactory.createContentObject(description));

		return edxlDistributionType;
	}

	/**
	 * EDXL-DEに公共情報コモンズContentObjectを設定します.
	 * 公共情報コモンズの場合はConentObjectを1つだけ設定しなければなりません.
	 * @param edxlDistributionType EDXL_DE
	 * @param commonsContentObjectType 公共情報コモンズContentObject
	 */
	public static void setCommonsContentObject(EDXLDistributionType edxlDistributionType, CommonsContentObjectType commonsContentObjectType) {
		List<JAXBElement<? extends ContentObjectType>> contentObjectTypeList = edxlDistributionType.getContentObject();
		JAXBElement<CommonsContentObjectType> contentObject = commonsEdxlFactory.createContentObject(commonsContentObjectType);

		contentObjectTypeList.clear();
		contentObjectTypeList.add(contentObject);
	}

	/**
	 * EDXL-DEに公共情報コモンズContentObjectを取得します.
	 * 公共情報コモンズの場合はConentObjectを1つだけ設定しなければなりません.
	 * @param edxlDistributionType EDXL_DE
	 * @return 公共情報コモンズContentObject
	 */
	public static CommonsContentObjectType getCommonsContentObject(EDXLDistributionType edxlDistributionType) {
		List<JAXBElement<? extends ContentObjectType>> contentObjectTypeList = edxlDistributionType.getContentObject();
		CommonsContentObjectType commonsContentObjectType = null;
		if(0<contentObjectTypeList.size()) {
			commonsContentObjectType = (CommonsContentObjectType) contentObjectTypeList.get(0).getValue();
		}
		return commonsContentObjectType;
	}

	/**
	 *
	 * @param edxlDistributionType
	 * @return
	 */
	public static TypeReport getReport(EDXLDistributionType edxlDistributionType) {
		List<TypeReport> reportList = getReportList(edxlDistributionType);
		return 0<reportList.size() ? reportList.get(0) : null;
	}

	public static List<TypeReport> getReportList(EDXLDistributionType edxlDistributionType) {
		List<TypeReport> reportList = new ArrayList<TypeReport>();
		if(edxlDistributionType!=null) {
			CommonsContentObjectType commonsContentObjectType = getCommonsContentObject(edxlDistributionType);
			XmlContentType xmlContentType = commonsContentObjectType.getXmlContent();
			List<AnyXMLType> embeddedXMLContentTypeList = xmlContentType.getEmbeddedXMLContent();
			if(0<embeddedXMLContentTypeList.size()) {
				AnyXMLType embeddedXMLContentType = embeddedXMLContentTypeList.get(0);
				List<Object> list = embeddedXMLContentType.getAny();
				for(Object obj : list) {
					if(obj instanceof JAXBElement) {
						JAXBElement<?> jaxbElement = (JAXBElement<?>) obj;
						if(jaxbElement.getValue() instanceof TypeReport) {
							TypeReport typeReport = (TypeReport) jaxbElement.getValue();
							reportList.add(typeReport);
						}
					}
				}
			}
		}
		return reportList;
	}

	/**
	 * EDXL-DE から情報種別を取得します.
	 * @param edxlDistributionType
	 * @return
	 */
	public static CategoryType getCategoryType(EDXLDistributionType edxlDistributionType) {
		CommonsContentObjectType commonsContentObjectType = PublicCommonsUtils.getCommonsContentObject(edxlDistributionType);
		CategoryType categoryType = commonsContentObjectType.getCategory();
		return categoryType;
	}

	/**
	 * TypeReport を生成します.
	 * @param typeControl
	 * @param typeHead
	 * @param body
	 * @return
	 */
	public static TypeReport createTypeReport(TypeControl typeControl, TypeHead typeHead, Object body) {
		TypeReport typeReport = pcxFactory.createTypeReport();
		typeReport.setControl(typeControl);
		typeReport.setHead(typeHead);
		typeReport.setAny(body);
		return typeReport;
	}

	/**
	 * XMLContentを生成します.
	 * @param typeControl
	 * @param typeHead
	 * @param body
	 * @return
	 */
	public static XmlContentType createXmlContentType(TypeControl typeControl, TypeHead typeHead, Object body) {
		TypeReport typeReport = createTypeReport(typeControl, typeHead, body);

		// EmbeddedXMLContent
		AnyXMLType embeddedXMLContentType = edxlFactory.createAnyXMLType();
		JAXBElement<TypeReport> report = pcxFactory.createReport(typeReport);
		embeddedXMLContentType.getAny().add(report);

		XmlContentType xmlContentType = edxlFactory.createXmlContentType();
		xmlContentType.getEmbeddedXMLContent().add(embeddedXMLContentType);
		return xmlContentType;
	}

	/**
	 * 対象地域を生成する.
	 * @param areaName 地域名称
	 * @param areaNameKana 地域名称(かな表記)
	 * @param jisX0402 市区町村及び一部事務組合等コード
	 * @param circle
	 * @return
	 */
	public static CommonsTargetAreaType createCommonsTargetAreaType(String areaName, String areaNameKana, String jisX0402, String circle) {
		// 対象地域
		CommonsTargetAreaType commonsTargetAreaType = commonsEdxlFactory.createCommonsTargetAreaType();
		commonsTargetAreaType.setAreaName(areaName);
		commonsTargetAreaType.setAreaNameKana(areaNameKana);
		commonsTargetAreaType.setJisX0402(jisX0402);
		if(StringUtils.isNotEmpty(circle)) {
			commonsTargetAreaType.getCircle().add(circle);
		}
		return commonsTargetAreaType;
	}

	/**
	 * PCXMLを設定します.
	 * @param edxlDistributionType
	 * @param pcxmlJAXBElement
	 */
	public static void setPCXML(EDXLDistributionType edxlDistributionType, JAXBElement<?> pcxmlJAXBElement) {
		TypeReport typeReport = getReport(edxlDistributionType);
		typeReport.setAny(pcxmlJAXBElement);
	}

	/**
	 * PCXMLを設定します.
	 * @param typeReport
	 * @param pcxmlJAXBElement
	 */
	public static void setPCXML(TypeReport typeReport, JAXBElement<?> pcxmlJAXBElement) {
		typeReport.setAny(pcxmlJAXBElement);
	}

	/**
	 * PCXMLを取得します.
	 * @param edxlDistributionType
	 */
	public static JAXBElement<?> getPCXML(EDXLDistributionType edxlDistributionType) {
		TypeReport typeReport = getReport(edxlDistributionType);
		return getPCXML(typeReport);
	}
	public static JAXBElement<?> getPCXML(TypeReport typeReport) {
		return (JAXBElement<?>) typeReport.getAny();
	}

	/**
	 * 起因説明を設定します.
	 * @param edxlDistributionType
	 * @param incidentDescription 起因説明
	 */
	public static void setIncidentDescription(EDXLDistributionType edxlDistributionType, String incidentDescription) {
		CommonsContentObjectType commonsContentObjectType = getCommonsContentObject(edxlDistributionType);
		commonsContentObjectType.setIncidentDescription(incidentDescription);
	}

	/**
	 * 標題 Report/Head/Titleを設定します.
	 * @param edxlDistributionType
	 * @param title
	 */
	public static void setTitle(EDXLDistributionType edxlDistributionType, String title) {
		TypeReport typeReport = getReport(edxlDistributionType);
		setTitle(typeReport, title);
	}
	/**
	 * 標題 Report/Head/Titleを設定します.
	 * @param typeReport
	 * @param title
	 */
	public static void setTitle(TypeReport typeReport, String title) {
		typeReport.getHead().setTitle(title);
	}

	/**
	 * 標題 Report/Head/Titleを取得します.
	 * @param edxlDistributionType
	 */
	public static String getTitle(EDXLDistributionType edxlDistributionType) {
		TypeReport typeReport = getReport(edxlDistributionType);
		return getTitle(typeReport);
	}
	public static String getTitle(TypeReport typeReport) {
		return typeReport.getHead().getTitle();
	}

	/**
	 * 見出し文を設定します.
	 * @param edxlDistributionType
	 * @param contentDescription 見出し文
	 */
	public static void setContentDescription(EDXLDistributionType edxlDistributionType, String contentDescription) {
		CommonsContentObjectType commonsContentObjectType = getCommonsContentObject(edxlDistributionType);
		commonsContentObjectType.setContentDescription(contentDescription);
	}


	/**
	 * 見出し文を取得します.
	 * @param edxlDistributionType
	 */
	public static String getContentDescription(EDXLDistributionType edxlDistributionType) {
		CommonsContentObjectType commonsContentObjectType = getCommonsContentObject(edxlDistributionType);
		return commonsContentObjectType.getContentDescription();
	}

	/**
	 * 補足情報を設定します.
	 * @param edxlDistributionType
	 * @param complementaryInfo
	 */
	public static void setComplementaryInfo(EDXLDistributionType edxlDistributionType, String complementaryInfo) {
		TypeReport typeReport = getReport(edxlDistributionType);
		setComplementaryInfo(typeReport, complementaryInfo);
	}
	/**
	 * 補足情報を設定します.
	 * @param typeReport
	 * @param complementaryInfo
	 */
	public static void setComplementaryInfo(TypeReport typeReport, String complementaryInfo) {
		JAXBElement<?> pcxml = getPCXML(typeReport);
		Object pcxmlObject = pcxml.getValue();
		if(pcxmlObject instanceof TypeEvacuationOrder) {
			TypeEvacuationOrder typeEvacuationOrder = (TypeEvacuationOrder) pcxmlObject;
			typeEvacuationOrder.setComplementaryInfo(complementaryInfo);
		}
		if(pcxmlObject instanceof TypeShelter) {
			TypeShelter typeShelter = (TypeShelter) pcxmlObject;
			typeShelter.setComplementaryInfo(complementaryInfo);
		}
	}

	/**
	 * 補足情報を取得します.
	 * @param edxlDistributionType
	 * @return
	 */
	public static String getComplementaryInfo(EDXLDistributionType edxlDistributionType) {
		TypeReport typeReport = getReport(edxlDistributionType);
		return getComplementaryInfo(typeReport);
	}

	/**
	 * 補足情報を取得します.
	 * @param typeReport
	 * @return
	 */
	public static String getComplementaryInfo(TypeReport typeReport) {
		String complementaryInfo = null;
		JAXBElement<?> pcxml = getPCXML(typeReport);
		if(pcxml!=null) {
			Object pcxmlObject = pcxml.getValue();
			if(pcxmlObject instanceof TypeEvacuationOrder) {
				TypeEvacuationOrder typeEvacuationOrder = (TypeEvacuationOrder) pcxmlObject;
				complementaryInfo = typeEvacuationOrder.getComplementaryInfo();
			}
			if(pcxmlObject instanceof TypeShelter) {
				TypeShelter typeShelter = (TypeShelter) pcxmlObject;
				complementaryInfo = typeShelter.getComplementaryInfo();
			}
		}
		return complementaryInfo;
	}

	public static void setReportDateTime(EDXLDistributionType edxlDistributionType, Date reportDate) {
		TypeReport typeReport = getReport(edxlDistributionType);
		typeReport.getHead().setReportDateTime(getXMLGregorianCalendar(reportDate));
	}

	public static Date getReportDateTime(EDXLDistributionType edxlDistributionType) {
		TypeReport typeReport = getReport(edxlDistributionType);
		return getReportDateTime(typeReport);
	}
	public static Date getReportDateTime(TypeReport typeReport) {
		Date reportDate = null;
		XMLGregorianCalendar xmlGregorianCalendar = typeReport.getHead().getReportDateTime();
		if(xmlGregorianCalendar!=null) {
			reportDate = xmlGregorianCalendar.toGregorianCalendar().getTime();
		}
		return reportDate;
	}

	/**
	 * 作成日時を設定します.
	 * @param edxlDistributionType
	 * @param createDate
	 */
	public static void setCreateDateTime(EDXLDistributionType edxlDistributionType, Date createDate) {
		TypeReport typeReport = getReport(edxlDistributionType);
		typeReport.getHead().setCreateDateTime(getXMLGregorianCalendar(createDate));
	}

	/**
	 * 組織の連絡先情報を生成する.
	 * @param type PHONE/FAX/E_MAILなど
	 * @param value
	 * @return 組織の連絡先情報
	 */
	public static TypeContactInfo createContactInfo(EnumTypeOfficeInfoContactInfoAttr type, String value) {
		TypeContactInfo typeContactInfo = pcxEbFactory.createTypeContactInfo();
		typeContactInfo.setContactType(type);
		typeContactInfo.setValue(value);
		return typeContactInfo;
	}


	/**
	 * 発表組織情報を生成する.
	 * Locationは非推奨のため使用しない.
	 * @param publiccommonsData コモンズ送信データ
	 * @return TypeOfficeInfo
	 */
	public static TypeOfficeInfo createOfficeInfo(PCommonsSendDto pCommonsSendDto) {
		TypeOfficeInfo typeOfficeInfo = pcxEbFactory.createTypeOfficeInfo();

		// 担当者(イベント発信のみセット)
		if(StringUtils.isNotEmpty(pCommonsSendDto.personResponsible) && PubliccommonsSendType.EVENT.equals(pCommonsSendDto.categoryType.toString())) {
			typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.PERSON_RESPONSIBLE, pCommonsSendDto.personResponsible));
		}

		// 組織名
		if(StringUtil.isNotEmpty(pCommonsSendDto.organizationName)) typeOfficeInfo.setOrganizationName(pCommonsSendDto.organizationName);

		// 地方公共団体コード
		if(StringUtil.isNotEmpty(pCommonsSendDto.organizationCode)) typeOfficeInfo.setOrganizationCode(pCommonsSendDto.organizationCode);

		// 組織ドメイン
		if(StringUtil.isNotEmpty(pCommonsSendDto.organizationDomainName)) typeOfficeInfo.setOrganizationDomainName(pCommonsSendDto.organizationDomainName);

		// 部署名
		if(StringUtil.isNotEmpty(pCommonsSendDto.officeName)) typeOfficeInfo.setOfficeName(pCommonsSendDto.officeName);

		// 部署名カナ
		if(StringUtil.isNotEmpty(pCommonsSendDto.officeNameKana)) typeOfficeInfo.setOfficeNameKana(pCommonsSendDto.officeNameKana);

		// 部署住所
		CommonsTargetAreaType commonsTargetAreaType = PublicCommonsUtils.createCommonsTargetAreaType(pCommonsSendDto.officeLocationArea,null,null,null);
		if(StringUtil.isNotEmpty(pCommonsSendDto.officeLocationArea)) typeOfficeInfo.setOfficeLocation(commonsTargetAreaType);

		// 部署電話番号
		if(StringUtils.isNotEmpty(pCommonsSendDto.phone)) typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.PHONE, pCommonsSendDto.phone));

		// 部署FAX番号
		if(StringUtils.isNotEmpty(pCommonsSendDto.fax)) typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.FAX, pCommonsSendDto.fax));

		// 部署メールアドレス
		if(StringUtils.isNotEmpty(pCommonsSendDto.email)) typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.E_MAIL, pCommonsSendDto.email));

		// 部署ドメイン
		if(StringUtils.isNotEmpty(pCommonsSendDto.officeDomainName)) typeOfficeInfo.setOfficeDomainName(pCommonsSendDto.officeDomainName);

		return typeOfficeInfo;
	}

	/**
	 * 作成組織情報を生成する.
	 * Locationは非推奨のため使用しない.
	 * @param publiccommonsData コモンズ送信データ
	 * @return TypeOfficeInfo
	 */
	public static TypeOfficeInfo createOfficeInfoEditorial(PCommonsSendDto pCommonsSendDto) {
		TypeOfficeInfo typeOfficeInfo = pcxEbFactory.createTypeOfficeInfo();

		// 組織名
		if(StringUtil.isNotEmpty(pCommonsSendDto.organizationNameEditorial)) typeOfficeInfo.setOrganizationName(pCommonsSendDto.organizationNameEditorial);

		// 地方公共団体コード
		if(StringUtil.isNotEmpty(pCommonsSendDto.organizationCodeEditorial)) typeOfficeInfo.setOrganizationCode(pCommonsSendDto.organizationCodeEditorial);

		// 組織ドメイン
		if(StringUtil.isNotEmpty(pCommonsSendDto.organizationDomainNameEditorial)) typeOfficeInfo.setOrganizationDomainName(pCommonsSendDto.organizationDomainNameEditorial);

		// 部署名
		if(StringUtil.isNotEmpty(pCommonsSendDto.officeNameEditorial)) typeOfficeInfo.setOfficeName(pCommonsSendDto.officeNameEditorial);

		// 部署名カナ
		if(StringUtil.isNotEmpty(pCommonsSendDto.officeNameKanaEditorial)) typeOfficeInfo.setOfficeNameKana(pCommonsSendDto.officeNameKanaEditorial);

		// 部署住所
		CommonsTargetAreaType commonsTargetAreaType = PublicCommonsUtils.createCommonsTargetAreaType(pCommonsSendDto.officeLocationAreaEditorial,null,null,null);
		if(StringUtil.isNotEmpty(pCommonsSendDto.officeLocationAreaEditorial)) typeOfficeInfo.setOfficeLocation(commonsTargetAreaType);

		// 部署電話番号
		if(StringUtils.isNotEmpty(pCommonsSendDto.phoneEditorial)) typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.PHONE, pCommonsSendDto.phoneEditorial));

		// 部署FAX番号
		if(StringUtils.isNotEmpty(pCommonsSendDto.faxEditorial)) typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.FAX, pCommonsSendDto.faxEditorial));

		// 部署メールアドレス
		if(StringUtils.isNotEmpty(pCommonsSendDto.emailEditorial)) typeOfficeInfo.getContactInfo().add(createContactInfo(EnumTypeOfficeInfoContactInfoAttr.E_MAIL, pCommonsSendDto.emailEditorial));

		// 部署ドメイン
		if(StringUtils.isNotEmpty(pCommonsSendDto.officeDomainNameEditorial)) typeOfficeInfo.setOfficeDomainName(pCommonsSendDto.officeDomainNameEditorial);

		return typeOfficeInfo;
	}

	/**
	 * 災害情報を生成する.
	 * @param disasterName 災害名
	 * @return 災害情報
	 */
	public static TypeDisaster createTypeDisaster(String disasterName) {
		TypeDisaster typeDisaster = pcxEbFactory.createTypeDisaster();
		typeDisaster.setDisasterName(disasterName);
		return typeDisaster;
	}

	/**
	 * XMLGregorianCalendarで取得します.
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
		try {
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//			date = format.parse(format.format(date));
			DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.setTime(date);
			XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
			return xmlGregorianCalendar;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * カテゴリマスタ情報
	 * @return キーに情報種別キー、値に情報種別名を持つMap
	 */
	public static Map<String, String> getCategoryTypeMap() {
		Map<String, String> categoryTypeMap = new LinkedHashMap<String, String>();
		categoryTypeMap.put(CategoryType.ANTIDISASTER_HEADQUARTER.toString(), lang.__("Establish disaster response HQ info"));
		categoryTypeMap.put(CategoryType.EVACUATION_ORDER.toString(), lang.__("Evacuation advisory, order info"));
		categoryTypeMap.put(CategoryType.SHELTER.toString(), lang.__("Shelter info"));
		categoryTypeMap.put(CategoryType.DAMAGE_INFORMATION.toString(), lang.__("Damage info"));
		categoryTypeMap.put(CategoryType.SEDIMENT_DISASTERS_WARNING.toString(), lang.__("Landslide warning info"));
		categoryTypeMap.put(CategoryType.WATER_LEVEL.toString(), lang.__("River water level info"));
		categoryTypeMap.put(CategoryType.RAIN_FALL.toString(), lang.__("Rainfall info"));
		categoryTypeMap.put(CategoryType.EVENT.toString(), lang.__("Event info"));
		categoryTypeMap.put(CategoryType.FAMILIARISE_RIVER.toString(), lang.__("Water level well-known rivers"));
		categoryTypeMap.put(CategoryType.GENERAL_INFORMATION.toString(), lang.__("Info<!--2-->"));
		categoryTypeMap.put(CategoryType.URGENT_MAIL.toString(), lang.__("Emergency e-mail"));
		return categoryTypeMap;
	}

	/**
	 * モジュラス11を計算します.
	 * @param digStr
	 * @return
	 */
	public static String mod11(String digStr) {
		int len = digStr.length();
		int sum = 0, first = len+1;
		for(int idx=0; idx<len; idx++) {
			int num = Character.getNumericValue(digStr.charAt(idx));
			sum += (first - idx) * num;
		}
		int remainder = sum % 11;
		switch(remainder) {
		case 0:
			return "0";
		case 1:
			return "X";
		default:
			return Integer.valueOf(11 - remainder).toString();
		}
	}

	/**
	 * 避難勧告・指示情報のユーティリティクラスです.
	 */
	public static class EvacuationOrder{

		/**
		 * TypeEvacuationOrder を取得します.
		 * @param edxlDistributionType
		 * @return
		 */
		public static TypeEvacuationOrder getTypeEvacuationOrder(EDXLDistributionType edxlDistributionType) {
			TypeReport typeReport = getReport(edxlDistributionType);
			return getTypeEvacuationOrder(typeReport);
		}

		/**
		 * TypeEvacuationOrder を取得します.
		 * @param typeReport
		 * @return
		 */
		public static TypeEvacuationOrder getTypeEvacuationOrder(TypeReport typeReport) {
			TypeEvacuationOrder typeEvacuationOrder = null;
			JAXBElement<?> pcxml = getPCXML(typeReport);
			if(pcxml!=null) {
				Object pcxmlObject = pcxml.getValue();
				if(pcxmlObject instanceof TypeEvacuationOrder) {
					typeEvacuationOrder = (TypeEvacuationOrder) pcxmlObject;
				}
			}
			return typeEvacuationOrder;
		}
	}

	/**
	 * 避難所情報のユーティリティクラスです.
	 */
	public static class Shelter{
		public static TypeShelter getTypeShelter(TypeReport typeReport) {
			TypeShelter typeShelter = null;
			JAXBElement<?> jaxbElement = PublicCommonsUtils.getPCXML(typeReport);
			if(jaxbElement!=null) {
				Object pcxmlObject = jaxbElement.getValue();
				if(pcxmlObject instanceof TypeShelter) {
					typeShelter = (TypeShelter) pcxmlObject;
				}
			}
			return typeShelter;
		}
	}

	/**
	 * 緊急速報メールのユーティリティクラスです.
	 */
	public static class UrgentMail{
		public static TypeInformation getTypeInformation(TypeReport typeReport) {
			TypeInformation typeInformation = null;
			JAXBElement<?> jaxbElement = PublicCommonsUtils.getPCXML(typeReport);
			if(jaxbElement!=null) {
				Object pcxmlObject = jaxbElement.getValue();
				if(pcxmlObject instanceof TypeInformation) {
					typeInformation = (TypeInformation) pcxmlObject;
				}
			}
			return typeInformation;
		}
	}

	/**
	 * 避難所開設状況から避難所区分(Sort)を取得
	 * @return 避難所区分
	 */
	public static Map<String, EnumTypeSort> getSortMap() {
		Map<String, EnumTypeSort> sortMap = new HashMap<String, EnumTypeSort>();
		sortMap.put(lang.__("Permanent status"), EnumTypeSort.常設);
		sortMap.put(lang.__("Unknown"), EnumTypeSort.不明);
		sortMap.put(lang.__("Not opening"), EnumTypeSort.未開設);
		sortMap.put(lang.__("Opening"), EnumTypeSort.開設);
		sortMap.put(lang.__("Closing"), EnumTypeSort.閉鎖);
		return sortMap;
	}

	/**
	 * 避難所種別から避難所種別(Type)を取得
	 * @return 避難所種別
	 */
	public static Map<String, EnumTypeType> getTypeMap() {
		Map<String, EnumTypeType> sortMap = new HashMap<String, EnumTypeType>();
		sortMap.put(lang.__("Shelter"), EnumTypeType.避難所);
		sortMap.put(lang.__("Temporary shelter<!--2-->"), EnumTypeType.臨時避難所);
		sortMap.put(lang.__("Temporary shelter: No open request"), EnumTypeType.一時避難場所_開設措置なし);
		sortMap.put(lang.__("Evacuation district: No opening measures"), EnumTypeType.広域避難場所_開設措置なし);
		return sortMap;
	}

	/**
	 * 運用種別から運用種別(StatusValues)を取得
	 * @return 運用種別
	 */
	public static Map<String, StatusValues> getStatusValuesMap() {
		Map<String, StatusValues> sortMap = new HashMap<String, StatusValues>();
		sortMap.put("TEST", StatusValues.TEST);			// テスト
		sortMap.put("ACTUAL", StatusValues.ACTUAL);	// 本番
		sortMap.put("EXERCISE", StatusValues.EXERCISE);	// 訓練
		return sortMap;
	}

	/**
	 * ファイル名やURLからMimeタイプを返します.
	 * @param filename ファイル名やURL
	 * @return MimeType Mimeタイプ
	 */
	public static String getMimeType(String filename) {
		if (filename == null || "".equals(filename)) return "";

		// ファイル名から拡張子(小文字)を得る
		String ext = "";
		int pos = filename.lastIndexOf(".");
		ext = pos == -1 ? "" : filename.substring(pos + 1).toLowerCase();

		//	FileTypeMap filetypeMap = FileTypeMap.getDefaultFileTypeMap();
		//	return filetypeMap.getContentType(filename);

		// application/octet-stream対策
		if ("xml".equals(ext)) { return "text/xml"; }
		else if ("pdf".equals(ext)) { return "application/pdf"; }
		else if ("jpg".equals(ext)) { return "image/jpeg"; }
		else if ("jpeg".equals(ext)) { return "image/jpeg"; }
		else if ("mp2".equals(ext)) { return "audio/mpeg";}
		else if ("mp3".equals(ext)) { return "audio/mpeg";}
		else if ("mpga".equals(ext)) { return "audio/mpeg";}
		else if ("mpa".equals(ext)) { return "audio/mpeg";}
		else if ("m1a".equals(ext)) { return "audio/mpeg";}
		else if ("m2a".equals(ext)) { return "audio/mpeg";}
		else { return ""; }
	}

	/**
	 * ファイル名の拡張子からMediaTypeを返します.
	 * @param filename ファイル名やURL
	 * @return MediaType メディアタイプ
	 */
	public static String getMediaType(String filename) {
		if (filename == null || "".equals(filename)) return "";

		// ファイル名から拡張子(小文字)を得る
		String ext = "";
		int pos = filename.lastIndexOf(".");
		ext = pos == -1 ? "" : filename.substring(pos + 1).toLowerCase();

		if ("txt".equals(ext)) { return "Text"; }
		else if ("csv".equals(ext)) { return "Text"; }
		else if ("tsv".equals(ext)) { return "Text"; }
		else if ("htm".equals(ext)) { return "Text"; }
		else if ("html".equals(ext)) { return "Text"; }
		else if ("xml".equals(ext)) { return "Text"; }
		else if ("jpeg".equals(ext)) { return "Graphic"; }
		else if ("jpg".equals(ext)) { return "Graphic"; }
		else if ("gif".equals(ext)) { return "Graphic"; }
		else if ("bmp".equals(ext)) { return "Graphic"; }
		else if ("png".equals(ext)) { return "Graphic"; }
		else if ("tif".equals(ext)) { return "Graphic"; }
		else if ("tiff".equals(ext)) { return "Graphic"; }
		else if ("mac".equals(ext)) { return "Graphic"; }
		else if ("wmf".equals(ext)) { return "Graphic"; }
		else if ("mp3".equals(ext)) { return "Audio"; }
		else if ("wav".equals(ext)) { return "Audio"; }
		else if ("aif".equals(ext)) { return "Audio"; }
		else if ("aiff".equals(ext)) { return "Audio"; }
		else if ("au".equals(ext)) { return "Audio"; }
		else if ("mid".equals(ext)) { return "Audio"; }
		else if ("midi".equals(ext)) { return "Audio"; }
		else if ("ra".equals(ext)) { return "Audio"; }
		else if ("ram".equals(ext)) { return "Audio"; }
		else if ("m4r".equals(ext)) { return "Audio"; }
		else if ("m4a".equals(ext)) { return "Audio"; }
		else if ("mp4".equals(ext)) { return "Video"; }
		else if ("mpg".equals(ext)) { return "Video"; }
		else if ("mpeg".equals(ext)) { return "Video"; }
		else if ("avi".equals(ext)) { return "Video"; }
		else if ("mov".equals(ext)) { return "Video"; }
		else if ("rm".equals(ext)) { return "Video"; }
		else if ("divx".equals(ext)) { return "Video"; }
		else if ("wmv".equals(ext)) { return "Video"; }
		else if ("mov".equals(ext)) { return "Video"; }
		else if ("flv".equals(ext)) { return "Video"; }
		else if ("vdo".equals(ext)) { return "Video"; }
		else if ("vob".equals(ext)) { return "Video"; }
		else if ("ogm".equals(ext)) { return "Video"; }
		else if ("dcr".equals(ext)) { return "Video"; }
		else if ("dir".equals(ext)) { return "Video"; }
		else if ("dxr".equals(ext)) { return "Video"; }
		else if ("wrl".equals(ext)) { return "Video"; }
		else if ("3gp".equals(ext)) { return "Video"; }
		else if ("3g2".equals(ext)) { return "Video"; }
		else if ("noa".equals(ext)) { return "Video"; }
		else if ("asf".equals(ext)) { return "Video"; }
		else if ("mqv".equals(ext)) { return "Video"; }
		else if ("amc".equals(ext)) { return "Video"; }
		else if ("pdf".equals(ext)) { return "Pdf"; }
		else { return "";}
	}

	/**
	 * 組織名を取得(都道府県/市町村対応版)
	 * @param 自治体情報
	 * @return 組織名
	 */
	public static String getOrganizationName(LocalgovInfo localgovInfo) {
		// 都道府県の場合(市町村名と市町村コードが未指定)
		if ("".equals(localgovInfo.city) && "".equals(localgovInfo.citycode)) {
			return localgovInfo.pref;
		} else {
			return localgovInfo.city;
		}
	}

	/**
	 * チェックディジェット付き地方公共自治体コードを取得(都道府県/市町村対応版)
	 * @param 自治体情報
	 * @return 組織名
	 */
	public static String getLocalGovermentCode(LocalgovInfo localgovInfo) {
		String jisX402 = localgovInfo.prefcode + localgovInfo.citycode;
		// 都道府県の場合、000を付加
		if (jisX402.length() < 3) jisX402 = jisX402 + "000";
		// チェックディジェットを付加
		return jisX402 + PublicCommonsUtils.mod11(jisX402);
	}

	/**
	 * 全域を公共情報コモンズに対応する表記に変換する
	 * @param refugeInformationList 避難情報リスト
	 * @return なし(引数の避難情報リストを書き換えます)
	 */
	public static void convertAllArea(List<RefugeInformationDto> refugeInformationList){

		// 全域と個別地域が両方ある場合、「全域 (避難指示解除の地域除く) 避難勧告 発令」のように表記変更する
		// 全域の発令内容がブランクの場合、全域を除外する
		for (RefugeInformationDto dto : refugeInformationList) {
			// 全域で発令内容がブランクでない場合
			if (Constants.ALL_AREA().equals(dto.chikuName) && !"".equals(dto.hatureiKbn.trim())) {

				// 「(避難指示解除の地域除く)」を生成
				StringBuffer allOption = new StringBuffer();
				String hatureiKbn = "";
				for (RefugeInformationDto dto2 : refugeInformationList) {
					// 全域は生成対象外
					if (!Constants.ALL_AREA().equals(dto2.chikuName)) {
						hatureiKbn = Constants.ISSUE_CANCEL_NAME().equals(dto2.hatureiKbn) ? dto2.lasthatureiKbn + " " + dto2.hatureiKbn + ", " : dto2.hatureiKbn + " " + Constants.ISSUE_HATUREI_NAME() + ", ";
						// 既に生成済みの発令区分は除外
						if (allOption.indexOf(hatureiKbn) == -1) allOption.append(hatureiKbn);
					}
				}
				if (allOption.length() > 1) {
					allOption.delete(allOption.length()-2, allOption.length());
					dto.chikuNameAllArea = Constants.ALL_AREA() + lang.__("(Except {0} district)", allOption.toString());
				} else {
					dto.chikuNameAllArea = Constants.ALL_AREA();
				}
			}
		}
		return;
	}

	/**
	 * 都道府県か市区町村か判定結果を返します
	 * @param 地方自治体情報(localgov_info)の市区町村名(city)
	 * @return true:都道府県 false:市区町村
	 */
	public static boolean checkPrefecture(String city) {
		// 市区町村名が空の場合、都道府県と判定する
		if ("".equals(city)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 公共情報コモンズのドキュメントIDの頭文字を取得します
	 * @return ドキュメントIDの頭文字
	 */
	public static String getDocumentIdHead() {
		final String DEFAULT_VALUE = "act";		// 本番運用時のドキュメントID頭文字
		try{
			String val = properties.getProperty("PCOMMONS_DOCUMENT_ID_HEAD");
			if (StringUtil.isEmpty(val)) {
				return DEFAULT_VALUE;
			} else {
				return val;
			}
		} catch(Exception e) {
			e.printStackTrace();
			Logger logger = Logger.getLogger(StringUtil.class);
			logger.error(lang.__("SaigaiTask.properties : Failed to get PCOMMONS_DOCUMENT_ID_HEAD. Confirm SaigaiTask.properties."));
			return DEFAULT_VALUE;
		}
	}

	/**
	 * 発表組織に追記する事業者名を取得します
	 * @return 追記する事業者名
	 */
	public static String getCorpname() {
		final String DEFAULT_VALUE = "";

		try{
			String val = properties.getProperty("PCOMMONS_DEVELOP_CORPNAME");
			if (StringUtil.isEmpty(val)) {
				return DEFAULT_VALUE;
			} else {
				return val;
			}
		} catch(Exception e) {
			e.printStackTrace();
			Logger logger = Logger.getLogger(StringUtil.class);
			logger.error(lang.__("SaigaiTask.properties : Failed to get PCOMMONS_DEVELOP_CORPNAME. Confirm SaigaiTask.properties."));
			return DEFAULT_VALUE;
		}
	}
}
