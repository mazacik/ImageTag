package project.gui.component.part;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.common.Settings;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.element.DataElement;

public class GalleryTile extends ImageView {
    /* vars */
    private static final InnerShadow effectSelectionBorder = buildSelectionBorderEffect();
    private static final ColorInput effectFocusMark = buildSelectionFocusMarkEffect();

    private static final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* constructors */
    public GalleryTile(DataElement dataElement) {
        super(dataElement.getImage());
        setFitWidth(galleryIconSizePref);
        setFitHeight(galleryIconSizePref);
        setOnMouseClick(dataElement);
    }

    /* public */
    public static void generateEffect(DataElement dataElement) {
        DataElement currentFocus = FocusControl.getCurrentFocus();

        boolean booleanSelection = SelectionControl.getDataElements().contains(dataElement);
        boolean booleanFocus = false;
        if (currentFocus != null)
            booleanFocus = currentFocus.equals(dataElement);

        if (!booleanSelection && !booleanFocus) {
            dataElement.getGalleryTile().setEffect(null);
        } else if (!booleanSelection && booleanFocus) {
            Blend blend = new Blend();
            blend.setTopInput(effectFocusMark);
            dataElement.getGalleryTile().setEffect(blend);
        } else if (booleanSelection && !booleanFocus) {
            dataElement.getGalleryTile().setEffect(effectSelectionBorder);
        } else if (booleanSelection && booleanFocus) {
            Blend effect = new Blend();
            effect.setTopInput(effectSelectionBorder);
            effect.setBottomInput(effectFocusMark);
            effect.setMode(BlendMode.OVERLAY);
            dataElement.getGalleryTile().setEffect(effect);
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
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                FocusControl.setFocus(dataElement);
                SelectionControl.swapSelectionStateOf(dataElement);
            }
        });
    }
}
