package user_interface.singleton.side;

import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import system.InstanceRepo;
import user_interface.factory.node.popup.InfoObjectRCM;

public class InfoListViewREvent implements InstanceRepo {
    private boolean isExpanded = false;

    public InfoListViewREvent() {
        onAction_btnExpCol();
    }

    public static void onMouseClick(TreeCell<InfoListCell> sourceCell) {
        sourceCell.setOnMouseClicked(event -> {
            if (event.getX() > 25) {
                switch (event.getButton()) {
                    case PRIMARY:
                        onLeftClick(sourceCell);
                        break;
                    case SECONDARY:
                        onRightClick(sourceCell, event);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private static void onLeftClick(TreeCell<InfoListCell> sourceCell) {
        mainStage.getInfoObjectRCM().hide();
        infoListViewR.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<InfoListCell> sourceCell, MouseEvent event) {
        InfoObjectRCM infoObjectRCM = mainStage.getInfoObjectRCM();
        infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(sourceCell));
        infoObjectRCM.show(infoListViewR, event.getScreenX(), event.getScreenY());
    }

    private void onAction_btnExpCol() {
        infoListViewR.getBtnExpCol().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isExpanded = !isExpanded;
                if (isExpanded) infoListViewR.getBtnExpCol().setText("Collapse");
                else infoListViewR.getBtnExpCol().setText("Expand");
                infoListViewR.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(isExpanded));
            }
        });
    }
}
