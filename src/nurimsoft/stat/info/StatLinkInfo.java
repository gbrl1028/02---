package nurimsoft.stat.info;

import nurimsoft.stat.util.PropertyManager;

/**
 * @author leekyujeong
 * 출처 및 링크 서비스 정보
 */
public class StatLinkInfo {
	
	private String statId;		//조사ID
	private String pubLink;		//온라인 간행물 link
	private String newsLink;	//보도자료 link
	private String confmNo;		//meta.narastat.kr로 넘겨줄 변수(SNO : 승인번호)
	private String inptYear;	//출처의 더보기 버튼 활성화 여부를 판단하기 위한 변수 null이 아닌 경우에만 더보기 버튼 활성화
	private String appAt;		//승인, 비승인 여부
	private String urlCn;		//문의처 링크 URL
	private String orgEngNm;	//TN_ORG의 ORG_ENG_NM, 2013.11.29 추가(영문보기 시 출처부분에 조직영문명을 출력하기 위함)
	private String snameeng;	//C_STAT의 snameeng, 2013.12.18 추가(영문보기 시 출처부분에 출처명을 출력하기 위함)
	
	private String linkStatSno = PropertyManager.getInstance().getProperty("link.stat.sno");			//출처 더보기 base url
	private String linkStatComment = PropertyManager.getInstance().getProperty("link.stat.comment");	//통계 설명자료 base url
	
	private boolean existStatNm;		//출처 유무
	private String exprStatNm;			//화면에 표시될 출처정보 문자
	private boolean existMoreBtn;		//더보기 버튼 유무
	private String exprInquire;			//화면에 표시될 문의처 문자
	private boolean existInquire;		//문의처 유무
	private boolean existInquireLink;	//문의처 링크 유무
	
	//SIGA url
	private String sigaUrl = PropertyManager.getInstance().getProperty("link.siga");
	
	public String getSnameeng() {
		return snameeng;
	}
	public void setSnameeng(String snameeng) {
		this.snameeng = snameeng;
	}
	
	public String getOrgEngNm() {
		return orgEngNm;
	}
	public void setOrgEngNm(String orgEngNm) {
		this.orgEngNm = orgEngNm;
	}
	
	public String getSigaUrl() {
		return sigaUrl;
	}
	public void setSigaUrl(String sigaUrl) {
		this.sigaUrl = sigaUrl;
	}
	public String getStatId() {
		return statId;
	}
	public void setStatId(String statId) {
		this.statId = statId;
	}
	public String getPubLink() {
		return pubLink;
	}
	public void setPubLink(String pubLink) {
		this.pubLink = pubLink;
	}
	public String getNewsLink() {
		return newsLink;
	}
	public void setNewsLink(String newsLink) {
		this.newsLink = newsLink;
	}
	public String getConfmNo() {
		return confmNo;
	}
	public void setConfmNo(String confmNo) {
		this.confmNo = confmNo;
	}
	public String getInptYear() {
		return inptYear;
	}
	public void setInptYear(String inptYear) {
		this.inptYear = inptYear;
	}
	public String getAppAt() {
		return appAt;
	}
	public void setAppAt(String appAt) {
		this.appAt = appAt;
	}
	public String getUrlCn() {
		return urlCn;
	}
	public void setUrlCn(String urlCn) {
		this.urlCn = urlCn;
	}
	public String getLinkStatSno() {
		return linkStatSno;
	}
	public String getLinkStatComment() {
		return linkStatComment;
	}
	public String getExprStatNm() {
		return exprStatNm;
	}
	public void setExprStatNm(String exprStatNm) {
		this.exprStatNm = exprStatNm;
	}
	public boolean isExistMoreBtn() {
		return existMoreBtn;
	}
	public void setExistMoreBtn(boolean existMoreBtn) {
		this.existMoreBtn = existMoreBtn;
	}
	public boolean isExistInquire() {
		return existInquire;
	}
	public void setExistInquire(boolean existInquire) {
		this.existInquire = existInquire;
	}
	public boolean isExistInquireLink() {
		return existInquireLink;
	}
	public void setExistInquireLink(boolean existInquireLink) {
		this.existInquireLink = existInquireLink;
	}
	public String getExprInquire() {
		return exprInquire;
	}
	public void setExprInquire(String exprInquire) {
		this.exprInquire = exprInquire;
	}
	public boolean isExistStatNm() {
		return existStatNm;
	}
	public void setExistStatNm(boolean existStatNm) {
		this.existStatNm = existStatNm;
	}
	
}
