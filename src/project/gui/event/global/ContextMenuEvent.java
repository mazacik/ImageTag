package project.gui.event.global;

import project.control.selection.Selection;
import project.database.object.DataObject;
import project.gui.component.GUINode;
import project.gui.custom.generic.ConfirmationWindow;
import project.settings.Settings;
import project.utils.ClipboardUtil;
import project.utils.MainUtil;

import java.awt.*;
import java.io.File;

public class ContextMenuEvent implements MainUtil {
    public ContextMenuEvent() {
        onAction_menuCopy();
        onAction_menuDelete();
    }

    private void onAction_menuCopy() {
        customStage.getDataObjectContextMenu().getMenuCopy().setOnAction(event -> {
            DataObject dataObject;

            if (!isPreviewFullscreen()) {
                //todo copy multiple files -- array?
                dataObject = selection.get(0);
            } else {
                dataObject = focus.getCurrentFocus();
            }

            ClipboardUtil.setClipboardContent(Settings.getPath_source() + dataObject.getName());
        });
    }
    private void onAction_menuDelete() {
        customStage.getDataObjectContextMenu().getMenuDelete().setOnAction(event -> {
            if (!isPreviewFullscreen()) {
                this.deleteSelection();
            } else {
                this.deleteCurrentFocus();
            }
            focus.refresh();
        });
    }

    private void deleteDataObject(DataObject dataObject) {
        if (filter.contains(dataObject)) {
            String fullPath = Settings.getPath_source() + "\\" + dataObject.getName();
            Desktop.getDesktop().moveToTrash(new File(fullPath));

            filter.remove(dataObject);
            mainData.remove(dataObject);
            selection.remove(dataObject);
        }
    }
    private void deleteSelection() {
        if (selection.isEmpty()) return;

        ConfirmationWindow confirmationWindow = new ConfirmationWindow();
        confirmationWindow.setTitle("Confirmation");
        confirmationWindow.setHeaderText("Delete " + selection.size() + " file(s)");
        confirmationWindow.setContentText("Are you sure?");

        if (confirmationWindow.getResult()) {
            ((Selection) selection.clone()).forEach(this::deleteDataObject);
            reload.queue(true, GUINode.GALLERYPANE);
        }
    }
    private void deleteCurrentFocus() {
        DataObject currentFocus = focus.getCurrentFocus();
        String fullPath = Settings.getPath_source() + "\\" + currentFocus.getName();

        ConfirmationWindow confirmationWindow = new ConfirmationWindow();
        confirmationWindow.setTitle("Confirmation");
        confirmationWindow.setHeaderText("Delete file: " + fullPath);
        confirmationWindow.setContentText("Are you sure?");

        if (confirmationWindow.getResult()) {
            //todo move most of this to focus
            int index = filter.indexOf(focus.getCurrentFocus());
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

            focus.set(filter.get(index));

            reload.queue(true, GUINode.GALLERYPANE, GUINode.PREVIEWPANE);
        }
    }
}
