package main;

import backend.BaseList;
import backend.cache.CacheLoader;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.filter.Filter;
import backend.group.Group;
import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import backend.reload.Notifier;
import backend.reload.Reload;
import backend.select.Select;
import backend.tag.Tag;
import backend.tag.TagList;
import frontend.UserInterface;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
	// CONST
	public static final boolean DEBUG_AUTOLOAD_LATEST_PROJECT = false;
	public static final boolean DEBUG_USE_CACHE = true;
	
	public static final boolean DEBUG_FS_ALLOW_FILE_MOVE = true;
	public static final boolean DEBUG_FS_ALLOW_FILE_DELETE = true;
	
	// DATABASE
	public static final Thread THREAD_MAIN;
	public static final ThreadGroup THREADS;
	
	public static final EntityList DB_ENTITY;
	public static final TagList DB_TAG;
	
	// CONTROLLER
	public static final Filter FILTER;
	public static final Select SELECT;
	
	// STATIC
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		
		Settings.readFromDisk();
		
		THREAD_MAIN = Thread.currentThread();
		THREADS = new ThreadGroup("ROOT");
		
		DB_ENTITY = new EntityList();
		DB_TAG = new TagList();
		
		FILTER = new Filter();
		SELECT = new Select();
	}
	
	// MAIN
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		UserInterface.initialize();
		
		if (!DEBUG_AUTOLOAD_LATEST_PROJECT || FileUtil.getProjectFiles().isEmpty()) {
			UserInterface.getStage().showIntroScene();
		} else {
			Main.startMain(null);
		}
	}
	
	public static void startMain(Project project) {
		if (project == null) {
			Project.setMostRecentAsCurrent();
		} else {
			Project.setCurrent(project);
		}
		
		Main.initDatabase();
		
		UserInterface.getStage().showMainScene();
		
		CacheLoader.startCacheThread(Main.DB_ENTITY);
	}
	
	private static void initDatabase() {
		initTags();
		initEntities();
		initGroups();
		
		Entity target = DB_ENTITY.getFirst();
		if (target != null) {
			SELECT.setTarget(target, true);
			if (target.hasGroup()) {
				SELECT.setAll(target.getGroup());
			} else {
				SELECT.set(target);
			}
		}
	}
	private static void initTags() {
		TagList projectTags = Project.getCurrent().getTagList();
		if (projectTags != null) DB_TAG.setAll(projectTags);
		
		DB_TAG.forEach(Tag::updateStringValue);
		DB_TAG.sort();
		
		Reload.notify(Notifier.TAGLIST_CHANGED);
	}
	private static void initEntities() {
		DB_ENTITY.setAll(Project.getCurrent().getEntityList());
		
		EntityList entitiesWithoutFiles = new EntityList(DB_ENTITY);
		BaseList<File> filesWithoutEntities = FileUtil.getFiles(new File(Project.getCurrent().getDirectory()), true);
		
		BaseList<String> newFileNames = new BaseList<>();
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
		
		if (!entitiesWithoutFiles.isEmpty()) {
			DB_ENTITY.removeAll(entitiesWithoutFiles);
		}
		if (!filesWithoutEntities.isEmpty()) {
			EntityList newEntities = new EntityList(filesWithoutEntities);
			DB_ENTITY.addAll(newEntities);
			FILTER.getLastImport().addAll(newEntities);
			DB_ENTITY.sortByName();
		}
		
		for (Entity entity : DB_ENTITY) {
			entity.initTags();
		}
		
		//todo check every entity for size change, generate new cache if needed
	}
	private static void initGroups() {
		BaseList<Group> groups = new BaseList<>();
		for (Entity entity : DB_ENTITY) {
			if (entity.hasGroup()) {
				boolean groupExists = false;
				for (Group group : groups) {
					if (group.getFirst().getGroupID() == entity.getGroupID()) {
						group.add(entity);
						entity.setGroup(group);
						groupExists = true;
						break;
					}
				}
				if (!groupExists) {
					Group group = new Group(entity);
					entity.setGroup(group);
					groups.add(group);
				}
			}
		}
		for (Entity entity : DB_ENTITY) {
			if (entity.hasGroup()) {
				if (entity.getGroup().size() == 1) {
					entity.setGroupID(0);
					entity.setGroup(null);
				}
			}
		}
	}
	
	public static void exitApplication() {
		THREADS.interrupt();
		UserInterface.getDisplayPane().disposeVideoPlayer();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
		System.exit(0);
	}
}
