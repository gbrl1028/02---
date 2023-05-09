package nurimsoft.stat.pivot.renderer;

import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.stat.pivot.Cell;
import nurimsoft.stat.pivot.ColumnAxis;
import nurimsoft.stat.pivot.Measure;
import nurimsoft.stat.pivot.RowAxis;
import nurimsoft.stat.util.StatPivotUtil;
import nurimsoft.stat.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PairRenderer extends Renderer {
    public File file;

    private String analTitle;
    private final String analTitle_KO = "분석";
    private final String analTitle_EN = "Analysis";

    private int openCnt;
    private HttpServletRequest request;
    private StringUtil sutil;

    public PairRenderer(ParamInfo paramInfo, Map<List<String>, Measure> resultMap, RowAxis rowAxis, ColumnAxis columnAxis, boolean levelExpr, CmmtInfoManager cmmtInfoManager, int openCnt, HttpServletRequest request) {
        super(paramInfo, resultMap, rowAxis, columnAxis, levelExpr, cmmtInfoManager);
        this.openCnt = openCnt;
        this.request = request;

        if(paramInfo.getDataOpt().indexOf("en") > -1){
            analTitle = analTitle_EN;
        }else{
            analTitle = analTitle_KO;
        }
    }

    public void write() {

        String smblYn	= paramInfo.getSmblYn(); //통계부호 받는지 여부
        String realPath = request.getSession().getServletContext().getRealPath("");
        String fileDir	= "tmpFile";
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

        StringBuffer fileText = new StringBuffer();
        Cell cell = null;

        List<Map<String, String>> exportList = new ArrayList<>();
        for (int i = 0; i < dataListSize; i++) {
            colMap = (Map<Integer, Cell>)dataList.get(i);
            iter = colMap.keySet().iterator();
            key = null;

            while (iter.hasNext()) {
                key = iter.next();
                cell = colMap.get(key);
                if(cell.getExportMap().size() == 0) continue;
                exportList.add(cell.getExportMap());
            }
        }

        JSONArray jsonArray = JSONArray.fromObject(exportList);
        if (paramInfo.getView().equals("xml")) {
            XMLSerializer xmlSerializer = new XMLSerializer();
            xmlSerializer.setArrayName("elements");
            xmlSerializer.setElementName("element");
            xmlSerializer.setTypeHintsEnabled(false);
            fileText.append(xmlSerializer.write(jsonArray, "UTF-8"));
        } else {
            fileText.append(jsonArray.toString());
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
}
