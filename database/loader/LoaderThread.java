package database.loader;

import database.list.DataObjectList;
import database.list.DataObjectListMain;
import database.object.DataObject;
import javafx.application.Platform;
import main.InstanceManager;
import userinterface.main.center.BaseTile;
import userinterface.main.side.FilterPane;
import userinterface.main.side.SelectPane;
import userinterface.stage.StageUtil;
import utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoaderThread extends Thread {
	public void run() {
		ArrayList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getDirSource());
		FileUtil.fixDuplicateFileNames(fileList);
		
		boolean readSuccess = InstanceManager.getObjectListMain().addAll(DataObjectListMain.readFromDisk());
		if (!readSuccess || !checkIntegrity()) {
			createBackup();
			createDatabase(fileList);
		}
		checkFileDifference(fileList);
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
		
		for (DataObject dataObject : InstanceManager.getObjectListMain()) {
			dataObject.setBaseTile(new BaseTile(dataObject, null));
		}
		
		Platform.runLater(() -> {
			InstanceManager.getGalleryPane().reload();
			InstanceManager.getGalleryPane().loadViewportCache();
		});
		
		//don't use foreach here, prone to ConcurrentModificationException from the I/O thread
		for (int i = 0; i < InstanceManager.getObjectListMain().size(); i++) {
			DataObject dataObject = InstanceManager.getObjectListMain().get(i);
			if (dataObject != null) ThumbnailReader.readThumbnail(dataObject);
		}
	}
	
	private boolean checkIntegrity() {
		for (DataObject dataObject : InstanceManager.getObjectListMain()) {
			if (dataObject.getName() == null || dataObject.getTagList() == null) {
				//the database is (most likely) corrupted or outdated
				AtomicBoolean createNew = new AtomicBoolean(false);
				FutureTask futureTask = new FutureTask<Boolean>(() -> {
					InstanceManager.getLogger().debug("Database failed to load.");
					createNew.set(StageUtil.showStageOkCancel("Database failed to load.\nCreate a new database?\nA backup will be created."));
				}, null);
				Platform.runLater(futureTask);
				try {
					futureTask.get();
				} catch (InterruptedException | ExecutionException ex) {
					ex.printStackTrace();
				}
				if (createNew.get()) {
					return false;
				} else {
					System.exit(1);
				}
			}
		}
		return true;
	}
	private void createBackup() {
		new File(FileUtil.getFileData()).renameTo(new File(FileUtil.getFileData() + "_backup"));
	}
	private void createDatabase(ArrayList<File> fileList) {
		DataObjectListMain objectListMain = InstanceManager.getObjectListMain();
		objectListMain.clear();
		for (File file : fileList) {
			objectListMain.add(new DataObject(file));
		}
	}
	private void checkFileDifference(ArrayList<File> fileList) {
		DataObjectList dataObjectList = InstanceManager.getObjectListMain();
		dataObjectList.sort(Comparator.comparing(DataObject::getName));
		fileList.sort(Comparator.comparing(File::getName));
		ArrayList<DataObject> orphanObjects = new ArrayList<>(dataObjectList);
		ArrayList<File> newFiles = new ArrayList<>(fileList);
		/* compare files in the working directory with knwon objects in the database */
		for (DataObject dataObject : dataObjectList) {
			for (int j = 0; j < newFiles.size(); j++) {
				File file = newFiles.get(j);
				if (dataObject.getName().equals(file.getName())) {
					orphanObjects.remove(dataObject);
					newFiles.remove(file);
					break;
				}
			}
		}
		/* match files with the exact same size, these were probably renamed outside of the application */
		//todo add a confirmation screen
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
			dataObjectList.add(dataObject);
			InstanceManager.getFilter().getCurrentSessionObjects().add(dataObject);
		}
		/* discard orphan objects */
		for (DataObject dataObject : orphanObjects) {
			dataObjectList.remove(dataObject);
		}
		dataObjectList.sort(Comparator.comparing(DataObject::getName));
	}
}
