/******************************************************** 
   파일명 : layout.js 
   설  명 : hover,클릭시 나타나는 효과 관련 script 
	수정일            수정자        Version      Function 명 
  ---------        --------    ----------    --------------- 
  20.06.30        김수빈            1.0              최초 생성 
  20.08.06        김수빈            1.0            스크립트 추가  
  20.08.28        김수빈            1.0            스크립트 추가
  20.09.23        김수빈            1.0            스크립트 추가   
  22.10.19        연진주            1.0            스크립트 추가   
  23.06.16        박승연            1.0            스크립트 추가   

*********************************************************/



/************************************************************************       
						  
   상단 메뉴 호버시 내려오는 서브메뉴 script	
   
************************************************************************/

$(document).ready(function () {
	//메뉴 over시
	$('.top_navi_li').mouseover(function () {
		$(this).children("a").addClass("top_navi_hover");
		$('#header').addClass("h_open");
	});
	$('.top_navi_li').mouseleave(function () {
		$(this).children("a").removeClass("top_navi_hover");
		$('#header').removeClass("h_open");
	});
	//메뉴 focus시
	$('.top_navi').focus(function () {
		$(this).addClass("top_navi_hover");
		$('#header').addClass("h_open");
	});
	$('.top_navi').blur(function () {
		$(this).removeClass("top_navi_hover");
		$('#header').removeClass("h_open");
	});
	$('.sub_navi a').focus(function () {
		$(this).parents(".top_navi_li").addClass("top_navi_hover");
		$('#header').addClass("h_open");
	});
	$('.sub_navi a').blur(function () {
		$(this).parents(".top_navi_li").removeClass("top_navi_hover");
		$('#header').removeClass("h_open");
	});
});


/************************************************************************        
						 
   메인 헤더고정 관련 Script
   
************************************************************************/
$(document).ready(function () {
	var hd_offset = $('#header').offset();
	$(window).scroll(function () {
		if ($(document).scrollTop() > hd_offset.top) {
			$('#header').addClass('h_fixed');
			$('#aside').addClass('h_fixed'); // 230616 SY Add
		}
		else {
			$('#header').removeClass('h_fixed');
			$('#aside').removeClass('h_fixed'); // 230616 SY Add
		}
	});
});



/************************************************************************        
						 
   footer에 있는 관련사이트 메뉴 클릭시 펼치기/서브 히스토리 메뉴클릭시 펼치기 
   
************************************************************************/

$(document).ready(function () {
	// 관련사이트 메뉴
	$('.family_site .r_site_title').click(function () {
		var submenu = $(this).prev("ul");
		// submenu 가 화면상에 보일때는 위로 접고 아니면 아래로 펼치기
		if (submenu.is(":visible")) {
			submenu.slideUp();
		} else {
			submenu.slideDown();
		}
	});
});

$(document).ready(function () {
	// 서브로케이션 메뉴
	$('.sub_l_menu > a').click(function () {
		var submenu = $(this).next("ul");
		// submenu 가 화면상에 보일때는 위로 접고 아니면 아래로 펼치기
		if (submenu.is(":visible")) {
			submenu.slideUp();
		} else {
			submenu.slideDown();
		}
	});
});





/************************************************************************        
						 
   오른쪽 사이드메뉴 검색  버튼 클릭시 효과 / 사이트맵 버튼 클릭시 효과
   
************************************************************************/
/*
$(document).ready(function(){
	$('.aside_search_btn').click(function() {
			// site_search의 높이가 150px일때는 위로 접고 아니면 아래로 펼치기
			if( $('.site_search').height() == 150 ){
				$('.site_search').removeClass("search_open");
			}else{
				$('.site_search').addClass("search_open");
			}
	});
});

$(document).ready(function(){
	$('html').click(function(e){
		if(!$(e.target).hasClass("site_search") && $('.site_search').height() == 150 ){
			$('.site_search').removeClass("search_open");
		}
	});
});*/

$(document).ready(function () {
	// 검색창
	$('.aside_search_btn').click(function () {
		var search = $('.site_search');
		// search 가 화면상에 보일때는 위로 접고 아니면 아래로 펼치기
		if (search.is(":visible")) {
			search.slideUp();
			$('.aside_search_btn').removeClass("close");
		} else {
			search.slideDown();
			$('.aside_search_btn').addClass("close");
		}
	});
});


//site map modal popup
$(document).ready(function () {
	$('.aside_sitemap_btn').click(function () {
		$('.modal_sitemap').show();
	});
	$('.modal_close').click(function () {
		$('.modal_sitemap').hide();
	});
});
$(document).ready(function () {
	$('.modal').click(function (e) {
		if ($(e.target).hasClass("modal")) {
			$('.modal_sitemap').hide();
		}
	});
});



/************************************************************************        
						 
   서브-통계원시자료 페이지에서 체크박스 체크시 배경색 채우기
   
************************************************************************/

$(document).ready(function () {
	$(".table_list table tr td input:checkbox").on('click', function () {
		if ($(this).prop('checked')) {
			$(this).parents("tr").addClass("chk_tr");
		} else {
			$(this).parents("tr").removeClass("chk_tr");
		}
	});
});



/************************************************************************        
						 
   서브-통계원시자료 페이지에서 설문조사 팝업 
   
************************************************************************/
//survey modal popup
/*$(document).ready(function(){
	$('.modal_survey_btn').click(function() {
		$('.modal_survey').show();
	});
	$('.modal_close').click(function() {
		$('.modal_survey').hide();
	});
});
$(document).ready(function(){
	$('.modal').click(function(e){
		if($(e.target).hasClass("modal")  ){
			$('.modal_survey').hide();
		}
	});
});*/





/************************************************************************        
						 
   하단 배너모음 슬라이드 제어 관련 Script
   
************************************************************************/
//banner link slide
$(document).ready(function () {
	$('.f_b_slide').slick({
		dots: false,
		infinite: true,
		speed: 500,
		slidesToShow: 4,
		pauseOnHover: true, //마우스올렸을때 멈춤기능
		autoplay: true,
		autoplaySpeed: 3500,
		variableWidth: true
	});
});

$(document).ready(function () {
	$('.f_b_btn > a').click(function () {
		if ($('.f_b_btn_stop').is(":visible")) {
			$('.f_b_slide').slick('slickPause');
			$('.f_b_btn_stop').hide();
			$('.f_b_btn_play').show();
		} else {
			$('.f_b_slide').slick('slickPlay');
			$('.f_b_btn_play').hide();
			$('.f_b_btn_stop').show();
		}
	});
});


/************************************************************************        
						 
   서브-주요지표 메뉴에서 그리드 숨기기버튼 클릭시 그리드 숨기는 Script	
   
************************************************************************/
$(document).ready(function () {
	// 그리드 숨기기 버튼
	$('.grid_hide').click(function () {
		var grid = $(this).next(".hide_table");
		// hide_table가 화면상에 보일때는 위로 접고 아니면 아래로 펼치기
		if (grid.is(":visible")) {
			$('.grid_hide').text('그리드 보기');
			grid.slideUp();
			$('.grid_hide').addClass('rotate');
		} else {
			$('.grid_hide').text('그리드 숨기기');
			grid.slideDown();
			$('.grid_hide').removeClass('rotate');
		}
	});
});


/************************************************************************        
						 
   메인인포그래픽/배너 슬라이드 제어 관련 Script
   
************************************************************************/

/*main infographic slide*/
$(document).ready(function () {
	var $status = $('.main_info .paging_info');
	var $slickElement = $('.info_slide');

	$slickElement.on('init reInit afterChange', function (event, slick, currentSlide, nextSlide) {
		//currentSlide is undefined on init -- set it to 0 in this case (currentSlide is 0 based)
		var i = (currentSlide ? currentSlide : 0) + 1;
		$status.html('<b>' + i + '</b><br>' + '/<br>' + slick.slideCount);
	});

	$('.info_slide').slick({
		dots: false,
		infinite: true,
		speed: 500,
		slidesToShow: 1,
		pauseOnHover: true, //마우스올렸을때 멈춤기능
		autoplay: true,
		autoplaySpeed: 3500,
		variableWidth: true
	});
	$('.info_slide_btn .pause').on('click', function () {
		$('.info_slide').slick('slickPause');
		$(this).hide();
		$('.info_slide_btn .play').show();
	});
	$('.info_slide_btn .play').on('click', function () {
		$('.info_slide').slick('slickPlay');
		$(this).hide();
		$('.info_slide_btn .pause').show();
	});

});


/*main banner slide*/
$(document).ready(function () {
	var $status = $('.banner_paging_info');
	var $slickElement = $('.banner_slide');

	$slickElement.on('init reInit afterChange', function (event, slick, currentSlide, nextSlide) {
		//currentSlide is undefined on init -- set it to 0 in this case (currentSlide is 0 based)
		var i = (currentSlide ? currentSlide : 0) + 1;
		$status.html('<span class="current">' + i + '</span>' + '<span class="count">/ ' + slick.slideCount + '</span>');
	});

	$('.banner_slide').slick({
		dots: false,
		infinite: true,
		speed: 500,
		slidesToShow: 1,
		pauseOnHover: true, //마우스올렸을때 멈춤기능
		autoplay: true,
		autoplaySpeed: 3500,
		variableWidth: true
	});
	$('.banner_slide_btn .pause').on('click', function () {
		$('.banner_slide').slick('slickPause');
		$(this).hide();
		$('.banner_slide_btn .play').show();
	});
	$('.banner_slide_btn .play').on('click', function () {
		$('.banner_slide').slick('slickPlay');
		$(this).hide();
		$('.banner_slide_btn .pause').show();
	});

});

/************************************************************************        
						 
   Placeholder label태그로 대체하기 위한 Script	
   
************************************************************************/

jQuery(document).ready(function () {

	var placeholderTarget = $(".placeholder_box input[type=text]");

	placeholderTarget.focus(function () {
		$(this).siblings("label").fadeOut("fast");
	});

	placeholderTarget.focusout(function () {
		if ($(this).val() == "") {
			$(this).siblings("label").fadeIn("fast");
		}
	});

});

/************************************************************************        
						 
   OpenAPI 도움말 버튼 관련 Script	
   
************************************************************************/
$(document).ready(function () {
	//메뉴 hover시
	$('.stat_top .db_tab ul li:last-child').mouseover(function () {
		$(this).children('#c_tab').addClass('bg_on');
	});
	$('.stat_top .db_tab ul li:last-child').mouseleave(function () {
		$(this).children('#c_tab').removeClass('bg_on');
	});

	// 메뉴 focus시
	$(document).on('focus', '#api_help_btn', function () {
		$(this).parents('.stat_top .db_tab ul li:last-child').children('#c_tab').addClass('bg_on');
	});
	$(document).on('blur', '#api_help_btn', function () {
		$(this).parents('.stat_top .db_tab ul li:last-child').children('#c_tab').removeClass('bg_on');
	});
});


/************************************************************************        
						 
   통계DB 조사별 meta popup 관련 Script	
   
************************************************************************/

$(document).ready(function () {
	$('.icon_meta').click(function () {
		$('.modal_list').show();
	});
	$('.modal_meta_cl').click(function () {
		$('.modal_list').hide();
	});
});

// $(document).ready(function () {
// 	$('.modal').click(function (e) {
// 		if ($(e.target).hasClass("modal")) {
// 			$('.modal_list').hide();
// 		}
// 	});
// });