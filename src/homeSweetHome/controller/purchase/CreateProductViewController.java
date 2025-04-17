//package homeSweetHome.controller.purchase;
//
//import homeSweetHome.dataPersistence.CurrentSession;
//import homeSweetHome.dataPersistence.InventoryDAO;
//import homeSweetHome.model.Inventory;
//import homeSweetHome.model.Purchase;
//import homeSweetHome.utils.AlertUtils;
//import java.net.URL;
//import java.time.LocalDate;
//import java.util.ResourceBundle;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//
///**
// * Controlador de la vista para la creación de productos en el inventario.
// */
//public class CreateProductViewController implements Initializable {
//
//    @FXML
//    private TextField fieldProductName; // Campo de texto para el nombre del producto
//    @FXML
//    private TextField fieldProductCurrentQuantity; // Campo de texto para la cantidad actual
//    @FXML
//    private TextField fieldProductMinQuantity; // Campo de texto para la cantidad mínima
//    @FXML
//    private ComboBox<String> Measure; // ComboBox para seleccionar la medida (Cantidad o Gramos)
//    @FXML
//    private ComboBox<String> category; // ComboBox para seleccionar la categoría
//    @FXML
//    private Button btnCreateProduct; // Botón para crear el producto
//    @FXML
//    private Button btnCancelProduct; // Botón para cancelar la creación del producto
//
//    private PurchaseViewController purchaseViewController; // Referencia al controlador principal
//
//    /**
//     * Método para inicializar el controlador.
//     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        // Rellenar las opciones del ComboBox de medidas
//        Measure.getItems().addAll("Cantidad", "Gramos");
//
//        // Rellenar las opciones del ComboBox de categorías
//        category.getItems().addAll("Alimentación", "Bebidas", "Limpieza", "Higiene", "Otros");
//    }
//
//    /**
//     * Método para establecer la referencia al PurchaseViewController principal.
//     *
//     * @param purchaseViewController - Controlador principal
//     */
//    public void setPurchaseViewController(PurchaseViewController purchaseViewController) {
//        this.purchaseViewController = purchaseViewController;
//    }
//
//    /**
//     * Método para crear un nuevo producto y guardarlo en la base de datos.
//     *
//     * @param event - Evento activado al presionar el botón "Crear".
//     */
//    @FXML
//    private void createNewProduct(ActionEvent event) {
//        try {
//            // Recuperar los valores de los campos de entrada
//            String nombreProducto = fieldProductName.getText().trim();
//            String medida = Measure.getSelectionModel().getSelectedItem();
//            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
//            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();
//            String cantidadMinimaTexto = fieldProductMinQuantity.getText().trim();
//
//            // Validar que los campos no estén vacíos
//            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null
//                    || cantidadActualTexto.isEmpty() || cantidadMinimaTexto.isEmpty()) {
//                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
//                        "Por favor completa todos los campos antes de continuar.");
//                return;
//            }
//
//            // Validar que las cantidades sean números válidos
//            int cantidadActual;
//            int cantidadMinima;
//            try {
//                cantidadActual = Integer.parseInt(cantidadActualTexto);
//                cantidadMinima = Integer.parseInt(cantidadMinimaTexto);
//            } catch (NumberFormatException e) {
//                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
//                        "Las cantidades deben ser números válidos.");
//                return;
//            }
//
//            // Verificar si el producto ya existe en el inventario
//            InventoryDAO inventoryDAO = new InventoryDAO();
//            int userGroupId = CurrentSession.getInstance().getUserGroupId();
//
//            if (inventoryDAO.isProductInInventory(nombreProducto, userGroupId)) {
//                AlertUtils.showAlert(Alert.AlertType.WARNING, "Producto duplicado",
//                        "El producto ya existe en el inventario.");
//                return;
//            }
//
//            // Crear el nuevo producto
//            Inventory nuevoProducto = new Inventory(
//                    0, // ID (se generará automáticamente)
//                    nombreProducto, // Nombre del producto
//                    cantidadActual, // Cantidad actual
//                    cantidadMinima, // Cantidad mínima
//                    0, // Cantidad máxima (opcional)
//                    categoriaSeleccionada, // Categoría seleccionada
//                    medida, // Tipo de medida (Cantidad o Gramos)
//                    userGroupId // ID del grupo del usuario
//            );
//
//            // Guardar el producto en la base de datos
//            boolean success = inventoryDAO.addInventoryProduct(nuevoProducto);
//
//            if (success) {
//                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito",
//                        "El producto se ha creado correctamente.");
//
//                // Refresca la tabla en el controlador principal
//                if (purchaseViewController != null) {
//                    purchaseViewController.loadInventory(userGroupId);
//                }
//
//                ((Stage) btnCreateProduct.getScene().getWindow()).close(); // Cierra la ventana
//            } else {
//                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                        "Hubo un problema al intentar crear el producto.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
//                    "Se produjo un error al intentar crear el producto.");
//        }
//    }
//
//    /**
//     * Devuelve el objeto Purchase creado en la vista.
//     *
//     * @return El objeto Purchase creado, o null si los campos no son válidos.
//     */
////    public Purchase getCreatedPurchase() {
////        try {
////            // Validar y recuperar los datos de los campos
////            String nombreProducto = fieldProductName.getText().trim();
////            String medida = Measure.getSelectionModel().getSelectedItem();
////            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();
////
////            // Validar que los campos no estén vacíos
////            if (nombreProducto.isEmpty() || medida == null || cantidadActualTexto.isEmpty()) {
////                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
////                        "Por favor completa todos los campos antes de continuar.");
////                return null; // No hay producto válido que devolver
////            }
////
////            // Validar que la cantidad sea un número válido
////            int cantidadActual;
////            try {
////                cantidadActual = Integer.parseInt(cantidadActualTexto);
////            } catch (NumberFormatException e) {
////                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
////                        "La cantidad debe ser un número válido.");
////                return null; // No hay producto válido que devolver
////            }
////
////            // Crear y devolver el objeto Purchase
////            return new Purchase(
////                    0, // ID generado automáticamente
////                    0, // ID del inventario (por defecto 0 porque aún no está vinculado)
////                    cantidadActual, // Cantidad necesaria
////                    LocalDate.now().toString(), // Fecha actual
////                    CurrentSession.getInstance().getUserGroupId(), // ID del grupo actual
////                    nombreProducto, // Nombre del producto
////                    medida // Tipo de medida (Cantidad o Gramos)
////            );
////        } catch (Exception e) {
////            e.printStackTrace();
////            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
////                    "Se produjo un error al intentar obtener los datos del producto.");
////            return null; // No hay producto válido que devolver
////        }
////    }
//
//    /**
//     * Método para cancelar y cerrar la ventana.
//     *
//     * @param event - Evento del botón.
//     */
//    @FXML
//    private void cancel(ActionEvent event) {
//        ((Stage) btnCancelProduct.getScene().getWindow()).close(); // Cierra la ventana actual
//    }
//}
package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
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
 * Controlador de la vista para la creación de productos en el inventario.
 * Permite también la integración de productos con la lista de compras.
 */
public class CreateProductViewController implements Initializable {

    @FXML
    private TextField fieldProductName; // Campo de texto para el nombre del producto
    @FXML
    private TextField fieldProductCurrentQuantity; // Campo de texto para la cantidad actual
    @FXML
    private TextField fieldProductMinQuantity; // Campo de texto para la cantidad mínima
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
     * Método para crear un nuevo producto y guardarlo en el inventario.
     *
     * @param event - Evento activado al presionar el botón "Crear".
     */
    @FXML
    private void createNewProduct(ActionEvent event) {
        try {
            // Recuperar los valores de los campos de entrada
            String nombreProducto = fieldProductName.getText().trim();
            String medida = Measure.getSelectionModel().getSelectedItem();
            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();
            String cantidadMinimaTexto = fieldProductMinQuantity.getText().trim();

            // Validar que los campos no estén vacíos
            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null
                    || cantidadActualTexto.isEmpty() || cantidadMinimaTexto.isEmpty()) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de continuar.");
                return;
            }

            // Validar que las cantidades sean números válidos
            int cantidadActual;
            int cantidadMinima;
            try {
                cantidadActual = Integer.parseInt(cantidadActualTexto);
                cantidadMinima = Integer.parseInt(cantidadMinimaTexto);
            } catch (NumberFormatException e) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
                        "Las cantidades deben ser números válidos.");
                return;
            }

            // Verificar si el producto ya existe en el inventario
            InventoryDAO inventoryDAO = new InventoryDAO();
            int userGroupId = CurrentSession.getInstance().getUserGroupId();

            if (inventoryDAO.isProductInInventory(nombreProducto, userGroupId)) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Producto duplicado",
                        "El producto ya existe en el inventario.");
                return;
            }

            // Crear el nuevo producto usando la clase `Product`
            Product nuevoProducto = new Product(
                    0, // ID generado automáticamente
                    nombreProducto, // Nombre del producto
                    cantidadActual, // Cantidad actual
                    cantidadMinima, // Cantidad mínima
                    0, // Cantidad máxima (opcional)
                    medida, // Tipo de medida (Cantidad o Gramos)
                    categoriaSeleccionada, // Categoría seleccionada
                    userGroupId, // ID del grupo del usuario
                    null // Fecha no aplica para inventario
            );

            // Guardar el producto en el inventario
            boolean success = inventoryDAO.addInventoryProduct(nuevoProducto);

            if (success) {
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito",
                        "El producto se ha creado correctamente.");

                // Refresca la tabla en el controlador principal
                if (purchaseViewController != null) {
                    purchaseViewController.loadInventory(userGroupId);
                }

                // Cierra la ventana actual
                ((Stage) btnCreateProduct.getScene().getWindow()).close();
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al intentar crear el producto.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                    "Se produjo un error al intentar crear el producto.");
        }
    }

    /**
     * Devuelve el objeto Product creado en la vista.
     *
     * @return El objeto Product creado, o null si los campos no son válidos.
     */
    public Product getCreatedProduct() {
        try {
            // Validar y recuperar los datos de los campos
            String nombreProducto = fieldProductName.getText().trim();
            String medida = Measure.getSelectionModel().getSelectedItem();
            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();

            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null || cantidadActualTexto.isEmpty()) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de continuar.");
                return null;
            }

            int cantidadActual;
            try {
                cantidadActual = Integer.parseInt(cantidadActualTexto);
            } catch (NumberFormatException e) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
                        "La cantidad debe ser un número válido.");
                return null;
            }

            // Crear y devolver el objeto Product
            return new Product(
                    0, // ID generado automáticamente
                    nombreProducto, // Nombre del producto
                    cantidadActual, // Cantidad necesaria (para lista de compras)
                    0, // Cantidad mínima no aplica en lista de compras
                    0, // Cantidad máxima no aplica en lista de compras
                    medida, // Tipo de medida (Cantidad o Gramos)
                    categoriaSeleccionada, // Categoría
                    CurrentSession.getInstance().getUserGroupId(), // ID del grupo actual
                    LocalDate.now().toString() // Fecha actual para lista de compras
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
