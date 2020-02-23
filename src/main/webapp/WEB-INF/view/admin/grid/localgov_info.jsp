<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function localgov_info(subgrid_id, row_id){
	var tabName = 'localgov_info';
	var localgov_info_gridId = 'main';
	if(subgrid_id != null){
		localgov_info_gridId = subgrid_id;
	}
	var localgov_info_tabId = tabName;
	if(row_id != null){
		localgov_info_tabId += row_id;
	}else{
		row_id = '';
	}

	var localgov_info_pagerId = localgov_info_tabId+'Nav';
	$('#'+localgov_info_gridId).append('<table id="'+localgov_info_tabId+'" class="scroll"></table><div id="'+localgov_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+localgov_info_tabId).jqGrid({
		caption: '<%=lang.__("Local gov. info<!--2-->")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Each local gov. domain")%>',
			'<%=lang.__("System name")%>',
			'<%=lang.__("Local gov. type<!--2-->")%>',
			'<%=lang.__("Prefectures (local gov. ID)")%>',
			'<%=lang.__("Prefecture name")%>',
			'<%=lang.__("Prefecture code")%>',
			'<%=lang.__("City")%>',
			'<%=lang.__("City code")%>',
			'<%=lang.__("Reserve")%>',
			'<%=lang.__("Auto alarm flag")%>',
			'<%=lang.__("Group name in case of auto alarm (L-Alert creator info)")%>',
			'<%=lang.__("Acquisition interval of alarm")%>',
			'<%=lang.__("SMTP server")%>',
			'<%=lang.__("E-mail address for sending")%>',
			'<%=lang.__("Coordinate decimal display")%>',
			'<%=lang.__("Language code")%>',
			'<%=lang.__("Logo image file")%>',
			'<%=lang.__("Logo image file upload")%>',
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
			{name:'domain',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Each local gov. domain(*)")%>'},
			hidden:false},

			{name:'systemname',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("System name")%>'},
				hidden:false},

			{name:'localgovtypeid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/localgovtypeid/localgovtype_master/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+localgov_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.localgovtypeid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'localgov_info', 'localgovtype_master');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Local gov. type(*)")%>'},
			hidden:false},
			{name:'preflocalgovinfoid',
				width:'150',
				align:'right',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'select',
				editable:true,
				editoptions:{value:':',
					defaultValue:'',
					readonly:false,
				},
				editrules:{required:false, number:false, integer:true, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Prefectures (local gov. ID)")%>'},
				hidden:false},

			{name:'pref',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Prefecture name(*)")%>'},
			hidden:false},
			{name:'prefcode',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Prefecture code(*)")%>'},
			hidden:false},
			{name:'city',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("City")%>'},
			hidden:false},
			{name:'citycode',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("City code")%>'},
			hidden:false},
			{name:'section',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Reserve")%>'},
			hidden:false},
			{name:'autostart',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Auto alarm flag(*)")%>'},
			hidden:false},

			{name:'autostartgroupinfoid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/autostartgroupinfoid/group_info/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+localgov_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.autostartgroupinfoid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'localgov_info', 'group_info');
					}
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Group name in case of auto alarm (L-Alert creator info)")%>'},
			hidden:false},


			{name:'alarminterval',
			width:'150',
			align:'right',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'text',
			editable:true,
			editoptions:{value:'',
				defaultValue:'120',
				readonly:false
				,style:'ime-mode:inactive'
			},
			editrules:{required:true, number:false, integer:true, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Acquisition interval of alarm(*)")%>'},
			hidden:false},
			{name:'smtp',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("SMTP server")%>'},
			hidden:false},
			{name:'email',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("E-mail address for sending(*)")%>'},
			hidden:false},

			{name:'coordinatedecimal',
				width:'150',
				align:'left',
				formatter: 'select',
				edittype:'select',
				editable:true,
				editoptions:{value:'<%=lang.__("True: enable; false: disable")%>',
					defaultValue:'false',
					readonly:false
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Coordinate decimal display(*)")%>'},
				hidden:false},

			{name:'multilanginfoid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/localgovtypeid/multilang_info/id/code',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+localgov_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.localgovtypeid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'localgov_info', 'multilang_info');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Language code")%>'},
			hidden:false},

			{name:'logoimagefile',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Logo image file")%>'},
			hidden:false},

			{name:'logoimagefileupload',
			width:'210',
			align:'left',
			edittype:'file',
			editable:true,
			editoptions:{
				enctype: "multipart/form-data"
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:true},
			sortable:false,
			search:false,
			hidden:true},

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
		subGrid: false,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: localgov_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].localgovtypeMaster;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+localgov_info_tabId).jqGrid('setColProp', 'localgovtypeid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].groupInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}
			}
			$('#'+localgov_info_tabId).jqGrid('setColProp', 'autostartgroupinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				//console.log(data.rows[i]);
				var ref = data.rows[i].multilangInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.code;
					str += refkey + ':' + refkey + '：' + refval;
				}
			}
			$('#'+localgov_info_tabId).jqGrid('setColProp', 'multilanginfoid', {editoptions:{value:str}});
		},
		loadComplete : function (data) {
			//ページボタンセンター配置対策
			$("#localgov_infoNav_left").css("width","");
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+localgov_info_tabId).getDataIDs();
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
	$('#'+localgov_info_tabId).jqGrid('navGrid',
			'#'+localgov_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+localgov_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+localgov_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Local gov. info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+localgov_info_tabId).jqGrid('getRowData', rowid);
					s += '&localgovtypeid='+rowdata.localgovtypeid;
					var rowdata = $('#'+localgov_info_tabId).jqGrid('getRowData', rowid);
					s += '&autostartgroupinfoid='+rowdata.autostartgroupinfoid;

					$('#'+localgov_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				beforeInitData:function(formid){
					//「地方自治体種別」のプルダウンが作成さた後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$.ajaxSetup({ async: true });
					//「都道府県(自治体ID)」のプルダウン作成
					initPrefSelect();
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+localgov_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(response, postdata){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					uploadFile(response, postdata);
					
					//サブミット後、グリッド再取得
					var pagenum = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+localgov_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+localgov_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+localgov_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+localgov_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
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
				addCaption:'<%=lang.__("Add - Local gov. info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+localgov_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
				},
				beforeInitData:function(formid){
					//「地方自治体種別」のプルダウンが作成された後で afterShowForm が呼ばれるように、一時的に非同期動作を禁止する
					$.ajaxSetup({ async: false });
				},
				afterShowForm:function(formid){
					$.ajaxSetup({ async: true });
					//「都道府県(自治体ID)」のプルダウン作成
					initPrefSelect();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(response, postdata){
					uploadFile(response, postdata);
					//サブミット後、グリッド再取得
					$('#'+localgov_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Local gov. info")%>',
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
								$('#'+localgov_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+localgov_info_pagerId,
			{
				id:'copy_localgov_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//「地方自治体種別」のプルダウンが作成された後で initPrefSelect() が呼ばれるように、一時的に非同期動作を禁止する
						$.ajaxSetup({ async: false });

						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+localgov_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Local gov. info")%>',
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
										//$('#'+localgov_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+localgov_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+localgov_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ localgov_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_localgov_info #id').val('');
						//「都道府県(自治体ID)」のプルダウン作成
						initPrefSelect();
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+localgov_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+localgov_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');


	//「都道府県(自治体ID)」のプルダウン初期化
	function initPrefSelect() {
		$('#localgovtypeid').on('change', makePrefSelect);
		$('#prefcode').change(makePrefSelect);
		makePrefSelect();
	}

	//「都道府県(自治体ID)」のプルダウン作成
	function makePrefSelect() {
		$('#preflocalgovinfoid').children().remove();
		var type = $('#localgovtypeid').val();
		var pref = $('#prefcode').val();
		//if (type != 2 || !pref) {
		// 種別が都道府県、または県コードがない場合
		if (type==1 || !pref) {
			$('#preflocalgovinfoid').append('<option value=""></option>');
			return;
		}
		// 種別が市区町村、その他の場合で県コードがあれば一覧を取得
		var url = convertTabNameToMethodMame(tabName)+'/createSelectTag/localgovtypeid/preflocalgov_info/id/pref?prefCode=' + pref + '';
		$.ajax({
			dataType: "json",
			type: "GET",
			cache: false,
			url: url,
			success: function(data) {
				var selectTag = data.selectTag;
				var rowid = $('#'+localgov_info_tabId).jqGrid('getGridParam', 'selrow');
				var rowdata = $('#'+localgov_info_tabId).jqGrid('getRowData', rowid);
				selectedId = rowdata.preflocalgovinfoid;
				for (var i = 0; i < selectTag.length; i++) {
					var selected = (selectTag[i].key == selectedId) ? 'selected=" selected"' : '';
					$('#preflocalgovinfoid').append('<option value="'+ selectTag[i].key + '"' + selected + '>' + selectTag[i].value + '</option>');
				}
				$('#preflocalgovinfoid').append('<option value=""></option>');
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("error : " + errorThrown);
			}
		});
	}

}

function uploadFile(response, postdata) {
	if ($("#logoimagefileupload").val() != "") {
		if (!postdata.id){
			postdata.id = JSON.parse(response.responseText).newEntity.id;
		}
		ajaxFileUpload(postdata);
	}
}

function ajaxFileUpload(postdata){
	$("#loading").ajaxStart(function () {
		$(this).show();
	}).ajaxComplete(function () {
		$(this).hide();
	});

	var fd = {
		"id": postdata['id'],
		"_csrf": '${_csrf.token}'
	};
	$.ajaxFileUpload({
		url: '${f:url("localgovInfo/upload/")}',
		secureuri: false,
		fileElementId: 'logoimagefileupload',
		dataType: 'json',
		type: 'post',
		data: fd,
		async: false,
		success: function(data) {
			$('#logoimagefile').val(data.editEntity.logoimagefile);
			if (data.message) {
				showMessage('localgov_info', jsonObj.message, $.jgrid.nav.alertcap);
			}
			else {
				alert("<%=lang.__("Logo image file upload complete.")%>");
			}
		},
		error: function(data, status, e) {
			if(data.responseText){
				try{
					var responseJson = $.parseJSON(data.responseText);
					alert(unescapeHTML(responseJson.message));
				}
				catch(e){
					alert("<%=lang.__("Failed to upload the logo image file.") %>");
				}
			}else{
					alert("<%=lang.__("Failed to upload the logo image file.") %>");
			}
		}
	});
}

function unescapeHTML(str){
    return str.replace(/&lt;/g,'<')
               .replace(/&gt;/g,'>')
               .replace(/&amp;/g,'&');
}
