package project.backend.database;

import javafx.scene.image.Image;
import project.backend.component.GalleryTile;

import java.io.Serializable;
import java.util.ArrayList;

public class DatabaseItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private ArrayList<String> tags;
    private transient Image image;
    private transient GalleryTile galleryTile;


    public String getName() {
        return name;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public Image getImage() {
        return image;
    }

    public GalleryTile getGalleryTile() {
        return galleryTile;
    }

    public void setName(String SimpleName) {
        this.name = SimpleName;
    }

    public void setTags(ArrayList<String> Tags) {
        this.tags = Tags;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setGalleryTile(GalleryTile galleryTile) {
        this.galleryTile = (galleryTile != null) ? galleryTile : new GalleryTile(this);
    }
}
