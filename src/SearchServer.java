import java.util.ArrayList;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchServer {
	private InvertedIndex index;
	private int port;
	private int threads;
	static ArrayList<HistoryObject> searchHistory;
	static ArrayList<HistoryObject> visitHistory;
	static ArrayList<String> favorite;
	static boolean mode;
	static boolean ifPartial;
	static String lastVisitTime;
	


	public SearchServer(InvertedIndex index, int port, int threads) {
		this.index = index;
		this.port = port;
		this.threads = threads;
		SearchServer.searchHistory = new ArrayList<HistoryObject>();
		SearchServer.visitHistory = new ArrayList<HistoryObject>();
		SearchServer.favorite = new ArrayList<String>();
		SearchServer.lastVisitTime = "not yet";
		mode = true;
		ifPartial = false;
		
	}
	
	public void serverStarts(){
		Server server = new Server(port);
		ServletHandler handler = new ServletHandler();
		
		handler.addServletWithMapping(new ServletHolder(new index(index,threads)), "/");
		handler.addServletWithMapping(new ServletHolder(new Shistory()), "/search history");
		handler.addServletWithMapping(new ServletHolder(new Vhistory()), "/visit history");
		handler.addServletWithMapping(new ServletHolder(new Favorite()), "/favorite");
		handler.addServletWithMapping(new ServletHolder(new FavoriteHandler()), "/favoriteHandler");
		handler.addServletWithMapping(new ServletHolder(new PrivateMode()), "/privateMode");
		handler.addServletWithMapping(new ServletHolder(new PartialMode()), "/partialMode");
		handler.addServletWithMapping(new ServletHolder(new NewSearch()), "/newSearch");
		
		server.setHandler(handler);
		try{
			server.start();
			server.join();
			
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
}
