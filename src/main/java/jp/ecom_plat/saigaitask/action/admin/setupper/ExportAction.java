/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.form.admin.setupper.ExportForm;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;

/**
 * システムマスタや自治体設定をエクスポートするアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin/setupper/export")
public class ExportAction extends AbstractAction {

	/** ActionForm */
	ExportForm exportForm;

	/** 認証情報のエクスポートフラグ */
	public boolean exportAuthInfo;

	// Service
	@Resource protected ExportService exportService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("exportAuthInfo", exportAuthInfo);

	}

	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/export","/admin/setupper/export/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute ExportForm exportForm, BindingResult bindingResult) throws IOException {
		this.exportForm = exportForm;
		// 権限チェック (システム管理者は localgovinfoid=0 でログインしている)
		if(0<loginDataDto.getLocalgovinfoid() && !exportForm.localgovinfoid.equals(loginDataDto.getLocalgovinfoid())) {
			throw new ServiceException(lang.__("Other local gov. data can not be exported."));
		}

		try {

			// 認証情報を出力しないようにセット
			exportAuthInfo = false;

			// Zip に圧縮してダウンロードする
			File zipFile = exportService.zip(exportForm.localgovinfoid, exportAuthInfo);
			InputStream is = null;
			OutputStream os = null;
			try {
				is = new FileInputStream(zipFile);
				os = response.getOutputStream();
				//HttpUtil.download(zipFile.getName(), is, "application/zip");
				response.setContentType("application/octet-stream;charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename="+zipFile.getName());

				byte[] b = new byte[1024];
				int len;
				while((len=is.read(b)) != -1){
					os.write(b, 0, len);
				}
				is.close();
				os.close();
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if(is!=null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ lang.__("\n Failed to create city data of city configuration."), e);
			ActionMessages errors = new ActionMessages();
			Throwable t = e;
			while(t!=null) {
				if(t instanceof ServiceException) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(t.getMessage(), false));
				}
				t=t.getCause();
			}
			//ActionMessagesUtil.addErrors(bindingResult, errors);
			// マスタマップが無い場合のエラーメッセージが表示されなかったので、bindingResultからsessionに変更
			ActionMessagesUtil.saveMessages(session, errors);

			return "forward:/admin/setupper/initSetup";
		}

		return null;
	}
}
