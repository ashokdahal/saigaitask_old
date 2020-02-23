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

import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.RefugeInformationDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastRefuge;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.EnumTypeHouseholdsUnitAttr;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.EnumTypeIssueOrLift;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.EnumTypeSort;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeArea;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeAreas;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeDetail;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeEvacuationOrder;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeHouseholds;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.TypeObject;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDateTime;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDisaster;
import jp.ne.publiccommons.xml.xml.edxl.CategoryType;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;

import org.seasar.framework.util.StringUtil;

/**
 * 公共情報コモンズの避難勧告・指示サービス
 */
@org.springframework.stereotype.Service
public class PcEvacuationService extends AbstractPublicCommonsService {

	/** PCX_EVファクトリ. 避難勧告・指示情報 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.ObjectFactory pcxEvFactory =	new jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.ObjectFactory();

	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;

	/**
	 * 避難勧告・避難指示情報を生成します.
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return TypeEvacuationOrder
	 */
	public TypeEvacuationOrder createEvacuationOrder(PCommonsSendDto pCommonsSendDto) {
		TypeEvacuationOrder typeEvacuationOrder = pcxEvFactory.createTypeEvacuationOrder();

		// 災害名称
		if (StringUtil.isNotEmpty(pCommonsSendDto.trackdataname)) {
			TypeDisaster typeDisaster = pcxEbFactory.createTypeDisaster();
			typeDisaster.setDisasterName(pCommonsSendDto.trackdataname);
			typeEvacuationOrder.setDisaster(typeDisaster);
		}

		// 補足情報
		if(StringUtil.isNotEmpty(pCommonsSendDto.complementaryInfo)) typeEvacuationOrder.setComplementaryInfo(pCommonsSendDto.complementaryInfo);

		// 詳細情報を作成
		for (RefugeInformationDto cur : pCommonsSendDto.refugeInformationList) {
			// 全域で発令区分がブランクは発信しない
			if (! (Constants.ALL_AREA().equals(cur.chikuName) && "".equals(cur.hatureiKbn.trim())) ) {
				String chikuName = Constants.ALL_AREA().equals(cur.chikuName) ? cur.chikuNameAllArea : cur.chikuName;	// 地区名
				String hatureiKbn = cur.hatureiKbn;							// 発令状況（なし・避難準備情報・避難勧告・避難指示）
				Integer targetHouseholds = cur.targetHouseholds;		// 対象世帯数
				Integer people = cur.people;										// 人数
				Timestamp hatureiDateTime = cur.hatureiDateTime;	// 発令時刻
				String hatureikbnLast = cur.lasthatureiKbn;					// 前回発令区分

				// 解除は前回の発令区分を設定する
				EnumTypeSort sort;
				EnumTypeIssueOrLift issueOrLift;
				if (Constants.ISSUE_CANCEL_NAME().equals(hatureiKbn)) {
	//				String hatureikbnLast = getLastEvacuationOrderByArea(trackdataid, pCommonsSendDto.localgovinfoid, cur.chikuName);
					sort = getEnumTypeSort(hatureikbnLast);
					issueOrLift = EnumTypeIssueOrLift.解除;
				// 発令は画面で指定された発令区分を設定する
				} else {
					sort = getEnumTypeSort(hatureiKbn);
					issueOrLift = EnumTypeIssueOrLift.発令;
				}
				TypeArea typeArea = createTypeArea(targetHouseholds, people, chikuName, hatureiDateTime);

				addArea(typeEvacuationOrder, sort, issueOrLift, typeArea);
			}
		}

		return typeEvacuationOrder;

	}

	/**
	 * 避難勧告／避難指示前回発令区分を取得
	 * @param trackdataid 記録データID
	 * @param localgovinfoid 自治体ID
	 * @param targetArea 取得する地区名
	 * @return 前回発令時の発令区分
	 */
	public String getLastEvacuationOrderByArea(Long trackdataid, Long localgovinfoid, String chikuname){
			// 公共情報コモンズ避難勧告／避難指示最終送信履歴管理テーブルを検索
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataWithLast(CategoryType.EVACUATION_ORDER.toString(), trackdataid, null, chikuname);
			for (PubliccommonsReportData publiccommonsReportData : publiccommonsReportDataList) {
				for (PubliccommonsReportDataLastRefuge publiccommonsReportDataLastRefuge : publiccommonsReportData.publiccommonsReportDataLastRefugeList){
					// 前回が解除でない場合、その発令区分を返す
					if (!Constants.ISSUE_CANCEL_NAME().equals(publiccommonsReportDataLastRefuge.hatureikbn)) {
						return publiccommonsReportDataLastRefuge.hatureikbn;
					}
				}
			}

		return "";
	}

	/**
	 * 避難勧告／避難指示前回発令区分から、発令or解除を取得
	 * @param trackdataid 記録データID
	 * @param localgovinfoid 自治体ID
	 * @param targetArea 取得する地区名
	 * @return 前回発令時の発令区分が解除なら、解除を返す
	 */
	public String getIssueOrLiftEvacuationOrderByArea(Long trackdataid, Long localgovinfoid, String chikuname){
			// 公共情報コモンズ避難勧告／避難指示最終送信履歴管理テーブルを検索
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataWithLast(CategoryType.EVACUATION_ORDER.toString(), trackdataid, null, chikuname);
			if(publiccommonsReportDataList != null && publiccommonsReportDataList.size() > 0){
				String issueorlift = publiccommonsReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(0).issueorlift;
				String hatureikbnLast = publiccommonsReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(0).hatureikbn;
				// 前回が発令解除なら、発令区分解除を返す
				if (Constants.ISSUE_CANCEL_NAME().equals(issueorlift)) {
					return hatureikbnLast;
				}
			}
		return "";
	}

	/**
	 * TypeAreaを生成します.
	 * @param nhousehold
	 * @param people
	 * @param areaName
	 * @param ordertime
	 * @return
	 */
	public TypeArea createTypeArea(Integer nhousehold, Integer people, String areaName, Timestamp ordertime) {
		TypeArea typeArea = pcxEvFactory.createTypeArea();
		TypeHouseholds households = pcxEvFactory.createTypeHouseholds();
		TypeObject number = pcxEvFactory.createTypeObject();

		// number
		if (nhousehold != null && people != null) {
			households.setUnit(EnumTypeHouseholdsUnitAttr.世帯);
			households.setValue(BigInteger.valueOf(nhousehold));
			number.setHouseholds(households);
			number.setHeadCount(BigInteger.valueOf(people));
			typeArea.setObject(number);
		}

		typeArea.setLocation(PublicCommonsUtils.createCommonsTargetAreaType(areaName, null, null, null));
		TypeDateTime typeDateTime = pcxEbFactory.createTypeDateTime();
		if (ordertime != null) {
			typeDateTime.setValue(PublicCommonsUtils.getXMLGregorianCalendar(new Date(ordertime.getTime())));
		}
		typeArea.setDateTime(typeDateTime);
		return typeArea;
	}

	/**
	 * 指定の避難情報にエリアを追加
	 * @param typeEvacuationOrder 避難勧告・指示情報
	 * @param sort 発令区分
	 * @param issueOrLift 発令・解除区分
	 * @param typeArea 追加するエリア
	 */
	public void addArea(TypeEvacuationOrder typeEvacuationOrder, EnumTypeSort sort, EnumTypeIssueOrLift issueOrLift, TypeArea typeArea) {
		// 詳細情報を取得
		TypeDetail typeDetail = null;
		for(TypeDetail entry : typeEvacuationOrder.getDetail()) {
			if(entry==null || sort==null || issueOrLift==null) continue;
			if(sort.equals(entry.getSort())==false) continue;
			if(issueOrLift.equals(entry.getIssueOrLift())==false) continue;
			typeDetail = entry;
		}

		// 詳細情報がない場合は新規作成
		if(typeDetail==null) {
			typeDetail = pcxEvFactory.createTypeDetail();
			typeDetail.setSort(sort);
			typeDetail.setIssueOrLift(issueOrLift);
			typeEvacuationOrder.getDetail().add(typeDetail);
		}

		// エリアを追加
		TypeAreas typeAreas = typeDetail.getAreas();
		if(typeAreas==null) {
			typeAreas = pcxEvFactory.createTypeAreas();
			typeDetail.setAreas(typeAreas);
		}
		typeAreas.getArea().add(typeArea);

		// 世帯数の更新
		if(typeArea.getObject()!=null) {
			TypeObject areaTypeObject = typeArea.getObject();
			BigInteger areaHouseholdsValue = null;
			if(areaTypeObject.getHouseholds()!=null && areaTypeObject.getHouseholds().getValue()!=null) {
				areaHouseholdsValue = areaTypeObject.getHouseholds().getValue();
			}
			if(areaHouseholdsValue!=null) {
				// 発令区分別の対象世帯数を更新
				TypeObject detailTypeObject = typeDetail.getObject();
				if(detailTypeObject==null) {
					detailTypeObject = createTypeObject();
					typeDetail.setObject(detailTypeObject);
				}
				sum(detailTypeObject, areaTypeObject);

				// 合計の対象世帯数を更新
				TypeObject totalNumber = typeEvacuationOrder.getTotalNumber();
				if(totalNumber==null) {
					totalNumber = createTypeObject();
					typeEvacuationOrder.setTotalNumber(totalNumber);
				}
				sum(totalNumber, areaTypeObject);
			}
		}
	}

	/**
	 * 世帯数0で初期化したTypeObjectを生成します.
	 * @return TypeObject
	 */
	public TypeObject createTypeObject() {
		TypeObject typeObject = pcxEvFactory.createTypeObject();
		TypeHouseholds households = pcxEvFactory.createTypeHouseholds();
		households.setUnit(EnumTypeHouseholdsUnitAttr.世帯);
		households.setValue(BigInteger.valueOf(0));
		typeObject.setHouseholds(households);
		typeObject.setHeadCount(BigInteger.valueOf(0));
		return typeObject;
	}

	/**
	 * TypeObjectの destにsrcの世帯数を足します。
	 * @param dest
	 * @param src
	 * @return dest
	 */
	public TypeObject sum(TypeObject dest, TypeObject src) {
		BigInteger householdsValue = src.getHouseholds().getValue();
		BigInteger sumHouseholdsValue = dest.getHouseholds().getValue().add(householdsValue);
		dest.getHouseholds().setValue(sumHouseholdsValue);

		BigInteger peopleValue = src.getHeadCount();
		BigInteger sumPeopleValue = dest.getHeadCount().add(peopleValue);
		dest.setHeadCount(sumPeopleValue);

		return dest;
	}

	/**
	 * 発令区分からEnumTypeSortを取得
	 * @param hatureiKbn 発令区分
	 * @return 対応するEnumTypeSortがない場合はnull
	 */
	public EnumTypeSort getEnumTypeSort(String hatureiKbn) {
		if(Constants.ISSUE_PREPARE_PCOMMONS_NAME().equals(hatureiKbn)) {
			return EnumTypeSort.避難準備;
		}
		else if(Constants.ISSUE_ADVISE_NAME().equals(hatureiKbn)) {
			return EnumTypeSort.避難勧告;
		}
		else if(Constants.ISSUE_INDICATE_NAME().equals(hatureiKbn)) {
			return EnumTypeSort.避難指示;
		}
		return null;
	}

	/**
	 * PCXMLのJAXBElemntに変換します.
	 * @param evacuationOrder
	 * @return
	 */
	public JAXBElement<TypeEvacuationOrder> getPCXMLJAXBElement(TypeEvacuationOrder evacuationOrder) {
		return pcxEvFactory.createEvacuationOrder(evacuationOrder);
	}

	/**
	 * EDXLDistributionType を生成
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistributionType
	 */
	public EDXLDistributionType createEDXLDistributionType(PubliccommonsReportData publiccommonsReportData, PCommonsSendDto pCommonsSendDto) {
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		// 避難勧告・指示情報を作成
		TypeEvacuationOrder evacuationOrder = createEvacuationOrder(pCommonsSendDto);
		JAXBElement<?> pcxmlJAXBElement = getPCXMLJAXBElement(evacuationOrder);
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
