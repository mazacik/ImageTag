package project.gui.event.handler.rightpane;

import project.database.control.TagControl;
import project.gui.component.rightpane.RightPane;

public abstract class EventHandlerRightPaneContextMenuEdit {
    public static void onAction() {
        TagControl.edit(TagControl.getTagObject(RightPane.getListView().getSelectionModel().getSelectedItem()));
    }
}
