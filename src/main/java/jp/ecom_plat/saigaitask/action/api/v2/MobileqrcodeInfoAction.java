/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import jp.ecom_plat.map.db.StoredConsumer;
import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.MobileqrcodeInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MobileqrcodeInfoService;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * 投稿アプリ認証QR情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/mobileqrcodeInfo")
public class MobileqrcodeInfoAction extends AbstractApiAction {

	/** 班情報サービス */
    @Resource
    protected GroupInfoService groupInfoService;
	/** 投稿アプリ認証QR情報サービス */
    @Resource
    protected MobileqrcodeInfoService mobileqrcodeInfoService;

	/**
	 * 投稿アプリ認証API
	 * @return null
	 * @throws ParseException 
	 * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(value={"/api/v2/mobileqrcodeInfo/auth"})
	@ResponseBody
	public String auth() throws ParseException, NoSuchAlgorithmException {
		String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();

		// このAPIは認証なしでOK
		apiDto.authorize();

		String error = null;
		if(isPostMethod()) {
			// リクエストデータを取得
			JSONObject jsonObject = toJSONObject(apiDto.getRequestData());
			String key = null;

			// get id
			Long id = null;
			key = "id";
			if(jsonObject.containsKey(key) == false || JSONNull.getInstance().equals(jsonObject.get(key))){
				return Response.sendJSONError("invalid parameter: "+key, HttpServletResponse.SC_BAD_REQUEST).execute(response);
			}
			else {
    			id = jsonObject.getLong(key);
			}
			
			// get clientKeyEncryption
			String clientKeyEncryption = null;
			key = "clientKeyEncryption";
			if(jsonObject.containsKey(key) == false || JSONNull.getInstance().equals(jsonObject.get(key))){
				return Response.sendJSONError("invalid parameter: "+key, HttpServletResponse.SC_BAD_REQUEST).execute(response);
			}
			else {
				clientKeyEncryption = jsonObject.getString(key);
			}
			
			// results
			//JSONArray results = new JSONArray();
			JSONObject result = new JSONObject();
			JSONObject jsonresult = new JSONObject();
			while(id!=null) {
				// 設定を取得
				MobileqrcodeInfo mobileqrcodeInfo = mobileqrcodeInfoService.findById(id);
				if(mobileqrcodeInfo==null
						|| mobileqrcodeInfo.valid==false) {
					logger.warn("API ERROR : "+"id="+id+" not found "+path);
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return null;
				}

				// 認証
				StoredConsumer consumer = StoredConsumer.fetch(mobileqrcodeInfo.oauthconsumerid);
				String expect = UserAuthorization.getEncryptedPass(consumer.getConsumerKey());
				if(StringUtil.isEmpty(clientKeyEncryption)
						|| clientKeyEncryption.equals(expect)==false) {
					logger.warn("API ERROR : "+"id="+id+" forbidden "+path);
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return null;
				}

				// 有効期限チェック
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date authDay = sdf.parse(sdf.format(mobileqrcodeInfo.getAuthenticationdate()));
				Date today   = sdf.parse(sdf.format(new Date()));
				if(today.after(authDay)) {
					logger.warn("API ERROR : "+"id="+id+" expired "+path);
					response.setStatus(HttpServletResponse.SC_GONE);
					return null;
				}

				// 投稿アプリ認証QRのJSONを出力
				Long groupid = null;
				Long unitid = null;
				boolean outputSecret = true;
				result = mobileqrcodeInfoService.getQRCodeJSON(id, groupid, unitid, outputSecret);

				// break loop
				break;
			}

			// Response
			//jsonresult.put("total", results.size());
			//jsonresult.put("results", results);
			jsonresult.put("result", result);
			jsonresult.put("error", error);
			responseJSONObject(jsonresult);
		}
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}
}
