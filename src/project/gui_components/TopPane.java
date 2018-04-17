package project.gui_components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class TopPane extends BorderPane {
    private static final Menu infoLabel = new Menu();

    public TopPane() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        menuBar.getMenus().addAll(menuFile);
        setCenter(menuBar);

        /* InfoLabel */
        MenuBar infoLabelArea = new MenuBar();
        infoLabelArea.getMenus().add(infoLabel);
        setRight(infoLabelArea);
    }

    public Menu getInfoLabel() {
        return infoLabel;
    }
}
