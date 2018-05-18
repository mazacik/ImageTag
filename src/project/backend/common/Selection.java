package project.backend.common;

import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.backend.singleton.PreviewPaneBack;
import project.frontend.Frontend;
import project.frontend.singleton.GalleryPaneFront;
import project.frontend.singleton.RightPaneFront;

import java.util.ArrayList;

public abstract class Selection {
    private static final ArrayList<DatabaseItem> databaseItemsSelected = Database.getDatabaseItemsSelected();

    private static DatabaseItem focusedItem = null;


    public static ArrayList<String> getSelectionTags() {
        if (databaseItemsSelected.isEmpty())
            return new ArrayList<>();

        ArrayList<String> sharedTags = new ArrayList<>();
        ArrayList<String> firstItemTags = databaseItemsSelected.get(0).getTags();
        for (String tag : firstItemTags) {
            for (DatabaseItem databaseItem : databaseItemsSelected) {
                if (databaseItem.getTags().contains(tag)) {
                    if (databaseItem.equals(databaseItemsSelected.get(databaseItemsSelected.size() - 1)))
                        sharedTags.add(tag);
                    continue;
                }
                break;
            }
        }

        return sharedTags;
    }

    public static void add(DatabaseItem databaseItem) {
        if (focusedItem != databaseItem || focusedItem == null)
            setFocusedItem(databaseItem, false);
        databaseItemsSelected.add(databaseItem);
        GalleryPaneFront.getInstance().setGalleryTileHighlight(databaseItem, true);
        changed();
    }

    public static void add(ArrayList<DatabaseItem> databaseItemsToAddToSelection) {
        for (DatabaseItem databaseItem : databaseItemsToAddToSelection)
            if (!databaseItemsSelected.contains(databaseItem)) {
                databaseItemsSelected.add(databaseItem);
                GalleryPaneFront.getInstance().setGalleryTileHighlight(databaseItem, true);
            }
        changed();
    }

    public static void set(DatabaseItem databaseItem) {
        clear();
        add(databaseItem);
    }

    public static void remove(DatabaseItem databaseItem) {
        if (!focusedItem.equals(databaseItem))
            setFocusedItem(databaseItem, false);
        databaseItemsSelected.remove(databaseItem);
        GalleryPaneFront.getInstance().setGalleryTileHighlight(databaseItem, false);
        changed();
    }

    public static void clear() {
        databaseItemsSelected.clear();
        RightPaneFront.getInstance().getListView().getItems().clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItems()) {
            if (databaseItem.getGalleryTile().getEffect() != null)
                databaseItem.getGalleryTile().setEffect(null);
        }
    }

    private static void changed() {
        if (Frontend.isPreviewFullscreen())
            PreviewPaneBack.getInstance().draw();
        RightPaneFront.getInstance().getListView().getItems().setAll(getSelectionTags());
    }

    public static void setFocusedItem(DatabaseItem databaseItem, boolean modifySelection) {
        focusedItem = databaseItem;
        if (modifySelection) {
            if (!databaseItemsSelected.contains(focusedItem))
                add(focusedItem);
            else
                remove(focusedItem);
        }
    }

    public static DatabaseItem getFocusedItem() {
        return focusedItem;
    }
}
