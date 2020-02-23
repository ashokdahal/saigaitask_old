package jp.ecom_plat.saigaitask.timer;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.service.MeteoricAlarmService;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;

/**
 * MeteoricAlarmServiceのスケジュールタスク
 */
@Component
public class MeteoricAlarmServiceTask {

	@Resource MeteoricAlarmService meteoricAlarmService;
	@Resource MeteoricEarthQuakeService meteoricEarthQuakeService;
	
	@Value("${saigaitask.timer.meteoricAlarmService.enable}") 
	private boolean enable;

	boolean init = false;
    
    @Scheduled(cron = "${saigaitask.timer.meteoricAlarmService.cron}")
    public void exec() {
    	if(enable==false) return;

    	if(init==false) {
        	// サービスを初期化
        	boolean start = false;
        	meteoricAlarmService.init(start);
    		init = true;
    	}

    	// 気象庁XMLを受信
    	meteoricAlarmService.checkAlarm();
    	
		// 震度レイヤを一定期間毎に地物を削除する関数を呼び出す
    	meteoricEarthQuakeService.earthquakeLayerDeleteCheck();
    }
}
