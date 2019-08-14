package application.database.loader;

import application.database.list.DataObjectList;
import application.database.list.DataObjectListMain;
import application.database.object.DataObject;
import application.gui.panes.center.GalleryTile;
import application.gui.panes.side.FilterPane;
import application.gui.panes.side.SelectPane;
import application.gui.stage.Stages;
import application.main.Instances;
import application.misc.FileUtil;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class LoaderThread extends Thread {
	public void run() {
		ArrayList<File> fileList = FileUtil.getSupportedFiles(FileUtil.getDirSource());
		FileUtil.fixDuplicateFileNames(fileList);
		
		boolean readSuccess = Instances.getObjectListMain().addAll(DataObjectListMain.readFromDisk());
		if (!readSuccess || !checkIntegrity()) {
			createBackup();
			createDatabase(fileList);
		}
		
		checkFileDifference(fileList);
		FileUtil.initDataObjectPaths(fileList);
		Instances.getObjectListMain().sort();
		Instances.getTagListMain().initialize();
		Instances.getObjectListMain().writeToDisk();
		Instances.getTagListMain().writeDummyToDisk();
		Instances.getFilter().refresh();
		Instances.getTarget().set(Instances.getFilter().get(0));
		Instances.getSelect().set(Instances.getFilter().get(0));
		
		for (DataObject dataObject : Instances.getObjectListMain()) {
			dataObject.setGalleryTile(new GalleryTile(dataObject, null));
		}
		
		Platform.runLater(() -> {
			FilterPane filterPane = Instances.getFilterPane();
			filterPane.reload();
			filterPane.collapseAll();
			
			SelectPane selectPane = Instances.getSelectPane();
			selectPane.reload();
			selectPane.collapseAll();
			
			Instances.getToolbarPane().reload();
			
			Instances.getReload().doReload();
			Instances.getGalleryPane().loadCacheOfTilesInViewport();
		});
	}
	
	private boolean checkIntegrity() {
		for (DataObject dataObject : Instances.getObjectListMain()) {
			if (dataObject.getName() == null || dataObject.getTagList() == null) {
				//the application.database is (most likely) corrupted or outdated
				AtomicBoolean createNew = new AtomicBoolean(false);
				FutureTask futureTask = new FutureTask<Boolean>(() -> {
					Logger.getGlobal().info("Database failed to load.");
					createNew.set(Stages.getOkCancelStage()._show("Database failed to load.\nCreate a new application.database?\nA backup will be created."));
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
		DataObjectListMain objectListMain = Instances.getObjectListMain();
		objectListMain.clear();
		for (File file : fileList) {
			objectListMain.add(new DataObject(file));
		}
	}
	private void checkFileDifference(ArrayList<File> fileList) {
		DataObjectList dataObjectList = Instances.getObjectListMain();
		dataObjectList.sort(Comparator.comparing(DataObject::getName));
		fileList.sort(Comparator.comparing(File::getName));
		ArrayList<DataObject> orphanObjects = new ArrayList<>(dataObjectList);
		ArrayList<File> newFiles = new ArrayList<>(fileList);
		/* compare files in the working directory with knwon objects in the application.database */
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
			Instances.getFilter().getCurrentSessionObjects().add(dataObject);
		}
		/* discard orphan objects */
		for (DataObject dataObject : orphanObjects) {
			dataObjectList.remove(dataObject);
		}
		dataObjectList.sort(Comparator.comparing(DataObject::getName));
	}
}
