package crawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;

    
    public boolean crawl (String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            
            // Check HTTP OK status code (200).
            if (connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            
            for (Element link : linksOnPage) {
            	if (link.attr("href").compareTo("") != 0 && 
            			link.attr("href").charAt(0) != '#' &&
            			link.absUrl("href").compareTo("") != 0) {
            		this.links.add(link.absUrl("href"));
            	} 
            	
            }
            
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }


    public boolean searchForWord(String searchWord) {
        // Defensive coding. This method should only be used after a successful crawl.
        if (this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        
        System.out.println("Searching for the word " + searchWord + "...");
        
        Element docBody = this.htmlDocument.body();
        String bodyText;
        if (docBody != null) {
        	bodyText = docBody.text();
        	return bodyText.toLowerCase().contains(searchWord.toLowerCase());
        } else
        	return false;
    }

    public List<String> getLinks() {
        return this.links;
    }
}