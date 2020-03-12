package main;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheManager;
import control.Select;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.application.Application;
import javafx.stage.Stage;
import misc.FileUtil;
import misc.Project;
import misc.Settings;
import ui.main.display.DisplayPane;
import ui.main.stage.MainStage;
import ui.stage.ImportStage;

import java.io.File;

public class Main extends Application {
	public static final boolean DEBUG_MAIN_QUICKSTART = false;
	
	public static final boolean DEBUG_FS_FILE_MOVE = true;
	public static final boolean DEBUG_FS_FILE_DELETE = true;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		
		Settings.readFromDisk();
		
		if (!DEBUG_MAIN_QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
			MainStage.layoutIntro();
		} else {
			Project.setFirstAsCurrent();
			MainStage.layoutMain();
			startProjectDatabaseLoading();
		}
	}
	
	public static void startProjectDatabaseLoading() {
		initTags();
		initEntities();
		initCollections();
		
		Select.setTarget(EntityList.getMain().getFirstImpl());
		Select.getEntities().addImpl(EntityList.getMain().getFirstImpl());
		
		Reload.notify(Notifier.values());
		Reload.start();
		
		CacheManager.checkCacheInBackground(EntityList.getMain());
	}
	
	private static void initEntities() {
		EntityList.getMain().setAllImpl(Project.getCurrent().getEntityList());
		
		EntityList entitiesWithoutFiles = new EntityList(EntityList.getMain());
		CustomList<File> filesWithoutEntities = FileUtil.getFiles(new File(Project.getCurrent().getDirectorySource()), true);
		
		CustomList<String> newFileNames = new CustomList<>();
		filesWithoutEntities.forEach(file -> newFileNames.addImpl(FileUtil.createEntityName(file)));
		
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
		
		if (!entitiesWithoutFiles.isEmpty()) {
			EntityList.getMain().removeAll(entitiesWithoutFiles);
		}
		if (!filesWithoutEntities.isEmpty()) {
			EntityList newEntities = new EntityList(filesWithoutEntities);
			EntityList.getMain().addAllImpl(newEntities);
			Filter.getLastImport().addAllImpl(newEntities);
			EntityList.getMain().sort();
		}
		
		for (Entity entity : EntityList.getMain()) {
			for (int tagID : entity.getTagIDs()) {
				entity.getTagList().addImpl(TagList.getMain().getTag(tagID));
			}
		}
	}
	private static void initCollections() {
		CustomList<EntityList> collections = new CustomList<>();
		for (Entity entity : EntityList.getMain()) {
			int collectionID = entity.getCollectionID();
			if (collectionID != 0) {
				boolean collectionExists = false;
				for (EntityList collection : collections) {
					if (collection.getFirstImpl().getCollectionID() == collectionID) {
						collection.addImpl(entity);
						entity.setCollection(collection);
						collectionExists = true;
						break;
					}
				}
				if (!collectionExists) {
					EntityList collection = new EntityList(entity);
					entity.setCollection(collection);
					collections.addImpl(collection);
				}
			}
		}
	}
	private static void initTags() {
		TagList allTags = Project.getCurrent().getTagList();
		TagList tagListMain = TagList.getMain();
		if (allTags != null) tagListMain.addAllImpl(allTags);
		
		tagListMain.forEach(Tag::updateStringValue);
		tagListMain.sort();
		
		Reload.notify(Notifier.TAGLIST_CHANGED);
	}
	
	public static void exitApplication() {
		if (CacheManager.getThread() != null) CacheManager.getThread().interrupt();
		if (ImportStage.getThread() != null) ImportStage.getThread().interrupt();
		
		DisplayPane.getInstance().disposeVideoPlayer();
		DisplayPane.getInstance().getControls().hide();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
		System.exit(0);
	}
}
