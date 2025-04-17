package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.dataPersistence.PurchaseDAO;
import homeSweetHome.model.Budget;
import homeSweetHome.model.Inventory;
import homeSweetHome.model.Product;
import homeSweetHome.model.Purchase;
import homeSweetHome.utils.AlertUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import java.sql.SQLException;
import java.util.Arrays;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

/**
 * Controlador para gestionar la vista de compras. Permite visualizar y
 * manipular el inventario, interactuar con el usuario y realizar
 * actualizaciones en la base de datos.
 */
public class PurchaseViewController {

//    // Tabla del inventario
//    @FXML
//    private TableView<Inventory> tableViewInventory;
//
//    @FXML
//    private TableColumn<Inventory, String> colProductName; // Columna para el nombre del producto
//
//    @FXML
//    private TableColumn<Inventory, Void> colQuantity; // Columna para "Cantidad Actual" con botones y valor
//
//    @FXML
//    private TableColumn<Inventory, Void> colMinQuantity; // Columna para "Cantidad Mínima" con botones y valor
//
//    @FXML
//    private TableColumn<Inventory, String> colCategory; // Columna para la categoría
//
//    @FXML
//    private TableView<Purchase> tableViewShoppingList;
//
//    @FXML
//    private TableColumn<Purchase, String> colShoppingProduct;
//
//    @FXML
//    private TableColumn<Purchase, Integer> colShoppingQuantity;
//
//    @FXML
//    private TableColumn<Inventory, String> colType; // Columna para mostrar el tipo en la tabla de inventario
//
//    @FXML
//    private TableColumn<Inventory, Void> colQuantityControls; // Columna para botones dinámicos
//
//    @FXML
//    private TableColumn<Purchase, String> colShoppingType; // Columna para mostrar el tipo en la tabla de lista de compras
//
//    // Lista interna que contiene los datos del inventario
//    private ObservableList<Inventory> inventoryList;
//
//    //Lista de la compra
//    private List<Purchase> shoppingCart;
//
//    // Lista interna que contiene los datos de la lista de la compra
//    private ObservableList<Purchase> shoppingList;
//    @FXML
//    private Button btnOpenAddNewProduct;
//    @FXML
//    private Button btnCompletePurchase;
    // Tabla del inventario
    @FXML
    private TableView<Product> tableViewInventory; // Ahora trabaja con Product

    @FXML
    private TableColumn<Product, String> colProductName; // Columna para el nombre del producto

    @FXML
    private TableColumn<Product, Void> colQuantity; // Columna para "Cantidad Actual" con botones y valor

    @FXML
    private TableColumn<Product, Void> colMinQuantity; // Columna para "Cantidad Mínima" con botones y valor

    @FXML
    private TableColumn<Product, String> colCategory; // Columna para la categoría

    @FXML
    private TableColumn<Product, String> colType; // Columna para mostrar el tipo en la tabla de inventario

    @FXML
    private TableColumn<Product, Void> colQuantityControls; // Columna para botones dinámicos

// Tabla de la lista de compras
    @FXML
    private TableView<Product> tableViewShoppingList; // Ahora trabaja con Product

    @FXML
    private TableColumn<Product, String> colShoppingProduct; // Columna para el nombre del producto en la lista de compras

    @FXML
    private TableColumn<Product, Integer> colShoppingQuantity; // Columna para la cantidad necesaria

    @FXML
    private TableColumn<Product, String> colShoppingType; // Columna para mostrar el tipo en la tabla de lista de compras

// Lista interna que contiene los datos del inventario
    private ObservableList<Product> inventoryList = FXCollections.observableArrayList();

// Lista interna que contiene los datos de la lista de compras
    private ObservableList<Product> shoppingList = FXCollections.observableArrayList();

    @FXML
    private Button btnOpenAddNewProduct; // Botón para añadir productos

    @FXML
    private Button btnCompletePurchase; // Botón para completar la compra

//    /**
//     * Inicializa los componentes de la vista al cargar. Configura las columnas
//     * de la tabla y carga los datos del inventario según el grupo del usuario
//     * conectado.
//     */
//    public void initialize() {
//        // Configura las columnas principales de inventario
//        colProductName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
//        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));
//        colType.setCellValueFactory(new PropertyValueFactory<>("tipo")); // Nueva columna para "Cantidad" o "Gramos"
//
//        // Configura los controles dinámicos para las columnas de cantidad y cantidad mínima
//        configureColumnWithControls(colQuantity, "cantidad");
//        configureColumnWithControls(colMinQuantity, "cantidadMinima");
//
//        // Configura las columnas principales de lista de compra
//        colShoppingProduct.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
//        colShoppingQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidadNecesaria"));
//        colShoppingType.setCellValueFactory(new PropertyValueFactory<>("tipo"));
//
//        // Configurar la columna de cantidades como editable
//        colShoppingQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        colShoppingQuantity.setOnEditCommit(event -> {
//            Purchase item = event.getRowValue();
//            item.setCantidadNecesaria(event.getNewValue()); // Actualizar el modelo con la nueva cantidad
//        });
//
//        // Hacer la tabla editable
//        tableViewShoppingList.setEditable(true);
//
//        shoppingCart = FXCollections.observableArrayList(); // Inicializa la lista de la compra
//
//        // Inicializa las listas de inventario y compras
//        inventoryList = FXCollections.observableArrayList();
//        shoppingList = FXCollections.observableArrayList();
//        tableViewShoppingList.setItems(shoppingList);
//
//        // Obtiene el grupo del usuario conectado desde CurrentSession y carga su inventario
//        int userGroupId = CurrentSession.getInstance().getUserGroupId();
//        loadInventory(userGroupId);
//
//        // Enlaza los datos cargados con la tabla de inventario
//        tableViewInventory.setItems(inventoryList);
//
//        // Actualiza la lista de la compra basada en el inventario inicial
//        for (Inventory inventory : inventoryList) {
//            updateShoppingList(inventory); // Evalúa cada producto y lo añade a la lista de la compra si es necesario
//        }
//
//        // Listener para manejar clics en las filas de la tabla de inventario
//        tableViewInventory.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2 && tableViewInventory.getSelectionModel().getSelectedItem() != null) {
//                Inventory selectedProduct = tableViewInventory.getSelectionModel().getSelectedItem();
//                openUpdateProductView(selectedProduct);
//            }
//        });
//    }
//    /**
//     * Carga los productos del inventario pertenecientes al grupo especificado.
//     *
//     * @param groupId El ID del grupo cuyos productos se cargarán.
//     */
//    public void loadInventory(int groupId) {
//        InventoryDAO inventoryDAO = new InventoryDAO();
//
//        // Limpia la lista actual antes de cargar nuevos datos
//        inventoryList.clear();
//
//        // Recupera los datos del inventario del grupo desde el DAO y los agrega a la lista
//        inventoryList.addAll(inventoryDAO.getAllInventoryProducts(groupId));
//
//        // Verifica que el campo "tipo" esté correctamente cargado
//        for (Inventory inventory : inventoryList) {
//            System.out.println("Cargando producto: " + inventory.getNombreProducto() + ", Tipo: " + inventory.getTipo());
//        }
//    }
    /**
     * Carga los productos del inventario pertenecientes al grupo especificado.
     *
     * @param groupId El ID del grupo cuyos productos se cargarán.
     */
    public void loadInventory(int groupId) {
        InventoryDAO inventoryDAO = new InventoryDAO();

        // Limpia la lista actual antes de cargar nuevos datos
        inventoryList.clear();

        // Recupera los datos del inventario del grupo desde el DAO y los agrega a la lista
        inventoryList.addAll(inventoryDAO.getAllInventoryProducts(groupId));

        // Verifica que los datos se hayan cargado correctamente
        for (Product product : inventoryList) {
            System.out.println("Cargando producto: " + product.getNombreProducto() + ", Tipo: " + product.getTipo());
        }
    }

//    /**
//     * Abre la vista para actualizar o eliminar un producto seleccionado.
//     *
//     * @param product El producto seleccionado en la tabla.
//     */
//    private void openUpdateProductView(Inventory product) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/UpdateProductView.fxml"));
//            Parent root = loader.load();
//
//            // Obtener el controlador de la vista
//            UpdateProductViewController updateController = loader.getController();
//            updateController.setProduct(product); // Pasar el producto seleccionado
//            updateController.setPurchaseViewController(this); // Referencia al controlador principal para actualizaciones
//
//            // Crear la escena y mostrarla
//            Stage stage = new Stage();
//            stage.setTitle("Actualizar Producto");
//            stage.setScene(new Scene(root));
//            stage.initModality(Modality.APPLICATION_MODAL); // Ventana modal
//            stage.showAndWait(); // Espera hasta que se cierre la ventana
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error al abrir la vista UpdateProductView.");
//        }
//    }
    /**
     * Inicializa los componentes de la vista al cargar. Configura las columnas
     * de la tabla y carga los datos del inventario y la lista de compras según
     * el grupo del usuario conectado.
     */
    public void initialize() {
        // Configura las columnas principales de inventario
        colProductName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colType.setCellValueFactory(new PropertyValueFactory<>("tipo")); // Nueva columna para "Cantidad" o "Gramos"

        // Configura los controles dinámicos para las columnas de cantidad y cantidad mínima
        configureColumnWithControls(colQuantity, "cantidad");
        configureColumnWithControls(colMinQuantity, "cantidadMinima");

        // Configura las columnas principales de lista de compras
        colShoppingProduct.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colShoppingQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad")); // Cantidad necesaria
        colShoppingType.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Configurar la columna de cantidades como editable
        colShoppingQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colShoppingQuantity.setOnEditCommit(event -> {
            Product item = event.getRowValue();
            item.setCantidad(event.getNewValue()); // Actualizar el modelo con la nueva cantidad
        });

        // Hacer la tabla editable
        tableViewShoppingList.setEditable(true);

        // Inicializa las listas observables para inventario y lista de compras
        inventoryList = FXCollections.observableArrayList();
        shoppingList = FXCollections.observableArrayList();

        // Enlaza las listas observables a las tablas
        tableViewInventory.setItems(inventoryList);
        tableViewShoppingList.setItems(shoppingList);

        // Obtiene el grupo del usuario conectado desde CurrentSession
        int userGroupId = CurrentSession.getInstance().getUserGroupId();

        // Cargar los datos de inventario desde la base de datos
        loadInventory(userGroupId);

        // Cargar la lista de compras inicial basada en el inventario
        for (Product product : inventoryList) {
            updateShoppingList(product); // Evalúa cada producto y lo añade a la lista de la compra si es necesario
        }

        // Listener para manejar clics en las filas de la tabla de inventario
        tableViewInventory.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableViewInventory.getSelectionModel().getSelectedItem() != null) {
                Product selectedProduct = tableViewInventory.getSelectionModel().getSelectedItem();
                openUpdateProductView(selectedProduct); // Abre la vista de edición
            }
        });
    }

    /**
     * Abre la vista para actualizar o eliminar un producto seleccionado.
     *
     * @param product El producto seleccionado en la tabla.
     */
    private void openUpdateProductView(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/UpdateProductView.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista
            UpdateProductViewController updateController = loader.getController();
            updateController.setProduct(product); // Pasar el producto seleccionado
            updateController.setPurchaseViewController(this); // Referencia al controlador principal para actualizaciones

            // Crear la escena y mostrarla
            Stage stage = new Stage();
            stage.setTitle("Actualizar Producto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Ventana modal
            stage.showAndWait(); // Espera hasta que se cierre la ventana
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al abrir la vista UpdateProductView.");
        }
    }

//    /**
//     * Configura una columna para que incluya controles (+/-) y permita al
//     * usuario ajustar cantidades.
//     *
//     * @param column La columna a configurar.
//     * @param propertyName El nombre de la propiedad del modelo que se mostrará
//     * (por ejemplo, "cantidad").
//     */
//    private void configureColumnWithControls(TableColumn<Inventory, Void> column, String propertyName) {
//        Callback<TableColumn<Inventory, Void>, TableCell<Inventory, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Inventory, Void> call(final TableColumn<Inventory, Void> param) {
//                return new TableCell<>() {
//                    private final Button btnIncrease = new Button("+");
//                    private final Button btnDecrease = new Button("-");
//                    private final Label lblValue = new Label(); // Muestra el valor actual
//
//                    {
//                        // Configura el estilo visual de los botones
//                        btnIncrease.getStyleClass().add("btn-increase");
//                        btnDecrease.getStyleClass().add("btn-decrease");
//
//                        // Acción para aumentar la cantidad
//                        btnIncrease.setOnAction(event -> {
//                            Inventory inventory = getTableView().getItems().get(getIndex());
//                            if ("cantidad".equals(propertyName)) {
//                                if ("Cantidad".equals(inventory.getTipo())) {
//                                    // Incremento de 1 unidad
//                                    inventory.setCantidad(inventory.getCantidad() + 1);
//                                } else if ("Gramos".equals(inventory.getTipo())) {
//                                    // Incremento de 50 gramos
//                                    inventory.setCantidad(inventory.getCantidad() + 50);
//                                }
//                                lblValue.setText(formatValue(inventory.getCantidad(), inventory.getTipo())); // Actualiza el valor mostrado
//                            } else if ("cantidadMinima".equals(propertyName)) {
//                                if ("Cantidad".equals(inventory.getTipo())) {
//                                    // Incremento de 1 unidad
//                                    inventory.setCantidadMinima(inventory.getCantidadMinima() + 1);
//                                } else if ("Gramos".equals(inventory.getTipo())) {
//                                    // Incremento de 50 gramos
//                                    inventory.setCantidadMinima(inventory.getCantidadMinima() + 50);
//                                }
//                                lblValue.setText(formatValue(inventory.getCantidadMinima(), inventory.getTipo())); // Actualiza el valor mostrado
//                            }
//                            updateInventoryProduct(inventory); // Actualiza la base de datos
//                            updateShoppingList(inventory); // Actualiza la lista de compra en tiempo real
//                        });
//
//                        // Acción para disminuir la cantidad
//                        btnDecrease.setOnAction(event -> {
//                            Inventory inventory = getTableView().getItems().get(getIndex());
//                            if ("cantidad".equals(propertyName)) {
//                                if ("Cantidad".equals(inventory.getTipo())) {
//                                    // Decremento de 1 unidad
//                                    if (inventory.getCantidad() > 0) {
//                                        inventory.setCantidad(inventory.getCantidad() - 1);
//                                    } else {
//                                        System.out.println("Cantidad mínima alcanzada.");
//                                    }
//                                } else if ("Gramos".equals(inventory.getTipo())) {
//                                    // Decremento de 50 gramos
//                                    if (inventory.getCantidad() > 0) {
//                                        inventory.setCantidad(Math.max(0, inventory.getCantidad() - 50)); // Evita valores negativos
//                                    } else {
//                                        System.out.println("Cantidad mínima alcanzada.");
//                                    }
//                                }
//                                lblValue.setText(formatValue(inventory.getCantidad(), inventory.getTipo())); // Actualiza el valor mostrado
//                            } else if ("cantidadMinima".equals(propertyName)) {
//                                if ("Cantidad".equals(inventory.getTipo())) {
//                                    // Decremento de 1 unidad
//                                    if (inventory.getCantidadMinima() > 0) {
//                                        inventory.setCantidadMinima(inventory.getCantidadMinima() - 1);
//                                    } else {
//                                        System.out.println("Cantidad mínima alcanzada.");
//                                    }
//                                } else if ("Gramos".equals(inventory.getTipo())) {
//                                    // Decremento de 50 gramos
//                                    if (inventory.getCantidadMinima() > 0) {
//                                        inventory.setCantidadMinima(Math.max(0, inventory.getCantidadMinima() - 50)); // Evita valores negativos
//                                    } else {
//                                        System.out.println("Cantidad mínima alcanzada.");
//                                    }
//                                }
//                                lblValue.setText(formatValue(inventory.getCantidadMinima(), inventory.getTipo())); // Actualiza el valor mostrado
//                            }
//                            updateInventoryProduct(inventory); // Actualiza la base de datos
//                            updateShoppingList(inventory); // Actualiza la lista de compra en tiempo real
//                        });
//                    }
//
//                    @Override
//                    public void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            Inventory inventory = getTableView().getItems().get(getIndex());
//                            if ("cantidad".equals(propertyName)) {
//                                lblValue.setText(formatValue(inventory.getCantidad(), inventory.getTipo()));
//                            } else if ("cantidadMinima".equals(propertyName)) {
//                                lblValue.setText(formatValue(inventory.getCantidadMinima(), inventory.getTipo()));
//                            }
//                            HBox buttonsBox = new HBox(5, lblValue, btnIncrease, btnDecrease);
//                            setGraphic(buttonsBox);
//                        }
//                    }
//                };
//            }
//        };
//
//        column.setCellFactory(cellFactory);
//    }
    /**
     * Configura una columna para que incluya controles (+/-) y permita al
     * usuario ajustar cantidades directamente desde la tabla.
     *
     * @param column La columna a configurar.
     * @param propertyName El nombre de la propiedad del modelo que se ajustará
     * (por ejemplo, "cantidad" o "cantidadMinima").
     */
    private void configureColumnWithControls(TableColumn<Product, Void> column, String propertyName) {
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btnIncrease = new Button("+");
                    private final Button btnDecrease = new Button("-");
                    private final Label lblValue = new Label(); // Muestra el valor actual

                    {
                        // Configura el estilo visual de los botones
                        btnIncrease.getStyleClass().add("btn-increase");
                        btnDecrease.getStyleClass().add("btn-decrease");

                        // Acción para aumentar la cantidad
                        btnIncrease.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            if ("cantidad".equals(propertyName)) {
                                if ("Cantidad".equals(product.getTipo())) {
                                    product.setCantidad(product.getCantidad() + 1); // Incrementa en 1 unidad
                                } else if ("Gramos".equals(product.getTipo())) {
                                    product.setCantidad(product.getCantidad() + 50); // Incrementa en 50 gramos
                                }
                                lblValue.setText(formatValue(product.getCantidad(), product.getTipo())); // Actualiza la vista
                            } else if ("cantidadMinima".equals(propertyName)) {
                                if ("Cantidad".equals(product.getTipo())) {
                                    product.setCantidadMinima(product.getCantidadMinima() + 1); // Incrementa en 1 unidad
                                } else if ("Gramos".equals(product.getTipo())) {
                                    product.setCantidadMinima(product.getCantidadMinima() + 50); // Incrementa en 50 gramos
                                }
                                lblValue.setText(formatValue(product.getCantidadMinima(), product.getTipo())); // Actualiza la vista
                            }
                            updateInventoryProduct(product); // Actualiza la base de datos
                            updateShoppingList(product); // Sincroniza con la lista de compras
                            tableViewInventory.refresh(); // Refresca la tabla
                            tableViewShoppingList.refresh(); // Refresca la lista de compras
                        });

                        // Acción para disminuir la cantidad
                        btnDecrease.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            if ("cantidad".equals(propertyName)) {
                                if ("Cantidad".equals(product.getTipo())) {
                                    product.setCantidad(Math.max(0, product.getCantidad() - 1)); // Decrementa en 1 unidad
                                } else if ("Gramos".equals(product.getTipo())) {
                                    product.setCantidad(Math.max(0, product.getCantidad() - 50)); // Decrementa en 50 gramos
                                }
                                lblValue.setText(formatValue(product.getCantidad(), product.getTipo())); // Actualiza la vista
                            } else if ("cantidadMinima".equals(propertyName)) {
                                if ("Cantidad".equals(product.getTipo())) {
                                    product.setCantidadMinima(Math.max(0, product.getCantidadMinima() - 1)); // Decrementa en 1 unidad
                                } else if ("Gramos".equals(product.getTipo())) {
                                    product.setCantidadMinima(Math.max(0, product.getCantidadMinima() - 50)); // Decrementa en 50 gramos
                                }
                                lblValue.setText(formatValue(product.getCantidadMinima(), product.getTipo())); // Actualiza la vista
                            }
                            updateInventoryProduct(product); // Actualiza la base de datos
                            updateShoppingList(product); // Sincroniza con la lista de compras
                            tableViewInventory.refresh(); // Refresca la tabla
                            tableViewShoppingList.refresh(); // Refresca la lista de compras
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Product product = getTableView().getItems().get(getIndex());
                            if ("cantidad".equals(propertyName)) {
                                lblValue.setText(formatValue(product.getCantidad(), product.getTipo()));
                            } else if ("cantidadMinima".equals(propertyName)) {
                                lblValue.setText(formatValue(product.getCantidadMinima(), product.getTipo()));
                            }
                            HBox buttonsBox = new HBox(5, lblValue, btnIncrease, btnDecrease);
                            setGraphic(buttonsBox);
                        }
                    }
                };
            }
        };

        column.setCellFactory(cellFactory);
    }

    /**
     * Formatea el valor mostrado según el tipo (Cantidad o Gramos).
     *
     * @param value El valor a formatear.
     * @param tipo El tipo de medida ("Cantidad" o "Gramos").
     * @return El valor formateado como cadena.
     */
    private String formatValue(int value, String tipo) {
        return "Cantidad".equals(tipo) ? String.valueOf(value) : value + " g";
    }

//    /**
//     * Actualiza un producto en la base de datos.
//     *
//     * @param inventory El objeto del producto a actualizar.
//     */
//    public void updateInventoryProduct(Inventory inventory) {
//        InventoryDAO inventoryDAO = new InventoryDAO();
//        boolean updated = inventoryDAO.updateInventoryProduct(inventory);
//        if (!updated) {
//            System.err.println("Error al actualizar el producto en la base de datos. Producto: "
//                    + inventory.getNombreProducto() + ", Tipo: " + inventory.getTipo());
//        } else {
//            System.out.println("Producto actualizado correctamente: "
//                    + inventory.getNombreProducto() + ", Tipo: " + inventory.getTipo());
//        }
//    }
    /**
     * Actualiza un producto en la base de datos.
     *
     * @param product El objeto del producto a actualizar.
     */
    public void updateInventoryProduct(Product product) {
        InventoryDAO inventoryDAO = new InventoryDAO();
        boolean updated = inventoryDAO.updateInventoryProduct(product);
        if (!updated) {
            System.err.println("Error al actualizar el producto en la base de datos. Producto: "
                    + product.getNombreProducto() + ", Tipo: " + product.getTipo());
        } else {
            System.out.println("Producto actualizado correctamente: "
                    + product.getNombreProducto() + ", Tipo: " + product.getTipo());
        }
    }

//    /**
//     * Actualiza la lista de la compra si la cantidad en inventario es menor que
//     * la mínima.
//     *
//     * @param inventory El objeto de inventario que se evaluará.
//     */
//    public void updateShoppingList(Inventory inventory) {
//        // Verifica si la cantidad actual es menor que la cantidad mínima
//        if (inventory.getCantidad() < inventory.getCantidadMinima()) {
//            // Calcula la cantidad necesaria para alcanzar la cantidad mínima
//            int cantidadNecesaria = inventory.getCantidadMinima() - inventory.getCantidad();
//
//            // Crea un nuevo objeto de compra con la cantidad necesaria
//            Purchase purchase = new Purchase(
//                    0, // ID aún no asignado
//                    inventory.getId(),
//                    cantidadNecesaria, // Cantidad necesaria calculada
//                    "2025-04-06", // Fecha simulada
//                    inventory.getIdGrupo(),
//                    inventory.getNombreProducto(),
//                    inventory.getTipo() // Asegura que el tipo también se incluye
//            );
//
//            // Comprueba si ya existe en la lista
//            boolean exists = shoppingList.stream().anyMatch(p -> p.getIdInventario() == inventory.getId());
//            if (!exists) {
//                shoppingList.add(purchase); // Añade el producto si no existe
//                System.out.println("Producto añadido a la lista: " + purchase.getNombreProducto()
//                        + ", cantidad: " + cantidadNecesaria + ", tipo: " + purchase.getTipo());
//            } else {
//                // Si ya existe, actualiza la cantidad necesaria
//                shoppingList.stream()
//                        .filter(p -> p.getIdInventario() == inventory.getId())
//                        .forEach(p -> {
//                            p.setCantidadNecesaria(cantidadNecesaria);
//                            p.setTipo(inventory.getTipo()); // Actualiza el tipo si cambia
//                            System.out.println("Cantidad actualizada en la lista: " + p.getNombreProducto()
//                                    + ", cantidad: " + cantidadNecesaria + ", tipo: " + p.getTipo());
//                        });
//            }
//        } else {
//            // Elimina de la lista de la compra si ya no cumple la condición
//            boolean removed = shoppingList.removeIf(p -> p.getIdInventario() == inventory.getId());
//            if (removed) {
//                System.out.println("Producto eliminado de la lista: " + inventory.getNombreProducto());
//            }
//        }
//
//        // Refresca la tabla de la lista de la compra
//        tableViewShoppingList.refresh();
//    }
    /**
     * Actualiza la lista de la compra si la cantidad en inventario es menor que
     * la cantidad mínima. Elimina el producto si ya no cumple la condición.
     *
     * @param product El objeto del inventario que se evaluará.
     */
    public void updateShoppingList(Product product) {
        // Verifica si la cantidad actual es menor que la cantidad mínima
        if (product.getCantidad() < product.getCantidadMinima()) {
            // Calcula la cantidad necesaria para alcanzar la cantidad mínima
            int cantidadNecesaria = product.getCantidadMinima() - product.getCantidad();

            // Comprueba si el producto ya existe en la lista de compras
            boolean exists = shoppingList.stream().anyMatch(p -> p.getId() == product.getId());
            if (!exists) {
                // Si no existe, crea un nuevo producto para la lista de compras
                Product shoppingProduct = new Product(
                        product.getId(), // Usa el ID del producto existente
                        product.getNombreProducto(),
                        cantidadNecesaria, // Cantidad necesaria calculada
                        0, // Cantidad mínima no relevante en lista de compras
                        0, // Cantidad máxima no relevante en lista de compras
                        product.getTipo(),
                        product.getCategoria(),
                        product.getIdGrupo(),
                        LocalDate.now().toString() // Fecha actual
                );
                shoppingList.add(shoppingProduct);
                System.out.println("Producto añadido a la lista de compras: " + shoppingProduct.getNombreProducto()
                        + ", cantidad necesaria: " + cantidadNecesaria + ", tipo: " + shoppingProduct.getTipo());
            } else {
                // Si ya existe, actualiza la cantidad necesaria
                shoppingList.stream()
                        .filter(p -> p.getId() == product.getId())
                        .forEach(p -> {
                            p.setCantidad(cantidadNecesaria);
                            p.setTipo(product.getTipo()); // Actualiza el tipo si cambia
                            System.out.println("Cantidad actualizada en la lista de compras: " + p.getNombreProducto()
                                    + ", cantidad necesaria: " + cantidadNecesaria + ", tipo: " + p.getTipo());
                        });
            }
        } else {
            // Si la cantidad actual es mayor o igual que la mínima, elimina el producto de la lista de compras
            boolean removed = shoppingList.removeIf(p -> p.getId() == product.getId());
            if (removed) {
                System.out.println("Producto eliminado de la lista de compras: " + product.getNombreProducto());
            }
        }

        // Refresca la tabla de la lista de compras
        tableViewShoppingList.refresh();
    }

    /**
     * Abre la ventana para crear un nuevo producto
     *
     * @param event - Evento activado al presionar el botón "Nuevo producto".
     */
    @FXML
    private void openAddNewProduct(ActionEvent event) {

        try {
            // Carga la vista CreateEventView desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/CreateProductView.fxml"));
            Parent root = loader.load();

            // Obtén el controlador de la nueva vista
            CreateProductViewController createProductController = loader.getController();

            // Pasa la referencia del controlador principal
            createProductController.setPurchaseViewController(this);

            // Configura una nueva ventana para la vista de creación
            Stage stage = new Stage();
            stage.setTitle("Añadir Nuevo Producto"); // Título de la ventana
            stage.setScene(new Scene(root)); // Configura la escena
            stage.initModality(Modality.WINDOW_MODAL); // Establece la ventana como modal
            stage.initOwner(btnOpenAddNewProduct.getScene().getWindow()); // Asocia la ventana actual como propietaria
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre
        } catch (IOException e) {
            // Registra un error en caso de problemas al cargar la vista
            System.err.println("Error al cargar la vista CreateProductView: " + e.getMessage());
        }
    }

//    /**
//     * Carga los productos de la lista de compras pertenecientes al grupo
//     * especificado.
//     *
//     * @param groupId El ID del grupo cuyos productos se cargarán.
//     */
//    public void loadShoppingList(int groupId) {
//        PurchaseDAO purchaseDAO = new PurchaseDAO();
//
//        // Limpia la lista actual antes de cargar nuevos datos
//        shoppingList.clear();
//
//        // Recupera los datos de la lista de compras desde el DAO y los agrega a la lista
//        shoppingList.addAll(purchaseDAO.getPurchasesByGroup(groupId));
//
//        // Verifica que los datos se hayan cargado correctamente
//        for (Product product : shoppingList) {
//            System.out.println("Cargando producto en lista de compras: " + product.getNombreProducto()
//                    + ", cantidad necesaria: " + product.getCantidad()
//                    + ", tipo: " + product.getTipo());
//        }
//
//        // Refresca la tabla de la lista de compras para reflejar los cambios
//        tableViewShoppingList.refresh();
//    }
    /**
     * Carga los productos de la lista de compras pertenecientes al grupo
     * especificado sin eliminar productos existentes que no estén en la
     * consulta.
     *
     * @param groupId El ID del grupo cuyos productos se cargarán.
     */
    public void loadShoppingList(int groupId) {
        try {
            PurchaseDAO purchaseDAO = new PurchaseDAO();

            // Recuperar los datos actualizados desde el DAO
            List<Product> updatedProducts = purchaseDAO.getPurchasesByGroup(groupId);

            // Mantener los productos existentes y añadir solo los nuevos
            for (Product updatedProduct : updatedProducts) {
                // Verificar si el producto ya existe en la lista observable
                boolean exists = shoppingList.stream()
                        .anyMatch(product -> product.getId() == updatedProduct.getId());

                if (!exists) {
                    shoppingList.add(updatedProduct);
                }
            }

            // Verificar que los datos se hayan cargado correctamente
            for (Product product : shoppingList) {
                System.out.println("Lista actualizada: " + product.getNombreProducto()
                        + ", cantidad necesaria: " + product.getCantidad()
                        + ", tipo: " + product.getTipo());
            }

            // Refresca la tabla de la lista de compras para reflejar los cambios
            tableViewShoppingList.refresh();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                    "Hubo un problema al cargar la lista de compras.");
        }
    }

    /**
     * Pregunta si se han comprado productos que no estaban en la lista de
     * compras y abre la vista para añadirlos si es necesario.
     *
     * @param event Evento activado al presionar el botón "Completar Compra".
     */
//    @FXML
//    private void completePurchase(ActionEvent event) {
//        try {
//            System.out.println("Inicio de completePurchase");
//
//            // Preguntar si las cantidades de los productos se han modificado
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Completar Compra");
//            alert.setHeaderText("¿Has modificado las cantidades de los productos en la lista?");
//            alert.setContentText("Si no has modificado las cantidades, se sumarán las necesarias directamente al inventario.");
//
//            ButtonType yesButton = new ButtonType("Sí");
//            ButtonType noButton = new ButtonType("No");
//            alert.getButtonTypes().setAll(yesButton, noButton);
//
//            Optional<ButtonType> result = alert.showAndWait();
//            System.out.println("Resultado de la alerta (modificar cantidades): " + result);
//
//            InventoryDAO inventoryDAO = new InventoryDAO();
//
//            if (result.isPresent() && result.get() == yesButton) {
//                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Modificar Cantidades",
//                        "Por favor, modifica las cantidades directamente en la tabla y luego pulsa nuevamente 'Completar Compra'.");
//                return; // Detener el flujo hasta que se modifiquen las cantidades
//            } else if (!result.isPresent()) {
//                System.err.println("No se seleccionó ninguna opción en la alerta de modificación de cantidades.");
//                return;
//            }
//
//            System.out.println("El usuario indicó que no modificó las cantidades.");
//
//            // Sumar productos directamente al inventario
//            for (Product product : shoppingList) {
//                System.out.println("Procesando producto: " + product.getNombreProducto() + " (ID: " + product.getId() + ")");
//
//                int currentQuantity = inventoryDAO.getCurrentQuantityById(product.getId());
//                if (currentQuantity == -1) {
//                    throw new SQLException("No se pudo obtener la cantidad actual del producto: " + product.getNombreProducto());
//                }
//
//                int newQuantity = currentQuantity + product.getCantidad();
//                boolean updated = inventoryDAO.updateInventoryQuantity(product.getId(), newQuantity);
//                System.out.println("Nueva cantidad calculada: " + newQuantity);
//
//                if (!updated) {
//                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                            "No se pudo actualizar la cantidad del producto: " + product.getNombreProducto());
//                    return;
//                }
//            }
//
//            // Preguntar si se han comprado productos fuera de la lista
//            alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Productos Fuera de la Lista");
//            alert.setHeaderText("¿Has comprado productos que no estaban en la lista?");
//            alert.setContentText("Si es así, podrás añadirlos directamente al inventario y a la lista de compras.");
//
//            yesButton = new ButtonType("Sí", ButtonBar.ButtonData.YES);
//            noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
//            alert.getButtonTypes().setAll(yesButton, noButton);
//
//            result = alert.showAndWait();
//            System.out.println("Resultado de la alerta (productos fuera de la lista): " + result);
//
//            // Manejo de productos fuera de la lista
//            if (result.isPresent() && result.get() == yesButton) {
//                System.out.println("El usuario indicó que compró productos fuera de la lista.");
//
//                try {
//                    System.out.println("Intentando cargar el archivo FXML para el popup.");
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/CreateProductInShoppingListView.fxml"));
//                    Parent root = loader.load();
//                    System.out.println("Archivo FXML cargado correctamente.");
//
//                    CreateProductInShoppingListViewController createController = loader.getController();
//                    createController.setPurchaseViewController(this);
//                    System.out.println("Controlador asignado correctamente.");
//
//                    Stage stage = new Stage();
//                    stage.setTitle("Añadir Producto al Inventario");
//                    stage.setScene(new Scene(root));
//                    stage.initModality(Modality.APPLICATION_MODAL);
//                    System.out.println("Ventana configurada correctamente.");
//                    stage.showAndWait();
//                    System.out.println("Popup mostrado correctamente.");
//
//                    Product newProduct = createController.getCreatedProduct();
//                    System.out.println("Nuevo producto creado: " + (newProduct != null ? newProduct.getNombreProducto() : "Ninguno"));
//
//                    if (newProduct != null) {
//                        boolean productExistsInInventory = inventoryDAO.isProductInInventory(newProduct.getNombreProducto(), newProduct.getIdGrupo());
//                        System.out.println("Producto ya existe en el inventario: " + productExistsInInventory);
//
//                        int idInventario;
//
//                        if (productExistsInInventory) {
//                            idInventario = inventoryDAO.getInventoryProductIdByName(newProduct.getNombreProducto(), newProduct.getIdGrupo());
//
//                            // Obtener cantidad actual y sumar la nueva cantidad
//                            int currentQuantity = inventoryDAO.getCurrentQuantityById(idInventario);
//                            if (currentQuantity == -1) {
//                                throw new SQLException("No se pudo obtener la cantidad actual del producto: " + newProduct.getNombreProducto());
//                            }
//
//                            int updatedQuantity = currentQuantity + newProduct.getCantidad();
//                            boolean updated = inventoryDAO.updateInventoryQuantity(idInventario, updatedQuantity);
//
//                            System.out.println("Cantidad actualizada con suma para el producto: " + updatedQuantity);
//                            if (!updated) {
//                                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                                        "No se pudo actualizar la cantidad del producto en el inventario.");
//                                return;
//                            }
//                        } else {
//                            idInventario = inventoryDAO.generateNewInventoryId();
//                            newProduct.setId(idInventario);
//
//                            boolean addedToInventory = inventoryDAO.addInventoryProduct(newProduct);
//                            System.out.println("Resultado de añadir al inventario: " + addedToInventory);
//
//                            if (!addedToInventory) {
//                                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                                        "No se pudo añadir el producto al inventario.");
//                                return;
//                            }
//                        }
//
//                        // Refrescar el inventario tras añadir/actualizar el producto
//                        loadInventory(CurrentSession.getInstance().getUserGroupId());
//                        System.out.println("Inventario actualizado automáticamente tras añadir/actualizar el producto.");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar la ventana para añadir un nuevo producto.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error inesperado al intentar añadir un producto.");
//                }
//            } else if (result.isPresent() && result.get() == noButton) {
//                System.out.println("El usuario indicó que no compró productos fuera de la lista.");
//            } else {
//                System.err.println("No se seleccionó ninguna opción en la alerta de productos fuera de la lista.");
//            }
//
//            // Solicitar el coste de la compra
//            TextInputDialog inputDialog = new TextInputDialog();
//            inputDialog.setTitle("Registrar Coste");
//            inputDialog.setHeaderText("Introduce el importe de la compra:");
//            inputDialog.setContentText("Importe:");
//            Optional<String> inputResult = inputDialog.showAndWait();
//
//            if (!inputResult.isPresent()) {
//                System.err.println("El usuario canceló la introducción del coste.");
//                return;
//            }
//
//            double importe;
//            try {
//                importe = Double.parseDouble(inputResult.get());
//            } catch (NumberFormatException e) {
//                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "El importe introducido no es válido.");
//                return;
//            }
//
//            // Solicitar el método de pago
//            List<String> paymentMethods = Arrays.asList("Efectivo", "Tarjeta");
//            ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("Efectivo", paymentMethods);
//            paymentDialog.setTitle("Registrar Método de Pago");
//            paymentDialog.setHeaderText("Selecciona el método de pago:");
//            paymentDialog.setContentText("Método de pago:");
//
//            Optional<String> paymentResult = paymentDialog.showAndWait();
//            if (!paymentResult.isPresent()) {
//                System.err.println("El usuario canceló la selección del método de pago.");
//                return;
//            }
//
//            String metodoPago = paymentResult.get();
//            System.out.println("Método de pago seleccionado: " + metodoPago);
//
//            // Registrar el gasto utilizando BudgetDAO
//            BudgetDAO budgetDAO = new BudgetDAO();
//            Budget newBudget = new Budget(
//                    0, // ID generado automáticamente
//                    "Compra", // Nombre
//                    "Alimentación", // Categoría
//                    importe, // Monto
//                    metodoPago, // Método de pago
//                    LocalDate.now(), // Fecha actual
//                    "", // Descripción vacía
//                    CurrentSession.getInstance().getUserGroupId() // ID del grupo actual
//            );
//
//            boolean expenseAdded = budgetDAO.addBudget(newBudget);
//            if (!expenseAdded) {
//                throw new SQLException("No se pudo añadir el registro de gasto.");
//            }
//
//            System.out.println("Registro de gasto añadido correctamente: Importe = " + importe + ", Método de Pago = " + metodoPago);
//
//            // Mostrar mensaje final de confirmación
//            Alert alertCompletion = new Alert(Alert.AlertType.INFORMATION);
//            alertCompletion.setTitle("Compra Completada");
//            alertCompletion.setHeaderText("¡Compra Exitosa!");
//            alertCompletion.setContentText("La compra se ha completado correctamente y los productos han sido procesados en el inventario.");
//            alertCompletion.showAndWait();
//
//            // Vaciar la lista de compras en la interfaz
//            shoppingList.clear();
//            tableViewShoppingList.refresh();
//            System.out.println("Lista de compras vaciada correctamente en la interfaz.");
//
//            // Vaciar la lista de compras de la base de datos
//            PurchaseDAO purchaseDAO = new PurchaseDAO();
//            int groupId = CurrentSession.getInstance().getUserGroupId();
//            boolean cleared = purchaseDAO.clearShoppingList(groupId);
//            System.out.println("Resultado de eliminar la lista de compras: " + cleared);
//
//            System.out.println("Mensaje de confirmación mostrado: Compra completada correctamente.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al procesar la compra.");
//        }
//    }
    /**
     * Método principal para completar una compra. Incluye validación de
     * cantidades, actualización de inventario, manejo de productos fuera de la
     * lista, registro de gastos y limpieza de la lista de compras.
     *
     * @param event Evento activado al presionar el botón "Completar Compra".
     */
    @FXML
    private void completePurchase(ActionEvent event) {
        try {
            System.out.println("Inicio de completePurchase");

            // Validar modificación de cantidades
            if (!validateModifiedQuantities()) {
                System.out.println("El usuario canceló la compra en la etapa de modificación de cantidades.");
                return; // Detener el flujo si se decide cancelar
            }

            // Confirmar si desea continuar con la actualización del inventario
            if (!confirmAction("Actualizar Inventario", "¿Desea continuar con la actualización del inventario?")) {
                System.out.println("El usuario canceló la compra en la etapa de actualización del inventario.");
                return; // Detener el flujo si se decide cancelar
            }

            // Actualizar productos directamente en el inventario
            InventoryDAO inventoryDAO = new InventoryDAO();
            updateInventoryQuantities(inventoryDAO);

            // Confirmar si desea continuar con el manejo de productos fuera de la lista
            if (!confirmAction("Productos Fuera de la Lista", "¿Desea continuar con el manejo de productos fuera de la lista?")) {
                System.out.println("El usuario canceló la compra en la etapa de manejo de productos fuera de la lista.");
                return; // Detener el flujo si se decide cancelar
            }

            // Manejar productos fuera de la lista
            manageProductsOutsideList(inventoryDAO);

            // Confirmar si desea continuar con el registro del gasto
            if (!confirmAction("Registrar Gasto", "¿Desea continuar con el registro del gasto?")) {
                System.out.println("El usuario canceló la compra en la etapa de registro del gasto.");
                return; // Detener el flujo si se decide cancelar
            }

            // Registrar el gasto
            registerExpense();

            // Vaciar la lista de compras
            clearShoppingList();

            // Mostrar mensaje final de éxito
            Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
            alertSuccess.setTitle("Compra Procesada");
            alertSuccess.setHeaderText("¡Compra completada con éxito!");
            alertSuccess.setContentText("Los productos han sido procesados correctamente y el gasto ha sido registrado con éxito en el sistema.");
            alertSuccess.showAndWait();

            System.out.println("Proceso completo: Compra procesada y gasto registrado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al procesar la compra.");
        }
    }

    /**
     * Muestra una alerta de confirmación al usuario para decidir si desea
     * continuar o cancelar.
     *
     * @param title Título de la alerta.
     * @param message Mensaje explicativo de la alerta.
     * @return True si el usuario desea continuar, False si decide cancelar.
     */
    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType continueButton = new ButtonType("Continuar", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(continueButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == continueButton;
    }

    /**
     * Valida si las cantidades de los productos en la lista han sido
     * modificadas. Si deben ser modificadas, muestra un mensaje informativo y
     * detiene el flujo.
     *
     * @return True si las cantidades no requieren modificación, False si deben
     * ser modificadas.
     */
    private boolean validateModifiedQuantities() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Completar Compra");
        alert.setHeaderText("¿Has modificado las cantidades de los productos en la lista?");
        alert.setContentText("Si no has modificado las cantidades, se sumarán las necesarias directamente al inventario.");

        ButtonType yesButton = new ButtonType("Sí");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        System.out.println("Resultado de la alerta (modificar cantidades): " + result);

        if (result.isPresent() && result.get() == yesButton) {
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Modificar Cantidades",
                    "Por favor, modifica las cantidades directamente en la tabla y luego pulsa nuevamente 'Completar Compra'.");
            return false; // Indicar que el flujo debe detenerse
        } else if (!result.isPresent()) {
            System.err.println("No se seleccionó ninguna opción en la alerta de modificación de cantidades.");
            return false;
        }

        System.out.println("El usuario indicó que no modificó las cantidades.");
        return true;
    }

    /**
     * Actualiza las cantidades de los productos en el inventario basado en la
     * lista de compras.
     *
     * @param inventoryDAO DAO para interactuar con la base de datos del
     * inventario.
     * @throws SQLException Si ocurre un error al acceder o actualizar los datos
     * del inventario.
     */
    private void updateInventoryQuantities(InventoryDAO inventoryDAO) throws SQLException {
        for (Product product : shoppingList) {
            System.out.println("Procesando producto: " + product.getNombreProducto() + " (ID: " + product.getId() + ")");

            int currentQuantity = inventoryDAO.getCurrentQuantityById(product.getId());
            if (currentQuantity == -1) {
                throw new SQLException("No se pudo obtener la cantidad actual del producto: " + product.getNombreProducto());
            }

            int newQuantity = currentQuantity + product.getCantidad();
            boolean updated = inventoryDAO.updateInventoryQuantity(product.getId(), newQuantity);
            System.out.println("Nueva cantidad calculada: " + newQuantity);

            if (!updated) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                        "No se pudo actualizar la cantidad del producto: " + product.getNombreProducto());
                return;
            }
        }
    }

    /**
     * Maneja los productos que no estaban en la lista de compras, mostrando un
     * popup para añadirlos al inventario.
     *
     * @param inventoryDAO DAO para interactuar con la base de datos del
     * inventario.
     * @throws IOException Si ocurre un problema al cargar la ventana del popup.
     * @throws SQLException Si ocurre un error al acceder o actualizar los datos
     * del inventario.
     */
    private void manageProductsOutsideList(InventoryDAO inventoryDAO) throws IOException, SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Productos Fuera de la Lista");
        alert.setHeaderText("¿Has comprado productos que no estaban en la lista?");
        alert.setContentText("Si es así, podrás añadirlos directamente al inventario y a la lista de compras.");

        ButtonType yesButton = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        System.out.println("Resultado de la alerta (productos fuera de la lista): " + result);

        if (result.isPresent() && result.get() == yesButton) {
            System.out.println("El usuario indicó que compró productos fuera de la lista.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/CreateProductInShoppingListView.fxml"));
            Parent root = loader.load();

            CreateProductInShoppingListViewController createController = loader.getController();
            createController.setPurchaseViewController(this);

            Stage stage = new Stage();
            stage.setTitle("Añadir Producto al Inventario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Product newProduct = createController.getCreatedProduct();
            if (newProduct != null) {
                boolean productExistsInInventory = inventoryDAO.isProductInInventory(newProduct.getNombreProducto(), newProduct.getIdGrupo());

                if (productExistsInInventory) {
                    int idInventario = inventoryDAO.getInventoryProductIdByName(newProduct.getNombreProducto(), newProduct.getIdGrupo());
                    int currentQuantity = inventoryDAO.getCurrentQuantityById(idInventario);

                    int updatedQuantity = currentQuantity + newProduct.getCantidad();
                    inventoryDAO.updateInventoryQuantity(idInventario, updatedQuantity);
                } else {
                    inventoryDAO.addInventoryProduct(newProduct);
                }

                loadInventory(CurrentSession.getInstance().getUserGroupId());
            }
        }
    }

    /**
     * Solicita el importe y método de pago para registrar el gasto en la base
     * de datos.
     *
     * @throws SQLException Si ocurre un error al registrar el gasto en la base
     * de datos.
     */
    private void registerExpense() throws SQLException {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Registrar Coste");
        inputDialog.setHeaderText("Introduce el importe de la compra:");
        inputDialog.setContentText("Importe:");
        Optional<String> inputResult = inputDialog.showAndWait();

        if (!inputResult.isPresent()) {
            System.err.println("El usuario canceló la introducción del coste.");
            return;
        }

        double importe = Double.parseDouble(inputResult.get());

        List<String> paymentMethods = Arrays.asList("Efectivo", "Tarjeta");
        ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("Efectivo", paymentMethods);
        paymentDialog.setTitle("Registrar Método de Pago");
        paymentDialog.setHeaderText("Selecciona el método de pago:");
        paymentDialog.setContentText("Método de pago:");
        Optional<String> paymentResult = paymentDialog.showAndWait();

        String metodoPago = paymentResult.get();

        BudgetDAO budgetDAO = new BudgetDAO();
        Budget newBudget = new Budget(0, "Compra", "Alimentación", importe, metodoPago, LocalDate.now(), "", CurrentSession.getInstance().getUserGroupId());
        budgetDAO.addBudget(newBudget);

        System.out.println("Registro de gasto añadido correctamente: Importe = " + importe + ", Método de Pago = " + metodoPago);
    }

    /**
     * Vacía la lista de compras tanto en la interfaz como en la base de datos.
     *
     * @throws SQLException Si ocurre un error al vaciar la lista de compras en
     * la base de datos.
     */
    private void clearShoppingList() throws SQLException {
        shoppingList.clear();
        tableViewShoppingList.refresh();
        System.out.println("Lista de compras vaciada correctamente en la interfaz.");

        PurchaseDAO purchaseDAO = new PurchaseDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId();
        purchaseDAO.clearShoppingList(groupId);
    }

}
