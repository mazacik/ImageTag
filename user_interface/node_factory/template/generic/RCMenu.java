package user_interface.node_factory.template.generic;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import system.CommonUtil;
import user_interface.node_factory.utils.ColorUtil;

public class RCMenu extends Popup {
    private Label root;
    private VBox vBox = new VBox();

    public RCMenu(Label root, Label... labels) {
        this.root = root;
        this.root.setOnMouseClicked(event -> {
            Bounds rootBounds = root.getBoundsInParent();
            this.show(CommonUtil.mainStage, rootBounds.getMinX(), rootBounds.getMaxY() + 1);
            this.setLabelGroupWidth(labels);
        });

        vBox.getChildren().addAll(labels);
        vBox.setBackground(ColorUtil.getBackgroundDef());
        vBox.setPadding(new Insets(5));

        this.getContent().add(vBox);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
    }

    private void setLabelGroupWidth(Label... labels) {
        double width = 0;
        for (Label label : labels) {
            if (width < label.getWidth()) width = label.getWidth();
        }
        for (Label label : labels) {
            label.setPrefWidth(width);
        }
    }

    public Label getRoot() {
        return root;
    }
    public ObservableList<Node> getChildren() {
        return vBox.getChildren();
    }
}
