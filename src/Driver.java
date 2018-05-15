import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
/**
 * 
 * The Drive of project
 * @author qiaojianhu
 *
 */
public class Driver {
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		//Initializes all members
		InvertedIndex data = new InvertedIndex();
		ArgumentMap maps = new ArgumentMap();
		QuerySearch pdata = new QuerySearch();
		ArrayList<String> terms = new ArrayList<String>();
		WebCrawler webCrawler = new WebCrawler();
		Bulider b = new Bulider();
		final int default_maxURL = 50; 
		final int default_port = 8080;
		
		int threads = 1;
		//parse args into Argument map
		maps.parse(args);
		
		
				
		//if has flags "-threads" 
		if(maps.hasFlag("-threads")) {
			if(maps.getString("-threads") == null) {
				threads = 5;
			}else {
				if(b.isNumeric(maps.getString("-threads"))) {
					threads = Integer.parseInt(maps.getString("-threads"));
					if(threads < 1) {
						threads = 5;
					}
				}else {
					threads = 5;
				}
			}
				
		}
				
		//if has flags "-path" 
		if( maps.hasFlag("-path")) {
			if(maps.getString("-path") == null) {
				return;
			}
			File file = new File(maps.getString("-path"));	
			if (!file.exists()) {
				return;
			}else {
				if(file.isDirectory()) {
					if(threads == 1) {
						data.buildData(file);
					}else {
						data.setData(b.finddata(file, threads));
					}
				}else {
					data.buildData(file);
				}
			}
		}
		
		//if has flags "-url"
		if (maps.hasFlag("-url")) {
			
			URL seed = new URL(maps.getString("-url"));
			int limit = default_maxURL;

			if (maps.hasValue("-limit")) {
				limit = maps.getInteger("-limit", default_maxURL);
			}
			
				
			data.setData(webCrawler.crawl(seed, limit, threads));
					
		}
		
		//if has flags "-port"
		if(maps.hasFlag("-port")){
			
			int inputPort = maps.getInteger("-port", default_port);
			
			SearchServer server = new SearchServer(data, inputPort, threads);
			
			server.serverStarts();
			
		}
		
		//if has flags "-index"
		if( maps.hasFlag("-index")) {
			if(maps.numFlags() == 1) {
				b.makeEmptyFile(maps.getString("-index"));
				return;
			}
			b.makeFile(maps.getString("-index"), data);
		}
		
		//if has flags "-query"
		if( maps.hasFlag("-query")) {
			if(maps.getString("-query") == null) {
				return;
			}
			b.readTerms(maps.getString("-query"),terms);
			if(threads == 1) {
				for(int i = 0; i < terms.size();i++ ) {
					pdata.addData(terms.get(i),data,maps.hasFlag("-exact")?true:false);
				}
			}else {
				pdata.setData(b.findPdata(terms,threads,maps.hasFlag("-exact")?true:false, data));
			}
		}
		
		//if has flags "-results"
		if( maps.hasFlag("-results")) {
			if(maps.numFlags() == 1) {
				b.makeEmptyFile(maps.getString("-results"));
				return;
			}
			b.makeResultFile(pdata,maps.getString("-results"));
		}
					
	}
}
