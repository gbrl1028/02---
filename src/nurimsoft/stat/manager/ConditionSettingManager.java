package nurimsoft.stat.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.ClassInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.info.PivotInfo;
import nurimsoft.stat.info.StatInfo;
import nurimsoft.stat.pivot.ClassDimension;
import nurimsoft.stat.pivot.ItemDimension;
import nurimsoft.stat.pivot.TimeDimension;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.webapp.StatHtmlDAO;
import nurimsoft.stat.util.MessageManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class ConditionSettingManager extends SettingManager{
	
protected Log log = LogFactory.getLog(this.getClass());
	
	public ConditionSettingManager(StatInfo statInfo, StatHtmlDAO statHtmlDAO){
		super(statInfo, statHtmlDAO);
	}

	public void setCondition(){
		
		if(olapStl != null){
			levelExpr = olapStl.substring(1, 2);
		}
		
		statInfo.setLevelExpr(levelExpr);
		
		//항목, 분류
		setClassItemUsingCondition();
		
		//시점
		setPeriodInfoUsingCondition();
		
		//피봇정보
		setPivotInfo();
		
		//PivoInfo 셋팅
		statInfo.setPivotInfo(getPivotInfo());
		
		//TEST
		/*
		PivotInfo pi = statInfo.getPivotInfo();
		
		for(int i = 0; i < pi.getColList().size(); i++){
			System.out.println("@@@@@@@@@@ col ::: " + pi.getColList().get(i) + ", " + pi.getColNmList().get(i) + "," + pi.getColEngNmList().get(i));
		}
		
		for(int i = 0; i < pi.getRowList().size(); i++){
			System.out.println("@@@@@@@@@@ row ::: " + pi.getRowList().get(i) + ", " + pi.getRowNmList().get(i) + "," + pi.getRowEngNmList().get(i));
		}
		*/
	}
	
	public void setClassItemUsingCondition(){
		//항목
		Map paramMap = new HashMap();
		List itemAllArr = new ArrayList();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());


		//2015.06.05 상속통계표 변수
		if( "Y".equals(paramInfo.getInheritYn()) ){
			paramMap.put("inheritYn", paramInfo.getInheritYn());
			paramMap.put("originOrgId", paramInfo.getOriginOrgId());
			paramMap.put("originTblId", paramInfo.getOriginTblId());
		}
		
		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());
		
		List list = statHtmlDAO.getDimensionItemList(paramMap, "item");
		Map tmpMap = null;
		
		List defaultItmList = new ArrayList();
		for(int i = 0; i < list.size(); i++){
			tmpMap = (Map)list.get(i);
			defaultItmList.add((String)tmpMap.get("ITM_ID"));
		}
		
		statInfo.getItemInfo().setDefaultItmList(defaultItmList);
		statInfo.getItemInfo().setDefaultItmCnt(defaultItmList.size());
		
		//분류
		List classInfoList = statInfo.getClassInfoList();
		ClassInfo classInfo = null;
		
		for(int i = 0; i < classInfoList.size(); i++){
			classInfo = (ClassInfo)classInfoList.get(i);
			
			paramMap.put("objVarId", classInfo.getClassId());
			paramMap.put("targetId", "OV_L" + classInfo.getVarOrdSn() + "_ID");			//분류순서 세팅
			
			list = statHtmlDAO.getDimensionItemList(paramMap, "class");
			
			defaultItmList = new ArrayList();
			for(int j = 0; j < list.size(); j++){
				tmpMap = (Map)list.get(j);
				defaultItmList.add(paramMap.get("objVarId")+"^"+(BigDecimal)tmpMap.get("LVL") + "#" + (String)tmpMap.get("ITM_ID"));
			}
			
			classInfo.setDefaultItmList(defaultItmList);
			classInfo.setDefaultItmCnt(defaultItmList.size());
		}
	}
	
	private void setPeriodInfoUsingCondition(){
		//시점 셋팅
		
		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		String[] prdArr = null;
		StringBuffer prdInfoBuff = new StringBuffer();	//주기 문자열 ex)M#Y#
		PeriodInfo periodInfo = statInfo.getPeriodInfo();
		char prdSe;
			
		prdArr = prd.split("@");
			
		for(int i = 0; i < prdArr.length; i++){
			String[] tmpArr = prdArr[i].split(",");
			int len = tmpArr.length;
			prdInfoBuff.append(tmpArr[0] + "#");
			
			prdSe = tmpArr[0].charAt(0);
			
			List defaultList = new ArrayList();
			
			for(int j = 1; j < tmpArr.length; j++){
				defaultList.add(tmpArr[j]);
			}
			
			switch(prdSe){
				case 'D' :
					periodInfo.setDefaultStartD(tmpArr[len - 1]);
					periodInfo.setDefaultEndD(tmpArr[1]);
					periodInfo.setDefaultListD(defaultList);
					break;
				case 'T' :
					periodInfo.setDefaultStartT(tmpArr[len - 1]);
					periodInfo.setDefaultEndT(tmpArr[1]);
					periodInfo.setDefaultListT(defaultList);
					break;
				case 'M' :
					periodInfo.setDefaultStartM(tmpArr[len - 1]);
					periodInfo.setDefaultEndM(tmpArr[1]);
					periodInfo.setDefaultListM(defaultList);
					break;
				case 'B' :
					periodInfo.setDefaultStartB(tmpArr[len - 1]);
					periodInfo.setDefaultEndB(tmpArr[1]);
					periodInfo.setDefaultListB(defaultList);
					break;
				case 'Q' :
					periodInfo.setDefaultStartQ(tmpArr[len - 1]);
					periodInfo.setDefaultEndQ(tmpArr[1]);
					periodInfo.setDefaultListQ(defaultList);
					break;
				case 'H' :
					periodInfo.setDefaultStartH(tmpArr[len - 1]);
					periodInfo.setDefaultEndH(tmpArr[1]);
					periodInfo.setDefaultListH(defaultList);
					break;
				case 'Y' :
					periodInfo.setDefaultStartY(tmpArr[len - 1]);
					periodInfo.setDefaultEndY(tmpArr[1]);
					periodInfo.setDefaultListY(defaultList);
					break;
				case 'F' :
					periodInfo.setDefaultStartF(tmpArr[len - 1]);
					periodInfo.setDefaultEndF(tmpArr[1]);
					periodInfo.setDefaultListF(defaultList);
					break;
			}
			
		}
		
		statInfo.setDefaultPeriodStr(prdInfoBuff.toString());
		statInfo.setDefaultPeriodCnt(prdArr.length);
	}
	
	public void setPivotInfo(){
		pivotInfo = new PivotInfo();
		
		List<ClassInfo> classInfoList = statInfo.getClassInfoList();
		
		List colList = pivotInfo.getColList();
		List colNmList = pivotInfo.getColNmList();
		List colEngNmList = pivotInfo.getColEngNmList();
		List rowList = pivotInfo.getRowList();
		List rowNmList = pivotInfo.getRowNmList();
		List rowEngNmList = pivotInfo.getRowEngNmList();
		
		String[] colAxisArr = null;
		String[] rowAxisArr = null;
		
		if(paramInfo.getColAxis() != null && paramInfo.getColAxis().trim().length() != 0){
			colAxisArr = paramInfo.getColAxis().split(",");
		}
		
		if(paramInfo.getRowAxis() != null && paramInfo.getRowAxis().trim().length() != 0){
			rowAxisArr = paramInfo.getRowAxis().split(",");
		}
		
		String timeYearKo = PropertyManager.getInstance().getProperty("string.head.time.year.ko");
		String timeYearEn = PropertyManager.getInstance().getProperty("string.head.time.year.en");
		
		String timeMQKo = PropertyManager.getInstance().getProperty("string.head.time.mq.ko");
		String timeMQEn = PropertyManager.getInstance().getProperty("string.head.time.mq.en");
		
		if(colAxisArr != null){
			for(int i = 0; i < colAxisArr.length; i++){
				if(!colAxisArr[i].startsWith("TIME") && !colAxisArr[i].startsWith("ITEM")){
					//분류
					for(int j = 0; j < classInfoList.size(); j++){
						ClassInfo classInfo = classInfoList.get(j);
						if(classInfo.getClassId().equals(colAxisArr[i]) && classInfo.isVisible()){
							colList.add(colAxisArr[i]);
							colNmList.add(classInfo.getClassNm());
							colEngNmList.add(classInfo.getClassEngNm());
							break;
						}
					}
				}else if(colAxisArr[i].startsWith("ITEM")){
					//항목
					colList.add("ITEM");
					colNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotItem", paramInfo.getDataOpt()));
					colEngNmList.add("ITEM");
				}else if(colAxisArr[i].startsWith("TIME")){
					//시점
					if(paramInfo.getTableType().equals("perYear")){
						if(colAxisArr[i].equals("TIME_YEAR")){
							colList.add(colAxisArr[i]);
							colNmList.add(timeYearKo);
							colEngNmList.add(timeYearEn);
						}else{
							//TIME_MQ
							colList.add(colAxisArr[i]);
							colNmList.add(timeMQKo);
							colEngNmList.add(timeMQEn);
						}
						
					}else{
						colList.add("TIME");
						colNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotTime", paramInfo.getDataOpt()));
						colEngNmList.add("TIME");
					}
				}
			}
		}
		
		if(rowAxisArr != null){
			for(int i = 0; i < rowAxisArr.length; i++){
				if(!rowAxisArr[i].startsWith("TIME") && !rowAxisArr[i].startsWith("ITEM")){
					//분류
					for(int j = 0; j < classInfoList.size(); j++){
						ClassInfo classInfo = classInfoList.get(j);
						if(classInfo.getClassId().equals(rowAxisArr[i]) && classInfo.isVisible()){
							rowList.add(rowAxisArr[i]);
							rowNmList.add(classInfo.getClassNm());
							rowEngNmList.add(classInfo.getClassEngNm());
							break;
						}
					}
				}else if(rowAxisArr[i].startsWith("ITEM")){
					//항목
					rowList.add("ITEM");
					rowNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotItem", paramInfo.getDataOpt()));
					rowEngNmList.add("ITEM");
				}else if(rowAxisArr[i].startsWith("TIME")){
					//시점
					if(paramInfo.getTableType().equals("perYear")){
						if(rowAxisArr[i].equals("TIME_YEAR")){
							rowList.add(rowAxisArr[i]);
							rowNmList.add(timeYearKo);
							rowEngNmList.add(timeYearEn);
						}else{
							//TIME_MQ
							rowList.add(rowAxisArr[i]);
							rowNmList.add(timeMQKo);
							rowEngNmList.add(timeMQEn);
						}
						
					}else{
						rowList.add("TIME");
						rowNmList.add(MessageManager.getInstance().getProperty("ui.label.pivotTime", paramInfo.getDataOpt()));
						rowEngNmList.add("TIME");
					}
				}
			}
		}
		
	}
	
}
