<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function mapmaster_info(subgrid_id, row_id){
	var tabName = 'mapmaster_info';
	var mapmaster_info_gridId = 'main';
	if(subgrid_id != null){
		mapmaster_info_gridId = subgrid_id;
	}
	var mapmaster_info_tabId = tabName;
	if(row_id != null){
		mapmaster_info_tabId += row_id;
	}else{
		row_id = '';
	}

	var mapmaster_info_pagerId = mapmaster_info_tabId+'Nav';
	$('#'+mapmaster_info_gridId).append('<table id="'+mapmaster_info_tabId+'" class="scroll"></table><div id="'+mapmaster_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+mapmaster_info_tabId).jqGrid({
		caption: '<%=lang.__("Map master info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Local gov. ID")%>',
			'<%=lang.__("Community ID")%>',
			'<%=lang.__("Group ID")%>',
			'<%=lang.__("Map ID")%>',
			'<%=lang.__("display limitation range BBOX value")%>',
			'<%=lang.__("Copy method")%>'
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
						var rowid = $('#'+mapmaster_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+mapmaster_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.localgovinfoid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'mapmaster_info', 'localgov_info');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Local gov. ID(*)")%>'},
			hidden:false},
			{name:'communityid',
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
			editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Community ID(*)")%>'},
			hidden:false},
			{name:'mapgroupid',
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
			editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Group ID(*)")%>'},
			hidden:false},
			{name:'mapid',
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
			editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Map ID(*)")%>'},
			hidden:false},
			{name:'restrictedextent',
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
				formoptions:{elmprefix:'', elmsuffix:

					'&nbsp;<a id="callrangemap" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Map<!--2-->")%></a>' ,
					label:'<%=lang.__("display limitation range BBOX value")%>'},
				hidden:false},
			{name:'copy',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'<%=lang.__("false: map copy only in drill ; true: map copy both in disaster and drill")%>',
				defaultValue:'false',
				readonly:false
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Copy method(*)")%>'},
			hidden:false},
		],
		ajaxGridOptions :{
			beforeSend: function(jqXHR) {
				var csrf_token = '${_csrf.token}';
				jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
			}
		},
		loadtext:'',
		loadui:'block',
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
		pager: mapmaster_info_pagerId,
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
			$('#'+mapmaster_info_tabId).jqGrid('setColProp', 'localgovinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//ページボタンセンター配置対策
			$("#mapmaster_infoNav_left").css("width","");
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+mapmaster_info_tabId).getDataIDs();
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
	$('#'+mapmaster_info_tabId).jqGrid('navGrid',
			'#'+mapmaster_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Map master info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+mapmaster_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+mapmaster_info_tabId).jqGrid('getRowData', rowid);
					s += '&localgovinfoid='+rowdata.localgovinfoid;
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+mapmaster_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+mapmaster_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){

					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+mapmaster_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+mapmaster_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
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
				addCaption:'<%=lang.__("Add - Map master info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+mapmaster_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Map master info")%>',
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
								$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
					var rowid = $('#'+mapmaster_info_tabId).jqGrid('getGridParam', 'selrow');
					//サブグリッドを閉じる
					$('#'+mapmaster_info_tabId).jqGrid('collapseSubGridRow', rowid);
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
			'#'+mapmaster_info_pagerId,
			{
				id:'copy_mapmaster_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+mapmaster_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+mapmaster_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Map master info")%>',
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
										//$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+mapmaster_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+mapmaster_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ mapmaster_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_mapmaster_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+mapmaster_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+mapmaster_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');



	//サブグリッド設定
	$('#'+mapmaster_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				tablemaster_info(subgrid_id, row_id);
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

			// ダイアログとグリッドの幅は画面の70%にする
			var wWidth = $(window.parent.document.getElementById('mainFrame')).width();
 			var dWidth = wWidth * 0.7;

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
						caption:'<%=lang.__("e-Com info selection window")%>',
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
					url:convertTabNameToMethodMame(tabName)+'/jqgridecommap',
					datatype: 'json',
					colNames: ['<%=lang.__("Community info")%>','<%=lang.__("Group info")%>','<%=lang.__("Map info")%>'],
					colModel: [
						{name:'communityInfo',editable:false,search:true,hidden:false},
						{name:'groupInfo',editable:false,search:true,hidden:false},
						{name:'mapInfo',editable:false,search:true,hidden:false},
					],
					ajaxGridOptions :{
						beforeSend: function(jqXHR) {
							var csrf_token = '${_csrf.token}';
							jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
						}
					},
					caption: '<%=lang.__("e-Com map info")%>',
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
//				$(document).on(
//					'click',
//					headerCancelSelector,
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
							// IDと名称に分割
							var rowdata = $('#ecomgrid_'+tabName+row_id).jqGrid('getRowData',rowid);
							var communityInfos = rowdata['communityInfo'].split(":");
							var groupInfos = rowdata['groupInfo'].split(":");
							var mapInfos = rowdata['mapInfo'].split(":");
							//eコミデータを親ダイアログの項目へセットする。
							var communityidSelector = '#editmod'+tabName + row_id + ' #communityid';
							var mapgroupidSelector = '#editmod'+tabName + row_id + ' #mapgroupid';
							var mapidSelector = '#editmod'+tabName + row_id + ' #mapid';
							$(communityidSelector).val(communityInfos[0]);
							$(mapgroupidSelector).val(groupInfos[0]);
							$(mapidSelector).val(mapInfos[0]);
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
$(document).on(
	'click',
	'#callrangemap',
	function(){
		// 関数呼び出し
		toggleMapArea();
	}
);
$(document).on(
	'click',
	'#btnSetMapArea',
	function(){
		// 関数呼び出し
		setMapArea();
	}
);


function setMapArea(base){
    var vector = getVectorLayer(mapobj_header);
    if (!vector || vector.length == 0)
        return;

    var bounds = vector[0].features[0].geometry.bounds;
    bounds = bounds.transform(new OpenLayers.Projection("EPSG:900913"), new OpenLayers.Projection("EPSG:4326"));
    // 選択された範囲を設定する

	var x1 = bounds.left.toFixed(6);
	var x2 = bounds.right.toFixed(6);
	var y1 = bounds.top.toFixed(6);
	var y2 = bounds.bottom.toFixed(6);

    var bBox = x1 + "," + y1 + "," + x2 + "," + y2;
	$('input[id="restrictedextent"]').val(bBox);
    //$('#mapctrl_header').hide();
    $.jgrid.hideModal('#rangeMapModalDialog',{jqm:true});
}


//function toggleMapArea(event,base) {
function toggleMapArea() {
    var mapctrl = $('#rangeMapModalDialog');
    if(mapctrl.css('display') == 'block'){
        // 表示中は閉じて終わり
         $.jgrid.hideModal('#rangeMapModalDialog',{jqm:true});
        return;
    }

	popupRangMapDialog();

    if (mapobj_header == null || document.getElementById("mapPanel_header").children.length == 0) {
        mapobj_header = initMyMap("mapPanel_header", function(bounds) {
                // 選択された範囲を設定する
                //alert("callback");

                //$('input[id="extent"]').val(bounds.left.toFixed(6) + " " + bounds.right.toFixed(6) + " " + bounds.top.toFixed(6) + " " + bounds.bottom.toFixed(6));

 				$.jgrid.hideModal('#rangeMapModalDialog',{jqm:true});
            });
    }

    removePolygon(mapobj_header);
    document.getElementById("rdoScroll").checked = true;
    getSearchBoxControl(mapobj_header)[0].deactivate();

    // 設定された範囲と適切な縮尺で表示
	var bBox = $('input[id="restrictedextent"]').val();
	if (isBBox(bBox)) {
		var box = parseBBox(bBox);

		if (box) {
			var l = box.left;
			var r = box.right;
		    var b = box.bottom;
			var t = box.top;

			if(l != NaN && r != NaN && b != NaN && t != NaN &&
		       l >= -180 && l <= 180 && r >= -180 && r <= 180 &&
		       b >= -90 && b <= 90 && t >= -90 && t <= 90){
		        if(r < l){
		            var tmp = l;
		            l = r;
		            r = tmp;
		        }
		        if(t < b){
		            var tmp = b;
		            b = t;
		            t = tmp;
		        }

		        var c = new OpenLayers.LonLat((l+r)/2.0,(b+t)/2.0).transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
		        var zl = analizeZoomLevel(l,b,r,t,mapobj_header);
		        mapobj_header.setCenter(c,zl);
		        addPolygonFromRect(l,b,r,t,mapobj_header);				//設定範囲を表示
		    }
	    }
	}
}

function popupRangMapDialog() {

	if (!document.getElementById("rangeMapModalDialog")) {	//ダイヤログが2回作成されることを避ける。
		var dialogHtml = '';

		dialogHtml += "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" ;
		dialogHtml += "<tr>" ;

		dialogHtml += "<td style='border: 0 !important; background-color: #CBCFE9 !important;'>" ;
		dialogHtml += "<%=lang.__("Base map")%>:"
		dialogHtml += "<select id='baselayer' name='baselayer'>" ;
		dialogHtml += "<option value = '0' ><%=lang.__("Geographical Survey Institute Tile")%></option>"
		dialogHtml += "<option value = '1' >OpenStreetMap</option>"
		dialogHtml += "</select>"
		dialogHtml += "</td>" ;

		dialogHtml += "</tr>" ;
		dialogHtml += "</table>" ;

		dialogHtml += "<div id='mapctrl_header' style='display:none; position:absolute; background-color:white;z-index:1000;'>" ;
		dialogHtml += "<div id='mapctrlinner_header'>" ;
		dialogHtml += "<div id='mapimgsizer_header'>" ;
		dialogHtml += "<div id='mapPanel_header'></div>" ;
		dialogHtml += "</div>" ;
		dialogHtml += "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" ;
		dialogHtml += "<tr>" ;

		dialogHtml += "<td style='border: 0 !important; background-color: #CBCFE9 !important;'>" ;
		dialogHtml += "<input type='radio' id='rdoScroll' name='mapmoderadio' value='none1' checked='checked' onchange='getSearchBoxControl(mapobj_header)[0].deactivate()'><%=lang.__("Scroll")%>" ;
		dialogHtml += "<input type='radio' id='rdoRange' name='mapmoderadio' value='none2' onchange='getSearchBoxControl(mapobj_header)[0].activate()'><%=lang.__("Range specification")%>" ;
		dialogHtml += "</td>" ;

		dialogHtml += "<td align='right' style='border: 0 !important; background-color: #CBCFE9 !important;'>" ;
		dialogHtml += "<input id='btnSetMapArea' type='button' value='<%=lang.__("Proceed")%>' >" ;
		dialogHtml += "<input type='button' value='<%=lang.__("Cancel")%>' onclick='$.jgrid.hideModal(\"#rangeMapModalDialog\",{jqm:true})'>" ;
		dialogHtml += "</td>" ;

		dialogHtml += "</tr>" ;
		dialogHtml += "</table>" ;
		dialogHtml += "</div>" ;
		dialogHtml += "</div>" ;

		var openDiv = $("[id^='edithdmapmaster_info']");
		var position = openDiv.position();

		//ダイアログ作成
		$.jgrid.createModal(
			IDs = {
				themodal:'rangeMapModalDialog',
				modalhead:'rangeMapModalDialogHd',
				modalcontent:'rangeMapModalDialogCnt',
				scrollelm : ''
			},
			dialogHtml,
			{
				gbox:"#gbox_rangeMapModalDialog",
				jqModal:true,
				drag:true,
				resize:true,
				caption:'<%=lang.__("Range specification")%>',
				top: position.top,
				left: position.left + 580,
				width:600,
				height: 670,
				closeOnEscape:true,
				zIndex:1000
			},
			'',
			'',
			true
		);
	}

	//ダイアログの表示
	$.jgrid.viewModal("#rangeMapModalDialog",{modal:true});

	$('#mapctrl_header').show();

	$("#rangeMapModalDialog").focus();

	$("#mapctrl_header").css("width", $("#rangeMapModalDialog").width());
	$("#mapctrl_header").css("height", $("#rangeMapModalDialog").height()-80);
	$("#mapctrlinner_header").css("width", "100%");
	$("#mapctrlinner_header").css("height", "100%");
	$("#mapimgsizer_header").css("width", "100%");
	$("#mapimgsizer_header").css("height", "100%");
	$("#mapPanel_header").css("width", "100%");
	$("#mapPanel_header").css("height", "100%");

	$('#rangeMapModalDialog').on('mouseup', function(){
		$("#mapctrl_header").css("width", $("#rangeMapModalDialog").width());
		$("#mapctrl_header").css("height", $("#rangeMapModalDialog").height()-80);
		mapobj_header.updateSize();
	});

	// 地理院タイルとOpenStreetMapの切替
	$('#baselayer').change(function() {
		mapobj_header.setBaseLayer(mapobj_header.layers[this.value]);
	});
}
