package backend.entity;

import backend.BaseList;
import backend.group.EntityGroup;
import backend.misc.FileUtil;
import com.google.gson.annotations.SerializedName;
import frontend.component.center.gallery.Tile;
import main.Main;

import java.io.File;

public class Entity {
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagList = new BaseList<>();
		this.size = file.length();
		this.entityGroupID = 0;
		this.type = null;
		this.mediaDuration = 0;
		
		this.entityGroup = null;
		this.tile = null;
	}
	
	public Entity getRepresentingEntity() {
		if (this.hasGroup()) {
			if (entityGroup.isOpen()) {
				return this;
			} else {
				return Main.FILTER.getFilteredList(entityGroup).getFirst();
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
	
	@SerializedName("g") private int entityGroupID;
	public int getEntityGroupID() {
		return entityGroupID;
	}
	public void setEntityGroupID(int entityGroupID) {
		this.entityGroupID = entityGroupID;
	}
	public boolean hasGroup() {
		return entityGroupID != 0;
	}
	public void discardGroup() {
		entityGroupID = 0;
		entityGroup = null;
		if (tile != null) tile.setEffect(null);
	}
	
	@SerializedName("d") private long mediaDuration;
	public long getMediaDuration() {
		return mediaDuration;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	
	@SerializedName("f") private boolean favorite;
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	private transient EntityType type;
	public EntityType getType() {
		return type;
	}
	public void initType() {
		type = FileUtil.getMediaType(this);
	}
	
	private transient EntityGroup entityGroup;
	public EntityGroup getEntityGroup() {
		return entityGroup;
	}
	public void setEntityGroup(EntityGroup entityGroup) {
		this.entityGroup = entityGroup;
	}
	
	private transient Tile tile;
	public Tile getTile() {
		return tile;
	}
	public void initTile() {
		this.tile = new Tile(this);
	}
}
