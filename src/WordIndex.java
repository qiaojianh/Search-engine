/**
 * Data structure to store strings and their positions.
 * @author qiaojianhu
 *
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordIndex {
	
	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private Map<String, Set<Integer>> index;
	private String html; 
	
	/**
	 * Initializes the index.
	 */
	public WordIndex() {
		index = new HashMap<String, Set<Integer>>();
		html = null;
	}
	
	/**
	 * Initializes the index and html;
	 * @param html
	 * 		where the index from
	 */
	public WordIndex(String html) {
		this();
		this.html = html.substring(html.indexOf("html"));
	}
	
	/**
	 * get set of words
	 * @return
	 * 		set of words
	 */
	public Set<String> getWordSet(){
		return index.keySet();
	}
	
	/**
	 * get the position
	 * @return
	 * 		position 
	 */
	public Map<String, Set<Integer>> getIndex(){
		return index;
	}
	
	/**
	 * return how many words
	 * @return
	 * 		how many words
	 */
	public int words() {
		return index.size();
	}
	
	/**
	 * check if the word exist in data
	 * @param word
	 * 		the word we lookling for
	 * @return
	 * 		if exist
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}
	
	/**
	 * get html name
	 * @return
	 * 		html name
	 */
	public String getHtml() {
		return html;
	}
	
	/**
	 * get the positions set
	 * @param key
	 * 		the key we looking for set
	 * @return
	 * 		key set
	 */
	public Set<Integer> getSet(String key){
		return index.get(key);
	}
	
	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word
	 *            word to clean and add to index
	 * @param position
	 *            position word was found
	 */
	public void add(String word, int position) {

		HashSet<Integer> tmp = null;
		if(contains(word)) {
			tmp = (HashSet<Integer>) index.get(word);
		}else {
			tmp = new HashSet<Integer>();
		}
		tmp.add(position);
		index.put(word, tmp);		
	}
	
	/**
	 * Adds the array of words at once, assuming the first word in the array is
	 * at position 1.
	 *
	 * @param words
	 *            array of words to add
	 *
	 * @see #addAll(String[], int)
	 */
	public void addAll(String[] words) {
		addAll(words, 1);
	}
	
	/**
	 * Adds the array of words at once, assuming the first word in the array is
	 * at the provided starting position
	 *
	 * @param words
	 *            array of words to add
	 * @param start
	 *            starting position
	 */
	public void addAll(String[] words, int start) {
		for(int i = 0; i < words.length; i++) {
			add(words[i],start + i);
		}
	}
	
	/**
	 * Return how many times the word been count
	 * @param word
	 * @return
	 */
	public int count(String word) {
		if(contains(word)) {
			return index.get(word).size();
		}
		return 0;
	}
	
	/**
	 * The toString method
	 */
	public String toString() {
		return html + "\n "+ index.toString();
	}
}
