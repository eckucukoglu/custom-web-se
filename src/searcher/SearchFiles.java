package searcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import utils.Enums;

/**
 * Searchs the given index for a query.
 * 
 * Example usage:
 * SearchFiles searchFile = new SearchFiles(indexPath);
 * TopDocs topDocs = searchFile.search(query, K);
 */
public class SearchFiles {
	
	private String indexDir;
	private TopDocs topDocs = null;
	
	/**
	 * Constructor for searcher. 
	 * 
	 * @param indexDirectory
	 */
	public SearchFiles (int collectionId) {
		Path path = Paths.get(System.getProperty("user.dir"), Enums.DATA_DIR, 
				Enums.INDEXES_DIR, Integer.toString(collectionId));
		String collectionPath = path.toString();
		
		this.indexDir = collectionPath;
	}
	
	/**
	 * Takes a query and how many documents wanted to retrieve.
	 * Sets documents for a given query.
	 * 
	 * @param index index directory path
	 * @param queries query list file path
	 * @param K
	 * @throws Exception
	 */
	public void search (String queryStr, int K) throws Exception {
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(this.indexDir)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser(Enums.FIELD_CONTENT, analyzer);
		
		if (queryStr == null || queryStr.length() == -1)
			return;
		
		queryStr = queryStr.trim();
		if (queryStr.length() == 0)
			return;
		
		Query query = parser.parse(queryStr);
		
		if (Enums.DEBUGMODE) System.out.println("[I]Searching for: " + query.toString(Enums.FIELD_CONTENT));
		
		this.topDocs = searcher.search(query, K);
		
		reader.close();
	}
	
	/**
	 * Getter for documents.
	 * 
	 * @return the topDocsList
	 */
	public TopDocs getDocs() {
		return topDocs;
	}
	
	/**
	 * Returns the names of the resultant documents.
	 * 
	 * Example usage:
	 * String[] names = searchFile.getNames(searchFile.getTopDocs());
	 * 
	 * @param results
	 * @return
	 * @throws IOException
	 */
	public String[] getNames (TopDocs results) throws IOException {
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(this.indexDir)));
		IndexSearcher searcher = new IndexSearcher(reader);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		int end = Math.min(numTotalHits, hits.length);
		String[] resultDocNames = new String[end];
		
		for (int i = 0; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String name = doc.get("name");
			resultDocNames[i] = name;
		}
		
		return resultDocNames;
	}
}
