package project.database.loader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.Main;
import project.common.Settings;
import project.database.DataElementDatabase;
import project.database.TagElementDatabase;
import project.database.element.DataElement;
import project.gui.GUIStage;
import project.gui.component.part.GalleryTile;
import project.gui.stage.LoadingWindow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DatabaseLoader extends Thread {
    /* imports */
    private final double galleryIconSizeMax = Settings.getGalleryIconSizeMax();
    private final String pathMainDirectory = Settings.getMainDirectoryPath();
    private final String pathImageCacheDirectory = Settings.getImageCacheDirectoryPath();
    private final String pathDatabaseCacheFile = Settings.getDatabaseCacheFilePath();
    private final ArrayList<DataElement> dataElements = DataElementDatabase.getDataElements();

    /* vars */
    private int fileCount = 0;
    private int currentElementIndex = 0;
    private ArrayList<File> validFiles;

    @Override
    public void run() {
        initialization();
        deserialization();
        verification();
        finalization();
        TagElementDatabase.initialize();
    }

    /* private */
    private void initialization() {
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(pathMainDirectory).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        File imageCacheDirectory = new File(pathImageCacheDirectory);
        if (!imageCacheDirectory.exists()) {
            imageCacheDirectory.mkdir();
        }
    }

    private void deserialization() {
        if (!new File(pathDatabaseCacheFile).exists() || !dataElements.addAll(Serialization.readFromDisk())) {
            createDatabaseCache();
        }
    }

    private void verification() {
        ArrayList<String> dataElementsItemNames = new ArrayList<>();
        ArrayList<String> validFilesItemNames = new ArrayList<>();

        for (DataElement dataElement : dataElements) {
            dataElementsItemNames.add(dataElement.getName());
        }
        for (File file : validFiles) {
            validFilesItemNames.add(file.getName());
        }

        dataElementsItemNames.sort(Comparator.naturalOrder());
        validFilesItemNames.sort(Comparator.naturalOrder());

        if (!dataElementsItemNames.equals(validFilesItemNames)) {
            rebuildDatabaseCache(dataElementsItemNames, validFilesItemNames);
        }
    }

    private void finalization() {
        for (DataElement dataElement : dataElements) {
            dataElement.setImage(getImageFromDataElement(dataElement));
            dataElement.setGalleryTile(new GalleryTile(dataElement));
        }

        Platform.runLater(() -> {
            Main.getLoadingWindow().close();
            Main.setStage(new GUIStage());
        });
    }

    private void createDatabaseCache() {
        for (File file : validFiles) {
            DataElement dataElement = createDataElementFromFile(file);
            dataElements.add(dataElement);
        }

        Serialization.writeToDisk();
    }

    private void rebuildDatabaseCache(ArrayList<String> dataElementsItemNames, ArrayList<String> validFilesItemNames) {
        /* add unrecognized items */
        for (File file : validFiles) {
            if (!dataElementsItemNames.contains(file.getName())) {
                dataElements.add(createDataElementFromFile(file));
            }
        }

        /* remove missing items */
        ArrayList<DataElement> temporaryList = new ArrayList<>(dataElements);
        for (DataElement dataElement : dataElements) {
            if (!validFilesItemNames.contains(dataElement.getName())) {
                temporaryList.remove(dataElement);
            }
        }

        dataElements.clear();
        dataElements.addAll(temporaryList);
        Serialization.writeToDisk();
    }

    private Image getImageFromDataElement(DataElement dataElement) {
        Image currentElementImage = null;
        currentElementIndex++;

        String currentElementName = dataElement.getName();
        String currentElementCachePath = pathImageCacheDirectory + "/" + currentElementName;
        File currentElementCacheFile = new File(currentElementCachePath);

        /* write image cache to disk if not present */
        if (!currentElementCacheFile.exists()) {
            try {
                currentElementCacheFile.createNewFile();
                String currentElementFilePath = "file:" + pathMainDirectory + "/" + currentElementName;
                currentElementImage = new Image(currentElementFilePath, galleryIconSizeMax, galleryIconSizeMax, false, true);
                String currentElementExtension = FilenameUtils.getExtension(currentElementName);
                BufferedImage currentElementBufferedImage = SwingFXUtils.fromFXImage(currentElementImage, null);
                ImageIO.write(currentElementBufferedImage, currentElementExtension, currentElementCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* update loading window label */
        if (Main.getLoadingWindow().getClass().equals(LoadingWindow.class)) {
            Platform.runLater(() -> Main.getLoadingWindow().getProgressLabel().setText("Loading item " + currentElementIndex + " of " + fileCount + ", " + currentElementIndex * 100 / fileCount + "% done"));
        }

        return Objects.requireNonNullElseGet(currentElementImage, () -> new Image("file:" + currentElementCachePath, galleryIconSizeMax, galleryIconSizeMax, false, true));
    }

    private DataElement createDataElementFromFile(File file) {
        return new DataElement(file.getName(), new ArrayList<>());
    }
}
