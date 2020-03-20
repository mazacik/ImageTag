package misc;

import base.CustomList;
import base.entity.Entity;
import com.sun.jna.platform.FileUtils;
import enums.MediaType;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import main.Main;
import ui.stage.SimpleMessageStage;

import java.io.File;
import java.io.FileFilter;
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
	
	private static FileFilter fileFilter = file -> {
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
	
	public static File getLastImportDirectory() {
		File lastImportDirectory = new File(Settings.IMPORT_LAST_PATH.getValue());
		if (lastImportDirectory.exists() && lastImportDirectory.isDirectory()) {
			return lastImportDirectory;
		} else {
			return new File(Settings.IMPORT_LAST_PATH.resetValue());
		}
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
		return getDirectoryLocal() + File.separator + "settings.txt";
	}
	
	public static String getApplicationName() {
		return APPLICATION_NAME;
	}
}
