package backend.project;

import backend.BaseList;
import backend.misc.FileUtil;

import java.io.File;
import java.util.Comparator;

public abstract class ProjectUtil {
	static transient Project currentProject;
	public static Project getCurrentProject() {
		return currentProject;
	}
	public static void setCurrentProject(Project project) {
		if (project == null) {
			BaseList<Project> projects = getProjects();
			if (!projects.isEmpty()) {
				projects.sort(comparatorLastAccessMs);
				currentProject = projects.getFirst();
			} else throw new NullPointerException();
		} else currentProject = project;
	}
	
	private static final Comparator<Project> comparatorLastAccessMs = (p1, p2) -> (int) (p2.getLastAccessMs() - p1.getLastAccessMs());
	public static Comparator<Project> getComparatorLastAccessMs() {
		return comparatorLastAccessMs;
	}
	
	public static String generateProjectFilePath(String projectName) {
		return FileUtil.getDirectoryProject() + File.separator + projectName + ".json";
	}
	
	public static BaseList<Project> getProjects() {
		BaseList<Project> projects = new BaseList<>();
		getProjectFiles().forEach(projectFile -> projects.add(Project.read(projectFile.getAbsolutePath())));
		return projects;
	}
	public static BaseList<File> getProjectFiles() {
		return new BaseList<>(new File(FileUtil.getDirectoryProject()).listFiles(file -> file.isFile() && file.getName().endsWith(".json")));
	}
}
