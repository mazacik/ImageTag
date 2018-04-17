package project.gui_components;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import project.backend.Database;
import project.backend.Main;
import project.backend.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RightPane extends BorderPane {
    private static final HBox addPane = new HBox();
    private static final Button addButton = new Button("+");
    private static final TextField addTextField = new TextField();
    private static final ListView<String> list = new ListView<>();

    public RightPane() {
        setPrefWidth(200);
        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setOnAction(event -> {
            String newTag = addTextField.getText();
            if (!newTag.equals("") && !newTag.equals(" ")) {
                if (!Database.getTagDatabase().contains(newTag)) {
                    Database.getTagDatabase().add(newTag);
                    if (Main.getLeftPane().getDisplayMode() == ListDisplayMode.TAGS)
                        Main.getLeftPane().getListView().getItems().add(new ColoredText(newTag, Color.BLACK));
                }
                for (int index : Database.getSelectedIndexes())
                    if (!Database.getItemDatabaseFiltered().get(index).getTags().contains(newTag)) {
                        Database.getItemDatabaseFiltered().get(index).getTags().add(newTag);
                        String tagsFilePath = Settings.DIRECTORY_PATH + "/tags/" + Database.getItemDatabaseFiltered().get(index).getSimpleName() + ".txt";
                        try {
                            File tagsFile = new File(tagsFilePath);
                            if (!tagsFile.exists())
                                tagsFile.createNewFile();
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tagsFilePath, true));
                            bufferedWriter.write(newTag);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });

        addPane.getChildren().addAll(addTextField, addButton);
        setCenter(list);
        setBottom(addPane);
    }
}
