package ui.node.textnodewitharrow;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import ui.decorator.Decorator;
import ui.node.textnode.TextNode;

public class TextNodeWithArrow extends BorderPane {
	private static final int PADDING_V = 3;
	private static final int PADDING_H = 10;
	private static final Insets insets = new Insets(PADDING_V, PADDING_H, PADDING_V, PADDING_H);
	
	private Label nodeMain;
	private Label nodeArrow;
	
	public TextNodeWithArrow(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding) {
		nodeMain = new TextNode(text, false, false, false, false);
		nodeArrow = new TextNode("▶", false, false, false, false);
		
		nodeMain.setAlignment(Pos.CENTER_LEFT);
		nodeArrow.setAlignment(Pos.CENTER_RIGHT);
		nodeMain.minWidthProperty().unbind();
		nodeArrow.minWidthProperty().unbind();
		
		if (defaultPadding) this.setPadding(insets);
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (hoverBackground) {
				this.setBackground(Decorator.getBackgroundSecondary());
			}
			if (hoverText) {
				nodeMain.setTextFill(Decorator.getColorSecondary());
				nodeArrow.setTextFill(Decorator.getColorSecondary());
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
				nodeMain.setTextFill(Decorator.getColorPrimary());
				nodeArrow.setTextFill(Decorator.getColorPrimary());
			}
			if (hoverCursor) {
				this.setCursor(Cursor.DEFAULT);
			}
		});
		
		this.setCenter(nodeMain);
		this.setRight(nodeArrow);
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
