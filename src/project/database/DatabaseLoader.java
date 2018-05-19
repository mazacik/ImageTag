package project.database;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.GUIController;
import project.common.Settings;
import project.component.gallery.part.GalleryTile;
import project.component.top.TopPaneFront;
import project.customdialog.generic.AlertWindow;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class DatabaseLoader extends Thread {
    /* imports */
    private final String pathMainDirectory = Settings.getMainDirectoryPath();
    private final String pathImageCacheDirectory = Settings.getImageCacheDirectoryPath();
    private final int galleryIconSizeMax = Settings.getGalleryIconSizeMax();

    /* variables */
    private final long loadingStartTime = System.currentTimeMillis();
    private final ArrayList<DatabaseItem> loaderItemDatabase = new ArrayList<>();
    private final ArrayList<String> loaderTagsDatabase = new ArrayList<>();
    private int fileCount;
    private ArrayList<File> validFiles;

    /* main method */
    @Override
    public void run() {
        /* initialization */
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(pathMainDirectory).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        File imageCacheDirectory = new File(pathImageCacheDirectory);
        if (!imageCacheDirectory.exists())
            imageCacheDirectory.mkdir();

        /* deserialization, loaded database null check, database size check -> database cache generation */
        if (!new File(Settings.getDatabaseCacheFilePath()).exists() || !loaderItemDatabase.addAll(Serialization.readFromDisk()) || loaderItemDatabase.isEmpty() || loaderItemDatabase.size() != fileCount) {
            regenerateDatabaseCache();
            loaderItemDatabase.sort(Comparator.comparing(DatabaseItem::getName));
            loaderTagsDatabase.sort(Comparator.naturalOrder());
            Serialization.writeToDisk();
        }

        /* load transient variables */
        for (DatabaseItem databaseItem : loaderItemDatabase) {
            databaseItem.setImage(loadDatabaseItemImage(databaseItem));
            databaseItem.setGalleryTile(new GalleryTile(databaseItem));
        }

        /* initialize application databases */
        Database.getDatabaseItems().addAll(loaderItemDatabase);
        Database.getDatabaseItemsFiltered().addAll(loaderItemDatabase);
        for (DatabaseItem databaseItem : loaderItemDatabase)
            for (String tag : databaseItem.getTags())
                if (!loaderTagsDatabase.contains(tag))
                    loaderTagsDatabase.add(tag);
        Database.getDatabaseTags().addAll(loaderTagsDatabase);

        /* request backend content initialization */
        Platform.runLater(() -> GUIController.getInstance().reloadComponentData(false));
        Platform.runLater(() -> TopPaneFront.getInstance().getInfoLabel().setText("Loading done in " + Long.toString(System.currentTimeMillis() - loadingStartTime) + " ms"));
    }

    /* private methods */
    private Image loadDatabaseItemImage(DatabaseItem databaseItem) {
        String currentItemCachePath = pathImageCacheDirectory + "/" + databaseItem.getName();
        File currentItemCacheFile = new File(currentItemCachePath);

        /* write image cache to disk if not present */
        if (!currentItemCacheFile.exists()) {
            try {
                currentItemCacheFile.createNewFile();
                Image tempImage = new Image("file:" + pathMainDirectory + "/" + databaseItem.getName(), galleryIconSizeMax, galleryIconSizeMax, false, true);
                ImageIO.write(SwingFXUtils.fromFXImage(tempImage, null), FilenameUtils.getExtension(databaseItem.getName()), currentItemCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (loaderItemDatabase.indexOf(databaseItem) != 0)
            Platform.runLater(() -> TopPaneFront.getInstance().getInfoLabel().setText("Loading item " + (loaderItemDatabase.indexOf(databaseItem) + 1) + " of " + fileCount + ", " + (loaderItemDatabase.indexOf(databaseItem) + 1) * 100 / fileCount + "% done"));
        else
            Platform.runLater(() -> TopPaneFront.getInstance().getInfoLabel().setText("Loading item " + (loaderItemDatabase.size()) + " of " + fileCount + ", " + (loaderItemDatabase.indexOf(databaseItem) + 1) * 100 / fileCount + "% done"));
        return new Image("file:" + currentItemCachePath, (double) galleryIconSizeMax, (double) galleryIconSizeMax, false, true);
    }

    private DatabaseItem buildDatabaseItem(File file) {
        DatabaseItem newDatabaseItem = new DatabaseItem();
        newDatabaseItem.setName(file.getName());
        newDatabaseItem.setTags(new ArrayList<>());
        return newDatabaseItem;
    }

    private void regenerateDatabaseCache() {
        Platform.runLater(() -> new AlertWindow("Database Size Mismatch", "Database cache size does not match file count. Unrecognized items will be added. Missing items will be removed."));
        /* add unrecognized items */
        ArrayList<String> temporaryItemDatabaseItemNames = new ArrayList<>();
        for (DatabaseItem databaseItem : loaderItemDatabase)
            temporaryItemDatabaseItemNames.add(databaseItem.getName());
        for (File file : validFiles)
            if (!temporaryItemDatabaseItemNames.contains(file.getName())) {
                loaderItemDatabase.add(buildDatabaseItem(file));
            }

        /* remove missing items */
        ArrayList<String> validFilesItemNames = new ArrayList<>();
        for (File file : validFiles)
            validFilesItemNames.add(file.getName());
        for (DatabaseItem databaseItem : loaderItemDatabase)
            if (!validFilesItemNames.contains(databaseItem.getName())) {
                loaderItemDatabase.remove(databaseItem);
            }
    }
}
