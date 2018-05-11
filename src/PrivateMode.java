import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class PrivateMode extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		if(SearchServer.mode == true) {
			SearchServer.mode = false;
		}else {
			SearchServer.mode = true;
		}
		response.sendRedirect("/?query="+request.getParameter("query")+"&ifAdd=false");

	}
}
