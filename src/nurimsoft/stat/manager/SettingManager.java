package nurimsoft.stat.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.ClassInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.info.PivotInfo;
import nurimsoft.stat.info.StatInfo;
import nurimsoft.stat.util.MessageManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SettingManager {

protected Log log = LogFactory.getLog(this.getClass());

	protected boolean usingMyScrap;	//default false;

	protected StatInfo statInfo;
	protected StatHtmlDAO statHtmlDAO;
	//Pivot 정보
	protected PivotInfo pivotInfo;

	protected ParamInfo paramInfo;
	protected String olapStl;
	protected String limitYn;

	protected String[] prdInfoArr;

	//olap_stl의 주기와 시점수
	protected String prdInfo;
	protected int timeCnt;
	//계층별  컬럼 보기값(F : 계층별 컬럼 보기 아님, S : 초기는 설정 - 해제  가능, T : 설정- 해제기능 없음)
	protected String levelExpr = "F";	//default = "F"

	protected int maxRow;

	protected long mixCnt = 1l;	//조합수

	//classInfoList를 sn(순서)에 맞도록 정렬한 list
	protected List sortedClassInfoList;

	//계층별 컬럼보기 기 분류수에 따른 분류값 MAX 수
	protected int maxRowForLevel;

	//서비스목록에서  넘어오는 특정 분류의 분류값
	private String obj_var_id;
	private String itm_id;

	private char firstChar = '0';

	public SettingManager(StatInfo statInfo, StatHtmlDAO statHtmlDAO){
		this.statInfo = statInfo;
		this.statHtmlDAO = statHtmlDAO;
		this.paramInfo = this.statInfo.getParamInfo();
		this.olapStl = this.statInfo.getOlapStl();
		this.obj_var_id = paramInfo.getObj_var_id();
		this.itm_id = paramInfo.getItm_id();

		maxRow = Integer.parseInt(PropertyManager.getInstance().getProperty("grid.maxrow"));

		List<Map> prdInfoList = statHtmlDAO.getPrdInfoArr(paramInfo.getDbUser());
		prdInfoArr = new String[prdInfoList.size()];
		for(int i = 0; i < prdInfoList.size(); i++){
			Map map = prdInfoList.get(i);
			prdInfoArr[i] = (String)map.get("PRD_SE");
		}
	}

	public void setCondition(){}

	//통계표 화면정보를 이용하여 pivotInfo 셋팅
	public void setPivotInfo(List<Map> scrInfoList){
		pivotInfo = new PivotInfo();

		List<ClassInfo> classInfoList = statInfo.getClassInfoList();

		List colList = pivotInfo.getColList();
		List colNmList = pivotInfo.getColNmList();
		List colEngNmList = pivotInfo.getColEngNmList();
		List rowList = pivotInfo.getRowList();
		List rowNmList = pivotInfo.getRowNmList();
		List rowEngNmList = pivotInfo.getRowEngNmList();

		String func = "";	//분석을 위한 변수

		//화면정보가 있으면
		if(scrInfoList != null && scrInfoList.size() > 0){
			log.info("화면정보에서 피봇정보 셋팅");
			Map map = null;
			String arrySe = null;
			String code = null;
			String colName = null;

			//분석
			func = (String)((Map)scrInfoList.get(0)).get("FUNC");

			for(int i = 0; i < scrInfoList.size(); i++){
				map = scrInfoList.get(i);
				arrySe = (String)map.get("ARRY_SE");
				code = (String)map.get("CODE");
				colName = (String)map.get("COLNAME");

				//표두 : 1212510, 표측 : 1212511
				if(arrySe.equals("1212510")){
					if(colName.equals("prd_de")){
						colList.add("TIME");
						colNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotTime", paramInfo.getDataOpt()));
						colEngNmList.add("TIME");
					}else if(colName.equals("measure_cd")){
						colList.add("ITEM");
						colNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotItem", paramInfo.getDataOpt()));
						colEngNmList.add("ITEM");
					}else{
						//공표구분적용
						for(int j = 0; j < classInfoList.size(); j++){
							ClassInfo classInfo = classInfoList.get(j);
							if(classInfo.getClassId().equals(code) && classInfo.isVisible()){
								colList.add(code);
								colNmList.add(classInfo.getClassNm());
								colEngNmList.add(classInfo.getClassEngNm());
								break;
							}
						}
					}

				}else if(arrySe.equals("1212511")){
					if(colName.equals("prd_de")){
						rowList.add("TIME");
						rowNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotTime", paramInfo.getDataOpt()));
						rowEngNmList.add("TIME");
					}else if(colName.equals("measure_cd")){
						rowList.add("ITEM");
						rowNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotItem", paramInfo.getDataOpt()));
						rowEngNmList.add("ITEM");
					}else{
						//공표구분적용
						for(int j = 0; j < classInfoList.size(); j++){
							ClassInfo classInfo = classInfoList.get(j);
							if(classInfo.getClassId().equals(code) && classInfo.isVisible()){
								rowList.add(code);
								rowNmList.add(classInfo.getClassNm());
								rowEngNmList.add(classInfo.getClassEngNm());
								break;
							}
						}
					}
				}
			}

			//2014.02.06 분석 처리
			applyFunc(func);

		//화면정보가 없으면
		}else{
			log.info("기본통계표로 피봇정보 셋팅");
			//표두에 시점, 항목 추가(항목이 1개인 경우에는 적용하지 않음)
			colList.add("TIME");
			colNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotTime", paramInfo.getDataOpt()));
			colEngNmList.add("TIME");

			//2013.12.17 CHAR_ITM_CO대신 공표구분 적용한 항목 수로 한다.
			//if(statInfo.getItemCnt() > 1){
			if(statInfo.getItemInfo().getItmCnt() > 1){
				colList.add("ITEM");
				colNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotItem", paramInfo.getDataOpt()));
				colEngNmList.add("ITEM");
			}

			//표측에 분류추가, VAR_ORD_CUR 적용해야 함.
			sortedClassInfoList = new ArrayList();
			for(int i = 0; i < classInfoList.size(); i++){
				ClassInfo classInfo = classInfoList.get(i);
				//공표구분 적용
				if(classInfo.isVisible()){
					sortedClassInfoList.add(classInfo);
				}
			}

			//VAR_ORD_CUR으로 정렬
			Collections.sort(sortedClassInfoList);
			for(int i = 0; i < sortedClassInfoList.size(); i++){
				ClassInfo classInfo = (ClassInfo)sortedClassInfoList.get(i);
				rowList.add(classInfo.getClassId());
				rowNmList.add(classInfo.getClassNm());
				rowEngNmList.add(classInfo.getClassEngNm());
			}

		}

		log.info("col and row List's size ::: " + pivotInfo.getColList().size() + "," + pivotInfo.getRowList().size());
		/*
		for(int i = 0; i < pivotInfo.getRowList().size(); i++){
			System.out.println(pivotInfo.getRowList().get(i));
		}
		*/
	}

	public void applyFunc(String func){
		//try{
			//2014.02.06 분석 처리
			if(func != null && func.trim().length() > 0){

				paramInfo.setDoAnal("Y");

				String[] funcList = null;
				String FUNC_CODE = null;
				String FUNC_PRD_CODE = null;
				String FUNC_STD_PRD = null;

				funcList = func.split(":");
		        FUNC_CODE = funcList[0];
		        FUNC_PRD_CODE = funcList[1];

		        // 기준시점비
		        if ( FUNC_PRD_CODE.indexOf("ONE_Y") > -1 || FUNC_PRD_CODE.indexOf("ONE_M") > -1 || FUNC_PRD_CODE.indexOf("ONE_Q") > -1 || FUNC_PRD_CODE.indexOf("ONE_H") > -1 || FUNC_PRD_CODE.indexOf("ONE_F") > -1) {
		            FUNC_PRD_CODE = funcList[1].substring(0, 5);
		            FUNC_STD_PRD = funcList[1].substring(5);

		            paramInfo.setAnalType(FUNC_CODE);
					paramInfo.setAnalCmpr(FUNC_PRD_CODE);
					paramInfo.setAnalTime(FUNC_STD_PRD);
		        }
		        
		        // 증감기여도, 증감기여율
		        if(FUNC_CODE.equals("CHG_RATE_CO") || FUNC_CODE.equals("CHG_RATE_CO_R")) {
		        	paramInfo.setAnalType(FUNC_CODE);
					paramInfo.setAnalCmpr(FUNC_PRD_CODE);
		        	paramInfo.setAnalClass(funcList[2]);
		            paramInfo.setAnalItem(funcList[3]);
				}
		        // 구성비, 누계구성비
		        else if(FUNC_CODE.equals("CMP_RATE") || FUNC_CODE.equals("TOTL_CMP_RATE")){

		        	String DimMsr_code = StringUtils.defaultString(funcList[2]);

		        	if (DimMsr_code.equals("2")) {
		                String[] cmpList = funcList[3].split("_");
		                String cmp_objid0 = cmpList[0];
		                String cmp_itmid0 = cmpList[1];

		                paramInfo.setAnalClass("ITEM,");
		                paramInfo.setAnalItem(cmp_itmid0 + ",");

		            } else {

		            	String[] cmpList = null;

		            	StringBuffer strBuffClass = new StringBuffer();
		            	StringBuffer strBuffItem = new StringBuffer();

		            	for(int i = 3; i < funcList.length; i++){
		            		cmpList = funcList[i].split("_");

		            		if(cmpList.length == 2 && cmpList[0].length() > 0 && cmpList[1].length() > 0){
		            			//System.out.println("cmpList[" + i + "] ::: " + cmpList[0] + "," + cmpList[1]);
		            			if(cmpList[0].equals("13999001")){
		            				strBuffClass.append("ITEM,");
		            			}else{
		            				strBuffClass.append(cmpList[0] + ",");
		            			}
			            		strBuffItem.append(cmpList[1] + ",");
			            	}
		            	}

		            	paramInfo.setAnalType(FUNC_CODE);
		            	paramInfo.setAnalClass(strBuffClass.toString());
		            	paramInfo.setAnalItem(strBuffItem.toString());
		            }
				}
		        else{
					paramInfo.setAnalType(FUNC_CODE);
					paramInfo.setAnalCmpr(FUNC_PRD_CODE);
				}
			}
		//}catch(Exception e){
		//	e.printStackTrace();
		//}catch(Error e){
		//	e.printStackTrace();
		//}
	}

	public void setOlapStl(){
		//01. olap_stl null 체크

		if(olapStl == null){
			//계층별 컬럼여부는 F로 설정하고 분류수를 참고하여 해당 분류레벨값을 1로 셋팅한다.
			//주기와 시점은 뒤에서 결정해야 하므로 "0", "00"으로 셋팅한다.
			StringBuffer tmpOlapStl = new StringBuffer();
			tmpOlapStl.append("0F").append("00000000").append("0").append("00");

			olapStl = tmpOlapStl.toString();
		}

		//02.주기 정보 확인
		Arrays.sort(prdInfoArr);
		//주기값 적합여부 판별하여 적합하지 않으면 해당 통계표가 가지고 있는 최단 주기를 구해야 함.
		if( Arrays.binarySearch(prdInfoArr, olapStl.substring(10, 11)) == -1 ){
			String defaultPeriod = statHtmlDAO.getDefaultPeriod(paramInfo);
			olapStl = olapStl.substring(0, 10) + defaultPeriod + olapStl.substring(11);
		}

		prdInfo = olapStl.substring(10, 11);

		//03.시점 정보 확인
		//시점 정보에 있는 값을 확인하여 00, 99, 숫자 이외의 값인 경우 없다고 판단하고 규칙에 따라 시점수를 결정한다.

		//2015.11.12 시점 조회시 만셀제한 해제가 추가 되면서 자릿수를 제대로 가져오도록
		//String tmpTimeCnt = olapStl.substring(11);
		String tmpTimeCnt = olapStl.substring(11,13);
		String tmpTimeTrans = (tmpTimeCnt.startsWith("0")) ? tmpTimeCnt.substring(1) : tmpTimeCnt;
		int timeInt = 0;
		boolean isCorrect = false;	//시점 수 적합성 판단
		if(!tmpTimeCnt.equals("00") && !tmpTimeCnt.equals("99")){
			try{
				timeInt = Integer.parseInt(tmpTimeTrans);

				if(timeInt > 0){
					isCorrect = true;
				}

			}catch(NumberFormatException e){}
		}

		if(isCorrect){
			timeCnt = timeInt;

		}else{
			//주기와 화면정보를 이용하여 시점수를 뽑는다.

			/*
			 - PivotInfo를 이용하고, 다음의 규칙을 적용한다.
			   1.표두가 1개이면서 시점만 있는 경우(TIME)는 다음과 같이 시점수를 정한다.
				D:10, T:3, M:6, B:3, Q:2, H:2, Y:3, F:3
			   2.그 이외의 경우 (표두에 시점+α가 오는 경우 (α : 항목 또는 분류) )
			    D:3, T:3, M:3, B:3, Q:2, H:2, Y:1, F:1
			*/

			boolean onlyTime = false;	//1번의 케이스인지를 판별하기 위한 변수
			if(pivotInfo.getColList().size() == 1 && pivotInfo.getColList().get(0).toString().equals("TIME")){
				onlyTime = true;
			}

			char prdSe = prdInfo.charAt(0);
			switch(prdSe){
				case 'D' :
					timeCnt = (onlyTime) ? 10 : 3;
					break;
				case 'T' :
					timeCnt = (onlyTime) ? 3 : 3;
					break;
				case 'M' :
					timeCnt = (onlyTime) ? 6 : 3;
					break;
				case 'B' :
					timeCnt = (onlyTime) ? 3 : 3;
					break;
				case 'Q' :
					timeCnt = (onlyTime) ? 2 : 2;
					break;
				case 'H' :
					timeCnt = (onlyTime) ? 2 : 2;
					break;
				case 'Y' :
					timeCnt = (onlyTime) ? 3 : 1;
					break;
				case 'F' :
					timeCnt = (onlyTime) ? 3 : 1;
					break;
			}

			/*2018.02.06
			 * 2015.03.09 오종민 과장이 작업한 2만셀제한 기능이 호스팅에서 작동하지 않는 부분을 확인하여 수정
			 * olapStl의 길이가 14일 경우에 한해 14번째 자리를 복원시켜줘야 함 - 김경호
			 * */
			if(timeCnt >= 10){
				if(paramInfo.getServerTypeOrigin().equals("stat")){	
					if(olapStl.length() <= 13){
						olapStl = olapStl.substring(0, 11) + (timeCnt + "");
					}else{
						olapStl = olapStl.substring(0, 11) + (timeCnt + "") + olapStl.substring(13, 14);
					}
				}else{ //서비스는 2만셀 조회를 적용하지 않음으로 기존 소스 그대로
					olapStl = olapStl.substring(0, 11) + (timeCnt + "");
				}
			}else{
				if(paramInfo.getServerTypeOrigin().equals("stat")){
					if(olapStl.length() <= 13){
						olapStl = olapStl.substring(0, 11) + ("0" + timeCnt);
					}else{
						olapStl = olapStl.substring(0, 11) + ("0" + timeCnt)  + olapStl.substring(13, 14);
					}
				}else{ //서비스는 2만셀 조회를 적용하지 않음으로 기존 소스 그대로
					olapStl = olapStl.substring(0, 11) + ("0" + timeCnt);
				}
			}
		}

		levelExpr = olapStl.substring(1, 2);
		firstChar = olapStl.substring(0, 1).charAt(0);

		statInfo.setLevelExpr(levelExpr);

		log.info("olapStl ::: " + olapStl);

	}

	protected void setInfosUsingOlapStl(){
		//분류X항목조합수(DIM_CO) 수와 시점수를 조합해서 10,000셀 이하의 경우, 10,000 이상의 경우에 따라
		//각 분류, 항목, 시점 범위를 해당정보를 가지고 있는 클래스에 지정한다.

		Map paramMap = null;
		//각 분류의 해당 레벨하위 분류값수를 구한다.
		List classInfoList = statInfo.getClassInfoList();
		int[] levelCntArr = new int[classInfoList.size()];
		ClassInfo classInfo = null;

		boolean isFirstCnt = false;
		for(int i = 0; i < classInfoList.size(); i++){

			classInfo = (ClassInfo)classInfoList.get(i);

			//분류가 비공개일 경우 분류값 수는 1로 한다.
			if(!classInfo.isVisible()){
				levelCntArr[i] = 1;
				continue;
			}

			//System.out.println("@@@@@@@@@@@@@ ::: firstChar ::: " + firstChar);

			if(!isFirstCnt){
				isFirstCnt = true;
				if(firstChar == 'A'){
					levelCntArr[i] = 1;
					continue;
				}
			}else{
				if(firstChar == 'B'){
					levelCntArr[i] = 1;
					continue;
				}
			}

			String tmpLevel = olapStl.substring(i + 2, i + 3);
			boolean A_levelChk	= false; // 레벨값에 A가 들어갔는지 체크
			
			/*
			 * 2016.10.13 - 김경호
			 * 초기조회조건 레벨값에 A 가 들어가 있으면 무조건 1레벨에서 1개만 가져온다
			 */
			if( tmpLevel.equals("A")){
				tmpLevel = "0";
				A_levelChk = true;
			}
			
			int tmpLevelInt = 0;
			try{
				tmpLevelInt = Integer.parseInt(tmpLevel);
			}catch(NumberFormatException e){}

			tmpLevelInt += 1;	//실제 레벨 값은 olap_stl에서 가지고 있는 값 + 1

			paramMap = new HashMap();
			paramMap.put("orgId", paramInfo.getOrgId());
			paramMap.put("tblId", paramInfo.getTblId());
			paramMap.put("serverType", paramInfo.getServerType());
			paramMap.put("dbUser", paramInfo.getDbUser());
			paramMap.put("objVarId", classInfo.getClassId());
			paramMap.put("level", tmpLevelInt);
			paramMap.put("levelCondition", -1);
			paramMap.put("totIgnrAt", classInfo.getTotIgnrAt());

			if( classInfo.getClassId().equals(StringUtils.defaultString(obj_var_id, "")) ){
				paramMap.put("itm_id", itm_id);
			}

			levelCntArr[i] = statHtmlDAO.getLevelCnt(paramMap);
			
			/*
			 * 2016.10.13 - 김경호
			 * [분류별 레벨설정에 A가 들어가있을 경우 무조건 1건의 분류만 가져오도록 정의 - 이원영]
			 * 초기조회조건 레벨값에 A 가 들어가 있으면 일단 1레벨로 가정하여 기존 프로세스대로 분류값 갯수를 조회한후
			 * 그 갯수가 0보다 클 경우 무조건 1개만 가져온다
			 */
			if( A_levelChk == true && levelCntArr[i] > 0){
				levelCntArr[i] = 1;
			}
		}

		//분류 조합
		for(int i = 0; i < levelCntArr.length; i++){
			mixCnt *= levelCntArr[i];
		}

		//항목 조합 추가
		mixCnt *= statInfo.getItemInfo().getItmCnt();

		log.info("mixCnt ::: " + mixCnt);

		//조합수를 계산하여 maxRow보다 큰 경우 dim_co와 비교한다.
		//1. 조합수(dim_co 포함. 이하 동일)가 10,000 초과한 경우 계층별 컬럼 보기값에 따라서 분류값 수를 조절하고 단일시점 적용함.
		// 1.1. 계층별 컬럼보기가 아닌 경우(F) > 항목 전체, 첫번째 분류 1레벨 전체 / 항목수, 두번째 이후는 1레벨의 첫번째 분류값
		// 1.2. 계층별 컬럼보기의 경우(T or S)
		//   분류개수에 따라 다음과 같이 적용(아래의 최대 분류값 수 / 항목 수)
		//   1 : 10,000, 2 : 100, 3 : 20, 4 : 10, 5 : 6, 6 : 5, 7 : 3, 8 : 3
		//2. 조합수가 10,000 이하인 경우 olap_stl의 시점수를 적용하되 시점까지의 조합수에 따라서 다음과 같이 처리
		// 2.1 조합수 X 시점수가 10,000 이하인 경우 > olap_stl 시점수 적용
		// 2.2 조합수 X 시점수가 10,000 초과한 경우 > 단일시점 적용

		int itemMultiplyObj01Cnt = 0; //1레벨 전체 * 항목 수
		int itemCnt = statInfo.getItemCnt();

		//2013.12.06 dimCo수를 체크하지 않도록 변경
		//TODO dimCo수 다시 조정 시 주석위치 변경

		//2015.03.05 OLAP_STL null인 데이터 처리
		if(olapStl == null){
			limitYn = "0";
		}else{
			if(olapStl.length() <= 13){ // 기존 13자리인 경우 기본값으로 '0'
				limitYn = "0";
			}else {
				if (mixCnt > 20000){ // 2015.03.09 조합수가 20000이 넘을 경우 기존 조회규칙 적용을 위해 '0'
					limitYn = "0";
				}else{
					limitYn = olapStl.substring(13, 14); // 14자리인경우 마지막 한자리 가져옮
				}
			}
		}

		//2015.03.05 만셀 초과시 조회 제한을 위하여 조건 추가 && !levelExpr.equals("1")
		if(mixCnt > maxRow && !limitYn.equals("1")){
		//if(mixCnt > maxRow && statInfo.getDimCo() > maxRow){
			//조합수가 maxRow보다 클 경우
			if(levelExpr.equals("F")){
				//계층별 컬럼보기가 아닌 경우 : 첫번째 분류 1레벨 전체, 나머지 분류 1레벨의 첫번째 분류값, 단일항목, 단일시점 적용
				//분류정보 생성
				boolean isFirst = false;
				for(int i = 0; i < classInfoList.size(); i++){
					classInfo = (ClassInfo)classInfoList.get(i);

					//비공개인경우 skip 하고 다음으로 진행(변경)
					//함수값 여부가 'Y'인 것 담는다.
					if(!classInfo.isVisible()){
						setClassInfoUnvisible(classInfo);
						continue;
					}

					if(!isFirst){
						//1레벨 전체 * 항목 수가 maxRow보다 클 경우 전체분류수 / 항목수만큼 보여준다.
						Map tmpMap = new HashMap();
						tmpMap.put("orgId", paramInfo.getOrgId());
						tmpMap.put("tblId", paramInfo.getTblId());
						tmpMap.put("serverType", paramInfo.getServerType());
						tmpMap.put("dbUser", paramInfo.getDbUser());
						tmpMap.put("objVarId", classInfo.getClassId());
						tmpMap.put("level", 1);
						tmpMap.put("levelCondition", 0);
						paramMap.put("totIgnrAt", classInfo.getTotIgnrAt());

						if( classInfo.getClassId().equals(StringUtils.defaultString(obj_var_id, "")) ){
							tmpMap.put("itm_id", itm_id);
						}

						int firstLevleCnt = statHtmlDAO.getLevelCnt(tmpMap);

						itemMultiplyObj01Cnt = itemCnt * firstLevleCnt;
						if(itemMultiplyObj01Cnt > maxRow){
							setClassInfo(classInfo, 1, -1, firstLevleCnt / itemCnt);
						}else{
							setClassInfo(classInfo, 1, -1, 0);
						}

						isFirst = true;
					}else{
						//1레벨에서 첫번째 값
						setClassInfo(classInfo, 1, -1, 1);
					}
				}

				//항목정보 생성
				setItemInfo(0);

				//스크랩인 경우(SIGA로부터 저장된 스크랩정보에 한함)에는 별도로 시점을 셋팅한다.
				if(!usingMyScrap){
					//시점정보생성
					setPeriodInfo(1, prdInfo);
				}

			}else{
				statInfo.setMixCnt(mixCnt);
				//계층별  컬럼보기의 경우
				//공개인 분류수를 산정한다.
				int objCnt = 0;
				for(int i = 0; i < classInfoList.size(); i++){
					classInfo = (ClassInfo)classInfoList.get(i);

					if(classInfo.isVisible()){
						objCnt++;
					}
				}

				maxRowForLevel = Integer.parseInt(PropertyManager.getInstance().getProperty("grid.level.maxrow." + objCnt));
				int objItemMaxCnt = maxRowForLevel / statInfo.getItemCnt();

				//분류정보 생성
				boolean isFirst = false;
				for(int i = 0; i < classInfoList.size(); i++){

					int applyObjItemMaxCnt = objItemMaxCnt;
					if(objItemMaxCnt <= 0){
						applyObjItemMaxCnt = 1;
					}

					classInfo = (ClassInfo)classInfoList.get(i);

					//비공개인경우 처리
					if(!classInfo.isVisible()){
						setClassInfoUnvisible(classInfo);
						continue;
					}

					int maxLevel = getMaxLevel(classInfo);

					//첫번째 분류의 경우에는 applyObjItemMaxCnt 마지막 레벨의 분류값  수 / 항목 수를 적용한다.
					if(!isFirst){

						if(objItemMaxCnt <= 0){
							Map tmpMap = new HashMap();
							tmpMap.put("orgId", paramInfo.getOrgId());
							tmpMap.put("tblId", paramInfo.getTblId());
							tmpMap.put("serverType", paramInfo.getServerType());
							tmpMap.put("dbUser", paramInfo.getDbUser());
							tmpMap.put("objVarId", classInfo.getClassId());
							tmpMap.put("level", maxLevel);
							tmpMap.put("levelCondition", 0);
							paramMap.put("totIgnrAt", classInfo.getTotIgnrAt());

							if( classInfo.getClassId().equals(StringUtils.defaultString(obj_var_id, "")) ){
								tmpMap.put("itm_id", itm_id);
							}

							int maxCnt = statHtmlDAO.getLevelCnt(tmpMap);
							applyObjItemMaxCnt = maxCnt / statInfo.getItemCnt();
						}

						isFirst = true;
					}

					setClassInfo(classInfo, maxLevel, 0,  applyObjItemMaxCnt);
				}

				//항목정보 생성
				setItemInfo(0);

				//스크랩인 경우(SIGA로부터 저장된 스크랩정보에 한함)에는 별도로 시점을 셋팅한다.
				if(!usingMyScrap){
					//시점정보 생성
					setPeriodInfo(1, prdInfo);
				}

			}
		}else{
			//조합수 보다 작거나 같을 경우

			//분류정보 생성
			boolean isFirstMake = false;
			for(int i = 0; i < classInfoList.size(); i++){
				classInfo = (ClassInfo)classInfoList.get(i);

				//비공개인경우 처리
				if(!classInfo.isVisible()){
					setClassInfoUnvisible(classInfo);
					continue;
				}

				if(!isFirstMake){
					isFirstMake = true;
					if(firstChar == 'A'){
						setClassInfo(classInfo, 1, 0, 1);
						continue;
					}
				}else{
					if(firstChar == 'B'){
						setClassInfo(classInfo, 1, 0, 1);
						continue;
					}
				}

				String tmpLevel = olapStl.substring(i + 2, i + 3);
				
				/*
				 * 2016.10.13 - 김경호
				 * 초기조회조건 레벨값에 A 가 들어가 있으면 무조건 1레벨에서 1개만 가져온다
				 */
				if( tmpLevel.equals("A")){
					tmpLevel = "0";
					setClassInfo(classInfo, Integer.parseInt(tmpLevel) + 1, -1, 1);
				}else{
					setClassInfo(classInfo, Integer.parseInt(tmpLevel) + 1, -1, 0);
				}
				
			}

			//항목정보 생성
			setItemInfo(0);

			//스크랩인 경우(SIGA로부터 저장된 스크랩정보에 한함)에는 별도로 시점을 셋팅한다.
			//스크랩이 아닌 경우 반드시 수행
			if(!usingMyScrap){
				if(mixCnt * timeCnt > maxRow){
					//조합수 X 시점수가 maxRow보다 클 경우	: 단일시점 적용
					setPeriodInfo(1, prdInfo);
				}else{
					//조합수 X 시점수가 maxRow보다 작거나 같을 경우 : 시점개수 적용
					setPeriodInfo(timeCnt, prdInfo);
				}
			}
		}
	}

	/**
	 * 분류정보 생성(ClassInfo)
	 * @param classInfo
	 * @param level
	 * @param maxCnt : 0이면 전체, 그렇지 않으면 해당 수만큼 적용(CHAR_ITM_SN)
	 * @param levelCondition : 0 : 동일한 레벨, -1 : 레벨 이하, 1 : 레벨이상
	 */
	public void setClassInfo(ClassInfo classInfo, int level, int levelCondition, int maxCnt){
		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("objVarId", classInfo.getClassId());
		paramMap.put("level", level);
		paramMap.put("levelCondition", levelCondition);
		paramMap.put("totIgnrAt", classInfo.getTotIgnrAt());

		if( classInfo.getClassId().equals(StringUtils.defaultString(obj_var_id, "")) ){
			if(level > 1) {
				paramMap.put("lv2", "Y");
				paramMap.put("itm_id", itm_id);	
			}else {
				paramMap.put("lv2", "N");
				paramMap.put("itm_id", itm_id);	
			}			
		}

		List list = null;
		if(maxCnt == 0){
			list = statHtmlDAO.getClassItemListAll(paramMap);
		}else{
			paramMap.put("maxCnt", maxCnt);
			list = statHtmlDAO.getClassItemListLimited(paramMap);
		}

		List defaultItmList = new ArrayList();
		Map tmpMap = null;
		for(int i = 0; i < list.size(); i++){
			tmpMap = (Map)list.get(i);
			defaultItmList.add(paramMap.get("objVarId")+"^"+(BigDecimal)tmpMap.get("LVL") + "#" + (String)tmpMap.get("ITM_ID"));
		}


		classInfo.setDefaultItmList(defaultItmList);
		classInfo.setDefaultItmCnt(defaultItmList.size());

	}

	/**
	 * 분류정보 생성 - 비공개인 분류(ClassInfo)
	 * @param classInfo
	 * @param level
	 * @param maxCnt : 0이면 전체, 그렇지 않으면 해당 수만큼 적용(CHAR_ITM_SN)
	 * @param levelCondition : 0 : 동일한 레벨, -1 : 레벨 이하, 1 : 레벨이상
	 */
	public void setClassInfoUnvisible(ClassInfo classInfo){
		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("objVarId", classInfo.getClassId());

		String itmId = statHtmlDAO.getClassItemListUnvisible(paramMap);

		List defaultItmList = new ArrayList();
		//함수값 여부는 1레벨에만 존재함
		defaultItmList.add(paramMap.get("objVarId")+"^"+"1#" + itmId);

		classInfo.setDefaultItmList(defaultItmList);
		classInfo.setDefaultItmCnt(defaultItmList.size());

	}

	/**
	 * 항목정보 생성(ItemInfo)
	 * @param maxCnt : 0이면 전체, 그렇지 않으면 해당 수만큼 적용(CHAR_ITM_SN, ITM_ID)
	 */
	public void setItemInfo(int maxCnt){
		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());

		List list = null;
		if(maxCnt == 0){
			list = statHtmlDAO.getItemListAll(paramMap);
		}else{
			paramMap.put("maxCnt", maxCnt);
			list = statHtmlDAO.getItemListLimited(paramMap);
		}

		List defaultItmList = new ArrayList();
		Map tmpMap = null;
		for(int i = 0; i < list.size(); i++){
			tmpMap = (Map)list.get(i);
			defaultItmList.add((String)tmpMap.get("ITM_ID"));
		}

		statInfo.getItemInfo().setDefaultItmList(defaultItmList);
		statInfo.getItemInfo().setDefaultItmCnt(defaultItmList.size());

		//check
		/*
		for(int i = 0; i < statInfo.getItemInfo().getDefaultItmList().size(); i++){
			log.info("ITM_ID ::: " + (String)statInfo.getItemInfo().getDefaultItmList().get(i));
		}
		*/
	}

	//분류의 max level 가져오기
	public int getMaxLevel(ClassInfo classInfo){
		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("objVarId", classInfo.getClassId());

		return statHtmlDAO.getMaxLevel(paramMap);
	}

	/**
	 * 시점정보 생성(PeriodInfo)
	 * * @param periodCnt
	 */
	public void setPeriodInfo(int periodCnt, String prdSeStr){
		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("periodCnt", periodCnt);
		paramMap.put("prdSe", prdSeStr);

		char prdSe = prdSeStr.charAt(0);

		//해당 통계표의 시작시점 및 최종시점을 맵에 셋팅한다.(StatInfoManager로부터 셋팅된 값)
		paramMap = getMinMaxPrdDe(paramMap, prdSe);

		PeriodInfo periodInfo = statInfo.getPeriodInfo();

		//2015.06.05 상속통계표
		if( "Y".equals(paramInfo.getInheritYn()) ){
			paramMap.put("inheritYn",   paramInfo.getInheritYn());
			paramMap.put("originOrgId", paramInfo.getOriginOrgId());
			paramMap.put("originTblId", paramInfo.getOriginTblId());
		}
		
		/* 
		 20.04.09 업무용에서 담당자가 st 파라미터를 이용하여 서비스용으로 조회할때 원래 serverType은 관리자 였으므로 기간보안 체크안함
		(손상호 주무관의 요청에 따라 [미리보는KOSIS]에서는 기간보안 체크안함.
		 */
		paramMap.put("serverTypeOrigin",paramInfo.getServerTypeOrigin());
		
		List list = statHtmlDAO.getPeriodList(paramMap);

		List defaultList = new ArrayList();
		Map tmpMap = null;

		for(int i = 0; i < list.size(); i++){
			tmpMap = (Map)list.get(i);
			defaultList.add((String)tmpMap.get("PRD_DE"));

			if(i == 0){
				//defaultStart
				switch(prdSe){
					case 'D' :
						periodInfo.setDefaultEndD((String)tmpMap.get("PRD_DE"));
						break;
					case 'T' :
						periodInfo.setDefaultEndT((String)tmpMap.get("PRD_DE"));
						break;
					case 'M' :
						periodInfo.setDefaultEndM((String)tmpMap.get("PRD_DE"));
						break;
					case 'B' :
						periodInfo.setDefaultEndB((String)tmpMap.get("PRD_DE"));
						break;
					case 'Q' :
						periodInfo.setDefaultEndQ((String)tmpMap.get("PRD_DE"));
						break;
					case 'H' :
						periodInfo.setDefaultEndH((String)tmpMap.get("PRD_DE"));
						break;
					case 'Y' :
						periodInfo.setDefaultEndY((String)tmpMap.get("PRD_DE"));
						break;
					case 'F' :
						periodInfo.setDefaultEndF((String)tmpMap.get("PRD_DE"));
						break;
				}
			}else if(i == list.size() - 1){
				//defaultEnd
				switch(prdSe){
					case 'D' :
						periodInfo.setDefaultStartD((String)tmpMap.get("PRD_DE"));
						break;
					case 'T' :
						periodInfo.setDefaultStartT((String)tmpMap.get("PRD_DE"));
						break;
					case 'M' :
						periodInfo.setDefaultStartM((String)tmpMap.get("PRD_DE"));
						break;
					case 'B' :
						periodInfo.setDefaultStartB((String)tmpMap.get("PRD_DE"));
						break;
					case 'Q' :
						periodInfo.setDefaultStartQ((String)tmpMap.get("PRD_DE"));
						break;
					case 'H' :
						periodInfo.setDefaultStartH((String)tmpMap.get("PRD_DE"));
						break;
					case 'Y' :
						periodInfo.setDefaultStartY((String)tmpMap.get("PRD_DE"));
						break;
					case 'F' :
						periodInfo.setDefaultStartF((String)tmpMap.get("PRD_DE"));
						break;
				}
			}
		}

		switch(prdSe){
			case 'D' :
				periodInfo.setDefaultListD(defaultList);
				break;
			case 'T' :
				periodInfo.setDefaultListT(defaultList);
				break;
			case 'M' :
				periodInfo.setDefaultListM(defaultList);
				break;
			case 'B' :
				periodInfo.setDefaultListB(defaultList);
				break;
			case 'Q' :
				periodInfo.setDefaultListQ(defaultList);
				break;
			case 'H' :
				periodInfo.setDefaultListH(defaultList);
				break;
			case 'Y' :
				periodInfo.setDefaultListY(defaultList);
				break;
			case 'F' :
				periodInfo.setDefaultListF(defaultList);
				break;
		}

		//System.out.println("periodInfo.defaultListD"+periodInfo.getDefaultListD());

	}

	public Map getMinMaxPrdDe(Map paramMap, char prdSe){

		String minDe = null;
		String maxDe = null;

		switch(prdSe){
			case 'D' :
				minDe = statInfo.getPeriodInfo().getStartD();
				maxDe = statInfo.getPeriodInfo().getEndD();
				break;
			case 'T' :
				minDe = statInfo.getPeriodInfo().getStartY();
				maxDe = statInfo.getPeriodInfo().getEndY();
				break;
			case 'M' :
				minDe = statInfo.getPeriodInfo().getStartM();
				maxDe = statInfo.getPeriodInfo().getEndM();
				break;
			case 'B' :
				minDe = statInfo.getPeriodInfo().getStartB();
				maxDe = statInfo.getPeriodInfo().getEndB();
				break;
			case 'Q' :
				minDe = statInfo.getPeriodInfo().getStartQ();
				maxDe = statInfo.getPeriodInfo().getEndQ();
				break;
			case 'H' :
				minDe = statInfo.getPeriodInfo().getStartH();
				maxDe = statInfo.getPeriodInfo().getEndH();
				break;
			case 'Y' :
				minDe = statInfo.getPeriodInfo().getStartY();
				maxDe = statInfo.getPeriodInfo().getEndY();
				break;
			case 'F' :
				minDe = statInfo.getPeriodInfo().getStartF();
				maxDe = statInfo.getPeriodInfo().getEndF();
				break;
		}

		paramMap.put("minDe", minDe);
		paramMap.put("maxDe", maxDe);

		return paramMap;
	}


	public StatInfo getStatInfo() {
		return statInfo;
	}

	public void setStatInfo(StatInfo statInfo) {
		this.statInfo = statInfo;
	}

	public StatHtmlDAO getStatHtmlDAO() {
		return statHtmlDAO;
	}

	public void setStatHtmlDAO(StatHtmlDAO statHtmlDAO) {
		this.statHtmlDAO = statHtmlDAO;
	}

	public PivotInfo getPivotInfo() {
		return pivotInfo;
	}

	public void setPivotInfo(PivotInfo pivotInfo) {
		this.pivotInfo = pivotInfo;
	}

}

