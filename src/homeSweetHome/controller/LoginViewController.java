package homeSweetHome.controller;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ValidationUtils;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Controlador para la vista de Login y Registro.
 */
public class LoginViewController {

    @FXML
    private Hyperlink hl_OlvidoContrasenia;
    @FXML
    private Button btnLogin;
    @FXML
    private AnchorPane si_loginForm;
    @FXML
    private PasswordField login_contrasenia;
    @FXML
    private TextField login_usuario;
    @FXML
    private Button side_createBtn;
    @FXML
    private AnchorPane side_form;
    @FXML
    private Button btnRegistro;
    @FXML
    private AnchorPane su_signupForm;
    @FXML
    private Button side_alreadyHave;
    @FXML
    private TextField nombre;
    @FXML
    private PasswordField contrasenia;
    @FXML
    private TextField apellidos;
    @FXML
    private TextField mail;

    /**
     * Animación entre login y registro de usuario.
     *
     * @param event
     */
    @FXML
    public void switchForm(ActionEvent event) {

        TranslateTransition slider = new TranslateTransition();

        if (event.getSource() == side_createBtn) {

            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_createBtn.setVisible(false);
            });

            slider.play();

        } else if (event.getSource() == side_alreadyHave) {

            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));

            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_createBtn.setVisible(true);
            });

            slider.play();

        }

    }

    /**
     * Método para registrar usuario nuevo(Solo es requisito el nombre,
     * apellidos, mail y contrasenia) llama al metodo addFirstUser.
     *
     * @param event
     */
    @FXML
    private void signUp(ActionEvent event) {

        // Valida que los campos no estén vacíos
        if (nombre.getText().isEmpty() || apellidos.getText().isEmpty() || mail.getText().isEmpty() || contrasenia.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            alert.showAndWait();
            return;
        }

        // Valida el formato del correo electrónico utilizando ValidationUtils
        if (!ValidationUtils.isValidEmail(mail.getText())) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "El correo electrónico no tiene un formato válido.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            alert.showAndWait();
            return;
        }

        // Crea un nuevo usuario con los datos ingresados
        User newUser = new User();
        newUser.setNombre(nombre.getText());
        newUser.setApellidos(apellidos.getText());
        newUser.setCorreoElectronico(mail.getText());
        newUser.setContrasenia(contrasenia.getText());
        newUser.setIdRol(1); // Por defecto, el rol es Administrador (ID: 1)

        // Intenta registrar al usuario utilizando el DAO
        UserDAO userDAO = new UserDAO();

        if (userDAO.addFirstUser(newUser)) {

            showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El usuario se ha registrado correctamente.");
            loginAutomatically(newUser, event);

        } else {

            showAlert(Alert.AlertType.ERROR, "Error de Registro", "Hubo un problema al registrar el usuario. Inténtalo nuevamente.");
        }
    }

    /**
     * Método para logear usuario existente(nombre de usuario-mail y
     * contrasenia)
     *
     * @param event
     */
    @FXML
    private void login(ActionEvent event) {

        // Validar que los campos no estén vacíos
        if (login_usuario.getText().isEmpty() || login_contrasenia.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            alert.showAndWait();
            return;
        }

        // Intentar autenticar al usuario con el DAO
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticateUser(login_usuario.getText(), login_contrasenia.getText());

        if (user != null) {

            // Guardar el userId y userGroupId en la sesión actual
            CurrentSession session = CurrentSession.getInstance();
            session.setUserId(user.getId());
            session.setUserGroupId(user.getIdGrupo());
            session.setUserRole(user.getIdRol());//REVISAR!!

            // Mostrar alerta de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "¡Bienvenido, " + user.getNombre() + "!");
            alert.setHeaderText(null);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());

            Optional<ButtonType> result = alert.showAndWait(); // Captura qué botón se presionó

            if (result.isEmpty() || result.get() == ButtonType.CLOSE) {

                return; // Si el usuario cierra con la "X", detén la ejecución
            }

            try {

                // Cargar la vista MainView
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/MainView.fxml"));
                Parent mainView = loader.load();

                // Obtener el controlador y pasar el nombre del usuario
                MainViewController mainViewController = loader.getController();
                mainViewController.setUserName(user.getNombre());

                // Crear y configurar la nueva escena
                Scene mainScene = new Scene(mainView);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(mainScene);
                stage.setMinHeight(768);
                stage.setMinWidth(1280);
                stage.setTitle("Sweet Home - Vista Principal");
                stage.setResizable(true);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {

                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "No se pudo cargar la vista principal.");
                errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                errorAlert.showAndWait();
            }

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            alert.showAndWait();
        }
    }

    /**
     * Método para limpiar el formulario.
     */
    private void clearForm() {
        nombre.clear();
        apellidos.clear();
        mail.clear();
        contrasenia.clear();
    }

    /**
     * Método para mostrar alerta.
     *
     * @param alertType - AlertType
     * @param title - String
     * @param message - String
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Método para iniciar sesión automáticamente después del registro.
     *
     * @param user - Usuario registrado
     * @param event - Evento de registro
     */
    private void loginAutomatically(User user, ActionEvent event) {

        try {

            // Guarda el userId y userGroupId en la sesión actual
            CurrentSession session = CurrentSession.getInstance();
            session.setUserId(user.getId());
            session.setUserGroupId(user.getIdGrupo()); // Asigna el grupo al usuario registrado

            showAlert(Alert.AlertType.INFORMATION, "Inicio de Sesión Exitoso", "¡Bienvenido, " + user.getNombre() + "!");

            // Redirige a la vista principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/MainView.fxml"));
            Parent mainView = loader.load();

            // Obtiene el controlador asociado
            MainViewController mainViewController = loader.getController();

            // Pasa el nombre del usuario al controlador de la vista principal
            mainViewController.setUserName(user.getNombre());

            // Configura la nueva escena
            Scene mainScene = new Scene(mainView);

            // Obtiene el escenario actual desde el evento
            Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establece la nueva escena en el escenario
            mainStage.setScene(mainScene);
            mainStage.setTitle("Sweet Home - Vista Principal");
            mainStage.centerOnScreen();
            mainStage.setResizable(true);
            mainStage.show();

        } catch (IOException e) {

            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista principal.");

        }
    }

///Recuperación de mail////////
    /**
     * Envie mail con la contraseña del usuario
     *
     * @param recipientEmail
     * @param userPassword
     */
    private void sendEmailWithPassword(String recipientEmail, String userPassword) {

        final String senderEmail = "apphomesweethome@gmail.com";
        final String senderPassword = "mskh lydf tzib vguo";

        // Configuración del servidor SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Recuperación de Contraseña");
            message.setText("Tu contraseña es: " + userPassword + ". Recuerda ir a ajustes de usuario para cambiarla por una nueva si quieres");

            Transport.send(message);

            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Correo enviado correctamente.");
            System.out.println("Correo de recuperación enviado a: " + recipientEmail);

        } catch (Exception e) {

            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo enviar el correo.");
        }
    }

    /**
     * envia la contraseñapor mail al current user
     *
     * @param event
     */
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        
        UserDAO userDAO = new UserDAO();
        
        String email = login_usuario.getText(); //Obtiene el email directamente del formulario

        if (email != null && !email.isEmpty()) {
            
            User user = userDAO.getUserByEmail(email); //Método para obtener el usuario por correo
            
            if (user != null) {
                
                String userPassword = userDAO.getUserPasswordById(user.getId()); // Recupera contraseña con el `userId`
                
                if (userPassword != null && !userPassword.isEmpty()) {
                    
                    sendEmailWithPassword(email, userPassword); //Envía el email con la contraseña
                    System.out.println("Correo de recuperación enviado a: " + email);
                    
                } else {
                    
                    System.err.println("No se pudo obtener la contraseña del usuario.");
                }
                
            } else {
                
                System.err.println("No se encontró un usuario con ese correo.");
            }
            
        } else {
            
            System.err.println("El campo de correo está vacío.");
        }
    }

}
