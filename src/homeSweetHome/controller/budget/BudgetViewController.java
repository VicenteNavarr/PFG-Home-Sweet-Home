/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller.budget;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.model.Budget;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class BudgetViewController implements Initializable {

    @FXML
    private TableView<Budget> tableViewSpent;
    @FXML
    private TableColumn<Budget, String> colSpentName;
    @FXML
    private TableColumn<Budget, String> colCategory;
    @FXML
    private TableColumn<Budget, Double> colQuantity; // Representa el monto gastado
    @FXML
    private TableColumn<Budget, String> colPaymentMethod;
    @FXML
    private TableColumn<Budget, LocalDate> colDate;
    @FXML
    private TableColumn<Budget, String> colDescription;
    @FXML
    private Button btnOpenAddNewSpent;

    @FXML
    private ComboBox<String> filterCategory;
    @FXML
    private DatePicker filterStartDate;
    @FXML
    private DatePicker filterEndDate;
    @FXML
    private ComboBox<String> filterOrder;
    @FXML
    private Button btnApplyFilters;
    @FXML
    private Button btnClearFilters;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Enlazar las columnas con los atributos del modelo Budget
        colSpentName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Cargar los datos en la tabla
        loadSpents();

        // Opciones del ComboBox para categoría
        filterCategory.getItems().addAll("Alimentación", "Transporte", "Ocio", "Salud", "Vivienda", "Otros");

        // Opciones del ComboBox para ordenar
        filterOrder.getItems().addAll("Ascendente", "Descendente");

        // Listener para detectar clics en las filas de la tabla
        tableViewSpent.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableViewSpent.getSelectionModel().getSelectedItem() != null) {
                Budget selectedSpent = tableViewSpent.getSelectionModel().getSelectedItem();
                openUpdateSpentView(selectedSpent);
            }
        });

    }

    public void loadSpents() {
        BudgetDAO budgetDAO = new BudgetDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Recupera el ID del grupo del usuario actual

        List<Budget> gastos = budgetDAO.getBudgetsByGroup(groupId); // Obtén los gastos de la base de datos

        tableViewSpent.getItems().setAll(gastos); // Llena la tabla con los datos obtenidos
    }

    /**
     * Abre la ventana para crear un nuevo gasto.
     *
     * @param event - Evento activado al presionar el botón "Nuevo gasto".
     */
    @FXML
    private void openAddNewSpent(ActionEvent event) {

        try {
            // Carga la vista CreateSpentView desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/budget/CreateSpentView.fxml"));
            Parent root = loader.load();

            // Obtén el controlador de la nueva vista
            CreateSpentViewController createSpentController = loader.getController();

            // Pasa la referencia del controlador principal
            createSpentController.setBudgetViewController(this);

            // Configura una nueva ventana para la vista de creación
            Stage stage = new Stage();
            stage.setTitle("Añadir Nuevo Gasto"); // Título de la ventana
            stage.setScene(new Scene(root)); // Configura la escena
            stage.initModality(Modality.WINDOW_MODAL); // Establece la ventana como modal
            stage.initOwner(btnOpenAddNewSpent.getScene().getWindow()); // Asocia la ventana actual como propietaria
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre
        } catch (IOException e) {
            // Registra un error en caso de problemas al cargar la vista
            System.err.println("Error al cargar la vista CreateSpentView: " + e.getMessage());
        }
    }

    /**
     * Abre la ventana para actualizar un gasto seleccionado.
     *
     * @param spent El gasto seleccionado en la tabla.
     */
    private void openUpdateSpentView(Budget spent) {
        try {
            // Carga la vista UpdateSpentView desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/budget/UpdateSpentView.fxml"));
            Parent root = loader.load();

            // Obtén el controlador de la nueva vista
            UpdateSpentViewController updateSpentController = loader.getController();

            // Pasa el gasto seleccionado al controlador de la nueva vista
            updateSpentController.setBudget(spent);

            // Pasa la referencia del controlador principal para actualizaciones
            updateSpentController.setBudgetViewController(this);

            // Configura una nueva ventana para la vista de actualización
            Stage stage = new Stage();
            stage.setTitle("Actualizar Gasto"); // Título de la ventana
            stage.setScene(new Scene(root)); // Configura la escena
            stage.initModality(Modality.WINDOW_MODAL); // Establece la ventana como modal
            stage.initOwner(tableViewSpent.getScene().getWindow()); // Asocia la ventana actual como propietaria
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre
        } catch (IOException e) {
            // Registra un error en caso de problemas al cargar la vista
            System.err.println("Error al cargar la vista UpdateSpentView: " + e.getMessage());
        }
    }

    /**
     * Método para aplicar filtros en la tabal
     *
     * @param event
     */
    @FXML
    private void applyFilters(ActionEvent event) {
        BudgetDAO budgetDAO = new BudgetDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId();

        // Obtener los valores de los filtros
        String categoria = filterCategory.getSelectionModel().getSelectedItem();
        LocalDate inicio = filterStartDate.getValue();
        LocalDate fin = filterEndDate.getValue();
        String orden = filterOrder.getSelectionModel().getSelectedItem();

        // Obtener todos los registros
        List<Budget> gastos = new ArrayList<>(budgetDAO.getBudgetsByGroup(groupId)); // Convertir la lista original en mutable

        // Aplicar filtro por categoría
        if (categoria != null) {
            gastos = gastos.stream()
                    .filter(gasto -> gasto.getCategoria().equals(categoria))
                    .collect(Collectors.toCollection(ArrayList::new)); // Lista mutable
        }

        // Aplicar filtro por rango de fechas
        if (inicio != null && fin != null) {
            gastos = gastos.stream()
                    .filter(gasto -> (gasto.getFecha().isAfter(inicio) || gasto.getFecha().isEqual(inicio))
                    && (gasto.getFecha().isBefore(fin) || gasto.getFecha().isEqual(fin)))
                    .collect(Collectors.toCollection(ArrayList::new)); // Lista mutable
        }

        // Ordenar por importe
        if (orden != null) {
            if (orden.equals("Ascendente")) {
                gastos.sort((g1, g2) -> Double.compare(g1.getMonto(), g2.getMonto())); // Orden ascendente
            } else if (orden.equals("Descendente")) {
                gastos.sort((g1, g2) -> Double.compare(g2.getMonto(), g1.getMonto())); // Orden descendente
            }
        }

        // Mostrar los resultados filtrados en la tabla
        tableViewSpent.getItems().setAll(gastos);
    }

    @FXML
    private void clearFilters(ActionEvent event) {
        // Limpiar los filtros
        filterCategory.getSelectionModel().clearSelection();
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        filterOrder.getSelectionModel().clearSelection();

        // Recargar todos los datos en la tabla
        loadSpents();
    }

}
