package project.control;

import project.database.control.DataControl;
import project.database.element.DataCollection;
import project.database.element.DataObject;
import project.database.element.TagCollection;
import project.database.element.TagObject;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.rightpane.RightPane;

import java.util.ArrayList;
import java.util.Random;

public abstract class SelectionControl {
    /* vars */
    private static final DataCollection dataObjects = new DataCollection();

    /* public */
    public static void addDataElement(DataObject dataObject) {
        if (dataObject != null && !dataObjects.contains(dataObject)) {
            dataObjects.add(dataObject);
            dataObject.getGalleryTile().generateEffect();
            ReloadControl.request(RightPane.class);
        }
    }
    public static void addDataElement(DataCollection dataElementsToAdd) {
        if (dataElementsToAdd != null) {
            for (DataObject dataObject : dataElementsToAdd) {
                if (!dataObjects.contains(dataObject)) {
                    dataObjects.add(dataObject);
                    dataObject.getGalleryTile().generateEffect();
                }
            }
            ReloadControl.request(RightPane.class);
        }
    }
    public static void removeDataElement(DataObject dataObject) {
        if (dataObject != null && dataObjects.contains(dataObject)) {
            dataObjects.remove(dataObject);
            dataObject.getGalleryTile().generateEffect();
            ReloadControl.request(RightPane.class);
        }
    }
    public static void setDataElement(DataObject dataObject) {
        dataObjects.clear();
        addDataElement(dataObject);
        FocusControl.setFocus(dataObject);
    }
    public static void clearDataElements() {
        SelectionControl.getCollection().clear();
        DataObject currentFocus = FocusControl.getCurrentFocus();
        for (Object dataObject : DataControl.getDataCollectionCopy()) {
            if (!dataObject.equals(currentFocus)) {
                ((DataObject) dataObject).getGalleryTile().setEffect(null);
            } else {
                ((DataObject) dataObject).getGalleryTile().generateEffect();
            }
        }
        ReloadControl.request(RightPane.class);
    }
    public static void setRandomValidDataElement() {
        ArrayList<DataObject> dataElementsFiltered = FilterControl.getCollection();
        int databaseItemsFilteredSize = dataElementsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        SelectionControl.setDataElement(dataElementsFiltered.get(randomIndex));
        GalleryPane.adjustViewportToCurrentFocus();
    }
    public static void swapSelectionStateOf(DataObject dataObject) {
        if (dataObject != null) {
            if (!dataObjects.contains(dataObject)) {
                addDataElement(dataObject);
            } else {
                removeDataElement(dataObject);
            }
        }
    }
    public static TagCollection getIntersectingTags() {
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

    /* boolean */
    public static boolean isSelectionEmpty() {
        return dataObjects.isEmpty();
    }
    public static boolean isSelectionSingleElement() {
        return dataObjects.size() == 1;
    }

    /* get */
    public static DataCollection getCollection() {
        return dataObjects;
    }
}
