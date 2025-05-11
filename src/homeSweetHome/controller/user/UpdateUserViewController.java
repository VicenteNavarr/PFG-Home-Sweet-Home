package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.utils.LanguageManager;
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
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class UpdateUserViewController implements Initializable {

    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldSurname;
    @FXML
    private TextField fieldMail;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private Button btnCancel;
    @FXML
    private ImageView imgUser;
    @FXML
    private Button btnLoadImage;
    @FXML
    private TextField fieldPassword;
    @FXML
    private Button btnSave;
    @FXML
    private Label updateUserTitle;

    private User currentUser; // Variable para almacenar el usuario actual

    private UserViewController userViewController;

    /**
     * Asigna el controlador de la vista de usuario.
     *
     * @param userViewController el controlador de la vista de usuario a
     * establecer
     */
    public void setUserViewController(UserViewController userViewController) {
        this.userViewController = userViewController;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
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
        btnSave.setText(languageManager.getTranslation("updateUser"));
        btnCancel.setText(languageManager.getTranslation("cancel"));
        btnLoadImage.setText(languageManager.getTranslation("loadImage"));

        updateUserTitle.setText(languageManager.getTranslation("updateUserTitle"));

        System.out.println("Botón 'updateUser': " + btnSave.getText());
        System.out.println("Botón 'cancel': " + btnCancel.getText());
        System.out.println("Botón 'loadImage': " + btnLoadImage.getText());

        // Traducción de campos de texto (`promptText`)
        fieldName.setPromptText(languageManager.getTranslation("promptUserName"));
        fieldSurname.setPromptText(languageManager.getTranslation("promptUserSurname"));
        fieldMail.setPromptText(languageManager.getTranslation("promptUserMail"));
        fieldPassword.setPromptText(languageManager.getTranslation("promptUserPassword"));

        System.out.println("Etiqueta 'promptUserName': " + fieldName.getPromptText());
        System.out.println("Etiqueta 'promptUserSurname': " + fieldSurname.getPromptText());
        System.out.println("Etiqueta 'promptUserMail': " + fieldMail.getPromptText());
        System.out.println("Etiqueta 'promptUserPassword': " + fieldPassword.getPromptText());

        // Traducción del `ComboBox` de roles
        cmbRol.getItems().setAll(
                languageManager.getTranslation("roleAdmin"),
                languageManager.getTranslation("roleConsultant")
        );

        cmbRol.setPromptText(languageManager.getTranslation("promptUserRole"));

        System.out.println("Opciones de 'cmbRol': " + cmbRol.getItems());

        // Refresca UI para aplicar los cambios visualmente
        Platform.runLater(() -> fieldName.getScene().getWindow().sizeToScene());

        System.out.println("Traducciones aplicadas correctamente en UpdateUserViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    
    /**
     * Guarda las modificaciones hechas al usuario seleccionado.
     *
     * @param event - Evento activado al presionar el botón "Guardar Cambios".
     */
    @FXML
    private void saveChanges(ActionEvent event) {

        // Valida que los campos obligatorios no estén vacíos
        String nombre = fieldName.getText();
        String apellidos = fieldSurname.getText();
        String correoElectronico = fieldMail.getText();
        String contrasenia = fieldPassword.getText();
        int idRol = cmbRol.getSelectionModel().getSelectedIndex() + 1; // Obtiene el índice del rol seleccionado

        // Valida campos obligatorios
        if (nombre.isEmpty() || apellidos.isEmpty() || correoElectronico.isEmpty() || contrasenia.isEmpty() || imgUser.getImage() == null) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, completa todos los campos e incluye una imagen.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete fields", "Please complete all fields and include an image.");

            }
            return;
        }

        // Convierte la imagen seleccionada en formato Blob
        Blob fotoPerfil;

        try {

            fotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(imgUser.getImage()));

        } catch (Exception e) {

            System.err.println("Error al convertir la imagen: " + e.getMessage());
            return;
        }

        // Crea un objeto User con los datos actualizados
        User updatedUser = new User(
                currentUser.getId(), // Conserva el ID del usuario actual
                nombre,
                apellidos,
                correoElectronico,
                contrasenia,
                idRol, // Usa el nuevo rol seleccionado
                fotoPerfil,
                currentUser.getIdGrupo() // Conserva el ID del grupo actual
        );

        // Usa el DAO para actualizar los datos en la base de datos
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updateUser(updatedUser);

        if (success) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                // Muestra un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                // Muestra un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully.");
            }

            System.out.println("Usuario actualizado exitosamente.");

            if (userViewController != null) {

                userViewController.loadUsers(); // Refresca la lista de usuarios en la vista
            }

            // Cierra la ventana actual
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } else {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                // Muestra un mensaje de error
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el usuario. Inténtalo de nuevo.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                // Muestra un mensaje de error
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "The user could not be updated. Please try again.");
            }

            System.err.println("Error al actualizar el usuario.");
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

        // Muestrael cuadro de diálogo y obtiene el archivo seleccionado
        File file = fileChooser.showOpenDialog(btnLoadImage.getScene().getWindow());

        if (file != null) {

            try {

                // Crea un objeto Image a partir del archivo seleccionado
                Image image = new Image(file.toURI().toString());
                imgUser.setImage(image);

                // Convierte la imagen a Blob para guardarla en currentUser
                Blob fotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(image));
                currentUser.setFotoPerfil(fotoPerfil);

                System.out.println("Imagen cargada correctamente: " + file.getName());

            } catch (Exception e) {

                // Muestra una alerta si ocurre un error al cargar la imagen
                System.err.println("Error al cargar la imagen: " + e.getMessage());
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen seleccionada. Inténtalo de nuevo.");
            }

        } else {

            // Usa una imagen predeterminada si no se seleccionó ningún archivo
            System.out.println("No se seleccionó ningún archivo. Usando imagen predeterminada.");
            imgUser.setImage(new Image(getClass().getResourceAsStream("/images/default-profile.png")));

            try {

                // Convierte la imagen predeterminada y guardarla en currentUser
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
     * Carga los datos del usuario desde la base de datos y los establece en la
     * vista.
     *
     * @param userId - ID del usuario que se desea cargar.
     */
    public void loadUserDataById(int userId) {

        UserDAO userDAO = new UserDAO(); // Instancia para acceder a los datos del usuario
        User user = userDAO.getUserById(userId); // Obtiene los datos del usuario desde la base de datos

        if (user != null) {

            this.currentUser = user; // Guarda el usuario en la variable global

            // Muestra los datos del usuario en los campos de texto
            fieldName.setText(user.getNombre());
            fieldSurname.setText(user.getApellidos());
            fieldMail.setText(user.getCorreoElectronico());
            fieldPassword.setText(user.getContrasenia());
            // cmbRol.getSelectionModel().select(user.getIdRol() - 1); // Selecciona el rol (comentado si no se usa)

            // Carga la imagen del usuario si está disponible
            if (user.getFotoPerfil() != null) {

                try {

                    Image image = new Image(user.getFotoPerfil().getBinaryStream());
                    imgUser.setImage(image);

                } catch (Exception e) {

                    System.err.println("Error al cargar la imagen del usuario: " + e.getMessage());
                }

            } else {

                imgUser.setImage(null); // Establece el campo como vacío si no hay imagen
            }

        } else {

            // Notifica si el usuario no fue encontrado
            System.err.println("El usuario con ID " + userId + " no fue encontrado en la base de datos.");
        }
    }

    /**
     * Método que recibe los datos de usuario y los establece en la vista (viene
     * de userItemVIewController).
     *
     * @param user - Objeto usuario que contiene los datos.
     */
    public void setUserData(User user) {

        this.currentUser = user; // Almacena el usuario recibido
        System.out.println("ID recibido en setUserData: " + user.getId());

        // Configura los campos de la vista con los datos del usuario
        fieldName.setText(user.getNombre());
        fieldSurname.setText(user.getApellidos());
        fieldMail.setText(user.getCorreoElectronico());
        System.out.println("Contraseña asignada al campo: " + user.getContrasenia());
        fieldPassword.setText(user.getContrasenia());
        cmbRol.getSelectionModel().select(user.getIdRol() - 1);

        if (user.getFotoPerfil() != null) {

            try {

                Image image = new Image(user.getFotoPerfil().getBinaryStream());
                imgUser.setImage(image);

            } catch (Exception e) {

                System.err.println("Error al cargar la imagen del usuario: " + e.getMessage());
            }
        }
    }
}
