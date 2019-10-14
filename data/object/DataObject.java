package application.data.object;

import application.data.list.DataList;
import application.data.list.TagList;
import application.data.loader.utils.ThumbnailCreator;
import application.gui.panes.center.GalleryTile;
import application.main.Instances;
import application.misc.FileUtil;
import application.misc.enums.FileType;

import java.io.File;
import java.io.Serializable;
import java.util.logging.Logger;

public class DataObject implements Serializable {
	private String name;
	private long size;
	private int jointID;
	private TagList tagList;
	
	private transient String path;
	private transient GalleryTile galleryTile;
	
	protected DataObject() {
	
	}
	
	public DataObject(File file) {
		this.name = file.getName();
		this.size = file.length();
		this.jointID = 0;
		this.tagList = new TagList();
		
		this.path = file.getAbsolutePath();
	}
	
	public void requestTileEffect() {
		Instances.getReload().requestTileEffect(this);
	}
	public DataList getJointObjects() {
		if (jointID == 0) {
			return new DataList();
		} else {
			DataList jointObjects = new DataList();
			for (DataObject dataObject : Instances.getDataListMain()) {
				if (dataObject.getJointID() == jointID) {
					jointObjects.add(dataObject);
				}
			}
			return jointObjects;
		}
	}
	public FileType getFileType() {
		for (String ext : FileUtil.getImageExtensions()) {
			if (getName().toLowerCase().endsWith(ext)) {
				return FileType.IMAGE;
			}
		}
		for (String ext : FileUtil.getGifExtensions()) {
			if (getName().toLowerCase().endsWith(ext)) {
				return FileType.GIF;
			}
		}
		for (String ext : FileUtil.getVideoExtensions()) {
			if (getName().toLowerCase().endsWith(ext)) {
				return FileType.VIDEO;
			}
		}
		Logger.getGlobal().warning("file type not supported");
		return null;
	}
	
	public String getName() {
		return name;
	}
	public long getSize() {
		return size;
	}
	public String getPath() {
		return path;
	}
	public int getJointID() {
		return jointID;
	}
	public TagList getTagList() {
		return tagList;
	}
	public GalleryTile getGalleryTile() {
		return galleryTile;
	}
	public String getCacheFile() {
		return FileUtil.getDirCache() + name + "-" + size + ThumbnailCreator.getCacheExtension();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public void setGalleryTile(GalleryTile galleryTile) {
		this.galleryTile = (galleryTile != null) ? galleryTile : new GalleryTile(this, null);
	}
	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}
	public void setJointID(int jointID) {
		this.jointID = jointID;
	}
}
