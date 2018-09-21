package project.database.loader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.Main;
import project.control.DataControl;
import project.control.MainControl;
import project.control.TagControl;
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
    private final double GALLERY_ICON_MAX_SIZE = Settings.getGalleryIconSizeMax();

    private final String PATH_SOURCE = Settings.getPath_source();
    private final String PATH_CACHE = Settings.getPath_cache();
    private final String PATH_DATA = Settings.getPath_data();

    private final DataCollection dataCollection = DataControl.getCollection();

    private int fileCount = 0;
    private int currentObjectIndex = 0;
    private ArrayList<File> validFiles;

    @Override
    public void run() {
        initialization();
        validation();
        finalization();
    }

    private void initialization() {
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(PATH_SOURCE).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        File path_cache = new File(PATH_CACHE);
        if (!path_cache.exists()) path_cache.mkdir();

        File path_data = new File(PATH_DATA);
        if (!path_data.exists()) path_data.mkdir();

        if (!DataControl.addAll(Serialization.readFromDisk())) {
            createDatabase();
        }
    }
    private void validation() {
        ArrayList<String> dataObjectsNames = new ArrayList<>();
        ArrayList<String> validFilesNames = new ArrayList<>();

        for (DataObject dataObject : dataCollection) {
            dataObjectsNames.add(dataObject.getName());
        }
        for (File file : validFiles) {
            validFilesNames.add(file.getName());
        }

        dataObjectsNames.sort(Comparator.naturalOrder());
        validFilesNames.sort(Comparator.naturalOrder());

        if (!dataObjectsNames.equals(validFilesNames)) {
            validateDatabaseCache(dataObjectsNames, validFilesNames);
        }
    }
    private void finalization() {
        //room for optimization here, we already foreach dataCollection in validation() method
        for (DataObject dataObject : dataCollection) {
            dataObject.setImage(getImageFromDataObject(dataObject));
            dataObject.setGalleryTile(new GalleryTile(dataObject));
        }

        Platform.runLater(() -> {
            Main.getStage().close();
            //Main.getLoadingWindow().close();
            Main.setStage(GUIInstance.getInstance());

            TagControl.initialize();
            MainControl.getReloadControl().reloadAll(true);
            GUIInstance.getInstance().show();
        });
    }

    private void createDatabase() {
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

        /* discard missing items */
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
        String currentObjectCachePath = PATH_CACHE + "/" + currentObjectName;
        File currentObjectCacheFile = new File(currentObjectCachePath);

        /* write image cache to disk if not present */
        if (!currentObjectCacheFile.exists()) {
            try {
                currentObjectCacheFile.createNewFile();
                String currentObjectFilePath = "file:" + PATH_SOURCE + "/" + currentObjectName;
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
        Platform.runLater(() -> ((LoadingWindow) Main.getStage()).getProgressLabel().setText("Loading item " + currentObjectIndex + " of " + fileCount + ", " + currentObjectIndex * 100 / fileCount + "% done"));
        //Platform.runLater(() -> Main.getLoadingWindow().getProgressLabel().setText("Loading item " + currentObjectIndex + " of " + fileCount + ", " + currentObjectIndex * 100 / fileCount + "% done"));
        return Objects.requireNonNullElseGet(currentObjectImage, () -> new Image("file:" + currentObjectCachePath, GALLERY_ICON_MAX_SIZE, GALLERY_ICON_MAX_SIZE, false, true));
    }
    private DataObject createDataObjectFromFile(File file) {
        return new DataObject(file.getName(), new TagCollection());
    }
}
