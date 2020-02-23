/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page.map;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.seasar.framework.util.StringUtil;

/**
 * eコミのWMSAuthに対応するアクションクラスのアクションフォームクラスです.
 */
@lombok.Getter @lombok.Setter
public class WmsAuthForm {

	static Logger logger = Logger.getLogger(WmsAuthForm.class);

	//====================================
	// Common request parameters
	//====================================
	/**
	 * The VERSION parameter specifies the protocol version number. The format of the
	 * version number, and version negotiation, are described in Section 6.1
	 */
	public String VERSION;

	/**
	 * The REQUEST parameter indicates which service operation is being invoked. The value
	 * shall be the name of one of the operations offered by the OGC Web Service Instance.
	 */
	public String REQUEST;

	/**
	 * The FORMAT parameter specifies the output format of the response to an operation.
	 */
	public String FORMAT;

	/**
	 * The EXCEPTIONS parameter states the format in which to report errors.
	 */
	public String exceptions;

	/**
	 * The Bounding Box (BBOX) is a set of four comma-separated decimal, scientific notation,
	 * or integer values (if integers are provided where floating point is needed, the decimal
	 * point is assumed at the end of the number). These values specify the minimum X,
	 * minimum Y, maximum X, and maximum Y ranges, in that order, expressed in units of
	 * the SRS of the request, such that a rectangular area is defined in those units. 
	 */
	public String BBOX;

	/**
	 * The Spatial Reference System (SRS) is a text parameter that names a horizontal
	 * coordinate reference system code. The name includes a namespace prefix, a colon, a
	 * numeric identifier, and possibly a comma followed by additional parameters. This
	 * specification defines two namespaces, EPSG and AUTO, which are discussed below.
	 */
	public String SRS;

	/**
	 * Some geospatial information may be available at multiple times (for example, an hourly
	 * weather map). An OGC Web Service may announce available times in its Capabilities
	 * XML, and some operations include a parameter for requesting a particular time. The
	 * format of a time string is specified in Annex C. Depending on the context, time values
	 * may appear as a single value, a list of values, or an interval, as specified in Annex D.
	 * When providing temporal information, Servers should declare a default value in
	 * Capabilities XML unless there is compelling reason to behave otherwise, and Servers
	 * shall respond with the default value if one has been declared and the Client request does
	 * not include a value.
	 */
	public String time;

	//====================================
	// GetMap request parameters
	//====================================
	/**
	 * The required SERVICE parameter indicates which of the available service types at a
	 * particular service instance is being invoked. This parameter allows the same URL prefix
	 * to offer Capabilities XML for multiple OGC Web Services
	 */
	public String service;

	/**
	 * Comma-separated list of one or more map layers.
	 * Optional if SLD parameter is present
	 */
	public String LAYERS;

	/**
	 * Comma-separated list of one rendering style per
	 * requested layer. Optional if SLD parameter is
	 * present
	 */
	public String STYLES;

	/**
	 * Width in pixels of map picture
	 */
	public String WIDTH;

	/**
	 * Height in pixels of map picture.
	 */
	public String HEIGHT;

	/**
	 * Background transparency of map
	 * (default=FALSE)
	 */
	public String TRANSPARNET;

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
	}

	//====================================
	// OpenLayers request parameters?
	// これは必要？
	//====================================
	//public String tiled;
	//public String tilesorigin;
	//public String _olsalt;

	//====================================
	// ecommap request parameters
	//====================================
	/** サイトID */
	public int cid;
	/** 地図ID */
	public long mid;
	/** 倍率 */
	public float rate;
	/** 線倍率 */
	public float linerate;
	/** 文字倍率 */
	public float textrate;
	/**
	 * 時間パラメータのレイヤ個別指定
	 * カンマ区切り "レイヤID1,時間1,レイヤID2,時間2"
	 */
	public String layertimes;
	/**
	 * layertimes のパース結果
	 */
	public Map<String, String> layertimesMap;
	/**
	 * 
	 * @param layerId
	 * @return レイヤ個別の時間パラメータ
	 */
	public String layertime(String layerId) {
		if(StringUtil.isNotEmpty(layertimes)) {
			// Mapを初期化
			if(layertimesMap==null) {
				layertimesMap = new HashMap<String, String>();
				String layerid = null;
				String layertime = null;
				for(String param : layertimes.split(",")) {
					if(layerid==null) layerid = param;
					else {
						layertime = param;
						layertimesMap.put(layerid, layertime);
						// clear
						layerid = layertime = null;
					}
				}
			}
			return layertimesMap.get(layerId);
		}
		return null;
	}

	//====================================
	// フィルタ機能 request parameters
	//====================================
	/** フィルタ対象レイヤ */
	public String filterlayer;

	/** フィルタキー */
	public String filterkey;

	/** フィルタされたフィーチャのグレーアウト透過度 */
	public Float grayout;

	/** 空間検索範囲レイヤフラグ */
	public Boolean spatiallayer;

	/** 検索範囲に利用するレイヤ条件のJSONオブジェクト配列 */
	public JSONArray spatiallayers;

	/** ルール別の表示状態指定 指定がなければnull */
	public String rule;
}
