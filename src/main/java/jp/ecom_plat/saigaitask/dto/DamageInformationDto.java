/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;

/**
 * 被害情報
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class DamageInformationDto implements Serializable {
		private static final long serialVersionUID = 1L;

		/** 記録データID */
		public String  trackdataid;

		/** 備考 */
		public String remarks;

		/** 報告数 */
		public String reportno;

		/** 死者 */
		public String deadPeople;

		/** 行方不明者数 */
		public String missingPeople;

		/** 負傷者 重傷 */
		public String seriouslyInjuredPeople;

		/** 負傷者 軽傷 */
		public String slightlyInjuredPeople;

		/** 全壊 棟 */
		public String totalCollapseBuilding;

		/** 全壊 世帯 */
		public String totalCollapseHousehold;

		/** 全壊 人 */
		public String totalCollapseHuman;

		/** 半壊 棟 */
		public String halfCollapseBuilding;

		/** 半壊 世帯 */
		public String halfCollapseHousehold;

		/** 半壊 人 */
		public String halfCollapseHuman;

		/** 一部破壊 棟 */
		public String someCollapseBuilding;

		/** 一部破壊 世帯 */
		public String someCollapseHousehold;

		/** 一部破壊 人 */
		public String someCollapseHuman;

		/** 床上浸水 棟 */
		public String overInundationBuilding;

		/** 床上浸水 世帯 */
		public String overInundationHousehold;

		/** 床上浸水 人 */
		public String overInundationHuman;

		/** 床下浸水 棟 */
		public String underInundationBuilding;

		/** 床下浸水 世帯 */
		public String underInundationHousehold;

		/** 床下浸水 人 */
		public String underInundationHuman;

		/** 公共建物 棟 */
		public String publicBuilding;

		/** その他 棟 */
		public String otherBuilding;

		/** 田_流出埋没 */
		public String ricefieldOutflowBuried;

		/** 田_冠水 */
		public String ricefieldFlood;

		/** 畑_流出埋没 */
		public String fieldOutflowBuried;

		/** 畑_冠水 */
		public String fieldFlood;

		/** 文教施設 */
		public String educationalFacilities;

		/** 病院 */
		public String hospital;

		/** 道路 */
		public String road;

		/** 橋りょう */
		public String bridge;

		/** 河川 */
		public String river;

		/** 港湾 */
		public String port;

		/** 砂防 */
		public String sedimentControl;

		/** 清掃施設 */
		public String cleaningFacility;

		/** 崖崩れ */
		public String cliffCollapse;

		/** 鉄道不通 */
		public String railwayInterruption;

		/** 被害船舶 */
		public String ship;

		/** 水道 */
		public String water;

		/** 電話 */
		public String phone;

		/** 電気 */
		public String electric;

		/** ガス */
		public String gas;

		/** ブロック塀等 */
		public String blockWalls_Etc;

		/** り災世帯数 */
		public String suffererHousehold;

		/** り災者数 */
		public String suffererHuman;

		/**火災 建物 */
		public String fireBuilding;

		/**火災 危険物 */
		public String fireDangerousGoods;

		/**火災 その他 */
		public String otherFire;

		/** 公共文教施設被害額 */
		public String schoolmount;

		/** 農林水産業施設被害額 */
		public String farmmount;

		/** 公立文教施設 */
		public String publicScoolFacillities;

		/** 農林水産業施設 */
		public String agricultureFacilities;

		/** 公共土木施設 */
		public String publicEngineeringFacilities;

		/** 施設被害小計 */
		public String subtotalDamageFacilities;

		/** 農業被害 */
		public String farmingDamage;

		/** 林業被害 */
		public String forestryDamage;

		/** 畜産被害 */
		public String animalDamage;

		/** 水産被害 */
		public String fisheriesDamage;

		/** 商工被害 */
		public String commerceAndIndustryDamage;

		/** その他 */
		public String otherDamageOther;

		/** その他被害小計 */
		public String subtotalOtherDamage;

		/** 被害総計 */
		public String totalDamage;

		/** 消防職員出動延人数 */
		public String fireman1;

		/** 消防団員出動延人数 */
		public String fireman2;
	}