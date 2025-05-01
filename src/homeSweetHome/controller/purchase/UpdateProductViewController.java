package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.model.Product;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

    private Product product; // El producto a modificar

    private PurchaseViewController purchaseViewController; // Referencia al controlador principal

    /**
     * Método para inicializar el controlador.
     */
    @FXML
    public void initialize() {
       
/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////               

    }
    
/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
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
                
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El producto se ha actualizado correctamente.");
                purchaseViewController.loadInventory(CurrentSession.getInstance().getUserGroupId());
                ((Stage) btnUpdateProduct.getScene().getWindow()).close(); // Cierra la ventana
                
            } else {
                
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar actualizar el producto.");
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al intentar actualizar el producto.");
        }
    }

    /**
     * Elimina el producto de la base de datos y refresca la tabla principal.
     */
    @FXML
    private void deleteProduct() {
        
        try {
            
            InventoryDAO inventoryDAO = new InventoryDAO();
            boolean success = inventoryDAO.deleteInventoryProduct(product.getId());

            if (success) {
                
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El producto se ha eliminado correctamente.");

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
                
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar eliminar el producto.");
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al intentar eliminar el producto.");
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
