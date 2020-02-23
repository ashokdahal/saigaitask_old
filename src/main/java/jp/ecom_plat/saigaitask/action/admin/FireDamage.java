/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.LandmarkData;
import jp.ecom_plat.saigaitask.form.admin.FireDamageForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.LandmarkDataService;
import jp.ecom_plat.saigaitask.service.db.LandmarkInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;

/**
 * 目標物データのアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/fireDamage")
public class FireDamage extends AbstractAdminAction<LandmarkData> {
	protected FireDamageForm fireDamageForm;

	/** サービスクラス */
	@Resource
	protected LandmarkDataService landmarkDataService;
	@Resource
	protected LandmarkInfoService landmarkInfoService;
	@Resource
	protected FileUploadService fileUploadService;
	@Resource
	protected TableService tableService;
	@Resource
	protected TrackmapInfoService trackmapInfoService;

	public void setupModel(Map<String,Object>model) {
		model.put("fireDamageForm", fireDamageForm);
	}

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/fireDamage/index";
	}

	/**
	 * xlsxファイル一括登録処理
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value={"","/index"}, params="upload")
	@ResponseBody
	public String xlsximport(Map<String, Object>model, @ModelAttribute FireDamageForm fireDamageForm) throws ServiceException {
		this.fireDamageForm = fireDamageForm;

		// 複数レイヤ、複数シートに対応。設定ファイルにカンマ区切りでレイヤIDとエクセルのシート名を入力。
		String fireLayerid = "c827,c828,c829,c830,c831,c832,c833,c834,c835,c836,c837,c838,c839";
		String[] layerIds = fireLayerid.split(",", -1);
		Map<String, String> histories = new HashMap<String, String>();
		histories.put("201801191030", layerIds[0]);
		histories.put("201801191200", layerIds[1]);
		histories.put("201801191500", layerIds[2]);
		histories.put("201801191800", layerIds[3]);
		histories.put("201801192100", layerIds[4]);
		histories.put("201801200000", layerIds[5]);
		histories.put("201801200200", layerIds[6]);
		histories.put("201801200500", layerIds[7]);
		histories.put("201801200700", layerIds[8]);
		histories.put("201801201000", layerIds[9]);
		histories.put("201801201200", layerIds[10]);
		histories.put("201801201600", layerIds[11]);
		histories.put("201801211200", layerIds[12]);
		int count = 0;

		try {
			InputStream is = fireDamageForm.uploadFile.getInputStream();
			Reader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			MapDB mapDB = MapDB.getMapDB();
			long mapid = 11;
			double[] center = new double[2];

			UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());
			HashMap<String, String> attribute = new HashMap<String, String>();
			// 出火棟数
			int fire = 0;
			// 全焼棟数
			int burn = 0;
			// 「日時:自治体コード」と「出火棟数の総数, 全焼棟数の総数」Map
			Map<String, Map<String, String>> fires = new HashMap<String, Map<String, String>>();
			// 「日時:自治体コード」と「全焼棟数の総数」Map
			Map<String, Map<String, String>> burns = new HashMap<String, Map<String, String>>();
			// 市町村単位の「日時:自治体コード」と「出火棟数の総数, 全焼棟数の総数」Map
			Map<String, Map<String, String>> cityfires = new HashMap<String, Map<String, String>>();
			// 市町村単位の「日時:自治体コード」と「全焼棟数の総数」Map
			Map<String, Map<String, String>> cityburns = new HashMap<String, Map<String, String>>();
			// 自治体コード
			String code = "";

			while((line = br.readLine()) != null) {
				String[] data = line.replaceAll(" ", "").split(",");
				if(data.length == 4) {
					code = data[1];
					// 時間&自治体と出火棟数のMAP
					if(!fires.containsKey(code)) {
						fires.put(code, new LinkedHashMap<String, String>());
						burns.put(code, new LinkedHashMap<String, String>());
					}
					(fires.get(code)).put(data[0], String.valueOf(data[2]));
					(burns.get(code)).put(data[0], String.valueOf(data[3]));
				}
			}

			for(Map.Entry<String, Map<String, String>> element : fires.entrySet()) {
				Map<String, String> firebuildings = element.getValue();
				Map<String, String> burnbuildings = burns.get(element.getKey());
				fire = 0;
				burn = 0;

				// 最新の出火、全焼棟数
				for(String key : firebuildings.keySet()) {
					if(fire < Integer.parseInt(firebuildings.get(key)))
						fire = Integer.parseInt(firebuildings.get(key));
					if(burn < Integer.parseInt(burnbuildings.get(key)))
						burn = Integer.parseInt(burnbuildings.get(key));
				}

				// ファイルから読み込めなかった時間の火災被害情報の登録
				// 例えば201801191520までしかデータが無かった場合、6/19 18:00, 6/19 21:00...のレイヤには最新のデータを登録する
				Iterator<String> it = histories.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					if(!firebuildings.containsKey(key)) {
						(fires.get(element.getKey())).put(key, String.valueOf(fire));
						(burns.get(element.getKey())).put(key, String.valueOf(burn));
					}
				}
			}

			// 町字単位から市町村単位に
			for(Map.Entry<String, Map<String, String>> element : fires.entrySet()) {
				Map<String, String> firebuildings = element.getValue();
				Map<String, String> burnbuildings = burns.get(element.getKey());
				code = element.getKey().substring(0, 5);

				for(String value : firebuildings.keySet()) {
					if(histories.containsKey(value)) {
						if(cityfires.containsKey(code) && cityfires.get(code).containsKey(value)) {
							fire = Integer.valueOf(cityfires.get(code).get(value)) + Integer.valueOf(firebuildings.get(value));
							burn = Integer.valueOf(cityburns.get(code).get(value)) + Integer.valueOf(burnbuildings.get(value));
						} else {
							if(!cityfires.containsKey(code)) {
								cityfires.put(code, new LinkedHashMap<String, String>());
								cityburns.put(code, new LinkedHashMap<String, String>());
							}
							fire = Integer.valueOf(firebuildings.get(value));
							burn = Integer.valueOf(burnbuildings.get(value));
						}
						(cityfires.get(code)).put(value, String.valueOf(fire));
						(cityburns.get(code)).put(value, String.valueOf(burn));
					}
				}
			}

			for(Map.Entry<String, Map<String, String>> element : cityfires.entrySet()) {
				Map<String, String> firebuildings = element.getValue();
				Map<String, String> burnbuildings = cityburns.get(element.getKey());

				for(String key : firebuildings.keySet()) {
					if(histories.containsKey(key)) {
						fire = Integer.parseInt(firebuildings.get(key));
						burn = Integer.parseInt(burnbuildings.get(key));
						String layerId = histories.get(key);
						LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
						Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
						vecLayerInfo.add(layerInfo);
						attribute.put("attr7", String.valueOf(fire));
						attribute.put("attr8", String.valueOf(burn));
						FeatureResultList resultList = FeatureDB.searchFeatureBbox(session, mapid, vecLayerInfo, null, null, "attr5="+element.getKey(), 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, center, false, null);
						for(int i = 0;i < resultList.total;i++) {
							if(!resultList.getResult(i).getAttrResult("attr6").getAttrValue().isEmpty()) {
								long featureId = resultList.getResult(i).featureId;
								// 建物棟数
								double building = Double.valueOf(resultList.getResult(i).getAttrResult("attr6").getAttrValue());
								// 火災の規模 = 出火棟数 / 建物棟数
								double scale = fire / building;
								attribute.put("attr9", String.valueOf(scale));
								// 出火中
								double burning = fire - burn;
								attribute.put("attr10", String.valueOf(burning));
								FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attribute, null);
								count++;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "登録失敗しました。";
		}
		return count+"件登録しました。";
	}
}


