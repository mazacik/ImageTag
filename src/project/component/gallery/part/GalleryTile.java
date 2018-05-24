package project.component.gallery.part;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.common.Selection;
import project.common.Settings;
import project.component.gallery.GalleryPaneFront;
import project.database.Database;
import project.database.DatabaseItem;

public class GalleryTile extends ImageView {
    /* imports */
    private final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* variables */
    private static InnerShadow selectionBorder = buildSelectionBorderEffect();

    /* constructors */
    public GalleryTile(DatabaseItem databaseItem) {
        super(databaseItem.getImage());
        setFitWidth(galleryIconSizePref);
        setFitHeight(galleryIconSizePref);
        setOnMouseClick(databaseItem);
    }

    //todo: fix me
    /* public methods */
    public void generateEffect(DatabaseItem databaseItem) {
        boolean selection = Database.getDatabaseItemsSelected().contains(databaseItem);
        boolean focus = GalleryPaneFront.getInstance().getCurrentFocusedItem().equals(databaseItem);

        if (!selection && !focus) {
            databaseItem.getGalleryTile().setEffect(null);
        } else if (!selection && focus) {
            int markSize = 6;
            int markPosition = (galleryIconSizePref - markSize) / 2;
            Blend effect = new Blend();
            effect.setTopInput(new ColorInput(markPosition, markPosition, markSize, markSize, Color.RED));
            databaseItem.getGalleryTile().setEffect(effect);
        } else if (selection && !focus) {
            databaseItem.getGalleryTile().setEffect(selectionBorder);
        } else if (selection && focus) {
            int markSize = 6;
            int markPosition = (galleryIconSizePref - markSize) / 2;
            Blend effect = new Blend();
            effect.setTopInput(selectionBorder);
            effect.setBottomInput(new ColorInput(markPosition, markPosition, markSize, markSize, Color.RED));
            effect.setMode(BlendMode.OVERLAY);
            databaseItem.getGalleryTile().setEffect(effect);
        }
    }

    /* private methods */
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

    /* event methods */
    private void setOnMouseClick(DatabaseItem databaseItem) {
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                GalleryPaneFront.getInstance().focusTile(databaseItem);
                Selection.getInstance().swap(databaseItem);
            }
        });
    }
}
