package ui.stage;

import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import ui.decorator.Decorator;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressNode extends ProgressBar {
	private Object caller;
	
	private double total;
	private double current;
	
	private String barColor;
	private String borderColor;
	
	public ProgressNode() {
		this.total = 1;
		this.current = 1;
		
		this.setMaxWidth(Double.MAX_VALUE);
		this.setPadding(new Insets(5));
		this.setOpacity(0);
		
		setBarColor(Decorator.getColorPrimary());
		setBorderColor(Decorator.getColorBorder());
		
		AtomicBoolean deco = new AtomicBoolean(false);
		this.progressProperty().addListener(event -> {
			if (!deco.get()) {
				try {
					//this.lookup(".progress-bar").setStyle("-fx-border-color:" + borderColor + ";");
					this.lookup(".bar").setStyle("-fx-background-color: " + barColor + "; -fx-background-insets: 1; -fx-background-radius: 0;");
					this.lookup(".track").setStyle("-fx-border-color:" + borderColor + ";-fx-background-color: transparent; -fx-background-radius: 0;");
					this.setOpacity(1);
					deco.set(true);
				} catch (NullPointerException ignored) {}
			}
		});
	}
	
	public void setup(Object caller, double total) {
		this.caller = caller;
		this.total = total;
		this.current = 0;
	}
	
	public void advance(Object caller) {
		advance(caller, 1);
	}
	public void advance(Object caller, double increment) {
		if (caller == this.caller) {
			current += increment;
			if (current > total) return;
			final double progress = current / total;
			this.setProgress(progress);
		}
	}
	
	public Object getCaller() {
		return caller;
	}
	
	public void setBarColor(Color color) {
		barColor = Decorator.getCssString(color);
	}
	public void setBorderColor(Color color) {
		borderColor = Decorator.getCssString(color);
	}
}
