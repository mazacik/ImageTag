package database.list;

import com.google.gson.reflect.TypeToken;
import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import system.InstanceRepo;
import system.SerializationUtil;
import user_interface.factory.stage.InfoObjectEditStage;

import java.lang.reflect.Type;

public class InfoObjectListMain extends InfoObjectList implements InstanceRepo {
    private static final transient String tagsFile = "data\\tags.json";

    public void initialize() {
        for (DataObject dataIterator : mainDataList) {
            InfoObjectList infoObjectList = dataIterator.getInfoObjectList();
            for (TagObject tagIterator : infoObjectList) {
                if (this.contains(tagIterator)) {
                    infoObjectList.set(infoObjectList.indexOf(tagIterator), getTagObject(tagIterator));
                } else {
                    this.add(tagIterator);
                }
            }
        }
        InfoObjectList allTags = readDummyFromDisk();
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
        Type typeToken = new TypeToken<InfoObjectList>() {}.getType();
        String path = settings.getCurrentDirectory() + tagsFile;
        SerializationUtil.writeJSON(this, typeToken, path);
    }
    private InfoObjectList readDummyFromDisk() {
        Type typeToken = new TypeToken<InfoObjectList>() {}.getType();
        String path = settings.getCurrentDirectory() + tagsFile;
        return (InfoObjectList) SerializationUtil.readJSON(typeToken, path);
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
