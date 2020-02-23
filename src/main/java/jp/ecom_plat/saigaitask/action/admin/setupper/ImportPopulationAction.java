/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.seasar.framework.util.StringUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.form.admin.setupper.ImportPopulationForm;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ImportPopulationAction extends AbstractSetupperAction {

	/** アクションフォーム */
	protected ImportPopulationForm importPopulationForm;

	@Resource
	protected LayerService layerService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected MapService mapService;

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/importPopulation","/admin/setupper/importPopulation/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute ImportPopulationForm importPopulationForm, BindingResult bindingResult) {
		this.importPopulationForm = importPopulationForm;
		content(model, importPopulationForm, bindingResult);
		setupModel(model);
		return "/admin/setupper/importPopulation/index";
	}


	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/importPopulation/content")
	public String content(Map<String,Object>model,
			@Valid @ModelAttribute ImportPopulationForm importPopulationForm, BindingResult bindingResult){
		this.importPopulationForm = importPopulationForm;
		setupModel(model);
		return "/admin/setupper/importPopulation/content";
	}

	/**
	 * 保存処理
	 * @return リダイレクト先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/importPopulation/update")
	public String update(Map<String,Object>model,
			@Valid @ModelAttribute ImportPopulationForm importPopulationForm, BindingResult bindingResult) {
		this.importPopulationForm = importPopulationForm;
    	String euser = loginDataDto.getEcomUser();
    	MapmasterInfo mapmaster = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
    	String name = lang.__("Population partition");
    	String description = "";
    	String geomType = "MULTIPOLYGON";
    	String names[] = new String[10];
    	names[0] = lang.__("Primary Area Partition");
    	names[1] = lang.__("Secondary Area Partition");
    	names[2] = lang.__("Third Area Partition");
    	names[3] = lang.__("Divided Grid Square");
    	names[4] = lang.__("Serial number");
    	names[5] = lang.__("Area partition");
    	names[6] = lang.__("Total population");
    	names[7] = lang.__("Men");
    	names[8] = lang.__("Women");
    	names[9] = lang.__("Total households number");
    	Integer types[] = new Integer[10];
    	types[0] = AttrInfo.DATATYPE_TEXT;
    	types[1] = AttrInfo.DATATYPE_TEXT;
    	types[2] = AttrInfo.DATATYPE_TEXT;
    	types[3] = AttrInfo.DATATYPE_TEXT;
    	types[4] = AttrInfo.DATATYPE_INTEGER;
    	types[5] = AttrInfo.DATATYPE_TEXT;
    	types[6] = AttrInfo.DATATYPE_INTEGER;
    	types[7] = AttrInfo.DATATYPE_INTEGER;
    	types[8] = AttrInfo.DATATYPE_INTEGER;
    	types[9] = AttrInfo.DATATYPE_INTEGER;
    	String selects[] = new String[10];
    	Integer sizes[] = new Integer[10];
    	for (int i = 0; i < selects.length; i++) {
    		selects[i] = "";
    		sizes[i] = 10;
    	}

    	if (StringUtil.isEmpty(importPopulationForm.layerId)) {
    		importPopulationForm.layerId = layerService.create(euser, mapmaster.communityid, mapmaster.mapgroupid, mapmaster.mapid, name, description, geomType, null, names, types, selects, sizes);
    	//String layerid = "c12148";
    	}

		InputStream in = null;
		InputStream in2 = null;
		try {
			in = importPopulationForm.shapeFile.getInputStream();
			ShapefileDataStore datastore = importAreaShape(in);

			in2 = importPopulationForm.textFile.getInputStream();
			Reader reader = new InputStreamReader(in2, "MS932");
			BufferedReader csvread = new BufferedReader(reader);
			String line = null;
			Map<String, String> codes = new HashMap<String, String>();
			int idx = 0;
			while ((line = csvread.readLine()) != null) {
				if (idx++ < 2) continue;
				String[] data = line.split(",");
				codes.put(data[0], line);
			}
			in2.close();

			//フィーチャー一覧の取得
			Query query = new DefaultQuery();
			FeatureSource<SimpleFeatureType, SimpleFeature> source = datastore.getFeatureSource();
			FeatureCollection<SimpleFeatureType, SimpleFeature> collect = source.getFeatures(query);
			FeatureIterator<SimpleFeature> iterator = collect.features();

			//フィーチャーごとに属性の取得
			while (iterator.hasNext()){
				//Featureから属性の取得
				SimpleFeature feature = (SimpleFeature) iterator.next();
				List<Object> attlist = feature.getAttributes();
				

				HashMap<String, String> attributes = new HashMap<String, String>();
				String key = "";
				for (int i = 1; i < attlist.size(); i++) {
					Object obj = attlist.get(i);
					attributes.put("attr"+(i-1), obj.toString());
					// ここが固定値になっているので、一番右端にある事を期待するなら下記に直せばOK
					if (i == attlist.size()-1)
						key = obj.toString();
				}
				// KEY_CODE の列の並びはフォーマットによって先頭だったり最後尾だったりする。
				// KEY_CODE という列があれば、属性値を直接取得してキーとする。
				String keyCode = (String) feature.getAttribute("KEY_CODE");
				if(keyCode!=null) key = keyCode;

				line = codes.get(key);
				if (line == null) continue;
				String val[] = line.split(",");
				attributes.put("attr6", val[1]);
				attributes.put("attr7", val[2]);
				attributes.put("attr8", val[3]);
				attributes.put("attr9", val[4]);

				String wkt = attlist.get(0).toString();
				mapService.insertFeature(loginDataDto.getEcomUser(), importPopulationForm.layerId, wkt, attributes);
			}

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			e.printStackTrace();
		}

		setupModel(model);
		return "forward:index";
	}

	protected ShapefileDataStore importAreaShape(InputStream in) throws Exception {
		ZipArchiveInputStream zis;
		zis = new ZipArchiveInputStream(in, "Windows-31J", true);
		ArchiveEntry entry;
		boolean dbf = false;
		boolean shx = false;
		boolean shp = false;
		File parent = File.createTempFile("unzip_shapefile","");
		if(parent.exists()){parent.delete(); parent.mkdir();};

		//ファイルを一旦保存する
		File shapeFile = null;
		while( (entry = zis.getNextEntry()) != null ) {
			try {
				String fileName;
				fileName = entry.getName().replaceAll(".+/", "").replaceAll("\\.+", ".");
				File file = new File(parent, fileName);
				if(!entry.isDirectory()) {
					try {
						//ファイルに保存
						FileOutputStream fos = new FileOutputStream(file);
						IOUtils.copy(zis, fos);
						fos.close();
						if (fileName.toLowerCase().matches("(.*\\.dbf)$")){
							dbf = true;
						}
						if (fileName.toLowerCase().matches("(.*\\.shx)$")) {
							shx = true;
						}
						if (fileName.toLowerCase().matches("(.*\\.shp)$")){
							shp = true;
							shapeFile = file;
						}
					} finally { }//if (file.exists()) file.delete(); }
				} else {
					if(!file.exists()) file.mkdir();
				}
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		zis.close();

		if(!dbf && !shx && !shp && !shapeFile.exists()){return null;}
		//ファイル読み込み
		ShapefileDataStore datastore = new ShapefileDataStore(shapeFile.toURI().toURL());
		//文字コード
		Charset charst;
		charst= Charset.forName("UTF-8");
		datastore.setStringCharset(charst);

		return datastore;
	}

}
