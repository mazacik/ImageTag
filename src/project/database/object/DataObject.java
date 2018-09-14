package project.database.object;

import javafx.scene.image.Image;
import project.gui.component.gallerypane.GalleryTile;

import java.io.Serializable;
import java.util.Comparator;

public class DataObject implements Serializable {
    /* vars */
    private String name;
    private TagCollection tagCollection;

    private transient Image image;
    private transient GalleryTile galleryTile;

    /* constructors */
    public DataObject(String name, TagCollection tagObjects) {
        this.name = name;
        this.tagCollection = tagObjects;
    }

    /* get */
    public String getName() {
        return name;
    }
    public TagCollection getTagCollection() {
        return tagCollection;
    }
    public Image getImage() {
        return image;
    }
    public GalleryTile getGalleryTile() {
        return galleryTile;
    }

    public static Comparator getComparator() {
        return Comparator.comparing(DataObject::getName);
    }

    /* set */
    public void setImage(Image image) {
        this.image = image;
    }
    public void setGalleryTile(GalleryTile galleryTile) {
        this.galleryTile = (galleryTile != null) ? galleryTile : new GalleryTile(this);
    }
}