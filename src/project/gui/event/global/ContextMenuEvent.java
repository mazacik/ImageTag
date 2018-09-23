package project.gui.event.global;

import project.MainUtils;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.component.GUINode;
import project.settings.Settings;
import project.utils.ClipboardUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContextMenuEvent implements MainUtils {
    public ContextMenuEvent() {
        onAction_menuCopy();
        onAction_menuDelete();
    }

    private void onAction_menuCopy() {
        customStage.getDataObjectContextMenu().getMenuCopy().setOnAction(event -> {
            DataObject dataObject;

            if (!isPreviewFullscreen()) {
                dataObject = selectionControl.getCollection().get(0);
            } else {
                dataObject = focusControl.getCurrentFocus();
            }

            ClipboardUtil.setClipboardContent(dataObject.getName());
        });
    }
    private void onAction_menuDelete() {
        customStage.getDataObjectContextMenu().getMenuDelete().setOnAction(event -> {
            DataCollection dataObjectsValid = filterControl.getCollection();

            if (!isPreviewFullscreen()) {
                deleteDataObject(GUINode.GALLERYPANE, selectionControl.getCollection(), dataObjectsValid);
            } else {
                deleteDataObject(GUINode.PREVIEWPANE, focusControl.getCurrentFocus(), dataObjectsValid);
            }
        });
    }

    private void deleteDataObject(GUINode sender, DataObject dataObject, DataCollection dataObjectsValid) {
        int index = dataObjectsValid.indexOf(dataObject);

        String pathString = Settings.getPath_source() + "\\" + dataObject.getName();
        Path path = Paths.get(pathString);

        try {
            Files.delete(path);

            dataObjectsValid.remove(dataObject);
            dataControl.remove(dataObject);
            selectionControl.getCollection().remove(dataObject);

            if (dataObjectsValid.get(index - 1) != null) {
                index--;
            } else if (dataObjectsValid.get(index + 1) != null) {
                index++;
            }

            focusControl.setFocus(dataObjectsValid.get(index));
            reloadControl.reload(true, sender);
        } catch (IOException e) {
            System.out.println("IOException: Trying to delete non-existent file; Path: " + pathString);
            e.printStackTrace();
        }
    }
    private void deleteDataObject(GUINode sender, DataCollection dataObjects, DataCollection dataObjectsValid) {
        for (DataObject dataObject : dataObjects) {
            if (dataObjectsValid.contains(dataObject)) {
                deleteDataObject(sender, dataObject, dataObjectsValid);
            }
        }
    }
}
