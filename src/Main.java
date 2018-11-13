import gui.custom.specific.IntroWindow;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.MainUtil;

public class Main extends Application implements MainUtil {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        log.out("starting application", this.getClass());
        new IntroWindow();
    }
}
