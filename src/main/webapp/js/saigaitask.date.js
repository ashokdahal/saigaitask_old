/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
SaigaiTask.Date = {
	/**
	 * 和暦元号配列
	 * @type {Array<Object>}
	 */
	eras: [{
		name: lang.__("Meiji"),
		start: new Date("1868/10/23")
	}, {
		name: lang.__("Taisho"),
		start: new Date("1912/7/30")
	}, {
		name: lang.__("Showa"),
		start: new Date("1926/12/25")
	}, {
		name: lang.__("Heisei"),
		start: new Date("1989/1/8")
	}],

	/**
	 * Dateから文字列にフォーマットします.
	 * @param {Date} date 日付
	 * @param {String} format 整形フォーマット
	 */
	format: function(date, format) {
		var year = (1900+date.getYear());
		var era = SaigaiTask.Date.getEra(date);

		var text = format;
		var formats = [
		               "GGGG", // 年号
		               "yyyy", // 年
		               "yy",
		               "MM", // 月
		               "M",
		               "dd", // 日
		               "d",
		               "HH", // 時
		               "H",
		               "mm", // 分
		               "m",
		               "ss", // 秒
		               "s"
		               ];
		for(var idx in formats) {
			var f = formats[idx];
			if(-1<text.indexOf(f)) {
				var length = null;
				if(1<f.length) length = f.length;
				var rep = null;
				if(f=="GGGG") {
					rep = era==null ? lang.__("A.D.") : era.name;
				}
				else {
					switch(f) {
					case "yy":
					case "yyyy":
						val = year;
						if(era!=null) {
							val = year - (era.start.getYear()+1900)  + 1;
							if(val==1) {
								val = lang.__("Original");
								length = null;
							}
						}
						break;
					case "M":
					case "MM":
						val = 1+date.getMonth();
						break;
					case "d":
					case "dd":
						val = date.getDate();
						break;
					case "H":
					case "HH":
						val = date.getHours();
						break;
					case "m":
					case "mm":
						val = date.getMinutes();
						break;
					case "s":
					case "ss":
						val = date.getSeconds();
						break;
					}
					if(val!=null) {
						if(length!=null && (""+val).length < length ) {
							val = SaigaiTask.Date.fill(val, length, '0'); 
						}
						rep = val;
					}
				}
				if(rep!=null) {
					text = text.replace(f, rep);
				}
			}
		}
		return text;
	},

	fill: function(text, length, char) {
		return new Array(length - ('' + text).length + 1).join(char) + text;
	},

	/**
	 * 元号情報を取得します.
	 * @return {Object} 元号情報(erasの中身)
	 */
	getEra: function(date) {
		// 元号を古い順にソート
		var eras = SaigaiTask.Date.eras;
		eras.sort(function(era1, era2) {
			return era1.start.getTime() - era2.start.getTime();
		});
		// 対象元号を取得
		var targetEra = null;
		for(var idx in eras) {
			var era = eras[idx];
			if( era.start <= date ) {
				targetEra = eras[idx];
			}
			else break;
		}
		// 元号を返却
		return targetEra;
	}
};