package application.frontend.component;

import application.frontend.decorator.ColorUtil;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class NodeUtil {
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
	
	public static Border getBorder(int top, int right, int bottom, int left) {
		return new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
	}
	public static Border getBorder(int border) {
		return getBorder(border, border, border, border);
	}
}
