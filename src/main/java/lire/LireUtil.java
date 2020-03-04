package lire;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LireUtil {
	public static final String LIRE_INDEX_PATH = "lire_index";
	
	public static void index(String directory) {
		File f = new File(directory);
		
		if (f.isDirectory() && f.isDirectory()) {
			Logger.getGlobal().info("Indexing images in " + directory);
			
			new LireIndexer(new File(directory));
			
			Logger.getGlobal().info("Finished indexing.");
		} else {
			Logger.getGlobal().warning(directory + " not a directory");
		}
	}
	public static void echo(double similarity) {
		try {
			IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(LIRE_INDEX_PATH)));
			for (int i = 0; i < ir.maxDoc(); i++) {
				Document document = ir.document(i);
				ArrayList<String> matches = null;
				try {
					matches = LireSearcher.search(document, similarity);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println("--------------------");
				for (int j = 0; j < matches.size(); j++) {
					System.out.println(j + ") " + matches.get(j));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
