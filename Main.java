import javafx.application.Application;
import javafx.stage.Stage;
import userinterface.template.specific.IntroWindow;
import utils.MainUtil;

public class Main extends Application implements MainUtil {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        logger.debug(this, "starting application");
        new IntroWindow();
    }
}
