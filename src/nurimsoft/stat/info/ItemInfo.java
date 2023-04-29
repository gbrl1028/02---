package nurimsoft.stat.info;

import java.util.List;
import java.util.Map;

public class ItemInfo {

	private final String itmId = "13999001";
	private final String itmEngNm = "ITEM";
	
	private String itmNm;
	
	private int itmCnt;
	
	private List<Map> itmList;
	
	//DefaultSettingManager가 해야 할일
	private int defaultItmCnt;
	
	//선택되어진 항목을  담는다.
	private List<String> defaultItmList;
	
	public String getItmId() {
		return itmId;
	}
	
	public String getItmEngNm() {
		return itmEngNm;
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

	public String getItmNm() {
		return itmNm;
	}

	public void setItmNm(String itmNm) {
		this.itmNm = itmNm;
	}
}

