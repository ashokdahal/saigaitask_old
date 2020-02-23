/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.util.Config;

@org.springframework.stereotype.Service
public class GeocoderService extends BaseService {

	Logger logger = Logger.getLogger(getClass());

	/** ログイン情報 */
	@Resource public LoginDataDto loginDataDto;

	/**
	 * 住所から逆ジオする
	 * @param address
	 * @return 逆ジオした結果
	 */
	public String geocode(String address){
		String geocoderType = Config.getGeocoder();
		boolean isGoogleGeocoder = geocoderType!=null && geocoderType.toUpperCase().equals("GOOGLE");
		if(isGoogleGeocoder) {
			try {
				Geocoder geocoder = new Geocoder();
				GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("ja").getGeocoderRequest();
				GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
				List<GeocoderResult> reslist = geocoderResponse.getResults();
				if (reslist.size() > 0) {
					GeocoderResult res = reslist.get(0);
					String lat = res.getGeometry().getLocation().getLat().toString();
					String lng = res.getGeometry().getLocation().getLng().toString();
					String center = lng + "," + lat;
					return center;
				}
			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		}
		return "";
	}
}
