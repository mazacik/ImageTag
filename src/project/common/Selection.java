package project.common;

import project.component.gallery.GalleryPaneFront;
import project.component.preview.PreviewPaneBack;
import project.component.right.RightPaneFront;
import project.database.DatabaseItem;

import java.util.ArrayList;

public abstract class Selection {
    /* imports */
    private static final ArrayList<DatabaseItem> databaseItemsSelected = Database.getDatabaseItemsSelected();

    /* public methods */
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
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        databaseItemsSelected.add(databaseItem);
        galleryPaneFront.focusTile(databaseItem);
    }

    public static void add(ArrayList<DatabaseItem> databaseItemsToAddToSelection) {
        for (DatabaseItem databaseItem : databaseItemsToAddToSelection)
            if (!databaseItemsSelected.contains(databaseItem)) {
                databaseItemsSelected.add(databaseItem);
                databaseItem.getGalleryTile().generateEffect(databaseItem);
            }
        PreviewPaneBack.getInstance().reloadContent();
        RightPaneFront.getInstance().getListView().getItems().setAll(getSelectionTags());
    }

    public static void set(DatabaseItem databaseItem) {
        clear();
        add(databaseItem);
    }

    public static void remove(DatabaseItem databaseItem) {
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        databaseItemsSelected.remove(databaseItem);
        galleryPaneFront.focusTile(databaseItem);
    }

    public static void swap(DatabaseItem databaseItem) {
        if (!databaseItemsSelected.contains(databaseItem))
            add(databaseItem);
        else
            remove(databaseItem);
    }

    public static void clear() {
        databaseItemsSelected.clear();
        RightPaneFront.getInstance().getListView().getItems().clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItems()) {
            databaseItem.getGalleryTile().generateEffect(databaseItem);
        }
    }
}
