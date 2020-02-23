<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>

<script>

function checkDataSub(uid, val, title) {
	if(uid == "dbareacode" ||
			uid == "dbprefcode" ||
			uid == "dbmaxquake" ||
			uid == "dbseismiccode" ||
			uid == "dbcitycode" ||
			uid == "dbkishoukeihoucode" ||
			uid == "dbwarncode" ||
			uid == "dbtatsunamiareacode" ||
			uid == "dbcategorykeihoucode" ||
			uid == "dbdosyasaigaiwarncode" ||
			uid == "dbdosyasaigaiareacode" ||
			uid == "dbvolcanoareacode" ||
			uid == "dbvolcanotypecode" ||
			uid == "dbkirokutekirainareacode" ||
			uid == "dbkirokutekiwarncode" ||
			uid == "dbtatsumakiwarncode" ||
			uid == "dbtatsumakiareacode" ||
			uid == "dbsiteigawaareacode" ||
			uid == "dbsiteigawawarncode"
			) 
	{
		if(val == "") {
			alert(MessageFormat.format('<%=lang.__("Code value not entered. Code value is essential({0}).")%>', title));
			return false;
		}
	}
	return true;
}

function checkData(type)
{
	// ChildNodes 非常にひどいBUGがあります、使えません。

	<%
	ArrayList<Map<String,String>> tableData = null;
	try {
	tableData = (ArrayList<Map<String,String>>)request.getAttribute("tableData");
	for(int i = 0; i < tableData.size(); i++) {
		Map<String,String> mapT = tableData.get(i);
	%>
		if(document.getElementById('xml_field_<%=mapT.get("lineuid")%>') != undefined) 
		{
			var val = document.getElementById('xml_field_<%=mapT.get("lineuid")%>').value;
			var uid = '<%=mapT.get("comboboxuid")%>';
			var ctrlType = '<%=mapT.get("linectrltype")%>';
			var title = '<%=mapT.get("name")%>';
			if(checkDataSub(uid, val, title) == false)return false;
			
			if(ctrlType == 'datetime' || ctrlType == 'text') {
				if(val.length <= 0) {
					alert(lang.__('<%=lang.__("Value not entered. Value is required ({0}).")%>', title));
					return false;
				}
				if(val.length > 250) {
					//入力可能文字数は250文字です。現在の入力文字数は○○文字なので、250文字以内で入力してください。
					alert(MessageFormat.format('<%=lang.__("The size of input characters must be less than or equal 250. The size of it is now {0}.({1}).")%>', val.length, title));
					return false;					
				}
			}
			
		}
	<% }
	}catch(Exception e){
		%>

		<%
	}
	%>
	
	return true;

}

function getJsonDataByJsp(type)
{
	// ChildNodes 非常にひどいBUGがあります、使えません。
	t01NewClearData();

	<%
	tableData = null;
	try {
	tableData = (ArrayList<Map<String,String>>)request.getAttribute("tableData");
	for(int i = 0; i < tableData.size(); i++) {
		Map<String,String> mapT = tableData.get(i);
	%>
		if(document.getElementById('xml_field_<%=mapT.get("lineuid")%>') != undefined) {
			{t01NewAddData('<%=mapT.get("lineuid")%>', document.getElementById('xml_field_<%=mapT.get("lineuid")%>').value);}
		}
	<% }
	}catch(Exception e){
		%>

		<%
	}
	%>

}

function xmlTablefileTypeOnChanage(val,uid,key) {
	// noting todo
}



$(document).ready(function(){
	addDataTime();
});
$(window).load(function(){

	addDataTime();
});

function addDataTime(){



	<%

	try
	{
		tableData = (ArrayList<Map<String,String>>)request.getAttribute("tableData");
		for(int i = 0; i < tableData.size(); i++)
		{
			Map<String,String> mapT = tableData.get(i);
			String strCType = mapT.get("linectrltype");
			String strCUID = mapT.get("comboboxuid");
			if(strCUID == null)strCUID = "";
			if(strCType.equals("datetime"))
			{
	%>
			    //jQuery('#time').datetimepicker({
			    //    timeFormat: "HH:mm:ss",
			    //    dateFormat: "yy-mm-dd"
			    //});

				//$("#reporttime").datetimepicker({
				//	controlType : 'select',
				//	timeFormat : 'HH:mm:ss'
				//});

				//$('#slider_example_5').datetimepicker({
				//	controlType: myControl
				//});


			    $("#xml_field_<%=mapT.get("lineuid")%>").datetimepicker({
			    	timeFormat: "HH:mm:ss",
			    	dateFormat: "yy-mm-dd"
			    });

	<%
			}
		}
	}catch(Exception e){
		%>
		<%
	}
	%>



}


</script>

<table id="meteoxmlTable" border="0" style="font-size:14px;border:0px;padding:4px;margin:8px">
	<thead>
	</thead>
	<tbody>


<c:forEach var="item" varStatus="s" items="${tableData}">
				<tr>
				<!-- ${item.level}=${item.name}=${item.botton}=${item.linetype}=${item.linectrltype}=${item.comboboxuid} -->
<c:if test="${item.linetype == 'fileeof'}">
					<td colspan="1" style="border-style: none none none none;"></td>
					<td colspan="6" style="border-style: solid none none none;"></td>

</c:if>
<c:if test="${item.linetype != 'fileeof'}">


<c:if test="${item.value == null}">
					<td style="width: 1px; border-style: none solid none none;"></td>
					<td class="${item.level == 1 ? "cell-style-b-lefttop" : "cell-style-b-left"}" ></td>
				<c:if test="${item.level == 2}">
					<td class="cell-style-b-lefttop"></td>
				</c:if>
				<c:if test="${item.level != 2}">
					<td class="${item.level < 2 ? "cell-style-b-top" : "cell-style-b-left"}" ></td>
				</c:if>
				<c:if test="${item.level == 3}">
					<td class="cell-style-b-lefttop"></td>
				</c:if>
				<c:if test="${item.level != 3}">
					<td class="${item.level < 3 ? "cell-style-b-top" : "cell-style-b-left"}" ></td>
				</c:if>
				<c:if test="${item.level == 4}">
					<td class="cell-style-b-lefttop"></td>
				</c:if>
				<c:if test="${item.level != 4}">
					<td class="${item.level < 4 ? "cell-style-b-top" : "cell-style-b-left"}" ></td>
				</c:if>
					<td style="border-style: solid none none none; padding: 2px; background-color: #CCDDEF;">
						<c:out value="${item.name}" escapeXml="true"></c:out>
					</td>
					<td style="border-style: solid solid none none; padding: 2px; background-color: #CCDDEF;"> <!-- violet -->
					</td>

					<c:if test="${item.button == 1}">
						<td><button class="btn botton_2" value="add" onclick="buttonAddClick('${item.lineuid}');"><%=lang.__("Add")%></button></td>
					</c:if>
					<c:if test="${item.button == 2}">
						<td><button class="btn botton_2" value="add" onclick="buttonAddClick('${item.lineuid}');"><%=lang.__("Add")%></button><button value="del" class="btn botton_2" onclick="buttonDelClick('${item.lineuid}');"><%=lang.__("Delete")%></button></td>
					</c:if>

</c:if>

<c:if test="${item.value != null}">
					<td style="width: 1px; border-style: none solid none none;" ></td>
				<c:if test="${item.level == 0}">
					<td class="cell-style-p-top"></td>
					<td class="cell-style-p-top"></td>
					<td class="cell-style-p-top"></td>
					<td class="cell-style-p-top"></td>
				</c:if>
				<c:if test="${item.level != 0}">
					<td class="cell-style-b-leftright"></td>
					<td class="${item.level < 2 ? "cell-style-p-top" : "cell-style-b-leftright"}" ></td>
					<td class="${item.level < 3 ? "cell-style-p-top" : "cell-style-b-leftright"}" ></td>
					<td class="${item.level < 4 ? "cell-style-p-top" : "cell-style-b-leftright"}" ></td>
				</c:if>
					<td style="border-style: solid none none none; background-color: #F7E8E8;">
						<c:out value="${item.name}" escapeXml="true"></c:out>
					</td>
					<td style="border-style: solid solid none solid; background-color: #F7E8E8;"> <!-- white -->
	<c:if test="${item.linectrltype == 'text'}">
						<span style="position: relative; z-index: 9999;">
						<input id="xml_field_${item.lineuid}" value='<c:out value="${item.value}" escapeXml="true"></c:out>' ></input>
						</span>
	</c:if>
	<c:if test="${item.linectrltype == 'datetime'}">
						<span style="position: relative; z-index: 9999;">
						<input id="xml_field_${item.lineuid}" value="${item.value}" readonly="readonly"></input>
						</span>
	</c:if>


	<c:if test="${item.linectrltype == 'combobox'}">
		<% // T01----------------------------------------------------------------------------------------------- %>
		
		<c:if test="${item.comboboxuid == 'dbareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbprefcode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbprefcode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbprefcode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbmaxquake'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbmaxquake');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbmaxquake}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbseismiccode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbseismiccode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbseismiccode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbcitycode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbcitycode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbcitycode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbkishoukeihoucode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbkishoukeihoucode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbkishoukeihoucode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<% // T01-----------------------------------------------------------------------------------------------END %>

		<% // T02----------------------------------------------------------------------------------------------- %>
		<c:if test="${item.comboboxuid == 'dbwarncode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box" onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbwarncode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbwarncode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbtatsunamiareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbtatsunamiareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbtatsunamiareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbcategorykeihoucode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbcategorykeihoucode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbcategorykeihoucode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>

		<c:if test="${item.comboboxuid == 'dbdosyasaigaiwarncode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbdosyasaigaiwarncode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbdosyasaigaiwarncode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbdosyasaigaiareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbdosyasaigaiareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbdosyasaigaiareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>

		<c:if test="${item.comboboxuid == 'dbvolcanoareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbvolcanoareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbvolcanoareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>


		<c:if test="${item.comboboxuid == 'dbvolcanotypecode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbvolcanotypecode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbvolcanotypecode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>


		<c:if test="${item.comboboxuid == 'dbkirokutekirainareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbkirokutekirainareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbkirokutekirainareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>

		<c:if test="${item.comboboxuid == 'dbkirokutekiwarncode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbkirokutekiwarncode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbkirokutekiwarncode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbtatsumakiwarncode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbtatsumakiwarncode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbtatsumakiwarncode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>

		<c:if test="${item.comboboxuid == 'dbtatsumakiareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbtatsumakiareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbtatsumakiareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>

		<c:if test="${item.comboboxuid == 'dbsiteigawaareacode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbsiteigawaareacode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbsiteigawaareacode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>
		<c:if test="${item.comboboxuid == 'dbsiteigawawarncode'}">
						<select id="xml_field_${item.lineuid}" style="width: 300px;" class="box"
						onchange="xmlTablefileTypeOnChanage(this.value,'${item.lineuid}', 'dbsiteigawawarncode');">
							<c:forEach var="itemCombo" varStatus="s" items="${dbsiteigawawarncode}">
								<option value="${itemCombo.key}" "${item.value == itemCombo.key ? " selected='selected' " : ""}">${itemCombo.value}</option>
							</c:forEach>
						</select>
		</c:if>


		<% // T02-----------------------------------------------------------------------------------------------END %>
	</c:if>



					</td>
</c:if>

</c:if>
				</tr>
</c:forEach>

	</tbody>
</table>
