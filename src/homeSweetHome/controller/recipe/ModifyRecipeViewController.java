package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.model.Recipe;
import homeSweetHome.model.Product;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.utils.LanguageManager;
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
import javafx.application.Platform;
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
    @FXML
    private Label modifyRecipeTitle;

    private RecipeViewController recipeViewController;

    private Recipe recipe;

    private File selectedImageFile;

    private Image recipeImage;

    private final RecipeDAO recipeDAO = new RecipeDAO();

    /**
     * Inicializa el controlador
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  

        colIngredientName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colIngredientQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIngredientUnit.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        //cmbRecipeCategory.getItems().addAll("Carnes", "Pescados", "Verduras", "Legumbres", "Postres", "Otros");
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

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    private void updateTexts() {
        
        // Accede al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        System.out.println("Actualizando textos de la interfaz...");

        // Traducción de los textos de los botones
        btnAddIngredient.setText(languageManager.getTranslation("addIngredient"));
        btnLoadImage.setText(languageManager.getTranslation("loadImage"));
        btnModifyRecipe.setText(languageManager.getTranslation("modifyRecipe"));
        btnCancel.setText(languageManager.getTranslation("cancel"));

        modifyRecipeTitle.setText(languageManager.getTranslation("modifyRecipeTitle"));

        // Traducción de los campos de texto
        fieldRecipeName.setPromptText(languageManager.getTranslation("recipeNamePrompt"));
        txtAreaInstructions.setPromptText(languageManager.getTranslation("instructionsPrompt"));

        // Traducción de encabezados de las columnas en la tabla de ingredientes
        colIngredientName.setText(languageManager.getTranslation("ingredientName"));
        colIngredientQuantity.setText(languageManager.getTranslation("ingredientQuantity"));
        colIngredientUnit.setText(languageManager.getTranslation("ingredientUnit"));
        deleteColumn.setText(languageManager.getTranslation("delete"));

        // Traducción del ComboBox de categorías de recetas
        cmbRecipeCategory.setPromptText(languageManager.getTranslation("recipeCategoryPrompt"));
        cmbRecipeCategory.getItems().clear();
        cmbRecipeCategory.getItems().addAll(
                languageManager.getTranslation("recipeCategoryMeat"),
                languageManager.getTranslation("recipeCategoryFish"),
                languageManager.getTranslation("recipeCategoryVegetables"),
                languageManager.getTranslation("recipeCategoryLegumes"),
                languageManager.getTranslation("recipeCategoryDesserts"),
                languageManager.getTranslation("recipeCategoryOthers")
        );

        System.out.println("Traducciones aplicadas correctamente en idioma: " + languageManager.getTranslation("currentLanguage"));
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    
    /**
     * Asigna el controlador de la vista de recetas.
     *
     * @param recipeViewController controlador de la vista de recetas
     */
    public void setRecipeViewController(RecipeViewController recipeViewController) {

        this.recipeViewController = recipeViewController;
    }

    /**
     * Asigna una receta y carga sus detalles.
     *
     * @param recipe la receta a establecer
     */
    public void setRecipe(Recipe recipe) {

        this.recipe = recipe;
        loadRecipeDetails();
    }

    

    /**
     * Carga y muestra los detalles de la receta actual.
     *
     * Este método configura los campos de texto, la categoría, los ingredientes
     * y la imagen de la receta en la interfaz, utilizando los datos asociados.
     */
    private void loadRecipeDetails() {
        
        if (recipe != null) {
            
            System.out.println("Cargando detalles para la receta: " + recipe.getNombre());
            fieldRecipeName.setText(recipe.getNombre());
            cmbRecipeCategory.setValue(recipe.getTipo());
            txtAreaInstructions.setText(recipe.getInstrucciones());

            // Carga ingredientes desde el DAO
            List<Product> ingredientes = recipeDAO.getProductsByRecipeId(recipe.getId());

            if (ingredientes == null || ingredientes.isEmpty()) {
                
                System.out.println("No hay ingredientes disponibles para esta receta.");
                tableIngredients.getItems().clear();
                
            } else {
                
                tableIngredients.getItems().clear();

                //  Depuración  para verificar los datos antes de insertarlos en la tabla
                for (Product ingrediente : ingredientes) {
                    
                    if (ingrediente.getTipo() == null || ingrediente.getTipo().isEmpty()) {
                        
                        ingrediente.setTipo("Cantidad"); // Asigna un valor por defecto si está vacío
                    }
                    
                    System.out.println("Ingrediente cargado: " + ingrediente.getNombreProducto()
                            + " - Cantidad: " + ingrediente.getCantidad()
                            + " - Unidad (Tipo): " + ingrediente.getTipo());
                }

                // Agrega ingredientes a la tabla
                tableIngredients.getItems().addAll(ingredientes);
                System.out.println("Total ingredientes cargados en la tabla: " + tableIngredients.getItems().size());

                // actualización visual de la tabla
                tableIngredients.refresh();
            }

            // Carga imagen si existe
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

    /**
     * Cierra la ventana actual al hacer clic en el botón cancelar.
     *
     * @param event el evento asociado al clic del botón
     */
    @FXML
    private void cancel(ActionEvent event) {

        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    /**
     * Abre una ventana para añadir un nuevo ingrediente a la receta.
     *
     * @param event el evento asociado al clic del botón añadir ingrediente
     */
    @FXML
    private void addIngredient(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/AddIngredientView.fxml"));
            Parent root = loader.load();

            AddIngredientViewController addIngredientController = loader.getController();
            addIngredientController.setModifyRecipeController(this);

            Stage stage = new Stage();
            stage.setTitle("Añadir Ingrediente");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/recipeIconBlue.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAddIngredient.getScene().getWindow());
            stage.showAndWait();

        } catch (Exception e) {

            System.err.println("Error al cargar la vista de añadir ingrediente: " + e.getMessage());
        }
    }

    /**
     * Añade un ingrediente a la tabla de ingredientes.
     *
     * @param ingredient el ingrediente a añadir
     */
    public void addIngredientToTable(Product ingredient) {

        if (ingredient != null) {

            tableIngredients.getItems().add(ingredient);
            System.out.println("Ingrediente añadido: " + ingredient.getNombreProducto());
        }
    }

    /**
     * Abre un selector de archivos para cargar una imagen y mostrarla en la
     * interfaz.
     *
     * @param event el evento asociado al clic del botón cargar imagen
     */
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

    /**
     * Modifica una receta con los datos introducidos en la interfaz.
     *
     * Valida los campos principales, actualiza los ingredientes, procesa la
     * imagen y guarda los cambios a través del DAO. Muestra alertas en caso de
     * errores o éxito.
     *
     * @param event el evento asociado al clic del botón modificar receta
     */
    @FXML
    private void modifyRecipe(ActionEvent event) {

        try {

            // Valida los campos principales
            String recipeName = fieldRecipeName.getText().trim();
            String recipeCategory = cmbRecipeCategory.getValue();
            String recipeInstructions = txtAreaInstructions.getText().trim();

            if (recipeName.isEmpty() || recipeCategory == null || recipeInstructions.isEmpty()) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
                return;
            }

            // Verifica que haya al menos un ingrediente
            if (tableIngredients.getItems().isEmpty()) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Sin Ingredientes", "Por favor, añade al menos un ingrediente.");
                return;
            }

            // Actualiza el objeto `Recipe` con los datos modificados
            recipe.setNombre(recipeName);
            recipe.setTipo(recipeCategory);
            recipe.setInstrucciones(recipeInstructions);
            recipe.setIngredientes(new ArrayList<>(tableIngredients.getItems())); // Ingredientes desde la tabla

            // Maneja la imagen (convertir `Image` a `byte[]`)
            if (imgRecipe.getImage() != null) {

                try {

                    recipe.setFoto(ImageUtils.convertImageToBlob(imgRecipe.getImage()).getBytes(1, (int) ImageUtils.convertImageToBlob(imgRecipe.getImage()).length()));
                } catch (Exception e) {

                    e.printStackTrace();

                    if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                        AlertUtils.showAlert(Alert.AlertType.ERROR, "Error con la Imagen", "No se pudo procesar la imagen seleccionada.");

                    } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                        AlertUtils.showAlert(Alert.AlertType.ERROR, "Image Error", "The selected image could not be processed.");
                    }

                    return;
                }
            }

            // Llama al DAO para guardar la receta modificada y sus ingredientes
            RecipeDAO recipeDAO = new RecipeDAO();
            boolean success = recipeDAO.updateRecipe(recipe);

            if (success) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Receta Modificada", "La receta se modificó exitosamente.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Recipe Modified", "The recipe was successfully modified.");
                }

                ((Stage) btnModifyRecipe.getScene().getWindow()).close(); // Cierra la ventana actual

            } else {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Modificar", "No se pudo modificar la receta. Por favor, inténtalo de nuevo.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Modification Error", "The recipe could not be modified. Please try again.");
                }

            }

        } catch (Exception e) {

            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado.");
        }
    }

    /**
     * Elimina un producto de la receta actual y de la tabla visual.
     *
     * Realiza la eliminación del producto en la base de datos y lo retira de la
     * tabla de ingredientes en la interfaz. Muestra mensajes según el
     * resultado.
     *
     * @param product el producto a eliminar
     */
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

            // Elimina el producto sólo de esta receta
            stmtDelete = conn.prepareStatement(queryDeleteProduct);
            stmtDelete.setInt(1, recipe.getId()); // Elimina usando la receta actual
            stmtDelete.setString(2, product.getNombreProducto()); // Producto específico
            int rowsAffected = stmtDelete.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println("Producto eliminado de la receta actual: " + product.getNombreProducto());

            } else {

                System.out.println("El producto no estaba asociado con esta receta: " + product.getNombreProducto());
            }

            // Elimina el producto de la tabla visual
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

    /**
     * Refresca manualmente la tabla de ingredientes.
     *
     * Desvincula temporalmente los datos de la tabla y los vuelve a enlazar
     * para actualizar su visualización.
     */
    private void refreshTableView() {

        ObservableList<Product> currentItems = tableIngredients.getItems();
        tableIngredients.setItems(null); // Rompe el enlace con la tabla
        tableIngredients.setItems(currentItems); // Vuelve a enlazar los datos con la tabla
        System.out.println("Tabla visual refrescada manualmente.");
    }

}
