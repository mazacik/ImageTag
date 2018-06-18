package project.custom.component.gallery.part;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.common.Database;
import project.common.Selection;
import project.common.Settings;
import project.custom.component.gallery.GalleryPaneFront;
import project.database.DatabaseItem;

public class GalleryTile extends ImageView {
    /* variables */
    private static InnerShadow selectionBorder = buildSelectionBorderEffect();
    private static ColorInput focusMark = buildSelectionFocusMarkEffect();

    /* imports */
    private static final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* constructors */
    public GalleryTile(DatabaseItem databaseItem) {
        super(databaseItem.getImage());
        setFitWidth(galleryIconSizePref);
        setFitHeight(galleryIconSizePref);
        setOnMouseClick(databaseItem);
    }

    /* public methods */
    public static void generateEffect(DatabaseItem databaseItem) {
        boolean selection = Database.getDatabaseItemsSelected().contains(databaseItem);
        boolean focus = false;
        if (GalleryPaneFront.getInstance().getCurrentFocusedItem() != null)
            focus = GalleryPaneFront.getInstance().getCurrentFocusedItem().equals(databaseItem);

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

    /* event methods */
    private void setOnMouseClick(DatabaseItem databaseItem) {
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                GalleryPaneFront.getInstance().focusTile(databaseItem);
                Selection.swap(databaseItem);
            }
        });
    }

    /* builder methods */
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
}
