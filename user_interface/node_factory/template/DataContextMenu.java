package user_interface.node_factory.template;

import database.object.DataObject;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import system.ClipboardUtil;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.node_factory.template.generic.WindowOkCancel;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class DataContextMenu extends ContextMenu implements InstanceRepo {
    private final MenuItem menuCopy = new MenuItem("Copy Filename");
    private final MenuItem menuDelete = new MenuItem("Delete File");

    public DataContextMenu() {
        this.getItems().addAll(menuCopy, menuDelete);
        //todo add copy full path
        menuCopy.setOnAction(event -> ClipboardUtil.setClipboardContent(coreSettings.getCurrentDirectory() + target.getCurrentTarget().getName()));
        menuDelete.setOnAction(event -> {
            target.storePosition();

            if (CommonUtil.isFullView()) {
                this.deleteCurrentTarget();
            } else {
                this.deleteSelection();
            }

            target.restorePosition();
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
            String fullPath = coreSettings.getCurrentDirectory() + "\\" + dataObject.getName();
            Desktop.getDesktop().moveToTrash(new File(fullPath));

            filter.remove(dataObject);
            mainListData.remove(dataObject);
            select.remove(dataObject);
        }
    }
    private void deleteSelection() {
        if (select.isEmpty()) return;

        ArrayList<DataObject> dataObjectsToDelete = new ArrayList<>();
        select.forEach(dataObject -> {
            if (dataObject.getMergeID() != 0 && !tileView.getExpandedGroups().contains(dataObject.getMergeID())) {
                dataObjectsToDelete.addAll(dataObject.getMergeGroup());
            } else {
                dataObjectsToDelete.add(dataObject);
            }
        });

        WindowOkCancel windowOkCancel = new WindowOkCancel("Delete " + dataObjectsToDelete.size() + " file(s)?");
        if (windowOkCancel.getResult()) {
            dataObjectsToDelete.forEach(this::deleteDataObject);
            reload.doReload();
        }
    }
    private void deleteCurrentTarget() {
        DataObject currentTarget = target.getCurrentTarget();
        if (currentTarget.getMergeID() != 0) {
            if (!tileView.getExpandedGroups().contains(currentTarget.getMergeID())) {
                WindowOkCancel windowOkCancel = new WindowOkCancel("Delete " + currentTarget.getMergeGroup().size() + " file(s)?");
                if (windowOkCancel.getResult()) {
                    currentTarget.getMergeGroup().forEach(this::deleteDataObject);
                    reload.doReload();
                }
            }
        } else {
            String fullPath = coreSettings.getCurrentDirectory() + currentTarget.getName();
            WindowOkCancel windowOkCancel = new WindowOkCancel("Delete file: " + fullPath + "?");
            if (windowOkCancel.getResult()) {
                this.deleteDataObject(currentTarget);
                reload.doReload();
            }
        }
    }
}
