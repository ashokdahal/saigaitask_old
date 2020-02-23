/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

/**
Parameter:
-mapId (Ex:0)
-userId (Ex:admin)
*/



function AttrInfo() {

}

AttrInfo.prototype =
{
	DATATYPE_TEXT : 12,

	//属性型 数値(整数)
	DATATYPE_INTEGER : 4,

	//属性型 数値
	DATATYPE_FLOAT : 6,

	//属性型 日付
	DATATYPE_DATE : 91,

	//属性型 日付と時間
	DATATYPE_DATETIME : 92,

	//属性型 時間
	DATATYPE_TIMESTAMP : 93,

	//属性型 URL
	DATATYPE_URL : 10010,

	//属性型 eメールアドレス
	DATATYPE_EMAIL : 10011,

	//属性型 HTMLタグを含む文書
	DATATYPE_HTML : 10020,

	//属性型 選択(文字列)
	DATATYPE_SELECT : 11000,

	//属性型 選択(文字列、自由入力)
	DATATYPE_COMBO : 11001,

	//属性型 選択(文字列、ラジオ)
	DATATYPE_RADIO : 11010,

	//属性型 (固定文字列 チェックボックス単体)
	DATATYPE_CHECKBOX : 11020,

	//属性型 更新日(編集ダイアログで自動設定される)
	DATATYPE_MODDATE : 20000, //<span class="ja">更新日自動設定</span><span class="en">The update date is automatically set</span>

	//属性型 更新日時(編集ダイアログで自動設定される)
	DATATYPE_MODDATETIME : 20001,

	//グループ化表示用の属性
	DATATYPE_GROUP : 20010,

	attrId: null,
	name: null,
	status: null,
	length: 0,
	maxLength: 0,
	dataType: 0,
	dataExp: 0,
	nullable: true,

	isReadOnly : function(){ return this.status==1; },
	isHidden : function(){ return this.status==-1; }
};


SaigaiTask.Filtering = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Filtering.prototype = {
	tab: "attr",		//attr, spatial
	layerAttrInfo : null,
	layerList: null,	//レイヤリストを維持する。
	mapId: 0,
	user: "admin",
	taskIdElementName: "",
	taskId: 0,
	filterIdElementId: "",
	filterId: 0,
	isSelectedFilter: false,		//読み込みリストにフィルタを選択したことがあるかどうか
	isFirstLoadList: 1,
	isModified: false,				// フィルター設定ダイアログで入力項目を変更したか
	isSaved: false,					// 検索条件保存ダイアログで保存を実行したか


	/** イベントオブジェクト */
	events: null,

	/** ダイアログ */
	dialog: null,

	msg: {
		FilterGeneralError: lang.__("An error occurred."),
		FilterMsg1: lang.__("Please decide after read or save a filter."),
		FilterMsg2: lang.__("Unable to get user and map ID."),
		FilterMsg3: lang.__("Search condition has not been set."),
		FilterMsg4: lang.__("Unable to get search condition."),
		FilterMsg5: lang.__("Select search conditions."),
		FilterMsg6: lang.__("Are you sure to delete the selected search conditions?"),
		FilterMsg7: lang.__("Search condition deleted."),
		FilterMsg8: lang.__("Set search conditions."),
		FilterMsg9: lang.__("Save search conditions."),
		FilterMsg10: lang.__("Unable to get the search condition. Clear filter ID and search")
	},

	initialize: function() {
		var me = this;
		me.events = $("<div></div>"); // イベント用の要素を作成
	},


	/**
	 * 検索ダイアログで検索対象を選択
	 */
	setSearchLayer: function(searchLayerId, onLoad) {
// console.log("*****setSearchLayer");
// console.log(this);
	    var me = this;
		if (!searchLayerId) return;
		me.searchLayerId = searchLayerId;

		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/getAttributeList/' + me.user + "/" + me.searchLayerId;
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		$.ajax({
			type: "POST",
			url: url,
			async: false,
			success: function(data) {
				var i;
				var objAttrSelect = document.getElementById("search_attr_select");

				me.layerAttrInfo = new Object();
				clearCombo(objAttrSelect);
				for (i = 0; i < data.items.length; i++) {
					me.layerAttrInfo[data.items[i].attrId] = data.items[i];
					addTextvalue2Combo(objAttrSelect, data.items[i].name, data.items[i].attrId);
				}

				if (onLoad) onLoad();
			},
			error: function(request, text, error) {
				alert(FilterGeneralError);
			}
		});
	},


	getSearchKeywords: function(form) {
		try{
			var attrs = [];
			var comps = [];
			var values = [];
			var values2 = [];
			var logical = [];

			//空のロジッカルを追加
			var temp = new Object();
			temp.name = "logical[]";
			temp.value = "";
			logical.push(temp);

			//DOMで生成したinputはIEは名前で取得できないのでelementsのnameをチェックして回す
			for (var i=0; i < form.elements.length; i++) {
				var elem = form.elements[i];
				if (elem.name == "attr[]") attrs.push(elem);
				else if (elem.name == "comp[]") comps.push(elem);
				else if (elem.name == "value[]") values.push(elem);
				else if (elem.name == "value2[]") values2.push(elem);
				else if (elem.name == "logical[]") logical.push(elem);
			}

			var v;
			var keywords='';
			for (var i=0; i < attrs.length; i++) {
				var attrId = attrs[i].value;
				var comp = comps[i].value;
				var input = values[i];
				var input2 = values2 ? values2[i] : null;

				if (logical && logical[i]) {
					keywords += logical[i].value+' ';
				}
				switch (input.type) {
				case "radio":
					if (input.checked && input.value.length) keywords += input.value+' ';
					break;
				case "checkbox":
					if (input.checked) keywords += attrId+'='+input.value+' ';
					else keywords += attrId+'<>'+input.value+' ';
					break;
				case "hidden":
					v = input.value;
					if (v && v.match(/\d\d\d\d-\d\d-\d\d/)) v = v.replace(/-/g, "/");
					if (input2 && input2.value.length>0) v += input2.value;
					if (v.length > 0) {
						keywords += attrId+comp+v+' ';
					}
					break;
				default:
					v = input.value.toString().trim();//空文字対策
					if (input2 && input2.value.toString().trim() != "") {
						v = v + "T" + input2.value.toString().trim();
					}
					if (v.length > 0) keywords += attrId+comp+v+' ';
				}
			}
			return keywords;
		} catch (e) { console.error(e); }
	},


	getSpatialSearchKeywords: function(form) {
		try{
			var layers = [];
			var buffers = [];
			var searches = [];
			var logical = [];

			//空のロジッカルを追加
			var temp = new Object();
			temp.name = "spatial_logical[]";
			temp.value = "";
			logical.push(temp);

			//DOMで生成したinputはIEは名前で取得できないのでelementsのnameをチェックして回す
			for (var i=0; i < form.elements.length; i++) {
				var elem = form.elements[i];
				if (elem.name == "spatial_layer[]") layers.push(elem);
				else if (elem.name == "spatial_buffer[]") buffers.push(elem);
				else if (elem.name == "spatial_search[]") searches.push(elem);
				else if (elem.name == "spatial_logical[]") logical.push(elem);
			}

			var v;
			var keywords='';
			for (var i=0; i < layers.length; i++) {
				var layerId = layers[i].options[layers[i].selectedIndex].text;
				var buffer = buffers[i].value;
				var search = searches[i].options[searches[i].selectedIndex].text;

				if (logical && logical[i]) {
					keywords += " " + logical[i].value+' ';
				}

				keywords += lang.__("{0} to {1} distance {2} m", layerId, search, buffer);
			}
			return keywords;
		} catch (e) { console.error(e); }
	},


	getSpatialSearchJson: function(form) {
		try{
			var layers = [];
			var buffers = [];
			var searches = [];
			var logical = [];

			//空のロジッカルを追加
			var temp = new Object();
			temp.name = "spatial_logical[]";
			temp.value = "";
			logical.push(temp);

			//DOMで生成したinputはIEは名前で取得できないのでelementsのnameをチェックして回す
			for (var i=0; i < form.elements.length; i++) {
				var elem = form.elements[i];
				if (elem.name == "spatial_layer[]") layers.push(elem);
				else if (elem.name == "spatial_buffer[]") buffers.push(elem);
				else if (elem.name == "spatial_search[]") searches.push(elem);
				else if (elem.name == "spatial_logical[]") logical.push(elem);
			}

			var v;
			var json = '';
			for (var i=0; i < layers.length; i++) {
				var layerId = layers[i].value;
				var buffer = buffers[i].value;
				var search = searches[i].value;

				if (json != "")
					json += ",";

				json += '{';
				json += '"buffer":' + buffer;
				json += ',"type":' + search;
				json += ',"layer":"' + layerId + '"';


				var l = "";
				if (logical && logical[i]) {
					l = logical[i].value;
				}
				json += ',"logical":"' + l + '"}';
			}
			return "[" + json + "]";	//JSON 配列形式
		} catch (e) { console.error(e); }
	},


	/**
	 * 属性検索条件、あるいは空間検索条件が存在するかどうかを確認する。
	 */
	hasCondition: function(form) {
		var hasCondition = false;
		for (var i=0; i < form.elements.length && !hasCondition; i++) {
			var elem = form.elements[i];
			if (elem.name == "comp[]" || elem.name == "spatial_search[]")
				hasCondition = true;
		}
		return hasCondition;
	},


	/**
	 * 属性検索条件をform内tableに追加
	 * @param attrId	属性ID
	 * @param comp	比較条件 無ければ一番上
	 * @param value	検索値 無ければ空欄  日時の場合はスペースで連結済み文字列
	 */
	addSearchInput: function(attrId, comp, value, logical) {
// console.log("*************************");
// console.log(this);
	    var me = this;
		if (!value) value = "";

		var attrInfo = me.layerAttrInfo[attrId];
// console.log("*************************");
		if (!attrInfo) {
			alert(lang.__("There is no attribute info.") + attrId);
			return;
		}

		var inputWidth = "200px";

		var formTbody = document.getElementById("search_attr_form_tbody");

		var trLogical, tr, td, select, option, i;
		//先頭でなければtrLogocal追加
		if (formTbody.children.length > 0) {
			trLogical = document.createElement("tr");
			trLogical.className = "logical";
			td = document.createElement("td");
			trLogical.appendChild(td);
			td = document.createElement("td");

			select = document.createElement('select');
			select.name = "logical[]";

			option = createOption("AND", "AND");
			select.appendChild(option);
			if (logical == "AND")
				option.selected = true;

			option = createOption("OR", "OR");
			select.appendChild(option);
			if (logical == "OR")
				option.selected = true;

			select.onchange = function(){me.setModified();};

			td.appendChild(select);
			trLogical.appendChild(td);
			formTbody.appendChild(trLogical);
		}

		tr = document.createElement("tr");
		td = document.createElement("td");
		td.align = "right";
		td.innerHTML = escapeXml(attrInfo.name)+"： ";
		tr.appendChild(td);
		td = document.createElement("td");

		td.appendChild(create("input", {type:"hidden", name:"attr[]", value:attrInfo.attrId}));
		var input;
		var input2 = create("input", {type:"hidden", name:"value2[]"});

		var attrInfoPrototype = AttrInfo.prototype;

		switch (attrInfo.dataType) {
			case attrInfoPrototype.DATATYPE_SELECT:
				select = create("select", {className:"search_attr", name:"value[]"});
				select.style.width = inputWidth;
				select.appendChild(createOption("", ""));
				var values = attrInfo.dataExp.split(",");
				for (var i=0; i<values.length; i++) {
					select.appendChild(createOption(values[i], values[i], values[i]==value));
				}
				td.appendChild(select);
				select.onchange = function(){me.setModified();};
				break;

			case attrInfoPrototype.DATATYPE_INTEGER:
			case attrInfoPrototype.DATATYPE_FLOAT:
				input = create("input", {className:"search_attr", type:"text", name:"value[]", value:value});
				input.style.width = inputWidth;
				input.onchange = function(){me.setModified();};
				td.appendChild(input);
				break;

			case attrInfoPrototype.DATATYPE_DATE:
				var date= null;
				if (value) {
					if (is_date(value, "yyyy/mm/dd")) {
						var temp = new Date(value);
						date = temp.getFullYear() + "/" + (temp.getMonth() + 1) + "/" + temp.getDate();
					}
				}
				input = create("input", {type:"text", name:"value[]", className:"search_attr", style:"width:100px;", value: date});
				input.onchange = function(){me.setModified();};
				td.appendChild(input);
				break;

			case attrInfoPrototype.DATATYPE_DATETIME:
				var date = null;
				var time = null;
				if (value) {
					var tokens = value.split("T");
					date = tokens[0];
					time = tokens[1];

					if (is_date(date, "yyyy/mm/dd")) {
						var temp = new Date(date);
						date = temp.getFullYear() + "/" + (temp.getMonth() + 1) + "/" + temp.getDate();
					}
				}

				input = create("input", {type:"text", name:"value[]", className:"search_attr", style:"width:100px;", value:date});
				input.onchange = function(){me.setModified();};
				td.appendChild(input);
				var timeTextBox = create("input", {
					type:"text", name:"value2[]", className:"search_attr", style:"width:80px;", value:time}
				);
				input2 = timeTextBox;
				input2.onchange = function(){me.setModified();};
				break;

			case attrInfoPrototype.DATATYPE_CHECKBOX:
				var chkValue = (attrInfo.dataExp && attrInfo.dataExp.length>0) ? escapeXml(attrInfo.dataExp) : "○";
				var label = document.createElement("label");
				input = create("input", {type: "checkbox", name:"value[]", className:"search_attr", value:chkValue, checked:comp=="="});
				input.onchange = function(){me.setModified();};
				label.appendChild(input);
				var span = document.createElement("span");
				span.innerHTML = " " + escapeXml(chkValue);
				label.appendChild(span);
				td.appendChild(label);
				break;

			default:
				if (attrInfo.length > 20) {
					input = create("textarea", {className:"search_attr", rows:2, name:"value[]", value:value});
				} else {
					input = create("input", {className:"search_attr", type:"text", name:"value[]", value:value});
				}
				input.style.width = inputWidth;
				input.onchange = function(){me.setModified();};
				td.appendChild(input);
		}



		td.appendChild(input2);
		tr.appendChild(td);

		td = document.createElement("td");
		td.style.whiteSpace = "nowrap";

		switch (attrInfo.dataType) {
			case attrInfoPrototype.DATATYPE_CHECKBOX:
				input = create("input", {type:"hidden", name:"comp[]", value:"="});
				td.appendChild(input);
				break;

			case attrInfoPrototype.DATATYPE_DATE:
			case attrInfoPrototype.DATATYPE_DATETIME:
				select = create("select", {name:"comp[]"});
				if (attrInfo.dataType = attrInfoPrototype.DATATYPE_DATETIME) {
					select.appendChild(createOption("~*", lang.__("Include")));
					select.appendChild(createOption("!~*", lang.__("Not include")));
				}
				select.appendChild(createOption("=", lang.__("= equal")));
				select.appendChild(createOption("<>", lang.__("<> different")));
				select.appendChild(createOption("<=", lang.__("< now or before")));
				select.appendChild(createOption(">=", lang.__(">= later")));
				select.appendChild(createOption("<", lang.__("< before")));
				select.appendChild(createOption(">", lang.__("> later")));
				select.onchange = function(){me.setModified();};
				td.appendChild(select);
				break;

			default :
				select = create("select", {name:"comp[]"});
				select.appendChild(createOption("~*", lang.__("Include")));
				select.appendChild(createOption("!~*", lang.__("Not include")));
				select.appendChild(createOption("=", lang.__("= equal")));
				select.appendChild(createOption("<>", lang.__("<> different")));
				select.appendChild(createOption("<=", lang.__("<= or less")));
				select.appendChild(createOption(">=", lang.__(">= or more")));
				select.appendChild(createOption("<", lang.__("< less than")));
				select.appendChild(createOption(">", lang.__("> more than")));
				select.onchange = function(){me.setModified();};
				td.appendChild(select);
			}

		if (comp) {
			if (attrInfo.dataType != attrInfoPrototype.DATATYPE_CHECKBOX) {
				i = 0;
				while (i < select.options.length && select.options[i].value != comp)
					i++;
				if (i < select.options.length)
					select.selectedIndex = i;
			}
		}


		tr.appendChild(td);
		td = document.createElement("td");
		td.style.whiteSpace = "nowrap";
		var a = document.createElement("a");
		a.href = "#";
		a.innerHTML = lang.__("Delete");
		//var self = this;
		a.onclick = function(){
			me.removeSearchInput(formTbody, trLogical, tr);
			me.setModified();
			return false;
		};
		td.appendChild(a);
		tr.appendChild(td);
		formTbody.appendChild(tr);
	},


	removeSearchInput: function(formTbody, trLogical, tr) {
		if (trLogical) {
			//先頭になった場合削除されるのでcatch
			try { formTbody.removeChild(trLogical); } catch (e) {}
		}
		formTbody.removeChild(tr);
		var firstTr = formTbody.firstChild;
		if (firstTr && firstTr.className == "logical") {
			formTbody.removeChild(firstTr);
		}
	},


	/**
	 * 検索条件をすべて削除する
	 */
	clearSearchInput: function() {
		var formTbody = document.getElementById('search_attr_form_tbody');
		formTbody.innerHTML = "";
	},





	/**
	 * 空間検索条件をform内tableに追加
	 */
	addSearchSpatial: function(layerId, buffer, type, logical) {
		var me = this;
		var inputWidth = "200px";

		var formTbody = document.getElementById("search_spatial_form_tbody");

		var trLogical, tr, td, select, option, i;
		//先頭でなければtrLogocal追加
		if (formTbody.children.length > 0) {
			trLogical = document.createElement("tr");
			trLogical.className = "logical";
			td = document.createElement("td");

			select = document.createElement('select');
			select.name = "spatial_logical[]";

			option = createOption("AND", "AND");
			select.appendChild(option);
			if (logical == "AND")
				option.selected = true;

			option = createOption("OR", "OR");
			select.appendChild(option);
			select.onchange = function(){me.setModified();};
			if (logical == "OR")
				option.selected = true;

			td.appendChild(select);
			trLogical.appendChild(td);

			td = document.createElement("td");
			trLogical.appendChild(td);
			td = document.createElement("td");
			trLogical.appendChild(td);
			td = document.createElement("td");
			trLogical.appendChild(td);
			td = document.createElement("td");
			trLogical.appendChild(td);


			formTbody.appendChild(trLogical);
		}
		tr = document.createElement("tr");


		//レイヤ選択コンボを作成
		td = document.createElement("td");
		select = document.createElement('select');
		select.name = "spatial_layer[]";
		var selectedIndex = 0;
		for (i = 0; i < me.layerList.length; i++) {
			select.appendChild(createOption(me.layerList[i].layerid, me.layerList[i].name));

			if (layerId) {
				if (me.layerList[i].layerid == layerId)
					selectedIndex = i;
			}
		}
		if (layerId) {
			select.selectedIndex = selectedIndex;
		}
		select.onchange = function(){me.setModified();};
		td.appendChild(select);
		tr.appendChild(td);


		//距離コンボを作成
		td = document.createElement("td");
		select = document.createElement('select');
		select.name = "spatial_buffer[]";
		select.appendChild(createOption(0, 0));
		select.appendChild(createOption(50, 50));
		select.appendChild(createOption(100, 100));
		select.appendChild(createOption(150, 150));
		select.appendChild(createOption(200, 200));
		select.appendChild(createOption(250, 250));
		select.appendChild(createOption(300, 300));
		select.appendChild(createOption(400, 400));
		select.appendChild(createOption(500, 500));
		select.appendChild(createOption(600, 600));

		select.appendChild(createOption(700, 700));
		select.appendChild(createOption(800, 800));
		select.appendChild(createOption(900, 900));
		select.appendChild(createOption(1000, 1000));

		select.appendChild(createOption(1500, 1500));
		select.appendChild(createOption(2000, 2000));
		select.appendChild(createOption(2500, 2500));
		select.appendChild(createOption(3000, 3000));
		select.appendChild(createOption(4000, 4000));
		select.appendChild(createOption(5000, 5000));
		select.appendChild(createOption(6000, 6000));
		select.appendChild(createOption(7000, 7000));
		select.appendChild(createOption(8000, 8000));
		select.appendChild(createOption(9000, 9000));
		select.appendChild(createOption(10000, 10000));
		select.onchange = function(){me.setModified();};

		//距離を選択
		if (buffer) {
			i = 0;
			while (i < select.options.length && select.options[i].value != buffer)
				i++;
			if (i < select.options.length)
				select.selectedIndex = i;
		}

		td.appendChild(select);
		tr.appendChild(td);

		td = document.createElement("td");
		td.innerHTML = "m &nbsp;";
		tr.appendChild(td);


		//ロジックボックス
		td = document.createElement("td");
		td.style.whiteSpace = "nowrap";

		select 		= document.createElement('select');
		select.name = "spatial_search[]";
		select.appendChild(createOption(1, lang.__("Overlap with the extent of")));
		select.appendChild(createOption(2, lang.__("Completely included in range")));
		select.appendChild(createOption(101, lang.__("No overlap with the extent of")));
		select.appendChild(createOption(103, lang.__("Not completely included in range")));
		select.onchange = function(){me.setModified();};

		//タイプ
		if (type) {
			i = 0;
			while (i < select.options.length && select.options[i].value != type)
				i++;
			if (i < select.options.length)
				select.selectedIndex = i;
		}

		td.appendChild(select);
		tr.appendChild(td);


		//削除ボタン
		td = document.createElement("td");
		td.style.whiteSpace = "nowrap";
		var a = document.createElement("a");
		a.href = "#";
		a.innerHTML = lang.__("Delete");
		a.onclick = function(){
			me.removeSearchSpatial(formTbody, trLogical, tr);
			me.setModified();
			return false;
		};
		td.appendChild(a);
		tr.appendChild(td);


		formTbody.appendChild(tr);
	},


	removeSearchSpatial: function(formTbody, trLogical, tr) {
		if (trLogical) {
			//先頭になった場合削除されるのでcatch
			try { formTbody.removeChild(trLogical); } catch (e) {}
		}
		formTbody.removeChild(tr);
		var firstTr = formTbody.firstChild;
		if (firstTr && firstTr.className == "logical") {
			formTbody.removeChild(firstTr);
		}
	},


	/**
	 * 検索条件をすべて削除する
	 */
	clearSearchSpatial: function() {
		var formTbody = document.getElementById('search_spatial_form_tbody');
		formTbody.innerHTML = "";
	},




	/**
	 * 一覧で選択した検索履歴をフォームに読み込み
	 */
	loadSearchCondition: function() {
	    var me = this;
		if (!me.isSelectedFilter || me.filterId == 0) {
			alert(me.msg.FilterMsg5);
			return 0;
		}

		var i;
		me.closeConditionListDialog();

		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/getConditionValue/' + me.user + "/" + me.filterId;
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		//検索条件を取得する。
		$.ajax({
			type: "POST",
			url: url,
			async: false,
			success: function(data) {
			    if (data.condition) {
					me.loadConditionValue(data);
			    }
				else {
					if (data.error)
						alert(data.error);
					else
						alert(me.msg.FilterMsg10);
					me.closeSearchDialog();
				}
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
			}
		});

	},

	/**
	 * 検索条件JSON形式を読み込んでダイアログにセットします.
	 * @param {Object} conditionValue 検索条件JSON形式
	 */
	loadConditionValue: function(conditionValue) {
// console.log("*****loadConditionValue");
	    var me = this;
		me.conditionValue = conditionValue;

		//ヒット条件設定
		var hit = document.getElementById("hit");
		if (conditionValue.isnot)
			hit.selectedIndex = 0;
		else
			hit.selectedIndex = 1;

		//属性条件を表示
		var conds = conditionValue.condition;
		var objSearchLayer = document.getElementById("searchLayer");
		var hasSpatialConds = 0;
		var spatialConds = null;

		if (conditionValue.spatiallayer) {
			hasSpatialConds = 1;
			spatialConds = conditionValue.spatiallayer;
		}


		i = 0;
		while (i < objSearchLayer.options.length && objSearchLayer.options[i].value != conditionValue.layerId)
			i++;
		if (i < objSearchLayer.options.length)
			objSearchLayer.options[i].selected = true;

		me.setSearchLayer(conditionValue.layerId, function(){
			me.clearSearchInput();
			for (i = 0; i < conds.length; i++) {
				me.addSearchInput(conds[i].attrId, conds[i].comp, conds[i].value, conds[i].logical);
			}

			//空間条件を表示
			if (hasSpatialConds) {
				for (i = 0; i < spatialConds.length; i++) {
					me.addSearchSpatial(spatialConds[i].layer, spatialConds[i].buffer, spatialConds[i].type, spatialConds[i].logical);
				}
			}
		});

		// フィルタID表示更新
		$("#filter_id_td").text(me.filterId);

	},

	deleteSearchConditions: function() {
		var me = this;
		var selarrow = jQuery("#conditionlist").jqGrid('getGridParam','selarrrow');
		if (selarrow.length == 0) {
			alert(me.msg.FilterMsg5);
			return 0;
		}

		if (confirm(me.msg.FilterMsg6)) {
			var ids = "";

			for (var i = 0; i < selarrow.length; i++) {
				if (ids != "")
					ids += ",";
				ids += selarrow[i];
			}

			// URL を設定する。(なるべく絶対パスで)
			var url = '/filterSetting/deleteConditions/' + me.user;
			if(typeof SaigaiTask=="undefined") url = '..'+url;
			else url = SaigaiTask.contextPath + '/admin' + url;

			//検索対象を取得する。
			$.ajax({
				type: "POST",
				url: url,
				data: {"ids": ids},
				async: false,
				success: function(data) {
					if (data.result) {
						alert(me.msg.FilterMsg7);
						me.getConditionList();
					}
					else {
						if (data.error)
							alert(data.error);
						else
							alert(me.msg.FilterGeneralError);
					}
				},
				error: function(request, text, error) {
					alert(me.msg.FilterGeneralError);
				}
			});
		}
	},


	/**
	 * 保存した検索条件を一覧表示する
	 */
	getConditionList: function() {
		var me = this;
		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/getConditionList/' + me.user + "/" + me.mapId;
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		//検索対象を取得する。
		$.ajax({
			type: "POST",
			url: url,
			async: false,
			success: function(data) {
				//選択状態をリセット
				me.isSelectedFilter = false;
				me.filterId = 0;

				if (data.items) {
//					if (me.isFirstLoadList) {
//						me.isFirstLoadList = 0;
						jQuery("#conditionlist").jqGrid({
							data: data.items, datatype: "local",
							colNames:['ID', lang.__('Retrieval object layer'), lang.__('Save date and time'), lang.__('Implementer'),lang.__('Search condition')],
							colModel:[
								{					  name:'id'			,width:20	, align:'center'},
								{					  name:'layer_id'	,width:70	},
								{					  name:'created'	,width:90	},
								{					  name:'user_id'	,width:45	},
								{					  name:'name'		,width:500, fixed: true	}
							 ],
						 	rowNum : 10,					//一ページに表示する行数
							rowList		: [10, 20, 30],		//変更可能な1ページ当たりの行数
							height		: 'auto',				//高さ
							width		: 910,				//幅
							pager		: 'pager1',			//footerのページャー要素のid
							shrinkToFit	: true,				//画面サイズに依存せず固定の大きさを表示する設定
							viewrecords	: true,				//footerの右下に表示する。
							multiselect	: true,
							sortname	: 'date',
							sortorder	: 'DESC',
							caption		: '',

							 //実行ボタン配置
							gridComplete: function()
							{

							},
							onSelectRow: function(id)
							{
								me.isSelectedFilter = true;
								me.filterId = id;
							},
							onSelectAll: function(aRowids, status)
							{
								if (!status) {	//選択を除外
									me.isSelectedFilter = false;
									me.filterId = 0;
								}
								else {
									if (aRowids.length && aRowids.length > 0) {
										me.isSelectedFilter = true;
										me.filterId = aRowids[aRowids.length - 1];
									}
								}
							}
						});
//					}
//					else {
//						$("#conditionlist").clearGridData();  // jqGridを削除
//						$("#conditionlist").jqGrid('setGridParam', { data: data.items }).trigger('reloadGrid'); // jqridをnewdでreload
//					}
				}
				else {
					if (data.error)
						alert(data.error);
					else
						alert(me.msg.FilterMsg4);
					me.closeSearchDialog();
				}
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
			}
		});
	},


	/**
	 * 検索履歴一覧ダイアログを表示する
	 */
	showConditionListDialog: function() {
		var me = this;
		if (!document.getElementById("ecomModalSearchConditionListDialog")) {	//ダイヤログが2回作成されることを避ける。

			var dialogHtml = '';
			dialogHtml += "<div>";
			dialogHtml += 	"<table id='conditionlist' class='scroll' cellpadding='0' cellspacing='0'></table>";
			dialogHtml += 	"<div id='pager1'></div>";
			dialogHtml += "</div>";

			dialogHtml += 	"<table width='100%' border=0>";
		    dialogHtml += 		"<tr>";
		    dialogHtml += 			"<td align='left' width='140px'>";
			dialogHtml += 				"<div style='padding:3px 3px 0px 0px;'>";
			dialogHtml += 					"<a id='btnLoad' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Search history reading");
		    dialogHtml += 					"<span class='ui-icon ui-icon-disk'></span></a>";
		    dialogHtml += 				"</div>";
		    dialogHtml += 			"</td>";
		    dialogHtml += 			"<td align='left'>";
			dialogHtml += 				"<div style='padding:3px 3px 0px 0px;'>";
			dialogHtml += 					"<a id='btnDeleteConditions' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Delete");
		    dialogHtml += 					"<span class='ui-icon ui-icon-trash'></span></a>";
		    dialogHtml += 				"</div>";
		    dialogHtml += 			"</td>";

		    dialogHtml += 			"<td align='right' width='50%'>";
			dialogHtml += 				"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
			dialogHtml += 					"<a id='btnCloseList' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Close");
		    dialogHtml += 					"<span class='ui-icon ui-icon-close'></span></a>";
		    dialogHtml += 				"</div>";
		    dialogHtml += 			"</td>";
		    dialogHtml += 		"</tr>";
		    dialogHtml += 	"</table>";

			//ダイアログ作成
			$.jgrid.createModal(
				IDs = {
					themodal:'ecomModalSearchConditionListDialog',
					modalhead:'ecomModalSearchConditionListDialogHd',
					modalcontent:'ecomModalSearchConditionListDialogCnt',
					scrollelm : ''
				},
				dialogHtml,
				{
					gbox:"#gbox_editmodmapmaster_info",
					jqModal:true,
					drag:true,
					resize:false,
					caption:lang.__('Search history reading'),
					top:100,
					left:100,
					width:920,
					height: 'auto',
					closeOnEscape:true,
					zIndex:2000
				},
				'',
				'',
				true
			);

			$("#btnLoad").click(function() {me.loadSearchCondition();});
			$("#btnDeleteConditions").click(function() {me.deleteSearchConditions();});
			$("#btnCloseList").click(function() {me.closeConditionListDialog();});
		}

		me.getConditionList();

		//ダイアログの表示
		$.jgrid.viewModal("#ecomModalSearchConditionListDialog",{modal:true});
		$("#ecomModalSearchConditionListDialog").focus();
	},

	/**
	 * 検索履歴読み込み一覧ダイアログを閉じる
	 */
	closeConditionListDialog: function() {
		$.jgrid.hideModal('#ecomModalSearchConditionListDialog');
		// ダイアログの構成要素を削除しておく
		$("#ecomModalSearchConditionListDialog").remove();
	},




	/**
	 * 検索条件をサーバへ（AJAX）送信し、検索履歴に挿入する。
	 */
	insertSearchCondition: function(data) {
		var me = this;
		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/insertCondition';
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		$.ajax({
			type: "POST",
			url: url,
			//dataType: "text",
			data: data,
			async: false,
			success: function(data) {
				if (data.id) {
					me.filterId = data.id;
					alert(me.msg.FilterMsg9);
//					$.jgrid.hideModal('#ecomModalSearchConditionSaveDialog',{jqm:true});
					me.isSaved = true;
					me.isModified = false;
					me.closeConditionSaveDialog();

				}
				else {
					alert("ERROR:" + data.error);
				}
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
			}
		});
	},

	/**
	 * 検索条件をサーバへ（AJAX）送信し、検索履歴を更新する。
	 */
	updateSearchCondition: function(data) {
		var me = this;
		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/updateCondition';
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		$.ajax({
			type: "POST",
			url: url,
			//dataType: "text",
			data: data,
			async: false,
			success: function(data) {
				if (data.id) {
					me.filterId = data.id;
					alert(me.msg.FilterMsg9);
//					$.jgrid.hideModal('#ecomModalSearchConditionSaveDialog',{jqm:true});
					me.isSaved = true;
					me.isModified = false;
					me.closeConditionSaveDialog();
				}
				else {
					alert("ERROR:" + data.error);
				}
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
			}
		});
	},

	/**
	 * 検索条件JSON形式を取得します.
	 */
	getSearchCondition: function() {
		var me = this;
		var searchCondition = {
			"mapId": me.mapId,
			"user": me.user,
			"layer": me.searchLayerId,
			"keywords": me.getSearchKeywords(document.forms.search_dialog_attr),
			"isnot": document.getElementById("hit").value,
			"buffer": 0,
			"spatial": 0,
			"spatialLayer": me.getSpatialSearchJson(document.forms.search_dialog_attr)
		};

		var nameEl = document.getElementById("search_cond_name");
		if(nameEl!=null) {
			searchCondition["name"] = nameEl.value;
		}

		return searchCondition;
	},

	/**
	 * 検索条件保存ダイアログを表示
	 */
	showConditionSaveDialog: function() {
		var me = this;
		if (!document.getElementById("ecomModalSearchConditionSaveDialog")) {	//ダイヤログが2回作成されることを避ける。

			if (!me.hasCondition(document.forms.search_dialog_attr)) {
				alert(me.msg.FilterMsg8);
				return 0;
			}

			var dialogHtml = '';
			dialogHtml += "<div style='width:100%;background-color:#eee;border:1px solid #ddd;padding:3px;'>";
			dialogHtml += lang.__("Retrieval object layer") + ":<span id='spanTargetLayer'></span>";
			dialogHtml += "";
			dialogHtml += "</div>";
			dialogHtml += lang.__("Search condition name") + ":<br/>";
//			dialogHtml += "<textarea id='name' name='name' rows=5 style='width:550px;'>";
			dialogHtml += "<textarea id='search_cond_name' name='search_cond_name' rows=5 style='width:550px;'>";
			dialogHtml += "</textarea>";

			dialogHtml += 	"<table width='100%' border=0>";
		    dialogHtml += 		"<tr>";
		    dialogHtml +=			"<td align='left' ><table><tr>";
		    dialogHtml += 			"<td align='left'>";
			dialogHtml += 				"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
			dialogHtml += 				"<a id='btnSaveAdd' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Add new");
		    dialogHtml += 				"<span class='ui-icon ui-icon-disk'></span></a>";
		    dialogHtml += 			"</div>";
		    dialogHtml += 			"</td>";
		    dialogHtml += 			"<td align='left'>";
			dialogHtml += 				"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
			dialogHtml += 				"<a id='btnSaveUpdate' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Update<!--2-->");
		    dialogHtml += 				"<span class='ui-icon ui-icon-disk'></span></a>";
		    dialogHtml += 			"</div>";
		    dialogHtml += 			"</td>";
		    dialogHtml +=			"</tr></table></td>";

//		    dialogHtml += 			"<td align='left' width='25%'>";
//			dialogHtml += 				"<div style='padding:3px 3px 0px 0px;'>";
//			dialogHtml += 				"<a id='btnSaveAdd' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>新規追加";
//		    dialogHtml += 				"<span class='ui-icon ui-icon-disk'></span></a>";
//		    dialogHtml += 			"</div>";
//		    dialogHtml += 			"</td>";
//		    dialogHtml += 			"<td align='left' width='25%'>";
//			dialogHtml += 				"<div style='padding:3px 3px 0px 0px;'>";
//			dialogHtml += 				"<a id='btnSaveUpdate' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>更新";
//		    dialogHtml += 				"<span class='ui-icon ui-icon-disk'></span></a>";
//		    dialogHtml += 			"</div>";
//		    dialogHtml += 			"</td>";
//		    dialogHtml += 			"<td align='left' width='50%'>";
		    dialogHtml += 			"<td align='right' width='50%'>";
			dialogHtml += 				"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
			dialogHtml += 					"<a id='btnCloseSave' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Close");
		    dialogHtml += 					"<span class='ui-icon ui-icon-close'></span></a>";
		    dialogHtml += 				"</div>";
		    dialogHtml += 			"</td>";
		    dialogHtml += 		"</tr>";
		    dialogHtml += 	"</table>";


			//ダイアログ作成
			$.jgrid.createModal(
				IDs = {
					themodal:'ecomModalSearchConditionSaveDialog',
					modalhead:'ecomModalSearchConditionSaveDialogHd',
					modalcontent:'ecomModalSearchConditionSaveDialogCnt',
					scrollelm : ''
				},
				dialogHtml,
				{
					gbox:"#gbox_editmodmapmaster_info",
					jqModal:true,
					drag:true,
					resize:false,
					caption:lang.__('Save search conditions'),
					top:100,
					left:100,
					width:600,
					height: 'auto',
					closeOnEscape:true,
					zIndex:2000
				},
				'',
				'',
				true
			);

			$("#btnSaveAdd").click(function() {
				var searchCondition = me.getSearchCondition();
				me.insertSearchCondition(searchCondition);
			});
			$("#btnSaveUpdate").click(function() {
				if(! $("#btnSaveUpdate").attr('disabled')){
					var searchCondition = me.getSearchCondition();
					searchCondition["filterid"] = $("#filter_id_td").text();
					me.updateSearchCondition(searchCondition);
				}
			});
			$("#btnCloseSave").click(function() {me.closeConditionSaveDialog();});

		}

		var nowShowFilterid = $("#filter_id_td").text();
		if(typeof nowShowFilterid === "undefined" || nowShowFilterid == null || nowShowFilterid <= 0 || nowShowFilterid === ""){
			$("#btnSaveUpdate").attr('disabled', true);
			$("#btnSaveUpdate").attr('class','fm-button ui-state-disabled ui-corner-all fm-button-icon-left');
		}else{
			$('#btnSaveUpdate').attr('disabled', false);
			$('#btnSaveUpdate').removeAttr('disabled');
			$("#btnSaveUpdate").attr('class','fm-button ui-state-default ui-corner-all fm-button-icon-left');
		}

		var keywords = me.getSearchKeywords(document.forms.search_dialog_attr);
		var spatialKeywords = me.getSpatialSearchKeywords(document.forms.search_dialog_attr);

		//検索対象レイヤ表示
		var objSearchLayer = document.getElementById("searchLayer");
		document.getElementById("spanTargetLayer").innerHTML = "";
		document.getElementById("spanTargetLayer").innerHTML = objSearchLayer.options[objSearchLayer.selectedIndex].text;

		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/renderSearchName';
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		//AJAXを呼び出して検索の名前を生成する
		$.ajax({
			type: "POST",
			url: url,
			dataType: "text",
			type: "POST",
			data: {"layer": me.searchLayerId, "keywords": keywords, "buffer": 0, "spatial": 0},
			async: false,
			success: function(data) {
// console.log($("#name").size());
// console.log(data);
// console.log(spatialKeywords);
				var i;
				var objName = document.getElementById("name");
				var objName = $("#search_cond_name");

				if (data != "" || spatialKeywords != ""){
// console.log("*****");
// console.log(objName);
//					objName.value = "(" + data + spatialKeywords + ")";
					$(objName).val("(" + data + spatialKeywords + ")");
				}else{
					objName.value = lang.__("(Filter)");
				}
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
			}
		});

		// フィルター条件表示対応
		var scn_val = $("#search_cond_name").val();
		// 二重追加の防止
		scn_val = scn_val.replace(lang.__("(To gray out hit in the filter conditions)"), "");
		scn_val = scn_val.replace(lang.__("(To gray out do not hit in the filter conditions)"), "");
		var isnot = document.getElementById("hit").value;
		if(isnot === '1'){
			scn_val = scn_val + lang.__("(To gray out hit in the filter conditions)");
		}else{
			scn_val = scn_val + lang.__("(To gray out do not hit in the filter conditions)");
		}
		$("#search_cond_name").val(scn_val);

		//ダイアログの表示
		$.jgrid.viewModal("#ecomModalSearchConditionSaveDialog",{modal:true});
		$("#ecomModalSearchConditionSaveDialog").focus();
	},

	/**
	 * 検索条件保存ダイアログを閉じる
	 */
	closeConditionSaveDialog: function() {
		$.jgrid.hideModal('#ecomModalSearchConditionSaveDialog',{jqm:true});
		var ecomModalSearchConditionSaveDialog = document.getElementById("ecomModalSearchConditionSaveDialog");
		if (ecomModalSearchConditionSaveDialog) {
			ecomModalSearchConditionSaveDialog.parentNode.removeChild(ecomModalSearchConditionSaveDialog);
		}
	},

	/**
	 * 入力した検索条件で検索を実行する
	 */
	filter: function() {
		var me = this;
		// URL を設定する。(なるべく絶対パスで)
		var url = '/map/filter';
		if(typeof SaigaiTask=="undefined") url = '../../page'+url;
		else url = SaigaiTask.contextPath + '/page' + url;

		// パラメータ作成
		var data = me.getSearchCondition();
		try{
			data["menuid"] = SaigaiTask.Page.menuInfo.id;
		}catch(e) {
			// do nothing
		}

		$.ajax({
			type: "POST",
			url: url,
			//dataType: "text",
			data: data,
			async: false,
			success: function(data) {
				//alert(JSON.stringify(data));
				me.events.trigger("filter", [data]);
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
			}
		});
	},

	submitFilterKey: function() {
		var me = this;
		if (me.filterId == 0) {
			alert(me.msg.FilterMsg1);
			return 0;
		}
		else {
			if(me.isModified && (! me.isSaved ) ){
				alert(lang.__("The search condition is changed. Please press a preservation button first."));
				return 0;
			}else{
				$(me.filterIdElementId).val(me.filterId);
//				$.jgrid.hideModal('#ecomModalConditionDialog',{jqm:true});
				me.closeSearchDialog();
			}
		}
	},

	/**
	 * ダイヤログを閉じる
	 */
	closeSearchDialog: function() {
		var me = this;
		me.isModified = false;
		me.isSaved = false;

		$.jgrid.hideModal('#ecomModalConditionDialog',{jqm:true});

		var ecomModalConditionDialog = document.getElementById("ecomModalConditionDialog");
		if (ecomModalConditionDialog) {     //ダイヤログが2回作成されることを避ける。
			ecomModalConditionDialog.parentNode.removeChild(ecomModalConditionDialog);
		}
	},

	switchAttrTab: function() {
		var me = this;
		me.tab = "attr";

		document.getElementById("divAttrTab").style.display = "block";
		document.getElementById("btnAttrTab").style.backgroundColor = "#87b6d9";

		document.getElementById("divSpatialTab").style.display = "none";
		document.getElementById("btnSpatialTab").style.backgroundColor = "#eaf4fd";
	},

	switchSpatialTab: function() {
		var me = this;
		me.tab = "spatial";

		document.getElementById("divAttrTab").style.display = "none";
		document.getElementById("btnAttrTab").style.backgroundColor = "#eaf4fd";

		document.getElementById("divSpatialTab").style.display = "block";
		document.getElementById("btnSpatialTab").style.backgroundColor = "#87b6d9";
	},


	/**
	 * 設定画面のメニュー情報からフィルタダイアログを呼び出す.
	 */
	calledBymenuInfo: function(menutaskinfoid, filteridCellId) {
	    return this.show({
			taskId: menutaskinfoid,
			filterIdElementId: filteridCellId,
			modal: true,
			decideButton: true
		});
	},

	setModified: function() {
		var me = this;
		me.isModified = true;
	},

	/**
	 * フィルタダイアログを表示する.
	 * 検索条件の読込は、以下の方法があります。
	 * 1. フィルタID（input要素ID）を指定する
	 * 2. conditionValue を直接渡す
	 * @param {Object} option
	 * @param {String} filterIdElementId value属性にフィルタIDが入っている要素ID
	 * @param {Number} filterId フィルタID
	 * @param {Object} conditionValue 検索条件オブジェクト
	 * @param {Number} taskId メニュータスクID
	 * @param {Boolean} modal モーダル表示 (option) default true
	 *        ダイアログの ui-widget-overlay は消えないので注意すること。
	 * @param {Boolean} closeButton 閉じるボタン
	 * @param {Boolean} loadButton 読み込み・保存ボタン
	 * @param {Boolean} searchButton 検索ボタン
	 * @param {Boolean} decideButton 決定し閉じるボタン
	 */
	show: function(option) {
		var me = this;
		option = $.extend({
			modal: true,
			conditionValue: null,
			closeButton: true,
			loadButton: true,
			searchButton: false,
			decideButton: false
		}, option);
		me.filterIdElementId = option.filterIdElementId;
		me.filterId = option.filterId;
		me.taskId = option.taskId;
		var modal = option.modal;
		var conditionValue = conditionValue = option.conditionValue;
//		alert(conditionValue);

//		if (!document.getElementById("ecomModalConditionDialog")) {	//ダイヤログが2回作成されることを避ける。
		var ecomModalConditionDialog = document.getElementById("ecomModalConditionDialog");
		if (ecomModalConditionDialog) {     //ダイヤログが2回作成されることを避ける。
			ecomModalConditionDialog.parentNode.removeChild(ecomModalConditionDialog);
		}
			var dialogHtml = '';
			dialogHtml += "<form id='search_dialog_attr' name='search_dialog_attr' style='width:100%;' onsubmit='return false;'>";
			dialogHtml += "<table width='100%' border=0>";

//			dialogHtml += 	"<tr>";
//			dialogHtml += 		"<td width='20%'>";
//			dialogHtml += 			"フィルターID：";
//			dialogHtml += 		"</td>";
//			dialogHtml += 		"<td id = 'filter_id_td' width='35%' align='left'>";
//			dialogHtml += 		"</td>";
//			dialogHtml += 		"<td></td>";
//			dialogHtml += 	"</tr>";

			dialogHtml += 	"<tr>";
			dialogHtml += 		"<td width='15%'>";
			dialogHtml += 			lang.__("Retrieval object:");
			dialogHtml += 		"</td>";
			dialogHtml += 		"<td width='35%' align='right'>";
			dialogHtml += 			"<select id='searchLayer' style='width:200px;'>";
			dialogHtml += 			"</select>";
			dialogHtml += 		"</td>";
			dialogHtml += 		"<td width='20%'>";
			dialogHtml += 			lang.__("Filter ID:");
			dialogHtml += 		"</td>";
			dialogHtml += 		"<td id = 'filter_id_td' width='20%' align='left'>";
			dialogHtml += 		"</td>";
			dialogHtml += 	"</tr>";

			dialogHtml += 	"<tr>";
			dialogHtml += 		"<td></td>";
			dialogHtml += 		"<td align='left' colspan='3'>";
			dialogHtml += 			lang.__("Filter conditions:");
			dialogHtml += 			"<select id='hit' style='width:120px;'>";
			dialogHtml += 				"<option value=1>" + lang.__("Selected") + "</option>";
			dialogHtml += 				"<option value=0>" + lang.__("Not selected") + "</option>";
			dialogHtml += 			"</select>";
			dialogHtml += 			lang.__(" to gray out");
			dialogHtml += 		"</td>";
			dialogHtml += 	"</tr>";
			dialogHtml += "</table>";

//			dialogHtml += 	"<tr>";
//			dialogHtml += 		"<td width='15%'>";
//			dialogHtml += 			"検索対象：";
//			dialogHtml += 		"</td>";
//			dialogHtml += 		"<td width='35%' align='right'>";
//			dialogHtml += 			"<select id='searchLayer' style='width:200px;'>";
//			dialogHtml += 			"</select>";
//			dialogHtml += 		"</td>";
//			dialogHtml += 		"<td></td>";
//			dialogHtml += 	"</tr>";
//
//			dialogHtml += 	"<tr>";
//			dialogHtml += 		"<td></td>";
//			dialogHtml += 		"<td align='right'>";
//			dialogHtml += 			"フィルタ条件に：";
//			dialogHtml += 			"<select id='hit' style='width:120px;'>";
//			dialogHtml += 				"<option value=1>ヒットした方</option>";
//			dialogHtml += 				"<option value=0>ヒットしない方</option>";
//			dialogHtml += 			"</select>";
//			dialogHtml += 		"</td>";
//			dialogHtml += 		"<td align='left'>";
//			dialogHtml += 			"をグレーアウトにする";
//			dialogHtml += 		"</td>";
//			dialogHtml += 	"</tr>";
//			dialogHtml += "</table>";

			//--------------- Tab buttons ---------------
			dialogHtml += 	"<table style='z-index:200;'><tr><td>"
			dialogHtml += 		"<a id='btnAttrTab' style='background-color:#87b6d9;border:1px solid #c5dbec' class='fm-button ui-corner-top'  href='javascript:void(0)'>" + lang.__("Attribute search");
	     	dialogHtml += 	"</td>";

	     	dialogHtml += 	"<td>";
			dialogHtml += 		"<a id='btnSpatialTab' style='background-color:#eaf4fd;border:1px solid #c5dbec' class='fm-button ui-corner-top' href='javascript:void(0)'>" + lang.__("Spatial search");
	     	dialogHtml += 	"</td></tr></table>";


	     	//----------- Tabs ------------
			dialogHtml += "<div style='background-color:#fff;margin-top:-4px;width:100%;border:1px solid #c5dbec;padding:3px;z-index:205;'>";
			dialogHtml += "<div id='divAttrTab'>";
			dialogHtml += 		"<div style='width:100%;background-color:#eee;border:1px solid #ddd;padding:3px;'>";
			dialogHtml += 		lang.__("Attribute selection");
			dialogHtml += 		"<select id='search_attr_select' style='width:200px;'></select>";

			dialogHtml += 		"<a id='btnAddAttr' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Add search condition");
	     	dialogHtml += 		"<span class='ui-icon ui-icon-plus'></span></a>";
			dialogHtml += 		"</div>";


			dialogHtml += 		"<table class='search_attr_table' style='empty-cells:show;'>";
			dialogHtml += 		"<tbody id='search_attr_form_tbody'></tbody>";
			dialogHtml += 		"</table>";

			dialogHtml += 		"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
			dialogHtml += 			"<a id='btnDeleteAllAttr' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Delete all");
	     	dialogHtml += 			"<span class='ui-icon ui-icon-trash'></span></a>";
	     	dialogHtml += 		"</div>";
	     	dialogHtml += "</div>";


	     	dialogHtml += "<div id='divSpatialTab' style='display:none;'>";
	     	dialogHtml += 		"<a id='btnAddSpatial' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Add<!--2-->");
	     	dialogHtml += 		"<span class='ui-icon ui-icon-plus'></span></a>";


			dialogHtml += 		"<table class='search_spatial_table' style='empty-cells:show;'>";
			dialogHtml += 		"<tbody id='search_spatial_form_tbody'></tbody>";
			dialogHtml += 		"</table>";

			dialogHtml += 		"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
			dialogHtml += 			"<a id='btnDeleteAllSpatial' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Delete all");
	     	dialogHtml += 			"<span class='ui-icon ui-icon-trash'></span></a>";
	     	dialogHtml += 		"</div>";
	     	dialogHtml += "</div>";

	     	dialogHtml += "</div>";


	     	dialogHtml += 	"<table width='100%' border=0>";
	     	dialogHtml += 		"<tr>";
			// 左側ボタン
			dialogHtml += 			"<td align='left' width='50%'>";
			dialogHtml += 				"<table>";
			dialogHtml += 					"<tr>";
			if(option.loadButton) {
				// 「読み込み」
				dialogHtml += 						"<td align='left'>";
				dialogHtml += 							"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
				dialogHtml += 								"<a id='btnLoadDialog' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Load");
				dialogHtml += 								"<span class='ui-icon ui-icon-arrow-1-n'></span></a>";
				dialogHtml += 							"</div>";
				dialogHtml += 						"</td>";
				// 「保存」
				dialogHtml += 						"<td align='left'>";
				dialogHtml += 							"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
				dialogHtml += 								"<a id='btnSaveDialog' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Save");
				dialogHtml += 								"<span class='ui-icon ui-icon-disk'></span></a>";
				dialogHtml += 							"</div>";
				dialogHtml += 						"</td>";
			}
			dialogHtml += 					"</tr>";
			dialogHtml += 				"</table>";
			dialogHtml += 			"</td>";

			// 右側のボタン
			dialogHtml += 			"<td align='right'>";
			dialogHtml += 				"<table>";
			dialogHtml += 					"<tr>";
			if(option.searchButton) {
				// 「検索」
				dialogHtml += 						"<td align='left'>";
				dialogHtml += 							"<div style='text-align:right; padding:3px 3px 0px 0px;' onmousehover='jqGridButton_onhover()'>";
				dialogHtml += 								"<a id='btnFilter' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Search");
				dialogHtml += 								"<span class='ui-icon ui-icon-play'></span></a>";
				dialogHtml += 							"</div>";
				dialogHtml += 						"</td>";
			}
			if(option.decideButton) {
				// 「決定し閉じる」
				dialogHtml += 						"<td align='left'>";
				dialogHtml += 							"<div style='text-align:right; padding:3px 3px 0px 0px;' onmousehover='jqGridButton_onhover()'>";
				dialogHtml += 								"<a id='btnSubmit' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Confirm and close");
				dialogHtml += 								"<span class='ui-icon ui-icon-play'></span></a>";
				dialogHtml += 							"</div>";
				dialogHtml += 						"</td>";
			}
			if(option.closeButton){
				// 「閉じる」
				dialogHtml += 						"<td align='left'>";
				dialogHtml += 							"<div style='text-align:right; padding:3px 3px 0px 0px;'>";
				dialogHtml += 								"<a id='btnClose' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'>" + lang.__("Close");
				dialogHtml += 								"<span class='ui-icon ui-icon-close'></span></a>";
				dialogHtml += 							"</div>";
				dialogHtml += 						"</td>";
			}
			dialogHtml += 					"</tr>";
			dialogHtml += 				"</table>";
			dialogHtml += 			"</td>";

	     	dialogHtml += 		"</tr>";
	     	dialogHtml += 	"</table>";

	     	dialogHtml += "</form>";




			//ダイアログ作成
			$.jgrid.createModal(
				IDs = {
					themodal:'ecomModalConditionDialog',
					modalhead:'ecomModalConditionDialogHd',
					modalcontent:'ecomModalConditionDialogCnt',
					scrollelm : ''
				},
				dialogHtml,
				{
					gbox:"#gbox_editmodmapmaster_info",
					jqModal:true,
					drag:true,
					resize:false,
					caption:lang.__('Filter settings'),
					top:100,
					left:100,
					width:600,
					height: 'auto',
					closeOnEscape:true,
					zIndex:1000
				},
				'',
				'',
				true
			);

			// フィルターIDの初回表示
			$("#filter_id_td").text($(me.filterIdElementId).val());

			//タブボタン
			$("#btnAttrTab").click(function() {me.switchAttrTab();});
			$("#btnSpatialTab").click(function() {me.switchSpatialTab();});


			//----------- 属性検索タブ ----------
			//属性を追加する。
			$("#btnAddAttr").click(function() {me.addSearchInput(document.getElementById("search_attr_select").value); me.isModified = true;});
			//属性を追加する。
			$("#btnDeleteAllAttr").click(function() {me.clearSearchInput(); me.isModified = true;});


			//----------- 空間検索タブ ----------
			//属性を追加する。
			$("#btnAddSpatial").click(function() {me.addSearchSpatial(); me.isModified = true;});
			//属性を追加する。
			$("#btnDeleteAllSpatial").click(function() {me.clearSearchSpatial(); me.isModified = true;});

			//検索対象が変わるときに属性リストを再読み込み。
			//$("#searchLayer").change(setSearchLayer);
			//$("#searchLayer").change(function(){me.setSearchLayer();});

			$("#btnLoadDialog").click(function() {me.showConditionListDialog()});
			$("#btnSaveDialog").click(function() {me.showConditionSaveDialog();});

			$("#btnFilter").click(function() {me.filter();});
			$("#btnSubmit").click(function() {me.submitFilterKey();});
			$("#btnClose").click(function() {me.closeSearchDialog();});

			$("#searchLayer").change(function() {me.setModified();});
			$("#hit").change(function() {me.setModified();});
			$("#search_attr_select").change(function() {me.setModified();});
			$(".search_attr").change(function() {me.setModified();});


//		}

		//データをリセット
		document.getElementById("hit").selectedIndex = 0;
		me.clearSearchInput();
		me.clearSearchSpatial();
		me.switchAttrTab();

		// URL を設定する。(なるべく絶対パスで)
		var url = '/filterSetting/getUserMapIds/' + me.taskId;
		if(typeof SaigaiTask=="undefined") url = '..'+url;
		else url = SaigaiTask.contextPath + '/admin' + url;

		//ダイアログの表示
		$.jgrid.viewModal("#ecomModalConditionDialog",
			{modal:modal}
		);
		$("#ecomModalConditionDialog").focus();
		me.dialog = $("#ecomModalConditionDialog");
		me.dialog.mask(lang.__("Now loading..<!--3-->"));

		//検索対象を取得する。
		$.ajax({
			type: "POST",
			url: url,
			async: false,
			success: function(data) {
				if (data.result != 1) {
					if (data.error)
						alert(data.error);
					else
						alert(me.msg.FilterMsg2);
					me.closeSearchDialog();
					me.dialog.unmask();
				}
				else {
					me.user = data.ecomuser;
					me.mapId = data.mapid;

					// URL を設定する。(なるべく絶対パスで)
					var url = '/filterSetting/getLayerList/' + me.user + "/" + me.mapId;
					if(typeof SaigaiTask=="undefined") url = '..'+url;
					else url = SaigaiTask.contextPath + '/admin' + url;

					//検索対象を取得する。
					$.ajax({
						type: "POST",
						url: url,
						async: false,
						success: function(data) {
							if (data.error) {
								alert(data.error);
								me.dialog.unmask();
								me.closeSearchDialog();
								return;
							}

							var i;
							var objSearchLayer = document.getElementById("searchLayer");

							me.layerList = [];
							clearCombo(objSearchLayer);
							for (i = 0; i < data.items.length; i++) {
								addTextvalue2Combo(objSearchLayer, data.items[i].name, data.items[i].layerid);
								me.layerList.push(data.items[i]);
							}

// console.log("*****show");
							me.setSearchLayer(objSearchLayer.value);
//							objSearchLayer.onchange = function() {me.setSearchLayer(this.value);};
							objSearchLayer.onchange = function() {
								me.dialog.mask(lang.__("Now loading..<!--3-->"));
								me.setSearchLayer(this.value);
								me.dialog.unmask();
							};

							if(conditionValue==null) {
								// フィルタキーの要素がある場合は、その要素の値から取得する
								if(typeof me.filterIdElementId=="string") {
									var id = $(me.filterIdElementId).val();
									if (id.trim() != "") {
										me.filterId = id.trim();
									}
								}
								//フィルタキーが既に選択された場合は、フィルタ設定を読み込みます
								if(me.filterId!=null) {
								    me.isSelectedFilter = true;
								    me.loadSearchCondition();
								    // console.log("*** loaded");
								}
							}
							else {
								me.loadConditionValue(conditionValue);
							}
							me.dialog.unmask();
						},
						error: function(request, text, error) {
							alert(me.msg.FilterGeneralError);
							me.dialog.unmask();
						}
					});
				}
			},
			error: function(request, text, error) {
				alert(me.msg.FilterGeneralError);
				me.dialog.unmask();
			}
		});

	}
};
