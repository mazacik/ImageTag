package project.gui.event.handler.rightpane;

import project.database.control.TagElementControl;
import project.gui.component.rightpane.RightPane;

public abstract class EventHandlerRightPaneContextMenuEdit {
    public static void onAction() {
        TagElementControl.edit(TagElementControl.getTagElement(RightPane.getListView().getSelectionModel().getSelectedItem()));
    }
}
