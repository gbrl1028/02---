<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div id="pop_sort">
	<div id="pop_sort2">
		<div class="pop_top4">
			<pivot:msg code="ui.label.dataSort"/><span class="closeBtn"><a href="javascript:popupSort('','hide')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<div class="pop_title">
				<h1 class="bu_circle"><span id="sortName"></span></h1>
			</div>
			<div class="con_lay4">
				<input id="dataSortAsc" name="dataSort" type="radio" value="0" checked="checked"/><pivot:msg code="ui.label.ascSort"/>
				<br/>
				<input id="dataSortDesc" name="dataSort" type="radio" value="1" /><pivot:msg code="ui.label.descSort"/>
			</div>
			<div class="btn_lay2"><span class="confirmBtn"><a href="javascript:fn_callSort();popupControl('pop_sort', 'hide', 'modal');"><pivot:msg code="ui.btn.accept"/></a></span></div>
		</div>
	</div>
</div>