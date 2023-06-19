/** 통계DB */
var tblCnt = 5;
var tabCnt = 0;

//url 구하기
function getContextPath(){
	var contextPath = window.location.href;
	var result = contextPath.substring(0,contextPath.indexOf("?"));
	return result;
}

$(document).ready(function() {
	
	if($("#fldList >li").hasClass("on")) {
		$("#fldList").closest(".db_content").scrollTop($("#fldList").find(".on").position().top);
		
		$(function () {
			if($("#fldDtList >li").hasClass("on") == false){
				$("#fldList > li").filter(".on").children().trigger("click");
			}
		});
	}
	
	if($("#fldDtList >li").hasClass("on")) {
		$("#fldDtList").closest(".db_content").scrollTop($("#fldDtList").find(".on").position().top);
		
		$(function () {
			$("#fldDtList > li").filter(".on").children().trigger("click");
		});
	}
	
	if($("#fldDtList2 >li").hasClass("on")) {
		$("#fldDtList2").closest(".db_content").scrollTop($("#fldDtList2").find(".on").position().top);
	}
	
	$(".stSearch").hide();
	
	// 마이 히스토리 버튼 클릭
	$("#btn_his").click(function() {
		fn_get_session();
		
		$("#aside").css("display", "none");
		$("#my_his_aside").css("display", "block");
	});
	
	// 통계 목록 화면 보이기
	$("#tab_list").click(function() {
		hideTblContent();
		removeSrcResult();
		//removeApi();
		var api_status = $("#api_status").val()
		if(api_status == 'Y'){
			getList($(this).find('a').attr("data-a"), 1, $(this).attr("data-b"));
			//$(".stat_grid").removeClass("api_grid")
			$("#api_status").val("")
			$("#search_tap").css('display', 'block')
			$("#tbl_list").css('display', 'block')
			$("#api_list").css('display', 'none')
			var vw_cd = $("#vw_cd").val()
			if(vw_cd == 'MT_ATITLE' || vw_cd == 'MT_BTITLE'){
			 $("#tbl_list > .stWrap").show();
			}
			$("#c_tab").removeClass("select")
			$("#a_tab").addClass("select")
		}else{
			$("#tbl_list").show();
			$("#api_list").hide();
		}	
		
		$(".stSearch").hide();
	});

	// 메뉴 클릭
	$("#tblLeftMenu ul li").click(function() {
		for(var i=0; i < $("#tblLeftMenu ul li").length; i++) {
			 $("#tblLeftMenu ul li a").eq(i).removeClass('select')
		}
		var vw_cd = $(this).find('a').attr("data-a");
		var menuId = $(this).find('a').attr("data-c");
		$("#menuId2").val(menuId);
		console.log("메뉴클릭 변경"+menuId);
		$(this).find('a').attr("class","select");
		hideTblContent();
		
		if($(this).find('a').attr("data-b") == 'OPENAPI'){
			//getApiList($(this).find('a').attr("data-a"), 1, $(this).attr("data-b"));
			//$(".stat_grid").addClass("api_grid")
			$("#api_status").val("Y")
			$("#search_tap").css('display', 'none')
			$("#tbl_list").css('display', 'none')
			$("#api_list").css('display', 'block')
			$("#api_list > .stWrap").show();
			$("#menunmsub").html("OpenAPI");
			$("#menunmsub_l").html("OpenAPI");
			$("#share_url").attr("onclick","copyUrlInfo($(this), encodeURIComponent('"+getContextPath()+"?rootId=" + rootId.value + "&vw_cd=" + vw_cd + "&menuId=" + menuId + "&api_status="+api_status.value+ "&statKind=" + statKind.value + "'), 'sns_url');");

		}else{
			getList($(this).find('a').attr("data-a"), 1, $(this).attr("data-b"));
			//$(".stat_grid").removeClass("api_grid")
			$("#api_status").val("")
			$("#search_tap").css('display', 'block')
			$("#tbl_list").css('display', 'block')
			$("#api_list").css('display', 'none')
			var vw_cd = $("#vw_cd").val()
			if(vw_cd == 'MT_ATITLE' || vw_cd == 'MT_BTITLE'){
			 $("#tbl_list > .stWrap").show();
			}
		}
		
	});

	// 통계표 화면 제거 이벤트 생성
	$("#tbl_title").on("click", "[id='delete']", function() {
		var index = $(this).parent().index() - 1;

		removeTblInfo(index);
	});

	// 통계표 화면 보이기 이벤트 생성
	$("#tbl_title").on("click", "[id='db_ListTitle']", function() {
		var index = $(this).parent().index() - 1;

		hideTblContent();
		$(".stWrap").hide();
		$(".stSearch").hide();
		//$(".api_wrap").hide();
		$("#tbl_list").show();
		$(this).parent().addClass("tab_on");
		$("[name='tbl']").eq(index).show();
	});

	// 분야별검색 이벤트 연결 - 상세분야검색
	$("#fldList").on("click", "li a", function() {
		hideTblContent();

		$("#fldList >li").each(function() {
			$(this).attr('class', 'tabmenu');
		});

		$(this).closest("li").attr('class', 'tabmenu tab_on');
		
		if($(this).attr("data-y") == "OPENAPI"){
			getApiList($(this).attr("data-a"), 2, '', $(this).attr("data-z"));
		}else{
			getList($(this).attr("data-a"), 2, '', $(this).attr("data-z"));
		}
		
	});

	// 상세분야별검색 이벤트 연결 - 상세분야검색
	$("#fldDtList").on("click", "li a", function() {
		hideTblContent();

		$("#fldDtList >li").each(function() {
			$(this).attr('class', '');
		});

		$(this).closest("li").attr('class', 'tab_on');

		if($(this).attr("data-y") == "OPENAPI"){
			getApiList($(this).attr("data-a"), 3, '', $(this).attr("data-z"));
		}else{
			getList($(this).attr("data-a"), 3,'', $(this).attr("data-z"));
		}
	});

	// 상세분야별검색 이벤트 연결 - 상세분야검색
	$("#fldDtList2").on("click", "li a", function() {
		hideTblContent();

		$("#fldDtList2 >li").each(function() {
			$(this).attr('class', '');
		});

		$(this).closest("li").attr('class', 'tab_on');

		getList($(this).attr("data-a"), 4);
	});

	// 통계표 화면 추가 - 통계표 목록 선택
	$("#tblList").on("click", "[id='showTblInfo']", function() {
		createTblShow($(this).attr("data-a"), $(this).attr("data-b"), $(this).attr("data-c"), $(this).attr("data-d"), $(this).attr("data-e"), $(this).attr("data-f"), $(this).attr("data-g"));
	});
	
	// OPENAPI 보기
	$("#api_list").on("click", "[class='showApi']", function() {
		fn_api($(this).attr("data-a"));
		$(".api_tapmenu").removeClass("tab_on")
		$(this).parent().addClass("tab_on")
	});
	
	$("#fldList").on("click", "[id='btn_api']", function(event) {
		$("#tbl_list").hide()
		$("#api_list").show()
		var list_id = $(this).attr("data-a")
		//getApiList($(this).find('a').attr("data-a"), 1, $(this).attr("data-b"));
		$("#a_tab").removeClass("select")
		$("#c_tab").addClass("select")
		$("#api_status").val("Y")
		fn_api($(this).attr("data-a"))
		$("#up_list_id").val(list_id)
		$("#search_tap").hide()
		$("#api_list > .stWrap").show()
		$("#menunmsub").html("OpenAPI");
		$("#menunmsub_l").html("OpenAPI");
		
		var vw_cd = $("#vw_cd").val();
		var menuId = $("#c_tab").attr("data-c");
		console.log("OPENAPI 작은버튼"+menuId);
		$("#share_url").attr("onclick","copyUrlInfo($(this), encodeURIComponent('"+getContextPath()+"?rootId=" + rootId.value + "&vw_cd=" + vw_cd + "&menuId=" + menuId + "&api_status="+api_status.value+ "&statKind=" + statKind.value + "'), 'sns_url');");
		
		if(list_id == '005_001') {
			$("#api_fldList").children().removeClass("tab_on")
			$("#005_001").addClass("tab_on")
		}
		if(list_id == '005_003') {
			$("#api_fldList").children().removeClass("tab_on")
			$("#005_003").addClass("tab_on")
		}
		if(list_id == '005_004') {
			$("#api_fldList").children().removeClass("tab_on")
			$("#005_004").addClass("tab_on")
		}
		//$(".api_tapmenu").removeClass("tab_on")
		//$(this).parent().addClass("tab_on")
	});

	// 메타정보 보기
	$("#tblList").on("click", "[id='showMeta']", function(event) {
		tblMetaInfo($(this).attr('data-a'), $(this).attr('data-b'), $(this), $(this).attr('data-c'), $(this).attr('data-d'), false);
	});

	// 통계표 화면 추가 - 검색결과 선택
	$("#srcResultList").on("click", "[id='showTblInfo']", function() {
		createTblShow($(this).attr("data-a"), $(this).attr("data-b"), $(this).attr("data-c"), $(this).attr("data-d"), $(this).attr("data-e"), $(this).attr("data-f"), $(this).attr("data-g"));
	});
	
	// OPENAPI 보기 - 검색결과 선택
	$("#srcResultList").on("click", "[id='showApi']", function() {
		fn_api($(this).attr("data-a"))
	});

	// 검색화면 - 메타정보 보기
	$("#srcResultList").on("click", "[id='showMeta']", function(event) {
		tblMetaInfo($(this).attr('data-a'), $(this).attr("data-b"), $(this), $(this).attr('data-c'), $(this).attr('data-d'), true);
	});

	// 통계표 검색
	$("#btn_search").click(function() {
		fn_search();
	});

	// 검색 키워드 이벤트
	$("#keyword_s").keydown(function (key) {
	    if (key.keyCode == 13) {
	    	fn_search();
	    	return false;
	    }
	    return true;
	});
	
	// 검색 결과 닫기 버튼 
	$("#search_close").click(function(){
		fn_searchClose();
	});
	
	// api 활용신청 닫기 버튼 
	$("#api_close").click(function(){
		$("#005_001").removeClass("tab_on")
		$("#005_003").removeClass("tab_on")
		$("#005_004").removeClass("tab_on")
		fn_apiClose();
	});
	
	// 1레벨 메타 정보 팝업 
	$("#fldList").on("click", ".icon_meta", function() {
		var tbl_id = $(this).attr("data-a");
		var vw_cd = $(this).attr("data-b");
		
		var unityId = vw_cd+"_"+tbl_id;
		
		$.ajax({
			url:"../../kor/tblInfo/metaInfoDetail.html",
			data : {
				"unity_id":unityId
			},
			dataType:"json",
			type:"POST",
			async : false,
			success : function(data) {
				if(data != null && data != "" && data != 'undefined' ) {
					$(".modal_meta").show();
					
					if(data.resultBean != null) {
						var file = "";
						if(data.resultBean.fileList.length > 0) {
							
							$.each(data.resultBean.fileList, function(i, item) {
								file += 
									'통계설명자료 <a href="javascript:;" class="btn basic_btn" id="fileDown" data-fileSeq="'+item.file_seq+'" data-fileNm="'+item.file_name+'" data-realNm="'+item.real_name+'" title="pdf 다운">설문지</a>';
							});
						} else {
							file += '통계설명자료';
						}
						
						
						$(".modal_title").append(file);
						
						var content = "";
						if(data.resultBean.stats_nm != null && data.resultBean.stats_nm != undefined && data.resultBean.stats_nm != "") content +='<tr><th>통계명</th><td>'+data.resultBean.stats_nm+'</td></tr>';
						if(data.resultBean.provd_year != null && data.resultBean.provd_year != undefined && data.resultBean.provd_year != "") content +='<tr><th>제공연도</th><td>'+data.resultBean.provd_year+'</td></tr>';						
						if(data.resultBean.stats_knd != null  && data.resultBean.stats_knd != undefined && data.resultBean.stats_knd != "") content +='<tr><th>통계종류</th><td>'+data.resultBean.stats_knd+'</td></tr>';						
						if(data.resultBean.lgl_basis != null && data.resultBean.lgl_basis != undefined && data.resultBean.lgl_basis != "") content +='<tr><th>법적근거</th><td>'+data.resultBean.lgl_basis+'</td></tr>';
						if(data.resultBean.examin_bcrn != null && data.resultBean.examin_bcrn != undefined && data.resultBean.examin_bcrn != "") content +='<tr><th>조사배경</th><td>'+data.resultBean.examin_bcrn+'</td></tr>';
						if(data.resultBean.examin_purps != null && data.resultBean.examin_purps != undefined && data.resultBean.examin_purps != "") content +='<tr><th>조사목적</th><td>'+data.resultBean.examin_purps+'</td></tr>';
						if(data.resultBean.examin_cycle != null && data.resultBean.examin_cycle != undefined && data.resultBean.examin_cycle != "") content +='<tr><th>조사주기</th><td>'+data.resultBean.examin_cycle+'</td></tr>';						
						if(data.resultBean.publict_schdul != null && data.resultBean.publict_schdul != undefined && data.resultBean.publict_schdul != "") content +='<tr><th>공표일정</th><td>'+data.resultBean.publict_schdul+'</td></tr>';						
						if(data.resultBean.examin_trget != null && data.resultBean.examin_trget != undefined && data.resultBean.examin_trget != "") content +='<tr><th>조사대상</th><td>'+data.resultBean.examin_trget+'</td></tr>';						
						if(data.resultBean.examin_mth != null && data.resultBean.examin_mth != undefined && data.resultBean.examin_mth != "") content +='<tr><th>조사방법</th><td>'+data.resultBean.examin_mth+'</td></tr>';
						if(data.resultBean.examin_pd != null && data.resultBean.examin_pd != undefined && data.resultBean.examin_pd != "") content +='<tr><th>조사기간</th><td>'+data.resultBean.examin_pd+'</td></tr>';
						if(data.resultBean.examin_cn != null && data.resultBean.examin_cn != undefined && data.resultBean.examin_cn != "") content +='<tr><th>조사내용</th><td>'+data.resultBean.examin_cn+'</td></tr>';						
						if(data.resultBean.writng_systm != null && data.resultBean.writng_systm != undefined && data.resultBean.writng_systm != "") content +='<tr><th>작성체계</th><td>'+data.resultBean.writng_systm+'</td></tr>';						
						if(data.resultBean.newest_dta != null && data.resultBean.newest_dta != undefined && data.resultBean.newest_dta != "") content +='<tr><th>최신자료</th><td>'+data.resultBean.newest_dta+'</td></tr>';
						if(data.resultBean.dta_search != null && data.resultBean.dta_search != undefined && data.resultBean.dta_search != "") content +='<tr><th>자료검색</th><td>'+data.resultBean.dta_search+'</td></tr>';						
						if(data.resultBean.chrg_instt != null && data.resultBean.chrg_instt != undefined && data.resultBean.chrg_instt != "") content +='<tr><th>담당기관</th><td>'+data.resultBean.chrg_instt+'</td></tr>';						
						if(data.resultBean.use_aptn != null && data.resultBean.use_aptn != undefined && data.resultBean.use_aptn != "") content +='<tr><th>이용시 유의점</th><td>'+data.resultBean.use_aptn+'</td></tr>';
						
						$(".table_meta table tbody").append(content);
					} 
		      	}
			}
		});
	});
	
});

// 통계표 검색
function fn_search() {
	var gubun = "kor";
	var vw_cd = $("#vw_cd").val();
	var api_status = $("#api_status").val();
	
	if(vw_cd == 'MT_ATITLE'){		
		if(api_status == 'Y'){
			var vwNm = 'OPENAPI';
		} else {
			var vwNm = '조사별 통계 ';
		}
	}else{
		var vwNm = '주제별 통계 ';
	}
	
	if($("#keyword_s").val() == "") {
		alert("검색하실 내용을 입력하세요.");

		$("#keyword_s").focus();

		return false;
	} else {
		$(".stWrap").hide();
		$("[name=tbl]").hide();
		//$(".api_wrap").hide();
		
		hideTblStatmeta();
		
		if($("#siteGb").val() == 'SITE002' || $("#siteGb").val() == 'SITE004')
			gubun = "en";
		
		$.ajax({
			url : "../../" + gubun + "/tblInfo/TblInfoSearchList.html",
			data :  { vw_cd : vw_cd, up_list_id : "0", lvl : $("#max_lvl").val(), keyword : $("#keyword_s").val(), api_status : api_status },
			type : "POST",
			async: false,
			success : function(data) {
				if(data != null && data != "'" && data != 'undefined' ) {
					// 통계표
					if(data.tblList == null ) {
						$("#tblList").html("");
					} else {
						
						$(".result").html(vwNm+"\'<span class='keyword'> "+$("#keyword_s").val() +"</span>'에 대한 검색결과가 <span class='count'>" + data.tblList.length + "건</span>입니다");
						
						if(api_status == 'Y'){
							$("#srcResultList").html(createApiHtml(data.tblList, data.tblInfoBean, true, true));
						} else {
							$("#srcResultList").html(createHtml(data.tblList, data.tblInfoBean, true, true));
						}
						
						
						
						$(".stSearch").show();
					}
				}
			},
			error : function() {
				return false;
			}
		});
		
		return true;
	}
}

// 통계표 화면 제거
function removeTblInfo(index) {
	$("[name='top_tab']").eq(index).remove();
	$("[name='tbl']").eq(index).remove();

	tabCnt -= 1;
	
	if($("[name=top_tab]").length > 0){
		$(".stat_tab").find("[id='db_ListTitle']").last().click();
	} else {
		var api_status = $("#api_status").val()
		if(api_status == 'Y'){
			$("#tbl_list").hide()
			$("#api_list > .stWrap").show();
		} else {
			$("#tbl_list > .stWrap").show();
		}	
	}
}

// 통계표 화면 제거2
function removeTblInfo2(index) {
	$("[name='top_tab']").eq(index).remove();
	$("[name='tbl']").eq(index).remove();
	
	tabCnt -= 1;
}

// 통계표 숨기기
function hideTblContent() {
	hideTblStatmeta();

	$("[name='tbl']").each(function() {
		$(this).hide();
	});

	for(var i=0; i < tblCnt; i++) {
		$("[name='top_tab']").eq(i).removeClass("tab_on");
	}
}

// 첫번째 통계표 닫기
function removeFstTblInfo() {
	hideTblContent();

	for(var i=0; i < tblCnt; i++) {
		if($("[name='tbl']").eq(i).length > 0) {
			removeTblInfo2(i);
			break;
		}
	}
}

/** 목록 및 통계표 조회 */
function getList(tbl_id, level, title, up_up_list_id_param) {
	var gubun = "kor";
	if($("#siteGb").val() == 'SITE002' ||$("#siteGb").val() == 'SITE004') gubun = "en";
	
	var url = "../../" + gubun + "/tblInfo/TblInfoFieldList.html";
	var vw_cd = $("#vw_cd").val();
	var up_list_id = tbl_id;
	var up_up_list_id = up_up_list_id_param;
	
	if(title == "OPENAPI"){
		$("#api_status").val("Y");
	}
	
	if(level == 1) {
		url = "../../" + gubun + "/tblInfo/TblInfoList.html";
		$("#up_list_id").val("0");
		$("#vw_cd").val(tbl_id);
		
		if(title == "OPENAPI"){
			$("#api_status").val("Y");
		}

		vw_cd = tbl_id;
		up_list_id = "0";
	}

	removeSrcResult();	
	//removeApi();
	
	$.ajax({
		url : url,
		data :  { vw_cd : vw_cd, up_list_id : up_list_id, lvl : level, re_search : "Y", up_up_list_id :  up_up_list_id },
		type : "POST",
		async: false,
		success : function(data) {
			if(data != null && data != "'" && data != 'undefined' ) {
				//korLayout 메뉴명 변경
				$("#menunmsub").html(data.menunmsub);
				$("#menudescsub").html(data.menudescsub);
				$("#menunmsub_l").html(data.menunmsub);
			
				console.log("리스트 조회"+menuId2.value);
				$("#share_url").attr("onclick","copyUrlInfo($(this), encodeURIComponent('"+getContextPath()+"?rootId=" + rootId.value + "&vw_cd=" + vw_cd + "&menuId=" + menuId2.value + "&statKind=" + statKind.value + "'), 'sns_url');");
				
				if(level == 1) {
					// 분야별검색
					$(".db_content > #fldList").html("");
					$(".db_content > #fldDtList").html("");
					$(".db_content > #fldDtList2").html("");
					$(".db_content > #tblList").html("");

					if(data.fldList != null) $("#fldList").html(createHtml(data.fldList, data.tblInfoBean));

					if(data.tblInfoBean.max_lvl > 3) {
						$('#tbl_list > .stat_01').attr('class','stat_list stat_02 stWrap');		// 통계표 사이즈 변경
						
					} else {
						$('#tbl_list > .stat_02').attr('class','stat_list stat_01 stWrap');		// 통계표 사이즈 변경
					}

					$("#max_lvl").val(data.tblInfoBean.max_lvl);

					if(data.tblInfoBean.siteGb == "SITE002") $(".db_top span").html("Users can search statistics '" + title + "'.");
					else if(data.tblInfoBean.siteGb == "SITE004") $(".db_top span").html("Users can search statistics '" + title + "'.");
					else $(".db_top span").html("* '" + title + "'통계를 이용할 수 있습니다");
				} else if(level == 2) {
					$("#fldDtList").html("");
					$("#fldDtList2").html("");
					$("#tblList").html("");

					 if(data.list != null) $("#fldDtList").html(createHtml(data.list, data.tblInfoBean));
				} else if(level == 3) {
					if($("#max_lvl").val() > 3) {
						if(data.list != null) $("#fldDtList2").html(createHtml(data.list, data.tblInfoBean));

						$("#tblList").html("");
					} else {
						if(data.list != null) $("#tblList").html(createHtml(data.list, data.tblInfoBean, true));
					}
				} else {
					if(data.list != null) $("#tblList").html(createHtml(data.list, data.tblInfoBean, true));
				}
			}
		},
		error : function() {
	        return false;
	    }
	});

	return true;
}

/** 목록 및 통계표 조회 */
function getApiList(tbl_id, level, title, up_up_list_id_param) {
	var gubun = "kor";
	if($("#siteGb").val() == 'SITE002' ||$("#siteGb").val() == 'SITE004') gubun = "en";
	
	var url = "../../" + gubun + "/tblInfo/TblInfoFieldList.html";
	var vw_cd = $("#vw_cd").val();
	var up_list_id = tbl_id;
	var up_up_list_id = up_up_list_id_param;
	var api_status = "Y"
	
	if(level == 1) {
		url = "../../" + gubun + "/tblInfo/TblInfoList.html";
		$("#up_list_id").val("0");
		$("#vw_cd").val(tbl_id);		
		$("#api_status").val("Y");		

		vw_cd = tbl_id;
		up_list_id = "0";
		api_status = "Y"
	}

	removeSrcResult();
	removeApi();
	
	$.ajax({
		url : url,
		data :  { vw_cd : vw_cd, up_list_id : up_list_id, lvl : level, re_search : "Y", up_up_list_id :  up_up_list_id, api_status : api_status },
		type : "POST",
		async: false,
		success : function(data) {
			if(data != null && data != "'" && data != 'undefined' ) {				
				//korLayout 메뉴명 변경
				$("#menunmsub").html(data.menunmsub);
				$("#menudescsub").html(data.menudescsub);
				$("#menunmsub_l").html(data.menunmsub);
				
				
				if(level == 1) {
					// 분야별검색
					$(".db_content >ul").html("");

					if(data.fldList != null) $("#api_fldList").html(createApiHtml(data.fldList, data.tblInfoBean));

					if(data.tblInfoBean.max_lvl > 3) {
						$('.stat_01').attr('class','stat_list stat_02 stWrap');		// 통계표 사이즈 변경
						
					} else {
						$('.stat_02').attr('class','stat_list stat_01 stWrap');		// 통계표 사이즈 변경
					}

					$("#max_lvl").val(data.tblInfoBean.max_lvl);

					if(data.tblInfoBean.siteGb == "SITE002") $(".db_top span").html("Users can search statistics '" + title + "'.");
					else if(data.tblInfoBean.siteGb == "SITE004") $(".db_top span").html("Users can search statistics '" + title + "'.");
					else $(".db_top span").html("* '" + title + "'통계를 이용할 수 있습니다");
				} else if(level == 2) {
					$("#fldDtList").html("");
					$("#fldDtList2").html("");
					$("#tblList").html("");

					 if(data.list != null) $("#fldDtList").html(createApiHtml(data.list, data.tblInfoBean));
				} else if(level == 3) {
					if($("#max_lvl").val() > 3) {
						if(data.list != null) $("#fldDtList2").html(createApiHtml(data.list, data.tblInfoBean));

						$("#tblList").html("");
					} else {
						if(data.list != null) $("#tblList").html(createApiHtml(data.list, data.tblInfoBean, true));
					}
				} else {
					if(data.list != null) $("#tblList").html(createApiHtml(data.list, data.tblInfoBean, true));
				}
			}
		},
		error : function() {
	        return false;
	    }
	});

	return true;
}

// 리스트 생성
function createHtml(list, tblInfoBean, tblYn, searchYn) {
	var html = "";
	var lang = (tblInfoBean.siteGb == "SITE002" || tblInfoBean.siteGb == "SITE004") ? "en" : "ko";

	if(list.length > 0) {
		for(var i in list) {
			html += "<li><a href='javascript:;' ";

			if(tblYn == true) html += "id='showTblInfo'";

			html += "data-a='" + list[i].tbl_id + "' data-b='" + list[i].list_nm + "' data-c='" + list[i].vw_cd + "' data-d='" + list[i].list_id + "' data-e='" + lang + "' data-z='" + list[i].up_up_list_id;			

			html += "'>";

			if(searchYn) {
				var vw_cd ="";
				if(list[i].vw_cd == 'MT_ATITLE'){
					vw_cd = "조사별통계";
				}else{
					vw_cd = "주제별통계";
				}
				
				var first = list[i].list_path.substring(0, list[i].list_path.lastIndexOf(' > '));
				var last = list[i].list_path.substring(list[i].list_path.lastIndexOf(' > '), list[i].list_path.length);

				html += vw_cd+" > "+ first + last.replace($("#keyword_s").val(), "<span class='keyword'>" + $("#keyword_s").val() + "</span>");
			} else {
				html += list[i].list_nm;
			}

			html +="</a>";
			if(list[i].vw_cd == 'MT_ATITLE'){
				if(list[i].list_id == '005_001') {
					html += "<a class='btnLink btn_api' href='javascript:;' id='btn_api' data-a=" + list[i].list_id + ">OpenAPI</a>"
				}
				if(list[i].list_id == '005_003') {
					html += "<a class='btnLink btn_api' href='javascript:;' id='btn_api' data-a=" + list[i].list_id + ">OpenAPI</a>"
				}
				if(list[i].list_id == '005_004') {
					html += "<a class='btnLink btn_api' href='javascript:;' id='btn_api' data-a=" + list[i].list_id + ">OpenAPI</a>"
				}
			}
			if(list[i].up_list_id == '0'){
				if(list[i].metaInfoUseAt == 'Y') {
					html += "<a href='javascript:;' class='f_s_0 icon_meta' title='메타정보' data-a='"+list[i].tbl_id+"'data-b='"+list[i].vw_cd+"'>메타정보</a>";
				}
			}

			html +="</li>";
		}
	}

	return html;
}

function createApiHtml(list, tblInfoBean, tblYn, searchYn) {
	var html = "";
	var lang = (tblInfoBean.siteGb == "SITE002" || tblInfoBean.siteGb == "SITE004") ? "en" : "ko";
	
	if(list.length > 0) {
		for(var i in list) {
			if(list[i].list_type == "TBL"){
				html += "<li><a class='statTitle_api' href='#' ";
			}else{
				html += "<li id='" + list[i].tbl_id + "' class='tabmenu api_tapmenu'><a href='javascript:;' class='showApi'";
			}
			

			// if(tblYn == true) html += "id='showTblInfo'";

			html += "data-a='" + list[i].tbl_id + "' data-b='" + list[i].list_nm + "'data-y='OPENAPI'";			

			html += "'>";
			
			if(searchYn) {
				var vw_cd ="OPENAPI";
				
				
				var first = list[i].list_path.substring(0, list[i].list_path.lastIndexOf(' > '));
				var last = list[i].list_path.substring(list[i].list_path.lastIndexOf(' > '), list[i].list_path.length);

				html += vw_cd+" > "+ first + last.replace($("#keyword_s").val(), "<span class='keyword'>" + $("#keyword_s").val() + "</span>");
			} else {
				html += list[i].list_nm;
			}
			
			html +="</a>";
			if(list[i].list_type == "TBL"){
				html += "<a class='btnLink btn_stat' href='javascript:;' ";
				if(tblYn == true) html += "id='showTblInfo'";

				html += "data-a='" + list[i].tbl_id + "' data-b='" + list[i].list_nm + "' data-c='" + list[i].vw_cd + "' data-d='" + list[i].list_id + "' data-e='" + lang + "'data-y='OPENAPI' data-z='" + list[i].up_up_list_id;			
	
				html += "'>";
				
				html += "통계표 조회</a>"
				let top_list_id = list[i].up_list_id.substr(0, 7)
				html += "<a class='btnLink btn_api' href='javascript:;' id='showApi' data-a='" + top_list_id + "'>API</a>"
			}			
			html +="</li>";
		}
	}

	return html;
}

// 검색결과 화면 제거하기
function removeSrcResult() {
	hideTblStatmeta();

//	$("#keyword_s").val("");
	$("#srcResultList").html("");
	$(".stSearch").hide();
	
	var api_status = $("#api_status").val()
	if(api_status == 'Y'){
		$("#api_list > .stWrap").show();
	}else{
		$("#tbl_list > .stWrap").show();
	}		
}

// api 활용신청 화면 제거하기
function removeApi() {
	hideTblStatmeta();
	
	$("#srcResultList").html("");
	//$(".api_wrap").hide();
	$(".stWrap").show();
}

// 통계표 화면 추가
function createTblShow(tbl_id, tblTitle, vw_cd, list_id, lang, itm_id, obj_var_id) {
	var titleHtml = "";
	var contentHtml = "";
	var confirmMsg = "통계표는 최대 " + tblCnt +"개까지만 여실 수 있습니다.\n\n첫번째 통계표를 닫으시고\n선택하신 통계표를 여시겠습니까?";
	
	// 마이 히스토리 관련 세션 저장 및 불러오기
	if(lang == "ko"){
		fn_set_session(tbl_id, tblTitle, lang);
		
		if($("#my_his_aside").css("display") != "none"){
			fn_get_session();
		}
	}
	
	$("[name=top_tab]").removeClass("tab_on");
	
	removeSrcResult();
	removeApi();
	hideTblStatmeta();
	
	// 같은 통계표 탭을 누른 경우
	if($("[id='tab"+tbl_id+"']").length > 0) {
		hideTblContent();
		$(".stWrap").hide();
		$(".stSearch").hide();
		
		$("#tab" + tbl_id).addClass("tab_on");
		$("#tbl" + tbl_id).show();
		
		return;
	}

	// 통계표 탭이 5개 이상인 경우
	if(tabCnt >= tblCnt) {
		if(confirm(confirmMsg)) {
			removeFstTblInfo();
		} else {
			return;
		}
	}
	
	// 타이틀 html 추가
	titleHtml += "<li id='tab" + tbl_id +"' name='top_tab' class='top_tab'>";
	titleHtml += "	<a href='javascript:;' class='title' id='db_ListTitle'>" + tblTitle + "</a>";
	titleHtml += "	<a href='javascript:;' class='close f_s_0 tab_x' id='delete'>x</a>";
	titleHtml += "</li>";

	$("#tbl_title").append(titleHtml);
	
	$("#tab" + tbl_id).addClass("tab_on");

	contentHtml += "<div id='tbl" + tbl_id + "' name='tbl' class='db_stats grid_box'>";
	contentHtml += "	<iframe id='tblInfoFrame' name='tblInfoFrame' frameborder='0' width='100%' height='800' scrolling='yes' title='" + (lang == "en" ? "Statistical DB" : "통계DB")+ "' ";
	contentHtml += "	src='" + statUrl + "/statHtml/statHtml.do?mode=tab&amp;orgId=005&amp;tblId="+ tbl_id + 
						"&amp;vw_cd=" + vw_cd + "&amp;list_id=" + list_id + "&amp;scrId=&amp;seqNo=&amp;language=" + lang + 
						"&amp;obj_var_id=" + obj_var_id + "&amp;itm_id=" + itm_id + "&amp;conn_path=" + vw_cd + "&amp;path='></iframe>";
	contentHtml += "</div>";
	$(".stWrap").hide();
	$("#tbl_list").append(contentHtml);

	tabCnt += 1;
}

// 메타정보 조회
function tblMetaInfo(vw_cd, tbl_id, obj, siteGb, list_id, searchYn) {
	
	// 기존 메타보기 layer 제거
	$("[id='meta_info']").each(function() {
		$(this).remove();
	});
	
	if(searchYn){
		$("#srcResultList").after("<div id='meta_info' class='meta_popup'></div>");
	} else {
		$("#tblList").after("<div id='meta_info' class='meta_popup'></div>");
	}

    $("#meta_info").css({
		'top':'65px'
    });
	
	$("#meta_info").load("../../comm/TblMetaInfo.html?vw_cd="+vw_cd+"&tbl_id="+tbl_id+"&siteGb="+siteGb+"&list_id="+list_id);
	$("#meta_info").show(500);
}

function hideTblStatmeta() {
	if($('#meta_info').length > 0)
		$('#meta_info').remove();
}

function fn_searchClose(){
	hideTblStatmeta();

	$("#keyword_s").val("");
	$("#srcResultList").html("");
	$(".stSearch").hide();
	
	var topTabCnt = $("[name=top_tab]").length;
		
	if(topTabCnt > 0){
		if($("[id=tbl_title]").find(".tab_on").length > 0){
			var tbl_id = $(".tab_on").attr("id");
			
			$("#" + tbl_id.replace("tab", "tbl")).show();
		} else {
			$(".stWrap").show();
		}
	}else{
		$(".stWrap").show();
	}
}

// 통계 테이블 세션 값 저장 함수
// 20개까지 저장, 중복 체크
function fn_set_session(tblId, tblTitle, lang) {	

	var obj = {
	    tbl_id : tblId,
	    tbl_title : tblTitle,
	    lang : lang
	};

	var isDup = false;
	
	// 중복 체크
	$(tblInfoArr).each(function(idx, item){
		var tblId = item.tbl_id;
		var objTblId = obj.tbl_id;
		
		if(tblId == objTblId){
			isDup = true;
		} 
	});
	
	if(!isDup){
		tblInfoArr.push(obj);
		
		if(tblInfoArr.length > 20){
			//20개까지만 저장
			tblInfoArr.shift();
		}
	}
	
	sessionStorage.setItem("tblInfo", JSON.stringify(tblInfoArr));
}

// 활용신청 화면
function fn_api(up_list_id) {
	var gubun = "kor";
	var vw_cd = $("#vw_cd").val();
	var up_list_id = up_list_id
	var api_status = $("#api_status").val();
	
	if(vw_cd == 'MT_ATITLE'){		
		if(api_status == 'Y'){
			var vwNm = 'OPENAPI';
		} else {
			var vwNm = '조사별 통계 ';
		}
	}else{
		var vwNm = '주제별 통계 ';
	}
	
	//$(".stSearch").hide();
	//$(".stWrap").hide();
	//$("[name=tbl]").hide();
	
	hideTblStatmeta();
	
	if($("#siteGb").val() == 'SITE002' || $("#siteGb").val() == 'SITE004')
		gubun = "en";
	
	$.ajax({
		url : "../../" + gubun + "/tblInfo/OpenApiApplication.html",
		data :  { vw_cd : vw_cd, up_list_id : up_list_id},
		type : "POST",
		async: false,
		success : function(data) {
			if(data != null && data != "'" && data != 'undefined' ) {
				$(".api_wrap").show();
				
				$("#up_list_id").val(data.resultBean.up_list_id)
				if(data.resultBean.up_list_id == "005_001"){
					$(".api_top h3").text("방송매체 이용행태조사")
					$("#dateModified").text("2022-11-29")
					$("#totalCountSample").text("329")
					$("#tbl_id_sample").text("DT_164002_A001")
					$("#list_id_sample").text("005_001_001")
					$("#up_list_id_sample").text("005_001")
					$("#list_nm_sample").text("가구별 설문 통계표")
					$("#up_list_nm_sample").text("방송매체 이용행태조사")
					$("#tbl_nm_sample").text("[가구]응답자 분포표")
					$("#prd_de_sample").text("2011~2020")
					$("#prd_se_sample").text("년")
					$("#stat_nm_sample").text("방송통신위원회, 방송매체 이용행태 조사")
					$("#tbl_url_sample").text("https://www.mediastat.or.kr/statHtml/statHtml.do?orgId=005&tblId=DT_164002_A001")					
				} else if(data.resultBean.up_list_id == "005_003"){
					$(".api_top h3").text("KI시청자평가지수 조사")
					$("#dateModified").text("2022-11-25")					
					$("#totalCountSample").text("4")
					$("#tbl_id_sample").text("DT_KCCI_003")
					$("#list_id_sample").text("005_003_001")
					$("#up_list_id_sample").text("005_003")
					$("#list_nm_sample").text("종합편성채널")
					$("#up_list_nm_sample").text("KI시청자평가지수 조사")
					$("#tbl_nm_sample").text("종편 시청자평가지수")
					$("#prd_de_sample").text("2012~2020")
					$("#prd_se_sample").text("년")
					$("#stat_nm_sample").text("방송통신위원회, KI 시청자평가지수 조사")
					$("#tbl_url_sample").text("https://www.mediastat.or.kr/statHtml/statHtml.do?orgId=005&tblId=DT_KCCI_003")
				} else if(data.resultBean.up_list_id == "005_004"){
					$(".api_top h3").text("방송산업 실태조사")
					$("#dateModified").text("2022-11-29")					
					$("#totalCountSample").text("47")
					$("#tbl_id_sample").text("DT_920010_0001")
					$("#list_id_sample").text("005_004_001")
					$("#up_list_id_sample").text("005_004")
					$("#list_nm_sample").text("방송사업자수")
					$("#up_list_nm_sample").text("방송산업 실태조사")
					$("#tbl_nm_sample").text("방송매체별 사업자 수")
					$("#prd_de_sample").text("2008~2019")
					$("#prd_se_sample").text("년")
					$("#stat_nm_sample").text("과학기술정보통신부·방송통신위원회, 방송산업 실태조사")
					$("#tbl_url_sample").text("https://www.mediastat.or.kr/statHtml/statHtml.do?orgId=005&tblId=DT_920010_0001")
				}
				
				$("#dateModified").text(data.resultBean.dateModified)
				$("#encodingFormat").text(data.resultBean.encodingFormat)
			}
		},
		error : function() {
			return false;
		}
	});
	
	return true;	
}
	
function fn_apiClose(){
	hideTblStatmeta();

	$("#keyword_s").val("");
	$("#srcResultList").html("");
	$(".api_wrap").hide();
	
	var topTabCnt = $("[name=top_tab]").length;
		
	if(topTabCnt > 0){
		if($("[id=tbl_title]").find(".tab_on").length > 0){
			var tbl_id = $(".tab_on").attr("id");
			
			$("#" + tbl_id.replace("tab", "tbl")).show();
		} else {
			$(".stWrap").show();
		}
	}else{
		$(".stWrap").show();
	}
}