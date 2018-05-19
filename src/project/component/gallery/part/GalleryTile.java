package project.component.gallery.part;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.common.Selection;
import project.common.Settings;
import project.component.gallery.GalleryPaneFront;
import project.database.DatabaseItem;

public class GalleryTile extends ImageView {
    /* imports */
    private final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* variables */
    private final Blend selectionHighlight = buildSelectionHighlight();

    /* constructors */
    public GalleryTile(DatabaseItem databaseItem) {
        super(databaseItem.getImage());
        setFitWidth(galleryIconSizePref);
        setFitHeight(galleryIconSizePref);
        setOnMouseClick(databaseItem);
    }

    /* public methods */
    public void setHighlight() {
        if (getEffect() != buildSelectionHighlight()) setEffect(buildSelectionHighlight());
        else setEffect(null);
    }

    public void setHighlight(boolean value) {
        if (value) setEffect(selectionHighlight);
        else setEffect(null);
    }

    /* event methods */
    private void setOnMouseClick(DatabaseItem databaseItem) {
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                GalleryPaneFront.getInstance().setFocusedItem(databaseItem);
                Selection.getInstance().swap(databaseItem);
            }
        });
    }

    /* builder methods */
    private Blend buildSelectionHighlight() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.RED);
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(0);
        innerShadow.setWidth(5);
        innerShadow.setHeight(5);
        innerShadow.setChoke(1);

        Blend highlightEffect = new Blend();
        highlightEffect.setTopInput(innerShadow);
        highlightEffect.setBottomInput(getEffect());
        highlightEffect.setMode(BlendMode.SRC_OVER);
        return highlightEffect;
    }
}
