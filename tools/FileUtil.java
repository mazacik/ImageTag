package tools;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import gui.stage.StageManager;
import gui.stage.template.ButtonBooleanValue;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import main.InstanceCollector;
import tools.enums.FileType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		projectDirSource = dirSource;
		projectFileData = dirProject + File.separator + "data.json";
		projectFileTags = dirProject + File.separator + "tags.json";
		projectDirCache = dirProject + File.separator + "cache";
		
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
		
		return directory.getAbsolutePath();
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
	
	public static void initEntityPaths() {
		String base = FileUtil.getProjectDirSource() + File.separator;
		for (Entity entity : entityListMain) {
			entity.setFilePath(base + entity.getName());
		}
	}
	public static void importFiles() {
		//todo fix
		if (true) return;
		
		CustomList<File> sourceDirFiles = getSupportedFiles(projectDirSource);
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
				String newPath = projectDirSource + File.separator;// + FileUtil.getNameForEntity();
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
			ButtonBooleanValue result = StageManager.getYesNoCancelStage().show(msg);
			if (result == ButtonBooleanValue.YES) {
				filter.setAll(newEntities);
				select.set(filter.getFirst());
				target.set(filter.getFirst());
			} else {
				filter.refresh();
			}
			
			reload.doReload();
		}
	}
	
	public static String getNameForEntity(File file) {
		return file.getAbsolutePath().substring((projectDirSource + File.separator).length());
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
		return FileUtil.getProjectDirCache() + File.separator + entity.getName() + "-" + entity.getLength() + ".jpg";
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
