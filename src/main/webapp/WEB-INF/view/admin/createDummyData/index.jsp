<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="../../common/lang_resource.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/global.css')}"/>
</head>
<body>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<form:form enctype="multipart/form-data" modelAttribute="createDummyDataForm">
<table>
<tr>
	<th><%=lang.__("Create hazard map")%></th>
</tr>
<tr>
	<td><%=lang.__("Hazard map layer ID")%>：</td><td><form:input path="hazardLayerIds" /> <%=lang.__("* Multiple entries allowed by CSV")%></td>
</tr>
<tr>
	<td><%=lang.__("Hazard map water level item ID")%>：</td><td><form:input path="hazardAttrId" /></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td><%=lang.__("Mesh layer ID")%>：</td><td><form:input path="meshLayerId" /></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td><%=lang.__("Out-to layer ID")%>：</td><td><form:input path="outLayerId" /></td>
</tr>
<tr>
	<td><%=lang.__("Output destination water level item ID")%>：</td><td><form:input path="outAttrId" /></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td><%=lang.__("Level reductions numeric")%>：</td><td><form:input path="minus" /></td>
</tr>
</table>
<input type="submit" name="mesh" value="<%=lang.__("Run meshing")%>" />
<br />
<br />
<table>
<tr>
	<th><%=lang.__("Create telemeter data")%></th>
</tr>
<tr>
	<td><%=lang.__("Type<!--2-->")%>：</td><td><form:radiobutton path="observtype" value="1" /><%=lang.__("Rainfall")%> <form:radiobutton path="observtype" value="2" /><%=lang.__("Water level")%> </td>
</tr>
<tr>
	<td><%=lang.__("Monitoring/observation info ID")%>：</td><td><form:input path="observpointinfoid" /></td>
</tr>
<tr>
	<td><%=lang.__("Registered date and time")%>：</td><td><form:input path="untildate" /> <form:input path="untiltime" /> <%=lang.__("* Register to specified time")%></td>
</tr>
<tr>
	<td><%=lang.__("Inflated")%>：</td><td><form:input path="wplus" /> </td>
</tr>
<tr>
	<td> <%=lang.__("File")%>:</td>
	<td>
		<input name="formFile" type="file" size="25" />
	</td>
</tr>
</table>
<input type="submit" name="telemeter" value="<%=lang.__("Create telemeter data")%>" />
<br />
<br />
<br />
<input type="submit" name="deletemap" value="<%=lang.__("Forbidden button")%>" />
<br />
<br />
<input type="submit" name="deletelayer" value="<%=lang.__("Forbidden button 2")%>" />



</form:form>
</body>
</html>
