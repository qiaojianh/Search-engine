import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class WebCrawler {
	
	private WorkQueue queue;
	private Set<URL> set;
	
	public WebCrawler() {
		this.set = new HashSet<>();
	}

	public TreeMap<String, TreeMap<String, Set<Integer>>> crawl(URL seed, int limit, int threads) throws MalformedURLException {
		// TODO Auto-generated method stub
		queue = new WorkQueue(threads);
		InvertedIndex local = new InvertedIndex();
		set.add(seed);		
		queue.execute(new WebCrawlerTask(seed, limit,local));
		queue.finish();
		return local.getData();
	}
	
	

	private class WebCrawlerTask implements Runnable {

		private URL seed;
		private int limit;
		private InvertedIndex data;


		public WebCrawlerTask(URL seed, int limit, InvertedIndex local) {
			this.seed = seed;
			this.limit = limit;
			this.data = local;
		}

		@Override
		public void run() {
			
			synchronized (set) {
				try {
					ArrayList<URL> links = LinkParser.crawlURL(seed);
					for (URL link : links) {
						if (set.size() >= limit) {
							break;
						} else {
							if (set.contains(link) == false) {
								set.add(link);
								queue.execute(new WebCrawlerTask(link, limit,data));

							}
						}
					}
					data.addIndexData(LinkParser.fetchWords(seed), seed.toString());
					
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}

		}
		
	}
	
}
