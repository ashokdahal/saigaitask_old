/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.JQG_DO_SEARCH;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SEARCH_FIELD;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SEARCH_OPER;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SEARCH_STRING;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.beans.util.BeanMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.entity.db.AdminbackupData;
import jp.ecom_plat.saigaitask.service.db.AdminbackupDataService;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/adminbackupData")
public class AdminbackupDataAction extends AbstractAdminAction<AdminbackupData> {

	/** サービスクラス */
	@Resource
	protected AdminbackupDataService adminbackupDataService;

	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/adminbackupData/index";
	}

    /**
     * 一覧情報を取得してJSON形式で返却（Ajax）
     * @return null
     * @throws Exception 
     */
    @RequestMapping(value="/jqgridindex")
	@ResponseBody
    public String jqgridindex() throws Exception {

        //検索条件マップ
        BeanMap conditions = new BeanMap();

		//システム管理 以外の場合は、ログイン者の自治体IDを検索条件にセット
		if (loginDataDto.getGroupid() != 0) {
			conditions.put("localgovinfoid", loginDataDto.getLocalgovinfoid());
		 }

		//検索条件をセット
		boolean isSearch = Boolean.valueOf((String) request.getParameter(JQG_DO_SEARCH)); //検索実行時か否か
		String searchField = (String) request.getParameter(JQG_SEARCH_FIELD); //検索対象
		String searchOper = (String) request.getParameter(JQG_SEARCH_OPER);   //比較演算子
		String searchString =  (String) request.getParameter(JQG_SEARCH_STRING);   //検索文字列
		if(isSearch && StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchOper) && StringUtils.isNotEmpty(searchString)){
			conditions.put(JqGridUtil.getCoditionStr(searchField, searchOper), searchString);
		}

        int page = 1;
		int totalPages = 1;
        // 一覧件数取得
        int count = adminbackupDataService.getCount(conditions);

        // 一覧内容取得
		JSONArray jsonList = new JSONArray();
        if(count > 0){
            List<AdminbackupData> list = adminbackupDataService.listBackup(conditions);
			for (AdminbackupData rec : list) {
				JSONObject jsonRec = new JSONObject();
				jsonRec.put("id", rec.id);
				if (rec.localgovInfo != null)
					jsonRec.put("localgovinfoid", "" + rec.localgovinfoid + ":" + rec.localgovInfo.pref + rec.localgovInfo.city);
				jsonRec.put("groupid", "" + rec.groupid + ":" + rec.groupInfo.name);
				jsonRec.put("name", rec.name);
				jsonRec.put("admin", rec.admin ? "○" : "");
				jsonRec.put("path", rec.path);
				jsonRec.put("registtime", TimeUtil.convertTimestamp2(rec.registtime.toString()));				
				jsonList.add(jsonRec);
			}
        }
        
        //返却値セット
        JSONObject json = new JSONObject();
        json.put("page", page); //現在ページ番号
        json.put("records", count); //総結果件数
        json.put("total", totalPages);  //総ページ数
        json.put("rows", jsonList);   //結果一覧

        //レスポンスへ出力
        adminbackupDataService.responseJson(json);

        return null;
    }

    /**
     * バックカップを実行
     * @return null
     * @throws Exception 
     */
    @RequestMapping(value="/backup")
	@ResponseBody
    public String backup() throws Exception {

		//バックアップ実行
		String error = adminbackupDataService.backup(request.getParameter("name"), loginDataDto);

		adminbackupDataService.responseText(error);

		return null;
	}

    /**
     * リストアを実行
     * @return null
     * @throws Exception 
     */
    @RequestMapping(value="/restore")
	@ResponseBody
    public String restore() throws Exception {
		//リストア実行
		String error = adminbackupDataService.restore(Long.parseLong(request.getParameter("id")));
		adminbackupDataService.responseText(error);

		return null;
	}

}
