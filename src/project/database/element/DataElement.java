package project.database.element;

import javafx.scene.image.Image;
import project.gui.component.GalleryPane.GalleryTile;

import java.io.Serializable;
import java.util.ArrayList;

public class DataElement implements Serializable {
    /* vars */
    private String name;
    private ArrayList<TagElement> tagElements;

    private transient Image image;
    private transient GalleryTile galleryTile;

    /* constructors */
    public DataElement(String name, ArrayList<TagElement> tagElements) {
        this.name = name;
        this.tagElements = tagElements;
    }

    /* get */
    public String getName() {
        return name;
    }
    public ArrayList<TagElement> getTagElements() {
        return tagElements;
    }
    public Image getImage() {
        return image;
    }
    public GalleryTile getGalleryTile() {
        return galleryTile;
    }

    /* set */
    public void setImage(Image image) {
        this.image = image;
    }
    public void setGalleryTile(GalleryTile galleryTile) {
        this.galleryTile = (galleryTile != null) ? galleryTile : new GalleryTile(this);
    }
}
