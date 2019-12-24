package baseobject.entity;

import baseobject.tag.TagList;
import gui.main.center.GalleryTile;
import main.InstanceCollector;
import tools.FileUtil;

import java.io.File;
import java.io.Serializable;

public class Entity implements InstanceCollector, Serializable {
	private String name;
	private TagList tagList;
	private long length;
	private int collectionID;
	
	private transient EntityList collection;
	private transient GalleryTile galleryTile;
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagList = new TagList();
		this.length = file.length();
		this.collectionID = 0;
		
		this.collection = null;
		this.galleryTile = new GalleryTile(this);
	}
	
	public String getName() {
		return name;
	}
	public TagList getTagList() {
		return tagList;
	}
	public long getLength() {
		return length;
	}
	public int getCollectionID() {
		return collectionID;
	}
	
	public EntityList getCollection() {
		return collection;
	}
	public GalleryTile getGalleryTile() {
		if (galleryTile == null) galleryTile = new GalleryTile(this);
		return galleryTile;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}
	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}
	
	public void setCollection(EntityList collection) {
		this.collection = collection;
	}
}
