package database.loader;

import control.Reload;
import database.list.ObjectList;
import database.object.DataObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lifecycle.InstanceManager;
import user_interface.singleton.center.BaseTile;
import user_interface.utils.SizeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public abstract class ThumbnailReader {
    private static Image placeholder = null;

    public static void readThumbnail(ObjectList dataObjects) {
        InstanceManager.getLogger().debug("loading image cache");

        ArrayList<Integer> mergeGroupsAlreadyShown = new ArrayList<>();
        ObservableList<Node> tilePaneChildren = InstanceManager.getGalleryPane().getTilePane().getChildren();

        for (DataObject dataObject : dataObjects) {
            String message = "loading progress: " + (dataObjects.indexOf(dataObject) + 1) + "/" + dataObjects.size();
            Platform.runLater(() -> InstanceManager.getToolbarPane().getNodeInfo().setText(message));

            Image thumbnail = readThumbnail(dataObject);
            dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));

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
    public static Image readThumbnail(DataObject dataObject) {
        File cacheFile = new File(dataObject.getCacheFile());
        Image image;

        if (cacheFile.exists()) {
            image = new Image("file:" + dataObject.getCacheFile());
        } else {
            image = ThumbnailCreator.createThumbnail(dataObject, cacheFile);
        }

        if (image == null) {
            if (placeholder == null) {
                final FutureTask<Image> query = new FutureTask<>(ThumbnailReader::createPlaceholder);
                Platform.runLater(query);
                try {
                    placeholder = query.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return placeholder;
        }
        return image;
    }

    private static Image createPlaceholder() {
        Label label = new Label("Placeholder");
        label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
        label.setWrapText(true);
        label.setFont(new Font(25));
        label.setAlignment(Pos.CENTER);

        int size = (int) SizeUtil.getGalleryIconSize();
        label.setMinWidth(size);
        label.setMinHeight(size);
        label.setMaxWidth(size);
        label.setMaxHeight(size);

        WritableImage img = new WritableImage(size, size);
        Scene scene = new Scene(new Group(label));
        scene.setFill(Color.GRAY);
        scene.snapshot(img);
        return img;
    }
}