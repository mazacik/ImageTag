package database.loader;

import javafx.scene.Scene;
import lifecycle.InstanceManager;
import settings.SettingsEnum;
import user_interface.factory.stage.DirectoryChooserStage;

import java.io.File;

public abstract class DirectoryUtil {
    private static final String dirNameData = ".data";
    private static final String dirNameCache = ".cache";

    private static String pathProject;
    private static String pathSource;
    private static String pathData;
    private static String pathCacheDump;
    private static String pathCacheProject;

    public static void init(String path) {
        File sourceDirectory = new File(path);
        if (sourceDirectory.exists() && sourceDirectory.isDirectory()) {
            String sep = File.separator;
            pathSource = path;
            if (!pathSource.endsWith(sep)) pathSource += sep;
            pathData = pathSource + dirNameData + sep;
            pathCacheDump = pathSource + dirNameCache + sep;
            pathCacheProject = pathCacheDump + InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE) + "px" + sep;
        } else {
            //error, directory not found
        }
    }

    public static String directoryChooser(Scene ownerScene, String initialDirectory) {
        if (ownerScene == null || ownerScene.getWindow() == null) throw new NullPointerException();

        String resultValue = new DirectoryChooserStage(ownerScene.getWindow(), "Choose a Directory", initialDirectory).getResultValue();
        if (!resultValue.isEmpty()) {
            char lastchar = resultValue.charAt(resultValue.length() - 1);
            char separatorChar = File.separatorChar;
            if (lastchar != separatorChar) resultValue += separatorChar;
            return resultValue;
        }
        return "";
    }
    public static String directoryChooser(Scene ownerScene) {
        return directoryChooser(ownerScene, System.getProperty("user.dir"));
    }

    public static String getDirNameData() {
        return dirNameData;
    }
    public static String getDirNameCache() {
        return dirNameCache;
    }

    public static String getPathSource() {
        return pathSource;
    }
    public static String getPathCacheDump() {
        return pathCacheDump;
    }
    public static String getPathCacheProject() {
        return pathCacheProject;
    }
    public static String getPathData() {
        return pathData;
    }
}
