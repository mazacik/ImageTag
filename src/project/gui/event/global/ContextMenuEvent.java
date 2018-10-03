package project.gui.event.global;

import project.database.object.DataObject;
import project.gui.component.GUINode;
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
                //todo
                dataObject = selection.get(0);
            } else {
                dataObject = focus.getCurrentFocus();
            }

            ClipboardUtil.setClipboardContent(dataObject.getName());
        });
    }
    private void onAction_menuDelete() {
        customStage.getDataObjectContextMenu().getMenuDelete().setOnAction(event -> {
            if (!isPreviewFullscreen()) {
                this.deleteDataObject(GUINode.GALLERYPANE);
            } else {
                this.deleteCurrentFocus(GUINode.PREVIEWPANE);
            }
        });
    }

    private void deleteDataObject(GUINode sender, DataObject dataObject) {
        int index = filter.indexOf(dataObject);

        String pathString = Settings.getPath_source() + "\\" + dataObject.getName();
        Desktop.getDesktop().moveToTrash(new File(pathString));

        filter.remove(dataObject);
        mainData.remove(dataObject);
        selection.remove(dataObject);

        if (filter.get(index - 1) != null) {
            index--;
        } else if (filter.get(index + 1) != null) {
            index++;
        }

        focus.set(filter.get(index));
        reload.queue(true, sender);
    }
    private void deleteDataObject(GUINode sender) {
        selection.forEach(dataObject -> {
            if (filter.contains(dataObject)) {
                this.deleteDataObject(sender, dataObject);
            }
        });
    }
    private void deleteCurrentFocus(GUINode sender) {
        this.deleteDataObject(sender, focus.getCurrentFocus());
    }
}
