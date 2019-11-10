package application.baseobject.tag;

import application.baseobject.entity.Entity;
import application.control.reload.ChangeIn;
import application.gui.stage.StageManager;
import application.main.InstanceCollector;
import application.tools.FileUtil;
import application.tools.JsonUtil;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;

import java.lang.reflect.Type;

public class TagListMain extends TagList implements InstanceCollector {
	private static final Type typeToken = new TypeToken<TagListMain>() {}.getType();
	
	public void initialize() {
		TagList allTags = readDummyFromDisk();
		if (allTags != null) {
			this.addAll(allTags);
		}
		
		for (Entity entity : entityListMain) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (this.containsEqualTo(tag)) {
					tagList.set(tagList.indexOf(tag), this.getTag(tag));
				} else {
					this.add(tag);
				}
			}
		}
		
		this.sort();
	}
	
	public void writeDummyToDisk() {
		JsonUtil.write(this, typeToken, FileUtil.getProjectFileTags());
	}
	private TagList readDummyFromDisk() {
		return (TagList) JsonUtil.read(typeToken, FileUtil.getProjectFileTags());
	}
	
	public boolean add(Tag tag) {
		if (super.add(tag)) {
			reload.notify(ChangeIn.TAG_LIST_MAIN);
			return true;
		}
		return false;
	}
	public boolean remove(Tag tag) {
		if (super.remove(tag)) {
			reload.notify(ChangeIn.TAG_LIST_MAIN);
			return true;
		}
		return false;
	}
	public boolean edit(Tag tagOld) {
		if (tagOld != null) {
			Pair<Tag, Boolean> result = StageManager.getTagEditStage().show(tagOld.getGroup(), tagOld.getName());
			Tag tagNew = result.getKey();
			
			if (tagNew != null) {
				tagOld.set(tagNew.getGroup(), tagNew.getName());
				super.sort();
				if (result.getValue()) {
					select.addTag(tagOld);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
