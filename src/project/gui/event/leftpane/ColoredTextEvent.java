package project.gui.event.leftpane;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import project.control.MainControl;
import project.control.TagControl;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.leftpane.ColoredText;
import project.gui.component.leftpane.LeftPane;

public abstract class ColoredTextEvent {
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
            if (MainControl.getFilterControl().isGroupWhitelisted(groupName)) {
                MainControl.getFilterControl().blacklistGroup(groupName);
                coloredText.setColor(Color.RED);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.RED);
                }
            } else if (MainControl.getFilterControl().isGroupBlacklisted(groupName)) {
                MainControl.getFilterControl().unlistGroup(groupName);
                coloredText.setColor(Color.BLACK);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.BLACK);
                }
            } else {
                MainControl.getFilterControl().whitelistGroup(groupName);
                coloredText.setColor(Color.GREEN);
                for (TreeItem<ColoredText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.GREEN);
                }
            }
        } else {
            if (MainControl.getFilterControl().isTagObjectWhitelisted(tagObject)) {
                MainControl.getFilterControl().blacklistTagObject(tagObject);
                coloredText.setColor(Color.RED);
            } else if (MainControl.getFilterControl().isTagObjectBlacklisted(tagObject)) {
                MainControl.getFilterControl().unlistTagObject(tagObject);
                coloredText.setColor(Color.BLACK);
            } else {
                MainControl.getFilterControl().whitelistTagObject(tagObject);
                coloredText.setColor(Color.GREEN);
            }
        }
        MainControl.getFilterControl().applyFilter();
        MainControl.getReloadControl().reload(true, GUINode.GALLERYPANE);
        LeftPane.refreshTreeView();
    }
}
