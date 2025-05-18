/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller.budget;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.model.Budget;
import homeSweetHome.utils.LanguageManager;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
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
    @FXML
    private Label budgetTitle;
    @FXML
    private HBox HboxFiltros;
    @FXML
    private Label instructionBudget;

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos
    @FXML
    private Label labelTotalSpent;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnOpenAddNewSpent.setDisable(true);

        }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////

        // Enlaza las columnas con los atributos del modelo Budget
        colSpentName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Carga los datos en la tabla
        loadSpents();

        // Opciones del ComboBox para categoría
        //filterCategory.getItems().addAll("Alimentación", "Transporte", "Ocio", "Salud", "Vivienda", "Otros");
        // Opciones del ComboBox para ordenar
        //filterOrder.getItems().addAll("Ascendente", "Descendente");
        // Listener para detectar clics en las filas de la tabla
        tableViewSpent.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableViewSpent.getSelectionModel().getSelectedItem() != null) {
                Budget selectedSpent = tableViewSpent.getSelectionModel().getSelectedItem();
                openUpdateSpentView(selectedSpent);
            }
        });

    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("LanguageManager no está disponible.");
            return;
        }

        // Actualiza los textos dinámicamente
        filterCategory.setPromptText(languageManager.getTranslation("selectCategory"));
        filterOrder.setPromptText(languageManager.getTranslation("selectOrder"));
        filterStartDate.setPromptText(languageManager.getTranslation("startDate"));
        filterEndDate.setPromptText(languageManager.getTranslation("endDate"));
        btnOpenAddNewSpent.setText(languageManager.getTranslation("addSpent"));
        btnApplyFilters.setText(languageManager.getTranslation("applyFilters"));
        btnClearFilters.setText(languageManager.getTranslation("clearFilters"));
        instructionBudget.setText(languageManager.getTranslation("instructionBudget"));

        budgetTitle.setText(languageManager.getTranslation("budgetTitle"));

        // Actualiza las opciones del ComboBox
        filterCategory.getItems().clear();
        filterCategory.getItems().setAll(
                languageManager.getTranslation("food"),
                languageManager.getTranslation("transport"),
                languageManager.getTranslation("leisure"),
                languageManager.getTranslation("health"),
                languageManager.getTranslation("housing"),
                languageManager.getTranslation("other")
        );

        filterOrder.getItems().clear();
        filterOrder.getItems().setAll(
                languageManager.getTranslation("ascending"),
                languageManager.getTranslation("descending")
        );

        // Actualiza encabezados de la tabla
        colSpentName.setText(languageManager.getTranslation("spentName"));
        colCategory.setText(languageManager.getTranslation("category"));
        colQuantity.setText(languageManager.getTranslation("amount"));
        colPaymentMethod.setText(languageManager.getTranslation("paymentMethod"));
        colDate.setText(languageManager.getTranslation("date"));
        colDescription.setText(languageManager.getTranslation("description"));
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////
    
    /**
     * Carga la tabla con los gastos de la bbddd
     */
    public void loadSpents() {

        BudgetDAO budgetDAO = new BudgetDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Recupera el ID del grupo del usuario actual

        List<Budget> gastos = budgetDAO.getBudgetsByGroup(groupId); // Obtiene los gastos de la base de datos
        
        // Calcula el total de gastos sin filtros
        double totalSpent = gastos.stream().mapToDouble(Budget::getMonto).sum();
        labelTotalSpent.setText(String.format("Total: %.2f€", totalSpent));

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

            // Obtiene el controlador de la nueva vista
            CreateSpentViewController createSpentController = loader.getController();

            // Pasa la referencia del controlador principal
            createSpentController.setBudgetViewController(this);

            // Pasa el LanguageManager a la vista
            //createSpentController.setLanguageManager(this.languageManager);
            // Configura una nueva ventana para la vista de creación
            Stage stage = new Stage();
            stage.setTitle("Añadir Nuevo Gasto"); // Título de la ventana
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/budgetIconBlue.png")));
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

        //Si el usuarois es consultor, desactivamos
        if (role == 2) {
            System.out.println("Acceso denegado: los usuarios con rol 2 no pueden actualizar gastos.");
            return; // Sale del método sin abrir la vista
        }

        try {

            // Carga la vista UpdateSpentView desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/budget/UpdateSpentView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la nueva vista
            UpdateSpentViewController updateSpentController = loader.getController();

            // Pasa el gasto seleccionado al controlador de la nueva vista
            updateSpentController.setBudget(spent);

            // Pasa el LanguageManager a la vista
            //updateSpentController.setLanguageManager(this.languageManager);
            // Pasa la referencia del controlador principal para actualizaciones
            updateSpentController.setBudgetViewController(this);

            // Configura una nueva ventana para la vista de actualización
            Stage stage = new Stage();
            stage.setTitle("Actualizar Gasto"); // Título de la ventana
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/budgetIconBlue.png")));
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

        // Obtiene los valores de los filtros
        String categoria = filterCategory.getSelectionModel().getSelectedItem();
        LocalDate inicio = filterStartDate.getValue();
        LocalDate fin = filterEndDate.getValue();
        String orden = filterOrder.getSelectionModel().getSelectedItem();

        // Depuración: Imprime los valores seleccionados
        System.out.println("=== INICIO DE APLICAR FILTROS ===");
        System.out.println("Categoría seleccionada: " + categoria);
        System.out.println("Fecha de inicio seleccionada: " + inicio);
        System.out.println("Fecha de fin seleccionada: " + fin);
        System.out.println("Orden seleccionado: " + orden);

        // Traducción de categorías (inglés -> español)
        Map<String, String> categoryMap = Map.of(
                "Food", "Alimentación",
                "Transport", "Transporte",
                "Leisure", "Ocio",
                "Health", "Salud",
                "Housing", "Vivienda",
                "Other", "Otros"
        );

        // Traduce la categoría seleccionada al español
        String categoriaSpanish = categoria != null ? categoryMap.getOrDefault(categoria, categoria) : null;
        System.out.println("Categoría traducida al español: " + categoriaSpanish);

        // Obtiene todos los registros
        List<Budget> gastos = new ArrayList<>(budgetDAO.getBudgetsByGroup(groupId)); // Convierte la lista original en mutable

        // Depuración: Imprime las categorías de la base de datos
        System.out.println("Total de registros antes del filtrado: " + gastos.size());
        for (Budget gasto : gastos) {
            System.out.println("Categoría en la base de datos: " + gasto.getCategoria());
        }

        // Filtra por categoría
        if (categoriaSpanish != null) {

            System.out.println("Aplicando filtro de categoría: " + categoriaSpanish);
            gastos = gastos.stream()
                    .filter(gasto -> gasto.getCategoria().trim().equalsIgnoreCase(categoriaSpanish.trim()))
                    .collect(Collectors.toCollection(ArrayList::new)); // Lista mutable
            System.out.println("Total de registros después de filtrar por categoría: " + gastos.size());
        }

        // Aplica filtro por rango de fechas
        if (inicio != null && fin != null) {

            System.out.println("Aplicando filtro de rango de fechas: Desde " + inicio + " hasta " + fin);
            gastos = gastos.stream()
                    .filter(gasto -> (gasto.getFecha().isAfter(inicio) || gasto.getFecha().isEqual(inicio))
                    && (gasto.getFecha().isBefore(fin) || gasto.getFecha().isEqual(fin)))
                    .collect(Collectors.toCollection(ArrayList::new)); // Lista mutable
            System.out.println("Total de registros después de filtrar por rango de fechas: " + gastos.size());
        }

        //Ordena por importe
        if (orden != null) {

            if (orden.equals("Ascendente") || orden.equals("Ascending")) {

                gastos.sort((g1, g2) -> Double.compare(g1.getMonto(), g2.getMonto())); // Orden ascendente

            } else if (orden.equals("Descendente") || orden.equals("Descending")) {

                gastos.sort((g1, g2) -> Double.compare(g2.getMonto(), g1.getMonto())); // Orden descendente
            }
        }
        
        // Calcula el total de gastos filtrados
        double totalSpent = gastos.stream().mapToDouble(Budget::getMonto).sum();
        labelTotalSpent.setText(String.format("Total: %.2f€", totalSpent));

        // Muestra los resultados filtrados en la tabla
        System.out.println("Total de registros finales: " + gastos.size());
        tableViewSpent.getItems().setAll(gastos);

        System.out.println("=== FIN DE APLICAR FILTROS ===");
    }

    /**
     * Metodo limpia los filtros de apllyfilter
     *
     * @param event
     */
    @FXML
    private void clearFilters(ActionEvent event) {

        // Limpia los filtros
        filterCategory.getSelectionModel().clearSelection();
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        filterOrder.getSelectionModel().clearSelection();

        // Recarga todos los datos en la tabla
        loadSpents();
    }

}
