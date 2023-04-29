function fn_countView(p_itmCnt,p_classCnt,p_timeCnt,p_classViewCntStr){

	g_textField = null;
    g_multiplication = 0;

	g_itemMultiply = p_itmCnt * p_classCnt;							// 항목* 분류

	g_multiplication = g_itemMultiply * p_timeCnt;
	g_textField = "("+p_itmCnt+") X <pivot:msg code="ui.label.cond.class"/>("+p_classViewCntStr+") X <pivot:msg code="ui.label.cond.time"/>("+g_timeCnt+") ="+g_multiplication+""; //분류 갯수 개별

	$("#changeTextLi").text(g_textField);
	// 탭메뉴 text세팅
	fn_changeTabText();

	// 분류 * 항목 조합수가 10,000 미만이면
	g_mixDimCnt = g_dimCo * g_timeCnt;

	// 2013.12.06 < 에서 <=로 변경
	// 2013.12.06 dimCo수를 체크하지 않도록 변경
	// dimCo수 다시 조정 시 주석위치 변경
		g_mixItemCnt = g_itemMultiply * g_timeCnt;				// 분류/항목 조합수 * 시점
		if(g_mixItemCnt > g_maxCell){
			$("#searchImg1").css("display", "none");
			$("#searchImg2").css("display", "block");

			$("#ico_swap").html("").html(swapOff);
			$("#ico_addfunc").html("").html(addfuncOff);

/*			<c:if test="${statInfo.downloadable}">
				$("#ico_download").html("").html(downGridOff);
				$("#ico_myscrap").html("").html(myscrapOff);
			</c:if>

			<c:if test="${statInfo.analyzable}">
				$("#ico_analysis").html("").html(assayOff);
			</c:if>*/

			$("#changeSpanRed").attr("class","f_gray");

			// 조합수가 200000 셀을 초과하면 dimCo 체크
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
			$("#searchImg2").css("display", "none");
			$("#searchImg1").css("display", "block");

			$("#ico_swap").html("").html(swapOn);
			$("#ico_addfunc").html("").html(addfuncOn);

/*			<c:if test="${statInfo.downloadable}">
				$("#ico_download").html("").html(downGridOn);
				$("#ico_myscrap").html("").html(myscrapOn);
			</c:if>

			<c:if test="${statInfo.analyzable}">
				if(g_assayYn == 'Y'){
					$("#ico_analysis").html("").html(assayOn);
				}
			</c:if>*/

			$("#changeSpanRed").attr("class","f_red");
			$("#changeSpanGray").attr("class","f_gray");
			$("#changeDownText").attr("class","f_gray");
		}
}

/************************************************************************
함수명 : fn_timeCountChk(timeCountObj,searchType)
설   명 : 시점탭에서 체크이벤트 함수(체크 true false 확인 후 count계산
인   자 : 1.checkbox = 선택된 object 객체
		 2.searchType
사용법 :
작성일 : 2013-10-02
작성자 : 국가통계포털 안영수

 date         author      note
 ----------   -------     -------------------
 2013-10-02      안영수              최초 생성
 ************************************************************************/
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

/************************************************************************
함수명 : fn_searchPeriod
설   명 : 셀렉트박스 2개에서 선택된 시점 정보로 주기에 해당하는 시점리스트 세팅
          셀렉트박스 2개이므로 first,last로 처리
          주기별 시점조회 공통
인   자 : searchType = 주기(D,T,M,B,Q,H,Y,F)
사용법 : fn_searchPeriod(searchType)
작성일 : 2013-08-08
작성자 : 국가통계포털 안영수

 date         author      note
 ----------   -------     -------------------
 2013-08-08      안영수              최초 생성
 ************************************************************************/
function fn_searchPeriod(searchType,rangeMode,p_rangeTimeArr){
	// 현재 체크된 갯수
	var  minusTimeChk = $('[name=timeChk'+searchType+']:checked').size();

	var startPrd =	$("#time"+searchType).find('select:first').val();
	var endPrd	 =  $("#time"+searchType).find('select:last').val();

	// validation 체크
	if(startPrd > endPrd){
		/*alert("<pivot:msg code="fail.reset.msg"/>");*/
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
					'dbUser'	 : "${ParamInfo.dbUser}"
				},
				success : function(response,status){
					var data = response.result ;
					var list = "";
					var chkStatus="";
					// 시점리스트 html만들기
					if(status ='success'){
						data.reverse();						// 정렬보다 빠름..전제조건은 Data가 뽑힐때 order by 정확하게 되어 있어야 함
						if(data!=null && data.length >0){
							for(var i=0;i<data.length;i++){
								var prdDe = data[i].prdDe;
								// 조회범위 상세설정 세팅
								if(rangeMode == "Y"){
									// 조회범위 상세설정에서 세팅된 시점만 체크상태
									var rangeTimeChk = $.inArray(prdDe,p_rangeTimeArr);
									if(rangeTimeChk > -1){
										chkStatus = "checked";

									}else{
										chkStatus ="";
									}
								}else{
									// 시점 조회 후 체크상태 true
									chkStatus = "checked";
								}
								// 상세설정 끝
								list+= "<li>\n";
								list+= "<input type=\"checkbox\" name=\"timeChk"+searchType+"\" onclick=\"fn_timeCountChk(this,\'"+searchType+"\');\" value='"+prdDe+"' "+chkStatus+">\n";
								list+= ""+fn_generatePrdDe(prdDe,searchType)+"";
								list+= "</li>\n";
							}

							// ie7인 경우 더미 li 생성
							if(g_browserVersion == "7.0"){
								list+="<input type=\"checkbox\" name='ie7bug' style='visibility:hidden'/>";
							}
							$('#searchPeriod'+searchType).html(list);

							// 화면에 세팅된 시점 갯수
							if(rangeMode == "Y"){
								g_timeCnt = g_rangeTimeCnt;
							}else{
								g_timeCnt = g_timeCnt - minusTimeChk + data.length;
							}
							fn_countView(g_itmCnt,g_classCnt,g_timeCnt,g_classViewCntStr);

						}else{
						//메세지 처리 해줄려면
						}
					}
				},
				error : function(error){
					/*alert( "<pivot:msg code="fail.common.msg"/>" );*/
				}
		});
}