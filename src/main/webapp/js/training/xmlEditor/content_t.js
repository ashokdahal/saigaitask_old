/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
// 0 add 1 edit
var g_dialog1_type = 0;
var g_dialog2_type = 0;
var g_dialog3_type = 0;
var g_dialog4_type = 0;

var g_area_num_1 = 0;
var g_area_num_1 = 0;
function insertAfter(byid){
   var obj=document.createElement("div");
   obj.id = "add div";
   document.getElementById(byid).appendChild(obj);
}

String.prototype.startWith=function(startStr){
  var d=startStr.length;
  return (d>=0&&this.indexOf(startStr)==0)
}
String.prototype.endWith=function(endStr){
  var d=this.length-endStr.length;
  return (d>=0&&this.lastIndexOf(endStr)==d)
}
function getAllChildNode(v, c)
{
	 var cs = document.getElementById(v).childNodes
	 for(i=0;i<cs.length;i++)
	 {
		var temp = cs[i].id + "";
		// alert(cs[i].nodeType);
		alert(temp.startWith(c));
		alert(temp);
		if(temp.startWith(c)) {
			alert(cs[i].id + " = " + cs[i].value)
		}
	 }

}

function findDivByPrevPos(p)
{
	 var cs = document.getElementById(v).childNodes
	 for(i=0;i<cs.length;i++)
	 {
		var temp = cs[i].id + "";
		//alert(cs[i].nodeType);
		//alert(temp.startWith(c));
		//alert(temp);
		if(temp.startWith(c)) {
			alert(cs[i].id + " = " + cs[i].value)
		}
	 }
}


function setCtrlSdInfo(ctrl, data_div_warp, classV, data_bigpos, data_bigposno, data_level, data_levelno, data_levelfieldno)
{
	ctrl.data_div_warp = data_div_warp;
	ctrl.class = classV;
	ctrl.data_bigpos = data_bigpos;
	ctrl.data_bigposno = data_bigposno;
	ctrl.data_level = data_level;
	ctrl.data_levelno = data_levelno;
	ctrl.data_levelfieldno = data_levelfieldno;
}


function getChildNodes(e) 
{
	var childArr = e.children || e.childNodes;
	var childArrItem = new Array();
	for(var i = 0; i < childArr.length; i++)
	{
		if(childArr[i].nodeType == 1) {
			childArrItem.push(childArr[i]);
		}
	}
	return childArrItem;
}
function getJsonDataChildNode(v, c, type)
{
	// ChildNodes 非常にひどいBUGがあります、使えません。
	t01NewClearData();
	var cscur = document.getElementById(v);
	var cs = getChildNodes(cscur);
	for (i = 0; i < cs.length; i++) {
		var temp = cs[i].id + "";

		
		// alert(cs[i].nodeType);
		//alert(temp.startWith(c));
		//alert(temp);
		if (temp.startWith(c)) {
			// alert(cs[i].id + " = " + cs[i].value)
			if (type == 1) {
				t01NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 2) {
				t02NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 3) {
				t03NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 4) {
				t04NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 5) {
				t05NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 6) {
				t06NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 7) {
				t07NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 8) {
				t08NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 9) {
				t09NewAddData(cs[i].id, cs[i].value);
			}

		}
		if(cs[i].hasChildNodes()) {
			var curs = getChildNodes(cs[i]);
			if(curs.length > 0) {
				getJsonDataChildNodeSub(curs, v, c, type);
			}
		}
	}

}

function getJsonDataChildNodeSub(parent, v, c, type)
{
	var cs = parent;
	//alert(cs.length)
	console.log("cs.length="+cs.length);
	
	for (i = 0; i < cs.length; i++) {
		var temp = cs[i].id + "";
		console.log("i========================="+i);
		console.log("cs[i].id="+cs[i].id);
		console.log("cs[i].nodeType="+cs[i].nodeType);
		console.log("cs[i].name="+cs[i].name);
		console.log("cs[i]="+cs[i]);
		console.log("cs[i].innerHTML="+cs[i].innerHTML);
		
		// alert(cs[i].nodeType);
		//alert(temp.startWith(c));
		
		//cs[i].style="background:#FF0000;";
		if (temp.startWith(c)) {
			// alert(cs[i].id + " = " + cs[i].value)
			if (type == 1) {
				t01NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 2) {
				t02NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 3) {
				t03NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 4) {
				t04NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 5) {
				t05NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 6) {
				t06NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 7) {
				t07NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 8) {
				t08NewAddData(cs[i].id, cs[i].value);
			}
			if (type == 9) {
				t09NewAddData(cs[i].id, cs[i].value);
			}

		}
		console.log("cs[i].innerHTML========================="+cs[i].innerHTML);
		// not element
		if(cs[i].hasChildNodes()) {
			//alert(cs[i].nodeType);
			var curs = getChildNodes(cs[i]);
			if(curs.length > 0) {
				console.log("iA========================="+i);
				console.log("curs.innerHTML========================="+curs.innerHTML);
				getJsonDataChildNodeSub(curs, v, c, type);
				console.log("iB========================="+i);
			}
		}

	}

}


