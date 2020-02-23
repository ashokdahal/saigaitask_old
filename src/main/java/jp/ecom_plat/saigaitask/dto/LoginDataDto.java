/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.annotation.SessionScope;

import facebook4j.internal.logging.Logger;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.IssuelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import net.sf.json.JSONObject;

/**
 * ログイン情報をセッションで管理します.
 */
@lombok.Getter @lombok.Setter
@org.springframework.stereotype.Component@SessionScope
public class LoginDataDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** ログイン直後フラグ */
	private boolean login = true;
	/** 自治体ID */
	private long localgovinfoid;
	/** 自治体情報 */
	private LocalgovInfo localgovInfo;
	/** 班 */
	private GroupInfo groupInfo;
	/** ログイン班 */
	private long groupid;
	/** ログインユーザ */
	//public String username;
	/** 災害種別 1:災害時 0:平常時*/
	private int disasterid;
	
	private String password;

	/** ログインID */
	private long logindataid;
	/** 記録している災害データ */
	private long trackdataid;
	/** 記録している災害データ */
	private long trackmapinfoid;
	/** アラーム間隔 */
	private int alarmInterval = 120;//2分
	/** アラーム開閉状態 */
	private boolean isAlarmOpen = false;
//	/** サブシステムリスト */
//	public List<SubsystemMaster> subsystemList = new ArrayList<SubsystemMaster>();

	/** 管理権限 */
	private boolean admin = false;
/*
	public boolean getAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	*/
	/** 編集権限 */
	private boolean ediable = true;
	/** マスター確認モード */
	private boolean master = false;

	/** 自治体管理画面 サブシステム管理権限 現在表示中の班ID */
	private long admgoCurrentGroupId;

	/** 自治体管理画面 サブシステム管理権限 現在表示中のサブシステムID */
	private int admgoCurrentSubsystemId;
	/** メニューから戻るページ */
	private String pageRef;
	/** 起動時にポップアップ可能フラグ */
	private boolean directPopup = false;

	/** URL */
	private String preMenuUrl = "";
	/** 発令レイヤ */
	private IssuelayerInfo issueLayerInfo = null;

	/** 平時モード */
	private boolean usual = false;
	/** 班 */
	private UnitInfo unitInfo;
	/** ログイン課 */
	private long unitid;

	/**
	 * セッションに保存したメタデータのJSONオブジェクト
	 * sessionMetadatas JSONObject<menuinfoid, sessionRecords>
	 * sessionRecords JSONObject<metadataid, record>
	 */
	private JSONObject sessionMetadatas = new JSONObject();

	/**
	 * 追加した検索フィルタリング
	 */
	private Map<Long, String> menuConditionMap = new HashMap<Long, String>();
	/**
	 * 検索フィルタリングID
	 */
	private Map<Long, Long> menuFilterIdMap = new HashMap<Long, Long>();
	/**
	 * メニュー検索条件
	 */
	private HashMap<Long ,List<ListEditDto>> menuSearchDatas = new HashMap<Long, List<ListEditDto>>();

	/**
	 * セッションに保存したメタデータのJSONオブジェクト
	 * sessionMetadatas JSONObject<menuinfoid, sessionRecords>
	 * sessionRecords JSONObject<metadataid, record>
	 */
	private JSONObject trainingsessionMetadatas = new JSONObject();

	/** 利用するジオコーダ */
	private String geocoder;

	/**
	 * 地図の表示位置情報
	 * ユーザが最後に表示した位置を保持する.
	 */
	private JSONObject location;

	/**
	 * 時系列マップで利用する表示時刻.
	 * 過去の災害でログイン中は endtime が設定される。
	 * 現在の災害などは現在時刻利用のため null をセットする。
	 */
	private Timestamp time;

	/**
	 * SLDルールによる凡例の表示切替状態のセッション保持
	 */
	private Map<String, String> ruleMap;
	
	/** メタデータ検索に　CKAN を使用する */
	private boolean useCkan = false;

	/**
	 * ログイン名称を取得
	 * @return ログイン名称、未ログイン時は「システム」と返す
	 */
	public String getLoginName() {
		//@javax.annotation.Resource protected SaigaiTaskDBLang lang;
		if (usual && unitInfo!=null) return unitInfo.name;
		else if(groupInfo!=null) return groupInfo.name;
		return /*lang._*/("System<!--2-->");
	}

	/**
	 * @return eコミユーザ名、未ログイン時は「admin」を返す
	 */
	public String getEcomUser() {
		if (usual && unitInfo!=null) return unitInfo.ecomuser;
		else if(groupInfo!=null) return groupInfo.ecomuser;
		//return null;
		// 気象庁XMLからの自動避難発令で TableFeatureServiceを呼び出しているが、
		// タイマーサービスの実行なので未ログイン状態となりエラーとなるので、
		// admin でログインさせることとする。
		return "admin";
	}

	/**
	 * @return ログ用のログインユーザ情報
	 */
	public String logInfo() {
		LoginDataDto loginDataDto = this;
		return "localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid();
	}
	
	
	public LoginDataDto() {
		super();

		Logger logger = Logger.getLogger(getClass());
		logger.info("-- LoginDataDto Created--");
	}
}
