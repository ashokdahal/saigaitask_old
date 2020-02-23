/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.extension.jdbc.JdbcManager;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.AdminmenuInfo;
import jp.ecom_plat.saigaitask.service.db.AdminmenuInfoService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;
import jp.ecom_plat.saigaitask.util.StringUtil;

/**
 * 管理画面のサイドメニュー用アクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/db/adminmenuInfo")
public class AdminmenuInfoAction {

    /** ログイン情報 */
    @Resource protected LoginDataDto loginDataDto;

    public List<AdminmenuInfo> adminmenuInfoItems;

    @Resource
    protected AdminmenuInfoService adminmenuInfoService;

    public JdbcManager jdbcManager;

    /** HTTP Request */
    @Resource protected HttpServletRequest request;
	private SaigaiTaskDBLang lang;

	@RequestMapping
    public String index() {
        adminmenuInfoItems = adminmenuInfoService.findAll();
        return "/db/adminmenuInfo/list.jsp";
    }

    /**
     * メニュー項目を取得するためのAJaxアクション。（JSON形式で返却）
     * @return
     * @throws IOException
     */
	@RequestMapping("/load_json")
    public String load_json(HttpServletResponse httpServletResponse) throws IOException {

        //データベースから部署データを全件取得します。
    	if(loginDataDto.isUsual()){
        	adminmenuInfoItems = adminmenuInfoService.findMenuInfoByUnitid(loginDataDto.getUnitid());
    	}else{
        	adminmenuInfoItems = adminmenuInfoService.findMenuInfoByGroupid(loginDataDto.getGroupid());
    	}
    	//System.out.println(">>"+adminmenuInfoItems.toString());

    	// 国際化対応
    	lang = SaigaiTaskLangUtils.getSiteDBLang(request);
    	for(AdminmenuInfo adminmenuInfoItem : adminmenuInfoItems){
    		adminmenuInfoItem.name = lang.__(adminmenuInfoItem.name);
    	}

    	// 出力用ストリームの作成
        //HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        PrintWriter sendPoint = httpServletResponse.getWriter();

        //Entity→JSON形式に変換して出力します。
        //System.out.println("JSONArray>"+jsonArray);
        sendPoint.println(StringUtil.jsonForCyclicalReferences(adminmenuInfoItems));
        sendPoint.flush();
        sendPoint.close();
        return null;
    }
}