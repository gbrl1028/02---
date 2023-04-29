package nurimsoft.stat.info;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.psl.dataaccess.util.EgovMap;

import nurimsoft.stat.manager.DefaultSettingManager;
import nurimsoft.stat.util.PropertyManager;

public class RelationInfo {
	
	//관련통계표정보
	private List<EgovMap> RelationList;

	private String relSn;
	private String relOrgId;
	private String relOrgNm;
	private String relTblId;
	private String relTblNm;
	private String statId;
	private String statNm;
	private String statEngNm;
	private String refNm;
	private String refEngNm;
	private String relKorDetail;
	private String orgId;
	private String tblId;
	private String relOrgEngNm;
	private String relTblEngNm;
	private String relEngDetail;
	private String lastPrdDe;
	
	
	public String getLastPrdDe() {
		return lastPrdDe;
	}

	public void setLastPrdDe(String lastPrdDe) {
		this.lastPrdDe = lastPrdDe;
	}

	public String getRelEngDetail() {
		return relEngDetail;
	}

	public void setRelEngDetail(String relEngDetail) {
		this.relEngDetail = relEngDetail;
	}

	public String getRelOrgEngNm() {
		return relOrgEngNm;
	}

	public void setRelOrgEngNm(String relOrgEngNm) {
		this.relOrgEngNm = relOrgEngNm;
	}

	public String getRelTblEngNm() {
		return relTblEngNm;
	}

	public void setRelTblEngNm(String relTblEngNm) {
		this.relTblEngNm = relTblEngNm;
	}

	public String getRelSn() {
		return relSn;
	}

	public void setRelSn(String relSn) {
		this.relSn = relSn;
	}

	public String getRelOrgId() {
		return relOrgId;
	}

	public void setRelOrgId(String relOrgId) {
		this.relOrgId = relOrgId;
	}

	public String getRelOrgNm() {
		return relOrgNm;
	}

	public void setRelOrgNm(String relOrgNm) {
		this.relOrgNm = relOrgNm;
	}

	public String getRelTblId() {
		return relTblId;
	}

	public void setRelTblId(String relTblId) {
		this.relTblId = relTblId;
	}

	public String getRelTblNm() {
		return relTblNm;
	}

	public void setRelTblNm(String relTblNm) {
		this.relTblNm = relTblNm;
	}

	public String getrelKorDetail() {
		return relKorDetail;
	}

	public void setrelKorDetail(String relKorDetail) {
		this.relKorDetail = relKorDetail;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getTblId() {
		return tblId;
	}

	public void setTblId(String tblId) {
		this.tblId = tblId;
	}

	public List<EgovMap> getRelationList() {
		return RelationList;
	}

	public void setRelationList(List<EgovMap> relationList) {
		RelationList = relationList;
	}	
	public String getStatId() {
		return statId;
	}

	public void setStatId(String statId) {
		this.statId = statId;
	}

	public String getStatNm() {
		return statNm;
	}

	public void setStatNm(String statNm) {
		this.statNm = statNm;
	}

	public String getStatEngNm() {
		return statEngNm;
	}

	public void setStatEngNm(String statEngNm) {
		this.statEngNm = statEngNm;
	}

	public String getRefNm() {
		return refNm;
	}

	public void setRefNm(String refNm) {
		this.refNm = refNm;
	}

	public String getRefEngNm() {
		return refEngNm;
	}

	public void setRefEngNm(String refEngNm) {
		this.refEngNm = refEngNm;
	}


}