package project.gui.event.leftpane;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import project.MainUtils;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.leftpane.ColoredText;

public class ColoredTextEvent implements MainUtils {
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
        TagObject tagObject = tagControl.getTagObject(sourceCell);
        ColoredText coloredText;
        try {
            coloredText = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        // if sourceCell is group level
        if (tagObject == null) {
            String groupName = coloredText.getText();
            if (filterControl.isGroupWhitelisted(groupName)) {
                filterControl.blacklistGroup(groupName);
                coloredText.setColor(Color.RED);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.RED);
                }
            } else if (filterControl.isGroupBlacklisted(groupName)) {
                filterControl.unlistGroup(groupName);
                coloredText.setColor(Color.BLACK);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.BLACK);
                }
            } else {
                filterControl.whitelistGroup(groupName);
                coloredText.setColor(Color.GREEN);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.GREEN);
                }
            }
        } else {
            if (filterControl.isTagObjectWhitelisted(tagObject)) {
                filterControl.blacklistTagObject(tagObject);
                coloredText.setColor(Color.RED);
            } else if (filterControl.isTagObjectBlacklisted(tagObject)) {
                filterControl.unlistTagObject(tagObject);
                coloredText.setColor(Color.BLACK);
            } else {
                filterControl.whitelistTagObject(tagObject);
                coloredText.setColor(Color.GREEN);
            }
        }
        filterControl.applyFilter();
        reloadControl.reload(true, GUINode.GALLERYPANE);
        leftPane.refreshTreeView();
    }
}
