package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.Enums;

// TODO: href=mailto:@ must be excluded.
// TODO: http://add.com and http://add.com/ must be considered as same page.
// TODO: fix character encoding problem

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
			
			// Check HTTP OK status code (200).
			if (connection.response().statusCode() == 200) {
				if (Enums.DEBUGMODE) System.out.println("[I]Received web page\t" + url.getUrl() + "\t" + url.getDepth());
			} else {
				// TODO: Exception in thread "main" java.lang.NullPointerException
				if (Enums.DEBUGMODE) System.out.println("[E]HTTP status code: " + connection.response().statusCode());
			}
			
			if (!connection.response().contentType().contains("text/html")) {
				if (Enums.DEBUGMODE) System.out.println("[E]Retrieved something other than HTML");
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
			if (this.htmlDocument != null && this.htmlDocument.body() != null) { 
				FileWriter fileWriter = new FileWriter(saveLocation);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(this.htmlDocument.text());
				bufferedWriter.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter for outgoing links.
	 * @return
	 */
	public List<Url> getLinks() {
		return this.links;
	}

	/**
	 * Append the map file a 
	 * document name - url pairs.
	 * 
	 * @param mapFilePath map file path
	 * @param docId document name or id
	 * @param currentUrl url
	 */
	public void appendToMap(String mapFilePath, int docId, Url currentUrl) {
		
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(mapFilePath, true)))) {
		    out.println(docId + "\t" + currentUrl.getUrl());
		} catch (IOException e) {
			if (Enums.DEBUGMODE) System.out.println("[E]Can't append to " + mapFilePath);
		}
		
	}
}