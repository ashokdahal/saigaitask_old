<% /* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<script type="text/javascript">

	<%-- 翻訳で使用するテーブル --%>  
	var lang_json_obj = <%=SaigaiTaskLangUtils.getLangJson(request)%>;

	<%-- JavaScript 用の MessageFormat（一部の jsp で使用）--%>
	var MessageFormat = {
		format: function(s,a0,a1,a2) {
			s = s.replace("{0}", a0);
			s = s.replace("{1}", a1);
			s = s.replace("{2}", a2);
			return s;
		}
	}
	</script>
<%-- lang.jsを2回以上読み込む場合、ブラウザが乱数パラメータを自動で付加するため、RatproxyでBad or no charset declared for renderable file となる？ --%>
<script type="text/javascript" charset="UTF-8" src="${f:url('/js/lang.js')}"></script>
