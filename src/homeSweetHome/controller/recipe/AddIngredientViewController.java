package homeSweetHome.controller.recipe;

import homeSweetHome.controller.purchase.*;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.model.Product;
import homeSweetHome.utils.AlertUtils;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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

        // Configurar las opciones de medida
        Measure.getItems().addAll("Cantidad", "Gramos");

        // Configurar las opciones de categoría
        category.getItems().addAll("Carnes", "Pescados", "Verduras", "Frutas", "Lácteos", "Otros");

    }

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
     * crea un objeto {@code Product} con los detalles del ingrediente y lo
     * envía al controlador principal para ser añadido a la tabla de
     * ingredientes en la vista de creación de recetas.
     *
     * @param event Evento del botón "Crear Ingrediente".
     */
//    @FXML
//    private void createAddIngredient(ActionEvent event) {
//        // Obtener y validar los valores introducidos por el usuario
//        String ingredientName = fieldIngredientName.getText().trim();
//        String ingredientQuantityStr = fieldIngredientQuantity.getText().trim();
//        String ingredientMeasure = Measure.getValue();
//        String ingredientCategory = category.getValue();
//
//        // Verificar si alguno de los campos está vacío
//        if (ingredientName.isEmpty() || ingredientQuantityStr.isEmpty() || ingredientMeasure == null || ingredientCategory == null) {
//            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
//            return;
//        }
//
//        // Validar que la cantidad sea un número positivo
//        int ingredientQuantity;
//        try {
//            ingredientQuantity = Integer.parseInt(ingredientQuantityStr);
//            if (ingredientQuantity <= 0) {
//                throw new NumberFormatException();
//            }
//        } catch (NumberFormatException e) {
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Cantidad Inválida", "La cantidad debe ser un número positivo.");
//            return;
//        }
//
//        // Crear el objeto Product con los datos del ingrediente
//        Product ingredient = new Product();
//        ingredient.setNombreProducto(ingredientName);  // Nombre del ingrediente
//        ingredient.setCantidad(ingredientQuantity);   // Cantidad requerida
//        ingredient.setTipo(ingredientMeasure);      // Unidad de medida (gramos, litros, etc.)
//        ingredient.setCategoria(ingredientCategory);  // Categoría del ingrediente (Carnes, Verduras, etc.)
//
//        // Enviar el ingrediente al controlador principal para añadirlo a la tabla
//        if (createRecipeController != null) {
//            createRecipeController.addIngredientToTable(ingredient);
//        }
//
//        // Cerrar la ventana después de añadir el ingrediente
//        ((Stage) btnAddIngredient.getScene().getWindow()).close();
//    }
//    @FXML
//    private void createAddIngredient(ActionEvent event) {
//        // Validar los campos introducidos por el usuario
//        String ingredientName = fieldIngredientName.getText().trim();
//        String ingredientQuantityStr = fieldIngredientQuantity.getText().trim();
//        String ingredientMeasure = Measure.getValue();
//        String ingredientCategory = category.getValue();
//
//        if (ingredientName.isEmpty() || ingredientQuantityStr.isEmpty() || ingredientMeasure == null || ingredientCategory == null) {
//            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
//            return;
//        }
//
//        // Validar la cantidad como número positivo
//        int ingredientQuantity;
//        try {
//            ingredientQuantity = Integer.parseInt(ingredientQuantityStr);
//            if (ingredientQuantity <= 0) {
//                throw new NumberFormatException();
//            }
//        } catch (NumberFormatException e) {
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Cantidad Inválida", "La cantidad debe ser un número positivo.");
//            return;
//        }
//
//        // Crear el objeto Product con los datos del ingrediente
//        Product ingredient = new Product();
//        ingredient.setNombreProducto(ingredientName);
//        ingredient.setCantidad(ingredientQuantity);
//        ingredient.setTipo(ingredientMeasure);
//        ingredient.setCategoria(ingredientCategory);
//
//        // Añadir el ingrediente a la tabla en el controlador principal
//        if (createRecipeController != null) {
//            createRecipeController.addIngredientToTable(ingredient);
//        }
//
//        // Preguntar si desea añadir más ingredientes
//        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir otro ingrediente?");
//        confirmationAlert.setTitle("Añadir Más Ingredientes");
//        confirmationAlert.setHeaderText(null);
//
//        // Mostrar la alerta y manejar la respuesta
//        ButtonType buttonYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
//        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
//        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);
//
//        Optional<ButtonType> result = confirmationAlert.showAndWait();
//        if (result.isPresent() && result.get() == buttonNo) {
//            // Cerrar la ventana si el usuario selecciona "No"
//            ((Stage) btnAddIngredient.getScene().getWindow()).close();
//        } else {
//            // Limpiar los campos si el usuario selecciona "Sí"
//            fieldIngredientName.clear();
//            fieldIngredientQuantity.clear();
//            Measure.getSelectionModel().clearSelection();
//            category.getSelectionModel().clearSelection();
//        }
//    }
//    @FXML
//    private void createAddIngredient(ActionEvent event) {
//        // Validar los campos introducidos por el usuario
//        String ingredientName = fieldIngredientName.getText().trim();
//        String ingredientQuantityStr = fieldIngredientQuantity.getText().trim();
//        String ingredientMeasure = Measure.getValue();
//
//        if (ingredientName.isEmpty() || ingredientQuantityStr.isEmpty() || ingredientMeasure == null) {
//            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
//            return;
//        }
//
//        // Validar la cantidad como número positivo
//        int ingredientQuantity;
//        try {
//            ingredientQuantity = Integer.parseInt(ingredientQuantityStr);
//            if (ingredientQuantity <= 0) {
//                throw new NumberFormatException();
//            }
//        } catch (NumberFormatException e) {
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Cantidad Inválida", "La cantidad debe ser un número positivo.");
//            return;
//        }
//
//        // Crear el objeto Product con los datos del ingrediente
//        Product ingredient = new Product();
//        ingredient.setNombreProducto(ingredientName);
//        ingredient.setCantidad(ingredientQuantity);
//        ingredient.setTipo(ingredientMeasure);
//        ingredient.setCategoria("Alimentación"); // Asignar categoría por defecto
//
//        // Añadir el ingrediente a la tabla en el controlador principal
//        if (createRecipeController != null) {
//            createRecipeController.addIngredientToTable(ingredient);
//        }
//
//        // Preguntar si desea añadir más ingredientes
//        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir otro ingrediente?");
//        confirmationAlert.setTitle("Añadir Más Ingredientes");
//        confirmationAlert.setHeaderText(null);
//
//        // Mostrar la alerta y manejar la respuesta
//        ButtonType buttonYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
//        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
//        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);
//
//        Optional<ButtonType> result = confirmationAlert.showAndWait();
//        if (result.isPresent() && result.get() == buttonNo) {
//            // Cerrar la ventana si el usuario selecciona "No"
//            ((Stage) btnAddIngredient.getScene().getWindow()).close();
//        } else {
//            // Limpiar los campos si el usuario selecciona "Sí"
//            fieldIngredientName.clear();
//            fieldIngredientQuantity.clear();
//            Measure.getSelectionModel().clearSelection();
//        }
//    }
    @FXML
    private void createAddIngredient(ActionEvent event) {
        // Validar los campos introducidos por el usuario
        String ingredientName = fieldIngredientName.getText().trim();
        String ingredientQuantityStr = fieldIngredientQuantity.getText().trim();
        String ingredientMeasure = Measure.getValue();

        if (ingredientName.isEmpty() || ingredientQuantityStr.isEmpty() || ingredientMeasure == null) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
            return;
        }

        // Validar la cantidad como número positivo
        int ingredientQuantity;
        try {
            ingredientQuantity = Integer.parseInt(ingredientQuantityStr);
            if (ingredientQuantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Cantidad Inválida", "La cantidad debe ser un número positivo.");
            return;
        }

        // Crear el objeto Product con los datos del ingrediente
        Product ingredient = new Product();
        ingredient.setNombreProducto(ingredientName);
        ingredient.setCantidad(ingredientQuantity);
        ingredient.setTipo(ingredientMeasure);
        ingredient.setCategoria("Alimentación"); // Asignar categoría por defecto

        // Determinar el controlador principal (creación o modificación)
        if (createRecipeController != null) {
            createRecipeController.addIngredientToTable(ingredient);
        } else if (modifyRecipeController != null) {
            modifyRecipeController.addIngredientToTable(ingredient);
        } else {
            System.err.println("No se ha establecido un controlador principal.");
            return;
        }

        // Preguntar si desea añadir más ingredientes
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas añadir otro ingrediente?");
        confirmationAlert.setTitle("Añadir Más Ingredientes");
        confirmationAlert.setHeaderText(null);

        // Mostrar la alerta y manejar la respuesta
        ButtonType buttonYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonNo) {
            // Cerrar la ventana si el usuario selecciona "No"
            ((Stage) btnAddIngredient.getScene().getWindow()).close();
        } else {
            // Limpiar los campos si el usuario selecciona "Sí"
            fieldIngredientName.clear();
            fieldIngredientQuantity.clear();
            Measure.getSelectionModel().clearSelection();
        }
    }

}
