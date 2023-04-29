package nurimsoft.stat.pivot.renderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;
import nurimsoft.webapp.StatHtmlDAO;

public class CsvTxtRenderer extends Renderer{

	public StringBuffer fileText = new StringBuffer();
	public File file;

	private String analTitle;
	private final String analTitle_KO = "분석";
	private final String analTitle_EN = "Analysis";

	private int openCnt;
	private StringUtil sutil;
	private HttpServletRequest request;
	
	public CsvTxtRenderer(ParamInfo paramInfo, Map<List<String>,  Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager, int openListCnt, HttpServletRequest request){
		super(paramInfo,resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager);
		this.request = request;
		
		if(paramInfo.getDataOpt().indexOf("en") > -1){
			analTitle = analTitle_EN;
		}else{
			analTitle = analTitle_KO;
		}

		openCnt = openListCnt;
	}

	//Overriding
	@SuppressWarnings("unchecked")
	public void write(){

		String smblYn	= paramInfo.getSmblYn(); //통계부호 받는지 여부
		//String realPath = paramInfo.getRealPath(); // 2020.08.13 was 정보 노출!! 보안취약점 제거
		String realPath = request.getSession().getServletContext().getRealPath("");
		String fileDir	= "tmpFile";

		/*2014-11-19 통계표명 유지보수 1960 통계표 다운로드시 파일명 변경 - 이원영*/
		//String fileName = paramInfo.getOrgId() + "_" + paramInfo.getTblId();
		String fileName = "";

		if(paramInfo.getDataOpt().indexOf("en") > -1){
			fileName = sutil.removeSpecialCharforFile(paramInfo.getTblEngNm());
		}else{
			fileName = sutil.removeSpecialCharforFile(paramInfo.getTblNm());
		}

		String view = paramInfo.getView();

		String dateString = StatPivotUtil.getDateString();

		File dir = new File(realPath + File.separator + fileDir);
		if(! dir.exists()){
			dir.mkdir();
		}

		if(paramInfo.getAnalTextTblNm() != null){
			fileName += "_" + dateString + "_" + analTitle + "(" + paramInfo.getAnalTextTblNm().replaceAll("<", "").replaceAll(">", "").replaceAll(" ", "_") + ")." + paramInfo.getView();
		}else{
			fileName += "_" + dateString + "." + paramInfo.getView();
		}

		file = new File(realPath + File.separator + fileDir, fileName);

		int dataListSize = dataList.size();
		Map<Integer, Cell> colMap = null;

		Iterator<Integer> iter = null;
		Integer key = null;
		
		Cell cell = null;
		int index = 0;
		
		for(int i = 0; i < dataListSize; i++){

			colMap = (Map<Integer, Cell>)dataList.get(i);
			iter = colMap.keySet().iterator();
			key = null;

			index = 0;
			while(iter.hasNext()){

				key = iter.next();
				cell = colMap.get(key);

				//String cellText = addQuotation(cell.getText().replaceAll("&nbsp;", ""));

				//2015.06.02 수치의 " " 와 천단위 , 제거 요청(이원영주무관)
				//2015.08.17 txt는 그대로 csv만 변경 되도록 수정 요청(이원영주무관) 6.2, 8.17 nportal에만 반영예정

				String cellText = "";

				// 표측 수
				rowDimCount = rowAxis.getDimemsionCount();

				if( index < rowDimCount ){
					cellText = addQuotation(cell.getText());
				}else{
					cellText = cell.getText().replaceAll(",", "");
				}

				if(index > 0){
					if(view.equals("csv")){
						fileText.append(",");
					}else{
						fileText.append("\t");
					}
				}
				// 2019.03.12 통계부호 출력여부 추가 (2019.03.12 이전에 없었음) 
				if(smblYn.equals("Y")){
					cellText = cellText.replace("[{", "").replace("}]", "");	// 통계부호를 표시하는 가로 제거
				}else{
					String buff[] = {"[{**}]","[{*}]","[{e}]","[{p}]","[{▽}]"};
					
					for(int z = 0; z < buff.length ; z++){
						if(cellText.indexOf(buff[z]) > -1){
							cellText = cellText.replace(buff[z], "");
							break;
						}
					}
				}
				
				fileText.append(cellText);

				index++;
			}
			fileText.append("\n");

		}

		BufferedWriter bw = null;

		try{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(fileText.toString());
		}catch(Exception e){

		}finally{
			try{
				bw.close();
			}catch(Exception e){}
		}
	}

	private String addQuotation(String text){
		return "\"" + text + "\"";
	}

}
