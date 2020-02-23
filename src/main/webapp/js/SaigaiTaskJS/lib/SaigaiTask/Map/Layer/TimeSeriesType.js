/**
 * レイヤー時系列種別
 * @requires SaigaiTask/Map/Layer.js
 */
SaigaiTask.Map.Layer.TimeSeriesType = {

		/**
		 * 時系列無し
		 */
		NONE: 0,

		/**
		 * 時系列あり 時点 time_fromのみ
		 */
		INSTANT: 10,

		/**
		 * 時系列あり 期間
		 */
		PERIOD: 11,

		/**
		 * 履歴管理
		 */
		HISTORY: 20
};

/**
 * 時間パラメータでタイムゾーン分時間をシフトするかどうかの設定
 * eコミマップがタイムゾーンをDBでもっていないための対策
 * @type Boolean
 */
SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET = false;