package nurimsoft.stat.info;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nurimsoft.stat.manager.DefaultSettingManager;
import nurimsoft.stat.util.PropertyManager;
/* 조회범위 상세 설정 */
public class SelectRangeInfo {
	//기본 정보 세팅
	//항목정보
	private ItemInfo itemInfo;
	
	//분류정보
	private List<ClassInfo> classInfoList;

	
	//사용자가 선택항 정보 세팅
	private List selectItmList;	//선택한 항목정보

	//시점탭
	private List tabTimeList;
	
	
	//주기별 시점정보
	private PeriodInfo periodInfo;
	
	//분류정보
	private List<ClassInfo> selectClassList;
	
	public ItemInfo getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(ItemInfo itemInfo) {
		this.itemInfo = itemInfo;
	}

	public List<ClassInfo> getClassInfoList() {
		return classInfoList;
	}

	public void setClassInfoList(List<ClassInfo> classInfoList) {
		this.classInfoList = classInfoList;
	}

	public List getSelectItmList() {
		return selectItmList;
	}

	public void setSelectItmList(List selectItmList) {
		this.selectItmList = selectItmList;
	}

	public List getTabTimeList() {
		return tabTimeList;
	}

	public void setTabTimeList(List tabTimeList) {
		this.tabTimeList = tabTimeList;
	}

	public List<ClassInfo> getSelectClassList() {
		return selectClassList;
	}

	public void setSelectClassList(List<ClassInfo> selectClassList) {
		this.selectClassList = selectClassList;
	}

	public PeriodInfo getPeriodInfo() {
		return periodInfo;
	}

	public void setPeriodInfo(PeriodInfo periodInfo) {
		this.periodInfo = periodInfo;
	}

}
