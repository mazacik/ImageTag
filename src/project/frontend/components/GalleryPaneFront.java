package project.frontend.components;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.Main;
import project.frontend.shared.Frontend;
import project.frontend.shared.RightClickContextMenu;

import java.util.ArrayList;

public class GalleryPaneFront extends ScrollPane {
    private final TilePane tilePane = new TilePane();
    private final InnerShadow highlightEffect = new InnerShadow();

    public GalleryPaneFront() {
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(Main.GALLERY_ICON_SIZE_PREF);
        tilePane.setPrefTileHeight(Main.GALLERY_ICON_SIZE_PREF);
        tilePane.widthProperty().addListener((observable, oldValue, newValue) -> recalculateHgap());

        /* gallery zoom */
        tilePane.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();

                if (event.getDeltaY() < 0) {
                    Main.GALLERY_ICON_SIZE_PREF -= 10;
                    if (Main.GALLERY_ICON_SIZE_PREF < Main.GALLERY_ICON_SIZE_MIN)
                        Main.GALLERY_ICON_SIZE_PREF = Main.GALLERY_ICON_SIZE_MIN;
                } else {
                    Main.GALLERY_ICON_SIZE_PREF += 10;
                    if (Main.GALLERY_ICON_SIZE_PREF > Main.GALLERY_ICON_SIZE_MAX)
                        Main.GALLERY_ICON_SIZE_PREF = Main.GALLERY_ICON_SIZE_MAX;
                }

                tilePane.setPrefTileWidth(Main.GALLERY_ICON_SIZE_PREF);
                tilePane.setPrefTileHeight(Main.GALLERY_ICON_SIZE_PREF);
                for (DatabaseItem databaseItem : Database.getDatabaseItems()) {
                    databaseItem.getImageView().setFitWidth(Main.GALLERY_ICON_SIZE_PREF);
                    databaseItem.getImageView().setFitHeight(Main.GALLERY_ICON_SIZE_PREF);
                }
                recalculateHgap();
            }
        });

        highlightEffect.setColor(Color.RED);
        highlightEffect.setOffsetX(0);
        highlightEffect.setOffsetY(0);
        highlightEffect.setWidth(5);
        highlightEffect.setHeight(5);
        highlightEffect.setChoke(1);

        setContextMenu(new RightClickContextMenu());
        setOnContextMenuRequested(event -> getContextMenu().show(this, event.getScreenX(), event.getScreenY()));
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setMinViewportWidth(Main.GALLERY_ICON_SIZE_PREF);
        setFitToWidth(true);
        setContent(tilePane);
    }

    public void highlight(DatabaseItem databaseItem, boolean visible) {
        if (visible)
            databaseItem.getImageView().setEffect(highlightEffect);
        else
            databaseItem.getImageView().setEffect(null);
    }

    private void recalculateHgap() {
        if (getColumnCount() != 0) tilePane.setHgap((int) tilePane.getWidth() % (int) tilePane.getPrefTileWidth() / getColumnCount());
    }

    private int getColumnCount() {
        return (int) tilePane.getWidth() / (int) tilePane.getPrefTileWidth();
    }

    public TilePane getTilePane() {
        return tilePane;
    }
}
