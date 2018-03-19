/**
 * Store Inverted Index information of html
 * @author qiaojianhu
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class InvertedIndex {
	
	/**
	 *  Stores a tree mapping of words to the positions the words were found and the html name.
	 */
	private TreeMap<String,TreeMap<String,Set<Integer>>> data;
	
	/**
	 * Initializes the data
	 */
	public InvertedIndex() {
		data = new TreeMap<String,TreeMap<String,Set<Integer>>>();
	}
	
	/**
	 * get the data
	 * @return
	 */
	public TreeMap<String,TreeMap<String,Set<Integer>>> getData(){
		return data;
	}
	
	/**
	 * return how many times the word in the data
	 * @param word
	 * 		the word we looking for
	 * @return
	 * 		how many times the word in the data
	 */
	public int sumOfWords(String word) {
		int sum = 0;
		TreeMap<String,Set<Integer>> tmp = data.get(word);
		for(String key :tmp.keySet()) {
			sum += tmp.get(key).size();
		}
		return sum;
	}
	
	/**
	 * return the size of one word show up how many time in exact one html
	 * @param key
	 * 		the word
	 * @param html
	 * 		in which html
	 * @return
	 */
	public int size(String key,String html) {
		return data.get(key).get(html).size();
	}
	
	/**
	 * get the number set of a word in one html 
	 * @param key
	 * @param html
	 * @return
	 */
	public Set<Integer> getSet(String key,String html){
		return data.get(key).get(html);
	}
	
	/**
	 * Adds the word and the position it was found to the index.
	 * @param index
	 */
	public void addData(WordIndex index) {
		
		for(String word: index.getWordSet()) {
			
			if(word.equals("")) {
				continue;
			}
			
			if(contains(word)) {
				TreeMap<String,Set<Integer>> tmp = data.get(word);
				tmp.put(index.getHtml(), index.getSet(word));
				data.put(word, tmp);
			}else {
				TreeMap<String,Set<Integer>> tmp = new TreeMap<String,Set<Integer>>();
				tmp.put(index.getHtml(), index.getSet(word));
				data.put(word, tmp );
			}
		}	
	}
	
	/**
	 * sort the set
	 * @param set
	 * @return
	 */
	private List<Integer> sortInt(Set<Integer> set){
		List<Integer> ints = new ArrayList<Integer>();  
		for(Integer num: set) {
			ints.add(num);
		}  
		Collections.sort(ints);
		return ints;
	}
		
	/**
	 * if contains the key
	 * @param word
	 * @return
	 */
	public boolean contains(String word) {
		return data.containsKey(word);
	}
	
	/**
	 * toString for position
	 * @param word
	 * @param html
	 * @return
	 */
	public String toStringPostion(String word, String html) {
		String result = "";
		Set<Integer> set = (Set<Integer>) data.get(word).get(html);
		for(int position: sortInt(set)) {
			result += "\n\t\t\t" + position +",";
		}
		return result.substring(0,result.lastIndexOf(",")) + "\n";
	}

	/**
	 * toString for html
	 * @param word
	 * @return
	 */
	public String toStringHtml(String word) {
		String result = "";
		TreeMap<String,Set<Integer>> tmp = data.get(word);
		for(String html: tmp.keySet()) {
			result += "\n\t\t\""+html+"\": ["+ toStringPostion(word,html) + "\t\t],";
		}
		return result.substring(0,result.lastIndexOf(",")) + "\n";
	}
	
	/**
	 * toString for words
	 */
	public String toString() {
		String result = "{\n";
		for(String word: data.keySet()) {
			result +="\t\"" + word +"\": {"
		+ toStringHtml(word)
		+"\t},\n";
		}
		return result.substring(0,result.lastIndexOf(",")) + "\n}";
	}
}
