package awspcm.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import awspcm.model.User;

/**
 * Servlet implementation class WelcomUser
 */
public class WelcomeUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WelcomeUser() {
        super();
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
		response.setContentType("application/x-json");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		User user = (User) request.getSession().getAttribute("user");
		if(user != null) {
			JSONObject json = null;
			try {
				 json = new JSONObject("{\'username\': \'" + user.getName() + "\',\'level\': \'" + user.getLevel() + "\'}");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			out.print(json);
			out.flush();
			out.close();
		}
	}

}
