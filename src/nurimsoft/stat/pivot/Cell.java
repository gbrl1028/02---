package nurimsoft.stat.pivot;

import java.util.HashMap;
import java.util.Map;

public class Cell{

	private int type;
	private String style;
	private String value;
	private String text;
	private int colspan;
	private boolean isFirst;
	private boolean isDummy;
	private int level;
	private int varOrdSn;	//Dimension Cell인 경우에만 들어가겠지.

	//주석정보
	//cmmtSe, objVarid, itmRcgnSn과 value는 주석링크를 위한 파라미터로 사용
	private int cmmtNo;			//주석번호
	private String cmmtSe;		//주석구분값
	private String objVarId = "@null@";	//분류 및 분류값, 항목을 찾기위함.
	private String itmId = "@null@";
	private String itmRcgnSn = "@null@";	//차원주석을 찾기위함.

	//2014.12 차트를 위한 단위정보 추가(항목의 단위를 표시)
	private String chartUnit;
	private String rowHeadId;	//차트 범례 생성을 위함(AYS)

	private Map<String, String> exportMap = new HashMap<>();

	public String getRowHeadId() {
		return rowHeadId;
	}

	public void setRowHeadId(String rowHeadId) {
		this.rowHeadId = rowHeadId;
	}

	public String getChartUnit() {
		return (chartUnit == null) ? "" : chartUnit;
	}

	public void setChartUnit(String chartUnit) {
		this.chartUnit = chartUnit;
	}

	public Cell(int type){
		this.type = type;
	}

	public Cell(int type, String value, String text, int varOrdSn , String rowHeadId){
		this.type = type;
		this.value = value;
		this.text = text;
		this.varOrdSn = varOrdSn;
		this.rowHeadId = rowHeadId;
	}

	public Cell(int type, String style, String value, String text){
		this.type = type;
		this.style = style;
		this.value = value;
		this.text = text;
	}

	public Cell(int type, String style, String value, String text, int varOrdSn){
		this.type = type;
		this.style = style;
		this.value = value;
		this.text = text;
		this.varOrdSn = varOrdSn;
	}

	public int getVarOrdSn() {
		return varOrdSn;
	}

	public void setVarOrdSn(int varOrdSn) {
		this.varOrdSn = varOrdSn;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
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

	public String getObjVarId() {
		return objVarId;
	}

	public void setObjVarId(String objVarId) {
		this.objVarId = objVarId;
	}

	public String getItmId() {
		return itmId;
	}

	public void setItmId(String itmId) {
		this.itmId = itmId;
	}

	public String getItmRcgnSn() {
		return itmRcgnSn;
	}

	public void setItmRcgnSn(String itmRcgnSn) {
		this.itmRcgnSn = itmRcgnSn;
	}

	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	public Map<String, String> getExportMap() {
		return exportMap;
	}

	public void setExportMap(Map<String, String> exportMap) {
		this.exportMap = exportMap;
	}
}
