package nurimsoft.stat.manager;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nurimsoft.stat.info.CmmtInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.pivot.ClassDimension;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Dimension;
import nurimsoft.stat.pivot.Item;
import nurimsoft.stat.pivot.ItemDimension;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.pivot.TimeDimension;
import nurimsoft.stat.pivot.renderer.ChartRenderer;
import nurimsoft.stat.pivot.renderer.CsvTxtRenderer;
import nurimsoft.stat.pivot.renderer.ExcelRenderer;
import nurimsoft.stat.pivot.renderer.HtmlRenderer;
import nurimsoft.stat.pivot.renderer.Renderer;
import nurimsoft.stat.pivot.renderer.XlsRenderer;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author leekyujeong
 *
 */
public class StatDataInfoManager {

	protected Log log = LogFactory.getLog(this.getClass());

	private ParamInfo paramInfo;
	private StatHtmlDAO statHtmlDAO;
	private HttpServletRequest request;
	
	private StringBuffer strBuff = new StringBuffer();
	private StringBuffer strBuffMainSelect = new StringBuffer();

	boolean levelExpr;
	boolean doAnal;

	private ColumnAxis columnAxis = new ColumnAxis();
	private RowAxis rowAxis = new RowAxis();

	/** temp 정보 **/
	private ColumnAxis tColumnAxis = new ColumnAxis();
	private RowAxis tRowAxis  = new RowAxis();

	/** 챠트용 col row 정보**/
	private ColumnAxis cColumnAxis = new ColumnAxis();
	private RowAxis cRowAxis = new RowAxis();

	private List<Integer> classList = new ArrayList();
	private boolean existItm;

	Map<List<String>, Measure> resultMap = new HashMap<List<String>, Measure>();

	CmmtInfoManager cmmtInfoManager = new CmmtInfoManager();

	//분류 리스트(OV_L1~8_ID 형태로 저장)
	//query where절에 사용.(ov_l1_id in (...) and ov_l2_id in (...))
	private List<String> classListForWhereClause = new ArrayList();

	private String prdWhereClause;
	private String prdWhere;
	private List<String> prdListDummy;

	String dbUser;
	String orgId;
	String tblId;
	String condTable;
	String sessionId;

	//분석
	private String analType = "N";
	private String analCmpr = "N";
	private String analTime = "N";
	//화면에서 문자열로 보내는 변수
	private String analClass;
	private String analItem;

	//분류 정보를 위한 배열들..여러개 선언하여 동일한 index를 가지도록 한다.(map보다 컨트롤하기 쉽다.)
	private List<String> analClassList;
	private List<String> analItemList;
	private List<String> varOrdSnFuncList;
	private List<String> varOrdSnFuncListSorted; //Collections 클래스를 이용학 위함

	//항목 정보를 위한 변수 null로 초기화 하고 null이 아닌 경우 항목값이 넘어온것으로 체크
	private String analItemStr;

	private String prdSe;
	private String from;
	private String to;

	private String fromFunc;
	private String toFunc;

	//분석기능예외코드
	private String fnExcptCd;

	//분석시 사용할..변수(화면에서 선택한 분류의 순서)
	private String varOrdSnFunc;

	private String serverType;

	//2014.07.09 이규정 더미여부, 차원단위존재여부, 가중치존재여부 추가
	private String dmmDispKd;
	private String dimUnitYn;
	private String wgtYn;
	//2014.10 TBL_SE, MART_USABLE(NSI_MART.TN_MT_INFO.LST_CHN_TP)
	private String tblSe;
	private String martStatus;
	private long dimCo;
	private String mtId;
	private boolean martUsable = false;
	private char martJoinMethod = 'N'; //default NL join

	protected int columnDimCount;
	protected int rowDimCount;

	public StatDataInfoManager(ParamInfo paramInfo, StatHtmlDAO statHtmlDAO, HttpServletRequest request){
		this.paramInfo = paramInfo;
		this.statHtmlDAO = statHtmlDAO;
		this.request = request;

		dbUser = paramInfo.getDbUser();
		orgId = paramInfo.getOrgId();
		tblId = paramInfo.getTblId();
		//condTable = PropertyManager.getInstance().getProperty("table.condition");
		condTable = PropertyManager.getInstance().getProperty("server.dbuser")+"."+PropertyManager.getInstance().getProperty("table.condition"); //2014.04.29 호스팅 DB이관문제 때문에 TN_STAT_HTML_COND_WEB 관련 수정 - 김경호
		sessionId = paramInfo.getSessionId();
		serverType = paramInfo.getServerType();

		//TEST 분석

		//기여도 - 101, DT_1J08001
		/*
		paramInfo.setAnalType("CHG_RATE_CO");
		//paramInfo.setAnalCmpr("PREV_M"); //전월비
		//paramInfo.setAnalCmpr("PREV_Y_MTOTL"); //전년누계비
		//paramInfo.setAnalCmpr("PREV_Y_ME"); //전년말월비
		//paramInfo.setAnalCmpr("PREV_Y_MS"); //전년동월비
		paramInfo.setAnalCmpr("ONE_M"); //기준월 선택
		paramInfo.setAnalTime("M201305"); //기준월 201305
		paramInfo.setAnalClass("I,");
		paramInfo.setAnalItem("0,");
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*
		//paramInfo.setAnalCmpr("PREV_M"); //전월비
		//paramInfo.setAnalCmpr("PREV_Y_MTOTL"); //전년누계비
		//paramInfo.setAnalCmpr("PREV_Y_ME"); //전년말월비
		//paramInfo.setAnalCmpr("PREV_Y_MS"); //전년동월비
		paramInfo.setAnalCmpr("ONE_M"); //기준월 선택
		paramInfo.setAnalTime("M201309"); //기준월 201310
		*/

		//paramInfo.setAnalCmpr("PREV_Y"); //전년비
		//paramInfo.setAnalCmpr("ONE_Y"); //기준년 선택
		//paramInfo.setAnalTime("Y2011"); //기준년 2011

		//paramInfo.setAnalCmpr("PREV_Q"); //전분기비
		//paramInfo.setAnalCmpr("PREV_Y_QTOTL"); //전년누계비
		//paramInfo.setAnalCmpr("PREV_Y_QS"); //전년동분기비
		//paramInfo.setAnalCmpr("ONE_Q"); //기준분기 선택
		//paramInfo.setAnalTime("Q201302"); //기준분기 201202

		//paramInfo.setAnalClass("I");
		//paramInfo.setAnalItem("0");


		//누계구성비
		/*
		paramInfo.setAnalType("TOTL_CMP_RATE");
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		paramInfo.setAnalClass("A");
		paramInfo.setAnalItem("11");
		//paramInfo.setAnalClass("ITEM");
		//paramInfo.setAnalItem("T10");
		*/

		/*
		//구성비
		paramInfo.setAnalType("CMP_RATE");
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		//paramInfo.setAnalClass("A");
		//paramInfo.setAnalItem("11");
		paramInfo.setAnalClass("ITEM");
		paramInfo.setAnalItem("T1");
		*/

		/*
		//누계
		paramInfo.setAnalType("TOTL");
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*****************************************************************
		 * 년
		 *****************************************************************/

		/*
		//증감, 증감율
		paramInfo.setAnalType("CHG");	//증감
		//paramInfo.setAnalType("CHG_RATE");	//증감율
		paramInfo.setAnalCmpr("PREV_Y"); //전년비
		//paramInfo.setAnalCmpr("ONE_Y"); //기준년도 선택
		//paramInfo.setAnalTime("Y2010"); //기준년도 2010
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*****************************************************************
		 * 월
		 *****************************************************************/
		/*
		//증감
		paramInfo.setAnalType("CHG");
		//paramInfo.setAnalCmpr("PREV_M"); //전월비
		paramInfo.setAnalCmpr("ONE_M"); //기준월 선택
		paramInfo.setAnalTime("M201306"); //기준월 201306
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*
		//증감 : 전년누계비, 전년말월비, 전년동월비
		paramInfo.setAnalType("CHG");
		//paramInfo.setAnalCmpr("PREV_Y_MTOTL"); //전년누계비
		//paramInfo.setAnalCmpr("PREV_Y_ME"); //전년말월비
		paramInfo.setAnalCmpr("PREV_Y_MS"); //전년동월비
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*****************************************************************
		 * 분기
		 *****************************************************************/
		/*
		paramInfo.setAnalType("CHG");
		//paramInfo.setAnalCmpr("PREV_Q"); //전분기비
		paramInfo.setAnalCmpr("ONE_Q"); //기준분기 선택
		paramInfo.setAnalTime("Q201302"); //기준분기 201302
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*
		//증감 : 전년누계비, 전년말분기비, 전년동분기비
		paramInfo.setAnalType("CHG");
		//paramInfo.setAnalCmpr("PREV_Y_QTOTL"); //전년누계비
		//paramInfo.setAnalCmpr("PREV_Y_QE"); //전년말분기비
		paramInfo.setAnalCmpr("PREV_Y_QS"); //전년동분기비
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*****************************************************************
		 * 반기
		 *****************************************************************/
		//반기 : 101, DT_1EZ0002
		/*
		paramInfo.setAnalType("CHG");
		//paramInfo.setAnalCmpr("PREV_H"); //전반기비
		paramInfo.setAnalCmpr("ONE_Q"); //기준반기 선택
		paramInfo.setAnalTime("H201201"); //기준반기 201201
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*
		//증감 : 전년누계비, 전년말반기비, 전년동반기비
		paramInfo.setAnalType("CHG");
		//paramInfo.setAnalCmpr("PREV_Y_HTOTL"); //전년누계비
		//paramInfo.setAnalCmpr("PREV_Y_HE"); //전년말반기비
		paramInfo.setAnalCmpr("PREV_Y_HS"); //전년동반기비
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		//부정기 - 이건...좀 그래..
		//101, DT_1B41
		//paramInfo.setAnalType("CHG");
		//paramInfo.setAnalType("CHG_RATE");	//증감율
		//paramInfo.setAnalType("TOTL");	//누계
		//paramInfo.setAnalCmpr("PREV_F"); //전기간비
		//paramInfo.setAnalCmpr("ONE_F"); //기준년도 선택
		//paramInfo.setAnalTime("F2005"); //기준부정기 2005

		/*
		//구성비
		//paramInfo.setAnalType("CMP_RATE"); //구성비
		paramInfo.setAnalType("TOTL_CMP_RATE"); //누계구성비
		//paramInfo.setAnalClass("B44J");
		//paramInfo.setAnalItem("11");
		paramInfo.setAnalClass("B44B");
		paramInfo.setAnalItem("03");
		//paramInfo.setAnalClass("ITEM");
		//paramInfo.setAnalItem("B44T16");
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		/*
		//구성비 101, DT_1F01003
		//paramInfo.setAnalType("CMP_RATE"); //구성비
		paramInfo.setAnalType("TOTL_CMP_RATE"); //누계구성비
		paramInfo.setAnalClass("A,C,");
		paramInfo.setAnalItem("00,0,");

		//paramInfo.setAnalClass("ITEM,");
		//paramInfo.setAnalItem("T10,");
		//paramInfo.setAnalClass("B44B");
		//paramInfo.setAnalItem("03");
		//paramInfo.setAnalClass("ITEM");
		//paramInfo.setAnalItem("B44T16");
		paramInfo.setDoAnal("Y");
		paramInfo.setOriginData("Y");
		*/

		String tmpAnalType = paramInfo.getAnalType();
		if(tmpAnalType != null && tmpAnalType.trim().length() > 0){
			analType = tmpAnalType;
		}

		String tmpAnalCmpr = paramInfo.getAnalCmpr();
		if(tmpAnalCmpr != null && tmpAnalCmpr.trim().length() > 0){
			analCmpr = tmpAnalCmpr;
		}

		String tmpAnalTime = paramInfo.getAnalTime();
		if(tmpAnalTime != null && tmpAnalTime.trim().length() > 0){
			analTime = tmpAnalTime;
		}

		String tmpAnalClass = paramInfo.getAnalClass();
		if(tmpAnalClass != null && tmpAnalClass.trim().length() > 0){
			analClass = tmpAnalClass;
		}

		String tmpAnalItem = paramInfo.getAnalItem();
		if(tmpAnalItem != null && tmpAnalItem.trim().length() > 0){
			analItem = tmpAnalItem;

			setAnalClassList(analClass.split(","), analItem.split(","));
		}

		//구성비, 누계구성비인 경우 analCmpr을 "N"로 처리한다. 화면에서도 처리해야 하겠지만, 서버에서도 한번 더 처리해 준다.
		if( analType != null && (analType.equals("CMP_RATE") || analType.equals("TOTL_CMP_RATE")) ){
			analCmpr = "N";
		}

		//check parameters
		/*
		System.out.println("------------------------------------------------------------------------------------------");
		System.out.println("analType 	::: " + analType);
		System.out.println("analCmpr 	::: " + analCmpr);
		System.out.println("analTime 	::: " + analTime);
		System.out.println("analClass 	::: " + analClass);
		System.out.println("analItem 	::: " + analItem);
		System.out.println("------------------------------------------------------------------------------------------");
		*/

		//2014.07.09 이규정 더미여부,차원단위존재여부, 가중치존재여부 가져오기
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("orgId", paramInfo.getOrgId());
		paramMap.put("tblId", paramInfo.getTblId());
		paramMap.put("dbUser", paramInfo.getDbUser());
		boolean isService = (paramInfo.getServerTypeOrigin().indexOf("service") >= 0) ? true : false;
		paramMap.put("isService", (isService) ? "Y" : "N");

		Map extraInfoMap = (Map)statHtmlDAO.selectStblInfoExtra(paramMap);
		dmmDispKd = (String)extraInfoMap.get("DMM_DISP_KD");
		dimUnitYn = (String)extraInfoMap.get("DIM_UNIT_YN");
		wgtYn = (String)extraInfoMap.get("WGT_YN");
		//2014.10
		if(isService){
			tblSe = StringUtils.defaultString( (String)extraInfoMap.get("TBL_SE") );
			martStatus = StringUtils.defaultString( (String)extraInfoMap.get("MART_STATUS") );
			dimCo = ((BigDecimal)extraInfoMap.get("DIM_CO")).longValue();
			mtId = (String)extraInfoMap.get("MT_ID");
		}

		/*2014.10.02 셀단위 관리하는 통계표이면 조회할때는 셀단위를 표시할것(일단 처음열렸을때 표시 그이후는 파라미터가 알아서함) - 김경호*/
		if( dimUnitYn.equals("Y") && paramInfo.getFirst_open().equals("")){
			paramInfo.setEnableCellUnit("Y");
		}

		//마트 사용가능 여부 판단 및 조인방법 결졍
		//System.out.println("############################################ itemMultiply ::: " + paramInfo.getItemMultiply());
		//System.out.println("############################# tblSe ::: " + tblSe + ", martStatus ::: " + martStatus);
		if(isService && StringUtils.defaultString(tblSe).equals("M") && !StringUtils.defaultString(martStatus).equals("P")){
			//실제 마트테이블 존재하는지 체크
			try{
				String chkSql = "SELECT 1 FROM NSI_MART." + mtId + " WHERE ROWNUM = 1";
				statHtmlDAO.chkMartExist(chkSql);

				martUsable = true;
				//System.out.println("############################# martUsable ::: " + martUsable);

				//마트 존재 시 join 방법 체크
				//DIM_CO가 0인경우 무조건 NL 수행
				if( dimCo != 0 && dimCo <= Long.parseLong(PropertyManager.getInstance().getProperty("mart.join.method")) ){
					long itemMultiply = Long.parseLong(paramInfo.getItemMultiply());
					//DIM_CO에 대한 조회조건 항목X분류 조합수가 1%를 초과하면 HASH로 전환
					if( itemMultiply * 100 / dimCo > 1){
						martJoinMethod = 'H';
					}
				}

			}catch(Exception e){
				//log.error(e.getMessage());
				log.info(e.getMessage()); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
			}
		}


	}

	private void setAnalClassList(String[] cls, String[] itm){

		if(cls.length != itm.length){
			return;
		}

		analClassList = new ArrayList<String>();
		analItemList = new ArrayList<String>();
		varOrdSnFuncList = new ArrayList<String>();
		varOrdSnFuncListSorted = new ArrayList<String>();

		Map<String, String> pMap = new HashMap();
		pMap.put("dbUser", dbUser);
		pMap.put("orgId", orgId);
		pMap.put("tblId", tblId);

		for(int i = 0; i < cls.length; i++){
			if(!"ITEM".equals(cls[i])){
				analClassList.add(cls[i]);
				analItemList.add(itm[i]);
				pMap.put("objVarId", cls[i]);
				String sn = statHtmlDAO.selectVarOrdSn(pMap);
				varOrdSnFuncList.add(sn);
				varOrdSnFuncListSorted.add(sn);
			}else{
				analItemStr = itm[i];
			}
		}

		//binarySearch를 활용하기 위함.
		if(varOrdSnFuncListSorted.size() > 0){
			Collections.sort(varOrdSnFuncListSorted);
		}
	}

	public void makeData() throws Exception{

		String view = paramInfo.getView();

		if(view == null || view.trim().length() == 0){
			view = "table";
		}

		//통계표 주석여부 체크 및 주석정보에 통계표 주석내용 insert
		setStblCmmt();

		//parameter 정보를 읽어서 dimension 정보를 생성한다.
		setAxis();

		//pivot정보에 분류가 2개일 지라도 공표구분이 비공개인것(포함 3개이상)에 대해서도 where 절에서는 사용해야 한다.
		setClassListForWhereClause();

		//sql 실행
		executeQuery();
		levelExpr = ( paramInfo.getEnableLevelExpr() != null && paramInfo.getEnableLevelExpr().equals("Y") ) ? true : false;

	}

	public Object[] getHtmlData() throws Exception{

		Object[] obj = new Object[3];
		List result = null;

		try{

			makeData();

			Renderer renderer = new HtmlRenderer(paramInfo, resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager); //levelExpr : 계층별 컬럼보기 여부
			renderer.write();

			cColumnAxis = columnAxis;
			cRowAxis = rowAxis;

			/**챠트 범례요청사항 기존 범례 지우고 table형태의 범례표기로 변경 별도의 챠트렌더러 추가
			 * 표측:항목,분류순
			 * 표두:시점 고정(년월분기표인 경우 년,월 2개시점 세팅
			 **/
			Dimension dim = null;
			Dimension itemDim = null;
			Dimension timeDim = null;
			Dimension yearDim = null;
			Dimension mqDim = null;
			//System.out.println("****************챠트*********************************");
			//System.out.println("cColumnAxis 카운트::"+cColumnAxis.getDimemsionCount());
			//System.out.println("cRowAxis 카운트::"+cRowAxis.getDimemsionCount());

			rowDimCount = cRowAxis.getDimemsionCount();
			columnDimCount = cColumnAxis.getDimemsionCount();
			String perYear = paramInfo.getTableType();
			String timeType = "";
			//표측 검사
			for(int i=0; i<rowDimCount;i++){
				dim = cRowAxis.getDimension(i);
				if(dim instanceof TimeDimension){
					timeType  = ((TimeDimension) dim).getTime()== null ? "TIME" : ((TimeDimension) dim).getTime();

					if(timeType.equals("TIME_YEAR")){
						yearDim = dim;
					}else if(timeType.equals("TIME_MQ")){
						mqDim = dim;
					}else{
						timeDim = dim;
					}
					//System.out.println("표측타임1"+((TimeDimension) dim).getTime());
					//System.out.println("표측타임2"+timeType);

				}else if(dim instanceof ItemDimension){
					itemDim = dim;
				}else if(dim instanceof ClassDimension){
					tRowAxis.addDimension(dim);
				}

			}

			for(int i=0; i<columnDimCount;i++){
				dim = cColumnAxis.getDimension(i);
				if(dim instanceof TimeDimension){
					timeType  = ((TimeDimension) dim).getTime()== null ? "TIME" : ((TimeDimension) dim).getTime();

					if(timeType.equals("TIME_YEAR")){
						yearDim = dim;
					}else if(timeType.equals("TIME_MQ")){
						mqDim = dim;
					}else{
						timeDim = dim;
					}
					//System.out.println("표두타임1"+((TimeDimension) dim).getTime());
					//System.out.println("표두타임2"+timeType);

				}else if(dim instanceof ItemDimension){
					itemDim = dim;
				}else if(dim instanceof ClassDimension){
					tRowAxis.addDimension(dim);
				}

			}


			cRowAxis = tRowAxis;
			if(itemDim != null){
				cRowAxis.addDimension(itemDim,0);									//item 항목은 맨처음에 추가한다.
			}

			if(paramInfo.getTableType().equals("perYear")){
				tColumnAxis.addDimension(yearDim);
				tColumnAxis.addDimension(mqDim);
			}else{
				tColumnAxis.addDimension(timeDim);
			}
			cColumnAxis = tColumnAxis;



			/** cRowAxis 항목지우고 마지막에
			int tmpSize = cRowAxis.getDimemsionCount();
			for(int i=0; i<tmpSize;i++){
				dim = cRowAxis.getDimension(i);
			//	System.out.print("DIM+++++++++++"+dim);
				if(dim instanceof ItemDimension){
					cRowAxis.removeDimension(dim,i);
					cRowAxis.addDimension(dim);
				}
			}
			**/


			//차트는 시계열 오름차순
			if(paramInfo.getPrdSort().equals("desc")){
				TimeDimension tDim = (TimeDimension)cColumnAxis.getDimension(0);	//TimeDimension 하나만 존재
				List<Item> ascList = new ArrayList<Item>();
				/*for(int i = tDim.getItemCount() - 1; i >= 0; i-- ){
					ascList.add(tDim.getItem(i));
				}*/
				
				/*2017.11.14 낮은 주기부터 먼저 조회되도록 작업하면서 틀어진 차트 시계열 수정
				 *차트의 시계열 오름차순을 유지하면서 주기의 우선순위만 변경함.*/
				/*--------------------------------------------------*/
				List<Item> ascList_buff = new ArrayList<Item>();
				String jugi	= "";	// 주기가 뭔지 저장...
				String buff = "";
				
				for(int i = 0; i < tDim.getItemCount(); i++ ){
					
					Item item = tDim.getItem(i);
	
					buff = item.getCode();
					
					if( buff != null && buff.length() > 0){
						buff = buff.substring(0,1);
					}
					
					if( !jugi.equals(buff)){
						jugi = buff;
						
						if( ascList_buff.size() > 0){
							for(int j = ascList_buff.size() - 1; j >=0  ; j-- ){
								ascList.add(ascList_buff.get(j));
								
							}
							ascList_buff.clear();
						}
					}
					
					ascList_buff.add(item);
				}
				
				//주기가 1개 일때 또는 주기가 여러개 일 경우 마지막 주기의 시계열이 담겨있는 ascList_buff의 사이즈가 0이 아닐때 
				if( ascList.size() == 0 || ascList_buff.size() > 0){
					for(int j = ascList_buff.size() - 1; j >=0  ; j-- ){
						ascList.add(ascList_buff.get(j));
					}
				}
				/*--------------------------------------------------*/
				
				tDim.setItemList(ascList);

			}
			Renderer chRenderer = new ChartRenderer(paramInfo,resultMap,cRowAxis,cColumnAxis,levelExpr,cmmtInfoManager);
			chRenderer.write();

			result = new ArrayList();
			result.add(((HtmlRenderer)renderer).htmlText.toString());
			result.add(((HtmlRenderer)renderer).cmmtText.toString());
//			result.add(((HtmlRenderer)renderer).chartData);
			result.add(((ChartRenderer)chRenderer).chartData);
			result.add(((HtmlRenderer)renderer).ThtmlText.toString()); //2016.02.16 List의 4번째로 추가 - 김경호

			obj[0] = result;
			obj[1] = (cmmtInfoManager.cmmtList.size() > 0) ? "Y" : "N";
			//Ays 추가 Debug용 row_cnt
			String rowCnt = Integer.toString(resultMap.size());
			obj[2] = rowCnt;
			//System.out.println("1111111111111111111111"+((HtmlRenderer)renderer).htmlText.toString());
			//System.out.println("1111111111111111111111"+((HtmlRenderer)renderer).cmmtText.toString());
		}catch(Error e){
			e.printStackTrace();
			log.info(e.getMessage());
		}

		return obj;
	}

	public File getExcelData(String fileType) throws Exception{

		File file = null;

		try{

			makeData();
			
			Renderer renderer = null;
			//System.out.println(((CsvTxtRenderer)renderer).fileText.toString());	
			
			if( fileType.equals("xls")){	// 2016-07-06 xls 다운로드 추가 - 우찬균 주무관 요청
				renderer = new XlsRenderer(paramInfo, resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager,statHtmlDAO, request); //levelExpr : 계층별 컬럼보기 여부
				renderer.write();
				file = ((XlsRenderer)renderer).file;
			}else{
				renderer = new ExcelRenderer(paramInfo, resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager,statHtmlDAO, request); //levelExpr : 계층별 컬럼보기 여부
				renderer.write();
				file = ((ExcelRenderer)renderer).file;
			}
			
		}catch(Error e){
			e.printStackTrace();
			log.info(e.getMessage());
		}

		return file;
	}

	public File getCsvTxtData() throws Exception{

		File file = null;

		try{

			makeData();

			Map tmMap = new HashMap();
			tmMap.put("orgId", paramInfo.getOrgId());
			tmMap.put("tblId", paramInfo.getTblId());
			tmMap.put("dbUser", paramInfo.getDbUser());

			int openCnt = statHtmlDAO.openListCnt(tmMap);

			Renderer renderer = new CsvTxtRenderer(paramInfo, resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager, openCnt, request); //levelExpr : 계층별 컬럼보기 여부
			renderer.write();

			file = ((CsvTxtRenderer)renderer).file;
		}catch(Error e){
			e.printStackTrace();
			log.info(e.getMessage());
		}

		return file;
	}

	//parameter 정보를 읽어서 dimension 정보를 생성한다.
	private void setAxis() throws Exception{

		String[] colAxisArr = null;
		String[] rowAxisArr = null;

		if(paramInfo.getColAxis() != null && paramInfo.getColAxis().trim().length() != 0){
			colAxisArr = paramInfo.getColAxis().split(",");
		}

		if(paramInfo.getRowAxis() != null && paramInfo.getRowAxis().trim().length() != 0){
			rowAxisArr = paramInfo.getRowAxis().split(",");
		}

		Dimension dimension = null;

		if(colAxisArr != null){
			for(int i = 0; i < colAxisArr.length; i++){
				if(!colAxisArr[i].startsWith("TIME") && !colAxisArr[i].startsWith("ITEM")){
					//분류
					dimension = new ClassDimension(paramInfo, colAxisArr[i], statHtmlDAO, cmmtInfoManager);
					classList.add( ((ClassDimension)dimension).getVarOrdSn() );
				}else if(colAxisArr[i].startsWith("ITEM")){
					//항목
					dimension = new ItemDimension(paramInfo, "13999001", statHtmlDAO, cmmtInfoManager);
					existItm = true;
				}else if(colAxisArr[i].startsWith("TIME")){
					//시점
					if(paramInfo.getTableType().equals("perYear")){
						dimension = new TimeDimension(paramInfo, colAxisArr[i], statHtmlDAO);
						if(colAxisArr[i].equals("TIME_YEAR")){
							prdWhereClause = ((TimeDimension)dimension).prdWhereClause.toString();
							prdWhere = ((TimeDimension)dimension).prdWhere.toString();
							prdListDummy = ((TimeDimension)dimension).prdListDummy;

/*							System.out.println(":::::::::::::::::::::::::::::::perYear");
							System.out.println(":::::::::::::::::::::::::::::::prdWhereClause"+prdWhereClause);
							System.out.println(":::::::::::::::::::::::::::::::prdListDummy"+prdListDummy);
							System.out.println(":::::::::::::::::::::::::::::::prdWhere"+prdWhere);*/
						}

					}else{
							dimension = new TimeDimension(paramInfo, statHtmlDAO);
							prdWhereClause = ((TimeDimension)dimension).prdWhereClause.toString();
							prdWhere = ((TimeDimension)dimension).prdWhere.toString();
							prdListDummy = ((TimeDimension)dimension).prdListDummy;

/*							System.out.println(":::::::::::::::::::::::::::::::perYear else");
							System.out.println(":::::::::::::::::::::::::::::::prdWhereClause"+prdWhereClause);
							System.out.println(":::::::::::::::::::::::::::::::prdListDummy"+prdListDummy);
							System.out.println(":::::::::::::::::::::::::::::::prdWhere"+prdWhere);*/
						}

					prdSe = ((TimeDimension)dimension).prdSe;
					from = ((TimeDimension)dimension).from;
					to = ((TimeDimension)dimension).to;
				}

				columnAxis.addDimension(dimension);
			//	cColumnAxis.addDimension(dimension);
			}
		}

		if(rowAxisArr != null){
			for(int i = 0; i < rowAxisArr.length; i++){
				if(!rowAxisArr[i].startsWith("TIME") && !rowAxisArr[i].startsWith("ITEM")){
					//분류
					dimension = new ClassDimension(paramInfo, rowAxisArr[i], statHtmlDAO, cmmtInfoManager);
					classList.add( ((ClassDimension)dimension).getVarOrdSn() );
				}else if(rowAxisArr[i].startsWith("ITEM")){
					//항목
					dimension = new ItemDimension(paramInfo, "13999001", statHtmlDAO, cmmtInfoManager);
					existItm = true;
				}else if(rowAxisArr[i].startsWith("TIME")){
					//시점
					if(paramInfo.getTableType().equals("perYear")){
						dimension = new TimeDimension(paramInfo, rowAxisArr[i], statHtmlDAO);
						if(rowAxisArr[i].equals("TIME_YEAR")){
							prdWhereClause = ((TimeDimension)dimension).prdWhereClause.toString();
							prdWhere = ((TimeDimension)dimension).prdWhere.toString();
							prdListDummy = ((TimeDimension)dimension).prdListDummy;
						}
					}else{
						dimension = new TimeDimension(paramInfo, statHtmlDAO);
						prdWhereClause = ((TimeDimension)dimension).prdWhereClause.toString();
						prdWhere = ((TimeDimension)dimension).prdWhere.toString();
						prdListDummy = ((TimeDimension)dimension).prdListDummy;
					}

					prdSe = ((TimeDimension)dimension).prdSe;
					from = ((TimeDimension)dimension).from;
					to = ((TimeDimension)dimension).to;

				}

				rowAxis.addDimension(dimension);
		//		cRowAxis.addDimension(dimension);
			}
		}

		//System.out.println("@@@@@@@@@@@@@@ : " + prdSe + "," + from + "," + to);

		Collections.sort(classList);

		//주석 번호 순서 때문에... 또 한번 돌려야 한다.(이것도 규칙이라고)
		/*
		 * 주석번호 순서 적용 시작
		 */
		List<Dimension> dimList = new ArrayList();

		dimList.addAll(columnAxis.getDimensionList());
		dimList.addAll(rowAxis.getDimensionList());

		Dimension dim = null;

		//항목 먼저
		//항목이 2개 이상인 경우(TN_STBL_INFO.CHAR_ITM_CO 값이 1보다 큰 경우 - ItemDimension이 있는 경우)
		if(existItm){
			for(int i = 0; i< dimList.size(); i++){
				dim = dimList.get(i);
				if(dim instanceof ItemDimension){
					((ItemDimension)dim).setInfo();
					break;
				}
			}
		}
		//TN_STBL_INFO.CHAR_ITM_CO 값이 1인 경우 - Dimension에는 포함되지 않음.
		//주석이 있는 경우 주석정보에 출력될 주석을 넣기 위함.
		else{
			ItemDimension itemDimension = new ItemDimension(paramInfo, "13999001", statHtmlDAO, cmmtInfoManager);
			itemDimension.setInfo();
		}

		//다음 분류 나와
		for(int h = 0; h < classList.size(); h++){
			int idx = classList.get(h);
			int varOrdSn = 0;
			for(int i = 0; i < dimList.size(); i++){
				dim = dimList.get(i);
				if(dim instanceof ClassDimension){
					varOrdSn = ((ClassDimension)dim).getVarOrdSn();
					if(varOrdSn == idx){
						((ClassDimension)dim).setInfo();
					}
				}
			}
		}

		/*
		 * 주석번호 순서 적용 끝
		 */

	}

	public void setStblCmmt(){

		List<String> list = new ArrayList<String>();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("orgId", orgId);
		paramMap.put("tblId", tblId);
		paramMap.put("dbUser", dbUser);
		paramMap.put("cmmtSe", "1210610");
		
		//2015.07.31 상속통계표 추가 
		paramMap.put("inheritYn", paramInfo.getInheritYn());
		paramMap.put("originOrgId", paramInfo.getOriginOrgId());
		paramMap.put("originTblId", paramInfo.getOriginTblId());

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			//영문
			if(paramInfo.getExistStblCmmtEng().equals("Y")){
				paramMap.put("lngSe", "1211911");
				list = statHtmlDAO.getStatCmmt(paramMap);

				setStblCmmtInfo(list);
			}

		}else{
			//한글 및 코드보기
			if(paramInfo.getExistStblCmmtKor().equals("Y")){
				paramMap.put("lngSe", "1211910");
				list = statHtmlDAO.getStatCmmt(paramMap);

				setStblCmmtInfo(list);
			}
		}

	}

	private void setStblCmmtInfo(List list){
		CmmtInfo cmmtInfo = new CmmtInfo();
		cmmtInfo.setCmmtNo(cmmtInfoManager.cmmtNo);
		cmmtInfo.setCmmtSe("1210610");
		cmmtInfo.setContent(StatPivotUtil.makeStrCmmt(list));

		cmmtInfoManager.cmmtList.add(cmmtInfo);
		cmmtInfoManager.cmmtNo++;
	}

	private void setDimCmmtInfo(List list, Measure measure){
		CmmtInfo cmmtInfo = new CmmtInfo();
		cmmtInfo.setCmmtNo(cmmtInfoManager.cmmtNo);
		cmmtInfo.setCmmtSe("1210614");

		measure.setCmmtNo(cmmtInfoManager.cmmtNo);
		measure.setCmmtSe("1210614");

		cmmtInfo.setContent(StatPivotUtil.makeStrCmmt(list));

		cmmtInfoManager.cmmtList.add(cmmtInfo);
		cmmtInfoManager.itmRcgnSnMap.put(measure.getItmRcgnSn(), cmmtInfoManager.cmmtNo);

		cmmtInfoManager.cmmtNo++;
	}

	//동일한 차원에 대해 미리 주석을 생성하였는지 여부
	private boolean checkExistCmmt(String itmRcgnSn){
		if( cmmtInfoManager.itmRcgnSnMap.get(itmRcgnSn) != null ){
			return true;
		}else{
			return false;
		}
	}

	private void setSameCmmtInfo(Measure measure){
		measure.setCmmtNo(cmmtInfoManager.itmRcgnSnMap.get(measure.getItmRcgnSn()));
		measure.setCmmtSe("1210614");
	}

	private void executeQuery() throws Exception{
		//query 생성
		String sql = generateQuery();
		log.info("SQL!!!! ::: " + sql);

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("sql", generateQuery());
		
		/*
		log.info("UsingMemory before : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		long time01 = System.currentTimeMillis();
		*/
		List<Map> list = statHtmlDAO.getDimDtData(paramMap);

		if(list == null || list.size() == 0){
			throw new StatExceptionManager("102");
		}
		/*
		log.info("UsingMemory after : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
		long time02 = System.currentTimeMillis();
		log.info("  spent time is " + (time02 - time01) + "ms.");
		*/

		String[] strArr = null;	//차원정보를 담기위한 문자배열
		//분류수 + 항목 + 시점 * 년/월(분기)표는 시점 2개
		int size = classList.size();

		//항목은 한개인 경우 dimension 정보에  나타나지 않는다.
		if(existItm){
			size += 1;
		}

		size += 1;	//시점

		//년/월(분기)표의 경우 1을 한번 더 더해준다.
		if(paramInfo.getTableType().equals("perYear")){
			size += 1;
		}

		Measure measure = null;

		List<String> cmmtList = new ArrayList<String>();//주석내용을 담기 위한 list
		paramMap.clear(); //주석 정보를 가져오기 위한 SQL 파라미터 변수로 재활용
		paramMap.put("orgId", orgId);
		paramMap.put("tblId", tblId);
		paramMap.put("dbUser", dbUser);
		paramMap.put("cmmtSe", "1210614"); //주석
		
		//2015.06.08 상속통계표 주석 변수 추가
		paramMap.put("inheritYn", paramInfo.getInheritYn());
		paramMap.put("originOrgId", paramInfo.getOriginOrgId());
		paramMap.put("originTblId", paramInfo.getOriginTblId());

		for(Map<String, Object> map : list){

			strArr = new String[size];

			int i = 0;
			for(int idx : classList){
				strArr[i] = idx + ":" + (String)map.get("OV_L" + idx + "_ID");
				i++;
			}

			int arrPos = classList.size();

			if(existItm){
				strArr[arrPos++] = "9:" + (String)map.get("CHAR_ITM_ID");
			}

			if(paramInfo.getTableType().equals("perYear")){
				strArr[arrPos++] = "$" + (String)map.get("TIME_YEAR");
				strArr[arrPos++] = "%" + (String)map.get("TIME_MQ");
			}else{
				strArr[arrPos++] = "#" + (String)map.get("TIME");
			}

			measure = new Measure();
			
			String prd_se_buff		= "";
			String dtval_co_buff	= "";
			String smbl_cn_buff		= ""; 
			String dmmt_at			= ""; //2020-05-19 추가
			
			prd_se_buff		=  StringUtils.defaultString((String)map.get("PRD_SE"));
			dtval_co_buff	=  StringUtils.defaultString((String)map.get("DTVAL_CO"));
			smbl_cn_buff	=  StringUtils.defaultString((String)map.get("SMBL_CN"));
			dmmt_at			=  StringUtils.defaultString((String)map.get("DMM_AT"));	//2020-05-19 추가
			
			
			/* 2016-01-21 김경호
			 * Renderer.java 에서는 tn_dt 테이블에 ROW가 없는데 더미처리되어 넘어온 데이터중 DTVAL_CO, DTVAL_CO, SMBL_CN 값이 모두 null일경우 공백처리함
			 * tn_dt 테이블에 ROW가 없고 더미도 아니면 [-] 처리되고 있음.
			 * tn_dt 테이블에 ROW가 있는데 DTVAL_CO, DTVAL_CN, SMBL_CN 이 모두 NULL 일 경우는 [-] 처리해달라고 함 - 이원영 
			 * tn_dt 테이블의 ROW 유무는 prd_se의 유무로 결정한다(기본키)
			 */
			// 수치값이 없고 통계표부호도 없는데 주기는 있다...(그럼 TN_DT테이블에 ROW가 있구만~)
			/* 2020-05-19
			 * 위 조건에 추가 - tn_dt 테이블에 ROW가 있는데 수치가 모두 NULL이어서 [-]이 되어야 하지만 더미가 Y 일경우 빈값으로 보여주도록 수정 - 손상호
			 */
			if( dmmt_at.equals("N") && dtval_co_buff.equals("") && smbl_cn_buff.equals("") && !prd_se_buff.equals("") ){
				measure.setDtvalCo("-");
			}else{ // ROW없으면 그냥 그대로 보내~
				measure.setDtvalCo((String)map.get("DTVAL_CO"));
			}
			
			measure.setSmblCn((String)map.get("SMBL_CN"));
			measure.setWgtCo((String)map.get("WGT_CO"));
			measure.setPeriodCo((String)map.get("PERIOD_CO"));
			measure.setUnitId((String)map.get("UNIT_ID"));
			measure.setUnitNmKor((String)map.get("UNIT_NM_KOR"));
			measure.setUnitNmEng((String)map.get("UNIT_NM_ENG"));
			measure.setExistCmmtKor(((String)map.get("CMMT_AT_KOR")).equals("Y") ? true : false);
			measure.setExistCmmtEng(((String)map.get("CMMT_AT_ENG")).equals("Y") ? true : false);
			measure.setItmRcgnSn((String)map.get("ITM_RCGN_SN"));
			if(map.get("ANAL_CO") != null){
				measure.setAnalCo( ((BigDecimal)map.get("ANAL_CO")).toString() );
			}

			//차원 주석담기
			if(paramInfo.getDataOpt().indexOf("en") > -1){
				//영문
				if(measure.isExistCmmtEng()){
					//동일차원의 주석이 존재하면 번호는 기존걸 가져다 셋팅
					if( checkExistCmmt(measure.getItmRcgnSn()) ){
						setSameCmmtInfo(measure);
					}else{
						paramMap.put("itmRcgnSn", measure.getItmRcgnSn());
						paramMap.put("lngSe", "1211911");
						list = statHtmlDAO.getDimCmmt(paramMap);

						setDimCmmtInfo(list, measure);
					}
				}

			}else{
				//한글 및 코드보기
				if(measure.isExistCmmtKor()){
					//동일차원의 주석이 존재하면 번호는 기존걸 가져다 셋팅
					if( checkExistCmmt(measure.getItmRcgnSn()) ){
						setSameCmmtInfo(measure);
					}else{
						paramMap.put("itmRcgnSn", measure.getItmRcgnSn());
						paramMap.put("lngSe", "1211910");
						list = statHtmlDAO.getDimCmmt(paramMap);

						setDimCmmtInfo(list, measure);
					}
				}
			}

			//출력 시 동일한 배열정보로부터 값을 추출해내기 위해 sort 한다.
			Arrays.sort(strArr);

			/*
			System.out.println("################ ::: strArr.length ::: " + strArr.length);
			for(int j = 0; j < strArr.length; j++){
				System.out.print(strArr[j] + ",");
			}
			System.out.println(measure.getDtvalCo());
			*/

			resultMap.put(Arrays.asList(strArr), measure);
		}

		list = null;
	}

	private String generateQuery(){

		//분석여부 체크
		String doAnalStr = paramInfo.getDoAnal();
		if(doAnalStr != null && doAnalStr.equals("Y")){
			doAnal = true;

			//ParamInfo의 analTypeNm, analCmprNm에 추가
			Map analTypeMap = statHtmlDAO.selectAnalTypeNm(paramInfo);
			paramInfo.setAnalTypeNm((String)analTypeMap.get("FUNC_NAME"));
			paramInfo.setAnalTypeNmEng((String)analTypeMap.get("FUNC_EN_NAME"));

			//누계, 구성비, 누계구성비가 아닌경우에만 적용
			if(!analType.equals("TOTL") && !analType.equals("CMP_RATE") && !analType.equals("TOTL_CMP_RATE")){
				Map analCmprMap = statHtmlDAO.selectAnalCmprNm(paramInfo);
				paramInfo.setAnalCmprNm((String)analCmprMap.get("FUNC_PRD_NAME"));
				paramInfo.setAnalCmprNmEng((String)analCmprMap.get("FUNC_PRD_EN_NAME"));

				//기여도, 기여율인 경우
				if(analType.equals("CHG_RATE_CO") || analType.equals("CHG_RATE_CO_R")){
					//분류값 명칭을 가져와서 셋팅

					Map tmpParamMap = new HashMap();
					tmpParamMap.put("dbUser", dbUser);
					tmpParamMap.put("orgId", orgId);
					tmpParamMap.put("tblId", tblId);
					StringBuffer tmpBuffKo = new StringBuffer();
					StringBuffer tmpBuffEn = new StringBuffer();
					for(int i = 0; i < analClassList.size(); i++){
						tmpParamMap.put("objVarId", analClassList.get(i));
						tmpParamMap.put("itmId", analItemList.get(i));

						Map tmpItemMap = statHtmlDAO.selectItemName(tmpParamMap);
						tmpBuffKo.append((String)tmpItemMap.get("SCR_KOR"));
						tmpBuffEn.append((String)tmpItemMap.get("SCR_ENG"));

						if(i < analClassList.size() - 1){
							tmpBuffKo.append(" ");
							tmpBuffEn.append(" ");
						}
					}

					paramInfo.setAnalTypeNm(paramInfo.getAnalTypeNm() + "-" + tmpBuffKo.toString());
					paramInfo.setAnalTypeNmEng(paramInfo.getAnalTypeNmEng() + "-" + tmpBuffEn.toString());

				}
			}
			//구성비, 누계구성비인경우 해당 항목이나 분류값 명칭을 가져와서 셋팅한다.
			else{
				if( analType.equals("CMP_RATE") || analType.equals("TOTL_CMP_RATE") ){

					Map tmpParamMap = new HashMap();
					tmpParamMap.put("dbUser", dbUser);
					tmpParamMap.put("orgId", orgId);
					tmpParamMap.put("tblId", tblId);
					StringBuffer tmpBuffKo = new StringBuffer();
					StringBuffer tmpBuffEn = new StringBuffer();

					if(analItemStr != null){
						tmpParamMap.put("objVarId", "13999001");
						tmpParamMap.put("itmId", analItemStr);

						Map tmpItemMap = statHtmlDAO.selectItemName(tmpParamMap);
						tmpBuffKo.append((String)tmpItemMap.get("SCR_KOR"));
						tmpBuffEn.append((String)tmpItemMap.get("SCR_ENG"));

					}else{

						for(int i = 0; i < analClassList.size(); i++){
							tmpParamMap.put("objVarId", analClassList.get(i));
							tmpParamMap.put("itmId", analItemList.get(i));


							Map tmpItemMap = statHtmlDAO.selectItemName(tmpParamMap);
							tmpBuffKo.append((String)tmpItemMap.get("SCR_KOR"));
							tmpBuffEn.append((String)tmpItemMap.get("SCR_ENG"));

							if(i < analClassList.size() - 1){
								tmpBuffKo.append(" ");
								tmpBuffEn.append(" ");
							}
						}
					}

					paramInfo.setAnalTypeNm(paramInfo.getAnalTypeNm() + "-" + tmpBuffKo.toString());
					paramInfo.setAnalTypeNmEng(paramInfo.getAnalTypeNmEng() + "-" + tmpBuffEn.toString());

				}
			}
		}

		//FN_EXCPT_CD 가져오기 및 분류순서 셋팅하기
		if(doAnal){
			fnExcptCd = statHtmlDAO.selectFnExcptCd(paramInfo);
			if(fnExcptCd == null){
				fnExcptCd = "";
			}

			paramInfo.setFnExcptCd(fnExcptCd);

			setFuncPrdDe();

		}

		//Main SQL 시작
		strBuff.setLength(0);
		strBuff.append(" WITH COND AS \n");
		strBuff.append(" ( \n");
		strBuff.append("  	SELECT ITM0.TARGET_VALUE AS CHAR_ITM_ID ");
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append(", ITM" + (i  + 1) + ".TARGET_VALUE AS " + (String)classListForWhereClause.get(i) + " ");
		}
		strBuff.append(" \n");
		strBuff.append("	FROM " + condTable + " ITM0 ");
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append(" , " + condTable + " ITM" + (i + 1));
		}
		strBuff.append(" \n");
		strBuff.append("	WHERE ITM0.SESSION_ID ='" + sessionId + "' AND ITM0.TARGET_ID = 'ITM_ID' \n");
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append("		AND ITM" + (i + 1) + ".SESSION_ID = '" + sessionId + "' AND ITM" + (i + 1) + ".TARGET_ID = '" + (String)classListForWhereClause.get(i) + "'	\n");
		}
		strBuff.append(" ) \n");

		if(doAnal){
			//분석 시 부정기 관련해서 WITH절에 추가해야한다.
			if (analCmpr.equals("PREV_F") && !(analType.equals("CMP_RATE") || analType.equals("TOTL"))) {
				strBuff.append("                         ,  prd_de_f as (  \n");
				strBuff.append("                              select rownum rn, p.*   \n");
				strBuff.append("                              from  \n");
				strBuff.append("                              (  \n");
				strBuff.append("                              select * from " + dbUser + "tn_recd_prd  \n");
				strBuff.append("                              where 1 = 1  \n");
				
				//2015.06.04 상속통계표
				if("Y".equals(paramInfo.getInheritYn())){
					strBuff.append("                             and org_id='" + paramInfo.getOriginOrgId() + "' and tbl_id='" + paramInfo.getOriginTblId() + "'  \n");
				}else{
					strBuff.append("                             and org_id='" + orgId + "' and tbl_id='" + tblId + "'  \n");
				}
				strBuff.append("                                 and prd_se='F'   \n");
				strBuff.append("                              order by prd_de desc  \n");
				strBuff.append("                              ) p  \n");
				strBuff.append("                          )  \n");
			}

		}

		//이건 테스트 후에 진행해야 겠다.
		//if(classList.size() > 5){
		//	strBuff.append(" SELECT	/*+ ordered use_hash(C A) index(a UQTN_DIM) index(B PKTN_DT) */	\n");
		//}else{
		//	strBuff.append(" SELECT	/*+ ordered use_nl(C A) index(a UQTN_DIM) index(B PKTN_DT) */	\n");
		//}

		strBuff.append(" SELECT A.* ");

		//분석 select 추가
        if(doAnal){
        	addSelectAnal();
        }
		strBuff.append(" FROM ( \n");

		if(martUsable){
			if(martJoinMethod == 'H'){
				strBuff.append(" SELECT	/*+ ordered use_hash(B) */	\n");
			}else{
				strBuff.append(" SELECT /*+ ordered use_nl(A B) index(B UQ_" + mtId + ")*/	\n");
			}
		}else{
			strBuff.append(" SELECT	/*+ ordered use_nl(C A B) index(a UQTN_DIM) index(B PKTN_DT) */	\n");
		}

		if(paramInfo.getTableType().equals("perYear")){
			strBuff.append("       	'Y'||SUBSTR(B.PRD_DE, 1, 4) AS TIME_YEAR	\n");
			strBuff.append("       	, B.PRD_SE||SUBSTR(B.PRD_DE,  5) AS TIME_MQ	\n");
		}else{
			//strBuff.append("       	B.PRD_SE||B.PRD_DE AS TIME	\n");
			
			//2015.06.04 상속통계표
			if("Y".equals(paramInfo.getInheritYn())){
				strBuff.append("       	DECODE(B.PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + paramInfo.getOriginOrgId()  + "' AND TBL_ID = '" + paramInfo.getOriginTblId()  + "' and prd_se='F' ), B.PRD_SE)||B.PRD_DE AS TIME	\n");
			}else{
				strBuff.append("       	DECODE(B.PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + orgId  + "' AND TBL_ID = '" + tblId  + "' and prd_se='F' ), B.PRD_SE)||B.PRD_DE AS TIME	\n");
			}
		}

		//2014.10 Mart 적용
		String dimAlias = "A.";
		if(martUsable){
			dimAlias = "B.";
		}

		/********************************************************************************************************
		 * strBuffMainSelect 시작
		 ********************************************************************************************************/
		//2014.07.09 사용자 정의 함수를 스칼라서브쿼리로 변경
		//select column strBuffMainSelect 에 담는다.
		strBuffMainSelect.setLength(0);

		strBuffMainSelect.append("       	, B.PRD_SE, B.PRD_DE	\n");
		//2014.10 Mart 적용
		if(martUsable){
			strBuffMainSelect.append("       	, '" + orgId  + "' AS ORG_ID, '" + tblId + "' AS TBL_ID	\n");
		}else{
			strBuffMainSelect.append("       	, A.ORG_ID, A.TBL_ID	\n");
		}

		//분류 정보 동적으로 가져오기
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuffMainSelect.append(" , " + dimAlias + (String)classListForWhereClause.get(i) + " \n");
		}
		strBuffMainSelect.append("        , " + dimAlias + "CHAR_ITM_ID	\n");
		strBuffMainSelect.append("        , DECODE(B.DTVAL_CO, NULL, B.DTVAL_CN, B.DTVAL_CO) AS DTVAL_CO	\n");
		
		//2019.01.28 윗 줄에서 문자를 DTVAL_CO에 넣어버리면 분석식에서 더하고 빼고 할때 에러남 그래서 DTVAL_CO를 따로 가져와서 비교하기 위해 DTVAL_CO_BUFF 추가
		strBuffMainSelect.append("        , B.DTVAL_CO AS DTVAL_CO_BUFF	\n");
		strBuffMainSelect.append("        , B.SMBL_CN	\n");
		//2014.07.09 가중치존재여부에 따라 다르게 수행
		if(StringUtils.defaultString(paramInfo.getEnableWeight()).equals("Y")){
			//2019.1.7 시점별 가중치 조회 가능 여부 추가(보급은 제외)
			//strBuffMainSelect.append("        , (CASE WHEN " + dimAlias + "WGT_CO < 0 AND " + dimAlias + "WGT_CO > -1 THEN '-0' ELSE TO_CHAR (TRUNC (" + dimAlias + "WGT_CO, 0), 'FM999,999,999,999,999,999,999') END) || DECODE (MOD (" + dimAlias + "WGT_CO, 1), 0, NULL, TO_CHAR (ABS (MOD (" + dimAlias + "WGT_CO, 1))))||'' AS WGT_CO	\n");
			if(StringUtils.defaultString(paramInfo.getServerLocation()).equals("NSO")){
				if("Y".equals(paramInfo.getInheritYn())){	//상속통계표일 경우
					strBuffMainSelect.append("        , CASE WHEN (SELECT WGT_YN FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE) = 'Y' " );
				}else{
					strBuffMainSelect.append("        , CASE WHEN (SELECT WGT_YN FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE) = 'Y' " );
				}
				strBuffMainSelect.append("          THEN ( (CASE WHEN " + dimAlias + "WGT_CO < 0 AND " + dimAlias + "WGT_CO > -1 THEN '-0' ELSE TO_CHAR (TRUNC (" + dimAlias + "WGT_CO, 0), 'FM999,999,999,999,999,999,999') END) || DECODE (MOD (" + dimAlias + "WGT_CO, 1), 0, NULL, TO_CHAR (ABS (MOD (" + dimAlias + "WGT_CO, 1))))||'' )" );
				strBuffMainSelect.append("          ELSE '-' END AS WGT_CO	\n");
			}else{ // 보급일 경우 TN_RECD_PRD 테이블의 WGT_YN를 참조하지 않도록...보급할때 필드 추가 필요없게 하려고...
				strBuffMainSelect.append("        , (CASE WHEN " + dimAlias + "WGT_CO < 0 AND " + dimAlias + "WGT_CO > -1 THEN '-0' ELSE TO_CHAR (TRUNC (" + dimAlias + "WGT_CO, 0), 'FM999,999,999,999,999,999,999') END) || DECODE (MOD (" + dimAlias + "WGT_CO, 1), 0, NULL, TO_CHAR (ABS (MOD (" + dimAlias + "WGT_CO, 1))))||'' AS WGT_CO	\n");
			}
		}else{
			strBuffMainSelect.append("        , '' AS WGT_CO	\n");
		}
		//strBuffMainSelect.append("        , '111' AS WGT_CO	\n"); //TEST
		//2015.06.04 상속통계표
		if("Y".equals(paramInfo.getInheritYn())){
			strBuffMainSelect.append("        , DECODE((SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND  PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE)	\n");
		}else{
			strBuffMainSelect.append("        , DECODE((SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND  PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE)	\n");
		}
		strBuffMainSelect.append("			, NULL	\n");
		strBuffMainSelect.append("			, DECODE(" + dimAlias + "PERIOD_CO, NULL, (SELECT PERIOD_CO FROM " + dbUser + "TN_ITM_LIST WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND OBJ_VAR_ID = '13999001' AND ITM_ID = " + dimAlias + "CHAR_ITM_ID), " + dimAlias + "PERIOD_CO)	\n");

		//2015.06.04 상속통계표
		if("Y".equals(paramInfo.getInheritYn())){
			strBuffMainSelect.append("			, (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND  PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE)	\n");
		}else{
			strBuffMainSelect.append("			, (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND  PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE)	\n");
		}
		strBuffMainSelect.append("		)||'' AS PERIOD_CO	\n");
		strBuffMainSelect.append("		, CASE " + dimAlias + "CMMT_AT	\n");
		strBuffMainSelect.append("		  	WHEN 'Y' THEN	\n");
		strBuffMainSelect.append("		    	CASE	\n");
		strBuffMainSelect.append("		        	WHEN (	\n");
		strBuffMainSelect.append("		        			SELECT	COUNT (1)	\n");
		strBuffMainSelect.append("		                  	FROM	" + dbUser + "TN_CMMT_INFO	\n");
		strBuffMainSelect.append("		                	WHERE   1 = 1	\n");
		
		//2015.06.08 상속통계표
		if("Y".equals(paramInfo.getInheritYn())){
			strBuffMainSelect.append("		                     		AND ORG_ID = '" + paramInfo.getOriginOrgId() + "'	\n");
			strBuffMainSelect.append("		                     		AND TBL_ID = '" + paramInfo.getOriginTblId() + "'	\n");
		}else{
			strBuffMainSelect.append("		                     		AND ORG_ID = '" + orgId + "'	\n");
			strBuffMainSelect.append("		                     		AND TBL_ID = '" + tblId + "'	\n");
		}
		strBuffMainSelect.append("		                      		AND CMMT_SE = '1210614'	\n");
		strBuffMainSelect.append("		                     		AND LNG_SE = '1211910'	\n");
		strBuffMainSelect.append("		                     		AND ITM_RCGN_SN = " + dimAlias + "ITM_RCGN_SN) > 0 THEN	\n");
		strBuffMainSelect.append("		           		'Y'	\n");
		strBuffMainSelect.append("		        	ELSE	\n");
		strBuffMainSelect.append("		            	'N'	\n");
		strBuffMainSelect.append("		 		END	\n");
		strBuffMainSelect.append("		 	ELSE	\n");
		strBuffMainSelect.append("		     	'N'	\n");
		strBuffMainSelect.append("		END AS CMMT_AT_KOR	\n");
		strBuffMainSelect.append("		, CASE " + dimAlias + "CMMT_AT	\n");
		strBuffMainSelect.append("		  	WHEN 'Y' THEN	\n");
		strBuffMainSelect.append("		    	CASE	\n");
		strBuffMainSelect.append("		        	WHEN (	\n");
		strBuffMainSelect.append("		        			SELECT	COUNT (1)	\n");
		strBuffMainSelect.append("		                 	FROM    " + dbUser + "TN_CMMT_INFO	\n");
		strBuffMainSelect.append("		                	WHERE   1 = 1	\n");
		
		//2015.06.08 상속통계표
		if("Y".equals(paramInfo.getInheritYn())){
			strBuffMainSelect.append("		                     		AND ORG_ID = '" + paramInfo.getOriginOrgId() + "'	\n");
			strBuffMainSelect.append("		                     		AND TBL_ID = '" + paramInfo.getOriginTblId() + "'	\n");
		}else{
			strBuffMainSelect.append("		                     		AND ORG_ID = '" + orgId + "'	\n");
			strBuffMainSelect.append("		                     		AND TBL_ID = '" + tblId + "'	\n");
		}
		strBuffMainSelect.append("		                      		AND CMMT_SE = '1210614'	\n");
		strBuffMainSelect.append("		                      		AND LNG_SE = '1211911'	\n");
		strBuffMainSelect.append("		                     		AND ITM_RCGN_SN = " + dimAlias + "ITM_RCGN_SN) > 0 THEN	\n");
		strBuffMainSelect.append("		          		'Y'	\n");
		strBuffMainSelect.append("		         	ELSE	\n");
		strBuffMainSelect.append("		             	'N'	\n");
		strBuffMainSelect.append("		  		END	\n");
		strBuffMainSelect.append("		  	ELSE	\n");
		strBuffMainSelect.append("		      	'N'	\n");
		strBuffMainSelect.append("		END AS CMMT_AT_ENG	\n");
		//2014.07.09 차원단위존재여부에 따라 다르게 수행
		if(StringUtils.defaultString(paramInfo.getEnableCellUnit()).equals("Y")){
			strBuffMainSelect.append("		, " + dimAlias + "UNIT_ID	\n");
			strBuffMainSelect.append("		, DECODE(" + dimAlias + "UNIT_ID, NULL, NULL, (SELECT NVL(CD_ABBR_NM, CD_NM) AS CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = " + dimAlias + "UNIT_ID)) AS UNIT_NM_KOR	\n");
			strBuffMainSelect.append("		, DECODE(" + dimAlias + "UNIT_ID, NULL, NULL, (SELECT NVL(CD_ABBR_ENG_NM, CD_ENG_NM) AS CD_ENG_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = " + dimAlias + "UNIT_ID)) AS UNIT_NM_ENG	\n");
		}else{
			strBuffMainSelect.append("      , '' AS UNIT_ID	\n");
			strBuffMainSelect.append("		, '' AS UNIT_NM_KOR	\n");
			strBuffMainSelect.append("		, '' AS UNIT_NM_ENG	\n");
		}
		strBuffMainSelect.append("		, TO_CHAR(" + dimAlias + "ITM_RCGN_SN) AS ITM_RCGN_SN	\n");
		
		//2020-05-19 업무규칙 SDSS0210에 해당하여 수치가 [-]로 나오는데 더미일 경우에는 예외를 두어 빈칸으로 나오도록 하자 - 손상호 주무관
		strBuffMainSelect.append("		, " + dimAlias + "DMM_AT	\n");
		
		/********************************************************************************************************
		 * strBuffMainSelect 끝
		 ********************************************************************************************************/

		strBuff.append(strBuffMainSelect);

		//PUB_SE는 서비스용으로 한정한다.
		if(martUsable){
			if(martJoinMethod == 'H'){
				strBuff.append(" FROM	COND C, \n");
				strBuff.append(" 		(\n");
				strBuff.append(" 			SELECT /*+ INDEX(B IDX01_" + mtId + ") */ * \n");
				strBuff.append(" 			FROM NSI_MART." + mtId + " B \n");
				strBuff.append("        	WHERE (\n");
				strBuff.append("        	" + prdWhereClause + "	\n");
				strBuff.append("        	)\n");
				//공표구분
				if(serverType.equals("service_en")){
					strBuff.append("        	AND PUB_SE_DIM IN ('1210110', '1210114')	\n");
					strBuff.append("        	AND PUB_SE_DT IN ('1210110', '1210114')	\n");
				}else{
					strBuff.append("        	AND PUB_SE_DIM IN ('1210110', '1210113', '1210114')	\n");
					strBuff.append("        	AND PUB_SE_DT IN ('1210110', '1210113', '1210114')	\n");
				}

				strBuff.append(" 			AND DMM_AT = 'N' \n");
				strBuff.append(" 		) B \n");
				//항목, 분류값 조건
				strBuff.append(" WHERE 	C.CHAR_ITM_ID = B.CHAR_ITM_ID	\n");
				for(int i = 0; i < classListForWhereClause.size(); i++){
					strBuff.append("        AND C." + (String)classListForWhereClause.get(i) + " = B." + (String)classListForWhereClause.get(i) + " \n");
				}
				//dummy
				strBuff.append(" 		AND DMM_AT = 'N' \n");
			}else{
				strBuff.append(" FROM	COND C, NSI_MART." + mtId + " B \n");
				//항목, 분류값 조건
				strBuff.append(" WHERE 	C.CHAR_ITM_ID = B.CHAR_ITM_ID	\n");
				for(int i = 0; i < classListForWhereClause.size(); i++){
					strBuff.append("        AND C." + (String)classListForWhereClause.get(i) + " = B." + (String)classListForWhereClause.get(i) + " \n");
				}
				strBuff.append(" 	   	AND (\n");
				strBuff.append("       	" + prdWhereClause + "	\n");
				strBuff.append("       	)\n");
				//공표구분
				if(serverType.equals("service_en")){
					strBuff.append("       	AND B.PUB_SE_DIM IN ('1210110', '1210114')	\n");
					strBuff.append("       	AND B.PUB_SE_DT IN ('1210110', '1210114')	\n");
				}else{
					strBuff.append("       	AND B.PUB_SE_DIM IN ('1210110', '1210113', '1210114')	\n");
					strBuff.append("       	AND B.PUB_SE_DT IN ('1210110', '1210113', '1210114')	\n");
				}

				strBuff.append(" 		AND B.DMM_AT = 'N' \n");

			}
		}else{
			strBuff.append(" FROM	COND C, " + dbUser + "TN_DIM A, " + dbUser + "TN_DT B	\n");
			strBuff.append(" WHERE  1=1	\n");
			strBuff.append("        AND A.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        AND A.TBL_ID = '" + tblId + "'	\n");
			//2015.06.04 상속통계표
			if("Y".equals(paramInfo.getInheritYn())){
				strBuff.append("        AND B.ORG_ID = '" + paramInfo.getOriginOrgId() + "'	\n");
				strBuff.append("        AND B.TBL_ID = '" + paramInfo.getOriginTblId() + "'	\n");
			}else{
				strBuff.append("        AND A.ORG_ID = B.ORG_ID	\n");
				strBuff.append("        AND A.TBL_ID = B.TBL_ID	\n");
			}
			strBuff.append("        AND A.ITM_RCGN_SN = B.ITM_RCGN_SN	\n");
			//주기, 시점 조건
			strBuff.append("        AND (\n");
			strBuff.append("        " + prdWhereClause + "	\n");
			strBuff.append("        )\n");
			//항목, 분류값 조건
			strBuff.append("        AND A.CHAR_ITM_ID = C.CHAR_ITM_ID	\n");
			for(int i = 0; i < classListForWhereClause.size(); i++){
				strBuff.append("        AND A." + (String)classListForWhereClause.get(i) + " = C." + (String)classListForWhereClause.get(i) + " \n");
			}
			
			//2015.06.16 상속통계표 공표 체크
			if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
				//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
			}else{
				//TN_DIM, TN_DT 공표구분 체크
				if(serverType.equals("service_en")){
					strBuff.append("        AND A.PUB_SE IN ('1210110', '1210114')	\n");
					strBuff.append("        AND B.PUB_SE IN ('1210110', '1210114')	\n");
				}else if(serverType.equals("stat")){
					strBuff.append("        AND A.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
					strBuff.append("        AND B.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
				}else if(serverType.equals("stat_emp")){
					strBuff.append("        AND A.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
					strBuff.append("        AND B.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				}else{
					strBuff.append("        AND A.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
					strBuff.append("        AND B.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
				}
			}

			//더미 여부 체크
			// 2015.7.3 주석 처리 해야 할 수 있음
			// 2016.1.28 dt에는 수치가 있는데 dim에 더미코드가 Y여서 수치가 안나오는 문제로 주석 (수치가 있으면 더미여도 수치가 우선) 위에 주석 누가달았는지 모름 - 김경호
			//strBuff.append("        AND NVL(A.DMM_AT, 'N') != 'Y'	\n " ); 

			//strBuff.append("        AND (B.DTVAL_CO IS NOT NULL OR B.DTVAL_CN IS NOT NULL OR B.SMBL_CN IS NOT NULL) \n");
		}


		//2014.07.09 더미인 통계표만 더미sql 사용
		if(dmmDispKd.equals("Y")){
			addDmmtAt();
		}

        strBuff.append(" ) A \n");

        //분석 from 추가
        if(doAnal){
        	addFromAnal();
        }

        //분석 where 추가
        if(doAnal){
        	strBuff.append(" WHERE 1 = 1 \n");
        	addWhereAnal();
        }

		return strBuff.toString();
	}

	private void setClassListForWhereClause(){
		classListForWhereClause = statHtmlDAO.getClassListForWhereClause(paramInfo);
	}

	public List<String> getClassListForWhereClause() {
		return classListForWhereClause;
	}

	private void addDmmtAt(){
		strBuff.append(" UNION ALL	\n");

		strBuff.append(" SELECT /*+ ordered use_nl(A B) index(a UQTN_DIM) index(B PKTN_DT) */ \n");

		if(paramInfo.getTableType().equals("perYear")){
			strBuff.append("       	'Y'||SUBSTR(A.TIME_DE, 1, 4) AS TIME_YEAR	\n");
			strBuff.append("       	, A.TIME_SE||SUBSTR(A.TIME_DE,  5) AS TIME_MQ	\n");
		}else{
			//strBuff.append("       	B.PRD_SE||B.PRD_DE AS TIME	\n");

			//2015.06.23 상속통계표
			if("Y".equals(paramInfo.getInheritYn())){
				strBuff.append("       	DECODE(A.TIME_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + paramInfo.getOriginOrgId()  + "' AND TBL_ID = '" + paramInfo.getOriginTblId()  + "' and prd_se='F'), A.TIME_SE)||A.TIME_DE AS TIME	\n");
			}else{
				strBuff.append("       	DECODE(A.TIME_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID and prd_se='F'), A.TIME_SE)||A.TIME_DE AS TIME	\n");
			}			
			
		}

		/************************************************
		 * 2014.10 마트 적용으로 인해..다시 정의한다.
		************************************************/

		//strBuff.append(strBuffMainSelect);
		strBuff.append("       	, B.PRD_SE, B.PRD_DE	\n");
		//2014.10 Mart 적용
		strBuff.append("       	, A.ORG_ID, A.TBL_ID	\n");

		//분류 정보 동적으로 가져오기
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append(" , A." + (String)classListForWhereClause.get(i) + " \n");
		}
		strBuff.append("        , A.CHAR_ITM_ID	\n");
		strBuff.append("        , DECODE(B.DTVAL_CO, NULL, B.DTVAL_CN, B.DTVAL_CO) AS DTVAL_CO	\n");
		
		//2019.01.28 윗 줄에서 문자를 DTVAL_CO에 넣어버리면 분석식에서 더하고 빼고 할때 에러남 그래서 DTVAL_CO를 따로 가져와서 비교하기 위해 DTVAL_CO_BUFF 추가
		strBuff.append("        , B.DTVAL_CO AS DTVAL_CO_BUFF	\n");
		strBuff.append("        , B.SMBL_CN	\n");
		//2014.07.09 가중치존재여부에 따라 다르게 수행
		if(StringUtils.defaultString(paramInfo.getEnableWeight()).equals("Y")){
			//2019.1.7 시점별 가중치 조회 가능 여부 추가(보급은 제외)
			//strBuff.append("        , (CASE WHEN A.WGT_CO < 0 AND A.WGT_CO > -1 THEN '-0' ELSE TO_CHAR (TRUNC (A.WGT_CO, 0), 'FM999,999,999,999,999,999,999') END) || DECODE (MOD (A.WGT_CO, 1), 0, NULL, TO_CHAR (ABS (MOD (A.WGT_CO, 1))))||'' AS WGT_CO	\n");
			if(StringUtils.defaultString(paramInfo.getServerLocation()).equals("NSO")){
				strBuff.append("        , CASE WHEN (SELECT WGT_YN FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = B.ORG_ID AND TBL_ID = B.TBL_ID AND PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE) = 'Y' " );
				strBuff.append("          THEN ( (CASE WHEN A.WGT_CO < 0 AND A.WGT_CO > -1 THEN '-0' ELSE TO_CHAR (TRUNC (A.WGT_CO, 0), 'FM999,999,999,999,999,999,999') END) || DECODE (MOD (A.WGT_CO, 1), 0, NULL, TO_CHAR (ABS (MOD (A.WGT_CO, 1))))||'' )" );
				strBuff.append("          ELSE '-' END AS WGT_CO	\n");
			}else{ // 보급일 경우 TN_RECD_PRD 테이블의 WGT_YN를 참조하지 않도록...보급할때 필드 추가 필요없게 하려고...
				strBuff.append("        , (CASE WHEN A.WGT_CO < 0 AND A.WGT_CO > -1 THEN '-0' ELSE TO_CHAR (TRUNC (A.WGT_CO, 0), 'FM999,999,999,999,999,999,999') END) || DECODE (MOD (A.WGT_CO, 1), 0, NULL, TO_CHAR (ABS (MOD (A.WGT_CO, 1))))||'' AS WGT_CO	\n");
			}
		}else{
			strBuff.append("        , '' AS WGT_CO	\n");
		}
		strBuff.append("        , DECODE((SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = B.ORG_ID AND TBL_ID = B.TBL_ID AND  PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE)	\n");
		strBuff.append("			, NULL	\n");
		strBuff.append("			, DECODE(A.PERIOD_CO, NULL, (SELECT PERIOD_CO FROM " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID), A.PERIOD_CO)	\n");
		strBuff.append("			, (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = B.ORG_ID AND TBL_ID = B.TBL_ID AND  PRD_SE = B.PRD_SE AND PRD_DE = B.PRD_DE)	\n");
		strBuff.append("		)||'' AS PERIOD_CO	\n");
		strBuff.append("		, CASE A.CMMT_AT	\n");
		strBuff.append("		  	WHEN 'Y' THEN	\n");
		strBuff.append("		    	CASE	\n");
		strBuff.append("		        	WHEN (	\n");
		strBuff.append("		        			SELECT	COUNT (1)	\n");
		strBuff.append("		                  	FROM	" + dbUser + "TN_CMMT_INFO	\n");
		strBuff.append("		                	WHERE   ORG_ID = A.ORG_ID	\n");
		strBuff.append("		                     		AND TBL_ID = A.TBL_ID	\n");
		strBuff.append("		                      		AND CMMT_SE = '1210614'	\n");
		strBuff.append("		                     		AND LNG_SE = '1211910'	\n");
		strBuff.append("		                     		AND ITM_RCGN_SN = A.ITM_RCGN_SN) > 0 THEN	\n");
		strBuff.append("		           		'Y'	\n");
		strBuff.append("		        	ELSE	\n");
		strBuff.append("		            	'N'	\n");
		strBuff.append("		 		END	\n");
		strBuff.append("		 	ELSE	\n");
		strBuff.append("		     	'N'	\n");
		strBuff.append("		END AS CMMT_AT_KOR	\n");
		strBuff.append("		, CASE A.CMMT_AT	\n");
		strBuff.append("		  	WHEN 'Y' THEN	\n");
		strBuff.append("		    	CASE	\n");
		strBuff.append("		        	WHEN (	\n");
		strBuff.append("		        			SELECT	COUNT (1)	\n");
		strBuff.append("		                 	FROM    " + dbUser + "TN_CMMT_INFO	\n");
		strBuff.append("		               		WHERE   ORG_ID = A.ORG_ID	\n");
		strBuff.append("		                      		AND TBL_ID = A.TBL_ID	\n");
		strBuff.append("		                      		AND CMMT_SE = '1210614'	\n");
		strBuff.append("		                      		AND LNG_SE = '1211911'	\n");
		strBuff.append("		                     		AND ITM_RCGN_SN = A.ITM_RCGN_SN) > 0 THEN	\n");
		strBuff.append("		          		'Y'	\n");
		strBuff.append("		         	ELSE	\n");
		strBuff.append("		             	'N'	\n");
		strBuff.append("		  		END	\n");
		strBuff.append("		  	ELSE	\n");
		strBuff.append("		      	'N'	\n");
		strBuff.append("		END AS CMMT_AT_ENG	\n");
		//2014.07.09 차원단위존재여부에 따라 다르게 수행
		if(StringUtils.defaultString(paramInfo.getEnableCellUnit()).equals("Y")){
			strBuff.append("		, A.UNIT_ID	\n");
			strBuff.append("		, DECODE(A.UNIT_ID, NULL, NULL, (SELECT NVL(CD_ABBR_NM, CD_NM) AS CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = A.UNIT_ID)) AS UNIT_NM_KOR	\n");
			strBuff.append("		, DECODE(A.UNIT_ID, NULL, NULL, (SELECT NVL(CD_ABBR_ENG_NM, CD_ENG_NM) AS CD_ENG_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = A.UNIT_ID)) AS UNIT_NM_ENG	\n");
		}else{
			strBuff.append("      , '' AS UNIT_ID	\n");
			strBuff.append("		, '' AS UNIT_NM_KOR	\n");
			strBuff.append("		, '' AS UNIT_NM_ENG	\n");
		}
		strBuff.append("		, TO_CHAR(A.ITM_RCGN_SN) AS ITM_RCGN_SN,	\n");

		//2020-05-19 업무규칙 SDSS0210에 해당하여 수치가 [-]로 나오는데 더미일 경우에는 예외를 두어 빈칸으로 나오도록 하자 - 손상호 주무관 
		strBuff.append("		A.DMM_AT");
		
		strBuff.append(" FROM ( \n");

		strBuff.append(" 	SELECT	/*+ ordered use_nl(C A) index(a UQTN_DIM) */	\n");
		strBuff.append("       	B.TIME_SE, B.TIME_DE	\n");
		strBuff.append("       	, A.ORG_ID, A.TBL_ID, A.ITM_RCGN_SN	\n");
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append(" , A." + (String)classListForWhereClause.get(i) + " \n");
		}
		strBuff.append(" 	, A.CHAR_ITM_ID	\n");
		strBuff.append(" 	, A.WGT_CO	\n");
		strBuff.append(" 	, A.PERIOD_CO	\n");
		strBuff.append(" 	, A.CMMT_AT	\n");
		strBuff.append(" 	, A.UNIT_ID	\n");

		//2020-05-19 업무규칙 SDSS0210에 해당하여 수치가 [-]로 나오는데 더미일 경우에는 예외를 두어 빈칸으로 나오도록 하자 - 손상호 주무관 
		strBuff.append("	, A.DMM_AT");
				
		strBuff.append(" 	FROM	COND C, " + dbUser + "TN_DIM A,	\n");

		//Dummy where
		strBuff.append("     		(	\n");

		int index = 0;
		for(String tmpPrdDe : prdListDummy){
			if(index > 0){
				strBuff.append("     	UNION ALL	\n");
			}

			strBuff.append("       	SELECT SUBSTR('" + tmpPrdDe + "', 1, 1) AS TIME_SE, SUBSTR('" + tmpPrdDe + "', 2) AS TIME_DE FROM DUAL	\n");

			index++;
		}

		strBuff.append("     	) B	\n");

		strBuff.append("  WHERE	A.ORG_ID = '" + orgId + "'	\n");
		strBuff.append("        AND A.TBL_ID = '" + tblId + "'	\n");

		//항목, 분류값 조건
		strBuff.append("        AND A.CHAR_ITM_ID = C.CHAR_ITM_ID	\n");
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append("        AND A." + (String)classListForWhereClause.get(i) + " = C." + (String)classListForWhereClause.get(i) + " \n");
		}

		//TN_DIM 공표구분 체크
		if(serverType.equals("service_en")){
			strBuff.append("        AND A.PUB_SE IN ('1210110', '1210114')	\n");
		}else if(serverType.equals("stat")){
			strBuff.append("        AND A.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
		}else if(serverType.equals("stat_emp")){
			strBuff.append("        AND A.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
		}else{
			strBuff.append("        AND A.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
		}

		//더미 여부 체크
		strBuff.append("        AND A.DMM_AT = 'Y'	\n"); //2014.07.09 NVL 함수 제거
		//strBuff.append("        AND NOT EXISTS (SELECT 1 FROM " + dbUser +"TN_DT WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND  ITM_RCGN_SN = A.ITM_RCGN_SN)  \n");

		strBuff.append(" ) A," + dbUser + "TN_DT B \n");
		strBuff.append(" WHERE   1 = 1 \n");
		//2015.10.28 상속통계표
		if("Y".equals(paramInfo.getInheritYn())){
			strBuff.append("         AND A.ORG_ID = '" + orgId + "' \n");
			strBuff.append("         AND A.TBL_ID = '" + tblId + "' \n");
			strBuff.append("         AND B.ORG_ID = '" + paramInfo.getOriginOrgId() + "' \n");
			strBuff.append("         AND B.TBL_ID = '" + paramInfo.getOriginTblId() + "' \n");
		}else{
			strBuff.append("         AND A.ORG_ID = B.ORG_ID(+) \n");
			strBuff.append("         AND A.TBL_ID = B.TBL_ID(+) \n");
		}
		strBuff.append("         AND A.ITM_RCGN_SN = B.ITM_RCGN_SN(+) \n");
		strBuff.append("         AND A.TIME_SE = B.PRD_SE(+) \n");
		strBuff.append("         AND A.TIME_DE = B.PRD_DE(+) \n");

		//2015.06.16 상속통계표 공표 체크
		if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
			//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
		}else{
			//TN_DT 공표구분 체크
			if(serverType.equals("service_en")){
				//strBuff.append("        AND B.PUB_SE IN ('1210110', '1210114')	\n");
				strBuff.append("        AND INSTR('1210110@1210114', B.PUB_SE(+)) > 0	\n");
			}else if(serverType.equals("stat")){
				//strBuff.append("        AND B.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        AND INSTR('1210110@1210112@1210113@1210114', B.PUB_SE(+)) > 0	\n");
			}else if(serverType.equals("stat_emp")){
				//strBuff.append("        AND B.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        AND INSTR('1210110@1210111@1210112@1210113@1210114', B.PUB_SE(+)) > 0	\n");
			}else{
				//strBuff.append("        AND B.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
				strBuff.append("        AND INSTR('1210110@1210113@1210114', B.PUB_SE(+)) > 0	\n");
			}
		}
	}

	private void setFuncPrdDe(){
		StringBuffer sb = new StringBuffer();

		// 전월비, 전분기비, 전반기비, 전년비
		if(analCmpr.equals("PREV_M") || analCmpr.equals("PREV_H") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_F")){
			if(prdSe.equals("M")){
				sb.append("    select to_char(ADD_MONTHS(to_date(substr('" + from + "',2,6),'YYYYMM'),-1), 'YYYYMM') ff, \n");
				sb.append("        to_char(ADD_MONTHS(to_date(substr('" + to + "',2,6),'YYYYMM'),-1), 'YYYYMM') tt from dual   \n");
			}else if(prdSe.equals("H")){
				sb.append("    select case when '" + from + "' like '%01' then substr('" + from + "',2,4)-1||'02' else substr('" + from + "',2,6)-1||'' end ff, \n");
				sb.append("            case when '" + to + "' like '%01' then substr('" + to + "',2,4)-1||'02' else substr('" + to + "',2,6)-1||'' end tt from dual \n");
			}else if(prdSe.equals("Y")){
				sb.append("    select substr('" + from + "',2,4)-1||'' ff, substr('" + to + "',2,4)-1||'' tt from dual  \n");
			}else if(prdSe.equals("Q")){
				sb.append("    select case when '" + from + "' like '%01' then substr('" + from + "',2,4)-1||'04' else substr('" + from + "',2,6)-1||'' end ff, \n");
				sb.append("            case when '" + to + "' like '%01' then substr('" + to + "',2,4)-1||'04' else substr('" + to + "',2,6)-1||'' end tt from dual \n");
			}else if(prdSe.equals("F")){
				sb.append("    with prd_de_f as (  \n");
				sb.append("        select rownum rn, p.*   \n");
				sb.append("        from      \n");
				sb.append("            (  \n");
				sb.append("            select * from " + dbUser + "tn_recd_prd  \n");
				sb.append("            where org_id='" + orgId + "' and tbl_id='" + tblId + "'  \n");
				sb.append("               and prd_se='F'   \n");
				sb.append("            order by prd_de desc  \n");
				sb.append("            ) p  \n");
				sb.append("    )  \n");
				sb.append("    select min(c.prd_de) ff, max(c.prd_de) tt   \n");
				sb.append("    from " + dbUser + "tn_recd_prd a, prd_de_f b, prd_de_f c  \n");
				sb.append("    where a.org_id='" + orgId + "' and a.tbl_id='" + tblId + "'  \n");
				sb.append("           and a.prd_se='F'   \n");
				sb.append("           and a.prd_se||a.prd_de between '" + from + "' and '" + to + "'  \n");
				sb.append("           and a.prd_se = b.prd_se  \n");
				sb.append("           and a.prd_de = b.prd_de  \n");
				sb.append("           and a.prd_se = b.prd_se  \n");
				sb.append("           and c.rn = b.rn+1  \n");
			}
		}
		// 전년말월비, 전년말분기비, 전년말반기비
		else if(analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_HE") || analCmpr.equals("PREV_Y_QE")){
			if(prdSe.equals("M")){
				sb.append("	   select substr('" + from + "',2,4)-1||'12' ff, substr('" + to + "',2,4)-1||'12' tt from dual \n");
			}else if(prdSe.equals("H")){
				sb.append("    select substr('" + from + "',2,4)-1||'02' ff, substr('" + to + "',2,4)-1||'02' tt from dual \n");
			}else if(prdSe.equals("Q")){
				sb.append("    select substr('" + from + "',2,4)-1||'04' ff, substr('" + to + "',2,4)-1||'04' tt from dual \n");
			}
		}
		// 전년동월비, 전년동분기비, 전년동반기비
		else if(analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y_HS") || analCmpr.equals("PREV_Y_QS")){
			sb.append("    select substr('" + from + "',2,4)-1||substr('" + from + "',6,2)||'' ff, substr('" + to + "',2,4)-1||substr('" + to + "',6,2)||'' tt from dual  \n");
		}
		// 전년누계비
		else if(analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL")){
			sb.append("  select substr('" + from + "',2,4)-1||'01' ff, substr('" + to + "',2,4)-1||substr('" + to + "',6,2)||'' tt from dual \n");
		}
		// 기준시점비
		else if(analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H")|| analCmpr.equals("ONE_F")){
			sb.append("  select substr('" + analTime + "',2,6)||'' ff, substr('" + analTime + "',2,6)||'' tt from dual \n");
		}

		if(analCmpr != null && !analCmpr.equals("") && !analCmpr.equals("N")){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("sql", sb.toString());

			Map funcPrdMap = statHtmlDAO.selectFuncPrd(paramMap);
			fromFunc = (String)funcPrdMap.get("FF");
			toFunc = (String)funcPrdMap.get("TT");
			//System.out.println("@@@@@@@@@@ fromFunc, toFunc ::: " + fromFunc + "," + toFunc);
		}
	}

	private void addSelectAnal(){
		// 2015.4.13
		// 분석 기준선택여부 체크
		String selectType = new String();
		String noselectType = new String();

		selectType = paramInfo.getNoSelect();

		noselectType = paramInfo.setNoSelect(selectType);

		//증감
		if(analType.equals("CHG")){
			//누계비인경우
			if(analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL")){
				strBuff.append("	,Z1.CUR_PRE ANAL_CO    \n");
			}else{
				//strBuff.append("	, CASE WHEN Z1.VAL2 IS NULL THEN NULL    \n"); //2016.02.25 DTVAL_CO가 없고 DTVAL_CN만 있을때 증감을 이용하면 에러나는것 때문에 추가 - 김경호
				strBuff.append("	, CASE WHEN (Z1.VAL2 IS NULL) OR (A.DTVAL_CO_BUFF IS NULL) THEN NULL    \n"); //2020.07.02 DTVAL_CO에 문자값이 들어갔을 경우 아래처럼 빼기를 하면 에러가 나기 때문에 위에꺼 보완 - 김경호
				strBuff.append("	ELSE A.DTVAL_CO - Z1.VAL2 END AS ANAL_CO   \n");
			}
		}
		//증감률
		else if(analType.equals("CHG_RATE")){

			//누계비인경우
			if(analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL")){
				//체크 - 이 케이스에 해당하는 통계표를 테스트하지 못했음.
				if (fnExcptCd.equals("CONSTRUCT")) {
					strBuff.append("	, CASE WHEN Z1.PRE_DIV_CNT IS NULL OR Z1.PRE_DIV_CNT = 0 THEN NULL ELSE ROUND(   \n");
					strBuff.append(" 		( CASE WHEN ( Z1.CUR_DIV_CNT - Z1.PRE_DIV_CNT ) < 0 THEN -1 ELSE 1 END )* ABS  \n");
					strBuff.append("    		(  ( ROUND(Z1.CUR_DIV_CNT, NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, A.PRD_DE), 8) )   \n");
					strBuff.append("                        -  ROUND( Z1.PRE_DIV_CNT, NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, (TO_CHAR(TO_NUMBER(SUBSTR(A.PRD_DE,1,4))-1)||SUBSTR(A.PRD_DE,5,2))), 8) ) )  \n");
					strBuff.append("                         / ROUND( Z1.PRE_DIV_CNT, NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, (TO_CHAR(TO_NUMBER(SUBSTR(A.PRD_DE,1,4))-1)||SUBSTR(A.PRD_DE,5,2))), 8) )  \n");
					strBuff.append("                         ) * 100, 8) END ANAL_CO   \n");
				}else{
					strBuff.append("     , CASE WHEN Z1.PRE_DIV_CNT IS NULL OR Z1.PRE_DIV_CNT = 0 THEN NULL ELSE ROUND(  \n");
					strBuff.append("     	(  ( ROUND(Z1.CUR_DIV_CNT, NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, A.PRD_DE), 8) )   \n");
					strBuff.append("             			-  ROUND( Z1.PRE_DIV_CNT, NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, (TO_CHAR(TO_NUMBER(SUBSTR(A.PRD_DE,1,4))-1)||SUBSTR(A.PRD_DE,5,2))), 8) ) )  \n");
					strBuff.append("                         / ROUND( Z1.PRE_DIV_CNT, NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, (TO_CHAR(TO_NUMBER(SUBSTR(A.PRD_DE,1,4))-1)||SUBSTR(A.PRD_DE,5,2))), 8) )  \n");
					strBuff.append("                         ) * 100, 8) END ANAL_CO   \n");
				}
			}else{
				//2019.01.28 DTVAL_CO_BUFF 추가
				if (fnExcptCd.equals("CONSTRUCT")) {
					strBuff.append("	, CASE WHEN (Z1.VAL2 IS NULL) OR (Z1.VAL2  = 0)  OR (A.DTVAL_CO_BUFF IS NULL) THEN NULL    \n");
					strBuff.append("	ELSE ROUND( (CASE WHEN (A.DTVAL_CO - Z1.VAL2) < 0 THEN -1 ELSE 1 END) * ABS( (A.DTVAL_CO - Z1.VAL2) / Z1.VAL2  * 100 ), 8) END ANAL_CO  \n");
				}else{
					strBuff.append("	, CASE WHEN (Z1.VAL2 IS NULL) OR (Z1.VAL2  = 0) OR (A.DTVAL_CO_BUFF IS NULL) THEN NULL ELSE ROUND( (A.DTVAL_CO - Z1.VAL2) / ABS(Z1.VAL2) * 100, 8 ) END ANAL_CO  \n");
				}
			}
		}
		//증감기여도
		else if(analType.equals("CHG_RATE_CO")){
			//누계비인경우
			if(analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL")){
				strBuff.append("           ,CASE WHEN ( Z2.PRE_DIV_CNT IS NULL) OR ( Z2.PRE_DIV_CNT = 0 ) THEN NULL   \n");
				strBuff.append(" ELSE ROUND(   \n");
				strBuff.append("(ROUND(Z1.CUR_DIV_CNT,  \n");
				strBuff.append("    NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, A.PRD_DE), 8))  \n");
				strBuff.append("- ROUND(Z1.PRE_DIV_CNT,  \n");
				strBuff.append("    NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, A.PRD_DE), 8)))  \n");
				strBuff.append("/ ROUND(Z2.PRE_DIV_CNT,  \n");
				strBuff.append("NVL(" + dbUser + "GET_RECD_PRD_PERIOD10(A.ORG_ID, A.TBL_ID, A.PRD_SE, A.PRD_DE), 8) ) * 100    \n");
				strBuff.append("* (CASE WHEN  Z2.WGT_CO IS NULL OR Z2.WGT_CO = 0  THEN 1   \n");
				strBuff.append("ELSE (REPLACE(A.WGT_CO, ',', '') / Z2.WGT_CO ) END), 8) END ANAL_CO  \n");
			}else{
				strBuff.append("	, CASE WHEN (Z_CO.VAL2 IS NULL) OR (Z_CO.VAL2  = 0) THEN NULL ELSE ROUND((A.DTVAL_CO - Z1.VAL2) / Z_CO.VAL2  * 100 * (CASE WHEN  Z_CO.WGT_CO IS NULL OR Z_CO.WGT_CO = 0  THEN 1 ELSE (REPLACE(A.WGT_CO, ',', '') / Z_CO.WGT_CO ) END),8) END ANAL_CO  \n");
			}
		}
		//증감기여율
		else if(analType.equals("CHG_RATE_CO_R")){
			//누계비인경우
			if(analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL")){
				strBuff.append("	, CASE WHEN ( Z2.CUR_PRE IS NULL) OR ( Z2.CUR_PRE = 0 ) THEN NULL ELSE ROUND( Z1.CUR_PRE/Z2.CUR_PRE * 100, 8) END ANAL_CO    \n");
			}else{
				strBuff.append("	, CASE WHEN (  (Z_CUR.VAL2 - Z_CO.VAL2) IS NULL) OR ( (Z_CUR.VAL2 - Z_CO.VAL2)  = 0) THEN NULL ELSE ROUND((A.DTVAL_CO - Z1.VAL2) / (Z_CUR.VAL2 - Z_CO.VAL2)  * 100, 6) END ANAL_CO  \n");
			}
		}
		//구성비
		//2015.4.13 분석 구성비 기준선택안함의 경우 계산 방식 변경
        else if(analType.equals("CMP_RATE") && noselectType.equals("noSelect") ){
            strBuff.append("      , CASE WHEN Z1.VAL2 IS NULL OR Z1.VAL2 = 0 THEN NULL ELSE    \n");
            strBuff.append("      ROUND(A.DTVAL_CO / DECODE(Z2.VAL,0,NULL,Z2.VAL) * 100,8) END ANAL_CO \n");
		}else if(analType.equals("CMP_RATE")){
			strBuff.append("      , CASE WHEN Z1.VAL2 IS NULL OR Z1.VAL2 = 0 THEN NULL ELSE    \n");
			strBuff.append("      ROUND(A.DTVAL_CO / Z1.VAL2 * 100, 8) END ANAL_CO   \n");
			strBuff.append(" \n");
		}
		//누계
		else if(analType.equals("TOTL")){
			strBuff.append("	, Z1.SUM_VAL ANAL_CO    \n");
		}
		//누계구성비
		else if(analType.equals("TOTL_CMP_RATE") && !noselectType.equals("noSelect")){
			strBuff.append("   , CASE WHEN Z2.SUM_VAL2 IS NULL OR Z2.SUM_VAL2 = 0 THEN NULL ELSE  \n");
			strBuff.append("    ROUND( Z1.SUM_VAL / Z2.SUM_VAL2 * 100, 8) END ANAL_CO   \n");
			strBuff.append(" \n");
		}
		//누계구성비 기준선택 안함
		else if(analType.equals("TOTL_CMP_RATE") && noselectType.equals("noSelect")){
			strBuff.append("   , CASE WHEN Z2.SUM_VAL2 IS NULL OR Z2.SUM_VAL2 = 0 THEN NULL ELSE  \n");
			strBuff.append("    ROUND( Z1.SUM_VAL / Z2.SUM_VAL2 * 100, 8) END ANAL_CO   \n");
			strBuff.append(" \n");
		}
	}

	private void addFromAnal(){
		// 2015.4.13 분석 기준선택여부 체크
		String selectType = new String();
		String noselectType = new String();
		selectType = paramInfo.getNoSelect();
		noselectType = paramInfo.setNoSelect(selectType);

		// 2015.5.15 시점 수에 따라 from 절 추가
		String prd = statHtmlDAO.getTimeDimensionList(paramInfo);
		String[] prdArr = prd.split("@");

		// 2015.5.18 비공개 분류수 가져오기
		Map tmMap = new HashMap();
		tmMap.put("orgId", paramInfo.getOrgId());
		tmMap.put("tblId", paramInfo.getTblId());
		tmMap.put("dbUser", paramInfo.getDbUser());
		int closeList = statHtmlDAO.selectItmListPub(tmMap);

		// 누계, 누계구성비
		// 기존 소스에는 누계와, 누계구성비가 따로 있었으나 z1 절이 똑같아서 하나로 합치고 누계구성비인 경우 z2를 추가하도록 함.
		// 2015.4.28 주기별 누계값이 계산되게 하기 위하여 SUBSTR(COMP_PRD_DE, 1, 4) 제거
		// 2020.01.07 2015.4.28 작업한 누계값 계산이 잘못된거라하여 원복 - 김경호
		if (analType.equals("TOTL") || analType.equals("TOTL_CMP_RATE") && !noselectType.equals("noSelect") ) {
			strBuff.append("     , (  SELECT ITM_RCGN_SN, PRD_SE, COMP_PRD_DE, \n");
			strBuff.append("   SUM(VAL2) OVER(PARTITION BY ITM_RCGN_SN, PRD_SE, SUBSTR(COMP_PRD_DE, 1, 4) ORDER BY ITM_RCGN_SN, PRD_SE, COMP_PRD_DE ROWS UNBOUNDED PRECEDING) SUM_VAL  \n");
			strBuff.append("   FROM (  \n");
			strBuff.append(" SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */	\n");
			strBuff.append(" 			S1.ITM_RCGN_SN, S2.PRD_SE, PRD_DE, PRD_DE AS COMP_PRD_DE, DTVAL_CO	AS VAL2 \n");
			strBuff.append(" FROM		COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
			strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
			//더미 여부 체크
			//strBuff.append("        AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

			//2015.4.16 누계 계산방식 수정 - 선택된 시점에 대해서면 누계 계산 되도록
			// 2020.01.07 2015.4.16 작업한 누계값 계산이 잘못된거라하여 원복 (년단위로 누계가 되어야함) - 김경호
			//strBuff.append("        AND S2.PRD_DE " + prdWhere + " \n");

			//항목, 분류값 조건
			addWhereCompareDim();

			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSe();

			strBuff.append("                    ) ) Z1  \n");

			//누계구성비인 경우 추가
			if(analType.equals("TOTL_CMP_RATE")){
				strBuff.append(" ,  (  SELECT  PRD_SE, PRD_DE, CHAR_ITM_ID, OV_L1_ID, OV_L2_ID, OV_L3_ID, OV_L4_ID, OV_L5_ID, OV_L6_ID, OV_L7_ID, OV_L8_ID ");
				// 2015.4.28 주기별 누계값이 계산되게 하기 위하여 SUBSTR(PRD_DE, 1, 4) 제거
				// 2020.01.07 2015.4.28 작업한 누계값 계산이 잘못된거라하여 원복 - 김경호
				strBuff.append("   , SUM(VAL2) OVER(PARTITION BY PRD_SE, CHAR_ITM_ID, SUBSTR(PRD_DE, 1, 4), ov_l1_id, ov_l2_id, ov_l3_id, ov_l4_id, ov_l5_id, ov_l6_id, ov_l7_id, ov_l8_id  ");
				strBuff.append(" ORDER BY PRD_SE, PRD_DE, CHAR_ITM_ID, ov_l1_id, ov_l2_id, ov_l3_id, ov_l4_id, ov_l5_id, ov_l6_id, ov_l7_id, ov_l8_id");
				strBuff.append(" ROWS UNBOUNDED PRECEDING) SUM_VAL2  \n");
				strBuff.append(" FROM (  \n");

				strBuff.append("          SELECT   /*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */  \n");
				strBuff.append("                  PRD_SE,  \n");
				strBuff.append("                  PRD_DE,  \n");
				strBuff.append("                  DTVAL_CO VAL2,  \n");
				strBuff.append("                  S1.CHAR_ITM_ID, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id, S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id  \n");
				strBuff.append(" FROM	COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
				strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
				strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
				strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
				strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
				strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
				//더미 여부 체크
				//strBuff.append("        AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
				strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

				//2015.4.16 누계 계산방식 수정 - 선택된 시점에 대해서면 누계 계산 되도록
				// 2020.01.07 2015.4.16 작업한 누계값 계산이 잘못된거라하여 원복 (년단위로 누계가 되어야함) - 김경호
				//strBuff.append("        AND S2.PRD_DE " + prdWhere + " \n");

				//항목, 분류값 조건
				addWhereCompareDim();

				//분류 또는 항목 비교
				for(int i = 0; i < analClassList.size(); i++){
					strBuff.append("        AND S1.OV_L" + varOrdSnFuncList.get(i) + "_ID = '" + analItemList.get(i) + "' \n");
				}

				//TN_DIM, TN_DT 공표구분 체크
				addWherePubSe();

				strBuff.append("                     )  ) Z2  \n");
			}
		}
		// 2015.5.19 누계구성비 기준선택 안함 추가
		else if(analType.equals("TOTL_CMP_RATE") && noselectType.equals("noSelect")){
			strBuff.append("     , (  SELECT CHAR_ITM_ID, ITM_RCGN_SN, PRD_SE, COMP_PRD_DE, \n");
			//2015.06.23 상속통계표
			if("Y".equals(paramInfo.getInheritYn())){
				strBuff.append("           DECODE(PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + paramInfo.getOriginOrgId()  + "' AND TBL_ID = '" + paramInfo.getOriginTblId()  + "' and prd_se='F' ), PRD_SE)||COMP_PRD_DE AS T_PRD    \n");
			}else{
				strBuff.append("           DECODE(PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + orgId  + "' AND TBL_ID = '" + tblId  + "' and prd_se='F' ), PRD_SE)||COMP_PRD_DE AS T_PRD    \n");
			}
			
/*			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
				strBuff.append("        ,  OV_L" + tmpStr + "_ID  \n");
			}*/
			strBuff.append("   ,SUM(VAL2) OVER(PARTITION BY ITM_RCGN_SN, PRD_SE ORDER BY ITM_RCGN_SN, PRD_SE, COMP_PRD_DE ROWS UNBOUNDED PRECEDING) SUM_VAL  \n");
			strBuff.append("   FROM (  \n");
			strBuff.append(" SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */	\n");
			strBuff.append(" 			S1.CHAR_ITM_ID, S1.ITM_RCGN_SN, S2.PRD_SE, S2.PRD_DE AS COMP_PRD_DE, DTVAL_CO	AS VAL2 \n");
			// 분류를 수 만큼
/*			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
				strBuff.append("        ,S1.OV_L" + tmpStr + "_ID  \n");
			}*/
			strBuff.append(" FROM		COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
			strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

			//2015.4.16 누계 계산방식 수정 - 선택된 시점에 대해서면 누계 계산 되도록
			// 2020.01.07 2015.4.16 작업한 누계값 계산이 잘못된거라하여 원복 (년단위로 누계가 되어야함) - 김경호
			//strBuff.append("        AND S2.PRD_DE " + prdWhere + " \n");

			//항목, 분류값 조건
			addWhereCompareDim();

			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSe();

			strBuff.append("                    ) ) Z1,(  \n");
			strBuff.append("	SELECT CHAR_ITM_ID, T_PRD, SUM(SUM_VAL) SUM_VAL2 ");
			for(int i = 0; i < classListForWhereClause.size()-1; i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
				strBuff.append("        ,OV_L" + tmpStr + "_ID \n");
			}
			strBuff.append("	FROM ( SELECT CHAR_ITM_ID, ITM_RCGN_SN, PRD_SE, COMP_PRD_DE, \n");
			//2015.06.23 상속통계표
			if("Y".equals(paramInfo.getInheritYn())){
				strBuff.append("	DECODE(PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + paramInfo.getOriginOrgId()  + "' AND TBL_ID = '" + paramInfo.getOriginTblId()  + "' and prd_se='F' ), PRD_SE)||COMP_PRD_DE AS T_PRD,    \n");
			}else{
				strBuff.append("	DECODE(PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + orgId  + "' AND TBL_ID = '" + tblId  + "' and prd_se='F' ), PRD_SE)||COMP_PRD_DE AS T_PRD,    \n");
			}
			
			for(int i = 0; i < classListForWhereClause.size()-1; i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
				strBuff.append("	OV_L" + tmpStr + "_ID,  \n");
			}
			strBuff.append("	SUM(VAL2) OVER(PARTITION BY ITM_RCGN_SN, PRD_SE ORDER BY ITM_RCGN_SN, PRD_SE, COMP_PRD_DE ROWS UNBOUNDED PRECEDING) SUM_VAL FROM (  \n");
			strBuff.append("	SELECT S1.CHAR_ITM_ID, S1.ITM_RCGN_SN, S2.PRD_SE, PRD_DE, PRD_DE AS COMP_PRD_DE, DTVAL_CO AS VAL2 \n");
			for(int i = 0; i < classListForWhereClause.size()-1; i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
				strBuff.append("	,S1.OV_L" + tmpStr + "_ID  \n");
			}
			strBuff.append(" FROM	COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
			strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

			//2015.4.16 누계 계산방식 수정 - 선택된 시점에 대해서면 누계 계산 되도록
			// 2020.01.07 2015.4.16 작업한 누계값 계산이 잘못된거라하여 원복 (년단위로 누계가 되어야함) - 김경호
			//strBuff.append("        AND S2.PRD_DE " + prdWhere + " \n");

			//항목, 분류값 조건
			addWhereCompareDim();

			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSe();

			strBuff.append("		)) GROUP BY CHAR_ITM_ID, T_PRD \n");
			for(int i = 0; i < classListForWhereClause.size()-1; i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
				strBuff.append("	,OV_L" + tmpStr + "_ID  \n");
			}
			strBuff.append("	)Z2");

		}
		 // 구성비
		else if(analType.equals("CMP_RATE") && !noselectType.equals("noSelect")){
			strBuff.append("     , (  \n");
			strBuff.append(" SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */ \n");
			strBuff.append(" 			S1.ITM_RCGN_SN, PRD_SE, PRD_DE, PRD_DE AS COMP_PRD_DE, DTVAL_CO	AS VAL2, S1.char_itm_id, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id, S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id \n");
			strBuff.append(" FROM		COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
			strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
			//더미 여부 체크
			//strBuff.append("        AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");
			strBuff.append("        AND S2.PRD_SE||S2.PRD_DE BETWEEN '" + from + "' AND '" + to + "'  \n");

			addWhereCompareDim();

			//분류 또는 항목  비교
			// 2015.4.13 분석 구성비 기준선택 안함의 경우 변경된 구성비 계산방식이 적용되도록
			if(analItemStr != null){
				strBuff.append("        AND S1.CHAR_ITM_ID = '" + analItemStr + "' \n");
			}else{
				if(!noselectType.equals("noSelect")){ // 기준선택
					for(int i = 0; i < analClassList.size(); i++){
						strBuff.append("        AND S1.OV_L" + varOrdSnFuncList.get(i) + "_ID = '" + analItemList.get(i) + "' \n");
					}
				}
			}
			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSe();

			strBuff.append(" ) Z1  \n");
		}
		// 구성비 기준선택 안함
		else if(analType.equals("CMP_RATE") && noselectType.equals("noSelect")){
			strBuff.append("     , (  \n");
			strBuff.append(" SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */ \n");
			strBuff.append(" 			S1.ITM_RCGN_SN, PRD_SE, PRD_DE, PRD_DE AS COMP_PRD_DE, DTVAL_CO	AS VAL2, S1.char_itm_id, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id, S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id \n");
			strBuff.append(" FROM		COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
			strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
			//더미 여부 체크
			//strBuff.append("        AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

			strBuff.append("        AND S2.PRD_SE||S2.PRD_DE BETWEEN '" + from + "' AND '" + to + "'  \n");

			addWhereCompareDim();

			//분류 또는 항목  비교
			// 2015.4.13 분석 구성비 기준선택 안함의 경우 변경된 구성비 계산방식이 적용되도록
			if(analItemStr != null){
				strBuff.append("        AND S1.CHAR_ITM_ID = '" + analItemStr + "' \n");
			}else{
				if(noselectType.equals("noSelect") && classListForWhereClause.size() == 1){ // 기준선택 안함 + 분류가 한개인 경우
					for(int i = 0; i < analClassList.size(); i++){
						strBuff.append("        AND S1.OV_L" + varOrdSnFuncList.get(i) + "_ID = '" + analItemList.get(i) + "' \n");
					}
				}
			}
			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSe();

			strBuff.append(" ) Z1  \n");
			strBuff.append("     , (  \n");

			for(int x = 0; x < prdArr.length; x++){

				String[] tmpArr = prdArr[x].split(",");
				String period = tmpArr[0];

				if(tmpArr.length == 1){
					//2015.5.8 자료선택 안함 구성비 계산을 위해 Z2 추가
					strBuff.append(" SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */ \n");
					strBuff.append(" 			SUM(S2.DTVAL_CO) VAL, S1.char_itm_id \n");

					if(classListForWhereClause.size()==1){
						//strBuff.append(" 			, S1.ov_l1_id \n");
					}else{
						if(closeList == 0){
							for(int i = 0; i < classListForWhereClause.size()-1; i++){
								String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
								strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
							}
						}else{ // 비공개 분류가 존재 한다면 그 수 만큼 빼주기
							for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
								String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
								strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
							}
						}
					}
					strBuff.append(" FROM		COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
					strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
					strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
					strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
					strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
					strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
					strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

					strBuff.append("        AND S2.PRD_SE||S2.PRD_DE IN( '" + from + "' , '" + to + "' ) \n");

					addWhereCompareDim();

					//분류 또는 항목  비교
					// 2015.4.13 분석 구성비 기준선택 안함의 경우 변경된 구성비 계산방식이 적용되도록
					if(analItemStr != null){
						strBuff.append("        AND S1.CHAR_ITM_ID = '" + analItemStr + "' \n");
					}

					//TN_DIM, TN_DT 공표구분 체크
					addWherePubSe();
					strBuff.append(" 			GROUP BY S1.CHAR_ITM_ID  \n");
					if(classListForWhereClause.size()==1){
						//strBuff.append(" 			, S1.ov_l1_id \n");
					}else{
						if(closeList == 0){
							for(int i = 0; i < classListForWhereClause.size()-1; i++){
								String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
								strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
							}
						}else{
							for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
								String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
								strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
							}
						}
					}
					strBuff.append(" ) Z2  \n");
				}else{ // 시점이 한개 이상일 경우

					for(int j = 1 ; j < tmpArr.length; j++){

						strBuff.append(" SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */ \n");
						strBuff.append(" 			SUM(S2.DTVAL_CO) VAL, S1.char_itm_id, \n");

						//2015.06.23 상속통계표
						if("Y".equals(paramInfo.getInheritYn())){
							strBuff.append("       	DECODE(S2.PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + paramInfo.getOriginOrgId()  + "' AND TBL_ID = '" + paramInfo.getOriginTblId()  + "' and prd_se='F' ), S2.PRD_SE)||S2.PRD_DE AS T_PRD	\n");
						}else{
							strBuff.append("       	DECODE(S2.PRD_SE, 'F', (SELECT NVL(PRD_DETAIL, 'IR') FROM " + dbUser + "TN_STBL_RECD_INFO WHERE ORG_ID = '" + orgId  + "' AND TBL_ID = '" + tblId  + "' and prd_se='F' ), S2.PRD_SE)||S2.PRD_DE AS T_PRD	\n");
						}
						
						
						if(classListForWhereClause.size()==1){
							//strBuff.append(" 			, S1.ov_l1_id \n");
						}else{
							if(closeList == 0){
								for(int i = 0; i < classListForWhereClause.size()-1; i++){
									String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
									strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
								}
							}else{
								for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
									String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
									strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
								}
							}
						}
						strBuff.append(" FROM		COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2	\n");
						strBuff.append(" WHERE  S1.ORG_ID = S2.ORG_ID	\n");
						strBuff.append("        AND S1.TBL_ID = S2.TBL_ID	\n");
						strBuff.append("        AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
						strBuff.append("        AND S1.ORG_ID = '" + orgId + "'	\n");
						strBuff.append("        AND S1.TBL_ID = '" + tblId + "'	\n");
						strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

						strBuff.append("        AND S2.PRD_SE||S2.PRD_DE ='" + period+tmpArr[j] + "' \n");

						addWhereCompareDim();

						//분류 또는 항목  비교
						// 2015.4.13 분석 구성비 기준선택 안함의 경우 변경된 구성비 계산방식이 적용되도록
						if(analItemStr != null){
							strBuff.append("        AND S1.CHAR_ITM_ID = '" + analItemStr + "' \n");
						}

						//TN_DIM, TN_DT 공표구분 체크
						addWherePubSe();
						strBuff.append(" 			GROUP BY S1.CHAR_ITM_ID, S2.PRD_SE,S2.PRD_DE  \n");
						if(classListForWhereClause.size()==1){
							//strBuff.append(" 			, S1.ov_l1_id \n");
						}else{
							if(closeList == 0){
								for(int i = 0; i < classListForWhereClause.size()-1; i++){
									String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
									strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
								}
							}else{
								for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
									String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
									strBuff.append("        ,  s1.OV_L" + tmpStr + "_ID  \n");
								}
							}
						}
						if(j+1 == tmpArr.length){
							//strBuff.append(" union all  \n");
						}else{
							strBuff.append(" union all  \n");
						}
					}
					strBuff.append(" ) Z2  \n");
				}
			}
		}
		//전년누계비
		else if (analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL") ) {
			strBuff.append("     ,  (  SELECT ITM_RCGN_SN, PRD_SE, COMP_PRD_DE, WGT_CO, \n");
			strBuff.append("   SUM(cur_val - pre_val) over(partition BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4) ORDER BY itm_rcgn_sn, prd_se, comp_prd_de ROWS UNBOUNDED PRECEDING) cur_pre,  \n");
			strBuff.append(" SUM(pre_val) OVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4) ORDER BY itm_rcgn_sn, prd_se, comp_prd_de ROWS UNBOUNDED PRECEDING) \n");
			strBuff.append("     / SUM(prd_cnt) OVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4)  ORDER BY itm_rcgn_sn, prd_se, comp_prd_de ROWS UNBOUNDED PRECEDING) pre_div_cnt, \n");
			strBuff.append(" SUM(cur_val) oVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4)  ORDER BY itm_rcgn_sn, prd_se, comp_prd_de ROWS UNBOUNDED PRECEDING) \n");
			strBuff.append("  / count(prd_cnt) OVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4) ORDER BY itm_rcgn_sn, prd_se, comp_prd_de ROWS UNBOUNDED PRECEDING) cur_div_cnt \n");
			strBuff.append("   from (  \n");

			strBuff.append("   		select * from ( ");
			strBuff.append("				SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */  \n");
			strBuff.append(" 						S1.ITM_RCGN_SN, S2.PRD_SE \n");
			strBuff.append(" 						,S1.CHAR_ITM_ID , S1.OV_L1_ID , S1.OV_L2_ID , S1.OV_L3_ID ,  S1.OV_L4_ID ,  S1.OV_L5_ID ,  S1.OV_L6_ID ,  S1.OV_L7_ID , S1.OV_L8_ID \n");
			strBuff.append(" 					 , S1.WGT_CO,  \n");
			strBuff.append("                     count(distinct S2.prd_se||S2.prd_de)/2 prd_cnt, min(nvl(R.period_co,0)) period_co,   \n");
			strBuff.append("                     styear||substr(S2.prd_de,5,2) comp_prd_de,     \n");
			strBuff.append("                     SUM (CASE WHEN S2.prd_de LIKE styear-1||'%' THEN S2.dtval_co ELSE 0 END)  \n");
			strBuff.append("                        pre_val,  \n");
			strBuff.append("                     SUM (CASE WHEN S2.prd_de LIKE styear||'%' THEN S2.dtval_co ELSE 0 END)  \n");
			strBuff.append("                        cur_val  \n");
			strBuff.append(" 				FROM COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2, " + dbUser + "TN_RECD_PRD R, \n");
			strBuff.append("                  ( select substr(prd_de,1,4) styear, prd_de st_prdde from " + dbUser + "tc_prd  \n");
			strBuff.append("                    where prd_se='Y' and prd_de between substr('" + from + "',2,4) and substr('" + to + "',2,4)  ) S   \n");
			strBuff.append(" 				WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        				AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        				AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        				AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        				AND S1.TBL_ID = '" + tblId + "'	\n");
			//더미 여부 체크
			//strBuff.append("       					AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

			strBuff.append("       		 			AND R.prd_se || R.prd_de BETWEEN S2.prd_se ||to_char(to_number(substr('" + from.substring(1,5) + "',1,4))-1)||'01' AND '" + to + "'  \n");

			//항목, 분류값 조건
			addWhereCompareDim();

			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSeIncludeRecdPrd();

			strBuff.append("        				AND S1.ORG_ID = R.ORG_ID	\n");
			strBuff.append("        				AND S1.TBL_ID = R.TBL_ID	\n");
			strBuff.append("        				AND S2.PRD_SE = R.PRD_SE	\n");
			strBuff.append("        				AND S2.PRD_DE = R.PRD_DE	\n");
			strBuff.append("        				AND R.prd_de between styear-1||'01' and styear||substr(R.prd_de,5,2)	\n");
			strBuff.append(" 	GROUP BY ");
			strBuff.append(" 	S1.ITM_RCGN_SN, S2.PRD_SE, S1.CHAR_ITM_ID , S1.OV_L1_ID , S1.OV_L2_ID , S1.OV_L3_ID ,  S1.OV_L4_ID ,  S1.OV_L5_ID ,  S1.OV_L6_ID ,  S1.OV_L7_ID , S1.OV_L8_ID ");
			strBuff.append(" , S1.WGT_CO, styear||substr(S2.prd_de,5,2) ) \n");
			strBuff.append("            ) )  Z1 \n");

			//기여도, 기여율
			if(analType.equals("CHG_RATE_CO") || analType.equals("CHG_RATE_CO_R")){
				strBuff.append("     ,  (  select  itm_rcgn_sn, prd_se, comp_prd_de, char_itm_id, wgt_co, ov_l1_id, ov_l2_id, ov_l3_id, ov_l4_id, ov_l5_id, ov_l6_id, ov_l7_id, ov_l8_id, \n");
				strBuff.append("   SUM(cur_val - pre_val) over(partition BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4), char_itm_id ORDER BY itm_rcgn_sn, prd_se, comp_prd_de, char_itm_id ROWS UNBOUNDED PRECEDING) cur_pre,  \n");
				strBuff.append(" SUM(pre_val) OVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4), char_itm_id ORDER BY itm_rcgn_sn, prd_se, comp_prd_de, char_itm_id ROWS UNBOUNDED PRECEDING) \n");
				strBuff.append("     / SUM(prd_cnt) OVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4), char_itm_id  ORDER BY itm_rcgn_sn, prd_se, comp_prd_de, char_itm_id ROWS UNBOUNDED PRECEDING) pre_div_cnt, \n");
				strBuff.append(" SUM(cur_val) oVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4), char_itm_id  ORDER BY itm_rcgn_sn, prd_se, comp_prd_de, char_itm_id ROWS UNBOUNDED PRECEDING) \n");
				strBuff.append("  / count(prd_cnt) OVER(PARTITION BY itm_rcgn_sn, prd_se, substr(comp_prd_de, 1, 4), char_itm_id ORDER BY itm_rcgn_sn, prd_se, comp_prd_de, char_itm_id ROWS UNBOUNDED PRECEDING) cur_div_cnt \n");
				strBuff.append("   from (  \n");
				strBuff.append("   		select * from ( ");
				strBuff.append("				SELECT	/*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */  \n");
				strBuff.append(" 						S1.ITM_RCGN_SN, S2.PRD_SE \n");
				strBuff.append("                     , S1.char_itm_id, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id, S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id  \n");
				strBuff.append(" 					 , S1.WGT_CO,  \n");
				strBuff.append("                     count(distinct S2.prd_se||S2.prd_de)/2 prd_cnt, min(nvl(R.period_co,0)) period_co,   \n");
				strBuff.append("                     styear||substr(S2.prd_de,5,2) comp_prd_de,     \n");
				strBuff.append("                     SUM (CASE WHEN S2.prd_de LIKE styear-1||'%' THEN S2.dtval_co ELSE 0 END)  \n");
				strBuff.append("                        pre_val,  \n");
				strBuff.append("                     SUM (CASE WHEN S2.prd_de LIKE styear||'%' THEN S2.dtval_co ELSE 0 END)  \n");
				strBuff.append("                        cur_val  \n");
				strBuff.append(" 				FROM COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2, " + dbUser + "TN_RECD_PRD R, \n");
				strBuff.append("                  ( select substr(prd_de,1,4) styear, prd_de st_prdde from " + dbUser + "tc_prd  \n");
				strBuff.append("                    where prd_se='Y' and prd_de between substr('" + from + "',2,4) and substr('" + to + "',2,4)  ) S   \n");
				strBuff.append(" 				WHERE  S1.ORG_ID = S2.ORG_ID	\n");
				strBuff.append("        				AND S1.TBL_ID = S2.TBL_ID	\n");
				strBuff.append("        				AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
				strBuff.append("        				AND S1.ORG_ID = '" + orgId + "'	\n");
				strBuff.append("        				AND S1.TBL_ID = '" + tblId + "'	\n");
				//더미 여부 체크
				//strBuff.append("        				AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
				strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

				strBuff.append("       		 			AND S2.prd_se || S2.prd_de BETWEEN S2.prd_se ||to_char(to_number(substr('" + from.substring(1,5) + "',1,4))-1)||'01' AND '" + to + "'  \n");

				//항목, 분류값 조건
				addWhereCompareDim();

				//TN_DIM, TN_DT 공표구분 체크
				addWherePubSeIncludeRecdPrd();

				strBuff.append("        				AND S1.ORG_ID = R.ORG_ID	\n");
				strBuff.append("        				AND S1.TBL_ID = R.TBL_ID	\n");
				strBuff.append("        				AND S2.PRD_SE = R.PRD_SE	\n");
				strBuff.append("        				AND S2.PRD_DE = R.PRD_DE	\n");
				strBuff.append("                        AND r.prd_se || r.prd_de BETWEEN S2.prd_se || to_char(to_number(substr('" + from.substring(1,5) + "',1,4))-1)||'01' AND '" + to + "'  \n");
				strBuff.append("        				AND R.prd_de between styear-1||'01' and styear||substr(R.prd_de,5,2)	\n");

				//분류비교
				for(int i = 0; i < analClassList.size(); i++){
					strBuff.append("        AND S1.OV_L" + varOrdSnFuncList.get(i) + "_ID = '" + analItemList.get(i) + "' \n");
				}

				strBuff.append(" 	GROUP BY ");
				strBuff.append(" 	S1.ITM_RCGN_SN, S2.PRD_SE, S1.CHAR_ITM_ID, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id, S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id ");
				strBuff.append(" , S1.WGT_CO, styear||substr(S2.prd_de,5,2) ) \n");
				strBuff.append("            ) )  Z2 \n");

				//F 테이블 추가
				strBuff.append("    ,(SELECT P.*, 1 GUBUN \n");
				strBuff.append("    FROM ( SELECT /*+ leading(rr) use_hash(rr qq) index(qq UQTN_DIM) */ \n");
				strBuff.append("             qq.*, rr.prd_se, rr.prd_de   \n");
				strBuff.append("                                from " + dbUser + "tn_dim qq, " + dbUser + "tn_recd_prd rr  \n");
				strBuff.append("                                where  qq.org_id = '" + orgId + "'  \n");
				strBuff.append("                               and qq.tbl_id = '" + tblId + "'  \n");

				//공표구분
				addWherePubSeForAliasF();

				strBuff.append("                               and rr.org_id = '" + orgId + "'  \n");
				strBuff.append("                               and rr.tbl_id = '" + tblId + "'  \n");
				strBuff.append("  and rr.prd_se='" + prdSe + "' and rr.prd_de between '" + from.substring(1) + "' and '" + to.substring(1) + "' \n");
				strBuff.append("                                     and qq.org_id = rr.org_id  \n");
				strBuff.append("                                     and qq.tbl_id = rr.tbl_id   \n");
				strBuff.append("  ) P ) F  \n");
			}
		}
		else{
			strBuff.append("       , (  \n");
			strBuff.append("                    SELECT /*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */  \n");
			strBuff.append("                         S2.itm_rcgn_sn, S2.prd_se, S2.prd_de,  \n");

			// 전월비, 전분기비, 전반기비, 전년비
			if (analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_H")) {
				strBuff.append("      case when S2.prd_se='M' then to_char(ADD_MONTHS(to_date(S2.prd_de,'YYYYMM'),1), 'YYYYMM')    \n");
				strBuff.append("           when S2.prd_se='Q' then (case when S2.prd_de like '%04' then substr(S2.prd_de,1,4)+1||'01' else S2.prd_de+1||'' end)    \n");
				strBuff.append("           when S2.prd_se='H' then (case when S2.prd_de like '%02' then substr(S2.prd_de,1,4)+1||'01' else S2.prd_de+1||'' end)    \n");
				strBuff.append("           when S2.prd_se='Y' then S2.prd_de+1||''   \n");
				strBuff.append("      end comp_prd_de,   \n");
			}
			// 전년말월비, 전년말분기비, 전년비
			else if (analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE") || analCmpr.equals("PREV_Y_HE") || analCmpr.equals("PREV_Y")) {
				strBuff.append("      case when S2.prd_se='M' then  substr(S2.prd_de,1,4)+1||'12'    \n");
				strBuff.append("           when S2.prd_se='Q' then  substr(S2.prd_de,1,4)+1||'04'    \n");
				strBuff.append("           when S2.prd_se='H' then  substr(S2.prd_de,1,4)+1||'02'    \n");
				strBuff.append("           when S2.prd_se='Y' then S2.prd_de+1||''   \n");
				strBuff.append("      end comp_prd_de,   \n");
			}
			// 전년동월비, 전년동분기비, 전년비
			else if (analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_Y_HS") || analCmpr.equals("PREV_Y")) {
				strBuff.append("      case when S2.prd_se='M' then  substr(S2.prd_de,1,4)+1||substr(S2.prd_de,5,2)   \n");
				strBuff.append("           when S2.prd_se='Q' then  substr(S2.prd_de,1,4)+1||substr(S2.prd_de,5,2)    \n");
				strBuff.append("           when S2.prd_se='H' then  substr(S2.prd_de,1,4)+1||substr(S2.prd_de,5,2)    \n");
				strBuff.append("           when S2.prd_se='Y' then S2.prd_de+1||''   \n");
				strBuff.append("      end comp_prd_de,   \n");
			}
			else if (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F")) {
				strBuff.append("      S2.prd_de comp_prd_de,   \n");
			}
			else if (analCmpr.equals("PREV_F")) {
				strBuff.append("   r.prd_de comp_prd_de,    \n");
			}
			else {
				strBuff.append("      '' comp_prd_de,    \n");
			}

			strBuff.append("      S2.dtval_co val2  \n");
			strBuff.append(" 				FROM COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2 \n");

			if (analCmpr.equals("PREV_F")) {
				strBuff.append("              ,(  \n");
				strBuff.append("                          select b.rn, a.prd_se, a.prd_de, c.prd_de prd_de_before   \n");
				strBuff.append("                          from " + dbUser + "tn_recd_prd a, prd_de_f b, prd_de_f c  \n");
				strBuff.append("                          where a.org_id='" + orgId + "' and a.tbl_id='" + tblId + "'  \n");
				strBuff.append("                                 and a.prd_se='F'   \n");
				strBuff.append("                                 and a.prd_se||a.prd_de between '" + from + "' and '" + to + "'  \n");
				strBuff.append("                                 and a.prd_se = b.prd_se  \n");
				strBuff.append("                                 and a.prd_de = b.prd_de  \n");
				strBuff.append("                                 and a.prd_se = b.prd_se  \n");
				strBuff.append("                                 and c.rn = b.rn+1 ) R  \n");
			}else{ //2017.12.19 시점 공표가 담당자인데 셀정보 공표가 미지정이라 전년비 수치가 조회되는 현상 방지 - 박혜진 주무관
				strBuff.append(" , " + dbUser + "TN_RECD_PRD R \n");
			}

			strBuff.append(" 				WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        				AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        				AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        				AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        				AND S1.TBL_ID = '" + tblId + "'	\n");

			//항목, 분류값 조건
			addWhereCompareDim();

			//TN_DIM, TN_DT 공표구분 체크
			//addWherePubSe();
			//2017.12.19 addWherePubSe() 가 기본이였기에 PREV_F는 그냥 기본거 쓰고 TN_RECD_PRD 테이블 추가한 나머지는 R에대한 공표를 추가해야 되기에 분기를 나눔
			if (analCmpr.equals("PREV_F")) {
				addWherePubSe();
			}else{
				addWherePubSeIncludeRecdPrd();
			}			

			// 전월비, 전분기비, 전반기비, 전년비
			// 전년동월비, 전년동분기비, 전년동반기비, 전년비
			if (analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_H") || analCmpr.equals("PREV_F")
					|| analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_Y_HS") || analCmpr.equals("PREV_Y")) {
				strBuff.append("                   AND S2.prd_se||S2.prd_de between '" + prdSe + fromFunc +"' and '" + prdSe + toFunc + "'  \n");
				//2017.12.19 TN_RECD_PRD 테이블 추가하면서 아래 추가
				strBuff.append("                   AND R.prd_se||R.prd_de between '" + prdSe + fromFunc +"' and '" + prdSe + toFunc + "'  \n");
			}
			// 전년말월비, 전년말분기비, 전년말반기비, 전년비
			else if (analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE")  || analCmpr.equals("PREV_Y_HE") || analCmpr.equals("PREV_Y")) {
				int prePrdTime   = 0;
				int nextPrdTime  = 0;
				String strTmpPrd = "";
				if (prdSe.equals("M")) {
					prePrdTime  = Integer.parseInt(fromFunc.substring(0,4));
					nextPrdTime = Integer.parseInt(toFunc.substring(0,4));
					for(int year = prePrdTime ; year <= nextPrdTime ; year++)
					{
						strTmpPrd += "'M"+year+"12'";
						if(year != nextPrdTime)
						{
							strTmpPrd += ", ";
						}
					}

					strBuff.append("               AND S2.prd_se||S2.prd_de in (" + strTmpPrd + ")  \n");
					//2017.12.19 TN_RECD_PRD 테이블 추가하면서 아래 추가
					strBuff.append("               AND R.prd_se||R.prd_de in (" + strTmpPrd + ")  \n");
				}else if (prdSe.equals("Q")) {
					prePrdTime  = Integer.parseInt(fromFunc.substring(0,4));
					nextPrdTime = Integer.parseInt(toFunc.substring(0,4));
					for(int year = prePrdTime ; year <= nextPrdTime ; year++)
					{
						strTmpPrd += "'H"+year+"02'";
						if(year != nextPrdTime)
						{
							strTmpPrd += ", ";
						}
					}

					strBuff.append("                AND S2.prd_se||S2.prd_de in (" + strTmpPrd + ")  \n");
					//2017.12.19 TN_RECD_PRD 테이블 추가하면서 아래 추가
					strBuff.append("                AND R.prd_se||R.prd_de in (" + strTmpPrd + ")  \n");
				}else if (prdSe.equals("H")) {
					prePrdTime  = Integer.parseInt(fromFunc.substring(0,4));
					nextPrdTime = Integer.parseInt(toFunc.substring(0,4));
					for(int year = prePrdTime ; year <= nextPrdTime ; year++)
					{
						strTmpPrd += "'Q"+year+"04'";
						if(year != nextPrdTime)
						{
							strTmpPrd += ", ";
						}
					}

					strBuff.append("                AND S2.prd_se||S2.prd_de in (" + strTmpPrd + ")  \n");
					//2017.12.19 TN_RECD_PRD 테이블 추가하면서 아래 추가
					strBuff.append("                AND R.prd_se||R.prd_de in (" + strTmpPrd + ")  \n");
				}else if (prdSe.equals("Y")) {
					strBuff.append("                AND S2.prd_se||S2.prd_de between 'Y" + fromFunc +"' and 'Y" + toFunc + "'  \n");
					//2017.12.19 TN_RECD_PRD 테이블 추가하면서 아래 추가
					strBuff.append("                AND R.prd_se||R.prd_de between 'Y" + fromFunc +"' and 'Y" + toFunc + "'  \n");
				}
			}
			else if (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F")) {
				strBuff.append("    			AND S2.prd_se||S2.prd_de = S2.prd_se||'" + fromFunc + "'   \n");
				//2017.12.19 TN_RECD_PRD 테이블 추가하면서 아래 추가
				strBuff.append("    			AND R.prd_se||R.prd_de = R.prd_se||'" + fromFunc + "'   \n");
			}

			if (analCmpr.equals("PREV_F")) {
				strBuff.append("              and R.prd_se = S2.prd_se  \n");
				strBuff.append("              and R.prd_de_before = S2.prd_de  \n");
			}else{ //2017.12.19 PREV_F() 가 아닐때의 분기가 없었는데 TN_RECD_PRD대한 조건때문에 분기를 추가함
				strBuff.append("              AND R.ORG_ID = '" + orgId + "'	\n");
				strBuff.append("              AND R.TBL_ID = '" + tblId + "'	\n");
				strBuff.append("              AND R.PRD_SE = S2.PRD_SE  \n");
				strBuff.append("              AND R.PRD_DE = S2.PRD_DE  \n");
			}

			strBuff.append("                     )  Z1 \n");
		}

		//기여도 쿼리 start
		if ((!analCmpr.equals("PREV_Y_MTOTL") && !analCmpr.equals("PREV_Y_QTOTL") && !analCmpr.equals("PREV_Y_HTOTL") ) && (analType.equals("CHG_RATE_CO") || analType.equals("CHG_RATE_CO_R") ) ) {
			strBuff.append(" ,  (  \n");
			strBuff.append("                    SELECT /*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */  \n");
			strBuff.append("                         S2.itm_rcgn_sn, S2.prd_se, S2.prd_de, S1.char_itm_id, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id,S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id,    \n");

			// 전월비, 전분기비, 전반기비, 전년비
			if (analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_H")) {
				strBuff.append("      case when S2.prd_se='M' then to_char(ADD_MONTHS(to_date(S2.prd_de,'YYYYMM'),1), 'YYYYMM')    \n");
				strBuff.append("           when S2.prd_se='Q' then (case when S2.prd_de like '%04' then substr(S2.prd_de,1,4)+1||'01' else S2.prd_de+1||'' end)    \n");
				strBuff.append("           when S2.prd_se='H' then (case when S2.prd_de like '%02' then substr(S2.prd_de,1,4)+1||'01' else S2.prd_de+1||'' end)    \n");
				strBuff.append("           when S2.prd_se='Y' then S2.prd_de+1||''   \n");
				strBuff.append("      end comp_prd_de,   \n");
			}
			// 전년말월비, 전년말분기비, 전년비
			else if (analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE") || analCmpr.equals("PREV_Y_HE") || analCmpr.equals("PREV_Y")) {
				strBuff.append("      case when S2.prd_se='M' then  substr(S2.prd_de,1,4)+1||'12'    \n");
				strBuff.append("           when S2.prd_se='Q' then  substr(S2.prd_de,1,4)+1||'04'    \n");
				strBuff.append("           when S2.prd_se='H' then  substr(S2.prd_de,1,4)+1||'02'    \n");
				strBuff.append("           when S2.prd_se='Y' then S2.prd_de+1||''   \n");
				strBuff.append("      end comp_prd_de,   \n");
			}
			// 전년동월비, 전년동분기비, 전년비
			else if (analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_Y_HS") || analCmpr.equals("PREV_Y")) {
				strBuff.append("      case when S2.prd_se='M' then  substr(S2.prd_de,1,4)+1||substr(S2.prd_de,5,2)   \n");
				strBuff.append("           when S2.prd_se='Q' then  substr(S2.prd_de,1,4)+1||substr(S2.prd_de,5,2)    \n");
				strBuff.append("           when S2.prd_se='H' then  substr(S2.prd_de,1,4)+1||substr(S2.prd_de,5,2)    \n");
				strBuff.append("           when S2.prd_se='Y' then S2.prd_de+1||''   \n");
				strBuff.append("      end comp_prd_de,   \n");
			}
			else if (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F")) {
				strBuff.append("      S2.prd_de comp_prd_de,   \n");
			}
			else if (analCmpr.equals("PREV_F")) {
				strBuff.append("   r.prd_de comp_prd_de,    \n");
			}
			else {
				strBuff.append("      '' comp_prd_de,    \n");
			}

			strBuff.append("      S2.dtval_co val2, S1.wgt_co  \n");
			strBuff.append(" 				FROM COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2 \n");

			if (analCmpr.equals("PREV_F")) {
				strBuff.append("              ,(  \n");
				strBuff.append("                          select b.rn, a.prd_se, a.prd_de, c.prd_de prd_de_before   \n");
				strBuff.append("                          from " + dbUser + "tn_recd_prd a, prd_de_f b, prd_de_f c  \n");
				strBuff.append("                          where a.org_id='" + orgId + "' and a.tbl_id='" + tblId + "'  \n");
				strBuff.append("                                 and a.prd_se='F'   \n");
				strBuff.append("                                 and a.prd_se||a.prd_de between '" + from + "' and '" + to + "'  \n");
				strBuff.append("                                 and a.prd_se = b.prd_se  \n");
				strBuff.append("                                 and a.prd_de = b.prd_de  \n");
				strBuff.append("                                 and a.prd_se = b.prd_se  \n");
				strBuff.append("                                 and c.rn = b.rn+1 ) R  \n");
			}

			strBuff.append(" 				WHERE  S1.ORG_ID = S2.ORG_ID	\n");
			strBuff.append("        				AND S1.TBL_ID = S2.TBL_ID	\n");
			strBuff.append("        				AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
			strBuff.append("        				AND S1.ORG_ID = '" + orgId + "'	\n");
			strBuff.append("        				AND S1.TBL_ID = '" + tblId + "'	\n");
			//더미 여부 체크
			//strBuff.append("        				AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
			strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

			//항목, 분류값 조건
			addWhereCompareDim();

			//TN_DIM, TN_DT 공표구분 체크
			addWherePubSe();

			// 전월비, 전분기비, 전반기비, 전년비
			// 전년동월비, 전년동분기비, 전년동반기비, 전년비
			if (analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_H") || analCmpr.equals("PREV_F")
					|| analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_Y_HS") || analCmpr.equals("PREV_Y")) {
				strBuff.append("                   AND S2.prd_se||S2.prd_de between '" + prdSe + fromFunc +"' and '" + prdSe + toFunc + "'  \n");
			}
			// 전년말월비, 전년말분기비, 전년말반기비, 전년비
			else if(analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE") || analCmpr.equals("PREV_Y_HE") || analCmpr.equals("PREV_Y")){
				strBuff.append("                   AND S2.prd_se||S2.prd_de in ('" + prdSe + fromFunc +"' , '" + prdSe + toFunc + "')  \n");
			}
			else if(analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F")){
				strBuff.append("                   AND S2.prd_se||S2.prd_de = S2.prd_se||'" + fromFunc + "'   \n");
			}

			if (analCmpr.equals("PREV_F")) {
				strBuff.append("              and R.prd_se = S2.prd_se  \n");
				strBuff.append("              and R.prd_de_before = S2.prd_de  \n");
			}

			//기존 소스 주석으로 남겨둔다.
			//strBuff.append("                        and (q.ov_l1_id='" + FUNC_CO_CODE + "' or q.ov_l2_id='" + FUNC_CO_CODE + "' or q.ov_l3_id='" + FUNC_CO_CODE + "' or q.ov_l4_id='" + FUNC_CO_CODE + "' or q.ov_l5_id='" + FUNC_CO_CODE + "' or q.ov_l6_id='" + FUNC_CO_CODE + "' or q.ov_l7_id='" + FUNC_CO_CODE + "' or q.ov_l8_id='" + FUNC_CO_CODE + "')   "+ chg_rate_where_tab + "  \n");
			//다음과 같이 처리

			//분류 적용 - 기여도(율)는 하나의 분류값만 존재하므로..loop 처리 해도 무방함
			for(int i = 0; i < analClassList.size(); i++){
				strBuff.append("        AND S1.OV_L" + varOrdSnFuncList.get(i) + "_ID = '" + analItemList.get(i) + "'\n");
			}

			strBuff.append("                     ) Z_CO  \n");

			//기여율 쿼리 start
			if(analType.equals("CHG_RATE_CO_R")){
				strBuff.append("  , (  \n");
				strBuff.append("                    SELECT /*+ ordered use_nl(S3 S1) index(S1 UQTN_DIM) index(S2 PKTN_DT) */  \n");
				strBuff.append("                         S2.itm_rcgn_sn, S2.prd_se, S2.prd_de, S1.char_itm_id, S1.ov_l1_id, S1.ov_l2_id, S1.ov_l3_id, S1.ov_l4_id, S1.ov_l5_id, S1.ov_l6_id, S1.ov_l7_id, S1.ov_l8_id,  S2.prd_de comp_prd_de,  \n");
				strBuff.append("      S2.dtval_co val2  \n");
				strBuff.append(" 				FROM COND S3, " + dbUser + "TN_DIM S1, " + dbUser + "TN_DT S2 \n");

				strBuff.append(" 				WHERE  S1.ORG_ID = S2.ORG_ID	\n");
				strBuff.append("        				AND S1.TBL_ID = S2.TBL_ID	\n");
				strBuff.append("        				AND S1.ITM_RCGN_SN = S2.ITM_RCGN_SN	\n");
				strBuff.append("        				AND S1.ORG_ID = '" + orgId + "'	\n");
				strBuff.append("        				AND S1.TBL_ID = '" + tblId + "'	\n");
				//더미 여부 체크
				//strBuff.append("        				AND NVL(S1.DMM_AT, 'N') != 'Y'	\n");
				strBuff.append("        AND (S2.DTVAL_CO IS NOT NULL OR S2.DTVAL_CN IS NOT NULL OR S2.SMBL_CN IS NOT NULL) \n");

				//항목, 분류값 조건
				addWhereCompareDim();

				//TN_DIM, TN_DT 공표구분 체크
				addWherePubSe();

				//분류 적용- 기여도(율)는  하나의 분류값만 존재하므로..loop 처리 해도 무방함
				for(int i = 0; i < analClassList.size(); i++){
					strBuff.append("        AND S1.OV_L" + varOrdSnFuncList.get(i) + "_ID = '" + analItemList.get(i) + "' \n");
				}

				//기존 소스 주석으로 남겨둔다..
				//strBuff.append("                        and (q.ov_l1_id='" + FUNC_CO_CODE + "' or q.ov_l2_id='" + FUNC_CO_CODE + "' or q.ov_l3_id='" + FUNC_CO_CODE + "' or q.ov_l4_id='" + FUNC_CO_CODE + "' or q.ov_l5_id='" + FUNC_CO_CODE + "' or q.ov_l6_id='" + FUNC_CO_CODE + "' or q.ov_l7_id='" + FUNC_CO_CODE + "' or q.ov_l8_id='" + FUNC_CO_CODE + "')   "+ chg_rate_where_tab + "  \n");

				strBuff.append("                     ) Z_CUR  \n");
			}
		}// end of 기여도쿼리

	}

	private void addWhereAnal(){
		// 2015.4.13 분석 기준선택여부 체크
		String selectType = new String();
		String noselectType = new String();
		selectType = paramInfo.getNoSelect();
		noselectType = paramInfo.setNoSelect(selectType);

		// 2015.5.18 비공개 분류수 가져오기
		Map tmMap = new HashMap();
		tmMap.put("orgId", paramInfo.getOrgId());
		tmMap.put("tblId", paramInfo.getTblId());
		tmMap.put("dbUser", paramInfo.getDbUser());
		int closeList = statHtmlDAO.selectItmListPub(tmMap);

		//증감
		if (analType.equals("CHG") && (analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_H") || analCmpr.equals("PREV_Y_HS")|| analCmpr.equals("PREV_F") )) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
		} else if (analType.equals("CHG") && (analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_HE") || analCmpr.equals("PREV_Y_QE") )) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and substr(A.prd_de,1,4) =  substr(z1.comp_prd_de(+),1,4)  \n");
		} else if (analType.equals("CHG") && (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F")) ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
		}

		//증감율
		if (analType.equals("CHG_RATE") && ( analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_Y_QS") ||  analCmpr.equals("PREV_H") || analCmpr.equals("PREV_Y_HS") || analCmpr.equals("PREV_F") )) {
			if (fnExcptCd.equals("CONSTRUCT")) {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
				strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			} else {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
				strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			}
		}
		else if (analType.equals("CHG_RATE") && ( analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE") || analCmpr.equals("PREV_Y_HE") )) {
			if (fnExcptCd.equals("CONSTRUCT")) {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
				strBuff.append("      and substr(A.prd_de,1,4) =  substr(z1.comp_prd_de(+),1,4)  \n");
			} else {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
				strBuff.append("      and substr(A.prd_de,1,4) =  substr(z1.comp_prd_de(+),1,4)  \n");
			}
		}
		else if (analType.equals("CHG_RATE") && (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F"))) {
			if (fnExcptCd.equals("CONSTRUCT")) {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			} else {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			}
		}

		//증감기여도
		if (analType.equals("CHG_RATE_CO") && ( analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_H") || analCmpr.equals("PREV_Y_HS")|| analCmpr.equals("PREV_F") )  ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_co.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z_co.comp_prd_de(+)   \n");

			//z_co
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_co.ov_l" + tmpStr + "_id(+) \n");
				}
			}
		}
		else if (analType.equals("CHG_RATE_CO") && ( analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE") || analCmpr.equals("PREV_Y_HE") )  ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and substr(A.prd_de,1,4) =  substr(z1.comp_prd_de(+),1,4)   \n");
			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_co.prd_se(+)    \n");
			strBuff.append("      and substr(A.prd_de,1,4) =  substr(z_co.comp_prd_de(+),1,4)   \n");

			//z_co
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_co.ov_l" + tmpStr + "_id(+) \n");
				}
			}

		}
		else if (analType.equals("CHG_RATE_CO") && (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F"))  ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_co.prd_se(+)    \n");

			//z_co
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_co.ov_l" + tmpStr + "_id(+) \n");
				}
			}
		}

		//증감기여율
		if (analType.equals("CHG_RATE_CO_R") && ( analCmpr.equals("PREV_M") || analCmpr.equals("PREV_Y_MS") || analCmpr.equals("PREV_Y") || analCmpr.equals("PREV_Q") || analCmpr.equals("PREV_Y_QS") || analCmpr.equals("PREV_H") || analCmpr.equals("PREV_Y_HS")|| analCmpr.equals("PREV_F") ) ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_co.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z_co.comp_prd_de(+)   \n");

			//z_co
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_co.ov_l" + tmpStr + "_id(+) \n");
				}
			}

			strBuff.append("      and A.char_itm_id = z_cur.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_cur.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z_cur.comp_prd_de(+)   \n");

			//z_cur
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_cur.ov_l" + tmpStr + "_id(+) \n");
				}
			}
		}
		else if (analType.equals("CHG_RATE_CO_R") && ( analCmpr.equals("PREV_Y_ME") || analCmpr.equals("PREV_Y_QE") || analCmpr.equals("PREV_Y_HE")  )) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and substr(A.prd_de,1,4) =  substr(z1.comp_prd_de(+),1,4)   \n");
			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_co.prd_se(+)    \n");
			strBuff.append("      and substr(A.prd_de,1,4) =  substr(z_co.comp_prd_de(+),1,4)   \n");

			//z_co
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_co.ov_l" + tmpStr + "_id(+) \n");
				}
			}

			strBuff.append("      and A.char_itm_id = z_cur.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_cur.prd_se(+)    \n");
			strBuff.append("      and substr(A.prd_de,1,4) =  substr(z_cur.comp_prd_de(+),1,4)   \n");

			//z_cur
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_cur.ov_l" + tmpStr + "_id(+) \n");
				}
			}
		}
		else if (analType.equals("CHG_RATE_CO_R") && (analCmpr.equals("ONE_Y") || analCmpr.equals("ONE_M") || analCmpr.equals("ONE_Q") || analCmpr.equals("ONE_H") || analCmpr.equals("ONE_F")) ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_co.prd_se(+)    \n");

			strBuff.append("      and A.char_itm_id = z_co.char_itm_id(+)    \n");

			//z_co
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_co.ov_l" + tmpStr + "_id(+) \n");
				}
			}

			strBuff.append("      and A.char_itm_id = z_cur.char_itm_id(+)    \n");
			strBuff.append("      and A.prd_se = z_cur.prd_se(+)    \n");

			//z_cur
			for(int i = 0; i < classListForWhereClause.size(); i++){
				String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

				if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z_cur.ov_l" + tmpStr + "_id(+) \n");
				}
			}
		}
		//누계
		else if ( analType.equals("TOTL")) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)  \n");
		}
		//누계구성비
		else if (analType.equals("TOTL_CMP_RATE") && !noselectType.equals("noSelect")) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");

			strBuff.append("      and A.prd_se = z2.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z2.prd_de(+)   \n");

			//분류 또는 항목 비교
			if(analItemStr == null){
				strBuff.append("        AND A.CHAR_ITM_ID = z2.CHAR_ITM_ID(+) \n");

				for(int i = 0; i < classListForWhereClause.size(); i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

					if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
						strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id(+) \n");
					}
				}
			}else{
				for(int i = 0; i < classListForWhereClause.size(); i++){
					strBuff.append("        AND A." + (String)classListForWhereClause.get(i) + " = z2." + (String)classListForWhereClause.get(i) + "(+) \n");
				}
			}
		}
		//누계구성비 기준선택 안함
		else if (analType.equals("TOTL_CMP_RATE") && noselectType.equals("noSelect") ) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			strBuff.append("      and A.time = z2.t_prd    \n");
			strBuff.append("      and z1.char_itm_id = z2.char_itm_id    \n");
			if(closeList == 0){
				for(int i = 0; i < classListForWhereClause.size()-1; i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id \n");
				}
			}else{
				for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id \n");
				}
			}

			if(analItemStr == null){
				strBuff.append("        AND A.CHAR_ITM_ID = z2.CHAR_ITM_ID(+) \n");

				if(closeList == 0){
					for(int i = 0; i < classListForWhereClause.size()-1; i++){
						String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
						strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id \n");
					}
				}else{
					for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
						String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
						strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id \n");
					}
				}
			}
		}
		//구성비
		else if (analType.equals("CMP_RATE") && !noselectType.equals("noSelect") ) {
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)  \n");

			//분류 또는 항목 비교
			if(analItemStr == null){
				strBuff.append("        AND A.CHAR_ITM_ID = z1.CHAR_ITM_ID(+) \n");

				for(int i = 0; i < classListForWhereClause.size(); i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

					if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
						strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z1.ov_l" + tmpStr + "_id(+) \n");
					}
				}
			}else{
				for(int i = 0; i < classListForWhereClause.size(); i++){
					strBuff.append("        AND A." + (String)classListForWhereClause.get(i) + " = z1." + (String)classListForWhereClause.get(i) + "(+) \n");
				}
			}

		}
		//2015.5.8 구성비 기준자료선택 안함
		else if (analType.equals("CMP_RATE") && noselectType.equals("noSelect") ) {
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)  \n");
			strBuff.append("      AND A.CHAR_ITM_ID = Z2.CHAR_ITM_ID \n");
			if(closeList == 0){
				for(int i = 0; i < classListForWhereClause.size()-1; i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id \n");
				}
			}else{
				for(int i = 0; i < (classListForWhereClause.size()-1)-closeList; i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);
					strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z2.ov_l" + tmpStr + "_id \n");
				}
			}


			String prd = statHtmlDAO.getTimeDimensionList(paramInfo);

			String[] prdArr = prd.split("@");

			for(int x = 0; x < prdArr.length; x++){

				String[] tmpArr = prdArr[x].split(",");

				if(tmpArr.length == 2){

				}else{
					strBuff.append("        AND A.TIME = Z2.T_PRD \n");
				}
			}
			//분류 또는 항목 비교
			if(analItemStr == null){
				strBuff.append("        AND A.CHAR_ITM_ID = z1.CHAR_ITM_ID(+) \n");

				for(int i = 0; i < classListForWhereClause.size(); i++){
					String tmpStr = (String)classListForWhereClause.get(i).substring(4,  5);

					if( Collections.binarySearch(varOrdSnFuncListSorted, tmpStr) < 0 ){
						strBuff.append("        AND A.OV_L" + tmpStr + "_ID = z1.ov_l" + tmpStr + "_id(+) \n");
					}
				}
			}else{
				for(int i = 0; i < classListForWhereClause.size(); i++){
					strBuff.append("        AND A." + (String)classListForWhereClause.get(i) + " = z1." + (String)classListForWhereClause.get(i) + "(+) \n");
				}
			}

		}

		//전년누계비
		if ( analType.equals("CHG") && (analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL") )) {
			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
		}
		else if ( analType.equals("CHG_RATE") && (analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL") )) {
			if (fnExcptCd.equals("CONSTRUCT")) {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
				strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			} else {
				strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
				strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
				strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			}
		}
		//기여도 전년누계비
		else if ( analType.equals("CHG_RATE_CO") && (analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL") )) {
			//F 테이블이 추가됨에 따라 조건절 추가
			strBuff.append("      and A.ORG_ID = F.ORG_ID    \n");
			strBuff.append("      and A.TBL_ID = F.TBL_ID    \n");
			strBuff.append("      and A.ITM_RCGN_SN = F.ITM_RCGN_SN    \n");
			strBuff.append("      and A.PRD_SE = F.PRD_SE    \n");
			strBuff.append("      and A.PRD_DE = F.PRD_DE    \n");

			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			//strBuff.append("      and F.char_itm_id = z2.char_itm_id(+)    \n");
			strBuff.append("      and F.prd_se = z2.prd_se(+)    \n");
			strBuff.append("      and F.prd_de = z2.comp_prd_de(+)   \n");

			//TODO siga쪽의 쿼리를 수정한 후에 해야할듯

			// * 사용하지 않더라도 삭제하지 말것 -- 일부로 오류 발생시킴 z1
			for(int i = 0; i < analClassList.size(); i++){
				strBuff.append("        AND F.OV_L" + varOrdSnFuncList.get(i) + "_ID = z1.ov_l" + varOrdSnFuncList.get(i) + "_id(+) \n");
			}

		}
		else if ( analType.equals("CHG_RATE_CO_R") && (analCmpr.equals("PREV_Y_MTOTL") || analCmpr.equals("PREV_Y_QTOTL") || analCmpr.equals("PREV_Y_HTOTL") )) {
			//F 테이블이 추가됨에 따라 조건절 추가
			strBuff.append("      and A.ORG_ID = F.ORG_ID    \n");
			strBuff.append("      and A.TBL_ID = F.TBL_ID    \n");
			strBuff.append("      and A.ITM_RCGN_SN = F.ITM_RCGN_SN    \n");
			strBuff.append("      and A.PRD_SE = F.PRD_SE    \n");
			strBuff.append("      and A.PRD_DE = F.PRD_DE    \n");

			strBuff.append("      and A.itm_rcgn_sn = z1.itm_rcgn_sn(+)    \n");
			strBuff.append("      and A.prd_se = z1.prd_se(+)    \n");
			strBuff.append("      and A.prd_de = z1.comp_prd_de(+)   \n");
			//strBuff.append("      and F.char_itm_id = z2.char_itm_id(+)    \n");
			strBuff.append("      and F.prd_se = z2.prd_se(+)    \n");
			strBuff.append("      and F.prd_de = z2.comp_prd_de(+)   \n");

			//TODO siga쪽의 쿼리를 수정한 후에 해야할듯

			 //* 사용하지 않더라도 삭제하지 말것 -- 일부로 오류 발생시킴 z1
			for(int i = 0; i < analClassList.size(); i++){
				strBuff.append("        AND F.OV_L" + varOrdSnFuncList.get(i) + "_ID = z1.ov_l" + varOrdSnFuncList.get(i) + "_id(+) \n");
			}

		}

	}

	//분석 쿼리에서 공통으로 사용할 항목, 분류값 조건
	private void addWhereCompareDim(){
		//항목, 분류값 조건
		strBuff.append("        AND S1.CHAR_ITM_ID = S3.CHAR_ITM_ID \n");
		for(int i = 0; i < classListForWhereClause.size(); i++){
			strBuff.append("        AND S1." + (String)classListForWhereClause.get(i) + " = S3." + (String)classListForWhereClause.get(i) + " \n");
		}
	}

	//분석 쿼리에서 공통으로 사용할 TN_DIM, TN_DT 공표구분 체크
	private void addWherePubSe(){
		
		//2015.06.16 상속통계표 공표 체크
		if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
			//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
		}else{
			//TN_DIM, TN_DT 공표구분 체크
			if(serverType.equals("service_en")){
				strBuff.append("        AND S1.PUB_SE IN ('1210110', '1210114')	\n");
				strBuff.append("        AND S2.PUB_SE IN ('1210110', '1210114')	\n");
			}else if(serverType.equals("stat")){
				strBuff.append("        AND S1.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        AND S2.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
			}else if(serverType.equals("stat_emp")){
				strBuff.append("        AND S1.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        AND S2.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
			}else{
				strBuff.append("        AND S1.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
				strBuff.append("        AND S2.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
			}
		}
	}

	//분석 쿼리에서 공통으로 사용할 TN_DIM, TN_DT, TN_RECD_PRD 공표구분 체크
	private void addWherePubSeIncludeRecdPrd(){
		
		//2015.06.16 상속통계표 공표 체크
		if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
			//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
		}else{
			//TN_DIM, TN_DT 공표구분 체크 + TN_RECD_PRD
			if(serverType.equals("service_en")){
				strBuff.append("        			AND S1.PUB_SE IN ('1210110', '1210114')	\n");
				strBuff.append("        			AND S2.PUB_SE IN ('1210110', '1210114')	\n");
				strBuff.append("        			AND R.PUB_SE IN ('1210110', '1210114')	\n");
			}else if(serverType.equals("stat")){
				strBuff.append("        			AND S1.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        			AND S2.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        			AND R.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
			}else if(serverType.equals("stat_emp")){
				strBuff.append("        			AND S1.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        			AND S2.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        			AND R.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
			}else{
				strBuff.append("        			AND S1.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
				strBuff.append("        			AND S2.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
				strBuff.append("        			AND R.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
			}
		}
	}

	//분석 쿼리에서 공통으로 사용할 TN_DIM, TN_DT, TN_RECD_PRD 공표구분 체크
	private void addWherePubSeForAliasF(){

		//2015.06.16 상속통계표 공표 체크
		if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
			//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
		}else{
			//TN_DIM, TN_DT 공표구분 체크 + TN_RECD_PRD
			if(serverType.equals("service_en")){
				strBuff.append("        			AND qq.PUB_SE IN ('1210110', '1210114')	\n");
				strBuff.append("        			AND rr.PUB_SE IN ('1210110', '1210114')	\n");
			}else if(serverType.equals("stat")){
				strBuff.append("        			AND qq.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        			AND rr.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
			}else if(serverType.equals("stat_emp")){
				strBuff.append("        			AND qq.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				strBuff.append("        			AND rr.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
			}else{
				strBuff.append("        			AND qq.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
				strBuff.append("        			AND rr.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
			}
		}
	}

}
