<%/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
//# sourceURL=admin/grid/mobileqrcode_info.jsp
function mobileqrcode_info(subgrid_id, row_id){
	var tabName = 'mobileqrcode_info';
	var mobileqrcode_info_gridId = 'main';
	if(subgrid_id != null){
		mobileqrcode_info_gridId = subgrid_id;
	}
	var mobileqrcode_info_tabId = tabName;
	if(row_id != null){
		mobileqrcode_info_tabId += row_id;
	}else{
		row_id = '';
	}

	var groupidALL = "-1："+lang.__("all group in common");
	var unitidALL  = "-1："+lang.__("all unit in common");

	var mobileqrcode_info_pagerId = mobileqrcode_info_tabId+'Nav';
	$('#'+mobileqrcode_info_gridId).append('<table id="'+mobileqrcode_info_tabId+'" class="scroll"></table><div id="'+mobileqrcode_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+mobileqrcode_info_tabId).jqGrid({
		caption: '<%=lang.__("post-appli authorized QR setting")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Local gov. ID")%>',
			'<%=lang.__("post-appli")%>',
			'<%=lang.__("QR name")%>',
			'<%=lang.__("authorized group ID")%>',
			'<%=lang.__("authorized unit ID")%>',
			'<%=lang.__("post-to table ID")%>',
			'<%=lang.__("authorized finish date")%>',
			'<%=lang.__("Notes")%>',
			'<%=lang.__("Display order")%>',
			'<%=lang.__("Valid / invalid")%>'
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
							var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.localgovinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'mobileqrcode_info', 'localgov_info', false);
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Local gov. ID(*)")%>'},
				hidden:false},
			{name:'oauthconsumerid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/oauthconsumerid/_oauth_consumer/id/id',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.localgovinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'mobileqrcode_info', '_oauth_consumer', false);
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("post-appli")%>(*)'},
				hidden:false},
			{name:'title',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("QR name")%>(*)'},
				hidden:false},
			{name:'groupid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/groupid/group_info/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.groupid;
						}else{
						}
						selectTag.unshift({key:'-1',value:groupidALL});
						return createSelectTag(rowid, selectTag, selectedId, 'mobileqrcode_info', 'group_info','true');
					}
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'int',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("authorized group ID")%>'},
			hidden:false},
			{name:'unitid',
			width:'150',
			align:'left',
			formatter: 'customSelect',
			edittype:'select',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				delimiter:'|d|',
				readonly:false
					,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/unitid/unit_info/id/name',
					//フォームのプルダウン作成
					buildSelect: function (data) {
						var selectTag = $.parseJSON(data).selectTag;
						var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						console.log('L108 rowid=', rowid);
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.unitid;
							console.log('L81 selectedId=', selectedId);
						}else{
						}
						selectTag.unshift({key:'-1',value:unitidALL});
						return createSelectTag(rowid, selectTag, selectedId, 'mobileqrcode_info', 'unit_info','true');
					}
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'int',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("authorized unit ID")%>'},
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
							var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.tablemasterinfoid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'mobileqrcode_info', 'tablemaster_info');
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("post-to table ID")%>(*)'},
				hidden:false},
			{name:'authenticationdate',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("authorized finish date")%>(*)'},
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
		subGrid: false,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: mobileqrcode_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].localgovinfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.pref + ref.city;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+mobileqrcode_info_tabId).jqGrid('setColProp', 'localgovinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var oauthconsumers = JSON.parse(data.userdata.oauthconsumer);
			var oauthconsumerSelect = {};
			for(var idx in oauthconsumers) {
				var oauthconsumer = oauthconsumers[idx];
				oauthconsumerSelect[oauthconsumer.key] = oauthconsumer.value;
			}
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].oauthconsumerid;
				if(ref != null){
					var selectTag = oauthconsumerSelect[ref];
					if(!!selectTag) str+=ref+':'+selectTag;
					else str += ref+':'+ref+'：';
				}

			}
			$('#'+mobileqrcode_info_tabId).jqGrid('setColProp', 'oauthconsumerid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].group;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}
				if(data.rows[i].groupid==-1) {
					str += '-1' + ':'+groupidALL;
				}

			}
			$('#'+mobileqrcode_info_tabId).jqGrid('setColProp', 'groupid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].unit;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}
				if(data.rows[i].unitid==-1) {
					str += '-1' + ':'+unitidALL;
				}

			}
			$('#'+mobileqrcode_info_tabId).jqGrid('setColProp', 'unitid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].tablemasterinfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+mobileqrcode_info_tabId).jqGrid('setColProp', 'tablemasterinfoid', {editoptions:{value:str}});

			// Timestamp 対応
			for(i=0; i < data.rows.length; i++){
				var row = data.rows[i];
				row.authenticationdate = FalUtil.toTimestampText(row.authenticationdate, "yyyy-MM-dd");
			}
		},
		loadComplete : function (data) {
			console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+mobileqrcode_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
			//表示順のデフォルト値（現在の最大値+1）をセットする。
			var maxDisporder = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam','userData').maxDisporder;
			$('#'+mobileqrcode_info_tabId).jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
			if(getEditMode() == 'add'){
				//登録モードの場合、表示順のデフォルト値（現在の最大値+1）をフォームの表示順欄にセットする。
				$('#tr_disporder #disporder').val(maxDisporder+1);
				console.log("ADD");
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
	$('#'+mobileqrcode_info_tabId).jqGrid('navGrid',
			'#'+mobileqrcode_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit")%>'+' - '+'<%=lang.__("post-appli authorized QR setting")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
					s += '&localgovinfoid='+rowdata.localgovinfoid;
					var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
					s += '&groupid='+rowdata.groupid;
					var rowdata = $('#'+mobileqrcode_info_tabId).jqGrid('getRowData', rowid);
					s += '&unitid='+rowdata.unitid;
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
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
					return {'serializedPreEditData':$('#'+mobileqrcode_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+mobileqrcode_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
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
				addCaption:'<%=lang.__("Add")%>'+' - '+'<%=lang.__("post-appli authorized QR setting")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+mobileqrcode_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					postdata.authenticationdate = postdata.authenticationdate.replace(/\//g,"-")
					//TODO: validate authenticationdate "yyyy-mm-dd"
					// return [false, "<%=lang.__("Invalid input!")%>"]
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete")%>'+' - '+'<%=lang.__("post-appli authorized QR setting")%>',
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
								$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+mobileqrcode_info_pagerId,
			{
				id:'copy_mobileqrcode_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+mobileqrcode_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+mobileqrcode_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy")%>'+' - '+'<%=lang.__("post-appli authorized QR setting")%>',
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
										$('#'+mobileqrcode_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										return [true,''];
									},
									ajaxEditOptions :{
										beforeSend: function(jqXHR) {
											var csrf_token = '${cookie.JSESSIONID.value}';
											jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
										}
									}
								}
							);
						//グリッドの選択列データをコピーフォームへデフォルト値としてセットする。
						$('#'+mobileqrcode_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ mobileqrcode_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_mobileqrcode_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+mobileqrcode_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+mobileqrcode_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');



}
