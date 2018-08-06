package project.gui.event.handler.contextmenu;

import project.control.FilterControl;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.DataObjectControl;
import project.database.element.DataObject;
import project.gui.GUIUtils;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.previewpane.PreviewPane;
import project.settings.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class EventHandlerContextMenuDelete {
    private static ArrayList<DataObject> dataObjectsValid;

    public static void onAction() {
        dataObjectsValid = FilterControl.getValidObjects();

        if (!GUIUtils.isPreviewFullscreen()) {
            doWork(GalleryPane.class, SelectionControl.getDataObjects());
        } else {
            doWork(PreviewPane.class, FocusControl.getCurrentFocus());
        }
    }

    private static void doWork(Class sender, DataObject dataObject) {
        int index = dataObjectsValid.indexOf(dataObject);

        dataObjectsValid.remove(dataObject);
        DataObjectControl.remove(dataObject);
        SelectionControl.getDataObjects().remove(dataObject);

        String pathString = Settings.getMainDirectoryPath() + "\\" + dataObject.getName();
        Path path = Paths.get(pathString);

        try {
            Files.delete(path);
        } catch (IOException e) {
            System.out.println("IOException: Trying to delete non-existent file; Path: " + pathString);
            e.printStackTrace();
        }

        if (dataObjectsValid.get(index - 1) != null) {
            index--;
        } else if (dataObjectsValid.get(index + 1) != null) {
            index++;
        }

        FocusControl.setFocus(dataObjectsValid.get(index));
        ReloadControl.requestComponentReload(true, sender);
    }
    private static void doWork(Class sender, ArrayList<DataObject> dataObjects) {
        for (DataObject dataObject : dataObjects) {
            if (dataObjectsValid.contains(dataObject)) {
                doWork(sender, dataObject);
            }
        }
    }
}
