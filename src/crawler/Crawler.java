package crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import utils.Enums;

/**
 * Crawler takes depth, maxpage and url as an
 * arguments and crawl url, stores pages, log 
 * information.
 * 
 * @author EmreCan
 *
 */
public class Crawler {
	
	private Url initialUrl;
	private int maxDepth;
	private int maxPages;
	private int collectionId = 0;

	private int docId = 0;
	
	private Set<Url> pagesVisited = new HashSet<Url>();
	private List<Url> pagesToVisit = new LinkedList<Url>();
	
	/**
	 * Crawler constructor.
	 * 
	 * @param url
	 * @param depth
	 * @param pages
	 */
	public Crawler (String url, int depth, int pages) {
		this.initialUrl = new Url(url, 0);
		this.maxDepth = depth;
		this.maxPages = pages;
		createDirectories();
	}
	
	/**
	 * Crawl the url with outgoing links recursively.
	 * Visited pages are discarded.
	 * 
	 */
	public void collect() {
		String mapFilePath = createUrlMap();
		
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
				if (Enums.DEBUGMODE) System.out.println("[I]Reached max-depth count.");
				return;
			}
			
			spider.crawl(currentUrl);
			spider.appendToMap(mapFilePath, this.docId, currentUrl);
			spider.saveHtml(saveLocation());
			this.pagesVisited.add(currentUrl);
			
			this.pagesToVisit.addAll(spider.getLinks());
		}
		
		if (Enums.DEBUGMODE) System.out.println("[I]Reached max-page count.");
	}
	
	/**
	 * Creates a map file which includes
	 * document name - url maps per line.
	 * 
	 * @return path of the map file
	 */
	private String createUrlMap() {
		Path path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.LOGS_DIR, Integer.toString(this.collectionId).concat(Enums.MAP_URL_FILE));
		String pathStr = path.toString();
		
		File file = new File(pathStr);
		
		try {
			file.createNewFile();
		} catch (IOException ioe) {
			if (Enums.DEBUGMODE) System.out.println("[E]Can't create file: " + pathStr);
		}
		
		return pathStr;
	}

	/**
	 * Creates collection directory for content storage.
	 * Assign numerical value to directories sequentially.
	 * 
	 * @return
	 */
	private boolean createCollectionDirectory() {
		while (true) {
			Path path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
					Enums.COLLECTIONS_DIR, Integer.toString(collectionId));
			
			if (!Enums.createDirectory(path)) {
				(this.collectionId)++;
			} else
				break;
		}
		
		return true;
	}
	
	private void createDirectories() {
		Path data_path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR);
		Path collections_path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.COLLECTIONS_DIR);
		Path indexes_path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.INDEXES_DIR);
		Path logs_path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.LOGS_DIR);
		
		if (Enums.createDirectory(data_path) && Enums.createDirectory(collections_path)) {
			Enums.createDirectory(indexes_path);
			Enums.createDirectory(logs_path);
			createCollectionDirectory();
		}
	}
	
	/**
	 * Create a log file in the collection directory.
	 * Log includes: creation date, initial url, max-page, max-depth
	 * number of seeded pages, list of seeded pages.
	 * @return true if successfull, false otherwise.
	 */
	public boolean createLog() {
		Path path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.LOGS_DIR, Integer.toString(this.collectionId).concat(Enums.LOG_FILE));
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
			
			if (Enums.DEBUGMODE) System.out.println("[I]Created log: " + path.toString());
			
			return true;
		} catch (FileNotFoundException e) {
			if (Enums.DEBUGMODE) System.out.println("[E]Collection directory does not exist. Log file couldn't generated.");
			return false;
		} catch (UnsupportedEncodingException e) {
			if (Enums.DEBUGMODE) System.out.println("[E]Unsupported encoding in log file.");
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
		Path path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.COLLECTIONS_DIR, Integer.toString(collectionId), Integer.toString(docId));
		String pathStr = path.toString();
		(this.docId)++;
		
		return pathStr;
	}
	
	/**
	 * Getter for crawled collection id
	 * 
	 * @return collection id
	 */
	public int getCollectionId() {
		return collectionId;
	}
}
