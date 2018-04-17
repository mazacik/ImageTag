package project.backend;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBuilder extends Thread {
    private static final long loadingStartTimeMillis = System.currentTimeMillis();
    private static final ArrayList<DatabaseItem> itemDatabase = new ArrayList<>();
    private static final ArrayList<String> tagsDatabase = new ArrayList<>();
    private static int indexCounter = 0;
    private static File[] validFiles;

    public DatabaseBuilder() {
        File cacheDirectory = new File(Settings.DIRECTORY_PATH + "/cache/");
        if (!cacheDirectory.exists()) cacheDirectory.mkdir();
        File tagsDirectory = new File(Settings.DIRECTORY_PATH + "/tags/");
        if (!tagsDirectory.exists()) tagsDirectory.mkdir();
        start();
    }

    @Override
    public void run() {
        for (File file : validFiles) {
            if (file.isDirectory()) continue;
            DatabaseItem newDatabaseItem = new DatabaseItem();

            /* basic info */
            newDatabaseItem.setFullPath(file.getAbsolutePath());
            newDatabaseItem.setSimpleName(file.getName());
            newDatabaseItem.setExtension(FilenameUtils.getExtension(file.getName()));
            newDatabaseItem.setIndex(indexCounter++);

            /* cache */
            String currentItemCachePath = Settings.DIRECTORY_PATH + "/cache/" + newDatabaseItem.getSimpleName();
            File currentItemCacheFile = new File(currentItemCachePath);
            if (!currentItemCacheFile.exists()) {
                try {
                    currentItemCacheFile.createNewFile();
                    Image tempImage = new Image("file:" + file.getAbsolutePath(), Settings.GALLERY_ICON_SIZE, Settings.GALLERY_ICON_SIZE, false, true);
                    ImageIO.write(SwingFXUtils.fromFXImage(tempImage, null), newDatabaseItem.getExtension(), currentItemCacheFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            newDatabaseItem.setImage(new Image("file:" + currentItemCachePath));

            /* tags */
            String currentItemTagsPath = Settings.DIRECTORY_PATH + "/tags/" + newDatabaseItem.getSimpleName() + ".txt";
            List<String> currentItemTagsList = new ArrayList<>();
            File currentItemTagsFile = new File(currentItemTagsPath);
            if (!currentItemTagsFile.exists()) {
                try {
                    currentItemTagsFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(currentItemTagsPath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String line;
                try {
                    assert reader != null;
                    while ((line = reader.readLine()) != null) {
                        currentItemTagsList.add(line);
                        if (!tagsDatabase.contains(line)) tagsDatabase.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            newDatabaseItem.setTags(currentItemTagsList);

            /* finalize */
            itemDatabase.add(newDatabaseItem);
            Platform.runLater(() -> Main.getTopPane().getInfoLabel().setText("Loading... " + indexCounter + " / " + Database.getFileCount() + ", " + indexCounter * 100 / Database.getFileCount() + "%"));
        }
        tagsDatabase.sort(null);
        Database.getItemDatabase().addAll(itemDatabase);
        Database.getItemDatabaseFiltered().addAll(itemDatabase);
        Database.getTagDatabase().addAll(tagsDatabase);
        Platform.runLater(() -> Main.getGalleryPane().refreshContent());
        Platform.runLater(() -> Main.getLeftPane().refreshContent());
        Platform.runLater(() -> Main.getTopPane().getInfoLabel().setText("Loading done in " + Long.toString(System.currentTimeMillis() - loadingStartTimeMillis) + " ms"));
    }

    public static int getFileCount() {
        validFiles = new File(Settings.DIRECTORY_PATH).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"));
        return (validFiles != null) ? validFiles.length : 0;
    }
}
