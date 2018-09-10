package project.database.loader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.Main;
import project.database.control.DataControl;
import project.database.control.TagControl;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.gui.GUIInstance;
import project.gui.component.gallerypane.GalleryTile;
import project.gui.custom.specific.LoadingWindow;
import project.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DataLoader extends Thread {
    /* imports */
    private final double GALLERY_ICON_MAX_SIZE = Settings.getGalleryIconSizeMax();

    private final String PATH_MAINDIR = Settings.getMainDirectoryPath();
    private final String PATH_IMAGECACHE = Settings.getImageCacheDirectoryPath();
    private final String PATH_DATABASECACHE = Settings.getDatabaseCacheFilePath();

    private final DataCollection dataCollection = DataControl.getCollection();

    /* vars */
    private int fileCount = 0;
    private int currentObjectIndex = 0;
    private ArrayList<File> validFiles;

    @Override
    public void run() {
        initialization();
        deserialization();
        validation();
        finalization();
        TagControl.initialize();
    }

    /* private */
    private void initialization() {
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(PATH_MAINDIR).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        File imageCacheDirectory = new File(PATH_IMAGECACHE);
        if (!imageCacheDirectory.exists()) {
            imageCacheDirectory.mkdir();
        }
    }
    private void deserialization() {
        if (!new File(PATH_DATABASECACHE).exists() || !DataControl.addAll(Serialization.readFromDisk())) {
            createDatabaseCache();
        }
    }
    private void validation() {
        ArrayList<String> dataObjectsItemNames = new ArrayList<>();
        ArrayList<String> validFilesItemNames = new ArrayList<>();

        for (Object dataObject : dataCollection) {
            dataObjectsItemNames.add(((DataObject) dataObject).getName());
        }
        for (File file : validFiles) {
            validFilesItemNames.add(file.getName());
        }

        dataObjectsItemNames.sort(Comparator.naturalOrder());
        validFilesItemNames.sort(Comparator.naturalOrder());

        if (!dataObjectsItemNames.equals(validFilesItemNames)) {
            validateDatabaseCache(dataObjectsItemNames, validFilesItemNames);
        }
    }
    private void finalization() {
        for (Object iterator : dataCollection) {
            DataObject dataObject = (DataObject) iterator;
            dataObject.setImage(getImageFromDataObject(dataObject));
            dataObject.setGalleryTile(new GalleryTile(dataObject));
        }

        Platform.runLater(() -> {
            Main.getLoadingWindow().close();
            GUIInstance.initialize();
            Main.setStage(GUIInstance.getInstance());
        });
    }

    private void createDatabaseCache() {
        for (File file : validFiles) {
            DataObject dataObject = createDataObjectFromFile(file);
            DataControl.add(dataObject);
        }

        Serialization.writeToDisk();
    }
    private void validateDatabaseCache(ArrayList<String> dataObjectsItemNames, ArrayList<String> validFilesItemNames) {
        /* add unrecognized items */
        for (File file : validFiles) {
            if (!dataObjectsItemNames.contains(file.getName())) {
                dataCollection.add(createDataObjectFromFile(file));
            }
        }

        /* remove missing items */
        DataCollection temporaryList = new DataCollection(dataCollection);
        for (DataObject dataObject : dataCollection) {
            if (!validFilesItemNames.contains(dataObject.getName())) {
                temporaryList.remove(dataObject);
            }
        }

        dataCollection.clear();
        dataCollection.addAll(temporaryList);
        Serialization.writeToDisk();
    }

    private Image getImageFromDataObject(DataObject dataObject) {
        Image currentObjectImage = null;
        currentObjectIndex++;

        String currentObjectName = dataObject.getName();
        String currentObjectCachePath = PATH_IMAGECACHE + "/" + currentObjectName;
        File currentObjectCacheFile = new File(currentObjectCachePath);

        /* write image cache to disk if not present */
        if (!currentObjectCacheFile.exists()) {
            try {
                currentObjectCacheFile.createNewFile();
                String currentObjectFilePath = "file:" + PATH_MAINDIR + "/" + currentObjectName;
                currentObjectImage = new Image(currentObjectFilePath, GALLERY_ICON_MAX_SIZE, GALLERY_ICON_MAX_SIZE, false, true);
                String currentObjectExtension = FilenameUtils.getExtension(currentObjectName);
                BufferedImage currentObjectBufferedImage = SwingFXUtils.fromFXImage(currentObjectImage, null);
                ImageIO.write(currentObjectBufferedImage, currentObjectExtension, currentObjectCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* update loading window label */
        if (Main.getLoadingWindow().getClass().equals(LoadingWindow.class)) {
            Platform.runLater(() -> Main.getLoadingWindow().getProgressLabel().setText("Loading item " + currentObjectIndex + " of " + fileCount + ", " + currentObjectIndex * 100 / fileCount + "% done"));
        }

        return Objects.requireNonNullElseGet(currentObjectImage, () -> new Image("file:" + currentObjectCachePath, GALLERY_ICON_MAX_SIZE, GALLERY_ICON_MAX_SIZE, false, true));
    }
    private DataObject createDataObjectFromFile(File file) {
        return new DataObject(file.getName(), new TagCollection());
    }
}
