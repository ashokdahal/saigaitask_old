/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayoutInfo;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.servlet.SLDServlet;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * 地図の初期化データのDtoクラスです.
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class MapInitDto{

	Logger logger = Logger.getLogger(MapInitDto.class);

	/** eコミマップ情報ID */
	public long ecommapInfoId;

	/** eコミマップURL(/mapまでのURL) */
	public String ecommapURL;

	/** サイトID */
	public int communityId;

	/** 地図ID */
	public long mapId;

	/** 地図情報 */
	public MapInfo mapInfo;

	/** EPSG */
	public String epsg;

	/** 登録情報レイヤリロード間隔(秒) */
	public int redrawInterval;

	/** ベースレイヤ情報 */
	public List<JSONLayerInfo> baseLayerInfos = new ArrayList<JSONLayerInfo>();

	/** KMLレイヤ情報 */
	public List<JSONLayerInfo> kmlLayerInfos = new ArrayList<JSONLayerInfo>();

	/** 登録情報レイヤグループ情報 */
	public List<JSONLayerInfo> groupContentsLayerInfos = new ArrayList<JSONLayerInfo>();

	/** 登録情報レイヤ情報 */
	public List<JSONLayerInfo> contentsLayerInfos = new ArrayList<JSONLayerInfo>();

	/** 登録情報レイヤ認証キー */
	public Map<String, String> layerAuthKeyMap = new HashMap<String, String>();

	/**
	 * 主題図項目レイヤ情報のマップ.
	 * 属する項目レイヤ情報を追加するときに検索するためにレイヤIDでマップします.
	 */
	public Map<String, ReferenceJSONLayerInfo> referenceLayerInfoMap =
			new LinkedHashMap<String, ReferenceJSONLayerInfo>();

	/** 主題図（画像）項目レイヤ情報 */
	public List<JSONLayerInfo> overlayLayerInfos = new ArrayList<JSONLayerInfo>();

	/** 外部地図レイヤ情報 */
	public List<JSONLayerInfo> externalMapLayerInfos = new ArrayList<JSONLayerInfo>();

	/** レイアウト情報 */
	public LayoutInfo layoutInfo;

	/** サイトの縮尺 */
	public Long[] scales;

	/**
	 * レイヤ情報をリストで取得します.
	 * @return レイヤ情報リスト
	 */
	public List<JSONLayerInfo> getJSONLayerInfos() {
		List<JSONLayerInfo> list = new ArrayList<MapInitDto.JSONLayerInfo>();
		list.addAll(baseLayerInfos);
		list.addAll(contentsLayerInfos);
		list.addAll(kmlLayerInfos);
		list.addAll(groupContentsLayerInfos);
		list.addAll(overlayLayerInfos);
		list.addAll(referenceLayerInfoMap.values());
		return list;
	}

	/**
	 * レイヤ情報をHashMapで取得します.
	 * @return レイヤIDがキーのHashMap
	 */
	public Map<String, JSONLayerInfo> getJSONLayerInfoMap() {
		Map<String, JSONLayerInfo> map = new LinkedHashMap<String, MapInitDto.JSONLayerInfo>();
		List<JSONLayerInfo> list = getJSONLayerInfos();
		for(JSONLayerInfo jsonLayerInfo : list) {
			map.put(jsonLayerInfo.layerInfo.layerId, jsonLayerInfo);
		}
		return map;
	}

	/**
	 * @return レイヤIDをリストで取得します.
	 */
	public List<String> getLayerIds() {
		Map<String, JSONLayerInfo> map = getJSONLayerInfoMap();
		List<String> layerIds = new ArrayList<String>();
		layerIds.addAll(map.keySet());
		return layerIds;
	}

	/**
	 * 指定レイヤを初期化データから削除します.
	 * @param jsonLayerInfo
	 */
	public void remove(JSONLayerInfo jsonLayerInfo) {
		baseLayerInfos.remove(jsonLayerInfo);
		contentsLayerInfos.remove(jsonLayerInfo);
		overlayLayerInfos.remove(jsonLayerInfo);
		referenceLayerInfoMap.remove(jsonLayerInfo.layerInfo.layerId);
		kmlLayerInfos.remove(jsonLayerInfo);
	}

	/**
	 * 指定レイヤ順でソートします.
	 * @param layerIds
	 */
	public void sort(List<String> layerIds) {
		// 指定レイヤ順で比較
		Comparator<MapLayerInfo> comparator = new MapLayerInfoComparator(layerIds);

		Vector<MapLayerInfo> mapLayerInfos = (Vector<MapLayerInfo>) mapInfo.getMapLayerIterable();
		Collections.sort(mapLayerInfos, comparator);

		// 指定されていないレイヤは取り除く
		List<MapLayerInfo> removes = new ArrayList<MapLayerInfo>();
		for(MapLayerInfo mapLayerInfo : mapLayerInfos) {
			String layerId = mapLayerInfo.layerId;
			if(layerIds.contains(layerId)==false) {
				removes.add(mapLayerInfo);
			}
		}
		mapLayerInfos.removeAll(removes);

	}

	/**
	 * レイヤ情報の親子関係を構築します.
	 * 主題図項目に対応。
	 * 項目グループは未対応。
	 */
	public void buildTree() {
		Map<String, JSONLayerInfo> jsonLayerInfoMap = getJSONLayerInfoMap();
		for(Map.Entry<String, JSONLayerInfo> entry : jsonLayerInfoMap.entrySet()) {
			JSONLayerInfo jsonLayerInfo = entry.getValue();
			// 親に追加
			String parentLayerId = jsonLayerInfo.mapLayerInfo.parent;
			if(parentLayerId!=null) {
				JSONLayerInfo parent = jsonLayerInfoMap.get(parentLayerId);
				if(parent!=null) {
					jsonLayerInfo.parent = parent;
					parent.children.add(jsonLayerInfo);
				}
			}
		}
	}

	/**
	 * 指定したレイヤIDのみ残します.
	 * 対象外のレイヤ情報は初期化データから取り除きます.
	 * 親子関係がある場合は以下のように処理します.
	 * ・親を指定された場合は自動で子をすべて残します.
	 * ・子を指定された場合は自動で親をすべて残します.
	 * @param layerIds
	 */
	public void filter(List<String> layerIds) {
		// レイヤIDをコピーする
		List<String> filterLayerIds = new ArrayList<String>();
		filterLayerIds.addAll(layerIds);
		// HashMapを取得
		Map<String, JSONLayerInfo> jsonLayerInfoMap = getJSONLayerInfoMap();
		// 取り除くレイヤID
		List<String> excludeLayerIds = getLayerIds();
		// 残すレイヤ情報はexcludeからはずす
		for(String layerId : filterLayerIds) {
			JSONLayerInfo jsonLayerInfo = jsonLayerInfoMap.get(layerId);
			if(jsonLayerInfo!=null) {
				excludeLayerIds.remove(jsonLayerInfo.layerId);
				excludeLayerIds.removeAll(jsonLayerInfo.getParentLayerIds());
				// 子のために自動で追加された親の場合
				// その親の子レイヤがすべて表示されてしまうため
				// いずれかの子に設定があるかチェックする
				boolean existChildLayerId = false;
				for(String clid : jsonLayerInfo.getChildLayerIds()) {
					if(layerIds.contains(clid)) {
						existChildLayerId = true;
						break;
					}
				}
				// 子が指定されていない場合は、すべての子を追加する
				if(existChildLayerId==false) {
					excludeLayerIds.removeAll(jsonLayerInfo.getChildLayerIds());
				}
			}
		}

		// exclude対象のレイヤを初期化データから削除します.
		for(String excludeLayerId : excludeLayerIds) {
			JSONLayerInfo jsonLayerInfo = jsonLayerInfoMap.get(excludeLayerId);
			remove(jsonLayerInfo);
		}
	}

	/**
	 * レイヤ情報をJSON形式で出力するためのクラスです.
	 */
	public static class JSONLayerInfo {

		static Logger logger = Logger.getLogger(JSONLayerInfo.class);

		/** サイトID */
		public int communityId;

		/** 地図ID */
		public long mapId;

		/** レイヤID */
		public String layerId;

		/** MapLayerInfo */
		public MapLayerInfo mapLayerInfo;
		/** LayerInfo */
		public LayerInfo layerInfo;

		/** 親JSONLayerInfo */
		public JSONLayerInfo parent;
		/** 子JSONLayerInfos */
		public List<JSONLayerInfo> children = new ArrayList<JSONLayerInfo>();
		/** 追加フラグ */
		public boolean addable = false;
		/** 編集フラグ */
		public boolean editable = false;

		/** 属性表示順 */
		public List<String> attrDisporder;

		/** メタデータJSON */
		public JSONObject metadata;

		/**
		 * レイヤ情報の拡張プロパティ設定.
		 * Map<プロパティ名, 値>
		 */
		public Map<String, Object> exLayerProperties = new HashMap<String, Object>();

		/**
		 * 属性の拡張プロパティ設定.
		 * Map<属性ID, Map<プロパティ名, 値>>
		 */
		public Map<String, Map<String, Object>> exAttrProperties =
				new HashMap<String, Map<String, Object>>();

		/** デフォルトコンストラクタ */
		public JSONLayerInfo() {}

		/**
		 * レイヤIDとレイヤ名を指定して新規にレイヤ情報を生成する.
		 * 登録情報レイヤの場合は {@link JSONLayerInfo#JSONLayerInfo(int, long, String, String)}を利用してください。
		 * @param layerId レイヤID
		 * @param name レイヤ名
		 */
		public JSONLayerInfo(String layerId, String name) {
			this(0, 0L, layerId, name);
		}

		/**
		 * レイヤIDとレイヤ名を指定して新規にレイヤ情報を生成する.
		 * @param communityId サイトID
		 * @param mapId 地図ID
		 * @param layerId レイヤID
		 * @param name レイヤ名
		 */
		public JSONLayerInfo(int communityId, long mapId, String layerId, String name) {
			JSONLayerInfo jsonLayerInfo = this;
			jsonLayerInfo.communityId = communityId;
			jsonLayerInfo.mapId = mapId;
			jsonLayerInfo.layerId = layerId;

			jsonLayerInfo.layerInfo = new LayerInfo();
			jsonLayerInfo.layerInfo.layerId = layerId;
			jsonLayerInfo.layerInfo.name = name;

			jsonLayerInfo.mapLayerInfo = new MapLayerInfo();
			jsonLayerInfo.mapLayerInfo.layerId = layerId;
			jsonLayerInfo.mapLayerInfo.layerName = name;
		}

		/**
		 * MapLayerInfoとLayerInfoの値をコピーしてインスタンスを生成します.
		 * 登録情報レイヤの場合は {@link JSONLayerInfo#JSONLayerInfo(int, long, MapLayerInfo, LayerInfo)}を利用してください。
		 * @param mapLayerInfo
		 * @param layerInfo
		 */
		public JSONLayerInfo(MapLayerInfo mapLayerInfo, LayerInfo layerInfo) {
			this(0, 0L, mapLayerInfo, layerInfo);
		}

		/**
		 * MapLayerInfoとLayerInfoの値をコピーしてインスタンスを生成します.
		 * @param communityId サイトID
		 * @param mapId 地図ID
		 * @param mapLayerInfo
		 * @param layerInfo
		 */
		public JSONLayerInfo(int communityId, long mapId, MapLayerInfo mapLayerInfo, LayerInfo layerInfo) {
			JSONLayerInfo jsonLayerInfo = this;
			jsonLayerInfo.communityId = communityId;
			jsonLayerInfo.mapId = mapId;

			this.layerId = layerInfo.layerId;
			this.mapLayerInfo = mapLayerInfo;
			this.layerInfo = layerInfo;
		}

		/**
		 * このインスタンスをJSONに変換します.
		 * @return JSONObject
		 */
		public JSONObject toJSON() {
			return toJSON(this, exLayerProperties, exAttrProperties);
		}

		/**
		 * JSONに変換します.
		 * @param jsonLayerInfo JSONレイヤ情報
		 * @param exLayerProperties 拡張レイヤマップ
		 * @param exAttrProperties 拡張属性マップ
		 * @return JSONObject
		 */
		public static JSONObject toJSON(JSONLayerInfo jsonLayerInfo, Map<String, Object> exLayerProperties, Map<String, Map<String, Object>> exAttrProperties) {
			// ポップアップ時に詳細を出す代わりにURLを新しいウィンドウで開く属性の名前
			String[] openBlankWindowAttrNames = Config.getStrings("ATTR_NAME.OPEN_BLANK_WINDOW_ON_POPUP");

			JSONObject json = null;
			LayerInfo layerInfo = jsonLayerInfo.layerInfo;
			MapLayerInfo mapLayerInfo = jsonLayerInfo.mapLayerInfo;
			try {
				json = layerInfo.toJSON(layerInfo.getGeometryType(), /*withAttr*/false);
				json.put("wmsFeatureURL", layerInfo.wmsFeatureURL);
				json.put("visibility", mapLayerInfo.visible);
				json.put("addable", jsonLayerInfo.addable);
				json.put("editable", jsonLayerInfo.editable);
				json.put("parent", mapLayerInfo.parent);
				json.put("expanded", !mapLayerInfo.closed);
				json.put("opacity", mapLayerInfo.opacity);
				if(exLayerProperties!=null) {
					for(Map.Entry<String, Object> exLayerProperty : exLayerProperties.entrySet()) {
						json.put(exLayerProperty.getKey(), exLayerProperty.getValue());
					}
				}

				// 凡例別の表示切替対応
				// http://localhost:18080/map/sld?rulelist=1&mid=10&cid=2&layer=c42
				// ["未開設","開設不能","開設指示済","開設済","閉鎖","常設"]
				// これを下記の様に出力する
				// [{ruleId:0, title:"未開設"}, ...]

				if(layerInfo.type==LayerInfo.TYPE_LOCAL) {
					// SLDルールによる凡例の表示切替のURL再現
					List<String> visibleRuleIds = null;
					if(exLayerProperties.containsKey("legendrules_visiblerules")) {
						visibleRuleIds = new ArrayList<>();
						String visibleRules = (String) exLayerProperties.get("legendrules_visiblerules");
						if(StringUtil.isNotEmpty(visibleRules)) {
							boolean first = true;
							for(String visibleRuleId : visibleRules.split(":")) {
								// 最初の要素はレイヤIDが入っているので飛ばす
								if(first) first=false;
								else {
									visibleRuleIds.add(visibleRuleId);
								}
							}
						}
					}

					JSONArray legendrules = new JSONArray();
					try {
						StyledLayerDescriptor sld = SLDServlet.getSld(jsonLayerInfo.communityId, jsonLayerInfo.mapId, layerInfo.layerId);
						if(sld!=null&&0<sld.getStyledLayers().length) {
							NamedLayer namedLayerOrg = (NamedLayer)sld.getStyledLayers()[0];
							Style style = namedLayerOrg.getStyles()[0];
							List<FeatureTypeStyle> fStyles = style.featureTypeStyles();
							List<Rule> rules = fStyles.get(0).rules();
							int ruleId = 0;
							for(Rule rule : rules) {
								JSONObject legendrule = new JSONObject();

								// ルールＩＤ、0ベースのナンバー
								legendrule.put("ruleId", ruleId);

								// タイトル
								String title = rule.getTitle();
								legendrule.put("title", title);

								// 点の場合はアイコンURL、eコミの内部URLになっているので調整する
								/*
								Symbolizer[] symbolizers = rule.getSymbolizers();
								try {
									if(symbolizers!=null && symbolizers.length==1 && symbolizers[0] instanceof PointSymbolizer) {
										PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizers[0];
										URL iconURL = pointSymbolizer.getGraphic().getExternalGraphics()[0].getLocation();
										legendrule.put("iconURL", iconURL.toString());
									}
								} catch(Exception e) {
									logger.error("failed to get symbol external graphic", e);
								}*/

								// 初期の表示状態はレイヤの初期表示にあわせて 全ON または 全OFF
								boolean visibility = mapLayerInfo.visible;
								// SLDルールによる凡例の表示切替のURL再現
								if(visibleRuleIds!=null) {
									visibility = visibleRuleIds.contains(String.valueOf(ruleId));
								}
								legendrule.put("visibility", visibility);
								legendrule.put("searchable", visibility);

								// ルールIDを１つすすめる
								ruleId++;

								legendrules.put(legendrule);
							}
						}
					} catch(Exception e) {
						logger.error("Can not read SLD: "+jsonLayerInfo.communityId+" "+jsonLayerInfo.mapId+" "+layerInfo.layerId, e);
					}
					// LayerInfo の JSON に記録
					json.put("legendrules", legendrules);
				}

				// AttrInfo -> attrInfos
				json.remove("AttrInfo");
				JSONArray attrInfos = MapService.getAttrInfos(layerInfo);
				int visibleAttrNum = 0;
				for(int idx=0; idx<attrInfos.length(); idx++) {
					JSONObject attrInfo = attrInfos.getJSONObject(idx);
					if(AttrInfo.STATUS_SEARCHHIDE<attrInfo.getInt("status")) {
						// 下記の官民用属性はカウント除外
						if(EdituserAttrService.EDITUSER_ATTR_ID.equals(attrInfo.getString("attrId"))) continue;
						if(TrackdataidAttrService.TRACKDATA_ATTR_ID.equals(attrInfo.getString("attrId"))) continue;
						visibleAttrNum++;
					}
				}
				for(int idx=0; idx<attrInfos.length(); idx++) {
					JSONObject attrInfo = attrInfos.getJSONObject(idx);
					// 属性情報を拡張
					String attrId = attrInfo.getString("attrId");
					
					Map<String, Object> exAttrProperty = null;
					if(exAttrProperties!=null) exAttrProperty = exAttrProperties.get(attrId);
					if(exAttrProperty==null) exAttrProperty = new HashMap<String, Object>();

					// ここでチェックしないと、地図レイヤ属性情報を設定しない場合の属性全表示のときに動作しないので。
					// 表示する場合のみリンクURLを有効にする
					if(AttrInfo.STATUS_SEARCHHIDE<attrInfo.getInt("status")) {
						// ポップアップ時に詳細を出す代わりにURLを新しいウィンドウで開く属性を設定
//						if(StringUtil.isNotEmpty(maplayerattrInfo.name) && maplayerattrInfo.name.equals(openBlankWindowAttrName)) {
						String name = attrInfo.getString("name");
						if(ArrayUtil.contains(openBlankWindowAttrNames, name)) {
							try {
								JSONObject buttonData = new JSONObject()
								.put("type", "link")
								.put("text", name)
								.put("open", visibleAttrNum==1);
								exAttrProperty.put("buttonData", buttonData);
							} catch (JSONException e) {
								logger.error(e.getMessage(), e);
							}
						}
					}

					// 拡張属性情報があれば
					for(Map.Entry<String, Object> exAttrPropertyEntry : exAttrProperty.entrySet()) {
						String key = exAttrPropertyEntry.getKey();
						Object value = exAttrPropertyEntry.getValue();
						attrInfo.put(key, value);
					}
				}
				json.put("attrInfos", attrInfos);

			} catch (JSONException e) {
				Logger.getLogger(JSONLayerInfo.class).error(e.getMessage(), e);
			}
			return json;
		}

		/**
		 * @return 親レイヤIDをすべて返します.
		 * 先祖をすべてたどります.
		 */
		public List<String> getParentLayerIds() {
			List<String> layerIds = new ArrayList<String>();
			if(parent!=null) {
				layerIds.addAll(parent.getParentLayerIds());
				layerIds.add(parent.layerId);
			}
			return layerIds;
		}

		/**
		 * @return 子レイヤIDをすべて返します.
		 * 孫もすべてたどります.
		 */
		public List<String> getChildLayerIds() {
			List<String> layerIds = new ArrayList<String>();
			if(0<children.size()) {
				for(JSONLayerInfo child : children) {
					layerIds.add(child.layerId);
					layerIds.addAll(child.getChildLayerIds());
				}
			}
			return layerIds;
		}

		/**
		 * 指定の属性ID順でソートします.
		 * @param attrIds
		 */
		public void sortAttr(final List<String> attrIds) {
			Vector<AttrInfo> attrInfos = (Vector<AttrInfo>) layerInfo.getAttrIterable();
			Collections.sort(attrInfos, new Comparator<AttrInfo>() {
				@Override
				public int compare(AttrInfo o1, AttrInfo o2) {
					int idx1 = attrIds.indexOf(o1.attrId);
					int idx2 = attrIds.indexOf(o2.attrId);
					if(idx1==idx2) return 0;
					// どちらかが存在しない場合
					if(idx1==-1) return 1;
					if(idx2==-1) return -1;
					// 両方存在する場合
					return idx1<idx2 ? -1 : 1;
				}
			});
		}
	}

	/**
	 * 主題図項目レイヤ情報のクラスです.
	 */
	public static class ReferenceJSONLayerInfo extends JSONLayerInfo {

		/** 属する項目レイヤ情報 */
		public List<LayerInfo> childLayerInfos = new ArrayList<LayerInfo>();

		/**
		 * MapLayerInfoとLayerInfoの値をコピーしてインスタンスを生成します.
		 * @param communityId サイトID
		 * @param mapId 地図ID
		 * @param mapLayerInfo
		 * @param layerInfo
		 */
		public ReferenceJSONLayerInfo(int communityId, long mapId, MapLayerInfo mapLayerInfo, LayerInfo layerInfo) {
			super(communityId, mapId, mapLayerInfo, layerInfo);
		}

		/**
		 * このインスタンスのJSONオブジェクトを生成します.
		 * @return JSONObject
		 */
		public JSONObject toJSON(){
			JSONObject json = null;
			try {
				json = super.toJSON();

				// 属する項目レイヤ情報
				JSONArray array = new JSONArray();
				json.put("childLayerInfos", array);
				for(LayerInfo childLayerInfo : childLayerInfos) {
					array.put(LayerInfo.toJSON(childLayerInfo));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return json;
		}
	}

	/**
	 * レイヤID順でソートするクラスです.
	 */
	public static class MapLayerInfoComparator implements Comparator<MapLayerInfo>, Serializable {

		private static final long serialVersionUID = 1L;
		private List<String> layerIds;

		/**
		 * @param layerIds このレイヤＩＤリストでソートする
		 */
		public MapLayerInfoComparator(List<String> layerIds) {
			this.layerIds = layerIds;
		}

		@Override
		public int compare(MapLayerInfo o1, MapLayerInfo o2) {
			int idx1 = layerIds.indexOf(o1.layerId);
			int idx2 = layerIds.indexOf(o2.layerId);
			if(idx1==idx2) return 0;
			// どちらかが存在しない場合
			if(idx1==-1) return 1;
			if(idx2==-1) return -1;
			// 両方存在する場合
			return idx1<idx2 ? -1 : 1;
		}
	};

	/**
	 * 地図初期化データの設定クラスです.
	 */
	public static class MapInitConfig {

		Logger logger = Logger.getLogger(MapInitConfig.class);

		// レイヤ関連
		/** 追加対象のレイヤ */
		public List<String> layerIds = new ArrayList<String>();

		/** 非表示レイヤ **/
		public List<String> hiddenLayerIds = new ArrayList<String>();

		/** 凡例を折りたたまないレイヤ */
		public List<String> expandLayerIds = new ArrayList<String>();

		/** 新規登録レイヤ */
		public List<String> addableLayerIds = new ArrayList<String>();

		/** ポップアップ編集レイヤ */
		public List<String> editableLayerIds = new ArrayList<String>();

		/**
		 * レイヤ情報の拡張プロパティ設定.
		 * Map<プロパティ名, 値>
		 */
		public Map<String, Map<String, Object>> exLayerPropertyMap = new HashMap<String, Map<String, Object>>();

		/**
		 * レイヤの拡張プロパティを設定します.
		 * @param layerId レイヤID
		 * @param propertyName 拡張プロパティ名
		 * @param propertyValue 拡張プロパティ値
		 */
		public void putExLayerProperty(String layerId, String propertyName, Object propertyValue) {
			// レイヤ拡張プロパティを取得
			Map<String, Object> exLayerProperty = exLayerPropertyMap.get(layerId);
			if(exLayerProperty==null) {
				exLayerProperty = new HashMap<String, Object>();
				exLayerPropertyMap.put(layerId, exLayerProperty);
			}

			// レイヤ拡張プロパティを設定
			exLayerProperty.put(propertyName, propertyValue);
		}

		// 属性関連
		/** レイヤ毎の属性ステータスのLinkedHashMap */
		public Map<String, LinkedHashMap<String, Short>> layerAttrStatusMap = new HashMap<String, LinkedHashMap<String, Short>>();

		/** 属性ステータスのデフォルト設定 */
		public Short attrDefaultStatus = AttrInfo.STATUS_DEFAULT;

		/**
		 * 属性の拡張プロパティのMap
		 * Map<レイヤID, exAttrProperties>
		 */
		public Map<String, Map<String, Map<String, Object>>> exAttrPropertiesMap = new HashMap<String, Map<String, Map<String, Object>>>();

		/**
		 * 属性の拡張プロパティを設定します.
		 * @param layerId レイヤID
		 * @param attrId 属性ID
		 * @param propertyName 拡張プロパティ名
		 * @param propertyValue 拡張プロパティ値
		 */
		public void putExAttrProperty(String layerId, String attrId, String propertyName, Object propertyValue) {
			// 属性拡張プロパティMapを取得
			Map<String, Map<String, Object>> exAttrProperties = exAttrPropertiesMap.get(layerId);
			if(exAttrProperties==null) {
				exAttrProperties = new HashMap<String, Map<String, Object>>();
				exAttrPropertiesMap.put(layerId, exAttrProperties);
			}

			// 属性拡張プロパティを取得
			Map<String, Object> exAttrProperty = exAttrProperties.get(attrId);
			if(exAttrProperty==null) {
				exAttrProperty = new HashMap<String, Object>();
				exAttrProperties.put(attrId, exAttrProperty);
			}

			// 属性拡張プロパティを設定
			exAttrProperty.put(propertyName, propertyValue);
		}

		/**
		 * 設定を反映します.
		 * @param mapInitDto
		 */
		public void config(MapInitDto mapInitDto) {
			MapInitConfig config = this;
			// 指定レイヤID以外を取り除く
			mapInitDto.filter(config.layerIds);

			// レイヤ情報の設定を上書きする
			List<JSONLayerInfo> jsonLayerInfos = mapInitDto.getJSONLayerInfos();
			for(JSONLayerInfo jsonLayerInfo : jsonLayerInfos) {
				String layerId = jsonLayerInfo.layerInfo.layerId;

				// デフォルト表示状態を設定
				jsonLayerInfo.mapLayerInfo.visible = !config.hiddenLayerIds.contains(layerId);

				// デフォルト凡例の折りたたみを設定
				jsonLayerInfo.mapLayerInfo.closed = !config.expandLayerIds.contains(layerId);

				// ポップアップ編集レイヤ
				jsonLayerInfo.editable = config.editableLayerIds.contains(layerId);
				jsonLayerInfo.addable = config.addableLayerIds.contains(layerId);

				// レイヤ拡張プロパティの設定
				Map<String, Object> exLayerProperties = config.exLayerPropertyMap.get(layerId);
				if(exLayerProperties!=null) {
					jsonLayerInfo.exLayerProperties.putAll(exLayerProperties);
				}

				// 属性設定
				LinkedHashMap<String, Short> attrStatusMap = layerAttrStatusMap.get(layerId);
				if(attrStatusMap!=null) {
					Short defaultStatus = attrDefaultStatus;
					for(AttrInfo attrInfo : jsonLayerInfo.layerInfo.getAttrIterable()) {
						Short status = attrStatusMap.get(attrInfo.attrId);
						if(status==null) status = defaultStatus;
						attrInfo.status = status;
					}

					// 属性の並び順
					jsonLayerInfo.sortAttr(new ArrayList<String>(attrStatusMap.keySet()));
				}
				// 属性拡張プロパティの設定
				jsonLayerInfo.exAttrProperties = exAttrPropertiesMap.get(layerId);
			}

			// レイヤの並び順を指定しなおす
			mapInitDto.sort(config.layerIds);
		}
	}
}

