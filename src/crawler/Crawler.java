package crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Crawler takes depth, maxpage and url as an
 * arguments and crawl url, stores pages, log 
 * information.
 * 
 * @author EmreCan
 *
 */
public class Crawler {
	
	public static final String DATADIR = "data/collections";
	public static final String LOGFILE = "crawl.log";
	
	private Url initialUrl;
	private int maxDepth;
	private int maxPages;
	private int collectionId = 0;
	private int docId = 0;
	
	private Set<Url> pagesVisited = new HashSet<Url>();
	private List<Url> pagesToVisit = new LinkedList<Url>();
	
	/**
	 * Constructor.
	 * 
	 * @param url
	 * @param depth
	 * @param pages
	 */
	Crawler (String url, int depth, int pages) {
		this.initialUrl = new Url(url, 0);
		this.maxDepth = depth;
		this.maxPages = pages;
		createDirectory();
	}
	
	/**
	 * Crawl the url with outgoing links recursively.
	 * Visited pages are discarded.
	 * 
	 */
	public void collect() {
		while (this.pagesVisited.size() < this.maxPages) {
			Url currentUrl;
			Spider spider = new Spider();
			
			if (this.pagesToVisit.isEmpty()) {
				currentUrl = this.initialUrl;
				this.pagesVisited.add(currentUrl);
			} else {
				currentUrl = this.nextUrl();
			}
			
			// Maximum depth control
			if (currentUrl.getDepth() > this.maxDepth) {
				if (InitCrawler.DEBUGMODE) System.out.println("[I]Reached max-depth count.");
				return;
			}
			
			spider.crawl(currentUrl);
			spider.saveHtml(saveLocation());
			this.pagesVisited.add(currentUrl);
			
			this.pagesToVisit.addAll(spider.getLinks());
		}
		
		if (InitCrawler.DEBUGMODE) System.out.println("[I]Reached max-page count.");
	}
	
	/**
	 * Create directory for content storage.
	 * Assign numerical value to directories sequentially.
	 * 
	 * @return
	 */
	private boolean createDirectory() {
		while (true) {
			Path path = Paths.get(System.getProperty("user.dir"), DATADIR, Integer.toString(collectionId));
			String pathStr = path.toString();
			File dir = new File(pathStr);
			
			if (!dir.exists()) {
				try {
					dir.mkdir();
					if (InitCrawler.DEBUGMODE) System.out.println("[I]Created directory: " + pathStr);
					return true;
				} catch (SecurityException se) {
					return false;
				}
			} else {
				(this.collectionId)++;
			}
		}
	}
	
	/**
	 * Create a log file in the collection directory.
	 * Log includes: creation date, initial url, max-page, max-depth
	 * number of seeded pages, list of seeded pages.
	 * @return true if successfull, false otherwise.
	 */
	public boolean createLog() {
		Path path = Paths.get(System.getProperty("user.dir"), DATADIR, Integer.toString(collectionId), LOGFILE);
		String pathStr = path.toString();
		
		try {
			PrintWriter writer = new PrintWriter(pathStr, "UTF-8");
			
			writer.println("Creation-date:\t" + new Date());
			writer.println("Initial-url:\t" + this.initialUrl.getUrl());
			writer.println("Max-depth:\t" + this.maxDepth);
			writer.println("Max-pages:\t" + this.maxPages);
			writer.println("Count-pages:\t" + this.pagesVisited.size());
			writer.println("Crawled-pages:");
			
			Iterator<Url> iterator = this.pagesVisited.iterator();
			while (iterator.hasNext()) {
				Url url = iterator.next();
				writer.println(url.getUrl() + "\t" + url.getDepth());
			}
			writer.close();
			
			if (InitCrawler.DEBUGMODE) System.out.println("[I]Created log: " + path.toString());
			
			return true;
		} catch (FileNotFoundException e) {
			if (InitCrawler.DEBUGMODE) System.out.println("[E]Collection directory does not exist. Log file couldn't generated.");
			return false;
		} catch (UnsupportedEncodingException e) {
			if (InitCrawler.DEBUGMODE) System.out.println("[E]Unsupported encoding in log file.");
			return false;
		}
	}
	
	/**
	* Unvisited Url object is returned.
	*
	* @return unvisited Url.
	*/
	private Url nextUrl() {
		Url nextUrl;
		
		do {
			nextUrl = this.pagesToVisit.remove(0);
		} while(this.pagesVisited.contains(nextUrl));
		
		return nextUrl;
	}
	
	/**
	 * Sequentially generate path for documents.
	 * 
	 * @return path string for next document storage.
	 */
	private String saveLocation () {
		Path path = Paths.get(System.getProperty("user.dir"), DATADIR, Integer.toString(collectionId), Integer.toString(docId));
		String pathStr = path.toString();
		(this.docId)++;
		
		return pathStr;
	}
}
