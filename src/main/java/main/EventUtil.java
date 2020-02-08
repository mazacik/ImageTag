package main;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class EventUtil {
	public static MouseEvent createEventMouseClick(KeyEvent keyEvent) {
		return new MouseEvent(
				MouseEvent.MOUSE_CLICKED,
				0,
				0,
				0,
				0,
				MouseButton.PRIMARY,
				1,
				keyEvent.isShiftDown(),
				keyEvent.isControlDown(),
				keyEvent.isAltDown(),
				keyEvent.isMetaDown(),
				false,
				false,
				false,
				true,
				false,
				true,
				null
		);
	}
}
