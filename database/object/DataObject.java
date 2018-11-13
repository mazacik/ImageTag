package database.object;

import gui.component.gallerypane.GalleryTile;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

public class DataObject implements Serializable {
    private String name;
    private TagCollection tagCollection;
    private transient GalleryTile galleryTile;

    public DataObject(String name, TagCollection tagObjects) {
        this.name = name;
        this.tagCollection = tagObjects;
    }
    public DataObject(File file) {
        this(file.getName(), new TagCollection());
    }
    public static Comparator getComparator() {
        return Comparator.comparing(DataObject::getName);
    }
    public void generateTileEffect() {
        galleryTile.generateEffect();
    }
    public String getName() {
        return name;
    }
    public TagCollection getTagCollection() {
        return tagCollection;
    }
    public GalleryTile getGalleryTile() {
        return galleryTile;
    }
    public void setGalleryTile(GalleryTile galleryTile) {
        this.galleryTile = (galleryTile != null) ? galleryTile : new GalleryTile(this, null);
    }
}
