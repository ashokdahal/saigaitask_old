<%	/* Copyright (c) 2013 National Research Institute for Earth Science and
	 * Disaster Prevention (NIED).
	 * This code is licensed under the GPL version 3 license, available at the root
	 * application directory.
	 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
	<%@include file="../common/adminjs-header.jsp" %>
	<script type="text/javascript" language="javascript">
	  $(document).ready(function() {
		  $('#backupgrid').jqGrid({
			  mtype: 'POST',
			  url:'${f:url("../")}' + convertTabNameToMethodMame('adminbackup_data') + '/jqgridindex/',
			  datatype: 'json',
			  //colNames: ['ID','自冶体','グループ','名称','実施日'],
			  colNames: ['ID','<%=lang.__("Name")%>','<%=lang.__("Execution date")%>'],
			  colModel: [
				  {name:'id',editable:false,sortable:false,width:5},
				  //{name:'localgovinfoid', editable:false,sortable:false,width:20},
				  //{name:'groupid',editable:false,sortable:false,width:13},
				  {name:'name',editable:false,sortable:false,width:40},
				  //{name:'path',hidden:true,sortable:false,width:0},
				  {name:'registtime',editable:false,sortable:false,width:15}
			  ],
			  //loadonce: true,
			  rowNum:10,
			  rowList:[5,10,30,50,100],
			  pager: '#backupnav',
			  sortname: 'id',
			  sortorder: 'asc',
			  height: '100%',
			  width:590,
			  subGrid: false,
			  caption: '<%=lang.__("Backup history")%>',
			  cellsubmit: 'clientArray',
			  editurl:'',
			  //1行おきに背景色をかえる
			  loadComplete: function () {
                var rowIDs = jQuery("#backupgrid").getDataIDs();
                $.each(rowIDs, function (i, item) {
                    if (i % 2 == 0) {
                        $('#'+item).removeClass('ui-widget-content');
                        $('#'+item).addClass('myodd');
                    }
                });
			  },
			  loadtext:'<%=lang.__("Now loading..<!--2-->")%>',
			  viewrecords: true,
		  });
		  //ナビゲーション
		  //$('#backupgrid').jqGrid('navGrid','#backupnav',{edit:false,add:false,del:false});
	  });

	  function adminbackupDataGrid() {
		  return $('#backupgrid');
	  }

	</script>
</head>
<body>
  <table id="backupgrid" class="scroll"></table>
  <div id="backupnav" class="scroll"></div>
</body>
</html>
