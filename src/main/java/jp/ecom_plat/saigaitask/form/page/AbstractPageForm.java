/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.Date;

import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;


@lombok.Getter @lombok.Setter

/**
 * 基本機能のリクエストパラメータ.
 * /pageに指定するパラメータ.
 * 各ページに共通なので抽象クラスにする.
 */
abstract public class AbstractPageForm {

	static Logger logger = Logger.getLogger(AbstractPageForm.class);

	/**
	 * メニューID
	 */
	public Long menuid;

	/**
	 * メニュータスクID
	 */
	public Long menutaskid;

	/** メニュー種別ID */
	public long menutypeid;

	/**
	 * ページ種別
	 * list, map など
	 */
	public String pagetype;

	/** 全画面表示 */
	public boolean fullscreen = false;

	/** 意思決定支援 */
	public boolean decisionsupport = false;

	/**
	 * 表示データ時間(ISO8601)
	 */
	public String time;

	/**
	 * レイヤ個別の表示データ時間(ISO8601)
	 * カンマ区切り。
	 * layerId1,layerTime1,layerId2,layerTime2...
	 */
	public String layertimes;

	/**
	 * @return 表示データ時間配列
	 */
	//public Date[] getTimeParams() { // getter だと SAStruts がうまく動かないので getXXX と書かない
	public Date[] timeParams() {
		return timeParams(!Config.isAvailableUTCTimeZone());
	}
	/**
	 * @param withOffset タイムゾーン分ずらすかどうか
	 *   false: UTM時刻    true: タイムゾーン調整時刻
	 * @return 表示データ時間配列
	 */
	public Date[] timeParams(boolean withOffset) {
		try{
			if(StringUtil.isNotEmpty(time)) {
				Date d = TimeUtil.parseISO8601(time);
				if(withOffset) return new Date[]{TimeUtil.newDateWithOffset(d.getTime())};
				return new Date[]{d};
			}
		}catch(Exception e) {
			logger.error("error time parameter: "+e.getMessage(), e);
		}
		return null;
		//if(withOffset) return new Date[]{TimeUtil.newDateWithOffset(new Date().getTime())};
		//return new Date[]{new Date()};
	}
}
