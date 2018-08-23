package project.gui.event.handler.rightpane;

import javafx.scene.control.ChoiceBox;
import project.control.ReloadControl;
import project.database.control.TagElementControl;
import project.database.element.TagElement;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.rightpane.RightPane;

public class EventHandlerButtonNew {
    private static void btnNewOnAction() {
        ChoiceBox cbGroup = RightPane.getCbGroup();
        ChoiceBox cbName = RightPane.getCbName();
        RightPane.getBtnNew().setOnAction(event -> {
            TagElement newTagElement = TagElementControl.create();
            if (newTagElement != null) {
                TagElementControl.add(newTagElement);
                cbGroup.getItems().setAll(TagElementControl.getGroups());
                cbGroup.getSelectionModel().select(newTagElement.getGroup());
                Object value = cbGroup.getValue();
                String group = value.toString();
                cbName.getItems().setAll(TagElementControl.getNamesInGroup(group));
                cbName.getSelectionModel().select(newTagElement.getName());
                ReloadControl.requestComponentReload(true, LeftPane.class);
            }
        });
    }
}
