package frontend.node.textnode;

import frontend.decorator.DecoratorUtil;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

public class ArrowTextNode extends BorderPane {
	private static final int PADDING_V = 3;
	private static final int PADDING_H = 10;
	private static final Insets insets = new Insets(PADDING_V, PADDING_H, PADDING_V, PADDING_H);
	
	private final ArrowTextNodeTemplates template;
	
	private final Label nodeMain;
	private final Label nodeArrow;
	
	public ArrowTextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding) {
		this(text, hoverBackground, hoverText, hoverCursor, defaultPadding, null);
	}
	public ArrowTextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding, ArrowTextNodeTemplates template) {
		this.template = template;
		
		nodeMain = new TextNode(text, false, false, false, false);
		nodeArrow = new TextNode("▶", false, false, false, false);
		
		nodeMain.minWidthProperty().unbind();
		nodeArrow.minWidthProperty().unbind();
		
		if (defaultPadding) this.setPadding(insets);
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (hoverBackground) {
				this.setBackground(DecoratorUtil.getBackgroundDefaultDark());
			}
			if (hoverText) {
				nodeMain.setTextFill(DecoratorUtil.getColorSecondary());
				nodeArrow.setTextFill(DecoratorUtil.getColorSecondary());
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
				nodeMain.setTextFill(DecoratorUtil.getColorPrimary());
				nodeArrow.setTextFill(DecoratorUtil.getColorPrimary());
			}
			if (hoverCursor) {
				this.setCursor(Cursor.DEFAULT);
			}
		});
		
		this.setLeft(nodeMain);
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
	
	public ArrowTextNodeTemplates getTemplate() {
		return template;
	}
}
