package nurimsoft.webapp.filedown.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nurimsoft.stat.info.ParamInfo;
import nurimsoft.webapp.StatHtmlService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FileDownloadController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	//Message
	@Resource(name = "messageSource")
	protected DelegatingMessageSource messageSource;
		
	@Resource(name = "statHtmlService")
	protected StatHtmlService statHtmlService;
	
	@RequestMapping(value = "/download")
	public ModelAndView downGrid(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String errMsg = null;
		int errCode = 0;
		
		ModelAndView model = new ModelAndView();
		model.addObject("file", getFile(request));
		
		model.setViewName("downloadView");
		
		return model;
	}
	/*
	@RequestMapping(value = "/downLarge")
	public void downLarge(
			@ModelAttribute("ParamInfo") ParamInfo paramInfo,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception{
		
		String errMsg = null;
		int errCode = 0;
		
		makeLargeFile(paramInfo, request, response);
	}
	
	public void makeLargeFile(ParamInfo paramInfo, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String mime = "application/octet-stream";
	    
		response.setContentType(mime);
		response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode("한글.txt", "UTF-8") + ";");
		response.setHeader("Charset", "UTF-8" );
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "EUC-KR"));
		bw.write("test \n");
		bw.write("테스트");
		bw.write("22222222222");
		bw.close();
	}
	*/
	
	private File getFile(HttpServletRequest request){
		//임시 경로 : 특정 디렉토릴 지정해야 함. properties 사용
		
		//TEST
		try{
			Thread.sleep(1000 * 5);
		}catch(Exception e){}
		
		String fileRootDir = "C:\\work\\statHtml\\tmpFile";
		String fileDir = ""; //excel, csv, txt 등
		String fileName = "filetest.PNG";
		File file = new File(fileRootDir + fileDir + File.separator + fileName);
		
		return file;
	}

}
