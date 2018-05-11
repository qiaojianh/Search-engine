import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Shistory extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		response.setContentType("text/html");
		         PrintWriter out = response.getWriter();
		         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		         out.println("<HTML>");
		         out.println("  <HEAD><TITLE>Search engine</TITLE></HEAD>");
		         out.println("  <BODY>");
		         out.print(" <p> Search History </p>");
		         for(HistoryObject o: SearchServer.searchHistory) {
		        	 	out.printf(" <p>%s<br/>"
		        	 			+ "Date: %s",o.getQuery(),o.getData());
		         }
		         out.print(" </p>");
		         out.println("<form method = \"post\" action = \"/search history\">");
		         out.println(" <input type=\"submit\" value=\"Clear\"  >");
		         out.println("  </form>");
		         out.printf("<a href=\"/?query=%s&ifAdd=false\">Click here back to Home page.</a>",request.getParameter("query"));
		         out.println("  </BODY>");
		         out.println("</HTML>");
		         out.flush();
		         out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		SearchServer.searchHistory = new ArrayList<HistoryObject>();
		doGet(request,response);
	}
	
}
