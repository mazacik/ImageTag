package main;

import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class EventUtil {
	public static MouseEvent createMouseEvent(EventType<MouseEvent> type) {
		return new MouseEvent(
				type,
				0,
				0,
				0,
				0,
				MouseButton.PRIMARY,
				1,
				false,
				false,
				false,
				false,
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
