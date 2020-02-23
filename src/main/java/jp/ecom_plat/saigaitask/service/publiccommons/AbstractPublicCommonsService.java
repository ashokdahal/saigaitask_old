/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsSendToInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1.TypeControl;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeErrata;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeOfficeInfo;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeOfficeInfos;
import jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis.TypeAreas;
import jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis.TypeHead;
import jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis.TypeHeadline;
import jp.ne.publiccommons.xml.xml.edxl.CategoryType;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import jp.ne.publiccommons.xml.xml.edxl.CommonsTargetAreaType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;
import oasis.names.tc.emergency.edxl.de._1.StatusValues;
import oasis.names.tc.emergency.edxl.de._1.TypeValues;
import oasis.names.tc.emergency.edxl.de._1.XmlContentType;

import org.seasar.framework.util.StringUtil;

/**
 * 公共情報コモンズの共通サービス
 */
@org.springframework.stereotype.Service
abstract public class AbstractPublicCommonsService extends BaseService {

	//========================================================================================
	// EDXL
	//========================================================================================
	/** EDXLファクトリ */
	public oasis.names.tc.emergency.edxl.de._1.ObjectFactory edxlFactory =
			new oasis.names.tc.emergency.edxl.de._1.ObjectFactory();
	/** コモンズEDXLファクトリ */
	public jp.ne.publiccommons.xml.xml.edxl.ObjectFactory commonsEdxlFactory =
			new jp.ne.publiccommons.xml.xml.edxl.ObjectFactory();

	//========================================================================================
	// PCXML
	//========================================================================================
	/** PCXファクトリ. ルート要素、Control部 */
	public jp.ne.publiccommons.xml.pcxml1._1.ObjectFactory pcxFactory =
			new jp.ne.publiccommons.xml.pcxml1._1.ObjectFactory();

	/** PCX_IBファクトリ. ヘッダ要素 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis.ObjectFactory pcxIbFactory =
			new jp.ne.publiccommons.xml.pcxml1._1_3.informationbasis.ObjectFactory();

	/** PCX_EBファクトリ. 共通要素 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.ObjectFactory pcxEbFactory =
			new jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.ObjectFactory();

	//========================================================================================
	// 災害対応システムサービス
	//========================================================================================
	/** 公共情報コモンズ発信データサービス */
	@Resource protected PubliccommonsReportDataService publiccommonsReportDataService;
	/** ファイルサービス */
	@Resource protected FileService fileService;
	/** 公共情報コモンズ発信先データサービス */
	@Resource protected PubliccommonsSendToInfoService publiccommonsSendToInfoService;
	/** 記録データサービス */
	@Resource protected TrackmapInfoService trackmapInfoService;
	//========================================================================================
	// メソッド
	//========================================================================================
	/**
	 * 送信システムIDを取得します.
	 * @param localgovinfoid 自治体ID
	 * @return 送信システムID
	 */
	public String getSenderId(Long localgovinfoid) {
		return localgovInfoService.findById(localgovinfoid).domain;
	}

	/**
	 * メッセージIDを取得します.
	 * @param localgovinfoid 自治体ID
	 * @return メッセージID
	 */
	public String getDistributionId(Long localgovinfoid) {
		return getSenderId(localgovinfoid)+"-"+UUID.randomUUID().toString();
	}

	/**
	 * ドキュメントIDを取得します.
	 * @param localgovinfoid 自治体ID
	 * @return ドキュメントID
	 */
	public String getDocumentId(Long localgovinfoid) {
		return getSenderId(localgovinfoid)+"-"+UUID.randomUUID().toString();
	}

	/**
	 * ドキュメントIDを取得します.
	 * @param localgovinfoid 自治体ID
	 * @param trackdataid 記録データID
	 * @param category 情報種別
	 * @param documentIdSerial ドキュメントID
	 * @return ドキュメントID
	 */
	public String getDocumentId(Long localgovinfoid, Long trackdataid, String category, int documentIdSerial) {
		return getSenderId(localgovinfoid) + "-" + publiccommonsReportDataService.getDocumentUid(trackdataid, category, documentIdSerial);
	}

	/**
	 * 対象地域
	 * @param localgovinfoid 自治体ID
	 * @return 対象地域
	 */
	public CommonsTargetAreaType getTargetArea(Long localgovinfoid) {
		CommonsTargetAreaType commonsTargetAreaType = null;
		// 自治体情報の取得
		LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
		if(localgovInfo!=null) {
			String organizationCode = PublicCommonsUtils.getLocalGovermentCode(localgovInfo);
			String organizationName;
			if(StringUtil.isNotEmpty(localgovInfo.city)){
				organizationName = localgovInfo.city;
			}else{
				organizationName = localgovInfo.pref;
			}
			commonsTargetAreaType = PublicCommonsUtils.createCommonsTargetAreaType(
					organizationName,
					null,
					organizationCode,
					null
			);
		}
		return commonsTargetAreaType;
	}

	/**
	 * 対象地域(イベント情報、お知らせ情報用対象地域)
	 * @param targetArea 場所、地域名、所在地等
	 * @return 対象地域(イベント情報、お知らせ情報用対象地域)
	 */
	public CommonsTargetAreaType getGeneralTargetArea(String targetArea) {
		CommonsTargetAreaType commonsTargetAreaType = null;
			commonsTargetAreaType = PublicCommonsUtils.createCommonsTargetAreaType(
					targetArea,
					null,
					null,
					null
			);
		return commonsTargetAreaType;
	}

	/**
	 * イベントタイトル
	 * @param localgovinfoid 自治体ID
	 * @return 対象地域
	 */
	public CommonsContentObjectType getContentObjectType(String description) {
		CommonsContentObjectType commonsContentObjectType = null;
		if(StringUtil.isNotEmpty(description)) {
			commonsContentObjectType = commonsEdxlFactory.createCommonsContentObjectType();
			commonsContentObjectType.setContentDescription(description);
		}
		return commonsContentObjectType;
	}
	/**
	 * EDXLDistributionを作成
	 * @param localgovinfoid 自治体ID
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistribution
	 */
	public EDXLDistributionType createEDXLDistribution(Long localgovinfoid, PCommonsSendDto pCommonsSendDto) {
		CommonsTargetAreaType targetArea = getTargetArea(localgovinfoid);

		// 見出し
		CommonsContentObjectType description = getContentObjectType(pCommonsSendDto.contentDescription);

		// 運用種別
		StatusValues distributionStatus = PublicCommonsUtils.getStatusValuesMap().get(pCommonsSendDto.statusValue);

		// 更新種別
		TypeValues distributionType = TypeValues.REPORT;
		if("cancelSend".equals(pCommonsSendDto.distributiontype)){	// 取消の場合には、キャンセルに設定
			distributionType = TypeValues.CANCEL;
		} else if (StringUtil.isNotEmpty(pCommonsSendDto.documentRevision)) {	// 取消以外で版数が1以上の場合には、更新に設定
			if (1 < Integer.parseInt(pCommonsSendDto.documentRevision)) {
				distributionType = TypeValues.UPDATE;
			}
		}

		EDXLDistributionType edxlDistributionType = PublicCommonsUtils.createEDXLDistribution(
				pCommonsSendDto.senderId,
				pCommonsSendDto.distributionId,
				distributionType,
				distributionStatus,
				targetArea,
				description
		);
		return edxlDistributionType;
	}

	/**
	 * 公共情報コモンズContentObjectを作成
	 * @param edxlDistributionType ディストリビューションタイプ
	 * @param publiccommonsData コモンズ送信データ
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return ContentObject
	 */
	public CommonsContentObjectType createCommonsContentObjectType(EDXLDistributionType edxlDistributionType, PubliccommonsReportData publiccommonsReportData, PCommonsSendDto pCommonsSendDto) {
		CommonsContentObjectType commonsContentObjectType = createContentObject(
				publiccommonsReportData,
				edxlDistributionType,
				pCommonsSendDto
		);
		return commonsContentObjectType;
	}

	/**
	 * 公共情報コモンズのContentObjectを生成します.
	 * @param publiccommonsReportData 公共情報コモンズ履歴情報
	 * @param edxlDistributionType EDXLDistribution(ContentObject以外の必須項目設定済み)
	 * @param pCommonsSendDto コモンズ送信情報
	 */
	public CommonsContentObjectType createContentObject(
			PubliccommonsReportData publiccommonsReportData,
			EDXLDistributionType edxlDistributionType,
			PCommonsSendDto pCommonsSendDto) {

		CategoryType category = 	CategoryType.valueOf(publiccommonsReportData.category);

		// 発表組織情報と作成組織情報を作成
		TypeOfficeInfo publishingOfficeInfo = PublicCommonsUtils.createOfficeInfo(pCommonsSendDto);
		TypeOfficeInfo publishingOfficeInfoEditorial = PublicCommonsUtils.createOfficeInfoEditorial(pCommonsSendDto);

		// ContentObject
		CommonsContentObjectType contentObjectType = commonsEdxlFactory.createCommonsContentObjectType();

		// 見出し(edxlde:contentDescription)
		contentObjectType.setContentDescription(pCommonsSendDto.contentDescription);

		// 作成組織(commons:publishingOffice)
		if(publishingOfficeInfo!=null) {
			contentObjectType.setPublishingOfficeName(publishingOfficeInfo.getOfficeName());
			contentObjectType.setPublishingOrganizationName(publishingOfficeInfo.getOrganizationName());
			contentObjectType.setPublishingOfficeID(publishingOfficeInfo.getOrganizationCode());
			contentObjectType.setPublishingOfficeDomainName(publishingOfficeInfo.getOfficeDomainName());
		}
		contentObjectType.setDocumentRevision(publiccommonsReportData.documentRevision);
		contentObjectType.setPreviousDocumentRevision(publiccommonsReportData.documentRevision - 1);
		contentObjectType.setDocumentID(publiccommonsReportData.documentId);
		contentObjectType.setCategory(category);

		// Control
		TypeControl typeControl = pcxFactory.createTypeControl();
		{
			typeControl.setTitle(category);
			typeControl.setDistributionStatus(edxlDistributionType.getDistributionStatus());

			// 発表組織(PublishingOffice)
			TypeOfficeInfos typePublishingOffice = pcxEbFactory.createTypeOfficeInfos();
			typePublishingOffice.getOfficeInfo().add(publishingOfficeInfo);
			typeControl.setPublishingOffice(typePublishingOffice);

			// 作成組織(EditorialOffice)
			typeControl.setEditorialOffice(publishingOfficeInfoEditorial);

			// 訂正・取消理由(Erratta)
			if("cancelSend".equals(pCommonsSendDto.distributiontype) || "correctionSend".equals(pCommonsSendDto.distributiontype)){
				if(StringUtil.isNotEmpty(pCommonsSendDto.description)){
					TypeErrata errata = pcxEbFactory.createTypeErrata();
					errata.setDateTime(PublicCommonsUtils.getXMLGregorianCalendar(new Date()));
					errata.setDescription(pCommonsSendDto.description);
					typeControl.setErrata(errata);
				}
			}
		}

		// Head
		TypeHead typeHead = pcxIbFactory.createTypeHead();
		{
			typeHead.setTitle(createReportHeadTitle(pCommonsSendDto, category));

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			// 作成日時(この文書が作成された日時)
			try{
		    	if (publiccommonsReportData.createtime != null) {
					XMLGregorianCalendar createDateTime = PublicCommonsUtils.getXMLGregorianCalendar(new java.util.Date(publiccommonsReportData.createtime.getTime()));
					typeHead.setCreateDateTime(createDateTime);
		    	}
			}catch (Exception e){
				e.printStackTrace();
			}

			// 初版作成日時(この文書の初版が作成された日時)
		    try {
		    	if (publiccommonsReportData.startsendtime != null) {
					XMLGregorianCalendar firstCreateDateTime = PublicCommonsUtils.getXMLGregorianCalendar(new java.util.Date(publiccommonsReportData.startsendtime.getTime()));
					typeHead.setFirstCreateDateTime(firstCreateDateTime);
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 公式発表日時(本情報の公式な発表時刻)
		    try {
		    	if (publiccommonsReportData.reporttime != null) {
					XMLGregorianCalendar reportDateTime = PublicCommonsUtils.getXMLGregorianCalendar(new java.util.Date(pCommonsSendDto.reporttime.getTime()));
					typeHead.setReportDateTime(reportDateTime);
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 希望公開開始日時(伝達者に希望する公開開始日時)
		    try {
		    	if (publiccommonsReportData.targetdatetime != null) {
					XMLGregorianCalendar targetDateTime = PublicCommonsUtils.getXMLGregorianCalendar((new java.util.Date(publiccommonsReportData.targetdatetime.getTime())));
					typeHead.setTargetDateTime(targetDateTime);
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 希望公開終了日時
		    try {
		    	if (StringUtil.isNotEmpty(pCommonsSendDto.validdatetime)) {
		    		XMLGregorianCalendar target = PublicCommonsUtils.getXMLGregorianCalendar(dateFormat.parse(pCommonsSendDto.validdatetime));
		    		typeHead.setValidDateTime(target);
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}

			typeHead.setDistributionID(edxlDistributionType.getDistributionID());
			typeHead.setDistributionType(edxlDistributionType.getDistributionType());
			typeHead.setDocumentRevision(publiccommonsReportData.documentRevision);
			typeHead.setPreviousDocumentRevision(publiccommonsReportData.documentRevision - 1);
			typeHead.setDocumentID(publiccommonsReportData.documentId);

			// Headline
			TypeHeadline typeHeadline = new TypeHeadline();
			typeHeadline.setText(pCommonsSendDto.contentDescription);

			CommonsTargetAreaType commonsTargetAreaType = null;
			TypeAreas typeAreas = new TypeAreas();

			if(PubliccommonsSendType.EVENT.equals(pCommonsSendDto.categoryType) || PubliccommonsSendType.GENERAL_INFORMATION.equals(pCommonsSendDto.categoryType)){
				String commaReplace =  pCommonsSendDto.area.replaceAll("，",",");
				String[] area = commaReplace.split(",");
				for (int i = 0; i < area.length; i++) {
					commonsTargetAreaType = PublicCommonsUtils.createCommonsTargetAreaType(area[i],null,null,null);
					typeAreas.getArea().add(commonsTargetAreaType);
				}
			}else{
				commonsTargetAreaType = getTargetArea(publiccommonsReportData.localgovinfoid);
				typeAreas.getArea().add(commonsTargetAreaType);
			}
			typeHeadline.setAreas(typeAreas);
			typeHead.setHeadline(typeHeadline);
		}

		// XMLContent
		XmlContentType xmlContentType = PublicCommonsUtils.createXmlContentType(typeControl, typeHead, null);
		contentObjectType.setXmlContent(xmlContentType);

		return contentObjectType;
	}

	/**
	 * カテゴリからタイトルを生成します.
	 * @param edxlDistributionType
	 * @param コモンズ送信情報
	 * @return タイトル
	 */
	public String createReportHeadTitle(EDXLDistributionType edxlDistributionType, PCommonsSendDto pCommonsSendDto) {
		CategoryType categoryType = PublicCommonsUtils.getCategoryType(edxlDistributionType);
		return createReportHeadTitle(pCommonsSendDto, categoryType);
	}

	/**
	 * 発表組織名とカテゴリからタイトルを生成します.
	 * @param edxlDistributionType
	 * @param コモンズ送信情報
	 * @return タイトル
	 */
	public String createReportHeadTitle(PCommonsSendDto pCommonsSendDto, CategoryType categoryType) {
		String title = null;
		if (CategoryType.EVACUATION_ORDER.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Evacuation advisory, order info");
		} else if (CategoryType.SHELTER.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Shelter info");
		} else if (CategoryType.URGENT_MAIL.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Emergency e-mail");
		} else if (CategoryType.EVENT.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Event info");
		} else if (CategoryType.DAMAGE_INFORMATION.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Damage info");
		} else if (CategoryType.GENERAL_INFORMATION.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Info<!--3-->");
		}else if (CategoryType.ANTIDISASTER_HEADQUARTER.equals(categoryType)) {
			title = pCommonsSendDto.organizationName + ": " + lang.__("Disaster response HQ situation");
		}
		return title;
	}

	/**
	 * ファイルを取得します.
	 * @param publiccommonsData コモンズ送信データ
	 * @return 送信ファイル
	 */
	public File getFile(PubliccommonsReportData publiccommonsData) {
		Long localgovinfoid = publiccommonsData.localgovinfoid;
		String fileName = publiccommonsReportDataService.getFileName(publiccommonsData);
		return fileService.getPubliccommonsXMLFile(localgovinfoid, fileName);
	}

	/**
	 * ファイルを生成します.
	 * @param publiccommonsData コモンズ送信データ
	 * @return 送信ファイル
	 */
	public File createFile(PubliccommonsReportData publiccommonsData) {
		File file = getFile(publiccommonsData);
		fileService.createFile(file);
		return file;
	}
}
