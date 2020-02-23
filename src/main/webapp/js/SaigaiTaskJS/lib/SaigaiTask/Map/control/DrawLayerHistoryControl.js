/**
 * DrawLayer の地物の履歴を管理するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.DrawLayerHistoryControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * @type {SaigaiTask.Map.Layer.DrawLayer}
	 */
	drawLayer: null,

	/**
	 * 履歴情報のスタック
	 * @type {Array<Object>}
	 */
	stack: null,

	/**
	 * undo, redo したときの履歴位置を保持する
	 * 空のときは -1 で、最新のときは配列最後のインデックスを指す
	 * @type {Number}
	 */
	stackIndex: null,

	/**
	 * true にセットすると履歴の記録をしない
	 * @type {Boolean}
	 */
	ignoreSave: null,

	initialize: function(drawLayer) {
		var me = this;
		me.drawLayer = drawLayer;
		me.clear();
		me.ignoreSave = true;

		// 描画レイヤのイベントで履歴を保存する
		var layer = drawLayer.layer;
		layer.events.on({
			"featureadded": function(evt) {
				if(!me.ignoreSave) {
					me.save({
						type: evt.type,
						operation: "add",
						feature: evt.feature
					});
				}
			},
			"featureremoved": function(evt) {
				if(!me.ignoreSave) {
					me.save({
						type: evt.type,
						operation: "remove",
						feature: evt.feature
					});
				}
			},
			"featuremodified": function(evt) {
				if(!me.ignoreSave) {
					me.save({
						type: evt.type,
						operation: "modify",
						feature: evt.feature,
						beforeFeature: evt.beforeFeature,
						afterFeature: evt.afterFeature
					});
				}
			},
			// feature moved は dragFeatureControl のイベントをカスタマイズしてある
			"featuremoved": function(evt) {
				if(!me.ignoreSave) {
					me.save({
						type: evt.type,
						operation: "modify",
						feature: evt.feature,
						beforeFeature: evt.beforeFeature,
						afterFeature: evt.afterFeature
					});
				}
			}
		});
	},


	/**
	 * 履歴の記録を開始
	 */
	startHistory: function() {
		this.ignoreSave = false;
	},
	/**
	 * 履歴の記録を停止
	 */
	stopHistory: function() {
		this.ignoreSave = true;
	},
	/**
	 * 履歴の記録をクリア
	 */
	clear: function() {
		var me = this;
		me.stack = [];
		me.stackIndex = me.stack.length-1;
		console.debug("clear: -1");
		me.drawLayer.layer.events.triggerEvent("historychange", {
			historyControl: me,
			operation: "clear"
		});
	},
	/**
	 * 履歴を記録する
	 * @param {Object} info
	 * @param {String} info.type イベントタイプ
	 * @param {String} info.operation 操作種別(add/remove/modify) (required)
	 * @param {OpenLayers.Feature.Vector} info.feature 操作対象のフィーチャ (required)
	 * @param {OpenLayers.Feature.Vector} info.beforeFeature 操作前のフィーチャ(状態保存用) modify の場合
	 * @param {OpenLayers.Feature.Vector} info.afterFeature 操作後のフィーチャ(状態保存用) modify の場合
	 */
	save: function(info) {
		var me = this;

		// 元に戻している場合は、それ以降の履歴を消す
		if(me.stackIndex!=me.stack.length-1) {
			if(0<me.stackIndex) me.stack = me.stack.slice(0, me.stackIndex+1);
			else me.stack = [];
		}

		// 履歴の記録
		me.stack.push(info);
		me.stackIndex = me.stack.length-1;
		console.debug("save: "+me.stackIndex);
		me.drawLayer.layer.events.triggerEvent("historychange", {
			historyControl: me,
			operation: "save"
		});
	},

	/**
	 * 1つ元に戻す
	 */
	undo: function() {
		var me = this;
		if(0<=me.stackIndex) {
			var undoIndex = me.stackIndex;
			var info = me.stack[undoIndex];
			var layer = me.drawLayer.layer;

			// 履歴の記録を停止
			me.stopHistory();

			// undo
			switch(info.operation) {
			case "add":
				// 追加されたものを削除
				layer.removeFeatures([info.feature]);
				break;
			case "remove":
				// 削除されたものを追加
				layer.addFeatures([info.feature]);
				break;
			case "modify":
				// 編集されたものを戻す
				// 一旦削除
				layer.removeFeatures([info.feature]);
				// ジオメトリ変更
				info.feature.geometry = info.beforeFeature.geometry.clone();
				// Style変更
				info.feature.style = $.extend({}, info.beforeFeature.style);
				// attributes変更
				info.feature.attributes = $.extend({}, info.beforeFeature.attributes);
				// 再度追加
				layer.addFeatures([info.feature]);
				break;
			}

			// 履歴の記録を開始
			me.startHistory();

			me.stackIndex--;
			console.debug("undo: "+me.stackIndex+"/"+(me.stack.length-1));
			me.drawLayer.layer.events.triggerEvent("historychange", {
				historyControl: me,
				operation: "undo",
				feature: info.feature
			});
			return true;
		}
		return false;
	},

	/**
	 * 1つやり直す
	 */
	redo: function() {
		var me = this;
		if(me.stackIndex<me.stack.length-1) {
			var redoIndex = me.stackIndex+1;
			var info = me.stack[redoIndex];
			var layer = me.drawLayer.layer;

			// 履歴の記録を停止
			me.stopHistory();

			// redo
			switch(info.operation) {
			case "add":
				// 追加をやり直し
				layer.addFeatures([info.feature]);
				break;
			case "remove":
				// 削除をやり直し
				layer.removeFeatures([info.feature]);
				break;
			case "modify":
				// 編集をやりなおし
				// 一旦削除
				layer.removeFeatures([info.feature]);
				// ジオメトリ変更
				info.feature.geometry = info.afterFeature.geometry.clone();
				// Style変更
				info.feature.style = $.extend({}, info.afterFeature.style);
				// attributes変更
				info.feature.attributes = $.extend({}, info.afterFeature.attributes);
				// 再度追加
				layer.addFeatures([info.feature]);
				break;
			}

			// 履歴の記録を開始
			me.startHistory();

			me.stackIndex++;
			console.debug("redo: "+me.stackIndex+"/"+(me.stack.length-1));
			me.drawLayer.layer.events.triggerEvent("historychange", {
				historyControl: me,
				operation: "redo",
				feature: info.feature
			});
			return true;
		}
		return false;
	}
});
