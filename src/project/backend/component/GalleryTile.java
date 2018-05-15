package project.backend.component;

import javafx.scene.image.ImageView;
import project.backend.listener.ListenerTemplate;
import project.backend.database.DatabaseItem;

public class GalleryTile extends ImageView {


    public GalleryTile(DatabaseItem databaseItem) {
        super(databaseItem.getImage());
        setOnMouseClicked(event -> ListenerTemplate.setGalleryTileMouseClick(event, databaseItem));
    }
}
