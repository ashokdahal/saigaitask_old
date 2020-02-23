/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.AntidisasterInformationDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.service.StationService;
import jp.ecom_plat.saigaitask.service.db.StationMasterService;
import jp.ecom_plat.saigaitask.service.db.StationclassInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter.EnumTypeStatus;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter.EnumTypeType;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter.TypeAntidisasterHeadquarter;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDisaster;
import jp.ne.publiccommons.xml.xml.edxl.CategoryType;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;

import org.seasar.framework.util.StringUtil;

/**
 * 公共情報コモンズの本部設置状況サービス
 */
@org.springframework.stereotype.Service
public class PcAntidisasterService extends AbstractPublicCommonsService {

	/** PCX_AHファクトリ. 本部設置状況 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter.ObjectFactory pcxAhFactory =	new jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter.ObjectFactory();

	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;

	@Resource protected StationService stationService;

	@Resource protected StationMasterService stationMasterService;

	/** 体制区分情報サービス */
	@Resource protected StationclassInfoService stationclassInfoService;

	/**
	 * 災害対策本部設置状況を生成します.
	 * @param antidisasterInformationList 災害対策本部設置状況リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return TypeEvacuationOrder
	 * @throws ParseException
	 */
	public TypeAntidisasterHeadquarter createAntidisasterInformation(AntidisasterInformationDto antidisasterInformationDto , PCommonsSendDto pCommonsSendDto) throws ParseException {
		TypeAntidisasterHeadquarter typeAntidisasterHeadquarter = pcxAhFactory.createTypeAntidisasterHeadquarter();

		// 災害名称
		if (StringUtil.isNotEmpty(pCommonsSendDto.trackdataname)) {
			TypeDisaster typeDisaster = pcxEbFactory.createTypeDisaster();
			typeDisaster.setDisasterName(pCommonsSendDto.trackdataname);
			typeAntidisasterHeadquarter.setDisaster(typeDisaster);
		}

		// 本部種別
		EnumTypeType enumTypeType = EnumTypeType.警戒本部;
		// 設置状況 (設置or解散)
		EnumTypeStatus enumTypeStatus = EnumTypeStatus.設置;
		// 本部名称
		String name = antidisasterInformationDto.name;

		if ( EnumTypeType.対策本部.toString().equals(antidisasterInformationDto.antidisasterKbn )){
			enumTypeType = EnumTypeType.対策本部;
		// 解散の場合は前回発信本部種別を取得
		} else if (Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(antidisasterInformationDto.antidisasterKbn )){
			if ( EnumTypeType.対策本部.toString().equals(antidisasterInformationDto.lastAntidisasterKbn )){
				enumTypeType = EnumTypeType.対策本部;
			}
			enumTypeStatus = EnumTypeStatus.解散;
		}

		typeAntidisasterHeadquarter.setType(enumTypeType);
		typeAntidisasterHeadquarter.setStatus(enumTypeStatus);
		typeAntidisasterHeadquarter.setName(name);

		// 設置・解散日時
		try {
			XMLGregorianCalendar xmlGregorianCalendar = PublicCommonsUtils.getXMLGregorianCalendar(DateFormat.getDateTimeInstance().parse(antidisasterInformationDto.getHatureiDateTime()));
			typeAntidisasterHeadquarter.setDateTime(xmlGregorianCalendar);
		} catch (Exception e){
			e.printStackTrace();
		}

		// 補足情報
		if(StringUtil.isNotEmpty(pCommonsSendDto.complementaryInfo)) typeAntidisasterHeadquarter.setComplementaryInfo(pCommonsSendDto.complementaryInfo);


		return typeAntidisasterHeadquarter;
	}

	/**
	 * 本部設置状況前回発信本部種別を取得
	 * @param trackdataid 記録データID
	 * @param localgovinfoid 自治体ID
	 * @param targetArea 取得する地区名
	 * @return 前回発表時の発表区分
	 */
	public String getLastAntidisasterHeadquarter(Long trackdataid, Long localgovinfoid){
			// 公共情報コモンズ本部設置状況最終送信履歴管理テーブルを検索
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataWithLast(CategoryType.ANTIDISASTER_HEADQUARTER.toString(), trackdataid, null,"");
			for (PubliccommonsReportData publiccommonsReportData : publiccommonsReportDataList) {
				// 前回が解散でない場合、その本部種別を返す
				if (!Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(publiccommonsReportData.publiccommonsReportDataLastAntidisaster.antidisasterkbn)) {
					return publiccommonsReportData.publiccommonsReportDataLastAntidisaster.antidisasterkbn;
				}
			}

		return "";
	}

	/**
	 * 本部設置状況前回発表区分から、発令or解除を取得
	 * @param trackdataid 記録データID
	 * @param localgovinfoid 自治体ID
	 * @param targetArea 取得する地区名
	 * @return 前回発表時の発表区分が解除なら、解除を返す
	 */
	public String getIssueOrLiftEvacuationOrderByArea(Long trackdataid, Long localgovinfoid){
			// 公共情報コモンズ本部設置状況最終送信履歴管理テーブルを検索
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataWithLast(CategoryType.EVACUATION_ORDER.toString(), trackdataid, null, "");
			if(publiccommonsReportDataList != null && publiccommonsReportDataList.size() > 0){
				String issueorlift = publiccommonsReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(0).issueorlift;
				String hatureikbnLast = publiccommonsReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(0).hatureikbn;
				// 前回が解散なら、発表区分解散を返す
				if (Constants.ISSUE_CANCEL_NAME().equals(issueorlift)) {
					return hatureikbnLast;
				}
			}
		return "";
	}



	/**
	 * PCXMLのJAXBElemntに変換します.
	 * @param evacuationOrder
	 * @return
	 */
	public JAXBElement<TypeAntidisasterHeadquarter> getPCXMLJAXBElement(TypeAntidisasterHeadquarter antidisasterHeadquarter) {
		return pcxAhFactory.createAntidisasterHeadquarter(antidisasterHeadquarter);
	}

	/**
	 * EDXLDistributionType を生成
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param antidisasterInformationList 災害対策本部設置状況リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistributionType
	 * @throws ParseException
	 */
	public EDXLDistributionType createEDXLDistributionType(PubliccommonsReportData publiccommonsReportData, AntidisasterInformationDto antidisasterInformationDto, PCommonsSendDto pCommonsSendDto) throws ParseException {
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		// 災害対策本部設置状況を作成
		TypeAntidisasterHeadquarter antidisasterInformation = createAntidisasterInformation(antidisasterInformationDto, pCommonsSendDto);
		JAXBElement<?> pcxmlJAXBElement = getPCXMLJAXBElement(antidisasterInformation);
		PublicCommonsUtils.setPCXML(edxlDistributionType, pcxmlJAXBElement);

		// 作成日時
		if(publiccommonsReportData.createtime != null) {
			PublicCommonsUtils.setCreateDateTime(edxlDistributionType, new Date(publiccommonsReportData.createtime.getTime()));
		}

		String issueorlift = "";
		if(Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(pCommonsSendDto.antidisasterInformationDto.antidisasterKbn)){
			issueorlift = EnumTypeStatus.解散.toString();
		}else{
			issueorlift = EnumTypeStatus.設置.toString();
		}

		// タイトル
		String title = createReportHeadTitle(edxlDistributionType, pCommonsSendDto) + " " + issueorlift;
		PublicCommonsUtils.setTitle(edxlDistributionType, title);

		return edxlDistributionType;
	}

	/**
	 * コモンズ側で表示する本部名称を取得
	 * @return 解散or本部名称
	 */
	public AntidisasterInformationDto getAntidisasterHeadquarter(){
		AntidisasterInformationDto dto = new AntidisasterInformationDto();

		// 現在の体制を取得
		StationclassInfo stationclassInfo = stationService.getLoginCurrentStationclassInfo();
		if (stationclassInfo != null) {
			// 体制ID
			int stationId = stationclassInfo.stationid;

			// 体制名(本部名称)
			dto.name = stationclassInfo.name;

			// 移行日時(設置・解散日時)
			String hatureiDateTime = stationService.getLoginCurrentStationShifttime();
			dto.hatureiDateTime =  null;
			if (StringUtil.isNotEmpty(hatureiDateTime)) {
				try{
					dto.hatureiDateTime =  new Timestamp(new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(hatureiDateTime).getTime());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 0なら解散
			if (stationId == 0){
				dto.antidisasterKbn = Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME();
			} else {
				// IDからコモンズでの本部種別を取得する。
				dto.antidisasterKbn = stationMasterService.getNameFindById(stationId);
			}
			return dto;
		} else {
			return null;
		}
	}

}
