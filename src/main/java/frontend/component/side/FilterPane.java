package frontend.component.side;

import backend.reload.Notifier;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import frontend.stage.settings.SettingsStage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.Main;

public class FilterPane extends SidePaneBase {
	private boolean bHidden = false;
	
	private final TextNode nodeText;
	
	public FilterPane() {
		nodeText = new TextNode("", false, false, false, true);
	}
	
	public void initialize() {
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Main.FILTER.getFilterListManager().clear();
			Reload.notify(Notifier.FILTER_CHANGED);
			Reload.start();
		});
		
		nodeText.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(nodeText, Priority.ALWAYS);
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> new SettingsStage().showAndWait());
		
		HBox boxButtons = new HBox();
		boxButtons.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		
		boxButtons.getChildren().add(btnReset);
		boxButtons.getChildren().add(TextNodeTemplates.TAG_CREATE.get());
		boxButtons.getChildren().add(nodeText);
		boxButtons.getChildren().add(btnSettings);
		
		String cHide = "←";
		String cShow = "→";
		TextNode btnHide = new TextNode(cHide, true, true, false, true);
		btnHide.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			bHidden = !bHidden;
			if (bHidden) {
				btnHide.setText(cShow);
				this.getChildren().setAll(btnHide);
				this.setMinWidth(btnHide.getWidth() + 1);
				this.setMaxWidth(btnHide.getWidth() + 1);
				UserInterface.getStage().getMainScene().handleWidthChange(btnHide.getWidth() + 1, UserInterface.getSelectPane().getWidth());
			} else {
				btnHide.setText(cHide);
				this.getChildren().setAll(boxButtons, listBox);
				boxButtons.getChildren().add(btnHide);
				this.setMinWidth(WIDTH);
				this.setMaxWidth(WIDTH);
				UserInterface.getStage().getMainScene().handleWidthChange(WIDTH, UserInterface.getSelectPane().getWidth());
			}
		});
		
		boxButtons.getChildren().add(btnHide);
		
		this.setBorder(DecoratorUtil.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(boxButtons, listBox);
	}
	
	public boolean refresh() {
		nodeText.setText("Filter: " + Main.FILTER.size());
		
		return true;
	}
	
	public boolean isHidden() {
		return bHidden;
	}
}
