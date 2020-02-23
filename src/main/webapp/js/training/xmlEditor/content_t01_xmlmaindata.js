/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
// t01
////////////////////////////////////////////////////////////////////
// Save
// この後ろの変数名は小文字にしてください
function t01control () {
	this.xmlext_filename = "";
	this.title = "";
	this.datetime = "";
}

function t01head() {
	this.title = "";
}

function t01headline() {
	this.text = "";
}

function t01itemarea() {
	this.name = "";
	this.code = "";
}

function t01item() {
	this.name = "";
	this.areaarray = new Array();
}

function t01body() {
	this.maxint = "";
	this.prefarray = new Array();
}

function t01pref() {
	this.code = "";
	this.maxint = "";
	this.areaarray = new Array();
}

function t01areapref() {
	this.code = "";
	this.maxint = "";
}

function T01JishinsokuhouItem() {
	this.control = new t01control();
	this.head = new t01head();
	this.headline = new t01headline();
	this.itemarray = new Array();
	this.body = new t01body();
}

// この前は変数名は小文字にしてください

var clsT01JishinsokuhouItem = new T01JishinsokuhouItem();

function getT01JsonData() {
	return clsT01JishinsokuhouItem;
}	
function getT01JsonDataUrl() {
	return encodeURIComponent(ObjToJSON(clsT01JishinsokuhouItem));
}	
function getT01JsonDataUrlOrg() {
	
	return ObjToJSON(clsT01JishinsokuhouItem);
}	
function t01ClearData() {
	clsT01JishinsokuhouItem = new T01JishinsokuhouItem();
}

function setT01ControlExtData(filename) {
	clsT01JishinsokuhouItem.control.xmlext_filename = filename;
}


function setT01ControlData(title, datetime) {
	clsT01JishinsokuhouItem.control.title = title;
	clsT01JishinsokuhouItem.control.datetime = datetime;
}

function setT01HeadData(title, text) {
	clsT01JishinsokuhouItem.head.title = title;
	clsT01JishinsokuhouItem.headline.text = text;
}

function setT01AddItemToArray(name) {
	var t = new t01item();
	t.name = name;
	clsT01JishinsokuhouItem.itemarray.push(t);
}
function setT01EditItemInArray(index, name) {
	clsT01JishinsokuhouItem.itemarray[index].name = name;
}

function setT01EditItemInArray_AddArea(index, name, code) {
	var t = new t01itemarea();
	t.name = name;	
	t.code = code;
	clsT01JishinsokuhouItem.itemarray[index].areaarray.push(t);
}

function setT01EditItemInArray_EditArea(index, indexArea, name, code) {
	clsT01JishinsokuhouItem.itemarray[index].areaarray[indexArea].name = name;
	clsT01JishinsokuhouItem.itemarray[index].areaarray[indexArea].code = code;
}

function setT01BodyData(maxint) {
	clsT01JishinsokuhouItem.body.maxint = maxint;
}

function setT01BodyData_AddPref(code, maxint) {
	var t = new t01pref()
	t.code = code;
	t.maxint = maxint;	
	clsT01JishinsokuhouItem.body.prefarray.push(t);
}

function setT01BodyData_EditPref(index, code, maxint) {
	clsT01JishinsokuhouItem.body.prefarray[index].code = code;
	clsT01JishinsokuhouItem.body.prefarray[index].maxint = maxint;
}

function setT01BodyData_Pref_AddArea(index, code, maxint) {
	var t = new t01areapref()
	t.code = code;
	t.maxint = maxint;		
	clsT01JishinsokuhouItem.body.prefarray[index].areaarray.push(t);
}

function setT01BodyData_Pref_EditArea(index, indexArea, code, maxint) {	
	clsT01JishinsokuhouItem.body.prefarray[index].areaarray[indexArea].code = code;
	clsT01JishinsokuhouItem.body.prefarray[index].areaarray[indexArea].maxint = maxint;
}

function utT01Test01InitData() {
	
	t01ClearData();
	
	setT01ControlExtData("20150903_230141_0001.xml");
	
	setT01ControlData("c_title_data1", "2012/11/11 11:11:11");
	
	setT01HeadData("h_title_data1", "jisinsokuhou 関数関数関数関数関数");
	
	setT01AddItemToArray("name1");
	setT01EditItemInArray_AddArea(0, "name01", "code1");
	setT01EditItemInArray_AddArea(0, "name02", "code2");
	
	setT01AddItemToArray("name2");
	setT01EditItemInArray_AddArea(1, "name11", "code1");
	setT01AddItemToArray("name3");
	setT01EditItemInArray_AddArea(2, "name21", "code1");
	setT01AddItemToArray("name4");
	setT01EditItemInArray_AddArea(3, "name31", "code1");
	
	setT01BodyData("6-");
	setT01BodyData_AddPref(0, "code01", "6+");
	setT01BodyData_AddPref(0, "code02", "6+");
	
	setT01BodyData("6+");
	setT01BodyData_AddPref(1, "code11", "6+");
	
	setT01BodyData_Pref_AddArea(1, "code111", "6++");
	setT01BodyData_Pref_AddArea(1, "code112", "6++1");
	setT01BodyData_Pref_AddArea(1, "code113", "6++2");
	
	setT01BodyData_AddPref(1, "code11", "6+");
	
}

////////////////////////////////////////////////////////////////////
// Edit
function T01EditInfo () {
	this.xmlext_filename = "";
	this.xmlext_edittype = "-1";
	this.xmlext_editsavetype = "0";
}
function getT01EditInfo_JsonDataUrl(filename) {
	var t = new T01EditInfo();
	t.xmlext_filename = filename;
	return encodeURIComponent(ObjToJSON(t));
}

function getT01ConfirmFileNameInfo_JsonDataUrl(filename, edittype, editsavetype) {
	var t = new T01EditInfo();
	t.xmlext_filename = filename;
	t.xmlext_edittype = edittype;
	t.xmlext_editsavetype = editsavetype;
	return encodeURIComponent(ObjToJSON(t));
}

///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////
// New TO1
function t01NewItem() {
	this.datauid = "";
	this.datavalue = "";
}
function t01NewItemArray() {
	this.dataarray = new Array();
}

var t01NewItemArrayObj = new t01NewItemArray();

function t01NewAddData(uid, value) {
	var t = new t01NewItem()
	t.datauid = uid;
	/*
	if(value.startWith("\"") == false) {
		value = "\"" + value;
	}
	if(value.endWith("\"") == false) {
		value =  value + "\"";
	}
	*/
	t.datavalue = value;		
	t01NewItemArrayObj.dataarray.push(t);
}

function t01NewClearData() {
	t01NewItemArrayObj = new t01NewItemArray();
}

function t01NewGetJsonData() {
	return t01NewItemArrayObj;
}	
function t01NewGetJsonDataUrl() {
	return encodeURIComponent(ObjToJSON(t01NewItemArrayObj));
}	
function t01NewGetJsonDataUrlOrg() {
	
	return ObjToJSON(t01NewItemArrayObj);
}

//New TO1 END
///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////

