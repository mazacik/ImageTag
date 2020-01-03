package base.entity;

import base.tag.TagList;
import enums.MediaType;
import misc.FileUtil;
import ui.main.center.GalleryTile;

import java.io.File;

public class Entity {
	private String name;
	private TagList tagList;
	private long size;
	private int collectionID;
	
	private MediaType mediaType;
	private long mediaDuration;
	
	private transient EntityList collection;
	private transient GalleryTile galleryTile;
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagList = new TagList();
		this.size = file.length();
		this.collectionID = 0;
		
		this.mediaType = null;
		this.mediaDuration = 0;
		
		this.collection = null;
		this.galleryTile = new GalleryTile(this);
	}
	
	public String getName() {
		return name;
	}
	public TagList getTagList() {
		return tagList;
	}
	public long getSize() {
		return size;
	}
	public int getCollectionID() {
		return collectionID;
	}
	
	public MediaType getMediaType() {
		if (mediaType == null) {
			mediaType = FileUtil.getMediaType(this);
		}
		return mediaType;
	}
	public long getMediaDuration() {
		return mediaDuration;
	}
	
	public EntityList getCollection() {
		return collection;
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
	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}
	
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	
	public void setCollection(EntityList collection) {
		this.collection = collection;
	}
}
