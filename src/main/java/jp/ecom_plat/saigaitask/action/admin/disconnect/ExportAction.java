/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.seasar.framework.exception.IORuntimeException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.form.admin.disconnect.InitImportExportForm;
import jp.ecom_plat.saigaitask.service.disconnect.TrackDataExportService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;

/**
 * システムマスタや自治体設定をエクスポートするアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin/disconnect/export")
@RequestMapping("/admin/disconnect/export")
public class ExportAction extends AbstractDisconnectAction {

	public InitImportExportForm initImportExportForm;

	/** 認証情報のエクスポートフラグ */
	public boolean exportAuthInfo;

	// Service
	@Resource protected TrackDataExportService trackDataExportService;
	@Resource protected ExportService exportService;


	@RequestMapping(value="/download")
	@ResponseBody
	public String download(@ModelAttribute InitImportExportForm initImportExportForm) {
		this.initImportExportForm = initImportExportForm;

		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		};

		Long localgovinfoid = Long.parseLong(initImportExportForm.localgovinfoid);

		// 権限チェック (システム管理者は localgovinfoid=0 でログインしている)
		if(0<loginDataDto.getLocalgovinfoid() && !localgovinfoid.equals(loginDataDto.getLocalgovinfoid())) {
			throw new ServiceException(lang.__("Other local gov. data can not be exported."));
		}

		// 認証情報を出力しないようにセット
		exportAuthInfo = false;

		// 添付ファイルをエクスポートする？
		boolean exportAttachedFile = "1".equals(initImportExportForm.exportAttachedFile);

		// Zip に圧縮してダウンロードする
		File zipFile = trackDataExportService.zip(localgovinfoid, exportAuthInfo, exportAttachedFile);

		// 展開し、再度ZIPにする
		InputStream is = null;
		try {
			is = new FileInputStream(zipFile);
			downloadFile(zipFile.getName(), is);
			zipFile.delete();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch(IORuntimeException ie){
			if(zipFile.exists()){
				zipFile.delete();
			}
		} finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return null;
	}
}
