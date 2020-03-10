package main;

import misc.Settings;
import org.junit.jupiter.api.Test;

class MainTest {
	@Test
	void main() {
		setup();
		test();
	}
	
	private void setup() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		Settings.readFromDisk();
		
		//		if (!Main.DEBUG_MAIN_QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
		//			MainStage.layoutIntro();
		//		} else {
		//			Project.setFirstAsCurrent();
		//			MainStage.layoutMain();
		//			Main.startProjectDatabaseLoading();
		//		}
	}
	
	private void test() {
	
	}
}
