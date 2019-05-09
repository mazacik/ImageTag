package database.loader;

import database.list.DataObjectList;
import database.object.DataObject;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import settings.SettingsEnum;
import system.CommonUtil;
import system.GifDecoder;
import system.InstanceRepo;
import user_interface.singleton.center.BaseTile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class LoaderUtil implements InstanceRepo {
    private static final String pathSource = settings.getCurrentDirectory();
    private static final String pathCache = pathSource + "cache" + File.separator + settings.intValueOf(SettingsEnum.TILEVIEW_THUMBSIZE) + "px" + File.separator;
    private static final String pathData = pathSource + "data" + File.separator;
    private static String[] imageExtensions = new String[]{".jpg", ".jpeg", ".png"};
    private static String[] gifExtensions = new String[]{".gif"};
    //private static String[] videoExtensions = new String[]{".mp4"};
    private static String[] videoExtensions = new String[]{};
    public static String getPathSource() {
        return pathSource;
    }
    public static String getPathCache() {
        return pathCache;
    }
    public static String getPathData() {
        return pathData;
    }
    public static void readImageCache(DataObjectList dataObjects) {
        logger.debug(LoaderUtil.class, "loading image cache");
        ArrayList<Integer> mergeGroupsAlreadyShown = new ArrayList<>();

        for (DataObject dataObject : dataObjects) {
            dataObject.setBaseTile(new BaseTile(dataObject, readThumbnail(dataObject)));

            int mergeID = dataObject.getMergeID();
            Runnable showTile = () -> tileView.getTilePane().getChildren().add(dataObject.getBaseTile());
            if (mergeID == 0) {
                Platform.runLater(showTile);
            } else if (mergeID != 0 && !mergeGroupsAlreadyShown.contains(mergeID)) {
                mergeGroupsAlreadyShown.add(dataObject.getMergeID());
                Platform.runLater(showTile);
            }
        }
    }
    private static Image readThumbnail(DataObject dataObject) {
        String objectName = dataObject.getName();
        String objectCache = LoaderUtil.getPathCache() + objectName;
        File cacheFile = new File(objectCache);

        if (cacheFile.exists()) {
            return new Image("file:" + objectCache);
        } else {
            return createThumbnail(dataObject, cacheFile);
        }
    }
    private static Image createThumbnail(DataObject dataObject, File currentObjectCacheFile) {
        switch (dataObject.getFileType()) {
            case IMAGE:
                return createThumbnailFromImage(dataObject, currentObjectCacheFile);
            case GIF:
                return createThumbnailFromGif(dataObject, currentObjectCacheFile);
            case VIDEO:
                return createThumbnailFromVideo(dataObject, currentObjectCacheFile);
            default:
                return null;
        }
    }
    private static Image createThumbnailFromImage(DataObject dataObject, File currentObjectCacheFile) {
        Image thumbnail = null;
        try {
            logger.debug(LoaderUtil.class, "thumbnail for " + dataObject.getName() + " not found, generating");
            currentObjectCacheFile.createNewFile();
            String currentObjectName = dataObject.getName();
            String currentObjectFilePath = "file:" + LoaderUtil.getPathSource() + currentObjectName;
            int galleryIconSizeMax = settings.intValueOf(SettingsEnum.TILEVIEW_THUMBSIZE);
            thumbnail = new Image(currentObjectFilePath, galleryIconSizeMax, galleryIconSizeMax, false, true);
            String currentObjectExtension = FilenameUtils.getExtension(currentObjectName);
            BufferedImage currentObjectBufferedImage = SwingFXUtils.fromFXImage(thumbnail, null);
            ImageIO.write(currentObjectBufferedImage, currentObjectExtension, currentObjectCacheFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnail;
    }
    private static Image createThumbnailFromGif(DataObject dataObject, File currentObjectCacheFile) {
        try {
            logger.debug(LoaderUtil.class, "thumbnail for " + dataObject.getName() + " not found, generating");

            String currentObjectName = dataObject.getName();
            String currentObjectFilePath = "file:" + LoaderUtil.getPathSource() + currentObjectName;
            int thumbSize = settings.intValueOf(SettingsEnum.TILEVIEW_THUMBSIZE);

            GifDecoder gifDecoder = new GifDecoder();
            gifDecoder.read(currentObjectFilePath);
            java.awt.Image firstFrame = gifDecoder.getFrame(0).getScaledInstance(thumbSize, thumbSize, java.awt.Image.SCALE_FAST);
            BufferedImage buffer = new BufferedImage(thumbSize, thumbSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D tGraphics2D = buffer.createGraphics();
            tGraphics2D.setBackground(Color.BLACK);
            tGraphics2D.clearRect(0, 0, thumbSize, thumbSize);
            tGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            tGraphics2D.drawImage(firstFrame, 0, 0, thumbSize, thumbSize, null);
            currentObjectCacheFile.createNewFile();
            ImageIO.write(buffer, FilenameUtils.getExtension(currentObjectName), currentObjectCacheFile);
            return SwingFXUtils.toFXImage(buffer, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Image createThumbnailFromVideo(DataObject dataObject, File currentObjectCacheFile) {
        Image thumbnail = null;
        try {
            logger.debug(LoaderUtil.class, "thumbnail for " + dataObject.getName() + " not found, generating");
            currentObjectCacheFile.createNewFile();
            String currentObjectName = dataObject.getName();
            String currentObjectFilePath = "file:" + LoaderUtil.getPathSource() + currentObjectName;
            int galleryIconSizeMax = settings.intValueOf(SettingsEnum.TILEVIEW_THUMBSIZE);
            thumbnail = new Image(currentObjectFilePath, galleryIconSizeMax, galleryIconSizeMax, false, true);
            String currentObjectExtension = FilenameUtils.getExtension(currentObjectName);
            BufferedImage currentObjectBufferedImage = SwingFXUtils.fromFXImage(thumbnail, null);
            ImageIO.write(currentObjectBufferedImage, currentObjectExtension, currentObjectCacheFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnail;
    }
    public static String[] getImageExtensions() {
        return imageExtensions;
    }
    public static String[] getGifExtensions() {
        return gifExtensions;
    }
    public static String[] getVideoExtensions() {
        return videoExtensions;
    }

    private static ArrayList<File> getAllFiles(File directory) {
        ArrayList<File> allFiles = new ArrayList<>();
        ArrayList<File> currentDirectory = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.listFiles())));

        for (File file : currentDirectory) {
            if (file.isDirectory()) {
                if (!file.getName().equals("cache") && !file.getName().equals("data")) {
                    allFiles.addAll(getAllFiles(file));
                }
            } else {
                allFiles.add(file);
            }
        }

        return allFiles;
    }
    public static ArrayList<File> getValidFiles(String directory) {
        ArrayList<String> validExtensions = new ArrayList<>();
        validExtensions.addAll(Arrays.asList(imageExtensions));
        validExtensions.addAll(Arrays.asList(gifExtensions));
        validExtensions.addAll(Arrays.asList(videoExtensions));

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
    public static void importFiles() {
        String PATH_SOURCE = settings.getCurrentDirectory();
        ArrayList<String> importDirectories = CommonUtil.settings.getImportDirList();

        DataObjectList newDataObjects = new DataObjectList();
        importDirectories.forEach(dir -> {
            for (File file : getValidFiles(dir)) {
                try {
                    //Files.copy(Paths.get(file.getAbsolutePath()), Paths.get((PATH_SOURCE) + file.getName()));
                    Files.move(Paths.get(file.getAbsolutePath()), Paths.get((PATH_SOURCE) + file.getName()));
                    newDataObjects.add(new DataObject(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        readImageCache(newDataObjects);
        mainDataList.addAll(newDataObjects);
        mainDataList.sort();
        filter.apply();
        //reload.notifyChangeIn(Reload.Control.values());
        reload.doReload();
    }
}
