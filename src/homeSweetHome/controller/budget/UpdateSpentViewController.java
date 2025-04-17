package homeSweetHome.controller.budget;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.model.Budget;
import homeSweetHome.utils.AlertUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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

    private Budget budget; // El gasto a modificar
    private BudgetViewController budgetViewController; // Referencia al controlador principal

    /**
     * Método para inicializar el controlador.
     */
    @FXML
    public void initialize() {
        // Opciones del ComboBox
        Category.getItems().addAll("Alimentación", "Transporte", "Ocio", "Salud", "Vivienda", "Otros");
        PaymentMethod.getItems().addAll("Efectivo", "Tarjeta");
    }

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
            // Actualizar los datos del gasto
            budget.setNombre(fieldSpentName.getText().trim());
            budget.setMonto(Double.parseDouble(fieldSpentQuantity.getText().trim()));
            budget.setMetodoPago(PaymentMethod.getSelectionModel().getSelectedItem());
            budget.setCategoria(Category.getSelectionModel().getSelectedItem());
            budget.setFecha(Date.getValue());
            budget.setDescripcion(fieldSpentDescription.getText().trim());

            // Actualizar en la base de datos
            BudgetDAO budgetDAO = new BudgetDAO();
            boolean success = budgetDAO.updateBudget(budget);

            if (success) {
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El gasto se ha actualizado correctamente.");
                budgetViewController.loadSpents(); // Refresca la tabla en el controlador principal
                ((Stage) btnUpdateSpent.getScene().getWindow()).close(); // Cierra la ventana
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar actualizar el gasto.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al intentar actualizar el gasto.");
        }
    }

    /**
     * Elimina el gasto de la base de datos y refresca la tabla principal.
     */
    @FXML
    private void deleteSpent() {
        try {
            BudgetDAO budgetDAO = new BudgetDAO();
            boolean success = budgetDAO.deleteBudget(budget.getId());

            if (success) {
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El gasto se ha eliminado correctamente.");
                budgetViewController.loadSpents(); // Refresca la tabla en el controlador principal
                ((Stage) btnDeleteSpent.getScene().getWindow()).close(); // Cierra la ventana
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar eliminar el gasto.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al intentar eliminar el gasto.");
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
