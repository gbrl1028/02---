package nurimsoft.stat.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nurimsoft.stat.info.MetaInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.info.StatLinkInfo;
import nurimsoft.stat.util.MessageManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MakeMetaManager {

protected Log log = LogFactory.getLog(this.getClass());

	private ParamInfo paramInfo;
	private StatHtmlDAO statHtmlDAO;
	private StringUtil sutil;
	private HttpServletRequest request;
	
	Map<String, String> paramMap = new HashMap();

	public MakeMetaManager(ParamInfo paramInfo, StatHtmlDAO statHtmlDAO, HttpServletRequest request){
		this.paramInfo = paramInfo;
		this.statHtmlDAO = statHtmlDAO;
		this.request = request;
		
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());
		paramMap.put("condTable", paramInfo.getCondTable());
	}

	public MetaInfo getMetaInfo(){

		MetaInfo metaInfo = new MetaInfo(paramInfo.getDataOpt());

		String orgId = paramInfo.getOrgId();
		String tblId = paramInfo.getTblId();

		Map map = null;

		//통계표ID
		metaInfo.setTblId(paramInfo.getTblId());
		//통계표명 및 자료다운일자, 수록기간
		map = statHtmlDAO.getTblNm(paramInfo);
		//System.out.println("###########"+map);
		metaInfo.setTblNm((String)map.get("tblNm"));
		metaInfo.setDownDate((String)map.get("downDate"));
		
		//2018.01.23 수록기간 주석(정보원 요청) - 김경호
		//metaInfo.setContainPeriod((String)map.get("containPeriod"));
		
		//2018.01.23 다운로드 파일의 메타자료에 항목이 1개일때 항목단원 표시 정보원의 요청으로 주석해제 - 김경호 
		if( (String)map.get("unit") != null){
			metaInfo.setUnit((String)map.get("unit"));
		}else{
			List itemList = statHtmlDAO.getDimensionItemList(paramMap, "item");
			
			if( itemList.size() == 1){
				map = (Map)itemList.get(0);
				metaInfo.setUnit(paramInfo.getDataOpt().indexOf("en") > -1 ? (String)map.get("UNIT_NM_ENG") : (String)map.get("UNIT_NM_KOR"));
			}
		}
		
		String defaultAction = PropertyManager.getInstance().getProperty("server.defaultaction");
		String parameters = "?orgId=" + orgId + "&tblId=" + tblId + (paramInfo.getLanguage().equals("en") ? "&language=en" : "")+"&conn_path=I3";
		String contextPath = paramInfo.getContextPath();
		String url = PropertyManager.getInstance().getProperty("server.url");
		metaInfo.setUrl(url + contextPath + defaultAction + parameters);

		StatLinkInfo statLinkInfo = new StatInfoManager().addStatLinkInfo(statHtmlDAO, paramInfo);

		if(statLinkInfo != null){
	//		metaInfo.setSource(statLinkInfo.getExprStatNm() + ( statLinkInfo.isExistInquire() ? " [" + statLinkInfo.getExprInquire() + "]" : "" ) );
			metaInfo.setSource(statLinkInfo.getExprStatNm());
			metaInfo.setIquire(statLinkInfo.getExprInquire()); //문의처
		}

		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		String[] prdArr = prd.split("@");

		//수록시점
		String prdSe = null;
		String prdSeNm = null;
		StringBuffer prdBuff = new StringBuffer();
		//Map paramMap = new HashMap();
		Map resultMap = new HashMap();
		paramMap.put("dataOpt", paramInfo.getDataOpt());
		for(int i = 0; i < prdArr.length; i++){
			String[] tmpArr = prdArr[i].split(",");
			prdSe = tmpArr[0];
			paramMap.put("orgId", orgId);
			paramMap.put("tblId", tblId);
			paramMap.put("prdSe", prdSe);

			//2018.01.23 다운로드 파일의 메타자료에 조회기간표시 주석되어 있었으나(이유모름) 정보원의 요청으로 주석해제 - 김경호 
			//부정기 체크
			if(prdSe.equals("F")){
				resultMap = statHtmlDAO.getPrdNmF(paramMap);
				prdSeNm = (String)resultMap.get("prdNm");
			}else{
				resultMap = statHtmlDAO.getPrdNm(paramMap);
				prdSeNm = (String)resultMap.get("prdNm");
			}

			prdBuff.append("[" + prdSeNm + "] " + tmpArr[tmpArr.length - 1] + "~" + tmpArr[1] + "  ");
		}

		metaInfo.setSearchPeriod(prdBuff.toString());
		//System.out.println(prdBuff.toString());

		//주석
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("dbUser", paramInfo.getDbUser());
		paramMap.put("sessionId", paramInfo.getSessionId());

		//2015.07.31 상속통계표 추가
		paramMap.put("inheritYn",   paramInfo.getInheritYn());
		paramMap.put("originOrgId", paramInfo.getOriginOrgId());
		paramMap.put("originTblId", paramInfo.getOriginTblId());
		
		//2014.04.29 호스팅 DB가 이관되면 EX3계정의 TN_STAT_HTML_COND_WEB를 못바라보기때문에 NSI_SYS계정으로 고정해서 사용하기위해 수정 - 김경호
		paramMap.put("condTable", paramInfo.getCondTable());

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			paramMap.put("lngSe", "1211911");
		}else{
			paramMap.put("lngSe", "1211910");
		}

		metaInfo.setCmmtList(statHtmlDAO.getCmmtList(paramMap));

		return metaInfo;
	}

	public File getMetaData(){

		File file = null;

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir = "tmpFile";
		String fileName = paramInfo.getOrgId() + "_" + paramInfo.getTblId();

		File dir = new File(realPath + File.separator + fileDir);
		if(! dir.exists()){
			dir.mkdir();
		}

		String dateString = StatPivotUtil.getDateString();

		fileName += "_" + dateString + ".txt";
		file = new File(realPath + File.separator + fileDir, fileName);

		StringBuffer strBuff = new StringBuffer();
		MetaInfo metaInfo = getMetaInfo();

		if(metaInfo == null){
			return null;
		}

		//strBuff.append("\r\n");
		strBuff.append("<" + metaInfo.getmetaDataTitle() + ">").append("\r\n");
		strBuff.append("\r\n");
		//통계표ID
		strBuff.append("○ " + metaInfo.getTblIdTitle() + " : ").append(metaInfo.getTblId()).append("\r\n");
		strBuff.append("\r\n");
		//통계표명
		strBuff.append("○ " + metaInfo.getTblNmTitle() + " : ");
		if(metaInfo.getTblNm() != null){
			strBuff.append(metaInfo.getTblNm()).append("\r\n");
		}
		strBuff.append("\r\n");
		//수록기간 - 2018.01.23 수록기간 주석(정보원 요청) - 김경호
		//strBuff.append("○ " + metaInfo.getContainPeriodTitle() + " : ").append(metaInfo.getContainPeriod()).append("\r\n");
		//조회기간
//		strBuff.append("○ " + metaInfo.getSearchPeriodTitle() + "     : ").append(metaInfo.getSearchPeriod()).append("\r\n");
		//strBuff.append("\r\n");
		//출처
		if(metaInfo.getSource() != null){
			strBuff.append("○ " + metaInfo.getSourceTitle() + " : ");
			strBuff.append("KOSIS("+metaInfo.getSource()+"), ").append(metaInfo.getDownDate()).append("\r\n");
			strBuff.append("\r\n");
		}
		//문의처
		if(metaInfo.getIquire() != null){
			strBuff.append("○ " + metaInfo.getIquireTitle() + " : ");
			strBuff.append(metaInfo.getIquire()).append("\r\n");
			strBuff.append("\r\n");
		}
//		strBuff.append("").append("\r\n");
		//자료다운일자
//		strBuff.append("○ " + metaInfo.getDownDateTitle() + " : ").append(metaInfo.getDownDate()).append("\r\n");
		//통계표URL
		strBuff.append("○ " + metaInfo.getUrlTitle() + " : ").append(metaInfo.getUrl()).append("\r\n");
		strBuff.append("              " + MessageManager.getInstance().getProperty("text.meta.url", paramInfo.getDataOpt())).append("\r\n");
		strBuff.append("\r\n");
		if(metaInfo.getUnit() != null){
			//단위
			strBuff.append("○ " + metaInfo.getUnitTitle() + " : ").append(metaInfo.getUnit()).append("\r\n");
			strBuff.append("\r\n");
		}

		List cmmtList = metaInfo.getCmmtList();
		int cmmtListSize = cmmtList.size();
		if(cmmtListSize > 0){
			//주석
			strBuff.append("○ " + metaInfo.getCmmtTitle());
			strBuff.append("\r\n");
			String spaceStr = "  ";

			Map map = null;
			String cmmtDc = null;
			int rnum = 0;
			String title = null;
			for(int i = 0; i < cmmtListSize; i++){

				map = (Map)cmmtList.get(i);

				cmmtDc = (String)map.get("cmmtDc");
				rnum = ((BigDecimal)map.get("rnum")).intValue();
				title = (String)map.get("title");
				title = title.replace(":", "").trim();

				if(rnum == 1){
					strBuff.append(" ■ " + title).append("\r\n");
				}

				strBuff.append(spaceStr).append(cmmtDc).append("\r\n");

			}

		}

		BufferedWriter bw = null;

		try{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(strBuff.toString());
		}catch(Exception e){

		}finally{
			try{
				bw.close();
			}catch(Exception e){}
		}

		return file;
	}

	public File getDirectMeta(){

		File file = null;

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		
		String fileDir = "tmpFile";
		String fileName = paramInfo.getOrgId() + "_" + paramInfo.getTblId();

		File dir = new File(realPath + File.separator + fileDir);
		if(! dir.exists()){
			dir.mkdir();
		}

		String dateString = StatPivotUtil.getDateString();

		fileName += "_meta" + "_" + dateString + ".csv";
		file = new File(realPath + File.separator + fileDir, fileName);

		StringBuffer strBuff = new StringBuffer();

		// 직접다운로드용 통계표정보
		Map statInfo = statHtmlDAO.getDirectStatInfo(paramInfo);
		// 직접다운로드용 주석정보
		List<Map> objitmInfo = statHtmlDAO.getObjItmInfo(paramInfo);
		// 직접다운로드용 주석정보
		List<Map> cmmtInfo = statHtmlDAO.getDirectCmmt(paramInfo);

		String tbl_id = paramInfo.getTblId();
		String tbl_nm = statInfo.get("TBL_NM").toString();
		String prd_info = statInfo.get("PRD_INFO").toString();
		String dept_nm = "";
		String dept_tel = "";
		String make_date = statInfo.get("MAKE_DATE").toString();

		if(statInfo.get("DEPT_NM") == null) {dept_nm = "";} else {dept_nm = statInfo.get("DEPT_NM").toString();}
		if(statInfo.get("DEPT_TEL") == null) {dept_tel = "";} else {dept_tel = statInfo.get("DEPT_TEL").toString();}


		strBuff.append("\"<통계표 메타자료>\"\n");
		strBuff.append("\"○ 통계표ID\",\"").append(tbl_id).append("\"\n");
		strBuff.append("\"○ 통계표명\",\"").append(tbl_nm).append("\"\n");
		strBuff.append("\"○ 수록기간\",\"").append(prd_info).append("\"\n");
		if ( dept_nm != null) {
			strBuff.append("\"○ 출처\",\"KOSIS(").append(dept_nm).append("), ").append(make_date).append("\"\n");
		}
		if (dept_tel != null) {
			strBuff.append("\"○ 문의처\",\"").append(dept_tel).append("\"\n");
		}
		strBuff.append("\"○ 주석 정보\"\n");

		Map cmmtmap	= new HashMap();
		String cmmtDc = "";
		String cmmtNm = "";

		// 분류값
		if(cmmtInfo.size() != 0){
			int rnum = 0;

			for(int i = 0; i<cmmtInfo.size(); i++){

				cmmtmap = cmmtInfo.get(i);

				cmmtDc = sutil.stringCheck((String)cmmtmap.get("CMMT_DC")); // 주석
				cmmtNm = sutil.stringCheck((String)cmmtmap.get("CMMT_NM")); // 주석 구분(통계표, 항목, 분류..)
				rnum = ((BigDecimal)cmmtmap.get("RNUM")).intValue();

				String cmtDcTrim = cmmtDc.trim(); // 주석의 불피요한 공백 제거
				if(rnum == 1){
					strBuff.append("\"").append(cmmtNm).append("\",\" ").append(cmtDcTrim).append("\"\n");
				}else{
					strBuff.append("\"\",\" ").append(cmtDcTrim).append("\"\n");
				}
			}
		}

		strBuff.append("\n");
		strBuff.append("\"○ 분류/항목 정보\",\n");
		strBuff.append("\"분류코드\",\"분류/항목명\",\"분류값\",\"분류값명\",\"단위\",\"가중치\"\n");
		String objitmcls = "", objnm = "", itmid = "", itmnm = "", unit = "", wgt = "";

		for(int i = 0; i<objitmInfo.size();i++){
			Map objmap = objitmInfo.get(i);
			if (objmap.get("OBJ_VAR_ID")  == null) {objitmcls="";} else {objitmcls=objmap.get("OBJ_VAR_ID").toString(); }
			if (objmap.get("OBJ_SCR_KOR") == null) {objnm="";    } else {objnm    =objmap.get("OBJ_SCR_KOR").toString();}
			if (objmap.get("ITM_ID")      == null) {itmid="";    } else {itmid    =objmap.get("ITM_ID").toString();     }
			if (objmap.get("ITM_SCR_KOR") == null) {itmnm="";    } else {itmnm    =objmap.get("ITM_SCR_KOR").toString();}
			if (objmap.get("UNIT_ID")     == null) {unit="";     } else {unit     =objmap.get("UNIT_ID").toString();    }
			if (objmap.get("WGT_CO")      == null) {wgt="";      } else {wgt      =objmap.get("WGT_CO").toString();     }
			strBuff.append("\"").append(objitmcls).append("\",")
				   .append("\"").append(objnm).append("\",")
				   .append("\"").append(itmid).append("\",")
				   .append("\"").append(itmnm).append("\",")
				   .append("\"").append(unit).append("\",")
				   .append("\"").append(wgt).append("\"")
				   .append("\n");
		}

		BufferedWriter bw = null;

		try{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(strBuff.toString());
		}catch(Exception e){

		}finally{
			try{
				bw.close();
			}catch(Exception e){}
		}

		return file;
	}

}
