package loader.cache;

import database.list.DataObjectList;
import database.object.DataObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import loader.LoaderUtil;
import system.InstanceRepo;
import user_interface.singleton.center.BaseTile;

import java.io.File;
import java.util.ArrayList;

public abstract class CacheReader {
    public static void readCache(DataObjectList dataObjects) {
        InstanceRepo.logger.debug(LoaderUtil.class, "loading image cache");

        ArrayList<Integer> mergeGroupsAlreadyShown = new ArrayList<>();
        ObservableList<Node> tilePaneChildren = InstanceRepo.tileView.getTilePane().getChildren();

        for (DataObject dataObject : dataObjects) {
            dataObject.setBaseTile(new BaseTile(dataObject, readCache(dataObject)));

            int mergeID = dataObject.getMergeID();
            Runnable showTile = () -> tilePaneChildren.add(dataObject.getBaseTile());
            if (mergeID == 0) {
                Platform.runLater(showTile);
            } else if (mergeID != 0 && !mergeGroupsAlreadyShown.contains(mergeID)) {
                mergeGroupsAlreadyShown.add(dataObject.getMergeID());
                Platform.runLater(showTile);
            }
        }
    }
    private static Image readCache(DataObject dataObject) {
        File cacheFile = new File(dataObject.getCachePath());

        if (cacheFile.exists()) {
            return new Image("file:" + dataObject.getCachePath());
        } else {
            return CacheCreator.createThumbnail(dataObject, cacheFile);
        }
    }
}
