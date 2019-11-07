package application.backend.util;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.loader.cache.CacheWriter;
import application.backend.util.enums.FileType;
import application.frontend.stage.StageManager;
import application.frontend.stage.template.YesNoCancelStage;
import application.main.InstanceCollector;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
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
	
	public static void initialize(String dirProject, String dirSource) {
		String separator = File.separator;
		if (!dirProject.endsWith(separator)) dirProject += separator;
		if (!dirSource.endsWith(separator)) dirSource += separator;
		
		FileUtil.projectDirSource = dirSource;
		
		projectFileData = dirProject + "data.json";
		projectFileTags = dirProject + "tags.json";
		projectDirCache = dirProject + "cache" + separator;
		
		File _dirCache = new File(projectDirCache);
		if (!_dirCache.exists()) _dirCache.mkdir();
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
	
	public static CustomList<File> getAllFiles(File directory) {
		CustomList<File> allFiles = new CustomList<>();
		CustomList<File> currentDir = new CustomList<>(Arrays.asList(Objects.requireNonNull(directory.listFiles())));
		
		for (File abstractFile : currentDir) {
			if (abstractFile.isDirectory()) {
				allFiles.addAll(getAllFiles(abstractFile));
			} else {
				allFiles.add(abstractFile);
			}
		}
		
		return allFiles;
	}
	public static CustomList<File> getSupportedFiles(String directory) {
		return getSupportedFiles(new File(directory));
	}
	public static CustomList<File> getSupportedFiles(File directory) {
		CustomList<String> validExtensions = new CustomList<>();
		validExtensions.addAll(Arrays.asList(getImageExtensions()));
		validExtensions.addAll(Arrays.asList(getGifExtensions()));
		validExtensions.addAll(Arrays.asList(getVideoExtensions()));
		
		CustomList<File> validFiles = new CustomList<>();
		for (File file : getAllFiles(directory)) {
			String fileName = file.getName();
			for (String ext : validExtensions) {
				if (fileName.toLowerCase().endsWith(ext)) {
					validFiles.add(file);
					break;
				}
			}
		}
		return validFiles;
	}
	
	public static void initEntityPaths(CustomList<File> fileList) {
		EntityList entities = entityListMain;
		if (entities.size() == fileList.size()) {
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = entities.get(i);
				File sourceFile = fileList.get(i);
				entity.setPath(sourceFile.getAbsolutePath());
				entity.setLength(sourceFile.length());
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
			StageManager.getErrorStage()._show("No valid files found.");
		} else {
			entityListMain.addAll(newEntities);
			entityListMain.sort();
			
			filter.getCurrentSessionEntities().addAll(newEntities);
			
			String msg = "Imported " + newEntities.size() + " files.\nWould you like to view the new files?";
			YesNoCancelStage.Result result = StageManager.getYesNoCancelStage()._show(msg);
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
