package project.backend.database;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.apache.commons.io.FilenameUtils;
import project.backend.Backend;
import project.backend.common.Serialization;
import project.Main;
import project.frontend.shared.ColoredText;
import project.frontend.shared.Frontend;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//todo: check, rework?
public class DatabaseLoader extends Thread {
    private static final long loadingStartTime = System.currentTimeMillis();
    private static final ArrayList<DatabaseItem> temporaryItemDatabase = new ArrayList<>();
    private static final ArrayList<String> temporaryTagsDatabase = new ArrayList<>();

    @Override
    public void run() {
        /* initialization, deserialization */
        File imageCacheDirectory = new File(Main.DIRECTORY_PATH + "/imagecache/");
        if (!imageCacheDirectory.exists()) imageCacheDirectory.mkdir();
        ArrayList<DatabaseItem> temporaryItemDatabaseLoaded = Serialization.readFromDisk();

        /* loaded database null check, database cache generation */
        if (temporaryItemDatabaseLoaded == null) generateCache();
        else {
            /* load transient variables */
            temporaryItemDatabase.addAll(temporaryItemDatabaseLoaded);
            for (DatabaseItem databaseItem : temporaryItemDatabase) {
                databaseItem.setImage(loadDatabaseItemImage(databaseItem));
                databaseItem.setImageView(new ImageView(databaseItem.getImage()));
                databaseItem.getImageView().setFitWidth(Main.GALLERY_ICON_SIZE_PREF);
                databaseItem.getImageView().setFitHeight(Main.GALLERY_ICON_SIZE_PREF);
                databaseItem.setColoredText(new ColoredText(databaseItem.getSimpleName(), Color.BLACK));
            }

            /* database size check */
            if (temporaryItemDatabase.size() != Database.getFileCount()) databaseSizeCheck();
        }

        /* initialize application databases */
        Database.getDatabaseItems().addAll(temporaryItemDatabase);
        Database.getDatabaseItemsFiltered().addAll(temporaryItemDatabase);
        for (DatabaseItem databaseItem : temporaryItemDatabase)
            for (String tag : databaseItem.getTags())
                if (!temporaryTagsDatabase.contains(tag)) temporaryTagsDatabase.add(tag);
        temporaryTagsDatabase.sort(null);
        Database.getDatabaseTags().addAll(temporaryTagsDatabase);

        /* request backend content initialization */
        Platform.runLater(Backend::reloadContent);
        Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Loading done in " + Long.toString(System.currentTimeMillis() - loadingStartTime) + " ms"));
    }

    private DatabaseItem buildDatabaseItem(File file) {
        DatabaseItem newDatabaseItem = new DatabaseItem();
        newDatabaseItem.setFullPath(file.getAbsolutePath());
        newDatabaseItem.setSimpleName(file.getName());
        newDatabaseItem.setExtension(FilenameUtils.getExtension(file.getName()));
        newDatabaseItem.setImage(loadDatabaseItemImage(newDatabaseItem));
        newDatabaseItem.setImageView(new ImageView(newDatabaseItem.getImage()));
        newDatabaseItem.getImageView().setFitWidth(Main.GALLERY_ICON_SIZE_PREF);
        newDatabaseItem.getImageView().setFitHeight(Main.GALLERY_ICON_SIZE_PREF);
        newDatabaseItem.setColoredText(new ColoredText(newDatabaseItem.getSimpleName(), Color.BLACK));
        newDatabaseItem.setTags(new ArrayList<>());
        return newDatabaseItem;
    }

    private Image loadDatabaseItemImage(DatabaseItem databaseItem) {
        String currentItemCachePath = Main.DIRECTORY_PATH + "/imagecache/" + databaseItem.getSimpleName();
        File currentItemCacheFile = new File(currentItemCachePath);

        /* write loaded image to disk if not present */
        if (!currentItemCacheFile.exists()) {
            try {
                currentItemCacheFile.createNewFile();
                Image tempImage = new Image("file:" + databaseItem.getFullPath(), Main.GALLERY_ICON_SIZE_MAX, Main.GALLERY_ICON_SIZE_MAX, false, true);
                ImageIO.write(SwingFXUtils.fromFXImage(tempImage, null), databaseItem.getExtension(), currentItemCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Loading item " + (temporaryItemDatabase.indexOf(databaseItem) + 1) + " of " + Database.getFileCount() + ", " + (temporaryItemDatabase.indexOf(databaseItem) + 1) * 100 / Database.getFileCount() + "% done"));
        return new Image("file:" + currentItemCachePath, (double) Main.GALLERY_ICON_SIZE_MAX, (double) Main.GALLERY_ICON_SIZE_MAX, false, true);
    }

    private void databaseSizeCheck() {
        System.out.println("databaseCacheSize doesn't match validFileCount...");

        /* add unrecognized items */
        ArrayList<String> temporaryItemDatabaseItemNames = new ArrayList<>();
        for (DatabaseItem databaseItem : temporaryItemDatabase)
            temporaryItemDatabaseItemNames.add(databaseItem.getSimpleName());
        for (File file : Database.getValidFiles())
            if (!temporaryItemDatabaseItemNames.contains(file.getName())) {
                DatabaseItem newDatabaseItem = buildDatabaseItem(file);
                temporaryItemDatabase.add(newDatabaseItem);
            }

        /* remove missing items */
        ArrayList<String> validFilesItemNames = new ArrayList<>();
        for (File file : Database.getValidFiles())
            validFilesItemNames.add(file.getName());
        for (DatabaseItem databaseItem : temporaryItemDatabase)
            if (!validFilesItemNames.contains(databaseItem.getSimpleName()))
                temporaryItemDatabase.remove(databaseItem);

        Serialization.writeToDisk(temporaryItemDatabase);
    }

    private void generateCache() {
        for (File file : Database.getValidFiles()) {
            DatabaseItem newDatabaseItem = buildDatabaseItem(file);
            temporaryItemDatabase.add(newDatabaseItem);
        }
        Serialization.writeToDisk(temporaryItemDatabase);
    }
}
