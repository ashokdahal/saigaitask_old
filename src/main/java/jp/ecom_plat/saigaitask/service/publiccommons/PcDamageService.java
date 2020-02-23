/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;

import jp.ecom_plat.saigaitask.dto.DamageInformationDto;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.service.db.DisasterbuildDataService;
import jp.ecom_plat.saigaitask.service.db.DisastercasualtiesDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterfarmDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterfireDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhospitalDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhouseDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhouseholdDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhouseregidentDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterlifelineDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterroadDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterschoolDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterwelfareDataService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeBuildingDamageCounterBuildingDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeDamageCounterCounterUnitAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeFireDamageCounterFireDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeHouseDamageCounterHouseDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeHumanDamageCounterHumanDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeLossesDamageCounterLossesDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeOtherDamageCounterOtherDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeSuffererDamageCounterSuffererDamageTypeAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.EnumTypeTotalLossesCurrencyUnitAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeBuildingDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeBuildingDamages;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeCategorizedLosses;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeDamageInformation;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeFireDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeFireDamages;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeFirefighter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeHouseDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeHouseDamages;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeHumanDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeHumanDamages;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeLosses;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeLossesDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeOtherDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeOtherDamages;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeSuffererDamageCounter;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeSufferers;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.TypeTotalLosses;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDisaster;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;

import org.seasar.framework.util.StringUtil;

/**
 * 公共情報コモンズの被害情報サービス
 */
@org.springframework.stereotype.Service
public class PcDamageService extends AbstractPublicCommonsService {

	/** PCX_DIファクトリ. 被害情報 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.ObjectFactory pcxDiFactory =	new jp.ne.publiccommons.xml.pcxml1._1_3.body.damageinformation.ObjectFactory();

	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;

	/** 被害サービス */
	@Resource
	protected DisastercasualtiesDataService disastercasualtiesDataService;
	@Resource
	protected DisasterhouseDataService disasterhouseDataService;
	@Resource
	protected DisasterhouseholdDataService disasterhouseholdDataService;
	@Resource
	protected DisasterhouseregidentDataService disasterhouseregidentDataService;
	@Resource
	protected DisasterroadDataService disasterroadDataService;
	@Resource
	protected DisasterlifelineDataService disasterlifelineDataService;
	@Resource
	protected DisasterhospitalDataService disasterhospitalDataService;
	@Resource
	protected DisasterfarmDataService disasterfarmDataService;
	@Resource
	protected DisasterwelfareDataService disasterwelfareDataService;
	@Resource
	protected DisasterschoolDataService disasterschoolDataService;
	@Resource
	protected DisasterbuildDataService disasterbuildDataService;
	@Resource
	protected DisasterfireDataService disasterfireDataService;

	/**
	 * 被害情報を生成します.
	 * @param damageInformationList 被害情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return TypeDamageInformation
	 * @throws ParseException
	 */
	public TypeDamageInformation createDamageInformation(DamageInformationDto damageInformationDto , PCommonsSendDto pCommonsSendDto){
		TypeDamageInformation typeDamage = pcxDiFactory.createTypeDamageInformation();

		// 災害名称
		if (StringUtil.isNotEmpty(pCommonsSendDto.trackdataname)) {
			TypeDisaster typeDisaster = pcxEbFactory.createTypeDisaster();
			typeDisaster.setDisasterName(pCommonsSendDto.trackdataname);
			typeDamage.setDisaster(typeDisaster);
		}

		// 補足情報
		if(StringUtil.isNotEmpty(pCommonsSendDto.complementaryInfo)) typeDamage.setComplementaryInfo(pCommonsSendDto.complementaryInfo);

		BigInteger val;

		// 被害数値のセット開始(nullと未入力はXMLに出力しない、0は出力する)
		// 人的被害
		TypeHumanDamages typeHumanDamages = new TypeHumanDamages();
		TypeHumanDamageCounter typeHumanDamageCounter = pcxDiFactory.createTypeHumanDamageCounter();

		// 人的被害 - 死者
		if (damageInformationDto.deadPeople != null && !"".equals(damageInformationDto.deadPeople)) {
			val = new BigInteger(damageInformationDto.deadPeople);
			typeHumanDamageCounter = pcxDiFactory.createTypeHumanDamageCounter();
			typeHumanDamageCounter.setHumanDamageType(EnumTypeHumanDamageCounterHumanDamageTypeAttr.死者);
			typeHumanDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHumanDamageCounter.setValue(val); //人数
			typeHumanDamages.getHumanDamage().add(typeHumanDamageCounter);
		}

		// 人的被害 - 行方不明者数
		if (damageInformationDto.missingPeople != null && !"".equals(damageInformationDto.missingPeople)) {
			val = new BigInteger(damageInformationDto.missingPeople);
			typeHumanDamageCounter = pcxDiFactory.createTypeHumanDamageCounter();
			typeHumanDamageCounter.setHumanDamageType(EnumTypeHumanDamageCounterHumanDamageTypeAttr.行方不明者数);
			typeHumanDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHumanDamageCounter.setValue(val); //人数
			typeHumanDamages.getHumanDamage().add(typeHumanDamageCounter);
		}

		// 人的被害 - 負傷者_重傷
		if (damageInformationDto.seriouslyInjuredPeople != null && !"".equals(damageInformationDto.seriouslyInjuredPeople)) {
			val = new BigInteger(damageInformationDto.seriouslyInjuredPeople);
			typeHumanDamageCounter = pcxDiFactory.createTypeHumanDamageCounter();
			typeHumanDamageCounter.setHumanDamageType(EnumTypeHumanDamageCounterHumanDamageTypeAttr.負傷者_重傷);
			typeHumanDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHumanDamageCounter.setValue(val); //人数
			typeHumanDamages.getHumanDamage().add(typeHumanDamageCounter);
		}

		// 人的被害 - 負傷者_軽傷
		if (damageInformationDto.slightlyInjuredPeople != null && !"".equals(damageInformationDto.slightlyInjuredPeople)) {
			val = new BigInteger(damageInformationDto.slightlyInjuredPeople);
			typeHumanDamageCounter = pcxDiFactory.createTypeHumanDamageCounter();
			typeHumanDamageCounter.setHumanDamageType(EnumTypeHumanDamageCounterHumanDamageTypeAttr.負傷者_軽傷);
			typeHumanDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHumanDamageCounter.setValue(val); //人数
			typeHumanDamages.getHumanDamage().add(typeHumanDamageCounter);
		}
		// セット
		if (typeHumanDamages.getHumanDamage().size() > 0) typeDamage.setHumanDamages(typeHumanDamages);

		// 非住家被害
		TypeBuildingDamages typeBuildingDamages  = new TypeBuildingDamages();
		TypeBuildingDamageCounter typeBuildingDamageCounter = pcxDiFactory.createTypeBuildingDamageCounter();

		// 非住家被害 - 公共建物
		if (damageInformationDto.publicBuilding != null && !"".equals(damageInformationDto.publicBuilding)) {
			val = new BigInteger(damageInformationDto.publicBuilding);
			typeBuildingDamageCounter.setBuildingDamageType(EnumTypeBuildingDamageCounterBuildingDamageTypeAttr.公共建物);
			typeBuildingDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeBuildingDamageCounter.setValue(val);
			typeBuildingDamages.getBuildingDamage().add(typeBuildingDamageCounter);
		}

		// 非住家被害 - その他
		if (damageInformationDto.otherBuilding != null && !"".equals(damageInformationDto.otherBuilding)) {
			val = new BigInteger(damageInformationDto.otherBuilding);
			typeBuildingDamageCounter = pcxDiFactory.createTypeBuildingDamageCounter();
			typeBuildingDamageCounter.setBuildingDamageType(EnumTypeBuildingDamageCounterBuildingDamageTypeAttr. その他);
			typeBuildingDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeBuildingDamageCounter.setValue(val);
			typeBuildingDamages.getBuildingDamage().add(typeBuildingDamageCounter);
		}
		// セット
		if (typeBuildingDamages.getBuildingDamage().size() > 0) typeDamage.setBuildingDamages(typeBuildingDamages);

		// 住家被害
		TypeHouseDamages typeHouseDamages = new  TypeHouseDamages();
		TypeHouseDamageCounter typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();

		// 住家被害 - 全壊 世帯
		if (damageInformationDto.totalCollapseHousehold != null && !"".equals(damageInformationDto.totalCollapseHousehold)) {
			val = new BigInteger(damageInformationDto.totalCollapseHousehold);
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.全壊);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.世帯);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 全壊 人
		if (damageInformationDto.totalCollapseHuman != null && !"".equals(damageInformationDto.totalCollapseHuman)) {
			val = new BigInteger(damageInformationDto.totalCollapseHuman);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.全壊);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 全壊 棟
		if (damageInformationDto.totalCollapseBuilding != null && !"".equals(damageInformationDto.totalCollapseBuilding)) {
			val = new BigInteger(damageInformationDto.totalCollapseBuilding);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.全壊);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 半壊 世帯
		if (damageInformationDto.halfCollapseHousehold != null && !"".equals(damageInformationDto.halfCollapseHousehold)) {
			val = new BigInteger(damageInformationDto.halfCollapseHousehold);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.半壊);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.世帯);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 半壊 人
		if (damageInformationDto.halfCollapseHuman != null && !"".equals(damageInformationDto.halfCollapseHuman)) {
			val = new BigInteger(damageInformationDto.halfCollapseHuman);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.半壊);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 半壊 棟
		if (damageInformationDto.halfCollapseBuilding != null && !"".equals(damageInformationDto.halfCollapseBuilding)) {
			val = new BigInteger(damageInformationDto.halfCollapseBuilding);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.半壊);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 一部破損 世帯
		if (damageInformationDto.someCollapseHousehold != null && !"".equals(damageInformationDto.someCollapseHousehold)) {
			val = new BigInteger(damageInformationDto.someCollapseHousehold);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.一部破損);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.世帯);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 一部破損 人
		if (damageInformationDto.someCollapseHuman != null && !"".equals(damageInformationDto.someCollapseHuman)) {
			val = new BigInteger(damageInformationDto.someCollapseHuman);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.一部破損);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 一部破損 棟
		if (damageInformationDto.someCollapseBuilding != null && !"".equals(damageInformationDto.someCollapseBuilding)) {
			val = new BigInteger(damageInformationDto.someCollapseBuilding);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.一部破損);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 床上浸水 世帯
		if (damageInformationDto.overInundationHousehold != null && !"".equals(damageInformationDto.overInundationHousehold)) {
			val = new BigInteger(damageInformationDto.overInundationHousehold);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.床上浸水);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.世帯);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 床上浸水 人
		if (damageInformationDto.overInundationHuman != null && !"".equals(damageInformationDto.overInundationHuman)) {
			val = new BigInteger(damageInformationDto.overInundationHuman);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.床上浸水);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 床上浸水 棟
		if (damageInformationDto.overInundationBuilding != null && !"".equals(damageInformationDto.overInundationBuilding)) {
			val = new BigInteger(damageInformationDto.overInundationBuilding);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.床上浸水);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 床下浸水 世帯
		if (damageInformationDto.underInundationHousehold != null && !"".equals(damageInformationDto.underInundationHousehold)) {
			val = new BigInteger(damageInformationDto.underInundationHousehold);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.床下浸水);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.世帯);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 床下浸水 人
		if (damageInformationDto.underInundationHuman != null && !"".equals(damageInformationDto.underInundationHuman)) {
			val = new BigInteger(damageInformationDto.underInundationHuman);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.床下浸水);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}

		// 住家被害 - 床下浸水 棟
		if (damageInformationDto.underInundationBuilding != null && !"".equals(damageInformationDto.underInundationBuilding)) {
			val = new BigInteger(damageInformationDto.underInundationBuilding);
			typeHouseDamageCounter = pcxDiFactory.createTypeHouseDamageCounter();
			typeHouseDamageCounter.setHouseDamageType(EnumTypeHouseDamageCounterHouseDamageTypeAttr.床下浸水);
			typeHouseDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.棟);
			typeHouseDamageCounter.setValue(val);
			typeHouseDamages.getHouseDamage().add(typeHouseDamageCounter);
		}
		// セット
		if (typeHouseDamages.getHouseDamage().size() > 0) typeDamage.setHouseDamages(typeHouseDamages);

		// その他
		TypeOtherDamages typeOtherDamages = new  TypeOtherDamages();
		TypeOtherDamageCounter typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();

		// その他 - 田_流出埋没
		if (damageInformationDto.ricefieldOutflowBuried != null && !"".equals(damageInformationDto.ricefieldOutflowBuried)) {
			val = new BigInteger(damageInformationDto.ricefieldOutflowBuried);
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.田_流出埋没);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.HA);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 田_冠水
		if (damageInformationDto.ricefieldFlood != null && !"".equals(damageInformationDto.ricefieldFlood)) {
			val = new BigInteger(damageInformationDto.ricefieldFlood);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.田_冠水);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.HA);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 畑_流出埋没
		if (damageInformationDto.fieldOutflowBuried != null && !"".equals(damageInformationDto.fieldOutflowBuried)) {
			val = new BigInteger(damageInformationDto.fieldOutflowBuried);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.畑_流出埋没);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.HA);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 畑_冠水
		if (damageInformationDto.fieldFlood != null && !"".equals(damageInformationDto.fieldFlood)) {
			val = new BigInteger(damageInformationDto.fieldFlood);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.畑_冠水);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.HA);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 文教施設
		if (damageInformationDto.educationalFacilities != null && !"".equals(damageInformationDto.educationalFacilities)) {
			val = new BigInteger(damageInformationDto.educationalFacilities);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.文教施設);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 病院
		if (damageInformationDto.hospital != null && !"".equals(damageInformationDto.hospital)) {
			val = new BigInteger(damageInformationDto.hospital);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.病院);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 道路
		if (damageInformationDto.road != null && !"".equals(damageInformationDto.road)) {
			val = new BigInteger(damageInformationDto.road);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.道路);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 橋りょう
		if (damageInformationDto.bridge != null && !"".equals(damageInformationDto.bridge)) {
			val = new BigInteger(damageInformationDto.bridge);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.橋りょう);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 河川
		if (damageInformationDto.river != null && !"".equals(damageInformationDto.river)) {
			val = new BigInteger(damageInformationDto.river);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.河川);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 港湾
		if (damageInformationDto.port != null && !"".equals(damageInformationDto.port)) {
			val = new BigInteger(damageInformationDto.port);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.港湾);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 砂防
		if (damageInformationDto.sedimentControl != null && !"".equals(damageInformationDto.sedimentControl)) {
			val = new BigInteger(damageInformationDto.sedimentControl);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.砂防);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 清掃施設
		if (damageInformationDto.cleaningFacility != null && !"".equals(damageInformationDto.cleaningFacility)) {
			val = new BigInteger(damageInformationDto.cleaningFacility);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.清掃施設);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 崖くずれ
		if (damageInformationDto.cliffCollapse != null && !"".equals(damageInformationDto.cliffCollapse)) {
			val = new BigInteger(damageInformationDto.cliffCollapse);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.崖くずれ);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 鉄道不通
		if (damageInformationDto.railwayInterruption != null && !"".equals(damageInformationDto.railwayInterruption)) {
			val = new BigInteger(damageInformationDto.railwayInterruption);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.鉄道不通);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 被害船舶
		if (damageInformationDto.ship != null && !"".equals(damageInformationDto.ship)) {
			val = new BigInteger(damageInformationDto.ship);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.被害船舶);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.隻);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 水道
		if (damageInformationDto.water != null && !"".equals(damageInformationDto.water)) {
			val = new BigInteger(damageInformationDto.water);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.水道);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.戸);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 電話
		if (damageInformationDto.phone != null && !"".equals(damageInformationDto.phone)) {
			val = new BigInteger(damageInformationDto.phone);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.電話);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.回線);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - 電気
		if (damageInformationDto.electric != null && !"".equals(damageInformationDto.electric)) {
			val = new BigInteger(damageInformationDto.electric);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.電気);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.戸);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - ガス
		if (damageInformationDto.gas != null && !"".equals(damageInformationDto.gas)) {
			val = new BigInteger(damageInformationDto.gas);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.ガス);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.戸);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}

		// その他 - ブロック塀等
		if (damageInformationDto.blockWalls_Etc != null && !"".equals(damageInformationDto.blockWalls_Etc)) {
			val = new BigInteger(damageInformationDto.blockWalls_Etc);
			typeOtherDamageCounter = pcxDiFactory.createTypeOtherDamageCounter();
			typeOtherDamageCounter.setOtherDamageType(EnumTypeOtherDamageCounterOtherDamageTypeAttr.ブロック塀等);
			typeOtherDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.箇所);
			typeOtherDamageCounter.setValue(val);
			typeOtherDamages.getOtherDamage().add(typeOtherDamageCounter);
		}
		// セット
		if (typeOtherDamages.getOtherDamage().size() > 0) typeDamage.setOtherDamages(typeOtherDamages);

		// り災
		TypeSufferers typeSufferers = new TypeSufferers();
		TypeSuffererDamageCounter typeSuffererDamageCounter = pcxDiFactory.createTypeSuffererDamageCounter();

		// り災 - り災世帯数
		if (damageInformationDto.suffererHousehold != null && !"".equals(damageInformationDto.suffererHousehold)) {
			val = new BigInteger(damageInformationDto.suffererHousehold);
			typeSuffererDamageCounter.setSuffererDamageType(EnumTypeSuffererDamageCounterSuffererDamageTypeAttr.り災世帯数);
			typeSuffererDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.世帯);
			typeSuffererDamageCounter.setValue(val);
			typeSufferers.getSufferer().add(typeSuffererDamageCounter);
		}

		// り災 - り災者数
		if (damageInformationDto.suffererHuman != null && !"".equals(damageInformationDto.suffererHuman)) {
			val = new BigInteger(damageInformationDto.suffererHuman);
			typeSuffererDamageCounter = pcxDiFactory.createTypeSuffererDamageCounter();
			typeSuffererDamageCounter.setSuffererDamageType(EnumTypeSuffererDamageCounterSuffererDamageTypeAttr.り災者数);
			typeSuffererDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.人);
			typeSuffererDamageCounter.setValue(val);
			typeSufferers.getSufferer().add(typeSuffererDamageCounter);
		}
		// セット
		if (typeSufferers.getSufferer().size() > 0) typeDamage.setSufferers(typeSufferers);

		// 火災
		TypeFireDamages typeFireDamages = new TypeFireDamages();
		TypeFireDamageCounter typeFireDamageCounter = pcxDiFactory.createTypeFireDamageCounter();

		// 火災 - 建物
		if (damageInformationDto.fireBuilding != null && !"".equals(damageInformationDto.fireBuilding)) {
			val = new BigInteger(damageInformationDto.fireBuilding);
			typeFireDamageCounter.setFireDamageType(EnumTypeFireDamageCounterFireDamageTypeAttr.建物);
			typeFireDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.件);
			typeFireDamageCounter.setValue(val);
			typeFireDamages.getFireDamage().add(typeFireDamageCounter);
		}

		// 火災 - 危険物
		if (damageInformationDto.fireDangerousGoods != null && !"".equals(damageInformationDto.fireDangerousGoods)) {
			val = new BigInteger(damageInformationDto.fireDangerousGoods);
			typeFireDamageCounter = pcxDiFactory.createTypeFireDamageCounter();
			typeFireDamageCounter.setFireDamageType(EnumTypeFireDamageCounterFireDamageTypeAttr.危険物);
			typeFireDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.件);
			typeFireDamageCounter.setValue(val);
			typeFireDamages.getFireDamage().add(typeFireDamageCounter);
		}

		// 火災 - その他
		if (damageInformationDto.otherFire != null && !"".equals(damageInformationDto.otherFire)) {
			val = new BigInteger(damageInformationDto.otherFire);
			typeFireDamageCounter = pcxDiFactory.createTypeFireDamageCounter();
			typeFireDamageCounter.setFireDamageType(EnumTypeFireDamageCounterFireDamageTypeAttr.その他);
			typeFireDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.件);
			typeFireDamageCounter.setValue(val);
			typeFireDamages.getFireDamage().add(typeFireDamageCounter);
		}
		// セット
		if (typeFireDamages.getFireDamage().size() > 0) typeDamage.setFireDamages(typeFireDamages);

		// 被害額
		TypeLosses typeLosses = new TypeLosses();
		TypeTotalLosses typeTotalLosses = new TypeTotalLosses();
		TypeLossesDamageCounter typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
		TypeCategorizedLosses typeCategorizedLosses = new TypeCategorizedLosses();

		// 被害額 - 公共文教施設
		if (damageInformationDto.publicScoolFacillities != null && !"".equals(damageInformationDto.publicScoolFacillities)) {
			val = new BigInteger(damageInformationDto.publicScoolFacillities);
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.公共文教施設);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 農林水産業施設
		if (damageInformationDto.agricultureFacilities != null && !"".equals(damageInformationDto.agricultureFacilities)) {
			val = new BigInteger(damageInformationDto.agricultureFacilities);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.農林水産業施設);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 公共土木施設
		if (damageInformationDto.publicEngineeringFacilities != null && !"".equals(damageInformationDto.publicEngineeringFacilities)) {
			val = new BigInteger(damageInformationDto.publicEngineeringFacilities);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.公共土木施設);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 施設被害額小計 (公共文教施設～公共土木施設)
		if (damageInformationDto.subtotalDamageFacilities != null && !"".equals(damageInformationDto.subtotalDamageFacilities)) {
			val = new BigInteger(damageInformationDto.subtotalDamageFacilities);
			typeTotalLosses.setCurrencyUnit(EnumTypeTotalLossesCurrencyUnitAttr.千円);
			typeCategorizedLosses.setTotalLosses(typeTotalLosses);
			typeLosses.setFacilitiesLosses(typeCategorizedLosses);
			typeLosses.getFacilitiesLosses().getTotalLosses().setValue(val);
		}

		// 被害額 - 農業被害
		if (damageInformationDto.farmingDamage != null && !"".equals(damageInformationDto.farmingDamage)) {
			val = new BigInteger(damageInformationDto.farmingDamage);
			typeCategorizedLosses = new TypeCategorizedLosses();
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.農業被害);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 林業被害
		if (damageInformationDto.forestryDamage != null && !"".equals(damageInformationDto.forestryDamage)) {
			val = new BigInteger(damageInformationDto.forestryDamage);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.林業被害);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 畜産被害
		if (damageInformationDto.animalDamage != null && !"".equals(damageInformationDto.animalDamage)) {
			val = new BigInteger(damageInformationDto.animalDamage);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.畜産被害);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 水産被害
		if (damageInformationDto.fisheriesDamage != null && !"".equals(damageInformationDto.fisheriesDamage)) {
			val = new BigInteger(damageInformationDto.fisheriesDamage);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.水産被害);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - 商工被害
		if (damageInformationDto.commerceAndIndustryDamage != null && !"".equals(damageInformationDto.commerceAndIndustryDamage)) {
			val = new BigInteger(damageInformationDto.commerceAndIndustryDamage);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.商工被害);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - その他
		if (damageInformationDto.otherDamageOther != null && !"".equals(damageInformationDto.otherDamageOther)) {
			val = new BigInteger(damageInformationDto.otherDamageOther);
			typeLossesDamageCounter = pcxDiFactory.createTypeLossesDamageCounter();
			typeLossesDamageCounter.setLossesDamageType(EnumTypeLossesDamageCounterLossesDamageTypeAttr.その他);
			typeLossesDamageCounter.setCounterUnit(EnumTypeDamageCounterCounterUnitAttr.千円);
			typeLossesDamageCounter.setValue(val);
			typeCategorizedLosses.getLoss().add(typeLossesDamageCounter);
		}

		// 被害額 - その他被害額小計 (農業被害～その他)
		if (damageInformationDto.subtotalOtherDamage != null && !"".equals(damageInformationDto.subtotalOtherDamage)) {
			val = new BigInteger(damageInformationDto.subtotalOtherDamage);
			typeTotalLosses = new TypeTotalLosses();
			typeTotalLosses.setCurrencyUnit(EnumTypeTotalLossesCurrencyUnitAttr.千円);
			typeCategorizedLosses.setTotalLosses(typeTotalLosses);
			typeLosses.setOtherLosses(typeCategorizedLosses);
			typeLosses.getOtherLosses().getTotalLosses().setValue(val);
		} else if (typeCategorizedLosses.getLoss().size() > 0) {
			typeLosses.setOtherLosses(typeCategorizedLosses);
		}

		// 被害額 - 被害総計
		if (damageInformationDto.totalDamage != null && !"".equals(damageInformationDto.totalDamage)) {
			val = new BigInteger(damageInformationDto.totalDamage);
			typeTotalLosses = new TypeTotalLosses();
			typeTotalLosses.setCurrencyUnit(EnumTypeTotalLossesCurrencyUnitAttr.千円);
			typeTotalLosses.setValue(val);
			typeLosses.setTotalLosses(typeTotalLosses);
		}

		// セット
		if (typeCategorizedLosses.getLoss().size() > 0 || typeTotalLosses.getValue() != null) typeDamage.setLosses(typeLosses);

		// 消防
		TypeFirefighter	 typeFirefighter = new  TypeFirefighter();
		if (damageInformationDto.fireman1 != null && !"".equals(damageInformationDto.fireman1)) {
			val = new BigInteger(damageInformationDto.fireman1);
			typeFirefighter.setTurnoutFireStation(val);
		}

		if (damageInformationDto.fireman2 != null && !"".equals(damageInformationDto.fireman2)) {
			val = new BigInteger(damageInformationDto.fireman2);
			typeFirefighter.setTurnoutFireCompany(val);
		}
		// セット
		if (typeFirefighter.getTurnoutFireCompany() != null || typeFirefighter.getTurnoutFireStation() != null) typeDamage.setFirefighter(typeFirefighter);

		return typeDamage;
	}



	/**
	 * EDXLDistributionType を生成
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param eventInformationList イベント情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistributionType
	 * @throws ParseException
	 */
	public EDXLDistributionType createEDXLDistributionType(PubliccommonsReportData publiccommonsReportData, DamageInformationDto damageInformationDto, PCommonsSendDto pCommonsSendDto){
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		// 被害情報を作成
		TypeDamageInformation Damage = createDamageInformation(damageInformationDto, pCommonsSendDto);
		JAXBElement<?> pcxmlJAXBElement = pcxDiFactory.createDamageInformation(Damage);
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
