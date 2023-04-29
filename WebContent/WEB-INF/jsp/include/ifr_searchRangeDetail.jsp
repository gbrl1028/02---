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
<script type="text/javascript" src="ext/js/jquery/jquery-ui-1.10.3.custom.js"></script>
</head>
<body onload="parent.callbackForIframe('pop_detailfunc');" style="min-width:740px">
<script type="text/javaScript">
//<![CDATA[
var g_classCloneArr = new Array();

var g_timeCnt;
var g_classLength = "${fn:length(rangeInfo.classInfoList)}";				<%--분류총갯수--%>
var g_defaultTabSn = 0;
var p_classAllChkYn = parent.$("#p_classAllChkYn").val();
var p_logicFlag = parent.$("#p_logicFlag").val();
var p_classAllSelectYn = parent.$("#p_classAllSelectYn").val();
$(document).ready(function(){
	var obj ={};
	var g_viewId ="";
	<c:forEach items="${rangeInfo.classInfoList}" var="classInfo" varStatus="statusClass">
		var tempClassArr = new Array();
		<c:forEach items="${classInfo.classAllList}" var="classList" varStatus="status">
			obj ={};
			obj.lvlInfo = "${classList.lvl}";
			obj.value = "${classList.itmId}"+"@"+"${classList.rownum}";
			obj.text	 = "${classList.scrKor}";
			tempClassArr.push(obj);
		</c:forEach>
		g_classCloneArr.push(tempClassArr);
	</c:forEach>


	g_timeCnt  = parent.$("#selectTimeRange").val();

	fn_rangeCountText();

	//일괄설정에서 접근:Y 메인 NSO에서 접근 :N
	if(p_classAllChkYn != "Y"){
		fn_parentClassAll();
	}

	<%--단어검색 포커스 함수--%>
	$("#compValue").focus(function(){
		//$("#compValue").attr("value","");	// 2014.09.12 익스 9 이상부터 이상하게 안먹음
		$("#compValue").val("");
		$("#compValue").css("color","black");
	});

	<%--
	$("#tabs").tabs({
		create:function(event,ui){

		}
	});

	//시점 탭클릭시 이벤트 잡기
	$("#tabs").on("tabsbeforeactivate",function(event,ui){
		if(g_viewId !=""){
			$("#"+g_viewId).css("display","none");
		}else{
			$("#tabs-1_1").css("display","none");
		}
		var tab_id= ui.newPanel.attr("id");

		var cloneDiv = tab_id+"_"+tab_id.split("-")[1];
		alert("cloneDiv"+cloneDiv);
		$("#"+cloneDiv).css("display","block");
		g_viewId = cloneDiv;
	});
	--%>

	<%--탭메뉴 click li --%>
	$("#detailTab").on("click","li",function(e){
		var tabStatus = $(this).attr("class");			<%--속성--%>
		var tabSn = $(this).index();					<%--순번--%>

		if(tabStatus != "tab_on"){

			<%--li 순서로 컨트롤--%>
			$("#detailTab li:eq("+g_defaultTabSn+")").attr("class","tab_off");
			$("#detailTab li:eq("+tabSn+")").attr("class","tab_on");

			$("#timeLeft_"+g_defaultTabSn).css("display","none");
			$("#timeLeft_"+tabSn).css("display","block");

			$("#timeRight_"+g_defaultTabSn).css("display","none");
			$("#timeRight_"+tabSn).css("display","block");

		}
		g_defaultTabSn = tabSn ;
	});
	
	$("#detailTab").find("li").each(function(){

		var tabSn = $(this).index();					<%--순번--%>
		var tabId = $(this).attr("id");					<%--ID--%>
		var tabText = $(this).html();					<%--TEXT--%>

		if($("#dataOpt").val().indexOf('en') >= 0){
			if(tabId == 'M'){
				var mon_arry =["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
				$("#samePrdse_"+tabSn).find("option").each(function(){
					var leftValue = this.value;
					if(leftValue != ''){
						this.text = mon_arry[leftValue-1];
					}
				});
			}else if(tabId == 'Q'){
				$("#samePrdse_"+tabSn).find("option").each(function(){
					var leftValue = this.value;
					if(leftValue != ''){
						this.text = leftValue + " Quarter";
					}
				});				
			}
		}else{
			if(tabId == 'M' || tabId == 'Q'){
				$("#samePrdse_"+tabSn).find("option").each(function(){
					var leftValue = this.value;
					if(leftValue != ''){
						this.text = leftValue + " " +tabText;
					}
				});
			}			
		}
	});

});


	<%--
	/************************************************************************
	함수명 : fn_parentClassAll()
	설   명 : 파라미터 주의//전체레벨 체크상태인가? 레벨별 전체 체크상태인가?주의
	인   자 : 메인화면 p_chkStatus,p_objVarId,p_lvl 세팅값으로 구분
	사용법 :
	작성일 : 2014-04-08
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2014-04-08      안영수              최초 생성
	************************************************************************/
	--%>
	function fn_parentClassAll(){
		<%-- 메인 nso.jsp 분류별 전체체크/분류별 레벨별 전체체크 --%>
		var p_chkStatus = parent.$("#p_chkStatus").val();
		var p_objVarId	= parent.$("#p_objVarId").val();
		var p_lvl		= parent.$("#p_lvl").val();

		var selectId = $('select[name='+p_objVarId+']').attr("id");		 //nso.jsp에서 넘어온 objVarId로 셀렉트박스 id 구하기

		if(p_lvl =="all"){
			//분류레벨전체 상태로 만들기 fn_mode 실행
 			if(p_chkStatus == "true"){
 				fn_mode('addAll',selectId,'class');
			}else{
				fn_mode('backAll',selectId);
			}
		}else{
			var boxSn;
			//분류별 레벨 전체상태로 만들기
			//분류별 레벨순서
			//1.mathMark "=" 변경 2.lvlMark "p_lvl"로 변경 기존로직 안건드리고 function호출
			<c:forEach items="${rangeInfo.classInfoList}" var="classInfo" varStatus="status">
				var classSn = "${status.count}";												//항목이 0이므로
				var defaultClassId = "${rangeInfo.classInfoList[status.index].classId}";
				var maxLevel	   = "${rangeInfo.classInfoList[status.index].depthLvl}";
				if(defaultClassId == p_objVarId){
					boxSn = classSn;						//boxSn 구하기
				}
			</c:forEach>

			//좌측 콤보박스 세팅
			$("#mathMark_"+boxSn+" option:selected").val("2");			//"=" 로 변경
			$("#lvlMark_"+boxSn+" option:selected").val(p_lvl);			//nso.jsp에서 체크한 분류 레벨값 세팅

			fn_changeSet(boxSn);										//mathMakr,lvlMark 이벤트등록
 			if(p_chkStatus == "true"){
 				//추가는 전체레벨또는
 				fn_mode('addAll',selectId,'class');
			}else{
				//우측 분류정보 레벨단위로 삭제 class에 lvl세팅 값이랑 비교
				$("#selectRight_"+boxSn).find("option").each(function(){
					 var classLvl = $(this).attr("class");
					 if(p_lvl == classLvl){
						 $(this).remove();
					 }
				});
			}
		}

		if(p_chkStatus !=""){
			parent.fn_progressBar("hide");
		}

		fn_definite();
	}

	function fn_rangeCountText(){
		g_textField="";

		var p_multiplication = 0; 			<%--항목,분류,시점 조합수--%>
		<%--항목 갯수--%>
		p_itmCnt = $("#selectRight_0").find("option").size();

		var textClassArr = new Array();
		var p_classCnt = 1;
		var p_classViewCntStr="";
		var p_timeCnt = 0;
		<%--분류 갯수--%>
		for(var ii=1; ii<=g_classLength; ii++){
			var rightClassCnt = $("#selectRight_"+ii).find("option").size();
			textClassArr.push(rightClassCnt);
			p_classCnt = rightClassCnt * p_classCnt;				<%--분류곱--%>
		}

		$.each(textClassArr,function(index,item){
			if(index+1 == textClassArr.length){
				p_classViewCntStr += item;
			}else {
				p_classViewCntStr += item+"*";
			}
		});


		<%--시점갯수--%>
		<c:forEach items="${rangeInfo.tabTimeList}" varStatus="status" var="tabTime">
			var tabIndex = "${status.index}";
			var timeCnt  = $("#selectTimeRight_"+tabIndex).find("option").size();
			p_timeCnt+=timeCnt;
		</c:forEach>

		g_timeCnt = p_timeCnt ;

		p_multiplication = p_itmCnt * p_classCnt * g_timeCnt;

		g_textField = "("+p_itmCnt+") X <pivot:msg code="ui.label.cond.class"/>("+p_classViewCntStr+") X <pivot:msg code="ui.label.cond.time"/>("+g_timeCnt+") = "+p_multiplication+""; //분류 갯수 개별

		$("#changeTextLi").text(g_textField);
	}


	<%--
	/************************************************************************
	함수명 : fn_definite()
	설   명 : p_classAllChkYn == "Y" 인 경우 메인화면에서 분류별전체선택/분류별레벨전체선택으로 갯수0체크 안함
	인   자 :
	사용법 :
	작성일 : 2013-11-01
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2013-11-01      안영수              최초 생성
	************************************************************************/
	--%>
	function fn_definite(){
		definiteCnt = g_classCloneArr.length+1;

		if(p_classAllChkYn =="Y"){				//일괄설정에서
			for(var i=0; i<definiteCnt; i++){
				var rightCnt = $("#selectRight_"+i).find("option").size();
				if(rightCnt == 0){
					alert( "<pivot:msg code="fail.searchRange.msg" />" );
					return;
				}
			}
		}

		if(g_timeCnt == 0){
			alert("<pivot:msg code="fail.searchTimeRange.msg"/>");
			return;
		}

		<%--
		//기간은 반드시 1개이상의 시점이 선택되어야 합니다.

		//항목은 1레벨만 존재하므로 nso.jsp와 동기화 처리
		//분류만 g_defaultClass랑 동기화시켜보자..
		//모든 항목/분류가 1개이상 선택되어 있는 경우
		--%>
		var itemRangeArr = new Array();

		$("#selectRight_0").find("option").each(function(index){
			itemRangeArr.push(this.value.split("@")[0]);
		});

		var makeJson1 = new Array;
		var makeJson2 = new Array;
		var makeJson3 = new Array;
		<%--
		//분류리스트 세팅
		//nso.jsp g_defaultArr 세팅과 비슷한 패턴임 1레벨을 뺀 나머지 레벨 세팅하기
		--%>
		<c:forEach items="${rangeInfo.classInfoList}" var="classInfo" varStatus="status">
			var defaultClassId = "${rangeInfo.classInfoList[status.index].classId}";
			var maxLevel	   = "${rangeInfo.classInfoList[status.index].depthLvl}";
			var classCntObj ={};
			for(var jj=1; jj<=maxLevel; jj++){
				var classIdArr = new Array();
				var obj ={};
				$("#selectRight_"+"${status.count}").find("option").each(function(index){
					var objVarId = $(this).parent().attr("name").split("@")[0];
					var level	 = $(this).attr("class");
					var itmId 	 = this.value.split("@")[0];

					if(defaultClassId == objVarId ){
						if(jj == level){
							classIdArr.push(itmId);
						}
					}
				});

				if(classIdArr.length > 0){						<%--분류리스트가 있는것만--%>
					obj.objVarId = defaultClassId;
					obj.classType = jj;
					obj.data = classIdArr;
					obj.classLvlCnt = classIdArr.length;		<%--메인화면에서 분류별 전체레벨/분류별 레벨별 전체선택 동기화를 위한 카운트--%>
					makeJson1.push(obj);
				}
			}

			var selectRightCnt=$("#selectRight_"+"${status.count} option").size();

			//갯수 array
			classCntObj.objVarId = defaultClassId;
			classCntObj.totalCnt = selectRightCnt;
			makeJson2.push(classCntObj);
		</c:forEach>

		var tempCnt=0;											<%--전체시점 카운트--%>
		<%--주기별 시점 start,end 구하기--%>
		<c:forEach items="${rangeInfo.tabTimeList}" varStatus="status" var="tabTime">
			var tabIndex = "${status.index}";
			var prdSe = "${tabTime.prdSe}";
			var timeObj = {};

			var totCnt  = $("#selectTimeRight_"+tabIndex).find("option").size();
			if(totCnt > 0){
				var endIndex = totCnt - 1;
				timeObj.searchPrdSe    = prdSe;
				timeObj.startTime = $("#selectTimeRight_"+tabIndex+" option:eq("+endIndex+")").attr("value");
				timeObj.endTime   = $("#selectTimeRight_"+tabIndex+" option:eq(0)").attr("value");

				var timeListArr = new Array();
				<%--주기에 선택된 시점 세팅 메인화면에서 시점 조회후 체크 상태를 위함--%>
				$("#selectTimeRight_"+tabIndex).find("option").each(function(index){
					timeListArr.push($(this).attr("value"));
					tempCnt++;
				});
				timeObj.chkListArr = timeListArr;

				makeJson3.push(timeObj);

			}
		</c:forEach>


		$("#jsonStr").val(JSON.stringify(makeJson1));
		var classRangeArr = $("#jsonStr").val();
		var classCntArr = JSON.stringify(makeJson2);
		var timeRangeArr = JSON.stringify(makeJson3);
		var timeTotCnt = tempCnt;
		<%-- 분류별 전체 레벨 해제를 제외하고 실행--%>
		if(p_logicFlag == "Y"){
			parent.fn_searchRange(classRangeArr,itemRangeArr,classCntArr,timeRangeArr,timeTotCnt);
		}
	}

	<%--
	/************************************************************************
	함수명 : fn_mode()
	설   명 : 조회범위 상세설정
	인   자 : mode:추가,전체추가,해제,전체해제 selectBoxObj:selectBoxLeft_순번/Right_순번
	사용법 : 1.순번세팅과 lvl 세팅 주의
	작성일 : 2013-11-01
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2013-11-01      안영수              최초 생성
	************************************************************************/
	--%>
	<%--이상한 셀렉트 박스 가보자 jquery로 안되는게 없지롱 selectBoxObj-> value값에 1.id , 2.순번 세팅/ 항목에는 class값 X 분류의 class값은 lvl 꼼수발동..--%>
	function fn_mode(mode,selectBoxObj,type){
		var nStart = new Date().getTime();

		var tailCnt = selectBoxObj.split("_")[1];				<%--기준 selectBox	Left_순번/Right_순번--%>
		if(p_classAllSelectYn == "Y"){
			$("#selectRight_"+tailCnt).empty();
			parent.$("#p_classAllSelectYn").val("N");
		}
		var rightYn = $("#selectRight_"+tailCnt).find("option").size() ==0?"N":"Y";
		if(mode =="add"){
			$("#"+selectBoxObj).find("option").each(function(){
				if(this.selected){
					var value = this.value;
					var text =  this.text;
					var leftKey = this.value.split("@")[0];
					var leftSort = this.value.split("@")[1];
					var onlyOne = true;
					var level	 = $(this).attr("class");
					var leftSn =($(this).attr("value").split("@")[1]);

					if(rightYn=="N"){
						$("#selectRight_"+tailCnt).append("<option value='"+value+"' class='"+level+"'>"+text+"</option>");
					}else{
						<%--중복 데이터 move하면 안됨 부모페이지에서 선택된 분류정보 오른쪽selectBox에 view해달라고 할수도 있음.. 코드 중복제거 대기--%>
						var rightKeyArr = new Array();
						var rightSortArr =  new Array();

						$("#selectRight_"+tailCnt).find("option").each(function(index){
							var rightKey = $(this).attr("value").split("@")[0];
							var rightSort = $(this).attr("value").split("@")[1];
							rightKeyArr.push(rightKey);
							rightSortArr.push(rightSort);
						});

						if($.inArray(leftKey,rightKeyArr) < 0){
						<%--중복이 아닐경우만 실행--%>
							var matchIndex = 0;
							var totalCnt = 0;

							rightSortArr.push(leftSn);

					//		rightSortArr = $.unique(rightSortArr);

							rightSortArr.sort(function(a,b){return a-b;});

							$.each(rightSortArr,function(index,item){
								if(leftSn == item){
									matchIndex = index;
								}
								totalCnt++;
							});

// 							var option = new Option();
// 							option.value = value;
// 							option.class = level;
// 							option.appendChild(document.createTextNode(text));
							if(matchIndex == 0){
								$("#selectRight_"+tailCnt+" option:eq(0)").before("<option value='"+value+"' class='"+level+"'>"+text+"</option>");
							}else if(matchIndex == totalCnt){
								var equalIndex = totalCnt - 1;
								$("#selectRight_"+tailCnt+" option:eq("+equalIndex+")").after("<option value='"+value+"' class='"+level+"'>"+text+"</option>");

							}else{
								var equalIndex = matchIndex - 1;
								$("#selectRight_"+tailCnt+" option:eq("+equalIndex+")").after("<option value='"+value+"' class='"+level+"'>"+text+"</option>");
							}
						}
					}
				}
			});
		}else if(mode =="addAll"){
	//		$("#selectRight_"+tailCnt).empty();
			$("#"+selectBoxObj).find("option").each(function(){
				var value = this.value;
				var text =  this.text;
				var leftKey = this.value.split("@")[0];
				var leftSort = this.value.split("@")[1];
				var onlyOne = true;
				var level	 = $(this).attr("class");

				var rightKeyArr = new Array();
				var rightSortArr = new Array();

				if(rightYn=="N"){
					$("#selectRight_"+tailCnt).append("<option value='"+value+"' class='"+level+"'>"+text+"</option>");
				}
				else{
					var leftSn =($(this).attr("value").split("@")[1]);
			//		중복 데이터 move하면 안됨 부모페이지에서 선택된 분류정보 오른쪽selectBox에 view해달라고 할수도 있음.. 코드 중복제거 대기
					$("#selectRight_"+tailCnt).find("option").each(function(index){
						var rightKey = $(this).attr("value").split("@")[0];
						var rightSort = $(this).attr("value").split("@")[1];
						rightKeyArr.push(rightKey);
						rightSortArr.push(rightSort);
					});

					if($.inArray(leftKey,rightKeyArr) < 0){
			//		중복이 아닐경우만 실행
						var matchIndex = 0;
						var totalCnt = 0;

						rightSortArr.push(leftSn);

						rightSortArr.sort(function(a,b){return a-b;});

					//	$.unique(rightSortArr);

						$.each(rightSortArr,function(index,item){

							if(leftSort == item){
								matchIndex = index;
							}
							totalCnt++;

						});

						if(matchIndex == 0){
							$("#selectRight_"+tailCnt+" option:eq(0)").before("<option value='"+value+"' class='"+level+"'>"+text+"</option>");
						}else if(matchIndex == totalCnt){
							var equalIndex = totalCnt - 1;
							$("#selectRight_"+tailCnt+" option:eq("+equalIndex+")").after("<option value='"+value+"' class='"+level+"'>"+text+"</option>");
						}else{
							var equalIndex = matchIndex - 1;
							$("#selectRight_"+tailCnt+" option:eq("+equalIndex+")").after("<option value='"+value+"' class='"+level+"'>"+text+"</option>");

						}
					}
				}
			});
		}else if(mode =="back"){
			$("#"+selectBoxObj).find("option").each(function(){
 				if(this.selected){
 					$(this).remove();
 				}
			});

		}else if(mode =="backAll"){
			$("#selectRight_"+tailCnt).empty();
		}
		var nEnd   = new Date().getTime();

		var nDiff = nEnd - nStart;
//		alert("처리응답시간::::::::"+nDiff +"ms");

		fn_rangeCountText();
	}

	<%--
	/************************************************************************
	함수명 : fn_changeSet()
	설   명 :
	인   자 : boxSn:선택된 순서 statusClass.count
	사용법 :
	작성일 : 2013-11-01
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2013-11-01      안영수              최초 생성
	************************************************************************/
	--%>
	function fn_changeSet(boxSn){
		var mathMark = $("#mathMark_"+boxSn+" option:selected").attr("value");
		var lvlMark = $("#lvlMark_"+boxSn+" option:selected").val();
		var cloneArr = new Array();

		var viewArr	 = new Array();

		var cnt = boxSn -1;
		cloneArr = g_classCloneArr[cnt];


		<%--cloneArr에서 데이터 삭제--%>
 		$.each(cloneArr,function(index,item){
 			var lvlInfo ="";
 			lvlInfo= item.lvlInfo;
			var obj = {};
			if(mathMark == 1){
				if(lvlInfo <= lvlMark){
					obj.lvlInfo = item.lvlInfo;
					obj.text	= item.text;
					obj.value	= item.value;
					viewArr.push(obj);
				}
			}else if(mathMark == 2){
				if(lvlInfo == lvlMark){
					obj.lvlInfo = item.lvlInfo;
					obj.text	= item.text;
					obj.value	= item.value;
					viewArr.push(obj);
				}
			}else if(mathMark == 3){
				if(lvlInfo >= lvlMark){
					obj.lvlInfo = item.lvlInfo;
					obj.text	= item.text;
					obj.value	= item.value;
					viewArr.push(obj);
				}
			}
 		});

 		$("#selectLeft_"+boxSn).empty();

 		$.each(viewArr,function(index,item){
  			<%--nbsp세팅--%>
  			var nbsp="";
  			var loopCnt = item.lvlInfo;
 			for(var i=0; i<loopCnt ;i++){
 				if(loopCnt > 1){
 					nbsp+= "&nbsp&nbsp"; // 2015년 01월 15일 공백 추가
 				}
  			}
 			$("#selectLeft_"+boxSn).append("<option value='"+item.value+"' class='"+item.lvlInfo+"'>"+nbsp+item.text+"</option>");
 		});
	}


	<%--
	/************************************************************************
	함수명 : fn_word()
	설   명 : 배경색 초기화시 #FFFFFF로 바꾸면 속도 안나옴.style속성 제거
	인   자 :
	사용법 :
	작성일 : 2013-11-01
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2013-11-01      안영수              최초 생성
	************************************************************************/
	--%>
	function fn_word(){

		var compareText = $("#compValue").val();
		if(compareText ==""){
			alert("<pivot:msg code="alert.searchWord.msg"/>");
			return;
		}
		<%--
		$("#selectLeft_0 option:contains('"+compareText+"')").css("background-color","#53C14B");
		$("#selectLeft_0 option:contains('"+compareText+"')").prop("selected",true);
 		$("#selectLeft_0 option:contains('"+compareText+"')").removeAttr("selected");
		$("#selectLeft_0 option:contains('"+compareText+"')").prop("selected",false);
		--%>

		var itemForm = document.getElementById("selectLeft_0");
		var itemLength = itemForm.length;
		for(var k=0; k<itemLength; k++){
			itemVal = itemForm.options[k].text;
			if(itemVal.indexOf(compareText) >=0){
				itemForm.options[k].selected = true;
				itemForm.options[k].selected = false;
				itemForm.options[k].style.backgroundColor = "#53C14B";
			}else{
				itemForm.options[k].selected = false;
				itemForm.options[k].style.backgroundColor = "#FFFFFF";
			}
		}

		for(ii=1; ii<=g_classLength; ii++){
			var classForm = document.getElementById("selectLeft_"+ii);
			var classLength = classForm.length;
			<%--
			$("#selectLeft_"+ii+" option:contains('"+compareText+"')").css("background-color","#53C14B");
			$("#selectLeft_"+ii+" option:contains('"+compareText+"')").prop("selected",true);
 			$("#selectLeft_"+ii+" option:contains('"+compareText+"')").removeAttr("selected","selected");
			$("#selectLeft_"+ii+" option:contains('"+compareText+"')").val("");
			$("#selectLeft_"+ii+" option:contains('"+compareText+"')").prop("selected",false);
			--%>
			for(var j=0; j<classLength; j++){
				classVal = classForm.options[j].text;
				if(classVal.indexOf(compareText) >=0){
					classForm.options[j].selected = true;
					classForm.options[j].selected = false;
					classForm.options[j].style.backgroundColor = "#53C14B";
				}else{
					classForm.options[j].selected = false;
					classForm.options[j].style.backgroundColor = "#FFFFFF";
				}
			}
		}

	}

	function enterToSearch(evt){
		var theEvent = evt ? evt : window.event;
		if(theEvent.keyCode == '13'){
			fn_word();
		}
	}


	<%--
	/************************************************************************
	함수명 : fn_TimeMode()
	설   명 : mode:추가,전체추가,해제,전체해제 selectBoxObj:selectBoxLeft_순번/Right_순번
	인   자 :
	사용법 :
	작성일 : 2013-12-20
	작성자 : 국가통계포털  안영수

	date         author      note
	----------   -------     -------------------
	2013-12-20     안영수              최초 생성
	************************************************************************/
	--%>
	function fn_TimeMode(mode,selectBoxObj){
		<%--현재 tab위치가 어디인지 판별// tab세팅시 tab으로 forEach 로 버튼까지세팅하고 visible로 컨트롤 되지만...이건 아님 --%>
		var tabId;		<%--현재 tab_on상태인 메뉴Id 찾기--%>

		 $("#detailTab li").each(function(index){
			var tabStatus = $(this).attr("class");
			if(tabStatus == "tab_on"){
				tabId = $(this).attr("id");
			}
		});

		if(mode =="add"){
			<%--시점 오른쪽 갯수 확인--%>
			var rightTimeYn = $("#selectTimeRight_"+g_defaultTabSn).find("option").size() == 0?"N":"Y";

			$("#"+selectBoxObj+g_defaultTabSn).find("option").each(function(){
				if(this.selected){
					var leftValue = this.value;
					var leftText  = this.text;

					if(rightTimeYn =="N"){
						var option = new Option();
						option.value = leftValue;
						option.appendChild(document.createTextNode(leftText));
						$("#selectTimeRight_"+g_defaultTabSn).append(option);
					}else{
						<%--시점 오른쪽--%>
						var rTimeSortArr = new Array();
						$("#selectTimeRight_"+g_defaultTabSn).find("option").each(function(index){
							rTimeSortArr.push($(this).attr("value"));

						});

						<%--중복이 아닐 경우만 실행--%>
						if($.inArray(leftValue,rTimeSortArr) < 0){
							var matchIndex = 0;
							var totalCnt = 0;
							rTimeSortArr.push(leftValue);


//							$.unique(rTimeSortArr);
							rTimeSortArr.sort(function(a,b){return b-a;});
// 							rTimeSortArr.reverse();
							$.each(rTimeSortArr,function(index,item){
								if(leftValue == item){
									matchIndex = index;
								}
								totalCnt++;
							});

							var option = new Option();
							option.value = leftValue;
							option.appendChild(document.createTextNode(leftText));

							if(matchIndex == 0){
								// option eq 0의 앞에
								//$("#selectTimeRight_"+g_defaultTabSn+" option:eq(0)").before("<option value='"+leftValue+"'>"+leftText+"</option>");
								$("#selectTimeRight_"+g_defaultTabSn+" option:eq(0)").before(option);
							}else if(matchIndex == totalCnt){
								//totalCnt -1 의 다음번째에
								var equalIndex = totalCnt - 1;
								//$("#selectTimeRight_"+g_defaultTabSn+" option:eq("+equalIndex+")").after("<option value='"+leftValue+"'>"+leftText+"</option>");
								$("#selectTimeRight_"+g_defaultTabSn+" option:eq("+equalIndex+")").after(option);
							}else{
								//matchIndex -1 다음번째에
								var equalIndex = matchIndex - 1;
								//$("#selectTimeRight_"+g_defaultTabSn+" option:eq("+equalIndex+")").after("<option value='"+leftValue+"'>"+leftText+"</option>");
								$("#selectTimeRight_"+g_defaultTabSn+" option:eq("+equalIndex+")").after(option);

							}
						}
					}
				}
			});


		}else if(mode =="addAll"){
			$("#selectTimeRight_"+g_defaultTabSn).empty();
			$("#"+selectBoxObj+g_defaultTabSn).find("option").each(function(){
				$("#selectTimeRight_"+g_defaultTabSn).append("<option value='"+this.value+"'>"+this.text+"</option>");
			});

		}else if(mode =="back"){
			$("#"+selectBoxObj+g_defaultTabSn).find("option").each(function(){
				if(this.selected){
					$(this).remove();
				}
			});

		}else if(mode =="backAll"){
			$("#selectTimeRight_"+g_defaultTabSn).empty();
		}

		fn_rangeCountText();
	}
	
	
	
	<%--
	/************************************************************************
	함수명 : fn_SameTimeSelect()
	설   명 : 월,격월,분기에 대해서 특정 월이나 분기를 일괄 설정
	작성일 : 2020-08-13

	date         author      note
	----------   -------     -------------------
	2020-08-13     김경호              최초 생성
	************************************************************************/
	--%>
	function fn_SameTimeSelect(selectBoxObj){
		
		var buff= $("#samePrdse_"+g_defaultTabSn).val();
		var sliceValue = "";
		
		if(buff.length == 1){
			buff = "0"+buff;
		}
		
		$("#selectTimeRight_"+g_defaultTabSn).empty(); // 오른쪽 초기화하라고 하네요...김기만 사무관님이
		
		$("#"+selectBoxObj+g_defaultTabSn).find("option").each(function(){
			var leftValue = this.value;
			
			if(leftValue.length >= 2){
				sliceValue = leftValue.substr(leftValue.length - 2, 2);
			}
			
			if(buff == sliceValue){
				this.selected = true;
			}else{
				this.selected = false;
			}
		});
		
		fn_TimeMode('add','selectTimeLeft_','class');
	}
//]]>
</script>

<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="dataOpt"/>
	<input type="hidden" id="jsonStr" name="jsonStr" value=""/>
</form:form>

	<div id="ifr_pop_selectAll2" style="width:723px">
		<div class="ifr_scr">
			<div class="pop_content">
				<div class="navi">
					<div style="float:left;">
						<span class="h2Title"><a href="#itm"><pivot:msg code="ui.label.item"/></a></span>
						<c:forEach items="${rangeInfo.classInfoList}" var="classInfo" varStatus="statusClass">
						<span class="h2Title"><a href="#${classInfo.classId}">${classInfo.classNm}</a></span>
						</c:forEach>
						<span class="h2Title"><a href="#time"><pivot:msg code="ui.label.time"/></a></span>
					</div>
					<div class="btn_lay3"><span class="confirmBtn"><a href="javascript:fn_definite();"><pivot:msg code="ui.btn.accept"/></a></span></div>
				</div>

<!-- 			<div class="popSearch"> -->
<!-- 				검색할 단어 : <input type="text" class="text" /> <a href="#"><img src="images/btn_search.png" alt="검색" /></a> -->
<!-- 			</div> -->

				<div id="textShow" class="popSearch">
					<ul>
						<li class="text"><pivot:msg code="ui.label.selectDataItem"/></li><li id="changeTextLi" class="text"/><li class="text"> &nbsp;<pivot:msg code="ui.label.cell"/></li>
						<li class="compare"><input type="text" id="compValue" name="compValue" size="10" value="<pivot:msg code="ui.label.searchWord"/>" style="color:#D5D5D5" onkeypress="enterToSearch(event)"/><label for="compValue"></label>
						<a style="cursor:pointer;"><img src="images/btn_search.png" alt="<pivot:msg code="ui.label.btnSearchWord"/>" title="<pivot:msg code="ui.label.btnSearchWord"/>" onclick="javascript:fn_word();" /></a></li>
					</ul>
				</div>

				<div class="detailDiv">
					<!--  왼쪽영역 -->
					<div class="detailSelect" style="width:373px;">
					<!-- 항목맨처음 세팅 -->
						<div class="detailPart">
							<h2 class="h2Title"><a name="itm"/></a><div><strong><pivot:msg code="ui.label.pivotItem"/></strong></div></h2>
							<ul class="selectItem">
								<h3 class="h3Title"><pivot:msg code="ui.label.allItem"/></h3>
								<select id="selectLeft_0" style="width:300px; height:100px;" multiple="multiple" name="selectLeft_0" >
								<c:forEach items="${rangeInfo.itemInfo.itmList}" var="itemInfo" varStatus="status">
									<option value="${itemInfo.itmId}@${itemInfo.charItmSn}">${itemInfo.scrKor}</option>
								</c:forEach>
								</select>
							</ul>
							<ul class="btnAlign">
								<li>
									<img src="images/rangeDetail/fw1.gif" alt="<pivot:msg code="ui.label.addAll"/>" title="<pivot:msg code="ui.label.addAll"/>" onclick="fn_mode('addAll','selectLeft_0','item');"/>
									<img src="images/rangeDetail/fw.gif" alt="<pivot:msg code="ui.label.add"/>" title="<pivot:msg code="ui.label.add"/>" onclick="fn_mode('add','selectLeft_0','item');"/>
								</li>
								<li style="margin-top:12px;">
									<img src="images/rangeDetail/back.gif" alt="<pivot:msg code="ui.label.clear"/>" title="<pivot:msg code="ui.label.clear"/>" onclick="fn_mode('back','selectRight_0','item');"/>
									<img src="images/rangeDetail/back1.gif" alt="<pivot:msg code="ui.label.clearAll"/>" title="<pivot:msg code="ui.label.clearAll"/>" onclick="fn_mode('backAll','selectright_0','item');"/>
								</li>
							</ul>
						</div>
						<!-- 분류세팅시작 -->
						<c:forEach items="${rangeInfo.classInfoList}" var="classInfo" varStatus="statusClass">
						<div class="detailPart" <c:choose><c:when test="${classInfo.visible == true}">style="display:block;"</c:when>
														  <c:otherwise>style="display:none;"</c:otherwise></c:choose>>
							<h2 class="h2Title"><a name="${classInfo.classId}"></a><div style="float:left;width:180px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" title="${classInfo.classNm}"><strong>${classInfo.classNm}</strong></div>
								<div style="float:left;">
									<select id="mathMark_${statusClass.count}" onchange="fn_changeSet('${statusClass.count}');">
										<option value="1">&lt;=</option>
										<option value="2">=</option>
										<option value="3">&gt;=</option>
									</select>
									<select id="lvlMark_${statusClass.count}" onchange="fn_changeSet('${statusClass.count}');">
									<c:forEach var="i" begin="1" end="${classInfo.depthLvl}" step="1">
										<option value='${i}' <c:if test="${classInfo.depthLvl eq i}">selected="selected"</c:if>>${i}</option>
									</c:forEach>
									</select> / ${classInfo.depthLvl}
								</div>
							</h2>
							<ul class="selectItem">
								<h3 class="h3Title"><pivot:msg code="ui.label.allItem"/></h3>
								<select id="selectLeft_${statusClass.count}" style="width:300px; height:150px;" multiple="multiple" name="${classInfo.classId}">
								<c:forEach items="${classInfo.classAllList}" var="classList" varStatus="status">
									<option value="${classList.itmId}@${classList.rownum}" class="${classList.lvl}">
								<c:forEach var="i" begin="1" end="${classList.lvl}" step="1" varStatus="status"><c:if test="${classList.lvl ne 1}">&nbsp;&nbsp;</c:if></c:forEach>${classList.scrKor}</option>
								</c:forEach>
								</select>
							</ul>
							<ul class="btnAlign">
								<li>
									<img src="images/rangeDetail/fw1.gif" alt="<pivot:msg code="ui.label.addAll"/>" title="<pivot:msg code="ui.label.addAll"/>" onclick="fn_mode('addAll','selectLeft_${statusClass.count}','class');"/>
									<img src="images/rangeDetail/fw.gif" alt="<pivot:msg code="ui.label.add"/>" title="<pivot:msg code="ui.label.add"/>" onclick="fn_mode('add','selectLeft_${statusClass.count}','class');"/>
								</li>
								<li style="margin-top:62px;">
									<img src="images/rangeDetail/back.gif" alt="<pivot:msg code="ui.label.clear"/>" title="<pivot:msg code="ui.label.clear"/>" onclick="fn_mode('back','selectRight_${statusClass.count}');"/>
									<img src="images/rangeDetail/back1.gif" alt="<pivot:msg code="ui.label.clearAll"/>" title="<pivot:msg code="ui.label.clearAll"/>" onclick="fn_mode('backAll','selectright_${statusClass.count}');"/>
								</li>
							</ul>
						</div>
						</c:forEach>
	 				</div>

	 				<!-- 오른쪽영역 -->
	 				<!-- 항목맨처음 세팅 -->
	 				<div class="detailSelect2">
	 				<div class="detailSelect" style="width:300px;">
	 					<div class="detailPart">
	 						<h2 class="h2Title_none"></h2>
	 						<ul class="selectItem">
								<h3 class="h3Title"><pivot:msg code="ui.label.selectedItem"/><span class="point"><pivot:msg code="ui.label.overOneSelect"/></span></h3>
								<select id="selectRight_0" style="width: 300px; height: 100px;" multiple="multiple" name="">
								<c:forEach items="${rangeInfo.selectItmList}" var="selectItmList">
									<option value="${selectItmList.itmId}@${selectItmList.charItmSn}">${selectItmList.scrKor}</option>
								</c:forEach>
								</select>
							</ul>
	 					</div>
	 				</div>

	 				<c:forEach items="${rangeInfo.selectClassList}" var="selectClassInfo" varStatus="status">
	 				<!-- 분류세팅시작 -->
	 				<div class="detailSelect" style="width:300px;<c:choose><c:when test="${selectClassInfo.visible == true}">display:block;</c:when>
																		   <c:otherwise>display:none;</c:otherwise></c:choose> ">
	 					<div class="detailPart">
	 						<h2 class="h2Title_none"></h2>
	 						<ul class="selectItem">
								<h3 class="h3Title"><pivot:msg code="ui.label.selectedItem"/><span class="point"><pivot:msg code="ui.label.overOneSelect"/></span></h3>
								<select id="selectRight_${status.count}" style="width: 300px; height: 150px;" multiple="multiple" name="${selectClassInfo.classId}">
								<c:forEach items="${selectClassInfo.classAllList}" var="selectClassList" varStatus="status">
									<option value="${selectClassList.itmId}@${selectClassList.rownum}" class="${selectClassList.lvl}">
								<c:forEach var="i" begin="1" end="${selectClassList.lvl}" step="1" varStatus="status"><c:if test="${selectClassList.lvl ne 1}">&nbsp;&nbsp;</c:if></c:forEach>${selectClassList.scrKor}</option>
								</c:forEach>
								</select>
							</ul>
	 					</div>
	 				</div>
 					</c:forEach>
 					</div>
					<!-- 조회범위 컨텐츠 가로 -->
					<div class="detailSelect" style="width:100%;">
						<div class="detailPart">
							<h2 class="h2Title"><a name="time"/></a><strong><pivot:msg code="ui.label.time"/></strong></h2>
							<div class="detailTabDiv">
							<ul class="detailTab" id="detailTab">
								<c:forEach items="${rangeInfo.tabTimeList}" varStatus="status" var="tabTime">
									<c:choose>
										<c:when test="${status.index eq 0}">
											<li class="tab_on" id="${tabTime.prdSe}">${tabTime.prdSeNm}</li>				<!-- 첫번째 탭 tab_on -->
										</c:when>
										<c:otherwise>
											<li class="tab_off" id="${tabTime.prdSe}">${tabTime.prdSeNm}</li>			<!-- 영문화 한글 모드 세팅 대기 prdNm으로 변경 -->
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</ul>
							</div>
							<div class="detailListlay">
								<c:forEach items="${rangeInfo.tabTimeList}" varStatus="status" var="tabTime">
									<ul id="timeLeft_${status.index}" class="selectItem_left"
										<c:if test="${status.index eq 0}">style="display: block";</c:if>
										<c:if test="${status.index ne 0}">style="display: none";</c:if>
										>
									<c:choose>

										<c:when test="${tabTime.prdSe == 'D'}">				<!-- 일 -->
											<p class="h3Title" style="height:22px;"><c:out value="${rangeInfo.periodInfo.startD} ~ ${rangeInfo.periodInfo.endD}"/></p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listD}" var="timeD" varStatus="status">
<%-- 											<option value="${selectClassList.itmId}@${selectClassList.rownum}" class="${selectClassList.lvl}"> --%>
<%-- 											${selectClassList.scrKor}</option> --%>
												<option value="${timeD.prdDe}">${timeD.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:when test="${tabTime.prdSe == 'T'}">				<!-- 순기 -->
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startD} ~ ${rangeInfo.periodInfo.endD}</p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listT}" var="timeT" varStatus="status">
												<option>${timeT.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:when test="${tabTime.prdSe == 'M'}">				<!-- 월 -->
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startM} ~ ${rangeInfo.periodInfo.endM}
												<span style="float:right;">
													<select id="samePrdse_${status.index}" style="height:20px;" onchange="fn_SameTimeSelect('selectTimeLeft_');">
														<option value="">---</option>
													<c:forEach var="i" begin="1" end="12" step="1">
														<option value="${i}">${i}</option>
													</c:forEach>
													</select>
												</span>
											</p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listM}" var="timeM" varStatus="status">
												<option value="${timeM.prdDe}">${timeM.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:when test="${tabTime.prdSe == 'B'}">				<!-- 격월 -->
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startB} ~ ${rangeInfo.periodInfo.endB}</p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listB}" var="timeB" varStatus="status">
												<option value="${timeB.prdDe}">${timeB.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:when test="${tabTime.prdSe == 'Q'}">				<!-- 분기 -->
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startQ} ~ ${rangeInfo.periodInfo.endQ}
												<span style="float:right;">
													<select id="samePrdse_${status.index}" style="height:20px;" onchange="fn_SameTimeSelect('selectTimeLeft_');">
														<option value="">---</option>
													<c:forEach var="i" begin="1" end="4" step="1">
														<option value="${i}">${i}</option>
													</c:forEach>
													</select>
												</span>
											</p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listQ}" var="timeQ" varStatus="status">
												<option value="${timeQ.prdDe}">${timeQ.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:when test="${tabTime.prdSe == 'H'}">				<!-- 반기 -->
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startH} ~ ${rangeInfo.periodInfo.endH}</p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listH}" var="timeH" varStatus="status">
												<option value="${timeH.prdDe}">${timeH.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:when test="${tabTime.prdSe == 'Y'}">				<!-- 년 -->
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startY} ~ ${rangeInfo.periodInfo.endY}</p>
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listY}" var="timeY" varStatus="status">
												<option value="${timeY.prdDe}">${timeY.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>


										<c:otherwise>
											<p class="h3Title" style="height:22px;">${rangeInfo.periodInfo.startF} ~ ${rangeInfo.periodInfo.endF}</p>					<!-- 부정기 -->
											<select id="selectTimeLeft_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="${tabTime.prdSe}">
											<c:forEach items="${rangeInfo.periodInfo.listF}" var="timeF" varStatus="status">
												<option value="${timeF.prdDe}">${timeF.prdTime}</option>
											</c:forEach>
											</select>
										</c:otherwise>

									</c:choose>
									</ul>

								</c:forEach>


								<ul class="btnAlign_ab" style="top:20px;">
									<li>
										<img src="images/rangeDetail/fw1.gif" alt="<pivot:msg code="ui.label.addAll"/>" title="<pivot:msg code="ui.label.addAll"/>" onclick="fn_TimeMode('addAll','selectTimeLeft_','class');"/>
										<img src="images/rangeDetail/fw.gif" alt="<pivot:msg code="ui.label.add"/>" title="<pivot:msg code="ui.label.add"/>" onclick="fn_TimeMode('add','selectTimeLeft_','class');"/>
									</li>
									<li style="margin-top:12px;">
										<img src="images/rangeDetail/back.gif" alt="<pivot:msg code="ui.label.clear"/>" title="<pivot:msg code="ui.label.clear"/>" onclick="fn_TimeMode('back','selectTimeRight_');"/>
										<img src="images/rangeDetail/back1.gif" alt="<pivot:msg code="ui.label.clearAll"/>" title="<pivot:msg code="ui.label.clearAll"/>" onclick="fn_TimeMode('backAll','selectTimeRight_');"/>
									</li>
								</ul>

								<c:forEach items="${rangeInfo.tabTimeList}" varStatus="status" var="tabTime">
									<ul id="timeRight_${status.index}" class="selectItem_right"
										<c:if test="${status.index eq 0}">style="display: block"</c:if>
										<c:if test="${status.index ne 0}">style="display: none"</c:if>
										>
										<h3 class="h3Title_none" style="height:22px;"></h3>
									<c:choose>
										<c:when test="${tabTime.prdSe == 'D'}">				<!-- 일 -->
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListD}" var="timeD">
												<option value="${timeD.prdDe}">${timeD.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:when test="${tabTime.prdSe == 'T'}">
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListT}" var="timeT">
												<option value="${timeT.prdDe}">${timeT.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:when test="${tabTime.prdSe == 'M'}">
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListM}" var="timeM">
												<option value="${timeM.prdDe}">${timeM.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:when test="${tabTime.prdSe == 'B'}">
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListB}" var="timeB">
												<option value="${timeB.prdDe}">${timeB.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:when test="${tabTime.prdSe == 'Q'}">
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListQ}" var="timeQ">
												<option value="${timeQ.prdDe}">${timeQ.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:when test="${tabTime.prdSe == 'H'}">
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListH}" var="timeH">
												<option value="${timeH.prdDe}">${timeH.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:when test="${tabTime.prdSe == 'Y'}">
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListY}" var="timeY">
												<option value="${timeY.prdDe}">${timeY.prdTime}</option>
											</c:forEach>
											</select>
										</c:when>

										<c:otherwise>
											<select id="selectTimeRight_${status.index}" style="width: 300px; height: 100px;" multiple="multiple" name="555">
											<c:forEach items="${rangeInfo.periodInfo.defaultListF}" var="timeF">
												<option value="${timeF.prdDe}">${timeF.prdTime}</option>
											</c:forEach>
											</select>
										</c:otherwise>
									</c:choose>

									</ul>
								</c:forEach>



							</div>
						</div>
					</div>
					<!-- //조회범위 컨텐츠 가로 -->

			</div>
			<div class="popMsg">
				<ul>
					<li class="text"><pivot:msg code="ui.label.controlMsg"/></li>
				</ul>
			</div>
		</div>
 	</div>
 	</div>

</body>
</html>