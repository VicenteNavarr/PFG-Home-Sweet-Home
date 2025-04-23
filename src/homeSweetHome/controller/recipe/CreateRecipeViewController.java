package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Product;
import homeSweetHome.model.Recipe;
import homeSweetHome.utils.AlertUtils;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador para la vista de creación de recetas.
 */
public class CreateRecipeViewController implements Initializable {

    @FXML
    private TextField fieldRecipeName;
    @FXML
    private ComboBox<String> cmbRecipeCategory;
    @FXML
    private Button btnAddIngredient;
    @FXML
    private TextArea txtAreaInstructions;
    @FXML
    private Button btnLoadImage;
    @FXML
    private Button btnCreateNewRecipe;
    @FXML
    private Button btnCancel;
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

    private byte[] recipeImage; // Para almacenar la imagen de la receta
    @FXML
    private ImageView imgRecipe;

    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar las columnas de la tabla
        colIngredientName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colIngredientQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIngredientUnit.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Configurar opciones para la categoría de recetas
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

    /**
     * Establece la referencia al controlador principal de la vista de recetas.
     *
     * @param recipeViewController El controlador principal.
     */
    public void setRecipeViewController(RecipeViewController recipeViewController) {
        this.recipeViewController = recipeViewController;
    }

    /**
     * Abre la ventana para añadir un nuevo ingrediente y lo añade a la tabla.
     *
     * @param event Evento del botón.
     */
    @FXML
    private void addIngredient(ActionEvent event) {
        try {
            // Cargar la vista para añadir ingrediente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/AddIngredientView.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista de añadir ingrediente
            AddIngredientViewController addIngredientController = loader.getController();
            addIngredientController.setCreateRecipeController(this); // Pasar referencia al controlador principal

            // Configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Añadir Ingrediente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAddIngredient.getScene().getWindow());
            stage.showAndWait(); // Esperar hasta que se cierre la ventana
        } catch (Exception e) {
            System.err.println("Error al cargar la vista de añadir ingrediente: " + e.getMessage());
        }
    }

    /**
     * Añade un ingrediente a la tabla de ingredientes.
     *
     * @param ingredient El objeto Product que representa el ingrediente.
     */
    public void addIngredientToTable(Product ingredient) {
        tableIngredients.getItems().add(ingredient); // Añadir el ingrediente a la tabla
    }

    /**
     * Carga una imagen desde el sistema de archivos y la muestra en el
     * ImageView de la receta. También almacena la imagen como un byte array
     * para su guardado en la base de datos.
     *
     * @param event Evento del botón "Cargar Imagen".
     */
    @FXML
    private void loadImage(ActionEvent event) {
        // Crear un selector de archivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));

        // Abrir el cuadro de diálogo para seleccionar un archivo
        File file = fileChooser.showOpenDialog(btnLoadImage.getScene().getWindow());

        if (file != null) {
            try {
                // Cargar la imagen seleccionada y mostrarla en el ImageView
                Image image = new Image(file.toURI().toString());
                imgRecipe.setImage(image); // Asigna la imagen al ImageView

                // Convertir la imagen a un byte array para almacenarla
                recipeImage = java.nio.file.Files.readAllBytes(file.toPath()); // Almacena los bytes de la imagen
                System.out.println("Imagen cargada correctamente: " + file.getName());
            } catch (Exception e) {
                // Manejar errores al cargar el archivo
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Cargar Imagen", "No se pudo cargar la imagen. Inténtalo de nuevo.");
                System.err.println("Error al cargar la imagen: " + e.getMessage());
            }
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }
    }

    /**
     * Guarda la receta en la base de datos junto con los ingredientes.
     *
     * @param event Evento del botón.
     */
//    @FXML
//    private void createNewRecipe(ActionEvent event) {
//        // Validar los campos de la receta
//        String recipeName = fieldRecipeName.getText().trim();
//        String recipeCategory = cmbRecipeCategory.getValue();
//        String recipeInstructions = txtAreaInstructions.getText().trim();
//
//        if (recipeName.isEmpty() || recipeCategory == null || recipeInstructions.isEmpty()) {
//            AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
//            return;
//        }
//
//        // Verificar que haya al menos un ingrediente
//        if (tableIngredients.getItems().isEmpty()) {
//            AlertUtils.showAlert(Alert.AlertType.WARNING, "Sin Ingredientes", "Por favor, añade al menos un ingrediente.");
//            return;
//        }
//
//        // Crear objeto Recipe y asignar los datos
//        Recipe recipe = new Recipe();
//        recipe.setNombre(recipeName);
//        recipe.setTipo(recipeCategory);
//        recipe.setInstrucciones(recipeInstructions);
//        recipe.setFoto(recipeImage); // Foto cargada previamente
//        recipe.setIdGrupo(CurrentSession.getInstance().getUserGroupId()); // Obtener el ID del grupo actual
//        recipe.setIngredientes(new ArrayList<>(tableIngredients.getItems())); // Ingredientes desde la tabla
//
//        // Llamar al DAO para guardar la receta
//        RecipeDAO recipeDAO = new RecipeDAO();
//        boolean success = recipeDAO.addRecipe(recipe);
//
//        if (success) {
//            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Receta Creada", "La receta se creó exitosamente.");
//            ((Stage) btnCreateNewRecipe.getScene().getWindow()).close(); // Cierra la ventana actual
//        } else {
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Guardar", "No se pudo guardar la receta. Por favor, inténtalo de nuevo.");
//        }
//    }
    @FXML
    private void createNewRecipe(ActionEvent event) {
        // Validar los campos de la receta
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

        // Crear objeto Recipe y asignar los datos
        Recipe recipe = new Recipe();
        recipe.setNombre(recipeName);
        recipe.setTipo(recipeCategory);
        recipe.setInstrucciones(recipeInstructions);
        recipe.setFoto(recipeImage); // Foto cargada previamente
        recipe.setIdGrupo(CurrentSession.getInstance().getUserGroupId()); // Obtener el ID del grupo actual
        recipe.setIngredientes(new ArrayList<>(tableIngredients.getItems())); // Ingredientes desde la tabla

        // Llamar al DAO para guardar la receta y los productos en la tabla temporal
        RecipeDAO recipeDAO = new RecipeDAO();
        boolean success = recipeDAO.addRecipeAndProducts(recipe);

        if (success) {
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Receta Creada", "La receta y sus productos se crearon exitosamente.");
            ((Stage) btnCreateNewRecipe.getScene().getWindow()).close(); // Cierra la ventana actual
        } else {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Guardar", "No se pudo guardar la receta. Por favor, inténtalo de nuevo.");
        }
    }

    private void deleteProductFromTable(Product product) {
        if (product == null) {
            System.out.println("No se seleccionó ningún producto para eliminar.");
            return;
        }

        System.out.println("Eliminando producto de la tabla visual: " + product.getNombreProducto());

        // Eliminar el producto de la tabla visual
        tableIngredients.getItems().remove(product);
        tableIngredients.refresh(); // Refrescar la tabla para sincronizar los datos
        System.out.println("Producto eliminado de la tabla visual.");
    }

    private void refreshTableView() {
        ObservableList<Product> currentItems = tableIngredients.getItems();
        tableIngredients.setItems(null); // Rompe el enlace con la tabla
        tableIngredients.setItems(currentItems); // Vuelve a enlazar los datos con la tabla
        System.out.println("Tabla visual refrescada manualmente.");
    }

    /**
     * Cancela la creación de la receta y cierra la ventana.
     *
     * @param event Evento del botón.
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) btnCancel.getScene().getWindow()).close(); // Cerrar la ventana actual
    }
}
