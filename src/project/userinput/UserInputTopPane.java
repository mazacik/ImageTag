package project.userinput;

import javafx.stage.WindowEvent;
import project.Main;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.loader.Serialization;
import project.enums.FilterCollection;
import project.gui.component.GalleryPane.GalleryPane;
import project.gui.component.LeftPane.LeftPane;
import project.gui.component.RightPane.RightPane;
import project.gui.component.TopPane.TopPane;
import project.gui.custom.generic.NumberInputWindow;

public abstract class UserInputTopPane {
    public static void initialize() {
        setOnAction_menuSave();
        setOnAction_menuExit();

        setOnAction_menuSelectAll();
        setOnAction_menuClearSelection();

        setOnAction_menuUntaggedOnly();
        setOnAction_menuMaxXTags();
        setOnAction_menuRefresh();
        setOnAction_menuReset();
    }

    private static void setOnAction_menuSave() {
        TopPane.getMenuSave().setOnAction(event -> Serialization.writeToDisk());
    }
    private static void setOnAction_menuExit() {
        TopPane.getMenuExit().setOnAction(event -> TopPane.getInstance().fireEvent(new WindowEvent(Main.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private static void setOnAction_menuSelectAll() {
        TopPane.getMenuSelectAll().setOnAction(event -> SelectionControl.addDataElement(FilterControl.getValidDataElements()));
        ReloadControl.requestComponentReload(true, GalleryPane.class, RightPane.class);
    }
    private static void setOnAction_menuClearSelection() {
        TopPane.getMenuClearSelection().setOnAction(event -> SelectionControl.clearDataElements());
        ReloadControl.requestComponentReload(true, GalleryPane.class, RightPane.class);
    }

    private static void setOnAction_menuUntaggedOnly() {
        TopPane.getMenuUntaggedOnly().setOnAction(event -> {
            FilterControl.setFilter(FilterCollection.SHOW_UNTAGGED);
            ReloadControl.requestComponentReload(true, LeftPane.class, GalleryPane.class);
        });
    }
    private static void setOnAction_menuMaxXTags() {
        TopPane.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("FilterCollection Settings", "Maximum number of tags:").getResultValue();
            if (maxTags == 0) return;
            FilterCollection.setMaxTagsValue(maxTags);

            FilterControl.setFilter(FilterCollection.SHOW_MAX_X_TAGS);
            ReloadControl.requestComponentReload(true, GalleryPane.class, RightPane.class);
        });
    }
    private static void setOnAction_menuRefresh() {
        TopPane.getMenuRefresh().setOnAction(event -> {
            ReloadControl.requestComponentReload(true, GalleryPane.class, RightPane.class);
        });
    }
    private static void setOnAction_menuReset() {
        TopPane.getMenuReset().setOnAction(event -> {
            FilterControl.setFilter(FilterCollection.SHOW_EVERYTHING);
            ReloadControl.requestComponentReload(true, GalleryPane.class, RightPane.class);
        });
    }
}
