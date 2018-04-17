import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Bulider {
	

	/**
	 *  Build PartialSearch file
	 * @param pdata
	 * @param resultFileName
	 */
	public  void makeResultFile(QuerySearch pdata, String resultFileName) {
		try (BufferedWriter bufw = new BufferedWriter(new FileWriter(resultFileName));){
			bufw.write(pdata.toString());		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * read terms from provide file
	 */
	public  void readTerms(String termsfile, ArrayList<String> terms ) {
		Path termsfilePath = Paths.get(termsfile);
		Charset charset = Charset.forName("UTF-8");
		String temp = "";
		try(BufferedReader br = Files.newBufferedReader(termsfilePath, charset);){
			while ((temp = br.readLine()) != null) {
				terms.add(temp.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", " ").replaceAll("[0-9]", " ").replaceAll("(?U)\\p{Space}+", " ").trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Use work queue to build PartialSearch
	 * @param terms
	 * @param threads
	 * @param excat
	 * @param data
	 * @return
	 */
	public  HashMap<String,ArrayList<ResultOfPartialSearch>> findPdata( ArrayList<String> terms ,int threads, boolean excat, InvertedIndex data ){
		QuerySearch pdata = new QuerySearch();
		WorkQueue queue = new WorkQueue(threads);
		for (String term : terms) {
			queue.execute(new PartialSearchTask(term, pdata,excat,data));
		}
		queue.finish();
		queue.shutdown();
		return pdata.getData();
	}
	
	/**
	 * Recursively get all file under the path
	 * @param allFiles
	 * @param file
	 */
	public  void getAllFile(ArrayList<Path> allFiles, File file) {
		File[] fileDirectory = file.listFiles();
		
		for(File tmpfile : fileDirectory) {
			if(tmpfile.isDirectory()){   
				getAllFile(allFiles,tmpfile);  
            }else {
            		allFiles.add(tmpfile.toPath());
            } 
		}
	}
	
	/**
	 * check if the string is a number
	 * @param str
	 * @return boolean
	 */
	public  boolean isNumeric(String str){   
		   Pattern pattern = Pattern.compile("[0-9]*");   
		   Matcher isNum = pattern.matcher(str);  
		   if( !isNum.matches() ){  
		       return false;   
		   }   
		   return true;   
		}  
	
	/**
	 * Use work queue to build InvertedIndex
	 * @param file
	 * @param thread: number of threads
	 */
	public  TreeMap<String,TreeMap<String,Set<Integer>>> finddata(File file, int threads ) {
		InvertedIndex buildingdata = new InvertedIndex();

		if (threads < 1) {
			throw new IllegalArgumentException("Number of worker threads must be greater than 0.");
		}

		WorkQueue queue = new WorkQueue(threads);
		ArrayList<Path> allFiles = new ArrayList<Path>();
		if(file.isDirectory()) {
			getAllFile(allFiles, file);
		}else {
			allFiles.add(file.toPath());
		}
		for (Path path : allFiles) {
			queue.execute(new InvertedIndexTask(path, buildingdata));
		}
		queue.finish();
		queue.shutdown();
		return buildingdata.getData();
	}
	
		
	/**
	 * make an empty file with provide name
	 * @param outPutName
	 */
	public  void makeEmptyFile(String outPutName) {
		
		try (BufferedWriter bufw = new BufferedWriter(new FileWriter(outPutName));){
			bufw.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
}
	
	/**
	 * make Inverted Index output file
	 */
	public  void makeFile(String fileName, InvertedIndex data) {		
		try (BufferedWriter bufw = new BufferedWriter(new FileWriter(fileName));){
			bufw.write(data.toString());		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
