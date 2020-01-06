package ui.main.stage;

import base.CustomList;
import misc.Project;
import ui.node.NodeText;
import ui.override.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import misc.FileUtil;

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
			this.getChildren().add(new NodeText("No Projects Found"));
		}
	}
}
