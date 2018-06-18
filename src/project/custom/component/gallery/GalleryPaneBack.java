package project.custom.component.gallery;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import project.common.Database;
import project.common.Utility;
import project.database.DatabaseItem;

public class GalleryPaneBack {
    /* lazy singleton */
    private static GalleryPaneBack instance;
    public static GalleryPaneBack getInstance() {
        if (instance == null) instance = new GalleryPaneBack();
        return instance;
    }

    /* imports */
    private final TilePane tilePane = GalleryPaneFront.getInstance().getTilePane();
    private final ObservableList<Node> galleryTiles = tilePane.getChildren();

    /* constructors */
    private GalleryPaneBack() {
        GalleryPaneListener.getInstance();
    }

    /* public methods */
    public void reloadContent() {
        if (Utility.isPreviewFullscreen()) return;
        galleryTiles.clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItemsFiltered())
            galleryTiles.add(databaseItem.getGalleryTile());
    }

    public int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }

    public void adjustViewportPositionToFocus(){
        double columnCount = GalleryPaneBack.getInstance().getColumnCount();
        double currentIndex = GalleryPaneFront.getInstance().getTilePane().getChildren().indexOf(GalleryPaneFront.getInstance().getCurrentFocusedItem().getGalleryTile());
        int currentRow = (int) (currentIndex / columnCount);

        double viewportHeight = GalleryPaneFront.getInstance().getViewportBounds().getHeight();
        double contentHeight = GalleryPaneFront.getInstance().getTilePane().getHeight() - viewportHeight;
        double vgap = GalleryPaneFront.getInstance().getTilePane().getVgap();
        double rowHeight = GalleryPaneFront.getInstance().getTilePane().getPrefTileHeight() + vgap;

        double rowToContentRatio = rowHeight / contentHeight;
        double viewportToContentRatio = viewportHeight / contentHeight;

        Bounds viewportBounds = GalleryPaneFront.getInstance().getViewportBounds();
        Bounds tileBounds = GalleryPaneFront.getInstance().getTilePane().getChildren().get((int)currentIndex).getBoundsInParent();

        double viewportTop = viewportBounds.getMaxY() * -1 + viewportBounds.getHeight();
        double viewportBottom = viewportBounds.getMinY() * -1 + viewportBounds.getHeight();
        double tileTop = tileBounds.getMaxY();
        double tileBottom = tileBounds.getMinY();

        if (viewportTop + rowHeight > tileTop) {
            GalleryPaneFront.getInstance().setVvalue(currentRow * rowToContentRatio);
        } else if (viewportBottom - rowHeight < tileBottom) {
            GalleryPaneFront.getInstance().setVvalue((currentRow + 1) * rowToContentRatio - viewportToContentRatio);
        }
    }
}
