/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import java.util.HashMap;

import javax.annotation.Resource;

import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.service.db.ClearinghousemetadataInfoService;
import jp.ecom_plat.saigaitask.util.CSWUtil;
import jp.ecom_plat.saigaitask.util.Config;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/clearinghouse")
public class ClearinghouseAction extends AbstractAdminAction<LocalgovInfo> {

	// サービスクラス
	@Resource protected ClearinghousemetadataInfoService clearinghousemetadataInfoService;

	/**
	 * CSWで地図を検索するAJAXサービス。
	 *
	 * @throws ServiceException
	 */
	@RequestMapping(value="/search", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String search() throws ServiceException {

		try {
			String keyword = (String)request.getParameter("keyword");
			String orderby = (String)request.getParameter("orderby");
			String startPosition = (String)request.getParameter("startPosition");
			String maxRecords = (String)request.getParameter("maxRecords");
			Boolean isTraining= "true".equalsIgnoreCase(request.getParameter("training"));

			if (orderby == null) orderby = "5";
			if (StringUtil.isEmpty(startPosition)) startPosition = "1";

			HashMap<String,String> condition = new HashMap<String,String>();
	    	condition.put("KEYWORD", keyword);
	    	condition.put("ORDERBY", orderby);
	    	condition.put("startPosition", startPosition);
	    	condition.put("maxRecords", maxRecords);

	    	logger.info("[search clearinghouse333] keyword:" + keyword + ", orderby:" + orderby + ", startPosition:" + startPosition + ", maxRecords:" + maxRecords);
	    	logger.info("[search clearinghouse] keyword:" + keyword + ", orderby:" + orderby + ", startPosition:" + startPosition + ", maxRecords:" + maxRecords);

	    	try {
				// 災害モードと訓練モードでクリアリングハウスの登録先を変更する
				String cswurl = isTraining ? Config.getString("CSWURL_TRAINING") : Config.getString("CSWURL");
				String cswuser = isTraining ? Config.getString("CSWUSER_TRAINING") : Config.getString("CSWUSER");
				String cswpasswd = isTraining ? Config.getString("CSWPASSWD_TRAINING") : Config.getString("CSWPASSWD");
				// カスケード検索設定
				String cswDistributedSearch = isTraining ? Config.getString("CSWDISTRIBUTEDSEARCH_TRAINING") : Config.getString("CSWDISTRIBUTEDSEARCH");
				if(StringUtil.isEmpty(cswDistributedSearch)) cswDistributedSearch="false";
				condition.put("DISTRIBUTEDSEARCH", cswDistributedSearch);
				//HOPCOUNTは分散検索する階層の指定で、次のサーバにリクエストする際に-1して渡します。
				//HOPCOUNT=1だと自分の検索して終わりです。
				//大きい値を指定しても問題ないですが、分散検索の設定していなければHOPCOUNTが残っていても終わりです。
				String cswHopCount= isTraining ? Config.getString("CSWHOPCOUNT_TRAINING") : Config.getString("CSWHOPCOUNT");
				if(StringUtil.isEmpty(cswHopCount)) cswHopCount="1";
				condition.put("HOPCOUNT", cswHopCount);
	    		String xml = CSWUtil.getRecords(cswurl, cswuser, cswpasswd, condition, Integer.parseInt(startPosition), Integer.parseInt(maxRecords), Integer.parseInt(orderby));

	    		//logger.info("KEYWORD:" + keyword + ", Result XML:" + xml);
	    		//logger.debug("xml:" + xml);
	    		String json = CSWUtil.GetRecordsResponseToJSON(xml);
	    		json = json.replace("\n", "<br/>");

	    		// タブのエスケープ
	    		// jQuery.ajax にて JSONレスポンスの場合、タブを直接出力すると parseerror となるため。
	    		json = json.replace("\t", "\\t");

	    		// replace("\\", "/") だと、ダブルクォートのエスケープ用\ も変換されてしまいJSONparseErrorとなってしまうので、
	    		// ここで置換はしない。
	    		//json = json.replace("\\", "/");
	    		//logger.debug("json:" + json);
	    		responseService.response(json);
	    	}
			catch (Exception ex) {
				logger.error(ex.getMessage());
			}

		}catch(Exception ex){
			logger.error(ex.getMessage());
		}

		return null;
	}


	/**
	 * CSWで地図の概要情報を取得するAJAXサービス。
	 *
	 * @throws ServiceException
	 */
	@RequestMapping(value="/getMapDetail", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String getMapDetail() throws ServiceException {

    	logger.info("--- getMapDetail ---");

    	try {
			String metaDataId = (String)request.getParameter("metaDataId");
			Boolean isTraining= "true".equalsIgnoreCase(request.getParameter("training"));
			// 災害モードと訓練モードでクリアリングハウスの登録先を変更する
			String cswurl = isTraining ? Config.getString("CSWURL_TRAINING") : Config.getString("CSWURL");
			String cswuser = isTraining ? Config.getString("CSWUSER_TRAINING") : Config.getString("CSWUSER");
			String cswpasswd = isTraining ? Config.getString("CSWPASSWD_TRAINING") : Config.getString("CSWPASSWD");

	    	try {
	    		String xml = CSWUtil.getRecordById(cswurl,cswuser,cswpasswd, metaDataId);
	    		logger.info("getMapDetail XML:" + xml);
	    		String json = CSWUtil.MDMetadataToJSON(xml);
	    		json = json.replace("\n", "<br/>");
	    		//logger.debug("json:" + json);
	    		responseService.response(json);
	    	}
			catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}

		return null;
	}

	/**
	 * URL(WMSCapabilities)で地図の概要情報を取得するAJAXサービス。
	 *
	 * @throws ServiceException
	 */
	@RequestMapping(value="/getMapDetailByWMSCapabilities")
	@ResponseBody
	public String getMapDetailByWMSCapabilities() throws ServiceException {
		logger.info("--- getMapDetailByWMSCapabilities ---");

		try {
			String wmsCapsUrl = (String)request.getParameter("wmsCapsUrl");

	    	try {
	    		logger.info("wmsCapsUrl:" + wmsCapsUrl);
	    		String json = CSWUtil.getWMSCapabilities(wmsCapsUrl);
	    		logger.info("JSON RESULT:" + json);
	    		json = json.replace("\n", "<br/>");
	    		responseService.response(json);
	    	}
			catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}

		return null;
	}



}
