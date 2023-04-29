package nurimsoft.stat.info;

import java.util.ArrayList;
import java.util.List;

public class PivotInfo {
	
	//표두
	private List<String> colList = new ArrayList();
	private List<String> colNmList = new ArrayList();
	private List<String> colEngNmList = new ArrayList();
	//표측
	private List<String> rowList = new ArrayList();
	private List<String> rowNmList = new ArrayList();
	private List<String> rowEngNmList = new ArrayList();
	
	public List<String> getColList() {
		return colList;
	}
	public void setColList(List<String> colList) {
		this.colList = colList;
	}
	public List<String> getColNmList() {
		return colNmList;
	}
	public void setColNmList(List<String> colNmList) {
		this.colNmList = colNmList;
	}
	public List<String> getColEngNmList() {
		return colEngNmList;
	}
	public void setColEngNmList(List<String> colEngNmList) {
		this.colEngNmList = colEngNmList;
	}
	public List<String> getRowList() {
		return rowList;
	}
	public void setRowList(List<String> rowList) {
		this.rowList = rowList;
	}
	public List<String> getRowNmList() {
		return rowNmList;
	}
	public void setRowNmList(List<String> rowNmList) {
		this.rowNmList = rowNmList;
	}
	public List<String> getRowEngNmList() {
		return rowEngNmList;
	}
	public void setRowEngNmList(List<String> rowEngNmList) {
		this.rowEngNmList = rowEngNmList;
	}
	
}
