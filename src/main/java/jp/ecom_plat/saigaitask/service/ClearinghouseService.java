/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import static jp.ecom_plat.saigaitask.entity.Names.externalmapdataInfo;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.geotools.data.ows.CRSEnvelope;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.Base64Util;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerGroupInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapGroupInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.osw.WMSUtils;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.MapInitDto.JSONLayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadatadefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadataInfoNames;
import jp.ecom_plat.saigaitask.service.db.ClearinghousemetadataInfoService;
import jp.ecom_plat.saigaitask.service.db.ClearinghousemetadatadefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternalmapdataInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternaltabledataInfoService;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.util.ArcGISUtil;
import jp.ecom_plat.saigaitask.util.CKANUtil;
import jp.ecom_plat.saigaitask.util.CSWUtil;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.MetadataUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SpringContext;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import jp.ecom_plat.saigaitask.util.WMSUtil;

/**
 * クリアリングハウスの検索を行うサービスクラスです.
 */
@org.springframework.stereotype.Service
public class ClearinghouseService {

	Logger logger = Logger.getLogger(ClearinghouseService.class);

	/** 外部地図用のレイヤ種別 WMSレイヤ(LAYERSの外部地図を子に持つレイヤ) */
	public static final short LAYER_TYPE_EXTERNALMAP_WMS = 900;
	/** 外部地図用のレイヤ種別 WMSレイヤのLAYERS用 */
	public static final short LAYER_TYPE_EXTERNALMAP_WMS_LAYERS = 901;
	/** 外部地図用のレイヤ種別 XYZレイヤ */
	public static final short LAYER_TYPE_EXTERNALMAP_XYZ = 910;

	protected SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	@Resource private LoginDataDto loginDataDto;
	@Resource private HttpSession session;
    @Resource private ExternalmapdataInfoService externalmapdataInfoService;
	@Resource protected ExternaltabledataInfoService externaltabledataInfoService;
	@Resource protected ClearinghousemetadataInfoService clearinghousemetadataInfoService;
	@Resource protected ClearinghousemetadatadefaultInfoService clearinghousemetadatadefaultInfoService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected TrackmapInfoService trackmapInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected LocalgovInfoService localgovInfoService;

    /**
     * クリアリングハウスよりメタデータを取得する
     * @param metadataid メタデータID
     * @param isTraining 訓練フラグ
     * @return メタデータのJSONObject
     * @throws JSONException
     */
    public JSONObject getRecordById(String metadataid, boolean isTraining) throws JSONException {
		long starttime = System.currentTimeMillis();

		String cswurl = isTraining ? Config.getString("CSWURL_TRAINING") : Config.getString("CSWURL");
		String cswuser = isTraining ? Config.getString("CSWUSER_TRAINING") : Config.getString("CSWUSER");
		String cswpasswd = isTraining ? Config.getString("CSWPASSWD_TRAINING") : Config.getString("CSWPASSWD");
		String xml = CSWUtil.getRecordById(cswurl, cswuser, cswpasswd, metadataid);
		String metadata = CSWUtil.MDMetadataToJSON(xml);

		// 改行をエスケープする
		metadata = metadata.replaceAll("\n", "\\\\n");

		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] ClearinghouseService.getRecordById elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");

		return new JSONObject(metadata);
	}


	/**
	 * クリアリングハウスよりメタデータを取得し、レイヤー情報を作成する
     * @param isTraining 訓練フラグ
	 * @return JSONレイヤ情報リスト
	 */
	public List<JSONLayerInfo> getMapLayers(Long menuid, boolean isTraining) {
		long starttime = System.currentTimeMillis();

		List<JSONLayerInfo>jsonLayerList = new ArrayList<JSONLayerInfo>();

		// DBよりクリアリングハウスメタデータを検索する
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put(externalmapdataInfo().menuinfoid().toString(), menuid);
		List<ExternalmapdataInfo> list =
			externalmapdataInfoService.findByCondition(conditions, externalmapdataInfo().disporder().toString(), "asc", null, null);

		// セッションよりクリアリングハウスメタデータを取得する
		List<ExternalmapdataInfo> sessionExternalmapdataInfos = getSessionExternalmapdataInfos(menuid, isTraining);
		list.addAll(sessionExternalmapdataInfos);

		// JSONLayerInfo を作成する
		int counter=1;
		for (ExternalmapdataInfo info : list) {
			long starttime2 = System.currentTimeMillis();
			try {
				// 子は、親の登録時に一括で登録する
				if(info.layerparent!=null && info.layerparent!=0) continue;

				// 子のレイヤ情報を検索してあれば保持する
				List<ExternalmapdataInfo> childs = null;
				for(ExternalmapdataInfo child : list) {
					if(info.id.equals(child.layerparent)) {
						if(childs==null) {
							childs= new ArrayList<ExternalmapdataInfo>();
						}
						childs.add(child);
					}
				}

				// レイヤIDの発行
				boolean isSession = sessionExternalmapdataInfos.contains(info);
				String extLayerId = "extmap_" +(isSession?"session_":"")+ info.id;

				try {
					// DBから取得したクリアリングハウスメタデータに
					// WMSCapabilitiesのURLが設定済みの場合
					if(info.wmscapsurl != null && info.wmscapsurl.length() > 0){
						// layerIds の設定
						String[] layerIds = null;
						if(childs!=null) {
							layerIds = new String[childs.size()];
							for(int idx=0; idx<childs.size(); idx++) {
								layerIds[idx] = childs.get(idx).featuretypeid;
							}
						}
						//if(isSession) {
						if(StringUtil.isNotEmpty(info.wmscapsurl)) {
							String wmsCapsUrl = info.wmscapsurl;
							// WMS に LAYERS パラメータからレイヤIDを取得
							int queryIdx = wmsCapsUrl.indexOf("?") + 1;
							String query = wmsCapsUrl.substring(queryIdx);
							if(StringUtil.isNotEmpty(query)) {
								String[] params = query.split("&");
								for(String param : params) {
									String[] elems = param.split("=");
									if(elems.length==2) {
										String key = elems[0];
										String value= elems[1];
										if("layers".equalsIgnoreCase(key)) {
											if(StringUtil.isNotEmpty(value)) {
												layerIds = value.split(",");
											}
										}
									}
								}
							}
						}

						// JSONLayerInfo を作成して追加
						if(ArcGISUtil.isArcGISLayer(info))
							jsonLayerList.addAll(createArcGISJSONLayerInfos(info, childs, info.wmscapsurl, extLayerId, layerIds));
						else {
							// JSONLayerInfo を作成して追加
							List<JSONLayerInfo> createlist = createWMSJSONLayerInfos(info, childs, info.wmscapsurl, extLayerId, layerIds);
							// クリアリングハウスより情報を取得する
							if(StringUtil.isNotEmpty(info.metadataid)) {
								JSONObject json = getRecordById(info.metadataid, isTraining);

								// JSONメタデータを設定
								for(JSONLayerInfo jli : createlist) {
									jli.metadata = json;
								}
							}
							jsonLayerList.addAll(createlist);
						}

					// 従来のクリアリングハウスから取得するコード
					}else{
						// クリアリングハウスより情報を取得する
						JSONObject json = getRecordById(info.metadataid, isTraining);

						// WMSとして追加する
						if(json.has("wmsurl") && StringUtil.isNotEmpty(json.get("wmsurl").toString()) && "null".equals(json.get("wmsurl").toString())==false) {
							// WMS の URL を取得する
							String wmsCapsUrl = json.get("wmsurl").toString();

							// JSONLayerInfo を作成して追加
							info.wmscapsurl = wmsCapsUrl;
							// クリアリングハウスからArcGISLayer 追加
							if(ArcGISUtil.isArcGISLayer(info))
								jsonLayerList.addAll(createArcGISJSONLayerInfos(info, childs, info.wmscapsurl, extLayerId, /*layerIds*/null));
							// WMS Layer
							else {
								// WMS に LAYERS パラメータからレイヤIDを取得
								String[] layerIds = null;
								int queryIdx = wmsCapsUrl.indexOf("?") + 1;
								String query = wmsCapsUrl.substring(queryIdx);
								if(StringUtil.isNotEmpty(query)) {
									String[] params = query.split("&");
									for(String param : params) {
										String[] elems = param.split("=");
										if(elems.length==2) {
											String key = elems[0];
											String value= elems[1];
											if("layers".equalsIgnoreCase(key)) {
												if(StringUtil.isNotEmpty(value)) {
													layerIds = value.split(",");
												}
											}
										}
									}
								}

								// JSONLayerInfo を作成して追加
								List<JSONLayerInfo> createlist = createWMSJSONLayerInfos(info, childs, wmsCapsUrl, extLayerId, layerIds);
								// JSONメタデータを設定
								for(JSONLayerInfo jli : createlist) {
									jli.metadata = json;
								}
								jsonLayerList.addAll(createlist);
							}
						}
						// XYZ として追加する
						else if(json.has("xyzurl") && StringUtil.isNotEmpty(json.get("xyzurl").toString()) && "null".equals(json.get("xyzurl").toString())==false) {
							// XYZ の URL を取得する
							String xyzUrl = json.get("xyzurl").toString();
							// オプション
							JSONObject option = null;
							if(json.has("xyzdesc") && StringUtil.isNotEmpty(json.get("xyzdesc").toString()) && "null".equals(json.get("xyzdesc").toString())==false) {
								String description = json.getString("xyzdesc");
								if(StringUtil.isNotEmpty(description)) {
									try {
										option = new JSONObject(description);
									} catch(JSONException e) {
										logger.warn(lang.__("Unable to load description of external map data info : {0}\"{1}\" metadataID:{2} as JSON format options.", info.id, info.name, info.metadataid), e);
									}
								}
							}

							jsonLayerList.add(createXYZJSONLayerInfo(info, extLayerId, xyzUrl, option));
						}
						// 対応できない場合
						else {
							throw new ServiceException("not supported metadata "+info.metadataid);
						}
					}
				} catch (Exception e) {
					logger.warn(lang.__("Unable to add external map data info : {0} \"{1}\" to map", info.id, info.name), e);

					// エラー用JSONLayerInfo を作成する
					try {
						LayerInfo layerInfo = LayerInfo.createReferenceLayerInfo(extLayerId, info.name, null, LayerInfo.STATUS_DEFAULT, LAYER_TYPE_EXTERNALMAP_WMS, 0, 0, "", null);
						// MapLayerInfo 作成
						MapLayerInfo mapLayerInfo = new MapLayerInfo();
						mapLayerInfo.setValues(layerInfo.layerId, "", layerInfo.type, null, "", info.visible, info.closed, 1, 1, 1, "", 0, "", "", "");
						// レイヤ情報を JSON 形式に変換し、リストに追加する
						JSONLayerInfo jsonLayerInfo = new JSONLayerInfo(mapLayerInfo, layerInfo);
						jsonLayerInfo.exLayerProperties.put("metadataid", info.metadataid);
						jsonLayerInfo.exLayerProperties.put("searchable", false);
						jsonLayerInfo.exLayerProperties.put("loaderror", true);
						jsonLayerList.add(jsonLayerInfo);
					} catch(Exception e2) {
						logger.error(lang.__("Unable to add external map data info : {0} \"{1}\" to map", info.id, info.name), e2);
					}
				}
			} finally {
				long endtime2 = System.currentTimeMillis();
				logger.info("[MethodDuration] ClearinghouseService.getMapLayers["+counter+"/"+list.size()+"] elapsed: "+String.format("%.2f", (double)(endtime2-starttime2)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime2))+")");
				counter++;
			}
		}

		// 外部地図のマージ処理
		// WMS Layer: 自治体コードとWMS URL が同じならマージ
		{
			List<JSONLayerInfo> merged = new ArrayList<JSONLayerInfo>();
			JSONLayerInfo parent = null;
			final String CITY = "Ident_city";
			final String ADMIN_AREA = "Ident_adminArea";
			final String CITY_CODE = "Ident_cityCode";
			final String ADMIN_AREA_CODE = "Ident_adminAreaCode";
			final String CONTACT_NAME = "contactname";
			for(JSONLayerInfo jsonLayerInfo : jsonLayerList) {
				short type = jsonLayerInfo.layerInfo.type;
				// WMS 親レイヤ
				if(type==LAYER_TYPE_EXTERNALMAP_WMS) {
					boolean execMerge = false;
					Boolean loaderror = (Boolean) jsonLayerInfo.exLayerProperties.get("loaderror");
					if(/*読み込みエラーが発生していない*/(loaderror==null||!loaderror) && parent!=null) {
						// 同じWMSURLならマージ実行する(layersパラメータ以降は官民共同用のため除外して比較する)
						Boolean isSameWMSURL = null;
						if(parent.layerInfo.wmsURL!=null) {
							String pwmsURL = parent.layerInfo.wmsURL.replaceAll("layers=*", "");
							if(jsonLayerInfo.layerInfo.wmsURL!=null) {
								String wmsURL = jsonLayerInfo.layerInfo.wmsURL.replaceAll("layers=*", "");
								isSameWMSURL = pwmsURL.equals(wmsURL);
							}
						}

						// 同じ自治体コードかどうか
						Boolean isSameLocalgov = null;
						if(parent.metadata!=null && jsonLayerInfo.metadata!=null) {
							try {
								String parentAdminAreaCode = parent.metadata.isNull(ADMIN_AREA_CODE) ? null : parent.metadata.getString(ADMIN_AREA_CODE);
								String parentCityCode = parent.metadata.isNull(CITY_CODE) ? null : parent.metadata.getString(CITY_CODE);
								String adminAreaCode = jsonLayerInfo.metadata.isNull(ADMIN_AREA_CODE) ? null : jsonLayerInfo.metadata.getString(ADMIN_AREA_CODE);
								String cityCode = jsonLayerInfo.metadata.isNull(CITY_CODE) ? null : jsonLayerInfo.metadata.getString(CITY_CODE);
								// 県コードが指定されている場合
								if(StringUtil.isNotEmpty(parentAdminAreaCode)) {
									if(StringUtil.isEmpty(adminAreaCode)) isSameLocalgov = false;
									else isSameLocalgov = adminAreaCode.equals(parentAdminAreaCode);
								}
								// 県コード一致の場合は市町村コードもチェック
								if(StringUtil.isNotEmpty(parentCityCode) && isSameLocalgov) {
									if(StringUtil.isEmpty(cityCode)) isSameLocalgov = false;
									else isSameLocalgov = cityCode.equals(parentCityCode);
								}
							} catch(JSONException e) {
								logger.error("merge failed: "+jsonLayerInfo.layerId, e);
							}
						}
						
						Boolean isSameContactname = null;
						if(parent.metadata!=null && jsonLayerInfo.metadata!=null) {
							try {
								// for CKAN
								String organizationName = CKANUtil.getOrganizationNameByRecord(jsonLayerInfo.metadata);
								if(organizationName!=null) {
									String parentOrganizationName = CKANUtil.getOrganizationNameByRecord(parent.metadata);
									// 組織名称が同じかどうか
									if(StringUtil.isNotEmpty(parentOrganizationName)) {
										if(StringUtil.isEmpty(organizationName)) isSameContactname = false;
										else isSameContactname = organizationName.equals(parentOrganizationName);
									}
								}
								// for nied-ClearingHouse
								else {
									String parentContactName = parent.metadata.isNull(CONTACT_NAME) ? null : parent.metadata.getString(CONTACT_NAME);
									String contactName = jsonLayerInfo.metadata.isNull(CONTACT_NAME) ? null : jsonLayerInfo.metadata.getString(CONTACT_NAME);
									// 問い合わせ先の名称が同じかどうか
									if(StringUtil.isNotEmpty(parentContactName)) {
										if(StringUtil.isEmpty(contactName)) isSameContactname = false;
										else isSameContactname = contactName.equals(parentContactName);
									}
								}
							} catch(JSONException e) {
								logger.error("merge failed: "+jsonLayerInfo.layerId, e);
							}
						}

						if(isSameWMSURL==null/*判定不可*/ || isSameWMSURL) execMerge = true;
						if(isSameContactname!=null) {
							if(isSameContactname!=null && isSameContactname==false) execMerge = false; // 問い合わせ先の名称が違う場合はマージしない
						}
						else {
							if(isSameLocalgov!=null && isSameLocalgov==false) execMerge = false; // 別自治体ならマージしない
						}
					}

					// マージできるときはparentを更新せず前のものを使う
					if(execMerge) {
						// do not add merged list
					}
					// マージできなければ、親をそのまま追加する
					else {
						merged.add(jsonLayerInfo);
						parent = jsonLayerInfo;
						// rename title
						if(jsonLayerInfo.metadata!=null) {
							try {
								String adminAreaCode = jsonLayerInfo.metadata.isNull(ADMIN_AREA_CODE) ? null : jsonLayerInfo.metadata.getString(ADMIN_AREA_CODE);
								String cityCode = jsonLayerInfo.metadata.isNull(CITY_CODE) ? null : jsonLayerInfo.metadata.getString(CITY_CODE);
								String adminArea = jsonLayerInfo.metadata.isNull(ADMIN_AREA) ? null : jsonLayerInfo.metadata.getString(ADMIN_AREA);
								String city = jsonLayerInfo.metadata.isNull(CITY) ? null : jsonLayerInfo.metadata.getString(CITY);
								String organizationName = CKANUtil.getOrganizationNameByRecord(jsonLayerInfo.metadata);
								String contactName = jsonLayerInfo.metadata.isNull(CONTACT_NAME) ? null : jsonLayerInfo.metadata.getString(CONTACT_NAME);
								// for CKAN 組織名称が指定されている場合
								if(organizationName!=null) {
									jsonLayerInfo.layerInfo.name = organizationName;
								}
								// 問い合わせ先の名称が指定されている場合
								else if(StringUtil.isNotEmpty(contactName)) {
									jsonLayerInfo.layerInfo.name = contactName;
								}
								// 県コードが指定されている場合
								// 本来は「地図データのタイトル」の「危機管理/レイヤ名称/自治体名」を使った方がよいかもしれない。
								else if(StringUtil.isNotEmpty(adminAreaCode)) {
									jsonLayerInfo.layerInfo.name = adminArea;
									if(StringUtil.isNotEmpty(cityCode)) {
										jsonLayerInfo.layerInfo.name += city;
									}
								}
								// 県コードも指定されていない場合
								else {
									if(StringUtil.isNotEmpty(city)) {
										jsonLayerInfo.layerInfo.name = city;
									}
									else if(StringUtil.isNotEmpty(adminArea)) {
										jsonLayerInfo.layerInfo.name = adminArea;
									}
								}
							} catch(JSONException e) {
								logger.error("merge failed: "+jsonLayerInfo.layerId, e);
							}
						}
					}
				}
				// WMS 子レイヤ
				else if(type==LAYER_TYPE_EXTERNALMAP_WMS_LAYERS) {
					// 親レイヤID更新
					jsonLayerInfo.mapLayerInfo.parent = parent.layerId;
					merged.add(jsonLayerInfo);
				}
				// その他レイヤタイプ
				else {
					merged.add(jsonLayerInfo);
					parent = null;
				}
			}
			jsonLayerList = merged;
		}

		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] ClearinghouseService.getMapLayers elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");

		return jsonLayerList;
	}

	/**
	 * WMSのJSONLayerInfoを作成する
	 * @param info 外部地図データ情報
	 * @param childs layers用の外部地図データ情報
	 * @param wmsCapsUrl WMS.Capabilities の URL
	 * @param extLayerId 外部地図レイヤID
	 * @param layerIds LAYERSの指定
	 * @return
	 */
	public List<JSONLayerInfo> createWMSJSONLayerInfos(ExternalmapdataInfo info, List<ExternalmapdataInfo> childs, String wmsCapsUrl, String extLayerId, String[] layerIds) {
		try {
			// WMS サーバに GetCapabilities を発行し、レイヤ情報を取得する
			Vector<LayerInfo> layers = new Vector<LayerInfo>();
			layers = WMSUtil.getWMSCapsLayerInfo(wmsCapsUrl, null, "xxx", extLayerId, layerIds, info.authorizationInfo);
			if(layers.size()==0) {
				throw new ServiceException(lang.__("Unable to get layer info from Capabilities: {0} in external map.", wmsCapsUrl));
			}
			// 指定したレイヤIDが追加されていなければ、追加する
			if(layerIds!=null) {
				for(String layerId : layerIds) {
					boolean exist = false;
					for(LayerInfo layerInfo : layers) {
						if(layerId.equals(layerInfo.featuretypeId)) {
							exist = true;
							break;
						}
					}
					if(exist==false) {
						logger.warn(lang.__("There is layer in external map that could not add from Capabilities.")+layerId);
						String childId = extLayerId+"_"+(layers.size()-1);
						LayerInfo layerInfo = LayerInfo.createReferenceLayerInfo(childId, layerId, null, LayerInfo.STATUS_DEFAULT, LAYER_TYPE_EXTERNALMAP_WMS_LAYERS, 0, 0, "", layerId);
						layers.add(layerInfo);
					}
				}
			}
			return buildJSONLayerInfos(info, layers);
		} catch(Exception e) {
			throw new ServiceException(lang.__("Unable to create {0} JSONLayerInfo for WMS.", "ExternalmapdataInfo.id="+info.id), e);
		}
	}

	public List<JSONLayerInfo> createArcGISJSONLayerInfos(ExternalmapdataInfo info, List<ExternalmapdataInfo> childs, String wmsCapsUrl, String extLayerId, String[] layerIds) {
		try {
			// WMS サーバに GetCapabilities を発行し、レイヤ情報を取得する
			Vector<LayerInfo> layers = new Vector<LayerInfo>();
			// wmsurlにlayertypeパラメータがあればArcGIS用
			layers = ArcGISUtil.getArcGISLayerInfo(wmsCapsUrl, extLayerId, info.name);
			if(layers.size()==0) {
				throw new ServiceException(lang.__("Unable to get layer info from Capabilities: {0} in external map.", wmsCapsUrl));
			}
			return buildJSONLayerInfos(info, layers);
		} catch(Exception e) {
			throw new ServiceException(lang.__("Unable to create {0} JSONLayerInfo for WMS.", "ExternalmapdataInfo.id="+info.id), e);
		}
	}

	public List<JSONLayerInfo> buildJSONLayerInfos(ExternalmapdataInfo info,Vector<LayerInfo> layers) throws SQLException {
		// WMS.GetMap の LAYERS の情報をJSONに変換する
		String parentLayerId = null;
		List<JSONLayerInfo> jsonLayerList = new ArrayList<JSONLayerInfo>();
		boolean first = true;
		for (LayerInfo layerInfo : layers) {

			// MapLayerInfo 作成
			MapLayerInfo mapLayerInfo = new MapLayerInfo();
			// 17/07/24 依頼により透過度未指定の場合に半透明から非透過に変更
			float floatopacity = info.layeropacity != null ? info.layeropacity.floatValue() : 1.0f;/*非透過*/ //0.5f;/*半透明*/
			mapLayerInfo.setValues(layerInfo.layerId, "", layerInfo.type, parentLayerId, "", info.visible, info.closed, floatopacity, 1, 1, "", 0, "", "", "");
			// レイヤ情報を JSON 形式に変換し、リストに追加する
			JSONLayerInfo jsonLayerInfo = new JSONLayerInfo(mapLayerInfo, layerInfo);
			if(!first) {
				jsonLayerInfo.exLayerProperties.put("metadataid", info.metadataid);
			}
			jsonLayerInfo.exLayerProperties.put("searchable", info.searchable);
			//jsonLayerInfo.exLayerProperties.put("searchable", info.searchable!=null ? info.searchable : false);
			if(info.authorizationinfoid!=null && info.authorizationinfoid > 0){
				jsonLayerInfo.exLayerProperties.put("wmsproxy", info.id);
				// 別スレッドで認証リクエストだけ実行する
				if(!info.authorizationInfo.username.equals("") && !info.authorizationInfo.userpass.equals("")){
					String authData = info.authorizationInfo.username + ":" + info.authorizationInfo.userpass;
					String authorization = info.authorizationInfo.authtype.equals("Basic") ?  "Basic " + Base64Util.encode(authData.getBytes()) : info.authorizationInfo.authword;

					session.setAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + info.metadataid, authorization);
				}
			}else{
				jsonLayerInfo.exLayerProperties.put("wmsproxy", "0");
			}
			jsonLayerList.add(jsonLayerInfo);

			// １回めのループで親レイヤのＩＤを記録する
			if (first) {
				parentLayerId = layerInfo.layerId;
				first = false;
			}
		}
		return jsonLayerList;
	}

	public JSONLayerInfo createXYZJSONLayerInfo(ExternalmapdataInfo info, String extLayerId, String xyzUrl, JSONObject option) {
		try {
			// XYZ LayerInfo
			LayerInfo layerInfo = LayerInfo.createReferenceLayerInfo(extLayerId, info.name, null, LayerInfo.STATUS_DEFAULT, LAYER_TYPE_EXTERNALMAP_XYZ, 0, 0, "", null);
			layerInfo.wmsURL = xyzUrl;
			if(option!=null) {
				// 最大ズームレベル（これを超えたらデジタルズーム）
				String key = "maxZoomLevel";
				if(option.has(key)) {
					layerInfo.maxZoomLevel = option.getInt(key);
				}
			}

			// MapLayerInfo 作成
			MapLayerInfo mapLayerInfo = new MapLayerInfo();
			// 17/07/24 依頼により透過度未指定の場合に半透明から非透過に変更
			float floatopacity = info.layeropacity != null ? info.layeropacity.floatValue() : 1.0f;/*非透過*/ //0.5f;/*半透明*/
			mapLayerInfo.setValues(layerInfo.layerId, "", layerInfo.type, null, "", info.visible, info.closed, floatopacity, 1, 1, "", 0, "", "", "");

			// レイヤ情報を JSON 形式に変換し、リストに追加する
			JSONLayerInfo jsonLayerInfo = new JSONLayerInfo(mapLayerInfo, layerInfo);
			jsonLayerInfo.exLayerProperties.put("metadataid", info.metadataid);
			// TODO: searchable
			//jsonLayerInfo.exLayerProperties.put("searchable", info.searchable!=null ? info.searchable : false);
			return jsonLayerInfo;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Unable to create {0} JSONLayerInfo for WMS.", "ExternalmapdataInfo.id="+info.id), e);
		}
	}

	/**
	 * セッションに保存された、メニューに表示すべき外部地図のExternalmapdataInfoリストを取得する
	 * 日付の新しい削除レコードが入っているものは除かれる.
	 * @param menuid メニューID
	 * @param isTraining 訓練フラグ
	 * @return 外部地図データ情報リスト
	 */
	public List<ExternalmapdataInfo> getSessionExternalmapdataInfos(Long menuid, boolean isTraining) {
		try {
			Map<String, ExternalmapdataInfo> map = new HashMap<String, ExternalmapdataInfo>();
			final Map<String, Timestamp> userTimestampMap = new HashMap<>();
			net.sf.json.JSONObject sessionMetadatas = isTraining ? loginDataDto.getTrainingsessionMetadatas() : loginDataDto.getSessionMetadatas();
			// 削除したメタデータIDを取得する
			Map<String, Timestamp> removed = new HashMap<String, Timestamp>();
			if(menuid!=null) {
				String key = String.valueOf(menuid);
				if(sessionMetadatas.has(key)) {
					net.sf.json.JSONObject records = sessionMetadatas.getJSONObject(key);
					for(Object obj : records.keySet()) {
						String name = (String) obj;
						net.sf.json.JSONObject record = records.getJSONObject(name);
						if(record.isEmpty()) continue;
						ExternalmapdataInfo externalmapdataInfo = ExternalmapdataInfo.valueOf(record);
						String state = record.getString("state");
						if("remove".equals(state)) {
							String updateTime = record.getString("updateTime");
							updateTime = updateTime.replaceAll("T", " ");
							//Timestamp timestamp = Timestamp.valueOf(updateTime);
							//removed.put(externalmapdataInfo.metadataid, timestamp);
							Timestamp userUpdateTime = new Timestamp(TimeUtil.parseISO8601(record.getString("userUpdateTime")).getTime());
							removed.put(externalmapdataInfo.metadataid, userUpdateTime);
						}

					}
				}
			}
			// 追加したレコードを取得
			long id = 1; // セッション内のIDを付ける
			List<String> keys = new ArrayList<String>();
			keys.add("all"); // すべての地図に追加したレコードを取得
			if(menuid!=null) keys.add(String.valueOf(menuid)); // この地図に追加したレコードを取得
			for(String key : keys) {
				if(sessionMetadatas.has(key)) {
					net.sf.json.JSONObject records = sessionMetadatas.getJSONObject(key);
					for(Object obj : records.keySet()) {
						String name = (String) obj;
						net.sf.json.JSONObject record = records.getJSONObject(name);
						if(record.isEmpty()) continue;
						String state = record.getString("state");
						if("remove".equals(state)) continue;
						//Date date = Date.valueOf(record.getString("updateTime"));
						//String updateTime = record.getString("updateTime");
						//updateTime = updateTime.replaceAll("T", " ");
						//Timestamp date = Timestamp.valueOf(updateTime);
						ExternalmapdataInfo externalmapdataInfo = ExternalmapdataInfo.valueOf(record);
						Timestamp userUpdateTime = new Timestamp(TimeUtil.parseISO8601(record.getString("userUpdateTime")).getTime());
						userTimestampMap.put(externalmapdataInfo.metadataid, userUpdateTime);
						// 削除された日付が新しければ追加しない
						Timestamp removedDate = removed.get(externalmapdataInfo.metadataid);
						if(removedDate!=null && (removedDate.after(userUpdateTime) || removedDate.equals(userUpdateTime))) {
							continue;
						}
						externalmapdataInfo.id = id++;
						// 複数リソース対応で name=metadataid+ckanresourceid
						//map.put(externalmapdataInfo.metadataid, externalmapdataInfo);
						map.put(name, externalmapdataInfo);
					}
				}
			}
			List<ExternalmapdataInfo> list = new ArrayList<ExternalmapdataInfo>();
			list.addAll(map.values());

			// ユーザ更新日順でソート
			Collections.sort(list, new Comparator<ExternalmapdataInfo>() {
				@Override
				public int compare(ExternalmapdataInfo o1, ExternalmapdataInfo o2) {
					Timestamp t1 = userTimestampMap.get(o1.metadataid);
					Timestamp t2 = userTimestampMap.get(o2.metadataid);
					return t1.before(t2) ? -1 : t1.after(t2) ? 1 : 0;
				}
			});
			return list;
		} catch (net.sf.json.JSONException e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<ExternalmapdataInfo>();
	}

	/**
	 * WMS の Capabilities より WFS の情報を類推する
	 * @param jsonMetadata メタデータのJSONObject
	 * @param localgovcode 自治体コード
	 * @param exTemp 時間に関する情報
	 * @return { getFeatureURL, featureName, featureTitle };
	 */
	public String[] getMapLayers(JSONObject jsonMetadata, String localgovcode, String exTemp) {
		String wmsGetmapUrl = null;
		String featureName = null;
		String featureTitle = null;
		Vector<LayerInfo> layers;

		try {

			// WMS の URL を取得する
			String wmsCapsUrl = jsonMetadata.get("wmsurl").toString();

			// WMS サーバに GetCapabilities を発行し、レイヤ情報を取得する
			layers = WMSUtils.getWMSCapsLayerInfo(wmsCapsUrl, null, "xxx", "extmap_tmp");
		} catch (Exception e) {
			return null;
		}

		for (LayerInfo layerInfo : layers) {

			// １回めのループで GetMap の URL を記録する
			if (wmsGetmapUrl == null) {
				wmsGetmapUrl = layerInfo.wmsURL;
			}

			// ２回めのループで featuretypeId, name を記録する
			else if (featureName == null) {
				featureName = layerInfo.featuretypeId;
				featureTitle = layerInfo.name;
				break;
			}
		}
		if (featureName == null)
			return null;

		// WMS の  GetMap の URL より　WFS の GetFeature の URL を作成する
		String getFeatureURL = wmsGetmapUrl.replaceAll("WMS",  "WFS").replaceAll("wms", "wfs");

		// WMS の リクエストパラメータを削除
		getFeatureURL = getFeatureURL.replaceFirst("\\?", "&").replaceAll("\\&$", "");
		getFeatureURL = getFeatureURL.replaceAll("(?i)\\&REQUEST=[^\\&]+", "");
		getFeatureURL = getFeatureURL.replaceAll("(?i)\\&VERSION=[^\\&]+", "");
		getFeatureURL = getFeatureURL.replaceAll("(?i)\\&REQUEST=[^\\&]+", "");
		getFeatureURL = getFeatureURL.replaceFirst("&", "?");

		String[] ret = { getFeatureURL, featureName, featureTitle, localgovcode, exTemp };

		return ret;
	}

	/**
	 * 登録情報レイヤのWMS URLを取得する
	 * @param request URLで利用するドメイン、プロトコルなどを取得するためのリクエストオブジェクト
	 * @param mapId 地図ID
	 * @param layerId レイヤID
	 * @return WMS URL
	 */
	public String getWMSURL(HttpServletRequest request, Long mapId, String layerId) {
		//ecommap のURL を取得する
		//ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
		String ecomimapUrl = Config.getEcommapURL(request); //bundle.getString("ECOMIMAPURL");
		String wms = ecomimapUrl + "map/wms?SERVICE=wms&REQUEST=getCapabilities&mid=" + mapId;
		// 危機管理クラウド用のレイヤ特定パラメータ layers を指定する.
		// 地図に対して GetCapabilities をするので公開されているレイヤすべてが得られてしまうため.
		// (layers は標準のWMSのGetCapabilitiesでは使用されない.)
		if(StringUtil.isNotEmpty(layerId)) wms += "&layers="+layerId;
//		String wms = ecomimapUrl + "wms?SERVICE=wms&mid=" + mapId;

		return wms;
	}

	/**
	 * 登録情報レイヤのWFS URLを取得する
	 * @param request URLで利用するドメイン、プロトコルなどを取得するためのリクエストオブジェクト
	 * @param cid サイトID
	 * @param mapId 地図ID
	 * @param layerId レイヤID
	 * @return WFS URL
	 */
	public String getWFSURL(HttpServletRequest request, int cid, Long mapId, String layerId) {
		//ecommap のURL を取得する
		//ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
		//ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
		String ecomimapUrl = Config.getEcommapURL(request); //bundle.getString("ECOMIMAPURL");
		//String wfs = ecomimapUrl + "map/wfsProxy?service=wfs&version=1.1.0&request=GetFeature";
		String wfs = ecomimapUrl + "map/wfsProxy?service=wfs&version=1.1.0&request=GetCapabilities&mid=" + mapId;
		wfs += "&cid="+cid;
		if (!layerId.equals("") && !layerId.equals("0"))
			wfs += "&typeName=map:" + layerId;

		return wfs;
	}

	/**
	 * 範囲をCapabilitiesから取得する.
	 * @param wms WMS URL
	 * @param layerId レイヤID
	 * @return {west, east, south, north};
	 */
	public String[] getHoriBndBox(String wms, String layerId) {
		// 外部URLの場合は内部URLに変換する（Basic認証をかけられてしまうことがあるので）
		String externalPrefix = Config.getEcommapURL()+"map";
		String internalPrefix = Config.getLocalRootURL()+"/map";
		if(wms.startsWith(externalPrefix)) wms = wms.replace(externalPrefix, internalPrefix);

		String[] horiBndBox = null;
		try {
			MapDB mapDB = MapDB.getMapDB();
			double[] bbox = mapDB.getFeatureDataExtent(layerId);
			double minX = bbox[2];
			double minY = bbox[1];
			double maxX = bbox[0];
			double maxY = bbox[3];
			CRSEnvelope envelope = new CRSEnvelope(null, minX, minY, maxX, maxY);
			//CRSEnvelope envelope = WMSUtil.getWMSBoundingBox(wms, layerId);
			if(envelope!=null) {
				String west = String.valueOf(envelope.getMinX() < envelope.getMaxX() ? envelope.getMinX() : envelope.getMaxX());
				String east = String.valueOf(envelope.getMinX() > envelope.getMaxX() ? envelope.getMinX() : envelope.getMaxX());
				String south = String.valueOf(envelope.getMinY() < envelope.getMaxY() ? envelope.getMinY() : envelope.getMaxY());
				String north = String.valueOf(envelope.getMinY() > envelope.getMaxY() ? envelope.getMinY() : envelope.getMaxY());
				horiBndBox = new String[]{west, east, south, north};
				logger.debug("HoriBndBox: "+Arrays.toString(horiBndBox));
			}
			

		} catch (Exception e) {
			logger.error(lang.__("Failed to get the extent from WMS Capabilities."),e);
		}
		return horiBndBox;
	}

	/**
	 * 履歴レイヤの時間範囲に関する情報を取得する.
	 *
	 * GeoServer 同様、time_from の min/max を期間計算に用いるが、
	 * min max が同じ場合は end を∞とする仕様とする。
	 *
	 * パターンとしては 4 つ考えられる.
	 * ---------------------------------------------------
	 *    time_from [Min] / [Max] -> メタデータ期間
	 * ---------------------------------------------------
	 * 1: -infinity  / -infinity  -> -∞ ～ ∞
	 * 2: -infinity  / TIMESTAMP1 -> -∞ ～ TIMESTAMP1
	 * 3: TIMESTAMP1 / TIMESTAMP1 -> TIMESTAMP1 ～ ∞
	 * 4: TIMESTAMP1 / TIMESTAMP2 -> TIMESTAMP1 ～ TIMESTAMP2
	 *
	 * @param _layerId レイヤID
	 * @return new String[] {begin, end}; 時系列でない場合は begin,end ともに null となる。
	 */
	public String[] getExTemp(String _layerId) {
		// レイヤの time_from の min/max を取得
		Timestamp[] timefromMinMax = ExMapDB.getLayerTimefromMinMax(_layerId);

		String begin = null;
		Timestamp min = timefromMinMax[0];
		if(min!=null) {
			// -infinity ならそのまま設定
			if(ExMapDB.isNegativeInfinity(min)) {
				begin = ExMapDB.iso8601df.format(min);
			}
			// 訓練マップなど time_from に開始日がすべて設定されているならそれで設定
			else {
				begin = ExMapDB.iso8601df.format(min);
			}
		}

		String end = null;
		Timestamp max = timefromMinMax[1];
		if(max!=null) {
			// min/max が同じなら ∞ を設定する
			if(min!=null && min.getTime()==max.getTime()) {
				end = ExMapDB.iso8601df.format(ExMapDB.positiveInfinity);
			}
			// 最終変更履歴があればそれで設定
			else {
				end = ExMapDB.iso8601df.format(max);
			}
		}
		logger.debug(_layerId+ " exTemp begin: "+begin+" end: "+end);
		return new String[] {begin, end};
	}

	/**
	 * 凡例画像URLを取得します.
	 * @param request URLで利用するドメイン、プロトコルなどを取得するためのリクエストオブジェクト
	 * @param cid サイトID
	 * @param mapId 地図ID
	 * @param _layerId レイヤID
	 * @return 凡例画像URL
	 */
	public String getLegendUrl(HttpServletRequest request, int cid, Long mapId, String _layerId) {
		ResourceBundle pathBundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
		String legendUrl = Config.getEcommapURL(request).replaceAll("/$", "") // eコミマップURL を取得(最後のスラッシュは除く)
				+pathBundle.getString("LEGEND_URL")
				+"WIDTH=20"
				+"&HEIGHT=20"
				+"&cid="+cid
				+"&mid="+mapId
				+"&layer="+_layerId
				+"&SCALE=1000";
		logger.debug("Legend URL: "+legendUrl);
		return legendUrl;
	}

	/**
	 * マスタマップから災害用マップを作成するときのクリアリングハウスの処理
	 * @param trackmapinfoid 記録地図情報
	 */
	public void onCreateDisasterMap(Long trackmapinfoid) {
		try {
			TrackmapInfo trackmapInfo = trackmapInfoService.findById(trackmapinfoid);

			// クリアリングハウスに登録済みのメタデータ情報を取得
			BeanMap conditions = new BeanMap();
			conditions.put(ClearinghousemetadataInfoNames.localgovinfoid().toString(), loginDataDto.getLocalgovinfoid());
			List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByCondition(conditions);

			HttpServletRequest request = SpringContext.getRequest();
			UpdateMetadataRun r = new UpdateMetadataRun(request, loginDataDto.getLocalgovinfoid());
			// クリアリングハウスのメタデータを更新
			r.onCreateDisasterMap(clearinghousemetadataInfos, trackmapInfo);

			Thread t = new Thread(r);
			t.start();
			// try{
			// 	t.join();		// 終了を待機する場合
			// }catch(InterruptedException e){
			// }
		} catch(Exception e) {
			String message = lang.__("An error occurred during clearinghouse update process on disaster map / layer creation");
			logger.error(message, e);
		}
	}
	/**
	 * マスタマップから訓練用マップを作成するときのクリアリングハウスの処理
	 * @param trackmapinfoid 記録地図情報
	 */
	public void onCreateTrainingMap(Long trackmapinfoid) {
		try {
			TrackmapInfo trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackmapinfoid);

			// クリアリングハウスに登録済みのメタデータ情報を取得
			BeanMap conditions = new BeanMap();
			conditions.put(ClearinghousemetadataInfoNames.localgovinfoid().toString(), loginDataDto.getLocalgovinfoid());
			List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByCondition(conditions);

			HttpServletRequest request = SpringContext.getRequest();
			UpdateMetadataRun r = new UpdateMetadataRun(request, loginDataDto.getLocalgovinfoid());
			// クリアリングハウスのメタデータを更新
			r.onCreateTrainingMap(clearinghousemetadataInfos, trackmapInfo);

			Thread t = new Thread(r);
			t.start();
			// try{
			// 	t.join();		// 終了を待機する場合
			// }catch(InterruptedException e){
			// }
		} catch(Exception e) {
			String message = lang.__("An error occurred during clearinghouse update process on disaster map / layer creation");
			logger.error(message, e);
		}
	}

	/**
	 * 災害対応を終えて平常時に戻る際のクリアリングハウス更新処理
	 * @param isTraining 訓練モードフラグ
	 */
	public void onCompleteDisasterMap() {
		// クリアリングハウスのメタデータを更新
		BeanMap conditions = new BeanMap();
		conditions.put(ClearinghousemetadataInfoNames.localgovinfoid().toString(), loginDataDto.getLocalgovinfoid());
		List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByCondition(conditions);

		try {
			HttpServletRequest request = SpringContext.getRequest();
			UpdateMetadataRun r = new UpdateMetadataRun(request, loginDataDto.getLocalgovinfoid());
			r.onCompleteDisasterMap(clearinghousemetadataInfos);

			Thread t = new Thread(r);
			t.start();
			// try{
			// 	t.join();		// 終了を待機する場合
			// }catch(InterruptedException e){
			// }
		} catch(Exception e) {
			logger.error("updatemetadata failed.", e);
		}
	}
	/**
	 * 訓練を終えて平常時に戻る際のクリアリングハウス更新処理
	 */
	public void onCompleteTrainingMap() {
		// クリアリングハウスのメタデータを更新
		BeanMap conditions = new BeanMap();
		conditions.put(ClearinghousemetadataInfoNames.localgovinfoid().toString(), loginDataDto.getLocalgovinfoid());
		List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByCondition(conditions);

		try {
			HttpServletRequest request = SpringContext.getRequest();
			UpdateMetadataRun r = new UpdateMetadataRun(request, loginDataDto.getLocalgovinfoid());
			r.onCompleteTrainingMap(clearinghousemetadataInfos);

			Thread t = new Thread(r);
			t.start();
			// try{
			// 	t.join();		// 終了を待機する場合
			// }catch(InterruptedException e){
			// }
		} catch(Exception e) {
			logger.error("updatemetadata failed.", e);
		}
	}

	/**
	 * クリアリングハウスの定期的に検索するときに除くメタデータIDリストを作成する
	 * @param menuid メニューID
	 * @param excludeMapMetadata 地図画面に追加されたメタデータを除く(trueの場合menuidは必須)
	 * @param excludeTableMetadata リスト画面に追加されたメタデータを除く(trueの場合menuidは必須)
	 * @param isTraining 訓練モードフラグ
	 * @return 除外対象のメタデータIDのJSONObject<メタデータID, 更新日時>
	 */
	public JSONObject getExcludeMetadataIds(Long menuid, boolean excludeMapMetadata, boolean excludeTableMetadata, boolean isTraining) {

		// name: metadataid, value: updateTime
		JSONObject excludes = new JSONObject();

		// 除外対象のメタデータIDリスト
		List<String> excludeMetadataIds = new ArrayList<String>();

		// 自分の自治体が登録したメタデータIDを取得
		List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());
		for(ClearinghousemetadataInfo clearinghousemetadataInfo : clearinghousemetadataInfos) {
			String metadataid = clearinghousemetadataInfo.metadataid;
			if(StringUtil.isNotEmpty(metadataid)) {
				excludeMetadataIds.add(metadataid);
			}
		}

		// 地図画面に追加されたメタデータを除く
		if(menuid!=null && excludeMapMetadata) {
			List<ExternalmapdataInfo> externalmapdataInfos = externalmapdataInfoService.findByMenuid(menuid);
			for(ExternalmapdataInfo externalmapdataInfo : externalmapdataInfos) {
				String metadataid = externalmapdataInfo.metadataid;
				if(StringUtil.isNotEmpty(metadataid)) {
					excludeMetadataIds.add(metadataid);
				}
			}
		}

		// リスト画面に追加されたメタデータを除く
		if(menuid!=null && excludeTableMetadata) {
			List<ExternaltabledataInfo> externaltabledataInfos = externaltabledataInfoService.findByMenuInfoId(menuid);
			for(ExternaltabledataInfo externaltabledataInfo : externaltabledataInfos) {
				String metadataid = externaltabledataInfo.metadataid;
				if(StringUtil.isNotEmpty(metadataid)) {
					excludeMetadataIds.add(metadataid);
				}
			}
		}

		// 無期限に除くメタデータIDをJSONObjectに設定する
		try {
			for(String excludeMetadataId : excludeMetadataIds) {
				excludes.put(excludeMetadataId, true);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}

		// セッションに保存されたメタデータIDがあればそれも除く
		if(menuid!=null) {
			try {
				net.sf.json.JSONObject sessionMetadatas = isTraining ? loginDataDto.getTrainingsessionMetadatas() : loginDataDto.getSessionMetadatas();
				// セッションに保存された、メニューに表示する外部地図のメタデータIDを設定する
				List<ExternalmapdataInfo> sessionExternalmapdataInfos = getSessionExternalmapdataInfos(menuid, isTraining);
				for(ExternalmapdataInfo externalmapdataInfo : sessionExternalmapdataInfos) {
					excludes.put(externalmapdataInfo.metadataid, true);
				}
				// 削除レコードのメタデータIDがまだ設定されていなければ、
				// それは削除されたメタデータなので、更新日時付きで設定する
				if(sessionMetadatas.has(String.valueOf(menuid))) {
					net.sf.json.JSONObject records = sessionMetadatas.getJSONObject(String.valueOf(menuid));
					for(Object obj : records.keySet()) {
						String name = (String) obj;
						net.sf.json.JSONObject record = records.getJSONObject(name);
						if(record.isEmpty()) continue;
						String metadataid = record.getString("metadataid");
						if(excludes.has(metadataid)==false) {
							String state = record.getString("state");
							if("remove".equals(state)) {
								try {
									String updateTime = record.getString("updateTime");
									//updateTime = updateTime.replaceAll("T", " ");
									Timestamp date = new Timestamp(TimeUtil.parseISO8601(updateTime).getTime());
									//Timestamp date = Timestamp.valueOf(updateTime);
									excludes.put(metadataid, date);
								} catch(Exception e) {
									logger.error("SessionMetadata parse error: "+e.getMessage(), e);
								}
							}
						}
					}
				}

			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.debug("excludes: "+excludes);
		return excludes;
	}

	/**
	 * 現在の状態でクリアリングハウスを全更新する.
	 */
	public void updatemetadataAll() {

		// マスタマップ情報
		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if(mapmasterInfo==null) return;

		TrackDataService trackDataService = SpringContext.getApplicationContext().getBean(TrackDataService.class);

		// 本番用クリアリングハウス
		{
			String cswurl = Config.getString("CSWURL");
			if(StringUtil.isNotEmpty(cswurl)) {
				List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid());
				// 災害時の場合
				if(0<trackDatas.size()) {
					onCreateDisasterMap(trackDatas.get(0).trackmapinfoid);
				}
				// 平常時の場合
				else {
					onCompleteDisasterMap();
				}
			}
		}

		// 訓練用クリアリングハウス
		{
			String cswurl = Config.getString("CSWURL_TRAINING");
			if(StringUtil.isNotEmpty(cswurl)) {
				List<TrackData> trainingTrackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
				// 訓練時の場合
				if(0<trainingTrackDatas.size()) {
					onCreateTrainingMap(trainingTrackDatas.get(0).trackmapinfoid);
				}
				// 訓練終了中の場合
				else {
					onCompleteTrainingMap();
				}
			}
		}
	}

	/**
	 * レイヤIDを指定してクリアリングハウスのメタデータを更新します.
	 * クリアリングハウスに未登録の場合は更新されません。
	 * @param layerId レイヤID
	 * @param trackmapinfoid 記録地図情報ID
	 * @return 成功フラグ
	 */
	public Boolean updatemetadataByLayerId(String layerId, long trackmapinfoid) {
		if(StringUtil.isEmpty(layerId)) return false;

		TrackmapInfo trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackmapinfoid);
		TablemasterInfo tablemasterInfo = null;
		if(trackmapInfo!=null) {
			List<TracktableInfo> tracktableInfos = tracktableInfoService.findByLayerId(layerId);
			if(tracktableInfos.size()==0) return false;
			// コピーフラグ:falseの場合、複数結果が得られる。
			// どれも同じなので先頭１つ目を用いる
			tablemasterInfo = tracktableInfos.get(0).tablemasterInfo;
		}
		else {
			// レイヤIDからテーブルマスタ情報を取得
			tablemasterInfo = tablemasterInfoService.findByLayerId(layerId);
		}
		if(tablemasterInfo==null) return false;

		// クリアリングハウスに登録したメタデータ情報を取得する
		BeanMap conditions = new BeanMap();
		conditions.put(ClearinghousemetadataInfoNames.localgovinfoid().toString(), loginDataDto.getLocalgovinfoid());
		conditions.put(ClearinghousemetadataInfoNames.tablemasterinfoid().toString(), tablemasterInfo.id);
		List<ClearinghousemetadataInfo> clearinghousemetadataInfos = clearinghousemetadataInfoService.findByCondition(conditions);

		return updatemetadataByMetadataInfos(clearinghousemetadataInfos, trackmapInfo);
	}

	/**
	 * 記録地図情報を指定してメタデータ更新する。
	 * 訓練の平常更新はできない。
	 * @param clearinghousemetadataInfos 更新対象のメタデータ情報
	 * @param trackmapInfo nullなら平常時更新、指定した場合は自動判定で災害/訓練更新
	 * @return 更新スレッド起動成功フラグ
	 */
	public Boolean updatemetadataByMetadataInfos(List<ClearinghousemetadataInfo> clearinghousemetadataInfos, TrackmapInfo trackmapInfo) {
		try {
			HttpServletRequest request = SpringContext.getRequest();
			UpdateMetadataRun r = new UpdateMetadataRun(request, loginDataDto.getLocalgovinfoid());
			r.updateByTrackmapInfo(clearinghousemetadataInfos, trackmapInfo);

			Thread t = new Thread(r);
			t.start();
			// try{
			// 	t.join();		// 終了を待機する場合
			// }catch(InterruptedException e){
			// }
			return true;
		} catch(Exception e) {
			logger.error("updatemetadata failed.", e);
		}
		return false;
	}

	/**
	 * メタデータ更新処理を別スレッドで実行する.
	 */
	private class UpdateMetadataRun implements Runnable {

		HttpServletRequest request;
		Long localgovinfoid;

		/** 訓練モードフラグ */
		boolean isTraining = false;

		List<ClearinghousemetadataInfo> clearinghousemetadataInfos;
		TrackmapInfo trackmapInfo;
		boolean getRecordFromDefaultCSW = false;

		/**
		 * メタデータの更新
		 */
		public UpdateMetadataRun(HttpServletRequest request, Long localgovinfoid){
			this.request = request;
			this.localgovinfoid = localgovinfoid;
			//this.isTraining = isTraining;
		}

		public void onCreateDisasterMap(List<ClearinghousemetadataInfo> clearinghousemetadataInfos, TrackmapInfo trackmapInfo) {
			this.clearinghousemetadataInfos = clearinghousemetadataInfos;
			this.trackmapInfo = trackmapInfo;
			this.getRecordFromDefaultCSW = false;
		}

		public void onCreateTrainingMap(List<ClearinghousemetadataInfo> clearinghousemetadataInfos, TrackmapInfo trackmapInfo) {
			this.clearinghousemetadataInfos = clearinghousemetadataInfos;
			this.trackmapInfo = trackmapInfo;
			this.isTraining = true;
			this.getRecordFromDefaultCSW = true; // 訓練マップを作成した場合は、本番用クリアリングハウスからコピーする
		}

		public void onCompleteDisasterMap(List<ClearinghousemetadataInfo> clearinghousemetadataInfos) {
			this.clearinghousemetadataInfos = clearinghousemetadataInfos;
			this.trackmapInfo = null; // 平常時に戻るので指定しない
			this.isTraining = false;
			this.getRecordFromDefaultCSW = false;
		}

		public void onCompleteTrainingMap(List<ClearinghousemetadataInfo> clearinghousemetadataInfos) {
			this.clearinghousemetadataInfos = clearinghousemetadataInfos;
			this.trackmapInfo = null; // 平常時に戻るので指定しない
			this.isTraining = true;
			this.getRecordFromDefaultCSW = false;
		}

		public void updateByTrackmapInfo(List<ClearinghousemetadataInfo> clearinghousemetadataInfos, TrackmapInfo trackmapInfo) {
			this.clearinghousemetadataInfos = clearinghousemetadataInfos;
			this.trackmapInfo = trackmapInfo;
			this.getRecordFromDefaultCSW = false;
		}

		public  void run(){
			boolean ret = this.updatemetadata();
			logger.debug("UpdateMetadataRun ret : "+ret);
		}

		/**
		 * @return
		 */
		private Boolean updatemetadata() {

			try {
				String cswurl = isTraining ? Config.getString("CSWURL_TRAINING") : Config.getString("CSWURL");
				String cswuser = isTraining ? Config.getString("CSWUSER_TRAINING") : Config.getString("CSWUSER");
				String cswpasswd = isTraining ? Config.getString("CSWPASSWD_TRAINING") : Config.getString("CSWPASSWD");

				// サイトID, 地図ID の取得
				int cid = 0;
				long mapId = 0;
				boolean isDisaster = false;
				{
					// 災害時
					if(trackmapInfo!=null) {
						cid = trackmapInfo.communityid;
						mapId =trackmapInfo.mapid;
						isDisaster = true;
						isTraining = trackmapInfo.trackDatas.get(0).isTraining();
					}
					// 平常時
					else {
						MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
						cid = mapmasterInfo.communityid;
						mapId = mapmasterInfo.mapid;
						isDisaster = false;
						//isTraining = false; // 平常時の場合は 引数で指定
					}

					// クリアリングハウスに登録があれば、インターネットに公開する
					if(0<clearinghousemetadataInfos.size()) {
						// 地図をインターネットに公開する
						MapGroupInfo.insert(cid, MapGroupInfo.TARGET_INTERNET, mapId, MapGroupInfo.TYPE_OPEN);
					}
				}

				// 各メタデータを更新
				ClearinghousemetadatadefaultInfo defaultInfo = null;
				{
					List<ClearinghousemetadatadefaultInfo> defaults = clearinghousemetadatadefaultInfoService.findByLocalgovinfoid(localgovinfoid);
					if(0<defaults.size()) defaultInfo = defaults.get(0);
					else {
						// デフォルト設定が設定されてない場合のデフォルト値を設定
						LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
						defaultInfo = new ClearinghousemetadatadefaultInfo();
						defaultInfo.adminareacode = localgovInfo.prefcode;
						defaultInfo.citycode = localgovInfo.citycode;
					}
				}
				for(ClearinghousemetadataInfo clearinghousemetadataInfo : clearinghousemetadataInfos) {
					String metadataid = clearinghousemetadataInfo.metadataid;

					// レイヤの取得
					Long tableid = clearinghousemetadataInfo.tablemasterinfoid;
					TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(tableid);
					if (tablemasterInfo != null) {
						// レイヤIDの取得
						String layerId = tablemasterInfo.layerid;
						if(isDisaster) {
							TracktableInfo tracktableInfo = tracktableInfoService.findByTrackmapInfoIdAndTablemasterinfoid(trackmapInfo.id, tablemasterInfo.id);
							layerId = tracktableInfo.layerid;
						}

						// レイヤをインターネットに公開する
						// ※本番用レイヤは管理画面でメタデータ登録時に公開されるが、
						// 　訓練用レイヤは公開にならないのでここで公開にする
						LayerGroupInfo.insert(cid, LayerGroupInfo.TARGET_INTERNET, layerId, LayerGroupInfo.SHARE_READ);

						// 状態: 完成
						String idStatus = isDisaster ? MetadataUtil.MD_ProgressCode.COMPLETED : MetadataUtil.MD_ProgressCode.EXPECTED;

						// WMS URL: mid, layers を災害用に変更
						String wmsUrl = getWMSURL(request, mapId, layerId);
						String[] onlineSrc_WMS = new String[]{wmsUrl, null};
						String wfsUrl = getWFSURL(request, cid, mapId, layerId);
						String[] onlineSrc_WFS = new String[]{wfsUrl, null};
						// 凡例画像URL
						String bgFileName = getLegendUrl(request, cid, mapId, layerId);
						// 範囲: Capabilities から再取得(事前に地図とレイヤを公開しておくこと)
						String[] horiBndBox = getHoriBndBox(wmsUrl, layerId); // 範囲
						// 時間要素: 履歴レイヤの begin/end
						String[] exTemp = getExTemp(layerId);

						//
						// set items
						//
						HashMap<String,Object> items = new HashMap<>();
						{
							if(idStatus != null)
								items.put("idStatus",idStatus);	// 状態
							if(bgFileName != null)
								items.put("bgFileName", bgFileName); // サムネイル画像
							if(horiBndBox != null)
								items.put("HoriBndBox",horiBndBox); // 地理境界ボックス
							if(exTemp != null)
								items.put("exTemp", exTemp);
							if(onlineSrc_WMS != null)
								items.put("onlineSrc_WMS",onlineSrc_WMS); // WMS URL
							if(onlineSrc_WFS != null)
								items.put("onlineSrc_WFS",onlineSrc_WFS); // WFS URL
						}

						//
						// set itemsIfEmpty
						//
						HashMap<String,Object> itemsIfEmpty = new HashMap<>();
						{
							// 問い合わせ先
							for(String prefix : new String[]{/*メタデータ作成者*/"mdContact", /*データ作成者*/"Ident"}) {
								// 識別情報問い合わせ先（組織名）（contact/organisationName）
								itemsIfEmpty.put(prefix+"_rpOrgName", defaultInfo.reference);
								// 識別情報問い合わせ先（電話番号）（contact/contactInfo/phone/voice）
								itemsIfEmpty.put(prefix+"_cntPhone", new String[]{defaultInfo.telno});
								// 問い合わせ先（住所詳細）（contact/contactInfo/address/deliveryPoint）
								itemsIfEmpty.put(prefix+"_delPoint", new String[]{defaultInfo.deliverypoint});
								// 問い合わせ先（市区町村）（contact/contactInfo/address/city）
								itemsIfEmpty.put(prefix+"_city", defaultInfo.city);
								// 問い合わせ先（市区町村コード）（contact/contactInfo/address/cityCode）
								itemsIfEmpty.put(prefix+"_cityCode", defaultInfo.citycode);
								// 問い合わせ先（都道府県名）（contact/contactInfo/address/administrativeArea）
								itemsIfEmpty.put(prefix+"_adminArea", defaultInfo.adminarea);
								// 問い合わせ先（都道府県コード）（contact/contactInfo/address/administrativeAreaCode）
								itemsIfEmpty.put(prefix+"_adminAreaCode", defaultInfo.adminareacode);
								// 問い合わせ先（郵便番号）（contact/contactInfo/address/postalCode）
								itemsIfEmpty.put(prefix+"_postCode", defaultInfo.postcode);
								// 問い合わせ先（電子メール）（contact/contactInfo/address/electronicMailAddress）
								itemsIfEmpty.put(prefix+"_eMailAdd", new String[]{defaultInfo.email});
								// 問い合わせ先（リンク）（contact/contactInfo/onlineResource/linkage）
								itemsIfEmpty.put(prefix+"_linkage", defaultInfo.linkage);
							}
						}

						//boolean ret = metadataService.updateMetadata(getRecordFromDefaultCSW, cswurl,cswuser,cswpasswd, metadataid,items);
						{
							boolean ret = false;
							// メタデータを取得
							String xml = getRecordFromDefaultCSW ? CSWUtil.getRecordById(metadataid) :  CSWUtil.getRecordById(cswurl,cswuser,cswpasswd, metadataid);
							if(xml != null) {
								// XMLの更新
								String now = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
								items.put("mdDateSt",now); // dateStamp
								items.put("refDateRev",now); // 改訂日

								// 問い合わせ先がなければデフォルトを設定
								{
									Element elm = MetadataUtil.parseMetadata(xml);

									// もし、メタデータが無くなっていたら再登録する
									if("ExceptionReport".equals(elm.getName())) {
										items.put("mdFileID"		, clearinghousemetadataInfo.metadataid);						//No2
										items.put("resTitle"		, clearinghousemetadataInfo.name);							//No28
										items.put("useLimit"		, new String[]{"001"});			//No64
										xml = CSWUtil.constructMetadata(items);
										elm = MetadataUtil.parseMetadata(xml);
									}
									
									boolean mdContactIsExists = MetadataUtil.getContact(elm)!=null;
									boolean IdentIsExists = MetadataUtil.getPointOfContact(MetadataUtil.CI_RoleCode.POINT_OF_CONTACT, elm)!=null;

									// 問い合わせ先
									for(String prefix : new String[]{/*メタデータ作成者*/"mdContact", /*データ作成者*/"Ident"}) {
										if(prefix.equals("mdContact") && mdContactIsExists) continue;
										if(prefix.equals("Ident") && IdentIsExists) continue;

										// 識別情報問い合わせ先（組織名）（contact/organisationName）
										itemsIfEmpty.put(prefix+"_rpOrgName", defaultInfo.reference);
										// 識別情報問い合わせ先（電話番号）（contact/contactInfo/phone/voice）
										itemsIfEmpty.put(prefix+"_cntPhone", new String[]{defaultInfo.telno});
										// 問い合わせ先（住所詳細）（contact/contactInfo/address/deliveryPoint）
										itemsIfEmpty.put(prefix+"_delPoint", new String[]{defaultInfo.deliverypoint});
										// 問い合わせ先（市区町村）（contact/contactInfo/address/city）
										itemsIfEmpty.put(prefix+"_city", defaultInfo.city);
										// 問い合わせ先（市区町村コード）（contact/contactInfo/address/cityCode）
										itemsIfEmpty.put(prefix+"_cityCode", defaultInfo.citycode);
										// 問い合わせ先（都道府県名）（contact/contactInfo/address/administrativeArea）
										itemsIfEmpty.put(prefix+"_adminArea", defaultInfo.adminarea);
										// 問い合わせ先（都道府県コード）（contact/contactInfo/address/administrativeAreaCode）
										itemsIfEmpty.put(prefix+"_adminAreaCode", defaultInfo.adminareacode);
										// 問い合わせ先（郵便番号）（contact/contactInfo/address/postalCode）
										itemsIfEmpty.put(prefix+"_postCode", defaultInfo.postcode);
										// 問い合わせ先（電子メール）（contact/contactInfo/address/electronicMailAddress）
										itemsIfEmpty.put(prefix+"_eMailAdd", new String[]{defaultInfo.email});
										// 問い合わせ先（リンク）（contact/contactInfo/onlineResource/linkage）
										itemsIfEmpty.put(prefix+"_linkage", defaultInfo.linkage);

										xml = CSWUtil.replaceMetadataIfEmpty(xml,itemsIfEmpty);
									}
								}

								xml = CSWUtil.replaceMetadata(xml,items);
								if(xml!=null) {
									// アップロード
									ret = CSWUtil.uploadMetadata(cswurl,cswuser,cswpasswd, xml,null,null);
								}
							}
							logger.info("Update Metadata "+metadataid+" "+(ret?"success":"failed"));
						}
					}
				}
				return true;
			} catch(Exception e) {
				logger.error("UpdateMetadataRun failed: "+e.getMessage(), e);
			}
			return false;
		}
	}

}
