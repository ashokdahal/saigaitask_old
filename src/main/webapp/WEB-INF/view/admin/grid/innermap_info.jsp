<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function innermap_info(subgrid_id, row_id){
	var tabName = 'innermap_info';
	var thisDialogId = subgrid_id;
	var thisDialog = $('#'+thisDialogId);
	var thisGridId = tabName+row_id;
	var thisNavId = thisGridId+'Nav';
	var parentgrid_refkey = 'menuinfoid';

	//ダイアログのHTML
	var dialogHtml = '';
	bC  ="<a href='javascript:void(0)' id='cancelData_"+tabName+row_id+"' class='fm-button ui-state-default ui-corner-all fm-button-icon-left'><%=lang.__("Cancel")%><span class='ui-icon ui-icon-close'></span></a>";
	var bt = "<table border='0' cellspacing='0' cellpadding='0' class='EditTable' id='TblGrid_"+tabName+row_id+"'><tbody><tr id='Act_Buttons'><td class='EditButton'>"+bC+"</td></tr>";
	bt += "</tbody></table>";
	dialogHtml = '<div id="dialog_'+subgrid_id+'"><table id="'+thisGridId+'" ></table><div id="'+thisNavId+'" ></div>'+bt+'</div>';

	// ダイアログとグリッドの幅は画面の70%にする
	var wWidth = $(window).width();
	var dWidth = wWidth * 0.7;

	//既存ダイアログがない場合
	if(thisDialog.size()==0){
	//ダイアログ作成
		$.jgrid.createModal(
			IDs = {
				themodal:thisDialogId,
				modalhead:thisDialogId+'Hd',
				modalcontent:thisDialogId+'Cnt',
				scrollelm : ''
			},
			dialogHtml,
			{
				gbox:'#gbox_editmod'+tabName+row_id,
				jqModal:true,
				drag:true,
				resize:false,
						caption:'<%=lang.__("Internal map layer info")%>',
//						top:100,
//						left:100,
						width:'auto',
						height: 'auto',
						closeOnEscape:true,
						zIndex: 200,
			},
			'',
			'',
			true
		);

		//ダイアログの表示
		$.jgrid.viewModal('#'+thisDialogId,
			{modal:true,}
		);
		thisDialog.focus();

		//ダイアログにグリッドとナビゲーションを表示
		var thisGrid = $('#'+thisGridId);
		var thisNav = $('#'+thisNavId);

		thisGrid.jqGrid({
			mtype: 'POST',
			url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
			datatype: 'json',
			colNames: [
				'ID',
				'<%=lang.__("Menu ID")%>',
				'<%=lang.__("Layer name<!--2-->")%>',
				'<%=lang.__("Edit")%>',
				'<%=lang.__("List")%>',
				'<%=lang.__("Initially displayed flag")%>',
				'<%=lang.__("Legend folding")%>',
				'<%=lang.__("Snap flag")%>',
				'<%=lang.__("Add flag(map layer)")%>',
				'<%=lang.__("Add flag(list)")%>',
				'<%=lang.__("Delete flag")%>',
				'<%=lang.__("Valid / invalid")%>',
				'<%=lang.__("Display order")%>',
				'<%=lang.__("Menu table ID")%>',
			],
			colModel: [
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
						formatter: 'nullstrToEmptyStrFmatter',
						edittype:'text',
						editable:true,
						editoptions:{value:'',
							defaultValue:row_id,
							readonly:true
							,style:'ime-mode:inactive'
						},
						editrules:{required:false, number:false, integer:false, email:false, edithidden:true},
						sortable:true,
						sorttype:'int',
						search:false,
						searchrules:{required:true},
						formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Menu ID")%>'},
						hidden:true},
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
							var rowid = thisGrid.jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = thisGrid.jqGrid('getRowData', rowid);
								selectedId = rowdata.tablemasterinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, tabName, 'tablemaster_info');
						}
					},
					editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'text',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Layer name(*)")%>'},
					hidden:false},
				{name:'editable',
					width:'150',
					align:'center',
					formatter:"checkbox",
					edittype:"checkbox",
					editable:true,
					editoptions:{value:'true:false',
						defaultValue:'',
						readonly:false
					},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'text',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Edit")%>', disabled : false},
					hidden:false,
					classes:"radio_class"},
				{name:'list',
					width:'150',
					align:'center',
					formatter:"checkbox",
					edittype:"checkbox",
					editable:true,
					editoptions:{value:'true:false',
						defaultValue:'',
						readonly:false
					},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'text',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("List")%>', disabled : false},
					hidden:false,
					classes:"radio_class"},
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Initially displayed flag(*)")%>'},
					hidden:false},
				{name:'closed',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Legend folding(*)")%>'},
					hidden:false},
				{name:'snapable',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Snap flag(*)")%>'},
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Add flag(map layer)(*)")%>'},
					hidden:false},
				{name:'menutableinfoaddable',
					width:'150',
					align:'left',
					formatter: 'select',
					edittype:'select',
					editable:true,
					editoptions:{value:':;true:true;false:false',
						defaultValue:'',
						readonly:false
					},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'text',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Add flag(list)")%>'},
					hidden:false},
				{name:'menutableinfodeletable',
					width:'150',
					align:'left',
					formatter: 'select',
					edittype:'select',
					editable:true,
					editoptions:{value:':;true:true;false:false',
						defaultValue:'',
						readonly:false
					},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'text',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Delete flag")%>'},
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
				{name:'disporder',
					width:'100',
					align:'right',
					formatter: 'nullstrToEmptyStrFmatter',
					edittype:'text',
					editable:true,
					editoptions:{value:'',
						defaultValue:'',
						readonly:false
					},
					editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
					sortable:true,
					sorttype:'int',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Display order(*)")%>'},
					hidden:false},
				{name:'menutableinfoid',
					width:'150',
					align:'left',
					formatter: 'nullstrToEmptyStrFmatter',
					edittype:'text',
					editable:true,
					editoptions:{value:'',
						defaultValue:'',
						readonly:true
						,style:'ime-mode:inactive'
					},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:true},
					sortable:true,
					sorttype:'int',
					search:false,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Menu table ID")%>'},
					hidden:true},
			],
			ajaxGridOptions :{
				beforeSend: function(jqXHR) {
					var csrf_token = '${_csrf.token}';
					jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
				}
			},
			caption: '<%=lang.__("Internal map")%>',
			loadtext:'',
			loadui:'block',
			loadonce:false,
			rowNum:30,
			rowList:[5,10,30,50,100],
			width:950,
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
			pager: '#'+thisNavId,
			autowidth: true,
			autoencode: false,
			beforeProcessing:function(data){
//				console.log(data);
				//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
				var str = '';
				for(i=0; i < data.rows.length; i++){
					if(str != ''){
						str += '|d|'
					}
					var refkey = data.rows[i].tablemasterinfoid;
					var refval = data.rows[i].name;
					str += refkey + ':' + refkey + '：' + refval;
				}
				thisGrid.jqGrid('setColProp', 'tablemasterinfoid', {editoptions:{value:str}});
			},
			loadComplete : function (data) {
				var trOddSelector = '#innermap_grid_'+tabName+row_id+' tr:odd';
				$(trOddSelector).removeClass('ui-widget-content');
				$(trOddSelector).addClass('myodd');
				//表示順のデフォルト値（現在の最大値+1）をセットする。
				var maxDisporder = thisGrid.jqGrid('getGridParam','userData').maxDisporder;
				thisGrid.jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
				if(getEditMode() == 'add'){
					//登録モードの場合、表示順のデフォルト値（現在の最大値+1）をフォームの表示順欄にセットする。
					$('#tr_disporder #disporder').val(maxDisporder+1);
				}
			},
			loadError : function (response, status, error){
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

		//ナビゲーション
		}).navGrid(
			'#'+thisNavId,
			{edit:true,add:true,del:true,search:true,
				beforeRefresh: function(){
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					thisGrid.jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					thisGrid.jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Internal map")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 300,
				beforeShowForm:function(formid){
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
					//編集前データをシリアライズ化してpostDataに一時保存
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+innermapInfoMakeSerializeData(formid, getEditMode(), thisGrid)}});
					//フォーム表示制御
					innermapInfoFormControl(formid, getEditMode(), thisGrid);
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':thisGrid.jqGrid('getGridParam','postData').serializedPreEditData};
				},
				beforeSubmit:function(postdata, formid){
					//カスタム入力チェック
					//メニューIDとテーブルIDでユニークになるようチェックする。
					if(isExistsPropertyInGridData(thisGridId, 'tablemasterinfoid', postdata.tablemasterinfoid)){
						return [false,'<%=lang.__("Can not register the same layer to the same menu.")%>',''];
					}
					return [true,'',''];
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					thisGrid.jqGrid('setGridParam',{datatype:'json'});
					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+innermapInfoMakeSerializeData(formid, getEditMode(), thisGrid)}});
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
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+innermapInfoMakeSerializeData(formid, getEditMode(), thisGrid)}});
					//フォーム表示制御
					innermapInfoFormControl(formid, getEditMode(), thisGrid);
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
				addCaption:'<%=lang.__("Add - Internal map ")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 300,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					thisGrid.jqGrid('resetSelection');
					//フォーム表示制御
					innermapInfoFormControl(formid, getEditMode(), thisGrid);
				},
				beforeSubmit:function(postdata, formid){
					//カスタム入力チェック
					//メニューIDとテーブルIDでユニークになるようチェックする。
					if(isExistsPropertyInGridData(thisGridId, 'tablemasterinfoid', postdata.tablemasterinfoid)){
						return [false,'<%=lang.__("Can not register the same layer to the same menu.")%>',''];
					}
					return [true,'',''];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					thisGrid.jqGrid('setGridParam',{datatype:'json'});
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
			{caption:'<%=lang.__("Delete - Internal map")%>',
				bSubmit:'<%=lang.__("Delete")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 300,
				beforeSubmit:function(postdata, formid){
					return [true,'',''];
				},
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
				closeOnEscape:true,
				zIndex: 300,
			}
		//コピーボタン
		).jqGrid(
			'navButtonAdd',
			'#'+thisNavId,
			{
				id:'copy_'+tabName,
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = thisGrid.jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						thisGrid.jqGrid(
							'editGridRow',
							'new',
							{recreateForm: true,
								addCaption:'<%=lang.__("Record copy - Internal map")%>',
								bSubmit:'<%=lang.__("Save")%>',
								width:'auto',
								modal:true,
								closeAfterAdd:true,
								closeOnEscape:true,
								zIndex: 400,
								beforeShowForm:function(formid){
									//フォームモードにコピーモードを設定
									$(formid).after('<span id="myEditMode" style="display:none">copy</span>');
									//フォーム表示制御
									innermapInfoFormControl(formid, getEditMode(), thisGrid);
								},
								beforeSubmit:function(postdata, formid){
									//カスタム入力チェック
									//メニューIDとテーブルIDでユニークになるようチェックする。
									if(isExistsPropertyInGridData(thisGridId, 'tablemasterinfoid', postdata.tablemasterinfoid)){
										return [false,'<%=lang.__("Can not register the same layer to the same menu.")%>',''];
									}
									return [true,'',''];
								},
								afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
									// 2017.08.09 SV triggerがあると2重リロードするので廃止
									//thisGrid.jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
									thisGrid.jqGrid('setGridParam',{datatype:'json'});
									return [true,''];
								},
								ajaxEditOptions :{
									beforeSend: function(jqXHR) {
//										var csrf_token = '${cookie.JSESSIONID.value}';
										var csrf_token = '${_csrf.token}';
										jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
									}
								}
							}
						);
						var selector = '#FrmGrid_'+ thisGridId;
						//グリッドの選択列データをコピーフォームへデフォルト値としてセットする。
						thisGrid.jqGrid('GridToForm', rowid, $(selector).id);
						//idはコピーしない。
						selector = selector + ' #id';
						$(selector).val('');
						//リストチェックボックスの状態に応じて追加フラグ(リスト)と削除フラグのdisableを切り替える。
						innermapInfoFormControl($('#FrmGrid_'+ thisGridId), getEditMode(), thisGrid);
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
		);	//グリッドナビゲーションend

		//タイトルバーの×ボタンのコールバック関数
		var headerCancelSelector = '#menuInfoInnermapViewlayerConfDialogHd_'+tabName+row_id+' a.ui-jqdialog-titlebar-close';
		$(headerCancelSelector).click(function(){
				thisGrid.jqGrid('clearGridData');
				$.jgrid.hideModal('#'+thisDialogId,{jqm:true});
			}
		);

		//フッターのキャンセルボタンのコールバック関数
		$(document).on(
			'click',
			'#cancelData_'+tabName+row_id,
			function(){
				thisGrid.jqGrid('clearGridData');
				$.jgrid.hideModal('#'+thisDialogId,{jqm:true});
			}
		);
	}else{
		//既存ダイアログがある場合
		//ダイアログの表示
		var thisGrid = $('#'+thisGridId);
		$.jgrid.viewModal('#'+thisDialogId,
			{modal:true,}
		);
		thisDialog.focus();

		//グリッドをリロード
		thisGrid.jqGrid('setGridParam',{datatype:'json'});
		thisGrid.trigger('reloadGrid');
	}

	//サブグリッド設定
	thisGrid.jqGrid('setGridParam',
		{subGridRowExpanded: function(subgrid_id, row_id) {
			maplayerattr_info(subgrid_id, row_id);
			tablelistcolumn_info(subgrid_id, row_id);
		}}
	);	//サブグリッドend
}

function innermapInfoFormControl(formid, mode, grid){
	var rowid = grid.jqGrid('getGridParam', 'selrow');
	//表示制御
	if(mode == 'edit'){
		//idをラベル表示にする。
		$('#tr_id #dispid', formid).remove();
		$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
		$('#tr_id .DataTD', formid).hide();
	}else{
		//idは非表示。
		$('#tr_id', formid).hide();
	}
	//menuinfoidを非表示にする。
	$('#tr_menuinfoid .CaptionTD', formid).hide();
	$('#tr_menuinfoid .DataTD', formid).hide();
	//menutableinfoidを非表示にする。
	$('#tr_menutableinfoid .CaptionTD', formid).hide();
	$('#tr_menutableinfoid .DataTD', formid).hide();

	//リストチェックボックスの状態に応じて追加フラグ(リスト)と削除フラグのdisableを切り替える。
	var listInput = '#tr_list #list';
	var menutableinfoaddableInput = '#tr_menutableinfoaddable #menutableinfoaddable';
	var menutableinfodeletableInput ='#tr_menutableinfodeletable #menutableinfodeletable';
	if($(listInput, formid).prop('checked')){
		$(menutableinfoaddableInput, formid).removeAttr('disabled');
		$(menutableinfodeletableInput, formid).removeAttr('disabled');
	}else{
		$(menutableinfoaddableInput, formid).attr('disabled', 'disabled');
		$(menutableinfodeletableInput, formid).attr('disabled', 'disabled');
	}
	$(listInput, formid).on('click', function(){
		if($(listInput, formid).prop('checked')){
			$(menutableinfoaddableInput, formid).removeAttr('disabled');
			$(menutableinfodeletableInput, formid).removeAttr('disabled');
		}else{
			$(menutableinfoaddableInput, formid).attr('disabled', 'disabled');
			$(menutableinfodeletableInput, formid).attr('disabled', 'disabled');
		}
	});
}

function innermapInfoMakeSerializeData(formid, mode, grid){
	//選択されていないチェックボックスやラジオボタン、disabledに設定された要素などはserialize対象外のため自力でセットする。
	var rowid = grid.jqGrid('getGridParam', 'selrow');
	var s = '';
	if(!$('#tr_list #list', formid).prop('checked')){
		s+='&list=false';
	}
	if(!$('#tr_editable #editable', formid).prop('checked')){
		s+='&editable=false';
	}
	if(!$('#tr_menutableinfoaddable #menutableinfoaddable', formid).attr('disabled') == 'disabled'){
		s+='&menutableinfoaddable=';
	}
	if(!$('#tr_menutableinfodeletable #menutableinfodeletable', formid).attr('disabled') == 'disabled'){
		s+='&menutableinfodeletable=';
	}
	var rowdata = grid.jqGrid('getRowData', rowid);
	if(rowdata.tablemasterinfoid != null){
		s += '&tablemasterinfoid='+rowdata.tablemasterinfoid;
	}else{
		s += '&tablemasterinfoid='+$('#tablemasterinfoid option:selected', formid).val();
	}
//console.log(s);
	return s;
}