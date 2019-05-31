package loader;

import control.logger.Logger;
import database.list.DataObjectList;
import database.object.DataObject;
import loader.cache.CacheCreator;
import system.Instances;
import user_interface.scene.SceneUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public abstract class LoaderUtil implements Instances {
    public static void startLoading(String sourcePath) {
        DirectoryUtil.init(sourcePath);
        new LoaderThread().start();
        SceneUtil.createMainScene();
        SceneUtil.showMainScene();
    }

    private static ArrayList<File> getAllFiles(File directory) {
        ArrayList<File> allFiles = new ArrayList<>();
        ArrayList<File> currentDir = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.listFiles())));

        for (File file : currentDir) {
            if (file.isDirectory()) {
                String dirName = file.getName();
                String dirNameData = DirectoryUtil.getDirNameData();
                String dirNameCache = DirectoryUtil.getDirNameCache();

                if (!dirName.equals(dirNameData) && !dirName.equals(dirNameCache)) {
                    allFiles.addAll(getAllFiles(file));
                }
            } else {
                allFiles.add(file);
            }
        }

        return allFiles;
    }
    public static ArrayList<File> getSupportedFiles(String directory) {
        ArrayList<String> validExtensions = new ArrayList<>();
        validExtensions.addAll(Arrays.asList(FileSupportUtil.getSprtImageExt()));
        validExtensions.addAll(Arrays.asList(FileSupportUtil.getSprtGifExt()));
        validExtensions.addAll(Arrays.asList(FileSupportUtil.getSprtVideoExt()));

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

    public static void initDataObjectPaths(DataObjectList dataObjects, ArrayList<File> fileList) {
        if (dataObjects.size() == fileList.size()) {
            dataObjects.sort(Comparator.comparing(DataObject::getName));
            fileList.sort(Comparator.comparing(File::getName));

            for (int i = 0; i < dataObjects.size(); i++) {
                DataObject dataObject = dataObjects.get(i);
                dataObject.setSourcePath(fileList.get(i).getAbsolutePath());
                dataObject.setCachePath(DirectoryUtil.getPathCacheProject() + dataObject.getName() + CacheCreator.getCacheExt());
            }
        } else {
            String error = "dataObjects.size() != fileList.size()";
            Logger.getInstance().error(LoaderUtil.class, error);
        }
    }
    public static void importFiles() {
        /*
        String pathSource = DirectoryUtil.getPathSource();

        ArrayList<String> importDirectories = CommonUtil.settings.getImportDirectoryList();

        DataObjectList newDataObjects = new DataObjectList();
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
        mainDataList.addAll(newDataObjects);
        mainDataList.sort();
        filter.apply();
        reload.doReload();
        */
    }
}
