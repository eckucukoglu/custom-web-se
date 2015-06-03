package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Spider crawl the given url address,
 * list its outgoing addresses.
 * 
 * @author EmreCan
 *
 */
public class Spider {
	private static final String USER_AGENT =
	"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<Url> links = new LinkedList<Url>();
	private Document htmlDocument;
	//private Document myDoc;
	
	/**
	 * This method gets html content of a given url,
	 * fetchs its outgoing links and stores them.
	 *  
	 * @param url crawl this url
	 * @return true if done, false otherwise.
	 */
	public boolean crawl (Url url) {
		try {
			Connection connection = Jsoup.connect(url.getUrl()).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;
			
			// TODO: fix character encoding problem
			//this.myDoc = Jsoup.parse(new URL(url.getUrl()).openStream(), "ISO-8859-9", url.getUrl());
			
			// Check HTTP OK status code (200).
			if (connection.response().statusCode() == 200) {
				if (InitCrawler.DEBUGMODE) System.out.println("[I]Received web page\t" + url.getUrl() + "\t" + url.getDepth());
			} else {
				// TODO: Exception in thread "main" java.lang.NullPointerException
				if (InitCrawler.DEBUGMODE) System.out.println("[E]HTTP status code: " + connection.response().statusCode());
			}
			
			if (!connection.response().contentType().contains("text/html")) {
				if (InitCrawler.DEBUGMODE) System.out.println("[E]Retrieved something other than HTML");
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
	
	/**
	 * Saves the crawled url's body content.
	 * 
	 * @param saveLocation
	 */
	public void saveHtml(String saveLocation) {
		try {
			FileWriter fileWriter = new FileWriter(saveLocation);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(this.htmlDocument.body().html());
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Search for word in the body content of a given url.
	 * @param word
	 * @return true if found, false otherwise.
	 */
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
	
	/**
	 * Getter for outgoing links.
	 * @return
	 */
	public List<Url> getLinks() {
		return this.links;
	}
}