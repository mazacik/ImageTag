package project.common;

public abstract class Settings {
    private static String mainDirectoryPath;
    private static String imageCacheDirectoryPath;
    private static String databaseCacheFilePath;

    private static int galleryIconSizeMax = 200;
    private static int galleryIconSizeMin = 100;
    private static int galleryIconSizePref = 150;


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
