package application.gui.panes.side;

import application.controller.Reload;
import application.database.object.TagObject;
import application.main.Instances;
import javafx.event.Event;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

public class SelectPaneEvent {
	public SelectPaneEvent() {
		onMouseClick();
		
		tfSearchEvents();
	}
	
	private void onMouseClick() {
		Instances.getSelectPane().setOnMouseClicked(event -> Instances.getSelectPane().requestFocus());
	}
	
	private void tfSearchEvents() {
		SelectPane selectPane = Instances.getSelectPane();
		TextField tfSearch = selectPane.getTfSearch();
		tfSearch.addEventFilter(KeyEvent.KEY_TYPED, Event::consume);
		tfSearch.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			/* first update the actualText variable */
			String actualText = selectPane.getActualText();
			if (event.getCode().isLetterKey()) {
				selectPane.setActualText(actualText + event.getText().toLowerCase());
			} else if (event.getCode() == KeyCode.BACK_SPACE) {
				//todo only delete selected text (if there is any)
				if (!actualText.isEmpty()) {
					selectPane.setActualText(actualText.substring(0, actualText.length() - 1));
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				Instances.getGalleryPane().requestFocus();
			}
			
			/* second update the TextField text */
			actualText = Instances.getSelectPane().getActualText();
			if (actualText.isEmpty()) {
				tfSearch.clear();
			} else if (actualText.length() < 3) {
				tfSearch.setText(actualText);
				tfSearch.positionCaret(tfSearch.getLength());
			} else if (actualText.length() >= 3) {
				ArrayList<TagObject> matches = new ArrayList<>();
				for (TagObject tagObject : Instances.getTagListMain()) {
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
		Instances.getSelectPane().getTfSearch().setOnAction(event -> {
			if (!Instances.getSelect().isEmpty()) {
				TagObject tagObject = Instances.getTagListMain().getTagObject(tfSearch.getText());
				if (tagObject != null) {
					selectPane.addTagObjectToSelection(tagObject);
					selectPane.setActualText("");
					tfSearch.clear();
					Instances.getReload().notify(Reload.Control.TAG);
					Instances.getReload().doReload();
				}
			}
		});
	}
}
