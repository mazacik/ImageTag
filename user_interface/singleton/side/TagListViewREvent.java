package user_interface.singleton.side;

import control.Reload;
import database.object.TagObject;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import lifecycle.InstanceManager;
import user_interface.factory.menu.ClickMenuLeft;

import java.util.ArrayList;

public class TagListViewREvent {
    public TagListViewREvent() {
        onMouseClick();

        tfSearchEvents();

        onAction_menuSelectAll();
        onAction_menuClearSelection();
        onAction_menuMergeSelection();
    }

    private void onMouseClick() {
        InstanceManager.getSelectPane().setOnMouseClicked(event -> InstanceManager.getSelectPane().requestFocus());
    }

    private void tfSearchEvents() {
        InstanceManager.getSelectPane().getTfSearch().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            String actualText = InstanceManager.getSelectPane().getActualText();
            if (event.getCode().isLetterKey()) {
                InstanceManager.getSelectPane().setActualText(actualText + event.getText().toLowerCase());
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                if (!actualText.isEmpty()) {
                    InstanceManager.getSelectPane().setActualText(actualText.substring(0, actualText.length() - 1));
                }
            }
        });
        InstanceManager.getSelectPane().getTfSearch().setOnKeyTyped(event -> {
            event.consume();

            String actualText = InstanceManager.getSelectPane().getActualText();
            TextField tfSearch = InstanceManager.getSelectPane().getTfSearch();

            if (actualText.isEmpty()) {
                tfSearch.clear();
            } else if (actualText.length() < 3) {
                tfSearch.setText(actualText);
                tfSearch.positionCaret(tfSearch.getLength());
            } else if (actualText.length() >= 3) {
                ArrayList<TagObject> matches = new ArrayList<>();
                for (TagObject tagObject : InstanceManager.getTagListMain()) {
                    if (tagObject.getFull().toLowerCase().contains(actualText)) {
                        matches.add(tagObject);
                    }
                }

                //this part could use some optimization
                TagObject bestMatch = null;
                //tag's name starts with actualtext
                for (TagObject tagObject : matches) {
                    if (tagObject.getName().toLowerCase().startsWith(actualText)) {
                        bestMatch = tagObject;
                        break;
                    }
                }
                if (bestMatch == null) {
                    //tag's name contains actualtext
                    for (TagObject tagObject : matches) {
                        if (tagObject.getName().toLowerCase().contains(actualText)) {
                            bestMatch = tagObject;
                            break;
                        }
                    }
                }
                if (bestMatch == null) {
                    //tag (group and name) contains actualtext
                    for (TagObject tagObject : matches) {
                        if (tagObject.getFull().toLowerCase().contains(actualText)) {
                            bestMatch = tagObject;
                            break;
                        }
                    }
                }
                //end of the part that could use some optimization

                if (bestMatch != null) {
                    String tagFull = bestMatch.getFull();
                    tfSearch.setText(tagFull);
                    int caretPos = tagFull.toLowerCase().lastIndexOf(actualText) + actualText.length();
                    tfSearch.positionCaret(caretPos);
                } else {
                    tfSearch.setText(actualText);
                    tfSearch.positionCaret(tfSearch.getLength());
                }
            }
        });
        InstanceManager.getSelectPane().getTfSearch().setOnAction(event -> {
            if (!InstanceManager.getSelect().isEmpty()) {
                TextField tfSearch = InstanceManager.getSelectPane().getTfSearch();
                TagObject tagObject = InstanceManager.getTagListMain().getTagObject(tfSearch.getText());
                if (tagObject != null) {
                    InstanceManager.getSelectPane().addTagObjectToSelection(tagObject);
                    InstanceManager.getSelectPane().setActualText("");
                    tfSearch.clear();
                    InstanceManager.getReload().flag(Reload.Control.INFO);
                    InstanceManager.getReload().doReload();
                }
            }
        });
    }

    private void onAction_menuSelectAll() {
        InstanceManager.getSelectPane().getNodeSelectAll().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().addAll(InstanceManager.getFilter());
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuClearSelection() {
        InstanceManager.getSelectPane().getNodeSelectNone().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().clear();
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuMergeSelection() {
        InstanceManager.getSelectPane().getNodeSelectMerge().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().merge();
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
}
