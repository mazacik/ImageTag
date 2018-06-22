package project.backend;

import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.ChangeNotificationHelper;
import project.gui.GUIController;
import project.gui.GUIStage;
import project.gui.component.GalleryPane;
import project.gui.component.part.GalleryTile;

import java.util.ArrayList;
import java.util.Random;

public abstract class Selection {
    /* change listeners */
    private static final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    public static ArrayList<TagItem> getSharedTags() {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
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
        GUIStage.getGalleryPane().adjustViewportToFocus();
    }

    public static void add(DatabaseItem databaseItem) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        GalleryPane galleryPane = GUIStage.getGalleryPane();
        databaseItemsSelected.add(databaseItem);
        galleryPane.focusTile(databaseItem);
    }

    public static void add(ArrayList<DatabaseItem> databaseItemsToAddToSelection) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        for (DatabaseItem databaseItem : databaseItemsToAddToSelection) {
            if (!databaseItemsSelected.contains(databaseItem)) {
                databaseItemsSelected.add(databaseItem);
                GalleryTile.generateEffect(databaseItem);
            }
        }

        GUIController.requestReload(GUIStage.getRightPane());
    }

    public static void set(DatabaseItem databaseItem) {
        clear();
        add(databaseItem);
    }

    public static void remove(DatabaseItem databaseItem) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        GalleryPane galleryPane = GUIStage.getGalleryPane();
        databaseItemsSelected.remove(databaseItem);
        galleryPane.focusTile(databaseItem);
    }

    public static void swap(DatabaseItem databaseItem) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        if (!databaseItemsSelected.contains(databaseItem))
            add(databaseItem);
        else
            remove(databaseItem);
    }

    public static void clear() {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        databaseItemsSelected.clear();
        GUIStage.getRightPane().getListView().getItems().clear();
        for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItems()) {
            GalleryTile.generateEffect(databaseItem);
        }
    }

    /* getters */
    public static ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}
