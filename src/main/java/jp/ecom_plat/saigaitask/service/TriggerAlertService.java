package jp.ecom_plat.saigaitask.service;

import javax.annotation.Resource;

/**
 * 通知トリガーサービス
 */
@org.springframework.stereotype.Service
public class TriggerAlertService extends BaseService {

	@Resource protected StationService stationService;

	/**
	 * 通知トリガーを実行する。
	 * @param tablemasterinfoid
	 * @param attrid
	 * @param value
	 */
	public void trigger(Long tablemasterinfoid, String attrid, String value) {
		// 体制アラート
		stationService.triggerAlertmessage(tablemasterinfoid, attrid, value);
	}
}
