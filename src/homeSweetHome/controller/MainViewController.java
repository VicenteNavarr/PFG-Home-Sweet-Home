/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller;

import homeSweetHome.controller.user.CurrentUserSettingsViewController;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.socket.Server;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class MainViewController implements Initializable {

    @FXML
    private AnchorPane viewContainer;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button btnLoadControlPanelView;
    @FXML
    private Button btnLoadUserView;
    @FXML
    private Button btnLoadMealView;
    @FXML
    private Button btnLoadPurchaseView;
    @FXML
    private Button btnLoadTaskView;
    @FXML
    private Button btnLoadEventView;
    @FXML
    private Button btnLoadBudgetView;
    @FXML
    private Label lblHola;

    private Server server; // Referencia al servidor

    private Thread serverThread; // Referencia al hilo del servidor
    @FXML
    private ImageView userImage;

    private User user; // Referencia al usuario actual
    @FXML
    private MenuItem btnSettings;
    @FXML
    private MenuItem btnCloseSession;
    @FXML
    private MenuItem btnExitApp;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Crear instancia de UserDAO
        UserDAO userDAO = new UserDAO();
        // Llamar al método para cargar la imagen del usuario actual
        setUserImageFromDatabase(userDAO);

    }

///////////////CARGA DE VISTAS -> ACCIÓN BOTONES/////////////////////////////////////////////
    /**
     * Método llamado cuando se hace clic en el botón "btnLoadControlPanelView".
     * Carga y muestra la vista ControlPanelView.fxml en el centro del
     * BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadControlPanelView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/ControlPanelView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadUserView". Carga y
     * muestra la vista UserView.fxml en el centro del BorderPane.
     *
     * @param event
     *
     */
    @FXML
    private void loadUserView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/user/UserView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadMealView". Carga y
     * muestra la vista MealView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadMealView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/MealView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadPurchaseView".
     * Carga y muestra la vista PurchaseView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadPurchaseView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/PurchaseView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadTaskView". Carga y
     * muestra la vista TaskView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadTaskView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/task/TaskView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadEventView". Carga
     * y muestra la vista EventView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadEventView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/event/EventView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadBudgetView". Carga
     * y muestra la vista BudgetView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadBudgetView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/BudgetView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Metodo para darle forma circular a las imagenes
     *
     * @param imageView - ImageView
     */
    private void setClipToCircle(ImageView imageView) {

        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle();
        clip.setRadius(Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2); // Radio del círculo
        clip.setCenterX(imageView.getFitWidth() / 2); // Centrar en X
        clip.setCenterY(imageView.getFitHeight() / 2); // Centrar en Y
        imageView.setClip(clip); // Aplicar el clip al ImageView
    }

    /**
     * Método para cargar y establecer la imagen de perfil del usuario actual
     * desde la base de datos.
     *
     * @param userDAO - Instancia de UserDAO para acceder a los datos del
     * usuario.
     */
    public void setUserImageFromDatabase(UserDAO userDAO) {
        // Obtener el ID del usuario actual desde la sesión
        int currentUserId = CurrentSession.getInstance().getUserId();
        System.out.println("Obteniendo usuario con ID: " + currentUserId);

        try {
            // Obtener el usuario desde la base de datos
            User currentUser = userDAO.getUserById(currentUserId);

            if (currentUser != null && currentUser.getFotoPerfil() != null) {
                // Cargar la imagen desde el Blob
                InputStream inputStream = currentUser.getFotoPerfil().getBinaryStream();
                Image userImg = new Image(inputStream);
                userImage.setImage(userImg); // Mostrar la imagen
                System.out.println("Imagen cargada correctamente para el usuario: " + currentUser.getNombre());
            } else {
                // Imagen predeterminada si no hay foto de perfil
                System.out.println("El usuario actual no tiene una imagen de perfil.");
                userImage.setImage(new Image(getClass().getResourceAsStream("/images/add-image.png")));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen del usuario: " + e.getMessage());
            // Cargar imagen predeterminada si hay un error
            userImage.setImage(new Image(getClass().getResourceAsStream("/images/add-image.png")));
        }

        // Aplicar recorte circular a la imagen
        setClipToCircle(userImage);
    }

    /**
     * Metodo para establecer el nombre del usuario en la parte superior de la
     * vista
     *
     * @param userName
     */
    public void setUserName(String userName) {
        lblHola.setText("Hola, " + userName + "!");
    }

    /**
     * Lleva a la pantalla de ajustes del usuario actual(update de la bbdd )
     *
     * @param event
     */
    @FXML
    private void settings(ActionEvent event) {

        /*try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/CurrentUserSettingsView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }*/
        try {
            // Crear una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/user/CurrentUserSettingsView.fxml"));

            // Cargar la vista y obtener el nodo raíz
            AnchorPane root = loader.load();

            // Obtener el controlador de la vista cargada
            CurrentUserSettingsViewController controller = loader.getController();

            // Pasar el ID del usuario actual al controlador
            int currentUserId = CurrentSession.getInstance().getUserId(); // Obtener el ID desde la sesión actual
            controller.setUserData(currentUserId); // Método en CurrentUserSettingsViewController

            // Establecer la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Cierra la vista actual y abre la vista de login
     *
     * @param event
     */
    @FXML
    private void closeSession(ActionEvent event) {
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/LoginView.fxml"));
            Parent root = loader.load();

            // Crear una nueva escena y asignarla a un nuevo Stage
            Stage newStage = new Stage();
            newStage.setScene(new javafx.scene.Scene(root));
            newStage.setTitle("Inicio de Sesión");
            newStage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cierra el programa system.exit(0)
     *
     * @param event
     */
    @FXML
    private void exitApp(ActionEvent event) {

        System.out.println("Saliendo del programa");
        System.exit(0);
    }

}
