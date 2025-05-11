/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller.user;

import homeSweetHome.controller.MainViewController;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.utils.LanguageManager;
import java.io.File;
import java.net.URL;
import java.sql.Blob;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    @FXML
    private Label settingsTitle;
    @FXML
    private Label transName;
    @FXML
    private Label transSurname;
    @FXML
    private Label transMail;
    @FXML
    private Label transPass;

    private ComboBox<?> cmbRol; // ScrollPane que envuelve el contenedor

    private UserViewController userViewController;

    private MainViewController mainViewController;

    private User currentUser; // Variable para almacenar el usuario actual


    /**
     * Asigna el controlador de la vista de usuario.
     *
     * @param userViewController el controlador de la vista de usuario a
     * establecer
     */
    public void setUserViewController(UserViewController userViewController) {
        this.userViewController = userViewController;
    }

    // Método para recibir la referencia de MainViewController - para carga de foto en main
    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
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
        btnCreate.setText(languageManager.getTranslation("updateUserSettings"));
        btnCancel.setText(languageManager.getTranslation("cancel"));
        btnLoadImage.setText(languageManager.getTranslation("loadImage"));
        transName.setText(languageManager.getTranslation("transName"));
        transSurname.setText(languageManager.getTranslation("transSurname"));
        transMail.setText(languageManager.getTranslation("transMail"));
        transPass.setText(languageManager.getTranslation("transPass"));

        settingsTitle.setText(languageManager.getTranslation("settingsTitle"));

        System.out.println("Botón 'updateUserSettings': " + btnCreate.getText());
        System.out.println("Botón 'cancel': " + btnCancel.getText());
        System.out.println("Botón 'loadImage': " + btnLoadImage.getText());

        // Traducción de campos de texto (`promptText`)
        System.out.println("Etiqueta 'promptUserName': " + fieldName.getPromptText());
        System.out.println("Etiqueta 'promptUserSurname': " + fieldSurname.getPromptText());
        System.out.println("Etiqueta 'promptUserMail': " + fieldMail.getPromptText());
        System.out.println("Etiqueta 'promptUserPassword': " + fieldPassword.getPromptText());

        // Traducción del `ComboBox` de roles 
        if (cmbRol != null) {

            cmbRol.setPromptText(languageManager.getTranslation("promptUserRole"));
            System.out.println("PromptText de 'cmbRol': " + cmbRol.getPromptText());
        }

        // Refrescar UI para aplicar los cambios visualmente
        Platform.runLater(() -> fieldName.getScene().getWindow().sizeToScene());

        System.out.println("Traducciones aplicadas correctamente en CurrentUserSettingsViewController.");
    }

/////////////////////////////////FIN IDIOMAS///////////////////////////////////////////// 
    
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

                // Carga la imagen seleccionada y guarda en currentUser
                Image image = new Image(file.toURI().toString());
                imgUser.setImage(image);

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

                // Convierte la imagen predeterminada y guarda en currentUser
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

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, completa todos los campos e incluye una imagen.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete fields", "Please complete all fields and include an image.");

            }

            return;
        }

        // Intenta convertir la imagen en un Blob para almacenarla
        Blob fotoPerfil;

        try {

            fotoPerfil = new javax.sql.rowset.serial.SerialBlob(ImageUtils.convertImageToBlob(imgUser.getImage()));

        } catch (Exception e) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error en imagen", "No se pudo procesar la imagen.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Image error", "The image could not be processed.");

            }

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

        // Aquí es donde actualizamos la imagen en MainViewController**
        if (success && mainViewController != null) {
            mainViewController.setUserImageFromDatabase(userDAO);
        }

        if (success) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                // Notifica al usuario que los datos se actualizaron correctamente
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Los datos del usuario han sido actualizados correctamente.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                // Notifica al usuario que los datos se actualizaron correctamente
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "The user's data has been updated successfully.");

            }

        } else {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                // Muestra mensaje de error si no se pudieron guardar los cambios
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los cambios. Verifica los datos e inténtalo nuevamente.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                // Muestra mensaje de error si no se pudieron guardar los cambios
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "The changes could not be saved. Check the data and try again.");

            }

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
