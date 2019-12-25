package main;

import base.CustomList;
import base.entity.EntityCollectionUtil;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheManager;
import control.filter.Filter;
import control.Select;
import control.Target;
import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.application.Application;
import javafx.stage.Stage;
import misc.FileUtil;
import misc.Settings;
import misc.Project;
import ui.main.center.PaneGallery;
import ui.main.center.GalleryTile;
import ui.main.center.PaneEntity;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;
import ui.main.top.PaneToolbar;
import ui.stage.StageManager;

import java.io.File;
import java.util.Comparator;
import java.util.logging.Logger;

public class Main extends Application {
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
		Settings.readFromDisk();
		
		PaneToolbar.get().init();
		PaneGallery.get().init();
		PaneEntity.get().init();
		PaneFilter.get().init();
		PaneSelect.get().init();
		
		GalleryTile.init();
		Reload.init();
	}
	private static void initLoading() {
		
		if (!QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
			StageManager.getStageMain().layoutIntro();
		} else {
			CustomList<Project> projects = FileUtil.getProjects();
			StageManager.getStageMain().layoutMain();
			
			projects.sort(Project.getComparator());
			startDatabaseLoading(projects.getFirst());
		}
	}
	
	public static void startDatabaseLoading(Project project) {
		Project.setCurrent(project);
		EntityList.getMain().addAll(project.getEntityList());
		checkForNewFiles(FileUtil.getSupportedFiles(new File(project.getDirectorySource())));
		
		EntityCollectionUtil.init();
		EntityList.getMain().sort();
		initTags(project);
		Filter.refresh();
		Target.set(Filter.getEntities().get(0));
		Select.getEntities().set(Filter.getEntities().get(0));
		
		Reload.start();
		
		PaneFilter.get().collapseAll();
		PaneSelect.get().collapseAll();
		
		CacheManager.createCacheInBackground(EntityList.getMain());
	}
	private static void initTags(Project project) {
		//todo help
		TagList allTags = project.getTagList();
		if (allTags != null) {
			TagList.getMainInstance().addAll(allTags);
		}
		
		for (Entity entity : EntityList.getMain()) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (TagList.getMainInstance().containsEqualTo(tag)) {
					tagList.set(tagList.indexOf(tag), TagList.getMainInstance().getTag(tag));
				} else {
					TagList.getMainInstance().add(tag);
				}
			}
		}
		
		TagList.getMainInstance().sort();
		Reload.notify(ChangeIn.TAG_LIST_MAIN);
	}
	private static void checkForNewFiles(CustomList<File> fileList) {
		EntityList.getMain().sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		
		EntityList orphanEntities = new EntityList(EntityList.getMain());
		CustomList<File> newFiles = new CustomList<>(fileList);
		
		/* compare files in the source directory with known objects in the database */
		for (Entity entity : EntityList.getMain()) {
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
			EntityList.getMain().add(entity);
			Filter.getNewEntities().add(entity);
		}
		
		/* discard orphan objects */
		for (Entity entity : orphanEntities) {
			EntityList.getMain().remove(entity);
		}
		
		EntityList.getMain().sort(Comparator.comparing(Entity::getName));
	}
	
	public static void exitApplication() {
		Logger.getGlobal().info("Application Exit");
		
		CacheManager.stopThread();
		
		PaneEntity.get().disposeVideoPlayer();
		PaneEntity.get().getControls().hide();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
	}
}
