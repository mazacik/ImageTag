package tools;

import gui.decorator.ColorUtil;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.Collection;

public abstract class NodeUtil {
	public static void equalizeWidth(Region... regions) {
		equalizeWidth(Arrays.asList(regions));
	}
	public static <T> void equalizeWidth(Collection<? extends T> regions) {
		final int[] width = {0};
		
		ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
			if (newValue.intValue() > width[0]) {
				width[0] = newValue.intValue();
				for (T region : regions) {
					((Region) region).setPrefWidth(width[0]);
				}
			}
		};
		
		for (T region : regions) {
			((Region) region).widthProperty().addListener(listener);
		}
	}
	
	public static Border getBorder(int top, int right, int bottom, int left) {
		return new Border(new BorderStroke(ColorUtil.getColorBorder(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
	}
	public static Border getBorder(int border) {
		return getBorder(border, border, border, border);
	}
}
