/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {

        /*try {

            // Inicio del servidor
            Server s = new Server(2000); // Constructor no lanza IOException
            Thread t = new Thread(s);
            System.out.println("Hebra servidor Iniciada");
            t.start();

        } catch (Exception ex) { // Captura cualquier excepción inesperada durante la ejecución

            System.err.println("Error inesperado al iniciar el servidor: " + ex.getMessage());

        }*/
        launch(args);

    }

}
