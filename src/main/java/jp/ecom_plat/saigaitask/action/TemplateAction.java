/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import static jp.ecom_plat.saigaitask.entity.Names.noticeTemplate;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_NEW_ENTITY;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER_ADD;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER_DEL;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER_EDIT;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_PAGE;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_RECORDS;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_ROWS;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_TOTAL;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_USERDATA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticetemplatetypeMaster;
import jp.ecom_plat.saigaitask.form.TemplateForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticetemplatetypeMasterService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 定型文表示のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class TemplateAction extends AbstractAction {

	protected TemplateForm templateForm;

	@Resource
	protected NoticetemplatetypeMasterService noticetemplatetypeMasterService;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	/** 通知テンプレート */
	public Map<Integer, List<NoticeTemplate>> noticeTemplateMap;

	@Resource
	protected NoticedefaultInfoService noticedefaultInfoService;

	@Resource
	protected ResponseService responseService;

	@Resource
	protected MenuInfoService menuInfoService;
	/** 通知デフォルト情報フラグ */
	public boolean noticeDefaultFound = false;
	/** 追加ボタン無効化フラグ */
	public boolean disableAddButton = true;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("noticeTemplateMap", noticeTemplateMap);
		model.put("noticeDefaultFound", noticeDefaultFound);
		model.put("disableAddButton", disableAddButton);
		model.put("templateForm", templateForm);
	}

    /**
     * 定型文表示
     * @return 次ページ
     */
	@org.springframework.web.bind.annotation.RequestMapping("/template/{menuinfoid}/{noticetypeid}")
    public String index(Map<String,Object>model, @Valid @ModelAttribute TemplateForm templateForm) {
		this.templateForm = templateForm;
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}


    	//通知デフォルト情報が登録されているか調べる
    	if (StringUtil.isNotEmpty(templateForm.menuinfoid)) {
	    	NoticedefaultInfo definfo = noticedefaultInfoService.findByMenuInfoId(Long.valueOf(templateForm.menuinfoid));
    		noticeDefaultFound = definfo != null;
    		disableAddButton = definfo == null || definfo.noticetemplatetypeid == null || StringUtil.isEmpty(definfo.templateclass);
    	}

    	setupModel(model);

    	return "/template/index";
    }

    /**
     * 選択完了処理（未使用？）
     * @return 次ページ
     */
	@org.springframework.web.bind.annotation.RequestMapping("/template/success")
	public String success() {

		return "/template/success";
	}

	/**
	 * 通知テンプレート一覧情報を取得してJSON形式で返却（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/template/jqgridindex", produces="application/json", method = RequestMethod.POST)
	public ResponseEntity<String> jqgridindex(Map<String,Object>model,
			@ModelAttribute TemplateForm templateForm, BindingResult bindingResult) throws ServiceException {

    	final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

    	long menuid = 0;
    	int templatetypeid = 0;
    	String templateclass = null;
    	if (StringUtil.isNotEmpty(templateForm.menuinfoid)) {
    		menuid = Long.parseLong(templateForm.menuinfoid);
    		//数値デフォルト情報を取得
    		NoticedefaultInfo definfo = noticedefaultInfoService.findByMenuInfoId(menuid);
    		if (definfo != null && definfo.noticetemplatetypeid != null) {
    			templatetypeid = definfo.noticetemplatetypeid;
    			templateclass = definfo.templateclass;
    		}
    	}

    	//通知テンプレート種別マスタを取得
    	List<NoticetemplatetypeMaster> noticetemplatetypeItems = noticetemplatetypeMasterService.findAllOrderByDisporder();

    	noticeTemplateMap = new HashMap<Integer, List<NoticeTemplate>>();
    	if (StringUtil.isEmpty(templateForm.noticetypeid)) templateForm.noticetypeid = "0";
    	List<NoticeTemplate> tlist = noticeTemplateService.findByLoaclgovInfoIdNoticetypeId(loginDataDto.getLocalgovinfoid(), Integer.parseInt(templateForm.noticetypeid));
    	for (NoticeTemplate temp : tlist) {
    		if (temp.noticetemplatetypeid == null) continue;
    		if (templatetypeid != 0 && templatetypeid != temp.noticetemplatetypeid) continue;
    		if (StringUtil.isNotEmpty(templateclass) && (StringUtil.isEmpty(temp.templateclass) || !templateclass.equals(temp.templateclass))) continue;
    		List<NoticeTemplate> list = noticeTemplateMap.get(temp.noticetemplatetypeid);
    		if (list == null) {
    			list = new ArrayList<NoticeTemplate>();
    			noticeTemplateMap.put(temp.noticetemplatetypeid, list);
    		}
       	    list.add(temp);
    	}

		try{

			//パラメータ取得
			long templateId;
			try {
				templateId = Long.parseLong(templateForm.templateid);
			} catch (Exception e) {
				templateId = 0;
			}

			JSONObject json = new JSONObject();
			JSONArray result = new JSONArray();

			for (NoticetemplatetypeMaster type : noticetemplatetypeItems) {
				List<NoticeTemplate> tempList = noticeTemplateMap.get(type.id);
				if (tempList == null)
					continue;
				int rowspan = tempList.size();
				for (NoticeTemplate template : tempList) {
					JSONObject row = new JSONObject();
					row.put("noticetypeid", type.name);
					row.put("templateclass", template.templateclass);
					String checked = templateId == template.id ? " checked" : "";
					row.put("tempid", "<input id=\"templateid_" + (result.size() + 1) + "\" type=\"radio\" value=\"" + template.id + "\" name=\"templateid\"" + checked + " />");
					row.put("title", template.title);
					row.put("content", template.content);
					row.put("rowspan", rowspan);
					rowspan = 0;
					result.add(row);
				}
			}

			JSONObject userData = new JSONObject();
			userData.put("noticetemplatetypeid", templatetypeid);
			userData.put("templateclass", templateclass);

			json.put(JQG_PAGER_PAGE, 1); //現在ページ番号
			json.put(JQG_PAGER_RECORDS, result.size()); //総結果件数
			json.put(JQG_PAGER_TOTAL, 1);  //総ページ数
			json.put(JQG_USERDATA, userData); //ユーザーデータ
			json.put(JQG_PAGER_ROWS, result);   //結果一覧

			//レスポンスへ出力
			//responseService.responseJson(json);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);

		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}
		//return null;
	}


	/**
	 * 通知デフォルト情報を取得してJSON形式で返却（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/template/jqgriddefindex", produces="application/json", method = RequestMethod.POST)
	public ResponseEntity<String> jqgriddefindex(Map<String,Object>model,
			@ModelAttribute TemplateForm templateForm, BindingResult bindingResult) throws ServiceException {

    	final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		BeanMap conditions = new BeanMap();
		conditions.put(noticeTemplate().localgovinfoid().toString(), loginDataDto.getLocalgovinfoid());
		List<NoticeTemplate> list = noticeTemplateService.findTypeAndClass(loginDataDto.getLocalgovinfoid());

		try{
			JSONObject json = new JSONObject();
			JSONArray result = new JSONArray();

			for (NoticeTemplate template : list) {
				JSONObject row = new JSONObject();
				JSONObject val = new JSONObject();
				//ラジオボタンの value に、テンプレート種別IDと区分を JSON 文字列で格納する
				val.put("noticetemplatetypeid", template.noticetemplatetypeid);
				val.put("templateclass", template.templateclass);
				row.put("tempid", "<input id=\"templateid_" + (result.size() + 1) + "\" type=\"radio\" value=\""
						+ val.toString().replaceAll("\"", "&#34")
						+ "\" name=\"templateid\" />");
				row.put("noticetypeid", template.note);				//テンプレート種別名称（note を流用）
				row.put("templateclass", template.templateclass);	//区分
				result.add(row);
			}

			json.put(JQG_PAGER_PAGE, 1); //現在ページ番号
			json.put(JQG_PAGER_RECORDS, result.size()); //総結果件数
			json.put(JQG_PAGER_TOTAL, 1);  //総ページ数
			json.put(JQG_USERDATA, null); //ユーザーデータ
			json.put(JQG_PAGER_ROWS, result);   //結果一覧

			//レスポンスへ出力
			//responseService.responseJson(json);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);

		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}
		//return null;
	}

	/**
	 * 通知テンプレート編集、追加、削除実行（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/template/jqgridedit", produces="application/json", method = RequestMethod.POST)
	public ResponseEntity<String> jqgridedit(Map<String,Object>model,
			@ModelAttribute TemplateForm templateForm, BindingResult bindingResult) throws ServiceException {

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtil.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			NoticeTemplate entity;
			//追加
			if (JQG_OPER_ADD.equals(oper)) {
				//コピー
				if (!templateForm.templateid.isEmpty()) {
					entity = noticeTemplateService.findById(Long.parseLong(templateForm.templateid));
				}
				//新規
				else {
			    	MenuInfo menuInfo = menuInfoService.findById(new Long(templateForm.menuinfoid));
					entity = new NoticeTemplate();
					entity.localgovinfoid = loginDataDto.getLocalgovinfoid();
					entity.noticetypeid = new Integer(templateForm.noticetypeid);
					entity.noticetemplatetypeid = new Integer(templateForm.noticetemplatetypeid);
					entity.templateclass = templateForm.templateclass;
					entity.title = menuInfo.name;	//title の初期値として menu_info の name を設定
					entity.content = "";
					entity.note = "";
					entity.disporder = noticeTemplateService.getLargestDisporder(new BeanMap()) + 1;
				}
				//登録実行
				noticeTemplateService.insert(entity);
				//採番したキー値を返却
				JSONObject json = new JSONObject();
				json.put(JQG_NEW_ENTITY, jp.ecom_plat.saigaitask.util.StringUtil.jsonForCyclicalReferences(entity));
				//responseService.responseJson(json);
				return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
			}
			//削除
			else if (JQG_OPER_DEL.equals(oper)) {
				entity = new NoticeTemplate();
				entity.id = Long.parseLong(templateForm.templateid);
				//削除実行
				noticeTemplateService.delete(entity);
				JSONObject json = new JSONObject();
				//responseService.responseJson(json);
				return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
			}
			//変更
			else if (JQG_OPER_EDIT.equals(oper)) {
				//テンプレートデータ取得
				entity = noticeTemplateService.findById(Long.parseLong(templateForm.templateid));
				if ("title".equals(templateForm.name))
					entity.title = templateForm.value;
				else if ("content".equals(templateForm.name))
					entity.content = templateForm.value;
				else
					// MessageResourcesUtil.getMessage("error.insert", "") は使えないので、メッセージを適当なものに変更
					throw new IllegalStateException(lang.__("An error occurred."));
				//登録実行
				noticeTemplateService.update(entity);
				//テンプレートＩＤを返却
				JSONObject json = new JSONObject();
				json.put(JQG_NEW_ENTITY, jp.ecom_plat.saigaitask.util.StringUtil.jsonForCyclicalReferences(entity));
				//responseService.responseJson(json);
				return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
			}
			return null;
		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
		//return null;
	}


	/**
	 * 通知デフォルト情報追加（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/template/jqgrideditdef", produces="application/json", method = RequestMethod.POST)
	public ResponseEntity<String> jqgrideditdef(Map<String,Object>model,
			@ModelAttribute TemplateForm templateForm, BindingResult bindingResult) throws ServiceException {

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

    	try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtil.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//追加
			if (JQG_OPER_ADD.equals(oper)) {
				NoticedefaultInfo defaultInfo = new NoticedefaultInfo();
				defaultInfo.menuinfoid =  Long.valueOf(templateForm.menuinfoid);
				defaultInfo.noticetemplatetypeid = Integer.valueOf(templateForm.noticetemplatetypeid);
				defaultInfo.templateclass = templateForm.templateclass;
				noticedefaultInfoService.insert(defaultInfo);
				JSONObject json = new JSONObject();
				//responseService.responseJson(json);
				return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
			}
		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
		return null;
	}

	/**
	 * 定型文の本文を挿入
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/template/content/{templateid}")
	public ResponseEntity<String> template(Map<String,Object>model, @Valid @ModelAttribute TemplateForm templateForm, BindingResult result) {
		//http://localhost:8080/SaigaiTask/page/facebook/template/8
    	final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Long tid = 0L;
		try {
			tid = Long.parseLong(templateForm.templateid);
			logger.error(lang.__("Text tempid : ") + templateForm.templateid);
			logger.error(lang.__("Text tid:") + tid);
		} catch (NumberFormatException e) {
		}
		NoticeTemplate temp = noticeTemplateService.findById(tid);
		if (temp != null) {
			String content = noticeTemplateService.replaceTag(temp.content);
			//ResponseUtil.write(content);
			return new ResponseEntity<String>(content, httpHeaders, HttpStatus.OK);
		}
		return null;
	}

	/**
	 * 定型文のタイトルを挿入
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/template/title/{templateid}")
	public ResponseEntity<String> templatetitle(Map<String,Object>model, @Valid @ModelAttribute TemplateForm templateForm, BindingResult result) {
    	final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Long tid = 0L;
		try {
			tid = Long.parseLong(templateForm.templateid);
			logger.error(lang.__("Text tempid : ") + templateForm.templateid);
			logger.error(lang.__("Text tid:") + tid);
		} catch (NumberFormatException e) {
		}
		NoticeTemplate temp = noticeTemplateService.findById(tid);
		if (temp != null) {
			String content = noticeTemplateService.replaceTag(temp.title);
			//ResponseUtil.write(content);
			return new ResponseEntity<String>(content, httpHeaders, HttpStatus.OK);
		}
		return null;
	}
}
