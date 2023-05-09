<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
//<![CDATA[
function fn_clickFileType(){
	var fileType = $(":radio[name='downGridFileType']:checked").val();

	if(fileType == "sdmx"){
		$(".downList ul li:last span.flex").removeClass("notFunctext");

		$("#downGridSdmxTypeDsd").removeAttr("disabled");
		$("#downGridSdmxTypeData").removeAttr("disabled");
		$("#sdmxDataType").removeAttr("disabled");

		document.getElementById("downGridSdmxTypeData").checked = true;
	}else{
		$(":radio[name='downGridSdmxType']").each(function(){
			this.checked = false;
		});

		$("#downGridSdmxTypeDsd").attr("disabled", true);
		$("#downGridSdmxTypeData").attr("disabled", true);
		$("#sdmxDataType").attr("disabled", true);

		$(".downList ul li:last span.flex").addClass("notFunctext");
	}

	if(fileType == "xls" || fileType == "xlsx"){
		$(".downList ul li:first span.flex").removeClass("notFunctext");

		$("#downGridMeta").removeAttr("disabled");
		$("#downGridCellMerge").removeAttr("disabled");
	}else{
		$(".downList ul li:first span.flex").addClass("notFunctext");
		$("#downGridMeta").attr("disabled", true);
		$("#downGridCellMerge").attr("disabled", true);
	}
}

function fn_downGridSubmit(){
	var view = $(":radio[name='downGridFileType']:checked").val();
	var viewSubKind;

	//2020.09.21 xls 파일은 데이터가 256열 이상이면 에러남
	if(view == "xls"){
		if($("#mainTable thead").children("tr:last-child").children("th").length > 256){
			alert( "<pivot:msg code="alert.xls.msg"/>" );
			return;
		}
	}

	fn_searchCond();

	$(".confirmBtn").hide();
	$(".loadingBtn").show();

	var sort = $(":radio[name='prdSort']:checked").val();

	if( document.getElementById("downAsc").checked == true){
		document.getElementById("prdSortAsc").checked = true;
	}else{
		document.getElementById("prdSortDesc").checked = true
	}

	var dataOpt2 = $("#dataOpt2 option:selected").val();

	if( document.getElementById("codeYn").checked == true){
		if( dataOpt2 == "ko"){
			$("#dataOpt").val("cdko");
		}else if( dataOpt2 == "en"){
			$("#dataOpt").val("cden");
		}
	}

	var enableCellUnit = $("#enableCellUnit").val();

	if( document.getElementById("CellUnit_remote").checked == true){
		document.getElementById("enableCellUnit").checked = true;
	}else{
		document.getElementById("enableCellUnit").checked = false;
	}

	var periodCo = $("#periodCo option:selected").val();

	<%-- 2017-08-23 수록자료형식과 동일은 tn_dt에 들어가 있는 수치 그대로 적용(99는 의미없음 소수점설정에 영향받지 않은 오리지널 수치값을 가져오기 위한 코드값이라고 보면됌) - 이원영 --%>
	if( document.getElementById("PtypeOri").checked == true){
		$("#periodCo").append("<option value='99'>99</option>");
		$("#periodCo").val("99");
	}

	if( $(":checkbox[name='smblYn']").is(":checked") == true ){
		g_param_smblYnChk = "Y";
	}else{
		g_param_smblYnChk = "N";
	}

	if(view == "xls" || view == "xlsx"){
		if(view == "xls"){
			if( $(":checkbox[name='downGridCellMerge']").is(":checked") == true ){
				viewSubKind = "2_1_1";
			}else{
				viewSubKind = "2_2_1";
			}
		}else{
			if( $(":checkbox[name='downGridCellMerge']").is(":checked") == true ){
				viewSubKind = "2_1";
			}else{
				viewSubKind = "2_2";
			}
		}

	}else if(view == "csv"){
		viewSubKind = "2_3";
	}else if(view == "txt"){
		viewSubKind = "2_4";
	}else if(view == "sdmx"){
		var sdmxType = $(":radio[name='downGridSdmxType']:checked").val();
		if(sdmxType == "dsd"){
			viewSubKind = "2_6_D";
		}else{
			var sdmxDataType = $("#sdmxDataType").val();
			if(sdmxDataType == "generic"){
				viewSubKind = "2_6_G";
			}else{
				viewSubKind = "2_6_C";
			}
		}
	}

	form.smblYn.value = g_param_smblYnChk;
	form.view.value = view;
	form.viewSubKind.value = viewSubKind;
	form.viewKind.value = "2";

	$.ajax({
		dataType : 'json',
		type : 'POST',
		url : '<%=request.getContextPath()%>/downGrid.do',
		data : $("#ParamInfo").serialize(),
		success : function(response,status){
			var file = response.file;


			form.action = "<%=request.getContextPath()%>/downNormal.do";
			form.file.value = file;
			form.submit();
			form.file.value = "";

			$(".confirmBtn").show();
			$(".loadingBtn").hide();
		},
		error : function(error){
			alert( "<pivot:msg code="fail.common.msg"/>" );
		}
	});

	form.viewSubKind.value = "";
	form.smblYn.value = "";

	if( sort == "asc"){
		document.getElementById("prdSortAsc").checked = true;
	}else{
		document.getElementById("prdSortDesc").checked = true
	}

	$("#dataOpt").val(dataOpt2);

	if( enableCellUnit == "Y"){
		document.getElementById("enableCellUnit").checked = true;
	}else{
		document.getElementById("enableCellUnit").checked = false;
	}

	if( document.getElementById("PtypeOri").checked == true){
		$("#periodCo option:last").remove();
		$("#periodCo").val(periodCo);
	}
}
//]]>
</script>
<div id="pop_downgrid" <c:if test="${statInfo.massYn == 'Y' && fn:indexOf(statInfo.serverType, 'service') >=0 && fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0}">style="height:560px;"</c:if>>
	<div id="pop_downgrid2">
		<div class="pop_top">
			<pivot:msg code="ui.label.fileDownload"/><span class="closeBtn"><a href="javascript:popupControl('pop_downgrid', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		<div class="pop_content2">
			<div class="pop_title2">
				<span class="btn_r grayBtn"><a href="javascript:fn_metaDown('grid');"><pivot:msg code="ui.btn.metaDown"/></a></span>
			</div>
			<div class="downList">

				<div style="margin-top:5px; position:relative;">
					<div style="width:250px;"><h1 class="bu_circle"><pivot:msg code="ui.label.fileType"/></h1></div>
					<div style="padding-right:5px; position:absolute; top:1px; right:0px;">
						<span><input id="smblYn" name="smblYn" type="checkbox" value="Y"<c:if test="${statInfo.smblYn == 'Y'}">checked="checked"</c:if> /><label for="smblYn">&nbsp;<pivot:msg code="ui.checkbox.smblYn"/></label></span>
						<span <c:if test="${statInfo.dimUnitYn != 'Y'}">style="display:none;"</c:if>><input id="CellUnit_remote" name="CellUnit_remote" type="checkbox" value="Y" <c:if test="${statInfo.dimUnitYn != 'Y'}">disabled="disabled"</c:if> <c:if test="${ParamInfo.enableCellUnit == 'Y' || (statInfo.dimUnitYn == 'Y' && ParamInfo.first_open == null) }">checked="checked"</c:if> /><label for="CellUnit_remote">&nbsp;<pivot:msg code="ui.label.unit"/></label></span>
						<span><input type="checkbox" id="codeYn" name="codeYn" value="Y" /><label for="codeYn">&nbsp;<pivot:msg code="ui.checkbox.includCode"/></label></span>
					</div>
				</div>
				<div class="fileList">
					<ul>
						<li>
							<input id="excelradio" type="radio" name="downGridFileType" value="xlsx" onclick="fn_clickFileType()" checked="checked"/> <img src="images/ico_excel.png" alt="" /><label for="excelradio">EXCEL(xlsx)</label>
							<input id="excelradio2" type="radio" name="downGridFileType" value="xls" onclick="fn_clickFileType()" style="margin-left:10px;"/> <img src="images/ico_excel.png" alt="" /><label for="excelradio2">EXCEL(xls)</label>
							<!--  <span style="display:none;"><pivot:msg code="ui.label.metaInclude"/></span> -->
							<label for="downGridCellMerge" style="margin-left:15px;">(</label><input type="checkbox" name="downGridCellMerge" id="downGridCellMerge" value= "Y" checked="checked" /> <span><label for="downGridCellMerge"><pivot:msg code="ui.label.cellPlus"/></label></span> <input type="checkbox" name="downGridMeta" id="downGridMeta" style="margin-left:20px; display:none;" value="Y" checked="checked"></input><label for="downGridMeta">)</label>
						</li>
						<li>
							<input id="csvradio" type="radio" name="downGridFileType" value="csv" onclick="fn_clickFileType()"/> <img src="images/ico_csv.png" alt="" /><label for="csvradio">CSV</label>
						</li>
						<li>
							<input id="txtradio" type="radio" name="downGridFileType" value="txt" onclick="fn_clickFileType()"/> <img src="images/ico_txt2.png" alt="" /><label for="txtradio">TXT</label>
						</li>
						<li>
							<input id="jsonradio" type="radio" name="downGridFileType" value="json" onclick="fn_clickFileType()"/> <img src="images/ico_txt2.png" alt="" /><label for="txtradio">JSON</label>
						</li>
						<li>
							<input id="xmlradio" type="radio" name="downGridFileType" value="xml" onclick="fn_clickFileType()"/> <img src="images/ico_txt2.png" alt="" /><label for="txtradio">XML</label>
						</li>
						<%--
						<li>
							<input id="sdmxradio" type="radio" name="downGridFileType" value="sdmx" onclick="fn_clickFileType()"/> <img src="images/ico_sdmx.png" alt="" /><label for="sdmxradio"><span class="sdmxDown">SDMX(2.0)</span></label>
							&nbsp;<b><span class="notFunctext flex"><label for="downGridSdmxTypeDsd">[</label></span></b><input type="radio" name="downGridSdmxType" id="downGridSdmxTypeDsd" value="dsd" style="margin-left:3px;" disabled="disabled"/>
							<span class="notFunctext flex"><pivot:msg code="ui.label.dsd"/></span>
							<input type="radio" name="downGridSdmxType" id="downGridSdmxTypeData" value="data" style="margin-left:3px;" disabled="disabled"/><label for="downGridSdmxTypeData"><span class="notFunctext flex">DATA</span></label>
							<select name="downGridSdmxDataType" id="sdmxDataType" disabled="disabled" style="margin-right:3px; height:18px;" title="${ParamInfo.language == 'en' ? 'SDMX Type' : 'SDMX 타입'}" >
								<option style="width:60px;" value="generic">Generic</option>
								<option style="width:60px;" value="compact">Compact</option>
							</select>
							<b><span class="notFunctext flex">]</span></b>
						</li>
						 --%>
					</ul>
				</div>
				<div style="margin-top:10px; position:relative;">
					<div style="width:250px;"><h1 class="bu_circle" style="width:250px;"><pivot:msg code="ui.label.downLarge.time.order"/></h1></div>
					<div style="padding-right:5px; position:absolute; top:1px; right:0px;"></div>
				</div>
				<div class="fileList">
					<input type="radio" name="downSort" id="downAsc" value="asc" checked="checked"/> <label for="downAsc"><pivot:msg code="ui.label.downLarge.time.asc"/> </label>
					<input type="radio" name="downSort" id="downDesc" value="desc" /> <label for="downDesc"><pivot:msg code="ui.label.downLarge.time.desc"/></label>
				</div>

				<div style="margin-top:10px; position:relative;">
					<div style="width:250px;"><h1 class="bu_circle" style="width:250px;"><pivot:msg code="ui.label.downLarge.point.type"/></h1></div>
				</div>
				<div class="fileList">
					<input type="radio" name="pointType" id="PtypeOri" value="original"/> <label for="PtypeOri"><pivot:msg code="ui.label.downLarge.point.original"/></label>
					<input type="radio" name="pointType" id="PtypeScr" value="screen" checked="checked"/> <label for="PtypeScr"><pivot:msg code="ui.label.downLarge.point.screen"/> </label>
				</div>

			</div>

			<div class="btn_lay" >
				<span class="confirmBtn"><a href="javascript:fn_downGridSubmit();"><pivot:msg code="ui.btn.download"/></a></span>
				<span class="loadingBtn" style="display:none;"><pivot:msg code="ui.text.wait.down"/></span>
			</div>

			<c:if test="${fn:indexOf(statInfo.serverType, 'service') >=0 && fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0 }">
				<c:if test="${statInfo.massYn == 'Y'}">
					<c:if test="${ParamInfo.language == 'ko'}">
					<div class="shortLay">
						<h2 class="arr_blue">통계표 파일서비스</h2>
						<p class="text">- 자료량이 많은 통계표로 미리 생성한 파일을 제공합니다. </p>
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
