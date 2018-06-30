package project.gui.component.part;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.common.Settings;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.DataElementDatabase;
import project.database.element.DataElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GalleryTile extends ImageView {
    /* const */
    private static final InnerShadow effectSelectionBorder = buildSelectionBorderEffect();
    private static final ColorInput effectFocusMark = buildSelectionFocusMarkEffect();

    private static final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* vars */
    private final DataElement parentDataElement;

    /* constructors */
    public GalleryTile(DataElement dataElement) {
        super(dataElement.getImage());
        parentDataElement = dataElement;
        setFitWidth(galleryIconSizePref);
        setFitHeight(galleryIconSizePref);
        setOnMouseClick(dataElement);
    }

    /* public */
    public void generateEffect() {
        boolean booleanSelection = false;
        if (parentDataElement != null) {
            booleanSelection = SelectionControl.getDataElements().contains(parentDataElement);
        }

        DataElement currentFocus = FocusControl.getCurrentFocus();
        boolean booleanFocus = false;
        if (currentFocus != null) {
            booleanFocus = currentFocus.equals(parentDataElement);
        }

        if (!booleanSelection && !booleanFocus) {
            setEffect(null);
        } else if (!booleanSelection && booleanFocus) {
            Blend blend = new Blend();
            blend.setTopInput(effectFocusMark);
            setEffect(blend);
        } else if (booleanSelection && !booleanFocus) {
            setEffect(effectSelectionBorder);
        } else if (booleanSelection && booleanFocus) {
            Blend effect = new Blend();
            effect.setTopInput(effectSelectionBorder);
            effect.setBottomInput(effectFocusMark);
            effect.setMode(BlendMode.OVERLAY);
            setEffect(effect);
        }
    }

    /* builder */
    private static InnerShadow buildSelectionBorderEffect() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.RED);
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(0);
        innerShadow.setWidth(5);
        innerShadow.setHeight(5);
        innerShadow.setChoke(1);
        return innerShadow;
    }
    private static ColorInput buildSelectionFocusMarkEffect() {
        int markSize = 6;
        int markPositionInTile = (Settings.getGalleryIconSizePref() - markSize) / 2;
        Color markColor = Color.RED;
        return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, markColor);
    }

    /* event */
    private void setOnMouseClick(DataElement dataElement) {
        setOnMouseClicked(event -> {
            MouseButton eventButton = event.getButton();
            if (eventButton.equals(MouseButton.PRIMARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.swapSelectionStateOf(dataElement);
            } else if (eventButton.equals(MouseButton.SECONDARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.addDataElement(dataElement);
                rightClickContextMenu(this, event.getScreenX(), event.getScreenY());
            }
        });
    }
    private void rightClickContextMenu(Node anchor, double screenX, double screenY) {
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

                DataElementDatabase.getDataElements().remove(parentDataElement);
                FilterControl.getValidDataElements().remove(parentDataElement);
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
        contextMenu.show(anchor, screenX, screenY);
    }
}
