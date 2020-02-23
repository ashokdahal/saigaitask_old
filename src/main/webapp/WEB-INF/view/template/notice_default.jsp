<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../common/jsp_lang.jsp" %>
//<script type="text/javascript">

//通知デフォルト追加画面の初期化
function init_notice_default() {
	var tabName = 'notice_default';
	var gridId = 'default_grid';
	var tableId = tabName;

	$('#'+gridId).append('<table id="'+tableId+'" class="table01"></table></div>');

	//グリッド
	$('#'+tableId).jqGrid({
		mtype: 'POST',
		url:'${f:url("/template/jqgriddefindex/")}',
		datatype: 'json',
		postData: {
			menuinfoid: ${f:h(templateForm.menuinfoid)}
		},
		colModel:[
			{
				name:'tempid',
				align:'center',
				edittype:'text',
				editable:false,
				sortable:false,
				search:false,
				hidden:false
			},
			{
				name:'noticetypeid',
				align:'left',
				editable:false,
				sortable:false,
				search:false,
				hidden:false,
			},
			{
				name:'templateclass',
				align:'left',
				edittype:'text',
				editable:false,
				sortable:false,
				search:false,
				hidden:false
			},
		],
		loadtext:'',
		rowNum:100000,
		height: 'auto',
		multiselect: false,
	 	viewrecords: true,
		cellsubmit: 'clientArray',
		subGrid: false,
	});	//グリッドend

	//「登録」ボタン処理
	$("#registdef").click(function(e) {
		var choice = $("input:radio[name='templateid']:checked").val();
		if (!choice) {
			alert('<%=lang.__("Select item associated with menu.")%>');
			return;
		}
		choice = JSON.parse(choice);
		var data = {
			'oper' : 'add',
			menuinfoid: ${f:h(templateForm.menuinfoid)},
			noticetemplatetypeid: choice.noticetemplatetypeid,
			templateclass: choice.templateclass
		};
		$.ajax({
			url: '${f:url("/template/jqgrideditdef/")}',
			dataType : "json",
			data: data,
			timeout: 10000,
			type: 'POST',
			success: function (data) {
				$("#default").remove();
				init_template();
				$("#main").show();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("ERROR: " + errorThrown);
			}
		});
	});
}
