import java.io.File;
import java.nio.file.Path;

public class InvertedIndexTask implements Runnable {
	
		private InvertedIndex data;
		
		private Path path;
		
		public InvertedIndexTask( Path path, InvertedIndex data ) {
			this.path = path;
			this.data = data;
		}
		
		
		@Override
		public void run() {
			synchronized (data) {
				File file = path.toFile();
				if (file.getName().toLowerCase().endsWith("html") || file.getName().toLowerCase().endsWith("htm")) {
					data.addHtmlDate(path.toString());
				}
			}
		}

}
