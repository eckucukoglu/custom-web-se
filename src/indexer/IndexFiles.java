package indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.Enums;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class IndexFiles {
	
	/**
	 * For a given collection, does indexing and stores
	 * in the index path.
	 * 
	 * @param docsPath documents' directory path
	 * @param indexPath index directory path
	 */
	public void index (String docsPath, String indexPath) {
		
		final Path docDir = Paths.get(docsPath);
		
		if (!Files.isReadable(docDir)) {
			if (Enums.DEBUGMODE) System.out.println("[E]Document directory '" + 
					docDir.toAbsolutePath() + "' does not exist or is not readable, "
							+ "please check the path");
			System.exit(1);
		}
		
		Date start = new Date();
		try {
			
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			
			iwc.setOpenMode(OpenMode.CREATE);
			
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			
			writer.close();
			
			Date end = new Date();
			if (Enums.DEBUGMODE) System.out.println("[I]" + (end.getTime() - start.getTime()) + " total milliseconds");
			
		} catch (IOException e) {
			if (Enums.DEBUGMODE) System.out.println("[E]Caught a " + e.getClass() +
			"\n with message: " + e.getMessage());
		}
	}
	
	/**
	 * Indexes documents that in the given path.
	 * 
	 * @param writer
	 * @param path
	 * @throws IOException
	 */
	static void indexDocs (final IndexWriter writer, Path path) throws IOException {
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try {
					indexDoc(writer, file);
				} catch (IOException ignore) {
					// don't index files that can't be read.
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	/**
	 * Indexes the given file.
	 * 
	 * @param writer
	 * @param file file path
	 * @throws IOException
	 */
	static void indexDoc (IndexWriter writer, Path file) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {

			Document doc = new Document();
			Field pathField = new StringField(Enums.FIELD_NAME, 
					file.getFileName().toString(), Field.Store.YES);
			
			doc.add(pathField);
			doc.add(new TextField(Enums.FIELD_CONTENT, 
					new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			
			if (Enums.DEBUGMODE) System.out.println("[I]Adding document\t" + file.getFileName());
			writer.addDocument(doc);
		}
	}
}
