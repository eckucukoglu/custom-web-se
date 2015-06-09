package wse;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

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
				+ " [-cid COLLECTION_ID] [-query QUERY_STRING] [-k TOP_K] [-w]\n\n"
				+ "This searchs the query in the given collection "
				+ "and print out top-k documents. If selected WAND optimization is used."
				+ "\nDefault values for K = 10.";
			
		String queryString = null;
		int collectionId = -1;
		int K = 10;
		boolean WAND = false;
		
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
			} else if ("-w".equals(args[i])) {
				WAND = true;
			} else {
				queryString = queryString.concat(" " + args[i]);
			}
		}
		
		if (queryString == null || collectionId == -1) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}
		
		if (!WAND) {
			/** Search the given query without WAND optimization. **/
			SearchFiles searcher = new SearchFiles(collectionId);
			try {
				searcher.search(queryString, K);
				String[] documentNames = searcher.getNames(searcher.getDocs());
				
				for (int i = 0; i < documentNames.length; i++)
					System.out.println(documentNames[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			/** Use WAND optimization. **/
			String[] queries = queryString.split(" ");
			ArrayList<TopDocs> docsList = new ArrayList<TopDocs>();
			SearchFiles searcher = new SearchFiles(collectionId);
			
			for (int i = 0; i < queries.length; i++) {
				try {
					searcher.search(queries[i], K*K);
					docsList.add(searcher.getDocs());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String[] documentNames = null;
			try {
				documentNames = searcher.getNames(wand(docsList, K));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < documentNames.length; i++)
				System.out.println(documentNames[i]);
		}
		
	}
	
	public static TopDocs wand (ArrayList<TopDocs> docsList, int K) {
		Mergesort sorter = new Mergesort();
		int[] docidIndexes = new int[docsList.size()];
		int docId;
		double documentScore;
		double scoreThreshold = 0;
		ArrayList<ScoreDoc> resultDocs = new ArrayList<ScoreDoc>();
		
		for (int i = 0; i < docsList.size(); i++) {
			sorter.sort(docsList.get(i).scoreDocs, true);
			docidIndexes[i] =  docsList.get(i).scoreDocs[0].doc;
		}
		
		do {
			docId = docidIndexes[findMin(docidIndexes)];
			
			if (docId < 0)
				break;
			documentScore = computeWANDscore(docId, docsList);
			
			if (scoreThreshold < documentScore) {
				ScoreDoc hitdoc = new ScoreDoc(docId, (float) documentScore);
				resultDocs.add(hitdoc);
				
				scoreThreshold = getNewThreshold(resultDocs, K);
			}
			
			docidIndexes = assignNextDocIds(docsList, docidIndexes);
		} while (checkDocIndexes(docidIndexes));
		
		ScoreDoc[] resultDocsArray = new ScoreDoc[resultDocs.size()];
		for (int i = 0; i < resultDocs.size(); i++) {
			resultDocsArray[i] = resultDocs.get(i);
		}
		
		sorter.sort(resultDocsArray, false);
		
		return (new TopDocs(K, resultDocsArray, 0));
	}
	
	

	private static boolean checkDocIndexes(int[] array) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != -1)
				return true;
		}
		
		return false;
	}

	private static int[] assignNextDocIds(ArrayList<TopDocs> docsList,
			int[] docidIndexes) {
		
		int[] retVal = new int[docidIndexes.length];
		int docId = docidIndexes[findMin(docidIndexes)];
		
		for (int i = 0; i < docsList.size(); ++i) {
			retVal[i] = -1;
			for (int j = 0; j < docsList.get(i).scoreDocs.length; j++) {
				if (docsList.get(i).scoreDocs[j].doc > docId) {
					retVal[i] = docsList.get(i).scoreDocs[j].doc;
					break;
				} else {
					continue;
				}
			}
			
		}
		
		return retVal;
		
	}

	private static double getNewThreshold(ArrayList<ScoreDoc> resultDocs, int k) {
		if (resultDocs.size() < k)
			return 0.0;
		ScoreDoc[] temp = new ScoreDoc[resultDocs.size()];
		
		for (int i = 0; i < resultDocs.size(); i++) {
			temp[i] = new ScoreDoc(resultDocs.get(i).doc, resultDocs.get(i).score);
		}
		
		Mergesort sorter = new Mergesort();
		sorter.sort(temp, false);
		
		return temp[k-1].score;
	}

	private static double computeWANDscore(int docId, ArrayList<TopDocs> docsList) {
		double retVal = 0.0;
		
		for (int i = 0; i < docsList.size(); ++i) {
			int index = searchDocId(docsList.get(i).scoreDocs, docId);
			if (index < 0)
				continue;
			
			retVal = retVal + (docsList.get(i).scoreDocs[index].score);
		}
		
		return retVal;
	}
	
	private static int searchDocId(ScoreDoc[] scoreDocs, int docId) {
		for (int i = 0; i < scoreDocs.length; i++) {
			if (scoreDocs[i].doc == docId)
				return i;
		}
		
		return (-1);
	}

	/**
	 * Find min value in the array and return its index.
	 * Discard negative values.
	 * 
	 * @param array
	 * @return index of minimum element
	 */
	static int findMin (int[] array) {
		int index = 0;
		int min = 223605000;
		
		for (int i = 0; i < array.length; ++i) {
			if (min > array[i] && array[i] > 0) {
				min = array[i];
				index = i;
			}
		}
		
		return index;
	}
	
}
