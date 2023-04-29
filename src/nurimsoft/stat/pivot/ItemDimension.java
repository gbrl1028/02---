package nurimsoft.stat.pivot;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import nurimsoft.stat.info.CmmtInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.webapp.StatHtmlDAO;

public class ItemDimension extends Dimension{
	
	Map<String, String> paramMap = new HashMap();
	
	public ItemDimension(ParamInfo paramInfo, String classCode, StatHtmlDAO statHtmlDAO, CmmtInfoManager cmmtInfoManager){
		super(paramInfo, classCode, statHtmlDAO, cmmtInfoManager);
		super.setVarOrdSn(9);
		
		nameKor = PropertyManager.getInstance().getProperty("string.head.item.ko");
		nameEng = PropertyManager.getInstance().getProperty("string.head.item.en");
		
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("serverType", paramInfo.getServerType());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());
		paramMap.put("objVarId", code);

		//2015.06.05 상속통계표 변수
		if( "Y".equals(paramInfo.getInheritYn()) ){
			paramMap.put("inheritYn", paramInfo.getInheritYn());
			paramMap.put("originOrgId", paramInfo.getOriginOrgId());
			paramMap.put("originTblId", paramInfo.getOriginTblId());
		}
		
		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());
	}
	
	public void setInfo(){
		//항목 셋팅
		setItemList();
	}
	
	private void setItemList(){
		List itemList = statHtmlDAO.getDimensionItemList(paramMap, "item");
		
		Map map = null;
		Item item = null;
		List<String> list = null;	//주석정보 담을 list
		for(int i = 0; i < itemList.size(); i++){
			map = (Map)itemList.get(i);
			item = new Item((String)map.get("ITM_ID"), (String)map.get("SCR_KOR"), (String)map.get("SCR_ENG"));
			
			if( itemList.size() != 1){	// 2014.04.28 통계표 단위가 없고 항목이 1개일때 항목단위가 있으면 통계표 단위로 표시하고 항목 단위는 안보여주도록...(그냥 항목1개일때는 단위를 안보여주는걸로 해결) - 김경호 
				item.setUnitId((String)map.get("UNIT_ID"));
				item.setUnitNmKor((String)map.get("UNIT_NM_KOR"));
				item.setUnitNmEng((String)map.get("UNIT_NM_ENG"));
			}else{ // 2014.08.19 현재 보이는 항목이 1개이지만...
				List itemListAll = statHtmlDAO.getItemListAll(paramMap); // 전체 항목은 몇개냐?
				
				if( itemListAll.size() > 1){ // 현재 보이는 항목이 1개고 전체 항목이 1개 이상이라는건 조회 조건에서 1개만 선택했다는것...그럴땐 항목단위를 보여라... 
					item.setUnitId((String)map.get("UNIT_ID"));
					item.setUnitNmKor((String)map.get("UNIT_NM_KOR"));
					item.setUnitNmEng((String)map.get("UNIT_NM_ENG"));
				}
			}
			
			item.setExistCmmtKor( ((String)map.get("CMMT_AT_KOR")).equals("Y") ? true : false );
			item.setExistCmmtEng( ((String)map.get("CMMT_AT_ENG")).equals("Y") ? true : false );
			item.setDataOpt(paramInfo.getDataOpt());
			item.setObjVarId("13999001");
			
			if(paramInfo.getDataOpt().indexOf("en") > -1){
				//영문
				if(item.isExistCmmtEng()){
					paramMap.put("itmId", (String)map.get("ITM_ID"));
					paramMap.put("cmmtSe", "1210611");
					paramMap.put("lngSe", "1211911");
					list = statHtmlDAO.getClassItemCmmt(paramMap);
					
					setCmmtInfo(list, item);
				}
				
			}else{
				//한글 및 코드보기
				if(item.isExistCmmtKor()){
					paramMap.put("itmId", (String)map.get("ITM_ID"));
					paramMap.put("cmmtSe", "1210611");
					paramMap.put("lngSe", "1211910");
					list = statHtmlDAO.getClassItemCmmt(paramMap);
					
					setCmmtInfo(list, item);
				}
			}
			
			this.addItem(item);
		}
	}
	
	private void setCmmtInfo(List list, Item item){
		CmmtInfo cmmtInfo = new CmmtInfo();
		cmmtInfo.setCmmtNo(cmmtInfoManager.cmmtNo);
		cmmtInfo.setCmmtSe("1210611");
		item.setCmmtNo(cmmtInfoManager.cmmtNo);
		item.setCmmtSe("1210611");
			
		if(paramInfo.getDataOpt().indexOf("en") > -1){
			//영문
			cmmtInfo.setTitle(this.getNameEng() + " > " + item.getNameEng());
		}else{
			//한글, 코드
			cmmtInfo.setTitle(this.getNameKor() + " > " + item.getNameKor());
		}
		
		cmmtInfo.setContent(StatPivotUtil.makeStrCmmt(list));
		
		cmmtInfoManager.cmmtList.add(cmmtInfo);
		cmmtInfoManager.cmmtNo++;
	}

}
