package homeSweetHome.controller.recipe;

import homeSweetHome.controller.purchase.*;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.model.Product;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la vista para la creación de productos en el inventario.
 * Permite también la integración de productos con la lista de compras.
 */
public class AddIngredientViewController implements Initializable {

    @FXML
    private ComboBox<String> Measure; // ComboBox para seleccionar la medida (Cantidad o Gramos)
    @FXML
    private ComboBox<String> category; // ComboBox para seleccionar la categoría
    @FXML
    private TextField fieldIngredientName;
    @FXML
    private TextField fieldIngredientQuantity;
    @FXML
    private Button btnAddIngredient;
    @FXML
    private Button btnCancel;
    @FXML
    private Label addIngredientTitle;

    private CreateRecipeViewController createRecipeController;

    /**
     * Controlador de modificación de recetas.
     */
    private ModifyRecipeViewController modifyRecipeController;

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

        // Obtiene la instancia única del Singleton
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager es nulo. Traducción no aplicada.");
            return;
        }

        // Verificación del idioma activo
        String idiomaActivo = languageManager.getLanguageCode();
        System.out.println("Idioma activo en updateTexts(): " + idiomaActivo);

        // Traducción de etiquetas y botones
        fieldIngredientName.setPromptText(languageManager.getTranslation("ingredientNamePrompt"));
        fieldIngredientQuantity.setPromptText(languageManager.getTranslation("ingredientQuantityPrompt"));
        btnAddIngredient.setText(languageManager.getTranslation("addIngredient"));
        btnCancel.setText(languageManager.getTranslation("cancel"));

        addIngredientTitle.setText(languageManager.getTranslation("addIngredientTitle"));

        System.out.println("Etiqueta 'ingredientNamePrompt': " + fieldIngredientName.getPromptText());
        System.out.println("Etiqueta 'ingredientQuantityPrompt': " + fieldIngredientQuantity.getPromptText());
        System.out.println("Botón 'addIngredient': " + btnAddIngredient.getText());
        System.out.println("Botón 'cancel': " + btnCancel.getText());

        // Traducción y promptText de ComboBox de medidas
        Measure.getItems().setAll(
                languageManager.getTranslation("measureQuantity"),
                languageManager.getTranslation("measureGrams")
        );
        Measure.setPromptText(languageManager.getTranslation("measurePrompt"));

        System.out.println("Opciones de 'Measure': " + Measure.getItems());

        // Traducción y promptText de ComboBox de categorías
        category.getItems().setAll(
                languageManager.getTranslation("recipeCategoryMeat"),
                languageManager.getTranslation("recipeCategoryFish"),
                languageManager.getTranslation("recipeCategoryVegetables"),
                languageManager.getTranslation("recipeCategoryLegumes"),
                languageManager.getTranslation("recipeCategoryDesserts"),
                languageManager.getTranslation("recipeCategoryOthers")
        );
        category.setPromptText(languageManager.getTranslation("categoryPrompt"));

        System.out.println("Opciones de 'category': " + category.getItems());

        // Refresca UI para aplicar los cambios visualmente
        Platform.runLater(() -> fieldIngredientName.getScene().getWindow().sizeToScene());

        System.out.println("Traducciones aplicadas correctamente en AddIngredientViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   
    
    /**
     * Establece la referencia al controlador de modificación de recetas.
     *
     * @param modifyRecipeController El controlador de modificación de recetas.
     */
    public void setModifyRecipeController(ModifyRecipeViewController modifyRecipeController) {

        this.modifyRecipeController = modifyRecipeController;
    }

    /**
     * Método para establecer la referencia al controlador principal.
     *
     * @param CreateRecipeViewController - Controlador principal que gestiona la
     * vista.
     */
    public void setCreateRecipeController(CreateRecipeViewController controller) {

        this.createRecipeController = controller;
    }

    /**
     * Método para cancelar y cerrar la ventana.
     *
     * @param event - Evento del botón.
     */
    @FXML
    private void cancel(ActionEvent event) {

        ((Stage) btnCancel.getScene().getWindow()).close(); // Cierra la ventana actual
    }

    /**
     * Maneja la creación de un nuevo ingrediente en la vista de añadir
     * ingredientes. Este método valida los datos introducidos por el usuario,
     * crea un objeto con los detalles del ingrediente y lo envía al controlador
     * principal para ser añadido a la tabla de ingredientes en la vista de
     * creación de recetas.
     *
     * @param event Evento del botón "Crear Ingrediente".
     */
    @FXML
    private void createAddIngredient(ActionEvent event) {

        // Valida los campos introducidos por el usuario
        String ingredientName = fieldIngredientName.getText().trim();
        String ingredientQuantityStr = fieldIngredientQuantity.getText().trim();
        String ingredientMeasure = Measure.getValue();

        if (ingredientName.isEmpty() || ingredientQuantityStr.isEmpty() || ingredientMeasure == null) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Empty Fields", "Please complete all fields.");

            }

            return;
        }

        // Valida la cantidad como número positivo
        int ingredientQuantity;

        try {

            ingredientQuantity = Integer.parseInt(ingredientQuantityStr);

            if (ingredientQuantity <= 0) {

                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Cantidad Inválida", "La cantidad debe ser un número positivo.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "The quantity must be a positive number.");

            }

            return;
        }

        // Crea el objeto Product con los datos del ingrediente
        Product ingredient = new Product();
        ingredient.setNombreProducto(ingredientName);
        ingredient.setCantidad(ingredientQuantity);
        ingredient.setTipo(ingredientMeasure);
        ingredient.setCategoria("Alimentación"); // Asigna categoría por defecto

        // Determina el controlador principal (creación o modificación)
        if (createRecipeController != null) {

            createRecipeController.addIngredientToTable(ingredient);

        } else if (modifyRecipeController != null) {

            modifyRecipeController.addIngredientToTable(ingredient);

        } else {

            System.err.println("No se ha establecido un controlador principal.");
            return;
        }


        // Crea la alerta de confirmación
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "");

        // Aplica el estilo CSS
        confirmationAlert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());

        // Define el mensaje según el idioma
        String message = LanguageManager.getInstance().getLanguageCode().equals("en")
                ? "Do you want to add another ingredient?"
                : "¿Deseas añadir otro ingrediente?";

        confirmationAlert.setContentText(message);
        confirmationAlert.setHeaderText(null); // Elimina el encabezado

        // Define botones personalizados
        ButtonType buttonYes = LanguageManager.getInstance().getLanguageCode().equals("en")
                ? new ButtonType("Yes", ButtonBar.ButtonData.YES)
                : new ButtonType("Sí", ButtonBar.ButtonData.YES);

        ButtonType buttonNo = LanguageManager.getInstance().getLanguageCode().equals("en")
                ? new ButtonType("No", ButtonBar.ButtonData.NO)
                : new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        // Captura la respuesta del usuario
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonNo) {
            ((Stage) btnAddIngredient.getScene().getWindow()).close(); // Cierra la ventana si elige "No"
        } else {
            fieldIngredientName.clear();
            fieldIngredientQuantity.clear();
            Measure.getSelectionModel().clearSelection(); // Limpia los campos si elige "Sí"
        }
    }
}
