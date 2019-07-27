package userinterface.nodes;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Window;
import userinterface.nodes.node.IntroStageNode;
import userinterface.style.ColorUtil;
import userinterface.style.StyleUtil;
import userinterface.style.enums.ColorType;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class NodeUtil {
	private static ArrayList<ColorData> nodeList = new ArrayList<>();
	public static void equalizeWidth(Region... regions) {
		equalizeWidth(new ArrayList<>(Arrays.asList(regions)));
	}
	public static void equalizeWidth(ObservableList<Node> nodes) {
		ArrayList<Region> regions = new ArrayList<>();
		for (Node node : nodes) regions.add((Region) node);
		equalizeWidth(regions);
	}
	public static void equalizeWidth(ArrayList<Region> regions) {
		double width = 0;
		for (Region region : regions) {
			if (width < region.getWidth()) width = region.getWidth();
			if (width < region.getPrefWidth()) width = region.getPrefWidth();
		}
		for (Region region : regions) {
			region.setPrefWidth(width);
		}
	}
	public static ArrayList<ColorData> getNodeList() {
		return nodeList;
	}
	
	public static IntroStageNode getIntroStageNode(String projectFile, String workingDirectory) {
		IntroStageNode introStageNode = new IntroStageNode(projectFile, workingDirectory);
		addToManager(introStageNode, ColorType.ALT, ColorType.DEF, ColorType.NULL, ColorType.NULL);
		return introStageNode;
	}
	
	public static HBox getHBox(ColorType backgroundDef, ColorType backgroundAlt, Node... children) {
		HBox hBox = new HBox(children);
		addToManager(hBox, backgroundDef, backgroundAlt, ColorType.NULL, ColorType.NULL);
		return hBox;
	}
	public static VBox getVBox(ColorType backgroundDef, ColorType backgroundAlt, Node... children) {
		VBox vBox = new VBox(children);
		addToManager(vBox, backgroundDef, backgroundAlt, ColorType.NULL, ColorType.NULL);
		return vBox;
	}
	public static Border getBorder(int top, int right, int bottom, int left) {
		return new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
	}
	public static Border getBorder(int border) {
		return getBorder(1, 1, 1, 1);
	}
	
	public static void addToManager(Region region, ColorType ct) {
		addToManager(region, ct, ct, ct, ct);
	}
	public static void addToManager(Region region, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
		nodeList.add(new ColorData(region, backgroundDef, backgroundAlt, textFillDef, textFillAlt));
		StyleUtil.applyStyle(region);
	}
	
	public static void removeOrphanNodes() {
		ArrayList<ColorData> orphans = new ArrayList<>();
		for (ColorData colorData : nodeList) {
			try {
				Node node = colorData.getRegion();
				Scene scene = node.getScene();
				Window window = scene.getWindow();
				
				//Window window = colorData.getRegion().getScene().getWindow();
				if (window == null) throw new NullPointerException();
			} catch (NullPointerException e) {
				orphans.add(colorData);
			}
		}
		
		for (ColorData orphan : orphans) {
			nodeList.remove(orphan);
		}
	}
}
