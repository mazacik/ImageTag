package client.ui.stage.primary.project;

import client.ui.custom.textnode.TextNode;
import client.ui.override.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import server.base.CustomList;
import server.misc.FileUtil;
import server.misc.Project;

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
