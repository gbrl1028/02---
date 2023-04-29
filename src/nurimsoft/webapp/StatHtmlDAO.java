package nurimsoft.webapp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Repository("statHtmlDAO")
@SuppressWarnings("unchecked")
public class StatHtmlDAO extends SqlMapClientDaoSupport{

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "sqlMapClient")
	public void setSuperSqlMapClient(SqlMapClient sqlMapClient){
		super.setSqlMapClient(sqlMapClient);
	}

	/***************************************************************************************************************
	 * 이규정 시작
	 ***************************************************************************************************************/

	/*
	 * defaultsetting
	 */

	//TC_PRD_INFO(주기정보테이블)
	public List getPrdInfoArr(String dbUser){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectTcPrdInfoList", dbUser);
	}

	//통계표가 가지고 있는 최단 주기 가져오기
	public String getDefaultPeriod(ParamInfo paramInfo){
		//상속통계표일때 
		if( "Y".equals( paramInfo.getInheritYn() ))
			return (String)getSqlMapClientTemplate().queryForObject("statinherit.selectDefaultPeriod", paramInfo);
		else
			return (String)getSqlMapClientTemplate().queryForObject("defaultsetting.selectDefaultPeriod", paramInfo);
	}

	//화면정보 가져오기(대표화면)
	public List getScrInfo(ParamInfo paramInfo){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectScrInfo", paramInfo);
	}

	//화면정보 가져오기(scrId 이용)
	public List getScrInfoUsingScrId(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectScrInfoUsingScrId", paramMap);
	}

	//분류의 특정레벨이하의 분류값 수 가져오기
	public int getLevelCnt(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("defaultsetting.selectLevelCnt", paramMap);
	}

	//분류의 특정레벨이하의 분류값 가져오기
	public List getClassItemListAll(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectClassItemListAll", paramMap);
	}

	//마이스크랩 테이블에서 분류값 가져오기
	public List getClassItemListScrap(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectClassItemListScrap", paramMap);
	}

	//분류의 특정레벨이하의 분류값 가져오기 - 단 특정 수만큼 가져온다.
	public List getClassItemListLimited(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectClassItemListLimited", paramMap);
	}

	//분류의 MAX LEVEL 가져오기
	public int getMaxLevel(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("defaultsetting.selectMaxLevel", paramMap);
	}

	//비공개인 분류의 함수값 여부가 'Y'인 분류값 가져오기
	public String getClassItemListUnvisible(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("defaultsetting.selectClassItemListUnvisible", paramMap);
	}

	//항목값 가져오기
	public List getItemListAll(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectItemListAll", paramMap);
	}

	//항목값 가져오기 - 단 특정 수만큼 가져온다.
	public List getItemListLimited(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectItemListLimited", paramMap);
	}

	//항목값 가져오기
	public List getItemListScrap(Map paramMap){
		return getSqlMapClientTemplate().queryForList("defaultsetting.selectItemListScrap", paramMap);
	}

	//특정 주기의 시점 가져오기
	public List getPeriodList(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectPeriodList", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("defaultsetting.selectPeriodList", paramMap);
	}

	//TN_STBL_SCR.PARAM 가져오기
	public String getScrParam(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("defaultsetting.selectScrParam", paramMap);
	}

	//TN_STBL_SCR.PARAM_WEB 가져오기
	public String getScrParamWeb(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("defaultsetting.selectScrParamWeb", paramMap);
	}

	//TN_STBL_SCR_ITM에서 데이터 건수 가져오기(조회설정값이 있는지를 확인하기 위함 - SIGA에서 스크랩 생성되는 경우에는 없음)
	public int getScrItmCount(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("defaultsetting.selectScrItmCount", paramMap);
	}

	/*
	 * statdata
	 */

	public Map getDimensionInfo(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return (Map)getSqlMapClientTemplate().queryForObject("statinherit.selectDimensionInfo",paramMap);
		//기존 조회
		else
			return (Map)getSqlMapClientTemplate().queryForObject("statdata.selectDimensionInfo", paramMap);
	}

	public List getDimensionItemList(Map paramMap, String type){

		List retList = null;
		if(type.equals("class")){
			//상속통계표일때 
			if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
				retList = getSqlMapClientTemplate().queryForList("statinherit.selectClassDimensionList", paramMap);
			//기존 조회
			else
				retList = getSqlMapClientTemplate().queryForList("statdata.selectClassDimensionList", paramMap);
		}else if(type.equals("item")){
			//상속통계표일때 
			if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
				retList = getSqlMapClientTemplate().queryForList("statinherit.selectItemDimensionList", paramMap);
			//기존 조회
			else
				retList = getSqlMapClientTemplate().queryForList("statdata.selectItemDimensionList", paramMap);
		}

		return retList;
	}

	public String getTimeDimensionList(ParamInfo paramInfo){
		return (String)getSqlMapClientTemplate().queryForObject("statdata.selectTimeDimensionList", paramInfo);
	}

	//비공개인 분류 목록 가져오기
	public List getClassListForWhereClause(ParamInfo paramInfo){
		return getSqlMapClientTemplate().queryForList("statdata.selectClassListForWhereClause", paramInfo);
	}

	//차원, 수치 테이블 데이터 가져오기
	public List getDimDtData(Map paramMap){
		return getSqlMapClientTemplate().queryForList("statdata.selectDimDtData", paramMap);
	}

	//2013.11.20
	//분석 시 비교할 시점 가져오기
	public Map selectFuncPrd(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statdata.selectFuncPrd", paramMap);
	}

	//분류의 순번(VAR_ORD_SN) 가져오기
	public String selectVarOrdSn(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("statdata.selectVarOrdSn", paramMap);
	}

	//분석종류 이름가져오기
	public Map selectAnalTypeNm(ParamInfo paramInfo){
		return (Map)getSqlMapClientTemplate().queryForObject("statdata.selectAnalTypeNm", paramInfo);
	}

	//분석기능주기 이름가져오기
	public Map selectAnalCmprNm(ParamInfo paramInfo){
		return (Map)getSqlMapClientTemplate().queryForObject("statdata.selectAnalCmprNm", paramInfo);
	}

	//특정 분류값, 항목 이름 가져오기
	public Map selectItemName(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statdata.selectItemName", paramMap);
	}

	/*
	 * CmmtInfo setting
	 */
	public List getStatCmmt(Map paramMap){
		return getSqlMapClientTemplate().queryForList("cmmtinfo.selectStblCmmt", paramMap);
	}

	public List getClassCmmt(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectClassCmmt", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("cmmtinfo.selectClassCmmt", paramMap);
	}

	public List getClassItemCmmt(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectClassItemCmmt", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("cmmtinfo.selectClassItemCmmt", paramMap);
	}

	public List getDimCmmt(Map paramMap){
		
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectDimCmmt", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("cmmtinfo.selectDimCmmt", paramMap);
	}

	/*
	 * 번호 클릭 시 주석가져오기
	 */

	public List getCmmtStr(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectCmmtStr", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("cmmtinfo.selectCmmtStr", paramMap);
	}

	public String getCmmtTitle(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("cmmtinfo.selectCmmtTitle", paramMap);
	}

	/*
	 * StatHtmlSessionListener에서 세션이 destroy 될 때 조회조건 테이블에서 데이터 삭제
	 */
	public void deleteSearchCondition(String sessionId){
		Map<String, String> paramMap = new HashMap<String, String>();

		String dbUser = PropertyManager.getInstance().getProperty("server.dbuser") + ".";
		String condTable = PropertyManager.getInstance().getProperty("server.dbuser")+"."+PropertyManager.getInstance().getProperty("table.condition"); //2014.04.29 호스팅 DB이관문제 때문에 TN_STAT_HTML_COND_WEB 관련 수정 - 김경호

		paramMap.put("sessionId", sessionId);
		paramMap.put("dbUser", dbUser);
		paramMap.put("condTable", condTable);

		getSqlMapClientTemplate().delete("common.deleteSearchCondition", paramMap);
	}

	//출처관련 메인 정보 가져오기 : 통계청
	public Map getStatLinkMain(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statlinkinfo.selectStatLinkMain", paramMap);
	}

	//출처관련 메인 정보 가져오기 : 보급
	public Map getStatLinkMainSply(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statlinkinfo.selectStatLinkMainSply", paramMap);
	}

	//출처에 출력될 부서명 및 문의처 가져오기
	public Map getStatLinkDept(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statlinkinfo.selectStatLinkDept", paramMap);
	}

	//비승인 통계의 경우 출처에 출력될 것 가져오기(tn_org에서 기관명칭만을 가져옴)
	public Map getStatLinkOrg(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statlinkinfo.selectStatLinkOrg", paramMap);
	}

	//팝업 html 파일명 가져오기
	public Map getPopupFileNm(ParamInfo paramInfo){
		return (Map)getSqlMapClientTemplate().queryForObject("statinfo.selectPopupFileNm", paramInfo);
	}

	//통계청 서비스용 log_seq 가져오기
	public String getLogSeqForNsoService(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("log.selectLogSeqForNsoService", paramMap);
	}

	//tn_uselog_code에서 해당 conn_path의 데이터가 있는지 체크
	public int getChkCnt(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("log.selectChkCnt", paramMap);
	}

	//tn_uselog_code에서 해당 conn_path 가져오기
	public String getConnPath(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("log.selectConnPath", paramMap);
	}

	//통계청 서비스용 empNm가져오기
	public String getEmpNmForNsoService(String empId){
		return (String)getSqlMapClientTemplate().queryForObject("log.selectEmpNmForNsoService", empId);
	}

	//업무용(호스팅) empNm 가져오기
	public String getEmpNmForNsoStat(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("log.selectEmpNmForNsoStat", paramMap);
	}

	//통계청 서비스용 TN_USELOG 적재
	public void insertUseLogForNsoService(Map paramMap){
		getSqlMapClientTemplate().insert("log.insertUseLogForNsoService", paramMap);
	}

	//통계청 서비스용 TH_USELOG_INTERCEPT 적재
	public void insertUseLogInterceptForNsoService(Map paramMap){
		getSqlMapClientTemplate().insert("log.insertUseLogInterceptForNsoService", paramMap);
	}

	//통계청 업무용 TH_LOG_SELECT 적재
	public void insertUseLogForStatNso(Map paramMap){
		getSqlMapClientTemplate().insert("log.insertUseLogForStatNso", paramMap);
	}

	//통계청 호스팅, 보급용 TN_COUNTLOG update
	public int updateUseLogForHostingNSply(Map paramMap){
		return getSqlMapClientTemplate().update("log.updateUseLogForHostingNSply", paramMap);
	}

	//통계청 호스팅, 보급용 TN_COUNTLOG insert
	public void insertUseLogForHostingNSply(Map paramMap){
		getSqlMapClientTemplate().insert("log.insertUseLogForHostingNSply", paramMap);
	}

	//통계청 서비스용 TN_OLAPLOG update
	public int updateOlapLog(Map paramMap){
		return getSqlMapClientTemplate().update("log.updateOlapLog", paramMap);
	}

	//통계청 서비스용 TN_OLAPLOG insert
	public void insertOlapLog(Map paramMap){
		getSqlMapClientTemplate().insert("log.insertOlapLog", paramMap);
	}

	/*****************************************************************
	 * 메타파일생성
	******************************************************************/

	//통계표명 및 자료다운일자 가져오기
	public Map getTblNm(ParamInfo paramInfo){
		return (Map)getSqlMapClientTemplate().queryForObject("metainfo.selectTblNm", paramInfo);
	}

	//TC_PRD_INFO로 부터 주기 명칭 가져오기
	public Map getPrdNm(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("metainfo.selectPrdNm", paramMap);
	}

	//TC_PRD_INFO로 부터 주기 명칭 가져오기(부정기 일 경우)
	public Map getPrdNmF(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("metainfo.selectPrdNmF", paramMap);
	}

	//주석가져오기
	public List getCmmtList(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectExcelCmmtList", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("metainfo.selectCmmtList", paramMap);
	}

	/*****************************************************************
	 * SDMX
	******************************************************************/

	//req_seq 가져오기
	public int selectSdmxReqSeq(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("sdmx.selectSdmxReqSeq", paramMap);
	}

	//TN_REQ_SMDX_INFO_2013 insert
	public void insertSdmxInfo(Map paramMap){
		getSqlMapClientTemplate().insert("sdmx.insertSdmxInfo", paramMap);
	}

	//TN_REQ_SMDX_INFO_MAP_2013 insert (분류, 항목)
	public void insertSdmxInfoMapObjItm(Map paramMap){
		getSqlMapClientTemplate().insert("sdmx.insertSdmxInfoMapObjItm", paramMap);
	}

	//TN_REQ_SMDX_INFO_MAP_2013 insert (시점)
	public void insertSdmxInfoMapPrd(final Map paramMap, final String prd){
		getSqlMapClientTemplate().execute(new SqlMapClientCallback(){
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException{
				executor.startBatch();

				String[] prdArr = prd.split("@");

				for(int i = 0; i < prdArr.length; i++){
					String[] tmpArr = prdArr[i].split(",");
					for(int j = 1; j < tmpArr.length; j++){
						paramMap.put("prdSe", tmpArr[0]);
						paramMap.put("prdDe", tmpArr[j]);

						executor.insert("sdmx.insertSdmxInfoMapPrd",paramMap);
					}
				}

				return new Integer(executor.executeBatch());
			}
		});
	}

	//SP_MAKE_SDMX_2013 호출
	public String callSdmxMake(Map paramMap) throws Exception{
		return (String)getSqlMapClientTemplate().queryForObject("sdmx.callSdmxMake", paramMap);
	}

	//CLOB 데이터 가져오기
	public String selectSdmxClob(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("sdmx.selectSdmxClob", paramMap);
	}

	//매핑 테이블의 데이터 삭제
	public void deleteSdmxInfoMap(Map paramMap){
		getSqlMapClientTemplate().delete("sdmx.deleteSdmxInfoMap", paramMap);
	}

	//스크랩 폴더 1 가져오기
	public List selectMyscrapFolder_1(ParamInfo paramInfo){
		return getSqlMapClientTemplate().queryForList("scrap.selectMyscrapFolder_1", paramInfo);
	}

	//스크랩 폴더 2 가져오기
	public List selectMyscrapFolder_2(ParamInfo paramInfo){
		return getSqlMapClientTemplate().queryForList("scrap.selectMyscrapFolder_2", paramInfo);
	}

	//SCR_ID 가져오기
	public String selectScrId(Map paramMap){
		return (String)getSqlMapClientTemplate().queryForObject("scrap.selectScrId", paramMap);
	}

	//스크랩 테이블에 저장하기
	//1. 데이타
	public void insertScrapData(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapData", paramMap);
	}

	//2. 가중치
	public void insertScrapWgtCo(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapWgtCo", paramMap);
	}

	//3. 함수값
	public void insertScrapFunc(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapFunc", paramMap);
	}

	//항목 넣기
	public void insertScrapItem(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapItem", paramMap);
	}

	//분류명칭 가져오기(한글, 영문)
	public Map selectClassName(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("scrap.selectClassName", paramMap);
	}

	//분류 넣기
	public void insertScrapClass(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapClass", paramMap);
	}

	//시점 넣기
	public void insertScrapPeriod(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapPeriod", paramMap);
	}

	//TN_STBL_SCR_ITM 데이터 넣기
	public void insertScrapTnStblScrItm(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapTnStblScrItm", paramMap);
	}

	//TN_STBL_SCR 파라미터 넣기
	public void insertScrapParams(Map paramMap){
		getSqlMapClientTemplate().insert("scrap.insertScrapParams", paramMap);
	}

	//다운로드 가능여부 체크(1보다 크면 불가능)
	public int selectDownloadable(ParamInfo paramInfo){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.selectDownloadable", paramInfo);
	}

	//분석 가능여부 체크(1보다 크면 불가능)
	public int selectAnalyzable(ParamInfo paramInfo){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.selectAnalyzable", paramInfo);
	}

	//분석기능예외코드 가져오기
	public String selectFnExcptCd(ParamInfo paramInfo){
		return (String)getSqlMapClientTemplate().queryForObject("statinfo.selectFnExcptCd", paramInfo);
	}

	//TN_RECD_PRD에서 SMBL_CN 갑 가져오기
	public List getRecdPrdSmblCn(Map paramMap){
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectRecdPrdSmblCn", paramMap);
		//기존 조회
		else
			return getSqlMapClientTemplate().queryForList("statdata.selectRecdPrdSmblCn", paramMap);
	}

	public String  getRTITLEListId(ParamInfo paramInfo){
		return (String)getSqlMapClientTemplate().queryForObject("statinfo.selectRTITLEListId", paramInfo);
	}

	//2014.01.10
	public int selectCntForEmpAuth(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.selectCntForEmpAuth", paramMap);
	}

	//2014.07.09 이규정 더미여부 가져오기 추가(TODO 추후에..대용량 여부도 추가해야겠지..관련 클래스 체크해야 함!!)
	public Map selectStblInfoExtra(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statinfo.selectStblInfoExtra", paramMap);
	}

	//2014.10 마트 존재여부 체크
	public int chkMartExist(String sql){
		return (Integer)getSqlMapClientTemplate().queryForObject("statdata.selectMartExist", sql);
	}

	/***************************************************************************************************************
	 * 이규정 끝
	 ***************************************************************************************************************/



	/***************************************************************************************************************
	 * 안영수 시작
	 * 탭메뉴 만들기
	 ***************************************************************************************************************/

	public List<EgovMap> selectTabMenuList(ParamInfo paramInfo) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectTabMenuList",paramInfo);
	}

	//분류 리스트 조회
	public List<EgovMap> selectHtmlItemList(Map tmMap) throws Exception{

		return getSqlMapClientTemplate().queryForList("statinfo.selectHtmlItemList",tmMap);
	}

	//분류의 MAX LEVEL 가져오기
	public int selectClassMaxLevel(Map tmMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.selectClassMaxLevel", tmMap);
	}

	//분류레벨별 COUNT 조회
	public int selectClassDepthCnt(Map tmMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.selectClassDepthCnt", tmMap);
	}

	// 2015.5.18 비공개 분류수
	public int  selectItmListPub(Map tmMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.selectItmListPub", tmMap);
	}

	// 2015.06.03 공개 분류수
	public int  openListCnt(Map tmMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.openListCnt", tmMap);
	}

	// 2015.10.21 분석함수 주석
	public List<EgovMap> assayTypeCmmtList(Map tmMap) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.assayTypeCmmtList",tmMap);
	}

/*	// 2015.07.21 통계표 목록공개 여부 확인 - 9월 반영 예정으로 주석 처리
	public int  openTblCnt(Map tmMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.openTblCnt", tmMap);
	}*/

	//주기,시점 조회 && AJAX동시사용`
	public List<EgovMap> selectPeriodInfoList(Map tmMap) throws Exception{
		//상속통계표일때 
		if( tmMap.get("inheritYn") != null && "Y".equals( tmMap.get("inheritYn").toString() ))
			return getSqlMapClientTemplate().queryForList("statinherit.selectPeriodInfoList",tmMap);
		else
			return getSqlMapClientTemplate().queryForList("statinfo.selectPeriodInfoList",tmMap);
	}

	//기본정보,주석여부,가중치..등 조회
	public Map selectMainInfo(Map paramMap) throws Exception{
		//상속통계표일때 
		if( paramMap.get("inheritYn") != null && "Y".equals( paramMap.get("inheritYn").toString() ))
			return (Map)getSqlMapClientTemplate().queryForObject("statinherit.selectInheritMainInfo",paramMap);
		//기존 통계표 조회
		else
			return (Map)getSqlMapClientTemplate().queryForObject("statinfo.selectMainInfo",paramMap);
	}

	public void deleteRequirement(ParamInfo paramInfo) {
		getSqlMapClientTemplate().delete("statinfo.deleteRequirement",paramInfo);
	}

	public void searchRequirementInsert(final JSONArray jFieldArr, final ParamInfo paramInfo) {

		getSqlMapClientTemplate().execute(new SqlMapClientCallback(){
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException{
				executor.startBatch();

				Map paramMap = new HashMap();
				paramMap.put("sessionId",paramInfo.getSessionId());
				paramMap.put("dbUser",paramInfo.getDbUser());

				//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
				paramMap.put("condTable", paramInfo.getCondTable());

				for(int i=0; i < jFieldArr.size(); i++){

					JSONObject jObj = (JSONObject) jFieldArr.get(i);

					paramMap.put("targetId", StringUtil.stringCheck((String)jObj.get("targetId")));
					paramMap.put("targetValue",StringUtil.stringCheck((String)jObj.get("targetValue")));
					paramMap.put("prdValue",StringUtil.stringCheck((String)jObj.get("prdValue")));

					executor.insert("statinfo.searchRequirementInsert",paramMap);

				}

				return new Integer(executor.executeBatch());
			}
		});

	}

	public String selectPrdDetail(ParamInfo paramInfo) throws Exception{
		//2015.06.23 상속통계표일때 
		if( "Y".equals( paramInfo.getInheritYn() )){
			return (String)getSqlMapClientTemplate().queryForObject("statinherit.selectPrdDetail", paramInfo);
		}else{
			return (String)getSqlMapClientTemplate().queryForObject("statinfo.selectPrdDetail", paramInfo);
		}
	}

	public List<EgovMap> selectTcPrdInfo(String dbUser) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectTcPrdInfo", dbUser);
	}

	public List<EgovMap> selectTcPrdDetail(String dbUser) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectTcPrdDetail", dbUser);
	}

	public Map selectItemUnit(ParamInfo paramInfo) throws Exception{
		return (Map)getSqlMapClientTemplate().queryForObject("statinfo.selectItemUnit",paramInfo);
	}

	public String getStatHtmlDefaultCondition(ParamInfo paramInfo)throws Exception{
		return (String)getSqlMapClientTemplate().queryForObject("statinfo.statHtmlDefaultCondition", paramInfo);
	}

	public Map selectRangeClassInfo(Map paramMap){
		return (Map)getSqlMapClientTemplate().queryForObject("statinfo.selectRangeClassInfo", paramMap);
	}

	//조회범위 상세설정
	public List<EgovMap> selectRangeItemList(Map paramMap) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectRangeItemList",paramMap);
	}

	public List<EgovMap> selectRangeClassList(Map paramMap) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectRangeClassList",paramMap);
	}

	//분석
	public String selectExcCode(ParamInfo paramInfo) throws Exception{
		return (String)getSqlMapClientTemplate().queryForObject("statinfo.selectExcCode", paramInfo);
	}

	public List<EgovMap> selectAssayTypeList(Map paramMap) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectAssayTypeList",paramMap);
	}

	// 분석 종류별 주석
	public List<EgovMap> selectCompareTypeList(Map paramMap) {
		return getSqlMapClientTemplate().queryForList("statinfo.selectCompareTypeList",paramMap);
	}

	public List<EgovMap> selectParentIcon(Map paramMap) throws Exception{
		return getSqlMapClientTemplate().queryForList("statinfo.selectParentIcon",paramMap);
	}
	/***************************************************************************************************************
	 * 안영수 끝
	 ***************************************************************************************************************/

	/***************************************************************************************************************
	 * 김정현
	 * 파일다운로드 시작
	 ***************************************************************************************************************/

	// 분류 리스트
	public List<Map> getObjVarList(ParamInfo paramInfo) throws Exception{
		return getSqlMapClientTemplate().queryForList("downlarge.objVarList",paramInfo);
	}

	// 항목리스트
	public List<Map> getItmIdList(ParamInfo paramInfo) throws Exception{
		return getSqlMapClientTemplate().queryForList("downlarge.itmIdList",paramInfo);
	}

	// 시점
	public String getPrdValue(ParamInfo paramInfo){
		return (String)getSqlMapClientTemplate().queryForObject("downlarge.selectPrdValue", paramInfo);
	}

	//차원, 수치 테이블 데이터 가져오기
	public List getSelectDownData(Map tmpMap){
		return getSqlMapClientTemplate().queryForList("downlarge.selectDownData", tmpMap);
	}

	// 부정기 체크
	public String getSelectPrdDetail(ParamInfo paramInfo) {
		//2015.06.23 상속통계표일때 
		if( "Y".equals( paramInfo.getInheritYn() )){
			return (String)getSqlMapClientTemplate().queryForObject("statinherit.selectPrdDetail", paramInfo);
		}else{
			return (String)getSqlMapClientTemplate().queryForObject("downlarge.selectPrdDetail",paramInfo);
		}
	}

	// 통계표 단위
	public Map getSelectTblUnitNm(ParamInfo paramInfo) {
		return (Map)getSqlMapClientTemplate().queryForObject("downlarge.selectTblUnitNm",paramInfo);
	}
	/***************************************************************************************************************
	 * 김정현 끝
	 ***************************************************************************************************************/

	/***************************************************************************************************************
	 * 직접 파일다운로드 시작
	 ***************************************************************************************************************/
	/*
	 * 직접다운로드 통계표 정보를 불러오는 부분
	 * @param paramInfo
	 * @return
	 */
	public Map getDirectStatInfo(ParamInfo paramInfo) {
		return (Map)getSqlMapClientTemplate().queryForObject("direct.getMetaDownInfo", paramInfo);
	}

	/*
	 * 직접다운로드 통계표 주기 정보를 불러오는 부분
	 * @param paramInfo
	 * @return
	 */
	public List<Map> getDirectPrdInfo(ParamInfo paramInfo) {
		return getSqlMapClientTemplate().queryForList("direct.getSelectPrdSe", paramInfo);
	}

	/*
	 * 직접다운로드 통계표 시점 정보를 불러오는 부분
	 * @param paramInfo
	 * @return
	 */
	public List<Map> getDirectPrdDe(ParamInfo paramInfo) {
		return getSqlMapClientTemplate().queryForList("direct.getSelectPrdDe", paramInfo);
	}

	/*
	 * 직접다운로드 통계표 분류 정보를 불러오는 부분
	 * @param paramInfo
	 * @return
	 */
	public List<Map> getObjItmInfo(ParamInfo paramInfo) {
		return getSqlMapClientTemplate().queryForList("direct.objitminfo",paramInfo);
	}

	/*
	 * 직접다운로드 통계표 주석 정보를 불러오는 부분
	 * @param paramInfo
	 * @return
	 */
	public List<Map> getDirectCmmt(ParamInfo paramInfo) {
		return getSqlMapClientTemplate().queryForList("direct.cmmtinfo",paramInfo);
	}

	/*
	 * 직접다운로드 분류목록 가져오기
	 * @param paramInfo
	 * @return
	 */
	public List getDirectClassListForWhereClause(ParamInfo paramInfo){
		return getSqlMapClientTemplate().queryForList("direct.directClassListForWhereClause", paramInfo);
	}

	public List<Map> getDirectObjVarPubInfo(ParamInfo paramInfo) throws Exception{
		return getSqlMapClientTemplate().queryForList("direct.directObjVarPubInfo",paramInfo);
	}

	// 항목리스트
	public List<Map> getDirectItmIdList(ParamInfo paramInfo) throws Exception{
		return getSqlMapClientTemplate().queryForList("direct.directItmIdList",paramInfo);
	}

	/*
	 * 직접다운로드 서비스용 USELOG에 쌓기위해 vw_cd로 connpath를 가져오는 부분
	 * @param paramInfo
	 * @return
	 */
	public String getConnPath(String vwCd){
		return (String)getSqlMapClientTemplate().queryForObject("direct.getConnPath", vwCd);
	}

	/*
	 * 직접다운로드 서비스용 USELOG에 쌓는 부분
	 * @param paramInfo
	 * @return
	 */
	public void insertUseLogForDirect(Map paramMap){
		getSqlMapClientTemplate().insert("log.insertUseLogForDirect", paramMap);
	}

	/***************************************************************************************************************
	 * 직접 파일다운로드 끝
	 ***************************************************************************************************************/

	/***************************************************************************************************************
	 * 김경호 시작
	 ***************************************************************************************************************/

	/*
	 * 최근시점 기준 스크랩시 기준 시점보다 최근 시점의 갯수 가져오기
	 * @param paramMap
	 */
	public int getTmpStatPrdListForScrap(Map paramMap){
		return (Integer)getSqlMapClientTemplate().queryForObject("scrap.getTmpStatPrdListForScrap", paramMap);
	}

	/***************************************************************************************************************
	 * 김경호 끝
	 ***************************************************************************************************************/

	/* AYS
	 * debug ="Y"
	 * @param debugMap
	 * @return
	 */
	public void resultTime(Map debugMap) {
		getSqlMapClientTemplate().insert("statinfo.resultTime", debugMap);
	}

	//만셀초과시 로그 적재
	public void insertOverCellLog(Map debugMap) {
		getSqlMapClientTemplate().insert("statinfo.insertOverCellLog", debugMap);
	}

	public int martCnt(ParamInfo paramInfo) {
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.martCnt", paramInfo);
	}

	/***************************************************************************************************************
	 * 정창호 시작
	 ***************************************************************************************************************/
	
	public List<EgovMap> getSelectRelInfo(ParamInfo paramInfo) {
	//	List<EgovMap> list = (List<EgovMap>)getSqlMapClientTemplate().queryForList("statinfo.selectRelInfo",paramMap);
		
		List<EgovMap> list = (List<EgovMap>)getSqlMapClientTemplate().queryForList("statinfo.selectRelInfo",paramInfo);
		return list;
	}
	
	public int getRelCount(ParamInfo paramInfo) {
		return (Integer)getSqlMapClientTemplate().queryForObject("statinfo.RelCnt",paramInfo);	
	}
	
	/***************************************************************************************************************
	 * 정창호 끝
	 ***************************************************************************************************************/
	
	//--------------------------------------------------------------------
	//2015.03.09 상속통계표  
	public EgovMap getStatInheritInfo(Map paramMap){
		return (EgovMap)getSqlMapClientTemplate().queryForObject("statinherit.getStatInheritInfo", paramMap);
	}
	
	public List<EgovMap> getSelectStatInfo(ParamInfo paramInfo) {		//				
		List<EgovMap> list = (List<EgovMap>)getSqlMapClientTemplate().queryForList("statinfo.selectStatInfo",paramInfo);
		return list;
	}
}