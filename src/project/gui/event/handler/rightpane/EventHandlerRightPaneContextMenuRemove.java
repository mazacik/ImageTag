package project.gui.event.handler.rightpane;

import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.TagControl;
import project.gui.component.GUINode;
import project.gui.component.rightpane.RightPane;

public abstract class EventHandlerRightPaneContextMenuRemove {
    public static void onAction() {
        SelectionControl.removeTagObjectSelection();
        ReloadControl.reload(true, GUINode.RIGHTPANE);
        TagControl.remove(TagControl.getTagObject(RightPane.getListView().getSelectionModel().getSelectedItem()));
    }
}
