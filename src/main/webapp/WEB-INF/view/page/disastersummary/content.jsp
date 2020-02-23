<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<script type="text/javascript" src="${f:url('/admin-js/js/jquery.jqGrid.min.js')}"></script>
<script type="text/javascript" src="${f:url('/admin-js/js/i18n/grid.locale-ja.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.exresize.0.1.0.js')}"></script>
<style type="text/css">
	body{
		font-size:11px;
	}
	.clear{
		clear:both;
	}
	h1{
		width:100%;
		border-bottom: 1px solid #6699CC;
		border-left: 5px solid #6699CC;
		font-size: 16px;
		font-weight: bold;
		margin-bottom: 15px;
		padding-left: 5px;
		padding-bottom:3px;
	}
	textarea{
		resize: none;
		font-size: 8pt;
		height:40px;
		width:100%;
	}
	#ds_wrapper {
		margin: 0 auto;
		width:100%;
		height:100%;
		background-color:#FFFFFF;
	}

	#ds_header{
		width:100%;
		margin-bottom:5px;
	}
	#foot_ctrl{
		width:100%;
		margin-top:5px;
		float:left;
		text-align:right;
	}
	#disp_div{
		width:50%;
		float:left;
	}
	#date_div{
		width:50%;
		float:right;
		text-align:right;
	}


	#ds_main{
		width:100%;
	}
	#ds_left_div{
		float:left;
		width:15%;
	}
	#ds_left_div table{
		width:100%;
	}
	#ds_center_div{
		width:55%;
		overflow-x:scroll;
		float:left;
	}
	#ds_right_div{
		float:left;
		width:30%;
	}
	#ds_right_div table{
		width:100%;
	}
	#ds_main table{
		margin:0px 0px 10px 0px;padding:0px;
		border-collapse:collapse;
		font-family:Arial, sans-serif;
		font-size:8pt;
		color:#000000;
	}
	#ds_main th{
		padding:3px;
		border:1px solid #BBBBBB;
		background-color:#4F81BD;
		color:#FFFFFF;
		font-weight:700;
		height:60px;
		white-space:nowrap;
		text-align:center;
	}
	#ds_main #ds_center_div th{
		height:30px;
	}
	#ds_main tbody tr{
		height:50px;
	}
	#ds_main td{
		padding:3px;
		border:1px solid #BBBBBB;
	}
	#ds_main .sumitem{
		text-align:right;
	}
	#ds_main input[type="text"]{
		width:60px;
		border:0;
		border:solid 1px #CCCCCC;
	}
	#ds_main .item_name input[type="text"]{
		width:100%;
	}
	#ds_main .areaitem input[type="text"],
	#ds_main .sumitem input[type="text"]{
		text-align:right;
	}
	#ds_main .sumitem input[type="text"]{
		width:80px;
	}
	#ds_main .memoitem input[type="text"]{
		width:100%;
	}
	#ds_main input[type="checkbox"]{
		border:0;
		border:solid 1px #CCCCCC;
	}
	#ds_main .ht_nondispflag{
		width:10px;
	}
	#ds_main .th_item{
		width:100%;
	}
	#ds_main .th_unit{
		width:30px;
	}
	#ds_main .th_area{
		width:68px;
	}
	#ds_main .th_sum{
		width:115px;
	}
	#ds_main .item_autosum{
		padding-right:2px;
	}
	#ds_main .th_memo{
	}
	#usertime{
		width:275px;
	}}
	.label_small{
		font-size:0.9em;
	}
	#ds_header input[type="button"],
	#foot_ctrl input[type="button"]{
		background: linear-gradient(to bottom, #FEFEFE, #F4F4F4) repeat scroll 0 0 #F3F3F3;
		border-color: #BBBBBB;
		color: #333333;
		text-shadow: 0 1px 0 #FFFFFF;
		-moz-box-sizing: border-box;
		border-radius: 3px;
		border-style: solid;
		border-width: 1px;
		cursor: pointer;
		display: inline-block;
		font-size: 12px;
		height: 24px;
		line-height: 23px;
		margin: 0;
		padding: 0 10px 1px;
		text-decoration: none;
		white-space: nowrap;
	}
	#ds_header select{
		border-radius: 3px;
		border-style: solid;
		border-width: 1px;
		border-color: rgb(204, 204, 204);
		margin-left:0;
		margin-right:10px;
	}
	#ds_header select option{
		padding-right:10px;
	}
	#ds_histdialog table{
		font-size:12px;
	}
</style>

<link rel="stylesheet" type="text/css" href="${f:url('/admin-js/css/ui.jqgrid.css')}" />
<style type="text/css">
	/* jqgrid用 */
	.ui-jqgrid tr.jqgrow td{
		border-bottom-color:#AAAAAA;
		border-right-color:#AAAAAA;
	}
	.ui-th-column .ui-th-ltr{
		border-bottom-color:#AAAAAA;
	}
	.ui-widget-content .ui-state-highlight{
		background: #fbec88 50% 50% repeat-x;
	}
</style>

<script type="text/javascript">
		var MAX_AREA_NUM = ${MAX_AREA_NUM};
		var initFlg = true;
		var btnUrl = {};
		var isRegistered = ${isRegistered};

		//過去集計選択画面を表示
		function showHistory(){
			//ダイアログのHTML
			var dialogHtml = '';
			bS  ="<a href='javascript:void(0)' id='selectData' class='fm-button ui-state-default ui-corner-all fm-button-icon-left'><%=lang.__("Select")%><span class='ui-icon ui-icon-disk'></span></a>",
			bC  ="<a href='javascript:void(0)' id='cancelData' class='fm-button ui-state-default ui-corner-all fm-button-icon-left'><%=lang.__("Close")%><span class='ui-icon ui-icon-close'></span></a>";
			var bt = "<table border='0' cellspacing='0' cellpadding='0' class='EditTable' id='TblGrid'><tbody><tr id='Act_Buttons'><td class='EditButton'>"+bS+bC+"</td></tr>";
			bt += "</tbody></table>";
			dialogHtml = '<div id="ds_histdialog"><table id="ds_histgrid" ></table><div id="ds_histnav" ></div>'+bt+'</div>';

			//既存ダイアログがない場合
			if($('#hisaisumupModalTableDialog').size()==0){
				//ダイアログ作成
				$.jgrid.createModal(
					IDs = {
						themodal:'hisaisumupModalTableDialog',
						modalhead:'hisaisumupModalTableDialogHd',
						modalcontent:'hisaisumupModalTableDialogCnt',
						scrollelm : ''
					},
					dialogHtml,
					{
						gbox:'#gbox_editmod',
						jqModal:true,
						drag:true,
						resize:true,
						caption:'<%=lang.__("Disaster summary")%>',
						top:150,
						left:100,
						width:'auto',
						height: 'auto',
						closeOnEscape:true,
					},
					'',
					'',
					true
				);

				//ダイアログの表示
				$.jgrid.viewModal('#hisaisumupModalTableDialog',
					{modal:true,}
				);
				$('#hisaisumupModalTableDialog').focus();

				//ダイアログにグリッドとナビゲーションを表示
				$('#ds_histgrid').jqGrid({
					mtype: 'POST',
					url: "${f:url('historyIndex')}",
					datatype: 'json',
					colNames: [
					<c:forEach var="historyGridHeaderName" items="${historyGridHeaderDispNameList}" varStatus="status">
					<c:if test="${historyGridHeaderName == ''}">
					"",
					</c:if>
					<c:if test="${historyGridHeaderName != ''}">
					"${f:h(historyGridHeaderName)}",
					</c:if>
					</c:forEach>
					],
					colModel: [
						<c:forEach var="historyGridHeaderName" items="${historyGridHeaderNameList}" varStatus="status">
						<c:if test="${status.index == 0}">
						{name:"${f:h(historyGridHeaderName)}",width:'120',align:'right',editable:false,search:true,hidden:true},
						</c:if>
						<c:if test="${status.index == 1 || status.index == 2}">
						{name:"${f:h(historyGridHeaderName)}",width:'160',align:'left',editable:false,search:true,hidden:false},
						</c:if>
						<c:if test="${status.index > 2}">
						{name:"${f:h(historyGridHeaderName)}",width:'80',align:'right',editable:false,search:true,hidden:false,formatter:'number', formatoptions:{thousandsSeparator: ",", decimalPlaces: 0, defaultValue:""}},
						</c:if>
						</c:forEach>
					],
					caption: '<%=lang.__("Aggregate of past data")%>',
					loadtext:'<%=lang.__("Now loading..")%>',
					loadui:'block',
					loadonce:true,
					rowNum:15,
					rowList:[5,10,15,20],
					width:'auto',
					height: 'auto',
					multiselect: false,
					editurl:'',
					cellEdit: false,
					cellsubmit: 'clientArray',
					sortname: 'update_datetime',
					sortorder: 'ASC',
					subGrid: false,
					viewrecords: true,
					postData:{'loadonce':true},
					pager: '#ds_histnav',
					loadComplete : function (data) {
/*						var trOddSelector = '#ds_histgrid'+' tr:odd';
						$(trOddSelector).removeClass('ui-widget-content');
						$(trOddSelector).addClass('myodd');
*/					},
				}).navGrid("#ds_histnav",
					//ナビゲーション
					{edit:false,add:false,del:false,search:true,
						beforeRefresh: function(){
							//リフレッシュボタン押下時、サーバ問い合わせ。
							$('#ds_histgrid').jqGrid('setGridParam',{datatype:'json'});
							return [true,''];
						}
					},
					{},
					{},
					{},
					{recreateForm: true, modal:true,}
				);

				//タイトルバーの×ボタンのコールバック関数
				var headerCancelSelector = '#hisaisumupModalTableDialogHd'+' a.ui-jqdialog-titlebar-close';
				$(headerCancelSelector).click(function(){
						$.jgrid.hideModal('#hisaisumupModalTableDialog',{jqm:true});
					}
				);

				//フッターのキャンセルボタンのコールバック関数
				$(document).on(
					'click',
					'#cancelData',
					function(){
						$.jgrid.hideModal('#hisaisumupModalTableDialog',{jqm:true});
					}
				);

			}else{
				//既存ダイアログがある場合
				//ダイアログの表示
				$.jgrid.viewModal('#hisaisumupModalTableDialog',
					{modal:true}
				);
				$('#hisaisumupModalTableDialog').offset({top:150, left:100}).focus();
				//グリッドをリロード
				$('#ds_histgrid').jqGrid('setGridParam',{datatype:'json'});
				$('#ds_histgrid').trigger('reloadGrid');
			}
		}

		$(document).ready(function(){
			$(window).load(function(){
				init();
				initFlg = false;

			})

			//初期処理
			function init(){
				if(isRegistered){
					//表示する地区列数を初期設定する。
					changeAreaNum($("#areanum").val());
					//表示する行数を初期設定する。
					changeItemDisp("init");
				}else{
					//未登録時、過去集計表示ボタン不活性化
					<%-- ボタンを個別指定できないので、最右ボタンを決め打ちで非活性化する --%>
					var selector = "#footer .buttons li:first a";
					changeValidBtn(selector, false);
				}
				//表の背景色をゼブラ
				addStripePattern("item_tr_left", "item_tr_center", "item_tr_right");
			}

			//指定クラス要素の背景色をゼブラにする
			function addStripePattern(){
				for(i=0; i < arguments.length; i++){
					selectorEven = "." + arguments[i] + ":visible:even";
					$(selectorEven).css("background-color", "#F0F0F6");
					selectorOdd = "." + arguments[i] + ":visible:odd";
					$(selectorOdd).css("background-color", "#FFFFFF");
				}
			}

			//ボタンを活性・非活性を切り替える
			function changeValidBtn(selector, valid){
				var className;
				var href;
				if(valid){
					className = "btn blue";
					href = btnUrl[selector];
				}else{
					className = "btn lgray";
					href = $(selector).prop("href");
					btnUrl[selector] = href;
					href = "#";
				}
				$(selector).prop("class", className);
				$(selector).prop("href", href);
			}

			//項目エラー表示
			function myError(selector, mode){
				if(mode == "on"){
					$(selector).css("background-color", "#FFC0CB");
				}else if(mode == "off"){
					$(selector).css("background-color", "#FFFFFF");
				}
			}
			//初回のみ非表示合計値数値を0セット
			$.each($(".sumitem input[type='hidden']"), function(){
				if($(this).val() == ""){
					$(this).val("0");
				}
			});
			//数値項目のカンマ編集
			$.each($(".areaitem input, .sumitem input[type='text']"), function(){
				setComma(this);
			});
			//数値項目のカンマ編集と入力チェック
			$(".areaitem input, .sumitem input").blur(function(){
				var val = $(this).val();
				if(!setComma(this)){
					//数値以外文字あり
					myError(this, "on");
				}else{
					//桁チェック
					var lengthError = false;
					var length = val.length;
					if($(this).parent().hasClass("areaitem")){
						if(length > ${MAX_AREAITEM_INPUT_SIZE}){
							lengthError = true;
						}
					}else{
						if(length > ${MAX_SUMITEM_INPUT_SIZE}){
							lengthError = true;
						}
					}

					if(lengthError){
						myError(this, "on");
					}else{
						myError(this, "off");
					}
				}
			});
			//数値項目のカンマ除去
			$(".areaitem input, .sumitem input").focus(function(){
				removeComma(this);
			});
			//数値項目のカンマ編集
			$.each($(".item_autosum"), function(){
				$(this).html(getCommaVal($(this).text()));
			});

			//自動集計値計算イベント
			$(".areaitem input").blur(function(){
				sum($(this).attr("class"));
			});

			//集計値計算
			function sum(classname){
				var total = 0;
				var i = 0;
				$("."+classname).each(function(idx){
					num = $(this).val();
					if(!num || isNaN(num)){
						num = getRemoveCommaVal(num);
						if(!num || isNaN(num)){
							return true;	//continue
						}
					}
					total = parseInt(total) + parseInt(num)

					//表示地区のみ集計対象
					i = i+1;
					if(i == $("#areanum").val()){
						return false;	//break
					}
				});
				$("#" + classname + "_autosum").html(getCommaVal(total));
				$("#" + classname + "_autosum_hidden").val(total);
			}

			//適用ボタン押下イベント
			$(document).on(
				'click',
				'#apply_btn',
				function(){
					var showoper = $("#showoper").val();
					var areanum = $("#areanum").val();
					//被害項目行の表示行数変更
					if(showoper){
						changeItemDisp(showoper);
						$("#showoper").val("");
						addStripePattern("item_tr_left", "item_tr_center", "item_tr_right");
					}
					//地区列の表示列数変更
					if(areanum){
						changeAreaNum(areanum);
						//自動集計値を再計算
						var sumItemClassName;
						for(i=1; i<=${MAX_DISASTERITEM_NUM}; i++){
							sumItemClassName = "item"+i;
							sum(sumItemClassName);
						}
					}
				}
			);

			//行の表示非表示を切り替える
			function changeItemDisp(showoper){
				if(showoper == "init" || showoper == "hideitem"){
					var items = $(".dispflag:checkbox");
					items.each(function(idx, checkbox){
						if($(checkbox).prop("checked")){
							idx = parseInt(idx) + 1;
							$("#item" + idx + "_tr_left").hide();
							$("#item" + idx + "_tr_center").hide();
							$("#item" + idx + "_tr_right").hide();
						}
					});
				}else if(showoper == "showall"){
					$(".item_tr").show();
					$(".dispflag:checkbox").prop("checked", false);
				}
			}

			/** 選択ボタン押下イベント */
			$(document).on(
					'click',
					'#selectData',
					function(){
						var rowid = $("#ds_histgrid").jqGrid('getGridParam','selrow');
						if(!rowid){
							alert("<%=lang.__("Select aggregate of past data")%>");
							return false;
						}
						var rowdata = $("#ds_histgrid").jqGrid('getRowData',rowid);
					 	$('#the_form').submit(function(){
					 		<c:set var="urlparam" value="/page/?menutaskid=${menutaskid}&menuid=${menuid}" />
					 		$(this).prop("action", "${f:url(urlparam)}");
					 		$("#selectedDisastersummaryhistoryid").val(rowdata['disastersummaryhistoryid']);
					 	}).submit();
					}
				);

			/** ウインドウリサイズ完了時に列数調整 */
			var timer = false;
			$(window).resize(function() {
				if (timer !== false) {
					clearTimeout(timer);
				}
				timer = setTimeout(function() {
					changeAreaNum($("#areanum").val());
				}, 200);
			});

			//左ペインのリサイズイベント時の地区列幅調整
			$("#menu").exResize(function(){
				changeAreaNum($("#areanum").val());
				return true;
			});
		});

		function setComma(item){
			var val = $(item).val();
			var commaVal = getCommaVal(val);
			if(commaVal ===  false){
				return false;
			}else{
				$(item).val(commaVal);
				return true;
			}
		}
		function removeComma(item){
			var val = $(item).val();
			$(item).val(getRemoveCommaVal(val));
		}
		function getCommaVal(val){
			if(val == ""){
				return 0;
			}
			val = "" + val;
			if (!jQuery.isNumeric(val)) {
				return false;
		    }else{
				while (val != (val = val.replace(/^(-?\d+)(\d{3})/, "$1,$2")));
		    }
			return val;
		}
		function getRemoveCommaVal(val){
			val = "" + val;
			return val.replace(/^\s+|\s+$|,/g, "");
		}

		//地区列の表示非表示を切り替える
		function changeAreaNum(areanum){
			var i;
			for(i=0; i<=MAX_AREA_NUM; i++){
				if(i<parseInt(areanum)+1){
					$(".area" + i).show();
				}else{
					$(".area" + i).hide();
				}
			}
			//表示する地区列の変更に伴い、表の幅を調整。
			var areaRowsSize =  getArearowsSize(areanum) + "px";
			$("#ds_center_div").width(areaRowsSize);
		}

		//地区列の横幅を取得する
		function getArearowsSize(areanum){
			var ds_main = $("#ds_main").width();
			var area_div_width = $("#ds_center_div").width();
			var area_col_width;
			if(initFlg && needAdjustWidth()){
				area_col_width = parseInt($(".th_area.area1").width());
			}else{
				area_col_width = parseInt($(".th_area.area1").outerWidth());
			}
			var area_col_width_total = parseInt(area_col_width)*parseInt(areanum);

			if(needAdjustWidth()){
				ds_main -= $.layout.defaults.west.size; //メニューペインのデフォルトサイズ
				ds_main -=  6; //左ペイン枠
				ds_main -= 10*2; //padding
			}
//console.log("area_div_width : "+area_div_width);
//console.log("area_col_width_total : "+area_col_width_total);
//console.log("ds_main*0.55 : "+ Math.floor(ds_main*0.55));

			if(area_div_width > area_col_width_total){
				//狭める
				return area_col_width_total;
			}else{
				//広げる
				if(area_col_width_total > ds_main*0.55){
					return Math.floor(ds_main*0.55)-1;
				}else{
					return area_col_width_total;
				}
			}
		}

		/** データの追加処理 */
		function saveData(){
			if (!confirm('<%=lang._E("Register disaster summary.\n Are you sure?")%>')) {
				return;
			}

			var form = document.getElementById("the_form");
	 		//入力チェック
	 		var hasError = false;
	 		//地区の数値の数値チェック、サイズチェック
			var areaitemnums = $(".areaitem :text");
	 		areaitemnums.each(function(idx, areaitemnum){
				val = getRemoveCommaVal($(areaitemnum).val());
				if (val != "" && !jQuery.isNumeric(val)) {
					hasError = true;
					alert("<%=lang.__("Input value of a numeric is invalid.")%>");
					return false;
				}
				if(val.length > ${MAX_AREAITEM_INPUT_SIZE}){
					hasError = true;
					alert("<%=lang.__("Input digit number of numeric is invalid.")%>");
					return false;
				}
			});

	 		if(!hasError){
	 			//手動入力の数値の数値チェック、サイズチェック
				var sumitemnums = $(".sumitem :text");
				sumitemnums.each(function(idx, sumitemnum){
					val = getRemoveCommaVal($(sumitemnum).val());
					if (val != "" && !jQuery.isNumeric(val)) {
						hasError = true;
						alert("<%=lang.__("Input value of a numeric is invalid.")%>");
						return false;
					}
					if(val.length > ${MAX_SUMITEM_INPUT_SIZE}){
						hasError = true;
						alert("<%=lang.__("Input digit number of numeric is invalid.")%>");
						return false;
					}
				});
	 		}

	 		if(!hasError){
				$("#the_form").prop("action", "${f:url('saveData')}");
				$("#the_form").mask("<%=lang.__("Now registering..")%>");
				form.submit();
	 		}
 		}

		//幅補正要否判定用
		function needAdjustWidth(){
			if(initFlg){
				var ds_main = $("#ds_main").width();
				var windowWidth = document.documentElement.clientWidth
				if(ds_main == windowWidth){
					return true;
				}
			}
			return false;
		}

//CSVファイル出力
function outputCSV(form){

	//ここでテーブル表示の内容と同じループを回して、CSVの元になるテキストを出力して二重配列の変数にセットする
	var datas = new Array();
	//ヘッダ
	var row = new Array("<%=lang.__("Damage item")%>", "<%=lang.__("Unit")%>");
	datas.push(row);
	for (var i = 1; i <= MAX_AREA_NUM; i++) {
		row.push("${i} ${f:h(disastersummaryForm.disastersummaryhistoryData[area])}");
	}
	row.push("<%=lang.__("Total<!--2-->")%>");
	row.push("<%=lang.__("Notes")%>");

	//formのPOSTで文字列を送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('outputcsv/')}";
		form.method = "POST";
		form.value.value = "";
		form.submit();
	}while(0);
	$("#csv-dialog").dialog("close");
}

//PDFファイル出力
function outputPDF(form){
	var firstRow = $("#areanum").val();
	var secondRow = 0;
	if (firstRow > 10) {
		secondRow = firstRow;
		firstRow = 10;
	}

	//ここでテーブル表示の内容と同じループを回して、PDFの元になるテキストを出力して二重配列の変数にセットする
	var datas = new Array();
	//ヘッダ
	var row = new Array("<%=lang.__("Damage item")%>", "<%=lang.__("Unit")%>");
	datas.push(row);
	for (var i = 1; i <= firstRow; i++) {
		row.push(i + " " + $('#area' + i).val());
	}
	if (secondRow) {
		row.push("");
		row.push("");
		row.push("");
		row = new Array("", "");
		datas.push(row);
		for (var i = 11; i <= 20; i++) {
			if (i <= secondRow)
				row.push(i + " " + $('#area' + i).val());
			else
				row.push("");
		}
	}
	row.push("<%=lang.__("Total (from)")%>");
	row.push("<%=lang.__("Total (hand)")%>");
	row.push("<%=lang.__("Notes")%>");

	//合計
	datas.push(null);

	//データ
	<c:forEach var="disastersituationhistoryData" items="${disastersummaryForm.disastersituationhistoryDataList}" varStatus="status">
		if (!$('#item${status.count}_tr_left').is(':hidden')) {
			var row = new Array();
			datas.push(row);
			row.push($('#item${status.count}_tr_left .item_name input').val());
			row.push($('#item${status.count}_tr_left .unit input').val());
			for (var i = 1; i <= firstRow; i++) {
				row.push($('#item${status.count}_' + i).val());
			}
			if (secondRow) {
				row.push('');
				row.push('');
				row.push('');
				row = new Array();
				datas.push(row);
				row.push('');
				row.push('');
				for (var i = 11; i <= 20; i++) {
					if (i <= secondRow)
						row.push($('#item${status.count}_' + i).val());
					else
						row.push('');
				}
			}
			row.push($('#item${status.count}_autosum').text());
			row.push($('#item${status.count}_manualsum').val());
			row.push($('#item${status.count}_memo').val().substring(0,22));
		}
	</c:forEach>

	//formのPOSTで文字列をJSON形式で送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('/page/list/outputpdf/')}";
		form.method = "POST";
		form.dataList.value = JSON.stringify(datas);
		form.submit();
	}while(0);
	$("#pdf-dialog").dialog("close");
}

$(function() {
	//ボタンのスタイル設定
	$(".dialog-button").button();
});
</script>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
	<c:if test="${disastersummaryForm.disastersummaryhistoryData!=null}">
	<div id="ds_wrapper">
<!--		<h1>被災集計機能</h1> -->
		<form:form method="post" id="the_form" modelAttribute="disastersummaryForm">
			<div id="ds_header">
				<div id="disp_div">
					<span class="label_small"><%=lang.__("Damage item")%>：</span>
					<select name="showoper" id="showoper">
						<option value=""></option>
						<option value="hideitem"><%=lang.__("Hide select.")%></option>
						<option value="showall"><%=lang.__("Show all")%></option>
					</select>
					<span class="label_small"><%=lang.__("Number of district")%>：</span>
						<form:select path="disastersummaryhistoryData.areanum" id="areanum" >
						<c:set var="isSelected" value="0" />
						<c:forEach var="i" begin="1" end="${MAX_AREA_NUM}">
							<c:if test="${i==disastersummaryForm.disastersummaryhistoryData.areanum}">
								<c:set var="selected" value=" selected=\"selected\"" />
								<c:set var="isSelected" value="1" />
							</c:if>
							<c:if test="${i!=disastersummaryForm.disastersummaryhistoryData.areanum}">
								<c:set var="selected" value="" />
							</c:if>
							<c:if test="${isSelected==0 && i == MAX_AREA_NUM}">
								<c:set var="selected" value=" selected=\"selected\"" />
							</c:if>
						<option value="${i}" ${selected}>${i}</option>
						</c:forEach>
						</form:select>
					<a href="#" class="btn blue" name="apply_btn" id="apply_btn"><%=lang.__("Application")%></a>
				</div>
				<div id="date_div">
					<c:if test="${disastersummaryForm.disastersummaryhistoryData.usertime == null}">
					<c:set var="usertime" value="${defaultUsertime}" />
					</c:if>
					<c:if test="${disastersummaryForm.disastersummaryhistoryData.usertime != null}">
					<c:set var="usertime" value="${disastersummaryForm.disastersummaryhistoryData.usertime}" />
					</c:if>
					<span class="label_small"><%=lang.__("User date and time")%></span><form:input path="disastersummaryhistoryData.usertime" value="${f:h(usertime)}" id="usertime" />
  				</div>
				<div class="clear"></div>
			</div>

			<div id="ds_main">
				<div id="ds_left_div">
					<table>
						<thead>
							<tr>
								<th class="ht_nondispflag"></th>
								<th class="th_item"><%=lang.__("Damage item")%></th>
								<th class="th_unit"><%=lang.__("Unit")%></th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="disastersituationhistoryData" items="${disastersummaryForm.disastersituationhistoryDataList}" varStatus="status">
							<tr class="item_tr item_tr_left" id="item${status.count}_tr_left">
								<td><form:checkbox path="disastersituationhistoryDataList[${status.index }].dispflag" cssClass="dispflag" /></td>
								<td class="item_name"><form:input path="disastersituationhistoryDataList[${status.index }].damageitem" /></td>
								<td class="unit"><form:input path="disastersituationhistoryDataList[${status.index }].unit" /></td>
								<form:hidden path="disastersituationhistoryDataList[${status.index }].lineno" value="${status.count}" />
						</c:forEach>
						</tbody>
					</table>
				</div>
				<div id="ds_center_div">
					<table>
						<thead>
							<tr>
								<c:forEach var="i" begin="1" end="${MAX_AREA_NUM}">
								<th class="th_area area${i}">${i}</th>
								</c:forEach>
							</tr>
							<tr id="area">
								<c:forEach var="i" begin="1" end="${MAX_AREA_NUM}">
								<th class="area area${i}"><form:input path="disastersummaryhistoryData.area${i}"  id="area${i}" indexed="false" /></th>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="disastersituationhistoryData" items="${disastersummaryForm.disastersituationhistoryDataList}" varStatus="status">
							<tr id="item${status.count}_tr_center" class="item_tr item_tr_center">
								<c:forEach var="i" begin="1" end="${MAX_AREA_NUM}">
									<td class="areaitem area${i}">
										<form:input path="disastersituationhistoryDataList[${status.index }].area${i }people" id="item${status.count}_${i}" cssClass="item${status.count}" />
									</td>
 								</c:forEach>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
				<div id="ds_right_div">
					<table>
						<thead>
							<tr>
								<th class="th_sum"><%=lang.__("Total<!--2-->")%></th>
								<th class="th_memo"><%=lang.__("Notes")%></th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="disastersituationhistoryData" items="${disastersummaryForm.disastersituationhistoryDataList}" varStatus="status">
							<tr class="item_tr item_tr_right" id="item${status.count}_tr_right">
								<td class="sumitem">
									<div>
									<span id="item${status.count}_autosum" class="item_autosum">${disastersituationhistoryData.autototal}</span>
									<span class="label_small""><%=lang.__("(Auto)")%></span>
									<form:hidden path="disastersituationhistoryDataList[${status.index }].autototal" id="item${status.count}_autosum_hidden" />
									</div>
									<div>
										<form:input path="disastersituationhistoryDataList[${status.index }].manualtotal" id="item${status.count}_manualsum" />
										<span class="label_small"><%=lang.__("(Manual)")%></span>
									</div>
								</td>
								<td class="memoitem"><form:textarea path="disastersituationhistoryDataList[${status.index }].note" id="item${status.count}_memo" rows="2" /></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<form:hidden path="menutaskid" />
			<form:hidden path="menuid" />
			<form:hidden path="selectedDisastersummaryhistoryid" id="selectedDisastersummaryhistoryid"/>
		</form:form>
	</div>
	</c:if>

<%-- CSVファイル出力 --%>
<div id="csv-dialog" title="<%=lang.__("CSV file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as CSV file.")%><br>
		<%=lang.__("* In case of filtering settings configured, only searched data (not in gray color)  are displayed.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputcsv" modelAttribute="disastersummaryForm">
			<input type="hidden" name="value" value="" >
			<a href='javascript:outputCSV(document.getElementById("outputcsv"));' class="dialog-button" ><%=lang.__("CSV output")%></a>
		</form:form>
	</div>
</div>

<%-- PDFファイル出力 --%>
<div id="pdf-dialog" title="<%=lang.__("PDF file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as PDF file.")%><br>
		<%=lang.__("* In case of filtering settings configured, only searched data (not in gray color)  are displayed.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputpdf" modelAttribute="disastersummaryForm">
			<input type="hidden" name="dataList" value="">
			<input type="hidden" name="menuid" value="${menuid}">
			<input type="hidden" name="menutaskid" value="${menutaskid}">
			<a href='javascript:outputPDF(document.getElementById("outputpdf"));' class="dialog-button" ><%=lang.__("PDF output")%></a>
		</form:form>
	</div>
</div>
