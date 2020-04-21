package frontend.stage.fileimport;

import backend.cache.CacheLoader;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.misc.FileUtil;
import backend.misc.Project;
import backend.override.Thread;
import javafx.application.Platform;
import main.Root;

import java.io.File;
import java.util.logging.Logger;

public abstract class ImportUtil {
	public static void startImportThread(String directoryPath, BaseList<File> filesToImport) {
		new Thread(() -> {
			EntityList entityList = ImportUtil.importFiles(directoryPath, filesToImport);
			
			if (entityList != null && !entityList.isEmpty()) {
				CacheLoader.startCacheThread(entityList);
				
				Root.ENTITYLIST.addAllImpl(entityList);
				Root.ENTITYLIST.sort();
				
				Root.FILTER.getLastImport().setAllImpl(entityList);
			}
			
			Platform.runLater(() -> ImportStage.displayResults(entityList));
		}).start();
	}
	
	private static EntityList importFiles(String directoryPath, BaseList<File> filesToImport) {
		Root.PSC.MAIN_STAGE.showLoadingBar(filesToImport, filesToImport.size());
		
		EntityList entityList = new EntityList();
		for (File file : filesToImport) {
			if (Thread.currentThread().isInterrupted()) return null;
			entityList.addImpl(ImportUtil.importFile(directoryPath, file));
			Root.PSC.MAIN_STAGE.advanceLoadingBar(filesToImport);
		}
		
		Root.PSC.MAIN_STAGE.hideLoadingBar(filesToImport);
		
		return entityList;
	}
	private static Entity importFile(String directoryPath, File file) {
		String pathOld = file.getAbsolutePath();
		String pathNew = Project.getCurrent().getDirectorySource() + File.separator + FileUtil.createEntityName(file, directoryPath);
		
		File fileNew = new File(pathNew);
		if (!fileNew.exists()) {
			//todo respect ImportMode
			FileUtil.moveFile(pathOld, pathNew);
			return new Entity(fileNew);
		} else {
			//todo rename the new file if they are not identical, don't import if they are identical
			Logger.getGlobal().info("IMPORT: COULD NOT IMPORT \"" + file.getName() + "\", ALREADY EXISTS");
			return null;
		}
	}
}
