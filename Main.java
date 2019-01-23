import javafx.application.Application;
import javafx.stage.Stage;
import userinterface.template.intro.IntroWindow;
import utils.InstanceRepo;

public class Main extends Application implements InstanceRepo {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        logger.debug(this, "starting application");
        new IntroWindow();
    }
}
