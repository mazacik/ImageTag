package database.loader;

public abstract class FileSupportUtil {
    private static final String[] sprtImageExt = new String[]{
            ".jpg",
            ".jpeg",
            ".png",
    };
    private static final String[] sprtGifExt = new String[]{
            ".gif",
    };
    private static final String[] sprtVideoExt = new String[]{
            ".mp4",
            ".m4v",
            ".mov",
            ".wmv",
            ".avi",
            ".webm",
    };

    public static String[] getSprtImageExt() {
        return sprtImageExt;
    }
    public static String[] getSprtGifExt() {
        return sprtGifExt;
    }
    public static String[] getSprtVideoExt() {
        return sprtVideoExt;
    }
}
