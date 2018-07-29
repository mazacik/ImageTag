package project.control;

import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.component.GalleryPane;
import project.gui.component.RightPane;

import java.util.ArrayList;
import java.util.Random;

public abstract class SelectionControl {
    /* vars */
    private static final ArrayList<DataElement> dataElements = new ArrayList<>();

    /* public */
    public static void addDataElement(DataElement dataElement) {
        if (dataElement != null && !dataElements.contains(dataElement)) {
            dataElements.add(dataElement);
            dataElement.getGalleryTile().generateEffect();
            ReloadControl.requestComponentReload(RightPane.class);
        }
    }
    public static void addDataElement(ArrayList<DataElement> dataElementsToAdd) {
        if (dataElementsToAdd != null) {
            for (DataElement dataElement : dataElementsToAdd) {
                if (!dataElements.contains(dataElement)) {
                    dataElements.add(dataElement);
                    dataElement.getGalleryTile().generateEffect();
                }
            }
            ReloadControl.requestComponentReload(RightPane.class);
        }
    }
    public static void removeDataElement(DataElement dataElement) {
        if (dataElement != null && dataElements.contains(dataElement)) {
            dataElements.remove(dataElement);
            dataElement.getGalleryTile().generateEffect();
            ReloadControl.requestComponentReload(RightPane.class);
        }
    }
    public static void setDataElement(DataElement dataElement) {
        dataElements.clear();
        addDataElement(dataElement);
        FocusControl.setFocus(dataElement);
    }
    public static void clearDataElements() {
        SelectionControl.getDataElements().clear();
        DataElement currentFocus = FocusControl.getCurrentFocus();
        for (DataElement dataElement : DataElementControl.getDataElementsCopy()) {
            if (!dataElement.equals(currentFocus)) {
                dataElement.getGalleryTile().setEffect(null);
            } else {
                dataElement.getGalleryTile().generateEffect();
            }
        }
        ReloadControl.requestComponentReload(RightPane.class);
    }
    public static void setRandomValidDataElement() {
        ArrayList<DataElement> dataElementsFiltered = FilterControl.getValidDataElements();
        int databaseItemsFilteredSize = dataElementsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        SelectionControl.setDataElement(dataElementsFiltered.get(randomIndex));
        GalleryPane.adjustViewportToCurrentFocus();
    }
    public static void swapSelectionStateOf(DataElement dataElement) {
        if (dataElement != null) {
            if (!dataElements.contains(dataElement)) {
                addDataElement(dataElement);
            } else {
                removeDataElement(dataElement);
            }
        }
    }
    public static ArrayList<TagElement> getIntersectingTags() {
        if (isSelectionEmpty()) return new ArrayList<>();

        ArrayList<TagElement> sharedTags = new ArrayList<>();
        ArrayList<TagElement> firstItemTags = dataElements.get(0).getTagElements();
        DataElement lastItemInSelection = dataElements.get(dataElements.size() - 1);

        for (TagElement tagElement : firstItemTags) {
            for (DataElement dataElement : dataElements) {
                if (dataElement.getTagElements().contains(tagElement)) {
                    if (dataElement.equals(lastItemInSelection)) {
                        sharedTags.add(tagElement);
                    }
                } else break;
            }
        }
        return sharedTags;
    }

    /* boolean */
    public static boolean isSelectionEmpty() {
        return dataElements.isEmpty();
    }
    public static boolean isSelectionSingleElement() {
        return dataElements.size() == 1;
    }

    /* get */
    public static ArrayList<DataElement> getDataElements() {
        return dataElements;
    }
}
