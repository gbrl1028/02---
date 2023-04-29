<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
//계층컬럼구분 선택 시 상위레벨표시 체크값 해제 및 활성화/비활성화 처리
function controllParentLevel(flag){
	if(flag == 1){
		if($("#enableLevelExpr").is(':checked')){
			$("#enableParentLevel").attr("checked", false);
		}	
	}else{
		if($("#enableParentLevel").is(':checked')){
			$("#enableLevelExpr").attr("checked", false);
		}
	}
}

<%-- log를 쌓기위한..몸부림 --%>
function fn_changeTableType(){
	form.isChangedTableType.value = "Y";
}

function fn_changePeriodCo(){
	form.isChangedPeriodCo.value = "Y";
}

function fn_changePrdSort(){
	form.isChangedPrdSort.value = "Y";
}

function fn_apply(){
	
	//view_sub_kind 컬럼에 쌓을지 체크, jquery로 간단하게 처리
	var logText = "";
	
	if($("#isChangedTableType").val() == "Y"){
		logText += "A";
	}
	
	var dataOpt = g_dataOpt;
	var dataOpt2 = $("#dataOpt2 option:selected").val();
	if(dataOpt != dataOpt2){
		logText += "B";
	}
	
	if($("#isChangedPeriodCo").val() == "Y"){
		logText += "C";
	}
	
	if($(":checkbox[name='enableLevelExpr']").is(":checked") == true){
		logText += "D";
	}
	
	if($(":checkbox[name='enableParentLevel']").is(":checked") == true){
		logText += "E";
	}
	
	if($(":checkbox[name='enableCellUnit']").is(":checked") == true){
		logText += "F";
	}
	
	if($(":checkbox[name='enableWeight']").is(":checked") == true){
		logText += "G";
	}
	
	if($("#isChangedPrdSort").val() == "Y"){
		logText += "H";
	}
	
	//마지막 체크
	if(logText.length > 0){
		form.useAddFuncLog.value = "1_" + logText;
	}
	
	fn_search();
	popupControl('pop_addfunc', 'hide', 'modal');
}
</script>
<div id="pop_addfunc">
	<div id="pop_addfunc2">
		<div class="pop_top">
			<pivot:msg code="ui.label.title.main.addfunc"/><span class="closeBtn"><a href="javascript:popupControl('pop_addfunc', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content">
			<!-- 부가기능 설정 -->
			<div class="pop_title">
				<h1 class="bu_circle"><pivot:msg code="ui.label.title.sub.addfunc"/></h1>
			</div>
			<div class="con_lay">
				<div>
					<select id="tableType" name="tableType" style="width:110px; margin-right:10px;" onchange="fn_changeTableType()" title="${ParamInfo.language == 'en' ? 'Table Type' : '통계표 타입'}">
						<option value="standard" <c:if test="${ParamInfo.tableType == 'standard'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.standard"/></option>
						<option value="timeSeriesV" <c:if test="${ParamInfo.tableType == 'timeSeriesV'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.timeSeriesV"/></option>
						<option value="timeSeriesH" <c:if test="${ParamInfo.tableType == 'timeSeriesH'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.timeSeriesH"/></option>
<%-- 					<option value="perYear" <c:if test="${ParamInfo.tableType == 'perYear'}">selected</c:if> >년/월(분기)표</option> --%>
						<option value="default" <c:if test="${ParamInfo.tableType == 'default'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.default"/></option>
					</select>
					<c:choose>
						<c:when test="${ParamInfo.language == 'en'}">
							<select id="dataOpt2" name="dataOpt2" style="width:120px; margin-right:7px;" title="${ParamInfo.language == 'en' ? 'Language & Code' : '언어 및 코드'}">
								<option value="en" <c:if test="${ParamInfo.dataOpt == 'en'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.en"/></option>
								<c:if test="${statInfo.downloadable}">
									<option value="cden" <c:if test="${ParamInfo.dataOpt == 'cden'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.cden"/></option>
								</c:if>
							</select>
						</c:when>
						<c:otherwise>
							<select id="dataOpt2" name="dataOpt2" style="width:120px; margin-right:7px;" title="${ParamInfo.language == 'en' ? 'Language & Code' : '언어 및 코드'}">
								<option value="ko" <c:if test="${ParamInfo.dataOpt == 'ko'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.ko"/></option>
								<option value="en" <c:if test="${ParamInfo.dataOpt == 'en'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.en"/></option>
								<c:if test="${statInfo.downloadable}">
									<option value="cd" <c:if test="${ParamInfo.dataOpt == 'cd'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.cd"/></option>
									<option value="cdko" <c:if test="${ParamInfo.dataOpt == 'cdko'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.cdko"/></option>
									<option value="cden" <c:if test="${ParamInfo.dataOpt == 'cden'}">selected="selected"</c:if> ><pivot:msg code="ui.combo.cden"/></option>
								</c:if>
							</select>
						</c:otherwise>
					</c:choose>
					<select id="periodCo" name="periodCo" style="width:75px; margin-right:6px;" onchange="fn_changePeriodCo()" title="${ParamInfo.language == 'en' ? 'point' : '소수점'}">
						<option value="" <c:if test="${ParamInfo.periodCo == null}">selected="selected"</c:if> ><pivot:msg code="ui.combo.decimal"/></option>
						<option value="0" <c:if test="${ParamInfo.periodCo == '0'}">selected="selected"</c:if> >0</option>
						<option value="1" <c:if test="${ParamInfo.periodCo == '1'}">selected="selected"</c:if> >1</option>
						<option value="2" <c:if test="${ParamInfo.periodCo == '2'}">selected="selected"</c:if> >2</option>
						<option value="3" <c:if test="${ParamInfo.periodCo == '3'}">selected="selected"</c:if> >3</option>
						<option value="4" <c:if test="${ParamInfo.periodCo == '4'}">selected="selected"</c:if> >4</option>
						<option value="5" <c:if test="${ParamInfo.periodCo == '5'}">selected="selected"</c:if> >5</option>
					</select>
				</div>
				<ul class="setCheck">
					<!-- 2015.11.24 남규옥 추가 ::: statInfo.tblSe가 O 이면 분류없는 통계표. I 이면 분류없는 통계표 상속. 
						분류없는 통계표일 경우 '계층컬럼구분','상위레벨표시' 기능 숨김 -->
					<li <c:if test="${statInfo.tblSe == 'O' || statInfo.tblSe == 'I'}">style="display: none;"</c:if> ><input id="enableLevelExpr" name="enableLevelExpr" type="checkbox" value="Y" <c:if test="${statInfo.levelExpr == 'T'}">onclick="return(false)"</c:if> <c:if test="${ParamInfo.enableLevelExpr == 'Y'}">checked="checked"</c:if> <c:if test="${statInfo.levelExpr != 'T'}">onclick="controllParentLevel(1)"</c:if> /><span <c:if test="${statInfo.levelExpr == 'T'}">class="notFunctext"</c:if>> <label for="enableLevelExpr"><pivot:msg code="ui.label.levelExpr"/></label></span><a href="javascript:popupControl('pop_levelExpr','show','modal')"><img src="images/help/ico_help.gif" alt="<pivot:msg code="ui.label.levelExpr"/> ?"/></a></li>
					<li <c:if test="${statInfo.tblSe == 'O' || statInfo.tblSe == 'I'}">style="display: none;"</c:if> ><input id="enableParentLevel" name="enableParentLevel" type="checkbox" value="Y" <c:if test="${statInfo.levelExpr == 'T'}">disabled="disabled"</c:if> <c:if test="${ParamInfo.enableParentLevel == 'Y'}">checked="checked"</c:if> <c:if test="${statInfo.levelExpr != 'T'}">onclick="controllParentLevel(2)"</c:if>/><span <c:if test="${statInfo.levelExpr == 'T'}">class="notFunctext"</c:if>> <label for="enableParentLevel"><pivot:msg code="ui.label.parentLevel"/></label> </span><a href="javascript:popupControl('pop_parentLevel','show','modal')"><img src="images/help/ico_help.gif" alt="<pivot:msg code="ui.label.parentLevel"/> ?"/></a></li>
					<!-- <li><input id="enableCellUnit" name="enableCellUnit" type="checkbox" value="Y" <c:if test="${statInfo.dimUnitYn != 'Y'}">disabled="disabled"</c:if> <c:if test="${ParamInfo.enableCellUnit == 'Y'}">checked="checked"</c:if> /><span <c:if test="${statInfo.dimUnitYn != 'Y'}">class="notFunctext"</c:if>> <pivot:msg code="ui.label.unit"/></span></li> -->
					<li style="display: none;"><input id="enableCellUnit" name="enableCellUnit" type="checkbox" value="Y" <c:if test="${statInfo.dimUnitYn != 'Y'}">disabled="disabled"</c:if> <c:if test="${ParamInfo.enableCellUnit == 'Y' || (statInfo.dimUnitYn == 'Y' && ParamInfo.first_open == null) }">checked="checked"</c:if> /><span <c:if test="${statInfo.dimUnitYn != 'Y'}">class="notFunctext"</c:if>> <label for="enableCellUnit"><pivot:msg code="ui.label.unit"/></label> </span></li>
					<li><input id="enableWeight" name="enableWeight" type="checkbox" value="Y" <c:if test="${statInfo.wgtYn != 'Y'}">disabled="disabled"</c:if> <c:if test="${ParamInfo.enableWeight == 'Y'}">checked="checked"</c:if> /><span <c:if test="${statInfo.wgtYn != 'Y'}">class="notFunctext"</c:if>> <label for="enableWeight"><pivot:msg code="ui.label.weight"/></label></span></li>
				</ul>
				<div class="line_lay">
					<span><pivot:msg code="ui.label.addfunc.time.order"/> : 
					<input id="prdSortAsc" name="prdSort" type="radio" value="asc" onclick="fn_changePrdSort()" <c:if test="${ParamInfo.prdSort == 'asc'}">checked="checked"</c:if> />  <label for="prdSortAsc"><pivot:msg code="ui.label.addfunc.time.asc"/></label> 
					<input id="prdSortDesc" name="prdSort" type="radio" value="desc" onclick="fn_changePrdSort()" <c:if test="${ParamInfo.prdSort == 'desc'}">checked="checked"</c:if> /> <label for="prdSortDesc"><pivot:msg code="ui.label.addfunc.time.desc"/></label>
					</span>
				</div>
			</div>
			<div class="btn_lay"><span class="confirmBtn"><a href="javascript:fn_apply();"><pivot:msg code="ui.btn.accept"/></a></span></div>
			<!-- //부가기능 설정 -->
			<!-- 데이터찾기 -->
			<!-- <div class="pop_title"> -->
			<div class="pop_title"><h1 class="bu_circle"><pivot:msg code="ui.label.title.sub.findData"/></h1>
			</div>
			<div class="con_lay">
				<p>
					<input id="findData01" name="findData" type="radio" checked="checked"/> <label for="findData01"><pivot:msg code="ui.label.addfunc.findText"/></label> 
					<select id="findOption" title="${ParamInfo.language == 'en' ? 'sign of inequality' : '부등호'}">
						<option value="1">&lt;</option>
						<option value="2">&lt;=</option>
						<option value="3">=</option>
						<option value="4">&gt;=</option>
						<option value="5">&gt;</option>
					</select> 
					<label for="compValue"></label><input type="text" id="compValue" name="compValue" class="text" size="15" />
				</p>
				<p style="margin-top:5px;"><label for="findData02"></label><input id="findData02" name="findData" type="radio" /> <label for="compValue01"></label><input type="text" id="compValue01" name="compValue01" class="text" size="12" /> <label for="compValue02">&lt;= <pivot:msg code="ui.label.addfunc.findText"/> &lt;= </label><input type="text" id="compValue02" name="compValue02" class="text" size="12" /></p>
				<p class="f_point"><pivot:msg code="ui.label.addfunc.findCmmt"/></p>
			</div>
			<div class="btn_lay"><span class="confirmBtn"><a href="javascript:fn_dataSearch();"><pivot:msg code="ui.btn.addfunc.findData"/></a></span></div>
			<!-- //데이터찾기 -->
		</div>
	</div>
</div>
