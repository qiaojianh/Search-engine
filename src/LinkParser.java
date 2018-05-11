import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

	/** Port used by socket. For web servers, should be port 80. */
	public static final int DEFAULT_PORT = 80;

	/** Version of HTTP used and supported. */
	public static final String version = "HTTP/1.1";

	/** Valid HTTP method types. */
	public static enum HTTP {
		OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
	};
	
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");
	
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

	/**
	 * Removes the fragment component of a URL (if present), and properly
	 * encodes the query string (if necessary).
	 *
	 * @param url
	 *            url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Helper method that parses HTTP headers into a map where the key is the
	 * field name and the value is the field value. The status code will be
	 * stored under the key "Status".
	 *
	 * @param headers
	 *            - HTTP/1.1 header lines
	 * @return field names mapped to values if the headers are properly
	 *         formatted
	 */
	public static Map<String, String> parseHeaders(List<String> headers) {
		Map<String, String> fields = new HashMap<>();

		if (headers.size() > 0 && headers.get(0).startsWith(version)) {
			fields.put("Status", headers.get(0).substring(version.length()).trim());

			for (String line : headers.subList(1, headers.size())) {
				String[] pair = line.split(":", 2);

				if (pair.length == 2) {
					fields.put(pair[0].trim(), pair[1].trim());
				}
			}
		}

		return fields;
	}

	/**
	 * Crafts a minimal HTTP/1.1 request for the provided method.
	 *
	 * @param url
	 *            - url to fetch
	 * @param type
	 *            - HTTP method to use
	 *
	 * @return HTTP/1.1 request
	 *
	 * @see {@link HTTP}
	 */
	public static String craftHTTPRequest(URL url, HTTP type) {
		String host = url.getHost();
		String resource = url.getFile().isEmpty() ? "/" : url.getFile();

		// The specification is specific about where to use a new line
		// versus a carriage return!
		return String.format("%s %s %s\r\n" + "Host: %s\r\n" + "Connection: close\r\n" + "\r\n", type.name(), resource,
				version, host);
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url
	 *            url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static String fetchHTML(URL url) {

		String request = craftHTTPRequest(url, HTTP.GET);
		List<String> lines = null;
		try {
			lines = fetchLines(url, request);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		int start = 0;
		int end = lines.size();

		while (!lines.get(start).trim().isEmpty() && start < end) {
			start++;
		}

		Map<String, String> fields = parseHeaders(lines.subList(0, start + 1));
		String type = fields.get("Content-Type");

		if (type != null && type.toLowerCase().contains("html")) {

			return String.join(System.lineSeparator(), lines.subList(start + 1, end));
		}

		return null;
	}

	/**
	 * Will connect to the web server and fetch the URL using the HTTP request
	 * provided. It would be more efficient to operate on each line as returned
	 * instead of storing the entire result as a list.
	 *
	 * @param url
	 *            - url to fetch
	 * @param request
	 *            - full HTTP request
	 *
	 * @return the lines read from the web server
	 *
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static List<String> fetchLines(URL url, String request) throws UnknownHostException, IOException {
		ArrayList<String> lines = new ArrayList<>();
		int port = url.getPort() < 0 ? DEFAULT_PORT : url.getPort();

		try (Socket socket = new Socket(url.getHost(), port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {

			writer.println(request);
			writer.flush();

			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	/**
	 * determine if a line of string contains url in HTML pages.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean hasURL(String url) {
		return url.contains("href");
	}

	/**
	 * fetch and clean entire html page and then parse words from the page.
	 * 
	 * @param url
	 * @return
	 */
	public static String[] fetchWords(URL url) {
		String text = HTMLcleaner.stripHTML(fetchHTML(url));
		return parseWords(text);

	}
	
	public static String clean(String text) {
		text = Normalizer.normalize(text, Normalizer.Form.NFC);
		text = CLEAN_REGEX.matcher(text).replaceAll(" ");
		return text.toLowerCase().trim();
	}


	public static String[] split(String text) {
		text = text.trim();
		return text.isEmpty() ? new String[0] : SPLIT_REGEX.split(text);
	}


	public static String[] parseWords(String text) {
		return split(clean(text));
	}


	public static TreeSet<String> uniqueWords(String text) {
		TreeSet<String> words = new TreeSet<>();
		Collections.addAll(words, parseWords(text));
		return words;
	}

	/**
	 * crawl url links from a single web page.
	 * 
	 * @param link
	 * @param set
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static ArrayList<URL> crawlURL(URL seed) throws UnknownHostException, IOException, URISyntaxException {

		int port = seed.getPort() < 0 ? DEFAULT_PORT : seed.getPort();
		String request = craftHTTPRequest(seed, HTTP.GET);
		try (Socket socket = new Socket(seed.getHost(), port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {

			writer.println(request);
			writer.flush();
			String line = null;
			ArrayList<String> links = new ArrayList<>();

			while ((line = reader.readLine()) != null) {
				if (hasURL(line)) {
					links.add(line);
				}
			}
			return listLinks(seed, links);

		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to
	 * absolute using the base URL and cleaned (removing fragments and encoding
	 * special characters as necessary).
	 *
	 * @param base
	 *            base url used to convert relative links to absolute3
	 * @param html
	 *            raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 * @throws MalformedURLException
	 */
	public static ArrayList<URL> listLinks(URL base, String html) throws MalformedURLException {
		
		
		ArrayList<URL> links = new ArrayList<URL>();
		// TODO
		String regex;
//		System.out.println("html =============================: " + html);
		ArrayList<String> list = new ArrayList<String>();
		regex = "(?i)href(\\s*)=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)";
		Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
		Matcher ma = pa.matcher(html);
		while (ma.find())
	    {
			list.add(ma.group());
	    }

		for(int i = 0 ; i< list.size(); i++) {
			if( list.get(i).contains("<")) {
				continue;
			}
			String tmp = list.get(i).substring( list.get(i).indexOf("\"")+1, list.get(i).indexOf("\"", list.get(i).indexOf("\"")+1));

			if(tmp.contains("#")) {
				tmp = tmp.substring(0,tmp.indexOf("#"));
				if (tmp.equals("")) {
					links.add(base);
					continue;
				}
			}
			
			
			if(! (tmp.contains("http") | tmp.contains("HTTP"))) {
				if(base.toString().endsWith("/")) {
					tmp = base.toString() + tmp;
				}else if(base.toString().endsWith(".html") ||base.toString().endsWith(".htm") ){

					if(base.toString().contains("user") && tmp.startsWith("../")) {
						tmp = base.toString().substring(0, base.toString().lastIndexOf("user"))  + tmp.substring(tmp.indexOf("/") + 1);
					}else {
						tmp = base.toString().substring(0, base.toString().lastIndexOf("/")) + "/" + tmp;
					}
				}else {
					tmp = base.toString() + "/" + tmp;
				}
				
			}
			if (tmp.contains(".css")) {
				continue;
			}
			if(tmp.contains("@")) {
				continue;
			}

			links.add(new URL(tmp));
			
		}
		
	   return links;
	}
	
	public static ArrayList<URL> listLinks(URL base, ArrayList<String> htmls) throws MalformedURLException {

		int GROUP = 1;
		ArrayList<URL> links = new ArrayList<URL>();
		String REGEX = "(?i)<a(?:[^<>]*?)href=\"([^\"]+?)\"";

		if (htmls.isEmpty() == false) {
			for (String html : htmls) {

				URL link = null;

				Pattern pattern = Pattern.compile(REGEX);
				Matcher match = pattern.matcher(html.replaceAll("\\s", "").trim());

				while (match.find()) {
					String site = match.group(GROUP);

					if (!site.startsWith("http")) {

						link = new URL(base, site);

					} else {
						link = new URL(site);
					}
					if (link.toString().startsWith("http")) {
						links.add(clean(link));
					}

				}
			}
		}

		return links;
	}
}