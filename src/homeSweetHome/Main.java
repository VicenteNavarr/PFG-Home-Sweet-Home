
package homeSweetHome;

import homeSweetHome.socket.Server;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Usuario
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/LoginView.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Registro de Usuario");
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {

        launch(args);

    }

}
