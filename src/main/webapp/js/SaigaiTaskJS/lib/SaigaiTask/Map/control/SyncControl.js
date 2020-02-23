/**
 * 複数地図の同期を制御するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.SyncControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * 中心位置を同期するかどうかのフラグ
	 * @type {Boolean}
	 */
	syncCenter: true,

	/**
	 * 縮尺を同期するかどうかのフラグ
	 * @type {Boolean}
	 */
	syncZoom: true,

	/**
	 * 同期グループ
	 * @type {Array<Map>}
	 */
	group: null,

	/**
	 * 同期状態
	 * @type {Object}
	 */
	state: null,

	initialize: function(stmap) {
		var me = this;
		me.stmap = stmap;
		me.group = [stmap];
		me.state = {
			/** 移動中 */
			moving: false
		};

		var olmap = stmap.map;
		olmap.events.on({
			"move": function() {
				me.moveEnd();
			},
			"zoomend": function() {
				me.zoomEnd();
			}
		});

		OpenLayers.Control.prototype.initialize.apply(this, []);
	},

	/**
	 * グループをマージして1つにします
	 * @param {SaigaiTask.Map.control.SyncControl}
	 */
	mergeGroup: function(syncControl) {
		var me = this;
		var concatGroup = me.group.concat(syncControl.group);
		me.group = syncControl.group = concatGroup;
		syncControl.state = me.state;
	},

	/**
	 * グループから取り除きます
	 * @param {SaigaiTask.Map.control.SyncControl}
	 */
	removeGroup: function(syncControl) {
		var me = this;
		me.group = jQuery.grep(me.group, function(value) {
			return value != syncControl.stmap;
		});
	},

	/**
	 * 地図移動時のハンドラです.
	 */
	moveEnd: function() {
		var me = this;
		var state = me.state;
		if(me.active && state.moving==false) {
			state.moving = true;
			//console.log("move end");
			var group = me.group;
			for(var idx in group) {
				var stmap = group[idx];
				// 動かした地図自身なら同期処理はやる必要がない.
				if(me.stmap==stmap) continue;
				// 中心位置を同期
				var center = me.stmap.getCenter();
				//console.log(center);
				stmap.setCenter(center);
				//console.log(stmap.getCenter());
			}
			state.moving = false;
		}
	},

	/**
	 * 地図移動時のハンドラです.
	 */
	zoomEnd: function() {
		var me = this;
		var state = me.state;
		if(me.active && state.moving==false) {
			state.moving = true;
			var group = me.group;
			for(var idx in group) {
				var stmap = group[idx];
				// 動かした地図自身なら同期処理はやる必要がない.
				if(me.stmap==stmap) continue;
				var center = me.stmap.getCenter();
				// ズームレベルを同期
				// resolution(解像度)でやるべき？
				var zoom = me.stmap.map.getZoom();
				stmap.setCenter(center, zoom);
			}
			state.moving = false;
		}
	}
});