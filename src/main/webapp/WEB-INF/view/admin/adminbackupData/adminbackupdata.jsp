<%	/* Copyright (c) 2013 National Research Institute for Earth Science and
	 * Disaster Prevention (NIED).
	 * This code is licensed under the GPL version 3 license, available at the root
	 * application directory.
	 */
%>
<%@include file="../../common/lang_resource.jsp" %>

<style type="text/css">
#adminbackupdata_savaing {
  display: none;
}
#adminbackupdata_loading {
  display: none;
}
</style>

<script type="text/javascript" language="javascript">

	function backupdata_init() {
		
		$(document.body).append(
			'<div id="backupreaddialog"><iframe id="adminbackupdata_iframe" frameborder="0" src="${f:url("../")}adminbackupData/" height=310 width=610></iframe>'
				+ '<br><img id=adminbackupdata_loading src="${f:url("/")}js/SaigaiTaskJS/css/images/loading.gif"></div>'
				+ '<div id="backupsavedialog"><form><label><%=lang.__("Name")%></label>'
				+ '<input type="text" id="backupsave-name" class="text ui-widget-content ui-corner-all" size="50"/>'
				+ '</form>'
				+ '<br><img id=adminbackupdata_savaing src="${f:url("/")}js/SaigaiTaskJS/css/images/loading.gif">'

				+ '</div>'
		);
		
		$('#backupreaddialog').dialog({
			autoOpen: false,
			title: '<%=lang.__("Backup dialog")%>',
			closeOnEscape: true,
			modal: true,
			height: 'auto',
			width:  'auto',
			buttons: {
				"<%=lang.__("Loading")%>": function(){
					//var grid = $('#backupgrid');
					var grid = $('#adminbackupdata_iframe').contents().find('#backupgrid');
					var selRow = grid.jqGrid ('getGridParam', 'selrow');
					if (!selRow) {
						showMessage("adminbackupdata_msg", "<%=lang.__("Please select backup.")%>", "ERROR");
						return;
					}
					$('#adminbackupdata_loading').show();
					jQuery.ajax({
						type: "POST",
						url:'${f:url("../")}' + convertTabNameToMethodMame('adminbackup_data') + '/restore/',
						data: 'id=' + grid.jqGrid ('getCell', selRow, 'id'),
						success: function(msg) {
							$('#adminbackupdata_loading').hide();
							if (msg)
								showMessage("adminbackupdata_msg", msg, "ERROR");
							else
								showMessage("adminbackupdata_msg", "<%=lang.__("Loaded")%>", "MESSAGE");
						},
						error: function(request, text, error) {
							$('#adminbackupdata_loading').hide();
							showMessage("adminbackupdata_msg", text, "ERROR");
						}
					});
				},
				"<%=lang.__("Close")%>": function(){
					$(this).dialog('close');
				}
			},
			
			//close: function(){
			//	// グリッドを削除しておかないと前の状態が残ってしまうので削除する。
			//	var grid = $('#adminbackupdata_iframe').contents().find('#backupgrid');
			//	grid.GridUnload();
			//}
		});
		
		$('#backupsavedialog').dialog({
			autoOpen: false,
			title: '<%=lang.__("Backup dialog")%>',
			closeOnEscape: true,
			modal: true,
			height: 'auto',
			width:  'auto',
			buttons: {
				"<%=lang.__("Save")%>": function(){
					var name = $("#backupsave-name").val();
					if (!name) {
						showMessage("adminbackupdata_msg", "<%=lang.__("Please enter name.")%>", "ERROR");
						return;
					}
					$('#adminbackupdata_savaing').show();
					jQuery.ajax({
						type: "POST",
						url:'${f:url("../")}' + convertTabNameToMethodMame('adminbackup_data') + '/backup/',
						data: 'name=' + name,
						success: function(msg, type) {
							$('#adminbackupdata_savaing').hide();
							if (msg)
								showMessage("adminbackupdata_msg", msg, "ERROR");
							else {
								showMessage("adminbackupdata_msg", "<%=lang.__("Saved")%>", "MESSAGE");
							}
						},
						error: function(request, text, error) {
							$('#adminbackupdata_savaing').hide();
							showMessage("adminbackupdata_msg", text, "ERROR");
						}
					});
				},
				"<%=lang.__("Close")%>": function(){
					$(this).dialog('close');
				}
			}
		});
	}

	function backupdata_read() {
		$('#backupreaddialog').dialog('open');
	}

	function backupdata_save() {
		$('#backupsavedialog').dialog('open');
	}

</script>
