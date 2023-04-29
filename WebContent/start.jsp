<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head></head>
<body>
<%	
	Enumeration enumeration = request.getParameterNames();

	out.println("<form name='send'>");
	String name = "";
	String[] values = null;
	while(enumeration.hasMoreElements()){
		name = (String)enumeration.nextElement();
		values = request.getParameterValues(name);
		for(int i = 0; i < values.length; i++){
			if(name.equals("empId")){
				session.setAttribute("empId", values[i]);
			}else{
				out.println("<input type='hidden' name='" + name + "' value='" + values[i] + "'>");
			}
		}
	}
	
	out.println("</form>");
%>
<script type='text/javascript'>
	init();
	
	function init(){
		var url = 'statHtml.do';
		send.action = url;
		send.method = 'post';
		send.submit();
	}
</script>
</body>
</html>