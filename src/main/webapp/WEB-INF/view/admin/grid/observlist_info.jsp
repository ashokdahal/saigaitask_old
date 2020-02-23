<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function observlist_info(subgrid_id, row_id){
	var tabName = 'observlist_info';
	var observlist_info_gridId = 'main';
	if(subgrid_id != null){
		observlist_info_gridId = subgrid_id;
	}
	var observlist_info_tabId = tabName;
	if(row_id != null){
		observlist_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'observmenu_info', 'observmenuinfoid');

	var observlist_info_pagerId = observlist_info_tabId+'Nav';
	$('#'+observlist_info_gridId).append('<table id="'+observlist_info_tabId+'" class="scroll"></table><div id="'+observlist_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	// 引数の監視観測IDより、紐づく観測所IDと観測所名のSelectItemを作成
	function createObservatoryinfoidSelectItem(observ_id){
		var observatoryinfoidSelectTag;
		var observatoryinfoidDataGetUrl;
		switch (observ_id){
			// 監視観測ＩＤ=1 の時は、雨量観測地点情報（observatoryrain_info）
 			case 1:
				observatoryinfoidDataGetUrl = '${f:url("observatoryrainInfo/createSelectTag/observatoryinfoid/observatoryrain_info/id/name")}';
				break;
			// 監視観測ＩＤ=2 の時は、河川水位観測地点情報（observatoryriver_info）
			case 2:
				observatoryinfoidDataGetUrl = '${f:url("observatoryriverInfo/createSelectTag/observatoryinfoid/observatoryriver_info/id/name")}';
				break;
			//監視観測ＩＤ=3 の時は、ダム観測所情報（observatorydam_info）
			case 3:
				observatoryinfoidDataGetUrl = '${f:url("observatorydamInfo/createSelectTag/observatoryinfoid/observatorydam_info/id/name")}';
				break;
			default:
				observatoryinfoidDataGetUrl = 'unknown';
				break;
		}

		if(observatoryinfoidDataGetUrl === 'unknown'){
			var alerttext = "<%=lang.__("There is no observation point info cooperate with monitoring/observation ID")%>";
			showMessage(observlist_info_tabId, alerttext, $.jgrid.nav.alertcap, 'undefined','undefined',450);
			return null;
		}else{
			$.ajax({
				type: 'POST',
				url: observatoryinfoidDataGetUrl,
 				dataType: 'json',
 				async: false,
 			}).done(function( selectItemData ) {
				observatoryinfoidSelectTag = selectItemData.selectTag;
			});
			return observatoryinfoidSelectTag;
		}
	}
	var observatoryinfoidSelectItems = new Object();
	// 引数の監視観測IDより、紐づく観測所IDと観測所名のSelectItemを返却
	function getObservatoryinfoidSelectItem(observ_id){
		switch (observ_id){
			// 監視観測ＩＤ=1 の時は、雨量観測地点情報（observatoryrain_info）
 			case 1:
				return observatoryinfoidSelectItems.rain;
				break;
			// 監視観測ＩＤ=2 の時は、河川水位観測地点情報（observatoryriver_info）
			case 2:
				return observatoryinfoidSelectItems.river;
				break;
			//監視観測ＩＤ=3 の時は、ダム観測所情報（observatorydam_info）
			case 3:
				return observatoryinfoidSelectItems.dam;
				break;
			default:
				return null;
				break;
		}
	}

	//グリッド
	$('#'+observlist_info_tabId).jqGrid({
		caption: '<%=lang.__("Monitoring/observation list info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Monitoring/observation menu ID")%>',
			'<%=lang.__("Monitoring/observation ID")%>',
			'<%=lang.__("Observation ID")%>',
			'<%=lang.__("Data item code")%>',
			'<%=lang.__("Display order")%>',
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
			{name:'observmenuinfoid',
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
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Monitoring/observation menu ID(*)")%>'},
				hidden:false},
			{name:'observid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/observid/observ_master/id/name',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectTag = $.parseJSON(data).selectTag;
							var rowid = $('#'+observlist_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+observlist_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.observid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'observlist_info', 'observ_master', false);
						},

					// 値を変更した場合、観測所ID欄を変更する
					dataEvents: [{
						type: 'change',
						fn: function(e) {
							var keyVal = $(e.target).val();
							var observatoryinfoidSelectTag = getObservatoryinfoidSelectItem(Number(keyVal));
							if(observatoryinfoidSelectTag){
								// 観測所IDセレクトボックスの要素を全て空にする
								$('#observatoryinfoid > option').remove();
								for(i = 0; i < observatoryinfoidSelectTag.length; i++){
									$('#observatoryinfoid').append($('<option>').html(observatoryinfoidSelectTag[i].value).val(observatoryinfoidSelectTag[i].key));
								}
							}
                        }
					}]
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Monitoring/observation ID(*)")%>'},
				hidden:false},

			{name:'observatoryinfoid',
				width:'150',
				align:'left',
				formatter: 'customSelect',
				edittype:'select',
				editable:true,
				editoptions:{
					value:'',
					defaultValue:'',
					delimiter:'|d|',
					readonly:false
						,dataUrl:'${f:url("observatoryrainInfo/createSelectTag/observatoryinfoid/observatorydam_info/id/name")}',
						//フォームのプルダウン作成
						buildSelect: function (data) {
							var selectedObserbid;
							var selectTag;
							var rowid = $('#'+observlist_info_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合は選択した行の監視観測IDを取得
								var rowdata = $('#'+observlist_info_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.observid;
								selectedObserbid = rowdata.observid;
								selectTag = getObservatoryinfoidSelectItem(Number(selectedObserbid));

							}else{
								//新規追加の場合はダイアログ上監視観測IDリストボックスの値を取得
								selectedObserbid = $("#observid option:selected").val();
								selectTag = getObservatoryinfoidSelectItem(Number(selectedObserbid));
							}
							return createSelectTag(rowid, selectTag, selectedId, 'observlist_info', 'observatory_master', false);
						}
				},
				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Observation ID(*)")%>'},
				hidden:false},


			{name:'itemcode',
				width:'150',
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
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Data item code(*)")%>'},
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
		pager: observlist_info_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){

			// 種別毎に観測所IDプルダウンを作成
			observatoryinfoidSelectItems.rain  = createObservatoryinfoidSelectItem(1);
			observatoryinfoidSelectItems.river = createObservatoryinfoidSelectItem(2);
			observatoryinfoidSelectItems.dam   = createObservatoryinfoidSelectItem(3);

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].observmenuInfo;
				if(ref != null){
					var refkey = ref.id;
					str += refkey + ':' + refkey;
				}

			}
			$('#'+observlist_info_tabId).jqGrid('setColProp', 'observmenuinfoid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].observMaster;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+observlist_info_tabId).jqGrid('setColProp', 'observid', {editoptions:{value:str}});

			//selectタグ項目のグリッド表示を「コード値」のラベル表示とする。（観測所ID）
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].observatoryinfoid;
				// 監視観測IDによって、マッチング対象の観測所IDSelectItemを変更する
				var observatoryinfoidSelectItem = getObservatoryinfoidSelectItem(Number(data.rows[i].observid));
				if(ref != null){
					var refkey = ref;
					var refval = '';
					str += refkey + ':' + refkey;
				}
			}
			$('#'+observlist_info_tabId).jqGrid('setColProp', 'observatoryinfoid', {editoptions:{value:str}});

// コード値+'：'+デコード値の表示がうまくいかない
// コード値が重複している場合、先に定義したコード値とデコード値の組み合わせが適用される模様
//			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。（観測所ID）
//			var str = '';
//			for(i=0; i < data.rows.length; i++){
//				if(str != ''){
//					str += '|d|'
//				}
//				var ref = data.rows[i].observatoryinfoid;
//				// 監視観測IDによって、マッチング対象の観測所IDSelectItemを変更する
//				var observatoryinfoidSelectItem = getObservatoryinfoidSelectItem(Number(data.rows[i].observid));
//				if(ref != null){
//					var refkey = ref;
//					var refval = '';
//					for(j=0; j < observatoryinfoidSelectItem.length;j++){
//						if(observatoryinfoidSelectItem[j].key === refkey ){
//							refval = observatoryinfoidSelectItem[j].value;
//							break;
//						}
//					}
//					str += refkey + ':' + refval;
//				}
//			}
//			$('#'+observlist_info_tabId).jqGrid('setColProp', 'observatoryinfoid', {editoptions:{value:str}});
		},
		loadComplete : function (data) {
			//ページボタンセンター配置対策
			$("#observlist_infoNav_left").css("width","");
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+observlist_info_tabId).getDataIDs();
			$.each(rowIDs, function (i, item) {
				if (i % 2 == 0) {
					$('#'+item).removeClass('ui-widget-content');
					$('#'+item).addClass('myodd');
				}
			});
			//表示順のデフォルト値（現在の最大値+1）をセットする。
			var maxDisporder = $('#'+observlist_info_tabId).jqGrid('getGridParam','userData').maxDisporder;
			$('#'+observlist_info_tabId).jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
			if(getEditMode() == 'add'){
				//登録モードの場合、表示順のデフォルト値（現在の最大値+1）をフォームの表示順欄にセットする。
				$('#tr_disporder #disporder').val(maxDisporder+1);
			}

			// 観測所ID列のツールチップに、「観測所ID：観測所名」を表示させる 開始
			for(idIndex = 0; idIndex < rowIDs.length; idIndex++){
				var chipStr = '';
				var currentObservid = $('#'+observlist_info_tabId).jqGrid('getCell',rowIDs[idIndex],'observid');
				var currentObservatoryinfoid = $('#'+observlist_info_tabId).jqGrid('getCell',rowIDs[idIndex],'observatoryinfoid');
				var observatoryinfoidSelectItem = getObservatoryinfoidSelectItem(Number(currentObservid));

				// 監視観測IDによって、マッチング対象の観測所IDSelectItemを変更する
				if(currentObservatoryinfoid != null){
					for(j=0; j < observatoryinfoidSelectItem.length;j++){
						if(observatoryinfoidSelectItem[j].key === Number(currentObservatoryinfoid) ){
							chipStr  = observatoryinfoidSelectItem[j].value;
							break;
						}
					}
				}
				$('#'+observlist_info_tabId).jqGrid('setCell',rowIDs[idIndex],'observatoryinfoid','','',{'title':chipStr});
			}
			// 観測所ID列のツールチップに、「観測所ID：観測所名」を表示させる 終了
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
	$('#'+observlist_info_tabId).jqGrid('navGrid',
			'#'+observlist_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Monitoring/observation list info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+observlist_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+observlist_info_tabId).jqGrid('getRowData', rowid);
					s += '&observmenuinfoid='+rowdata.observmenuinfoid;
					var rowdata = $('#'+observlist_info_tabId).jqGrid('getRowData', rowid);
					s += '&observid='+rowdata.observid;
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_observmenuinfoid .CaptionTD', formid).after('<td id="dispobservmenuinfoid">'+row_id+'</td>');
					$('#tr_observmenuinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+observlist_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
	  				var pagenum = $('#'+observlist_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+observlist_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+observlist_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_observmenuinfoid #dispobservmenuinfoid', formid).remove();
					$('#tr_observmenuinfoid .CaptionTD', formid).after('<td id="dispobservmenuinfoid">'+row_id+'</td>');
					$('#tr_observmenuinfoid .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - Monitoring/observation list info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+observlist_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_observmenuinfoid .CaptionTD', formid).after('<td id="dispobservmenuinfoid">'+row_id+'</td>');
					$('#tr_observmenuinfoid .DataTD', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+observlist_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Monitoring/observation list info")%>',
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
								$('#'+observlist_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+observlist_info_pagerId,
			{
				id:'copy_observlist_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+observlist_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+observlist_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Monitoring/observation list info")%>',
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
										$('#tr_observmenuinfoid .CaptionTD', formid).after('<td id="dispobservmenuinfoid">'+row_id+'</td>');
										$('#tr_observmenuinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+observlist_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+observlist_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+observlist_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ observlist_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_observlist_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+observlist_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+observlist_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');
}
