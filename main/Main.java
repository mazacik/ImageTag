package main;

import baseobject.CustomList;
import baseobject.Project;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.Tag;
import baseobject.tag.TagList;
import control.reload.ChangeIn;
import gui.main.center.GalleryTile;
import gui.stage.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import tools.CacheManager;
import tools.CollectionUtil;
import tools.FileUtil;

import java.io.File;
import java.util.Comparator;
import java.util.logging.Logger;

public class Main extends Application implements InstanceCollector {
	private static final boolean QUICKSTART = false;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		initLogger();
		initInstances();
		initLoading();
	}
	
	private static void initLogger() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
	}
	private static void initInstances() {
		settings.readFromDisk();
		
		paneToolbar.init();     /* needs Settings */
		paneGallery.init();     /* needs Settings */
		paneEntity.init();      /* needs Settings, GalleryPane */
		paneFilter.init();      /* needs Settings */
		paneSelect.init();      /* needs Settings */
		
		GalleryTile.init();
	}
	private static void initLoading() {
		CustomList<Project> projects = FileUtil.getProjects();
		
		if (!QUICKSTART || projects.isEmpty()) {
			StageManager.getStageMain().layoutIntro();
		} else {
			StageManager.getStageMain().layoutMain();
			
			projects.sort(Project.getComparator());
			startDatabaseLoading(projects.getFirst());
		}
	}
	
	public static void startDatabaseLoading(Project project) {
		Project.setCurrent(project);
		mainEntityList.addAll(project.getEntityList());
		checkForNewFiles(FileUtil.getSupportedFiles(new File(project.getDirectorySource())));
		
		CollectionUtil.init();
		mainEntityList.sort();
		initTags(project);
		filter.refresh();
		target.set(filter.get(0));
		select.set(filter.get(0));
		
		reload.doReload();
		
		paneFilter.collapseAll();
		paneSelect.collapseAll();
		
		CacheManager.createCacheInBackground(mainEntityList);
	}
	private static void initTags(Project project) {
		//todo help
		TagList allTags = project.getTagList();
		if (allTags != null) {
			mainTagList.addAll(allTags);
		}
		
		for (Entity entity : mainEntityList) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (mainTagList.containsEqualTo(tag)) {
					tagList.set(tagList.indexOf(tag), mainTagList.getTag(tag));
				} else {
					mainTagList.add(tag);
				}
			}
		}
		
		mainTagList.sort();
		reload.notify(ChangeIn.TAG_LIST_MAIN);
	}
	private static void checkForNewFiles(CustomList<File> fileList) {
		mainEntityList.sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		
		EntityList orphanEntities = new EntityList(mainEntityList);
		CustomList<File> newFiles = new CustomList<>(fileList);
		
		/* compare files in the source directory with known objects in the database */
		for (Entity entity : mainEntityList) {
			for (int i = 0; i < newFiles.size(); i++) {
				File file = newFiles.get(i);
				if (entity.getName().equals(FileUtil.createEntityName(file))) {
					orphanEntities.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File newFile : new CustomList<>(newFiles)) {
			for (Entity orphanEntity : new CustomList<>(orphanEntities)) {
				if (newFile.length() == orphanEntity.getLength()) {
					newFiles.remove(newFile);
					orphanEntities.remove(orphanEntity);
					
					/* rename the object and cache file */
					File oldCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					orphanEntity.setName(newFile.getName());
					File newCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					
					if (oldCacheFile.exists() && !newCacheFile.exists()) {
						oldCacheFile.renameTo(newCacheFile);
					}
				}
			}
		}
		
		/* add unrecognized objects */
		for (File file : newFiles) {
			Entity entity = new Entity(file);
			mainEntityList.add(entity);
			filter.getNewEntities().add(entity);
		}
		
		/* discard orphan objects */
		for (Entity entity : orphanEntities) {
			mainEntityList.remove(entity);
		}
		
		mainEntityList.sort(Comparator.comparing(Entity::getName));
	}
	
	public static void exitApplication() {
		Logger.getGlobal().info("Application Exit");
		
		CacheManager.stopThread();
		
		paneEntity.disposeVideoPlayer();
		paneEntity.getControls().hide();
		
		Project.getCurrent().writeToDisk();
		settings.writeToDisk();
	}
}
