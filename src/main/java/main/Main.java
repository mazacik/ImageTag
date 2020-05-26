package main;

import backend.cache.CacheLoader;
import backend.control.filter.Filter;
import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.control.select.Select;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.list.group.Group;
import backend.list.tag.Tag;
import backend.list.tag.TagList;
import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import frontend.component.display.DisplayPane;
import frontend.component.gallery.GalleryPane;
import frontend.component.side.FilterPane;
import frontend.component.side.select.SelectionPane;
import frontend.component.top.ToolbarPane;
import frontend.stage.primary.PrimaryStage;
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
	
	public static final EntityList ENTITYLIST;
	public static final TagList TAGLIST;
	
	public static final Filter FILTER;
	public static final Select SELECT;
	
	// GUI
	public static final ToolbarPane TOOLBAR_PANE;
	public static final GalleryPane GALLERY_PANE;
	public static final DisplayPane DISPLAY_PANE;
	public static final FilterPane FILTER_PANE;
	public static final SelectionPane SELECT_PANE;
	
	public static final PrimaryStage STAGE;
	
	// STATIC
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		
		Settings.readFromDisk();
		
		THREAD_MAIN = Thread.currentThread();
		THREADS = new ThreadGroup("ROOT");
		
		ENTITYLIST = new EntityList();
		TAGLIST = new TagList();
		
		FILTER = new Filter();
		SELECT = new Select();
		
		TOOLBAR_PANE = new ToolbarPane();
		GALLERY_PANE = new GalleryPane();
		DISPLAY_PANE = new DisplayPane();
		FILTER_PANE = new FilterPane();
		SELECT_PANE = new SelectionPane();
		
		STAGE = new PrimaryStage();
	}
	
	// MAIN
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		if (!DEBUG_AUTOLOAD_LATEST_PROJECT || FileUtil.getProjectFiles().isEmpty()) {
			STAGE.showIntroScene();
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
		
		STAGE.showMainScene();
		
		CacheLoader.startCacheThread(Main.ENTITYLIST);
	}
	
	private static void initDatabase() {
		initTags();
		initEntities();
		initGroups();
		
		Entity target = ENTITYLIST.getFirst();
		if (target != null) {
			SELECT.setTarget(target);
			if (target.hasGroup()) {
				SELECT.setAll(target.getGroup());
			} else {
				SELECT.set(target);
			}
		}
	}
	private static void initTags() {
		TagList projectTags = Project.getCurrent().getTagList();
		if (projectTags != null) TAGLIST.setAll(projectTags);
		
		TAGLIST.forEach(Tag::updateStringValue);
		TAGLIST.sort();
		
		Reload.notify(Notifier.TAGLIST_CHANGED);
	}
	private static void initEntities() {
		ENTITYLIST.setAll(Project.getCurrent().getEntityList());
		
		EntityList entitiesWithoutFiles = new EntityList(ENTITYLIST);
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
					/* rename the object and client.cache file */
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
			ENTITYLIST.removeAll(entitiesWithoutFiles);
		}
		if (!filesWithoutEntities.isEmpty()) {
			EntityList newEntities = new EntityList(filesWithoutEntities);
			ENTITYLIST.addAll(newEntities);
			FILTER.getLastImport().addAll(newEntities);
			ENTITYLIST.sort();
		}
		
		for (Entity entity : ENTITYLIST) {
			entity.initTags();
		}
	}
	private static void initGroups() {
		BaseList<Group> groups = new BaseList<>();
		for (Entity entity : ENTITYLIST) {
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
		for (Entity entity : ENTITYLIST) {
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
		DISPLAY_PANE.disposeVideoPlayer();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
		System.exit(0);
	}
}
