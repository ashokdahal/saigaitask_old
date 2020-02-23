/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.ApiDto;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * APIアクションの抽象クラス.
 * 共通処理はここで定義する.
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractApiAction extends AbstractAction {

	public static String REQUEST_HEADER_X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";

	@Resource protected ApiDto apiDto;

	private static RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


	public boolean isGetMethod() {
		return "GET".equals(apiDto.getMethod());
	}

	public boolean isPostMethod() {
		return "POST".equals(apiDto.getMethod());
	}

	public boolean isPutMethod() {
		return "PUT".equals(apiDto.getMethod());
	}

	public boolean isPatchMethod() {
		return "PATCH".equals(apiDto.getMethod());
	}

	public boolean isDeleteMethod() {
		return "DELETE".equals(apiDto.getMethod());
	}


	/**
	 * @param msg エラーメッセージ
	 * @param status HTTPステータスコード
	 * @return 戻り先URL
	 */
	public String errorResponseJSONObject(String msg, int status) {
		JSONObject json = new JSONObject();
		json.put("error", msg);
		return responseJSONObject(json, status);
	}

	/**
	 * @param json レスポンスとして返すJSONObject
	 * @return 戻り先URL
	 */
	public String responseJSONObject(JSON json) {
		return responseJSONObject(json, 0);
	}

	/**
	 * @param json レスポンスとして返すJSONObject
	 * @param status HTTPステータスコード
	 * @return 戻り先URL
	 */
	public String responseJSONObject(JSON json, int status) {
		if(0<status) response.setStatus(status);
		try {
			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print( json.toString() );
			out.flush();

			return null;
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * JAX-RS のレスポンスクラス(javax.ws.rs.core.Response) と同等のインタフェースを持つレスポンスクラス.
	 * eコミマップの OAuth2認証処理を移植したため、ソースをあわせるために定義する.
	 */
	public static class Response {
		public HttpServletRequest request;
		/** HTTPステータスコード */
		public int status;
		/** レスポンスBody */
		public String body;
		/** レスポンスヘッダ */
		public Map<String, Object> headers = new LinkedHashMap<String, Object>();
		/** 戻り先URL */
		public URI location;

		/**
		 * @param status HTTPステータスコード
		 * @return {@link ResponseBuilder}
		 */
		public static ResponseBuilder status(int status) {
			ResponseBuilder builder = new ResponseBuilder();
			builder.response = new Response();
			builder.response.status = status;
			return builder;
		}

		/**
		 * @param location 戻り先URL
		 * @return {@link ResponseBuilder}
		 */
		public static ResponseBuilder temporaryRedirect(URI location, HttpServletRequest request) {
			ResponseBuilder builder = new ResponseBuilder();
			builder.response = new Response();
			builder.response.status = HttpServletResponse.SC_TEMPORARY_REDIRECT;
			return builder.location(location, request);
		}

		static public Response sendJSONError(String msg) throws IOException
		{
			return sendJSONError(msg, 200);
		}

		static public Response sendJSONError(String msg, int errorCode)
		{
			try {
				JSONObject json = new JSONObject();
				json.put("error", msg);
				if (errorCode > 0) {
					json.put("code", errorCode);
				}
				return Response.status(errorCode).entity(json.toString()).build();
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}

		/**
		 * SAStrutsのアクションクラスのレスポンス処理を実行する.
		 * @param response {@link HttpServletResponse}
		 * @return 戻り先URL
		 */
		public String execute(HttpServletResponse response) {

			// set status code
			if(0<status) {
				response.setStatus(status);
			}

			// set header
			for(Map.Entry<String, Object> entry : headers.entrySet()) {
				String key = entry.getKey();
				Object val = entry.getValue();
				if(val!=null) {
					response.setHeader(key, val.toString());
				}
			}

			// 出力の準備
			if(body!=null) {
				response.setStatus(status);
				try {

					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					PrintWriter out = response.getWriter();
					out.print( body );
				}catch(Exception e) {
					throw new ServiceException(e);
				}
			}

			// return path
			String path = null;
			if(location!=null) {
				path = location.toString();
				// HTTP 300 Redirect
				if(300<=status && status<400) {
					try {
						redirectStrategy.sendRedirect(request, response, path);
					} catch (BeansException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}


			return path;
		}
	}

	/**
	 * JAX-RS のレスポンスクラス(javax.ws.rs.core.ResponseBuilder) と同等のインタフェースを持つレスポンスクラス.
	 * eコミマップの OAuth2認証処理を移植したため、ソースをあわせるために定義する.
	 */
	public static class ResponseBuilder {

		/** レスポンス */
		public Response response;

		/**
		 * @param body レスポンスBody
		 * @return ResponseBuilder
		 */
		public ResponseBuilder entity(String body) {
			response.body = body;
			return this;
		}

		/**
		 * @param tag ETag
		 * @return ResponseBuilder
		 */
		public ResponseBuilder tag(String tag) {
			return header("ETag", tag);
		}

		/**
		 * @param name Response Header Name
		 * @param value Response Header Value
		 * @return ResponseBuilder
		 */
		public ResponseBuilder header(String name, Object value) {
			response.headers.put(name, value);
			return this;
		}

		/**
		 * @param location 戻り先URL
		 * @return ResponseBuilder
		 */
		public ResponseBuilder location(URI location, HttpServletRequest request) {
			response.location = location;
			response.request = request;
			return this;
		}

		/**
		 * @return Response
		 */
		public Response build() {
			return this.response;
		}
	}

	/**
	 * エンティティのカラムフィールドのみをJSONObjectに変換する
	 * @param entity エンティティ
	 * @return エンティティのJSONObject
	 */
	public static JSONObject toJSONObject(Object entity) {
		if(entity instanceof String) {
			if(((String) entity).length()==0) return new JSONObject();
		}
		JsonConfig config = new JsonConfig();
		// publicフィールドは対象としない(getter/setterが用意されているはずなので）
		config.setIgnorePublicFields(true);
		// Columnフィールド以外は除く
		List<String> excludes = new ArrayList<String>();
		if(entity!=null) {
			Class<?> entityClass = entity.getClass();
			for(Field field : entityClass.getFields()) {
				if(field.getAnnotation(Column.class)==null) {
					excludes.add(field.getName());
				}
			}
		}
		config.setExcludes(excludes.toArray(new String[excludes.size()]));
		JSONObject jsonObject = JSONObject.fromObject(entity, config);
		return jsonObject;
	}
}
