/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import java.io.File;
import java.net.URL;
import java.sql.Blob;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class de vista Usuario
 *
 * @author Usuario
 */
public class CurrentUserSettingsViewController implements Initializable {

    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldSurname;
    @FXML
    private TextField fieldMail;
    @FXML
    private TextField fieldPassword;
    @FXML
    private ImageView imgUser;
    @FXML
    private Button btnLoadImage;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    private ComboBox<?> cmbRol; // ScrollPane que envuelve el contenedor

    private UserViewController userViewController;

    private User currentUser; // Variable para almacenar el usuario actual

    public void setUserViewController(UserViewController userViewController) {
        this.userViewController = userViewController;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * Método para cargar la imagen de perfil desde un archivo seleccionado por
     * el usuario o utilizar una imagen predeterminada si no se selecciona
     * ningún archivo.
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
            try {
                // Carga la imagen seleccionada y guardarla en currentUser
                Image image = new Image(file.toURI().toString());
                imgUser.setImage(image);

                Blob fotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(image));
                currentUser.setFotoPerfil(fotoPerfil);

                System.out.println("Imagen cargada correctamente: " + file.getName());
            } catch (Exception e) {
                // Mostrar una alerta si ocurre un error al cargar la imagen
                System.err.println("Error al cargar la imagen: " + e.getMessage());
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen seleccionada. Inténtalo de nuevo.");
            }
        } else {
            // Usa una imagen predeterminada si no se seleccionó ningún archivo
            System.out.println("No se seleccionó ningún archivo. Usando imagen predeterminada.");
            imgUser.setImage(new Image(getClass().getResourceAsStream("/images/default-profile.png")));

            try {
                // Convierter la imagen predeterminada y guardarla en currentUser
                Blob defaultFotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(
                        new Image(getClass().getResourceAsStream("/images/add-image.png"))
                ));
                currentUser.setFotoPerfil(defaultFotoPerfil);
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen predeterminada: " + e.getMessage());
            }
        }
    }

    /**
     * Guarda las modificaciones hechas en el usuario seleccionado (por ID del
     * usuario).
     *
     * @param event - Evento de acción activado por el botón "Guardar Cambios".
     */
    @FXML
    private void saveChanges(ActionEvent event) {
        // Valida que los campos obligatorios no estén vacíos
        String nombre = fieldName.getText();
        String apellidos = fieldSurname.getText();
        String correoElectronico = fieldMail.getText();
        String contrasenia = fieldPassword.getText();

        if (nombre.isEmpty() || apellidos.isEmpty() || correoElectronico.isEmpty() || contrasenia.isEmpty() || imgUser.getImage() == null) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, completa todos los campos e incluye una imagen.");
            return;
        }

        // Intenta convertir la imagen en un Blob para almacenarla
        Blob fotoPerfil;
        try {
            fotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(imgUser.getImage()));
        } catch (Exception e) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error en imagen", "No se pudo procesar la imagen.");
            return;
        }

        // Actualiza la información del usuario actual con los datos ingresados
        currentUser.setNombre(nombre);
        currentUser.setApellidos(apellidos);
        currentUser.setCorreoElectronico(correoElectronico);
        currentUser.setContrasenia(contrasenia);
        currentUser.setIdRol(1); // Establecer rol como Administrador
        currentUser.setFotoPerfil(fotoPerfil);

        // Guarda los cambios en la base de datos
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updateUser(currentUser);

        if (success) {
            // Notifica al usuario que los datos se actualizaron correctamente
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Los datos del usuario han sido actualizados correctamente.");
        } else {
            // Muestra mensaje de error si no se pudieron guardar los cambios
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los cambios. Verifica los datos e inténtalo nuevamente.");
        }
    }

    /**
     * Método que recibe los datos del usuario actual desde la base de datos y
     * los establece en la vista correspondiente.
     *
     * @param userId - ID del usuario actual que se desea cargar.
     */
    public void setUserData(int userId) {
        UserDAO userDAO = new UserDAO(); // Instancia para acceder a los datos del usuario
        User user = userDAO.getUserById(userId); // Obtiuene los datos del usuario desde la base de datos

        if (user != null) {
            this.currentUser = user; // Guarda el usuario en la variable global

            // Configura los campos de la vista con los datos del usuario
            fieldName.setText(user.getNombre());
            fieldSurname.setText(user.getApellidos());
            fieldMail.setText(user.getCorreoElectronico());
            fieldPassword.setText(user.getContrasenia());
            // cmbRol.getSelectionModel().select(user.getIdRol() - 1); // Seleccionar rol correspondiente (comentado si no es usado)

            // Intenta cargar la imagen del usuario si existe
            if (user.getFotoPerfil() != null) {
                try {
                    Image image = new Image(user.getFotoPerfil().getBinaryStream());
                    imgUser.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error al cargar la imagen del usuario: " + e.getMessage());
                }
            } else {
                imgUser.setImage(null); // Establecer como vacío o usar una imagen predeterminada
            }
        } else {
            // Maneja el caso en que el usuario no se encuentra en la base de datos
            System.err.println("El usuario con ID " + userId + " no fue encontrado en la base de datos.");
        }
    }

}
