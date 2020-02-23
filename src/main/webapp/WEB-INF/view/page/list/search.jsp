<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<style type="text/css">
<!--
#searchTable TD {
	text-align:left;
}
//-->
</style>

<%@include file="../../common/lang_resource.jsp" %>
<script type="text/javascript" src="${f:url('/js/saigaitask.detail.js')}"></script>
<script type="text/javascript">
$(function() {
	$("#searchTable").tablesorter({
		widgets: ['zebra'],
		headers: {
			0:{sorter:false},
			1:{sorter:false},
			2:{sorter:false},
			3:{sorter:false},
		}
	});

	//x$('input[type="button"]').button();

	$('.datepicker').datepicker({changeYear:true,yearRange:'c-100:c+10'});
	$('.datetimepicker').datetimepicker({controlType: 'select'});

	// ダイアログにボタン追加
	var form = null;
	var searchButtons = {
		"<%=lang.__("Reset")%>": function() {
			$("#search-dialog input[type=reset]").click();
			$('input:checkbox').removeAttr('checked');
			$('input:radio').removeAttr('checked');
		},
		"<%=lang.__("show all results")%>": function() {
			$("#search-dialog input[name=searchall]").click();
		},
		"<%=lang.__("Search")%>": function() {
			$("#search-dialog input[name=search]").click();
		},
		"<%=lang.__("Close")%>": function() {
			winclose();
		}
	};
	$('#search-dialog').dialog('option', 'buttons', searchButtons);


	<c:forEach var="f" varStatus="t" items="${listForm.saveDatas}">
	<c:if test="${f.value!=''}">
		if ($("input[type='radio'][name='${f.id}'][value='${f:h(f.value)}']").length > 0)
			$("input[type='radio'][name='${f.id}'][value='${f:h(f.value)}']").attr("checked", true );
		else if ($("select[name='${f.id}']").length > 0)
			$("select[name='${f.id}']").val("${f:h(f.value)}");
		else if ($("input[type='checkbox'][name='${f.id}']").length > 0)
			$("input[type='checkbox'][name='${f.id}']").attr("checked", true );
		else
			$("input[name='${f.id}']").val("${f:h(f.value)}");
		//$("checkbox[name='${f.id}']").attr('checked', true);
	</c:if>
	</c:forEach>
});

function searchData(form) {

	var url = "${f:url('/page/list/search')}/${listForm.menutaskid}/${listForm.menuid}/${listForm.pagetype}";
	// 送信データを生成
	var data = {
		saveDatas: []
	};

	var array = [];
	for (var i = 0; i < form.length; i++) {
        var elem = form.elements[i];
        if (typeof elem.name !== "undefined") {
        	if (!SaigaiTask.Detail.checkDetailData(elem.className, elem.value)) return false;

        	var ids = elem.name.split(":");
	        if (elem.type=="checkbox" && !elem.checked) continue;
	        if (elem.type=="radio" && !elem.checked) continue;
	        if (ids.length == 4) {
	    		data.saveDatas.push({
	    			id: elem.name,
	    			value: elem.value
	    		});
	        }
        }
    }

	$('#content').mask("Loading...");
	// 時間パラメータの付与
	var time = SaigaiTask.PageURL.getTime();
	if(!!time) {
		var iso8601Time = time.toISOString();
		// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
		if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
			iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
		}
		url += "?time="+iso8601Time;
	}
	// 変更登録
	$.ajax({
		dataType: "json",
		type: "POST",
		cache: false,
		url: url,
		data: SaigaiTask.Edit.toSAStrutsParam(data),
		success: function(msg) {
			$('#search-dialog').dialog("close");
			$("#filter-gray-ck-div").css("display", "inline");

			reloadlist(msg);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("error : <%=lang.__("Search failed")%>");
			$('#content').unmask();
		}
	});
}

function searchAllData(form) {
	form.reset();
	$('input:checkbox').removeAttr('checked');
	$('input:radio').removeAttr('checked');
	searchData(form);
}

function winclose() {
	$('#search-dialog').dialog("close");
}
</script>
<div>

<form:form>

<div>

<table border="0" cellpadding="3" cellspacing="2" id="searchTable" class="tablesorter" style="margin-left:10px;width:auto;">
	<thead>
		<tr>
			<th><%=lang.__("Item name")%></th>
			<th><%=lang.__("Value")%></th>
			<th><%=lang.__("Item name")%></th>
			<th><%=lang.__("Value")%></th>
		</tr>
	</thead>

	<tbody id="tablebody">
<c:set var="gid" value=""/>

	<c:forEach var="f" varStatus="t" items="${colinfoItems}">
	<c:if test="${t.index%2==0}">
		<tr>
	</c:if>
		<%-- データ変換 --%>

		    <td><label for="name">${f:h(f.name)}:</label></td>
		    <td nowrap>
		    <c:if test="${editClass[t.index] == 'String'}">
		    	<input type="text" name="${table}:${f.attrid}:${key}:0" size="20" id="${table}_${f.attrid}_${key}_0" class="${editClass[t.index]}"/>
		    </c:if>
		    <c:if test="${editClass[t.index] == 'Date'}">
		    	<input type="text" name="${table}:${f.attrid}:${key}:1" size="15" id="${table}:${f.attrid}:${key}:1" class="${editClass[t.index]} datepicker"/>～<br><input type="text" name="${table}:${f.attrid}:${key}:2" size="15" id="${table}:${f.attrid}:${key}:2" class="${editClass[t.index]} datepicker"/>
		    </c:if>
		    <c:if test="${editClass[t.index] == 'DateTime'}">
		    	<input type="text" name="${table}:${f.attrid}:${key}:1" size="20" id="${table}:${f.attrid}:${key}:1" class="${editClass[t.index]} datetimepicker"/>～<br><input type="text" name="${table}:${f.attrid}:${key}:2" size="20" id="${table}:${f.attrid}:${key}:2" class="${editClass[t.index]} datetimepicker"/>
		    </c:if>
		    <c:if test="${editClass[t.index] == 'Number' || editClass[t.index] == 'Float'}">
		    	<input type="text" name="${table}:${f.attrid}:${key}:1" size="5" id="${table}:${f.attrid}:${key}:1"  class="${editClass[t.index]}"/>～<input type="text" name="${table}:${f.attrid}:${key}:2" size="5" id="${table}:${f.attrid}:${key}:2" class="${editClass[t.index]}"/>
		    </c:if>
		    <c:if test="${editClass[t.index] == 'TextArea'}">
				<textarea cols="15" rows="2" name="${table}:${f.attrid}:${key}:0" id="textarea" class="${editClass[t.index]}"></textarea>
		    </c:if>
		    <c:if test="${editClass[t.index] == 'Select'}">
				<select name="${table}:${f.attrid}:${key}:0">
				<option value=""></option>
				<c:forEach var="g" varStatus="u" items="${selectStr[f.attrid]}">
					<c:set var="g2" value="${selectVal[f.attrid]}" />
					<option value="${f:h(g2[u.index])}">${f:h(g)}</option>
				</c:forEach>
				</select>
		    </c:if>
			<c:if test="${editClass[t.index] == 'Checkbox'}">
				<input type="radio" name="${table}:${f.attrid}:${key}:0" value="${f:h(checkStr[f.attrid])}"><%=lang.__("items selected")%>
				<input type="radio" name="${table}:${f.attrid}:${key}:0" value="<%=lang.__("None")%>"><%=lang.__("None")%>
			</c:if>
			<c:if test="${editClass[t.index] == 'Upload'}">
				<input type="text" disabled>
			</c:if>
			</td>

	<c:if test="${t.index%2==1 || t.index==fn:length(colinfoItems)}">
		</tr>
	</c:if>
	</c:forEach>
	</tbody>
</table>
	<%-- $("[name=searchall]").click() など送信のためにクリックで呼び出しているので、非表示のまま残す --%>
	<div style="text-align:right; display:none;">
		<input type="reset" name="" value="<%=lang.__("Reset")%>" />
		<input type="button" name="searchall" value="<%=lang.__("show all results")%>" onClick="searchAllData(this.form)" />
		<input type="button" name="search" value="<%=lang.__("Search")%>" onClick="searchData(this.form)" />
		<a href="javascript:winclose()" class="closebtn"><%=lang.__("Close")%></a>
	</div>

	</div>
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">

</form:form>
</div><!-- /page -->
