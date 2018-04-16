import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InvertedIndexTask implements Runnable {
	
		private InvertedIndex data;
		
		private Path path;
		
		public InvertedIndexTask( Path path, InvertedIndex data ) {
			this.path = path;
			this.data = data;
		}
		
		
		@Override
		public void run() {
//			synchronized (data) {
				File file = path.toFile();
				if (file.getName().toLowerCase().endsWith("html") || file.getName().toLowerCase().endsWith("htm")) {
					WordIndex index = new WordIndex(file.toString());
					Path htmlPath = Paths.get(file.toString());
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
//					data.addHtmlDate(path.toString());
					synchronized (data) {
						data.addData(index);
					}
				}
//			}
		}

}
