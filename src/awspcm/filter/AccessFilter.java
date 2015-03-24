package awspcm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccessFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		String url = ((HttpServletRequest)request).getRequestURL().toString();
		String page = url.substring(url.lastIndexOf("/") + 1);
//		System.out.println("url: " + url);
		HttpSession session = ((HttpServletRequest)request).getSession();
		if( url.contains("/css/") ||
				url.contains("/js/") ||
				url.contains("/images/") ||
				session.getAttribute("user") != null ||
				page.equals("login.html")  ||
				page.equals("Login") ||
				page.equals("")		//home page
				) {
			chain.doFilter(request, response);
		} else {
			((HttpServletResponse) response).sendRedirect("login.html?message=Please Login!");
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
