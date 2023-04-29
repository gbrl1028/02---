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
		
	parent.popupControl('pop_statGrid', 'hide', 'modal');
	var tmpStr = $("#dbUser").val();
	
	$("#orgId").val(list.relOrgId);
	$("#tblId").val(list.relTblId);
	$("#dbUser").val(tmpStr.substring(0,tmpStr.length-1));	
	
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
			url:"<%=request.getContextPath()%>/statInfoList.do",
			jsonReader: {
				repeatiems : false,
				page: "page",
				total: "total",
				root: "statInfo.relationList"			
			},
			colNames:['Name of Survey'],
		   	colModel:[{name:'statEngNm',index:'statEngNm',width:510,align:'center'}],
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

		   	},
		    loadError:function(xhr, status, error){
		    	alert(error);
			},
			loadBeforeSend:function(data){
		    },
		   	gridComplete: function(){
				$(".ui-jqgrid-hdiv").hide();
				$("#gview_tbl_data_view").attr("style","border:0px solid #c6c7d0;");
				$(".ui-jqgrid-bdiv").attr("style","border:0px solid #c6c7d0;");
		   	}
		});
	}else{
		
		$("#tbl_data_view").jqGrid(
			{
			datatype: 'json',
			type : 'POST',
			url:"<%=request.getContextPath()%>/statInfoList.do",
			jsonReader: {
				repeatiems : false,
				page: "page",
				total: "total",
				root: "statInfo.relationList"			
			},
			colNames:['출처'],
		   	colModel:[{name:'statNm',index:'statNm',width:510,align:'center'}],
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

		   	},
		    loadError:function(xhr, status, error){
		    	alert(error);
			},
			loadBeforeSend:function(data){
		    },
		   	gridComplete: function(){
				$(".ui-jqgrid-hdiv").hide();
				$("#gview_tbl_data_view").attr("style","border:0px solid #c6c7d0;");
				$(".ui-jqgrid-bdiv").attr("style","border:0px solid #c6c7d0;");
		   	}
		});
	}


});

</script>
</head>
<body onload="parent.callbackForIframe('pop_statGrid');">
<form:form commandName="ParamInfo" name="ParamInfo" method="post">
	<form:hidden path="orgId"/>
	<form:hidden path="tblId"/>
	<form:hidden path="dbUser"/>	
	<form:hidden path="dataOpt"/>
</form:form>
	<div id="ifr_statInfo">
		<div class="pop_content">
			<div id="jqGrid">
					<table id="tbl_data_view" style="table-layout:fixed;"><tr><td></td></tr></table>
			</div>
			<br>			
		</div>
	</div>	
</body>
</html> 	
