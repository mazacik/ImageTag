package application.backend.base.loader;

import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.base.entity.EntityListMain;
import application.backend.util.FileUtil;
import application.frontend.stage.StageManager;
import application.main.InstanceCollector;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class LoaderThread extends Thread implements InstanceCollector {
	public void run() {
		ArrayList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getDirSource());
		FileUtil.fixDuplicateFileNames(fileList);
		
		boolean readSuccess = entityListMain.addAll(EntityListMain.readFromDisk());
		if (!readSuccess || !isDatabaseOk()) {
			createBackup();
			createDatabase(fileList);
		}
		
		checkFileDifference(fileList);
		FileUtil.initDataObjectPaths(fileList);
		entityListMain.sort();
		tagListMain.initialize();
		entityListMain.writeToDisk();
		tagListMain.writeDummyToDisk();
		filter.refresh();
		target.set(filter.get(0));
		select.set(filter.get(0));
		
		Platform.runLater(() -> {
			filterPane.reload();
			filterPane.collapseAll();
			
			selectPane.reload();
			selectPane.collapseAll();
			
			toolbarPane.reload();
			
			reload.doReload();
			galleryPane.updateViewportTilesVisibility();
		});
	}
	
	private boolean isDatabaseOk() {
		for (Entity entity : entityListMain) {
			if (entity.getName() == null || entity.getTagList() == null) {
				//the database is (most likely) corrupted or outdated
				AtomicBoolean createNew = new AtomicBoolean(false);
				FutureTask futureTask = new FutureTask<Boolean>(() -> {
					Logger.getGlobal().info("Database failed to load.");
					createNew.set(StageManager.getOkCancelStage()._show("Database failed to load.\nCreate a new application.database?\nA backup will be created."));
				}, null);
				Platform.runLater(futureTask);
				try {
					futureTask.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				if (createNew.get()) {
					return false;
				} else {
					System.exit(1);
				}
			}
		}
		return true;
	}
	
	private void createBackup() {
		new File(FileUtil.getFileData()).renameTo(new File(FileUtil.getFileData() + "_backup"));
	}
	
	private void createDatabase(ArrayList<File> fileList) {
		EntityListMain objectListMain = entityListMain;
		objectListMain.clear();
		for (File file : fileList) {
			objectListMain.add(new Entity(file));
		}
	}
	
	private void checkFileDifference(ArrayList<File> fileList) {
		EntityList entityList = entityListMain;
		entityList.sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		ArrayList<Entity> orphanObjects = new ArrayList<>(entityList);
		ArrayList<File> newFiles = new ArrayList<>(fileList);
		/* compare files in the working directory with knwon objects in the application.database */
		for (Entity entity : entityList) {
			for (int j = 0; j < newFiles.size(); j++) {
				File file = newFiles.get(j);
				if (entity.getName().equals(file.getName())) {
					orphanObjects.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File file : new ArrayList<>(newFiles)) {
			for (Entity entity : new ArrayList<>(orphanObjects)) {
				if (file.length() == entity.getLength()) {
					newFiles.remove(file);
					orphanObjects.remove(entity);
					
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
			entityList.add(entity);
			filter.getCurrentSessionObjects().add(entity);
		}
		/* discard orphan objects */
		for (Entity entity : orphanObjects) {
			entityList.remove(entity);
		}
		entityList.sort(Comparator.comparing(Entity::getName));
	}
}
