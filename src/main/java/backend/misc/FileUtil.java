package backend.misc;

import backend.list.BaseList;
import backend.list.entity.Entity;
import com.sun.jna.platform.FileUtils;
import frontend.stage.SimpleMessageStage;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import main.Main;

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
	
	public static final String EXT_CACHE = "jpg";
	
	public static final String[] EXT_IMG = new String[]{"jpg", "jpeg", "png"};
	public static final String[] EXT_GIF = new String[]{"gif"};
	public static final String[] EXT_VID = new String[]{"mp4", "m4v", "mov", "wmv", "avi", "webm"};
	
	public static String directoryChooser(Scene ownerScene) {
		if (ownerScene == null || ownerScene.getWindow() == null) throw new NullPointerException();
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose a Directory");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File directory = directoryChooser.showDialog(ownerScene.getWindow());
		
		if (directory == null) return "";
		
		return directory.getAbsolutePath();
	}
	
	public static boolean copyFile(String from, String to) {
		if (Main.DEBUG_FS_ALLOW_FILE_MOVE) {
			try {
				new File(to).getParentFile().mkdirs();
				Files.copy(Paths.get(from), Paths.get(to));
				return new File(from).length() == new File(to).length();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	public static boolean moveFile(String from, String to) {
		if (Main.DEBUG_FS_ALLOW_FILE_MOVE) {
			try {
				new File(to).getParentFile().mkdirs();
				Files.move(Paths.get(from), Paths.get(to));
				return !new File(from).exists() && new File(to).exists();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
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
						for (File _abstractFile : new BaseList<>(abstractFile.listFiles())) {
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
			return path.substring(path.lastIndexOf(".") + 1);
		}
		return path;
	}
	
	private static final FileFilter fileFilter = file -> {
		if (file.isDirectory()) {
			return true;
		}
		
		String fileName = file.getName().toLowerCase();
		for (String ext : EXT_IMG) {
			if (fileName.endsWith(ext)) {
				return true;
			}
		}
		for (String ext : EXT_GIF) {
			if (fileName.endsWith(ext)) {
				return true;
			}
		}
		for (String ext : EXT_VID) {
			if (fileName.endsWith(ext)) {
				return true;
			}
		}
		
		return false;
	};
	public static BaseList<File> getFiles(File directory, boolean useFilter) {
		BaseList<File> actualFiles = new BaseList<>();
		BaseList<File> abstractFiles;
		
		if (useFilter) {
			abstractFiles = new BaseList<>(directory.listFiles(fileFilter));
		} else {
			abstractFiles = new BaseList<>(directory.listFiles());
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
	
	public static BaseList<Project> getProjects() {
		BaseList<Project> projects = new BaseList<>();
		FileUtil.getProjectFiles().forEach(projectFile -> projects.addImpl(Project.readFromDisk(projectFile.getAbsolutePath())));
		return projects;
	}
	public static BaseList<File> getProjectFiles() {
		return new BaseList<>(new File(FileUtil.getDirectoryProject()).listFiles(file -> file.isFile() && file.getName().endsWith(".json")));
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
		return getDirectoryCache(Project.getCurrent().getProjectName()) + File.separator + entity.getName() + "-" + entity.getSize() + "." + EXT_CACHE;
	}
	public static EntityType getMediaType(Entity entity) {
		String ext = entity.getName().toLowerCase().substring(entity.getName().lastIndexOf('.') + 1);
		
		for (String _ext : EXT_IMG) {
			if (ext.equals(_ext)) {
				return EntityType.IMG;
			}
		}
		for (String _ext : EXT_GIF) {
			if (ext.equals(_ext)) {
				return EntityType.GIF;
			}
		}
		for (String _ext : EXT_VID) {
			if (ext.equals(_ext)) {
				return EntityType.VID;
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
