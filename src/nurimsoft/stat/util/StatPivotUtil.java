package nurimsoft.stat.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class StatPivotUtil {

	//주기별 시점 출력 규칙
	public static String generatePrdDe(String prdDe, String period){
		String retStr = null;

		if(period.equals("M")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(4);
		}else if(period.equals("Q")){
			retStr = prdDe.substring(0, 4) + " " + prdDe.substring(5) + "/4";
		}else if(period.equals("H")){
			retStr = prdDe.substring(0, 4) + " " + prdDe.substring(5) + "/2";
		}else if(period.equals("D") || period.equals("T")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(4, 6) + ". " + prdDe.substring(6);
		}else if(period.equals("B")){
			retStr = prdDe.substring(0, 4) + " " + prdDe.substring(5) + "/6 ";
		}else{
			retStr = prdDe;
		}

		return retStr;
	}

	//년/월(분기)표를 위한 월, 분기 출력 규칙
	public static String generatePerYearPrdDe(String prdDe, String period, String dataOpt){
		String retStr = null;

		char lang = 'k';
		if(dataOpt.indexOf("en") > -1){
			lang = 'e';
		}

		if(period.equals("M")){
			if(lang == 'e'){
				retStr = "Month ";
			}else{
				retStr = "월 ";
			}
			retStr += prdDe;
		}else if(period.equals("Q")){
			if(lang == 'e'){
				retStr = "Quarter ";
			}else{
				retStr = "분기 ";
			}
			retStr += prdDe;
		}else{
			retStr = prdDe;
		}

		return retStr;
	}

	//수치가 숫자형태인지 체크
	public static boolean isNumericValue(String paramValue) {

		boolean isNumber = false;
		try {
			Double.parseDouble(paramValue);
			isNumber = true;
		}catch (NumberFormatException ne) {
		}catch (Exception e){
		}

		return isNumber;
	}

	//type 0 : data, 1 : value;
	public static String getNumberFormatString(String word, String periodCo, int type) {

    	StringBuffer buff = new StringBuffer();
    	if(word.startsWith(".")) {
    		word = "0" + word;
    	}

    	if(type == 1){
    		buff.append("#################0");
    	}else{
    		buff.append("###,###,###,###,###,##0");
    	}
    	int period = 0;

    	String result = null;

    	if(periodCo == null){
    		buff.append(".##########");

    		DecimalFormat decFormat = new DecimalFormat(buff.toString());
    		BigDecimal dec = new BigDecimal(word);

    		//dec.setScale(period,BigDecimal.ROUND_HALF_UP);
            result = decFormat.format(dec);
    	}else{
    		period = Integer.parseInt(periodCo);
    		if(period > 0){
    			buff.append(".");
        		for (int i = 0; i < period; i++) {
        			buff.append("0");
        		}
    		}

    		DecimalFormat decFormat = new DecimalFormat(buff.toString());

    		try {
	            //BigDecimal 에서 rounding up 하려는 자리의 값이 0이고 반올림 대상이 되는 값이 5 인경우 안된다..java bug report에도 올라가 있는듯함.
	            int dotIndex = word.indexOf(".");
	            int dotLength = (dotIndex < 0) ? 0 : word.substring(dotIndex + 1).length();
	            String str1 = null;
				String str2 = null;
				String str3 = null;

				if(period == 0){
					if(dotLength > period){
						str1 = word.substring(dotIndex - 1, dotIndex);
						str2 = word.substring(dotIndex + 1, dotIndex + 2);

						if( str2.equals("5") ){
							str3 = word.substring(0, dotIndex) + word.substring(dotIndex, dotIndex + 1) + (Integer.parseInt(str2) + 1);
							word = str3;
						}
					}
				}else{
					if(dotLength > period){
						str1 = word.substring(dotIndex + period, dotIndex + period + 1);
						str2 = word.substring(dotIndex + period + 1, dotIndex + period + 2);

						if( str2.equals("5") ){
							str3 = word.substring(0, dotIndex + period + 1) + (Integer.parseInt(str2) + 1);
							word = str3;
						}
					}
				}

				BigDecimal dec = new BigDecimal(word);

	            //dec.setScale(period,BigDecimal.ROUND_HALF_UP);
	            result = decFormat.format(dec);

	        } catch (Exception e) {
	            result = word;
	        }
    	}
        return result;
    }

	public static String makeStrCmmt(List<String> list){

		String cmmt = "";

		for(int i = 0; i < list.size(); i++){
			if(i == 0){
				cmmt += list.get(i);
			}else{
				cmmt += "<br/>" + list.get(i);
			}
		}

		return cmmt;
	}

	public static String getDateString(){
		Calendar c = Calendar.getInstance();
		String dateString = "" + c.get(Calendar.YEAR)
			+ ( (c.get(Calendar.MONTH) + 1 < 10) ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1) )
			+ ( (c.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH) )
			//+ "_"
			+ ( (c.get(Calendar.HOUR_OF_DAY) < 10) ? "0" + c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY) )
			+ ( (c.get(Calendar.MINUTE) < 10) ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE) )
			+ ( (c.get(Calendar.SECOND) < 10) ? "0" + c.get(Calendar.SECOND) : c.get(Calendar.SECOND) );

		return dateString;
	}

	//Renderer의 메소드를 약간 변형해서 200,000 셀 초과 시 이용하기 위함
	//Renderer의 메소드와 동일하므로 수정 시 양쪽을 다 해줘야함.
	public static String getFormatedMeasure(String[] valArray, String paramPeriodCo, int type){

		String retVal = null;
		String value = valArray[0];

		if(value == null){
			value = "";
		}

		String periodCo = valArray[1];
		String smblCn = valArray[2];
		String unitNm = valArray[3];

		value = value.replaceAll(",", "");
		String period = null;

		boolean isNumber = isNumericValue(value);

		//소수점 자리수
		if(isNumber){
			if(paramPeriodCo == null || paramPeriodCo.trim().length() == 0){
				if(periodCo != null && periodCo.trim().length() > 0){
					period = periodCo;
				}
			}else{
				period = paramPeriodCo;
			}
		}

		if( period != null){
			if( period.equals("99")){	/*2014.07.01 만셀넘는 엑셀다운시는 소수점 여부 관련없이 다보여주도록 - 김경호*/
				period = null;
			}
		}
		
		if(value == null || value.trim().length() == 0){
			if(smblCn == null || smblCn.trim().length() == 0){
				retVal = "";
			}else{
				if(smblCn.equals("x")){
					retVal = "X";
				}else{
					//... 인 경우
					retVal = smblCn;
				}
			}

		}else if(type == 0 && value.equals("99999999999.99999")){
			//99999999999.99999 인경우 미상자료
			retVal = "...";
		}else{
			if(smblCn == null || smblCn.trim().length() == 0){
				retVal = (isNumber) ? getNumberFormatString(value, period, 1) + unitNm : value + unitNm;
			}else{
				if( smblCn.equals("x")){ // 2016.09.13 smbl_cn에 비밀보호코드 (x: 소문자 엑스)로 되어 있을 경우 수치를 X로 치환하여 알 수 없게 한다.(이원영) - 김경호
					retVal = "X";
				}else if( smblCn.equals("...")){ // 2017.08.23 smbl_cn에 미상 (...)로 되어 있을 경우 수치를 (...)로 치환하여 알 수 없게 한다.(이원영) - 김경호
					retVal = "...";
				}else if( smblCn.equals("-")){ // 2017.08.23 smbl_cn에 수치없음 (-)로 되어 있을 경우 수치를 (-)로 치환하여 알 수 없게 한다.(이원영) - 김경호
					retVal = "-";
				}else{
					retVal = (isNumber) ? getNumberFormatString(value, period, 1) + smblCn + unitNm : value + smblCn + unitNm;
				}
			}
		}
		return retVal;
	}

	//영수 영문화 리스트 egovMap용
	@SuppressWarnings("unchecked")
	public static List<EgovMap> listViewNm(List<EgovMap> list, String attrType, String dataOpt){
		List<EgovMap> transformList = list ;
		for(int i=0; i<transformList.size();i++){
			Map<String, String> transMap = new HashMap<String, String>();
			String classStr="";
			transMap = (Map<String, String>) transformList.get(i);
			classStr = dataOptViewNm(transMap,attrType,dataOpt);
			transMap.remove("scrKor");
			transMap.put("scrKor",classStr);
			transformList.set(i,(EgovMap) transMap);
		}
		return transformList;
	}


	//영문,한글,코드세팅 egovMap용
	@SuppressWarnings("unused")
	public static String dataOptViewNm(Map<String, String> tmMap, String attrType, String dataOpt){
		String viewNm="";
		String compareDpt = dataOpt;
		String compareType = attrType;
		if(compareType.equals("object")){
			if(compareDpt.equals("ko")){
				viewNm = tmMap.get("scrKor");
			}else if(compareDpt.equals("en")){
				viewNm = tmMap.get("scrEng");
			}else if(compareDpt.equals("cd")){
				viewNm = tmMap.get("objVarId");
			}else if(compareDpt.equals("cdko")){
				viewNm = tmMap.get("objVarId")+" "+tmMap.get("scrKor");
			}else{
				viewNm = tmMap.get("objVarId")+" "+tmMap.get("scrEng");
			}
		}else{
			if(compareDpt.equals("ko")){
				viewNm = tmMap.get("scrKor");
			}else if(compareDpt.equals("en")){
				viewNm = tmMap.get("scrEng");
			}else if(compareDpt.equals("cd")){
				viewNm = tmMap.get("itmId");
			}else if(compareDpt.equals("cdko")){
				viewNm = tmMap.get("itmId")+" "+tmMap.get("scrKor");
			}else{
				viewNm = tmMap.get("itmId")+" "+tmMap.get("scrEng");
			}
		}
		return viewNm;
	}


	//영수 영문화 리스트 hashMap용
	@SuppressWarnings("unchecked")
	public static List listViewNm2(List list, String attrType, String dataOpt){
		List transformList = list ;
		for(int i=0; i<transformList.size();i++){
			Map<String, String> transMap = new HashMap<String, String>();
			String classStr="";
			transMap = (Map<String, String>) transformList.get(i);
			classStr = dataOptViewNm2(transMap,attrType,dataOpt);
			transMap.remove("SCR_KOR");
			transMap.put("SCR_KOR",classStr);
			//System.out.println("classStr+++"+classStr);
			transformList.set(i, transMap);
		}
		return transformList;
	}


	//영문,한글,코드세팅 hasMap용
	@SuppressWarnings("unused")
	public static String dataOptViewNm2(Map<String, String> tmMap, String attrType, String dataOpt){
		String viewNm="";
		String compareDpt = dataOpt;
		String compareType = attrType;
		if(compareType.equals("object")){
			if(compareDpt.equals("ko")){
				viewNm = tmMap.get("SCR_KOR");
			}else if(compareDpt.equals("en")){
				viewNm = tmMap.get("SCR_ENG");
			}else if(compareDpt.equals("cd")){
				viewNm = tmMap.get("OBJ_VAR_ID");
			}else if(compareDpt.equals("cdko")){
				viewNm = tmMap.get("OBJ_VAR_ID")+" "+tmMap.get("SCR_KOR");
			}else{
				viewNm = tmMap.get("OBJ_VAR_ID")+" "+tmMap.get("SCR_ENG");
			}
		}else{
			if(compareDpt.equals("ko")){
				viewNm = tmMap.get("SCR_KOR");
			}else if(compareDpt.equals("en")){
				viewNm = tmMap.get("SCR_ENG");
			}else if(compareDpt.equals("cd")){
				viewNm = tmMap.get("ITM_ID");
			}else if(compareDpt.equals("cdko")){
				viewNm = tmMap.get("ITM_ID")+" "+tmMap.get("SCR_KOR");
			}else{
				viewNm = tmMap.get("ITM_ID")+" "+tmMap.get("SCR_ENG");
			}
		}
		return viewNm;
	}
}
