package userinterface.node.topmenu;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import utils.CommonUtil;

public class CustomCM extends Popup {
    private VBox vBox = new VBox();

    public CustomCM(Label... labels) {
        vBox.getChildren().addAll(labels);
        vBox.setBackground(CommonUtil.getBackgroundDefault());
        vBox.setPadding(new Insets(5));

        this.getContent().add(vBox);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
    }
}
