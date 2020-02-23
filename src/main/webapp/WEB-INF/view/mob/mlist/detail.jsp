<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/jsp_lang.jsp" %>
<html>
<head>
<meta charset="utf-8" />
<script type="text/javascript">
</script>
</head>
<body>
<div data-role="page">

<c:set var="gid" value=""/>
	<c:set var="e" value="${result[0]}"/>
	<c:if test="${e == null}"><c:set var="gid" value="0"/></c:if>
	<c:if test="${e != null}"><c:set var="gid" value="${e[key]}"/></c:if>
	<div data-role="header">
		<h1><%=lang.__("NIED disaster information sharing system")%> </h1>
<script type="text/javascript">
$(function() {
	loadPhoto('${table}', ${gid});

	showLocation('${e.theGeom}');

	selectPhoto(null);
});

function showLocation(loc) {
	if (loc == '') return;
	else if (loc.indexOf('POLYGON') >= 0)
		loc = 'POLYGON';
	else if (loc.indexOf('LINE') >= 0)
		loc = 'LINE';
	else if (loc.indexOf(';') > 0) {
		loc = loc.substring(loc.indexOf(';')+1);
	}
	$('#location').text(loc);
}

function datechange(form, name, datename, timename) {
	form[name].value = form[datename].value + ' ' + form[timename].value;
}

var ecommapURL = "${ecommapURL}";
</script>
	</div><!-- /header -->

<form:form modelAttribute="listForm">
	<% FormUtils.printToken(out, request); %>
	<div data-role="content" data-theme="a">

	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
<table width="90%">
	<c:forEach var="f" varStatus="t" items="${colinfoItems}">
		<tr>
		<%-- データ変換 --%>
		<c:set var="colVal" value="${e[f.attrid]}"/>
		<c:set var="req" value="" />
		<c:if test="${colMap[f.attrid]}"><c:set var="req" value=" Required" /></c:if>

		<c:choose>
			<c:when test="${f.editable || gid == 0}"><!-- edit -->

			    <td nowrap><label for="name">${f:h(f.name)}${colMap[f.attrid]?'※':':'}</label></td>
			    <td>
			    <c:if test="${editClass[t.index] == 'String' || editClass[t.index] == 'Number' || editClass[t.index] == 'Float'}">
			    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}" id="name" size="10" value="${f:h(colVal)}" data-mini="true" class="${editClass[t.index]} ${req}"/>
			    </c:if>
			    <c:if test="${editClass[t.index] == 'DateTime'}">
			    	<input type="hidden" name="${table}:${f.attrid}:${key}:${gid}" id="name" value="${f:h(colVal)}" class="${editClass[t.index]} ${req}">
			    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}:date" id="name" size="5" value="${f:h(fn:substringBefore(colVal,' '))}" data-mini="true" data-role="datebox" data-options='{"mode":"calbox","overrideDateFormat": "%Y/%m/%d"}'" onChange="datechange(this.form, '${table}:${f.attrid}:${key}:${gid}', '${table}:${f.attrid}:${key}:${gid}:date', '${table}:${f.attrid}:${key}:${gid}:time')"/>
			    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}:time" id="mode9" size="5" value="${f:h(fn:substringAfter(colVal,' '))}" data-mini="true" data-role="datebox" data-options='{"mode":"timebox","overrideTimeFormat": "24"}'" onChange="datechange(this.form, '${table}:${f.attrid}:${key}:${gid}', '${table}:${f.attrid}:${key}:${gid}:date', '${table}:${f.attrid}:${key}:${gid}:time')"/>
			    </c:if>
			    <c:if test="${editClass[t.index] == 'Date'}">
			    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}" id="name" size="10" value="${f:h(colVal)}" data-mini="true" data-role="datebox" data-options='{"mode":"calbox","overrideDateFormat": "%Y/%m/%d"}' class="${editClass[t.index]} ${req}"/>
			    </c:if>
			    <c:if test="${editClass[t.index] == 'TextArea'}">
					<textarea cols="10" rows="8" name="${table}:${f.attrid}:${key}:${gid}" id="textarea" data-mini="true" class="${editClass[t.index]} ${req}">${f:h(colVal)}</textarea>
			    </c:if>
			    <c:if test="${editClass[t.index] == 'Select'}">
					<select name="${table}:${f.attrid}:${key}:${gid}" data-mini="true">
					<c:if test="${!colMap[f.attrid] && f.editable && selectVal[f.attrid][0]!=''}"><option value=""></option></c:if>
					<c:forEach var="g" varStatus="u" items="${selectStr[f.attrid]}">
						<c:set var="g2" value="${selectVal[f.attrid]}" />
						<c:if test="${g2[u.index] != colVal}">
							<option value="${f:h(g2[u.index])}">${f:h(g)}</option>
						</c:if>
						<c:if test="${g2[u.index] == colVal}">
							<option value="${f:h(g2[u.index])}" selected>${f:h(g)}</option>
						</c:if>
					</c:forEach>
					</select>
			    </c:if>
				<c:if test="${editClass[t.index] == 'Checkbox'}">
					<input type="checkbox" name="${table}:${f.attrid}:${key}:${gid}" value="${f:h(checkStr[f.attrid])}" ${(checkStr[f.attrid]==colVal)?"checked":""} data-mini="true">
				</c:if>
				<c:if test="${editClass[t.index] == 'Upload'}">
					<c:if test="${fn:length(colVal) > 0}">
						<a href="${f:url(colVal)}" target="_blank">${f:h(colVal)}</a>
					</c:if>
				</c:if>
				</td>

			</c:when>
			<c:otherwise><!-- not edit -->
				<td><label for="name">${f:h(f.name)}:</label></td>
			    <td>${f:h(colVal)}</td>
			</c:otherwise>
		</c:choose>
		</tr>
	</c:forEach>
		<tr>
			<td><%=lang.__("position info")%></td>
			<td>
				<div id="location"></div>
				<input type="hidden" name="${table}:theGeom:${key}:${gid}" id="thegeom" value="" />
			</td>
		</tr>
		<tr>
			<td colspan="2" style="max-height:100px">
				<div id="photoarea"></div>
			</td>
		</tr>
</table>

	<div class="ui-body ui-body-b">
		<c:if test="${key == 'gid' || key == '_orgid'}">
			<input type="file" class="selector" accept="image/*" capture="camera" name="formFile" data-theme="b" data-icon="plus" data-mini="true" id="selector" onchange="selectPhoto(this)"/><!-- uploadPhoto(${gid}, ${listForm.menuid}, '${table}') -->
		</c:if>
		<fieldset class="">
			<div class="ui-block-e" style="float:right;"><input type="button" name="close" data-theme="a" data-icon="save" data-mini="true" value="<%=lang.__("Close")%>" onClick="javascript:$('.ui-dialog').dialog('close');"/></div>
			<div class="ui-block-d" style="float:right;"><input type="button" name="update" data-theme="e" data-icon="check" data-mini="true" value="<%=lang.__("Save")%>" onClick="saveData(${listForm.menuid},this.form, '${cookie.JSESSIONID.value}')"/></div>
		<c:if test="${deletable}">
			<div class="ui-block-c" style="float:right;"><input type="button" name="delete" data-theme="c" data-icon="delete" data-mini="true" value="<%=lang.__("Delete")%>" onClick="deleteData('${table}:delete:${key}:${gid}', '${gid}', '${cookie.JSESSIONID.value}')" /></div>
		</c:if>
		<c:if test="${(key == 'gid' || key == '_orgid') && geomType == 'POINT'}">
			<div class="ui-block-a" style="float:right;"><input type="button" name="gps" data-theme="b" data-icon="plus" data-mini="true" value="<%=lang.__("getting position")%>" onClick="javascript:getLocation()" /></div>
		</c:if>
	    </fieldset>
	</div>
	</div><!-- /content -->
</form:form>

</div><!-- /page -->
</body>
</html>
