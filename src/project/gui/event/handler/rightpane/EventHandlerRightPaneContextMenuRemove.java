package project.gui.event.handler.rightpane;

import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.TagControl;
import project.gui.component.rightpane.RightPane;

public abstract class EventHandlerRightPaneContextMenuRemove {
    public static void onAction() {
        SelectionControl.removeTagObjectSelectionFromDataObjectSelection();
        ReloadControl.reload(true, RightPane.class);
        TagControl.remove(TagControl.getTagObject(RightPane.getListView().getSelectionModel().getSelectedItem()));
    }
}
