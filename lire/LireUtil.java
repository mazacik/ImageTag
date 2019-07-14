package lire;

import main.InstanceManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LireUtil {
	private static IndexReader ir;
	
	public static void index(String directory) throws IOException {
		File f = new File(directory);
		if (!f.exists() || !f.isDirectory()) {
			InstanceManager.getLogger().error(directory + " not a directory");
			return;
		}
		
		InstanceManager.getLogger().debug("Indexing images in " + directory);
		
		new LireIndexer(directory);
		
		InstanceManager.getLogger().debug("Finished indexing.");
	}
	
	public static void moveDuplicates(double similarity) throws IOException {
		if (ir == null) ir = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
		for (int i = 0; i < InstanceManager.getObjectListMain().size(); i++) {
			InstanceManager.getLogger().debug("Removing duplicates... " + i + "/" + InstanceManager.getObjectListMain().size());
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
					InstanceManager.getLogger().debug(fileNew.getName() + " is a duplicate of " + largestFile.getName());
					fileOld.renameTo(fileNew);
				}
			}
		}
	}
}

//LireUtil.index(FileUtil.getDirSource(), true);
//LireUtil.moveDuplicates(99);
//LireSearcher.search("C:\\Michal\\ImageTag\\duplicates\\24852607_1834985956512300_1641074986066574913_n.jpg", 100).forEach(s -> InstanceManager.getLogger().debug(s));
