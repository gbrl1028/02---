package nurimsoft.stat.pivot;

import java.util.HashMap;
import java.util.Map;

public class Measure {

	//수치값
	private String dtvalCo;
	//소수점
	private String periodCo;
	//가중치
	private String wgtCo;
	//심볼
	private String smblCn;
	//분석값
	private String analCo;

	private String unitId;
	private String unitNmKor;
	private String unitNmEng;

	private boolean isExistCmmtKor;//분류 주석여부(한글)
	private boolean isExistCmmtEng;//분류 주석여부(영문)

	//차원번호
	private String  itmRcgnSn;
	//주석정보
	private int cmmtNo;
	private String cmmtSe;

	private Map<String, String> exportMap = new HashMap<>();

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
	public String getItmRcgnSn() {
		return itmRcgnSn;
	}
	public void setItmRcgnSn(String itmRcgnSn) {
		this.itmRcgnSn = itmRcgnSn;
	}
	public String getDtvalCo() {
		return dtvalCo;
	}
	public void setDtvalCo(String dtvalCo) {
		this.dtvalCo = dtvalCo;
	}
	public String getPeriodCo() {
		return periodCo;
	}
	public void setPeriodCo(String periodCo) {
		this.periodCo = periodCo;
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
	public String getWgtCo() {
		return wgtCo;
	}
	public void setWgtCo(String wgtCo) {
		this.wgtCo = wgtCo;
	}
	public String getSmblCn() {
		return smblCn;
	}
	public void setSmblCn(String smblCn) {
		this.smblCn = smblCn;
	}
	public String getAnalCo() {
		return analCo;
	}
	public void setAnalCo(String analCo) {
		this.analCo = analCo;
	}

	public Map<String, String> getExportMap() {
		return exportMap;
	}

	public void setExportMap(Map<String, String> exportMap) {
		this.exportMap = exportMap;
	}
}
