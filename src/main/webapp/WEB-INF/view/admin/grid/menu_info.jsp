<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
<%@page import="jp.ecom_plat.saigaitask.constant.Menutype"%>

function menu_info(subgrid_id, row_id){
	var tabName = 'menu_info';
	var menu_info_gridId = 'main';
	if(subgrid_id != null){
		menu_info_gridId = subgrid_id;
	}
	var menu_info_tabId = tabName;
	if(row_id != null){
		menu_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'menutasktype_info', 'menutasktypeinfoid');

	var menu_info_pagerId = menu_info_tabId+'Nav';
	$('#'+menu_info_gridId).append('<table id="'+menu_info_tabId+'" class="scroll"></table><div id="'+menu_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

    var excellistTrSelector = '#tr_excellisttemplatefile';
    excellistTrSelector    += ',#tr_excellistoutputtablemasterinfoid';
    excellistTrSelector    += ',#tr_excellistoutputtableregisttimeattrid';
    excellistTrSelector    += ',#tr_excellistoutputtabledownloadlinkattrid';

	//グリッド
	$('#'+menu_info_tabId).jqGrid({
		caption: '<%=lang.__("Menu info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Task type")%>',
			'<%=lang.__("Menu type")%>',
			'<%=lang.__("Name")%>',
			'<%=lang.__("Online help")%>',
			'<%=lang.__("Display / hide")%>',

			'<%=lang.__("Notes")%>',
			'<%=lang.__("Valid / invalid")%>',
			'<%=lang.__("Excel template file")%>',
			'<%=lang.__("excel list table ID")%>',
			'<%=lang.__("excel list registered date time attribution ID")%>',
			'<%=lang.__("excel list DL link attribution ID")%>'
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
			{name:'menutasktypeinfoid',
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
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Task type(*)")%>'},
			hidden:false},
			{name:'menutypeid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/menutypeid/menutype_master/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+menu_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+menu_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.menutypeid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'menu_info', 'menutype_master');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Menu type(*)")%>'},
			hidden:false},
			{name:'name',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Name(*)")%>'},
			hidden:false},
			{name:'helphref',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Online help")%>'},
			hidden:false},
			{name:'visible',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Display / hide(*)")%>'},
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
			{name:'excellisttemplatefile',
			width:'210',
			align:'left',
			//formatter: jgImageFormatter,
			edittype:'file',
			editable:true,
			editoptions:{dataEvents: [
//				{ type: 'change', fn: function(e) { $('#filepath').val($('#uploadFile').val()); } }
			]},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:true},
			sortable:false,
			search:false,
			hidden:true},
			{name:'excellistoutputtablemasterinfoid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/excellistoutputtablemasterinfoid/tablemaster_info/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+menu_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+menu_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.excellistoutputtablemasterinfoid
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'menu_info', 'tablemaster_info');
						//var ret = createSelectTag(rowid, selectTag, selectedId, 'menu_info', 'tablemaster_info');
						//// trigger change
						//$("#excellistoutputtablemasterinfoid").change();
						//return ret;
					}
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("excel list table ID(*)")%>'},
			hidden:true},
			{name:'excellistoutputtableregisttimeattrid',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'select',
			editable:true,
			editoptions:{value:':',
				defaultValue:'',
				readonly:false
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("excel list registered date time attribution ID(*)")%>'},
			hidden:true},
			{name:'excellistoutputtabledownloadlinkattrid',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'select',
			editable:true,
			editoptions:{value:':',
				defaultValue:'',
				readonly:false
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("excel list DL link attribution ID(*)")%>'},
			hidden:true}
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
		pager: menu_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].menutasktypeInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+menu_info_tabId).jqGrid('setColProp', 'menutasktypeinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].menutypeMaster;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+menu_info_tabId).jqGrid('setColProp', 'menutypeid', {editoptions:{value:str}});

			//selectタグのグリッド表示　※非表示なので単純にIDのみ表示
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].excellistoutputtablemasterinfoid;
				if(ref != null){
					var refkey = ref;
					var refval = ref;
					str += refkey + ':' + refkey;
				}

			}
			$('#'+menu_info_tabId).jqGrid('setColProp', 'excellistoutputtablemasterinfoid', {editoptions:{value:str}});
		},
		loadComplete : function (data) {
			console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+menu_info_tabId).getDataIDs();
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
	$('#'+menu_info_tabId).jqGrid('navGrid',
			'#'+menu_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+menu_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+menu_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Menu info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+menu_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+menu_info_tabId).jqGrid('getRowData', rowid);
					s += '&menutasktypeinfoid='+rowdata.menutasktypeinfoid;
					var rowdata = $('#'+menu_info_tabId).jqGrid('getRowData', rowid);
					s += '&menutypeid='+rowdata.menutypeid;
					$('#'+menu_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menutasktypeinfoid .CaptionTD', formid).after('<td id="dispmenutasktypeinfoid">'+row_id+'</td>');
					$('#tr_menutasktypeinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');

			        // メニュータイプがエクセル帳票の場合、ファイルアップロード要素を表示
			        if(rowdata.menutypeid === '<%= Menutype.EXCELLIST %>'){
			        	$(excellistTrSelector).toggle(true);
			        }else{
			        	$(excellistTrSelector).toggle(false);
			        }

					//外部地図表示レイヤ設定ボタンを追加
					$('<a id="calloutermapinfo_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("External map display layer settings")%><span class="ui-icon"></span></a>')
			              .insertAfter(formid);
					//内部地図表示レイヤ設定ボタンを追加
					$('<a id="callinnermapinfo_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Internal map layer info")%><span class="ui-icon"></span></a>')
			              .insertAfter(formid);
				},
				beforeInitData:function(formid){
					//「テーブルID」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$(formid).attr("enctype","multipart/form-data");
					$.ajaxSetup({ async: true });
					//「属性ID」のプルダウン作成
					initAttrSelect();
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+menu_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				beforeSubmit:function(postdata, formid){

					//新規登録の場合
					var rowid = $('#'+menu_info_tabId).jqGrid('getGridParam', 'selrow');
					var selectedId = null;
					if(rowid == null){
						// メニュータイプがエクセル帳票の場合、ファイルアップロード必須
						var selectedMenutypeId = $('#menutypeid').val();
						if(selectedMenutypeId === '<%= Menutype.EXCELLIST %>'){
							if (!($("#excellisttemplatefile").val() != "")) {
								return [false,"<%=lang.__("Excel template file: this item is required.")%>",""];
							}
						}
					}

					return [true,"",""];
				},
				afterSubmit:function(response, postdata){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+menu_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menu_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					uploadFile(response, postdata);

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+menu_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+menu_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menu_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+menu_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menutasktypeinfoid #dispmenutasktypeinfoid', formid).remove();
					$('#tr_menutasktypeinfoid .CaptionTD', formid).after('<td id="dispmenutasktypeinfoid">'+row_id+'</td>');
					$('#tr_menutaskinfoid .DataTD', formid).hide();

					var rowdata = $('#'+menu_info_tabId).jqGrid('getRowData', rowid);

			        // メニュータイプがエクセル帳票の場合、ファイルアップロード要素を表示
			        if(rowdata.menutypeid === '<%= Menutype.EXCELLIST %>'){
			        	$(excellistTrSelector).toggle(true);
			        }else{
			        	$(excellistTrSelector).toggle(false);
			        }

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
				addCaption:'<%=lang.__("Add - Menu info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+menu_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_menutasktypeinfoid .CaptionTD', formid).after('<td id="dispmenutasktypeinfoid">'+row_id+'</td>');
					$('#tr_menutasktypeinfoid .DataTD', formid).hide();

			        // ファイルアップロード要素はデフォルト非表示
		        	$(excellistTrSelector).toggle(false);
				},
				beforeInitData:function(formid){
					//「テーブルID」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				beforeSubmit:function(postdata, formid){

					// メニュータイプがエクセル帳票の場合、ファイルアップロード必須
					var selectedMenutypeId = $('#menutypeid').val();
					if(selectedMenutypeId === '<%= Menutype.EXCELLIST %>'){
						if (!($("#excellisttemplatefile").val() != "")) {
							return [false,"<%=lang.__("Excel template file: this item is required.")%>",""];
						}
					}

					return [true,"",""];
				},
				afterShowForm:function(formid){
					$(formid).attr("enctype","multipart/form-data");
					$.ajaxSetup({ async: true });
					//「属性ID」のプルダウン作成
					initAttrSelect();
				},
				afterSubmit:function(response, postdata){
					//サブミット後、グリッド再取得
					$('#'+menu_info_tabId).jqGrid('setGridParam',{datatype:'json'});

					uploadFile(response, postdata);

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
			//削除フォームオプション設定
			{
				caption:'<%=lang.__("Delete - Menu info")%>',
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
								$('#'+menu_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
				onclickSubmit:function(){
					var rowid = $('#'+menu_info_tabId).jqGrid('getGridParam', 'selrow');
					//サブグリッドを閉じる
					$('#'+menu_info_tabId).jqGrid('collapseSubGridRow', rowid);
					return {};
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
			'#'+menu_info_pagerId,
			{
				id:'copy_menu_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+menu_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+menu_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Menu info")%>',
									bSubmit:'<%=lang.__("Save")%>',
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
										$('#tr_menutasktypeinfoid .CaptionTD', formid).after('<td id="dispmenutasktypeinfoid">'+row_id+'</td>');
										$('#tr_menutasktypeinfoid .DataTD', formid).hide();

								        // ファイルアップロード要素はコピー元がExcel帳票の場合のみ初期表示
										var rowdata = $('#'+menu_info_tabId).jqGrid('getRowData', rowid);
										var selectedMenutypeId = rowdata.menutypeid;
										if(selectedMenutypeId === '<%= Menutype.EXCELLIST %>'){
								        	$(excellistTrSelector).toggle(true);
										}else{
								        	$(excellistTrSelector).toggle(false);
										}
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+menu_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+menu_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+menu_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ menu_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_menu_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+menu_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+menu_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//「属性ID」のプルダウン初期化
	function initAttrSelect() {
		var objid = '#excellistoutputtablemasterinfoid';
		var attrcols = [
			{ name: 'excellistoutputtableregisttimeattrid', required:true },
			{ name: 'excellistoutputtabledownloadlinkattrid', required:true }
		];
		var initMakeAttrSelect = function() {
			if($('#excellistoutputtablemasterinfoid').length==0) return false;
			$('#excellistoutputtablemasterinfoid').on('change', function() {
				makeAttrSelect(menu_info_tabId, tabName, attrcols, objid);
			});
			makeAttrSelect(menu_info_tabId, tabName, attrcols, objid);
			return true;
		}
		var initMakeAttrSelectDelay = function() {
			setTimeout(function(){
				if(initMakeAttrSelect()==false) {
					initMakeAttrSelectDelay();
				}
			}, 500);
		}
		// 編集して保存後に編集フォームを開きなおすと、なぜか属性IDプルダウンが表示されないので
		// 初期化失敗した場合は遅延させる
		if(initMakeAttrSelect()==false) {
			initMakeAttrSelectDelay();
		}
	}

	//サブグリッド設定
	$('#'+menu_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				menutable_info(subgrid_id, row_id);
				filter_info(subgrid_id, row_id);
//				summarylistcolumn_info(subgrid_id, row_id);
				pagemenubutton_info(subgrid_id, row_id);
				maplayer_info(subgrid_id, row_id);
				mapkmllayer_info(subgrid_id, row_id);
				mapreferencelayer_info(subgrid_id, row_id);
//				meteolayer_info(subgrid_id, row_id);
				mapbaselayer_info(subgrid_id, row_id);
//				menuaction_info(subgrid_id, row_id);
// 外部地図データ情報と外部リストデータ情報は、メニュー情報の「外部地図表示レイヤ設定」ボタンから操作すべきなので非表示に変更。
//				externalmapdata_info(subgrid_id, row_id);
//				externaltabledata_info(subgrid_id, row_id);
				menumap_info(subgrid_id, row_id);
				noticedefault_info(subgrid_id, row_id);
			}}
	);	//サブグリッドend

	var head = $('#'+menu_info_gridId).find(".ui-jqgrid-titlebar");
	setHeaderCss(head, thisGridLevel);
	//var thead = $('#'+menu_info_gridId).find(".ui-jqgrid-hdiv");
	//setTableHeaderCss(thead, thisGridLevel);
	//var tbody = $('#'+menu_info_gridId).find(".ui-jqgrid-bdiv");
	//setTableBodyCss(tbody, thisGridLevel);
	//var foot = $('#'+menu_info_gridId).find(".ui-jqgrid-pager");
	//setFooterCss(foot, thisGridLevel);



	////////////////////////////////////////////////////////////////////////
	//内部地図表示レイヤ設定ボタン押下イベント
	$(document).on(
		'click',
		'#callinnermapinfo_'+tabName+row_id,
		function(){
			// メニューIDを取得
			var menuinfoidSelector = '#editmod'+tabName + row_id + ' #id';
			var menuinfoid = $(menuinfoidSelector).val();
			// 関数呼び出し
            innermap_info(menu_info_tabId + '_' + menuinfoid, menuinfoid);
		}
	);

	////////////////////////////////////////////////////////////////////////
	//外部地図表示レイヤ設定ボタン押下イベント
	$(document).on(
		'click',
		'#calloutermapinfo_'+tabName+row_id,
		function(){
			// メニューIDを取得
			var menuinfoidSelector = '#editmod'+tabName + row_id + ' #id';
			var menuinfoid = $(menuinfoidSelector).val();
			// 関数呼び出し
            outermap_info('outer' + menu_info_tabId + '_' + menuinfoid, menuinfoid);
		}
	);

	////////////////////////////////////////////////////////////////////////
	//メニュータイプ選択イベント
	$(document).on(
		'change',
		'#menutypeid',
		function(){
			// 選択されたメニュータイプIDを取得
			var selectedMenutypeId = $('#menutypeid').val();

			// メニュータイプがエクセル帳票の場合はファイルアップロード要素を表示
			if(selectedMenutypeId === '<%= Menutype.EXCELLIST %>'){
				$(excellistTrSelector).toggle(true);
			}else{
				$(excellistTrSelector).toggle(false);
			}
		}
	);
}
function uploadFile(response, postdata) {
	if ($("#excellisttemplatefile").val() != "") {
		if (!postdata.id){
			postdata.id = JSON.parse(response.responseText).newEntity.id;
		}
		ajaxFileUpload(postdata);
	}
	return [true];
}

function ajaxFileUpload(postdata){
	$("#loading").ajaxStart(function () {
		$(this).show();
	}).ajaxComplete(function () {
		$(this).hide();
	});

	var fd = {
		"menutasktypeinfoid": postdata['menutasktypeinfoid'],
		"id": postdata['id'],
		"_csrf": '${_csrf.token}'
	};
	$.ajaxFileUpload({
		url: '${f:url("menuInfo/upload/")}',
		secureuri: false,
		fileElementId: 'excellisttemplatefile',
		dataType: 'json',
		type: 'post',
		data: fd,
		async: false,
		success: function(data) {
			if (data.message) {
				showMessage('menu_info', jsonObj.message, $.jgrid.nav.alertcap);
//				alert(jsonObj.message);
			}
			else {
				alert("<%=lang.__("Succeed at upload file.")%>");
			}
		},
		error: function(data, status, e) {
			if(data.responseText){
				try{
					var responseJson = $.parseJSON(data.responseText);
					alert(unescapeHTML(responseJson.message));
				}
				catch(e){
					alert("<%=lang.__("Failed to upload the excel template file.") %>");
				}
			}else{
					alert("<%=lang.__("Failed to upload the excel template file.") %>");
			}
		}
	});
}

function unescapeHTML(str){
    return str.replace(/&lt;/g,'<')
               .replace(/&gt;/g,'>')
               .replace(/&amp;/g,'&');
}

