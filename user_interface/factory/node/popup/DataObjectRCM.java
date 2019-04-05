package user_interface.factory.node.popup;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import system.ClipboardUtil;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.stage.OkCancelStage;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class DataObjectRCM extends RightClickMenu implements InstanceRepo {
    public DataObjectRCM() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        Label nodeCopyName = NodeFactory.getLabel("Copy Name", colorData);
        Label nodeCopyPath = NodeFactory.getLabel("Copy Path", colorData);
        Label nodeDelete = NodeFactory.getLabel("Delete File", colorData);

        nodeCopyName.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ClipboardUtil.setClipboardContent(target.getCurrentTarget().getName());
            }
        });
        nodeCopyPath.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ClipboardUtil.setClipboardContent(settings.getCurrentDirectory() + target.getCurrentTarget().getName());
            }
        });
        nodeDelete.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (CommonUtil.isFullView()) {
                    this.deleteCurrentTarget();
                } else {
                    this.deleteSelection();
                }
            }
        });

        this.getChildren().addAll(nodeCopyName, nodeCopyPath, nodeDelete);
        this.setOnShown(event -> {
            double width = 0;
            for (Node node : this.getChildren()) {
                if (width < ((Label) node).getWidth()) width = ((Label) node).getWidth();
            }
            for (Node node : this.getChildren()) {
                ((Label) node).setPrefWidth(width);
            }
        });
    }

    public void show(Node anchor, double screenX, double screenY) {
        super.show(anchor, screenX, screenY);
    }
    public void show(Node anchor, MouseEvent event) {
        super.show(anchor, event.getScreenX(), event.getScreenY());
    }

    private void deleteDataObject(DataObject dataObject) {
        if (filter.contains(dataObject)) {
            String fullPath = settings.getCurrentDirectory() + "\\" + dataObject.getName();
            Desktop.getDesktop().moveToTrash(new File(fullPath));

            select.remove(dataObject);
            filter.remove(dataObject);
            mainDataList.remove(dataObject);
        }
    }
    private void deleteSelection() {
        if (select.isEmpty()) {
            logger.debug(this, "deleteSelection() - empty selection");
            return;
        }

        ArrayList<DataObject> dataObjectsToDelete = new ArrayList<>();
        select.forEach(dataObject -> {
            if (dataObject.getMergeID() != 0 && !tileView.getExpandedGroups().contains(dataObject.getMergeID())) {
                dataObjectsToDelete.addAll(dataObject.getMergeGroup());
            } else {
                dataObjectsToDelete.add(dataObject);
            }
        });

        OkCancelStage okCancelStage = new OkCancelStage("Delete " + dataObjectsToDelete.size() + " file(s)?");
        if (okCancelStage.getResult()) {
            target.storePosition();
            dataObjectsToDelete.forEach(this::deleteDataObject);
            target.restorePosition();

            reload.notifyChangeIn(Reload.Control.FILTER, Reload.Control.TARGET);
            reload.doReload();
        }
    }
    private void deleteCurrentTarget() {
        DataObject currentTarget = target.getCurrentTarget();
        if (currentTarget.getMergeID() != 0) {
            if (!tileView.getExpandedGroups().contains(currentTarget.getMergeID())) {
                OkCancelStage okCancelStage = new OkCancelStage("Delete " + currentTarget.getMergeGroup().size() + " file(s)?");
                if (okCancelStage.getResult()) {
                    currentTarget.getMergeGroup().forEach(this::deleteDataObject);
                    reload.doReload();
                }
            }
        } else {
            String fullPath = settings.getCurrentDirectory() + currentTarget.getName();
            OkCancelStage okCancelStage = new OkCancelStage("Delete file: " + fullPath + "?");
            if (okCancelStage.getResult()) {
                target.storePosition();
                this.deleteDataObject(currentTarget);
                target.restorePosition();

                reload.notifyChangeIn(Reload.Control.FILTER, Reload.Control.TARGET);
                reload.doReload();
            }
        }
    }
}
