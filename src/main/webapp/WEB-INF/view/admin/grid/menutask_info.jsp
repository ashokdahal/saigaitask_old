<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function menutask_info(subgrid_id, row_id){
	var tabName = 'menutask_info';
	var menutask_info_gridId = 'main';
	if(subgrid_id != null){
		menutask_info_gridId = subgrid_id;
	}
	var menutask_info_tabId = tabName;
	if(row_id != null){
		menutask_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'menuprocess_info', 'menuprocessinfoid');

	var menutask_info_pagerId = menutask_info_tabId+'Nav';
	$('#'+menutask_info_gridId).append('<table id="'+menutask_info_tabId+'" class="scroll"></table><div id="'+menutask_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+menutask_info_tabId).jqGrid({
		caption: '<%=lang.__("Menu task info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Process ID")%>',
			'<%=lang.__("Task type")%>',
			'<%=lang.__("Name")%>',
			'<%=lang.__("vital flag")%>',
			'<%=lang.__("Display / hide")%>',
			'<%=lang.__("Display order")%>',
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
			{name:'menuprocessinfoid',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Process ID(*)")%>'},
			hidden:false},
			{name:'menutasktypeinfoid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/menutasktypeinfoid/menutasktype_info/id/name',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+menutask_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+menutask_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.menutasktypeinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'menutask_info', 'menutasktype_info', false);
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Task type(*)")%>'},
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
			{name:'important',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'false:false;true:true',
				defaultValue:'false',
				readonly:false
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("vital flag (*)")%>'},
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
		sortname: 'disporder',
		sortorder: 'ASC',
		subGrid: true,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: menutask_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].menuprocessInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+menutask_info_tabId).jqGrid('setColProp', 'menuprocessinfoid', {editoptions:{value:str}});
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
			$('#'+menutask_info_tabId).jqGrid('setColProp', 'menutasktypeinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+menutask_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
			//表示順のデフォルト値（現在の最大値+1）をセットする。
			var maxDisporder = $('#'+menutask_info_tabId).jqGrid('getGridParam','userData').maxDisporder;
			$('#'+menutask_info_tabId).jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
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
	$('#'+menutask_info_tabId).jqGrid('navGrid',
			'#'+menutask_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+menutask_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+menutask_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Menu task info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+menutask_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+menutask_info_tabId).jqGrid('getRowData', rowid);
					s += '&menuprocessinfoid='+rowdata.menuprocessinfoid;
					var rowdata = $('#'+menutask_info_tabId).jqGrid('getRowData', rowid);
					s += '&menutasktypeinfoid='+rowdata.menutasktypeinfoid;


					$('#'+menutask_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menuprocessinfoid .CaptionTD', formid).after('<td id="dispmenuprocessinfoid">'+row_id+'</td>');
					$('#tr_menuprocessinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+menutask_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+menutask_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menutask_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+menutask_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+menutask_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menutask_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+menutask_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menuprocessinfoid #dispmenuprocessinfoid', formid).remove();
					$('#tr_menuprocessinfoid .CaptionTD', formid).after('<td id="dispmenuprocessinfoid">'+row_id+'</td>');
					$('#tr_menuprocessinfoid .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - Menu task info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+menutask_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_menuprocessinfoid .CaptionTD', formid).after('<td id="dispmenuprocessinfoid">'+row_id+'</td>');
					$('#tr_menuprocessinfoid .DataTD', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+menutask_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Menu task info")%>',
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
								$('#'+menutask_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+menutask_info_pagerId,
			{
				id:'copy_menutask_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+menutask_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+menutask_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Menu task info")%>',
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
										$('#tr_menuprocessinfoid .CaptionTD', formid).after('<td id="dispmenuprocessinfoid">'+row_id+'</td>');
										$('#tr_menuprocessinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+menutask_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+menutask_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+menutask_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ menutask_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_menutask_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+menutask_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+menutask_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//サブグリッド設定
	$('#'+menutask_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
//				menu_info(subgrid_id, row_id);
				menutaskmenu_info(subgrid_id, row_id);
			}}
	);	//サブグリッドend


}
