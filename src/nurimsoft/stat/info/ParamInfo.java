package nurimsoft.stat.info;

import nurimsoft.stat.util.StringUtil;

public class ParamInfo {

	private String orgId;
	private String tblId;
	private String scrId;	//스크랩ID
	private String empId;	//사용자ID
	private String empNm;	//사용자명
	private String sessionId;
	private String serverType;
	private String serverLocation;

	private String dbUser;
	private String statId;	//조사ID
	private String pub;
	private String pubLog;		//TN_STBL_INFO.PUB_SE의 마지막 숫자

	//pivot 정보
	//ex) 년월(분기)표의 경우 TIME은 TIME_YEAR, TIME_MQ로 넘김
	// colAxis=ITEM,TIME,&rowAxis=A,B,
	private String colAxis;
	private String rowAxis;

	private String language;		//언어('ko' or 'en')
	private String dataOpt;		//한글보기 : ko, 코드+한글보기 : cdko, 영문보기 : en, 코드+영문보기 : cden, 코드보기 : cd

	private String prdSort = "desc";		//시점정렬(asc : 오름차순, desc : 내림차순) - default ::: desc

	private String enableWeight = "N";	//가중치 여부(Y or N)
	private String enableCellUnit = "N";  //셀단위 여부(Y or N)

	private String periodCo;		//소수점

	private String enableLevelExpr = "N"; //계층별 컬럼보기 여부(Y or N)

	//통계표형태
	//기본통계표: standard
	//시계열표(표측) : timeSeriesV
	//시계열표(표두) : timeSeriesH
	//년/월(분기) : perYear
	//초기조회 : default
	private String tableType = "default";

	private String enableParentLevel;	//상위레벨보기(Y or N)

	//서비스 목록 및 조회 경로 정보
	private String connPath;
	private String vwCd;
	private String listId;

	private String logSeq;
	private String isFirst = "Y"; //최초 조회여부 로그정보를 남기기 위함.(Y or N) default : Y

	//통계표주석 유무(언어별) : 주석정보를 만들기 위함(주석번호)
	private String existStblCmmtKor = "N"; //통계표주석 여부(한글) - Y or N
	private String existStblCmmtEng = "N"; //통계표주석 여부(영문) - Y or N

	//조회조건 json field
	private String fieldList;

	//사용자 ip
	private String ipAddr;

	//조회유형 및 조회상세유형
	private String viewKind;
	private String viewSubKind;

	//분석여부 추가 Y or N
	private String doAnal = "N";

	//분석종류
	private String analType;
	private String analTypeNm;
	private String analTypeNmEng;

	//분석 비교기준
	private String analCmpr;
	private String analCmprNm;
	private String analCmprNmEng;

	//분석  시 기준년(월) 값
	private String analTime;

	//2013.11.20
	private String analClass;
	private String analItem;

	//분석(구성비/누계 구성비 일경우 콤보박스세팅)
	private String analCombo;

	//분석 원자료 함께 보기
	private String originData; //Y or N

	private String funcPrdSe;				//분석 비교기준 세팅을 위한 변수

	//분석(구성비) 기준선택 여부
	private String noSelect;

	private String smblYn;

	//조회 유형
	/*
	 * 통계표조회 : table
	 * 엑셀 : excel
	 * 엑셀분석용 : excelAnal
	 * csv : csv
	 * txt : txt
	 * (sdmx : sdmx)
	 */
	private String view = "table";	//Default table

	private String contextPath;
	//private String realPath;	// 2020.08.13 was 정보 노출!! 보안취약점 제거

	private String ordColIdx;	//정렬컬럼index
	private String ordType;		//정렬방법 > 0:asc, 1:desc

	private String downLargeFileType; // 파일형태 excel, csv
	private String downLargeExprType; // 출력형태 1:시점표두 2:항목표두
	private String exprYn;			  // 코드포함
	private String downLargeSort;

	private String downGridMeta;	//엑셀 출력 시 메타자료 포함여부
	private String downGridCellMerge;	//엑셀 출력 시 셀병합여부

	private String downGridSdmxType;	//sdmx 파일 타입 > dsd, data
	private String downGridSdmxDataType;	//data 파일 타입 > generic, compact

	//서비스목록으로 부터 특정 분류값이 넘어왔을경우 담는 변수
	//이규정 수정1000
	private String obj_var_id;
	private String itm_id;

	private String tblNm;
	private String tblEngNm;

	//scrap
	private String folderId;		//folder_id
	private String myscrabTblNm;	//스크랩 저장시 테이블 명
	private String myscrapPeriod;	//시점 저장기준(최근시점기준 또는 조회시점기준

	private String isChangedDataOpt;	//dataOpt(언어)변경 여부 체크

	private String mode;				//Tab or 새창

	private String classAllArr;			//분류 파라미터값(화면에서 받아옴), 선택정보전체보기 또는 dataOpt(언어)변경 시 사용됨.
	private String classSet;			//분류 파라미터값(화면에서 받아옴), 일괄설정에서만 사용

	private String analTextTblNm;		//분석 시 통계표 명 우측에 분석명을 표시하기 위한 변수

	private String itemMultiply;	//항목 X 분류 조합수
	private String dimCo;			//TN_STBL_INFO.DIM_CO

	private String fnExcptCd;	//OLAP_FN_EXCPT의 EXCPT_CD 값

	private String periodStr;	//주기 문자열(#으로 구분. ex) D#, D#M#Y#)

	//2014.01.10 부가기능 및 피봇 이용 로그를 위한 param
	private String usePivot;	//피봇 화면에서 적용버튼 클릭한 경우 (Y or N)
	private String useAddFuncLog;	//부가기능 적용 버튼 클릭 시 넘어오는 view_sub_kind 값

	private String etldbLink;

	private String chargerLvl;			//담당자레벨 1인경우 0,1,2,3,4 없는 경우 0,2,3,4

	private String viewType;			//업무:B , 호스팅:H , 보급용:S

	//2014.02.06
	private String analText;

	private String prdSe;	//직접다운로드에서 넘어오는 주기
	private String prdDe; 	//직접다운로드에서 넘어오는 시점(string : 2012,2011,2010,...)
	private String direct;	//직접다운로드에서 넘어오는 타입(직접 다운로드인 경우에만 direct로 받음)

	private String st;	//업무용에서 공표구분을 0,3,4 로만 조회하고자 하는 경우 사용할 파라미터
	private String serverTypeOrigin;	//2014.03.26 :: serverType원본 : 기존의 serverType은  공표구분을 결정짓는데 사용하며(단 통계표 정보테이블에서 공표구분 체크시에는 이 변수를 사용) 이 변수는 실제 서버 구성을 체크할때 사용한다.

	//2014.04.29 호스팅 DB이전시 TN_STAT_HTML_COND_WEB 테이블을 읽어올수 없는 문제로 NSI, NSI_SYS, 각 보급계정에서만 사용하게 하기위해 추가
	private String condTable;
	private String new_win;	// 2014.05.02 새창열기시에 지금 보고있는데로 열리도록하기위해 새창인지 구별할 수 있는 파라미터 추가 - 김경호
	private String first_open;	// 2014.06.19 처음오픈시만 초기조회조건으로 차트를 열기위해 처음인지 아닌지 판별 파라미터 추가 - 김경호

	private String debug;	//2014.07.07 debug 모드 추가 Ays
	//2014.09
	private String maxCellOver = ""; //20,000셀까지 조회셀수 조정했는지 여부

	//2014.10.16
	private String reqCellCnt = "0"; //조회조건 셀수

	//2015.06.04 상속통계표 관련
	private String inheritYn 	= "N";	//상속통계표 여부 (서비스용에서는 상속통계표 기능 제외하니까 N으로)
	private String originOrgId 	= "";	//부모통계표 기관코드
	private String originTblId 	= "";	//부모통계표 통계표ID
	private String pubSeType	= "";	//상속통계표 공표 타입 (0:미지정, 1:원통계표 공표 따라감)
	
	//2015.04.09 관련통계표 관련
	private String relYn = "N";		//2015.04.09 관련통계표 체크
	private String relUserYn = "N"; //2015.07.17 관련통계 사용/비사용 체크
	private String relChkOrgId;     //2015.08.03 관련통계표의 원통계표 orgId 
	private String relChkTblId;     //2015.08.03 관련통계표의 원통계표 tblId
	
	public String getRelChkOrgId() {
		return relChkOrgId;
	}
	public void setRelChkOrgId(String relChkOrgId) {
		this.relChkOrgId = relChkOrgId;
	}
	public String getRelChkTblId() {
		return relChkTblId;
	}
	public void setRelChkTblId(String relChkTblId) {
		this.relChkTblId = relChkTblId;
	}
	public String getRelUserYn() {
		return relUserYn;
	}
	public void setRelUserYn(String relUserYn) {
		this.relUserYn = relUserYn;
	}
	public String getRelYn() {
		return relYn;
	}
	public void setRelYn(String relYn) {
		this.relYn = relYn;
	}
	public String getReqCellCnt() {
		return reqCellCnt;
	}
	public void setReqCellCnt(String reqCellCnt) {
		this.reqCellCnt = reqCellCnt;
	}
	public String getMaxCellOver() {
		return maxCellOver;
	}
	public void setMaxCellOver(String maxCellOver) {
		this.maxCellOver = maxCellOver;
	}
	public String getFirst_open() {
		return first_open;
	}
	public void setFirst_open(String first_open) {
		this.first_open = first_open;
	}
	public String getNew_win() {
		return new_win;
	}
	public void setNew_win(String new_win) {
		this.new_win = new_win;
	}
	public String getCondTable() {
		return condTable;
	}
	public void setCondTable(String condTable) {
		this.condTable = condTable;
	}

	public String getServerTypeOrigin() {
		return serverTypeOrigin;
	}
	public void setServerTypeOrigin(String serverTypeOrigin) {
		this.serverTypeOrigin = serverTypeOrigin;
	}

	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getDirect() {
		return (direct == null) ? "" : direct;
	}
	public void setDirect(String direct) {
		this.direct = direct;
	}
	public String getPrdDe() {
		return prdDe;
	}
	public void setPrdDe(String prdDe) {
		this.prdDe = prdDe;
	}
	public String getPrdSe() {
		return prdSe;
	}
	public void setPrdSe(String prdSe) {
		this.prdSe = prdSe;
	}

	public String getPubLog() {
		return pubLog;
	}
	public void setPubLog(String pubLog) {
		this.pubLog = pubLog;
	}

	public String getAnalText() {
		return analText;
	}
	public void setAnalText(String analText) {
		this.analText = analText;
	}

	public String getEtldbLink() {
		return etldbLink;
	}

	public void setEtldbLink(String etldbLink) {
		this.etldbLink = etldbLink;
	}
	public String getUseAddFuncLog() {
		return useAddFuncLog;
	}
	public void setUseAddFuncLog(String useAddFuncLog) {
		this.useAddFuncLog = useAddFuncLog;
	}
	public String getUsePivot() {
		return usePivot;
	}
	public void setUsePivot(String usePivot) {
		this.usePivot = usePivot;
	}
	public String getFnExcptCd() {
		return fnExcptCd;
	}
	public void setFnExcptCd(String fnExcptCd) {
		this.fnExcptCd = fnExcptCd;
	}
	public String getItemMultiply() {
		return itemMultiply;
	}
	public void setItemMultiply(String itemMultiply) {
		this.itemMultiply = itemMultiply;
	}
	public String getDimCo() {
		return dimCo;
	}
	public void setDimCo(String dimCo) {
		this.dimCo = dimCo;
	}
	public String getAnalTextTblNm() {
		return analTextTblNm;
	}
	public void setAnalTextTblNm(String analTextTblNm) {
		this.analTextTblNm = "<" + analTextTblNm + ">";
	}
	public String getAnalTypeNm() {
		return analTypeNm;
	}
	public void setAnalTypeNm(String analTypeNm) {
		this.analTypeNm = analTypeNm;
	}
	public String getAnalTypeNmEng() {
		return analTypeNmEng;
	}
	public void setAnalTypeNmEng(String analTypeNmEng) {
		this.analTypeNmEng = analTypeNmEng;
	}
	public String getAnalCmprNm() {
		return analCmprNm;
	}
	public void setAnalCmprNm(String analCmprNm) {
		this.analCmprNm = analCmprNm;
	}
	public String getAnalCmprNmEng() {
		return analCmprNmEng;
	}
	public void setAnalCmprNmEng(String analCmprNmEng) {
		this.analCmprNmEng = analCmprNmEng;
	}

	public String getAnalClass() {
		return analClass;
	}
	public void setAnalClass(String analClass) {
		this.analClass = analClass;
	}
	public String getAnalItem() {
		return analItem;
	}
	public void setAnalItem(String analItem) {
		this.analItem = analItem;
	}

	public String getClassAllArr() {
		return classAllArr;
	}
	public void setClassAllArr(String classAllArr) {
		this.classAllArr = classAllArr;
	}

	public String getIsChangedDataOpt() {
		return isChangedDataOpt;
	}
	public void setIsChangedDataOpt(String isChangedDataOpt) {
		this.isChangedDataOpt = isChangedDataOpt;
	}

	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = StringUtil.getCSRF(mode);
	}

	//이규정 수정1000
	public String getObj_var_id() {
		return obj_var_id;
	}
	public void setObj_var_id(String obj_var_id) {
		this.obj_var_id = obj_var_id;
	}
	public String getItm_id() {
		return itm_id;
	}
	public void setItm_id(String itm_id) {
		this.itm_id = itm_id;
	}
	public String getTblNm() {
		return tblNm;
	}
	public void setTblNm(String tblNm) {
		this.tblNm = tblNm;
	}
	public String getTblEngNm() {
		return tblEngNm;
	}
	public void setTblEngNm(String tblEngNm) {
		this.tblEngNm = tblEngNm;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public String getMyscrabTblNm() {
		return myscrabTblNm;
	}
	public void setMyscrabTblNm(String myscrabTblNm) {
		this.myscrabTblNm = myscrabTblNm;
	}
	public String getMyscrapPeriod() {
		return myscrapPeriod;
	}
	public void setMyscrapPeriod(String myscrapPeriod) {
		this.myscrapPeriod = myscrapPeriod;
	}

	public String getDownGridSdmxType() {
		return downGridSdmxType;
	}
	public void setDownGridSdmxType(String downGridSdmxType) {
		this.downGridSdmxType = downGridSdmxType;
	}
	public String getDownGridSdmxDataType() {
		return downGridSdmxDataType;
	}
	public void setDownGridSdmxDataType(String downGridSdmxDataType) {
		this.downGridSdmxDataType = downGridSdmxDataType;
	}

	public String getDownGridMeta() {
		return downGridMeta;
	}
	public void setDownGridMeta(String downGridMeta) {
		this.downGridMeta = downGridMeta;
	}
	public String getDownGridCellMerge() {
		return downGridCellMerge;
	}
	public void setDownGridCellMerge(String downGridCellMerge) {
		this.downGridCellMerge = downGridCellMerge;
	}
	public String getDownLargeFileType() {
		return downLargeFileType;
	}
	public void setDownLargeFileType(String downLargeFileType) {
		this.downLargeFileType = downLargeFileType;
	}
	public String getDownLargeExprType() {
		return downLargeExprType;
	}
	public void setDownLargeExprType(String downLargeExprType) {
		this.downLargeExprType = downLargeExprType;
	}
	public String getExprYn() {
		return exprYn;
	}
	public void setExprYn(String exprYn) {
		this.exprYn= exprYn;
	}
	public String getDownLargeSort() {
		return downLargeSort;
	}
	public void setDownLargeSort(String downLargeSort) {
		this.downLargeSort = downLargeSort;
	}
/*	
	public String getRealPath() {
		return realPath;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
*/
	public String getOrdColIdx() {
		return ordColIdx;
	}
	public void setOrdColIdx(String ordColIdx) {
		this.ordColIdx = ordColIdx;
	}
	public String getOrdType() {
		return ordType;
	}
	public void setOrdType(String ordType) {
		this.ordType = ordType;
	}
	public String getContextPath() {
		return contextPath;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public String getServerLocation() {
		return serverLocation;
	}
	public void setServerLocation(String serverLocation) {
		this.serverLocation = serverLocation;
	}

	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	public String getOriginData() {
		return originData;
	}
	public void setOriginData(String originData) {
		this.originData = originData;
	}
	public String getAnalTime() {
		return analTime;
	}
	public void setAnalTime(String analTime) {
		this.analTime = analTime;
	}
	public String getAnalType() {
		return analType;
	}
	public void setAnalType(String analType) {
		this.analType = analType;
	}
	public String getAnalCmpr() {
		return analCmpr;
	}
	public void setAnalCmpr(String analCmpr) {
		this.analCmpr = analCmpr;
	}
	public String getDoAnal() {
		return doAnal;
	}
	public void setDoAnal(String doAnal) {
		this.doAnal = doAnal;
	}
	public String getViewKind() {
		return viewKind;
	}
	public void setViewKind(String viewKind) {
		this.viewKind = viewKind;
	}
	public String getViewSubKind() {
		return viewSubKind;
	}
	public void setViewSubKind(String viewSubKind) {
		this.viewSubKind = viewSubKind;
	}
	public String getPub() {
		return pub;
	}
	public void setPub(String pub) {
		this.pub = pub;
	}

	public String getStatId() {
		return statId;
	}
	public void setStatId(String statId) {
		this.statId = statId;
	}

	public String getEmpNm() {
		return empNm;
	}
	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}

	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getTblId() {
		return tblId;
	}
	public void setTblId(String tblId) {
		this.tblId = tblId;
	}
	public String getScrId() {
		return scrId;
	}
	public void setScrId(String scrId) {
		this.scrId = scrId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getColAxis() {
		return colAxis;
	}
	public void setColAxis(String colAxis) {
		this.colAxis = colAxis;
	}
	public String getRowAxis() {
		return rowAxis;
	}
	public void setRowAxis(String rowAxis) {
		this.rowAxis = rowAxis;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDataOpt() {
		return dataOpt;
	}
	public void setDataOpt(String dataOpt) {
		this.dataOpt = dataOpt;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getPrdSort() {
		return prdSort;
	}
	public void setPrdSort(String prdSort) {
		this.prdSort = prdSort;
	}
	public String getEnableWeight() {
		return enableWeight;
	}
	public void setEnableWeight(String enableWeight) {
		this.enableWeight = enableWeight;
	}
	public String getEnableCellUnit() {
		return enableCellUnit;
	}
	public void setEnableCellUnit(String enableCellUnit) {
		this.enableCellUnit = enableCellUnit;
	}
	public String getPeriodCo() {
		return periodCo;
	}
	public void setPeriodCo(String periodCo) {
		this.periodCo = periodCo;
	}
	public String getEnableLevelExpr() {
		return enableLevelExpr;
	}
	public void setEnableLevelExpr(String enableLevelExpr) {
		this.enableLevelExpr = enableLevelExpr;
	}
	public String getConnPath() {
		return StringUtil.changeHtmlToText(connPath);
	}
	public void setConnPath(String connPath) {
		this.connPath = StringUtil.changeHtmlToText(connPath);
	}
	public String getVwCd() {
		return vwCd;
	}
	public void setVwCd(String vwCd) {
		this.vwCd = vwCd;
	}
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
		this.listId = listId;
	}
	public String getLogSeq() {
		return logSeq;
	}
	public void setLogSeq(String logSeq) {
		this.logSeq = logSeq;
	}
	public String getIsFirst() {
		return isFirst;
	}
	public void setIsFirst(String isFirst) {
		this.isFirst = isFirst;
	}
	public String getEnableParentLevel() {
		return enableParentLevel;
	}
	public void setEnableParentLevel(String enableParentLevel) {
		this.enableParentLevel = enableParentLevel;
	}

	public String getExistStblCmmtKor() {
		return existStblCmmtKor;
	}
	public void setExistStblCmmtKor(String existStblCmmtKor) {
		this.existStblCmmtKor = existStblCmmtKor;
	}
	public String getExistStblCmmtEng() {
		return existStblCmmtEng;
	}
	public void setExistStblCmmtEng(String existStblCmmtEng) {
		this.existStblCmmtEng = existStblCmmtEng;
	}
	public String getFieldList() {
		return fieldList;
	}
	public void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}
	public String getFuncPrdSe() {
		return funcPrdSe;
	}
	public void setFuncPrdSe(String funcPrdSe) {
		this.funcPrdSe = funcPrdSe;
	}
	public String getAnalCombo() {
		return analCombo;
	}
	public void setAnalCombo(String analCombo) {
		this.analCombo = analCombo;
	}
	public String getPeriodStr() {
		return periodStr;
	}
	public void setPeriodStr(String periodStr) {
		this.periodStr = periodStr;
	}
	public String getChargerLvl() {
		return chargerLvl;
	}
	public void setChargerLvl(String chargerLvl) {
		this.chargerLvl = chargerLvl;
	}
	public String getViewType() {
		return viewType;
	}
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
	public String getClassSet() {
		return classSet;
	}
	public void setClassSet(String classSet) {
		this.classSet = classSet;
	}
	public String getDebug() {
		return debug;
	}
	public void setDebug(String debug) {
		this.debug = debug;
	}
	public String getNoSelect() {
		return noSelect;
	}
	public String setNoSelect(String noSelect) {
		return this.noSelect = noSelect;
	}
	public String getInheritYn() {
		return inheritYn;
	}
	public void setInheritYn(String inheritYn) {
		this.inheritYn = inheritYn;
	}
	public String getOriginOrgId() {
		return originOrgId;
	}
	public void setOriginOrgId(String originOrgId) {
		this.originOrgId = originOrgId;
	}
	public String getOriginTblId() {
		return originTblId;
	}
	public void setOriginTblId(String originTblId) {
		this.originTblId = originTblId;
	}
	public String getPubSeType() {
		return pubSeType;
	}
	public void setPubSeType(String pubSeType) {
		this.pubSeType = pubSeType;
	}
	public String getSmblYn() {
		return smblYn;
	}
	public void setSmblYn(String smblYn) {
		this.smblYn = smblYn;
	}
}
