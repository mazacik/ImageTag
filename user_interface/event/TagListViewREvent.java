package user_interface.event;

import control.reload.Reload;
import database.object.TagObject;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import system.InstanceRepo;
import user_interface.factory.node.popup.LeftClickMenu;

import java.util.ArrayList;

public class TagListViewREvent implements InstanceRepo {
    public TagListViewREvent() {
        onMouseClick();

        tfSearchEvents();

        onAction_menuSelectAll();
        onAction_menuClearSelection();
        onAction_menuMergeSelection();
    }

    private void onMouseClick() {
        tagListViewR.setOnMouseClicked(event -> tagListViewR.requestFocus());
    }

    private void tfSearchEvents() {
        tagListViewR.getTfSearch().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            String actualText = tagListViewR.getActualText();
            if (event.getCode().isLetterKey()) {
                tagListViewR.setActualText(actualText + event.getText().toLowerCase());
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                if (!actualText.isEmpty()) {
                    tagListViewR.setActualText(actualText.substring(0, actualText.length() - 1));
                }
            }
        });
        tagListViewR.getTfSearch().setOnKeyTyped(event -> {
            event.consume();

            String actualText = tagListViewR.getActualText();
            TextField tfSearch = tagListViewR.getTfSearch();

            if (actualText.isEmpty()) {
                tfSearch.clear();
            } else if (actualText.length() < 3) {
                tfSearch.setText(actualText);
                tfSearch.positionCaret(tfSearch.getLength());
            } else if (actualText.length() >= 3) {
                ArrayList<TagObject> matches = new ArrayList();
                for (TagObject tagObject : mainInfoList) {
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
        tagListViewR.getTfSearch().setOnAction(event -> {
            if (!select.isEmpty()) {
                TextField tfSearch = tagListViewR.getTfSearch();
                TagObject tagObject = mainInfoList.getTagObject(tfSearch.getText());
                if (tagObject != null) {
                    tagListViewR.addTagObjectToSelection(tagObject);
                    tagListViewR.setActualText("");
                    tfSearch.clear();
                    reload.notifyChangeIn(Reload.Control.INFO);
                    reload.doReload();
                }
            }
        });
    }

    private void onAction_menuSelectAll() {
        tagListViewR.getNodeSelectAll().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.addAll(filter);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuClearSelection() {
        tagListViewR.getNodeSelectNone().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.clear();
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuMergeSelection() {
        tagListViewR.getNodeSelectMerge().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.merge();
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }

    private void hideLeftClickMenus() {
        LeftClickMenu.getInstanceList().forEach(leftClickMenu -> {
            if (leftClickMenu.isShowing()) {
                leftClickMenu.hide();
            }
        });
    }
}
