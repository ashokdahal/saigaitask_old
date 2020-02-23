<%@page import="org.seasar.framework.beans.util.Beans"%>
<%@page import="jp.ecom_plat.saigaitask.form.admin.setupper.LocalgovGroupForm"%>
<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<c:set var="isList"   value="${empty localgovGroupForm.id}"/>
<c:set var="isInsert" value="${isList==false && localgovGroupForm.id=='0'}"/>
<c:set var="isUpdate" value="${isList==false && isInsert==false}"/>

<script type="text/javascript">
var isList   = ${isList};
var isInsert = ${isInsert};
var isUpdate = ${isUpdate};
$(function() {
	$(".contents_localgovGroup_form").each(function(){
		var form = $(this);
		
		// 一覧表示の場合
		if(isList) {
			// 入力フォームを解除してテキストだけにする。
			$("input,select,textarea", form).each(function() {
				var input = $(this);
				var tagName = input.prop("tagName");
				var type = input.attr("type");

				if(tagName=="INPUT") {
					// 編集ボタン、削除ボタンの場合は何もしない
					if(type=="button") {
						var name = input.attr("name");
						if(name=="edit" || name=="delete") {
							return;
						}
					}
					// hidden も何もしない
					else if(type=="hidden") {
						return;
					}
				}
				
				// そのほかの入力フィールドは閲覧モードに変更する。
				var text = input.val();
				if(tagName=="SELECT") text = $("option:selected", input).text();
				if(tagName=="INPUT" && type=="radio") {
					if(input.is(":checked")==false) return; // 未チェックなら何もしない。
					text = $("label[for="+input.attr("id")+"]").text();
				}
				input.parent().text(text);
			});
			//子組織テーブルのレイアウト調整
			var localgovGroupMemberTable = $("table.localgovGroupMemberTable");
			localgovGroupMemberTable.css("border", "0");
			$("tr,td,th", localgovGroupMemberTable).css("border", "0").css("padding", "0");
			
			
		}
	});
});

/**
 * 自治体グループ登録/編集画面で保存前に入力チェックする関数
 */
function validLocalgovGroupForm() {
	var form = $(".contents_localgovGroup_form");
	
	var messages = [];
	
	// 自治体グループ名
	if($("[name='name']", form).val().length==0) {
		messages.push(lang.__('自治体グループ名を入力してください。'));
	}
	
	// 親組織
	var parentSelect = $("[name='localgovinfoid']", form);
	if(parentSelect.val()==0) {
		messages.push(lang.__('親組織が選択されていません。'));
	}
	
	// 親組織と子組織で重複した自治体IDがないかチェック
	// 親組織と子組織の自治体IDを取得
	var localgovinfoids = $("select[name=localgovinfoid],select.member-localgovinfoid", form)
	.map(function(){return $(this).val();}).get();
	// 重複をチェック
	var duplicates = [];
	var uniqueLocalgovinfoids = localgovinfoids.filter(function(x,i,self) {
		if(x=="0") return false;  // 0:未指定は重複チェックしない
		var isUnique = self.indexOf(x)===i;
		if(!isUnique) {
			if(duplicates.indexOf(x)==-1) {
				duplicates.push(x);
			}
		}
		return isUnique;
	});
	// 子組織チェック
	if(uniqueLocalgovinfoids.length<=(parentSelect.val()!=0?1:0)) {
		messages.push(lang.__('子組織が選択されていません。'));
	}
	
	var childSelect = $(".member-localgovinfoid", form).eq(0);
	if(0<duplicates.length) {
		messages.push(lang.__('下記の組織が重複して設定されています。'));
		for(var key in duplicates) {
			var duplicate = duplicates[key];
			var label = $("option[value="+duplicate+"]", childSelect).text();
			messages.push("    "+label);
		}
	}
	
	// 表示順
	if($("[name='disporder']", form).val().length==0) {
		messages.push(lang.__('表示順を数値で入力してください。'));
	}
	
	
	
	// 入力エラー
	if(0<messages.length) {
		alert(lang.__("入力エラーがあります。")+"\n"+messages.join("\n"));
		return false	;
	}
	
	return true;
}

/**
 * 自治体グループ登録/編集画面で「子組織を追加」ボタンの処理
 */
function addGroupMember() {
	//子組織テーブルのレイアウト調整
	var localgovGroupMemberTable = $("table.localgovGroupMemberTable");
	var templateTR = $("tr:last", localgovGroupMemberTable);
	var cloneTR = templateTR.clone(true);

	// 自治体グループメンバー情報ID
	var idInput = $("input.id", cloneTR);
	idInput.val(0); // 新規登録なので 0
	// nameのインデックス番号を調整
	var name = idInput.attr("name");
	var nameIdx = Number(name.substring("members[".length, name.indexOf("]")))+1; // +1する
	idInput.attr("name", "members["+nameIdx+"].id");
	

	// 子組織セレクトの初期状態をセット
	var memberLocalgovinfoidSelect = $("select", cloneTR);
	memberLocalgovinfoidSelect.val(0);
	// nameのインデックス番号を調整
	memberLocalgovinfoidSelect.attr("name", "members["+nameIdx+"].localgovinfoid");

	// 表示順の初期状態をセット
	var disporderInput = $("input.disporder", cloneTR);
	var disporder = Number(disporderInput.val())+10;
	disporderInput.val(disporder);
	// nameのインデックス番号を調整
	disporderInput.attr("name", "members["+nameIdx+"].disporder");

	var disporderInput = $("input", cloneTR);
	// 最終行に追加
	localgovGroupMemberTable.append(cloneTR);
}

function deleteLocalgovGroup(button) {
	var form = $(button).parents('.contents_localgovGroup_form');
	return SaigaiTask.setupper.submitForm(form.attr('id'), '<%=lang._E("自治体グループを削除してよろしいですか？")%>');
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1>${isList ? lang.__('自治体グループ一覧') : isInsert ? lang.__('自治体グループ登録画面') : lang.__('自治体グループ編集画面')}
	<c:if test="${isList && loginDataDto.groupid==0}"><%-- システム管理者の場合は自治体IDフィルターを表示 --%>
	<select name="filterlocalgovinfoid" style="position: relative; top: -1px; font-size:12px;"
			onchange="location.href='${f:url('/admin/setupper/localgovGroup/?filterlocalgovinfoid=')}'+$(this).val()">
		<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
		<c:set var="selected" value="${localgovSelectOption.key==localgovGroupForm.filterlocalgovinfoid}"/>
		<option value="${localgovSelectOption.key}" ${selected?"selected":""}>${f:h(localgovSelectOption.key)}: ${f:h(localgovSelectOption.key==0 ? lang.__('すべて表示') : localgovSelectOption.value)}</option>
		</c:forEach>
	</select>
	</c:if>
	</h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">

<c:forEach var="group" items="${groups}">
<%
	LocalgovGroupForm form = (LocalgovGroupForm) request.getAttribute("localgovGroupForm");
	LocalgovGroupForm group = (LocalgovGroupForm) pageContext.getAttribute("group");
	Beans.copy(group, form).execute();
%>
<form:form
	id="contents_localgovGroup_form_${localgovGroupForm.id}"
	cssClass="contents_localgovGroup_form"
	method="post" enctype="multipart/form-data"
	cssStyle="width: 550px;"
	servletRelativeAction="/admin/setupper/localgovGroup/${isList?'delete':'save'}?_csrf=${_csrf.token}"
	modelAttribute="localgovGroupForm">
<div>
	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
    <c:forEach var="msg" items="${messages}">
		<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
	</c:forEach>
	<c:remove var="messages" scope="session"/>

</div>
	<c:set var="isGroupManager" value="${localgovGroupForm.localgovinfoid==loginDataDto.localgovinfoid}"/>
	<table border="1" class="form">
		<tr style="${isInsert ? 'display:none;' : ''}">
			<th style="width:150px;"><%=lang.__("自治体グループID")%></th>
			<td>${localgovGroupForm.id}<form:hidden path="id" size="10" readonly="true"/></td>
		</tr>
		<tr>
			<th><%=lang.__("自治体グループ名")%></th>
			<td><form:input path="name" size="50"/></td>
		</tr>
		<tr>
			<th><%=lang.__("親組織")%></th>
			<td>
				<c:set var="readonly" value="${loginDataDto.groupid!=0}"/>
				<select name="localgovinfoid" style="position: relative; top: -1px; font-size:12px;">
					<c:forEach var="localgovSelectOption" items="${localgovSelectOptionsAll}">
					<c:set var="selected" value="${localgovSelectOption.key==localgovGroupForm.localgovinfoid}"/>
					<c:if test="${readonly==false || selected}">
					<option value="${localgovSelectOption.key}" ${selected?"selected":""}>${f:h(localgovSelectOption.key)}: ${f:h(localgovSelectOption.value)}</option>
					</c:if>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<th><%=lang.__("子組織")%><br></th>
			<td>
				<c:if test="${fn:length(localgovGroupForm.members)==0}"><%=lang.__("子組織はありません。") %></c:if>
				<table border="1" class="form localgovGroupMemberTable" style="margin-bottom:0;">
					<c:if test="${isInsert||isUpdate}">
					<tr class="localgovGroupMemberTable-header">
						<th><%=lang.__("子組織")%></th>
						<th style="width:80px;"><%=lang.__("表示順")%></th>
					</tr>
					</c:if>
					
					<c:forEach var="member" items="${localgovGroupForm.members}" varStatus="status">
					<tr>
						<td><form:hidden path="members[${status.index}].id" cssClass="id" size="10"/>
							<select name="members[${status.index}].localgovinfoid" class="member-localgovinfoid" style="position: relative; top: -1px; font-size:12px;">
								<c:forEach var="localgovSelectOption" items="${localgovSelectOptionsAll}">
								<c:set var="selected" value="${localgovSelectOption.key==member.localgovinfoid}"/>
								<option value="${localgovSelectOption.key}" ${selected?"selected":""}>${f:h(localgovSelectOption.key)}: ${f:h(localgovSelectOption.value)}</option>
								</c:forEach>
							</select>
						</td>
						<c:if test="${isInsert||isUpdate}">
						<td><form:input path="members[${status.index}].disporder" cssClass="disporder" size="10"/></td>
						</c:if>
					</tr>
					</c:forEach>

				</table>
				<c:if test="${isInsert || isUpdate}">
					<div style="text-align:right;"><input type="button" value="<%=lang.__("子組織を追加")%>" style="padding: 0px 5px;" onclick="addGroupMember()"/></div>
					<span><%=lang.__("※子組織から削除する場合は未指定を選択してください。")%></span>
				</c:if>
			</td>
		</tr>
		<tr>
			<th><%=lang.__("表示順")%></th>
			<td><form:input path="disporder" size="10"/></td>
		</tr>
		<tr>
			<th><%=lang.__("有効・無効")%></th>
			<td><form:radiobutton path="valid" label="${lang.__('有効')}" value="true"/>&nbsp;<form:radiobutton path="valid" label="${lang.__('無効')}" value="false"/></td>
		</tr>
		<c:if test="${isList && (isGroupManager || loginDataDto.groupid==0) }">
		<tr>
			<td colspan="2">
				<input type="button" value="<%=lang.__("編集")%>" name="edit"   style="padding: 0px 5px;" onclick="SaigaiTask.setupper.loadContent('${f:url('/')}admin/setupper/localgovGroup/content?id=${localgovGroupForm.id}');"/>
				<input type="button" value="<%=lang.__("削除")%>" name="delete" style="padding: 0px 5px;" onclick="deleteLocalgovGroup(this);"/>
			</td>
		</tr>
		</c:if>
	</table>
</form:form>
</c:forEach>
</div>

<div id="content_footer" class="ui-layout-south">
	<c:if test="${isList}">
		<a href="#" class="btn blue" onclick="SaigaiTask.setupper.loadContent('${f:url('/')}admin/setupper/localgovGroup/content?id=0');"><%=lang.__("グループの追加")%></a>
	</c:if>
	<c:if test="${isInsert || isUpdate}">
		<a href="#" class="btn blue" onclick="if(validLocalgovGroupForm()) return SaigaiTask.setupper.submitForm($('.contents_localgovGroup_form').attr('id'), isInsert ? '<%=lang._E("自治体グループを作成してよろしいですか？")%>' : '<%=lang._E("保存してよろしいですか？")%>');"><%=lang.__("保存")%></a>
	</c:if>
</div>

