
public class HistoryObject {
	private String query;
	private String Data;
	
	public HistoryObject(String query, String Data) {
		this.query = query;
		this.Data = Data;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getData() {
		return Data;
	}
}
