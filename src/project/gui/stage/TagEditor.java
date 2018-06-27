package project.gui.stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.database.part.TagItem;

public class TagEditor extends Stage {
    /* components */
    private final BorderPane editorPane = new BorderPane();
    private final Scene editorScene = new Scene(editorPane);

    private final TextField tfCategory = new TextField();
    private final TextField tfName = new TextField();
    private final Button btnOK = new Button("OK");

    /* constructors */
    public TagEditor(TagItem tagItem) {
        if (tagItem == null) return;
        initializeComponents(tagItem);
        initializeProperties();
    }

    /* initialize methods */
    private void initializeComponents(TagItem tagItem) {
        editorPane.setCenter(new VBox(2, tfCategory, tfName));
        editorPane.setBottom(btnOK);

        tfCategory.requestFocus();
        tfCategory.setText(tagItem.getGroup());
        tfName.setText(tagItem.getName());

        tfCategory.prefWidthProperty().bind(widthProperty());
        tfName.prefWidthProperty().bind(widthProperty());
        btnOK.prefWidthProperty().bind(widthProperty());

        btnOK.setOnAction(event -> {
            String category = tfCategory.getText();
            String name = tfName.getText();
            if (!category.isEmpty() && !name.isEmpty()) {
                tagItem.setGroup(category);
                tagItem.setName(name);
            }
        });
    }
    private void initializeProperties() {
        setTitle("Edit Tag");
        setAlwaysOnTop(true);
        setResizable(false);
        setWidth(300);
        setScene(editorScene);
        centerOnScreen();
        showAndWait();
    }
}