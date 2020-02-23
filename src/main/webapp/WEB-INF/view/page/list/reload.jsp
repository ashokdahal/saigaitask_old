<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<script type="text/javascript">
$(document).ready(function(){
	//btn.init();
	//btn.addEvent(window,'load', function() { btn.init();} );
	//$("#listmap")
	var as = $("[id=listmapbtn]");
	btninit(as);
	as = $("[id=listphotobtn]");
	btninit(as);
});

function btninit(as) {
	for (i=0; i<as.length; i++) {
	    var i1 = document.createElement('i');
	    var i2 = document.createElement('i');
	    var s1 = document.createElement('span');
	    //if (tt.length==1)
	    //	s1.style.cssText = "padding:12px 0px 14px 0px;";
	    var s2 = document.createElement('span');
	    s1.appendChild(i1);
	    s1.appendChild(s2);
	    while (as[i].firstChild) {
	      s1.appendChild(as[i].firstChild);
	    }
	    as[i].appendChild(s1);
	    as[i] = as[i].insertBefore(i2, s1);
	}
}
</script>
	<jsp:include page="list.jsp" />
