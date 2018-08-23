package project.gui.event.listener.rightpane;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import project.control.ReloadControl;
import project.gui.component.rightpane.RightPane;

public class EventListenerButtonAdd {
    private static void btnAddOnAction() {
        Button btnAdd = RightPane.getBtnAdd();
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });
        btnAdd.setOnAction(event -> {
            RightPane.addTagToSelection();
            ReloadControl.requestComponentReload(true, RightPane.class);
        });
    }
}
