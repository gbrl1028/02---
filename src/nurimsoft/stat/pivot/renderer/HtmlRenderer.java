package nurimsoft.stat.pivot.renderer;

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
import nurimsoft.stat.util.StringUtil;

/**
 * @author leekyujeong
 *
 */
public class HtmlRenderer extends Renderer{

	public StringBuffer htmlText = new StringBuffer();
	public StringBuffer ThtmlText = new StringBuffer();
	public StringBuffer cmmtText = new StringBuffer();

	final String rowHeadCell	= " class='rowHead'";
	final String colHeadFirstCell	= " class='colHead-first'";
	final String colHeadMergeCell	= " class='colHead-merge'";
	final String firstCell 		= " class='first'";
	final String mergeCell 		= " class='merge'";
	final String valueCell 		= " class='value'";

	final int CD_WIDTH = 97;
	final int RD_WIDTH = 147;

	public HtmlRenderer(ParamInfo paramInfo, Map<List<String>,  Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager){
		super(paramInfo,resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager);

	}

	//Overriding
	@SuppressWarnings("unchecked")
	public void write(){

		//colResize를 위한 map
		Map<Integer, Cell> reSizeMap = null;
		//reSizeMap = (Map<Integer, Cell>)dataList.get(1);	   // 한 row에 cell 수를 추출
		
		/*2020.07.07 무조건 dataList의 1번을 가져오도록 되어있음
		 * java.lang.IndexOutOfBoundsException: Index: 1, Size: 1 에러가 발생하는 경우가 있어 아래처럼 수정
		*/
		if(dataList.size() > 1){
			reSizeMap = (Map<Integer, Cell>)dataList.get(1);	   // 한 row에 cell 수를 추출
		}else{
			reSizeMap = (Map<Integer, Cell>)dataList.get(0);	   // 한 row에 cell 수를 추출
		}
		
		String tbl_nm_buff = "";
		String summery_buff = "";
		
		if(paramInfo.getDataOpt().indexOf("en") > -1){	// 2017-08-14 웹 접근성을 위해 추가
			tbl_nm_buff = paramInfo.getTblNm();
			summery_buff = "Statistical Contents";
		}else{
			tbl_nm_buff = paramInfo.getTblEngNm();
			summery_buff = "통계 내용";
		}
		
		htmlText.append("<table id='mainTable' summary='"+summery_buff+"'>"); //id필수
		htmlText.append("<caption style='display:none;'>"+tbl_nm_buff+"</caption>"); //접근성을 위해 캡션추가
		htmlText.append("<colgroup>");						   //colResize 기능을위한 colgroup 세팅
		
		ThtmlText.append("<table id='mainTableT' summary='"+summery_buff+"'>"); //id필수
		ThtmlText.append("<caption style='display:none;'>"+tbl_nm_buff+"</caption>"); //접근성을 위해 캡션추가
		ThtmlText.append("<colgroup>");

		 for(int i=0; i<reSizeMap.size();i++){
			 if(valColBeginIdx>i){
				 htmlText.append("<col class='Selectable1'/>");	//세로 1번째컬럼 넓이
				 ThtmlText.append("<col class='Selectable1'/>");

			 }else{
				 htmlText.append("<col class='Selectable2'/>");  //세로 2번째컬럼 넓이
				 ThtmlText.append("<col class='Selectable2'/>");
			 }

//			 htmlText.append("<col class='Selectable'/>");	//세로 1번째컬럼 넓이
		 }
		htmlText.append("</colgroup>");
		htmlText.append("<thead>");
		ThtmlText.append("</colgroup>");
		ThtmlText.append("<thead>");

		Map<Integer, Cell> colMap = null;
		//System.out.println("dataList.size()"+dataList.size());
		int dataListSize = dataList.size();
		//System.out.println("colDimRowCount ::: " + colDimRowCount);
		//System.out.println("rowDimColCount ::: " + rowDimColCount);

		Cell cell = null;
		int cellType = 0;
		String cellValue = "";
		String cellStyle = "";
		String cellText = "";

		int cellCmmtNo = 0;
		int level = 0;
		String cellText_buff = "";

		String ordColIdx = "";
		String ordType = "";

		String sortClass = null;
		String strKey = null;
		String imgSrc = paramInfo.getContextPath() + "/images/";

		String asc = ""; //오름차순 툴팁
		String desc = ""; //내림차순 툴팁
		String ini = ""; //초기화 툴팁
		
		for(int i = 0; i < dataListSize; i++){

			htmlText.append("<tr>");

			colMap = (Map<Integer, Cell>)dataList.get(i);
			Iterator<Integer> iter = colMap.keySet().iterator();
			Integer key = null;

			while(iter.hasNext()){
				key = iter.next();
				cell = colMap.get(key);
				cellType = cell.getType();
				cellValue = cell.getValue();
				cellStyle = cell.getStyle();
				cellText = cell.getText();

				cellCmmtNo = cell.getCmmtNo();
				level = cell.getLevel();
				
				//System.out.println("iter.hasNext////cellText====>"+cellText);
				if(cellType == CellType.RH){
					//System.out.println("cellType.RH////cellText====>"+cellText);
					if(i == 0){
						if(colDimRowCount == 0){
							htmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' title='" + cellText + "'>");
							ThtmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' title='" + cellText + "'>");
							//System.out.println("colDimRowCount==0"+colDimRowCount);
						}else{
							htmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' rowspan='" + colDimRowCount + "' title='" + cellText + "'>");
							ThtmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' rowspan='" + colDimRowCount + "' title='" + cellText + "'>");
							//System.out.println("colDimRowCount===>else"+colDimRowCount);
						}
						//System.out.println("i갯수==>"+i);
						if(cellCmmtNo > 0){
							htmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a>");
							ThtmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a>");
						}else{
							htmlText.append("<span>&nbsp;</span>");
							ThtmlText.append("<span>&nbsp;</span>");
						}
						htmlText.append("<span class='text'>");
						htmlText.append(cellText);
						htmlText.append("</span>");
						htmlText.append("</th>");
						
						ThtmlText.append("<span class='text'>");
						ThtmlText.append(cellText);
						ThtmlText.append("</span>");
						ThtmlText.append("</th>");
					}
				}else if(cellType == CellType.CD){
					if(cell.isFirst()){
						//첫번째 ColSpan 추출
						htmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' colspan='" + cell.getColspan() + "' title='" + cellText.replaceAll("&nbsp;", "") + "'>");
						ThtmlText.append("<th class='" + cellStyle + "' name='" + cellValue + "' colspan='" + cell.getColspan() + "' title='" + cellText.replaceAll("&nbsp;", "") + "'>");

						if(cellCmmtNo > 0 && !cell.isDummy()){
							htmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
							ThtmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
						}else{
							htmlText.append("<span>&nbsp;</span>");
							ThtmlText.append("<span>&nbsp;</span>");
						}
						htmlText.append("<span style='width:" + (cell.getColspan() * CD_WIDTH) + "px;word-wrap:break-word;'>");
						htmlText.append( cellText );
						htmlText.append("</span>");
						htmlText.append("</th>");
						
						ThtmlText.append("<span style='width:" + (cell.getColspan() * CD_WIDTH) + "px;word-wrap:break-word;'>");
						ThtmlText.append( cellText );
						ThtmlText.append("</span>");
						ThtmlText.append("</th>");
					}

				}else if(cellType == CellType.VAL){

					// 2019.03.05 통계부호 관련 제어
					cellText_buff = cellText.replace("[{", "").replace("}]", "");
					htmlText.append("<td class='" + cellStyle + "' title='" + cellText_buff + "'>");

					if(cell.getCmmtNo() > 0){
						htmlText.append("<a name='popupCmmt" + cellCmmtNo + "' onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
					}else{
						htmlText.append("<span>&nbsp;</span>");
					}

					/*
					2015.11.09 수치의 부호중 *,**,e,p,x,▽ 있을경우 윗첨자로 나오도록
					2018.11.06 x는 윗첨자로 더이상 필요없으므로 제거
					2019.03.05 셀단위가 들어갈경우 통계부호 위치를 알 수 없음으로 기존 substring 방식으로는 윗첨자를 제어할 수 없음
							   따라서 Renderer에서 cellText를 만들때 부터 통계부호를 알 수 있도록 [{ }] 안에 넣어서 추후에 replace시킬수 있도록 한다. 
					2022.07.20 통계표 좌측정렬 > 우측정렬 되도록
					*/
					if(cellText.indexOf("[{**}]") > -1 ||cellText.indexOf("[{*}]") > -1 ||cellText.indexOf("[{e}]") > -1||cellText.indexOf("[{p}]") > -1||cellText.indexOf("[{▽}]") > -1){
						
						String buff[] = {"[{**}]","[{*}]","[{e}]","[{p}]","[{▽}]"};
						String buff_2[] = {"**","*","e","p","▽"};
						
						htmlText.append("<span class='val'>");
						
						for(int z = 0; z < buff.length ; z++){
							if(cellText.indexOf(buff[z]) > -1){
								htmlText.append( cellText.replace(buff[z], "&nbsp;<sup style='font-size:12px;'>"+buff_2[z]+"</sup>") );
								break;
							}
						}
					}else{
						boolean hanChk = StringUtil.checkHan(cellText);

						if(hanChk){ 
							htmlText.append("<span class='val' style='text-align:right;'>");
						}else{
							// 전체 좌측정렬로 변경
							htmlText.append("<span class='val' style='text-align:right;'>");
						}
						htmlText.append( cellText );
					}

					//htmlText.append( cellText );
					//htmlText.append( cellText + "@" + cell.getChartUnit() ); //chart에 제공할 데이터
					htmlText.append("</span>");
					htmlText.append("<input type='hidden' value='"+cellValue+"'/>");//데이터찾기
					htmlText.append("</td>");

				}else{
					// 2015년 1월 09일 spaceWidth = 5 -> 10 으로 변경
					int spaceWidth = 10;
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
							htmlText.append("<a onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
						}else{
							htmlText.append("<span>&nbsp;</span>");
						}

						htmlText.append("<span style='float:left;width:" + spaceWidth + "px;'>");
						htmlText.append("</span>");

						htmlText.append("<span style='width:" + (RD_WIDTH - spaceWidth) + "px;word-wrap:break-word;'>");
						htmlText.append(cellText);
						htmlText.append("</span>");

					}else{

						if(cellStyle.indexOf("merge") > -1){
							//merge
							if(i == dataListSize - 1){
								htmlText.append("<td class='merge-end' title='" + cellText + "'>");
							}else{
								htmlText.append("<td class='" + cellStyle + "' title='" + cellText + "'>");
							}
						}else{
							//first
							if(i == dataListSize - 1){
								htmlText.append("<td class='first-end' title='" + cellText.replaceAll("&nbsp;", "") + "'>");
							}else{
								htmlText.append("<td class='" + cellStyle + "' title='" + cellText.replaceAll("&nbsp;", "") + "'>");
							}
						}

						//style이 merge인 경우 빈 공백 출력
						if(cellStyle.indexOf("merge") > -1){
							htmlText.append("&nbsp;");
						}else{
							if(cellCmmtNo > 0 && !cell.isDummy()){
								htmlText.append("<a onclick='setCmmtPosition(event)' href=\"javascript:popupCmmt('" + cellCmmtNo + "#" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')\"><span class='h2_title'>" + cellCmmtNo + ")</span></a>");
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

					}

					htmlText.append("</td>");
				}

			}

			htmlText.append("</tr>");
			ThtmlText.append("</tr>");

			if(i == (colDimRowCount == 0 ? 1 : colDimRowCount) - 1){
				htmlText.append("<tr>");
				ThtmlText.append("<tr>");

				if(colDimRowCount == 0){
					colMap = (Map<Integer, Cell>)dataList.get(0);
				}

				iter = colMap.keySet().iterator();

				ordColIdx = paramInfo.getOrdColIdx();
				ordType = paramInfo.getOrdType();

				if(ordColIdx == null){
					ordColIdx = "";
				}

				if(ordType == null){
					ordType = "";
				}

				//class
				sortClass = null;
				strKey = null;
				imgSrc = paramInfo.getContextPath() + "/images/";

				while(iter.hasNext()){
					key = iter.next();
					strKey = key + "";
					
					cell = null;
					cell = colMap.get(key);
					cellType = cell.getType();

					if(cellType == CellType.RH){
						sortClass = "sortRowHead";
					}else{
						sortClass = "sortColHead";
					}

					asc = StringUtils.defaultString(MessageManager.getInstance().getProperty("ui.label.asc", paramInfo.getDataOpt()), ""); //오름차순 툴팁
					desc = StringUtils.defaultString(MessageManager.getInstance().getProperty("ui.label.desc", paramInfo.getDataOpt()), ""); //내림차순 툴팁
					ini = StringUtils.defaultString(MessageManager.getInstance().getProperty("ui.label.ini", paramInfo.getDataOpt()), ""); //초기화 툴팁
					
					htmlText.append("<th class='" + sortClass + "'>");
					ThtmlText.append("<th class='" + sortClass + "'>");
					if(strKey.equals(ordColIdx) && ordType.equals("0")){
						htmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_ov.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_off.png' alt='"+desc+"' title='"+desc+"' /></a>");
						ThtmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_ov.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_off.png' alt='"+desc+"' title='"+desc+"' /></a>");
					}else if(strKey.equals(ordColIdx) && ordType.equals("1")){
						htmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_off.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_ov.png' alt='"+desc+"' title='"+desc+"' /></a>");
						ThtmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_off.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_ov.png' alt='"+desc+"' title='"+desc+"' /></a>");
					}else{
						htmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_off.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_off.png' alt='"+desc+"' title='"+desc+"' /></a>");
						ThtmlText.append("<a href='javascript:fn_sortSearch(" + key + ", 0)'><img src='" + imgSrc + "ico_up_off.png' alt='"+asc+"' title='"+asc+"' /></a>&nbsp;<a href='javascript:fn_sortSearch(" + key + ", 1)'><img src='" + imgSrc + "ico_down_off.png' alt='"+desc+"' title='"+desc+"' /></a>");
					}

					htmlText.append("&nbsp;<a href='javascript:fn_search()'><img src='" + imgSrc + "ico_hypen.png' alt='"+ini+"' title='"+ini+"' /></a></th>");
					ThtmlText.append("&nbsp;<a href='javascript:fn_search()'><img src='" + imgSrc + "ico_hypen.png' alt='"+ini+"' title='"+ini+"' /></a></th>");

				}

				htmlText.append("</tr>");
				htmlText.append("</thead>");
				htmlText.append("<tbody>");
				
				ThtmlText.append("</tr>");
				ThtmlText.append("</thead>");
				ThtmlText.append("<tbody>");
			}
		}
		htmlText.append("</tbody>");
		htmlText.append("</table>");
		
		ThtmlText.append("</tbody>");
		ThtmlText.append("</table>");

		//주석만들기
		CmmtInfo cmmtInfo = null;
		for(int i = 0; i < cmmtInfoManager.cmmtList.size(); i++){
			cmmtInfo = null;
			cmmtInfo = (CmmtInfo)cmmtInfoManager.cmmtList.get(i);

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

	}
}
