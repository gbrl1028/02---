package nurimsoft.stat.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
	
	private static PropertyManager instance = new PropertyManager();
	private Properties prop = new Properties();
	
	private PropertyManager(){
		load();
	}
	
	public static PropertyManager getInstance(){
		if(instance == null){
			synchronized(PropertyManager.class){
				if(instance == null){
					instance = new PropertyManager();
				}
			}
		}
		return instance;
	}
	
	public void load(){
		InputStream is = null;
		try{
			is = getClass().getClassLoader().getResourceAsStream("config.properties");
			prop.load(is);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(is != null){
				try{
					is.close();
				}catch(Exception e){}
			}
		}
	}
	
	public String getProperty(String key){
		return prop.getProperty(key);
	}
	
}