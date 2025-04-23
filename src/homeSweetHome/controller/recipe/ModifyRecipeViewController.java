//package homeSweetHome.controller.recipe;
//
//import homeSweetHome.controller.event.*;
//import homeSweetHome.dataPersistence.CurrentSession;
//import homeSweetHome.dataPersistence.EventDAO;
//import homeSweetHome.dataPersistence.UserDAO;
//import homeSweetHome.model.Event;
//import homeSweetHome.model.Recipe;
//import homeSweetHome.model.User;
//import homeSweetHome.utils.AlertUtils;
//import homeSweetHome.utils.TimeUtils;
//import java.net.URL;
//import java.sql.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.ResourceBundle;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.DatePicker;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import java.sql.Time;
//import java.time.LocalDate;
//import javafx.scene.control.TextArea;
//import javafx.scene.image.ImageView;
//
///**
// * Controlador de la vista para la creación de eventos.
// */
//public class ModifyRecipeViewController implements Initializable {
//
//    //private Map<String, Integer> userMap = new HashMap<>(); // Map para usuarios: nombre -> ID
//    private RecipeViewController recipeViewController; // Referencia al controlador principal
//    @FXML
//    private TextField fieldRecipeName;
//    @FXML
//    private ComboBox<?> cmbRecipeCategory;
//    @FXML
//    private Button btnAddIngredient;
//    @FXML
//    private TextArea txtAreaIngredients;
//    @FXML
//    private TextArea txtAreaInstructions;
//    @FXML
//    private ImageView imgUser;
//    @FXML
//    private Button btnLoadImage;
//    @FXML
//    private Button btnCancel;
//
//    private Recipe recipe; // Variable para almacenar la receta que se va a modificar
//    @FXML
//    private Button btnModifyRecipe;
//
//    /**
//     * Método para inicializar el controlador.
//     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//
//    }
//
//    /**
//     * Método para establecer la referencia al EventViewController principal.
//     *
//     * @param eventViewController - Controlador principal
//     */
//    public void setRecipeViewController(RecipeViewController recipeViewController) {
//        this.recipeViewController = recipeViewController;
//    }
//
//    /**
//     * Establece la receta que se va a modificar.
//     *
//     * @param recipe La receta a modificar.
//     */
//    public void setRecipe(Recipe recipe) {
//        this.recipe = recipe;
//        loadRecipeDetails(); // Lógica opcional para cargar los datos en la vista
//    }
//
//    /**
//     * Carga los detalles de la receta en los campos de la vista.
//     */
//    private void loadRecipeDetails() {
//        if (recipe != null) {
//            System.out.println("Cargando datos de la receta: " + recipe.getNombre());
//            // Aquí cargarías los datos de la receta en los campos de texto, imágenes, etc.
//        }
//    }
//
//    /**
//     * Método para cancelar y cerrar la ventana.
//     *
//     * @param event - Evento del botón
//     */
//    @FXML
//    private void cancel(ActionEvent event) {
//        ((Stage) btnCancel.getScene().getWindow()).close(); // Cierra la ventana actual
//    }
//
//    @FXML
//    private void addIngredient(ActionEvent event) {
//    }
//
//    @FXML
//    private void loadImage(ActionEvent event) {
//    }
//
//    @FXML
//    private void modifyRecipe(ActionEvent event) {
//    }
//}
package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.model.Recipe;
import homeSweetHome.model.Product;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controlador para la vista de modificación de recetas. Permite cargar, editar
 * y guardar los detalles de una receta existente.
 */
public class ModifyRecipeViewController implements Initializable {

    @FXML
    private TextField fieldRecipeName;

    @FXML
    private ComboBox<String> cmbRecipeCategory;

    @FXML
    private Button btnAddIngredient;

    @FXML
    private TextArea txtAreaInstructions;

    @FXML
    private ImageView imgRecipe;

    @FXML
    private Button btnLoadImage;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnModifyRecipe;

    @FXML
    private TableView<Product> tableIngredients;

    @FXML
    private TableColumn<Product, String> colIngredientName;

    @FXML
    private TableColumn<Product, Integer> colIngredientQuantity;

    @FXML
    private TableColumn<Product, String> colIngredientUnit;

    @FXML
    private TableColumn<Product, Void> deleteColumn;

    private RecipeViewController recipeViewController;

    private Recipe recipe;

    private File selectedImageFile;

    private Image recipeImage;

    private final RecipeDAO recipeDAO = new RecipeDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIngredientName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colIngredientQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIngredientUnit.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        cmbRecipeCategory.getItems().addAll("Carnes", "Pescados", "Verduras", "Legumbres", "Postres", "Otros");

        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Eliminar");

            {
                btnDelete.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    System.out.println("Producto seleccionado desde botón: " + product.getNombreProducto());

                    // Lógica de eliminación
                    deleteProductFromTable(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });

    }

    public void setRecipeViewController(RecipeViewController recipeViewController) {
        this.recipeViewController = recipeViewController;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        loadRecipeDetails();
    }

    private void loadRecipeDetails() {
        if (recipe != null) {
            System.out.println("Cargando detalles para la receta: " + recipe.getNombre());
            fieldRecipeName.setText(recipe.getNombre());
            cmbRecipeCategory.setValue(recipe.getTipo());
            txtAreaInstructions.setText(recipe.getInstrucciones());

            // Cargar ingredientes desde el DAO
            List<Product> ingredientes = recipeDAO.getProductsByRecipeId(recipe.getId());
            if (ingredientes == null || ingredientes.isEmpty()) {
                System.out.println("No hay ingredientes disponibles para esta receta.");
                tableIngredients.getItems().clear();
            } else {
                tableIngredients.getItems().clear();
                tableIngredients.getItems().addAll(ingredientes);
                System.out.println("Ingredientes cargados: " + ingredientes);
            }

            // Cargar imagen si existe
            if (recipe.getFoto() != null) {
                try {
                    Image image = new Image(new ByteArrayInputStream(recipe.getFoto()));
                    imgRecipe.setImage(image);
                    ImageUtils.setClipToCircle(imgRecipe); // Clip opcional
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    @FXML
    private void addIngredient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/AddIngredientView.fxml"));
            Parent root = loader.load();

            AddIngredientViewController addIngredientController = loader.getController();
            addIngredientController.setModifyRecipeController(this);

            Stage stage = new Stage();
            stage.setTitle("Añadir Ingrediente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAddIngredient.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al cargar la vista de añadir ingrediente: " + e.getMessage());
        }
    }

    public void addIngredientToTable(Product ingredient) {
        if (ingredient != null) {
            tableIngredients.getItems().add(ingredient);
            System.out.println("Ingrediente añadido: " + ingredient.getNombreProducto());
        }
    }

    @FXML
    private void loadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));

        selectedImageFile = fileChooser.showOpenDialog(btnLoadImage.getScene().getWindow());
        if (selectedImageFile != null) {
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                imgRecipe.setImage(image);
                ImageUtils.setClipToCircle(imgRecipe);
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void modifyRecipe(ActionEvent event) {
        try {
            // Validar los campos principales
            String recipeName = fieldRecipeName.getText().trim();
            String recipeCategory = cmbRecipeCategory.getValue();
            String recipeInstructions = txtAreaInstructions.getText().trim();

            if (recipeName.isEmpty() || recipeCategory == null || recipeInstructions.isEmpty()) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
                return;
            }

            // Verificar que haya al menos un ingrediente
            if (tableIngredients.getItems().isEmpty()) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Sin Ingredientes", "Por favor, añade al menos un ingrediente.");
                return;
            }

            // Actualizar el objeto `Recipe` con los datos modificados
            recipe.setNombre(recipeName);
            recipe.setTipo(recipeCategory);
            recipe.setInstrucciones(recipeInstructions);
            recipe.setIngredientes(new ArrayList<>(tableIngredients.getItems())); // Ingredientes desde la tabla

            // Manejar la imagen (convertir `Image` a `byte[]`)
            if (imgRecipe.getImage() != null) {
                try {
                    recipe.setFoto(ImageUtils.convertImageToBlob(imgRecipe.getImage()).getBytes(1, (int) ImageUtils.convertImageToBlob(imgRecipe.getImage()).length()));
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error con la Imagen", "No se pudo procesar la imagen seleccionada.");
                    return;
                }
            }

            // Llamar al DAO para guardar la receta modificada y sus ingredientes
            RecipeDAO recipeDAO = new RecipeDAO();
            boolean success = recipeDAO.updateRecipe(recipe);

            if (success) {
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Receta Modificada", "La receta se modificó exitosamente.");
                ((Stage) btnModifyRecipe.getScene().getWindow()).close(); // Cierra la ventana actual
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Modificar", "No se pudo modificar la receta. Por favor, inténtalo de nuevo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado.");
        }
    }

//    private void deleteProductFromTable(Product product) {
//        if (product == null) {
//            System.out.println("No se seleccionó ningún producto para eliminar.");
//            return;
//        }
//
//        System.out.println("Intentando eliminar el producto: " + product.getNombreProducto() + " | Receta ID: " + recipe.getId());
//
//        String queryDeleteProduct = "DELETE FROM Receta_producto WHERE id_receta = ? AND nombre_producto = ?";
//        Connection conn = null;
//        PreparedStatement stmtDelete = null;
//
//        try {
//            conn = MySQLConnection.getConnection();
//
//            stmtDelete = conn.prepareStatement(queryDeleteProduct);
//            stmtDelete.setInt(1, recipe.getId());
//            stmtDelete.setString(2, product.getNombreProducto());
//            int rowsAffected = stmtDelete.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("Producto eliminado de la base de datos: " + product.getNombreProducto());
//            } else {
//                System.out.println("El producto no estaba en la base de datos: " + product.getNombreProducto());
//            }
//
//            // Reconstruir la lista de productos desde la tabla actual
//            ObservableList<Product> updatedItems = FXCollections.observableArrayList(tableIngredients.getItems());
//            updatedItems.remove(product); // Remover el producto eliminado
//            tableIngredients.setItems(updatedItems); // Reasignar los datos actualizados
//            tableIngredients.refresh(); // Refrescar manualmente la tabla
//            System.out.println("Tabla visual reconstruida y refrescada.");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (stmtDelete != null) {
//                    stmtDelete.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException closeEx) {
//                closeEx.printStackTrace();
//            }
//        }
//    }
    private void deleteProductFromTable(Product product) {
        if (product == null) {
            System.out.println("No se seleccionó ningún producto para eliminar.");
            return;
        }

        System.out.println("Intentando eliminar el producto: " + product.getNombreProducto() + " | Receta ID: " + recipe.getId());

        // Consulta para eliminar el producto sólo de la receta actual
        String queryDeleteProduct = "DELETE FROM Receta_producto WHERE id_receta = ? AND nombre_producto = ?";

        Connection conn = null;
        PreparedStatement stmtDelete = null;

        try {
            conn = MySQLConnection.getConnection();

            // Eliminar el producto sólo de esta receta
            stmtDelete = conn.prepareStatement(queryDeleteProduct);
            stmtDelete.setInt(1, recipe.getId()); // Eliminar usando la receta actual
            stmtDelete.setString(2, product.getNombreProducto()); // Producto específico
            int rowsAffected = stmtDelete.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Producto eliminado de la receta actual: " + product.getNombreProducto());
            } else {
                System.out.println("El producto no estaba asociado con esta receta: " + product.getNombreProducto());
            }

            // Eliminar el producto de la tabla visual
            tableIngredients.getItems().remove(product);
            System.out.println("Producto eliminado de la tabla visual: " + product.getNombreProducto());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmtDelete != null) {
                    stmtDelete.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private void refreshTableView() {
        ObservableList<Product> currentItems = tableIngredients.getItems();
        tableIngredients.setItems(null); // Rompe el enlace con la tabla
        tableIngredients.setItems(currentItems); // Vuelve a enlazar los datos con la tabla
        System.out.println("Tabla visual refrescada manualmente.");
    }

}
