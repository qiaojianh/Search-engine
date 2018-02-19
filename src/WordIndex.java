import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordIndex {
	
	//Data members
	private Map<String, Set<Integer>> index;
	private String html; 
	
	//constructor
	public WordIndex() {
		index = new HashMap<String, Set<Integer>>();
		html = null;
	}
	
	public WordIndex(String html) {
		index = new HashMap<String, Set<Integer>>();
		this.html = html.substring(html.indexOf("html"));
	}
	
	//getter
	public Set<String> getWordSet(){
		return index.keySet();
	}
	
	public Map<String, Set<Integer>> getIndex(){
		return index;
	}
	
	public int words() {
		return index.size();
	}
	
	public boolean contains(String word) {
		return index.containsKey(word);
	}
	
	public String getHtml() {
		return html;
	}
	
	public Set<Integer> getSet(String key){
		return index.get(key);
	}
	
	//add data
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
	
	public void addAll(String[] words) {
		addAll(words, 1);
	}
	
	public void addAll(String[] words, int start) {
		for(int i = 0; i < words.length; i++) {
			add(words[i],start + i);
		}
	}
	
	public int count(String word) {
		if(contains(word)) {
			return index.get(word).size();
		}
		return 0;
	}
	
	//tostring method
	public String toString() {
		return html + "\n "+ index.toString();
	}
}
