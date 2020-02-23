/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.exception.NoSuchFieldRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.GeneralizationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.form.page.GeneralizationHistoryForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.db.GeneralizationhistoryDataService;

/**
 * 総括表履歴のアクションです.
 *
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class GeneralizationHistoryAction extends AbstractPageAction {

	protected GeneralizationHistoryForm generalizationHistoryForm;

	// サービス
	@Resource protected GeneralizationhistoryDataService generalizationhistoryDataService;
	@Resource protected FileService fileService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("generalizationHistoryForm", generalizationHistoryForm);
	}

	/**
	 * 履歴を保存する
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/generalizationHistory/save", produces="application/json")
	public ResponseEntity<String> save(Map<String,Object>model,
			@Valid @ModelAttribute GeneralizationHistoryForm generalizationHistoryForm, BindingResult bindingResult) {

    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	this.generalizationHistoryForm = generalizationHistoryForm;
    	initPage("generalizationHistory", generalizationHistoryForm);

    	long localgovinfoid = loginDataDto.getLocalgovinfoid();
		long menuid = generalizationHistoryForm.menuid;
		long taskid = generalizationHistoryForm.menutaskid;
		//long menutypeid = generalizationHistoryForm.menutypeid;

		// 入力チェック
		if(StringUtil.isEmpty(generalizationHistoryForm.pagetype)) {
			throw new ServiceException(lang.__("Page type has not been specified."));
		}
		if(StringUtil.isEmpty(generalizationHistoryForm.listid)) {
			throw new ServiceException(lang.__("Table ID has not been specified."));
		}

		// 登録日時
		Timestamp registtime = new Timestamp(System.currentTimeMillis());

		// 履歴保存
		GeneralizationhistoryData generalizationhistoryData = new GeneralizationhistoryData();
		generalizationhistoryData.trackdataid = loginDataDto.getTrackdataid();
		//generalizationhistoryData.menutypeid = menutypeid;
		generalizationhistoryData.pagetype = generalizationHistoryForm.pagetype;
		generalizationhistoryData.listid = generalizationHistoryForm.listid;
		generalizationhistoryData.registtime = registtime;
		generalizationhistoryDataService.insert(generalizationhistoryData);

		// ファイル名
		String filename = generalizationhistoryData.id
				+ "-" + generalizationHistoryForm.pagetype + "-" + generalizationHistoryForm.listid
				+ "-" + new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.JAPANESE).format(registtime);

		// CSV 保存
		String csvStr = generalizationHistoryForm.createCsvStringBydataList();
		if(StringUtil.isNotEmpty(csvStr)) {
			// CSV 生成
			File csvFile = saveCSV(localgovinfoid, filename, csvStr);
			String csvRelativePath = fileService.getFileRealRelativePath(csvFile);
			if(csvRelativePath!=null) {
				generalizationhistoryData.csvpath = csvRelativePath;
			}
		}

		// PDF 保存
		File pdfFile = savePDF(filename, localgovinfoid, menuid, taskid);
		if(pdfFile.exists()) {
			generalizationhistoryData.pdfpath = pdfFile.getAbsolutePath();
			String pdfRelativePath = fileService.getFileRealRelativePath(pdfFile);
			if(pdfRelativePath!=null) {
				generalizationhistoryData.pdfpath = pdfRelativePath;
			}
		}

		// パスを更新
		generalizationhistoryDataService.update(generalizationhistoryData);

		// 登録分の情報を取得
		JSONObject json = createHistoryItem(generalizationhistoryData);
		//response.getWriter().println(json.toString());

		final HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);

		//return null;
	}


	/**
	 * 履歴表示用のJSON文字列を返却する.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/generalizationHistory/list", produces="application/json")
	public ResponseEntity<String> list(Map<String,Object>model,@Valid @ModelAttribute GeneralizationHistoryForm generalizationHistoryForm, BindingResult bindingResult) {
		this.generalizationHistoryForm = generalizationHistoryForm;
		try {
			JSONArray array = new JSONArray();
			List<GeneralizationhistoryData> generalizationhistoryDatas = generalizationhistoryDataService.findByTrackdataidAndPagetypeAndListid(loginDataDto.getTrackdataid(), generalizationHistoryForm.pagetype, generalizationHistoryForm.listid);
			for(GeneralizationhistoryData generalizationhistoryData : generalizationhistoryDatas) {
				JSONObject json = createHistoryItem(generalizationhistoryData);
				array.put(json);
			}

			// 出力の準備
			/*response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(array.toString());*/

			final HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(array.toString(), httpHeaders, HttpStatus.OK);

		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 総括表履歴情報をJSON形式に変換します.
	 * @param generalizationhistoryData
	 * @return
	 */
	protected JSONObject createHistoryItem(GeneralizationhistoryData generalizationhistoryData) {
		// 履歴情報を１件返す
		JSONObject json = new JSONObject();
		try {
			json.put("id", generalizationhistoryData.id);
			json.put("registtime", jp.ecom_plat.saigaitask.util.StringUtil.toWareki(generalizationhistoryData.registtime));
			if(StringUtil.isNotEmpty(generalizationhistoryData.csvpath)) {
				json.put("csv", true);
			}
			if(StringUtil.isNotEmpty(generalizationhistoryData.pdfpath)) {
				json.put("pdf", true);
			}
		} catch(Exception e) {
			logger.warn(json);
		}
		return json;
	}

	/**
	 * CSVをファイルに保存します.
	 * @param localgovinfoid
	 * @param filename
	 * @param csvStr
	 * @return CSVファイル
	 */
	public File saveCSV(long localgovinfoid, String filename, String csvStr) {
		try {
			File file = fileService.createHistoryFile(localgovinfoid, filename, "csv");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(new OutputStreamWriter(fos, "MS932"));
					writeCSV(csvStr, pw);
				} finally {
					if(pw!=null) {
						pw.close();
					}
				}
			} finally {
				if(fos!=null) {
					try {
						fos.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			return file;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * PDFファイルを作成します.
	 * @param filename PDFファイル名
	 * @param localgovinfoid 自治体ID
	 * @param menuid メニューID
	 * @param taskid メニュータスクID
	 * @return PDFファイル
	 */
	public File savePDF(String filename, long localgovinfoid, long menuid, long taskid) {
		if(StringUtil.isEmpty(filename)) return null;

		// PDF 生成
		MenuInfo menuInfo = menuInfoService.findById(menuid);
		MenutaskInfo menutaskInfo = menutaskInfoService.findById(taskid);
		MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(menutaskInfo.menuprocessinfoid);
		String menuprocessName = menuprocessInfo.name;
		String menutaskName = menutaskInfo.name;
		String menuName = menuInfo.name;

		ByteArrayOutputStream bos = createListPdf(generalizationHistoryForm.time, menuprocessName, menutaskName, menuName, generalizationHistoryForm.dataList, generalizationHistoryForm.totalable);

		// PDF を保存
		File file = fileService.createHistoryFile(localgovinfoid, filename, "pdf");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			IOUtils.write(bos.toByteArray(), fos);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return file;
	}

	/**
	 * 総括表履歴ファイルをダウンロードする
	 * @return null(フォワードしない)
	 * @throws IOException 
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/generalizationHistory/download")
	@ResponseBody
	public HttpEntity<byte[]> download(Map<String,Object>model, @Valid @ModelAttribute GeneralizationHistoryForm generalizationHistoryForm) throws IOException {
		this.generalizationHistoryForm = generalizationHistoryForm;
    	// 総括表履歴データ
		GeneralizationhistoryData generalizationhistoryData = generalizationhistoryDataService.findById(generalizationHistoryForm.id);
		if(generalizationhistoryData==null) {
			throw new ServiceException(lang.__("No data corresponding to ID = {0}.", generalizationHistoryForm.id));
		}

		// 拡張子からファイルを選択する
		String fieldname = generalizationHistoryForm.suffix+"path";
		try {
			// 拡張子からパスを取得
			Field pathField = ClassUtil.getField(GeneralizationhistoryData.class, fieldname);
			String path = (String) FieldUtil.get(pathField, generalizationhistoryData);

			// ファイルの取得
			File file = null;
			if(!path.startsWith("/")) { // 相対パスの場合
				file = fileService.getFileBy(path);
			}
			// else // 絶対パスの場合

			// ファイルの存在をチェック
			if(file==null || !file.exists()) {
				throw new FileNotFoundException();
			}

			// レスポンス
			InputStream input = new FileInputStream(file);
			//ResponseUtil.download(file.getName(), input);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
			headers.setContentDispositionFormData("filename", file.getName());
			return new HttpEntity<byte[]>(IOUtils.toByteArray(input), headers);
		} catch (NoSuchFieldRuntimeException e) {
			throw new ServiceException(lang.__("Extension {0} not support.", generalizationHistoryForm.suffix));
		} catch (FileNotFoundException e) {
			throw new ServiceException(lang.__("File does not exist."));
		}
	}

	/**
	 * 総括表履歴ファイルのダウンロードリクエストのパラメータを検証する.
	 * @return エラーメッセージ
	 */
	public ActionMessages validateGeneralizationHistory() {
		ActionMessages errors = new ActionMessages();
		if(generalizationHistoryForm.id==0) {
			errors.add("id", new ActionMessage(lang.__("ID is required."), false));
		}
		if(StringUtil.isEmpty(generalizationHistoryForm.pagetype)) {
			errors.add("pagetype", new ActionMessage(lang.__("Page type is required."), false));
		}
		if(StringUtil.isEmpty(generalizationHistoryForm.listid)) {
			errors.add("listid", new ActionMessage(lang.__("Table ID is required."), false));
		}
		if(StringUtil.isEmpty(generalizationHistoryForm.suffix)) {
			errors.add("suffix", new ActionMessage(lang.__("The extension is required."), false));
		}
		return errors;
	}
}
