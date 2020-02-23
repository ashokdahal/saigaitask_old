<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<%-- <jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include> --%>
<%@include file="../common/adminjs-header.jsp" %>
<meta charset="utf-8">
<meta name="description" content="<%=lang.__("It is admin window of NIED disaster information sharing system.")%>">
<meta name=" keywords" content="<%=lang.__("NIED disaster information sharing system, admin window")%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />
<title><%=lang.__("Admin window : NIED disaster information sharing system")%></title>

<script type="text/javascript" language="javascript">
function confirm_insert() {
	if (document.getElementById("registered").value == "true") {
		return confirm("<%=lang.__("Selected layer has been registered already. Are you sure to update?")%>");
	}
	else {
		return confirm("<%=lang.__("Are you sure to register?")%>");
	}
}

$(function() {
  $("#lyid").change(function() {
	  if($(this).val() != ""){
		var mapIdLayerId = $(this).val().split(':');
		var _mapId = mapIdLayerId[0];
		var _layerId = mapIdLayerId[1];
		var _id = mapIdLayerId[2];
		var layerName = $("option:selected", this).text();
		$.ajax({
			async:    false, // 同期通信
			url:      SaigaiTask.contextPath+'/admin/uploadclearinghouseData/changevalue',
			type:     'GET',
			dataType: 'json',
			cache:    false, // ブラウザにキャッシュさせません。
//			data:     {layerId :$(this).val()},
			data:     {mapId :_mapId, layerId : _layerId, id : _id},
			error:    function(){
				alert("<%=lang.__("Failed to read json file.")%>");
			},
			success:  function(data){ // サーバからJSON形式のデータを受け取る
				// JSON 形式をHTMLへ変換する
		      	$("#wms").val(data.wms);
				//document.getElementById("spanwms").innerHTML = data.wms;
		      	$("#wfs").val(data.wfs);
				//document.getElementById("spanwfs").innerHTML = data.wfs;
		      	$("#registered").val(data.registered);
				document.getElementById("registered").value = data.registered;
				document.getElementById("spanExTmp").innerHTML = (data.exTemp[0]==null&&data.exTemp[1]==null) ? "<%=lang.__("None")%>" : data.exTemp;

				////////////////////////////////////////////////////////////
				//メタデータに関する情報
				////////////////////////////////////////////////////////////
				// メタデータを公開する対象
				var useLimitInput = $("[name=useLimit]");
				useLimitInput.val(data["useLimit"]);
				if(data.registered==false && useLimitInput.val()==null) {
					useLimitInput.val("001");
				}

				////////////////////////////////////////////////////////////
				// 地図データに関する情報
				////////////////////////////////////////////////////////////
				// 地図データのタイトル
				$("#layerName").text(layerName);

				// 地図データの内容の要約
				$("[name=abstr]").val(data["abstract"]);

				// 地図データが作成された目的
				$("[name=purpose]").val(data["purpose"]);

				////////////////////////////////////////////////////////////
				// メタデータ/地図データに関する問い合わせ先情報
				////////////////////////////////////////////////////////////
				// 問い合わせ先の名称
				$("[name=organizationName]").val(data["contactname"]);

				// 郵便番号
				$("[name=postCode]").val(data["Ident_postCode"]);

				// 都道府県
				$("[name=adminArea]").val(data["Ident_adminArea"]);
				$("[name=adminAreaCode]").val(data["Ident_adminAreaCode"]);

				// 市区町村
				$("[name=city]").val(data["Ident_city"]);
				$("[name=cityCode]").val(data["Ident_cityCode"]);

				// 町名、番地、ビル名等
				$("[name=delPoint]").val(data["Ident_delPoint"]);

				// 電話番号
				$("[name=voice]").val(data["Ident_cntPhone"]);

				// 電子メールアドレス
				$("[name=eMailAdd]").val(data["Ident_eMailAdd"]);

				// 問い合わせ先のHP等のURL
				$("[name=linkage]").val(data["Ident_linkage"]);

				var information = "";
				if(data.registered) {
					information +=MessageFormat.format("<%=lang.__("meta data ID {0} registered.")%>", data["fileIdentifier"]);
					information +="<br/><%=lang.__("Update date and time")%>："+data["updateTime"];
				}
				$("#registration_information").html(information);
			}
		});
	  }else{
			document.getElementById("spanwms").innerHTML = "";
			document.getElementById("spanwfs").innerHTML = "";
			$("#layerName").text("");
	  }
  });
});
</script>

</head>

<body>
<div id="wrapper">
	<h1><%=lang.__("Registration window to the clearinghouse")%></h1>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<form:form modelAttribute="uploadclearinghouseDataForm">

	<table border="1" class="form" style="float:left;">
		<tr>
			<th><%=lang.__("Target layer<!--2-->")%><font color="#ff0000">※</font></th>
			<td>
				<form:select  id="lyid" path="layerid" cssClass="styledselect">
<!-- 					<form:option value="0">選択してください</form:option>-->
					<form:option value=""><%=lang.__("Select<!--2-->")%></form:option>
					<c:forEach var="e" varStatus="s" items="${tablemasterInfos}">
<!--						<form:option value="${e.layerid}">${f:h(e.name)} </form:option>-->
						<c:if test="${!empty e.layerid}">
						<form:option value="${f:h(e.mapmasterInfo.mapid)}:${f:h(e.layerid)}:${f:h(e.id)}">${f:h(e.name)} </form:option>
						</c:if>
					</c:forEach>
				</form:select>
				<c:if test="${isRequiredLayerId}">
					<font color="#ff0000"><%=lang.__("Select layer.")%></font>
				</c:if>
			</td>
		</tr>
	</table>

	<br/>

	<table border="1" class="form" style="float:left;">

		<tr>
			<td colspan=2 id="registration_information" style="background-color: none;"></td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Info for meta data")%></th>
		</tr>

		<tr>
			<th><%=lang.__("Meta data publicity target")%></th>
			<td>
				<form:select id="useLimit" path="useLimit" cssClass="styledselect">
					<option value="001" <c:if test="${uploadclearinghouseDataForm.useLimit == '001'}">selected</c:if>><%=lang.__("Share in disaster prevention agencies")%></option>
					<option value="002" <c:if test="${uploadclearinghouseDataForm.useLimit == '002'}">selected</c:if>><%=lang.__("Release to the public")%></option>
				</form:select>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Info for map data")%></th>
		</tr>

		<tr>
			<th><%=lang.__("Map data title")%><font style="font-size:80%;">(*1)</font></th>
			<td><form:input id="pfix" path="prefix" size="15"/>/<span id="layerName"></span>/<form:input id="sfix" path="suffix" size="15"/></td>
		</tr>

		<tr>
			<th><%=lang.__("Summary for map data")%></th>
			<td><form:textarea path="abstr" cols="60" rows="3"/></td>
		</tr>
		<tr>
			<th><%=lang.__("The purpose of the map created")%></th>
			<td><form:textarea path="purpose" cols="60" rows="3"/></td>
		</tr>
		<tr>
			<th><%=lang.__("Creation status for map data")%></th>
			<td>
			<%--
				<form:select id="status" path="status" cssClass="styledselect">
					<option value="001" <c:if test="${uploadclearinghouseDataForm.status == '001'}">selected</c:if>><%=lang.__("Completion")%></option>
					<option value="999" <c:if test="${uploadclearinghouseDataForm.status == '999'}">selected</c:if>><%=lang.__("Plan")%></option>
				</form:select>
			--%>
			<%=lang.__("Register ''Plan'' in normal time. <br/> Register ''Finished'' in disaster or drill time.")%>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Contact info for meta/map data")%><font style="font-size:80%;">(*1)</font></th>
		</tr>

		<tr>
			<th><%=lang.__("Name of contact to")%></th>
			<td><form:textarea path="organizationName" cols="60" rows="3"/></td>
		</tr>

		<tr>
			<th><%=lang.__("Post Code")%></th>
			<td><form:input id="postCode" path="postCode" size="20"/></td>
		</tr>

		<tr>
			<th><%=lang.__("Prefectures")%></th>
			<td>
				<span><form:input path="adminArea" size="20"/>&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Code")%>:<form:input id="adminAreaCode" path="adminAreaCode" size="20"/></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("City")%></th>
			<td>
				<span><form:input path="city" size="20"/>&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Code")%>:<form:input path="cityCode" size="20"/></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("house number, building name.")%></th>
			<td><form:input id="delPoint" path="delPoint" cssStyle="width: 99%;"/></td>
		</tr>

		<tr>
			<th><%=lang.__("Phone number")%></th>
			<td><form:input id="voice" path="voice" cssStyle="width: 99%;"/></td>
		</tr>

		<tr>
			<th><%=lang.__("e-Mail")%></th>
			<td><form:input id="eMailAdd" path="eMailAdd" cssStyle="width: 99%;"/></td>
		</tr>

		<tr>
			<th><%=lang.__("URL of contact to")%></th>
			<td><form:input id="linkage" path="linkage" cssStyle="width: 99%;"/></td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Time coverage info for map data")%></th>
		</tr>

		<tr>
			<th><%=lang.__("Info about time coverage")%></th>
			<td>
				<span id="spanExTmp"><c:out value="${uploadclearinghouseDataForm.wfs}"></c:out></span>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Access info for map data")%></th>
		</tr>

		<tr>
			<th>WMS URL</th>
			<td><form:input id="wms" path="wms" readonly="true" cssStyle="width:99%;"/></td>
		</tr>

		<tr>
			<th>WFS URL</th>
			<td><form:input id="wfs" path="wfs" readonly="true" cssStyle="width:99%;"/></td>
		</tr>

		<tr>
			<td colspan=2 style="background-color: white;"><input type="checkbox" id="updateDefault" name="updateDefault" value="true"/><label for="updateDefault"><%=lang.__("Update meta data default info.")%></label><font style="font-size:80%;">(*1)</font></td>
		</tr>

	</table>
	<form:hidden id="registered" path="registered" value= "false" />

		<div class="txtC" style="clear: left;">
			<input type="submit" name="insert" value="<%=lang.__("Registration")%>" onclick="return confirm_insert();" style="width:120px;"/><br/>
<%--			<input type="submit" name="delete" value="削除" style="width:120px;"/>--%>
		</div>
	</form:form>
	</div><!-- /#contents_setup -->
</div><!-- /#wrapper -->
</body>
</html>
