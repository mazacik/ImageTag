package userinterface.template.generic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.CommonUtil;

public class TitleBar extends BorderPane {
    public TitleBar(Stage owner) {
        this(owner, "");
    }
    public TitleBar(Stage owner, String title) {
        Label labelTitle = new Label(title);
        labelTitle.setFont(CommonUtil.getFont());
        labelTitle.setTextFill(CommonUtil.getTextColorDefault());
        labelTitle.setPadding(new Insets(1, 5, 1, 5));
        BorderPane.setAlignment(labelTitle, Pos.CENTER_LEFT);

        Button btnExit = new Button("✕");
        btnExit.setFont(CommonUtil.getFont());
        btnExit.setTextFill(CommonUtil.getTextColorDefault());
        btnExit.setPadding(new Insets(1, 5, 1, 5));
        btnExit.setBackground(CommonUtil.getBackgroundDefault());
        btnExit.setOnMouseEntered(event -> btnExit.setTextFill(CommonUtil.getTextColorHighlight()));
        btnExit.setOnMouseExited(event -> btnExit.setTextFill(CommonUtil.getTextColorDefault()));
        btnExit.setOnMouseClicked(event -> btnExit.fireEvent(new WindowEvent(owner, WindowEvent.WINDOW_CLOSE_REQUEST)));

        this.setCenter(labelTitle);
        this.setRight(btnExit);
        this.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
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
}
