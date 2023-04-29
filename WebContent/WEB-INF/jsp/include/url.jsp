<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div id="pop_url">
	<div id="pop_url2">
		<div class="pop_top">
			<pivot:msg code="ui.label.addrInfo"/><span class="closeBtn"><a href="javascript:popupControl('pop_url', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<input type="text" id="urlText" value="${statInfo.url}${pageContext.request.contextPath}${statInfo.defaultAction}?orgId=${ParamInfo.orgId}&amp;tblId=${ParamInfo.tblId}&amp;conn_path=I2<c:if test="${ParamInfo.language == 'en' }">&amp;language=en</c:if><c:if test="${ParamInfo.viewType == 'H'}">&amp;dbUser=${fn:substring(ParamInfo.dbUser,0,fn:length(ParamInfo.dbUser)-1)}</c:if>" 
				style="width:675px;height:20px;font-size:12px;" readonly="readonly"/>
			<label for="urlText"></label>
		</div>
	</div>
</div>