package backend.entity;

import backend.BaseList;
import backend.group.Group;
import backend.misc.FileUtil;
import com.google.gson.annotations.SerializedName;
import frontend.component.gallery.Tile;
import main.Main;

import java.io.File;

public class Entity {
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagList = new BaseList<>();
		this.size = file.length();
		this.groupID = 0;
		this.type = null;
		this.mediaDuration = 0;
		
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
	
	@SerializedName("n") private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@SerializedName("t") private final BaseList<String> tagList;
	public BaseList<String> getTagList() {
		return tagList;
	}
	
	@SerializedName("s") private final long size;
	public long getSize() {
		return size;
	}
	
	@SerializedName("g") private int groupID;
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public boolean hasGroup() {
		return groupID != 0;
	}
	
	//todo do not serialize stuff that can be created on init and does not need to be serialized
	@SerializedName("f") private EntityType type;
	public EntityType getType() {
		if (type == null) {
			type = FileUtil.getMediaType(this);
		}
		return type;
	}
	
	@SerializedName("d") private long mediaDuration;
	public long getMediaDuration() {
		return mediaDuration;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	
	@SerializedName("fav") private boolean favorite;
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	private transient Group group;
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	
	private transient Tile tile;
	public Tile getTile() {
		if (tile == null) {
			tile = new Tile(this);
		}
		return tile;
	}
}
