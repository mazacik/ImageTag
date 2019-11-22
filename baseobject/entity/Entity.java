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
	private int entityGroupID;
	
	private transient EntityList entityGroup;
	private transient GalleryTile galleryTile;
	
	private transient String filePath;
	
	public Entity(File file) {
		this.name = FileUtil.getNameForEntity(file);
		this.tagList = new TagList();
		this.length = file.length();
		this.entityGroupID = 0;
		
		this.entityGroup = null;
		this.galleryTile = new GalleryTile(this);
		
		this.filePath = file.getAbsolutePath();
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
	
	public EntityList getEntityGroup() {
		return entityGroup;
	}
	public GalleryTile getGalleryTile() {
		if (galleryTile == null) galleryTile = new GalleryTile(this);
		return galleryTile;
	}
	public String getFilePath() {
		return filePath;
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
	
	public void setEntityGroup(EntityList entityGroup) {
		this.entityGroup = entityGroup;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
