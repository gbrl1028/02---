package nurimsoft.stat.pivot.renderer;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nurimsoft.stat.info.MetaInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.manager.MakeMetaManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.CellType;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.util.MessageManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XlsRenderer extends Renderer{

	public File file;
	private HSSFWorkbook wb;

	HSSFCellStyle styleRight;
	HSSFCellStyle styleRight1;
	HSSFCellStyle styleRight2;
	HSSFCellStyle styleRight3;
	HSSFCellStyle styleRight4;
	HSSFCellStyle styleRight5;
	HSSFCellStyle styleRight6;
	HSSFCellStyle styleRight7;
	HSSFCellStyle styleRight8;
	HSSFCellStyle styleRight9;

	HSSFCellStyle styleRH;
	HSSFCellStyle styleCD;
	HSSFCellStyle styleRD;
	HSSFCellStyle styleRD_first;
	HSSFCellStyle styleRD_first_end;
	HSSFCellStyle styleRD_merge;
	HSSFCellStyle styleRD_merge_end;

	private String sheetName;
	private String sheetMetaName;

	private final String sheetName_KO = "데이터";
	private final String sheetName_EN = "Data";
	private final String sheetMetaName_KO = "메타정보";
	private final String sheetMetaName_EN = "Meta Data";

	private String analTitle;
	private final String analTitle_KO = "분석";
	private final String analTitle_EN = "Analysis";

	private final short rhColor = HSSFColor.CORNFLOWER_BLUE.index;
	private final short cdColor = HSSFColor.TAN.index;
	private final short rdColor = HSSFColor.LIGHT_CORNFLOWER_BLUE.index;

	private StatHtmlDAO statHtmlDAO;
	private StringUtil sutil;
	private HttpServletRequest request;

	public XlsRenderer(ParamInfo paramInfo, Map<List<String>,  Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager, StatHtmlDAO statHtmlDAO, HttpServletRequest request){
		super(paramInfo,resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager);
		this.statHtmlDAO = statHtmlDAO;
		this.request = request;

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			sheetName = sheetName_EN;
			sheetMetaName = sheetMetaName_EN;
			analTitle = analTitle_EN;
		}else{
			sheetName = sheetName_KO;
			sheetMetaName = sheetMetaName_KO;
			analTitle = analTitle_KO;
		}
	}

	//Overriding
	@SuppressWarnings("unchecked")
	public void write(){

		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir = "tmpFile";

		/*2014-11-19 통계표명 유지보수 1960 통계표 다운로드시 파일명 변경 - 이원영*/
		//String fileName = paramInfo.getOrgId() + "_" + paramInfo.getTblId();
		String fileName = "";

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			//2016-01-26 특수문자가 매번 걸려 StringUtil에서 왠만한건 한번에 거르게 수정 - 김경호
			fileName = sutil.removeSpecialCharforFile(paramInfo.getTblEngNm());
		}else{
			fileName = sutil.removeSpecialCharforFile(paramInfo.getTblNm());
		}

		String merge = paramInfo.getDownGridCellMerge();
		boolean isMerge = (merge != null && merge.equals("Y")) ? true : false;
		String meta = paramInfo.getDownGridMeta();
		boolean isContainMeta = (meta != null && meta.equals("Y")) ? true : false;

		String dateString = StatPivotUtil.getDateString();

		File dir = new File(realPath + File.separator + fileDir);
		if(! dir.exists()){
			dir.mkdir();
		}

		/*2015-01-23 분석 적용후 파일명 생성시 / -> _ 로 변경 부분 추가*/
		if(paramInfo.getAnalTextTblNm() != null){
			fileName += "_" + dateString + "_" + analTitle + "(" + paramInfo.getAnalTextTblNm().replaceAll("<", "").replaceAll(">", "").replaceAll(" ", "_").replaceAll("/", "_") + ").xls";
		}else{
			fileName += "_" + dateString + ".xls";
		}

		file = new File(realPath + File.separator + fileDir, fileName);

		wb = new HSSFWorkbook();

		appendRHStyle();
		appendCDStyle();
		appendValueStyle();

		if(isMerge){
			makeMergeExcel(); //셀병합 다운
		}else{
			makeExcel();
		}

		if(isContainMeta){
			addMetaSheet();
		}
		FileOutputStream fos = null;

		try{
			fos = new FileOutputStream(file);
			wb.write(fos);
		}catch(Exception e){
		}finally{
			try{
				fos.close();
			}catch(Exception e){}
		}
	}

	private void makeExcel(){
		HSSFSheet sheetData = wb.createSheet(sheetName);	// 데이터 시트생성
		HSSFRow hssfRow     = null;		// row
		HSSFCell hssfCell   = null;		// cell
		short cellIdx   = 0;		// 셀 index
		short rowIdx    = 0;		// row index

		appendRDStyle();

		int dataListSize = dataList.size();
		Map<Integer, Cell> colMap = null;

		HSSFCellStyle style = null;

		/*2014.10.13 엑셀 다운시 분류쪽 너비 조정(유지보수:1916 박선영) - 김경호 */
		int j = 0;
		int cellData_leng = 0;
		int[] j_array = null;
		/*------------------------------------------------------*/

		Cell cell = null;
		int cellType = 0;
		String cellStyle = "";
		String cellText = "";
		String cellValue = "";
		String dataType = "";
		String cellData = "";
		int level = 0;

		boolean superYn	= false;	// 통계부호 여부
		
		for(int i = 0; i < dataListSize; i++){

			colMap = (Map<Integer, Cell>)dataList.get(i);
			Iterator<Integer> iter = colMap.keySet().iterator();
			Integer key = null;

			hssfRow = sheetData.createRow((short)rowIdx++);	// header row 생성
			hssfRow.setHeight((short)400);	// 2016.10.28 윗첨자 크기가 커지면서 row 높이도 약간 높게 조정 - 김경호
			cellIdx = 0;

			/*2014.10.13 엑셀 다운시 분류쪽 너비 조정(유지보수:1916 박선영) - 김경호 */
			j = 0;
			cellData_leng = 0;

			if( i == 0){
				j_array = new int[colMap.size()];

				for(int x =0 ; x < j_array.length ; x++ ){
					j_array[x] = 0;
				}
			}
			/*------------------------------------------------------*/

			while(iter.hasNext()){

				key = iter.next();
				cell = colMap.get(key);
				cellType = cell.getType();
				cellStyle = cell.getStyle();
				cellText = cell.getText();
				cellValue = cell.getValue();
				dataType = "String";
				cellData = cellText;
				level = cell.getLevel();

				superYn	= false;	// 통계부호 여부

				/*2014.10.13 엑셀 다운시 분류쪽 너비 조정(유지보수:1916 박선영) - 김경호 */
				if( cellData.getBytes().length >= 80){ // 최대 넓이 : 20000
					cellData_leng = 20000;
				}else if( cellData.getBytes().length <= 6){ // 최소 넓이 : 1500
					cellData_leng = 1500;
				}else{
					cellData_leng = cellData.getBytes().length * 250;
				}

				if( j_array[j] < cellData_leng){
					j_array[j] = cellData_leng;
				}
				sheetData.setColumnWidth(j, j_array[j]);
				/*------------------------------------------------------*/

				if(cellType == CellType.RH){
					style = styleRH;
				}else if(cellType == CellType.CD){
					style = styleCD;
				}else if(cellType == CellType.RD){
					style = styleRD;

					//계층별 컬럼보기 아닌 경우 레벨 깊이 표시
					if(!levelExpr){
						cellData = getIntend(level) + cellText;
					}
				}else{
					if(StatPivotUtil.isNumericValue(cellText.replaceAll(",", ""))){
						cellData = cellValue;
						dataType = "Num";
						int len = cellValue.length();
						int dotIdx = cellValue.indexOf(".");

						if(dotIdx > -1){
							dotIdx = len - (dotIdx + 1);

							if(dotIdx == 1)			style = styleRight1;
							else if(dotIdx == 2)	style = styleRight2;
							else if(dotIdx == 3)	style = styleRight3;
							else if(dotIdx == 4)	style = styleRight4;
							else if(dotIdx == 5)	style = styleRight5;
							else if(dotIdx == 6)	style = styleRight6;
							else if(dotIdx == 7)	style = styleRight7;
							else if(dotIdx == 8)	style = styleRight8;
							else if(dotIdx == 9)	style = styleRight9;			
							else 	style.setDataFormat((short)0);  //2020.10.13 원데이터가 소수점 9자리가 넘어가는건 그대로 보여줄수 있도록 엑셀다운시 셀서식 일반으로 설정효과
						}
						else{
							style = styleRight;
						}
					}else{
						style = styleRight;
					}
					
					/*2020.11.30 수치부분에 대한 너비 조정 - 김경호 */
					cellData_leng = cellData.getBytes().length * 600;
					if( j_array[j] < cellData_leng){
						j_array[j] = cellData_leng;
					}
					sheetData.setColumnWidth(j, j_array[j]);
					/*---------------------------------*/
				}
				setCellValue(hssfCell, hssfRow, style, cellIdx++, dataType, cellData, cellType);
				j++;
			}
		}
	}

	private void makeMergeExcel(){

		HSSFSheet sheetData = wb.createSheet(sheetName);	// 데이터 시트생성
		HSSFRow hssfRow     = null;		// row
		HSSFCell hssfCell   = null;		// cell
		short cellIdx   = 0;		// 셀 index
		short rowIdx    = 0;		// row index

		appendRD_first_Style();
		appendRD_first_end_Style();
		appendRD_merge_Style();
		appendRD_merge_end_Style();

		int dataListSize = dataList.size();
		Map<Integer, Cell> colMap = null;

		HSSFCellStyle style = null;

		/*2014.10.13 엑셀 다운시 분류쪽 너비 조정(유지보수:1916 박선영) - 김경호 */
		int j = 0;
		int cellData_leng = 0;
		int[] j_array = null;
		/*------------------------------------------------------*/

		Cell cell = null;
		int cellType = 0;
		String cellStyle = "";
		String cellText = "";
		String cellValue = "";
		String dataType = "";
		String cellData = "";
		int level = 0;

		boolean superYn	= false;	// 통계부호 여부
		
		for(int i = 0; i < dataListSize; i++){

			colMap = (Map<Integer, Cell>)dataList.get(i);
			Iterator<Integer> iter = colMap.keySet().iterator();
			Integer key = null;

			hssfRow = sheetData.createRow((short)rowIdx);	// header row 생성
			hssfRow.setHeight((short)400);	// 2016.10.28 윗첨자 크기가 커지면서 row 높이도 약간 높게 조정 - 김경호
			cellIdx = 0;

			/*2014.10.13 엑셀 다운시 분류쪽 너비 조정(유지보수:1916 박선영) - 김경호 */
			j = 0;
			cellData_leng = 0;

			if( i == 0){
				j_array = new int[colMap.size()];

				for(int x =0 ; x < j_array.length ; x++ ){
					j_array[x] = 0;
				}
			}
			/*------------------------------------------------------*/

			while(iter.hasNext()){

				key = iter.next();
				cell = colMap.get(key);
				cellType = cell.getType();
				cellStyle = cell.getStyle();
				cellText = cell.getText();
				cellValue = cell.getValue();
				dataType = "String";
				cellData = cellText;
				level = cell.getLevel();

				superYn	= false;	// 통계부호 여부

				/*2014.10.13 엑셀 다운시 분류쪽 너비 조정(유지보수:1916 박선영) - 김경호 */
				if( cellData.getBytes().length >= 80){ // 최대 넓이 : 20000
					cellData_leng = 20000;
				}else if( cellData.getBytes().length <= 6){ // 최소 넓이 : 1500
					cellData_leng = 1500;
				}else{
					cellData_leng = cellData.getBytes().length * 250;
				}

				if( j_array[j] < cellData_leng){
					j_array[j] = cellData_leng;
				}
				sheetData.setColumnWidth(j, j_array[j]);
				/*------------------------------------------------------*/

				if(cellType == CellType.RH){ // 분류명
					if(i == 0){
						if(colDimRowCount > 1){
							sheetData.addMergedRegion(new CellRangeAddress(rowIdx,colDimRowCount - 1,cellIdx,cellIdx));
						}
						style = styleRH;
					}
				}else if(cellType == CellType.CD){ // 항목명
					if(cell.isFirst()){

						if(cell.getColspan() > 1){
							sheetData.addMergedRegion(new CellRangeAddress(rowIdx,rowIdx,cellIdx,cellIdx + cell.getColspan() - 1));
						}

						style = styleCD;
					}
				}else if(cellType == CellType.RD){ // 분류값

					//계층별 컬럼보기 아닌 경우 레벨 깊이 표시
					if(!levelExpr){
						cellData = getIntend(level) + cellText;
					}

					if(!isSort){
						//merge인 경우 공백 출력
						if(cellStyle.indexOf("merge") > -1){
							cellData = "";
						}
					}

					//현재는 정렬이전값을 출력한다..추후에 정렬 적용 할 경우 바르게 수행됨.
					if(isSort){
						style = styleRD_first_end;
					}else{
						if(cellStyle.indexOf("merge") > -1){
							//merge
							if(i == dataListSize - 1){
								style = styleRD_merge_end;
							}else{
								style = styleRD_merge;
							}
						}else{
							//first
							if(i == dataListSize - 1){
								style = styleRD_first_end;
							}else{
								style = styleRD_first;
							}
						}
					}
				}else{
					if(StatPivotUtil.isNumericValue(cellText.replaceAll(",", ""))){
						cellData = cellValue;
						dataType = "Num";
						int len = cellValue.length();
						int dotIdx = cellValue.indexOf(".");

						if(dotIdx > -1){
							dotIdx = len - (dotIdx + 1);

							if(dotIdx == 1)			style = styleRight1;
							else if(dotIdx == 2)	style = styleRight2;
							else if(dotIdx == 3)	style = styleRight3;
							else if(dotIdx == 4)	style = styleRight4;
							else if(dotIdx == 5)	style = styleRight5;
							else if(dotIdx == 6)	style = styleRight6;
							else if(dotIdx == 7)	style = styleRight7;
							else if(dotIdx == 8)	style = styleRight8;
							else if(dotIdx == 9)	style = styleRight9;							
							else 	style.setDataFormat((short)0);  //2020.10.13 원데이터가 소수점 9자리가 넘어가는건 그대로 보여줄수 있도록 엑셀다운시 셀서식 일반으로 설정효과
						}
						else{
							style = styleRight;
						}
					}else{
						style = styleRight;
					}
					
					/*2020.11.30 수치부분에 대한 너비 조정 - 김경호 */
					cellData_leng = cellData.getBytes().length * 600;
					if( j_array[j] < cellData_leng){
						j_array[j] = cellData_leng;
					}
					sheetData.setColumnWidth(j, j_array[j]);
					/*---------------------------------*/
				}
				setCellValue(hssfCell, hssfRow, style, cellIdx, dataType, cellData, cellType);	// 통계표명

				cellIdx++;

				j++;
			}
			rowIdx++;
		}
	}

	private void addMetaSheet(){
		HSSFSheet sheetMeta = wb.createSheet(sheetMetaName);	// 메타 시트생성
		HSSFRow hssfRow     = null;		// row
		HSSFCell hssfCell   = null;		// cell
		short cellIdx   = 0;		// 셀 index
		short rowIdx    = 0;		// row index

		//MetaInfo 가져오기
		MakeMetaManager metaManager = new MakeMetaManager(paramInfo, statHtmlDAO, request);
		MetaInfo metaInfo = metaManager.getMetaInfo();

		if(metaInfo == null){
			return;
		}

		HSSFCellStyle styleMeta = wb.createCellStyle();	// default cell style
		styleMeta.setAlignment(HSSFCellStyle.ALIGN_LEFT);

		//통계표ID
		hssfRow = sheetMeta.createRow((short)rowIdx++);
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getTblIdTitle());
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getTblId());

		//통계표명
		if(metaInfo.getTblNm() != null){
			hssfRow = sheetMeta.createRow((short)rowIdx++);
			setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getTblNmTitle());
			setCellValue1( hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getTblNm() + ((paramInfo.getAnalTextTblNm() != null) ? " " + paramInfo.getAnalTextTblNm() : "") );
		}

		//2018.01.23 수록기간 주석(정보원 요청) - 김경호
		//수록기간
		//hssfRow = sheetMeta.createRow((short)rowIdx++);
		//setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getContainPeriodTitle());
		//setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getContainPeriod());

		//2018.01.23 다운로드 파일의 메타자료에 조회기간표시함. 주석되어 있었으나(이유모름) 정보원의 요청으로 주석해제 - 김경호
		//조회기간
		hssfRow = sheetMeta.createRow((short)rowIdx++);
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getSearchPeriodTitle());
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getSearchPeriod());

		//출처
		if(metaInfo.getSource() != null){
			hssfRow = sheetMeta.createRow((short)rowIdx++);
			setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getSourceTitle());
			setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getSource());
		}

		//자료다운일자
		hssfRow = sheetMeta.createRow((short)rowIdx++);
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getDownDateTitle());
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getDownDate());

		//통계표URL
		hssfRow = sheetMeta.createRow((short)rowIdx++);
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getUrlTitle());
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getUrl());
		hssfRow = sheetMeta.createRow((short)rowIdx++);
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "");
		setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", MessageManager.getInstance().getProperty("text.meta.url", paramInfo.getDataOpt()));

		//단위
		if(metaInfo.getUnit() != null){
			hssfRow = sheetMeta.createRow((short)rowIdx++);
			setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getUnitTitle());
			setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", metaInfo.getUnit());
		}

		//주석

		List cmmtList = metaInfo.getCmmtList();
		int cmmtListSize = cmmtList.size();
		if(cmmtListSize > 0){

			Map map = null;
			String cmmtDc = null;
			int rnum = 0;
			String title = null;

			hssfRow = sheetMeta.createRow((short)rowIdx++);
			setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "○ " + metaInfo.getCmmtTitle());

			for(int i = 0; i < cmmtListSize; i++){

				hssfRow = sheetMeta.createRow((short)rowIdx++);

				map = (Map)cmmtList.get(i);

				cmmtDc = (String)map.get("cmmtDc");
				rnum = ((BigDecimal)map.get("rnum")).intValue();
				title = (String)map.get("title");
				title = title.replace(":", "").trim();

				if(rnum == 1){
					setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", title);
				}else{
					setCellValue1(hssfCell, hssfRow, styleMeta, (short)0, "String", "");
				}

				setCellValue1(hssfCell, hssfRow, styleMeta, (short)1, "String", cmmtDc);
			}

		}

	}

	// 2017.01.19 int cellType 추가 - 김경호
	private void setCellValue(HSSFCell cell, HSSFRow row, HSSFCellStyle style, short cellIdx, String dataType, String cellText, int cellType)
	{
		cell = row.createCell((int) cellIdx);

		if(style != null){
			cell.setCellStyle(style);
		}

		// 2015.11.13 통계부호 윗첨자로 만들기
		String smblYn = paramInfo.getSmblYn(); //통계부호 받는지 여부
		cellText = sutil.stringCheck(cellText);	// 셀값의 null 제거

		String replace_cellText = cellText.replace("[{", "").replace("}]", "");	// 실제 텍스트가 몇자인지 알 수 있도록 통계부호를 표시하는 가로 제거
		int loc_code = 0; // 통계부호 위치저장을 위한 변수
		
		HSSFRichTextString str = new HSSFRichTextString(replace_cellText); // str에 [{}] 제거한 cellText값을 셋팅

		if( !cellText.equals("") ){	// 수치 + 통계부호가 값이 있을때
			
			boolean text_chk = false; // 지정된 통계부호가 들어가 있는지 여부
			
			if(smblYn.equals("Y")){
				if( replace_cellText.length() > 1){ // 1자리 이하면 윗첨자 적용할 필요가 없음
					
					/*
					String lastText = buff.substring(buff.length()-1); //2016.11.03 수치마지막에 통계부호가 들어갈때만 밑에 폰트 적용이 들어가도록...(서비스쪽에 통계부호같이보기하면 부하가 심함) -김경호
					2017.01.19 
						영문명 끝에 알파벳을 통계부호로 인식하는 문제로 인해 수치일때만 통계부호가 적용되도록 수정 
						(시점에는 통계부호 뒤에 [)] 가 추가 됨으로 해당안됨.)- 김경호
					2019.03.12 셀단위가 들어갈경우 통계부호 위치를 알 수 없음으로 기존 substring 방식으로는 윗첨자를 제어할 수 없음
							   따라서 Renderer에서 cellText를 만들때 부터 통계부호를 알 수 있도록 [{ }] 안에 넣어서 추후에 replace시킬수 있도록 한다. 				
					*/
					if( cellType == CellType.VAL){ // 수치일때만 통계부호에 관련된 윗첨자 작업을 할수있도록 text_chk를 true로 만들 수 있다.	
						if(cellText.indexOf("[{**}]") > -1 ||cellText.indexOf("[{*}]") > -1 ||cellText.indexOf("[{e}]") > -1||cellText.indexOf("[{p}]") > -1||cellText.indexOf("[{▽}]") > -1){
							text_chk = true;
						}
					}
				}			
			
				if(smblYn.equals("Y") && text_chk){	// 화면상에서 통계부호를 보겠다 체크했을때
					
					Font font = wb.createFont();
					font.setFontHeightInPoints((short)14); //2016.11.01 윗첨자 색상 변경 및 크기 변경 - 김경호
					font.setTypeOffset(HSSFFont.SS_SUPER);
					font.setColor((short)(HSSFFont.COLOR_RED));
					
					if( cellText.indexOf("[{**}]") > -1){
						loc_code = cellText.indexOf("[{**}]");
						
						str.applyFont(loc_code, loc_code + 2,font); // **이 두자리니까 2
					}else if( cellText.indexOf("[{*}]") > -1){
						loc_code = cellText.indexOf("[{*}]");
						
						str.applyFont(loc_code, loc_code + 1,font); // *이 한자리니까 1
					}else if( cellText.indexOf("[{e}]") > -1 ){
						loc_code = cellText.indexOf("[{e}]");
						
						str.applyFont(loc_code, loc_code + 1,font);
					}else if( cellText.indexOf("[{p}]") > -1){
						loc_code = cellText.indexOf("[{p}]");
						
						str.applyFont(loc_code, loc_code + 1,font);
					}else if( cellText.indexOf("[{▽}]") > -1){
						loc_code = cellText.indexOf("[{▽}]");
						
						str.applyFont(loc_code, loc_code + 1,font);
					}
				}
			}
		}

		if(dataType != null){
			if(dataType.equals("Num")){
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(NumberUtils.toDouble(cellText));
			}else{
				if(smblYn.equals("Y")){ //2019.03.12 통계부호를 볼때만 윗첨자 작업된 셀값을 보여준다
					cell.setCellValue(str);
				}else{
					String buff[] = {"[{**}]","[{*}]","[{e}]","[{p}]","[{▽}]"};
					
					for(int z = 0; z < buff.length ; z++){
						if(cellText.indexOf(buff[z]) > -1){
							cellText = cellText.replace(buff[z], "");
							break;
						}
					}
					cell.setCellValue(new HSSFRichTextString(cellText)); // 통계부호를 보지 않을때는 이미 셀값에 들어가 있는 부호를 제거하고 보여준다.
				}
			}
		}else{
			// 통계부호관련해서 따로 컨트롤하는 부분이 안보이므로 [{}] 만 떼서 보여준다...2019.03 이전버전에 [{}] 없을때처럼 넣어주기위해서 replace한 값을 넣어줌
			cell.setCellValue(new HSSFRichTextString(replace_cellText));
		}
	}

	private void setCellValue1(HSSFCell cell, HSSFRow row, HSSFCellStyle style, short cellIdx, String dataType, String cellText)
	{
		cell = row.createCell((int) cellIdx);

		if(style != null){
			cell.setCellStyle(style);
		}

		if(dataType != null){
			if(dataType.equals("Num")){
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(NumberUtils.toDouble(cellText));
			}else{
				cell.setCellValue(new HSSFRichTextString(cellText));
			}
		}
		else{
			cell.setCellValue(new HSSFRichTextString(cellText));
		}
	}

	private void appendRHStyle(){
		styleRH = wb.createCellStyle();
		styleRH.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleRH.setVerticalAlignment((short)1); // 가운데 맞춤
		styleRH.setFillForegroundColor(rhColor);
		styleRH.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRH.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRH.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRH.setBorderTop(HSSFCellStyle.BORDER_THIN);
	}

	private void appendCDStyle(){
		styleCD = wb.createCellStyle();
		styleCD.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleCD.setFillForegroundColor(cdColor);
		styleCD.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleCD.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleCD.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleCD.setBorderTop(HSSFCellStyle.BORDER_THIN);
	}

	private void appendRDStyle(){
		styleRD = wb.createCellStyle();
		styleRD.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleRD.setFillForegroundColor(rdColor);
		styleRD.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRD.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRD.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRD.setBorderTop(HSSFCellStyle.BORDER_THIN);
	}

	private void appendRD_first_Style(){
		styleRD_first = wb.createCellStyle();
		styleRD_first.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleRD_first.setFillForegroundColor(rdColor);
		styleRD_first.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRD_first.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRD_first.setBorderTop(HSSFCellStyle.BORDER_THIN);
	}

	private void appendRD_first_end_Style(){
		styleRD_first_end = wb.createCellStyle();
		styleRD_first_end.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleRD_first_end.setFillForegroundColor(rdColor);
		styleRD_first_end.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRD_first_end.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRD_first_end.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRD_first_end.setBorderTop(HSSFCellStyle.BORDER_THIN);
	}

	private void appendRD_merge_Style(){
		styleRD_merge = wb.createCellStyle();
		styleRD_merge.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleRD_merge.setFillForegroundColor(rdColor);
		styleRD_merge.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRD_merge.setBorderRight(HSSFCellStyle.BORDER_THIN);
	}

	private void appendRD_merge_end_Style(){
		styleRD_merge_end = wb.createCellStyle();
		styleRD_merge_end.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleRD_merge_end.setFillForegroundColor(rdColor);
		styleRD_merge_end.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRD_merge_end.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRD_merge_end.setBorderRight(HSSFCellStyle.BORDER_THIN);
	}

	private void appendValueStyle(){

		styleRight = wb.createCellStyle();	// 오른쪽정렬  cell style
		styleRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

		styleRight1 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight1.setDataFormat(wb.createDataFormat().getFormat("#,##0.0"));

		styleRight2 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight2.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));

		styleRight3 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight3.setDataFormat(wb.createDataFormat().getFormat("#,##0.000"));

		styleRight4 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight4.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight4.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight4.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight4.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight4.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight4.setDataFormat(wb.createDataFormat().getFormat("#,##0.0000"));

		styleRight5 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight5.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight5.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight5.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight5.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight5.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight5.setDataFormat(wb.createDataFormat().getFormat("#,##0.00000"));
		
		styleRight6 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight6.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight6.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight6.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight6.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight6.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight6.setDataFormat(wb.createDataFormat().getFormat("#,##0.000000"));
		
		styleRight7 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight7.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight7.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight7.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight7.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight7.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight7.setDataFormat(wb.createDataFormat().getFormat("#,##0.0000000"));
		
		styleRight8 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight8.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight8.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight8.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight8.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight8.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight8.setDataFormat(wb.createDataFormat().getFormat("#,##0.00000000"));
		
		styleRight9 = wb.createCellStyle();;	// 오른쪽정렬  cell style
		styleRight9.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		styleRight9.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleRight9.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleRight9.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleRight9.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleRight9.setDataFormat(wb.createDataFormat().getFormat("#,##0.000000000"));
	}

	private String getIntend(int level){
		StringBuffer intend = new StringBuffer();
		intend.append(""); // 1레벨의 간격
		for(int z = 1; z < level; z++){
			intend.append("　　　"); //두개씩 가야 표가 난다. 2015.3.12 3칸으로 늘림
		}

		return intend.toString();
	}

}
