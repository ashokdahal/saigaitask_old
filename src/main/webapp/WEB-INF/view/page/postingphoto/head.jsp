<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>

<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery-ui-timepicker-addon.css')}" />
<style type="text/css">
<!--
.tablesorter TD.sum {
	background-color: #1E4B78;
	color: white;
	text-align: center;
}
.ui-datepicker .ui-datepicker-buttonpane button.ui-datepicker-current {
	opacity: 1;
}
//-->
</style>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-timepicker-addon.js')}"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script type="text/javascript" src="${f:url('/js/jquery-ui-timepicker-ja.js')}"></script>
</c:if>
<script type="text/javascript" src="${f:url('/js/jquery.upload-1.0.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.list.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.filter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.timeslider.js')}"></script>
