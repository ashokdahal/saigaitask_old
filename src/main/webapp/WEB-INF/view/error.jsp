<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page import="org.apache.log4j.Logger" %>
<%@page import="jp.ecom_plat.saigaitask.action.AbstractAction" %>
<%@page import="jp.ecom_plat.saigaitask.dto.LoginDataDto" %>
<%@page import="jp.ecom_plat.saigaitask.util.StringUtil" %>
<%@include file="common/lang_resource.jsp" %>
<%
	Logger logger = Logger.getLogger(AbstractAction.class);
	String message = lang.__("Error page is displayed.");
	Exception e = null;
	Object obj;

	// URL
	message += "\nURL: "+request.getAttribute("javax.servlet.forward.request_uri");
	message += "\nQuery: "+request.getAttribute("javax.servlet.forward.query_string");

	// Exception
	obj = request.getAttribute("javax.servlet.error.exception");
	if(obj!=null && obj instanceof Exception) {
		e = (Exception) obj;

		// JVMは、同じような箇所で組込み例外が繰り返し throw されると、
		// メソッドを再コンパイルして最適化することがある。
		// すると、スタックトレースが提供されない 事前に用意された例外 が代わりに投げられてしまう。
		// 最適化を無効にするには JVM起動オプションに下記オプションを付ける
		//  -XX:-OmitStackTraceInFastThrow

		// StackTrace
		message += "\nStackTrace:";
		if(e.getStackTrace()!=null) {
			for(StackTraceElement stackTraceElement : e.getStackTrace()) {
				message += "\n  "+stackTraceElement.toString();
			}
		}

		// Cause
		message += "\nCause: ";
		if(e.getCause()!=null) {
			message += e.getCause().toString();
		}
	}

	// セッション情報
	obj = session.getAttribute("loginDataDto");
	if(obj!=null && obj instanceof LoginDataDto) {
		LoginDataDto loginDataDto = (LoginDataDto) obj;
		message += "\nlocalgovinfoid: "+loginDataDto.getLocalgovinfoid();
	}

	logger.error(message, e);
	if(e!=null) {
		System.out.println(message);
		e.printStackTrace();
	}
%>
<%--
<tiles:insertTemplate template="/WEB-INF/view/common/layout.jsp" flush="true">
	<tiles:putAttribute name="content" type="string">
 --%>

<script type="text/javascript">
function backPage(){
	var req_url = "${f:h(requestScope["javax.servlet.forward.servlet_path"])}";
	var contextPath = "<%=request.getContextPath()%>";
	var url = "${param.menuid}";
	if(url.length!=0 && url != null && url!="0"){
		url = 	contextPath+'/page/?menuid='+url;
	}else if(req_url.indexOf(/admin/) == -1) {
		url = 	contextPath+'/page/';
	} else {
		url = 	contextPath+'/admin/';
	}
	top.location.href=url;
}
</script>

		<br>
		<%=lang.__("An error occurred. Check following contents.")%>
		<div><font color="red"><ul><form:errors path="*" element="li"/></ul></font></div>
		<c:if test="${requestScope['javax.servlet.error.status_code']!=null}">
		<ul>
			<li><%=lang.__("Status code")%>：${requestScope["javax.servlet.error.status_code"] }</li>
			<li><%=lang.__("Message")%>：${f:h(requestScope["javax.servlet.error.exception"]) }</li>
			<li><%=lang.__("Message")%>：${f:h(requestScope["javax.servlet.error.exception"].message) }</li>
			<!--<li>例外の発生したクラス：${f:h(requestScope["javax.servlet.error.exception_type"]) }</li> -->
			<li>URI：${requestScope["javax.servlet.error.request_uri"] }</li>
			<li><%=lang.__("Servlet name")%>：${requestScope["javax.servlet.error.servlet_name"] }</li>
		</ul>
		</c:if>

		<input type="button" value="<%=lang.__("Return")%>" onclick="backPage();"/>

<%--
	</tiles:putAttribute>
</tiles:insertTemplate>
 --%>
