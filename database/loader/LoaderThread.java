package database.loader;

import control.filter.FilterTemplate;
import control.reload.Reload;
import database.list.DataObjectList;
import database.object.DataObject;
import javafx.application.Platform;
import system.InstanceRepo;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class LoaderThread extends Thread implements InstanceRepo {
    public void run() {
        initDirs();
        ArrayList<File> fileList = LoaderUtil.getValidFiles(LoaderUtil.getPathSource());

        try {
            mainDataList.addAll(mainDataList.readFromDisk());
        } catch (Exception e) {
            logger.debug(this, "existing database failed to load or does not exist");
            createBackup();
            createDatabase(fileList);
        }

        checkCache(fileList);
        LoaderUtil.readImageCache(mainDataList);

        mainDataList.sort();
        mainInfoList.initialize();
        mainDataList.writeToDisk();
        filter.addAll(mainDataList);

        if (mainDataList.size() > 0) target.set(mainDataList.get(0));

        filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
        reload.notifyChangeIn(Reload.Control.values());
        Platform.runLater(reload::doReload);
    }

    private void initDirs() {
        logger.debug(this, "checking directories");

        String pathCache = LoaderUtil.getPathCache();
        String pathData = LoaderUtil.getPathData();

        File dirCache = new File(pathCache);
        if (!dirCache.exists()) {
            logger.debug(this, "creating cache directory: " + pathCache);
            dirCache.mkdirs();
        }
        File dirData = new File(pathData);
        if (!dirData.exists()) {
            logger.debug(this, "creating data directory: " + pathData);
            dirData.mkdirs();
        }
    }
    private void createBackup() {
        String dataDir = LoaderUtil.getPathData();
        String backupPath = dataDir + "backup_" + new Date().toString() + ".json";
        String currentDataPath = dataDir + "data.json";

        File backupFile = new File(backupPath);
        File currentDataFile = new File(currentDataPath);

        if (currentDataFile.exists()) {
            currentDataFile.renameTo(backupFile);
            logger.debug(this, "database backup created");
        } else {
            logger.debug(this, "database backup not created - database does not exist");
        }
    }
    private void createDatabase(ArrayList<File> fileList) {
        logger.debug(this, "creating database");
        for (File file : fileList) {
            mainDataList.add(new DataObject(file));
        }
    }
    private void checkCache(ArrayList<File> fileList) {
        logger.debug(this, "validating database");
        ArrayList<String> dataObjectNames = new ArrayList<>();
        ArrayList<String> fileListNames = new ArrayList<>();

        mainDataList.forEach(dataObject -> dataObjectNames.add(dataObject.getName()));
        fileList.forEach(file -> fileListNames.add(file.getName()));

        dataObjectNames.sort(Comparator.naturalOrder());
        fileListNames.sort(Comparator.naturalOrder());

        /* add unrecognized items */
        int added = 0;
        logger.debug(this, "looking for new data objects");
        for (File file : fileList) {
            if (!dataObjectNames.contains(file.getName())) {
                logger.debug(this, "adding " + file.getName());
                mainDataList.add(new DataObject(file));
                added++;
            }
        }
        logger.debug(this, "added " + added + " files");

        /* discard missing items */
        int removed = 0;
        logger.debug(this, "looking for orphan data objects");
        DataObjectList temporaryList = new DataObjectList();
        temporaryList.addAll(mainDataList);
        for (DataObject dataObject : mainDataList) {
            String objectName = dataObject.getName();
            if (!fileListNames.contains(objectName)) {
                logger.debug(this, "discarding " + objectName);
                temporaryList.remove(dataObject);
                removed++;
            }
        }
        logger.debug(this, "discarded " + removed + " files");

        mainDataList.clear();
        mainDataList.addAll(temporaryList);
    }
}
