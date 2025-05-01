package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.model.Product;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
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

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////                

    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {
        
        // Accede directamente al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {
            
            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Traducir los textos de los botones
        btnCreateProduct.setText(languageManager.getTranslation("createProduct")); 
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
            
            // Recupera los valores de los campos de entrada
            String nombreProducto = fieldProductName.getText().trim();
            String medida = Measure.getSelectionModel().getSelectedItem();
            String categoriaSeleccionada = category.getSelectionModel().getSelectedItem();
            String cantidadActualTexto = fieldProductCurrentQuantity.getText().trim();
            String cantidadMinimaTexto = fieldProductMinQuantity.getText().trim();

            // Trazas: verificar los valores ingresados
            System.out.println("=== Valores ingresados ===");
            System.out.println("Nombre del producto: " + nombreProducto);
            System.out.println("Medida seleccionada: " + medida);
            System.out.println("Categoría seleccionada: " + categoriaSeleccionada);
            System.out.println("Cantidad actual texto: " + cantidadActualTexto);
            System.out.println("Cantidad mínima texto: " + cantidadMinimaTexto);

            // Valida que los campos no estén vacíos
            if (nombreProducto.isEmpty() || medida == null || categoriaSeleccionada == null
                    || cantidadActualTexto.isEmpty() || cantidadMinimaTexto.isEmpty()) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de continuar.");
                return;
            }

            // Valida que las cantidades sean números válidos
            int cantidadActual;
            int cantidadMinima;
            
            try {
                
                cantidadActual = Integer.parseInt(cantidadActualTexto);
                cantidadMinima = Integer.parseInt(cantidadMinimaTexto);

                // Trazas: después de convertir las cantidades
                System.out.println("Cantidad actual numérica: " + cantidadActual);
                System.out.println("Cantidad mínima numérica: " + cantidadMinima);

            } catch (NumberFormatException e) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Cantidad inválida",
                        "Las cantidades deben ser números válidos.");
                return;
            }

            // Verifica si el producto ya existe en el inventario
            InventoryDAO inventoryDAO = new InventoryDAO();
            int userGroupId = CurrentSession.getInstance().getUserGroupId();

            System.out.println("ID del grupo del usuario: " + userGroupId);

            if (inventoryDAO.isProductInInventory(nombreProducto, userGroupId)) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Producto duplicado",
                        "El producto ya existe en el inventario.");
                return;
            }

            // Crea el nuevo producto usando la clase `Product`
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

            // Trazas: Verifica que el producto se crea correctamente
            System.out.println("=== Producto creado ===");
            System.out.println("Nombre: " + nuevoProducto.getNombreProducto());
            System.out.println("Cantidad actual: " + nuevoProducto.getCantidad());
            System.out.println("Cantidad mínima: " + nuevoProducto.getCantidadMinima());
            System.out.println("Medida: " + nuevoProducto.getTipo());
            System.out.println("Categoría: " + nuevoProducto.getCategoria());
            System.out.println("Grupo: " + nuevoProducto.getIdGrupo());

            // Guarda el producto en el inventario
            boolean success = inventoryDAO.addInventoryProduct(nuevoProducto);

            // Trazas: Verificar si la inserción fue exitosa
            if (success) {
                
                System.out.println("El producto se guardó correctamente en el inventario.");
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito",
                        "El producto se ha creado correctamente.");

                // Refresca la tabla en el controlador principal
                if (purchaseViewController != null) {
                    
                    System.out.println("Recargando datos de inventario...");
                    purchaseViewController.loadInventory(userGroupId);
                }

                // Cierra la ventana actual
                ((Stage) btnCreateProduct.getScene().getWindow()).close();

            } else {
                
                System.err.println("Hubo un problema al guardar el producto en el inventario.");
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
            
            // Valida y recupera los datos de los campos
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

            // Crea y devuelve el objeto Product
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
