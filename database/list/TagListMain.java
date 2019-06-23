package database.list;

import com.google.gson.reflect.TypeToken;
import control.Reload;
import database.object.DataObject;
import database.object.TagObject;
import lifecycle.InstanceManager;
import user_interface.stage.StageUtil;
import user_interface.stage.Stages;
import utils.FileUtil;
import utils.JsonUtil;

import java.lang.reflect.Type;

public class TagListMain extends TagList {
    public void initialize() {
        for (DataObject dataIterator : InstanceManager.getObjectListMain()) {
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
            InstanceManager.getReload().flag(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean remove(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.remove(tagObject)) {
            //InstanceManager.getFilter().refresh();
            InstanceManager.getReload().flag(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean edit(TagObject tagObject) {
        if (tagObject == null) return false;
		TagObject newTagObject = (TagObject) StageUtil.show(Stages.STAGE_TAG_EDITOR, tagObject.getGroup(), tagObject.getName());
        if (newTagObject != null) {
            tagObject.setFull(newTagObject.getGroup(), newTagObject.getName());
            super.sort();
            InstanceManager.getReload().flag(Reload.Control.INFO);
            return true;
        }
        return false;
    }
}
