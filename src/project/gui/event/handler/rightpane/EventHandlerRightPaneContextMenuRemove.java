package project.gui.event.handler.rightpane;

import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.TagControl;
import project.gui.component.rightpane.RightPane;

public abstract class EventHandlerRightPaneContextMenuRemove {
    public static void onAction() {
        FilterControl.removeTagElementSelectionFromDataElementSelection();
        ReloadControl.request(true, RightPane.class);
        TagControl.remove(TagControl.getTagObject(RightPane.getListView().getSelectionModel().getSelectedItem()));
    }
}
