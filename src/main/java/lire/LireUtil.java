package lire;

import javafx.scene.image.Image;
import misc.Project;
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
	
	public static void index() {
		index(Project.getCurrent().getDirectorySource());
	}
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
			ArrayList<String> processed = new ArrayList<>();
			
			IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(LIRE_INDEX_PATH)));
			for (int i = 0; i < ir.maxDoc(); i++) {
				Document document = ir.document(i);
				ArrayList<String> matches = null;
				
				try {
					matches = LireSearcher.search(document, similarity);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				matches.removeAll(processed);
				processed.addAll(matches);
				
				echoBatch(matches);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void echoBatch(ArrayList<String> batch) {
		if (!batch.isEmpty()) {
			String best = getBestOfBatch(batch);
			System.out.println(best);
		}
	}
	private static String getBestOfBatch(ArrayList<String> batch) {
		final String[] bestString = {""};
		final double[] bestSize = {-1};
		batch.forEach(path -> {
			Image image = new Image("file:" + path);
			double width = image.getWidth();
			double hegiht = image.getHeight();
			double size = width * hegiht;
			if (size > bestSize[0]) {
				bestString[0] = path;
				bestSize[0] = size;
			}
		});
		return bestString[0];
	}
}
