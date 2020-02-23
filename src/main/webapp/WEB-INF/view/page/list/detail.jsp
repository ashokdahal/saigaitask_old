<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<link rel="stylesheet" href="${f:url('/css/detail2.css')}"  type="text/css" media="screen" title="default" />
<style type="text/css">
<!--
#label_green{
  background-image: none;
  background-color: lime;
  color: black;
}
//-->
</style>
<%-- <script type="text/javascript" src="${f:url('/js/saigaitask.detail.js')}"></script> --%>
<script type="text/javascript">
<c:set var="gid" value=""/>
<c:set var="e" value="${result[0]}"/>
<c:if test="${e == null}"><c:set var="gid" value="0"/></c:if>
<c:if test="${e != null}"><c:set var="gid" value="${e[key]}"/></c:if>

$("body").on({
	"oneditgeom": function(e, wkt) {
		$('#map-dialog').dialog('close')
		$('<input>').attr({
			type: 'hidden',
			id: 'the_geom',
			name: '${table}:theGeom:${key}:${gid}',
			value: wkt
		}).appendTo(document.detailform);
	}
})

$(function() {
	$("#detailTable").tablesorter({
		/* widgets: ['zebra'], */
		headers: {
			0:{sorter:false},
			1:{sorter:false},
		}
	});
	//$('input[type="submit"]').button();
	//$('input[type="button"]').button();
	$('.closebtn').button();
	$('.label_green').button();
	//$('.relationattrlist').button();

	$('.datepicker').datepicker({changeYear:true,yearRange:'c-100:c+10'});
	$('.datetimepicker').datetimepicker({controlType: 'select'});

	var myButtons = {
		<c:if test="${deletable && gid != 0}">
			"<%=lang.__("Delete")%>": function() {
				SaigaiTask.Detail.deleteData('${table}:delete:${key}:${e[key]}', '${e[key]}', '${cookie.JSESSIONID.value}');
			},
		</c:if>
		<c:if test="${editable}">
			"<%=lang.__("Save")%>": function () {
				if (!confirm("<%=lang.__("Are you sure to save?<!--2-->")%>")) return;
				SaigaiTask.Detail.saveDetailData({
					menuid: ${listForm.menuid},
					form: document.detailform,
					token: '${cookie.JSESSIONID.value}',
					dialog: $('#detail-dialog')
				});
			},
		</c:if>
		<c:if test="${key=='gid' || key=='_orgid'}">
		"<%=lang.__("Map display")%>": function () {
			$("#the_geom").remove();
			// 登録フォームの有無
			var insertFeature = '';
			// 住所
			var address = '';
			// 中心位置
			var center = '';
			// ポップアップ
			var popup = '';
			// wkt
			var wkt = '';
			var layerId = '';
			// 編集可であれば登録フォームを表示
			<c:if test="${editable}">
				insertFeature = '&insertFeature=true';
			</c:if>
			// 編集可でなければポップアップを表示
			<c:if test="${!editable}">
				if(typeof document.detailform == "undefined" || !(!!document.detailform) || document.detailform.gid != 0)
					insertFeature = '&insertFeature=true';
				else popup = '&popup=${table}.'+document.detailform.gid.value;
			</c:if>
			<c:if test="${e.theGeom != null}">
				var theGeom = "${e.theGeom}";
				wkt = "&drawGeometrywkt=${e.wkt}";
				center = '&center='+theGeom.slice(theGeom.indexOf("(")+1,theGeom.indexOf(")"));
			</c:if>
			// 住所項目が登録されている場合addressに住所を格納
			<c:if test="${!empty master.addresscolumn}">
				var readaddress = $("[name*=':${master.addresscolumn}:']");
				if(readaddress.length == 1){
					address = '&address=' + readaddress[0].value;
				}
			</c:if>
			layerId = '&layerId=${table}';
			//var url = 'map/content/?menuid=${menuid}&menutaskid=${menutaskid}&legendCollapsed=true&drawGeometry=true&drawGeometryfid=${gid}'+insertFeature+center+wkt+popup+address+layerId;
			var url = '${f:url('/page/map/content/')}?menuid=${listForm.menuid}&menutaskid=${listForm.menutaskid}&legendCollapsed=true&drawGeometry=true&drawGeometryfid=${gid}'+insertFeature+center+wkt+popup+address+layerId;
			// 時間パラメータの付与
			var time = SaigaiTask.PageURL.getTime();
			if(!!time) {
				var iso8601Time = time.toISOString();
				// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
				if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
					iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
				}
				url += "&time="+iso8601Time;
			}
			SaigaiTask.Detail.showMap(url);
		},
		</c:if>
		"<%=lang.__("Close")%>": function () {
			SaigaiTask.Detail.close();
		}
	};
	$('#detail-dialog').dialog('option', 'buttons', myButtons);

	SaigaiTask.Detail.createAttrListDialog();

	$('.Upload').change(function(e) {
		var td = $(e.target).parent();
		var up = $(e.target);
		SaigaiTask.Detail.storefile(this.id, up);
	});
});

function setDefaultData(bid) {
	<c:set var="r" value="${result[0]}"/>
	<c:forEach var="e" varStatus="s" items="${menueditbuttonInfoItems}">
	if (bid == ${e.id}) {
		<c:forEach var="f" varStatus="t" items="${e.menueditbuttoncolumnInfoList}">
			<c:choose>
			<c:when test="${f.defaultvalue=='now'}">
				<jsp:useBean id="now" class="java.util.Date" />
				$('#${table}_${f.attrid}_${key}_${r[key]}').val('<fmt:formatDate value="${now}" pattern="yyyy/MM/dd HH:mm"/>');
			</c:when>
			<c:otherwise>
				$('#${table}_${f.attrid}_${key}_${r[key]}').val('${f:h(f.defaultvalue)}');
			</c:otherwise>
			</c:choose>
		</c:forEach>
	}
	</c:forEach>
}
</script>

<div>

<form name="${gid == 0 ? 'addlineform' : 'detailform' }">

	<div data-role="content">

<c:forEach var="e" varStatus="s" items="${menueditbuttonInfoItems}">
	<input type="button" name="save" id="label_green" class="label_green" value="${f:h(e.name)}" onClick="setDefaultData(${e.id})" />
</c:forEach>

<table border="0" cellpadding="3" cellspacing="2" id="detailTable" class="tablesorter" style="margin:0px;width:100%;">
	<thead>
		<tr>
			<th><%=lang.__("Item name")%></th>
			<th><%=lang.__("Value")%></th>
		</tr>
	</thead>

	<tbody>
<c:set var="gid" value=""/>
	<c:set var="e" value="${result[0]}"/>
	<c:if test="${e == null}"><c:set var="gid" value="0"/></c:if>
	<c:if test="${e != null}"><c:set var="gid" value="${e[key]}"/></c:if>

	<%-- 後で詳細と新規登録の処理をまとめる。--%>
	<%-- 詳細 --%>
	<c:forEach var="f" varStatus="t" items="${kartecolinfoItems}">
		<c:set var="high" value="" />
		<c:if test="${f.highlight}"><c:set var="high" value="class='highlight'" /></c:if>
		<c:if test="${f.attrid == 'group'}">
			<c:set var="group" value="group-${t.index}" />
			<tr id="${group}">
				<td colspan="2" style="background-color:rgb(238, 236, 225)" ${high} >${f:h(f.name)}</td>
			</tr>
		</c:if>
		<c:if test="${f.attrid != 'group'}">
			<tr id=tr_${f.attrid}>
			<%-- データ変換 --%>
			<c:set var="colVal" value="${e[f.attrid]}"/>
			<c:set var="req" value="" />
			<c:if test="${colMap[f.attrid]}"><c:set var="req" value="Required" /></c:if>

			<%-- デフォルト値 --%>
			<c:if test="${gid == 0}">
				<c:set var="defval" />
				<c:if test="${tableattrInfoMap[f.attrid] != null}">
					<c:set var="defval" value="${tableattrInfoMap[f.attrid].defaultvalue}"/>
				</c:if>
				<c:choose>
				<c:when test="${defval == null || defval == ''}">
					<c:set var="colVal" value=""/>
				</c:when>
				<c:when test="${defval=='serial' || defval=='address' || defval=='reporter' || defval=='reportaddress' || defval=='reporttel'}">
					<c:set var="colVal" value=""/>
				</c:when>
				<c:when test="${defval == 'now'}">
					<jsp:useBean id="today2" class="java.util.Date" />
					<fmt:formatDate var="colVal" value="${today}" pattern="yyyy/MM/dd HH:mm" />
				</c:when>
				<c:otherwise>
					<c:set var="colVal" value="${defval}" />
				</c:otherwise>
			</c:choose>
			</c:if>

			<c:choose>
				<c:when test="${(editable && f.editable) || gid == 0}"><!-- edit -->
				    <td ${high}>${f:h(f.name)}${colMap[f.attrid]?'※':':'}</td>
				    <td ${high} style="text-align:left">
				    <c:if test="${editClass[t.index] == 'String' || editClass[t.index] == 'Number' || editClass[t.index] == 'Float'}">
				    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}" id="${table}_${f.attrid}_${key}_${gid}" value="${f:h(colVal)}" class="${editClass[t.index]} ${req} a ${defval}" />
				    </c:if>
				    <c:if test="${editClass[t.index] == 'Date'}">
				    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}" id="${table}_${f.attrid}_${key}_${gid}" value="${f:h(colVal)}" class="datepicker Date ${req}" />
				    </c:if>
				    <c:if test="${editClass[t.index] == 'DateTime'}">
				    	<input type="text" name="${table}:${f.attrid}:${key}:${gid}" id="${table}_${f.attrid}_${key}_${gid}" value="${f:h(colVal)}" class="datetimepicker DateTime ${req}" />
				    </c:if>
				    <c:if test="${editClass[t.index] == 'TextArea'}">
						<textarea cols="30" rows="5" name="${table}:${f.attrid}:${key}:${gid}" id="${table}_${f.attrid}_${key}_${gid}" class=" ${req}">${f:h(colVal)}</textarea>
				    </c:if>
				    <c:if test="${editClass[t.index] == 'Select'}">
						<select name="${table}:${f.attrid}:${key}:${gid}" id="${table}_${f.attrid}_${key}_${gid}" onChange="onSelStyleChange(${f.id}, this, '#${f.attrid}:${gid}')">
						<c:if test="${!colMap[f.attrid] && f.editable && selectVal[f.attrid][0]!=''}"><option value=""></option></c:if>
						<c:forEach var="h" varStatus="u" items="${selectStr[f.attrid]}">
							<c:set var="h2" value="${selectVal[f.attrid]}" />
							<c:if test="${h2[u.index] != colVal}">
								<option value="${f:h(h2[u.index])}">${f:h(h)}</option>
							</c:if>
							<c:if test="${h2[u.index] == colVal}">
								<option value="${f:h(h2[u.index])}" selected>${f:h(h)}</option>
							</c:if>
						</c:forEach>
						</select>
				    </c:if>
					<c:if test="${editClass[t.index] == 'Checkbox'}">
						<input type="checkbox" name="${table}:${f.attrid}:${key}:${gid}" value="${f:h(checkStr[f.attrid])}" ${(checkStr[f.attrid]==colVal)?"checked":""} onClick="onCheckStyleChange(${f.id}, this, '#${f.attrid}:${gid}')">
					</c:if>
					<c:if test="${editClass[t.index] == 'Upload' || editClass[t.index] == 'Link'}">
						<div class="uploadButton">
						    <%=lang.__("Choose file")%>
						<input type="file" class="Upload" name="${table}:${f.attrid}:${key}:${gid}" id="${table}_${f.attrid}_${key}_${gid}" size="10" onchange="uv.style.display='inline-block'; uv.value = this.value;" /><br>
					    <input type="text" id="uv" class="uploadValue" disabled />
						</div>
						<c:if test="${fn:length(colVal) > 0}">
							<a href="${f:url(colVal)}" target="_blank">${f:h(colVal)}</a>
						</c:if>
					</c:if>
					<c:if test="${f.tips !=null}">
				    	</br><p style="text-align:left"><font size="1">${f:h(f.tips)}</font></p>
				    </c:if>
					</td>

				</c:when>
				<c:otherwise><!-- not edit -->

				<td>${f:h(f.name)}:</td>
			    <td id="td_${f.attrid}" style="text-align:left">
			    	<c:choose>
			    	<c:when test="${editClass[t.index] == 'Link'}">
						<c:if test="${fn:length(colVal) > 0}">
							<a href="${f:url(colVal)}" target="_blank" style="color:blue;">${f:h(colVal)}</a>
						</c:if>
			    	</c:when>
			    	<c:otherwise>${f:h(colVal)}</c:otherwise>
			    	</c:choose>
					<c:if test="${f.tips !=null}">
				    	</br><p style="text-align:left"><font size="1">${f:h(f.tips)}</font></p>
				    </c:if>
				</td>

				</c:otherwise>
			</c:choose>
			</tr>
		</c:if>
	</c:forEach>

		<c:if test="${(key == 'gid' || key == '_orgid') && (editable||(addable && gid==0)) }">
		<tr>
			<td><%=lang.__("Picture")%></td>
			<td>
				<input type="file" name="formFile" onchange="SaigaiTask.Detail.selectPhoto(this)" /><br>
				<img id="preview" style="max-width:150px;max-height:150px;"/>
			</td>
		</tr>
		</c:if>

		<c:if test="${gid != 0}">
			<td colspan="2" style="background-color:rgb(238, 236, 225)" id='photolist'><%=lang.__("photo list")%></td>
			<tr>
				<td colspan="2" style="max-height:100px; max-width:400px">
					<div id="photoarea"></div>
				</td>
			</tr>
		</c:if>
	</tbody>
</table>
	<div style="text-align:right;">
	</div>

	</div><!-- /content -->
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<input type="hidden" id="gid" name="gid" value="${gid}">
	<%-- <input type="hidden" id="the_geom" name="${table}:theGeom:${key}:${gid}" value="" /> --%>
	<input type="hidden" name="dataList" value="">
	<input type="hidden" id="wkt" name="wkt" value="${result[0].theGeom}">

<script type="text/javascript">
$(function() {
	if(${gid} != 0) SaigaiTask.Detail.loadPhoto('${table}', ${gid}, ${editable});
	$('#photolist').click(function(){
		$('#photoarea').slideToggle("normal");
	});
});

$(function(){
	<c:set var="group"  value=""/>
	<c:forEach var="f" varStatus="status" items="${kartecolinfoItems}">
		<c:if test="${f.attrid == 'group'}">
			<c:set var="group" value="group-${status.index}" />
			<c:set var="closed" value="${f.closed}" />
		</c:if>
			<c:if test="${f.attrid != 'group'}">
				<c:if test="${group != null}">
					$('#${group}').click(function(){
						$('#tr_${f.attrid}').slideToggle("normal");
					});
				</c:if>
				<c:if test="${closed == true}">
					$("#tr_${f.attrid}").css("display","none");
				</c:if>
			</c:if>
	</c:forEach>
})

</script>
</form>

<div id="report-dialog" title="<%=lang.__("File format output")%>" style="display:none;">
	<div align="center">
		<form name="outputreportform">
			<input type="hidden" name="value" value="" >
			<input type="hidden" name="dataList" value="">
			<input type="hidden" name="menuid" value="${listForm.menuid}">
			<input type="hidden" name="menutaskid" value="${listForm.menutaskid}">
		</form>
	</div>
</div>

<div id="attrlist-dialog" title="<%=lang.__("Display list")%>" style="display:none;">
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<div style="float: right;">
	</div>
</div>

</div><!-- /page -->

