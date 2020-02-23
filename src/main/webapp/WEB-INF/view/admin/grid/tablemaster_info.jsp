<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function tablemaster_info(subgrid_id, row_id){
	var tabName = 'tablemaster_info';
	var tablemaster_info_gridId = 'main';
	if(subgrid_id != null){
		tablemaster_info_gridId = subgrid_id;
	}
	var tablemaster_info_tabId = tabName;
	if(row_id != null){
		tablemaster_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'mapmaster_info', 'mapmasterinfoid');

	var tablemaster_info_pagerId = tablemaster_info_tabId+'Nav';
	$('#'+tablemaster_info_gridId).append('<table id="'+tablemaster_info_tabId+'" class="scroll"></table><div id="'+tablemaster_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+tablemaster_info_tabId).jqGrid({
		caption: '<%=lang.__("Table master info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Map master info ID")%>',
			'<%=lang.__("Layer ID")%>',
			'<%=lang.__("Table name")%>',
			'<%=lang.__("Name")%>',
			'<%=lang.__("Geometry type")%>',
			'<%=lang.__("Address item name")%>',
			'<%=lang.__("Update date and time item name")%>',
			'<%=lang.__("Coordinate display item name")%>',
			'<%=lang.__("MGRS grid item name")%>',
			'<%=lang.__("Number of MGRS digits")%>',
			'<%=lang.__("Copy method")%>',
			'<%=lang.__("Reset flag")%>',
			'<%=lang.__("Paging")%>',
			'<%=lang.__("Notes")%>',
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
			{name:'mapmasterinfoid',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Map master info ID(*)")%>'},
			hidden:false},
			{name:'layerid',
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Layer ID")%>'},
			hidden:false},
			{name:'tablename',
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Table name ID(*)")%>'},
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Name(*)")%>'},
			hidden:false},
			{name:'geometrytype',
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Geometry type")%>'},
			hidden:false},
			{name:'addresscolumn',
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Address item name")%>'},
			hidden:false},
			{name:'updatecolumn',
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Update date and time item name")%>'},
			hidden:false},
			{name:'coordinatecolumn',
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
			formoptions:{elmprefix:'', elmsuffix:'<a id="callecom_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Search")%><span class="ui-icon ui-icon-search"></span></a>', label:'<%=lang.__("Coordinate display item name")%>'},
			hidden:false},
			{name:'mgrscolumn',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("MGRS grid item name")%>'},
				hidden:false},
			{name:'mgrsdigit',
				width:'150',
				align:'right',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'4',
					readonly:false
					,style:'ime-mode:inactive'
				},
				editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Number of MGRS digits(*)")%>'},
				hidden:false},
			{name:'copy',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'<%=lang.__("0: layer referred to not copied; 1: only latest data copied(without history); 2: copy with history; 3: data copied with empty")%>',
				defaultValue:'1',
				readonly:false
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Copy method(*)")%>'},
			hidden:false},
			{name:'reset',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Reset flag(*)")%>'},
				hidden:false},
			{name:'paging',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Paging")%>(*)'},
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
		pager: tablemaster_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].mapmasterInfo;
				if(ref != null){
					var refkey = ref.id;
					str += refkey + ':' + refkey;
				}

			}
			$('#'+tablemaster_info_tabId).jqGrid('setColProp', 'mapmasterinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+tablemaster_info_tabId).getDataIDs();
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
	$('#'+tablemaster_info_tabId).jqGrid('navGrid',
			'#'+tablemaster_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Table master info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex:100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+tablemaster_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+tablemaster_info_tabId).jqGrid('getRowData', rowid);
					s += '&mapmasterinfoid='+rowdata.mapmasterinfoid;
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_mapmasterinfoid .CaptionTD', formid).after('<td id="dispmapmasterinfoid">'+row_id+'</td>');
					$('#tr_mapmasterinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+tablemaster_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+tablemaster_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){

					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+tablemaster_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+tablemaster_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_mapmasterinfoid #dispmapmasterinfoid', formid).remove();
					$('#tr_mapmasterinfoid .CaptionTD', formid).after('<td id="dispmapmasterinfoid">'+row_id+'</td>');
					$('#tr_mapmasterinfoid .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - Table master info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex:100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+tablemaster_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_mapmasterinfoid .CaptionTD', formid).after('<td id="dispmapmasterinfoid">'+row_id+'</td>');
					$('#tr_mapmasterinfoid .DataTD', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Table master info")%>',
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
								$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+tablemaster_info_pagerId,
			{
				id:'copy_tablemaster_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+tablemaster_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+tablemaster_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Table master info")%>',
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
										$('#tr_mapmasterinfoid .CaptionTD', formid).after('<td id="dispmapmasterinfoid">'+row_id+'</td>');
										$('#tr_mapmasterinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+tablemaster_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+tablemaster_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ tablemaster_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_tablemaster_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
		//最適化ボタン
		).jqGrid(
			'navButtonAdd',
			'#'+tablemaster_info_pagerId,
			{
				id:'optimize_tablemaster_info',
				caption:'',
				buttonicon:'ui-icon-wrench',
				position:'last',
				title:'<%=lang.__("optimization")%>',
				onClickButton:function(e) {
					// ダイアログが作成されていない場合は作成する
					if ($('#optimize_dialog').length <= 0) {
						$('body').append('<div id="optimize_dialog"><\div>');
						$('#optimize_dialog').dialog({
							title: "<%=lang.__("optimization tool")%>",
							modal: true
						});
					}
					// ダイアログを表示する
					$('#optimize_dialog').html('<%=lang.__("Continue to set configurations to features having no coordinates and UTM data?")%>'
					+"</br>"+"<%=lang.__("Add attributes using by system to layer.")%>");
					$('#optimize_dialog').dialog({
						buttons: {
							OK: function() {
								// 最適化を実行
								var url = convertTabNameToMethodMame(tabName)+'/jqgridedit/';
								var data = {
									oper: 'optimize',
									mapmasterinfoid: row_id
								};
//								var csrf_token = '${cookie.JSESSIONID.value}';
								var csrf_token = '${_csrf.token}';
								$('#optimize_dialog').mask("<%=lang.__("Running")%>");
								$.ajax({
									dataType: "json",
									type: "POST",
									cache: false,
									url: url,
									data: data,
									headers: {
										'X-CSRF-Token': csrf_token
								 	},
									success: function(data) {
										// 更新件数を表示する
										$('#optimize_dialog').unmask();
										$('#optimize_dialog').text(data.count + '<%=lang.__("updated.")%>');
										$('#optimize_dialog').dialog({
											buttons: {
												OK: function() {
													$('#optimize_dialog').dialog('close');
												}
											}
										});
									},
									error: function(request, text, error) {
										$('#optimize_dialog').unmask();
								 		alert("<%=lang.__("An error occurred.")%>");
										$('#optimize_dialog').dialog('close');
									}
								});
										},
							'<%=lang.__("Cancel")%>': function() {
								$('#optimize_dialog').dialog('close');
							}
						}
					});
					$('#optimize_dialog').dialog('open');
				}
			 }
		);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+tablemaster_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+tablemaster_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//サブグリッド設定
	$('#'+tablemaster_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				tablecalculatecolumn_info(subgrid_id, row_id);
				timelinetable_info(subgrid_id, row_id);
				tableresetcolumn_data(subgrid_id, row_id);
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
					url:convertTabNameToMethodMame(tabName)+'/jqgridecommap/'+parentgrid_refkey+'/'+row_id+'',
					datatype: 'json',
					colNames: ['<%=lang.__("Community info")%>',
							'<%=lang.__("Group info")%>',
							'<%=lang.__("Map info")%>',
							'<%=lang.__("Layer info")%>',
							'<%=lang.__("Geometry type")%>',
							'<%=lang.__("Address item name")%>',
							'<%=lang.__("Update date and time item name")%>',
							'<%=lang.__("Coordinate display item name")%>'
					],
					colModel: [
						{name:'communityInfo',editable:false,search:true,hidden:false},
						{name:'groupInfo',editable:false,search:true,hidden:false},
						{name:'mapInfo',editable:false,search:true,hidden:false},
						{name:'layerInfo',editable:false,search:true,hidden:false},
						{name:'geometryType',editable:false,search:true,hidden:false},
						{name:'addressColumn',editable:false,search:true,hidden:false},
						{name:'updateColumn',editable:false,search:true,hidden:false},
						{name:'coordinateColumn',editable:false,search:true,hidden:false},
					],
					ajaxGridOptions :{
						beforeSend: function(jqXHR) {
							var csrf_token = '${_csrf.token}';
							jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
						}
					},
					caption: '<%=lang.__("e-Com map map layer info")%>',
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
							// レイヤ情報をレイヤIDと名称に分割
							var rowdata = $('#ecomgrid_'+tabName+row_id).jqGrid('getRowData',rowid);
							var layerInfos = rowdata['layerInfo'].split(":");

							// ジオメトリのタイプを取得
							var geometryType = rowdata['geometryType'];

							// 住所項目名を取得
							var addressColumn = rowdata['addressColumn'];

							// 更新日時項目名を取得
							var updateColumn = rowdata['updateColumn'];

							// 座標表示項目名を取得
							var coordinateColumn = rowdata['coordinateColumn'];

							//eコミデータを親ダイアログの項目へセットする。
							// レイヤIDをセット
							var layeridSelector = '#editmod'+tabName + row_id + ' #layerid';
							$(layeridSelector).val(layerInfos[0]);
							// テーブル名をセット
							var tablenameSelector = '#editmod'+tabName + row_id + ' #tablename';
							$(tablenameSelector).val(layerInfos[0]);
							// 名称をセット
							var nameSelector = '#editmod'+tabName + row_id + ' #name';
							$(nameSelector).val(layerInfos[1]);
							// ジオメトリのタイプをセット
							var geometrytypeSelector = '#editmod'+tabName + row_id + ' #geometrytype';
							$(geometrytypeSelector).val(geometryType);
							// 住所項目名をセット
							var addresscolumnSelector = '#editmod'+tabName + row_id + ' #addresscolumn';
							$(addresscolumnSelector).val(addressColumn);
							// 更新日時項目名をセット
							var updatecolumnSelector = '#editmod'+tabName + row_id + ' #updatecolumn';
							$(updatecolumnSelector).val(updateColumn);
							// 座標表示項目名をセット
							var coordinatecolumnSelector = '#editmod'+tabName + row_id + ' #coordinatecolumn';
							$(coordinatecolumnSelector).val(coordinateColumn);

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
