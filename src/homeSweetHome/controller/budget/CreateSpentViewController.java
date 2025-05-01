package homeSweetHome.controller.budget;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.model.Budget;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateSpentViewController implements Initializable {

    @FXML
    private ComboBox<String> Category;
    @FXML
    private TextField fieldSpentQuantity;
    @FXML
    private ComboBox<String> PaymentMethod;
    @FXML
    private DatePicker Date;
    @FXML
    private TextField fieldSpentDescription;
    @FXML
    private Button btnCreateSpent;
    @FXML
    private TextField fieldSpentName;
    @FXML
    private Button btnCancelSpent;

    private BudgetViewController budgetViewController; // Referencia al controlador principal

    //private LanguageManager languageManager;
    /**
     * Método para inicializar el controlador.
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
        // Acceder al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {
            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Configurar textos de los botones
        System.out.println("Configurando textos de botones...");
        btnCreateSpent.setText(languageManager.getTranslation("createSpent"));
        btnCancelSpent.setText(languageManager.getTranslation("cancel"));

        // Configurar PromptText de los campos de texto
        System.out.println("Configurando PromptText de TextField...");
        fieldSpentName.setPromptText(languageManager.getTranslation("promptSpentName"));
        fieldSpentQuantity.setPromptText(languageManager.getTranslation("promptSpentQuantity"));
        fieldSpentDescription.setPromptText(languageManager.getTranslation("promptSpentDescription"));

        // Configurar ComboBox: Categorías
        System.out.println("Configurando ComboBox de categorías...");
        Category.getItems().clear();
        Category.getItems().addAll(
                languageManager.getTranslation("categoryFood"),
                languageManager.getTranslation("categoryTransport"),
                languageManager.getTranslation("categoryLeisure"),
                languageManager.getTranslation("categoryHealth"),
                languageManager.getTranslation("categoryHousing"),
                languageManager.getTranslation("categoryOthers")
        );
        Category.setPromptText(languageManager.getTranslation("promptCategory"));

        // Configurar ComboBox: Métodos de Pago
        System.out.println("Configurando ComboBox de métodos de pago...");
        PaymentMethod.getItems().clear();
        PaymentMethod.getItems().addAll(
                languageManager.getTranslation("paymentCash"),
                languageManager.getTranslation("paymentCard")
        );
        PaymentMethod.setPromptText(languageManager.getTranslation("promptPaymentMethod"));
        // Configuración de DatePicker: Fecha
        Date.setPromptText(languageManager.getTranslation("promptDate")); 

        // Depuración
        System.out.println("Traducciones aplicadas correctamente en CreateSpentView.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////    
    /**
     * Método para establecer la referencia al BudgetViewController principal.
     *
     * @param budgetViewController - Controlador principal
     */
    public void setBudgetViewController(BudgetViewController budgetViewController) {
        this.budgetViewController = budgetViewController;
    }

    /**
     * Método para crear un nuevo gasto y guardarlo en la base de datos.
     *
     * @param event - Evento activado al presionar el botón "Crear".
     */
    @FXML
    private void createNewSpent(ActionEvent event) {
        
        try {
            // Acceder al Singleton para traducciones
            LanguageManager languageManager = LanguageManager.getInstance();

            if (languageManager == null) {
                
                System.err.println("Error: LanguageManager no está disponible.");
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gestión de idiomas no disponible.");
                return;
            }

            // Validar el método de pago seleccionado y mapear valores traducidos
            String metodoPagoSeleccionado = PaymentMethod.getSelectionModel().getSelectedItem();
            
            if (metodoPagoSeleccionado == null || metodoPagoSeleccionado.trim().isEmpty()) {
                
                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("paymentMethodNotSelected")
                );
                
                return;
            }

            if (metodoPagoSeleccionado.equals(languageManager.getTranslation("paymentCash"))) {
                
                metodoPagoSeleccionado = "Efectivo";
                
            } else if (metodoPagoSeleccionado.equals(languageManager.getTranslation("paymentCard"))) {
                
                metodoPagoSeleccionado = "Tarjeta";
                
            } else {
                
                AlertUtils.showAlert(
                        Alert.AlertType.ERROR,
                        languageManager.getTranslation("error"),
                        "Método de pago no válido: " + metodoPagoSeleccionado
                );
                return;
            }

            // Validar la categoría seleccionada
            String categoriaSeleccionada = Category.getSelectionModel().getSelectedItem();
            
            if (categoriaSeleccionada == null || categoriaSeleccionada.trim().isEmpty()) {
                
                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("categoryNotSelected")
                );
                
                return;
            }

            // Validar el nombre del gasto
            String nombreGasto = fieldSpentName.getText().trim();
            
            if (nombreGasto.isEmpty()) {
                
                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("spentNameEmpty")
                );
                
                return;
            }

            // Validar y parsear la cantidad
            String cantidadGasto = fieldSpentQuantity.getText().trim();
            double montoGasto;
            
            try {
                
                montoGasto = Double.parseDouble(cantidadGasto);
                
                if (montoGasto <= 0) {
                    
                    throw new NumberFormatException();
                }
                
            } catch (NumberFormatException ex) {
                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("spentQuantityInvalid")
                );
                return;
            }

            // Crear un nuevo gasto con los datos validados
            Budget nuevoGasto = new Budget(
                    0, // ID inicial, suponiendo que la base de datos lo genera automáticamente
                    nombreGasto,
                    categoriaSeleccionada,
                    montoGasto,
                    metodoPagoSeleccionado,
                    Date.getValue(),
                    fieldSpentDescription.getText().trim(),
                    CurrentSession.getInstance().getUserGroupId()
            );

            // Agregar el gasto a la base de datos
            BudgetDAO budgetDAO = new BudgetDAO();
            boolean success = budgetDAO.addBudget(nuevoGasto);

            // Mostrar alerta según el resultado de la operación
            if (success) {
                
                AlertUtils.showAlert(
                        Alert.AlertType.INFORMATION,
                        languageManager.getTranslation("success"),
                        languageManager.getTranslation("spentCreated")
                );
                
                if (budgetViewController != null) {
                    
                    budgetViewController.loadSpents(); // Refrescar la tabla en el controlador principal
                }
                
                ((Stage) btnCreateSpent.getScene().getWindow()).close(); // Cerrar la ventana actual
                
            } else {
                
                AlertUtils.showAlert(
                        Alert.AlertType.ERROR,
                        languageManager.getTranslation("error"),
                        languageManager.getTranslation("spentCreateFailed")
                );
            }

        } catch (Exception e) {
            
            e.printStackTrace();
            LanguageManager languageManager = LanguageManager.getInstance(); // Reforzar acceso al Singleton
            
            AlertUtils.showAlert(
                    Alert.AlertType.ERROR,
                    languageManager.getTranslation("error"),
                    languageManager.getTranslation("unexpectedError")
            );
        }
    }

    /**
     * Método para cancelar y cerrar la ventana.
     *
     * @param event - Evento del botón.
     */
    @FXML
    private void cancel(ActionEvent event) {
        
        ((Stage) btnCancelSpent.getScene().getWindow()).close(); // Cierra la ventana actual
    }

}
