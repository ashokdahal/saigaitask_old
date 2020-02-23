<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8" import="java.io.*" %>
<%@include file="../../common/jsp_lang.jsp" %>
<%!
String extractInline(InputStream is) {
	StringBuffer sb = new StringBuffer();
	try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line;
		while((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return sb.toString();
}
%>
if(typeof SaigaiTask=="undefined") {
	window.SaigaiTask = {
		contextPath: "<%=request.getContextPath()%>"
	};
}

<%-- フィルタリングJavaScript をここに展開する --%>
<%=extractInline(application.getResourceAsStream("/js/saigaitask.filtering.js"))%>
