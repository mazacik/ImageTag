package frontend.component.top;

import backend.control.reload.Reload;
import backend.list.entity.Entity;
import backend.misc.Direction;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.menu.ClickMenu;
import frontend.node.menu.ListMenu;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Main;

public class ToolbarPane extends BorderPane {
	private final TextNode nodeTarget;
	
	public ToolbarPane() {
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		ClickMenu.install(nodeFile, Direction.DOWN, MouseButton.PRIMARY,
		                  TextNodeTemplates.APPLICATION_SAVE.get(),
		                  TextNodeTemplates.APPLICATION_IMPORT.get(),
		                  TextNodeTemplates.APPLICATION_REFRESH.get(),
		                  TextNodeTemplates.APPLICATION_SETTINGS.get(),
		                  TextNodeTemplates.CACHE_RESET.get(),
		                  new SeparatorNode(),
		                  TextNodeTemplates.APPLICATION_SAVE_AND_CLOSE.get(),
		                  TextNodeTemplates.APPLICATION_EXIT.get()
		);
		
		TextNode nodeEdit = new TextNode("Edit", true, true, false, true);
		ClickMenu.install(nodeEdit, Direction.DOWN, MouseButton.PRIMARY,
		                  TextNodeTemplates.SELECT_ALL.get(),
		                  TextNodeTemplates.SELECT_NONE.get()
		);
		
		TextNode nodeFilter = new TextNode("Filter", true, true, false, true);
		ClickMenu.install(nodeFilter, Direction.DOWN, MouseButton.PRIMARY,
		                  TextNodeTemplates.FILTER_CREATE_PRESET.get(),
		                  TextNodeTemplates.FILTER_SIMILAR.get(),
		                  TextNodeTemplates.FILTER_COLLAGE.get()
		);
		
		TextNode nodeRandom = new TextNode("Random", true, true, false, true);
		nodeRandom.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Main.SELECT.setRandom(true);
			Reload.start();
		});
		
		TextNode nodeSlideshow = new TextNode("Slideshow", true, true, false, true);
		nodeSlideshow.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (Main.SELECT.isSlideshowRunning()) {
				Main.SELECT.slideshowStop();
			} else {
				Main.SELECT.slideshowStart();
				Reload.start();
			}
		});
		
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY, ListMenu.Preset.ENTITY);
		
		HBox hBox = new HBox(nodeFile, nodeEdit, nodeRandom, nodeSlideshow);
		hBox.setAlignment(Pos.CENTER);
		
		this.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		this.setLeft(hBox);
		this.setCenter(nodeTarget);
	}
	
	public boolean reload() {
		Entity target = Main.SELECT.getTarget();
		if (target != null) {
			if (!nodeTarget.isVisible()) {
				nodeTarget.setVisible(true);
			}
			
			if (target.hasGroup()) {
				String s = (target.getGroup().indexOf(target) + 1) + "/" + target.getGroup().size();
				nodeTarget.setText("[" + s + "] " + target.getName());
			} else {
				nodeTarget.setText(target.getName());
			}
		} else {
			nodeTarget.setVisible(false);
		}
		
		return true;
	}
}
