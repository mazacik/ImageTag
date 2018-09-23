package project.settings;

import java.io.*;
import java.util.Properties;

public abstract class Settings {
    private static String path_source;
    private static String path_cache;
    private static String path_data;

    private static int galleryIconSizeMax = 200;
    private static int galleryIconSizeMin = 100;
    private static int galleryIconSizePref = 150;

    private static int guiMinWidth = 800;
    private static int guiMinHeight = 600;

    private static String settingsFilePath = "JavaExplorer.ini";

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

        path_source = properties.getProperty("SourcePath", "");
        path_cache = properties.getProperty("CachePath", "");
        path_data = properties.getProperty("DataPath", "");

        return !path_source.isEmpty() && !path_cache.isEmpty() && !path_data.isEmpty();
    }
    public static void writeToFile() {
        try {
            Properties props = new Properties();
            props.setProperty("SourcePath", path_source);
            props.setProperty("CachePath", path_cache);
            props.setProperty("DataPath", path_data);
            File file = new File(settingsFilePath);
            OutputStream out = new FileOutputStream(file);
            props.store(out, null);
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

    public static int getGuiMinWidth() {
        return guiMinWidth;
    }
    public static int getGuiMinHeight() {
        return guiMinHeight;
    }

    public static String getPath_source() {
        return path_source;
    }
    public static String getPath_cache() {
        return path_cache;
    }
    public static String getPath_data() {
        return path_data;
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

    public static void setPath_source(String path_source) {
        Settings.path_source = path_source;
    }
    public static void setPath_cache(String path_cache) {
        Settings.path_cache = path_cache;
    }
    public static void setPath_data(String path_data) {
        Settings.path_data = path_data;
    }
}
