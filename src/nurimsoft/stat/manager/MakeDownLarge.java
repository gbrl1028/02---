package nurimsoft.stat.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nurimsoft.stat.info.MetaInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.info.PeriodInfo;
import nurimsoft.stat.util.MessageManager;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MakeDownLarge{

	protected Log log = LogFactory.getLog(this.getClass());

	private StatHtmlDAO statHtmlDAO;
	private ParamInfo paramInfo;
	private HttpServletRequest request;
	
	private List<Map> list;
	private List<Map> itmIdList;
	private List<Map> objVarList;
	private String prdValue;
	private Map<String, String> stblUnitNm;
	private String[] prdArry;
	private List<String> classListForWhereClause = new ArrayList();

	//주기별 시점 출력 규칙(대용
	public String generatePrdDeForDown(String prdDe, String period, char lang, PeriodInfo pi){
		String retStr = null;

		if(period.equals("M")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(4);
			if(lang == 'e'){
				retStr += " Month";
			}else{
				retStr += " 월";
			}
		}else if(period.equals("Q")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(5) + "/4";
		}else if(period.equals("H")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(5) + "/2";
		}else if(period.equals("D")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(4, 6) + ". " + prdDe.substring(6);
		}else if(period.equals("T")){
			retStr = " (" + pi.getNameT() + ")" + prdDe;
		}else if(period.equals("B")){
			retStr = prdDe.substring(0, 4) + ". " + prdDe.substring(5) + "/6";
		}else if(period.equals("IR")){
			if(lang == 'e'){
				retStr = "Irregularly " + prdDe;
			}else{
				retStr = "부정기 " + prdDe;
			}
		}else{
			retStr = prdDe;
			if(lang == 'e'){
				retStr += " Year";
			}else{
				retStr += " 년";
			}
		}
		return retStr;
	}


	public MakeDownLarge(ParamInfo paramInfo, StatHtmlDAO statHtmlDAO, HttpServletRequest request) {
		this.paramInfo = paramInfo;
		this.statHtmlDAO = statHtmlDAO;
		this.request = request;
	}

	// 엑셀 다운로드
	public String getMakeExcel() throws Exception{
		PeriodInfo pi = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());

		MetaInfo metaInfo = new MakeMetaManager(paramInfo, statHtmlDAO, request).getMetaInfo();

		String sheetName = "";
		String sheetMetaName = "";

		final String sheetName_KO = "데이터";
		final String sheetName_EN = "Data";
		final String sheetMetaName_KO = "메타정보";
		final String sheetMetaName_EN = "Meta Data";

		char lang = (paramInfo.getDataOpt().indexOf("en") > -1) ? 'e' : 'k';

		if(lang == 'e'){
			sheetName = sheetName_EN;
			sheetMetaName = sheetMetaName_EN;
		}else{
			sheetName = sheetName_KO;
			sheetMetaName = sheetMetaName_KO;
		}

		Map paramListMap = new HashMap();
		Map tmpMap	= new HashMap();
		Map tmp	= new HashMap();
		Map headerMap = new HashMap();

		setClassListForWhereClause(paramInfo);

		//String sql = generateQuery(paramInfo);
		tmpMap.put("sql", generateQuery(paramInfo));
		
		long starttime = System.currentTimeMillis();
		
		list = statHtmlDAO.getSelectDownData(tmpMap);	// generator에서 조회한 값
		//System.out.println("대용량다운로드 수행시간  : " + (System.currentTimeMillis() - starttime)/1000.0 + "초");
		
		itmIdList = statHtmlDAO.getItmIdList(paramInfo); //  항목정보 조회
		objVarList = statHtmlDAO.getObjVarList(paramInfo); // 분류정보 조회
		prdValue =  statHtmlDAO.getPrdValue(paramInfo); // 시점 정보
		stblUnitNm = statHtmlDAO.getSelectTblUnitNm(paramInfo); //통계표 단위

		int size = objVarList.size() ;
		String exprType = paramInfo.getDownLargeExprType(); // 1: 시점표두 2: 항목표두
		String exprYn = paramInfo.getExprYn();
		String downLargeSort = paramInfo.getDownLargeSort(); // 시점 정렬 asc, desc
		String scrKor = "";
		String scrEng = "";
		String objVar = "";
		String itmId = "";
		String varOrdSn = "";
		String unitNm = "";
		String tblUnitNm = "";
		int sVSize = 0;
		List<String> columnList = new ArrayList();
		int prdArrLen = 0;

		String direct = paramInfo.getDirect();
		if(stblUnitNm != null){
			tblUnitNm = (!direct.equals("direct") && lang == 'e') ? (String)stblUnitNm.get("cdEngNm") : (String)stblUnitNm.get("cdNm");
		}

		if(exprType.equals("1")){
			// 분류
			if("Y".equals(exprYn)){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrEng);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrKor);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_ID");
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
//			 	항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_ID", "[Item]Item"  );
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_ID", "[Item]항목");
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_ID");
				columnList.add("CHAR_ITM_NM");
			}else{
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
//			 	항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_NM");
			}

			// 단위
			if(lang == 'e'){
				headerMap.put("UNIT_NM_KOR", "UNIT");
			}else{
				headerMap.put("UNIT_NM_KOR", "단위");
			}
			columnList.add("UNIT_NM_KOR");

			// 시점
			prdArry = prdValue.split("@");
			if(downLargeSort.equals("asc")){
				for(int k = 0; k < prdArry.length; k++){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = tmpArry.length - 1; j > 0; j--){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}else{
				for(int k = prdArry.length - 1; k >= 0; k--){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = 1; j < tmpArry.length; j++){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}

		//항목 표두일 때
		}else{
			if(objVarList != null){
				if("Y".equals(exprYn)){
					// 분류
					for(int i = 0; i < objVarList.size(); i++){
						tmpMap = objVarList.get(i);

						scrKor = (String) tmpMap.get("scrKor");		// 분류명
						scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
						objVar = (String) tmpMap.get("objVarId");	// 분류 코드
						varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));
						unitNm = (String)tmpMap.get("unitNm");

						if(lang == 'e'){
							headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+ scrEng);
							headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
						}else{
							headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+ scrKor);
							headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
						}
						columnList.add("OV_L" + varOrdSn + "_ID");
						columnList.add("OV_L" + varOrdSn + "_NM");
					}

				}else{
					// 분류
					for(int i = 0; i < objVarList.size(); i++){
						tmpMap = objVarList.get(i);

						scrKor = (String) tmpMap.get("scrKor");		// 분류명
						scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
						objVar = (String) tmpMap.get("objVarId");	// 분류 코드
						varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));
						unitNm = (String)tmpMap.get("unitNm");

						if(lang == 'e'){
							headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
						}else{
							headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
						}
						columnList.add("OV_L" + varOrdSn + "_NM");
					}
				}
				// 시점
				if(lang == 'e'){
					headerMap.put("PRD_SE",  "PERIOD" );
				}else{
					headerMap.put("PRD_SE",  "시점" );
				}
				columnList.add("PRD_SE");

				// 항목
				for(int i = 0; i < itmIdList.size(); i++){
					tmpMap = itmIdList.get(i);

					scrKor = (String)tmpMap.get("scrKor"); // 항목명
					scrEng = (String)tmpMap.get("scrEng"); // 항목명
					itmId = (String)tmpMap.get("itmId"); // 코드||항목

					if(lang == 'e'){
						headerMap.put(itmId, scrEng);
					}else{
						if(unitNm == null){
							headerMap.put(itmId, scrKor);
						}else{
							headerMap.put(itmId, scrKor);
						}
					}

					columnList.add(itmId);
				}
			}
		}

		list.add(0, headerMap);

		StringBuffer lineBuff = new StringBuffer();

		if("Y".equals(exprYn)){
			sVSize = size * 2; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 3;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize += 1;
			}
		}else{
			sVSize = size; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 2;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize += 1;
			}
		}

		int columnListSize = columnList.size();

		Map listMap = null;
		int listCnt = list.size() + 1; // 엑셀에서 리스트 row수 + 통계표 제목 row(1)수

		lineBuff.append("	<?xml version=\"" + "1.0" + "\"  encoding=\"" + "EUC-KR" + "\" ?>	\n");
		lineBuff.append("	<?mso-application progid=\"" + "Excel.Sheet" + "\"?>	\n");
		lineBuff.append("	<Workbook xmlns=\"" + "urn:schemas-microsoft-com:office:spreadsheet" + "\"	\n");
		lineBuff.append("	xmlns:o=\"" + "urn:schemas-microsoft-com:office:office" + "\"	\n");
		lineBuff.append("	xmlns:x=\"" + "urn:schemas-microsoft-com:office:excel" + "\"	\n");
		lineBuff.append("	xmlns:dt=\"" + "uuid:C2F41010-65B3-11d1-A29F-00AA00C14882" + "\"	\n");
		lineBuff.append("	xmlns:s=\"" + "uuid:BDC6E3F0-6DA3-11d1-A2A3-00AA00C14882" + "\"	\n");
		lineBuff.append("	xmlns:rs=\"" + "urn:schemas-microsoft-com:rowset" + "\" xmlns:z=\"" + "#RowsetSchema" + "\"	\n");
		lineBuff.append("	xmlns:ss=\"" + "urn:schemas-microsoft-com:office:spreadsheet" + "\"	\n");
		lineBuff.append("	xmlns:html=\"" + "http://www.w3.org/TR/REC-html40" + "\"	\n");
		lineBuff.append("	xmlns:xsd=\"" + "http://www.w3.org/2001/XMLSchema" + "\"	\n");
		lineBuff.append("	xmlns:xsi=\"" + "http://www.w3.org/2001/XMLSchema-instance" + "\">	\n");
		lineBuff.append("	<DocumentProperties xmlns=\"" + "urn:schemas-microsoft-com:office:office" + "\">	\n");
		lineBuff.append("	<Title>Regional production structure by SIC'93</Title>	\n");
		lineBuff.append("	<Author>CBS</Author>	\n");
		lineBuff.append("	<LastAuthor>Registered User</LastAuthor>	\n");
		lineBuff.append("	<LastSaved>2013-08-09T06:29:16Z</LastSaved>	\n");
		lineBuff.append("	<Company>CBS</Company>	\n");
		lineBuff.append("	<Version>12.00</Version>	\n");
		lineBuff.append("	</DocumentProperties>	\n");
		lineBuff.append("	<ExcelWorkbook xmlns=\"" + "urn:schemas-microsoft-com:office:excel" + "\">	\n");
		lineBuff.append("	<WindowHeight>10005</WindowHeight>	\n");
		lineBuff.append("	<WindowWidth>10005</WindowWidth>	\n");
		lineBuff.append("	<WindowTopX>120</WindowTopX>	\n");
		lineBuff.append("	<WindowTopY>135</WindowTopY>	\n");
		lineBuff.append("	<ProtectStructure>False</ProtectStructure>	\n");
		lineBuff.append("	<ProtectWindows>False</ProtectWindows>	\n");
		lineBuff.append("	</ExcelWorkbook>	\n");
		lineBuff.append("	<Styles>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "Default" + "\" ss:Name=\"" + "Normal" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Size=\"" + "8" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "s62" + "\" ss:Name=\"" + "Header" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Size=\"" + "8" + "\" ss:Bold=\"" + "0" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "s63" + "\" ss:Name=\"" + "Hyperlink" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Size=\"" + "8" + "\" ss:Color=\"" + "#0000FF" + "\"	\n");
		lineBuff.append("	ss:Underline=\"" + "Single" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "s64" + "\" ss:Name=\"" + "Title" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Bold=\"" + "1" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	</Styles>	\n");
		lineBuff.append("	<Worksheet ss:Name=\"" + sheetName + "\">	\n");
		lineBuff.append("	<Table ss:ExpandedColumnCount=\"" + columnListSize + "\" ss:ExpandedRowCount=\"" + listCnt + "\" x:FullColumns=\"" + "1" + "\"	\n");
		lineBuff.append("	x:FullRows=\"" + "1" + "\" ss:DefaultColumnWidth=\"" + "90" + "\" ss:DefaultRowHeight=\"" + "11.25" + "\">	\n");
		lineBuff.append("	<Column ss:AutoFitWidth=\"" + "0" + "\" ss:Width=\"" + "90" + "\"/>	\n");
		lineBuff.append("	<Column ss:Width=\"" + "90" + "\"/>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		if(tblUnitNm == null ){
			lineBuff.append("	<Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">○ "+  StringUtil.replace_str(metaInfo.getTblNm())  + "</Data></Cell>	\n");
		}else{
			lineBuff.append("	<Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">○ "+  StringUtil.replace_str(metaInfo.getTblNm()) + " ["+ tblUnitNm +"]"  + "</Data></Cell>	\n");
		}
		lineBuff.append("	</Row>	\n");

		for(int i = 0; i<list.size(); i++){
			listMap = list.get(i);
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");

			String colName = null;
			String result = null;

			for(int j = 0; j < columnListSize; j++){
				colName = columnList.get(j);
				result = (String)listMap.get(colName);

				if(i == 0 || sVSize >= (j + 1)){
					if(sVSize == (j+1) && colName != null && colName.equals("PRD_SE")){
						String[] timeArr = result.split("@#");
						if(timeArr.length > 1){
							result = generatePrdDeForDown(timeArr[1], timeArr[0], lang, pi);
						}
					}

					if(sVSize == (j+1) && colName != null && colName.equals("UNIT_NM_KOR")){
						String[] unitArr = result.split("@");
						for(int k = 0;k<unitArr.length; k++){
							String temp = unitArr[k];
							if(!temp.equals("^")){
								result = temp;
								break;
							}else{
								result = "";
							}
						}
					}

					lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">"+result+"</Data></Cell>	\n");
				}else{
					if(listMap.get(columnList.get(j)) != null){
						String[] arr = result.split("#@");

						String[] valArray = new String[4];
						valArray[0] = arr[0];	//DTVAL_CO or WGT_CO
						valArray[1] = arr[1];		//PERIOD_CO

						if(arr.length == 3){
							valArray[2] = arr[2];
						}else{
							valArray[2] = "";
						}
						if(arr.length == 4){
							valArray[3] = arr[3];
						}else{
							valArray[3] = "";
						}

						//result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						/*2014.07.01 만셀넘는 엑셀다운시는 소수점 여부 관련없이 다보여주도록 - 김경호*/
						String value_buff = valArray[0];
						value_buff = value_buff.replaceAll(",", "");

						if( value_buff != null && !value_buff.equals("99999999999.99999") && !value_buff.equals("") ){
							//result = valArray[0];
							result = StatPivotUtil.getFormatedMeasure(valArray, "99", 0);
						}else{
							result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						}
						/*-------------------------------------------------------*/

						Boolean resultType = StatPivotUtil.isNumericValue(result);
						if(resultType == false){
							lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">"+result+"</Data></Cell>	\n");
						}else{
							lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "Number" + "\">"+result+"</Data></Cell>	\n");
						}
					}else{
						lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\"></Data></Cell>	\n");
					}
				}
			}
			lineBuff.append("	</Row>	\n");
		}
		lineBuff.append("	</Table>	\n");
		lineBuff.append("	</Worksheet>	\n");

		lineBuff.append("	<Worksheet ss:Name=\"" + sheetMetaName + "\">	\n");
		lineBuff.append("	<Table>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		if(lang == 'e'){
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">< Statistics metadata ></Data>	\n");
		}else{
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">< 통계표 메타자료 ></Data>	\n");
		}
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getTblIdTitle() + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + metaInfo.getTblId() + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getTblNmTitle() + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + StringUtil.replace_str(metaInfo.getTblNm()) + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getContainPeriodTitle()+ "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">"  +metaInfo.getContainPeriod()+ "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		if(metaInfo.getSource() != null){
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getSourceTitle() + "</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">KOSIS(" + StringUtils.defaultString(metaInfo.getSource(), "") + "), " + metaInfo.getDownDate()+" </Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	</Row>	\n");
		}
		if(metaInfo.getIquire() != null){
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getIquireTitle() + "</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + StringUtils.defaultString(metaInfo.getIquire(), "") + " </Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	</Row>	\n");
		}
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getUrlTitle()+ "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + metaInfo.getUrl() + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\"></Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + MessageManager.getInstance().getProperty("text.meta.url", paramInfo.getDataOpt()) + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		if(metaInfo.getUnit() != null){
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○" + metaInfo.getUnitTitle()+ "</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + metaInfo.getUnit() + "</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	</Row>	\n");
		}

		List cmmtList = metaInfo.getCmmtList();
		int cmmtListSize = cmmtList.size();
		if(cmmtListSize > 0){
			//주석
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○"+ metaInfo.getCmmtTitle() +"</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	</Row>	\n");

			String spaceStr = "         ";

			Map map = null;
			String cmmtDc = null;
			int rnum = 0;
			String title = null;

			StringBuffer strBuff = new StringBuffer();

			for(int i = 0; i < cmmtListSize; i++){
				strBuff.setLength(0);

				if(i != 0){
					strBuff.append(spaceStr);
				}

				map = (Map)cmmtList.get(i);

				cmmtDc = (String)map.get("cmmtDc");
				rnum = ((BigDecimal)map.get("rnum")).intValue();
				title = (String)map.get("title");

				if(rnum == 1){
					strBuff.append(title);
				}

				strBuff.append(cmmtDc);

				// 2014.06.19 주석정보에 HTML태그가 들어가 있어 다운시 에러가 나는 문제 수정 - 김경호
				String buff	 = StringUtil.replace_str(strBuff.toString());

				lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
				lineBuff.append("	<Cell> 	\n");
				lineBuff.append("	<Data ss:Type=\"" + "String" + "\"></Data>	\n");
				lineBuff.append("	</Cell>	\n");
				lineBuff.append("	<Cell> 	\n");
				lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + buff + "\"</Data>	\n");
				lineBuff.append("	</Cell>	\n");
				lineBuff.append("	</Row>	\n");

				strBuff.append(cmmtDc).append("\r\n");
			}

		}
		lineBuff.append("	</Table>	\n");
		lineBuff.append("	</Worksheet>	\n");
		lineBuff.append("	</Workbook>	\n");

		return lineBuff.toString();
	}

	// CSV 다운로드
	public String getMakeCSV() throws Exception{

		String sql = generateQuery(paramInfo);

		PeriodInfo pi = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());

		Map paramMap = new HashMap();
		Map tmpMap	= new HashMap();
		Map headerMap = new HashMap();

		setClassListForWhereClause(paramInfo);

		tmpMap.put("sql", generateQuery(paramInfo));

		List<Map> list = statHtmlDAO.getSelectDownData(tmpMap);//generator에서 조회한 값
		objVarList = statHtmlDAO.getObjVarList(paramInfo); // 분류정보 조회
		itmIdList = statHtmlDAO.getItmIdList(paramInfo); //  항목정보 조회
		prdValue =  statHtmlDAO.getPrdValue(paramInfo); // 시점 정보

		int size = objVarList.size() ;

		String scrKor = "";	// 한글 분류명
		String scrEng = ""; //영문 분류명
		String itmId = "";
		String objVar = "";
		String varOrdSn = "";
		String exprType = paramInfo.getDownLargeExprType(); // 1: 시점표두 2: 항목표두
		String exprYn = paramInfo.getExprYn(); // 분류, 항목 코드 유무
		List<String> columnList = new ArrayList();
		int prdArrLen = 0;

		char lang = (paramInfo.getDataOpt().indexOf("en") > -1) ? 'e' : 'k';

		// 시점 표두일 때
		if(exprType.equals("1")){
			if("Y".equals(exprYn)){
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]" + scrEng);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]" +scrKor);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_ID");
					columnList.add("OV_L" + varOrdSn + "_NM");
				}

				// 항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_ID", "[Item]Item");
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_ID", "[Item]항목");
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_ID");
				columnList.add("CHAR_ITM_NM");
			}else{
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_NM");
				}

				// 항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_NM");
			}
			// 단위
			if(lang == 'e'){
				headerMap.put("UNIT_NM_KOR", "UNIT");
			}else{
				headerMap.put("UNIT_NM_KOR", "단위");
			}
			columnList.add("UNIT_NM_KOR");

			String downLargeSort = paramInfo.getDownLargeSort(); // 시점 정렬 asc, desc

			prdArry = prdValue.split("@");
			if(downLargeSort.equals("asc")){
				for(int k = 0; k < prdArry.length; k++){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = tmpArry.length - 1; j > 0; j--){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}else{
				for(int k = prdArry.length - 1; k >= 0; k--){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = 1; j < tmpArry.length; j++){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}

		}else{
			// 항목 표두일 때
			if("Y".equals(exprYn)){
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 한글 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrEng);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrKor);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_ID");
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
			}else{
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 한글 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
			}

			//시점
			if(lang == 'e'){
				headerMap.put("PRD_SE",  "PERIOD" );
			}else{
				headerMap.put("PRD_SE",  "시점" );
			}
			columnList.add("PRD_SE");

			// 항목
			for(int i = 0; i < itmIdList.size(); i++){
				tmpMap = itmIdList.get(i);

				scrKor = (String)tmpMap.get("scrKor"); // 항목명
				scrEng = (String)tmpMap.get("scrEng"); // 영문 항목명
				itmId = (String)tmpMap.get("itmId"); // 코드||항목

				if(lang == 'e'){
					headerMap.put(itmId, scrEng);
				}else{
					headerMap.put(itmId, scrKor);
				}
				columnList.add(itmId);
			}
		}

		list.add(0, headerMap);

		Map listMap = null;

		StringBuffer lineBuff = new StringBuffer();

		int columnListSize = columnList.size();
		int sVSize = 0; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등]

		if("Y".equals(exprYn)){
			sVSize = size * 2; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 3;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize++;
			}
		}else{
			sVSize = size; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 2;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize += 1;
			}
		}

		// csv 데이터 만들기
		for(int i = 0; i<list.size(); i++){
			listMap = list.get(i);

			String colName = null;
			String result = null;

			for(int j = 0; j < columnListSize; j++){

				colName = columnList.get(j);
				result = (String)listMap.get(colName);

				if(i == 0 || sVSize >= (j + 1)){
					if(sVSize == (j+1) && colName != null && colName.equals("PRD_SE")){
						String[] timeArr = result.split("@#");
						if(timeArr.length > 1){
							result = generatePrdDeForDown(timeArr[1], timeArr[0], lang, pi);
						}
					}
					if(sVSize == (j+1) && colName != null && colName.equals("UNIT_NM_KOR")){
						String[] unitArr = result.split("@");
						for(int k = 0;k<unitArr.length; k++){
							String temp = unitArr[k];
							if(!temp.equals("^")){
								result = temp;
								break;
							}else{
								result = "";
							}
						}
					}
					lineBuff.append("\"" + result + "\",");
				}else{
					if(listMap.get(columnList.get(j)) != null){
						String value = ((String)listMap.get(columnList.get(j)));	// columnList.get(j) 컬럼 값

						String[] arr = value.split("#@");

						String[] valArray = new String[4];
						valArray[0] = arr[0];	//DTVAL_CO or WGT_CO
						valArray[1] = arr[1];	//PERIOD_CO

						if(arr.length == 3){
							valArray[2] = arr[2];
						}else{
							valArray[2] = "";
						}
						if(arr.length == 4){
							valArray[3] = arr[3];
						}else{
							valArray[3] = "";
						}

						//result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						/*2014.07.01 만셀넘는 엑셀다운시는 소수점 여부 관련없이 다보여주도록 - 김경호*/
						String value_buff = valArray[0];
						value = value_buff.replaceAll(",", "");

						if( value_buff != null && !value_buff.equals("99999999999.99999") && !value_buff.equals("") ){
							result = valArray[0];
						}else{
							result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						}
						/*-------------------------------------------------------*/

						lineBuff.append("\"" + result + "\",");
					}else{
						lineBuff.append("\"" + "" + "\",");
					}
				}
			}
			lineBuff.append("\n");
		}
		return lineBuff.toString();
	}

	// 엑셀 다운로드
	public String getDirectMakeExcel() throws Exception{
		PeriodInfo pi = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());

		//MetaInfo metaInfo = new MakeMetaManager(paramInfo, statHtmlDAO).getMetaInfo();

		// 직접다운로드용 통계표정보
		Map statInfo = statHtmlDAO.getDirectStatInfo(paramInfo);
		// 직접다운로드용 주석정보
		List<Map> cmmtInfo = statHtmlDAO.getDirectCmmt(paramInfo);



		String tblId = paramInfo.getTblId();
		String tblNm = "";
		String prdInfo = "";
		String deptNm = "";
		String sysdate = "";
		String deptTel = "";

		if (statInfo.get("TBL_NM")    != null) { tblNm   = statInfo.get("TBL_NM").toString();    }
		if (statInfo.get("PRD_INFO")  != null) { prdInfo = statInfo.get("PRD_INFO").toString();  }
		if (statInfo.get("DEPT_NM")   != null) { deptNm  = statInfo.get("DEPT_NM").toString();   }
		if (statInfo.get("MAKE_DATE") != null) { sysdate = statInfo.get("MAKE_DATE").toString(); }
		if (statInfo.get("DEPT_TEL")  != null) { deptTel = statInfo.get("DEPT_TEL").toString();  }

		String sheetName = "";
		String sheetMetaName = "";

		final String sheetName_KO = "데이터";
		final String sheetName_EN = "Data";
		final String sheetMetaName_KO = "메타정보";
		final String sheetMetaName_EN = "Meta Data";

		char lang = (paramInfo.getDataOpt().indexOf("en") > -1) ? 'e' : 'k';

		if(lang == 'e'){
			sheetName = sheetName_EN;
			sheetMetaName = sheetMetaName_EN;
		}else{
			sheetName = sheetName_KO;
			sheetMetaName = sheetMetaName_KO;
		}

		Map paramListMap = new HashMap();
		Map tmpMap	= new HashMap();
		Map tmp	= new HashMap();
		Map headerMap = new HashMap();

		setDirectClassListForWhereClause(paramInfo);

		String sql = generateQuery(paramInfo);
		tmpMap.put("sql", generateQuery(paramInfo));
		long start = System.currentTimeMillis();
		list = statHtmlDAO.getSelectDownData(tmpMap);	// generator에서 조회한 값
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 튜닝 후 실행 시간 : " + (System.currentTimeMillis() - start) / 1000);
		itmIdList = statHtmlDAO.getDirectItmIdList(paramInfo); //  항목정보 조회
		objVarList = statHtmlDAO.getObjVarList(paramInfo); // 분류정보 조회
		prdValue =  paramInfo.getPrdSe() + "," + paramInfo.getPrdDe() + "@";
		stblUnitNm = statHtmlDAO.getSelectTblUnitNm(paramInfo); //통계표 단위

		int size = objVarList.size() ;
		String exprType = paramInfo.getDownLargeExprType(); // 1: 시점표두 2: 항목표두
		String exprYn = paramInfo.getExprYn();
		String downLargeSort = paramInfo.getDownLargeSort(); // 시점 정렬 asc, desc
		String scrKor = "";
		String scrEng = "";
		String objVar = "";
		String itmId = "";
		String varOrdSn = "";
		String unitNm = "";
		String tblUnitNm = "";
		int sVSize = 0;
		List<String> columnList = new ArrayList();
		int prdArrLen = 0;

		String direct = paramInfo.getDirect();
		if(stblUnitNm != null){
			tblUnitNm = (!direct.equals("direct") && lang == 'e') ? (String)stblUnitNm.get("cdEngNm") : (String)stblUnitNm.get("cdNm");
		}

		if(exprType.equals("1")){
			// 분류
			if("Y".equals(exprYn)){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrEng);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrKor);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_ID");
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
//			 	항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_ID", "[Item]Item"  );
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_ID", "[Item]항목");
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_ID");
				columnList.add("CHAR_ITM_NM");
			}else{
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
//			 	항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_NM");
			}

			// 단위
			headerMap.put("UNIT_NM_KOR", "단위");
			columnList.add("UNIT_NM_KOR");

			// 시점
			prdArry = prdValue.split("@");
			if(downLargeSort.equals("asc")){
				for(int k = 0; k < prdArry.length; k++){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = tmpArry.length - 1; j > 0; j--){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}else{
				for(int k = prdArry.length - 1; k >= 0; k--){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = 1; j < tmpArry.length; j++){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}

		//항목 표두일 때
		}else{
			if(objVarList != null){
				if("Y".equals(exprYn)){
					// 분류
					for(int i = 0; i < objVarList.size(); i++){
						tmpMap = objVarList.get(i);

						scrKor = (String) tmpMap.get("scrKor");		// 분류명
						scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
						objVar = (String) tmpMap.get("objVarId");	// 분류 코드
						varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));
						unitNm = (String)tmpMap.get("unitNm");

						if(lang == 'e'){
							headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+ scrEng);
							headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
						}else{
							headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+ scrKor);
							headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
						}
						columnList.add("OV_L" + varOrdSn + "_ID");
						columnList.add("OV_L" + varOrdSn + "_NM");
					}

				}else{
					// 분류
					for(int i = 0; i < objVarList.size(); i++){
						tmpMap = objVarList.get(i);

						scrKor = (String) tmpMap.get("scrKor");		// 분류명
						scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
						objVar = (String) tmpMap.get("objVarId");	// 분류 코드
						varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));
						unitNm = (String)tmpMap.get("unitNm");

						if(lang == 'e'){
							headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
						}else{
							headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
						}
						columnList.add("OV_L" + varOrdSn + "_NM");
					}
				}
				// 시점
				if(lang == 'e'){
					headerMap.put("PRD_SE",  "PERIOD" );
				}else{
					headerMap.put("PRD_SE",  "시점" );
				}
				columnList.add("PRD_SE");

				// 항목
				for(int i = 0; i < itmIdList.size(); i++){
					tmpMap = itmIdList.get(i);

					scrKor = (String)tmpMap.get("scrKor"); // 항목명
					scrEng = (String)tmpMap.get("scrEng"); // 항목명
					itmId = (String)tmpMap.get("itmId"); // 코드||항목

					if(lang == 'e'){
						headerMap.put(itmId, scrEng);
					}else{
						if(unitNm == null){
							headerMap.put(itmId, scrKor);
						}else{
							headerMap.put(itmId, scrKor);
						}
					}

					columnList.add(itmId);
				}
			}
		}

		list.add(0, headerMap);

		StringBuffer lineBuff = new StringBuffer();

		if("Y".equals(exprYn)){
			sVSize = size * 2; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 3;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize += 1;
			}
		}else{
			sVSize = size; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 2;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize += 1;
			}
		}

		int columnListSize = columnList.size();

		Map listMap = null;
		int listCnt = list.size() + 1; // 엑셀에서 리스트 row수 + 통계표 제목 row(1)수

		lineBuff.append("	<?xml version=\"" + "1.0" + "\"  encoding=\"" + "EUC-KR" + "\" ?>	\n");
		lineBuff.append("	<?mso-application progid=\"" + "Excel.Sheet" + "\"?>	\n");
		lineBuff.append("	<Workbook xmlns=\"" + "urn:schemas-microsoft-com:office:spreadsheet" + "\"	\n");
		lineBuff.append("	xmlns:o=\"" + "urn:schemas-microsoft-com:office:office" + "\"	\n");
		lineBuff.append("	xmlns:x=\"" + "urn:schemas-microsoft-com:office:excel" + "\"	\n");
		lineBuff.append("	xmlns:dt=\"" + "uuid:C2F41010-65B3-11d1-A29F-00AA00C14882" + "\"	\n");
		lineBuff.append("	xmlns:s=\"" + "uuid:BDC6E3F0-6DA3-11d1-A2A3-00AA00C14882" + "\"	\n");
		lineBuff.append("	xmlns:rs=\"" + "urn:schemas-microsoft-com:rowset" + "\" xmlns:z=\"" + "#RowsetSchema" + "\"	\n");
		lineBuff.append("	xmlns:ss=\"" + "urn:schemas-microsoft-com:office:spreadsheet" + "\"	\n");
		lineBuff.append("	xmlns:html=\"" + "http://www.w3.org/TR/REC-html40" + "\"	\n");
		lineBuff.append("	xmlns:xsd=\"" + "http://www.w3.org/2001/XMLSchema" + "\"	\n");
		lineBuff.append("	xmlns:xsi=\"" + "http://www.w3.org/2001/XMLSchema-instance" + "\">	\n");
		lineBuff.append("	<DocumentProperties xmlns=\"" + "urn:schemas-microsoft-com:office:office" + "\">	\n");
		lineBuff.append("	<Title>Regional production structure by SIC'93</Title>	\n");
		lineBuff.append("	<Author>CBS</Author>	\n");
		lineBuff.append("	<LastAuthor>Registered User</LastAuthor>	\n");
		lineBuff.append("	<LastSaved>2013-08-09T06:29:16Z</LastSaved>	\n");
		lineBuff.append("	<Company>CBS</Company>	\n");
		lineBuff.append("	<Version>12.00</Version>	\n");
		lineBuff.append("	</DocumentProperties>	\n");
		lineBuff.append("	<ExcelWorkbook xmlns=\"" + "urn:schemas-microsoft-com:office:excel" + "\">	\n");
		lineBuff.append("	<WindowHeight>10005</WindowHeight>	\n");
		lineBuff.append("	<WindowWidth>10005</WindowWidth>	\n");
		lineBuff.append("	<WindowTopX>120</WindowTopX>	\n");
		lineBuff.append("	<WindowTopY>135</WindowTopY>	\n");
		lineBuff.append("	<ProtectStructure>False</ProtectStructure>	\n");
		lineBuff.append("	<ProtectWindows>False</ProtectWindows>	\n");
		lineBuff.append("	</ExcelWorkbook>	\n");
		lineBuff.append("	<Styles>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "Default" + "\" ss:Name=\"" + "Normal" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Size=\"" + "8" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "s62" + "\" ss:Name=\"" + "Header" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Size=\"" + "8" + "\" ss:Bold=\"" + "0" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "s63" + "\" ss:Name=\"" + "Hyperlink" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Size=\"" + "8" + "\" ss:Color=\"" + "#0000FF" + "\"	\n");
		lineBuff.append("	ss:Underline=\"" + "Single" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	<Style ss:ID=\"" + "s64" + "\" ss:Name=\"" + "Title" + "\">	\n");
		lineBuff.append("	<Alignment ss:Vertical=\"" + "Bottom" + "\"/>	\n");
		lineBuff.append("	<Font ss:FontName=\"" + "Arial" + "\" x:Family=\"" + "Swiss" + "\" ss:Bold=\"" + "1" + "\"/>	\n");
		lineBuff.append("	</Style>	\n");
		lineBuff.append("	</Styles>	\n");
		lineBuff.append("	<Worksheet ss:Name=\"" + sheetName + "\">	\n");
		lineBuff.append("	<Table ss:ExpandedColumnCount=\"" + columnListSize + "\" ss:ExpandedRowCount=\"" + listCnt + "\" x:FullColumns=\"" + "1" + "\"	\n");
		lineBuff.append("	x:FullRows=\"" + "1" + "\" ss:DefaultColumnWidth=\"" + "90" + "\" ss:DefaultRowHeight=\"" + "11.25" + "\">	\n");
		lineBuff.append("	<Column ss:AutoFitWidth=\"" + "0" + "\" ss:Width=\"" + "90" + "\"/>	\n");
		lineBuff.append("	<Column ss:Width=\"" + "90" + "\"/>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		if(tblUnitNm == null || tblUnitNm.equals("")){
			lineBuff.append("	<Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">○ "+  StringUtil.replace_str(tblNm)  + "</Data></Cell>	\n");
		}else{
			lineBuff.append("	<Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">○ "+  StringUtil.replace_str(tblNm) + " ["+ tblUnitNm +"]"  + "</Data></Cell>	\n");
		}
		lineBuff.append("	</Row>	\n");

		for(int i = 0; i<list.size(); i++){
			listMap = list.get(i);
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");

			String colName = null;
			String result = null;

			for(int j = 0; j < columnListSize; j++){
				colName = columnList.get(j);
				result = (String)listMap.get(colName);

				if(i == 0 || sVSize >= (j + 1)){
					if(sVSize == (j+1) && colName != null && colName.equals("PRD_SE")){
						String[] timeArr = result.split("@#");
						if(timeArr.length > 1){
							result = generatePrdDeForDown(timeArr[1], timeArr[0], lang, pi);
						}
					}

					if(sVSize == (j+1) && colName != null && colName.equals("UNIT_NM_KOR")){
						String[] unitArr = result.split("@");
						for(int k = 0;k<unitArr.length; k++){
							String temp = unitArr[k];
							if(!temp.equals("^")){
								result = temp;
								break;
							}else{
								result = "";
							}
						}
					}

					lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">"+result+"</Data></Cell>	\n");
				}else{
					if(listMap.get(columnList.get(j)) != null){
						String[] arr = result.split("#@");

						String[] valArray = new String[4];
						valArray[0] = arr[0];	//DTVAL_CO or WGT_CO
						valArray[1] = arr[1];		//PERIOD_CO

						if(arr.length == 3){
							valArray[2] = arr[2];
						}else{
							valArray[2] = "";
						}
						if(arr.length == 4){
							valArray[3] = arr[3];
						}else{
							valArray[3] = "";
						}

						//result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						/*2014.07.01 만셀넘는 엑셀다운시는 소수점 여부 관련없이 다보여주도록 - 김경호*/
						String value_buff = valArray[0];
						value_buff = value_buff.replaceAll(",", "");

						if( value_buff != null && !value_buff.equals("99999999999.99999") && !value_buff.equals("") ){
							result = valArray[0];
						}else{
							result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						}
						/*-------------------------------------------------------*/

						Boolean resultType = StatPivotUtil.isNumericValue(result);
						if(resultType == false){
							lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\">"+result+"</Data></Cell>	\n");
						}else{
							lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "Number" + "\">"+result+"</Data></Cell>	\n");
						}
					}else{
						lineBuff.append("	 <Cell ss:StyleID=\"" + "s62" + "\"><Data ss:Type=\"" + "String" + "\"></Data></Cell>	\n");
					}
				}
			}
			lineBuff.append("	</Row>	\n");
		}
		lineBuff.append("	</Table>	\n");
		lineBuff.append("	</Worksheet>	\n");

		lineBuff.append("	<Worksheet ss:Name=\"" + sheetMetaName + "\">	\n");
		lineBuff.append("	<Table>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		if(lang == 'e'){
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">< Statistics metadata ></Data>	\n");
		}else{
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">< 통계표 메타자료 ></Data>	\n");
		}
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○ 통계표ID</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + tblId +"</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○ 통계표명</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + StringUtil.replace_str(tblNm) +"</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○ 수록기간</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + prdInfo + "</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		if (deptNm != null) {
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○ 출처</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">KOSIS(" + deptNm + "), "+sysdate+" </Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	</Row>	\n");
		}
		if (deptTel != null) {
			lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">○문의처</Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	<Cell> 	\n");
			lineBuff.append("	<Data ss:Type=\"" + "String" + "\">"+ deptTel + " </Data>	\n");
			lineBuff.append("	</Cell>	\n");
			lineBuff.append("	</Row>	\n");
		}
		// 주석
		lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + "○주석"+"</Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	<Cell> 	\n");
		lineBuff.append("	<Data ss:Type=\"" + "String" + "\"></Data>	\n");
		lineBuff.append("	</Cell>	\n");
		lineBuff.append("	</Row>	\n");
		if(cmmtInfo.size() != 0){
			if(cmmtInfo.size() != 0){
				int rnum = 0;
				for(int i = 0; i<cmmtInfo.size(); i++){

					tmpMap = cmmtInfo.get(i);

					String cmtDc = (String)tmpMap.get("CMMT_DC"); // 주석
					String cmtNm = (String)tmpMap.get("CMMT_NM"); // 주석 구분(통계표, 항목, 분류..)
					String cmtDcTrim = "";
					rnum = ((BigDecimal)tmpMap.get("RNUM")).intValue();

					if(cmtDc != null) {cmtDcTrim = cmtDc;}

					if(rnum == 1){
						lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
						lineBuff.append("	<Cell> 	\n");
						lineBuff.append("	<Data ss:Type=\"" + "String" + "\">" + cmtNm +"</Data>	\n");
						lineBuff.append("	</Cell>	\n");
						lineBuff.append("	<Cell> 	\n");
						lineBuff.append("	<Data ss:Type=\"" + "String" + "\"> " + cmtDcTrim +"</Data>	\n");
						lineBuff.append("	</Cell>	\n");
						lineBuff.append("	</Row>	\n");
					}else{
						lineBuff.append("	<Row ss:AutoFitHeight=\"" + "0" + "\" ss:Height=\"" + "12" + "\">	\n");
						lineBuff.append("	<Cell> 	\n");
						lineBuff.append("	<Data ss:Type=\"" + "String" + "\"></Data>	\n");
						lineBuff.append("	</Cell>	\n");
						lineBuff.append("	<Cell> 	\n");
						lineBuff.append("	<Data ss:Type=\"" + "String" + "\"> " + cmtDcTrim +"</Data>	\n");
						lineBuff.append("	</Cell>	\n");
						lineBuff.append("	</Row>	\n");
					}
				}
			}
		}
		lineBuff.append("	</Table>	\n");
		lineBuff.append("	</Worksheet>	\n");
		lineBuff.append("	</Workbook>	\n");
		return lineBuff.toString();
	}

	// CSV 다운로드
	public String getDirectMakeCSV() throws Exception{
		PeriodInfo pi = new PeriodInfo(paramInfo.getDataOpt(), statHtmlDAO, paramInfo.getDbUser());

		Map paramMap = new HashMap();
		Map tmpMap	= new HashMap();
		Map headerMap = new HashMap();

		setDirectClassListForWhereClause(paramInfo);
		tmpMap.put("sql", generateQuery(paramInfo));
		long start = System.currentTimeMillis();

		List<Map> list = statHtmlDAO.getSelectDownData(tmpMap);//generator에서 조회한 값
		//System.out.println("직접다운로드 수행시간  : " + (System.currentTimeMillis() - start)/1000.0 + "초");		
		
		objVarList = statHtmlDAO.getObjVarList(paramInfo); // 분류정보 조회
		itmIdList = statHtmlDAO.getDirectItmIdList(paramInfo); //  항목정보 조회
		prdValue =  paramInfo.getPrdSe() + "," + paramInfo.getPrdDe() + "@";

		int size = objVarList.size() ;

		String scrKor = "";	// 한글 분류명
		String scrEng = ""; //영문 분류명
		String itmId = "";
		String objVar = "";
		String varOrdSn = "";
		String exprType = paramInfo.getDownLargeExprType(); // 1: 시점표두 2: 항목표두
		String exprYn = paramInfo.getExprYn(); // 분류, 항목 코드 유무
		String type = paramInfo.getDownLargeFileType();	// 파일 타입 excel, csv, txt
		
		List<String> columnList = new ArrayList();
		int prdArrLen = 0;

		char lang = (paramInfo.getDataOpt().indexOf("en") > -1) ? 'e' : 'k';

		// 시점 표두일 때
		if(exprType.equals("1")){
			if("Y".equals(exprYn)){
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]" + scrEng);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]" +scrKor);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_ID");
					columnList.add("OV_L" + varOrdSn + "_NM");
				}

				// 항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_ID", "[Item]Item");
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_ID", "[Item]항목");
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_ID");
				columnList.add("CHAR_ITM_NM");
			}else{
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_NM");
				}

				// 항목
				if(lang == 'e'){
					headerMap.put("CHAR_ITM_NM", "Item");
				}else{
					headerMap.put("CHAR_ITM_NM", "항목");
				}
				columnList.add("CHAR_ITM_NM");
			}
			// 단위
			headerMap.put("UNIT_NM_KOR", "단위");
			columnList.add("UNIT_NM_KOR");

			String downLargeSort = paramInfo.getDownLargeSort(); // 시점 정렬 asc, desc

			prdArry = prdValue.split("@");
			if(downLargeSort.equals("asc")){
				for(int k = 0; k < prdArry.length; k++){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = tmpArry.length - 1; j > 0; j--){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}else{
				for(int k = prdArry.length - 1; k >= 0; k--){
					String[] tmpArry = prdArry[k].split(",");

					String period = tmpArry[0];

					if(period.equals("F")){
						period = statHtmlDAO.getSelectPrdDetail(paramInfo);
					}

					prdArrLen += (tmpArry.length - 1);
					for(int j = 1; j < tmpArry.length; j++){

						String prdDe = tmpArry[j];
						String result = generatePrdDeForDown(prdDe, period, lang, pi);

						headerMap.put(tmpArry[0]+tmpArry[j], result);
						columnList.add(tmpArry[0]+tmpArry[j]);
					}
				}
			}

		}else{
			// 항목 표두일 때
			if("Y".equals(exprYn)){
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 한글 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrEng);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_ID", "["+objVar+"]"+scrKor);
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_ID");
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
			}else{
				// 분류
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);

					scrKor = (String) tmpMap.get("scrKor");		// 한글 분류명
					scrEng = (String) tmpMap.get("scrEng");		// 영문 분류명
					objVar = (String) tmpMap.get("objVarId");	// 분류 코드
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					if(lang == 'e'){
						headerMap.put("OV_L" + varOrdSn + "_NM", scrEng);
					}else{
						headerMap.put("OV_L" + varOrdSn + "_NM", scrKor);
					}
					columnList.add("OV_L" + varOrdSn + "_NM");
				}
			}

			//시점
			if(lang == 'e'){
				headerMap.put("PRD_SE",  "PERIOD" );
			}else{
				headerMap.put("PRD_SE",  "시점" );
			}
			columnList.add("PRD_SE");

			// 항목
			for(int i = 0; i < itmIdList.size(); i++){
				tmpMap = itmIdList.get(i);

				scrKor = (String)tmpMap.get("scrKor"); // 항목명
				scrEng = (String)tmpMap.get("scrEng"); // 영문 항목명
				itmId = (String)tmpMap.get("itmId"); // 코드||항목

				if(lang == 'e'){
					headerMap.put(itmId, scrEng);
				}else{
					headerMap.put(itmId, scrKor);
				}
				columnList.add(itmId);
			}
		}

		list.add(0, headerMap);

		Map listMap = null;

		StringBuffer lineBuff = new StringBuffer();

		int columnListSize = columnList.size();
		int sVSize = 0; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등]

		if("Y".equals(exprYn)){
			sVSize = size * 2; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 3;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize++;
			}
		}else{
			sVSize = size; // 데이터가 아닌 컬럼 사이즈[표측 분류값, 코드 등] (size = 분류갯수)

			if(exprType.equals("1")){	// 시점 표두일때
				sVSize += 2;	// +2는 항목코드,항목명
			}else{	// 항목이 표두일때
				sVSize += 1;
			}
		}

		// csv 데이터 만들기
		for(int i = 0; i<list.size(); i++){
			listMap = list.get(i);

			String colName = null;
			String result = null;

			for(int j = 0; j < columnListSize; j++){

				colName = columnList.get(j);
				result = (String)listMap.get(colName);

				if(i == 0 || sVSize >= (j + 1)){
					if(sVSize == (j+1) && colName != null && colName.equals("PRD_SE")){
						String[] timeArr = result.split("@#");
						if(timeArr.length > 1){
							result = generatePrdDeForDown(timeArr[1], timeArr[0], lang, pi);
						}
					}
					if(sVSize == (j+1) && colName != null && colName.equals("UNIT_NM_KOR")){
						String[] unitArr = result.split("@");
						for(int k = 0;k<unitArr.length; k++){
							String temp = unitArr[k];
							if(!temp.equals("^")){
								result = temp;
								break;
							}else{
								result = "";
							}
						}
					}
					lineBuff.append("\"" + result + "\"");
					
					//2017.11.29 직접다운로드에 TXT형식 추가로인한 구분자 선택
					if( type.equals("csv")){
						lineBuff.append(",");
					}else{
						lineBuff.append("\t");
					}
				}else{
					if(listMap.get(columnList.get(j)) != null){
						String value = ((String)listMap.get(columnList.get(j)));	// columnList.get(j) 컬럼 값

						String[] arr = value.split("#@");

						String[] valArray = new String[4];
						valArray[0] = arr[0];	//DTVAL_CO or WGT_CO
						valArray[1] = arr[1];	//PERIOD_CO

						if(arr.length == 3){
							valArray[2] = arr[2];
						}else{
							valArray[2] = "";
						}
						if(arr.length == 4){
							valArray[3] = arr[3];
						}else{
							valArray[3] = "";
						}

						//result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						/*2014.07.01 만셀넘는 엑셀다운시는 소수점 여부 관련없이 다보여주도록 - 김경호*/
						String value_buff = valArray[0];
						value_buff = value_buff.replaceAll(",", "");

						if( value_buff != null && !value_buff.equals("99999999999.99999") && !value_buff.equals("") ){
							result = valArray[0];
						}else{
							result = StatPivotUtil.getFormatedMeasure(valArray, null, 0);
						}
						/*-------------------------------------------------------*/

						lineBuff.append("\"" + result + "\"");
						
						//2017.11.29 직접다운로드에 TXT형식 추가로인한 구분자 선택
						if( type.equals("csv")){
							lineBuff.append(",");
						}else{
							lineBuff.append("\t");
						}
					}else{
						lineBuff.append("\"" + "" + "\"");
						
						//2017.11.29 직접다운로드에 TXT형식 추가로인한 구분자 선택
						if( type.equals("csv")){
							lineBuff.append(",");
						}else{
							lineBuff.append("\t");
						}
					}
				}
			}
			lineBuff.append("\n");
		}
		return lineBuff.toString();
	}


	private String generateQuery(ParamInfo paramInfo) throws Exception{

		char lang = (paramInfo.getDataOpt().indexOf("en") > -1) ? 'e' : 'k';

		objVarList = statHtmlDAO.getObjVarList(paramInfo); // 분류정보 조회
		List<Map> objVarPubInfo = null;
		if (paramInfo.getDirect().equals("direct")) {
			itmIdList = statHtmlDAO.getDirectItmIdList(paramInfo); //  항목정보 조회
			prdValue = paramInfo.getPrdSe() + "," + paramInfo.getPrdDe() + "@";
			objVarPubInfo = statHtmlDAO.getDirectObjVarPubInfo(paramInfo);
		} else {
			itmIdList = statHtmlDAO.getItmIdList(paramInfo); //  항목정보 조회
			prdValue = statHtmlDAO.getPrdValue(paramInfo); // 시점 정보
		}
		Map tmpMap = new HashMap();
		Map<String, String> paramMap = new HashMap<String, String>();

		String exprType = paramInfo.getDownLargeExprType(); // 1: 시점표두 2: 항목표두
		String downLargeSort = paramInfo.getDownLargeSort(); // 시점 정렬 asc, desc
		//System.out.println("downLargeSort::::::"+downLargeSort);
		String dbUser = paramInfo.getDbUser();
		String orgId = paramInfo.getOrgId();
		String tblId = paramInfo.getTblId();
		String direct = paramInfo.getDirect();
		String sessionId = paramInfo.getSessionId();
		//String condTable = PropertyManager.getInstance().getProperty("table.condition");
		String condTable = PropertyManager.getInstance().getProperty("server.dbuser")+"."+PropertyManager.getInstance().getProperty("table.condition"); //2014.04.29 호스팅 DB이관문제 때문에 TN_STAT_HTML_COND_WEB 관련 수정 - 김경호
		String varOrdSn = "";
		StringBuffer strBuff = new StringBuffer();

		//2014.07.09 이규정 차원단위존재여부 추가
		Map<String, String> tmpExtraMap = new HashMap<String, String>();
		tmpExtraMap.put("orgId", paramInfo.getOrgId());
		tmpExtraMap.put("tblId", paramInfo.getTblId());
		tmpExtraMap.put("dbUser", paramInfo.getDbUser());
		boolean isService = (paramInfo.getServerTypeOrigin().indexOf("service") >= 0) ? true : false;
		tmpExtraMap.put("isService", (isService) ? "Y" : "N");

		Map extraInfoMap = (Map)statHtmlDAO.selectStblInfoExtra(tmpExtraMap);
		String dimUnitYn = (String)extraInfoMap.get("DIM_UNIT_YN");

		//2014.10 TBL_SE, MART_USABLE(NSI_MART.TN_MT_INFO.LST_CHN_TP)
		String tblSe = null;
		String martStatus = null;
		long dimCo = 0;
		String mtId = null;

		if(isService){
			tblSe = StringUtils.defaultString( (String)extraInfoMap.get("TBL_SE") );
			martStatus = StringUtils.defaultString( (String)extraInfoMap.get("MART_STATUS") );
			dimCo = ((BigDecimal)extraInfoMap.get("DIM_CO")).longValue();
			mtId = (String)extraInfoMap.get("MT_ID");
		}
		boolean martUsable = false;
		char martJoinMethod = 'N'; //default NL join

		//마트 사용가능 여부 판단 및 조인방법 결졍
		//System.out.println("############################################ itemMultiply ::: " + paramInfo.getItemMultiply());
		//System.out.println("############################# tblSe ::: " + tblSe + ", martStatus ::: " + martStatus);
		if(isService && tblSe.equals("M") && !martStatus.equals("P")){
			//실제 마트테이블 존재하는지 체크
			try{
				String chkSql = "SELECT 1 FROM NSI_MART." + mtId + " WHERE ROWNUM = 1";
				statHtmlDAO.chkMartExist(chkSql);

				martUsable = true;
				//System.out.println("############################# martUsable ::: " + martUsable);

				//마트 존재 시 join 방법 체크
				//DIM_CO가 1000000 초과하면 NL 수행
				//DIM_CO가 0인경우 무조건 NL 수행
				//직접다운로드는 HASH로 수행하도록 한다.
				if(direct.equals("direct")){
					martJoinMethod = 'H';
				}else{
					if( dimCo != 0 && dimCo <= Long.parseLong(PropertyManager.getInstance().getProperty("mart.join.method")) ){
						long itemMultiply = 0l;
						try{
							itemMultiply = Long.parseLong(paramInfo.getItemMultiply());
							//DIM_CO에 대한 조회조건 항목X분류 조합수가 1%를 초과하면 HASH로 전환
							if( itemMultiply * 100 / dimCo > 1){
									martJoinMethod = 'H';
							}
						}catch(Exception e){}
					}
				}

			}catch(Exception e){
				//log.error(e.getMessage()); //2020.07.21 제니퍼에서 자꾸 에러 카운트 올라간다~ 잡아라~
				log.info(e.getMessage());
			}
		}

		//2014.10 Mart 적용
		String dtAlias = "B.";
		if(martUsable){
			dtAlias = "A.";
		}

		//시점표두
		if(exprType.equals("1")){
		// with 절
			strBuff.append(" WITH TMP AS(    \n");
			if(martUsable){
				if(martJoinMethod == 'H'){
					//strBuff.append(" SELECT     /*+ ordered use_hash(A) */    \n"); //2014-11-21 직접다운로드 쿼리 튜닝
					strBuff.append(" SELECT     /*+ use_hash(A,B) */    \n");
					strBuff.append("	'" + orgId + "' AS ORG_ID,    \n");
					strBuff.append("	'" + tblId + "' AS TBL_ID    \n");
				}else{
					strBuff.append(" SELECT     /*+ use_nl(A) index(A UQ_" + mtId + ") */    \n");
					strBuff.append("	'" + orgId + "' AS ORG_ID,    \n");
					strBuff.append("	'" + tblId + "' AS TBL_ID    \n");
				}
			}else{
				//strBuff.append(" SELECT    /*+  ordered use_hash(B) index(a UQTN_DIM) index(B PKTN_DT)  */    \n"); 
				//strBuff.append(" SELECT    /*+  use_hash(A,B) index(a PKTN_DIM) index(B PKTN_DT)  */    \n");	//2014-11-21 직접다운로드 쿼리 튜닝
				strBuff.append(" SELECT    /*+ leading(a) index(a XPK_TN_DIM) */    \n"); // 2019.04.23 오라클에서 쿼리튜닝해줌
				strBuff.append("	A.ORG_ID    \n");
				strBuff.append("	, A.TBL_ID    \n");
			}

			strBuff.append("	, " + dtAlias + "PRD_SE||" + dtAlias + "PRD_DE AS TIME    \n");

			int classListSize = classListForWhereClause.size();
			if(objVarList != null){

				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append("	, A.OV_L"+ varOrdSn +"_ID     \n");
					strBuff.append("	, ITM"+ varOrdSn +".CHAR_ITM_SN AS OV_L"+ varOrdSn +"_SN     \n");
				}
			}
			strBuff.append("        , A.CHAR_ITM_ID    \n");
			strBuff.append("        , ITM0.CHAR_ITM_SN    \n");
			strBuff.append("        , DECODE(" + dtAlias + "DTVAL_CO, NULL, " + dtAlias + "DTVAL_CN, " + dtAlias + "DTVAL_CO)     \n");
			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("        ||'#@'||NVL(DECODE( (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			else
				strBuff.append("        ||'#@'||NVL(DECODE( (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			strBuff.append("        , NULL    \n");
			strBuff.append("        , DECODE(A.PERIOD_CO, NULL, (SELECT PERIOD_CO FROM " + dbUser + "TN_ITM_LIST WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID), A.PERIOD_CO)    \n");
			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("        , (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			else
				strBuff.append("        , (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			strBuff.append("        ), 0)||'#@'|| " + dtAlias + "SMBL_CN AS DTVAL_CO	\n");

			//2014.07.09 차원단위존재여부 추가
			if (direct.equals("direct")) {
				if(dimUnitYn.equals("Y")){
					strBuff.append("	    , NVL(DECODE(A.UNIT_ID, NULL, NULL, (SELECT CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = A.UNIT_ID)), '^')  ");
				}else{
					strBuff.append("	    , '^'  ");
				}
			}else{
				if(dimUnitYn.equals("Y")){
					strBuff.append("	    , NVL(DECODE(A.UNIT_ID, NULL, NULL, (SELECT NVL(DECODE('" + lang + "', 'e', CD_ENG_NM, CD_NM), '') FROM " + dbUser + "TC_UNIT WHERE CD_ID = A.UNIT_ID)), '^')  ");
				}else{
					strBuff.append("	    , '^'  ");
				}
			}

			for(int q = 0; q < classListSize; q++){
				String varSn = classListForWhereClause.get(q);
				varSn = varSn.substring(4, 5);
				strBuff.append("		  || '@' || NVL(ITM" + varSn + ".ITM_UNIT_NM_"+ varSn +", '^')          ");
			}
			strBuff.append(" 		|| '@' ||NVL(ITM_UNIT_NM, '^')    \n");
			strBuff.append(" AS UNIT_NM_KOR    \n");
			strBuff.append("  FROM  (   \n");
			if (direct.equals("direct")) {
				strBuff.append("        			   SELECT ITM.ITM_ID AS CHAR_ITM_ID  	\n");
			} else {
				//strBuff.append("        			   SELECT COND.TARGET_VALUE AS CHAR_ITM_ID  	\n"); 
				strBuff.append("        			   SELECT /*+ no_merge */ COND.TARGET_VALUE AS CHAR_ITM_ID  	\n"); // 2019.04.23 오라클에서 쿼리튜닝해줌
			}
			strBuff.append("        					  , ITM.UNIT_ID       	\n");
			if (direct.equals("direct")) {
				strBuff.append("        					  , (SELECT CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID ) AS ITM_UNIT_NM       	\n");
				strBuff.append("        					  , ITM.CHAR_ITM_SN       	\n");
				strBuff.append("        				 FROM  " + dbUser + "TN_ITM_LIST ITM     	\n");
			} else {
				strBuff.append("        					  , (SELECT NVL(DECODE('" + lang + "', 'e', CD_ENG_NM, CD_NM), '') FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID ) AS ITM_UNIT_NM       	\n");
				strBuff.append("        					  , ITM.CHAR_ITM_SN       	\n");
				strBuff.append("        				 FROM  " + condTable + " COND,     	\n");
				strBuff.append("        					   " + dbUser + "TN_ITM_LIST ITM     	\n");
			}
			strBuff.append("        			    WHERE ITM.OBJ_VAR_ID = '13999001'     	\n");
			strBuff.append("        					  AND ITM.ORG_ID = '" + orgId + "'     	\n");
			strBuff.append("        					  AND ITM.TBL_ID = '" + tblId + "'	     	\n");

			if (!direct.equals("direct")) {
				strBuff.append("           					  AND ITM.ITM_ID = COND.TARGET_VALUE  	\n");
				strBuff.append("             				  AND COND.SESSION_ID = '" + sessionId + "'  	\n");
				strBuff.append("             				  AND COND.TARGET_ID = 'ITM_ID'	\n");
			} else {
				//2015.08.10 상속통계표 (pub_se_type = 1 원통계표 공표 , else 미지정 - 데이터 맞추긴 했는데 혹시나~) 
				if( "Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType())){
					strBuff.append("           					  AND ITM.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
				}else{
					if(paramInfo.getServerType().equals("service_en")){
						strBuff.append("           					  AND ITM.PUB_SE IN ('1210110', '1210114')	\n");
					}else if(paramInfo.getServerType().equals("stat")){
						strBuff.append("           					  AND ITM.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
					}else if(paramInfo.getServerType().equals("stat_emp")){
						strBuff.append("           					  AND ITM.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
					}else{
						strBuff.append("           					  AND ITM.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
					}
				}
			}

			strBuff.append("             			) ITM0, 	\n");

			if (direct.equals("direct")) {
				int classPubListSize = objVarPubInfo.size();
				for(int q = 0; q < classPubListSize; q++){

					Map obj_info = objVarPubInfo.get(q);
					String varSn = obj_info.get("targetId").toString();
					varSn = varSn.substring(4, 5);
					String objpub = obj_info.get("pubSe").toString();

					//2015.08.10 상속통계표 (pub_se_type = 1 원통계표 공표 , else 미지정 - 데이터 맞추긴 했는데 혹시나~) 
					if( "Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType())){
						objpub = "1210110";
					}
					strBuff.append("            			( SELECT ITM.ITM_ID AS "+ obj_info.get("targetId").toString() + "	\n");
					strBuff.append("             				   , ITM.UNIT_ID	\n");
					strBuff.append("        					  , (SELECT CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID ) AS ITM_UNIT_NM_" + varSn + "       	\n");
					strBuff.append("        					  , ITM.CHAR_ITM_SN              	\n");
					strBuff.append("             			  FROM " + dbUser + "TN_ITM_LIST ITM,	\n");
					strBuff.append("             				   " + dbUser + "TN_OBJ_ITM_CLS CLS	\n");
					strBuff.append("            			 WHERE ITM.ORG_ID = CLS.ORG_ID 	\n");
					strBuff.append("            				   AND ITM.TBL_ID = CLS.TBL_ID 	\n");
					strBuff.append("            				   AND ITM.OBJ_VAR_ID = CLS.OBJ_VAR_ID 	\n");
					strBuff.append("             				   AND CLS.VAR_ORD_SN = '" + varSn + "' \n");
					strBuff.append("           					   AND ITM.ORG_ID= '" + orgId + "'  	\n");
					strBuff.append("            				   AND ITM.TBL_ID = '" + tblId + "'	 	\n");
					strBuff.append("            				   AND ITM.OBJ_VAR_ID != '13999001'	\n");
					if(paramInfo.getServerType().equals("service_en")){
						if(objpub.equals("1210110")||objpub.equals("1210114")) {
							strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210114')	\n");
						} else {
							strBuff.append("            				   AND ITM.FTN_VAL_AT = 'Y' \n");
						}
					}else if(paramInfo.getServerType().equals("stat")){
						if(objpub.equals("1210110")||objpub.equals("1210112")||objpub.equals("1210113")||objpub.equals("1210114")) {
							strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
						} else {
							strBuff.append("            				   AND ITM.FTN_VAL_AT = 'Y' \n");
						}
					}else if(paramInfo.getServerType().equals("stat_emp")){
						strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
					}else{
						if(objpub.equals("1210110")||objpub.equals("1210113")||objpub.equals("1210114")) {
							strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
						} else {
							strBuff.append("            				   AND ITM.FTN_VAL_AT = 'Y' \n");
						}
					}

					if(classPubListSize != (q + 1)){
						strBuff.append("           			   ) ITM" + varSn + ", 	\n");
					}else{
						strBuff.append("           			   ) ITM" + varSn + " 	\n");
					}
				}
			} else {
				for(int q = 0; q < classListSize; q++){

					String varSn = classListForWhereClause.get(q);
					varSn = varSn.substring(4, 5);
					//strBuff.append("            			( SELECT COND.TARGET_VALUE AS "+ (String)classListForWhereClause.get(q) + "	\n");  
					strBuff.append("            			( SELECT /*+ no_merge */ COND.TARGET_VALUE AS "+ (String)classListForWhereClause.get(q) + "	\n"); // 2019.04.23 오라클에서 쿼리튜닝해줌
					strBuff.append("             				   , ITM.UNIT_ID	\n");
					strBuff.append("        					  , (SELECT NVL(DECODE('" + lang + "', 'e', CD_ENG_NM, CD_NM), '') FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID ) AS ITM_UNIT_NM_" + varSn + "       	\n");
					strBuff.append("        					  , ITM.CHAR_ITM_SN              	\n");
					strBuff.append("             			  FROM  " + condTable + " COND,	\n");
					strBuff.append("             			  	   " + dbUser + "TN_ITM_LIST ITM,	\n");
					strBuff.append("             				   " + dbUser + "TN_OBJ_ITM_CLS CLS	\n");
					strBuff.append("            			 WHERE ITM.ORG_ID = CLS.ORG_ID 	\n");
					strBuff.append("            				   AND ITM.TBL_ID = CLS.TBL_ID 	\n");
					strBuff.append("            				   AND ITM.OBJ_VAR_ID = CLS.OBJ_VAR_ID 	\n");
					strBuff.append("             				   AND CLS.VAR_ORD_SN = '" + varSn + "' \n");
					strBuff.append("           					   AND ITM.ORG_ID= '" + orgId + "'  	\n");
					strBuff.append("            				   AND ITM.TBL_ID = '" + tblId + "'	 	\n");
					strBuff.append("            				   AND ITM.ITM_ID = COND.TARGET_VALUE	\n");
					strBuff.append("             				   AND COND.SESSION_ID = '" + sessionId + "' \n");
					strBuff.append("     						   AND COND.TARGET_ID = '" + (String)classListForWhereClause.get(q) + "'   	\n");

					if(classListSize != (q + 1)){
						strBuff.append("           			   ) ITM" + varSn + ", 	\n");
					}else{
						strBuff.append("           			   ) ITM" + varSn + " 	\n");
					}
				}
			}

			if(martUsable){
				if(martJoinMethod == 'H'){
					strBuff.append(" 		, (\n");
					strBuff.append(" 			SELECT /*+ INDEX(B IDX01_" + mtId + ") */ * \n");
					strBuff.append(" 			FROM NSI_MART." + mtId + " B \n");
					strBuff.append("        	WHERE (\n");
					prdArry = prdValue.split("@");
					for(int i = 0; i < prdArry.length; i++){
						String[] tmpArry = prdArry[i].split(",");

						if(i != 0){
							strBuff.append(" OR \n ");
						}
						strBuff.append("(PRD_SE = '" + tmpArry[0] + "' AND PRD_DE IN (");

						for(int j = 1; j < tmpArry.length; j++){
							//if(i == 0 && j == 1){
							if(j == 1){
								//strBuff.append("'"+ tmpArry[0] + tmpArry[j] +"'");
								strBuff.append("'"+ tmpArry[j] +"'");
							}else{
								strBuff.append(", '"+ tmpArry[j] +"'");
							}

						}
						strBuff.append("))");
					}
					strBuff.append("        	)\n");
					//공표구분
					if(paramInfo.getServerType().equals("service_en")){
						strBuff.append("        	AND PUB_SE_DIM IN ('1210110', '1210114')	\n");
						strBuff.append("        	AND PUB_SE_DT IN ('1210110', '1210114')	\n");
					}else{
						strBuff.append("        	AND PUB_SE_DIM IN ('1210110', '1210113', '1210114')	\n");
						strBuff.append("        	AND PUB_SE_DT IN ('1210110', '1210113', '1210114')	\n");
					}
					strBuff.append("  ) A  \n");
					strBuff.append("  WHERE  \n");
					//항목, 분류값 조건
					strBuff.append("         A.CHAR_ITM_ID = ITM0.CHAR_ITM_ID	\n");
					if (direct.equals("direct")) {
						int classPubListSize = objVarPubInfo.size();
						for(int p = 0; p < classPubListSize; p++){
							Map obj_info = objVarPubInfo.get(p);
							String varSnP = obj_info.get("targetId").toString();
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + obj_info.get("targetId").toString() + " = ITM" + varSnP + "." + obj_info.get("targetId").toString() + " \n");
						}
					} else {
						for(int p = 0; p < classListSize; p++){
							String varSnP = classListForWhereClause.get(p);
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + (String)classListForWhereClause.get(p) + " = ITM" + varSnP + "." + (String)classListForWhereClause.get(p) + " \n");
						}
					}
				}else{
					strBuff.append("         , NSI_MART." + mtId + " A    	\n");
					strBuff.append("  WHERE  \n");
					//항목, 분류값 조건
					strBuff.append("         A.CHAR_ITM_ID = ITM0.CHAR_ITM_ID	\n");
					if (direct.equals("direct")) {
						int classPubListSize = objVarPubInfo.size();
						for(int p = 0; p < classPubListSize; p++){
							Map obj_info = objVarPubInfo.get(p);
							String varSnP = obj_info.get("targetId").toString();
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + obj_info.get("targetId").toString() + " = ITM" + varSnP + "." + obj_info.get("targetId").toString() + " \n");
						}
					} else {
						for(int p = 0; p < classListSize; p++){
							String varSnP = classListForWhereClause.get(p);
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + (String)classListForWhereClause.get(p) + " = ITM" + varSnP + "." + (String)classListForWhereClause.get(p) + " \n");
						}
					}
					//2014.07.09 이규정 sql 변경
					strBuff.append("		 AND (	\n");
					prdArry = prdValue.split("@");
					for(int i = 0; i < prdArry.length; i++){
						String[] tmpArry = prdArry[i].split(",");

						if(i != 0){
							strBuff.append(" OR \n ");
						}
						strBuff.append("(A.PRD_SE = '" + tmpArry[0] + "' AND A.PRD_DE IN (");

						for(int j = 1; j < tmpArry.length; j++){
							//if(i == 0 && j == 1){
							if(j == 1){
								//strBuff.append("'"+ tmpArry[0] + tmpArry[j] +"'");
								strBuff.append("'"+ tmpArry[j] +"'");
							}else{
								strBuff.append(", '"+ tmpArry[j] +"'");
							}

						}
						strBuff.append("))");
					}
					strBuff.append(" )	\n");

					//2015.06.16 상속통계표 공표 체크
					if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
						//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
					}else{
						// TN_DIM, TN_DT 공표구분 체크
						if(paramInfo.getServerType().equals("service_en")){
							strBuff.append("        	AND A.PUB_SE_DIM IN ('1210110', '1210114')	\n");
							strBuff.append("        	AND A.PUB_SE_DT IN ('1210110', '1210114')	\n");
						}else{
							strBuff.append("        	AND A.PUB_SE_DIM IN ('1210110', '1210113', '1210114')	\n");
							strBuff.append("        	AND A.PUB_SE_DT IN ('1210110', '1210113', '1210114')	\n");
						}
					}
				}
			}else{
				strBuff.append("         , " + dbUser + "TN_DIM A,  " + dbUser + "TN_DT B    	\n");
				//2015.08.10 상속통계표
				if("Y".equals(paramInfo.getInheritYn()) ){
					strBuff.append("  WHERE  B.ORG_ID = '" + paramInfo.getOriginOrgId() + "'    	\n");
					strBuff.append("    AND  B.TBL_ID = '" + paramInfo.getOriginTblId() + "'     	\n");
				}else{
					strBuff.append("  WHERE  A.ORG_ID = B.ORG_ID    	\n");
					strBuff.append("         AND A.TBL_ID = B.TBL_ID    	\n");
				}
				//항목, 분류값 조건
				strBuff.append("         AND A.CHAR_ITM_ID = ITM0.CHAR_ITM_ID	\n");
				if (direct.equals("direct")) {
					int classPubListSize = objVarPubInfo.size();
					for(int p = 0; p < classPubListSize; p++){
						Map obj_info = objVarPubInfo.get(p);
						String varSnP = obj_info.get("targetId").toString();
						varSnP = varSnP.substring(4, 5);
						strBuff.append("        AND A." + obj_info.get("targetId").toString() + " = ITM" + varSnP + "." + obj_info.get("targetId").toString() + " \n");
					}
				} else {
					for(int p = 0; p < classListSize; p++){
						String varSnP = classListForWhereClause.get(p);
						varSnP = varSnP.substring(4, 5);
						strBuff.append("        AND A." + (String)classListForWhereClause.get(p) + " = ITM" + varSnP + "." + (String)classListForWhereClause.get(p) + " \n");
					}
				}
				strBuff.append("         AND A.ITM_RCGN_SN = B.ITM_RCGN_SN   	\n");
				strBuff.append("         AND A.ORG_ID = '" + orgId + "'    	\n");
				strBuff.append("         AND A.TBL_ID = '" + tblId + "'	\n");
				//2014.07.09 이규정 sql 변경
				//strBuff.append("         AND B.PRD_SE||B.PRD_DE IN ( ");
				strBuff.append("		 AND (	\n");
				prdArry = prdValue.split("@");
				for(int i = 0; i < prdArry.length; i++){
					String[] tmpArry = prdArry[i].split(",");

					if(i != 0){
						strBuff.append(" OR \n ");
					}
					strBuff.append("(B.PRD_SE = '" + tmpArry[0] + "' AND B.PRD_DE IN (");

					for(int j = 1; j < tmpArry.length; j++){
						//if(i == 0 && j == 1){
						if(j == 1){
							//strBuff.append("'"+ tmpArry[0] + tmpArry[j] +"'");
							strBuff.append("'"+ tmpArry[j] +"'");
						}else{
							strBuff.append(", '"+ tmpArry[j] +"'");
						}

					}
					strBuff.append("))");
				}
				strBuff.append(" )	\n");

				//2015.06.16 상속통계표 공표 체크
				if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
					//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
				}else{
					// TN_DIM, TN_DT 공표구분 체크
					if(paramInfo.getServerType().equals("service_en")){
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210114')	\n");
					}else if(paramInfo.getServerType().equals("stat")){
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
					}else if(paramInfo.getServerType().equals("stat_emp")){
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
					}else{
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
					}
				}
			}

			strBuff.append("         )  \n"); //end of with clause

			strBuff.append("SELECT ORG_ID, TBL_ID, UNIT_NM_KOR	\n");
			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append("	,OV_L" + varOrdSn + "_ID	\n");
					strBuff.append("	,OV_L" + varOrdSn + "_NM	\n");
				}
			}
			strBuff.append("	,CHAR_ITM_ID, CHAR_ITM_NM  \n");


			if(downLargeSort.equals("asc")){
				for(int i = 0; i < prdArry.length; i++){
					String[] tmpArry = prdArry[i].split(",");
					for(int j = tmpArry.length - 1; j > 0; j--){
						strBuff.append("        , \""+ tmpArry[0]+tmpArry[j] +"\"    	\n");
					}
				}
			}else{
				for(int i = prdArry.length - 1; i >= 0; i--){
					String[] tmpArry = prdArry[i].split(",");
					for(int j = 1; j < tmpArry.length; j++){
						strBuff.append("        , \""+ tmpArry[0]+tmpArry[j] +"\"    	\n");
					}
				}
			}


			strBuff.append(" FROM ( \n");
			strBuff.append("        	SELECT ORG_ID, TBL_ID ,UNIT_NM_KOR	\n");
			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append("			, OV_L"+ varOrdSn +"_ID	\n");
					strBuff.append("			, OV_L" + varOrdSn + "_SN    \n");
					//strBuff.append("			, (SELECT NVL(DECODE('" + lang + "', 'e', SCR_ENG, SCR_KOR), '') AS SCR_KOR FROM  " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = (SELECT OBJ_VAR_ID FROM  " + dbUser + "TN_OBJ_ITM_CLS WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND VAR_ORD_SN = '" + varOrdSn +"') AND ITM_ID = A.OV_L" + varOrdSn + "_ID) AS OV_L" + varOrdSn + "_NM	\n");
					// 2019.04.23 오라클에서 쿼리튜닝해줌
					strBuff.append("			, (SELECT /*+ index(TN_ITM_LIST XPKTN_ITM_LIST) */ NVL(DECODE('" + lang + "', 'e', SCR_ENG, SCR_KOR), '') AS SCR_KOR FROM  " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = (SELECT OBJ_VAR_ID FROM  " + dbUser + "TN_OBJ_ITM_CLS WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND VAR_ORD_SN = '" + varOrdSn +"') AND ITM_ID = A.OV_L" + varOrdSn + "_ID) AS OV_L" + varOrdSn + "_NM	\n");
				}
			}

			strBuff.append("	    , CHAR_ITM_SN	\n");

			if (direct.equals("direct")) {
				strBuff.append("		, CASE WHEN (SELECT UNIT_ID FROM " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID) IS NOT NULL \n");
				strBuff.append("			   THEN (SELECT CHAR_ITM_ID || '[' || CD_ID || ']' FROM " + dbUser + "TC_UNIT S1, " + dbUser + "TN_ITM_LIST S2 WHERE S1.CD_ID = S2.UNIT_ID AND S2.ORG_ID = A.ORG_ID AND S2.TBL_ID = A.TBL_ID AND S2.OBJ_VAR_ID = '13999001' AND S2.ITM_ID =A.CHAR_ITM_ID) \n");
				strBuff.append("			   ELSE CHAR_ITM_ID  \n");
				strBuff.append("		  END AS CHAR_ITM_ID  \n");
				strBuff.append("		, CASE WHEN (SELECT UNIT_ID FROM " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID) IS NOT NULL \n");
				strBuff.append("			   THEN (SELECT NVL(DECODE('"+lang+"', 'e', SCR_ENG, SCR_KOR ), '') || '[' || CD_NM || ']' FROM " + dbUser + "TC_UNIT, " + dbUser + "TN_ITM_LIST S2 WHERE CD_ID = S2.UNIT_ID AND S2.ORG_ID = A.ORG_ID AND S2.TBL_ID = A.TBL_ID AND S2.OBJ_VAR_ID = '13999001' AND S2.ITM_ID =A.CHAR_ITM_ID) \n");
				strBuff.append("			   ELSE (SELECT NVL(DECODE('"+lang+"', 'e', SCR_ENG, SCR_KOR ), '') FROM    " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID )  \n");
				strBuff.append("		  END AS CHAR_ITM_NM  \n");
			} else {
				strBuff.append("		, CHAR_ITM_ID	  \n");
				//strBuff.append("	    , (SELECT NVL(DECODE('"+lang+"', 'e', SCR_ENG, SCR_KOR ), '') FROM    " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID ) AS CHAR_ITM_NM	\n");
				// 2019.04.23 오라클에서 쿼리튜닝해줌
				strBuff.append("	    , (SELECT /*+ index(TN_ITM_LIST XPKTN_ITM_LIST) */ NVL(DECODE('"+lang+"', 'e', SCR_ENG, SCR_KOR ), '') FROM    " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID ) AS CHAR_ITM_NM	\n");
			}

			for(int i = 0; i < prdArry.length; i++){
				String[] tmpArry = prdArry[i].split(",");
				for(int j = 1; j < tmpArry.length; j++){
					strBuff.append("	    , MAX(DECODE(TIME, '" + tmpArry[0]+tmpArry[j] + "', DTVAL_CO, NULL)) AS \"" + tmpArry[0]+tmpArry[j] + "\" \n");
				}
			}
			strBuff.append("	  FROM tmp A	\n");

			// GROUP BY
			strBuff.append("	 GROUP BY ORG_ID, TBL_ID, UNIT_NM_KOR,	\n");
//			strBuff.append("	 GROUP BY ORG_ID, TBL_ID, 	\n");

			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					//-----
					strBuff.append("		 OV_L" + varOrdSn + "_ID, ");
					strBuff.append("		 OV_L" + varOrdSn + "_SN, ");
					//-----
				}
			}
			//-----
			//strBuff.append("CHAR_ITM_ID )	\n");
			strBuff.append("CHAR_ITM_ID, 	\n");
			strBuff.append("CHAR_ITM_SN 	\n");

			// ORDER BY
			strBuff.append("ORDER BY ");
			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append(" OV_L" + varOrdSn + "_SN ,OV_L" + varOrdSn + "_ID, ");
				}
			}
			strBuff.append("CHAR_ITM_SN, CHAR_ITM_ID \n");
			strBuff.append("            )	\n");

		// 항목 표두
		}else{
			// with 절
			strBuff.append(" WITH TMP AS(    \n");
			if(martUsable){
				if(martJoinMethod == 'H'){
					strBuff.append(" SELECT     /*+ ordered use_hash(A) */    \n");
					strBuff.append("	'" + orgId + "' AS ORG_ID,    \n");
					strBuff.append("	'" + tblId + "' AS TBL_ID    \n");
				}else{
					strBuff.append(" SELECT     /*+ use_nl(A) index(A UQ_" + mtId + ") */    \n");
					strBuff.append("	'" + orgId + "' AS ORG_ID,    \n");
					strBuff.append("	'" + tblId + "' AS TBL_ID    \n");
				}
			}else{
				//strBuff.append(" SELECT    /*+ ordered use_hash(B) index(a UQTN_DIM) index(B PKTN_DT) */    \n");
				// 2019.04.23 오라클에서 쿼리튜닝해줌
				strBuff.append(" SELECT /*+ leading(a) index(a UQTN_DIM) */    \n");
				strBuff.append("	A.ORG_ID    \n");
				strBuff.append("	, A.TBL_ID    \n");
			}
			strBuff.append("	 , CASE   \n");
			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("		WHEN  (SELECT BB.PRD_DETAIL FROM " + dbUser + "TC_PRD_DETAIL AA, " + dbUser + "TN_STBL_RECD_INFO BB WHERE AA.PRD_DETAIL = BB.PRD_DETAIL AND '" + paramInfo.getOriginOrgId() + "' = BB.ORG_ID AND '" + paramInfo.getOriginTblId() + "' = BB.TBL_ID AND " + dtAlias + "PRD_SE = 'F') = 'IR' THEN    \n");
			else
				strBuff.append("		WHEN  (SELECT BB.PRD_DETAIL FROM " + dbUser + "TC_PRD_DETAIL AA, " + dbUser + "TN_STBL_RECD_INFO BB WHERE AA.PRD_DETAIL = BB.PRD_DETAIL AND '" + orgId + "' = BB.ORG_ID AND '" + tblId + "' = BB.TBL_ID AND " + dtAlias + "PRD_SE = 'F') = 'IR' THEN    \n");
			if(lang == 'e'){
				strBuff.append("		'Irregularly' || " + dtAlias + "PRD_DE   \n");
			}else{
				strBuff.append("		'부정기' || " + dtAlias + "PRD_DE   \n");
			}

			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("		WHEN  " + dtAlias + "PRD_SE = 'F' AND (SELECT BB.PRD_DETAIL FROM " + dbUser + "TC_PRD_DETAIL AA, " + dbUser + "TN_STBL_RECD_INFO BB WHERE AA.PRD_DETAIL = BB.PRD_DETAIL AND '" + paramInfo.getOriginOrgId() + "' = BB.ORG_ID AND '" + paramInfo.getOriginTblId() + "' = BB.TBL_ID AND " + dtAlias + "PRD_SE = 'F') IS NULL THEN   \n");
			else
				strBuff.append("		WHEN  " + dtAlias + "PRD_SE = 'F' AND (SELECT BB.PRD_DETAIL FROM " + dbUser + "TC_PRD_DETAIL AA, " + dbUser + "TN_STBL_RECD_INFO BB WHERE AA.PRD_DETAIL = BB.PRD_DETAIL AND '" + orgId + "' = BB.ORG_ID AND '" + tblId + "' = BB.TBL_ID AND " + dtAlias + "PRD_SE = 'F') IS NULL THEN   \n");
			if(lang == 'e'){
				strBuff.append("		'Irregularly' || " + dtAlias + "PRD_DE   \n");
			}else{
				strBuff.append("		'부정기' || " + dtAlias + "PRD_DE   \n");
			}
			
			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("	    WHEN  (SELECT BB.PRD_DETAIL FROM " + dbUser + "TC_PRD_DETAIL AA, " + dbUser + "TN_STBL_RECD_INFO BB WHERE AA.PRD_DETAIL = BB.PRD_DETAIL AND '" + paramInfo.getOriginOrgId() + "' = BB.ORG_ID AND '" + paramInfo.getOriginTblId() + "' = BB.TBL_ID AND " + dtAlias + "PRD_SE = 'F') != 'IR'  THEN   \n");
			else
				strBuff.append("	    WHEN  (SELECT BB.PRD_DETAIL FROM " + dbUser + "TC_PRD_DETAIL AA, " + dbUser + "TN_STBL_RECD_INFO BB WHERE AA.PRD_DETAIL = BB.PRD_DETAIL AND '" + orgId + "' = BB.ORG_ID AND '" + tblId + "' = BB.TBL_ID AND " + dtAlias + "PRD_SE = 'F') != 'IR'  THEN   \n");
			if(lang == 'e'){
				strBuff.append("	    " + dtAlias + "PRD_DE||'Year'   \n");
			}else{
				strBuff.append("	    " + dtAlias + "PRD_DE||'년'   \n");
			}
			strBuff.append("		ELSE  " + dtAlias + "PRD_SE||'@#'||" + dtAlias + "PRD_DE   \n");
			strBuff.append("	 END PRD_SE   \n");
			strBuff.append("	, PRD_SE AS PRD_SE_ORD   \n");
			strBuff.append("	, " + dtAlias + "PRD_SE||" + dtAlias + "PRD_DE AS TIME    \n");

			int classListSize = classListForWhereClause.size();

			if(objVarList != null){

				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));
					strBuff.append("	, ITM"+ varOrdSn +".CHAR_ITM_SN AS OV_L"+ varOrdSn +"_SN     \n");
					strBuff.append("	, A.OV_L"+ varOrdSn +"_ID     \n");
				}
			}
			strBuff.append("        , A.CHAR_ITM_ID    \n");
			strBuff.append("        , DECODE(" + dtAlias + "DTVAL_CO, NULL, " + dtAlias + "DTVAL_CN, " + dtAlias + "DTVAL_CO)     \n");
			
			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("        ||'#@'||NVL(DECODE( (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			else
				strBuff.append("        ||'#@'||NVL(DECODE( (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			strBuff.append("        , NULL    \n");
			strBuff.append("        , DECODE(A.PERIOD_CO, NULL, (SELECT PERIOD_CO FROM " + dbUser + "TN_ITM_LIST WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND OBJ_VAR_ID = '13999001' AND ITM_ID = A.CHAR_ITM_ID), A.PERIOD_CO)    \n");

			//2015.08.10 상속통계표 
			if( "Y".equals(paramInfo.getInheritYn()) )
				strBuff.append("        , (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + paramInfo.getOriginOrgId() + "' AND TBL_ID = '" + paramInfo.getOriginTblId() + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			else
				strBuff.append("        , (SELECT PERIOD_CO FROM " + dbUser + "TN_RECD_PRD WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND  PRD_SE = " + dtAlias + "PRD_SE AND PRD_DE = " + dtAlias + "PRD_DE)    \n");
			//2014.07.09 차원단위존재여부 추가
			if (direct.equals("direct")) {
				if(dimUnitYn.equals("Y")){
					strBuff.append("        ), 0)||'#@'|| " + dtAlias + "SMBL_CN || '#@' || DECODE((SELECT UNIT_ID FROM " + dbUser + "TN_DIM WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND ITM_RCGN_SN = A.ITM_RCGN_SN), NULL, NULL, (SELECT '[' || CD_NM || ']' FROM  " + dbUser + "TC_UNIT WHERE CD_ID = (SELECT UNIT_ID FROM " + dbUser + "TN_DIM WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND ITM_RCGN_SN = A.ITM_RCGN_SN))) AS DTVAL_CO	\n");
				}else{
					strBuff.append("        ), 0)||'#@'|| " + dtAlias + "SMBL_CN || '#@' || '' AS DTVAL_CO	\n");
				}
			}else{
				if(dimUnitYn.equals("Y")){
					strBuff.append("        ), 0)||'#@'|| " + dtAlias + "SMBL_CN || '#@' || DECODE((SELECT UNIT_ID FROM " + dbUser + "TN_DIM WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND ITM_RCGN_SN = A.ITM_RCGN_SN), NULL, NULL, (SELECT '[' || NVL(DECODE('" + lang + "', 'e', CD_ENG_NM, CD_NM), '') || ']' FROM  " + dbUser + "TC_UNIT WHERE CD_ID = (SELECT UNIT_ID FROM " + dbUser + "TN_DIM WHERE ORG_ID = '" + orgId + "' AND TBL_ID = '" + tblId + "' AND ITM_RCGN_SN = A.ITM_RCGN_SN))) AS DTVAL_CO	\n");
				}else{
					strBuff.append("        ), 0)||'#@'|| " + dtAlias + "SMBL_CN || '#@' || '' AS DTVAL_CO	\n");
				}
			}
			strBuff.append("  		, CHAR_UNIT_NM   \n");
			if(objVarList != null){

				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append("	, OV_L"+ varOrdSn +"_UNIT_NM  \n");
				}
			}
			strBuff.append("  FROM  (   \n");

			if (direct.equals("direct")) {
				strBuff.append("          SELECT ITM.ITM_ID AS CHAR_ITM_ID  	\n");
				strBuff.append("               , ITM.CHAR_ITM_SN AS CHAR_ITM_SN	\n");
				strBuff.append("        	   , ITM.UNIT_ID, (SELECT CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID) AS CHAR_UNIT_NM          	\n");
			} else {
				//strBuff.append("          SELECT COND.TARGET_VALUE AS CHAR_ITM_ID  	\n");
				strBuff.append("          SELECT /*+ no_merge */ COND.TARGET_VALUE AS CHAR_ITM_ID  	\n"); // 2019.04.23 오라클에서 쿼리튜닝해줌
				strBuff.append("               , ITM.CHAR_ITM_SN AS CHAR_ITM_SN	\n");
				strBuff.append("        	   , ITM.UNIT_ID, (SELECT NVL(DECODE('" + lang + "', 'e', CD_ENG_NM, CD_NM), '') FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID) AS CHAR_UNIT_NM          	\n");
			}

			if (direct.equals("direct")) {
				strBuff.append("            FROM " + dbUser + "TN_ITM_LIST ITM     	\n");
			} else {
				strBuff.append("            FROM  " + condTable + " COND,     	\n");
				strBuff.append("          		" + dbUser + "TN_ITM_LIST ITM     	\n");
			}

			strBuff.append("           WHERE ITM.OBJ_VAR_ID = '13999001'     	\n");
			strBuff.append("             AND ITM.ORG_ID = '" + orgId + "'     	\n");
			strBuff.append("             AND ITM.TBL_ID = '" + tblId + "'	     	\n");

			if (!direct.equals("direct")) {
				strBuff.append("             AND ITM.ITM_ID = COND.TARGET_VALUE  	\n");
				strBuff.append("             AND COND.SESSION_ID = '" + sessionId + "'  	\n");
				strBuff.append("             AND COND.TARGET_ID = 'ITM_ID'	\n");
			}

			strBuff.append("           ) ITM0, 	\n");

			if (direct.equals("direct")) {
				int classPubListSize = objVarPubInfo.size();
				for(int q = 0; q < classPubListSize; q++){
					Map obj_info = objVarPubInfo.get(q);
					String varSn = obj_info.get("targetId").toString();
					varSn = varSn.substring(4, 5);
					String objpub = obj_info.get("pubSe").toString();
					strBuff.append("           ( SELECT ITM.ITM_ID AS "+ obj_info.get("targetId").toString() + "	\n");
					strBuff.append("                  , ITM.CHAR_ITM_SN AS CHAR_ITM_SN	\n");
					strBuff.append("             	  , ITM.UNIT_ID, (SELECT CD_NM FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID) AS OV_L"+ varSn +"_UNIT_NM	\n");
					strBuff.append("               FROM " + dbUser + "TN_ITM_LIST ITM,	\n");
					strBuff.append("             		" + dbUser + "TN_OBJ_ITM_CLS CLS	\n");
					strBuff.append("              WHERE ITM.ORG_ID = CLS.ORG_ID 	\n");
					strBuff.append("            	AND ITM.TBL_ID = CLS.TBL_ID 	\n");
					strBuff.append("            	AND ITM.OBJ_VAR_ID = CLS.OBJ_VAR_ID 	\n");
					strBuff.append("             	AND CLS.VAR_ORD_SN = '" + varSn + "' \n");
					strBuff.append("           		AND ITM.ORG_ID= '" + orgId + "'  	\n");
					strBuff.append("            	AND ITM.TBL_ID = '" + tblId + "'	 	\n");


					//2015.06.16 상속통계표 공표 체크
					if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
						//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
						strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
					}else{ 
						if(paramInfo.getServerType().equals("service_en")){
							if(objpub.equals("1210110")||objpub.equals("1210114")) {
								strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210114')	\n");
							} else {
								strBuff.append("            				   AND ITM.FTN_VAL_AT = 'Y' \n");
							}
						}else if(paramInfo.getServerType().equals("stat")){
							if(objpub.equals("1210110")||objpub.equals("1210112")||objpub.equals("1210113")||objpub.equals("1210114")) {
								strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
							} else {
								strBuff.append("            				   AND ITM.FTN_VAL_AT = 'Y' \n");
							}
						}else if(paramInfo.getServerType().equals("stat_emp")){
							strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
						}else{
							if(objpub.equals("1210110")||objpub.equals("1210113")||objpub.equals("1210114")) {
								strBuff.append("            				   AND ITM.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
							} else {
								strBuff.append("            				   AND ITM.FTN_VAL_AT = 'Y' \n");
							}
						}
					}

					if(classPubListSize != (q + 1)){
						strBuff.append("          ) ITM" + varSn + ", 	\n");
					}else{
						strBuff.append("          ) ITM" + varSn + " 	\n");
					}
				}
			} else {
				for(int q = 0; q < classListSize; q++) {

					String varSnP = classListForWhereClause.get(q);
					varSnP = varSnP.substring(4, 5);

					//strBuff.append("           ( SELECT COND.TARGET_VALUE AS "+ (String)classListForWhereClause.get(q) + "	\n");
					// 2019.04.23 오라클에서 쿼리튜닝해줌
					strBuff.append("           ( SELECT /*+ no_merge */ COND.TARGET_VALUE AS "+ (String)classListForWhereClause.get(q) + "	\n");
					strBuff.append("                  , ITM.CHAR_ITM_SN AS CHAR_ITM_SN	\n");
					strBuff.append("             	  , ITM.UNIT_ID, (SELECT NVL(DECODE('" + lang + "', 'e', CD_ENG_NM, CD_NM), '') FROM " + dbUser + "TC_UNIT WHERE CD_ID = ITM.UNIT_ID) AS OV_L"+ varSnP+"_UNIT_NM	\n");
					strBuff.append("               FROM  " + condTable + " COND,	\n");
					strBuff.append("             		" + dbUser + "TN_ITM_LIST ITM,	\n");
					strBuff.append("             		" + dbUser + "TN_OBJ_ITM_CLS CLS	\n");
					strBuff.append("              WHERE ITM.ORG_ID = CLS.ORG_ID 	\n");
					strBuff.append("            	AND ITM.TBL_ID = CLS.TBL_ID 	\n");
					strBuff.append("            	AND ITM.OBJ_VAR_ID = CLS.OBJ_VAR_ID 	\n");
					strBuff.append("             	AND CLS.VAR_ORD_SN = '" + varSnP + "' \n");
					strBuff.append("           		AND ITM.ORG_ID= '" + orgId + "'  	\n");
					strBuff.append("            	AND ITM.TBL_ID = '" + tblId + "'	 	\n");
					strBuff.append("            	AND ITM.ITM_ID = COND.TARGET_VALUE	\n");
					strBuff.append("             	AND COND.SESSION_ID = '" + sessionId + "' \n");
					strBuff.append("     			AND COND.TARGET_ID = '" + (String)classListForWhereClause.get(q) + "'   	\n");

					if(classListSize != (q + 1)){
						strBuff.append("          ) ITM" + varSnP + ", 	\n");
					}else{
						strBuff.append("          ) ITM" + varSnP + " 	\n");
					}
				}
			}

			if(martUsable){
				if(martJoinMethod == 'H'){
					strBuff.append(" 		, (\n");
					strBuff.append(" 			SELECT /*+ INDEX(B IDX01_" + mtId + ") */ * \n");
					strBuff.append(" 			FROM NSI_MART." + mtId + " B \n");
					strBuff.append("        	WHERE (\n");
					prdArry = prdValue.split("@");
					for(int i = 0; i < prdArry.length; i++){
						String[] tmpArry = prdArry[i].split(",");

						if(i != 0){
							strBuff.append(" OR \n ");
						}
						strBuff.append("(PRD_SE = '" + tmpArry[0] + "' AND PRD_DE IN (");

						for(int j = 1; j < tmpArry.length; j++){
							//if(i == 0 && j == 1){
							if(j == 1){
								//strBuff.append("'"+ tmpArry[0] + tmpArry[j] +"'");
								strBuff.append("'"+ tmpArry[j] +"'");
							}else{
								strBuff.append(", '"+ tmpArry[j] +"'");
							}

						}
						strBuff.append("))");
					}
					strBuff.append("        	)\n");
					//공표구분
					if(paramInfo.getServerType().equals("service_en")){
						strBuff.append("        	AND PUB_SE_DIM IN ('1210110', '1210114')	\n");
						strBuff.append("        	AND PUB_SE_DT IN ('1210110', '1210114')	\n");
					}else{
						strBuff.append("        	AND PUB_SE_DIM IN ('1210110', '1210113', '1210114')	\n");
						strBuff.append("        	AND PUB_SE_DT IN ('1210110', '1210113', '1210114')	\n");
					}
					strBuff.append("  ) A  \n");
					strBuff.append("  WHERE  \n");
					//항목, 분류값 조건
					strBuff.append("         A.CHAR_ITM_ID = ITM0.CHAR_ITM_ID	\n");
					if (direct.equals("direct")) {
						int classPubListSize = objVarPubInfo.size();
						for(int p = 0; p < classPubListSize; p++){
							Map obj_info = objVarPubInfo.get(p);
							String varSnP = obj_info.get("targetId").toString();
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + obj_info.get("targetId").toString() + " = ITM" + varSnP + "." + obj_info.get("targetId").toString() + " \n");
						}
					} else {
						for(int p = 0; p < classListSize; p++){
							String varSnP = classListForWhereClause.get(p);
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + (String)classListForWhereClause.get(p) + " = ITM" + varSnP + "." + (String)classListForWhereClause.get(p) + " \n");
						}
					}
				}else{
					strBuff.append("         , NSI_MART." + mtId + " A    	\n");
					strBuff.append("  WHERE  \n");
					//항목, 분류값 조건
					strBuff.append("         A.CHAR_ITM_ID = ITM0.CHAR_ITM_ID	\n");
					if (direct.equals("direct")) {
						int classPubListSize = objVarPubInfo.size();
						for(int p = 0; p < classPubListSize; p++){
							Map obj_info = objVarPubInfo.get(p);
							String varSnP = obj_info.get("targetId").toString();
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + obj_info.get("targetId").toString() + " = ITM" + varSnP + "." + obj_info.get("targetId").toString() + " \n");
						}
					} else {
						for(int p = 0; p < classListSize; p++){
							String varSnP = classListForWhereClause.get(p);
							varSnP = varSnP.substring(4, 5);
							strBuff.append("        AND A." + (String)classListForWhereClause.get(p) + " = ITM" + varSnP + "." + (String)classListForWhereClause.get(p) + " \n");
						}
					}
					//2014.07.09 이규정 sql 변경
					strBuff.append("		 AND (	\n");
					prdArry = prdValue.split("@");
					for(int i = 0; i < prdArry.length; i++){
						String[] tmpArry = prdArry[i].split(",");

						if(i != 0){
							strBuff.append(" OR \n ");
						}
						strBuff.append("(A.PRD_SE = '" + tmpArry[0] + "' AND A.PRD_DE IN (");

						for(int j = 1; j < tmpArry.length; j++){
							//if(i == 0 && j == 1){
							if(j == 1){
								//strBuff.append("'"+ tmpArry[0] + tmpArry[j] +"'");
								strBuff.append("'"+ tmpArry[j] +"'");
							}else{
								strBuff.append(", '"+ tmpArry[j] +"'");
							}
			
						}
						strBuff.append("))");
					}
					strBuff.append(" )	\n");

					//2015.06.16 상속통계표 공표 체크
					if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
						//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
					}else{
						// TN_DIM, TN_DT 공표구분 체크
						if(paramInfo.getServerType().equals("service_en")){
							strBuff.append("        	AND A.PUB_SE_DIM IN ('1210110', '1210114')	\n");
							strBuff.append("        	AND A.PUB_SE_DT IN ('1210110', '1210114')	\n");
						}else{
							strBuff.append("        	AND A.PUB_SE_DIM IN ('1210110', '1210113', '1210114')	\n");
							strBuff.append("        	AND A.PUB_SE_DT IN ('1210110', '1210113', '1210114')	\n");
						}
					}
				}
			}else{
				strBuff.append("         , " + dbUser + "TN_DIM A,  " + dbUser + "TN_DT B    	\n");

				//2015.08.10 상속통계표
				if("Y".equals(paramInfo.getInheritYn()) ){
					strBuff.append("  WHERE  B.ORG_ID = '" + paramInfo.getOriginOrgId() + "'    	\n");
					strBuff.append("    AND  B.TBL_ID = '" + paramInfo.getOriginTblId() + "'     	\n");
				}else{
					strBuff.append("  WHERE  A.ORG_ID = B.ORG_ID    	\n");
					strBuff.append("         AND A.TBL_ID = B.TBL_ID    	\n");
				}
				//항목, 분류값 조건
				strBuff.append("         AND A.CHAR_ITM_ID = ITM0.CHAR_ITM_ID	\n");
				if (direct.equals("direct")) {
					int classPubListSize = objVarPubInfo.size();
					for(int p = 0; p < classPubListSize; p++){
						Map obj_info = objVarPubInfo.get(p);
						String varSnP = obj_info.get("targetId").toString();
						varSnP = varSnP.substring(4, 5);
						strBuff.append("        AND A." + obj_info.get("targetId").toString() + " = ITM" + varSnP + "." + obj_info.get("targetId").toString() + " \n");
					}
				} else {
					for(int p = 0; p < classListSize; p++){
						String varSnP = classListForWhereClause.get(p);
						varSnP = varSnP.substring(4, 5);
						strBuff.append("         AND A." + (String)classListForWhereClause.get(p) + " = ITM" + varSnP + "." + (String)classListForWhereClause.get(p) + " \n");
					}
				}
				strBuff.append("         AND A.ITM_RCGN_SN = B.ITM_RCGN_SN   	\n");
				strBuff.append("         AND A.ORG_ID = '" + orgId + "'    	\n");
				strBuff.append("         AND A.TBL_ID = '" + tblId + "'	\n");
				//2014.07.09 이규정 sql 변경
				//strBuff.append("         AND B.PRD_SE||B.PRD_DE IN ( ");
				strBuff.append("		 AND (	\n");
				prdArry = prdValue.split("@");
				for(int i = 0; i < prdArry.length; i++){
					String[] tmpArry = prdArry[i].split(",");

					if(i != 0){
						strBuff.append(" OR \n ");
					}
					strBuff.append("(B.PRD_SE = '" + tmpArry[0] + "' AND B.PRD_DE IN (");

					for(int j = 1; j < tmpArry.length; j++){
						//if(i == 0 && j == 1){
						if(j == 1){
							//strBuff.append("'"+ tmpArry[0] + tmpArry[j] +"'");
							strBuff.append("'"+ tmpArry[j] +"'");
						}else{
							strBuff.append(", '"+ tmpArry[j] +"'");
						}

					}
					strBuff.append("))");
				}

				strBuff.append(" )	\n");


				//2015.06.16 상속통계표 공표 체크
				if("Y".equals(paramInfo.getInheritYn()) && !"1".equals(paramInfo.getPubSeType()) ){
					//상속통계표 공표 타입이 "미지정"이면 TN_DT 도 공표 미지정으로... (그냥 몽땅인거지)
				}else{
					// TN_DIM, TN_DT 공표구분 체크
					if(paramInfo.getServerType().equals("service_en")){
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210114')	\n");
					}else if(paramInfo.getServerType().equals("stat")){
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210112', '1210113', '1210114')	\n");
					}else if(paramInfo.getServerType().equals("stat_emp")){
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210111', '1210112', '1210113', '1210114')	\n");
					}else{
						strBuff.append("         AND A.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
						strBuff.append("         AND B.PUB_SE IN ('1210110', '1210113', '1210114')	\n");
					}
				}

			}

			strBuff.append("         )  \n");//end of with clause

			strBuff.append("SELECT ORG_ID, TBL_ID, PRD_SE	\n");
			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append("	 	, OV_L" + varOrdSn + "_ID	\n");
					strBuff.append("	 	, OV_L" + varOrdSn + "_SN	\n");
					strBuff.append("	 	, OV_L" + varOrdSn + "_NM	\n");
				}
			}
			strBuff.append("	 	, TIME  \n");

			String varObj = "";
			if(itmIdList != null){
				for(int i = 0; i < itmIdList.size(); i++){
					tmpMap = itmIdList.get(i);
					varObj = (String) tmpMap.get("itmId");	// 공표구분

					strBuff.append("		, \"" + varObj + "\" \n");
				}
			}
			strBuff.append("	  FROM	( \n");
			strBuff.append("        	SELECT ORG_ID, TBL_ID, PRD_SE ,PRD_SE AS PRD_SE_ORD \n");
			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append("			, OV_L"+ varOrdSn +"_ID	\n");
					strBuff.append("			, OV_L" + varOrdSn + "_SN    \n");

					strBuff.append("			, (SELECT /*+ index(TN_ITM_LIST XPKTN_ITM_LIST) */ NVL(DECODE('" + lang + "', 'e', SCR_ENG || DECODE(OV_L" + varOrdSn + "_UNIT_NM, NULL, '', '['|| OV_L" + varOrdSn + "_UNIT_NM || ']'), SCR_KOR || DECODE(OV_L" + varOrdSn + "_UNIT_NM, NULL, '', '['|| OV_L" + varOrdSn + "_UNIT_NM || ']')), '') AS SCR_KOR FROM  " + dbUser + "TN_ITM_LIST WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND OBJ_VAR_ID = (SELECT OBJ_VAR_ID FROM  " + dbUser + "TN_OBJ_ITM_CLS WHERE ORG_ID = A.ORG_ID AND TBL_ID = A.TBL_ID AND VAR_ORD_SN = '" + varOrdSn +"') AND ITM_ID = A.OV_L" + varOrdSn + "_ID) AS OV_L" + varOrdSn + "_NM	\n");
				}
			}
			strBuff.append("	  		,TIME	\n");

			if(itmIdList != null){
				for(int i = 0; i < itmIdList.size(); i++){
					tmpMap = itmIdList.get(i);
					varObj = (String) tmpMap.get("itmId");	// 공표구분
					strBuff.append("	  		,MAX(DECODE(CHAR_ITM_ID, '" + varObj + "', DTVAL_CO, NULL)) AS \"" + varObj + "\" \n");
				}
			}
			strBuff.append("		FROM tmp A	\n");

			// GROUP BY
			strBuff.append("		GROUP BY ORG_ID, TBL_ID, PRD_SE, PRD_SE_ORD ");
			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append(" ,OV_L" + varOrdSn + "_ID, OV_L" + varOrdSn + "_SN, OV_L" + varOrdSn + "_UNIT_NM ");
				}
			}

			strBuff.append(",TIME \n");

			// ORDER BY
			strBuff.append("ORDER BY ");

			if(objVarList != null){
				for(int i = 0; i < objVarList.size(); i++){
					tmpMap = objVarList.get(i);
					varOrdSn = String.valueOf(tmpMap.get("varOrdSn"));

					strBuff.append(" OV_L" + varOrdSn + "_SN ,OV_L" + varOrdSn + "_ID, ");
				}
			}
			if(downLargeSort.equals("asc")){
				strBuff.append(" (SELECT PRD_SN FROM "+dbUser+"TC_PRD_INFO WHERE PRD_SE = A.PRD_SE_ORD) , TIME \n");
			}else{
				strBuff.append("  (SELECT PRD_SN FROM "+dbUser+"TC_PRD_INFO WHERE PRD_SE = A.PRD_SE_ORD) DESC, TIME DESC \n");
			}
			strBuff.append("	)	S1 \n");

		}

		return strBuff.toString();
	}


	@SuppressWarnings("unchecked")
	private void setClassListForWhereClause(ParamInfo paramInfo){
		classListForWhereClause = statHtmlDAO.getClassListForWhereClause(paramInfo);
	}

	@SuppressWarnings("unchecked")
	private void setDirectClassListForWhereClause(ParamInfo paramInfo){
		classListForWhereClause = statHtmlDAO.getDirectClassListForWhereClause(paramInfo);
	}
}

