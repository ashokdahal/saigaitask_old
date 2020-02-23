<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<script type="text/javascript">
$(function() {
	// メニューの初期化
	$("#menulist").menu();
	$("#menulist li a").on("click", function(e){
		var anchor = $(this);

			$("#menulist li a").css("background-Color", "transparent");
			anchor.css("background-Color", "yellow");
			SaigaiTask.disconnect.loadContent(anchor.get(0).href+"content");
		// cancel anchor href
		return false;
	});

<c:if test="${param.code!=null}">
	// OAuth サーバよりリダイレクトされた場合は直ちに画面をロードする（
//	var anchor = $("#menulist li#${param.name} a");
//	$("#menulist li a").css("background-Color", "transparent");
//	anchor.css("background-Color", "yellow");
//	SaigaiTask.disconnect.loadContent(anchor.get(0).href+"content");
</c:if>

});

</script>
<div>
	<ul id="menulist" style="border:0px;">
		<li><a href="${f:url('/')}admin/disconnect/initImportExport/"><%=lang.__("Import / export")%></a></li>
		<li><a href="${f:url('/')}admin/disconnect/rasterDataDownload/"><%=lang.__("Download raster data")%></a></li>
		<li><a href="${f:url('/')}admin/disconnect/rasterDataUpload/"><%=lang.__("Upload raster data")%></a></li>
		<li><a href="${f:url('/')}admin/disconnect/datasync/"><%=lang.__("Disaster data synchronization")%></a></li>
	</ul>
	</ul>
</div>
