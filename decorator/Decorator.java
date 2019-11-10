package application.decorator;

import application.main.InstanceCollector;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class Decorator implements InstanceCollector {
	private static Font font = new Font(settings.getFontSize());
	
	public static String getColorAsStringForCss(Color color) {
		return String.format("#%02X%02X%02X",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}
	
	public static void applyScrollbarStyle(Node node) {
		try {
			node.applyCss();
			node.lookup(".track").setStyle("-fx-background-color: transparent;" +
					" -fx-border-color: transparent;" +
					" -fx-background-radius: 0.0em;" +
					" -fx-border-radius: 0.0em;" +
					" -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;" +
					" -fx-pref-width: 15;" +
					" -fx-padding: 3 2 3 3;");
			node.lookup(".increment-button").setStyle("-fx-background-color: transparent;" +
					" -fx-background-radius: 0.0em;" +
					" -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".decrement-button").setStyle("-fx-background-color: transparent;" +
					" -fx-background-radius: 0.0em;" +
					" -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".increment-arrow").setStyle("-fx-padding: 0.0em 0.0;");
			node.lookup(".decrement-arrow").setStyle("-fx-padding: 0.0em 0.0;");
			node.lookup(".thumb").setStyle("-fx-background-color: derive(black, 90.0%);" +
					" -fx-background-insets: 0.0, 0.0, 0.0;" +
					" -fx-background-radius: 0.0em;");
			node.lookup(".viewport").setStyle("-fx-background-color: transparent;");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public static Font getFont() {
		return font;
	}
}
