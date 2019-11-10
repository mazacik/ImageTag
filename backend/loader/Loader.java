package application.backend.loader;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.base.entity.EntityListMain;
import application.backend.loader.cache.CacheWriter;
import application.backend.util.FileUtil;
import application.frontend.stage.StageManager;
import application.frontend.stage.template.YesNoCancelStage;
import application.main.InstanceCollector;

import java.io.File;
import java.util.Comparator;

public abstract class Loader implements InstanceCollector {
	public static void run() {
		CustomList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getProjectDirSource());
		FileUtil.fixDuplicateFileNames(fileList);
		
		boolean readSuccess = entityListMain.addAll(EntityListMain.readFromDisk());
		if (!readSuccess) {
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
		
		CacheWriter.writeAllCacheInBackground();
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
		
		/* compare files in the working directory with known objects in the database */
		for (Entity entity : entityListMain) {
			for (int i = 0; i < newFiles.size(); i++) {
				File file = newFiles.get(i);
				if (entity.getName().equals(file.getName())) {
					orphanEntities.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File newFile : new CustomList<>(newFiles)) {
			Entity newFileEntity = new Entity(newFile);
			CacheWriter.write(newFileEntity);
			
			for (Entity orphanEntity : new CustomList<>(orphanEntities)) {
				if (newFile.length() == orphanEntity.getLength()) {
					if (StageManager.getCacheCompareStage().show(orphanEntity.getName(), newFileEntity.getName(), FileUtil.getCacheFilePath(orphanEntity), FileUtil.getCacheFilePath(newFileEntity)).equals(YesNoCancelStage.Result.YES)) {
						newFiles.remove(newFile);
						orphanEntities.remove(orphanEntity);
						
						/* rename the object and cache file */
						File oldCacheFile = new File(FileUtil.getCacheFilePath(orphanEntity));
						orphanEntity.setName(newFile.getName());
						File newCacheFile = new File(FileUtil.getCacheFilePath(orphanEntity));
						
						if (oldCacheFile.exists() && !newCacheFile.exists()) {
							oldCacheFile.renameTo(newCacheFile);
						}
					} else {
						break;
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
