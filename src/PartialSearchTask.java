import java.util.Set;

public class PartialSearchTask implements Runnable {

	private QuerySearch pdata;
	
	private String term;
	
	private Boolean excat; 
	
	private InvertedIndex data;
	
	public PartialSearchTask( String term,QuerySearch pdata, Boolean excat , InvertedIndex data) {
		this.term = term;
		this.pdata = pdata;
		this.excat = excat;
		this.data = data;
	}
	
	public int minValue(Set<Integer> ints) {
		int min = 999999;
		for(int num : ints) {
			if(num < min) {
				min = num;
			}
		}
		return min;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		pdata.addData(term, data, excat);
		
		
	}
	
}
