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
	public static void startImportThread(ImportMode importMode, String directory, BaseList<File> fileList) {
		new Thread(() -> {
			EntityList entityList = ImportUtil.importFiles(importMode, directory, fileList);
			
			if (entityList != null && !entityList.isEmpty()) {
				CacheLoader.startCacheThread(entityList);
				
				Root.ENTITYLIST.addAllImpl(entityList);
				Root.ENTITYLIST.sort();
				
				Root.FILTER.getLastImport().setAllImpl(entityList);
			}
			
			Platform.runLater(() -> ImportStage.displayResults(entityList));
		}).start();
	}
	
	private static EntityList importFiles(ImportMode importMode, String directory, BaseList<File> fileList) {
		Root.PSC.MAIN_STAGE.showLoadingBar(Thread.currentThread(), fileList.size());
		
		EntityList entityList = new EntityList();
		for (File fileFrom : fileList) {
			if (Thread.currentThread().isInterrupted()) return null;
			
			Entity entity = ImportUtil.importFile(importMode, directory, fileFrom);
			if (entity != null) {
				entityList.addImpl(entity);
			}
			
			Root.PSC.MAIN_STAGE.advanceLoadingBar(Thread.currentThread());
		}
		
		Root.PSC.MAIN_STAGE.hideLoadingBar(Thread.currentThread());
		
		return entityList;
	}
	private static Entity importFile(ImportMode importMode, String directory, File fileFrom) {
		String pathFrom = fileFrom.getAbsolutePath();
		String pathTo = Project.getCurrent().getDirectorySource() + File.separator + FileUtil.createEntityName(fileFrom, directory);
		
		File fileTo = new File(pathTo);
		
		if (!fileTo.exists()) {
			if (ImportUtil.doImport(importMode, pathFrom, pathTo)) {
				return new Entity(fileTo);
			}
		} else {
			if (fileTo.length() != fileFrom.length()) {
				//it's a different file with the same name, prepend it with it's size and import it
				pathTo = Project.getCurrent().getDirectorySource() + File.separator + fileFrom.length() + "-" + FileUtil.createEntityName(fileFrom, directory);
				if (ImportUtil.doImport(importMode, pathFrom, pathTo)) {
					return new Entity(new File(pathTo));
				}
			} else {
				Logger.getGlobal().info("IMPORT: Identical file already exists:\n" + fileFrom.getName());
			}
		}
		
		return null;
	}
	private static boolean doImport(ImportMode importMode, String pathFrom, String pathTo) {
		switch (importMode) {
			case COPY:
				return FileUtil.copyFile(pathFrom, pathTo);
			case MOVE:
				return FileUtil.moveFile(pathFrom, pathTo);
			default:
				return false;
		}
	}
}
