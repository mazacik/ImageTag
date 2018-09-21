package project.control;

import javafx.collections.ObservableList;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.rightpane.RightPane;

import java.util.ArrayList;
import java.util.Random;

public class SelectionControl {
    private final DataCollection dataObjects;

    SelectionControl() {
        dataObjects = new DataCollection();
    }

    public void addDataObject(DataObject dataObject) {
        if (!dataObjects.contains(dataObject)) {
            dataObjects.add(dataObject);
            dataObject.getGalleryTile().generateEffect();
            MainControl.getReloadControl().reload(GUINode.RIGHTPANE);
        }
    }
    public void addDataObject(DataCollection dataObjectsToAdd) {
        for (DataObject dataObject : dataObjectsToAdd) {
            if (!dataObjects.contains(dataObject)) {
                dataObjects.add(dataObject);
                dataObject.getGalleryTile().generateEffect();
            }
        }
        MainControl.getReloadControl().reload(GUINode.RIGHTPANE);
    }
    public void removeDataObject(DataObject dataObject) {
        if (dataObjects.contains(dataObject)) {
            dataObjects.remove(dataObject);
            dataObject.getGalleryTile().generateEffect();
            MainControl.getReloadControl().reload(GUINode.RIGHTPANE);
        }
    }
    public void setDataObject(DataObject dataObject) {
        dataObjects.clear();
        addDataObject(dataObject);
        MainControl.getFocusControl().setFocus(dataObject);
    }
    public void clearDataObjects() {
        getCollection().clear();
        DataObject currentFocus = MainControl.getFocusControl().getCurrentFocus();
        for (Object dataObject : dataControl.getCollection()) {
            if (!dataObject.equals(currentFocus)) {
                ((DataObject) dataObject).getGalleryTile().setEffect(null);
            } else {
                ((DataObject) dataObject).getGalleryTile().generateEffect();
            }
        }
        MainControl.getReloadControl().reload(GUINode.RIGHTPANE);
    }
    public void setRandomValidDataObject() {
        ArrayList<DataObject> dataObjectsFiltered = MainControl.getFilterControl().getCollection();
        int databaseItemsFilteredSize = dataObjectsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        setDataObject(dataObjectsFiltered.get(randomIndex));
        GalleryPane.adjustViewportToCurrentFocus();
    }
    public void swapSelectionStateOf(DataObject dataObject) {
        if (!dataObjects.contains(dataObject)) {
            addDataObject(dataObject);
        } else {
            removeDataObject(dataObject);
        }
    }
    public TagCollection getIntersectingTags() {
        if (isSelectionEmpty()) return new TagCollection();

        TagCollection sharedTags = new TagCollection();
        TagCollection firstItemTags = dataObjects.get(0).getTagCollection();
        DataObject lastItemInSelection = dataObjects.get(dataObjects.size() - 1);

        for (TagObject tagObject : firstItemTags) {
            for (DataObject dataObject : dataObjects) {
                if (dataObject.getTagCollection().contains(tagObject)) {
                    if (dataObject.equals(lastItemInSelection)) {
                        sharedTags.add(tagObject);
                    }
                } else break;
            }
        }
        return sharedTags;
    }

    public void addTagObject(TagObject tagObject) {
        if (!tagObject.isEmpty()) {
            if (!TagControl.getCollection().contains(tagObject)) {
                TagControl.add(tagObject);
            }

            TagCollection tagCollection;
            for (DataObject dataObject : getCollection()) {
                tagCollection = dataObject.getTagCollection();
                if (!tagCollection.contains(tagObject)) {
                    tagCollection.add(tagObject);
                }
            }
        }
    }
    public void removeTagObjectSelection() {
        TagCollection tagObjectsToRemove = new TagCollection();
        ObservableList<String> tagObjectSelection = RightPane.getListView().getSelectionModel().getSelectedItems();
        for (String tagObject : tagObjectSelection) {
            tagObjectsToRemove.add(TagControl.getTagObject(tagObject));
        }

        for (TagObject tagObject : tagObjectsToRemove) {
            for (DataObject dataObject : getCollection()) {
                dataObject.getTagCollection().remove(tagObject);
            }

            boolean tagExists = false;
            DataCollection dataObjects = DataControl.getCollection();
            for (DataObject dataObject : dataObjects) {
                if (dataObject.getTagCollection().contains(tagObject)) {
                    tagExists = true;
                    break;
                }
            }
            if (!tagExists) {
                MainControl.getFilterControl().unlistTagObject(tagObject);
                TagControl.remove(tagObject);
                MainControl.getReloadControl().reload(GUINode.LEFTPANE);
            }
        }

        MainControl.getReloadControl().reload(GUINode.RIGHTPANE);
    }

    public boolean isSelectionEmpty() {
        return dataObjects.isEmpty();
    }
    public boolean isSelectionSingleObject() {
        return dataObjects.size() == 1;
    }

    public DataCollection getCollection() {
        return dataObjects;
    }
}
