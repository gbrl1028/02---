package nurimsoft.stat.pivot;

import nurimsoft.stat.info.CmmtInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.webapp.StatHtmlDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDimension extends Dimension{

	private boolean isExistCmmtKor;//분류 주석여부(한글)
	private boolean isExistCmmtEng;//분류 주석여부(영문)

	//주석정보
	private int cmmtNo;
	private String cmmtSe;

	Map<String, String> paramMap = new HashMap();

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public ClassDimension(ParamInfo paramInfo, String classCode, StatHtmlDAO statHtmlDAO, CmmtInfoManager cmmtInfoManager){
		super(paramInfo, classCode, statHtmlDAO, cmmtInfoManager);

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

		//분류정보 가져오기 - 한글명,  영문명, 주석여부
		Map classMap = statHtmlDAO.getDimensionInfo(paramMap);

		this.setExistCmmtKor( ((String)classMap.get("CMMT_AT_KOR")).equals("Y") ? true : false );
		this.setExistCmmtEng( ((String)classMap.get("CMMT_AT_ENG")).equals("Y") ? true : false );
		this.setNameKor((String)classMap.get("SCR_KOR"));
		this.setNameEng((String)classMap.get("SCR_ENG"));
		super.setVarOrdSn( ((BigDecimal)classMap.get("VAR_ORD_SN")).intValue() );
	}

	public void setInfo(){
		//분류주석셋팅
		setDimensionCmmtInfo();
		//분류값 셋팅
		setItemList();
	}

	private void setDimensionCmmtInfo(){

		List<String> list = new ArrayList<String>();

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			//영문
			if(this.isExistCmmtEng()){
				paramMap.put("cmmtSe", "1210612");
				paramMap.put("lngSe", "1211911");
				list = statHtmlDAO.getClassCmmt(paramMap);

				setCmmtInfo(list, 1, null);
			}

		}else{
			//한글 및 코드보기
			if(this.isExistCmmtKor()){
				paramMap.put("cmmtSe", "1210612");
				paramMap.put("lngSe", "1211910");
				list = statHtmlDAO.getClassCmmt(paramMap);

				setCmmtInfo(list, 1, null);
			}
		}

		paramMap.put("targetId", "OV_L" + varOrdSn + "_ID");

	}

	private void setItemList(){
		List itemList = statHtmlDAO.getDimensionItemList(paramMap, "class");

		int maxLvl = 1;

		int level = 1;
		Map map = null;
		Item item = null;
		List<String> list = null;	//주석정보 담을 list
		for(int i = 0; i < itemList.size(); i++){
			map = (Map)itemList.get(i);
			level = ((BigDecimal)map.get("LVL")).intValue();
			item = new Item(this.code, (String)map.get("ITM_ID"), (String)map.get("SCR_KOR"), (String)map.get("SCR_ENG"), level
					, (String)map.get("PARENT_CODE"), (String)map.get("PARENT_NAME_KOR"), (String)map.get("PARENT_NAME_ENG"));
			item.setUnitId((String)map.get("UNIT_ID"));
			item.setUnitNmKor((String)map.get("UNIT_NM_KOR"));
			item.setUnitNmEng((String)map.get("UNIT_NM_ENG"));
			item.setExistCmmtKor( ((String)map.get("CMMT_AT_KOR")).equals("Y") ? true : false );
			item.setExistCmmtEng( ((String)map.get("CMMT_AT_ENG")).equals("Y") ? true : false );
			item.setDataOpt(paramInfo.getDataOpt());

			if(paramInfo.getDataOpt().indexOf("en") > -1){
				//영문
				if(item.isExistCmmtEng()){
					paramMap.put("itmId", (String)map.get("ITM_ID"));
					paramMap.put("cmmtSe", "1210613");
					paramMap.put("lngSe", "1211911");
					list = statHtmlDAO.getClassItemCmmt(paramMap);

					setCmmtInfo(list, 2, item);
				}

			}else{
				//한글 및 코드보기
				if(item.isExistCmmtKor()){
					paramMap.put("itmId", (String)map.get("ITM_ID"));
					paramMap.put("cmmtSe", "1210613");
					paramMap.put("lngSe", "1211910");
					list = statHtmlDAO.getClassItemCmmt(paramMap);

					setCmmtInfo(list, 2, item);
				}
			}

			//item.setCmmtInfo(cmmtInfo);	//주석정보 셋팅

			if(level > maxLvl){
				maxLvl = level;
			}

			this.addItem(item);
		}

		maxLevel = maxLvl;
	}

	//type 1 : 분류, type 2 : 분류값
	private void setCmmtInfo(List list, int type, Item item){
		CmmtInfo cmmtInfo = new CmmtInfo();
		cmmtInfo.setCmmtNo(cmmtInfoManager.cmmtNo);
		if(type == 1){
			cmmtInfo.setCmmtSe("1210612");
			this.cmmtNo = cmmtInfoManager.cmmtNo;
			this.cmmtSe = "1210612";

			if(paramInfo.getDataOpt().indexOf("en") > -1){
				//영문
				cmmtInfo.setTitle(this.getNameEng());
			}else{
				//한글, 코드
				cmmtInfo.setTitle(this.getNameKor());
			}

		}else{
			cmmtInfo.setCmmtSe("1210613");
			item.setCmmtNo(cmmtInfoManager.cmmtNo);
			item.setCmmtSe("1210613");

			if(paramInfo.getDataOpt().indexOf("en") > -1){
				//영문
				cmmtInfo.setTitle(this.getNameEng() + " > " + item.getNameEng());
			}else{
				//한글, 코드
				cmmtInfo.setTitle(this.getNameKor() + " > " + item.getNameKor());
			}
		}
		cmmtInfo.setContent(StatPivotUtil.makeStrCmmt(list));

		cmmtInfoManager.cmmtList.add(cmmtInfo);
		cmmtInfoManager.cmmtNo++;
	}

	public boolean isExistCmmtKor() {
		return isExistCmmtKor;
	}

	public void setExistCmmtKor(boolean isExistCmmtKor) {
		this.isExistCmmtKor = isExistCmmtKor;
	}

	public boolean isExistCmmtEng() {
		return isExistCmmtEng;
	}

	public void setExistCmmtEng(boolean isExistCmmtEng) {
		this.isExistCmmtEng = isExistCmmtEng;
	}

	public int getCmmtNo() {
		return cmmtNo;
	}

	public void setCmmtNo(int cmmtNo) {
		this.cmmtNo = cmmtNo;
	}

	public String getCmmtSe() {
		return cmmtSe;
	}

	public void setCmmtSe(String cmmtSe) {
		this.cmmtSe = cmmtSe;
	}
}
