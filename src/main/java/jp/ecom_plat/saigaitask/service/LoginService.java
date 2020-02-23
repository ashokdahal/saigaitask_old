/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.LoginData;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.service.db.AssemblestateDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.LoginDataService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteotriggerDataService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupInfoService;
import jp.ecom_plat.saigaitask.service.db.SafetystateMasterService;
import jp.ecom_plat.saigaitask.service.db.StationclassInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;

@org.springframework.stereotype.Service
public class LoginService extends BaseService {

	Logger logger = Logger.getLogger(getClass());

	/** ログイン情報 */
	@Resource public LoginDataDto loginDataDto;

	/** 災害対応中の記録データリスト */
	public List<TrackData> trackDatas;

	/** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 災害情報マスターサービス */
	@Resource
	protected DisasterMasterService disasterMasterService;

	/** 班情報リスト */
	public List<GroupInfo> groupInfoItems;
	/** 班情報サービス */
	@Resource
	protected GroupInfoService groupInfoService;
	/** 班情報 */
	protected GroupInfo groupInfo;

	/** 記録データサービス */
	@Resource
	protected TrackDataService trackDataService;

	/** ログインデータサービス */
	@Resource LoginDataService loginDataService;

	/** メニューログイン情報サービス */
	@Resource MenuloginInfoService menuloginInfoService;

	/** 気象情報トリガーデータサービス */
	@Resource MeteotriggerDataService meteotriggerDataService;

	/** 体制区分サービス */
	@Resource StationclassInfoService stationclassInfoService;

	/** 通知グループ */
	@Resource NoticegroupInfoService noticegroupInfoService;

	/** 職員参集状況サービス */
	@Resource AssemblestateDataService assemblestateDataService;

	/** 安否確認状況マスタサービス */
	@Resource SafetystateMasterService safetystateMasterService;

	/** 言語情報リスト */
	public List<MultilangInfo> multilangInfoItems;

	/** 職員参集に対するURLでの安否状況更新メッセージ表示用 */
	public String message;

	/** マスター確認 */
	public boolean bmaster = false;

	/** 災害対応モード */
	public static final int MODE_TASK = 1;
	/** 過去閲覧モード */
	public static final int MODE_MASTER = 0;
	/** 平常時モード */
	public static final int MODE_USUAL = 2;
	/** セットアッパーにログインする */
	public static final int MODE_SETUPPER = 3;
	
	/** Spring Security の ADMIN権限 */
	public static final String AUTHORITY_ADMIN="ADMIN";
	/** Spring Security の USER権限 */
	public static final String AUTHORITY_USER="USER";
	
	/**
	 * ログイン共通処理
	 * @param mode モード
	 * @return 次ページ
	 */
	public String login(int mode, LoginForm loginForm, Object info) {
		GroupInfo groupInfo = null;
		UnitInfo unitInfo = null;

		//StringBuffer buff = request.getRequestURL();
		//String url2 = buff.toString();

		// デフォルト値でリセット
		Beans.copy(new LoginDataDto(), loginDataDto).execute();
		loginDataDto.setPassword(loginForm.password);
		//平常時
		if (mode == MODE_USUAL) {
			unitInfo = (UnitInfo) info;
			//loginDataDto.getUnitid() = indexForm.getUnitidLong();
			loginDataDto.setUnitid(unitInfo.id);
			loginDataDto.setUnitInfo(unitInfo);
			loginDataDto.setLocalgovinfoid(unitInfo.localgovinfoid);
			loginDataDto.setDisasterid(0);

			//権限
			loginDataDto.setAdmin(unitInfo.admin);
			loginDataDto.setUsual(true);

			// eコミから移したサーブレット(PdfServlet)の認証で使うため、eコミのアカウントをセッションに設定してログイン状態にする
			session.setAttribute("authId", unitInfo.ecomuser);
		}
		//災害時
		else { // MODE_TASK, MODE_MASTER
			groupInfo = (GroupInfo) info;
			//loginDataDto.getGroupid() = indexForm.getGroupidLong();
			loginDataDto.setGroupid(groupInfo.id);
			loginDataDto.setGroupInfo(groupInfo);
			loginDataDto.setLocalgovinfoid(groupInfo.localgovinfoid);
			
			/* 災害種別が未指定の場合に自動選択する処理は、V2.0では不要
			if(indexForm.disasterid.equals("0")){
				HashSet<Integer> usingDisasterids = menuloginInfoService.getUsingDisasterIds(loginDataDto.getLocalgovinfoid());
				loginDataDto.getDisasterid() = Integer.parseInt(usingDisasterids.iterator().next().toString());
			}else{
				loginDataDto.getDisasterid() = Integer.parseInt(indexForm.disasterid);
			}
			*/
			loginDataDto.setDisasterid(1);

			//権限
			loginDataDto.setAdmin(groupInfo.admin);
			loginDataDto.setUsual(false);

			// eコミから移したサーブレット(PdfServlet)の認証で使うため、eコミのアカウントをセッションに設定してログイン状態にする
			session.setAttribute("authId", groupInfo.ecomuser);
		}

		// 自治体ログイン
		LocalgovInfo gov = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		// システム管理者であればセッションの自治体IDは０とするそうでなければ、地方自治体情報を設定する
		if (gov == null){
			loginDataDto.setLocalgovinfoid(0L);
			loginDataDto.setLocalgovInfo(null);
		}
		else {
			loginDataDto.setLocalgovinfoid(gov.id);
			loginDataDto.setLocalgovInfo(gov);
			// アラーム取得時間を自治体情報から設定
			if(gov.alarminterval!=null) {
				loginDataDto.setAlarmInterval(gov.alarminterval);
			}
		}

		//loginDataDto.getLocalgovinfoid() = 2;

		//loginDataDto.isEdiable() = groupInfo.editable;
		//loginDataDto.visible = groupInfo.visible;

		//System.out.println(loginDataDto.getGroupid());

		// 言語コード設定を保存しておく
		SaigaiTaskLangUtils.getSiteDBLang(request, loginForm.langCode);

		// 利用するジオコーダを取得
		loginDataDto.setGeocoder(Config.getGeocoder());

		//発生している災害
		loginDataDto.setTrackdataid(0);
		if(StringUtil.isNotEmpty(loginForm.trackdataid) && mode == MODE_TASK) {
			loginDataDto.setTrackdataid(Long.parseLong(loginForm.trackdataid));
		}
		trackDataService.loginTrackData(loginDataDto.getTrackdataid());

		// マスター確認モードログインの場合は、trackdataid=0にログインしても編集可とする
		if (mode==MODE_MASTER) {
			loginDataDto.setEdiable(true);
			loginDataDto.setMaster(true);
		}
		//loginDataDto.isEdiable() = true;
		//loginDataDto.getTrackdataid() = 1;

		//ログイン履歴の登録
		LoginData loginData = new LoginData();
		//loginData.disasterid = loginDataDto.getDisasterid();
		loginData.groupid = loginDataDto.getGroupid();
		// V2.0 のためコメントアウト
		loginData.unitid = loginDataDto.getUnitid();
		if(loginDataDto.getTrackdataid()!=0)
			loginData.trackdataid = loginDataDto.getTrackdataid();
		loginData.logintime = new Timestamp(System.currentTimeMillis());
		loginDataService.insert(loginData);
		loginDataDto.setLogindataid(loginData.id);

		String href = "";
		if (loginForm.returnpath.length() > 0)
			href = loginForm.returnpath;
		loginDataDto.setPageRef(href);

		// メタデータ検索で CKAN を使用
		loginDataDto.setUseCkan(!Config.getString("CKAN_URL", "").isEmpty());

		// 地図表示・非表示
		String formMapVisible = loginForm.mapVisible;
		request.getSession().setAttribute(Constants.SESSIONPARAM_MAPVISIBLE, formMapVisible);

		return href;
	}

    /**
     * 平常時ログインフラグ
     * loginusualのボタンクリックでログインしたリクエストは平常時ログインとみなす
     * @param request
     * @return
     */
    public static boolean isUsualLoginRequest(HttpServletRequest request) {
        boolean isUsualLogin = request.getParameter("loginusual")!=null;
        return isUsualLogin;
    }

	/**
	 * セッション上にDtoがあるか、あった場合その中にuserIDは保持されているか。
	 * @return 上記の条件を両方満たしていればtrue
	 */
	public boolean isLoggedIn() {
		return (loginDataDto != null && (loginDataDto.getGroupInfo() != null || loginDataDto.getUnitInfo() != null));
	}

	/**
	 * セッション上にDtoがあるか、あった場合その中にuserIDは保持されているか。
	 * @return 上記の条件を両方満たしていればtrue
	 */
	public boolean isPageLoggedIn() {
		return (loginDataDto != null && (loginDataDto.getGroupInfo() != null || loginDataDto.getUnitInfo() != null) && loginDataDto.getLocalgovinfoid() != 0);
	}

	/**
	 * セッション上にDtoがあるか、あった場合その中に管理者フラグがあるか。
	 * @return 上記の条件を両方満たしていればtrue
	 */
	public boolean isAdminLoggedIn() {
		return (loginDataDto != null && loginDataDto.getGroupInfo() != null && loginDataDto.isAdmin());
	}

	/**
	 * ログアウト実行
	 * @return トップページ
	 */
	public String logout(long logindataid) {
		try {
			//ログアウトの記録
			LoginData loginData = loginDataService.findById(logindataid);
			loginData.logouttime = new Timestamp(System.currentTimeMillis());
			loginDataService.update(loginData);
//			// eコミマップからログアウト
//			sessionService.logout();

			// eコミから移したサーブレット(PdfServlet)の認証で使った、eコミのログインアカウントを削除
			//session.removeAttribute("authId");

		} catch (Exception e) {

		}
        return "/";
	}
}
