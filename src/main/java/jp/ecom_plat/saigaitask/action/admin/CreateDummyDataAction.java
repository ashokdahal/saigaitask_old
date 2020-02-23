/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.util.GeometryUtils;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.constant.Observ;
import jp.ecom_plat.saigaitask.entity.db.JudgeInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgeformulaMaster;
import jp.ecom_plat.saigaitask.entity.db.JudgemanInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultData;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleData;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.form.admin.CreateDummyDataForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.JudgeInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgeformulaMasterService;
import jp.ecom_plat.saigaitask.service.db.JudgemanInfoService;
import jp.ecom_plat.saigaitask.service.db.JudgeresultDataService;
import jp.ecom_plat.saigaitask.service.db.JudgeresultstyleDataService;
import jp.ecom_plat.saigaitask.service.db.JudgeresultstyleInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryrainInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryriverInfoService;
import jp.ecom_plat.saigaitask.service.db.TelemeterDataService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.hishidama.eval.ExpRuleFactory;
import jp.hishidama.eval.Expression;
import jp.hishidama.eval.var.MapVariable;

/**
 * 意思決定支援デモデータ作成用プログラム
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/createDummyData")
public class CreateDummyDataAction extends AbstractAction {

	protected CreateDummyDataForm createDummyDataForm;

	@Resource
	protected JdbcManager jdbcManager;
	@Resource
	protected TableService tableService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected MapService mapService;

	@Resource
	protected ObservatoryrainInfoService observatoryrainInfoService;
	@Resource
	protected ObservatoryriverInfoService observatoryriverInfoService;
	@Resource
	protected TelemeterDataService telemeterDataService;
	@Resource
	protected JudgeresultstyleDataService judgeresultstyleDataService;
	@Resource
	protected JudgemanInfoService judgemanInfoService;
	@Resource
	protected JudgeresultDataService judgeresultDataService;
	@Resource
	protected JudgeInfoService judgeInfoService;
	@Resource
	protected JudgeformulaMasterService judgeformulaMasterService;
	@Resource
	protected JudgeresultstyleInfoService judgeresultstyleInfoService;

	@RequestMapping(value={"","/index"})
	public String index() {

		return "/admin/createDummyData/index";
	}

	@RequestMapping(value="/mesh")
	public String mesh(@ModelAttribute CreateDummyDataForm createDummyDataForm) {

        MapDB mapDB = MapDB.getMapDB();

        //参考ハザードマップレイヤID
        String hazardLayerIds = createDummyDataForm.hazardLayerIds;
        String hazardAttrId = createDummyDataForm.hazardAttrId;
        String[] hazardLayerId = hazardLayerIds.split(",");

        //入力メッシュレイヤID
        String meshLayerId = createDummyDataForm.meshLayerId;
        //出力先レイヤID
        String outLayerId = createDummyDataForm.outLayerId;
        String outAttrId = createDummyDataForm.outAttrId;
        //レベル引下げ数値
        int minus = createDummyDataForm.minus;

        //地図ID
        //int mapId = 73;
        //地図グループID
        //int mapgroupId = 15;
        //コミュニティID
        //int communityId = 1;

        String meshcols = "gid, ST_AsText(\"the_geom\")";
        //メッシュレイヤ取得
        LayerInfo meshLayer = mapDB.getLayerInfo(meshLayerId);
        Iterator<AttrInfo> it = meshLayer.getAttrIterable().iterator();
        while (it.hasNext()) {
        	AttrInfo attr = it.next();
        	meshcols += ","+attr.attrId;
        }
        List<BeanMap> inmeshlist = selectAll(meshLayerId, meshcols, "asc", "gid");

        List<List<BeanMap>> hazardlistList = new ArrayList<List<BeanMap>>();
        for (String hlayerId : hazardLayerId) {
            String hazardcols = "gid, ST_AsText(\"the_geom\")";
	        LayerInfo hazardLayer = mapDB.getLayerInfo(hlayerId);
	        it = hazardLayer.getAttrIterable().iterator();
	        while (it.hasNext()) {
	        	AttrInfo attr = it.next();
	        	hazardcols += ","+attr.attrId;
	        }
	        List<BeanMap> hazardlist = selectAll(hlayerId, hazardcols, "asc", "gid");
	        for (BeanMap hazard : hazardlist) {
	        	try {
	            	Geometry ghazard = GeometryUtils.getGeometryFromWKT((String)hazard.get("stAstext"));
	            	hazard.put("geometry", ghazard);
				} catch (ParseException e) {
					e.printStackTrace();
				}
	        }
	        hazardlistList.add(hazardlist);
        }

        //データの削除
        try {
			mapDB.deleteGeometryTable(outLayerId);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        for (BeanMap mesh : inmeshlist) {
        	try {
        		String wkt = (String)mesh.get("stAstext");
            	Geometry gmesh = GeometryUtils.getGeometryFromWKT(wkt);
            	int level = 0;
            	for (List<BeanMap> hazardlist : hazardlistList) {
	            	for (BeanMap hazard : hazardlist) {
	            		Geometry ghazard = (Geometry)hazard.get("geometry");

	            		if (gmesh.intersects(ghazard)) {
	            			String val = (String)hazard.get(hazardAttrId);
	            			int lvl = Integer.parseInt(val)-minus;
	            			if (lvl == 10) continue;
	            			if (level < lvl) level = lvl;
	            		}
	            	}
            	}
            	//メッシュ作成
            	if (level > 0) {
        			// 属性と値を Map に保存
        			HashMap<String, String> attributes = new HashMap<String, String>();
        			Iterator<String> kit = mesh.keySet().iterator();
        			while (kit.hasNext()) {
        				String key = kit.next();
        				if (key.indexOf("attr") == 0) {
        					attributes.put(key, (String)mesh.get(key));
        				}
        			}
        			attributes.put(outAttrId, ""+level);
            		long fid = mapService.insertFeature(loginDataDto.getEcomUser(), outLayerId, wkt, attributes);
            	}
			} catch (ParseException e) {
				e.printStackTrace();
			}

        }

		//Geometry geom = GeometryUtils.getGeometryFromWKT(wkt);

		return "index";
	}

	@RequestMapping(value="/telemeter")
	public String telemeter(@ModelAttribute CreateDummyDataForm createDummyDataForm) {

		int observtype = createDummyDataForm.observtype;
		long observpointinfoid = createDummyDataForm.observpointinfoid;

		long fullcode = 0l;
		if (observtype == Observ.RAIN) {
			ObservatoryrainInfo rain = observatoryrainInfoService.findById(observpointinfoid);
			fullcode = rain.getPointFullCode();
		}
		else if (observtype == Observ.RIVER) {
			ObservatoryriverInfo river = observatoryriverInfoService.findById(observpointinfoid);
			fullcode = river.getPointFullCode();
		}
		int ritemcode = 10;
		int ritemcodeh = 30;
		int ritemcode6h = 50;
		int ruikacode = 70;
		int witemcode = 10;
		String until = createDummyDataForm.untildate+","+createDummyDataForm.untiltime+",";

		//String file = "D:\\NIED\\戦略推進\\災害対応システム３\\data\\西三河\\okazaki_rain.dat";

		double rwarn1[] = new double[]{20d, 40d};
		double rwarn2[] = new double[]{70d, 140d};
		long styleid1[] = new long[]{2, 3};
		long styleid2[] = new long[]{4, 5};

		double wplus = Double.parseDouble(createDummyDataForm.wplus);

		//現在のデータ削除
		String sql = "delete from telemeter_data where code = "+fullcode;
		jdbcManager.updateBySql(sql).execute();

		//データ追加
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd,HH:mm,");
		//FileInputStream fis = null;
		try {
			//fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(createDummyDataForm.formFile.getInputStream()));
    		String line = null;
    		Date untiltime = sdf.parse(until);
    		int mukou6h = 0;
    		double ruika = 0d;
    		double add60 = 0d;
    		while ((line = br.readLine()) != null) {
    			Date datetime = sdf.parse(line);
    			if (datetime.getTime() > untiltime.getTime()) continue;

    			String[] vals = line.split(",");
    			double val = Double.parseDouble(vals[2].trim());
    			BigDecimal sum = new BigDecimal(vals[2].trim());
    			val = sum.add(new BigDecimal(createDummyDataForm.wplus)).doubleValue();

    			if (observtype == Observ.RAIN) {
	    			TelemeterData data = new TelemeterData();
	    			data.code = fullcode;
	    			data.observtime = new Timestamp(datetime.getTime());
	    			data.itemcode = ritemcodeh;
	    			data.contentscode = 0;
	    			data.val = val;
	    			telemeterDataService.insert(data);
	    			checkRisk();

	    			//累加雨量（6時間）
	    			if (val == 0d) mukou6h++;
	    			if (mukou6h >= 6) add60 = 0d;
	    			if (val > 0d) mukou6h = 0;
	    			add60 += val;

	    			data = new TelemeterData();
	    			data.code = fullcode;
	    			data.observtime = new Timestamp(datetime.getTime());
	    			data.itemcode = ritemcode6h;
	    			data.contentscode = 0;
	    			data.val = add60;
	    			telemeterDataService.insert(data);
	    			checkRisk();

	    			if (mukou6h >= 6) ruika = 0d;
	    			for (int i = 5; i >= 0; i--) {
	    				double val10 = val/6d;
	    				//10分雨量
	        			data = new TelemeterData();
	        			data.code = fullcode;
	        			data.observtime = new Timestamp(datetime.getTime() - i*10*60*1000l);
	        			data.itemcode = ritemcode;
	        			data.contentscode = 0;
	        			data.val = val10;
	        			telemeterDataService.insert(data);
	        			//checkRisk();

	        			//累加雨量
	        			ruika += val10;

	        			data = new TelemeterData();
	        			data.code = fullcode;
	        			data.observtime = new Timestamp(datetime.getTime() - i*10*60*1000l);
	        			data.itemcode = ruikacode;
	        			data.contentscode = 0;
	        			data.val = ruika;
	        			telemeterDataService.insert(data);
	        			//checkRisk();
	    			}
    			}
    			else if (observtype == Observ.RIVER) {
	    			TelemeterData data = new TelemeterData();
	    			data.code = fullcode;
	    			data.observtime = new Timestamp(datetime.getTime());
	    			data.itemcode = witemcode;
	    			data.contentscode = 0;
	    			data.val = val;
	    			telemeterDataService.insert(data);

	    			checkRisk();
    			}
    			//System.out.println(line);
    		}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		//

		return "index";
	}

	public void insertStyle(TelemeterData data, double[] vals, long[] infoids) {
		int index = -1;
		for (int i = 0; i < vals.length; i++) {
			if (data.val > vals[i])
				index = i;
		}
		if (index >= 0) {
			JudgeresultstyleData style = new JudgeresultstyleData();
			style.judageresultstyleinfoid = infoids[index];
			style.telemeterdataid = data.id;
			judgeresultstyleDataService.insert(style);
		}
	}

	/**
	 * データ検索
	 * @param tablename	テーブル名
	 * @param asc 昇順、降順
	 * @param col 項目
	 * @return
	 */
	public List<BeanMap> selectAll(String tablename, String cols, String asc, String col)
	{
		return jdbcManager.selectBySql(BeanMap.class, "select "+cols+" from "+tablename+" order by "+col+" "+asc).getResultList();
	}


	/**
	 * 判定実行
	 */
	protected void checkRisk() {

		List<JudgemanInfo> jmanlist = judgemanInfoService.findByValidOrderByJudgeOrder();

		//判定するリスク管理を検索
		for (JudgemanInfo jman : jmanlist) {

			//if (!canJudgeTime(jman)) continue;

			//すでに判定済みか確認
			JudgeresultData res = judgeresultDataService.findByJudgemanInfoId(jman.id);
			//if (res != null && res.bsent) continue;

			boolean bjudge = true;
			List<JudgeInfo> jlist = judgeInfoService.findByJudgemanInfoIdAndValid(jman.id);
			if (jlist.size() == 0) bjudge = false;
			String rvalue = "";
			List<TelemeterData> datalist = null;
			for (JudgeInfo judge : jlist) {

				datalist = telemeterDataService.getData(judge.telemeterdatacode, judge.itemcode, 1, /*タイムスライダー未対応*/null);
				if (datalist.size() != 1) {
					bjudge = false;
					continue;
				}
				TelemeterData data = datalist.get(0);
				if (data.val == null) {
					bjudge = false;
					continue;
				}

				Object oval = Double.parseDouble(judge.val);
				Object val = data.val;
				try {
					JudgeformulaMaster formula = judgeformulaMasterService.findById(judge.judgeformulaid);
					boolean ret = judgeRisk(Types.DOUBLE, formula.formula, oval, val);
					if (!ret)//基本的にAND判定
						bjudge = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (rvalue.isEmpty())
					rvalue = val.toString();
				else
					rvalue += ","+val.toString();
			}

			if (bjudge && (res == null || res.bcancel)) {//判定結果TRUE
				//結果の登録
				JudgeresultData result = new JudgeresultData();
				result.judgemaninfoid = jman.id;
				result.judgetime = new Timestamp(System.currentTimeMillis());
				result.val = rvalue;
				result.bcancel = false;
				judgeresultDataService.insert(result);
			}
			else if (!bjudge && res != null && !res.bcancel) {//判定結果FALSE

				//判定解除
				res.bcancel = true;
				res.canceltime = new Timestamp(System.currentTimeMillis());
				judgeresultDataService.update(res);
			}
			//リストの色設定
			if (bjudge) {
				JudgeresultstyleInfo style = judgeresultstyleInfoService.findByJudgemanInfoId(jman.id);
				if (style != null && datalist != null && datalist.size() > 0) {
					for (TelemeterData data : datalist) {
						if (judgeresultstyleDataService.findByTelemeterDataId(data.id) == null) {
							JudgeresultstyleData sdata = new JudgeresultstyleData();
							sdata.judageresultstyleinfoid = style.id;
							sdata.telemeterdataid = data.id;
							judgeresultstyleDataService.insert(sdata);
						}
					}
				}
			}
		}
	}

	/**
	 * 値の判定
	 * @param valueType SQLの型
	 * @param method 式
	 * @param oval 閾値
	 * @param obj 値
	 * @return 結果
	 * @throws IOException
	 */
	public static boolean judgeRisk(int valueType, String method, Object oval, Object obj) throws IOException
	{
		//値の判定
		Map<String, Object> varMap = new HashMap<String, Object>();
		if (method.indexOf("r1") >= 0)//判定値
			varMap.put("r1", oval);
		if (method.indexOf("v1") >= 0)//情報値
			varMap.put("v1", obj);
		//System.out.println("vi="+((Double)obj).doubleValue());
		return judgeRisk(valueType, method, varMap);
	}

	protected static boolean judgeRisk(int valueType, String method, Map<String, Object> varMap) throws IOException
	{
		boolean judge = false;

		Expression exp = ExpRuleFactory.getDefaultRule().parse(method);
		exp.setVariable(new MapVariable(varMap));
		switch (valueType) {
		case Types.VARCHAR:
			Object oret = exp.eval();
			if (oret instanceof Boolean) {
				if (((Boolean)oret).booleanValue())
					judge = true;
			}
			else
				throw new IOException("unsupported value or method type ");
			break;
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.TINYINT:
			long ret = exp.evalLong();
			if (ret == 1) //配列の一つでも有効であれば送信可
				judge = true;
			break;
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
			double dret = exp.evalDouble();
			if (dret == 1d) //配列の一つでも有効であれば送信可
				judge = true;
			break;
		default:
			throw new IOException("unsupported value or method type ");
		}

		return judge;
	}

	@Resource protected TrackDataService trackDataService;
	@Resource protected TrackmapInfoService trackmapInfoService;

	@RequestMapping(value="/deletemap")
	@ResponseBody
	public String deletemap() {
		MapDB mapDB = MapDB.getMapDB();

		List<LocalgovInfo> govlist = localgovInfoService.findAll();
		for (LocalgovInfo gov : govlist) {

			List<TrackData> trackdataList = trackDataService.findByLocalgovinfoid(gov.id, true);
			for (TrackData trackData : trackdataList) {
				if (!trackData.deleted) continue;
				TrackmapInfo tmapInfo = trackmapInfoService.findById(trackData.trackmapinfoid);

				//他の災害が終了してなければ、削除禁止
				List<TrackData> tdatalist = trackDataService.findByTrackmapinfoidAndNotDeleted(trackData.trackmapinfoid);
				if (tdatalist.size() > 0) continue;

				//地図の検索
				MapInfo emapInfo = mapDB.getMapInfo(tmapInfo.mapid);
				if (emapInfo.status != MapInfo.STATUS_DELETED) {
					trackDataService.deleteDisasterMap(trackData);
				}
			}
		}

		return null;
	}

	@RequestMapping(value="/deletelayer")
	@ResponseBody
	public String deletelayer() {
		MapDB mapDB = MapDB.getMapDB();

		try {
			List<BeanMap> maplist = tableService.selectAll("_map");
			for (BeanMap map : maplist) {
				Short mapstatus = (Short)map.get("mapStatus");
				if (mapstatus != -100) continue;

				Integer cid = (Integer)map.get("mapCommunityId");
				if (cid != 19) continue;

				long mapid = (Long)map.get("mapId");
				Vector<MapLayerInfo> maplayerlist = mapDB.getMapLayerInfo((int)mapid);
				for (MapLayerInfo maplayer : maplayerlist) {
					String layerid = maplayer.layerId;

					String sql = "select * from _maplayer where layer_id='"+layerid+"'";
					List<BeanMap> layerlist = jdbcManager.selectBySql(BeanMap.class, sql).getResultList();
					if (layerlist.size() == 1) {
						LayerInfo layerInfo = mapDB.getLayerInfo(layerid);
						if (layerInfo.type == LayerInfo.TYPE_LOCAL)
							mapDB.deleteLayerInfo(layerid);
					}
				}

			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return null;
	}
}
