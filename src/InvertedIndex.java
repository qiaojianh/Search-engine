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
import java.util.TreeSet;


public class InvertedIndex {
	
	/**
	 *  Stores a tree mapping of words to the positions the words were found and the html name.
	 */
	private TreeMap<String,TreeMap<String,Set<Integer>>> data;
	private ReadWriteLock lock;
	
	/**
	 * Initializes the data
	 */
	public InvertedIndex() {
		this.lock = new ReadWriteLock();
		data = new TreeMap<String,TreeMap<String,Set<Integer>>>();
	}
	
	/**
	 * get the data
	 * @return
	 */
	
	public ReadWriteLock getLock() {
		return lock;
	}
	
	public TreeMap<String,TreeMap<String,Set<Integer>>> getData(){
		return data;
	}
	
	public int size() {
		return data.size();
	}
		
	/**
	 * Add contents to tree-map in this class by calling a hash-map from another
	 * class of InvertedIndex.
	 * 
	 * @param words
	 *            String array of words
	 * @param path
	 *            path to store
	 */
	public void addAll(TreeMap<String, TreeMap<String, Set<Integer>>> treeMap) {
		for (String word : treeMap.keySet()) {
			if (this.data.containsKey(word) == false) {
				this.data.put(word, treeMap.get(word));
			} else {
				for (String path : treeMap.get(word).keySet()) {
					if (this.data.get(word).containsKey(path) == false) {
						this.data.get(word).put(path, treeMap.get(word).get(path));
					} else {
						this.data.get(word).get(path).addAll(treeMap.get(word).get(path));
					}

				}
			}
		}
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
	}
	
	public void addHtmlText(String htmltext) {
		WordIndex index = new WordIndex();
		index.addAll(HTMLcleaner.stripHTML(htmltext).toLowerCase().replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", " ").replaceAll("(?U)\\p{Space}+", " ").trim().split(" "));
		addData(index);
	}
	
	
	public void getDirectory(File file) {
//		this.lock = new ReadWriteLock();
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
//		this.lock.unlockReadWrite();
	}
	
	public void buildData(File file) {
//		lock.lockReadWrite();
		this.lock = new ReadWriteLock();
		if (file.isDirectory()) {
			getDirectory(file);
		}else {
			if(file.getName().toLowerCase().endsWith("html") || file.getName().toLowerCase().endsWith("htm")) {
				addHtmlDate(file.getPath());
			}
		}
		lock.unlockReadWrite();
	}
	
	/**
	 * sort the set
	 * @param set
	 * @return
	 */
	private List<Integer> sortInt(Set<Integer> set){
//		this.lock = new ReadWriteLock();
		List<Integer> ints = new ArrayList<Integer>();  
		for(Integer num: set) {
			ints.add(num);
		}  
		Collections.sort(ints);
//		this.lock.unlockReadWrite();
		return ints;
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

	public void addIndexData(String[] words, String path) {
		lock.lockReadWrite();
		int wordPosition = 0;
		for (String word : words) {

			wordPosition += 1;
			addHelper(word, path, wordPosition);
		}
		lock.unlockReadWrite();
	}

	/**
	 * helper method to parse word, text files, and position of words in text
	 * files to map
	 * 
	 * @param word
	 * @param txtFile
	 * @param position
	 */
	private void addHelper(String word, String txtFile, int position) {
		
		if (!hasWord(word)) {
			data.put(word, new TreeMap<>());
		}

		if (!data.get(word).containsKey(txtFile)) {
			data.get(word).put(txtFile, new TreeSet<>());
		}

		data.get(word).get(txtFile).add(position);
		

	}
	


	
	public boolean hasWord(String word) {
		return data.containsKey(word);
	}

	public void setData(TreeMap<String, TreeMap<String, Set<Integer>>> data) {
		this.data = data;
		
	}



	
	
}
