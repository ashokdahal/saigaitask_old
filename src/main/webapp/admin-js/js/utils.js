
function clearCombo(v_combo) {
	while (v_combo.options.length > 0)
		v_combo.options.remove(0);
}

function addTextvalue2Combo(v_combo, v_text, v_value) {
	var v_opt = document.createElement('option');
	v_opt.text = v_text;
	v_opt.value = v_value;
	try {
		v_combo.add(v_opt, null); 	//standards compliant
	}
	catch(ex) {
		v_combo.add(v_opt);			//IE only
	}
}

function createOption(value, label, selected)
{
	option = document.createElement("option");
	option.value = value;
	option.innerHTML = escapeXml(label);
	if (selected) option.selected = true;
	return option;
}

function create(objectName, attributes) {
	var object = document.createElement(objectName);
	for (key in attributes)
		object[key] = attributes[key];
	return object;
}

function escapeXml(str)
{
	return str.toString().replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
}

function get_list_selected_text(listObj) {
	return listObj.options[listObj.selectedIndex].text;
}

function get_radio_value(radio_name) {
	var rdo = document.getElementsByName(radio_name);
	var rdoValue;	
	for (i = 0; i < rdo.length; i++) {
		if (rdo[i].checked)
			rdoValue = rdo[i].value;
	}
	return rdoValue;
}

function jqGridButton_hover(obj) {
	$("#" + obj.id).addClass("ui-state-hover");
}

function jqGridButton_out(obj) {
	$("#" + obj.id).removeClass("ui-state-hover");
}

function is_date(v_sDate, format) {
	format = format.toLowerCase();
	var pattern = get_date_format_pattern(format);
	return pattern.test(v_sDate);
}

function get_date_format_pattern(v_format) {
	if (v_format == 'yyyy/mm/dd') {
		pattern = /^[0-9]{4}[\/](0?[1-9]|1[012])[\/](0?[1-9]|[12][0-9]|3[01])$/;
	}
	else if (v_format == 'dd/mm/yyyy') {
		pattern = /^(0[1-9]|[12][0-9]|3[01])[\/](0[1-9]|1[012])[\/][0-9]{4}$/;
	}
	else {
		pattern = /^(0[1-9]|1[012])[\/](0[1-9]|[12][0-9]|3[01])[\/][0-9]{4}$/;
	}
	return pattern;
}

function br2breakline(v_text) {
	if (v_text) {
		v_text		= v_text.replace(/<br>/g, "\n");
		v_text		= v_text.replace(/<br\/>/g, "\n");
		return v_text;
	}
	else
		return "";
}