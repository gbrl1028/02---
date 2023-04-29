<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript" src="ext/js/jquery/jquery-ui-1.10.3.custom.js"></script>
<script type="text/javascript">
//<![CDATA[
var clickObject;
var liIndex;
var liId;
var selectFlag ='N';
var liInputValue;
var fireFoxBugFlag = 'N';
$(document).ready(function() {
 	//jquery-ui-1.10.3에 정의 
	$("ul.swapList").sortable({
		connectWith: 'ul',
		opacity: 0.6,
		placeholder:"highlight",
		activate:function(event,ui){
			$("[class^=swapList] li").each(function(e){
				//테두리 초기화
				$(this).css('border','1px solid #a3bad9');
			});
			var dragItem = $(ui.item);
 			$(dragItem).css('border','red solid 1px');				<%--선택된 item 테두리색 변경--%>
 			fireFoxBugFlag ='N';
			<%--drag 아이템도 li클릭했을때처럼 변수 세팅해줘야함..drag후 바로 우측/좌측 버튼으로 이동할 수 있으므로 테두리색 지울때는 필요없음.--%>
		},
		stop:function(event,ui){
			var dragItem = $(ui.item).attr('id');
			var dragItem1 = $(this).attr('id');
 			$("#"+dragItem).css('border','1px solid #a3bad9');		<%--선택된 item 테두리색 초기화--%>
 			selectFlag ="N";										<%--선택 후 다른 li drag했을경우 처음선택한 li 선택 flag N으로 변경--%>
 			<%--
  			if(g_browser=="mozilla"){
  				fireFoxBugFlag ='Y';
  				alert(" 모질라 팝업");
  			}
			--%>
 			fn_definite('N');	<%--트리거 이벤트 효과를 함수호출로..드래그 이벤트 끝날때마다 PIVOT 정보 세팅--%>
		}
	});

	<%--		
 	// $(".id ul li").on("click",function(e){}) ->기존 li태그에는 click이벤트가 적용되지만 추가되는 <li>element에는 작동안함..
 	// event delegated를 이용하여 이벤트펑션 생성해야함.
 	--%>
	$("[class^=swapList]").on("click","li",function(e){
		<%--좌측&우측 ul->li 만큼 반복후 일단 border 초기화.--%>
		
		$("[class^=swapList] li").each(function(e){
			$(this).css('border','1px solid #a3bad9');
		});
		
		
		liIndex =$(this).index();		 							<%--클릭 index 추출--%>
		clickObject = $(this).attr("id");							<%--현재 li id 추출--%>
		
		liInputValue = $("#"+clickObject+" input").attr("value");	<%-- li input value 추출 (코드값)--%>
		liId = $(this).parent().attr('id'); 						<%-- 클릭 li 부모 Id 추출--%>

// 		if(g_browser =="mozilla"){
// 			if(fireFoxBugFlag =='N'){
				$("#"+liId+" li:eq("+liIndex+")").css('border','red solid 1px'); //선택값 테두리 표시
				selectFlag='Y';
// 			}
// 		}
// 		fireFoxBugFlag ='Y';
	});
 	
});

		<%-- 
		/************************************************************************ 
		함수명 : fn_moveItem                                   
		설   명 : 멀티셀렉트에서 순서 변경 설정       
		 인   자 : go, listType        
		 사용법 : 좌측&우측 셀렉트에서 선택후 버튼 클릭(체크박스 선택된것 대비) 단순 한건이동이면 id를 Ri_1로 세팅해서 1만 추출하면 index 번호 구할수 있음
		 작성일 : 2013-09-23
		작성자 : 국가통계포털  안영수
		
		date         author      note 
		----------   -------     ------------------- 
		2013-09-23      안영수              최초 생성
		************************************************************************/ 
		--%>
		function fn_moveItem(go,ulId,listType){
			 var changeId = ulId;
			 if(selectFlag =='N')
			 {
				 if(ulId=='ulLeft'){
					alert("<pivot:msg code="alert.pivotInfo.msg"/>");
					
				 }else{
					alert("<pivot:msg code="alert.pivotInfo.msg"/>");
				 }
				 return;
			 }
			<%--listType에 따른 up/down 분기--%>
			
			if(go == "up"){
				
				$('#'+ulId+' li').each(function(index){
		
					<%--clickObject id를 index로 찾기--%>
					 if($(this).attr("id")== clickObject){
						 
						 var selIndex = index-1;
						 var removeIndex = index+1;
						 var upIndex = index;
						<%--리스트중 맨처음 선택된거는 up되지 못하게..--%>
						 if(selIndex == -1){
							return;
						 }	
		
					var newItem = $("#"+changeId+" li:eq("+selIndex+")");
					<%-- 위로 이동 --%>
					$("#"+changeId+" li:eq("+upIndex+")").insertBefore(newItem);	
					}
				});
			}else if(go == "down"){
				$('#'+ulId+' li').each(function(index){			
					 if($(this).attr("id")== clickObject){
						 var selIndex = index;
						 var removeIndex = index+1;
						 var newItem = $("#"+changeId+" li:eq("+removeIndex+")"); 
					<%-- 아래로 이동 --%>
					$("#"+changeId+" li:eq("+selIndex+")").insertAfter(newItem);
					
					}			 
				});
			}	
			fn_definite('N');
		}

		function fn_add(){
			
			var leftChk = $('#ulLeft li').length;

				if(leftChk == 0){
					alert("<pivot:msg code="alert.noInfoSide.msg"/>");
					return;
				}else {
					if(selectFlag =='N' || liId == "ulRight"){
						alert("<pivot:msg code="alert.selectInfoSide.msg"/>");
						return;
					}
				}

				var html="";
				var rightChk = 0; 						<%--오른쪽 데이터 갯수--%>
				var leftCdNm;							<%--left 이름;--%>
				
				rightChk = $('#ulRight li').length;
				<%--cd 및 기타 파라미터 값있을 경우 한번 더 세팅--%>
				
				leftCdNm = $("#ulLeft li:eq("+liIndex+")").text();
				rightChk++;								<%--right 화면 카운트 증가--%>
														<%--동적Html--%>
														
				html="<li id='Ri"+rightChk+"'>\n";
				html+="<input type='hidden' value='"+liInputValue+"'/>";
				html+=leftCdNm+"\n";
				html+="</li>\n";
				
				$("#ulLeft li:eq("+liIndex+")").remove();
				$('#ulRight').append(html);
				selectFlag='N';
				return;
				fn_definite('N');
		}
		
		function fn_remove(){

			var rightChk = $('#ulRight li').length;

				if(rightChk == 0){
					alert("<pivot:msg code="alert.noInfoHead.msg"/>");
					return;
				}else {
					if(selectFlag =='N' || liId == "ulLeft"){
						alert("<pivot:msg code="alert.selectInfoHead.msg"/>");
						return;
					}
				}

				var html="";
				var leftChk = 0; 						<%--왼쪽 데이터 갯수--%>
				var rightCdNm;							<%--right 이름;--%>
				
				leftChk = $('#ulLeft li').length;
				
				<%--cd 및 기타 파라미터 값있을 경우 한번 더 세팅--%>
				rightCdNm = $("#ulRight li:eq("+liIndex+")").text();
				
				leftChk++;								<%--right 화면 카운트 증가--%>
				
				<%--동적Html--%>
				html="<li id='Le"+leftChk+"'>\n";
				html+="<input type='hidden' value='"+liInputValue+"'/>";
				html+=rightCdNm+"\n";
				html+="</li>\n";
				
				$("#ulRight li:eq("+liIndex+")").remove();
				$('#ulLeft').append(html);
				selectFlag='N';
				fn_definite('N');
		}


		<%-- 
		/************************************************************************ 
		함수명 : fn_definite()                                   
		설   명 : 멀티셀렉트에서 순서 변경 설정       
		인   자 : userAction        
		사용법 : 1.사용자가 적용버튼을 누른경우 Y drag이벤트나 move,add,remove함수 호출시에는 N 
				2.변경된 정보로 부가기능 설정 통계표 조회 구분에 맵핑해야함. 
		작성일 : 2013-09-23
		작성자 : 국가통계포털  안영수
		
		date         author      note 
		----------   -------     ------------------- 
		2013-09-23      안영수              최초 생성
		************************************************************************/ 
		--%>
		function fn_definite(userAction){
			 g_rowStr ="";
			 g_colStr ="";

			<%--표측 세팅--%>
			$("#ulLeft li").each(function(e){
				rowCd = $("#"+$(this).attr("id")+" input").attr("value");	<%--li->input value 추출 (코드값)--%>
				g_rowStr+=","+rowCd;
			});
			
			<%--표두 세팅--%>
			$("#ulRight li").each(function(e){
				colCd = $("#"+$(this).attr("id")+" input").attr("value");	<%--li->input value 추출 (코드값)--%>
				g_colStr+=","+colCd;	
			});
			g_rowStr = g_rowStr.substring(1);
			g_colStr = g_colStr.substring(1);
			
		 	form.rowAxis.value = g_rowStr;
		 	form.colAxis.value = g_colStr;
		 	
		 	<%--사용자가 적용버튼 클릭시--%>
		 	if(userAction =='Y'){
		 		form.usePivot.value = "Y";
		 		
			 	popupControl('pop_pivotfunc', 'hide', 'modal');
			 	fn_search();
		 	}
		}
//]]>		
</script>
<div id="pop_pivotfunc">
	<div id="pop_pivotfunc2">
		<div class="pop_top">
			<pivot:msg code="ui.label.pivotSetting"/><span class="closeBtn"><a href="javascript:popupControl('pop_pivotfunc', 'hide', 'modal')"><pivot:msg code="ui.label.close"/></a></span>
		</div>
		
		<div class="pop_content">
			<div class="swap_lay">
				<div class="pop_title">
					<h1><img src="images/ico_swap_tbl.gif" alt="<pivot:msg code="ui.label.pivot.rowaxis"/>" title="<pivot:msg code="ui.label.pivot.rowaxis"/>" /></h1>
					<span class="btn_r"><a href="javascript:fn_moveItem('up','ulLeft','listBox1');"><img src="images/ico_arrow_up.png" alt="<pivot:msg code="ui.label.pivot.up"/>" title="<pivot:msg code="ui.label.pivot.up"/>" /></a>
					<a href="javascript:fn_moveItem('down','ulLeft','listBox1');"><img src="images/ico_arrow_down.png" alt="<pivot:msg code="ui.label.pivot.down"/>" title="<pivot:msg code="ui.label.pivot.down"/>" /></a></span>
				</div>
				<ul id="ulLeft" class="swapList">
					<c:forEach var="row" items="${statInfo.pivotInfo.rowList}" varStatus="status">
						<li id="Le${status.index}"><input type="hidden" value="${row}"/>${statInfo.pivotInfo.rowNmList[status.index]}</li>
					</c:forEach>
				</ul>
			</div>
			<div class="alignBtn">
				<p>
					<a href="javascript:fn_add();"><img src="images/btn_arrow_right.png" alt="<pivot:msg code="ui.label.pivot.right"/>" title="<pivot:msg code="ui.label.pivot.right"/>"/></a>
					<a href="javascript:fn_remove();"><img src="images/btn_arrow_left.png" alt="<pivot:msg code="ui.label.pivot.left"/>" title="<pivot:msg code="ui.label.pivot.left"/>"/></a>
				</p>
			</div>
			<div class="swap_lay">
				<div class="pop_title">
					<h1><img src="images/ico_swap_tbl2.gif" alt="<pivot:msg code="ui.label.pivot.colaxis"/>" title="<pivot:msg code="ui.label.pivot.colaxis"/>" /></h1>
					<span class="btn_r"><a href="javascript:fn_moveItem('up','ulRight','listBox2');"><img src="images/ico_arrow_up.png" alt="<pivot:msg code="ui.label.pivot.up"/>" title="<pivot:msg code="ui.label.pivot.up"/>" /></a>
					<a href="javascript:fn_moveItem('down','ulRight','listBox2');"><img src="images/ico_arrow_down.png" alt="<pivot:msg code="ui.label.pivot.down"/>" title="<pivot:msg code="ui.label.pivot.down"/>" /></a></span>
				</div>
				<ul id="ulRight" class="swapList"> 
					<c:forEach var="col" items="${statInfo.pivotInfo.colList}" varStatus="status">
						<li id="Ri${status.index}"><input type="hidden" value="${col}"/>${statInfo.pivotInfo.colNmList[status.index]}</li>
					</c:forEach>
				</ul>
			</div>
			<div class="btn_lay">
<!-- 				<span class="confirmBtn"><a href="#">확인</a></span> -->
				<span class="confirmBtn"><a href="javascript:fn_definite('Y');"><pivot:msg code="ui.btn.accept"/></a></span>
<!-- 				<span class="cancelBtn"><a href="#">취소</a></span> -->
			</div>
		</div>
		
	</div>
</div>