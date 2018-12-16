package database.loader;

import control.reload.Reload;
import database.list.DataListMain;
import database.object.DataObject;
import gui.singleton.center.BaseTile;
import gui.template.specific.LoadingWindow;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import settings.Settings;
import utils.MainUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DataLoader extends Thread implements MainUtil {
    private final String PATH_SOURCE = Settings.getPath_source();
    private final String PATH_CACHE = Settings.getPath_cache();
    private final String PATH_DATA = Settings.getPath_data();

    public void run() {
        logger.out("loader thread start", this.getClass());
        final LoadingWindow[] loadingWindow = new LoadingWindow[1];
        Platform.runLater(() -> loadingWindow[0] = new LoadingWindow());

        checkDirectoryPaths();
        ArrayList<File> fileList = getValidFiles();
        if (!loadExistingDatabase()) createDatabase(fileList);
        validateDatabaseCache(fileList);
        loadImageCache(loadingWindow[0], fileList.size());

        dataListMain.sort();
        infoListMain.initialize();
        filter.addAll(dataListMain);
        filter.sort();

        reload.notifyChangeIn(Reload.Control.values());
        reload.doReload();

        Platform.runLater(() -> {
            loadingWindow[0].close();
            mainStage.show();
        });
        logger.out("loader thread end", this.getClass());
    }

    private ArrayList<File> getValidFiles() {
        return new ArrayList<>(Arrays.asList(new File(PATH_SOURCE).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"))));
    }
    private void checkDirectoryPaths() {
        logger.out("checking directories", this.getClass());
        File path_data = new File(PATH_DATA);
        if (!path_data.exists()) {
            logger.out("creating data directory: " + PATH_DATA, this.getClass());
            path_data.mkdir();
        }

        File path_cache = new File(PATH_CACHE);
        if (!path_cache.exists()) {
            logger.out("creating cache directory: " + PATH_CACHE, this.getClass());
            path_cache.mkdir();
        }
    }
    private void createDatabase(ArrayList<File> fileList) {
        logger.out("creating database", this.getClass());
        for (File file : fileList) {
            dataListMain.add(new DataObject(file));
        }

        Serialization.writeToDisk();
    }
    private void validateDatabaseCache(ArrayList<File> fileList) {
        logger.out("validating database", this.getClass(), true);
        ArrayList<String> dataObjectNames = new ArrayList<>();
        ArrayList<String> fileListNames = new ArrayList<>();

        dataListMain.forEach(dataObject -> dataObjectNames.add(dataObject.getName()));
        fileList.forEach(file -> fileListNames.add(file.getName()));

        dataObjectNames.sort(Comparator.naturalOrder());
        fileListNames.sort(Comparator.naturalOrder());

        if (dataObjectNames.equals(fileListNames)) {
            logger.out("... ok");
            return;
        } else {
            logger.out("\n");
        }

        /* addAll unrecognized items */
        int added = 0;
        logger.out("adding new data objects", this.getClass());
        for (File file : fileList) {
            if (!dataObjectNames.contains(file.getName())) {
                logger.out("adding " + file.getName(), this.getClass());
                dataListMain.add(new DataObject(file));
                added++;
            }
        }
        logger.out("added " + added + " files", this.getClass());

        /* discard missing items */
        int removed = 0;
        logger.out("discarding orphan data objects", this.getClass());
        DataListMain temporaryList = new DataListMain(dataListMain);
        for (DataObject dataObject : dataListMain) {
            String objectName = dataObject.getName();
            if (!fileListNames.contains(objectName)) {
                logger.out("discarding " + objectName, this.getClass());
                temporaryList.remove(dataObject);
                removed++;
            }
        }
        logger.out("discarded " + removed + " files", this.getClass());

        dataListMain.clear();
        dataListMain.addAll(temporaryList);
        Serialization.writeToDisk();
    }
    private void loadImageCache(LoadingWindow loadingWindow, int fileListSize) {
        logger.out("loading image cache", this.getClass());
        final int galleryIconSizeMax = Settings.getGalleryIconSizeMax();
        int currentObjectIndex = 1;
        Image thumbnail;

        for (DataObject dataObject : dataListMain) {
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
        return dataListMain.addAll(Serialization.readFromDisk());
    }

    private Image getImageFromDataObject(DataObject dataObject, double galleryIconMaxSize) {
        Image currentObjectImage = null;

        String currentObjectName = dataObject.getName();
        String currentObjectCachePath = PATH_CACHE + "/" + currentObjectName;
        File currentObjectCacheFile = new File(currentObjectCachePath);

        /* write image cache to disk if not present */
        if (!currentObjectCacheFile.exists()) {
            try {
                logger.out("cached image of file " + dataObject.getName() + " not found, generating", this.getClass());
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
