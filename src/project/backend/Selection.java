package project.backend;

import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.component.gallery.GalleryPaneBack;
import project.gui.component.gallery.GalleryPaneFront;
import project.gui.component.gallery.part.GalleryTile;
import project.gui.component.right.RightPaneBack;
import project.gui.component.right.RightPaneFront;

import java.util.ArrayList;
import java.util.Random;

public abstract class Selection {
    /* imports */
    private static final ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();

    /* public methods */
    public static ArrayList<TagItem> getSharedTags() {
        if (databaseItemsSelected.isEmpty()) return new ArrayList<>();

        ArrayList<TagItem> sharedTags = new ArrayList<>();
        ArrayList<TagItem> firstItemTags = databaseItemsSelected.get(0).getTags();
        DatabaseItem lastItemInSelection = databaseItemsSelected.get(databaseItemsSelected.size() - 1);

        for (TagItem tagItem : firstItemTags)
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (databaseItem.getTags().contains(tagItem)) {
                    if (databaseItem.equals(lastItemInSelection))
                        sharedTags.add(tagItem);
                } else break;

        return sharedTags;
    }

    public static void selectRandomItem() {
        ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();
        int databaseItemsFilteredSize = databaseItemsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        Selection.set(databaseItemsFiltered.get(randomIndex));
        GalleryPaneBack.getInstance().adjustViewportPositionToFocus();
    }

    public static void add(DatabaseItem databaseItem) {
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        databaseItemsSelected.add(databaseItem);
        galleryPaneFront.focusTile(databaseItem);
    }

    public static void add(ArrayList<DatabaseItem> databaseItemsToAddToSelection) {
        for (DatabaseItem databaseItem : databaseItemsToAddToSelection) {
            if (!databaseItemsSelected.contains(databaseItem)) {
                databaseItemsSelected.add(databaseItem);
                GalleryTile.generateEffect(databaseItem);
            }
        }

        RightPaneBack.getInstance().reloadContent();
        //PreviewPaneBack.getInstance().reloadContent();
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
        for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItems()) {
            GalleryTile.generateEffect(databaseItem);
        }
    }
}
