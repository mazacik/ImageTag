package project.gui.component.gallery.part;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.backend.Selection;
import project.backend.Settings;
import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.gui.GUIStage;
import project.gui.component.gallery.GalleryPane;

public class GalleryTile extends ImageView {
    private static InnerShadow selectionBorder = buildSelectionBorderEffect();
    private static ColorInput focusMark = buildSelectionFocusMarkEffect();

    private static final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    public GalleryTile(DatabaseItem databaseItem) {
        super(databaseItem.getImage());
        setFitWidth(galleryIconSizePref);
        setFitHeight(galleryIconSizePref);
        setOnMouseClick(databaseItem);
    }

    public static void generateEffect(DatabaseItem databaseItem) {
        GalleryPane galleryPane = GUIStage.getGalleryPane();

        boolean selection = ItemDatabase.getDatabaseItemsSelected().contains(databaseItem);
        boolean focus = false;
        if (galleryPane.getCurrentFocusedItem() != null)
            focus = galleryPane.getCurrentFocusedItem().equals(databaseItem);

        if (!selection && !focus) {
            databaseItem.getGalleryTile().setEffect(null);
        } else if (!selection && focus) {
            Blend blend = new Blend();
            blend.setTopInput(focusMark);
            databaseItem.getGalleryTile().setEffect(blend);
        } else if (selection && !focus) {
            databaseItem.getGalleryTile().setEffect(selectionBorder);
        } else if (selection && focus) {
            Blend effect = new Blend();
            effect.setTopInput(selectionBorder);
            effect.setBottomInput(focusMark);
            effect.setMode(BlendMode.OVERLAY);
            databaseItem.getGalleryTile().setEffect(effect);
        }
    }

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

    private void setOnMouseClick(DatabaseItem databaseItem) {
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                GUIStage.getGalleryPane().focusTile(databaseItem);
                Selection.swap(databaseItem);
            }
        });
    }
}
