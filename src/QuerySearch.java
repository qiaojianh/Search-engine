/**
 * This PartialSearch can do partial search base on the InvertedIndex, 
 * For this project, you will extend your previous project to support exact search and partial search. In addition to meeting the previous project requirements, 
 * your code must be able to parse a query file, generate a sorted list of search results from the inverted index, and support writing those results to a JSON file.For example,
 *  suppose we are performing partial search on an inverted index with the words: after, apple, application, and happen. If the query word is app, your code should return results 
 *  for both apple and application but not happen
 * @author qiaojianhu
 *
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class QuerySearch {
	
	/**
	 * Stores a mapping of words to the information about the words.
	 */
	private HashMap<String,ArrayList<ResultOfPartialSearch>> data;
	
	/**
	 * Initializes the data.
	 */
	public QuerySearch() {
		data = new HashMap<String,ArrayList<ResultOfPartialSearch>>();
	}
	
	public HashMap<String,ArrayList<ResultOfPartialSearch>> getData(){
		return data;
	}
	
	public void setData(HashMap<String,ArrayList<ResultOfPartialSearch>> data) {
		this.data = data;
	}
	
	
	/**
	 * first of all we get each query and go through in  invertedIndex database
	 * then dependes on the ifExact flag, we choose the contain or the startsWith
	 * then put one html with information about index and count into a object and put into a array
	 * finilly put the array into hashmap with the query
	 * @param term
	 * 		the query we looking for
	 * @param invertedIndexData
	 * 		the data base
	 * @param ifExact
	 * 		do we need exact search
	 */
	public void addData(String term, InvertedIndex invertedIndexData, boolean ifExact) {
		
		ReadWriteLock lock = invertedIndexData.getLock();
		lock.lockReadOnly();
		
		TreeMap<String,TreeMap<String,Set<Integer>>> tmpData = invertedIndexData.getData();
		ArrayList<ResultOfPartialSearch> objects = new  ArrayList<ResultOfPartialSearch>();
		ArrayList<String> htmls = new  ArrayList<String>();
		String[] terms = term.toLowerCase().split(" ");
		
		for(int i = 0; i < terms.length; i++) {
			for(String word:tmpData.keySet() ) {
				
				if(ifExact) {
					if(word.equals(terms[i])) {
					
						for(String html : tmpData.get(word).keySet()) {							
							if(!htmls.contains(html)) {								
								objects.add(new ResultOfPartialSearch(html,invertedIndexData.size(word, html),minValue(tmpData.get(word).get(html))));	
								htmls.add(html);
							}else {
								for(int j = 0; j< objects.size();j++) {
									if(objects.get(j).getWhere().equals(html)) {
										int min = objects.get(j).getIndex();
										if(minValue(tmpData.get(word).get(html)) < min) {
											objects.set(j, new ResultOfPartialSearch(html,objects.get(j).getCount()+invertedIndexData.size(word, html),minValue(invertedIndexData.getSet(word, html))));
										}else {
											objects.set(j, new ResultOfPartialSearch(html,objects.get(j).getCount()+invertedIndexData.size(word, html),min));
										}
									}
								}
							}
						}
					}
				}else {
					if(word.startsWith(terms[i])) {
						
						for(String html :tmpData.get(word).keySet()) {							
							if(!htmls.contains(html)) {								
								objects.add(new ResultOfPartialSearch(html,invertedIndexData.size(word, html),minValue(tmpData.get(word).get(html))));	
								htmls.add(html);
							}else {
								for(int j = 0; j< objects.size();j++) {
									if(objects.get(j).getWhere().equals(html)) {
										int min = objects.get(j).getIndex();
										if(minValue(tmpData.get(word).get(html)) < min) {
											objects.set(j, new ResultOfPartialSearch(html,objects.get(j).getCount()+invertedIndexData.size(word, html),minValue(invertedIndexData.getSet(word, html))));
										}else {
											objects.set(j, new ResultOfPartialSearch(html,objects.get(j).getCount()+invertedIndexData.size(word, html),min));
										}
									}
								}
							}
						}
					}
				}		
			}
		}
		lock.unlockReadOnly();
		
		Arrays.sort(terms);
		synchronized (data) {
			String rebulidTerms = Arrays.toString(terms).replaceAll("\\pP", "").trim();	
		
			if(!data.containsKey(rebulidTerms) && !rebulidTerms.equals("")) {
				data.put(rebulidTerms, objects);
			}
		}
		
	}
	
	/**
	 * get the minimum value in the Set<Integer>
	 * @param ints
	 * 		Provide Set<Integer>
	 * @return
	 * 		minimum value in the Set<Integer>
	 */
	public int minValue(Set<Integer> ints) {
		int min = 999999;
		for(int num : ints) {
			if(num < min) {
				min = num;
			}
		}
		return min;
	}
	
	/**
	 * toString method for each object of query
	 * @param query
	 * 		the query
	 * @return
	 * 		the String of all object
	 */
	public String toStringObject(String query) {
		String result = "";
		ArrayList<ResultOfPartialSearch> objects = data.get(query);
		Collections.sort(objects);
		for(int i = 0; i < objects.size() ; i++) {
			result += "\t\t\t{\n";
			result += "\t\t\t\t\"where\": \""+objects.get(i).getWhere() + "\",\n";
			result += "\t\t\t\t\"count\": "+objects.get(i).getCount() + ",\n";
			result += "\t\t\t\t\"index\": "+objects.get(i).getIndex() + "\n";
			if(i < objects.size()-1) {
				result += "\t\t\t},\n";
			}else {
				result += "\t\t\t}\n";
			}
		}
		return result;
	}
	
	/**
	 * toString method for each query
	 */
	public String toString() {
		ArrayList<String> queries = new ArrayList<String>();
		for(String tmp: data.keySet() ) {
			queries.add(tmp);
		}
		Collections.sort(queries);
		
		String result = "[\n";
		for(int i = 0; i< queries.size();i++) {
			result += "\t{\n\t\t\"queries\": \""+  queries.get(i) + "\",\n";
			result += "\t\t\"results\": [\n";
			result += toStringObject(queries.get(i));
			result += "\t\t]\n";
			result += "\t},\n";
		}
		result = result.substring(0,result.lastIndexOf(',')) + "\n";
		result += "]";
		return result;
	}
}