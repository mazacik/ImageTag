package project.userinput.gui;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.gui.component.GalleryPane;
import project.helper.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class UserInputGalleryPane {
    public static void initialize() {
        setOnScrollListener_tilePane();
        setWidthPropertyListener_tilePane();
    }

    public static void setOnMouseClicked_galleryTile(DataElement dataElement) {
        dataElement.getGalleryTile().setOnMouseClicked(event -> {
            MouseButton eventButton = event.getButton();
            if (eventButton.equals(MouseButton.PRIMARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.swapSelectionStateOf(dataElement);
            } else if (eventButton.equals(MouseButton.SECONDARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.addDataElement(dataElement);
                setContextMenu_galleryTile(dataElement, event.getScreenX(), event.getScreenY());
            }
        });
    }
    public static void setContextMenu_galleryTile(DataElement parentDataElement, double screenX, double screenY) {
        //todo split
        MenuItem menuDelete = new MenuItem("Delete Selection");
        menuDelete.setOnAction(event -> {
            String parentDataElementName = parentDataElement.getName();
            try {
                Files.delete(Paths.get(Settings.getMainDirectoryPath() + "\\" + parentDataElementName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (FilterControl.getValidDataElements().contains(parentDataElement)) {
                int index = FilterControl.getValidDataElements().indexOf(parentDataElement);

                DataElementControl.remove(parentDataElement);
                FilterControl.getValidDataElements().remove(parentDataElement); //todo remove direct links like this
                SelectionControl.getDataElements().remove(parentDataElement);

                if (FilterControl.getValidDataElements().get(index - 1) != null) {
                    index--;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));
                } else if (FilterControl.getValidDataElements().get(index + 1) != null) {
                    index++;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));
                }
            }
        });

        MenuItem menuCopy = new MenuItem("Copy Name");
        menuCopy.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(parentDataElement.getName());
            clipboard.setContent(content);
        });

        ContextMenu contextMenu = new ContextMenu();
        ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();
        if (SelectionControl.isSelectionSingleElement()) {
            contextMenuItems.add(menuCopy);
        }
        contextMenuItems.addAll(menuDelete);
        contextMenu.show(parentDataElement.getGalleryTile(), screenX, screenY);
    }

    private static void setOnScrollListener_tilePane() {
        int galleryIconSizeMax = Settings.getGalleryIconSizeMax();
        int galleryIconSizeMin = Settings.getGalleryIconSizeMin();
        int galleryIconSizePref = Settings.getGalleryIconSizePref();

        TilePane tilePane = GalleryPane.getTilePane();

        tilePane.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();

                if (event.getDeltaY() < 0) {
                    Settings.setGalleryIconSizePref(Settings.getGalleryIconSizePref() - 10);
                    if (galleryIconSizePref < galleryIconSizeMin)
                        Settings.setGalleryIconSizePref(galleryIconSizeMin);
                } else {
                    Settings.setGalleryIconSizePref(Settings.getGalleryIconSizePref() + 10);
                    if (galleryIconSizePref > galleryIconSizeMax)
                        Settings.setGalleryIconSizePref(galleryIconSizeMax);
                }

                tilePane.setPrefTileWidth(galleryIconSizePref);
                tilePane.setPrefTileHeight(galleryIconSizePref);

                for (DataElement dataElement : DataElementControl.getDataElementsLive()) {
                    dataElement.getGalleryTile().setFitWidth(galleryIconSizePref);
                    dataElement.getGalleryTile().setFitHeight(galleryIconSizePref);
                }
                GalleryPane.recalculateHgap();
            }
        });
    }
    private static void setWidthPropertyListener_tilePane() {
        GalleryPane.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> GalleryPane.recalculateHgap());
    }
}
