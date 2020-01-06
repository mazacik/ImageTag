package base.entity;

import base.tag.TagList;
import com.google.gson.annotations.SerializedName;
import enums.MediaType;
import misc.FileUtil;
import ui.main.gallery.Tile;

import java.io.File;

public class Entity {
	@SerializedName("n") private String name;
	@SerializedName("t") private TagList tagList;
	@SerializedName("s") private long size;
	@SerializedName("c") private int collectionID;
	
	@SerializedName("f") private MediaType mediaType;
	@SerializedName("d") private long mediaDuration;
	
	private transient EntityList collection;
	private transient Tile tile;
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagList = new TagList();
		this.size = file.length();
		this.collectionID = 0;
		
		this.mediaType = null;
		this.mediaDuration = 0;
		
		this.collection = null;
		this.tile = new Tile(this);
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
	public Tile getTile() {
		if (tile == null) {
			tile = new Tile(this);
		}
		return tile;
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
