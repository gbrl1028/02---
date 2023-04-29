package nurimsoft.stat.util;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class MessageTag extends SimpleTagSupport{
	
	private String code;

	public void doTag() throws JspException, IOException{
		
		JspWriter out = getJspContext().getOut();
		//System.out.println("######################################################" + ((PageContext)getJspContext()).getSession().getAttribute("dataOpt"));
		//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + getJspContext().getAttribute("dataOpt", PageContext.SESSION_SCOPE));
		out.print(MessageManager.getInstance().getProperty(code, (String)getJspContext().getAttribute("dataOpt", PageContext.SESSION_SCOPE)));
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
}
