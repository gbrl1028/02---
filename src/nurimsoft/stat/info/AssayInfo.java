package nurimsoft.stat.info;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nurimsoft.stat.manager.DefaultSettingManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.webapp.StatHtmlDAO;

public class AssayInfo {

	//항목정보
	private List assayItmList;

	//주기별 시점정보
	private List assayTimeList;

	//분류정보
	private List<ClassInfo> classInfoList;

	//분석종류
	private List assayTypeList;

	//분석주석
	private List assayTypeCmmtList;

	//비교기준
	private List compareTypeList;

	public List<ClassInfo> getClassInfoList() {
		return classInfoList;
	}

	public void setClassInfoList(List<ClassInfo> classInfoList) {
		this.classInfoList = classInfoList;
	}

	public List getAssayItmList() {
		return assayItmList;
	}

	public void setAssayItmList(List assayItmList) {
		this.assayItmList = assayItmList;
	}

	public List getAssayTimeList() {
		return assayTimeList;
	}

	public void setAssayTimeList(List assayTimeList) {
		this.assayTimeList = assayTimeList;
	}

	public List getAssayTypeList() {
		return assayTypeList;
	}

	public void setAssayTypeList(List assayTypeList) {
		this.assayTypeList = assayTypeList;
	}

	public List getCompareTypeList() {
		return compareTypeList;
	}

	public void setCompareTypeList(List compareTypeList) {
		this.compareTypeList = compareTypeList;
	}

	public List getAssayTypeCmmtList() {
		return assayTypeCmmtList;
	}

	public void setAssayTypeCmmtList(List assayTypeCmmtList) {
		this.assayTypeCmmtList = assayTypeCmmtList;
	}

}
