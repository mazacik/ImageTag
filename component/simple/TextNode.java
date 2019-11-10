package application.component.simple;

import application.decorator.ColorUtil;
import application.decorator.Decorator;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.text.TextAlignment;

public class TextNode extends Label {
	public TextNode() {
		this("");
	}
	public TextNode(String text) {
		this(text, false, false, false);
	}
	public TextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor) {
		this(text, hoverBackground, hoverText, hoverCursor, false);
	}
	public TextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding) {
		super(text);
		this.setAlignment(Pos.CENTER);
		this.setFont(Decorator.getFont());
		this.setBackground(Background.EMPTY);
		this.setTextAlignment(TextAlignment.CENTER);
		this.setTextFill(ColorUtil.getTextColorDef());
		this.minWidthProperty().bind(this.heightProperty());
		
		if (defaultPadding) {
			this.setPadding(new Insets(5, 10, 5, 10));
		}
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (hoverBackground && hoverText) {
				this.setBackground(ColorUtil.getBackgroundAlt());
				this.setTextFill(ColorUtil.getTextColorAlt());
			} else if (hoverBackground) {
				this.setBackground(ColorUtil.getBackgroundAlt());
			} else if (hoverText) {
				this.setTextFill(ColorUtil.getTextColorAlt());
			}
			
			if (hoverCursor) {
				this.setCursor(Cursor.HAND);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (hoverBackground) {
				this.setBackground(Background.EMPTY);
			}
			if (hoverText) {
				this.setTextFill(ColorUtil.getTextColorDef());
			}
			if (hoverCursor) {
				this.setCursor(Cursor.DEFAULT);
			}
		});
	}
	
	public <T extends Event> void addMouseEvent(final EventType<T> eventType, MouseButton mouseButton, Runnable runnable) {
		this.addEventFilter(eventType, event -> {
			if (event instanceof MouseEvent) {
				MouseEvent mouseEvent = (MouseEvent) event;
				if (mouseEvent.getButton() == mouseButton) {
					runnable.run();
				}
			}
		});
	}
}
