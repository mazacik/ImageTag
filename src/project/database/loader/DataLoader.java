package project.database.loader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.MainUtil;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.component.gallerypane.GalleryTile;
import project.gui.custom.specific.LoadingWindow;
import project.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DataLoader extends Thread implements MainUtil {
    private final String PATH_SOURCE = Settings.getPath_source();
    private final String PATH_CACHE = Settings.getPath_cache();
    private final String PATH_DATA = Settings.getPath_data();

    private LoadingWindow loadingWindow;

    public void start(LoadingWindow loadingWindow) {
        log.out("spawning loader thread", this.getClass());
        this.loadingWindow = loadingWindow;
        super.start();
    }
    public void run() {
        log.out("starting loader thread", this.getClass());
        checkDirectoryPaths();
        ArrayList<File> fileList = new ArrayList<>(Arrays.asList(new File(PATH_SOURCE).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"))));
        if (!getExistingDatabase()) createDatabase(fileList);

        validateDatabaseCache(fileList);

        log.out("loading cache", this.getClass());
        int currentObjectIndex = 1;
        for (DataObject dataObject : mainData) {
            updateLoadingLabel(fileList.size(), currentObjectIndex++);
            dataObject.setImage(getImageFromDataObject(dataObject, Settings.getGalleryIconSizeMax()));
            dataObject.setGalleryTile(new GalleryTile(dataObject));
        }

        mainTags.initialize();
        reload.all(true);
        Platform.runLater(() -> {
            loadingWindow.close();
            customStage.show();
        });
    }

    private void checkDirectoryPaths() {
        File path_data = new File(PATH_DATA);
        if (!path_data.exists()) {
            log.out("creating data directory", this.getClass());
            path_data.mkdir();
        }

        File path_cache = new File(PATH_CACHE);
        if (!path_cache.exists()) {
            log.out("creating cache directory", this.getClass());
            path_cache.mkdir();
        }
    }
    private void createDatabase(ArrayList<File> fileList) {
        log.out("creating database", this.getClass());
        for (File file : fileList) {
            mainData.add(new DataObject(file));
        }

        Serialization.writeToDisk();
    }
    private void validateDatabaseCache(ArrayList<File> fileList) {
        log.out("validating database", this.getClass(), true);
        ArrayList<String> dataObjectNames = new ArrayList<>();
        ArrayList<String> fileListNames = new ArrayList<>();

        mainData.forEach(dataObject -> dataObjectNames.add(dataObject.getName()));
        fileList.forEach(file -> fileListNames.add(file.getName()));

        dataObjectNames.sort(Comparator.naturalOrder());
        fileListNames.sort(Comparator.naturalOrder());

        if (dataObjectNames.equals(fileListNames)) {
            log.out("... ok");
            return;
        } else {
            log.out("\n");
        }

        /* addAll unrecognized items */
        int added = 0;
        log.out("adding new data objects", this.getClass());
        for (File file : fileList) {
            if (!dataObjectNames.contains(file.getName())) {
                log.out("adding " + file.getName(), this.getClass());
                mainData.add(new DataObject(file));
                added++;
            }
        }
        log.out("added " + added + " files", this.getClass());

        /* discard missing items */
        int removed = 0;
        log.out("discarding orphan data objects", this.getClass());
        DataCollection temporaryList = new DataCollection(mainData);
        for (DataObject dataObject : mainData) {
            String objectName = dataObject.getName();
            if (!fileListNames.contains(objectName)) {
                log.out("discarding " + objectName, this.getClass());
                temporaryList.remove(dataObject);
                removed++;
            }
        }
        log.out("discarded " + removed + " files", this.getClass());

        mainData.clear();
        mainData.addAll(temporaryList);
        Serialization.writeToDisk();
    }
    private void updateLoadingLabel(int fileCount, int currentObjectIndex) {
        Platform.runLater(() -> loadingWindow.getProgressLabel().setText("Loading item " + currentObjectIndex + " of " + fileCount + ", " + currentObjectIndex * 100 / fileCount + "% done"));
    }
    private boolean getExistingDatabase() {
        return mainData.addAll(Serialization.readFromDisk());
    }

    private Image getImageFromDataObject(DataObject dataObject, double galleryIconMaxSize) {
        log.out("loading image of file " + dataObject.getName(), this.getClass());
        Image currentObjectImage = null;

        String currentObjectName = dataObject.getName();
        String currentObjectCachePath = PATH_CACHE + "/" + currentObjectName;
        File currentObjectCacheFile = new File(currentObjectCachePath);

        /* write image cache to disk if not present */
        if (!currentObjectCacheFile.exists()) {
            try {
                currentObjectCacheFile.createNewFile();
                String currentObjectFilePath = "file:" + PATH_SOURCE + "/" + currentObjectName;
                currentObjectImage = new Image(currentObjectFilePath, galleryIconMaxSize, galleryIconMaxSize, false, true);
                String currentObjectExtension = FilenameUtils.getExtension(currentObjectName);
                BufferedImage currentObjectBufferedImage = SwingFXUtils.fromFXImage(currentObjectImage, null);
                ImageIO.write(currentObjectBufferedImage, currentObjectExtension, currentObjectCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* update loading window label */
        return Objects.requireNonNullElseGet(currentObjectImage, () -> new Image("file:" + currentObjectCachePath, galleryIconMaxSize, galleryIconMaxSize, false, true));
    }
}
