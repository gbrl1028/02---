<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<div id="pop_levelExpr">

	<div id="pop_levelExpr2">
		<div class="pop_top">
			<pivot:msg code="ui.label.title.main.levelExprHelp"/><span class="closeBtn"><a href="javascript:popupControl('pop_levelExpr', 'hide', 'help')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
				<img id="helpClose1" src="images/help/enableLevelExpr.help.png" alt="계층컬럼구분이란?"/>
			</c:if>
			<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
				<img id="helpClose1" src="images/help/enableLevelExpr.help_en.png" alt="Startified Column?"/>
			</c:if>
		</div>
	</div>
</div>
