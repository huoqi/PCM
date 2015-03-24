package awspcm.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import awspcm.dynamodb.FileDao;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

/**
 * Servlet implementation class FileDelete
 */
public class FileDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public FileDelete() {
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
		
		String deleteObjectKey = request.getParameter("objectKey");
		String content = request.getParameter("content");
		String fileName = request.getParameter("fileName");
		if(deleteObjectKey != null && content != null) {
			JSONObject msg = null;
			try {
				FileDao.deleteObject(deleteObjectKey);   //delete from s3
				System.out.println("Delete file from s3, done! " + fileName);
				FileDao.deleteFromDynamoDB(deleteObjectKey, content);   //delete from dynamodb
				System.out.println("Delete file from dynamodb, done! "  + fileName);
				msg = new JSONObject("{'message' : 'File \"" + fileName + "\" has been deleted!'}");
				
				out.print(msg);
				out.flush();
				out.close();
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}

	}

}
