package misc;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import cache.CacheManager;
import com.sun.jna.platform.FileUtils;
import control.filter.Filter;
import control.reload.Reload;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import ui.stage.StageManager;

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
		try {
			Files.move(Paths.get(from), Paths.get(to));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void deleteFile(String path) {
		FileUtils fileUtils = FileUtils.getInstance();
		File abstractFile = new File(path);
		if (fileUtils.hasTrash()) {
			if (abstractFile.isFile()) {
				try {
					fileUtils.moveToTrash(new File[]{abstractFile});
				} catch (IOException e) {
					StageManager.getErrorStage().show("Delete failed: " + abstractFile.getAbsolutePath() + "\nCannot access the file because it is being used by another process.");
					e.printStackTrace();
				}
			} else {
				for (File _abstractFile : new CustomList<>(abstractFile.listFiles())) {
					deleteFile(_abstractFile.getAbsolutePath());
				}
			}
		} else {
			Logger.getGlobal().severe("No OS-level file trash support");
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
	
	public static void importFiles() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a directory to import from");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File directory = directoryChooser.showDialog(StageManager.getStageMain());
		
		EntityList newEntities = new EntityList();
		if (directory != null && directory.isDirectory()) {
			for (File file : getSupportedFiles(directory)) {
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
		}
		
		if (newEntities.isEmpty()) {
			StageManager.getErrorStage().show("Imported 0 files.");
		} else {
			CacheManager.checkCacheInBackground(newEntities);
			
			EntityList.getMain().addAll(newEntities);
			EntityList.getMain().sort();
			
			String s = "Imported " + newEntities.size() + " files.\nWould you like to view the new files?";
			if (StageManager.getYesNoStage().show(s)) {
				Filter.getNewEntities().setAll(newEntities);
				Filter.getSettings().setShowOnlyNewEntities(true);
			}
			
			Filter.refresh();
			Reload.start();
		}
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
		return getDirectoryCache(Project.getCurrent()) + File.separator + entity.getName() + "-" + entity.getLength() + ".jpg";
	}
	public static FileType getType(Entity entity) {
		String ext = entity.getName().toLowerCase().substring(entity.getName().lastIndexOf('.'));
		
		for (String _ext : EXTENSIONS_IMAGE) {
			if (ext.equals(_ext)) {
				return FileType.IMAGE;
			}
		}
		for (String _ext : EXTENSIONS_GIF) {
			if (ext.equals(_ext)) {
				return FileType.GIF;
			}
		}
		for (String _ext : EXTENSIONS_VIDEO) {
			if (ext.equals(_ext)) {
				return FileType.VIDEO;
			}
		}
		
		throw new RuntimeException("FileType " + ext.toUpperCase() + " is not supported");
	}
	
	public static String getDirectoryLocal() {
		return System.getenv("APPDATA") + File.separator + APPLICATION_NAME;
	}
	public static String getDirectoryProject() {
		return getDirectoryLocal() + File.separator + "data";
	}
	public static String getDirectoryCache(Project project) {
		return getDirectoryLocal() + File.separator + "cache" + File.separator + project.getProjectName();
	}
	public static String getFileSettings() {
		return getDirectoryLocal() + File.separator + "settings.json";
	}
	
	public static String getApplicationName() {
		return APPLICATION_NAME;
	}
	
	public enum FileType {
		IMAGE,
		GIF,
		VIDEO,
	}
}
