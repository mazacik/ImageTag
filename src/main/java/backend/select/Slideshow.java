package backend.select;

import backend.reload.Reload;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import main.Main;

public class Slideshow {
	private final PauseTransition pT = new PauseTransition(new Duration(5000));
	
	private boolean running = false;
	private boolean changing = false;
	
	public Slideshow() {
		pT.setOnFinished(event -> next());
	}
	
	private void next() {
		changing = true;
		Main.SELECT.setRandom();
		changing = false;
		Reload.start();
		pT.playFromStart();
	}
	
	public void start() {
		running = true;
		next();
	}
	public void stop() {
		running = false;
		pT.stop();
	}
	
	public boolean isRunning() {
		return running;
	}
	public boolean isChanging() {
		return changing;
	}
}
