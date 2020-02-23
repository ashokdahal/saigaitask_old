package jp.ecom_plat.saigaitask.timer;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.service.TelemeterService;

/**
 * TelemeterServiceのスケジュールタスク
 */
@Component
public class TelemeterServiceTask {

	Logger logger = Logger.getLogger(TelemeterServiceTask.class);
	
	@Resource TelemeterService telemeterService;
	
	@Value("${saigaitask.timer.telemeterService.enable}") 
	private boolean enable;

    boolean init = false;
    
    @Scheduled(cron = "${saigaitask.timer.telemeterService.cron}")
    public void exec() {
    	if(enable==false) return;

    	if(init==false) {
        	// サービスを初期化
        	boolean start = false;
        	telemeterService.init(start);
    		init = true;
    	}

		logger.info("telemeter check start");
    	try {
			telemeterService.checkData();
		} catch (IOException e) {
			logger.error("telemeter check fail", e);
		}
		logger.info("telemeter check end");
    }
}
