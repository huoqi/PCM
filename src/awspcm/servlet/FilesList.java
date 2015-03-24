package awspcm.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONArray;

import awspcm.dynamodb.FileDao;

public class FilesList extends HttpServlet {
	private static final long serialVersionUID = -4064667607039268088L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/x-json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter out =resp.getWriter();
		
		String content = (String) req.getParameter("content");
		JSONArray files = new JSONArray();
		if(content != null && !content.isEmpty())
			files = FileDao.getItemsByContent(content);
		
		out.print(files);
		out.flush();
		out.close();
	}

}
