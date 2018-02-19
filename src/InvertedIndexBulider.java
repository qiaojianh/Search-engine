import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class InvertedIndexBulider {

	//Data members
	private String path;
	private String fileName;
	private File file;
	private DataBase data;
	private boolean make;

	//constructor
	public InvertedIndexBulider() {
		this.path = null;
		this.fileName = "index.json";
		this.file = null;
		this.data = new DataBase();
		this.make = true;
	}

	//check args
	public boolean parasArgs(String[] args) {

		if(!Arrays.asList(args).contains("-index")) {
			make = false;
		}
				
		if (args.length < 1) {
			System.out.println("No Arguement");
			return false;
		} else if (args.length == 1 && args[0].equals("-index")) {
			MakeEmptyFile();
			return false;
		} else if (!Arrays.asList(args).contains("-path")) {
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
		return true;

	}

	// main logic
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
		// make json file
		if(make) {
			MakeFile();
		}
	}

	// recursively search file and add data
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

	//read and get clean data from file, then add into data base
	public void addHtmlDate(String htmlFile) {
		WordIndex index = new WordIndex(htmlFile);
		Path htmlPath = Paths.get(htmlFile);
		Charset charset = Charset.forName("UTF-8");
		String temp, html = "";

		try {
			BufferedReader br = Files.newBufferedReader(htmlPath, charset);

			while ((temp = br.readLine()) != null) {
				html += temp + " ";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		index.addAll(HTMLcleaner.stripHTML(html).toLowerCase().replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", " ").replaceAll("(?U)\\p{Space}+", " ").trim().split(" "));
		data.addData(index);
	}

	//make an empty file
	public void MakeEmptyFile() {
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter bufw = new BufferedWriter(fw);
			bufw.write("");
			bufw.flush();  
			bufw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
}
	
	//make a file
	public void MakeFile() {		
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter bufw = new BufferedWriter(fw);
			bufw.write(data.toString());
			bufw.flush();  
			bufw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	//used to chenk output
	public void print() {
		System.out.println(data.toString());
	}

}
