package userinterface.node.topmenu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ClickableMenu extends Menu {
    private MenuItem menuItemDummy = new MenuItem();

    public ClickableMenu(String string) {
        super(string);
        getItems().add(menuItemDummy);
        showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                menuItemDummy.fire();
            }
        });
    }
    public void _setOnAction(EventHandler<ActionEvent> value) {
        menuItemDummy.setOnAction(value);
    }
}
