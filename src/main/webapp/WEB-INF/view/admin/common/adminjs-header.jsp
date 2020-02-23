<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/lang_resource.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<!--[if lt IE 9]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
	<link type="text/css" media="screen" href="${f:url('/admin-js/css/redmond/jquery-ui-1.10.3.custom.min.css')}" rel="stylesheet" />
	<!-- <link type="text/css" media="screen" href="${f:url('/admin-js/css/start/jquery-ui-1.10.3.custom.min.css')}" rel="stylesheet" />-->
	<!-- <link type="text/css" media="screen" href="${f:url('/admin-js/css/pepper-grinder/jquery-ui-1.10.3.custom.min.css')}" rel="stylesheet" />-->
	<!-- <link type="text/css" media="screen" href="${f:url('/admin-js/css/smoothness/jquery-ui-1.10.3.custom.min.css')}" rel="stylesheet" /> -->
	<link type="text/css" media="screen" href="${f:url('/admin-js/css/ui.jqgrid.css')}" rel="stylesheet" />
	<style type="text/css">body {font-size: 11px;}</style>
	<link type="text/css" media="screen" href="${f:url('/css/style_adminjqgrid.css')}" rel="stylesheet" />
	<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.css" />
	<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.js')}"></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery-ui-1.10.3.custom.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery.jqGrid.min.js')}" ></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/i18n')}/<%= lang.getJqgridLocaleFile() %>" ></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery.dynatree.min.js')}" ></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery.bgiframe.min.js')}" ></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/ajaxfileupload.js')}" ></script>
	<script type="text/javascript" src="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.min.js"></script>
	<script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/falutil.js')}"></script>
	<script type="text/javascript">

		window.SaigaiTask = {
			contextPath: "<%=request.getContextPath()%>",
			csrfToken: "<%= session.getId() %>"
		};

		//for IE
		{
			// ダミーのコンソール
			SaigaiTask.console = {
				log : function() {},
				debug : function() {},
				info : function() {},
				warn : function() {},
				error : function() {},
				assert : function() {},
				trace : function() {}
			};

			if (typeof window.console == "undefined") {
				window.console = SaigaiTask.console;
			}
			else {
				for ( var idx in SaigaiTask.console) {
					if (typeof window.console[idx] == "undefined") {
						window.console[idx] = SaigaiTask.console[idx];
					}
				}
			}
		}
		
		$(document).ready(function(){
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			$(document).ajaxSend(function(e, xhr, options) {
                if(xhr!=null && typeof xhr.setRequestHeader=="function") {
    			    xhr.setRequestHeader(header, token);
                }
			});
			SaigaiTask.ajaxcsrfToken = token;
		});

		// set JqGrid ajaxSelectOptions.cache=false
		// @see http://www.trirand.com/jqgridwiki/doku.php?id=wiki:options
		if(!$.jgrid.defaults.ajaxSelectOptions) $.jgrid.defaults.ajaxSelectOptions = {};
		$.jgrid.defaults.ajaxSelectOptions.cache = false;

		//グリッド階層保持２次配列変数。
		//テーブル名を保持する。
		var breadcrumbs = new Array();

		//IE8以下用にindexOfファンクションを実装
		if (!Array.indexOf) {
			Array.prototype.indexOf = function(o) {
				for (var i in this) {
					if (this[i] == o) {
						return i;
					}
				}
				return -1;
			}
		}

		// 編集ボタンが押された場合、警告ダイアログの表示位置を常にウインドウ中央にセットする
		var orgViewModal = $.jgrid.viewModal;
		$.extend($.jgrid, {
	    	viewModal: function (selector, o) {
	    		var selector_chkstr = ' ' + selector;
	    		var modal_chkstr = '#alertmod';
	    		if (selector_chkstr.indexOf(" " + modal_chkstr) !== -1) {
	            	var $gbox = $(o.gbox), $selector = $(selector);
	            	var of = $gbox.offset(), w = $gbox.width(), h = $gbox.height();
	            	var w1 = $selector.width(), h1 = $selector.height();
	            	$selector.css({
	                	'top': of.top + ((h-h1)/2),
	                	'left': of.left + ((w-w1)/2)
	            	});
	        	}
	        	orgViewModal.call(this, selector, o);
	    	}
		});

		/**
		 * 自グリッドのテーブル名から階層番号を返却する関数。<br>
		 * @parentGrid 親グリッドのテーブル名
		 * @selfGrid 自グリッドのテーブル名
		 * @return 1以上の階層番号。１階層目は1、２階層目は2...。
		 */
		function getBreadcrumb(parentGrid, selfGrid){
			if(!parentGrid){
				//最上位階層
				if(breadcrumbs.length == 0){
					//まだ空の場合
					breadcrumbs[0] = new Array(selfGrid);
				}else{
					//最上位階層データが存在する場合、自分自身の登録がなければ登録する。
					if(breadcrumbs[0].indexOf(selfGrid) == -1){
						breadcrumbs[0].push(selfGrid);
					}else{
					}
				}
				//最上位階層番号1を返却。
				return 1;
			}else{
				//最上位でない場合
				for(var i=0; i<breadcrumbs.length; i++){
					for(var j=0; j<breadcrumbs[i].length; j++){
						if(breadcrumbs[i][j] == parentGrid){
							//親のindexを探し、+1して自身のindexをセット。
							var idx = Number(i)+Number(1);
							if(breadcrumbs[idx] == null){
								breadcrumbs.push(new Array(selfGrid));
							}else{
								//自分自身の登録がなければ登録する。
								if(breadcrumbs[idx].indexOf(selfGrid) == -1){
									breadcrumbs[idx].push(selfGrid);
								}else{
								}
							}
							//階層番号は配列のindex+1を返却。
							return idx+Number(1);
						}
					}
				}
			}
		}

		/**
		 * subgrid_idから親のテーブル名を返却する関数。
		 */
		function getParentTableName(subgridid){
			if(subgridid == null){
				return '';
			}
			return subgridid.substring(0, subgridid.search(/\d*_\d*$/));
		}

		/**
		 * "_"で連結されたテーブル名をJavaのキャメル型のメソッド名に変換する関数。
		 */
		function convertTabNameToMethodMame(tabName){
			var methodName = '';
			var tabNameAry = tabName.split('_');
			for(var i= 0;  i<tabNameAry.length; i++){
				if(i==0){
					methodName = tabNameAry[i];
				}else{
					methodName += (tabNameAry[i].substring(0, 1).toUpperCase() + tabNameAry[i].substring(1).toLowerCase());
				}
			}
			return methodName;
		}

		/**
		 * null値を空文字に変換するフォーマット変換関数。
		 */
		jQuery.extend($.fn.fmatter , {
		    nullstrToEmptyStrFmatter : function(cellvalue, options, rowdata) {
		    	if(cellvalue == null){
				    return '';
				}else{
					return $.jgrid.htmlEncode(cellvalue);
				}
		}});

		/**
		 * null値を空文字に変換したフォーマットを元に戻すフォーマット関数。
		 */
		jQuery.extend($.fn.fmatter.nullstrToEmptyStrFmatter , {
		    unformat : function(cellvalue, options) {
			}
		});

		/**
		 * selectボックス項目をグリッド上「id：値」形式で表示する表示フォーマット関数。<br>
		 * 内部でのデータの持ち方は、「id:id：値」。<br>
		 * "id：値"の部分をグリッド上に表示する。
		 */
		jQuery.extend($.fn.fmatter , {
			customSelect : function(cellvalue, options, rowdata) {

//			if(cellvalue == 0){
//				return 0;
//			}
//			if(cellvalue == ''){
//console.log("*****");
//				return '';
//			}
			var elms = options['colModel']['editoptions']['value'].split('|d|')
			for(i=0;i<elms.length;i++){
				var elm = elms[i].split(':');
				if(elm[0] != '' && elm[0] == cellvalue){
					if(elm[1] != ''){
						return elm[1];
					}
				}
			}
			return '';
		}});

		/**
		 * selectボックス項目をグリッド上「id：値」形式で表示する表示フォーマットをidに戻すアンフォーマット関数。<br>
		 * 内部でのデータの持ち方は、「id:id：値」。
		 * "id：値"の部分をグリッド上に表示する。
		 */
		jQuery.extend($.fn.fmatter.customSelect , {
			unformat : function(cellvalue, options) {
				if(cellvalue == ''){
					return '';
				}
//				return cellvalue.split('：')[0];
			var elms = options['colModel']['editoptions']['value'].split('|d|');
//			var name = options['colModel']['name'];
			for(i=0;i<elms.length;i++){
				var elm = elms[i].split(':');
				if($.jgrid.htmlDecode(elm[1]) == cellvalue){
					return elm[0];
				}
			}

			//cellvalue(グリッド上に表示する値)に一致するものがない(idも表示しない)場合は
			//班情報のシステム管理者レコードの自治体ID項目の場合のみ。
			//このときの自治体IDは「0」なので「0」を返却。
			return 0;
			}
		});

		/**
		 * subgrid_idから親テーブルへの参照キーを取得する関数。<br>
		 * テーブルによっては別ページでそれぞれ別の親テーブルに紐付く場合あり。<br>
		 * subgrid_idはテーブル名+"_"+連番。
		 */
		function getParentgridRefkey(subgridid, parentgridNamesStr, parentgridRefkeysStr){
			var parentgridNames = parentgridNamesStr.split("#");
			var parentgridRefkeys = parentgridRefkeysStr.split("#");
			var parentgrid_name = subgridid.substring(0, subgridid.search(/\d*_\d*$/));
			if(parentgridNames.length == 1){
				return parentgridRefkeys[0];
			}else{
				for(var i=0; i<parentgridNames.length; i++){
					if(parentgridNames[i] == parentgrid_name){
						return parentgridRefkeys[i];
					}
				}
			}
		}

		/**
		 * プルダウンメニューを作成する関数。
		 * @rowid 選択行id
		 * @selectTag プルダウンリスト
		 * @selectedId 選択している項目の値
		 * @gridName グリッドのテーブル名
		 * @selectName プルダウンのデコード値保持テーブル名
		 * @addBlank プルダウンリストにブランクを追加するか否かフラグ
		 */
		function createSelectTag(rowid, selectTag, selectedId, gridName, selectName, addBlank){
			if(addBlank == null){
				addBlank = false;
			}
			var s = '<select>';
			//班情報のシステム管理者レコードの自治体ID項目の場合、id=0の選択肢を追加する。
			if(rowid == '0'
					&& gridName == 'group_info'
					&& selectName == 'localgov_info'){
				s += '<option value="0" selected>0</option>';
			}else if(gridName == 'alarmmessage_info' && selectName == 'group_info'){
				s += '<option value="0" selected><%=lang.__("0: Default")%></option>';
			}else{
				if(addBlank){
					s += '<option value=""></option>';
				}
			}
			if (selectTag && selectTag.length) {
				for (var i = 0; i < selectTag.length; i++) {
					if(rowid != null && selectTag[i].key == selectedId){
						s += '<option value="'+ selectTag[i].key+'" selected="selected">'+selectTag[i].value+'</option>';
					}else{
						s += '<option value="'+ selectTag[i].key+'">'+selectTag[i].value+'</option>';
					}
				}
			}
			if(s == '<select>'){
				s+= '<option value=""></option>';
			}
			if(gridName == 'alarmmessage_info' && selectName == 'group_info'){
			  s = s.replace(/<option value="0"><%=lang.__("0: System management ")%>  <\/option>/,"");
//console.log(s);
			}
			return s + '</select>';
		}

		/**
		 * Jqgridのメッセージを表示する関数。
		 * @param id ページ全体でユニークとなるid
		 * @param message 表示する文言
		 * @param caption メッセージボックスのキャプション
		 * @param posX メッセージボックスのブラウザ左上からのX軸表示位置
		 * @param posY メッセージボックスのブラウザ左上からのY軸表示位置
		 * @param width メッセージボックスの幅(ピクセル)
		 */
		function showMessage(id, message, caption, posX, posY, width){
			var alertIDs = {themodal:'alertmod',modalhead:'alerthd',modalcontent:'alertcnt'};

			//メッセージが作成済みであれば削除する
			if(!(typeof $("#"+alertIDs.themodal).html() === "undefined") || !($("#"+alertIDs.themodal).html() === null)){
				$("#"+alertIDs.themodal).remove();
			}

			//幅
			var messagewidth = 200;
			if(!(typeof width === "undefined")){
				messagewidth = width;
			}

			//表示位置
			var messageleft;
			var messagetop;
			if(!(typeof posX === "undefined") && !(typeof posY === "undefined")){
				//表示位置パラメータがあればセット。
				messageleft = posX;
				messagetop = posY;
			}else{
				//表示位置パラメータがなければ画面中央の表示位置の取得しセット。
				if (window.innerWidth !== undefined) {
					messageleft = window.innerWidth;
					messagetop = window.innerHeight;
				} else if (document.documentElement !== undefined && document.documentElement.clientWidth !== undefined && document.documentElement.clientWidth !== 0) {
					messageleft = document.documentElement.clientWidth;
					messagetop = document.documentElement.clientHeight;
				} else {
					messageleft=1024;
					messagetop=768;
				}
				messageleft = messageleft/2 - parseInt(messagewidth,10)/2;
				messagetop = messagetop/2-25;
			}

			$.jgrid.createModal(
				alertIDs,
				"<div>"+message+"</div><span tabindex='0'><span tabindex='-1' id='jqg_alrt'></span></span>",
				{gbox:"#gbox_"+id,
					jqModal:true,
					drag:true,
					resize:true,
					caption:caption,
					left:messageleft,
					top:messagetop,
					width:messagewidth,
					height:'auto',
					closeOnEscape:true,
					zIndex:999
				},
				"",
				"",
				true
			);

			$.jgrid.viewModal("#"+alertIDs.themodal,{gbox:"#gbox_"+id,jqm:true});
			$("#jqg_alrt").focus();
		}

		/**
		 * フォーム画面処理中メッセージ表示関数。<br>
		 * メッセージとオーバーレイヤーの表示を行う。<br>
		 * 後続、表示したメッセージとオーバーレイヤーを削除もしくは非表示にする処理が必要。
		 * @formDivId 表示対象フォームid
		 * @text 表示文言
		 */
		function showProcessing(formDivId, text){
//			var formProcessing;
			var formOverlay;
//			if($('#processing_'+formDivId).length == 0){
//				$('#'+formDivId).after('<div id="processing_'+formDivId+'" class="loading ui-state-default ui-state-active" style="display: none;"></div>');
//				$('#'+formDivId).after('<div id="form-overlay" class="ui-widget-overlay" style="height: 100%; width: 100%; position: fixed; left: 0px; top: 0px; z-index: 101; opacity: 0.3; display: none;"></div>');
				$('#'+formDivId).after('<div id="processing_'+formDivId+'" class="ui-jqgrid loading" style="display: none;"></div>');
				$('#'+formDivId).after('<div id="form-overlay" class="ui-widget-overlay" style="height: 100%; width: 100%; position: fixed; left: 0px; top: 0px; z-index: 1999; opacity: 0.3; display: none;"></div>');
//			}
			formProcessing = $('#processing_'+formDivId);
//			formProcessing.text(text);
			formOverlay = $('#form-overlay');
//			formProcessing.attr('z-index', 102);
//			var formProcessingTop = parseInt($('#'+formDivId).offset().top);
//			var formProcessingLeft = parseInt($('#'+formDivId).offset().left);
//			formProcessing.css('position', 'absolute');
//			formProcessing.css('top', formProcessingTop);
//			formProcessing.css('left', formProcessingLeft);
//			$(formProcessing).position({
//				my:'left bottom',
//				at:'left bottom',
//				of: '#'+formDivId
//			});

//var formProcessingTop = Math.floor(($(window).height() - $(formProcessing).height()) / 2);
//var formProcessingLeft = Math.floor(($(window).width() - $(formProcessing).width()) / 2);
//$(formProcessing).css({
//	"top": formProcessingTop,
//	"left": formProcessingLeft
//});
			formProcessing.show();
			formOverlay.show();
		 }

		/**
		 * フォームが登録、編集のどちらで開かれているかを返却する関数。<br>
		 * ナビゲーションのボタン押下時に、フォームのdivに#myEditModeで生成保持している。
		 * @return add:登録モード edit:編集モード
		 */
		 function getEditMode(){
			 return $('#myEditMode').html();
		 }

		/**
		 * 現在のコンテンツエリアの横幅と自グリッドの階層番号から自グリッドの横幅を算出して返却する関数。
		 * @level 階層番号
		 * @return グリッドの横幅(width)
		 */
		 function getGridWidth(level){
			var HANDLE_WIDTH = 28;
			//第１階層はコンテンツエリアの横幅*98%
			//第２階層以上コンテンツエリアの横幅*98%-表示されている開閉ハンドルの横幅
			var adjustWidth = ((Number(level)-Number(1)) * Number(HANDLE_WIDTH));
			var contentsWidth = $(window.parent.document.getElementById('mainFrame')).width() * 0.98;
			return contentsWidth - adjustWidth;
		 }

		/**
		 * グリッドデータ中に(編集は選択レコード以外で)指定の値のプロパティを持つレコードが存在するかを返却する関数。<br>
		 * １つでも存在すればtrueを返却する。
		 * @gridId グリッドID
		 * @propName プロパティ名
		 * @propVal プロパティ値
		 * @return true:存在する false:存在しない
		 */
		function isExistsPropertyInGridData(gridId, propName, propVal){
			 var rowid = $("#"+gridId).jqGrid('getGridParam', 'selrow');
			var gridData = $("#"+gridId).jqGrid('getRowData');
			for(var i=0; i<gridData.length; i++){
				if(getEditMode() == 'edit' && gridData[i]['id'] == rowid){
					continue;
				}
				if(gridData[i][propName] && gridData[i][propName] == propVal){
					return true;
				}
			}
			return false;
		}

		function setHeaderCss(head, idx) {
			if (idx == 2) {
				//head.css('background', 'url("../../admin-js/css/ui-lightness/images/ui-bg_gloss-wave_35_f6a828_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
				head.css('background', 'url("../../admin-js/css/redmond/images/ui-bg_gloss-wave_50_6eac2c_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
				//head.css('color', '#222222');
			}
			else if (idx == 3) {
				head.css('background', 'url("../../admin-js/css/redmond/images/ui-bg_gloss-wave_45_e14f1c_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
			}
			else if (idx == 4) {
				head.css('background', 'url("../../admin-js/css/redmond/images/ui-bg_gloss-wave_45_817865_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
			}
			else if (idx == 5) {
				head.css('background', 'url("../../admin-js/css/redmond/images/ui-bg_gloss-wave_60_fece2f_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
				head.css('color', '#222222');
			}
			else if (idx == 6) {
				head.css('background', 'url("../../admin-js/css/redmond/images/ui-bg_gloss-wave_30_44372c_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
			}
		}

		/*function setTableHeaderCss(head, idx) {
			if (idx == 2) {
				head.css('background', 'url("../../admin-js/css/smoothness/images/ui-bg_glass_75_e6e6e6_1x400.png") repeat-x scroll 50% 50% #E6E6E6');
				head.css('border', '1px solid #D3D3D3');
				//head.css('color', '#555555');
				head.find(".ui-state-default").each(function(){
					$(this).css('background', 'url("../../admin-js/css/smoothness/images/ui-bg_glass_75_e6e6e6_1x400.png") repeat-x scroll 50% 50% #E6E6E6');
					$(this).css('border', '1px solid #D3D3D3');
					//$(this).css('color', '#555555');
				});
			}
		}

		function setTableBodyCss(body, idx) {
			if (idx == 2) {
				body.find(".ui-widget-content").each(function(){
					$(this).css('border', '1px solid #AAAAAA');
				});
			}
		}

		function setFooterCss(foot, idx) {
			if (idx == 2) {
				foot.css('background', 'url("../../admin-js/css/start/images/ui-bg_gloss-wave_50_6eac2c_500x100.png") repeat-x scroll 50% 50% #5C9CCC');
				foot.css('color', '#555555');
			}
		}*/

	/**
	 * 属性IDのプルダウンを作成する関数。
	 */
	// objid 引数（例　'#tablemasterinfoid'）を追加してテーブルマスターIDを別名としても対応できるようにする
	function makeAttrSelect(tabId, tabName, attrcols, objid) {
		var tablemasterid = $(objid).val();
		//var url = '${f:url("../'+convertTabNameToMethodMame(tabName)+'/createSelectTag/tablemasterinfoid/_attr/attrId/name?tablemasterId=' + tablemasterid + '")}';
		var url = convertTabNameToMethodMame(tabName)+'/createSelectTag/tablemasterinfoid/_attr/attrId/name?tablemasterId=' + tablemasterid;
		$.ajax({
			dataType: "json",
			type: "GET",
			cache: false,
			url: url,
			success: function(data) {
				var selectTag = data.selectTag;
				var rowid = $('#'+tabId).jqGrid('getGridParam', 'selrow');
				var rowdata = $('#'+tabId).jqGrid('getRowData', rowid);
				$.each(attrcols, function(j, attrcol) {
					//全項目削除
					$("#"+attrcol.name).children().remove();
					selectedId = rowdata[attrcol.name] || '';
					//項目追加
					for (var i = 0; i < selectTag.length; i++) {
						var selected = (selectTag[i].key == selectedId) ? 'selected=" selected"' : '';
						$('#'+attrcol.name).append('<option value="'+ selectTag[i].key + '"' + selected + '>' + selectTag[i].value + '</option>');
					}
					//空の項目を１個追加
					if (selectTag.length <= 0 || !attrcol.required)
						$('#'+attrcol.name).append('<option value=""></option>');
				});
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("error : " + errorThrown);
			}
		});
	}

	// 公共情報コモンズ避難勧告情報・避難所情報の追加・編集ダイアログの属性名テキストエリアに
	// テーブルマスタIDに紐づくレイヤの属性IDをセットする
	function loadPubliccommonsDialogTextInputColumn(tabName) {
		var tablemasterinfoid = $('#tablemasterinfoid').val();
		//var url = '${f:url("../'+convertTabNameToMethodMame(tabName)+ '/loadDialogTextInputColumnValue")}';
		var url = convertTabNameToMethodMame(tabName)+ '/loadDialogTextInputColumnValue';
		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: url,
			data:{tablemasterinfoid : tablemasterinfoid},
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			success: function(data) {
				console.log(tabName);
				console.log(data);
				for(var i = 0; i < data.columns.length ; i++){
					$('#'+ data.columns[i].key).val(data.columns[i].value);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert("error : " + errorThrown);
			}
		});
	}
	</script>

<title><%=lang.__("System management : NIED disaster information sharing system")%></title>
