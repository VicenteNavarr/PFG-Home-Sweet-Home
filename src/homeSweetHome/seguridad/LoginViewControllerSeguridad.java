///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package homeSweetHome.seguridad;
//
//import homeSweetHome.dataPersistence.UserDAO;
//import homeSweetHome.model.User;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ResourceBundle;
//import javafx.animation.TranslateTransition;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Hyperlink;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//
///**
// *
// * @author Usuario
// */
//public class LoginViewControllerSeguridad {
// 
//    
//     @FXML
//    private Hyperlink hl_OlvidoContrasenia;
//    @FXML
//    private Button btnLogin;
//    @FXML
//    private AnchorPane si_loginForm;
//    @FXML
//    private PasswordField login_contrasenia;
//    @FXML
//    private TextField login_usuario;
//    @FXML
//    private Button side_createBtn;
//    @FXML
//    private AnchorPane side_form;
//    @FXML
//    private Button btnRegistro;
//    @FXML
//    private AnchorPane su_signupForm;
//    @FXML
//    private Button side_alreadyHave;
//    @FXML
//    private TextField nombre;
//    @FXML
//    private PasswordField contrasenia;
//    @FXML
//    private TextField apellidos;
//    @FXML
//    private TextField mail;
//
//    /**
//     * Animación entre login y registro de usuario
//     *
//     * @param event
//     */
//    @FXML
//    public void switchForm(ActionEvent event) {
//
//        TranslateTransition slider = new TranslateTransition();
//
//        if (event.getSource() == side_createBtn) {
//
//            slider.setNode(side_form);
//            slider.setToX(300);
//            slider.setDuration(Duration.seconds(.5));
//
//            slider.setOnFinished((ActionEvent e) -> {
//                side_alreadyHave.setVisible(true);
//                side_createBtn.setVisible(false);
//            });
//
//            slider.play();
//
//        } else if (event.getSource() == side_alreadyHave) {
//
//            slider.setNode(side_form);
//            slider.setToX(0);
//            slider.setDuration(Duration.seconds(.5));
//
//            slider.setOnFinished((ActionEvent e) -> {
//                side_alreadyHave.setVisible(false);
//                side_createBtn.setVisible(true);
//            });
//
//            slider.play();
//
//        }
//
//    }
//
//    /**
//     * Metodo para registrar usuario nuevo
//     *
//     * @param event
//     */
//    @FXML
//    private void signUp(ActionEvent event) {
//
//        // Validar que los campos no estén vacíos
//        if (nombre.getText().isEmpty() || apellidos.getText().isEmpty() || mail.getText().isEmpty() || contrasenia.getText().isEmpty()) {
//            
//            showAlert(Alert.AlertType.ERROR, "Error de Registro", "Todos los campos son obligatorios.");
//            
//            return;
//        }
//
//        // Crear un nuevo usuario con los datos ingresados
//        User newUser = new User();
//        newUser.setNombre(nombre.getText());
//        newUser.setApellidos(apellidos.getText());
//        newUser.setCorreoElectronico(mail.getText());
//        newUser.setContrasenia(contrasenia.getText());
//        newUser.setIdRol(1); // Por defecto, el rol es Administrador (ID: 1)
//
//        // Intentar registrar al usuario utilizando el DAO
//        UserDAO userDAO = new UserDAO();
//        
//        if (userDAO.addUser(newUser)) {
//            
//            showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El usuario se ha registrado correctamente.");
//            clearForm(); // Limpiar los campos después del registro
//            
//        } else {
//            
//            showAlert(Alert.AlertType.ERROR, "Error de Registro", "Hubo un problema al registrar el usuario. Inténtalo nuevamente.");
//        }
//    }
//
//    /**
//     * Metodo para logear usuario existente
//     *
//     * @param event
//     */
//    @FXML
//    private void login(ActionEvent event) {
//
//        // Valida que los campos no estén vacíos
//        if (login_usuario.getText().isEmpty() || login_contrasenia.getText().isEmpty()) {
//            showAlert(Alert.AlertType.ERROR, "Error de Inicio de Sesión", "Todos los campos son obligatorios.");
//            return;
//        }
//
//        // Intenta autenticar al usuario utilizando el DAO
//        UserDAO userDAO = new UserDAO();
//        User user = userDAO.authenticateUser(login_usuario.getText(), login_contrasenia.getText());
//
//        if (user != null) {
//            showAlert(Alert.AlertType.INFORMATION, "Inicio de Sesión Exitoso",
//                    "¡Bienvenido, " + user.getNombre() + "!");
//            try {
//                // Carga la vista MainView si usuario existe
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/MainView.fxml"));
//                Parent mainView = loader.load();
//
//                // Crea una nueva escena
//                Scene mainScene = new Scene(mainView);
//
//                // Obtiene el escenario actual desde el evento
//                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//                // Establece la nueva escena en el escenario
//                stage.setScene(mainScene);
//                stage.setTitle("Sweet Home - Vista Principal");
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista principal.");
//            }
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Error de Inicio de Sesión", "Usuario o contraseña incorrectos.");
//        }
//    }
//
//    /**
//     * Metodo para limpiar el formulario
//     *
//     */
//    private void clearForm() {
//        nombre.clear();
//        apellidos.clear();
//        mail.clear();
//        contrasenia.clear();
//    }
//
//    /**
//     * Metodo para mostrar alerta
//     *
//     * @param alertType - AlertType
//     * @param title - String
//     * @param message - String
//     */
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    /**
//     * Initializes the controller class.
//     */
//    //@Override
//    public void initialize(URL url, ResourceBundle rb) {
//        // TODO
//        btnRegistro.setOnAction(this::signUp); // Asocia el método signUp con el botón de registro
//        btnLogin.setOnAction(this::login);   // Inicio de sesión
//    }
//
//    
//    
//    
//}
