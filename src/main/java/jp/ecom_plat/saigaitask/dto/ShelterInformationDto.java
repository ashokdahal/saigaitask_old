/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * 避難所情報
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ShelterInformationDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 避難所名 */
	public String shelterName;

	/** 地区 */
	public String chikuName;

	/** 住所 */
	public String shelterAddress;

	/** 電話番号 */
	public String shelterPhone;

	/** FAX */
	public String shelterFax;

	/** 代表者氏名 */
	public String shelterStaff;

	/** 開設状況 */
	public String shelterStatus;

	/** 収容定員数 */
	public String shelterCapacity;

	/** 開設日時 */
	public Timestamp setupTime;

	/** 閉鎖日時 */
	public Timestamp closeTime;

	/** 種別 */
	public String type;

	/** 種別詳細(種別だけで表現しきれない時) */
	public String typeDetail;

	/** 座標 */
	public String circle;

	/** 避難人数 */
	public String headCount;

	/** 避難人数(うち自主避難) */
	public String headCountVoluntary;

	/** 避難世帯数 */
	public String houseHolds;

	/** 避難世帯数(うち自主避難) */
	public String houseHoldsVoluntary;

	/** 避難所レイヤ名(コモンズ送信不要) */
	public String shelterLayerName;

	/** テーブル名(コモンズ送信不要) */
	public String tablename;

	public String getSetupTime() {
		return setupTime == null ? "" : new SimpleDateFormat("yyyy/MM/dd HH:mm").format(setupTime);
	}

	public String getCloseTime() {
		return closeTime == null ? "" : new SimpleDateFormat("yyyy/MM/dd HH:mm").format(closeTime);
	}
}
