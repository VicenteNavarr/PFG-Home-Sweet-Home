package homeSweetHome.controller.user;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import static homeSweetHome.utils.AlertUtils.showAlert;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.utils.LanguageManager;
import homeSweetHome.utils.ValidationUtils;
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
    @FXML
    private Label newUserTitle;

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
        btnCreate.setText(languageManager.getTranslation("createUser"));
        btnCancel.setText(languageManager.getTranslation("cancel"));
        btnLoadImage.setText(languageManager.getTranslation("loadImage"));

        newUserTitle.setText(languageManager.getTranslation("newUserTitle"));

        System.out.println("Botón 'createUser': " + btnCreate.getText());
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

        // Refrescar UI para aplicar los cambios visualmente
        Platform.runLater(() -> fieldName.getScene().getWindow().sizeToScene());

        System.out.println("Traducciones aplicadas correctamente en CreateUserViewController.");
    }

/////////////////////////////////FIN IDIOMAS///////////////////////////////////////////// 
    
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

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, completa todos los campos e incluye una imagen.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete fields", "Please complete all fields and include an image.");

            }
            return;
        }

        // Validaa el formato del correo electrónico utilizando ValidationUtils
        if (!ValidationUtils.isValidEmail(fieldMail.getText())) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                showAlert(Alert.AlertType.ERROR, "Error de Registro", "El correo electrónico no tiene un formato válido.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                showAlert(Alert.AlertType.ERROR, "Register Error", "Mail invalid format.");
            }
            return;
        }

        // Comprueba si el usuario ya existe utilizando el correo electrónico
        if (UserDAO.userExists(correoElectronico)) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error de Registro", "El usuario ya existe.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Registration Error", "The user already exists.");
            }

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
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado exitosamente.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Sucess", "User successfully created.");
            }

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
