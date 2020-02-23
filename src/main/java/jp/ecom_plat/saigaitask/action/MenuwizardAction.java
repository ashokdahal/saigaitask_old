/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.TimelineAction.TimelineMenue;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TimelinetableInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.HistorytableInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.StationlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecolumnMasterService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablerowstyleInfoService;
import jp.ecom_plat.saigaitask.service.db.TimelinetableInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.service.menuwizard.MenuwizardService;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * メニューウイザードを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class MenuwizardAction extends AbstractAction {

	@Resource
	protected MenuwizardService menuwizardService;


	/** 追加メニュー名称 */
	public String addmenuname;
	/** 削除メニューID */
	public String deletemenuid;
	/** メッセージ */
	public String message;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("addmenuname", addmenuname);
		model.put("deletemenuid", deletemenuid);
		model.put("message", message);
	}



	/**
	 * メニューウイザード画面を表示する.
	 * @return フォワード先
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/menuwizard", "/menuwizard/index"})
	public String index(Map<String,Object>model) throws ParseException {
		setupModel(model);
		return "/menuwizard/index";
	}

	/**
	 * メニューを追加する
	 * @return null(フォワードしない)
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/menuwizard/createmenu"})
	public ResponseEntity<String> createmenu(Map<String,Object>model, HttpServletResponse res) throws ParseException {

		String message;
    	JSONObject json = new JSONObject();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String addmenuname = (String)request.getParameter("addmenuname");
		boolean isSuccess = false;
		if(StringUtils.isEmpty(addmenuname)) {
			message = lang.__("名称が未入力です。");
		}else {
			
			isSuccess = menuwizardService.createTemplateMenu(loginDataDto.getTrackdataid(), addmenuname);
			message = lang.__("追加しました。");
			
		}

		try {
			setupModel(model);
			json.put("message", message);

			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * メニューを削除する
	 * @return null(フォワードしない)
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/menuwizard/deletemenu"})
	public String deletemenu(Map<String,Object>model, HttpServletResponse res) throws ParseException {


		return null;
	}
}
