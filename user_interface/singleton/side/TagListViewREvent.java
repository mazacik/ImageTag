package user_interface.singleton.side;

import control.reload.Reload;
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
        InstanceManager.getTagListViewR().setOnMouseClicked(event -> InstanceManager.getTagListViewR().requestFocus());
    }

    private void tfSearchEvents() {
        InstanceManager.getTagListViewR().getTfSearch().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            String actualText = InstanceManager.getTagListViewR().getActualText();
            if (event.getCode().isLetterKey()) {
                InstanceManager.getTagListViewR().setActualText(actualText + event.getText().toLowerCase());
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                if (!actualText.isEmpty()) {
                    InstanceManager.getTagListViewR().setActualText(actualText.substring(0, actualText.length() - 1));
                }
            }
        });
        InstanceManager.getTagListViewR().getTfSearch().setOnKeyTyped(event -> {
            event.consume();

            String actualText = InstanceManager.getTagListViewR().getActualText();
            TextField tfSearch = InstanceManager.getTagListViewR().getTfSearch();

            if (actualText.isEmpty()) {
                tfSearch.clear();
            } else if (actualText.length() < 3) {
                tfSearch.setText(actualText);
                tfSearch.positionCaret(tfSearch.getLength());
            } else if (actualText.length() >= 3) {
                ArrayList<TagObject> matches = new ArrayList<>();
                for (TagObject tagObject : InstanceManager.getMainInfoList()) {
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
        InstanceManager.getTagListViewR().getTfSearch().setOnAction(event -> {
            if (!InstanceManager.getSelect().isEmpty()) {
                TextField tfSearch = InstanceManager.getTagListViewR().getTfSearch();
                TagObject tagObject = InstanceManager.getMainInfoList().getTagObject(tfSearch.getText());
                if (tagObject != null) {
                    InstanceManager.getTagListViewR().addTagObjectToSelection(tagObject);
                    InstanceManager.getTagListViewR().setActualText("");
                    tfSearch.clear();
                    InstanceManager.getReload().notifyChangeIn(Reload.Control.INFO);
                    InstanceManager.getReload().doReload();
                }
            }
        });
    }

    private void onAction_menuSelectAll() {
        InstanceManager.getTagListViewR().getNodeSelectAll().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().addAll(InstanceManager.getFilter());
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuClearSelection() {
        InstanceManager.getTagListViewR().getNodeSelectNone().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().clear();
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuMergeSelection() {
        InstanceManager.getTagListViewR().getNodeSelectMerge().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().merge();
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
}
