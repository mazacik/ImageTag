package project.gui.component.part;

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
import project.gui.component.GalleryPane;

public class GalleryTile extends ImageView {
    /* variables */
    private static final InnerShadow effectSelectionBorder = buildSelectionBorderEffect();
    private static final ColorInput effectFocusMark = buildSelectionFocusMarkEffect();

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
        GalleryPane galleryPane = GUIStage.getGalleryPane();

        boolean selection = ItemDatabase.getDatabaseItemsSelected().contains(databaseItem);
        boolean focus = false;
        if (galleryPane.getCurrentFocusedItem() != null)
            focus = galleryPane.getCurrentFocusedItem().equals(databaseItem);

        if (!selection && !focus) {
            databaseItem.getGalleryTile().setEffect(null);
        } else if (!selection && focus) {
            Blend blend = new Blend();
            blend.setTopInput(effectFocusMark);
            databaseItem.getGalleryTile().setEffect(blend);
        } else if (selection && !focus) {
            databaseItem.getGalleryTile().setEffect(effectSelectionBorder);
        } else if (selection && focus) {
            Blend effect = new Blend();
            effect.setTopInput(effectSelectionBorder);
            effect.setBottomInput(effectFocusMark);
            effect.setMode(BlendMode.OVERLAY);
            databaseItem.getGalleryTile().setEffect(effect);
        }
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

    /* event methods */
    private void setOnMouseClick(DatabaseItem databaseItem) {
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                GUIStage.getGalleryPane().focusTile(databaseItem);
                Selection.swap(databaseItem);
            }
        });
    }
}
