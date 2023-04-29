package nurimsoft.stat.info;

import java.util.ArrayList;
import java.util.List;

import nurimsoft.stat.util.StringUtil;

public class MetaInfo {

	private char lang = 'k';

	//통계표 메타자료
	private String metadataTitle;
	//통계표ID, Table ID
	private String tblId;
	//통계표명,Table Name
	private String tblNm;
	//수록기간, Term
	private String containPeriod;
	//조회기간, Search Period
	private String searchPeriod;
	//출처, Source
	private String source;
	//문의처, iquire
	private String iquire;
	//자료다운일자 : Download Data
	private String downDate;
	//단위, Unit : 통계표단위
	private String unit;
	//통계표URL : 한글에서만 생성
	private String url;
	//주석
	private List cmmtList = new ArrayList();

	private final String metaDataTitle_KO = "통계표 메타자료";
	private final String metaDataTitle_EN = "Statistics metadata";
	private final String tblIdTitle_KO = "통계표ID";
	private final String tblIdTitle_EN = "Table ID";
	private final String tblNmTitle_KO = "통계표명";
	private final String tblNmTitle_EN = "Table Name";
	private final String containPeriodTitle_KO = "수록기간";
	private final String containPeriodTitle_EN = "Term";
	private final String searchPeriodTitle_KO = "조회기간";
	private final String searchPeriodTitle_EN = "Search Period";
	private final String sourceTitle_KO = "출처";
	private final String sourceTitle_EN = "Source";
	private final String iquireTitle_KO = "문의처";
	private final String iquireTitle_EN = "Iquire";
	private final String downDateTitle_KO = "자료다운일자";
	private final String downDateTitle_EN = "Download Date";
	private final String unitTitle_KO = "단위";
	private final String unitTitle_EN = "Unit";
	private final String urlTitle_KO = "통계표URL";
	private final String urlTitle_EN = "Table URL";
	private final String cmmtTitle_KO = "주석";
	private final String cmmtTitle_EN = "Comment";

	public MetaInfo(String dataOpt){
		if(dataOpt.indexOf("en") > -1){
			lang = 'e';
		}
	}

	public String getmetaDataTitle(){
		return (lang == 'e') ? metaDataTitle_EN : metaDataTitle_KO;
	}

	public String getTblIdTitle(){
		return (lang == 'e') ? tblIdTitle_EN : tblIdTitle_KO;
	}

	public String getTblNmTitle(){
		return (lang == 'e') ? tblNmTitle_EN : tblNmTitle_KO;
	}

	public String getContainPeriodTitle(){
		return (lang == 'e') ? containPeriodTitle_EN : containPeriodTitle_KO;
	}

	public String getSearchPeriodTitle(){
		return (lang == 'e') ? searchPeriodTitle_EN : searchPeriodTitle_KO;
	}

	public String getSourceTitle(){
		return (lang == 'e') ? sourceTitle_EN : sourceTitle_KO;
	}

	public String getIquireTitle(){
		return (lang == 'e') ? iquireTitle_EN : iquireTitle_KO;
	}

	public String getDownDateTitle(){
		return (lang == 'e') ? downDateTitle_EN : downDateTitle_KO;
	}

	public String getUnitTitle(){
		return (lang == 'e') ? unitTitle_EN : unitTitle_KO;
	}

	public String getUrlTitle(){
		return (lang == 'e') ? urlTitle_EN : urlTitle_KO;
	}

	public String getCmmtTitle(){
		return (lang == 'e') ? cmmtTitle_EN : cmmtTitle_KO;
	}

	public String getMetadataTitle() {
		return metadataTitle;
	}
	public void setMetadataTitle(String metadataTitle) {
		this.metadataTitle = metadataTitle;
	}

	public String getTblId() {
		return tblId;
	}
	public void setTblId(String tblId) {
		this.tblId = tblId;
	}
	public String getTblNm() {
		return tblNm;
	}
	public void setTblNm(String tblNm) {
		this.tblNm = tblNm;
	}
	public String getContainPeriod() {
		return containPeriod;
	}
	public void setContainPeriod(String containPeriod) {
		this.containPeriod = containPeriod;
	}
	public String getSearchPeriod() {
		return searchPeriod;
	}
	public void setSearchPeriod(String searchPeriod) {
		this.searchPeriod = searchPeriod;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getIquire() {
		return iquire;
	}
	public void setIquire(String iquire) {
		this.iquire = iquire;
	}
	public String getDownDate() {
		return downDate;
	}
	public void setDownDate(String downDate) {
		this.downDate = downDate;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List getCmmtList() {
		return cmmtList;
	}
	public void setCmmtList(List cmmtList) {
		this.cmmtList = cmmtList;
	}

}
