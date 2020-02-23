/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.form.OrganizationForm;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;

/**
 * 公共情報コモンズ発信時の発表組織／作成組織選択ダイアログを表示するアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class OrganizationAction extends AbstractAction {

	/** アクションフォーム */
	protected OrganizationForm organizationForm;

	/** 発表組織／作成組織アクションフォーム */
	public List<OrganizationForm> organizationFormList;
	/** 組織タイプ(1:発表組織 2:作成組織) */
	public String organizationType = "";
	/** 発表組織／作成組織が見つかったかどうか */
	public boolean organizationFound = false;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("organizationForm", organizationForm);
		model.put("organizationFormList", organizationFormList);
		model.put("organizationType", organizationType);
		model.put("organizationFound", organizationFound);
	}

	/**
     * 発表組織／作成組織選択ダイアログ表示
     * @return 次ページ
	 * @throws ServiceException
     */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/organization/{organizationType}", "/organization/{organizationType}"})
//  public String index(Map<String,Object>model) throws ServiceException { // 2017.07.28 kawada 修正
    public String index(Map<String,Object>model, @ModelAttribute OrganizationForm organizationForm) throws ServiceException {
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

		try {
			List<LocalgovInfo> govList= localgovInfoService.findLocalgovInfoJoinGroupInfo(loginDataDto.getLocalgovinfoid());
			if (govList == null) {
				setupModel(model);
				return "/organization/index";
			}

			organizationFormList = new ArrayList<OrganizationForm>();
			organizationType = organizationForm.organizationType;

			int rownum = 1;
			// 検索結果を元に発表組織や作成組織の様式に整える
			for (LocalgovInfo gov : govList) {
				if (gov.groupInfos != null) {
					for (GroupInfo group : gov.groupInfos) {
							organizationForm = new OrganizationForm();
							organizationForm.rownum = rownum;																						// 行番号
							organizationForm.organizationName = StringUtil.isNotEmpty(gov.city) ? gov.city : gov.pref;			// 組織名
							organizationForm.organizationCode =  PublicCommonsUtils.getLocalGovermentCode(gov);			// 地方公共団体コード(チェックディジェット付き)
							organizationForm.organizationDomainName = gov.domain;															// 組織ドメイン
							organizationForm.officeName = StringUtil.isNotEmpty(group.name) ? group.name : "";					// 部署名
							organizationForm.officeNameKana = StringUtil.isNotEmpty(group.namekana) ? group.namekana : "";	// 部署名カナ
							organizationForm.officeLocationArea = StringUtil.isNotEmpty(group.address) ? group.address : "";	// 部署住所
							organizationForm.phone = StringUtil.isNotEmpty(group.phone) ? group.phone : "";						// 部署電話番号
							organizationForm.fax = StringUtil.isNotEmpty(group.fax) ? group.fax : "";										// 部署FAX番号
							organizationForm.email = StringUtil.isNotEmpty(group.email) ? group.email : "";							// 部署メールアドレス
							organizationForm.officeDomainName = StringUtil.isNotEmpty(group.domain) ? group.domain : "";	// 部署ドメイン
							organizationFormList.add(organizationForm);
							rownum++;
					}
				} else {
					organizationForm = new OrganizationForm();
					organizationForm.rownum = rownum;																				// 行番号
					organizationForm.organizationName = StringUtil.isNotEmpty(gov.city) ? gov.city : gov.pref;	// 組織名
					organizationForm.organizationCode =  PublicCommonsUtils.getLocalGovermentCode(gov);	// 地方公共団体コード(チェックディジェット付き)
					organizationForm.organizationDomainName = gov.domain;													// 組織ドメイン
					organizationForm.officeName = "";												// 部署名
					organizationForm.officeNameKana = "";										// 部署名カナ
					organizationForm.officeLocationArea = "";									// 部署住所
					organizationForm.phone = "";														// 部署電話番号
					organizationForm.fax = "";															// 部署FAX番号
					organizationForm.email = "";														// 部署メールアドレス
					organizationForm.officeDomainName = "";									// 部署ドメイン
					organizationFormList.add(organizationForm);
					rownum++;
				}
			}
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}
		if (organizationFormList != null) organizationFound = true;
		setupModel(model);
//    	return "/Organization/index"; // 2017.07.28 kawada 修正
    	return "/organization/index";
	}
}
