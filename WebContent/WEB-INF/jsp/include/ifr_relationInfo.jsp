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

<script type="text/javascript" src="ext/js/jqgrid/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="ext/js/jqgrid/jquery.jqGrid.src.js"></script>
<script type="text/javascript" src="ext/js/jqgrid/grid.locale-en.js"></script>
<link type="text/css" rel="stylesheet" href="ext/css/jqgrid/ui.jqgrid.relstat.css"/>
<script type="text/javascript">

var g_ordId = "${ParamInfo.orgId}"; 
var g_tblId = "${ParamInfo.tblId}";

function fn_relsearch(rowid){
	var list = $("#tbl_data_view").getRowData(rowid);
		
	parent.popupControl('pop_relGrid', 'hide', 'modal');
	var tmpStr = $("#dbUser").val();
	
	$("#orgId").val(list.relOrgId);
	$("#tblId").val(list.relTblId);
	$("#dbUser").val(tmpStr.substring(0,tmpStr.length-1));
	$("#relChkOrgId").val(g_ordId);
	$("#relChkTblId").val(g_tblId);
	
	var form = document.ParamInfo;
	form.action = "<%=request.getContextPath()%>/statHtml.do";
	form.target = "_parent";
	form.method = "POST";
	form.submit();
	return;
}

$(document).ready(function(){

	var dataOpt = "${ParamInfo.dataOpt}";
	
	if(dataOpt.indexOf("en") > -1){
	
		$("#tbl_data_view").jqGrid(
			{
			datatype: 'json',
			type : 'POST',
			url:"<%=request.getContextPath()%>/relationInfoList.do",
			jsonReader: {
				repeatiems : false,
				page: "page",
				total: "total",
				root: "relationInfo.relationList"			
			},
			colNames:['OrgId','Organization name','TblId','Name of Survey','Table Name','Final time period','Detail','search'],
		   	colModel:[
		   	        {name:'relOrgId',index:'relOrgId',width:100,hidden:true},
	   	   	        {name:'relOrgEngNm',index:'relOrgEngNm',width:100,align:'center',hidden:true},	   	        
		   	     	{name:'relTblId',index:'relTblId',width:100,hidden:true},
		   	        {name:'statEngNm',index:'statEngNm',width:110,align:'center'},
		   	        {name:'relEngTblNm',index:'relEngTblNm',width:208,align:'center'},
		   	    	{name:'lastPrdDe',index:'lastPrdDe',width:113,align:'center'},
		   	    	{name:'relEngDetail',index:'relEngDetail',hidden:true},
		   	    	{name:'TblYn',index:'TblYn',width:50,align:'center'}],
		   	postData:$("#ParamInfo").serialize(),
		   	scrollrows:true,
		   	height: 121,
		   	width : 548,	
		   	rownumbers: true,
		  	gridview: true,
		   	loadonce: true,
		    viewrecords: false,
			shrinkToFit: false,			//가로 넓이 맞춰서 데이터 출력
			sortable: false,
		   	loadui: false,				// loading 표시안함
		   	onSelectRow:function(rowid,status,e){
		   		var list = $("#tbl_data_view").getRowData(rowid);
		   		$("#relDetail").val(list.relEngDetail);
		   	},
		    loadError:function(xhr, status, error){
		    	alert(error);
		    	//gfn_ajaxerror(error);
			},
			loadBeforeSend:function(data){
		    },
		   	gridComplete: function(){
		   		var grid = $("#tbl_data_view");
		   		var ids = grid.jqGrid("getDataIDs");
		   		for(var i = 0 ; i<ids.length; i++){
		   			var rowId = ids[i];
// 		   			var checkOut = "<img id ='searchRelImg1' src='images/btn_rel_search_en.png' alt='search'  style='width:65px; height:26px; cursor:pointer;' "+
		   			var checkOut = "<img id ='searchRelImg1' src='images/btn_search_rel_en.png' alt='search'  style='cursor:pointer;' "+
								   "onclick=\"fn_relsearch("+rowId+");\" />";
		   			grid.jqGrid('setRowData',rowId,{TblYn : checkOut});
		   			
		   		}
		   		// 관련통계표에 설명이 존재하는 경우 선택을 해야만 설명이 조회되므로 load시 설명을 조회할 수 있도록 수정
		   		var list = $("#tbl_data_view").getRowData(1);
		   		$("#relDetail").val(list.relEngDetail);
		   		
		   		// 조회할 수 있는 관련통계표가 존재하지 않을 경우 메세지 출력
// 		   		var msg = "NO RELATION STATISTIC DATA";
// 		   		var gridAllWidth = $("#tbl_data_view").css('width');
// 		   		var gridWidth = $("#gbox_tbl_data_view").css('width');
// 		   		var msgHtml = "";
	
// 		   		if($("#tbl_data_view").getGridParam("records") == 0){		// 조회 결과가 없을 경우 그리드에 출력
// 		   			$("#tbl_data_view_nodata").remove();
// 		   			msgHtml = "<div id='tbl_data_view_nodata' style='width:529px; border-bottom:1px solid #e2e2e2; border-right:1px solid #e2e2e2; height:30px; line-height:30px; text-align:center'>";
// 		   			msgHtml += "<div style='width:"+gridWidth+"; text-align:center'>";
// 		   			msgHtml += msg;
// 		   			msgHtml += "</div>";
// 		   			msgHtml += "</div>";
// 		   			$("#tbl_data_view").before(msgHtml);
// 		   		}else{
// 		   			$("#tbl_data_view_nodata").remove();
// 		   		}
		   	}
		});
	}else{
		
		$("#tbl_data_view").jqGrid(
			{
			datatype: 'json',
			type : 'POST',
			url:"<%=request.getContextPath()%>/relationInfoList.do",
			jsonReader: {
				repeatiems : false,
				page: "page",
				total: "total",
				root: "relationInfo.relationList"			
			},
			colNames:['기관코드','기관명','통계표ID','통계명','통계표명','최종수록시점','한글설명','조회'],
		   	colModel:[
		   	        {name:'relOrgId',index:'relOrgId',width:110,hidden:true},
	   	   	        {name:'relOrgNm',index:'relOrgNm',width:70,align:'center',hidden:true},
	  	   	        {name:'relTblId',index:'relTblId',width:100,hidden:true},
		   	        {name:'statNm',index:'statNm',width:90,align:'center'},
		   	    	{name:'relTblNm',index:'relTblNm',width:253,align:'center'},
		   	    	{name:'lastPrdDe',index:'lastPrdDe',width:85,align:'center'},
		   	    	{name:'relKorDetail',index:'relKorDetail',hidden:true},
		   	    	{name:'TblYn',index:'TblYn',width:53,align:'center'}],
		   	postData:$("#ParamInfo").serialize(),
		   	scrollrows:true,
		   	height: 120,
		   	width : 548,
		   	rownumbers: true,
		  	gridview: true,
		   	loadonce: true,
		    viewrecords: false,
			shrinkToFit: false,	//가로 넓이 맞춰서 데이터 출력
			sortable: false,
		   	loadui: false,				// loading 표시안함
		   	onSelectRow:function(rowid,status,e){
		   		var list = $("#tbl_data_view").getRowData(rowid);
		   		$("#relDetail").val(list.relKorDetail);
		   	},
		    loadError:function(xhr, status, error){
		    	alert(error);
		    	//gfn_ajaxerror(error);
			},
			loadBeforeSend:function(data){
		    },
		   	gridComplete: function(){
		   		var grid = $("#tbl_data_view");
		   		var ids = grid.jqGrid("getDataIDs");
		   		for(var i = 0 ; i<ids.length; i++){
		   			var rowId = ids[i];
// 		   			var checkOut = "<img id ='searchRelImg1' src='images/btn_rel_search.png' alt='조회' style='width:65px; height:26px; cursor:pointer;' "+
		   			var checkOut = "<img id ='searchRelImg1' src='images/btn_search_rel.png' alt='조회' style='cursor:pointer;' "+
		   						   "onclick=\"fn_relsearch("+rowId+");\" />";
		   			grid.jqGrid('setRowData',rowId,{TblYn : checkOut});
		   			
		   		}
		   		// 관련통계표에 설명이 존재하는 경우 선택을 해야만 설명이 조회되므로 load시 설명을 조회할 수 있도록 수정
		   		var list = $("#tbl_data_view").getRowData(1);
		   		$("#relDetail").val(list.relKorDetail);
		   		
		   		// 조회할 수 있는 관련통계표가 존재하지 않을 경우 메세지 출력
// 		   		var msg = "조회가능한 관련 통계표가 존재하지 않습니다.";
// 		   		var gridAllWidth = $("#tbl_data_view").css('width');
// 		   		var gridWidth = $("#gbox_tbl_data_view").css('width');
// 		   		var msgHtml = "";
	
// 		   		if($("#tbl_data_view").getGridParam("records") == 0){		// 조회 결과가 없을 경우 그리드에 출력
// 		   			$("#tbl_data_view_nodata").remove();
// 		   			msgHtml = "<div id='tbl_data_view_nodata' style='width:529px; border-bottom:1px solid #e2e2e2; border-right:1px solid #e2e2e2; height:30px; line-height:30px; text-align:center'>";
// 		   			msgHtml += "<div style='width:"+gridWidth+"; text-align:center'>";
// 		   			msgHtml += msg;
// 		   			msgHtml += "</div>";
// 		   			msgHtml += "</div>";
// 		   			$("#tbl_data_view").before(msgHtml);
// 		   		}else{
// 		   			$("#tbl_data_view_nodata").remove();
// 		   		}
		   	}
		});
	}
// 	$(".ui-jqgrid tr.jqgrow td").css("height","206px");

});

</script>
</head>
<body onload="parent.callbackForIframe('pop_relGrid');">
<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="orgId"/>
	<form:hidden path="tblId"/>
	<form:hidden path="dbUser"/>
	<form:hidden path="pub"/>
	<form:hidden path="dataOpt"/>
	<form:hidden path="relChkOrgId" />
	<form:hidden path="relChkTblId"/>
</form:form>
	<div id="ifr_relationInfo">
		<div class="pop_content">
			<div id="jqGrid">
					<table id="tbl_data_view" style="table-layout:fixed;"><tr><td></td></tr></table>
			</div>
			<br>
			<div class="pop_title">
				<h1 class="bu_circle3">
					<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') >= 0  }">
						Detail
					</c:if>
					<c:if test="${fn:indexOf(ParamInfo.dataOpt, 'en') < 0  }">
						한글설명
					</c:if>
				</h1>
			</div>
			<textarea id="relDetail" readonly="readonly"></textarea>
		</div>
	</div>
	${relationInfo}
</body>
</html> 	
