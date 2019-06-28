package database.loader;

import database.list.ObjectList;
import database.list.ObjectListMain;
import database.object.DataObject;
import javafx.application.Platform;
import main.InstanceManager;
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
		boolean readSuccess = InstanceManager.getObjectListMain().addAll(ObjectListMain.readFromDisk());
		if (!readSuccess || !checkIntegrity()) createDatabase(fileList);
		
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
	
	private boolean checkIntegrity() {
		for (DataObject dataObject : InstanceManager.getObjectListMain()) {
			if (dataObject.getName() == null || dataObject.getTagList() == null) {
				//the database is (most likely) corrupted or outdated
				AtomicBoolean createNew = new AtomicBoolean(false);
				FutureTask futureTask = new FutureTask<Boolean>(() -> {
					InstanceManager.getLogger().debug("Database failed to load.");
					createNew.set(StageUtil.showStageOkCancel("Database failed to load. Create a new database?"));
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
	
	private void createDatabase(ArrayList<File> fileList) {
		ObjectListMain objectListMain = InstanceManager.getObjectListMain();
		objectListMain.clear();
		for (File file : fileList) {
			objectListMain.add(new DataObject(file));
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
