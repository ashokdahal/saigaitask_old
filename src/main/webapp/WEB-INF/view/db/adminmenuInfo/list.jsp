<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/global.css')}"/>
</head>
<body>

<html:errors/>


<table border="1">
<tr style="background-color:pink">

	<th>classify</th>
	<th>name</th>
	<th>level</th>
	<th>publicmode</th>
	<th>groupid</th>
	<th>url</th>
	<th>valid</th>
<th></th><th></th><th></th>
</tr>

<c:forEach var="e" varStatus="s" items="${adminmenuInfoItems}">
	<tr style="background-color:${s.index %2 == 0 ? 'white' : 'aqua'}">
		<td>
			${f:h(e.classify)}
		</td>
		<td>
			${f:h(e.name)}
		</td>
		<td>
			${f:h(e.level)}
		</td>
		<td>
			${f:h(e.publicmode)}
		</td>
		<td>
			${f:h(e.groupid)}
		</td>
		<td>
			${f:h(e.url)}
		</td>
		<td>
			${f:h(e.valid)}
		</td>
		<td><s:link href="show/${e.id}"> show </s:link></td>
		<td><s:link href="edit/${e.id}"> edit </s:link></td>
		<td><s:link onclick="return confirm('delete OK?');" href="delete/${e.id}">delete</s:link></td>
	</tr>
</c:forEach>

</table>

<s:link href="create"> create new Object </s:link>
</body>
</html>
