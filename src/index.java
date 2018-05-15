import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class index extends HttpServlet {
	protected InvertedIndex data;
	protected int threads;
	protected Bulider b;
	protected WebCrawler webCrawler;
	
	
	public index(InvertedIndex data,int threads) {
		super();
		this.data = data;
		this.threads = threads;
		this.b = new Bulider();
		this.webCrawler = new WebCrawler();
		SearchServer.user = null;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		String re = request.getParameter("re");
		String newUrl = request.getParameter("newUrlSearch");
		if(newUrl != null && newUrl.equals("true")) {
			System.out.println("New search: " + request.getParameter("url"));
			
			try {
				URL seed = new URL(request.getParameter("url"));
				URLConnection  con = seed.openConnection();
				con.connect();
				data.addAll(webCrawler.crawl(seed, 50, threads));
			}catch(MalformedURLException e){
				System.out.println("Invalid URL");
			}
			
			
		}
		if(re != null) {
			if(SearchServer.mode == true) {
				SearchServer.visitHistory.add(new HistoryObject(re, Bulider.getDate()));
			}
			response.sendRedirect(re);
		}
		String query = request.getParameter("query");
		
		if(query != null) {
			searchPage(request,response,query);	
		}else {
		response.setContentType("text/html");
		         PrintWriter out = response.getWriter();
		         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		         out.println("<HTML>");
		         out.println("  <HEAD><TITLE>Search engine</TITLE></HEAD>");
		         out.println("  <BODY>");
		         out.print(" <div>Welcome to Qiao's Search engine");
		         if((SearchServer.user == null) == false) {
		        	 	out.printf(" %s",SearchServer.user);
		         }
		         if(SearchServer.user == null) {
		        	 	out.print("<a style = \"float: right; color: #000\"href = \"/login\">Login</a><br>");
		         }else {
		        	 	out.print("<a style = \"float: right; color: #000\"href = \"/login?logout\">Logout</a><br>");
		         }
		         out.print("<a style = \"float: right; color: #000\"href = \"/visit history\">Visit History</a><br>");
		         if(SearchServer.lastVisitTime != null) {
		        	 	out.printf(" Last visit time: %s",SearchServer.lastVisitTime);
		         }
		         out.print(" <a style = \"float: right; color: #000\"href = \"/search history\">Search History</a><br>");
		         if(SearchServer.mode == true) {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/privateMode\">Turn on private mode</a><br>");
		         }else {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/privateMode\">Turn off private mode</a><br>");
		         }
		         if(SearchServer.ifPartial == false) {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/partialMode\">Turn off partial search mode</a><br>");
		         }else {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/partialMode\">Turn on partial search mode</a><br>");
		         }
		         out.print(" <a style = \"float: right; color: #000\"href = \"/favorite\">Favorite</a> ");
		         out.print("Hello, how is your day:" + 
		        		 	"<form method = \"post\">"+
		        		 	"          <input type=\"text\" placeholder=\"query\" name = \"query\">\n" + 
		        		 	"          <input type=\"submit\" value=\"Search\"  class=\"button\">\n" + 
		        		 	"</form>" + 
		        		 	"<form method = \"post\" action = \"/newSearch\">"+
		        		 	"          <input type=\"text\" placeholder=\"New URL\" name = \"url\">\n" + 
		        		 	"          <input type=\"submit\" value=\"Search\"  class=\"button\">\n" + 
		        		 	"</form>" +
		        		 	"</p></div>");
		         out.println("  </BODY>");
		         out.println("</HTML>");
		         out.flush();
		         out.close();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String query = request.getParameter("query");
		if(query.trim().equals("")) {
			response.sendRedirect("/");
		}else {
			searchPage(request,response,query);
		}
	}
	
	protected void searchPage(HttpServletRequest request, HttpServletResponse response, String query)throws ServletException, IOException {
			
		String ifAdd = request.getParameter("ifAdd");
		if(ifAdd == null) {
			if(SearchServer.mode == true) {
				SearchServer.searchHistory.add(new HistoryObject(query,Bulider.getDate()));
			}
		}else {
			if(ifAdd.equals("false")) {
			}else {
				if(SearchServer.mode == true) {
					SearchServer.searchHistory.add(new HistoryObject(query,Bulider.getDate()));
				}
			}
		}
		
		QuerySearch pdata = new QuerySearch(); 
		String[] terms = query.split(" ");
		pdata.setData(b.findPdata(terms,threads,SearchServer.ifPartial, data));
		HashMap<String, ArrayList<ResultOfPartialSearch>> result= pdata.getData();
		
		ArrayList<String> queries = new ArrayList<String>();
		for(String tmp: result.keySet() ) {
			queries.add(tmp);
		}
		Collections.sort(queries);

		
		response.setContentType("text/html");
		         PrintWriter out = response.getWriter();
		         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		         out.println("<HTML>");
		         out.println("  <HEAD><TITLE>Search engine</TITLE></HEAD>");
		         out.println("  <BODY>");
		         out.print(" <div>Welcome to  Qiao's Search engine");
		         if((SearchServer.user == null) == false) {
		        	 	out.printf(" %s",SearchServer.user);
		         }
		         if(SearchServer.user == null) {
		        	 	out.print("<a style = \"float: right; color: #000\"href = \"/login\">Login</a><br>");
		         }else {
		        	 	out.print("<a style = \"float: right;color: #000 \"href = \"/login?logout\">Logout</a><br>");
		         }
		         out.printf(" <a style = \"float: right; color: #000\"href = \"/visit history?query=%s\">Visit History</a><br>",query);
		         if(SearchServer.lastVisitTime != null) {
		        	 	out.printf(" Last visit time: %s",SearchServer.lastVisitTime);
		         }
		         out.printf(" <a style = \"float: right; color: #000\"href = \"/search history?query=%s\">Search History</a><br>",query);
		         if(SearchServer.mode == true) {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/privateMode?query=%s\">Turn on private mode</a><br>",query);
		         }else {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/privateMode?query=%s\">Turn off private mode</a><br>",query);
		         }
		         if(SearchServer.ifPartial == false) {
		        	 	out.printf(" <a style = \"float: right;color: #000 \"href = \"/partialMode?query=%s\">Turn off partial search mode</a><br>",query);
		         }else {
		        	 	out.printf(" <a style = \"float: right; color: #000\"href = \"/partialMode?query=%s\">Turn on partial search mode</a><br>",query);
		         }
		         out.printf(" <a style = \"float: right;color: #000 \"href = \"/favorite?query=%s\">Favorite</a>",query);
		         out.print("Hello, how is your day:" + 
		        		 	"<form method = \"post\" action = \"/\">"+
		        		 	"          <input type=\"text\" placeholder=\"query\" name = \"query\">\n" + 
		        		 	"          <input type=\"submit\" value=\"Search\"  class=\"button\">\n" + 
		        		 	"</form>" + 
		        		 	"<form method = \"post\" action = \"/newSearch\">"+
		        		 	"          <input type=\"text\" placeholder=\"New URL\" name = \"url\">\n" + 
		        		 	"          <input type=\"submit\" value=\"Search\"  class=\"button\">\n" + 
		        		 	"</form>" +
		        		 	"</p></div>");
		         for(String s: queries) {
		        	 	if(result.get(s).size() != 0) {
		        	 		out.printf("<p>Term: %s",s);
		        	 		ArrayList<ResultOfPartialSearch> sortedQuery = result.get(s);
		        	 		Collections.sort(sortedQuery);
		        	 		for(ResultOfPartialSearch r: sortedQuery) {
		        	 			out.printf("<div style = \"background: #CFCFCF\">Where: <a href= \"/?re=%s\">%s</a><br/>",r.getWhere(),r.getWhere());
		        	 			out.printf("Count: %s",r.getCount());
		        	 			if(SearchServer.favorite.contains(r.getWhere())) {
		        	 				out.printf("<br>");
		        	 			}else {
		        	 			out.printf(" <a style = \"float: right; color: #000 \"href = \"favoriteHandler?favorite=%s&query=%s\">Add to favorite</a><br>",r.getWhere(),query);
		        	 			}
		        	 			out.printf("Index: %s</div></p>\n",r.getIndex()); 
		        	 			
		        	 		}

		        	 	}
		        	 	else {
		        	 		if(s.equals("null") == false) {
		        	 			out.printf("<p>No results of \"%s\"</p>\n",s);	
		        	 		}
		        	 	}
		         }
		         out.println("  </BODY>");
		         out.println("</HTML>");
		         out.flush();
		         out.close();

	}
	
}
