/*
 * jQuery edit-table
 * Version Beta
 * https://github.com/ewai/jquery.edit-table
 * Copyright (c) 2013 Atsushi Iwai
 * Dual licensed under the MIT and GPL licenses.
*/
(function($) {
	var kc = {};
	kc.ESC = 27;
	kc.TAB = 9;
	kc.LEFT = 37;
	kc.RIGHT = 39;
	kc.UP = 38;
	kc.DOWN = 40;
	kc.F2 = 113;
	kc.BACKSPASE = 8;
	kc.DEL = 46;
	kc.ENTER = 13;
	kc.SLASH = 191;
	kc.CODE_0 = 48;
	kc.CODE_9 = 57;
	kc.CODE_T0 = 96;
	kc.CODE_T9 = 105;
	kc.CODE_C = 67;
	kc.CODE_V = 86;
	kc.SPACE = 32;
	kc.CTRL = 17;
	kc.PERIOD = 190;
	kc.PERIOD2 = 110;

	var setting = {};
	setting.thBackgroundColor = 'darkolivegreen';
	setting.thColor = 'lavender';
	var tn = {};
	tn.STRING = 'String';
	tn.TEXTAREA = 'TextArea';
	tn.DATE = 'Date';
	tn.DATETIME = 'DateTime';
	tn.NUMBER = 'Number';
	tn.FLOAT = 'Float';
	tn.SELECT = 'Select';
	tn.CHECKBOX = 'Checkbox';
	tn.UPLOAD = 'Upload';
	tn.DISPLAY = 'Display';

	var isMaybeIE = (!jQuery.support.opacity) || (!jQuery.support.noCloneEvent && jQuery.support.opacity);
	var headTh=null;
	var sideTh=null;
	var beforeKey = 0;
	var cpCell;
	var cpType;
	var saveData = [];
	var saveFiles = [];

	$.fn.extend({
		editTable:function(options) {
			var defaults = {
				datepicker:true,
				comma:true,
				cellWidth:0,
				cellHeight:0,
				sideHead:false,
				headerNames:[],
				types:[],
				datas:{},
				selects:{},
				autoRowNum:false,
				callback:null,
				mobile:false
			};
			var opts = $.extend({}, defaults, options);
			setTimeout(ini, 1);
			function ini() {
				setDom();
				setClass();
				setData();
				setEvent();
				setStyle();
			};
			function setDom() {
				// append tbody tr
				if (!($('table#editTable tbody').length>0)) {
					var html = '<tbody><tr>';
					$(opts.headerNames).each(function() { html += '<td></td>'; });
					html += '</tr></tbody>';
					$('table#editTable').prepend(html)
				}
				// append thead tr
				if (opts.headerNames.length>0 && !($('table#editTable thead').length>0)) {
					var html = '<thead><tr>';
					$(opts.headerNames).each(function() { html += '<th></th>'; });
					html += '</tr></thead>';
					$('table#editTable').prepend(html)
				}
				// add checkbox
				$('table#editTable tbody').find('tr').each(function() {
					var col = 0;
					$(this).find('td').each(function() {
						if (opts.types[col] == tn.CHECKBOX) {
							$(this).html('<input type="checkbox"/>');
						} if (opts.types[col] == tn.SELECT) {
							html = '<select><option value=""></option>';
							$(opts.selects[col]).each(function() {
								html += '<option value="' + this +'">' + this + '</option>';
							});
							html += '</select>';
							$(this).html(html);
						}
						col++;
					});
				});
				// append tr
				if (!$.isEmptyObject(opts.datas)) {
					var tbody = $('table#editTable tbody');
					var trs = tbody.find('tr');
					len = opts.datas.length - trs.length;
					if (len > 0) {
						clonTr = trs.eq(0).clone(false);
						for (i = 0; i < len; i++) {
							$(tbody).append('<tr>' + clonTr.html() + '</tr>');
						}
					}
				}
			}
			function setClass() {
				// add class tbody > td
				if (!$.isEmptyObject(opts.types)) {
					$('table#editTable tbody tr').each(function(e) {
						var col = 0;
						$(this).find('td').each(function(e) {
							$(this).addClass(opts.types[col++]);
						});
					});
				}
			}
			function setData() {
				// set thead > th
				if (opts.headerNames.length>0) {
					var row = 0;
					$('table#editTable thead').find('th').each(function() {
						$(this).text(opts.headerNames[row++]);
					});
				}
				// set tbody > td
				if (!$.isEmptyObject(opts.datas)) {
					var row = 0;
					$('table#editTable tbody').find('tr').each(function() {
						var col = 0;
						$(this).find('td').each(function() {
							val = opts.datas[row][col];
							if (opts.types[col] == tn.CHECKBOX) {
								if (val) $(this).find('input').attr('checked', true);
							} else if (opts.types[col] == tn.SELECT) {
								$(this).find('Select').val(val).find('option').each(function() {
									if ($(this).val() == val) $(this).attr('selected', 'selected')
								});
							} else if (opts.types[col] == tn.NUMBER || opts.types[col] == tn.FLOAT) {
								if (opts.comma) $(this).text(addComma(val));
							} else {
								$(this).text(val);
							}
							col++;
						});
						row++;
					});
				}
			}
			function setEvent() {
				$.each($('table#editTable').find('td'), function(event) {
					if($(this).hasClass(tn.STRING)
							|| $(this).hasClass(tn.TEXTAREA)
							|| $(this).hasClass(tn.DATE)
							|| $(this).hasClass(tn.DATETIME)
							|| $(this).hasClass(tn.FLOAT)
							|| $(this).hasClass(tn.NUMBER)) {
						setTdClickEvent(this, event, true);
					} else if($(this).hasClass(tn.SELECT)
							|| $(this).hasClass(tn.CHECKBOX)
							|| $(this).hasClass(tn.UPLOAD)) {
						setTdClickEvent(this, event, false);
					}
				});
				$('body').click(function(e) {
					if (!isInInput(e)) {
						offInput(false);
					}
				}).keydown(function(e) {
					return typeKey(e);
				}).keyup(function(e) {
					beforeKey = 0;
					return true;
				});
				$(window).resize(function(){
					if($('.onfocusCell').length > 0) setTimeout(onFocusBorder, 1);
					if($('.onfocusCell').length > 0) setTimeout(onCopyBorder, 1);
				});
			}
			function setStyle() {
				if (opts.autoRowNum) {
					var row = 1;
					$('table#editTable tr').each(function() {
						th = $(this).find('th:first');
						if (th.length) th.before('<th class="NoHead"></td>');
						td = $(this).find('td:first');
						if (td.length) td.before('<th class="No">' + row++ + '</th>');
					});
				}
				if (opts.cellWidth != 0) {
					$('table#editTable td').css('width', opts.cellWidth);
				}
				if (opts.cellHeight != 0) {
					$('table#editTable td, table#editTable th').css('height', opts.cellHeight);
				}
				$('table#editTable td.Display input:checkbox, table#editTable td.Display select').attr('disabled', 'disabled');
				if(opts.sideHead) {
					$('table#editTable tr td:nth-child(1)').addClass('sideHead');
				}
				$('<div id="focusBorder-left" class="focusBorder" />').appendTo('body').hide();
				$('<div id="focusBorder-right" class="focusBorder"/>').appendTo('body').hide();
				$('<div id="focusBorder-top" class="focusBorder"/>').appendTo('body').hide();
				$('<div id="focusBorder-bottom" class="focusBorder"/>').appendTo('body').hide();
				$('<div id="copyBorder-left" class="copyBorder"/>').appendTo('body').hide();
				$('<div id="copyBorder-right" class="copyBorder"/>').appendTo('body').hide();
				$('<div id="copyBorder-top" class="copyBorder"/>').appendTo('body').hide();
				$('<div id="copyBorder-bottom" class="copyBorder"/>').appendTo('body').hide();
			}
			function isInInput(e) {
				td = $(e.target).parent().find('td');
				if(td.hasClass(tn.STRING)
						|| td.hasClass(tn.TEXTAREA)
						|| td.hasClass(tn.DATE)
						|| td.hasClass(tn.DATETIME)
						|| td.hasClass(tn.FLOAT)
						|| td.hasClass(tn.NUMBER)) {
					return false;
				}
				return true;
			}
			function setTdClickEvent(obj, e, createInputFlg) {
				if (createInputFlg) {
					$(obj).mousedown(function(e) {
						if (!isInInput(e)) {
							onFocussCell(this);
						}
					});
				} else {
					$(obj).mousedown(function(e) {
						onFocussCell($(this));
					});
					//$('table#editTable Select').change(function(e) {
					$(obj).find('select').change(function(e) {
						var td = $(e.target).parent();
						var sel = e.target;
						save(td[0].id, sel.options[sel.selectedIndex].value);
						if (opts.callback != null) opts.callback();
					});
					$(obj).find('input:checkbox').click(function(e) {
						var td = $(e.target).parent();
						var sel = e.target;
						if (sel.checked)
							save(td[0].id, sel.value);
						else
							save(td[0].id, '');
						if (opts.callback != null) opts.callback();
					});
					$(obj).find('input:file').change(function(e) {
						var td = $(e.target).parent();
						var up = $(e.target);
						savefile(td[0].id, up);
					});
				}
				//グレーアウト表示対応
				//if(!$(obj).parent('tr').hasClass('gray')){
					var func = function(e) {
						// スマホだとpress時にうまくフォーカスされなかったので、ここでフォーカスさせる
//						if (opts.mobile)
//							onFocussCell($(this));
						// 自分以外に開いているものがあれば、閉じる
						$("[id='inputNow']").each(function() {
							offInput(false);
						});
						// 開く
						if (createInputFlg) createInput($(this), $(this).hasClass(tn.TEXTAREA));
					};
					if (!opts.mobile) {
						$(obj).unbind('dblclick');
						$(obj).dblclick(func);
					}
				//}
				// スマホ対応
					if (opts.mobile) {
						$(obj).unbind('press')
						$(obj).on('press', func);
					}
			}
			function setOnCellTh() {
				if (headTh!=null) headTh.css('background-color', '').css('color', '');
				if (sideTh!=null) sideTh.css('background-color', '').css('color', '');
				var tds = $('.onfocusCell').prevAll();
				if (opts.autoRowNum) {
					var td = tds.eq(tds.length-1);
					td.css('color', setting.thColor);
					td.css('background-color', setting.thBackgroundColor);
					sideTh= td;
				}
				var leftNum = tds.length;
				var th = $('table#editTable').find('tr:nth-child(1) th').eq(leftNum);
				th.css('color', setting.thColor);
				th.css('background-color', setting.thBackgroundColor);
				headTh = th;
			}
			function createInput(obj, inp) {
				// すでにあれば生成しなおさない
				if(0<$(obj).has("#inputNow").length) return;
				//var val = $(obj).text().trim();
				var val = $.trim($(obj).text());
				var tw = $(obj).width();
				if (tw > 150) tw = 150;
				if (tw < 50) tw = 50;
				if (inp)
					$('<textarea />').attr('id', 'inputNow').attr('size', '15').val(val).appendTo($(obj).html(''));
				else
					$('<input type="text"/>').attr('id', 'inputNow').css('width', tw).val(val).appendTo($(obj).html(''));
				setInputEvent($('.onfocusCell'), $('#inputNow'));
				$('#inputNow').focus();
				var oid = obj.attr('id');
				setTags(oid, val);
				var len = val.length;
				if(isMaybeIE){
					var range = document.selection.createRange();
					range.moveEnd('character' , len);
					range.moveStart('character', len);
					range.select();
				} else {
					document.getElementById('inputNow').setSelectionRange(len, len);
				}
			}
			function setInputEvent(tdObj, inputObj) {
				if (tdObj.hasClass(tn.STRING) || tdObj.hasClass(tn.TEXTAREA)) {
				} else if (tdObj.hasClass(tn.DATE)) {
					if (opts.datepicker) {
						inputObj.datepicker({dateFormat: 'yy/mm/dd'});
					}
				} else if (tdObj.hasClass(tn.DATETIME)) {
					if (opts.datepicker) {
						inputObj.datetimepicker({controlType: 'select', dateFormat: 'yy/mm/dd'});
					}
				} else if (tdObj.hasClass(tn.NUMBER) || tdObj.hasClass(tn.FLOAT)) {
					//inputObj.val(removeComma(inputObj.val()));
				}
			}
			function offInput(borderOffFlg) {
				var val = $('#inputNow').val();
				var td = $('#inputNow').parent();
				if(td.hasClass(tn.NUMBER) || td.hasClass(tn.FLOAT)) {
					if (opts.comma) {
						val = addComma(val);
					}
				}
				td.text(val);
				//nullチェックのため、valがnullでも以下の処理に進む
				//if (val && td[0]) {
				if (td[0]) {
					opts.callback = ckNull;
					if (!valcheck(td, val)) {
						alert("入力値が不正です。");
						recreateInput(td);
					}
					else if(!opts.callback(td[0].id, val)){
						alert("入力必須項目です！値を入力してください。");
						recreateInput(td);
					//nullチェックが問題なければデータをsaveする
					}else{
						save(td[0].id, val);
					}
				}
				if (borderOffFlg) {
					$('.onfocusCell').removeClass('onfocusCell');
				}
			}
			function valcheck(td, val) {
				if (val == "") return true;
				if(td.hasClass(tn.NUMBER)) {
					return jQuery.isNumeric(val) && val.indexOf('.') == -1;
				}
				else if(td.hasClass(tn.FLOAT)) {
					return jQuery.isNumeric(val);
				}
				else if (td.hasClass(tn.DATE)) {
					return val.match(/^\d{4}\/\d{2}\/\d{2}$/);
				}
				else if (td.hasClass(tn.DATETIME)) {
					return val.match(/^([1-2]\d{3}\/([0]?[1-9]|1[0-2])\/([0-2]?[0-9]|3[0-1])) (20|21|22|23|[0-1]?\d{1}):([0-5]?\d{1})(:([0-5]?\d{1}))*$/);
				}
				return true;
			}
			function recreateInput(td) {
				if(td.hasClass(tn.STRING) || td.hasClass(tn.NUMBER) || td.hasClass(tn.FLOAT)) {
					createInput(td);
				}else if (td.hasClass(tn.TEXTAREA)) {
					createInput(td, true);
				}else if (td.hasClass(tn.DATE)) {
					if (opts.datepicker) {
						$(td).datepicker({dateFormat: 'yy/mm/dd'});
						createInput(td);
					}
				} else if (td.hasClass(tn.DATETIME)) {
					if (opts.datepicker) {
						$(td).datetimepicker({controlType: 'select', dateFormat: 'yy/mm/dd'});
						createInput(td);
					}
				}
			}
			function save(tagid, value) {
				var up = false;//同じタグIDは追加しない。
				for (i = 0; i < saveData.length; i++) {
					if (saveData[i][0] == tagid) {
						saveData[i] = [tagid, value];
						up = true;
						break;
					}
				}
				if (!up)
					saveData.push([tagid, value]);
				return;
			}
			function savefile(tagid, file) {
				saveFiles.push([tagid, file]);
				return;
			}
			function onFocusBorder() {
				var width = $('.onfocusCell').width();
				var height = $('.onfocusCell').height();
				var ost = $('.onfocusCell').offset();
				var top = ost.top;
				var left = ost.left;
				var ost = $('.onfocusCell').offset();
				$('#focusBorder-left')
					.css('width', 0)
					.css('height', height+2)
					.css('top', top)
					.css('left', left-1)
					.show();
				$('#focusBorder-right')
					.css('width', 0)
					.css('height', height+2)
					.css('top', top)
					.css('left', left+width+4)
					.show();
				$('#focusBorder-top')
					.css('width', width+4)
					.css('height', 0)
					.css('top', top)
					.css('left', left-1)
					.show();
				$('#focusBorder-bottom')
					.css('width', width+4)
					.css('height', 0)
					.css('top', top+height+3)
					.css('left', left-1)
					.show();
			}
			function onCopyBorder() {
				var width = $('.onCopyCell').width();
				var height = $('.onCopyCell').height();
				var ost = $('.onCopyCell').offset();
				if (ost) {
					var top = ost.top;
					var left = ost.left;
					var ost = $('.onCopyCell').offset();
					$('#copyBorder-left').css('width', 0).css('height', height + 2).css('top', top).css('left', left - 1).show();
					$('#copyBorder-right').css('width', 0).css('height', height + 2).css('top', top).css('left', left + width + 4).show();
					$('#copyBorder-top').css('width', width + 4).css('height', 0).css('top', top).css('left', left - 1).show();
					$('#copyBorder-bottom').css('width', width + 4).css('height', 0).css('top', top + height + 3).css('left', left - 1).show();
				}
			}
			function onFocussCell(obj) {
				offInput(true);
				$(obj).addClass('onfocusCell');
				setTimeout(onFocusBorder, 1);
				setTimeout(setOnCellTh, 1);
			}
			function offCopyBorder() {
				$('.onCopyCell').removeClass('onCopyCell');
			}
			function typeKey(e){
				var key = e.keyCode;
				if (isInInput(e)) {
					switch(key) {
						case kc.LEFT:
						case kc.RIGHT:
						case kc.UP:
						case kc.DOWN:
						case kc.BACKSPASE:
						case kc.DEL:
							break;
						case kc.ESC:
						case kc.ENTER:
							if($('.onfocusCell').hasClass(tn.SELECT)) {
								$('.onfocusCell select').blur();
							} else if($('.onfocusCell').hasClass(tn.TEXTAREA)) {
							} else {
								offInput(false);
							}
							break;
						case kc.TAB:
							moveRight();
							break;
						default:
							// check input
							var nowTd = $('#inputNow').parent();
							if(nowTd.hasClass(tn.STRING) || nowTd.hasClass(tn.TEXTAREA)) {
							} else if(nowTd.hasClass(tn.DATE)) {
								return isDate(key);
							} else if(nowTd.hasClass(tn.NUMBER)) {
								return isNumeric(key);
							} else if(nowTd.hasClass(tn.FLOAT)) {
								return isFloat(key);
							} else if(nowTd.hasClass(tn.SELECT)) {
							} else if(nowTd.hasClass(tn.CHECKBOX)) {
							}
							break;
					}
				} else {
					switch(key) {
						case kc.ESC:
							offCopyBorder();
							$('.copyBorder').hide();
							return false;
						case kc.TAB:
							moveRight();
							return false;
						case kc.LEFT:
							moveLeft();
							return false;
						case kc.UP:
							moveUp();
							return false;
						case kc.RIGHT:
							moveRight();
							return false;
						case kc.DOWN:
							moveDown();
							return false;
						case kc.F2:
							if(!$('.onfocusCell').hasClass(tn.CHECKBOX)
								&& !$('.onfocusCell').hasClass(tn.SELECT)) {
								createInput($('.onfocusCell'));
							}
							if($('.onfocusCell').hasClass(tn.SELECT)) {
								$('.onfocusCell select').focus();
							}
							break;
						case kc.SPACE:
						case kc.ENTER:
							if($('.onfocusCell').hasClass(tn.CHECKBOX)) {
								$('.onfocusCell input').click();
							}
							if($('.onfocusCell').hasClass(tn.SELECT)) {
								$('.onfocusCell select').focus();
							}
							if($('.onfocusCell').hasClass(tn.DATE)) {
								return false;
							}
							break;
						case kc.CODE_C:
							if (beforeKey == kc.CTRL) {
								if ($('.onfocusCell').hasClass(tn.STRING)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.STRING;
								} else if ($('.onfocusCell').hasClass(tn.TEXTAREA)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.TEXTAREA;
								} else if ($('.onfocusCell').hasClass(tn.DATE)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.DATE;
								} else if ($('.onfocusCell').hasClass(tn.NUMBER)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.NUMBER;
								} else if ($('.onfocusCell').hasClass(tn.FLOAT)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.FLOAT;
								} else if ($('.onfocusCell').hasClass(tn.SELECT)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.SELECT;
								} else if ($('.onfocusCell').hasClass(tn.CHECKBOX)) {
									cpCell = $('.onfocusCell').html();
									cpType = tn.CHECKBOX;
								}
								offCopyBorder();
								$('.onfocusCell').addClass('onCopyCell');
								onCopyBorder();
							}
							break;
						case kc.CODE_V:
							if (beforeKey == kc.CTRL && $('.onfocusCell').length > 0) {
								if (($('.onfocusCell').hasClass(tn.STRING) && cpType == tn.STRING)
										|| ($('.onfocusCell').hasClass(tn.TEXTAREA) && cpType == tn.TEXTAREA)
										|| ($('.onfocusCell').hasClass(tn.DATE) && cpType == tn.DATE)
										|| ($('.onfocusCell').hasClass(tn.NUMBER) && cpType == tn.NUMBER)
										|| ($('.onfocusCell').hasClass(tn.FLOAT) && cpType == tn.FLOAT)
										|| ($('.onfocusCell').hasClass(tn.SELECT) && cpType == tn.SELECT)
										|| ($('.onfocusCell').hasClass(tn.CHECKBOX) && cpType == tn.CHECKBOX)) {
									$('.onfocusCell').html(cpCell);
								}
							}
							break;
						default:
							break;
					}
				}
				beforeKey = key;
				return true;
			}
			function moveRight() {
				$('.onfocusCell').nextAll().each(function() {
					if(move(this)) return false;
				});
			}
			function moveLeft() {
				$('.onfocusCell').prevAll().each(function() {
					if(move(this)) return false;
				});
			}
			function moveUp() {
				var leftNum = $('.onfocusCell').prevAll().length;
				$('.onfocusCell').parent().prevAll().each(function() {
					upTd = $(this).find('td, th').eq(leftNum);
					if(move(upTd)) return false;
				});
			}
			function moveDown() {
				var leftNum = $('.onfocusCell').prevAll().length;
				$('.onfocusCell').parent().nextAll().each(function() {
					downTd = $(this).find('td, th').eq(leftNum);
					if(move(downTd)) return false;
				});
			}
			function move(obj){
				if($(obj).hasClass(tn.STRING)
						|| $(obj).hasClass(tn.TEXTAREA)
						|| $(obj).hasClass(tn.DATE)
						|| $(obj).hasClass(tn.DATETIME)
						|| $(obj).hasClass(tn.NUMBER)
						|| $(obj).hasClass(tn.FLOAT)
						|| $(obj).hasClass(tn.SELECT)
						|| $(obj).hasClass(tn.CHECKBOX)) {
					onFocussCell(obj);
					return true;
				}
				return false;
			}
		},
		getSaveData:function() {
			return saveData;
		},
		clearSaveData:function() {
			saveData = [];
		},
		saveData:function(tagid, value) {
			saveData.push([tagid, value]);
		},
		countSaveFiles:function() {
			return saveFiles.length;
		},
		getSaveFiles:function() {
			return saveFiles;
		},
		clearSaveFiles:function() {
			saveFiles = [];
		},
		clearEvent:function() {
			$.each($('table#editTable').find('td'), function(event) {
				$(this).unbind('dblclick');
				$(this).unbind('press');
			});
			$("body").unbind('click')
		},
		getData:function(options) {
			//未使用
			var result = "[";
			var val = "";
			var row = 0;
			var col = 0;
			$('table#editTable tbody').find('tr').each(function() {
				result += '[';
				col = 0;
				$(this).find('td').each(function() {
					if($(this).hasClass(tn.STRING) || $(this).hasClass(tn.TEXTAREA)
						|| $(this).hasClass(tn.DATE) || $(this).hasClass(tn.DATETIME)) {
						result += '"' + $(this).text() + '",';
					} else if($(this).hasClass(tn.NUMBER)) {
						result += removeComma($(this).text()) + ',';
					} else if($(this).hasClass(tn.FLOAT)) {
						result += removeComma($(this).text()) + ',';
					} else if($(this).hasClass(tn.SELECT)) {
						result += '"' + $(this).find('select').val() + '",';
					} else if($(this).hasClass(tn.CHECKBOX)) {
						if ($(this).find('input').is(':checked')) {
							result += 'true,';
						} else {
							result += 'false,';
						}
					} else if ($(this).hasClass(tn.DISPLAY)) {
						if ($(this).find('select').length > 0) {
							result += '"' + $(this).find('select').val() + '",';
						} else if ($(this).find('input:checkbox').length > 0) {
							if ($(this).find('input').attr('checked')) {
								result += 'true,';
							} else {
								result += 'false,';
							}
						} else {
							result += '"' + $(this).text() + '",';
						}
					}
				});
				result = result.substring(0, result.length - 1) + '],';
			});
			result = result.substring(0, result.length - 1) + "]";
			return result;
		},
		getJson:function(options) {
			return jQuery.parseJSON(this.getData());
		}
	});
	function addComma(str) {
		var num = (new String(str)).replace(/,/g, '');
		while(num != (num = num.replace(/^(-?\d+)(\d{3})/, '$1,$2')));
		return num;
	}
	function removeComma(str) {
		var num = new String(str).replace(/,/g, '');
		if (num != '') {
			return new Number(num);
		} else {
			return 0;
		}
	}
	function isNumeric(keycode) {
		if ((keycode >= kc.CODE_0 && keycode <= kc.CODE_9)
				|| (keycode >= kc.CODE_T0 && keycode <= kc.CODE_T9)) {
			return true;
		} else {
			return false;
		}
	}
	function isFloat(keycode) {
		if ((keycode >= kc.CODE_0 && keycode <= kc.CODE_9)
				|| (keycode >= kc.CODE_T0 && keycode <= kc.CODE_T9) || keycode == kc.PERIOD || keycode == kc.PERIOD2) {
			return true;
		} else {
			return false;
		}
	}
	function isDate(keycode) {
		var val = $('#inputNow').val();
		if (keycode >= kc.CODE_0 && keycode <= kc.CODE_9) {
			if (val.replace(/\//g, '').length >= 8) return false;
			return true;
		}
		if (keycode == kc.SLASH) {
			if (val.replace(/[0-9]/g, '').length >= 2) return false;
			return true;
		}
		return false;
	}
})(jQuery);

String.prototype.trim = function() {
    return this.replace(/^[\s　]+|[\s　]+$/g, '');
}

