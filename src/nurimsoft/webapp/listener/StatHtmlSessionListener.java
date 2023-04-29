package nurimsoft.webapp.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import nurimsoft.webapp.StatHtmlDAO;
import nurimsoft.webapp.StatHtmlService;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.FrameworkServlet;

public class StatHtmlSessionListener implements HttpSessionListener{
	
	public void sessionCreated(HttpSessionEvent arg0){
		/*
		HttpSession session = arg0.getSession();
		
		System.out.println("###########################################################################################################");
		System.out.println(session.getId() + " ::: Bound");
		System.out.println("###########################################################################################################");
		*/
	}
	
	public void sessionDestroyed(HttpSessionEvent arg0){
		HttpSession session = arg0.getSession();
		
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext(), FrameworkServlet.SERVLET_CONTEXT_PREFIX + "action");
		StatHtmlDAO statHtmlDAO = (StatHtmlDAO)ctx.getBean("statHtmlDAO");
		
		try{
			statHtmlDAO.deleteSearchCondition(session.getId());
		}catch(Exception e){
			e.printStackTrace();
		}
		/*
		System.out.println("###########################################################################################################");
		System.out.println(session.getId() + " ::: Unbound");
		System.out.println("###########################################################################################################");
		*/
	}
	
}
