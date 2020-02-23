<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
	<link type="text/css" media="screen" href="${f:url('/admin-js/css/ui.jqgrid.css')}" rel="stylesheet" />
	<link type="text/css" media="screen" href="${f:url('/css/style_adminjqgrid.css')}" rel="stylesheet" />
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery.jqGrid.min.js')}" ></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/i18n/grid.locale-ja.js')}" ></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/utils.js')}" ></script>
	<script type="text/javascript" src="${f:url('/js/saigaitask.filtering.js')}" ></script>

	<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>
	
	<div id="map"></div>
<c:if test="${pageDto.mapVisible == '1'}">
	<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
	<script type="text/javascript" src="${f:url('/js/jquery-ui-timepicker-addon.js')}"></script>
	<c:if test='${lang.getLangCode().equals("ja")}'>
	<script type="text/javascript" src="${f:url('/js/jquery-ui-timepicker-ja.js')}"></script>
	</c:if>
	<script type="text/javascript">
		var initMapPage = false;
		SaigaiTask.Layout.body.on("oninitcontentlayout", function() {
			if(!initMapPage) {
				// 地図ページのJavaScriptを初期化
				initMapPage = true;
				SaigaiTask.Page.Map({
					ecommapInfoOption: ${empty mapDto.ecommapInfoJSON ? "null" : mapDto.ecommapInfoJSON},
					updateTimeFormat: "${pageDto.updateTimeFormat}",
					filterlayer: "${f:h(mapDto.filterlayer)}",
					coordinateDecimal: ${f:h(mapDto.coordinateDecimal)},
					geocoder: "${loginDataDto.geocoder}"
				});
			}
		});


		$(function() {
			//for 一括変更
			slimerDto = JSON.parse('${fal:json(slimerDto)}');
			// 一括変更の初期化
			SaigaiTask.Edit.slimers = [];
			if(!!slimerDto.slimerInfos) {
				var first = true;
				// TODO: 編集レイヤが複数ある場合の一括変更対応
				window.editableLayer = slimerDto.slimerInfos.length;
				for(var idx in slimerDto.slimerInfos) {
					var slimerInfo = slimerDto.slimerInfos[idx];
					slimer = new SaigaiTask.Edit.Slimer(slimerInfo);
					slimer.dialog.bind("slimersuccess", function(evt) {
						// 地図のリロード
						map.reload();
					});
					if(first) {
						first = false;
						SaigaiTask.Edit.slimer = slimer;
						SaigaiTask.Edit.slimer.dialog.attr("id", "slimer-dialog");
					}
					SaigaiTask.Edit.slimers.push(slimer);
				}
			}
			// 一括変更ボタンのハンドラを上書き
			var defaultSlimerDialog = window.slimerDialog;
			window.slimerDialog = function() {
				var selectFeatureControl = map.controls.selectFeatureControl;
				if(selectFeatureControl.window!=null && !selectFeatureControl.window.window.isHidden()) {
					selectFeatureControl.onClickSlimerButton();
				}
				else {
					if(1<SaigaiTask.Edit.slimers.length) {
						// TODO: 編集可能なレイヤが複数ある場合
						$("<div><%=lang.__("Since two or more editable layers are set,<br>bulk change function not in service.")%><br></div>").dialog({
							title: "<%=lang.__("Bulk change")%>",
							minWidth: 400,
							maxHeight: 500,
							modal: true
						});
					}
					else {
						defaultSlimerDialog();
					}
				}
			};

			// フィルタリング初期化
			SaigaiTask.Page.Filter.size = ${fn:length(pageDto.menuInfo.filterInfoList)};
			SaigaiTask.Page.Filter.nofilter  = ${filterDto==null && 0<fn:length(pageDto.menuInfo.filterInfoList)};
			SaigaiTask.Page.Filter.nofilter |= ${filterDto!=null && filterDto.filterInfoId==0};
			SaigaiTask.Page.Filter.nofilter |= ${filterDto!=null && filterDto.conditionValue.has("nofilter") && filterDto.conditionValue.get("nofilter")};
			SaigaiTask.Page.filter = new SaigaiTask.Page.Filter();
			// 検索イベントハンドラを定義
			SaigaiTask.Page.filter.events.on("search", function(event, filterInfo, searchResult){
				try {
					var conditionValueActual = searchResult.conditionValueActual;
					var ecommap = map.ecommaps[0];
					var layerInfo = ecommap.layerInfoStore[conditionValueActual.layerId];
					layerInfo.spatialLayers = !!conditionValueActual.spatiallayer ? conditionValueActual.spatiallayer : null;
					layerInfo.params.spatiallayers = !!layerInfo.spatialLayers ? JSON.stringify(layerInfo.spatialLayers) : "[]";
					// 空間検索条件があれば範囲表示チェックボックスを表示させる
					if(0<filterInfo.id) {
						$("#filter-gray-ck,label[for=filter-gray-ck]").show();
						if(!!layerInfo.spatialLayers) {
							$("#filter-spatial-ck,label[for=filter-spatial-ck]").show();
						}
					}

					// フィルター解除の場合
					var nofilter = !!conditionValueActual.nofilter ? conditionValueActual.nofilter : false;
					if(nofilter) {
						$("#filter-gray-ck,label[for=filter-gray-ck]").hide();
						$("#filter-spatial-ck,label[for=filter-spatial-ck]").hide();
					}

					// 一括変更対応
					if(!!SaigaiTask.Edit.slimers) {
						// リロード時に前回の一括変更ダイアログがあった場合はtargetIdsを更新する
						for(var idx in SaigaiTask.Edit.slimers) {
							var slimer = SaigaiTask.Edit.slimers[idx];
							slimer.targetIds = searchResult.filteredFeatureIds;
						}
					}
					
					// 地図のリロード
					map.redrawContentsLayer(0);
				} catch(e) {
					alert("<%=lang.__("Failed to reload filter conditions.")%>")
					console.error(e);
				}
			});
			// フィルタ件数の更新
			var total = "${filterDto.total}";
			var filteredNum = "${fn:length(filterDto.filteredFeatureIds)}";
			if(SaigaiTask.Page.Filter.nofilter) {
				SaigaiTask.Page.filter.getFilterNameEl().text("<%=lang.__("解除")%>");
				SaigaiTask.Page.filter.getFilterNumEl().text(" [ "+addFigure(total)+"<%=lang.__("Items")%> ]");
				$("#filter-gray-ck,label[for=filter-gray-ck]").hide();
			}
			else {
				$("#filter-gray-ck,label[for=filter-gray-ck]").show();
				if($.isNumeric(total) && $.isNumeric(filteredNum)) {
					var sum = parseInt(total);
					var filterNum = sum - parseInt(filteredNum);
					SaigaiTask.Page.filter.getFilterNumEl().text(MessageFormat.format(" <%=lang.__("({0} items / {1} items)")%>", sum-filterNum, sum));
				}
			}

			//グレー表示On/Offチェックボックスの表示/非表示
			//alert($("#filter-gray-ck-div").css("display"));
			var filterIds = "${f:h(mapDto.filterlayer)}";
			//alert(filterIds);
			// フィルター解除の場合でもフィルターを表示させる
			//var showFilterDiv = $("#filter-gray-ck") && filterIds.length>0;
			var showFilterDiv = $("#filter-gray-ck") && 0<SaigaiTask.Page.Filter.size;
			if(showFilterDiv){
				$("#filter-gray-ck-div").css("display","block");
			}else{
				$("#filter-gray-ck-div").css("display","none");
			}

			//情報の登録ボタン表示のOn/Off
			var addable = "${addable}" == "true";
			if(!addable){
				disableButton("#add-button",BUTTON_MODE_ONCLICK);
			}

			//一括変更ボタン表示のOn/Off
			var slimer = "${slimer}" == "true";
			if(!slimer){
				disableButton("#slimer-button",BUTTON_MODE_CLICK);
			}

			// 意思決定支援モードなら推定避難者数に応じて矢印表示を行う
			if(SaigaiTask.PageURL.initParams.decisionsupport){
				SaigaiTask.Page.decisionSupport = new SaigaiTask.DecisionSupport("map");
			}
		});

		<%-- 17/05/17の依頼により、災害未起動時はクリアリングハウス更新通知をOFFにする --%>
		<c:if test="${0<loginDataDto.trackdataid}">
		// <%-- 最近追加・更新されたメタデータを定期的に検索 --%>
		SaigaiTask.Map.initCheckNewMetadata(${pageDto.metadataInterval},
			${fal:json(excludeMetadataIds)} // <%-- 除外するメタデータIDリスト --%>
		);
		</c:if>

	</script>

	<div id="newmetadatalist" style="display: none;"></div><%-- あたらしいメタデータを表示するダイアログ --%>

	<%-- CSVファイル出力、地図ページからは実行しないが、ボタンは表示されてしまうのでとりあえず対応 --%>
	<div id="csv-dialog" title="<%=lang.__("CSV file output")%>" style="display:none;">
		<div>
			<%=lang.__("Execute to output CSV file on list page.")%>
		</div>
	</div>
</c:if>
<c:if test="${pageDto.mapVisible == '0'}">
	<span><%=lang.__("Map not displayed due to no map-display mode. Click \"map display\" on the menu to show map.")%></span>
</c:if>

