package nurimsoft.stat.manager;

import nurimsoft.stat.info.ParamInfo;
import org.springframework.ui.ModelMap;

 @SuppressWarnings("serial")
public class StatExceptionManager extends Exception{
	 
	 private String code;
		
	 public StatExceptionManager(String code){
		 super("");
		 this.code = code;
	 }
	
	 public String getCode(){
		 return code;
	 }
 }

