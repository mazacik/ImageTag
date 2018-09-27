package project.gui.event.leftpane;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.leftpane.ColoredText;
import project.utils.MainUtil;

public class ColoredTextEvent implements MainUtil {
    public static void onMouseClick(TreeCell<ColoredText> source) {
        source.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    onLeftClick(source);
                    break;
                default:
                    break;
            }
        });
    }

    private static void onLeftClick(TreeCell<ColoredText> sourceCell) {
        TagObject tagObject = mainTags.getTagObject(sourceCell);
        ColoredText coloredText;
        try {
            coloredText = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        // if sourceCell is group level
        if (tagObject == null) {
            String groupName = coloredText.getText();
            if (filter.isGroupWhitelisted(groupName)) {
                filter.blacklistGroup(groupName);
                coloredText.setColor(Color.RED);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.RED);
                }
            } else if (filter.isGroupBlacklisted(groupName)) {
                filter.unlistGroup(groupName);
                coloredText.setColor(Color.BLACK);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.BLACK);
                }
            } else {
                filter.whitelistGroup(groupName);
                coloredText.setColor(Color.GREEN);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.GREEN);
                }
            }
        } else {
            if (filter.isTagObjectWhitelisted(tagObject)) {
                filter.blacklistTagObject(tagObject);
                coloredText.setColor(Color.RED);
            } else if (filter.isTagObjectBlacklisted(tagObject)) {
                filter.unlistTagObject(tagObject);
                coloredText.setColor(Color.BLACK);
            } else {
                filter.whitelistTagObject(tagObject);
                coloredText.setColor(Color.GREEN);
            }
        }
        filter.apply();
        reload.queue(true, GUINode.GALLERYPANE);
        leftPane.refreshTreeView();
    }
}
