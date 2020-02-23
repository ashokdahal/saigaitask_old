/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousesearchInfo;
import jp.ecom_plat.saigaitask.service.db.ClearinghousesearchInfoService;
import jp.ecom_plat.saigaitask.util.CKANUtil;
import jp.ecom_plat.saigaitask.util.CSWUtil;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * クリアリングハウスのAPIを提供するサービスクラスです.
 */
@org.springframework.stereotype.Service
public class MetadataService extends BaseService {

	Logger logger = Logger.getLogger(getClass());

	/** ログイン情報 */
	@Resource public LoginDataDto loginDataDto;

	@Resource
	ClearinghousesearchInfoService clearinghousesearchInfoService;

	/**
	 * クリアリングハウスから所定の時間内に登録・更新されたメタデータを検索し、
	 * @param isTraining 訓練モードフラグ
	 * @return JSON文字列
	 */
	public String getLatestMetadata(boolean isTraining){
		// 設定で変更できる条件など
		String bbox = null;		// 検索対象の地理範囲
		int span = 60;			// 最新と判断する間隔（分）
		int maxrec = 50;		// 最大取得件数
		int orderby = 13;		// ソート順序
        try{
			bbox = Config.getString("LATESTMETADATA_BBOX");
			span = Integer.parseInt(Config.getString("LATESTMETADATA_SPAN","60")); // デフォルト 60分
			maxrec = Integer.parseInt(Config.getString("LATESTMETADATA_MAXREC","50")); // デフォルト 50件
			orderby = Integer.parseInt(Config.getString("LATESTMETADATA_ORDERBY","13")); // デフォルト 更新日時降順
        }catch(Exception e){
			logger.warn(e,e);
        }

		// 条件を追加
		HashMap<String,String> condition = new HashMap<String,String>();

		//DBからパラメータ、検索範囲を取得する。
        ClearinghousesearchInfo search = clearinghousesearchInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
        if (search != null) {
        	if (StringUtil.isNotEmpty(search.area))
        		bbox = search.area;
        	if (StringUtil.isNotEmpty(search.query)) {
        		String[] params = search.query.split("&");
        	for (String param : params) {
        			int idx = param.indexOf('=');
        			if (idx >= 0)
        				condition.put(param.substring(0, idx), param.substring(idx+1));
        		}
        	}
        }

		// 「地図データのアクセスに関する情報」にて、設定される種類を指定する
		// WMS,WFS,XYZ
		condition.put("APPLICATIONPROFILE","WMS,WFS,XYZ");
		// 予定メタデータを含まない
		condition.put("INCLUDEPLANNED","false");
		// 地理範囲
		if(bbox != null)
			condition.put("BBOX",bbox);
		// 時間範囲（過去xx分以内）
		GregorianCalendar to = new GregorianCalendar();
		GregorianCalendar from = (GregorianCalendar)to.clone();
		// from.add(GregorianCalendar.HOUR_OF_DAY,-span);
		from.add(GregorianCalendar.MINUTE,-span);
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//condition.put("UPDATETERM",sdf.format(from.getTime())+","+sdf.format(to.getTime()));
		condition.put("UPDATETERM",TimeUtil.iso8601Formatter.format(from.getTime())+","+TimeUtil.iso8601Formatter.format(to.getTime()));
		// リクエスト送信
		// 災害モードと訓練モードでクリアリングハウスの登録先を変更する
		String json;
		if (loginDataDto.isUseCkan()) {
			String ckanurl = isTraining ? Config.getString("CKAN_URL_TRAINING") : Config.getString("CKAN_URL");
			json = CKANUtil.getRecords(ckanurl, condition,1,maxrec,orderby);
		}
		else {
			String cswurl = isTraining ? Config.getString("CSWURL_TRAINING") : Config.getString("CSWURL");
			String cswuser = isTraining ? Config.getString("CSWUSER_TRAINING") : Config.getString("CSWUSER");
			String cswpasswd = isTraining ? Config.getString("CSWPASSWD_TRAINING") : Config.getString("CSWPASSWD");
			String xml = CSWUtil.getRecords(cswurl, cswuser, cswpasswd, condition,1,maxrec,orderby);
			// JSON変換
			json = CSWUtil.GetRecordsResponseToJSON(xml);
		}
		return json;
	}
}
