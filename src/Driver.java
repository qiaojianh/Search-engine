import java.io.File;
import java.util.ArrayList;
/**
 * 
 * The Drive of project
 * @author qiaojianhu
 *
 */
public class Driver {
	
	public static void main(String[] args) {
		

		InvertedIndex data = new InvertedIndex();
		ArgumentMap maps = new ArgumentMap();
		PartialSearch pdata = new PartialSearch();
		ArrayList<String> terms = new ArrayList<String>();
		
		maps.parse(args);
		
		if( maps.hasFlag("-path")) {
			if(maps.getString("-path") == null) {
				return;
			}
			File file = new File(maps.getString("-path"));	
			if (!file.exists()) {
				return;
			}else {
				if (file.isDirectory()) {
					Bulider.getDirectory(file, data);
				}else {
					Bulider.addHtmlDate(file.getPath(),data);
				}
			}
		}
		
		if( maps.hasFlag("-index")) {
			if(maps.numFlags() == 1) {
				Bulider.makeEmptyFile(maps.getString("-index"));
				return;
			}
			Bulider.makeFile(maps.getString("-index"), data);
		}
		
		if( maps.hasFlag("-query")) {
			if(maps.getString("-query") == null) {
				return;
			}
			Bulider.readTerms(maps.getString("-query"),terms);
			for(int i = 0; i < terms.size();i++ ) {
				pdata.addData(terms.get(i),data,maps.hasFlag("-exact")?true:false);
			}
		}
		
		if( maps.hasFlag("-results")) {
			if(maps.numFlags() == 1) {
				Bulider.makeEmptyFile(maps.getString("-results"));
				return;
			}
			Bulider.makeResultFile(pdata,maps.getString("-results"));
		}
					
	}
}
