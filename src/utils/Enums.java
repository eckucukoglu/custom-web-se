package utils;

import java.io.File;
import java.nio.file.Path;

/**
 * Common constants for whole project.
 */
public class Enums {
	
	/** Print debug info **/
	public static final boolean DEBUGMODE = true;
	/** Main data directory **/
	public static final String DATA_DIR = "data";
	/** Crawler uses this directory to store documents **/
	public static final String COLLECTIONS_DIR = "collections";
	/** Indexes of collections are stored in it **/
	public static final String INDEXES_DIR = "indexes";
	/** Indexes of collections are stored in it **/
	public static final String LOGS_DIR = "logs";
	/** After crawling phase, log is stored with [collectionId]+this name **/
	public static final String LOG_FILE = ".crawl.log";
	/** Document filename - crawled url map file **/
	public static final String MAP_URL_FILE = ".url.map";
	/** Search will be executed on field of documents. **/
	public static final String FIELD_CONTENT = "contents";
	/** Document names are stored in the field **/
	public static final String FIELD_NAME = "name";

	
	/**
	 * Create directory for a given path.
	 * 
	 * @return true if success
	 */
	public static boolean createDirectory(Path path) {
		String pathStr = path.toString();
		File dir = new File(pathStr);
		
		if (!dir.exists()) {
			try {
				if (dir.mkdir()) {
					if (Enums.DEBUGMODE) System.out.println("[I]Created directory: " + pathStr);
					return true;
				} else {
					if (Enums.DEBUGMODE) System.out.println("[E]Directory could not create: " + pathStr);
					return false;
				}
			} catch (SecurityException se) {
				return false;
			}
		} else {
			if (Enums.DEBUGMODE) System.out.println("[E]Directory exists: " + pathStr);
			return false;
		}
	}
}
