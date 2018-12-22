package gui.event.global;

import control.select.Select;
import database.object.DataObject;
import gui.template.generic.ConfirmationWindow;
import utils.MainUtil;
import utils.system.ClipboardUtil;

import java.awt.*;
import java.io.File;

public class GlobalContextMenuEvent implements MainUtil {
    public GlobalContextMenuEvent() {
        onAction_menuCopy();
        onAction_menuDelete();
    }

    private void onAction_menuCopy() {
        mainStage.getDataObjectContextMenu().getMenuCopy().setOnAction(event -> {
            DataObject dataObject;

            if (!isFullView()) {
                //todo copy multiple files -- array?
                dataObject = select.get(0);
            } else {
                dataObject = target.getCurrentFocus();
            }

            ClipboardUtil.setClipboardContent(settings.getCurrentDirectory() + dataObject.getName());
        });
    }
    private void onAction_menuDelete() {
        mainStage.getDataObjectContextMenu().getMenuDelete().setOnAction(event -> {
            target.storePosition();

            if (isFullView()) {
                this.deleteCurrentFocus();
            } else {
                this.deleteSelection();
            }

            target.restorePosition();
        });
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
