<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../common/jsp_lang.jsp" %>
//<script type="text/javascript">

//定型文一覧画面の初期化
function init_template() {
	var tabName = 'template';
	var gridId = 'main_grid';
	var tableId = tabName;

	$('#'+gridId).append('<table id="'+tableId+'" class="table01"></table>');

	//グリッド
	$('#'+tableId).jqGrid({
		mtype: 'POST',
		url:'${f:url("/template/jqgridindex/")}',
		datatype: 'json',
		width: '916',
		postData: {
			menuinfoid: ${f:h(templateForm.menuinfoid)},
			noticetypeid: ${f:h(templateForm.noticetypeid)}
		},
		colModel:[
			{
				name:'noticetypeid',
				align:'left',
				editable:false,
				sortable:false,
				search:false,
				hidden:false,
				cellattr: function (rowId, val, rawObject, cm) {
					//IE8 以下の場合
					if(!jQuery.support.opacity) {
						return '';
					}
					if (rawObject.rowspan != 0) {
						return ' rowspan="' + rawObject.rowspan + '"';
					}
					else {
						return ' style="display: none"';
					}
				}
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
				name:'title',
				align:'left',
				edittype:'text',
				editable:true,
				sortable:false,
				search:false,
				hidden:false
			},
			{
				name:'content',
				align:'left',
				edittype:'textarea',
				editable:true,
				sortable:false,
				search:false,
				hidden:false,
				editoptions: {
					rows: 4,
					dataEvents: [
						{
							type: 'keydown',
							fn: function(e) {
								//改行を入力可能にする
								if (e.charCode == 13 || e.keyCode == 13) {
									e.charCode = 0;
									e.keyCode = 0;
								}
							}
						}
					]
				}
			}
		],
		loadtext:'',
		rowNum:100000,
		height: 'auto',
		multiselect: false,
		editurl:'${f:url("/template/jqgridedit/")}',
	 	viewrecords: true,
		cellEdit: true,
		cellsubmit: 'clientArray',
		subGrid: false,
		ajaxGridOptions :{
			beforeSend: function(jqXHR) {
				var csrf_token = '${cookie.JSESSIONID.value}';
				jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
			}
		},
		afterSaveCell: function (rowid, name, val, iRow, iCol) {
			if (!confirm('<%=lang.__("Change template.")%>')) {
				var templateid = $("input:radio[name='templateid']:checked").val();
				$("#template").jqGrid('setGridParam',{
					datatype: 'json',
					postData: {
						menuinfoid: ${f:h(templateForm.menuinfoid)},
						templateid: templateid
					}
				}).trigger('reloadGrid');
				return;
			}
			var templateid = $('#templateid_' + rowid).val();
			var data = {
				'oper' : 'edit',
				'name' : name,
				'value' : val,
				'templateid' : templateid
			};
			$.ajax({
				url: '${f:url("/template/jqgridedit/")}',
				dataType : "json",
				data: data,
				timeout: 10000,
				type: 'POST',
				success: function (data) {
					$("#template").jqGrid('setGridParam',{
						datatype: 'json',
						postData: {
							menuinfoid: ${f:h(templateForm.menuinfoid)},
							templateid: data.newEntity.id
						}
					}).trigger('reloadGrid');
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					alert("ERROR: " + errorThrown);
				}
			});
		}
	});	//グリッドend

	//「追加」ボタン処理
	$("#template_add").click(function() {
		if (!confirm('<%=lang.__("Add template.")%>')) {
			retrun;
		}
		var userData = $("#template").jqGrid('getGridParam', 'userData');
		var data = {
			'oper' : 'add',
			menuinfoid: ${f:h(templateForm.menuinfoid)},
			noticetemplatetypeid: userData.noticetemplatetypeid,
			templateclass: userData.templateclass,
			noticetypeid: ${f:h(templateForm.noticetypeid)}
		};
		$.ajax({
			url: '${f:url("/template/jqgridedit/")}',
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			dataType : "json",
			data: data,
			timeout: 10000,
			type: 'POST',
			success: function (data) {
				if (data.message) {
					alert(data.message);
					return;
				}
				$("#template").jqGrid('setGridParam',{
					datatype: 'json',
					postData: {
						menuinfoid: ${f:h(templateForm.menuinfoid)},
						templateid: data.newEntity.id
					}
				}).trigger('reloadGrid');
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("ERROR: " + errorThrown);
			}
		});
	});

	//「コピー」ボタン処理
	$("#template_copy").click(function() {
		var templateid = $("input:radio[name='templateid']:checked").val();
		if (!templateid) {
			alert('<%=lang.__("Please choose a template.")%>');
			return;
		}
		if (!confirm('<%=lang.__("Copy template.")%>')) {
			retrun;
		}
		var data = {
			'oper' : 'add',
			'templateid' : templateid
		};
		$.ajax({
			url: '${f:url("/template/jqgridedit/")}',
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			dataType : "json",
			data: data,
			timeout: 10000,
			type: 'POST',
			success: function (data) {
				$("#template").jqGrid('setGridParam',{
					datatype: 'json',
					postData: {
						menuinfoid: ${f:h(templateForm.menuinfoid)},
						templateid: data.newEntity.id
					}
				}).trigger('reloadGrid');
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("ERROR: " + errorThrown);
			}
		});
	});

	//「削除」ボタン処理
	$("#template_del").click(function(e) {
		var templateid = $("input:radio[name='templateid']:checked").val();
		if (!templateid) {
			alert('<%=lang.__("Please choose a template.")%>');
			return;
		}
		if (!confirm('<%=lang.__("Delete template.")%>')) {
			retrun;
		}
		var data = {
			'oper' : 'del',
			'templateid' : templateid
		};
		$.ajax({
			url: '${f:url("/template/jqgridedit/")}',
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			dataType : "json",
			data: data,
			timeout: 10000,
			type: 'POST',
			success: function (data) {
				$("#template").jqGrid('setGridParam',{
					datatype: 'json',
					postData: {
						menuinfoid: ${f:h(templateForm.menuinfoid)},
						templateid: undefined
					}
				}).trigger('reloadGrid');
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("ERROR: " + errorThrown);
			}
		});
	});
}
