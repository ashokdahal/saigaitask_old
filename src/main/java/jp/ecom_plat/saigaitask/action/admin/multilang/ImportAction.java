/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.multilang;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.form.admin.multilang.ImportExportForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangmesInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.service.multilang.ImportService;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping(value="/admin/multilang/import")
public class ImportAction extends AbstractAction{

	public static final String IMPORTTYPE_NEW = "1";
	public static final String IMPORTTYPE_RENEW = "2";
	public static final String IMPORTTYPE_UPDATE = "3";

	public String totalcount;
	public String updatecount;
	public String insertcount;

	protected ImportExportForm importExportForm;

	/** サービスクラス */
	@Resource
	protected MultilangInfoService multilangInfoService;
	@Resource
	protected MultilangmesInfoService multilangmesInfoService;
	@Resource
	protected ResponseService responseService;
	@Resource
	protected FileUploadService fileUploadService;
	@Resource
	protected ImportService importService;

	private File uploadLangDataFile;

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("totalcount", totalcount);
		model.put("updatecount", updatecount);
		model.put("insertcount", insertcount);
	}

	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object> model, @ModelAttribute ImportExportForm importExportForm) {
		// 初期化
		initAction();

		// 表示内容の取得
		content(model);

		return "/admin/multilang/import/index";
	}

	/**
	 * ページ表示内容作成
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/content")
	public String content(Map<String,Object> model) {

		setupModel(model);

		return "/admin/multilang/import/content";
	}

	/**
	 * インポート方式に応じて言語コードリストを作成しなおす
	 * @return
	 */
	@RequestMapping(value="/reloadlangCodeSelect", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String reloadlangCodeSelect(){

		String importType = (String)request.getParameter("selectImportType");

		List<String> langCodeStrList = new ArrayList<String>();
		List<BeanMap> newLangCodeList = new ArrayList<BeanMap>();

		if(importType != null){
			if(importType.equals(ImportAction.IMPORTTYPE_NEW)){
				langCodeStrList = SaigaiTaskLangUtils.getJqgridLangCodes(request);
				for(String langCode : langCodeStrList){
					BeanMap data = new BeanMap();
					data.put("code", langCode);
					data.put("name", "test");
					newLangCodeList.add(data);
				}
			}else{
				List<MultilangInfo> multilangInfos = multilangInfoService.findAll(new OrderByItem("id", OrderingSpec.ASC));
				langCodeStrList = new ArrayList<String>();
				for(MultilangInfo multilangInfo : multilangInfos){
					BeanMap data = new BeanMap();
					data.put("code", multilangInfo.code);
					data.put("name", multilangInfo.name);
					newLangCodeList.add(data);
				}
			}
		}

		JSONObject retValue = new JSONObject();
		try{
			retValue.put("langCodeList", newLangCodeList);
			responseService.responseJson(retValue);

		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 * インポートボタン押下時の処理
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/clickImportButton")
	public String clickImportButton(Map<String,Object> model, @ModelAttribute ImportExportForm importExportForm,  BindingResult bindingResult){
		this.importExportForm = importExportForm;

		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		};

		// バリデーションチェック
		ActionMessages errors = validate();

		String [] result = new String[3];
		result[0] = "0";
		result[1] = "0";
		result[2] = "0";
		if(errors.isEmpty()){
			// インポート実行
			errors = importService.importLang(
					uploadLangDataFile,
					importExportForm.importType,
					importExportForm.langCode,
					importExportForm.langName,
					result);
			if(! errors.isEmpty()){
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}
		}else{
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		if(uploadLangDataFile != null && uploadLangDataFile.exists()){
			FileUtil.dirDelete(uploadLangDataFile.getParentFile());
		}

		setupModel(model);

		if(errors.isEmpty()){
			// キャッシュ削除
			try{
				SaigaiTaskLangUtils.discardMessageCache(importExportForm.langCode);
			}catch(Exception e){
				logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to initialize language info of language code {0}. {1}",importExportForm.langCode,e.getMessage()), e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to initialize language info of language code {0}. {1}",importExportForm.langCode,e.getMessage()), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
				return "/admin/multilang/import/index";
			}

			return "forward:/admin/multilang/import/importresult?totalcount="+result[0] +"&updatecount="+result[1]+"&insertcount="+result[2]+"&redirect=true";
		}else{
			return "/admin/multilang/import/index";
		}
	}


	/**
	 * 入力フォームのバリデーションチェック
	 * @return
	 */
	private ActionMessages validate(){
		ActionMessages errors = new ActionMessages();
		// 必須チェック
		if(importExportForm.importType == null || importExportForm.importType.length() <= 0){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Specify the import method."), false));
		}
		if(importExportForm.langCode == null || importExportForm.langCode.length() <= 0){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Specify the language code."), false));
		}
		if(importExportForm.langName == null || importExportForm.langName.length() <= 0){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Enter language name."), false));
		}
		if(importExportForm.langDataFile == null || importExportForm.langDataFile.getSize() <= 0){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Specify import file."), false));
		}

		// 新規入力の場合、既に作成済みの言語コードを指定した場合はエラーにする。
		if(ImportAction.IMPORTTYPE_NEW.equals(importExportForm.importType)){
			MultilangInfo multilangInfo = multilangInfoService.findByCode(importExportForm.langCode);
			if(multilangInfo != null){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Specified language code already created. Change import method to replacing or updating existing language set."), false));
			}
		}

		// 言語データファイルをチェックする
		if(errors.isEmpty()){
			checkFile(errors);
		}

		return errors;
	}

	/**
	 * 言語リソースファイルの内容チェック
	 * @param errors
	 */
	private void checkFile(ActionMessages errors){

		// 作業ディレクトリの準備
		File tmpDir = FileUtil.getTmpDir();
		String workDirName = "multilang-import-" + loginDataDto.getLocalgovinfoid() + "-" + loginDataDto.getGroupid() + "-" + UUID.randomUUID().toString();
		File workDir = new File(tmpDir, workDirName);
		if(! workDir.exists()){
			workDir.mkdir();
		}

		// アップロードされた言語データファイルの保存
		HashSet<String> allowedExtent = new HashSet<String>(Arrays.asList(new String[]{"csv","txt"}));
		String uplodLangDataFileName = fileUploadService.uploadFile(importExportForm.langDataFile, workDir.getPath(), allowedExtent);
		if(uplodLangDataFileName==null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to update language file (1)."), false));
		}
		uploadLangDataFile = new File(workDir, uplodLangDataFileName);
		if(!uploadLangDataFile.exists()){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to update language file (2)."), false));
		}

		// 言語データファイルを読み込む
		try{
	        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(uploadLangDataFile),"UTF-8"));
			int rowNum = 0;
	        String line;
	        while ((line = br.readLine()) != null) {
        		rowNum++;
	        	// 空行は無視する
	        	if(line == null || line.trim().length() <= 0){
	        		continue;
	        	}else{
	        		// 列数チェック
	        		String [] rowData = line.split("\t", -1);
	        		if(rowData.length != ExportAction.CSVFILE_HEADER.length){
	        			errors.add(ActionMessages.GLOBAL_MESSAGE,
	        					new ActionMessage(lang.__("The number of line No.{0} columns is not {1}.",rowNum,ExportAction.CSVFILE_HEADER.length), false));
	        		}else{
	        			String rowDataId    = rowData[0];
	        			String rowDataKey   = rowData[1];
	        			String rowDataValue = rowData[2];

	        			if(rowDataId == null || rowDataId.trim().length() <= 0){
		        			errors.add(ActionMessages.GLOBAL_MESSAGE,
		        					new ActionMessage(lang.__("First column of line No.{0} is empty.",rowNum), false));
	        			}
	        			if(rowDataKey == null || rowDataKey.trim().length() <= 0){
		        			errors.add(ActionMessages.GLOBAL_MESSAGE,
		        					new ActionMessage(lang.__("Second column of line No.{0} is empty.",rowNum), false));
	        			}
	        			if(rowDataValue == null || rowDataValue.trim().length() <= 0){
		        			errors.add(ActionMessages.GLOBAL_MESSAGE,
		        					new ActionMessage(lang.__("Third column of line No.{0} is empty.",rowNum), false));
	        			}

	        		}
	        	}
	        }
	        br.close();
		}catch(IOException e){

		}

	}

	/**
	 * 言語リソースインポート結果画面を表示
	 * @return
	 */
	@RequestMapping(value="/importresult")
	public String importresult(Map<String,Object> model){
		totalcount = (String)request.getParameter("totalcount");
		updatecount = (String)request.getParameter("updatecount");
		insertcount = (String)request.getParameter("insertcount");

		setupModel(model);

		return "/admin/multilang/import/importresult";
	}
}