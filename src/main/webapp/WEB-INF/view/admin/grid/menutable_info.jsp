<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function menutable_info(subgrid_id, row_id){
	var tabName = 'menutable_info';
	var menutable_info_gridId = 'main';
	if(subgrid_id != null){
		menutable_info_gridId = subgrid_id;
	}
	var menutable_info_tabId = tabName;
	if(row_id != null){
		menutable_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'menu_info', 'menuinfoid');

	var menutable_info_pagerId = menutable_info_tabId+'Nav';
	$('#'+menutable_info_gridId).append('<table id="'+menutable_info_tabId+'" class="scroll"></table><div id="'+menutable_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+menutable_info_tabId).jqGrid({
		caption: '<%=lang.__("Menu table info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Menu ID")%>',
			'<%=lang.__("Table ID")%>',
			'<%=lang.__("Add flag")%>',
			'<%=lang.__("Delete flag")%>',
			'<%=lang.__("Total flag")%>',
			'<%=lang.__("Display order")%>',
			'<%=lang.__("Accordion target item")%>',
			'<%=lang.__("Name")%>',
			'<%=lang.__("Accordion expand status")%>'
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
			{name:'menuinfoid',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Menu ID(*)")%>'},
			hidden:false},
			{name:'tablemasterinfoid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/tablemasterinfoid/tablemaster_info/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+menutable_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+menutable_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.tablemasterinfoid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'menutable_info', 'tablemaster_info');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Table ID(*)")%>'},
			hidden:false},
			{name:'addable',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Add flag(*)")%>'},
			hidden:false},
			{name:'deletable',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Delete flag(*)")%>'},
			hidden:false},
			{name:'totalable',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Total flag(*)")%>'},
				hidden:false},
			{name:'disporder',
				width:'100',
				align:'right',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
					,style:'ime-mode:inactive'
				},
				editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Display order(*)")%>'},
				hidden:false},
			{name:'accordionattrid',
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
				formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Accordion target item")%>'},
				hidden:false},
			{name:'accordionname',
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
				formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Name")%>'},
				hidden:false},
			{name:'accordionopen',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'true:true;false:false',
					defaultValue:'true',
					readonly:false
				},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Accordion expand status")%>'},
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
		sortname: 'disporder',
		sortorder: 'ASC',
		subGrid: true,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: menutable_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].menuInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+menutable_info_tabId).jqGrid('setColProp', 'menuinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].tablemasterInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+menutable_info_tabId).jqGrid('setColProp', 'tablemasterinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+menutable_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
			//表示順のデフォルト値（現在の最大値+1）をセットする。
			var maxDisporder = $('#'+menutable_info_tabId).jqGrid('getGridParam','userData').maxDisporder;
			$('#'+menutable_info_tabId).jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
			if(getEditMode() == 'add'){
				//登録モードの場合、表示順のデフォルト値（現在の最大値+1）をフォームの表示順欄にセットする。
				$('#tr_disporder #disporder').val(maxDisporder+1);
			}
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
	$('#'+menutable_info_tabId).jqGrid('navGrid',
			'#'+menutable_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Menu table info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+menutable_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+menutable_info_tabId).jqGrid('getRowData', rowid);
					s += '&menuinfoid='+rowdata.menuinfoid;
					var rowdata = $('#'+menutable_info_tabId).jqGrid('getRowData', rowid);
					s += '&tablemasterinfoid='+rowdata.tablemasterinfoid;
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();

					$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
					$('#tr_menuinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+menutable_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+menutable_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+menutable_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menutable_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menuinfoid #dispmenuinfoid', formid).remove();
					$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
					$('#tr_menuinfoid .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - Menu table info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+menutable_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
					$('#tr_menuinfoid .DataTD', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+menutable_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Menu table info")%>',
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
								$('#'+menutable_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+menutable_info_pagerId,
			{
				id:'copy_menutable_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+menutable_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+menutable_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Menu table info")%>',
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
										$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
										$('#tr_menuinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+menutable_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+menutable_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+menutable_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ menutable_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_menutable_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+menutable_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+menutable_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//サブグリッド設定
	$('#'+menutable_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				tablelistcolumn_info(subgrid_id, row_id);
				tablelistkarte_info(subgrid_id, row_id);
			}}
	);	//サブグリッドend

	////////////////////////////////////////////////////////////////////////
	//eコミデータ取り込み
	////////////////////////////////////////////////////////////////////////
	//eコミ連携ボタン押下イベント
	$(document).on(
		'click',
		'#callecom_'+tabName+row_id,
		function(){
			//ダイアログのHTML
			var dialogHtml = '';
			bS  ="<a href='javascript:void(0)' id='selectData_"+tabName+row_id+"' class='fm-button ui-state-default ui-corner-all fm-button-icon-left'><%=lang.__("Select")%><span class='ui-icon ui-icon-disk'></span></a>",
			bC  ="<a href='javascript:void(0)' id='cancelData_"+tabName+row_id+"' class='fm-button ui-state-default ui-corner-all fm-button-icon-left'><%=lang.__("Cancel")%><span class='ui-icon ui-icon-close'></span></a>";
			var bt = "<table border='0' cellspacing='0' cellpadding='0' class='EditTable' id='TblGrid_"+tabName+row_id+"'><tbody><tr id='Act_Buttons'><td class='EditButton'>"+bS+bC+"</td></tr>";
			bt += "</tbody></table>";
			dialogHtml = '<div id="ecomdialog_'+tabName+row_id+'"><table id="ecomgrid_'+tabName+row_id+'" ></table><div id="ecomNav_'+tabName+row_id+'" ></div>'+bt+'</div>';

			// ダイアログとグリッドの幅は画面の80%にする
			var wWidth = $(window.parent.document.getElementById('mainFrame')).width();
 			var dWidth = wWidth * 0.8;
			var tableid = $('#tablemasterinfoid').val();

			//既存ダイアログがない場合
			if($('#ecomModalTableDialog_'+tabName+row_id).size()==0){
				//ダイアログ作成
				$.jgrid.createModal(
					IDs = {
						themodal:'ecomModalTableDialog_'+tabName+row_id,
						modalhead:'ecomModalTableDialogHd_'+tabName+row_id,
						modalcontent:'ecomModalTableDialogCnt_'+tabName+row_id,
						scrollelm : ''
					},
					dialogHtml,
					{
						gbox:'#gbox_editmod'+tabName+row_id,
						jqModal:true,
						drag:true,
						resize:false,
						caption:'<%=lang.__("Attribute info selection window.")%>',
//						top:100,
//						left:100,
//						width:'auto',
						width:dWidth,
						height: 'auto',
						closeOnEscape:true,
//						zIndex: 200,
					},
					'',
					'',
					true
				);

				//ダイアログの表示
				$.jgrid.viewModal('#ecomModalTableDialog_'+tabName+row_id,
					{modal:true,}
				);
				$('#ecomModalTableDialog_'+tabName+row_id).position({
					my:'left bottom',
					at:'left bottom',
					of: '#callecom_'+tabName+row_id

				});
				$('#ecomModalTableDialog_'+tabName+row_id).focus();

				//ダイアログにグリッドとナビゲーションを表示
				$('#ecomgrid_'+tabName+row_id).jqGrid({
					mtype: 'POST',
					url:convertTabNameToMethodMame(tabName)+'/jqgridecommap/'+tableid+'',
					datatype: 'json',
//					jsonReader:{
//						root:"items",
//					},
					colNames: [
							'<%=lang.__("Layer name")%>',
							'<%=lang.__("Attribute ID")%>',
							'<%=lang.__("Attribute name")%>',
					],
					colModel: [
						{name:'layerName',width:'150',editable:false,search:true,hidden:false},
						{name:'attrId',width:'60',editable:false,search:true,hidden:false},
						{name:'name',width:'150',editable:false,search:true,hidden:false},
					],
					ajaxGridOptions :{
						beforeSend: function(jqXHR) {
							var csrf_token = '${_csrf.token}';
							jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
						}
					},
					caption: '<%=lang.__("Attribute info")%>',
					loadtext:'',
					loadui:'block',
					loadonce:false,
					rowNum:30,
					rowList:[5,10,30,50,100],
//					width:950,
					width:dWidth-5,
					height: 'auto',
					multiselect: false,
					editurl:'',
					cellEdit: false,
					cellsubmit: 'clientArray',
					sortname: 'communityId',
					sortorder: 'ASC',
					subGrid: false,
					viewrecords: true,
					postData:{'loadonce':false},
					pager: '#ecomNav_'+tabName+row_id,

					loadComplete : function (data) {
					var trOddSelector = '#ecomgrid_'+tabName+row_id+' tr:odd';
					$(trOddSelector).removeClass('ui-widget-content');
					$(trOddSelector).addClass('myodd');
					var caption_list = '<%=lang.__("Attribute info")%>';
					if (typeof data.title === "undefined") { // e-commap 連携かの判断は jsonデータのtitle エレメントの存在有無とする
						// e-commap であれば、e-commap連携のダイアログ画面と一覧のタイトルを再設定する
						caption_list = '<%=lang.__("e-Com map layer")%>' + caption_list;
					} else {
						// e-commap でなければ、システムテーブルのダイアログ画面と一覧のタイトルを再設定する
						caption_list = '<%=lang.__("System table")%>' + caption_list;
					}
					$("#gview_ecomgrid_"+tabName+row_id+" .ui-jqgrid-title").html(caption_list);
					},

				}).navGrid("#ecomNav_"+tabName+row_id,
					//ナビゲーション
					{edit:false,add:false,del:false,search:true,
						beforeRefresh: function(){
							//リフレッシュボタン押下時、サーバ問い合わせ。
							$('#ecomgrid_'+tabName+row_id).jqGrid('setGridParam',{datatype:'json'});
							return [true,''];
						}
					},
					{},
					{},
					{},
					{recreateForm: true, modal:true,}
				);

				//タイトルバーの×ボタンのコールバック関数
				var headerCancelSelector = '#ecomModalTableDialogHd_'+tabName+row_id+' a.ui-jqdialog-titlebar-close';
				$(headerCancelSelector).click(function(){
						$('#ecomgrid_'+tabName+row_id).jqGrid('clearGridData');
						$.jgrid.hideModal('#ecomModalTableDialog_'+tabName+row_id,{jqm:true});
					}
				);

				//フッターのキャンセルボタンのコールバック関数
				$(document).on(
					'click',
					'#cancelData_'+tabName+row_id,
					function(){
						$('#ecomgrid_'+tabName+row_id).jqGrid('clearGridData');
						$.jgrid.hideModal('#ecomModalTableDialog_'+tabName+row_id,{jqm:true});
					}
				);

				//呼び出し元の編集フォームへ選択レコードデータをセットするコールバック関数
				$(document).on(
					'click',
					'#selectData_'+tabName+row_id,
					function(){
						var rowid = $('#ecomgrid_'+tabName+row_id).jqGrid('getGridParam','selrow');
						if(rowid){
							var rowdata = $('#ecomgrid_'+tabName+row_id).jqGrid('getRowData',rowid);
							// テーブル項目名に属性IDをセット
							$('#accordionattrid').val(rowdata.attrId);
							// 名称に属性名をセット
							$('#accordionname').val(rowdata.name);

							//グリッドをクリアし、ダイアログ非表示
							$('#ecomgrid_'+tabName+row_id).jqGrid('clearGridData');
							$.jgrid.hideModal('#ecomModalTableDialog_'+tabName+row_id,{jqm:true});
						}else{
							$.jgrid.viewModal($('#alertmod_ecomgrid_'+tabName+row_id),{jqm:true});
						}
					}
				);
			}else{
				//既存ダイアログがある場合
				// ダイアログ位置再設定

				//ダイアログの表示
				$.jgrid.viewModal('#ecomModalTableDialog_'+tabName+row_id,
					{modal:true,}
				);
				$('#ecomModalTableDialog_'+tabName+row_id).position({
					my:'left bottom',
					at:'left bottom',
					of: '#callecom_'+tabName+row_id
				});
				$('#ecomModalTableDialog_'+tabName+row_id).focus();
				//グリッドをリロード
				$('#ecomgrid_'+tabName+row_id).jqGrid('setGridParam',{
					datatype:'json',
					url:convertTabNameToMethodMame(tabName)+'/jqgridecommap/'+tableid+''
				});
				$('#ecomgrid_'+tabName+row_id).trigger('reloadGrid');
			}
		}
	);
}
