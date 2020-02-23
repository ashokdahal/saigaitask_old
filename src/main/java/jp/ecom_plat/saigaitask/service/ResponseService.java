/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.log.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * HTTPレスポンスを管理するサービスクラスです.
 */
@org.springframework.stereotype.Service
public class ResponseService {

	Logger logger = Logger.getLogger(ResponseService.class);
	@Resource protected HttpServletResponse response;

	public JSONObject toJSONObject(Object object) {

		JsonConfig jsonConfig = new JsonConfig();
		// サイクルになるプロパティはnullにする
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		setDefaultNullProcessor(jsonConfig);

		return JSONObject.fromObject(object, jsonConfig);
	}

	public void setDefaultNullProcessor(JsonConfig config) {
		// デフォルト値をnullに変更するプロセサ
		DefaultValueProcessor defaultNullProcessor = new DefaultValueProcessor() {
			@SuppressWarnings("rawtypes")
			public Object getDefaultValue(Class type) {
				return JSONNull.getInstance();
			}
		};
		// デフォルト値をnullに変更
		config.registerDefaultValueProcessor(Integer.class, defaultNullProcessor);
		config.registerDefaultValueProcessor(Long.class, defaultNullProcessor);
		config.registerDefaultValueProcessor(String.class, defaultNullProcessor);
	}

	/**
	 * JSONをHTTPレスポンスに返します.
	 * @param count 取得数
	 * @param total 総件数
	 * @param limit 最大取得数
	 * @param offset 取得開始位置
	 * @param records 検索レコード
	 */
	public void responseJson(
			Integer count,
			Long total,
			Integer limit,
			Integer offset,
			JSONArray records
	) {
		JSONObject json = new JSONObject();
		try{
			json.put("count", count);
			json.put("total", total);
			json.put("limit", limit);
			json.put("offset", offset);
			json.put("records", records);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		response(json.toString());
	}

	/**
	 * JSONをHTTPレスポンスに返します.
	 * @param json
	 */
	public void response(String data) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			if(data!=null) {
				byte[] byteStr = data.getBytes("UTF-8");
				response.setContentLength(byteStr.length);
				response.getOutputStream().write(byteStr);
			}
			else {
				response.getOutputStream().print(null);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * JSON形式の文字列をレスポンスへ出力する。
	 * @param json JSONオブジェクト
	 * @throws Exception
	 */
	public void responseJson(JSONObject json) throws Exception{
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter sendPoint = response.getWriter();
		sendPoint.println(json.toString());
		sendPoint.flush();
		sendPoint.close();
	}

	/**
	 * JSON形式の文字列をレスポンスへ出力する。
	 * @param json JSONオブジェクト
	 * @throws Exception
	 */
	public void responseJsonText(JSONObject json) throws Exception{
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter sendPoint = response.getWriter();
		sendPoint.println(json.toString());
		sendPoint.flush();
		sendPoint.close();
	}
}
