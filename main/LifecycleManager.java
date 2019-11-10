package application.main;

import application.baseobject.CustomList;
import application.baseobject.Project;
import application.baseobject.entity.Entity;
import application.baseobject.entity.EntityList;
import application.baseobject.entity.EntityListMain;
import application.cache.CacheWriter;
import application.gui.stage.StageManager;
import application.gui.stage.template.YesNoCancelStage;
import application.tools.FileUtil;

import java.io.File;
import java.util.Comparator;

public abstract class LifecycleManager implements InstanceCollector {
	public static void init() {
		setLoggerFormat();
		initInstances();
		showGUI();
	}
	
	private static void setLoggerFormat() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
	}
	private static void initInstances() {
		settings.readFromDisk();
		
		toolbarPane.init();     /* needs Settings */
		galleryPane.init();     /* needs Settings */
		mediaPane.init();       /* needs Settings, GalleryPane */
		filterPane.init();      /* needs Settings */
		selectPane.init();      /* needs Settings */
		
		filter.init();
		target.init();
		select.init();
		reload.init();          /* needs everything */
	}
	private static void showGUI() {
		StageManager.getMainStage().init();
		StageManager.getMainStage().setStagePropertiesIntro();
		StageManager.getMainStage().showIntroScene();
	}
	
	public static void startLoading(Project project) {
		StageManager.getMainStage().setVisible(false);
		StageManager.getMainStage().setStagePropertiesMain();
		StageManager.getMainStage().showMainScene();
		
		String projectFilePath = project.getProjectFileFullPath();
		String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
		String sourceDirectory = project.getWorkingDirectory();
		
		FileUtil.init(projectDirectory, sourceDirectory);
		
		CustomList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getProjectDirSource());
		FileUtil.fixDuplicateFileNames(fileList);
		
		boolean readSuccess = entityListMain.addAll(EntityListMain.readFromDisk());
		if (!readSuccess) {
			createDatabase(fileList);
		}
		
		checkFileDifference(fileList);
		
		FileUtil.initEntityPaths(fileList);
		entityListMain.sort();
		tagListMain.initialize();
		entityListMain.writeToDisk();
		tagListMain.writeDummyToDisk();
		filter.refresh();
		target.set(filter.get(0));
		select.set(filter.get(0));
		
		reload.doReload();
		
		filterPane.collapseAll();
		selectPane.collapseAll();
		
		galleryPane.updateViewportTilesVisibility();
		
		StageManager.getMainStage().setVisible(true);
		
		CacheWriter.writeAllCacheInBackground();
	}
	
	private static void createDatabase(CustomList<File> fileList) {
		entityListMain.clear();
		for (File file : fileList) {
			entityListMain.add(new Entity(file));
		}
	}
	
	private static void checkFileDifference(CustomList<File> fileList) {
		entityListMain.sort(Comparator.comparing(Entity::getName));
		fileList.sort(Comparator.comparing(File::getName));
		EntityList orphanEntities = new EntityList(entityListMain);
		CustomList<File> newFiles = new CustomList<>(fileList);
		
		/* compare files in the working directory with known objects in the database */
		for (Entity entity : entityListMain) {
			for (int i = 0; i < newFiles.size(); i++) {
				File file = newFiles.get(i);
				if (entity.getName().equals(file.getName())) {
					orphanEntities.remove(entity);
					newFiles.remove(file);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File newFile : new CustomList<>(newFiles)) {
			Entity newFileEntity = new Entity(newFile);
			CacheWriter.write(newFileEntity);
			
			for (Entity orphanEntity : new CustomList<>(orphanEntities)) {
				if (newFile.length() == orphanEntity.getLength()) {
					if (StageManager.getCacheCompareStage().show(orphanEntity.getName(), newFileEntity.getName(), FileUtil.getCacheFilePath(orphanEntity), FileUtil.getCacheFilePath(newFileEntity)).equals(YesNoCancelStage.Result.YES)) {
						newFiles.remove(newFile);
						orphanEntities.remove(orphanEntity);
						
						/* rename the object and cache file */
						File oldCacheFile = new File(FileUtil.getCacheFilePath(orphanEntity));
						orphanEntity.setName(newFile.getName());
						File newCacheFile = new File(FileUtil.getCacheFilePath(orphanEntity));
						
						if (oldCacheFile.exists() && !newCacheFile.exists()) {
							oldCacheFile.renameTo(newCacheFile);
						}
					} else {
						break;
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
}
