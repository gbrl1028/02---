package nurimsoft.stat.pivot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlDAO;

public class TimeDimension extends Dimension{

	//시점은 Item으로 생성할때 주기와 시점을 붙여 넣는다.(월과 격월, 분기와 반기 시점 데이터가 같으므로 구분하기 위함)

	//Query에 사용될 where 절 구문
	//년, 월표의 경우 TIME_MQ는 없다..TIME_YEAR에서 WHERE 절을 셋팅함
	public StringBuffer prdWhereClause = new StringBuffer();

	public StringBuffer prdWhere = new StringBuffer();

	//더미여부를 위한 배열
	public List<String> prdListDummy = new ArrayList();

	//연/월(분기) 표현 시 년도인지 월/분기 인지를 구분하기 위함
	//TIME_YEAR : 년, TIME_MQ : 월/분기
	private String time;

	//2013.11.20
	//분석시 사용할 시작시점과 종료시점(첫번째 주기만 적용 - 분석 시 하나의 주기만 가능하므로)
	public String prdSe;
	public String from;
	public String to;

	public TimeDimension(ParamInfo paramInfo, StatHtmlDAO statHtmlDAO) throws Exception{
		super(paramInfo, statHtmlDAO);
		nameKor = PropertyManager.getInstance().getProperty("string.head.time.ko");
		nameEng = PropertyManager.getInstance().getProperty("string.head.time.en");

		//시점 셋팅
		setItemList();
	}

	public TimeDimension(ParamInfo paramInfo, String time, StatHtmlDAO statHtmlDAO) throws Exception{
		super(paramInfo, statHtmlDAO);
		this.time = time;

		if(time.equals("TIME_YEAR")){
			nameKor = PropertyManager.getInstance().getProperty("string.head.time.year.ko");
			nameEng = PropertyManager.getInstance().getProperty("string.head.time.year.en");
		}else{
			//TIME_MQ
			nameKor = PropertyManager.getInstance().getProperty("string.head.time.mq.ko");
			nameEng = PropertyManager.getInstance().getProperty("string.head.time.mq.en");
		}

		//시점 셋팅
		setItemList();
	}

	@SuppressWarnings("unchecked")
	private void setItemList() throws Exception{

		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);

		String[] prdArr = prd.split("@");

		Item item = null;

		//2014.07.09 where 절을 변경(prd_se||prd_de를 prd_se = '' and prd_de in () 형태로 변경하여 index를 효율적으로 이용하도록 한다.)
		if(time == null){
			//일반통계표형태
			//prdWhereClause.append("(");

			if(paramInfo.getPrdSort().equals("asc")){
				//시점 오름차순
				for(int i = 0; i < prdArr.length; i++){

					String[] tmpArr = prdArr[i].split(",");

					String period = tmpArr[0];

					if(i != 0){
						prdWhereClause.append(" OR \n ");
					}
					prdWhereClause.append("(B.PRD_SE = '" + period + "' AND B.PRD_DE IN (");

					if(period.equals("F")){
						period = statHtmlDAO.selectPrdDetail(paramInfo);
					}

					//2013.11.20
					if(i == 0){
						prdSe = tmpArr[0];
						to = prdSe + tmpArr[1];
						from = prdSe + tmpArr[tmpArr.length - 1];
					}

					for(int j = tmpArr.length - 1; j > 0; j--){
						item = new Item(period + tmpArr[j], StatPivotUtil.generatePrdDe(tmpArr[j], tmpArr[0]));
						item.setDataOpt(paramInfo.getDataOpt());
						addItem(item);

						//if(i == 0 && j == tmpArr.length - 1){
						if(j == tmpArr.length - 1){
							//prdWhereClause.append("'" + tmpArr[0] + tmpArr[j] + "'");
							prdWhereClause.append("'" + tmpArr[j] + "'");
							prdWhere.append("IN ('" + tmpArr[j] + "'");
						}else{
							//prdWhereClause.append(",'" + tmpArr[0] + tmpArr[j] + "'");
							prdWhereClause.append(",'" + tmpArr[j] + "'");
							prdWhere.append(",'" + tmpArr[j] + "'");
						}
						//더미여부를 위한 배열에 담는다.
						prdListDummy.add(tmpArr[0] + tmpArr[j]);

					}

					prdWhereClause.append("))");
					prdWhere.append(")");
				}

			}else{
				//시점 내림차순
				//for(int i = prdArr.length - 1; i >= 0; i--){ //2017.11.14 낮은 주기부터 먼저~ 범미희 사무관~
				for(int i = 0; i < prdArr.length; i++){
					String[] tmpArr = prdArr[i].split(",");

					String period = tmpArr[0];

					//if(i != prdArr.length - 1){ //2017.11.14 낮은 주기부터 먼저~ 범미희 사무관~
					if(i != 0){
						prdWhereClause.append(" OR \n ");
					}
					prdWhereClause.append("(B.PRD_SE = '" + period + "' AND B.PRD_DE IN (");

					if(period.equals("F")){
						period = statHtmlDAO.selectPrdDetail(paramInfo);
					}

					//2013.11.20
					if(i == 0){
						prdSe = tmpArr[0];
						to = prdSe + tmpArr[1];
						from = prdSe + tmpArr[tmpArr.length - 1];
					}

					for(int j = 1; j < tmpArr.length; j++){
						item = new Item(period + tmpArr[j], StatPivotUtil.generatePrdDe(tmpArr[j], tmpArr[0]));
						item.setDataOpt(paramInfo.getDataOpt());
						addItem(item);

						//if(i == prdArr.length - 1 && j == 1){
						if(j == 1){
							//prdWhereClause.append("'" + tmpArr[0] + tmpArr[j] + "'");
							prdWhereClause.append("'" + tmpArr[j] + "'");
							prdWhere.append("IN ('" + tmpArr[j] + "'");
						}else{
							//prdWhereClause.append(",'" + tmpArr[0] + tmpArr[j] + "'");
							prdWhereClause.append(",'" + tmpArr[j] + "'");
							prdWhere.append(",'" + tmpArr[j] + "'");
						}

						//더미여부를 위한 배열에 담는다.
						prdListDummy.add(tmpArr[0] + tmpArr[j]);
					}

					prdWhereClause.append("))");
					prdWhere.append(")");
				}
			}

			//prdWhereClause.append(")");

		}else{
			//년/월(분기)표 형태

			if(time.equals("TIME_YEAR")){
				//01. 년
				List prdList = new ArrayList();
				//prdWhereClause.append("(");

				for(int i = 0; i < prdArr.length; i++){
					String[] tmpArr = prdArr[i].split(",");

					//2013.11.20
					if(i == 0){
						prdSe = tmpArr[0];
						to = prdSe + tmpArr[1];
						from = prdSe + tmpArr[tmpArr.length - 1];
					}

					if(i > 0){
						prdWhereClause.append(" OR \n ");
					}
					prdWhereClause.append("(B.PRD_SE = '" + tmpArr[0] + "' AND B.PRD_DE IN (");

					for(int j = 1; j < tmpArr.length; j++){
						String tmpStr = tmpArr[j].substring(0, 4);
						Collections.sort(prdList);
						if( Collections.binarySearch(prdList, tmpStr) < 0){
							prdList.add(tmpStr);
						}

						//if(i == 0 && j == 1){
						if(j == 1){
							//prdWhereClause.append("'" + tmpArr[0] + tmpArr[j] + "'");
							prdWhereClause.append("'" + tmpArr[j] + "'");
							prdWhere.append("IN ('" + tmpArr[j] + "'");
						}else{
							//prdWhereClause.append(",'" + tmpArr[0] + tmpArr[j] + "'");
							prdWhereClause.append(",'" + tmpArr[j] + "'");
							prdWhere.append(",'" + tmpArr[j] + "'");
						}

						//더미여부를 위한 배열에 담는다.
						prdListDummy.add(tmpArr[0] + tmpArr[j]);
					}

					prdWhereClause.append("))");
					prdWhere.append(")");
				}

				if(paramInfo.getPrdSort().equals("asc")){
					//시점 정렬 오름차순
					Collections.sort(prdList);
				}else{
					//시점 정렬 내림차순
					Collections.sort(prdList, Collections.reverseOrder());
				}

				for(int k = 0; k < prdList.size(); k++){
					String tmpPrd = (String)prdList.get(k);
					item = new Item("Y" + tmpPrd, tmpPrd);
					item.setDataOpt(paramInfo.getDataOpt());
					addItem(item);
				}

				//prdWhereClause.append(")");

				//CHECK
				/*
				for(int k = 0; k < prdList.size(); k++){
					System.out.println(prdList.get(k));
				}
				*/

			}else{
				//02. 월(분기) - TIME_MQ
				if(paramInfo.getPrdSort().equals("asc")){
					//시점 오름차순
					for(int i = 0; i < prdArr.length; i++){
						String[] tmpArr = prdArr[i].split(",");

						//2013.11.20
						if(i == 0){
							prdSe = tmpArr[0];
							to = prdSe + tmpArr[1];
							from = prdSe + tmpArr[tmpArr.length - 1];
						}

						List prdList = new ArrayList();
						for(int j = 1; j < tmpArr.length; j++){
							String tmpStr = tmpArr[j].substring(4);

							Collections.sort(prdList);
							if( Collections.binarySearch(prdList, tmpStr) < 0){
								prdList.add(tmpStr);
							}

							Collections.sort(prdList);

						}

						for(int k = 0; k < prdList.size(); k++){
							String tmpPrd = (String)prdList.get(k);
							item = new Item(tmpArr[0] + tmpPrd, StatPivotUtil.generatePerYearPrdDe(tmpPrd, tmpArr[0], paramInfo.getDataOpt()));
							item.setDataOpt(paramInfo.getDataOpt());
							addItem(item);
						}
					}

				}else{
					//시점 내림차순
					for(int i = prdArr.length - 1; i >= 0; i--){
						String[] tmpArr = prdArr[i].split(",");

						List prdList = new ArrayList();
						for(int j = 1; j < tmpArr.length; j++){
							String tmpStr = tmpArr[j].substring(4);

							Collections.sort(prdList);
							if( Collections.binarySearch(prdList, tmpStr) < 0){
								prdList.add(tmpStr);
							}

							Collections.sort(prdList, Collections.reverseOrder());

						}

						for(int k = 0; k < prdList.size(); k++){
							String tmpPrd = (String)prdList.get(k);
							item = new Item(tmpArr[0] + tmpPrd, StatPivotUtil.generatePerYearPrdDe(tmpPrd, tmpArr[0], paramInfo.getDataOpt()));
							item.setDataOpt(paramInfo.getDataOpt());
							addItem(item);
						}
					}
				}
			}
		}

		//시점에 잠정치 작업 추가, 2013.12.13
		List prdList = new ArrayList();
		String buff	= "";
		
		for(Item tmpItem : itemList){
			buff	=  StringUtil.stringCheck(tmpItem.getCode());
			
			//2017.01.12 부정기일때 통계부호가 나오지 않는 현상 수정 - 김경호
			if( buff.indexOf("2Y") > -1 ){
				buff = buff.replace("2Y", "F");
			}else if( buff.indexOf("3Y") > -1 ){
				buff = buff.replace("3Y", "F");
			}else if( buff.indexOf("4Y") > -1 ){
				buff = buff.replace("4Y", "F");
			}else if( buff.indexOf("5Y") > -1 ){
				buff = buff.replace("5Y", "F");
			}else if( buff.indexOf("10Y") > -1 ){
				buff = buff.replace("10Y", "F");
			}else if( buff.indexOf("IR") > -1 ){
				buff = buff.replace("IR", "F");
			}

			prdList.add(buff);
		}

		Map paramMap = new HashMap();

		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("prdList", prdList);
		
		//2015.07.14 상속통계표 추가
		paramMap.put("inheritYn", paramInfo.getInheritYn());
		paramMap.put("originOrgId", paramInfo.getOriginOrgId());
		paramMap.put("originTblId", paramInfo.getOriginTblId());

		List<Map> recdPrdList = statHtmlDAO.getRecdPrdSmblCn(paramMap);
		Map recdPrdMap = new HashMap();
		for(Map tmpMap : recdPrdList){
			recdPrdMap.put(tmpMap.get("CODE"), tmpMap.get("SMBL_CN"));
		}

		for(Item tmpItem : itemList){
			buff	=  StringUtil.stringCheck(tmpItem.getCode());
			
			//2017.01.12 부정기일때 통계부호가 나오지 않는 현상 수정 - 김경호
			if( buff.indexOf("2Y") > -1 ){
				buff = buff.replace("2Y", "F");
			}else if( buff.indexOf("3Y") > -1 ){
				buff = buff.replace("3Y", "F");
			}else if( buff.indexOf("4Y") > -1 ){
				buff = buff.replace("4Y", "F");
			}else if( buff.indexOf("5Y") > -1 ){
				buff = buff.replace("5Y", "F");
			}else if( buff.indexOf("10Y") > -1 ){
				buff = buff.replace("10Y", "F");
			}else if( buff.indexOf("IR") > -1 ){
				buff = buff.replace("IR", "F");
			}
			
			if(recdPrdMap.get(buff) != null){
				tmpItem.setName(tmpItem.getName() + " " + recdPrdMap.get(buff) + ")");
			}
		}

		//CHECK
/*
		System.out.println(prdWhereClause.toString());
		List list = this.getItemList();
		System.out.println(list.size());
		for(int i = 0; i < list.size(); i++){
			System.out.println( ((Item)list.get(i)).getCode() + "," + ((Item)list.get(i)).getName() );
		}
*/
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
