package project.backend.database;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.backend.Backend;
import project.backend.common.Serialization;
import project.backend.common.Settings;
import project.backend.component.GalleryTile;
import project.frontend.common.AlertWindow;
import project.frontend.singleton.TopPaneFront;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseLoader extends Thread {
    private String mainDirectoryPath = Settings.getMainDirectoryPath();
    private String imageCacheDirectoryPath = Settings.getImageCacheDirectoryPath();

    private int galleryIconSizeMax = Settings.getGalleryIconSizeMax();
    private int galleryIconSizePref = Settings.getGalleryIconSizePref();

    private final long loadingStartTime = System.currentTimeMillis();
    private final ArrayList<DatabaseItem> temporaryItemDatabase = new ArrayList<>();
    private final ArrayList<String> temporaryTagsDatabase = new ArrayList<>();

    private ArrayList<File> validFiles;
    private int fileCount;


    @Override
    public void run() {
        /* initialization */
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(mainDirectoryPath).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        File imageCacheDirectory = new File(imageCacheDirectoryPath);
        if (!imageCacheDirectory.exists())
            imageCacheDirectory.mkdir();

        /* deserialization, loaded database null check -> database cache generation */
        if (!temporaryItemDatabase.addAll(Serialization.readFromDisk()) || temporaryItemDatabase.isEmpty())
            generateCache();

        /* database size check */
        else if (temporaryItemDatabase.size() != fileCount) databaseSizeCheck();

        /* load transient variables */
        for (DatabaseItem databaseItem : temporaryItemDatabase) {
            databaseItem.setImage(loadDatabaseItemImage(databaseItem));
            databaseItem.setGalleryTile(new GalleryTile(databaseItem));
            databaseItem.getGalleryTile().setFitWidth(galleryIconSizePref);
            databaseItem.getGalleryTile().setFitHeight(galleryIconSizePref);
        }

        /* initialize application databases */
        Database.getDatabaseItems().addAll(temporaryItemDatabase);
        Database.getDatabaseItemsFiltered().addAll(temporaryItemDatabase);
        for (DatabaseItem databaseItem : temporaryItemDatabase)
            for (String tag : databaseItem.getTags())
                if (!temporaryTagsDatabase.contains(tag))
                    temporaryTagsDatabase.add(tag);
        Database.getDatabaseTags().addAll(temporaryTagsDatabase);
        Database.sort();

        /* serialization */
        Serialization.writeToDisk();

        /* request backend content initialization */
        Platform.runLater(() -> Backend.reloadContent(false));
        Platform.runLater(() -> TopPaneFront.getInstance().getInfoLabel().setText("Loading done in " + Long.toString(System.currentTimeMillis() - loadingStartTime) + " ms"));
    }

    private Image loadDatabaseItemImage(DatabaseItem databaseItem) {
        String currentItemCachePath = imageCacheDirectoryPath + "/" + databaseItem.getName();
        File currentItemCacheFile = new File(currentItemCachePath);

        /* write loaded image to disk if not present */
        if (!currentItemCacheFile.exists()) {
            try {
                currentItemCacheFile.createNewFile();
                Image tempImage = new Image("file:" + mainDirectoryPath + "/" + databaseItem.getName(), galleryIconSizeMax, galleryIconSizeMax, false, true);
                ImageIO.write(SwingFXUtils.fromFXImage(tempImage, null), FilenameUtils.getExtension(databaseItem.getName()), currentItemCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
                new AlertWindow(e.toString());
            }
        }

        if (temporaryItemDatabase.indexOf(databaseItem) != 0)
            Platform.runLater(() -> TopPaneFront.getInstance().getInfoLabel().setText("Loading item " + (temporaryItemDatabase.indexOf(databaseItem) + 1) + " of " + fileCount + ", " + (temporaryItemDatabase.indexOf(databaseItem) + 1) * 100 / fileCount + "% done"));
        else
            Platform.runLater(() -> TopPaneFront.getInstance().getInfoLabel().setText("Loading item " + (temporaryItemDatabase.size()) + " of " + fileCount + ", " + (temporaryItemDatabase.indexOf(databaseItem) + 1) * 100 / fileCount + "% done"));
        return new Image("file:" + currentItemCachePath, (double) galleryIconSizeMax, (double) galleryIconSizeMax, false, true);
    }

    private DatabaseItem buildDatabaseItem(File file) {
        DatabaseItem newDatabaseItem = new DatabaseItem();
        newDatabaseItem.setName(file.getName());
        newDatabaseItem.setTags(new ArrayList<>());
        return newDatabaseItem;
    }

    private void databaseSizeCheck() {
        Platform.runLater(() -> new AlertWindow("Database Size Mismatch", "Database cache size does not match file count. Unrecognized items will be added. Missing items will be removed."));
        /* add unrecognized items */
        ArrayList<String> temporaryItemDatabaseItemNames = new ArrayList<>();
        for (DatabaseItem databaseItem : temporaryItemDatabase)
            temporaryItemDatabaseItemNames.add(databaseItem.getName());
        for (File file : validFiles)
            if (!temporaryItemDatabaseItemNames.contains(file.getName())) {
                temporaryItemDatabase.add(buildDatabaseItem(file));
            }

        /* remove missing items */
        ArrayList<String> validFilesItemNames = new ArrayList<>();
        for (File file : validFiles)
            validFilesItemNames.add(file.getName());
        for (DatabaseItem databaseItem : temporaryItemDatabase)
            if (!validFilesItemNames.contains(databaseItem.getName())) {
                temporaryItemDatabase.remove(databaseItem);
            }
    }

    private void generateCache() {
        for (File file : validFiles) {
            temporaryItemDatabase.add(buildDatabaseItem(file));
        }
    }
}
