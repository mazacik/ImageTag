package project.gui.event.handler.rightpane;

import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.TagElementControl;
import project.gui.component.rightpane.RightPane;

public abstract class EventHandlerRightPaneContextMenuRemove {
    public static void onAction() {
        FilterControl.removeTagElementSelectionFromDataElementSelection();
        ReloadControl.requestComponentReload(true, RightPane.class);
        TagElementControl.remove(TagElementControl.getTagElement(RightPane.getListView().getSelectionModel().getSelectedItem()));
    }
}
