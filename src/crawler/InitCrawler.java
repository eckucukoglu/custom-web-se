package crawler;

/**
* Initializer for a Crawler.
*
* @author EmreCan
*
*/
public class InitCrawler {
	//TODO: move DEBUGMODE to generic place.
	public static final boolean DEBUGMODE = true;
	
	public static void main(String[] args) {
		String seedUrl = "http://www.metu.edu.tr";
		int maxDepth = 2;
		int maxPages = 500;
		
		Crawler crawler = new Crawler(seedUrl, maxDepth, maxPages);
		crawler.collect();
		crawler.createLog();
	}
}