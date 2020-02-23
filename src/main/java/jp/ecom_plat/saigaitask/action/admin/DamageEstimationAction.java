/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import jp.ecom_plat.saigaitask.form.admin.DamageEstimationForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.LandmarkDataService;
import jp.ecom_plat.saigaitask.service.db.LandmarkInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;

/**
 * 目標物データのアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/damageEstimation")
public class DamageEstimationAction extends AbstractAdminAction<LandmarkData> {
	protected DamageEstimationForm damageEstimationForm;

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
		model.put("damageEstimationForm", damageEstimationForm);
	}

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/damageEstimation/index";
	}

	/**
	 * xlsxファイル一括登録処理
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value={"","/index"}, params="upload")
	@ResponseBody
	public String xlsximport(Map<String, Object>model, @ModelAttribute DamageEstimationForm damageEstimationForm) throws ServiceException {
		this.damageEstimationForm = damageEstimationForm;

		Map<String, String> histories = new LinkedHashMap<String, String>();
		histories.put("201806191800", "c181");
		histories.put("201806200600", "c182");
		histories.put("201806201800", "c183");
		histories.put("201806210600", "c184");
		histories.put("201806211800", "c185");
		histories.put("201806220600", "c186");

		String sheetName = "GCPAdmDamageInf";
		int count = 0;

		// 町字単位
		// 負傷者
		Map<String, Map<String, String>> injured = new HashMap<String, Map<String, String>>();
		// 死亡者
		Map<String, Map<String, String>> dead = new HashMap<String, Map<String, String>>();
		// 行方不明者
		Map<String, Map<String, String>> missing = new HashMap<String, Map<String, String>>();
		// 建物全壊
		Map<String, Map<String, String>> complete = new HashMap<String, Map<String, String>>();
		// 建物半壊
		Map<String, Map<String, String>> half = new HashMap<String, Map<String, String>>();

		// 市町村単位
		// 負傷者
		Map<String, Map<String, String>> cityinjured = new HashMap<String, Map<String, String>>();
		// 死亡者
		Map<String, Map<String, String>> citydead = new HashMap<String, Map<String, String>>();
		// 行方不明者
		Map<String, Map<String, String>> citymissing = new HashMap<String, Map<String, String>>();
		// 建物全壊
		Map<String, Map<String, String>> citycomplete = new HashMap<String, Map<String, String>>();
		// 建物半壊
		Map<String, Map<String, String>> cityhalf = new HashMap<String, Map<String, String>>();

		try {
			InputStream is = damageEstimationForm.uploadFile.getInputStream();
			Workbook workbook = WorkbookFactory.create(is);
			Sheet sheet = workbook.getSheet(sheetName);
			Iterator<Row> rows = sheet.rowIterator();
			// 上4行は項目名のため読み飛ばす
			rows.next();
			rows.next();
			rows.next();
			rows.next();
			while(rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell(1);
				// そのまま文字として取得すると、指数表記で取得されてしまうので一度BigDecimalで取得する
				// BigDecimal型の市町村コード
				if(cell != null) {
					BigDecimal bigdecimalCode = BigDecimal.valueOf(cell.getNumericCellValue());
					// toPlainStringで指数フィールドを取り払う
					String code = String.valueOf(bigdecimalCode.toPlainString());
					if(cell != null) {
						injured.put(code, new HashMap<String, String>());
						dead.put(code, new HashMap<String, String>());
						missing.put(code, new HashMap<String, String>());
						complete.put(code, new HashMap<String, String>());
						half.put(code, new HashMap<String, String>());
						int index = 0;
						for(String key : histories.keySet()) {
							// 負傷者
							(injured.get(code)).put(key, String.format("{0:0.###}", row.getCell(3+index).getNumericCellValue()));
							// 死亡者
							(dead.get(code)).put(key, String.format("{0:0.###}", row.getCell(10+index).getNumericCellValue()));
							// 行方不明者
							(missing.get(code)).put(key, String.format("{0:0.###}", row.getCell(17+index).getNumericCellValue()));
							// 建物全壊
							(complete.get(code)).put(key, String.format("{0:0.###}", row.getCell(24+index).getNumericCellValue()));
							//　建物半壊
							(half.get(code)).put(key, String.format("{0:0.###}", row.getCell(31+index).getNumericCellValue()));
							index++;
						}
					}
				}
			}

			// 町字単位から市町村単位に
			for(Map.Entry<String, Map<String, String>> element : injured.entrySet()) {
				Map<String, String> injuredmap = element.getValue();
				Map<String, String> deadmap = dead.get(element.getKey());
				Map<String, String> missingmap = missing.get(element.getKey());
				Map<String, String> completemap = complete.get(element.getKey());
				Map<String, String> halfmap = half.get(element.getKey());
				String code = element.getKey().substring(0, 5);
				for(String value : element.getValue().keySet()) {
					BigDecimal totalinjured = null;
					BigDecimal totaldead = null;
					BigDecimal totalmissing = null;
					BigDecimal totalcomplete = null;
					BigDecimal totalhalf = null;
					if(cityinjured.containsKey(code) && cityinjured.get(code).containsKey(value)) {
						totalinjured = new BigDecimal(cityinjured.get(code).get(value)).add(new BigDecimal(injuredmap.get(value)));
						totaldead = new BigDecimal(citydead.get(code).get(value)).add(new BigDecimal(deadmap.get(value)));
						totalmissing = new BigDecimal(citymissing.get(code).get(value)).add(new BigDecimal(missingmap.get(value)));
						totalcomplete = new BigDecimal(citycomplete.get(code).get(value)).add(new BigDecimal(completemap.get(value)));
						totalhalf = new BigDecimal(cityhalf.get(code).get(value)).add(new BigDecimal(halfmap.get(value)));
					} else {
						if(!cityinjured.containsKey(code)) {
							cityinjured.put(code, new HashMap<String, String>());
							citydead.put(code, new HashMap<String, String>());
							citymissing.put(code, new HashMap<String, String>());
							citycomplete.put(code, new HashMap<String, String>());
							cityhalf.put(code, new HashMap<String, String>());
						}
						totalinjured = new BigDecimal(injuredmap.get(value));
						totaldead = new BigDecimal(deadmap.get(value));
						totalmissing = new BigDecimal(missingmap.get(value));
						totalcomplete = new BigDecimal(completemap.get(value));
						totalhalf = new BigDecimal(halfmap.get(value));
					}
					(cityinjured.get(code)).put(value, totalinjured.toPlainString());
					(citydead.get(code)).put(value, totaldead.toPlainString());
					(citymissing.get(code)).put(value, totalmissing.toPlainString());
					(citycomplete.get(code)).put(value, totalcomplete.toPlainString());
					(cityhalf.get(code)).put(value, totalhalf.toPlainString());
				}
			}

			for(Map.Entry<String, Map<String, String>> element : cityinjured.entrySet()) {
				Map<String, String> injuredmap = element.getValue();
				Map<String, String> deadmap = citydead.get(element.getKey());
				Map<String, String> missingmap = citymissing.get(element.getKey());
				Map<String, String> completemap = citycomplete.get(element.getKey());
				Map<String, String> halfmap = cityhalf.get(element.getKey());
				BigDecimal totalinjured = null;
				BigDecimal totaldead = null;
				BigDecimal totalmissing = null;
				BigDecimal totalcomplete = null;
				BigDecimal totalhalf = null;
				for(String key : histories.keySet()) {
					totalinjured = new BigDecimal(injuredmap.get(key));
					totaldead = new BigDecimal(deadmap.get(key));
					totalmissing = new BigDecimal(missingmap.get(key));
					totalcomplete = new BigDecimal(completemap.get(key));
					totalhalf = new BigDecimal(halfmap.get(key));
					String layerId = histories.get(key);
					MapDB mapDB = MapDB.getMapDB();
					long mapid = 11;
					LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
					Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
					vecLayerInfo.add(layerInfo);
					double[] center = new double[2];
					FeatureResultList resultList = FeatureDB.searchFeatureBbox(session, mapid, vecLayerInfo, null, null, "attr5="+element.getKey(), 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, center, false, null);
					// ファイルから読み取った市町村コードとattr5が一致した場合、データを登録する
					if(resultList.total > 0) {
						UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());
						HashMap<String, String> attribute = new HashMap<String, String>();
						long featureId = resultList.getResult(0).featureId;
						attribute.put("attr6", totalinjured.toPlainString());
						attribute.put("attr7", totaldead.toPlainString());
						attribute.put("attr8", totalmissing.toPlainString());
						attribute.put("attr9", totalcomplete.toPlainString());
						attribute.put("attr10", totalhalf.toPlainString());
						// 飛び地や島により同じ自治体コードのデータが複数存在する場合有
						for(int j = 0; j < resultList.total;j++) {
							featureId = resultList.getResult(j).featureId;
							FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attribute, null);
							count++;
						}
					}
				}
			}
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "登録失敗しました。";
		}

		return count+"件登録しました。";

	}

}
