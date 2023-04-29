package nurimsoft.webapp.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import nurimsoft.stat.info.AssayInfo;
import nurimsoft.stat.info.ClassInfo;
import nurimsoft.stat.info.ItemInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.info.RelationInfo;
import nurimsoft.stat.info.SelectAllInfo;
import nurimsoft.stat.info.SelectRangeInfo;
import nurimsoft.stat.info.StatInfo;
import nurimsoft.stat.manager.StatExceptionManager;
import nurimsoft.stat.util.MessageManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlService;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import egovframework.rte.psl.dataaccess.util.EgovMap;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *	수정일		수정자		수정내용
 *	----------	--------	---------------------------
 *	2016.01.04	남규옥		addParamInfo 메소드 호출 시 예외처리함
 *	
 * </pre>
 */
@Controller
public class StatHtmlController {

	protected Log log = LogFactory.getLog(this.getClass());

	//Message
	@Resource(name = "messageSource")
	protected DelegatingMessageSource messageSource;

	@Resource(name = "statHtmlService")
	protected StatHtmlService statHtmlService;

	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "/statHtml")
	public String getStatInfo (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			@ModelAttribute("ItemInfo") ItemInfo itemInfo,
			@ModelAttribute("PeriodInfo") PeriodInfo periodInfo,
			@ModelAttribute("ClassInfo") ClassInfo classInfo,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) throws Exception{

		/* TODO test 반영시 주석*/
		/*
		HttpSession session = request.getSession();
		session.setAttribute("empId", "micari");
		*/
		String isChangedDataOpt = paramInfo.getIsChangedDataOpt();
		String new_win = paramInfo.getNew_win(); // 2014.05.02 새창열기시 Y로 셋팅되서 넘어옴. 새창열기시 기존셋팅값 고대로 가져와서 보여주기 위해... - 김경호

		String msg="";

		if( (isChangedDataOpt != null && isChangedDataOpt.equals("Y")) || (new_win != null && new_win.equals("Y")) ){
			try{
				addParamInfo(request, paramInfo);
				//조회조건 테이블에 추가
				addSelectAllInfo(request, paramInfo);
			}catch(StatExceptionManager se){
				return "error";
			}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
				return "error";
			}
		}else{
			try{
				//statHtml.do 일 경우에만 사용(최초의 경우에만 - 언어변경시에는 적용하지 않음)
				addInitParamInfo(request, paramInfo);
				addParamInfo(request, paramInfo);
			}catch(StatExceptionManager se){
				return "error";
			}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
				return "error";
			}

			//사용자 정의 Exception 체크//
			String InfoYN = null;
			try{
				InfoYN = statHtmlService.getStatHtmlDefaultCondition(paramInfo);
			}catch(Exception e){
				return "error";
			}
			//dataOpt 기본은 ko
			if(InfoYN.equals("N")){

				//통계표가 비공개인 경우 업무용,호스팅용 메시지
				if(paramInfo.getServerTypeOrigin().equals("stat")){
					msg = MessageManager.getInstance().getProperty("103", paramInfo.getDataOpt());
				}else{
					msg = MessageManager.getInstance().getProperty("101", paramInfo.getDataOpt());
				}
				model.addAttribute("resultMsg",msg);
				return "error";
			}

			//2015.06.12 URL에 < > 있을경우 오류 페이지로 가도록
			Enumeration i = request.getParameterNames();

			while( i.hasMoreElements()){
				String x = (String)i.nextElement();

				String reText = (String)request.getParameter(x);

				if( reText.indexOf("<") > -1 ||reText.indexOf(">") > -1){
					msg = MessageManager.getInstance().getProperty("104", paramInfo.getDataOpt());
					model.addAttribute("resultMsg",msg);
					return "error";
				}
			}
		}

		try{
			StatInfo statInfo = statHtmlService.getStatInfo(paramInfo, model);
			model.addAttribute("statInfo", statInfo);
			return "NSO";
		}catch(Exception e){
			//log.error(e.getMessage()); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
			msg = MessageManager.getInstance().getProperty("101", paramInfo.getDataOpt());
			model.addAttribute("resultMsg",msg);
			return "error";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/html")
	public ModelAndView getHtml (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request, HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();
		
		/*2020.06.09 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		String referer = request.getHeader("referer");

		if( referer == null || referer.indexOf("statHtml.do") < 0){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}
		
		String errMsg = null;
		int errCode = 0;

		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			//log.error(se.getMessage()); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
			log.info(se.getMessage());
			errCode = 1;
			//2013.11.26
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			model.addObject("errCode", errCode);
			model.addObject("errMsg", errMsg);

			model.setViewName("jsonView");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			errMsg = MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt());
			model.addObject("errCode", errCode);
			model.addObject("errMsg", errMsg);

			model.setViewName("jsonView");
			return model;
		}

		addSelectAllInfo(request, paramInfo);

		Object[] obj = null;
		List list = null;
		
		try{
			//2013.11.29 주석여부를 가져오기 위해 기존의 list 순번을 바꾸지 않으려고 함.
			//list로 반환했던것을 object array로 받도록 함.
			obj = statHtmlService.getHtmlData(paramInfo, request);
			list = (List)obj[0];
		}catch(StatExceptionManager se){
			//log.error(se.getMessage()); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
			errCode = 1;
			//2013.11.26
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			model.addObject("errCode", errCode);
			model.addObject("errMsg", errMsg);

			model.setViewName("jsonView");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			errMsg = MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt());
			model.addObject("errCode", errCode);
			model.addObject("errMsg", errMsg);

			model.setViewName("jsonView");
			return model;
		}

		//이규정 추가 uselog 적재
		//statHtmlService.setLog(paramInfo);
		//list.add(paramInfo.getLogSeq());
		/*2018.07.10 
			모니터링(connPath = Z6)에서 
			서비스용(ServerLocation = NSO :통계청에 설치된, ServerTypeOrigin = service :서비스용)을 조회할경우 
			TN_USELOG에 적재하지 않는다 - 최윤정 주무관
		*/
		String connPath = paramInfo.getConnPath();

		if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1 && connPath.equals("Z6")){
			list.add(""); // list의 갯수를 유지 시키기 위해 빈값을 넣어줌.
		}else{
			statHtmlService.setLog(paramInfo);
			list.add(paramInfo.getLogSeq());
		}
		
		//2013.11.26 추가 : 분석명
		list.add(paramInfo.getAnalTextTblNm());

		//2013.11.29 주석여부 추가
		list.add((String)obj[1]);
		
		//이규정 추가 olaplog 적재
		//통계청 서비스용이면서 viewKind가 1, 2, 5(조회, 다운로드, 분석)의 경우 적재
		//2013.11.26
		if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){

			// 2018.07.10 모니터링(connPath = Z6)에서는 TN_USELOG 와 마찬가지로 TN_OLAPLOG 도 적재하지 않는다. - 최윤정
			// connPath 가 Z6(모니터링) 이 아닐때만 TN_OLAPLOG 적재
			if( !connPath.equals("Z6")){	
				String viewKind = paramInfo.getViewKind();
				if(viewKind == null){
					viewKind = "";
				}
				if( viewKind.equals("1") || viewKind.equals("2") || viewKind.equals("5") ){
					statHtmlService.setOlapLog(paramInfo);
				}
			}
		}

		//부모 아이콘 세팅
		List parentIconArr = statHtmlService.getParentIconArr(paramInfo);

		model.addObject("parentResult",parentIconArr);
		model.addObject("result", list);
		model.setViewName("jsonView");
		return model;
	}

	// 안영수 통계표 조회와 선택정보 전체보기를 위한 로직 분리
	public void addSelectAllInfo(HttpServletRequest request, ParamInfo  paramInfo) throws Exception{
		statHtmlService.deleteRequirement(paramInfo);

		JSONArray jFieldArr = JSONArray.fromObject(StringEscapeUtils.unescapeHtml(paramInfo.getFieldList()));

		this.statHtmlService.searchRequirementInsert(jFieldArr, paramInfo);
	}

	public void addParamInfo(HttpServletRequest request, ParamInfo  paramInfo) throws Exception{
		//세션id, 서버type 등의 정보 추가
		HttpSession session = request.getSession();
		paramInfo.setSessionId(session.getId());

		StringUtil sutil = new StringUtil();
		
		String orgid = sutil.stringCheck(paramInfo.getOrgId());
		String tblid = sutil.stringCheck(paramInfo.getTblId());
		
		/*2020.07.09 필수 파라미터를 빼고 접속하는 URL 에 대해 차단 - 코드는 일단 보내는데 받아서 쓰는건 놔두자...굳이...*/
		if(orgid.equals("") || tblid.equals("")){
			throw new StatExceptionManager("101");
		}
		
		String serverType = PropertyManager.getInstance().getProperty("server.type");
		if(serverType == null || serverType.trim().length() == 0 ){
			serverType = "service";
		}

		String serverTypeOrigin = serverType;

		String serverLocation = PropertyManager.getInstance().getProperty("server.location");
		if(serverLocation == null || serverLocation.trim().length() == 0 ){
			serverLocation = "NSO";
		}

		paramInfo.setServerType(serverType);
		paramInfo.setServerTypeOrigin(serverType);
		paramInfo.setServerLocation(serverLocation);

		String etldbLink = PropertyManager.getInstance().getProperty("server.dblink.etldb");
		if(etldbLink == null || etldbLink.trim().length() == 0 ){
			etldbLink = "@ETLDB";
		}
		paramInfo.setEtldbLink(etldbLink);

		//이규정 추가
		// 2020.08.13 was 정보 노출!! 보안취약점 제거
		//paramInfo.setRealPath(request.getSession().getServletContext().getRealPath(""));

		String dbUser = PropertyManager.getInstance().getProperty("server.dbuser");

		//2014.04.29 호스팅 DB이관문제 때문에 TN_STAT_HTML_COND_WEB 관련 추가 - 김경호
		String condTable = PropertyManager.getInstance().getProperty("server.dbuser")+"."+PropertyManager.getInstance().getProperty("table.condition");

		String buff = paramInfo.getDbUser();

		if(buff == null || buff.length() == 0){
			paramInfo.setDbUser(dbUser + ".");
		}else{
			if(buff.length() >= 3 && (!buff.substring(0, 3).equals("NSI") && !buff.substring(0, 3).equals("nsi"))){ //2020.07.16 dbUser 파라미터에 엉뚱한거 넣는넘들 있음
				paramInfo.setDbUser(dbUser + ".");
			}else{
				paramInfo.setDbUser(paramInfo.getDbUser().replaceAll("\\.", "") + ".");
			}
		}

		// TN_STAT_HTML_COND_WEB 테이블 위치 추가
		paramInfo.setCondTable(condTable);

		//사용자 IP추가
		paramInfo.setIpAddr(request.getRemoteAddr());

		//언어정보 셋팅
		String language = paramInfo.getLanguage();

		//담당자 레벨 조회 세팅
		String chargerLvl = request.getParameter("pub");

		if(language == null || !language.equals("en")){
			paramInfo.setLanguage("ko");
		}else{
			paramInfo.setLanguage("en");
		}

		String dataOpt = paramInfo.getDataOpt();
		if(dataOpt == null || dataOpt.trim().length() == 0){
			paramInfo.setDataOpt(paramInfo.getLanguage());
		}

		if(serverLocation.equals("NSO")){
			//Service용
			if(serverTypeOrigin.equals("service")){

				//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ ::: " + paramInfo.getLanguage());

				if(paramInfo.getLanguage().equals("en")){
					paramInfo.setServerType("service_en");
					paramInfo.setServerTypeOrigin("service_en");
				}

				//session에 empId, empNm을 가져와서 paramInfo에 추가한다.
				String sessionEmpId = (String)session.getAttribute("empId");
				String sessionEmpNm = (String)session.getAttribute("empNm");

				if(sessionEmpId != null && sessionEmpId.trim().length() > 0){
					paramInfo.setEmpId(sessionEmpId);
				}

				if(sessionEmpNm != null && sessionEmpNm.trim().length() > 0){
					paramInfo.setEmpNm(sessionEmpNm);
				}
			}
			//업무용(호스팅용) : 담당자 권한 체크
			else if(serverTypeOrigin.equals("stat")){
				try{
					settingStatSession(paramInfo, session, chargerLvl);
				}catch(Exception e){
					throw new StatExceptionManager("fail.common.msg");
				}

				//업무용
				String dbUserExcptDot = StringUtils.defaultString(dbUser.replaceAll("\\.", ""));
				if(dbUserExcptDot.equals("NSI_IN_101")){
					paramInfo.setViewType("B");

				}else{
					paramInfo.setViewType("H");		//호스팅용
				}
			}
		}
		//보급
		else{
			if(serverTypeOrigin.equals("stat")){
				settingStatSession(paramInfo, session, chargerLvl);
			}
			paramInfo.setViewType("S");
		}

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			session.setAttribute("dataOpt", "en");
		}else{
			session.setAttribute("dataOpt", "ko");
		}
		
		// 20151224 남규옥 추가 시작 ::: 호스팅일 경우 exception 처리
		if(("H").equals(paramInfo.getViewType())){	
			String dbUserExcptDot = StringUtils.defaultString(paramInfo.getDbUser().replaceAll("\\.", ""));
			if(dbUserExcptDot.equals(dbUser)){
				//System.out.println("호스팅:: 적절하지않은 dbUser!! 에러발생 [dbUser] [" + dbUser+ "]---[dbUserExcptDot] ["+dbUserExcptDot+"]");
				throw new StatExceptionManager("fail.common.msg");
			}
		}
		// 20151224 남규옥 추가 끝
		
		// 2015.06.25 관련통계표 사용여부가 Y이고 등록된 관련통계표가 존재할 때만 관련통계표 버튼 보임
		if(("ko").equals(paramInfo.getLanguage())){
			String relUserYn = PropertyManager.getInstance().getProperty("rel.user.yn");
			paramInfo.setRelUserYn(relUserYn);
			
			int relCnt = statHtmlService.getRelCount(paramInfo);
			if (relCnt != 0){
				paramInfo.setRelYn("Y");
			}
		}
		// 2015.06.25 관련통계표
	}

	public void settingStatSession(ParamInfo paramInfo, HttpSession session, String chargerLvl) throws Exception{
		//String empId = paramInfo.getEmpId();	//세션 적용을 위해 주석처리(보급이 걸리네)
		String sessionEmpId = (String)session.getAttribute("empId");
		if(sessionEmpId == null){
			sessionEmpId = (String)session.getAttribute("EMP_ID");
		}

		/*
		Enumeration eee = session.getAttributeNames();

		while(eee.hasMoreElements()){
			String temp = (String)eee.nextElement();
			System.out.println( "session attribute's name ::: " + temp + " >>>>> " + session.getAttribute( temp ) );
		}
		*/

		//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		//System.out.println(sessionEmpId + "," + session.getId());
		//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		String empId = sessionEmpId;
		paramInfo.setEmpId(empId);
		/*
		if(sessionEmpId == null){
			sessionEmpId = "";
		}
		 */
		//파라미터로 넘어오지 않으면 세션에서 가져온다.
		//파라미터로 넘어온 경우 세션과 다르면 파라미터를 적용한다.
		/* 세션적용을 위해 주석처리(보급이 걸리네)
		if(empId == null || empId.trim().length() == 0){
			if(sessionEmpId != null){
				empId = sessionEmpId;
				paramInfo.setEmpId(empId);
			}

		}
		*/

		/*담당자권한으로 통계표를 테스트할 경우 
		 * empId 를 아무 내용이나 넣어주고 아래 chkCnt 를 0 이상으로 넣어주면 됌.
		 * */
		String empNm = null;
		//empId가 있다면 권한 체크
		if(empId != null && empId.trim().length() > 0){
			int chkCnt = statHtmlService.checkEmpAuth(paramInfo);
			if(chkCnt > 0){
				if(chargerLvl != null && chargerLvl.equals("1")){
					paramInfo.setServerType("stat_emp");
					paramInfo.setServerTypeOrigin("stat_emp");
					paramInfo.setChargerLvl(chargerLvl);						//담당자레벨 paramInfo 세팅
				}
			}

			//Service에서 서비스용 업무용 구분해서 가져옴
			paramInfo.setEmpNm(statHtmlService.getEmpNm(paramInfo));
			session.setAttribute("empId", empId);
			session.setAttribute("empNm", empNm);
		}

		if(StringUtils.defaultString(paramInfo.getSt()).equals("service")){
			paramInfo.setServerType("service");
			paramInfo.setChargerLvl(null);		//담당자레벨 해제
		}
	}

	public void addInitParamInfo(HttpServletRequest request, ParamInfo  paramInfo) throws Exception{

		String serverType = PropertyManager.getInstance().getProperty("server.type");
		if(serverType == null || serverType.trim().length() == 0 ){
			serverType = "service";
		}

		String serverLocation = PropertyManager.getInstance().getProperty("server.location");
		if(serverLocation == null || serverLocation.trim().length() == 0 ){
			serverLocation = "NSO";
		}

		HttpSession session = request.getSession();

		String empId = null;
		String empNm = null;

		//서비스용인경우
		if(serverLocation.equals("NSO") && serverType.equals("service")){
			if(session.getAttribute("USRID") != null){
				empId = (String)session.getAttribute("USRID");

				//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				//System.out.println(empId + "," + session.getId());
				//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

				paramInfo.setEmpId(empId);

			}

			//TODO localtest - commit 또는 서버 반영시 반드시 주석처리
			/*
			if(paramInfo.getEmpId() != null){
				session.setAttribute("empId", paramInfo.getEmpId());
			}
			*/

			if(empId != null && empId.trim().length() > 0){
				session.setAttribute("empId", empId);
				//Service에서 서비스용 업무용 구분해서 가져옴
				empNm = statHtmlService.getEmpNm(paramInfo);
				paramInfo.setEmpNm(empNm);
				session.setAttribute("empNm", empNm);
			}
		}

		String connPath = request.getParameter("conn_path");
		String referer = request.getHeader("referer");
		//conn_path가 E2로 들어올경우 Z9로 변경

		if(connPath != null && connPath.equals("E2")){
			connPath = "Z9";
		}

		paramInfo.setConnPath(connPath);
		paramInfo.setVwCd(request.getParameter("vw_cd"));
		paramInfo.setListId(request.getParameter("list_id"));

		paramInfo.setContextPath(request.getContextPath());

		//MessageManager 등록 - 최초 실행 시 한번 등록
		MessageManager.getInstance().load(request.getSession().getServletContext().getRealPath(""));

	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/periodDivSelect")
	public ModelAndView periodSelect(HttpServletRequest request,
									 HttpServletResponse response,
									@ModelAttribute("ParamInfo") ParamInfo paramInfo) throws Exception{
		ModelAndView model = new ModelAndView();

		Map paramMap = new HashMap();
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}

		paramMap.put("orgId",paramInfo.getOrgId());
		paramMap.put("tblId",paramInfo.getTblId());
		paramMap.put("serverType",paramInfo.getServerType());
		paramMap.put("dbUser",paramInfo.getDbUser());

		paramMap.put("startPrd",request.getParameter("startPrd"));
		paramMap.put("endPrd",request.getParameter("endPrd"));
		paramMap.put("prdSe", request.getParameter("prdSe"));

		//2015.06.05 상속통계표
		if( "Y".equals(paramInfo.getInheritYn()) ){
			paramMap.put("inheritYn",	paramInfo.getInheritYn());
			paramMap.put("originOrgId",	paramInfo.getOriginOrgId());
			paramMap.put("originTblId",	paramInfo.getOriginTblId());
		}

		/* 
		 20.04.09 업무용에서 담당자가 st 파라미터를 이용하여 서비스용으로 조회할때 원래 serverType은 관리자 였으므로 기간보안 체크안함
		(손상호 주무관의 요청에 따라 [미리보는KOSIS]에서는 기간보안 체크안함.
		 */
		paramMap.put("serverTypeOrigin",paramInfo.getServerTypeOrigin());
		
		List<EgovMap> divPeriodList = (List<EgovMap>) statHtmlService.getPeriodList(paramMap);
		model.addObject("result", divPeriodList);
		model.setViewName("jsonView");

		return model;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/classDivSelect")
	public ModelAndView classSelect(HttpServletRequest request,
									 HttpServletResponse response,
									 @ModelAttribute("ParamInfo") ParamInfo paramInfo) throws Exception{

			ModelAndView model = new ModelAndView();
			Map paramMap = new HashMap();
			
			try{
				addParamInfo(request, paramInfo);
			}catch(StatExceptionManager se){
				model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
				model.setViewName("alert");
				return model; 
			}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
				model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
				model.setViewName("alert");
				return model;
			}
			
			paramMap.put("orgId",paramInfo.getOrgId());
			paramMap.put("tblId",paramInfo.getTblId());
			paramMap.put("serverType",paramInfo.getServerType());
			paramMap.put("dbUser",paramInfo.getDbUser());
			paramMap.put("itmId",request.getParameter("itmId"));
			paramMap.put("objVarId", request.getParameter("objVarId"));
			paramMap.put("lvl",request.getParameter("lvl"));
			paramMap.put("searchCondition", request.getParameter("searchCondition"));
			paramMap.put("dtatOpt",paramInfo.getDataOpt());

			//e나라 지표 지역별 통계표//
			if(paramMap.get("objVarId").equals(StringUtils.defaultString(paramInfo.getObj_var_id(), "")) ){
				paramMap.put("itmid",paramInfo.getItm_id());
			}else{
				paramMap.put("itmid","");
			}

			model = statHtmlService.getClassSearchType(paramMap,model);

			model.setViewName("jsonView");

			return model;
	}

	/*
	 * 이규정 번호클릭 시 주석가져오기
	 */
	@RequestMapping(value = "/cmmtInfo")
	public ModelAndView getCmmtStr (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request, HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();
		String errMsg = null;
		int errCode = 0;

		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model; 
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}

		Map paramMap = new HashMap();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("dataOpt", paramInfo.getDataOpt());
		paramMap.put("cmmtSe", request.getParameter("cmmtSe"));
		paramMap.put("objVarId", request.getParameter("objVarId"));
		paramMap.put("itmId", request.getParameter("itmId"));
		paramMap.put("itmRcgnSn", request.getParameter("itmRcgnSn"));
		paramMap.put("dbUser", paramInfo.getDbUser());

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			paramMap.put("lngSe", "1211911");
		}else{
			paramMap.put("lngSe", "1211910");
		}

		//2015.06.05 상속통계표 변수 (통계표 주석말고 분류, 항목 등 주석은 원통계표)
		if(!"1210610".equals((String)request.getParameter("cmmtSe")) && "Y".equals((String)request.getParameter("inheritYn"))){
			paramMap.put("inheritYn", 	request.getParameter("inheritYn"));
			paramMap.put("originOrgId",	request.getParameter("originOrgId"));
			paramMap.put("originTblId",	request.getParameter("originTblId"));
		}

		List list = statHtmlService.getCmmtStr(paramMap);
		int line = list.size();
		String result = StatPivotUtil.makeStrCmmt(list);

		String title = statHtmlService.getCmmtTitle(paramMap);

		model.addObject("result", result);
		model.addObject("line", line);
		model.addObject("title", title);
		model.setViewName("jsonView");

		return model;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/setOlapLogForNsoService")
	public void setOlapLogForNsoService(HttpServletRequest request,
									 HttpServletResponse response,
									@ModelAttribute("ParamInfo") ParamInfo paramInfo) throws Exception{
		try{
			addParamInfo(request, paramInfo);
			statHtmlService.setLog(paramInfo);
		}catch(StatExceptionManager se){
			String errMsg = null;
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			log.info(errMsg);
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			String errMsg = null;
			errMsg = MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt());
			log.info(errMsg);
		}		
	}

	@RequestMapping(value = "/downGrid")
	public ModelAndView downGrid(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();

		/*2020.06.09 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		String referer = request.getHeader("referer");

		if( referer == null || referer.indexOf("statHtml.do") < 0){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}
		
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}

		//2015.11.13 통계부호 포함 다운로드 여부 확인을 위한 파라미터
		String smblTypeYn = request.getParameter("smblYn");

		if(smblTypeYn == null){
			smblTypeYn = "N";
		}

		paramInfo.setSmblYn(smblTypeYn);

		/* 2020.06.09 위 작업으로 인해 바로 차단 - 제니퍼 에러  발생안되도록하고 DB적재는 이제 고만...IP알아서 뭐할라고?
		if(referer == null || referer.indexOf("statHtml.do") < 0){
			//통계청 서비스용 인경우에만 로그를 적재
			if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){
				statHtmlService.setInterCeptLog(paramInfo, referer);
			}
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}
		 */
		
		addSelectAllInfo(request, paramInfo);

		File file = null;

/*		String fileName = paramInfo.getOrgId()+"_"+paramInfo.getTblId();
		paramInfo.setTblNm(fileName);*/

		//System.out.println("view ::: " + paramInfo.getView());
		try{
			if(paramInfo.getView().equals("xls") || paramInfo.getView().equals("xlsx")){ // 2016-07-05 xls 다운추가 우찬균 주무관 요청
				//excel
				file = statHtmlService.getExcelData(paramInfo, request);
			}else if(paramInfo.getView().equals("sdmx")){
				//sdmx
				file = statHtmlService.getSdmxData(paramInfo, request);
			}else{
				//csv, txt
				file = statHtmlService.getCsvTxtData(paramInfo, request);
			}
		}catch(StatExceptionManager se){  //2020.07.21 제니퍼에 아예 안나오도록...
			log.info("CODE : "+ se.getCode());
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;			
		}catch(Exception e){ //2020.07.21 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}
			
		statHtmlService.setLog(paramInfo);
		//통계청 서비스용이면서 viewKind가 1, 2, 5(조회, 다운로드, 분석)의 경우 적재
		//2013.11.26
		if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){

			String viewKind = paramInfo.getViewKind();
			if(viewKind == null){
				viewKind = "";
			}

			if( viewKind.equals("1") || viewKind.equals("2") || viewKind.equals("5") ){
				statHtmlService.setOlapLog(paramInfo);
			}
		}

		model.addObject("file", file.getName());
		model.setViewName("jsonView");

		return model;
	}

	@RequestMapping(value = "/downHelp")
	public ModelAndView downHelp(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();
		String errMsg = null;

		/*2020.07.14 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		String referer = request.getHeader("referer");

		if( referer == null || referer.indexOf("statHtml.do") < 0){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}		
		
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			log.info(errMsg);
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}

		File file = null;

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir = "help";
		String fileName = "manual.pdf";

		File dir = new File(realPath + File.separator + fileDir);
		if(! dir.exists()){
			dir.mkdir();
		}

		file = new File(realPath + File.separator + fileDir, fileName);

		model.addObject("file", file);
		model.setViewName("downloadView");

		return model;
	}

	@RequestMapping(value = "/downMeta")
	public ModelAndView downMeta(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();

		/*2020.06.09 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		String referer = request.getHeader("referer");

		if( referer == null || referer.indexOf("statHtml.do") < 0){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}
		
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.addObject("backUrl", referer);
			model.setViewName("alert");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.addObject("backUrl", referer);
			model.setViewName("alert");
			return model;
		}
		
		/* 2020.06.09 위 작업으로 인해 바로 차단 - 제니퍼 에러  발생안되도록하고 DB적재는 이제 고만...IP알아서 뭐할라고?
		if(referer == null || referer.indexOf("statHtml.do") < 0){
			//통계청 서비스용 인경우에만 로그를 적재
			if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){
				statHtmlService.setInterCeptLog(paramInfo, referer);
			}
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("error");
			return model;
		}
		*/

		addSelectAllInfo(request, paramInfo);

		File file = statHtmlService.getMetaData(paramInfo, request);

		if(file == null){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("401", paramInfo.getDataOpt()));
			model.addObject("backUrl", referer);
			model.setViewName("alert");
			return model;
		}

		statHtmlService.setLog(paramInfo);
		//통계청 서비스용이면서 viewKind가 1, 2, 5(조회, 다운로드, 분석)의 경우 적재
		//2013.11.26
		if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){

			String viewKind = paramInfo.getViewKind();
			if(viewKind == null){
				viewKind = "";
			}

			if( viewKind.equals("1") || viewKind.equals("2") || viewKind.equals("5") ){
				statHtmlService.setOlapLog(paramInfo);
			}
		}

		model.addObject("file", file);
		model.setViewName("downloadView");

		return model;
	}

	// 직접다운로드 메타 다운로드
	@RequestMapping(value = "/downDirectMeta")
	public ModelAndView downDirectMeta(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();
		String referer = request.getHeader("referer");
		String direct = paramInfo.getDirect();

		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.addObject("backUrl", referer);
			model.setViewName("alert");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.addObject("backUrl", referer);
			model.setViewName("alert");
			return model;
		}

		if( StringUtils.defaultString(direct).equals("direct") && paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1 ){
			String ipAddr = request.getRemoteAddr();
			String orgId = paramInfo.getOrgId();
			String tblId = paramInfo.getTblId();
			String usrId = StringUtils.defaultIfEmpty(paramInfo.getEmpId(),"");
			String usrNm = StringUtils.defaultIfEmpty(paramInfo.getEmpNm(),"");
			String connpath = StringUtils.defaultIfEmpty(statHtmlService.getConnPath(request.getParameter("VWCD")),"Z1");
			String viewPeriod = "";
			String viewSubKind = "2_8_META";


			if (orgId.equals("") || tblId.equals("")) {
				model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
				model.addObject("backUrl", referer);
				model.setViewName("alert");
				return model;
			}

			Map uselog_map = new HashMap();
			uselog_map.put("ipAddr", ipAddr);
			uselog_map.put("orgId", orgId);
			uselog_map.put("tblId", tblId);
			uselog_map.put("usrId", usrId);
			uselog_map.put("usrNm", usrNm);
			uselog_map.put("connPath", connpath);
			uselog_map.put("viewPeriod", viewPeriod);
			uselog_map.put("viewSubKind", viewSubKind);
			uselog_map.put("statKind", "");
			uselog_map.put("dbUser", paramInfo.getDbUser());
			uselog_map.put("serverType", paramInfo.getServerType());
			uselog_map.put("useSeq", PropertyManager.getInstance().getProperty("table.useseq"));
			uselog_map.put("useLog", PropertyManager.getInstance().getProperty("table.uselog"));

			statHtmlService.insertUseLogForDirect(uselog_map);
		}

		File file = statHtmlService.getDirectMeta(paramInfo, request);

		if(file == null){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("401", paramInfo.getDataOpt()));
			model.addObject("backUrl", referer);
			model.setViewName("alert");
			return model;
		}

		model.addObject("file", file);
		model.setViewName("downloadView");

		return model;
	}

	//김정현
	@RequestMapping(value = "/makeLarge")
	public ModelAndView makeLarge(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();

		/*2014.07.01 - 직접접근으로 추정되는 접속증가로 인한 CPU 과부하로 인해 직접접근 차단 - 김경호 */
		String referer = request.getHeader("referer");

		String msg	= "KOSIS는 시스템의 안정적 운영을 위하여\\n통계자료 URL을 직접 생성하여 호출하는 방식은\\n제공하지 않습니다.\\n정상적인 경로로 이용하여 주시기 바랍니다.\\n궁금하신 점은 아래 메일주소로 연락주시기 바랍니다.\\n(email: kosis01@korea.kr)"
					+ "\\n\\nWe prohibit the use of direct URL method for the\\nstable operation of the KOSIS.\\nPlease use statistical data through the normal route.";

		if( referer == null || (referer.indexOf("statHtml.do") < 0 && referer.indexOf("directDownDiv.do") < 0) ){
			model.addObject("resultMsg",msg);
			model.setViewName("error");
			return model;
		}
		/*--------------------------------------------------------------------*/

		String errMsg = null;
		int errCode = 0;

		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			errCode = 1;
			model.setViewName("jsonView");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			errMsg = MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt());
			errCode = 1;
			model.addObject("errCode", errCode);
			model.addObject("errMsg", errMsg);

			model.setViewName("jsonView");
			return model;
		}	

		String direct = paramInfo.getDirect();

		//직접다운로드
		String serverUrl = PropertyManager.getInstance().getProperty("server.url");
		if(StringUtils.defaultString(direct).equals("direct")) {
			if( paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1 ) {
				String vwCd = StringUtils.defaultIfEmpty(request.getParameter("VWCD"), "");

				String[] tempArr = paramInfo.getPrdDe().split(",");	// 시점목록을 배열형태로 저장
				int arrLen = tempArr.length;
				String startPrdDe = tempArr[arrLen - 1];
				String endPrdDe = tempArr[0];
				String prdSe = paramInfo.getPrdSe();

				//ekp.tn_uselog 데이터 쌓기
				HttpSession session = request.getSession();

				String viewSubKind = null;
				String statKind = null;
				String ipAddr = request.getRemoteAddr();
				String conn_path = StringUtils.defaultIfEmpty(statHtmlService.getConnPath(vwCd),"Z1");

				// 통계표구성 시점표두, 항목표두
				if(paramInfo.getDownLargeExprType().equals("1")){
					statKind = "3";
				}else{
					statKind = "2";
				}

				// 파일형태 excel, csv
				if(paramInfo.getDownLargeFileType().equals("excel")){
					viewSubKind = "2_8_1";
				}else if(paramInfo.getDownLargeFileType().equals("csv")){	
					viewSubKind = "2_8_2";
				}else{	//2017.11.29 직접다운로드로 TXT 추가 - 김기만 사무관
					viewSubKind = "2_4";
				}
				// 시점
				String viewPeriod = prdSe + startPrdDe + "_" + prdSe + endPrdDe;

				Map tmpMap = new HashMap();

				tmpMap.put("ipAddr", ipAddr);
				tmpMap.put("orgId", paramInfo.getOrgId());
				tmpMap.put("tblId", paramInfo.getTblId());
				tmpMap.put("usrId", paramInfo.getEmpId());
				tmpMap.put("usrNm", paramInfo.getEmpNm());
				tmpMap.put("connPath", conn_path);
				tmpMap.put("viewPeriod", viewPeriod);
				tmpMap.put("viewSubKind", viewSubKind);
				tmpMap.put("statKind", statKind);
				tmpMap.put("dbUser", paramInfo.getDbUser());
				tmpMap.put("serverType", paramInfo.getServerType());
				tmpMap.put("useSeq", PropertyManager.getInstance().getProperty("table.useseq"));
				tmpMap.put("useLog", PropertyManager.getInstance().getProperty("table.uselog"));

				statHtmlService.insertUseLogForDirect(tmpMap);
			}
		}else{ //10,000 셀 초과 다운로드

			if(request.getParameter("from") != null && request.getParameter("from").equals("portal")){
				paramInfo.setLanguage("ko");
				paramInfo.setDataOpt("ko");
				// 테스트용
//					paramInfo.setSessionId("1736BE192E0B286FDF96990C32CE95D2");
			}

			if(request.getParameter("from") == null || !request.getParameter("from").equals("portal")){
				addSelectAllInfo(request, paramInfo);
				statHtmlService.setLog(paramInfo);
			}


			//통계청 서비스용이면서 viewKind가 1, 2, 5(조회, 다운로드, 분석)의 경우 적재
			//2013.11.26
			if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){

				String viewKind = paramInfo.getViewKind();
				if(viewKind == null){
					viewKind = "";
				}

				if( viewKind.equals("1") || viewKind.equals("2") || viewKind.equals("5") ){
					statHtmlService.setOlapLog(paramInfo);
				}
			}

		}

		String fileName = makeLargeFile(paramInfo, request, response);

		model.addObject("file", fileName);
		model.setViewName("jsonView");

		return model;
	}

	//김정현
	public String makeLargeFile(ParamInfo paramInfo, HttpServletRequest request, HttpServletResponse response) throws Exception{

		// 파일 타입 excel, csv, txt
		String type = paramInfo.getDownLargeFileType();
		String orgId = paramInfo.getOrgId();
		String tblId = paramInfo.getTblId();
		String prdSe = paramInfo.getPrdSe();
		String direct = StringUtils.defaultString(paramInfo.getDirect());

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		
		String fileDir = "tmpFile";
		String fileName = orgId + "_" + tblId;

		String dateString = StatPivotUtil.getDateString();
		String ext = "";
		
		if( type.equals("excel")){
			ext = "xls";
		}else if( type.equals("csv")){
			ext = "csv";
		}else{	//2017.11.29 직접다운로드로 TXT 추가 - 김기만 사무관
			ext = "txt";
		}
		
		if(direct.equals("direct")){
			fileName += "_" + prdSe;
		}

		/*
		String mime = "application/octet-stream";
		response.setContentType(mime);

		if(type.equals("excel")){
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(orgId+"_"+tblId+"_"+ dateString +".xls", "UTF-8") + ";");
		}else{
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(orgId+"_"+tblId+"_"+ dateString +".csv", "UTF-8") + ";");
		}
		response.setHeader("Charset", "UTF-8" );

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "EUC-KR"));
		*/

		File dir = new File(realPath + File.separator + fileDir);
		if(! dir.exists()){
			dir.mkdir();
		}

		fileName += "_" + dateString + "." + ext;
		File file = new File(realPath + File.separator + fileDir, fileName);

		String resultStr = "";

		//service에서 list 담아온다
		if (direct.equals("direct")) {
			if(type.equals("excel")){
				resultStr = statHtmlService.getDirectMakeExcel(paramInfo, request);
			}else{
				resultStr = statHtmlService.getDirectMakeCSV(paramInfo, request);
			}
		} else {
			if(type.equals("excel")){
				resultStr = statHtmlService.getMakeExcel(paramInfo, request);
			}else{
				resultStr = statHtmlService.getMakeCSV(paramInfo, request);
			}
		}

		BufferedWriter bw = null;

		try{
			bw = new BufferedWriter(new FileWriter(file));
			//bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "EUC-KR"));
			bw.write(resultStr);
			//bw.write(resultStr, 0, resultStr.length());

		}catch(Exception e){
		}finally{
			try{
				bw.close();
			}catch(Exception e){}
		}

		return file.getName();

		/*
		bw.write(list);
		bw.close();
		*/
	}

	@RequestMapping(value = "/downLarge")
	public ModelAndView downLarge(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();

		/*2020.06.09 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		if(!paramInfo.getDirect().equals("direct")) {
			String referer = request.getHeader("referer");
			if(referer == null || referer.indexOf("statHtml.do") < 0){
				model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
				model.setViewName("alert");
				return model;
			}
		}
		
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("error");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("error");
			return model;
		}

		/* 2020.06.09 위 작업으로 인해 바로 차단 - 제니퍼 에러  발생안되도록하고 DB적재는 이제 고만...IP알아서 뭐할라고?
		if(!paramInfo.getDirect().equals("direct")) {
			String referer = request.getHeader("referer");
			if(referer == null || referer.indexOf("statHtml.do") < 0){
				//통계청 서비스용 인경우에만 로그를 적재
				if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){
					statHtmlService.setInterCeptLog(paramInfo, referer);
				}
				model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
				model.setViewName("error");
				return model;
			}
		}
		*/
		
		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir = "tmpFile";

		String fileName = request.getParameter("file");
		File file = new File(realPath + File.separator + fileDir + File.separator + fileName);

		/*2020.07.02 제니퍼에서 Exception으로 잡혀 에러로 찍히는거 안나오도록...손상호 주무관*/
		if(!file.isFile()){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("104", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}

		model.addObject("file", file);
		model.setViewName("downloadView");

		return model;
	}


	@RequestMapping(value = "/downNormal")
	public ModelAndView downNormal(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();
		
		/*2020.06.09 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		String referer = request.getHeader("referer");

		if( referer == null || referer.indexOf("statHtml.do") < 0){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}		
		
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("error");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("error");
			return model;
		}

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir = "tmpFile";

		String fileName = request.getParameter("file");
		File file = new File(realPath + File.separator + fileDir + File.separator + fileName);

		//2020.07.02 제니퍼에서 Exception으로 잡혀 에러로 찍히는거 안나오도록...손상호 주무관
		if(!file.isFile()){
			model.addObject("resultMsg",MessageManager.getInstance().getProperty("104", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}
		
		model.addObject("file", file);
		model.setViewName("downloadView");

		return model;
	}
	// 프린트 - 통계표 출력
	@RequestMapping(value = "/print")
	public String getPrint () throws Exception{

		String errMsg = null;
		int errCode = 0;

		return "include/print";
	}

	/*
	 * 안영수 선택정보 전체보기
	 */
	@RequestMapping(value = "/selectAll")
	public String getSelectAll (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) throws Exception{

		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addAttribute("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			return "alert";
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addAttribute("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			return "alert";
		}
		addSelectAllInfo(request, paramInfo);

		SelectAllInfo selectAllInfo = statHtmlService.getSelectAllInfo(paramInfo, model);
		model.addAttribute("selectAllInfo",selectAllInfo);
		return "/include/ifr_selectAll";
	}

	/*
	 * 안영수 조회범위 상세설정
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchRangeDetail")
	public String searchRangeDetail (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model)throws Exception{

		/*2020.06.09 - 직접접근으로 추정되는 접속이 제니퍼에서 error로 연속하여 올라오고 있음 접근금지하여 에러발생 차단- 손상호 주무관 */
		String referer = request.getHeader("referer");

		if( referer == null || referer.indexOf("statHtml.do") < 0){
			model.addAttribute("resultMsg",MessageManager.getInstance().getProperty("301", paramInfo.getDataOpt()));
			return "alert";
		}
		
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addAttribute("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			return "alert";
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addAttribute("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			return "alert";
		}
		addSelectAllInfo(request, paramInfo);

		SelectRangeInfo rangeInfo = statHtmlService.getRangeInfo(paramInfo, model);

		model.addAttribute("rangeInfo",rangeInfo);
		return "/include/ifr_searchRangeDetail";
	}

	/*
	 * 안영수 분석
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/assayInfo")
	public String assayInfo (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model)throws Exception{

		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addAttribute("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			return "alert";
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addAttribute("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			return "alert";
		}
		addSelectAllInfo(request, paramInfo);

		AssayInfo assayInfo = statHtmlService.getAssayInfo(paramInfo, model);

		model.addAttribute("assayInfo",assayInfo);
		return "/include/ifr_assayInfo";
	}

	/*
	 * 정창호 관련통계표
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/relationInfo")
	public String relationInfo (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{

		return "/include/ifr_relationInfo";
	}

	@RequestMapping(value = "/relationInfoList")
	public ModelAndView relationInfoList (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{

		ModelAndView model = new ModelAndView();
		String errMsg = null;
		int errCode = 0;

		try{
			addParamInfo(request, paramInfo);

			RelationInfo relationInfoList = statHtmlService.getRelationInfo(paramInfo);
			model.addObject("relationInfo",relationInfoList);
		}catch(StatExceptionManager se){
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			errCode = 1;
			log.info(errMsg);
		}catch(Exception e){
			e.printStackTrace();
			log.info(e.getMessage());
		}

		model.setViewName("jsonView");
		return model;
	}

	/*
	 * 스크랩화면
	 */
	@RequestMapping(value = "/myscrapView")
	public ModelAndView myscrapView (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		ModelAndView model = new ModelAndView();
		try{
			addParamInfo(request, paramInfo);
		}catch(StatExceptionManager se){
			model.addObject("resultMsg", MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}catch(Exception e){ //2020.07.16 제니퍼에 아예 안나오도록...
			model.addObject("resultMsg", MessageManager.getInstance().getProperty("fail.common.msg", paramInfo.getDataOpt()));
			model.setViewName("alert");
			return model;
		}

		String empId = paramInfo.getEmpId();
		String empNm = paramInfo.getEmpNm();
		if(empId != null && empId.trim().length() > 0){
			if(empNm == null || empNm.trim().length() == 0){
				empNm = statHtmlService.getEmpNm(paramInfo);
				paramInfo.setEmpNm(empNm);
			}
		}

		List folder_1 = statHtmlService.getMyscrapFolder_1(paramInfo);
		List folder_2 = statHtmlService.getMyscrapFolder_2(paramInfo);

		model.addObject("tblNm", paramInfo.getTblNm());
		model.addObject("empNm", paramInfo.getEmpNm());
		model.addObject("folder_1", folder_1);
		model.addObject("folder_2", folder_2);
		model.setViewName("/include/ifr_myscrap");

		return model;
	}

	/*
	 * 스크랩저장
	 */
	@RequestMapping(value = "/myscrapSave")
	public ModelAndView myscrapSave (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{

		String errMsg = "success";
		int errCode = 0;

		try{
			addParamInfo(request, paramInfo);
			statHtmlService.saveScrapInfo(paramInfo);
		}catch(StatExceptionManager se){
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			errCode = 1;
		}catch(Exception e){
			e.printStackTrace();
			errMsg = "error";
			errCode = 1;
		}

		ModelAndView model = new ModelAndView();

		model.addObject("result", errMsg);
		model.setViewName("jsonView");

		return model;
	}

	@RequestMapping(value = "/directDownDiv")
	public String directDownDiv(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) throws Exception{

		String msg="";

		try{
			addParamInfo(request, paramInfo);
			// 직접다운로드용 통계표정보
			Map stblMap = statHtmlService.getDirectStatInfo(paramInfo);
			// 직접다운로드용 주기 정보
			List<Map> prdSeList = statHtmlService.getDirectPrdInfo(paramInfo);

			if(prdSeList != null){
				stblMap.put("prdSeList", prdSeList );	// 주기 정보 Map에 주기 List put
			}

			// Max Cell 셋팅
			int maxCell = Integer.parseInt(PropertyManager.getInstance().getProperty("direct.maxrow"));
			// 제한될 시점 개수 계산
			int prdDeCnt = maxCell / ((BigDecimal)stblMap.get("DIM_CO")).intValue();
			stblMap.put("prdDeCnt", prdDeCnt);
			stblMap.put("VW_CD", request.getParameter("vwCd"));

			DecimalFormat df = new DecimalFormat("###,##0");

			stblMap.put("PROCESS", request.getParameter("process")); // 통계표 조회 프로그램 여부

			// 파라메터 셋팅
			model.addAttribute("stblMap", stblMap);
			model.addAttribute("maxCell", df.format(maxCell));								// 제한 셀 개수

		}catch(StatExceptionManager se){
			String errMsg = null;
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			model.addAttribute("resultMsg",errMsg);
			return "error";
		}catch(Exception e){
			//log.error(e); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
			msg = MessageManager.getInstance().getProperty("101", paramInfo.getDataOpt());
			model.addAttribute("resultMsg",msg);
			return "error";
		}
		return "/direct/directDownDiv";
	}

	@RequestMapping(value = "/directDownPrdDe")
	public String directDownPrdDe(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) throws Exception{

		String msg="";

		try{
			addParamInfo(request, paramInfo);
			List<Map> prdList =  statHtmlService.getDirectPrdList(paramInfo);
			model.addAttribute("prdList", prdList);
		}catch(StatExceptionManager se){
			msg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			model.addAttribute("resultMsg",msg);
			return "alert";
		}catch(Exception e){
			//e.printStackTrace();
			//log.error(e); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
			msg = MessageManager.getInstance().getProperty("101", paramInfo.getDataOpt());
			model.addAttribute("resultMsg",msg);
			return "error";
		}
		return "/direct/directDownPrdDe";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/statInfo")
	public String statInfo (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{

		return "/include/ifr_statInfo";
	}

	@RequestMapping(value = "/statInfoList")
	public ModelAndView statInfoList (
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{

		ModelAndView model = new ModelAndView();
		String errMsg = null;
		int errCode = 0;

		try{
			addParamInfo(request, paramInfo);

			RelationInfo statInfoList = statHtmlService.getStatInfo(paramInfo);
			model.addObject("statInfo",statInfoList);
		}catch(StatExceptionManager se){
			errMsg = MessageManager.getInstance().getProperty(se.getCode(), paramInfo.getDataOpt());
			errCode = 1;
			log.info(errMsg);
		}catch(Error e){
			e.printStackTrace();
			log.info(e.getMessage());
		}

		model.setViewName("jsonView");
		return model;
	}

}
