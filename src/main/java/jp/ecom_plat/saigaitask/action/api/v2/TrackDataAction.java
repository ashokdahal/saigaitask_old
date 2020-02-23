/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.seasar.framework.beans.util.Beans;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.form.TrackForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.TrackService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * インシデント管理APIアクション
 */
@Controller
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/trackData")
public class TrackDataAction extends AbstractApiAction{
	protected TrackForm trackForm;
	/** 災害情報サービス */
	@Resource
	protected TrackDataService trackDataService;

	@Resource
	protected ClearinghouseService clearinghouseService;

	@Resource
	protected TrackService trackService;

	@Resource
	protected UserTransaction userTransaction;

	@Resource
	protected TrackgroupDataService trackgroupDataService;

	@RequestMapping(value={"/api/v2/trackData"})
	@Transactional(propagation=Propagation.NEVER)
	@ResponseBody
	public String index(@RequestParam("trackdataid") Long trackdataid, @ModelAttribute TrackForm trackForm){
		this.trackForm = trackForm;
		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		if(isGetMethod()){
			// results
			JSONObject jsonobj= new JSONObject();
			// Check login
			if(apiDto.getGroupInfo().localgovinfoid != null){
				if(trackdataid != null){
					// trackdataがあるかどうか
					TrackData trackData = trackDataService.findById(trackdataid);
					if(trackData == null) return Response.sendJSONError(lang.__("trackdata not found."), HttpServletResponse.SC_BAD_REQUEST).execute(response);
					long id = trackdataid;
					jsonobj.put("id", id);
					jsonobj.put("trackmapinfoid", trackData.trackmapinfoid);
					jsonobj.put("localgovinfoid", trackData.localgovinfoid);
					jsonobj.put("demoinfoid", trackData.demoinfoid);
					//jsonobj.put("disasterid", trackData.disasterid);
					jsonobj.put("trainigplandataid", trackData.trainingplandataid);
					jsonobj.put("name", trackData.name);
					jsonobj.put("note", trackData.note);
					String starttime = trackData.starttime.toString();
					jsonobj.put("starttime", starttime);
					if(trackData.endtime != null) jsonobj.put("endtime", TimeUtil.iso8601Formatter.format(trackData.endtime));
					jsonobj.put("deleted", trackData.deleted);
				}else{
					return Response.sendJSONError("invalid parameter: trackdataid", HttpServletResponse.SC_BAD_REQUEST).execute(response);
				}
			}
			responseJSONObject(jsonobj);
		}
		else if(isPostMethod()){
			if(request.getContentType().startsWith("application/json")){
				// 本部権限があるかどうか
				if(!apiDto.getGroupInfo().headoffice){
					return Response.sendJSONError("No permission to regist trackData", HttpServletResponse.SC_FORBIDDEN).execute(response);
				}
				// リクエストデータを取得
				JSONObject jsonObject = toJSONObject(apiDto.getRequestData());

				// 必須パラメータチェック
				if(jsonObject.containsKey("name") == false || JSONNull.getInstance().equals(jsonObject.get("name"))){
					return Response.sendJSONError("invalid parameter: name", HttpServletResponse.SC_BAD_REQUEST).execute(response);
				}
				if(jsonObject.containsKey("disasterid") == false || JSONNull.getInstance().equals(jsonObject.get("disasterid"))){
					return Response.sendJSONError("invalid parameter: disasterid", HttpServletResponse.SC_BAD_REQUEST).execute(response);
				}
				trackForm.name = jsonObject.get("name").toString();
				//trackForm.disasterid = jsonObject.get("disasterid").toString();
				if(jsonObject.has("note")) trackForm.note = jsonObject.get("note").toString();

				// 災害情報
				TrackData trackData = trackService.insertDisaster(apiDto.getGroupInfo().localgovinfoid, trackForm, apiDto.getGroupInfo());

				JSONObject result = new JSONObject();
				result.put("id", trackData.id);
				result.put("name", trackData.name);
				//result.put("disasterid", trackData.disasterid);
				if(jsonObject.has("note")) result.put("note", trackData.note);

				return responseJSONObject(result);
			}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		}
		else if(isPutMethod()){
			if(request.getContentType().startsWith("application/json")){
				if(trackdataid != null){
					// trackdataがあるかどうか
					TrackData trackData = trackDataService.findById(trackdataid);
					if(trackData == null) return Response.sendJSONError(lang.__("trackdata not found."), HttpServletResponse.SC_BAD_REQUEST).execute(response);
					// 本部権限があるかどうか
					if(!apiDto.getGroupInfo().headoffice){
						return Response.sendJSONError("No permission to regist trackData", HttpServletResponse.SC_FORBIDDEN).execute(response);
					}
					// リクエストデータを取得
					JSONObject jsonObject = toJSONObject(apiDto.getRequestData());
					// 必須パラメータチェック
					if(jsonObject.containsKey("name") == false || JSONNull.getInstance().equals(jsonObject.get("name"))){
						return Response.sendJSONError("invalid parameter: name", HttpServletResponse.SC_BAD_REQUEST).execute(response);
					}
					if(jsonObject.containsKey("disasterid") == false || JSONNull.getInstance().equals(jsonObject.get("disasterid"))){
						return Response.sendJSONError("invalid parameter: disasterid", HttpServletResponse.SC_BAD_REQUEST).execute(response);
					}
					// endtimeをtrackdataに格納
					if(jsonObject.has("endtime")){
						String endtimestr = jsonObject.get("endtime").toString();
						Date parseDate = TimeUtil.parseISO8601(endtimestr);
						if(parseDate!=null) {
							Timestamp endtime = new Timestamp(parseDate.getTime());
							trackData.endtime = endtime;
						}
						// copyColumnFieldOnlyの処理でendtimeは不要となるので削除
						jsonObject.remove("endtime");
					}
					DatabaseUtil.copyColumnFieldOnly(jsonObject, trackData);
					// 更新
					Beans.copy(trackData, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					trackForm.name = jsonObject.get("name").toString();
					//trackForm.disasterid = jsonObject.get("disasterid").toString();

					if(jsonObject.has("note")) trackForm.note = jsonObject.get("note").toString();
					else trackForm.note = "";
					if(jsonObject.has("endtime")) trackForm.endtime = jsonObject.getString("endtime");
					else trackForm.endtime = "";

					// 災害グループがあれば引き継ぐ
					copyTrackgroupDatas(trackData, trackForm);

					long localgovinfoid = apiDto.getGroupInfo().localgovinfoid;
					long groupid = apiDto.getGroupInfo().id;
					trackService.updateDisaster(localgovinfoid, groupid, trackForm);

					// endtimeがあれば災害完了処理
					if(trackData.endtime != null) trackService.completeDisaster(localgovinfoid, trackForm);

					JSONObject result = new JSONObject();
					result.put("id", trackData.id);
					result.put("name", trackData.name);
					//result.put("disasterid", trackData.disasterid);
					if(jsonObject.has("note")) result.put("note", trackData.note);
					if(trackData.endtime != null) result.put("endtime", TimeUtil.iso8601Formatter.format(trackData.endtime));

					return responseJSONObject(result);
				}
			}
		}
		else if(isPatchMethod()){
			if(request.getContentType().startsWith("application/json")){
				if(trackdataid != null){
					// trackdataがあるかどうか
					TrackData trackData = trackDataService.findById(trackdataid);
					if(trackData == null) return Response.sendJSONError(lang.__("trackdata not found."), HttpServletResponse.SC_BAD_REQUEST).execute(response);
					// 本部権限があるかどうか
					if(!apiDto.getGroupInfo().headoffice){
						return Response.sendJSONError("No permission to regist trackData", HttpServletResponse.SC_FORBIDDEN).execute(response);
					}
					// リクエストデータを取得
					JSONObject jsonObject = toJSONObject(apiDto.getRequestData());
					// endtimeをtrackdataに格納
					if(jsonObject.has("endtime")){
						String endtimestr = jsonObject.get("endtime").toString();
						Date parseDate = TimeUtil.parseISO8601(endtimestr);
						if(parseDate!=null) {
							Timestamp endtime = new Timestamp(parseDate.getTime());
							trackData.endtime = endtime;
						}
						// copyColumnFieldOnlyの処理でendtimeは不要となるので削除
						jsonObject.remove("endtime");
					}
					DatabaseUtil.copyColumnFieldOnly(jsonObject, trackData);
					// 更新
					Beans.copy(trackData, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					if(jsonObject.has("name")) trackForm.name = jsonObject.get("name").toString();
					//if(jsonObject.has("disasterid")) trackForm.disasterid = jsonObject.get("disasterid").toString();
					if(jsonObject.has("note")) trackForm.note = jsonObject.get("note").toString();
					if(jsonObject.has("endtime")) trackForm.endtime = jsonObject.getString("endtime");

					// 災害グループがあれば引き継ぐ
					copyTrackgroupDatas(trackData, trackForm);

					long localgovinfoid = apiDto.getGroupInfo().localgovinfoid;
					long groupid = apiDto.getGroupInfo().id;
					trackService.updateDisaster(localgovinfoid, groupid, trackForm);

					// endtimeがあれば災害完了処理
					if(trackData.endtime != null) trackService.completeDisaster(localgovinfoid, trackForm);

					JSONObject result = new JSONObject();
					result.put("id", trackData.id);
					result.put("name", trackData.name);
					//result.put("disasterid", trackData.disasterid);
					if(jsonObject.has("note")) result.put("note", trackData.note);
					if(trackData.endtime != null) result.put("endtime", TimeUtil.iso8601Formatter.format(trackData.endtime));

					return responseJSONObject(result);
				}
			}
		}
		else if(isDeleteMethod()){
			// trackdataがあるかどうか
			TrackData trackData = trackDataService.findById(trackdataid);
			if(trackData == null) return Response.sendJSONError(lang.__("trackdata not found."), HttpServletResponse.SC_BAD_REQUEST).execute(response);
			// 本部権限があるかどうか
			if(!apiDto.getGroupInfo().headoffice){
				return Response.sendJSONError("No permission to regist trackData", HttpServletResponse.SC_FORBIDDEN).execute(response);
			}
			// 既に災害が起動していなければ削除
			if(trackDataService.findById(trackdataid).endtime != null){
				// 災害グループがあれば引き継ぐ
				copyTrackgroupDatas(trackData, trackForm);
				trackService.deleteDisaster(trackdataid);
			}else{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			JSONObject result = new JSONObject();
			result.put("name", trackData.name);

			return responseJSONObject(result);
		}
		return null;
	}

	void copyTrackgroupDatas(TrackData trackData, TrackForm trackForm) {
		List<TrackgroupData> trackgroupDatas = trackgroupDataService.findByPreftrackdataid(trackData.id);
		trackForm.setCitytrackdataids(new ArrayList<String>(trackgroupDatas.size()));
		for(TrackgroupData trackgroupData : trackgroupDatas) {
			trackForm.citytrackdataids.add(String.valueOf(trackgroupData.citytrackdataid));
		}
	}
}
