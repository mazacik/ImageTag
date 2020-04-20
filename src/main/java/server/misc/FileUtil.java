package server.misc;

import client.ui.stage.SimpleMessageStage;
import com.sun.jna.platform.FileUtils;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import main.Main;
import server.base.CustomList;
import server.base.entity.Entity;
import server.enums.MediaType;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class FileUtil {
	public static final String APP_NAME = "Tagallery";
	public static final String DIR_NAME_CACHE = "cache";
	public static final String DIR_NAME_DATA = "data";
	public static final String FILE_NAME_SETTINGS = "settings.txt";
	
	public static final String EXTENSION_CACHE = ".jpg";
	
	public static final String[] EXTENSIONS_IMG = new String[]{".jpg", ".jpeg", ".png"};
	public static final String[] EXTENSIONS_GIF = new String[]{".gif"};
	public static final String[] EXTENSIONS_VID = new String[]{".mp4", ".m4v", ".mov", ".wmv", ".avi", ".webm"};
	
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
		if (Main.DEBUG_FS_ALLOW_FILE_MOVE) {
			try {
				new File(to).getParentFile().mkdirs();
				Files.move(Paths.get(from), Paths.get(to));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean deleteFile(String path) {
		if (Main.DEBUG_FS_ALLOW_FILE_DELETE) {
			int counterDone = 0;
			int counterFail = 0;
			File abstractFile = new File(path);
			
			SimpleMessageStage simpleMessageStage = null;
			
			if (abstractFile.exists()) {
				if (FileUtils.getInstance().hasTrash()) {
					if (abstractFile.isFile()) {
						try {
							FileUtils.getInstance().moveToTrash(new File[]{abstractFile});
							//if (!abstractFile.exists()) counterDone++;
							counterDone++;
						} catch (IOException e) {
							counterFail++;
							if (simpleMessageStage == null) {
								simpleMessageStage = new SimpleMessageStage("Error", "Failed to delete \"" + abstractFile.getAbsolutePath() + "\"");
								simpleMessageStage.show();
							} else {
								simpleMessageStage.setText("Failed to delete " + counterFail + " files.");
							}
						}
					} else {
						for (File _abstractFile : new CustomList<>(abstractFile.listFiles())) {
							deleteFile(_abstractFile.getAbsolutePath());
						}
					}
				} else {
					simpleMessageStage = new SimpleMessageStage("Error", "Delete failed: No OS-level recycle bin support");
					simpleMessageStage.show();
					Logger.getGlobal().severe("No OS-level recycle bin support");
				}
			}
			
			return counterDone != 0;
		} else {
			return false;
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
	public static String getFileExtension(Entity entity) {
		return getFileExtension(FileUtil.getFileEntity(entity));
	}
	public static String getFileExtension(String path) {
		if (path.contains(".")) {
			path = path.substring(path.lastIndexOf("."));
		}
		return path;
	}
	
	private static final FileFilter fileFilter = file -> {
		if (file.isDirectory()) {
			return true;
		}
		
		String fileName = file.getName().toLowerCase();
		for (String ext : EXTENSIONS_IMG) {
			if (fileName.endsWith(ext)) {
				return true;
			}
		}
		for (String ext : EXTENSIONS_GIF) {
			if (fileName.endsWith(ext)) {
				return true;
			}
		}
		for (String ext : EXTENSIONS_VID) {
			if (fileName.endsWith(ext)) {
				return true;
			}
		}
		
		return false;
	};
	public static CustomList<File> getFiles(File directory, boolean useFilter) {
		CustomList<File> actualFiles = new CustomList<>();
		CustomList<File> abstractFiles;
		
		if (useFilter) {
			abstractFiles = new CustomList<>(directory.listFiles(fileFilter));
		} else {
			abstractFiles = new CustomList<>(directory.listFiles());
		}
		
		for (File abstractFile : abstractFiles) {
			if (abstractFile.isDirectory()) {
				actualFiles.addAllImpl(FileUtil.getFiles(abstractFile, useFilter));
			} else {
				actualFiles.addImpl(abstractFile);
			}
		}
		
		return actualFiles;
	}
	
	public static CustomList<Project> getProjects() {
		CustomList<Project> projects = new CustomList<>();
		FileUtil.getProjectFiles().forEach(projectFile -> projects.addImpl(Project.readFromDisk(projectFile.getAbsolutePath())));
		return projects;
	}
	public static CustomList<File> getProjectFiles() {
		return new CustomList<>(new File(FileUtil.getDirectoryProject()).listFiles(file -> file.isFile() && file.getName().endsWith(".json")));
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
		return getDirectoryCache(Project.getCurrent().getProjectName()) + File.separator + entity.getName() + "-" + entity.getSize() + EXTENSION_CACHE;
	}
	public static MediaType getMediaType(Entity entity) {
		String ext = entity.getName().toLowerCase().substring(entity.getName().lastIndexOf('.'));
		
		for (String _ext : EXTENSIONS_IMG) {
			if (ext.equals(_ext)) {
				return MediaType.IMG;
			}
		}
		for (String _ext : EXTENSIONS_GIF) {
			if (ext.equals(_ext)) {
				return MediaType.GIF;
			}
		}
		for (String _ext : EXTENSIONS_VID) {
			if (ext.equals(_ext)) {
				return MediaType.VID;
			}
		}
		
		throw new RuntimeException("MediaType " + ext.toUpperCase() + " is not supported");
	}
	
	public static File getLastImportDirectory() {
		File lastImportDirectory = new File(Settings.IMPORT_LAST_PATH.getValue());
		if (lastImportDirectory.exists() && lastImportDirectory.isDirectory()) {
			return lastImportDirectory;
		} else {
			return new File(Settings.IMPORT_LAST_PATH.resetValue());
		}
	}
	
	public static String getDirectoryLocal() {
		return System.getenv("APPDATA") + File.separator + APP_NAME;
	}
	public static String getDirectoryProject() {
		return getDirectoryLocal() + File.separator + DIR_NAME_DATA;
	}
	public static String getDirectoryCache() {
		return getDirectoryCache(Project.getCurrent().getProjectName());
	}
	public static String getDirectoryCache(String projectName) {
		return getDirectoryLocal() + File.separator + DIR_NAME_CACHE + File.separator + projectName;
	}
	public static String getFileSettings() {
		return getDirectoryLocal() + File.separator + FILE_NAME_SETTINGS;
	}
}
