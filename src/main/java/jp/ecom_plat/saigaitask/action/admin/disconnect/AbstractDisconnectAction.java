/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;

@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractDisconnectAction extends AbstractAction {
	public static final String VERSION="1.4";

	/** 自治体切り替えSELECTオプション */
	public Map<Long, String> localgovSelectOptions;

	@Resource protected HttpServletResponse response;
	@Resource protected DisasterMasterService disasterMasterService;
	@Resource protected MenuloginInfoService menuloginInfoService;

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("localgovSelectOptions", localgovSelectOptions);
	}

	public void initDisconnect() {
		// アクションの初期化
		initAction();
	}

	public boolean loginLocalgovInfo(long localgovinfoid) {
		LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
		if(localgovInfo==null) throw new ServiceException(lang.__("Local gov. ID = {0} not found.", localgovinfoid));
		// 自治体情報のログイン
		loginDataDto.setLocalgovinfoid(localgovInfo.id);
		loginDataDto.setLocalgovInfo(localgovInfo);
		// 使っている災害種別を取得
		HashSet<Integer> usingDisasterids = menuloginInfoService.getUsingDisasterIds(loginDataDto.getLocalgovinfoid());
		// 使っている災害種別があれば、先頭のものでとりあえずログイン
		if(0<usingDisasterids.size()) {
			//loginDataDto.getDisasterid() = usingDisasterids.iterator().next();
		}
		return true;
	}

	public void createLocalgovSelectOptions(){
		String pref = "";
		String city = "";
		String section = "";

		// 自治体切り替えSELECTオプション
		localgovSelectOptions = new LinkedHashMap<Long, String>();
		// システム管理者でログイン中
		if(loginDataDto.getGroupid()==0) {
			List<LocalgovInfo> localgovInfos = localgovInfoService.findAll(
					Operations.asc(LocalgovInfoNames.prefcode()),
					Operations.asc(LocalgovInfoNames.citycode()),
					Operations.asc(LocalgovInfoNames.id())
			);
//			localgovSelectOptions.put(0L, "＜新規作成＞");
			for(LocalgovInfo localgovInfo : localgovInfos) {
				if(localgovInfo.id.equals(0L)) continue;
				pref = localgovInfo.pref;
				city = localgovInfo.city;
				section = localgovInfo.section;
				if(pref == null){
					pref = "";
				}
				if(city == null){
					city = "";
				}
				if(section == null){
					section = "";
				}
				localgovSelectOptions.put(localgovInfo.id, pref+city+section);
			}
		}
		else {
			LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
			pref = localgovInfo.pref;
			city = localgovInfo.city;
			section = localgovInfo.section;
			if(pref == null){
				pref = "";
			}
			if(city == null){
				city = "";
			}
			if(section == null){
				section = "";
			}
			localgovSelectOptions.put(localgovInfo.id, pref+city+section);
		}

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
