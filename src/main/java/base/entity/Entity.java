package base.entity;

import base.CustomList;
import com.google.gson.annotations.SerializedName;
import enums.MediaType;
import misc.FileUtil;
import ui.main.gallery.Tile;

import java.io.File;

public class Entity {
	@SerializedName("n") private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@SerializedName("t") private CustomList<Integer> tagIDs;
	public CustomList<Integer> getTagIDs() {
		return tagIDs;
	}
	public void setTagIDs(CustomList<Integer> tagIDs) {
		this.tagIDs = tagIDs;
	}
	
	@SerializedName("s") private long size;
	public long getSize() {
		return size;
	}
	
	@SerializedName("c") private int collectionID;
	public int getCollectionID() {
		return collectionID;
	}
	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}
	
	@SerializedName("f") private MediaType mediaType;
	public MediaType getMediaType() {
		if (mediaType == null) {
			mediaType = FileUtil.getMediaType(this);
		}
		return mediaType;
	}
	
	@SerializedName("d") private long mediaDuration;
	public long getMediaDuration() {
		return mediaDuration;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	
	private transient EntityList collection;
	public EntityList getCollection() {
		return collection;
	}
	public void setCollection(EntityList collection) {
		this.collection = collection;
	}
	
	private transient Tile tile;
	public Tile getTile() {
		if (tile == null) {
			tile = new Tile(this);
		}
		return tile;
	}
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagIDs = new CustomList<>();
		this.size = file.length();
		this.collectionID = 0;
		
		this.mediaType = null;
		this.mediaDuration = 0;
		
		this.collection = null;
		this.tile = new Tile(this);
	}
}
