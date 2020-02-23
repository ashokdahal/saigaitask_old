<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function group_info(subgrid_id, row_id){
	var tabName = 'group_info';
	var group_info_gridId = 'main';
	if(subgrid_id != null){
		group_info_gridId = subgrid_id;
	}
	var group_info_tabId = tabName;
	if(row_id != null){
		group_info_tabId += row_id;
	}else{
		row_id = '';
	}

	var group_info_pagerId = group_info_tabId+'Nav';
	$('#'+group_info_gridId).append('<table id="'+group_info_tabId+'" class="scroll"></table><div id="'+group_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+group_info_tabId).jqGrid({
		caption: '<%=lang.__("Group info<!--2-->")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Local gov. ID")%>',
			'<%=lang.__("Group name<!--2-->")%>',
			'<%=lang.__("Login password")%>',
			'<%=lang.__("e-Com map account")%>',
			<%-- eコミマップのアカウントパスワードは未使用のため非表示にする --%>
			//'eコミマップのアカウントパスワード',
			'<%=lang.__("Admin privileges")%>',
			'<%=lang.__("HQ authority")%>',
			'<%=lang.__("Home(WKT)")%>',
			'<%=lang.__("Resolution (default 0)")%>',
			'<%=lang.__("Group kana (L-Alert delivery creator info)")%>',
			'<%=lang.__("Phone number (L-Alert delivery creator info)")%>',
			'<%=lang.__("FAX number (L-Alert creator info)")%>',
			'<%=lang.__("E-mail address (L-Alert creator info)")%>',
			'<%=lang.__("Address (L-Alert post creator info)")%>',
			'<%=lang.__("Domain (L-Alert delivery creator info)")%>',
			'<%=lang.__("API key")%>',
			'<%=lang.__("Notes")%>',
			'<%=lang.__("Display order")%>',
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
						var rowid = $('#'+group_info_tabId).jqGrid('getGridParam', 'selrow');
						var selectedId = null;
						if(rowid != null){
							//編集の場合
							var rowdata = $('#'+group_info_tabId).jqGrid('getRowData', rowid);
							selectedId = rowdata.localgovinfoid;
						}else{
						}
						return createSelectTag(rowid, selectTag, selectedId, 'group_info', 'localgov_info');
					}
			},
			editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'int',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Local gov. ID(*)")%>'},
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Group name(*)<!--2-->")%>'},
			hidden:false},
			{name:'password',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'text',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				readonly:false
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:true},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Login password(*)")%>'},
			hidden:true},
			{name:'ecomuser',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("e-Com map account(*)")%>'},
			hidden:false},
//			{name:'ecompass',
//			width:'150',
//			align:'left',
//			formatter: 'nullstrToEmptyStrFmatter',
//			edittype:'text',
//			editable:true,
//			editoptions:{value:'',
//				defaultValue:'',
//				readonly:false
//			},
//			editrules:{required:true, number:false, integer:false, email:false, edithidden:true},
//			sortable:true,
//			sorttype:'text',
//			search:true,
//			searchrules:{required:true},
//			formoptions:{elmprefix:'', elmsuffix:'', label:'eコミマップのアカウントパスワード(*)'},
//			hidden:true},
			{name:'admin',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Admin privileges(*)")%>'},
			hidden:false},
			{name:'headoffice',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("HQ authority(*)")%>'},
			hidden:false},
			{name:'extent',
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
			label:'<%=lang.__("Home(WKT)")%>'},
			hidden:false},
			{name:'resolution',
			width:'150',
			align:'left',
			formatter: 'select',
			edittype:'select',
			editable:true,
			editoptions:{value:'<%=lang.__("0:not specified ;0.0004762:1/150000;0.0002222:1/70000;0.0001111:1/35000; 0.0000556:1/17500; 0.0000286:1/9000; 0.0000127:1/4000;0.0000063:1/2000; 0.0000032:1/1000; 0.0000016:1/500")%>',
				defaultValue:'0',
				readonly:false
				,style:'ime-mode:inactive'
			},
			editrules:{required:true, number:true, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Resolution (default 0)")%><%=lang.__("(STANDARD)")%>'},
			hidden:false},
			{name:'namekana',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'text',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Group kana (L-Alert delivery creator info)")%>'},
			hidden:false},
			{name:'phone',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Phone number (L-Alert delivery creator info)")%>'},
			hidden:false},
			{name:'fax',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("FAX number (L-Alert creator info)")%>'},
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
			editrules:{required:false, number:false, integer:false, email:true, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("E-mail address (L-Alert creator info)")%>'},
			hidden:false},
			{name:'address',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'text',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Address (L-Alert post creator info)")%>'},
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
				,style:'ime-mode:active'
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Domain (L-Alert delivery creator info)")%>'},
			hidden:false},
			{name:'apikey',
			width:'150',
			align:'left',
			formatter: 'nullstrToEmptyStrFmatter',
			edittype:'text',
			editable:true,
			editoptions:{value:'',
				defaultValue:'',
				readonly:true
			},
			editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
			sortable:true,
			sorttype:'text',
			search:true,
			searchrules:{required:true},
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("API key")%>'},
			hidden:false},
			{name:'note',
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
		subGrid: true,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: group_info_pagerId,
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
				}else{
					str += '0:';
				}

			}
			$('#'+group_info_tabId).jqGrid('setColProp', 'localgovinfoid', {editoptions:{value:str}});

		},
		onSelectRow:function(rowid){
			//システム管理者レコードの場合、削除ボタン、コピーボタンを非表示。
			if(rowid == '0'){
				$("#del_group_info").hide();
				$("#copy_group_info").hide();
			}else{
				$("#del_group_info").show();
				$("#copy_group_info").show();
			}
		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+group_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
			//表示順のデフォルト値（現在の最大値+1）をセットする。
			var maxDisporder = $('#'+group_info_tabId).jqGrid('getGridParam','userData').maxDisporder;
			$('#'+group_info_tabId).jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
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
	$('#'+group_info_tabId).jqGrid('navGrid',
			'#'+group_info_pagerId,
			//ナビゲーションオプション設定
			{edit:${admin},add:${admin},del:${admin},refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+group_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Group info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+group_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+group_info_tabId).jqGrid('getRowData', rowid);
					s += '&localgovinfoid='+rowdata.localgovinfoid;
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					var rowid = $('#'+group_info_tabId).jqGrid('getGridParam', 'selrow');
					var data = $('#'+group_info_tabId).jqGrid('getRowData', rowid);
					//暗号化データは非表示
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptpassword:data.password}});
					$('#password', formid).val('');
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptecompass:data.ecompass}});
					$('#ecompass', formid).val('');
					//システム管理者レコードの場合、自治体idは非表示。
					if(rowid == '0'){
						$('#tr_localgovinfoid', formid).hide();
					}
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					//APIキーをラベル表示にする。
					$('#tr_apikey .CaptionTD', formid).after('<td id="dispapikey">'+rowdata.apikey+'</td>');
					$('#tr_apikey .DataTD input[type="text"]', formid).hide();
					// APIキー編集ラジオボタンを追加する。
					if($('#groupinfo_apikeymodify').length == 0){
						$('<span id="groupinfo_apikeymodify"><span><input type="radio" name="groupinfo_apikeymodify_radio" id="groupinfo_apikeymodify_1" value="reissue"><%=lang.__("Reissue")%></span><span><input type="radio" name="groupinfo_apikeymodify_radio" id="groupinfo_apikeymodify_2" value="clear"><%=lang.__("Delete")%></span></span>').insertAfter('#tr_apikey #dispapikey');
					}
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+group_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				// 班名の重複チェック
				beforeSubmit:function(postdata, formid){
					var rowid = $('#'+group_info_tabId).jqGrid('getGridParam', 'selrow');

					//班名の重複チェック
					if( !name_validate(postdata,"edit", rowid) ){
						return [false,"<%=lang.__("Edit: group name duplicated. Enter other name.")%>",""];
					}
					// APIキー編集情報をリクエストに追加する
					postdata.apikeymodify = $('input[name=groupinfo_apikeymodify_radio]:checked').val();

					return [true,"",""];
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+group_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+group_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});
					
					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//暗号化データがあればグリッドとフォームで保持
					var rowid = $('#'+group_info_tabId).jqGrid('getGridParam', 'selrow');
					var res = $.parseJSON(response.responseText);
					if(res != null && res.editEntity != null &&res.editEntity.password != ""){
						//新たに暗号化したデータがある場合
						$('#'+group_info_tabId).jqGrid('setRowData', rowid, {password:res.editEntity.password});
						$('#password', formid).val(res.editEntity.password);
						$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptpassword:res.editEntity.password}});
					}else{
						//新たに暗号化したデータがない場合
						var oldVal = $('#'+group_info_tabId).jqGrid('getGridParam').postData.cryptpassword;
						$('#'+group_info_tabId).jqGrid('setRowData', rowid, {password:oldVal});
						$('#password', formid).val(oldVal);
					}
					if(res != null && res.editEntity != null &&res.editEntity.ecompass != ""){
						//新たに暗号化したデータがある場合
						$('#'+group_info_tabId).jqGrid('setRowData', rowid, {ecompass:res.editEntity.ecompass});
						$('#ecompass', formid).val(res.editEntity.ecompass);
						$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptecompass:res.editEntity.ecompass}});
					}else{
						//新たに暗号化したデータがない場合
						var oldVal = $('#'+group_info_tabId).jqGrid('getGridParam').postData.cryptecompass;
						$('#'+group_info_tabId).jqGrid('setRowData', rowid, {ecompass:oldVal});
						$('#ecompass', formid).val(oldVal);
					}
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//暗号化データは非表示
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptpassword:$('#password', formid).val()}});
					$('#password', formid).val('');
					//暗号化データは非表示
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptecompass:$('#ecompass', formid).val()}});
					$('#ecompass', formid).val('');
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					// 編集ダイアログ上のAPIキーラベル再描画
					$('#dispapikey').text(res.editEntity.apikey);
					$('#tr_apikey .DataTD #apikey').val(res.editEntity.apikey);
					// APIキー編集ラジオボタンチェック解除
					$('input[name=groupinfo_apikeymodify_radio]').prop('checked',false);
					
					var pagenum = $('#'+group_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+group_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//システム管理者レコードの場合、自治体idプルダウンにダミーを追加。
					if(rowid == '0'){
						$('#localgovinfoid', formid).prepend('<option id="localgovinfoid_mgr" selected value="0">0</option>');
					}else{
						$('#localgovinfoid option[value=0]', formid).remove();
					}
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//システム管理者レコードの場合、自治体idは非表示。
					if(rowid == '0'){
						$('#tr_localgovinfoid', formid).hide();
					}else{
						$('#tr_localgovinfoid', formid).show();
					}
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					//暗号化データは非表示
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptpassword:$('#password', formid).val()}});
					$('#password', formid).val('');
					//暗号化データは非表示
					$('#'+group_info_tabId).jqGrid('setGridParam',{postData:{cryptecompass:$('#ecompass', formid).val()}});
					$('#ecompass', formid).val('');
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
				addCaption:'<%=lang.__("Add - Group info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+group_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					//apikeyは非表示。
					$('#tr_apikey', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					//カスタム入力チェック
					//班名の重複チェック
					if( !name_validate(postdata,"add") ){
						return [false,"<%=lang.__("Add: group name duplicated. Enter other name.")%>",""];
					}
					// 追加時はパスワード入力必須
					if(postdata.password == ""){
						return [false,"<%=lang.__("Login Password: This field is required.")%>",""];
					}
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+group_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Group info")%>',
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
								$('#'+group_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
		)	//グリッドナビゲーションend

		<c:if test="${admin}" >
		//コピーボタン
		$('#'+group_info_tabId).jqGrid(
			'navButtonAdd',
			'#'+group_info_pagerId,
			{
				id:'copy_group_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+group_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+group_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Group info")%>',
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
										//apikeyは非表示。
										$('#tr_apikey', formid).hide();
										if($('#isAllCopy').length == 0){
											$('<span><input type="checkbox" id="isAllCopy"><%=lang.__("Copy all")%></span>').insertBefore('#Act_Buttons #sData');
										}
									},
									beforeSubmit:function(postdata, formid){
										//班名の重複チェック
										if( !name_validate(postdata,"copy") ){
											return [false,"<%=lang.__("Copy: group name duplicated. Enter other name.")%>",""];
										}
										//カスタム入力チェック
										if(postdata.password == ""){
											return [false,"<%=lang.__("Login Password: This field is required.")%>",""];
										}
										//一括コピー用
										showProcessing('FrmGrid_'+ group_info_tabId);
										postdata.isAllCopy = $('#isAllCopy').prop('checked');
										postdata.allCopyId = $('#'+group_info_tabId).jqGrid('getRowData', rowid).id;
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+group_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+group_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+group_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ group_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_group_info #id').val('');

						//暗号化データはコピーしない。
						$('#FrmGrid_group_info #password').val('');

						//暗号化データはコピーしない。
						$('#FrmGrid_group_info #ecompass').val('');

					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
		);	//コピーボタンend
		</c:if>

	/**
	 * ダイアログに入力した班名が既に登録済みかチェックする関数
	 * @param {object} postdata   - ダイアログデータ
	 * @param {string} mode       - edit:編集中 add:追加 copy:コピー　のいずれか
	 * @param {string} selectedid - 編集中の場合、編集中の対象ID
	 * @return {boolean}          - true:重複あり  false:重複なし
	 */
	function name_validate(postdata, mode, selectedid){
		// 自治体IDに紐づく班情報(id, name)を取得する
		var tableAll = getIdName(postdata.localgovinfoid);

		// selectedid は string, idを格納したdata["selectTag"][i]["key"]はnumber
		// よって selectedid を string -> number 型変換
		selectedid = Number(selectedid);

		var data = JSON.parse(tableAll);

		// id, name 配列の初期化
		// 編集中の場合は selectedid を除いてid. name配列を作成
		var id = new Array();
		var name = new Array();
		if(mode === "edit"){
			for(var i = 0; i < data["selectTag"].length; i++){
				if(data["selectTag"][i]["key"] !== selectedid){
   					id.push(data["selectTag"][i]["key"]);
  					name.push(data["selectTag"][i]["value"]);
  				}
			}
		// 編集中以外（追加、コピー時）は全てのid, name を対象とする
		}else{
			for(var i = 0; i < data["selectTag"].length; i++){
   				id.push(data["selectTag"][i]["key"]);
  				name.push(data["selectTag"][i]["value"]);
			}
		}

		// 班名重複チェック
		//   is_double = true  重複あり
		//               false 重複なし
		var is_double = false;
		var i=0;
		while(i < id.length && !is_double){
			if(postdata.name === name[i]){
				is_double = true;
			}
			i++;
		}
		if(is_double){
      		return false;
      	}else{
			return true;
      	}

		return true;
	}

	/**
	 * 自治体IDに紐づく班情報(id, name)を取得する関数
	 * @param {string} localgovinfoid - 自治体ID
	 * @return {object} result - 班情報(id, name)
	 */
	function getIdName(localgovinfoid){

		var url = convertTabNameToMethodMame(tabName)+'/createIdName?localgovinfoid='+localgovinfoid+'';
		var result = $.ajax({
			dataType: "json",
			type: "GET",
			cache: false,
			url: url,
			async: false
		}).responseText;
    	return result;
	}


	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+group_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+group_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//サブグリッド設定
	$('#'+group_info_tabId).jqGrid('setGridParam',
			{subGridRowExpanded: function(subgrid_id, row_id) {
				user_info(subgrid_id, row_id, 'group_info');
			}}
	);	//サブグリッドend
}

////////////////////////////////////////////////////////////////////////
	//外部地図追加ボタン押下イベント
//	var calloutermapselectElmId = '#callrangemap';
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

	// APIキー編集ラジオボタンの制御。チェック中をクリックした場合はチェックを解除する
	var groupinfo_apikeymodify_radio_checked = ''
	$(document).on(
		'click',
		'input[name=groupinfo_apikeymodify_radio]',
		function(){
			var nowValue = $(this).val();
	        if(nowValue == groupinfo_apikeymodify_radio_checked) {
	            $(this).prop('checked', false);
	            groupinfo_apikeymodify_radio_checked = '';
	        } else {
	            groupinfo_apikeymodify_radio_checked = $(this).val();
	        }
		}
	);

function setMapArea(base){
	//alert("setMapArea");
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

    var wkt = "POLYGON((" +  x1 + " " + y1 + "," + x2 + " " + y1 + "," + x2 + " " + y2 + "," + x1 + " " + y2 + "," + x1 + " " + y1 + "))";
	$('input[id="extent"]').val(wkt);

    /*
    $('input[name="'+base+'_WESTBOUNDLONGITUDE"]').val(bounds.left.toFixed(6));
    $('input[name="'+base+'_EASTBOUNDLONGITUDE"]').val(bounds.right.toFixed(6));
    $('input[name="'+base+'_SOUTHBOUNDLATITUDE"]').val(bounds.bottom.toFixed(6));
    $('input[name="'+base+'_NORTHBOUNDLATITUDE"]').val(bounds.top.toFixed(6));
    */

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
	var wkt = $('input[id="extent"]').val();
	if (isWKTPolygon(wkt)) {
		var box = parseWKTPologon(wkt);

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

		var openDiv = $("[id^='editmodgroup_info']");
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
				left: position.left + 350,
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

