package jp.ecom_plat.saigaitask.action.mob;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.QrcodeService;

// @see https://github.com/raonigabriel/spring-qrcode-example/blob/master/src/main/java/com/github/spring/example/SpringExampleApp.java
@Controller
@EnableAsync
@EnableCaching
@EnableScheduling
public class QrcodeAction {

	public static final String QRCODE_ENDPOINT = "/mob/qrcode";
	public static final long THIRTY_MINUTES = 1800000; 
	
	@Resource
	protected QrcodeService qrcodeService;

	@GetMapping(value = QRCODE_ENDPOINT, produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getQRCode(@RequestParam(value = "text", required = true) String text) {
		try {
			return ResponseEntity.ok().cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
					.body(qrcodeService.generateQRCodeAsync(text, 256, 256).get());
		} catch (Exception ex) {
			throw new ServiceException("Error while generating QR code image.", ex);
		}
	}
	
	@Scheduled(fixedRate = THIRTY_MINUTES)
	//@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = QRCODE_ENDPOINT)
	public void deleteAllCachedImages() {
		qrcodeService.purgeCache();
	}	
}
