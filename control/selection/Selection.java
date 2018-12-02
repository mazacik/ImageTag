package control.selection;

import control.reload.Reload;
import database.object.DataCollection;
import database.object.DataObject;
import database.object.TagCollection;
import database.object.TagObject;
import utils.MainUtil;

public class Selection extends DataCollection implements MainUtil {
    public boolean add(DataObject dataObject) {
        if (dataObject == null) return false;
        if (super.add(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public boolean addAll(DataCollection dataCollection) {
        if (dataCollection == null) return false;
        if (super.addAll(dataCollection)) {
            dataCollection.forEach(DataObject::generateTileEffect);
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public boolean remove(DataObject dataObject) {
        if (dataObject == null) return false;
        if (super.remove(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public void set(DataObject dataObject) {
        this.clear();
        this.add(dataObject);
    }
    public void clear() {
        DataCollection helper = new DataCollection(this);
        super.clear();
        helper.forEach(DataObject::generateTileEffect);
        reload.notifyChangeIn(Reload.Control.SELECTION);
    }
    public void swapState(DataObject dataObject) {
        if (super.contains(dataObject)) {
            this.remove(dataObject);
        } else {
            this.add(dataObject);
        }
    }

    public void addTagObject(TagObject tagObject) {
        if (!tagObject.isEmpty()) {
            if (!mainTags.contains(tagObject)) {
                mainTags.add(tagObject);
            }

            TagCollection tagCollection;
            for (DataObject dataIterator : this) {
                tagCollection = dataIterator.getTagCollection();
                if (!tagCollection.contains(tagObject)) {
                    tagCollection.add(tagObject);
                }
            }
        }
    }
    public void removeTagObject(TagObject tagObject) {
        for (DataObject dataObject : this) {
            dataObject.getTagCollection().remove(tagObject);
        }

        boolean tagExists = false;
        for (DataObject dataObject : mainData) {
            if (dataObject.getTagCollection().contains(tagObject)) {
                tagExists = true;
                break;
            }
        }
        if (!tagExists) {
            filter.unlistTagObject(tagObject);
            mainTags.remove(tagObject);
        }
    }

    public TagCollection getIntersectingTags() {
        if (this.size() < 1) return new TagCollection();

        TagCollection intersectingTags = new TagCollection();
        DataObject lastObject = this.get(this.size() - 1);
        for (TagObject tagObject : this.get(0).getTagCollection()) {
            for (DataObject dataObject : this) {
                if (dataObject.getTagCollection().contains(tagObject)) {
                    if (dataObject.equals(lastObject)) {
                        intersectingTags.add(tagObject);
                    }
                } else break;
            }
        }
        return intersectingTags;
    }
    public TagCollection getSharedTags() {
        if (this.size() < 1) return new TagCollection();

        TagCollection sharedTags = new TagCollection();
        for (DataObject dataObject : this) {
            for (TagObject tagObject : dataObject.getTagCollection()) {
                if (!sharedTags.contains(tagObject)) {
                    sharedTags.add(tagObject);
                }
            }
        }
        return sharedTags;
    }
}
