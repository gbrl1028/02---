<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>통계표 조회 파라미터 설정</title>
<script>
function fn_search(){
	var f = document.f;
	var orgId = f.orgId.value;
	var tblId = f.tblId.value;
	f.action="<%=request.getContextPath() %>/statHtml.do?orgId=" + orgId + "&tblId=" + tblId;
	f.method="GET";
	f.target="_blank";
	f.submit();
}
</script>
</head>
<body>
<form name="f">
기관코드 : <input type="text" name="orgId" size="30"/>
<br/><br/>
통계표ID : <input type="text" name="tblId" size="30"/>&nbsp;&nbsp;<input type="button" value="조회" onclick="fn_search()">
</form>
</body>
</html>