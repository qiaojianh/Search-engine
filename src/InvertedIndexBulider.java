/**
 * build the InvertedIndex data base
 * @author qiaojianhu
 *
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class InvertedIndexBulider {

	/**
	 * Data members
	 */
	private String path;
	private String fileName;
	private String resultFileName;
	private String termsfile;
	private File file;
	private InvertedIndex data;
	private PartialSearch partialSearchdata;
	private boolean ifMakeFile;
	private boolean ifMakeSearchedTermFile; 
	private boolean hasTermFile;
	private boolean ifExact;
	private ArrayList<String> terms;

	/**
	 * Initializes all data members
	 */
	public InvertedIndexBulider() {
		this.path = null;
		this.fileName = "index.json";
		this.resultFileName = "results.json";
		this.file = null;
		this.data = new InvertedIndex();
		this.partialSearchdata = new PartialSearch();
		this.ifMakeFile = true;
		this.ifMakeSearchedTermFile = false;
		this.hasTermFile = false;
		this.ifExact = false;
		this.terms = new ArrayList<String>();
	}

	/**
	 * Paras the args
	 * @param args
	 * @return
	 */
	public boolean parasArgs(String[] args) {

		if(!Arrays.asList(args).contains("-index")) {
			ifMakeFile = false;
		}
				
		if (args.length < 1) {
			System.out.println("No Arguement");
			return false;
		}else if (args.length == 1 && args[0].equals("-results")) {
			makeEmptyFile("results.json");
			return false;
		} else if (args.length == 1 && args[0].equals("-index")) {
			makeEmptyFile("index.json");
			return false;
		} else if (!Arrays.asList(args).contains("-path") && Arrays.asList(args).contains("-results")) {
			makeEmptyFile("results.json");
			return false;
		}else if (!Arrays.asList(args).contains("-path")) {
			System.out.println("Bad Arguement");
			return false;
		} else if (args.length == 1 && args[0].equals("-path")) {
			System.out.println("Missing Path");
			return false;
		}
		
		path = args[Arrays.asList(args).indexOf("-path") + 1];
		file = new File(path);
		if (!file.exists()) {
			System.out.println("Invalid Path");
			return false;
		}
		// case of switch order
		if(Arrays.asList(args).indexOf("-index") + 1 < args.length && !args[Arrays.asList(args).indexOf("-index") + 1].equals("-path")) {
			fileName = args[Arrays.asList(args).indexOf("-index") + 1];
		}
		if( Arrays.asList(args).indexOf("-query") > -1 && Arrays.asList(args).indexOf("-query") + 1 < args.length && !args[Arrays.asList(args).indexOf("-query") + 1].equals("-path")) {
			hasTermFile = true;
			termsfile = args[Arrays.asList(args).indexOf("-query") + 1];
		}
			
		if( Arrays.asList(args).indexOf("-results") > -1) {
			ifMakeSearchedTermFile = true;
		}
		
		if( Arrays.asList(args).indexOf("-results") > -1 && Arrays.asList(args).indexOf("-results") + 1 < args.length && !args[Arrays.asList(args).indexOf("-results") + 1].equals("-path")) {
			ifMakeSearchedTermFile = true;
			resultFileName = args[Arrays.asList(args).indexOf("-results") + 1];
		}
		
		if(Arrays.asList(args).contains("-exact")) {
			ifExact = true;
		}
		return true;

	}

	/**
	 * Main logic of the builder 
	 * @param args
	 */
	public void bulid(String[] args) {
		
		
		if (!parasArgs(args)) {
			return;
		}
		//recursively search file and add data
		if (file.isDirectory()) {
			getDirectory(file);
		} 	else {
			addHtmlDate(file.getPath());
		}
		
		if(hasTermFile) {			
			readTerms();
			for(int i = 0; i < terms.size();i++ ) {
				partialSearchdata.addData(terms.get(i),data,ifExact);
			}
		}
		// make json file
		if(ifMakeFile) {
			makeFile();
		}
		if(ifMakeSearchedTermFile) {
			makeResultFile();
		}
	}

	/**
	 * read terms from provide file
	 */
	private void readTerms() {
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
	 * recursively search file and add data
	 * @param file
	 * 		the file we look into
	 */
	private void getDirectory(File file) {
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
	}

	
	/**
	 * read and get clean data from file, then add into data base
	 * @param htmlFile
	 * 		the html file name
	 */
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
		data.addData(index);
	}

	/**
	 * make an empty file with provide name
	 * @param outPutName
	 */
	public void makeEmptyFile(String outPutName) {
		
		try (BufferedWriter bufw = new BufferedWriter(new FileWriter(outPutName));){
			bufw.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
}
	
	/**
	 * make Inverted Index output file
	 */
	public void makeFile() {		
		try (BufferedWriter bufw = new BufferedWriter(new FileWriter(fileName));){
			bufw.write(data.toString());		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * make partial Search output file
	 */
	public void makeResultFile() {
		try (BufferedWriter bufw = new BufferedWriter(new FileWriter(resultFileName));){
			bufw.write(partialSearchdata.toString());		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
