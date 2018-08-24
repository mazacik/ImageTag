package project.gui.custom.specific;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.database.element.TagObject;

public class TagEditor extends Stage {
    /* components */
    private final BorderPane editorPane = new BorderPane();
    private final Scene editorScene = new Scene(editorPane);

    private final TextField tfGroup = new TextField();
    private final TextField tfName = new TextField();
    private final Button btnOK = new Button("OK");

    /* vars */
    TagObject tagObject;

    /* constructors */
    public TagEditor(TagObject tagObject) {
        this.tagObject = tagObject;
        initializeComponents();
        initializeInstance();
    }

    public TagEditor() {
        this(null);
    }

    /* public */
    public static TagObject createTag() {
        TagObject newTagObject = new TagEditor().getResult();
        if (newTagObject.equals(null) || newTagObject.isEmpty()) return null;
        return newTagObject;
    }

    public TagObject getResult() {
        return tagObject;
    }

    /* private */
    private void getValue() {
        String group = tfGroup.getText();
        String name = tfName.getText();
        if (!group.isEmpty() && !name.isEmpty()) {
            tagObject = new TagObject(group, name);
        }
    }

    /* initialize */
    private void initializeComponents() {
        editorPane.setCenter(new VBox(2, tfGroup, tfName));
        editorPane.setBottom(btnOK);
        editorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnOK.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });

        tfGroup.requestFocus();
        if (tagObject != null) {
            tfGroup.setText(tagObject.getGroup());
            tfName.setText(tagObject.getName());
        }

        tfGroup.prefWidthProperty().bind(widthProperty());
        tfName.prefWidthProperty().bind(widthProperty());
        btnOK.prefWidthProperty().bind(widthProperty());

        btnOK.setOnAction(event -> {
            getValue();
            close();
        });
    }
    private void initializeInstance() {
        setTitle("Tag Editor");
        setAlwaysOnTop(true);
        setResizable(false);
        setWidth(300);
        setScene(editorScene);
        centerOnScreen();
        showAndWait();
    }
}