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

public class DatabaseLoader extends Thread {
    //private static final long loadingStartTimeMillis = System.currentTimeMillis();
    private static final ArrayList<DatabaseItem> loaderItemDatabase = new ArrayList<>();
    private static final ArrayList<String> loaderTagsDatabase = new ArrayList<>();

    @Override
    public void run() {
        File imageCacheDirectory = new File(Backend.DIRECTORY_PATH + "/imagecache/");
        if (!imageCacheDirectory.exists()) imageCacheDirectory.mkdir();
        File databaseCacheFile = new File(Backend.DIRECTORY_PATH + "/database");
        if (databaseCacheFile.exists()) {
            ArrayList<DatabaseItem> databaseCache = Database.readFromDisk();
            if (databaseCache == null) {
                rebuildCache();
            } else {
                loaderItemDatabase.addAll(databaseCache);

                for (DatabaseItem databaseItem : loaderItemDatabase) {
                    databaseItem.setImageView(new ImageView(getItemImage(databaseItem)));
                    databaseItem.setColoredText(new ColoredText(databaseItem.getSimpleName(), Color.BLACK, databaseItem));
                }
            }
        } else
            rebuildCache();
        Database.getItemDatabase().addAll(loaderItemDatabase);
        Database.getFilteredItems().addAll(loaderItemDatabase);
        for (DatabaseItem databaseItem : loaderItemDatabase)
            for (String tag : databaseItem.getTags())
                if (!loaderTagsDatabase.contains(tag))
                    loaderTagsDatabase.add(tag);
        loaderTagsDatabase.sort(null);
        Database.getTagDatabase().addAll(loaderTagsDatabase);
        Platform.runLater(Frontend::refreshContent);
        Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Fullscreen"));
        //Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Loading done in " + Long.toString(System.currentTimeMillis() - loadingStartTimeMillis) + " ms"));
    }

    private void rebuildCache() {
        for (File file : Database.getValidFiles()) {
            if (file.isDirectory()) continue;
            DatabaseItem newDatabaseItem = new DatabaseItem();
            newDatabaseItem.setFullPath(file.getAbsolutePath());
            newDatabaseItem.setSimpleName(file.getName());
            newDatabaseItem.setExtension(FilenameUtils.getExtension(file.getName()));
            newDatabaseItem.setImageView(new ImageView(getItemImage(newDatabaseItem)));
            newDatabaseItem.setColoredText(new ColoredText(newDatabaseItem.getSimpleName(), Color.BLACK, newDatabaseItem));
            newDatabaseItem.setTags(new ArrayList<>());
            loaderItemDatabase.add(newDatabaseItem);
            Platform.runLater(() -> Frontend.getTopPane().getInfoLabel().setText("Loading item " + (loaderItemDatabase.size() + 1) + " of " + Database.getFileCount() + ", " + (loaderItemDatabase.size() + 1) * 100 / Database.getFileCount() + "% done"));
        }
        Database.writeToDisk(loaderItemDatabase);
    }

    private Image getItemImage(DatabaseItem databaseItem) {
        String currentItemCachePath = Backend.DIRECTORY_PATH + "/imagecache/" + databaseItem.getSimpleName();
        File currentItemCacheFile = new File(currentItemCachePath);
        if (!currentItemCacheFile.exists()) {
            try {
                currentItemCacheFile.createNewFile();
                Image tempImage = new Image("file:" + databaseItem.getFullPath(), Backend.GALLERY_ICON_SIZE, Backend.GALLERY_ICON_SIZE, false, true);
                ImageIO.write(SwingFXUtils.fromFXImage(tempImage, null), databaseItem.getExtension(), currentItemCacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Image("file:" + currentItemCachePath);
    }
}
