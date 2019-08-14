package application.misc;

import application.controller.Filter;
import application.controller.Select;
import application.controller.Target;
import application.database.list.DataObjectList;
import application.database.object.DataObject;
import application.gui.panes.center.GalleryTile;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class FileUtil {
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
	private static String dirSource;
	private static String fileData;
	private static String fileTags;
	private static String dirCache;
	
	public static void initialize(String dirProject, String dirSource) {
		String separator = File.separator;
		if (!dirProject.endsWith(separator)) dirProject += separator;
		if (!dirSource.endsWith(separator)) dirSource += separator;
		
		FileUtil.dirSource = dirSource;
		
		fileData = dirProject + "data.json";
		fileTags = dirProject + "tags.json";
		dirCache = dirProject + "cache" + separator;
		
		File _dirCache = new File(dirCache);
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
	
	public static ArrayList<File> getAllFiles(File directory) {
		ArrayList<File> allFiles = new ArrayList<>();
		ArrayList<File> currentDir = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.listFiles())));
		
		for (File abstractFile : currentDir) {
			if (abstractFile.isDirectory()) {
				allFiles.addAll(getAllFiles(abstractFile));
			} else {
				allFiles.add(abstractFile);
			}
		}
		
		return allFiles;
	}
	public static ArrayList<File> getSupportedFiles(String directory) {
		return getSupportedFiles(new File(directory));
	}
	public static ArrayList<File> getSupportedFiles(File directory) {
		ArrayList<String> validExtensions = new ArrayList<>();
		validExtensions.addAll(Arrays.asList(getImageExtensions()));
		validExtensions.addAll(Arrays.asList(getGifExtensions()));
		validExtensions.addAll(Arrays.asList(getVideoExtensions()));
		
		ArrayList<File> validFiles = new ArrayList<>();
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
	
	public static void initDataObjectPaths(ArrayList<File> fileList) {
		DataObjectList dataObjects = Instances.getObjectListMain();
		if (dataObjects.size() == fileList.size()) {
			for (int i = 0; i < dataObjects.size(); i++) {
				DataObject dataObject = dataObjects.get(i);
				File sourceFile = fileList.get(i);
				dataObject.setPath(sourceFile.getAbsolutePath());
				dataObject.setSize(sourceFile.length());
			}
		} else {
			String error = "dataObjects.size() != fileList.size()";
			Logger.getGlobal().warning(error);
		}
	}
	public static void importFiles() {
		ArrayList<File> sourceDirFiles = getSupportedFiles(dirSource);
		fixDuplicateFileNames(sourceDirFiles);
		ArrayList<String> sourceDirFileNames = new ArrayList<>();
		sourceDirFiles.forEach(file -> sourceDirFileNames.add(file.getName()));
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a directory to import");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File directory = directoryChooser.showDialog(Stages.getMainStage());
		
		DataObjectList newDataObjects = new DataObjectList();
		if (directory != null && directory.isDirectory()) {
			for (File file : getSupportedFiles(directory)) {
				String oldPath = file.getAbsolutePath();
				String newPath = dirSource + file.getName();
				if (sourceDirFileNames.contains(file.getName())) {
					String name = getFileNameWithoutExtension(file);
					String extension = getExtension(file).toLowerCase();
					int k = 1;
					String newFileName;
					do {
						newFileName = name + "_" + k++ + extension;
						newPath = dirSource + newFileName;
					}
					while (sourceDirFileNames.contains(newFileName));
				}
				try {
					Files.move(Paths.get(oldPath), Paths.get(newPath));
					sourceDirFileNames.add(new File(newPath).getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				newDataObjects.add(new DataObject(new File(newPath)));
			}
		}
		
		if (!newDataObjects.isEmpty()) {
			DataObjectList dataObjectListMain = Instances.getObjectListMain();
			Filter filter = Instances.getFilter();
			Select select = Instances.getSelect();
			Target target = Instances.getTarget();
			
			for (DataObject dataObject : newDataObjects) dataObject.setGalleryTile(new GalleryTile(dataObject, null));
			dataObjectListMain.addAll(newDataObjects);
			dataObjectListMain.sort();
			
			filter.getCurrentSessionObjects().addAll(newDataObjects);
			
			if (Stages.getYesNoStage()._show("Do you wish to show the imported files?")) {
				filter.setAll(newDataObjects);
				select.set(filter.getFirst());
				target.set(filter.getFirst());
			}
			
			Instances.getReload().doReload();
			Instances.getGalleryPane().loadCacheOfTilesInViewport();
		}
	}
	
	public static void fixDuplicateFileNames(ArrayList<File> fileList) {
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
	
	public static String getDirSource() {
		return dirSource;
	}
	public static String getFileData() {
		return fileData;
	}
	public static String getFileTags() {
		return fileTags;
	}
	public static String getDirCache() {
		return dirCache;
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
