/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.util.Date;

import javax.xml.bind.JAXBElement;

import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.urgentmail.TypeInformation;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.urgentmail.TypeUrgentMail;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;
import oasis.names.tc.emergency.edxl.de._1.ValueListType;

/**
 * 公共情報コモンズの緊急速報メールサービス
 */
@org.springframework.stereotype.Service
public class PcUrgentMailService extends AbstractPublicCommonsService {

	/** PCX_UMファクトリ. 緊急速報メール情報 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.urgentmail.ObjectFactory pcxUmFactory =
			new jp.ne.publiccommons.xml.pcxml1._1_3.body.urgentmail.ObjectFactory();

	/**
 	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param title タイトル
	 * @param message メッセージ
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return edxlDistributionType
	 */
	public EDXLDistributionType createEDXLDistribution(PubliccommonsReportData publiccommonsReportData, String title, String message, PCommonsSendDto pCommonsSendDto) {
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		TypeUrgentMail typeUrgentMail = pcxUmFactory.createTypeUrgentMail();
		TypeInformation typeInformation = pcxUmFactory.createTypeInformation();
		typeInformation.setTitle(title);
		typeInformation.setMessage(message);
		typeUrgentMail.getInformation().add(typeInformation);

		JAXBElement<?> pcxmlJAXBElement = pcxUmFactory.createInformation(typeInformation);
		JAXBElement<?> pcxmlJAXBElement2 = pcxUmFactory.createUrgentMail(typeUrgentMail);

		PublicCommonsUtils.setPCXML(edxlDistributionType, pcxmlJAXBElement);
		PublicCommonsUtils.setPCXML(edxlDistributionType, pcxmlJAXBElement2);

		// 作成日時
		if(publiccommonsReportData.createtime != null) {
			PublicCommonsUtils.setCreateDateTime(edxlDistributionType, new Date(publiccommonsReportData.createtime.getTime()));
		}

		return edxlDistributionType;
	}

	/**
	 * @param edxlDistributionType
	 * @param docomo 成功:true, 失敗:false
	 * @param au 成功:true, 失敗:false
	 * @param softbank 成功:true, 失敗:false
	 */
	public void setCarrier(EDXLDistributionType edxlDistributionType, boolean docomo, boolean au, boolean softbank) {
		CommonsContentObjectType commonsContentObjectType = PublicCommonsUtils.getCommonsContentObject(edxlDistributionType);
		// キャリアを設定
		ValueListType valueListType = edxlFactory.createValueListType();
		valueListType.setValueListUrn("publicCommons:media:urgentmail:carrier");
		if(docomo) valueListType.getValue().add("UMC1");
		if(au) valueListType.getValue().add("UMC2");
		if(softbank) valueListType.getValue().add("UMC3");

		commonsContentObjectType.getConsumerRole().clear();
		commonsContentObjectType.getConsumerRole().add(valueListType);
	}
}
