package project.userinput.gui;

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
                dataElement.getGalleryTile().showContextMenu(event.getScreenX(), event.getScreenY());
            }
        });
    }
    public static void setOnAction_menuCopy(DataElement dataElement) {
        dataElement.getGalleryTile().getMenuCopy().setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(dataElement.getName());
            clipboard.setContent(content);
        });

    }
    public static void setOnAction_menuDelete(DataElement dataElement) {
        dataElement.getGalleryTile().getMenuDelete().setOnAction(event -> {
            String parentDataElementName = dataElement.getName();
            try {
                Files.delete(Paths.get(Settings.getMainDirectoryPath() + "\\" + parentDataElementName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (FilterControl.getValidDataElements().contains(dataElement)) {
                int index = FilterControl.getValidDataElements().indexOf(dataElement);

                DataElementControl.remove(dataElement);
                FilterControl.getValidDataElements().remove(dataElement); //todo remove direct links like this
                SelectionControl.getDataElements().remove(dataElement);

                if (FilterControl.getValidDataElements().get(index - 1) != null) {
                    index--;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));
                } else if (FilterControl.getValidDataElements().get(index + 1) != null) {
                    index++;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));
                }
            }
        });
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
