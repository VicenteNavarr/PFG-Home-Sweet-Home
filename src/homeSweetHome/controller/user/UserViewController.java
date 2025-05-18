/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.LanguageManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class de vista Usuario
 *
 * @author Usuario
 */
public class UserViewController implements Initializable {

    @FXML
    private VBox UserContainer;
    @FXML
    private Button btnOpenCreateUser;
    @FXML
    private ScrollPane scrollPane; // ScrollPane que envuelve el contenedor
    @FXML
    private Label usersTitle;

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos

    //String userRol = homeSweetHome.dataPersistence.CurrentSession.getInstance().
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            //btnOpenCreateUser.setVisible(false);
            btnOpenCreateUser.setDisable(true);

        }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  

        loadUsers();//Carga el metodo de listar usuarios y mostrar en el scrollpane
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        // Obtiene la instancia única del Singleton
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager es nulo. Traducción no aplicada.");
            return;
        }

        // Verificación del idioma activo
        String idiomaActivo = languageManager.getLanguageCode();
        System.out.println("Idioma activo en updateTexts(): " + idiomaActivo);

        // Traducción del botón de creación de usuario
        btnOpenCreateUser.setText(languageManager.getTranslation("createUser"));

        usersTitle.setText(languageManager.getTranslation("usersTitle"));

        System.out.println("Botón 'createUser': " + btnOpenCreateUser.getText());

        // Refrescar UI para aplicar los cambios visualmente
        //Platform.runLater(() -> btnOpenCreateUser.getScene().getWindow().sizeToScene());
        System.out.println("Traducciones aplicadas correctamente en UserViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   
    
    /**
     * Metodo para crear un nuevo usuario - abre el pop up CreateUserView
     *
     * @param event
     */
    @FXML
    private void openCreateUser(ActionEvent event) {

        try {

            // Cargar el archivo FXML para la vista CreateUserView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/user/CreateUserView.fxml"));
            Parent root = loader.load();

            // Controlador asociado a la ventana CreateUserView
            CreateUserViewController createUserController = loader.getController();

            // Pasa la referencia del controlador actual (UserViewController)
            //Sireve para que funcione el metodo load()
            createUserController.setUserViewController(this);

            // Crea un nuevo Stage (ventana)
            Stage stage = new Stage();
            stage.setTitle("Crear Usuario");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/usersIconBlue.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Ventana modal
            stage.initOwner(btnOpenCreateUser.getScene().getWindow()); // Establece el dueño de la ventana

            // Muestra el popup
            stage.showAndWait();

        } catch (IOException e) {

            System.err.println("Error al cargar la vista CreateUserView: " + e.getMessage());
        }
    }

    /**
     * Metodo para cargar la lista de usuarios por id de grupo(toma de DAO)
     */
    public void loadUsers() {

        UserDAO userDAO = new UserDAO();

        // Obtiene todos los usuarios del grupo actual
        int userGroupId = homeSweetHome.dataPersistence.CurrentSession.getInstance().getUserGroupId();
        List<User> users = userDAO.getUsersByGroup(userGroupId);

        // Obteniene el ID del usuario actual
        int currentUserId = homeSweetHome.dataPersistence.CurrentSession.getInstance().getUserId();

        UserContainer.getChildren().clear(); // Limpia el contenedor antes de poblarlo

        //Recorremos la lista usuarios sacada de la bbdd
        for (User user : users) {

            // Excluir al usuario actual(no queremos que se muestre a si mismo)
            if (user.getId() == currentUserId) {

                continue;
            }

            try {

                // Carga el archivo FXML de UserItemView(esta vista muestra usuario)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/user/UserItemView.fxml"));
                Node userItemNode = loader.load();

                // Pasa los datos al controlador de UserItemView
                UserItemViewController controller = loader.getController();
                controller.setUserData(user);

                // Pasa la referencia del controlador principal
                controller.setUserViewController(this);

                // Añadeel nodo al contenedor
                UserContainer.getChildren().add(userItemNode);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

}
