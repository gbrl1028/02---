/** 공통 */
var browserFlag = "";
var connLang = "ko";

/* 방송통계  */
var statUrl = "https://www.mediastat.or.kr";
var org_id = "005";

var browser = {
		//사용자의 브라우저 확인
		version: parseInt(navigator.appVersion),
		isIPhone: navigator.userAgent.indexOf("iPhone") != -1,
		isAndroid: navigator.userAgent.indexOf("Android") != -1,
		isNetscape: navigator.appName.indexOf("Netscape") != -1,
		isMicrosoft: navigator.appName.indexOf("Microsoft") != -1 ||  navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1
};

if(browser.isMicrosoft) {
	browserFlag = "microsoft";
} else if(browser.isIPhone) {
	browserFlag = "iphone";
} else if(browser.isAndroid) {
	browserFlag = "Android";
} else {
	browserFlag = "other";
}

$(document).ready(function() {
	connLang = $(location).attr("href").indexOf("/kor/") < 0 ? "en" : "ko";

	$('#Progress_Loading').hide();

	// 로딩바 동작
	$.ajaxSetup({
		complete:function(result) {
			$('#Progress_Loading').hide();
		},beforeSend: function(xhr) {
			$('#Progress_Loading').show();
			xhr.setRequestHeader(header, token);			            
        }
	});

	if($("#src_st_dt[type=text]").length > 0) {
		setDatePicker($("#src_st_dt"));

		$("#src_st_dt").next().appendTo("#st_icon");
	}

	if($("#src_ed_dt[type=text]").length > 0) {
		setDatePicker($("#src_ed_dt"));

		$("#src_ed_dt").next().appendTo("#ed_icon");
	}

	//PlaceHolder Setting
	var placeholderTarget = $(".placeholder_box input[type=text]");

	placeholderTarget.focus(function() {
		$(this).siblings("label").fadeOut("fast");
	});

	placeholderTarget.focusout(function() {
		if($(this).val() == ""){
			$(this).siblings("label").fadeIn("fast");
		}
	});

	// 텍스트가 이미 입력되어 있는 경우 label 숨기기
	if(placeholderTarget.val() != undefined && placeholderTarget.val() != "") {
		placeholderTarget.siblings("label").hide();
	}

	var replaceWord  = /[<>()%]/gi;	// 입력을 제한 할 특수문자의 정규식
    $("#keyword").on("focusout", function() {
        var x = $(this).val();

        if(x.length > 0) {
            if (x.match(replaceWord)) {
               x = x.replace(replaceWord, "");
            }

            $(this).val(x);
        }
    }).on("keyup", function() {
    	$(this).val($(this).val().replace(replaceWord, ""));
    });
});

/** 일반 통계표 팝업 */
function stat_view(tbl_id, lang, obj_var_id, itm_id, vw_cd, list_id) {
	var url = statUrl + "/statHtml/statHtml.do?orgId="+org_id+"&tblId=" + tbl_id + "&vw_cd=" + vw_cd + "&list_id=" + list_id +
	"&scrId=&seqNo=&language=" + lang + "&obj_var_id=" + obj_var_id + "&itm_id=" + itm_id + "&conn_path=I2&path=";

	window.open(url,"_blank","scrollbars=no,width=1150,height=840,top=10,left=10");
}

/** 일반 통계표 새탭 */
function stat_view2(tbl_id, lang, obj_var_id, itm_id, vw_cd, list_id) {
	var url = statUrl + "/statHtml/statHtml.do?orgId="+org_id+"&tblId=" + tbl_id + "&vw_cd=" + vw_cd + "&list_id=" + list_id +
	"&scrId=&seqNo=&language=" + lang + "&obj_var_id=" + obj_var_id + "&itm_id=" + itm_id + "&conn_path=I2&path=";

	window.open(url);
}

// 달력 연결
function setDatePicker(dateObj) {
	dateObj.datepicker({
		showOn: "both",
		buttonImage: "../../img/calendar_icon_2.png",
		buttonImageOnly: true,
		buttonText: "Select date",
		changeMonth: true,
		changeYear: true,
		showMonthAfterYear:true,	//	년도 뒤에 month 배치하기
		monthNames:['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		monthNamesShort:['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		dayNamesMin:['일','월','화','수','목','금','토'],
		dateFormat:'yymmdd',
	    onClose: function( selectedDate ) {
	    	if(dateObj.attr("id").indexOf("src_st_dt") != -1){
	    		$("#src_ed_dt").datepicker( "option", "minDate", selectedDate );
	    		$("#src_ed_dt").next().appendTo("#ed_icon");
	    	} else if(dateObj.attr("id").indexOf("src_ed_dt") != -1){
	    		$("#src_st_dt").datepicker( "option", "maxDate", selectedDate );
	    		$("#src_st_dt").next().appendTo("#st_icon");
	    	}
	    }
	});	
}

/** 파일 타입 확인 */
function typeCheck(path) {
	var lastidx = -1;

	lastidx = path.lastIndexOf('.');

	var extention = path.substring(lastidx+1,path.length);

	// 게시판 웹취약점 조치 20190903 수정 (**운영서버에 따로 추가하기)
	if((/sh|bat|js|htm|html|exe|jsp|cgi|php|php3|asp|inc|pl/gi).test(extention)) {
		return false;
	}

	return true;
}

/** 이미지 파일 타입 확인 */
function typeImgCheck(path) {
	var lastidx = -1;

	lastidx = path.lastIndexOf('.');

	var extention = path.substring(lastidx+1,path.length);

	if((/jpg|png|gif|bmp|jpeg|jpe|jfif/gi).test(extention)) {
		return true;
	} else {
		return false;
	}

	return true;
}

/** 파일 타입 확인 - 1개 */
function typeOneCheck(path, type) {
	var lastidx = -1;

	lastidx = path.lastIndexOf('.');

	var extention = path.substring(lastidx+1, path.length);

	// doc, ppt, xls : 자릿수 3자리만 확인
	if(extention != null && extention != undefined) {
		extention = extention.toLowerCase();

		if(extention.length > 3) {
			if(extention.substring(0, 3) == "doc" || extention.substring(0, 3) == "ppt" || extention.substring(0, 3) == "xls") {
				extention = extention.substring(0, 3);
			}
		}
	}

	if(type == extention) {
		return true;
	} else {
		return false;
	}

	return true;
}

/** 이메일 유효성 확인 */
function emailCheck(email) {
	var regExp = /([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;

	if (!regExp.test(email)) {
		if(connLang == "en") alert("Please enter your email address correctly.");
		else  alert("메일주소를 정확히 입력해주세요.");

		return false;
	}

	return true;
}

/** 전화번호 유효성 확인 */
function telNumCheck(tel) {
	var regTel = /^(0(2|3[1-3]|4[1-4]|5[1-5]|6[1-4]))(\d{3,4})(\d{4})$/;
	var regPhone = /^(?:(010\d{4})|(01[1|6|7|8|9]\d{3,4}))(\d{4})$/;
	var telNum = tel.replace(/-/gi, "")

	if (!regTel.test(telNum) && !regPhone.test(telNum)) {
		if(connLang == "en") alert("Please enter the correct phone number.");
		else  alert("전화번호를 정확히 입력해주세요.");

		return false;
	}

	return true;
}

/** 날짜 유효성 체크 */
function dateCheck(date) {
	var regExp = /^(19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])$/;

	if (!regExp.test(date)) {
		alert("날짜 형식에 맞게 입력해주세요. 예: 20150101");
		return false;
	}

	return true;
}

/** 숫자만 남김 */
function numOnlyChange(num) {
	var chgNum;

	chgNum = num.replace(/[^0-9]/g, '');

	return chgNum;
}

/** 숫자 + 콤마 */
function numCommaChange(num) {
	var chgNum = num;

	chgNum = num.replace(/[^0-9]/g, '');
	chgNum = chgNum.replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");

	return chgNum;
}

/** 숫자 + 하이픈 */
function numBarChange(num) {
	var chgNum = num;

	chgNum = num.replace(/[^0-9-]/g, '');

	return chgNum;
}

/** 페이지 이동 */
function linkPage(url, pageIndex, orderby, desc) {
	var form;

	form = $("#bodyForm");

	$("#pageIndex").val(pageIndex);

	if(orderby != null && orderby != "" && orderby != "undifined" && orderby != "null") form.append("<input type='hidden' id='orderby' name='orderby' value='" + orderby + "' />");
	if(desc != null && desc != "" && desc != "undifined" && desc != "null") form.append("<input type='hidden' id='desc' name='desc' value='" + desc + "' />");

	form.attr("action", url);
	form.attr("target", "_self");
	form.submit();
}

/** board_seq 얻어오기 */
function getBoardSeq() {
	var failMsg = "등록에 실패하였습니다.";

	if(connLang == "en") failMsg = "등록에 실패하였습니다.";

	$.ajax({
		url : "../../kor/board/NextBoardSeq.html",
		type : "POST",
		async: false,
		success : function(data) {
			if(data != null && data != "'" && data != 'undefined' ) {
				$("#board_seq").val(data);
			} else {
				alert(failMsg);
				return false;
			}
		},
		error : function() {
			alert(failMsg);
			return false;
		}
	});

	return true;
}

/** cont_seq 얻어오기 */
function getContReqSeq(msg) {
	$.ajax({
		url : "../../kor/contents/NextContReqSeq.html",
		type : "POST",
		async: false,
		success : function(data) {
			if(data != null && data != "'" && data != 'undefined' ) {
				$("#req_seq").val(data);
			} else {
				alert(msg);
				return false;
			}
		},
		error : function() {
			 alert(msg);
			return false;
		}
	});

	return true;
}

//입력된 문자열 길이 확인
function fnChkByte(obj, maxByte, btObj) {
	var str = obj.value;
	var str_len = str.length;

	var rbyte = 0;
	var rlen = 0;
	var one_char = "";
	var str2 = "";

	for (var i = 0; i < str_len; i++) {
		one_char = str.charAt(i);

		if (escape(one_char).length > 4) {
			rbyte += 2; //한글2Byte
		} else {
			rbyte++; //영문 등 나머지 1Byte
		}

		if (rbyte <= maxByte) {
			rlen = i + 1; //return할 문자열 갯수
		}
	}

	if (rbyte > maxByte) {
		str2 = str.substr(0, rlen); //문자열 자르기
		obj.value = str2;
		fnChkByte(obj, maxByte);
	} else {
		btObj.html(rbyte);
	}
}

/** 클립보드 복사
 * menuNm - chart : 도표로보기
 * 			   - event : 이벤트/당첨자발표
 */
function copyUrlInfo(obj, copyUrl, menuNm, lang) {
	var parent = obj.closest("div");
	var position = new Array();
	var clientX = 0;
	var clientY = 0;

	// 기존 메타보기 layer 제거
	$("[id='copyLayer']").each(function() {
		$(this).remove();
	});

	parent.append("<div id='copyLayer' class='copy_layer'></div>");

	if(menuNm == "chart") {
		clientX = 0;
		clientY = 95;
	} else if(menuNm == "event") {
		clientX = 0;
		clientY = 37;
	} else {
		clientX = 10;
		clientY = 50;
	}

	$("#copyLayer").css({
		'position':'absolute',
		'top':clientY+'px',
		'z-index':'4000'
	});

	$("#copyLayer").load("../../comm/CopyUrl.html?copyUrl="+copyUrl+"&lang="+lang);
	$("#copyLayer").show(500);
}

/** Form 초기화 */
function initForm(parentForm) {
	parentForm.attr("action", "../../kor/board/BoardForm.html");
	parentForm.attr("target","_self");
	parentForm.submit();
}

/* 통계표 컬럼 hide 함수 */
function fn_StatColHide(data){
	var chk = false; 						 // 항목/분류명 동일 시 (true)
	var trNm = $(".statGridTr");			 // row 명칭
	var titleRow = $(".title01[rowspan]");	 // 헤더 rowspan
	var gr_ycol;							 // 표측 컬럼
	var ycolCnt = titleRow.size();			 // 표측 컬럼 갯수
	
	// 리어모어 차트인 경우
	if(data != null && data != ""){
		gr_ycol = data.rmstatbean.gr_ycol;	 
		ycolCnt = gr_ycol.split(",").length; 
		$(".title01[rowspan=1]").removeAttr("rowspan"); // 병합되지 않은 rowspan 제거
	}
	
	// row 최소 갯수 (2개 이상)							
	if(trNm.size() > 1){
		var statGridTr = trNm.eq(0).children();		    // 통계표 첫번째 로우
		
		$(statGridTr).each(function(idx, data){
			// 표측 컬럼 갯수보다 작거나 같은 경우만
			if(idx <= (ycolCnt - 1)){
				var text = "";	// 비교 문자 1
				var temp = "";  // 비교 문자 2
				var startLine;	// 시작줄
				
				// 병합된 셀이 있는 경우
				if(titleRow.size() > 0){ 
					startLine = statGridTr.eq(0).attr("rowspan");
				} else { 
					startLine = 1;
				}
				
				// 비교할 문자 
				text = trNm.eq(startLine).children().eq(idx).text();
				
				$(trNm).each(function(idx2, data){
					if(idx2 >= startLine){
						temp = trNm.eq(idx2).children().eq(idx).text(); // 비교할 문자2
						
						// 시점 제외
						if(statGridTr.eq(idx).text() != "시점" || statGridTr.eq(idx).text() != "time"){
							// 동일 명칭의 row가 존재하는지 (항목 및 분류만) 
							if(text === temp){
								chk=true;
							} else {
								chk=false;
								return false;
							}	
						} else {
							chk=false;
							return false;
						}
					}
				});
				
				// 동일 row가 존재 하는 경우
				if(chk == true){
					$(trNm).each(function(idx3, data){	
						// 병합된 셀이 있을 경우 
						if(titleRow.size() > 0 && statGridTr.eq(0).attr("rowspan") != 1){
							if(idx3 != 1){	// 시점 제외
								trNm.eq(idx3).children().eq(idx).hide();												
							}
						} else {
							trNm.eq(idx3).children().eq(idx).hide();												
						}											
					});		
				}
			}
		});
	}
}

// 문자열 null 또는 공백 체크 함수
function isStringNullOrEmpty(val) {
    switch (val) {
        case "":
        case 0:
        case "0":
        case null:
        case false:
        case undefined:
        case typeof this === 'undefined':
            return true;
        default: return false;
    }
};

// 문자열 null 또는 공백 체크 함수 
function isStringNullOrWhiteSpace(val) {
    return this.isStringNullOrEmpty(val) || val.replace(/\s/g, "") === '';
};

// 문자열이 null 또는 공백 시 null 또는 기존 값 리턴
function nullIfStringNullOrEmpty(val) {
    if (this.isStringNullOrEmpty(val)) {
        return null;
    }
    return val;
};

// 엑셀 양식 다운
function excelFormDown(file_name) {
	var url="../../comm/downloadExcelForm.html";
	var obj="";
	obj += "<input type='hidden' id='file_name' name='file_name' value='" + file_name + "' />";

	$('<form action="'+ url +'" method=POST>'+ obj +'</form>').appendTo('body').submit().remove();
}

// 파일 다운로드
function fn_download(parentForm, file_seq, fileNm, relNm) {
	$("#file_seq").val(file_seq);
	$("#real_name").val(relNm);
	$("#file_name").val(fileNm);

	parentForm.attr("action", "../../fileDownload.html");
	parentForm.attr("target","_self");
	parentForm.attr("enctype","multipart/form-data");
	parentForm.submit();
}

//PDF 미리보기
function fnPdfPreview(parentForm, relNm, type) {
	if(!typeOneCheck(relNm, "pdf")) {
		if(connLang == "en"){
			alert("Only PDF can be previewed.");
		} else {
			alert("미리보기는 PDF만 가능합니다.");
		}

		return false;
	}

	if(type == null || type == "" || type == undefined) type = "file";

	$("#real_name").val(relNm);

	parentForm.attr("action", "../../viewer/viewPdfPreview.html?type=" + type);
	parentForm.attr("target", "_blank");
	parentForm.submit();

	parentForm.attr("target", "_self");
}

// 셀렉트박스 이벤트 함수 (10, 50, 100개씩 조회)
function fn_selectEvent(selectObj, url) {
	var pageUnit = "#pageUnit";
	var bodyForm = "#bodyForm";
	
	if(selectObj.value == 10) {
		$(pageUnit).val(10);
		$(bodyForm).attr("action", url);
		$(bodyForm).submit();
	}

	if(selectObj.value == 50) {
		$(pageUnit).val(50);
		$(bodyForm).attr("action", url);
		$(bodyForm).submit();
	}

	if(selectObj.value == 100) {
		$(pageUnit).val(100);
		$(bodyForm).attr("action", url);
		$(bodyForm).submit();
	}
}

// 게시판 웹취약점 조치 20190903 수정 (**운영서버에 따로 추가하기)
$(document).ready(function() {
	// 파일 선택
	$("input[name=down]").change(function() {
		var path = $(this).val();
		var fileSize = this.files[0].size;
		/*
		var maxSize = 104857600;  
		
		if(fileSize > maxSize){			
			alert("파일용량 100MB을 초과했습니다.");
			$(this).val("");
			return false;
		}
		*/
		if(path != "") {
			if(!typeCheck(path)) {
				alert("파일 확장자를 확인해주세요.");
				$(this).val("");
				return false;
			} 
		}
	});
});

// 셀 병합 함수
$.fn.rowspan = function(colIdx, isStats) {
    return this.each(function(){
        var that;
        $('tr', this).each(function(row) {
            $('td:eq('+colIdx+')', this).filter(':visible').each(function(col) {
                if ($(this).html() == $(that).html()
                    && (!isStats 
                            || isStats && $(this).prev().html() == $(that).prev().html()
                            )
                    ) {
                    rowspan = $(that).attr("rowspan") || 1;
                    rowspan = Number(rowspan)+1;

                    $(that).attr("rowspan",rowspan);

                    // do your action for the colspan cell here
                    $(this).hide();

                    //$(this).remove();
                    // do your action for the old cell here
                } else {
                    that = this;
                }          

                // set the that if not already set
                that = (that == null) ? this : that;
            });
        });
    });
}

// 메뉴별 방문자 수 체크 통신 함수
/*function fn_ajax_menuVisitCntChk(){
	var form = $("#topfrm");
	
	var domain = window.document.location.protocol + "//" + window.document.location.host;
	var uri = window.document.location.href;
	var currUri = "";
	var currUri2 = uri.replace(domain, '');
	
	if(currUri2 != "" && currUri2 != null){
		if(currUri2.indexOf("/main.html") == -1){
			var currUriArr = currUri2.split("/");
			
			for (var i = 2; i < currUriArr.length; i++) {
				currUri += "/" + currUriArr[i]; 
			}
			
			$.ajax({
				url : "../../sysUseStat/insertMenuPageView.html",
				type : "post",
				data : {
					'currUri'  : currUri,
					'currUri2' : currUri2,
					'menu_id'  : $("#menuId").val() 
				},
				success : function(data){
					if(data != null){
						if(data.isTrue == "false"){
							console.log("MenuPageViewChk error");
						}
					}
				},
				error: function(request, status, error){
					console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
				}
			});
		}
	}
}
}*/
// 게시물별 조회수 체크 통신 함수
function fn_ajax_postViewsChk(formObj, boardClass, postNo, menuId, postNm){
	var form = formObj;
	
	var board_gb = "";
	var post_no  = "";
	var menu_id  = "";
	var post_nm  = "";
	
	if(!isStringNullOrWhiteSpace(boardClass)){
		board_gb = boardClass;
	}
	
	if(!isStringNullOrWhiteSpace(postNo)){
		post_no = postNo;
	}
	
	if(!isStringNullOrWhiteSpace(menuId)){
		menu_id = menuId;
	}
	
	if(!isStringNullOrWhiteSpace(postNm)){
		post_nm = postNm;
	}
	
	if((menuId != null && menuId != "") && (postNo != null && postNo != "")){
		$.ajax({
			url : "../../sysUseStat/insertPostViews.html",
			type : "post",
			data : {
				'board_gb' : board_gb,
				'post_no'  : post_no,
				'menu_id'  : menu_id,
				'post_nm'  : post_nm
			},
			success : function(data){
				if(data != null){
					if(data.isTrue == "false"){
						console.log("PostViewsChk error");
					}
				}
			},
			error: function(request, status, error){
				console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			}
		});
	}	
}


function fnPrint(){
	window.open("../../comm/PrintContent.html","_blank","scrollbars=no,width=900,height=840,top=10,left=10");
}