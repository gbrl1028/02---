<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<c:choose>
	<c:when test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
<link type="text/css" rel="stylesheet" href="ext/css/common/base_eng.css" />
<link type="text/css" rel="stylesheet" href="ext/css/common/popup_eng.css" />
	</c:when>
	<c:otherwise>
<link type="text/css" rel="stylesheet" href="ext/css/common/base.css" />
<link type="text/css" rel="stylesheet" href="ext/css/common/popup.css" />
	</c:otherwise>
</c:choose>
<script type="text/javascript" src="ext/js/jquery/jquery-1.9.1.js"></script>
<script type="text/javascript" src="ext/js/common/json2.js"></script>

<script type="text/javascript">
//<![CDATA[
	var g_rightMoveCnt = 0;
	var g_actionLeft = 0;
	var g_actionRight = 0;
	var g_gridSn="";		 //tabMenuClick 후 변수에 저장
	var g_liStatus="";
	var g_tabClickCnt=0;	//tabMenu click 카운트
	var g_liCnt=0;
	var g_periodYn="";
	var g_analType = "${ParamInfo.analType}";
	var g_analCmpr = "${ParamInfo.analCmpr}";
	var g_analClass ="${ParamInfo.analClass}";
	var g_originData = "${ParamInfo.originData}";
	var g_analTime ="${ParamInfo.analTime}";
	var g_analCmprFlag;
	var g_doAnal = "${ParamInfo.doAnal}";
	var g_firstComboYn = "Y";
	//비교기준이 없는 경우도 있다...
	var g_compareCnt = "${fn:length(assayInfo.compareTypeList)}";		//분류갯수만

	var g_param_analType;
	var g_param_analCmpr;
	var g_param_analItem="";
	var g_param_analClass;
	var g_param_originData;
	var g_selectTxt;
	var g_param_analCombo = new Array();
	var g_tabClassCnt =  "${fn:length(assayInfo.classInfoList)}";		//tabLi 갯수
	var g_moveCnt = g_tabClassCnt - 5;									//tabLi default 갯수 5개
$(document).ready(function(){
	<%--
	/************************************************************************
	함수명  : $(document).ready(function()
	설   명 : 1.팝업의 넓이는 nso.jsp의 fn_assay()에서 지정
			 2.iframe인 관계로 paramInfo에 analType 세팅해놓고 click이벤트 트리거 analType없으면 기본세팅 구성비/누계구성비인 경우 화면 넒힘
			 3.class 와 item div 따로 처리(tabClass/tabItem 주의)
			 4.bind click이벤트에 trigger가 안걸림.ㅠ 펑션 처리
			 5. object 전달에 주의하면서 ~jquery 짱이죠;해석은 알아서하는걸로 분석업그레이드~
	인   자 :
	사용법 :
	작성일 : 2013-11-22
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2013-11-02      안영수              최초 생성
	************************************************************************/
	--%>
	fn_init();

	<%--비교기준 마지막 li 아래에 기준월,년...선택 붙이기--%>
	$(".rightListLi:last").attr("class","rightLi");

	<%--trigger 안걸려서 쌍으로 움직이는 function으로 처리
	//분석종류
	--%>
	$('input[name^="assayLeft"]').bind({
		click: function(){
			fn_analTypeTrigger($(this));
 		}
	});

	<%--//비교기준--%>
	$('input[name="assayRight"]').bind({
		click: function(){
			fn_analCmprTrigger($(this));
		}
	});

	<%--//분류/항목 --%>
	$('input[name^="selectType"]').bind({
		click: function(){
			fn_classTrigger($(this));
		}
	});

	<%--//기준선택 여부 --%>
	$('input[name^="assayselectType"]').bind({
		click: function(){
			var selectType = $(":radio[name='assayselectType']:checked").val();
			if(selectType == "select2"){
 		 		// 글자색 회색으로
				$("[name=gridList]").each(function(i){
	 	 			$(this).css("color","grey");
				});
				$(g_liColor).css("background-color","white");
				$("#analItemList option").remove();
			}else{
				// 글자색 원래대로
 		 		$("[name=gridList]").each(function(i){
	 	 			$(this).css("color","black");
				});
			}
		}
	});

/* 	$('[id=triggerLeft]').bind({
		click:function(){
			var assayTypeCmmt = null;
			assayTypeCmmt =	$(":radio[name='assayLeft']:checked").val();
			alert(assayTypeCmmt);
		}
	}); */

	<%--// 선택된 li에 배경색 변경--%>
	$('[name=gridList]').bind({
		<%--// 2017.11.07 굳이 클릭하고 더블클릭하고 나눠놓을 필요가 없다고 느껴서 수정함 - 김기만사무관 (통계청장 사용중 문의) 
		click: function(){

			var selectType = $(":radio[name='assayselectType']:checked").val();
			if(selectType=="select1"){
				if(g_liCnt > 0){
					$(g_liColor).css("background-color","white");
				}
				$(this).css("background-color","#ffd700");
				g_liColor = $(this);
				g_liCnt++;
				g_selectTxt = $(this).text();
				g_param_analItem = $(this).find("input").val();
			}else{

			}
		},
		
		dblclick: function(){
		--%>
		click: function(){
 			var selectType = $(":radio[name='assayselectType']:checked").val();
			if(selectType=="select1"){
				<%--// 위에 클릭에 있던거 -시작- --%>
				if(g_liCnt > 0){
					$(g_liColor).css("background-color","white");
				}
				$(this).css("background-color","#ffd700");
				g_liColor = $(this);
				g_liCnt++;
				g_selectTxt = $(this).text();
				g_param_analItem = $(this).find("input").val();
				<%--// 위에 클릭에 있던거 -끝- --%>
				
				$("#analItemList option").remove();
				$("#analItemList").attr("disabled",false);
				var analItemCnt = $("#analItemList option").size();
				var existCnt = 0;
				var optionValue= g_liStatus+"!@#"+g_param_analClass+"!@#"+g_param_analItem;
				var indexArr = new Array();
				<%--// 			var poIndex = g_gridSn.split("_")[1];--%>
				var poIndex = g_liStatus.toString();

				if(analItemCnt == 0){
					if(g_param_analClass != "ITEM"){
						$("#analItemList").prepend("<option value='"+optionValue+"' selected>"+g_selectTxt+"</option>");
						$("#analItemList").prepend("<option value=0_All_Del><pivot:msg code="ui.label.clearAll"/></option>");
					}else{
						$("#analItemList").prepend("<option value='"+optionValue+"' selected>"+g_selectTxt+"</option>");
					}
				}else{
					<%--//더블클릭하는 순간 selectBox option체크 없는것만 after 시키기~--%>
					$("#analItemList option").each(function(){
						var compStr = $(this).attr("value");
						var compGrid = compStr.split("!@#")[0];					<%--//tab구분 status--%>
						var compObjId 	= compStr.split("!@#")[1]; 				<%--//tab 메뉴의 objVarId--%>
						var compItemId     = compStr.split("!@#")[2];			<%--//grid의 ItemId--%>
						<%--//전체해제 option index가 존재한다는걸~ --%>
						if(g_param_analClass == compObjId){
							existCnt++;
						}
						<%--
						//추가될 option index 구하기
					//	var opIndex = compGrid.split("_")[1];			//compGrid
					--%>
						indexArr.push(compGrid);							<%--//기존 세팅된 옵션의 순번 index 정렬되어있음...--%>
					});


					var test = $.inArray(poIndex,indexArr);

					if($.inArray(poIndex,indexArr) < 0){
						indexArr.push(poIndex);
			    		indexArr.sort(function(a,b){return a-b;});			<%--//정렬  항목은100 맨 마지막임.--%>
					}

					var addIndex;
					$.each(indexArr,function(index,item){
						if(item == poIndex){
							addIndex = index;
						}
					});

					<%--//옵션에 세팅 0이면 신규 1이면 교체!--%>
					if(existCnt == 0){
						addIndex--;
						$("#analItemList option:eq("+addIndex+")").after("<option value='"+optionValue+"' selected>"+g_selectTxt+"</option>");
					}else{
						$("#analItemList option:eq("+addIndex+")").replaceWith("<option value='"+optionValue+"' selected>"+g_selectTxt+"</option>");
					}
				}
			}else{
				// 기준선택 안함 일 경우 아무것도 하지 않음
			}
		}
	});
});


	function fn_init(){

		var assayRightObj;
		if(g_analType == ""){
			<%--
			//원자료 함께보기 체크
			//2013.11.29 원자료 함께보기 체크해제
			//$("#originData").prop("checked",true);
			--%>

			var assayleftObj = $("#triggerLeft li:eq(0)").find("input");			<%--//g_analType없을때 default세팅을 위한 분석 종류ojbect--%>
			fn_analTypeTrigger(assayleftObj);
			assayRightObj = $("#triggerRight li:eq(0)").find("input");		<%--//g_analCmp없을때 default세팅을 위한 비교 기준ojbect--%>
			fn_analCmprTrigger(assayRightObj);
		}else{
			<%--//분석종류 세팅--%>
			$('[name=assayLeft]').each(function(index){
				var statusChk = $(this).val();

				if(statusChk == g_analType){
					$(this).prop("checked",true);
					fn_analTypeTrigger($(this));

					try{
						$("#cmmt_"+g_analType).css("display", "");
					}catch(e){}

				}
			});

			if(g_analCmpr ==""){
				assayRightObj = $("#triggerRight li:eq(0)").find("input");		<%--//구성비또는 누계구성비 분석시 비교기준 초기화되므로 다른 분석종류 클릭시 디폴트 전월비 세팅--%>
				fn_analCmprTrigger(assayRightObj);
			}else{
				<%--//비교기준 세팅--%>
				$("[name=assayRight]").each(function(index){
					var statusChk = $(this).val();
					if(statusChk == g_analCmpr){
						$(this).prop("checked",true);
						fn_analCmprTrigger($(this));
					}
				});
			}


			<%--//시점 세팅--%>
			if(g_analTime !=""){
				$("#standardTime").val(g_analTime);
				<%--//	$("#standardTime > option[@value="+g_analTime+"]").attr("selected","true");--%>
			}

			<%--//원자료 세팅 --%>
			if(g_originData =="Y"){
				$("#originData").prop("checked",true);
			}else{
				$("#originData").prop("checked",false);
			}


		}
	}

	function fn_analTypeTrigger(analType){
		var assayType = analType.val();
 	 	parent.fn_assay(assayType);						<%--//클릭시 화면 넓이 지정--%>
 	 	<%--//	$(parent.document).find("input[id=analType]").val(assayType);		//부모창에 paramInfo.analType세팅 --%>
 	 	analType.prop("checked",true);
 	 	g_param_analType = assayType;
 	 	//if(assayType == "TOTL_CMP_RATE" || assayType == "CMP_RATE" || assayType == "TOTL"){
 	 	if(assayType == "TOTL_CMP_RATE" || assayType == "TOTL"){
	 		$('#selectTypeItem').css("visibility","hidden");
 			$('#itemTitle').css("visibility","hidden");
 			$('#selectTypeClass').css("visibility","hidden");
 			$('#classTitle').css("visibility","hidden");
 	 		$("input[name=assayRight]").each(function(i){
				$(this).attr("disabled", true);
			});
			g_analCmprFlag="N";
			$("#standardTime").attr("disabled", true);
 	 	}else if(assayType == "CHG_RATE_CO"){
 			$("input[name=assayRight]").each(function(i){
				$(this).attr("disabled", false);
				var standard = $(this).val();
				if(standard == g_param_analCmpr){
					$("#standardTime").attr("disabled", false);
				}else{
					$("#standardTime").attr("disabled", true);
				}
			});
			g_analCmprFlag="Y";

			$('input[name=selectType]').each(function(index){
				$(this).css("visibility","hidden");
			});
			$("#classTitle").text("");
			$("#itemTitle").text("");
			// 2015.5.19 구성비 선택 할 경우 항목 라디오 버튼 보이지 않게
	  	 	}else if(assayType == "CMP_RATE"){
	  	 		$('#selectTypeItem').css("visibility","hidden");
 	 			$('#itemTitle').css("visibility","hidden");
 	 			$('#selectTypeClass').css("visibility","hidden");
 	 			$('#classTitle').css("visibility","hidden");
		}else{
			$("input[name=assayRight]").each(function(i){
				$(this).attr("disabled", false);
				var standard = $(this).val();
				if(standard == g_param_analCmpr){
					$("#standardTime").attr("disabled", false);
				}else{
					$("#standardTime").attr("disabled", true);
				}
			});
			g_analCmprFlag="Y";
		}

 	 	<%--//g_analClass 뒤에 , 붙음..--%>
 	 	g_analClass = g_analClass.replace(',', '');
 	 	if(g_analClass == "ITEM"){
<%--
//  	 		$("input[name=selectType]").each(function(index){
// 				var statusT = $(this).val();
// 				if(g_analClass == statusT){
// 					$(this).attr("checked",true);
// 				}
// 			});
--%>
 	 		analClass = $("input[name=selectType]").eq(1);
<%--
	//		var assayleftObj = $("#triggerLeft li:eq(0)").find("input");			//g_analType없을때 default세팅을 위한 분석 종류ojbect
	//		$("#tabItem").css("display","block");
	//		$("#tabClass").css("display","none");
	//		$("#assayItemList").css("display","block");
// 			var g_analCombo = JSON.parse("${ParamInfo.analCombo}");
// 	 		for(var i in g_analCombo){
// 	 			var tmp = g_analCombo[i];
// 	 			alert("tmp"+tmp.value);
// 	 			alert("tmp"+tmp.text);
// 	 		}
--%>
		}else{												<%--//분석팝업만 호출하고 분석시작버튼을 누르지 않을 경우..--%>
			<%--//			analType.prop("checked",true);--%>
			<%--//트리거로 변경 임시
			//$(parent.document).find("input[id=analCmpr]").val($("#compareType li:eq(0)").find("input").val());
			--%>
			$('[name=selectType]').each(function(index){
				if(index == 0){
					$(this).prop("checked",true);
					analClass = $(this);
				}
			});

			analClass = $("input[name=selectType]").eq(0);		//분류 선택
		}
 	 	$(analClass).attr("checked",true);
 	 	fn_classTrigger(analClass);
	}

	function fn_analCmprTrigger(analCmpr){
		if(g_compareCnt > 0){
			var analCmprStr = 	analCmpr.val();
			analCmpr.prop("checked",true);
			var standard = analCmprStr.split("_")[0];
			if(standard == "ONE"){

				g_standardPeriod = analCmprStr.split("_")[1];
				$("#standardTime").attr("disabled", false);
				g_periodYn = "Y";
			}else{
				$("#standardTime").attr("disabled", true);
				g_periodYn = "N";
			}
			<%--//		$(parent.document).find("input[id=analCmpr]").val(analCmprStr); --%>
			g_param_analCmpr = analCmprStr;
		}else{
			<%--//비교기준이 없는 경우--%>
			$("#standardTime").css("display","none");
			<%--//분석종류-> 증감,증감률 비활성화 체크해제 하기--%>
			$("#triggerLeft li:eq(0)").attr("disabled",true);
			$("#triggerLeft li:eq(1)").attr("disabled",true);
			$("#triggerLeft li:eq(0)").find("input").prop("checked",false);
		}
	}

	function fn_classTrigger(analClass){
		$("#analItemList option").remove();
		$("#analItemList").attr("disabled",true);

		 if(analClass.is(":checked")){
			if(analClass.val() == "ITEM"){
				$("#tabClass").css("display","none");
				$("#tabItem").css("display","block");
				<%--//분류의 tabMenu를 한번도 click 안했으면 0번 display--%>
				if(g_tabClickCnt ==0){
					$("#assayClassList_1").css("display","none");
				}else{
					$("#assayClassList_"+g_liStatus).css("display","none");
					fn_autoCombo(g_liStatus);
				}
				$("#assayItemList_100").css("display","block");
				<%--//		$(parent.document).find("input[id=analClass]").val("ITEM");--%>

				g_param_analClass = "ITEM";
			}else{
				$("#tabItem").css("display","none");
				$("#tabClass").css("display","block");
				$("#assayItemList_100").css("display","none");
				<%--//분류의 tabMenu를 한번도 click 안했으면 0번 display--%>
<%--// 			if(g_tabClickCnt ==0){--%>
					$("#assayClassList_1").css("display","block");
					fn_display('assayClassList_1',1);
<%--
// 				}else{
// 					alert("g_liStatus"+g_liStatus);
// 					$("#assayClassList_"+g_liStatus).css("display","block");
// 				}
--%>
				$('[name=rememberLi]').each(function(index){
					var classObjVarId = $(this).find("input").val();
					<%--//li class = tab_on 설정--%>
					var paramYn = $(this).attr("class");
					if(paramYn =="tab_on"){
						g_param_analClass = $(this).find("input").val();
					}
				});
			}

			<%--//최초에 한번만 실행하고 나머지는 remove상태 유지후 선택값 세팅해야함.--%>
			if(g_doAnal =="Y"){
				if(g_firstComboYn =="Y"){
					firstSetCombo(g_param_analClass);
					$("#analItemList").attr("disabled",false);
				}
			}else{
				$("#analItemList").attr("disabled",true);
			}

		 }
	}

	function firstSetCombo(g_param_analClass){
		<%--//콤보박스세팅--%>
		if(g_param_analClass == "ITEM"){
			if($("#analCombo").val()!=""){
				var jsonArr = JSON.parse($("#analCombo").val());
				for( var i in jsonArr){
					var tmp = jsonArr[i];
					$("#analItemList").prepend("<option value='"+tmp.value+"' selected>"+tmp.text+"</option>");
					}
			}
		}else{
			if($("#analCombo").val()!=""){
				var jsonArr = JSON.parse($("#analCombo").val());
				$("#analItemList").prepend("<option value=0_All_Del><pivot:msg code="ui.label.clearAll"/></option>");
				for( var i in jsonArr){
					var tmp = jsonArr[i];
					$("#analItemList option:eq("+i+")").after("<option value='"+tmp.value+"'>"+tmp.text+"</option>");
					}

			}
		}
		g_firstComboYn = "N";
	}

	<%--//분류,항목 tabMenu 나눈다고 생각하고 코딩들어가자--%>
	<%--//2017.11.06 확인결과 분류탭이 5개 이상이면 마우스로 선택을 못해서 화살표로 이동하도록 해줌--%>
	function fn_move(direction){
		var classTabCnt = $('[id=tabMove] li').size(); <%--//right방향 move한계--%>

		if(direction =="left"){
			if(g_moveCnt <= 0 ){
				if(g_rightMoveCnt > 0 ){
					var showCnt = 4+g_rightMoveCnt;
					$("[id=tabMove] li:eq("+showCnt+")").hide();
					g_rightMoveCnt--;
					$("[id=tabMove] li:eq("+g_rightMoveCnt+")").show();
					g_actionLeft = showCnt;
					g_moveCnt++;
				}
			}
		}else{
			if(g_moveCnt > 0){
				if(g_actionRight < classTabCnt-1){
					$("[id=tabMove] li:eq("+g_rightMoveCnt+")").hide();
					g_rightMoveCnt++;
					var showCnt = 4+g_rightMoveCnt ;
					$("[id=tabMove] li:eq("+showCnt+")").show();
					g_actionRight = showCnt;
				}
				g_moveCnt--;
			}
		}
	}

	function fn_display(gridSn,liStatus){
		if(g_tabClickCnt >0){
			$("#"+g_gridSn).css("display","none");
			$("#classli"+g_liStatus).attr("class","tab_off");
		}else{
			$("#assayClassList_1").css("display","none");
			$("#classli1").attr("class","tab_on");
			<%--//			grisSn = assayClassList_1;							//최초에 열리는 tab은 분류의 첫번째...--%>
			liStatus = 1;
		}

		$("#"+gridSn).css("display","block");
		$("#classli"+liStatus).attr("class","tab_on");
		g_gridSn = gridSn;
		g_liStatus = liStatus;
		g_tabClickCnt++;

		var radioStatus = $('input[name=selectType]:checked').val();

		if(radioStatus != "ITEM"){
			$('[name=rememberLi]').each(function(index){
				var clickStatus = $(this).attr("class");

				if(clickStatus == "tab_on"){
					var classObjVarId = $(this).find("input").val();
					<%--//				$(parent.document).find("input[id=analClass]").val(classObjVarId);--%>
					g_param_analClass = classObjVarId;
				}
			});
		}

		fn_autoCombo(g_liStatus);
	}


	function fn_autoCombo(g_liStatus){

		var comboCnt = $("#analItemList option").size();
		if(comboCnt > 0){
			$("#analItemList option").each(function(){
				var compStr = $(this).attr("value");
				var compGrid = compStr.split("_")[0];					<%--//tab구분 status--%>
				if(g_liStatus == compGrid){
					$(this).attr("selected","selected");
				}
			});
		}
	}

	function fn_setDel(){
		var comboCnt = $("#analItemList option").size();
		if(comboCnt ==0){
			alert("<pivot:msg code="alert.selectBaseData.msg"/>");
			return;
		}
		var delMode	= $("#analItemList option:selected").val().split("_")[0];

		if(delMode == 0){
			<%--//다지우기--%>
			$("#analItemList option").remove();
		}else{
			<%--
			//선택된것만 지우기
			//분류일경우 2개면 전체삭제도 같이 지움
			--%>
			if(comboCnt > 2){
				$("#analItemList option:eq("+delMode+")").remove();
			}else{
				$("#analItemList option").remove();
			}
		}
	}

	function fn_assayStart(){
		var standardTime;

		<%-- 기준자료 선택 유무에 따라 작업 처리--%>
		var selectType = $(":radio[name='assayselectType']:checked").val();

		<%-- 2015.4.6 분석 구성비 기준자료 선택여부에 따른 계산 방식 변경--%>
		if(selectType == "select1"){
			var noselectType = "Select";
			$(parent.document).find("input[id=noSelect]").val(noselectType);
			<%--//비교기준이 없는 경우-> 분석종류 디폴트 체크상태를 false로 세팅 되므로 구성비/누계/누계구성비 체크상태 확인--%>
			var tempCnt = $('[name=assayLeft]:checked').size();	//분석 종류 체크 갯수
			if(tempCnt == 0){
				alert("<pivot:msg code="alert.selectAssayType.msg"/>");
				return;
			}

			if(g_param_analType == "TOTL_CMP_RATE" || g_param_analType == "CMP_RATE" || g_param_analType =="CHG_RATE_CO"){
				var analItemCnt = $("#analItemList option").size();
				if(analItemCnt == 0){
					alert("<pivot:msg code="alert.selectBaseData.msg"/>");
					return;
				}

				<%--//구성비와 누계구성비 파라미터 세팅 자바단 배열처리이므로..1건이라도 ,붙여서 가야함..--%>
				var selectType = $(":radio[name='selectType']:checked").val();
				if(selectType == "ITEM"){
					var g_param_analClass = "ITEM,";
					g_param_analItem = g_param_analItem+",";

					var tmpObj = {};

					tmpObj.value = $("#analItemList option:selected").val();
					tmpObj.text = $("#analItemList option:selected").text();

					g_param_analCombo.push(tmpObj);

					<%--// 				$(parent.document).find("input[id=analCombo]").val(g_param_analCombo.join(","));--%>
					$(parent.document).find("input[id=analCombo]").val(JSON.stringify(g_param_analCombo));
					$(parent.document).find("input[id=analClass]").val(g_param_analClass);
					$(parent.document).find("input[id=analItem]").val(g_param_analItem);

				}else{
					<%--//분류 한개또는 여러개인 경우--%>
					var classArr = new Array();
					var itemArr = new Array();
					if(analItemCnt > 2){

						$("#analItemList option").each(function(index){
							var tmpObj ={};
							if(index > 0){
								var compStr = $(this).attr("value");
								var compObjId 	= compStr.split("!@#")[1]; 				//tab 메뉴의 objVarId
								var compItemId     = compStr.split("!@#")[2];				//grid의 ItemId

								classArr.push(compObjId);
								itemArr.push(compItemId);

								alert(classArr[index]);
								alert(itemArr[index]);

								tmpObj.value = compStr;
								tmpObj.text = $(this).text();
								g_param_analCombo.push(tmpObj);
							}
						});

						$(parent.document).find("input[id=analClass]").val(classArr.join(","));
						$(parent.document).find("input[id=analItem]").val(itemArr.join(","));
					}else{
						var oneObj = $("#analItemList option:last");
						var classOne = oneObj.attr("value").split("!@#")[1]+",";
						var itemOne = oneObj.attr("value").split("!@#")[2]+",";

						var tmpObj = {};

						tmpObj.value = oneObj.val();
						tmpObj.text = oneObj.text();
						g_param_analCombo.push(tmpObj);

						$(parent.document).find("input[id=analClass]").val(classOne);
						$(parent.document).find("input[id=analItem]").val(itemOne);
					}
					<%--//				$(parent.document).find("input[id=analCombo]").val(g_param_analCombo.join(","));--%>
					$(parent.document).find("input[id=analCombo]").val(JSON.stringify(g_param_analCombo));
				}
			}

			<%--//2013.11.29 이규정추가 가중치 체크해제--%>
			$(parent.document).find(":checkbox[id='enableWeight']").attr("checked", false);

			if(g_periodYn =="Y"){
				<%--// 			standardTime = 	$("#standardTime option:selected").attr("value");		//원본데이터 세팅 --%>
				standardTime = 	$("#standardTime option:selected").val();		//원본데이터 세팅
				$(parent.document).find("input[id=analTime]").val(standardTime);		//시점 세팅
			}

			if(g_analCmprFlag =="N"){
				<%--//disabled 된상태는 파라미터 넘어가면 안됨 g_param_analCmpr 초기화--%>
				g_param_analCmpr="";
			}

			var originChk = $("#originData").prop("checked");
			if(originChk == true){
				g_param_originData = "Y";
			}else{
				g_param_originData = "N";
			}
			$(parent.document).find("input[id=originData]").val(g_param_originData);
			$(parent.document).find("input[id=doAnal]").val("Y");

			$(parent.document).find("input[id=analType]").val(g_param_analType);
			$(parent.document).find("input[id=analCmpr]").val(g_param_analCmpr);

			parent.popupControl('pop_assay', 'hide', 'modal');
			parent.fn_search();

		}else if(selectType == "select2"){
			var noselectType = "noSelect";
			$(parent.document).find("input[id=noSelect]").val(noselectType);

			if(g_periodYn =="Y"){
				<%--// 			standardTime = 	$("#standardTime option:selected").attr("value");		//원본데이터 세팅 --%>
				standardTime = 	$("#standardTime option:selected").val();		//원본데이터 세팅
				$(parent.document).find("input[id=analTime]").val(standardTime);		//시점 세팅
			}

			if(g_analCmprFlag =="N"){
				<%--//disabled 된상태는 파라미터 넘어가면 안됨 g_param_analCmpr 초기화--%>
				g_param_analCmpr="";
			}

			var originChk = $("#originData").prop("checked");
			if(originChk == true){
				g_param_originData = "Y";
			}else{
				g_param_originData = "N";
			}
			$(parent.document).find("input[id=originData]").val(g_param_originData);
			$(parent.document).find("input[id=doAnal]").val("Y");

			$(parent.document).find("input[id=analType]").val(g_param_analType);
			$(parent.document).find("input[id=analCmpr]").val(g_param_analCmpr);


			parent.popupControl('pop_assay', 'hide', 'modal');

			parent.fn_search();
		}
	}

	function fn_assayCanCel(){
		$(parent.document).find("input[id=doAnal]").val("N");
		parent.popupControl('pop_assay', 'hide', 'modal');
		parent.fn_search();
	}

	<%-- 2015.4.13 분석 구성비 기준자료 선택시 분류코드, 분류값코드 전달을 위해--%>
	function fn_assaySelect(){

		var itemOne = $("#assayClassList_1 li:eq(0) input:eq(0)").val();
		var classOne = g_param_analClass;

		$(parent.document).find("input[id=analClass]").val(classOne);
		$(parent.document).find("input[id=analItem]").val(itemOne);

		//alert($("#assayClassList_1 li:eq(0) input:eq(0)").val()+ " ----- " + g_param_analClass);
	}

	function fn_assayTypeCmmt(obj){

		var assayTypeCmmt =	obj.value;
		var cmmt_leng = document.getElementsByName("assayList_cmmt").length;

 		for(var i = 1 ; i <= cmmt_leng ; i++){
 			$(document).find("label[name=assayList_cmmt]").css("display", "none");
		}

		$(document).find("label[id=cmmt_"+assayTypeCmmt+"]").css("display", "");
	}

//]]>
</script>
</head>
<body onload="parent.callbackForIframe('pop_assay');">
<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="dataOpt"/>
	<form:hidden path="analType"/>
	<form:hidden path="analCmpr"/>
	<form:hidden path="analTime"/>
	<form:hidden path="analClass"/>
	<form:hidden path="analItem"/>
	<form:hidden path="analCombo"/>
</form:form>
	<div id="ifr_assay" style="width:830px;">
	<c:set var="viewMode" value="${ParamInfo.dataOpt}"/>
		<div class="pop_content3">
			<div class="assay_lay">
				<div class="pop_title">
					<h1 class="bu_circle"><pivot:msg code="ui.label.title.type"/></h1>
				</div>
				<div id="triggerLeft" class="assayListDiv">
					<ul class="assayList">		<!-- 원자료 100%있다면 -->
						<c:forEach var="leftList" items="${assayInfo.assayTypeList}" begin="1" end="${fn:length(assayInfo.assayTypeList)}" step="1" varStatus="status">
							<c:choose>
								<c:when test="${fn:indexOf(viewMode,'ko') > -1}">
								<li><input name="assayLeft" id="assayLeft_${status.count}" type="radio" value="${leftList.funcCode}" onchange="fn_assayTypeCmmt(this)" /><label for="assayLeft_${status.count}"> ${leftList.funcName}</label></li>
								</c:when>
								<c:when test="${fn:indexOf(viewMode,'en') > -1}">
								<li><input name="assayLeft" id="assayLeft_${status.count}" type="radio" value="${leftList.funcCode}" onchange="fn_assayTypeCmmt(this)" /><label for="assayLeft_${status.count}"> ${leftList.funcEnName}</label></li>
								</c:when>
								<c:otherwise>
								<li><input name="assayLeft" id="assayLeft_${status.count}" type="radio" value="${leftList.funcCode}" onchange="fn_assayTypeCmmt(this)" /><label for="_${status.count}"> ${leftList.funcName}</label></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</ul>
				</div>
			</div>

			<div class="assay_lay">
				<div class="pop_title">
					<h1 class="bu_circle"><pivot:msg code="ui.label.title.compare"/></h1>
				</div>
				<div id="triggerRight" class="assayListDiv">
					<ul class="assayList">
						<c:forEach var="rightList" items="${assayInfo.compareTypeList}" varStatus="status">
							<c:choose>
								<c:when test="${fn:indexOf(viewMode,'ko') > -1}">
								<li class="rightListLi"><input name="assayRight" id="assayRight" type="radio" value="${rightList.funcPrdCode}" /><label for="assayRight"> ${rightList.funcPrdName}</label></li>
								</c:when>
								<c:when test="${fn:indexOf(viewMode,'en') > -1}">
								<li class="rightListLi"><input name="assayRight" id="assayRight" type="radio" value="${rightList.funcPrdCode}" /><label for="assayRight"> ${rightList.funcPrdEnName}</label></li>
								</c:when>
								<c:otherwise>
								<li class="rightListLi"><input name="assayRight" id="assayRight" type="radio" value="${rightList.funcPrdCode}" /><label for="assayRight"> ${rightList.funcPrdName}</label></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<span class="btn_r">
							<select id="standardTime" disabled="disabled" style="width:130px; margin-left:18px;">
							<c:forEach var="timeList" items="${assayInfo.assayTimeList}" varStatus="status">
								<option value="${timeList.prdSe}${timeList.prdDe}">${timeList.viewPrdDe}</option>
							</c:forEach>
							</select>
						</span>
					</ul>
				</div>
			</div>

			<div class="pop_title3" style="padding-top:0px;">
			</div>
			<p><input id="originData" type="checkbox"/> <pivot:msg code="ui.label.originData"/></p>
			<div class="btn_lay4">
				<span class="confirmBtn"><a style="cursor:pointer;" onclick="fn_assayStart();"><pivot:msg code="ui.btn.start"/></a></span>
				<span <c:if test="${ParamInfo.doAnal =='N'}">style="display:none"</c:if> class="confirmBtn"><a href="#" onclick="fn_assayCanCel();"><pivot:msg code="ui.btn.quit"/></a></span>
			</div>
			<!--2015.10.21 분석 주석 추가  -->
			<!-- <li name="assayCmmt" id="assayCmmt" style="color:blue; ">※&nbsp테스트</li> -->
			<div>
				<ul style="clear:both; color: blue; width: 415px; ">
				<c:forEach var="assayList" items="${assayInfo.assayTypeCmmtList}" begin="0" end="${fn:length(assayInfo.assayTypeCmmtList)}" step="1" varStatus="status">
					<c:choose>
						<c:when test="${fn:indexOf(viewMode,'ko') > -1}">
							<label name="assayList_cmmt" <c:if test="${assayList.funccode ne 'CHG' }"> style="display:none;" </c:if> id="cmmt_${assayList.funccode}" > ※&nbsp${assayList.cmmt}</label>
						</c:when>
						<c:when test="${fn:indexOf(viewMode,'en') > -1}">
							<label name="assayList_cmmt" <c:if test="${assayList.funccode ne 'CHG' }"> style="display:none;" </c:if> id="cmmt_${assayList.funccode}" > ※&nbsp${assayList.cmmteng}</label>
						</c:when>
						<c:otherwise>
							<label name="assayList_cmmt" id="cmmt_${assayList.funccode}">※&nbsp${assayList.cmmt}</label>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</ul>
			</div>
		</div>

			<!-- 2015.4.1 분석 구성비 기준자료선택여부 -->
			<div class="assaySelect">
				<div class="assaySelect1">
					<h1 class="assaySelect2"><img src="ext/css/common/images/ico_arrGreen.gif" alt=""/> <pivot:msg code="ui.assay.select"/></h1>
				</div>
				<div class="assaySelect3">
		           	<input  type="radio" value="select1" checked="checked" name="assayselectType" id="selectTypeTrue"/><span id="trueTitle"><label for="selectTypeTrue"><pivot:msg code="ui.assay.selectTrue"/></label></span>
		            <input  type="radio" value="select2" name="assayselectType" id="selectTypeFalse" onclick="fn_assaySelect();" <c:if test="${ParamInfo.noSelect =='noSelect'}">checked</c:if>/><span id="falseTitle"><label for="selectTypeFalse"><pivot:msg code="ui.assay.selectFalse"/></label></span>
		        </div>
			</div>

		<div class="assayClass">
			<div class="checkbox">
                <ul>
                    <li class="left">
                        <input  type="radio" value="" name="selectType" id="selectTypeClass"/><span id="classTitle"><label for="selectTypeClass"><pivot:msg code="ui.radio.class"/></label></span>
                        <input  type="radio" value="ITEM" name="selectType" id="selectTypeItem" style="margin-left:3px;" /><span id="itemTitle"><label for="selectTypeItem"><pivot:msg code="ui.radio.item"/></label></span>
                    </li>
                    <li class="right">
<!--                    <input id="selectTxt" type="text" class="text" size="25"/> -->
<!--                    <span class="smallgrayBtn"><a href="#">해제</a></span> -->
						<span><pivot:msg code="ui.assay.base"/></span>
						<select id="analItemList" style="width:150px; ">
							<%-- objVarId, ItemId, itemNm만 있으면 됨
<!-- 							<option value="0_All_Del">전체해제</option> --%>
						</select>
						<span class="smallgrayBtn"><a style="cursor:pointer;" onclick="javscript:fn_setDel();"><pivot:msg code="ui.assay.cancel"/></a></span>
                    </li>
                </ul>
            </div>
            <!-- 분류탭세팅 -->
            <div id="tabClass" class="assayTab" style="display:none">
                <ul id="tabMove">
                	<c:forEach var="tabClass" items="${assayInfo.classInfoList}" varStatus="status">
                	<li id="classli${status.count}" name="rememberLi" class="tab_off first" style="<c:if test="${status.count > 5}">display:none;</c:if>">
                	<a href="javascript:fn_display('assayClassList_${status.count}',${status.count});">
                	<input type="hidden" value ="${tabClass.classId}"/>${tabClass.classNm}</a></li>
                    </c:forEach>
                </ul>

            </div>

            <div id="tabItem" class="assayTab" style="display:none">
 				<ul id="tabMove">
                   	<li id="classli${status.count}" class="tab_on first">
                    <a href="javascript:fn_display('assayItemList','100');"><pivot:msg code="ui.label.item"/></a></li>
           		</ul>
           </div>

            <p class="arrowBtn"><a style="cursor:pointer;"><img src="images/dataAssay/btn_arrow_left2.png" alt="" onclick="fn_move('left');"/></a>
                					<a style="cursor:pointer;"><img src="images/dataAssay/btn_arrow_right2.png" alt="" onclick="fn_move('right');" /></a></p>

      		<!-- 분류별 리스트 세팅 -->
            <c:forEach var="classList" items="${assayInfo.classInfoList}" varStatus="status">
<%--            <c:set var="selectionClassList" value="${classList.classAllList}"/> --%>
            	 <c:choose>
            	 	<c:when test="${status.count eq 1}">
            	 	<div class="assayTabList" id="assayClassList_${status.count}" style="display:none"/>
            	 	</c:when>
            	 <c:otherwise>
            	 	<div class="assayTabList"  id="assayClassList_${status.count}" style="display:none;"/>
            	 </c:otherwise>
            	 </c:choose>
            	<c:forEach var="classGrid" items="${classList.classAllList}">
	                <ul>
	                    <li name="gridList"><input type="hidden" value="${classGrid.ITM_ID}"/><c:out value="${classGrid.SCR_KOR}"/></li>
	                </ul>
	            </c:forEach>
	            </div>
            </c:forEach>


           	<div class="assayTabList"  id="assayItemList_100" style="display:none;">
           	 <c:forEach var="itemList" items="${assayInfo.assayItmList}" varStatus="status">
           	   <ul>
	                <li name="gridList"><input type="hidden" value="${itemList.ITM_ID}"/><c:out value="${itemList.SCR_KOR}"/></li>
	           </ul>
           	 </c:forEach>
           	</div>
		</div>
 	</div>
</body>
</html>
