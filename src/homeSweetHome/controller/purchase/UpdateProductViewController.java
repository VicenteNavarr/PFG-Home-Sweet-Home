package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.model.Product;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la vista para actualizar productos.
 */
public class UpdateProductViewController {

    @FXML
    private TextField fieldProductName;
    @FXML
    private TextField fieldProductCurrentQuantity;
    @FXML
    private TextField fieldProductMinQuantity;
    @FXML
    private ComboBox<String> Measure;
    @FXML
    private ComboBox<String> category;
    @FXML
    private Button btnUpdateProduct;
    @FXML
    private Button btnDeleteProduct;
    @FXML
    private Button btnCancelProduct;
    @FXML
    private Label modifyProductTitle;

    private Product product; // El producto a modificar

    private PurchaseViewController purchaseViewController; // Referencia al controlador principal

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
     * Configura las traducciones
     */
    private void updateTexts() {

        // Accede directamente al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Traducir los textos de los botones
        btnUpdateProduct.setText(languageManager.getTranslation("updateProduct"));
        btnCancelProduct.setText(languageManager.getTranslation("cancelProduct"));

        modifyProductTitle.setText(languageManager.getTranslation("modifyProductTitle"));

        // Traducir los PromptText de los campos de texto
        fieldProductName.setPromptText(languageManager.getTranslation("productNamePrompt"));
        fieldProductCurrentQuantity.setPromptText(languageManager.getTranslation("currentQuantityPrompt"));
        fieldProductMinQuantity.setPromptText(languageManager.getTranslation("minQuantityPrompt"));

        // Establecer PromptText en los ComboBox y traducir opciones dinámicamente
        Measure.setPromptText(languageManager.getTranslation("measurePrompt"));
        Measure.getItems().clear();
        Measure.getItems().addAll(
                languageManager.getTranslation("measureQuantity"),
                languageManager.getTranslation("measureGrams")
        );

        category.setPromptText(languageManager.getTranslation("categoryPrompt"));
        category.getItems().clear();
        category.getItems().addAll(
                languageManager.getTranslation("categoryFood"),
                languageManager.getTranslation("categoryDrinks"),
                languageManager.getTranslation("categoryCleaning"),
                languageManager.getTranslation("categoryHygiene"),
                languageManager.getTranslation("categoryOthers")
        );

        // Depuración
        System.out.println("Traducciones y PromptText aplicados correctamente en CreateProductViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    
    /**
     * Establece el producto seleccionado.
     *
     * @param product El producto seleccionado en la tabla.
     */
    public void setProduct(Product product) {

        this.product = product;
        loadProductData();
    }

    /**
     * Carga los datos del producto en los campos de la vista.
     */
    private void loadProductData() {

        fieldProductName.setText(product.getNombreProducto());
        fieldProductCurrentQuantity.setText(String.valueOf(product.getCantidad()));
        fieldProductMinQuantity.setText(String.valueOf(product.getCantidadMinima()));
        Measure.getSelectionModel().select(product.getTipo());
        category.getSelectionModel().select(product.getCategoria());
    }

    /**
     * Establece la referencia al controlador principal.
     *
     * @param purchaseViewController El controlador principal.
     */
    public void setPurchaseViewController(PurchaseViewController purchaseViewController) {

        this.purchaseViewController = purchaseViewController;
    }

    /**
     * Actualiza el producto en la base de datos y refresca la tabla principal.
     */
    @FXML
    private void updateProduct() {

        try {

            // Actualiza los datos del producto
            product.setNombreProducto(fieldProductName.getText().trim());
            product.setCantidad(Integer.parseInt(fieldProductCurrentQuantity.getText().trim()));
            product.setCantidadMinima(Integer.parseInt(fieldProductMinQuantity.getText().trim()));
            product.setTipo(Measure.getSelectionModel().getSelectedItem());
            product.setCategoria(category.getSelectionModel().getSelectedItem());

            // Actualiza en la base de datos
            InventoryDAO inventoryDAO = new InventoryDAO();
            boolean success = inventoryDAO.updateInventoryProduct(product);

            if (success) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El producto se ha actualizado correctamente.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "The product has been updated successfully.");

                }

                purchaseViewController.loadInventory(CurrentSession.getInstance().getUserGroupId());
                ((Stage) btnUpdateProduct.getScene().getWindow()).close(); // Cierra la ventana

            } else {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar actualizar el producto.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "There was a problem updating the product.");

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar actualizar el producto.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "There was a problem updating the product.");

            }
        }
    }

    /**
     * Elimina el producto de la base de datos y refresca la tabla principal.
     */
    @FXML
    private void deleteProduct() {

        int userGroupId = CurrentSession.getInstance().getUserGroupId();

        // Crear alerta de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
        alert.setTitle(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Confirmación" : "Confirmation");
        alert.setHeaderText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Eliminar producto" : "Delete Product");
        alert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "¿Seguro que desea eliminar este producto?" : "Are you sure you want to delete this product?");

        // Agregar botones manualmente
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                
                InventoryDAO inventoryDAO = new InventoryDAO();
                boolean success = inventoryDAO.deleteInventoryProduct(product.getId());

                purchaseViewController.loadInventory(userGroupId);

                if (success) {
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                    successAlert.setTitle(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Éxito" : "Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "El producto se ha eliminado correctamente." : "The product has been successfully deleted.");
                    successAlert.showAndWait();

                    if (purchaseViewController != null) {
                        
                        System.out.println("Eliminando producto: " + product.getNombreProducto());

                        // Actualiza la lista de compras directamente
                        purchaseViewController.getShoppingList().removeIf(p -> p.getId() == product.getId());

                        // Repinta la tabla con los datos actualizados
                        purchaseViewController.repaintShoppingListTable();

                        System.out.println("Tabla de lista de compras repintada tras eliminar el producto.");
                        
                    } else {
                        
                        System.err.println("Error: No se ha establecido el controlador principal.");
                    }

                    ((Stage) btnDeleteProduct.getScene().getWindow()).close();
                    
                } else {
                    
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Hubo un problema al intentar eliminar el producto." : "There was a problem deleting the product.");
                    errorAlert.showAndWait();
                }

            } catch (Exception e) {
                
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText(LanguageManager.getInstance().getLanguageCode().equals("es") ? "Hubo un problema al intentar eliminar el producto." : "There was a problem deleting the product.");
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

        ((Stage) btnCancelProduct.getScene().getWindow()).close(); // Cierra la ventana actual
    }
}
