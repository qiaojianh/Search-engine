import java.util.ArrayList;

public class Userdata {
	private ArrayList<HistoryObject> searchHistory;
	private ArrayList<HistoryObject> visitHistory;
	private ArrayList<String> favorite;
	private String loginTime;

	
	public Userdata( ArrayList<HistoryObject> searchHistory,ArrayList<HistoryObject> visitHistory,ArrayList<String> favorite, String loginTime) {
		this.searchHistory = searchHistory;
		this.visitHistory = visitHistory;
		this.favorite = favorite;
		this.loginTime = loginTime;
	}
	
	public ArrayList<HistoryObject> getSearchHistory(){
		return searchHistory;
	}
	
	public ArrayList<HistoryObject> getVisitHistory(){
		return visitHistory;
	}
	
	public ArrayList<String> getFavorite(){
		return favorite;
	}
	
	public String getLogInTime() {
		return loginTime;
	}
	
}
