package project.frontend;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.backend.Database;
import project.backend.DatabaseItem;
import project.backend.SharedBE;

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

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> SharedBE.renameFile(Database.getLastSelectedItem()));
        contextMenu.getItems().add(menuRename);
        setContextMenu(contextMenu);
        setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));


        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setFitToWidth(true);
        setContent(tilePane);
    }

    public static void imageViewClicked(DatabaseItem databaseItem, MouseEvent event) {
        /* assigned in DatabaseItem.setImageView() */
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            Database.setLastSelectedItem(databaseItem);
            if (!Database.getSelectedItems().contains(databaseItem))
                Database.addToSelection(databaseItem);
            else
                Database.removeIndexFromSelection(databaseItem);
        }
    }

    public void refreshContent() {
        tilePane.getChildren().clear();
        for (DatabaseItem item : Database.getFilteredItems()) {
            tilePane.getChildren().add(item.getImageView());
        }
    }

    public static InnerShadow getHighlightEffect() {
        return highlightEffect;
    }
}