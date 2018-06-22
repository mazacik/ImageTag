package project.database.loader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import project.Main;
import project.common.Settings;
import project.database.ItemDatabase;
import project.database.TagDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.ChangeEventControl;
import project.gui.GUIStage;
import project.gui.component.part.GalleryTile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseLoader extends Thread {
    /* imports */
    private final String pathMainDirectory = Settings.getMainDirectoryPath();
    private final String pathImageCacheDirectory = Settings.getImageCacheDirectoryPath();
    private final int galleryIconSizeMax = Settings.getGalleryIconSizeMax();

    /* variables */
    private final ArrayList<DatabaseItem> itemDatabase = ItemDatabase.getDatabaseItems();
    private final ArrayList<TagItem> tagDatabase = TagDatabase.getDatabaseTags();

    private final ArrayList<String> itemDatabaseItemNames = new ArrayList<>();
    private final ArrayList<String> validFilesItemNames = new ArrayList<>();

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

        /* deserialization, database null check -> database cache regeneration */
        if (!new File(Settings.getDatabaseCacheFilePath()).exists() || !itemDatabase.addAll(Serialization.readFromDisk()))
            regenerateDatabaseCache();

        /* database size check -> database cache regeneration*/
        for (DatabaseItem databaseItem : itemDatabase)
            itemDatabaseItemNames.add(databaseItem.getName());
        for (File file : validFiles)
            validFilesItemNames.add(file.getName());
        if (!itemDatabaseItemNames.equals(validFilesItemNames))
            regenerateDatabaseCache();

        /* load transient variables */
        for (DatabaseItem databaseItem : itemDatabase) {
            databaseItem.setImage(loadDatabaseItemImage(databaseItem));
            databaseItem.setGalleryTile(new GalleryTile(databaseItem));
        }

        /* initialize application databases */
        ItemDatabase.getDatabaseItemsFiltered().addAll(itemDatabase);
        for (DatabaseItem databaseItem : itemDatabase) {
            for (TagItem tagItem : databaseItem.getTags()) {
                if (!TagDatabase.contains(tagItem)) {
                    tagDatabase.add(tagItem);
                } else {
                    databaseItem.getTags().set(databaseItem.getTags().indexOf(tagItem), TagDatabase.getTagItem(tagItem.getCategory(), tagItem.getName()));
                }
            }
        }

        done();
    }

    private void done() {
        /* request common content initialization */
        Platform.runLater(() -> {
            Main.getLoadingWindow().close();
            new GUIStage();
            ChangeEventControl.requestReloadGlobal();
        });
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

        if (Main.getLoadingWindow() != null) {
            if (itemDatabase.indexOf(databaseItem) != 0)
                Platform.runLater(() -> Main.getLoadingWindow().getProgressLabel().setText("Loading item " + (itemDatabase.indexOf(databaseItem) + 1) + " of " + fileCount + ", " + (itemDatabase.indexOf(databaseItem) + 1) * 100 / fileCount + "% done"));
            else
                Platform.runLater(() -> Main.getLoadingWindow().getProgressLabel().setText("Loading item " + (itemDatabase.size()) + " of " + fileCount + ", " + (itemDatabase.indexOf(databaseItem) + 1) * 100 / fileCount + "% done"));
        }

        return new Image("file:" + currentItemCachePath, (double) galleryIconSizeMax, (double) galleryIconSizeMax, false, true);
    }

    private DatabaseItem buildDatabaseItem(File file) {
        DatabaseItem newDatabaseItem = new DatabaseItem();
        newDatabaseItem.setName(file.getName());
        newDatabaseItem.setTags(new ArrayList<>());
        return newDatabaseItem;
    }

    private void regenerateDatabaseCache() {
        /* addTag unrecognized items */
        for (File file : validFiles) {
            if (!itemDatabaseItemNames.contains(file.getName())) {
                itemDatabase.add(buildDatabaseItem(file));
            }
        }

        /* removeItem missing items */
        ArrayList<DatabaseItem> temporaryList = new ArrayList<>(itemDatabase);
        for (DatabaseItem databaseItem : itemDatabase) {
            if (!validFilesItemNames.contains(databaseItem.getName())) {
                temporaryList.remove(databaseItem);
            }
        }

        itemDatabase.clear();
        itemDatabase.addAll(temporaryList);
        Serialization.writeToDisk();
    }
}
