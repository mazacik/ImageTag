package project.control.selection;

import javafx.collections.ObservableList;
import project.MainUtil;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.database.object.TagObject;
import project.gui.component.GUINode;

public class Selection extends DataCollection implements MainUtil {
    public boolean add(DataObject dataObject) {
        if (super.add(dataObject)) {
            dataObject.getGalleryTile().generateEffect();
            reload.queue(GUINode.RIGHTPANE);
            return true;
        }
        return false;
    }
    public boolean addAll(DataCollection dataCollection) {
        if (super.addAll(dataCollection)) {
            dataCollection.forEach(DataObject::generateTileEffect);
            reload.queue(GUINode.RIGHTPANE);
            return true;
        }
        return false;
    }
    public boolean remove(DataObject dataObject) {
        if (super.remove(dataObject)) {
            dataObject.generateTileEffect();
            reload.queue(GUINode.RIGHTPANE);
            return true;
        }
        return false;
    }
    public void set(DataObject dataObject) {
        this.clear();
        this.add(dataObject);
        focus.set(dataObject);
    }
    public void clear() {
        DataCollection helper = new DataCollection(this);
        super.clear();
        helper.forEach(DataObject::generateTileEffect);
        reload.queue(GUINode.RIGHTPANE);
    }
    public void swapState(DataObject dataObject) {
        if (!super.contains(dataObject)) {
            this.add(dataObject);
        } else {
            this.remove(dataObject);
        }
    }
    public TagCollection getIntersectingTags() {
        if (this.size() < 1) return new TagCollection();

        TagCollection sharedTags = new TagCollection();
        DataObject lastObject = this.get(this.size() - 1);
        for (TagObject tagObject : this.get(0).getTagCollection()) {
            for (DataObject dataObject : this) {
                if (dataObject.getTagCollection().contains(tagObject)) {
                    if (dataObject.equals(lastObject)) {
                        sharedTags.add(tagObject);
                    }
                } else break;
            }
        }
        return sharedTags;
    }

    public void addTagObject(TagObject tagObject) {
        if (!tagObject.isEmpty()) {
            if (!mainTags.contains(tagObject)) {
                mainTags.add(tagObject);
            }

            TagCollection tagCollection;
            for (DataObject dataObject : this) {
                tagCollection = dataObject.getTagCollection();
                if (!tagCollection.contains(tagObject)) {
                    tagCollection.add(tagObject);
                }
            }
        }
    }
    public void removeTagObject() {
        TagCollection tagObjectsToRemove = new TagCollection();
        ObservableList<String> tagObjectSelection = rightPane.getListView().getSelectionModel().getSelectedItems();
        for (String tagObject : tagObjectSelection) {
            tagObjectsToRemove.add(mainTags.getTagObject(tagObject));
        }

        for (TagObject tagObject : tagObjectsToRemove) {
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
                reload.queue(GUINode.LEFTPANE);
            }
        }

        reload.queue(GUINode.RIGHTPANE);
    }
}
