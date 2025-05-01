/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller;

import homeSweetHome.controller.budget.BudgetViewController;
import homeSweetHome.controller.event.EventItemViewController;
import homeSweetHome.controller.event.EventViewController;
import homeSweetHome.controller.meal.MealViewController;
import homeSweetHome.controller.purchase.PurchaseViewController;
import homeSweetHome.controller.recipe.RecipeViewController;
import homeSweetHome.controller.task.TaskViewController;
import homeSweetHome.controller.user.CurrentUserSettingsViewController;
import homeSweetHome.controller.user.UserViewController;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.User;
import homeSweetHome.socket.Server;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.utils.LanguageManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class MainViewController implements Initializable {

    @FXML
    private AnchorPane viewContainer;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button btnLoadControlPanelView;
    @FXML
    private Button btnLoadUserView;
    @FXML
    private Button btnLoadMealView;
    @FXML
    private Button btnLoadPurchaseView;
    @FXML
    private Button btnLoadTaskView;
    @FXML
    private Button btnLoadEventView;
    @FXML
    private Button btnLoadBudgetView;
    @FXML
    private Label lblHola;
    @FXML
    private ImageView userImage;
    @FXML
    private MenuItem btnSettings;
    @FXML
    private MenuItem btnCloseSession;
    @FXML
    private MenuItem btnExitApp;
    @FXML
    private Button btnLoadRecipes;
    @FXML
    private ComboBox<String> languageSelector;

    private Server server; // Referencia al servidor

    private Thread serverThread; // Referencia al hilo del servidor

    private User user; // Referencia al usuario actual

    private String userName;

    // Utiliza el Singleton para obtener la instancia del LanguageManager
    private final LanguageManager languageManager = LanguageManager.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Carga la imagen del usuario actual desde la base de datos
        UserDAO userDAO = new UserDAO();
        setUserImageFromDatabase(userDAO);

        int currentUserId = CurrentSession.getInstance().getUserId();
        User currentUser = userDAO.getUserById(currentUserId);

        // Inicializa idioma y textos dinámicos
        setupLanguageSelector(currentUser);
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////    
    
    /**
     * Configura el ComboBox de selección de idioma y actualiza los textos.
     */
    private void setupLanguageSelector(User currentUser) {
        
        // Agrega opciones de idiomas al ComboBox
        languageSelector.getItems().addAll("Español", "English");
        languageSelector.setValue("Español"); // Idioma predeterminado

        // Configura el idioma inicial en el LanguageManager
        languageManager.setLanguage("es");
        updateTexts(currentUser);

        // Listener para cambios en el ComboBox de idiomas
        languageSelector.setOnAction(event -> {
            String selectedLanguage = languageSelector.getValue().equals("English") ? "en" : "es";
            languageManager.setLanguage(selectedLanguage);
            updateTexts(currentUser); // Actualiza los textos de la interfaz
        });
    }

    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts(User currentUser) {
        lblHola.setText(languageManager.getTranslation("greeting") + " " + currentUser.getNombre());
        btnLoadControlPanelView.setText(languageManager.getTranslation("panel"));
        btnLoadUserView.setText(languageManager.getTranslation("users"));
        btnLoadMealView.setText(languageManager.getTranslation("meals"));
        btnLoadPurchaseView.setText(languageManager.getTranslation("purchase"));
        btnLoadTaskView.setText(languageManager.getTranslation("tasks"));
        btnLoadEventView.setText(languageManager.getTranslation("events"));
        btnLoadBudgetView.setText(languageManager.getTranslation("budget"));
        btnLoadRecipes.setText(languageManager.getTranslation("recipes"));
        btnSettings.setText(languageManager.getTranslation("settings"));
        btnCloseSession.setText(languageManager.getTranslation("closeSession"));
        btnExitApp.setText(languageManager.getTranslation("exit"));
    }
    
/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////    
    


///////////////CARGA DE VISTAS -> ACCIÓN BOTONES/////////////////////////////////////////////
    /**
     * Método llamado cuando se hace clic en el botón "btnLoadControlPanelView".
     * Carga y muestra la vista ControlPanelView.fxml en el centro del
     * BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadControlPanelView(ActionEvent event) {

        try {

            AnchorPane root = FXMLLoader.load(getClass().getResource("/homeSweetHome/view/ControlPanelView.fxml"));
            viewContainer.getChildren().setAll(root);

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {

            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadUserView". Carga y
     * muestra la vista UserView.fxml en el centro del BorderPane.
     *
     * @param event
     *
     */
    @FXML
    private void loadUserView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/user/UserView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            UserViewController userController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadMealView". Carga y
     * muestra la vista MealView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadMealView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/meal/MealView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            MealViewController mealController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadPurchaseView".
     * Carga y muestra la vista PurchaseView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadPurchaseView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/PurchaseView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            PurchaseViewController purchaseController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadTaskView". Carga y
     * muestra la vista TaskView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadTaskView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/task/TaskView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            TaskViewController taskController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadEventView". Carga
     * y muestra la vista EventView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadEventView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/event/EventView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            EventViewController eventController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadBudgetView". Carga
     * y muestra la vista BudgetView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadBudgetView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/budget/BudgetView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            BudgetViewController budgetController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método llamado cuando se hace clic en el botón "btnLoadBudgetView". Carga
     * y muestra la vista BudgetView.fxml en el centro del BorderPane.
     *
     * @param event
     */
    @FXML
    private void LoadRecipeView(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/RecipeView.fxml"));

            // Carga la vista y obtiene el nodo raíz
            AnchorPane root = loader.load();

            // Obtiene el controlador asociado a la vista cargada
            RecipeViewController recipeController = loader.getController();

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Método para cargar y establecer la imagen de perfil del usuario actual
     * desde la base de datos.
     *
     * @param userDAO - Instancia de UserDAO para acceder a los datos del
     * usuario.
     */
    public void setUserImageFromDatabase(UserDAO userDAO) {
        
        // Obtener el ID del usuario actual desde la sesión
        int currentUserId = CurrentSession.getInstance().getUserId();
        System.out.println("Obteniendo usuario con ID: " + currentUserId);

        try {
            // Obtiene el usuario desde la base de datos
            User currentUser = userDAO.getUserById(currentUserId);

            if (currentUser != null && currentUser.getFotoPerfil() != null) {
                
                // Carga la imagen desde el Blob
                InputStream inputStream = currentUser.getFotoPerfil().getBinaryStream();
                Image userImg = new Image(inputStream);
                userImage.setImage(userImg); // Muestra la imagen
                System.out.println("Imagen cargada correctamente para el usuario: " + currentUser.getNombre());
                
            } else {
                
                // Imagen predeterminada si no hay foto de perfil
                System.out.println("El usuario actual no tiene una imagen de perfil.");
                userImage.setImage(new Image(getClass().getResourceAsStream("/images/add-image.png")));
            }
            
        } catch (Exception e) {
            
            System.err.println("Error al cargar la imagen del usuario: " + e.getMessage());
            // Carga imagen predeterminada si hay un error
            userImage.setImage(new Image(getClass().getResourceAsStream("/images/add-image.png")));
        }

        // Aplica recorte circular a la imagen
        ImageUtils.setClipToCircle(userImage);
    }

    /**
     * Metodo para establecer el nombre del usuario en la parte superior de la
     * vista
     *
     * @param userName
     */
    public void setUserName(String userName) {

        lblHola.setText("Hola, " + userName + "!");

    }

    /**
     * Lleva a la pantalla de ajustes del usuario actual(update de la bbdd )
     *
     * @param event
     */
    @FXML
    private void settings(ActionEvent event) {

        try {
            
            // Crea una instancia explícita de FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/user/CurrentUserSettingsView.fxml"));

            // Carga la vista y obtener el nodo raíz
            AnchorPane root = loader.load();

            // Obtieneel controlador de la vista cargada
            CurrentUserSettingsViewController controller = loader.getController();

            // Pasa el ID del usuario actual al controlador
            int currentUserId = CurrentSession.getInstance().getUserId(); // Obtiene el ID desde la sesión actual
            controller.setUserData(currentUserId); // Método en CurrentUserSettingsViewController

            // Establece la vista cargada en el centro del BorderPane
            rootPane.setCenter(root);

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Cierra la vista actual y abre la vista de login
     *
     * @param event
     */
    @FXML
    private void closeSession(ActionEvent event) {
        try {
            
            // Carga el archivo FXML de la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/LoginView.fxml"));
            Parent root = loader.load();

            // Crea una nueva escena y asigna a un nuevo Stage
            Stage newStage = new Stage();
            newStage.setScene(new javafx.scene.Scene(root));
            newStage.setTitle("Inicio de Sesión");
            newStage.show();

            // Cierra la ventana actual
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException ex) {
            
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cierra el programa system.exit(0)
     *
     * @param event
     */
    @FXML
    private void exitApp(ActionEvent event) {

        System.out.println("Saliendo del programa");
        System.exit(0);
    }

   

}
