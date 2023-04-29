<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ext/css/jquery/ui-lightness/jquery-ui-1.10.3.custom.min.css"/>

<script type="text/javascript" src="<%=request.getContextPath()%>/ext/js/jquery/jquery-1.9.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/ext/js/jquery/jquery.fileDownload.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/ext/js/jquery/jquery-ui-1.10.3.custom.js"></script>
<script	type="text/javascript">
	
	//jquery ajax filedownload
	$(function(){
		
		//normal (html에는 아무것도 필요없다.)
		$(document).on("click", "a.downloadClass", function(){
			$.fileDownload($(this).prop('href'), {
				preparingMessageHtml : "파일 준비중입니다. 기다려 주세요.",
				failMessageHtml : "파일 준비중 문제가 발생했습니다. 다시 시도해보세요."
			});
			return false; //this is critical to stop the click event which event with will trigger a normal file download
		});
		
		//custom (div를 미리 만들어 놓는다.)
		$(document).on("click", "a.downloadCustomClass", function(){
			//var $preparingFileModal = $("#preparing-file-modal");
			//$preparingFileModal.dialog({modal : true});
			$("#downloadCustomClass").text("waiting!!!!!!!!");
			$.fileDownload($(this).prop('href'), {
				successCallback : function(url){
					//$preparingFileModal.dialog('close');
					$("#downloadCustomClass").text("ok!!!!!!!!");
				},
				failCallback : function(responseHtml, url){
					//$preparingFileModal.dialog('close');
					//$("#error-modal").dialog({modal : true});
				}
			});
			return false; //this is critical to stop the click event which event with will trigger a normal file download
		});
		
	});

	var getJSON = function(){
		$.ajax({
			url : "<%=request.getContextPath()%>/test/test02.do",
			type : "POST",
			dataType : "json",
			success : function(data){
				
				var errCode = data.errCode;
				var errMsg = data.errMsg;
				
				if(errCode == "1"){
					alert(errMsg);
				}else{
					var jsonData = JSON.stringify(data.list);
					$("#obj").append(jsonData);
				}
				
				//alert(data.list);
			},
			error : function(){}
		});
	};
	
	var txTest = function(){
		$.ajax({
			url : "<%=request.getContextPath()%>/test/test04.do",
			type : "POST",
			dataType : "json",
			success : function(data){
				
				var errCode = data.errCode;
				var errMsg = data.errMsg;
				
				if(errCode == "1"){
					alert(errMsg);
				}else{
					var jsonData = JSON.stringify(data.list);
					$("#obj").append(jsonData);
				}
				
				//alert(data.list);
			},
			error : function(){}
		});
	}
	
	var download = function(){
		window.location = "<%=request.getContextPath()%>/download.do";
	}
	
</script>
<title>Insert title here</title>
</head>
<body>
hello!!!!!!!!!
<%
	request.setAttribute("var99", "9999");

	List<String> list = new ArrayList<String>();
	list.add("a");
	list.add("b");
	list.add("c");
	
	request.setAttribute("list", list);
	
	Date date = new Date();
	request.setAttribute("date", date);
%>
<c:set var="var01" value="ok!!"/>
${var01}, ${var99}, ${list}, <c:out value="${var99}"/><br/> 
<fmt:formatNumber value="12345678" type="number"/>,
<fmt:formatNumber value="100" type="currency" currencySymbol="$"/>,
<fmt:formatNumber value="100" type="percent"/>,
<fmt:formatNumber value="12345.678" pattern=".00"/>,
<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>
<br/>
<table border='1'>
	<tr>
		<td>가</td><td>나</td><td>다</td>
	</tr>
	<tr>
		<c:forEach var="idx" items="${list}" varStatus="listCount">
			<td>${idx}</td>
		</c:forEach>
	</tr>
</table>
<br/>
<input type='button' id="btn01" value="jsonView" onclick="getJSON()"/>
<br/>
<input type='button' id="btn02" value="transaction test" onclick="txTest()"/>
<br/>
<!-- <a href="javascript:download()">file download</a> -->

Default ajax 파일 다운로드 : 
<a class="downloadClass" href="<%= request.getContextPath()%>/download.do">Default file download</a>
<br/>
Custom ajax 파일 다운로드 : 
<a id="downloadCustomClass" class="downloadCustomClass" href="<%= request.getContextPath()%>/download.do">Custom file download</a>
<div id="preparing-file-modal" title="파일 준비중..." style="display:none">
	파일 준비중입니다. 기다려 주세요.
	<div class="ui-progressbar-value ui-corner-left ui-corner-right" style="width:100%; height:22px; margin-top:20px;"></div>
</div>
<div id="error-modal" title="에러 발생" style="display:none">
	파일 준비중 문제가 발생했습니다. 다시 시도해보세요.
</div>

<br/>
<br/>
<div id="obj"></div>
</body>

</html>