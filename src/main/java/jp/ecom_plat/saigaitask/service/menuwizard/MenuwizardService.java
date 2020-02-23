package jp.ecom_plat.saigaitask.service.menuwizard;

import javax.annotation.Resource;

import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;

@org.springframework.stereotype.Service
public class MenuwizardService {

	@Resource
	private TablemasterInfoService tablemasterInfoService;
	
	@Resource
	private MenuloginInfoService menuloginInfoService;

	
	public boolean createTemplateMenu(Long trackId, String layerName) {
		
		// テンプレートメニューから複製を作成
		
		
		// レイヤのコピー作成
		
		return true;
	}

	public boolean deleteTemplateMenu() {
		return true;
	}
}
