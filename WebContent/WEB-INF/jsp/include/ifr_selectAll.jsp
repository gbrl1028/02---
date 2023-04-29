<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<link type="text/css" rel="stylesheet" href="ext/css/common/popup.css"/>
<link type="text/css" rel="stylesheet" href="ext/css/common/base.css"/>
</head>
<body onload="parent.callbackForIframe('pop_selectAll');">
<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="dataOpt"/>
</form:form>
	<div id="ifr_selectAll" style="overflow-y:auto;">
		<div class="ifr_scr2">
			<div class="pop_content">
				<c:set var="viewMode" value="${ParamInfo.dataOpt}"/>
				<div class="select_lay_Left">
					<h2 class="bu_circle6"><pivot:msg code="ui.label.item"/></h2>
					<ul class="pop_selectBox">
						<c:forEach items="${selectAllInfo.selectItmList}" var="itemInfo" varStatus="status">
							<li>${itemInfo.SCR_KOR}</li>
						</c:forEach>
					</ul>
				</div>
				
				<div class="select_lay_Right">
					<h2 class="bu_circle6"><pivot:msg code="ui.label.cond.time"/></h2>
					<ul class="pop_selectBox">
						<c:forEach items="${selectAllInfo.selectTimeList}" var="timeInfo" varStatus="status">
						<li>(${timeInfo.prdSeNm}) ${timeInfo.prdTime}</li>
						</c:forEach>
					</ul>
				</div>    
				<!-- div class 세팅 왼쪽:select_lay_Left 오른쪽:select_lay_Right -->
				<c:forEach items="${selectAllInfo.classInfoList}" var="classInfo" varStatus="status">
				<div <c:choose><c:when test="${status.count%2 eq 1}">class="select_lay_Left" </c:when><c:otherwise>class="select_lay_Right"</c:otherwise></c:choose> >
					<h2 class="bu_circle6"><a title="${classInfo.classNm}">${classInfo.classNm}</a></h2>
					<ul class="pop_selectBox">
					<c:forEach var="selection" items="${classInfo.classAllList}">
						<li><c:forEach var="i" begin="1" end="${selection.LVL}" step="1" varStatus="status">&nbsp;</c:forEach>${selection.SCR_KOR}</li>
					</c:forEach>	
					</ul>
				</div>
				</c:forEach>
			</div>
		</div>
 	</div> 
 </body>
 </html>
