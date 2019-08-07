package application.gui.scene;

import application.controller.Reload;
import application.controller.Select;
import application.controller.Target;
import application.gui.panes.center.BaseTileEvent;
import application.main.Instances;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyEvent;

public class MainStageEvent {
	private SimpleBooleanProperty shiftDown = new SimpleBooleanProperty(false);
	
	public MainStageEvent() {
		onKeyPress();
		onKeyRelease();
	}
	private void onKeyPress() {
		Instances.getMainStage().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (Instances.getSelectPane().getTfSearch().isFocused()) return;
			
			Select select = Instances.getSelect();
			Reload reload = Instances.getReload();
			Target target = Instances.getTarget();
			
			switch (event.getCode()) {
				case ESCAPE:
					if (SceneUtil.isFullView()) {
						SceneUtil.swapViewMode();
					}
					break;
				case E:
					BaseTileEvent.onGroupButtonPress(target.getCurrentTarget());
					reload.doReload();
					Instances.getGalleryPane().loadCacheOfTilesInViewport();
					break;
				case R:
					select.setRandom();
					reload.doReload();
					break;
				case F:
					SceneUtil.swapViewMode();
					reload.doReload();
					break;
				case SHIFT:
					shiftDown.setValue(true);
					Instances.getSelect().setShiftStart(target.getCurrentTarget());
					break;
				case W:
				case A:
				case S:
				case D:
					target.move(event.getCode());
					
					if (event.isShiftDown()) select.shiftSelectTo(target.getCurrentTarget());
					else if (event.isControlDown()) select.add(target.getCurrentTarget());
					else select.set(target.getCurrentTarget());
					
					Instances.getReload().doReload();
					break;
				default:
					break;
			}
		});
	}
	private void onKeyRelease() {
		Instances.getMainStage().getScene().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch (event.getCode()) {
				case SHIFT:
					shiftDown.setValue(false);
					break;
				default:
					break;
			}
		});
	}
	
	public boolean isShiftDown() {
		return shiftDown.get();
	}
	public SimpleBooleanProperty shiftDownProperty() {
		return shiftDown;
	}
}
