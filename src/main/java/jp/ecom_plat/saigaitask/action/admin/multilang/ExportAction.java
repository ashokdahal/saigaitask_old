/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.multilang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangmesInfo;
import jp.ecom_plat.saigaitask.form.admin.multilang.ImportExportForm;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangmesInfoService;

@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin/multilang/export")
@RequestMapping(value="/admin/multilang/export")
public class ExportAction extends AbstractAction{

	public static String [] CSVFILE_HEADER = {"ID","MESSAGEID","MESSAGE"};


	protected ImportExportForm importExportForm;

	/** サービスクラス */
	@Resource
	protected MultilangInfoService multilangInfoService;
	@Resource
	protected MultilangmesInfoService multilangmesInfoService;

	public Map<String,String> langCodeList;

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("langCodeList", langCodeList);
	}

	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object> model, @ModelAttribute ImportExportForm importExportForm) {
		langCodeList = new LinkedHashMap<String, String>();
		List<MultilangInfo> multilangInfos = multilangInfoService.findAll(new OrderByItem("id", OrderingSpec.ASC));
		for(MultilangInfo multilangInfo : multilangInfos){
			langCodeList.put(multilangInfo.code, multilangInfo.code + ":" +multilangInfo.name);
		}

		setupModel(model);

		return "/admin/multilang/export/index";
	}


	@RequestMapping(value="/clickExportButton")
	@ResponseBody
	public String clickExportButton() {

		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		};

		String langCode = (String)request.getParameter("langCode");

		String csvFileName = "SaigaiTask-multilangmesInfo-" + langCode + ".csv";
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(CSVFILE_HEADER[0]);
		sbuf.append("\t");
		sbuf.append(CSVFILE_HEADER[1]);
		sbuf.append("\t");
		sbuf.append(CSVFILE_HEADER[2]);
		sbuf.append("\n");

		MultilangInfo multilangInfo = multilangInfoService.findByCode(langCode);
		if(multilangInfo != null){
			List<MultilangmesInfo> multilangmesInfos = multilangmesInfoService.findByMultilanginfoid(multilangInfo.id);
			for(MultilangmesInfo multilangmesInfo: multilangmesInfos){
				sbuf.append(multilangmesInfo.id);
				sbuf.append("\t");

				String escMessageid = multilangmesInfo.messageid;
				escMessageid = escMessageid.replaceAll("\\\\", "\\\\\\\\");	// "\" to "\\"
				escMessageid = escMessageid.replaceAll("\\n", "\\\\n");		// NL to "\n"
				sbuf.append(escMessageid);
				sbuf.append("\t");

				String escMessage = multilangmesInfo.message;
				escMessage = escMessage.replaceAll("\\\\", "\\\\\\\\");	// "\" to "\\"
				escMessage = escMessage.replaceAll("\\n", "\\\\n");		// NL to "\n"
				sbuf.append(escMessage);
				sbuf.append("\n");
			}
		}

		try{
			byte [] data = sbuf.toString().getBytes("utf-8");
			InputStream is = new ByteArrayInputStream(data);
			downloadFile(csvFileName, is);
		}catch(IOException e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
		}

		return null;
	}

    /**
     * ファイルのダウンロード
     *
     * @param fileName
     * @param in
     */
    protected void downloadFile(String fileName, InputStream in) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
            OutputStream out = response.getOutputStream();
            try {
                InputStreamUtil.copy(in, out);
                OutputStreamUtil.flush(out);
            } finally {
                    OutputStreamUtil.close(out);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            InputStreamUtil.close(in);
        }
    }

}
