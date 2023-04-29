package nurimsoft.stat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MessageManager {
	
	private static MessageManager instance = new MessageManager();
	private Properties propKo = new Properties();
	private Properties propEn = new Properties();
	
	private final String rootDir = "/WEB-INF/message/";
	
	private MessageManager(){}
	
	public static MessageManager getInstance(){
		if(instance == null){
			synchronized(MessageManager.class){
				if(instance == null){
					instance = new MessageManager();
				}
			}
		}
		return instance;
	}
	
	public void load(String realPath){
		InputStream fis = null;
		try{
			fis = new FileInputStream(realPath + File.separator + rootDir + File.separator + "msg_ko.properties");
			propKo.load(fis);
			fis.close();
			
			fis = new FileInputStream(realPath + File.separator + rootDir + File.separator + "msg_en.properties");
			propEn.load(fis);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fis != null){
				try{
					fis.close();
				}catch(Exception e){}
			}
		}
	}
	
	public String getProperty(String key, String lang){
		return ( lang == null || lang.indexOf("en") < 0 ) ? propKo.getProperty(key) : propEn.getProperty(key);
	}
	
}