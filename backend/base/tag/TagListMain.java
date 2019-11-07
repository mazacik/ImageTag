package application.backend.base.tag;

import application.backend.base.entity.Entity;
import application.backend.control.reload.ChangeIn;
import application.backend.util.FileUtil;
import application.backend.util.JsonUtil;
import application.frontend.stage.StageManager;
import application.main.InstanceCollector;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;

import java.lang.reflect.Type;

public class TagListMain extends TagList implements InstanceCollector {
	private static final Type typeToken = new TypeToken<TagListMain>() {}.getType();
	
	public void initialize() {
		TagList allTags = readDummyFromDisk();
		if (allTags != null) {
			this.addAll(allTags, true);
		}
		
		for (Entity entity : entityListMain) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (this.containsEqualTo(tag)) {
					tagList.set(tagList.indexOf(tag), this.getTag(tag));
				} else {
					this.add(tag, true);
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
			Pair<Tag, Boolean> result = StageManager.getTagEditStage()._show(tagOld.getGroup(), tagOld.getName());
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
