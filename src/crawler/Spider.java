package crawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// TODO: javadoc comments

// TODO: print debug info
public class Spider {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<Url> links = new LinkedList<Url>();
    private Document htmlDocument;

    
    public boolean crawl (Url url) {
        try {
            Connection connection = Jsoup.connect(url.getUrl()).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            
            // Check HTTP OK status code (200).
            if (connection.response().statusCode() == 200) {
                System.out.println("**Visiting** Received web page at " + url.getUrl() + "\t" + url.getDepth());
            } else {
            	// TODO: Exception in thread "main" java.lang.NullPointerException
            	System.out.println(">> HTTP status code: " + connection.response().statusCode());
            }
            
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            
            Elements linksOnPage = htmlDocument.select("a[href]");
            
            for (Element link : linksOnPage) {
            	if (link.attr("href").compareTo("") != 0 && 
            			link.attr("href").charAt(0) != '#' &&
            			link.absUrl("href").compareTo("") != 0) {
            		this.links.add(new Url(link.absUrl("href"), url.getDepth()+1));
            	} 
            }
            
            return true;
        } catch (IOException ioe) {
            
            return false;
        }
    }


    public boolean searchWord(String word) {
        if (this.htmlDocument == null)
            return false;
        
        Element docBody = this.htmlDocument.body();
        String bodyText;
        if (docBody != null) {
        	bodyText = docBody.text();
        	return bodyText.toLowerCase().contains(word.toLowerCase());
        } else
        	return false;
    }

    public List<Url> getLinks() {
        return this.links;
    }
}