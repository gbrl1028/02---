package nurimsoft.stat.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParameterMap {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private Map paramMap = new HashMap();
	private String NULL_STRING = null;
	
	// 매개변수 없이는 인스턴스화  할 수 없다.
	private ParameterMap() {}

	/**
	 * 생성자 
	 * @param request HttpServletRequest를 파라미터로 받는다.
	 */
	public ParameterMap(HttpServletRequest request) {
		init(request);
	}
	
	
	/**
	 * 생성자 
	 * @param request HttpServletRequest를 파라미터로 받는다.
	 * @param nullStr Null 일경우 리턴되는 값을 셋한다.
	 */
	public ParameterMap(HttpServletRequest request, String nullStr) {
		init(request);
		setNullString(nullStr);
	}
	
	public void init(HttpServletRequest request) {
		
		if (request != null) {
			String key = null;
			String[] values = null;
			for (Enumeration e = request.getParameterNames();e.hasMoreElements();) {
				key = (String) e.nextElement();
				values = request.getParameterValues(key);
				setValue(key, values);
			}
		}// close of if
		
	}
	
	/**
	 * get(key)호출할때 NULL일경우 리턴되는 값을 셋한다. 디폴트는 null 이다.
	 * @param value
	 */
	public void setNullString(String value) {
		NULL_STRING = value;
	}

	/**
	 * 파라미터 value를 리턴한다. 
	 * @param key 파라미터명
	 * @return 해당 Key value가 없을시 null이 리턴된다. 
	 */
	public String getValue(String key) {
		String value = null;
		
		try{ 
			value = (getValues(key))[0];
		} catch (NullPointerException e) {
			value = NULL_STRING;
		} catch (ClassCastException e) {
			value = (String) paramMap.get(key);
			value = (value == null ? NULL_STRING : value);
		}
		return value; 
	}
	
	/**
	 * 파라미터 value를 리턴한다. 
	 * @param key 파라미터명
	 * @param defaultValue null일경우 리턴되는 기본값
	 * @return  
	 */
	public String getValue(String key, String defaultValue) {
		String value = null;
		
		try{ 
			value = (getValues(key))[0];
		} catch (NullPointerException e) {
			value = defaultValue;
		} catch (ClassCastException e) {
			value = (String) paramMap.get(key);
			value = (value == null ? defaultValue : value);
		}
		return value; 
	}
	
	/**
	 * 파라미터를 셋한다.
	 * @param key
	 * @param value
	 */
	public void setValue(String key, Object value) {
		paramMap.put(key, value);
	}
	
	/**
	 * 파라미터 values (String[])를 리턴한다. 
	 * @param key 파라미터명
	 * @return 해당 key value가 없을시 null이 리턴된다. 
	 */
	public String[] getValues(String key) throws ClassCastException {
		return (String[]) paramMap.get(key);
	}
	
	/**
	 * 파라미터를 넣는다.
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		paramMap.put(key, value);
	}
	
	
	/**
	 * 파라미터의 이름을 리턴한다. 
	 * @return
	 */
	public Set getNames() {
		Set keyset = paramMap.keySet();
		return keyset;
	}
	
	
	/**
	 * 전체 키 / 값 쌍을 스트링으로 리턴한다.
	 * @return
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("\nParameterMap Key / Value output \n");
		
		for (Iterator iter = getNames().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			buff.append(key).append(" / ").append(getValue(key)).append("\n");
		}
		
		return buff.toString();
		
	}
}