/**
 * Store Inverted Index information of html
 * @author qiaojianhu
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


public class InvertedIndex {
	
	/**
	 *  Stores a tree mapping of words to the positions the words were found and the html name.
	 */
	private static TreeMap<String,TreeMap<String,Set<Integer>>> data;
	private ReadWriteLock lock;
	
	/**
	 * Initializes the data
	 */
	public InvertedIndex() {
		this.lock = new ReadWriteLock();
		data = new TreeMap<String,TreeMap<String,Set<Integer>>>();
//		lock = new ReadWriteLock();
	}
	
	/**
	 * get the data
	 * @return
	 */
	
	public ReadWriteLock getLock() {
		this.lock.lockReadOnly();
		try {
			return lock;
		} finally {
			this.lock.unlockReadOnly();
		}
	}
	
	public TreeMap<String,TreeMap<String,Set<Integer>>> getData(){
		this.lock.lockReadOnly();
		try {
			return data;
		} finally {
			this.lock.unlockReadOnly();
		}
	}
	
//	public void setData(TreeMap<String,TreeMap<String,Set<Integer>>> data){
//		this.lock.lockReadWrite();
//		this.data = data;
//		this.lock.unlockReadWrite();
//	}
	
	
	
	/**
	 * return how many times the word in the data
	 * @param word
	 * 		the word we looking for
	 * @return
	 * 		how many times the word in the data
	 */
	public int sumOfWords(String word) {
		this.lock = new ReadWriteLock();
		int sum = 0;
		TreeMap<String,Set<Integer>> tmp = data.get(word);
		for(String key :tmp.keySet()) {
			sum += tmp.get(key).size();
		}
		this.lock.unlockReadWrite();
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
		this.lock.lockReadOnly();
		try {
			return data.get(key).get(html).size();
		} finally {
			this.lock.unlockReadOnly();
		}	
	}
	
	/**
	 * get the number set of a word in one html 
	 * @param key
	 * @param html
	 * @return
	 */
	public Set<Integer> getSet(String key,String html){
		this.lock.lockReadOnly();
		try {
			return data.get(key).get(html);
		} finally {
			this.lock.unlockReadOnly();
		}	
	}
	
	/**
	 * Adds the word and the position it was found to the index.
	 * @param index
	 */
	public void addData(WordIndex index) {

		this.lock.lockReadWrite();
		for(String word: index.getWordSet()) {
			
			if(word.equals("")) {
				continue;
			}
			
			if(data.containsKey(word)) {
				TreeMap<String,Set<Integer>> tmp = data.get(word);
				tmp.put(index.getHtml(), index.getSet(word));
				data.put(word, tmp);
			}else {
				TreeMap<String,Set<Integer>> tmp = new TreeMap<String,Set<Integer>>();
				tmp.put(index.getHtml(), index.getSet(word));
				data.put(word, tmp );
			}
		}	
		this.lock.unlockReadWrite();
	}
	
	public void addHtmlDate(String htmlFile) {
		this.lock = new ReadWriteLock();
		WordIndex index = new WordIndex(htmlFile);
		Path htmlPath = Paths.get(htmlFile);
		Charset charset = Charset.forName("UTF-8");
		String temp, html = "";

		try(BufferedReader br = Files.newBufferedReader(htmlPath, charset);){
			while ((temp = br.readLine()) != null) {
				html += temp + " ";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		index.addAll(HTMLcleaner.stripHTML(html).toLowerCase().replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", " ").replaceAll("(?U)\\p{Space}+", " ").trim().split(" "));
		addData(index);
		this.lock.unlockReadWrite();
	}
	
	
	public void getDirectory(File file) {
		this.lock = new ReadWriteLock();
		File flist[] = file.listFiles();
		if (flist == null || flist.length == 0) {
			return;
		}
		for (File f : flist) {
			if (f.isDirectory()) {
				getDirectory(f);
			} else if (f.getName().toLowerCase().endsWith("html") || f.getName().toLowerCase().endsWith("htm")) {
				addHtmlDate(f.getPath().toString());
			}
		}
		this.lock.unlockReadWrite();
	}
	
	public void buildData(File file) {
		this.lock = new ReadWriteLock();
		if (file.isDirectory()) {
			getDirectory(file);
		}else {
			if(file.getName().toLowerCase().endsWith("html") || file.getName().toLowerCase().endsWith("htm")) {
				addHtmlDate(file.getPath());
			}
		}
		this.lock.unlockReadWrite();
	}
	
	/**
	 * sort the set
	 * @param set
	 * @return
	 */
	private List<Integer> sortInt(Set<Integer> set){
		this.lock = new ReadWriteLock();
		List<Integer> ints = new ArrayList<Integer>();  
		for(Integer num: set) {
			ints.add(num);
		}  
		Collections.sort(ints);
		this.lock.unlockReadWrite();
		return ints;
	}
		
	
	/**
	 * toString for position
	 * @param word
	 * @param html
	 * @return
	 */
	public String toStringPostion(String word, String html) {
		this.lock = new ReadWriteLock();
		String result = "";
		Set<Integer> set = (Set<Integer>) data.get(word).get(html);
		for(int position: sortInt(set)) {
			result += "\n\t\t\t" + position +",";
		}
		this.lock.unlockReadWrite();
		return result.substring(0,result.lastIndexOf(",")) + "\n";
	}

	/**
	 * toString for html
	 * @param word
	 * @return
	 */
	public String toStringHtml(String word) {
		this.lock = new ReadWriteLock();
		String result = "";
		TreeMap<String,Set<Integer>> tmp = data.get(word);
		for(String html: tmp.keySet()) {
			result += "\n\t\t\""+html+"\": ["+ toStringPostion(word,html) + "\t\t],";
		}
		this.lock.unlockReadWrite();
		return result.substring(0,result.lastIndexOf(",")) + "\n";
	}
	
	/**
	 * toString for words
	 */
	public String toString() {
		this.lock = new ReadWriteLock();
		String result = "{\n";
		for(String word: data.keySet()) {
			result +="\t\"" + word +"\": {"
		+ toStringHtml(word)
		+"\t},\n";
		}
		this.lock.unlockReadWrite();
		return result.substring(0,result.lastIndexOf(",")) + "\n}";
	}


	
	
}
