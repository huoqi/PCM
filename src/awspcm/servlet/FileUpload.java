package awspcm.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.util.json.JSONObject;

import awspcm.dynamodb.FileDao;

public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 7572577367674110305L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html"); 
        response.setCharacterEncoding("UTF-8");  // 设置字符编码为UTF-8, 这样支持汉字显示
        PrintWriter out = response.getWriter();

        String content = request.getParameter("content");

//        System.out.println("SubContent: " + content);  //test
        if(content != null) {
	        String uploadPath = this.getServletContext().getRealPath("/upload/");
	        
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        factory.setSizeThreshold(4096);
	        ServletFileUpload upload = new ServletFileUpload(factory);     // Create a new file upload handler
	        if(ServletFileUpload.isMultipartContent(request)){
	        	List<FileItem> fileItems = null;
	        	int fileNum = 0;
				try {
					fileItems = upload.parseRequest(request);
					Iterator<FileItem> fileItr = fileItems.iterator();
					while (fileItr.hasNext()) {
			            // 得到当前文件
			             FileItem fileItem = (FileItem) fileItr.next();
			            // 忽略简单form字段而不是上传域的文件域(<input type="text" />等)
			            if (fileItem == null || fileItem.isFormField()) {
			                continue;
			            }
			            // 得到文件的完整路径 (firefox下只有文件名)
			            String filename = fileItem.getName();
			            if(filename.contains("\\")) {		//IE下若包含文件路径则去掉，只留下文件名
			            	filename = filename.substring(filename.lastIndexOf("\\") + 1);
			            }
			            String saveName = Md5(filename); 	//以时间+文件名做hash，作为s3 object的key
			            
			            long size = fileItem.getSize();  //file size
//			            System.out.println("Upload Path: " + uploadPath);
			            fileItem.write(new File(uploadPath  + "/" + saveName));  //save file to ec2
	//		            FileDao.fileUpload(path, new File(name));
//			            System.out.println("file name: " + filename);
//			            System.out.println("Save name: " + saveName);
			            
			            String key = content + "/" + saveName;
			            File file = new File(uploadPath + "/" + saveName);
//			            System.out.println("Key: " + key);
			            
			            String link = FileDao.fileUpload(key, file);  //upload file to s3
			            long date = System.currentTimeMillis();  //upload datetime
			            FileDao.insert2DynamoDB(key, content, filename, size, date, link);
			            file.delete();  //delete this file
			            
			            fileNum++;
//			            System.out.println(filename + " : Upload done!");
			            	            
					}
					
					JSONObject msg = new JSONObject("{\'message\' : \'" + fileNum + " file(s) has been uploaded!\'}");
					out.print(msg);
		            out.flush();
		            out.close();
		            
				} catch (Exception e) {
					e.printStackTrace();
					String msg = "{\'message\' : \'" + e.getMessage() + "\'}";
					out.print(msg);

				}
	        }
        }
	}
	
	private String Md5(String str){
		try {
			str += String.valueOf(System.currentTimeMillis());
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if(i<0) i+= 256;
				if(i<16) buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return str;
	}

}
