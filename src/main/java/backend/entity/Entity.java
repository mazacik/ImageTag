package backend.entity;

import backend.BaseList;
import backend.group.EntityGroup;
import backend.misc.FileUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import frontend.component.center.gallery.Tile;
import main.Main;

import java.io.File;

public class Entity {
	
	@JsonProperty("n") private String name;
	@JsonProperty("t") private BaseList<String> tagList;
	@JsonProperty("s") private long size;
	@JsonProperty("g") private int groupID;
	@JsonProperty("d") private long mediaDuration;
	@JsonProperty("c") private boolean keepCache;
	@JsonProperty("i") private long importDate;
	@JsonProperty("l") private int likes;
	
	private transient EntityType mediaType;
	private transient EntityGroup group;
	private transient Tile tile;
	
	public Entity() {
	}
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagList = new BaseList<>();
		this.size = file.length();
		this.groupID = 0;
		this.mediaType = null;
		this.mediaDuration = 0;
		this.keepCache = false;
		this.importDate = 0;
		this.likes = 0;
		
		this.group = null;
		this.tile = null;
	}
	
	public Entity getRepresentingEntity() {
		if (this.hasGroup()) {
			if (group.isOpen()) {
				return this;
			} else {
				return Main.FILTER.getFilteredList(group).getFirst();
			}
		} else {
			return this;
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public BaseList<String> getTagList() {
		return tagList;
	}
	
	public long getSize() {
		return size;
	}
	
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public boolean hasGroup() {
		return groupID != 0;
	}
	public void discardGroup() {
		groupID = 0;
		group = null;
		if (tile != null) tile.setEffect(null);
	}
	
	public long getMediaDuration() {
		return mediaDuration;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	
	public boolean isKeepCache() {
		return keepCache;
	}
	public void setKeepCache(boolean keepCache) {
		this.keepCache = keepCache;
	}
	
	public long getImportDate() {
		return importDate;
	}
	public void setImportDate(long importDate) {
		this.importDate = importDate;
	}
	
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	public EntityType getMediaType() {
		return mediaType;
	}
	public void initType() {
		mediaType = FileUtil.getMediaType(this);
	}
	
	public EntityGroup getGroup() {
		return group;
	}
	public void setGroup(EntityGroup group) {
		this.group = group;
	}
	
	public Tile getTile() {
		return tile;
	}
	public void initTile() {
		this.tile = new Tile(this);
	}
	
}
