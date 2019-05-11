package loader;

import settings.SettingsEnum;
import system.InstanceRepo;

import java.io.File;

public abstract class DirectoryUtil {
    private static final String dirNameData = "data";
    private static final String dirNameCache = "cache";
    private static final String pathSource = InstanceRepo.settings.getCurrentDirectory();
    private static final String pathData = pathSource + dirNameData + File.separator;
    private static final String pathCache = pathSource + dirNameCache + File.separator + InstanceRepo.settings.intValueOf(SettingsEnum.TILEVIEW_ICONSIZE) + "px" + File.separator;
    public static String getDirNameData() {
        return dirNameData;
    }
    public static String getDirNameCache() {
        return dirNameCache;
    }
    public static String getPathSource() {
        return pathSource;
    }
    public static String getPathCache() {
        return pathCache;
    }
    public static String getPathData() {
        return pathData;
    }
}
