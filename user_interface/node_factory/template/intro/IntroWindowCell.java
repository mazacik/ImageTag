package user_interface.node_factory.template.intro;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import user_interface.node_factory.NodeColorData;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;

public class IntroWindowCell extends BorderPane {
    private Label nodeName;
    private Label nodePath;

    private Label nodeRemove;

    public IntroWindowCell(String path) {
        NodeColorData nodeProps = new NodeColorData(ColorType.NULL, ColorType.NULL, ColorType.DEF, ColorType.DEF);

        nodeName = NodeFactory.getLabel("", nodeProps);
        nodePath = NodeFactory.getLabel("", nodeProps);
        VBox vBox = new VBox(nodeName, nodePath);
        vBox.setAlignment(Pos.CENTER_LEFT);

        this.setCenter(vBox);
        nodeRemove = NodeFactory.getLabel("✕", ColorType.NULL, ColorType.NULL, ColorType.DEF, ColorType.ALT);
        this.setRight(nodeRemove);
        setAlignment(nodeRemove, Pos.CENTER);

        nodeRemove.setFont(new Font(20));
        nodeRemove.setVisible(false);

        nodeName.setText(this.getDirectoryFromPath(path));
        nodePath.setText(path);

        this.setPadding(new Insets(10));
    }

    private String getDirectoryFromPath(String path) {
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
        return nodePath.getText();
    }
    public Label getNodeRemove() {
        return nodeRemove;
    }
}
