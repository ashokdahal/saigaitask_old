/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.seasar.framework.exception.IORuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HttpUtil {

	private static Logger logger = Logger.getLogger(HttpUtil.class);

	/**
	 * HTTPリクエストをプロキシする。(認証情報無し)
	 * @param url
	 * @param method
	 * @param contentType
	 * @param postData
	 * @param useCaches
	 * @return
	 * @throws Exception
	 */
	public static HttpURLConnection httpProxy(
			String url,
			String method,
			String contentType,
			String postData,
			boolean useCaches ) throws Exception {
		return httpProxy(url, method, contentType, postData, useCaches, "");
	}

	/**
	 * HTTPリクエストをプロキシする。(認証情報有)
	 * @param url
	 * @param method
	 * @param contentType
	 * @param postData
	 * @param useCaches
	 * @param authorization : "Basic " + Base64したusername+":"+userpass
	 * @return
	 * @throws Exception
	 */
	public static HttpURLConnection httpProxy(
			String url,
			String method,
			String contentType,
			String postData,
			boolean useCaches,
			String authorization) throws Exception {
		System.out.println("httpProxy: "+url);

		HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();

		// HTTPリクエスト 接続準備
		http.setRequestMethod(method);
//		http.setConnectTimeout(1000); // ms
		if(postData!=null&&0<postData.length()){
			http.setDoOutput(true);
		}
		if(contentType!=null) http.setRequestProperty("content-type", contentType);
		http.setUseCaches(useCaches);
		// 認証情報があれば付与する
		if(!authorization.equals("")){
			http.setRequestProperty("Authorization", authorization);
		}

		debug("----------------------------------------------------------");
		debug("Request Properties: ");
		debug(http.getRequestProperties());
		debug("Cookie: "+http.getRequestProperty("Cookie"));
		debug("----------------------------------------------------------");

		// HTTP 接続
		http.connect();

		// POST
		if(postData!=null&&0<postData.length()){
			debug("post data: "+postData);
			IOUtils.write(postData, http.getOutputStream());
		}

		logger.info("response code: "+http.getResponseCode());
		return http;
	}

	/**
	 * デバッグ出力用関数
	 * @param obj
	 */
	@SuppressWarnings("unchecked")
	public static void debug(Object obj){
		Level priority = Level.DEBUG;
		if( obj instanceof Map){
			for( Map.Entry<Object,Object> e : ((Map<Object,Object>)obj).entrySet() ) {
				Object key = e.getKey();
				Object value = e.getValue();
				String k = key!=null?key.toString():null;
				String v = value!=null?value.toString():null;
				logger.log(priority, k+"="+v);
			}
		}
		else if( obj instanceof InputStream ) {
			InputStream is = (InputStream) obj;
			java.io.BufferedReader reader=null;
			try {
				reader = new java.io.BufferedReader(new java.io.InputStreamReader(is,"UTF-8"));
				String line;
				while( (line=reader.readLine())!=null ){
					logger.log(priority, line);
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(reader!=null) {
					try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
				}
			}
		}
		else if( obj instanceof String) {
			logger.log(priority, obj);
		}
		else {
			logger.log(priority, "[unknown type] "+obj);
		}
	}

	/**
	 * 指定されたURLからファイルを取得する
	 * @param downloadUrl
	 * @param saveFile
	 * @return
	 */
	public static boolean getFile(URL downloadUrl, File saveFile) throws FileNotFoundException,IOException{
		boolean returnValue = true;

		HttpURLConnection urlConnection = null;
		DataInputStream dis = null;
		try{
			FileOutputStream fos = new FileOutputStream(saveFile);
			urlConnection = (HttpURLConnection)downloadUrl.openConnection();
			urlConnection.connect();

			int num;
			byte buf[] = new byte[4096];

			dis = new DataInputStream(urlConnection.getInputStream());
			while((num=dis.read(buf))!=-1) {
				fos.write(buf,0,num);
			}
		}finally{
			if(urlConnection != null){
				urlConnection.disconnect();
			}
			if(dis != null){
				dis.close();
			}
		}

		return returnValue;
	}

	// Springでは使えなくなったのでコメントアウト
    /* *
     * 指定されたストリームから読み込んで、ダウンロードレスポンスを出力します。
     * 成否にかかわらずストリームは閉じます。
     *
     * ストリームから読み込めないか、ユーザが途中でダウンロードを中断した場合に、IORuntimeExceptionが発生します。
     *
     * {@link ResponseUtil#download(String, InputStream)} にcontentTypeを指定できるようにした。
     *
     * @param fileName レスポンスとして返されるファイル名
     * @param in ダウンロードさせたいデータ
     * @param contentType contentType
     * /
    public static void download(String fileName, InputStream in, String contentType) {
        try {
            if(StringUtils.isEmpty(contentType)) contentType = "application/octet-stream";
            HttpServletResponse response = ResponseUtil.getResponse();
            response.setContentType(contentType);
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + fileName + "\"");
            OutputStream out = response.getOutputStream();
            try {
                InputStreamUtil.copy(in, out);
                OutputStreamUtil.flush(out);
            } finally {
                    OutputStreamUtil.close(out);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            InputStreamUtil.close(in);
        }
    }*/
    
    public static void download(HttpServletResponse response, File file) {
        try {
            byte[] byteArray = FileUtils.readFileToByteArray(file);
            response.setContentType("application/octet-stream");
            response.setContentLength(byteArray.length);
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + file.getName() + "\"");
            FileCopyUtils.copy(byteArray, response.getOutputStream());
        } catch (IOException cause) {
            throw new IORuntimeException(cause);
        }
    }

    
    public static void throwUnauthorizedException() {
		throw new UnauthorizedException();
    }

    /**
     * 401 Unauthorized
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {
		private static final long serialVersionUID = 1L;
    }

    
    public static void throwBadRequestExceptionIfIsNull(Object... args) {
    	for(Object arg : args) {
    		if(arg==null) {
    			throw new BadRequestException();
    		}
    	}
    }
    
    /**
     * 403 BadRequest
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class BadRequestException extends RuntimeException {
		private static final long serialVersionUID = 1L;
    }

    public static void throwMethodNotAllowdException() {
		throw new MethodNotAllowdException();
    }

    
    /**
     * 405 MethodNotAllowd
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    static class MethodNotAllowdException extends RuntimeException {
		private static final long serialVersionUID = 1L;
    }

}
