/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.ecommap;

import java.io.File;

import jp.ecom_plat.map.util.KMLCache;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.util.EcommapServletContext;

import org.apache.log4j.Logger;

/**
 * KML関係のサービスクラスです.
 */
@org.springframework.stereotype.Service
public class KmlService extends BaseService {

	Logger logger = Logger.getLogger(KmlService.class);

	String kmlPath;

	/**
	 * eコミマップのServletContext を初期化
	 */
	public KmlService() {
		EcommapServletContext ecommapServletContext = new EcommapServletContext();
		this.kmlPath = KMLCache.getCacheRealPath(ecommapServletContext);
	}

	/**
	 * KMLファイルを取得します.
	 * ダウンロードした KMLファイル はeコミ側でキャッシュされます.
	 * @param kmlUrlString KMLファイルのURL
	 * @param reloadSec 指定した秒数以上更新されていなければサーバからKMLファイルをダウンロードしなおします。
	 *        時間内であれば、キャッシュファイルを返します.
	 * @return KMLファイル
	 */
	public File getKmlFile(String kmlUrlString, int reloadSec) {
		File kmlFile = this.getKmlFile(kmlUrlString, reloadSec, false);
		return kmlFile;
	}
	/**
	 * KMLファイルを取得します.
	 * ダウンロードした KMLファイル はeコミ側でキャッシュされます.
	 * @param kmlUrlString KMLファイルのURL
	 * @param reloadSec 指定した秒数以上更新されていなければサーバからKMLファイルをダウンロードしなおします。
	 *        時間内であれば、キャッシュファイルを返します.
	 * @param reload trueを指定した場合、キャッシュファイルを削除してKMLファイルをダウンロードしなおします。
	 * @return KMLファイル
	 */
	public File getKmlFile(String kmlUrlString, int reloadSec, boolean reload) {
		try {
			File kmlFile = KMLCache.getKMLFile(this.kmlPath, kmlUrlString, reloadSec, reload);
			logger.debug("KmlService.getKmlFile: kmlUrlString=\""+kmlUrlString+"\", reloadSec="+reloadSec+", reload="+reload);
			return kmlFile;
		} catch (Exception e) { throw new ServiceException(lang.__("An error occurred in the getting KML file."), e); }
	}
}
