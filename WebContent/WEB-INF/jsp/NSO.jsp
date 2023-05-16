<%--
 /**
  * @Filename : NSO.jsp
  * @Description : 통계표 조회
  * @Modification Information
  * @
  * @  수정일         수정자                   수정내용
  * @ -------    --------    ---------------------------
  * @ 2013-07-19    안영수          최초 생성
 */
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@include file="/WEB-INF/jsp/common/taglibs.jsp"%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<title>KOSIS</title>
<script type="text/javascript" src="ext/js/common/json2.js"></script>
<script type="text/javascript" src="ext/js/jquery/jquery-1.9.1.js"></script>

<!-- <script type="text/javascript" src="fusioncharts_3.12/fusioncharts.js"></script> -->
<!-- <script type="text/javascript" src="fusioncharts/fusioncharts.js"></script> -->
<script type="text/javascript" src="https://cdn.fusioncharts.com/fusioncharts/latest/fusioncharts.js"></script>
<!-- <script type="text/javascript" src="https://cdn.fusioncharts.com/fusioncharts/3.15.3/fusioncharts.js"></script> -->

<script type="text/javascript" src="ext/js/jqgrid/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="ext/js/jqgrid/jquery.jqGrid.src.js"></script>
<script type="text/javascript" src="ext/js/jqgrid/grid.locale-en.js"></script>
<link type="text/css" rel="stylesheet" href="ext/css/jqgrid/ui.jqgrid.css" />
<link type="text/css" rel="stylesheet" href="ext/css/jqgrid/chart.custom.css" />

<c:choose>
	<c:when test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
		<link type="text/css" rel="stylesheet" href="ext/css/common/base_eng.css?v=200917" />
		<link type="text/css" rel="stylesheet" href="ext/css/common/popup_eng.css?v=180607" />
	</c:when>
	<c:otherwise>
		<link type="text/css" rel="stylesheet" href="ext/css/common/base.css?v=200917" />
		<link type="text/css" rel="stylesheet" href="ext/css/common/popup.css?v=180607" />
	</c:otherwise>
</c:choose>
</head>
<body id="statHtmlBody">
	<script type="text/javascript">
//<![CDATA[
		var g_maxCell = 20000;											<%--조회 가능한 셀 수--%>
		var g_maxCellDefault = 10000;									<%--2014.09 조회 가능한 셀 수 초기값--%>
		var g_maxCellDownload = 200000;									<%--다운로드 가능한 셀수--%>

		var g_mixCnt = "${statInfo.mixCnt}";
		var g_classTabCnt = "${fn:length(statInfo.classInfoList)}";		<%--분류갯수만--%>
		var tabMenuCnt = "${fn:length(statInfo.tabMenuList)}";
		var g_dimCo = "${statInfo.dimCo}";
		var g_charItmCo	= "${statInfo.itemInfo.itmCnt}";				<%--항목갯수 1개일때 탭메뉴 클릭조건사용--%>
		var g_tabItemCnt = "${statInfo.itemInfo.itmCnt}";
		var g_tabItemNm = "${statInfo.itemInfo.itmNm}";
		var g_tabTimeNm = "<pivot:msg code="ui.label.cond.time"/>" 		<%--"${statInfo.periodInfo.periodNm}";--%>
		var g_hideId ="";
		var g_TabGubun ="";												<%--클릭한 li tabGubun 임시저장--%>
		var g_flag = true;
		var g_searchGridFlag = false;
		var g_classChoiceArr	= new Array;							<%--사용자가 선택한 분류레벨정보--%>
		var g_tabClassCnt		= new Array;							<%--탭메뉴에 갯수 저장을 위한 arry--%>
		var g_periodStr = "${statInfo.periodStr}";						<%--시점 세팅--%>
		var g_result = g_periodStr.split("#");
		var g_textField="";
		var g_defaultClassArr = new Array;
		var g_maxLvl=0;													<%--텝메뉴의 레벨중 가장 큰 레벨--%>
		var g_htmlGrid="";												<%--통계표 hpopupCmmtYtml--%>
		var g_ThtmlGrid="";												<%--통계표 헤더부분만 따로 가져온것--%>
		var g_cmmt="";
		var g_chart;
		var popupCmmtY;
		var popupX;
		var g_colDimRowCnt;												<%--정렬에 해당하는 컬럼을 담기위한 변수 1--%>
		var g_cellIdx;													<%--정렬에 해당하는 컬럼을 담기위한 변수 2--%>

		var g_timeCnt=0;												<%--시점 선택갯수--%>
		var g_itmCnt=0;													<%--항목 선택갯수--%>
		var g_classCnt=0;												<%--분류 선택갯수--%>
		var g_itemMultiply = 0;											<%--분류*항목--%>
		var g_multiplication = 0; 										<%--조합수--%>
		var g_classEachCnt = new Array();	 							<%--분류의 개별 카운트 저장 배열(소스변경 안되면 삭제)--%>
		var g_classViewCntStr = "";										<%--분류 개별 카운트 스트링 문자열--%>
		var g_mixItemCnt = 0;			    							<%--통계표 조회& 파일다운로드 버튼--%>
		var g_mixDimCnt = 0;
		var g_browser;
		var g_browserVersion;
		var g_rowStr ="";												<%--표측 str	pivot정보에서 변경된 ul.li 문자열--%>
		var g_colStr ="";												<%--표두 str	pivot정보에서 변경된 ul.li 문자열--%>
		var g_listBox1ACnt = 0;  										<%--pivot 변경된 후  표측 갯수(after)--%>
		var g_listBox2ACnt = 0;											<%--pivot 변경된 후  표측 갯수(after)--%>
		var g_firstColAxis;												<%--부가기능 설정에서 초기 조회를 위한 초기 표두정보--%>
		var g_firstRowAxis;												<%--부가기능 설정에서 초기 조회를 위한 초기 표측정보--%>
		var g_defaultPeriodArr	= new Array; 							<%--부가기능 설정에서 월/분기표 세팅을 위함..addfunc.jsp의 tableType의 년/월분기표 select option 동적으로 세팅--%>
		var g_tableTypeOption = new Array;
		var g_headType="";												<%--분석,년월분기표 실행을 위한 기간탭의 선택된 한개의 주기정보--%>
		var g_tableYMQ ="N";
		var g_tableType="";
		var g_chartActive="N";											<%--챠트활성화여부--%>
		var g_chartLableArr = new Array();
		var g_chartDataArr  = new Array();
		var g_chartMsg;
		var g_remarkH;													<%-- 챠트 범례헤더--%>
		var g_remarkB;													<%-- 챠트 범례바디--%>
		var g_chartColor = new Array(59);								<%-- 챠트색 --%>
		var g_remarkSelect="";											<%-- 챠트 클릭시 범례순서--%>
		var g_assayYn = "Y";											<%--분석여부--%>
		var g_directYn = "${statInfo.directYn}";
		var g_unitNm = "${statInfo.unitNm}";
		var g_CHG_RATE_unit = "%";

		var g_xmlStr="";												<%--챠트 xml형식의 문자열--%>

		var g_ChartGubun="";											<%--챠트 구분--%>
		var g_deliveryClassArr = new Array();							<%--selectAll.jsp에 필요한 탭메뉴의 분류정보--%>
		var g_deliveryClassSet = new Array();							<%--searchRangeDetai.jsp에 필요한 탭메뉴의 분류정보--%>
		var g_analyzable = "${statInfo.analyzable}"; 					<%--분석 여부 true/false--%>
		var g_tableNm="";
		var g_dataOpt = "${ParamInfo.dataOpt}";
		var g_mode = "${ParamInfo.mode}";								<%--새창모드구분--%>
		var g_windowHeight;
		var g_windowWidth;
		var g_pivotColChk = new Array();								<%--item,time,class의 objVarId와 count 정보--%>
		var g_tblId = "${statInfo.tblId}";
		var g_rangeTimeArr = new Array();
		var g_rangeTimeCnt =0;
		var g_otherChartWidth = 1025; <%-- 2017.11.29 KOSIS 리뉴얼에 따른 사이즈 변경, 최신 크롬에서 부모박스(1040px)와 width를 같게 하면 부모박스를 벗어나는 문제가 있어서 약간 작게 --%>

		if("${ParamInfo.language}" == "en"){
			g_otherChartWidth = 950;
		}

		var olapStl = "${fn:substring(statInfo.olapStl,0,1)}";			<%--olapStl 맨앞 1자리 C일경우 차트먼저 보여줌 - 2014.04.17 김경호 --%>
		var limitYn = "${fn:substring(statInfo.olapStl,13,14)}";		<%--olapStl 맨뒤자리 1일경우 각 만셀 제한 해제 - 2015.03.03 오종민 --%>

		var g_assaySelect = "${ParamInfo.noSelect}";

		var g_parentIconArr = new Array();								<%--parentIcon 변경 정보--%>
		var g_classLvlArr = new Array();								<%--분류별 레벨 카운트 갯수--%>
		var g_maxChkFlag = "Y";
		var form;

		var chartInstance = new FusionCharts();

		FusionCharts.options.license({
		   key: 'EE-13E5snlA22B9A8D4D2B2F2A4I4D2A1B4d1sB-11A1C4I-8zpnD17B3F6rfwD3B1D8A3B2A1A1F4F1F1A10B1A5B3D1F3fyF-7A4B8E2B11E2E3G1nmdC8B2E6bfuI4B3C8fD-13zD3D2E3E4I1C11A1B6C2A1E2A7uwB3B7FB1ycrA33A18B14crC6UA4H4nhyA7A3A3A5E5A4I4B1B9A9A3A5E4G2a==',
		});

		var gColNm = "";									<%--분류명--%>
		var g_gubun = "";									<%-- 통계표차트 구분--%>

		if(g_dataOpt =='ko'){
			document.title="${statInfo.tblNm}";
		}else if(g_dataOpt =='en'){
			document.title="${statInfo.tblEngNm}";
		}else{}


$(document).ready(function(){
		$("#pop_detailfunc").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_detailfunc").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_detailfunc").offset({"top":"0"});	}
			}
		});
		<%-- 피봇 --%>
		$("#pop_pivotfunc").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_pivotfunc").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_pivotfunc").offset({"top":"0"});	}
			}
		});
		<%--부가기능--%>
		$("#pop_addfunc").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_addfunc").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_addfunc").offset({"top":"0"});	}
			}
		});
		<%--분석 --%>
		$("#pop_assay").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_assay").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_assay").offset({"top":"0"});	}
			}
		});
		<%--관련통계표--%>
		$("#pop_relGrid").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_relGrid").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_relGrid").offset({"top":"0"});	}
			}
		});
		<%--다운로드--%>
		$("#pop_downgrid").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_downgrid").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_downgrid").offset({"top":"0"});	}
			}
		});
		$("#pop_downlarge").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true});
		<%--URL보기--%>
		$("#pop_url").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_url").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_url").offset({"top":"0"});	}
			}
		});
		<%--KOSIS 서비스 리스트에서 통계표 경로보기--%>
		$("#pop_listLoc").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_nmlist").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_nmlist").offset({"top":"0"});	}
			}
		});
		<%--주석보기 --%>
		$("#pop_cmmtInfoAll").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			handle : '.pop_top',
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_cmmtInfoAll").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_cmmtInfoAll").offset({"top":"0"});	}
			}
		});

		<%--인쇄--%>
		$("#pop_print").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_print").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_print").offset({"top":"0"});	}
			}
		});
		<%--차트--%>
		$("#pop_chart").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_chart").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_chart").offset({"top":"0"});	}
			}
		});
		// 출처 더보기
		$("#pop_statGrid").draggable({containment: "#wrap",scroll:false,cursor:"move",iframeFix:true,
			stop: function(){
				var offset = $(this).offset();
				var xPos = offset.left;
				var yPos = offset.top;
				if( xPos < 0 ){ $("#pop_statGrid").offset({"left":"0"});	}
				if( yPos < 0 ){ $("#pop_statGrid").offset({"top":"0"});	}
			}
		});

		if(g_directYn == "Y"){
			$("#directMenu").css("display", "block");
			$(".tabMu").css("display","none");
			$(".cont_line").css("display","none");
			$(".cont_lay").css("display","none");
		}
		<%-- 새창보기인 경우--%>
		if(g_mode != "tab"){
			var windowWidth = $(window).width();
			var bodyWidth = windowWidth - 10;
			var windowHeight = $(window).height();
			var bodyHeight = windowHeight -191;

			var bodyMinWidth = $("#statHtmlBody").css("min-width");
			bodyMinWidth =  Number(bodyMinWidth.replace('px', ''));

			<%--2014.03.02 기본mode=tab이고 화면 전체사이즈를 줄인상태에서 새창보기 클릭시 화면깨짐 수정--%>
			if(windowWidth > bodyMinWidth){
				$("#wrap").css("width",bodyWidth-10);
				$("#wrap").css("height","100%");
				$("#modal").css("width",bodyWidth-10);
				$("#modal2").css("width",bodyWidth-10);
				$("#popup_outer").css("width",bodyWidth-10);
				$("#popup_outer").css("height","800px");

				$(".cont_lay").css("width",bodyWidth-10);
				$(".cont_line").css("width",bodyWidth-11);
				$("#textShow").css("width",bodyWidth-30);
				$("#Divchart").css("width",bodyWidth-12);
				$("#jqGrid").css("width",bodyWidth-12);
				$("#Legned").css("width",bodyWidth-12);
				$("#popMode").css("width",bodyWidth-12);
				$("#kosis").css("left",bodyWidth-80);
			}
		}else{
			<%--kosis통계 / 북한통계인 경우 --%>
			var otherWidth = $("#statHtmlBody").parent().css("width");
			otherWidth = Number(otherWidth.replace('px', ''));

			$("#statHtmlBody").css("min-width",otherWidth-38);
			$("#wrap").css("width",otherWidth-38);
			$(".cont_lay").css("width",otherWidth-39);
			$(".cont_line").css("width",otherWidth-38);
			$("#popMode").css("width",otherWidth-38);
			g_otherChartWidth = otherWidth-38;
		}

		$(window).resize(function(){

			var bodyMinHeight;
			<%-- 챠트 조회시 범례높이에 따른  footer 높이 동적세팅--%>
			if(g_chartActive =="Y"){
				bodyMinHeight = "850px"; <%-- 2015.01.06 작은 모니터에서 풋터가 위로 올라가면서 다른 영역을 침범하는 현상 수정 - 김경호 --%>
			}else{
				bodyMinHeight = $("#statHtmlBody").css("min-height");				<%--최소사이즈는 줄일 필요없음--%>
			}
			g_windowHeight = $(window).height();
			bodyMinHeight = Number(bodyMinHeight.replace('px', ''));

			var bodyMinWidth = $("#statHtmlBody").css("min-width");

			g_windowWidth = $(window).width();
			bodyMinWidth =  Number(bodyMinWidth.replace('px', ''));
			var selectBoxHeight;
			if(g_mode != "tab"){

				if(bodyMinHeight > g_windowHeight){
					g_windowHeight = bodyMinHeight;
				}else{
					popModeHeight = g_windowHeight -172+85-110-38;					 <%--popMode 높이--%>

					selectBoxHeigth = g_windowHeight-300;
					selectListHeight = g_windowHeight-300-65;

					<%--분류별 selectBox높이 세팅해야함--%>
					$(".selectBox").each(function(){
						$(this).css("height",selectBoxHeigth);
						$(this).find('ul').css("height",selectListHeight);

					});

					<%--시점별 selectBox높이 세팅--%>
					$(".selectTimeBox").each(function(){
					$(this).css("height",selectBoxHeigth);
					$(this).find('ul').css("height",selectListHeight-36);
					});

					$("#popMode").css("height",popModeHeight);

					$("#kosis").css("left",g_windowWidth-85);
				}

				$("#modal").css("height",g_windowHeight - 48);
				$("#modal2").css("height",g_windowHeight - 48);
				$("#wrap").css("height",g_windowHeight - 48);
				$("#popup_outer").css("height",g_windowHeight -48);

				$(".cont_line").css("height",g_windowHeight -202);
				$(".cont_lay").css("height",g_windowHeight -202);
				$("#tailExplain").css("top",g_windowHeight-227);

				if(g_chartActive =="Y" && $("#Divchart").css("height") != "0px"){
					$("#popMode").css("height","0px");
					$("#htmlGrid").css("margin-top","0");
					$("#htmlGrid").css("height","0");
					$("#Divchart").css("height","356px");
				}
			}

			var bodyWidth;
			if(bodyMinWidth > g_windowWidth){
				bodyWidth = bodyMinWidth;
				$("#kosis").css("left","860");								<%--최소 넓이에 보다 작아질 경우 링크 left는 고정--%>
			}else{
				bodyWidth = g_windowWidth - 10;
			}

			$("#wrap").css("width",bodyWidth-10);
			$(".cont_lay").css("width",bodyWidth-10);
			$(".cont_line").css("width",bodyWidth-11);
			$("#textShow").css("width",bodyWidth-28);
			$("#popMode").css("width",bodyWidth-12);
			$("#Divchart").css("width",bodyWidth-12);
			$("#jqGrid").css("width",bodyWidth-12);
			$("#Legned").css("width",bodyWidth-12);
			$("#modal").css("width",bodyWidth-10);
			$("#modal2").css("width",bodyWidth-10);
			$("#popup_outer").css("width",bodyWidth-10);

			<%--
			   2016.02.16 통계표 상단 틀고정 시작
			   htmlGrid 위에 ThtmlGrid를 보여줘서 틀고정이 된것처럼 보이게 만듬 - 김경호
			     열리는 창의 사이즈가 바뀔때마다 틀고정되는 div도 크기조정
			--%>
			$("#ThtmlGrid").css("width", $("#popMode").width() - 17); <%-- 세로스크롤바(17px) 만큼 줄인다...아래 있는 htmlGrid의 세로 스크롤바를 보이게 하기위해... --%>
			<%-- 통계표 상단 틀고정 끝 --%>

			<%--2017.11.24 크로미움 기반 웹브라우져에서 스크롤이 있다 사라지면 수치영역 사라지는 버그로 인해 사이즈를 px단위로 픽스--%>
			if(g_chartActive !="Y"){
				$("#htmlGrid").css("width", $("#popMode").width()-2);
				$("#htmlGrid").css("height", $("#popMode").height()-2);
				$("#ThtmlGrid").css("width", $("#ThtmlGrid").width()-2);
			}

			<%--2017.12.13 창크기 변경시 스크롤바 틀어지는문제 수정 --%>
			$("#ThtmlGrid").scrollLeft($("#htmlGrid").scrollLeft());
		});


		if(g_dataOpt =='ko'){
			g_tableNm = "${statInfo.tblNm}";
		}else if(g_dataOpt =='cd'){
			g_tableNm = "${statInfo.tblId}";
		}else if(g_dataOpt =='cdko'){
			g_tableNm = "${statInfo.tblId} ${statInfo.tblNm}";
		}else if(g_dataOpt == 'en'){
			g_tableNm = "${statInfo.tblEngNm}";
		}else{
			g_tableNm = "${statInfo.tblId} ${statInfo.tblEngNm}";
		}

		if(g_tableNm == null || g_tableNm == ""){
			g_tableNm = "&nbsp;";
		}

		<%--년월 분기표를 조회하고 다른 부가기능을 실행시--%>
		if(g_tableType =="perYear"){
			g_tableYMQ ="Y";
		}
		$("#titleText").html("<font title='"+g_tableNm+"'>"+g_tableNm+"</font>");

		g_modal2Height = $("#modal2").css("height");					<%--새창보기 팝업모드일 경우 progressbar height 지정을 위함.--%>

		form = document.ParamInfo;

		var defaultRowList = new Array;
		<c:forEach var="pushItem" items="${statInfo.pivotInfo.rowList}">
			defaultRowList.push("${pushItem}");
		</c:forEach>


		var defaultColList = new Array;
		<c:forEach var="pushItem" items="${statInfo.pivotInfo.colList}">
			defaultColList.push("${pushItem}");
		</c:forEach>

		form.rowAxis.value = defaultRowList.join(",");
		//표두
		form.colAxis.value = defaultColList.join(",");


		g_firstColAxis		= $("#colAxis").attr("value");		<%--부가기능 설정에서 초기 조회를 위한 초기 표두정보--%>
	 	g_firstRowAxis		= $("#rowAxis").attr("value");		<%--부가기능 설정에서 초기 조회를 위한 초기 표측정보--%>

		var tempSubstr = "${statInfo.defaultPeriodStr}";

		<%--default시점이 한개 일 경우 D#,F#,Y#..이고 스크랩일 경우는 D#F#Y#M#... 시점구분+#으로 세팅되므로 한개일 경우, 배열로 변환하기 위해 마지막# substring--%>
		tempSubstr = tempSubstr.substring(0,tempSubstr.length-1);
		g_defaultPeriodArr = tempSubstr.split("#");

		$.each(g_defaultPeriodArr,function(index,item){
			if(item =="M"){
				g_tableTypeOption.push("M");
			}else if(item =="Q"){
				g_tableTypeOption.push("Q");

			}
		});

		<c:forEach items="${statInfo.classInfoList}" var="tab" varStatus="status">
			<c:set var="i" value="${statInfo.classInfoList[status.index].depthLvl}"/>
					var tempMaxLvl ="${i}";						<%--분류탭메뉴 정보중에 가장큰 레벨값 세팅--%>
					if(g_maxLvl <tempMaxLvl){
						g_maxLvl=tempMaxLvl;
					}
		</c:forEach>

		<%--통계표 관리자가 default로 세팅한 항목정보 세팅 항목은 1레벨값만 세팅--%>
		var defaultTempItem = new Array;
		<c:forEach var="pushItem" items="${statInfo.itemInfo.defaultItmList}" varStatus="status">
			defaultTempItem.push("${pushItem}");
		</c:forEach>

		var itemChkFlag=0;
		var defaultTempItemCnt = defaultTempItem.length;

		for(var i=0; i<defaultTempItemCnt; i++){
			$('[name=itemChkLi]').each(function(index){
				if(defaultTempItem[i] == $(this).val()){
					$(this).prop("checked",true);
					itemChkFlag++;
				}
			});
		}

		var itemChkCnt = $('[name=itemChkLi]').size();

		if(itemChkCnt == itemChkFlag){
			$("#itemChk").prop("checked",true);
		}

		<%--통계표 관리자가 default로 세팅한 분류정보 세팅--%>
		<%--분류별 레벨 카운트 추가..레벨전체선택 추가로 인한 동적 화면에서 카운트 증가 및 체크상태 유지--%>
		var defaultTempClass = new Array;
		var tempInsertClassCnt=1;
			<c:forEach items="${statInfo.classInfoList}" varStatus="status" var="tabClass">
				var defaultClassId = "${statInfo.classInfoList[status.index].classId}";
				var totalCnt	   = "${statInfo.classInfoList[status.index].itmCnt}";
				var classSn		   = "${status.count}";
				var classNm		   = "${statInfo.classInfoList[status.index].classNm}";
				var classDepth	   = "${statInfo.classInfoList[status.index].depthLvl}";

				<c:forEach var="classLvlArr" items="${statInfo.classInfoList[status.index].listClassLvl}">
					var classLvlObj ={};
					classLvlObj.objVarId = "${classLvlArr.objVarId}";
					classLvlObj.lvl		 = "${classLvlArr.lvl}";
					classLvlObj.lvlCnt	 = "${classLvlArr.lvlCnt}";
					g_classLvlArr.push(classLvlObj);
				</c:forEach>

				<c:if test="${tabClass.visible == true}">
					var deliveryClassObj={};
					deliveryClassObj.objVarId = defaultClassId;
					deliveryClassObj.ovlSn = "${status.count}";
					g_deliveryClassArr.push(deliveryClassObj);
				</c:if>
					<%--searchRangeDetai.jsp 일괄설정에서 필요한 분류탭정보--%>
					var deliveryClassAllObj ={};
					deliveryClassAllObj.objVarId = defaultClassId;
					deliveryClassAllObj.ovlSn = "${status.count}";
					deliveryClassAllObj.visible = "${tabClass.visible == true}";
					g_deliveryClassSet.push(deliveryClassAllObj);
				<c:set var="defaultItmList" value="${statInfo.classInfoList[status.index].defaultItmList}"/> 	<%--배열로 세팅하기 위해--%>
				<c:forEach var="pushClass" items="${defaultItmList}">
					defaultTempClass.push("${pushClass}");
				</c:forEach>
				for(var i=1; i<=g_maxLvl; i++){
					var defaultSplitArr = new Array;
					$.each(defaultTempClass,function(index,item){
						var compareObjId 	= item.split("^")[0];
						var compareText     = item.split("^")[1];

						var compareLvl 		= eval(compareText.split("#")[0]);
 						var compareClassId  = compareText.split("#")[1];

	 					if(compareLvl == i && defaultClassId == compareObjId){
	 						defaultSplitArr.push(compareClassId);
	 					}
					});

					if(defaultSplitArr.length >0){
						var defaultObj={};
						defaultObj.objVarId = defaultClassId;
	 					defaultObj.data = defaultSplitArr;
	 					defaultObj.classType = i;					<%--1레벨부터 ~ Max레벨까지 세팅--%>
	 					defaultObj.classLvlCnt = defaultSplitArr.length;	<%--2014.04.08 분류별 레벨의 카운트 갯수--%>
	 					g_defaultClassArr.push(defaultObj);
					}
				}

				<%-- 탭메뉴 default 갯수 세팅--%>
				var defaultTempCntArr = new Array;
				<%--default갯수 세팅--%>
				$.each(defaultTempClass,function(index,item){
					var compareObjId 	= item.split("^")[0];
					var compareText     = item.split("^")[1];
					var compareClassId  = compareText.split("#")[1];

 					if(defaultClassId == compareObjId){
 						defaultTempCntArr.push(compareClassId);
 					}
				});

				if(defaultTempCntArr.length >0){
					var defaultClassCntObject ={};
					defaultClassCntObject.objVarId = defaultClassId;
					defaultClassCntObject.dataCnt =  defaultTempCntArr.length;			<%--분류별 갯수--%>
					defaultClassCntObject.totalCnt = totalCnt;
					defaultClassCntObject.classSn =  classSn;
					defaultClassCntObject.classNm =  classNm;
					defaultClassCntObject.classDepth = classDepth;
					g_tabClassCnt.push(defaultClassCntObject);
				}
			</c:forEach>

			<%--Item항목 디폴트 체크 해야함..//분류 1레벨 체크박스 상태로 변경 무조건 1레벨만 div순번은 1_1,2_1,3_1,4_1....	 시점은 jstl에서 세팅함--%>
			<c:forEach items="${statInfo.classInfoList}" var="tabClass" varStatus="status">
				var chkObjId = "${statInfo.classInfoList[status.index].classId}";
				var classSn	 = "${status.count}";
				var classDepth	   = "${statInfo.classInfoList[status.index].depthLvl}";
				var classAllChk=0;														<%--1레벨 div영역이 모두 체크상태일때 전체선택체크 상태 유무--%>
				var classChkli=0;
				$.each(g_defaultClassArr,function(index,item){
					if(item.objVarId == chkObjId && item.classType == 1){
						$('[name=classChkLi'+classSn+'_'+1+']').each(function(index){
							var itemDataArr	 = item.data;								<%--비교 대상 Array--%>
							var defaultItmId = $(this).val().split('=')[0];
							defaultCheckbox = $(this);
							if($.inArray($(this).val().split('=')[0],itemDataArr) > -1){
								$(this).attr("checked",true);
							}
						});
					}
				});

				<%--기본 li 갯수와 checked=true--인 경우 비교--%>
				classAllChk = $('[name=classChkLi'+classSn+'_'+1+']').size();
				classChkli =  $('[name=classChkLi'+classSn+'_'+1+']:checked').size();

				<%--전체선택체크 상태--%>
				if(classChkli == classAllChk){
					$("#classChk"+classSn+'_'+1).prop("checked",true);
				}
				<c:if test="${tabClass.visible == true}">
					$.each(g_tabClassCnt,function(index,item){

						if(chkObjId == item.objVarId){
							var tabTotalCnt = "${tabClass.itmCnt}";					<%--분류별 전체 갯수--%>
							var tabSetCnt = item.dataCnt;
							var tabClassNm = "${tabClass.classNm}";
 							var str = tabClassNm+"["+tabSetCnt+"/"+tabTotalCnt+"]";
							 tempInsertClassCnt= item.dataCnt * tempInsertClassCnt;				<%--통계표에 세팅된 분류정보 전체 곱한값,분류*항목*시점=g_multiplication 표시할때,통계표 조회&다운로드 버튼 컨트롤--%>

							 tmpObj = {};
							 tmpObj.objVarId = item.objVarId;
							 tmpObj.dataCnt  = item.dataCnt;
							 g_classEachCnt.push(tmpObj);										<%--fn_countView 에서 분류 갯수를 나누어서 view../g_classEachCnt에 다시 push 그 이유는 tabClass.visible == false인 경우도 있기 때문에..--%>
 							$("#tabClassText_"+classSn+" span").html("<font title='"+str+"'>"+str+"</font>");

 							if(tabTotalCnt == tabSetCnt){ <%--2014 04.08 분류별 default갯수와 전체 갯수가 같으면--%>
	 							var h3_arrowChk = "checked";
	 							<%--분류의 전체레벨 선택 상태--%>

	 							<%--
	 							classDepth정보로 해당분류의 레벨 수 만큼 1레벨 전체선택,2레벨 전체선택..classLvlChk1_1,classLvlChk1_2 체크상태로 만들어주기
	 							처음 레이아웃 그릴때는 체크상태가 아니므로 true세팅
	 							--%>
	 							fn_lowerLank(classSn,classDepth,true,'first');		//first 최초세팅시
 							}else{
 								var h3_arrowChk = "";
 							}

 							var tempStr = tabClassNm+" ["+"<input type=\"checkbox\" id=\"classAllSelect"+classSn+"_"+classDepth+"\" name=\"classAllSelect\" onclick=\"fn_classAllSelect(\'"+classSn+"\',\'"+chkObjId+"\','all',\'"+classDepth+"\');\" "+h3_arrowChk+" title=\"<pivot:msg code="ui.label.allLvl"/> "+classDepth+"\">"+"<pivot:msg code="ui.label.allLvl"/> "+classDepth+"]";

 							$("#h3_arrow"+classSn).html(tempStr);
						}
					});

					<%-- 통계표 조회후 분류별 전체레벨이 체크 상태가 false이고 분류별/레벨별 전체선택 체크상태확인--%>
					fn_lowerLankLevel(chkObjId,classSn,classDepth);
 				</c:if>
			</c:forEach>

		g_itmCnt   = itemChkFlag;													<%--항목 갯수 세팅--%>
		g_classCnt = tempInsertClassCnt;											<%--분류 갯수 세팅--%>
		<%--화면에 세팅된 시점 갯수--%>
		for(var i=0;i<g_result.length; i++){
			g_timeCnt+=$('[name=timeChk'+g_result[i]+']:checked').size();			<%--시점 갯수 세팅--%>
		}

		<%--초기 탭메뉴 크기지정 TODO 데이터가 많을 경우 li width값이 디폴트에서 지정된 넓이로 변하는게 보이면 안됨...--%>
		fn_tabWidth();

		<%--개별 분류 문자열 세팅--%>
		fn_classStrView(g_classEachCnt);

		<%--카운트 세팅--%>
		fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);

		<%-- 2018.07.19 외부망에서만 열리는 url이기때문에 통계표조회 url에 kosis.kr가 들어가는 서비스와 호스팅만 서비스 - 김기만 --%>
		<c:if test="${statInfo.popupFileNm != null && fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0 }">
			popupHtml("${statInfo.popupHtmlUrl}${statInfo.popupFileNm}.html", "${statInfo.popupFileHeight}");
		</c:if>


		$('input[name^="itemChkLi"]').bind({
			click: function(){
				var classCal =0;
				if($(this).is(":checked")){
					classCal = g_itmCnt+1;
				}else{
					classCal = g_itmCnt-1;
				}

				g_itmCnt = classCal;

				if(g_itmCnt == g_tabItemCnt){
					$("input:checkbox[id=itemChk]").prop("checked",true);
				}else {
					$("input:checkbox[id=itemChk]").prop("checked",false);
				}

				fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);
			}
		})

		<%--부가기능 설정의 데이터 찾기 textInput 박스 숫자만 입력받고 + 숫자 3자리수마다 콤마찍기. class이름을 text로 해야함.--%>
		$('.text').css('imeMode','disabled').keypress(function(event){
		}).keyup(function(){
			if($(this).val()!==null && $(this).val()!=''){

			  var temps = $(this).val().replace(/([^0-9\-.?])/g,'');			<%--입력받을 문자는 숫자,소수점,마이너스--%>
			  var temps1= temps.replace(/(\d)([-]|[.][-]|[.][.])/g,'$1');		<%--1.숫자입력후 -입력불가 2.소수점입력후 -입력불가 3.소수점 입력후 소수점 입력불가--%>
			  var temps2= temps1.replace(/([-][-]|^[.])/g,'');					<%--1.맨처음 -입력후 -입력불가 2.맨처음 소수점 입력불가--%>
			  var temps3 = temps2.replace(/(\d)(?=(\d\d\d)+(?!\d))/g,'$1,');	<%--숫자 3자리에서 콤마찍어주기--%>
			 $(this).val(temps3);
			}
		});

		<%--브라우저 체크--%>
		fn_browser();

		<%--체인지이벤트를 click 이벤트로 바꿀수도 있음..// 		$("#tableType").bind({ 	click: function(){  --%>
		$("#tableType").change(function(){
			g_rowStr ="";
			g_colStr ="";
			g_tableType = $("#tableType option:selected").val();
			var pivotli = new Array;
			<%--표측 세팅--%>
			$("#ulLeft li").each(function(e){
				var tempObj = {};
				rowCd = $("#"+$(this).attr("id")+" input").attr("value");	<%-- li input value 추출 (코드값)--%>
				rowNm = $("#"+$(this).attr("id")).text();					<%-- li text --%>

				if(rowCd !="TIME_YEAR" && rowCd !="TIME_MQ"){				<%--TIME_YEAR,TIME_MQ빼고 세팅한다면--%>
					tempObj.cd = rowCd;
					tempObj.nm = rowNm;
					g_rowStr+=","+rowCd;
					pivotli.push(tempObj);
				}
			});

			<%--표두 세팅--%>
			$("#ulRight li").each(function(e){
				var tempObj = {};
				colCd = $("#"+$(this).attr("id")+" input").attr("value");	<%-- li input value 추출 (코드값)--%>
				colNm = $("#"+$(this).attr("id")).text();					<%-- li text--%>
				if(colCd !="TIME_YEAR" && colCd !="TIME_MQ"){
					tempObj.cd = colCd;
					tempObj.nm = colNm;
					g_colStr+=","+colCd;
					pivotli.push(tempObj);
				}
			});

			<%--피봇정보 ul li 세팅할때 TIME_MQ,TIME_YEAR 는 세팅안함..따로세팅--%>
			if(g_tableYMQ =="Y"){
				var timeObj ={};
				timeObj.cd = "TIME";
				timeObj.nm = "시점";
				pivotli.push(timeObj);
				<%--쪼개진 TIME의 위치는 표측의 마지막으로 정함..나중에 변경될 수 있음--%>
			}

			g_rowStr = g_rowStr.substring(1);
			g_colStr = g_colStr.substring(1);

			<%--년월분기표를 조회 후 다시 시계열표(표측,표두),기본조회 조회할때는...--%>
			var rowStr ="";
			var colStr ="";
			var tableType = $(this).val();
			var listBox1Arr = new Array;		<%--표측 배열--%>
			var listBox2Arr = new Array;		<%--표두 배열--%>

			<%--if체크 안하면 listBoxArr.length =1로 나옴 주의..--%>
			if(g_rowStr !=""){							<%--표측에 pivot정보가 있을경우만 배열로 변환--%>
				listBox1Arr = g_rowStr.split(",");
			}

			if(g_colStr !=""){
				listBox2Arr = g_colStr.split(",");		<%--표두에 pivot정보가 있을경우만 배열로 변환--%>
			}
			rowLength  = listBox1Arr.length;
			colLength  = listBox2Arr.length;

			var box1_Item = $.inArray("ITEM",listBox1Arr);
			var box2_Item = $.inArray("ITEM",listBox2Arr);
			var box1_Time = $.inArray("TIME",listBox1Arr);
			var box2_Time = $.inArray("TIME",listBox2Arr);

			if(tableType == 'standard'){
				<%--표측에 분류,표두에 시점,항목
					사용자가 pivot정보가 변하더라도 표측:분류,표두:시점,항목은 변함없음...pivot순서 반영하려면 수정해야함..
				--%>
				g_tableYMQ ="N";

				<%--탭메뉴 정보로 세팅 후 pivot정보 맵핑하기--%>
				var defaultItem  = new Array;
				var defaultClass = new Array;
				var defaultTime  = new Array;

				$('input[name=naviInfo]').each(function(index){
					var tabCompareType = $(this).attr("value");
					if(tabCompareType == "tabItemText"){
						defaultItem.push("ITEM");
					}else if(tabCompareType !="tabItemText" && tabCompareType !="tabTimeText"){
						defaultClass.push($(this).attr("value"));
					}
				});

				<%--항목이 있으면--%>
				if(defaultItem.length > 0){
					colStr ="TIME,ITEM";										<%--표두에 시점,항목 세팅//사용자가 변경한 pivot정보의 시점 항목 순서랑 다름..--%>
				}else{
					colStr ="TIME";
				}
				rowStr = defaultClass.join(",");								<%--표측 세팅--%>

			}else if(tableType == 'timeSeriesV'){
				<%--시계열표(표측)의 시점은 무조건 표두에 못감..--%>
				if(rowLength < 1 ){
					rowStr = "TIME";											<%--표측에 pivot정보가 없을때 표측에는 시점만 세팅--%>
					listBox2Arr.splice($.inArray("TIME",listBox2Arr),1);		<%--표두에 있는 시점 삭제,분류 항목은 그대로--%>
					colStr = listBox2Arr.join(",");
				}else if(colLength < 1){
					if($.inArray("ITEM",listBox1Arr) > -1){						<%--항목이 ${statInfo.itemCnt}값이 1이면 탭메뉴,pivot정보에도 없음--%>
						colStr = "ITEM";										<%--표두에 pivot정보가 없을때 표두에는 항목만 세팅--%>
						listBox1Arr.splice($.inArray("ITEM",listBox1Arr),1);	<%--표측에 있는 항목 삭제,분류 시점은 그대로--%>
						rowStr = listBox1Arr.join(",");
					}else{
						<%--항목이 없을경우 item값 세팅 안함..시계열표의 표측정보는 시점이 항상 마지막임...--%>
						listBox1Arr.splice($.inArray("TIME",listBox1Arr),1);	<%--시점순서가 표측 pivot정보에 의해 변경된 경우 ....삭제 후 마지막에 다시 삽입--%>
						listBox1Arr.push("TIME");

						colStr ="";
						rowStr = listBox1Arr.join(",");
					}
				}else{															<%--표측 표두 둘다 세팅되어 있는 경우--%>
					if(box1_Item > -1){											<%--표측에 ITEM이 있는 경우--%>
						<%--1.ITEM은 표두로 이동 2.시점은 표측마지막으로 이동--%>
						listBox1Arr.splice(box1_Item,1);						<%--표측에서 항목 삭제--%>
						listBox2Arr.push("ITEM");								<%--표측에서 항목이 표두로 이동시 맨 마지막에 세팅--%>
						if(box2_Time > -1){										<%--표두에 시점이 있다면--%>
							listBox2Arr.splice(box2_Time,1);					<%--표두에 시점 삭제--%>
						}
						if(box1_Time > -1){										<%--표측에 시점이 있다면--%>
							listBox1Arr.splice(box1_Time,1);					<%--표측시점 삭제...순번이 변경될 수 있으므로.--%>
						}
						listBox1Arr.push("TIME");								<%--표측 마지막에 시점 삽입--%>
						rowStr = listBox1Arr.join(",");
						colStr = listBox2Arr.join(",");
					}else if(box2_Item > -1){									<%--표두에 항목ITEM이 있는 경우(항목 순서 무시)--%>
						if(box2_Time > -1){										<%--표두에 시점이 있는 경우 --%>
							listBox2Arr.splice(box2_Time,1);					<%--표두에서 시점삭제--%>
						}
						if(box1_Time > -1){
							listBox1Arr.splice(box1_Time,1);					<%--표측시점 삭제...순번이 변경될 수 있으므로.--%>
						}
						listBox1Arr.push("TIME");								<%--표측 마지막에 시점 삽입--%>
						rowStr = listBox1Arr.join(",");
						colStr = listBox2Arr.join(",");
					}else{														<%--표측,표두에 항목이 없는 경우 표측 마지막에 시점 세팅--%>
						if(box2_Time > - 1){									<%--표두에 시점이 있으면--%>
							listBox2Arr.splice(box2_Time,1);
						}else if(box1_Time > -1){
							listBox1Arr.splice(box1_Time,1);
						}
						listBox1Arr.push("TIME");
						rowStr = listBox1Arr.join(",");
						colStr = listBox2Arr.join(",");
					}
				}
				g_tableYMQ ="N";
			}else if(tableType == 'timeSeriesH'){
				if(rowLength < 1){
					if($.inArray("ITEM",listBox2Arr) > -1){
						rowStr = "ITEM";										<%--표측에 pivot정보가 없을때 표측에는 항목만 세팅--%>
						listBox2Arr.splice($.inArray("ITEM",listBox2Arr),1);	<%--표두에 있는 항목 삭제,분류 시점은 그대로--%>
						colStr = listBox2Arr.join(",");							<%--pivot순서 그대로 적용--%>
					}else{
						rowStr ="";
						colStr = listBox2Arr.join(",");
					}
				}else if(colLength < 1){
					if($.inArray("ITEM",listBox1Arr) > -1){						<%--항목이 ${statInfo.itemCnt}값이 1이면 탭메뉴,pivot정보에도 없음--%>
						colStr = "TIME";										<%--표두에 pivot정보가 없을때 표두에는 시점만 세팅--%>
						listBox1Arr.splice($.inArray("TIME",listBox1Arr),1);
						listBox1Arr.splice($.inArray("ITEM",listBox1Arr),1);	<%--항목순서가 표측 pivot정보에 의해 변경된 경우....삭제 후 마지막에 다시 삽입--%>
						listBox1Arr.push("ITEM");
						rowStr = listBox1Arr.join(",");
					}else{														<%--항목이 없을 경우 item값 세팅 안함--%>
						listBox1Arr.splice($.inArray("TIME",listBox1Arr),1);	<%--시점순서가 표측 pivot정보에 의해 변경된 경우 ....삭제 후 마지막에 다시 삽입--%>
						colStr = "TIME";
						rowStr = listBox1Arr.join(",");
					}
					g_tableYMQ ="N";

				}else{															<%--표측 표두 둘다 세팅되어 있는 경우--%>
					if(box1_Item > -1){											<%--표측에 ITEM이 있는 경우--%>
						listBox1Arr.splice($.inArray("ITEM",listBox1Arr),1); 	<%--표측에서 항목삭제...순번이 변경 될 수 있으므로--%>
						listBox1Arr.push("ITEM");							 	<%--표측 마지막에 항목 삽입--%>
						if(box2_Time > -1 ){
							rowStr = listBox1Arr.join(",");
							colStr = listBox2Arr.join(",");
						}else if(box1_Time > -1){
							listBox1Arr.splice(box1_Time,1);
							<%--시점의 순서 위치에 따라 표두에 올라가는 위치는 맨마지막--%>
							listBox2Arr.push("TIME");
							rowStr = listBox1Arr.join(",");
							colStr = listBox2Arr.join(",");
						}
					}else if(box2_Item > -1){				  				 	<%--표두에 항목ITEM이 있는 경우(항목 순서 무시)--%>
						if(box2_Time > -1){					   					<%--표두에 시점이 있는 경우--%>
							listBox2Arr.splice(box2_Item,1);
						}else if(box1_Time > -1){
							listBox1Arr.splice(box1_Time,1);
							listBox2Arr.splice(box2_Item,1);
							listBox2Arr.push("TIME");
						}else{													<%--년월 분기표에서 시계열표(표두)--%>
							listBox2Arr.splice(box2_Item,1);
							listBox2Arr.push("TIME");
						}
						listBox1Arr.push("ITEM");
						rowStr = listBox1Arr.join(",");
						colStr = listBox2Arr.join(",");
					}else{														<%--표측,표두에 항목이 없는 경우--%>
						if(box2_Time > -1){
							rowStr = listBox1Arr.join(",");
							colStr = listBox2Arr.join(",");
						}else if(box1_Time > -1){
							listBox1Arr.splice(box1_Time,1);					<%--표두로 시점만 올라감--%>
							listBox2.push("TIME");
							rowStr = listBox1Arr.join(",");
							colStr = listBox2Arr.join(",");
						}
					}
				}
				g_tableYMQ ="N";
			}else if(tableType == 'perYear'){									<%--년월분기표 선택시 챠트범례 pivot정보기준 표측 마지막 년 표두처음 월--%>
				g_tableYMQ = "Y";												<%--년월 분기표 플래그...--%>
				if(rowLength < 1){
					rowStr = "TIME_YEAR";										<%--표측에 pivot정보가 없을때 표측에는 TIME_YEAR 세팅--%>
					listBox2Arr.splice($.inArray("TIME",listBox2Arr),1);		<%--표두에 있는 시점 삭제,분류 항목은 그대로--%>
					listBox2Arr.unshift("TIME_MQ");
					colStr = listBox2Arr.join(",");
				}else if(colLength < 1){
					colStr = "TIME_MQ";
					listBox1Arr.splice($.inArray("TIME",listBox1Arr),1);		<%--우선 TIME값 안 쪼개졌을때 먼저 처리--%>
					listBox1Arr.push("TIME_YEAR");
					rowStr = listBox1Arr.join(",");
				}else{															<%--표측,표두에 정보가 있고--%>
					if(box1_Item > -1){
						listBox1Arr.splice($.inArray("ITEM",listBox1Arr),1); 	<%--표측에서 항목삭제...순번이 변경 될 수 있으므로--%>
						listBox1Arr.push("ITEM");							 	<%--표측 마지막에 항목 삽입--%>
					}else if(box2_Item > -1){
						listBox2Arr.splice($.inArray("ITEM",listBox2Arr),1); 	<%--표측에서 항목삭제...순번이 변경 될 수 있으므로--%>
						listBox2Arr.push("ITEM");							 	<%--표측 마지막에 항목 삽입--%>
					}

					if($.inArray("TIME",listBox1Arr) > -1){						<%--표측에 시점정보가 있으면--%>
						listBox1Arr.splice($.inArray("TIME",listBox1Arr),1);	<%--우선 TIME값 안 쪼개졌을때 먼저 처리--%>
						listBox1Arr.push("TIME_YEAR");							<%--마지막 값에 TIME_YEAR로 변경..--%>
						listBox2Arr.unshift("TIME_MQ");
					}else{														<%--표측에 시점정보가 없고 표두에 있으면--%>
						listBox1Arr.push("TIME_YEAR");
						listBox2Arr.splice($.inArray("TIME",listBox2Arr),1);
						listBox2Arr.unshift("TIME_MQ");							<%--TIME_MQ 맨마지막에 넣기--%>
					}
					rowStr = listBox1Arr.join(",");
					colStr = listBox2Arr.join(",");
				}

				var yearObj={};
				var quarterObj ={};

				$.each(pivotli,function(index,item){
					if(item.cd =="TIME"){										<%--TIME은 한개임...한개 찾아서 월/분기 세팅--%>
						pivotli.splice(index,1);								<%--TIME 쪼개기--%>
						yearObj.cd 	  = "TIME_YEAR";
						yearObj.nm 	  = "<pivot:msg code="ui.label.timeYear"/>";
						quarterObj.cd = "TIME_MQ";
						quarterObj.nm = "<pivot:msg code="ui.label.timeMQ"/>";
						pivotli.push(yearObj);
						pivotli.push(quarterObj);
					}
				});

			}else if(tableType == 'default'){
				rowStr = defaultRowList.join(",") ;
				colStr = defaultColList.join(",") ;
			}

			<%--표측--%>
			form.rowAxis.value = rowStr;
			<%--표두--%>
			form.colAxis.value = colStr;

			<%--pivot ul 초기화--%>
			$("#ulLeft").empty();
			$("#ulRight").empty();

			var	setList1 = rowStr.split(",");						<%--pivot정보는 존재하더라도--%>
			var setList2 = colStr.split(",");
			var list1Cnt = setList1.length;							<%--pivot정보로 세팅하면 안됨--%>
			var list2Cnt = setList2.length;

			<%--한글보기,영문보기 표측세팅--%>
			for(var i=0; i<list1Cnt; i++){
				var listData1 = setList1[i];
				$.each(pivotli,function(index,item){
					if(listData1 == item.cd){
						var appendLi="";
						appendLi="<li id='Le"+i+"'>";
						appendLi+="<input type='hidden' value='"+listData1+"'/><a href='#'>"+item.nm+"</a></li>";
						$("#ulLeft").append(appendLi);
					}
				});
			}

			<%--표두세팅--%>
			for(var ii=0; ii<list2Cnt; ii++){
				var listData2 = setList2[ii];
				$.each(pivotli,function(index,item){
					if(listData2 == item.cd){
						var appendRi="";
						appendRi="<li id='Ri"+ii+"'><input type='hidden' value='"+listData2+"'/><a href='#'>"+item.nm+"</a></li>";
						$("#ulRight").append(appendRi);
					}
				});
			}
		});

		<%--챠트 화면에서 selectBox 변경시 이벤트 등록--%>
		$("#chartViewCnt").change(function(){
			fn_fusionChartExcute(g_ChartGubun);
		});

		form.classAllArr.value = JSON.stringify(g_deliveryClassArr);
		form.classSet.value = JSON.stringify(g_deliveryClassSet);
		<%--분석가능 여부를 기준으로 fn_enable체크--%>
		 if(g_analyzable){
			fn_enable(g_result[0]);
		 }else{
			<%--년월 분기표 세팅--%>
			if(g_defaultPeriodArr.length == 1 && g_tableTypeOption.length==1){
				if("${ParamInfo.tableType}" == 'perYear'){
					$("#tableType option:eq(2)").after("<option value='perYear' selected='selected'><pivot:msg code="ui.combo.perYear"/></option>");
					<%--년원 분기표를 조회하고 다른 부가기능 선택했을 경우--%>
				}else{
					$("#tableType option:eq(2)").after("<option value='perYear'><pivot:msg code="ui.combo.perYear"/></option>");
				}
			}
		 }

		if(g_directYn != "Y"){
			fn_search();
		}

		$("#helpClose1, #helpClose2").bind({
			click: function(){
				$("#pop_levelExpr, #pop_parentLevel").css("display","none");
			}
		});


		fn_chartColorSet();

		<%--
			2016.02.16 통계표 상단 틀고정 시작
		--%>
		$("#ThtmlGrid").css("overflow", "hidden");
		$("#ThtmlGrid").css("border-bottom", "none");
		$("#ThtmlGrid").css("border-right", "none");
		$("#ThtmlGrid").width($("#htmlGrid").width() - 17); <%-- 세로스크롤바(17px) 만큼 줄인다...아래 있는 htmlGrid의 세로 스크롤바를 보이게 하기위해    --%>
		$("#htmlGrid").scroll(function (){
			$("#ThtmlGrid").css("overflow", "auto");
			$("#ThtmlGrid").scrollLeft($("#htmlGrid").scrollLeft());
			$("#ThtmlGrid").css("overflow-x", "hidden");
		});
		<%-- 통계표 상단 틀고정 끝 --%>

});
		function fn_browser(){
			<%--jquery 1.9.0버전 부터 broswer 확인 함수 없어짐...--%>
			var browser = (function() {
				  var s = navigator.userAgent.toLowerCase();

				  var match = /(chrome)[ \/](\w.]+)/.exec(s) ||
					  		  /(webkit)[ \/](\w.]+)/.exec(s) ||
				              /(opera)(?:.*version)?[ \/](\w.]+)/.exec(s) ||
				              /(msie) ([\w.]+)/.exec(s) ||
				              /(mozilla)(?:.*? rv:([\w.]+))?/.exec(s) ||
				             [];
				  return { name: match[1] || "", version: match[2] || "0" };
				}());

				var isIe11 = !!(navigator.userAgent.match(/Trident/)&& navigator.userAgent.match(/rv:11.0/));

				if(browser.name == 'msie'){
					gr_browser="ie";
					g_browserVersion = browser.version;
				}
		}


		<%--
		/************************************************************************
		함수명 : fn_tabWidth()
		설   명 : tabMenu li width값 동적세팅	추후 사이즈값 조절
		 ************************************************************************/
		 --%>
		function fn_tabWidth(){
			var liCnt = $('.selection1 li').size();

			var divisionCnt;
			divisionCnt = 586/liCnt;
			atextCnt = divisionCnt - 20;
			if(liCnt>4){
				<%--통계표조회 버튼이 tabMenu로 이동 20131217 넓이 83--%>
				var lastBgWidth = 852 - 820 + (liCnt*6) +15;

				$('.selection1 li').each(function(index){
					$(this).css("width",divisionCnt);
					$(this).find('a').css("width",atextCnt);
				});
			}else{
				var lastBgWidth = 852 - (180*liCnt) + (liCnt*5) +15;

				$('.selection1 li').each(function(index){
					$(this).css("width","137px");
					$(this).find('a').css("width","120px");
				});
			}
		}


		function fn_changeTabText(){
			var tabTimeCnt = "${statInfo.periodCnt}";
			var tabItemText="";
			var tabTimeText="";
			<%--항목 탭 세팅--%>
			tabItemText= g_tabItemNm+"["+g_itmCnt+"/"+g_tabItemCnt+"]";
			$("#tabItemText span").html("<font title='"+tabItemText+"'>"+tabItemText+"</font>");

			var g_tabTimeCnt = eval(g_classTabCnt)+1;
 			tabTimeText = g_tabTimeNm+"["+g_timeCnt+"/"+tabTimeCnt+"]";
 			$("#tabTimeText a").html("<font title='"+tabTimeText+"'>"+tabTimeText+"</font>");

 			<%--pivot 표두 첫번째 colspan 1000개 체크를 위한 Array 세팅--%>
 			g_pivotColChk = new Array();
 			g_pivotColChk = g_classEachCnt.concat([]);						<%--배열 메모리 참조--%>

			var itemObj ={};
			itemObj.objVarId ="ITEM";
			itemObj.dataCnt = g_itmCnt;

			var timeObj ={};
			timeObj.objVarId = "TIME";
			timeObj.dataCnt = g_timeCnt;

			g_pivotColChk.push(itemObj);
			g_pivotColChk.push(timeObj);
		}

		function fn_classStrView(p_classEachCnt){
			var p_classEachCntLength = p_classEachCnt.length-1;
			g_classViewCntStr ="";
			$.each(p_classEachCnt,function(index,item){
				if(index == p_classEachCntLength){
					g_classViewCntStr += item.dataCnt;
				}else {
					g_classViewCntStr += item.dataCnt+"*";
				}
			});
		}

		function fn_countView(p_itmCnt,p_classCnt,p_timeCnt,p_classViewCntStr){

			g_textField = null;
		    g_multiplication = 0;

			g_itemMultiply = p_itmCnt * p_classCnt;							<%--항목* 분류--%>

			g_multiplication = g_itemMultiply * p_timeCnt;
			<%-- 2015.06.09 분류없는 통계표는 메세지 출력 시 분류() 부분을 나오지 않게 함 --%>
			if(p_classViewCntStr != ''){	<%-- 기존 메세지 출력 --%>
				g_textField = "("+p_itmCnt+") X <pivot:msg code="ui.label.cond.class"/>("+p_classViewCntStr+") X <pivot:msg code="ui.label.cond.time"/>("+g_timeCnt+") ="+g_multiplication+""; //분류 갯수 개별
			}else{ <%-- 분류없는 통계표 메세지 출력 --%>
				g_textField = "("+p_itmCnt+") X <pivot:msg code="ui.label.cond.time"/>("+g_timeCnt+") ="+g_multiplication+"";
			}

			$("#changeTextLi").text(g_textField);
			$("#itemMultiply").val(g_itemMultiply);
			<%--탭메뉴 text세팅--%>
			fn_changeTabText();

			<%--분류 * 항목 조합수가 10,000 미만이면--%>
			g_mixDimCnt = g_dimCo * g_timeCnt;

			<%--2013.12.06 < 에서 <=로 변경
				2013.12.06 dimCo수를 체크하지 않도록 변경
				dimCo수 다시 조정 시 주석위치 변경--%>
				g_mixItemCnt = g_itemMultiply * g_timeCnt;				<%--분류/항목 조합수 * 시점--%>
				if(g_mixItemCnt > g_maxCellDefault){
					<%-- 2015.03.05 만셀, 2만셀 조회 제한 해제를 위한 조건 추가 limitYn != "1" --%>
					if( g_maxChkFlag == "Y" && g_mixItemCnt < g_maxCell && g_mixItemCnt <= g_maxCellDownload && $("#maxCellOver").val() != "Y" && $("#maxCellOver").val() != "N" && limitYn != "1"){
						alert("<pivot:msg code="confirm.maxCellOver.msg"/>");
						$("#maxCellOver").val("Y");
					}

					if(g_mixItemCnt > g_maxCell){
						if( $("#searchImg2").css("display") == "none" ){
							alert("<pivot:msg code='confirm.Download.msg'/>");
						}

						$("#searchImg1").css("display", "none");
						$("#searchImg2").css("display", "block");

						$("#ico_swap").addClass("off");
						$("#ico_addfunc").addClass("off");

						<c:if test="${statInfo.downloadable}">
							$("#ico_download").addClass("off");
							$("#ico_myscrap").addClass("off");
						</c:if>

						<c:if test="${statInfo.analyzable}">
							$("#ico_analysis").addClass("off");
						</c:if>

						$("#changeSpanRed").attr("class","f_gray");

						<%--조합수가 200000 셀을 초과하면 dimCo 체크--%>
						if(g_mixItemCnt > g_maxCellDownload){
							if(g_mixDimCnt > g_maxCellDownload){
								$("#changeSpanGray").attr("class","f_gray");
								$("#changeDownText").attr("class","f_red");
							}else{
								$("#changeSpanGray").attr("class","f_red");
								$("#changeDownText").attr("class","f_gray");
							}

						}else{
							$("#changeSpanGray").attr("class","f_red");
							$("#changeDownText").attr("class","f_gray");
						}
					}else{
						fn_enableSearchBtn();
					}
				}else{
					fn_enableSearchBtn();
				}
		}

		function fn_enableSearchBtn(){
			$("#searchImg2").css("display", "none");
			$("#searchImg1").css("display", "block");

			$("#ico_swap").removeClass("off");
			$("#ico_addfunc").removeClass("off");

			<c:if test="${statInfo.downloadable}">
				$("#ico_download").removeClass("off");
				$("#ico_myscrap").removeClass("off");
			</c:if>

			<c:if test="${statInfo.analyzable}">
				if(g_assayYn == 'Y'){
					$("#ico_analysis").removeClass("off");
				}
			</c:if>

			$("#changeSpanRed").attr("class","f_red");
			$("#changeSpanGray").attr("class","f_gray");
			$("#changeDownText").attr("class","f_gray");
		}

		<%--10,000 셀 초과 ~ 200,000이하 파일 다운로드--%>
		function fn_downLarge(){
			<c:choose>
				<c:when test="${statInfo.downloadable}">
					<%--2013.12.06 다운로드인 경우에만 dimCo수를 체크--%>
					if( g_multiplication > g_maxCellDownload && g_mixDimCnt > g_maxCellDownload){
						alert("<pivot:msg code="search.condition.msg"/>");
						return;
					}
					popupControl('pop_downlarge', 'show', 'modal');
				</c:when>
				<c:otherwise>
					alert("<pivot:msg code="alert.not.downloadable.msg"/>");
				</c:otherwise>
			</c:choose>
		}

		<%--
		/************************************************************************
		함수명 : fn_classLvlChk()
		설   명 : 사용자가 분류 조회 후 체크선택된 분류값 세팅,g_classChoiceArr 리터럴 배열
		인   자 : 1.objVarId = (탭메뉴의 objVarId)
				 2.classSn  = 분류탭의 순서
				 3.classLvl = 체크된 분류레벨
				 4.itmId	= 분류값
				 5.checkbox = 선택된 object 객체
		 ************************************************************************/
		 --%>
		function fn_classLvlChk(objVarId,classSn,classLvl,itmId,checkbox){
			var lvlObj ={};
			var lvlArray = new Array;
			var arrCnt = g_defaultClassArr.length;
			var tempArr = new Array;
			var removeArr = new Array;
			var chkFlag;
			var classBoxName = $(checkbox).attr('name');					<%--선택된 분류정보의 name값 추출--%>
			var extant=0;
			if($(checkbox).is(":checked")){

				if(arrCnt > 0){												<%--분류정보 저장 레벨--%>
					lvlObj.objVarId  = objVarId;							<%--탭메뉴의 분류값(obj_var_id)--%>
					lvlObj.classType = classLvl;							<%--사용자가 선택한 분류레벨itemId--%>
					for(var k=0; k<arrCnt; k++){
						if(g_defaultClassArr[k].objVarId == objVarId && g_defaultClassArr[k].classType == classLvl){
							tempArr= g_defaultClassArr[k].data;
							tempArr.push(itmId);
							lvlObj.data = tempArr;
							lvlObj.classLvlCnt = tempArr.length;
							g_defaultClassArr.splice(k,1,lvlObj);
							extant++;										<%--extant가 0보다 크므로 다음 if문 실행안됨.--%>
						}
					}

					<%--레벨 전체선택 해제후 최초에 개별선택하는 경우 한번만 실행--%>
					if(extant == 0){
						lvlArray.push(itmId);
						lvlObj.data = lvlArray;
						lvlObj.classLvlCnt = lvlArray.length;
						g_defaultClassArr.push(lvlObj);
					}
				}
				<%--화면에서 분류에 대한 레벨 다 해제하고 ->개별또는 전체로 선택한경우--%>
				else{
					lvlArray.push(itmId);								<%--사용자 선택한 분류레벨--%>
					lvlObj.objVarId  = objVarId;
					lvlObj.classType = classLvl;						<%--레벨 순서--%>
					lvlObj.data = lvlArray;
					lvlObj.classLvlCnt = lvlArray.length;
					g_defaultClassArr.push(lvlObj);
				}
				chkFlag = true;
				chkCnt = 1;

				$.each(g_defaultClassArr,function(index,item){
					if(item.objVarId == objVarId && item.classType == classLvl){
						var classLvlCnt = item.classLvlCnt;
						fn_oneLankLevel(objVarId,classSn,classLvl,classLvlCnt);
					}
				});


				$.each(g_defaultClassArr,function(index,item){
					if(item.objVarId == objVarId && item.classType == classLvl){
						var classLvlCnt = item.classLvlCnt;
						fn_oneLankLevel(objVarId,classSn,classLvl,classLvlCnt);
					}
				});
			}else{
				<%--전체 선택 해제/ 레벨별 전체선택 해제--%>
				$("#classChk"+classSn+'_'+classLvl).prop("checked",false);
				$("#classLvlAllChk"+classSn+'_'+classLvl).prop("checked",false);

				<%--체크 해제시 해당 ID 삭제후 다시 세팅--%>
				for(var k=0; k<arrCnt; k++){
					lvlObj.objVarId  = objVarId;							<%--탭메뉴 분류값(obj_var_id)--%>
					lvlObj.classType = classLvl;							<%--분류 선택 레벨--%>
					if(g_defaultClassArr[k].objVarId == objVarId && g_defaultClassArr[k].classType == classLvl){
						removeArr = g_defaultClassArr[k].data;
							$.each(removeArr,function(index,item){
								if(item == itmId){
									removeArr.splice(index,1);
									if(removeArr.length > 0 ){				<%--체크상태가 한개이상--%>
										lvlObj.data = removeArr;
										lvlObj.classLvlCnt = removeArr.length;
									}
								}
							});
						if(lvlObj.data != null){
							g_defaultClassArr.splice(k,1,lvlObj);
						}
					}
				}
			chkFlag = false;
			chkCnt = -1;
			}

		var classBoxCnt  = $("[name="+classBoxName+"]:checked").size();
		var compareCnt   = $("[name="+classBoxName+"]").size();
		<%--체크 선택시 체크된 갯수와 레벨에 보여지는 갯수가 같을 경우 전체선택 체크해줘야 함--%>
		if(classBoxCnt == compareCnt){
			$("#classChk"+classSn+'_'+classLvl).prop("checked",true);
		}

		<%--분류 개별선택--%>
		var selectType = "one";

		fn_tabTextView(objVarId,selectType,chkCnt);
		fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);
	}


		<%--
		/************************************************************************
		함수명 : fn_timeCountChk(timeCountObj,searchType)
		설   명 : 시점탭에서 체크이벤트 함수(체크 true false 확인 후 count계산
		인   자 : 1.checkbox = 선택된 object 객체
				 2.searchType
		 ************************************************************************/
		 --%>
		function fn_timeCountChk(timeCountObj,searchType){
			var timeChkFlag = $(timeCountObj).is(":checked");
			if(timeChkFlag == true){
				timeCal = g_timeCnt + 1;
			}else{
				timeCal = g_timeCnt - 1;
			}
			g_timeCnt = timeCal;
			fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);
		}


		<%--
		/************************************************************************
		함수명 : fn_tabTextView(objVarId,selectType,chkCnt)
		설   명 : 분류 정보만 컨트롤
		인   자 : 1.objVarId,selectType,chkCnt		//TODO ..selectType 추후 삭제
		 ************************************************************************/
		 --%>
		function fn_tabTextView(objVarId,selectType,chkCnt){
			var tempCnt;
			var tempObject={};
			var tempInsertClassCnt = 1;
			$.each(g_tabClassCnt,function(index,item){
				if(objVarId == item.objVarId){
					tempCnt = item.dataCnt+chkCnt;
					var tempStr = item.classNm+"["+tempCnt+"/"+item.totalCnt+"]";
					$("#tabClassText_"+item.classSn+" span").html("<font title='"+tempStr+"'>"+tempStr+"</font>");

					tempObject.objVarId = objVarId;
					tempObject.dataCnt  = tempCnt;
					tempObject.totalCnt = item.totalCnt;
					tempObject.classSn  = item.classSn;
					tempObject.classNm	= item.classNm;
					tempObject.classDepth = item.classDepth;

					<%--배열 인덱스 초기화상태 확인--%>
					g_tabClassCnt.splice(index,1,tempObject);

					<%--분류별 전체 classAllSelect 체크상태 동기화--%>
					if(tempCnt == item.totalCnt){
						$("#classAllSelect"+tempObject.classSn+"_"+tempObject.classDepth).prop("checked",true);
					}else{
						$("#classAllSelect"+tempObject.classSn+"_"+tempObject.classDepth).prop("checked",false);
					}

					for(var i=0; i<g_classEachCnt.length; i++){
						if(objVarId == g_classEachCnt[i].objVarId){
							tmpObj ={};
							tmpObj.objVarId = objVarId;
							tmpObj.dataCnt  = tempCnt;
							g_classEachCnt.splice(index,1,tmpObj);
						}
					}
				}
			});

			$.each(g_tabClassCnt,function(index,item){
				tempInsertClassCnt = item.dataCnt * tempInsertClassCnt;
			})

			g_classCnt = tempInsertClassCnt;
			fn_classStrView(g_classEachCnt);
		}


		function fn_sortSearch(colIdx, type){
			$("#ordColIdx").val(colIdx);
			$("#ordType").val(type);

			var t_ColIdx = colIdx;
			var t_Type = type;

			fn_search('','sort');
		}

		function fn_time_sort(val){
			if( val == 0){
				document.getElementById("prdSortAsc").checked = true;
			}else{
				document.getElementById("prdSortDesc").checked = true
			}

			fn_search();
		}

		function fn_search(rangeType, sort){
			<%--피봇 정보중 표두가 2개이상일 경우 첫번재 분류의 colspan 값 체크--%>
			var tempArray = form.colAxis.value.split(",");
			if(tempArray.length >= 2){
				var colMaxCnt=1;
				$.each(tempArray,function(index,item){
				var colItem = item;
				if(index > 0){
					$.each(g_pivotColChk,function(index,item){
						if(colItem == item.objVarId){
							colMaxCnt = item.dataCnt * colMaxCnt;
						}
					});
				}
				});
				if(colMaxCnt >= 1000){
					alert( "<pivot:msg code="alert.colMaxCnt.msg"/>" );
					popupControl('pop_pivotfunc', 'show', 'modal');
					return;
				}
			}

			if(g_multiplication == 0){
				alert("<pivot:msg code="fail.selectNotSearch.msg"/>");
				if(form.doAnal.value =="Y"){
					form.doAnal.value ="N";
				}
				return;
			}

			var headCnt = $('[name=headCheck]:checked').size();
			if(form.doAnal.value == "Y"){
				if(headCnt > 1){											<%--분석 조회 후 2개이상의 주기를 선택한 경우--%>
					form.doAnal.value ="N";

				}else{														<%--주기가 한개이며 분석 가능한 주기 현재 체크상태 확인--%>
					$('[name=headCheck]').each(function(index){				<%--세팅된 시점Div에서 checked 된 M,Q 찾기--%>
						if($(this).is(":checked")){
							headType = $(this).attr("value");
						}
					});

					if(headType != $("#funcPrdSe").val()){
						if(form.isFirst.value == "N"){
							form.doAnal.value ="N";
						}
					}
				}
			}

			form.selectAllFlag.value	= "N";
			fn_searchProcess(rangeType, sort);
		}

		<%--
		/************************************************************************
		함수명 : fn_searchProcess()
		설   명 : 통계표 조회 버튼 클릭시 초기조회 조건 세팅
				1. 전체주기에 따른 체크 시점 세팅
				2. 항목은 input box name=itemChkLi
				3. 탭메뉴 분류순서, 분류에 따른 DIV 갯수로 조합하여  inputbox name=classChkLi"${status.count}"+"_"+"${i} 분류찾기
		 ************************************************************************/
		 --%>
		function fn_searchProcess(rangeType, sort){

			if( g_multiplication > g_maxCell ){
				return;
			}

			if(sort != 'sort'){
				$("#ordColIdx").val("");
				$("#ordType").val("");
			}

			fn_searchCond();
			<%--viewKind (1 : 단순조회)--%>
			if(form.doAnal.value == "Y"){
				form.viewKind.value = "5";
			}else{
				form.viewKind.value="1";
				form.viewSubKind.value = "";
			}

			if($("#enableWeight").is(':checked')){
				if( $("#doAnal").val() == "Y"){
					$("#doAnal").val("N");
				}
			}

			$("#reqCellCnt").val(g_mixItemCnt);

			<%--그리드 조회인지 파일 다운로드 및 파일 종류를 파악하기 위한 변수--%>
			form.view.value = "table";
			var dataOpt = g_dataOpt;
			var dataOpt2 = $("#dataOpt2 option:selected").val();
			$("#classAllArr").val(JSON.stringify(g_deliveryClassArr));
			if(dataOpt != dataOpt2){
				$("#isChangedDataOpt").val("Y");
				$("#dataOpt").val(dataOpt2);
				form.action = "statHtml.do";
				form.method = "POST";
				form.submit();
				return;
			}

			var scrollLeft = $("#htmlGrid").scrollLeft();

 			if(g_tableType == "default" && $("#ordColIdx").val() == ""){
 				var paramUrl = "orgId=" + $("#orgId").val()
				 + "&tblId=" + $("#tblId").val()
				 + "&isFirst=" + $("#isFirst").val()
				 + "&logSeq=" + $("#logSeq").val()
				 + "&vw_cd=" + $("#vwCd").val()
				 + "&list_id=" + $("#listId").val()
				 + "&conn_path=" + $("#connPath").val()
				 + "&viewKind=" + $("#viewKind").val()
				 + "&view=" + $("#view").val()
				 + "&obj_var_id=" + $("#obj_var_id").val()
				 + "&itm_id=" + $("#itm_id").val()
 				 + "&useAddFuncLog=1_A"
 				 + "&maxCellOver=" + $("#maxCellOver").val();

				 if(g_mode == 'tab'){
					paramUrl += "&mode=tab";
				 }

				 <%--2014.02.05 추가 담당자 레벨 조회--%>
				 if("${ParamInfo.chargerLvl}" == "1"){
					 paramUrl +="&pub=1";
				 }

				 if("${ParamInfo.language}" == "en"){
					 paramUrl +="&language=en";
				 }

				 if("${ParamInfo.viewType}" == "H" || "${ParamInfo.viewType}" == "B"){

					 paramUrl +="&st=" + $("#st").val();

					 if("${ParamInfo.viewType}" == "H"){
						 var tmpStr = $("#dbUser").val();
						 paramUrl +="&dbUser=" + tmpStr.substring(0,tmpStr.length-1);
					 }
				 }
 				location.href = "<%=request.getContextPath()%>/statHtml.do?" + paramUrl;
			}else{
				if(rangeType =="Y"){
					popupControl('pop_detailfunc','hide','modal');
				}

				 var debugMode = "<%=request.getParameter("debug")%>" ;
				 if(debugMode =="Y"){
				 	$("#debug").val("Y");
				 }
				fn_progressBar('show');
				$.ajax({
					dataType : 'json',
					type : 'POST',
					url : "<%=request.getContextPath()%>/html.do",
					dataType : "json",
					data : $("#ParamInfo").serialize(),
					success : function(response,status){
						var errCode = response.errCode;
						var errMsg = response.errMsg;

						if(errCode != null && errCode == "1"){
							alert(errMsg);
							$("#htmlGrid").empty();
							$("#ThtmlGrid").empty();
							fn_progressBar('hide');
							var tId = $("#tabMenu li:eq(0)").attr("id");
							var idx = tId.split("_");

							if(!idx[1]){
								idx = 0;
							}else{
								idx = idx[1];
							}

							g_flag = true;
							$("#"+tId).attr("class","menu_off");
							fn_disPlay(tId, idx);
							return;
						}

						var data = response.result ;

						if(status ='success'){
							if(data!=null && data.length >0){

								var levelExpr = "${statInfo.levelExpr}";

								<%-- 2015.03.05 만셀, 2만셀 조회 제한 해제를 위한 조건 추가 limitYn != "1" --%>
								if($("#isFirst").val() != "N" && (levelExpr == "T" || levelExpr == "S") && g_mixCnt > g_maxCell && limitYn !="1"){
									alert("<pivot:msg code="alert.levelExpr.first"/>");
								}
								g_htmlGrid =data[0];
								g_cmmt	   =data[1];
								g_chart    =data[2];
								g_ThtmlGrid    =data[3]; <%-- StatDataInfoManager 618라인에서 추가된 값 --%>
								g_chartLableArr = g_chart.lable;			<%--챠트 표두 list--%>
								g_chartDataArr  = g_chart.data;				<%--챠트 표측title 및 데이터 list--%>
								g_chartMsg		= g_chart.msg;				<%--챠트 조합 생성 여부--%>
								g_remarkH = g_chart.remarkH;
								g_remarkB = g_chart.remarkB;

								$("#block"+g_hideId).css("display","none");
								$("#"+g_TabGubun).attr("class","menu_off");
								$("#textShow").hide();
								$("#btnShow").show();
								$("#stblUnit").show();
								$("#tailExplain").hide();

								<%--챠트조회 후 조회한 경우--%>
								if(g_chartActive == "Y"){
									fn_fusionChartCtrl();
								}
								<%--
									scrollLeft추가 및 scrolltop 추가
									정렬시에만 scrollLeft 사용
								--%>
								if($("#ordColIdx").val() != ""){
									$("#htmlGrid").animate({scrollLeft:scrollLeft});
								}
								$("#htmlGrid").scrollTop(0);

								$("#htmlGrid").html(g_htmlGrid);

								g_cmmt = g_cmmt.replace(/null/g, "");

								if(g_cmmt != ""){ //2020.10.13 추가
									$("#cmmtAll").html(g_cmmt);
									$("#btn002").css("display","");
								}

								<%--
								   2016.02.16 통계표 상단 틀고정 시작
								--%>
								$("#ThtmlGrid").html(g_ThtmlGrid);

								$("#mainTableT tr:last th").css("border-bottom", "none");

 								if(g_chartActive == "Y"){	<%-- 차트조회일 경우는 통계표 상단 틀고정부분을 보여주지 않기 --%>
 									$("#ThtmlGrid").css("display","none");
								}else{
									adjustThtmlGrid(); <%-- ThtmlGrid 위치조정 --%>
								}
 								<%-- 통계표 상단 틀고정 끝 --%>

								<%--통계표 조회후 class속성 변경--%>
								$("#changeAttribute").attr("class","cont_line");
								$("#popMode").css("display","block");
								g_searchGridFlag = true;
								g_flag = true;									<%--tab메뉴에서 분류선택-> 상세설정-> 분석시작 그리드 조회성공-> tab 다시 선택시 g_flag초기화--%>

								fn_progressBar('hide');							<%--계층별 컬럼보기 시 추가메시지 출력, 분석명칭 추가--%>

								<%--
									OLAP_STL 첫번째 자리값이 C일 경우 차트먼저 보여줌 2014.04.17 - 김경호
									처음 조회 일때만 초기조회 조건을 보고 차트우선인지 판별 2014.06.19 - 김경호
								--%>
								if( $("#first_open").val() == ""){
									$("#first_open").val("Y");

									if( olapStl == "C"){
										fn_fusionChartCtrl();
									}
								}

								if(data[6] == "Y"){
									$("#showCmmtAll").show();
								}else{
									$("#showCmmtAll").hide();
								}

								if(data[5] != null && data[5] != ""){
									$("#analText").val(data[5]);
									var analText = data[5].replace("<", "").replace(">", "");
									var analImg = "<img src='images/ico_arrow_red.gif' alt=''/>";
									var analTitle = "<pivot:msg code="text.analysis.title"/>";

									if($("#analType").val() == "CHG_RATE"){
										analText += "(%)";
									}

									<%-- 2015.4.13 구성비 기준자료 선택안함 으로 분석시 상단 문구 표시--%>
									if(form.noSelect.value == "noSelect"){
										$("#analysisText").html(analImg + " " + analTitle + " : " + "<pivot:msg code="ui.label.assay.noselect"/>");
									}else{
										$("#analysisText").html(analImg + " " + analTitle + " : " + analText);
										$("#analysisText").attr("title", analText);
									}
									if(g_chartActive != "Y"){ <%-- 2020.09.14 분석명이 단위 아래로 내려가면서 추가해줌, 차트조회가 아닐때만 적용 --%>
										adjustThtmlGrid();
										$("#htmlGrid").css("height","585px");
									}
								}else{
									$("#analysisText").html("");
									$("#analText").val("");
									if(g_chartActive != "Y"){ <%-- 2020.09.14 분석명이 단위 아래로 내려가면서 추가해줌, 차트조회가 아닐때만 적용 --%>
										adjustThtmlGrid();
										$("#htmlGrid").css("height","606px");
									}
								}

								form.logSeq.value=data[4]; <%-- StatHtmlController 387 라인에서 추가된 값 --%>
								form.usePivot.value = "N";
								<%--부가기능설정 flag 값 초기화--%>
								form.isChangedTableType.value = "N";
								form.isChangedPeriodCo.value = "N";
								form.isChangedPrdSort.value = "N";
								form.useAddFuncLog.value = "";

								if(form.doAnal.value == "Y"){
									var fileType = $(":radio[name='downGridFileType']:checked").val();
									if(fileType == "sdmx"){
										$(":radio[name='downGridFileType']:radio[value='excel']").attr("checked", true);
										fn_clickFileType();
									}
									<%--분석인 경우 sdmx 비활성화--%>
									$(":radio[name='downGridFileType']:radio[value='sdmx']").attr("disabled", true);
									$(".downList ul li:last span.sdmxDown").addClass("notFunctext");
									$(".downList ul li:last span.flex").addClass("notFunctext");
								}else{
									$(":radio[name='downGridFileType']:radio[value='sdmx']").removeAttr("disabled");
									$(".downList ul li:last span.sdmxDown").removeClass("notFunctext");
								}

								<%-- 정렬이미지 변경 --%>
								if( $(":radio[name=prdSort]:checked").val() == "asc"){
									$("#sortImg").attr("src", "images/ico_time_up.png" );
									$("#sortImg2").attr("src", "images/ico_time_down2.png" );
								}else{
									$("#sortImg").attr("src", "images/ico_time_up2.png" );
									$("#sortImg2").attr("src", "images/ico_time_down.png" );
								}

								<%--
									부모 아이콘 원래대로 변경
									처음 조회시 실행안됨
									g_parentIconArr length에 상관없이 .class repair 전부 변경
								--%>
								$("[class=repair]").each(function(index){
									$(this).attr("src","images/ico_folder.png");
									$(this).removeAttr("class");
								});

								g_parentIconArr = response.parentResult;

								if(g_parentIconArr!=null && g_parentIconArr.length >0){

									var isFirst = $("#isFirst").val();
									for(var i=0;i<g_parentIconArr.length; i++){
										var tmpId = g_parentIconArr[i].objVarId;

										var parentArr = g_parentIconArr[i].parentList;

										<c:forEach items="${statInfo.classInfoList}" varStatus="status" var="tabClass">
											var defaultClassId = "${statInfo.classInfoList[status.index].classId}";
											var classSn		   = "${status.count}";
											var classDepth	   = "${statInfo.classInfoList[status.index].depthLvl}";
											<c:if test="${tabClass.visible == true}">
												if(defaultClassId == tmpId){
													<%--맨 처음 조회 일 경우 1레벨만 변경 --%>
													if(isFirst != "N"){
														$('#classList'+classSn+"_1 li").each(function(index){

															var parentId = $(this).find('input').val().split("=")[0];	<%-- 2018.06.14 선택안됨으로 인해 수정 - 김경호 --%>
															if($.inArray(parentId,parentArr) > -1){
																$(this).find('img').attr("src","images/ico_fd_chk_blue1.png");
																$(this).find('img').attr("class","repair");
															}
														});
													}else{
														<%--마지막 depth레벨 보다 1작은 레벨까지 --%>
														var parentDepth = classDepth-1;
														for(var ii=1; ii<=parentDepth; ii++){
															$('#classList'+classSn+"_"+ii+" li").each(function(index){
																var leaf = $(this).find('input:last').val();
																if(leaf == 0){
																	var parentId = $(this).find('input').val().split("=")[0];	<%-- 2018.06.14 선택안됨으로 인해 수정 - 김경호 --%>
																	if($.inArray(parentId,parentArr) > -1){
																		$(this).find('img').attr("src","images/ico_fd_chk_blue1.png");
																		$(this).find('img').attr("class","repair");
																	}else{
																		$(this).find('img').attr("src","images/ico_folder.png");
																	}
																}else{
																	$(this).find('img').attr("src","images/ico_doc.png");
																}
															});
														}
													}
												}
											</c:if>
										</c:forEach>
									}
								}else{


									<c:forEach items="${statInfo.classInfoList}" varStatus="status" var="tabClass">
									var defaultClassId = "${statInfo.classInfoList[status.index].classId}";
									var classSn		   = "${status.count}";
									var classDepth	   = "${statInfo.classInfoList[status.index].depthLvl}";
									<c:if test="${tabClass.visible == true}">
										if(defaultClassId == tmpId){
											<%--맨 처음 조회 일 경우 --%>
											if(isFirst != "N"){
												$('#classList'+classSn+"_1 li").each(function(index){

													var parentId = $(this).find('input').val().split("=")[0];	<%-- 2018.06.14 선택안됨으로 인해 수정 - 김경호 --%>
													if($.inArray(parentId,parentArr) > -1){
														$(this).find('img').attr("src","images/ico_fd_chk_blue1.png");
														$(this).find('img').attr("class","repair");
													}
												});
											}else{
												<%-- 마지막 depth레벨 보다 1작은 레벨까지 --%>
												var parentDepth = classDepth-1;
												for(var ii=1; ii<=parentDepth; ii++){

													$('#classList'+classSn+"_"+ii+" li").each(function(index){
														var leaf = $(this).find('input:last').val();
														if(leaf == 0){

															var parentId = $(this).find('input').val().split("=")[0];	<%-- 2018.06.14 선택안됨으로 인해 수정 - 김경호 --%>
															if($.inArray(parentId,parentArr) > -1){
																$(this).find('img').attr("src","images/ico_fd_chk_blue1.png");
																$(this).find('img').attr("class","repair");
															}else{
																$(this).find('img').attr("src","images/ico_folder.png");
															}
														}else{
															<%-- value="on" 나오는거는 1레벨 선택후 -> 2레벨 첫번째 li에 1레벨 선택한 체크박스 li세팅하므로 --%>
															$(this).find('img').attr("src","images/ico_doc.png");
														}
													});
												}
											}
										}
									</c:if>
									</c:forEach>
								}

								form.isFirst.value="N";

							}else{
								<%--메세지 처리 해줄려면--%>
							}
						}
						//2020.08.20 선택 로우 색상 변경
						$("#mainTable").children("tbody").children("tr").click(rowClick);
					},
					error : function(error){
						fn_progressBar('hide');
						alert( "<pivot:msg code="fail.common.msg"/>" );
					}
				});
			}
		}

		function fn_searchCond(){
			var makeJson  = new Array;
			var obj ={};
			var strPrd="";

			for(var i=0;i<g_result.length; i++){							<%--주기 갯수--%>
				var inFlag=0;
				$('[name=timeChk'+g_result[i]+']').each(function(index){ <%--2014.03.31 시점 내림차순조회--%>
 					if($(this).is(":checked")){
 						if(inFlag == 0){ <%--체크가 되었고 inFlag가 0일경우 조회하려고하는 시점이 있고 해당 주기에 대해서 처음이다...그럼 주기를 셋팅하자...2017-08-10 김경호 --%>
 							strPrd+=g_result[i]+","; 			<%--맨 처음 년도세팅오류--%>
 						}

						inFlag++;
						strPrd+=$(this).val()+",";
					}
				});

				<%-- 선택된 시점이 한개도 없는데 해당 주기의 체크박스는 체크가 되어있다...그럼 해당 주기의 체크박스를 체크해제해주자... 2017-08-10 김경호 --%>
				if(inFlag == 0 && $("input:checkbox[id='check"+g_result[i]+"']").is(":checked")){
					$("input:checkbox[id='check"+g_result[i]+"']").prop('checked', false);
					fn_Headenable(g_result[i]);
				}

				if(inFlag >0){
					strPrd+="@";
				}
			}
			<%--시점 끝--%>

			obj.targetId = "PRD";
			obj.targetValue ="";
			obj.prdValue = strPrd;
			makeJson.push(obj);

			<%--항목 세팅--%>
			$('[name=itemChkLi]').each(function(index){
				if($(this).is(":checked")){
					obj={};
						obj.targetId ="ITM_ID";
						obj.targetValue = ($(this).val());
						obj.prdValue = "";
						makeJson.push(obj);
				}
			});

			<c:forEach items="${statInfo.classInfoList}" var="tab" varStatus="status">
				var tempClassId = "${tab.classId}";
				var ov_cnt ="${status.count}";
				for(var i=0; i<g_defaultClassArr.length; i++){
					if(g_defaultClassArr[i].objVarId == tempClassId){
						var ov_arr = new Array;
						ov_arr = g_defaultClassArr[i].data;
						$.each(ov_arr,function(index,item){
							obj={};
							obj.targetId ="OV_L"+ov_cnt+"_ID";
							obj.targetValue = item;
							obj.prdValue="";
							makeJson.push(obj);
						});
					}
				}
			</c:forEach>
			<%--분류 끝--%>

 			form.fieldList.value= JSON.stringify(makeJson);
		}
		<%--
		/************************************************************************
		함수명 : fn_disPlay
		설   명 : 탭메뉴 클릭시 show,hide 컨트롤   g_flag 초기값은 true
		인   자 : 1.tabGubun = 항목/분류/시점 구분값
				 2.Cnt		= 탭메뉴 순번
		 ************************************************************************/
		 --%>
		function fn_disPlay(tabGubun,Cnt){
			<%-- 챠트 범례 초기화--%>
			fn_chartDisable();

			var cntLi;
			<%--g_charItmCo 1일때 항목탭 안보임 li순번 다시 세팅--%>
			if(g_charItmCo == 1){
				cntLi = Cnt-1;
			}else{
				cntLi = Cnt;
			}

			if(g_flag){
				$("#btnShow").hide();
				$("#stblUnit").hide();
				$("#textShow").show();
				<%--시점에서는 안보여줌--%>
				if(tabGubun != "tabTimeText" && tabGubun != "tabItemText"){
					$("#tailExplain").show();
				}else{
					$("#tailExplain").hide();
				}

 				$("#"+g_TabGubun).attr("class","menu_off");
				$("#"+tabGubun).attr("class","menu_on");
				$("#block"+g_hideId).css("display","none");
				$("#popMode").css("display","none");

				<%--2020.09.14 텍스트 버튼으로 변경되면서 생기는 문제 때문에 ;;--%>
				if($("#analysisText").height() > 0){
					$("#popMode").css("height","585px");
				}else{
					$("#popMode").css("height","604px");
				}

				$("#block"+Cnt).css("display","block");
				<%--체인지 class속성--%>
				$("#changeAttribute").attr("class","cont_lay");
				g_flag= false;
			}else{
				var menuClickFlag = $("#"+tabGubun).attr("class");
				if(menuClickFlag == "menu_on"){
					$("#textShow").hide();
					$("#btnShow").show();
					$("#stblUnit").show();
					$("#tailExplain").hide();
					$("#block"+g_hideId).css("display","none");
					<%--챠트가 활성화 되어 있으면// popMode 높이 재설정--%>
					if(g_chartActive == "Y"){
						$("#popMode").css("height","604px");
					}

					<%--2020.09.14 텍스트 버튼으로 변경되면서 생기는 문제 때문에 ;;--%>
					if($("#analysisText").height() > 0){
						$("#popMode").css("height","585px");
					}else{
						$("#popMode").css("height","604px");
					}

					$("#popMode").css("display","block");
					$("#changeAttribute").attr("class","cont_line");
					$("#Divchart").css("height","0px");

				}else{
					$("#block"+g_hideId).css("display","none");
					$("#block"+Cnt).css("display","block");
					$("#"+tabGubun).attr("class","menu_on");
					$("#popMode").css("display","none");

					if(tabGubun != "tabTimeText" && tabGubun != "tabItemText"){
						$("#tailExplain").show();
					}else{
						$("#tailExplain").hide();
					}

					if(g_searchGridFlag == false){
						$("#block"+g_hideId).css("display","none");
					}
					<%--통계표 조회후 속성 변경--%>
					$("#changeAttribute").attr("class","cont_lay");
				}

				$("#"+g_TabGubun).attr("class","menu_off");
				g_flag = true;
			}
			g_searchGridFlag = false;
			g_hideId = Cnt;						<%--hideId 세팅--%>
			g_TabGubun = tabGubun;

			if(g_chartActive == "Y"){
				$("#Divchart").css("visibility","hidden");
				$("#Chartcontent").css("display","none");
			}

			<%--챠트조회후 ->화면분할-> 다시 탭메뉴 클릭한 경우 챠트 초기화--%>

			<%--chart 분할상태면 htmlGrid 높이 지정--%>
			$("#popMode").css("top","30px");
			$("#htmlGrid").css("height", $("#popMode").height()-2);

			$("#htmlGrid").css("border","1px solid #b1b1b1");
		}


		<%--
		/************************************************************************
		함수명 : fn_selectAll
		설   명 : 조회된 목록에 있는 체크박스를 전체선택/전체해제 시켜주는 함수
				 fn_classLvlChk 호출
		인   자 : 1.objVarId = 탭메뉴의 obj_var_id
				 2.chkPosition = 전체선택 실행을 위한 checkbox Id
				 3.chkLi = 선택될 checkbox의 name
		 ************************************************************************/
		 --%>
		function fn_selectAll(objVarId,chkPosition,chkLi){
			var selectType ="more";
			var chkstatus = $("input:checkbox[id=classChk"+chkPosition+"]").is(":checked");
			var AllClassLvl = chkPosition.split("_")[1];					<%--선택된 레벨값..--%>
			var classSn  	= chkPosition.split("_")[0];

			var classLvlAllStatus = $("#classLvlAllChk"+classSn+"_"+AllClassLvl).is(":checked");
			if(classLvlAllStatus){
				if(!chkstatus){
					$("#classLvlAllChk"+classSn+"_"+AllClassLvl).prop("checked",false);
				}
			}
			var tempArr = new Array;
			var classSelectCnt = $("[name="+chkLi+"]:checked").size();		<%--분류레벨에 세팅된 체크된 갯수--%>
			var	levelTotalCnt=0;											<%--전체 선택을 누른 레벨의 총 갯수 1레벨의 총갯수...2레벨의 총갯수--%>
			var removeArr = new Array;
			var cloneArr = new Array;
			var nonChkArr = new Array;
			$("[name="+chkLi+"]").each(function(index){
				if(chkstatus){
					if($(this).is(":checked") == false){
						nonChkArr.push($(this).val().split('=')[0]);
					}
					tempArr.push($(this).val().split('=')[0]);
				}else{
					removeArr.push($(this).val().split('=')[0]);
				}
				levelTotalCnt++;
			});

			if(chkstatus){
				$("input:checkbox[name="+chkLi+"]").prop("checked",true);
				levelTotalCnt = levelTotalCnt - classSelectCnt;
			}else{
				$("input:checkbox[name="+chkLi+"]").prop("checked",false);
				levelTotalCnt = -levelTotalCnt;
			}

			fn_tabTextView(objVarId,selectType,levelTotalCnt);
			fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);

			var lvlObj ={};
			var tempAllCnt=0;

			$.each(g_defaultClassArr,function(index,item){
				if(item.objVarId == objVarId && item.classType == AllClassLvl){
					tempAllCnt++;
					if(chkstatus){
						lvlObj.objVarId  = objVarId;
						lvlObj.classType = AllClassLvl;
						cloneArr = item.data;
						$.each(nonChkArr,function(index,item){
							cloneArr.push(item);
						});
						lvlObj.data = cloneArr;
						lvlObj.classLvlCnt = cloneArr.length;
					}else{								//선택된 레벨 전체해제
						lvlObj.objVarId  = objVarId;
						lvlObj.classType = AllClassLvl;
						cloneArr = item.data;
						$.each(removeArr,function(index,item){
							cloneArr.splice($.inArray(item,cloneArr),1);
						});
						lvlObj.data = cloneArr;
						lvlObj.classLvlCnt = cloneArr.length;
					}
					g_defaultClassArr.splice(index,1,lvlObj);
					var classLvlCnt = lvlObj.classLvlCnt;
					fn_oneLankLevel(objVarId,classSn,AllClassLvl,classLvlCnt);
				}

			});

			if(tempAllCnt==0 && chkstatus == true){
				lvlObj.objVarId  = objVarId;
				lvlObj.classType = AllClassLvl;
				lvlObj.data = tempArr;
				lvlObj.classLvlCnt = tempArr.length;
				g_defaultClassArr.push(lvlObj);
			}
		}

		function fn_selectItemAll(rangeType){
			var chkstatus = $("input:checkbox[id=itemChk]").is(":checked");
			if(rangeType == "Y"){
				$("input:checkbox[id=itemChk]").prop("checked",false);
				$("input:checkbox[name=itemChkLi]").prop("checked",false);
			}else{
				if(chkstatus){
					$("input:checkbox[name=itemChkLi]").prop("checked",true);
				}else{
					$("input:checkbox[name=itemChkLi]").prop("checked",false);
				}
			}
			var itemChkAllCnt = $('[name=itemChkLi]:checked').size();

			g_itmCnt = itemChkAllCnt;
			fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);
		}

		<%--
		/************************************************************************
		함수명 : fn_searchClass
		설   명 : leaf=0인것만 이벤트 발생
		인   자 : 1. itmId = 항목코드
		         2. objVarId= 대상항목분류코드
		         3. lvl=선택된 레벨(div순서가 레벨)
		         4. classSn= 탭메뉴에서 분류 순서
		         5. depthLvl= 분류에 들어있는  MaxLevel 분류리스트 조회후 삭제처리 계산
		         6. liSn li순번
		사용법 : fn_searchClass('${selection.itmId}','${selection.objVarId}',${i},,${status.index},${statInfo.classInfoList[status.index-1].depthLvl},liSn)
		 ************************************************************************/
		 --%>
		function fn_searchClass(itmId,objVarId,lvl,classSn,depthLvl,liSn){
			var selectLiNm = "classChkLi"+classSn+"_"+lvl;
			var selectNextLvl = "classChkLi"+classSn+"_"+lvl+1;
			<%--input name으로 접근후 -> parent인 li 찾기 -> li 안에 있는 a 찾고 bold 변경--%>
			var litext =""; <%-- 1레벨 클릭한 li text명 담을 변수--%>
			$('[name='+selectLiNm+']').each(function(index){
				var  liObj = $(this).parent();
				if(index == liSn){
					$(liObj).find('a').css("font-weight","bold");
					litext = $(liObj).find('a').text();
				}else{
					$(liObj).find('a').css("font-weight","");
				}
			});

			var compareArr = new Array;
			<%--lvl은 현재 선택된 레벨 위치..결과값은lvl+1--%>
			var drawLvl;
			drawLvl = lvl+1;
			fn_progressBar("show");
			$.ajax({
				dataType : 'json',
				type : 'POST',
				url : "<%=request.getContextPath()%>/classDivSelect.do",
				dataType : "json",
				data : {
					'itmId'   	 : itmId,
					'lvl'     	 : lvl,
					'objVarId'	 : objVarId,
					'searchCondition' : "J",
					'orgId'      : "${ParamInfo.orgId}",
					'tblId'	     : "${ParamInfo.tblId}",
					'dataOpt'	 : "${ParamInfo.dataOpt}",
					'language'	 : "${ParamInfo.language}",
					'pub'		 : "${ParamInfo.pub}",
					'dbUser'	 : "${ParamInfo.dbUser}",
					'st'		 : "${ParamInfo.st}"
				},
				success : function(response,status){
					var data = response.result ;
					var list = "";
					var autoChkCnt=0;						<%--전체선택을 클릭하고 다른 레벨 조회 후 그전 레벨로 다시 조회 할 경우..전체선택박스에 자동체크비교 카운트--%>

					if(status ='success'){
						if(data!=null && data.length >0){
							<%-- 변경아이콘 세팅 Arr--%>
							var parentArr;
							if(g_parentIconArr!=null && g_parentIconArr.length >0){
								for(var i=0;i<g_parentIconArr.length; i++){
									var tmpId = g_parentIconArr[i].objVarId;
									if(tmpId == objVarId){
										parentArr = g_parentIconArr[i].parentList;
									}
								}
							}

							<%--비교대상 cmpareArr 세팅--%>
							for(var j=0; j<g_defaultClassArr.length; j++){
								if(g_defaultClassArr[j].classType == drawLvl && g_defaultClassArr[j].objVarId == objVarId){
									compareArr = g_defaultClassArr[j].data;
								}
							}

							<%--2014.03.31 list출력된 전체선택을 위해 추가 --%>
							list+="<li>";
							list+= "<input id='classChk"+classSn+"_"+drawLvl+"' type=\"checkbox\" onclick=\"fn_selectAll(\'"+objVarId+"\','"+classSn+"_"+drawLvl+"','classChkLi"+classSn+"_"+drawLvl+"')\" />"+litext+"\n";
							list+="</li>";

							for(var i=0;i<data.length;i++){

								var rItmId = data[i].itmId;
								var rObjVarId = data[i].objVarId;
								var rUpItmId = data[i].upItmId;
								var rLeaf = data[i].leaf;
								var chkType = "";

								<%--체크상태값 세팅--%>
								if($.inArray(rItmId,compareArr) > -1){
									chkType = "checked";
									autoChkCnt++;
								}else{
									chkType = "";
								}

								list+= "<li class=\"liOther\">\n";
 								list+= "<input type=\"checkbox\" id=\"classChkLi"+classSn+"_"+drawLvl+"_"+rItmId+"\"  name=\"classChkLi"+classSn+"_"+drawLvl+"\" value=\'"+rItmId+"="+rUpItmId+"\' onclick=\"fn_classLvlChk(\'"+rObjVarId+"',"+classSn+","+drawLvl+",\'"+rItmId+"\',this);\" "+chkType+">\n";
 								list+= "<input type=\"hidden\" name=\"defaultFolder\" value=\'"+rLeaf+"\'>";

 								if(data[i].leaf == 0){
									if($.inArray(rItmId,parentArr) > -1){
										list+= "<img class='repair' src='images/ico_fd_chk_blue1.png' alt='폴더'/>\n";
									}else{
										list+= "<img src='images/ico_folder.png' alt='폴더'/>\n";
									}
									list+= "<a style=\"cursor:pointer;\" href=\"#\" onclick=\"fn_searchClass(\'"+rItmId+"\',\'"+rObjVarId+"\',"+drawLvl+","+classSn+","+depthLvl+","+i+");return false;\">";
									list+= data[i].scrKor+"</a>";
								}else{
									list+= "<img src='images/ico_doc.png' alt='문서'/>\n";
									list+= ''+data[i].scrKor+'';
								}
								list+= '</li>\n';

							}
							<%--
							//마지막 depth레벨까지 리스트 호출 후 다른 레벨을 선택시 화면에서 지우기
							//화면에 리스트가 없을 경우 전체선택박스 비활성화
							--%>
							for(var i=drawLvl;i<=depthLvl; i++){
								$("#itmList"+classSn+" ul:eq("+i+")").empty();
								$("#classChk"+classSn+"_"+i).attr("disabled",true);
							}

							$('#classList'+classSn+"_"+drawLvl).html(list);

							<%--전체선택 체크박스 활성화--%>
							$("#classChk"+classSn+"_"+drawLvl).removeAttr("disabled");

							<%--선택이 모두 되어있었다면..전체선택 체크상태--%>
 							if(data.length == autoChkCnt){
								$("#classChk"+classSn+"_"+drawLvl).attr("checked",true);
 							}else{
 								$("#classChk"+classSn+"_"+drawLvl).attr("checked",false);
 							}
						}else{
							<%--메세지 처리 해줄려면--%>
						}
					}
					fn_progressBar("hide");
				},
				error : function(error){
					fn_progressBar("hide");
					alert( "<pivot:msg code="fail.common.msg"/>" );
				}
		});
		}

		<%--
		/************************************************************************
		함수명 : fn_generatePrdDe
		설   명 : 주기에 해당하는 시점 표시
		인   자 : prdDe = 날짜 type = 주기(D,T,M,B,Q,H,Y,F)
		 ************************************************************************/
		 --%>
		function fn_generatePrdDe(prdDe,period){
			var retStr = "";

			if(period=="M" || period =="B"){
				retStr = prdDe.substring(0,4)+"."+prdDe.substring(4);
			}else if(period=="Q"){
				retStr = prdDe.substring(0, 4) + " " + prdDe.substring(5) + "/4";
			}else if(period =="H"){
				retStr = prdDe.substring(0, 4) + " " + prdDe.substring(5) + "/2";
			}else if(period =="D"){
				retStr = prdDe.substring(0,4)+"."+prdDe.substring(4,6)+"."+prdDe.substring(6,8);
			}else{
				retStr = prdDe;
			}

			return retStr;
		}

		<%--
		/************************************************************************
		함수명 : fn_Headenable
		설   명 : 아래 fn_enable()의 확장형으로 메인의 시점탭에서 주기 체크박스를 클릭하면 체크된 시점갯수를 파악하여 전체 시점수에서 빼준다.
		인   자 : type = 주기(D,T,M,B,Q,H,Y,F)
		사용법 : fn_Headenable(type)
		 ************************************************************************/
		 --%>
		function fn_Headenable(type){
			var timeEnableChk = $("input:checkbox[id='check"+type+"']").is(":checked");

			if(timeEnableChk == false){
				var  minusTimeChk = $('[name=timeChk'+type+']:checked').size();
				g_timeCnt = g_timeCnt - minusTimeChk;
				fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);
			}
			fn_enable(type);
		}

		<%--
		/************************************************************************
		함수명 : fn_enable
		설   명 : 주기별 체크박스 선택시 콤보박스 활성화/비활성화
		인   자 : type = 주기(D,T,M,B,Q,H,Y,F)
		사용법 : fn_searchPeriod(searchType)
		 ************************************************************************/
		 --%>
		function fn_enable(type){

			var timeEnableChk = $("input:checkbox[id='check"+type+"']").is(":checked");
			<%--
			//주기별 div->바로 밑에 inputBox 선택여부에 따라 콤보박스 및 조회버튼 활성화/비활성화
			//조회버튼/주기별 시점리스트 활성화 비활성화--%>
			if(timeEnableChk == true){
				$("#time"+type).find('select').removeAttr("disabled");
			}else{
				$("#time"+type).find('select').attr("disabled",true);
				$("#searchPeriod"+type).children().remove();									<%--리스트정보 지우기--%>
				$("#divSelectAll"+type).css("display", "none");	<%--조회버튼 비활성화 되면서 해당 주기의 전체선택 안보이게...--%>
			}

			<%--일단 M,Q하나만 선택되었을 때 년월 분기표 selectBox option세팅--%>
			var headCnt = $('[name=headCheck]:checked').size();
			var headType="";
			if(headCnt == 1){
				$('[name=headCheck]').each(function(index){										<%--세팅된 시점Div에서 checked 된 M,Q 찾기--%>
					if($(this).is(":checked")){
						 g_headType = $(this).attr("value");
						 headType = $(this).attr("value");
						<%--년월 분기표 세팅--%>
						 if(g_headType == "M" || g_headType =="Q"){
							if("${ParamInfo.tableType}" == 'perYear'){
							 	$("#tableType option:eq(2)").after("<option value='perYear' selected='selected'><pivot:msg code="ui.combo.perYear"/></option>");
							}else{
								$("#tableType option:eq(2)").after("<option value='perYear'><pivot:msg code="ui.combo.perYear"/></option>");
							}
						}
						<%--분석 이미지 컨트롤--%>
						 if(headType !="D" && headType !="T" && headType !="B"){	<%--자료분석을 위한 시점 선택상태 주기는 1개만 선택해야하며 Q,Y,H,M,F 일 경우만--%>
								g_assayYn="Y";
							}else{
								g_assayYn="N";
							}
					}
				});
			}else{
				var tempId = $("#tableType option:eq(3)").attr("value");
				if(tempId == "perYear"){
					$("#tableType option:eq(3)").remove();
				}

				g_assayYn="N";
			}

			fn_analysisChk(g_analyzable);

			if(timeEnableChk == true){
				fn_searchPeriod(type);	<%--2017-08-16 주기선택시 시점 자동 조회--%>
			}
		}


		function fn_analysisChk(g_analyzable){
			if(g_assayYn == "N"){
				if(g_analyzable){
					$("#ico_analysis").addClass("off");
				}
			}else{
				if(g_analyzable == "true"){
					if(g_multiplication > g_maxCell){									<%--단일 시점선택 분석이 가능해도 조합 셀수 체크해야함--%>
						$("#ico_analysis").addClass("off");
					}else{
						$("#ico_analysis").removeClass("off");
					}
				}
			}
		}

		function setCmmtPosition(event){
			popupCmmtY = event.clientY;
			popupX = event.clientX;
		}


		<%--
		/************************************************************************
		함수명 : fn_searchPeriod
		설   명 : 셀렉트박스 2개에서 선택된 시점 정보로 주기에 해당하는 시점리스트 세팅
		          셀렉트박스 2개이므로 first,last로 처리
		          주기별 시점조회 공통
		인   자 : searchType = 주기(D,T,M,B,Q,H,Y,F)
		사용법 : fn_searchPeriod(searchType)
		 ************************************************************************/
		 --%>
		function fn_searchPeriod(searchType,rangeMode,p_rangeTimeArr){
			<%--현재 체크된 갯수--%>
			var  minusTimeChk = $('[name=timeChk'+searchType+']:checked').size();

			var startPrd =	$("#time"+searchType).find('select:first').val();
			var endPrd	 =  $("#time"+searchType).find('select:last').val();

			if(startPrd > endPrd){
				alert("<pivot:msg code="fail.reset.msg"/>");
				return;
			}
			$.ajax({
					dataType : 'json',
					type : 'POST',
					url : "<%=request.getContextPath()%>/periodDivSelect.do",
					async : false,
					data : {
						'startPrd'   : startPrd,
						'endPrd'     : endPrd,
						'orgId'      : "${ParamInfo.orgId}",
						'tblId'	     : "${ParamInfo.tblId}",
						'prdSe'	     : searchType,
						'dataOpt'    : "${ParamInfo.dataOpt}",
						'language'   : "${ParamInfo.language}",
						'pub'		 : "${ParamInfo.pub}",
						'dbUser'	 : "${ParamInfo.dbUser}",
						'st'		 : "${ParamInfo.st}",
						'inheritYn'	 : "${ParamInfo.inheritYn}",
						'originOrgId': "${ParamInfo.originOrgId}",
						'originTblId': "${ParamInfo.originTblId}"
					},
					success : function(response,status){
						var data = response.result ;
						var list = "";
						var chkStatus="";
						<%--시점리스트 html만들기--%>
						if(status ='success'){
							data.reverse();						<%--정렬보다 빠름..전제조건은 Data가 뽑힐때 order by 정확하게 되어 있어야 함--%>
							if(data!=null && data.length >0){
								for(var i=0;i<data.length;i++){
									var prdDe = data[i].prdDe;
									<%--조회범위 상세설정 세팅--%>
									if(rangeMode == "Y"){
										<%--조회범위 상세설정에서 세팅된 시점만 체크상태--%>
										var rangeTimeChk = $.inArray(prdDe,p_rangeTimeArr);
										if(rangeTimeChk > -1){
											chkStatus = "checked";

										}else{
											chkStatus ="";
										}
									}else{
										<%--시점 조회 후 체크상태 true--%>
										chkStatus = "checked";
									}
									<%--상세설정 끝--%>
									list+= "<li>\n";
									list+= "<input type=\"checkbox\" name=\"timeChk"+searchType+"\" onclick=\"fn_timeCountChk(this,\'"+searchType+"\');\" value='"+prdDe+"' title='"+prdDe+"' "+chkStatus+">\n";
									list+= ""+fn_generatePrdDe(prdDe,searchType)+"";
									list+= "</li>\n";
								}

								<%--ie7인 경우 더미 li 생성--%>
								if(g_browserVersion == "7.0"){
									list+="<input type=\"checkbox\" name='ie7bug' style='visibility:hidden'/>";
								}
								$('#searchPeriod'+searchType).html(list);

								<%--화면에 세팅된 시점 갯수--%>
								if(rangeMode == "Y"){
									g_timeCnt = g_rangeTimeCnt;
								}else{
									g_timeCnt = g_timeCnt - minusTimeChk + data.length;
								}
								fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);

								$("#selectAll"+searchType).prop("checked", true);	<%--조회할 시점들이 있으면 전체선택 체크박스의 체크상태 활성화--%>
								$("#divSelectAll"+searchType).css("display", "block");	<%--조회할 시점들이 있으면 해당 주기의 전체선택 보이게...--%>
							}else{
								<%--메세지 처리 해줄려면--%>
							}
						}
					},
					error : function(error){
						alert( "<pivot:msg code="fail.common.msg"/>" );
					}
			});
		}

		<%--모달 팝업 컨트롤--%>
		function popupControl(divName, view, modal, y, x){
			if(view == 'show'){

				<%--스크랩 저장시 체크--%>
				if(divName == "pop_myscrap"){
					<%--2020.09.14 이미지 버튼에서 텍스트 버튼으로 변경되면서 클래스 유무로 팝업 컨트롤 --%>
					if($("#ico_myscrap").hasClass("off") == true){
						return;
					}
					$("#myscrapPopContent, #ifr_myscrap").css("visibility", "hidden");
					<c:if test="${empId eq null || empty empId}">
						alert("<pivot:msg code="fail.login.msg"/>");
						return;
					</c:if>
				}else if(divName =="pop_selectAll"){
					$("#ifr_selectAll, #ifrSelectAll").css("visibility", "hidden");
				}else if(divName =="pop_detailfunc"){
					$("#ifr_pop_selectAll2, #ifrSearchDetail").css("visibility", "hidden");
				}else if(divName =="pop_assay"){
					<%--2020.09.14 이미지 버튼에서 텍스트 버튼으로 변경되면서 클래스 유무로 팝업 컨트롤 --%>
					if($("#ico_analysis").hasClass("off") == true){
						return;
					}
					$("#ifr_assay,#ifrAssayInfo").css("visibility","hidden");
				}else if(divName =="pop_relGrid"){
					$("#ifr_relationInfo,#ifrRelationInfo").css("visibility","hidden");
				}else if(divName =="pop_statGrid"){
					$("#ifr_statInfo,#ifrStatInfo").css("visibility","hidden");
				}else if(divName =="pop_cmmtInfoAll"){
					<%--주석전체보기시 로그 저장 추가--%>
					form.viewKind.value="6";
					form.viewSubKind.value="6_4";

					$.ajax({
						dataType : 'json',
						type : 'POST',
						async : false,
						url : "<%=request.getContextPath()%>/setOlapLogForNsoService.do",
						data : $("#ParamInfo").serialize()
					});

					<%--초기화 --%>
					form.viewSubKind.value="";
				}else if(divName == "pop_downgrid"){
					if($("#ico_download").hasClass("off") == true){
						return;
					}
					if(g_multiplication == 0){
						alert("<pivot:msg code="fail.selectNotSearch.msg"/>");
						if(form.doAnal.value =="Y"){
							form.doAnal.value ="N";
						}
						return;
					}
				}else if(divName =="pop_addfunc"){		<%--2020.09.14 이미지 버튼에서 텍스트 버튼으로 변경되면서 클래스 유무로 팝업 컨트롤 --%>
					if($("#ico_addfunc").hasClass("off") == true){
						return;
					}
				}else if(divName =='pop_pivotfunc'){
					if($("#ico_swap").hasClass("off") == true){
						return;
					}
				}

				if(x > 0){
					var mainTop = $("#popup_outer").position().top;
					var mainLeft = $("#popup_outer").position().left;
					$("#" + divName).css( "left", x - mainLeft + 5 );
					$("#" + divName).css( "top", y - mainTop + 5 );
				}else if(y > 0){
					var mainTop = $("#popup_outer").position().top;
					$("#" + divName).css( "top", y - mainTop + 20 );
				}else{
				}

				if(divName =="pop_selectAll"){
					fn_searchCond();
					form.selectAllFlag.value	= "Y";									<%--항목 분류 시점 선택정보 전체 보기--%>

					form.action="<%=request.getContextPath()%>/selectAll.do";
					form.target="ifrSelectAll";
					form.submit();
				}else if(divName == "pop_myscrap"){

					form.action="<%=request.getContextPath()%>/myscrapView.do";
					form.target="ifr_myscrap";
					form.submit();
				}else if(divName =="pop_detailfunc"){
					fn_searchCond();							 						<%--현재 선택정보 다시 세팅--%>
					form.selectTimeRangeCnt.value = g_timeCnt;							<%--시점 카운트 세팅--%>
					form.action="<%=request.getContextPath()%>/searchRangeDetail.do";
					$("#p_classAllChkYn").val("Y");
					$("#p_logicFlag").val("Y");				<%--fn 리턴함수 실행여부 flag --%>
					form.target="ifrSearchDetail";
					form.submit();
				}else if(divName =="pop_assay"){
					fn_searchCond();
					form.funcPrdSe.value = g_headType;									<%--선택된 단 한개의 주기--%>
					form.action="<%=request.getContextPath()%>/assayInfo.do";
					form.target="ifrAssayInfo";
					form.submit();
				}else if(divName =="pop_relGrid"){
					fn_searchCond();
					form.funcPrdSe.value = g_headType;									<%--관련통계표--%>
					form.action="<%=request.getContextPath()%>/relationInfo.do";
					form.target="ifrRelationInfo";
					form.submit();
				}else if(divName =="pop_statGrid"){
					fn_searchCond();
					form.funcPrdSe.value = g_headType;									<%--출처더보기--%>
					form.action="<%=request.getContextPath()%>/statInfo.do";
					form.target="ifrStatInfo";
					form.submit();
				}

				if(modal == 'modal'){
					$("#modal").css("visibility", "visible");
				}
				$("#popup_outer").css("visibility", "visible");
				$("#" + divName).css("display", "block");

				if(divName == "pop_cmmtInfoAll"){

					<%--2019.03.05 주석창의 제목이 길 경우 두줄로 보여지는 현상 방지 --%>
					var buff = g_tableNm;
					if(g_tableNm.length > 50){
						buff = g_tableNm.substring(0, 50) + "...";
					}

					$("#pop_cmmtInfoAll .bu_circle3").text(buff);
				}

				form.target = "_self";

			}else{
				if(modal == 'modal'){
					$("#modal").css("visibility", "hidden");
					if(divName =='pop_pivotfunc'){
						$("[name^=listBox] li").each(function(e){			<%--좌측 우측 ul->li 만큼 반복후  border 초기화.--%>
							$(this).css('border','1px solid #a3bad9');		<%--테두리 초기화--%>
						});
					}

					if(divName == "pop_addfunc"){
						$("#pop_levelExpr, #pop_parentLevel").css("display","none");
					}
					$("#popup_outer").css("visibility", "hidden");

				}
				<%--helpClose,helpClose2를 위해 로직분리--%>
				$("#" + divName).css("display", "none");
			}
		}

		<%--iframe열리고 나면 이 함수를 호출하여 보이도록 한다.--%>
		function callbackForIframe(divName){
			if(divName == "pop_myscrap"){
				$("#myscrapPopContent, #ifr_myscrap").css("visibility", "visible");
			}else if(divName =="pop_selectAll"){
				$("#ifr_selectAll, #ifrSelectAll").css("visibility", "visible");
			}else if(divName =="pop_detailfunc"){
				$("#ifr_SearchDetail,#ifrSearchDetail").css("visibility","visible");
			}
			else if(divName =="pop_assay"){
				$("#ifr_assay,#ifrAssayInfo").css("visibility","visible");
			}else if(divName =="pop_relGrid"){
				$("#ifr_relationInfo,#ifrRelationInfo").css("visibility","visible");
			}else if(divName =="pop_statGrid"){
				$("#ifr_statInfo,#ifrStatInfo").css("visibility","visible");
			}
		}

		<%--해당 주석보기 팝업div--%>
		function popupCmmt(param, view){
			if(view != 'hide'){
				view = 'show';
			}

			if(view == 'show'){
				$("#cmmtNo").html("");
				$("#cmmtTitle").html("");
				$("#cmmt").html("");

				var array = param.split("#");

				$.ajax({
					dataType : 'json',
					type : 'POST',
					url : "<%=request.getContextPath()%>/cmmtInfo.do",
					data : {
						'orgId'      : "${ParamInfo.orgId}",
						'tblId'	     : "${ParamInfo.tblId}",
						'cmmtSe'     : array[1],
						'objVarId' 	 : array[2],
						'itmId' 	 : array[3],
						'itmRcgnSn'  : array[4],
						'dataOpt'	 : "${ParamInfo.dataOpt}",
						'language'	 : "${ParamInfo.language}",
						'pub'		 : "${ParamInfo.pub}",
						'dbUser'	 : "${ParamInfo.dbUser}",
						'st'		 : "${ParamInfo.st}",
						'inheritYn'	 : "${ParamInfo.inheritYn}",
						'originOrgId': "${ParamInfo.originOrgId}",
						'originTblId': "${ParamInfo.originTblId}"
					},
					success : function(response,status){
						$("#cmmtNo").html(array[0] + ")");
						$("#cmmtTitle").html(response.title);

						$("#pop_cmmtInfo").css("height", "");

						var buff = response.result;
						buff = buff.replace(/null/g, "");

						$("#cmmt").html(buff);
						var cmmtDivHeight = $("#cmmt").css('height');
						cmmtDivHeight = Number(cmmtDivHeight.replace('px', ''));

						if(cmmtDivHeight > 450){
							cmmtDivHeight = 450;
							$('#pop_cmmtInfo').css('height', cmmtDivHeight + 'px');
							$('#cmmt_lay3').css('height', (cmmtDivHeight - 40) + 'px');
						}
					},error : function(request, status, error){
						alert("에러가 발생했습니다.");
					}
				});
			}

			popupControl('pop_cmmtInfo', view, 'modal', popupCmmtY);
		}

		var win_num = 0;
		function fn_newWindow(){
			win_num++;
			var target_nm = "${ParamInfo.tblId}"+win_num;
			var mode_buff = "${ParamInfo.mode}";

			window.open("", target_nm, "location=yes, status=no, directories=yes, menubar=yes, toolbar=yes, scrollbars=yes, resizable=yes");

			$("#new_win").val("Y");
			form.action = "${statInfo.url}${pageContext.request.contextPath}${statInfo.defaultAction}";
			form.target = target_nm;
			form.mode.value = "";
			form.method = "POST";
			form.submit();
			form.target = "";
			form.mode.value = mode_buff;
		}

		function openStatSno(url) {
			var s_status ;
			s_status = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=820,height=700";
			window.open(url,"statNso",s_status);
		}

		function openInquire(url) {
		    var s_status ;
			s_status = "location=yes,toolbar=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=1024,height=768";
			window.open( url,"inquire",s_status);
		}

		<c:if test="${statInfo.massYn == 'Y'}">
			<%--2013.12.27 대용량 파일 서비스(국문 서비스용)--%>
			function openMass(val) {

				var url = "http://${pageContext.request.serverName}/statisticsList/mass/mass_list.jsp?org_id=${ParamInfo.orgId}&tbl_id=${ParamInfo.tblId}&vw_cd=${ParamInfo.vwCd}&list_id=${ParamInfo.listId}&process=statHtml";

				if(val == 'E'){
					url = "http://${pageContext.request.serverName}/statisticsList/mass/mass_list_e.jsp?org_id=${ParamInfo.orgId}&tbl_id=${ParamInfo.tblId}&vw_cd=${ParamInfo.vwCd}&list_id=${ParamInfo.listId}&process=statHtml";
				}

			    var s_status ;
				s_status = "location=no,toolbar=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=646,height=570";

				window.open( url,"mass",s_status);
			}
		</c:if>

		<%--통계설명자료, 온라인간행물, 보도자료 클릭 시--%>
		function openNewWin(url, type){
			if(type != '0'){

				form.viewKind.value="6";
				form.viewSubKind.value=type;

				$.ajax({
					dataType : 'json',
					type : 'POST',
					async : false,
					url : "<%=request.getContextPath()%>/setOlapLogForNsoService.do",
					data : $("#ParamInfo").serialize()
				});

				<%--초기화--%>
				form.viewSubKind.value = "";
			}

			<%--
				2014.05.19 온라인간행물 URL을 등록할때 변수로 이미 인코딩된 값이 들어있고 스크립트로 던질때 인코딩된 값을 자동으로 또 인코딩해서
				새창열기시 제대로된 값이 나오지 않는 문제 수정 - 김경호
			--%>
			if(type == "6_2"){
				url = '${statInfo.statLinkInfo.pubLink}';
			}

			var s_status ;
			s_status = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=820,height=700";
			window.open( url,"_blank",s_status);
		}

		function fn_dataSearch(){
			<%--다시 검색할 경우 backGround 초기화--%>
			$('[class=value] input').each(function(index){
				$(this).parent().css('background-color','#ffffff');
				$(this).parent().css('font-weight','');
			});

			radioType = $('[name=findData]:checked').attr("id");

			if(radioType =="findData01"){
				var compValue = $("#compValue").val().replace(/[^0-9]/g,'');
				if(compValue == ""){
					alert("<pivot:msg code="fail.searchNum.msg"/>");
					$("#compValue").focus();
					return;
				}

				var mark =  $("#findOption option:selected").val();

				 switch(mark){
					case "1":
						$('[class=value] input').each(function(index){
							 mathData =$(this).val();
						if(mathData < parseInt(compValue)){
							$(this).parent().css('background-color','#F3F781');
							$(this).parent().css('font-weight','bold');
						}
						});
						break;
					case "2":
						$('[class=value] input').each(function(index){
							 mathData =$(this).val();
						if(mathData <= parseInt(compValue)){
							$(this).parent().css('background-color','#F3F781');
							$(this).parent().css('font-weight','bold');
						}
						});
						break;
					case "3":
						$('[class=value] input').each(function(index){
							 mathData =$(this).val();
						if(mathData == parseInt(compValue)){
							$(this).parent().css('background-color','#F3F781');
							$(this).parent().css('font-weight','bold');
						}
						});
						break;
					case "4":
						$('[class=value] input').each(function(index){
							 mathData =$(this).val();
						if(mathData >= parseInt(compValue)){
							$(this).parent().css('background-color','#F3F781');
							$(this).parent().css('font-weight','bold');
						}
						});
						break;
					case "5":
						$('[class=value] input').each(function(index){
							 mathData =$(this).val();
						if(mathData > parseInt(compValue)){
							$(this).parent().css('background-color','#F3F781');
							$(this).parent().css('font-weight','bold');
						}
						});
						break;
				}
			}else if(radioType =="findData02"){
				var compValue01 = $("#compValue01").val().replace(/[^0-9]/g,'');
				var compValue02 = $("#compValue02").val().replace(/[^0-9]/g,'');
				if(compValue01 ==""){
					alert("<pivot:msg code="fail.searchNum.msg"/>");
					$("#compValue01").focus();
					return;
				}else if(compValue02 ==""){
					alert("<pivot:msg code="fail.searchNum.msg"/>");
					$("#compValue02").focus();
					return;
				}

				<%--다시 검색할 경우 backGround 초기화--%>
				$('[class=value] input').each(function(index){
					mathData = $(this).val();
					if(parseInt(compValue01)<= mathData && mathData <= parseInt(compValue02)){
						$(this).parent().css('background-color','YELLOW');
						$(this).parent().css('font-weight','bold');
					}
				});
			}

			popupControl('pop_addfunc', 'hide', 'modal');
		}

		function fn_fusionChartCtrl(){
			if(g_chartMsg =="N"){
				alert( "<pivot:msg code="fail.chart.msg"/>" );

				<%--2015.11.20 남규옥 추가 시작 ::: 차트조회 후 재조회했을 경우 차트 닫기 --%>
				if(g_chartActive == "Y"){
					fn_chartClose();
				}
				<%--2015.11.20 남규옥 추가 끝 --%>
				return;
			}

			var dataOpt2 = $("#dataOpt2 option:selected").val();

			if( dataOpt2 == "en"){
				$("#searchImg1").attr("src","images/btn_tableSearch_en.gif");
			}else{
				$("#searchImg1").attr("src","images/btn_chartSearch.gif");
				<%--2015.12.16 차트조회시 버튼 팝업 차트조회로 나오도록 메세지 추가--%>
				$("#searchImg1").attr("title","<pivot:msg code='ui.label.chartSearch'/>");
			}

			form.viewKind.value="3";

			$.ajax({
				dataType : 'json',
				type : 'POST',
				url : "<%=request.getContextPath()%>/setOlapLogForNsoService.do",
				data : $("#ParamInfo").serialize()
			});

			$("#popMode").css("display","none");
			$("#popMode").css("height","0px");
			$("#htmlGrid").css("margin-top","0");
			$("#htmlGrid").css("height","0px");
			$("#Divchart").css("visibility","visible");
			$("#Divchart").css("height","356px");


 			if(g_timeCnt > 1){								<%--시점이 2개 이상인 경우 꺽은선챠트--%>
				fn_fusionChartExcute("msline");
 			}else{
 				$("#chartViewCnt").val(10);					<%--단일 시점인 경우 데이터10개 보여주기--%>
 				fn_fusionChartExcute("MSColumn3D");			<%--기본챠트--%>
 			}
 			g_chartActive ="Y";								<%--챠트 실행후 플래그 Y, N일경우 fn_chartClose실행해야함--%>

 			$("#gbox_tbl_data_view").css("height","231px");
 			$("#gview_tbl_data_view").css("height","180px");
 			$(".ui-jqgrid-bdiv").css("height","206px");

			$("#jqGrid").css("top","1px");
			$("#jqGrid").css("height","260px");
			$("#jqGrid").css("visibility","visible");
			$("#Legend").css("top","1px");

			if($("#analysisText").height() > 0){
				$("#Legend").css("height","230px");
			}else{
				$("#Legend").css("height","250px");
			}

			$("#Legend").css("visibility","visible");

			<%--2015.08.03 차트 조회시 아이콘 비활성화--%>
			$("#ico_swap").addClass("off");
			$("#ico_addfunc").addClass("off");
			$("#ico_analysis").addClass("off");
			$("#ico_myscrap").addClass("off");
			$("#ico_download").addClass("off");
			$("#ico_print").addClass("off");
			$("#chartEnable").addClass("off");
		}


		<%--
		/************************************************************************
		함수명 : fn_susionChartExcute
		설   명 : MSColumn3D,MsColumn2D,ScrollColumn2D,msline,ScrollLine2D,StackedColumn2D,StackedColumn3D,
				 ScrollStackedColumn2D,MSBar2D,MSBar3D,StackedBar2D,StackedBar3D,MSArea,ScrollArea2D,StackedArea2D,Pie3D
		인   자 : 통계표 종류
		사용법 : fn_chartRender("높이") 호출
		 ************************************************************************/
		 --%>
		function fn_fusionChartExcute(strGubun){
			<%--chartViewCnt 항상 체크해야함.--%>
			var chartViewCnt = $("#chartViewCnt option:selected").val();

			<%--범례 초기화--%>
			fn_chartDisable();

			fn_chart2014(chartViewCnt);

			g_ChartGubun = strGubun;			//챠트타입

			<%--chart 옵션--%>
			var sPalette,sRotateLabels,sAnimation,sNumdivlines,sBaseFont;
			var sBaseFontSize,sBorderAlpha,sFontSize,labelDisplay, yAxisValueDecimals;
			var yAxisMinValue,yAxisMaxValue,sShowValues,sBgcolor,anchorRadius;
    		var yAxisMin,yAxisMax,sDrawcrossline;

			g_xmlStr="";

			sPalette = '2';
		    sRotateLabels = '0';
		    sAnimation = '1'; <%--그래프 애니메이션 켜기 : 1, 끄기 : 0 --%>
		    sNumdivlines = '4';

			if( g_dataOpt.indexOf("en") >= 0){//2014.10.01 영문차트 폰트변경 - 김경호
				sBaseFont = 'Verdana';
			}else{
				sBaseFont = '굴림체';
			}

			sBaseFontSize = '12';
	    	sBorderAlpha = '30';
			sFontSize = '12';
			lFontSize = '18';
			labelDisplay = 'NONE'; 		<%--NONE,STAGGER,WRAP,ROTATE--%>
			yAxisValueDecimals = 0;
			yAxisMinValue = '0';
			yAxisMaxValue = '0';
			sShowValues = '0';			<%--그래프 value 값 보이기 : 1, 안보이기 : 0 --%>
			sBgcolor = 'ffffff';
			anchorRadius = '4';			<%--수치 앵커, 원의 크기 --%>
			sDrawcrossline = '0';		<%-- 0:false, 1: true 꺽은선일 경우 동일선상 값들을 범례와같이 보여줌 --%>

			<%--2019.05.16 Drawcrossline 지원 가능한 차트들만 기능 ON--%>
			if( g_ChartGubun == "MsColumn2D" || g_ChartGubun == "msline" || g_ChartGubun == "ScrollLine2D" || g_ChartGubun == "MSBar2D" ||
				g_ChartGubun == "StackedBar2D" || g_ChartGubun == "MSArea" || g_ChartGubun == "StackedArea2D"){
				sDrawcrossline = '1';
			}

	    	<%-- 2016.08.09 차트제목에 홀따옴표가 있으면 차트에러발생 - 김경호 --%>
	    	g_tableNm = g_tableNm.replace(new RegExp("'",'gi'),"");

		    if(strGubun == "Pie3D"){
		    	var pieTitle = g_tableNm+"("+g_chartLableArr[0]+")";			<%--기존챠트도 표두 첫번째 이름만 출력--%>

		    	var dataArry = new Array();
		    	var dataMap = {};

				$.each(g_chartDataArr,function(index,item){
					var dataValueArr = item.rowData;
					var unitArr = item.rowUnit;

					if(index < chartViewCnt){
						dataMap = {};

						dataMap.value = dataValueArr[0];
						dataMap.tooltext = numberFormat(dataValueArr[0]) + unitArr[0]+""; <%-- tooltip을 이용한 단위 표현 --%>
						dataMap.color = g_chartColor[index];
						dataMap.link = "javascript:selChartGird(\"" + item + "\","+index+","+chartViewCnt+")";
						dataArry.push(dataMap);
					}
				});

		    	chartInstance = new FusionCharts({
					type: g_ChartGubun,
					width: g_otherChartWidth,
					height: '320',
					dataFormat: 'json',
					renderAt: 'Chartcontent',
					dataSource:{
						"chart" :{
							"palette": sPalette, "rotateLabels": sRotateLabels, "animation": sAnimation, "numdivlines": sNumdivlines, "baseFont": sBaseFont,
							"baseFontSize": sBaseFontSize, "borderAlpha": sBorderAlpha, "captionFontSize": lFontSize,"labelFontSize":sFontSize,
							"caption": g_tableNm, "exportFileName": g_tblId, "bgColor" : sBgcolor, "showLegend": "1",
							"canvasPadding": '20', "adjustDiv": '0', "numDivLines": '3',
							"decimals": '1', "numberScaleValue": '1', "showhovereffect": "1", "exportShowMenuItem" : "0",
						    "exportEnabled": '1', "exportAtClientSide": '1', "enableSmartLabels":"1","exportFormats":"PNG|JPG|PDF",
							"theme": "fusion"
						},
						"data": dataArry
					}
		    	});

		    	chartInstance.render();


		    }else{
				<%--최소,최대 세팅시작--%>
		    	var yAxisArr = new Array();
		    	$.each(g_chartDataArr,function(index,item){
					if(index < chartViewCnt){
						var dataValueArr = item.rowData;
						$.each(dataValueArr,function(index,item){
							yAxisArr.push(item);
						});
					}
				});

		    	yAxisArr.sort(function(a,b){return a-b;});	<%--정렬--%>
		    	yAxisMin = yAxisArr[0];						<%--최소값--%>
		    	yAxisMax = yAxisArr[yAxisArr.length-1];		<%--최대값--%>

		    	var per = yAxisMax - yAxisMin;
		    	var zeromin ="N";

		    	if(yAxisMin >=0 && yAxisMin < per){zeromin ="Y";}

		    	if(per < 10){yAxisValueDecimals = 1;}
		    	if(per < 1){yAxisValueDecimals = 2;}

		    	if(zeromin == "Y"){
		    		yAxisMinValue = 0;
		    	}else{
		    		yAxisMinValue = eval(yAxisMin) - (per/12);
		    	}

		    	yAxisMaxValue = eval(yAxisMax) + (per/12)

		    	<%-- 차트 카테고리 등록시작 --%>
		    	var cateMainMap = {};
		    	var cateArry = new Array();
		    	var cateMap = {};

				$.each(g_chartLableArr,function(index,item){
					cateMap = {};
					cateMap.label = item;
					cateArry.push(cateMap);
				});
				cateMainMap.category = cateArry;
				<%-- 차트 카테고리 등록끝 --%>

				var dataSetArry = new Array();
		    	var dataSetMap = {};
		    	var dataArry = new Array();
		    	var dataMap = {};

		    	var dataMapTest = {};

				$.each(g_chartDataArr,function(index,item){
					if(index < chartViewCnt){

						var dataValueArr = item.rowData;
						var unitArr = item.rowUnit;

						dataArry = new Array();
						$.each(dataValueArr,function(index2,item2){
							dataMap = {};
							dataMap.value = item2;
							dataMap.tooltext = numberFormat(item2) + unitArr[index2]+"";
							dataMap.color = g_chartColor[index];
							dataMap.link = "javascript:selChartGird(\"" + item + "\","+index+","+chartViewCnt+")";
							dataArry.push(dataMap);
						});

						dataSetMap = {};
						dataSetMap.data = dataArry;

						<%-- 범례 만들기 시작 - 김경호 --%>
						var ids = $("#tbl_data_view").jqGrid('getDataIDs');
						var colModel = $("#tbl_data_view").jqGrid('getGridParam', 'colModel');

						dataMapTest[index] = "";

						for(x=0; x < colModel.length ; x++){
							if( x > 0){
								dataMapTest[index] += $("#tbl_data_view").getCell(ids[index], x);

								if( x+1 < colModel.length){
									dataMapTest[index] += ", ";
								}
							}
						}
						dataSetMap.seriesname = dataMapTest[index];
						<%-- 범례 만들기 끝 - 김경호 --%>

						dataSetArry.push(dataSetMap);
					}
				});

				<%-- showLegend : (1 : 범례 보이기, 0 : 범례 안보이기) --%>
		    	chartInstance = new FusionCharts({
					type: g_ChartGubun,
					width: g_otherChartWidth,
					height: '320',
					dataFormat: 'json',
					renderAt: 'Chartcontent',
					dataSource:{
						"chart" :{
							"palette": sPalette, "rotateLabels": sRotateLabels, "animation": sAnimation, "numdivlines": sNumdivlines, "baseFont": sBaseFont,
							"baseFontSize": sBaseFontSize, "borderAlpha": sBorderAlpha, "captionFontSize": lFontSize,"labelFontSize":sFontSize, "yAxisValueDecimals": yAxisValueDecimals,
							"yAxisMinValue": yAxisMinValue, "yAxisMaxValue": yAxisMaxValue, "showValues" : sShowValues, "drawcrossline" : sDrawcrossline,
							"caption": g_tableNm, "exportFileName": g_tblId, "showLegend": "1",
							"canvasPadding": '20', "adjustDiv": '0', "numDivLines": '3',
							"decimals": '1', "numberScaleValue": '1', "showhovereffect": "1",
						    "exportEnabled": '1', "exportAtClientSide": '1',"xportFormats":"PNG|JPG|PDF","exportShowMenuItem" : "0",
							"theme": "fusion"
						},
						"categories": [cateMainMap],
						"dataset": dataSetArry
					}
		    	});

		    	chartInstance.render();

			}
			$("#Chartcontent").css("display","block");
		}

		function selChartGird(item,sn,chartViewCnt){
			sn++;
			if(g_remarkSelect !=""){
	    		$("#tbl_data_view").find("tr:eq("+g_remarkSelect+")").css("background-color","#FFFFF1");
			}
			g_remarkSelect = sn;
    		$("#tbl_data_view").find("tr:eq("+sn+")").css("background-color","yellow");

    		if(sn <= 7){
				sn = 0;
			}else if(sn > 7 && sn < 15){
				sn = sn - 1;
			}else{
				sn = chartViewCnt - 1;
			}

	   		$("#tbl_data_view").jqGrid('setSelection',sn);
		}

		<%-- 그래프의 수치데이터의 포맷변경(#,###,###.999999 --%>
		function numberFormat(data){
			var result = "";
			var numStr = "";
			var digitStr = "";

			try{
				var allStr = data.toString();

				var digitIdx = allStr.indexOf(".");
				if(digitIdx > -1){
					numStr = allStr.substring(0, digitIdx);
					digitStr = allStr.substring(digitIdx);
				}else{
					numStr = allStr;
				}

				for(var i = 0; i < numStr.length; i++){
					var tmp = numStr.length - (i + 1);
					if( i%3 == 0 && i != 0 ){
						result = "," + result;
					}
					result = numStr.charAt(tmp) + result;
				}

				result += digitStr;
			}catch(e){
				result = data;
			}

			return result;
		}

		function fn_chartClose(){

			<%-- 2015.08.03 차트창 닫을 경우 아이콘 원래대로 --%>
			$("#ico_swap").removeClass("off");
			$("#ico_addfunc").removeClass("off");
			$("#ico_print").removeClass("off");
			$("#chartEnable").removeClass("off");

		<c:if test="${statInfo.downloadable}">
			$("#ico_myscrap").removeClass("off");
			$("#ico_download").removeClass("off");
		</c:if>

		<c:if test="${statInfo.analyzable}">
			$("#ico_analysis").removeClass("off");
		</c:if>

			var dataOpt2 = $("#dataOpt2 option:selected").val();

			if( dataOpt2 == "en"){
				$("#searchImg1").attr("src","images/btn_tableSearch_en.gif");
			}else{
				$("#searchImg1").attr("src","images/btn_tableSearch.gif");
			}

			$("#Divchart").css("visibility","hidden");
			$("#Chartcontent").css("display","none");
			$("#Divchart").css("height","0px");

			fn_chartDisable();

			$("#popMode").css("height","606px");

			if($("#analysisText").height() > 0){
				$("#htmlGrid").css("height","585px");
			}else{
				$("#htmlGrid").css("height","604px");
			}

			$("#htmlGrid").css("width","100%");
			$("#popMode").css("display","block");

			g_chartActive = "N";
			<%-- 챠트 범례 jqGrid 수정 후 htmlGrid border 겹치는 현상--%>
			$("#htmlGrid").css("border","1px solid #b1b1b1");

			<%-- 2016.03.24 차트조회후 닫았을때 통계표 상단 틀고정 시작  --%>
			adjustThtmlGrid();
			<%-- 틀고정 끝  --%>
		}

		function fn_metaDown(downType){
			fn_searchCond();

			form.view.value = "txt";

			if(downType == "grid"){
				form.viewSubKind.value = "2_META";
				form.viewKind.value = "2";
			}else{
				form.viewSubKind.value = "2_7_META";
				form.viewKind.value = "2";
			}
			form.action = "<%=request.getContextPath()%>/downMeta.do";
			form.submit();
			form.viewSubKind.value = "";
		}

		function fn_progressBar(viewType){
			if(viewType =='show'){
				$("#modal2").css("visibility", "visible");

				var progressBarHeight =  g_modal2Height/2;
				$("#disPlayBox").css("display", "block");
			}else{
				$("#modal2").css("visibility","hidden");
				$("#disPlayBox").css("display","none");
			}
		}

		function fn_popupSIGA(){
			var url = "${statInfo.statLinkInfo.sigaUrl}?orgId=${ParamInfo.orgId}&tblId=${ParamInfo.tblId}&empId=${empId}";
			$("#ifr_SIGA").attr("src", url);
		}

		function popupPrint(){

			if($("#ico_print").hasClass("off") == true){
				return;
			}

			var orgId = $('#orgId').val();
			var tblId = $('#tblId').val();

			if(confirm("<pivot:msg code="alert.print.msg"/>")){
				form.viewKind.value="4";

				$.ajax({
					dataType : 'json',
					type : 'POST',
					url : "<%=request.getContextPath()%>/setOlapLogForNsoService.do",
					data : $("#ParamInfo").serialize()
				});

				window.open("<%=request.getContextPath()%>/print.do?orgId="+orgId+"&"+"tblId="+tblId, "", "width=950, height=600, scrollbars=yes ,resizable=yes, toolbar=yes");
			}
		}

		<%--
		/************************************************************************
		함수명 : fn_searchRange()
		설   명 : 짜증나는 조회범위 상세설정
		인   자 : condRangeArr : searchRangeDetail.jsp에서 항목,분류 세팅된 Array
		 ************************************************************************/
		 --%>
		function fn_searchRange(classRangeArr,itemRangeArr,classCntArr,timeRangeArr,timeTotCnt){

			g_maxChkFlag = "N";
			g_defaultClassArr= new Array();
			g_defaultClassArr = eval("("+classRangeArr+")");

			wowArr = new Array();
			wowArr = eval("("+classRangeArr+")");

			cntArr = new Array();
			cntArr = eval("("+classCntArr+")");

			timeArr = new Array();
			timeArr = eval("("+timeRangeArr+")");

			g_rangeTimeCnt = timeTotCnt;
			var chkFlag = $("#p_classAllChkYn").val();
			var disableArr = new Array();
			$.each(timeArr,function(index,item){
				var searchPrdSe = item.searchPrdSe;
				var startTime	= item.startTime;
				var endTime		= item.endTime;
				$("#time"+searchPrdSe+" h2:last").find('select:first').val(startTime);
				$("#time"+searchPrdSe+" h2:last").find('select:last').val(endTime);

				var rangeMode = "Y"
				g_rangeTimeArr = item.chkListArr.concat([]);
				<%--headCheck 상태--%>
				var headChkStatus =$("#check"+searchPrdSe).is(":checked");
				if(headChkStatus == false){
					$("#check"+searchPrdSe).prop("checked",true);
				}
				fn_enable(searchPrdSe);
				fn_searchPeriod(searchPrdSe,rangeMode,g_rangeTimeArr);

				disableArr.push(searchPrdSe);
			});

			<%--주기 체크 상태 비활성화 처리 01.15--%>
			$.each(g_result,function(index,item){
				var headDisable = $.inArray(item,disableArr);
				if(headDisable < 0){
					var headChkStatus = $("#check"+item).is(":checked");
					if(headChkStatus == true){
						$("#check"+item).prop("checked",false);
						fn_enable(item);
					}
				}
			});

			if(chkFlag!="N"){
				fn_selectItemAll("Y");		<%--일단 항목 정보 초기화 --%>

	 			<%--항목 카운트 체크--%>
				$.each(itemRangeArr,function(index,item){
					var itemRange = item;
					$('[name=itemChkLi]').each(function(index){
						var parentItem = $(this).val();
						if(itemRange == parentItem){
							$(this).trigger("click");

						}
					});
				});
			}
			<%--
			분류 카운트 체크: g_defaultClassArr에 세팅된 그자체로 조회조건에 insert
			탭분류의 카운트 초기화 .분류1레벨은 반드시 체크상태 확인해야함
			--%>
			var RangeCnt = cntArr.length;

			for(var k=0;k<RangeCnt; k++){
				var dataCnt = cntArr[k].totalCnt;
				$.each(g_tabClassCnt,function(index,item){
					if(cntArr[k].objVarId == item.objVarId){
						var tempObj = {};
						tempObj.objVarId = item.objVarId;
						tempObj.dataCnt =  dataCnt;
						tempObj.totalCnt = item.totalCnt;
						tempObj.classSn =  item.classSn;
						tempObj.classNm =  item.classNm;
						tempObj.classDepth = item.classDepth;
						g_tabClassCnt.splice(index,1,tempObj);

						var tempStr = item.classNm+"["+dataCnt+"/"+item.totalCnt+"]";
						$("#tabClassText_"+item.classSn+" span").html("<font title='"+tempStr+"'>"+tempStr+"</font>");
					}
				});
			}

			var tempInsertClassCnt = 1;

			$.each(g_tabClassCnt,function(index,item){
				tempInsertClassCnt = item.dataCnt * tempInsertClassCnt;
				for(var i=0; i<g_classEachCnt.length; i++){
					if(item.objVarId == g_classEachCnt[i].objVarId){
						tmpObj ={};
						tmpObj.objVarId = item.objVarId;
						tmpObj.dataCnt  = item.dataCnt;
						g_classEachCnt.splice(index,1,tmpObj);
					}
				}
			});
			<%--분류 총갯수 세팅--%>
			g_classCnt = tempInsertClassCnt;

			<%--개별 분류 문자열 세팅--%>
			fn_classStrView(g_classEachCnt);
			g_maxChkFlag = "Y";
			fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);

			<c:forEach items="${statInfo.classInfoList}" var="tab" varStatus="status">
				var tempClassId = "${tab.classId}";
				var status ="${status.count}";
				var totalCnt	 = "${statInfo.classInfoList[status.index].itmCnt}";
				var classDepth	   = "${statInfo.classInfoList[status.index].depthLvl}";

				<c:if test="${tab.visible == true}">
				chkId = "classChk"+status+"_1"
				chkName = "classChkLi";
				<%--2014 02.18 분류의 1레벨만 컨트롤 함..default로 1레벨이 전체체크되어 있고 일괄설정에서 1레벨 한개또는 전체 제외하고 2레벨..3레벨 세팅시 1레벨 전체체크 해제
					일괄성정에서 분류별1레벨을 선택 안할수도 있고 한개또는 전체 선택 할수 있음..일단 체크해제하고 아래에서 한번더 체크 *여기 중요* 분류별 count 갯수에 영향
				--%>

				$("#classChk"+status+'_1').prop("checked",false);
				$('[name=classChkLi'+status+'_'+1+']').prop("checked",false);

				$.each(wowArr,function(index,item){
					var tempArr = new Array();
					var chkComandCnt =  0;
					<%--
					//여기 중요함 속도 안나오면 여기 고치기
					//1.분류의 전체체크 상태가 N-일 경우 하위값체크상태를 N으로 변경하기
					//2.분류의 전체체크 상태가 Y-일 경우 전체체크를 N으로 변경
					--%>
					if(tempClassId == item.objVarId && item.classType == 1){

						chkId = "classChk"+status+"_"+item.classType;
						chkName = "classChkLi"+status+"_"+item.classType;
					   	tempArr = item.data;

					   var tempCnt = tempArr.length;

					   for(var ii=0; ii<tempCnt; ii++){
					   		rangeClassId = "classChkLi"+status+"_"+item.classType+"_"+tempArr[ii];					<%--분류별 레벨 input Id 값--%>
							document.getElementById(rangeClassId).checked = true;

							chkComandCnt++;
					   	}
					}
				});
				</c:if>

				<%-- 통계표 조회후 분류별 전체레벨이 체크 상태가 false이고 분류별/레벨별 전체선택 체크상태확인2014.04.20--%>
				fn_lowerLankLevel(tempClassId,status,classDepth);
			</c:forEach>

			popupControl('pop_detailfunc', 'hide', 'modal');
			<%--조회셀이 만셀보다 크면--%>
			if(g_multiplication > g_maxCell){
				if(chkFlag!="N"){
				<%--조회셀이 20만셀보다 크면--%>
				<%--
					if(g_mixDimCnt > g_maxCellDownload){
					2015.06.02 조회 범위 조절시 200000만셀 초과되지 않았는데 초과 메세지 보여 수정
					g_mixDimCnt -> dim_co 와 비교하지 않고 조합수를 비교하도록 수정
				--%>
					if(g_multiplication > g_maxCellDownload){
						alert( "<pivot:msg code="search.condition.msg"/>" );
						return;
					}else{
						alert( "<pivot:msg code="fail.downLoad.msg2"/>" );
						return;
					}
				}
			}else{
				if(chkFlag!="Y"){
					<%--메인화면에서 전체레벨 체크상태&& 레벨별 전체선택 동기화처리--%>
					$("#p_classAllChkYn").val("Y");
				}else{
					<%--일괄설정--%>
					<%--열려있는 분류레벨 empty처리--%>
					<c:forEach items="${statInfo.classInfoList}" var="tabClass" varStatus="status">
						var closeIndex = "${status.count}"
						var classDepth = "${statInfo.classInfoList[status.index].depthLvl}";

						for(var i=1; i<=classDepth; i++){										<%--1레벨은 열려있어야 함--%>
							$("#itmList"+closeIndex+" ul:eq("+i+")").empty();					<%--eq index는 0부터--%>
							var headIndex = i + 1;
							$("#classChk"+closeIndex+"_"+headIndex).attr("disabled",true);
						}
					</c:forEach>

					fn_search("Y"); 		<%--조회범위 상세설정하기 rangeType =="Y"--%>
				}
			}
		}

		<%--
		/************************************************************************
		함수명 : fn_assay()
		설   명 :  분석
		 ************************************************************************/
		 --%>
		function fn_assay(assayType){
			if(assayType =="TOTL_CMP_RATE" || assayType =="CMP_RATE" || assayType=="CHG_RATE_CO"){
				$("#pop_assay").css("width","830px");
				$("#ifrAssayInfo").css("width","830px");
			}else{
				$("#pop_assay").css("width","420px");
				$("#ifrAssayInfo").css("width","420px");
			}
			var noselectType = null;
		}

		function popupHtml(url, popupHeight){
			if ( notice_getCookie( "${statInfo.popupFileNm}${ParamInfo.tblId}" ) != "done" )  {
				window.open(url,"_blank","width=350, height=" + popupHeight + ", left=10, top=10,status=yes, scrollbars=no,resizable=no, menubar=no");
			}
		}

		<%--2013.12.07 통계표 조회 시 팝업알림창 관련 스크립트--%>
		function notice_getCookie( name ){
			var nameOfCookie = name + "=";
			var x = 0;
			while ( x <= document.cookie.length )
			{
				var y = (x+nameOfCookie.length);
				if ( document.cookie.substring( x, y ) == nameOfCookie ) {
					if ( (endOfCookie=document.cookie.indexOf( ";", y )) == -1 )
						endOfCookie = document.cookie.length;
					return unescape( document.cookie.substring( y, endOfCookie ) );
				}
				x = document.cookie.indexOf( " ", x ) + 1;
				if ( x == 0 )
					break;
			}
			return "";
		}

		function downHelp(){
			form.action = '${ParamInfo.contextPath}/downHelp.do';
			form.submit();
		}

		function fn_gridOpen(){

			$("#block"+g_hideId).css("display","none");
			$("#"+g_TabGubun).attr("class","menu_off");
			$("#textShow").hide();
			$("#btnShow").show();
			$("#stblUnit").show();
			$("#tailExplain").hide();
			$("#popMode").css("height","606px");
			$("#htmlGrid").css("height","604px");
			$("#htmlGrid").css("width","100%");
			$("#Divchart").css("height","0");
			$("#popMode").css("display","block");
			g_flag = true;

			$("#changeAttribute").attr("class","cont_line");

			<%-- 2016.03.24 차트조회후 닫았을때 통계표 상단 틀고정 시작 --%>
			adjustThtmlGrid();	<%-- ThtmlGrid 위치조정 --%>
			<%-- 틀고정 끝 --%>
		}

		<%--
		/************************************************************************
		함수명 : fn_classAllSelect()
		설   명 :  분류별 전체체크
				  화면에서 레벨이 펼쳐진 경우 펼쳐진 레벨까지 체크상태로 변경(g_defaultClasArr에는 세팅안함)
				  일괄설정의 전체 addAll기능으로 연결/분류레벨은 현재 동적으로 작동되기때문
				  분류별전체체크(id="classAllSelect")상태에 따라 나머지 레벨 전체선택 체크상태 변경
		인   자 : 순번,분류코드,레벨=All,총레벨
		 ************************************************************************/
		 --%>
		function fn_classAllSelect(sn,classId,lvl,classDepth){
			<%--
				1.현재 선택된 분류의 레벨이 열려 있는지 확인하고
				열려있다면 열려있는 모든 분류의 체크 상태를 체크로 바꿈
				2.전체선택만 체크해놓고 다른 분류를 선택하고 레벨을 조회할 경우 //레벨별 전체체크와
			--%>

			$("#p_classAllChkYn").val("N");
			var chkStatus;

				chkStatus = $("#classAllSelect"+sn+"_"+classDepth).is(":checked"); 			<%--분류별 전체선택 체크상태--%>

				if(chkStatus == true){
					$("#p_classAllSelectYn").val("Y");							<%--분류별 전체인경우 일괄설정 팝업에서 우측select option empty후 append 할 구분값--%>
					var lvlArray = new Array();
					var searchLvl;
					$('[name=classLvlAllChk'+sn+'_'+classDepth+']').each(function(index){
						var lvlChk = $(this).is(":checked");
						if(lvlChk != true){
							index++;
							lvlArray.push(index);
						}
					});

					searchLvl = lvlArray.join(",");
					fn_ajaxAllChk(sn,classId,searchLvl,classDepth);

					fn_lowerLank(sn,classDepth,true);							<%--분류별 레벨단위 전체 체크 true--%>

					$.each(g_tabClassCnt,function(index,item){
						if(classId == item.objVarId){
							var tempObj = {};
							tempObj.objVarId = item.objVarId;
							tempObj.dataCnt =  item.totalCnt;
							tempObj.totalCnt = item.totalCnt;
							tempObj.classSn =  item.classSn;
							tempObj.classNm =  item.classNm;
							tempObj.classDepth = item.classDepth;
							g_tabClassCnt.splice(index,1,tempObj);
							var tempStr = item.classNm+"["+item.totalCnt+"/"+item.totalCnt+"]";
							$("#tabClassText_"+item.classSn+" span").html("<font title='"+tempStr+"'>"+tempStr+"</font>");
						}
					});

				}else{
					fn_lowerLank(sn,classDepth,false);						<%--분류별 레벨단위 전체 체크 false--%>
					<%--
						1. 탭메뉴 카운트값 변경
						2. changeTextLi 카운트 text 변경
						3. g_defaultClassArr 분류별 레벨 삭제 하기

						function 처리 가능함...
					--%>
					$.each(g_tabClassCnt,function(index,item){
						if(classId == item.objVarId){
							var tempObj = {};
							tempObj.objVarId = item.objVarId;
							tempObj.dataCnt =  0;
							tempObj.totalCnt = item.totalCnt;
							tempObj.classSn =  item.classSn;
							tempObj.classNm =  item.classNm;
							tempObj.classDepth = item.classDepth;
							g_tabClassCnt.splice(index,1,tempObj);
							var tempStr = item.classNm+"[0/"+item.totalCnt+"]";
							$("#tabClassText_"+item.classSn+" span").html("<font title='"+tempStr+"'>"+tempStr+"</font>");
						}
					});

					var tempInsertClassCnt=1;

					$.each(g_classEachCnt,function(index,item){
						tempInsertClassCnt = item.dataCnt * tempInsertClassCnt;
						if(item.objVarId == classId){
							tmpObj ={};
							tmpObj.objVarId = item.objVarId;
							tmpObj.dataCnt  = 0;
							g_classEachCnt.splice(index,1,tmpObj);
						}
					});

					<%-- 분류별 전체선택을 false를 제외한것만 추출--%>
					var tempArr = new Array;
					$.each(g_defaultClassArr,function(index,item){
						if(classId != item.objVarId){
							var defaultObj={};
							defaultObj.objVarId = item.objVarId;
		 					defaultObj.data = item.data;
		 					defaultObj.classType = item.classType;							<%--1레벨부터 ~ Max레벨까지 세팅--%>
		 					defaultObj.classLvlCnt = item.data.length;						<%--2014.04.08 분류별 레벨의 카운트 갯수--%>
		 					tempArr.push(defaultObj);
						}
					});

					g_defaultClassArr = tempArr;
					<%--분류 총갯수 세팅--%>
					g_classCnt = 0;
					<%--개별 분류 문자열 세팅--%>
					fn_classStrView(g_classEachCnt);
					fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);

				}
		}

		<%--
		/************************************************************************
		함수명 : fn_classSubSelect()
		설   명 : 분류별 ~레벨 전체선택  classLvlAllChk1_1 ~ 작동 함수
				 속도 개선을 위한 로직 분리
				★ 분류별 레벨의 전체 체크 상태 수와 총레벨 수를 비교 같으면 if_serchRangeDetail에서 분기★
		인   자 : 순번,분류코드,현재 선택된 레벨,총레벨
		 ************************************************************************/
		 --%>
		function fn_classSubSelect(sn,classId,lvl,classDepth){
			$("#p_classAllChkYn").val("N");
			$("#p_classAllSelectYn").val("N");
			chkStatus = $("#classLvlAllChk"+sn+"_"+lvl).is(":checked");		<%--분류별 레벨별 전체선택 체크상태--%>
			$("#classChk"+sn+'_'+lvl).prop("checked",chkStatus);			<%--상위레벨 선택 하면 하위레벨 title 체크박스--%>
			fn_deceitLook(sn,lvl,chkStatus);

			if(chkStatus == true){
				var tempCnt = $('[name=classLvlAllChk'+sn+'_'+classDepth+']:checked').size(); <%-- 분류별 체크된 전체선택 체크박스 갯수 --%>
				if(tempCnt == classDepth){
					$("#classAllSelect"+sn+"_"+classDepth).prop("checked",true);
				}
				fn_ajaxAllChk(sn,classId,lvl,classDepth);
			}else{
				var chkCnt=0;

				$("#classAllSelect"+sn+"_"+classDepth).prop("checked",false);

					<%-- 분류별 전체선택을 false를 제외한것만 추출--%>
					var tempArr = new Array;
					$.each(g_defaultClassArr,function(index,item){
						if(item.objVarId == classId && item.classType == lvl){
	 						chkCnt = item.classLvlCnt;
						}else{
							var defaultObj={};
							defaultObj.objVarId = item.objVarId;
		 					defaultObj.data = item.data;
		 					defaultObj.classType = item.classType;							<%--1레벨부터 ~ Max레벨까지 세팅--%>
		 					defaultObj.classLvlCnt = item.data.length;						<%--2014.04.08 분류별 레벨의 카운트 갯수--%>
		 					tempArr.push(defaultObj);
						}
					});

					g_defaultClassArr = tempArr;

				for(var i=0; i<g_classEachCnt.length; i++){
					if(classId == g_classEachCnt[i].objVarId){
						tmpObj ={};
						tmpObj.objVarId = classId;
						tmpObj.dataCnt  = g_classEachCnt[i].dataCnt-chkCnt;
						g_classEachCnt.splice(i,1,tmpObj);
					}
				}

				var tempInsertClassCnt =1;
				$.each(g_tabClassCnt,function(index,item){
					if(classId == item.objVarId){
						var tempObj = {};
						var dataCnt = item.dataCnt - chkCnt;
						tempObj.objVarId = item.objVarId;
						tempObj.dataCnt =  dataCnt;
						tempObj.totalCnt = item.totalCnt;
						tempObj.classSn =  item.classSn;
						tempObj.classNm =  item.classNm;
						tempObj.classDepth = item.classDepth;
						g_tabClassCnt.splice(index,1,tempObj);

						var tempStr = item.classNm+"["+dataCnt+"/"+item.totalCnt+"]";
						$("#tabClassText_"+item.classSn+" span").html("<font title='"+tempStr+"'>"+tempStr+"</font>");
					}
				});

				$.each(g_tabClassCnt,function(index,item){
					tempInsertClassCnt = item.dataCnt * tempInsertClassCnt;
				});

				<%-- 분류 총갯수 세팅 --%>
				g_classCnt = tempInsertClassCnt;
				<%-- 개별 분류 문자열 세팅 --%>
				fn_classStrView(g_classEachCnt);
				fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);
			}
		}

		function fn_ajaxAllChk(sn,classId,lvl,classDepth){
			fn_progressBar("show");
			$.ajax({
				dataType : 'json',
				type : 'POST',
				url : "<%=request.getContextPath()%>/classDivSelect.do",
				dataType : "json",
				data : {
					'lvl'     	 : lvl,
					'objVarId'	 : classId,
					'searchCondition' : "A",
					'orgId'      : "${ParamInfo.orgId}",
					'tblId'	     : "${ParamInfo.tblId}",
					'dataOpt'	 : "${ParamInfo.dataOpt}",
					'language'	 : "${ParamInfo.language}",
					'pub'		 : "${ParamInfo.pub}",
					'dbUser'	 : "${ParamInfo.dbUser}",
					'st'		 : "${ParamInfo.st}"
				},
				success : function(response,status){
					var tmpList = response.result ;
					var list = "";
					var autoChkCnt=0;						<%--전체선택을 클릭하고 다른 레벨 조회 후 그전 레벨로 다시 조회 할 경우..전체선택박스에 자동체크비교 카운트--%>

					<%--분류리스트 html만들기--%>
					if(status ='success'){
						if(tmpList!=null){
							var arrCnt = g_defaultClassArr.length;
							<%--1. g_defaultClassArr.length > 0 큰경우 분류별 레벨 정보가 세팅되어 있음
								 1-1. g_defaultClassArr에 tmpList의 세팅값이 같은 경우 tmpList데이터를 기준으로 치환
								 1-2. 치환된 레벨정보 array에 세팅
								 1-3 치환된 정보가 있으면 tmpList에서 치환되지 않은 데이터를 찾고 push
								 1-4 치환된 정보가 없으면 tmpList 데이터를 push
								2. g_defaultClassArr.length == 0 인 경우 tmpList 데이터를 push
							--%>
							if(arrCnt > 0){
								var removeArr = new Array();
								$.each(tmpList,function(index,info){
									var defaultId = info.objVarId;
									var defaultLvl = info.classType;
									var defaultCnt = info.classLvlCnt;
									var defaultData = info.data;

									for(var k=0; k<arrCnt; k++){
										if(g_defaultClassArr[k].objVarId == defaultId && g_defaultClassArr[k].classType == defaultLvl){
											tmpObj ={};
											tmpObj.objVarId = defaultId;
											tmpObj.classType  = defaultLvl;
											tmpObj.classLvlCnt  = defaultCnt;
											tmpObj.data  = defaultData;
											g_defaultClassArr.splice(k,1,tmpObj);
											removeArr.push(defaultLvl);
										}
									}
								});

								if(removeArr.length > 0){
									$.each(tmpList,function(index,info){
										if($.inArray(info.classType,removeArr) < 0){
											tmpObj ={};
											tmpObj.objVarId = info.objVarId;
											tmpObj.classType = info.classType;
											tmpObj.data = info.data;
											tmpObj.classLvlCnt = info.classLvlCnt;
											g_defaultClassArr.push(tmpObj);
										}
									});

								}else{
									$.each(tmpList,function(index,info){
										tmpObj ={};
										tmpObj.objVarId = info.objVarId;
										tmpObj.classType = info.classType;
										tmpObj.data = info.data;
										tmpObj.classLvlCnt = info.classLvlCnt;
										g_defaultClassArr.push(tmpObj);
									});
								}
							}else{
								$.each(tmpList,function(index,info){
									tmpObj ={};
									tmpObj.objVarId = info.objVarId;
									tmpObj.classType = info.classType;
									tmpObj.data = info.data;
									tmpObj.classLvlCnt = info.classLvlCnt;
									g_defaultClassArr.push(tmpObj);
								});
							}

							var extractionCnt=0;
							<%--분류 카운트 추출...--%>
							$.each(g_defaultClassArr,function(index,info){
								if(classId == info.objVarId){
									extractionCnt = extractionCnt + info.classLvlCnt;
								}
							});

							for(var i=0; i<g_classEachCnt.length; i++){
								if(classId == g_classEachCnt[i].objVarId){
									tmpObj ={};
									tmpObj.objVarId = classId;
									tmpObj.dataCnt  = extractionCnt;
									g_classEachCnt.splice(i,1,tmpObj);
								}
							}

							var tempInsertClassCnt=1;
							$.each(g_tabClassCnt,function(index,item){
								if(classId == item.objVarId){
									var tempObj = {};
									tempObj.objVarId = item.objVarId;
									tempObj.dataCnt =  extractionCnt;
									tempObj.totalCnt = item.totalCnt;
									tempObj.classSn =  item.classSn;
									tempObj.classNm =  item.classNm;
									tempObj.classDepth = item.classDepth;
									g_tabClassCnt.splice(index,1,tempObj);

									var tempStr = item.classNm+"["+extractionCnt+"/"+item.totalCnt+"]";
									$("#tabClassText_"+item.classSn+" span").html("<font title='"+tempStr+"'>"+tempStr+"</font>");
								}
							});

							$.each(g_tabClassCnt,function(index,item){
								tempInsertClassCnt = item.dataCnt * tempInsertClassCnt;
							});

							g_classCnt = tempInsertClassCnt;

							<%--개별 분류 문자열 세팅--%>
							fn_classStrView(g_classEachCnt);

							fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);


						<%--g_defaultClassArr.push(tmpList); 자바단 tmpList map형식은 바로 push가능--%>

							fn_progressBar("hide");
						}else{
						}
					}
				},
				error : function(error){
					alert( "<pivot:msg code="fail.common.msg"/>" );
				}
			});
		}
		<%--
		/************************************************************************
		함수명 : fn_lowerLank()
		설   명 :  초기 화면 구성시 분류별 defaultCnt 갯수와 분류별 maxCnt 갯수가 같을
				  경우 분류의 레벨별 전체선택 체크 상태 변경
		인   자 : classSn->현재분류 순서, classDepth->현재분류의 최대 레벨 type="first" 최초세팅할때는 분류레벨이 1만 보임
				 default세팅에서 분류값 체크여부 확인함
		 ************************************************************************/
		 --%>
		function fn_lowerLank(classSn,classDepth,status,type){
			var loopCnt = classDepth;
			var standardSn = classSn;

			if(type == "first"){
				for(var i=1; i<=loopCnt;i++){
					if(status == true){
						$("#classLvlAllChk"+standardSn+"_"+i).prop("checked",true);
					}else{
						$("#classLvlAllChk"+standardSn+"_"+i).prop("checked",false);
					}
				}
			}else{
				for(var i=1; i<=loopCnt;i++){
					if(status == true){
						$("#classLvlAllChk"+standardSn+"_"+i).prop("checked",true);
	 					fn_deceitLook(standardSn,i,true);
					}else{
						$("#classLvlAllChk"+standardSn+"_"+i).prop("checked",false);
	 					fn_deceitLook(standardSn,i,false);
					}
				}
			}

		}

		<%--
		/************************************************************************
		함수명 : fn_lowerLankLevel()
		설   명 : 통계표조회시 분류별 전체선택이 아닌경우 레벨별 전체선택체크박스 동기화
		인   자 : objVarId,classDepth
		 ************************************************************************/
		 --%>
		function fn_lowerLankLevel(objVarId,classSn,classDepth){
			var loopLvl = classDepth;
			var tmpId = objVarId;
			<%--분류별 전체선택 체크상태 판별--%>
			var classTypeChk = $("#classAllSelect"+classSn+"_"+classDepth).is(":checked");

			<%--분류별 레벨별 전체선택 여부 검사시작--%>
				var trueCnt=0;
				var i=1;
				while ( i <= loopLvl ){
					<%-- 2014 04 20 먼저 해제 후 다시 체크--%>
					$("#classLvlAllChk"+classSn+"_"+i).prop("checked",false);
					$.each(g_classLvlArr,function(index,item){
						var baseId  = item.objVarId;
						var baseLvl = item.lvl;
						if(tmpId == baseId && i == baseLvl){
							var baseLvlCnt = item.lvlCnt;						<%-- 레벨 카운트 추출 --%>
							$.each(g_defaultClassArr,function(index,info){
								var defaultId = info.objVarId;
								var defaultLvl = info.classType;
								var defaultCnt = info.classLvlCnt;

								if(baseId == defaultId && baseLvl == defaultLvl && baseLvlCnt == defaultCnt){
									$("#classLvlAllChk"+classSn+"_"+i).prop("checked",true);
									trueCnt++;
								}
							});
						}
					});

					i++;
				}

			<%--nso.jsp에서 처리되는 로직과 별개로 일괄설정에서 분류 레벨별 전체선택인 경우 classAllSelect+classSn+classDepth 체크상태 동기화
				1.nso에서 분류 부분 선택하고 일괄설정에서 전체선택 한경우 체크박스 true
				2.nso에서 분류 전체 선택하고 일괄설정에서 부부선택 한경우 체크박스 false
			--%>
			if(trueCnt == loopLvl){
				$("#classAllSelect"+classSn+"_"+loopLvl).prop("checked",true);
			}else{
				$("#classAllSelect"+classSn+"_"+loopLvl).prop("checked",false);
			}
		}

		<%--
		/************************************************************************
		함수명 : fn_deceitLook()
		설   명 : ul class="selectList" ->li-> input name classChkLi~_~ 체크상태 동기화
				 fn_lowerLank()에서 하위 체크 시 사용&& 분류별 레벨별 전체선택 체크시 사용
		인   자 : classSn->현재분류 순서, classDepth->현재분류의 최대 레벨
		 ************************************************************************/
		 --%>
		function fn_deceitLook(classSn,lvl,chkStatus){

			if(chkStatus == true){
				$("#classChk"+classSn+'_'+lvl).prop("checked",true);			<%--상위레벨 선택 하면 하위레벨 title 체크박스 --%>
				$('[name=classChkLi'+classSn+'_'+lvl+']').each(function(index){
					$(this).prop("checked",true);
				});
			}else{
				$("#classChk"+classSn+'_'+lvl).prop("checked",false);			<%--상위레벨 선택 하면 하위레벨 title 체크박스 --%>
				$('[name=classChkLi'+classSn+'_'+lvl+']').each(function(index){
					$(this).prop("checked",false);
				});
			}
		}


		<%--
		/************************************************************************
		함수명 : fn_rangeCallFunc()
		설   명 : 분류별 전체레벨 선택/해제 분리 checked true 인 경우만
		인   자 : chkStatus,classId,lvl
		 ************************************************************************/
		 --%>
		function fn_rangeCallFunc(chkStatus,classId,lvl,logicFlag){
			$("#p_chkStatus").val(chkStatus);
			$("#p_objVarId").val(classId);
			$("#p_lvl").val(lvl);
			$("#p_logicFlag").val(logicFlag);

			fn_progressBar("show");

			fn_searchCond();							 						<%--현재 선택정보 다시 세팅--%>
			form.selectTimeRangeCnt.value = g_timeCnt;							<%--시점 카운트 세팅--%>
			form.action="<%=request.getContextPath()%>/searchRangeDetail.do";
			form.target="ifrSearchDetail";
			form.submit();
		}

		<%--
		/************************************************************************
		함수명 : fn_oneLankLevel()
		설   명 : 분류별 레벨별 전체 체크 상태 확인
		인   자 : objVarId,classSn,AllClassLvl,classLvlCnt
		 ************************************************************************/
		 --%>
		function fn_oneLankLevel(objVarId,classSn,AllClassLvl,classLvlCnt){

			$("#classLvlAllChk"+classSn+"_"+AllClassLvl).prop("checked",false);		<%--nso에서 체크 상태였다가 일괄설정에서 분류정보 해제시 동기화 필요하므로 해제후 다시 체크--%>
			$.each(g_classLvlArr,function(index,item){
				var baseId = item.objVarId;
				var baseLvl = item.lvl;
				if(objVarId == baseId && AllClassLvl == baseLvl){
					if(classLvlCnt == item.lvlCnt){
						$("#classLvlAllChk"+classSn+"_"+AllClassLvl).prop("checked",true);
					}
				}
			});
		}

		function fn_chart2014(chartViewCnt){

			var tbl_id = 'tbl_data_view';
			var col_name_arrays = new Array();
			var col_model_arrays = new Array();

			$.each(g_remarkH,function(index,item){
				col_name_arrays.push(item.title);
				col_model_arrays.push({
					name : item.expression,
					index : item.expression,
					sortable: false,
					width : 150, aligh:'center',
					cellattr: function(){
						return pv_cellCssFormat(item.expression);
					}
				});
			});

			<%--2020.03.05 처음 로딩후에 계층컬럼구분같은 기능을 사용하여 컬럼명이 변경되었음에도 기존 컬럼명을 유지 하여 차트 범례가 안나오는 현상 수정 - 김경호--%>
			$('#'+tbl_id).jqGrid('GridUnload');

	    	$('#'+tbl_id).jqGrid({
				datatype: 'local',
				useColSpanStyle : true,
	    	   	colNames:col_name_arrays,
	    	   	colModel:col_model_arrays,
	    	   	scrollrows:true,
	    	   	height: 600,
	    	   	width : g_otherChartWidth,
	    	  	gridview: true,
	    		rowNum: -1,
	    		<%--rowList: [5, 10, 20, 50],--%>
	    	   	loadonce: true,
	            viewrecords: false,
	    		shrinkToFit: false,	<%--가로 넓이 맞춰서 데이터 출력--%>
	    		sortable: false,
	    	   	loadui: false,		<%-- loading 표시안함 --%>
	    	    loadError:function(xhr, status, error){
	    	    	gfn_ajaxerror(error);
	    		},
	    		loadBeforeSend:function(data){
	    	    },
	    	   	gridComplete: function(){
	    	   	},
	    		loadComplete:function(data){
	    		}
	    	});
			gColNm = "";
	    	for(var i=0; i<col_name_arrays.length; i++){
	    		if(col_name_arrays[i] != ""){
		    		if(i < col_name_arrays.length-1){
		    			gColNm += col_name_arrays[i] + ","
		    		}else{
		    			gColNm += col_name_arrays[i]
		  	    	}
	    		}

	    	}
	    	$("#clsNm").text(gColNm);
	    	var data_cnt = g_remarkB.length;
	    	<%-- 챠트보기 숫자 콤보박스에 선택된 값에 따라 출력해줘야 함 연동대기 --%>

	    	<%--2020.02.27 차트범례 누적으로 인한 범레 출력 오류 수정 - 김경호 --%>
	    	$("#tbl_data_view").jqGrid('clearGridData');

	    	for(var i=0; i<chartViewCnt;i++){
	    		var obj = g_remarkB[i];

	    		$("#tbl_data_view").jqGrid('addRowData',i,g_remarkB[i]);
	    		$("#"+i+" td:eq(0)").css("color","#"+g_chartColor[i]);
	    		$("#"+i+" td:eq(0)").css("text-align","center");
	    		$("#"+i).css("font-weight","bold");
	    		$("#"+i).css("background-color","#FFFFF1");
	    		$("#"+i+" td").css("border-right","1px solid #000000");
	    		$("#"+i+" td").css("border-bottom","1px solid #000000");
	    	}

	    	$("#popMode").css("display","none");
	    	$("#popMode").css("height","0px");
	    	$("#htmlGrid").css("margin-top","0");
	    	$("#htmlGrid").css("height","0px");

			$(".ui-jqgrid-bdiv").css("height","206px");
			$("#Legend").css("top","1px");
			$("#Legend").css("height","240px");
			$("#Legend").css("visibility","visible");
			$("#gbox_tbl_data_view").css("border-top,border-right","1px solid #b1b1b1");
			$(".legend").css("margin-top","7px");
			<%-- 챠트 범례 jqGrid 수정 후 htmlGrid border 겹치는 현상으로 border 값 초기화--%>
			$("#htmlGrid").css("border","0px");
		}

		function pv_cellCssFormat(col_id){
			 var style = '';

			if(col_id == 'title'){
				style = 'style="background-color:#EEFFF5; font-weight:bold; color:#72777b;"';
			}else if(col_id.indexOf('tr')>-1){
				style = 'style="background-color:#FFFFF1;"';
			}else if(col_id.indexOf('ax')>-1){
				style = 'style="background-color:#FFFFF1;"';
			}
			return style;
		}

		function fn_chartColorSet(){
			g_chartColor[0] = "AFD8F8";
			g_chartColor[1] = "F6BD0F";
			g_chartColor[2] = "8BBA00";
			g_chartColor[3] = "FF8E46";
			g_chartColor[4] = "008E8E";
			g_chartColor[5] = "D64646";
			g_chartColor[6] = "8E468E";
			g_chartColor[7] = "588526";
			g_chartColor[8] = "B3AA00";
			g_chartColor[9] = "008ED6";
			g_chartColor[10] = "9D080D";
			g_chartColor[11] = "A186BE";
			g_chartColor[12] = "CC6600";
			g_chartColor[13] = "FDC689";
			g_chartColor[14] = "ABA000";
			g_chartColor[15] = "F26D7D";
			g_chartColor[16] = "FFF200";
			g_chartColor[17] = "0054A6";
			g_chartColor[18] = "F7941C";
			g_chartColor[19] = "CC3300";
		}

		function fn_chartDisable(){

			if(g_chartActive =="Y"){
				$('#Legned').empty();

				var legendHtml = "";
				legendHtml +='<div class="legend"><p id="liClsNm" class="right"><pivot:msg code="ui.label.chart.class" /><span id="clsNm" style="color:blue"></span></p><span class="f_bold"><pivot:msg code="ui.label.chart.legend" /><br/><pivot:msg code="ui.label.chart.legend2" /></span></div>';

				$('#Legend').html(legendHtml);
				$('#Legend').css("visibility","hidden");
				$('#Legend').css("top","1px");
				$('#Legend').css("height","0px");
			}
		}

		<%--
		/************************************************************************
		함수명 : adjustThtmlGrid()
		설   명 : ThtmlGrid 그리드 위치잡기
		 ************************************************************************/
		--%>
		function adjustThtmlGrid(){
			var x = $("#htmlGrid").position().left + "px";
			var y = $("#htmlGrid").position().top + "px";

			<%-- 2017.11.06 항목,분류,시점에서 하단 스크롤바 이동후 조회시 틀고정 틀어지는 문제 수정 - 김경호 --%>
			if($("#htmlGrid").position().left < 0){
				x = "0px";
			}

			$("#ThtmlGrid").css("left",x);	<%--div 위치 잡기--%>
			$("#ThtmlGrid").css("top",y);

			$("#ThtmlGrid").css("display","block");
			var mainTableT_H = $("#mainTableT").height();
			$("#ThtmlGrid").css("height",mainTableT_H);

			$("#ThtmlGrid").scrollLeft($("#htmlGrid").scrollLeft()); <%-- 통계표의 가로 스크롤 위치와 틀고정되는 표두와의 스크롤 위치를 다시 맞춰준다...--%>
		}

		<%--
		/************************************************************************
		함수명 : fn_timeAllSelect()
		설   명 : 시점에서의 전체선택 및 해제
		 ************************************************************************/
		--%>
		function fn_timeAllSelect(val){
			$('[name=timeChk'+val+']').each(function(index){
				if($("input:checkbox[id='selectAll"+val+"']").is(":checked")){
					if(!$(this).is(":checked")){
						$(this).prop('checked', true);
						fn_timeCountChk(this, val);
					}
				}else{
					if($(this).is(":checked")){
						$(this).prop('checked', false);
						fn_timeCountChk(this, val);
					}
				}
			});
		}
		<%--
		/************************************************************************
		함수명 : openKosis()
		설   명 : kosis 새창 열기
		 ************************************************************************/
		--%>
		function openKosis(){
			var cw = screen.availWidth;
			var ch = screen.availHeight;

			window.open("http://kosis.kr", "KOSIS", "width="+cw+",height="+ch +" , location=yes, status=yes, directories=yes, menubar=yes, toolbar=yes, scrollbars=yes, resizable=yes");
		}

		<%--
		/************************************************************************
		함수명 : chartDown()
		설   명 : 차트이미지 다운로드 버튼 이벤트
		 ************************************************************************/
		--%>
		function chartDown(){
			var format='JPG';
			chartInstance.exportChart({
				exportFormat: format
			});
		};

		<%--
		/************************************************************************
		함수명 : chartPrint()
		설   명 : 차트다운로드 이벤트
		 ************************************************************************/
		--%>
    	function chartPrint(){
    		var features = "menubar=no,toolbar=no,location=no,directories=no,status=no,scrollbars=yes,resizable=yes,width=1100,height=600,left=0,top=0";
    		var printPage = window.open("about:blank", "",features);

    		printPage.document.open();
    		printPage.document.write("<html><head><title></title><style type='text/css'>body,tr,td,input,textarea{font-family:Tahoma;Font-size:9pt;}</style>\n</head>\n<body>"+Chartcontent.innerHTML+"<br>"+liClsNm.innerHTML+"\n</body></html>");
    		printPage.document.close();
    		printPage.print();
    		printPage.location.reload();
    	}

		function rowClick(){
			$("#mainTable").children("tbody").children("tr").removeClass("rowClick");

			$(this).addClass("rowClick");
		}

		function openStat() {
		    var s_status ;
			s_status = "location=yes,toolbar=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=1024,height=768";
			window.open( "https://kosis.kr/statisticsList/statisticsListIndex.do?publicationYN=Y&statId=${statInfo.statId}","",s_status);
		}

		function fn_addBookmark(){
		    var bookmarkURL = window.location.href;
		    var bookmarkTitle = document.title;
		    var triggerDefault = false;

		    if (window.sidebar && window.sidebar.addPanel) {
		        // Firefox version &lt; 23
		        window.sidebar.addPanel(bookmarkTitle, bookmarkURL, '');
		    } else if ((window.sidebar && (navigator.userAgent.toLowerCase().indexOf('firefox') < -1)) || (window.opera && window.print)) {
		        // Firefox version &gt;= 23 and Opera Hotlist
		        var $this = $(this);
		        $this.attr('href', bookmarkURL);
		        $this.attr('title', bookmarkTitle);
		        $this.attr('rel', 'sidebar');
		        $this.off(e);
		        triggerDefault = true;
		    } else if (window.external && ('AddFavorite' in window.external)) {
		        // IE Favorite
		        window.external.AddFavorite(bookmarkURL, bookmarkTitle);
		    } else {
		        // WebKit - Safari/Chrome
		        alert((navigator.userAgent.toLowerCase().indexOf('mac') != -1 ? 'Cmd' : 'Ctrl') + '+D 를 이용해 이 페이지를 즐겨찾기에 추가할 수 있습니다.');
		        window.open(bookmarkURL,"_blank");
		    }
		}
//]]>
</script>
	<form:form commandName="ParamInfo" name="ParamInfo" method="post">
		<form:hidden path="orgId"></form:hidden>
		<form:hidden path="tblId"></form:hidden>
		<form:hidden path="language" />
		<input type="hidden" id="file" name="file" value="" />
		<form:hidden path="analText" />
		<form:hidden path="scrId" />
		<form:hidden path="fieldList" />
		<form:hidden path="colAxis" />
		<form:hidden path="rowAxis" />
		<form:hidden path="isFirst" />
		<form:hidden path="contextPath" />
		<form:hidden path="ordColIdx" />
		<form:hidden path="ordType" />
		<form:hidden path="logSeq" />
		<form:hidden path="vwCd" />
		<form:hidden path="listId" />
		<form:hidden path="connPath" />
		<form:hidden path="statId" />
		<form:hidden path="pub" />
		<form:hidden path="pubLog" />
		<form:hidden path="viewKind" />
		<form:hidden path="viewSubKind" />
		<form:hidden path="doAnal" />
		<form:hidden path="analType" />
		<form:hidden path="analCmpr" />
		<form:hidden path="analTime" />
		<form:hidden path="analCombo" />
		<form:hidden path="originData" />
		<form:hidden path="analClass" />
		<form:hidden path="analItem" />
		<form:hidden path="obj_var_id" />
		<form:hidden path="itm_id" />
		<form:hidden path="mode" />
		<form:hidden path="dataOpt" />
		<form:hidden path="noSelect" />
		<form:hidden path="view" />
		<!-- 통계표주석여부 및 보기 값 추가 -->
		<input type="hidden" name="existStblCmmtKor" id="existStblCmmtKor" value="${statInfo.existStblCmmtKor}" />
		<input type="hidden" name="existStblCmmtEng" id="existStblCmmtEng" value="${statInfo.existStblCmmtEng}" />
		<!-- 선택정보 전체보기//분석 탭메뉴 objvarId -->
		<input type="hidden" id="classAllArr" name="classAllArr" value="" />
		<!-- 일괄설정 objVarId -->
		<input type="hidden" id="classSet" name="classSet" value="" />
		<!-- 선택정보 전체보기를 click할 경우 java 분기 flag -->
		<input type="hidden" id="selectAllFlag" name="selectAllFlag" value="" />
		<!-- 조회범위 상세설정에 따른 시점카운트 -->
		<input type="hidden" id="selectTimeRange" name="selectTimeRangeCnt" value="" />
		<!-- 조회범위 상세설정 주기문자열 -->
		<input type="hidden" id="periodStr" name="periodStr" value="${statInfo.periodStr}" />
		<!-- 분석설정 호출시 화면에서 선택된 1개의 주기-->
		<form:hidden path="funcPrdSe" />
		<input type="hidden" name="tblNm" id="tblNm" value="${statInfo.tblNm}" />
		<input type="hidden" name="tblEngNm" id="tblEngNm" value="${statInfo.tblEngNm}" />
		<input type="hidden" name="isChangedDataOpt" id="isChangedDataOpt" value="" />
		<form:hidden path="itemMultiply" />
		<form:hidden path="dimCo" />
		<%-- 2014.01.10 dbUser, 및 부가기능설정에 관련된 파라미터 추가 --%>
		<form:hidden path="dbUser" />
		<input type="hidden" name="usePivot" id="usePivot" value="N" />
		<input type="hidden" name="isChangedTableType" id="isChangedTableType" value="N" />
		<input type="hidden" name="isChangedPeriodCo" id="isChangedPeriodCo" value="N" />
		<input type="hidden" name="isChangedPrdSort" id="isChangedPrdSort" value="N" />
		<%-- 2014.03.25 분류별 전체선택/분류별 레벨별 전체선택 value값 세팅 ifr_searchRangeDetail.jsp onLoad함수에 상태 확인--%>
		<input type="hidden" id="p_chkStatus" name="p_chkStatus" value="" />
		<input type="hidden" id="p_objVarId" name="p_objVarId" value="" />
		<input type="hidden" id="p_lvl" name="p_lvl" value="" />
		<input type="hidden" id="p_logicFlag" name="p_logicFlag" value="" />
		<%--전체레벨 해제:N --%>
		<input type="hidden" id="p_classAllChkYn" name="p_classAllChkYn" value="N" />
		<%--일괄설정과 체크박스 구분 --%>
		<input type="hidden" id="p_classAllSelectYn" name="p_classAllSelectYn" value="N" />
		<form:hidden path="useAddFuncLog" />
		<form:hidden path="chargerLvl" />
		<form:hidden path="st" />
		<%-- 2014.05.02 새창열기시에 기존 셋팅값과 동일하게 보여주기 위해 새창인지 구별할수있는 파라미터 추가 --%>
		<form:hidden path="new_win" />
		<%-- 2014.06.19 처음으로 열었는지 체크 - 김경호 --%>
		<form:hidden path="first_open" />
		<input type="hidden" name="debug" id="debug" value="" />
		<form:hidden path="maxCellOver" />
		<form:hidden path="reqCellCnt" />
		<%--상속통계표 추가 --%>
		<form:hidden path="inheritYn" />
		<form:hidden path="originOrgId" />
		<form:hidden path="originTblId" />
		<form:hidden path="pubSeType" />
		<%-- 2015.08.04 관련통계표의 팝업으로 조회된 경우 원통계표의 정보 - 강혜림 --%>
		<form:hidden path="relChkOrgId" />
		<form:hidden path="relChkTblId" />
		<!-- 조회형식 (그리드, 파일다운로드-종류별) -->
		<c:if test="${ParamInfo.mode != 'tab'}">
			<c:choose>
				<c:when test="${ParamInfo.viewType == 'H'}">
					<div id="topTitleKo">
						<span class="logo"><img src="images/dbsearchTitle.gif"
							alt="" /></span> <span class="textarea"><img
							src="images/ico_h.gif" alt="" class="vtp" /> <c:choose>
								<c:when test="${ParamInfo.chargerLvl eq 1}"> ${ParamInfo.empNm}(${ParamInfo.empId}) 담당</c:when>
								<c:otherwise> ${statInfo.orgNm}</c:otherwise>
							</c:choose> </span>
					</div>
				</c:when>

				<c:when test="${ParamInfo.viewType == 'S'}">
					<div id="topTitleKo">
						<span class="logo"><img src="images/dbsearchTitle.gif"
							alt="" /></span> <span class="textarea"><img
							src="images/ico_s.gif" alt="" class="vtp" /> <c:choose>
								<c:when test="${ParamInfo.chargerLvl eq 1}"> ${ParamInfo.empNm}(${ParamInfo.empId}) 담당</c:when>
								<c:otherwise> ${statInfo.orgNm}</c:otherwise>
							</c:choose> </span>
					</div>
				</c:when>

				<c:when test="${ParamInfo.viewType == 'B' }">
					<div id="topTitleKo">
						<span class="logo"><img src="images/dbsearchTitle.gif"
							alt="" /></span> <span class="textarea"><img
							src="images/ico_h.gif" alt="" class="vtp" /> <c:if
								test="${ParamInfo.chargerLvl eq 1}"> ${ParamInfo.empNm}(${ParamInfo.empId}) 담당</c:if></span>
					</div>
				</c:when>

				<c:otherwise>
					<c:if test="${ParamInfo.language == 'ko' && ParamInfo.mode != 'noLogo'}">
						<div id="topTitleKo">
							<p class="logo">
								<a href="javascript:openKosis();"><img src="images/kosisTitle.gif" alt="open KOSIS" /></a>
							</p>
						</div>
					</c:if>
					<c:if test="${ParamInfo.language == 'en' && ParamInfo.mode != 'noLogo'}">
						<div id="topTitleEn">
							<p class="logo">
								<a href="javascript:openKosis();"><img src="images/kosisTitle.gif" alt="open KOSIS" /></a>
							</p>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>

			<%-- <c:if test="${fn:indexOf(statInfo.serverType, 'stat') >=0 && fn:indexOf(statInfo.serverUrl, 'kosis.kr') < 0}">
				<a href="http://statdb.nsi.go.kr/nsi/inmg/sch/sch_index.jsp" id="kosis"></a>
			</c:if> --%>
		</c:if>
		<div id="wrap">
			<!-- 모달 div-->
			<div id="modal"></div>
			<!-- progress Bar-->
			<div id="modal2"></div>
			<div id="popup_outer">
				<!-- popup -->
				<!-- 피봇설정 -->
				<%@ include file="include/pivotInfo.jsp"%>
				<!-- 부가기능 -->
				<%@ include file="include/addfunc.jsp"%>
				<!-- 주석전체보기 -->
				<%@ include file="include/cmmtInfoAll.jsp"%>
				<!-- 해당주석보기 -->
				<%@ include file="include/cmmtInfo.jsp"%>
				<!-- 파일다운로드 -->
				<%@ include file="include/downGrid.jsp"%>
				<!-- 10,000 셀 초과 200,000셀 이하 파일다운로드 -->
				<%@ include file="include/downLarge.jsp"%>
				<!-- 스크랩 -->
				<%@ include file="include/myscrap.jsp"%>
				<!-- 선택정보 전체보기 -->
				<%@ include file="include/selectAll.jsp"%>
				<!-- 조회범위 상세설정 -->
				<%@ include file="include/searchRangeDetail.jsp"%>
				<!-- 주소보기 -->
				<%@ include file="include/url.jsp"%>
				<!-- 분석설정-->
				<%@ include file="include/assayInfo.jsp"%>
				<!-- 관련통계표-->
				<%@ include file="include/relationInfo.jsp"%>
				<!-- 계층컬럼구분 -->
				<%@ include file="include/levelExprHelp.jsp"%>
				<!-- 상위레벨표시 -->
				<%@ include file="include/parentLevelHelp.jsp"%>
				<!-- 전체선택안내 -->
				<%@ include file="include/classLvlAllChkHelp.jsp"%>
				<!-- 출처더보기-->
				<%@ include file="include/statInfo.jsp"%>
			</div>
			<!-- Top -->
			<div id="header">
				<!-- 20131217 -->
				<div class="titleCenter">
					<div class="titleLeft">
						<div class="titleRight">
							<h1 class="title">
								<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0 && statInfo.existStblCmmtKor == 'Y'}">
									<span class="sup"> <a onclick='setCmmtPosition(event)' href="javascript:popupCmmt('1#1210610#@null@#@null@#@null@')" name="popupCmmt1">
										<span class="h2_title">1)</span></a>
									</span>
								</c:if>
								<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0 && statInfo.existStblCmmtEng == 'Y'}">
									<span class="sup"> <a onclick='setCmmtPosition(event)' href="javascript:popupCmmt('1#1210610#@null@#@null@#@null@')" name="popupCmmt1">
										<span class="h2_title">1)</span></a>
									</span>
								</c:if>

								<a id="titleText"></a>
							</h1>
							<!-- 2013.12.20 영문모드일 경우 수정 -->
							<c:if test="${ParamInfo.language != 'en'}">
								<ul class="title_utill">
									<li><!-- 2017.08.11 호환성보기때문에 li추가... ul안에 li가 없을때가 있어서 오류라고 나옴  --></li>
									<%-- <c:if test="${statInfo.statLinkInfo.existMoreBtn}">
										<li class="none">
											<!-- 2015.01.06 파라미터중 kosisYn=Y 추가 -->
											<a href="javascript:openNewWin('${statInfo.statLinkInfo.linkStatComment}?orgId=${ParamInfo.orgId}&amp;confmNo=${statInfo.statLinkInfo.confmNo}&amp;kosisYn=Y', '6_1')" style="color: #000000">
												<pivot:msg code="ui.label.stat.desc" />
											</a>
										</li>
									</c:if> --%>
									<!-- 2018.07.17 외부망에서만 열리는 url이기때문에 통계표조회 url에 kosis.kr가 들어가는 서비스와 호스팅만 서비스 - 여인철주무관 -->
									<c:if test="${fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0 }">
										<c:if test="${statInfo.statLinkInfo.pubLink != null}">
											<li><a href="javascript:openNewWin('${statInfo.statLinkInfo.pubLink}', '6_2')" style="color: #000000"><pivot:msg code="ui.label.pub" /></a>
											</li>
										</c:if>

										<c:if test="${statInfo.statLinkInfo.newsLink != null}">
											<li>
												<a href="javascript:openNewWin('${statInfo.statLinkInfo.newsLink}', '6_3')" style="color: #000000">
													<pivot:msg code="ui.label.press" />
												</a>
											</li>
										</c:if>
									</c:if>
									<!-- 2015.07.17 관련통계표가 존재하고 properties에서 사용(Y)으로 되어있어야 사용가능 -->
									<c:if test="${ParamInfo.relYn == 'Y'&& ParamInfo.relUserYn == 'Y' || ParamInfo.relUserYn == 'y'}">
										<li>
											<a href="javascript:popupControl('pop_relGrid','show','modal')" style="color: #000000">
												<pivot:msg code="ui.lable.rel" />
											</a>
										</li>
									</c:if>

								</ul>
							</c:if>
						</div>
					</div>
				</div>
				<!-- 2013.12.20 출처 수정 -->
				<p class="more">
					<c:if test="${statInfo.statLinkInfo.existStatNm}">
						<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
									출처 :
						</c:if>
						${statInfo.statLinkInfo.exprStatNm}



						<c:if test="${statInfo.statLinkInfo.existMoreBtn}">

							<a href="javascript:popupControl('pop_statGrid','show','modal')" style="color: #000000">
								<c:choose>
									<c:when test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
										<img src="images/btn_more.gif" alt="<pivot:msg code="ui.btn.more"/>" class="moreClass" title="<pivot:msg code="ui.btn.more"/>" />
									</c:when>
									<c:otherwise>
										<img src="images/btn_more_en.gif" alt="<pivot:msg code="ui.btn.more"/>" class="moreClass" title="<pivot:msg code="ui.btn.more"/>" />
									</c:otherwise>
								</c:choose>
							</a>
						</c:if>
					</c:if>
				</p>
			</div>

			<!-- //Top -->
			<div id="content" class="content">
				<div id="disPlayBox" style="display: none;">
					<img src='images/processing_eng.gif' alt="Loading image" />
				</div>
				<!-- 2013.12.20 문의처 수정, 2014.02.04 자료갱신일 -->
				<h2 class="bu_2circle">
					<c:if test="${statInfo.renewalDate != null}">
						<pivot:msg code="ui.label.update.date" />: ${statInfo.renewalDate} /
					</c:if>
					<pivot:msg code="ui.label.term" />: ${statInfo.containPeriod}
					<c:if test="${statInfo.statLinkInfo.existInquire}">
						<c:choose>
							<c:when test="${statInfo.statLinkInfo.existInquireLink}">
								<a href="javascript:openInquire('${statInfo.statLinkInfo.urlCn}')"
									class="aUnderLine" style="color: #000000"> / 자료문의처 :
									${statInfo.statLinkInfo.exprInquire} </a><!-- / 시스템문의처 : 02-2012-9114  자료문의처 길은것 때문에 일단 보류-->
							</c:when>
							<c:otherwise>
						/ 자료문의처 : ${statInfo.statLinkInfo.exprInquire}<!--   / 시스템문의처 : 02-2012-9114 -->
							</c:otherwise>
						</c:choose>
					</c:if>
				</h2>

				<%-- 직접다운로드 링크 --%>
				<div id="directMenu">
					<c:if test="${statInfo.directYn == 'Y'}">
						<!-- 통계표 직접다운로드 -->
						<p class="bigGreen">통계표 직접다운로드</p>
						<dl class="ex_text">
							<dd>이 통계표는 파일 다운로드만 제공하는 통계표로 아래 사항을 설정한 후 다운로드 받으시기 바랍니다.</dd>
						</dl>

						<div class="tableLayer">
							<iframe
								style="width: 682px; height: 420px; background-color: #fff; padding: 0px;"
								src="directDownDiv.do?orgId=${ParamInfo.orgId}&tblId=${ParamInfo.tblId}&vwCd=${ParamInfo.vwCd}&listId=${ParamInfo.listId}&pub=${ParamInfo.chargerLvl}&dbUser=${ParamInfo.dbUser}&st=${ParamInfo.st}&process=statHtml"
								scrolling="no" frameborder="0"> </iframe>
						</div>
						<!-- //통계표 직접다운로드 끝 -->

						<c:if test="${statInfo.massYn == 'Y'}">
							<!-- 통계표 파일 서비스 -->
							<p class="bigGreen">통계표 파일서비스</p>
							<div class="fileService">
								<span class="smallGreen">자료량이 많은 통계표로 미리 생성한 파일을 제공합니다.</span>
									<a href="javascript:openMass('K');"><img src="images/shortcutBtn.gif" alt="바로가기" /></a>
							</div>
							<!-- //통계표 파일 서비스 끝 -->
						</c:if>
					</c:if>
				</div>

				<div id="tabMenu" class="tabMu">
					<!-- 					<p class="detailBtn">  -->
					<p class="leftBtn">
						<a href="javascript:popupControl('pop_detailfunc', 'show', 'modal')">
							<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
								<img src="images/detailSetBtn.gif" alt="<pivot:msg code="ui.label.detail"/>" title="<pivot:msg code="ui.label.detail"/>" />
							</c:if> <c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
								<img src="images/detailSetBtn_en.gif" alt="<pivot:msg code="ui.label.detail"/>" title="<pivot:msg code="ui.label.detail"/>" />
							</c:if>
						</a>
					</p>
					<ul class="selection1">
						<%-- 2013.12.17 CHAR_ITM_CO에서 공표구분 항목 수 적용 --%>
						<%-- 2015.02.04 시가 조회화면처럼 항목탭이 나올 수 있도록 수정 요청 - 이원영주무관 --%>

						<li id="tabItemText" class="menu_off" onclick="fn_disPlay('tabItemText',0);" style="cursor: pointer;">
							<input name="naviInfo" type="hidden" value="tabItemText" /> <span></span>
						</li>
						<c:forEach items="${statInfo.classInfoList}" var="tabClass" varStatus="status">
							<c:if test="${tabClass.visible == true}">
								<li id="tabClassText_${status.count}" class="menu_off"
									value="${tabClass.classId}"
									onclick="fn_disPlay('tabClassText_${status.count}','${status.count}');"
									style="cursor: pointer;"><input name="naviInfo"
									type="hidden" value="${tabClass.classId}" /> <span></span></li>
							</c:if>
						</c:forEach>

						<!--  무조건 있음 -->
						<li id="tabTimeText" class="menu_off">
							<input name="naviInfo" type="hidden" value="tabTimeText" />
							<a href="javascript:fn_disPlay('tabTimeText',${fn:length(statInfo.classInfoList)+1})"></a>
							<img id="sortImg" src="images/ico_time_up.png" style="padding: 0 0 0 10px; cursor: pointer;" onclick="fn_time_sort(0);" alt="<pivot:msg code="ui.label.asc" />" title="<pivot:msg code="ui.label.asc"/>" />
							<img id="sortImg2" src="images/ico_time_down.png" style="cursor: pointer;" onclick="fn_time_sort(1);" alt="<pivot:msg code="ui.label.desc" />" title="<pivot:msg code="ui.label.desc"/>" />
						</li>
					</ul>

					<p class="rightBtn">
						<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
							<img id="searchImg1" src="images/btn_tableSearch.gif"
								alt="<pivot:msg code="ui.label.btnSearch"/>"
								title="<pivot:msg code="ui.label.btnSearch"/>"
								onclick="fn_search();" />
						</c:if>
						<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
							<img id="searchImg1" src="images/btn_tableSearch_en.gif"
								alt="<pivot:msg code="ui.label.btnSearch"/>"
								title="<pivot:msg code="ui.label.btnSearch"/>"
								onclick="fn_search();" />
						</c:if>
						<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
							<img id="searchImg2" src="images/btn_downLoad.gif"
								alt="<pivot:msg code="ui.label.btnDownload"/>"
								title="<pivot:msg code="ui.label.btnDownload"/>"
								onclick="fn_downLarge();" style="display: none" />
						</c:if>
						<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
							<img id="searchImg2" src="images/btn_downLoad_en.gif"
								alt="<pivot:msg code="ui.label.btnDownload"/>"
								title="<pivot:msg code="ui.label.btnDownload"/>"
								onclick="fn_downLarge();" style="display: none" />
						</c:if>
					</p>
				</div>

				<div id="changeAttribute" class="cont_line">
					<div class="btnIcon" id="btnShow" style="display: inline-block">
						<%--2013.01.16 --%>
						<c:if test="${statInfo.unitNm != null }">
							<p id="stblUnit" class="text" style="width: 18%;">
								<c:choose>
									<c:when test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0}">(Unit:</c:when>
									<c:otherwise>(단위 :</c:otherwise>
								</c:choose>
								<span id="tblUnit" title="${statInfo.unitNm}">${statInfo.unitNm}</span>)
							</p>
						</c:if>
						<p class="text" style="width: 30%;">
							<span id="analysisText"></span>
						</p>
						<!-- 통계표목록 버튼 S -->
						<div class="BtnGroup">
							<button id="btn000" type="button" class="Btn_scrap" onclick="fn_addBookmark();" alt="<pivot:msg code="ui.label.bookmark"/>" title="<pivot:msg code="ui.label.bookmark"/>"><pivot:msg code="ui.label.bookmark"/></button>
						<c:if test="${ParamInfo.mode == 'tab'}">
							<button id="btn001" type="button" class="Btn_newwin" onclick="fn_newWindow();" alt="<pivot:msg code="ui.label.newwin"/>" title="<pivot:msg code="ui.label.newwin"/>"><pivot:msg code="ui.label.newwin"/></button>
						</c:if>
							<button id="btn002" style="display:none"; type="button" class="Btn_comment" onclick="popupControl('pop_cmmtInfoAll','show','modal');" alt="<pivot:msg code="ui.btn.cmmt"/>" title="<pivot:msg code="ui.btn.cmmt"/>" ><pivot:msg code="ui.btn.cmmt"/></button>
						<c:if test="${statInfo.downloadable}">
							<button id="btn003" type="button" class="Btn_url" onclick="popupControl('pop_url', 'show', 'modal');" alt="<pivot:msg code="ui.btn.url"/>" title="<pivot:msg code="ui.btn.url"/>" ><pivot:msg code="ui.btn.url"/></button>
						</c:if>
							<span class="Partition">구분선</span>
							<button id="ico_swap" type="button" class="Btn_swap" onclick="popupControl('pop_pivotfunc', 'show', 'modal');" alt="<pivot:msg code="ui.label.pivot"/>" title="<pivot:msg code="ui.label.pivot"/>" ><pivot:msg code="ui.label.pivot"/></button>
							<button id="ico_analysis" type="button" class="Btn_analysis <c:if test="${statInfo.analyzable != 'true'}">off</c:if>"  <c:if test="${statInfo.analyzable == 'true'}">onclick="popupControl('pop_assay','show','modal');"</c:if> alt="<pivot:msg code="ui.label.analysis"/>" title="<pivot:msg code="ui.label.analysis"/>" >
								<pivot:msg code="ui.label.analysis"/>
							</button>
							<button id="chartEnable" type="button" class="Btn_chart" onclick="fn_fusionChartCtrl();" alt="<pivot:msg code="ui.label.chart"/>" title="<pivot:msg code="ui.label.chart"/>" >
								<pivot:msg code="ui.label.chart"/>
							</button>
							<button id="ico_addfunc" type="button" class="Btn_setting" onclick="popupControl('pop_addfunc', 'show', 'modal');" alt="<pivot:msg code="ui.label.optionSetting"/>" title="<pivot:msg code="ui.label.optionSetting"/>" ><pivot:msg code="ui.label.optionSetting"/></button>
							<%-- 스크랩 영문모드인경우 제외 --%>
							<%-- <c:if test="${fn:indexOf(statInfo.serverType, 'service') >=0 && fn:indexOf(statInfo.serverUrl, 'kosis.kr') >=0 && ParamInfo.language != 'en'}">
								<button id="ico_myscrap" type="button" class="Btn_scrap <c:if test="${statInfo.downloadable != 'true'}">off</c:if>" <c:if test="${statInfo.downloadable == 'true'}">onclick="popupControl('pop_myscrap', 'show', 'modal');"</c:if> alt="<pivot:msg code="ui.label.clipping"/>" title="<pivot:msg code="ui.label.clipping"/>" ><pivot:msg code="ui.label.clipping"/></button>
							</c:if> --%>
							<button id="ico_download" type="button" class="Btn_download <c:if test="${statInfo.downloadable != 'true'}">off</c:if>" <c:if test="${statInfo.downloadable == 'true'}">onclick="popupControl('pop_downgrid', 'show', 'modal');"</c:if>alt="<pivot:msg code="ui.label.download"/>" title="<pivot:msg code="ui.label.download"/>" ><pivot:msg code="ui.label.download"/></button>
							<button id="ico_print" type="button" class="Btn_print" onclick="popupPrint();" alt="<pivot:msg code="ui.label.print"/>" title="<pivot:msg code="ui.label.print"/>" ><pivot:msg code="ui.label.print"/></button>
						<%-- <c:if test="${ParamInfo.language != 'en'}">
							<button id="btn011" type="button" class="Btn_info" onclick="downHelp();" alt="<pivot:msg code="ui.label.help"/>" title="<pivot:msg code="ui.label.help"/>"><pivot:msg code="ui.label.help"/></button>
						</c:if> --%>
						</div>
						<!-- 통계표목록 버튼 E -->
					</div>

					<div id="textShow" class="text_lay" style="display: none">
						<ul style="height: 21px;">
							<li class="text" style="padding-left: 10px;"><pivot:msg code="ui.label.selectDataItem" /></li>
							<li id="changeTextLi" class="text"></li>
							<li class="text">&nbsp;<pivot:msg code="ui.label.cell" /> <span class="f_bold"> [ </span>
								<span id="changeSpanRed">
									20,000
									(<pivot:msg code="ui.label.cond.search" />)
								</span> <span class="f_bold"> | </span>
								<span id="changeSpanGray">200,000
									(<pivot:msg code="ui.label.cond.download" />)
								</span> <span class="f_bold"> | </span>
								<span id="changeDownText"><pivot:msg code="ui.label.notDownloadable" /></span> <span class="f_bold">] </span>
							</li>
							<li class="btn_r">
								<a href="javascript:popupControl('pop_selectAll', 'show', 'modal')"><pivot:msg code="ui.label.selectInfo" /></a>
								<a href="javascript:fn_gridOpen();"><img src="images/charts/gbtn_close.bmp" alt="<pivot:msg code="ui.label.close" />" /></a>
							</li>
						</ul>
					</div>

					<div id="Divchart" class="chart" style="visibility: hidden; height: 0px;">
						<div class="chartTop">
							<div class="chartBtn">
								<a href="javascript:fn_fusionChartExcute('MSColumn3D');"><img
									src="images/charts/gbtn_bar01.bmp"
									alt="<pivot:msg code="ui.btn.chart.MSColumn3D"/>"
									title="<pivot:msg code="ui.btn.chart.MSColumn3D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('MsColumn2D');"><img
									src="images/charts/gbtn_bar03.bmp"
									alt="<pivot:msg code="ui.btn.chart.MsColumn2D"/>"
									title="<pivot:msg code="ui.btn.chart.MsColumn2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('ScrollColumn2D');"><img
									src="images/charts/gbtn_bar02.bmp"
									alt="<pivot:msg code="ui.btn.chart.ScrollColumn2D"/>"
									title="<pivot:msg code="ui.btn.chart.ScrollColumn2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('msline');"> <img
									src="images/charts/gbtn_line01.bmp"
									alt="<pivot:msg code="ui.btn.chart.3DLine"/>"
									title="<pivot:msg code="ui.btn.chart.3DLine"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('ScrollLine2D');"><img
									src="images/charts/gbtn_line02.bmp"
									alt="<pivot:msg code="ui.btn.chart.ScrollLine2D"/>"
									title="<pivot:msg code="ui.btn.chart.ScrollLine2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('StackedColumn2D');"><img
									src="images/charts/gbtn_bar06.bmp"
									alt="<pivot:msg code="ui.btn.chart.StackedColumn2D"/>"
									title="<pivot:msg code="ui.btn.chart.StackedColumn2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('StackedColumn3D');"><img
									src="images/charts/gbtn_bar05.bmp"
									alt="<pivot:msg code="ui.btn.chart.StackedColumn3D"/>"
									title="<pivot:msg code="ui.btn.chart.StackedColumn3D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('ScrollStackedColumn2D');"><img
									src="images/charts/gbtn_bar04.bmp"
									alt="<pivot:msg code="ui.btn.chart.ScrollStackedColumn2D"/>"
									title="<pivot:msg code="ui.btn.chart.ScrollStackedColumn2D"/>" /></a>
								<a href="javascript:fn_fusionChartExcute('MSBar2D');"><img
									src="images/charts/gbtn_vbar02.bmp"
									alt="<pivot:msg code="ui.btn.chart.MSBar2D"/>"
									title="<pivot:msg code="ui.btn.chart.MSBar2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('MSBar3D');"><img
									src="images/charts/gbtn_vbar01.bmp"
									alt="<pivot:msg code="ui.btn.chart.MSBar3D"/>"
									title="<pivot:msg code="ui.btn.chart.MSBar3D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('StackedBar2D');"><img
									src="images/charts/gbtn_vbar03.bmp"
									alt="<pivot:msg code="ui.btn.chart.StackedBar2D"/>"
									title="<pivot:msg code="ui.btn.chart.StackedBar2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('StackedBar3D');"><img
									src="images/charts/gbtn_vbar04.bmp"
									alt="<pivot:msg code="ui.btn.chart.StackedBar3D"/>"
									title="<pivot:msg code="ui.btn.chart.StackedBar3D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('MSArea');"> <img
									src="images/charts/gbtn_area01.bmp"
									alt="<pivot:msg code="ui.btn.chart.MSArea"/>"
									title="<pivot:msg code="ui.btn.chart.MSArea"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('ScrollArea2D');"><img
									src="images/charts/gbtn_area02.bmp"
									alt="<pivot:msg code="ui.btn.chart.ScrollArea2D"/>"
									title="<pivot:msg code="ui.btn.chart.ScrollArea2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('StackedArea2D');"><img
									src="images/charts/gbtn_area03.bmp"
									alt="<pivot:msg code="ui.btn.chart.StackedArea2D"/>"
									title="<pivot:msg code="ui.btn.chart.StackedArea2D"/>" /></a> <a
									href="javascript:fn_fusionChartExcute('Pie3D');"> <img
									src="images/charts/gbtn_pie.bmp"
									alt="<pivot:msg code="ui.btn.chart.Pie3D"/>"
									title="<pivot:msg code="ui.btn.chart.Pie3D"/>" /></a>

							</div>
								<div class="alignRight">
									<%-- 2019.08.05 차트 저장, 인쇄기능 추가 --%>
									<p class="PrintBtn">
										<button title="<pivot:msg code="ui.label.chartdown" />" id="export" class="ImageDownload" type="button" onclick="chartDown()"><pivot:msg code="ui.label.chartdown" /></button>
										<button title="<pivot:msg code="ui.label.chartprint" />" id="showPrint" class="GraphPrint" type="button" onclick="chartPrint()"><pivot:msg code="ui.label.chartprint" /></button>
									</p>
									<select id="chartViewCnt" title="${ParamInfo.language == 'en' ? 'View Count' : '조회 카운트'}" style="margin : 0 0 0 10px;">
										<c:forEach var="i" begin="1" end="20" step="1" varStatus="status">
											<option value="${i}" <c:if test="${i eq '5'}"> selected="selected"</c:if>>
												<c:out value="${status.count}" />
											</option>
										</c:forEach>
									</select>
									<a href="javascript:fn_chartClose();"><img src="images/charts/gbtn_close.${ParamInfo.language == 'en' || ParamInfo.dataOpt == 'en' ? 'bmp' : 'gif'}" alt="<pivot:msg code="ui.label.close" />" /></a>
								</div>
							<div class="alignText">
								<span id="saveCharts"></span>
							</div>
						</div>

						<div id="Chartcontent" style="display: none; margin: 2px 0 0 1px;"></div>
						<div id="Legend" class="remark" style="visibility: hidden; margin: -2px 0 0 -1px;">
							<div class="legend">
								<p id="liClsNm" class="right"><pivot:msg code="ui.label.chart.class" /><span id="clsNm" style="color:blue"></span></p>
								<span class="f_bold">
									<pivot:msg code="ui.label.chart.legend" />
									<br/><pivot:msg code="ui.label.chart.legend2" />
								</span>
							</div>
						</div>

						<!-- chart저장 추가 -->
					</div>

					<div id="jqGrid" class="remark" style="visibility: hidden; float: left; height: 0px; display:none;">
						 <table id="tbl_data_view" style="table-layout: fixed;"><tr><td></td></tr></table>
						<div class="legend">
							<span class="f_bold">
								<pivot:msg code="ui.label.chart.legend" />
								<br/><pivot:msg code="ui.label.chart.legend2" />
							</span>
						</div>
					</div>

					<div id="popMode" class="popMode">
						<div id="htmlGrid" class="inner"></div>
						<div id="ThtmlGrid" class="inner" style="display: none; position: absolute;"></div>
					</div>

					<c:forEach items="${statInfo.tabMenuList}" varStatus="status">
						<c:choose>
							<c:when test="${fn:length(statInfo.tabMenuList)-1 eq status.index}">
								<c:set var="divWidth" value="${fn:length(fn:split(statInfo.periodStr,'#'))*232}" />
								<!-- 시점div width -->
							</c:when>
							<c:when test="${statInfo.tabMenuList[status.index].objVarId eq '13999001'}">
								<!-- 항목div width-->
								<c:set var="divWidth" value="928" />
							</c:when>
							<c:otherwise>
								<c:set var="divWidth" value="${statInfo.classInfoList[status.index-1].depthLvl*308}" />
								<!-- 분류div width -->
							</c:otherwise>
						</c:choose>
						<div id="block${status.index}" class="block_definition" style="display: none;">
							<c:choose>
								<c:when test="${statInfo.tabMenuList[status.index].objVarId eq '13999001'}">
									<p class="h3_arrow">${statInfo.itemInfo.itmNm}</p>
								</c:when>
								<c:when test="${fn:length(statInfo.tabMenuList)-1 eq status.index}">
									<p class="h3_arrow">
										<pivot:msg code="ui.label.cond.time" />
									</p>
								</c:when>
								<c:otherwise>
									<p id="h3_arrow${status.index}" class="h3_arrow"></p>
								</c:otherwise>
							</c:choose>
							<div id="itmList${status.index}" class="cont_detail" style="width:${divWidth}px;">
								<c:choose>
									<c:when test="${fn:length(statInfo.tabMenuList)-1 eq status.index}">
										<c:forTokens var="time" items="${statInfo.periodStr}" delims="#">
											<!-- 처음 시점 정보 div 생성 갯수 -->
											<c:choose>
												<c:when test="${fn:indexOf(time,'D')>-1}">
													<div id="timeD" class="selectTimeBox" style="width: 224px;">
														<!-- 주기별 div세팅 해줘야함 -->
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkD" name="headCheck" value="D" type="checkbox" onclick="fn_Headenable('D');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<c:set var="textD" value="${statInfo.periodInfo.nameD}(${fn:substring(statInfo.periodInfo.startD,0,4)}.${fn:substring(statInfo.periodInfo.startD,4,6)}.${fn:substring(statInfo.periodInfo.startD,6,8)} ~
																				${fn:substring(statInfo.periodInfo.endD,0,4)}.${fn:substring(statInfo.periodInfo.endD,4,6)}.${fn:substring(statInfo.periodInfo.endD,6,8)})" />
																		<label for="checkD"> ${textD} </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkD" name="headCheck" value="D" type="checkbox" onclick="fn_Headenable('D');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkD">${statInfo.periodInfo.nameD} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkD">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${fn:substring(statInfo.periodInfo.startD,0,4)}.${fn:substring(statInfo.periodInfo.startD,4,6)}.${fn:substring(statInfo.periodInfo.startD,6,8)}
																			~
																			${fn:substring(statInfo.periodInfo.endD,0,4)}.${fn:substring(statInfo.periodInfo.endD,4,6)}.${fn:substring(statInfo.periodInfo.endD,6,8)})
																		</label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>

														<c:set var="timeLengthD"
															value="${fn:length(statInfo.periodInfo.listD)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('D');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listD}"
																	varStatus="status">
																	<c:set var="comboD" value="${statInfo.periodInfo.listD[timeLengthD - status.count]}" />
																	<option value="${comboD}"
																		<c:if test="${comboD eq statInfo.periodInfo.defaultStartD}"> selected="selected"</c:if>>
																		<c:out value="${fn:substring(comboD,0,4)}.${fn:substring(comboD,4,6)}.${fn:substring(comboD,6,8)}" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('D');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listD}" varStatus="status">
																	<c:set var="comboD" value="${statInfo.periodInfo.listD[timeLengthD - status.count]}" />
																	<option value="${comboD}"
																		<c:if test="${comboD eq statInfo.periodInfo.defaultEndD}"> selected="selected"</c:if>>
																		<c:out value="${fn:substring(comboD,0,4)}.${fn:substring(comboD,4,6)}.${fn:substring(comboD,6,8)}" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodD" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListD"
																	items="${statInfo.periodInfo.defaultListD}"
																	varStatus="status">
																	<li><input id="timeChD${status.index}"
																		type="checkbox" name="timeChkD"
																		onclick="fn_timeCountChk(this,'D');"
																		value="${timeListD}" title="${timeListD}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChD${status.index}"></label>
																	<c:out
																			value="${fn:substring(timeListD,0,4)}.${fn:substring(timeListD,4,6)}.${fn:substring(timeListD,6,8)}" />
																	</li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugD"></label>
																	<input id="ie7bugD" type="checkbox" style="visibility: hidden" name="ie7bug" value="ie7bug" />
																</li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllD" <c:if test="${fn:length(statInfo.periodInfo.defaultListD) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllD" value="selectAllD" checked="checked" onclick="fn_timeAllSelect('D')"/><label for="selectAllD"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>

												<c:when test="${fn:indexOf(time,'T')>-1}">
													<div id="timeT" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkT" name="headCheck" value="T"
																			type="checkbox" onclick="fn_Headenable('T');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<c:set var="textT"
																			value="${statInfo.periodInfo.nameT}(${statInfo.periodInfo.startT} ~ ${statInfo.periodInfo.endT})" />
																		<label for="checkT"> ${textT} </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkT" name="headCheck" value="T"
																			type="checkbox" onclick="fn_Headenable('T');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkT">
																			${statInfo.periodInfo.nameT} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkT">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${statInfo.periodInfo.startT}
																			~ ${statInfo.periodInfo.endT}) </label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>

														<c:set var="timeLengthT" value="${fn:length(statInfo.periodInfo.listT)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('T');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listT}"
																	varStatus="status">
																	<c:set var="comboT"
																		value="${statInfo.periodInfo.listT[timeLengthT - status.count]}" />
																	<option value="${comboT}"
																		<c:if test="${comboD eq statInfo.periodInfo.defaultStartT}"> selected="selected"</c:if>>
																		<c:out value="${comboT}" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('T');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listT}" varStatus="status">
																	<c:set var="comboT" value="${statInfo.periodInfo.listT[timeLengthT - status.count]}" />
																	<option value="${comboT}"
																		<c:if test="${comboT eq statInfo.periodInfo.defaultEndT}"> selected="selected"</c:if>>
																		<c:out value="${comboT}" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodT" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListT" items="${statInfo.periodInfo.defaultListT}"
																	varStatus="status">
																	<%--<c:set var="timeListT" value="${statInfo.periodInfo.listT[timeLengthT - status.count]}"/> --%>
																	<li><input id="timeChT${status.index}"
																		type="checkbox" name="timeChkT"
																		onclick="fn_timeCountChk(this,'T');"
																		value="${timeListT}" title="${timeListT}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChT${status.index}"></label>
																	<c:out value="${timeListT.prdDe}"></c:out></li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugT"></label> <input id="ie7bugT" type="checkbox" style="visibility: hidden" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllT" <c:if test="${fn:length(statInfo.periodInfo.defaultListT) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllT" value="selectAllT" checked="checked" onclick="fn_timeAllSelect('T')"/><label for="selectAllT"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>

												<c:when test="${fn:indexOf(time,'M')>-1}">
													<div id="timeM" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkM" name="headCheck" value="M"
																			type="checkbox" onclick="fn_Headenable('M');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />

																		<c:set var="textM"
																			value="${statInfo.periodInfo.nameM}(${fn:substring(statInfo.periodInfo.startM,0,4)}.${fn:substring(statInfo.periodInfo.startM,4,6)} ~
																				${fn:substring(statInfo.periodInfo.endM,0,4)}.${fn:substring(statInfo.periodInfo.endM,4,6)})" />
																		<label for="checkM"> ${textM} </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkM" name="headCheck" value="M"
																			type="checkbox" onclick="fn_Headenable('M');"
																			style="height: 17px;"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkM">
																			${statInfo.periodInfo.nameM} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkM">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${fn:substring(statInfo.periodInfo.startM,0,4)}.${fn:substring(statInfo.periodInfo.startM,4,6)}
																			~
																			${fn:substring(statInfo.periodInfo.endM,0,4)}.${fn:substring(statInfo.periodInfo.endM,4,6)})
																		</label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>
														<c:set var="timeLengthM"
															value="${fn:length(statInfo.periodInfo.listM)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('M');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listM}"
																	varStatus="status">
																	<c:set var="comboM"
																		value="${statInfo.periodInfo.listM[timeLengthM - status.count]}" />
																	<option value="${comboM}"
																		<c:if test="${comboM eq statInfo.periodInfo.defaultStartM}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboM,0,4)}.${fn:substring(comboM,4,6)}" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('M');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listM}"
																	varStatus="status">
																	<c:set var="comboM" value="${statInfo.periodInfo.listM[timeLengthM - status.count]}" />
																	<option value="${comboM}"
																		<c:if test="${comboM eq statInfo.periodInfo.defaultEndM}"> selected="selected"</c:if>>
																		<c:out value="${fn:substring(comboM,0,4)}.${fn:substring(comboM,4,6)}" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodM" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListM"
																	items="${statInfo.periodInfo.defaultListM}"
																	varStatus="status">
																	<%--<c:set var="timeListM" value="${statInfo.periodInfo.listM[timeLengthM - status.count]}"/> --%>
																	<li><input id="timeChM${status.index}"
																		type="checkbox" name="timeChkM"
																		onclick="fn_timeCountChk(this,'M');"
																		value="${timeListM}" title="${timeListM}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChM${status.index}"></label>
																	<c:out
																			value="${fn:substring(timeListM,0,4)}.${fn:substring(timeListM,4,6)}"></c:out>
																	</li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugM"></label> <input id="ie7bugM" type="checkbox" style="visibility: hidden" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllM" <c:if test="${fn:length(statInfo.periodInfo.defaultListM) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllM" value="selectAllM" checked="checked" onclick="fn_timeAllSelect('M')"/><label for="selectAllM"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>

												<c:when test="${fn:indexOf(time,'B')>-1}">
													<div id="timeB" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkB" name="headCheck" value="B"
																			type="checkbox" onclick="fn_Headenable('B');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<%-- <c:set var="textBK" value="격월"/>
																<c:set var="textBE" value=""/> --%>
																		<c:set var="textB"
																			value="${statInfo.periodInfo.nameB}(${fn:substring(statInfo.periodInfo.startB,0,4)}. ${fn:substring(statInfo.periodInfo.startQ,4,5)} ~
																					${fn:substring(statInfo.periodInfo.endB,0,4)}. ${fn:substring(statInfo.periodInfo.endQ,4,5)})" />
																		<label for="checkB"> ${textB} </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkB" name="headCheck" value="B"
																			type="checkbox" onclick="fn_Headenable('B');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkB">
																			${statInfo.periodInfo.nameB} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkB">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${fn:substring(statInfo.periodInfo.startB,0,4)}.
																			${fn:substring(statInfo.periodInfo.startQ,4,5)} ~
																			${fn:substring(statInfo.periodInfo.endB,0,4)}.
																			${fn:substring(statInfo.periodInfo.endQ,4,5)}) </label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>
														<c:set var="timeLengthB"
															value="${fn:length(statInfo.periodInfo.listB)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('B');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listB}"
																	varStatus="status">
																	<c:set var="comboB"
																		value="${statInfo.periodInfo.listB[timeLengthB - status.count]}" />
																	<option value="${comboB}"
																		<c:if test="${comboB eq statInfo.periodInfo.defaultStartB}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboB,0,4)}.${fn:substring(comboB,4,6)}" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('B');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listB}"
																	varStatus="status">
																	<c:set var="comboB"
																		value="${statInfo.periodInfo.listB[timeLengthB - status.count]}" />
																	<option value="${comboB}"
																		<c:if test="${comboB eq statInfo.periodInfo.defaultEndB}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboB,0,4)}.${fn:substring(comboB,4,6)}" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodB" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListB"
																	items="${statInfo.periodInfo.defaultListB}"
																	varStatus="status">
																	<li><input id="timeChB${status.index}"
																		type="checkbox" name="timeChkB"
																		onclick="fn_timeCountChk(this,'B');"
																		value="${timeListB}" title="${timeListB}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChB${status.index}"></label>
																	<c:out
																			value="${fn:substring(comboB,0,4)}.${fn:substring(comboB,4,6)}" />
																	</li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugB"></label> <input id="ie7bugB" type="checkbox" style="visibility: hidden" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllB" <c:if test="${fn:length(statInfo.periodInfo.defaultListB) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllB" value="selectAllB" checked="checked" onclick="fn_timeAllSelect('B')"/><label for="selectAllB"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>
												<c:when test="${fn:indexOf(time,'Q')>-1}">
													<div id="timeQ" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkQ" name="headCheck" value="Q"
																			type="checkbox" onclick="fn_Headenable('Q');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<c:set var="textQ"
																			value="${statInfo.periodInfo.nameQ}(${fn:substring(statInfo.periodInfo.startQ,0,4)} ${fn:substring(statInfo.periodInfo.startQ,5,6)}/4 ~
																					${fn:substring(statInfo.periodInfo.endQ,0,4)} ${fn:substring(statInfo.periodInfo.endQ,5,6)}/4)" />
																		<label for="checkQ"> ${textQ} </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkQ" name="headCheck" value="Q"
																			type="checkbox" onclick="fn_Headenable('Q');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkQ">
																			${statInfo.periodInfo.nameQ} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkQ">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${fn:substring(statInfo.periodInfo.startQ,0,4)}
																			${fn:substring(statInfo.periodInfo.startQ,5,6)}/4 ~
																			${fn:substring(statInfo.periodInfo.endQ,0,4)}
																			${fn:substring(statInfo.periodInfo.endQ,5,6)}/4) </label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>
														<c:set var="timeLengthQ"
															value="${fn:length(statInfo.periodInfo.listQ)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('Q');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listQ}"
																	varStatus="status">
																	<c:set var="comboQ"
																		value="${statInfo.periodInfo.listQ[timeLengthQ - status.count]}" />
																	<option value="${comboQ}"
																		<c:if test="${comboQ eq statInfo.periodInfo.defaultStartQ}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboQ,0,4)} ${fn:substring(comboQ,5,6)}/4" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('Q');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listQ}"
																	varStatus="status">
																	<c:set var="comboQ"
																		value="${statInfo.periodInfo.listQ[timeLengthQ - status.count]}" />
																	<option value="${comboQ}"
																		<c:if test="${comboQ eq statInfo.periodInfo.defaultEndQ}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboQ,0,4)} ${fn:substring(comboQ,5,6)}/4" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodQ" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListQ"
																	items="${statInfo.periodInfo.defaultListQ}"
																	varStatus="status">
																	<li><input id="timeChQ${status.index}"
																		type="checkbox" name="timeChkQ" value="${timeListQ}" title="${timeListQ}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChQ${status.index}"></label>
																	<c:out
																			value="${fn:substring(timeListQ,0,4)} ${fn:substring(timeListQ,5,6)}/4" />
																	</li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugQ"></label> <input id="ie7bugQ" type="checkbox" style="visibility: hidden" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllQ" <c:if test="${fn:length(statInfo.periodInfo.defaultListQ) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllQ" value="selectAllQ" checked="checked" onclick="fn_timeAllSelect('Q')"/><label for="selectAllQ"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>
												<c:when test="${fn:indexOf(time,'H')>-1}">
													<div id="timeH" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkH" name="headCheck" value="H"
																			type="checkbox" onclick="fn_Headenable('H');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<c:set var="textH"
																			value="${statInfo.periodInfo.nameH}(${fn:substring(statInfo.periodInfo.startH,0,4)} ${fn:substring(statInfo.periodInfo.startH,5,6)}/2 ~
																					${fn:substring(statInfo.periodInfo.endH,0,4)} ${fn:substring(statInfo.periodInfo.endH,5,6)}/2)" />
																		<label for="checkH"> ${textH} </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkH" name="headCheck" value="H"
																			type="checkbox" onclick="fn_Headenable('H');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkH">
																			${statInfo.periodInfo.nameH} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkH">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${fn:substring(statInfo.periodInfo.startH,0,4)}
																			${fn:substring(statInfo.periodInfo.startH,5,6)}/2 ~
																			${fn:substring(statInfo.periodInfo.endH,0,4)}
																			${fn:substring(statInfo.periodInfo.endH,5,6)}/2) </label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>
														<c:set var="timeLengthH"
															value="${fn:length(statInfo.periodInfo.listH)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('H');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listH}"
																	varStatus="status">
																	<c:set var="comboH"
																		value="${statInfo.periodInfo.listH[timeLengthH - status.count]}" />
																	<option value="${comboH}"
																		<c:if test="${comboH eq statInfo.periodInfo.defaultStartH}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboH,0,4)} ${fn:substring(comboH,5,6)}/2" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('H');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listH}"
																	varStatus="status">
																	<c:set var="comboH"
																		value="${statInfo.periodInfo.listH[timeLengthH - status.count]}" />
																	<option value="${comboH}"
																		<c:if test="${comboH eq statInfo.periodInfo.defaultEndH}"> selected="selected"</c:if>>
																		<c:out
																			value="${fn:substring(comboH,0,4)} ${fn:substring(comboH,5,6)}/2" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodH" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListH"
																	items="${statInfo.periodInfo.defaultListH}">
																	<li><input id="timeChH${status.index}"
																		type="checkbox" name="timeChkH"
																		onclick="fn_timeCountChk(this,'H');"
																		value="${timeListH}" title="${timeListH}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChH${status.index}"></label>
																	<c:out
																			value="${fn:substring(timeListH,0,4)} ${fn:substring(timeListH,5,6)}/2" />
																	</li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugH"></label> <input id="ie7bugH" type="checkbox" style="visibility: hidden" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllH" <c:if test="${fn:length(statInfo.periodInfo.defaultListH) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllH" value="selectAllH" checked="checked" onclick="fn_timeAllSelect('H')"/><label for="selectAllH"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>
												<c:when test="${fn:indexOf(time,'Y')>-1}">
													<div id="timeY" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkY" name="headCheck" value="Y"
																			type="checkbox" onclick="fn_Headenable('Y');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<c:set var="textY"
																			value="${statInfo.periodInfo.nameY}(${statInfo.periodInfo.startY} ~ ${statInfo.periodInfo.endY})" />
																		<label for="checkY">${textY}</label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkY" name="headCheck" value="Y"
																			type="checkbox" onclick="fn_Headenable('Y');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		${statInfo.periodInfo.nameY}
																	</p>
																	<p class="tit3">
																		<label for="checkY">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${statInfo.periodInfo.startY}
																			~ ${statInfo.periodInfo.endY}) </label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>
														<c:set var="timeLengthY"
															value="${fn:length(statInfo.periodInfo.listY)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('Y');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listY}"
																	varStatus="status">
																	<c:set var="comboY"
																		value="${statInfo.periodInfo.listY[timeLengthY - status.count]}" />
																	<option value="${comboY}"
																		<c:if test="${comboY eq statInfo.periodInfo.defaultStartY}"> selected="selected"</c:if>>
																		<c:out value="${comboY}" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('Y');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listY}"
																	varStatus="status">
																	<c:set var="comboY"
																		value="${statInfo.periodInfo.listY[timeLengthY - status.count]}" />
																	<option value="${comboY}"
																		<c:if test="${comboY eq statInfo.periodInfo.defaultEndY}"> selected="selected"</c:if>>
																		<c:out value="${comboY}" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodY" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListY"
																	items="${statInfo.periodInfo.defaultListY}"
																	varStatus="status">
																	<li><input type="checkbox"
																		id="timeChY${status.index}" name="timeChkY"
																		onclick="fn_timeCountChk(this,'Y');"
																		value="${timeListY}" title="${timeListY}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChY${status.index}"></label>
																	<c:out value="${timeListY}"></c:out></li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugY"></label> <input type="checkbox" style="visibility: hidden" id="ie7bugY" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllY" <c:if test="${fn:length(statInfo.periodInfo.defaultListY) == 0 }"> style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllY" value="selectAllY" checked="checked" onclick="fn_timeAllSelect('Y')"/><label for="selectAllY"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>
												<c:when test="${fn:indexOf(time,'F')>-1}">
													<div id="timeF" class="selectTimeBox" style="width: 224px;">
														<div class="top">
															<c:choose>
																<c:when
																	test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
																	<p class="tit">
																		<input id="checkF" name="headCheck" value="F"
																			type="checkbox" onclick="fn_Headenable('F');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkF">
																			${statInfo.periodInfo.nameF}(${statInfo.periodInfo.startF}
																			~ ${statInfo.periodInfo.endF}) </label>
																	</p>
																</c:when>
																<c:otherwise>
																	<p class="tit2">
																		<input id="checkF" name="headCheck" value="F"
																			type="checkbox" onclick="fn_Headenable('F');"
																			<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="checkF">
																			${statInfo.periodInfo.nameF} </label>
																	</p>
																	<p class="tit3">
																		<label for="checkF">
																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(${statInfo.periodInfo.startF}
																			~ ${statInfo.periodInfo.endF}) </label>
																	</p>
																</c:otherwise>
															</c:choose>
														</div>
														<c:set var="timeLengthF"
															value="${fn:length(statInfo.periodInfo.listF)}" />
														<h2 class="top">
															<select class="box" onchange="fn_searchPeriod('F');" title="<pivot:msg code="ui.label.startTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listF}"
																	varStatus="status">
																	<c:set var="comboF"
																		value="${statInfo.periodInfo.listF[timeLengthF - status.count]}" />
																	<option value="${comboF}"
																		<c:if test="${comboF eq statInfo.periodInfo.defaultStartF}"> selected="selected"</c:if>>
																		<c:out value="${comboF}" />
																	</option>
																</c:forEach>
															</select>~ <select class="box" onchange="fn_searchPeriod('F');" title="<pivot:msg code="ui.label.endTime"/>"
																<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)<0}">disabled="disabled"</c:if>>
																<c:forEach items="${statInfo.periodInfo.listF}"
																	varStatus="status">
																	<c:set var="comboF"
																		value="${statInfo.periodInfo.listF[timeLengthF - status.count]}" />
																	<option value="${comboF}"
																		<c:if test="${comboF eq statInfo.periodInfo.defaultEndF}"> selected="selected"</c:if>>
																		<c:out value="${comboF}" />
																	</option>
																</c:forEach>
															</select>
														</h2>
														<ul id="searchPeriodF" class="selectList">
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">
																<c:forEach var="timeListF"
																	items="${statInfo.periodInfo.defaultListF}"
																	varStatus="status">
																	<li><input type="checkbox"
																		id="timeChF${status.index}" name="timeChkF"
																		onclick="fn_timeCountChk(this,'F');"
																		value="${timeListF}" title="${timeListF}"
																		<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time)>-1}">checked="checked"</c:if> />
																		<label for="timeChF${status.index}"></label>
																	<c:out value="${timeListF}"></c:out></li>
																</c:forEach>
																<%--ie7 마지막 체크박스 사라짐 더미 li 	--%>
																<li><label for="ie7bugF"></label> <input type="checkbox" id="ie7bugF" style="visibility: hidden" name="ie7bug" value="ie7bug" /></li>
															</c:if>
															<%-- 2017-08-11 ul에 아무 li도 없으면 호환성테스트에 걸림 --%>
															<c:if test="${fn:indexOf(statInfo.defaultPeriodStr,time) == -1}">
																<li>&nbsp;</li>
															</c:if>
														</ul>
														<div id="divSelectAllF" <c:if test="${fn:length(statInfo.periodInfo.defaultListF) == 0 }">style="display:none;" </c:if>>
															<input type="checkbox" id="selectAllF" value="selectAllF" checked="checked" onclick="fn_timeAllSelect('F')"/><label for="selectAllF"> <pivot:msg code="ui.label.selectAll" /></label>
														</div>
													</div>
												</c:when>
											</c:choose>
										</c:forTokens>
									</c:when>
									<c:when
										test="${statInfo.tabMenuList[status.index].objVarId eq '13999001'}">
										<!-- 항목분기 -->
										<div class="selectBox" style="width: 305px;">
											<h2 class="top">
												<span style="line-height: 25px;"><input type="checkbox" id="itemChk" onclick="fn_selectItemAll();" />
													<label for="itemChk"><pivot:msg code="ui.label.selectAll" /></label>
												</span>
											</h2>
											<ul class="selectList">
												<c:set var="selectionList" value="itmList${status.index}" />
												<c:forEach var="selection"
													items="${requestScope[selectionList]}" varStatus="status">
													<li><input id="itemChkLi${status.index}"
														name="itemChkLi" type="checkbox"
														value="${selection.itmId}" title="${selection.scrKor}"/> <label
														for="itemChkLi${status.index}"><c:out
																value="${selection.scrKor}"></c:out></label></li>
												</c:forEach>
											</ul>
										</div>
									</c:when>
									<c:otherwise>
										<c:set var="selectionClassList" value="itmList${status.index}" />
										<c:forEach var="i" begin="1" end="${statInfo.classInfoList[status.index-1].depthLvl}" step="1">
											<!-- tabMenuList와 classInfoList갯수 다름 기본적으로 -1(항목)부분이 분류스타트임-->
											<div class="selectBox" style="width: 299px;">
												<h2 class="top">
													<span style="float: left; line-height: 25px;"> <c:set
															var="objVarId"
															value="${statInfo.classInfoList[status.index-1].classId}" />
														<label for="classLvlAllChk${status.index}_${i}"></label>
														<input type="checkbox"
														id="classLvlAllChk${status.index}_${i}"
														name="classLvlAllChk${status.index}_${statInfo.classInfoList[status.index-1].depthLvl}"
														onclick="fn_classSubSelect('${status.index}','${objVarId}','${i}','${statInfo.classInfoList[status.index-1].depthLvl}');"></input>
														<label>${i} <pivot:msg code="ui.label.level" /> <pivot:msg code="ui.label.selectAll" /></label>
														<c:if test="${i ne 1}">
															<a href="javascript:popupControl('pop_classLvAllChkHelp','show','modal')"><img alt="<pivot:msg code="ui.label.selectAll" /> ?" src="images/help/ico_help.gif" /></a>
														</c:if>
													</span>
												</h2>
												<ul id="classList${status.index}_${i}"
													<c:choose><c:when test="${i eq 1}">class="selectList"</c:when><c:otherwise>class="selectList2"</c:otherwise></c:choose>>
													<c:choose>
														<c:when test="${i eq 1}">
															<!-- 분류는 1레벨만 출력 -->
															<c:forEach var="selection"
																items="${requestScope[selectionClassList]}"
																varStatus="li">
																<li><label for="classChkLi${status.index}_${i}_${selection.itmId}"></label>
																	<input
																	id="classChkLi${status.index}_${i}_${selection.itmId}"
																	name="classChkLi${status.index}_${i}" type="checkbox"
																	value="${selection.itmId}=${selection.upItmId}" title="${selection.scrKor}"
																	onclick="fn_classLvlChk('${selection.objVarId}',${status.index},${i},'${selection.itmId}',this)" />
																	<input name="defaultFolder" type="hidden"
																	value="${selection.leaf}" title="${selection.leaf}"/> <c:if
																		test="${selection.leaf eq 0}">
																		<a style="cursor: pointer;"
																			onclick="fn_searchClass('${selection.itmId}','${selection.objVarId}',${i},${status.index},${statInfo.classInfoList[status.index-1].depthLvl},${li.index});">
																			<img src="images/ico_folder.png" alt="폴더" /> <!-- i는 레벨 값 -->
																			<c:out value="${selection.scrKor}"></c:out>
																		</a>
																	</c:if>
																	<c:if test="${selection.leaf eq 1}">
																		<img src="images/ico_doc.png" alt="문서" />
																		<c:out value="${selection.scrKor}"></c:out>
																	</c:if></li>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<li>&nbsp;</li>
														</c:otherwise>
													</c:choose>
												</ul>
											</div>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:forEach>

					<p class="exemple" id="tailExplain" style="display: none;">
						<span><img src="images/ico_doc.png" alt="" /> <pivot:msg code="text.level.icon.file" /></span>
						<span><img src="images/ico_folder.png" alt="" /> <img src="images/ico_fd_chk_blue1.png" alt="" /> <pivot:msg code="text.level.icon.folder" /></span>
					</p>
				</div>
			</div>
			<div id="footer">
				<ul class="footer_wrap">
					<li class="left"><pivot:msg code="text.smbl.expr" /></li>
					<c:if test="${fn:indexOf(statInfo.serverType, 'service') >=0}">
						<li class="right">☞<pivot:msg code="ui.label.display.info" /></li>
					</c:if>
				</ul>
			</div>
		</div>
	</form:form>
</body>
</html>
