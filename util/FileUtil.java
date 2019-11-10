package application.util;

import application.base.CustomList;
import application.base.entity.Entity;
import application.base.entity.EntityList;
import application.cache.CacheWriter;
import application.main.InstanceCollector;
import application.stage.StageManager;
import application.stage.template.YesNoCancelStage;
import application.util.enums.FileType;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.logging.Logger;

public abstract class FileUtil implements InstanceCollector {
	private static final String[] imageExtensions = new String[]{
			".jpg",
			".jpeg",
			".png",
			};
	private static final String[] gifExtensions = new String[]{
			".gif",
			};
	private static final String[] videoExtensions = new String[]{
			".mp4",
			".m4v",
			".mov",
			".wmv",
			".avi",
			".webm",
			};
	private static String projectDirSource;
	private static String projectFileData;
	private static String projectFileTags;
	private static String projectDirCache;
	
	private static CustomList<String> validExtensions = new CustomList<>();
	
	public static void init(String dirProject, String dirSource) {
		String separator = File.separator;
		if (!dirProject.endsWith(separator)) dirProject += separator;
		if (!dirSource.endsWith(separator)) dirSource += separator;
		
		projectDirSource = dirSource;
		projectFileData = dirProject + "data.json";
		projectFileTags = dirProject + "tags.json";
		projectDirCache = dirProject + "cache" + separator;
		
		File dirCache = new File(projectDirCache);
		if (!dirCache.exists()) {
			dirCache.mkdirs();
		}
		
		validExtensions.addAll(imageExtensions);
		validExtensions.addAll(gifExtensions);
		validExtensions.addAll(videoExtensions);
	}
	
	public static String directoryChooser(Scene ownerScene, String initialDirectory) {
		if (ownerScene == null || ownerScene.getWindow() == null) throw new NullPointerException();
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose a Directory");
		directoryChooser.setInitialDirectory(new File(initialDirectory));
		File directory = directoryChooser.showDialog(ownerScene.getWindow());
		
		if (directory == null) return "";
		
		String result = directory.getAbsolutePath();
		char lastChar = result.charAt(result.length() - 1);
		if (lastChar != File.separatorChar) result += File.separatorChar;
		return result;
	}
	public static String directoryChooser(Scene ownerScene) {
		return directoryChooser(ownerScene, System.getProperty("user.dir"));
	}
	
	public static CustomList<File> getSupportedFiles(File directory) {
		CustomList<File> result = new CustomList<>();
		CustomList<File> abstractFiles = new CustomList<>(directory.listFiles(file -> {
			if (file.isDirectory()) {
				return true;
			}
			
			String fileName = file.getName().toLowerCase();
			for (String ext : validExtensions) {
				if (fileName.endsWith(ext)) {
					return true;
				}
			}
			
			return false;
		}));
		
		for (File abstractFile : abstractFiles) {
			if (abstractFile.isDirectory()) {
				result.addAll(FileUtil.getSupportedFiles(abstractFile));
			} else {
				result.add(abstractFile);
			}
		}
		
		return result;
	}
	public static CustomList<File> getSupportedFiles(String directory) {
		return getSupportedFiles(new File(directory));
	}
	
	public static void initEntityPaths(CustomList<File> fileList) {
		EntityList entities = entityListMain;
		if (entities.size() == fileList.size()) {
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = entities.get(i);
				File sourceFile = fileList.get(i);
				entity.setPath(sourceFile.getAbsolutePath());
				//entity.setLength(sourceFile.length());
			}
		} else {
			String error = "entities.size() != fileList.size()";
			Logger.getGlobal().warning(error);
		}
	}
	public static void importFiles() {
		CustomList<File> sourceDirFiles = getSupportedFiles(projectDirSource);
		fixDuplicateFileNames(sourceDirFiles);
		CustomList<String> sourceDirFileNames = new CustomList<>();
		sourceDirFiles.forEach(file -> sourceDirFileNames.add(file.getName()));
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a directory to import");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File directory = directoryChooser.showDialog(StageManager.getMainStage());
		
		EntityList newEntities = new EntityList();
		if (directory != null && directory.isDirectory()) {
			for (File file : getSupportedFiles(directory)) {
				String oldPath = file.getAbsolutePath();
				String newPath = projectDirSource + file.getName();
				if (sourceDirFileNames.contains(file.getName())) {
					String name = getFileNameWithoutExtension(file);
					String extension = getExtension(file).toLowerCase();
					int k = 1;
					String newFileName;
					do {
						newFileName = name + "_" + k++ + extension;
						newPath = projectDirSource + newFileName;
					}
					while (sourceDirFileNames.contains(newFileName));
				}
				try {
					Files.move(Paths.get(oldPath), Paths.get(newPath));
					sourceDirFileNames.add(new File(newPath).getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				newEntities.add(new Entity(new File(newPath)));
			}
		}
		
		if (newEntities.isEmpty()) {
			StageManager.getErrorStage().show("No valid files found.");
		} else {
			entityListMain.addAll(newEntities);
			entityListMain.sort();
			
			filter.getCurrentSessionEntities().addAll(newEntities);
			
			String msg = "Imported " + newEntities.size() + " files.\nWould you like to view the new files?";
			YesNoCancelStage.Result result = StageManager.getYesNoCancelStage().show(msg);
			if (result == YesNoCancelStage.Result.YES) {
				filter.setAll(newEntities);
				select.set(filter.getFirst());
				target.set(filter.getFirst());
			} else {
				filter.refresh();
			}
			
			reload.doReload();
			galleryPane.updateViewportTilesVisibility();
		}
	}
	
	public static void fixDuplicateFileNames(CustomList<File> fileList) {
		fileList.sort(Comparator.comparing(File::getName));
		File file1;
		File file2;
		int j;
		int sameName;
		for (int i = 0; i < fileList.size() - 1; i++) {
			sameName = 0;
			j = i + 1;
			file1 = fileList.get(i);
			file2 = fileList.get(j);
			while (file1.getName().equals(file2.getName())) {
				String path = getAbsulatePathWithoutExtension(file2);
				String extension = getExtension(file2).toLowerCase();
				int k = 1;
				File newFile;
				do {
					newFile = new File(path + "_" + k + extension);
					k++;
				} while (newFile.exists());
				Logger.getGlobal().info("Renaming " + file2.getAbsolutePath() + " to " + newFile.getAbsolutePath());
				file2.renameTo(newFile);
				sameName++;
				file2 = fileList.get(++j);
			}
			i += sameName;
		}
	}
	public static String getAbsulatePathWithoutExtension(File file) {
		String absolutePath = file.getAbsolutePath();
		int index = absolutePath.lastIndexOf('.');
		return absolutePath.substring(0, index);
	}
	public static String getFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf('.');
		return fileName.substring(0, index);
	}
	public static String getExtension(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf('.');
		return fileName.substring(index);
	}
	
	public static FileType getFileType(Entity entity) {
		String ext = entity.getName().toLowerCase().substring(entity.getName().lastIndexOf('.'));
		
		for (String _ext : FileUtil.getImageExtensions()) {
			if (ext.equals(_ext)) {
				return FileType.IMAGE;
			}
		}
		for (String _ext : FileUtil.getGifExtensions()) {
			if (ext.equals(_ext)) {
				return FileType.GIF;
			}
		}
		for (String _ext : FileUtil.getVideoExtensions()) {
			if (ext.equals(_ext)) {
				return FileType.VIDEO;
			}
		}
		
		throw new RuntimeException("FileType " + ext.toUpperCase() + " is not supported");
	}
	public static String getCacheFilePath(Entity entity) {
		return FileUtil.getProjectDirCache() + entity.getName() + "-" + entity.getLength() + CacheWriter.getCacheExtension();
	}
	
	public static String getProjectDirSource() {
		return projectDirSource;
	}
	public static String getProjectFileData() {
		return projectFileData;
	}
	public static String getProjectFileTags() {
		return projectFileTags;
	}
	public static String getProjectDirCache() {
		return projectDirCache;
	}
	
	public static String[] getImageExtensions() {
		return imageExtensions;
	}
	public static String[] getGifExtensions() {
		return gifExtensions;
	}
	public static String[] getVideoExtensions() {
		return videoExtensions;
	}
}
