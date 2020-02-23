<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
function outermap_info(subgrid_id, row_id){
	var tabName = 'outermap_info';
	var thisDialogId = subgrid_id;
	var thisDialog = $('#'+thisDialogId);
	var thisGridId = tabName+row_id;
	var thisNavId = thisGridId+'Nav';
	var parentgrid_refkey = 'menuinfoid';

	var outermap_info_tabId = tabName;
	if(row_id != null){
		outermap_info_tabId += row_id;
	}else{
		row_id = '';
	}

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
						caption:'<%=lang.__("External map display layer settings")%>',
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
				'<%=lang.__("Metadata ID")%>',
				'WMS url',
				'<%=lang.__("Authorization info ID")%>',
				'<%=lang.__("List")%>',
				'<%=lang.__("Initially displayed flag")%>',
				'<%=lang.__("Legend folding")%>',
// filterid は未実装なので非表示にする
//				'<%=lang.__("Filter ID")%>',
				'<%=lang.__("Transmittance")%>',
				'<%=lang.__("Display order")%>',
				'<%=lang.__("External list data info ID")%>',
				'<%=lang.__("External list data info filter ID")%>',
				'WMSURL',
				'WMSLEGENDURL',
				'WMSFEATUREURL',
				'<%=lang.__("Author info")%>',
				'<%=lang.__("WMS format")%>',
				'<%=lang.__("Explanation of map")%>',
				'<%=lang.__("Legend image")%>',
				'<%=lang.__("Select WMS layer ID")%>',
				'<%=lang.__("Select WMS layer name")%>'
			],
			colModel: [
				{name:'id',
					width:'30',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;ID'},
					hidden:false},
				{name:'menuinfoid',
					width:'10',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Menu ID")%>'},
					hidden:true},
				{name:'name',
					width:'120',
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
					formoptions:{elmprefix:'', elmsuffix:'&nbsp;<a id="calloutermapselect_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Add external map")%><span class="ui-icon ui-icon-search"></span></a>', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Layer name(*)")%>'},
					hidden:false},
				{name:'metadataid',
					width:'120',
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
					formoptions:{elmprefix:'', elmsuffix:'&nbsp;<a id="calloutermapselect_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Add external map")%><span class="ui-icon ui-icon-search"></span></a>', label:'<input type="radio" name="outerMapType" id="outerMapType_MAP">&nbsp;<%=lang.__("Metadata ID(*)")%>'},
					hidden:false},

				{name:'wmscapsurl',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'&nbsp;<a id="calloutermapselect_'+tabName+row_id+'" class="fm-button ui-state-default ui-corner-all fm-button-icon-left" href="javascript:void(0)"><%=lang.__("Add external map")%><span class="ui-icon ui-icon-search"></span></a>', label:'<input type="radio" name="outerMapType" id="outerMapType_WMS">&nbsp;WMS url(*)'},
					hidden:false},
				{name:'authorizationinfoid',
					width:'50',
					align:'center',
					formatter: 'customSelect',
					edittype:'select',
					editable:true,
					editoptions:{value:'',
						defaultValue:'',
						delimiter:'|d|',
						readonly:false
							,dataUrl:convertTabNameToMethodMame(tabName)+'/createSelectTag/authorizationinfoid/authorization_info/id/name',
							//フォームのプルダウン作成
							buildSelect: function (data) {
								var selectTag = $.parseJSON(data).selectTag;
								// 先頭に空行を追加しておく
								var blankRow = new Object();
								blankRow.key = '';
								blankRow.value = '';
								selectTag.unshift(blankRow);

								var rowid = $('#'+outermap_info_tabId).jqGrid('getGridParam', 'selrow');
								var selectedId = null;
								if(rowid != null){
									//編集の場合
									var rowdata = $('#'+outermap_info_tabId).jqGrid('getRowData', rowid);
									selectedId = rowdata.authorizationinfoid;
								}else{
								}
								return createSelectTag(rowid, selectTag, selectedId, 'outermap_info', 'authorization_info');
							}
						},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'int',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Authorization info ID")%>'},
					hidden:false},
				{name:'list',
					width:'30',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("List")%>', disabled : false},
					hidden:false,
					classes:"radio_class"},
				{name:'visible',
					width:'60',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Initially displayed flag(*)")%>'},
					hidden:false},
				{name:'closed',
					width:'60',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Legend folding(*)")%>'},
					hidden:false},
//				{name:'filterid',
//					width:'50',
//					align:'left',
//					formatter: 'nullstrToEmptyStrFmatter',
//					edittype:'text',
//					editable:true,
//					editoptions:{value:'',
//						defaultValue:'',
//						readonly:false
//						,style:'ime-mode:inactive'
//					},
//					editrules:{required:false, number:false, integer:true, email:false, edithidden:false},
//					sortable:true,
//					sorttype:'int',
//					search:false,
//					searchrules:{required:true},
//					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Filter ID")%>'},
//					hidden:false},
				{name:'layeropacity',
					width:'150',
					align:'left',
					formatter: 'select',
					edittype:'select',
					editable:true,
					editoptions:{value:': ;1.0:'+lang.__("0%(nontransparent)")+';0.9:10%;0.8:20%;0.7:30%;0.6:40%;0.5:50%;0.4:60%;0.3:70%;0.2:80%;0.1:90%',
						defaultValue:' ',
						readonly:false
					},
					editrules:{required:false, number:false, integer:false, email:false, edithidden:false},
					sortable:true,
					sorttype:'text',
					search:true,
					searchrules:{required:true},
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Transmittance")%>'},
					hidden:false},
				{name:'disporder',
					width:'30',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'&nbsp;&nbsp;&nbsp;&nbsp;<%=lang.__("Display order(*)")%>'},
					hidden:false},
				{name:'externaltabledatainfoid',
					width:'10',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("External list data info ID")%>'},
					hidden:true},
				{name:'externaltabledatainfofilterid',
					width:'10',
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
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("External list data info filter ID")%>'},
					hidden:true},

				{name:'wmsurl',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'WMSURL'},
					hidden:true},

				{name:'wmslegendurl',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'WMSLEGENDURL'},
					hidden:true},

				{name:'wmsfeatureurl',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'WMSFEATUREURL'},
					hidden:true},

				{name:'attribution',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Author info")%>'},
					hidden:true},

				{name:'wmsformat',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("WMS format")%>'},
					hidden:true},

				{name:'layerdescription',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Explanation of map")%>'},
					hidden:true},

				{name:'legendheight',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Legend image")%>'},
					hidden:true},

				{name:'selectedwmslayerids',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Select WMS layer ID")%>'},
					hidden:true},

				{name:'selectedwmslayernames',
					width:'10',
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
					searchrules:{required:false},
					formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Select WMS layer name")%>'},
					hidden:true}
			],
			ajaxGridOptions :{
				beforeSend: function(jqXHR) {
					var csrf_token = '${_csrf.token}';
					jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
				}
			},
			caption: '<%=lang.__("External map")%>',
			loadtext:'',
			loadui:'block',
			loadonce:false,
			rowNum:30,
			rowList:[5,10,30,50,100],
			width:980,
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
			pager: '#'+thisNavId,
			autowidth: false,
			autoencode: false,
			beforeProcessing:function(data){
//				console.log(data);
				//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
				var str = '';
				for(i=0; i < data.rows.length; i++){
					if(str != ''){
						str += '|d|'
					}
					var ref = data.rows[i].authorizationInfo;
					if(ref != null){
						var refkey = ref.id;
						var refval = ref.name;
						str += refkey + ':' + refkey + '：' + refval;
					}
				}
				$('#'+outermap_info_tabId).jqGrid('setColProp', 'authorizationinfoid', {editoptions:{value:str}});

			},
			loadComplete : function (data) {
				var trOddSelector = '#outermap_grid_'+tabName+row_id+' tr:odd';
				$(trOddSelector).removeClass('ui-widget-content');
				$(trOddSelector).addClass('myodd');
				//表示順のデフォルト値（現在の最大値+1）をセットする。
				var maxDisporder = thisGrid.jqGrid('getGridParam','userData').maxDisporder;
				thisGrid.jqGrid('setColProp', 'disporder', {editoptions:{defaultValue:maxDisporder+1}});
				if(getEditMode() == 'add'){
					//登録モードの場合、表示順のデフォルト値（現在の最大値+1）をフォームの表示順欄にセットする。
					$('#tr_disporder #disporder').val(maxDisporder+1);
				}

				//表示順がおかしい
				else if(getEditMode() == 'edit'){
					//編集前データをシリアライズ化してpostDataに一時保存
					var formid = $('#FrmGrid_'+ thisGridId);
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+outermapInfoMakeSerializeData($('#FrmGrid_'+ thisGridId), getEditMode(), thisGrid)}});
					//フォーム表示制御
					outermapInfoFormControl(formid, getEditMode(), thisGrid);

					//alert(document.getElementById('outerMapType_WMS'));
					//document.getElementById('outerMapType_WMS').checked = true;
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
				editCaption:'<%=lang.__("Edit - External map")%>',
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
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+outermapInfoMakeSerializeData(formid, getEditMode(), thisGrid)}});
					//フォーム表示制御
					outermapInfoFormControl(formid, getEditMode(), thisGrid);
				},
				afterShowForm:function(formid){
					//if (document.getElementById("metadataid").value == "WMSCapabilities")	//メタデータIDかWMSCapabilities かどうかでラジオボタンを制御
					if (document.getElementById("wmscapsurl").value != "")	//WMSCapabilities レイヤ
						document.getElementById("outerMapType_WMS").checked = true;
					else
						document.getElementById("outerMapType_MAP").checked = true;
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':thisGrid.jqGrid('getGridParam','postData').serializedPreEditData};
				},
				beforeSubmit:function(postdata, formid){
					//カスタム入力チェック
					//メニューIDとメタデータIDでユニークになるようチェックする。
					if(isExistsPropertyInGridData(thisGridId, 'metadataid', postdata.metadataid)){
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
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+outermapInfoMakeSerializeData(formid, getEditMode(), thisGrid)}});
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
					console.log("afterclickPgButtons");
					//if (document.getElementById("metadataid").value == "WMSCapabilities")	//メタデータIDかWMSCapabilities かどうかでラジオボタンを制御
					if (document.getElementById("wmscapsurl").value != "")	//WMSCapabilities レイヤ
						document.getElementById("outerMapType_WMS").checked = true;
					else
						document.getElementById("outerMapType_MAP").checked = true;

					//編集前データをシリアライズ化してpostDataに一時保存。
					thisGrid.jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+outermapInfoMakeSerializeData(formid, getEditMode(), thisGrid)}});
					//フォーム表示制御
					outermapInfoFormControl(formid, getEditMode(), thisGrid);
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
				addCaption:'<%=lang.__("Add - External map")%>',
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
					outermapInfoFormControl(formid, getEditMode(), thisGrid);
				},
				beforeSubmit:function(postdata, formid){
					//カスタム入力チェック
					//メニューIDとメタデータIDでユニークになるようチェックする。
					if(isExistsPropertyInGridData(thisGridId, 'metadataid', postdata.metadataid)){
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
					document.getElementById("outerMapType_MAP").checked = false;
					document.getElementById("outerMapType_WMS").checked = false;
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
			{caption:'<%=lang.__("Delete - External map")%>',
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
								addCaption:'<%=lang.__("Record copy - External map")%>',
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
									outermapInfoFormControl(formid, getEditMode(), thisGrid);
								},
								beforeSubmit:function(postdata, formid){
									//カスタム入力チェック
									//メニューIDとメタデータIDでユニークになるようチェックする。
									if(isExistsPropertyInGridData(thisGridId, 'metadataid', postdata.metadataid)){
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
						outermapInfoFormControl($('#FrmGrid_'+ thisGridId), getEditMode(), thisGrid);
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
		);	//グリッドナビゲーションend

		//タイトルバーの×ボタンのコールバック関数
		var headerCancelSelector = '#menuInfoOutermapViewlayerConfDialogHd_'+tabName+row_id+' a.ui-jqdialog-titlebar-close';
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

	////////////////////////////////////////////////////////////////////////
	//外部地図追加ボタン押下イベント
//	var calloutermapselectElmId = '#calloutermapselect'+tabName+row_id;
	$(document).on(
		'click',
		'#calloutermapselect_'+tabName+row_id,
		function(){
			// 関数呼び出し
			outermap_select('name', 'metadataid', 'wmscapsurl', 'wmsurl', 'wmslegendurl', 'wmsfeatureurl', 'outerMapType', 'attribution', 'layeropacity', 'wmsformat', 'layerdescription', 'legendheight', 'selectedwmslayerids', 'selectedwmslayernames');
		}
	);
}

function outermapInfoFormControl(formid, mode, grid){
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
	//externaltabledatainfoidを非表示にする。
	$('#tr_externaltabledatainfoid .CaptionTD', formid).hide();
	$('#tr_externaltabledatainfoid .DataTD', formid).hide();
	//externaltabledatainfofilteridを非表示にする。
	$('#tr_externaltabledatainfofilterid .CaptionTD', formid).hide();
	$('#tr_externaltabledatainfofilterid .DataTD', formid).hide();
}

function outermapInfoMakeSerializeData(formid, mode, grid){
	//選択されていないチェックボックスやラジオボタン、disabledに設定された要素などはserialize対象外のため自力でセットする。
	var rowid = grid.jqGrid('getGridParam', 'selrow');
	var s = '';

	var rowdata = grid.jqGrid('getRowData', rowid);
	s += '&authorizationinfoid='+rowdata.authorizationinfoid;

	//フォームのプルダウン構築前はグリッドから、グリッド構築前はフォームから取得する。
//console.log(s);
	return s;
}