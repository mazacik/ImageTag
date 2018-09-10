package project.gui.event.listener.toppane;

import javafx.stage.WindowEvent;
import project.Main;
import project.control.Filter;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.component.toppane.TopPane;
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
        TopPane.getMenuSelectAll().setOnAction(event -> SelectionControl.addDataObject(FilterControl.getCollection()));
        ReloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
    }
    private static void setOnAction_menuClearSelection() {
        TopPane.getMenuClearSelection().setOnAction(event -> SelectionControl.clearDataObjects());
        ReloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
    }

    private static void setOnAction_menuUntaggedOnly() {
        TopPane.getMenuUntaggedOnly().setOnAction(event -> {
            FilterControl.setFilter(Filter.SHOW_UNTAGGED);
            ReloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void setOnAction_menuMaxXTags() {
        TopPane.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("Filter Settings", "Maximum number of tags:").getResultValue();
            if (maxTags == 0) return;
            Filter.setMaxTagsValue(maxTags);

            FilterControl.setFilter(Filter.SHOW_MAX_X_TAGS);
            ReloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void setOnAction_menuRefresh() {
        TopPane.getMenuRefresh().setOnAction(event -> {
            ReloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void setOnAction_menuReset() {
        TopPane.getMenuReset().setOnAction(event -> {
            FilterControl.setFilter(Filter.SHOW_EVERYTHING);
            ReloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
}
