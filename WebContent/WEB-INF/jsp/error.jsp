<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<c:url value='/css/egovframework/sample.css'/>"/>
<script type="text/javaScript" language="javascript">
<c:if test="${!empty resultMsg}">alert("${resultMsg}");</c:if>	
</script>
<title>통계청::error</title>
</head>
<body>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="100%" height="100%" align="center" valign="middle" style="padding-top:150px;"><table border="0" cellspacing="0" cellpadding="0">
	  <tr>
		<td class="error">
			<c:choose>
			<c:when test="${ParamInfo.language == 'en'}">
				<img src="images/sorry_en.gif" alt='<pivot:msg code="ui.error.msg"/>' border="0"/></a>
			</c:when>
			<c:otherwise>
				<img src="images/sorry_ko.gif" alt='<pivot:msg code="ui.error.msg"/>' border="0"/></a>
			</c:otherwise>
			</c:choose>
		</td>
	  </tr>
	</table></td>
  </tr>
</table>
</body>
</html>