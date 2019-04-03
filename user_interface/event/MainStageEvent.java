package user_interface.event;

import system.CommonUtil;
import system.InstanceRepo;

public class MainStageEvent implements InstanceRepo {
    public MainStageEvent() {
        onShowing();
        onShown();
        onClose();
        onKeyPress();
    }

    private void onClose() {
        mainStage.setOnCloseRequest(event -> {
            settings.writeToDisk();
            mainDataList.writeToDisk();
            logger.debug(this, "application exit");
            System.exit(0);
        });
    }
    private void onShown() {
        mainStage.setOnShown(event -> {
            infoListViewL.onShown();
            infoListViewR.onShown();
            tileView.onShown();

            target.set(mainDataList.get(0));

            CommonUtil.updateNodeProperties();
            mainStage.setWidth(CommonUtil.getUsableScreenWidth());
            mainStage.setHeight(CommonUtil.getUsableScreenHeight());
            mainStage.centerOnScreen();

            logger.debug(this, "user interface onShown end");
        });
    }
    private void onShowing() {
        mainStage.setOnShowing(event -> reload.doReload());
    }
    private void onKeyPress() {
        mainStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    topMenu.requestFocus();
                    break;
                case Q:
                    select.swapState(target.getCurrentTarget());
                    reload.doReload();
                    break;
                case E:
                    BaseTileEvent.onGroupButtonClick(target.getCurrentTarget());
                    reload.doReload();
                    break;
                case R:
                    select.setRandom();
                    reload.doReload();
                    break;
                case F:
                    CommonUtil.swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    target.move(event.getCode());
                    reload.doReload();
                    break;
                default:
                    break;
            }
        });
    }
}
