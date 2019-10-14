package application.data.loader;

import application.data.list.DataList;
import application.data.list.DataListMain;
import application.data.object.DataObject;
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
		
		boolean readSuccess = Instances.getDataListMain().addAll(DataListMain.readFromDisk());
		if (!readSuccess || !checkIntegrity()) {
			createBackup();
			createDatabase(fileList);
		}
		
		checkFileDifference(fileList);
		FileUtil.initDataObjectPaths(fileList);
		Instances.getDataListMain().sort();
		Instances.getTagListMain().initialize();
		Instances.getDataListMain().writeToDisk();
		Instances.getTagListMain().writeDummyToDisk();
		Instances.getFilter().refresh();
		Instances.getTarget().set(Instances.getFilter().get(0));
		Instances.getSelect().set(Instances.getFilter().get(0));
		
		for (DataObject dataObject : Instances.getDataListMain()) {
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
			Instances.getGalleryPane().updateViewportTilesVisibility();
		});
	}
	
	private boolean checkIntegrity() {
		for (DataObject dataObject : Instances.getDataListMain()) {
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
		DataListMain objectListMain = Instances.getDataListMain();
		objectListMain.clear();
		for (File file : fileList) {
			objectListMain.add(new DataObject(file));
		}
	}
	private void checkFileDifference(ArrayList<File> fileList) {
		DataList dataList = Instances.getDataListMain();
		dataList.sort(Comparator.comparing(DataObject::getName));
		fileList.sort(Comparator.comparing(File::getName));
		ArrayList<DataObject> orphanObjects = new ArrayList<>(dataList);
		ArrayList<File> newFiles = new ArrayList<>(fileList);
		/* compare files in the working directory with knwon objects in the application.database */
		for (DataObject dataObject : dataList) {
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
			dataList.add(dataObject);
			Instances.getFilter().getCurrentSessionObjects().add(dataObject);
		}
		/* discard orphan objects */
		for (DataObject dataObject : orphanObjects) {
			dataList.remove(dataObject);
		}
		dataList.sort(Comparator.comparing(DataObject::getName));
	}
}
