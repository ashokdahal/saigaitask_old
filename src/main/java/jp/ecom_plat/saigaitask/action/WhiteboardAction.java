package jp.ecom_plat.saigaitask.action;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.WhiteboardData;
import jp.ecom_plat.saigaitask.form.WhiteboardForm;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.WhiteboardDataService;

/**
 * ホワイトボード機能アクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class WhiteboardAction extends AbstractAction {
	protected WhiteboardForm whiteboardForm;
	/** 班情報リスト */
	public List<GroupInfo> groupInfoItems;
	public String message;
	/** 班情報サービス */
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected WhiteboardDataService whiteboardDataService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("groupInfoItems", groupInfoItems);
		model.put("message", message);
	}
	/**
	 * ホワイトボードの初期ページを表示する
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/whiteboard")
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute WhiteboardForm whiteboardForm, BindingResult bindingResult){
		this.whiteboardForm = whiteboardForm;
		// 平常時はIDが一番若い班を表示する
		if(whiteboardForm.groupid.equals("0")){
			whiteboardForm.groupid = groupInfoService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid()).get(0).id.toString();
		}
		long groupidLong = Long.parseLong(whiteboardForm.groupid);
		WhiteboardData whiteboardData = whiteboardDataService.findBygroupIdAndTrackdataId(groupidLong, loginDataDto.getTrackdataid());
		// メッセージを取得
		if(whiteboardData != null){
			message = whiteboardData.message;
			// 改行コードを削除(保存時に行っているが念のため)
			message = message.replace("\n", "");
			message = message.replace("\r\n", "");

			// 最終更新日時を取得
			if(whiteboardData.registtime != null){
				whiteboardForm.registtime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(whiteboardData.registtime);
			}
		}
		groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
		setupModel(model);
		return "/whiteboard/index";
	}

	/**
	 * 新規メッセージの作成
	 * @return JSON Object
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/whiteboard/saveMessage", produces="application/json", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public ResponseEntity<String> saveMessage(Map<String,Object>model,
			@Valid @ModelAttribute WhiteboardForm whiteboardForm, BindingResult bindingResult) {
		this.whiteboardForm = whiteboardForm;
    	JSONObject json = new JSONObject();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String message = request.getParameter("message");
		// 改行コードを削除
		message = message.replace("\n", "");
		message = message.replace("\r\n", "");
		// スレッドのID
		Long groupid = Long.parseLong(request.getParameter("groupid"));

		WhiteboardData whiteboardData = new WhiteboardData();
		whiteboardData.groupid = groupid;
		whiteboardData.message = message;
		whiteboardData.trackdataid = loginDataDto.getTrackdataid();
		/*registtime = new Timestamp(System.currentTimeMillis());
		registtime.to
		whiteboardData.registtime = registtime;*/
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		whiteboardData.registtime = timestamp;
		whiteboardForm.registtime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timestamp);
		whiteboardDataService.insert(whiteboardData);

		try {
			setupModel(model);
			json.put("message", message);
			json.put("groupid", groupid);
			json.put("registtime", whiteboardForm.registtime);
			json.put("sentmessage", lang.__("Sent completed."));

			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
			/*PrintWriter out;
			out = response.getWriter();
			out.print(json.toString());*/
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();

			return null;
		}
	}

}
