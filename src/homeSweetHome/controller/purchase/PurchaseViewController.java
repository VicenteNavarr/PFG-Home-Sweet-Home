package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.model.Inventory;
import homeSweetHome.model.Purchase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Controlador para gestionar la vista de compras. Permite visualizar y
 * manipular el inventario, interactuar con el usuario y realizar
 * actualizaciones en la base de datos.
 */
public class PurchaseViewController {

    // Tabla del inventario
    @FXML
    private TableView<Inventory> tableViewInventory;

    @FXML
    private TableColumn<Inventory, String> colProductName; // Columna para el nombre del producto

    @FXML
    private TableColumn<Inventory, Void> colQuantity; // Columna para "Cantidad Actual" con botones y valor

    @FXML
    private TableColumn<Inventory, Void> colMinQuantity; // Columna para "Cantidad Mínima" con botones y valor

    @FXML
    private TableColumn<Inventory, String> colCategory; // Columna para la categoría

    @FXML
    private TableView<Purchase> tableViewShoppingList;

    @FXML
    private TableColumn<Purchase, String> colShoppingProduct;

    @FXML
    private TableColumn<Purchase, Integer> colShoppingQuantity;

    // Lista interna que contiene los datos del inventario
    private ObservableList<Inventory> inventoryList;

    // Lista interna que contiene los datos de la lista de la compra
    private ObservableList<Purchase> shoppingList;

    /**
     * Inicializa los componentes de la vista al cargar. Configura las columnas
     * de la tabla y carga los datos del inventario según el grupo del usuario
     * conectado.
     */
    @FXML
    public void initialize() {

        // Configura las columnas principales de inventario
        colProductName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // Configura las columnas principales de lista de compra
        colShoppingProduct.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colShoppingQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidadNecesaria"));

        // Configura las columnas que tienen controles dinámicos
        configureColumnWithControls(colQuantity, "cantidad");
        configureColumnWithControls(colMinQuantity, "cantidadMinima");

        // Inicializa la lista de datos del inventario
        inventoryList = FXCollections.observableArrayList();

        shoppingList = FXCollections.observableArrayList();
        tableViewShoppingList.setItems(shoppingList); // Enlazamos la lista con la tabla correspondiente

        // Obtiene el grupo del usuario conectado desde CurrentSession y carga su inventario
        int userGroupId = CurrentSession.getInstance().getUserGroupId();
        loadInventory(userGroupId);

        // Enlaza los datos cargados con la tabla
        tableViewInventory.setItems(inventoryList);

        // Actualiza la lista de la compra basada en el inventario inicial
        for (Inventory inventory : inventoryList) {
            updateShoppingList(inventory); // Evalúa cada producto y añade a la lista de la compra si es necesario
        }
    }

    /**
     * Carga los productos del inventario pertenecientes al grupo especificado.
     *
     * @param groupId El ID del grupo cuyos productos se cargarán.
     */
    private void loadInventory(int groupId) {
        InventoryDAO inventoryDAO = new InventoryDAO();

        // Limpia la lista actual antes de cargar nuevos datos
        inventoryList.clear();

        // Recupera los datos del inventario del grupo desde el DAO y los agrega a la lista
        inventoryList.addAll(inventoryDAO.getAllInventoryProducts(groupId));
    }

    /**
     * Configura una columna para que incluya controles (+/-) y muestre el valor
     * actual.
     *
     * @param column La columna a configurar.
     * @param propertyName El nombre de la propiedad del modelo que se mostrará
     * (por ejemplo, "cantidad").
     */
    private void configureColumnWithControls(TableColumn<Inventory, Void> column, String propertyName) {
        Callback<TableColumn<Inventory, Void>, TableCell<Inventory, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Inventory, Void> call(final TableColumn<Inventory, Void> param) {
                return new TableCell<>() {
                    private final Button btnIncrease = new Button("+");
                    private final Button btnDecrease = new Button("-");
                    private final Label lblValue = new Label(); // Muestra el valor actual

                    {
                        // Configura el estilo visual de los botones
                        btnIncrease.getStyleClass().add("btn-increase");
                        btnDecrease.getStyleClass().add("btn-decrease");

                        // Configura la acción del botón para aumentar el valor
                        btnIncrease.setOnAction(event -> {
                            Inventory inventory = getTableView().getItems().get(getIndex());
                            if (propertyName.equals("cantidad")) {
                                if (inventory.getCantidad() < inventory.getCantidadMaxima()) {
                                    inventory.setCantidad(inventory.getCantidad() + 1);
                                    lblValue.setText(String.valueOf(inventory.getCantidad())); // Actualiza el Label
                                    updateInventoryProduct(inventory); // Actualiza la base de datos
                                    updateShoppingList(inventory); // Actualiza la lista de la compra en tiempo real
                                } else {
                                    System.out.println("Cantidad máxima alcanzada.");
                                }
                            } else if (propertyName.equals("cantidadMinima")) {
                                inventory.setCantidadMinima(inventory.getCantidadMinima() + 1);
                                lblValue.setText(String.valueOf(inventory.getCantidadMinima())); // Actualiza el Label
                                updateInventoryProduct(inventory);
                                updateShoppingList(inventory);
                            }
                        });

                        // Configura la acción del botón para disminuir el valor
                        btnDecrease.setOnAction(event -> {
                            Inventory inventory = getTableView().getItems().get(getIndex());
                            if (propertyName.equals("cantidad")) {
                                if (inventory.getCantidad() > 0) {
                                    inventory.setCantidad(inventory.getCantidad() - 1);
                                    lblValue.setText(String.valueOf(inventory.getCantidad())); // Actualiza el Label
                                    updateInventoryProduct(inventory); // Actualiza la base de datos
                                    updateShoppingList(inventory); // Actualiza la lista de la compra en tiempo real
                                } else {
                                    System.out.println("Cantidad mínima alcanzada.");
                                }
                            } else if (propertyName.equals("cantidadMinima")) {
                                if (inventory.getCantidadMinima() > 0) {
                                    inventory.setCantidadMinima(inventory.getCantidadMinima() - 1);
                                    lblValue.setText(String.valueOf(inventory.getCantidadMinima())); // Actualiza el Label
                                    updateInventoryProduct(inventory);
                                    updateShoppingList(inventory);
                                } else {
                                    System.out.println("Cantidad mínima alcanzada.");
                                }
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Inventory inventory = getTableView().getItems().get(getIndex());
                            if (propertyName.equals("cantidad")) {
                                lblValue.setText(String.valueOf(inventory.getCantidad()));
                            } else if (propertyName.equals("cantidadMinima")) {
                                lblValue.setText(String.valueOf(inventory.getCantidadMinima()));
                            }
                            HBox buttonsBox = new HBox(5, lblValue, btnIncrease, btnDecrease); // Combina controles y valor
                            setGraphic(buttonsBox);
                        }
                    }
                };
            }
        };

        column.setCellFactory(cellFactory);
    }

    /**
     * Actualiza un producto en la base de datos.
     *
     * @param inventory El objeto del producto a actualizar.
     */
    private void updateInventoryProduct(Inventory inventory) {
        InventoryDAO inventoryDAO = new InventoryDAO();
        boolean updated = inventoryDAO.updateInventoryProduct(inventory);
        if (!updated) {
            System.err.println("Error al actualizar el producto en la base de datos.");
        }
    }

//    /**
//     * Actualiza la lista de la compra si el la cantidad en inventario es menor que la mínima
//     * @param inventory 
//     */
//    private void updateShoppingList(Inventory inventory) {
//        if (inventory.getCantidad() < inventory.getCantidadMinima()) {
//            Purchase purchase = new Purchase(
//                    0, // ID aún no asignado
//                    inventory.getId(),
//                    inventory.getCantidadMinima() - inventory.getCantidad(),
//                    "2025-04-06", // Fecha simulada
//                    inventory.getIdGrupo(),
//                    inventory.getNombreProducto()
//            );
//
//            // Comprueba si ya existe en la lista
//            boolean exists = shoppingList.stream().anyMatch(p -> p.getIdInventario() == inventory.getId());
//            if (!exists) {
//                shoppingList.add(purchase);
//            }
//        } else {
//            // Elimina de la lista de la compra si ya no cumple la condición
//            shoppingList.removeIf(p -> p.getIdInventario() == inventory.getId());
//        }
//
//        // Refresca la tabla de la lista de la compra
//        tableViewShoppingList.refresh();
//    }
    /**
     * Actualiza la lista de la compra si la cantidad en inventario es menor que
     * la mínima.
     *
     * @param inventory El objeto de inventario que se evaluará.
     */
    private void updateShoppingList(Inventory inventory) {
        // Verifica si la cantidad actual es menor que la cantidad mínima
        if (inventory.getCantidad() < inventory.getCantidadMinima()) {
            // Calcula la cantidad necesaria para alcanzar la cantidad mínima
            int cantidadNecesaria = inventory.getCantidadMinima() - inventory.getCantidad();

            // Crea un nuevo objeto de compra con la cantidad necesaria
            Purchase purchase = new Purchase(
                    0, // ID aún no asignado
                    inventory.getId(),
                    cantidadNecesaria, // Cantidad necesaria calculada
                    "2025-04-06", // Fecha simulada
                    inventory.getIdGrupo(),
                    inventory.getNombreProducto()
            );

            // Comprueba si ya existe en la lista
            boolean exists = shoppingList.stream().anyMatch(p -> p.getIdInventario() == inventory.getId());
            if (!exists) {
                shoppingList.add(purchase); // Añade el producto si no existe
                System.out.println("Producto añadido a la lista: " + purchase.getNombreProducto() + ", cantidad: " + cantidadNecesaria);
            } else {
                // Si ya existe, actualiza la cantidad necesaria
                shoppingList.stream()
                        .filter(p -> p.getIdInventario() == inventory.getId())
                        .forEach(p -> {
                            p.setCantidadNecesaria(cantidadNecesaria);
                            System.out.println("Cantidad actualizada en la lista: " + p.getNombreProducto() + ", cantidad: " + cantidadNecesaria);
                        });
            }
        } else {
            // Elimina de la lista de la compra si ya no cumple la condición
            boolean removed = shoppingList.removeIf(p -> p.getIdInventario() == inventory.getId());
            if (removed) {
                System.out.println("Producto eliminado de la lista: " + inventory.getNombreProducto());
            }
        }

        // Refresca la tabla de la lista de la compra
        tableViewShoppingList.refresh();
    }

}
