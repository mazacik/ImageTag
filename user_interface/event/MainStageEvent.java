package user_interface.event;

import system.CommonUtil;
import system.InstanceRepo;

public class MainStageEvent implements InstanceRepo {
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
