package project.userinput.gui;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.gui.component.GalleryPane;
import project.gui.component.part.GalleryTile;
import project.helper.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class UserInputGalleryPane {
    public static void initialize() {
        setOnScroll_tilePane();
        setWidthPropertyListener_tilePane();
    }

    public static void setOnMouseClicked_galleryTile(GalleryTile galleryTile) {
        galleryTile.setOnMouseClicked(event -> {
            DataElement dataElement = galleryTile.getParentDataElement();
            MouseButton eventButton = event.getButton();
            if (eventButton.equals(MouseButton.PRIMARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.swapSelectionStateOf(dataElement);
            } else if (eventButton.equals(MouseButton.SECONDARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.addDataElement(dataElement);
                galleryTile.showContextMenu(event.getScreenX(), event.getScreenY());
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
                ReloadControl.requestReloadOf(true, GalleryPane.class);
            }
        });
    }

    private static void setOnScroll_tilePane() {
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
