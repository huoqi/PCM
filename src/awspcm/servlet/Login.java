package awspcm.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import awspcm.dynamodb.UserDao;
import awspcm.model.User;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("user");
		String password = request.getParameter("password");
		if(userName == null || password == null) {  //direct access "Login" both username and password are null
			response.sendRedirect("login.html");
		}
		else {
			User user = new User(userName, password);
			int level = UserDao.login(user);
			if(level == -2) response.sendRedirect("login.html?message=Invalided Username!");
			else if(level == -1) response.sendRedirect("login.html?message=Incorrected Password!");
			else {
				user.setLevel(level);
				request.getSession().setAttribute("user", user);
				response.sendRedirect("PCM_Disk.html");
			}
		}
	}

}
