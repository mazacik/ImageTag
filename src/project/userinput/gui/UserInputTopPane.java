package project.userinput.gui;

import javafx.stage.WindowEvent;
import project.Main;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.loader.Serialization;
import project.gui.component.GalleryPane;
import project.gui.component.LeftPane;
import project.gui.component.RightPane;
import project.gui.component.TopPane;

public abstract class UserInputTopPane {
    public static void initialize() {
        setOnAction_menuSave();
        setOnAction_menuExit();

        setOnAction_menuSelectAll();
        setOnAction_menuClearSelection();

        setOnAction_menuUntaggedOnly();
        setOnAction_menuLessThanXTags();
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
        ReloadControl.requestReloadOf(true, GalleryPane.class, RightPane.class);
    }
    private static void setOnAction_menuClearSelection() {
        TopPane.getMenuClearSelection().setOnAction(event -> SelectionControl.clearDataElements());
        ReloadControl.requestReloadOf(true, GalleryPane.class, RightPane.class);
    }

    private static void setOnAction_menuUntaggedOnly() {
        TopPane.getMenuUntaggedOnly().setOnAction(event -> {
            FilterControl.setCustomFilterUntaggedOnly(true);
            FilterControl.setCustomFilterLessThanXTags(false);
            FilterControl.revalidateDataElements();
            TopPane.getMenuUntaggedOnly().setSelected(true);
            TopPane.getMenuLessThanXTags().setSelected(false);
            ReloadControl.requestReloadOf(true, LeftPane.class, GalleryPane.class);
        });
    }
    private static void setOnAction_menuLessThanXTags() {
        TopPane.getMenuLessThanXTags().setOnAction(event -> {
            FilterControl.setCustomFilterUntaggedOnly(false);
            FilterControl.setCustomFilterLessThanXTags(true);
            FilterControl.customFilterLessThanXTagsGetValue();
            FilterControl.revalidateDataElements();
            TopPane.getMenuUntaggedOnly().setSelected(false);
            TopPane.getMenuLessThanXTags().setSelected(true);
            ReloadControl.requestReloadOf(true, GalleryPane.class, RightPane.class);
        });
    }
    private static void setOnAction_menuRefresh() {
        TopPane.getMenuRefresh().setOnAction(event -> {
            FilterControl.revalidateDataElements();
            ReloadControl.requestReloadOf(true, GalleryPane.class, RightPane.class);
        });
    }
    private static void setOnAction_menuReset() {
        TopPane.getMenuReset().setOnAction(event -> {
            TopPane.getMenuUntaggedOnly().setSelected(false);
            TopPane.getMenuLessThanXTags().setSelected(false);
            FilterControl.customFilterResetFiltering();
            ReloadControl.requestReloadOf(true, GalleryPane.class, RightPane.class);
        });
    }
}
