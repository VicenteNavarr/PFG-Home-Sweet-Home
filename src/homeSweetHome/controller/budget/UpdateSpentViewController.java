package homeSweetHome.controller.budget;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.model.Budget;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateSpentViewController {

    @FXML
    private TextField fieldSpentName;
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
    private Button btnUpdateSpent;
    @FXML
    private Button btnDeleteSpent;
    @FXML
    private Button btnCancelSpent;
    @FXML
    private Label modifySpentTitle;

    private Budget budget; // El gasto a modificar

    private BudgetViewController budgetViewController; // Referencia al controlador principal

    /**
     * Método para inicializar el controlador.
     */
    public void initialize() {

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

        /// Accede directamente al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager no está disponible.");
            return;

        } else {

            System.out.println("Método updateTexts llamado en UpdateSpentView. Idioma actual: " + languageManager.getLanguageCode());
        }

        // Traducción de los botones principales
        System.out.println("Configurando textos de botones...");
        btnUpdateSpent.setText(languageManager.getTranslation("updateSpent"));
        btnCancelSpent.setText(languageManager.getTranslation("cancel"));
        btnDeleteSpent.setText(languageManager.getTranslation("deleteSpent"));
        System.out.println("Texto de btnUpdateSpent: " + btnUpdateSpent.getText());
        System.out.println("Texto de btnCancelSpent: " + btnCancelSpent.getText());
        System.out.println("Texto de btnDeleteSpent: " + btnDeleteSpent.getText());

        modifySpentTitle.setText(languageManager.getTranslation("modifySpentTitle"));

        // Traducción de los campos de texto con PromptText
        System.out.println("Configurando PromptText de TextField...");
        fieldSpentName.setPromptText(languageManager.getTranslation("promptSpentName"));
        fieldSpentQuantity.setPromptText(languageManager.getTranslation("promptSpentQuantity"));
        fieldSpentDescription.setPromptText(languageManager.getTranslation("promptSpentDescription"));
        System.out.println("PromptText de fieldSpentName: " + fieldSpentName.getPromptText());
        System.out.println("PromptText de fieldSpentQuantity: " + fieldSpentQuantity.getPromptText());
        System.out.println("PromptText de fieldSpentDescription: " + fieldSpentDescription.getPromptText());

        // Configuración dinámica del ComboBox: Categorías
        System.out.println("Configurando ComboBox de categorías...");
        Category.setPromptText(languageManager.getTranslation("promptCategory"));
        System.out.println("PromptText de Category: " + Category.getPromptText());
        Category.getItems().clear();
        System.out.println("Borrando elementos actuales de Category...");
        Category.getItems().addAll(
                languageManager.getTranslation("categoryFood"),
                languageManager.getTranslation("categoryTransport"),
                languageManager.getTranslation("categoryLeisure"),
                languageManager.getTranslation("categoryHealth"),
                languageManager.getTranslation("categoryHousing"),
                languageManager.getTranslation("categoryOthers")
        );
        
        System.out.println("Elementos actuales de Category: " + Category.getItems());

        // Configuración dinámica del ComboBox: Métodos de Pago
        System.out.println("Configurando ComboBox de métodos de pago...");
        PaymentMethod.setPromptText(languageManager.getTranslation("promptPaymentMethod"));
        System.out.println("PromptText de PaymentMethod: " + PaymentMethod.getPromptText());
        PaymentMethod.getItems().clear();
        System.out.println("Borrando elementos actuales de PaymentMethod...");
        PaymentMethod.getItems().addAll(
                languageManager.getTranslation("paymentCash"),
                languageManager.getTranslation("paymentCard")
        );
        
        System.out.println("Elementos actuales de PaymentMethod: " + PaymentMethod.getItems());

        System.out.println("Traducciones aplicadas correctamente en UpdateSpentView.");

    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    
    /**
     * Establece el gasto seleccionado.
     *
     * @param budget El gasto seleccionado en la tabla.
     */
    public void setBudget(Budget budget) {

        this.budget = budget;
        loadSpentData();
    }

    /**
     * Carga los datos del gasto en los campos de la vista.
     */
    private void loadSpentData() {

        fieldSpentName.setText(budget.getNombre());
        fieldSpentQuantity.setText(String.valueOf(budget.getMonto()));
        PaymentMethod.getSelectionModel().select(budget.getMetodoPago());
        Category.getSelectionModel().select(budget.getCategoria());
        Date.setValue(budget.getFecha());
        fieldSpentDescription.setText(budget.getDescripcion());
    }

    /**
     * Establece la referencia al controlador principal.
     *
     * @param budgetViewController El controlador principal.
     */
    public void setBudgetViewController(BudgetViewController budgetViewController) {

        this.budgetViewController = budgetViewController;
    }

    /**
     * Actualiza el gasto en la base de datos y refresca la tabla principal.
     */
    @FXML
    private void updateSpent() {

        try {
            
            // Usa el Singleton directamente para traducciones
            LanguageManager languageManager = LanguageManager.getInstance();

            if (languageManager == null) {

                System.err.println("Error: LanguageManager no está disponible.");
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gestión de idiomas no disponible.");
                return;
            }

            // Valida el método de pago seleccionado
            String metodoPagoSeleccionado = PaymentMethod.getSelectionModel().getSelectedItem();

            if (metodoPagoSeleccionado == null || metodoPagoSeleccionado.trim().isEmpty()) {

                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("paymentMethodNotSelected")
                );

                return;
            }

            // Valida la categoría seleccionada
            String categoriaSeleccionada = Category.getSelectionModel().getSelectedItem();

            if (categoriaSeleccionada == null || categoriaSeleccionada.trim().isEmpty()) {

                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("categoryNotSelected")
                );

                return;
            }

            // Valida los datos de los campos de texto
            String nombreGasto = fieldSpentName.getText().trim();

            if (nombreGasto.isEmpty()) {

                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("spentNameEmpty")
                );

                return;
            }

            String cantidadGasto = fieldSpentQuantity.getText().trim();
            double montoGasto;

            try {

                montoGasto = Double.parseDouble(cantidadGasto);

                if (montoGasto <= 0) {

                    throw new NumberFormatException("Monto inválido.");

                }

            } catch (NumberFormatException ex) {

                AlertUtils.showAlert(
                        Alert.AlertType.WARNING,
                        languageManager.getTranslation("invalidInput"),
                        languageManager.getTranslation("spentQuantityInvalid")
                );

                return;
            }

            // Actualiza el objeto Budget con los datos validados
            budget.setNombre(nombreGasto);
            budget.setMonto(montoGasto);
            budget.setMetodoPago(metodoPagoSeleccionado); // Almacena en el idioma de la interfaz
            budget.setCategoria(categoriaSeleccionada); // Almacena en el idioma de la interfaz
            budget.setFecha(Date.getValue());
            budget.setDescripcion(fieldSpentDescription.getText().trim());

            // Actualiza en la base de datos
            BudgetDAO budgetDAO = new BudgetDAO();
            boolean success = budgetDAO.updateBudget(budget);

            // Muestra alerta según el resultado de la actualización
            if (success) {

                AlertUtils.showAlert(
                        Alert.AlertType.INFORMATION,
                        languageManager.getTranslation("success"),
                        languageManager.getTranslation("spentUpdated")
                );

                budgetViewController.loadSpents(); // Refresca la tabla en el controlador principal
                ((Stage) btnUpdateSpent.getScene().getWindow()).close(); // Cierra la ventana

            } else {

                AlertUtils.showAlert(
                        Alert.AlertType.ERROR,
                        languageManager.getTranslation("error"),
                        languageManager.getTranslation("spentUpdateFailed")
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
            LanguageManager languageManager = LanguageManager.getInstance();

            AlertUtils.showAlert(
                    Alert.AlertType.ERROR,
                    languageManager.getTranslation("error"),
                    languageManager.getTranslation("unexpectedError")
            );
        }
    }

    /**
     * Elimina el gasto de la base de datos y refresca la tabla principal.
     */
    @FXML
    private void deleteSpent() {

        // Crea alerta de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());

        //alert.getDialogPane().getStylesheets().add(getClass().getResource("../homeSweetHome/utils/alertsCss.css").toExternalForm());
        alert.setTitle(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Confirmación" : "Confirmation");
        alert.setHeaderText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Eliminar gasto" : "Delete Expense");
        alert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "¿Seguro que desea eliminar este gasto?" : "Are you sure you want to delete this expense?");

        // Agrega botones manualmente
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                
                BudgetDAO budgetDAO = new BudgetDAO();
                boolean success = budgetDAO.deleteBudget(budget.getId());

                if (success) {
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                    successAlert.setTitle(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Éxito" : "Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "El gasto se ha eliminado correctamente." : "The expense has been deleted successfully.");
                    successAlert.showAndWait();

                    budgetViewController.loadSpents(); // Refresca la tabla en el controlador principal
                    ((Stage) btnDeleteSpent.getScene().getWindow()).close(); // Cierra la ventana

                } else {
                    
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Hubo un problema al intentar eliminar el gasto." : "There was a problem deleting the expense.");
                    errorAlert.showAndWait();
                }

            } catch (Exception e) {
                
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Se produjo un error al intentar eliminar el gasto." : "An error occurred while trying to delete the expense.");
                errorAlert.showAndWait();
            }
            
        } else {
            
            System.out.println(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Eliminación cancelada por el usuario." : "Deletion canceled by user.");
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
