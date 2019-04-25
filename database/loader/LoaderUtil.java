package database.loader;

import database.list.DataObjectList;
import database.object.DataObject;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import settings.SettingsEnum;
import system.InstanceRepo;
import user_interface.singleton.center.BaseTile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class LoaderUtil implements InstanceRepo {
    private static final String pathSource = settings.getCurrentDirectory();
    private static final String pathCache = pathSource + "cache\\";
    private static final String pathData = pathSource + "data\\";

    public static void readImageCache(DataObjectList dataObjects) {
        logger.debug(LoaderUtil.class, "loading image cache");
        ArrayList mergeGroupsAlreadyShown = new ArrayList();

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
        String objectCache = LoaderUtil.getPathCache() + "/" + objectName;
        File cacheFile = new File(objectCache);

        if (cacheFile.exists()) {
            return new Image("file:" + objectCache);
        } else {
            return createThumbnail(dataObject, cacheFile);
        }
    }
    private static Image createThumbnail(DataObject dataObject, File currentObjectCacheFile) {
        Image thumbnail = null;
        try {
            logger.debug(LoaderUtil.class, "thumbnail for " + dataObject.getName() + " not found, generating");
            currentObjectCacheFile.createNewFile();
            String currentObjectName = dataObject.getName();
            String currentObjectFilePath = "file:" + LoaderUtil.getPathSource() + currentObjectName;
            int galleryIconSizeMax = settings.intValueOf(SettingsEnum.TILEVIEW_ICONSIZE);
            thumbnail = new Image(currentObjectFilePath, galleryIconSizeMax, galleryIconSizeMax, false, true);
            String currentObjectExtension = FilenameUtils.getExtension(currentObjectName);
            BufferedImage currentObjectBufferedImage = SwingFXUtils.fromFXImage(thumbnail, null);
            ImageIO.write(currentObjectBufferedImage, currentObjectExtension, currentObjectCacheFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnail;
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
