package jp.ecom_plat.saigaitask.action.page.map;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.AuthInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.PdfRange;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;

/**
 * 印刷範囲設定の保存・削除・読み込み機能用AJAX
 * @see /map/map/PdfRange.jsp
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class PdfRangeAction extends AbstractAction {

	@Resource protected MapmasterInfoService mapmasterInfoService;

	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/pdfRange/insert", method = RequestMethod.POST)
	@ResponseBody
	public String insert() throws SQLException, Exception {

		////////////////////////////////////////////////////////////////
		// eコミマップJSPに合わせた変数を初期化
		UserInfo userInfo = getUserInfo();
		MapInfo mapInfo = getMapInfo();
		int cid = mapInfo.communityId;
		long mapId = mapInfo.mapId;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		////////////////////////////////////////////////////////////////
		//追加
		if (request.getParameter("insert") != null) {
			//※CSRF対策はSpringで行うので、eコミのCSRFチェックは不要
		
			//権限チェック
			if (!AuthInfo.authMapEdit(userInfo, cid, mapInfo)) {
				out.println("{\"error\":\""+lang.__("地図の編集権限がありません")+"\"}"); return null;
			}
			
			String name = FormUtils.getStringParameter(request, "name");
			//カンマ区切りのbbox
			String range = FormUtils.getStringParameter(request, "range");
			
			//全設定
			JSONObject jsonOptions = new JSONObject();
			try {
				String options = FormUtils.getStringParameter(request, "options");
				if(options != null) jsonOptions = new JSONObject(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			int rotate = FormUtils.getIntParameter(request, "rotate");
			
			JSONObject json = new JSONObject();
			try {
				//bbox
				JSONObject jsonRange = new JSONObject("{\"bbox\":["+range+"]}");
				//用紙の向き
				jsonRange.put("rotate", rotate);
				//DBに追加
				long id = PdfRange.insertPdfRange(userInfo, mapId, name, jsonRange, jsonOptions);
				json.put("id", id);
	
			} catch (Exception e) {
				e.printStackTrace();
				json = new JSONObject();
				json.put("error", lang.__("エラーが発生しました。"));
			}
			out.print(json.toString());
			return null;
		}

		return null;
	}

	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/pdfRange/delete", method = RequestMethod.POST)
	@ResponseBody
	public String delete() throws IOException {

		////////////////////////////////////////////////////////////////
		// eコミマップJSPに合わせた変数を初期化
		UserInfo userInfo = getUserInfo();
		//MapInfo mapInfo = getMapInfo();
		//int cid = mapInfo.communityId;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		////////////////////////////////////////////////////////////////
		//削除
		//TODO 管理者権限は自分以外の履歴も消せるようにする？
		if (request.getParameter("delete") != null) {
			//※CSRF対策はSpringで行うので、eコミのCSRFチェックは不要
			
			//権限チェック
			long id = FormUtils.getLongParameter(request, "id");
			
			if (id <= 0) { out.println("{\"error\":\""+lang.__("選択されていません")+"\"}"); return null; }
			
			
			try {
				//印刷範囲を削除
				int ret = PdfRange.deletePdfRange(userInfo, id);
				if (ret > 0) {
					out.print("{\"success\":\""+lang.__("印刷設定を削除しました。")+"\"}");
				} else {
					out.print("{\"error\":\""+lang.__("印刷設定を削除できませんでした。")+"\"}");
				}
			} catch (Exception e) {
				e.printStackTrace();
				out.print("{\"error\":\""+lang.__("エラーが発生しました。")+"\"}");
			}
			return null;
		}

		return null;
	}

	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/pdfRange/list")
	@ResponseBody
	public String list() throws SQLException, Exception {
		////////////////////////////////////////////////////////////////
		// eコミマップJSPに合わせた変数を初期化
		UserInfo userInfo = getUserInfo();
		MapInfo mapInfo = getMapInfo();
		int cid = mapInfo.communityId;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		////////////////////////////////////////////////////////////////
		//一覧返却  DataGridのQueryReadStoreに対応
		
		//権限チェック
		if (!AuthInfo.authMapView(userInfo, cid, mapInfo)) {
			out.println("{\"error\":\""+lang.__("地図の閲覧権限がありません")+"\"}"); return null;
		}

		//DBから印刷範囲一覧取得
		
		String sort = FormUtils.getStringParameter(request, "sort");
		if (sort == null) sort = "-created";
		boolean desc = false;
		if (sort.startsWith("-")) {
			sort = sort.substring(1);
			desc = true;
		}
		int limit = Math.min(100, FormUtils.getIntParameter(request, "count", 100));
		int offset = FormUtils.getIntParameter(request, "start");
		
		//検索対象status=0なら自分の地図、status=1なら地図内の公開検索履歴(全ユーザ)を検索
		short status = FormUtils.getShortParameter(request, "status");
		//ゲスト権限なら公開履歴のみ
		if (userInfo == null || userInfo.authId==UserInfo.GUEST_AUTH_ID) status = PdfRange.STATUS_PUBLISHED;
		if (status == PdfRange.STATUS_DEFAULT) {
			out.print(PdfRange.getPdfRange(userInfo, status, mapInfo, sort, desc, limit, offset).toString());
		}
		else out.print(PdfRange.getPdfRange(null, status, mapInfo, sort, desc, limit, offset).toString());

		return null;
	}

	protected UserInfo getUserInfo() {
		String authId = loginDataDto.getEcomUser();
		return MapDB.getMapDB().getAuthIdUserInfo(authId);
	}

	protected MapInfo getMapInfo() {
		Long localgovinfoid = loginDataDto.getLocalgovinfoid();
		List<MapmasterInfo> list = mapmasterInfoService.findByLocalgovinfoid(localgovinfoid, false);
		MapmasterInfo mapmasterInfo = 0<list.size() ? list.get(0) : null;
		if(mapmasterInfo!=null) return MapDB.getMapDB().getMapInfo(mapmasterInfo.mapid);
		return null;
	}
}
