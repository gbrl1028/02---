<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<div id="pop_parentLevel">
	<div id="pop_parentLevel2">
		<div class="pop_top">
			<pivot:msg code="ui.label.title.main.parentLevelHelp"/><span class="closeBtn"><a href="javascript:popupControl('pop_parentLevel', 'hide', 'help')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
				<img id="helpClose2" src="images/help/enableParentLevel.help.png" alt="상위레벨표시란"/>
			</c:if>
			<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
				<img id="helpClose2" src="images/help/enableParentLevel.help_en.png" alt="Display the upper level?"/>
			</c:if>
		</div>
	</div>
</div>
