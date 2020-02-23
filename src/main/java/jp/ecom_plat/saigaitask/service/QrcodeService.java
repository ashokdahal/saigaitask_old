package jp.ecom_plat.saigaitask.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@Service
public class QrcodeService {

	private static final Logger LOGGER = Logger.getLogger(QrcodeService.class);

	@SuppressWarnings("deprecation")
	public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {

		Assert.hasText(text);
		Assert.isTrue(width > 0);
		Assert.isTrue(height > 0);
		
		//LOGGER.info("Will generate image  text=[{}], width=[{}], height=[{}]", text, width, height);
		
		Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
		hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8); // デフォルトは "ISO-8859-1"

		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToStream(matrix, MediaType.IMAGE_PNG.getSubtype(), baos, new MatrixToImageConfig());
		return baos.toByteArray();
	}

	@Async
	public ListenableFuture<byte[]> generateQRCodeAsync(String text, int width, int height) throws Exception {
		return new AsyncResult<byte[]>(generateQRCode(text, width, height));
	}
	
	@CacheEvict(cacheNames = "qr-code-cache", allEntries = true)
	public void purgeCache() {
		LOGGER.info("Purging cache");
	}
}
