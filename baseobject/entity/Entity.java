package baseobject.entity;

import baseobject.tag.TagList;
import gui.main.center.GalleryTile;
import main.InstanceCollector;

import java.io.File;
import java.io.Serializable;

public class Entity implements InstanceCollector, Serializable {
	private String name;
	private TagList tagList;
	private long length;
	private int entityGroupID;
	
	private transient String path;
	private transient GalleryTile galleryTile;
	private transient EntityList entityGroup;
	
	public Entity(File file) {
		this.name = file.getName();
		this.tagList = new TagList();
		this.length = file.length();
		this.entityGroupID = 0;
		
		this.path = file.getAbsolutePath();
		this.entityGroup = null;
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
	public EntityList getEntityGroup() {
		return entityGroup;
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
	public void setEntityGroup(EntityList entityGroup) {
		this.entityGroup = entityGroup;
	}
}
