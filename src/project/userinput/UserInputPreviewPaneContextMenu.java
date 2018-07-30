package project.userinput;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.gui.component.PreviewPane.PreviewPane;
import project.gui.component.PreviewPane.RightClickMenu;
import project.settings.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class UserInputPreviewPaneContextMenu {
    public static void initialize() {
        setOnAction_menuCopy();
        setOnAction_menuDelete();
    }

    public static void setOnAction_menuCopy() {
        RightClickMenu.getMenuCopy().setOnAction(event -> {
            DataElement dataElement = FocusControl.getCurrentFocus();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(dataElement.getName());
            clipboard.setContent(content);
        });

    }
    public static void setOnAction_menuDelete() {
        RightClickMenu.getMenuDelete().setOnAction(event -> {
            DataElement dataElement = FocusControl.getCurrentFocus();
            if (FilterControl.getValidDataElements().contains(dataElement)) {
                int index = FilterControl.getValidDataElements().indexOf(dataElement);

                DataElementControl.remove(dataElement);
                FilterControl.getValidDataElements().remove(dataElement);
                SelectionControl.getDataElements().remove(dataElement);

                try {
                    Files.delete(Paths.get(Settings.getMainDirectoryPath() + "\\" + dataElement.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (FilterControl.getValidDataElements().get(index - 1) != null) {
                    index--;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));
                } else if (FilterControl.getValidDataElements().get(index + 1) != null) {
                    index++;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));
                }
                ReloadControl.requestComponentReload(true, PreviewPane.class);
            }
        });
    }
}
