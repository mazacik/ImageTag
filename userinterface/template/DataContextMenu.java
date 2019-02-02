package userinterface.template;

import control.select.Select;
import database.object.DataObject;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import userinterface.template.generic.WindowOkCancel;
import utils.CommonUtil;
import utils.InstanceRepo;
import utils.system.ClipboardUtil;

import java.awt.*;
import java.io.File;

public class DataContextMenu extends ContextMenu implements InstanceRepo {
    private final MenuItem menuCopy = new MenuItem("Copy Filename");
    private final MenuItem menuDelete = new MenuItem("Delete File");

    public DataContextMenu() {
        this.getItems().addAll(menuCopy, menuDelete);
        menuCopy.setOnAction(event -> {
            DataObject dataObject;

            if (!CommonUtil.isFullView()) {
                //todo copy multiple files -- array?
                dataObject = select.get(0);
            } else {
                dataObject = target.getCurrentTarget();
            }

            ClipboardUtil.setClipboardContent(coreSettings.getCurrentDirectory() + dataObject.getName());
        });
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

        WindowOkCancel windowOkCancel = new WindowOkCancel("Delete " + select.size() + " file(s)?");
        if (windowOkCancel.getResult()) {
            ((Select) select.clone()).forEach(this::deleteDataObject);
            reload.doReload();
        }
    }
    private void deleteCurrentTarget() {
        DataObject currentTarget = target.getCurrentTarget();
        String fullPath = coreSettings.getCurrentDirectory() + currentTarget.getName();

        WindowOkCancel windowOkCancel = new WindowOkCancel("Delete file: " + fullPath + "?");
        if (windowOkCancel.getResult()) {
            //todo move most of this to target
            int index = filter.indexOf(target.getCurrentTarget());
            this.deleteDataObject(currentTarget);

            if (index < 0) {
                index = 0;
            }

            if (filter.get(index) == null) {
                if (index != filter.size() && filter.get(index + 1) != null) {
                    index++;
                } else if (index != 0 && filter.get(index - 1) != null) {
                    index--;
                } else {
                    index = 0;
                }
            }

            target.set(filter.get(index));
            reload.doReload();
        }
    }
}
