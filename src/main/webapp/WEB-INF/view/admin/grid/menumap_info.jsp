<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>

function menumap_info(subgrid_id, row_id){
	var tabName = 'menumap_info';
	var menumap_info_gridId = 'main';
	if(subgrid_id != null){
		menumap_info_gridId = subgrid_id;
	}
	var menumap_info_tabId = tabName;
	if(row_id != null){
		menumap_info_tabId += row_id;
	}else{
		row_id = '';
	}
	var parentgrid_refkey = getParentgridRefkey(subgrid_id, 'menu_info', 'menuinfoid');

	var menumap_info_pagerId = menumap_info_tabId+'Nav';
	$('#'+menumap_info_gridId).append('<table id="'+menumap_info_tabId+'" class="scroll"></table><div id="'+menumap_info_pagerId+'" class="scroll"></div>');

	var thisGridLevel = getBreadcrumb(subgrid_id!=''?getParentTableName(subgrid_id):'', tabName);

	//グリッド
	$('#'+menumap_info_tabId).jqGrid({
		caption: '<%=lang.__("Menu map info")%>',
		mtype: 'POST',
		url:convertTabNameToMethodMame(tabName)+'/jqgridindex/'+parentgrid_refkey+'/'+row_id+'',
		datatype: 'json',
		//forceFit: false,
		//shrinkToFit: false,
		colNames: [
			'ID',
			'<%=lang.__("Menu ID 111")%>',
			'<%=lang.__("Map range (WKT)")%>',
			'<%=lang.__("Resolution (default 0)")%>',
			'<%=lang.__("Notes")%>'
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
			{name:'menuinfoid',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Menu ID(*)")%>'},
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
			label:'<%=lang.__("Map range (WKT)")%>'},
			hidden:false},
			{name:'resolution',
			width:'250',
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
			formoptions:{elmprefix:'', elmsuffix:'', label:'<%=lang.__("Resolution (default 0)(*)")%><%=lang.__("(STANDARD)")%>'},
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
		subGrid: false,
		viewrecords: true,
		postData:{'loadonce':false},
		pager: menumap_info_pagerId,
		autowidth: false,
		width: '600px',
		autoencode: false,
		onClose: function() {
			alert("onClose");
		},
		beforeProcessing:function(data){
			//selectタグ項目のグリッド表示を「コード値：デコード値」のラベル表示とする。
			var str = '';
			for(i=0; i < data.rows.length; i++){
				if(str != ''){
					str += '|d|'
				}
				var ref = data.rows[i].menuInfo;
				if(ref != null){
					var refkey = ref.id;
					var refval = ref.name;
					str += refkey + ':' + refkey + '：' + refval;
				}

			}
			$('#'+menumap_info_tabId).jqGrid('setColProp', 'menuinfoid', {editoptions:{value:str}});

		},
		loadComplete : function (data) {
			//console.log(data); //Ajaxで取得したデータがdataに入ってくる。
			//1行おきに背景色をかえる
			var rowIDs = $('#'+menumap_info_tabId).getDataIDs();
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
	$('#'+menumap_info_tabId).jqGrid('navGrid',
			'#'+menumap_info_pagerId,
			//ナビゲーションオプション設定
			{edit:true,add:true,del:true,refresh:true,
				beforeRefresh: function(){	//リフレッシュボタン押下時
					//リフレッシュ処理直前にdatatypeを再設定することで再度Ajaxでデータ取得する。
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{datatype:'json'});
					//リフレッシュで検索関連のパラメータが消されないため、絞りこまれた状態で再表示となってしまう。
					//そこで検索関連のパラメータを削除する。_searchは消せないようだ。
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{postData:{searchField:'',searchString:'',searchOper:''}}).trigger('reloadGrid');
					return [true,''];
				}
			},
			//編集フォームオプション設定
			{recreateForm: true,
				editCaption:'<%=lang.__("Edit - Menu map info")%>',
				width:'auto',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
				//編集前データをシリアライズ化してpostDataに一時保存
					var rowid = $('#'+menumap_info_tabId).jqGrid('getGridParam', 'selrow');
					var s = "";
					var rowdata = $('#'+menumap_info_tabId).jqGrid('getRowData', rowid);
					s += '&menuinfoid='+rowdata.menuinfoid;
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()+s}});
					//idをラベル表示にする。
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
					$('#tr_menuinfoid .DataTD', formid).hide();
					//フォームモードに編集モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">edit</span>');
				},
				afterShowForm:function(formid){
				},
				onclickSubmit:function(opt, data){
					//シリアライズ化しpostDataに一時保存した編集前データは何故か送信されないためこのイベントトリガーで送信する。
					return {'serializedPreEditData':$('#'+menumap_info_tabId).jqGrid('getGridParam','postData').serializedPreEditData};
				},
				afterSubmit:function(){
					// 2017.08.09 SV 同期処理を有効化
					$.extend($.jgrid.ajaxOptions, { async: false });

					//サブミット後、グリッド再取得
					var pagenum = $('#'+menumap_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{datatype:'json',page:pagenum,postData:{editing:true}});

					return [true,''];
				},
				afterComplete:function(response, postdata, formid){
					//サブミット後、直前の編集データをシリアライズ化してpostDataに一時保存
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//サブミット後、更新処理でエラーがあった場合、アラートを表示する。
					if(response.responseText){
						var res = $.parseJSON(response.responseText);
						if(res){
							if(res.message){
								showMessage($.jgrid.jqID(this.p.id), res.message, $.jgrid.nav.alertcap);
							}
						}
					}
					var pagenum = $('#'+menumap_info_tabId).jqGrid('getGridParam', 'page');
					$('#'+menumap_info_tabId).trigger("reloadGrid",[{page:pagenum}]);

					// 2017.08.09 SV 同期処理を解除
					$.extend($.jgrid.ajaxOptions, { async: true });

					return [true,''];
				},
				afterclickPgButtons:function(whichbutton, formid, rowid){
					//編集前データをシリアライズ化してpostDataに一時保存。
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{postData:{'serializedPreEditData': formid.serialize()}});
					//idをラベル表示にする。
					$('#tr_id #dispid', formid).remove();
					$('#tr_id .CaptionTD', formid).after('<td id="dispid">'+rowid+'</td>');
					$('#tr_id .DataTD', formid).hide();
					$('#tr_menuinfoid #dispmenuinfoid', formid).remove();
					$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
					$('#tr_menuinfoid .DataTD', formid).hide();
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
				addCaption:'<%=lang.__("Add - Menu map info")%>',
				bSubmit:'<%=lang.__("Save")%>',
				bCancel:'<%=lang.__("Cancel")%>',
				modal:true,
				closeOnEscape:true,
				zIndex: 100,
				beforeShowForm:function(formid){
					//フォームモードに登録モードを設定
					$(formid).after('<span id="myEditMode" style="display:none">add</span>');
					//行選択をはずす。
					$('#'+menumap_info_tabId).jqGrid('resetSelection');
					//idは非表示。
					$('#tr_id', formid).hide();
					$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
					$('#tr_menuinfoid .DataTD', formid).hide();
				},
				beforeSubmit:function(postdata, formid){
					return [true,"",""];
				},
				afterSubmit:function(){
					//サブミット後、グリッド再取得
					$('#'+menumap_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
				caption:'<%=lang.__("Delete - Menu map info")%>',
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
								$('#'+menumap_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
			'#'+menumap_info_pagerId,
			{
				id:'copy_menumap_info',
				caption:'',
				buttonicon:'ui-icon-newwin',
				position:'last',
				title:'<%=lang.__("Copy")%>',
				onClickButton:function(e){
					var rowid = $('#'+menumap_info_tabId).jqGrid('getGridParam', 'selrow');
					if(rowid){
						//コピー対象のグリッド列を選択済みの場合、コピーフォームを表示する。
						$('#'+menumap_info_tabId).jqGrid(
								'editGridRow',
								'new',
								{recreateForm: true,
									addCaption:'<%=lang.__("Record copy - Menu map info")%>',
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
										$('#tr_menuinfoid .CaptionTD', formid).after('<td id="dispmenuinfoid">'+row_id+'</td>');
										$('#tr_menuinfoid .DataTD', formid).hide();
									},
									beforeSubmit:function(postdata, formid){
										return [true,"",""];
									},
									afterSubmit: function(){	//フォームサブミット後、グリッドデータをサーバから再取得する設定。
										// 2017.08.09 SV triggerがあると2重リロードするので廃止
										//$('#'+menumap_info_tabId).jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
										$('#'+menumap_info_tabId).jqGrid('setGridParam',{datatype:'json'});
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
						$('#'+menumap_info_tabId).jqGrid('GridToForm', rowid, $('#FrmGrid_'+ menumap_info_tabId).id);
						//idはコピーしない。
						$('#FrmGrid_menumap_info #id').val('');
					}else{
						showMessage($.jgrid.jqID(this.p.id), $.jgrid.nav.alerttext, $.jgrid.nav.alertcap);
					}
				}
			}
	);	//グリッドナビゲーションend

	//リサイズ可能　リサイズできるようにすると横スクロールバーがでるため、一時コメントアウト
//	$('#'+menumap_info_tabId).jqGrid('gridResize');

	// 動的リサイズ。グリッド全体はmainFrameの画面幅の98%-開閉ハンドルで表示
	 $(window).bind('resize', function () {
	      $('#'+menumap_info_tabId).setGridWidth(getGridWidth(thisGridLevel));
	  }).trigger('resize');



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


	//クリアリングハウス検索ダイアログで地図を選択
function showrangemap() {
	if (!document.getElementById("rangeMapModalDialog")) {	//ダイヤログが2回作成されることを避ける。
		var dialogHtml = '';
		dialogHtml += "<div style='padding:3px;margin-bottom:2px;border:1px solid #DDDDDD;'>";
		dialogHtml += "Range MAP";
		dialogHtml += "</div>";

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
				resize:false,
				caption:'<%=lang.__("Range specification")%>',
				top:100,
				left:100,
				width:600,
				//height: 'auto',
				height: 650,
				closeOnEscape:true,
				zIndex:1000
			},
			'',
			'',
			true
		);


		//----------- ボタン設定 ----------
		//検索実行
		$("#cswSubmit").click(function() {searchClearingHouse(1);});
		//ダイアログを閉じる
		$("#cswCancel").click(closeClearingHouseDialog);

		$('#keyword').keypress(function (e) {
			if (e.which == 13) {
				searchClearingHouse(1);
			}
		});
	}

	//初期の検索
	//searchClearingHouse(1);

	//ダイアログの表示
	$.jgrid.viewModal("#rangeMapModalDialog",
		{modal:true}
	);
	$("#rangeMapModalDialog").focus();
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

		var openDiv = $("[id^='editmodmenumap_info']");
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
