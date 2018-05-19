package project.common;

import project.component.gallery.GalleryPaneFront;
import project.component.preview.PreviewPaneBack;
import project.component.right.RightPaneFront;
import project.database.Database;
import project.database.DatabaseItem;

import java.util.ArrayList;

public class Selection {
    /* lazy singleton */
    private static Selection instance;
    public static Selection getInstance() {
        if (instance == null) instance = new Selection();
        return instance;
    }

    /* imports */
    private final ArrayList<DatabaseItem> databaseItemsSelected = Database.getDatabaseItemsSelected();

    /* public methods */
    public ArrayList<String> getSelectionTags() {
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

    public void add(DatabaseItem databaseItem) {
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        galleryPaneFront.setCurrentFocusedItem(databaseItem);
        databaseItemsSelected.add(databaseItem);
        databaseItem.getGalleryTile().generateEffect(databaseItem);
        changed();
    }

    public void add(ArrayList<DatabaseItem> databaseItemsToAddToSelection) {
        for (DatabaseItem databaseItem : databaseItemsToAddToSelection)
            if (!databaseItemsSelected.contains(databaseItem)) {
                databaseItemsSelected.add(databaseItem);
                databaseItem.getGalleryTile().generateEffect(databaseItem);
            }
        changed();
    }

    public void set(DatabaseItem databaseItem) {
        clear();
        add(databaseItem);
    }

    public void remove(DatabaseItem databaseItem) {
        if (!GalleryPaneFront.getInstance().getCurrentFocusedItem().equals(databaseItem))
            GalleryPaneFront.getInstance().setCurrentFocusedItem(databaseItem);
        databaseItemsSelected.remove(databaseItem);
        databaseItem.getGalleryTile().generateEffect(databaseItem);
        changed();
    }

    public void swap(DatabaseItem databaseItem) {
        if (!databaseItemsSelected.contains(databaseItem))
            add(databaseItem);
        else
            remove(databaseItem);
    }

    public void clear() {
        databaseItemsSelected.clear();
        RightPaneFront.getInstance().getListView().getItems().clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItems()) {
            databaseItem.getGalleryTile().generateEffect(databaseItem);
        }
    }

    /* private methods */
    private void changed() {
        PreviewPaneBack.getInstance().reloadContent();
        RightPaneFront.getInstance().getListView().getItems().setAll(getSelectionTags());
    }
}
