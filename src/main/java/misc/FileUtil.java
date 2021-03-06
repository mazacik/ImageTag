package misc;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import cache.CacheManager;
import com.sun.jna.platform.FileUtils;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import enums.MediaType;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import main.Main;
import ui.main.stage.StageMain;
import ui.stage.StageConfirmation;
import ui.stage.StageSimpleMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class FileUtil {
	private static final String APPLICATION_NAME = "Tagallery";
	
	private static final String[] EXTENSIONS_IMAGE = new String[]{".jpg", ".jpeg", ".png"};
	private static final String[] EXTENSIONS_GIF = new String[]{".gif"};
	private static final String[] EXTENSIONS_VIDEO = new String[]{".mp4", ".m4v", ".mov", ".wmv", ".avi", ".webm"};
	
	public static String directoryChooser(Scene ownerScene) {
		if (ownerScene == null || ownerScene.getWindow() == null) throw new NullPointerException();
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose a Directory");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File directory = directoryChooser.showDialog(ownerScene.getWindow());
		
		if (directory == null) return "";
		
		return directory.getAbsolutePath();
	}
	
	public static void moveFile(String from, String to) {
		if (Main.DEBUG_FS_FILE_MOVE) {
			try {
				Files.move(Paths.get(from), Paths.get(to));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean deleteFile(String path) {
		if (Main.DEBUG_FS_FILE_DELETE) {
			int counter = 0;
			FileUtils fileUtils = FileUtils.getInstance();
			File abstractFile = new File(path);
			
			if (fileUtils.hasTrash()) {
				if (abstractFile.isFile()) {
					try {
						fileUtils.moveToTrash(new File[]{abstractFile});
						//if (!abstractFile.exists()) counter++;
						counter++;
					} catch (IOException e) {
						StageSimpleMessage.show("Delete failed: " + abstractFile.getAbsolutePath() + "\nCannot access the file because it is being used by another process.");
						e.printStackTrace();
					}
				} else {
					for (File _abstractFile : new CustomList<>(abstractFile.listFiles())) {
						deleteFile(_abstractFile.getAbsolutePath());
					}
				}
			} else {
				StageSimpleMessage.show("Delete failed: No OS-level file trash support");
				Logger.getGlobal().severe("No OS-level file trash support");
			}
			
			return counter != 0;
		} else {
			return true;
		}
	}
	
	public static String getFileNameNoExtension(String path) {
		if (path.contains(File.separator)) {
			path = path.substring(path.lastIndexOf(File.separator) + 1);
		}
		if (path.contains(".")) {
			path = path.substring(0, path.lastIndexOf("."));
		}
		return path;
	}
	
	private static Thread importThread = null;
	public static void importFiles() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a directory to import");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File directory = directoryChooser.showDialog(StageMain.getInstance());
		
		if (directory != null && directory.isDirectory()) {
			if (importThread == null || !importThread.isAlive()) {
				importThread = new Thread(() -> {
					EntityList newEntities = new EntityList();
					for (File file : getSupportedFiles(directory)) {
						if (Thread.currentThread().isInterrupted()) {
							Logger.getGlobal().info("interrupted");
							return;
						}
						
						String pathBefore = file.getAbsolutePath();
						String pathAfter = Project.getCurrent().getDirectorySource() + File.separator + FileUtil.createEntityName(file, directory.getAbsolutePath());
						
						File fileAfter = new File(pathAfter);
						if (!fileAfter.exists()) {
							fileAfter.getParentFile().mkdirs();
							FileUtil.moveFile(pathBefore, pathAfter);
							newEntities.add(new Entity(fileAfter));
						} else {
							Logger.getGlobal().info("Could not import file " + file.getName() + ", file already exists");
						}
					}
					
					if (newEntities.isEmpty()) {
						Platform.runLater(() -> StageSimpleMessage.show("Imported 0 files."));
					} else {
						CacheManager.checkCacheInBackground(newEntities);
						
						EntityList.getMain().addAll(newEntities);
						EntityList.getMain().sort();
						
						Filter.getNewEntities().setAll(newEntities);
						
						Platform.runLater(() -> {
							String s = "Imported " + newEntities.size() + " files.\nWould you like to view the new files?";
							if (StageConfirmation.show(s)) {
								Filter.getSettings().setShowImages(true);
								Filter.getSettings().setShowGifs(true);
								Filter.getSettings().setShowVideos(true);
								Filter.getSettings().setShowOnlyNewEntities(true);
								Filter.getSettings().setEnableLimit(false);
								
								Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
								Reload.start();
							}
						});
					}
					Logger.getGlobal().info("finished");
				});
				importThread.start();
			}
		}
	}
	public static void stopImportThread() {
		if (importThread != null) importThread.interrupt();
	}
	
	public static CustomList<Project> getProjects() {
		CustomList<File> projectFiles = new CustomList<>(new File(getDirectoryProject()).listFiles(file -> file.isFile() && file.getName().endsWith(".json")));
		CustomList<Project> projects = new CustomList<>();
		projectFiles.forEach(projectFile -> projects.add(Project.readFromDisk(projectFile.getAbsolutePath())));
		return projects;
	}
	public static CustomList<File> getProjectFiles() {
		return new CustomList<>(new File(getDirectoryProject()).listFiles(file -> file.isFile() && file.getName().endsWith(".json")));
	}
	public static CustomList<File> getSupportedFiles() {
		return getSupportedFiles(new File(Project.getCurrent().getDirectorySource()));
	}
	public static CustomList<File> getSupportedFiles(File directorySource) {
		CustomList<File> actualFiles = new CustomList<>();
		CustomList<File> abstractFiles = new CustomList<>(directorySource.listFiles(file -> {
			if (file.isDirectory()) {
				return true;
			}
			
			String fileName = file.getName().toLowerCase();
			for (String ext : EXTENSIONS_IMAGE) {
				if (fileName.endsWith(ext)) {
					return true;
				}
			}
			for (String ext : EXTENSIONS_GIF) {
				if (fileName.endsWith(ext)) {
					return true;
				}
			}
			for (String ext : EXTENSIONS_VIDEO) {
				if (fileName.endsWith(ext)) {
					return true;
				}
			}
			
			return false;
		}));
		
		for (File abstractFile : abstractFiles) {
			if (abstractFile.isDirectory()) {
				actualFiles.addAll(FileUtil.getSupportedFiles(abstractFile));
			} else {
				actualFiles.add(abstractFile);
			}
		}
		
		return actualFiles;
	}
	
	public static String createEntityName(File file) {
		return createEntityName(file, Project.getCurrent().getDirectorySource());
	}
	public static String createEntityName(File file, String directorySource) {
		return file.getAbsolutePath().substring((directorySource + File.separator).length());
	}
	
	public static String getFileEntity(Entity entity) {
		return Project.getCurrent().getDirectorySource() + File.separator + entity.getName();
	}
	public static String getFileCache(Entity entity) {
		return getDirectoryCache(Project.getCurrent().getProjectName()) + File.separator + entity.getName() + "-" + entity.getSize() + ".jpg";
	}
	public static MediaType getMediaType(Entity entity) {
		String ext = entity.getName().toLowerCase().substring(entity.getName().lastIndexOf('.'));
		
		for (String _ext : EXTENSIONS_IMAGE) {
			if (ext.equals(_ext)) {
				return MediaType.IMAGE;
			}
		}
		for (String _ext : EXTENSIONS_GIF) {
			if (ext.equals(_ext)) {
				return MediaType.GIF;
			}
		}
		for (String _ext : EXTENSIONS_VIDEO) {
			if (ext.equals(_ext)) {
				return MediaType.VIDEO;
			}
		}
		
		throw new RuntimeException("MediaType " + ext.toUpperCase() + " is not supported");
	}
	
	public static String getDirectoryLocal() {
		return System.getenv("APPDATA") + File.separator + APPLICATION_NAME;
	}
	public static String getDirectoryProject() {
		return getDirectoryLocal() + File.separator + "data";
	}
	public static String getDirectoryCache() {
		return getDirectoryCache(Project.getCurrent().getProjectName());
	}
	public static String getDirectoryCache(String projectName) {
		return getDirectoryLocal() + File.separator + "cache" + File.separator + projectName;
	}
	public static String getFileSettings() {
		return getDirectoryLocal() + File.separator + "settings.json";
	}
	
	public static String getApplicationName() {
		return APPLICATION_NAME;
	}
}
