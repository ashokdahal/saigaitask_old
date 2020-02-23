// Adding a mode parameter with "build" as value in the run-tests.html will
// make usage of the build version of the library.
// get the OLLoader.js script location
(function() {
    var r = new RegExp("(^|(.*?\\/))(" + "STLoader.js" + ")(\\?|$)"),
        s = document.getElementsByTagName('script'),
        src, m, l = "";
    for(var i=0, len=s.length; i<len; i++) {
        src = s[i].getAttribute('src');
        if(src) {
            var m = src.match(r);
            if(m) {
                l = m[1];
                break;
            }
        }
    }

    var regex = new RegExp( "[\\?&]mode=([^&#]*)" );
    var href = window.parent.location.href;
    var results = regex.exec( href );
    l += (results && results[1] == 'build') ?
        "../build/SaigaiTask.js" : "../lib/SaigaiTask.js";
    //l += "../build/SaigaiTask-debug-all.js";

    scriptTag = "<script type='text/javascript' src='" + l + "'></script>";
    document.write(scriptTag);
})();
