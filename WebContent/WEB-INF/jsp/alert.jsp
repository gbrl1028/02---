<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<script type="text/javaScript" language="javascript">
<c:if test="${!empty resultMsg}">alert("${resultMsg}");</c:if>
<c:if test="${linkYn == 'Y'}">
	var sURL = "https://${pageContext.request.serverName}/memberInfo/memberInfo_01List.jsp?returnURL="+parent.document.URL;
	var sForm=parent.document.getElementById("form_search");
	if(sForm) {
		sURL="https://${pageContext.request.serverName}/memberInfo/memberInfo_01List.jsp";
	}
	parent.document.location.href=sURL;
</c:if>
<c:if test="${!empty backUrl}">history.back();</c:if>
</script>
<title>Notice</title>
</head>
<body>
</body>
</html>