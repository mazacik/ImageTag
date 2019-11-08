package application.backend.loader;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.base.entity.EntityListMain;
import application.backend.util.FileUtil;
import application.frontend.stage.StageManager;
import application.main.InstanceCollector;

import java.io.File;
import java.util.Comparator;
import java.util.logging.Logger;

public abstract class Loader implements InstanceCollector {
	public static void run() {
		CustomList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getProjectDirSource());
		FileUtil.fixDuplicateFileNames(fileList);
		
		boolean readSuccess = entityListMain.addAll(EntityListMain.readFromDisk());
		if (!readSuccess || !isDatabaseOk()) {
			createBackup();
			createDatabase(fileList);
		}
		
		checkFileDifference(fileList);
		FileUtil.initEntityPaths(fileList);
		entityListMain.sort();
		tagListMain.initialize();
		entityListMain.writeToDisk();
		tagListMain.writeDummyToDisk();
		filter.refresh();
		target.set(filter.get(0));
		select.set(filter.get(0));
		
		reload.doReload();
		
		filterPane.collapseAll();
		selectPane.collapseAll();
		
		galleryPane.updateViewportTilesVisibility();
		
		new BackgroundCacheLoader().start();
	}
	
	private static boolean isDatabaseOk() {
		//todo check, rework, refactor, whatever
		for (Entity entity : entityListMain) {
			if (entity.getName() == null || entity.getTagList() == null) {
				//the database is (most likely) corrupted or outdated
				Logger.getGlobal().info("Database failed to load.");
				if (StageManager.getOkCancelStage().show("Database failed to load.\nCreate a new application.database?\nA backup will be created.")) {
					return false;
				} else {
					System.exit(1);
				}
			}
		}
		return true;
	}
	
	private static void createBackup() {
		new File(FileUtil.getProjectFileData()).renameTo(new File(FileUtil.getProjectFileData() + "_backup"));
	}
	
	private static void createDatabase(CustomList<File> fileList) {
		entityListMain.clear();
		for (File file : fileList) {
			entityListMain.add(new Entity(file));
		}
	}
	
	private static void checkFileDifference(CustomList<File> fileList) {
		entityListMain.sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		EntityList orphanEntities = new EntityList(entityListMain);
		CustomList<File> newFiles = new CustomList<>(fileList);
		/* compare files in the working directory with knwon objects in the application.database */
		for (Entity entity : entityListMain) {
			for (int j = 0; j < newFiles.size(); j++) {
				File file = newFiles.get(j);
				if (entity.getName().equals(file.getName())) {
					orphanEntities.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File file : new CustomList<>(newFiles)) {
			for (Entity entity : new CustomList<>(orphanEntities)) {
				if (file.length() == entity.getLength()) {
					newFiles.remove(file);
					orphanEntities.remove(entity);
					
					/* rename the object and cache file */
					File oldCacheFile = new File(FileUtil.getCacheFilePath(entity));
					entity.setName(file.getName());
					File newCacheFile = new File(FileUtil.getCacheFilePath(entity));
					
					if (oldCacheFile.exists() && !newCacheFile.exists()) {
						oldCacheFile.renameTo(newCacheFile);
					}
				}
			}
		}
		/* add unrecognized objects */
		for (File file : newFiles) {
			Entity entity = new Entity(file);
			entityListMain.add(entity);
			filter.getCurrentSessionEntities().add(entity);
		}
		/* discard orphan objects */
		for (Entity entity : orphanEntities) {
			entityListMain.remove(entity);
		}
		entityListMain.sort(Comparator.comparing(Entity::getName));
	}
}
