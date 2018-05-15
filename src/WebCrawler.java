import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is a thread-version of webcrawler class using multithreading
 * @author Qiaojian Hu
 */

public class WebCrawler {
	
	//Data member
	private WorkQueue queue;
	private Set<URL> set;
	
	/**
	 * Initializes the data
	 */
	public WebCrawler() {
		this.set = new HashSet<>();
	}

	/**
	 * crawl url using work queue recursively from the base url as input
	 * 
	 * @param seed
	 *            base url
	 * @param limit
	 *            number of urls that should be crawled
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public TreeMap<String, TreeMap<String, Set<Integer>>> crawl(URL seed, int limit, int threads) throws MalformedURLException {
		queue = new WorkQueue(threads);
		InvertedIndex local = new InvertedIndex();
		set.add(seed);		
		queue.execute(new WebCrawlerTask(seed, limit,local));
		queue.finish();
		return local.getData();
	}
	
	
	/**
	 * Runnable task that crawl each url and store info from url to
	 * InvertedIndex
	 */
	private class WebCrawlerTask implements Runnable {

		private URL seed;
		private int limit;
		private InvertedIndex data;

		/**
		 * Default constructor
		 * 
		 * @param seed
		 *            base url
		 * @param limit
		 *            number of urls that should be crawled
		 */
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
