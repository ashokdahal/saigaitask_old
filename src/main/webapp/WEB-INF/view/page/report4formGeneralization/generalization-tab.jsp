<%@page contentType="text/html; charset=UTF-8" %>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>
<c:set var="listId" value="generalizationlist"/>
<c:set var="showTotal" value="true"/>
<table class="table02 generalization-table" id="${listId}">
	<tr>
		<th class="head" colspan="5"><%=lang.__("City<!--2-->")%></th>
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${0<fn:length(report4formForm.city)?report4formForm.city:report4formForm.pref}</td>
		</c:forEach>
		<c:if test="${showTotal}"><td><%=lang.__("Total<!--2-->")%></td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="5"><%=lang.__("Disaster name")%></th>
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.disastername)}</td>
		</c:forEach>
		<c:if test="${showTotal}"><td>―</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="5"><%=lang.__("Report number")%></th>
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td><%=lang.__("No.<!--2-->")%>${f:h(report4formForm.reportno)}<%=lang.__("Report")%></td>
		</c:forEach>
		<c:if test="${showTotal}"><td>―</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="5"><%=lang.__("Reporting date and time")%></th>
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${fn:replace(report4formForm.reporttime, " ", "<br/>")}</td>
		</c:forEach>
		<c:if test="${showTotal}"><td>―</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="5"><%=lang.__("Rapporteur name")%></th>
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.reporter)}</td>
		</c:forEach>
		<c:if test="${showTotal}"><td>―</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="4"><%=lang.__("Human<br/>damage")%></th>
		<th class="head" colspan="3"><%=lang.__("Deaths")%></th>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.casualties21)}</td>
		<c:set var="total" value="${total +  report4formForm.casualties21}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Missing persons")%></th>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.casualties22)}</td>
		<c:set var="total" value="${total +  report4formForm.casualties22}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="2" colspan="2"><%=lang.__("Injured")%></th>
		<th class="head" width="40"><%=lang.__("Seriously injured")%></th>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.casualties23)}</td>
		<c:set var="total" value="${total +  report4formForm.casualties23}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("Slight injury")%></th>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.casualties24)}</td>
		<c:set var="total" value="${total +  report4formForm.casualties24}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="15"><%=lang.__("House<br/>damage")%></th>
		<th class="head" colspan="3" rowspan="3"><%=lang.__("Fully destroyed")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houseall1)}</td>
		<c:set var="total" value="${total +  report4formForm.houseall1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("Household")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houseall2)}</td>
		<c:set var="total" value="${total +  report4formForm.houseall2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houseall3)}</td>
		<c:set var="total" value="${total +  report4formForm.houseall3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3" rowspan="3"><%=lang.__("Partially destroyed")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.househalf1)}</td>
		<c:set var="total" value="${total +  report4formForm.househalf1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("Household")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.househalf2)}</td>
		<c:set var="total" value="${total +  report4formForm.househalf2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.househalf3)}</td>
		<c:set var="total" value="${total +  report4formForm.househalf3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3" rowspan="3"><%=lang.__("Partial damage")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.housepart1)}</td>
		<c:set var="total" value="${total +  report4formForm.housepart1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("Household")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.housepart2)}</td>
		<c:set var="total" value="${total +  report4formForm.housepart2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.housepart3)}</td>
		<c:set var="total" value="${total +  report4formForm.housepart3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3" rowspan="3"><%=lang.__("Inundation above floor")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houseupper1)}</td>
		<c:set var="total" value="${total +  report4formForm.houseupper1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("Household")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houseupper2)}</td>
		<c:set var="total" value="${total +  report4formForm.houseupper2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houseupper3)}</td>
		<c:set var="total" value="${total +  report4formForm.houseupper3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3" rowspan="3"><%=lang.__("Inundation under floor")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houselower1)}</td>
		<c:set var="total" value="${total +  report4formForm.houselower1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("Household")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houselower2)}</td>
		<c:set var="total" value="${total +  report4formForm.houselower2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.houselower3)}</td>
		<c:set var="total" value="${total +  report4formForm.houselower3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="2"><%=lang.__("Ruinous<br/>house")%></th>
		<th class="head" colspan="3"><%=lang.__("Public facility")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.build1)}</td>
		<c:set var="total" value="${total +  report4formForm.build1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Other")%></th>
		<th class="head"><%=lang.__("Building<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.build2)}</td>
		<c:set var="total" value="${total +  report4formForm.build2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="22"><%=lang.__("O<br/>O<br/>O")%></th>
		<th class="head" rowspan="2"><%=lang.__("Rice field")%></th>
		<th class="head" colspan="2"><%=lang.__("Outflow<br/>pit")%></th>
		<th class="head">ha</th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.field1)}</td>
		<c:set var="total" value="${total +  report4formForm.field1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="2"><%=lang.__("Flooding")%></th>
		<th class="head">ha</th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.field2)}</td>
		<c:set var="total" value="${total +  report4formForm.field2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="2"><%=lang.__("Farm")%></th>
		<th class="head" colspan="2"><%=lang.__("Outflow<br/>pit")%></th>
		<th class="head">ha</th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.farm1)}</td>
		<c:set var="total" value="${total +  report4formForm.farm1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="2"><%=lang.__("Flooding")%></th>
		<th class="head">ha</th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.farm2)}</td>
		<c:set var="total" value="${total +  report4formForm.farm2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Educational facilities")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.school)}</td>
		<c:set var="total" value="${total +  report4formForm.school}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Hospital")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.hospital)}</td>
		<c:set var="total" value="${total +  report4formForm.hospital}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Road")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.road)}</td>
		<c:set var="total" value="${total +  report4formForm.road}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Bridge")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.bridge)}</td>
		<c:set var="total" value="${total +  report4formForm.bridge}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("River")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.river)}</td>
		<c:set var="total" value="${total +  report4formForm.river}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Port")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.harbor)}</td>
		<c:set var="total" value="${total +  report4formForm.harbor}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Erosion control")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.landslide)}</td>
		<c:set var="total" value="${total +  report4formForm.landslide}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Cleaning facilities")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.gabage)}</td>
		<c:set var="total" value="${total +  report4formForm.gabage}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Cliff failure")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.cliff)}</td>
		<c:set var="total" value="${total +  report4formForm.cliff}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Train service interruptions")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.railway)}</td>
		<c:set var="total" value="${total +  report4formForm.railway}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Damaged ship")%></th>
		<th class="head"><%=lang.__("Count<!--2-->")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.ship)}</td>
		<c:set var="total" value="${total +  report4formForm.ship}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Water supply<!--2-->")%></th>
		<th class="head"><%=lang.__("Houses")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.water)}</td>
		<c:set var="total" value="${total +  report4formForm.water}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Telephone")%></th>
		<th class="head"><%=lang.__("Circuit")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.telephone)}</td>
		<c:set var="total" value="${total +  report4formForm.telephone}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Number of afflicted households")%></th>
		<th class="head"><%=lang.__("Household")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.suffer1)}</td>
		<c:set var="total" value="${total +  report4formForm.suffer1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Electricity")%></th>
		<th class="head"><%=lang.__("Houses")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.electricity)}</td>
		<c:set var="total" value="${total +  report4formForm.electricity}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Gas")%></th>
		<th class="head"><%=lang.__("Houses")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.gas)}</td>
		<c:set var="total" value="${total +  report4formForm.gas}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Block wall, etc.")%></th>
		<th class="head"><%=lang.__("Place")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.block)}</td>
		<c:set var="total" value="${total +  report4formForm.block}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Number of victims")%></th>
		<th class="head"><%=lang.__("People")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.suffer2)}</td>
		<c:set var="total" value="${total +  report4formForm.suffer2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="3"><%=lang.__("F<br/>i<br/>r<br/>e")%></th>
		<th class="head" colspan="3"><%=lang.__("Building")%></th>
		<th class="head"><%=lang.__("Items")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.fire1)}</td>
		<c:set var="total" value="${total +  report4formForm.fire1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Hazardous material")%></th>
		<th class="head"><%=lang.__("Items")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.fire2)}</td>
		<c:set var="total" value="${total +  report4formForm.fire2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Other")%></th>
		<th class="head"><%=lang.__("Items")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.fire3)}</td>
		<c:set var="total" value="${total +  report4formForm.fire3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="4"><%=lang.__("Public educational facilities")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount1)}</td>
		<c:set var="total" value="${total +  report4formForm.amount1}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="4"><%=lang.__("Agriculture, forestry and fisheries facilities")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount2)}</td>
		<c:set var="total" value="${total +  report4formForm.amount2}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="4"><%=lang.__("Public works facilities")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount3)}</td>
		<c:set var="total" value="${total +  report4formForm.amount3}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="4"><%=lang.__("Other public facilities")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount4)}</td>
		<c:set var="total" value="${total +  report4formForm.amount4}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" rowspan="6"><%=lang.__("O<br/>O<br/>O")%></th>
		<th class="head" colspan="3"><%=lang.__("Damaged agriculture")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount5)}</td>
		<c:set var="total" value="${total +  report4formForm.amount5}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Damaged forestry")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount6)}</td>
		<c:set var="total" value="${total +  report4formForm.amount6}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Livestock damage")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount7)}</td>
		<c:set var="total" value="${total +  report4formForm.amount7}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Damaged fisheries")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount8)}</td>
		<c:set var="total" value="${total +  report4formForm.amount8}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Commerce and industry damage")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount9)}</td>
		<c:set var="total" value="${total +  report4formForm.amount9}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
	<tr>
		<th class="head" colspan="3"><%=lang.__("Other")%></th>
		<th class="head"><%=lang.__("1000 yen")%></th>
		<c:set var="total" value="0" />
		<c:forEach var="report4formForm" items="${report4formForms}">
		<td>${f:h(report4formForm.amount10)}</td>
		<c:set var="total" value="${total +  report4formForm.amount10}"/>
		</c:forEach>
		<c:if test="${showTotal}"><td>${total}</td></c:if>
	</tr>
</table>

<div id="csv-dialog" title="<%=lang.__("CSV file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as CSV file.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form servletRelativeAction="/page/report4formGeneralization/generalizationTabCsv" method="POST" modelAttribute="report4formForm">
			<% FormUtils.printToken(out, request); %>
			<form:hidden path="menuid"/>
			<c:forEach var="rdid" items="${reportdataids}">
			<input type="hidden" name="reportdataids" value="${rdid}"/>
			</c:forEach>
			<a href='#' onclick='$(this).parent("form").submit(); SaigaiTask.Page.csvDialog.dialog("close");' class="dialog-button" ><%=lang.__("CSV output")%></a>
		</form:form>
	</div>
</div>

<div id="pdf-dialog" title="<%=lang.__("PDF file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as PDF file.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form servletRelativeAction="/page/report4formGeneralization/generalizationTabPdf" method="POST" modelAttribute="report4formForm">
			<% FormUtils.printToken(out, request); %>
			<form:hidden path="menuid"/>
			<c:forEach var="rdid" items="${reportdataids}">
			<input type="hidden" name="reportdataids" value="${rdid}"/>
			</c:forEach>
			<a href='#' onclick='$(this).parent("form").submit(); SaigaiTask.Page.pdfDialog.dialog("close");' class="dialog-button" ><%=lang.__("PDF output")%></a>
		</form:form>
	</div>
</div>

<script type="text/javascript">
//履歴保存
function saveHistoryData(listId) {

	var reportdataids = JSON.parse("${reportdataids}");

	// URL
	var url = SaigaiTask.contextPath+"/page/report4formGeneralization/generalizationTabSave?";
	for(var idx in reportdataids) {
		if(0<idx) url += "&";
		url += "reportdataids="+reportdataids[idx];
	}

	SaigaiTask.Page.saveDialog.mask("<%=lang.__("Now saving..<!--2-->")%>");
	$.ajax({
		url: url,
		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		method: "post",
		complete: function(xhr, textStatus) {
			SaigaiTask.Page.saveDialog.unmask();
			SaigaiTask.Page.saveDialog.dialog("close");
			var msg = "<div><%=lang.__("Saved.")%></div>";
			if(textStatus=="error") {
				msg = "<div><%=lang.__("Unable to save.")%></div>";
			}
			$(msg).dialog({
				title: "<%=lang.__("Save history")%>",
				buttons: { "OK": function() { $(this).dialog("close"); } }
			});
		}
	});
}
</script>

<%-- 履歴保存 --%>
<div id="save-dialog" title="<%=lang.__("Save history")%>" style="display:none;">
	<div>
		<%=lang.__("Save data displayed on the list as history in CSV and PDF file.")%><br>
	</div>
	<br>
	<div align="center">
		<a href="javascript:saveHistoryData('${listId}');" class="dialog-button" ><%=lang.__("History saving of summary table")%></a>
	</div>
</div>

<%-- 履歴表示 --%>
<div id="history-dialog" title="<%=lang.__("History display")%>" style="display:none;">
	<div>
		<%=lang.__("Please choose format to download you want.")%><br>
	</div>
	<br>
	<div align="center">
		<a href="javascript:showHistoryData('${listId}');" class="dialog-button" ><%=lang.__("History display of summary table")%></a>
	</div>
</div>

<script type="text/javascript">
$(".dialog-button").button();
</script>
