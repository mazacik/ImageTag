package ui;

import ui.decorator.ColorUtil;
import javafx.scene.layout.*;

public abstract class NodeUtil {
	public static Border getBorder(int top, int right, int bottom, int left) {
		return new Border(new BorderStroke(ColorUtil.getColorBorder(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
	}
	public static Border getBorder(int border) {
		return getBorder(border, border, border, border);
	}
}
