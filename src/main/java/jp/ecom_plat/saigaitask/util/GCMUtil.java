/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.ecom_plat.saigaitask.action.ServiceException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.PayloadBuilder;

public class GCMUtil {


	public static void sendGCMforAndroid(JSONArray regids, String msg, String API_KEY)
	{
		Logger logger = Logger.getLogger(GCMUtil.class);

        logger.debug("GCMUtil.sendGCMforAndroid( regids="+regids.toString()+", msg="+msg+", API_KEY="+API_KEY);
        
        /**
		 * for iOS ? //TODO:試してない
			https://gcm-http.googleapis.com/gcm/send
			Content-Type:application/json
			Authorization:key=AIzaSyZ-1u...0GBYzPu7Udno5aA
			
			{
			  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
			  "content_available" : "true"
			  "notification" : {
			    "body" : "great match!",
			    "title" : "Portugal vs. Denmark"
			    }
			}
		 */
		try {
			// Prepare JSON containing the GCM message content. What to send and where to send.
			JSONObject jGcmData = new JSONObject();
			JSONObject jData = new JSONObject();
			//if (regids.length() == 1)
			//	jGcmData.put("registration_id", regids.toString());
			//else//TODO:複数台は試してない。
			// 文字列ではなくJSON配列で端末のIDを指定する
			jGcmData.put("registration_ids", regids);
			jData.put("message", msg);
			//jData.put("detail", "詳細？？？？？？？？？？？？？？");
            // Where to send GCM message.
            /*if (args.length > 1 && args[1] != null) {
                jGcmData.put("to", args[1].trim());
            } else {*/
			    // トピックto指定を一旦やめて、registration_ids指定に変更
                //jGcmData.put("to", "/topics/global");
            //}
            // What to send in GCM message.
            jGcmData.put("data", jData);
            
            logger.debug("GCM data: "+jGcmData.toString());

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            // Read GCM response.
            InputStream inputStream;
            int responseCode = conn.getResponseCode();
            if(responseCode / 100 == 4 || responseCode / 100 == 5){
                inputStream = conn.getErrorStream();
            }else{
                inputStream = conn.getInputStream();
            }
            String resp = IOUtils.toString(inputStream);
            logger.info("GCMUtil(Android) push response: "+resp);
            //System.out.println("Check your device/emulator for notification or logcat for " +
            //        "confirmation of the receipt of the GCM message.");
            inputStream.close();
        } catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
        }
	}
	
	public static void sendAPNSforiOS(JSONArray regids, String msg, URL keyurl, String password)
	{
		// Appleのサーバーと通信するための証明書ファイルのPathを取得する
		String certFilePath = keyurl.getFile();
		// 証明書のパスワードを取得する
		String certPassword = password;
 
		ApnsServiceBuilder serviceBuilder =
			APNS.newService().withCert(certFilePath, certPassword)
            // 注:↓を指定しないと内部でThreadを生成しようとしてコケる
                .withNoErrorDetection();
 
        // 接続先としてSandbox(開発用環境)を指定する場合
        serviceBuilder.withSandboxDestination();
        // 接続先として本番用環境を指定する場合
        // serviceBuilder.withProductionDestination();
 
        ApnsService apnsService = null;
        try {
            apnsService = serviceBuilder.build();
            PayloadBuilder payloadBuilder = APNS.newPayload();
 
            // alert文字列
            payloadBuilder.alertBody(msg);
            // 音の指定
            // payloadBuilder.sound("hoge.wav");
            // バッジの指定(アイコン右上の赤丸内の数字)
            // payloadBuilder.badge(4);
 
            for (int i = 0; i < regids.length(); i++) {
            	String token = regids.getString(i);
                // Push通知の送信(複数のデバイストークンをまとめて送信も可能)
                apnsService.push(token, payloadBuilder.build());
            }
        } catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
        } finally {
            // Connectionを解放
            if (apnsService != null) {
                apnsService.stop();
            }
        }
	}
	
    //config //TODO:試してない
	//http://stackoverflow.com/questions/31109514/making-gcm-work-for-ios-device-in-the-background
    /*static String apiKey = ""; // Put here your API key
    static String GCM_Token = ""; // put the GCM Token you want to send to here
    static String notification = "{\"sound\":\"default\",\"badge\":\"2\",\"title\":\"default\",\"body\":\"Test Push!\"}"; // put the message you want to send here
    static String messageToSend = "{\"to\":\"" + GCM_Token + "\",\"notification\":" + notification + "}"; // Construct the message.
	protected void sendGCMforIOS() 
	{

        try {

            // URL
            URL url = new URL("https://android.googleapis.com/gcm/send");

            System.out.println(messageToSend);
            // Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Specify POST method
            conn.setRequestMethod("POST");

            //Set the headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);

            //Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            byte[] data = messageToSend.getBytes("UTF-8");
            wr.write(data);

            //Send the request and close
            wr.flush();
            wr.close();

            //Get the response
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //Print result
            System.out.println(response.toString()); //this is a good place to check for errors using the codes in http://androidcommunitydocs.com/reference/com/google/android/gcm/server/Constants.html

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}*/
}
