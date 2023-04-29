<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>통계표 조회 테스트 페이지</title>
</head>
<body>
<div>
	<ul>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1EZ0002" target="_blank">101, DT_1EZ0002(대푯값)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B8000F" target="_blank">101, DT_1B8000F</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B26001_A01" target="_blank">101, DT_1B26001_A01</a>
		</li>		
		<br/>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B8000F&empId=micari" target="_blank">101, DT_1B8000F(마이스크랩 저장)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=141&tblId=DT_14103_A000" target="_blank">
			141, DT_14103_A000(기본조회 : 월 주기의 시점6개)
			</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=141&tblId=DT_14103_A000&empId=micari&scrId=2" target="_blank">
			141, DT_14103_A000(마이스크랩 정보조회, WEB표준 : 시점 표측, 최근시점기준으로 월 2개,년 11개 - 온실가스유형별 분류값 2개만 저장됨)
			</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=141&tblId=DT_14103_A000&empId=micari&scrId=1004" target="_blank">
			141, DT_14103_A000(마이스크랩 정보조회, SIGA : 시점 표두, M201012_M201112,Y2008_Y2011)
			</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=141&tblId=DT_14103_A000&empId=micari&scrId=1003" target="_blank">
			141, DT_14103_A000(마이스크랩 정보조회, SIGA : 시점 표두, RM13,RY4 (최근시점기준으로 월13개, 년4개))
			</a>
		</li>
		<br/>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1EZ0002" target="_blank">
			101, DT_1EZ0002(분석 - 반기테스트)
			</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1AG126" target="_blank">
			101, DT_1AG126(분석 - 부정기테스트?????)
			</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08001" target="_blank">
			101, DT_1J08001(분석 - 기여도)
			</a>
		</li>
		<br/>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_IZGI05_021" target="_blank">101, DT_IZGI05_021(비승인 출처)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1EB001" target="_blank">101, DT_1EB001(오픈 시 팝업)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=301&tblId=DT_043Y055" target="_blank">301, DT_043Y055(5레벨)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=301&tblId=DT_027Y118" target="_blank">301, DT_027Y118(4레벨-셀수:6528)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN1004" target="_blank">101, DT_1IN1004(대용량-siga 조회 시 7분소요)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F01003" target="_blank">101, DT_1F01003(가중치)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1LB231" target="_blank">101, DT_1LB231(셀단위)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1EF211" target="_blank">101, DT_1EF211(계층별컬럼보기-기본)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1YL0000&obj_var_id=A&itm_id=3432" target="_blank">101, DT_1YL0000(obj_var_id, itm_id 넘어왔을때 충남연기군)</a>
		</li>
		<li>
			<a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1JD1012" target="_blank">101, DT_1JD1012(대용량 파일 서비스)</a>
		</li>
	</ul>
</div>

<BR><BR><B><복합통계표 및 직접다운로드></B>
<div>
	<ul>
		<li><a href="http://10.134.7.42/nsportal/multiStat/multiStatCond.jsp" target="_blank">복합통계표 (임시테스트용-개발자PC)</a></li>
		<li><a href="http://10.134.7.161/nsportal/statisticsList/statisticsList_01List.jsp?vwcd=MT_ZTITLE&parentId=A" target="_blank">직접다운로드 (임시테스트용-개발자PC)</a></li>
	</ul>
</div>

<BR><BR><B><속도측정 대상 통계표></B>
<div>
	<ul>
		<BR><BR>◎ 1그룹 :  이용로그 20,000건 이상, 항목분류시점&gt;70,000개 <BR><BR>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B26001" target="_blank">시군구/성/연령(5세)별 이동자수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001" target="_blank">총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_ENG" target="_blank">총조사인구 총괄(시도/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F160622" target="_blank">시도(시군구)/산업분류별 주요지표(10명 이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F01001" target="_blank">시도/산업별 광공업생산지수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08135" target="_blank">품목별 소비자물가지수(지출목적별)(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B04005" target="_blank">동읍면/5세별 주민등록인구(1992~2010)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08112" target="_blank">품목별 소비자물가지수(품목성질별)(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08001" target="_blank">지출목적별 소비자물가지수(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1GA0001" target="_blank">총조사가구 총괄(행정구역/거처의 종류/가구원수/사용방수별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08005" target="_blank">생활물가지수(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_05" target="_blank">총조사인구 총괄(시군구/성/5세연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B26001_A01" target="_blank">시군구별 이동자수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1K52B01" target="_blank">시도·산업·사업체구분별 사업체수, 종사자수(`06~ )</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=408&tblId=DT_PLCAHTUSE" target="_blank">지가변동률</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_31" target="_blank">경기도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B34E07" target="_blank">사망원인(236항목)/성/연령(5세)별 사망자수(1983~), 사망률(2000~)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B34E13" target="_blank">시군구/사망원인(50항목)/성/ 사망자수, 사망률, 연령표준화 사망률(2005~)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08002" target="_blank">품목성질별 소비자물가지수(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN1003" target="_blank">연령 및 성별 인구 - 읍면동</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B34E01" target="_blank">사망원인(103항목)/성/연령(5세)별 사망자수, 사망률</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B01003" target="_blank">연령별(시도) 추계인구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B28023" target="_blank">국적/체류자격별 외국인 입국자(체류기간 90일 초과)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=301&tblId=DT_013Y202" target="_blank">7.1.1 생산자물가지수(기본분류)(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=360&tblId=DT_1R11006_FRM101" target="_blank">국가별 수출액,수입액</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1YL0000" target="_blank">e-지방지표</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1G1B002" target="_blank">발주자/공종별 건설수주액(경상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_11" target="_blank">서울특별시 총조사인구 총괄(읍면동/성/연령별)</a></li>

		<BR><BR>◎ 2그룹 :  이용로그 10,000건 이상, 항목분류시점&gt;70,000개  <BR><BR>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1EW0004" target="_blank">어업별 품종별 통계</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1K51002" target="_blank">시도/산업/조직형태별 사업체수,종사자수(`93~`05)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1K52B03" target="_blank">시도 · 산업 · 종사자규모별 사업체수, 종사자수(`06~ )</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F1610" target="_blank">시도/산업분류별 출하액,생산액,부가가치 및 주요생산비(10명 이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B34E11" target="_blank">시도/사망원인(103항목)/성/연령(5세)별/ 사망자수, 사망률(2000~)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1JU0001" target="_blank">총조사 주택총괄(행정구역/주택의 종류/ 거주가구수/ 총방수별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KI2002" target="_blank">시군구 및 산업소분류별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B26003_A01" target="_blank">전출지/전입지(시도)별 이동자수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J08004" target="_blank">신선식품 소비자물가지수(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B8000K" target="_blank">읍면동, 성별/출생ㆍ사망 건수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1D07012" target="_blank">성/연령별 경제활동인구 (구직기간1주기준)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KB1001" target="_blank">시도/산업별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B80A18" target="_blank">시군구/성/연령(5세)별 사망자수, 사망률(2005~)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1L9H008" target="_blank">소득10분위별 가구당 가계수지 (전국,2인이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F30005" target="_blank">제조업 생산능력 및 가동률지수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KC2010" target="_blank">업종별 서비스업생산지수(2010년=100.0)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KA1001" target="_blank">시도/산업별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_21" target="_blank">부산광역시 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B34E09" target="_blank">사망원인(103항목)/성/시도별 사망자수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=116&tblId=DT_MLTM_5498" target="_blank">자동차등록대수현황 시도별</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B26011" target="_blank">읍면동별 이동자수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B81A01" target="_blank">시군구/성/월별 출생</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=301&tblId=DT_022Y002" target="_blank">8.1.1 국제수지</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=141&tblId=DT_14102_B001" target="_blank">기상요소별 관측값</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1BZ0503" target="_blank">가구주의연령/가구유형/가구원수별 추계가구-전국</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1L9H006" target="_blank">소득5분위별 가구당 가계수지 (전국,2인이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=387&tblId=DT_38701_N001_2" target="_blank">＊종목별 시험현황</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1D07002" target="_blank">연령별 경제활동인구 총괄(구직기간 1주 기준)</a></li>
		<br><br>◎ 3그룹 :  이용로그 5,000건 이상, 항목분류시점 70,000개 이상 <br><br>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F1607" target="_blank">시도/산업분류/종사자 규모별 주요지표(10명 이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN1004" target="_blank">성, 연령 및 교육정도별 인구(6세 이상) - 시군구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0505" target="_blank">성/연령/종교별 인구-시군구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1K52B02" target="_blank">시도 · 산업 · 조직형태별 사업체수, 종사자수(`06~ )</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KA3003" target="_blank">시도/산업/매출액규모별 현황</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=118&tblId=DT_118N_SAUP57" target="_blank">시군구별(9개도), 산업별, 규모별, 사업체수 및 종사자수(성별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1EW0001" target="_blank">어업생산동향 총괄표</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KI2003" target="_blank">읍면동 및 산업대분류별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_38" target="_blank">경상남도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=301&tblId=DT_013Y204" target="_blank">7.1.3 생산자물가지수(품목별)(2010=100)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_37" target="_blank">경상북도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_36" target="_blank">전라남도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1F01003" target="_blank">시도/재별 제조업생산지수</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_34" target="_blank">충청남도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1L9I008" target="_blank">소득10분위별 가구당 가계수지 (도시,2인이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0503" target="_blank">연령/성별 인구-읍면동</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1AG101" target="_blank">경지규모별 농가 및 경지면적</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_35" target="_blank">전라북도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_32" target="_blank">강원도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN1005" target="_blank">연령 및 성, 혼인상태별 인구(15세 이상) - 시군구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=118&tblId=DT_118N_SAUP50" target="_blank">전국, 산업별, 성별, 규모별 사업체수 및 종사자수(종사자지위별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1IN0001_33" target="_blank">충청북도 총조사인구 총괄(읍면동/성/연령별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KI1005" target="_blank">산업세세분류 및 매출액규모별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KI3017" target="_blank">[광업,제조업]산업분류 및 행정구역별 주요지표(10인 이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=354&tblId=DT_HIRA48" target="_blank">3_14 종별 의료장비 현황("11년 이전)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KI2001" target="_blank">산업세분류 및 시도별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1JU1003" target="_blank">주택의 종류, 연면적 및 거주인수별 주택-시군구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1NJ401" target="_blank">한우 사육규모별 농가 및 마리수</a></li>
		<br><br>◎ 4그룹 :  이용로그 5,000건 이상, 항목분류시점 70,000개 미만 <br><br>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=117&tblId=DT_11761_N005" target="_blank">등록장애인수-시군별,유형별,남녀별</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1L9I002" target="_blank">가구당 월평균 가계수지 (도시,2인이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1DA7002" target="_blank">연령별 경제활동인구 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B8000I" target="_blank">시군구/인구동태건수 및 동태율</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=301&tblId=DT_040Y002" target="_blank">9.2.2 소비자동향조사(한국은행,전국)(월)(2008년9월~)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1DA7004" target="_blank">행정구역(시도)별 경제활동인구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1JU1001" target="_blank">주택의 종류별 주택-읍면동</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1C51" target="_blank">경제활동별 지역내총생산</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1KI1001" target="_blank">산업세세분류별 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B040A3" target="_blank">시군구별 주민등록인구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B040A4" target="_blank">시군구별 외국인등록인구</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1L9H002" target="_blank">가구당 월평균 가계수지 (전국,2인이상)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1DA7001" target="_blank">성별 경제활동인구 총괄</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1C8007" target="_blank">경기종합지수(2010=100) (8차)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B81A17" target="_blank">시군구/합계출산율, 모의 연령별 출산율</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J51" target="_blank">농가구입가격지수(2005=100, 분기)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B42" target="_blank">완전생명표(각세별)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B8000G" target="_blank">월.연간 인구동향(출생,사망,혼인,이혼통계)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1DA7102" target="_blank">성/연령별 실업률</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1C8008" target="_blank">경기종합지수(2010=100) 구성지표 시계열 (8차)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_2KAAG01" target="_blank">OECD 국가의 주요지표</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1J88042" target="_blank">월별 소비자물가 등락률</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B040B3" target="_blank">시군구별 주민등록세대</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B35001" target="_blank">가정별 성비,인구성장률,인구구조,부양비,노령화지수,중위연령,평균연령(전국)</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=114&tblId=DT_114N_1A004" target="_blank">농림업 생산금액</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1DA7C06" target="_blank">산업별 취업자</a></li>
		<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1B81A19" target="_blank">시도/출산순위별 출생성비</a></li>
<li><a href="<%=request.getContextPath() %>/statHtml.do?orgId=101&tblId=DT_1L6E001" target="_blank">소득분배지표</a></li>
	</ul>
</div>
</body>
</html>