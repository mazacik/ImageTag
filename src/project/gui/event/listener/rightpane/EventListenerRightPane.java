package project.gui.event.listener.rightpane;

import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public abstract class EventListenerRightPane {
    private static String cbNameText;
    private static String cbGroupText;

    public static void initialize() {
        EventListenerRightPane.onKeyPress();
        EventListenerButtonAdd.onAction();
        EventListenerButtonNew.onAction();
        EventListenerRightPane.onChoiceBoxValueChange();
        EventListenerRightPane.onContextMenuAction();
    }

    private static void onKeyPress() {
        //RightPane.getListView().setOnKeyPressed(event -> {
        RightPane.getInstance().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    TopPane.getInstance().requestFocus();
                    break;
                case DELETE:
                    RightPane.getContextMenu().getMenuRemove().fire();
                    break;
                default:
                    break;
            }
        });
    }
    private static void onChoiceBoxValueChange() {
        EventListenerGroupNode.initialize();
        EventListenerNameNode.initialize();
    }
    private static void onContextMenuAction() {
        EventListenerRightPaneContextMenuEdit.initialize();
        EventListenerRightPaneContextMenuRemove.initialize();
    }

    public static String getNameText() {
        return cbNameText;
    }
    public static String getGroupText() {
        return cbGroupText;
    }

    public static void setNameText(String cbNameText) {
        EventListenerRightPane.cbNameText = cbNameText;
    }
    public static void setGroupText(String cbGroupText) {
        EventListenerRightPane.cbGroupText = cbGroupText;
    }
}
