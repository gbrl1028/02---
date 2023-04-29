<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<link type="text/css" rel="stylesheet" href="ext/css/common/popup.css"/>
<link type="text/css" rel="stylesheet" href="ext/css/common/base.css"/>
<script type="text/javascript" src="ext/js/jquery/jquery-1.9.1.js"></script>
<script type="text/javascript">
//<![CDATA[
function fn_setName(name, id){
	$("#myscrapSelectedCategoryName").val(name);
	$("#folderId").val(id);
}

function fn_save(){

	if($.trim($("#myscrabTblNm").val()) == ""){
		alert("<pivot:msg code="alert.myscrap.msg"/>");
		return;
	}
	var form = document.ParamInfo;
	form.itemMultiply.value = parent.g_itemMultiply; // 항목 * 분류 (최신시점기준 스크랩시 필요)

	$.ajax({
		dataType : 'json',
		type : 'POST',
		url : "<%=request.getContextPath()%>/myscrapSave.do",
		data : $("#ParamInfo").serialize(),
		success : function(response,status){
			var data = response.result ;

			if(data == 'success'){
				var action = confirm("<pivot:msg code="alert.myscrap.movePortal"/>");
				if(action){
					//top.location.href="http://kosis.kr/myPage/myPage_01List.jsp";
					top.location.href="http://kosis.kr/myPage/myStatisList.do";
					// 통합로그인 테스트를 위해 nportal로 가도록 수정
					//top.location.href="http://kosis.kr/nportal/myPage/myPage_01List.jsp";
				}
			}else{
				alert( "<spring:message code="fail.common.msg" />" );
			}
		},
		error : function(error){
			alert( "<spring:message code="fail.common.msg" />" );
		}
	});
}
function lay_open(val){
	if( val == 1){
		if( $("#prdR_lay").css("display") == "block"){
			$("#prdR_lay").hide();
		}else{
			$("#prdR_lay").show();
		}
	}else if( val == 2){
		if( $("#prdS_lay").css("display") == "block"){
			$("#prdS_lay").hide();
		}else{
			$("#prdS_lay").show();
		}
	}
}
function lay_close(val){
	if( val == 1){
		$("#prdR_lay").hide();
	}else if( val == 2){
		$("#prdS_lay").hide();
	}
}
//]]>
</script>
</head>
<body onload="parent.callbackForIframe('pop_myscrap');">
<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="orgId"/>
	<form:hidden path="tblId"/>
	<form:hidden path="colAxis"/>
	<form:hidden path="rowAxis"/>
	<form:hidden path="language"/>
	<form:hidden path="dataOpt"/>
	<form:hidden path="doAnal" />
	<form:hidden path="analType"/>
	<form:hidden path="analCmpr"/>
	<form:hidden path="analTime"/>
	<form:hidden path="analClass"/>
	<form:hidden path="analItem"/>
	<form:hidden path="analText"/>
	<form:hidden path="originData"/>
	<form:hidden path="noSelect"/>
	<input type="hidden" name="itemMultiply" id="itemMultiply" value=""/>
	<input type="hidden" name="folderId" id="folderId" value=""/>

	<div id="myscrapPopContent" class="pop_content" style="width:480px;">
		<div class="assay_lay2" style="width:210px;">
			<h1 class="bu_circle"><pivot:msg code="ui.label.myScrapCategory"/></h1>
			<div class="tree">
				<ul>
					<c:set var="lv1Cnt" value = "0"/>
				<li>
					<a style="cursor:pointer;" onclick="fn_setName('${empNm}<pivot:msg code="ui.label.room"/>', '');" ><img src="<%=request.getContextPath()%>/images/myscrap/icon_folder.gif" alt="image" /> <c:out value='${empNm}'/><pivot:msg code="ui.label.room"/></a>
				</li>
				<c:forEach items="${folder_1}" var="folder_1" >
						<li>
						<c:set var="lv2Cnt" value = "0"/>
						<c:set var="lv1Cnt" value="${lv1Cnt+1}" />
						<c:choose>
							<c:when test="${folder_1.CNT eq lv1Cnt}">
								<img src="<%=request.getContextPath()%>/images/myscrap/b_02.gif"  alt="image" />
							</c:when>
							<c:otherwise>
								<img src="<%=request.getContextPath()%>/images/myscrap/b_03.gif" alt="image" />
							</c:otherwise>
						</c:choose>
						<a style="cursor:pointer;" onclick="fn_setName('${folder_1.FOLDER_NAME}', '${folder_1.FOLDER_ID}');"><img src="<%=request.getContextPath()%>/images/myscrap/icon_folder.gif" alt="image"/> <c:out value="${folder_1.FOLDER_NAME}"/></a>
					</li>
					<c:forEach items="${folder_2}" var="folder_2">
						<c:if test="${folder_1.CNT ne '0'}">
							<c:if test="${folder_1.FOLDER_ID eq folder_2.UP_FOLDER_ID}">
							<li>
								<c:set var="lv2Cnt" value="${lv2Cnt+1}" />
								<c:choose>
									<c:when test="${folder_1.CNT ne lv1Cnt}">
										<img src="<%=request.getContextPath()%>/images/myscrap/b_01.gif" alt="image" />
									</c:when>
									<c:otherwise>
										<span>　</span>
									</c:otherwise>
								</c:choose>
								<c:if test="${folder_1.FOLDER_ID eq folder_2.UP_FOLDER_ID}">
									<c:choose>
										<c:when test="${folder_2.CNT eq lv2Cnt}">
											<img src="<%=request.getContextPath()%>/images/myscrap/b_02.gif"  alt="image" />
										</c:when>
										<c:otherwise>
											<img src="<%=request.getContextPath()%>/images/myscrap/b_03.gif" alt="image" />
										</c:otherwise>
									</c:choose>
									<a style="cursor:pointer;" onclick="fn_setName('${folder_2.FOLDER_NAME}', '${folder_2.FOLDER_ID}');"><img src="<%=request.getContextPath()%>/images/myscrap/icon_folder.gif" alt="image" /> <c:out value="${folder_2.FOLDER_NAME}"/></a>
								</c:if>
							</li>
							</c:if>
						</c:if>
					</c:forEach>
				</c:forEach>
				</ul>
			</div>
		</div>

		<div class="assay_lay2" style="float:left; padding-left:15px;">
			<h1 class="bu_circle"><label for="myscrapSelectedCategoryName"><pivot:msg code="ui.label.selectCategory"/></label></h1>
			<p><input type="text" id="myscrapSelectedCategoryName" class="text" style="background-color:#f4f4f4;" size="30" value="${empNm}<pivot:msg code="ui.label.room"/>" readonly="readonly"/></p>
			<h1 class="bu_circle" style="margin-top:20px;"><label for="myscrabTblNm"><pivot:msg code="ui.label.statisticsName"/></label></h1>
			<p><input type="text" id="myscrabTblNm" name="myscrabTblNm" class="text" size="35" value="${tblNm} ${ParamInfo.analText}" maxlength="100"/></p>
			<h1 class="bu_circle" style="margin-top:20px;"><pivot:msg code="ui.label.periodStandard"/></h1>
			<ul class="innerList">
				<li><input id="myscrapPeriodR" name="myscrapPeriod" type="radio" value="r"/><span><label for="myscrapPeriodR"><pivot:msg code="ui.label.lastPeriodStandard"/></label></span><a href="javascript:lay_open(1);"><img src="images/help/ico_help.gif" alt="help"/></a></li>
				<li><input id="myscrapPeriodS" name="myscrapPeriod" type="radio" value="s" checked="checked"/><span><label for="myscrapPeriodR"><pivot:msg code="ui.label.searchPeriodStandard"/></label></span><a href="javascript:lay_open(2);"><img src="images/help/ico_help.gif" alt="help"/></a></li>
			</ul>
		</div>
		<div class="btn_lay"><span class="confirmBtn"><a style="cursor:pointer;" onclick="fn_save();"><pivot:msg code="ui.label.save"/></a></span></div>
	</div>
</form:form>
<div id="prdR_lay" style="position:absolute; background:#ffffff; left:50%; top:62%;  width:230px; height:80px; padding: 2px 2px; display:none; border:1px solid #3366CC;">
<p style="font-size: 11px;">통계표의 선택한 시점부터 최근시점까지 조회 단, 조회수 제한(10,000셀) 초과시, <br/>최근시점부터 조회수 범위내 시점까지 조회</p>
<p style="padding-top:10px; text-align:right"><a href="javascript:lay_close(1);"><pivot:msg code="ui.label.close"/></a></p>
</div>
<div id="prdS_lay" style="position:absolute; background:#ffffff; left:50%; top:70%;  width:230px; height:50px; padding: 2px 2px; display:none; border:1px solid #3366CC;">
<p style="font-size: 11px;">통계표에서 선택한 시점 그대로 조회</p>
<p style="padding-top:10px; text-align:right"><a href="javascript:lay_close(2);"><pivot:msg code="ui.label.close"/></a></p>
</div>
</body>
</html>