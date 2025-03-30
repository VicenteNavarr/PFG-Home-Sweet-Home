package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.sql.Blob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class CreateUserViewController implements Initializable {

    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldSurname;
    @FXML
    private TextField fieldMail;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;
    @FXML
    private ImageView imgUser;
    @FXML
    private Button btnLoadImage;
    @FXML
    private TextField fieldPassword;

    private UserViewController userViewController;

    public void setUserViewController(UserViewController userViewController) {
        this.userViewController = userViewController;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRol.getItems().addAll("Administrador", "Consultor"); // Añadir los roles al ComboBox
    }

    /**
     * Método para crear un nuevo usuario desde la interfaz del administrador.
     *
     * @param event - Evento de acción activado por el botón "Crear".
     */
    @FXML
    private void createNewUser(ActionEvent event) {
        // Obtienelos datos de los campos de texto
        String nombre = fieldName.getText();
        String apellidos = fieldSurname.getText();
        String correoElectronico = fieldMail.getText();
        String contrasenia = fieldPassword.getText();
        int idRol = cmbRol.getSelectionModel().getSelectedIndex() + 1; // Rol seleccionado

        // Valida campos obligatorios
        if (nombre.isEmpty() || apellidos.isEmpty() || correoElectronico.isEmpty() || contrasenia.isEmpty() || imgUser.getImage() == null) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, completa todos los campos e incluye una imagen.");
            return;
        }

        // Comprueba si el usuario ya existe utilizando el correo electrónico
        if (UserDAO.userExists(correoElectronico)) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error de Registro", "El usuario ya existe.");
            System.out.println("Error: Usuario con el correo ya registrado: " + correoElectronico);

            // Limpia los campos si el usuario ya existe
            fieldName.clear();
            fieldSurname.clear();
            fieldMail.clear();
            imgUser.setImage(null);
            return;
        }

        // Convierte la imagen a formato Blob
        Blob fotoPerfil;
        try {
            fotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(imgUser.getImage()));
        } catch (Exception e) {
            System.err.println("Error al convertir la imagen: " + e.getMessage());
            return;
        }

        // Obtieneel ID del grupo del usuario actual desde la sesión
        int userGroupId = CurrentSession.getInstance().getUserGroupId();

        // Crea un objeto User con los datos proporcionados
        User newUser = new User(0, nombre, apellidos, correoElectronico, contrasenia, idRol, fotoPerfil, userGroupId);

        // Guarda el usuario en la base de datos
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.addUser(newUser);

        if (success) {
            // Usuario creado exitosamente
            System.out.println("Usuario creado exitosamente.");

            // Limpia los campos del formulario
            fieldName.clear();
            fieldSurname.clear();
            fieldMail.clear();
            imgUser.setImage(null);

            // Refresca la vista de usuarios si el controlador está disponible
            if (userViewController != null) {
                userViewController.loadUsers();
            }

            // Cierra la ventana actual
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } else {
            // Maneja error en la creación del usuario
            System.out.println("Hubo un error al crear el usuario.");
        }
    }

    /**
     * Método para cancelar y cerrar la vista actual.
     *
     * @param event - Evento de acción activado por el botón "Cancelar".
     */
    @FXML
    private void cancel(ActionEvent event) {
        // Cierra la ventana actual
        ((Button) event.getSource()).getScene().getWindow().hide();
        System.out.println("Ventana cerrada.");
    }

    /**
     * Método para cargar la imagen de perfil desde un archivo.
     *
     * @param event - Evento de acción activado por el botón "Cargar Imagen".
     */
    @FXML
    private void loadImage(ActionEvent event) {
        // Abre un cuadro de diálogo para seleccionar un archivo de imagen
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));

        // Muestra el cuadro de diálogo y obtener el archivo seleccionado
        File file = fileChooser.showOpenDialog(btnLoadImage.getScene().getWindow());

        if (file != null) {
            // Carga y muestra la imagen seleccionada
            Image image = new Image(file.toURI().toString());
            imgUser.setImage(image);
            System.out.println("Imagen cargada correctamente: " + file.getName());
        } else {
            // Maneja el caso en que no se seleccionó un archivo
            System.out.println("No se seleccionó ningún archivo.");
        }
    }

}
