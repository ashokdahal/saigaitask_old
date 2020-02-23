/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.util.FileInputStreamUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.ecommap.KmlService;

/**
 * KMLレイヤのサービスクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class KmlAction extends AbstractAction {

	@Resource protected KmlService kmlService;

	/**
	 * 指定したKMLファイルのURLをプロキシでアクセスする.
	 * 取得したKMLファイルは一定時間キャッシュする.
	 * 
	 * @param url KML URL
	 * @param reloadSec キャッシュ保持期間
	 * @param reload キャッシュ使用フラグ
	 * 
	 * @return KMLファイル
	 */
	@RequestMapping(value="/page/map/kml/", produces="application/vnd.google-earth.kml+xml")
	@ResponseBody
	public HttpEntity index(@RequestParam String url, @RequestParam(required=false, defaultValue="1800") Integer reloadSec, @RequestParam(required=false, defaultValue="false") Boolean reload) {

		// URLにアクセスしてキャッシュ後、KMLファイルを取得する
		File kmlFile = kmlService.getKmlFile(url, reloadSec, reload);

		FileInputStream in = null;
		try {
			// InputStream 読み込み
			in = FileInputStreamUtil.create(kmlFile);
			long length = kmlFile.length();
			if(Integer.MAX_VALUE < length) {
				throw new ServiceException(lang.__("KML file size is too large."));
			}

			// ダウンロード用ファイル名を決定
			String fileName = "doc.kml"; // ファイル名は KMZの場合のメインのKMLファイル名 doc.xml とする
			// .kml で終わる場合は、URLのファイル名を使用する
			if(url.endsWith(".kml")) {
				String str = url;
				// リクエストパラメータがあれば除く
				if(-1<str.indexOf('?')) {
					str = str.substring(0, str.indexOf('?'));
				}
				// 最後のパス名をファイル名とする
				int idx = str.lastIndexOf('/');
				if(-1<idx) {
					fileName = str.substring(idx+1);
				}
			}

			// レスポンス
			//ResponseUtil.download(fileName, in, (int) length);
		    HttpHeaders headers = new HttpHeaders();
		    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		    return new HttpEntity<byte[]>(IOUtils.toByteArray(in), headers);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		

		return null;
	}
}
