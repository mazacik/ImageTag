import javafx.application.Application;
import javafx.stage.Stage;
import system.InstanceRepo;
import user_interface.node_factory.template.intro.IntroWindow;

public class Main extends Application implements InstanceRepo {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        logger.debug(this, "starting application");
        new IntroWindow();
    }
}
