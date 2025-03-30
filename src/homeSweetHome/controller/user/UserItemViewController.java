package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.ImageUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserItemViewController {

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

    public void setUserViewController(UserViewController userViewController) {

        this.userViewController = userViewController;
    }

    /**
     * Metodo que setea los datos de la vista para mostrar usuario
     *
     * @param user - User
     */
    public void setUserData(User user) {

        this.user = user;

        // Configura el nombre del usuario
        lblUserName.setText(user.getNombre());
        // Configura el rol del usuario
        lblRol.setText(user.getNombreRol());

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
            // Recuperar datos completos del usuario por su ID
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
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenUpdate.getScene().getWindow());

            // Mostrar la ventana y esperar a que se cierre
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

        // Confirma la eliminación del usuario 
        System.out.println("Eliminando usuario: " + user.getNombre());

        // Llama al DAO para eliminar el usuario
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.deleteUserById(user.getId());

        if (success) {

            System.out.println("Usuario eliminado exitosamente.");

            // Refresca la lista en el UserViewController() para ver los cambios en tiempo real)
            if (userViewController != null) {

                System.out.println("Llamando a loadUsers desde deleteUser...");
                userViewController.loadUsers();

            } else {

                System.out.println("Error: userViewController es null.");
            }

        } else {

            System.out.println("Error al eliminar el usuario.");
        }
    }

}
