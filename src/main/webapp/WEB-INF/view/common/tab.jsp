<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<div id="tab_header">

<style TYPE="text/css">
<!--
#tabL1 {
	height : 24px;
}
#tabL2 {
	height : 26px;
	background-color: #4F81BD;
}
#tab li {
	color: #666666;
	float: left;
	padding: 5px 5px 1px 5px;
	margin: 0 2px 0 2px;
	list-style: none;
	cursor: pointer;
	background-color: #eeeeee;
	border-radius: 10px 10px 0 0;
}
#tab li.select {
	color: #FFFFFF;
	background-color: #4F81BD;
	font-weight: bold;
}
#tab li:hover{
	/*border: 1px solid #999999;*//*{borderColorHover}*/
	background: #dadada/*{bgColorHover}*/ url(../js/jquery-ui-1.10.3/themes/base/images/ui-bg_glass_75_dadada_1x400.png)/*{bgImgUrlHover}*/ 50%/*{bgHoverXPos}*/ 50%/*{bgHoverYPos}*/ ;
	font-weight: normal/*{fwDefault}*/;
	/*color: #212121;*//*{fcHover}*/
	color: #666666;
}
.tabL2 li {
	color: #666666;
	float: left;
	padding: 5px 5px 1px 5px;
	margin: 2px 2px 0 2px;
	list-style: none;
	cursor: pointer;
	background-color: #eeeeee;
	border-radius: 10px 10px 0 0;
}
.tabL2 li.select {
	background-color: #FFFF00;
	font-weight: bold;
}
.tabL2 li:hover {
	/*border: 1px solid #999999;*//*{borderColorHover}*/
	background: #dadada/*{bgColorHover}*/ url(../js/jquery-ui-1.10.3/themes/base/images/ui-bg_glass_75_dadada_1x400.png)/*{bgImgUrlHover}*/ 50%/*{bgHoverXPos}*/ 50%/*{bgHoverYPos}*/ ;
	font-weight: normal/*{fwDefault}*/;
	/*color: #212121;*//*{fcHover}*/
}
.tabL2 li.select a{
	color: #000000;
	font-weight: bold;
}
.tabL2 li.select a:hover{
	color: #666666;
	font-weight: normal;
}
.disnon {
	display: none;
}
.content_wrap {
	clear: left;
	background-color: #cccccc;
	color: #666666;
}
a{
	color: #666666;
	text-decoration: none;
}

-->
</style>

<script type="text/javascript">
$(function() {

	//タブ1段目のクリック
	$("ul#tab li").click(function() {

		//タブ1段目の選択状態を変更する
		var num = $("ul#tab li").index(this);
		//alert(num);
		$(".content_wrap").addClass('disnon');
		$(".content_wrap").eq(num).removeClass('disnon');
		$("ul#tab li").removeClass('select');
		$(this).addClass('select');

		//タブ2段目の先頭のタブをクリックした状態にする
		var tab1Id = $(this).attr("id");
		//alert("#"+tab1Id+"_0");
		$("#"+tab1Id+"_0").click();

	});

});

//タブ2段目のクリック
function tab2Click(menutaskInfoId){
	location.href='${f:url("/page/")}?menutaskid='+menutaskInfoId;
}

</script>
<c:set var="isDisaster" value="${loginDataDto.usual==false}"/>
<c:set var="isTraining" value="${pageDto.trackData!=null && pageDto.trackData.trainingplandataid!=null && pageDto.trackData.trainingplandataid > 0}"/>
<c:set var="tabColor"   value="${isTraining ? 'background-color:#00c000;' : isDisaster&&0<loginDataDto.trackdataid ? 'background-color:#ff8000;' : ''}"/>

	<!-- タブ1段目 -->
	<div id=tabL1>
		<ul id="tab">
			<c:forEach var="menuprocessInfo" varStatus="s" items="${pageDto.menuprocessInfos}">
				<c:if test="${menuprocessInfo.visible}">
					<c:set var="isSelected" value="${pageDto.menuprocessInfo.name==menuprocessInfo.name}"/>
					<li class="${isSelected?'select':''}" style="${isSelected?tabColor:''}" id="${s.index}"><c:if test="${menuprocessInfo.important }"><font color='orange'>★</font></c:if>${f:h(menuprocessInfo.name)}</li>
				</c:if>
			</c:forEach>
		</ul>
	</div>

	<!-- タブ2段目 -->
	<div id=tabL2 style="${tabColor}">
		<c:forEach var="menuprocessInfo" varStatus="s" items="${pageDto.menuprocessInfos}">
			<c:if test="${menuprocessInfo.visible}">
				<div class="content_wrap<c:if test="${pageDto.menuprocessInfo.name ne menuprocessInfo.name}"> disnon</c:if>">
					<ul id="tabL2_${s.index}" class="tabL2">
						<c:set var="tab2Idx" value="0"/>
						<c:forEach var="menutaskInfo" varStatus="s2" items="${menuprocessInfo.menutaskInfos}">
							<c:if test="${menutaskInfo.visible}">
								<li class="<c:if test="${menutaskInfo.id==pageDto.menutaskInfo.id}">select</c:if>" id="${s.index}_${tab2Idx}" onclick="tab2Click(${menutaskInfo.id});">
									<c:if test="${menutaskInfo.important }"><font color='orange'>★</font></c:if>${f:h(menutaskInfo.name)}
								</li>
								<c:set var="tab2Idx2" value="${tab2Idx+1}"/>
								<c:set var="tab2Idx" value="${tab2Idx2}"/>
							</c:if>
						</c:forEach>
					</ul>
				</div>
			</c:if>
		</c:forEach>
	</div>

</div>

