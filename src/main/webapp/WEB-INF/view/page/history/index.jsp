<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<tiles:insertTemplate template="/WEB-INF/view/common/layout.jsp" flush="true">
	<tiles:putAttribute name="content" type="string">
<link rel="stylesheet" type="text/css" href="../css/jquery.tablesorter/style.css" />
<link rel="stylesheet" type="text/css" href="../css/jquery-ui-timepicker-addon.css" />
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="../js/jquery.edit-table.js"></script>
<script type="text/javascript" src="../js/jquery-ui-timepicker-addon.js"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script type="text/javascript" src="../js/jquery-ui-timepicker-ja.js"></script>
</c:if>
<script type="text/javascript" src="../js/jquery.upload-1.0.2.min.js"></script>
<jsp:include page="/WEB-INF/view/page/map/head.jsp"/>
<script type="text/javascript">

//SaigaiTask.Page.List({
	/**
	 * メタデータ
	 * @type {Object<String, String>} メタデータID, タイトル
	 */
//	metadatas: JSON.parse('${fal:json(metadataMap)}')
//});

var addline = false;
var idsArr = new Array();
var typesArr = new Array();
var groupingArr = new Array();

$(function() {
    $("#editTable").tablesorter({
    	widgets: ['zebra'],
		headers: {
<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		<c:if test="${!e.sortable}">
			${s.index+1}:{sorter:false},
		</c:if>
</c:forEach>
		},
    	sortList: [[0,1]]
    });
});
$(function() {

	//for フィルタリング
	var filterFunc;

	if($("tr.gray")){
		$("tr.gray").find('select').attr("disabled", "disabled");
		$("tr.gray").find('a').removeAttr("onclick");//onclickを不可に
		$("tr.gray").find('a').css("cursor", "default");//カーソルをpointerにしない

		var grayFids = new Array(
				<c:forEach var="e" varStatus="s" items="${result}">
					<c:if test="${!filterIds[f:h(e[key])]}">
						"${e[key]}",
					</c:if>
				</c:forEach>
				""//ダミーのnullデータ
			);
		//対象外データのグレー表示/非表示の切り替え
		if($("#filter-gray-ck") && grayFids.length>1){
			//$("#filter-gray-ck").removeAttr("disabled");
			$("#filter-gray-ck-div").css("display","block");
			filterFunc = function(){
				//グレー表示
				if($("#filter-gray-ck").prop("checked")){
					$("tr.gray").css("display", "table-row");
					//grayの行を非表示にすると、tablesorterの色分けが崩れるので、以下で制御
					//一旦、oddとevenクラスを全削除
					$("table#editTable tr").each(function(){
						$(this).removeClass('even');
						$(this).removeClass('odd');
					});
					//振り直し
					var idx=0;
					$("table#editTable tr").each(function(){
						if(idx%2==1){
							$(this).addClass('odd');
						}else{
							$(this).addClass('even');
						}
						idx++;
					});
					SaigaiTask.PageURL.override({filtergray : "true"});
				//非表示
				}else{
					$("tr.gray").css("display", "none");
					//grayの行を非表示にすると、tablesorterの色分けが崩れるので、以下で制御
					//一旦、oddとevenクラスを全削除
					$("table#editTable tr").each(function(){
						$(this).removeClass('even');
						$(this).removeClass('odd');
					});
					//振り直し
					var idx=0;
					$("table#editTable tr").each(function(){
						if(!$(this).hasClass("gray")){//grayクラスは飛ばす
							if(idx%2==1){
								$(this).addClass('odd');
							}else{
								$(this).addClass('even');
							}
							idx++;
						}
					});
					SaigaiTask.PageURL.override({filtergray : "false"});
				}
			//});
			}
			$("#filter-gray-ck").change(function(){
				filterFunc();
			});
		}else{
			//$("#filter-gray-ck").attr("disabled", "disabled");
			$("#filter-gray-ck-div").css("display", "none");
		}
	}

	//グレー表示On/Off
	if($("#filter-gray-ck") && filterFunc!=undefined){
		var filtergray = "${filtergray}";
		if(filtergray == "false"){
			$("#filter-gray-ck").attr("checked", false );
		}else{
			$("#filter-gray-ck").attr("checked", true );
		}
		filterFunc();
	}

	//ボタンのスタイル設定
	$(".dialog-button").button();
	$("#dialog-button-list li").css("margin", "5px").css("float", "left").css("list-style", "none");

});

//CSVファイル出力
function outputCSV(form){

	//ここでテーブル表示の内容と同じループを回して、CSVの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	var datas = new Array();
	//ヘッダ
	datas[0] = new Array();
	datas[0][0] = "";//合計値用対策
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		datas[0][${s.index+1}] = "${f:h(e.name)}";
	</c:forEach>
	//合計
	datas[1] = new Array();
	datas[1][0] = "<%=lang.__("Total<!--2-->")%>";//<%=lang.__("Total<!--2-->")%>値用対策
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		datas[1][${s.index+1}] = "${sumItems[e.name]}";
	</c:forEach>
	//データ、フィルタリング対象のもののみ出力
	<c:forEach var="e" varStatus="s" items="${result}">
		datas[${s.count+1}] = new Array();
		<c:if test="${filterIds[f:h(e[key])]}">
			datas[${s.count+1}][0] = "";//合計値用対策
			<c:forEach var="f" varStatus="t" items="${colinfoItems}">
				<c:set var="selMap" value="${selectValView[f.attrid]}" />
				<c:choose>
					<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
						datas[${s.count+1}][${t.index+1}] = "${fal:nolf(selMap[f:h(e[f.attrid])])}";
					</c:when>
					<c:when test="${editClass[t.index] == 'Upload'}">
						<c:if test="${fn:length(e[f.attrid]) > 0}">
							datas[${s.count+1}][${t.index+1}] = "${fal:nolf(f:h(e[f.attrid]))}";
						</c:if>
					</c:when>
					<c:otherwise>
						datas[${s.count+1}][${t.index+1}] = "${fal:nolf(f:h(e[f.attrid]))}";
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:if>
	</c:forEach>

	//二重配列に格納したデータをカンマ区切りで整形、改行は「<br>」で代用して、パラメータ文字列に整形
	var csvStr = "";
	for(var i=0; i<datas.length; i++){
		for(var j=0; j<datas[i].length; j++){
			//alert(datas[i].length);
			csvStr += datas[i][j];
			if(j<datas[i].length-1){
				csvStr += ",";
			}
		}
		csvStr += "<br>";
	}

	//formのPOSTで文字列を送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('/page/list/outputcsv/')}";
		form.method = "POST";
		$('<input>').attr({"type":"hidden","name":"session_token","value":'${cookie.JSESSIONID.value}'}).appendTo(form);
		form.value.value = csvStr;
		form.submit();
	}while(0);
	$("#csv-dialog").dialog("close");
}

//PDFファイル出力
function outputPDF(form){

	//ここでテーブル表示の内容と同じループを回して、PDFの元になるテキストを出力して二重配列の変数にセットする
	var datas = new Array();
	//ヘッダ
	datas[0] = new Array();
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		datas[0][${s.index}] = "${f:h(e.name)}";
	</c:forEach>
	//データ、フィルタリング対象のもののみ出力
	<c:forEach var="e" varStatus="s" items="${result}">
		<c:if test="${filterIds[f:h(e[key])]}">
		datas[${s.count}] = new Array();
			<c:forEach var="f" varStatus="t" items="${colinfoItems}">
				<c:set var="selMap" value="${selectValView[f.attrid]}" />
				<c:choose>
					<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
						datas[${s.count}][${t.index}] = "${selMap[f:h(e[f.attrid])]}";
					</c:when>
					<c:when test="${editClass[t.index] == 'Upload'}">
						<c:if test="${fn:length(e[f.attrid]) > 0}">
							datas[${s.count}][${t.index}] = "${f:h(e[f.attrid])}";
						</c:if>
					</c:when>
					<c:otherwise>
						datas[${s.count}][${t.index}] = "${f:h(e[f.attrid])}";
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:if>
	</c:forEach>

	//formのPOSTで文字列をJSON形式で送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('/page/list/outputpdf/')}";
		form.method = "POST";
		$('<input>').attr({"type":"hidden","name":"session_token","value":'${cookie.JSESSIONID.value}'}).appendTo(form);
		form.dataList.value = JSON.stringify(datas);
		form.submit();
	}while(0);
	$("#pdf-dialog").dialog("close");
}


</script>
<table border="0" cellpadding="3" cellspacing="2" id="editTable" class="tablesorter" style="margin-left:10px;">
	<thead>
	<tr>
<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		<c:set var="hi" value="" />
		<c:if test="${e.highlight}"><c:set var="hi" value="highlight" /></c:if>
		<th class="${hi}">
			${f:h(e.name)}<c:if test="${colMap[e.attrid] && e.editable}"><font style="color:FF0000"> ※</font></c:if>
		</th>
</c:forEach>
	</tr>
	</thead>
	<tbody>
<c:forEach var="e" varStatus="s" items="${result}">
	<c:if test="${!filterIds[f:h(e[key])]}">
		<tr class="gray" style="display:table-row;" >
	</c:if>
	<c:if test="${filterIds[f:h(e[key])]}">
		<tr>
	</c:if>

		<c:forEach var="f" varStatus="t" items="${colinfoItems}">
		<c:set var="hi" value="" />
		<c:if test="${f.highlight}"><c:set var="hi" value="highlight" /></c:if>
		<c:set var="st" value="" />
		<c:if test="${styleMap[f.id] != null}">
			<c:forEach var="g" varStatus="u" items="${styleMap[f.id]}">
				<c:if test="${g.val == e[f.attrid]}">
					<c:set var="st" value="${f:h(g.style)}" />
				</c:if>
			</c:forEach>
		</c:if>

		<c:set var="selMap" value="${selectValView[f.attrid]}" />
		<c:choose>
			<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
				<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${hi}" style="${st}">${selMap[f:h(e[f.attrid])]}</td>
			</c:when>
			<c:otherwise>
				<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${hi}" style="${st}">${f:h(e[f.attrid])}</td>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</tr>
</c:forEach>
	</tbody>
</table>

<%-- CSVファイル出力 --%>
<div id="csv-dialog" title="<%=lang.__("CSV file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as CSV file.")%><br>
		<%=lang.__("* In case of filtering settings configured, only searched data (not in gray color)  are displayed.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputcsv" modelAttribute="listForm">
			<% FormUtils.printToken(out, request); %>
			<input type="hidden" name="value" value="" >
			<a href='javascript:outputCSV(document.getElementById("outputcsv"));' class="dialog-button" ><%=lang.__("CSV output")%></a>
		</form:form>
	</div>
</div>

<%-- PDFファイル出力 --%>
<div id="pdf-dialog" title="<%=lang.__("PDF file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as PDF file.")%><br>
		<%=lang.__("* In case of filtering settings configured, only searched data (not in gray color)  are displayed.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputpdf" modelAttribute="listForm">
			<% FormUtils.printToken(out, request); %>
			<input type="hidden" name="dataList" value="">
			<input type="hidden" name="menuid" value="${menuid}">
			<input type="hidden" name="menutaskid" value="${menutaskid}">
			<a href='javascript:outputPDF(document.getElementById("outputpdf"));' class="dialog-button" ><%=lang.__("PDF output")%></a>
		</form:form>
	</div>
</div>
</tiles:putAttribute>
</tiles:insertTemplate>

