package project.common;

import java.io.*;
import java.util.Properties;

public abstract class Settings {
    private static String mainDirectoryPath;
    private static String imageCacheDirectoryPath;
    private static String databaseCacheFilePath;

    private static int galleryIconSizeMax = 200;
    private static int galleryIconSizeMin = 100;
    private static int galleryIconSizePref = 150;

    private static String settingsFilePath = "JavaExplorer.ini";

    public static boolean readFromFile(Class mainClass) {
        Properties props = new Properties();
        InputStream fileInputStream;

        // First try loading from the current directory
        try {
            File file = new File(settingsFilePath);
            fileInputStream = new FileInputStream(file);
        } catch (Exception e) {
            fileInputStream = null;
        }

        try {
            if (fileInputStream == null) {
                // Try loading from classpath
                fileInputStream = mainClass.getResourceAsStream(settingsFilePath);
            }

            // Try loading properties from the file (if found)
            props.load(fileInputStream);
        } catch (Exception e) {
            return false;
        }

        mainDirectoryPath = props.getProperty("MainDirectoryPath", null);
        imageCacheDirectoryPath = props.getProperty("ImageCacheDirectoryPath", null);
        databaseCacheFilePath = props.getProperty("DatabaseCacheFilePath", null);
        return true;
    }

    public static void writeToFile() {
        try {
            Properties props = new Properties();
            props.setProperty("MainDirectoryPath", mainDirectoryPath);
            props.setProperty("ImageCacheDirectoryPath", imageCacheDirectoryPath);
            props.setProperty("DatabaseCacheFilePath", databaseCacheFilePath);
            File f = new File(settingsFilePath);
            OutputStream out = new FileOutputStream(f);
            props.store(out, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
