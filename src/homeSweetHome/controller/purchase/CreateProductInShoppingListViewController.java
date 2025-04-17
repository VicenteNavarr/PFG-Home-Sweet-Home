package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.dataPersistence.PurchaseDAO;
import homeSweetHome.model.Product;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la vista para la creación de productos en la lista de compras.
 */
public class CreateProductInShoppingListViewController implements Initializable {

    @FXML
    private TextField fieldProductName; // Campo de texto para el nombre del producto
    @FXML
    private TextField fieldProductCurrentQuantity; // Campo de texto para la cantidad actual (necesaria para la compra)
    @FXML
    private ComboBox<String> Measure; // ComboBox para seleccionar la medida (Cantidad o Gramos)
    @FXML
    private ComboBox<String> category; // ComboBox para seleccionar la categoría
    @FXML
    private Button btnCreateProduct; // Botón para crear el producto
    @FXML
    private Button btnCancelProduct; // Botón para cancelar la creación del producto

    private PurchaseViewController purchaseViewController; // Referencia al controlador principal

    /**
     * Método para inicializar el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Rellenar las opciones del ComboBox de medidas
        Measure.getItems().addAll("Cantidad", "Gramos");

        // Rellenar las opciones del ComboBox de categorías
        category.getItems().addAll("Alimentación", "Bebidas", "Limpieza", "Higiene", "Otros");
    }

    /**
     * Método para establecer la referencia al controlador principal.
     *
     * @param purchaseViewController - Controlador principal que gestiona la
     * vista.
     */
    public void setPurchaseViewController(PurchaseViewController purchaseViewController) {
        this.purchaseViewController = purchaseViewController;
    }

    /**
     * Método para crear un nuevo producto y añadirlo a la lista de compras,
     * gestionando su existencia en el inventario.
     *
     * @param event Evento activado al presionar el botón "Crear".
     */
//    @FXML
//    private void createNewProduct(ActionEvent event) {
//        try {
//            // Recuperar los valores de los campos de entrada
//            String nombreProducto = fieldProductName.getText().trim();
//            String medida = Measure.getSelectionModel().getSelectedItem();
//            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
//            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();
//
//            // Validar que los campos no estén vacíos
//            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null || cantidadActualTexto.isEmpty()) {
//                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
//                        "Por favor completa todos los campos antes de continuar.");
//                return;
//            }
//
//            // Validar que la cantidad sea un número válido
//            int cantidadNecesaria;
//            try {
//                cantidadNecesaria = Integer.parseInt(cantidadActualTexto);
//            } catch (NumberFormatException e) {
//                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
//                        "La cantidad debe ser un número válido.");
//                return;
//            }
//
//            // Obtener el ID del grupo de usuario actual
//            int userGroupId = CurrentSession.getInstance().getUserGroupId();
//
//            // Validar si el producto ya existe en el inventario
//            InventoryDAO inventoryDAO = new InventoryDAO();
//            PurchaseDAO purchaseDAO = new PurchaseDAO();
//
//            boolean productExists = inventoryDAO.isProductInInventory(nombreProducto, userGroupId);
//            int idInventario;
//
//            if (productExists) {
//                // Si el producto existe, obtener su ID directamente
//                idInventario = inventoryDAO.getInventoryProductIdByName(nombreProducto, userGroupId);
//                System.out.println("Producto encontrado en el inventario: ID " + idInventario);
//            } else {
//                // Si no existe, generar un nuevo ID único y añadirlo al inventario
//                idInventario = inventoryDAO.generateNewInventoryId();
//                Product nuevoProductoInventario = new Product(
//                        idInventario,
//                        nombreProducto,
//                        0, // Cantidad inicial en el inventario
//                        0, // Cantidad mínima
//                        0, // Cantidad máxima
//                        medida,
//                        categoriaSeleccionada,
//                        userGroupId,
//                        null
//                );
//
//                boolean addedToInventory = inventoryDAO.addInventoryProduct(nuevoProductoInventario);
//                if (!addedToInventory) {
//                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                            "No se pudo añadir el producto al inventario.");
//                    return;
//                }
//                System.out.println("Producto añadido al inventario: ID " + idInventario);
//            }
//
//            // Crear el nuevo producto para la lista de compras
//            Product nuevoProductoCompra = new Product(
//                    idInventario, // ID del inventario
//                    nombreProducto,
//                    cantidadNecesaria, // Cantidad necesaria para la lista de compras
//                    0, // Cantidad mínima no relevante
//                    0, // Cantidad máxima no relevante
//                    medida,
//                    categoriaSeleccionada,
//                    userGroupId,
//                    LocalDate.now().toString()
//            );
//
//            // Guardar el producto en la lista de compras
//            boolean success = purchaseDAO.addPurchase(nuevoProductoCompra);
//
//            if (success) {
//                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito",
//                        "El producto se ha añadido correctamente a la lista de compras.");
//
//                // Refresca la tabla en el controlador principal
//                if (purchaseViewController != null) {
//                    purchaseViewController.loadShoppingList(userGroupId);
//                }
//
//                // Cierra la ventana actual
//                ((Stage) btnCreateProduct.getScene().getWindow()).close();
//            } else {
//                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                        "Hubo un problema al intentar añadir el producto a la lista de compras.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                    "Se produjo un error al intentar añadir el producto.");
//        }
//    }
    @FXML
    private void createNewProduct(ActionEvent event) {
        try {
            // Recuperar los valores de los campos de entrada
            String nombreProducto = fieldProductName.getText().trim();
            String medida = Measure.getSelectionModel().getSelectedItem();
            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();

            // Validar que los campos no estén vacíos
            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null || cantidadActualTexto.isEmpty()) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de continuar.");
                return;
            }

            // Validar que la cantidad sea un número válido
            int cantidadNecesaria;
            try {
                cantidadNecesaria = Integer.parseInt(cantidadActualTexto);
            } catch (NumberFormatException e) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
                        "La cantidad debe ser un número válido.");
                return;
            }

            // Obtener el ID del grupo de usuario actual
            int userGroupId = CurrentSession.getInstance().getUserGroupId();

            // Validar si el producto ya existe en el inventario
            InventoryDAO inventoryDAO = new InventoryDAO();

            boolean productExists = inventoryDAO.isProductInInventory(nombreProducto, userGroupId);
            int idInventario;

            if (productExists) {
                // Si el producto existe, obtener su ID directamente
                idInventario = inventoryDAO.getInventoryProductIdByName(nombreProducto, userGroupId);
                System.out.println("Producto encontrado en el inventario: ID " + idInventario);

                // Actualizar la cantidad en el inventario
                int currentQuantity = inventoryDAO.getCurrentQuantityById(idInventario);
                int updatedQuantity = currentQuantity + cantidadNecesaria;
                boolean updated = inventoryDAO.updateInventoryQuantity(idInventario, updatedQuantity);

                if (!updated) {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                            "No se pudo actualizar la cantidad del producto en el inventario.");
                    return;
                }
                System.out.println("Cantidad actualizada para el producto: " + updatedQuantity);
            } else {
                // Si no existe, generar un nuevo ID único y añadirlo al inventario
                idInventario = inventoryDAO.generateNewInventoryId();
                Product nuevoProductoInventario = new Product(
                        idInventario,
                        nombreProducto,
                        cantidadNecesaria, // Cantidad inicial en el inventario
                        0, // Cantidad mínima
                        0, // Cantidad máxima
                        medida,
                        categoriaSeleccionada,
                        userGroupId,
                        null
                );

                boolean addedToInventory = inventoryDAO.addInventoryProduct(nuevoProductoInventario);
                if (!addedToInventory) {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                            "No se pudo añadir el producto al inventario.");
                    return;
                }
                System.out.println("Producto añadido al inventario: ID " + idInventario);
            }

            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "El producto se ha añadido correctamente al inventario.");

            // Refrescar el inventario en el controlador principal
            if (purchaseViewController != null) {
                purchaseViewController.loadInventory(userGroupId);
            }

            // Cierra la ventana actual
            ((Stage) btnCreateProduct.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                    "Se produjo un error al intentar añadir el producto.");
        }
    }

    /**
     * Devuelve el objeto Product creado en la vista.
     *
     * @return El objeto Product creado, o null si los campos no son válidos.
     */
    public Product getCreatedProduct() {
        try {
            String nombreProducto = fieldProductName.getText().trim();
            String medida = Measure.getSelectionModel().getSelectedItem();
            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();

            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null || cantidadActualTexto.isEmpty()) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de continuar.");
                return null;
            }

            int cantidadNecesaria;
            try {
                cantidadNecesaria = Integer.parseInt(cantidadActualTexto);
            } catch (NumberFormatException e) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
                        "La cantidad debe ser un número válido.");
                return null;
            }

            return new Product(
                    0, // ID generado automáticamente
                    nombreProducto,
                    cantidadNecesaria,
                    0,
                    0,
                    medida,
                    categoriaSeleccionada,
                    CurrentSession.getInstance().getUserGroupId(),
                    LocalDate.now().toString()
            );
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                    "Se produjo un error al intentar crear el producto.");
            return null;
        }
    }

    /**
     * Método para cancelar y cerrar la ventana.
     *
     * @param event - Evento del botón.
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) btnCancelProduct.getScene().getWindow()).close(); // Cierra la ventana actual
    }
}
