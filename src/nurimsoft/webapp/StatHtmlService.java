package nurimsoft.webapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nurimsoft.stat.info.AssayInfo;
import nurimsoft.stat.info.ClassInfo;
import nurimsoft.stat.info.ItemInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.info.RelationInfo;
import nurimsoft.stat.info.SelectAllInfo;
import nurimsoft.stat.info.SelectRangeInfo;
import nurimsoft.stat.info.StatInfo;
import nurimsoft.stat.manager.MakeDownLarge;
import nurimsoft.stat.manager.MakeMetaManager;
import nurimsoft.stat.manager.StatDataInfoManager;
import nurimsoft.stat.manager.StatInfoManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service("statHtmlService")
public class StatHtmlService {

	@Resource(name = "statHtmlDAO")
	private StatHtmlDAO statHtmlDAO;
	private StringUtil sutil;
	
	public String getRTITLEListId(ParamInfo paramInfo) throws Exception{

		String listId = paramInfo.getListId();
		if(listId == null || listId.trim().length() == 0){
			listId = StringUtils.defaultString(statHtmlDAO.getRTITLEListId(paramInfo), "");
		}

		return listId;
	}

	public StatInfo getStatInfo(ParamInfo paramInfo, ModelMap model) throws Exception{

		StatInfoManager statInfoManager = new StatInfoManager();
		StatInfo statInfo = statInfoManager.getStatInfo(paramInfo, statHtmlDAO, model);

		return statInfo;
	}

	public Object[] getHtmlData(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		Map debugMap = new HashMap();
		debugMap.put("startTime", System.currentTimeMillis());
		StatDataInfoManager pivotInfoManager = new StatDataInfoManager(paramInfo, statHtmlDAO, request);
		Object[] obj = pivotInfoManager.getHtmlData();

		if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){
			debugMap.put("dbUser", paramInfo.getDbUser());
			debugMap.put("orgId", paramInfo.getOrgId());
			debugMap.put("tblId", paramInfo.getTblId());
			debugMap.put("rowCnt", obj[2]);

		//	if(paramInfo.getIsFirst().equals("Y")){ //최초에만 쌓이도록 - 조회시마다 적재하도록 변경(주석처리)
			if(paramInfo.getDebug().equals("Y")){
				int martCnt = statHtmlDAO.martCnt(paramInfo);

				//if(martCnt > 0){ //마트인 경우만 적용하도록 - 모든 통계표에 대해서 적재하도록 변경(주석처리)
			    //debugMap.put("debugType", "B");							//구조변경전
			    //debugMap.put("debugType", "T");								//SQL튜닝적용
			    debugMap.put("debugType", "A");							//데이터마트 적용 후
			    debugMap.put("endTime",System.currentTimeMillis());
			    statHtmlDAO.resultTime(debugMap);
				//}
			}
		//	}

			//2014.10.16 만셀 초과 시 로그 적재
			int reqCellCnt = Integer.parseInt(paramInfo.getReqCellCnt());
			if(reqCellCnt > 10000){
				String serverUrl = PropertyManager.getInstance().getProperty("server.url");
				String systemTp = "O"; //외부망
				if(serverUrl.indexOf("kosis.kr") < 0){
					systemTp = "I"; //내부망
				}

				//debugMap 활용
				debugMap.put("systemTp", systemTp);
				debugMap.put("reqCellCnt", reqCellCnt);
				debugMap.put("usrId", paramInfo.getEmpId());
				statHtmlDAO.insertOverCellLog(debugMap);
			}

		}

		return obj;
	}

	public File getExcelData(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		StatDataInfoManager pivotInfoManager = new StatDataInfoManager(paramInfo, statHtmlDAO, request);
		
		File file = null; 
				
		if(paramInfo.getView().equals("xls")){
			file = pivotInfoManager.getExcelData("xls");
		}else{
			file = pivotInfoManager.getExcelData("xlsx");
		}

		return file;
	}

	public File getCsvTxtData(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		StatDataInfoManager pivotInfoManager = new StatDataInfoManager(paramInfo, statHtmlDAO, request);
		File file = pivotInfoManager.getCsvTxtData();

		return file;
	}

	public File getMetaData(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		MakeMetaManager metaManager = new MakeMetaManager(paramInfo, statHtmlDAO, request);
		File file = metaManager.getMetaData();

		return file;
	}

	public File getDirectMeta(ParamInfo paramInfo, HttpServletRequest request) throws Exception {
		MakeMetaManager metaManager = new MakeMetaManager(paramInfo, statHtmlDAO, request);
		File file = metaManager.getDirectMeta();

		return file;
	}

	public File getSdmxData(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		File file = null;

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir = "tmpFile";

		/*2014-11-19 통계표명 유지보수 1960 통계표 다운로드시 파일명 변경 - 이원영*/
		//String fileName = paramInfo.getOrgId() + "_" + paramInfo.getTblId();
		String fileName = "";

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			fileName = sutil.removeSpecialCharforFile(paramInfo.getTblEngNm());
		}else{
			fileName = sutil.removeSpecialCharforFile(paramInfo.getTblNm());
		}

		String dateString = StatPivotUtil.getDateString();

		Map paramMap = new HashMap();
		//조회조건 테이블에 insert
		paramMap = setSdmxCondition(paramInfo);

		String fileType = (String)paramMap.get("fileType");

		fileName += "_" + fileType + "_" + dateString + ".xml";
		file = new File(realPath + File.separator + fileDir, fileName);

		//SP_MAKE_SDMX_2013 호출하여 sdmx clob 데이터 생성
		String result = makeSdmxClob(paramMap);
		if(result == null || !result.equals("F")){
			throw new Exception("생성 시 오류가 발생");
		}
		//clob으로 부터 데이터 추출하여 파일로 생성
		String data = statHtmlDAO.selectSdmxClob(paramMap);

		BufferedWriter bw = null;

		try{
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			bw.write(data);
		}catch(Exception e){

		}finally{
			try{
				bw.close();
			}catch(Exception e){}
		}

		return file;
	}

	//SDMX 파일 생성 시 조회조건 테이블의 데이터를 SDMX 생성을 위한 조회조건 테이블로 insert(기존 모듈 재활용하기 위함 - 테이블 일부 수정)
	@Transactional
	@SuppressWarnings("unchecked")
	public Map setSdmxCondition(ParamInfo paramInfo) throws Exception{
		String sdmxInfo = PropertyManager.getInstance().getProperty("table.sdmx.info");
		String sdmxInfoMap = PropertyManager.getInstance().getProperty("table.sdmx.mapping");
		String sdmxSeq = PropertyManager.getInstance().getProperty("table.sdmx.seq");
		String sdmxMake = PropertyManager.getInstance().getProperty("sp.sdmx.make");

		String fileType = null;

		String downGridSdmxType = paramInfo.getDownGridSdmxType();

		if(downGridSdmxType != null && downGridSdmxType.equals("dsd")){
			fileType = "dsd";
		}else if(downGridSdmxType.equals("data")){
			fileType = paramInfo.getDownGridSdmxDataType();
		}

		Map paramMap = new HashMap();
		paramMap.put("sdmxInfo", sdmxInfo);
		paramMap.put("sdmxInfoMap", sdmxInfoMap);
		paramMap.put("sdmxSeq", sdmxSeq);
		paramMap.put("sdmxMake", sdmxMake);
		paramMap.put("dbUser", paramInfo.getDbUser());

		//1. REQ_SDMX_SEQ_2013로부터 seq 가져오기
		int reqSeq = statHtmlDAO.selectSdmxReqSeq(paramMap);

		//2. TN_REQ_SDMX_INFO_2013 테이블에 insert
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("sessionId", paramInfo.getSessionId());
		paramMap.put("reqSeq", reqSeq);
		paramMap.put("reqUsrId", paramInfo.getEmpId());
		paramMap.put("fileType", fileType);
		paramMap.put("reqIp", paramInfo.getIpAddr());

		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());

		statHtmlDAO.insertSdmxInfo(paramMap);

		//3. TN_REQ_SDMX_INFO_MAP_2013 분류와 항목 넣기
		statHtmlDAO.insertSdmxInfoMapObjItm(paramMap);

		//4. TN_REQ_SDMX_INFO_MAP_2013 시점 넣기
		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		statHtmlDAO.insertSdmxInfoMapPrd(paramMap, prd);

		return paramMap;
	}

	//SDMX 파일 생성 프로시저 호출(기존 모듈 재활용 - 프로시저 일부 수정됨)
	public String makeSdmxClob(Map paramMap) throws Exception{
		statHtmlDAO.callSdmxMake(paramMap);
		String result = (String)paramMap.get("result");

		if(result != null || result.equals("F")){
			statHtmlDAO.deleteSdmxInfoMap(paramMap);
		}

		return result;
	}

	/** 시점 AJAX
	 * @return */
	public List<EgovMap> getPeriodList(Map paramMap)throws Exception{
		return statHtmlDAO.selectPeriodInfoList(paramMap);
	}

	public List<EgovMap> getClassList(Map paramMap)throws Exception{
		return statHtmlDAO.selectHtmlItemList(paramMap);
	}

	public void deleteRequirement(ParamInfo paramInfo) {
		statHtmlDAO.deleteRequirement(paramInfo);
	}

	public int getRelCount(ParamInfo paramInfo) {
		return statHtmlDAO.getRelCount(paramInfo);
	}
	
	public void searchRequirementInsert(JSONArray jFieldArr, ParamInfo paramInfo) {
		 statHtmlDAO.searchRequirementInsert(jFieldArr, paramInfo);
	}

	/*
	 * 이규정 번호 클릭 시 주석가져오기
	 */
	public List getCmmtStr(Map paramMap)throws Exception{
		return statHtmlDAO.getCmmtStr(paramMap);
	}

	/*
	 * 이규정 번호 클릭 시 주석타이틀 가져오기
	 */
	public String getCmmtTitle(Map paramMap) throws Exception{
		return statHtmlDAO.getCmmtTitle(paramMap);
	}

	/*
	 * 이규정 로그 쌓기
	 */
	public void setLog(ParamInfo paramInfo) throws Exception{

		//service, 업무, 호스팅, 보급에 따라 처리방법이 다르다.
		String serverLocation = paramInfo.getServerLocation();
		String serverTypeOrigin = paramInfo.getServerTypeOrigin();

		// 2014.06.14 호스팅에서 dbUser가 NSI_IN_101(통계청)일경우 업무용으로 인식해서 로그쌓는 부분이 에러가남 - 김경호
		String dbUser = PropertyManager.getInstance().getProperty("server.dbuser");

		//2013.11.26
		//통계청, 호스팅
		if(serverLocation.equals("NSO")){
			
			if(serverTypeOrigin.indexOf("service") > -1){
				
				String connPath = paramInfo.getConnPath();
				
				/*2018.07.10 
					모니터링(connPath = Z6)에서 
					서비스용(ServerLocation = NSO :통계청에 설치된, ServerTypeOrigin = service :서비스용)을 조회할경우 
					TN_USELOG에 적재하지 않는다 - 최윤정 주무관
				 */
				/* 2018.08.23 위 내용의 작업을 할때 다운로드쪽은 검토하지 않아서 다운로드시 에러가 발생하여 조치함 */
				if(!connPath.equals("Z6")){
					//통계청 서비스용
					setLogForNsoService(paramInfo);
				}
			}
			//업무용, 호스팅용
			else{
				//업무용
				// dbUser가 NSI_IN_101 일 경우 호스팅일 수도 있으므로 프로퍼티의 dbUser까지 같이 비교 - 김경호
				if( paramInfo.getDbUser().replaceAll("\\.", "").equals("NSI_IN_101") && dbUser.equals("NSI_IN_101")){
					setLogForStatNso(paramInfo);
				}
				//호스팅용
				else{
					setLogForHostingNSply(paramInfo);
				}
			}
		}
		//보급용
		else{
			setLogForHostingNSply(paramInfo);
		}
	}

	//서비스용 로그 적재
	public void setLogForNsoService(ParamInfo paramInfo) throws Exception{
		//TN_USELOG

		//최초조회인지 여부 판단하여 최초인 경우 useseq를 이용하여 log_seq를 가져온다.
		String logSeq = null;
		String connPath = paramInfo.getConnPath();
		Map<String, String> paramMap = new HashMap<String, String>();

		if(paramInfo.getIsFirst().equals("Y")){
			paramMap.put("useSeq", PropertyManager.getInstance().getProperty("table.useseq"));
			logSeq = statHtmlDAO.getLogSeqForNsoService(paramMap);
		}else{
			//최초가 아닌경우 ParamInfo에서 가져온다.
			logSeq = paramInfo.getLogSeq();
		}

		//ParamInfo에 logSeq 셋팅
		paramInfo.setLogSeq(logSeq);

		//conn_path 체크
		paramMap.put("useLogCode", PropertyManager.getInstance().getProperty("table.uselogcode"));
		paramMap.put("connPath", connPath);
		int chkCnt = statHtmlDAO.getChkCnt(paramMap);

		//2014.02.06
		if(chkCnt == 0){
			String connPath2 = statHtmlDAO.getConnPath(paramMap);
			if(connPath2 != null){
				connPath = connPath2;
			}
		}

		//connPath 최총적으로 한번 더 체크해준다.null인 경우가 없도록 하기 위함
		if(connPath == null || connPath.trim().length() == 0){
			connPath = "Z1";
		}

		if(connPath.length() > 4){
			connPath = "Z1";
		}

		//2013.11.26
		String viewSe = "5";

		String viewKind = paramInfo.getViewKind();
		if(viewKind == null){
			viewKind = "";
		}

		// 2015.7.20 통계표 목록공개 여부 확인 - 9월 반영 예정으로 주석 처리
/*		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());

		int tblCnt;

		tblCnt = statHtmlDAO.openTblCnt(paramMap);
		if(tblCnt == 0){
			connPath = "Z10";
		}*/

		// 2015.09.06 관련통계표 팝업을 통해 조회된 통계표의 경우 rel_org_id, rel_tbl_id 원통계표 정보를 추가
		String relChkOrgId = paramInfo.getRelChkOrgId();
		String relChkTblId = paramInfo.getRelChkTblId();
		
		if(relChkOrgId == null || relChkOrgId.trim().length() == 0){
			relChkOrgId = "NULL";
		}
		
		if(relChkTblId == null || relChkTblId.trim().length() == 0){
			relChkTblId = "NULL";
		}
		
		paramMap.clear();
		paramMap.put("useLog", PropertyManager.getInstance().getProperty("table.uselog"));

		paramMap.put("logSeq", logSeq);
		paramMap.put("firstYn", paramInfo.getIsFirst());
		paramMap.put("ipAddr", paramInfo.getIpAddr());
		paramMap.put("isFrist", paramInfo.getIsFirst());
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("pub", paramInfo.getPubLog());
		paramMap.put("viewSe", viewSe);
		paramMap.put("connPath", connPath);
		paramMap.put("viewKind", viewKind);
		
		// 2015.09.06 관련통계표 조회시 정보
		paramMap.put("relChkOrgId", relChkOrgId);
		paramMap.put("relChkTblId", relChkTblId);

		String viewSubKind = paramInfo.getViewSubKind();
		if(viewSubKind == null){
			viewSubKind = "";
		}

		//2013.11.26 수정
		//분석 시 USE_ANAL 컬럼 적용
		/*
		 * 증감 : CHG
		 * 증감률 : CHG_RATE
		 * 구성비 : CMP_RATE
		 * 누계 : TOTL
		 * 누계구성비 : TOTL_CMP_RATE
		 * 기여도 : CHG_RATE_CO
		 * 기여율 : CHG_RATE_CO_R
		 */
		if(viewKind.equals("5")){
			String analType = paramInfo.getAnalType();
			String useAnal = null;

			if(analType == null){
				analType = "";
			}

			if(analType.equals("CHG")){
				useAnal = "2";
			}else if(analType.equals("CHG_RATE")){
				useAnal = "3";
			}else if(analType.equals("CMP_RATE")){
				useAnal = "4";
			}else if(analType.equals("TOTL")){
				useAnal = "5";
			}else if(analType.equals("TOTL_CMP_RATE")){
				useAnal = "6";
			}else if(analType.equals("CHG_RATE_CO")){
				useAnal = "7";
			}else if(analType.equals("CHG_RATE_CO_R")){
				useAnal = "8";
			}

			paramMap.put("useAnal", useAnal);
		}else{
			paramMap.put("useAnal", null);
		}

		//viewKind : 7 - 초과셀다운로드 인 경우에는 시점표두, 항목표두에 따라 statKind 값을 설정한다.
		//바로가기가 아닌 경우에만 STAT_KIND(통계표유형)의 데이터를 넣는다.
		String statKind = null;
		if(viewKind.equals("7")){
			String downLargeExprType = paramInfo.getDownLargeExprType();
			if(downLargeExprType == null){
				downLargeExprType = "1"; //Default 시점 표두
			}

			if(downLargeExprType.equals("1")){
				statKind = "3";
			}else{
				statKind = "2";
			}

		}else if(!viewKind.equals("6")){
			String tableType = paramInfo.getTableType();
			if(tableType == null){
				tableType = "";
			}

			if(tableType.equals("timeSeriesV")){
				statKind = "2";
			}else if(tableType.equals("timeSeriesH")){
				statKind = "3";
			}else if(tableType.equals("perYear")){
				statKind = "4";
			}else{
				statKind = "1";
			}

			//2014.01.10 pivot 적용 및 부가기능 추가 버튼 클릭 시 추가
			if( viewKind.equals("1")){
				if(StringUtils.defaultString(paramInfo.getUsePivot()).equals("Y") ){
					viewSubKind = "1_PIVOT";
					statKind = "1";
				}else if(StringUtils.defaultString(paramInfo.getUseAddFuncLog()).length() > 0){
					viewSubKind = paramInfo.getUseAddFuncLog();
				}
			}
		}

		paramMap.put("viewSubKind", viewSubKind);
		paramMap.put("statKind", statKind);

		paramMap.put("usrId", paramInfo.getEmpId());
		paramMap.put("usrNm", paramInfo.getEmpNm());
		paramMap.put("statId", paramInfo.getStatId());

		//바로가기가 아닌 경우에만 VIEW_PERIOD 데이터를 넣는다.
		//META인 경우에도 넣지 않는다.

		if(!viewKind.equals("6") && viewSubKind.indexOf("META") < 0){

			//주기별로 데이터를 넣어야 한다.
			String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
			StringBuffer tmpBuff = new StringBuffer();
			
			// 2016-01-11 null 값을 split 하려고 하다가 Exception 나는 경우를 S35 log에서 발견하여 수정 - 김경호
			if(prd != null && prd.length() > 0){
				String[] prdArr = prd.split("@");
				
				String prdSe = null;
				for(int i = 0; i < prdArr.length; i++){
					String[] tmpArr = prdArr[i].split(",");
					prdSe = tmpArr[0];
	
					if(i != 0){
						tmpBuff.append(",");
					}
	
					tmpBuff.append(prdSe + tmpArr[tmpArr.length - 1] + "_" + prdSe + tmpArr[1]);
				}
			}
			paramMap.put("viewPeriod", tmpBuff.toString());
			statHtmlDAO.insertUseLogForNsoService(paramMap);
		}else{
			paramMap.put("viewPeriod", null);
			statHtmlDAO.insertUseLogForNsoService(paramMap);
		}
	}

	//서비스용에서 비정상적인 경로를 통해 다운로드 하는 경우 로그를 적재. EKP.TH_USELOG_INTERCEPT
	public void setInterCeptLog(ParamInfo paramInfo, String referer) throws Exception{

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("useLogIntercept", PropertyManager.getInstance().getProperty("table.uselog.intercept"));
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("ipAddr", paramInfo.getIpAddr());
		if(referer != null){
			paramMap.put("beforePage", referer.substring(0, 1300));	//4000byte 컬럼 UTF-8인경우 3Byte이므로 1300자로 제한하여 넣는다.
		}

		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		if(prd != null && prd.length() > 0){
			String[] prdArr = prd.split("@");
			int prdArrLen = prdArr.length;

			StringBuffer prdSe = new StringBuffer();
			StringBuffer strtPrdDe = new StringBuffer();
			StringBuffer endPrdDe = new StringBuffer();

			for(int i = 0; i < prdArrLen; i++){
				String[] tmpArr = prdArr[i].split(",");

				prdSe.append(tmpArr[0]);
				strtPrdDe.append(tmpArr[tmpArr.length - 1]);
				endPrdDe.append(tmpArr[tmpArr.length - 1]);

				if(i != 0){
					prdSe.append(",");
					strtPrdDe.append(",");
					endPrdDe.append(",");
				}
			}

			paramMap.put("prdSe", prdSe.toString());
			paramMap.put("strtPrdDe", strtPrdDe.toString());
			paramMap.put("endPrdDe", endPrdDe.toString());
		}

		statHtmlDAO.insertUseLogInterceptForNsoService(paramMap);

	}

	//업무용 로그 적재
	public void setLogForStatNso(ParamInfo paramInfo) throws Exception{
		//웹표준 조회프로그램 - 기존 HTML과 동일하게 3번을 사용
		String viewSe = "3";

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("useLog", PropertyManager.getInstance().getProperty("table.uselog"));
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("empId", paramInfo.getEmpId());

		// 2015.08.06 관련통계표 팝업을 통해 조회된 통계표의 경우 rel_org_id, rel_tbl_id 원통계표 정보를 추가
		String relChkOrgId = paramInfo.getRelChkOrgId();
		String relChkTblId = paramInfo.getRelChkTblId();
		
		if(relChkOrgId == null || relChkOrgId.trim().length() == 0){
			relChkOrgId = "NULL";
		}
		
		if(relChkTblId == null || relChkTblId.trim().length() == 0){
			relChkTblId = "NULL";
		}
		
		paramMap.put("relChkOrgId", relChkOrgId);
		paramMap.put("relChkTblId", relChkTblId);
		
		if(paramInfo.getServerType().equals("stat_emp")){
			paramMap.put("pubSe", "1");
		}else{
			paramMap.put("pubSe", "2");
		}

		paramMap.put("viewSe", viewSe);
		paramMap.put("ipAdd", paramInfo.getIpAddr());
		paramMap.put("curEmpNm", "_" + StringUtils.defaultString(paramInfo.getEmpNm()));

		statHtmlDAO.insertUseLogForStatNso(paramMap);

	}

	//호스팅용, 보급용 로그 적재 - 서비스용의 olaplog 쌓기과 거의 유사 (setOlapLog)
	@SuppressWarnings("unchecked")
	public void setLogForHostingNSply(ParamInfo paramInfo) throws Exception{

		Map paramMap = new HashMap();

		//vwCd, listId이 null인 경우 NULL(서비스용: " " 과 다름)로 변환해야 함.TABLE의 해당 컬럼이 not null임.
		String vwCd = paramInfo.getVwCd();
		String listId = paramInfo.getListId();

		if(vwCd == null || vwCd.trim().length() == 0){
			vwCd = "NULL";
		}

		if(listId == null || listId.trim().length() == 0){
			listId = "NULL";
		}

		String viewKind = paramInfo.getViewKind();

		if(viewKind == null){
			viewKind = "";
		}

		Calendar today = Calendar.getInstance();
		int nYear = today.get(Calendar.YEAR);
		int	nMonth = today.get(Calendar.MONTH)+1;
		String strDay = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
		String columnName = null;

		if (viewKind.equals("2") || viewKind.equals("7")) {
			columnName = "d"+strDay;
		}else{
			columnName = "h"+strDay;
		}

		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("useLog", PropertyManager.getInstance().getProperty("table.uselog"));
		paramMap.put("columnName", columnName);
		paramMap.put("year", nYear);
		paramMap.put("month", nMonth);
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("vwCd", vwCd);
		paramMap.put("listId", listId);
		//v_type 협의 > 협의 결과 W로 결정(TO-DO DB 컬럼 주석에 W : 웹표준 추가해야 함)
		paramMap.put("vType", "W");

		// 2015.08.03 관련통계표 팝업을 통해 조회된 통계표의 경우 rel_org_id, rel_tbl_id 원통계표 정보를 추가
		String relChkOrgId = paramInfo.getRelChkOrgId();
		String relChkTblId = paramInfo.getRelChkTblId();
		
		if(relChkOrgId == null || relChkOrgId.trim().length() == 0){
			relChkOrgId = "NULL";
		}
		
		if(relChkTblId == null || relChkTblId.trim().length() == 0){
			relChkTblId = "NULL";
		}
		
		paramMap.put("relChkOrgId", relChkOrgId);
		paramMap.put("relChkTblId", relChkTblId);
		// ----------------------------------------------------------------------------------------------------
		
		int updateCnt = statHtmlDAO.updateUseLogForHostingNSply(paramMap);
		//update 하지 않았으면 데이터를 추가해야 함.
		if(updateCnt == 0){
			statHtmlDAO.insertUseLogForHostingNSply(paramMap);
		}
	}

	//서비스용의 olaplog 쌓기
	@SuppressWarnings("unchecked")
	public void setOlapLog(ParamInfo paramInfo) throws Exception{

		Map paramMap = new HashMap();

		//vwCd, listId이 null인 경우 " "(스페이스)로 변환해야 함.TABLE의 해당 컬럼이 not null임.
		String vwCd = paramInfo.getVwCd();
		String listId = paramInfo.getListId();

		if(vwCd == null || vwCd.trim().length() == 0){
			vwCd = " ";
		}

		if(listId == null || listId.trim().length() == 0){
			listId = " ";
		}

		String viewKind = paramInfo.getViewKind();

		if(viewKind == null){
			viewKind = "";
		}

		Calendar today = Calendar.getInstance();
		int nYear = today.get(Calendar.YEAR);
		int	nMonth = today.get(Calendar.MONTH)+1;
		String strDay = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
		String columnName = null;

		if (viewKind.equals("1") || viewKind.equals("5")) {
			columnName = "h"+strDay;
		} else if (viewKind.equals("2")) {
			columnName = "d"+strDay;
		}

		paramMap.put("olapLog", PropertyManager.getInstance().getProperty("table.olaplog"));
		paramMap.put("columnName", columnName);
		paramMap.put("year", nYear);
		paramMap.put("month", nMonth);
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("vwCd", vwCd);
		paramMap.put("listId", listId);
		//v_type 협의 > 협의 결과 W로 결정(TO-DO DB 컬럼 주석에 W : 웹표준 추가해야 함)
		paramMap.put("vType", "W");

		// 2015.09.17 관련통계표 팝업을 통해 조회된 통계표의 경우 rel_org_id, rel_tbl_id 원통계표 정보를 추가
		String relChkOrgId = paramInfo.getRelChkOrgId();
		String relChkTblId = paramInfo.getRelChkTblId();
		
		if(relChkOrgId == null || relChkOrgId.trim().length() == 0){
			relChkOrgId = "NULL";
		}
		
		if(relChkTblId == null || relChkTblId.trim().length() == 0){
			relChkTblId = "NULL";
		}
		
		paramMap.put("relChkOrgId", relChkOrgId);
		paramMap.put("relChkTblId", relChkTblId);
		// ----------------------------------------------------------------------------------------------------
		
		int updateCnt = statHtmlDAO.updateOlapLog(paramMap);
		//update 하지 않았으면 데이터를 추가해야 함.
		if(updateCnt == 0){
			statHtmlDAO.insertOlapLog(paramMap);
		}
	}

	public int checkEmpAuth(ParamInfo paramInfo) throws Exception{
		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("empId", paramInfo.getEmpId());
		paramMap.put("empAuth", PropertyManager.getInstance().getProperty("table.emp.auth"));

		return statHtmlDAO.selectCntForEmpAuth(paramMap);
	}

	//empNm 가져오기
	public String getEmpNm(ParamInfo paramInfo) throws Exception{

		String empNm = null;

		//service, 업무, 호스팅, 보급에 따라 처리방법이 다르다.
		String serverLocation = PropertyManager.getInstance().getProperty("server.location");
		String serverType = PropertyManager.getInstance().getProperty("server.type");

		//2013.11.26
		if(serverLocation.equals("NSO") && serverType.equals("service")){
			//통계청 서비스용
			empNm = statHtmlDAO.getEmpNmForNsoService(paramInfo.getEmpId());
		}else if(serverType.indexOf("stat") > -1){
			//업무용(호스팅)
			Map paramMap = new HashMap();

			paramMap.put("empId", paramInfo.getEmpId());
			paramMap.put("dbUser", paramInfo.getDbUser());
			empNm = statHtmlDAO.getEmpNmForNsoStat(paramMap);
		}

		return empNm;
	}

	/*
	 *	안영수 선택정보 전체보기
	 */
	public SelectAllInfo getSelectAllInfo(ParamInfo paramInfo, ModelMap model) throws Exception{

		PeriodInfo piInfo = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());		//시점정보 세팅을 위한 period
//		String classParam	=	paramInfo.getClassAllArr();
//		String[] tempClassArr = classParam.split(",");

		JSONArray jFieldArr = JSONArray.fromObject(StringEscapeUtils.unescapeHtml(paramInfo.getClassAllArr()));

		//시점 정보
		Map paramMap = new HashMap();
		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		String[] prdArr = null;
		if(prd != null){
			prdArr = prd.split("@");
		}

		paramMap.put("dataOpt", paramInfo.getDataOpt());
		paramMap.put("dbUser", paramInfo.getDbUser());	// 151202 남규옥 ::: 호스팅일때 선택정보 전체보기 클릭시 에러발생해서 추가

		String prdSe = null;
		String prdSeNm = null;
		Map resultMap = new HashMap();

		SelectAllInfo selectAllInfo = new SelectAllInfo();
		//시점
//		List<EgovMap> timeAllArr = new ArrayList();
		List timeAllList = new ArrayList();

		int prdArrLength = (prdArr != null) ? prdArr.length : 0;
		for(int i = 0; i < prdArrLength; i++){
			String[] tmpArr = prdArr[i].split(",");
				prdSe = tmpArr[0];
				paramMap.put("prdSe", prdSe);

				if(prdSe.endsWith("F")){
					String prdDetail = statHtmlDAO.selectPrdDetail(paramInfo);	//저장된 주기 상세주기
					piInfo.setIrregular(prdDetail);
					prdSeNm = piInfo.getNameF();
				}else{
					resultMap = statHtmlDAO.getPrdNm(paramMap);
					prdSeNm = (String)resultMap.get("prdNm");
				}

				for(int j=1; j<tmpArr.length; j++){
	//				EgovMap tempMap = new EgovMap();
					String tempStr = StatPivotUtil.generatePrdDe(tmpArr[j], prdSe);

					Map tempMap = new HashMap();
					tempMap.put("prdSeNm", prdSeNm);
					tempMap.put("prdTime", tempStr);
					timeAllList.add(tempMap);
				}
		}

		//항목
		List<EgovMap> itemAllArr = new ArrayList();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());

		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());

		List itemAllList = statHtmlDAO.getDimensionItemList(paramMap, "item");

		//분류
		List classList = new ArrayList();
		for(int k=0; k < jFieldArr.size(); k++){
//			int tempCnt = k;
//			tempCnt++;
//			paramMap.put("objVarId",tempClassArr[k]);
//			paramMap.put("targetId", "OV_L" + tempCnt + "_ID");			//분류순서 세팅
			JSONObject jObj = (JSONObject) jFieldArr.get(k);
			paramMap.put("objVarId",jObj.get("objVarId"));
			paramMap.put("targetId", "OV_L"+jObj.get("ovlSn")+"_ID");

			ClassInfo clInfo = new ClassInfo();
			Map classMap = statHtmlDAO.getDimensionInfo(paramMap);
			List selectClass = statHtmlDAO.getDimensionItemList(paramMap, "class");

			clInfo.setClassEngNm((String) classMap.get("SCR_ENG"));
	//		clInfo.setClassNm((String) classMap.get("SCR_KOR"));
			clInfo.setClassNm(StatPivotUtil.dataOptViewNm2(classMap,"object",paramInfo.getDataOpt()));
			clInfo.setClassId((String) classMap.get("OBJ_VAR_ID"));
			clInfo.setClassAllList(StatPivotUtil.listViewNm2(selectClass,"list",paramInfo.getDataOpt()));
			classList.add(clInfo);
		}

		selectAllInfo.setSelectItmList(StatPivotUtil.listViewNm2(itemAllList,"list",paramInfo.getDataOpt()));
		selectAllInfo.setSelectTimeList(timeAllList);
		selectAllInfo.setClassInfoList(classList);

		return selectAllInfo;
	}

	/* 안영수 조회범위 상세설정
	 *
	 */
	@SuppressWarnings("unchecked")
	public SelectRangeInfo getRangeInfo(ParamInfo paramInfo, ModelMap model) throws Exception{

		//1.기준 항목 분류 시점
		JSONArray jFieldArr = JSONArray.fromObject(StringEscapeUtils.unescapeHtml(paramInfo.getClassSet()));

//		String classParam	=	paramInfo.getClassAllArr();					//nso.jsp에서 세팅된 탭메뉴정보 코드
//		String[] tempClassArr = classParam.split(",");

		//항목 세팅
		Map paramMap = new HashMap();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("objVarId","13999001");
		List<EgovMap> HtmlItemList = (List<EgovMap>)statHtmlDAO.selectHtmlItemList(paramMap);

		ItemInfo imInfo = new ItemInfo();
		imInfo.setItmList(StatPivotUtil.listViewNm(HtmlItemList,"list",paramInfo.getDataOpt()));							//imInfo.setItmList로 바꾸기
		imInfo.setItmCnt(HtmlItemList.size());

		//분류세팅
		List classList = new ArrayList();
		for(int k=0; k < jFieldArr.size(); k++){
			ClassInfo clInfo = new ClassInfo();
			JSONObject jObj = (JSONObject) jFieldArr.get(k);
//			paramMap.put("objVarId",tempClassArr[k]);
			paramMap.put("objVarId",jObj.get("objVarId"));

			String visible = (String) jObj.get("visible");

			Map classMap = statHtmlDAO.selectRangeClassInfo(paramMap);
			//searchCondition =j,D 값 없음
			//e나라 지표 지역별 통계표//
			if(paramMap.get("objVarId").equals(StringUtils.defaultString(paramInfo.getObj_var_id(), "")) ){
				paramMap.put("itmid",paramInfo.getItm_id());
			}else{
				paramMap.put("itmid","");
			}
			paramMap.put("totIgnrAt",classMap.get("totIgnrAt"));							//함수값여부
			List<EgovMap> ClassList = (List<EgovMap>) statHtmlDAO.selectHtmlItemList(paramMap);

			int maxLevel = statHtmlDAO.selectClassMaxLevel(paramMap);

			clInfo.setClassEngNm((String) classMap.get("scrEng"));
			clInfo.setClassNm(StatPivotUtil.dataOptViewNm(classMap,"object",paramInfo.getDataOpt()));
			clInfo.setClassId((String) classMap.get("objVarId"));
			clInfo.setDepthLvl(Integer.toString(maxLevel));
			clInfo.setClassAllList(StatPivotUtil.listViewNm(ClassList,"list",paramInfo.getDataOpt()));

			if(visible.equals("true")){
				clInfo.setVisible(true);
			}else{
				clInfo.setVisible(false);
			}
			classList.add(clInfo);
		}

		//시점 세팅 고
		List tabTimeArr = new ArrayList();			//selectRangeInfo.tabTimeList 세팅하자 TODO.. 언어별로~~~부정기일때 표현방식도~
		String[] tempTimeArr = paramInfo.getPeriodStr().split("#");

		PeriodInfo piInfo = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());

		paramMap.put("assayYn", "Y");			//분석과 상세설정에서만 사용하는 시점 구분
	//	char chTempPeriod;
		String prdSeNm = null;
		Map resultMap = new HashMap();
		for(int ii=0; ii < tempTimeArr.length; ii++){
			String tempPeriod = tempTimeArr[ii];
	//		chTempPeriod = tempPeriod.charAt(0);
			Map tabMap = new HashMap();
			paramMap.put("dbUser", paramInfo.getDbUser());
			paramMap.put("prdSe",tempPeriod);
			paramMap.put("dataOpt", paramInfo.getDataOpt());
			if(tempPeriod.endsWith("F")){
				String prdDetail = statHtmlDAO.selectPrdDetail(paramInfo);
				piInfo.setIrregular(prdDetail);
				prdSeNm = piInfo.getNameF();
			}else{
				resultMap = statHtmlDAO.getPrdNm(paramMap);
				prdSeNm = (String)resultMap.get("prdNm");
			}
			tabMap.put("prdSeNm",prdSeNm);						//탭메뉴 영문,한글
			tabMap.put("prdSe",tempPeriod);
			tabTimeArr.add(tabMap);

			//2015.06.05 상속통계표일때 주기 시점은 부모통계표를 바라봄 
			if( "Y".equals(paramInfo.getInheritYn()) ){
				paramMap.put("inheritYn", 	paramInfo.getInheritYn());
				paramMap.put("originOrgId", paramInfo.getOriginOrgId());
				paramMap.put("originTblId", paramInfo.getOriginTblId());
			}

			/* 
			 20.04.09 업무용에서 담당자가 st 파라미터를 이용하여 서비스용으로 조회할때 원래 serverType은 관리자 였으므로 기간보안 체크안함
			(손상호 주무관의 요청에 따라 [미리보는KOSIS]에서는 기간보안 체크안함.
			 */
			paramMap.put("serverTypeOrigin",paramInfo.getServerTypeOrigin());

			List<EgovMap> HtmlPeriodList = (List<EgovMap>)statHtmlDAO.selectPeriodInfoList(paramMap);		//주기 조회
			Collections.reverse(HtmlPeriodList);															//역순 세팅

			//화면에서 주기별 시점 세팅해주자.. 화면에서 해줘도 되는데...머가 다 빠를까? 화면에서 필요한건 prdSe/prdDe가필요한데 일단 세팅
			List SetPeriodList = new ArrayList();

			for(EgovMap periodInfo:HtmlPeriodList){
			String tempStr = StatPivotUtil.generatePrdDe((String) periodInfo.get("prdDe"), tempPeriod);
				Map tempMap = new HashMap();
				tempMap.put("prdSe",tempPeriod);
				tempMap.put("prdDe",(String) periodInfo.get("prdDe"));
				tempMap.put("prdTime",tempStr);

				SetPeriodList.add(tempMap);
			}
			String startStr="";
			String endStr ="";
			if(Character.toString(piInfo.D).equals(tempPeriod)){
				piInfo.setListD(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartD(startStr);
				piInfo.setEndD(endStr);
			}else if(Character.toString(piInfo.T).equals(tempPeriod)){
				piInfo.setListT(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartT(startStr);
				piInfo.setEndT(endStr);
			}else if(Character.toString(piInfo.M).equals(tempPeriod)){
				piInfo.setListM(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartM(startStr);
				piInfo.setEndM(endStr);
			}else if(Character.toString(piInfo.B).equals(tempPeriod)){
				piInfo.setListB(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartB(startStr);
				piInfo.setEndB(endStr);
			}else if(Character.toString(piInfo.Q).equals(tempPeriod)){
				piInfo.setListQ(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartQ(startStr);
				piInfo.setEndQ(endStr);
			}else if(Character.toString(piInfo.H).equals(tempPeriod)){
				piInfo.setListH(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartH(startStr);
				piInfo.setEndH(endStr);
			}else if(Character.toString(piInfo.Y).equals(tempPeriod)){
				piInfo.setListY(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartY(startStr);
				piInfo.setEndY(endStr);
			}else if(Character.toString(piInfo.F).equals(tempPeriod)){
				piInfo.setListF(SetPeriodList);
				startStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(HtmlPeriodList.size()-1).get("prdDe"), tempPeriod);
				endStr = StatPivotUtil.generatePrdDe((String) HtmlPeriodList.get(0).get("prdDe"), tempPeriod);
				piInfo.setStartF(startStr);
				piInfo.setEndF(endStr);
			}
		}
		/*
		System.out.println("setListD====>"+piInfo.getListD());
		System.out.println("setListT====>"+piInfo.getListT());
		System.out.println("setListM====>"+piInfo.getListH());
		System.out.println("setListB====>"+piInfo.getListB());
		System.out.println("setListQ====>"+piInfo.getListQ());
		System.out.println("setListH====>"+piInfo.getListH());
		System.out.println("setListY====>"+piInfo.getListY());
		System.out.println("setListF====>"+piInfo.getListF());
		*/

		SelectRangeInfo rangeInfo = new SelectRangeInfo();
		rangeInfo.setItemInfo(imInfo);
		rangeInfo.setClassInfoList(classList);

		//2. 사용자 선택정보
		//항목
		List<EgovMap> itemAllArr = new ArrayList();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());
		paramMap.put("objVarId","13999001");

		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());

		List<EgovMap> itemAllList = (List<EgovMap>)statHtmlDAO.selectRangeItemList(paramMap);

		//분류
		List selectClassList = new ArrayList();
		for(int k=0; k < jFieldArr.size(); k++){
//			int tempCnt = k;
//			tempCnt++;
//			paramMap.put("objVarId",tempClassArr[k]);
//			paramMap.put("targetId", "OV_L" + tempCnt + "_ID");			//분류순서 세팅
//
			JSONObject jObj = (JSONObject) jFieldArr.get(k);
			paramMap.put("objVarId",jObj.get("objVarId"));
			paramMap.put("targetId", "OV_L"+jObj.get("ovlSn")+"_ID");


			String visible = (String) jObj.get("visible");

			ClassInfo clInfo = new ClassInfo();
			Map classMap = statHtmlDAO.getDimensionInfo(paramMap);

			//searchCondition =j,D 값 없음
			//e나라 지표 지역별 통계표//
			if(paramMap.get("objVarId").equals(StringUtils.defaultString(paramInfo.getObj_var_id(), "")) ){
				paramMap.put("itmid",paramInfo.getItm_id());
			}else{
				paramMap.put("itmid","");
			}
			paramMap.put("totIgnrAt",classMap.get("TOT_IGNR_AT"));
			//분류...기존의 rownum 알고있으면 화면 수정거의 안해도 되는데......쿼리에서 기존 rowNum정보 (+)조인으로 가지고 있어야 될듯.ㅋ

			List<EgovMap> selectClass = (List<EgovMap>)statHtmlDAO.selectRangeClassList(paramMap);

			clInfo.setClassEngNm((String) classMap.get("SCR_ENG"));
			clInfo.setClassNm(StatPivotUtil.dataOptViewNm2(classMap,"object",paramInfo.getDataOpt()));
			clInfo.setClassId((String) classMap.get("OBJ_VAR_ID"));
			clInfo.setClassAllList(StatPivotUtil.listViewNm(selectClass,"list",paramInfo.getDataOpt()));
			//2013.3.09 일괄설정에서 view구조 변경  nso.jsp에서 분류visible=true 상태만 세팅하지 말고 전체 분류를 paramInfo.getClassSet에 세팅하고 visible 상태값도 같이 넘긴다.
			if(visible.equals("true")){
				clInfo.setVisible(true);
			}else{
				clInfo.setVisible(false);
			}
			selectClassList.add(clInfo);
		}

		//시점
		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		String [] prdArr = null;
		if(prd != null){
			prdArr = prd.split("@");
		}

		String prdSe = null;

		List timeTempD = new ArrayList();
		List timeTempT = new ArrayList();
		List timeTempM = new ArrayList();
		List timeTempB = new ArrayList();
		List timeTempQ = new ArrayList();
		List timeTempH = new ArrayList();
		List timeTempY = new ArrayList();
		List timeTempF = new ArrayList();

		int prdArrLength = (prdArr != null) ? prdArr.length : 0;
		for(int i = 0; i < prdArrLength; i++){
			String[] tmpArr = prdArr[i].split(",");
			prdSe = tmpArr[0];

			for(int j=1; j<tmpArr.length; j++){
				String tempStr = StatPivotUtil.generatePrdDe(tmpArr[j], prdSe);

				Map tempMap = new HashMap();
				tempMap.put("prdSe", prdSe);
				tempMap.put("prdDe", tmpArr[j]);
				tempMap.put("prdTime", tempStr);

				if(Character.toString(piInfo.D).equals(prdSe)){
					timeTempD.add(tempMap);
				}else if(Character.toString(piInfo.T).equals(prdSe)){
					timeTempT.add(tempMap);
				}else if(Character.toString(piInfo.M).equals(prdSe)){
					timeTempM.add(tempMap);
				}else if(Character.toString(piInfo.B).equals(prdSe)){
					timeTempB.add(tempMap);
				}else if(Character.toString(piInfo.Q).equals(prdSe)){
					timeTempQ.add(tempMap);
				}else if(Character.toString(piInfo.H).equals(prdSe)){
					timeTempH.add(tempMap);
				}else if(Character.toString(piInfo.Y).equals(prdSe)){
					timeTempY.add(tempMap);
				}else if(Character.toString(piInfo.F).equals(prdSe)){
					timeTempF.add(tempMap);
				}

			}
		}

		piInfo.setDefaultListD(timeTempD);
		piInfo.setDefaultListT(timeTempT);
		piInfo.setDefaultListM(timeTempM);
		piInfo.setDefaultListB(timeTempB);
		piInfo.setDefaultListQ(timeTempQ);
		piInfo.setDefaultListH(timeTempH);
		piInfo.setDefaultListY(timeTempY);
		piInfo.setDefaultListF(timeTempF);

		rangeInfo.setSelectItmList(StatPivotUtil.listViewNm(itemAllList,"list",paramInfo.getDataOpt()));
		rangeInfo.setSelectClassList(selectClassList);
		rangeInfo.setTabTimeList(tabTimeArr);				//시점 탭
		rangeInfo.setPeriodInfo(piInfo);

		return rangeInfo;
	}

	/* 안영수 분석
	 *
	 */
	public AssayInfo getAssayInfo(ParamInfo paramInfo, ModelMap model) throws Exception{

		String funcPrdSe =	paramInfo.getFuncPrdSe();				//nso.jsp 시점 탭에서 선택한 주기

//		String classParam	=	paramInfo.getClassAllArr();
//		String[] tempClassArr = classParam.split(",");

		JSONArray jFieldArr = JSONArray.fromObject(StringEscapeUtils.unescapeHtml(paramInfo.getClassAllArr()));

		Map paramMap = new HashMap();
		AssayInfo assayInfo = new AssayInfo();

		//항목
		List<EgovMap> itemAllArr = new ArrayList();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());

		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());

		List itemAllList = statHtmlDAO.getDimensionItemList(paramMap, "item");

		//분류
		List classList = new ArrayList();
		for(int k=0; k < jFieldArr.size(); k++){
//			int tempCnt = k;
//			tempCnt++;
//			paramMap.put("objVarId",tempClassArr[k]);
//			paramMap.put("targetId", "OV_L" + tempCnt + "_ID");			//분류순서 세팅

			JSONObject jObj = (JSONObject) jFieldArr.get(k);
			paramMap.put("objVarId",jObj.get("objVarId"));
			paramMap.put("targetId", "OV_L"+jObj.get("ovlSn")+"_ID");

			ClassInfo clInfo = new ClassInfo();
			Map classMap = statHtmlDAO.getDimensionInfo(paramMap);
			List selectClass = statHtmlDAO.getDimensionItemList(paramMap, "class");

			clInfo.setClassEngNm((String) classMap.get("SCR_ENG"));
//			clInfo.setClassNm((String) classMap.get("SCR_KOR"));
			clInfo.setClassNm(StatPivotUtil.dataOptViewNm2(classMap,"object",paramInfo.getDataOpt()));
			clInfo.setClassId((String) classMap.get("OBJ_VAR_ID"));
			clInfo.setClassAllList(StatPivotUtil.listViewNm2(selectClass,"list",paramInfo.getDataOpt()));
		//	clInfo.setClassAllList(selectClass);
			classList.add(clInfo);
		}

		/* 
		 20.04.09 업무용에서 담당자가 st 파라미터를 이용하여 서비스용으로 조회할때 원래 serverType은 관리자 였으므로 기간보안 체크안함
		(손상호 주무관의 요청에 따라 [미리보는KOSIS]에서는 기간보안 체크안함.
		 */
		paramMap.put("serverTypeOrigin",paramInfo.getServerTypeOrigin());
		
		//화면에서 넘어온 시점 정보
		paramMap.put("assayYn", "Y");			//분석에서만 사용하는 시점 구분
		paramMap.put("prdSe",paramInfo.getFuncPrdSe());
		paramMap.put("dbUser", paramInfo.getDbUser());
		List<EgovMap> timeList = (List<EgovMap>)statHtmlDAO.selectPeriodInfoList(paramMap);

		List assayTimeList = new ArrayList();

		//주기별 출력형태 세팅//
		for(EgovMap timeInfo:timeList){
			String prdSe = (String) timeInfo.get("prdSe");
			String prdDe = (String) timeInfo.get("prdDe");
			String tempStr = StatPivotUtil.generatePrdDe(prdDe, prdSe);
			//분석 analTime 파라미터는 가공전 시점정보임//
			Map tempMap = new HashMap();
			tempMap.put("prdSe", prdSe);
			tempMap.put("prdDe", prdDe);
			tempMap.put("viewPrdDe",tempStr);
			assayTimeList.add(tempMap);
		}

		Collections.reverse(assayTimeList);

		assayInfo.setAssayTimeList(assayTimeList);  //시점
		assayInfo.setAssayItmList(StatPivotUtil.listViewNm2(itemAllList,"list",paramInfo.getDataOpt()));		//항목
		assayInfo.setClassInfoList(classList);		//분류

		//분석종류리스트
		String excCode = statHtmlDAO.selectExcCode(paramInfo);
		if(excCode == null){
			// 2015.4.17 초기설정 AL-> NX 변경 이원영주무관요청
			excCode ="NX";
		}
		paramMap.put("excCode",excCode);

		List<EgovMap> assayTypeList = (List<EgovMap>)statHtmlDAO.selectAssayTypeList(paramMap);
		List<EgovMap> compareTypeList = (List<EgovMap>)statHtmlDAO.selectCompareTypeList(paramMap);

		assayInfo.setAssayTypeList(assayTypeList);
		assayInfo.setCompareTypeList(compareTypeList);

		// 2015.10.28 분석종류의 주석 보여주기 위해 추가
		if(paramInfo.getServerLocation().equals("NSO") && paramInfo.getServerTypeOrigin().indexOf("service") > -1){
			List<EgovMap> assayTypeCmmtList = (List<EgovMap>)statHtmlDAO.assayTypeCmmtList(paramMap);
			assayInfo.setAssayTypeCmmtList(assayTypeCmmtList);
		}

		return assayInfo;
	}

	
	/* 정창호 관련통계표
	 * 
	 */
	public RelationInfo getRelationInfo(ParamInfo paramInfo) throws Exception{
	
		RelationInfo relationInfo = new RelationInfo();
		
		List<EgovMap> relationInfoList = (List<EgovMap>)statHtmlDAO.getSelectRelInfo(paramInfo);
		
		relationInfo.setRelationList(relationInfoList);
		
		return relationInfo;
	}
	
	/*
	 *	김정현 파일다운로드
	 */
	public String getDirectMakeExcel(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		MakeDownLarge downLarge = new MakeDownLarge(paramInfo, statHtmlDAO, request);
		String result = downLarge.getDirectMakeExcel();

		return result;
	}

	public String getDirectMakeCSV(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		MakeDownLarge downLarge = new MakeDownLarge(paramInfo, statHtmlDAO, request);
		String result = downLarge.getDirectMakeCSV();

		return result;
	}

	public String getMakeExcel(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		MakeDownLarge downLarge = new MakeDownLarge(paramInfo, statHtmlDAO, request);
		String result = downLarge.getMakeExcel();

		return result;
	}

	public String getMakeCSV(ParamInfo paramInfo, HttpServletRequest request) throws Exception{

		MakeDownLarge downLarge = new MakeDownLarge(paramInfo, statHtmlDAO, request);
		String result = downLarge.getMakeCSV();

		return result;
	}

	public  String getStatHtmlDefaultCondition(ParamInfo paramInfo) throws Exception{
		return statHtmlDAO.getStatHtmlDefaultCondition(paramInfo);
	}

	public List getMyscrapFolder_1(ParamInfo paramInfo) throws Exception{
		return statHtmlDAO.selectMyscrapFolder_1(paramInfo);
	}

	public List getMyscrapFolder_2(ParamInfo paramInfo) throws Exception{
		return statHtmlDAO.selectMyscrapFolder_2(paramInfo);
	}

	//스크랩 저장
	@Transactional
	public void saveScrapInfo(ParamInfo paramInfo) throws Exception{
		String[] colAxisArr = null;
		String[] rowAxisArr = null;

		if(paramInfo.getColAxis() != null && paramInfo.getColAxis().trim().length() != 0){
			colAxisArr = paramInfo.getColAxis().split(",");
		}

		if(paramInfo.getRowAxis() != null && paramInfo.getRowAxis().trim().length() != 0){
			rowAxisArr = paramInfo.getRowAxis().split(",");
		}

		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("empId", paramInfo.getEmpId());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("tnStblScr", PropertyManager.getInstance().getProperty("table.scrap.scr"));
		paramMap.put("tnStblScrItm", PropertyManager.getInstance().getProperty("table.scrap.scr.itm"));
		paramMap.put("folderId", paramInfo.getFolderId());
		paramMap.put("sessionId", paramInfo.getSessionId());

		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());

		//분석 적용 - 2014.02.06 (기존 소스 그대로 적용)
		String doAnal = StringUtils.defaultString(paramInfo.getDoAnal());
		String func_param = "";

		if(doAnal.equals("Y")){
			String FUNC_CODE = paramInfo.getAnalType();
			String FUNC_PRD_CODE = paramInfo.getAnalCmpr();
			String FUNC_STD_PRD = paramInfo.getAnalTime();

			String analClass = StringUtils.defaultString(paramInfo.getAnalClass());
			String analItem = StringUtils.defaultString(paramInfo.getAnalItem());

			String[] analClassArr = analClass.split(",");
			String[] analItemArr = analItem.split(",");

			String DimMsr_code = "1";
			String temp_cmp_str = "";

			//ITEM을 13999001 로 변경
			if(analClassArr != null && analClassArr.length > 0){
				if("ITEM".equals(analClassArr[0])){
					analClassArr[0] = "13999001";
					DimMsr_code = "2";
				}
			}

			func_param = FUNC_CODE + ":" + FUNC_PRD_CODE;

			// 기준시점비  - 기준시점 parameter 추가
			if (FUNC_PRD_CODE.equals("ONE_Y") || FUNC_PRD_CODE.equals("ONE_M") || FUNC_PRD_CODE.equals("ONE_Q") || FUNC_PRD_CODE.equals("ONE_H") || FUNC_PRD_CODE.indexOf("ONE_F") > -1) {
				func_param = func_param + FUNC_STD_PRD;
			}

			// 증감기여도, 증감기여율 - 기준분류그룹코드, 기준분류코드 추가
			if (FUNC_CODE.equals("CHG_RATE_CO") || FUNC_CODE.equals("CHG_RATE_CO_R")) {
				func_param = func_param + ":" + analClassArr[0] + ":" + analItemArr[0];
			}

			// 구성비 - 기준분류코드 추가
			if (FUNC_CODE.equals("CMP_RATE") || FUNC_CODE.equals("TOTL_CMP_RATE")) {

				func_param = func_param + ":" + DimMsr_code;
				if (DimMsr_code.equals("2")) {
					temp_cmp_str = analClassArr[0] + "_" + analItemArr[0];
				} else {
					//4개다...SIGA와 동일하게 처리하도록 그대로 사용
					temp_cmp_str = analClassArr[0] + "_" + analItemArr[0] + ":"
							+ ( (analClassArr.length > 1) ? analClassArr[1] : "" ) + "_" + ( (analItemArr.length > 1) ? analItemArr[1] : "" ) + ":"
							+ ( (analClassArr.length > 2) ? analClassArr[2] : "" ) + "_" + ( (analItemArr.length > 2) ? analItemArr[2] : "" ) + ":"
							+ ( (analClassArr.length > 3) ? analClassArr[3] : "" ) + "_" + ( (analItemArr.length > 3) ? analItemArr[3] : "" ) + ":"
							+ ( (analClassArr.length > 4) ? analClassArr[4] : "" ) + "_" + ( (analItemArr.length > 4) ? analItemArr[4] : "" ) + ":"
							+ ( (analClassArr.length > 5) ? analClassArr[5] : "" ) + "_" + ( (analItemArr.length > 5) ? analItemArr[5] : "" ) + ":"
							+ ( (analClassArr.length > 6) ? analClassArr[6] : "" ) + "_" + ( (analItemArr.length > 6) ? analItemArr[6] : "" ) + ":"
							+ ( (analClassArr.length > 7) ? analClassArr[7] : "" ) + "_" + ( (analItemArr.length > 7) ? analItemArr[7] : "" );
				}
				func_param = func_param + ":" + temp_cmp_str;
			}

			paramMap.put("func", func_param);
		}

		//SCR_ID 가져오기
		String scrId = statHtmlDAO.selectScrId(paramMap);
		paramMap.put("scrId", scrId);
		paramMap.put("tblNmAlias", paramInfo.getMyscrabTblNm().trim());

		//기존 데이터 삽입규칙에 따라 수행한다.
		int arrySn = 3;
		//1. 데이타
		statHtmlDAO.insertScrapData(paramMap);
		//2. 가중치
		statHtmlDAO.insertScrapWgtCo(paramMap);
		//3. 함수값
		statHtmlDAO.insertScrapFunc(paramMap);


		//항목 체크하면서 년월표의 경우 TIME_MQ는 제거한다.
		List<String> rowList = new ArrayList();
		List<String> colList = new ArrayList();
		boolean isExistItem = false;	//항목 존재여부
		if(rowAxisArr != null){	//표측 1212511
			String rowAxis = null;
			for(int i = 0; i < rowAxisArr.length; i++){
				rowAxis = rowAxisArr[i];
				if(rowAxis.startsWith("ITEM")){
					isExistItem = true;
					rowList.add("ITEM");
				}else if(rowAxis.startsWith("TIME")){
					if(rowAxis.equals("TIME") || rowAxis.equals("TIME_YEAR")){	//년월표의 경우 년을 기준으로 삼는다.
						rowList.add("TIME");
					}
				}else{
					rowList.add(rowAxis);
				}
			}
		}

		if(colAxisArr != null){	//표두 1212510
			String colAxis = null;
			for(int i = 0; i < colAxisArr.length; i++){
				colAxis = colAxisArr[i];
				if(colAxis.startsWith("ITEM")){
					isExistItem = true;
					colList.add("ITEM");
				}else if(colAxis.startsWith("TIME")){
					if(colAxis.equals("TIME") || colAxis.equals("TIME_YEAR")){	//년월표의 경우 년을 기준으로 삼는다.
						colList.add("TIME");
					}
				}else{
					colList.add(colAxis);
				}
			}
		}

		//4. 항목이 없는 경우 처리
		if(!isExistItem){
			paramMap.put("arrySe", "0");
			paramMap.put("arrySn", 4);
			statHtmlDAO.insertScrapItem(paramMap);
			arrySn = 4;
		}

		//표측과 표두 데이터 넣기
		paramMap.put("arrySe", "1212511");	//표측
		arrySn = insertScrapTnStblScr(rowList, paramMap, paramInfo, arrySn);

		paramMap.put("arrySe", "1212510");	//표두
		arrySn = insertScrapTnStblScr(colList, paramMap, paramInfo, arrySn);

		//#TN_STBL_SCR테이블에 데이터 넣기
		statHtmlDAO.insertScrapTnStblScrItm(paramMap);

		/*
		2018.12.04 파라미터 등록(통계기획과 과장의 요구에 TN_STBL_SCR 테이블에 같이 등록하되 일단은 원자료 함께보기만 가능하도록...)
		하려고 했으나 기준자료 선택안함이 스크랩이 안되는 문제가 있음. 개발팀에서 미완성인채로 넘어왔음...
		기존 FUNC 필드에 들어가는 분류를 안넣어서 수정해 보려고 했으나 고쳐야할 부분이 많이 보이고 그로 인해 생길 오류등에 대한 부담으로 그냥 파라미터를 추가함
		*/
		if(paramInfo.getOriginData() != null){
			String paramWeb = "";
			if(paramInfo.getOriginData().equals("Y")){
				paramWeb = "originData,Y,@";
			}
			if(paramInfo.getNoSelect().equals("noSelect")){
				paramWeb += "noSelect,noSelect@";
			}		
			paramMap.put("paramWeb", paramWeb);
			paramMap.put("arrySn", ++arrySn);
			statHtmlDAO.insertScrapParams(paramMap);
		}
	}

	//TN_STBL_SCR insert
	public int insertScrapTnStblScr(List<String> list, Map paramMap, ParamInfo paramInfo, int arrySn) throws Exception{

		Map classMap = null;
		String scrKor = null;
		String scrEng = null;
		int varOrdSn = 0;

		for(String item : list){
			paramMap.put("arrySn", ++arrySn);
			if(item.equals("ITEM")){
				statHtmlDAO.insertScrapItem(paramMap);
			}else if(item.equals("TIME")){
				//시점 파라미터 생성
				String[] periodStrArr = generatePeriodStr(paramInfo);

				paramMap.put("param", periodStrArr[0]);
				paramMap.put("paramWeb", periodStrArr[1]);
				statHtmlDAO.insertScrapPeriod(paramMap);
			}else{
				paramMap.put("objVarId", item);
				classMap = statHtmlDAO.selectClassName(paramMap);
				scrKor = (String)classMap.get("SCR_KOR");
				scrEng = (String)classMap.get("SCR_ENG");
				varOrdSn = ((BigDecimal)classMap.get("VAR_ORD_SN")).intValue();

				paramMap.put("code", item);
				paramMap.put("korName", scrKor);
				paramMap.put("engName", scrEng);
				paramMap.put("varOrdSn", varOrdSn);
				statHtmlDAO.insertScrapClass(paramMap);
			}
		}

		return arrySn;
	}

	//TN_STBL_SCR의 PARAM, PARAM_WEB에 넣을 문자열 생성
	public String[] generatePeriodStr(ParamInfo paramInfo) throws Exception{
		String[] retStr = new String[2];

		String myscrapPeriodStr = paramInfo.getMyscrapPeriod();

		char myscrapPeriod = 's';
		if(myscrapPeriodStr != null && myscrapPeriodStr.trim().length() > 0){
			myscrapPeriod = myscrapPeriodStr.charAt(0);
		}

		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		String[] prdArr = prd.split("@");

		String prdSe = null;
		StringBuffer prdBuff = new StringBuffer();
		StringBuffer prdBuffWeb = new StringBuffer();

		/* 2014.07.03 - 최근시점기준일 경우 10,000셀을 기준으로 각주기별로 선택한 시점 갯수에 따른 백분율로 각각 최근시점부터의 시점갯수를 할당해 준다.*/
		int[] prdSelcnt = new int[prdArr.length]; //주기별 선택한 시점수 저장배열
		String[] prdSelist = new String[prdArr.length]; //선택한 주기
		String[] prdSel = new String[prdArr.length]; //주기별 선택한 시점중 제일 작은 시점 (시작 시점)
		double[] prdSelPer = new double[prdArr.length]; //주기별 선택한 시점수 백분율
		int[] prdDimCnt = new int[prdArr.length]; //주기별 백분율에 따른 허용 차원수
		int[] prdCntArry = new int[prdArr.length]; //주기별 백분율에 따른 허용 시점수
		int ItemMultiply = NumberUtils.stringToInt(paramInfo.getItemMultiply());  //(항목 * 분류)
		int total_cnt = 0;
		int OverPrdListCnt = 0; // 해당 주기의 시작시점보다 큰 시점들의 카운트

		//String Selcnt = "";
		//String SelPer = "";
		//String DimCnt = "";
		//String buff = "";

		Map paramMap = new HashMap();

		paramMap.put("serverType",paramInfo.getServerType());
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());

		if(myscrapPeriod == 'r'){ // 최근시점기준일 경우
			String[] tmpArr = null;
			for(int i = 0; i < prdArr.length; i++){
				tmpArr = null;
				tmpArr = prdArr[i].split(",");

				prdSelist[i] = tmpArr[0]; // 선택한 주기
				prdSelcnt[i] = tmpArr.length - 1; // 선택한 시점의 갯수만 필요하기 때문에 맨앞의 주기는 뺀다
				total_cnt += (tmpArr.length - 1);

				prdSel[i] = tmpArr[tmpArr.length-1]; // 주기별 선택한 시점중 제일 작은 시점 (시작 시점)

				//Selcnt += prdSelcnt[i] + ",";
			}

			for(int i = 0; i < prdArr.length; i++){
				prdSelPer[i] = ((double)prdSelcnt[i] / total_cnt) * 100; // 총 10000셀중 해당주기에 할당되는 퍼센트

				prdDimCnt[i] = (int)prdSelPer[i] * 100;	// 해당주기의 허용되는 총 차원수

				paramMap.put("prdSe", prdSelist[i]);
				paramMap.put("prdDe", prdSel[i]);

				OverPrdListCnt = statHtmlDAO.getTmpStatPrdListForScrap(paramMap);

				int k = 0;
				for( k = OverPrdListCnt ; k > 0; k--){ // 자신이 고른 최소 시점부터 해당 주기의 최근 시점 갯수만큼 반복
					if( (k * ItemMultiply) <= prdDimCnt[i]){ // 할당된 최대 차원수만큼의 시점을 구함
						break;
					}
				}
				prdCntArry[i] = k;

				//buff += OverPrdListCnt + " ::: " +prdCntArry[i]+",";
				//SelPer += (int)prdSelPer[i] + ", ";
				//DimCnt += (int)prdDimCnt[i] + ", ";
			}
		}
		/*
		System.out.println("=============================================================> prdCntArry : " + buff);
		System.out.println("=============================================================> Selcnt : " + Selcnt);
		System.out.println("=============================================================> SelPer : " + SelPer);
		System.out.println("=============================================================> DimCnt : " + DimCnt);
		System.out.println("=============================================================> ItemMultiply : " + ItemMultiply);
		System.out.println("=============================================================> total_cnt : " + total_cnt);
		*/

		for(int i = 0; i < prdArr.length; i++){
			String[] tmpArr = prdArr[i].split(",");

			if(i > 0){
				prdBuff.append(",");
			}

			//최근시점 기준인 경우 앞에 R을 붙인다.
			if(myscrapPeriod == 'r'){
				prdBuff.append("R");
				prdBuffWeb.append("R,");
			}

			prdSe = tmpArr[0];

			if(myscrapPeriod == 'r'){
				prdBuff.append(prdSe);
			}
			prdBuffWeb.append(prdSe + ",");

			int prdCnt = 0;
			for(int j = 1; j < tmpArr.length; j++){
				if(myscrapPeriod == 's'){
					prdBuffWeb.append(tmpArr[j] + ",");
				}
				prdCnt++;
			}

			if(myscrapPeriod == 'r'){
				//prdBuff.append(prdCnt);
				//prdBuffWeb.append(prdCnt);
				prdBuff.append(prdCntArry[i]);	// 2014.07.07 마이스크랩 최근시점기준 스크랩관련 수정
				prdBuffWeb.append(prdCntArry[i]); // 2014.07.07 마이스크랩 최근시점기준 스크랩관련 수정
			}else{
				prdBuff.append(prdSe + tmpArr[tmpArr.length - 1] + "_" + prdSe + tmpArr[1]);
			}

			prdBuffWeb.append("@");
		}

		//System.out.println("@@@@@@@@@@@@@@ prdBuff ::: " + prdBuff.toString());
		//System.out.println("@@@@@@@@@@@@@@ prdBuffWeb ::: " + prdBuffWeb.toString());
		retStr[0] = prdBuff.toString();
		retStr[1] = prdBuffWeb.toString();

		return retStr;
	}

	public Map getDirectStatInfo(ParamInfo paramInfo) throws Exception{
		return statHtmlDAO.getDirectStatInfo(paramInfo);
	}

	public List<Map> getDirectPrdInfo(ParamInfo paramInfo) throws Exception{
		return statHtmlDAO.getDirectPrdInfo(paramInfo);
	}

	public List<Map> getDirectPrdList(ParamInfo paramInfo) throws Exception{

		List<Map> prdList = statHtmlDAO.getDirectPrdDe(paramInfo);	// 시점 조회

		Map tmpMap	= new HashMap();

		String prdSe = paramInfo.getPrdSe();

		String tmpPrdDe = null;
		String tmpPrdDeStr = null;
		for(int i=0; i<prdList.size(); i++){
			tmpMap = prdList.get(i);

			tmpPrdDe = (String)tmpMap.get("PRD_DE");
			tmpPrdDeStr = StatPivotUtil.generatePrdDe(tmpPrdDe, prdSe); // 화면에 보이는 시점 리스트

			tmpMap.put("PRD_DE_STR", tmpPrdDeStr);
		}

		return prdList;
	}

	public String getConnPath(String vwCd) throws Exception{
		return statHtmlDAO.getConnPath(vwCd);
	}

	public void insertUseLogForDirect(Map paramMap) throws Exception{
		statHtmlDAO.insertUseLogForDirect(paramMap);
	}

	/*
	 *	안영수 부모아이콘 변경
	 */
	public List getParentIconArr(ParamInfo paramInfo) throws Exception{

		List parentArr = new ArrayList();

		//분류정보
		JSONArray jFieldArr = JSONArray.fromObject(StringEscapeUtils.unescapeHtml(paramInfo.getClassAllArr()));

		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());

		for(int k=0; k< jFieldArr.size(); k++){
			Map addMap = new HashMap();
			List <EgovMap>addArr = new ArrayList();
			JSONObject jObj = (JSONObject) jFieldArr.get(k);
			paramMap.put("objVarId",jObj.get("objVarId"));
			paramMap.put("targetId", "OV_L"+jObj.get("ovlSn")+"_ID");

			//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
			paramMap.put("condTable", paramInfo.getCondTable());

			addArr = statHtmlDAO.selectParentIcon(paramMap);
			if(addArr.size() > 0){
				addMap.put("objVarId",jObj.get("objVarId"));
				List parentList = new ArrayList();
				for(EgovMap tempArr:addArr){
					String parentCode = (String) tempArr.get("parentCode");

					String[] parArr = parentCode.split("@");
					int parArrLen = parArr.length;

					for(int i = 0; i < parArrLen; i++){
						parentList.add(parArr[i]);
					}
				}

				ArrayList<String> uniqueArr = new ArrayList<String>();			//hashSet list세팅 @parent 중복 제거
				HashSet hs = new HashSet(parentList);
				Iterator it = hs.iterator();
				while(it.hasNext()){
					uniqueArr.add((String)it.next());
				}
				addMap.put("parentList", uniqueArr);
				parentArr.add(addMap);
			}
		}

		/*for(int v=0; v<parentArr.size(); v++){
			System.out.println("아이콘 변경리스트===>"+parentArr.get(v));
		}*/

		return parentArr;
	}

	public ModelAndView getClassSearchType(Map paramMap, ModelAndView model) throws Exception{

		String searchCondition = (String) paramMap.get("searchCondition");
		if(searchCondition.equals("J")){
			List<EgovMap> divClassList = statHtmlDAO.selectHtmlItemList(paramMap);
			model.addObject("result", StatPivotUtil.listViewNm(divClassList,"list",(String) paramMap.get("dtatOpt")));
		}else{
			List<Map> tmpList = new ArrayList();

			String lvlStr = (String) paramMap.get("lvl");
			String[] parArr = lvlStr.split(",");
			int parArrLen = parArr.length;

			for(int i = 0; i < parArrLen; i++){
				paramMap.put("lvl", parArr[i]);
				//1레벨인 경우 함수값 여부 체크
				if(paramMap.get("lvl").equals("1")){
					Map classMap = statHtmlDAO.selectRangeClassInfo(paramMap);
					paramMap.put("totIgnrAt",classMap.get("totIgnrAt"));							//함수값여부
				}

				List<EgovMap> divClassList = statHtmlDAO.selectHtmlItemList(paramMap);
				//nso.jsp g_defaultClassArr 형태로 세팅

				List dataList = new ArrayList();

				for(EgovMap tempList:divClassList){
					dataList.add((String)tempList.get("itmId"));
				}
				Map tmpMap = new HashMap();
				tmpMap.put("objVarId", paramMap.get("objVarId"));
				tmpMap.put("classType",parArr[i]);					//화면 레벨
				tmpMap.put("data",dataList);
				tmpMap.put("classLvlCnt", dataList.size());
				tmpList.add(tmpMap);
			}

			model.addObject("result",tmpList);
		}
		return model;
	}
	
	/* 출처 더보기
	 * 
	 */
	public RelationInfo getStatInfo(ParamInfo paramInfo) throws Exception{
	
		RelationInfo statInfo = new RelationInfo();
		
		List<EgovMap> statInfoList = (List<EgovMap>)statHtmlDAO.getSelectStatInfo(paramInfo);
		
		statInfo.setRelationList(statInfoList);
		
		return statInfo;
	}

}
