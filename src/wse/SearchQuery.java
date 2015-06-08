package wse;

import searcher.SearchFiles;

/**
 * For a given query string, search the
 * collection and print out at most K documents
 * results.
 * 
 * ScoreDoc[] hits = searcher.getDocs().scoreDocs;
 */
public class SearchQuery {

	public static void main(String[] args) {
		String usage = "java wse.SearchQuery"
				+ " [-cid COLLECTION_ID] [-query QUERY_STRING] [-k TOP_K]\n\n"
				+ "This searchs the query in the given collection "
				+ "and print out top-k documents.\nDefault values for "
				+ "K = 10.";
			
		String queryString = null;
		int collectionId = -1;
		int K = 10;
		
		for (int i=0; i < args.length; i++) {
			if ("-cid".equals(args[i])) {
				collectionId = Integer.parseInt(args[i+1]);
				i++;
			} else if ("-query".equals(args[i])) {
				queryString = args[i+1];
				i++;
			} else if ("-k".equals(args[i])) {
				K = Integer.parseInt(args[i+1]);
				i++;
			} else {
				queryString = queryString.concat(" " + args[i]);
			}
		}
		
		if (queryString == null || collectionId == -1) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}
		
		/** Search the given query. **/
		SearchFiles searcher = new SearchFiles(collectionId);
		try {
			searcher.search(queryString, K);
			String[] documentNames = searcher.getNames(searcher.getDocs());
			
			for (int i = 0; i < documentNames.length; i++)
				System.out.println(documentNames[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
