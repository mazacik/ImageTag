package database.loader;

import control.filter.FilterManager;
import control.reload.Reload;
import database.list.DataObjectList;
import database.list.DataObjectListMain;
import database.object.DataObject;
import javafx.application.Platform;
import database.loader.cache.CacheReader;
import lifecycle.InstanceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class LoaderThread extends Thread {
    public void run() {
        initDirs();
        ArrayList<File> fileList = LoaderUtil.getSupportedFiles(DirectoryUtil.getPathSource());

        try {
            InstanceManager.getMainDataList().addAll(DataObjectListMain.readFromDisk());
        } catch (Exception e) {
            InstanceManager.getLogger().debug(this, "existing database failed to load or does not exist");
            createBackup();
            createDatabase(fileList);
        }

        checkDatabase(fileList);

        LoaderUtil.initDataObjectPaths(InstanceManager.getMainDataList(), fileList);

        InstanceManager.getMainDataList().sort();
        InstanceManager.getMainInfoList().initialize();
        InstanceManager.getMainDataList().writeToDisk();
        InstanceManager.getFilter().addAll(InstanceManager.getMainDataList());

        if (InstanceManager.getMainDataList().size() > 0) InstanceManager.getTarget().set(InstanceManager.getMainDataList().get(0));

        Platform.runLater(() -> {
            InstanceManager.getTagListViewL().reload();
            InstanceManager.getTagListViewR().reload();
        });

        CacheReader.readCache(InstanceManager.getMainDataList());

        InstanceManager.getReload().notifyChangeIn(Reload.Control.DATA);
        Platform.runLater(InstanceManager.getReload()::doReload);
    }

    private void initDirs() {
        InstanceManager.getLogger().debug(this, "checking directories");

        String pathCacheDump = DirectoryUtil.getPathCacheDump();
        String pathCache = DirectoryUtil.getPathCacheProject();
        String pathData = DirectoryUtil.getPathData();

        File dirCacheDump = new File(pathCacheDump);
        if (!dirCacheDump.exists()) {
            InstanceManager.getLogger().debug(this, "creating cache dump directory: " + pathCacheDump);
            dirCacheDump.mkdirs();
        }
        try {
            Files.setAttribute(Paths.get(pathCacheDump), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dirCache = new File(pathCache);
        if (!dirCache.exists()) {
            InstanceManager.getLogger().debug(this, "creating cache directory: " + pathCache);
            dirCache.mkdirs();
        }
        File dirData = new File(pathData);
        if (!dirData.exists()) {
            InstanceManager.getLogger().debug(this, "creating data directory: " + pathData);
            dirData.mkdirs();
        }
        try {
            Files.setAttribute(Paths.get(pathData), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createBackup() {
        String dataDir = DirectoryUtil.getPathData();
        String backupPath = dataDir + "backup_" + new Date().toString() + ".json";
        String currentDataPath = dataDir + "data.json";

        File backupFile = new File(backupPath);
        File currentDataFile = new File(currentDataPath);

        if (currentDataFile.exists()) {
            currentDataFile.renameTo(backupFile);
            InstanceManager.getLogger().debug(this, "database backup created");
        } else {
            InstanceManager.getLogger().debug(this, "database backup not created - database does not exist");
        }
    }
    private void createDatabase(ArrayList<File> fileList) {
        InstanceManager.getLogger().debug(this, "creating database");
        for (File file : fileList) {
            InstanceManager.getMainDataList().add(new DataObject(file));
        }
    }
    private void checkDatabase(ArrayList<File> fileList) {
        InstanceManager.getLogger().debug(this, "validating database");
        ArrayList<String> dataObjectNames = new ArrayList<>();
        ArrayList<String> fileListNames = new ArrayList<>();

        InstanceManager.getMainDataList().forEach(dataObject -> dataObjectNames.add(dataObject.getName()));
        fileList.forEach(file -> fileListNames.add(file.getName()));

        dataObjectNames.sort(Comparator.naturalOrder());
        fileListNames.sort(Comparator.naturalOrder());

        /* add unrecognized items */
        int added = 0;
        InstanceManager.getLogger().debug(this, "looking for new data objects");
        for (File file : fileList) {
            if (!dataObjectNames.contains(file.getName())) {
                InstanceManager.getLogger().debug(this, "adding " + file.getName());
                DataObject dataObject = new DataObject(file);
                InstanceManager.getMainDataList().add(dataObject);
                FilterManager.addNewDataObject(dataObject);
                added++;
            }
        }
        InstanceManager.getLogger().debug(this, "newItems " + added + " files");

        /* discard missing items */
        int removed = 0;
        InstanceManager.getLogger().debug(this, "looking for orphan data objects");
        DataObjectList temporaryList = new DataObjectList();
        temporaryList.addAll(InstanceManager.getMainDataList());
        for (DataObject dataObject : InstanceManager.getMainDataList()) {
            String objectName = dataObject.getName();
            if (!fileListNames.contains(objectName)) {
                InstanceManager.getLogger().debug(this, "discarding " + objectName);
                temporaryList.remove(dataObject);
                removed++;
            }
        }
        InstanceManager.getLogger().debug(this, "discarded " + removed + " files");

        InstanceManager.getMainDataList().clear();
        InstanceManager.getMainDataList().addAll(temporaryList);
    }
}
