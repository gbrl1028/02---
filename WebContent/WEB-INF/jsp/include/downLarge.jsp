<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
//<![CDATA[
window.onload = function(){
	$("#downLarge_lay").show();
	$("#loading_lay").hide();
}
function fn_downLargeSubmit(){
	
	fn_searchCond();
	
	var view = $(":radio[name='downLargeFileType']:checked").val();
	var viewSubKind;
	
	<%--
	// 	var x = $("input:checkbox[name='exprYn']").val();
	
	//TO-DO excel의 경우 셀 병합케이스에 따라 구분되어야 함. sdmx 추가
	--%>
	if(view == "excel"){
		viewSubKind = "2_7_1";
	}else if(view == "csv"){
		viewSubKind = "2_7_2";
	}
	form.view.value = view;
	form.viewSubKind.value = viewSubKind;
	form.itemMultiply.value = g_itemMultiply;
	form.dimCo.value = g_dimCo;
	//2013.12.26 viewKind 수정
	form.viewKind.value = "2"; <%--웹표준에서 조회제한셀초과다운로드--%>
	
<%-- 	fn_progressBar('show'); --%>
$("#downLargeMetaHref").attr("href", "#");
$("#downLarge_lay").hide();
$("#loading_lay").show();
	$.ajax({
		dataType : 'json',
		type : 'POST',
		url : "<%=request.getContextPath()%>/makeLarge.do",
		data : $("#ParamInfo").serialize(),
		success : function(response,status){
<%-- 			fn_progressBar('hide'); --%>
			$("#loading_lay").hide();
			$("#downLarge_lay").show();
			$("#downLargeMetaHref").attr("href", "javascript:fn_metaDown('large');");
			var file = response.file;
			form.action = "<%=request.getContextPath()%>/downLarge.do?file=" + file;
			form.submit();
			<%-- 초기화 --%>
			form.viewSubKind.value = "";
		},
		error : function(error){
			<%-- fn_progressBar('hide'); --%>
			$("#loading_lay").hide();
			$("#downLarge_lay").show();
			$("#downLargeMetaHref").attr("href", "javascript:fn_metaDown('large');");
			<%-- 초기화 --%>
			form.viewSubKind.value= "";
			
			alert( "<pivot:msg code="fail.common.msg"/>" );
		}
	});
	<%-- 초기화 --%>
	form.viewSubKind.value = ""; // 2014.04.25 대용량다운로드가 오래걸려 취소하고 조회를 하면 조회에 다운로드 코드가 찍힘으로 수정 - 김경호
}
//]]>
</script>
<div id="pop_downlarge" <c:if test="${statInfo.massYn == 'Y' && fn:indexOf(statInfo.serverType, 'service') >=0 && fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0 }">style="height:490px;"</c:if>>
	<div id="pop_downglarge2">
		<div class="pop_top">
		<pivot:msg code="ui.label.fileDownload"/><span class="closeBtn"><a href="javascript:popupControl('pop_downlarge', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content2">
			<div class="pop_title2">
				<span class="btn_r grayBtn"><a id="downLargeMetaHref" href="javascript:fn_metaDown('large');"><pivot:msg code="ui.btn.metaDown"/></a></span>
			</div>
			<div class="downLargeList">
				<h1 class="bu_circle"><pivot:msg code="ui.label.fileType"/></h1>
				<div class="fileList">
					<p>
						<input type="radio" name="downLargeFileType" id="downLargeExcel" value="excel" checked="checked"/> <img src="images/ico_excel.png" alt="EXCEL" title="<pivot:msg code="ui.label.excel"/>"/><label for="downLargeExcel"> EXCEL</label> 
						<input type="radio" name="downLargeFileType" id="downLargeCSV" value="csv" style="margin-left:10px;" /> <img src="images/ico_csv.png" alt="CSV" title="<pivot:msg code="ui.label.csv"/>"/><label for="downLargeCSV"> CSV</label> 
					</p>
				</div>
				<div style="margin-top:10px; position:relative;">
					<div style="width:250px;"><h1 class="bu_circle" style="width:250px;"><pivot:msg code="ui.label.exprType"/></h1></div>
					<div style="padding-right:5px; position:absolute; top:1px; right:0px;"><input type="checkbox" id="exprYn" name="exprYn" value="Y" /><label for="exprYn">&nbsp;<pivot:msg code="ui.checkbox.includCode"/></label></div>
				</div> 
				<div class="fileList">
					<p><input type="radio" name="downLargeExprType" id="downLargeTimeH" value="1" checked="checked"/><label for="downLargeTimeH"> <pivot:msg code="ui.radio.exprType.periodTop"/></label></p>
					<p><input type="radio" name="downLargeExprType" id="downLargeTimeV" value="2"/><label for="downLargeTimeV"> <pivot:msg code="ui.radio.exprType.itemTop"/></label></p>
				</div>
				<h1 class="bu_circle" style="margin-top:10px;"><pivot:msg code="ui.label.downLarge.time.order"/></h1>
				<div class="fileList">
					<input type="radio" name="downLargeSort" id="downLargeAsc" value="asc" checked="checked"/><label for="downLargeAsc"> <pivot:msg code="ui.label.downLarge.time.asc"/></label> 
					<input type="radio" name="downLargeSort" id="downLargeDesc" value="desc" style="margin-left:10px;" /><label for="downLargeDesc"> <pivot:msg code="ui.label.downLarge.time.desc"/></label>
				</div>
			</div>
			
			<div id="downLarge_lay" class="downLarge_lay">
				<span id="downLargeBtn" class="downLargeBtn"><a href="javascript:fn_downLargeSubmit();"><pivot:msg code="ui.btn.download"/></a></span>
			</div>
			<div id="loading_lay" class="loading_lay">
				<span class="loadingBtn"><pivot:msg code="ui.text.wait.down"/></span>
			</div>
			
			<!-- 2018.07.18 서비스이면서 url에 kosis.kr이 들어갈때만 파일서비스 보여줄것 - 여인철 주무관 -->
			<c:if test="${fn:indexOf(statInfo.serverType, 'service') >=0 && fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0 }">
				<c:if test="${statInfo.massYn == 'Y'}">
					<c:if test="${ParamInfo.language == 'ko'}">
					<div class="shortLay">
						<h2 class="arr_blue">통계표 파일서비스</h2>
						<p class="text">- 자료량이 많은 통계표로 미리 생성한 파일을  제공합니다. </p>
						<p class="goBtn"><a href="javascript:openMass('K');"><img src="images/shortcutBtn.gif" alt="바로가기" /></a></p>
					</div>
					</c:if>
					
					<c:if test="${ParamInfo.language == 'en'}">
					<div class="shortLay">
						<h2 class="arr_blue">Statistical Table File Service</h2>
						<p class="text">- Provides pre-generated files with high volume statistics. </p>
						<p class="goBtn"><a href="javascript:openMass('E');"><img src="images/shortcutBtn_eng.gif" alt="Shotcut" /></a></p>
					</div>
					</c:if>
				</c:if>
			</c:if>
			
		</div>
	</div>
</div>
