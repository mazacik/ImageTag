package project.gui.stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.database.element.TagElement;

public class TagEditor extends Stage {
    /* components */
    private final BorderPane editorPane = new BorderPane();
    private final Scene editorScene = new Scene(editorPane);

    private final TextField tfGroup = new TextField();
    private final TextField tfName = new TextField();
    private final Button btnOK = new Button("OK");

    /* constructors */
    public TagEditor(TagElement tagElement) {
        initializeComponents(tagElement);
        initializeProperties();
    }

    public TagEditor() {
        this(null);
    }

    /* public */
    public TagElement getResult() {
        String group = tfGroup.getText();
        String name = tfName.getText();
        if (!group.isEmpty() && !name.isEmpty()) {
            return new TagElement(group, name);
        } else {
            return null;
        }
    }

    /* initialize */
    private void initializeComponents(TagElement tagElement) {
        editorPane.setCenter(new VBox(2, tfGroup, tfName));
        editorPane.setBottom(btnOK);

        tfGroup.requestFocus();
        if (tagElement != null) {
            tfGroup.setText(tagElement.getGroup());
            tfName.setText(tagElement.getName());
        }

        tfGroup.prefWidthProperty().bind(widthProperty());
        tfName.prefWidthProperty().bind(widthProperty());
        btnOK.prefWidthProperty().bind(widthProperty());

        btnOK.setOnAction(event -> close());
    }
    private void initializeProperties() {
        setTitle("Tag Editor");
        setAlwaysOnTop(true);
        setResizable(false);
        setWidth(300);
        setScene(editorScene);
        centerOnScreen();
        showAndWait();
    }
}