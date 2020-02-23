package jp.ecom_plat.saigaitask.timer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.service.RiskJudgeService;

/**
 * RiskJudgeServiceのスケジュールタスク
 */
@Component
public class RiskJudgeServiceTask {

	Logger logger = Logger.getLogger(RiskJudgeServiceTask.class);
	
	@Resource RiskJudgeService riskJudgeService;

	@Value("${saigaitask.timer.riskJudgeService.enable}") 
	private boolean enable;

    boolean init = false;
    
    @Scheduled(cron = "${saigaitask.timer.riskJudgeService.cron}")
    public void exec() {
    	if(enable==false) return;

    	if(init==false) {
        	// サービスを初期化
        	boolean start = false;
        	riskJudgeService.init(start);
    		init = true;
    	}

    	riskJudgeService.checkRisk();
    }
}
