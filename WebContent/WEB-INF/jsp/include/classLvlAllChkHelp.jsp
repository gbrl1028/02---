<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<div id="pop_classLvAllChkHelp">
	<div id="pop_classLvAllChkHelp2">
	<c:if test="${ParamInfo.dataOpt == 'ko'}">	
		<div class="pop_top">
			레벨별 전체선택<span class="closeBtn"><a href="javascript:popupControl('pop_classLvAllChkHelp', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<div class="pop_title">
				<h1 class="bu_circle">기능설명</h1>
				<p style="margin:3px 0;">- <b>상위레벨 선택(일부)값에 상관없이</b> 해당레벨 전체가 선택됩니다.</p>
				<p style="margin:3px 0;">- 왼쪽 위 <b>일괄설정은</b> 분류, 항목 및 시점을 보다 편리하게 설정할 수 있는 기능입니다.</p>
				<p style="margin-top:15px; text-align:center;"><img src="images/help/classLvAllChkHelp.png" alt="레벨별 전체선택이란"/></p>
			</div>
		</div>
	</c:if>
	<c:if test="${ParamInfo.dataOpt == 'en'}">
		<div class="pop_top">
			Select all data by level<span class="closeBtn"><a href="javascript:popupControl('pop_classLvAllChkHelp', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<div class="pop_title">
				<h1 class="bu_circle">Function explanation</h1>
				<p style="margin:3px 0;">- All data in the relevant level are selected regardless of values in the <br/><span style="margin-left:10px;">upper level.</span></p>
				<p style="margin:3px 0;">- Batch settings in the upper left enable users to set classification, items <br/><span style="margin-left:10px;">and time more conveniently.</span></p>
				<p style="margin-top:15px; text-align:center;"><img src="images/help/classLvAllChkHelp_en.png" alt="Level Select all"/></p>
			</div>
		</div>
	</c:if>
	</div>
</div>
