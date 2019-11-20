package gui.stage;

import gui.component.simple.TextNode;
import gui.decorator.ColorUtil;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import gui.component.simple.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tools.NodeUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TitleBar extends BorderPane {
	private TextNode lblTitle;
	private TextNode btnMinimize;
	private TextNode btnExit;
	
	public TitleBar() {
	
	}
	public TitleBar(String title) {
		init(title);
	}
	
	public void init(String title) {
		lblTitle = new TextNode("", false, false, false, true);
		lblTitle.setText(title);
		BorderPane.setAlignment(lblTitle, Pos.CENTER_LEFT);
		
		btnMinimize = new TextNode("⚊", true, true, false, true);
		btnMinimize.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				btnMinimize.setBackground(Background.EMPTY);
				btnMinimize.setTextFill(ColorUtil.getColorPrimary());
				btnMinimize.applyCss();
				((Stage) this.getScene().getWindow()).setIconified(true);
			}
		});
		BorderPane.setAlignment(btnMinimize, Pos.CENTER);
		
		btnExit = new TextNode("✕", true, true, false, true);
		btnExit.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
		BorderPane.setAlignment(btnExit, Pos.CENTER);
		
		this.setLeft(lblTitle);
		this.setRight(new HBox(btnMinimize, btnExit));
		this.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		
		initDragEvents();
	}
	
	private void initDragEvents() {
		AtomicBoolean clickOnButton = new AtomicBoolean(false);
		AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
		AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
		
		this.setOnMousePressed(event -> {
			Parent parent = event.getPickResult().getIntersectedNode().getParent();
			clickOnButton.set(parent == btnExit || parent == btnMinimize);
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
		lblTitle.setText(title);
	}
}
