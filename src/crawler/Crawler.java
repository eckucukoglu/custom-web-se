package crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author EmreCan
 *
 */
public class Crawler {
	// TODO: print debug info
	
	public static final String DATADIR = "data/collections";
	public static final String LOGFILE = "crawl.log";
	
	private Url initialUrl;
	private int maxDepth;
	private int maxPages;
	private int collectionId = 0;
	
    private Set<Url> pagesVisited = new HashSet<Url>();
    private List<Url> pagesToVisit = new LinkedList<Url>();
    
    Crawler (String url, int depth, int pages) {
    	this.initialUrl = new Url(url, 0);
    	this.maxDepth = depth;
    	this.maxPages = pages;
    	createDirectory();
    }
    
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
            if (currentUrl.getDepth() > this.maxDepth)
            	return;
            
            spider.crawl(currentUrl);
            this.pagesVisited.add(currentUrl);

            this.pagesToVisit.addAll(spider.getLinks());
        }
    }
    
    private boolean createDirectory() {
    	while (true) {
    		Path path = Paths.get(System.getProperty("user.dir"), DATADIR, Integer.toString(collectionId));
    		String pathStr = path.toString();
    		File dir = new File(pathStr);
    		
    		if (!dir.exists()) {
        	    try {
        	        dir.mkdir();
        	        if (InitCrawler.DEBUGMODE) System.out.println("Created directory: " + pathStr);
        	        return true;
        	    } catch (SecurityException se) {
        	        return false;
        	    }
        	} else {
        		(this.collectionId)++;
        	}
    	}
    } 
    
    public boolean createLog() {
    	Path path = Paths.get(System.getProperty("user.dir"), DATADIR, Integer.toString(collectionId));
    	String pathStr = path.toString();
		File dir = new File(pathStr);
		
		if (dir.exists()) {
			path = Paths.get(System.getProperty("user.dir"), DATADIR, Integer.toString(collectionId), LOGFILE);
	    	pathStr = path.toString();
			
			try {
				PrintWriter writer = new PrintWriter(pathStr, "UTF-8");
				
				writer.println("Creation date:\t" + new Date());
		    	writer.println("Seed Url:\t" + this.initialUrl.getUrl());
		    	writer.println("Max depth:\t" + this.maxDepth);
		    	writer.println("Max pages:\t" + this.maxPages);
		    	writer.println("Seeded pages:\t" + this.pagesVisited.size());
		    	writer.close();
		    	
		    	if (InitCrawler.DEBUGMODE) System.out.println("Created log: " + path.toString());
		    	
		    	return true;
			} catch (FileNotFoundException e) {
				return false;
			} catch (UnsupportedEncodingException e) {
				return false;
			}
	    	
    	} else {
    		if (InitCrawler.DEBUGMODE) System.out.println("Collection directory does not exist. Log file couldn't generated.");
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
        
        // TODO: check that Url.equals works correctly. 
        
        return nextUrl;
    }
}
