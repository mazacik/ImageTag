package main;

import backend.BaseList;
import backend.cache.CacheLoader;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.filter.Filter;
import backend.group.EntityGroup;
import backend.misc.FileUtil;
import backend.misc.Settings;
import backend.project.Project;
import backend.project.ProjectUtil;
import backend.reload.Notifier;
import backend.reload.Reload;
import backend.select.Select;
import frontend.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.util.Comparator;

public class Main extends Application {
	public static final boolean AUTOLOAD_LATEST_PROJECT = false;
	
	public static final ThreadGroup THREADGROUP = new ThreadGroup("MAIN");
	public static final Thread THREAD_MAIN = Thread.currentThread();
	
	public static final EntityList ENTITYLIST = new EntityList();
	public static final BaseList<String> TAGLIST = new BaseList<>();
	
	public static final Filter FILTER = new Filter();
	public static final Select SELECT = new Select();
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		Settings.read();
		UserInterface.initialize();
		
		if (AUTOLOAD_LATEST_PROJECT && !ProjectUtil.getProjectFiles().isEmpty()) {
			startLoadingProject(null);
		} else {
			UserInterface.getStage().showIntroScene();
		}
	}
	
	public static void startLoadingProject(Project project) {
		startLoadingAsync(project);
		UserInterface.getStage().showMainScene();
	}
	
	private static void startLoadingAsync(Project project) {
		new Thread(Main.THREADGROUP, () -> {
			ProjectUtil.setCurrentProject(project);
			
			initEntityList();
			if (!Main.ENTITYLIST.isEmpty()) {
				BaseList<EntityGroup> entityGroups = new BaseList<>();
				for (Entity entity : Main.ENTITYLIST) {
					entity.initTile();
					entity.initType();
					initEntityGroup(entityGroups, entity);
				}
				for (EntityGroup entityGroup : entityGroups) {
					if (entityGroup.size() == 1) {
						entityGroup.discard();
					}
				}
				
				Main.TAGLIST.setAll(Main.ENTITYLIST.getTagList());
				Main.TAGLIST.sort(Comparator.naturalOrder());
				
				Entity target = Main.ENTITYLIST.getFirst();
				if (target != null) {
					Main.SELECT.setTarget(target);
					if (target.hasGroup()) {
						Main.SELECT.setAll(target.getEntityGroup());
					} else {
						Main.SELECT.set(target);
					}
				}
			}
			
			Platform.runLater(() -> {
				Reload.notify(Notifier.values());
				Reload.start();
			});
			
			CacheLoader.startCacheThread(Main.ENTITYLIST);
		}).start();
	}
	private static void initEntityList() {
		Main.ENTITYLIST.setAll(ProjectUtil.getCurrentProject().getEntityList());
		
		EntityList entitiesWithoutFiles = new EntityList(Main.ENTITYLIST);
		BaseList<File> filesWithoutEntities = FileUtil.getFiles(new File(ProjectUtil.getCurrentProject().getDirectory()), true);
		
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
			Main.ENTITYLIST.removeAll(entitiesWithoutFiles);
		}
		if (!filesWithoutEntities.isEmpty()) {
			EntityList newEntities = new EntityList(filesWithoutEntities);
			Main.ENTITYLIST.addAll(newEntities);
			Main.FILTER.getLastImport().addAll(newEntities);
			Main.ENTITYLIST.sortByName();
		}
	}
	private static void initEntityGroup(BaseList<EntityGroup> entityGroups, Entity entity) {
		if (entity.hasGroup()) {
			for (EntityGroup entityGroup : entityGroups) {
				if (entityGroup.getID() == entity.getEntityGroupID()) {
					entityGroup.add(entity);
					entity.setEntityGroup(entityGroup);
					entity.getTile().updateGroupIcon();
					return;
				}
			}
			EntityGroup entityGroup = new EntityGroup(entity);
			entity.setEntityGroup(entityGroup);
			entity.getTile().updateGroupIcon();
			entityGroups.add(entityGroup);
		}
	}
}
