package project.gui_components;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import project.backend.Database;
import project.backend.DatabaseItem;

public class GalleryPane extends ScrollPane {
    private static final TilePane tilePane = new TilePane();

    public GalleryPane() {
        //tilePane.setPadding(new Insets(10, 10, 10, 10));
        tilePane.setVgap(1);
        tilePane.setHgap(1);

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setFitToWidth(true);
        setContent(tilePane);
    }

    public void refreshContent() {
        tilePane.getChildren().clear();
        for (DatabaseItem item : Database.getItemDatabaseFiltered()) {
            ImageView imageView = new ImageView(item.getImage());
            tilePane.getChildren().add(imageView);
            imageView.setOnMouseClicked(event -> {
                int index = tilePane.getChildren().indexOf(imageView);
                Database.setLastSelectedIndex(index);
                if (!Database.getSelectedIndexes().contains(index))
                    Database.addToSelection(index);
                else
                    Database.removeIndexFromSelection(index);
            });
        }
    }

    public TilePane getTilePane() {
        return tilePane;
    }
}