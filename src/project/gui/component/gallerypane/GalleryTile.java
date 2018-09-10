package project.gui.component.gallerypane;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.object.DataObject;
import project.gui.event.listener.gallerypane.EventListenerGalleryTile;
import project.settings.Settings;

public class GalleryTile extends ImageView {
    /* const */
    private static final InnerShadow EFFECT_SELECTIONBORDER = buildSelectionBorderEffect();
    private static final ColorInput EFFECT_FOCUSMARK = buildSelectionFocusMarkEffect();
    private static final int GALLERY_ICON_SIZE_PREF = Settings.getGalleryIconSizePref();

    /* vars */
    private final DataObject parentDataObject;

    /* constructors */
    public GalleryTile(DataObject dataObject) {
        super(dataObject.getImage());
        parentDataObject = dataObject;
        setFitWidth(GALLERY_ICON_SIZE_PREF);
        setFitHeight(GALLERY_ICON_SIZE_PREF);
        EventListenerGalleryTile.onMouseClick(this);
    }

    /* public */
    public void generateEffect() {
        boolean booleanSelection = false;
        if (parentDataObject != null) {
            booleanSelection = SelectionControl.getCollection().contains(parentDataObject);
        }

        DataObject currentFocus = FocusControl.getCurrentFocus();
        boolean booleanFocus = false;
        if (currentFocus != null) {
            booleanFocus = currentFocus.equals(parentDataObject);
        }

        if (!booleanSelection && !booleanFocus) {
            setEffect(null);
        } else if (!booleanSelection && booleanFocus) {
            Blend blend = new Blend();
            blend.setTopInput(EFFECT_FOCUSMARK);
            setEffect(blend);
        } else if (booleanSelection && !booleanFocus) {
            setEffect(EFFECT_SELECTIONBORDER);
        } else if (booleanSelection && booleanFocus) {
            Blend effect = new Blend();
            effect.setTopInput(EFFECT_SELECTIONBORDER);
            effect.setBottomInput(EFFECT_FOCUSMARK);
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

    /* get */
    public DataObject getParentDataObject() {
        return parentDataObject;
    }
}
