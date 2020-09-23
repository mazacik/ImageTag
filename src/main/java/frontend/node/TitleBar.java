package frontend.node;

import frontend.decorator.DecoratorUtil;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TitleBar extends BorderPane {
	private TextNode nodeTitle;
	private final TextNode nodeMinimize;
	private final TextNode nodeExit;
	
	public TitleBar() {
		this(null, "");
	}
	public TitleBar(String title) {
		this(null, title);
	}
	public TitleBar(Stage owner) {
		this(owner, "");
	}
	public TitleBar(Stage owner, String title) {
		if (owner != null) {
			owner.titleProperty().addListener((observable, oldValue, newValue) -> nodeTitle.setText(newValue));
		}
		
		nodeTitle = new TextNode(title, false, false, false, true);
		BorderPane.setAlignment(nodeTitle, Pos.CENTER_LEFT);
		
		nodeMinimize = new TextNode("⚊", true, true, false, true);
		nodeMinimize.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				nodeMinimize.setBackground(Background.EMPTY);
				nodeMinimize.setTextFill(DecoratorUtil.getColorPrimary());
				nodeMinimize.applyCss();
				((Stage) this.getScene().getWindow()).setIconified(true);
			}
		});
		BorderPane.setAlignment(nodeMinimize, Pos.CENTER);
		
		nodeExit = new TextNode("✕", true, true, false, true);
		nodeExit.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
		BorderPane.setAlignment(nodeExit, Pos.CENTER);
		
		this.setLeft(nodeTitle);
		this.setRight(new HBox(nodeMinimize, nodeExit));
		this.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		
		initDragEvents();
	}
	
	private void initDragEvents() {
		AtomicBoolean clickOnButton = new AtomicBoolean(false);
		AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
		AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
		
		this.setOnMousePressed(event -> {
			Parent parent = event.getPickResult().getIntersectedNode().getParent();
			clickOnButton.set(parent == nodeExit || parent == nodeMinimize);
			xOffset.set(this.getScene().getWindow().getX() - event.getScreenX());
			yOffset.set(this.getScene().getWindow().getY() - event.getScreenY());
		});
		this.setOnMouseDragged(event -> {
			if (!clickOnButton.get()) {
				this.getScene().getWindow().setX(event.getScreenX() + xOffset.get());
				this.getScene().getWindow().setY(event.getScreenY() + yOffset.get());
			}
		});
		this.setOnMouseReleased(event -> {
			if (event.getScreenY() == 0 || this.localToScreen(this.getBoundsInLocal()).getMinY() < 0) {
				this.getScene().getWindow().centerOnScreen();
			}
			clickOnButton.set(false);
		});
	}
	
	public void setTitle(String title) {
		nodeTitle.setText(title);
	}
}
