package main;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheManager;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.application.Application;
import javafx.stage.Stage;
import misc.FileUtil;
import misc.Project;
import misc.Settings;
import ui.main.center.GalleryTile;
import ui.main.center.PaneEntity;
import ui.main.center.PaneGallery;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;
import ui.main.top.PaneToolbar;
import ui.stage.StageManager;

import java.io.File;

public class Main extends Application {
	private static final boolean QUICKSTART = true;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
		
		//Settings.readFromDisk();
		
		PaneToolbar.getInstance().init();
		PaneGallery.getInstance().init();
		PaneEntity.getInstance().init();
		PaneFilter.getInstance().init();
		PaneSelect.getInstance().init();
		
		Reload.initStaticVariables();//todo remove
		
		if (!QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
			StageManager.getStageMain().layoutIntro();
		} else {
			CustomList<Project> projects = FileUtil.getProjects();
			StageManager.getStageMain().layoutMain();
			
			projects.sort(Project.getComparator());
			Project.setCurrent(projects.getFirst());
			startDatabaseLoading();
		}
	}
	
	public static void startDatabaseLoading() {
		initEntities();
		initCollections();
		initTags();
		
		Filter.refresh();
		Reload.start();
		
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
		File newFile;
		long newFileLength;
		Entity orphanEntity;
		for (int i = 0; i < filesWithoutEntities.size(); i++) {
			newFile = filesWithoutEntities.get(i);
			newFileLength = newFile.length();
			for (int j = 0; j < entitiesWithoutFiles.size(); j++) {
				orphanEntity = entitiesWithoutFiles.get(j);
				if (newFileLength == orphanEntity.getLength()) {
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
			needsSort = true;
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
		//todo probably needs rework
		CustomList<EntityList> collections = new CustomList<>();
		int collectionID;
		for (Entity entity : EntityList.getMain()) {
			collectionID = entity.getCollectionID();
			if (collectionID != 0) {
				boolean match = false;
				for (EntityList collection : collections) {
					if (collection.getFirst().getCollectionID() == collectionID) {
						collection.add(entity);
						entity.setCollection(collection);
						match = true;
						break;
					}
				}
				if (!match) {
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
		
		Tag entityTag;
		Tag mainListTag;
		TagList tagList;
		for (Entity entity : EntityList.getMain()) {
			tagList = entity.getTagList();
			
			for (int i = 0; i < tagList.size(); i++) {
				entityTag = tagList.get(i);
				mainListTag = tagListMain.getTag(entityTag);
				
				if (mainListTag != null) {
					tagList.set(i, mainListTag);
				} else {
					tagListMain.add(entityTag);
				}
			}
		}
		
		tagListMain.sort();
		Reload.notify(ChangeIn.TAG_LIST_MAIN);
	}
	
	public static void exitApplication() {
		CacheManager.stopThread();
		
		PaneEntity.getInstance().disposeVideoPlayer();
		PaneEntity.getInstance().getControls().hide();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
	}
}
