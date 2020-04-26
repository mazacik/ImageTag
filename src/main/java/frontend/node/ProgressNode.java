package frontend.node;

import frontend.decorator.DecoratorUtil;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.Root;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressNode extends StackPane {
	private Thread caller;
	
	private double total;
	private double current;
	
	private final ProgressBar progressBar;
	private final Text progressText;
	
	private String barColor;
	private String borderColor;
	
	public ProgressNode() {
		this.total = 1;
		this.current = 1;
		
		progressBar = new ProgressBar();
		progressBar.setMaxWidth(Double.MAX_VALUE);
		
		progressText = new Text("");
		progressText.setFont(DecoratorUtil.getFont());
		progressText.setFill(Color.WHITE);
		progressText.setStyle("-fx-blend-mode: difference;");
		
		this.getChildren().addAll(progressBar, progressText);
		this.setPadding(new Insets(5));
		this.setMouseTransparent(true);
		this.setOpacity(0);
		this.setBackground(DecoratorUtil.getBackgroundPrimary());
		this.maxHeightProperty().bind(progressBar.heightProperty());
		
		this.setBarColor(DecoratorUtil.getColorPrimary());
		this.setBorderColor(DecoratorUtil.getColorBorder());
		
		AtomicBoolean deco = new AtomicBoolean(false);
		progressBar.progressProperty().addListener(event -> {
			if (!deco.get()) {
				try {
					//this.lookup(".progress-bar").setStyle("-fx-background-color:" + barColor + ";");
					this.lookup(".bar").setStyle("-fx-background-color: " + barColor + "; -fx-background-insets: 1; -fx-background-radius: 0;");
					this.lookup(".track").setStyle("-fx-border-color:" + borderColor + ";-fx-background-color: transparent; -fx-background-radius: 0;");
					this.setOpacity(1);
					deco.set(true);
				} catch (NullPointerException ignored) {}
			}
		});
	}
	
	public void setup(Thread caller, double total) {
		if (Thread.currentThread() != Root.THREAD_MAIN) {
			this.caller = caller;
			this.total = total;
			this.current = 0;
		}
	}
	
	public void advance(Thread caller) {
		advance(caller, 1);
	}
	public void advance(Thread caller, double increment) {
		if (caller == this.caller) {
			current += increment;
			if (current > total) return;
			final double progress = current / total;
			progressBar.setProgress(progress);
			progressText.setText((int) (progress * 100) + "%");
		}
	}
	
	public Thread getCaller() {
		return caller;
	}
	
	public void setBarColor(Color color) {
		barColor = DecoratorUtil.getCssString(color);
	}
	public void setBorderColor(Color color) {
		borderColor = DecoratorUtil.getCssString(color);
	}
}
