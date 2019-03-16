package user_interface.singleton.side;

import control.reload.Reload;
import database.object.InfoObject;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import system.InstanceRepo;
import user_interface.factory.node.popup.InfoObjectRCM;
import user_interface.factory.stage.InfoObjectEditStage;

public class InfoListViewLEvent implements InstanceRepo {
    private boolean isExpanded = false;

    public InfoListViewLEvent() {
        onAction_btnExpCol();
        onAction_btnNew();
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
        infoListViewL.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<InfoListCell> sourceCell, MouseEvent event) {
        InfoObjectRCM infoObjectRCM = mainStage.getInfoObjectRCM();
        infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(sourceCell));
        infoObjectRCM.show(infoListViewL, event.getScreenX(), event.getScreenY());
    }

    private void onAction_btnExpCol() {
        infoListViewL.getBtnExpCol().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isExpanded = !isExpanded;
                if (isExpanded) infoListViewL.getBtnExpCol().setText("Collapse");
                else infoListViewL.getBtnExpCol().setText("Expand");
                infoListViewL.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(isExpanded));
            }
        });
    }
    private void onAction_btnNew() {
        infoListViewL.getBtnNew().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InfoObject newInfoObject = new InfoObjectEditStage().getResult();
                mainInfoList.add(newInfoObject);
                reload.notifyChangeIn(Reload.Control.INFO);
                reload.doReload();
            }
        });
    }
}
