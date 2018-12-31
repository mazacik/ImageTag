package database.object;

import control.reload.Reload;
import database.list.MainListData;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import settings.SettingsNamespace;
import userinterface.node.center.BaseTile;
import userinterface.template.specific.LoadingWindow;
import utils.MainUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DataLoader extends Thread implements MainUtil {
    private final String PATH_SOURCE = settings.getCurrentDirectory();
    private final String PATH_CACHE = PATH_SOURCE + "cache\\";
    private final String PATH_DATA = PATH_SOURCE + "data\\";

    public void run() {
        logger.debug(this, "loader thread start");
        final LoadingWindow[] loadingWindow = new LoadingWindow[1];
        Platform.runLater(() -> loadingWindow[0] = new LoadingWindow());

        checkDirectoryPaths();
        ArrayList<File> fileList = getValidFiles();
        if (!loadExistingDatabase()) createDatabase(fileList);
        validateDatabaseCache(fileList);
        loadImageCache(loadingWindow[0], fileList.size());

        mainListData.sort();
        mainListData.writeToDisk();
        mainListInfo.initialize();
        filter.addAll(mainListData);
        filter.sort();

        reload.notifyChangeIn(Reload.Control.values());
        reload.doReload();

        Platform.runLater(() -> {
            loadingWindow[0].close();
            mainStage.show();
        });
        logger.debug(this, "loader thread end");
    }

    private ArrayList<File> getValidFiles() {
        return new ArrayList<>(Arrays.asList(new File(PATH_SOURCE).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"))));
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
    private void createDatabase(ArrayList<File> fileList) {
        logger.debug(this, "creating database");
        for (File file : fileList) {
            mainListData.add(new DataObject(file));
        }
    }
    private void validateDatabaseCache(ArrayList<File> fileList) {
        logger.debug(this, "validating database");
        ArrayList<String> dataObjectNames = new ArrayList<>();
        ArrayList<String> fileListNames = new ArrayList<>();

        mainListData.forEach(dataObject -> dataObjectNames.add(dataObject.getName()));
        fileList.forEach(file -> fileListNames.add(file.getName()));

        dataObjectNames.sort(Comparator.naturalOrder());
        fileListNames.sort(Comparator.naturalOrder());

        /* addAll unrecognized items */
        int added = 0;
        logger.debug(this, "looking for new data objects");
        for (File file : fileList) {
            if (!dataObjectNames.contains(file.getName())) {
                logger.debug(this, "adding " + file.getName());
                mainListData.add(new DataObject(file));
                added++;
            }
        }
        logger.debug(this, "added " + added + " files");

        /* discard missing items */
        int removed = 0;
        logger.debug(this, "looking for orphan data objects");
        MainListData temporaryList = new MainListData();
        temporaryList.addAll(mainListData);
        for (DataObject dataObject : mainListData) {
            String objectName = dataObject.getName();
            if (!fileListNames.contains(objectName)) {
                logger.debug(this, "discarding " + objectName);
                temporaryList.remove(dataObject);
                removed++;
            }
        }
        logger.debug(this, "discarded " + removed + " files");

        mainListData.clear();
        mainListData.addAll(temporaryList);
    }
    private void loadImageCache(LoadingWindow loadingWindow, int fileListSize) {
        logger.debug(this, "loading image cache");
        final int galleryIconSizeMax = settings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE);
        int currentObjectIndex = 1;
        Image thumbnail;

        for (DataObject dataObject : mainListData) {
            updateLoadingLabel(loadingWindow, fileListSize, currentObjectIndex++);
            thumbnail = getImageFromDataObject(dataObject, galleryIconSizeMax);
            dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));
        }
    }
    private void updateLoadingLabel(LoadingWindow loadingWindow, int fileCount, int currentObjectIndex) {
        if (loadingWindow != null) {
            Platform.runLater(() -> loadingWindow.getProgressLabel().setText("Loading item " + currentObjectIndex + " of " + fileCount + ", " + currentObjectIndex * 100 / fileCount + "% done"));
        }
    }
    private boolean loadExistingDatabase() {
        return mainListData.addAll(mainListData.readFromDisk());
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
