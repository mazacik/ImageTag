package user_interface.factory.base;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.enums.ColorType;

public class CheckBoxNode extends Pane {
    private final TextNode nodeMark;
    private final TextNode nodeText;

    private SimpleBooleanProperty selectedProperty;

    public CheckBoxNode(String text) {
        this(text, false);
    }
    public CheckBoxNode(String text, boolean selected) {
        super();

        this.nodeText = new TextNode(text, ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);
        this.nodeMark = new TextNode("", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);

        this.selectedProperty = new SimpleBooleanProperty();
        this.setSelected(selected);

        Insets insets = new Insets(-1, 2, -1, 2);
        nodeText.setPadding(insets);
        nodeMark.setPadding(insets);
        nodeMark.setBorder(NodeUtil.getBorder(1));
        nodeMark.heightProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> nodeMark.setMinWidth(nodeMark.getHeight())));

        HBox hBox = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeMark, nodeText);
        hBox.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler<Event>) event -> setSelected(!isSelected()));
        hBox.setSpacing(SizeUtil.getGlobalSpacing());

        this.getChildren().add(hBox);
    }
    public void setTextFill(Color color) {
        nodeMark.setTextFill(color);
        nodeText.setTextFill(color);
    }
    public void setText(String text) {
        nodeText.setText(text);
    }
    public boolean isSelected() {
        return selectedProperty.getValue();
    }
    public void setSelected(boolean selected) {
        selectedProperty.setValue(selected);

        if (selected) {
            nodeMark.setText("✕");
        } else {
            nodeMark.setText("");
        }
    }
    public SimpleBooleanProperty getSelectedProperty() {
        return selectedProperty;
    }
}
