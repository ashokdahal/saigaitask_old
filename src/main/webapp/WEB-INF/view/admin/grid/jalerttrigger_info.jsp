<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function jalerttrigger_info(subgrid_id, row_id){
	var tabName = 'jalerttrigger_info';
	var jalerttrigger_info_gridId = 'main';
	if(subgrid_id != null){
		jalerttrigger_info_gridId = subgrid_id;
	}
	var jalerttrigger_info_tabId = tabName;
	if(row_id != null){
		jalerttrigger_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'jalertrequest_info', 'jalertrequestinfoid');

	var jalerttrigger_info_pagerId = jalerttrigger_info_tabId+'Nav';
	$('#'+jalerttrigger_info_gridId).append('<table id="'+jalerttrigger_info_tabId+'" class="scroll"></table><div id="'+jalerttrigger_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+jalerttrigger_info_tabId).jqGrid({
		caption: '<%=lang.__("JAlert info trigger info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("JAlert info acquisition ID")%>',
			'<%=lang.__("Trigger")%>',
			'<%=lang.__("Disaster mode activation flag")%>',
			'<%=lang.__("Notification group ID")%>',
			'<%=lang.__("System ID")%>',
			'<%=lang.__("Assemble staff e-mail send flag")%>',
			'<%=lang.__("Evacuation advisory table")%>',
			'<%=lang.__("Evacuation advisory attribute item")%>',
			'<%=lang.__("Evacuation advisory string")%>',
			'<%=lang.__("L-Alert send flag")%>',
			'<%=lang.__("District mail send flag")%>',
			'<%=lang.__("SNS send flag")%>',
			'<%=lang.__("Layer add flag")%>',
			'<%=lang.__("Notes")%>',
			'<%=lang.__("Valid / invalid")%>',
		],
		colModel:[
			{name:'id',
				width:'60',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:true
					,style:'ime-mode:inactive'
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'ID'},
				hidden:false},
			{name:'jalertrequestinfoid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:row_id,
					delimiter:'|d|',
					readonly:true

				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("JAlert info acquisition ID(*)")%>'},
				hidden:false},
			{name:'trigger',
				width:'150',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Trigger(*)")%>'},
				hidden:false},
			{name:'startup',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'false',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Disaster mode activation flag(*)")%>'},
				hidden:false},
			{name:'noticegroupinfoid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/noticegroupinfoid/noticegroup_info/id/name',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+jalerttrigger_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+jalerttrigger_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.noticegroupinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'jalerttrigger_info', 'noticegroup_info', false);
						}
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Notification group ID")%>'},
				hidden:false},
			{name:'stationclassinfoid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/stationclassinfoid/stationclass_info/id/name',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+jalerttrigger_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+jalerttrigger_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.stationclassinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'jalerttrigger_info', 'stationclass_info', false);
						}
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("System ID")%>'},
				hidden:false},
			{name:'assemblemail',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Assemble staff e-mail send flag(*)")%>'},
				hidden:false},
			{name:'issuetablemasterinfoid',
				width:'150',
				align:'right',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
					,style:'ime-mode:inactive'
				},
				editrules:{required:false, number:false, integer:true, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Evacuation advisory table")%>'},
				hidden:false},
			{name:'issueattrid',
				width:'150',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
					,style:'ime-mode:inactive'
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Evacuation advisory attribute item")%>'},
				hidden:false},
			{name:'issuetext',
				width:'150',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Evacuation advisory string")%>'},
				hidden:false},
			{name:'publiccommons',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("L-Alert send flag(*)")%>'},
				hidden:false},
			{name:'publiccommonsmail',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("District mail send flag(*)")%>'},
				hidden:false},
			{name:'sns',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("SNS send flag(*)")%>'},
				hidden:false},
			{name:'addlayer',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Layer add flag(*)")%>'},
				hidden:false},
			{name:'note',
				width:'150',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'textarea',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
					,style:'ime-mode:active'
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Notes")%>'},
				hidden:false},
			{name:'valid',
				width:'130',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'<%=lang.__("True: enable; false: disable")%>',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Valid / invalid(*)")%>'},
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
		subGrid: false,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: jalerttrigger_info_pagerId,
		autowidth: true,
		autoencode: true,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].jalertrequestInfo;
				if(ref != null){
					var refkey = ref.id;
					str += refkey + ':' + refkey;
				}

			}
			$('#'+jalerttrigger_info_tabId).jqGrid('setColProp', 'jalertrequestinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].noticegroupInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+jalerttrigger_info_tabId).jqGrid('setColProp', 'noticegroupinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].stationclassInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+jalerttrigger_info_tabId).jqGrid('setColProp', 'stationclassinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+jalerttrigger_info_tabId).getDataIDs();
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
	$('#'+jalerttrigger_info_tabId).jqGrid('navGrid',
			'#'+jalerttrigger_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - JAlert info trigger info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+jalerttrigger_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+jalerttrigger_info_tabId).jqGrid('getRowData', rowid);
					s += '&jalertrequestinfoid='+rowdata.jalertrequestinfoid;
					var rowdata = $('#'+jalerttrigger_info_tabId).jqGrid('getRowData', rowid);
					s += '&noticegroupinfoid='+rowdata.noticegroupinfoid;
					var rowdata = $('#'+jalerttrigger_info_tabId).jqGrid('getRowData', rowid);
					s += '&stationclassinfoid='+rowdata.stationclassinfoid;
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_jalertrequestinfoid .CaptionTD', formid).after('<td id="dispjalertrequestinfoid">'+row_id+'</td>');
					$('#tr_jalertrequestinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+jalerttrigger_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_jalertrequestinfoid #dispjalertrequestinfoid', formid).remove();
					$('#tr_jalertrequestinfoid .CaptionTD', formid).after('<td id="dispjalertrequestinfoid">'+row_id+'</td>');
					$('#tr_jalertrequestinfoid .DataTD', formid).hide();
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
				width:'auto',
				addCaption:'<%=lang.__("Add - JAlert info trigger info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+jalerttrigger_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_jalertrequestinfoid .CaptionTD', formid).after('<td id="dispjalertrequestinfoid">'+row_id+'</td>');
					$('#tr_jalertrequestinfoid .DataTD', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - JAlert info trigger info")%>',
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
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
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
				sopt:['eq','ne','lt','le','gt','ge','bw','bn','ew','en','cn','nc'],
				modal:true,
				closeOnEscape:true
			}
		//コピーボタン
		).jqGrid(
			'navButtonAdd',
			'#'+jalerttrigger_info_pagerId,
			{
				id:'copy_jalerttrigger_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+jalerttrigger_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+jalerttrigger_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - JAlert info trigger info")%>',
									width:'auto',
									modal:true,
									closeAfterAdd:true,
									closeOnEscape:true,
									zIndex: 100,
									beforeShowForm:function(formid){
										//フォームモードにコピーモードを設定
										$(formid).after('<span id="myEditMode" style="display:none">copy</span>');
										//idは非表示。
										$('#tr_id', formid).hide();
										$('#tr_jalertrequestinfoid .CaptionTD', formid).after('<td id="dispjalertrequestinfoid">'+row_id+'</td>');
										$('#tr_jalertrequestinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+jalerttrigger_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+jalerttrigger_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ jalerttrigger_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_jalerttrigger_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+jalerttrigger_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+jalerttrigger_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');



}
