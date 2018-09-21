package project.gui.event.global;

import project.control.DataControl;
import project.control.MainControl;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.GUIInstance;
import project.gui.component.GUINode;
import project.settings.Settings;
import project.utils.ClipboardUtil;

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

            if (!GUIInstance.isPreviewFullscreen()) {
                dataObject = MainControl.getSelectionControl().getCollection().get(0);
            } else {
                dataObject = MainControl.getFocusControl().getCurrentFocus();
            }

            ClipboardUtil.setClipboardContent(dataObject.getName());
        });
    }
    private static void onAction_menuDelete() {
        GUIInstance.getDataObjectContextMenu().getMenuDelete().setOnAction(event -> {
            DataCollection dataObjectsValid = MainControl.getFilterControl().getCollection();

            if (!GUIInstance.isPreviewFullscreen()) {
                deleteDataObject(GUINode.GALLERYPANE, MainControl.getSelectionControl().getCollection(), dataObjectsValid);
            } else {
                deleteDataObject(GUINode.PREVIEWPANE, MainControl.getFocusControl().getCurrentFocus(), dataObjectsValid);
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
            MainControl.getSelectionControl().getCollection().remove(dataObject);

            if (dataObjectsValid.get(index - 1) != null) {
                index--;
            } else if (dataObjectsValid.get(index + 1) != null) {
                index++;
            }

            MainControl.getFocusControl().setFocus(dataObjectsValid.get(index));
            MainControl.getReloadControl().reload(true, sender);
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
