package frontend.node.textnode;

import frontend.decorator.DecoratorUtil;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;

public class TextNode extends Label {
	private static final int PADDING_V = 3;
	private static final int PADDING_H = 10;
	private static final Insets insets = new Insets(PADDING_V, PADDING_H, PADDING_V, PADDING_H);
	
	private final TextNodeTemplates template;
	
	public TextNode() {
		this("");
	}
	public TextNode(String text) {
		this(text, false, false, false, false);
	}
	public TextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding) {
		this(text, hoverBackground, hoverText, hoverCursor, defaultPadding, null);
	}
	public TextNode(String text, boolean hoverBackground, boolean hoverText, boolean hoverCursor, boolean defaultPadding, TextNodeTemplates template) {
		super(text);
		
		this.template = template;
		
		this.setAlignment(Pos.CENTER);
		this.setFont(DecoratorUtil.getFont());
		this.setBackground(Background.EMPTY);
		this.setTextFill(DecoratorUtil.getColorPrimary());
		
		DecoratorUtil.getNodeList().add(this);
		
		if (defaultPadding) this.setPadding(insets);
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (hoverBackground) {
				this.setBackground(DecoratorUtil.getBackgroundDefaultDark());
			}
			if (hoverText) {
				this.setTextFill(DecoratorUtil.getColorSecondary());
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
				this.setTextFill(DecoratorUtil.getColorPrimary());
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
	
	public TextNodeTemplates getTemplate() {
		return template;
	}
}
