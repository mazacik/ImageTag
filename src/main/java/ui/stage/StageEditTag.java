package ui.stage;

import base.CustomList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import ui.decorator.Decorator;
import ui.node.NodeEdit;
import ui.node.NodeText;
import ui.override.HBox;
import ui.override.VBox;

public class StageEditTag extends StageBase {
	private static final CustomList<NodeLevel> nodesLevel;
	private static final VBox boxLevels;
	
	private static final NodeText levelAdd;
	
	private static final ScrollPane scrollPane;
	private static final VBox boxContent;
	
	private static final NodeText nodeOK;
	private static final NodeText nodeCancel;
	
	private static final CustomList<String> levels;
	
	static {
		levels = new CustomList<>();
		nodesLevel = new CustomList<>();
		boxLevels = new VBox();
		
		levelAdd = new NodeText("Add Level", true, true, false, true);
		scrollPane = new ScrollPane(boxLevels);
		scrollPane.setMinHeight(500);
		boxContent = new VBox(scrollPane, levelAdd);
		
		levelAdd.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			NodeLevel nodeLevel = new NodeLevel(boxLevels.getChildren().size(), "");
			nodesLevel.add(nodeLevel);
			boxLevels.getChildren().add(nodeLevel);
		});
		
		boxContent.setAlignment(Pos.CENTER);
		boxContent.setPadding(new Insets(5));
		boxContent.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				returnValue();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				getInstance().close();
			}
		});
		
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setBackground(Background.EMPTY);
		
		nodeOK = new NodeText("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (checkEditNodes()) {
				returnValue();
			}
		});
		
		nodeCancel = new NodeText("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, getInstance()::close);
	}
	
	private static void returnValue() {
		if (checkEditNodes()) {
			levels.clear();
			nodesLevel.forEach(nodeLevel -> levels.add(nodeLevel.nodeEdit.getText()));
			getInstance().close();
		}
	}
	
	public static boolean checkEditNodes(String... fields) {
		//		if (fields[0].trim().isEmpty()) {
		//			getInstance().setErrorMessage("Field \"" + nodeText.getText() + "\" cannot be empty.");
		//			return false;
		//		}
		return true;
	}
	
	private static void refreshBoxContent(CustomList<String> levelsBefore) {
		nodesLevel.clear();
		
		if (levelsBefore == null) {
			nodesLevel.add(new NodeLevel(0, ""));
		}
		
		for (int i = 0; i < levelsBefore.size(); i++) {
			nodesLevel.add(new NodeLevel(i, levelsBefore.get(i)));
		}
		
		boxLevels.getChildren().setAll(nodesLevel);
	}
	
	public static CustomList<String> show(CustomList<String> levelsBefore) {
		refreshBoxContent(levelsBefore);
		
		getInstance().setErrorMessage("");
		getInstance().showAndWait();
		
		return levels;
	}
	
	public static ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	private StageEditTag() {
		super("Edit Group", true, true, true);
		setRoot(boxContent);
		setButtons(nodeOK, nodeCancel);
	}
	private static class Loader {
		private static final StageEditTag INSTANCE = new StageEditTag();
	}
	public static StageEditTag getInstance() {
		return Loader.INSTANCE;
	}
	
	private static class NodeLevel extends HBox {
		private final NodeText nodeText;
		private final NodeEdit nodeEdit;
		
		public NodeLevel(int level, String edit) {
			nodeText = new NodeText("Level " + (level + 1) + ":");
			nodeText.setPrefWidth(80);
			
			nodeEdit = new NodeEdit(edit);
			nodeEdit.setPrefWidth(200);
			nodeEdit.setBorder(Decorator.getBorder(1, 1, 1, 1));
			
			this.getChildren().addAll(nodeText, nodeEdit);
		}
		
		public NodeText getNodeText() {
			return nodeText;
		}
		public NodeEdit getNodeEdit() {
			return nodeEdit;
		}
	}
}
