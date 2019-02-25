package user_interface.node_factory.template.generic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import system.CommonUtil;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;

public class TitleBar extends BorderPane {
    private final Label labelTitle;
    private final Label btnExit;

    public TitleBar(Stage owner) {
        this(owner, "");
    }
    public TitleBar(Stage owner, String title) {
        labelTitle = NodeFactory.getLabel(title, ColorType.DEF, ColorType.DEF);
        labelTitle.setFont(CommonUtil.getFont());
        labelTitle.setPadding(new Insets(1, 5, 1, 5));
        BorderPane.setAlignment(labelTitle, Pos.CENTER_LEFT);

        btnExit = NodeFactory.getLabel("✕", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.ALT);
        btnExit.setFont(CommonUtil.getFont());
        btnExit.setPadding(new Insets(1, 5, 1, 5));
        btnExit.setOnMouseClicked(event -> btnExit.fireEvent(new WindowEvent(owner, WindowEvent.WINDOW_CLOSE_REQUEST)));
        BorderPane.setAlignment(btnExit, Pos.CENTER);

        NodeFactory.addNodeToBackgroundManager(this, ColorType.DEF);

        this.setCenter(labelTitle);
        this.setRight(btnExit);
        this.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        final double[] xOffset = {0};
        final double[] yOffset = {0};
        this.setOnMousePressed(event -> {
            xOffset[0] = owner.getX() - event.getScreenX();
            yOffset[0] = owner.getY() - event.getScreenY();
        });
        this.setOnMouseDragged(event -> {
            owner.setX(event.getScreenX() + xOffset[0]);
            owner.setY(event.getScreenY() + yOffset[0]);
        });
    }

    public Label getLabelTitle() {
        return labelTitle;
    }
    public Label getBtnExit() {
        return btnExit;
    }
}
