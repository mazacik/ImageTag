package database.loader;

import database.list.ObjectList;
import database.list.ObjectListMain;
import database.object.DataObject;
import javafx.application.Platform;
import main.InstanceManager;
import userinterface.main.side.FilterPane;
import userinterface.main.side.SelectPane;
import utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class LoaderThread extends Thread {
	public void run() {
		ArrayList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getDirSource());
		
		try {
			InstanceManager.getObjectListMain().addAll(ObjectListMain.readFromDisk());
		} catch (Exception e) {
			InstanceManager.getLogger().debug("existing database failed to load or does not exist");
			createDatabase(fileList);
		}
		
		checkDatabase(fileList);
		FileUtil.initDataObjectPaths(fileList);
		
		InstanceManager.getObjectListMain().sort();
		InstanceManager.getTagListMain().initialize();
		InstanceManager.getObjectListMain().writeToDisk();
		InstanceManager.getTagListMain().writeDummyToDisk();
		InstanceManager.getFilter().refresh();
		InstanceManager.getTarget().set(InstanceManager.getFilter().get(0));
		InstanceManager.getSelect().set(InstanceManager.getFilter().get(0));
		
		Platform.runLater(() -> {
			FilterPane filterPane = InstanceManager.getFilterPane();
			filterPane.reload();
			filterPane.collapseAll();
			
			SelectPane selectPane = InstanceManager.getSelectPane();
			selectPane.reload();
			selectPane.collapseAll();
			
			InstanceManager.getToolbarPane().reload();
		});
		
		ThumbnailReader.readThumbnails(InstanceManager.getObjectListMain());
	}
	
	private void createDatabase(ArrayList<File> fileList) {
		for (File file : fileList) {
			InstanceManager.getObjectListMain().add(new DataObject(file));
		}
	}
	private void checkDatabase(ArrayList<File> fileList) {
		ObjectList objectList = InstanceManager.getObjectListMain();
		objectList.sort(Comparator.comparing(DataObject::getName));
		fileList.sort(Comparator.comparing(File::getName));
		
		ArrayList<DataObject> orphanObjects = new ArrayList<>(objectList);
		ArrayList<File> newFiles = new ArrayList<>(fileList);
		
		/* compare files in the working directory with knwon objects in the database */
		for (DataObject dataObject : objectList) {
			for (File file : fileList) {
				//complexity n^2, optimizable
				if (dataObject.getName().equals(file.getName())) {
					newFiles.remove(file);
					orphanObjects.remove(dataObject);
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (File file : new ArrayList<>(newFiles)) {
			for (DataObject dataObject : new ArrayList<>(orphanObjects)) {
				if (file.length() == dataObject.getSize()) {
					newFiles.remove(file);
					orphanObjects.remove(dataObject);
					
					/* rename the object and cache file */
					File oldCacheFile = new File(dataObject.getCacheFile());
					dataObject.setName(file.getName());
					File newCacheFile = new File(dataObject.getCacheFile());
					
					if (oldCacheFile.exists() && !newCacheFile.exists()) {
						oldCacheFile.renameTo(newCacheFile);
					}
				}
			}
		}
		
		/* add unrecognized objects */
		for (File file : newFiles) {
			DataObject dataObject = new DataObject(file);
			objectList.add(dataObject);
			InstanceManager.getFilter().getCurrentSessionObjects().add(dataObject);
		}
		
		/* discard orphan objects */
		for (DataObject dataObject : orphanObjects) {
			objectList.remove(dataObject);
		}
		
		objectList.sort(Comparator.comparing(DataObject::getName));
	}
}
