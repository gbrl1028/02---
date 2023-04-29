<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="/WEB-INF/jsp/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<link rel="stylesheet" type="text/css" href="ext/css/direct/directList.css"/>
<script type="text/javascript" src="ext/js/jquery/jquery-1.9.1.js"></script>
<script type="text/javascript" src="ext/js/common/json2.js"></script>
</head>
<body onload="fnPrdSe();">
<script type="text/javascript">
	//수치다운로드
	function dtValDown(){
		var chkArr  = $("input:checkbox[name=PRD_DE]:checked");
		var prdList = "";	// 시점목록
		var prd = "";
		var mode = "directMake";
		var chkPrdSe = $("select[name=prdSe]").val();
		if(chkPrdSe == null || chkPrdSe == ''){
			alert("주기를 선택해야 합니다.");
			return;
		}
		// 체크된 시점을 문자열로
		$(chkArr).each(function(){
			prd += $(this).val() + ","; //ex)2011, 2010, 2009
		});

		if(prd == null || prd == ''){
			alert("시점을 선택해야 합니다.");
			return;
		}

		$("#prdDe").val(prd);

		var f = document.ParamInfo;

		$("#downBtn").hide();
		$("#loadingBtn").show();
		$("#metaDown").attr("href", "#");

		$.ajax({
			dataType : 'json',
			type : 'POST',
			url : '<%=request.getContextPath()%>/makeLarge.do',
			data : $("#ParamInfo").serialize(),
			success : function(response,status){
				$("#downBtn").show();
				$("#loadingBtn").hide();
				$("#metaDown").attr("href", "javascript:metaDown();");

				var file = response.file;
				f.method = "POST";
				f.action = "<%=request.getContextPath()%>/downLarge.do?file=" + file;
				f.submit();

			},
			error : function(error){
				$("#downBtn").show();
				$("#loadingBtn").hide();
			}
		});
	}

	//주기선택에 따른 시점 갖고오기
	function fnPrdSe() {
		try{

			$.ajax({
				dataType : 'text',
				type : 'POST',
				url : '<%=request.getContextPath()%>/directDownPrdDe.do',
				data : {
					'orgId'      : "${ParamInfo.orgId}",
					'tblId'	     : "${ParamInfo.tblId}",
					'prdSe'	     : $("#prdSe").val(),
					'pub'		 : "${ParamInfo.pub}",
					'dbUser'	 : "${ParamInfo.dbUser}",
					'st'		 : "${ParamInfo.st}"
				},
				success : function(response,status){
					$("#prd_set").html(response);
					fnSelectPrdDe();
				},
				error : function(error){
					alert(error);
				}
			});
		}catch(e){alert(e);}
	}

	// 선택한 시점 리스트만 보이기
	function fnSelectPrdDe(){
		var strtPrdDe = $("#startPrdDe").val();
		var endPrdDe = $("#endPrdDe").val();
		var cnt = 0;
		var prdDeCnt = $("#prdDeCnt").val(); // 가능 시점 수 : 50000/항목*분류 계산한 값

		if(strtPrdDe > endPrdDe){
			alert("시점 조회 조건을 다시 설정해 주세요. ");
		}

		$('input[name=PRD_DE]').each(function(){
			var chkId = $(this).attr('value');

			if(parseInt(strtPrdDe) <= parseInt(chkId) && parseInt(endPrdDe) >= parseInt(chkId)){
				$(this).parent().show();		// checkbox 보임
				if(cnt < prdDeCnt){
					//2015.08.17 크롬, 파폭 등에서 체크박스 체크가 되지 않는 현상으로 수정
					//$(this).attr('checked', true);
					$(this).prop('checked', true);	// checkbox 선택
				}else{
					//$(this).attr('checked', false);
					$(this).prop('checked', false);	// checkbox 선택
				}
				cnt++;
			}
			else{
				$(this).parent().hide();		// checkbox 숨김
				//$(this).attr('checked', false);
				$(this).prop('checked', false);	// checkbox 선택해제
			}
		});
	}

	// 선택할 수 있는 시점갯수보다 더 체크되면 alert()
	function cntPrdDe(obj){
		var prdDeCnt = $("#prdDeCnt").val(); // 가능 시점 수 : 50000/항목*분류 계산한 값
		var chkArr   = $("input:checkbox[name=PRD_DE]:checked");
		var prdList = "";	// 시점목록
		if(chkArr.length > prdDeCnt){
			alert("최대 셀수를 초과하여 더 이상 시점을 추가 할 수 없습니다.");
			$(obj).attr('checked', false);
			return;
		}
	}

	// 메타 다운로드
	function metaDown(){

		var chkArr  = $("input:checkbox[name=PRD_DE]:checked");
		var prd = "";

		// 체크된 시점을 문자열로
		$(chkArr).each(function(){
			prd += $(this).val() + ","; //ex)2011, 2010, 2009
		});

		$("#prdDe").val(prd);

		var f = document.ParamInfo;
		f.method = "POST";
		f.action = "<%=request.getContextPath()%>/downDirectMeta.do";
		f.submit();
	}
	
	//2017.11.21 직접다운로드에서 TXT,SDMX는 HTML통계표조회 페이지 다운받도록 링크 생성
	function goStatHtml(){
		var orgId = $('#orgId').val();
		var tblId = $('#tblId').val();
		var dbUser = $('#dbUser').val();
		
		
		var serverTypeOri = "${ParamInfo.serverTypeOrigin}";
		
		cw=screen.availWidth;
		ch=screen.availHeight;
		
		<%-- 2020.06.16 공동활용에서 호스팅쪽 통계표조회의 직접다운로드 페이지를 호출하게 되면서 통계표조회 URL의 변화가 필요함 --%>
		if(serverTypeOri.indexOf("service") < 0 ){
			dbUser = "&dbUser="+dbUser.replace(".","");
		}else{
			dbUser ="";
		}
		
		var newWin = window.open("<%=request.getContextPath()%>/statHtml.do?orgId="+orgId+"&"+"tblId="+tblId+dbUser, "goStatHtml", "location=yes, status=yes, directories=yes, menubar=yes, toolbar=yes, scrollbars=yes, resizable=yes, width="+cw+", height="+ch);

		$(newWin.document).ready(function(){
			newWin.focus();
			
			downWinOpen = setInterval(function(){
				var buff = $(newWin.document).find("#htmlGrid").html();
				
				if( buff.length > 0){
					newWin.popupControl('pop_downgrid','show','modal');
					clearInterval(downWinOpen);
				}
			}, 1000); 
		});
	}

</script>
<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="orgId"></form:hidden>
	<form:hidden path="tblId"></form:hidden>
	<input type="hidden" id="prdDe" name="prdDe" />
	<input type="hidden" id="prdNm" name="prdNm" />
	<input type="hidden" id="prdDeCnt" name="prdDeCnt" value="<c:out value="${stblMap.prdDeCnt }"/>" />
	<input type="hidden" name="DIM_CO" value="<c:out value="${stblMap.DIM_CO}"></c:out>" />
	<input type="hidden" id="mode" name="mode" />
	<input type="hidden" id="VWCD" name="VWCD" value="<c:out value="${stblMap.VW_CD}"></c:out>" />
	<input type="hidden" id="direct" name="direct" value="direct" />
	<form:hidden path="dbUser"></form:hidden>
	<form:hidden path="pub"></form:hidden>
	<form:hidden path="st"></form:hidden>
	<table class="firstDown" style="width:670px; border:0px;">
		<colgroup>
			<col width="15%" /><col width="85%" />
		</colgroup>
		<tbody>
		<c:if test="${stblMap.PROCESS != 'statHtml' }" >
			<tr>
				<th><img src="images/direct/th01.gif" alt="통계표명" /></th>
				<td><c:out value="${stblMap.TBL_NM}"></c:out></td>
			</tr>
		</c:if>
			<tr>
				<th><img src="images/direct/th02.gif" alt="기본정보" /></th>
				<td><img src="images/direct/td_text.gif" alt="출처,주석,단위,분류값 등 통계표 정보제공" />
					<span style="margin-left:30px;">
						<a id="metaDown" href="javascript:metaDown();" ><img src="images/direct/027.gif" alt="통계표기본정보 다운로드"  border="0"/></a>
					</span>
				</td>
			</tr>
			<c:if test="${stblMap.PROCESS != 'statHtml' }" >
			<tr>
				<th><img src="images/direct/th03.gif" alt="자료갱신일" /></th>
				<td><c:out value="${stblMap.RENEWAL_DATE}"></c:out> / 수록기간 : <c:out value="${stblMap.PRD_INFO}"></c:out></td>
			</tr>
			</c:if>
			<tr>
				<th><img src="images/direct/th04.gif" alt="파일형태" /></th>
				<td><select name="downLargeFileType" style="border:#ddd 1px solid;">
						<option value="txt">TXT</option>
						<option value="csv">CSV</option>
						<option value="excel">EXCEL</option>
					</select>
					<c:if test="${fn:substring(stblMap.OLAP_STL, 0, 1) != 'X'}"><%-- 초기조회조건에서 직접다운로드로 설정한 통계표는 통계표조회 링크를 붙여주지 않음 - 2018.05.18 --%>
					<span style="color:#cc3333">※ SDMX는 통계표 조회페이지에서 다운로드할 수 있습니다.</span> 
					
					<span style="font-weight: bold;cursor:pointer;" onclick="goStatHtml()">[통계표조회 바로가기]</span>
					</c:if>
				</td>
			</tr>
			<tr>
				<th><img src="images/direct/th05.gif" alt="통계표구성" /></th>
				<td class="check">
				<input type="radio" id="directDownExprType1" name="downLargeExprType" value="1" title="시점 표두, 항목 표측" checked /> <label for="directDownExprType1">시점 표두, 항목 표측 </label>
				  <input type="radio" id="directDownExprType2" name="downLargeExprType" value="2" title="항목 표두, 시점 표측"  /> <label for="directDownExprType2"> 항목 표두, 시점 표측 </label>
				  <span class="gap">|</span> <input type="radio" id="periodAsc" name="downLargeSort" value="asc" checked title="시점 오름차순"  /> <label for="periodAsc"> 시점 오름차순  </label>
				  <input type="radio" id="periodDesc" name="downLargeSort" value="desc" title="시점 내림차순"  /> <label for="periodDesc">시점 내림차순  </label>
				  <span class="gap">|</span> <input type="checkbox"  id="exprYn" name="exprYn" value="Y"/> <label for="codeYn">코드포함</label>
				</td>
			</tr>
			<tr>
				<th><img src="images/direct/th06.gif" alt="주기선택" /></th>
				<td>
				<select id="prdSe" name="prdSe" style="border:#ddd 1px solid;" onchange="javascript:fnPrdSe();return false;">
				<c:forEach items="${stblMap.prdSeList}" var="item">
					<option value="<c:out value="${item.PRD_SE}" />"><c:out value="${item.PRD_NM}" /></option>
				</c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<th valign="top"><img src="images/direct/th07.gif" alt="시점선택" /></th>
				<td valign="top">
					<div id="prd_set" style="margin:0px; height:210px; width:510px;"></div>
				</td>
			</tr>
			<tr>
				<th></th>
				<td>
					(항목x분류 조합수가 <c:out value="${stblMap.DIM_CO}"></c:out>셀이므로 <c:out value="${stblMap.prdDeCnt}"></c:out>개까지 시점 선택이 가능합니다. <span class="f_red">*최대 셀 수는 <c:out value="${maxCell}"></c:out>개</span>)
				</td>
			</tr>
		</tbody>
	</table>
	<!--// 테이블 끝 -->
	<!-- 버튼 -->
	<div id="downBtn" class="downBtn" style="display:block;"><a onclick="javascript:dtValDown(); return false;"><img src="images/direct/downloadBtn.gif" alt="다운로드" border="0"/></a></div>
	<div id="loadingBtn" class="downBtn" style="display:none"><a style="position:absolute; margin:3px 0 0 192px;"></a><img src="images/direct/loading_bg.gif" alt="다운로드  중" border="0" /></div>
</form:form>
</body>
</html>