<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function meteorequest_info(subgrid_id, row_id){
	var tabName = 'meteorequest_info';
	var meteorequest_info_gridId = 'main';
	if(subgrid_id != null){
		meteorequest_info_gridId = subgrid_id;
	}
	var meteorequest_info_tabId = tabName;
	if(row_id != null){
		meteorequest_info_tabId += row_id;
	}else{
		row_id = '';
	}

	var meteorequest_info_pagerId = meteorequest_info_tabId+'Nav';
	$('#'+meteorequest_info_gridId).append('<table id="'+meteorequest_info_tabId+'" class="scroll"></table><div id="'+meteorequest_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+meteorequest_info_tabId).jqGrid({
		caption: '<%=lang.__("Acquired weather info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Local gov. ID")%>',
			'<%=lang.__("Weather info type")%>',
			'<%=lang.__("District ID")%>',
			'<%=lang.__("District ID (hidden)")%>',
			'<%=lang.__("Reserve district ID")%>',
			'<%=lang.__("Reserve district ID (hidden)")%>',
			'<%=lang.__("Alarm flag")%>',
			'<%=lang.__("Display flag")%>',
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
			{name:'localgovinfoid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/localgovinfoid/localgov_info/id/id',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+meteorequest_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.localgovinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'meteorequest_info', 'localgov_info', false);
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Local gov. ID(*)")%>'},
				hidden:false},
			{name:'meteotypeid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/meteotypeid/meteotype_master/id/name',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+meteorequest_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.meteotypeid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'meteorequest_info', 'meteotype_master', false);
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Weather info type(*)")%>'},
				hidden:false},
			{name:'meteoareaid',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("District ID(*)")%>'},
				hidden:false},
			//セレクトボックス用の非表示カラム
			{name:'meteoareaid_select',
				jsonmap:'meteoareaid',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("District ID(*)")%>'},
				hidden:true},
			{name:'meteoareaid2',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Reserve district ID")%>'},
				hidden:false},
			//セレクトボックス用の非表示カラム
			{name:'meteoareaid2_select',
				jsonmap:'meteoareaid2',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Reserve district ID")%>'},
				hidden:true},
			{name:'alarm',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Alarm flag(*)")%>'},
				hidden:false},
			{name:'view',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Display flag(*)")%>'},
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
		subGrid: true,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: meteorequest_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].localgovInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.pref + ref.city;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+meteorequest_info_tabId).jqGrid('setColProp', 'localgovinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].meteotypeMaster;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+meteorequest_info_tabId).jqGrid('setColProp', 'meteotypeid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+meteorequest_info_tabId).getDataIDs();
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
	$('#'+meteorequest_info_tabId).jqGrid('navGrid',
			'#'+meteorequest_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Acquired weather info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+meteorequest_info_tabId).jqGrid('getRowData', rowid);
					s += '&localgovinfoid='+rowdata.localgovinfoid;
					var rowdata = $('#'+meteorequest_info_tabId).jqGrid('getRowData', rowid);
					s += '&meteotypeid='+rowdata.meteotypeid;
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				beforeInitData:function(formid){
					//「気象情報種別」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$.ajaxSetup({ async: true });
					//「エリアID」のプルダウン作成
					initAreaSelect();
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+meteorequest_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+meteorequest_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					//「エリアID」のプルダウン作成
					makeAreaSelect();
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
				addCaption:'<%=lang.__("Add - Acquired weather info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+meteorequest_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
				},
				beforeInitData:function(formid){
					//「気象情報種別」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$.ajaxSetup({ async: true });
					//「エリアID」のプルダウン作成
					initAreaSelect();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Acquired weather info")%>',
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
								$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+meteorequest_info_pagerId,
			{
				id:'copy_meteorequest_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//「地方自治体種別」のプルダウンが作成された後で initPrefSelect() が呼ばれるように、一時的に非同期動作を禁止する
						$.ajaxSetup({ async: false });

						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+meteorequest_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Acquired weather info")%>',
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
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+meteorequest_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$.ajaxSetup({ async: true });
						//グリッドの選択列データをコピーフォームへデフォルト値としてセットする。
						$('#'+meteorequest_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ meteorequest_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_meteorequest_info #id').val('');
						//「エリアID」のプルダウン作成
						initAreaSelect();
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+meteorequest_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+meteorequest_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//サブグリッド設定
	$('#'+meteorequest_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				meteotrigger_info(subgrid_id, row_id);
			}}
	);	//サブグリッドend


	//「エリアID」のプルダウン初期化
	function initAreaSelect() {
		$('#meteotypeid').on('change', makeAreaSelect);
		//セレクトボックスの値をテキストボックスにコピーする
		$('#meteoareaid_select').on('change', function() { $('#meteoareaid').val($('#meteoareaid_select').val()); });
		$('#meteoareaid2_select').on('change', function() { $('#meteoareaid2').val($('#meteoareaid2_select').val()); });
		makeAreaSelect();
	}

	var meteotypes = {
		1: { 'table': 'meteoareainformationcity_master', decode: 'namewarn'},
		2: { 'table': 'meteoseismicarea1_master' },
		3: { 'table': 'meteotsunamiarea_master'},
		4: { 'table': 'meteoareainformationcity_master', decode: 'nameseismic' },
		5: { 'table': 'meteoriver_master' },
		6: { 'table': 'meteoareainformationcity_master', decode: 'namewarn' },
		7: { 'table': 'meteorainarea_master' },
		8: { 'table': 'meteoareainformationcity_master', decode: 'namewarn' },
		9: { 'table': 'meteoareainformationcity_master', decode: 'namevolcano' },
	};

	//「エリアID」のプルダウン作成
	function makeAreaSelect() {
		var areaids = [ 'meteoareaid', 'meteoareaid2' ];
		var meteotypeid = $('#meteotypeid').val();
		var meteotype = meteotypes[meteotypeid];
		var table = meteotype.table;
		//「津波警報・注意報・予報a」が選択された場合はプルダウンではなくテキストボックスを表示する
		if (!table) {
			$('#tr_meteoareaid_select').hide();
			$('#tr_meteoareaid').show();
			$('#tr_meteoareaid2_select').hide();
			$('#tr_meteoareaid2').show();
			var rowid = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'selrow');
			var rowdata = $('#'+meteorequest_info_tabId).jqGrid('getRowData', rowid);
			$.each(areaids, function(j, areaid) {
				$('#'+areaid).val(rowdata[areaid] || '');
			});
			return;
		}
		$('#tr_meteoareaid').hide();
		$('#tr_meteoareaid_select').show();
		$('#tr_meteoareaid2').hide();
		$('#tr_meteoareaid2_select').show();
		var code = meteotype.code || "code";
		var decode = meteotype.decode || "name";
		var url = convertTabNameToMethodMame(tabName)+'/createSelectTag/meteoareaid/' + table + '/' + code + '/' + decode + '?meteotypeid=' + meteotypeid + '';
		$.ajax({
			dataType: "json",
			type: "GET",
			cache: false,
			url: url,
			success: function(data) {
				var selectTag = data.selectTag;
				var rowid = $('#'+meteorequest_info_tabId).jqGrid('getGridParam', 'selrow');
				var rowdata = $('#'+meteorequest_info_tabId).jqGrid('getRowData', rowid);
				//末尾に空の項目を追加する
				selectTag[selectTag.length] = { 'key': '', 'value': '' };
				$.each(areaids, function(j, areaid) {
					$('#'+areaid+'_select').children().remove();
					selectedId = rowdata[areaid] || '';
					var found = false;
					for (var i = 0; i < selectTag.length; i++) {
						//meteoareaid2のみ空の項目を追加する（meteoareaidには追加しない）
						if (j == 0 && i > 0 && !selectTag[i].key)
							continue;
						var selected = '';
						if (selectTag[i].key == selectedId || !selectTag[i].key && !found) {
							selected = 'selected=" selected"';
							found = true;
						}
						$('#'+areaid+'_select').append('<option value="'+ selectTag[i].key + '"' + selected + '>' + selectTag[i].value + '</option>');
						if (j == 0 && i == 0 || selected)
							$('#'+areaid).val(selectTag[i].key);
					}
				})
				$("input.ui-autocomplete-input", $("#meteoareaid_select").parent()).val("");
				$('#meteoareaid_select').combobox({
					select: function (event, ui) {
						$('#meteoareaid').val($('#meteoareaid_select').val());
					}
				});
				$("input.ui-autocomplete-input", $("#meteoareaid2_select").parent()).val("");
				$('#meteoareaid2_select').combobox({
					select: function (event, ui) {
						$('#meteoareaid2').val($('#meteoareaid2_select').val());
					}
				});
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("error : " + errorThrown);
			}
		});
	}

}
