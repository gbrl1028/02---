package nurimsoft.stat.pivot.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.CellType;
import nurimsoft.stat.pivot.ClassDimension;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Dimension;
import nurimsoft.stat.pivot.Item;
import nurimsoft.stat.pivot.ItemDimension;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.MeasureDimension;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.pivot.TimeDimension;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Renderer {

	protected Log log = LogFactory.getLog(this.getClass());

	ParamInfo paramInfo;

	Map<List<String>, Measure> resultMap = new HashMap<List<String>, Measure>();
	protected RowAxis rowAxis;
	protected ColumnAxis columnAxis;

	protected int columnDimCount;
	protected int rowDimCount;

	protected int columnSize;
	protected int rowSize;

	protected boolean levelExpr;	//계층별 컬럼보기 여부(Default : false)
	protected boolean isSort;	    //정렬 작업 여부

	//최종 데이터 셋을 담는 변수와 정보
	protected List<Map> dataList = new ArrayList<Map>();

	protected List<Map> dataHeaderList = new ArrayList<Map>();
	protected List<Map> dataBodyList = new ArrayList<Map>();

	int colDimRowCount;
	int rowDimColCount;
	int valColBeginIdx;
	int valRowBeginIdx;

	protected final String rowHeadCell	= "rowHead";
	protected final String colHeadFirstCell	= "colHead-first";
	protected final String colHeadMergeCell	= "colHead-merge";
	protected final String firstCell 		= "first";
	protected final String mergeCell 		= "merge";
	protected final String valueCell 		= "value";

	protected CmmtInfoManager cmmtInfoManager;

	PropertyManager pm = PropertyManager.getInstance();

	protected boolean doAnal;
	protected boolean originData;

	protected String analType;
	protected String fnExcptCd;

	public Renderer(ParamInfo paramInfo, Map<List<String>, Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager){
		this.paramInfo = paramInfo;
		this.resultMap = resultMap;
		this.rowAxis = rowAxis;
		this.columnAxis = columnAxis;
		this.levelExpr = levelExpr;
		this.cmmtInfoManager = cmmtInfoManager;

		if(paramInfo.getDoAnal() != null && paramInfo.getDoAnal().equals("Y")){
			doAnal = true;
			fnExcptCd = paramInfo.getFnExcptCd();
		}

		if(paramInfo.getOriginData() != null && paramInfo.getOriginData().equals("Y")){
			originData = true;
		}

		setCondition();
	}

	public void setCondition(){

		//가중치
		if(paramInfo.getEnableWeight() != null && paramInfo.getEnableWeight().equals("Y")){
			MeasureDimension measureDimension = new MeasureDimension(paramInfo);
			columnAxis.addDimension(measureDimension);
			measureDimension.setItemsWeight();
		}
		//분석인 경우에는 원자료함께보기인 경우에만 MeasureDimension을 생성한다.
		else if(doAnal){

			analType = paramInfo.getAnalType();

			MeasureDimension measureDimension = null;
			if(originData){
				measureDimension = new MeasureDimension(paramInfo);
				columnAxis.addDimension(measureDimension);
			}

			String analCmpr = paramInfo.getAnalCmpr();
			String analTime = paramInfo.getAnalTime();

			if( (analCmpr != null && analCmpr.indexOf("ONE") > -1) && analTime != null && analTime.trim().length() > 0 ){
				String prdDeStr = StatPivotUtil.generatePrdDe(analTime.substring(1), analTime.substring(0, 1));
				prdDeStr = prdDeStr.replaceAll("/", "-"); // 2014-04-07 슬러쉬가 다운로드 파일명에 들어갈때 폴더구분자로 사용되어 404에러나는것때문에 수정 - 김경호

				String nmKor = prdDeStr + " " + PropertyManager.getInstance().getProperty("string.measure.func.ko");
				String nmEng = PropertyManager.getInstance().getProperty("string.measure.func.en") + " " + prdDeStr;
				if(originData){
					//measureDimension.setItemsAnal(nmKor + " " + paramInfo.getAnalTypeNm(), nmEng + " " + paramInfo.getAnalTypeNmEng());
					measureDimension.setItemsAnal(nmKor + " " + paramInfo.getAnalTypeNm(), paramInfo.getAnalTypeNmEng() + " " + nmEng);  //2014.10.10 영문일 경우 앞뒤 문장 위치 변경 - 김경호(이원영 요청)
				}

				//통계표 명 옆에 붙을 문자
				if(paramInfo.getDataOpt().indexOf("en") > -1){
					//paramInfo.setAnalTextTblNm(nmEng + " " + paramInfo.getAnalTypeNmEng());
					paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNmEng() + " " + nmEng );  //2014.10.10 영문일 경우 앞뒤 문장 위치 변경 - 김경호(이원영 요청)
				}else{
					paramInfo.setAnalTextTblNm(nmKor + " " + paramInfo.getAnalTypeNm());
				}
			}else{
				//누계, 구성비, 누계구성비가 아닌 경우
				String analType = paramInfo.getAnalType();
				if(!analType.equals("TOTL") && !analType.equals("CMP_RATE") && !analType.equals("TOTL_CMP_RATE")){
					if(originData){
						//measureDimension.setItemsAnal(paramInfo.getAnalCmprNm() + " " + paramInfo.getAnalTypeNm(), paramInfo.getAnalCmprNmEng() + " " + paramInfo.getAnalTypeNmEng());
						measureDimension.setItemsAnal(paramInfo.getAnalCmprNm() + " " + paramInfo.getAnalTypeNm(), paramInfo.getAnalTypeNmEng() + " " + paramInfo.getAnalCmprNmEng()); //2014.10.10 영문일 경우 앞뒤 문장 위치 변경 - 김경호(이원영 요청)
					}

					//통계표 명 옆에 붙을 문자
					if(paramInfo.getDataOpt().indexOf("en") > -1){
						//paramInfo.setAnalTextTblNm(paramInfo.getAnalCmprNmEng() + " " + paramInfo.getAnalTypeNmEng());
						paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNmEng() + " " + paramInfo.getAnalCmprNmEng()); //2014.10.10 영문일 경우 앞뒤 문장 위치 변경 - 김경호(이원영 요청)
					}else{
						paramInfo.setAnalTextTblNm(paramInfo.getAnalCmprNm() + " " + paramInfo.getAnalTypeNm());
					}
				}else{
					if( analType.equals("CMP_RATE") || analType.equals("TOTL_CMP_RATE") ){

						if(originData){
							//measureDimension.setItemsAnal(paramInfo.getAnalTypeNm() + "-" + paramInfo.getAnalCmprNm(), paramInfo.getAnalTypeNmEng() + "-" + paramInfo.getAnalCmprNmEng());
							measureDimension.setItemsAnal(paramInfo.getAnalTypeNm(), paramInfo.getAnalTypeNmEng());
						}

						//통계표 명 옆에 붙을 문자
						if(paramInfo.getDataOpt().indexOf("en") > -1){
							//paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNmEng() + "-" + paramInfo.getAnalCmprNmEng());
							paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNmEng());
						}else{
							//paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNm() + "-" + paramInfo.getAnalCmprNm());
							paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNm());
						}
					}else{

						if(originData){
							measureDimension.setItemsAnal(paramInfo.getAnalTypeNm(), paramInfo.getAnalTypeNmEng());
						}

						//통계표 명 옆에 붙을 문자
						if(paramInfo.getDataOpt().indexOf("en") > -1){
							paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNmEng());
						}else{
							paramInfo.setAnalTextTblNm(paramInfo.getAnalTypeNm());
						}
					}
				}
			}
		}


		rowDimCount = rowAxis.getDimemsionCount();
		columnDimCount = columnAxis.getDimemsionCount();

		//System.out.println("columnDimCount ::: " + columnDimCount);

		//Column 먼저.
		List<Dimension> columnDimList = columnAxis.getDimensionList();
		List<Dimension> rowDimList = rowAxis.getDimensionList();

		Dimension curDim = null;
		Dimension befDim = null;

		int columnCount = 1;
		int rowCount = 1;

		int upperItemCount = 1;	//repeatGroup을 구하기 위한 변수
		int lowerItemCount = 1; //repeatSelf를 구하기 위한 변수

		//@@@ Column-Dimension
		//#1. 순방향으로 RepeateGroup 셋팅, 아울러 총 컬럼 수도 셋팅
		int tmpMaxLevel = 1;
		for(int i = 0; i < columnAxis.getDimemsionCount(); i++){
			curDim = (Dimension)columnDimList.get(i);
			int tmpLevel = curDim.getMaxLevel();
			if(tmpLevel > tmpMaxLevel){
				tmpMaxLevel = tmpLevel;
			}

			//상위레벨의 조합만큼 그룹카운트를 셋팅, 최상위의 경우 1
			if(i > 0){
				curDim.setRepeatGroup(upperItemCount);
			}
			upperItemCount = upperItemCount * curDim.getItemCount();
		}

		columnCount = upperItemCount;

		//#2. 역방향으로 RepeatSelf 셋팅
		curDim = null;
		befDim = null;
		for(int i = columnAxis.getDimemsionCount() - 1; i >= 0; i--){
			curDim = (Dimension)columnDimList.get(i);

			//상위분류 조합수 만큼 repeatSelf 셋팅, 최하위의 경우 1;
			if(i < columnAxis.getDimemsionCount() - 1){
				lowerItemCount = lowerItemCount * befDim.getItemCount();
				curDim.setRepeatSelf(lowerItemCount);
			}else{
				curDim.setRepeatSelf(1);
			}

			befDim = curDim;

		}

		//System.out.println("columnCount ::: " + columnCount);

		//@@@Row-Dimension

		//#1. 순방향으로 RepeateGroup 셋팅, 아울러 총 컬럼 수도 셋팅
		curDim = null;
		befDim = null;
		upperItemCount = 1;
		for(int i = 0; i < rowAxis.getDimemsionCount(); i++){
			curDim = (Dimension)rowDimList.get(i);

			//상위레벨의 조합만큼 그룹카운트를 셋팅, 최상위의 경우 1
			if(i > 0){
				curDim.setRepeatGroup(upperItemCount);
			}

			upperItemCount = upperItemCount * curDim.getItemCount();

		}

		curDim = null;
		befDim = null;
		lowerItemCount = 1;
		for(int i = rowAxis.getDimemsionCount() - 1; i >= 0; i--){
			curDim = (Dimension)rowDimList.get(i);

			//상위분류 값 만큼 repeat 최하위의 경우 repeatCount = 0;
			if(i < rowAxis.getDimemsionCount() - 1){
				lowerItemCount = lowerItemCount * befDim.getItemCount();
				curDim.setRepeatSelf(lowerItemCount);
			}else{
				curDim.setRepeatSelf(1);
			}

			befDim = curDim;

		}

		rowCount = upperItemCount;

		columnSize = rowDimCount + columnCount;
		rowSize = columnDimCount + rowCount;

		//dataList 생성
		makeDataList();

	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public void makeDataList(){

		setHeaderCount();

		/*
		 * 01. 표두가 없는 경우
		 * 02. 표측이 없는 경우
		 * 03. 두개 모두 존재하는 경우
		 * @see 코드가 중복되는 부분도 있으나. 분리시키는 것도 효율적이라 생각지 않으므로 일단 둔다.
		 *      따라서, 수정 시 헤드, 바디에 해당하는 부분을 3개의 케이스 소스를 다 살펴보아야 한다.
		 */
		if(columnDimCount == 0){
			//표두가 없다.
			makeHeadForColZero();
			makeBodyForColZero();
		}else if(rowDimCount == 0){
			//표측이 없다.
			makeHeadForRowZero();
			makeBodyForRowZero();
		}else{
			//양측 다 존재.
			makeHeadForBoth();
			makeBodyForBoth();
		}

		removeNullData();
		decorateHtml();
		//정렬함 가자.
		if(paramInfo.getOrdColIdx() != null && paramInfo.getOrdColIdx().trim().length() > 0 && paramInfo.getOrdType() != null && paramInfo.getOrdType().trim().length() > 0){
			isSort = true;

			final int colIdx = Integer.parseInt(paramInfo.getOrdColIdx());
			final int type = Integer.parseInt(paramInfo.getOrdType());

			//System.out.println("정렬들어간다." + colIdx + "," + type);

			Collections.sort(dataBodyList, new Comparator(){
				public int compare(Object map1, Object map2){

					Cell cell01 = (Cell)((Map)map1).get(colIdx);
					Cell cell02 = (Cell)((Map)map2).get(colIdx);

					// 2015.06.02 cell01, cell02 둘중 null 값이 있을 경우 오류 발생되어 null 처리
					if(cell01 == null || cell02 == null){
						return 0;
					}

					if(cell01.getType() == CellType.RD){
						//문자비교
						String str1 = cell01.getText();
						String str2 = cell02.getText();

						int compare = str1.compareTo(str2);

						if(type == 0){
							//asc
							if(compare > 0){
								return 1;
							}else if(compare == 0){
								return 0;
							}else{
								return -1;
							}
						}else{
							//desc
							if(compare < 0){
								return 1;
							}else if(compare == 0){
								return 0;
							}else{
								return -1;
							}
						}
					}else{
						//숫자비교, 그러나 둘중 하나가 문자인경우 둘다 문자로 비교한다.
						String str1 = cell01.getValue();
						String str2 = cell02.getValue();

						if( StatPivotUtil.isNumericValue(str1) && StatPivotUtil.isNumericValue(str2) ){
							//숫자로 치환하여 비교
							double d1 = Double.parseDouble(str1);
							double d2 = Double.parseDouble(str2);

							if(type == 0){
								//asc
								if(d1 > d2){
									return 1;
								}else if(d1 == d2){
									return 0;
								}else{
									return -1;
								}
							}else{
								//desc
								if(d1 < d2){
									return 1;
								}else if(d1 == d2){
									return 0;
								}else{
									return -1;
								}
							}
						}else{
							//문자로 비교
							int compare = str1.compareTo(str2);
							if(type == 0){
								//asc
								if(compare > 0){
									return 1;
								}else if(compare == 0){
									return 0;
								}else{
									return -1;
								}
							}else{
								//desc
								if(compare < 0){
									return 1;
								}else if(compare == 0){
									return 0;
								}else{
									return -1;
								}
							}
						}
					}
				}
			});
		}
		dataList.addAll(dataHeaderList);
		dataList.addAll(dataBodyList);
	}

	/*
	 * Column-Dimension 수가 0일 경우
	 */
	private void makeHeadForColZero(){

		Map<Integer, Cell> colMap = new TreeMap<Integer, Cell>();
		int mapIdx = 0;

		Cell cell = null;
		Dimension rowDim = null;
		
		for(int i = 0; i < rowDimCount; i++){
			rowDim = rowAxis.getDimension(i);

			//계층별 컬럼보기 여부에 따라
			if(levelExpr){
				int maxLevel = rowDim.getMaxLevel();

				for(int j = 0; j < maxLevel; j++){
					if(rowDim instanceof ClassDimension){
						colMap.put( mapIdx++, makeHeadCell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName() + "(" + (j + 1)+  ")", (ClassDimension)rowDim, j+1) );
					}else{
						cell = new Cell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName(), rowDim.getVarOrdSn());
						colMap.put( mapIdx++, cell );
					}
				}
				
			}else{
				cell = new Cell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName(), rowDim.getVarOrdSn());
				colMap.put( mapIdx++, cell );
			}
		}

		cell = new Cell(CellType.CD);

		if(paramInfo.getDataOpt() != null && paramInfo.getDataOpt().indexOf("en") > -1){
			cell.setValue(pm.getProperty("string.head.val.en"));
			cell.setText(pm.getProperty("string.head.val.en"));
		}else{
			cell.setValue(pm.getProperty("string.head.val.ko"));
			cell.setText(pm.getProperty("string.head.val.ko"));
		}

		cell.setStyle(colHeadFirstCell);
		cell.setFirst(true);
		cell.setColspan(1);
		colMap.put( mapIdx++, cell );

		dataHeaderList.add(colMap);
	}

	private void makeBodyForColZero(){

		int mapStrArrIdx = 0;
		int itemCount = 0;
		int repeatSelf = 0;
		int repeatGroup = 0;

		int rowCount = 0;

		Item item = null;
		Item parentItem = null;

		rowCount = rowAxis.getDimension(0).getItemCount() * rowAxis.getDimension(0).getRepeatSelf();
		//value를 추출하기 위한 String[] 변수를 선언한다.
		String[] mapStrArr = new String[rowDimCount];

		Map<Integer, Cell> colMap = null;
		Dimension dim = null;
		Cell cell = null;
		
		for(int i = 0; i < rowCount; i++){

			colMap = new TreeMap<Integer, Cell>();

			dim = null;
			int itemIndex = 0;

			String chartUnit = "";

			cell = null;
			
			//Row-Dimension 출력
			int mapIdx = 0;
			for(int j = 0; j < rowDimCount; j++){
				dim = rowAxis.getDimension(j);
				itemCount = dim.getItemCount();
				repeatSelf = dim.getRepeatSelf();
				repeatGroup = dim.getRepeatGroup();

				itemIndex = getItemIndex(i, itemCount, repeatGroup, repeatSelf);

				mapStrArrIdx = j;
				item = dim.getItem(itemIndex);

				if(dim instanceof TimeDimension){
					String time = ((TimeDimension)dim).getTime();
					if(time == null){
						mapStrArr[mapStrArrIdx] = "#" + item.getCode();
					}else{
						if(time.equals("TIME_YEAR")){
							mapStrArr[mapStrArrIdx] = "$" + item.getCode();
						}else{
							//TIME_MQ
							mapStrArr[mapStrArrIdx] = "%" + item.getCode();
						}
					}
				}else{
					mapStrArr[mapStrArrIdx] = dim.getVarOrdSn() + ":" + item.getCode();
					//2014.12 차트항목단위
					if(dim instanceof ItemDimension){
						chartUnit = item.getChartUnitNm();
					}
				}

				//계층별 컬럼보기 여부에 따라
				if(levelExpr){
					int maxLevel = dim.getMaxLevel();
					int level = item.getLevel();

					String[] parentCodeArr = item.getParentCodeAsArray();
					String[] parentNameArr = item.getParentNameAsArray();

					//level이라...1부터 시작하는게 보기 좋겠지?
					for(int k = 1; k <= maxLevel; k++){
						if(level <= k){
							if(level == k){
								colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), item.getName(), item, dim) );
							}else{
								colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), item.getName(), item, dim) );
							}
						}else{
							if(level > k){
								//상위찍고
								parentItem = dim.getItem(parentCodeArr[k - 1]);

								if(parentItem != null){
									colMap.put( mapIdx++, makeCell(CellType.RD, parentItem.getCode(), parentItem.getName(), parentItem, dim) );
								}else{
									//cell = new Cell(CellType.RD, parentCodeArr[k-1], parentNameArr[k-1], dim.getVarOrdSn(),parentItem.getCode());
									//2020.07.07 else문에서 parentItem 가 null일때 여길 타는데 parentItem.getCode() 를 참조하고 있음...
									cell = new Cell(CellType.RD, parentCodeArr[k-1], parentNameArr[k-1], dim.getVarOrdSn(),"");
									colMap.put( mapIdx++, cell );
								}

							}else{
								//데이터는 자신이나 출력 시 소계찍고
								colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), item.getName(), item, dim) );
							}
						}
					}
				}else{
					//상위레벨 보기 여부에 따라
					if(paramInfo.getEnableParentLevel() != null && paramInfo.getEnableParentLevel().equals("Y")){
						colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), getIntend(item.getLevel()) + addParentName(item), item, dim) );
					}else{
						colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), getIntend(item.getLevel()) + item.getName(), item, dim) );
					}
				}

			}//end of for(int j = 0; j < rowDimCount; j++){

			String[] finalMapArr = new String[mapStrArr.length];
			for(int idx = 0; idx < mapStrArr.length; idx++){
				finalMapArr[idx] = mapStrArr[idx];
			}

			Arrays.sort(finalMapArr);

			Measure measure = resultMap.get(Arrays.asList(finalMapArr));
			String[] retValue = null;
			if(doAnal){
				retValue = getFormatedMeasure(measure, 2);
			}else{
				retValue = getFormatedMeasure(measure);
			}

			colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );

			dataBodyList.add(colMap);

		}//end of for(int i = 0; i < rowCount; i++){

	}

	private void makeHeadForRowZero(){

		int columnCount = columnAxis.getDimension(0).getItemCount() * columnAxis.getDimension(0).getRepeatSelf();

		int itemCount = 0;
		int repeatSelf = 0;
		int repeatGroup = 0;

		int itemIndex = 0;

		Dimension colDim = null;
		Cell cell = null;

		Map<Integer, Cell> colMap = null;
		Item item = null;
		Item parentItem = null;
		
		for(int h = 0; h < columnDimCount; h++){

			colDim = columnAxis.getDimension(h);

			itemCount = colDim.getItemCount();
			repeatSelf = colDim.getRepeatSelf();
			repeatGroup = colDim.getRepeatGroup();

			if(levelExpr){
				int colMaxLevel = colDim.getMaxLevel();

				for(int j = 1; j <= colMaxLevel; j++){
					int mapIdx = 0;
					colMap = new TreeMap<Integer, Cell>();
					item = null;
					parentItem = null;

					for(int i = 0; i < columnCount; i++){
						itemIndex = getItemIndex(i, itemCount, repeatGroup, repeatSelf);

						item = colDim.getItem(itemIndex);

						int level = item.getLevel();
						String[] parentCodeArr = item.getParentCodeAsArray();
						String[] parentNameArr = item.getParentNameAsArray();

						if(level <= j){
							colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), item.getName(), item, colDim) );
						}else{
							if(level > j){
								//상위찍고
								parentItem = colDim.getItem(parentCodeArr[j - 1]);

								if(parentItem != null){
									colMap.put( mapIdx++, makeCell(CellType.CD, parentItem.getCode(), parentItem.getName(), parentItem, colDim) );
								}else{
									cell = new Cell(CellType.CD, parentCodeArr[j - 1], parentNameArr[j - 1], colDim.getVarOrdSn(),colDim.getCode()+j);
									colMap.put( mapIdx++, cell );
								}

							}else{
								colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), item.getName(), item, colDim) );
							}
						}

					}

					dataHeaderList.add(colMap);
				}
			}else{

				colMap = new TreeMap<Integer, Cell>();
				int mapIdx = 0;
				item = null;
				for(int i = 0; i < columnCount; i++){
					itemIndex = getItemIndex(i, itemCount, repeatGroup, repeatSelf);

					item = colDim.getItem(itemIndex);
					//상위레벨 보기 여부에 따라
					if(paramInfo.getEnableParentLevel() != null && paramInfo.getEnableParentLevel().equals("Y")){
						colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), addParentName(item), item, colDim ));
					}else{
						colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), item.getName(), item, colDim) );
					}
				}

				dataHeaderList.add(colMap);
			}
		}

	}

	private void makeBodyForRowZero(){

		int columnCount = columnAxis.getDimension(0).getItemCount() * columnAxis.getDimension(0).getRepeatSelf();
		int mapStrArrIdx = 0;
		int itemCount = 0;
		int repeatSelf = 0;
		int repeatGroup = 0;

		String[] mapStrArr = null;

		if( (paramInfo.getEnableWeight() != null && paramInfo.getEnableWeight().equals("Y")) || (doAnal && originData) ){
			mapStrArr = new String[columnDimCount - 1];
		}else{
			mapStrArr = new String[columnDimCount];
		}

		Map<Integer, Cell> colMap = new TreeMap<Integer, Cell>();
		int mapIdx = 0;
		for(int k = 0; k < columnCount; k++){
			//수치값을 출력하기 위해 Col-Dimension을 이용하여 차원정보를 얻어낸다.

			Dimension dim = null;
			int itemIndex = 0;
			Item item = null;

			String chartUnit = "";

			for(int m = 0; m < columnDimCount; m++){
				dim = columnAxis.getDimension(m);

				//MeasureDimension은 표두의 마지막에 온다. 그러므로 빠져나온다.
				if(dim instanceof MeasureDimension){
					break;
				}

				itemCount = dim.getItemCount();
				repeatSelf = dim.getRepeatSelf();
				repeatGroup = dim.getRepeatGroup();

				itemIndex = getItemIndex(k, itemCount, repeatGroup, repeatSelf);
				item = dim.getItem(itemIndex);

				mapStrArrIdx = rowDimCount + m;

				if(dim instanceof TimeDimension){
					String time = ((TimeDimension)dim).getTime();
					if(time == null){
						mapStrArr[mapStrArrIdx] = "#" + item.getCode();
					}else{
						if(time.equals("TIME_YEAR")){
							mapStrArr[mapStrArrIdx] = "$" + item.getCode();
						}else{
							//TIME_MQ
							mapStrArr[mapStrArrIdx] = "%" + item.getCode();
						}
					}
				}else{
					mapStrArr[mapStrArrIdx] = dim.getVarOrdSn() + ":" + item.getCode();
					//2014.12 차트항목단위
					if(dim instanceof ItemDimension){
						chartUnit = item.getChartUnitNm();
					}
				}

				//System.out.println("column [" + mapStrArrIdx + "] ::: " + dim.getItem(itemIndex).getCode());
			}//end of for(int m = 0; m < columnDimCount; m++){

			String[] finalMapArr = new String[mapStrArr.length];
			for(int idx = 0; idx < mapStrArr.length; idx++){
				finalMapArr[idx] = mapStrArr[idx];
			}

			Arrays.sort(finalMapArr);

			Measure measure = resultMap.get(Arrays.asList(finalMapArr));

			//가중치
			if(paramInfo.getEnableWeight() != null && paramInfo.getEnableWeight().equals("Y")){

				if(mapIdx % 2 == 0){
					//원데이터
					String[] retValue = getFormatedMeasure(measure);
					colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
				}else{
					//가중치
					String[] retValue = getFormatedMeasure(measure,  1);
					colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
				}
			}else{
				//분석
				if(doAnal){
					//원자료함께보기
					if(originData){
						if(mapIdx % 2 == 0){
							//원데이터
							String[] retValue = getFormatedMeasure(measure);
							colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
						}else{
							//분석
							String[] retValue = getFormatedMeasure(measure,  2);
							colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
						}
					}
					//분석만 보기
					else{
						String[] retValue = getFormatedMeasure(measure, 2);
						colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
					}
				}
				//normal
				else{
					String[] retValue = getFormatedMeasure(measure);
					colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
				}
			}

		}//end of for(int k = 0; k < columnCount; k++){

		dataBodyList.add(colMap);
	}

	private void makeHeadForBoth(){

		int columnCount = columnAxis.getDimension(0).getItemCount() * columnAxis.getDimension(0).getRepeatSelf();

		int itemCount = 0;
		int repeatSelf = 0;
		int repeatGroup = 0;

		int itemIndex = 0;

		Cell cell = null;
		
		for(int i = 0; i < columnDimCount; i++){

			Dimension colDim = columnAxis.getDimension(i);
			itemCount = colDim.getItemCount();
			repeatSelf = colDim.getRepeatSelf();
			repeatGroup = colDim.getRepeatGroup();

			//계층별 컬럼보기의 경우
			if(levelExpr){
				int colMaxLevel = colDim.getMaxLevel();

				for(int j = 1; j <= colMaxLevel; j++){
					int mapIdx = 0;
					Map<Integer, Cell> colMap = new TreeMap<Integer, Cell>();

					//Row-Dimension의 td 생성
					for(int k = 0; k < rowDimCount; k++){
						Dimension rowDim = rowAxis.getDimension(k);
						int rowMaxLevel = rowDim.getMaxLevel();

						for(int m = 0; m < rowMaxLevel; m++){

							if(rowDim instanceof ClassDimension){
								colMap.put( mapIdx++, makeHeadCell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName() + "(" + (m + 1)+  ")", (ClassDimension)rowDim,m+1) );
							}else{
								cell = new Cell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName());
								colMap.put( mapIdx++, cell );
							}
						}
					}

					//Col-Dimension td 생성
					Item item = null;
					Item parentItem = null;

					for(int k = 0; k < columnCount; k++){
						itemIndex = getItemIndex(k, itemCount, repeatGroup, repeatSelf);

						item = colDim.getItem(itemIndex);
						int level = item.getLevel();
						String[] parentCodeArr = item.getParentCodeAsArray();
						String[] parentNameArr = item.getParentNameAsArray();

						if(level <= j){
							colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), item.getName(), item, colDim) );
						}else{
							if(level > j){
								//상위찍고
								parentItem = colDim.getItem(parentCodeArr[j - 1]);

								if(parentItem != null){
									colMap.put( mapIdx++, makeCell(CellType.CD, parentItem.getCode(), parentItem.getName(), parentItem, colDim) );
								}else{
									colMap.put( mapIdx++, new Cell(CellType.CD, parentCodeArr[j - 1], parentNameArr[j - 1], colDim.getVarOrdSn(),colDim.getCode()+j) );
								}

							}else{
								colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), item.getName(), item, colDim) );
							}
						}

					}

					dataHeaderList.add(colMap);

				}
			}else{
				//01. Row-Dimension 생성
				int mapIdx = 0;
				Map<Integer, Cell> colMap = new TreeMap<Integer, Cell>();

				Dimension rowDim = null;
				Item item = null;
				for(int j = 0; j < rowDimCount; j++){
					rowDim = rowAxis.getDimension(j);

					if(rowDim instanceof ClassDimension){
						colMap.put( mapIdx++, makeHeadCell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName(), (ClassDimension)rowDim,j+1) );
					}else{
						cell = new Cell(CellType.RH, rowHeadCell, rowDim.getCode(), rowDim.getName(), rowDim.getVarOrdSn());
						colMap.put( mapIdx++, cell );
					}
				}

				//02. Col-Dimension 생성
				for(int k = 0; k < columnCount; k++){
					itemIndex = getItemIndex(k, itemCount, repeatGroup, repeatSelf);
					item = colDim.getItem(itemIndex);

					//상위레벨 보기 여부에 따라
					if(paramInfo.getEnableParentLevel() != null && paramInfo.getEnableParentLevel().equals("Y")){
						colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), addParentName(item), item, colDim) );
					}else{
						colMap.put( mapIdx++, makeCell(CellType.CD, item.getCode(), item.getName(), item, colDim) );
					}
				}

				dataHeaderList.add(colMap);
			}
		}
	}

	private void makeBodyForBoth(){

		//줄 수를 가져온다.(기준이 될 for 문)
		int rowCount = rowAxis.getDimension(0).getItemCount() * rowAxis.getDimension(0).getRepeatSelf();
		int columnCount = columnAxis.getDimension(0).getItemCount() * columnAxis.getDimension(0).getRepeatSelf();

		int mapStrArrIdx = 0;
		int itemCount = 0;
		int repeatSelf = 0;
		int repeatGroup = 0;

		String cellStyle = null;

		//value를 추출하기 위한 String[] 변수를 선언한다.
		String[] mapStrArr = null;

		Cell cell = null;
		
		//가중치거나 분석시 원자료 함께보기인경우 처리
		if( (paramInfo.getEnableWeight() != null && paramInfo.getEnableWeight().equals("Y")) || (doAnal && originData) ){
			mapStrArr = new String[rowDimCount + columnDimCount - 1];
		}else{
			mapStrArr = new String[rowDimCount + columnDimCount];
		}

		for(int i = 0; i < rowCount; i++){
			mapStrArrIdx = 0;

			Map<Integer, Cell> colMap = new TreeMap<Integer, Cell>();
			int mapIdx = 0;

			itemCount = 0;
			repeatSelf = 0;
			repeatGroup = 0;

			Dimension dim = null;
			int itemIndex = 0;
			Item item = null;

			String chartUnit = "";

			//Row-Dimension 출력
			for(int j = 0; j < rowDimCount; j++){
				dim = rowAxis.getDimension(j);
				itemCount = dim.getItemCount();
				repeatSelf = dim.getRepeatSelf();
				repeatGroup = dim.getRepeatGroup();

				itemIndex = getItemIndex(i, itemCount, repeatGroup, repeatSelf);
				item = dim.getItem(itemIndex);

				mapStrArrIdx = j;

				if(dim instanceof TimeDimension){
					String time = ((TimeDimension)dim).getTime();
					if(time == null){
						mapStrArr[mapStrArrIdx] = "#" + item.getCode();
					}else{
						if(time.equals("TIME_YEAR")){
							mapStrArr[mapStrArrIdx] = "$" + item.getCode();
						}else{
							//TIME_MQ
							mapStrArr[mapStrArrIdx] = "%" + item.getCode();
						}
					}
				}else{
					mapStrArr[mapStrArrIdx] = dim.getVarOrdSn() + ":" + item.getCode();
					//2014.12 차트항목단위
					if(dim instanceof ItemDimension){
						chartUnit = item.getChartUnitNm();
					}
				}

				//System.out.println("row [" + mapStrArrIdx + "] ::: " + dim.getItem(itemIndex).getCode());
				//계층별 컬럼보기 여부에 따라
				if(levelExpr){
					int maxLevel = dim.getMaxLevel();

					Item parentItem = null;
					int level = item.getLevel();
					String[] parentCodeArr = item.getParentCodeAsArray();
					String[] parentNameArr = item.getParentNameAsArray();
					int jqheaderKey =0;
					//level이라...1부터 시작하는게 보기 좋겠지?
					for(int k = 1; k <= maxLevel; k++){
						jqheaderKey++;
						if(level <= k){
							colMap.put( mapIdx++, makeCell1(CellType.RD, item.getCode(), item.getName(), item, dim,jqheaderKey) );
						}else{
							if(level > k){
								//상위찍고
								parentItem = dim.getItem(parentCodeArr[k - 1]);
								//System.out.println("parentItem"+dim.getCode());
								if(parentItem != null){
									colMap.put( mapIdx++, makeCell1(CellType.RD, parentItem.getCode(), parentItem.getName(), parentItem, dim , jqheaderKey) );
								}else{
									cell = new Cell(CellType.RD, parentCodeArr[k - 1], parentNameArr[k - 1], dim.getVarOrdSn(),dim.getCode()+k);
									colMap.put( mapIdx++, cell);
								}

							}else{
								//자신찍고
								//colMap.put( mapIdx++, makeCell(CellType.RD, cellStyle, item.getCode(), item.getName(), item) );
								colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), item.getName(), item, dim) );
							}
						}
					}
				}else{

					//상위레벨 보기 여부에 따라
					if(paramInfo.getEnableParentLevel() != null && paramInfo.getEnableParentLevel().equals("Y")){
						colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), getIntend(item.getLevel()) + addParentName(item), item, dim) );
					}else{
						colMap.put( mapIdx++, makeCell(CellType.RD, item.getCode(), getIntend(item.getLevel()) + item.getName(), item, dim) );
					}
				}

			}//end of for(int j = 0; j < rowDimCount; j++){

			for(int k = 0; k < columnCount; k++){
				//수치값을 출력하기 위해 Col-Dimension을 이용하여 차원정보를 얻어낸다.
				for(int m = 0; m < columnDimCount; m++){
					dim = columnAxis.getDimension(m);

					//MeasureDimension은 표두의 마지막에 온다. 그러므로 빠져나온다.
					if(dim instanceof MeasureDimension){
						break;
					}

					itemCount = dim.getItemCount();
					repeatSelf = dim.getRepeatSelf();
					repeatGroup = dim.getRepeatGroup();

					itemIndex = getItemIndex(k, itemCount, repeatGroup, repeatSelf);
					item = dim.getItem(itemIndex);

					mapStrArrIdx = rowDimCount + m;

					if(dim instanceof TimeDimension){
						String time = ((TimeDimension)dim).getTime();
						if(time == null){
							mapStrArr[mapStrArrIdx] = "#" + item.getCode();
						}else{
							if(time.equals("TIME_YEAR")){
								mapStrArr[mapStrArrIdx] = "$" + item.getCode();
							}else{
								//TIME_MQ
								mapStrArr[mapStrArrIdx] = "%" + item.getCode();
							}
						}
					}else{
						mapStrArr[mapStrArrIdx] = dim.getVarOrdSn() + ":" + item.getCode();
						//2014.12 차트항목단위
						if(dim instanceof ItemDimension){
							chartUnit = item.getChartUnitNm();
						}
					}

				}//end of for(int m = 0; m < columnDimCount; m++){

				String[] finalMapArr = new String[mapStrArr.length];
				for(int idx = 0; idx < mapStrArr.length; idx++){
					finalMapArr[idx] = mapStrArr[idx];
				}

				Arrays.sort(finalMapArr);

				Measure measure = resultMap.get(Arrays.asList(finalMapArr));

				/*
				for(int m = 0; m < finalMapArr.length; m++){
					System.out.print(finalMapArr[m] + ",");
				}
				*/

				//가중치
				if(paramInfo.getEnableWeight() != null && paramInfo.getEnableWeight().equals("Y")){

					if((mapIdx - rowDimColCount) % 2 == 0){
						//원데이터
						String[] retValue = getFormatedMeasure(measure);
						colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
					}else{
						//가중치
						String[] retValue = getFormatedMeasure(measure,  1);
						colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
					}
				}else{
					//분석
					if(doAnal){
						//원자료함께보기
						if(originData){
							if((mapIdx - rowDimColCount) % 2 == 0){
								//원데이터
								String[] retValue = getFormatedMeasure(measure);
								colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
							}else{
								//분석
								String[] retValue = getFormatedMeasure(measure,  2);
								colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
							}
						}
						//분석만 보기
						else{
							String[] retValue = getFormatedMeasure(measure, 2);
							colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
						}
					}
					//normal
					else{
						String[] retValue = getFormatedMeasure(measure);
						colMap.put( mapIdx++, makeMeasureCell(CellType.VAL, valueCell, retValue[0], retValue[1], measure, chartUnit) );
					}
				}

			}//end of for(int k = 0; k < columnCount; k++){

			dataBodyList.add(colMap);

		}//end of for(int i = 0; i < rowCount; i++){
	}


	/**
	 * @param level
	 * @return String
	 * @see 계층이 있는 경우 앞에 공백을 넣기 위함
	 */
	private String getIntend(int level){
		/*
		StringBuffer intend = new StringBuffer();
		intend.append("");
		for(int z = 1; z < level; z++){
			intend.append("&nbsp;&nbsp;"); //두개씩 가야 표가 난다.
		}

		return intend.toString();
		*/
		return "";
	}

	private int getItemIndex(int rowNum, int itemCount, int repeatGroup, int repeatSelf){
		int itemIndex = 0;

		if(repeatGroup == 1){
			itemIndex = rowNum / repeatSelf;
		}else if(repeatSelf == 1){
			itemIndex = rowNum % itemCount;
		}else{
			int groupCount = itemCount * repeatSelf;
			itemIndex = (rowNum - (groupCount * ( rowNum / groupCount))) / repeatSelf;
		}

		return itemIndex;
	}

	private void setHeaderCount(){
		//01. Column
		if(columnDimCount > 0){
			if(levelExpr){
				for(int i = 0; i < columnDimCount; i++){
					Dimension dim = columnAxis.getDimension(i);
					colDimRowCount += dim.getMaxLevel();
				}
			}else{
				colDimRowCount = columnDimCount;
			}

			valRowBeginIdx = colDimRowCount;
		}else{
			valRowBeginIdx = colDimRowCount + 1;
		}

		//02. Row
		if(rowDimCount > 0){
			if(levelExpr){
				for(int i = 0; i < rowDimCount; i++){
					Dimension dim = rowAxis.getDimension(i);
					rowDimColCount += dim.getMaxLevel();
				}
			}else{
				rowDimColCount = rowDimCount;
			}

			valColBeginIdx = rowDimColCount;
		}
		//System.out.println(valColBeginIdx + "," + valRowBeginIdx);
	}

	private void decorateHtml(){

		Map<Integer, Cell> colMap = null;
		Map<Integer, Cell> upperColMap = null;

		//Row style 적용
		String[] rowVals = new String[valColBeginIdx];
		Object[] rowCellObjs = new Object[valColBeginIdx];
		//System.out.println("rowDimCount===========>"+rowDimCount);
		if(rowDimCount > 0){
			for(int i = 0; i < dataBodyList.size(); i++){
				colMap = (Map<Integer, Cell>)dataBodyList.get(i);

				Cell tmpCell = null;
				String tmpVal = null;
				Cell tmpUpperCell = null;
				for(int j = 0; j < valColBeginIdx; j++){

					tmpCell = (Cell)colMap.get(j);
					tmpVal = tmpCell.getValue();

					if(j > 0){
						tmpUpperCell = (Cell)colMap.get(j - 1);

						if(levelExpr && (tmpCell.getVarOrdSn() + ":" +  tmpVal).equals(tmpUpperCell.getVarOrdSn() + ":" + tmpUpperCell.getValue())){
							if(paramInfo.getDataOpt() != null && paramInfo.getDataOpt().indexOf("en") > -1){
								tmpCell.setText(pm.getProperty("string.level.dummytxt.en"));
								tmpCell.setDummy(true);
							}else{
								tmpCell.setText(pm.getProperty("string.level.dummytxt.ko"));
								tmpCell.setDummy(true);
							}

						}
					}

					if(rowVals[j] == null || !rowVals[j].equals(tmpVal)){
						rowVals[j] = tmpVal;
						tmpCell.setStyle(firstCell);

						rowCellObjs[j] = tmpCell;
					}else{

						if(tmpUpperCell != null && tmpUpperCell.getStyle().indexOf("first") > -1){
							tmpCell.setStyle(firstCell);
						}else{
							tmpCell.setStyle(mergeCell);
						}

					}
				}

			}
		}

		colMap = null;

		//Column style 적용
		if(columnDimCount > 0){

			String colVal = null;
			Cell colCell = null;

			Cell tmpCell = null;
			String tmpVal = null;

			Cell parentCell = null;
			Cell tmpUpperCell = null;
			
			for(int i = 0; i < valRowBeginIdx; i++){
				colMap = (Map<Integer, Cell>)dataHeaderList.get(i);

				//상위 동일한 컬럼index의 style 값에 따라 하위의 style을 merge로 할지 first로 할지 결정하기 위함.
				if(i > 0){
					upperColMap = (Map<Integer, Cell>)dataHeaderList.get(i - 1);
				}

				colVal = null;
				colCell = null;

				tmpCell = null;
				tmpVal = null;

				parentCell = null;
				tmpUpperCell = null;

				Iterator<Integer> iter = colMap.keySet().iterator();

				int tmpIdx = 0;
				while(tmpIdx < valColBeginIdx){
					iter.next();
					tmpIdx++;
				}

				Integer key = null;
				int j = 0;
				int span = 0;
				while(iter.hasNext()){

					key = iter.next();
					tmpCell = colMap.get(key);
					tmpVal = tmpCell.getValue();

					if(levelExpr && upperColMap != null){
						tmpUpperCell = upperColMap.get(key);
					}

					if( levelExpr && tmpUpperCell != null && (tmpCell.getVarOrdSn() + ":" + tmpVal).equals(tmpUpperCell.getVarOrdSn() + ":" + tmpUpperCell.getValue()) ){
						if(paramInfo.getDataOpt() != null && paramInfo.getDataOpt().indexOf("en") > -1){
							tmpCell.setText(pm.getProperty("string.level.dummytxt.en"));
							tmpCell.setDummy(true);
						}else{
							tmpCell.setText(pm.getProperty("string.level.dummytxt.ko"));
							tmpCell.setDummy(true);
						}
					}

					if(colVal == null){
						colVal = tmpVal;
						colCell = tmpCell;
					}else if(colVal.equals(tmpVal)){
						if(upperColMap != null){
							parentCell = upperColMap.get(key);
							if(parentCell.isFirst()){
								//tmpCell.setStyle(colHeadFirstCell);
								//tmpCell.setFirst(true);
								//tmpCell.setColspan(++span);

								colCell.setColspan(++span);
								colCell.setFirst(true);
								colCell.setStyle(colHeadFirstCell);

								colVal = tmpVal;
								colCell = tmpCell;
								span = 0;
							}else{
								tmpCell.setStyle(colHeadMergeCell);
								++span;
							}
						}else{
							tmpCell.setStyle(colHeadMergeCell);
							++span;
						}

					}else{
						if(colCell != null){
							colCell.setColspan(++span);
							colCell.setFirst(true);
							colCell.setStyle(colHeadFirstCell);
							//System.out.println("#### ::: " + colCell.getText());

							if( levelExpr && tmpUpperCell != null && (tmpCell.getVarOrdSn() + ":" +  tmpVal).equals(tmpUpperCell.getVarOrdSn() + ":" + tmpUpperCell.getText()) ){
								if(paramInfo.getDataOpt() != null && paramInfo.getDataOpt().indexOf("en") > -1){
									tmpCell.setText(pm.getProperty("string.level.dummytxt.en"));
									tmpCell.setDummy(true);
								}else{
									tmpCell.setText(pm.getProperty("string.level.dummytxt.ko"));
									tmpCell.setDummy(true);
								}
							}

							colVal = tmpVal;
							colCell = tmpCell;
							span = 0;
						}
					}

					j++;
				}

				
				System.out.println("tmpVal**************"+tmpVal);
				System.out.println("colVal**************"+colVal);
				
				
				//마지막 값 처리
				if(colVal.equals(tmpVal)){
					colCell.setColspan(++span);
					colCell.setFirst(true);
					colCell.setStyle(colHeadFirstCell);
				}else{
					colCell.setColspan(1);
					colCell.setFirst(true);
					colCell.setStyle(colHeadFirstCell);
				}
			}
		}
	}

	//Null 셀 삭제
	private void removeNullData(){

		Map<Integer, Cell> colMap = null;

		//Row 삭제
		if(rowDimCount > 0){
			
			Cell cell = null;
			String val = null;
			
			for(int i = 0; i < dataBodyList.size(); i++){

				boolean rowDelete = true;
				colMap = (Map<Integer, Cell>)dataBodyList.get(i);

				cell = null;
				val = null;
				
				for(int j = valColBeginIdx; j < colMap.size(); j++){
					cell = (Cell)colMap.get(new Integer(j));

					val = cell.getValue();
					if(!val.equals("@null@")){
						rowDelete = false;
						break;
					}
				}

				if(rowDelete){
					dataBodyList.remove(i);
					i--;
				}

			}
		}

		colMap = null;

		//Column 삭제
		if(columnDimCount > 0){

			int columnCount = columnAxis.getDimension(0).getItemCount() * columnAxis.getDimension(0).getRepeatSelf();

			Cell cell = null;
			String val = null;
			
			for(int i = valColBeginIdx; i < columnCount + valColBeginIdx; i++){
				boolean colDelete = true;
				cell = null;
				val = null;
				
				for(int j = 0; j < dataBodyList.size(); j++){
					colMap = (Map<Integer, Cell>)dataBodyList.get(j);
					cell = colMap.get(i);
					val = cell.getValue();
					if(!val.equals("@null@")){
						colDelete = false;
						break;
					}
				}

				if(colDelete){
					//Row 에서 해당 컬럼을 다 삭제한다.
					//Header
					for(int j = 0; j < dataHeaderList.size(); j++){
						colMap = (Map<Integer, Cell>)dataHeaderList.get(j);
						colMap.remove(i);
					}

					//Body
					for(int j = 0; j < dataBodyList.size(); j++){
						colMap = (Map<Integer, Cell>)dataBodyList.get(j);
						colMap.remove(i);
					}
				}

			}//for(int i = valColBeginIdx; i < columnCount + valColBeginIdx; i++){
		}//if(columnDimCount > 0){

	}

	//주석여부 판단을 위해 이곳에서 처리한다. (분류)
	public Cell makeHeadCell(int cellType, String style, String code, String name, ClassDimension dim, int levelCnt){
		Cell cell = new Cell(cellType, style, code, name);

		cell.setVarOrdSn(dim.getVarOrdSn());

		//cmmtNo가 0보다 크면 주석이 존재
		if(dim.getCmmtNo() > 0){
			cell.setCmmtNo(dim.getCmmtNo());
			cell.setCmmtSe(dim.getCmmtSe());
			cell.setObjVarId(dim.getCode());
		}

		if(levelExpr){
			cell.setRowHeadId(cell.getValue()+levelCnt);
		}


		return cell;
	}

	//주석여부 판단을 위해 이곳에서 처리한다. (분류값 및 항목 셀)
	//item.getCode, item.getName은 레벨보기 여부, 상위레벨 보기 여부에 따라 다르므로 각 위치에서 처리하도록 한다.
	public Cell makeCell(int cellType, String code, String name, Item item, Dimension dim){

		Cell cell = new Cell(cellType, code, name, dim.getVarOrdSn(),dim.getCode());
		cell.setLevel(item.getLevel());
		cell.setVarOrdSn(dim.getVarOrdSn());
		cell.setRowHeadId(dim.getCode());

		//cmmtNo가 0보다 크면 주석이 존재
		if(item.getCmmtNo() > 0){
			cell.setCmmtNo(item.getCmmtNo());
			cell.setCmmtSe(item.getCmmtSe());
			cell.setObjVarId(item.getObjVarId());
			cell.setItmId(item.getCode());
		}

		if(levelExpr){
			cell.setRowHeadId(dim.getCode()+item.getLevel());
		}

		//System.out.println("cell.setRowHeadId----->"+cell.getRowHeadId());
		return cell;
	}

	//챠트정보를 계층별 컬럼기준 case만 따로 처리한다. (분류값 및 항목 셀) A.Y.S
	public Cell makeCell1(int cellType, String code, String name, Item item, Dimension dim,int jqheaderKey){

		Cell cell = new Cell(cellType, code, name, dim.getVarOrdSn(),dim.getCode());
		cell.setLevel(item.getLevel());
		cell.setVarOrdSn(dim.getVarOrdSn());
		cell.setRowHeadId(dim.getCode());

		//cmmtNo가 0보다 크면 주석이 존재
		if(item.getCmmtNo() > 0){
			cell.setCmmtNo(item.getCmmtNo());
			cell.setCmmtSe(item.getCmmtSe());
			cell.setObjVarId(item.getObjVarId());
			cell.setItmId(item.getCode());
		}

		if(levelExpr){
			cell.setRowHeadId(dim.getCode()+jqheaderKey);				//맵핑키 생성
		}
		return cell;
	}

	//주석여부 판단을 위해 이곳에서 처리한다. (수치 셀)
	//item.getCode, item.getName은 레벨보기 여부, 상위레벨 보기 여부에 따라 다르므로 각 위치에서 처리하도록 한다.
	public Cell makeMeasureCell(int cellType, String style, String code, String name, Measure measure, String chartUnit){

		Cell cell = new Cell(cellType, style, code, name);
		cell.setChartUnit(chartUnit);
		//cmmtNo가 0보다 크면 주석이 존재
		if(measure != null && measure.getCmmtNo() > 0){
			cell.setCmmtNo(measure.getCmmtNo());
			cell.setCmmtSe(measure.getCmmtSe());
			cell.setItmRcgnSn(measure.getItmRcgnSn());
		}

		return cell;
	}

	//DTVAL_CO 처리 parameter 1개일 경우 DTVAL_CO를 return 한다.
	public String[] getFormatedMeasure(Measure measure){
		return getFormatedMeasure(measure, 0);
	}

	/*
	 * type - 0 : DTVAL_CO, 1 : WGT_CO, 2 : ANAL_CO
	 */

	//2013.10.23
	//StatPivotUtil 에도 동일한 명의 메소드가 있으므로 이쪽에서 수정시  StatPivotUtil 메소드도 동일하게 수정해줘야 함.
	public String[] getFormatedMeasure(Measure measure, int type){

		//첫번째는 수치테이블 자료 그대로..(정렬시 이 데이터를 사용)
		//두번째는 소수점 적용 및 단위 붙이고, 천단위로 ','로 포함해서 넘김
		String retStr[] = new String[2];

		if(measure == null){
			return new String[]{"@null@", "-"}; // 값이 없을 경우 - 로 표시
		}

		String data = null;
		String dataUnit = null;

		String value = null;

		switch(type){
			case 0 :
				value = measure.getDtvalCo();
				break;
			case 1 :
				value = measure.getWgtCo();
				break;
			case 2 :
				value = measure.getAnalCo();
				break;
		}

		if(value == null){
			value = "";
		}

		String smblCn = measure.getSmblCn();
		String periodCo = measure.getPeriodCo();
		String unitId = measure.getUnitId();

		String dataOpt = paramInfo.getDataOpt();

		String value_buff = value; // 2014.12.15 수치값이 나라이름같은 문자열이고 쉼표가 들어가있을 수 있기때문에 숫자형태 체크전에 백업후 나중에 숫자가아닌 문자열일경우 원복 - 김경호

		value = value.replaceAll(",", "");
		String period = null;

		boolean isNumber = StatPivotUtil.isNumericValue(value);

		//소수점 자리수
		if(isNumber){

			if(value.startsWith(".")){
				value = "0" + value;
			}

			if(paramInfo.getPeriodCo() == null || paramInfo.getPeriodCo().trim().length() == 0){

				if(periodCo != null && periodCo.trim().length() > 0){
					period = periodCo;
				}

				//분석인경우 재설정
				//기여도, 기여율, 누계구성비의 경우 소수점 2자리로
				//증감율, 구성비인 경우 소수점 1자리 (2014.08.04 구성비인 경우 소수점 1자리로 - 김경호)
				if(type == 2 && doAnal){
					if(analType.equals("CHG_RATE_CO") || analType.equals("CHG_RATE_CO_R") || analType.equals("TOTL_CMP_RATE")){
						period = "2";
					}else if(analType.equals("CHG_RATE") || analType.equals("CMP_RATE") ){
						period = "1";
					}
					//증감이면서 fnExcptCd가 POINT 인 경우 소숫점 1자리
					else if(analType.equals("CHG") && fnExcptCd.equals("POINT")){
						period = "1";
					}
				}

			}else{
				period = paramInfo.getPeriodCo();
			}
		}

		if(type == 1){
			period = null;
		}

		if( period != null){
			if( period.equals("99")){	// 2017-08-23 수록자료형식과 동일은 tn_dt에 들어가 있는 수치 그대로 적용 - 이원영
				period = null;
			}
		}
		
		Boolean smblCn_Chk = false;
		
		if(value == null || value.trim().length() == 0){
			if(smblCn == null || smblCn.trim().length() == 0){
				data = ""; // 2015.02.23 더미여부는 공란으로
			}else{
				if(smblCn.equals("x")){
					data = "X";
				}else{
					//... 인 경우
					data = smblCn;
				}
			}
		}else if(type == 0 && value.equals("99999999999.99999")){
			//99999999999.99999 인경우 미상자료
			data = "...";
		}else{
			if(smblCn == null || smblCn.trim().length() == 0){
				data = (isNumber) ? StatPivotUtil.getNumberFormatString(value, period, 0) : value_buff;
			}else{
				if( smblCn.equals("x")){ // 2016.09.13 smbl_cn에 비밀보호코드 (x: 소문자 엑스)로 되어 있을 경우 수치를 X로 치환하여 알 수 없게 한다.(이원영) - 김경호
					data = "X";
				}else if( smblCn.equals("...")){ // 2017.08.23 smbl_cn에 미상 (...)로 되어 있을 경우 수치를 (...)로 치환하여 알 수 없게 한다.(이원영) - 김경호
					data = "...";
				}else if( smblCn.equals("-")){ // 2017.08.23 smbl_cn에 수치없음 (-)로 되어 있을 경우 수치를 (-)로 치환하여 알 수 없게 한다.(이원영) - 김경호
					data = "-";
				}else{
					//2019.03.12 수치와 통계부호, 셀단위등을 붙여서 저장하면 통계부호를 따로 구분할 수 없기 때문에 [{ }] 로 감싸서 추후에 가공할 수 있도록 수정
					//data = (isNumber) ? StatPivotUtil.getNumberFormatString(value, period, 0) + smblCn : value_buff + smblCn;
					
					smblCn_Chk =true;
					data = (isNumber) ? StatPivotUtil.getNumberFormatString(value, period, 0) + "[{"+smblCn+ "}]" : value_buff + "[{"+smblCn+ "}]";
				}
			}
		}
		
		/*분석이고 숫자값일 경우 - 시작
		 * 0값인 수치에 마이너스가 붙어있으면 빼고 보여주도록 수정 - 박진현, 여인철 주무관 19.09.24
		 * */
		if(type == 2 && doAnal && isNumber){
			if( data.length() > 1){
				if("-".equals(data.substring(0, 1))){ // 마이너스 값일 경우
					if(smblCn_Chk){ // 통계부호나 셀단위가 붙은 값일 경우
						if(data.indexOf("-0.0000[{") > -1){
							data = data.replace("-0.0000[{", "0.0000[{");
						}else if(data.indexOf("-0.000[{") > -1){
							data = data.replace("-0.000[{", "0.000[{");
						}else if(data.indexOf("-0.00[{") > -1){
							data = data.replace("-0.00[{", "0.00[{");
						}else if(data.indexOf("-0.0[{") > -1){
							data = data.replace("-0.0[{", "0.0[{");
						}else if(data.indexOf("-0[{") > -1){
							data = data.replace("-0[{", "0[{");
						}
					}else{ // 그냥 숫자일 경우 0이나 0.00 같은 그냥 0인값에 (-) 마이너스가 붙어있으면 빼고 보여줌
						if(data.equals("-0.0000")){
							data = "0.0000";
						}else if(data.equals("-0.000")){
							data = "0.000";
						}else if(data.equals("-0.00")){
							data = "0.00";
						}else if(data.equals("-0.0")){
							data = "0.0";
						}else if(data.equals("-0")){
							data = "0";
						}
					}
				}
			}
		}
		/*분석이고 숫자값일 경우 - 끝*/
		
		//단위 표현인 경우
		if(paramInfo.getEnableCellUnit() != null && paramInfo.getEnableCellUnit().equals("Y")){
			if(dataOpt.equals("ko")){
				dataUnit = data + ( (unitId != null && unitId.trim().length() > 0) ? " (" + measure.getUnitNmKor() + ")" : "" );
			}else if(dataOpt.equals("en")){
				dataUnit = data + ( (unitId != null && unitId.trim().length() > 0) ? " (" + measure.getUnitNmEng() + ")" : "" );
			}else if(dataOpt.equals("cd")){
				dataUnit = data + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitId + ")" : "" );
			}else if(dataOpt.equals("cdko")){
				dataUnit = data + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitId + " " + measure.getUnitNmKor() + ")" : "" );
			}else if(dataOpt.equals("cden")){
				dataUnit = data + ( (unitId != null && unitId.trim().length() > 0) ? " (" + unitId + " " + measure.getUnitNmEng() + ")" : "" );
			}
		}else{
			dataUnit = data;
		}

		//value도 소수점 자리수 적용해야 함
		if(isNumber){
			value = StatPivotUtil.getNumberFormatString(value, period, 1);
		}

		retStr[0] = value;
		retStr[1] = dataUnit;

		return retStr;

	}

	//상위레벨 보기  'Y'의 경우 상위레벨값을 보여준다.
	public String addParentName(Item item){
		String retStr = "";

		String parentCode = item.getParentCode();
		String parentNameKor = item.getParentNameKor();
		String parentNameEng = item.getParentNameEng();

		String dataOpt = paramInfo.getDataOpt();

		if(dataOpt.equals("ko")){
			if(parentNameKor == null){
				retStr = item.getName();
			}else{
				retStr = parentNameKor.replaceAll("@", " > ") + item.getName();
			}
		}else if(dataOpt.equals("en")){
			if(parentNameEng == null){
				retStr = item.getName();
			}else{
				retStr = parentNameEng.replaceAll("@", " > ") + item.getName();
			}
		}else if(dataOpt.equals("cd")){
			if(parentCode == null){
				retStr = item.getCode();
			}else{
				retStr = parentCode.replaceAll("@", " > ") + item.getCode();
			}
		}else if(dataOpt.equals("cdko")){
			//cd
			if(parentCode == null){
				retStr = item.getCode() + " " + item.getName();
			}else{
				retStr = parentCode.replaceAll("@", " > ") + " > " + item.getCode();
				//ko
				if(parentNameKor != null){
					retStr = retStr + " " +parentNameKor.replaceAll("@", " > ") + item.getName();
				}
			}

		}else if(dataOpt.equals("cden")){
			//cd
			if(parentCode == null){
				retStr = item.getCode() + " " + item.getName();
			}else{
				retStr = parentCode.replaceAll("@", " > ") + item.getCode();
				//en
				if(parentNameEng != null){
					retStr = retStr + " " +parentNameEng.replaceAll("@", " > ") + item.getName();
				}
			}

		}

		return retStr;
	}

	public void write(){}

}

