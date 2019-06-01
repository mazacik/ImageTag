package database.list;

import com.google.gson.reflect.TypeToken;
import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import database.loader.DirectoryUtil;
import lifecycle.InstanceManager;
import system.JsonUtil;
import user_interface.factory.stage.InfoObjectEditStage;

import java.io.File;
import java.lang.reflect.Type;

public class TagListMain extends TagList {
    private static final transient String tagsFile = DirectoryUtil.getDirNameData() + File.separator + "tags.json";

    public void initialize() {
        for (DataObject dataIterator : InstanceManager.getMainDataList()) {
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
            allTags.forEach(tagObject -> {
                if (!this.contains(tagObject)) {
                    this.add(tagObject);
                }
            });
        }
        super.sort();
    }

    public void writeDummyToDisk() {
        Type typeToken = new TypeToken<TagList>() {
        }.getType();
        String path = DirectoryUtil.getPathSource() + tagsFile;
        JsonUtil.write(this, typeToken, path);
    }
    private TagList readDummyFromDisk() {
        Type typeToken = new TypeToken<TagList>() {
        }.getType();
        String path = DirectoryUtil.getPathSource() + tagsFile;
        return (TagList) JsonUtil.read(typeToken, path);
    }

    public boolean add(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.add(tagObject)) {
            InstanceManager.getReload().notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean remove(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.remove(tagObject)) {
            //InstanceManager.getFilter().refresh();
            InstanceManager.getReload().notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean edit(TagObject tagObject) {
        if (tagObject == null) return false;
        TagObject newTagObject = new InfoObjectEditStage(tagObject).getResult();
        if (newTagObject != null) {
            tagObject.setFull(newTagObject.getGroup(), newTagObject.getName());
            super.sort();
            InstanceManager.getReload().notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
}
