package indexer;

import java.nio.file.Path;
import java.nio.file.Paths;

import utils.Enums;

/**
 * Calls Lucene indexer to index collection.
 * Index files are stored in index path to search later.
 */
public class Indexer {
	
	private int collectionId;
	
	public Indexer (int colId) {
		this.collectionId = colId;
	}
	
	/**
	 * Index all documents in the collection folder.
	 */
	public void index() {
		
		Path path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.COLLECTIONS_DIR, Integer.toString(this.collectionId));
		String docsPath = path.toString();

		path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.INDEXES_DIR, Integer.toString(this.collectionId));
		Enums.createDirectory(path);
		String indexPath = path.toString();
		
		IndexFiles indexFile = new IndexFiles();
		indexFile.index(docsPath, indexPath);
	}
}