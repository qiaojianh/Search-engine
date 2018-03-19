/**
 * The object store all information about the Partial Search words
 * @author qiaojianhu
 *
 */
public class resultOfPartialSearch implements Comparable<resultOfPartialSearch>{
	
	/**
	 * store Frequency, Position and Location
	 */
	int count;
	int index;
	String where;
	
	/**
	 *  Initializes Frequency, Position and Location
	 * @param where
	 * @param count
	 * @param index
	 */
	public resultOfPartialSearch(String where, int count, int index) {
		this.index = index;
		this.count = count;
		this.where = where;
	}
	
	/**
	 * return Location
	 * @return
	 */
	public String getWhere() {
		return where;
	}
	
	/**
	 * return Frequency
	 * @return
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * return Position
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set Location with new one
	 * @param s
	 */
	public void setWhere(String s) {
		where = s;
	}
	
	/**
	 * Set Frequency with new one
	 * @param s
	 */
	public void setCount(int i ) {
		count = i;
	}
	
	/**
	 * Set Position with new one
	 * @param s
	 */
	public void setIndex(int i) {
		index = i;
	}

	/**
	 * Compares Objects by Location in case-insensitive order. If the Position are the same,
	 * compares by Position in ascending order. If the Locations are the same,
	 * compares by Locations in ascending order. 
	 */
	@Override
	public int compareTo(resultOfPartialSearch o) {
		if(Integer.compare( o.count,count)==0) {
			if(Integer.compare( index,o.index)==0) {
				return where.compareTo(o.where);
			}
			return Integer.compare( index,o.index);
		}
		return Integer.compare(o.count,count);
	}
	
}
