package application.database.list;

import application.controller.Reload;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.stage.StageUtil;
import application.main.Instances;
import application.misc.FileUtil;
import application.misc.JsonUtil;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;

import java.lang.reflect.Type;

public class TagListMain extends TagList {
	public void initialize() {
		for (DataObject dataIterator : Instances.getObjectListMain()) {
			TagList tagList = dataIterator.getTagList();
			for (TagObject tagIterator : tagList) {
				if (this.contains(tagIterator)) {
					tagList.set(tagList.indexOf(tagIterator), getTagObject(tagIterator));
				} else {
					this.add(tagIterator);
				}
			}
		}
		TagList allTags = readDummyFromDisk();
		if (allTags != null) {
			for (TagObject tagObject : allTags) {
				if (!this.contains(tagObject)) {
					this.add(tagObject);
				}
			}
		}
		
		super.sort();
	}
	
	public void writeDummyToDisk() {
		Type typeToken = new TypeToken<TagList>() {}.getType();
		JsonUtil.write(this, typeToken, FileUtil.getFileTags());
	}
	private TagList readDummyFromDisk() {
		Type typeToken = new TypeToken<TagList>() {}.getType();
		return (TagList) JsonUtil.read(typeToken, FileUtil.getFileTags());
	}
	
	public boolean add(TagObject tagObject) {
		if (tagObject == null) return false;
		if (super.add(tagObject)) {
			Instances.getReload().notify(Reload.Control.TAG);
			return true;
		}
		return false;
	}
	public boolean remove(TagObject tagObject) {
		if (tagObject == null) return false;
		if (super.remove(tagObject)) {
			//InstanceManager.getFilter().refresh();
			Instances.getReload().notify(Reload.Control.TAG);
			return true;
		}
		return false;
	}
	public boolean edit(TagObject tagObjectOld) {
		if (tagObjectOld == null) return false;
		
		Pair<TagObject, Boolean> result = StageUtil.showStageEditorTag(tagObjectOld.getGroup(), tagObjectOld.getName());
		TagObject tagObjectNew = result.getKey();
		
		if (tagObjectNew != null) {
			tagObjectOld.setFull(tagObjectNew.getGroup(), tagObjectNew.getName());
			super.sort();
			if (result.getValue()) Instances.getSelect().addTagObject(tagObjectOld);
			Instances.getReload().notify(Reload.Control.TAG);
			return true;
		}
		return false;
	}
}
