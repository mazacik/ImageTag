package database.object;

import control.filter.FilterTemplate;
import control.reload.Reload;
import database.list.DataObjectList;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import settings.SettingsEnum;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.scene.SceneUtil;
import user_interface.singleton.center.BaseTile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DataLoader extends Thread implements InstanceRepo {
    private final String PATH_SOURCE = settings.getCurrentDirectory();
    private final String PATH_CACHE = PATH_SOURCE + "cache\\";
    private final String PATH_DATA = PATH_SOURCE + "data\\";

    public void run() {
        logger.debug(this, "loader thread start");
        Platform.runLater(SceneUtil::showMainScene);

        checkDirectoryPaths();
        //importFiles();
        ArrayList<File> fileList = getValidFiles(PATH_SOURCE);

        try {
            mainDataList.addAll(mainDataList.readFromDisk());
        } catch (NullPointerException e) {
            logger.debug(this, "existing database does not exist or failed to load");
            createBackup();
            createDatabase(fileList);
        }

        validateDatabaseCache(fileList);
        loadImageCache(fileList.size());

        mainDataList.sort();
        mainDataList.writeToDisk();
        mainInfoList.initialize();
        filter.addAll(mainDataList);

        filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
        reload.notifyChangeIn(Reload.Control.values());

        //Platform.runLater(SceneUtil::showMainScene);
        logger.debug(this, "loader thread end");
    }

    private void importFiles() {
        ArrayList<String> importDirectories = CommonUtil.settings.getImportDirList();
        importDirectories.forEach(dir -> {
            logger.debug(this, "importing files from " + dir);
            int importCounter = 0;
            for (File file : getValidFiles(dir)) {
                try {
                    Files.copy(Paths.get(file.getAbsolutePath()), Paths.get((PATH_SOURCE) + file.getName()));
                    logger.debug(this, "imported " + file.getName());
                    importCounter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logger.debug(this, "imported " + importCounter + " files");
        });
    }
    private ArrayList<File> getValidFiles(String directory) {
        return new ArrayList<>(Arrays.asList(new File(directory).listFiles((dir, name) -> {
            String _name = name.toLowerCase();
            return _name.endsWith(".jpg") || _name.endsWith(".jpeg") || _name.endsWith(".png");
        })));
    }
    private void checkDirectoryPaths() {
        logger.debug(this, "checking directories");
        File path_data = new File(PATH_DATA);
        if (!path_data.exists()) {
            logger.debug(this, "creating data directory: " + PATH_DATA);
            path_data.mkdir();
        }

        File path_cache = new File(PATH_CACHE);
        if (!path_cache.exists()) {
            logger.debug(this, "creating cache directory: " + PATH_CACHE);
            path_cache.mkdir();
        }
    }
    private void createBackup() {
        File backupDir = new File(PATH_SOURCE + "backup\\");
        File backupData = new File(backupDir + "data.json");
        File currentData = new File(PATH_DATA + "data.json");

        if (currentData.exists()) {
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            logger.debug(this, "database backup created");
            currentData.renameTo(backupData);
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
    private void validateDatabaseCache(ArrayList<File> fileList) {
        logger.debug(this, "validating database");
        ArrayList<String> dataObjectNames = new ArrayList<>();
        ArrayList<String> fileListNames = new ArrayList<>();

        mainDataList.forEach(dataObject -> dataObjectNames.add(dataObject.getName()));
        fileList.forEach(file -> fileListNames.add(file.getName()));

        dataObjectNames.sort(Comparator.naturalOrder());
        fileListNames.sort(Comparator.naturalOrder());

        /* addAll unrecognized items */
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
    private void loadImageCache(int fileListSize) {
        logger.debug(this, "loading image cache");
        final int galleryIconSizeMax = settings.intValueOf(SettingsEnum.TILEVIEW_ICONSIZE);
        int currentObjectIndex = 1;
        Image thumbnail;

        for (DataObject dataObject : mainDataList) {
            //updateLoadingLabel(fileListSize, currentObjectIndex++);
            thumbnail = getImageFromDataObject(dataObject, galleryIconSizeMax);
            dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));
            Platform.runLater(() -> tileView.getTilePane().getChildren().add(dataObject.getBaseTile()));
        }
    }
    private void updateLoadingLabel(int fileCount, int currentObjectIndex) {
        if (mainStage.getScene() != null) {
            Platform.runLater(() -> SceneUtil.getLoadingScene().getProgressLabel().setText("Loading item " + currentObjectIndex + " of " + fileCount + ", " + currentObjectIndex * 100 / fileCount + "% done"));
        }
    }

    private Image getImageFromDataObject(DataObject dataObject, double galleryIconMaxSize) {
        Image currentObjectImage = null;

        String currentObjectName = dataObject.getName();
        String currentObjectCachePath = PATH_CACHE + "/" + currentObjectName;
        File currentObjectCacheFile = new File(currentObjectCachePath);

        /* writeJSON image cache to disk if not present */
        if (!currentObjectCacheFile.exists()) {
            try {
                logger.debug(this, "cached image for " + dataObject.getName() + " not found, generating");
                currentObjectCacheFile.createNewFile();
                String currentObjectFilePath = "file:" + PATH_SOURCE + currentObjectName;
                currentObjectImage = new Image(currentObjectFilePath, galleryIconMaxSize, galleryIconMaxSize, false, true);
                String currentObjectExtension = FilenameUtils.getExtension(currentObjectName);
                BufferedImage currentObjectBufferedImage = SwingFXUtils.fromFXImage(currentObjectImage, null);
                ImageIO.write(currentObjectBufferedImage, currentObjectExtension, currentObjectCacheFile);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return Objects.requireNonNullElseGet(currentObjectImage, () -> new Image("file:" + currentObjectCachePath, galleryIconMaxSize, galleryIconMaxSize, false, true));
    }
}
