package homeSweetHome.controller.budget;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.model.Budget;
import homeSweetHome.utils.AlertUtils;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
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

    private BudgetViewController budgetViewController; // Referencia al controlador principal

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


    /**
     * Método para inicializar el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Rellenar las opciones del ComboBox de categorías
        Category.getItems().addAll("Alimentación", "Transporte", "Ocio", "Salud", "Vivienda", "Otros");

        // Rellenar las opciones del ComboBox de métodos de pago
        PaymentMethod.getItems().addAll("Efectivo", "Tarjeta");
    }

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
            // Recuperar los valores de los campos de entrada
            String nombreGasto = fieldSpentName.getText().trim();
            String categoriaSeleccionada = Category.getSelectionModel().getSelectedItem();
            String metodoPagoSeleccionado = PaymentMethod.getSelectionModel().getSelectedItem();
            String montoTexto = fieldSpentQuantity.getText().trim();
            LocalDate fechaSeleccionada = Date.getValue();
            String descripcion = fieldSpentDescription.getText().trim();

            // Validar que los campos no estén vacíos
            if (nombreGasto.isEmpty() || categoriaSeleccionada == null || metodoPagoSeleccionado == null
                    || montoTexto.isEmpty() || fechaSeleccionada == null) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de continuar.");
                return;
            }

            // Validar que el monto sea un número válido
            double monto;
            try {
                monto = Double.parseDouble(montoTexto);
            } catch (NumberFormatException e) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
                        "El monto debe ser un número válido.");
                return;
            }

            // Crear el nuevo gasto
            Budget nuevoGasto = new Budget(
                    0, // ID (se generará automáticamente)
                    nombreGasto, // Nombre del gasto
                    categoriaSeleccionada, // Categoría seleccionada
                    monto, // Monto
                    metodoPagoSeleccionado, // Método de pago
                    fechaSeleccionada, // Fecha seleccionada
                    descripcion, // Descripción
                    CurrentSession.getInstance().getUserGroupId() // ID del grupo del usuario
            );

            // Guardar el gasto en la base de datos
            BudgetDAO budgetDAO = new BudgetDAO();
            boolean success = budgetDAO.addBudget(nuevoGasto);

            if (success) {
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito",
                        "El gasto se ha creado correctamente.");

                // Refresca la tabla en el controlador principal
                if (budgetViewController != null) {
                    budgetViewController.loadSpents();
                }

                ((Stage) btnCreateSpent.getScene().getWindow()).close(); // Cierra la ventana
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al intentar crear el gasto.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                    "Se produjo un error al intentar crear el gasto.");
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
