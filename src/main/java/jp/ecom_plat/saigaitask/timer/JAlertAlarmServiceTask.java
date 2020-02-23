package jp.ecom_plat.saigaitask.timer;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.service.JAlertAlarmService;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;

/**
 * JAlertAlarmServiceのスケジュールタスク
 */
@Component
public class JAlertAlarmServiceTask {

	@Resource JAlertAlarmService jAlertAlarmService;
	@Resource MeteoricEarthQuakeService meteoricEarthQuakeService;
	
	@Value("${saigaitask.timer.jAlertAlarmService.enable}") 
	private boolean enable;
	
    boolean init = false;
    
    @Scheduled(cron = "${saigaitask.timer.jAlertAlarmService.cron}")
    public void exec() {
    	if(enable==false) return;
    	
    	if(init==false) {
        	// サービスを初期化
        	boolean start = false;
        	jAlertAlarmService.init(start);
    		init = true;
    	}

    	// 気象庁XMLを受信
    	jAlertAlarmService.checkAlarm();
    	
		// 震度レイヤを一定期間毎に地物を削除する関数を呼び出す
    	meteoricEarthQuakeService.earthquakeLayerDeleteCheck();
    }
}
