package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.utils.LanguageManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserItemViewController implements Initializable {

    @FXML
    private ImageView userImage; // Imagen del usuario
    @FXML
    private Label lblUserName; // Nombre del usuario
    @FXML
    private Label lblRol; // Rol del usuario
    @FXML
    private Button btnOpenUpdate; // Botón para modificar
    @FXML
    private Button btnDelete; // Botón para eliminar

    private User user; // Referencia al usuario actual

    private UserViewController userViewController;
    
    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos

    // Paso del controlador
    public void setUserViewController(UserViewController userViewController) {

        this.userViewController = userViewController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Si el usuario tiene rol consultor, desactivamos botones
        if (role==2){
        
            //btnOpenCreateUser.setVisible(false);
            btnOpenUpdate.setDisable(true);
            btnDelete.setDisable(true);
        
        }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   

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

        // Traducción de botones
        btnOpenUpdate.setText(languageManager.getTranslation("updateUser"));
        btnDelete.setText(languageManager.getTranslation("deleteUser"));

        System.out.println("Botón 'updateUser': " + btnOpenUpdate.getText());
        System.out.println("Botón 'deleteUser': " + btnDelete.getText());

        // Traducción del rol del usuario
        String rolOriginal = lblRol.getText();
        String rolTraducido = switch (rolOriginal) {

            case "Administrador", "Administrator" ->
                languageManager.getTranslation("roleAdmin");
            case "Consultor", "Consultant" ->
                languageManager.getTranslation("roleConsultant");
            default ->
                rolOriginal; // Mantiene el rol si no se encuentra una traducción
        };

        lblRol.setText(rolTraducido);

        System.out.println("Etiqueta 'userNameLabel': " + lblUserName.getText());
        System.out.println("Rol original: " + rolOriginal + " → Traducción: " + rolTraducido);

        // Refresca UI para aplicar los cambios visualmente
        //Platform.runLater(() -> lblUserName.getScene().getWindow().sizeToScene());
        System.out.println("Traducciones aplicadas correctamente en UserItemViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////    
    
    /**
     * Metodo que setea los datos de la vista para mostrar usuario
     *
     * @param user - User
     */
    public void setUserData(User user) {

        this.user = user;

        // Configura el nombre del usuario
        lblUserName.setText(user.getNombre());

        // Traducción del rol al momento de establecer los datos
        String rolOriginal = user.getNombreRol();
        String rolTraducido = switch (rolOriginal) {

            case "Administrador", "Administrator" ->
                LanguageManager.getInstance().getTranslation("roleAdmin");
            case "Consultor", "Consultant" ->
                LanguageManager.getInstance().getTranslation("roleConsultant");
            default ->
                rolOriginal;
        };

        lblRol.setText(rolTraducido);

        System.out.println("Rol original: " + rolOriginal + " → Traducción: " + rolTraducido);

        // Intenta cargar la imagen de perfil
        if (user.getFotoPerfil() != null) {

            try {

                InputStream inputStream = user.getFotoPerfil().getBinaryStream();
                System.out.println("Cargando imagen para el usuario: " + user.getNombre());
                Image userImg = new Image(inputStream);
                userImage.setImage(userImg); // Mostrar la imagen

            } catch (Exception e) {

                System.err.println("Error al cargar la imagen de perfil: " + e.getMessage());
                userImage.setImage(new Image(getClass().getResourceAsStream("/images/add-image.png")));
            }

        } else {

            System.out.println("Sin imagen de perfil para el usuario: " + user.getNombre());
            userImage.setImage(new Image(getClass().getResourceAsStream("/images/add-image.png")));
        }

        // Aplica el recorte circular a la imagen
        ImageUtils.setClipToCircle(userImage);
    }

    /**
     * Metodo para modificar datos de usuario abre la vistaupdateuserview y pasa
     * comunica con ella los datos
     *
     * @param event
     */
    @FXML
    private void openUpdateUser(ActionEvent event) {

        try {

            UserDAO userDAO = new UserDAO();
            // Recupera datos completos del usuario por su ID
            User updatedUser = userDAO.getUserById(user.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/user/UpdateUserView.fxml"));
            Parent root = loader.load();

            // Obteniene el controlador asociado a la vista UpdateUserView
            UpdateUserViewController updateUserController = loader.getController();

            // Pasa el controlador principal y el usuario actualizado
            if (userViewController != null) {

                updateUserController.setUserViewController(userViewController);
            }

            updateUserController.setUserData(updatedUser);

            // Crea un Stage (ventana) para el popup
            Stage stage = new Stage();
            stage.setTitle("Actualizar Usuario");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/usersIconBlue.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenUpdate.getScene().getWindow());

            // Muestra la ventana y esperar a que se cierre
            stage.showAndWait();

        } catch (IOException e) {

            System.err.println("Error al cargar la vista UpdateUserView: " + e.getMessage());
        }
    }

    /**
     * Metodo para eliminar usuario
     *
     * @param event
     */
    @FXML
    private void deleteUser(ActionEvent event) {

        if (user == null) {
            System.out.println("Error: No hay usuario seleccionado para eliminar.");
            return;
        }

        // Confirmación con alerta según el idioma
        if (LanguageManager.getInstance().getLanguageCode().equals("es")) {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!
            alert.setTitle("Confirmación");
            alert.setHeaderText("Eliminar usuario");
            alert.setContentText("¿Seguro que desea eliminar al usuario " + user.getNombre() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Si el usuario confirma, se procede con la eliminación
                System.out.println("Eliminando usuario: " + user.getNombre());

                UserDAO userDAO = new UserDAO();
                boolean success = userDAO.deleteUserById(user.getId());

                if (success) {
                    
                    Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
                    alertSuccess.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!
                    alertSuccess.setTitle("Éxito");
                    alertSuccess.setHeaderText(null);
                    alertSuccess.setContentText("Usuario eliminado exitosamente.");
                    alertSuccess.showAndWait();

                    if (userViewController != null) {
                        
                        userViewController.loadUsers();
                    }
                    
                } else {
                    
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!
                    alertError.setTitle("Error");
                    alertError.setHeaderText(null);
                    alertError.setContentText("Error al eliminar el usuario.");
                    alertError.showAndWait();
                }
            }
            
        } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!
            alert.setTitle("Confirmation");
            alert.setHeaderText("Delete user");
            alert.setContentText("Are you sure you want to delete user " + user.getNombre() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent() && result.get() == ButtonType.OK) {

                System.out.println("Deleting user: " + user.getNombre());

                UserDAO userDAO = new UserDAO();
                boolean success = userDAO.deleteUserById(user.getId());

                if (success) {
                    
                    Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
                    alertSuccess.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!
                    alertSuccess.setTitle("Success");
                    alertSuccess.setHeaderText(null);
                    alertSuccess.setContentText("User successfully deleted.");
                    alertSuccess.showAndWait();

                    if (userViewController != null) {
                        
                        userViewController.loadUsers();
                    }
                    
                } else {
                    
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!
                    alertError.setTitle("Error");
                    alertError.setHeaderText(null);
                    alertError.setContentText("Error deleting user.");
                    alertError.showAndWait();
                }
            }
        }
    }

}
