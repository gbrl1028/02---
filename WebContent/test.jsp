<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="expires" content="0" />
<%
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Expires", "0");
%>
<title>통계표 조회 테스트 페이지</title>
<script type="text/javascript">
	function aaa(){
		document,charset="euc-kr";
		document.getElementById('abc').submit();
	}
</script>
</head>
<body>
<form id="abc" action="test2.jsp" method="get">
	<input type="text" width="100px" name="aaa" value= "111" /> 
	<input type="text" width="100px" name="bbb" value= "222" />
	<input type="text" width="100px" name="ccc" value= "333" />
	<input type="text" width="100px" name="ddd" value= "444" />
	<input type="text" width="100px" name="eee" value= "노랑" />
	<input type="text" width="100px" name="fff" value= "파랑" />
</form>


<a href="javascript:aaa();"> 전송</a>
</body>
</html>