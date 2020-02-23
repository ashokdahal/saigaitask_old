<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function landmark_data(subgrid_id, row_id){
	var tabName = 'landmark_data';
	var landmark_data_gridId = 'main';
	if(subgrid_id != null){
		landmark_data_gridId = subgrid_id;
	}
	var landmark_data_tabId = tabName;
	if(row_id != null){
		landmark_data_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'landmark_info', 'landmarkinfoid');

	var landmark_data_pagerId = landmark_data_tabId+'Nav';
	$('#'+landmark_data_gridId).append('<table id="'+landmark_data_tabId+'" class="scroll"></table><div id="'+landmark_data_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	var csvfile = null;

	//グリッド
	$('#'+landmark_data_tabId).jqGrid({
		caption: '<%=lang.__("Landmark data info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		colNames: [
			'ID',
			'<%=lang.__("Landmark info ID")%>',
			'<%=lang.__("Group ID<!--2-->")%>',
			'<%=lang.__("Landmark")%>',
			'<%=lang.__("Latitude")%>',
			'<%=lang.__("Longitude")%>',
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
			{name:'landmarkinfoid',
				width:'150',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:row_id,
					delimiter:'|d|',
					readonly:true
				},
//				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Landmark info ID(*)")%>'},
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
							var rowid = $('#'+landmark_data_tabId).jqGrid('getGridParam', 'selrow');
							var selectedId = null;
							if(rowid != null){
								//編集の場合
								var rowdata = $('#'+landmark_data_tabId).jqGrid('getRowData', rowid);
								selectedId = rowdata.groupid;
							}else{
							}
							return createSelectTag(rowid, selectTag, selectedId, 'landmark_data', 'group_info', false);
						}
				},
//				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'int',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Group ID(*)<!--2-->")%>'},
				hidden:false},
			{name:'landmark',
				width:'150',
				align:'left',
				formatter: 'nullstrToEmptyStrFmatter',
				edittype:'text',
				editable:true,
				editoptions:{value:'',
					defaultValue:'',
					readonly:false
				},
//				editrules:{required:true, number:false, integer:false, email:false, edithidden:false},
				editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Landmark(*)")%>'},
				hidden:false},
			{name:'latitude',
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
//				editrules:{required:true, number:true, integer:false, email:false, edithidden:false},
				editrules:{required:false, number:true, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Latitude(*)")%>'},
				hidden:false},
			{name:'longitude',
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
//				editrules:{required:true, number:true, integer:false, email:false, edithidden:false},
				editrules:{required:false, number:true, integer:false, email:false, edithidden:false},
				sortable:true,
				sorttype:'text',
				search:true,
				searchrules:{required:true},
				formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Longitude(*)")%>'},
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
		pager: landmark_data_pagerId,
		autowidth: true,
		autoencode: false,
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].landmarkInfo;
				if(ref != null){
					var refkey = ref.id;
					str += refkey + ':' + refkey;
				}

			}
			$('#'+landmark_data_tabId).jqGrid('setColProp', 'landmarkinfoid', {editoptions:{value:str}});

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
			$('#'+landmark_data_tabId).jqGrid('setColProp', 'groupid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//ページボタンセンター配置対策
			$("#landmark_dataNav_left").css("width","");
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+landmark_data_tabId).getDataIDs();
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
	$('#'+landmark_data_tabId).jqGrid('navGrid',
			'#'+landmark_data_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:false,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Landmark data info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+landmark_data_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+landmark_data_tabId).jqGrid('getRowData', rowid);
					s += '&landmarkinfoid='+rowdata.landmarkinfoid;
					var rowdata = $('#'+landmark_data_tabId).jqGrid('getRowData', rowid);
					s += '&groupid='+rowdata.groupid;
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_landmarkinfoid .CaptionTD', formid).after('<td id="displandmarkinfoid">'+row_id+'</td>');
					$('#tr_landmarkinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+landmark_data_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				beforeSubmit:function(postdata, formid){
					return custom_validate(postdata,"add");
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
	  				var pagenum = $('#'+landmark_data_tabId).jqGrid('getGridParam', 'page');
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+landmark_data_tabId).jqGrid('getGridParam', 'page');
					$('#'+landmark_data_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_landmarkinfoid #displandmarkinfoid', formid).remove();
					$('#tr_landmarkinfoid .CaptionTD', formid).after('<td id="displandmarkinfoid">'+row_id+'</td>');
					$('#tr_landmarkinfoid .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - Landmark data info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+landmark_data_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_landmarkinfoid .CaptionTD', formid).after('<td id="displandmarkinfoid">'+row_id+'</td>');
					$('#tr_landmarkinfoid .DataTD', formid).hide();

					// CSVインポートエリア作成
					$(formid).append("<hr class='ui-widget-content' style='margin:1px'>");
					$(formid).append("<span><%=lang.__("Bulk registration")%></span>");
					$(formid).append("<br>");
					$(formid).append("<input type='file' accept='.csv,.CSV' id='landmark_data_csvfile' name='csvfile'>");
					$(formid).append("<input type='radio' id='landmark_data_radio_swap' name='csvloadradio' value='1' checked='checked'>");
					$(formid).append("<span><%=lang.__("Exchange")%></span>");
					$(formid).append("<input type='radio' id='landmark_data_radio_append' name='csvloadradio' value='2'>");
					$(formid).append("<span><%=lang.__("Add<!--2-->")%></span>");
				},
				beforeSubmit:function(postdata, formid){
					// postdataにCSVインポートエリアの情報を追加
					var nowCsvfile = $('#landmark_data_csvfile')[0].files[0];
					if(nowCsvfile){
						postdata.csvfilename = $('#landmark_data_csvfile')[0].files[0].name;
						postdata.csvloadradio = $("input[name='csvloadradio']:checked").val();
						// CSVファイルのオブジェクトを保存しておく
						csvfile = $('#landmark_data_csvfile')[0].files[0];
					}

					return custom_validate(postdata,"add");
//					return [true,"",""];
				},
				afterSubmit:function(response, postdata){
					// CSVファイルが指定されていた場合はここでアップロードを実行
					if(csvfile != null){
						postdata.csvfile = csvfile;
						uploadFile(response, postdata);
					}

					//サブミット後、グリッド再取得
					$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Landmark data info")%>',
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
								$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+landmark_data_pagerId,
			{
				id:'copy_landmark_data',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+landmark_data_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+landmark_data_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Landmark data info")%>',
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
										$('#tr_landmarkinfoid .CaptionTD', formid).after('<td id="displandmarkinfoid">'+row_id+'</td>');
										$('#tr_landmarkinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return custom_validate(postdata,"add");
										// return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+landmark_data_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ landmark_data_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_landmark_data #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
		//カスタム削除ボタン
		).jqGrid(
			'navButtonAdd',
			'#'+landmark_data_pagerId,
			{
				//caption:"Delete", buttonimg:"../row-delete.png", onClickButton: function(){
				caption:"All", buttonicon:"ui-icon-trash", onClickButton: function(){

				var msgtext="::";
				var rowid = $('#'+landmark_data_tabId).jqGrid('getGridParam', 'selrow');

				// 1行選択時は現行の削除処理
				if(rowid){
            		$('#'+landmark_data_tabId).jqGrid(
						'delGridRow',
						rowid,
						{
							afterComplete:function(response, postdata, formid){
								//サブミット後、削除処理でエラーがあった場合、アラートを表示する。
								if(response.responseText){
									var res = $.parseJSON(response.responseText);
									if(res){
										if(res.message){
											showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
											$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										}
									}
								}
								return [true,''];
							},
							ajaxDelOptions  :{
								beforeSend: function(jqXHR) {
//									var csrf_token = '${cookie.JSESSIONID.value}';
									var csrf_token = '${_csrf.token}';
									jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
								},
								error:function (response, status, error){
										//Ajaxエラー時、error.jspを出力。
										var errorpage = response.responseText;
										window.top.document.body.innerHTML = errorpage;
									}
							}
					});
				// 複数行選択時・非選択時は一括削除処理
				}else{
					// グリッド内の全データを配列に取り込む
        			var rowIds = $('#'+landmark_data_tabId).jqGrid('getDataIDs');
        			if(rowIds.length > 0){
						var isExists = confirm("<%=lang.__("Are you sure to delete all landmark data?")%>");
        			}else{
        				return false;
        			}

					if(isExists){
	        			// 親ID(目標物情報ID)を取得
	                    var rowdataobj = $('#'+landmark_data_tabId).getRowData(rowIds[0]);
	                    var pid = rowdataobj.landmarkinfoid;
	        			var url = convertTabNameToMethodMame(tabName)+'/jqgriddeleteall/';
						$.ajax({
							dataType: "json",
							type: "POST",
							cache: false,
							url: url,
//							headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
							headers: {"X-CSRF-Token":'${_csrf.token}'},
							data: {oper:'delall', parentid:pid},
							success: function(msg) {
								$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
								return [true,''];
							},
							error: function(XMLHttpRequest, textStatus, errorThrown) {
								//TODO:エラー処理
								alert("<%=lang.__("Error: Failed to delete.")%>");
							}
						});
					}
				}
			}
	});	//グリッドナビゲーションend

	function custom_validate(postdata, mode){
		var reqMessage="<%=lang.__(": This item is required")%>";
		if(mode === "add"){
			if(postdata.csvfilename == null || postdata.csvfilename == ""){
				if(postdata.landmarkinfoid == null || postdata.landmarkinfoid == ""){
					return [false,"<%=lang.__("Landmark info ID(*)")%>" + reqMessage];
				}
				if(postdata.groupid == null || postdata.groupid == ""){
					return [false,"<%=lang.__("Group ID(*)<!--2-->")%>" + reqMessage];
				}
				if(postdata.landmark == null || postdata.landmark == ""){
					return [false,"<%=lang.__("Landmark(*)")%>" + reqMessage];
				}
				if(postdata.latitude == null || postdata.latitude == ""){
					return [false,"<%=lang.__("Latitude(*)")%>" + reqMessage];
				}
				if(postdata.longitude == null || postdata.longitude == ""){
					return [false,"<%=lang.__("Longitude(*)")%>" + reqMessage];
				}
			}
		}else{
			if(postdata.landmarkinfoid == null || postdata.landmarkinfoid == ""){
				return [false,"<%=lang.__("Landmark info ID(*)")%>" + reqMessage];
			}
			if(postdata.groupid == null || postdata.groupid == ""){
				return [false,"<%=lang.__("Group ID(*)<!--2-->")%>" + reqMessage];
			}
			if(postdata.landmark == null || postdata.landmark == ""){
				return [false,"<%=lang.__("Landmark(*)")%>" + reqMessage];
			}
			if(postdata.latitude == null || postdata.latitude == ""){
				return [false,"<%=lang.__("Latitude(*)")%>" + reqMessage];
			}
			if(postdata.longitude == null || postdata.longitude == ""){
				return [false,"<%=lang.__("Longitude(*)")%>" + reqMessage];
			}
		}

//		return [false, "hoge"];
		return [true,"",""];
	}

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+landmark_data_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+landmark_data_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');

	//TODO: エラーハンドリング、アップロードしたファイル名をグリッドに表示するか？
	function uploadFile(response, postdata) {
		ajaxFileUpload(postdata);
		return [true];
	}

	function ajaxFileUpload(postdata){
		$("#loading").ajaxStart(function () {
			$(this).show();
		}).ajaxComplete(function () {
			$(this).hide();
		});


		var fd = {
			"landmarkinfoid": postdata['landmarkinfoid'],
			"groupid": postdata['groupid'],
			"csvloadradio": postdata['csvloadradio'],
			"_csrf": '${_csrf.token}'
		};
		$.ajaxFileUpload({
			url: convertTabNameToMethodMame(tabName)+'/csvimport/',
			secureuri: false,
			fileElementId: 'landmark_data_csvfile',
			dataType: "json",
			type: "POST",
			data: fd,
			async: false,
			cache: false,
			success: function(data) {
				if (data.message) {
//					showMessage(tabName, data.message, $.jgrid.nav.alertcap);
					alert(data.message);
					console.log(tabName);
					console.log($.jgrid.nav.alertcap);
				}
				else {
					alert("<%=lang.__("Succeeded to upload CSV file.")%>");
				}
				$('#'+landmark_data_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
				return [true,''];
			},
			error: function(data, status, e) {
				console.log(data);
				console.log(status);
				console.log(e);
				alert("error");
			}
		});
	}
}

