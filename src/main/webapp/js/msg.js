/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

function insertTextAtPosision(obj, pos, txt) {
    obj.focus();
    //if (jQuery.browser.msie) {
    //    pos.text = txt;
    //    pos.select();
    //} else {
        var s = obj.value;
        var np = pos + txt.length;
        obj.value = s.substr(0, pos) + txt + s.substr(pos);
        if(!jQuery.support.opacity) {
            setSelectionRangeIE8(obj, np, np);
        }else{
            obj.setSelectionRange(np, np);
        }
    //}
}
function getCaretPosition(obj) {
    obj.focus();
    var pos;
    //if (jQuery.browser.msie) {
     //   pos = document.selection.createRange();
    //} else {
        pos = obj.selectionStart;
    //}
    return pos;
}

function setSelectionRangeIE8(input, selectionStart, selectionEnd){
    if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
    }else if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
    }
}