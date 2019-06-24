package utils;

import database.list.ObjectList;
import database.loader.Project;
import database.loader.ThumbnailReader;
import database.object.DataObject;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import lifecycle.InstanceManager;
import lifecycle.LifeCycleManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
		String result = directory.getAbsolutePath();
		if (!result.isEmpty()) {
			char lastChar = result.charAt(result.length() - 1);
			if (lastChar != File.separatorChar) result += File.separatorChar;
			return result;
		} else {
			return "";
		}
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
		ArrayList<String> validExtensions = new ArrayList<>();
		validExtensions.addAll(Arrays.asList(getImageExtensions()));
		validExtensions.addAll(Arrays.asList(getGifExtensions()));
		validExtensions.addAll(Arrays.asList(getVideoExtensions()));
		
		ArrayList<File> validFiles = new ArrayList<>();
		for (File file : getAllFiles(new File(directory))) {
			String fileName = file.getName();
			for (String ext : validExtensions) {
				if (fileName.endsWith(ext)) {
					validFiles.add(file);
					break;
				}
			}
		}
		
		return validFiles;
	}
	
	public static void initDataObjectPaths(ArrayList<File> fileList) {
		ObjectList dataObjects = InstanceManager.getObjectListMain();
		if (dataObjects.size() == fileList.size()) {
			for (int i = 0; i < dataObjects.size(); i++) {
				DataObject dataObject = dataObjects.get(i);
				File sourceFile = fileList.get(i);
				dataObject.setPath(sourceFile.getAbsolutePath());
				dataObject.setSize(sourceFile.length());
			}
		} else {
			String error = "dataObjects.size() != fileList.size()";
			InstanceManager.getLogger().error(error);
		}
	}
	
	public static void importFiles() {
		Project project = LifeCycleManager.getProject();
		String sourceDirectory = project.getSourceDirectoryList().get(0);
		
		ObjectList newDataObjects = new ObjectList();
		
		for (String dir : LifeCycleManager.getProject().getImportDirectoryList()) {
			for (File file : getSupportedFiles(dir)) {
				try {
					String oldPath = file.getAbsolutePath();
					String newPath = sourceDirectory + file.getName();
					Files.move(Paths.get(oldPath), Paths.get(newPath));
					newDataObjects.add(new DataObject(new File(newPath)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		ThumbnailReader.readThumbnails(newDataObjects);
		InstanceManager.getObjectListMain().addAll(newDataObjects);
		InstanceManager.getObjectListMain().sort();
		InstanceManager.getFilter().getCurrentSessionObjects().addAll(newDataObjects);
		InstanceManager.getFilter().refresh();
		InstanceManager.getReload().doReload();
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
