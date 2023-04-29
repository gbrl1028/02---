package nurimsoft.stat.pivot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.CmmtInfo;

//항목 정보를 담는다.
/**
 * @author leekyujeong
 *
 */
public class Item {

	private String objVarId;

	private String code;
	private String nameKor;
	private String nameEng;
	private int level;
	private String parentCode;
	private String parentNameKor;
	private String parentNameEng;
	private String unitId;
	private String unitNmKor;
	private String unitNmEng;

	private String dataOpt;

	private boolean isExistCmmtKor;//분류 주석여부(한글)
	private boolean isExistCmmtEng;//분류 주석여부(영문)

	//주석정보
	private int cmmtNo;
	private String cmmtSe;

	public Item(String objVarId, String code, String nameKor, String nameEng, int level, String parentCode, String parentNameKor, String parentNameEng){
		this.objVarId = objVarId;
		this.code = code;
		this.nameKor = nameKor;
		this.nameEng = nameEng;
		this.level = level;
		this.parentCode = parentCode;
		this.parentNameKor = parentNameKor;
		this.parentNameEng = parentNameEng;
	}

	public Item(String code, String nameKor, String nameEng){
		this(null, code, nameKor, nameEng, 1, null, null, null);
	}

	public Item(String code, String name){
		this(null, code, name, name, 1, null, null, null);
	}

	public Item(String code){
		this(null, code, null, null, 1, null, null, null);
	}

	public String getObjVarId() {
		return objVarId;
	}

	public void setObjVarId(String objVarId) {
		this.objVarId = objVarId;
	}

	public String getParentCode() {
		return parentCode;
	}

	public String getParentNameKor() {
		return parentNameKor;
	}

	public String getParentNameEng() {
		return parentNameEng;
	}

	public String[] getParentCodeAsArray() {

		String[] strArr = null;

		if(parentCode != null){
			strArr = parentCode.split("@");
		}
		return strArr;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String[] getParentNameAsArray() {

		String[] strArr = null;

		if(dataOpt.equals("ko")){
			if(parentNameKor != null){
				strArr = parentNameKor.split("@");
			}
		}else if(dataOpt.equals("en")){
			if(parentNameEng != null){
				strArr = parentNameEng.split("@");
			}
		}else if(dataOpt.equals("cd")){
			if(parentNameKor != null){
				strArr = parentCode.split("@");
			}
		}else if(dataOpt.equals("cdko")){

			String[] tmpCodeArr = null;
			String[] tmpNameArr = null;

			if(parentCode != null){
				tmpCodeArr = parentCode.split("@");
			}

			if(parentNameKor != null){
				tmpNameArr = parentNameKor.split("@");
			}

			if(tmpCodeArr != null){
				strArr = new String[tmpCodeArr.length];
				for(int i = 0; i < tmpCodeArr.length; i++){
					strArr[i] = tmpCodeArr[i] + " " + tmpNameArr[i].trim();
				}
			}

		}else if(dataOpt.equals("cden")){

			String[] tmpCodeArr = null;
			String[] tmpNameArr = null;

			if(parentCode != null){
				tmpCodeArr = parentCode.split("@");
			}

			if(parentNameEng != null){
				tmpNameArr = parentNameEng.split("@");
			}

			if(tmpCodeArr != null){
				strArr = new String[tmpCodeArr.length];
				for(int i = 0; i < tmpCodeArr.length; i++){
					strArr[i] = tmpCodeArr[i] + " " + tmpNameArr[i].trim();
				}
			}

		}

		return strArr;
	}


	public void getParentName() {
		//this.parentNameKor = parentNameKor;
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {

		String retName = null;

		if(dataOpt.equals("ko")){
			retName = nameKor + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitNmKor + ")" : "" );
		}else if(dataOpt.equals("en")){ // 2014.10.01 영문단위 없을때는 빈 가로도 보여주지 말것 - 김경호
			//retName = nameEng + ( (unitId != null && unitId.trim().length() > 0 ) ? " (" + unitNmEng + ")" : "" );
			retName = nameEng ;
			
			if( unitId != null && unitId.trim().length() > 0 ){
				if( unitNmEng != null && unitNmEng != " " ){					
					retName += " (" + unitNmEng + ")";
				}else{
					retName += " " + unitNmEng;
				}
			}
		}else if(dataOpt.equals("cd")){
			retName = code + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitId + ")" : "" );
		}else if(dataOpt.equals("cdko")){
			retName = code + " " + nameKor + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitId + " " + unitNmKor + ")" : "" );
		}else if(dataOpt.equals("cden")){
			retName = code + " " + nameEng + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitId + " " + unitNmEng + ")" : "" );
		}

		if(retName == null){
			retName = "";
		}

		return retName;
	}

	//한글, 영문 동시에 적용됨..시점에 잠정치, 추정치와 같은 데이터가 있는 경우에만 사용..그 이외에는 사용하기 없기
	public void setName(String name){
		this.nameKor = name;
		this.nameEng = name;
	}

	public String getNameKor(){
		return nameKor;
	}

	public String getNameEng(){
		return nameEng;
	}

	public void setNameKor(String nameKor) {
		this.nameKor = nameKor;
	}

	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}

	public String getDataOpt() {
		return dataOpt;
	}

	public void setDataOpt(String dataOpt) {
		this.dataOpt = dataOpt;
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

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitNmKor() {
		return (unitNmKor == null) ? "" : unitNmKor;
	}

	public void setUnitNmKor(String unitNmKor) {
		this.unitNmKor = unitNmKor;
	}

	public String getUnitNmEng() {
		return (unitNmEng == null) ? "" : unitNmEng.replace("(", "").replace(")", ""); //2015.3.13 단위명에 포함된 (, ) 제거
	}

	public void setUnitNmEng(String unitNmEng) {
		this.unitNmEng = unitNmEng;
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

	//2014.12 차트에 표현할 단위를 가져오는 메소드
	public String getChartUnitNm(){
		String retName = null;

		if(unitId == null || unitId.trim().equals("")){
			retName = "";
		}

		if(dataOpt.equals("ko")){
			retName = (unitNmKor == null) ? "" : " [" + unitNmKor + "]";
		}else if(dataOpt.equals("en")){
			retName = (unitNmEng == null) ? "" : " [" + unitNmEng + "]";
		}else if(dataOpt.equals("cd")){
			retName = "[" + unitId + "]";
		}else if(dataOpt.equals("cdko")){
			retName = " [" + unitId + ( (unitNmKor == null) ? "]" : unitNmKor + "]" );
		}else if(dataOpt.equals("cden")){
			retName = " [" + code + ( (unitNmEng == null) ? "]" : unitNmEng + "]" );
		}

		return retName;
	}

}
