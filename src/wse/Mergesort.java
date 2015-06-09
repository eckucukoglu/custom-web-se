package wse;

import org.apache.lucene.search.ScoreDoc;

public class Mergesort {
	private ScoreDoc[] scoredocs;
	private ScoreDoc[] helper;
	boolean sortDoc;
	
	private int number;
	
	public void sort(ScoreDoc[] scoreDocs, boolean sortwrtDoc) {
		this.scoredocs = scoreDocs;
		this.sortDoc = sortwrtDoc;
		number = scoreDocs.length;
		this.helper = new ScoreDoc[number];
		for (int i = 0; i < number; i++) {
			this.helper[i] = new ScoreDoc(-1,-1);
		}
		mergesort(0, number - 1);
	}
	
	private void mergesort(int low, int high) {
		if (low < high) {
			int middle = low + (high - low) / 2;
			mergesort(low, middle);
			mergesort(middle + 1, high);
			merge(low, middle, high);
		}
	}
	
	private void merge(int low, int middle, int high) {
		
		for (int i = low; i <= high; i++) {
			helper[i].doc = scoredocs[i].doc;
			helper[i].score = scoredocs[i].score;
		}
		
		int i = low;
		int j = middle + 1;
		int k = low;

		while (i <= middle && j <= high) {
			if (this.sortDoc) {
				if (helper[i].doc <= helper[j].doc) {
					scoredocs[k].doc = helper[i].doc;
					scoredocs[k].score = helper[i].score;
					i++;
				} else {
					scoredocs[k].doc = helper[j].doc;
					scoredocs[k].score = helper[j].score;
					j++;
				}
			} else {
				if (helper[i].score >= helper[j].score) {
					scoredocs[k].doc = helper[i].doc;
					scoredocs[k].score = helper[i].score;
					i++;
				} else {
					scoredocs[k].doc = helper[j].doc;
					scoredocs[k].score = helper[j].score;
					j++;
				}
			}
			k++;
		}

		while (i <= middle) {
			scoredocs[k].doc = helper[i].doc;
			scoredocs[k].score = helper[i].score;
			k++;
			i++;
		}
	}
}