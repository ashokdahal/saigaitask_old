<form:form modelAttribute="report4formForm">

<form:hidden path="trackdataid" />
<form:hidden path="reporttypeinfoid" />
<form:hidden path="userinfoid" />
<form:hidden path="menuid" />
<form:hidden path="fileurl" />
<form:hidden path="reportdataid" />
<form:hidden path="deleted" />
<form:hidden path="note" />
<form:hidden path="subtotal" />
<form:hidden path="atotal" />

<jsp:include page="/WEB-INF/view/page/report4form/form.jsp" />

</form:form>