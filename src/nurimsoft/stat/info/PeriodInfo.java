package nurimsoft.stat.info;

import java.util.List;

import egovframework.rte.psl.dataaccess.util.EgovMap;

import nurimsoft.webapp.StatHtmlDAO;

public class PeriodInfo {
	
	private char lang = 'k';
	
	public static char D;
	public static char T;
	public static char M;
	public static char B;
	public static char Q;
	public static char H;
	public static char Y;
	public static char F;
	
	private String periodNm;
	//TC_PRD_INFO, TC_PRD_DETAIL 테이블 참조(화면 출력 용)
	//F
	private String F_IR_CODE;
	private String F_2Y_CODE;
	private String F_3Y_CODE;
	private String F_4Y_CODE;
	private String F_5Y_CODE;
	private String F_10Y_CODE;
	
	private String D_NAME_KO;
	private String T_NAME_KO;
	private String M_NAME_KO;
	private String B_NAME_KO;
	private String Q_NAME_KO;
	private String H_NAME_KO;
	private String Y_NAME_KO;
	//F
	private String F_IR_NAME_KO;
	private String F_2Y_NAME_KO;
	private String F_3Y_NAME_KO;
	private String F_4Y_NAME_KO;
	private String F_5Y_NAME_KO;
	private String F_10Y_NAME_KO;
	
	private String D_NAME_EN;
	private String T_NAME_EN;
	private String M_NAME_EN;
	private String B_NAME_EN;
	private String Q_NAME_EN;
	private String H_NAME_EN;
	private String Y_NAME_EN;
	
	private String F_IR_NAME_EN;
	private String F_2Y_NAME_EN;
	private String F_3Y_NAME_EN;
	private String F_4Y_NAME_EN;
	private String F_5Y_NAME_EN;
	private String F_10Y_NAME_EN;
	
	private String codeD;
	private String codeT;
	private String codeM;
	private String codeB;
	private String codeQ;
	private String codeH;
	private String codeY;
	private String codeF;
	
	private String nameD;
	private String nameT;
	private String nameM;
	private String nameB;
	private String nameQ;
	private String nameH;
	private String nameY;
	private String nameF;
	
	
	//start, end 각 주기가 가지고 있는 시점
	private String startD;
	private String endD;
	
	private String startT;
	private String endT;
	
	private String startM;
	private String endM;
	
	private String startB;
	private String endB;
	
	private String startQ;
	private String endQ;
	
	private String startH;
	private String endH;
	
	private String startY;
	private String endY;
	
	private String startF;
	private String endF;
	
	private List listD;
	private List listT;
	private List listM;
	private List listB;
	private List listQ;
	private List listH;
	private List listY;
	private List listF;
	
	//조회조건으로 생성되는 시점(DefaultSettingManager(ScrapSettingManager)가 값을 입력)
	private String defaultStartD;
	private String defaultStartT;
	private String defaultStartM;
	private String defaultStartB;
	private String defaultStartQ;
	private String defaultStartH;
	private String defaultStartY;
	private String defaultStartF;
	
	private String defaultEndD;
	private String defaultEndT;
	private String defaultEndM;
	private String defaultEndB;
	private String defaultEndQ;
	private String defaultEndH;
	private String defaultEndY;
	private String defaultEndF;
	
	private List defaultListD;
	private List defaultListT;
	private List defaultListM;
	private List defaultListB;
	private List defaultListQ;
	private List defaultListH;
	private List defaultListY;
	private List defaultListF;
	
	public PeriodInfo(){}
	
	public PeriodInfo(String dataOpt, StatHtmlDAO statHtmlDAO, String dbUser) throws Exception{
		if(dataOpt.indexOf("en") > -1){
			lang = 'e';
		}
		
		List<EgovMap> tcPrdInfoList = statHtmlDAO.selectTcPrdInfo(dbUser);
		List<EgovMap> tcPrdDetailList = statHtmlDAO.selectTcPrdDetail(dbUser);
		
		String prdSe;
		char prdSeChar;
		for(EgovMap map : tcPrdInfoList){
			prdSe = (String)map.get("prdSe");
			prdSeChar = prdSe.charAt(0);
			
			switch(prdSeChar){
				case 'D':
					D = prdSeChar;
					codeD = prdSe;
					D_NAME_KO = (String)map.get("prdNm");
					D_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'T':
					T = prdSeChar;
					codeT = prdSe;
					T_NAME_KO = (String)map.get("prdNm");
					T_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'M':
					M = prdSeChar;
					codeM = prdSe;
					M_NAME_KO = (String)map.get("prdNm");
					M_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'B':
					B = prdSeChar;
					codeB = prdSe;
					B_NAME_KO = (String)map.get("prdNm");
					B_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'Q':
					Q = prdSeChar;
					codeQ = prdSe;
					Q_NAME_KO = (String)map.get("prdNm");
					Q_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'H':
					H = prdSeChar;
					codeH = prdSe;
					H_NAME_KO = (String)map.get("prdNm");
					H_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'Y':
					Y = prdSeChar;
					codeY = prdSe;
					Y_NAME_KO = (String)map.get("prdNm");
					Y_NAME_EN = (String)map.get("prdEngNm");
					break;
				case 'F':
					F = prdSeChar;
					break;
			}
		}
		
		String prdDetail;
		for(EgovMap map : tcPrdDetailList){
			prdDetail = (String)map.get("prdDetail");
			
			if(prdDetail.equals("2Y")){
				F_2Y_CODE = prdDetail;
				F_2Y_NAME_KO = (String)map.get("prdNm");
				F_2Y_NAME_EN = (String)map.get("prdEngNm");
			}else if(prdDetail.equals("3Y")){
				F_3Y_CODE = prdDetail;
				F_3Y_NAME_KO = (String)map.get("prdNm");
				F_3Y_NAME_EN = (String)map.get("prdEngNm");
			}else if(prdDetail.equals("4Y")){
				F_4Y_CODE = prdDetail;
				F_4Y_NAME_KO = (String)map.get("prdNm");
				F_4Y_NAME_EN = (String)map.get("prdEngNm");
			}else if(prdDetail.equals("5Y")){
				F_5Y_CODE = prdDetail;
				F_5Y_NAME_KO = (String)map.get("prdNm");
				F_5Y_NAME_EN = (String)map.get("prdEngNm");
			}else if(prdDetail.equals("10Y")){
				F_10Y_CODE = prdDetail;
				F_10Y_NAME_KO = (String)map.get("prdNm");
				F_10Y_NAME_EN = (String)map.get("prdEngNm");
			}else{
				F_IR_CODE = prdDetail;
				F_IR_NAME_KO = (String)map.get("prdNm");
				F_IR_NAME_EN = (String)map.get("prdEngNm");
			}
			
		}
		
	}
	
	public void setIrregular(String irregular){
		codeF = irregular;
		
		if(irregular.equals("2Y")){
			nameF = (lang == 'e') ? F_2Y_NAME_EN : F_2Y_NAME_KO;
		}else if(irregular.equals("3Y")){
			nameF = (lang == 'e') ? F_3Y_NAME_EN : F_3Y_NAME_KO;
		}else if(irregular.equals("4Y")){
			nameF = (lang == 'e') ? F_4Y_NAME_EN : F_4Y_NAME_KO;
		}else if(irregular.equals("5Y")){
			nameF = (lang == 'e') ? F_5Y_NAME_EN : F_5Y_NAME_KO;
		}else if(irregular.equals("10Y")){
			nameF = (lang == 'e') ? F_10Y_NAME_EN : F_10Y_NAME_KO;
		}else{
			nameF = (lang == 'e') ? F_IR_NAME_EN : F_IR_NAME_KO;
		}
	}
	
	public String getCodeF() {
		return codeF;
	}
	
	public String getNameD() {
		return (lang == 'e') ? D_NAME_EN : D_NAME_KO;
	}
	
	public String getNameT() {
		return (lang == 'e') ? T_NAME_EN : T_NAME_KO;
	}
	
	public String getNameM() {
		return (lang == 'e') ? M_NAME_EN : M_NAME_KO;
	}
	
	public String getNameB() {
		return (lang == 'e') ? B_NAME_EN : B_NAME_KO;
	}
	
	public String getNameQ() {
		return (lang == 'e') ? Q_NAME_EN : Q_NAME_KO;
	}
	
	public String getNameH() {
		return (lang == 'e') ? H_NAME_EN : H_NAME_KO;
	}
	
	public String getNameY() {
		return (lang == 'e') ? Y_NAME_EN : Y_NAME_KO;
	}
	
	public String getNameF() {
		return nameF;
	}
	
	public String getCodeD() {
		return codeD;
	}
	public String getCodeT() {
		return codeT;
	}
	public String getCodeM() {
		return codeM;
	}
	public String getCodeB() {
		return codeB;
	}
	public String getCodeQ() {
		return codeQ;
	}
	public String getCodeH() {
		return codeH;
	}
	public String getCodeY() {
		return codeY;
	}
	
	public String getStartD() {
		return startD;
	}
	public void setStartD(String startD) {
		this.startD = startD;
	}
	public String getEndD() {
		return endD;
	}
	public void setEndD(String endD) {
		this.endD = endD;
	}
	public String getStartT() {
		return startT;
	}
	public void setStartT(String startT) {
		this.startT = startT;
	}
	public String getEndT() {
		return endT;
	}
	public void setEndT(String endT) {
		this.endT = endT;
	}
	public String getStartM() {
		return startM;
	}
	public void setStartM(String startM) {
		this.startM = startM;
	}
	public String getEndM() {
		return endM;
	}
	public void setEndM(String endM) {
		this.endM = endM;
	}
	public String getStartB() {
		return startB;
	}
	public void setStartB(String startB) {
		this.startB = startB;
	}
	public String getEndB() {
		return endB;
	}
	public void setEndB(String endB) {
		this.endB = endB;
	}
	public String getStartQ() {
		return startQ;
	}
	public void setStartQ(String startQ) {
		this.startQ = startQ;
	}
	public String getEndQ() {
		return endQ;
	}
	public void setEndQ(String endQ) {
		this.endQ = endQ;
	}
	public String getStartH() {
		return startH;
	}
	public void setStartH(String startH) {
		this.startH = startH;
	}
	public String getEndH() {
		return endH;
	}
	public void setEndH(String endH) {
		this.endH = endH;
	}
	public String getStartY() {
		return startY;
	}
	public void setStartY(String startY) {
		this.startY = startY;
	}
	public String getEndY() {
		return endY;
	}
	public void setEndY(String endY) {
		this.endY = endY;
	}
	public String getStartF() {
		return startF;
	}
	public void setStartF(String startF) {
		this.startF = startF;
	}
	public String getEndF() {
		return endF;
	}
	public void setEndF(String endF) {
		this.endF = endF;
	}
	public List getListD() {
		return listD;
	}
	public void setListD(List listD) {
		this.listD = listD;
	}
	public List getListT() {
		return listT;
	}
	public void setListT(List listT) {
		this.listT = listT;
	}
	public List getListM() {
		return listM;
	}
	public void setListM(List listM) {
		this.listM = listM;
	}
	public List getListB() {
		return listB;
	}
	public void setListB(List listB) {
		this.listB = listB;
	}
	public List getListQ() {
		return listQ;
	}
	public void setListQ(List listQ) {
		this.listQ = listQ;
	}
	public List getListH() {
		return listH;
	}
	public void setListH(List listH) {
		this.listH = listH;
	}
	public List getListY() {
		return listY;
	}
	public void setListY(List listY) {
		this.listY = listY;
	}
	public List getListF() {
		return listF;
	}
	public void setListF(List listF) {
		this.listF = listF;
	}
	public String getDefaultStartD() {
		return defaultStartD;
	}
	public void setDefaultStartD(String defaultStartD) {
		this.defaultStartD = defaultStartD;
	}
	public String getDefaultStartT() {
		return defaultStartT;
	}
	public void setDefaultStartT(String defaultStartT) {
		this.defaultStartT = defaultStartT;
	}
	public String getDefaultStartM() {
		return defaultStartM;
	}
	public void setDefaultStartM(String defaultStartM) {
		this.defaultStartM = defaultStartM;
	}
	public String getDefaultStartB() {
		return defaultStartB;
	}
	public void setDefaultStartB(String defaultStartB) {
		this.defaultStartB = defaultStartB;
	}
	public String getDefaultStartQ() {
		return defaultStartQ;
	}
	public void setDefaultStartQ(String defaultStartQ) {
		this.defaultStartQ = defaultStartQ;
	}
	public String getDefaultStartH() {
		return defaultStartH;
	}
	public void setDefaultStartH(String defaultStartH) {
		this.defaultStartH = defaultStartH;
	}
	public String getDefaultStartY() {
		return defaultStartY;
	}
	public void setDefaultStartY(String defaultStartY) {
		this.defaultStartY = defaultStartY;
	}
	public String getDefaultStartF() {
		return defaultStartF;
	}
	public void setDefaultStartF(String defaultStartF) {
		this.defaultStartF = defaultStartF;
	}
	public String getDefaultEndD() {
		return defaultEndD;
	}
	public void setDefaultEndD(String defaultEndD) {
		this.defaultEndD = defaultEndD;
	}
	public String getDefaultEndT() {
		return defaultEndT;
	}
	public void setDefaultEndT(String defaultEndT) {
		this.defaultEndT = defaultEndT;
	}
	public String getDefaultEndM() {
		return defaultEndM;
	}
	public void setDefaultEndM(String defaultEndM) {
		this.defaultEndM = defaultEndM;
	}
	public String getDefaultEndB() {
		return defaultEndB;
	}
	public void setDefaultEndB(String defaultEndB) {
		this.defaultEndB = defaultEndB;
	}
	public String getDefaultEndQ() {
		return defaultEndQ;
	}
	public void setDefaultEndQ(String defaultEndQ) {
		this.defaultEndQ = defaultEndQ;
	}
	public String getDefaultEndH() {
		return defaultEndH;
	}
	public void setDefaultEndH(String defaultEndH) {
		this.defaultEndH = defaultEndH;
	}
	public String getDefaultEndY() {
		return defaultEndY;
	}
	public void setDefaultEndY(String defaultEndY) {
		this.defaultEndY = defaultEndY;
	}
	public String getDefaultEndF() {
		return defaultEndF;
	}
	public void setDefaultEndF(String defaultEndF) {
		this.defaultEndF = defaultEndF;
	}
	public List getDefaultListD() {
		return defaultListD;
	}
	public void setDefaultListD(List defaultListD) {
		this.defaultListD = defaultListD;
	}
	public List getDefaultListT() {
		return defaultListT;
	}
	public void setDefaultListT(List defaultListT) {
		this.defaultListT = defaultListT;
	}
	public List getDefaultListM() {
		return defaultListM;
	}
	public void setDefaultListM(List defaultListM) {
		this.defaultListM = defaultListM;
	}
	public List getDefaultListB() {
		return defaultListB;
	}
	public void setDefaultListB(List defaultListB) {
		this.defaultListB = defaultListB;
	}
	public List getDefaultListQ() {
		return defaultListQ;
	}
	public void setDefaultListQ(List defaultListQ) {
		this.defaultListQ = defaultListQ;
	}
	public List getDefaultListH() {
		return defaultListH;
	}
	public void setDefaultListH(List defaultListH) {
		this.defaultListH = defaultListH;
	}
	public List getDefaultListY() {
		return defaultListY;
	}
	public void setDefaultListY(List defaultListY) {
		this.defaultListY = defaultListY;
	}
	public List getDefaultListF() {
		return defaultListF;
	}
	public void setDefaultListF(List defaultListF) {
		this.defaultListF = defaultListF;
	}

	public String getPeriodNm() {
		return periodNm;
	}

	public void setPeriodNm(String periodNm) {
		this.periodNm = periodNm;
	}
	
	
}
