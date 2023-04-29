package nurimsoft.stat.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.ClassInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.info.StatInfo;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScrapSettingManager extends SettingManager{
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	String param;
	String paramWeb;
	
	public ScrapSettingManager(StatInfo statInfo, StatHtmlDAO statHtmlDAO){
		super(statInfo, statHtmlDAO);
		
		usingMyScrap = true;
	}
	
	public void setCondition(){
		
		Map paramMap = new HashMap();
		
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("empId", paramInfo.getEmpId());
		paramMap.put("scrId", paramInfo.getScrId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("tnStblScr", PropertyManager.getInstance().getProperty("table.scrap.scr"));
		paramMap.put("tnStblScrItm", PropertyManager.getInstance().getProperty("table.scrap.scr.itm"));

		//2015.06.05 상속통계표
		if( "Y".equals(paramInfo.getInheritYn()) ){
			paramMap.put("inheritYn",   paramInfo.getInheritYn());
			paramMap.put("originOrgId", paramInfo.getOriginOrgId());
			paramMap.put("originTblId", paramInfo.getOriginTblId());
		}
		
		List<Map> scrInfoList = statHtmlDAO.getScrInfoUsingScrId(paramMap);
		setPivotInfo(scrInfoList);
		
		paramMap.put("colName", "prd_de");
		param = statHtmlDAO.getScrParam(paramMap);
		paramWeb = statHtmlDAO.getScrParamWeb(paramMap);
		int chkCnt = statHtmlDAO.getScrItmCount(paramMap);
		
		//System.out.println("@@@@@@@@@@@@@@ ::: " + chkCnt);
		
		if(chkCnt > 0){
			//WEB표준 시스템으로 스크랩이 저장된 경우
			
			//1. 계층별 컬럼보기값 셋팅
			if(olapStl != null){
				levelExpr = olapStl.substring(1, 2);
			}
			statInfo.setLevelExpr(levelExpr);
			
			//항목/분류 셋팅
			setInfosUsingScrItm(paramMap);
			
			//시점 셋팅
			setPeriodInfoUsingScr(paramMap);
			
		}else{
			//B. OLAP_STL 가져오기(없으면 생성)
			setOlapStl();
			
			//olap_Stl을 이요하여 분류, 항목 셋팅(시점은 제외됨)
			setInfosUsingOlapStl();	
			
			//시점 셋팅
			setPeriodInfoUsingScr(paramMap);
		}
		
		//PivoInfo 셋팅
		statInfo.setPivotInfo(getPivotInfo());
		
		paramMap.put("colName", "params");
		String params = statHtmlDAO.getScrParamWeb(paramMap);
		
		String[] paramArr = null;
		if(params != null){
			/*
			2018.12.04 WEB 표준 시스템에서 파라미터를 저장한 경우 (현재는 원자료보기 originData 와  구성비, 누계구성비에서 나오는 기준선택여부의 기준자료 선택안함만 등록할것임)
			기준자료 선택안함이 스크랩이 안되는 문제는 개발팀에서 미완성인채로 넘어왔음.  
			등록 예) originData,Y,@noSelect,noSelect@
			*/
			paramArr = params.split("@");

			for(int i = 0; i < paramArr.length; i++){
				String[] tmpArr = paramArr[i].split(",");
				
				String tmp_buff = StringUtil.stringCheck(tmpArr[0]);
				String tmp_buff2 = StringUtil.stringCheck(tmpArr[1]);
				
				if(tmp_buff.equals("originData") && tmp_buff2.equals("Y")){
					paramInfo.setOriginData("Y");
				}else if(tmp_buff.equals("noSelect") && tmp_buff2.equals("noSelect")){
					paramInfo.setNoSelect("noSelect");
				}
			}
		}
	}
	
	private void setInfosUsingScrItm(Map paramMap){
		
		//분류셋팅
		List classInfoList = statInfo.getClassInfoList();
		ClassInfo classInfo = null;
		
		List list = null;
		Map tmpMap = null;
		for(int i = 0; i < classInfoList.size(); i++){
			classInfo = (ClassInfo)classInfoList.get(i);
			
			paramMap.put("objVarId", classInfo.getClassId());
			
			list = statHtmlDAO.getClassItemListScrap(paramMap);
			
			List defaultItmList = new ArrayList();
			for(int j = 0; j < list.size(); j++){
				tmpMap = (Map)list.get(j);
				defaultItmList.add(paramMap.get("objVarId")+"^"+(BigDecimal)tmpMap.get("LVL") + "#" + (String)tmpMap.get("ITM_ID"));
			}
			
			classInfo.setDefaultItmList(defaultItmList);
			classInfo.setDefaultItmCnt(defaultItmList.size());
		}
		
		//항목 셋팅
		list = statHtmlDAO.getItemListScrap(paramMap);
		List defaultItmList = new ArrayList();
		for(int i = 0; i < list.size(); i++){
			tmpMap = (Map)list.get(i);
			defaultItmList.add((String)tmpMap.get("ITM_ID"));
		}
		
		statInfo.getItemInfo().setDefaultItmList(defaultItmList);
		statInfo.getItemInfo().setDefaultItmCnt(defaultItmList.size());
		
	}
	
	private void setPeriodInfoUsingScr(Map paramMap){
		//시점 셋팅
		String[] prdArr = null;
		StringBuffer prdInfoBuff = new StringBuffer();	//주기 문자열 ex)M#Y#
		PeriodInfo periodInfo = statInfo.getPeriodInfo();
		char prdSe;
		
		if(paramWeb != null){
			//WEB 표준 시스템에서 스크랩정보를 저장한 경우
			//M,201112,201111,201110,201109,201108,201107,@Y,2011,2010,@
			//R,M,6@R,Y,2@
			
			prdArr = paramWeb.split("@");
			if(prdArr[0].split(",")[0].equals("R")){
				//최근시점
				int periodCnt = 0;
				for(int i = 0; i < prdArr.length; i++){
					String[] tmpArr = prdArr[i].split(",");
					prdInfoBuff.append(tmpArr[1] + "#");
					try{
						periodCnt = Integer.parseInt(tmpArr[2]);
					}catch(NumberFormatException nfe){
						periodCnt = 1;
					}
					
					setPeriodInfo(periodCnt, tmpArr[1]);
				}
			}else{
				//조회기간
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
			}
			
			statInfo.setDefaultPeriodStr(prdInfoBuff.toString());
			statInfo.setDefaultPeriodCnt(prdArr.length);
			
		}else{
			//SIGA에서 스크랩정보를 저장한 경우
			//M201107_M201112,Y2010_Y2011
			//RM6,RY2
			
			prdArr = param.split(",");
			
			if(prdArr[0].substring(0, 1).equals("R")){
				//최근시점
				//2013.12.06 dimCo수를 체크하지 않도록 변경
				//TODO dimCo수 다시 조정 시 주석위치 변경
				if(mixCnt > maxRow){
				//if(mixCnt > maxRow && statInfo.getDimCo() > maxRow){
					prdSe = prdArr[0].substring(1, 2).charAt(0);
					setPeriodInfo(1, prdSe + "");
					
					statInfo.setDefaultPeriodStr(prdSe + "#");
					statInfo.setDefaultPeriodCnt(1);
				}else{
					List<String> defaultList = new ArrayList();
					Map<String, String> tMap = new HashMap();
					int periodCnt = 0;
					
					for(int i = 0; i < prdArr.length; i++){
						prdSe = prdArr[i].substring(1, 2).charAt(0);
						try{
							periodCnt = Integer.parseInt(prdArr[i].substring(2));
						}catch(NumberFormatException nfe){
							periodCnt = 1;
						}
						
						paramMap.put("prdSe", prdSe + "");
						//해당 통계표의 시작시점 및 최종시점을 맵에 셋팅한다.(StatInfoManager로부터 셋팅된 값)
						paramMap = getMinMaxPrdDe(paramMap, prdSe);
						paramMap.put("periodCnt", periodCnt);
						
						List<Map> list = statHtmlDAO.getPeriodList(paramMap);
						for(Map<String, String> tmpMap : list){
							defaultList.add(prdSe + tmpMap.get("PRD_DE"));
						}
					}
					
					setSIGAScrapPeriodInfo(defaultList, periodInfo);
				}
			}else{
				//조회기간
				//2013.12.06 dimCo수를 체크하지 않도록 변경
				//TODO dimCo수 다시 조정 시 주석위치 변경
				if(mixCnt > maxRow){
				//if(mixCnt > maxRow && statInfo.getDimCo() > maxRow){
					//조합수가 maxRow보다 클 경우 : 첫번째 주기에서 첫번째 최근시점만 적용한다.
					prdSe = prdArr[0].charAt(0);
					String prdDe = prdArr[0].split("_")[1];
					
					List defaultList = new ArrayList();
					defaultList.add(prdDe);
					
					switch(prdSe){
						case 'D' :
							periodInfo.setDefaultStartD(prdDe);
							periodInfo.setDefaultEndD(prdDe);
							periodInfo.setDefaultListD(defaultList);
							break;
						case 'T' :
							periodInfo.setDefaultStartT(prdDe);
							periodInfo.setDefaultEndT(prdDe);
							periodInfo.setDefaultListT(defaultList);
							break;
						case 'M' :
							periodInfo.setDefaultStartM(prdDe);
							periodInfo.setDefaultEndM(prdDe);
							periodInfo.setDefaultListM(defaultList);
							break;
						case 'B' :
							periodInfo.setDefaultStartB(prdDe);
							periodInfo.setDefaultEndB(prdDe);
							periodInfo.setDefaultListB(defaultList);
							break;
						case 'Q' :
							periodInfo.setDefaultStartQ(prdDe);
							periodInfo.setDefaultEndQ(prdDe);
							periodInfo.setDefaultListQ(defaultList);
							break;
						case 'H' :
							periodInfo.setDefaultStartH(prdDe);
							periodInfo.setDefaultEndH(prdDe);
							periodInfo.setDefaultListH(defaultList);
							break;
						case 'Y' :
							periodInfo.setDefaultStartY(prdDe);
							periodInfo.setDefaultEndY(prdDe);
							periodInfo.setDefaultListY(defaultList);
							break;
						case 'F' :
							periodInfo.setDefaultStartF(prdDe);
							periodInfo.setDefaultEndF(prdDe);
							periodInfo.setDefaultListF(defaultList);
							break;
					}
					
					statInfo.setDefaultPeriodStr(prdSe + "#");
					statInfo.setDefaultPeriodCnt(1);
					
				}else{
					//maxRow를 초과하지 않는 경우 스크랩에 저장된 시점수를 계산하여 곱한 후 maxRow 이하 범위내에서 시점수를 조정한다..단. 조정시점은 저장된 주기의 뒤에서 부터 적용
					//List에 각 주기별로 구간 사이의 모든 값을 query를 통해서 가져온 후(역순으로 가져와야 한다.) Map 형태로 추가한다.(Map 은 prdSe, prdDe 두개의 키로 구성한다.)
					//List의 size가 시점수가 되며, maxRow와 계산하여 적용될 시점수를 추출한 후 List에서 뒤에서부터 삭제한다.d
					//그리고, 남은 LIST의 주기, 시점이 곧...적용할 것이 된다.
					
					List<String> defaultList = new ArrayList();
					
					String scrapMinDe = null;
					String scrapMaxDe = null;
					
					Map<String, String> tMap = new HashMap();
					for(int i = 0; i < prdArr.length; i++){
						prdSe = prdArr[i].charAt(0);
						scrapMinDe = prdArr[i].split("_")[0].substring(1);
						scrapMaxDe = prdArr[i].split("_")[1].substring(1);
						
						paramMap.put("prdSe", prdSe + "");
						//해당 통계표의 시작시점 및 최종시점을 맵에 셋팅한다.(StatInfoManager로부터 셋팅된 값)
						paramMap = getMinMaxPrdDe(paramMap, prdSe);
						paramMap.put("scrapMinDe", scrapMinDe);
						paramMap.put("scrapMaxDe", scrapMaxDe);
						
						List<Map> list = statHtmlDAO.getPeriodList(paramMap);
						for(Map<String, String> tmpMap : list){
							defaultList.add(prdSe + tmpMap.get("PRD_DE"));
						}
					}
					
					setSIGAScrapPeriodInfo(defaultList, periodInfo);
					
				}
			}
		}
	}
	
	public void setSIGAScrapPeriodInfo(List<String> defaultList, PeriodInfo periodInfo){
		
		StringBuffer prdInfoBuff = new StringBuffer();	//주기 문자열 ex)M#Y#
		
		long tmpTimeCnt = maxRow / mixCnt;
		
		//tmmpTimeCnt 까지만 남겨주고 나머지는 삭제
		int idx;
		while( (idx = defaultList.size()) > tmpTimeCnt ){
			defaultList.remove(idx - 1);
		}
		
		//각 주기별로 Default 주기값들 셋팅(최종)
		char befPrdSe = 0;
		String sPrdDe = null;
		String ePrdDe = null;
		int tmpSize = 0;
		List prdList = new ArrayList();
		idx = 0;
		int defaultListSize = defaultList.size();
		for(String forPrdDe : defaultList){
			char tmpPrdSe = forPrdDe.charAt(0);
			String tmpPrdDe = forPrdDe.substring(1);
			
			if(befPrdSe != tmpPrdSe){
				tmpSize++;
				prdInfoBuff.append(tmpPrdSe + "#");
				
				if(defaultListSize == 0){
					befPrdSe = tmpPrdSe;
					sPrdDe = tmpPrdDe;
					ePrdDe = tmpPrdDe;
					prdList.add(tmpPrdDe);
				}
				
				if(idx > 0 || defaultListSize == 0){
					switch(befPrdSe){
						case 'D' :
							periodInfo.setDefaultStartD(sPrdDe);
							periodInfo.setDefaultEndD(ePrdDe);
							periodInfo.setDefaultListD(prdList);
							break;
						case 'T' :
							periodInfo.setDefaultStartT(sPrdDe);
							periodInfo.setDefaultEndT(ePrdDe);
							periodInfo.setDefaultListT(prdList);
							break;
						case 'M' :
							periodInfo.setDefaultStartM(sPrdDe);
							periodInfo.setDefaultEndM(ePrdDe);
							periodInfo.setDefaultListM(prdList);
							break;
						case 'B' :
							periodInfo.setDefaultStartB(sPrdDe);
							periodInfo.setDefaultEndB(ePrdDe);
							periodInfo.setDefaultListB(prdList);
							break;
						case 'Q' :
							periodInfo.setDefaultStartQ(sPrdDe);
							periodInfo.setDefaultEndQ(ePrdDe);
							periodInfo.setDefaultListQ(prdList);
							break;
						case 'H' :
							periodInfo.setDefaultStartH(sPrdDe);
							periodInfo.setDefaultEndH(ePrdDe);
							periodInfo.setDefaultListH(prdList);
							break;
						case 'Y' :
							periodInfo.setDefaultStartY(sPrdDe);
							periodInfo.setDefaultEndY(ePrdDe);
							periodInfo.setDefaultListY(prdList);
							break;
						case 'F' :
							periodInfo.setDefaultStartF(sPrdDe);
							periodInfo.setDefaultEndF(ePrdDe);
							periodInfo.setDefaultListF(prdList);
							break;
					}
					
				}//end of if(idx > 0 || defaultListSize == 0){
				
				befPrdSe = tmpPrdSe;
				prdList = new ArrayList();
				ePrdDe = tmpPrdDe;
				
			}// end of if(befPrdSe != tmpPrdSe){
			
			sPrdDe = tmpPrdDe;
			prdList.add(tmpPrdDe);
			
			if(idx == defaultList.size() - 1){
				switch(tmpPrdSe){
					case 'D' :
						periodInfo.setDefaultStartD(sPrdDe);
						periodInfo.setDefaultEndD(ePrdDe);
						periodInfo.setDefaultListD(prdList);
						break;
					case 'T' :
						periodInfo.setDefaultStartT(sPrdDe);
						periodInfo.setDefaultEndT(ePrdDe);
						periodInfo.setDefaultListT(prdList);
						break;
					case 'M' :
						periodInfo.setDefaultStartM(sPrdDe);
						periodInfo.setDefaultEndM(ePrdDe);
						periodInfo.setDefaultListM(prdList);
						break;
					case 'B' :
						periodInfo.setDefaultStartB(sPrdDe);
						periodInfo.setDefaultEndB(ePrdDe);
						periodInfo.setDefaultListB(prdList);
						break;
					case 'Q' :
						periodInfo.setDefaultStartQ(sPrdDe);
						periodInfo.setDefaultEndQ(ePrdDe);
						periodInfo.setDefaultListQ(prdList);
						break;
					case 'H' :
						periodInfo.setDefaultStartH(sPrdDe);
						periodInfo.setDefaultEndH(ePrdDe);
						periodInfo.setDefaultListH(prdList);
						break;
					case 'Y' :
						periodInfo.setDefaultStartY(sPrdDe);
						periodInfo.setDefaultEndY(ePrdDe);
						periodInfo.setDefaultListY(prdList);
						break;
					case 'F' :
						periodInfo.setDefaultStartF(sPrdDe);
						periodInfo.setDefaultEndF(ePrdDe);
						periodInfo.setDefaultListF(prdList);
						break;
				}
			}
			
			idx++;
		}
		
		statInfo.setDefaultPeriodStr(prdInfoBuff.toString());
		statInfo.setDefaultPeriodCnt(idx);
	}

}
