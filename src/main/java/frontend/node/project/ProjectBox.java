package frontend.node.project;

import backend.BaseList;
import backend.project.Project;
import backend.project.ProjectUtil;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class ProjectBox extends VBox {
	public ProjectBox() {
		this.setMinWidth(400);
		this.setPadding(new Insets(5));
		this.refresh();
	}
	
	public void refresh() {
		BaseList<Project> projects = ProjectUtil.getProjects();
		
		this.getChildren().clear();
		
		if (!projects.isEmpty()) {
			projects.sort(ProjectUtil.getComparatorLastAccessMs());
			projects.forEach(project -> this.getChildren().add(new ProjectNode(project)));
		} else {
			this.setAlignment(Pos.CENTER);
			this.getChildren().add(new TextNode("No Projects Found", false, false, false, false));
		}
	}
}
