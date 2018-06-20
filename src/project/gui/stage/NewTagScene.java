package project.gui.stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.database.TagDatabase;

public class NewTagScene extends Stage {
    /* components */
    private final BorderPane newTagPane = new BorderPane();
    private final Scene NewTagScene = new Scene(newTagPane);

    private final TextField tfCategory = new TextField();
    private final TextField tfName = new TextField();
    private final Button btnAdd = new Button("+");

    /* constructors */
    public NewTagScene() {
        /* frontend */
        setTitle("Crate New Tag");
        setAlwaysOnTop(true);
        setResizable(false);
        setWidth(300);
        centerOnScreen();

        /* initialization */
        setScene(NewTagScene);
        newTagPane.setCenter(new VBox(tfCategory, tfName));
        newTagPane.setBottom(btnAdd);

        tfCategory.prefWidthProperty().bind(widthProperty());
        tfName.prefWidthProperty().bind(widthProperty());
        btnAdd.prefWidthProperty().bind(widthProperty());

        /* action listeners */
        btnAdd.setOnAction(event -> {
            String category = tfCategory.getText();
            String name = tfName.getText();
            if (category.isEmpty() || name.isEmpty()) return;

            if (!TagDatabase.getDatabaseTags().contains(TagDatabase.getTagItem(category, name))) {
                TagDatabase.addTag(name, category);
            }

            close();
        });

        show();
    }
}
