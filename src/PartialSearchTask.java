public class PartialSearchTask implements Runnable {

	private PartialSearch pdata;
	
	private String term;
	
	private Boolean excat; 
	
	private InvertedIndex data;
	
	public PartialSearchTask( String term,PartialSearch pdata, Boolean excat , InvertedIndex data) {
		this.term = term;
		this.pdata = pdata;
		this.excat = excat;
		this.data = data;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (pdata) {
			pdata.addData(term, data, excat);
		}
		
	}
	
}
