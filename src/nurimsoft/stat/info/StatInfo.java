package nurimsoft.stat.info;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nurimsoft.stat.manager.DefaultSettingManager;
import nurimsoft.stat.util.PropertyManager;

public class StatInfo {

	protected Log log = LogFactory.getLog(this.getClass());

	private ParamInfo paramInfo;

	//기본정보
	private String orgId;
	private String tblId;

	private String orgNm;

	//TN_STBL_INFO 정보
	private String tblNm;
	private String tblEngNm;
	private String olapStl;
	private String existStblCmmtKor;	//통계표 주석 존재 유무-한글(Y or N)
	private String existStblCmmtEng;	//통계표 주석 존재 유무-영문(Y or N)
	private int dimCo;	//DIM_CO 차원수
	private String dimUnitYn;	//차원 단위 여부(Y or N)
	private String wgtYn; //가중치 여부(Y or N)
	private String tblSe; 		// 2015.11.24 남규옥 추가 ::: 통계표 구분

	private String unitId;	//단위 ID
	private String unitNmKor;	//한글 단위
	private String unitNmEng;	//영문 단위
	private String unitNm; 		//데이터보기 옵션에 따라 변수 값 넣기
	private int itemCnt;	//항목 개수(TN_STBL_INFO의 CHAR_ITM_CO)		//항목 카운트가 1이면 tabMenu에서 안보여줌

	private String smblYn;

	//주석 존재 유무(TN_CMMT_INFO에서 주석이 존재하는 지 여부)
	private String existCmmtKor;  //count 언어
	private String existCmmtEng;

	private String levelExpr;	//계층별 컬럼 구분 값(T or F or S) - DefaultSettingManager가 담당

	//탭 메뉴 리스트
	private List tabMenuList;	//항목,시점은 jsp에 고정. 탭메뉴 갯수에 따라 각각 메인 view 존재

	//이규정 자료갱신일 수록기간 추가
	//자료갱신일
	private String renewalDate;

	//수록기간
	private String containPeriod;

	//조사아이디 및 TN_STBL_INFO의 PUB_SE 마지막 숫자
	private String statId;
	private String pubLog;

	//주기 수
	private int periodCnt;
	//주기 문자열(#으로 구분. ex) D#, D#M#Y#)
	private String periodStr;

	//초기에 활성화될 주기 수 및 문자(#으로 구분. ex) D#, D#M#Y#)
	private int defaultPeriodCnt;
	private String defaultPeriodStr;

	//주기별 시점정보
	private PeriodInfo periodInfo;

	//항목정보
	private ItemInfo itemInfo;

	//분류정보
	private List<ClassInfo> classInfoList;

	//Pivot 정보
	private PivotInfo pivotInfo;

	//출처 및 링크 정보
	private StatLinkInfo statLinkInfo;

	//통계표 팝업 html 명 및 높이
	private String popupFileNm;
	private String popupFileHeight;

	//통계표 팝업 base url
	private String popupHtmlUrl = PropertyManager.getInstance().getProperty("link.stat.popuphtml");

	//서버 url
	private String url = PropertyManager.getInstance().getProperty("server.url");

	//Default Action
	private String defaultAction = PropertyManager.getInstance().getProperty("server.defaultaction");

	//2013.11.19 추가
	private boolean downloadable = true;	//다운로드 가능여부
	private boolean analyzable = true;		//분석 가능여부

	//2013.12.23 추가 (대용량 파일 서비스 여부)
	private String massYn = "N";

	//2014.01.02 추가 (직접다운로드 링크 여부, olap_stl 첫째 자리가 'X'인 통계표)
	private String directYn = "N";

	//2014.01.06 mixCnt 추가(olap_stl의 분류*항목 조합 수)
	private long mixCnt;

	//2014.01.15
	private String serverType;

	private String serverUrl;

	public String getServerUrl() {
		return (serverUrl == null) ? "" : serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public long getMixCnt() {
		return mixCnt;
	}

	public void setMixCnt(long mixCnt) {
		this.mixCnt = mixCnt;
	}

	public String getDirectYn() {
		return directYn;
	}

	public void setDirectYn(String directYn) {
		this.directYn = directYn;
	}

	public StatInfo(ParamInfo paramInfo){
		this.paramInfo = paramInfo;
		this.orgId = paramInfo.getOrgId();
		this.tblId = paramInfo.getTblId();
	}

	public String getMassYn() {
		return massYn;
	}

	public void setMassYn(String massYn) {
		this.massYn = massYn;
	}

	public String getUnitNm() {
		return unitNm;
	}

	public void setUnitNm(String unitNm) {
		this.unitNm = unitNm;
	}

	//2013.11.19 추가
	public boolean isDownloadable() {
		return downloadable;
	}

	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}

	public boolean isAnalyzable() {
		return analyzable;
	}

	public void setAnalyzable(boolean analyzable) {
		this.analyzable = analyzable;
	}

	public String getPubLog() {
		return pubLog;
	}

	public void setPubLog(String pubLog) {
		this.pubLog = pubLog;
	}

	public String getStatId() {
		return statId;
	}

	public void setStatId(String statId) {
		this.statId = statId;
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

	public PivotInfo getPivotInfo() {
		return pivotInfo;
	}

	public void setPivotInfo(PivotInfo pivotInfo) {
		this.pivotInfo = pivotInfo;
	}

	public String getOlapStl(){
		return olapStl;
	}

	public void setOlapStl(String olapStl) {
		this.olapStl = olapStl;
	}

	public ParamInfo getParamInfo() {
		return paramInfo;
	}

	public void setParamInfo(ParamInfo paramInfo) {
		this.paramInfo = paramInfo;
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

	public String getExistCmmtKor() {
		return existCmmtKor;
	}

	public void setExistCmmtKor(String existCmmtKor) {
		this.existCmmtKor = existCmmtKor;
	}

	public String getExistCmmtEng() {
		return existCmmtEng;
	}

	public void setExistCmmtEng(String existCmmtEng) {
		this.existCmmtEng = existCmmtEng;
	}

	public int getItemCnt() {
		return itemCnt;
	}

	public void setItemCnt(int itemCnt) {
		this.itemCnt = itemCnt;
	}


	public int getPeriodCnt() {
		return periodCnt;
	}

	public void setPeriodCnt(int periodCnt) {
		this.periodCnt = periodCnt;
	}

	public PeriodInfo getPeriodInfo() {
		return periodInfo;
	}

	public void setPeriodInfo(PeriodInfo periodInfo) {
		this.periodInfo = periodInfo;
	}

	public ItemInfo getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(ItemInfo itemInfo) {
		this.itemInfo = itemInfo;
	}

	public List getTabMenuList() {
		return tabMenuList;
	}

	public void setTabMenuList(List tabMenuList) {
		this.tabMenuList = tabMenuList;
	}

	public List getClassInfoList() {
		return classInfoList;
	}

	public void setClassInfoList(List classInfoList) {
		this.classInfoList = classInfoList;
	}

	public int getDimCo() {
		return dimCo;
	}

	public void setDimCO(int dimCo) {
		this.dimCo = dimCo;
	}

	public String getPeriodStr() {
		return periodStr;
	}

	public void setPeriodStr(String periodStr) {
		this.periodStr = periodStr;
	}

	public int getDefaultPeriodCnt() {
		return defaultPeriodCnt;
	}

	public void setDefaultPeriodCnt(int defaultPeriodCnt) {
		this.defaultPeriodCnt = defaultPeriodCnt;
	}

	public String getDefaultPeriodStr() {
		return defaultPeriodStr;
	}

	public void setDefaultPeriodStr(String defaultPeriodStr) {
		this.defaultPeriodStr = defaultPeriodStr;
	}

	public String getDimUnitYn() {
		return dimUnitYn;
	}

	public void setDimUnitYn(String dimUnitYn) {
		this.dimUnitYn = dimUnitYn;
	}

	public String getWgtYn() {
		return wgtYn;
	}

	public void setWgtYn(String wgtYn) {
		this.wgtYn = wgtYn;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitNmKor() {
		return unitNmKor;
	}

	public void setUnitNmKor(String unitNmKor) {
		this.unitNmKor = unitNmKor;
	}

	public String getUnitNmEng() {
		return unitNmEng;
	}

	public void setUnitNmEng(String unitNmEng) {
		this.unitNmEng = unitNmEng;
	}

	public String getLevelExpr() {
		return levelExpr;
	}

	public void setLevelExpr(String levelExpr) {
		this.levelExpr = levelExpr;
	}

	public void setDimCo(int dimCo) {
		this.dimCo = dimCo;
	}

	//이규정 자료갱신일 및 수록기간 추가
	public String getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(String renewalDate) {
		this.renewalDate = renewalDate;
	}

	public String getContainPeriod() {
		return containPeriod;
	}

	public void setContainPeriod(String containPeriod) {
		this.containPeriod = containPeriod;
	}

	public StatLinkInfo getStatLinkInfo() {
		return statLinkInfo;
	}

	public void setStatLinkInfo(StatLinkInfo statLinkInfo) {
		this.statLinkInfo = statLinkInfo;
	}

	public String getPopupFileNm() {
		return popupFileNm;
	}

	public void setPopupFileNm(String popupFileNm) {
		this.popupFileNm = popupFileNm;
	}

	public String getPopupFileHeight() {
		return popupFileHeight;
	}

	public void setPopupFileHeight(String popupFileHeight) {
		this.popupFileHeight = popupFileHeight;
	}

	public String getPopupHtmlUrl() {
		return popupHtmlUrl;
	}

	public void setPopupHtmlUrl(String popupHtmlUrl) {
		this.popupHtmlUrl = popupHtmlUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(String defaultAction) {
		this.defaultAction = defaultAction;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getSmblYn() {
		return smblYn;
	}

	public void setSmblYn(String smblYn) {
		this.smblYn = smblYn;
	}

	/* 2015.11.24 남규옥 추가 ::: 통계표 구분 */
	public String getTblSe() {
		return tblSe;
	}
	public void setTblSe(String tblSe) {
		this.tblSe = tblSe;
	}
	/* 2015.11.24 남규옥 추가 끝 */
}
