package nurimsoft.stat.pivot.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nurimsoft.stat.info.CmmtInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.CellType;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.util.MessageManager;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.webapp.StatHtmlDAO;

/**
 * @author leekyujeong
 *
 */
public class HtmlRenderer_bk20141203 extends Renderer{

	public StringBuffer htmlText = new StringBuffer();
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

	public HtmlRenderer_bk20141203(ParamInfo paramInfo, Map<List<String>,  Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager){
		super(paramInfo,resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager);

	}

	//Overriding
	@SuppressWarnings("unchecked")
	public void write(){

		//colResize를 위한 map
		Map<Integer, Cell> reSizeMap = null;
		reSizeMap = (Map<Integer, Cell>)dataList.get(1);	   // 한 row에 cell 수를 추출
		System.out.println("reSizeMap.size()"+reSizeMap.size());
		htmlText.append("<table id='mainTable'>"); //id필수
		htmlText.append("<colgroup>");						   //colResize 기능을위한 colgroup 세팅

		 for(int i=0; i<reSizeMap.size();i++){
			 if(valColBeginIdx>i){
				 htmlText.append("<col class='Selectable1'/>");	//세로 1번째컬럼 넓이

			 }else{
				 htmlText.append("<col class='Selectable2'/>");  //세로 2번째컬럼 넓이
			 }

//			 htmlText.append("<col class='Selectable'/>");	//세로 1번째컬럼 넓이
		 }
		htmlText.append("</colgroup>");
		htmlText.append("<thead>");


		Map<Integer, Cell> colMap = null;
		System.out.println("dataList.size()"+dataList.size());
		int dataListSize = dataList.size();
		System.out.println("colDimRowCount ::: " + colDimRowCount);
		System.out.println("rowDimColCount ::: " + rowDimColCount);

		List chartLableArr = new ArrayList();
		List chartLableTailArr = new ArrayList();

		String chartLabelHead = "";
		List chartRowArr = new ArrayList();
		int chartCreatCnt = 0;					//rowNm Count로 챠트생성 여부 판단
		boolean chartLabelFirst =true;			//챠트 첫번째 기본 true
		boolean labelFirst = true;
		String chartMsgFlag="Y";				//Y,N여부에 따라 nso.jsp에서 챠트 클릭시 메시지 출력
		int standardLoop =0;
		int labelSetCnt = 0;
		for(int i = 0; i < dataListSize; i++){

		//	List chartValue = new ArrayList();
			List tdValueArr = new ArrayList();
			List tdUnitArr = new ArrayList(); //2014.12 차트항목단위
			htmlText.append("<tr>");

			colMap = (Map<Integer, Cell>)dataList.get(i);
			Iterator<Integer> iter = colMap.keySet().iterator();
			Integer key = null;
			//chart 표측 cellText세팅
			Map chartCol = new HashMap();
			//chart addFlag;
			int chartAddCnt=0;
			String chartText ="";
			int idxLableCnt = 0;
			while(iter.hasNext()){
				key = iter.next();
				Cell cell = colMap.get(key);
				int cellType = cell.getType();
				String cellValue = cell.getValue();
				String cellStyle = cell.getStyle();
				String cellText = cell.getText();
				int cellCmmtNo = cell.getCmmtNo();
				int level = cell.getLevel();
		//		System.out.println("iter.hasNext////cellText====>"+cellText);
				if(cellType == CellType.RH){
					//System.out.println("cellType.RH////cellText====>"+cellText);
					if(i == 0){
						if(colDimRowCount == 0){
							htmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' title='" + cellText + "'>");
							//System.out.println("colDimRowCount==0"+colDimRowCount);
						}else{
							htmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' rowspan='" + colDimRowCount + "' title='" + cellText + "'>");
							//System.out.println("colDimRowCount===>else"+colDimRowCount);
						}
						//System.out.println("i갯수==>"+i);
						if(cellCmmtNo > 0){
							htmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a>");
						}else{
							htmlText.append("<span>&nbsp;</span>");
						}
						htmlText.append("<span class='text'>");
						htmlText.append(cellText);
						htmlText.append("</span>");
						htmlText.append("</th>");
						chartAddCnt++;
					}
				}else if(cellType == CellType.CD){
					if(cell.isFirst()){
						//첫번째 ColSpan 추출
						htmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' colspan='" + cell.getColspan() + "' title='" + cellText.replaceAll("&nbsp;", "") + "'>");
//						System.out.println("cell.isFirst////cellText====>"+cellText);
//						//챠트
//						String chartLabeltail ="";
//						System.out.println("getColSpan"+cell.getColspan());
//						if(chartLabelFirst){
//						//	chartLoopSize = cell.getColspan();					//챠트첫번째 컬럼 colSpan갯수 세팅
//							if(cell.getColspan() > 1){
//								chartLabelHead ="";
//								chartLabelHead = cellText;
//								System.out.println("chartLabelHead-----:::::"+chartLabelHead);
//								chartLabelFirst = false;
//							}
//						}else{
//							chartLabeltail = "/"+cellText;
//					//		chartLabeltail = chartLabelHead.concat(chartLabeltail);
//							System.out.println("chartLabeltail-----:::::"+chartLabeltail);
//							chartLableTailArr.add(chartLabeltail);
//						}

						if(cellCmmtNo > 0 && !cell.isDummy()){
							htmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
						}else{
							htmlText.append("<span>&nbsp;</span>");
						}
						htmlText.append("<span style='width:" + (cell.getColspan() * CD_WIDTH) + "px;word-wrap:break-word;'>");
						htmlText.append(cellText);
						htmlText.append("</span>");
						htmlText.append("</th>");
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
					idxLableCnt++;

				}else if(cellType == CellType.VAL){

					htmlText.append("<td class='" + cellStyle + "' title='" + cellText + "'>");

					if(cell.getCmmtNo() > 0){
						htmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
					}else{
						htmlText.append("<span>&nbsp;</span>");
					}
					htmlText.append("<span class='val'>");
					htmlText.append( cellText );
					//htmlText.append( cellText + "@" + cell.getChartUnit() ); //chart에 제공할 데이터
					htmlText.append("</span>");
					htmlText.append("<input type='hidden' value='"+cellValue+"'/>");//데이터찾기
					htmlText.append("</td>");
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

					int spaceWidth = 5;
					if(levelExpr){
						spaceWidth = 0;
					}else{
						spaceWidth = spaceWidth * level - spaceWidth;
						if(spaceWidth <= 0){
							spaceWidth = 0;
						}
					}

					if(isSort){
						htmlText.append("<td class='first-end' title='" + cellText + "'>");
						if(cellCmmtNo > 0 && !cell.isDummy()){
							htmlText.append("<a onclick='setCmmtPosition(event)' href=javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
						}else{
							htmlText.append("<span>&nbsp;</span>");
						}

						htmlText.append("<span style='float:left;width:" + spaceWidth + "px;'>");
						htmlText.append("</span>");

						htmlText.append("<span style='width:" + (RD_WIDTH - spaceWidth) + "px;word-wrap:break-word;'>");
						htmlText.append(cellText);
						htmlText.append("</span>");

						chartAddCnt++;
					}else{

						if(cellStyle.indexOf("merge") > -1){
							//merge
							if(i == dataListSize - 1){
								htmlText.append("<td class='merge-end'>");
							}else{
								htmlText.append("<td class='" + cellStyle + "'>");
							}
						}else{
							//first
							if(i == dataListSize - 1){
								htmlText.append("<td class='first-end' title='" + cellText.replaceAll("&nbsp;", "") + "'>");
								chartText = cellText+"/";
							}else{
								htmlText.append("<td class='" + cellStyle + "' title='" + cellText.replaceAll("&nbsp;", "") + "'>");
								chartText+=cellText+"/";
							}
						}

						//style이 merge인 경우 빈 공백 출력
						if(cellStyle.indexOf("merge") > -1){
							htmlText.append("&nbsp;");
							//System.out.println("merge"+cellText);
							chartText += cellText+"/";
							//System.out.println("mergeEnd"+chartText);
						}else{
							if(cellCmmtNo > 0 && !cell.isDummy()){
								htmlText.append("<a onclick='setCmmtPosition(event)' href=javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
							}else{
								htmlText.append("<span>&nbsp;</span>");
							}

							htmlText.append("<span style='float:left;width:" + spaceWidth + "px;'>");
							if(level > 1){
								htmlText.append("&nbsp;");
							}
							htmlText.append("</span>");

							htmlText.append("<span style='width:" + (RD_WIDTH - spaceWidth) + "px;word-wrap:break-word;'>");
							htmlText.append(cellText);
							htmlText.append("</span>");
						}

						if(chartRowArr.size()<20){
							String chartTextSlice = chartText.substring(0,chartText.length()-1);
							if(chartTextSlice != null){
								chartCol.put("rowNm",chartTextSlice);
								chartCreatCnt++; //챠트생성을 위한 rowNm 생성갯수 chartRowArr.size로 챠트생성여부 판단불가
							}
						}

					}

					htmlText.append("</td>");
				}

			}

			if(chartRowArr.size()<20){
				if(chartAddCnt == 0){
					chartCol.put("rowData",tdValueArr);
					chartCol.put("rowUnit",tdUnitArr); //2014.12 차트항목단위
					chartRowArr.add(chartCol);
				}
			}
			htmlText.append("</tr>");

			if(i == (colDimRowCount == 0 ? 1 : colDimRowCount) - 1){
				htmlText.append("<tr>");
				String contextPath = paramInfo.getContextPath();

				if(colDimRowCount == 0){
					colMap = (Map<Integer, Cell>)dataList.get(0);
				}

				iter = colMap.keySet().iterator();

				String ordColIdx = paramInfo.getOrdColIdx();
				String ordType = paramInfo.getOrdType();

				if(ordColIdx == null){
					ordColIdx = "";
				}

				if(ordType == null){
					ordType = "";
				}

				//class
				String sortClass = null;
				String strKey = null;
				String imgSrc = paramInfo.getContextPath() + "/images/";

				while(iter.hasNext()){
					key = iter.next();
					strKey = key + "";

					Cell cell = colMap.get(key);
					int cellType = cell.getType();

					if(cellType == CellType.RH){
						sortClass = "sortRowHead";
					}else{
						sortClass = "sortColHead";
					}

					String asc = StringUtils.defaultString(MessageManager.getInstance().getProperty("ui.label.asc", paramInfo.getDataOpt()), ""); //오름차순 툴팁
					String desc = StringUtils.defaultString(MessageManager.getInstance().getProperty("ui.label.desc", paramInfo.getDataOpt()), ""); //내림차순 툴팁
					String ini = StringUtils.defaultString(MessageManager.getInstance().getProperty("ui.label.ini", paramInfo.getDataOpt()), ""); //초기화 툴팁

					htmlText.append("<th class='" + sortClass + "'>");
					if(strKey.equals(ordColIdx) && ordType.equals("0")){
						htmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_ov.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_off.png' alt='"+desc+"' title='"+desc+"' /></a>");
					}else if(strKey.equals(ordColIdx) && ordType.equals("1")){
						htmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_off.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_ov.png' alt='"+desc+"' title='"+desc+"' /></a>");
					}else{
						htmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_off.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_off.png' alt='"+desc+"' title='"+desc+"' /></a>");
					}

					htmlText.append("&nbsp;<a href='javascript:fn_search()'><img src='" + imgSrc + "ico_hypen.png' alt='"+ini+"' title='"+ini+"' /></a></th>");

				}

				htmlText.append("</tr>");
				htmlText.append("</thead>");
				htmlText.append("<tbody>");
			}
		}
		htmlText.append("</tbody>");
		htmlText.append("</table>");

		//주석만들기
		for(int i = 0; i < cmmtInfoManager.cmmtList.size(); i++){
			CmmtInfo cmmtInfo = (CmmtInfo)cmmtInfoManager.cmmtList.get(i);

			if(cmmtInfo.getCmmtSe().equals("1210610") || cmmtInfo.getCmmtSe().equals("1210614")){
				//통계표, 차원 주석
				cmmtText.append("<dl class='text_con2'>");
				cmmtText.append("\t<dt class='h2_title'>").append(cmmtInfo.getCmmtNo() + ")").append("</dt>");
				cmmtText.append("\t<dd>").append(cmmtInfo.getContent()).append("</dd>");
				cmmtText.append("</dl>");
			}else{
				//항목,분류, 분류값 주석
				cmmtText.append("<p class='h2_title'>").append(cmmtInfo.getCmmtNo() + ") ").append("<span class='name'>")
						.append(cmmtInfo.getTitle()).append("</span></p>");
				cmmtText.append("<p class='text_con'>").append(cmmtInfo.getContent()).append("</p>");
			}

		}
/*
		for(int ii=0; ii< chartLableArr.size(); ii++){
			System.out.println("category label="+chartLableArr.get(ii));
		}
		System.out.println("chartFinalRowArr.size()"+chartRowArr.size());
		for(int kk=0; kk< chartRowArr.size(); kk++){
			System.out.println("category data="+chartRowArr.get(kk));
		}
		for(int j=0; j< chartLableTailArr.size();  j++){
			System.out.println("tail"+chartLableTailArr.get(j));
			chartLabelHead.concat((String) chartLableTailArr.get(j));
			System.out.println("chartLabelHead"+chartLabelHead.concat((String) chartLableTailArr.get(j)));
			chartLableArr.add(chartLabelHead.concat((String) chartLableTailArr.get(j)));
		}
*/
		//챠트 세팅
		if(chartCreatCnt==0){				//rowNm 정보가 0 이면 챠트조합 불가
			chartMsgFlag = "N";
		}
		chartData.put("lable",chartTextMerge);
		chartData.put("data", chartRowArr);
		chartData.put("msg", chartMsgFlag);

	}
}
