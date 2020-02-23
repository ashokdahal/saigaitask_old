/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;



/**
 * テレメータデータを詳細画面用にフォーマットしたデータのDtoクラスです.
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ObservDto {
	
	/*
	 * 例：2013/02/01 00:00の場合
	 * 
	 *   dateValue = '2013/2/1'
	 *   dateLabel = '2013/1/31'
	 *   timeValue = '00:00'
	 *   timeLabel = '24:00'
	 */
	
	/**
	 * テレメータデータの日付の値
	 *   yyyy/M/d
	 */
	private String datevalue = null;
	
	/**
	 * テレメータデータの日付のラベル
	 *   yyyy/M/d
	 */
	private String datelabel = null;
	
	/**
	 * テレメータデータの時刻の値
	 *   HH:mm
	 */
	private String timevalue = null;
	
	/**
	 * テレメータデータの時刻のラベル
	 *   kk:mm
	 */
	private String timelabel = null;
	
	/**
	 * テレメータデータ１
	 */
	private String itemdata1 = null;
	
	/**
	 * テレメータデータの矢印１
	 */
	private String itemdataarrow1 = null;
	
	/**
	 * テレメータデータ１のコンテンツコード
	 */
	private Integer contentscode1 = null;
	
	/**
	 * テレメータデータ１のコンテンツ名
	 */
	private String contentsname1 = null;
	
	/**
	 * テレメータデータ２
	 */
	private String itemdata2 = null;
	
	/**
	 * テレメータデータの矢印２
	 */
	private String itemdataarrow2 = null;
	
	/**
	 * テレメータデータ２のコンテンツコード
	 */
	private Integer contentscode2 = null;
	
	/**
	 * テレメータデータ２のコンテンツ名
	 */
	private String contentsname2 = null;
	
	/**
	 * テレメータデータ３
	 */
	private String itemdata3 = null;
	
	/**
	 * テレメータデータの矢印３
	 */
	private String itemdataarrow3 = null;
	
	/**
	 * テレメータデータ３のコンテンツコード
	 */
	private Integer contentscode3 = null;
	
	/**
	 * テレメータデータ３のコンテンツ名
	 */
	private String contentsname3 = null;
	
	/**
	 * テレメータデータ４
	 */
	private String itemdata4 = null;
	
	/**
	 * テレメータデータの矢印４
	 */
	private String itemdataarrow4 = null;
	
	/**
	 * テレメータデータ４のコンテンツコード
	 */
	private Integer contentscode4 = null;
	
	/**
	 * テレメータデータ４のコンテンツ名
	 */
	private String contentsname4 = null;
}
