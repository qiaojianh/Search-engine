import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class DataBase {
	
	//data members
	private TreeMap<String,TreeMap> Data;
	
	//constructor
	public DataBase() {
		Data = new TreeMap<String,TreeMap>();
	}
	
	//add data
	public void addData(WordIndex index) {
		
		for(String word: index.getWordSet()) {
			
			if(word.equals("")) {
				continue;
			}
			
			if(contains(word)) {
				TreeMap<String,Set<Integer>> tmp = Data.get(word);
				tmp.put(index.getHtml(), index.getSet(word));
				Data.put(word, tmp);
			}else {
				TreeMap<String,Set<Integer>> tmp = new TreeMap<String,Set<Integer>>();
				tmp.put(index.getHtml(), index.getSet(word));
				Data.put(word, tmp );
			}
		}	
	}
	
	//sorter
	private List<Integer> SortInt(Set<Integer> set){
		List<Integer> ints = new ArrayList<Integer>();  
		for(Integer num: set) {
			ints.add(num);
		}  
		Collections.sort(ints);
		return ints;
	}
		
	public boolean contains(String word) {
		return Data.containsKey(word);
	}
	
	//toString methods
	public String toStringPostion(String word, String html) {
		String result = "";
		Set<Integer> set = (Set<Integer>) Data.get(word).get(html);
		for(int position: SortInt(set)) {
			result += "\n			" + position +",";
		}
		return result.substring(0,result.lastIndexOf(",")) + "\n";
	}

	public String toStringHtml(String word) {
		String result = "";
		TreeMap<String,Set<Integer>> tmp = Data.get(word);
		for(String html: tmp.keySet()) {
			result += "\n		\""+html+"\": ["+ toStringPostion(word,html) + "		],";
		}
		return result.substring(0,result.lastIndexOf(",")) + "\n";
	}
	
	public String toString() {
		String result = "{\n";
		for(String word: Data.keySet()) {
			result +="	\"" + word +"\": {"
		+ toStringHtml(word)
		+"	},\n";
		}
		return result.substring(0,result.lastIndexOf(",")) + "\n}";
	}
}
