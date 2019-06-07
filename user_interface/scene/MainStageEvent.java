package user_interface.scene;

import control.Filter;
import control.Reload;
import control.Select;
import control.Target;
import javafx.scene.input.KeyEvent;
import lifecycle.InstanceManager;
import user_interface.singleton.center.BaseTileEvent;
import utils.CommonUtil;

public class MainStageEvent {
    public MainStageEvent() {
        onKeyPress();
    }
    private void onKeyPress() {
        InstanceManager.getMainStage().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Filter filter = InstanceManager.getFilter();
            Select select = InstanceManager.getSelect();
            Reload reload = InstanceManager.getReload();
            Target target = InstanceManager.getTarget();


            switch (event.getCode()) {
                case ESCAPE:
                    if (MainScene.isFullView()) {
                        MainScene.swapViewMode();
                    } else if (InstanceManager.getSelectPane().getTfSearch().isFocused()) {
                        InstanceManager.getToolbarPane().requestFocus();
                    }

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
                case SHIFT:
                    InstanceManager.getSelect().setShiftStart(target.getCurrentTarget());
                    break;
                case W:
                case A:
                case S:
                case D:
                    target.move(event.getCode());

                    if (event.isShiftDown()) select.shiftSelectTo(target.getCurrentTarget());
                    else select.set(target.getCurrentTarget());

                    InstanceManager.getReload().doReload();
                    break;
                default:
                    break;
            }
        });
    }
}
