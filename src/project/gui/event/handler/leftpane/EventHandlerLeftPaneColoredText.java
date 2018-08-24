package project.gui.event.handler.leftpane;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.TagControl;
import project.database.element.TagObject;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.ColoredText;
import project.gui.component.leftpane.LeftPane;

//todo refactor
public abstract class EventHandlerLeftPaneColoredText {
    public static void onLeftClick(TreeCell<ColoredText> sourceCell) {
        TagObject tagObject = TagControl.getTagObject(sourceCell);
        ColoredText coloredText;
        try {
            coloredText = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        // if sourceCell is group level
        if (tagObject == null) {
            String groupName = coloredText.getText();
            if (FilterControl.isGroupWhitelisted(groupName)) {
                FilterControl.blacklistGroup(groupName);
                coloredText.setColor(Color.RED);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.RED);
                }
            } else if (FilterControl.isGroupBlacklisted(groupName)) {
                FilterControl.unlistGroup(groupName);
                coloredText.setColor(Color.BLACK);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.BLACK);
                }
            } else {
                FilterControl.whitelistGroup(groupName);
                coloredText.setColor(Color.GREEN);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.GREEN);
                }
            }
        } else {
            if (FilterControl.isTagElementWhitelisted(tagObject)) {
                FilterControl.blacklistTagElement(tagObject);
                coloredText.setColor(Color.RED);
            } else if (FilterControl.isTagElementBlacklisted(tagObject)) {
                FilterControl.removeTagObject(tagObject);
                coloredText.setColor(Color.BLACK);
            } else {
                FilterControl.whitelistTagElement(tagObject);
                coloredText.setColor(Color.GREEN);
            }
        }
        FilterControl.refresh();
        ReloadControl.request(true, GalleryPane.class);
        LeftPane.refreshTreeview();
    }
}
