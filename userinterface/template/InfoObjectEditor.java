package userinterface.template;

import database.object.InfoObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class InfoObjectEditor extends Stage {
    private final GridPane editorPane = new GridPane();
    private final Scene editorScene = new Scene(editorPane);

    private final TextField tfGroup = new TextField();
    private final TextField tfName = new TextField();
    private final Label lblGroup = new Label("Group");
    private final Label lblName = new Label("Name");
    private final Button btnOK = new Button("OK");

    InfoObject infoObject;

    public InfoObjectEditor(InfoObject infoObject) {
        this.infoObject = infoObject;
        initializeComponents();
        initializeInstance();
    }
    public InfoObjectEditor() {
        this(null);
    }

    public InfoObject getResult() {
        return infoObject;
    }
    private void getValue() {
        String group = tfGroup.getText();
        String name = tfName.getText();
        if (!group.isEmpty() && !name.isEmpty()) {
            infoObject = new InfoObject(group, name);
        }
    }

    private void initializeComponents() {
        editorPane.add(lblGroup, 0, 0);
        editorPane.add(lblName, 0, 1);
        editorPane.add(tfGroup, 1, 0);
        editorPane.add(tfName, 1, 1);
        editorPane.add(btnOK, 1, 2);

        lblGroup.setMinWidth(40);
        tfGroup.setMinWidth(200);

        editorPane.setPadding(new Insets(10));
        editorPane.setHgap(5);
        editorPane.setVgap(3);

        editorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnOK.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });

        tfGroup.requestFocus();
        if (infoObject != null) {
            tfGroup.setText(infoObject.getGroup());
            tfName.setText(infoObject.getName());
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
        setTitle("Editor");
        setAlwaysOnTop(true);
        setResizable(false);
        setScene(editorScene);
        centerOnScreen();
        showAndWait();
    }
}