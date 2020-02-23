<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function tabledatagroupdata_info(){
	tabledatagroupdata_info("","")
}

function tabledatagroupdata_info(subgrid_id, row_id){
	var tabName = 'tabledatagroupdata_info';
	var tabledatagroupdata_info_gridId = 'main';
	if(subgrid_id != null){
		tabledatagroupdata_info_gridId = subgrid_id;
	}
	var tabledatagroupdata_info_tabId = tabName;
	if(row_id != null){
		tabledatagroupdata_info_tabId += row_id;
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'ERROR', 'ERROR');

	var tabledatagroupdata_info_pagerId = tabledatagroupdata_info_tabId+'Nav';
	$('#'+tabledatagroupdata_info_gridId).html('<table id="'+tabledatagroupdata_info_tabId+'" class="scroll"></table><div id="'+tabledatagroupdata_info_pagerId+'" class="scroll"></div>');

	//グリッド
	$('#'+tabledatagroupdata_info_tabId).jqGrid({
		caption: '<%=lang.__("Table list group data")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("List group ID")%>',
			'<%=lang.__("Data ID")%>',
		],
		colModel:[
			{name:'id',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'text',
			editable:true,
			editoptions:{value:'', defaultValue:'', readonly:true
			},
			editrules:{required:false, number:false, integer:false, email:false},
			sortable:true,
			sorttype:'int',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'ID'},
			hidden:false},
			{name:'tablelistgroupinfoid',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'', defaultValue:'', readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/tablelistgroupinfoid/tabledatagroup_info/id/name',
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var s = '<select>';
						if (selectTag && selectTag.length) {
							for (var i = 0; i < selectTag.length; i++) {
								//サブグリッドかつ親グリッドの選択ID
								if(row_id != null && selectTag[i].key == row_id){
									s += '<option value="'+ selectTag[i].key+'" selected>'+selectTag[i].value+'</option>';
								}else{
									s += '<option value="'+ selectTag[i].key+'">'+selectTag[i].value+'</option>';
								}
							}
						}
						return s + '</select>';
					}
			},
			editrules:{required:false, number:false, integer:false, email:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("List group ID")%>'},
			hidden:false},
			{name:'dataid',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'', defaultValue:'', readonly:false
			},
			editrules:{required:false, number:false, integer:false, email:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Data ID")%>'},
			hidden:false},
		],
		ajaxGridOptions :{
			beforeSend: function(jqXHR) {
				var csrf_token = '${_csrf.token}';
				jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
			}
		},
		loadtext:'',
		loadui:'enable',
		loadonce:false,
		rowNum:30,
		rowList:[5,10,30,50,100],
		height: 'auto',
		multiselect: false,
		editurl:convertTabNameToMethodMame(tabName)+'/jqgridedit/',
		cellEdit: false,
		cellsubmit: 'clientArray',
		sortname: 'id',
		sortorder: 'ASC',
		subGrid: true,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: tabledatagroupdata_info_pagerId,
		beforeProcessing:function(data){
			//selectタグ項目の表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += ';'
				}
				var ref = data.rows[i].tabledatagroupInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}
			}
			$('#'+tabledatagroupdata_info_tabId).jqGrid('setColProp', 'tablelistgroupinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//幅指定
			$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridWidth', 1024, true);
			//1行おきに背景色をかえる
			var rowIDs = $('#'+tabledatagroupdata_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
		},
		'loadError' : function (response, status, error){
			//Ajaxエラー時、error.jspを出力。
			var errorpage = response.responseText;
			window.top.document.body.innerHTML = errorpage;
		},
		ajaxSelectOptions :{
			error:function (response, status, error){
					//Ajaxエラー時、error.jspを出力。
					var errorpage = response.responseText;
					window.top.document.body.innerHTML = errorpage;
			}
		},
	});	//グリッドend

	//ナビゲーション
	$('#'+tabledatagroupdata_info_tabId).jqGrid('navGrid',
			'#'+tabledatagroupdata_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
				var s = "";
					var rowid = $('#'+tabledatagroupdata_info_tabId).jqGrid('getGridParam', 'selrow');
					var rowdata = $('#'+tabledatagroupdata_info_tabId).jqGrid('getRowData', rowid);
					s += '&tablelistgroupinfoid='+rowdata.tablelistgroupinfoid;
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+tabledatagroupdata_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+tabledatagroupdata_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){

					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});

					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								alert(res.message);
							}
						}
					}
					var pagenum = $('#'+tabledatagroupdata_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+tabledatagroupdata_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					return [true,''];
				},
				ajaxEditOptions :{
					beforeSend: function(jqXHR) {
//						var csrf_token = '${cookie.JSESSIONID.value}';
						var csrf_token = '${_csrf.token}';
						jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
					},
					error:function (response, status, error){
							//Ajaxエラー時、error.jspを出力。
							var errorpage = response.responseText;
							window.top.document.body.innerHTML = errorpage;
						}
				},
				reloadAfterSubmit:true
			},
			//追加フォームオプション設定
			{recreateForm: true,
				addCaption:'<%=lang.__("Add<!--2-->")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				beforeSubmit:function(postdata, formid){
						return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
				},
				ajaxEditOptions :{
					beforeSend: function(jqXHR) {
//						var csrf_token = '${cookie.JSESSIONID.value}';
						var csrf_token = '${_csrf.token}';
						jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
					},
					error:function (response, status, error){
							//Ajaxエラー時、error.jspを出力。
							var errorpage = response.responseText;
							window.top.document.body.innerHTML = errorpage;
						}
				},
				reloadAfterSubmit:true
			},
			//削除フォームオプション設定
			{
				cption:'<%=lang.__("Delete")%>',
				bSubmit:'<%=lang.__("Delete")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				afterComplete:function(response, postdata, formid){
					//サブミット後、削除処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								alert(res.message);
							}
						}
					}
					return [true,''];
				},
				ajaxDelOptions  :{
					beforeSend: function(jqXHR) {
//						var csrf_token = '${cookie.JSESSIONID.value}';
						var csrf_token = '${_csrf.token}';
						jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
					},
					error:function (response, status, error){
							//Ajaxエラー時、error.jspを出力。
							var errorpage = response.responseText;
							window.top.document.body.innerHTML = errorpage;
						}
				},
			},
			//検索時フォームオプション設定
			{recreateForm: true,
			sopt:['eq','ne','lt','le','gt','ge','bw','bn','ew','en','cn','nc']}
		//コピーボタン
		).jqGrid(
			'navButtonAdd',
			'#'+tabledatagroupdata_info_pagerId,
			{
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+tabledatagroupdata_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+tabledatagroupdata_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy")%>',
									bSubmit:'<%=lang.__("Save")%>',
									closeAfterAdd:true,
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',{datatype:'json'});
										return [true,''];
									},
									ajaxEditOptions :{
										beforeSend: function(jqXHR) {
//											var csrf_token = '${cookie.JSESSIONID.value}';
											var csrf_token = '${_csrf.token}';
											jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
										}
									}
								}
							);
						//グリッドの選択列データをコピーフォームへデフォルト値としてセットする。
						$('#'+tabledatagroupdata_info_tabId).jqGrid('GridToForm', rowid, $('FrmGrid_'+ tabledatagroupdata_info_tabId).id);
					}else{
						alert('<%=lang.__("Select target data")%>');
					}
				}
			}
	);	//グリッドナビゲーションend

	//サブグリッド設定
	$('#'+tabledatagroupdata_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				ERROR(subgrid_id, row_id);
			}}
	);	//サブグリッドend


}
