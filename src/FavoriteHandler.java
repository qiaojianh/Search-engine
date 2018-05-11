import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FavoriteHandler extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		String favorite = request.getParameter("favorite");
		SearchServer.favorite.add(favorite);
		response.sendRedirect("/?query="+request.getParameter("query")+"&ifAdd=false");

	}
	
	
}