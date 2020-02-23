<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function tablelistkarte_info(subgrid_id, row_id){
	var tabName = 'tablelistkarte_info';
	var tablelistkarte_info_gridId = 'main';
	if(subgrid_id != null){
		tablelistkarte_info_gridId = subgrid_id;
	}
	var tablelistkarte_info_tabId = tabName;
	if(row_id != null){
		tablelistkarte_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'menutable_info', 'menutableinfoid');

	var tablelistkarte_info_pagerId = tablelistkarte_info_tabId+'Nav';
	$('#'+tablelistkarte_info_gridId).append('<table id="'+tablelistkarte_info_tabId+'" class="scroll"></table><div id="'+tablelistkarte_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	var afterShowForm = function() {

		//ラジオボタンの状態に応じて項目を表示／非表示にする
		var showHide = function() {
			var attridColProp = $('#'+tablelistkarte_info_tabId).jqGrid('getColProp', 'attrid');
			if ($('[name=attrgroupradio]:checked').val() == '1') {
				$('[name=attrid]').parents('tr').show();
				$('[name=editable]').parents('tr').show();
				$('[name=highlight]').parents('tr').show();
				$('[name=closed]').parents('tr').hide();
				$('#name_search_button').show();
				attridColProp.editrules.required = true;
			}
			else {
				$('[name=attrid]').parents('tr').hide();
				$('[name=editable]').parents('tr').hide();
				$('[name=highlight]').parents('tr').hide();
				$('[name=closed]').parents('tr').show();
				$('#name_search_button').hide();
				attridColProp.editrules.required = false;
			}
			$('#'+tablelistkarte_info_tabId).jqGrid('setColProp', attridColProp);
		}

		if ($('[name=attrid]').val() == 'group')
			$('[name=attrgroupradio]').val(['2']);
		else
			$('[name=attrgroupradio]').val(['1']);
		$('[name=attrgroupradio]').change(showHide);
		showHide();
	};

	var beforeSubmit = function(postdata, formid) {

		//ラジオボタンの状態に応じて非表示の項目に値を設定する
		if ($('[name=attrgroupradio]:checked').val() == '1') {
			if (postdata.attrid == 'group') {
				return [false, lang.__("Input group as a table item name not allowed in attribution mode."), ""];
			}
			postdata.closed = 'false';
			$('[name=closed]').val('false');
		}
		else {
			postdata.attrid = 'group';
			$('[name=attrid]').val('group');
			postdata.editable = 'false';
			$('[name=editable]').val('false');
			postdata.highlight = 'false';
			$('[name=highlight]').val('false');
		}
		return [true,"",""];
	};

	//グリッド
	$('#'+tablelistkarte_info_tabId).jqGrid({
		caption: '<%=lang.__("Table list karte info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'',
			'<%=lang.__("ID")%>',
			'<%=lang.__("Menu table ID")%>',
			'<%=lang.__("Table item name")%>',
			'<%=lang.__("Name")%>',
			'<%=lang.__("Allowed to edit")%>',
			'<%=lang.__("Highlighting")%>',
			'<%=lang.__("Group folder")%>',
			'<%=lang.__("Display order")%>',
			'<%=lang.__("Tips")%>',
		],
		colModel:[
			{name:'attrgroup',
				editable:true,
			 	edittype:'custom',
				editrules:{ edithidden:true },
				editoptions:{
					custom_element: function(value, options) {
						return '<div><input type="radio" name="attrgroupradio" value="1"><%=lang.__("Attribute")%></input>&nbsp;&nbsp;&nbsp;&nbsp;' +
							'<input type="radio" name="attrgroupradio"  value="2"><%=lang.__("Group<!--2-->")%></input></div>';
					},
					custom_value: function(elem, operation, value) {
						return '';
					}
				},
				jsonmap:'attrid',
				hidden:true},
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("ID")%>'},
				hidden:false},
			{name:'menutableinfoid',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Menu table ID(*)")%>'},
				hidden:false},
			{name:'attrid',
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
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Table item name(*)")%>'},
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
				formoptions:{elmprefix:'', elmsuffix:'<a id="name_search_button" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Name(*)")%>'},
				hidden:false},
			{name:'editable',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Allowed to edit(*)")%>'},
				hidden:false},
			{name:'highlight',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Highlighting(*)")%>'},
				hidden:false},
			{name:'closed',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Group folder(*)")%>'},
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
			{name:'tips',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Tips")%>'},
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
		subGrid: false,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: tablelistkarte_info_pagerId,
		autowidth: true,
		autoencode: true,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].menutableInfo;
				if(ref != null){
					var refkey = ref.id;
					str += refkey + ':' + refkey;
				}

			}
			$('#'+tablelistkarte_info_tabId).jqGrid('setColProp', 'menutableinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//ページボタンセンター配置対策
			$("#tablelistkarte_infoNav_left").css("width","");
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+tablelistkarte_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
			//表示順のデフォルト値（現在の最大値+1）をセットする。
			var maxDisporder = $('#'+tablelistkarte_info_tabId).jqGrid('getGridParam','userData').maxDisporder;
			$('#'+tablelistkarte_info_tabId).jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
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
	$('#'+tablelistkarte_info_tabId).jqGrid('navGrid',
			'#'+tablelistkarte_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Table list karte info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+tablelistkarte_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+tablelistkarte_info_tabId).jqGrid('getRowData', rowid);
					s += '&menutableinfoid='+rowdata.menutableinfoid;
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menutableinfoid .CaptionTD', formid).after('<td id="dispmenutableinfoid">'+row_id+'</td>');
					$('#tr_menutableinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
			 	afterShowForm:afterShowForm,
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+tablelistkarte_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+tablelistkarte_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});
					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+tablelistkarte_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+tablelistkarte_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menutableinfoid #dispmenutableinfoid', formid).remove();
					$('#tr_menutableinfoid .CaptionTD', formid).after('<td id="dispmenutableinfoid">'+row_id+'</td>');
					$('#tr_menutableinfoid .DataTD', formid).hide();
					afterShowForm();
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
				reloadAfterSubmit:true,
				beforeSubmit:beforeSubmit
			},
			//追加フォームオプション設定
			{recreateForm: true,
				width:'auto',
				addCaption:'<%=lang.__("Add - Table list karte info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+tablelistkarte_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_menutableinfoid .CaptionTD', formid).after('<td id="dispmenutableinfoid">'+row_id+'</td>');
					$('#tr_menutableinfoid .DataTD', formid).hide();
				},
				beforeSubmit:beforeSubmit,
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				reloadAfterSubmit:true,
			 	afterShowForm:afterShowForm
			},
			//削除フォームオプション設定
			{
				caption:'<%=lang.__("Delete - Table list karte info")%>',
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
			'#'+tablelistkarte_info_pagerId,
			{
				id:'copy_tablelistkarte_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+tablelistkarte_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+tablelistkarte_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Table list karte info")%>',
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
										$('#tr_menutableinfoid .CaptionTD', formid).after('<td id="dispmenutableinfoid">'+row_id+'</td>');
										$('#tr_menutableinfoid .DataTD', formid).hide();
									},
									beforeSubmit:beforeSubmit,
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+tablelistkarte_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+tablelistkarte_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ tablelistkarte_info_tabId).id);
						afterShowForm();
						//idはコピーしない。
						$('#FrmGrid_tablelistkarte_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+tablelistkarte_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+tablelistkarte_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

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
					url:convertTabNameToMethodMame(tabName)+'/jqgridecommap/'+row_id+'',
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
							var layeridSelector = '#editmod'+tabName + row_id + ' #attrid';
							$(layeridSelector).val(rowdata.attrId);
							// 名称に属性名をセット
							var nameSelector = '#editmod'+tabName + row_id + ' #name';
							$(nameSelector).val(rowdata.name);

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
				$('#ecomgrid_'+tabName+row_id).jqGrid('setGridParam',{datatype:'json'});
				$('#ecomgrid_'+tabName+row_id).trigger('reloadGrid');
			}
		}
	);

}
