package database.loader.cache;

import control.Reload;
import database.list.ObjectList;
import database.object.DataObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import lifecycle.InstanceManager;
import user_interface.singleton.center.BaseTile;

import java.io.File;
import java.util.ArrayList;

public abstract class CacheReader {
    public static void readCache(ObjectList dataObjects) {
        InstanceManager.getLogger().debug(CacheReader.class, "loading image cache");

        ArrayList<Integer> mergeGroupsAlreadyShown = new ArrayList<>();
        ObservableList<Node> tilePaneChildren = InstanceManager.getGalleryPane().getTilePane().getChildren();

        for (DataObject dataObject : dataObjects) {
            String message = "loading progress: " + (dataObjects.indexOf(dataObject) + 1) + "/" + dataObjects.size();
            Platform.runLater(() -> InstanceManager.getToolbarPane().getNodeInfo().setText(message));

            dataObject.setBaseTile(new BaseTile(dataObject, readCache(dataObject)));

            int mergeID = dataObject.getMergeID();
            Runnable showTile = () -> {
                if (!tilePaneChildren.contains(dataObject.getBaseTile())) {
                    tilePaneChildren.add(dataObject.getBaseTile());
                }
            };
            if (mergeID == 0) {
                Platform.runLater(showTile);
            } else if (mergeID != 0 && !mergeGroupsAlreadyShown.contains(mergeID)) {
                mergeGroupsAlreadyShown.add(dataObject.getMergeID());
                Platform.runLater(showTile);
            }
        }

        InstanceManager.getReload().flag(Reload.Control.DATA);
    }
    public static Image readCache(DataObject dataObject) {
        File cacheFile = new File(dataObject.getCacheFile());

        if (cacheFile.exists()) {
            return new Image("file:" + dataObject.getCacheFile());
        } else {
            return CacheCreator.createThumbnail(dataObject, cacheFile);
        }
    }
}
