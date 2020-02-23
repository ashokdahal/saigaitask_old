/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.mob;

import java.util.Map;

import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.postgis.PGgeometry;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.page.ListAction;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.form.page.ListForm;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/mob")
public class MlistAction extends ListAction {

	public double x;

	public double y;

	public String geomType = "POINT";

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("x", x);
		model.put("y", y);
		model.put("geomType", geomType);
	}

	/**
	 * /mob/ のリクエストマッピング.
	 * 継承により ListAction.index のリクエストマッピングがあるため、
	 * これをオーバライドする必要がある。
	 * @return フォワード先 リストページ
	 */
	@Override
	@org.springframework.web.bind.annotation.RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult) {
		return "redirect:/mob/process";
	}

	/**
	 * 初期表示
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/mlist","/mlist/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {

    	long taskid = listForm.menutaskid;
    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

    	super.content(taskid, menuid);

    	setupModel(model);

    	return "/mob/mlist/index";
    }

    /**
     * 一覧表示
     */
	@org.springframework.web.bind.annotation.RequestMapping(value="/mlist/list/{menutaskid}/{menuid}")
	public String list(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;

    	long taskid = listForm.menutaskid;
    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

    	super.content(taskid, menuid);

    	setupModel(model);

    	return "/mob/mlist/index";
    }

    /**
     * 詳細表示
     * @return
     */
	@org.springframework.web.bind.annotation.RequestMapping(value="/mlist/detail/{menutaskid}/{menuid}/{id}")
	public String detail(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;

    	long id = Long.parseLong(listForm.id);
    	long taskid = listForm.menutaskid;
    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

    	super.content(taskid, menuid, id);

    	MapDB mapDB = MapDB.getMapDB();
    	LayerInfo layerInfo = mapDB.getLayerInfo(table);
    	geomType = layerInfo.getGeometryType();

    	setupModel(model);

    	return "/mob/mlist/detail";
    }

    /**
     * 新規追加
     * @return
     */
	@org.springframework.web.bind.annotation.RequestMapping(value="/mlist/create/{menutaskid}/{menuid}")
	public String create(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;

    	//long id = Long.parseLong(listForm.id);
    	long taskid = listForm.menutaskid;
    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);

    	super.content(taskid, menuid, -1);
    	deletable = false;

    	MapDB mapDB = MapDB.getMapDB();
    	LayerInfo layerInfo = mapDB.getLayerInfo(table);
    	geomType = layerInfo.getGeometryType();

    	setupModel(model);

    	return "/mob/mlist/detail";
    }

    /**
     * 新規追加
     * @return
     */
	@org.springframework.web.bind.annotation.RequestMapping(value="/mlist/map/{menutaskid}/{menuid}/{id}")
	public String map(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		this.listForm = listForm;

    	long id = Long.parseLong(listForm.id);
    	long taskid = listForm.menutaskid;
    	long menuid = listForm.menuid;

    	MenuInfo menuInfo = menuInfoService.findById(menuid);
    	pageDto.setMenuInfo(menuInfo);

    	super.content(taskid, menuid, id);

    	if (result != null) {
    		PGgeometry geom = (PGgeometry)result.get(0).get("theGeom");
    		x = geom.getGeometry().getFirstPoint().x;
    		y = geom.getGeometry().getFirstPoint().y;
    		System.out.println(geom);
    	}

    	setupModel(model);

    	return "/mob/mlist/map";
    }

	/**
	 * ファイルリストを返す。
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/mlist/filelist/{value}/{id}")
	@ResponseBody
	public String filelist(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {

		try{
			long fid = Long.parseLong(listForm.id);
			//写真情報
			JSONArray jary = MapDB.getMapDB().getFeatureFileList(listForm.value, fid);
			JSONObject jobj = new JSONObject();
			jobj.append("data", jary);

			// 出力の準備
			//response.setContentType("application/json");
			//response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( jobj.toString() );
			return jobj.toString();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
}
