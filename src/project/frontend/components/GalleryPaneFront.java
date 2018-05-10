package project.frontend.components;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.backend.shared.Main;
import project.frontend.shared.RightClickContextMenu;

public class GalleryPaneFront extends ScrollPane {
    private final TilePane tilePane = new TilePane();
    private final InnerShadow highlightEffect = new InnerShadow();

    public GalleryPaneFront() {
        setMinViewportWidth(Main.GALLERY_ICON_SIZE_PREF);
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(Main.GALLERY_ICON_SIZE_PREF);
        tilePane.setPrefTileHeight(Main.GALLERY_ICON_SIZE_PREF);
        tilePane.widthProperty().addListener((observable, oldValue, newValue) -> recalculateHgap());

        highlightEffect.setColor(Color.RED);
        highlightEffect.setOffsetX(0);
        highlightEffect.setOffsetY(0);
        highlightEffect.setWidth(5);
        highlightEffect.setHeight(5);
        highlightEffect.setChoke(1);

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
                for (DatabaseItem databaseItem : Database.getItemDatabase()) {
                    databaseItem.getImageView().setFitWidth(Main.GALLERY_ICON_SIZE_PREF);
                    databaseItem.getImageView().setFitHeight(Main.GALLERY_ICON_SIZE_PREF);
                }
                recalculateHgap();
            }
        });

        setContextMenu(new RightClickContextMenu());
        setOnContextMenuRequested(event -> getContextMenu().show(this, event.getScreenX(), event.getScreenY()));
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setFitToWidth(true);
        setContent(tilePane);
    }

    private void recalculateHgap() {
        int tileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = (int) tilePane.getWidth() / tileWidth;
        int emptySpace = (int) tilePane.getWidth() % tileWidth;
        if (columnCount != 0) tilePane.setHgap(emptySpace / columnCount);
    }

    public TilePane getTilePane() {
        return tilePane;
    }

    public InnerShadow getHighlightEffect() {
        return highlightEffect;
    }
}
