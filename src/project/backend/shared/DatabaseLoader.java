package project.backend.shared;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.apache.commons.io.FilenameUtils;
import project.frontend.shared.ColoredText;
import project.frontend.shared.Frontend;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Background thread used to load the working directory as a database in case of deserialization failure.
 */
public class DatabaseLoader extends Thread {
    private static final long loadingStartTimeMillis = System.currentTimeMillis();
    private static final ArrayList<DatabaseItem> loaderItemDatabase = new ArrayList<>();
    private static final ArrayList<String> loaderTagsDatabase = new ArrayList<>();

    @Override
    public void run() {
        Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Loading..."));
        File imageCacheDirectory = new File(Backend.DIRECTORY_PATH + "/imagecache/");
        if (!imageCacheDirectory.exists()) imageCacheDirectory.mkdir();
        ArrayList<DatabaseItem> databaseCache = Database.readFromDisk();

        if (databaseCache == null) rebuildCache();
        else {
            loaderItemDatabase.addAll(databaseCache);
            for (DatabaseItem databaseItem : loaderItemDatabase) {
                databaseItem.setImageView(new ImageView(loadItemImage(databaseItem)));
                databaseItem.setColoredText(
                        new ColoredText(databaseItem.getSimpleName(), Color.BLACK, databaseItem));
            }
            if (databaseCache.size() < Database.getFileCount()) {
                System.out.println(
                        "databaseCacheSize doesn't match validFileCount, building missing databaseItems...");
                ArrayList<String> databaseCacheItemNames = new ArrayList<>();
                for (DatabaseItem databaseItem : databaseCache)
                    databaseCacheItemNames.add(databaseItem.getSimpleName());
                for (File file : Database.getValidFiles())
                    if (!databaseCacheItemNames.contains(file.getName())) {
                        DatabaseItem newDatabaseItem = buildNewDatabaseItem(file);
                        loaderItemDatabase.add(newDatabaseItem);
                    }
                Database.writeToDisk(loaderItemDatabase);
            }
        }
        Database.getItemDatabase().addAll(loaderItemDatabase);
        Database.getFilteredItems().addAll(loaderItemDatabase);
        for (DatabaseItem databaseItem : loaderItemDatabase)
            for (String tag : databaseItem.getTags())
                if (!loaderTagsDatabase.contains(tag)) loaderTagsDatabase.add(tag);
        loaderTagsDatabase.sort(null);
        Database.getTagDatabase().addAll(loaderTagsDatabase);
        Platform.runLater(Backend::reloadContent);
        // Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Fullscreen"));
        Platform.runLater(
                () ->
                        Frontend.getTopPane()
                                .getInfoLabel()
                                .setText(
                                        "Loading done in "
                                                + Long.toString(System.currentTimeMillis() - loadingStartTimeMillis)
                                                + " ms"));
    }

    /**
     * Generates a database item of an unrecognized file from the working directory.
     *
     * @param file the unrecognized file
     */
    private DatabaseItem buildNewDatabaseItem(File file) {
        DatabaseItem newDatabaseItem = new DatabaseItem();
        newDatabaseItem.setFullPath(file.getAbsolutePath());
        newDatabaseItem.setSimpleName(file.getName());
        newDatabaseItem.setExtension(FilenameUtils.getExtension(file.getName()));
        newDatabaseItem.setImageView(new ImageView(loadItemImage(newDatabaseItem)));
        newDatabaseItem.setColoredText(
                new ColoredText(newDatabaseItem.getSimpleName(), Color.BLACK, newDatabaseItem));
        newDatabaseItem.setTags(new ArrayList<>());
        return newDatabaseItem;
    }

    /**
     * Rebuilds the entire working directory database in case of deserialization failure.
     */
    private void rebuildCache() {
        for (File file : Database.getValidFiles()) {
            DatabaseItem newDatabaseItem = buildNewDatabaseItem(file);
            loaderItemDatabase.add(newDatabaseItem);
            Platform.runLater(
                    () ->
                            Frontend.getTopPane()
                                    .getInfoLabel()
                                    .setText(
                                            "Loading item "
                                                    + (loaderItemDatabase.size() + 1)
                                                    + " of "
                                                    + Database.getFileCount()
                                                    + ", "
                                                    + (loaderItemDatabase.size() + 1) * 100 / Database.getFileCount()
                                                    + "% done"));
        }
        Database.writeToDisk(loaderItemDatabase);
    }

    /**
     * Loads the thumbnail of an item from the cache directory.
     *
     * @param databaseItem the database item whose thumbnail has been requested
     */
    private Image loadItemImage(DatabaseItem databaseItem) {
        String currentItemCachePath =
                Backend.DIRECTORY_PATH + "/imagecache/" + databaseItem.getSimpleName();
        File currentItemCacheFile = new File(currentItemCachePath);
        if (!currentItemCacheFile.exists()) {
            try {
                currentItemCacheFile.createNewFile();
                Image tempImage =
                        new Image(
                                "file:" + databaseItem.getFullPath(),
                                Main.GALLERY_ICON_SIZE,
                                Main.GALLERY_ICON_SIZE,
                                false,
                                true);
                ImageIO.write(
                        SwingFXUtils.fromFXImage(tempImage, null),
                        databaseItem.getExtension(),
                        currentItemCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Image("file:" + currentItemCachePath);
    }
}
