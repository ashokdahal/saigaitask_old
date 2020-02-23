/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;

import jp.ecom_plat.saigaitask.dto.EventInformationDto;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeFile;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeFiles;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeMediaType;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeMimeType;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.event.TypeEvent;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;

/**
 * 公共情報コモンズのイベント情報サービス
 */
@org.springframework.stereotype.Service
public class PcEventService extends AbstractPublicCommonsService {

	/** PCX_ENファクトリ. イベント情報 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.event.ObjectFactory pcxEnFactory =	new jp.ne.publiccommons.xml.pcxml1._1_3.body.event.ObjectFactory();

	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;

	/**
	 * イベント情報を生成します.
	 * @param eventInformationList イベント情報リスト
	 * @return typeEvent
	 * @throws ParseException
	 */
	public TypeEvent createEventInformation(EventInformationDto eventInformationDto) throws ParseException {
		TypeEvent typeEvent = pcxEnFactory.createTypeEvent();
			typeEvent.setName(eventInformationDto.title);

			// Ver1.2以降、カテゴリ「お知らせ」を利用する場合、お知らせ発信を利用しなくてはならない
			//typeEvent.setCategory_0020(EnumTypeCategory.お知らせ);

			if (!"".equals(eventInformationDto.eventFrom)) {
				typeEvent.setFrom(PublicCommonsUtils.getXMLGregorianCalendar(DateFormat.getDateTimeInstance().parse(eventInformationDto.eventFrom)));
			}
			if (!"".equals(eventInformationDto.eventTo)) {
				typeEvent.setTo(PublicCommonsUtils.getXMLGregorianCalendar(DateFormat.getDateTimeInstance().parse(eventInformationDto.eventTo)));
			}

			typeEvent.setDurationDescription(eventInformationDto.eventFrom+"~"+eventInformationDto.eventTo);
			typeEvent.setFee(eventInformationDto.eventFee);
			typeEvent.setDescription(eventInformationDto.text);

			if (!"".equals(eventInformationDto.eventNotificationUri)) {
				typeEvent.setURL(eventInformationDto.eventNotificationUri);
			}
			if (!"".equals(eventInformationDto.eventFileUri)) {
				TypeFile typeFile = new TypeFile();
				typeFile.setURI(eventInformationDto.eventFileUri);
				//MimeTypeのXML設定 (コモンズ側で対応するMimeTypeがない場合は何もセットしない)
				if ("audio/mpeg".equals(eventInformationDto.mimeType)){
					typeFile.setMimeType(TypeMimeType.AUDIO_MPEG);
				} else if("image/jpeg".equals(eventInformationDto.mimeType)){
					typeFile.setMimeType(TypeMimeType.IMAGE_JPEG);
				} else if("text/xml".equals(eventInformationDto.mimeType)){
					typeFile.setMimeType(TypeMimeType.TEXT_XML);
				}  else if("application/pdf".equals(eventInformationDto.mimeType)){
					typeFile.setMimeType(TypeMimeType.APPLICATION_PDF);
				}

				//MediaTypeの設定
				if("Audio".equals(eventInformationDto.mediaType)){
					typeFile.setMediaType(TypeMediaType.AUDIO);
				} else if("Graphic".equals(eventInformationDto.mediaType)){
					typeFile.setMediaType(TypeMediaType.GRAPHIC);
				} else if("Pdf".equals(eventInformationDto.mediaType)){
					typeFile.setMediaType(TypeMediaType.PDF);
				} else if("Photo".equals(eventInformationDto.mediaType)){
					typeFile.setMediaType(TypeMediaType.PHOTO);
				} else if("Text".equals(eventInformationDto.mediaType)){
					typeFile.setMediaType(TypeMediaType.TEXT);
				} else if("Video".equals(eventInformationDto.mediaType)){
					typeFile.setMediaType(TypeMediaType.VIDEO);
				}

				//ファイルタイトルの設定
				if(!"".equals(eventInformationDto.eventFileCaption) && !"".equals(eventInformationDto.eventFileUri)){
					typeFile.setCaption(eventInformationDto.eventFileCaption);
				}

				TypeFiles typeFiles = new TypeFiles();
				typeFiles.getFile().add(typeFile);
				typeEvent.setAttachmentFiles(typeFiles);
			}

		return typeEvent;

	}


	/**
	 * EDXLDistributionType を生成
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param eventInformationList イベント情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistributionType
	 * @throws ParseException
	 */
	public EDXLDistributionType createEDXLDistributionType(PubliccommonsReportData publiccommonsReportData, EventInformationDto eventInformationDto, PCommonsSendDto pCommonsSendDto) throws ParseException {
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		// イベント情報を作成
		TypeEvent event = createEventInformation(eventInformationDto);
		JAXBElement<?> pcxmlJAXBElement = pcxEnFactory.createEvent(event);
		PublicCommonsUtils.setPCXML(edxlDistributionType, pcxmlJAXBElement);

		// 作成日時
		if(publiccommonsReportData.createtime != null) {
			PublicCommonsUtils.setCreateDateTime(edxlDistributionType, new Date(publiccommonsReportData.createtime.getTime()));
		}

		// タイトル
		String title = createReportHeadTitle(edxlDistributionType, pCommonsSendDto);
		PublicCommonsUtils.setTitle(edxlDistributionType, title);

		return edxlDistributionType;
	}
}
