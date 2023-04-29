package nurimsoft.stat.info;

import java.util.List;
import java.util.Map;

public class ClassInfo implements Comparable<ClassInfo>{

	private String classId;
	private String classNm;
	private String classEngNm;
	private int itmCnt;
	private String sn;					//pivot 순서를 적용하기 위함.
	private String depthLvl;			//LVL_CO 분류가 가지고 있는 레벨, SCR_LVL_CO 화면에 출력에 해야할 레벨
	private boolean visible;
	private String totIgnrAt;		//총계무시여부

	//1레벨만 담는다.
	private List<Map> itmList;

	private List<Map> classAllList;		//사용자가 선택한 분류리스트정보
	//DefaultSettingManager가 해야 할일
	private int defaultItmCnt;

	//선택되어진 분류값을  담는다.(담당자.. 1#분류값코드
	private List<String> defaultItmList;

	//2014.01.13 varOrdSn 추가 - 분류순서
	private String varOrdSn;

	//2014.04.04 분류레벨별 카운트 리스트
	private List ListClassLvl;

	public String getVarOrdSn() {
		return varOrdSn;
	}

	public void setVarOrdSn(String varOrdSn) {
		this.varOrdSn = varOrdSn;
	}

	public String getTotIgnrAt() {
		return totIgnrAt;
	}

	public void setTotIgnrAt(String totIgnrAt) {
		this.totIgnrAt = totIgnrAt;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassNm() {
		return classNm;
	}

	public void setClassNm(String classNm) {
		this.classNm = classNm;
	}

	public String getClassEngNm() {
		return classEngNm;
	}

	public void setClassEngNm(String classEngNm) {
		this.classEngNm = classEngNm;
	}

	public int getItmCnt() {
		return itmCnt;
	}

	public void setItmCnt(int itmCnt) {
		this.itmCnt = itmCnt;
	}

	public List getItmList() {
		return itmList;
	}

	public void setItmList(List itmList) {
		this.itmList = itmList;
	}

	public List getClassAllList() {
		return classAllList;
	}

	public void setClassAllList(List classAllList) {
		this.classAllList = classAllList;
	}

	public int getDefaultItmCnt() {
		return defaultItmCnt;
	}

	public void setDefaultItmCnt(int defaultItmCnt) {
		this.defaultItmCnt = defaultItmCnt;
	}

	public List getDefaultItmList() {
		return defaultItmList;
	}

	public void setDefaultItmList(List defaultItmList) {
		this.defaultItmList = defaultItmList;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getDepthLvl() {
		return depthLvl;
	}

	public void setDepthLvl(String depthLvl) {
		this.depthLvl = depthLvl;
	}
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public int compareTo(ClassInfo classInfo){
		return sn.compareTo(classInfo.getSn());

	}

	public List getListClassLvl() {
		return ListClassLvl;
	}

	public void setListClassLvl(List listClassLvl) {
		ListClassLvl = listClassLvl;
	}

}
