<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<tiles:insertTemplate template="/WEB-INF/view/common/layout.jsp" flush="true">
	<tiles:putAttribute name="content" type="string">
	<!-- headでインクルードだと動かない -->
	<script type="text/javascript" src="${f:url('/js/lightbox2-master/dist/js/lightbox.min.js')}"></script>
	<jsp:include page="list.jsp" />
</tiles:putAttribute>
</tiles:insertTemplate>

