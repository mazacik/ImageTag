package project.settings;

import java.io.*;
import java.util.Properties;

public abstract class Settings {
    /* vars */
    private static String mainDirectoryPath;
    private static String imageCacheDirectoryPath;
    private static String databaseCacheFilePath;

    private static int galleryIconSizeMax = 200;
    private static int galleryIconSizeMin = 100;
    private static int galleryIconSizePref = 150;

    private static String settingsFilePath = "JavaExplorer.ini";

    /* public */
    public static boolean readFromFile(Class main) {
        Properties properties = new Properties();
        InputStream fileInputStream;

        try {
            File file = new File(settingsFilePath);
            fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
        } catch (Exception e1) {
            try {
                fileInputStream = main.getResourceAsStream(settingsFilePath);
                properties.load(fileInputStream);
            } catch (Exception e2) {
                return false;
            }
        }

        mainDirectoryPath = properties.getProperty("MainDirectoryPath", "");
        imageCacheDirectoryPath = properties.getProperty("ImageCacheDirectoryPath", "");
        databaseCacheFilePath = properties.getProperty("DatabaseCacheFilePath", "");

        if (mainDirectoryPath.isEmpty() || imageCacheDirectoryPath.isEmpty() || databaseCacheFilePath.isEmpty()) {
            return false;
        }

        if (!databaseCacheFilePath.endsWith(".json")) {
            databaseCacheFilePath += ".json";
        }
        return true;
    }
    public static void writeToFile() {
        try {
            Properties props = new Properties();
            props.setProperty("MainDirectoryPath", mainDirectoryPath);
            props.setProperty("ImageCacheDirectoryPath", imageCacheDirectoryPath);
            props.setProperty("DatabaseCacheFilePath", databaseCacheFilePath);
            File file = new File(settingsFilePath);
            OutputStream out = new FileOutputStream(file);
            props.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* get */
    public static int getGalleryIconSizeMax() {
        return galleryIconSizeMax;
    }
    public static int getGalleryIconSizeMin() {
        return galleryIconSizeMin;
    }
    public static int getGalleryIconSizePref() {
        return galleryIconSizePref;
    }
    public static String getMainDirectoryPath() {
        return mainDirectoryPath;
    }
    public static String getImageCacheDirectoryPath() {
        return imageCacheDirectoryPath;
    }
    public static String getDatabaseCacheFilePath() {
        return databaseCacheFilePath;
    }

    /* set */
    public static void setGalleryIconSizeMax(int galleryIconSizeMax) {
        Settings.galleryIconSizeMax = galleryIconSizeMax;
    }
    public static void setGalleryIconSizeMin(int galleryIconSizeMin) {
        Settings.galleryIconSizeMin = galleryIconSizeMin;
    }
    public static void setGalleryIconSizePref(int galleryIconSizePref) {
        Settings.galleryIconSizePref = galleryIconSizePref;
    }
    public static void setMainDirectoryPath(String mainDirectoryPath) {
        Settings.mainDirectoryPath = mainDirectoryPath;
    }
    public static void setImageCacheDirectoryPath(String imageCacheDirectoryPath) {
        Settings.imageCacheDirectoryPath = imageCacheDirectoryPath;
    }
    public static void setDatabaseCacheFilePath(String databaseCacheFilePath) {
        Settings.databaseCacheFilePath = databaseCacheFilePath;
    }
}
