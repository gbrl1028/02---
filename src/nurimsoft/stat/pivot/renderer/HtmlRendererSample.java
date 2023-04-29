package nurimsoft.stat.pivot.renderer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.CmmtInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.CellType;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.webapp.StatHtmlDAO;

/**
 * @author leekyujeong
 *
 */
public class HtmlRendererSample extends Renderer{

	public StringBuffer htmlText = new StringBuffer();
	public StringBuffer cmmtText = new StringBuffer();

	public HtmlRendererSample(ParamInfo paramInfo, Map<List<String>,  Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager){
		super(paramInfo,resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager);

	}

	//Overriding
	public void write(){
		//System.out.println(dataList.size());

		htmlText.append("<table border='1'>");

		System.out.println(colDimRowCount);

		Map<Integer, Cell> colMap = null;
		/*
		for(int i = 0; i < dataList.size(); i++){
			htmlText.append("<tr>");

			colMap = (Map<Integer, Cell>)dataList.get(i);
			Iterator<Integer> iter = colMap.keySet().iterator();
			Integer key = null;
			while(iter.hasNext()){
				key = iter.next();

				Cell cell = colMap.get(key);
				if(cell.getType() == CellType.CD){
					System.out.println(cell.getText() + "," + "," + cell.getStyle() + "," + cell.isFirst() + "," + cell.getColspan());
				}

				htmlText.append("<td class='" + cell.getStyle() + "' name='" + cell.getValue() + "'>");
				//htmlText.append(cell.getText());
				htmlText.append(cell.getValue() + " : " + cell.getText());
				htmlText.append("</td>");
			}

			htmlText.append("</tr>");
		}
		*/

		for(int i = 0; i < dataList.size(); i++){
			htmlText.append("<tr>");

			//System.out.println(i + " ::: --------------------------------------------------------");

			colMap = (Map<Integer, Cell>)dataList.get(i);
			Iterator<Integer> iter = colMap.keySet().iterator();
			Integer key = null;
			while(iter.hasNext()){
				key = iter.next();

				Cell cell = colMap.get(key);
				System.out.println(cell.getCmmtNo());

				//System.out.println("@@@ ::: " + cell.getText() + "," + cell.isFirst() + "," + cell.getColspan());

				if(cell.getType() == CellType.RH){
					if(i == 0){
						htmlText.append("<td class='" + cell.getStyle() + "' name='" + cell.getValue() + "' rowspan='" + colDimRowCount + "'>");
						//주석 존재 유무에 따라 달리 표현
						if(cell.getCmmtNo() > 0){
							htmlText.append("<sup><a href=javascript:openComment('" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "') name='popupCmmt" + cell.getCmmtNo() + "'><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a></sup> ");
						}
						htmlText.append( cell.getText() );
						htmlText.append("</td>");
					}
				}else if(cell.getType() == CellType.CD){
					if(cell.isFirst()){
						htmlText.append("<td class='" + cell.getStyle() + "' name='" + cell.getValue() + "' colspan='" + cell.getColspan() + "'>");
						if(cell.getCmmtNo() > 0){
							htmlText.append("<sup><a href=javascript:openComment('" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "') name='popupCmmt" + cell.getCmmtNo() + "'><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a></sup> ");
						}
						htmlText.append( cell.getText() );
						htmlText.append("</td>");
					}
				}else if(cell.getType() == CellType.VAL){
					htmlText.append("<td class='" + cell.getStyle() + "' name='" + cell.getValue() + "'>");
					if(cell.getCmmtNo() > 0){
						htmlText.append("<sup><a href=javascript:openComment('" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "') name='popupCmmt" + cell.getCmmtNo() + "'><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a></sup> ");
					}
					htmlText.append( cell.getText() );
					htmlText.append("</td>");
				}else{

					htmlText.append("<td class='" + cell.getStyle() + "' name='" + cell.getText() + "'");

					if(cell.getCmmtNo() > 0){
						htmlText.append(" value=\"<sup><a href=javascript:openComment('" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "')><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a></sup> \">");
					}else{
						htmlText.append(">");
					}

					//style이 merge인 경우 빈 공백 출력
					if(cell.getStyle().indexOf("merge") > -1){
						htmlText.append("&nbsp;");
					}else{

						if( cell.getText() == null || cell.getText().trim().length() == 0 ){
							htmlText.append("&nbsp;");
						}else{
							if(cell.getCmmtNo() > 0){
								htmlText.append("<sup><a href=javascript:openComment('" + cell.getCmmtSe() + "#" + cell.getObjVarId() + "#" + cell.getItmId() + "#" + cell.getItmRcgnSn() + "') name='popupCmmt" + cell.getCmmtNo() + "'><span class='h2_title'>" + cell.getCmmtNo() + ")</span></a></sup> ");
							}
							htmlText.append( cell.getText() );
						}
					}
					htmlText.append("</td>");
				}
			}

			htmlText.append("</tr>");
		}

		htmlText.append("</table>");

		//주석만들기
		for(int i = 0; i < cmmtInfoManager.cmmtList.size(); i++){
			CmmtInfo cmmtInfo = (CmmtInfo)cmmtInfoManager.cmmtList.get(i);
			System.out.println(cmmtInfo.getCmmtNo() + " ::: " + cmmtInfo.getTitle() + "," + cmmtInfo.getContent());

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
