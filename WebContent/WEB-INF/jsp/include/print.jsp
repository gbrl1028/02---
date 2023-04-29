<%--
 /** 
  * @Filename : print.jsp
  * @Description : 통계표 프린트
  * @Modification Information
  * @
  * @  수정일         수정자                   수정내용
  * @ -------    --------    ---------------------------
  * @ 
  *
  *  @author  김정현
  *  @since 2013-11-01
  *  @version 1.0
  *  @see
  *	
  */
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />	
	
	<script type="text/javascript" src="ext/js/common/json2.js"></script>
	<script type="text/javascript" src="ext/js/jquery/jquery-1.9.1.js"></script>
	
	<link type="text/css" rel="stylesheet" href="ext/css/common/print.css"/> 
	<link type="text/css" rel="stylesheet" href="ext/css/common/popup.css"/>
	<script type="text/javascript">
	
$(document).ready(function(){
	
	var header = $("#titleText", opener.document).html();
	var more = $(".more", opener.document).html(); //출처
	var grid = $("#htmlGrid", opener.document).html();

	var stblUnit = $("#stblUnit", opener.document).html(); // 2018.07.10 단위 추가 - 박진현
	//$('#header').html(header);

	$("#grid").html(grid).prepend(stblUnit).prepend("<br/>").prepend(header).append(more);
	
	$('span').remove('.h2_title'); // 통계표명 앞의 숫자 제거
	
	$('img').remove('.moreClass');		// 더보기버튼 제거
// 	$('ul').remove('.dateBtn');		// 자료갱신일 제거
	$('th').remove('.sortColHead');	// sort 버튼 제거
	$('th').remove('.sortRowHead');	// sort 버튼 제거
	
	window.print();
});

		
	</script>
</head>
<body>
<div>

	<div id="grid" class="print"></div>

</div>
</body>
</html>
