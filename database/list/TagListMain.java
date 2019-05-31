package database.list;

import com.google.gson.reflect.TypeToken;
import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import loader.DirectoryUtil;
import system.Instances;
import system.JsonUtil;
import user_interface.factory.stage.InfoObjectEditStage;

import java.io.File;
import java.lang.reflect.Type;

public class TagListMain extends TagList implements Instances {
    private static final transient String tagsFile = DirectoryUtil.getDirNameData() + File.separator + "tags.json";

    public void initialize() {
        for (DataObject dataIterator : mainDataList) {
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
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean remove(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.remove(tagObject)) {
            //filter.apply();
            reload.notifyChangeIn(Reload.Control.INFO);
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
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
}
