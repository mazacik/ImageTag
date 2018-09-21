package project.gui.event.global;

import project.control.Control;
import project.control.Utils;
import project.database.control.DataControl;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.GUIInstance;
import project.gui.GUIUtils;
import project.gui.component.GUINode;
import project.settings.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ContextMenuEvent {
    public static void initialize() {
        onAction_menuCopy();
        onAction_menuDelete();
    }

    private static void onAction_menuCopy() {
        GUIInstance.getDataObjectContextMenu().getMenuCopy().setOnAction(event -> {
            DataObject dataObject;

            if (!GUIUtils.isPreviewFullscreen()) {
                dataObject = Control.getSelectionControl().getCollection().get(0);
            } else {
                dataObject = Control.getFocusControl().getCurrentFocus();
            }

            Utils.setClipboardContent(dataObject.getName());
        });
    }
    private static void onAction_menuDelete() {
        GUIInstance.getDataObjectContextMenu().getMenuDelete().setOnAction(event -> {
            DataCollection dataObjectsValid = Control.getFilterControl().getCollection();

            if (!GUIUtils.isPreviewFullscreen()) {
                deleteDataObject(GUINode.GALLERYPANE, Control.getSelectionControl().getCollection(), dataObjectsValid);
            } else {
                deleteDataObject(GUINode.PREVIEWPANE, Control.getFocusControl().getCurrentFocus(), dataObjectsValid);
            }
        });
    }

    private static void deleteDataObject(GUINode sender, DataObject dataObject, DataCollection dataObjectsValid) {
        int index = dataObjectsValid.indexOf(dataObject);

        String pathString = Settings.getPath_source() + "\\" + dataObject.getName();
        Path path = Paths.get(pathString);

        try {
            Files.delete(path);

            dataObjectsValid.remove(dataObject);
            DataControl.remove(dataObject);
            Control.getSelectionControl().getCollection().remove(dataObject);

            if (dataObjectsValid.get(index - 1) != null) {
                index--;
            } else if (dataObjectsValid.get(index + 1) != null) {
                index++;
            }

            Control.getFocusControl().setFocus(dataObjectsValid.get(index));
            Control.getReloadControl().reload(true, sender);
        } catch (IOException e) {
            System.out.println("IOException: Trying to delete non-existent file; Path: " + pathString);
            e.printStackTrace();
        }
    }
    private static void deleteDataObject(GUINode sender, DataCollection dataObjects, DataCollection dataObjectsValid) {
        for (DataObject dataObject : dataObjects) {
            if (dataObjectsValid.contains(dataObject)) {
                deleteDataObject(sender, dataObject, dataObjectsValid);
            }
        }
    }
}
