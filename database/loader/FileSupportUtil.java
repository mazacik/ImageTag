package database.loader;

public abstract class FileSupportUtil {
    private static final String[] imageExtensions = new String[]{
            ".jpg",
            ".jpeg",
            ".png",
    };
    private static final String[] gifExtensions = new String[]{
            ".gif",
    };
    private static final String[] videoExtensions = new String[]{
            ".mp4",
            ".m4v",
            ".mov",
            ".wmv",
            ".avi",
            ".webm",
    };

    public static String[] getImageExtensions() {
        return imageExtensions;
    }
    public static String[] getGifExtensions() {
        return gifExtensions;
    }
    public static String[] getVideoExtensions() {
        return videoExtensions;
    }
}
