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
    private final Button btnAdd = new Button("+");

    /* constructors */
    public TagEditor(TagItem tagItem) {
        if (tagItem != null) {
            /* frontend */
            setTitle("Edit Tag");
            setAlwaysOnTop(true);
            setResizable(false);
            setWidth(300);
            centerOnScreen();

            /* initialization */
            setScene(editorScene);
            editorPane.setCenter(new VBox(tfCategory, tfName));
            editorPane.setBottom(btnAdd);

            tfCategory.setText(tagItem.getCategory());
            tfName.setText(tagItem.getName());

            tfCategory.prefWidthProperty().bind(widthProperty());
            tfName.prefWidthProperty().bind(widthProperty());
            btnAdd.prefWidthProperty().bind(widthProperty());

            /* action listeners */
            btnAdd.setOnAction(event -> {
                String category = tfCategory.getText();
                String name = tfName.getText();
                if (!category.isEmpty() && !name.isEmpty()) {
                    tagItem.setCategory(category);
                    tagItem.setName(name);
                }
            });

            showAndWait();
        }
    }
}