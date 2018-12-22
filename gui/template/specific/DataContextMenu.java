package gui.template.specific;

import control.select.Select;
import database.object.DataObject;
import gui.template.generic.ConfirmationWindow;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;
import utils.system.ClipboardUtil;

import java.awt.*;
import java.io.File;

public class DataContextMenu extends ContextMenu implements MainUtil {
    private final MenuItem menuCopy = new MenuItem("Copy Filename");
    private final MenuItem menuDelete = new MenuItem("Delete File");

    public DataContextMenu() {
        this.getItems().addAll(menuCopy, menuDelete);
        menuCopy.setOnAction(event -> {
            DataObject dataObject;

            if (!isFullView()) {
                //todo copy multiple files -- array?
                dataObject = select.get(0);
            } else {
                dataObject = target.getCurrentFocus();
            }

            ClipboardUtil.setClipboardContent(settings.getCurrentDirectory() + dataObject.getName());
        });
        menuDelete.setOnAction(event -> {
            target.storePosition();

            if (isFullView()) {
                this.deleteCurrentFocus();
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
            String fullPath = settings.getCurrentDirectory() + "\\" + dataObject.getName();
            Desktop.getDesktop().moveToTrash(new File(fullPath));

            filter.remove(dataObject);
            mainListData.remove(dataObject);
            select.remove(dataObject);
        }
    }
    private void deleteSelection() {
        if (select.isEmpty()) return;

        ConfirmationWindow confirmationWindow = new ConfirmationWindow();
        confirmationWindow.setTitle("Confirmation");
        confirmationWindow.setHeaderText("Delete " + select.size() + " file(s)");
        confirmationWindow.setContentText("Are you sure?");

        if (confirmationWindow.getResult()) {
            ((Select) select.clone()).forEach(this::deleteDataObject);
            reload.doReload();
        }
    }
    private void deleteCurrentFocus() {
        DataObject currentFocus = target.getCurrentFocus();
        String fullPath = settings.getCurrentDirectory() + "\\" + currentFocus.getName();

        ConfirmationWindow confirmationWindow = new ConfirmationWindow();
        confirmationWindow.setTitle("Confirmation");
        confirmationWindow.setHeaderText("Delete file: " + fullPath);
        confirmationWindow.setContentText("Are you sure?");

        if (confirmationWindow.getResult()) {
            //todo move most of this to target
            int index = filter.indexOf(target.getCurrentFocus());
            this.deleteDataObject(currentFocus);

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
