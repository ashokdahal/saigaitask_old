<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
		<c:if test="${0<fn:length(listDto.columnNames) }">
		<table border="1" cellpadding="3" cellspacing="2" id="${listDto.styleId}" class="tablesorter shortline" style="margin-left:10px;">
			<c:if test="${!empty listDto.title }">
			<caption style="font-weight: bold; font-size: large;">${listDto.title}</caption>
			</c:if>
			<thead>
				<tr id="column-header">
					<c:if test="${listDto.totalable}"><th class=""></th></c:if>
					<c:forEach varStatus="s" items="${listDto.columnNames}">
					<th id="column-${s.index}">${s.current}</th>
					</c:forEach>
				</tr>
				<c:if test="${listDto.totalable}">
				<tr id="total-header">
					<td class="sum"><%=lang.__("Total<!--2-->")%></td>
					<c:forEach var="name" varStatus="s" items="${listDto.columnNames}">
					<td class="sum" headers="column-${s.index}">${listDto.sumItems[name]}</td>
					</c:forEach>
				</tr>
				</c:if>
			</thead>
			<tbody>
				<c:forEach varStatus="s" items="${listDto.columnValues}">
				<tr>
					<c:if test="${listDto.totalable}"><td class=""></td></c:if>
					<c:forEach varStatus="s2" items="${s.current}">
					<c:set var="st2" value="text-align:${listDto.typeItems[listDto.columnNames[s2.index]]=='number'?pageDto.number_align:pageDto.text_align}" />
					<td class="showtip" headers="column-${s2.index}" style="${st2}; ${s2.current==lang.__('Not yet transformation')?'background-Color:yellow;':''}">${listDto.typeItems[name]}<c:if test="${s2.current!='null'}">${s2.current}</c:if></td>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<script type="text/javascript">
		$(function() {
		    $("#${listDto.styleId}").tablesorter({
		    	//widgets: ['zebra'],
				headers: {
		//<c:forEach var="e" varStatus="s" items="${listDto.columnNames}">
		//				${s.index}:{sorter:false},
		//</c:forEach>
				},
				<c:if test="${!empty listDto.defsort}">
				sortList: [[${f:h(listDto.defsort)}]]
				</c:if>
		    }).bind("sortEnd", function(sorter) {
				currentSort = sorter.target.config.sortList;
				SaigaiTask.PageURL.override({
					sort: currentSort.join(",")
				});
			});
		
		    // テーブルの各セルに対して、パディング設定
			$("#${listDto.styleId} td, th").css("padding", "0 10px");
			// テーブルヘッダの背景色をグレーに変更
			$("#${listDto.styleId} thead tr").css("background-color", "#ddd");
		    
			//ボタンのスタイル設定
			$(".dialog-button").button();
		});
		</script>
		</c:if>
