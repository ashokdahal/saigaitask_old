<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function publiccommons_report_shelter_info(subgrid_id, row_id){
	var tabName = 'publiccommons_report_shelter_info';
	var publiccommons_report_shelter_info_gridId = 'main';
	if(subgrid_id != null){
		publiccommons_report_shelter_info_gridId = subgrid_id;
	}
	var publiccommons_report_shelter_info_tabId = tabName;
	if(row_id != null){
		publiccommons_report_shelter_info_tabId += row_id;
	}else{
		row_id = '';
	}

	var publiccommons_report_shelter_info_pagerId = publiccommons_report_shelter_info_tabId+'Nav';
	$('#'+publiccommons_report_shelter_info_gridId).append('<table id="'+publiccommons_report_shelter_info_tabId+'" class="scroll"></table><div id="'+publiccommons_report_shelter_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+publiccommons_report_shelter_info_tabId).jqGrid({
		caption: '<%=lang.__("L-Alert shelter info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Local gov. ID")%>',
			'<%=lang.__("Table ID")%>',
			'<%=lang.__("Shelter name")%>',
			'<%=lang.__("District")%>',
			'<%=lang.__("Address")%>',
			'<%=lang.__("Phone number")%>',
			'FAX',
			'<%=lang.__("Representative name")%>',
			'<%=lang.__("Shelter status")%>',
			'<%=lang.__("Capacity")%>',
			'<%=lang.__("Opening date and time")%>',
			'<%=lang.__("Closing date and time")%>',
			'<%=lang.__("Type<!--2-->")%>',
			'<%=lang.__("Coordinates<!--2-->")%>',
			'<%=lang.__("Total number of evacuees")%>',
			'<%=lang.__("Total number of evacuees (including voluntary evacuation)")%>',
			'<%=lang.__("Number of evacuated households")%>',
			'<%=lang.__("Number of evacuated households (including voluntary evacuation)")%>',
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
			{name:'localgovinfoid',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/localgovinfoid/localgov_info/id/id',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.localgovinfoid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'publiccommons_report_shelter_info', 'localgov_info');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Local gov. ID(*)")%>'},
			hidden:false},
			{name:'tablemasterinfoid',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/tablemasterinfoid/tablemaster_info/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.tablemasterinfoid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'publiccommons_report_shelter_info', 'tablemaster_info');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Table ID(*)")%>'},
			hidden:false},
			{name:'attrshelter',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Shelter name(*)")%>'},
			hidden:false},
			{name:'attrarea',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("District(*)")%>'},
			hidden:false},
			{name:'attraddress',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Address(*)")%>'},
			hidden:false},
			{name:'attrphone',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Phone number(*)")%>'},
			hidden:false},
			{name:'attrfax',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'FAX(*)'},
			hidden:false},
			{name:'attrstaff',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Representative name(*)")%>'},
			hidden:false},
			{name:'attrstatus',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Shelter status(*)")%>'},
			hidden:false},
			{name:'attrcapacity',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Capacity(*)")%>'},
			hidden:false},
			{name:'attrsetuptime',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Opening date and time(*)")%>'},
			hidden:false},
			{name:'attrclosetime',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Closing date and time(*)")%>'},
			hidden:false},
			{name:'attrtype',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Type(*)")%>'},
			hidden:false},
			{name:'attrcircle',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Coordinates(*)")%>'},
			hidden:false},
			{name:'attrheadcount',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Number of evacuees(*)")%>'},
			hidden:false},
			{name:'attrheadcountvoluntary',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Number of evacuees (including voluntary evacuation)(*)")%>'},
			hidden:false},
			{name:'attrhouseholds',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Number of evacuated households<!--2-->")%>'},
			hidden:false},
			{name:'attrhouseholdsvoluntary',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Number of evacuated households (including voluntary evacuation)(*)")%>'},
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
		pager: publiccommons_report_shelter_info_pagerId,
		autowidth: true,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += ';'
				}
				var ref = data.rows[i].localgovInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.pref + ref.city;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setColProp', 'localgovinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += ';'
				}
				var ref = data.rows[i].tablemasterInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setColProp', 'tablemasterinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+publiccommons_report_shelter_info_tabId).getDataIDs();
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
	$('#'+publiccommons_report_shelter_info_tabId).jqGrid('navGrid',
			'#'+publiccommons_report_shelter_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - L-Alert shelter info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getRowData', rowid);
					s += '&localgovinfoid='+rowdata.localgovinfoid;
					var rowdata = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getRowData', rowid);
					s += '&tablemasterinfoid='+rowdata.tablemasterinfoid;
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
				},
				beforeInitData:function(formid){
					//「テーブルID」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$.ajaxSetup({ async: true });
					//「避難所名」～「避難所世帯数（うち自主避難）」に、属性名が一致した属性の値をセットする
					initPubliccommonsDialogTextInputColumn();
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+publiccommons_report_shelter_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - L-Alert shelter info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				beforeShowForm:function(formid){
					//行選択をはずす。
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
				},
				beforeInitData:function(formid){
					//「テーブルID」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$.ajaxSetup({ async: true });
					//「避難所名」～「避難所世帯数（うち自主避難）」に、属性名が一致した属性の値をセットする
					initPubliccommonsDialogTextInputColumn();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - L-Alert shelter info")%>',
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
								$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+publiccommons_report_shelter_info_pagerId,
			{
				id:'copy_publiccommons_report_shelter_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+publiccommons_report_shelter_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+publiccommons_report_shelter_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - L-Alert shelter info")%>',
									bSubmit:'<%=lang.__("Save")%>',
									width:'auto',
									modal:true,
									closeAfterAdd:true,
									closeOnEscape:true,
									beforeShowForm:function(formid){
										//idは非表示。
										$('#tr_id', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+publiccommons_report_shelter_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+publiccommons_report_shelter_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ publiccommons_report_shelter_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_publiccommons_report_shelter_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+publiccommons_report_shelter_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+publiccommons_report_shelter_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	// 公共情報コモンズ避難勧告情報・避難所情報の追加・編集ダイアログの属性名テキストエリアの初期化
	function initPubliccommonsDialogTextInputColumn() {
		$('#tablemasterinfoid').on('change', function(){loadPubliccommonsDialogTextInputColumn(tabName)});
		loadPubliccommonsDialogTextInputColumn(tabName);
}
}
