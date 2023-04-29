package nurimsoft.stat.info;

public class CmmtInfo {
	
	private int cmmtNo;		//주석번호
	private String title;	//주석명칭
	private String content;	//주석내용
	private String cmmtSe;	//주석종류코드
	
	public int getCmmtNo() {
		return cmmtNo;
	}
	
	public void setCmmtNo(int cmmtNo) {
		this.cmmtNo = cmmtNo;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getCmmtSe() {
		return cmmtSe;
	}

	public void setCmmtSe(String cmmtSe) {
		this.cmmtSe = cmmtSe;
	}
	
}
