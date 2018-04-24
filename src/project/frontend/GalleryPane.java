package project.frontend;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.backend.Database;
import project.backend.DatabaseItem;

public class GalleryPane extends ScrollPane {
    private static final TilePane tilePane = new TilePane();
    private static final InnerShadow highlightEffect = new InnerShadow();

    GalleryPane() {
        tilePane.setVgap(1);
        tilePane.setHgap(1);

        highlightEffect.setColor(Color.RED);
        highlightEffect.setOffsetX(0);
        highlightEffect.setOffsetY(0);
        highlightEffect.setWidth(5);
        highlightEffect.setHeight(5);
        highlightEffect.setChoke(1);

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setFitToWidth(true);
        setContent(tilePane);
    }

    public static void imageViewClicked(DatabaseItem databaseItem) {
        /* assigned in DatabaseItem.setImageView() */
        Database.setLastSelectedItem(databaseItem);
        if (!Database.getSelectedItems().contains(databaseItem))
            Database.addToSelection(databaseItem);
        else
            Database.removeIndexFromSelection(databaseItem);
    }

    public static InnerShadow getHighlightEffect() {
        return highlightEffect;
    }

    public void refreshContent() {
        tilePane.getChildren().clear();
        for (DatabaseItem item : Database.getFilteredItems()) {
            tilePane.getChildren().add(item.getImageView());
        }
    }
}