package nurimsoft.stat.pivot.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.CellType;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.stat.util.StatPivotUtil;
import egovframework.rte.psl.dataaccess.util.EgovMap;

/**
 * 퓨전챠트 클래스
 * 챠트만을 위한 렌더러 정보
 * 범례표기방식을 표 형식으로 변경->NSO.jsp jqGrid header body 세팅
 * 피봇정보 :row=분류,항목 col=시점
 * @author 안영수
 * @since 2014.12.03
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *	수정일		수정자		수정내용
 *	----------	--------	---------------------------
 *	2014.12.03	안영수		최초 생성
 *
 *  2015.11.19  남규옥		분류없는 통계표가 항목1개로 구성되어 있으면 차트조합이 불가하지만
 *							시점이 여러개일 경우에는 차트의 의미가 있으므로 임의값을 넣어 범례가 조합될 수 있도록 수정
 *  						그리고, 조회하기 위해 선택한 차원*시점이 1일 경우에는 차트 나오지 않도록 막음
 *
 * </pre>
 */
public class ChartRenderer extends Renderer{


	public StringBuffer cmmtText = new StringBuffer();

//	StringBuffer chartTextMerge = new StringBuffer();
	public Map chartData = new HashMap();
	public	 List chartTextMerge = new ArrayList();
	final String rowHeadCell	= " class='rowHead'";
	final String colHeadFirstCell	= " class='colHead-first'";
	final String colHeadMergeCell	= " class='colHead-merge'";
	final String firstCell 		= " class='first'";
	final String mergeCell 		= " class='merge'";
	final String valueCell 		= " class='value'";

	final int CD_WIDTH = 97;
	final int RD_WIDTH = 147;

	public ChartRenderer(ParamInfo paramInfo, Map<List<String>,  Measure> resultMap, RowAxis cRowAxis, ColumnAxis cColumnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager){
		super(paramInfo,resultMap, cRowAxis, cColumnAxis, levelExpr, cmmtInfoManager);

	}

	//Overriding
	@SuppressWarnings("unchecked")
	public void write(){
		//colResize를 위한 map
		Map<Integer, Cell> reSizeMap = null;
		reSizeMap = (Map<Integer, Cell>)dataList.get(1);	   // 한 row에 cell 수를 추출

		Map<Integer, Cell> colMap = null;
		int dataListSize = dataList.size();

		List chartRowArr = new ArrayList();
		int chartCreatCnt = 0;					//rowNm Count로 챠트생성 여부 판단

		String chartMsgFlag="Y";				//Y,N여부에 따라 nso.jsp에서 챠트 클릭시 메시지 출력

		// 테스트
		List remarkHeader = new ArrayList();

		//범례 구분 정보
		EgovMap colorType = new EgovMap();

		/**
		if(paramInfo.getDataOpt().indexOf("en") > -1){
			colorType.put("title", "Chart-color");
		}else{
			colorType.put("title", "구분");
		}
		**/

		String chartEnable = "";
		colorType.put("title", "");

		colorType.put("expression","cT");
		remarkHeader.add(colorType);

		List levelExprArr = new ArrayList();
		// 테스트
		List<Map> remarkBody = new ArrayList<Map>();
		String levelCell ="";
		
		int cdCnt = 0;	// 151119 남규옥 ::: 시점 개수

		List tdValueArr = new ArrayList();
		List tdUnitArr = new ArrayList(); //2014.12 차트항목단위

		Iterator<Integer> iter = null;
		Integer key = null;
		//chart 표측 cellText세팅
		Map chartCol = null;
		HashMap remarkB = null;
		
		Cell cell = null;
		
		for(int i = 0; i < dataListSize; i++){

		//	List chartValue = new ArrayList();
			tdValueArr = new ArrayList();
			tdUnitArr = new ArrayList(); //2014.12 차트항목단위

			colMap = (Map<Integer, Cell>)dataList.get(i);
			iter = colMap.keySet().iterator();
			key = null;
			//chart 표측 cellText세팅
			chartCol = new HashMap();
			//chart addFlag;
			int chartAddCnt = 0;
			String chartText = "";
			int idxLableCnt = 0;
			remarkB = new HashMap();

			int levelCnt = 0;

			while(iter.hasNext()){
				key = iter.next();
				cell = colMap.get(key);
				int cellType = cell.getType();
				String cellValue = cell.getValue();
				String cellStyle = cell.getStyle();
				String cellText = cell.getText();

				/** 2015.01.23.YS 챠트범례 표기중 계층별 컬럼구분 기본인경우 JqGrid 동적컬럼 맵핑키 생성
				 *  분류키 정보만 renderer 수정
				 *  **/
				if(cellType == CellType.RH){

					EgovMap remarkH = new EgovMap();
					if(i == 0){
						remarkH.put("title", cellText);
						if(levelExpr){										//계층별 컬럼구분 기본인 경우

							levelCell = cellValue+1 ;						//계층별 구분 항목 key 생성
							if(!cellValue.equals("13999001")){				//항목이 아닌 것만 세팅
								levelExprArr.add(levelCell);
								remarkH.put("expression",cell.getRowHeadId());
							}else{
								remarkH.put("expression",levelCell);			//일반
							}

						}else{
							remarkH.put("expression",cellValue);			//일반
						}
						remarkHeader.add(remarkH);
						levelCnt++;
					}
				}else if(cellType == CellType.CD){
					if(cell.isFirst()){
						//첫번째 ColSpan 추출
						chartAddCnt++;
					}

					//챠트 Lable세팅
					if(i==0){
						chartTextMerge.add(cellText);

					}else{
						String str="";

						if(i!=colDimRowCount){
							str = (String)chartTextMerge.get(idxLableCnt);
							str+="/"+cellText;
							chartTextMerge.set(idxLableCnt, str);

						}
					}

					cdCnt++;	// 151119 남규옥 ::: 시점개수
					
					idxLableCnt++;

				}else if(cellType == CellType.VAL){

					//챠트 기본 tdValue
					if(cellValue == "@null@"){							//ToDO '-' 세팅 치환 대기
						cellValue="0";
					}
					//숫자체크해서 문자면 0으로  치환
					boolean isNumber = StatPivotUtil.isNumericValue(cellValue);
					if(!isNumber){
						cellValue="0";
					}
					tdValueArr.add(cellValue);
					tdUnitArr.add(cell.getChartUnit());	//2014.12 차트항목단위
					//System.out.println(cell.getText() + "," + cell.getChartUnit());
				}else{
					if(isSort){
						chartAddCnt++;
					}else{

						if(cellStyle.indexOf("merge") > -1){
							//merge
							if(i == dataListSize - 1){
							}else{

							}
						}else{
							//first
							if(i == dataListSize - 1){
								chartText = cellText+"/";

							}else{
								chartText+=cellText+"/";
								//계층별 컬럼기준
							}

						}

						//style이 merge인 경우 빈 공백 출력
						if(cellStyle.indexOf("merge") > -1){
							//System.out.println("merge"+cellText);
							chartText += cellText+"/";
							//System.out.println("mergeEnd"+chartText);

						}

						remarkB.put(cell.getRowHeadId(),cellText);
						remarkB.put("cT","■");

						if(chartRowArr.size()<20){
							String chartTextSlice = chartText.substring(0,chartText.length()-1);
							if(chartTextSlice != null){
								chartCol.put("rowNm",chartTextSlice);
								chartCreatCnt++; //챠트생성을 위한 rowNm 생성갯수 chartRowArr.size로 챠트생성여부 판단불가
							}
						}

					}
				}

			}

			if(chartRowArr.size()<20){
				if(chartAddCnt == 0){
					chartCol.put("rowData",tdValueArr);
					chartCol.put("rowUnit",tdUnitArr); //2014.12 차트항목단위
					chartRowArr.add(chartCol);
					remarkBody.add(remarkB);
				}
			}
		}

		//챠트 세팅
		if(chartCreatCnt==0){				//rowNm 정보가 0 이면 챠트조합 불가
			// 151119 남규옥 추가 시작 
			// 분류없는 통계표가 항목1개로 구성되어 있으면 차트조합이 불가하지만
			// 시점이 여러개일 경우에는 차트의 의미가 있으므로 임의값을 넣어줘서 조합될 수 있도록 수정함
			if(cdCnt > 1){	// 시점이 여러개일때
				EgovMap remarkHtmp = new EgovMap();
				EgovMap remarkBtmp = new EgovMap();

				PropertyManager pm = PropertyManager.getInstance();
				String tmpVal = "";
				
				if(paramInfo.getDataOpt() != null && paramInfo.getDataOpt().indexOf("en") > -1){
					tmpVal = pm.getProperty("string.head.val.en");	// 영문표현
				}else{
					tmpVal = pm.getProperty("string.head.val.ko");	// 한글표현
				}
				
				remarkHtmp.put("title", tmpVal);
				remarkHtmp.put("expression", "data");
				remarkHeader.add(remarkHtmp);
				remarkBody.clear();
				remarkBtmp.put("cT", "■");
				remarkBtmp.put("data", tmpVal);
				remarkBody.add(remarkBtmp);
			}else{
			// 151119 남규옥 추가 끝				
				chartMsgFlag = "N";
			// 151119 남규옥 추가 시작
			}
			// 151119 남규옥 추가 끝
		}
		
		// 151119 남규옥 추가 시작 ::: 차원*시점의 개수가 1이면 차트조합 하지 않음
		if(Integer.parseInt(paramInfo.getItemMultiply()) * cdCnt == 1){	
			chartMsgFlag = "N";			
		}
		// 151119 남규옥 추가 끝	
		
		chartData.put("lable",chartTextMerge);
		chartData.put("data", chartRowArr);
		chartData.put("msg", chartMsgFlag);
		chartData.put("remarkH",remarkHeader);
		chartData.put("remarkB",remarkBody);
		chartData.put("enable", chartEnable);

		/**
		System.out.println("levelExprArr"+levelExprArr);
		for(int i=0; i<remarkHeader.size(); i++){
			System.out.println("헤더정보"+remarkHeader.get(i));
		}

		for(int i=0; i<remarkBody.size(); i++){
			System.out.println("Body정보"+remarkBody.get(i));
		}

		System.out.println("chartTextMerge"+chartTextMerge);
		System.out.println("chartTextMerge"+chartRowArr);
		**/
	}
}
