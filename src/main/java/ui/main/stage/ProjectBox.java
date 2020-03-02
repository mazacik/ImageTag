package ui.main.stage;

import base.CustomList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import misc.FileUtil;
import misc.Project;
import ui.node.TextNode;
import ui.override.VBox;

public class ProjectBox extends VBox {
	public ProjectBox() {
		this.setMinWidth(400);
		this.setPadding(new Insets(5));
		this.refresh();
	}
	
	public void refresh() {
		CustomList<Project> projects = FileUtil.getProjects();
		
		this.getChildren().clear();
		
		if (!projects.isEmpty()) {
			projects.sort(Project.getComparator());
			projects.forEach(project -> this.getChildren().add(new ProjectNode(project)));
		} else {
			this.setAlignment(Pos.CENTER);
			this.getChildren().add(new TextNode("No Projects Found", false, false, false, false));
		}
	}
}
