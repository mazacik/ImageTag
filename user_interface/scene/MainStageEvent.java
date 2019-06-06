package user_interface.scene;

import database.object.DataObject;
import lifecycle.InstanceManager;
import utils.CommonUtil;
import user_interface.singleton.center.BaseTileEvent;

public class MainStageEvent {
    public MainStageEvent() {
        onKeyPress();
    }
    private void onKeyPress() {
        InstanceManager.getMainStage().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (MainScene.isFullView()) {
                        MainScene.swapViewMode();
                    } else if (InstanceManager.getSelectPane().getTfSearch().isFocused()){
                        InstanceManager.getToolbarPane().requestFocus();
                    }

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
                    DataObject dataObject = InstanceManager.getTarget().getCurrentTarget();

                    if (event.isShiftDown()) {
                        InstanceManager.getSelect().add(dataObject);
                    } else {
                        InstanceManager.getSelect().set(dataObject);
                    }

                    InstanceManager.getReload().doReload();
                    break;
                default:
                    break;
            }
        });
    }
}
