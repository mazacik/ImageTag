package main;

import base.CustomList;
import base.entity.Collection;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheUtil;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.application.Application;
import javafx.stage.Stage;
import misc.FileUtil;
import misc.Project;
import misc.Settings;

import java.io.File;

public class Main extends Application {
	public static final boolean DEBUG_MAIN_QUICKSTART = true;
	
	public static final boolean DEBUG_FS_ALLOW_FILE_MOVE = true;
	public static final boolean DEBUG_FS_ALLOW_FILE_DELETE = true;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		
		Settings.readFromDisk();
		
		if (!DEBUG_MAIN_QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
			Root.MAIN_STAGE.layoutIntro();
		} else {
			Project.setFirstAsCurrent();
			Root.MAIN_STAGE.layoutMain();
			startProjectDatabaseLoading();
		}
	}
	
	public static void startProjectDatabaseLoading() {
		initTags();
		initEntities();
		initCollections();
		
		Entity target = EntityList.getMain().getFirst();
		if (target != null) {
			Root.SELECT.setTarget(target);
			if (target.hasCollection()) {
				Root.SELECT.addAll(target.getCollection());
			} else {
				Root.SELECT.add(target);
			}
		}
		
		Reload.notify(Notifier.values());
		Reload.start();
		
		CacheUtil.loadCache(EntityList.getMain());
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
			Root.FILTER.getLastImport().addAllImpl(newEntities);
			EntityList.getMain().sort();
		}
		
		for (Entity entity : EntityList.getMain()) {
			for (int tagID : entity.getTagIDs()) {
				entity.getTagList().addImpl(TagList.getMain().getTag(tagID));
			}
		}
	}
	private static void initCollections() {
		CustomList<Collection> collections = new CustomList<>();
		for (Entity entity : EntityList.getMain()) {
			if (entity.hasCollection()) {
				boolean collectionExists = false;
				for (Collection collection : collections) {
					if (collection.getFirst().getCollectionID() == entity.getCollectionID()) {
						collection.addImpl(entity);
						entity.setCollection(collection);
						collectionExists = true;
						break;
					}
				}
				if (!collectionExists) {
					Collection collection = new Collection(entity);
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
		Thread[] threads = new Thread[Root.THREADPOOL.activeCount()];
		Root.THREADPOOL.enumerate(threads);
		for (Thread thread : threads) {
			if (thread.isAlive()) {
				thread.interrupt();
			}
		}
		
		Root.DISPLAY_PANE.disposeVideoPlayer();
		Root.DISPLAY_PANE.getControls().hide();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
		System.exit(0);
	}
}
