package homeSweetHome.controller;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanzadorMail;
import homeSweetHome.utils.PasswordRecoveryMailSender;
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
import javafx.scene.image.Image;
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

            Alert alert = new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios.\nAll the fields are required.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            alert.showAndWait();
            return;
        }

        // Valida el formato del correo electrónico utilizando ValidationUtils
        if (!ValidationUtils.isValidEmail(mail.getText())) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "El correo electrónico no tiene un formato válido.\nMail invalid format");
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

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "El usuario se ha registrado correctamente.\nThe user has been successfully registered.");
            successAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            successAlert.showAndWait();

            Alert loginAlert = new Alert(Alert.AlertType.INFORMATION, "Pulsa 'ya tengo una cuenta' y logeate!.\nClick 'I already have an account' and log in!.");
            loginAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            loginAlert.showAndWait();

        } else {

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error de Registro");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Hubo un problema al registrar el usuario - El mail ya está registrado. Inténtalo nuevamente.\nMail already exists.");
            errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
            errorAlert.showAndWait();

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

            Alert alert = new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios.\nAll the fields are required.");
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "¡Bienvenido/Welcome, " + user.getNombre() + "!");
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
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/HSHLogoCuadrado.png")));
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

            Alert alert = new Alert(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos.\nInvalid user or password.");
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
        String email = login_usuario.getText(); // Obtiene el email del formulario

        if (email != null && !email.isEmpty()) {

            User user = userDAO.getUserByEmail(email); // Obtiene el usuario por correo

            if (user != null) {

                String userPassword = userDAO.getUserPasswordById(user.getId()); // Recupera la contraseña

                if (userPassword != null && !userPassword.isEmpty()) {

                    LanzadorMail lanzador = new LanzadorMail();
                    lanzador.lanzarEnvioCorreoPass(email, userPassword);

                    System.out.println("Correo de recuperación enviado a: " + email);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Éxito");
                        alert.setHeaderText(null);
                        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                        alert.setContentText("El correo de recuperación ha sido enviado correctamente.\nThe recovery email has been sent successfully.");
                        alert.showAndWait();
                    });

                } else {

                    System.err.println("No se pudo obtener la contraseña del usuario.");

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("No se pudo obtener la contraseña del usuario.");
                        alert.showAndWait();
                    });
                }

            } else {

                System.err.println("No se encontró un usuario con ese correo.");

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontró un usuario con ese correo.\nNo user was found with that email.");
                    alert.showAndWait();
                });
            }

        } else {

            System.err.println("El campo de correo está vacío.");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                alert.setHeaderText(null);
                alert.setContentText("El campo de correo está vacío. Introduce un correo para poder enviar la contraseña.\nThe email field is empty. Please enter an email to send the password.");
                alert.showAndWait();
            });
        }
    }

}
