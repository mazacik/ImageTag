package project.userinput.gui;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.gui.component.PreviewPane;
import project.helper.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class UserInputPreviewPane {
    public static void initialize() {
        setOnMouseClicked_canvas();
        setOnAction_menuCopy();
        setOnAction_menuDelete();
        setSizeListener_canvas();
    }

    private static void setOnMouseClicked_canvas() {
        PreviewPane.getCanvas().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                PreviewPane.getInstance().requestFocus();
                PreviewPane.getContextMenu().hide();
            } else if (event.getButton() == MouseButton.SECONDARY) {
                DataElement dataElement = FocusControl.getCurrentFocus();
                FocusControl.setFocus(dataElement);
                SelectionControl.addDataElement(dataElement);
                PreviewPane.getContextMenu().show(PreviewPane.getInstance(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    public static void setOnAction_menuCopy() {
        PreviewPane.getMenuCopy().setOnAction(event -> {
            DataElement dataElement = FocusControl.getCurrentFocus();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(dataElement.getName());
            clipboard.setContent(content);
        });

    }
    public static void setOnAction_menuDelete() {
        PreviewPane.getMenuDelete().setOnAction(event -> {
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
                ReloadControl.requestReloadOf(true, PreviewPane.class);
            }
        });
    }

    private static void setSizeListener_canvas() {
        Canvas canvas = PreviewPane.getCanvas();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            ReloadControl.requestReloadOf(true, PreviewPane.class);
        };
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
