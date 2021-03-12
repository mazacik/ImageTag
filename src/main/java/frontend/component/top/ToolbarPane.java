package frontend.component.top;

import backend.entity.Entity;
import backend.misc.Direction;
import backend.reload.Reload;
import frontend.node.SeparatorNode;
import frontend.node.menu.ListMenu;
import frontend.node.menu.MenuPreset;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Main;

public class ToolbarPane extends BorderPane {
	private final TextNode btnLike;
	private final TextNode nodeTarget;
	
	public ToolbarPane() {
		btnLike = new TextNode("", true, true, false, true);
		nodeTarget = new TextNode("", true, true, false, true);
	}
	
	public void initialize() {
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		ListMenu.install(nodeFile, Direction.DOWN, ListMenu.MenuTrigger.CLICK_LEFT,
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
		ListMenu.install(nodeEdit, Direction.DOWN, ListMenu.MenuTrigger.CLICK_LEFT,
		                 TextNodeTemplates.SELECT_ALL.get(),
		                 TextNodeTemplates.SELECT_NONE.get()
		);
		
		TextNode nodeFilter = new TextNode("Filter", true, true, false, true);
		ListMenu.install(nodeFilter, Direction.DOWN, ListMenu.MenuTrigger.CLICK_LEFT,
		                 TextNodeTemplates.FILTER_CREATE_PRESET.get(),
		                 TextNodeTemplates.FILTER_SIMILAR.get(),
		                 TextNodeTemplates.FILTER_COLLAGE.get()
		);
		
		TextNode nodeRandom = new TextNode("Random", true, true, false, true);
		nodeRandom.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Main.SELECT.setRandom();
			Reload.start();
		});
		
		TextNode nodeSlideshow = new TextNode("Slideshow", true, true, false, true);
		nodeSlideshow.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (Main.SELECT.getSlideshow().isRunning()) {
				Main.SELECT.getSlideshow().stop();
			} else {
				Main.SELECT.getSlideshow().start();
			}
		});
		
		HBox boxCenter = new HBox(nodeFile, nodeEdit, nodeRandom, nodeFilter, nodeSlideshow);
		boxCenter.setAlignment(Pos.CENTER);
		
		btnLike.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Entity target = Main.SELECT.getTarget();
			
			target.setLikes(target.getLikes() + 1);
			btnLike.setText(Integer.toString(target.getLikes()));
			
			Reload.start();
		});
		btnLike.addMouseEvent(MouseEvent.MOUSE_ENTERED, MouseButton.NONE, () -> {
			btnLike.setText("+1");
		});
		btnLike.addMouseEvent(MouseEvent.MOUSE_EXITED, MouseButton.NONE, () -> {
			btnLike.setText(Integer.toString(Main.SELECT.getTarget().getLikes()));
		});
		
		nodeTarget.setAlignment(Pos.CENTER_LEFT);
		ListMenu.install(nodeTarget, Direction.DOWN, ListMenu.MenuTrigger.CLICK_LEFT, MenuPreset.ENTITY_TILE.getTemplate());
		
		HBox boxRight = new HBox(btnLike, nodeTarget);
		//		boxRight.prefWidthProperty().bind(UserInterface.getSelectPane().widthProperty());
		
		this.setLeft(boxCenter);
		this.setRight(boxRight);
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
			
			btnLike.setText(String.valueOf(target.getLikes()));
		} else {
			nodeTarget.setVisible(false);
		}
		
		return true;
	}
}
