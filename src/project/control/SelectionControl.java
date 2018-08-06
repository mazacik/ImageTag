package project.control;

import project.database.control.DataObjectControl;
import project.database.element.DataObject;
import project.database.element.TagElement;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.rightpane.RightPane;

import java.util.ArrayList;
import java.util.Random;

public abstract class SelectionControl {
    /* vars */
    private static final ArrayList<DataObject> dataObjects = new ArrayList<>();

    /* public */
    public static void addDataElement(DataObject dataObject) {
        if (dataObject != null && !dataObjects.contains(dataObject)) {
            dataObjects.add(dataObject);
            dataObject.getGalleryTile().generateEffect();
            ReloadControl.requestComponentReload(RightPane.class);
        }
    }
    public static void addDataElement(ArrayList<DataObject> dataElementsToAdd) {
        if (dataElementsToAdd != null) {
            for (DataObject dataObject : dataElementsToAdd) {
                if (!dataObjects.contains(dataObject)) {
                    dataObjects.add(dataObject);
                    dataObject.getGalleryTile().generateEffect();
                }
            }
            ReloadControl.requestComponentReload(RightPane.class);
        }
    }
    public static void removeDataElement(DataObject dataObject) {
        if (dataObject != null && dataObjects.contains(dataObject)) {
            dataObjects.remove(dataObject);
            dataObject.getGalleryTile().generateEffect();
            ReloadControl.requestComponentReload(RightPane.class);
        }
    }
    public static void setDataElement(DataObject dataObject) {
        dataObjects.clear();
        addDataElement(dataObject);
        FocusControl.setFocus(dataObject);
    }
    public static void clearDataElements() {
        SelectionControl.getDataObjects().clear();
        DataObject currentFocus = FocusControl.getCurrentFocus();
        for (DataObject dataObject : DataObjectControl.getDataElementsCopy()) {
            if (!dataObject.equals(currentFocus)) {
                dataObject.getGalleryTile().setEffect(null);
            } else {
                dataObject.getGalleryTile().generateEffect();
            }
        }
        ReloadControl.requestComponentReload(RightPane.class);
    }
    public static void setRandomValidDataElement() {
        ArrayList<DataObject> dataElementsFiltered = FilterControl.getValidObjects();
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
    public static ArrayList<TagElement> getIntersectingTags() {
        if (isSelectionEmpty()) return new ArrayList<>();

        ArrayList<TagElement> sharedTags = new ArrayList<>();
        ArrayList<TagElement> firstItemTags = dataObjects.get(0).getTagElements();
        DataObject lastItemInSelection = dataObjects.get(dataObjects.size() - 1);

        for (TagElement tagElement : firstItemTags) {
            for (DataObject dataObject : dataObjects) {
                if (dataObject.getTagElements().contains(tagElement)) {
                    if (dataObject.equals(lastItemInSelection)) {
                        sharedTags.add(tagElement);
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
    public static ArrayList<DataObject> getDataObjects() {
        return dataObjects;
    }
}
