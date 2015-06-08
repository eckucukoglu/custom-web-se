package wse;

import indexer.Indexer;
import crawler.Crawler;

/**
 * From the given url, traverse hyperlinks
 * until the given depth or max pages reached.
 * 
 * Crawls all the pages, stores them, and then
 * indexes collection.
 * 
 * Two log files are also generated. One for crawling
 * phase information, one for document file name - url
 * matching.
 *
 */
public class CreateCorpus {

	public static void main(String[] args) {
		String usage = "java wse.CreateCorpus"
			+ " [-url SEED_URL] [-depth MAX_DEPTH] [-pages MAX_PAGES]\n\n"
			+ "This crawls the seed url until max depth or max pages number "
			+ "reached. Then indexes the documents.";
		
		String seedUrl = null;
		int maxDepth = 2;
		int maxPages = 500;
		
		for (int i=0; i < args.length; i++) {
			if ("-url".equals(args[i])) {
				seedUrl = args[i+1];
				i++;
			} else if ("-depth".equals(args[i])) {
				maxDepth = Integer.parseInt(args[i+1]);
				i++;
			} else if ("-pages".equals(args[i])) {
				maxPages = Integer.parseInt(args[i+1]);
				i++;
			}
		}
		
		if (seedUrl == null) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}
		
		/** Crawl the initial url wrt max depth and max pages values. **/
		Crawler crawler = new Crawler(seedUrl, maxDepth, maxPages);
		int collectionId = crawler.getCollectionId();
		crawler.collect();
		crawler.createLog();
		
		/** Index the extracted documents **/
		Indexer indexer = new Indexer(collectionId);
		indexer.index();
	}
}
