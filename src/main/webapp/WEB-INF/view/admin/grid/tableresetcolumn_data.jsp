<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function tableresetcolumn_data(subgrid_id, row_id){
	var tabName = 'tableresetcolumn_data';
	var tableresetcolumn_data_gridId = 'main';
	if(subgrid_id != null){
		tableresetcolumn_data_gridId = subgrid_id;
	}
	var tableresetcolumn_data_tabId = tabName;
	if(row_id != null){
		tableresetcolumn_data_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'tablemaster_info', 'tablemasterinfoid');

	var tableresetcolumn_data_pagerId = tableresetcolumn_data_tabId+'Nav';
	$('#'+tableresetcolumn_data_gridId).append('<table id="'+tableresetcolumn_data_tabId+'" class="scroll"></table><div id="'+tableresetcolumn_data_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+tableresetcolumn_data_tabId).jqGrid({
		caption: '<%=lang.__("Table reset target attribution info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Table ID")%>',
			'<%=lang.__("Attribute item name")%>',
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
			{name:'tablemasterinfoid',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Table ID(*)")%>'},
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
//				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Attribute item name")%>'},
				formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Attribute item name")%>'},
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
		pager: tableresetcolumn_data_pagerId,
		autowidth: true,
		autoencode: true,
		beforeProcessing:function(data){
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
			$('#'+tableresetcolumn_data_tabId).jqGrid('setColProp', 'tablemasterinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+tableresetcolumn_data_tabId).getDataIDs();
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
	$('#'+tableresetcolumn_data_tabId).jqGrid('navGrid',
			'#'+tableresetcolumn_data_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Table reset target info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+tableresetcolumn_data_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+tableresetcolumn_data_tabId).jqGrid('getRowData', rowid);
					s += '&tablemasterinfoid='+rowdata.tablemasterinfoid;
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_tablemasterinfoid .CaptionTD', formid).after('<td id="disptablemasterinfoid">'+row_id+'</td>');
					$('#tr_tablemasterinfoid .DataTD', formid).hide();

					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+tableresetcolumn_data_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{datatype:'json'});
					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
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
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_tablemasterinfoid #disptablemasterinfoid', formid).remove();
					$('#tr_tablemasterinfoid .CaptionTD', formid).after('<td id="disptablemasterinfoid">'+row_id+'</td>');
					$('#tr_tablemasterinfoid .DataTD', formid).hide();

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
				addCaption:'<%=lang.__("Add - Table reset target info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+tableresetcolumn_data_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_tablemasterinfoid .CaptionTD', formid).after('<td id="disptablemasterinfoid">'+row_id+'</td>');
					$('#tr_tablemasterinfoid .DataTD', formid).hide();

				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Table reset target info")%>',
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
			'#'+tableresetcolumn_data_pagerId,
			{
				id:'copy_tableresetcolumn_data',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+tableresetcolumn_data_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+tableresetcolumn_data_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Copy - Table reset target info")%>',
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
										$('#tr_tablemasterinfoid .CaptionTD', formid).after('<td id="disptablemasterinfoid">'+row_id+'</td>');
										$('#tr_tablemasterinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+tableresetcolumn_data_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+tableresetcolumn_data_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ tableresetcolumn_data_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_tableresetcolumn_data #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+tableresetcolumn_data_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+tableresetcolumn_data_tabId).setGridWidth(getGridWidth(thisGridLevel));
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
