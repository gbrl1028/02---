<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="/WEB-INF/jsp/common/taglibs.jsp"%>
<table>
	<tr>
		<td style="padding-top:0px;">
			<select id="startPrdDe" name="startPrdDe" class="date" onchange="fnSelectPrdDe()">
			<c:forEach items="${prdList}" var="item">
				<option value="<c:out value="${item.PRD_DE }" />">&nbsp;<c:out value="${item.PRD_DE_STR }" /></option>
			</c:forEach>
			</select>
			~
			<select id="endPrdDe" name="endPrdDe" class="date" onchange="fnSelectPrdDe()">
			<c:forEach items="${prdList}" var="item">
				<option value="<c:out value="${item.PRD_DE }" />">&nbsp;<c:out value="${item.PRD_DE_STR }" /></option>
			</c:forEach>
			</select>
			<a id="fnSelect" href="javascript:fnSelectPrdDe();"><img src="images/direct/searchBtn.gif" alt="검색" border="0" style="width:30px; height:18px; vertical-align:bottom;"/></a>
		</td>
	</tr>
	<tr>
		<td>
			<div class="dataBox">
				<ul>
					<c:forEach items="${prdList}" var="item">
					<li style="display:none;">
						<input type="checkbox" id="<c:out value="${item.PRD_DE }" />" name="PRD_DE" onclick="cntPrdDe(this)" value="<c:out value="${item.PRD_DE }" />">
						<label for="<c:out value="${item.PRD_DE}"/>"> <c:out value="${item.PRD_DE_STR}"/></label>
					</li>
					</c:forEach>
				</ul>
			</div>
		</td>
	</tr>
</table>