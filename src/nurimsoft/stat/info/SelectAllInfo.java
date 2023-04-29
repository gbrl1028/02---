package nurimsoft.stat.info;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nurimsoft.stat.manager.DefaultSettingManager;
import nurimsoft.stat.util.PropertyManager;

public class SelectAllInfo {
		
	private List selectItmList;	//선택한 항목정보

	//주기별 시점정보
	private List selectTimeList;
	
	//분류정보
	private List<ClassInfo> classInfoList;

	public List getSelectItmList() {
		return selectItmList;
	}

	public void setSelectItmList(List selectItmList) {
		this.selectItmList = selectItmList;
	}

	public List getSelectTimeList() {
		return selectTimeList;
	}

	public void setSelectTimeList(List selectTimeList) {
		this.selectTimeList = selectTimeList;
	}

	public List<ClassInfo> getClassInfoList() {
		return classInfoList;
	}

	public void setClassInfoList(List<ClassInfo> classInfoList) {
		this.classInfoList = classInfoList;
	}
	
	
	
}
