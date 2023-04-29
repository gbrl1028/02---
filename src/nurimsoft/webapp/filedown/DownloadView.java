package nurimsoft.webapp.filedown;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

public class DownloadView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {

        File file = (File) map.get("file");
        String fileName = (file != null) ? file.getName() : "";
        response.setContentType(this.getContentType());
        response.setContentLength((int) file.length());
        if (request.getParameter("direct") != null && request.getParameter("direct").equals("direct")) {
            String downFileName = null;
            int lastIndexUnder = fileName.lastIndexOf("_");
            int lastIndexDot = fileName.lastIndexOf(".");
            downFileName = fileName.substring(0, lastIndexUnder) + fileName.substring(lastIndexDot);
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(downFileName, "UTF-8") + ";");
        } else {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            //fileName = fileName.replaceAll("\\+", "%20"); // URLEncoder 를 할 경우 빈공백은 +로 변경된다 그걸 다시 공백으로 돌려놓는거...혹시모를 나중을 위해 기록해둠 - 2016-01-26 김경호
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ";");
        }
        response.setHeader("Content-Transfer", "binary");

        //ajax
        //response.setHeader("Set-Cookie", "fileDownload=true;path=/");

        OutputStream out = response.getOutputStream();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileCopyUtils.copy(fis, out);
        } catch (Exception e) {
            throw e;
        } finally {
            if (!fileName.equals("manual.pdf")) {
                file.delete();
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }

        out.flush();

    }


}
