package database.loader;

import database.list.ObjectList;
import database.object.DataObject;
import javafx.scene.Scene;
import lifecycle.InstanceManager;
import user_interface.factory.stage.DirectoryChooserStage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public abstract class FileUtil {
    private static String dirSource;
    private static String fileData;
    private static String fileTags;
    private static String dirCache;

    public static void initialize(String dirProject, String dirSource) {
        String separator = File.separator;
        if (!dirProject.endsWith(separator)) dirProject += separator;
        if (!dirSource.endsWith(separator)) dirSource += separator;

        FileUtil.dirSource = dirSource;

        fileData = dirProject + "data.json";
        fileTags = dirProject + "tags.json";
        dirCache = dirProject + "cache" + separator;

        File _dirCache = new File(dirCache);
        if (!_dirCache.exists()) _dirCache.mkdir();
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

    public static ArrayList<File> getAllFiles(File directory) {
        ArrayList<File> allFiles = new ArrayList<>();
        ArrayList<File> currentDir = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.listFiles())));

        for (File abstractFile : currentDir) {
            if (abstractFile.isDirectory()) {
                allFiles.addAll(getAllFiles(abstractFile));
            } else {
                allFiles.add(abstractFile);
            }
        }

        return allFiles;
    }
    public static ArrayList<File> getSupportedFiles(String directory) {
        ArrayList<String> validExtensions = new ArrayList<>();
        validExtensions.addAll(Arrays.asList(FileSupportUtil.getImageExtensions()));
        validExtensions.addAll(Arrays.asList(FileSupportUtil.getGifExtensions()));
        validExtensions.addAll(Arrays.asList(FileSupportUtil.getVideoExtensions()));

        ArrayList<File> validFiles = new ArrayList<>();
        for (File file : getAllFiles(new File(directory))) {
            String fileName = file.getName();
            for (String ext : validExtensions) {
                if (fileName.endsWith(ext)) {
                    validFiles.add(file);
                    break;
                }
            }
        }

        return validFiles;
    }

    public static void initDataObjectPaths(ArrayList<File> fileList) {
        ObjectList dataObjects = InstanceManager.getObjectListMain();
        if (dataObjects.size() == fileList.size()) {
            for (int i = 0; i < dataObjects.size(); i++) {
                DataObject dataObject = dataObjects.get(i);
                File sourceFile = fileList.get(i);
                dataObject.setPath(sourceFile.getAbsolutePath());
                dataObject.setSize(sourceFile.length());
            }
        } else {
            String error = "dataObjects.size() != fileList.size()";
            InstanceManager.getLogger().error(FileUtil.class, error);
        }
    }

    public static byte[] getMD5(String path) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try (InputStream is = Files.newInputStream(Paths.get(path));
             DigestInputStream dis = new DigestInputStream(is, md)) {
            int i = 0;
            while (i <= 5000 && i != -1) {
                i = dis.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md.digest();
    }

    public static void importFiles() {
        /*
        String pathSource = FileUtil.getDirSource();

        ArrayList<String> importDirectories = CommonUtil.settings.getImportDirectoryList();

        ObjectList newDataObjects = new ObjectList();
        importDirectories.forEach(dir -> {
            for (File file : getSupportedFiles(dir)) {
                try {
                    Files.move(Paths.get(file.getAbsolutePath()), Paths.get((pathSource) + file.getName()));
                    newDataObjects.add(new DataObject(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        CacheReader.readCache(newDataObjects);
        InstanceManager.getObjectListMain().addAll(newDataObjects);
        InstanceManager.getObjectListMain().sort();
        InstanceManager.getFilter().refresh();
        InstanceManager.getReload().doReload();
        */
    }

    public static String getDirSource() {
        return dirSource;
    }
    public static String getFileData() {
        return fileData;
    }
    public static String getFileTags() {
        return fileTags;
    }
    public static String getDirCache() {
        return dirCache;
    }
}
