<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="pop_cmmtInfoAll">
	<div id="pop_cmmtInfoAll2">
		<div class="pop_top">
			<pivot:msg code="ui.label.cmmtInfo"/><span class="closeBtn"><a href="javascript:popupControl('pop_cmmtInfoAll', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<div class="pop_title">
				<h1 class="bu_circle3">${statInfo.tblNm}</h1>
			</div>
			<div id="cmmtAll" class="con_lay2">
			</div>
		</div>
	</div>
</div>
