package project.gui.component.rightpane;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import project.database.object.DataObject;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.custom.specific.RightPaneContextMenu;
import project.utils.MainUtil;

import java.util.ArrayList;

public class RightPane extends VBox implements MainUtil {
    private final Button btnCommon; // == 0
    private final Button btnRecent; // == 1
    private final ListView<String> suggestionListView;
    private ArrayList<String> recentTags = new ArrayList<>();
    private SuggestMode suggestMode = SuggestMode.COMMON;

    private final ChoiceBox cbGroup;
    private final ChoiceBox cbName;
    private final Button btnAdd;
    private final Button btnNew;
    private final RightPaneContextMenu contextMenu;
    private final ListView<String> intersectionListView;

    public RightPane() {
        btnCommon = new Button("Common");
        btnRecent = new Button("Recent");
        suggestionListView = new ListView<>();
        cbGroup = new ChoiceBox();
        cbName = new ChoiceBox();
        btnAdd = new Button("Add");
        btnNew = new Button("New");
        contextMenu = new RightPaneContextMenu();
        intersectionListView = new ListView<>();

        this.setDefaultValuesChildren();
        this.setDefaultValues();
    }
    private void setDefaultValuesChildren() {
        cbGroup.prefWidthProperty().bind(this.prefWidthProperty());
        cbGroup.maxWidthProperty().bind(this.maxWidthProperty());
        cbName.prefWidthProperty().bind(this.prefWidthProperty());
        cbName.maxWidthProperty().bind(this.maxWidthProperty());
        btnAdd.prefWidthProperty().bind(this.prefWidthProperty());
        btnAdd.maxWidthProperty().bind(this.maxWidthProperty());
        btnNew.prefWidthProperty().bind(this.prefWidthProperty());
        btnNew.maxWidthProperty().bind(this.maxWidthProperty());

        btnCommon.setMaxWidth(Double.MAX_VALUE);
        btnRecent.setMaxWidth(Double.MAX_VALUE);

        suggestionListView.setMaxHeight(Double.MAX_VALUE);
        suggestionListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        suggestionListView.setMaxHeight(5 * 24);
        intersectionListView.setMaxHeight(Double.MAX_VALUE);


        //btnAdd.setDisable(true);
        cbName.setDisable(true);

        intersectionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        intersectionListView.setContextMenu(contextMenu);
    }
    private void setDefaultValues() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);
        this.setSpacing(2);

        VBox.setVgrow(suggestionListView, Priority.ALWAYS);
        VBox.setVgrow(intersectionListView, Priority.ALWAYS);

        HBox hbox1 = new HBox(btnCommon, btnRecent);
        HBox.setHgrow(btnCommon, Priority.ALWAYS);
        HBox.setHgrow(btnRecent, Priority.ALWAYS);

        this.getChildren().addAll(hbox1, suggestionListView, cbGroup, cbName, btnAdd, btnNew, intersectionListView);
    }

    public void addTagToSelection() {
        Object cbGroupValue = cbGroup.getValue();
        Object cbNameValue = cbName.getValue();
        String group = "";
        String name = "";

        try {
            group = cbGroupValue.toString();
            name = cbNameValue.toString();
        } catch (NullPointerException ignored) {}

        if (!group.isEmpty() && !name.isEmpty()) {
            TagObject tagObject = mainTags.getTagObject(group, name);
            if (selection.size() < 1) {
                DataObject currentFocusedItem = focus.getCurrentFocus();
                if (currentFocusedItem != null) {
                    currentFocusedItem.getTagCollection().add(tagObject);
                }
            } else {
                selection.addTagObject(tagObject);
            }

            ArrayList<String> oldRecentTags = this.recentTags;
            ArrayList<String> newRecentTags = new ArrayList<>();
            String tagGroupAndName = tagObject.getGroupAndName();
            newRecentTags.add(tagGroupAndName);

            if (oldRecentTags.size() != 0) {
                oldRecentTags.remove(tagGroupAndName);
                newRecentTags.addAll(oldRecentTags);
            }

            this.recentTags = newRecentTags;
            reload.queue(GUINode.RIGHTPANE);
        }
    }
    public void reload() {
        this.reloadIntersecting();
        this.reloadSuggested();
    }
    private void reloadIntersecting() {
        ArrayList<String> sharedTags = new ArrayList<>();
        if (selection.size() == 0) {
            DataObject currentFocusedItem = focus.getCurrentFocus();
            if (currentFocusedItem != null) {
                for (TagObject tagObject : currentFocusedItem.getTagCollection()) {
                    sharedTags.add(tagObject.getGroupAndName());
                }
            }
        } else {
            for (TagObject tagObject : selection.getIntersectingTags()) {
                sharedTags.add(tagObject.getGroupAndName());
            }
        }
        intersectionListView.getItems().setAll(sharedTags);
    }
    private void reloadSuggested() {
        switch (suggestMode) {
            case COMMON:
                ArrayList<String> topTenTags = new ArrayList<>();
                for (TagObject tagObject : mainTags) {

                }
                break;
            case RECENT:
                suggestionListView.getItems().setAll(recentTags);
                break;
            default:
                break;
        }
    }

    public Button getBtnCommon() {
        return btnCommon;
    }
    public Button getBtnRecent() {
        return btnRecent;
    }

    public ChoiceBox getCbGroup() {
        return cbGroup;
    }
    public ChoiceBox getCbName() {
        return cbName;
    }
    public Button getBtnAdd() {
        return btnAdd;
    }
    public Button getBtnNew() {
        return btnNew;
    }

    public RightPaneContextMenu getContextMenu() {
        return contextMenu;
    }
    public ListView<String> getSuggestionListView() {
        return suggestionListView;
    }
    public ListView<String> getIntersectionListView() {
        return intersectionListView;
    }

    public void setSuggestMode(SuggestMode suggestMode) {
        this.suggestMode = suggestMode;
    }
}
