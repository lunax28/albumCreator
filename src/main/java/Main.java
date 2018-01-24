import com.equilibriummusicgroup.albumCreator.model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Album Creator is a helper utility to create iTunes Producer packages
 *
 * @author  lunax28
 * @version 1.0
 * @since   2018-01-24
 */

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("albumCreatorHome.fxml")) ;

            BorderPane root = (BorderPane)loader.load();
            AlbumCreatorController controller = loader.getController();
            Model model = new Model();
            controller.setModel(model) ;

            Scene scene = new Scene(root);
            primaryStage.setTitle("Album Creator");
            primaryStage.setScene(scene);
            primaryStage.show();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}