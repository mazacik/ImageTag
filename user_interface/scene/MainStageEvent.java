package user_interface.scene;

import system.CommonUtil;
import system.Instances;
import user_interface.singleton.center.BaseTileEvent;

public class MainStageEvent implements Instances {
    public MainStageEvent() {
        onKeyPress();
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
                    CommonUtil.swapViewMode();
                    reload.doReload();
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
