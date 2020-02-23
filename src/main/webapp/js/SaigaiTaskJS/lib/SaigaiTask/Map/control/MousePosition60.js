/**
 * マウスカーソル位置の経緯度を60進(DMS)表記で表示するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.MousePosition60 = OpenLayers.Class(OpenLayers.Control.MousePosition, {

    initialize: function (options) {
        // 画面右下に表示する CSSクラス を指定
        // 指定しないと、CLASS_NAME で指定されてしまうため
        OpenLayers.Util.extend(options, {
            "displayClass": "olControlMousePosition"
        })
        OpenLayers.Control.MousePosition.prototype.initialize.apply(this, [options]);
    },

    /**
     * Method: formatOutput
     * Override to provide custom display output
     *
     * Parameters:
     * lonLat - {<OpenLayers.LonLat>} Location to display
     */
    formatOutput: function(lonLat) {
        var digits = parseInt(this.numDigits);
        var dx = lonLat.lon;
        var hx = parseInt(dx);
        var mx = parseInt((dx-hx)*60);
        var sx = ((dx-hx)*60-mx)*60;

        var dy = lonLat.lat;
        var hy = parseInt(dy);
        var my = parseInt((dy-hy)*60);
        var sy = ((dy-hy)*60-my)*60;

        var newHtml =
            this.prefix +
            hx+"°"+mx+"'"+sx.toFixed(1) +
            this.separator +
            hy+"°"+my+"'"+sy.toFixed(1) +
            this.suffix;
        return newHtml;
    },

    CLASS_NAME: "SaigaiTask.Map.control.MousePosition60"
});