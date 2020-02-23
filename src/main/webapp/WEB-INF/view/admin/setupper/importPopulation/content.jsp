<%/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<script type="text/javascript">
</script>

<div id="content_head" class="ui-layout-north">
	<h1>
		<span><%=lang.__("Population import tool")%></span>
	</h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left; padding: 0px;">
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<form:form id="importPolulationForm" servletRelativeAction="/admin/setupper/importPopulation/update" enctype="multipart/form-data" modelAttribute="importPopulationForm">
<a href="http://e-stat.go.jp/SG2/eStatGIS/page/download.html" target="_blank"><%=lang.__("To download page")%></a><br>
<br>
<%=lang.__("Layer ID")%>:<form:input path="layerId" size="10" /><%=lang.__("* A new layer will be created in case of no text input.")%><br>
<br>
<%=lang.__("Statistics text data")%><br>
<input type="file" name="textFile"/><br>
<br>
<%=lang.__("Border Shape data(ZIP format)")%><br>
<input type="file" name="shapeFile"/><br>


</form:form>

</div>


<div id="content_footer" class="ui-layout-south">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.submitForm('importPolulationForm', '<%=lang._E("Send?")%>');"><%=lang.__("Send")%></a>

</div>

