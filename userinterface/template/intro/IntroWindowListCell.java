package userinterface.template.intro;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import utils.CommonUtil;

public class IntroWindowListCell extends BorderPane {
    private Label nameLabel = new Label();
    private Label pathLabel = new Label();

    private Label removeLabel = new Label("✕");

    public IntroWindowListCell(String path) {
        VBox vBox = new VBox(nameLabel, pathLabel);
        vBox.setAlignment(Pos.CENTER_LEFT);

        this.setCenter(vBox);
        this.setRight(removeLabel);
        setAlignment(removeLabel, Pos.CENTER);

        removeLabel.setFont(new Font(20));
        removeLabel.setTextFill(CommonUtil.getTextColorDefault());
        removeLabel.setOnMouseEntered(event -> removeLabel.setTextFill(CommonUtil.getTextColorHighlight()));
        removeLabel.setOnMouseExited(event -> removeLabel.setTextFill(CommonUtil.getTextColorDefault()));
        removeLabel.setVisible(false);

        nameLabel.setText(this.formatName(path));
        nameLabel.setFont(new Font(14));
        pathLabel.setText(path);
        pathLabel.setFont(new Font(14));

        nameLabel.setTextFill(CommonUtil.getTextColorDefault());
        pathLabel.setTextFill(CommonUtil.getTextColorDefault());

        this.setBackground(CommonUtil.getButtonBackgroundHover());
        this.setOnMouseEntered(event -> {
            this.setBackground(CommonUtil.getButtonBackgroundDefault());
            this.setCursor(Cursor.HAND);
            removeLabel.setVisible(true);
        });
        this.setOnMouseExited(event -> {
            this.setBackground(CommonUtil.getButtonBackgroundHover());
            this.setCursor(Cursor.DEFAULT);
            removeLabel.setVisible(false);
        });

        this.setPadding(new Insets(10));
    }

    private String formatName(String path) {
        char separator;
        if (path.contains("/")) {
            separator = '/';
        } else {
            separator = '\\';
        }

        if (path.endsWith(String.valueOf(separator))) {
            path = path.substring(0, path.length() - 1);
        }

        return path.substring(path.lastIndexOf(separator) + 1);
    }

    public String getPath() {
        return pathLabel.getText();
    }
    public Label getRemoveLabel() {
        return removeLabel;
    }
}
