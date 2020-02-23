<%/* Copyright (c) 2013 National Research Institute for Earth Science and * Disaster Prevention (NIED). * This code is licensed under the GPL version 3 license, available at the root * application directory. */%>
<div>
<ul class="buttons">
<c:set var="bbtncount" value="0"/>
<c:forEach var="e" varStatus="s" items="${bbuttonItems}">
	<c:if test="${bbtncount==5 }"><li style="width:120px;">&nbsp;</li></c:if>
	<c:if test="${buttonMap[e.pagebuttonid]!=null}"><%-- check valid setting --%>
	<c:set var="bbtncount" value="${bbtncount+1}"/>
	<li>
	<c:set var="enable" value="${e.enable}"/>
	<c:if test="${pagetype=='map' && e.pagebuttonid==11}"><c:set var="enable" value="${false}"/></c:if>
	<c:if test="${pagetype=='map' && e.pagebuttonid==17}"><c:set var="enable" value="${false}"/></c:if>
	<c:if test="${enable}">
	<c:set var="href" value="#"/>
	<c:if test="${not empty buttonMap[e.pagebuttonid].href}"><c:set var="href" value="${buttonMap[e.pagebuttonid].href}"/></c:if>
	<c:if test="${not empty e.href}"><c:set var="href" value="${e.href}"/></c:if>
	<c:if test="${not empty e.target}"><c:set var="target" value="target=\"${f:h(e.target)}\""/></c:if>
	<a href="${f:h(href)}" class="btn blue" ${target}>${buttonMap[e.pagebuttonid].name}</a>
	</c:if>
	<c:if test="${!enable}">
	<a href="#" class="btn lgray">${buttonMap[e.pagebuttonid].name}</a>
	</c:if>
	</li>
	</c:if>
</c:forEach>
</ul>
</div>