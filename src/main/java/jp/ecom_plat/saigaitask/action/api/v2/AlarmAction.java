/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;



import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AlarmshowDataService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * ポップアップのAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/alarm")
public class AlarmAction extends AbstractApiAction{

	@Resource protected AlarmmessageDataService alarmMessageDataService;
	@Resource protected AlarmshowDataService alarmShowDataService;
	@Resource protected TrackDataService trackDataService;

	@RequestMapping(value={"/api/v2/alarm"})
	@ResponseBody
	public String index(){
		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		if(isPostMethod()){

			if(request.getContentType().startsWith("application/json")) {
				// ログインしていなければ、登録できない
				if(apiDto.getGroupInfo()==null) {
					return Response.sendJSONError("No permission to regist UserInfo", HttpServletResponse.SC_FORBIDDEN).execute(response);
				}
				//リクエストデータを取得
				JSONObject jsonObject = toJSONObject(apiDto.getRequestData());
				AlarmmessageData alarm = new AlarmmessageData();

				// 必須パラメータチェック
				// message
				if(jsonObject.containsKey("message")==false || JSONNull.getInstance().equals(jsonObject.get("message"))) {
					return Response.sendJSONError("invalid parameter: message", HttpServletResponse.SC_BAD_REQUEST).execute(response);
				}
				if(jsonObject.containsKey("groupids")==false || JSONNull.getInstance().equals(jsonObject.get("groupids"))) {
					return Response.sendJSONError("invalid parameter: groupids", HttpServletResponse.SC_BAD_REQUEST).execute(response);
				}

				Long localgovInfoid = loginDataDto.getLocalgovInfo().id;
				List<TrackData> trackdata = trackDataService.findByCurrentTrackDatas(localgovInfoid);
				if(trackdata.isEmpty()){
					// -1 だと平常時、災害時関係なく表示される
					//alarm.trackdataid = -1L;
					// 平常時へのアラームなので 0L をセットする。
					alarm.trackdataid = 0L;
				}else{
					alarm.trackdataid = loginDataDto.getTrackdataid();
				}
				alarm.sendgroupid = apiDto.getGroupInfo().id;
				alarm.localgovinfoid = localgovInfoid;
				alarm.showmessage = true;
				alarm.duration = 0;
				alarm.registtime = new Timestamp(System.currentTimeMillis());
				alarm.deleted = false;
				alarm.noticeto = "";
				alarm.noticeto = alarm.noticeto.trim();
				DatabaseUtil.copyColumnFieldOnly(jsonObject, alarm);

				JSONObject result = new JSONObject();
				result.put("message", jsonObject.get("message"));
				JSONArray results = new JSONArray();

				for(Object groupid: jsonObject.getJSONArray("groupids")){
					alarm.groupid = Long.parseLong(groupid.toString());
					// INSERT
					alarmMessageDataService.insert(alarm);
					jsonObject = toJSONObject(alarm);

					result.put("groupid", Long.parseLong(groupid.toString()));
					results.add(result);
				}

				return responseJSONObject(results);
			}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		}else{
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;

	}
}
