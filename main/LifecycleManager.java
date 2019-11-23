package main;

import baseobject.CustomList;
import baseobject.Project;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import gui.main.center.GalleryTile;
import gui.stage.StageManager;
import tools.CacheManager;
import tools.EntityGroupUtil;
import tools.FileUtil;

import java.io.File;
import java.util.Comparator;
import java.util.logging.Logger;

public abstract class LifecycleManager implements InstanceCollector {
	public static void startApplication() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
		
		settings.readFromDisk();
		
		toolbarPane.init();     /* needs Settings */
		galleryPane.init();     /* needs Settings */
		mediaPane.init();       /* needs Settings, GalleryPane */
		filterPane.init();      /* needs Settings */
		selectPane.init();      /* needs Settings */
		
		GalleryTile.init();
		
		filter.init();
		target.init();
		select.init();
		reload.init();          /* needs everything */
		
		StageManager.getMainStage().init();
	}
	
	public static void startDatabaseLoading(Project project) {
		StageManager.getMainStage().setVisible(false);
		StageManager.getMainStage().setStagePropertiesMain();
		StageManager.getMainStage().showMainScene();
		
		String projectFilePath = project.getProjectFileFullPath();
		String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
		String sourceDirectory = project.getSourceDirectory();
		
		FileUtil.init(projectDirectory, sourceDirectory);
		
		CustomList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getSourceDirectoryPath());
		
		entityListMain.readFromDisk();
		if (entityListMain.isEmpty()) {
			for (File file : fileList) {
				entityListMain.add(new Entity(file));
			}
		}
		
		checkFileDifference(fileList);
		
		EntityGroupUtil.initGroups();
		entityListMain.sort();
		tagListMain.initialize();
		entityListMain.writeToDisk();
		tagListMain.writeToDisk();
		filter.refresh();
		target.set(filter.get(0));
		select.set(filter.get(0));
		
		reload.doReload();
		
		filterPane.collapseAll();
		selectPane.collapseAll();
		
		StageManager.getMainStage().setVisible(true);
		
		CacheManager.createCacheInBackground();
	}
	private static void checkFileDifference(CustomList<File> fileList) {
		entityListMain.sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		EntityList orphanEntities = new EntityList(entityListMain);
		CustomList<File> newFiles = new CustomList<>(fileList);
		
		/* compare files in the source directory with known objects in the database */
		for (Entity entity : entityListMain) {
			for (int i = 0; i < newFiles.size(); i++) {
				File file = newFiles.get(i);
				if (entity.getName().equals(FileUtil.getNameForEntity(file))) {
					orphanEntities.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File newFile : new CustomList<>(newFiles)) {
			Entity newFileEntity = new Entity(newFile);
			CacheManager.create(newFileEntity);
			
			for (Entity orphanEntity : new CustomList<>(orphanEntities)) {
				if (newFile.length() == orphanEntity.getLength()) {
					newFiles.remove(newFile);
					orphanEntities.remove(orphanEntity);
					
					/* rename the object and cache file */
					File oldCacheFile = new File(FileUtil.getCacheFilePath(orphanEntity));
					orphanEntity.setName(newFile.getName());
					File newCacheFile = new File(FileUtil.getCacheFilePath(orphanEntity));
					
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
	
	public static void exitApplication() {
		Logger.getGlobal().info("Application Exit");
		
		CacheManager.stopThread();
		
		mediaPane.disposeVideoPlayer();
		mediaPane.getControls().hide();
		
		entityListMain.writeToDisk();
		tagListMain.writeToDisk();
		settings.writeToDisk();
	}
}
