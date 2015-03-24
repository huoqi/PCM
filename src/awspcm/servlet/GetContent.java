package awspcm.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

/**
 * Servlet implementation class GetContent
 */
public class GetContent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetContent() {
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
		
		String path = this.getServletContext().getRealPath("/");
		File contentFile = new File(path + "/content.json");
		String content = "";
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(contentFile));
		String line = null;
		while((line = br.readLine()) != null){
			content += line.trim();
		}
		JSONObject json = null;
		try {
			json = new JSONObject(content);
			out.print(json);
			out.flush();
			out.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(br != null) br.close();
		}
	}

}
