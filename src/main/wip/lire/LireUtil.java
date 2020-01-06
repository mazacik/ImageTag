package wip.lire;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LireUtil implements InstanceCollector {
	private static IndexReader ir;
	
	public static void index(String directory) {
		File f = new File(directory);
		if (!f.exists() || !f.isDirectory()) {
			Logger.getGlobal().warning(directory + " not a directory");
			return;
		}
		
		Logger.getGlobal().info("Indexing images in " + directory);
		
		new LireIndexer(directory);
		
		Logger.getGlobal().info("Finished indexing.");
	}
	
	public static void moveDuplicates(double similarity) throws IOException {
		if (ir == null) ir = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
		for (int i = 0; i < entityListMain.size(); i++) {
			Logger.getGlobal().info("Removing duplicates... " + i + "/" + entityListMain.size());
			Document document = ir.document(i);
			ArrayList<String> matches = null;
			try {
				matches = LireSearcher.search(document, similarity);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (matches != null) {
				File largestFile = new File(matches.get(0));
				ArrayList<File> filesToMove = new ArrayList<>();
				for (String match : matches) {
					if (!largestFile.getAbsolutePath().equals(match)) {
						File file = new File(match);
						if (file.length() > largestFile.length()) {
							filesToMove.add(largestFile);
							largestFile = file;
						} else {
							filesToMove.add(file);
						}
					}
				}
				for (File fileOld : filesToMove) {
					File fileNew = new File("C:\\Michal\\ImageTag\\duplicates\\" + fileOld.getName());
					Logger.getGlobal().info(fileNew.getName() + " is a duplicate of " + largestFile.getName());
					fileOld.renameTo(fileNew);
				}
			}
		}
	}
}

//LireUtil.index(FileUtil.getDirSource(), true);
//LireUtil.moveDuplicates(99);
//LireSearcher.search("C:\\Michal\\ImageTag\\duplicates\\24852607_1834985956512300_1641074986066574913_n.jpg", 100).forEach(s -> InstanceManager.getLogger().debug(s));
