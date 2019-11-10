package application.base.entity;

import application.base.tag.TagList;
import application.main.InstanceCollector;
import application.pane.center.GalleryTile;

import java.io.File;
import java.io.Serializable;

public class Entity implements InstanceCollector, Serializable {
	private String name;
	private TagList tagList;
	private long length;
	private int entityGroupID;
	private transient String path;
	private transient GalleryTile galleryTile;
	
	public Entity(File file) {
		this.name = file.getName();
		this.tagList = new TagList();
		this.length = file.length();
		this.entityGroupID = 0;
		
		this.path = file.getAbsolutePath();
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
	public int getEntityGroupID() {
		return entityGroupID;
	}
	public String getPath() {
		return path;
	}
	public GalleryTile getGalleryTile() {
		if (galleryTile == null) {
			galleryTile = new GalleryTile(this);
		}
		return galleryTile;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}
	public void setEntityGroupID(int entityGroupID) {
		this.entityGroupID = entityGroupID;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
