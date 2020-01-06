package main;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheManager;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.application.Application;
import javafx.stage.Stage;
import misc.FileUtil;
import misc.Project;
import misc.Settings;
import ui.main.display.PaneDisplay;
import ui.main.gallery.Tile;
import ui.main.side.PaneFilter;
import ui.main.side.PaneSelect;
import ui.main.stage.StageMain;
import ui.main.top.PaneToolbar;

import java.io.File;

public class Main extends Application {
	private static final boolean QUICKSTART = true;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
		
		PaneToolbar.getInstance().init();
		PaneDisplay.getInstance().init();
		PaneFilter.getInstance().init();
		PaneSelect.getInstance().init();
		
		if (!QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
			StageMain.layoutIntro();
		} else {
			CustomList<Project> projects = FileUtil.getProjects();
			StageMain.layoutMain();
			
			projects.sort(Project.getComparator());
			Project.setCurrent(projects.getFirst());
			startDatabaseLoading();
		}
	}
	
	public static void startDatabaseLoading() {
		initEntities();
		initCollections();
		initTags();
		
		Reload.notify(Notifier.values());
		Reload.start();
		
		PaneFilter.getInstance().collapseAll();
		PaneSelect.getInstance().collapseAll();
		
		CacheManager.checkCacheInBackground();
	}
	
	private static void initEntities() {
		EntityList.getMain().setAll(Project.getCurrent().getEntityList());
		
		EntityList entitiesWithoutFiles = new EntityList(EntityList.getMain());
		CustomList<File> filesWithoutEntities = FileUtil.getSupportedFiles();
		
		CustomList<String> newFileNames = new CustomList<>();
		filesWithoutEntities.forEach(file -> newFileNames.add(FileUtil.createEntityName(file)));
		
		/* match files in the source directory with known entities in the database */
		for (int i = 0; i < entitiesWithoutFiles.size(); i++) {
			for (int j = 0; j < filesWithoutEntities.size(); j++) {
				if (entitiesWithoutFiles.get(i).getName().equals(newFileNames.get(j))) {
					entitiesWithoutFiles.remove(i--);
					newFileNames.remove(j);
					filesWithoutEntities.remove(j);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (int i = 0; i < filesWithoutEntities.size(); i++) {
			File newFile = filesWithoutEntities.get(i);
			long newFileLength = newFile.length();
			for (int j = 0; j < entitiesWithoutFiles.size(); j++) {
				Entity orphanEntity = entitiesWithoutFiles.get(j);
				if (newFileLength == orphanEntity.getSize()) {
					/* rename the object and cache file */
					File oldCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					orphanEntity.setName(FileUtil.createEntityName(newFile));
					File newCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					
					if (oldCacheFile.exists() && !newCacheFile.exists()) {
						oldCacheFile.renameTo(newCacheFile);
					}
					
					filesWithoutEntities.remove(i--);
					entitiesWithoutFiles.remove(j);
					break;
				}
			}
		}
		
		boolean needsSort = false;
		if (!entitiesWithoutFiles.isEmpty()) {
			EntityList.getMain().removeAll(entitiesWithoutFiles);
		}
		if (!filesWithoutEntities.isEmpty()) {
			EntityList newEntities = new EntityList(filesWithoutEntities);
			EntityList.getMain().addAll(newEntities);
			Filter.getNewEntities().addAll(newEntities);
			needsSort = true;
		}
		if (needsSort) EntityList.getMain().sort();
	}
	private static void initCollections() {
		CustomList<EntityList> collections = new CustomList<>();
		for (Entity entity : EntityList.getMain()) {
			int collectionID = entity.getCollectionID();
			if (collectionID != 0) {
				boolean collectionExists = false;
				for (EntityList collection : collections) {
					if (collection.getFirst().getCollectionID() == collectionID) {
						collection.add(entity);
						entity.setCollection(collection);
						collectionExists = true;
						break;
					}
				}
				if (!collectionExists) {
					EntityList collection = new EntityList(entity);
					entity.setCollection(collection);
					collections.add(collection);
				}
			}
		}
	}
	private static void initTags() {
		TagList allTags = Project.getCurrent().getTagList();
		TagList tagListMain = TagList.getMain();
		if (allTags != null) tagListMain.addAll(allTags);
		
		for (Entity entity : EntityList.getMain()) {
			TagList tagList = entity.getTagList();
			
			for (int i = 0; i < tagList.size(); i++) {
				Tag entityTag = tagList.get(i);
				Tag mainListTag = tagListMain.getTag(entityTag);
				
				if (mainListTag != null) {
					tagList.set(i, mainListTag);
				} else {
					tagListMain.add(entityTag);
				}
			}
		}
		
		tagListMain.sort();
		Reload.notify(Notifier.TAG_LIST_MAIN);
	}
	
	public static void exitApplication() {
		CacheManager.stopCacheThread();
		FileUtil.stopImportThread();
		
		PaneDisplay.getInstance().disposeVideoPlayer();
		PaneDisplay.getInstance().getControls().hide();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
	}
}
