<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<style type="text/css">
.measure_div{
	display:none;
	padding:2px;
}
.measure_div input{
	width:120px;
	color:black;
}
</style>

<div class="widget_inner_box" id=buttons>
	<input type="radio" name="measure_type" id="measure_type_dist" /><label for="measure_type_dist"><%=lang.__("Distance")%></label>
	<input type="radio" name="measure_type" id="measure_type_area" /><label for="measure_type_area"><%=lang.__("Area")%></label>
	<input type="radio" name="measure_type" id="measure_type_people" /><label for="measure_type_people"><%=lang.__("Population")%></label>
</div>

<div id="measure_dist_span" class="measure_div"><%=lang.__("Distance")%>：<input type="text" id="measure_dist" disabled />
	<span id="measure_dist_units">m</span></div>
<div id="measure_area_span" class="measure_div"><%=lang.__("Area")%>：<input type="text" id="measure_area" disabled />
	<span id="measure_area_units">m</span><sup>2</sup></div>
<div id="measure_outer_span" class="measure_div"><%=lang.__("Circumference")%>：<input type="text" id="measure_outer" disabled />
	<span id="measure_outer_units">m</span><sup>2</sup></div>
<div id="measure_people_span" class="measure_div" style="">
	<%=lang.__("Population<!--2-->")%><%=lang.__("(Estimate)")%>：<input type="text" id="measure_people" disabled /><%=lang.__("People")%><br />
	<%=lang.__("Households")%><%=lang.__("(Estimate)")%>：<input type="text" id="measure_house" disabled />
</div>
