package user_interface.scene;

import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.singleton.center.BaseTileEvent;

public class MainStageEvent {
    public MainStageEvent() {
        onKeyPress();
    }
    private void onKeyPress() {
        InstanceManager.getMainStage().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    InstanceManager.getToolbarPane().requestFocus();
                    break;
                case Q:
                    InstanceManager.getSelect().swapState(InstanceManager.getTarget().getCurrentTarget());
                    InstanceManager.getReload().doReload();
                    break;
                case E:
                    BaseTileEvent.onGroupButtonClick(InstanceManager.getTarget().getCurrentTarget());
                    InstanceManager.getReload().doReload();
                    break;
                case R:
                    InstanceManager.getSelect().setRandom();
                    InstanceManager.getReload().doReload();
                    break;
                case F:
                    CommonUtil.swapViewMode();
                    InstanceManager.getReload().doReload();
                    break;
                case W:
                case A:
                case S:
                case D:
                    InstanceManager.getTarget().move(event.getCode());
                    InstanceManager.getReload().doReload();
                    break;
                default:
                    break;
            }
        });
    }
}
