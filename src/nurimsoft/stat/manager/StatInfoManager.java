package nurimsoft.stat.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nurimsoft.stat.info.ClassInfo;
import nurimsoft.stat.info.ItemInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.info.StatInfo;
import nurimsoft.stat.info.StatLinkInfo;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.webapp.StatHtmlDAO;
import nurimsoft.webapp.StatHtmlService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.ModelMap;

import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class StatInfoManager {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "statHtmlService")
	protected StatHtmlService statHtmlService;

	StatInfo statInfo = null;

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public StatInfo getStatInfo(ParamInfo paramInfo, StatHtmlDAO statHtmlDAO, ModelMap model) throws Exception{
		//StatInfo 생성
		statInfo = new StatInfo(paramInfo);

		String periodStr ="";									// 주기 문자열(#으로 구분. ex) D#, D#M#Y#)

		//2014.01.15
		statInfo.setServerType(paramInfo.getServerType());
		statInfo.setServerUrl(PropertyManager.getInstance().getProperty("server.url"));

		// TO-DO StatInfo 정보
		//탭메뉴 구성
		//항목분류시점 탭 메뉴 조회//
		List<EgovMap> tabMenuList = (List<EgovMap>)statHtmlDAO.selectTabMenuList(paramInfo);
		statInfo.setTabMenuList(tabMenuList);
		ItemInfo imInfo = new ItemInfo();
		PeriodInfo piInfo = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());
		Map<String, String> paramMap = new HashMap();

		List<EgovMap> HtmlItemList = null;

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());
		//이규정, 언어와 데이터보기 옵션 추가
		paramMap.put("language", paramInfo.getLanguage());
		paramMap.put("dataOpt", paramInfo.getDataOpt());
		//서버타입과 서버로케이션 추가

		//2014.02.10 Controller 단에서 service_en으로 셋팅을 하나..한번 더 체크해서 넣어준다.(사전에 Error 방지)d
		if(paramInfo.getServerTypeOrigin().equals("service")){

			if(paramInfo.getLanguage().equals("en")){
				paramInfo.setServerType("service_en");
			}
		}

		//--------------------------------------------------------------------
		//2015.06.30 서비스용에선 상속통계표 기능 제외
		boolean isService = (paramInfo.getServerTypeOrigin().indexOf("service") >= 0) ? true : false;
		String originOrgId 	= "";
		String originTblId 	= "";
		if(!isService){
			//2015.03.09 상속통계표 START 
			EgovMap inheritMap 	= (EgovMap) statHtmlDAO.getStatInheritInfo(paramMap);
			String pubSeType 	= "";
			if( inheritMap != null ){
				originOrgId = inheritMap.get("referOrgId").toString();
				originTblId = inheritMap.get("referTblId").toString();
				pubSeType 	= inheritMap.get("pubSeType").toString();
				if(!"".equals( inheritMap.get("referTblId").toString() )){
					paramInfo.setInheritYn("Y");
					paramInfo.setOriginOrgId(originOrgId);
					paramInfo.setOriginTblId(originTblId);
					paramInfo.setPubSeType(pubSeType);

					statInfo.setParamInfo(paramInfo);
					
					paramMap.put("inheritYn",  	"Y");
					paramMap.put("orginOrgId", 	originOrgId);
					paramMap.put("orginTblId", 	originTblId);
					paramMap.put("pubSeType", 	pubSeType);
				}
			}
			//2015.03.09 상속통계표 END
		}
		//--------------------------------------------------------------------
		
		paramMap.put("serverLocation", paramInfo.getServerLocation());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("serverTypeOrigin", paramInfo.getServerTypeOrigin());

		List pushClassInfo = new ArrayList();

		for(int i=0; i<tabMenuList.size(); i++){
			//항목,분류...시점 동적DIV 생성을 위한 Attribute생성
			Map tmMap = new HashMap();
			tmMap = tabMenuList.get(i);
			tmMap.put("orgId", paramInfo.getOrgId());	//
			tmMap.put("tblId", paramInfo.getTblId());
			tmMap.put("dbUser", paramInfo.getDbUser());
			//이규정 수정1000
			tmMap.put("serverType",paramInfo.getServerType());			//권한구분 //

			if(tmMap.get("objVarId").equals("period")){
				
				//상속통계표 : 주기 시점은 부모통계표 정보
				if( "Y".equals(paramInfo.getInheritYn()) ){
					tmMap.put("inheritYn", 	 paramInfo.getInheritYn());
					tmMap.put("originOrgId", paramInfo.getOriginOrgId());
					tmMap.put("originTblId", paramInfo.getOriginTblId());
				}
				
				/* 
				 20.04.09 업무용에서 담당자가 st 파라미터를 이용하여 서비스용으로 조회할때 원래 serverType은 관리자 였으므로 기간보안 체크안함
				(손상호 주무관의 요청에 따라 [미리보는KOSIS]에서는 기간보안 체크안함.
				 */
				tmMap.put("serverTypeOrigin",paramInfo.getServerTypeOrigin());
				
				//시점정보 조회 리스트 만들기
				List<EgovMap> HtmlPeriodList = (List<EgovMap>)statHtmlDAO.selectPeriodInfoList(tmMap);

				List timeTempD = new ArrayList();
				List timeTempT = new ArrayList();
				List timeTempM = new ArrayList();
				List timeTempB = new ArrayList();
				List timeTempQ = new ArrayList();
				List timeTempH = new ArrayList();
				List timeTempY = new ArrayList();
				List timeTempF = new ArrayList();

				int compareIndex = 1;			//compareIndex는 1이상만 되면 됨
				for(EgovMap timeInfo:HtmlPeriodList){


					Map<String,String> pushTimeMap = new TreeMap<String, String>();

					String prdSeType = (String) timeInfo.get("prdSe");					//eGovMap 컬럼명 자동 변환됨..

					//주기구분에 따라 PeriodInfo List에 세팅
					if(Character.toString(piInfo.D).equals(prdSeType)){
						timeTempD.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempD.size()){
							periodStr =	periodStr.concat("#D");
						}
					}else if(Character.toString(piInfo.T).equals(prdSeType)){
						timeTempT.add((String)timeInfo.get("prdDe"));					// prdDe 컬럼속성 필요없으면 Map 지우고 요렇게 씀
						if(compareIndex == timeTempT.size()){
							periodStr = periodStr.concat("#T");
						}
					}else if(Character.toString(piInfo.M).equals(prdSeType)){
						timeTempM.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempM.size()){
							periodStr = periodStr.concat("#M");
						}
					}else if(Character.toString(piInfo.B).equals(prdSeType)){
						timeTempB.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempB.size()){
							periodStr = periodStr.concat("#B");
						}
					}else if(Character.toString(piInfo.Q).equals(prdSeType)){
						timeTempQ.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempQ.size()){
							periodStr = periodStr.concat("#Q");
						}
					}else if(Character.toString(piInfo.H).equals(prdSeType)){
						timeTempH.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempH.size()){
							periodStr = periodStr.concat("#H");
						}
					}else if(Character.toString(piInfo.Y).equals(prdSeType)){
						timeTempY.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempY.size()){
							periodStr = periodStr.concat("#Y");
						}
					}else if(Character.toString(piInfo.F).equals(prdSeType)){
						timeTempF.add((String)timeInfo.get("prdDe"));
						if(compareIndex == timeTempF.size()){
							periodStr = periodStr.concat("#F");
						}
					}
				}
					//시점 세팅
				piInfo.setListD(timeTempD);
				piInfo.setListT(timeTempT);
				piInfo.setListM(timeTempM);
				piInfo.setListB(timeTempB);
				piInfo.setListQ(timeTempQ);
				piInfo.setListH(timeTempH);
				piInfo.setListY(timeTempY);
				piInfo.setListF(timeTempF);

				//start, end setting
				if(timeTempD.size() > 0){
					piInfo.setStartD((String)timeTempD.get(0));
					piInfo.setEndD((String)timeTempD.get(timeTempD.size() - 1));
				}

				if(timeTempT.size() > 0){
					piInfo.setStartT((String)timeTempT.get(0));
					piInfo.setEndT((String)timeTempT.get(timeTempT.size() - 1));
				}

				if(timeTempM.size() > 0){
					piInfo.setStartM((String)timeTempM.get(0));
					piInfo.setEndM((String)timeTempM.get(timeTempM.size() - 1));
				}

				if(timeTempB.size() > 0){
					piInfo.setStartB((String)timeTempB.get(0));
					piInfo.setEndB((String)timeTempB.get(timeTempB.size() - 1));
				}

				if(timeTempQ.size() > 0){
					piInfo.setStartQ((String)timeTempQ.get(0));
					piInfo.setEndQ((String)timeTempQ.get(timeTempQ.size() - 1));
				}

				if(timeTempH.size() > 0){
					piInfo.setStartH((String)timeTempH.get(0));
					piInfo.setEndH((String)timeTempH.get(timeTempH.size() - 1));
				}

				if(timeTempY.size() > 0){
					piInfo.setStartY((String)timeTempY.get(0));
					piInfo.setEndY((String)timeTempY.get(timeTempY.size() - 1));
				}

				if(timeTempF.size() > 0){
					piInfo.setStartF((String)timeTempF.get(0));
					piInfo.setEndF((String)timeTempF.get(timeTempF.size() - 1));
				}

				//주기가 F 인 경우 PeriodInfo의 F에 대한 명칭과 코드값을 셋팅한다.
				if(timeTempF.size() > 0){
					String prdDetail = statHtmlDAO.selectPrdDetail(paramInfo);
					piInfo.setIrregular(prdDetail);
				}

				piInfo.setPeriodNm(StatPivotUtil.dataOptViewNm(tmMap,"object",paramInfo.getDataOpt()));
				//# 주기열 세팅
				periodStr = periodStr.substring(1,periodStr.length());

				statInfo.setPeriodStr(periodStr);
				statInfo.setPeriodCnt(HtmlPeriodList.size()); 			//총 주기수 세팅

			}else if(tmMap.get("objVarId").equals("13999001")){
				// tbl_id,org_id,obj_var_id로 항목리스트 구하기
				//tmMap.put("searchCondition", "D");			//상위만 조회:D 상위에서 하위조회: J  없을때는 전체 connectBy
				tmMap.put("searchItem","Y");
				HtmlItemList = (List<EgovMap>)statHtmlDAO.selectHtmlItemList(tmMap);	// tabMenuList.get(i)

				imInfo.setItmList(HtmlItemList);							//imInfo.setItmList로 바꾸기
				imInfo.setItmCnt(HtmlItemList.size());
				imInfo.setItmNm(StatPivotUtil.dataOptViewNm(tmMap,"object",paramInfo.getDataOpt()));

				//2013.12.11
				model.addAttribute("itmList"+i,StatPivotUtil.listViewNm(imInfo.getItmList(),"list",paramInfo.getDataOpt()));		//항목정보		한개이므로 고정ID

			}else{
				// tbl_id,org_id,obj_var_id로 분류리스트 구하기
				if( tmMap.get("objVarId").equals(StringUtils.defaultString(paramInfo.getObj_var_id(), "")) ){
					//egovMap에서는 '_'를 없애도 바로뒤의문자를 대문자로 변환하여 key값을 생성하기 때문에 '_'없애버린다.
					tmMap.put("itmid", paramInfo.getItm_id());
				}

				ClassInfo clInfo = new ClassInfo();
				String comparePubSe =((String)tmMap.get("pubSe"));			//visible 속성 세팅 비교값

				clInfo.setClassEngNm((String) tmMap.get("scrEng"));			//영문
	//			clInfo.setClassNm((String) tmMap.get("scrKor"));			//한글
				clInfo.setClassId((String) tmMap.get("objVarId"));			//ID
				clInfo.setTotIgnrAt((String) tmMap.get("totIgnrAt"));
	//			clInfo.setItmCnt(((BigDecimal) tmMap.get("totalCnt")).intValue());					//분류별리스트 전체갯수

				//영문화//
				clInfo.setClassNm(StatPivotUtil.dataOptViewNm(tmMap,"object",paramInfo.getDataOpt()));

				if(tmMap.get("itmid") != null) {
					tmMap.put("searchCondition", "J");			//상위만 조회:D 상위에서 하위조회: J  없을때는 전체 connectBy
					tmMap.put("itmId", tmMap.get("itmid"));			//상위만 조회:D 상위에서 하위조회: J  없을때는 전체 connectBy				
				}else {
					tmMap.put("searchCondition", "D");			//상위만 조회:D 상위에서 하위조회: J  없을때는 전체 connectBy
				}
				
				tmMap.put("searchCondition", "D");			//상위만 조회:D 상위에서 하위조회: J  없을때는 전체 connectBy
				
				List<EgovMap> HtmlClassList = (List<EgovMap>)statHtmlDAO.selectHtmlItemList(tmMap);					//totIgnrAt에 따라 쿼리 조건 변경됨
					
				//영문화//
				clInfo.setItmList(StatPivotUtil.listViewNm(HtmlClassList,"list",paramInfo.getDataOpt()));							//분류별 리스트

				/* VAR_ORD_CUR를 기준으로 세팅 null이면 VAR_ORD_SN으로 세팅 */	//화면에 출력할 순번
				if(tmMap.get("varOrdCur") == null){
					clInfo.setSn(String.valueOf(tmMap.get("varOrdSn")));
				}else{
					clInfo.setSn(String.valueOf(tmMap.get("varOrdCur")));
				}

				clInfo.setVarOrdSn(String.valueOf(tmMap.get("varOrdSn")));

				int maxLevel = statHtmlDAO.selectClassMaxLevel(tmMap);

				if(tmMap.get("visibleYn").equals("Y")){
					clInfo.setVisible(true);
				}else{
					clInfo.setVisible(false);
				}

				//분류 총 카운트 구하기 **
				if(tmMap.get("objVarId").equals(StringUtils.defaultString(paramInfo.getObj_var_id(), "")) ){
					//egovMap에서는 '_'를 없애도 바로뒤의문자를 대문자로 변환하여 key값을 생성하기 때문에 '_'없애버린다.
					tmMap.put("itm_id", paramInfo.getItm_id());
				}

				clInfo.setItmCnt(statHtmlDAO.getLevelCnt(tmMap));

				clInfo.setDepthLvl(Integer.toString(maxLevel));

				List tmpLvlArr = new ArrayList();
				//분류 레벨카운트 세팅
				for(int kk=1; maxLevel>=kk; kk++){
					//visibleYn 순서 맞춰줘야함...
					tmMap.put("classLvl", kk);
					int depthCnt = statHtmlDAO.selectClassDepthCnt(tmMap);
//					System.out.println("=====>"+tmpLvlArr.size());
					Map addMap = new HashMap();

					addMap.put("lvl",kk);
					addMap.put("objVarId",tmMap.get("objVarId"));
					addMap.put("lvlCnt",depthCnt);
					tmpLvlArr.add(addMap);
				}
//				System.out.println("=====11111111111111>"+tmpLvlArr);
				clInfo.setListClassLvl(tmpLvlArr);
				pushClassInfo.add(clInfo);									//리스트 add

				/* classInfoList 와 분류리스트 맵핑 주의 */
				model.addAttribute("itmList"+i,clInfo.getItmList());		//화면에 출력할 분류 리스트 itmList+id 세팅

			}

			// 각 탭메뉴에 해당하는 리스트정보 model에 add
		}



		Map selectMainInfo = statHtmlDAO.selectMainInfo(paramMap);
		statInfo.setTblNm((selectMainInfo.get("TBL_NM").toString()).replaceAll("\r\n", "")); //2014-05-20 값에 엔터값이 들어가있어서 조회 시스템 에러나는 항목 찾아 조치(박영옥 사무관) - 김경호
		statInfo.setTblId(selectMainInfo.get("TBL_ID").toString());
		String tblEngNm = (String)selectMainInfo.get("TBL_ENG_NM");
		if(tblEngNm == null){
			tblEngNm = "";
		}
		statInfo.setTblEngNm(tblEngNm.trim().replaceAll("\r\n", "")); //2014-04-07 영문명에 엔터값들어가는것도 조회시스템에서 에러나지않게 조치(박영옥 사무관) - 김경호
		statInfo.setItemCnt( ((BigDecimal)selectMainInfo.get("CHAR_ITM_CO")).intValue() );
		statInfo.setExistStblCmmtEng((String)selectMainInfo.get("EXIST_STBL_CMMT_ENG"));
		statInfo.setExistStblCmmtKor((String)selectMainInfo.get("EXIST_STBL_CMMT_KOR"));
		statInfo.setExistCmmtEng((String)selectMainInfo.get("EXIST_CMMT_ENG"));
		statInfo.setExistCmmtKor((String)selectMainInfo.get("EXIST_CMMT_KOR"));
		statInfo.setUnitId(((String)selectMainInfo.get("UNIT_ID")));
		statInfo.setUnitNmKor((String)selectMainInfo.get("UNIT_NM_KOR"));
		statInfo.setUnitNmEng((String)selectMainInfo.get("UNIT_NM_ENG"));
		statInfo.setDimUnitYn((String)selectMainInfo.get("DIM_UNIT_YN"));
		statInfo.setWgtYn((String)selectMainInfo.get("WGT_YN"));
		statInfo.setOlapStl((String)selectMainInfo.get("OLAP_STL"));
		statInfo.setDimCo(((BigDecimal)selectMainInfo.get("DIM_CO")).intValue());
		statInfo.setContainPeriod((String)selectMainInfo.get("CONTAIN_PERIOD"));
		statInfo.setRenewalDate((String)selectMainInfo.get("RENEWAL_DATE"));
		//이규정 추가(STAT_ID, PUB)
		statInfo.setStatId((String)selectMainInfo.get("STAT_ID"));
		statInfo.setPubLog((String)selectMainInfo.get("PUB"));
		statInfo.setTblSe((String)selectMainInfo.get("TBL_SE"));	// 2015.11.24 남규옥 추가 ::: TBL_SE(통계표 구분)
		statInfo.setOrgNm((String)selectMainInfo.get("ORG_NM"));

		// 2015.11.09 통계부호 존재여부
		statInfo.setSmblYn((String)selectMainInfo.get("SMBL_YN"));

		//2013.12.23 대용량 파일 서비스 여부 추가
		//통계청 이면서 국문서비스인 경우에만 처리한다.
		//if(paramInfo.getServerTypeOrigin().equals("service") && paramInfo.getServerLocation().equals("NSO")){
		//2017.03.30 영문서비스도 대용량 파일 서비스 추가
		if((paramInfo.getServerTypeOrigin().equals("service") || paramInfo.getServerTypeOrigin().equals("service_en")) && paramInfo.getServerLocation().equals("NSO")){
			statInfo.setMassYn((String)selectMainInfo.get("MASS_YN"));
		}

		//2014.01.02 직접다운로드 연결 여부
		String olapStl = statInfo.getOlapStl();
		String directYn = "N";
		if(olapStl != null && olapStl.substring(0, 1).equals("X")){
			//TODO - 직접다운로드 화면 완료 시 모든 케이스에 적용하도록 변경한다.(단 언어가 영문인 경우에만 제외, language=en)
			//if(paramInfo.getServerType().equals("service") && paramInfo.getServerLocation().equals("NSO") && statInfo.getServerUrl().indexOf("kosis.kr") > -1){
			if(!paramInfo.getLanguage().equals("en")){
				directYn = "Y";
			}
		}

		statInfo.setDirectYn(directYn);
		//statInfo.setDirectYn("Y"); //TEST

		statInfo.setClassInfoList(pushClassInfo);							//탭메뉴 세팅시...분류리스트
		statInfo.setItemInfo(imInfo);
		statInfo.setPeriodInfo(piInfo);

		if( paramInfo.getDataOpt().indexOf("en") > -1 ){
			statInfo.setUnitNm(statInfo.getUnitNmEng());
		}else{
			statInfo.setUnitNm(statInfo.getUnitNmKor());
		}

		//2013.10.25 추가 수정사항 : 통계표 단위가 없고 항목이 1개인 경우 그 항목에 단위가 있는 경우 통계표단위처럼 보여준다.(크~ 아무리 생각해도 이건...시스템 참 안보인다.)
		//2013.12.13 추가 : 항목이 1개인 경우 통계표명 표기 시 항목명이 나오도록 변경
		//2013.12.17 수정 : 항목개수를 체크할때 CHAR_ITM_CO로 하지 말고 공표구분 적용하여 오픈되어 있는 항목수로 한다.
		//if(statInfo.getItemCnt() == 1){
		//System.out.println("HtmlItemList.size() ::: " + HtmlItemList.size());
		if(HtmlItemList.size() == 1){

			EgovMap map = HtmlItemList.get(0);
			String tmpTblNm = null;
			String tmpDataOpt = null;

			/*	2017.07.18 기존 항목이 1개이고 그 항목명이 통계표명에 포함되지 않을때 항목명이 '통계표명 (항목명)' 이렇게 나오던것을 제거 - 김기만, 이원영 요청
			if( paramInfo.getDataOpt().indexOf("en") > -1 ){
				String en = (String)map.get("scrEng");
				if(en == null){
					en = "";
				}

				if(en.trim().length() > 0){
					//항목명이 통계표명에 포함되지 않는 경우 출력
					String tmpScr = (String)map.get("scrEng");
					tmpScr = tmpScr.replaceAll(" ", "");

					tmpTblNm = statInfo.getTblEngNm();	//영문명
					if(tmpTblNm != null){
						tmpTblNm = tmpTblNm.replaceAll(" ", "");
					}

					if(tmpTblNm.indexOf(tmpScr) < 0){
						statInfo.setTblEngNm(statInfo.getTblEngNm() + " (" + map.get("scrEng") + ")");
					}
				}

			}else{
				//항목명이 통계표명에 포함되지 않는 경우 출력
				String tmpScr = (String)map.get("scrKor");

				tmpTblNm = statInfo.getTblNm();	//한글명
				tmpDataOpt = paramInfo.getDataOpt(); // 부가기능설정

				if( tmpDataOpt.equals("cdko") && tmpScr.indexOf(" ") != -1 && tmpScr.length() > 0 ){ // 2014.05.22 부가기능설정에서 코드+한글일 경우 항목의 코드를 빼고 항목명만 제목에 있는지 검색하기 위해 - 김경호
					if( tmpScr.indexOf(" ") < tmpScr.length()){ // 사소한 substring 에러를 막기위해서 추가
						tmpScr = tmpScr.substring(tmpScr.indexOf(" "), tmpScr.length());
					}
				}
				tmpScr = tmpScr.replaceAll(" ", "");

				if(tmpTblNm != null){
					tmpTblNm = tmpTblNm.replaceAll(" ", "");
				}

				if(tmpTblNm.indexOf(tmpScr) < 0 && !tmpScr.equals("항목") ){ // 2014.09.12 항목명이 '항목' 일경우 제목옆에 붙이지 않음 - 김경호
					statInfo.setTblNm(statInfo.getTblNm() + " (" + map.get("scrKor") + ")");
				}
			}
			*/
			
			if(statInfo.getUnitId() == null){
				Map itemUnitMap = statHtmlDAO.selectItemUnit(paramInfo);
				String tmpUnitId = (String)itemUnitMap.get("unitId");

				if(tmpUnitId != null && tmpUnitId.trim().length() > 0){
					statInfo.setUnitId(tmpUnitId);
					statInfo.setUnitNmKor((String)itemUnitMap.get("unitNmKor"));
					statInfo.setUnitNmEng((String)itemUnitMap.get("unitNmEng"));

					if( paramInfo.getDataOpt().indexOf("en") > -1 ){
						statInfo.setUnitNm(statInfo.getUnitNmEng());
					}else{
						statInfo.setUnitNm(statInfo.getUnitNmKor());
					}
				}
			}
		}

		//초기조회조건 생성
		// 2014.05.02 paramInfo.getNew_win() 새창열기시 초기조회가 아닌 기존 조건들을 가지고 열기 위해서 추가되었음 - 김경호
		SettingManager settingManager = null;
		if( (paramInfo.getIsChangedDataOpt() != null && paramInfo.getIsChangedDataOpt().equals("Y")) || (paramInfo.getNew_win() != null && paramInfo.getNew_win().equals("Y")) ){
			settingManager = new ConditionSettingManager(statInfo, statHtmlDAO);
			paramInfo.setNew_win(""); //초기화
		}else{
			if(paramInfo.getScrId() != null && paramInfo.getScrId().trim().length() > 0){
				settingManager = new ScrapSettingManager(statInfo, statHtmlDAO);
			}else{
				settingManager = new DefaultSettingManager(statInfo, statHtmlDAO);
			}
		}

		settingManager.setCondition();

		//출처 및 링크서비스 정보 추가
		StatLinkInfo statLinkInfo = addStatLinkInfo(statHtmlDAO, paramInfo);
		statInfo.setStatLinkInfo(statLinkInfo);

		//통계표 팝업 화면 파일 명 추가
		//우선 서비스용에서만 적용되도록 한다.
		//TODO : 영문에서도 적용하는지 확인하여 equals를 indexOf > -1로 변경한다.
		// 2019.01.15 영문통계표에 팝업을 추가하기 위해 위처럼 equals를 indexOf 로 변경하였음.
		if(paramInfo.getServerTypeOrigin().indexOf("service") > -1 && paramInfo.getServerLocation().equals("NSO")){
			addPopupFileNm(statHtmlDAO, paramInfo);
			
			// 영문통계표 팝업은 딴데 있다네 ;;;
			if(paramInfo.getServerTypeOrigin().equals("service_en")){
				String buff = statInfo.getPopupHtmlUrl();
				
				if(buff != null){
					buff = buff.replace("kosis.kr", "kosis.kr/eng");
				}
				statInfo.setPopupHtmlUrl(buff);
			}
		}

		//이규정
		if(statInfo.getLevelExpr().equals("T") || statInfo.getLevelExpr().equals("S")){
			paramInfo.setEnableLevelExpr("Y");
		}

		paramInfo.setStatId(statInfo.getStatId());
		paramInfo.setPubLog(statInfo.getPubLog());

		//2013.11.19 추가
		//다운로드, 분석 가능여부 체크
		//01. 다운로드 체크
		int downCnt = statHtmlDAO.selectDownloadable(paramInfo);
		int funcCnt = statHtmlDAO.selectAnalyzable(paramInfo);

		if(downCnt > 0){
			statInfo.setDownloadable(false);
		}

		if(funcCnt > 0){
			statInfo.setAnalyzable(false);
		}

		return statInfo;
	}

	/*
	 * 출처 정보 셋팅하는 부분
	 * 보급기관용은 또 다르겠지..
	 * TO-DO 보급인 경우 적용해야 함.
	 */
	public StatLinkInfo addStatLinkInfo(StatHtmlDAO statHtmlDAO,ParamInfo  paramInfo){

		StatLinkInfo statLinkInfo = new StatLinkInfo();

		String serverLocation = paramInfo.getServerLocation();

		//조사아이디, 승인통계여부 및 온라인간행물, 보도자료 링크, 더보기 버튼 여부 컬럼, 문의처 URL 가져오기
		Map paramMap = new HashMap();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("serverTypeOrigin", paramInfo.getServerTypeOrigin());
		paramMap.put("st", paramInfo.getSt());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("etldbLink", paramInfo.getEtldbLink());
		try{
			Map statLinkMainMap = null;
			if(serverLocation.equals("SPLY")){
				statLinkMainMap = statHtmlDAO.getStatLinkMainSply(paramMap);
			}else{
				statLinkMainMap = statHtmlDAO.getStatLinkMain(paramMap);
			}

			statLinkInfo.setStatId((String)statLinkMainMap.get("statId"));
			statLinkInfo.setPubLink((String)statLinkMainMap.get("pubLink"));
			statLinkInfo.setNewsLink((String)statLinkMainMap.get("newsLink"));
			statLinkInfo.setConfmNo((String)statLinkMainMap.get("confmNo"));
			statLinkInfo.setInptYear((String)statLinkMainMap.get("inptYear"));
			statLinkInfo.setAppAt((String)statLinkMainMap.get("appAt"));
			statLinkInfo.setUrlCn((String)statLinkMainMap.get("urlCn"));
			statLinkInfo.setOrgEngNm((String)statLinkMainMap.get("orgEngNm"));
			statLinkInfo.setSnameeng((String)statLinkMainMap.get("snameeng"));

			//승인통계이면 영문 시 Source : Statistics Korea로 붙여주기만 한다.
			//비승인통계인 경우 영문에도 기관명만 보여주고...링크정보 비활성화 한다.(스크랩, 자료문의 만 보여준다)

			if(statLinkInfo.getAppAt().equals("Y")){
				//승인통계
				if( paramInfo.getDataOpt().indexOf("en") > -1 ){
					//영문

					//2013.11.29 우선 수정한다. tn_org에서 명칭을 가져와서 있으면 출력 없으면 출력하지 않는다.
					String orgEngNm = statLinkInfo.getOrgEngNm();

					StringBuffer statBuff = new StringBuffer();
					if(orgEngNm != null && orgEngNm.trim().length() > 0){
						statLinkInfo.setExistStatNm(true);

						// 기관명, 출처명에 "," 가 있는 경우 "「」" 로 표시될 수 있게 변경
						// 기관명에 "," 가 있는 경우 "「」" 로 표시될 수 있게 변경
						if( orgEngNm.indexOf(',')   > -1 ){
							orgEngNm   = "「"+orgEngNm+"」";
						}

						statBuff.append(orgEngNm);
						//statLinkInfo.setExprStatNm("Source : " + orgEngNm);

						//2013.12.18 출처명 추가
						String snameeng = statLinkInfo.getSnameeng();

						if(snameeng != null && snameeng.trim().length() > 0){

							// 출처명에 "," 가 있는 경우 "「」" 로 표시될 수 있게 변경
							if( snameeng.indexOf(',')   > -1 ){
								snameeng   = "「"+snameeng+"」";
							}

							statBuff.append(", " + snameeng);
						}

						statLinkInfo.setExprStatNm("Source: " + statBuff.toString());

						if(paramInfo.getServerLocation().equals("NSO") && statLinkInfo.getInptYear() != null){
							statLinkInfo.setExistMoreBtn(true);
						}
					}

					/*//2013.11.28이전 소스 주석처리
					statLinkInfo.setExistStatNm(true);
					statLinkInfo.setExprStatNm("Source : STATISTICS KOREA");
					*/
				}else{
					//한글 및 코드
					paramMap.put("statId", statLinkInfo.getStatId());
					paramMap.put("appAt", statLinkInfo.getAppAt());
					Map deptMap = statHtmlDAO.getStatLinkDept(paramMap);
					String deptNm = (String)deptMap.get("deptNm");
					String deptTel = (String)deptMap.get("deptTel");

					if(deptNm != null){
						statLinkInfo.setExistStatNm(true);
						statLinkInfo.setExprStatNm(deptNm);

						//출처 더보기 버튼 활성화
						//보급용은 없다. 2014.01.15
						if(paramInfo.getServerLocation().equals("NSO") && statLinkInfo.getInptYear() != null){
							statLinkInfo.setExistMoreBtn(true);
						}

					}

					if(deptTel != null && paramInfo.getDataOpt().indexOf("en") < 0){
						statLinkInfo.setExistInquire(true);
						statLinkInfo.setExprInquire(deptTel);

						//문의처 링크 활성화
						if(statLinkInfo.getUrlCn() != null){
							statLinkInfo.setExistInquireLink(true);
						}
					}

				}
			}else{
				//비승인통계
				
				if( paramInfo.getDataOpt().equals("en") || paramInfo.getDataOpt().equals("cden") ){
					//영문					
					paramMap.put("appAt", statLinkInfo.getAppAt());
					Map deptMap = statHtmlDAO.getStatLinkDept(paramMap);
					String deptNm = (String)deptMap.get("deptNm");
					String deptTel = (String)deptMap.get("deptTel");

					if(deptNm != null){
						statLinkInfo.setExistStatNm(true);
						statLinkInfo.setExprStatNm(statLinkInfo.getSnameeng());

						//출처 더보기 버튼 활성화
						if(statLinkInfo.getInptYear() != null){
							statLinkInfo.setExistMoreBtn(true);
						}

					}
				}else{
					//한글 및 코드
					//승인통계와 동일하다. 수행되는 sql이 다름..
					//규칙이 좀 많이 바뀌냐. 어쩔수없이 동일하게 붙여놓고 시작한다.
					paramMap.put("appAt", statLinkInfo.getAppAt());
					Map deptMap = statHtmlDAO.getStatLinkDept(paramMap);
					String deptNm = (String)deptMap.get("deptNm");
					String deptTel = (String)deptMap.get("deptTel");

					if(deptNm != null){
						statLinkInfo.setExistStatNm(true);
						statLinkInfo.setExprStatNm(deptNm);

						//출처 더보기 버튼 활성화
						if(statLinkInfo.getInptYear() != null){
							statLinkInfo.setExistMoreBtn(true);
						}

					}

					if(deptTel != null){
						statLinkInfo.setExistInquire(true);
						statLinkInfo.setExprInquire(deptTel);

						//문의처 링크 활성화
						if(statLinkInfo.getUrlCn() != null){
							statLinkInfo.setExistInquireLink(true);
						}
					}


					/*
					 * TN_ORG에서 가져오는것 아니잖아!!! >>>> 이전 규칙이었던것 같음.-_-
					*/
					/*
					Map orgMap = statHtmlDAO.getStatLinkOrg(paramMap);
					String orgNm = (String)orgMap.get("orgNm");
					//String orgNmEng = (String)orgMap.get("orgNmEng");	//영문에서는 보여주지 않으므로 필요가 없다.

					if(orgNm != null){
						statLinkInfo.setExistStatNm(true);
						statLinkInfo.setExprStatNm(orgNm);
					}
					*/

				}
			}
			
			String statcnt = String.valueOf(statLinkMainMap.get("statcnt"));
			int cnt = Integer.parseInt(statcnt);
			if(cnt > 1) {
				statLinkInfo.setExistMoreBtn(true);
			}
						
		} catch (NullPointerException e) {
			statLinkInfo = null;
		}

		//statInfo에 추가
		return statLinkInfo;
	}

	//팝업 html 페이지 명 추가
	//TO-DO 서비스용에만 적용되는지 확인해야함. 우선 서비스용에만 호출되도록 한다.
	private void addPopupFileNm(StatHtmlDAO statHtmlDAO,ParamInfo  paramInfo){
		Map map = statHtmlDAO.getPopupFileNm(paramInfo);
		if(map != null){
			statInfo.setPopupFileNm( (String)map.get("NAME") );
			statInfo.setPopupFileHeight( (String)map.get("POPUP_HEIGHT") );
		}
	}

}
