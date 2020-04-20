package client.ui.override;

import javafx.scene.Parent;
import javafx.scene.control.Label;

public class Scene extends javafx.scene.Scene {
	public Scene() {
		this(new Label());
	}
	public Scene(Parent root) {
		super(root);
	}
}
