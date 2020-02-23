/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.ShelterInformationDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.EnumTypeHouseholdsUnitAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.TypeHouseholds;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.TypeInformation;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.TypeLocation;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.TypeNumberOf;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.TypeShelter;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.EnumTypeOfficeInfoContactInfoAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeContactInfo;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDisaster;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;

import org.seasar.framework.util.StringUtil;


/**
 * 公共情報コモンズの避難所情報サービス
 */
@org.springframework.stereotype.Service
public class PcShelterService extends AbstractPublicCommonsService {

	/** PCX_SHファクトリ. 避難所情報 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.ObjectFactory pcxShFactory = new jp.ne.publiccommons.xml.pcxml1._1_3.body.shelter.ObjectFactory();

	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;

	/**
	 * EDXLDistributionType を生成
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param shelterInformationList 避難所情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistributionType
	 */
	public EDXLDistributionType createEDXLDistributionType(PubliccommonsReportData publiccommonsReportData, List<ShelterInformationDto> shelterInformationList, PCommonsSendDto pCommonsSendDto) {
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		// 避難所情報を作成
		TypeShelter typeShelter = createShelter(shelterInformationList, pCommonsSendDto);
		JAXBElement<?> pcxmlJAXBElement = getPCXMLJAXBElement(typeShelter);
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

	/**
	 * 避難所情報を生成します.
	 * @param shelterInformationList 避難所情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return TypeShelter
	 */
	private TypeShelter createShelter(List<ShelterInformationDto> shelterInformationList , PCommonsSendDto pCommonsSendDto) {
		TypeShelter typeShelter = pcxShFactory.createTypeShelter();

		// 災害名称
		if (StringUtil.isNotEmpty(pCommonsSendDto.trackdataname)) {
			TypeDisaster typeDisaster = pcxEbFactory.createTypeDisaster();
			typeDisaster.setDisasterName(pCommonsSendDto.trackdataname);
			typeShelter.setDisaster(typeDisaster);
		}

		// 補足情報
		if(StringUtil.isNotEmpty(pCommonsSendDto.complementaryInfo)) typeShelter.setComplementaryInfo(pCommonsSendDto.complementaryInfo);;

		typeShelter.setInformations(pcxShFactory.createTypeInformations());

		BigInteger headCountTotal = new BigInteger("0");
		BigInteger headCountVoluntaryTotal = new BigInteger("0");
		BigInteger houseHoldsTotal = new BigInteger("0");
		BigInteger houseHoldsVoluntaryTotal = new BigInteger("0");
		for (ShelterInformationDto cur : shelterInformationList) {
			String shelterType = cur.type;						// 避難所種別
			String shelterTypeDetail = cur.typeDetail;		// 避難所種別 詳細
			String shelterName = cur.shelterName;			// 避難所名
			String shelterAddress = cur.shelterAddress;	// 住所
			String shelterCircle = cur.circle;						// 座標
			String shelterPhone = cur.shelterPhone;			// 電話番号
			String shelterFax = cur.shelterFax;					// FAX
			String shelterStaff = cur.shelterStaff;				// 担当者
			String shelterCapacity = cur.shelterCapacity;  // 最大収容人数
			String shelterStatus = cur.shelterStatus;		// 開設状況
			Timestamp setupTime = cur.setupTime;		// 開設時間
			Timestamp closeTime = cur.closeTime;			// 閉鎖時間
			String headCount = cur.headCount;				// 避難人数
			String headCountVoluntary = cur.headCountVoluntary;// 避難人数(うち自主避難)
			String houseHolds = cur.houseHolds;				// 避難世帯数
			String houseHoldsVoluntary = cur.houseHoldsVoluntary;// 避難世帯数(うち自主避難)
			BigInteger headCountBig = new BigInteger("0");
			BigInteger headCountVoluntaryBig = new BigInteger("0");
			BigInteger houseHoldsBig = new BigInteger("0");
			BigInteger houseHoldsVoluntaryBig = new BigInteger("0");

			TypeLocation location = pcxShFactory.createTypeLocation();
			TypeInformation information = pcxShFactory.createTypeInformation();

			// 避難所情報 避難所名
			location.setAreaName(shelterName);
			// 避難所情報 住所
			if (StringUtil.isNotEmpty(shelterAddress)) location.setAddress(shelterAddress);
			// 避難所情報 座標
			if (StringUtil.isNotEmpty(shelterCircle)) location.getCircle().add(shelterCircle);
			// 避難所情報 電話番号
			if (StringUtil.isNotEmpty(shelterPhone)) {
				TypeContactInfo typeContactInfo = pcxEbFactory.createTypeContactInfo();
				typeContactInfo.setContactType(EnumTypeOfficeInfoContactInfoAttr.PHONE);
				typeContactInfo.setValue(shelterPhone);
				location.getContactInfo().add(typeContactInfo);
			}
			// 避難所情報 FAX
			if (StringUtil.isNotEmpty(shelterFax)) {
				TypeContactInfo typeContactInfo = pcxEbFactory.createTypeContactInfo();
				typeContactInfo.setContactType(EnumTypeOfficeInfoContactInfoAttr.FAX);
				typeContactInfo.setValue(shelterFax);
				location.getContactInfo().add(typeContactInfo);
			}
			// 避難所情報 担当者
			if (StringUtil.isNotEmpty(shelterStaff)) {
				TypeContactInfo typeContactInfo = pcxEbFactory.createTypeContactInfo();
				typeContactInfo.setContactType(EnumTypeOfficeInfoContactInfoAttr.PERSON_RESPONSIBLE);
				typeContactInfo.setValue(shelterStaff);
				location.getContactInfo().add(typeContactInfo);
			}
			// 避難所情報
			information.setLocation(location);

			// 避難所種別
			information.setType(PublicCommonsUtils.getTypeMap().get(shelterType));

			// 避難所種別 詳細
			if (StringUtil.isNotEmpty(shelterTypeDetail)) information.setTypeDetail(shelterTypeDetail);

			// 開設状況
			information.setSort(PublicCommonsUtils.getSortMap().get(shelterStatus));

			// 開設・閉鎖日時(画面で入力された日時をセット)
			Date date = null;
			//date = new Timestamp(System.currentTimeMillis());
			if (Constants.SHELTER_CLOSE_NAME().equals(shelterStatus)) {
				date = closeTime;
			} else if (Constants.SHELTER_OPEN().equals(shelterStatus)) {
				date = setupTime;
			}
			if (date != null) {
				XMLGregorianCalendar dateTime = PublicCommonsUtils.getXMLGregorianCalendar(date);
				information.setDateTime(dateTime);
			}

			// 避難人数
			TypeNumberOf tno = new TypeNumberOf();
			try {
				if (StringUtil.isNotEmpty(headCount)) {
					headCountBig = BigInteger.valueOf(Long.valueOf(headCount));
					tno.setHeadCount(headCountBig);
					headCountTotal = headCountTotal.add(headCountBig);
				}
			} catch(NumberFormatException e) {
			}
			// 避難人数(うち自主避難)
			try {
				if (StringUtil.isNotEmpty(headCountVoluntary)) {
					headCountVoluntaryBig = BigInteger.valueOf(Long.valueOf(headCountVoluntary));
					tno.setHeadCountVoluntary(headCountVoluntaryBig);
					headCountVoluntaryTotal = headCountVoluntaryTotal.add(headCountVoluntaryBig);;	// 総数
				}
			} catch(NumberFormatException e) {
			}
			// 避難世帯数
			TypeHouseholds thh = new TypeHouseholds();
			thh.setUnit(EnumTypeHouseholdsUnitAttr.世帯);
			try {
				if (StringUtil.isNotEmpty(houseHolds)) {
					houseHoldsBig = BigInteger.valueOf(Long.valueOf(houseHolds));
					thh.setValue(houseHoldsBig);
					tno.setHouseholds(thh);
					houseHoldsTotal = houseHoldsTotal.add(houseHoldsBig);	// 総数
				}
			} catch(NumberFormatException e) {
			}
			// 避難世帯数(うち自主避難)
			thh = new TypeHouseholds();
			thh.setUnit(EnumTypeHouseholdsUnitAttr.世帯);
			try {
				if (StringUtil.isNotEmpty(houseHoldsVoluntary)) {
					houseHoldsVoluntaryBig = BigInteger.valueOf(Long.valueOf(houseHoldsVoluntary));
					thh.setValue(houseHoldsVoluntaryBig);
					tno.setHouseholdsVoluntary(thh);
					houseHoldsVoluntaryTotal = houseHoldsVoluntaryTotal.add(houseHoldsVoluntaryBig);	// 総数
				}
			} catch(NumberFormatException e) {
			}
			if (tno.getHeadCount() != null ||
					tno.getHeadCountVoluntary() != null ||
					tno.getHouseholds() != null ||
					tno.getHouseholdsVoluntary() != null ) information.setNumberOf(tno);

			// 最大収容人数
			try {
				// 0人または未入力時は項目自体をセットしない
				if (StringUtil.isNotEmpty(shelterCapacity) && Long.valueOf(shelterCapacity) > 0) {
					long val = Long.valueOf(shelterCapacity);
					information.setCapacity(BigInteger.valueOf(val));
				}
			} catch(NumberFormatException e) {
				//information.setCapacity(BigInteger.valueOf(0));
			}

			typeShelter.getInformations().getInformation().add(information);
		}

		// 総避難人数、総避難人数(うち自主避難)
		TypeNumberOf tno = new TypeNumberOf();
		tno.setHeadCount(headCountTotal);
		tno.setHeadCountVoluntary(headCountVoluntaryTotal);

		// 総避難世帯数
		TypeHouseholds thh = new TypeHouseholds();
		thh.setUnit(EnumTypeHouseholdsUnitAttr.世帯);
		thh.setValue(houseHoldsTotal);
		tno.setHouseholds(thh);

		// 総避難世帯数(うち自主避難)
		thh = new TypeHouseholds();
		thh.setUnit(EnumTypeHouseholdsUnitAttr.世帯);
		thh.setValue(houseHoldsVoluntaryTotal);
		tno.setHouseholdsVoluntary(thh);

		if (tno.getHeadCount() != null ||
				tno.getHeadCountVoluntary() != null ||
				tno.getHouseholds() != null ||
				tno.getHouseholdsVoluntary() != null ) typeShelter.setTotalNumber(tno);

		// 開設状況でソート
//		final List<EnumTypeSort> order = new ArrayList<EnumTypeSort>();
//		order.add(EnumTypeSort.開設);
//		order.add(EnumTypeSort.常設);
//		order.add(EnumTypeSort.閉鎖);
//		order.add(EnumTypeSort.未開設);
//		order.add(EnumTypeSort.不明);
//		Collections.sort(typeShelter.getInformations().getInformation(), new Comparator<TypeInformation>() {
//			@Override
//			public int compare(TypeInformation o1, TypeInformation o2) {
//				int o1Idx = order.indexOf(o1.getSort());
//				int o2Idx = order.indexOf(o2.getSort());
//				return o1Idx<o2Idx ? -1 : o1Idx==o2Idx ? 0 : 1;
//			}
//		});

		return typeShelter;
	}

	/**
	 * @param typeShelter
	 * @return
	 */
	private JAXBElement<?> getPCXMLJAXBElement(TypeShelter typeShelter) {
		return pcxShFactory.createShelter(typeShelter);
	}
}
